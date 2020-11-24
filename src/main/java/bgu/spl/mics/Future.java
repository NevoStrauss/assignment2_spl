package bgu.spl.mics;

import java.util.concurrent.TimeUnit;

/**
 * A Future object represents a promised result - an object that will
 * eventually be resolved to hold a result of some operation. The class allows
 * Retrieving the result once it is available.
 *
 * Only private methods may be added to this class.
 * No public constructor is allowed except for the empty constructor.
 */
public class Future<T> {
	private boolean isDone;
	private T result;

	/**
	 * This should be the the only public constructor in this class.
	 */
	public Future() {
		isDone=false;
		result=null;
	}

	/**
	 * retrieves the result the Future object holds if it has been resolved.
	 * This is a blocking method! It waits for the computation in case it has
	 * not been completed.
	 * <p>
	 * @return return the result of type T if it is available, if not wait until it is available.
	 *
	 */

//	@PRE: none
//	@POST: @POST(isDone)==true
	public T get() {
		if (!isDone)
			//wait until is done
			return result;
		return result;
	}

	/**
	 * Resolves the result of this Future object.
	 */
//	@PRE: @PRE(isDone())=false, @PRE(get(0,millisconds))=null
//	@POST: @POST(isDone())=true, @POST(get(0,milliseconds))=@param(result))
	public void resolve (T result) {
		if (isDone)
			throw new RuntimeException("Already resolved");
		isDone=true;
		this.result=result;
	}

	/**
	 * @return true if this object has been resolved, false otherwise
	 */
//	@PRE: none
//	@POST: @POST(isDone())=@PRE(isDone())
	public boolean isDone() {
		return isDone;
	}

	/**
	 * retrieves the result the Future object holds if it has been resolved,
	 * This method is non-blocking, it has a limited amount of time determined
	 * by {@code timeout}
	 * <p>
	 * @param timeout 	the maximal amount of time units to wait for the result.
	 * @param unit		the {@link TimeUnit} time units to wait.
	 * @return return the result of type T if it is available, if not,
	 * 	       wait for {@code timeout} TimeUnits {@code unit}. If time has
	 *         elapsed, return null.
	 */

//	@PRE: none
//	@POST: if isDone(): result!=null, else: @POST(System.currentmillis())=@PRE(System.currentmillis())+timeout in unit
	public T get(long timeout, TimeUnit unit) {
		if (!isDone)
			//wait timout in unit
			return result;

		return null;
	}
}