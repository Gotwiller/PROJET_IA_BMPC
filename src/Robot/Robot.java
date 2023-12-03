package Robot;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
import lejos.robotics.chassis.WheeledChassis;
import lejos.utility.Delay;

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

	//attraper et emener dans les cages le premier palet 
	
	public void firstpuck() {
		pliers.setClosed(true);
		pliers.open();
		position = new Position(300,1500,0);
		boolean b = allerVersPuck(2000);
		if(b)
			getPuck();
		else
			pliers.close();
		wheels.rotateRight(45);
		while (wheels.isMoving());
		position.updateAngle(45);
		long time = System.currentTimeMillis();
		wheels.travel(300);
		while (wheels.isMoving());
		position.updateLinear(wheels.getLinearSpeed(),System.currentTimeMillis()-time);
		wheels.rotateLeft(45);
		while (wheels.isMoving());
		position.updateAngle(MOINS_ANGLE_45);
		time = System.currentTimeMillis();
		wheels.travel(2000);
		position.updateLinear(wheels.getLinearSpeed(),System.currentTimeMillis()-time);
		while (wheels.isMoving() && colorSensor.isWhiteDetected()== false) { 
			avoid();
			time = System.currentTimeMillis();
			wheels.travel(100);
			position.updateLinear(wheels.getLinearSpeed(),System.currentTimeMillis()-time);
		}
		pliers.open();
		time = System.currentTimeMillis();
		wheels.travel(-100);
		while (wheels.isMoving());
		position.updateLinear(wheels.getLinearSpeed(),System.currentTimeMillis()-time);
		pliers.close();
	}

	// Detecte si detection proche et évite l'obstacle par la droite

	public void avoid () {
		int detected = ultrasonSensor.getDetectedDistance();
		long time = System.currentTimeMillis();
		Delay.msDelay(20);
		if (ultrasonSensor.getDetectedDistance()<= 200) {
			wheels.stop();
			wheels.rotateRight(85);
			while (wheels.isMoving());
			position.updateAngle(85);
			wheels.travel(300);
			while (wheels.isMoving());
			position.updateLinear(wheels.getLinearSpeed(),System.currentTimeMillis()-time);
			wheels.rotateLeft(90);
			while (wheels.isMoving());
			position.updateAngle(-90);
		}
	}

	//attraper le palet
	
	public void catchPuck() {
		pliers.setClosed(true);
		pliers.open();
		position = new Position(300,1500,0);
		boolean b = allerVersPuck(2000);
		if(b)
			getPuck();
		else
			pliers.close();
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

	/**
	 * Find the closest puck and give its distance by looking at it
	 * 
	 * Inconsistent values observed even after all tests performed and modifications made
	 * 
	 * @return distance The distance of the puck
	 */
	public int findPuck2() {
		List<Integer> distances = new ArrayList<>();
		wheels.rotate(360);
		while(wheels.isMoving()) {
			distances.add(ultrasonSensor.getDetectedDistance());
			Delay.msDelay(4);
		}
		int[][] angles_distances = get2ClosestPuckAngleAndDistance(distances);
		/*
		try {
			int size = distances.size();

			BufferedWriter bw = new BufferedWriter(new FileWriter("info.txt",true));
			bw.write(angles_distances[0][0]+" ; "+angles_distances[0][1]);
			bw.newLine();
			bw.write(angles_distances[1][0]+" ; "+angles_distances[1][1]);
			bw.newLine();
			for(int i = 0; i < size; i++) {
				bw.write(i*360/size+"	"+distances.get(i));
				bw.newLine();
			}
			bw.close();
		}catch(Exception e) {}
		 */
		if(angles_distances[0][0]==-1) {
			// TODO : move & re-do
			return -1;
		}
		wheels.rotate(angles_distances[0][0]);
		while(wheels.isMoving()) {}
		if(Math.abs(ultrasonSensor.getDetectedDistanceBrut()-angles_distances[0][1]) < ACCEPTED_DISTANCE_ERROR){
			return (int)angles_distances[0][1];
		}
		if(angles_distances[1][0]==-1) {
			// TODO : move & re-do
			return -1;
		}
		wheels.rotate(angles_distances[1][0]);
		while(wheels.isMoving()) {}
		if(Math.abs(ultrasonSensor.getDetectedDistanceBrut()-angles_distances[1][1]) < ACCEPTED_DISTANCE_ERROR){
			return (int)angles_distances[1][1];
		}
		// TODO : move & re-do
		return -1;
	}

	private static final int MIN_JUMP_VALUE = 150; // in millimeter 
	private static final int MAX_PUCK_ANGLE = 16; // Angle en ° maximum de détéction par le robot
	/**
	 * Gives the angle of the 2 closest pucks and their distance from the distance table in parameter
	 * 
	 * @param distances A list of distance measure
	 * @return tab A table of 2 angle and distance of closest puck. Angle of -1 if there is no info
	 */
	private static int MAX_NUMBER_OF_IDX_BTWN_2_JUMPS_FOR_PUCK;
	private int[][] get2ClosestPuckAngleAndDistance(List<Integer> distance) {
		int size = distance.size();
		int dif;
		MAX_NUMBER_OF_IDX_BTWN_2_JUMPS_FOR_PUCK = size*MAX_PUCK_ANGLE/360;
		Map<Integer /* index */, Integer /* jump value */> jumps = new LinkedHashMap<>();
		for(int i = 1; i < size; i++) {
			dif = distance.get(i-1)-distance.get(i);
			if(Math.abs(dif) > MIN_JUMP_VALUE) {
				jumps.put(i, dif);	
			}
		}

		int[][] clotestPuck = new int[][] {{-1,0},{-1,0}};

		int oldIdx = 0; int oldJumpValue = 0;
		for(Map.Entry<Integer, Integer> e : jumps.entrySet()) {
			// Jump avant et Jump arière rapide ( rapide = a moins de MAX_NUMBER_OF_IDX_BTWN_2_JUMPS_FOR_PUCK idx)
			if(oldJumpValue > 0 && e.getValue() < 0 && e.getKey()-oldIdx < MAX_NUMBER_OF_IDX_BTWN_2_JUMPS_FOR_PUCK) {
				// idx du milieu du puck
				int idx = (oldIdx+e.getKey())/2;
				// distance du puck
				int dis = distance.get(idx);
				if(clotestPuck[0][0]==-1) {
					clotestPuck[0][0] = idx;
					clotestPuck[0][1] = dis;
				} else {
					if(clotestPuck[0][1] < dis) {
						if(clotestPuck[1][0] == -1 || clotestPuck[1][1] > dis) {
							clotestPuck[1][0] = idx;
							clotestPuck[1][1] = dis;
						}
					} else {
						if(clotestPuck[1][0] == -1 || clotestPuck[1][1] > clotestPuck[1][0]) {
							clotestPuck[1][0] = clotestPuck[0][0];
							clotestPuck[1][1] = clotestPuck[0][1];
						}
						clotestPuck[0][0] = idx;
						clotestPuck[0][1] = dis;
					}
				}
			}
			oldIdx = e.getKey();
			oldJumpValue = e.getValue();
		}
		/*
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter("info.txt"));
			bw.write("\n\n\nJUMPS : \n");
			for(Map.Entry<Integer, Integer> e : jumps.entrySet()) {
				bw.write("key = "+e.getKey()+" ; val = "+e.getValue()+" ; distance = "+distance.get(e.getKey()));
				bw.newLine();
			}
			bw.close();
		}catch(Exception e) {throw new RuntimeException("PB dans l'écriture");}
		 */
		if(clotestPuck[0][0]!=-1)
			clotestPuck[0][0] = 360*clotestPuck[0][0]/size;
		if(clotestPuck[1][0]!=-1) 
			clotestPuck[1][0] = 360*clotestPuck[1][0]/size;
		return clotestPuck;
	}

	private void hopelessFindPuck() {
		int angle, distance;
		long time, newTime;
		pliers.open();
		first : do {
			// rotates at random angles 
			angle = 35+(int)(Math.random()*20); // 35 < angle < 55
			wheels.rotateRight(angle);
			while(wheels.isMoving());
			position.updateAngle(angle);

			time = System.currentTimeMillis();
			wheels.moveForward(3500); // 3500 = the diagonal distance, (max distance)
			while(wheels.isMoving()) {
				// Update the position
				newTime = System.currentTimeMillis();
				if(newTime-time > 250) {
					position.updateLinear(wheels.getLinearSpeed(), newTime-time);
					time = newTime;
				}

				// Re-rotate if there is an obstacle
				distance = ultrasonSensor.getDetectedDistance();
				if(distance<20) {
					wheels.stop();
					position.updateLinear(wheels.getLinearSpeed(), System.currentTimeMillis()-time);
					continue first;
				}
				if(touchSensor.isPressed()) {
					break first;
				}
			}
		}while(!touchSensor.isPressed());
		wheels.stop();
		position.updateLinear(wheels.getLinearSpeed(), System.currentTimeMillis()-time);
		pliers.close();
	}
}
