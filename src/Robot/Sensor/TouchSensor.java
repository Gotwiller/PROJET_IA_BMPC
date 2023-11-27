package Robot.Sensor;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3TouchSensor;

public class TouchSensor extends EV3TouchSensor {

	public TouchSensor(Port p) {
		super(p);
	}
	public boolean isPressed() {
		float[] touch = new float[1];
		getTouchMode().fetchSample(touch, 0);
		return touch[0]==1;
	}
}