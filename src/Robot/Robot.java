package Robot;

import Robot.Motor.CustomWheelsChassis;
import Robot.Motor.Pliers;
import Robot.Position.Position;
import Robot.Sensor.ColorSensor;
import Robot.Sensor.TouchSensor;
import Robot.Sensor.UltrasonSensor;
import lejos.hardware.Brick;
import lejos.hardware.BrickFinder;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.SensorPort;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.utility.Delay;




public class Robot {



	private static final int WHEEL_DIAMETER= 56;
	private static final float WHEEL_OFFSET_VALUE = 61.5f;

	private Brick brick;

	private Position position;

	private CustomWheelsChassis wheels;
	private Pliers pliers;

	private ColorSensor colorSensor;
	private TouchSensor touchSensor;
	private UltrasonSensor ultrasonSensor;

	private static final int ACCEPTED_DISTANCE_ERROR = 50;	// in millimeter
	private static final int MIN_WALL_DISTANCE = 150;		// in millimeter

	public Robot() {
		brick = BrickFinder.getDefault();

		Wheel leftWheel = WheeledChassis.modelWheel(Motor.D, WHEEL_DIAMETER).offset(-WHEEL_OFFSET_VALUE);
		Wheel rightWheel = WheeledChassis.modelWheel(Motor.C, WHEEL_DIAMETER).offset(WHEEL_OFFSET_VALUE);

		wheels = new CustomWheelsChassis(new Wheel[]{leftWheel, rightWheel}, WheeledChassis.TYPE_DIFFERENTIAL);
		pliers = new Pliers(brick.getPort("A"));

		colorSensor = new ColorSensor(SensorPort.S1);	   
		touchSensor = new TouchSensor(SensorPort.S2);		
		ultrasonSensor = new UltrasonSensor(SensorPort.S3); 
		ultrasonSensor.enable();
	}

	public boolean isWhite() {
		ColorSensor cs = new ColorSensor(SensorPort.S3);
		cs.setFloodlight(false);
		if (cs.isWhiteDetected()) {            
			return true;
		}     
		else
			return false;           
	}

	private void start(char side, char line) {
		position = new Position(side, line);
		int res, distance = 2500;
		do {
			// Go forward while nothing happen
			res = goForward(distance);
			if(res == 0) { // White line
				rotateForFindPuck();
				distance = position.getExpectedDistance()-MIN_WALL_DISTANCE+50;
				continue;
			} else if(res == 1) { // To close to wall
				rotateForFindPuck();
				distance = position.getExpectedDistance()-MIN_WALL_DISTANCE+50;
				continue;
			} else if (res == 2) { // Suspect detection
				res = suspectDetection(); // Clarified the suspicious detection
				// Simple wall far away
				if(res == 0) {
					distance = ultrasonSensor.getDetectedDistance()-MIN_WALL_DISTANCE+50;
					continue;
				} // To close to wall
				else if(res == 1) {
					rotateForFindPuck();
					distance = position.getExpectedDistance()-MIN_WALL_DISTANCE+50;
					continue;
				} // Puck
				else if(res == 2) {
					boolean havePuck = rushTakePuck(ultrasonSensor.getDetectedDistance()+100);
					// TODO
				} // Robot
				else if(res == 3) {
					// TODO
				}
				else throw new RuntimeException("the return of the suspectDetection is incorrect");
			} else { // Full travel
				verifyPosition(); // Check if the position is correct and modify the position if necessary
				rotateForFindPuck();
				distance = position.getExpectedDistance()-MIN_WALL_DISTANCE+50;
				continue;
			}
		} while(true); // TODO : modify the condition : if we do all the map without find puck (go back home on the white line because it's fun)
	}

	/**
	 * Go forward and return the next action.
	 * 
	 * @param distance Max distance to do
	 * @return 
	 * 		0 = white line 
			1 = To close to wall.
	 * 		2 = suspect detection.
	 * 		3 = full travel
	 */
	private int goForward(int distance) {
		long newTime, time = System.currentTimeMillis();
		float[] front = new float[1];
		int detectedDistance, expectedDistance;
		wheels.travel(distance);
		while(wheels.isMoving()) {
			// Update the position if necessary (every 2 seconds)
			newTime = System.currentTimeMillis();
			if(newTime-time > 1000) {
				position.update(wheels.getLinearSpeed());
				time = newTime;
			}

			// Get the detected distance & expected distance
			detectedDistance = ultrasonSensor.getDetectedDistance();
			expectedDistance = position.getExpectedDistance();

			// White line
			if(colorSensor.isWhiteDetected()) {
				return 0;
			}
			// To close to wall
			if(detectedDistance > expectedDistance-ACCEPTED_DISTANCE_ERROR && detectedDistance < MIN_WALL_DISTANCE) {
				wheels.stop();
				position.update(wheels.getLinearSpeed()*System.currentTimeMillis()-time);
				return 1;
			} 
			// Suspect detection
			if (detectedDistance + ACCEPTED_DISTANCE_ERROR < expectedDistance){
				wheels.stop();
				position.update(wheels.getLinearSpeed()*System.currentTimeMillis()-time);
				return 2;
			}
		}
		wheels.stop();
		position.update(wheels.getLinearSpeed()*System.currentTimeMillis()-time);
		return 3;
	}
	/**
	 * Do a series of 2 distance measurements using the UltrasonSensor to determine the nature of the detection.
	 * 
	 * @return
	 *     0 = wall far away.
	 *     1 = to close to wall.
	 *     2 = puck.
	 *     3 = robot.
	 */
	private int suspectDetection() {
		int expectedDistance = ultrasonSensor.getDetectedDistance();
		int detectedDistance1 = ultrasonSensor.getDetectedDistance();
		Delay.msDelay(1000);
		int detectedDistance2 = ultrasonSensor.getDetectedDistance();
		// It's a simple wall 
		if(detectedDistance1 > expectedDistance-ACCEPTED_DISTANCE_ERROR) {
			// Wall to close
			if(detectedDistance1 < MIN_WALL_DISTANCE)
				return 1;
			// Wall far away
			return 0;
		}
		// Same distance +- 5 millimeter = a puck
		else if(detectedDistance1 > detectedDistance2-5 && detectedDistance1 < detectedDistance2+5)
			return 2;
		// Isn't the same distance = robot
		else
			return 3;
	}

	/**
	 * Rotate by a certain angle and update the position.
	 * 
	 * @param angle
	 */
	private void rotate(int angle) {
		// TODO
	}
	/**
	 * Do the best rotation for find a puck.
	 */
	private void rotateForFindPuck() {
		// TODO
	}
	/**
	 * Do the best rotation to back home.
	 * 
	 * @param dodge If it's for dodge a object in front of the robot.
	 */
	private void rotateForBackHome(boolean dodge) {
		int angleRetour = position.calculateAngleToReturnHome();
		rotate(angleRetour);
		position.updateAngle();// update l'angle
		
		while(colorSensor.isWhiteDetected()==false) {
			if(suspectDetection()==0) {	//detection
				goForward(2500); // Creer methode pour calculer distance de la ligne blanche?? ou alors avancer jusqu a detecter la ligne blanche?
				position.updateLinear();}
			
			else if (suspectDetection()==2){
				// eviter par la droite
				if (position.getX()<1000 && (position.getHome()=='g')||position.getX()>1000 && (position.getHome()=='b')) { 
					rotate(45);
					position.updateAngle(); // update l'angle
					goForward(200); //avancer pour se decaler de 20cm
					position.updateLinear();//update x et y 
					rotate(-45);//tourner de 45 degres
					position.updateAngle();
				}

				// eviter par la gauche
				rotate(-45);
				position.updateAngle();// update l'angle
				goForward(200); //avancer pour se decaler de 20cm
				position.updateLinear();//update x et y 
				rotate(45);
				position.updateAngle(); //update l'angle
				}
			}
		
	}

	public void test() {
		pliers.close(true);
		while(pliers.isMoving()) {}
		pliers.open();
	}
}