package Robot.Motor;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.Motor;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.navigation.MovePilot;

public class CustomWheelsChassis extends WheeledChassis {
	private EV3LargeRegulatedMotor leftMotor;
    private EV3LargeRegulatedMotor rightMotor;
  
    
    


	public CustomWheelsChassis(double wheelDiameter, double TRACK_WIDTH) {
		 super(new Wheel[] {WheeledChassis.modelWheel(Motor.D, wheelDiameter).offset(-70),
				 WheeledChassis.modelWheel(Motor.C, wheelDiameter).offset(70)}, 
				 WheeledChassis.TYPE_DIFFERENTIAL);
		
		 
	        
	        
	       
	}	

	


    public void moveBackward(double distance) {
        this.travel(-distance);
    }

    public void stop() {
        super.stop();
    }

    public void rotateLeft(double degrees) {
    	super.rotate(-degrees);
        
    }

    public void rotateRight(double degrees) {
    	 super.rotate(degrees);
    }

   
	public void moveForward(double distance) {
		this.travel(distance);
		
	}
	
}
