package Robot.Sensor;

import java.io.BufferedReader;
import java.io.FileReader;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;

public class ColorSensor extends EV3ColorSensor {

	private static final String RED = "RED";
	private static final int[] RED_RGB;
	private static final String GREEN = "GREEN";
	private static final int[] GREEN_RGB;
	private static final String BLUE = "BLUE";
	private static final int[] BLUE_RGB;
	private static final String YELLOW = "YELLOW";
	private static final int[] YELLOW_RGB;
	private static final String BLACK = "BLACK";
	private static final int[] BLACK_RGB;
	private static final String WHITE = "WHITE";
	private static final int[] WHITE_RGB;

	/**
	 * Instantiates the RGB color values with those saved in the colors.txt file
	 */
	static {
		String[][] rgv_value = new String[6][];
		try{
			BufferedReader br = new BufferedReader(new FileReader("color.txt"));
			String line;
			for(int i = 0; (line = br.readLine()) != null; i++)
				rgv_value[i] = line.split(";");
			br.close();
		} catch (Exception e) { e.printStackTrace(); }
		RED_RGB = new int[] {
				Integer.parseInt(rgv_value[0][0]),
				Integer.parseInt(rgv_value[0][1]),
				Integer.parseInt(rgv_value[0][2])
		};
		GREEN_RGB = new int[] {
				Integer.parseInt(rgv_value[1][0]),
				Integer.parseInt(rgv_value[1][1]),
				Integer.parseInt(rgv_value[1][2])
		};
		BLUE_RGB = new int[] {
				Integer.parseInt(rgv_value[2][0]),
				Integer.parseInt(rgv_value[2][1]),
				Integer.parseInt(rgv_value[2][2])
		};
		YELLOW_RGB = new int[] {
				Integer.parseInt(rgv_value[3][0]),
				Integer.parseInt(rgv_value[3][1]),
				Integer.parseInt(rgv_value[3][2])
		};
		BLACK_RGB = new int[] {
				Integer.parseInt(rgv_value[4][0]),
				Integer.parseInt(rgv_value[4][1]),
				Integer.parseInt(rgv_value[4][2])
		};
		WHITE_RGB = new int[] {
				Integer.parseInt(rgv_value[5][0]),
				Integer.parseInt(rgv_value[5][1]),
				Integer.parseInt(rgv_value[5][2])
		};
	}

	public ColorSensor(Port p) {
		super(p);
	}
}