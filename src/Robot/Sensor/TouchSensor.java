package Robot.Sensor;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3TouchSensor;

public class TouchSensor extends EV3TouchSensor {

	public TouchSensor(Port p) {
		super(p);
	}
	public boolean isPressed() {
		if (getTouchMode()== getMode(0))
		return false;
		else return true;
	}
}