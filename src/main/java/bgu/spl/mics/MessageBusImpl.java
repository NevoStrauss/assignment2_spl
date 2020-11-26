package bgu.spl.mics;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	//fields
	private HashMap<Class,Queue<Message>> my_map;
	private static MessageBusImpl single_instance = null;

	//CTR
	private MessageBusImpl(){
		my_map = new HashMap<Class, Queue<Message>>();
	}

	//methods
	public static MessageBusImpl getInstance() {
		if (single_instance == null)
			single_instance = new MessageBusImpl();
		return single_instance;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		
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
		if (!my_map.containsKey(m.getClass()))
			my_map.put(m.getClass(), new LinkedList<>());
	}

	@Override
	public void unregister(MicroService m) {
		
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		if (!my_map.containsKey(m.getClass()))
			throw new InterruptedException(m.getClass()+" hasn't been registered to the message bus");
		if (my_map.get(m.getClass()).isEmpty()) {
		//waits until there is a message
		}
		return my_map.get(m.getClass()).poll();
	}
}