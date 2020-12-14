package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

public class DeactivationEvent implements Event<Boolean> {
    private boolean finished;

    /**
     * CTR
     */
    public DeactivationEvent(){
        finished=false;
    }

    /**
     * @return if this event is already executed by R2D2.
     */
    public boolean isFinished() {
        return finished;
    }

    /**
     * @param finished for setting this event to finished.
     */
    public void setFinished(boolean finished) {
        this.finished = finished;
    }
}

