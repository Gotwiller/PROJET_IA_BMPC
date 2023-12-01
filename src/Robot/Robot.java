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

	private static final int ANGLE_45 = 45;
	private static final int ANGLE_180 = 180;
	private static final int MOINS_ANGLE_45 = -45;
	private static final int MILLIEU_TERRAIN = 1000;
	private static final int TIMER_UPDATE = 100;
	private static final int DISTANCE_ENTRE_2CAMPS = 2500;
	private static final int DISTANCE = 200;
	private static final int GAUCHE = -1;
	private static final int DROITE = 1;



	private Brick brick;

	private Position position;

	private CustomWheelsChassis wheels;
	private Pliers pliers;
	private ColorSensor colorSensor;
	private TouchSensor touchSensor;
	private UltrasonSensor ultrasonSensor;

	public Position getPosition() { return position; }
	public CustomWheelsChassis getWheels() { return wheels; }
	public Pliers getPliers() { return pliers; }
	public ColorSensor getColorSensor() { return colorSensor; }
	public TouchSensor getTouchSensor() { return touchSensor; }
	public UltrasonSensor getUltrasonSensor() { return ultrasonSensor; }

	public Robot() {

		brick = BrickFinder.getDefault();

		//Wheel leftWheel = WheeledChassis.modelWheel(Motor.D, WHEEL_DIAMETER).offset(-WHEEL_OFFSET_VALUE);
		//Wheel rightWheel = WheeledChassis.modelWheel(Motor.C, WHEEL_DIAMETER).offset(WHEEL_OFFSET_VALUE);

		wheels = new CustomWheelsChassis(WHEEL_DIAMETER, WheeledChassis.TYPE_DIFFERENTIAL);
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

	private void rotateForBackHome() {
		long newTime, time = System.currentTimeMillis();
		int angleRetour = (int)position.calculateAngleToReturnHome();
		int cote; // -1 si plus proche du mur gauche / 1 si plus proche du mur droit
		rotate(angleRetour); position.updateAngle(angleRetour);// update l'angle
		while (wheels.isMoving());
		wheels.travel(DISTANCE_ENTRE_2CAMPS);  
		while(colorSensor.isWhiteDetected()==false) {
			// MAJ de la position toute les 100ms
			newTime = System.currentTimeMillis();
			if(newTime-time > TIMER_UPDATE) {
				position.updateLinear(wheels.getLinearSpeed(),newTime-time);
				time = newTime;
			}

			while(wheels.isMoving()) {
				// Update the position if necessary (every 2 seconds)
				newTime = System.currentTimeMillis();
				if(newTime-time > TIMER_UPDATE) {
					position.updateLinear(wheels.getLinearSpeed(),newTime-time);
				}
				time = newTime;
			}

			//Detecte rien
			if(suspectDetection()==0) continue;

			// Detecte un palais
			else if (suspectDetection()==2){ 	
				wheels.stop(); position.updateLinear(wheels.getLinearSpeed(),System.currentTimeMillis()-time);
				// Coté par lequel eviter
				if (position.getX()<MILLIEU_TERRAIN && (position.getHome()=='g')||position.getX()>MILLIEU_TERRAIN && (position.getHome()=='b')) 
					cote = GAUCHE; // Eviter par la droite
				else cote= DROITE; // Eviter par la gauche
				rotate(MOINS_ANGLE_45*cote); while(wheels.isMoving()); position.updateAngle(MOINS_ANGLE_45*cote);
				// Cas ou le robot tourne de 45degres ET palais dans le champ
				while (suspectDetection()==2) { 
					rotate(ANGLE_45*cote); while(wheels.isMoving()); position.updateAngle(ANGLE_45*cote);
					wheels.travel(100); while(wheels.isMoving());
					wheels.stop(); position.updateLinear(wheels.getLinearSpeed(),System.currentTimeMillis()-time);
					rotate(MOINS_ANGLE_45*cote); while(wheels.isMoving()); position.updateAngle(MOINS_ANGLE_45*cote);
				}
				wheels.travel(DISTANCE); while (wheels.isMoving());
				wheels.stop(); position.updateLinear(wheels.getLinearSpeed(),System.currentTimeMillis()-time);
				rotate(ANGLE_45*cote);while (wheels.isMoving()); position.updateAngle(ANGLE_45*cote);
			}
		}
		wheels.stop(); position.updateLinear(wheels.getLinearSpeed(),System.currentTimeMillis()-time);
	}

	//public void test() {
	/*position = new Position('b','y');
		wheels.travel(1500); 
		while (wheels.isMoving());
		wheels.stop(); position.updateLinear(wheels.getLinearSpeed(),System.currentTimeMillis());
		wheels.rotate(30);
		while (wheels.isMoving());
		position.updateAngle(30);
		rotateForBackHome();*/
	//LCD.drawString(colorSensor.toString(),0 ,0 ); // Utiliser instance car Class est static
	//return Button.waitForAnyPress();

	/*for(int i =0; i<10;i++) {
			if(suspectDetection()==0) {
				wheels.travel(50);
				while (wheels.isMoving());
			}

			else if(suspectDetection()==2) {
				wheels.rotate(50);
				while (wheels.isMoving());
				wheels.stop();
			}
		}
	}*/

	public void test() {
	        long time = System.currentTimeMillis();
	        position = new Position(300,1500,0);
	        wheels.rotateRight(45);
	        while (wheels.isMoving());
	        position.updateAngle(45);
	        wheels.travel(300);
	        while (wheels.isMoving());
	        position.updateLinear(wheels.getLinearSpeed(),System.currentTimeMillis()-time);
	        wheels.rotateLeft(45);
	        while (wheels.isMoving());
	        position.updateAngle(MOINS_ANGLE_45);
	        while (ultrasonSensor.getDetectedDistance()>=30) {
	        wheels.travel(1000);
	        while (wheels.isMoving()) { 
	        	avoid();
	        }
	        }
		}
		
		// Detecte si detection proche et évite l'obstacle par la droite
		
		public void avoid () {
			int detected = ultrasonSensor.getDetectedDistance();
			long time = System.currentTimeMillis();
			Delay.msDelay(20);
			if (ultrasonSensor.getDetectedDistance()<= 200) {
				wheels.stop();
				wheels.rotateRight(90);
				while (wheels.isMoving());
	        	position.updateAngle(90);
	        	wheels.travel(300);
	        	while (wheels.isMoving());
	        	position.updateLinear(wheels.getLinearSpeed(),System.currentTimeMillis()-time);
	        	wheels.rotateLeft(90);
	        	while (wheels.isMoving());
	        	position.updateAngle(-90);
			}
		}

	public boolean allerVersPuck(double distance) {
		distance+=50;
		//double dx = targetX - position.getX();
		//double dy = targetY - position.getY();
		// double targetAngle = Math.toDegrees(Math.atan2(dy, dx));
		//double distance = Math.sqrt(dx * dx + dy * dy);
		// double angleDiff = targetAngle - position.getOrientation();
		// wheels.rotate(angleDiff);
		long times = System.currentTimeMillis();
		wheels.moveForward(distance);
		while(wheels.isMoving()) {
			if(touchSensor.isPressed()) {
				wheels.stop();
				position.updateLinear(wheels.getLinearSpeed(), System.currentTimeMillis() - times);
				return true;
			}

		}
		position.updateLinear(wheels.getLinearSpeed(), System.currentTimeMillis() - times);
		return false;
		

		// position.setOrientation(targetAngle);
	}



	public void getPuck() {
		// double distance = ultrasonSensor.getDetectedDistance();
		//pliers.open();
		wheels.moveBackward(1); 
		while(wheels.isMoving());
		pliers.close();
	}

}
