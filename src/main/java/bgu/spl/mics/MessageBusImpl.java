package bgu.spl.mics;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	//fields
	private ConcurrentHashMap<Class, Queue<Message>> queueMap;
	private ConcurrentHashMap<Class, Queue<MicroService>> eventMap;
	private ConcurrentHashMap<Class,List<MicroService>> broadcastMap;
	private ConcurrentHashMap<Event,Future> futureMap;
	private ConcurrentHashMap<Class,List<Class>> subscribeMap;
	private static MessageBusImpl single_instance = null;

	//CTR
	private MessageBusImpl(){
		queueMap = new ConcurrentHashMap<>();
		eventMap = new ConcurrentHashMap<>();
		broadcastMap = new ConcurrentHashMap<>();
		futureMap = new ConcurrentHashMap<>();
		subscribeMap = new ConcurrentHashMap<>();
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
		if (!eventMap.containsKey(type)) {
			eventMap.put(type, new LinkedList<>());
			subscribeMap.put(m.getClass(),new LinkedList<>());
		}
		if (!eventMap.get(type).contains(m)) {
			eventMap.get(type).add(m);
			subscribeMap.get(m.getClass()).add(type);
		}
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
		futureMap.get(e).resolve(result);
		notifyAll();
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		for(MicroService m : broadcastMap.get(b.getClass())){
			queueMap.get(m.getClass()).offer(b);
		}
		notifyAll();
	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		MicroService m = eventMap.get(e.getClass()).poll();
		queueMap.get(m.getClass()).offer(e);
		eventMap.get(e.getClass()).offer(m);
		Future<T> f = new Future<>();
		futureMap.put(e,f);
		notifyAll();
		return f;
	}

	@Override
	public void register(MicroService m) {
		if (!isRegistered(m))
			queueMap.put(m.getClass(), new LinkedList<>());
	}

	@Override
	public void unregister(MicroService m) {
		if (isRegistered(m)){
			for (Class c:subscribeMap.get(m.getClass())){
				unsubscribeEventOrBroadcast(c,m);
			}
		}
		queueMap.remove(m.getClass());
	}

	private boolean isRegistered(MicroService m){
		return queueMap.contains(m.getClass());
	}


	private void unsubscribeEventOrBroadcast(Class c, MicroService m){
		if (eventMap.containsKey(c)){
			eventMap.get(c).remove(m);
		}
		else if (broadcastMap.containsKey(c)){
			broadcastMap.get(c).remove(m);
		}
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		if (!isRegistered(m))
			throw new InterruptedException(m.getName()+" hasn't been registered to the message bus");
		while (queueMap.get(m.getClass()).isEmpty()) {
			try {
				wait();
			}
			catch (InterruptedException e){}
		}
		return queueMap.get(m.getClass()).poll();
	}
}