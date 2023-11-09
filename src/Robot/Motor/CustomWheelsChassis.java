package Robot.Motor;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.navigation.MovePilot;

public class CustomWheelsChassis extends WheeledChassis {
	private EV3LargeRegulatedMotor leftMotor;
    private EV3LargeRegulatedMotor rightMotor;
    private MovePilot MovePilot;

	public CustomWheelsChassis(Wheel[] wheels, int dim) {
		super(wheels, dim);
	}

    public void moveForward() {
		MovePilot.forward();
    }

    public void moveBackward() {
        MovePilot.backward();
    }

    public void stop() {
        MovePilot.stop();
    }

    public void turnLeft() {
        MovePilot.rotateLeft();
    }

    public void turnRight() {
        MovePilot.rotateRight();
    }

    public void close() {
        leftMotor.close();
        rightMotor.close();
    }

	public void moveForward(double distance) {
		MovePilot.forward();
		
	}
}
