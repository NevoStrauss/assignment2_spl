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

	/**
	 * @return the attack object field of the AttackEvent
	 */
	public Attack getAttack() {
		return attack;
	}

	/**
	 * @return if this attack is finished, a.k.a executed by one of the Attacking MicroServices.
	 */
	public boolean isFinished() {
		return finished;
	}

	/**
	 * @param finished for setting this AttackEvent to finished.
	 */
	public void setFinished(boolean finished) {
		this.finished = finished;
	}
}
