package Robot.Motor;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;

public class Pliers extends EV3LargeRegulatedMotor {
	
	public Pliers(Port p) {
		super(p);
	}
	public void open () {
	pliers.rotate(360*2);
}	
	public void close() {
	pliers.rotate(-360*2);
	}