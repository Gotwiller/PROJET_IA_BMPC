package Robot.Sensor;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3UltrasonicSensor;

public class UltrasonSensor extends EV3UltrasonicSensor {

	public UltrasonSensor(Port p) {
		super(p);
	}
	/**
	 * Get the detected distance from the UltrasonSensor in millimeter.
	 * 
	 * @return a distance in millimeter
	 */
	public int getDetectedDistance() {
	    float[] front = new float[1];
	    getDistanceMode().fetchSample(front, 0);
	    return (int)(front[0]*1000);
	}
}
