package Robot.Position;

public class Position {

	private static final int TABLE_LENGTH = 3000;
	public int getTableLength() {
		return TABLE_LENGTH;
	}
	private static final int TABLE_WIDTH = 2000;

	private static final int YELLOW_LINE = 500;
	private static final int BLACK_Y_LINE = 1000;
	private static final int RED_LINE = 1500;

	private static final int LEFT_WHITE_LINE = 300;
	private static final int GREEN_LINE = 900;
	private static final int BLACK_X_LINE = 1500;
	private static final int BLUE_LINE = 2100;
	private static final int RIGHT_WHITE_LINE = 2700;

	private static final int CAPTER_DISTANCE = 140; 
	public int getCapterDistance() {
		return CAPTER_DISTANCE;
	}

	// Left = g (green) , right = b (blue)
	private char goal;

	private double x;
	private double y;
	private double orientation;

	/**
	 * Constructor for initializing the position based on the starting side and color line of the robot.
	 * 
	 * @param side The starting side of the robot ('g' for left, 'b' for right).
	 * @param startingColor The starting color of the robot ('r' for red, 'y' for yellow, 'b' for black).
	 */
	public Position(char side, char startingColor) {
		goal = side=='b'?'g':'b';
		if(side == 'g') {
			x = LEFT_WHITE_LINE;
			orientation = 0;
		} else {
			x = RIGHT_WHITE_LINE;
			orientation = 180;
		}
		if(startingColor == 'r') 
			y = RED_LINE;
		else if (startingColor == 'y') 
			y = YELLOW_LINE;
		else 
			y = BLACK_Y_LINE;
	}

	/**
	 * Constructor for initializing the position based on given coordinates and orientation.
	 * 
	 * @param x The x-coordinate.
	 * @param y The y-coordinate.
	 * @param orientation The orientation angle in degrees.
	 */
	public Position(double x, double y, double orientation) {
		this.x = x;
		this.y = y;
		this.orientation = orientation;
		normalizeOrientation();
	}

	/**
	 * Normalizes an angle to be within the range 0 - 360.
	 */
	private void normalizeOrientation() {
		while (orientation < 0) {
			orientation += 360;
		}
		while (orientation >= 360) {
			orientation -= 360;
		}
	}

	/**
	 * Return the value of the angle to do to back at the home.
	 * 
	 * @return positive = left, negative = right
	 */
	public double calculateAngleToGoal() {
		if(goal == 'g') return 180-orientation;
		if(orientation > 180) return 360-orientation;
		return -orientation;
	}

	/**
	 * Gets the x-coordinate of the position.
	 * 
	 * @return The x-coordinate.
	 */
	public double getX() { 
		return x; 
	}
	/**
	 * Sets the x-coordinate of the position.
	 * 
	 * @param x The new x-coordinate.
	 */
	public void setX(double x) {
		this.x = x;
	}
	/**
	 * Gets the y-coordinate of the position.
	 * 
	 * @return The y-coordinate.
	 */
	public double getY() { 
		return y; 
	}
	/**
	 * Sets the y-coordinate of the position.
	 * 
	 * @param y The new y-coordinate.
	 */
	public void setY(double y) {
		this.y = y;
	}
	/**
	 * Gets the orientation angle in degrees.
	 * 
	 * @return The orientation angle.
	 */
	public double getOrientation() { 
		return orientation; 
	}
	/**
	 * Sets the orientation angle in degrees.
	 * 
	 * @param orientation The new orientation angle.
	 */
	public void setOrientation(double orientation) {
		this.orientation = orientation;
		normalizeOrientation();
	}
	/**
	 * Gets the goal side of the robot.
	 * 
	 * @return The goal side ('g' for left, 'b' for right).
	 */
	public char getGoal() { 
		return goal; 
	}

	/**
	 * Performs a rotation operation on the current direction by the specified angle.
	 * 
	 * @param angle The angle in ° 
	 */
	public void updateAngle(double angle) {
		orientation += angle;
		normalizeOrientation();
		System.out.println("("+(int)x+","+(int)y+") "+orientation+"°");
	}
	/**
	 * Updates the position. Works only for straight line travel. The robot must not do rotation. 
	 * 
	 * @param accelerationSpeed In millimeters per seconds pow 2
	 * @param linearSpeed In millimeters per seconds
	 * @param time Time in milliseconds since the last update.
	 */
	public void updateLinear(double accelerationSpeed, double linearSpeed, long time) {
		double timeS = time/1000.0;
		double tempsDAcceleration = linearSpeed/accelerationSpeed;
		double tempsVitesseMax = timeS-tempsDAcceleration;
		if(tempsDAcceleration<time) tempsVitesseMax = 0;

		double distance = 0.5 * linearSpeed * Math.pow(tempsDAcceleration, 2) + linearSpeed * tempsVitesseMax;
		double theta = Math.toRadians(orientation);

		x += distance*Math.cos(theta);
		y += distance*Math.sin(theta);
		System.out.println("FT : ("+(int)x+","+(int)y+") "+orientation+"° "+(int)(distance*Math.cos(theta))+";"+(int)(distance*Math.sin(theta))+" ; "+distance);
	}
	public void removeSurplusAcceleration(double accelerationSpeed, double linearSpeed) {
		double time = linearSpeed/accelerationSpeed;
		double distance = 0.5 * linearSpeed * Math.pow(time, 2);
		double theta = Math.toRadians(orientation);

		x -= distance*Math.cos(theta);
		y -= distance*Math.sin(theta);
		System.out.println("RS : ("+(int)x+","+(int)y+") "+orientation+"° "+(int)(distance*Math.cos(theta))+";"+(int)(distance*Math.sin(theta))+" ; "+distance);
	}

	/**
	 * Updates the position. Works only for straight line travel. The robot must not do rotation. 
	 * 
	 * @param linearSpeed In millimeters per seconds
	 * @param time Time in milliseconds since the last update.
	 */
	public void updateLinear(double linearSpeed, long time) {
		double distance = linearSpeed*time/1000;
		double theta = Math.toRadians(orientation);

		x += distance*Math.cos(theta);
		y += distance*Math.sin(theta);
		System.out.println("PF : ("+(int)x+","+(int)y+") "+orientation+"° "+(int)(distance*Math.cos(theta))+";"+(int)(distance*Math.sin(theta)));
	}

	/**
	 * Moves the robot by a given distance in the current direction.
	 * 
	 * @param distance The distance to move in millimeters.
	 */
	public void move(double distance) {
		// Update the position based on the current heading and the given distance
		double radianOrientation = Math.toRadians(orientation);
		x += distance * Math.cos(radianOrientation);
		y += distance * Math.sin(radianOrientation);
		System.out.println("MO : ("+(int)x+","+(int)y+") "+orientation+"° "+(int)(distance*Math.cos(radianOrientation))+";"+(int)(distance*Math.sin(radianOrientation)));
	}
	/**		System.out.println("("+(int)x+","+(int)y+") "+orientation+"°");

	 * Rotates the robot's heading by the specified angle.
	 * 
	 * @param angle The angle to rotate in degrees.
	 */
	public void rotate(double angle) {
		// Rotate the heading by the given angle
		orientation += angle;
		normalizeOrientation();
		System.out.println("("+(int)x+","+(int)y+") "+orientation+"°");
	}

	/**
	 * Calculates the expected distance from the UltrasonSensor depending on the direction and x y coordinates. The white line is considered as a wall : behind this line there may be pucks that we sould'n go tack
	 * 
	 * @return The expected distance in millimeters.
	 */
	public int getExpectedDistance() {
		double teta_0 = Math.toRadians(orientation%90);
		double o,a;
		if(orientation < 90) {
			o = TABLE_WIDTH-y;
			a = TABLE_LENGTH-x;
		} else if(orientation < 180) {
			o = x;
			a = TABLE_WIDTH-y;
		} else if(orientation < 270) {
			o = y;
			a = x;
		} else {
			o = TABLE_LENGTH-x;
			a = y;
		}
		if(teta_0 > Math.atan(o*1.0/a))
			teta_0 = Math.PI/2-teta_0;
		else
			o = a;
		return (int)(o/Math.cos(teta_0))-CAPTER_DISTANCE;
	}
	public void majWhiteLine() {
		x = goal=='g'?(LEFT_WHITE_LINE+200):(RIGHT_WHITE_LINE-200);
	}
}