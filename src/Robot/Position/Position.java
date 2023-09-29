package Robot.Position;

public class Position {

	private static final int YELLOW_LINE = 500;
	private static final int BLACK_Y_LINE = 1000;
	private static final int RED_LINE = 1500;

	private static final int LEFT_WHITE_LINE = 300;
	private static final int GREEN_LINE = 900;
	private static final int BLACK_X_LINE = 1500;
	private static final int BLUE_LINE = 2100;
	private static final int RIGHT_WHITE_LINE = 2700;

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
}