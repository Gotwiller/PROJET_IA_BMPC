package Robot.Sensor;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3UltrasonicSensor;

public class UltrasonSensor extends EV3UltrasonicSensor {

	public UltrasonSensor(Port p) {
		super(p);
	}
	public SampleProvider getDistanceMode() {
		switchMode(MODE_DISTANCE,SWITCH DELAY);
		return getMode(0);
	}
}
