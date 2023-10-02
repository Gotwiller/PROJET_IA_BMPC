package Robot.Motor;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;

public class Pliers extends EV3LargeRegulatedMotor {
	boolean closed = true;
	public Pliers(Port p) {
		super(p);
	}

	public void open() {
		open(false);
	}	
	public void open(boolean force) {
		if (force==true) {
			this.rotate(360*2);
			closed = false;
		}
		if (isOpen()==false) {
			this.rotate(360*2);
			closed = false;
		}
	}
	public void close() {
		close(false);
	}	
	public void close(boolean force) {
		if (force==true) {
			this.rotate(-360*2);
			closed = true;
		}
		if (isClose()==false) {
			this.rotate(-360*2);
			closed = true;
		}
	}
	public boolean isOpen() {
		if (closed==true) {
			return false;
		}
		else return true;
	}
	public boolean isClose() {
		if (closed==false) {
			return false;
		}
		else return true;
	}
}

