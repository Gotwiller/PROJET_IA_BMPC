package Robot.Motor;

import lejos.hardware.motor.NXTRegulatedMotor;

public class Pliers {
	
	private NXTRegulatedMotor motor;
	boolean closed;
	
	public Pliers(NXTRegulatedMotor motor) {
		this.motor = motor;
		this.motor.setSpeed(motor.getSpeed()*2);
		closed = true;
	}

	/**
	 * Open the pliers.
	 */
	public void open() {
		open(false);
	}
	/**
	 * Open the pliers. You can force them to open.
	 * 
	 * @param force Forced opening
	 */
	public void open(boolean force) {
		if (closed || force) {
			motor.rotate(360*3);
			while(motor.isMoving()) {}
			closed = false;
		}
	}
	/**
	 * Open the pliers. You can force them to open.
	 * 
	 * @param force Forced opening
	 */
	public void openDesyncr() {
		if (closed) {
			motor.rotate(360*3);
			closed = false;
		}
	}
	
	/**
	 * Close the pliers.
	 */
	public void close() {
		close(false);
	}	
	/**
	 * Close the pliers. You can force them to close.
	 * 
	 * @param force Forced closing
	 */
	public void close(boolean force) {
		if (!closed || force) {
			motor.rotate(-360*3);
			while(motor.isMoving()) {}
			closed = true;
		}
	}
	
	/**
	 * Check if the pliers is open.
	 * 
	 * @return true if open, else false
	 */
	public boolean isOpen() {
		return !closed;
	}
	/**
	 * Check if the pliers is closed.
	 * 
	 * @return true if close, else false
	 */
	public boolean isClose() {
		return closed;
	}

	/**
	 * Set the "closed" value to true or false.
	 * 
	 * @param closed
	 */
	public void setClosed(boolean closed) {
		this.closed=closed;
	}
}