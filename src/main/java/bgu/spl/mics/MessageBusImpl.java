package bgu.spl.mics;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This class is responsible of getting and delivering messages (Events and Broadcasts)
 * between MicroServices.
 * Note that this class is implemented as a singleton.
 */
public class MessageBusImpl implements MessageBus {
	private static class single_instance{
		private static final MessageBusImpl single_instance = new MessageBusImpl();
	}
	//Stores a queue for each MicroService, to organize its missions.
	//Also responsible of the Round Robbin separation of the events.
	private final ConcurrentHashMap<MicroService, ConcurrentLinkedQueue<Message>> queueMap;

	//Stores all the MicroServices that subscribed to each Event
	private final ConcurrentHashMap<Class<? extends Event>, Queue<MicroService>> eventMap;

	//Stores all the MicroServices that subscribed to each Broadcast
	private final ConcurrentHashMap<Class<? extends Broadcast>,List<MicroService>> broadcastMap;

	//Stores the Future Objects for each Event that has been sent.
	private final HashMap<Event,Future> futureMap;

	//Stores the Messages which each MicroService is subscribed to.
	private final HashMap<MicroService,List<Class<? extends Message>>> subscribeMap;


	/**
	 * CTR
	 * Initializing all the field with an empty hash map.
	 */
	private MessageBusImpl(){
		queueMap = new ConcurrentHashMap<>();
		eventMap = new ConcurrentHashMap<>();
		broadcastMap = new ConcurrentHashMap<>();
		futureMap = new HashMap<>();
		subscribeMap = new HashMap<>();
	}

	/**
	 * @return the only instance of the messageBus
	 */
	public static MessageBusImpl getInstance() {
		return single_instance.single_instance;
	}

	/**
	 * If {@code m} is already subscribed to {@code type}, do nothing.
	 * @param type The type of Event to subscribe to.
	 * @param m    The subscribing micro-service.
	 * @param <T> The type of the result of the event which extended by the class.
	 */

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		eventMap.putIfAbsent(type, new LinkedList<>());		//put new type of event if there isn't one already
		if (!eventMap.get(type).contains(m)) {		//check if {@code type} contains {@code m}
			synchronized (eventMap.get(type)) {		//lock the queue of {@code type}
				if (!eventMap.get(type).contains(m)) {	//double check if {@code type} contains {@code m} (might change before sync)
					eventMap.get(type).add(m);
					subscribeMap.get(m).add(type);
				}
			}
		}
	}

	/**
	 * If {@code m} is already subscribed to {@code type}, do nothing.
	 * @param type 	The type of Broadcast to subscribe to.
	 * @param m    	The subscribing micro-service.
	 */
	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		broadcastMap.putIfAbsent(type,new LinkedList<>());	//put new type of event if there isn't one already
		if (!broadcastMap.get(type).contains(m)) {		//check if {@code type} contains {@code m}
			synchronized (broadcastMap.get(type)) {		//lock the List of {@code type}
				if (!broadcastMap.get(type).contains(m)) {	//double check if {@code type} contains {@code m} (might change before sync)
					broadcastMap.get(type).add(m);
					subscribeMap.get(m).add(type);
				}
			}
		}
	}

	/**
	 * Completes the received request {@code e} with the result {@code result}.
	 * Removes the Event {@code e} from the Future hash map.
	 * @param e      The Event to complete.
	 * @param result The resolved result of the completed event (our case->tru/false).
	 * @param <T>    The type of the expected result of the processed event.
	 */
	@Override @SuppressWarnings({"unchecked"})
	public <T> void complete(Event<T> e, T result) {
		futureMap.get(e).resolve(result);
		futureMap.remove(e);
	}

	/**
	 * If there are none MicroServices subscribed to {@code b} , return.
	 * Sends Broadcast {@code b} to all of the MicroServices which subscribed to this type of Broadcast
	 * @param b 	The Broadcast (message) to add to the queues of the MicroServices.
	 */
	@Override
	public void sendBroadcast(Broadcast b) {
		if (!broadcastMap.containsKey(b.getClass())){	//check if there is a MicroService that subscribed to this broadcast class
			synchronized (broadcastMap) {				//lock broadcastMap
				if (!broadcastMap.containsKey(b.getClass()))	//double check
					return;
			}
		}
		synchronized (broadcastMap.get(b.getClass())) {			//lock the MicroServices list of b class
			//add the broadcast to all the subscribed MicroServices
			for (MicroService m : broadcastMap.get(b.getClass())) {
				synchronized (queueMap.get(m)) {	//lock the queue of the current MicroService
					queueMap.get(m).offer(b);
					queueMap.get(m).notifyAll();	//notify the Thread that waits in awaitMessage function on this Microservice queue of Messages
				}
			}
		}
	}


	/**
	 * If there are none MicroServices subscribed to {@code e} , null.
	 * @param e     	The event to add to the queue of the next MicroServices which subscribes
	 *                  to {@code e} Event- by Round Robbin manner.
	 * @param <T>       The type of the result of the Future that returned after completing the event.
	 * @return			{@param f} The future Object derived from the Event {@code e}.
	 */
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		if (!eventMap.containsKey(e.getClass())){	//check if there is a MicroService that subscribed to this event class
			synchronized (eventMap) {				//lock eventMap
				if (!eventMap.containsKey(e.getClass()))	//double check
					return null;
			}
		}
		Future<T> f = new Future<>();		//create new future object to return
		futureMap.put(e,f);
		synchronized (eventMap.get(e.getClass())){		//lock the queue of MicroServices that are subscribed to e class
			MicroService m = eventMap.get(e.getClass()).poll();		//get the first MicroService in the queue
			eventMap.get(e.getClass()).offer(m);					//put him back to reserve the RoundRobin Manner
			if (m == null) throw new NullPointerException("there are no MicroServices in the current queue");
			synchronized (queueMap.get(m)) {		//lock the queue of the current MicroService
				queueMap.get(m).offer(e);
				queueMap.get(m).notifyAll();		//notify the Thread that waits in awaitMessage function on this Microservice queue of Messages
			}
		}
		return f;
	}

	/**
	 * If {@code m} already registered, do nothing.
	 * @param m the micro-service to add to the Microservices hash map,
	 *          and to create a queue for.
	 */
	@Override
	public void register(MicroService m) {
		queueMap.putIfAbsent(m, new ConcurrentLinkedQueue<>());
		if (!subscribeMap.containsKey(m))
			subscribeMap.put(m,new LinkedList<>());
	}

	/**
	 * If {@code m} is not register, do nothing.
	 * Remove {@code m} from all the data structures it has been inserted in.
	 * @param m the micro-service to unregister.
	 */
	@Override
	public void unregister(MicroService m) {
		if (queueMap.containsKey(m)) {
			synchronized (subscribeMap.get(m)) {	//lock the list of Messages that {@code m} is subscribed to
				//unsubscribe from all the Events and Broadcast {@code m} is subscribed to
				for (Class<? extends Message> c : subscribeMap.get(m)) {
					unsubscribeEventOrBroadcast(c, m);
				}
			}
			subscribeMap.remove(m);
			queueMap.remove(m);
		}
	}

	/**
	 * If MicroService {@code m} is not subscribe to Message {@code c} (Event/Broadcast), do nothing.
	 * Removes MicroService {@code m} from eventMap or BroadcastMap.
	 * If there are noe more MicroServices which subscribed to {@code c}, remove {@code c}.
	 * @param c The Message to unsubscribe {@code m} from.
	 * @param m The MicroService to unsubscribe from {@code c}.
	 */
	private void unsubscribeEventOrBroadcast(Class<? extends Message> c, MicroService m){
		if (eventMap.containsKey(c)) {
			removeEvent(c,m);
		}
		if (broadcastMap.containsKey(c)) {
			removeBroadcast(c,m);
		}
	}

	/**
	 * unsubscribe {@code m} from {@code c}. if after the removal {@code c} is empty (no other MicroServices are subscribed)
	 * delete {@code c} from {eventMap}
	 * @param c The event to unsubscribe {@code m} from
	 * @param m The MicroService to unsubscribe from {@code c}
	 */
	private void removeEvent (Class<? extends Message> c, MicroService m){
		synchronized (eventMap.get(c)) {
			eventMap.get(c).remove(m);
		}
		synchronized (eventMap) {
			//checks if {@code c} is still in the hash map, or some other Thread already removed it to avoid NullPtrException
			if (eventMap.containsKey(c) && eventMap.get(c).isEmpty()) {
				eventMap.remove(c);
			}
		}
	}

	/**
	 * unsubscribe {@code m} from {@code c}. if after the removal {@code c} is empty (no other MicroServices are subscribed)
	 * delete {@code c} from {braodcastMap}
	 * @param c The broadcast to unsubscribe {@code m} from
	 * @param m The MicroService to unsubscribe from {@code c}
	 */
	public void removeBroadcast (Class<? extends Message> c, MicroService m){
		synchronized (broadcastMap.get(c)) {
			broadcastMap.get(c).remove(m);
		}
		synchronized (broadcastMap) {
			//checks if {@code c} is still in the hash map, or some other Thread already removed it to avoid NullPtrException
			if (broadcastMap.containsKey(c) && broadcastMap.get(c).isEmpty()) {
				broadcastMap.remove(c);
			}
		}
	}

	/**
	 * If MicroService {@code m} is not registered to MessageBus- raise Exception.
	 * If there are no Messages in {@code m} queue- wait.
	 * @param m The microService requesting to take a message from its message
	 *          queue.
	 * @return the next Message to handle by {@code m}.
	 */
	@Override
	public Message awaitMessage(MicroService m){
		if (!queueMap.containsKey(m))
			throw new RuntimeException(m.getName()+" hasn't been registered to the message bus");
		if (queueMap.get(m).isEmpty()) {	//check if its empty, to avoid unnecessary synchronize
			synchronized (queueMap.get(m)) {
				while (queueMap.get(m).isEmpty()) {
					try {
						queueMap.get(m).wait();		//being notified from sendEvent and sendBroadcast
					} catch (InterruptedException ignored) {}
				}
			}
		}
		return queueMap.get(m).poll();
	}
}