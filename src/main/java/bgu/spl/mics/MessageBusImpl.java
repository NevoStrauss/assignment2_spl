package bgu.spl.mics;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	//fields
	private HashMap<Class,Queue<Message>> queueMap;
	private HashMap<Class, List<MicroService>> eventMap;
	private HashMap<Class,List<MicroService>> broadcastMap;
	private HashMap<Event,Future> futureMap;
	private static MessageBusImpl single_instance = null;

	//CTR
	private MessageBusImpl(){
		queueMap = new HashMap<>();
		eventMap = new HashMap<>();
		broadcastMap = new HashMap<>();
		futureMap = new HashMap<>();
	}

	//methods
	public static MessageBusImpl getInstance() {
		if (single_instance == null)
			single_instance = new MessageBusImpl();
		return single_instance;
	}

	/**
	 * if {@code m} is already subscribed to {@code type}, do nothing
	 * @param type The type to subscribe to,
	 * @param m    The subscribing micro-service.
	 * @param <T>
	 */

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		if (!eventMap.containsKey(type))
			eventMap.put(type,new LinkedList<>());
		if (!eventMap.get(type).contains(m))
			eventMap.get(type).add(m);
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		if (!broadcastMap.containsKey(type))
			broadcastMap.put(type,new LinkedList<>());
		if (!broadcastMap.get(type).contains(m))
			broadcastMap.get(type).add(m);
    }

	@Override @SuppressWarnings("unchecked")
	public <T> void complete(Event<T> e, T result) {
		
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		
	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		
        return null;
	}

	@Override
	public void register(MicroService m) {
		if (!queueMap.containsKey(m.getClass()))
			queueMap.put(m.getClass(), new LinkedList<>());
	}

	@Override
	public void unregister(MicroService m) {
		
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		if (!queueMap.containsKey(m.getClass()))
			throw new InterruptedException(m.getClass()+" hasn't been registered to the message bus");
		if (queueMap.get(m.getClass()).isEmpty()) {
		//waits until there is a message
		}
		return queueMap.get(m.getClass()).poll();
	}
}