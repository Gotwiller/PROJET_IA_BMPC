package Robot.Motor;

import lejos.hardware.motor.Motor;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;

public class CustomWheelsChassis extends WheeledChassis {
	public static final float WHEEL_OFFSET_VALUE = 61.5f;
    
	public CustomWheelsChassis(double wheelDiameter, double TRACK_WIDTH) {
		 super(new Wheel[] {
				 WheeledChassis.modelWheel(Motor.D, wheelDiameter).offset(-WHEEL_OFFSET_VALUE),
				 WheeledChassis.modelWheel(Motor.C, wheelDiameter).offset(WHEEL_OFFSET_VALUE)}, 
				 WheeledChassis.TYPE_DIFFERENTIAL);
	}	

    /**
     * Stops wheels.
     */
    public void stop() {
        super.stop();
    }

    /**
     * Rotates the robot by the specified angle in degrees.
     *
     * @param degrees The angle to rotate the robot.
     */
    public void rotate(double degrees) {
    	super.rotate(degrees);
    }
    /**
     * Rotates the robot to the left by the specified angle in degrees.
     *
     * @param degrees The angle to rotate the robot to the left.
     */
    public void rotateLeft(double degrees) {
    	super.rotate(-degrees);
        
    }
    /**
     * Rotates the robot to the right by the specified angle in degrees.
     *
     * @param degrees The angle to rotate the robot to the right.
     */
    public void rotateRight(double degrees) {
    	 super.rotate(degrees);
    }

    /**
     * Moves the robot backward by the specified distance.
     *
     * @param distance The distance to move the robot backward.
     */
    public void moveBackward(double distance) {
        this.travel(-distance);
    }
    /**
     * Moves the robot forward by the specified distance.
     *
     * @param distance The distance to move the robot forward.
     */
	public void moveForward(double distance) {
		this.travel(distance);
	}
}