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
	private ConcurrentHashMap<MicroService, Queue<Message>> queueMap;
	private ConcurrentHashMap<Class<? extends Event>, Queue<MicroService>> eventMap;
	private ConcurrentHashMap<Class<? extends Broadcast>,List<MicroService>> broadcastMap;
	private ConcurrentHashMap<Event,Future> futureMap;
	private ConcurrentHashMap<MicroService,List<Class<? extends Message>>> subscribeMap;
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
		register(m);
		synchronized (eventMap) {
			if (!eventMap.containsKey(type))
				eventMap.put(type, new LinkedList<>());
		}
		synchronized (eventMap.get(type)){
			if (!eventMap.get(type).contains(m)) {
				eventMap.get(type).add(m);
				subscribeMap.get(m).add(type);
			}
			eventMap.get(type).notifyAll();
		}
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		register(m);
		synchronized (broadcastMap) {
			if (!broadcastMap.containsKey(type)) {
				broadcastMap.put(type, new LinkedList<>());
				broadcastMap.notifyAll();
			}
		}
		synchronized (broadcastMap.get(type)){
			if (!broadcastMap.get(type).contains(m))
				broadcastMap.get(type).add(m);
			broadcastMap.get(type).notifyAll();
		}
	}

	@Override @SuppressWarnings("unchecked")
	public <T> void complete(Event<T> e, T result) {
		futureMap.get(e).resolve(result);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		synchronized (broadcastMap) {
			while (!broadcastMap.containsKey(b.getClass())) {
				try {
					broadcastMap.wait();
				} catch (InterruptedException ignored) {
				}
			}
		}
		synchronized (broadcastMap.get(b.getClass())){
			for (MicroService m : broadcastMap.get(b.getClass())) {
				synchronized (queueMap.get(m)) {
					queueMap.get(m).offer(b);
					queueMap.get(m).notifyAll();
				}
			}
		}
	}


	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		synchronized (eventMap) {
			while (!eventMap.containsKey(e.getClass())) {
				try {
					eventMap.wait();
				} catch (InterruptedException ignored) {
				}
			}
		}
		synchronized (eventMap.get(e.getClass())){
			MicroService m = eventMap.get(e.getClass()).poll();
			assert m != null;
			synchronized (queueMap.get(m)) {
				queueMap.get(m).offer(e);
				queueMap.get(m).notifyAll();
			}
			eventMap.get(e.getClass()).offer(m);
		}
		Future<T> f = new Future<>();
		futureMap.put(e,f);
		return f;
	}

	@Override
	public void register(MicroService m) {
		if (!isRegistered(m)) {
			queueMap.put(m, new LinkedList<>());
			subscribeMap.put(m,new LinkedList<>());
		}
	}

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

	private boolean isRegistered(MicroService m){
		return queueMap.containsKey(m);
	}


	private void unsubscribeEventOrBroadcast(Class<? extends Message> c, MicroService m){
		if (eventMap.containsKey(c)){
			eventMap.get(c).remove(m);
		}
		if (broadcastMap.containsKey(c)){
			broadcastMap.get(c).remove(m);
		}
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		if (!isRegistered(m))
			throw new InterruptedException(m.getName()+" hasn't been registered to the message bus");
		synchronized (queueMap.get(m)) {
			while (queueMap.get(m).isEmpty()) {
				try {
					queueMap.get(m).wait();
				}catch(InterruptedException e){}
			}
		}
		return queueMap.get(m).poll();
	}
	public void clean(){
		single_instance=null;
	}
}