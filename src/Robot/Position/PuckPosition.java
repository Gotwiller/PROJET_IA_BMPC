package Robot.Position;

public class PuckPosition {

	private static int[][] puckPosition = new int[][] {
		{900,1500},{1500,1500},{2100,1500},
		{900,1000},{1500,1000},{2100,1000},
		{900, 500},{1500, 500},{2100, 500}
	};
	private static boolean[] estEncoreLa = new boolean[] {true,true,true,true,true,true,true,true,true};

	/**
	 * Get the closest puck position. 
	 * 
	 * @param x The x position of the robot
	 * @param y The y position of the robot
	 * @return position The position of the closest puck [ x , y ] or null if no puck is available
	 */
	public static int[] getPuckPosition(double x, double y) {
		if(!availableKnowPuck()) return null;
		int[] distances = new int[puckPosition.length];
		for(int i = 0; i < puckPosition.length; i++) {
			distances[i] = (int)Math.sqrt(Math.pow(puckPosition[i][0]-x, 2)+Math.pow(puckPosition[i][1]-y, 2));
		}
		int distanceMin = 5000;
		int[] position = new int[] {-1,-1};
		for(int i = 0; i < puckPosition.length; i++) {
			if(estEncoreLa[i] && distances[i] < distanceMin) {
				distanceMin = distances[i];
				position = puckPosition[i];
			}
		}
		return position;
	}
	/**
     * Marks a puck as no longer available.
     *
     * @param x The x position of the puck
     * @param y The y position of the puck
     */
	public static void estPlusLa(int x, int y) {
		for(int i = 0; i < puckPosition.length; i++) 
			if(puckPosition[i][0] == x && puckPosition[i][1] == y) {
				estEncoreLa[i] = false;
				break;
			}
	}

	/**
     * Marks a puck as no longer available.
     *
     * @param x The x position of the puck
     * @param y The y position of the puck
     */
	public static void estPlusLa(int[] position) {
		estPlusLa(position[0],position[1]);
	}
	/**
	 * Check if there is a available puck
	 * @return
	 */
	private static boolean availableKnowPuck() {
		for(boolean b : estEncoreLa) if(b) return true;
		return false;
	}
}