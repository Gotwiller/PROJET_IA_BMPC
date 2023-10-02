package Robot.Motor;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;

public class Pliers extends EV3LargeRegulatedMotor {

	public Pliers(Port p) {
		super(p);
	}
	
	public void open() {
		this.rotate(360*2);
	}
	
	
}