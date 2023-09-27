package Robot.Sensor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;

public class ColorSensor extends EV3ColorSensor {

	private static final String[] COLOR_NAMES = new String[] {"RED","GREEN","BLUE","YELLOW","BLACK","GRAY","WHITE"}; 
	private static final Map<String, int[]> COLORS = new HashMap<>();

	private static final String calibratedColorsFileName = "colors.txt";
	private static final int RGB_TOLERANCE = 45;

	/**
	 * Instantiates the RGB color values with those saved in the colors.txt file
	 */
	static {
		File file = new File(calibratedColorsFileName);
		if(!file.exists())
			throw new RuntimeException("Cannot initialize Map<String, int[]> COLORS from ColorSensor because colors.txt file is not found.");

		String[][] rgv_value = new String[7][];
		try{
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			for(int i = 0; (line = br.readLine()) != null; i++)
				rgv_value[i] = line.split(";");
			br.close();
		} catch (Exception e) { e.printStackTrace(); }
		COLORS.put("RED", new int[] {
				Integer.parseInt(rgv_value[0][0]),
				Integer.parseInt(rgv_value[0][1]),
				Integer.parseInt(rgv_value[0][2])
		});
		COLORS.put("GREEN", new int[] {
				Integer.parseInt(rgv_value[1][0]),
				Integer.parseInt(rgv_value[1][1]),
				Integer.parseInt(rgv_value[1][2])
		});
		COLORS.put("BLUE", new int[] {
				Integer.parseInt(rgv_value[2][0]),
				Integer.parseInt(rgv_value[2][1]),
				Integer.parseInt(rgv_value[2][2])
		});
		COLORS.put("YELLOW", new int[] {
				Integer.parseInt(rgv_value[3][0]),
				Integer.parseInt(rgv_value[3][1]),
				Integer.parseInt(rgv_value[3][2])
		});
		COLORS.put("BLACK", new int[] {
				Integer.parseInt(rgv_value[4][0]),
				Integer.parseInt(rgv_value[4][1]),
				Integer.parseInt(rgv_value[4][2])
		});
		COLORS.put("GRAY", new int[] {
				Integer.parseInt(rgv_value[5][0]),
				Integer.parseInt(rgv_value[5][1]),
				Integer.parseInt(rgv_value[5][2])
		});
		COLORS.put("WHITE", new int[] {
				Integer.parseInt(rgv_value[6][0]),
				Integer.parseInt(rgv_value[6][1]),
				Integer.parseInt(rgv_value[6][2])
		});
	}

	public ColorSensor(Port p) {
		super(p);
	}

	/**
	 * Get the name of the color detected.<p>
	 * 
	 * @return RED, GREEN, BLUE, YELLOW, BLACk, GRAY, WHITE, UNKNOWN
	 */
	public String getDetectedColor() {
		float[] rgb = new float[3];
		this.getRGBMode().fetchSample(rgb, 0);

		rgb[0] = (int)(rgb[0]*255);
		rgb[1] = (int)(rgb[1]*255);
		rgb[2] = (int)(rgb[2]*255);

		int[] calibatedColor;

		for(int i = 0; i < COLOR_NAMES.length; i++) {
			calibatedColor = COLORS.get(COLOR_NAMES[i]);
			if	(	calibatedColor[0]-RGB_TOLERANCE < rgb[0] && rgb[0] < calibatedColor[0]+RGB_TOLERANCE &&
					calibatedColor[1]-RGB_TOLERANCE < rgb[1] && rgb[1] < calibatedColor[1]+RGB_TOLERANCE &&
					calibatedColor[2]-RGB_TOLERANCE < rgb[2] && rgb[2] < calibatedColor[2]+RGB_TOLERANCE 	)
				return COLOR_NAMES[i];
		}
		return "UNKNOWN";
	}

	/**
	 * Checks if the color passed as a parameter is the detected color.
	 *
	 * @param colorName The name of the color
	 * @return true if colorName is the detected color, otherwise false.
	 */
	private boolean isDetecte(String colorName) {
		return getDetectedColor().equals(colorName);
	}

	/**
	 * Checks if red color is detected.
	 *
	 * @return true if red color is detected, otherwise false.
	 */
	public boolean isRedDetected() {
		return isDetecte(COLOR_NAMES[0]);
	}
	/**
	 * Checks if green color is detected.
	 *
	 * @return true if green color is detected, otherwise false.
	 */
	public boolean isGreenDetected() {
		return isDetecte(COLOR_NAMES[1]);
	}
	/**
	 * Checks if blue color is detected.
	 *
	 * @return true if blue color is detected, otherwise false.
	 */
	public boolean isBlueDetected() {
		return isDetecte(COLOR_NAMES[2]);
	}
	/**
	 * Checks if yellow color is detected.
	 *
	 * @return true if yellow color is detected, otherwise false.
	 */
	public boolean isYellowDetected() {
		return isDetecte(COLOR_NAMES[3]);
	}
	/**
	 * Checks if black color is detected.
	 *
	 * @return true if black color is detected, otherwise false.
	 */
	public boolean isBlackDetected() {
		return isDetecte(COLOR_NAMES[4]);
	}
	/**
	 * Checks if gray color is detected.
	 *
	 * @return true if gray color is detected, otherwise false.
	 */
	public boolean isGrayDetected() {
		return isDetecte(COLOR_NAMES[5]);
	}
	/**
	 * Checks if white color is detected.
	 *
	 * @return true if white color is detected, otherwise false.
	 */
	public boolean isWhiteDetected() {
		return isDetecte(COLOR_NAMES[6]);
	}
}