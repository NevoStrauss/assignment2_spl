package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Attack;

public class AttackEvent implements Event<Boolean> {
	private final Attack attack;
	private boolean finished;

	public AttackEvent(Attack attack){
	    this.attack=attack;
	    finished=false;
    }

	public Attack getAttack() {
		return attack;
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}
}
