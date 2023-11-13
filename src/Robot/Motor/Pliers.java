package Robot.Motor;

import lejos.hardware.motor.NXTRegulatedMotor;

public class Pliers {
	
	private NXTRegulatedMotor motor;
	boolean closed;
	
	private Pliers(NXTRegulatedMotor motor) {
		this.motor = motor;
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
			motor.rotate(360*2);
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
			motor.rotate(-360*2);
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

	public boolean isMoving() {
		return motor.isMoving();
	}
}
