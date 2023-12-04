package Robot;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import Robot.Motor.CustomWheelsChassis;
import Robot.Motor.Pliers;
import Robot.Position.Position;
import Robot.Position.PuckPosition;
import Robot.Sensor.ColorSensor;
import Robot.Sensor.TouchSensor;
import Robot.Sensor.UltrasonSensor;
import lejos.hardware.Brick;
import lejos.hardware.BrickFinder;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.SensorPort;
import lejos.robotics.chassis.WheeledChassis;
import lejos.utility.Delay;

public class Robot {

	public static final int TIME_BETWEEN_POSITION_UPDATES = 500; //in ms

	public static final int WHEEL_DIAMETER= 56;
	public static final float WHEEL_OFFSET_VALUE = 61.5f;
	public static final int ACCEPTED_DISTANCE_ERROR = 80;	// en millimetre
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
		wheels.setAngularSpeed(90);
		pliers = new Pliers(Motor.A);
		
		colorSensor = new ColorSensor(SensorPort.S1);	   
		touchSensor = new TouchSensor(SensorPort.S3);		
		ultrasonSensor = new UltrasonSensor(SensorPort.S4); 
	}

	public void start(char side, char line) {
		position = new Position(side, line);
		firstpuck();

		while(!Thread.currentThread().isInterrupted()) {
			int distance = lookClosestPuckAndGetDistance();
			if(distance < 0) break;

			int realDistance = lookCenterOfPuck()+position.getCapterDistance();

			if(!catchPuck(realDistance)) continue;

			rotateToGoGoal();
			goToGoal();
			dropPuck();
		}
		while(!Thread.currentThread().isInterrupted()) {
			hopelessFindPuck();
			rotateToGoGoal();
			goToGoal();
			dropPuck();
		}
	}

	private int lookCenterOfPuck() {
		wheels.rotate(15);
		while(wheels.isMoving());
		position.rotate(15);
		List<Integer> distances = new ArrayList<Integer>();
		wheels.rotate(-30);
		while(wheels.isMoving()) {
			distances.add(ultrasonSensor.getDetectedDistance());
			Delay.msDelay(4);
		} 
		position.rotate(-30);
		if(distances.size() == 0) return ultrasonSensor.getDetectedDistance();
		int minValueIdx = 0, minDistance = distances.get(minValueIdx);
		for(int i = 1; i < distances.size(); i++) {
			if(distances.get(i)<minDistance) {
				minDistance = distances.get(i);
				minValueIdx = i;
			}
		}

		// Look the center of the same distance for a better middle aim
		int a=minValueIdx,b=minValueIdx;
		for(; a > 0 && distances.get(a) == minDistance; a--);
		for(; b > distances.size() && distances.get(a) == minDistance; b++);
		minValueIdx = (a+b)/2;

		int angle = 30-30*minValueIdx/distances.size();
		wheels.rotateLeft(angle);
		while(wheels.isMoving());
		position.updateAngle(angle);
		System.out.println("Real Dis avant add : "+minDistance);
		return minDistance;
	}

	// Test 

	public void test() {
		wheels.rotate(360*6);
		while(wheels.isMoving());
	}

	// Find pucks

	private int lookClosestPuckAndGetDistance() {
		int[] puckPosition = PuckPosition.getPuckPosition(position.getX(),position.getY());
		if(puckPosition==null) return -1;

		PuckPosition.estPlusLa(puckPosition); // Il n'y sera plus en tout cas

		int distance = (int)Math.sqrt(Math.pow(puckPosition[0]-position.getX(), 2)+Math.pow(puckPosition[1]-position.getY(), 2));
		double angle = Math.toDegrees(Math.atan2(puckPosition[1] - position.getY(),puckPosition[0]- position.getX()));

		wheels.rotate(angle-position.getOrientation());
		while(wheels.isMoving());
		position.updateAngle(angle-position.getOrientation());

		LCD.clear();
		LCD.drawString("PP : "+puckPosition[0]+","+puckPosition[1],0,0);
		LCD.drawString("RP : "+(int)position.getX()+","+(int)position.getY(),0,1);
		LCD.drawString("DI : "+distance,0,2);
		LCD.drawString("AN : "+angle,0,3);
		Delay.msDelay(1000);

		System.out.println("==========");
		System.out.println(position.getX()+","+position.getY()+" - "+position.getOrientation());
		System.out.println("PP : "+puckPosition[0]+","+puckPosition[1]);
		System.out.println("RP : "+(int)position.getX()+","+(int)position.getY());
		System.out.println("DI : "+distance);
		System.out.println("AN : "+angle);

		return distance;
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
			position.updateAngle(-angle);

			time = System.currentTimeMillis();
			wheels.moveForward(3500); // 3500 = the diagonal distance, (max distance)
			while(wheels.isMoving()) {
				// Update the position
				newTime = System.currentTimeMillis();
				if(newTime-time > TIME_BETWEEN_POSITION_UPDATES) {
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
			position.removeSurplusAcceleration(wheels.getLinearAcceleration(), wheels.getLinearSpeed());
		}while(!touchSensor.isPressed());
		wheels.stop();
		position.updateLinear(wheels.getLinearSpeed(), System.currentTimeMillis()-time);
		pliers.close();
	}

	// Catch the puck

	public boolean catchPuck(double distance) {
		pliers.open();
		if(allerVersPuck(distance)) {
			pliers.close();
			return true;
		} else {
			pliers.close();
			return false;
		}
	}
	public boolean allerVersPuck(double distance) {
		distance+=50; // 5cm for safety
		long time = System.currentTimeMillis();
		wheels.moveForward(distance);
		while(wheels.isMoving()) {
			if(touchSensor.isPressed()) {
				wheels.stop();
				position.updateLinear(wheels.getLinearSpeed(), System.currentTimeMillis() - time);
				return true;
			}
		}
		position.move(distance);
		return false;
	}

	// Go to the goal

	private void rotateToGoGoal() {
		double angle = position.calculateAngleToGoal();
		wheels.rotate(angle);
		while(wheels.isMoving());
		position.updateAngle(angle);
	}
	private void goToGoal() {
		long time = System.currentTimeMillis(),newTime;
		wheels.travel(3000);
		while (wheels.isMoving() && !colorSensor.isWhiteDetected()) {
			// regularly updates the position
			newTime = System.currentTimeMillis();
			if(newTime-time>TIME_BETWEEN_POSITION_UPDATES) {
				position.updateLinear(wheels.getLinearSpeed(), newTime-time);
				time = newTime;
			}

			// Dodge if there is an obstacle
			if (ultrasonSensor.getDetectedDistance() < 200) {
				wheels.stop();
				position.updateLinear(wheels.getLinearSpeed(),System.currentTimeMillis()-time);
				position.removeSurplusAcceleration(wheels.getLinearAcceleration(), wheels.getLinearSpeed());
				if(!avoid()) break;
				wheels.travel(3000);
				time = System.currentTimeMillis();
			}
		}
		wheels.stop();
		position.updateLinear(wheels.getLinearSpeed(),System.currentTimeMillis()-time);
		position.majWhiteLine();
		//position.removeSurplusAcceleration(wheels.getLinearAcceleration(), wheels.getLinearSpeed());
	}
	public boolean avoid () {
		return avoidRight() || avoidLeft();
	}
	private boolean avoidRight() {
		wheels.rotateRight(90);
		while (wheels.isMoving());
		position.updateAngle(-90);
		if(ultrasonSensor.getDetectedDistance() < 250) {
			wheels.rotateLeft(90);
			while (wheels.isMoving());
			position.updateAngle(90);
			return false;
		}
		wheels.travel(300);
		while (wheels.isMoving());
		position.move(300);
		wheels.rotateLeft(90);
		while (wheels.isMoving());
		position.updateAngle(90);
		return true;
	}
	private boolean avoidLeft() {
		wheels.rotateLeft(90);
		while (wheels.isMoving());
		position.updateAngle(90);
		if(ultrasonSensor.getDetectedDistance() < 250) {
			wheels.rotateRight(90);
			while (wheels.isMoving());
			position.updateAngle(-90);
			return false;
		}
		wheels.travel(300);
		while (wheels.isMoving());
		position.move(300);
		wheels.rotateRight(90);
		while (wheels.isMoving());
		position.updateAngle(-90);
		return true;
	}

	// Drop the puck

	private void dropPuck() {
		pliers.open();
		wheels.travel(-100);
		while (wheels.isMoving());
		pliers.close();
		wheels.rotate(180);
		while(wheels.isMoving());
		position.updateAngle(180);
	}

	// First puck

	public void firstpuck() {
		// Catch the puck in front of us
		catchPuck(600);

		// moves towards the center for dodge puck
		wheels.rotateRight(45);
		while (wheels.isMoving());
		position.updateAngle(-45);
		wheels.travel(300);
		while (wheels.isMoving());
		position.move(300);
		wheels.rotateLeft(45);
		while (wheels.isMoving());
		position.updateAngle(45);

		// travel to the goal and dodge obstacles
		goToGoal();
		// Drop the puck
		dropPuck();
	}

	// Dead Code

	/**
	 * Go forward and return the next action.
	 * 
	 * @param distance Max distance to do
	 * @return if a puck is found
	 */
	private boolean findPuck1(int distance) {
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

	/**
	 * Do the best rotation to back home.
	 * 
	 * @param dodge If it's for dodge a object in front of the robot.
	 */	
	private void rotateForBackHome() {
		long newTime, time = System.currentTimeMillis();
		int angleRetour = (int)position.calculateAngleToGoal();
		int cote; // -1 si plus proche du mur gauche / 1 si plus proche du mur droit
		wheels.rotate(angleRetour); 
		position.updateAngle(angleRetour);// update l'angle
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
				if (position.getX()<MILLIEU_TERRAIN && (position.getGoal()=='g')||position.getX()>MILLIEU_TERRAIN && (position.getGoal()=='b')) 
					cote = GAUCHE; // Eviter par la droite
				else cote= DROITE; // Eviter par la gauche
				wheels.rotate(MOINS_ANGLE_45*cote); while(wheels.isMoving()); position.updateAngle(MOINS_ANGLE_45*cote);
				// Cas ou le robot tourne de 45degres ET palais dans le champ
				while (suspectDetection()==2) { 
					wheels.rotate(ANGLE_45*cote); while(wheels.isMoving()); position.updateAngle(ANGLE_45*cote);
					wheels.travel(100); while(wheels.isMoving());
					wheels.stop(); position.updateLinear(wheels.getLinearSpeed(),System.currentTimeMillis()-time);
					wheels.rotate(MOINS_ANGLE_45*cote); while(wheels.isMoving()); position.updateAngle(MOINS_ANGLE_45*cote);
				}
				wheels.travel(DISTANCE); while (wheels.isMoving());
				wheels.stop(); position.updateLinear(wheels.getLinearSpeed(),System.currentTimeMillis()-time);
				wheels.rotate(ANGLE_45*cote);while (wheels.isMoving()); position.updateAngle(ANGLE_45*cote);
			}
		}
		wheels.stop(); position.updateLinear(wheels.getLinearSpeed(),System.currentTimeMillis()-time);
	}
}