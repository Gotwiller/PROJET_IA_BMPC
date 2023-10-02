package Robot;

import java.lang.Thread;


import lejos.hardware.lcd.LCD;
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
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;




public class Robot {
	
	

	private static final int WHEEL_DIAMETER= 56;
	private static final float WHEEL_OFFSET_VALUE = 61.5f;
	
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

        colorSensor = new ColorSensor(SensorPort.S1);	   
        touchSensor = new TouchSensor(SensorPort.S2);		
        ultrasonSensor = new UltrasonSensor(SensorPort.S3); 
        
       
        
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
	

	private void start() {
		
		
	}


   	
        
	}
	