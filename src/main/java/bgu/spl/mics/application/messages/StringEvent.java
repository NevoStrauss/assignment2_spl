package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

public class StringEvent implements Event<String> {
    private String name;
    private boolean finished;

    public StringEvent(String name){
        this.name=name;
        finished=false;
    }

    public StringEvent(StringEvent other){
        this.name=other.name;
        this.finished= other.finished;
    }

    public boolean isFinished() {
        return finished;
    }

    public String getName() {
        return name;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }
}
