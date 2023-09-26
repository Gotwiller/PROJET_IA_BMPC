package Robot;

import Robot.Motor.CustomWheelsChassis;
import Robot.Motor.Pliers;
import Robot.Sensor.ColorSensor;
import Robot.Sensor.TouchSensor;
import Robot.Sensor.UltrasonSensor;
import lejos.hardware.Brick;
import lejos.hardware.BrickFinder;
import lejos.hardware.motor.Motor;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;

public class Robot {

	private static final int WHEEL_DIAMETER= 56;
	private static final int WHEEL_OFFSET_VALUE= 56;
	
	private Brick brick;
	
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
        pliers = new Pliers(brick.getPort("A"));

        colorSensor = new ColorSensor(brick.getPort("1"));		// TODO : Put the correct port name
        touchSensor = new TouchSensor(brick.getPort("2"));		// TODO : Put the correct port name
        ultrasonSensor = new UltrasonSensor(brick.getPort("3"));// TODO : Put the correct port name
	}

}