package Main;

import lejos.hardware.Brick;
import lejos.hardware.BrickFinder;
import lejos.hardware.motor.Motor;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;

public class Main {

	/*
		Brick brick = BrickFinder.getDefault();
		brick.getPower().getVoltage();

		EV3LargeRegulatedMotor motorR = new EV3LargeRegulatedMotor(brick.getPort("C"));
		EV3LargeRegulatedMotor motorL = new EV3LargeRegulatedMotor(brick.getPort("D"));

		motorR.setSpeed(360);
		motorL.setSpeed(360);

		motorR.forward();
		motorL.forward();

		try {
			Thread.sleep(1000);
		} catch(Exception e) {}

		motorR.stop();
		motorL.stop();

		motorR.close();
		motorL.close();

		EV3LargeRegulatedMotor pilers = new EV3LargeRegulatedMotor(brick.getPort("A"));

		// angle positif = ouvrir ; angle négatif = fermer
		pilers.rotate(360*2);
	 */

	public static void main(String[] args) {
		//Brick brick = BrickFinder.getDefault();
		//EV3ColorSensor colorSensor = new EV3ColorSensor(brick.getPort("1"));
		// TODO : All, good luck guys *u*
	
		//Wheel leftWheel = WheeledChassis.modelWheel(Motor.D, 56).offset(-61.5);
		//Wheel rightWheel = WheeledChassis.modelWheel(Motor.C, 56).offset(61.5);
		//Chassis chassis = new WheeledChassis(new Wheel[]{leftWheel, rightWheel}, WheeledChassis.TYPE_DIFFERENTIAL);
		//chassis.setLinearAcceleration(45);
		//chassis.setLinearSpeed(180);
	
		//chassis.rotate(-360*5);
		//while(chassis.isMoving()) {}
		
		/*
		chassis.travel(1500);
		while(chassis.isMoving()) {}
		chassis.rotate(180);
		while(chassis.isMoving()) {}
		chassis.travel(1500);
		while(chassis.isMoving()) {}
		chassis.rotate(180);
		while(chassis.isMoving()) {}
		*/
	}
}