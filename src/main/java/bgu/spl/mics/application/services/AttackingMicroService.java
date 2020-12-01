package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;

public abstract class AttackingMicroService extends MicroService {
    /**
     * @param name the micro-service name (used mainly for debugging purposes -
     *             does not have to be unique)
     */
    public AttackingMicroService(String name) {
        super(name);
    }

    /**
     * this method is called once when the event loop starts.
     */
    @Override
    protected abstract void initialize();
}
