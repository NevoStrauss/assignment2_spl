package bgu.spl.mics;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */

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
	private final ConcurrentHashMap<MicroService, Queue<Message>> queueMap;

	//Stores all the MicroServices that subscribed to each Event
	private final ConcurrentHashMap<Class<? extends Event>, Queue<MicroService>> eventMap;

	//Stores all the MicroServices that subscribed to each Broadcast
	private final ConcurrentHashMap<Class<? extends Broadcast>,List<MicroService>> broadcastMap;

	//Stores the Future Objects for each Event that have been sent.
	private final ConcurrentHashMap<Event,Future> futureMap;

	//Stores the Messages which each MicroServices is subscribed to.
	private final ConcurrentHashMap<MicroService,List<Class<? extends Message>>> subscribeMap;


	/**
	 * CTR
	 * Initializing all the field with a concurrentHashMap.
	 */
	private MessageBusImpl(){
		queueMap = new ConcurrentHashMap<>();
		eventMap = new ConcurrentHashMap<>();
		broadcastMap = new ConcurrentHashMap<>();
		futureMap = new ConcurrentHashMap<>();
		subscribeMap = new ConcurrentHashMap<>();
	}

	/**
	 *
	 * @return the only instance of the messageBus
	 */
	public static MessageBusImpl getInstance() {
		return single_instance.single_instance;
	}

	/**
	 * If {@code m} is already subscribed to {@code type}, do nothing.
	 * @param type The type of Event to subscribe to,
	 * @param m    The subscribing micro-service.
	 * @param <T> The type of the result of the event which extended by the class.
	 */

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		if (!eventMap.containsKey(type)) {
			synchronized (eventMap) {
				if (!eventMap.containsKey(type))
					eventMap.put(type, new LinkedList<>());
				eventMap.notifyAll();
			}
		}
		if (!eventMap.get(type).contains(m)) {
			synchronized (eventMap.get(type)) {
				if (!eventMap.get(type).contains(m)) {
					eventMap.get(type).add(m);
					subscribeMap.get(m).add(type);
				}
				eventMap.get(type).notifyAll();
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
		if (!broadcastMap.containsKey(type)) {
			synchronized (broadcastMap) {
				if (!broadcastMap.containsKey(type)) {
					broadcastMap.put(type, new LinkedList<>());
					broadcastMap.notifyAll();
				}
			}
		}
		if (!broadcastMap.get(type).contains(m)) {
			synchronized (broadcastMap.get(type)) {
				if (!broadcastMap.get(type).contains(m)) {
					broadcastMap.get(type).add(m);
					subscribeMap.get(m).add(type);
					broadcastMap.get(type).notifyAll();
				}
			}
		}
	}

	/**
	 * Completes the received request {@code e} with the result {@code result}.
	 * Removes the Event {@code e} from the Future data structure.
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
	 * If there are none MicroServices subscribed to {@code b} , wait.
	 * Sends Broadcast {@code b} to all of the MicroServices which subscribed to this type of Broadcast
	 * @param b 	The Broadcast (message) to add to the queues of the MicroServices.
	 */
	@Override
	public void sendBroadcast(Broadcast b) {
		if (!broadcastMap.containsKey(b.getClass())){
			synchronized (broadcastMap) {
				if (!broadcastMap.containsKey(b.getClass()))
					return;
			}
		}
		synchronized (broadcastMap.get(b.getClass())) {
			for (MicroService m : broadcastMap.get(b.getClass())) {
				synchronized (queueMap.get(m)) {
					queueMap.get(m).offer(b);
					queueMap.get(m).notifyAll();
				}
			}
		}
	}


	/**
	 * If there are none MicroServices subscribed to {@code e} , wait.
	 * @param e     	The event to add to the queue of the next MicroServices which subscribes
	 *                  to {@code e} Event- by Round Robbin manner.
	 * @param <T>       The type of the result of the Future that returned after completing the event.
	 * @return			{@param f} The future Object derived from the Event {@code e}.
	 */
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		if (!eventMap.containsKey(e.getClass())){
			synchronized (eventMap) {
				if (!eventMap.containsKey(e.getClass()))
					return null;
			}
		}
		Future<T> f = new Future<>();
		synchronized (eventMap.get(e.getClass())){
			MicroService m = eventMap.get(e.getClass()).poll();
			assert m != null;
			synchronized (queueMap.get(m)) {
				queueMap.get(m).offer(e);
				futureMap.put(e,f);
				queueMap.get(m).notifyAll();
			}
			eventMap.get(e.getClass()).offer(m);
		}
		return f;
	}

	/**
	 * If {@code m} already registered, do nothing.
	 * @param m the micro-service to add to the Microservices data structure,
	 *          and to create a queue for.
	 */
	@Override
	public void register(MicroService m) {
		if (!isRegistered(m)) {
			queueMap.put(m, new LinkedList<>());
			subscribeMap.put(m,new LinkedList<>());
		}
	}

	/**
	 * If {@code m} is not register, do nothing.
	 * Remove {@code m} from all the data structures it has been inserted in.
	 * @param m the micro-service to unregister.
	 */
	@Override
	public void unregister(MicroService m) {
		if (isRegistered(m)){
			synchronized (subscribeMap.get(m)) {
				for (Class<? extends Message> c : subscribeMap.get(m)) {
					unsubscribeEventOrBroadcast(c, m);
				}
			}
			subscribeMap.remove(m);
			queueMap.remove(m);
		}
	}

	/**
	 *
	 * @param m The MicroServices to check if registered.
	 * @return if {@code m} is registered.
	 */
	private boolean isRegistered(MicroService m){
		return queueMap.containsKey(m);
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
			synchronized (eventMap.get(c)) {
				eventMap.get(c).remove(m);
			}
			synchronized (eventMap) {
				if (eventMap.get(c).isEmpty()) {
					eventMap.remove(c);
				}
			}
		}
		if (broadcastMap.containsKey(c)) {
			synchronized (broadcastMap.get(c)) {
				broadcastMap.get(c).remove(m);
			}
			synchronized (broadcastMap) {
				if (broadcastMap.get(c).isEmpty()) {
					broadcastMap.remove(c);
				}
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
		if (!isRegistered(m))
			throw new RuntimeException(m.getName()+" hasn't been registered to the message bus");
		if (queueMap.get(m).isEmpty()) {
			synchronized (queueMap.get(m)) {
				while (queueMap.get(m).isEmpty()) {
					try {
						queueMap.get(m).wait();
					} catch (InterruptedException e) {}
				}
			}
		}
		return queueMap.get(m).poll();
	}
}