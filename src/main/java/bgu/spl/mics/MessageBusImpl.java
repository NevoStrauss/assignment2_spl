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

public class MessageBusImpl implements MessageBus {
	private static class single_instance{
		private static final MessageBusImpl single_instance = new MessageBusImpl();
	}
	//fields
	private final ConcurrentHashMap<MicroService, Queue<Message>> queueMap;
	private final ConcurrentHashMap<Class<? extends Event>, Queue<MicroService>> eventMap;
	private final ConcurrentHashMap<Class<? extends Broadcast>,List<MicroService>> broadcastMap;
	private final ConcurrentHashMap<Event,Future> futureMap;
	private final ConcurrentHashMap<MicroService,List<Class<? extends Message>>> subscribeMap;


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
		return single_instance.single_instance;
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
			synchronized (eventMap) {			//can change to putIfAbsent
				if (!eventMap.containsKey(type))
					eventMap.put(type, new LinkedList<>());
			}
			eventMap.putIfAbsent(type,new LinkedList<>());
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

	@Override @SuppressWarnings({"unchecked"})
	public <T> void complete(Event<T> e, T result) {
		futureMap.get(e).resolve(result);
		futureMap.remove(e);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		long startTime = System.currentTimeMillis();
		boolean toContinue = true;
		if (!broadcastMap.containsKey(b.getClass())){
			synchronized (broadcastMap) {
				while (!broadcastMap.containsKey(b.getClass())) {
					try {
						broadcastMap.wait(50);
						if (System.currentTimeMillis()-startTime>500)
							toContinue = false;
					} catch (InterruptedException ignored) {
					}
				}
			}
		}
		if (toContinue) {
			synchronized (broadcastMap.get(b.getClass())) {
				for (MicroService m : broadcastMap.get(b.getClass())) {
					synchronized (queueMap.get(m)) {
						queueMap.get(m).offer(b);
						queueMap.get(m).notifyAll();
					}
				}
			}
		}
	}



	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		long startTime = System.currentTimeMillis();
		if (!eventMap.containsKey(e.getClass())){
			synchronized (eventMap) {
				while (!eventMap.containsKey(e.getClass())) {
					try {
						eventMap.wait(50);
						if (System.currentTimeMillis() - startTime > 500)
							return null;
					} catch (InterruptedException ignored) {
					}
				}
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

	@Override
	public Message awaitMessage(MicroService m){
		if (!isRegistered(m))
			throw new RuntimeException(m.getName()+" hasn't been registered to the message bus");
		if (queueMap.get(m).isEmpty()) {
			synchronized (queueMap.get(m)) {
				while (queueMap.get(m).isEmpty()) {
					try {
						queueMap.get(m).wait();
					} catch (InterruptedException e) {
					}
				}
			}
		}
		return queueMap.get(m).poll();
	}
}