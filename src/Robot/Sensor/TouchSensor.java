package Robot.Sensor;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3TouchSensor;

public class TouchSensor extends EV3TouchSensor {

	public TouchSensor(Port p) {
		super(p);
	}
	
    /**
     * Checks if the touch sensor is currently pressed.
     *
     * @return true if the touch sensor is pressed, false otherwise.
     */
	public boolean isPressed() {
		float[] touch = new float[1];
		getTouchMode().fetchSample(touch, 0);
		return touch[0]==1;
	}
}