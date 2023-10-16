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
		try {Thread.sleep(15);}
		catch (Exception e) {}
		int detected2 = getDetectedDistance();
		if (expectedDistance >= detected2-erreur && expectedDistance <= detected2+erreur)
			return 0;
		if (expectedDistance <= detected2-erreur && expectedDistance >= detected2+erreur && detected >= detected2-erreur && detected <= detected2+ erreur)
			return 2;
		else return 1;
	}
	
}
