package Robot.Sensor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;

public class ColorSensor extends EV3ColorSensor {

	private static final String[] COLOR_NAMES = new String[] {"RED","GREEN","BLUE","YELLOW","BLACK","WHITE"}; 
	private static final Map<String, int[]> COLORS = new HashMap<>();

	private static final String calibratedColorsFileName = "colors.txt";
	
	/**
	 * Instantiates the RGB color values with those saved in the colors.txt file
	 */
	static {
		String[][] rgv_value = new String[6][];
		try{
			BufferedReader br = new BufferedReader(new FileReader(calibratedColorsFileName));
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
		COLORS.put("WHITE", new int[] {
				Integer.parseInt(rgv_value[5][0]),
				Integer.parseInt(rgv_value[5][1]),
				Integer.parseInt(rgv_value[5][2])
		});
	}

	public ColorSensor(Port p) {
		super(p);
	}
}