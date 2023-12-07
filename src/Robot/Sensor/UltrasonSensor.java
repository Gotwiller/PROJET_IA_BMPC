package Robot.Sensor;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.utility.Delay;

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
	/**
	 * Get the detected distance from the UltrasonSensor in meter.
	 * 
	 * @return a distance in meter
	 */
	public float getDetectedDistanceBrut() {
		float[] front = new float[1];
		getDistanceMode().fetchSample(front, 0);
		return front[0];
	}

	/**
	 * clarify if the detection is a puck , a wall/robot/angle or if it's a wrong detection
	 * 
	 * @param expectedDistance
	 * @return 	0 = not a puck, need to rotate (close to wall, robot or angle)
	 *     		1 = nothing, continue
	 *     		2 = puck
	 */
	public int clarifySuspectDetection(int expectedDistance) {
		int detected = getDetectedDistance(), erreur = 50;
		Delay.msDelay(300);
		int detected2 = getDetectedDistance();
		if (expectedDistance >= detected2-erreur && expectedDistance <= detected2+erreur)
			return 0;
		if (expectedDistance <= detected2-erreur && expectedDistance >= detected2+erreur && detected >= detected2-erreur && detected <= detected2+ erreur)
			return 2;
		else return 1;
	}
}