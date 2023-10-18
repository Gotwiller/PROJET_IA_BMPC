package Robot.Position;

import Robot.Robot;

public class Position {

	private static final int YELLOW_LINE = 500;
	private static final int BLACK_Y_LINE = 1000;
	private static final int RED_LINE = 1500;

	private static final int LEFT_WHITE_LINE = 300;
	private static final int GREEN_LINE = 900;
	private static final int BLACK_X_LINE = 1500;
	private static final int BLUE_LINE = 2100;
	private static final int RIGHT_WHITE_LINE = 2700;

	private static final int CAPTER_DISTANCE = 75; // TODO : Meusurer la distance entre le centre de rotation du robot et le capteur ultrason
	
	// Left = g (green) , right = b (blue)
	private char home;

	private int direction;
	private int x;
	private int y;

	public Position(char side, char startingColor) {
		home = side;
		if(side == 'g') {
			x = LEFT_WHITE_LINE;
			direction = 0;
		} else {
			x = RIGHT_WHITE_LINE;
			direction = 180;
		}
		if(startingColor == 'r') 
			y = RED_LINE;
		else if (startingColor == 'y') 
			y = YELLOW_LINE;
		else 
			y = BLACK_Y_LINE;
	}

	/**
	 * Return the value of the angle to do to back at the home.
	 * 
	 * @return positive = left, negative = right
	 */
	public int calculateAngleToReturnHome() {
		if(home == 'g') return 180-direction;
		if(direction > 180) return 360-direction;
		return -direction;
	}

	public int getX() { return x; }
	public int getY() { return y; }
	public int getDirection() { return direction; }
	public char getHome() { return home; }

	/**
	 * Performs a rotation operation on the current direction by the specified angle.
	 * 
	 * @param angle The angle in ° 
	 */
	public void updateAngle(int angle) {
		direction += angle;
		if(direction < 0) direction += 360;
		else direction %= 360;
	}

	/**
	 * Calculates the expected distance from the UltrasonSensor depending on the direction and x y coordinates. The white line is considered as a wall : behind this line there may be pucks that we sould'n go tack
	 * 
	 * @return The expected distance in millimeters.
	 */
	public int getExpectedDistance() {
		double teta_0 = Math.toRadians(direction%90);
		int o,a;
		if(direction < 90) {
			o = 2000-y;
			a = 2700-x;
		} else if(direction < 180) {
			o = x-300;
			a = 2000-y;
		} else if(direction < 270) {
			o = y;
			a = x-300;
		} else {
			o = 2700-x;
			a = y;
		}
		if(teta_0 > Math.atan(o*1.0/a))
			teta_0 = Math.PI/2-teta_0;
		else
			o = a;
		return (int)(o/Math.cos(teta_0))-CAPTER_DISTANCE;
	}

	/**
	 * Updates the position. Works only for straight line travel. The robot must not do rotation. 
	 * 
	 * @param linearSpeed In millimeters per seconds
	 * @param time Time in milliseconds since the last update.
	 */
    public void updateLinear(double linearSpeed, long time) {
        double distance = linearSpeed*time/1000;
        double theta = Math.toRadians(direction);

        x += distance*Math.cos(theta);
        y += distance*Math.sin(theta);
    }
}
