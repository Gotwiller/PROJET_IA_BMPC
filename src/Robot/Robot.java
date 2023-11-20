package Robot;

import java.lang.Object;

import lejos.hardware.sensor.EV3ColorSensor;
import Robot.Motor.CustomWheelsChassis;
import Robot.Motor.Pliers;
import Robot.Position.Position;
import Robot.Sensor.ColorSensor;
import Robot.Sensor.TouchSensor;
import Robot.Sensor.UltrasonSensor;
import lejos.hardware.Brick;
import lejos.hardware.BrickFinder;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.hardware.Device;
import lejos.hardware.sensor.BaseSensor;
import lejos.hardware.sensor.AnalogSensor;
import lejos.hardware.sensor.NXTLightSensor;
import lejos.hardware.sensor.NXTColorSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.robotics.SampleProvider;



public class Robot {

	public static final int WHEEL_DIAMETER= 56;
	public static final float WHEEL_OFFSET_VALUE = 61.5f;
	public static final int ACCEPTED_DISTANCE_ERROR = 50;	// en millimetre
	public static final int MIN_WALL_DISTANCE = 150;		// en millimetre

	private Brick brick;

	private Position position;

	private CustomWheelsChassis wheels;
	private Pliers pliers;
	private ColorSensor colorSensor;
	private TouchSensor touchSensor;
	private UltrasonSensor ultrasonSensor;

	public Robot() {

		brick = BrickFinder.getDefault();

		Wheel leftWheel = WheeledChassis.modelWheel(Motor.D, WHEEL_DIAMETER).offset(-WHEEL_OFFSET_VALUE);
		Wheel rightWheel = WheeledChassis.modelWheel(Motor.C, WHEEL_DIAMETER).offset(WHEEL_OFFSET_VALUE);

		wheels = new CustomWheelsChassis(new Wheel[]{leftWheel, rightWheel}, WheeledChassis.TYPE_DIFFERENTIAL);
		pliers = new Pliers(Motor.A);

		colorSensor = new ColorSensor(SensorPort.S1);	   
		touchSensor = new TouchSensor(SensorPort.S3);		
		ultrasonSensor = new UltrasonSensor(SensorPort.S4); 
		//ultrasonSensor.enable();
	}

	public void start(char side, char line) {
		position = new Position(side, line);
		boolean puckFound;
		int distance = 2500;
		do {
			// Go forward while nothing happen = green part
			puckFound = goFindPuck(distance);
			if(!puckFound) {
				distance = position.getExpectedDistance()-MIN_WALL_DISTANCE+50;
				continue;
			} else { // Suspect detection
				// TODO puck found
			}
			/*// Useless ?
				else { // Full travel
				verifyPosition(); // Check if the position is correct and modify the position if necessary
				rotateForFindPuck();
				distance = position.getExpectedDistance()-MIN_WALL_DISTANCE+50;
				continue;
			 */
		} while(true); // TODO : modify the condition : if we do all the map without find puck (go back home on the white line because it's fun)
	}

	/**
	 * Go forward and return the next action.
	 * 
	 * @param distance Max distance to do
	 * @return if a puck is found
	 */
	private boolean goFindPuck(int distance) {
		long newTime, time = System.currentTimeMillis();
		float[] front = new float[1];
		int detectedDistance, expectedDistance, numberOfSuspectDetection = 0;
		wheels.travel(distance);
		while(wheels.isMoving()) {
			// Update the position if necessary (every 2 seconds)
			newTime = System.currentTimeMillis();
			if(newTime-time > 2000) {
				position.updateLinear(wheels.getLinearSpeed(),newTime-time);
				time = newTime;
			}

			// Get the detected distance & expected distance
			detectedDistance = ultrasonSensor.getDetectedDistance();
			expectedDistance = position.getExpectedDistance();

			// White line
			if(colorSensor.isWhiteDetected()) {
				wheels.stop();
				position.updateLinear(wheels.getLinearSpeed(),System.currentTimeMillis()-time);
				return false; // Need To Rotate
			} 
			// To close to wall
			if(/* Useless ? detectedDistance > expectedDistance-ACCEPTED_DISTANCE_ERROR &&*/ detectedDistance < MIN_WALL_DISTANCE) {
				wheels.stop();
				position.updateLinear(wheels.getLinearSpeed(),System.currentTimeMillis()-time);
				return false; // Need To Rotate
			} 
			// Suspect detection
			if (detectedDistance + ACCEPTED_DISTANCE_ERROR < expectedDistance){
				wheels.stop();
				newTime = System.currentTimeMillis();
				position.updateLinear(wheels.getLinearSpeed(),newTime-time);
				int res = suspectDetection();
				numberOfSuspectDetection++;
				if(res == 0 || numberOfSuspectDetection == 4) 
					return false; // Need To Rotate
				else if(res == 2) 
					return true; // Puck, need to rush
				time = System.currentTimeMillis();
				continue; // Nothing, error on the captor / a robot on a frame, continue
			}
		}
		wheels.stop();
		position.updateLinear(wheels.getLinearSpeed(),System.currentTimeMillis()-time);
		return false; // Normalement il n'y a pas a aller ici
	}
	/**
	 * Use the UltrasonSensor to determine the nature of the detection.
	 * 
	 * @return
	 *     0 = not a puck, need to rotate (close to wall, robot or angle)
	 *     1 = nothing, continue
	 *     2 = puck
	 */
	private int suspectDetection() {
		int expectedDistance = ultrasonSensor.getDetectedDistance();
		return ultrasonSensor.clarifySuspectDetection(expectedDistance);
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
		long newTime, time = System.currentTimeMillis();
		int angleRetour = position.calculateAngleToReturnHome();
		int cote; //-1 si plus proche du mur gauche / 1 si plus proche du mur droit
		rotate(angleRetour); position.updateAngle(angleRetour);// update l'angle

		while(colorSensor.isWhiteDetected()==false) {
			// MAJ de la position toute les 100ms
			newTime = System.currentTimeMillis();
			if(newTime-time > 100) {
				position.updateLinear(wheels.getLinearSpeed(),newTime-time);
				time = newTime;
			}
			//Detecte rien
			if(suspectDetection()==0) wheels.travel(2500);

			// Detecte un palais
			else if (suspectDetection()==2){ 	
				wheels.stop(); position.updateLinear(wheels.getLinearSpeed(),System.currentTimeMillis()-time);
				// Coté par lequel eviter
				if (position.getX()<1000 && (position.getHome()=='g')||position.getX()>1000 && (position.getHome()=='b')) 
					cote = -1; // Eviter par la droite
				else cote= 1; // Eviter par la gauche
				rotate(-45*cote); position.updateAngle(-45*cote);
				// Cas ou le robot tourne de 45degres ET palais dans le champ
				while (suspectDetection()==2) { 
					rotate(45*cote); position.updateAngle(45*cote);
					wheels.travel(100); 
					wheels.stop(); position.updateLinear(wheels.getLinearSpeed(),System.currentTimeMillis()-time);
					rotate(-45*cote); position.updateAngle(-45*cote);
				}
				wheels.travel(200); 
				wheels.stop(); position.updateLinear(wheels.getLinearSpeed(),System.currentTimeMillis()-time);
				rotate(45*cote); position.updateAngle(45*cote);
			}
		}	
		wheels.stop(); position.updateLinear(wheels.getLinearSpeed(),System.currentTimeMillis()-time);
	}

	public void test() {
		pliers.open(true);
		while(pliers.isMoving()) {}
		pliers.close();
	}

	public void AllerVersPuck(double targetX, double targetY) {
		double dx = targetX - position.getX();
		double dy = targetY - position.getY();
		double targetAngle = Math.toDegrees(Math.atan2(dy, dx));
		double distance = Math.sqrt(dx * dx + dy * dy);
		double angleDiff = targetAngle - position.getOrientation();
		wheels.rotate(angleDiff);
		wheels.moveForward(distance);

		position.setX(targetX);
		position.setY(targetY);
		position.setOrientation(targetAngle);
	}

	public void getPuck() {
		double distance = ultrasonSensor.getDetectedDistance();
		boolean isTouche = touchSensor.isPressed();
		if (distance < 0.1 && isTouche) {
			pliers.open();
			wheels.moveForward(0.2); 
			pliers.close();
		}
	}
}