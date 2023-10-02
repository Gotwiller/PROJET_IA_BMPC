package Robot.Motor;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;

public class Pliers extends EV3LargeRegulatedMotor {
	boolean closed = true;
	public Pliers(Port p) {
		super(p);
	}

	public void open () {
		if (isOpen()==false) {
			this.rotate(360*2);
			closed = false;
		}
	}	
	public void close() {

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

