package Robot.Sensor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.Color;
import lejos.robotics.SampleProvider;

public class ColorSensor extends EV3ColorSensor {
	private EV3ColorSensor colorSensor;
    private SampleProvider rgbMode;
    private float[] sample;
    private Calibration calibration;
	private static final int[] COLOR_NAMES = new int[] {Color.RED,Color.GREEN,Color.BLUE,Color.YELLOW,Color.BLACK,Color.GRAY,Color.WHITE}; 
	private static final Map<Integer, int[]> COLORS = new HashMap<>();

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
		COLORS.put(Color.RED, new int[] {
				Integer.parseInt(rgv_value[0][0]),
				Integer.parseInt(rgv_value[0][1]),
				Integer.parseInt(rgv_value[0][2])
		});
		COLORS.put(Color.GREEN, new int[] {
				Integer.parseInt(rgv_value[1][0]),
				Integer.parseInt(rgv_value[1][1]),
				Integer.parseInt(rgv_value[1][2])
		});
		COLORS.put(Color.BLUE, new int[] {
				Integer.parseInt(rgv_value[2][0]),
				Integer.parseInt(rgv_value[2][1]),
				Integer.parseInt(rgv_value[2][2])
		});
		COLORS.put(Color.YELLOW, new int[] {
				Integer.parseInt(rgv_value[3][0]),
				Integer.parseInt(rgv_value[3][1]),
				Integer.parseInt(rgv_value[3][2])
		});
		COLORS.put(Color.BLACK, new int[] {
				Integer.parseInt(rgv_value[4][0]),
				Integer.parseInt(rgv_value[4][1]),
				Integer.parseInt(rgv_value[4][2])
		});
		COLORS.put(Color.GRAY, new int[] {
				Integer.parseInt(rgv_value[5][0]),
				Integer.parseInt(rgv_value[5][1]),
				Integer.parseInt(rgv_value[5][2])
		});
		COLORS.put(Color.WHITE, new int[] {
				Integer.parseInt(rgv_value[6][0]),
				Integer.parseInt(rgv_value[6][1]),
				Integer.parseInt(rgv_value[6][2])
		});
	}

	private ColorSensor(Port p, EV3ColorSensor colorSensor) {
		super(p);
		this.colorSensor = colorSensor;
        this.rgbMode = colorSensor.getRGBMode();
        this.sample = new float[rgbMode.sampleSize()];
        this.calibration = new Calibration();
	}
	
	/**
	 * Get the name of the color detected.<p>
	 * 
	 * @return RED, GREEN, BLUE, YELLOW, BLACk, GRAY, WHITE value. UNKNOWN = -1 
	 */
	
	
    class Calibration {
	    private float redValue;
	    private float greenValue;
	    private float blueValue;

	    public float getRedValue() {
	        return redValue;
	    }

	    public float getGreenValue() {
	        return greenValue;
	    }

	    public float getBlueValue() {
	        return blueValue;
	    }
	}

	 public void readColor() {
	        // lis et retourne en afficharge les valeurs RGB
	        rgbMode.fetchSample(sample, 0);
	        float redValue = sample[0];
	        float greenValue = sample[1];
	        float blueValue = sample[2];

	        System.out.println("Current RGB - R: " + redValue + " G: " + greenValue + " B: " + blueValue);
	    }

	    public void close() {
	        //arrete le capteur quand tache finit
	        colorSensor.close();
	    }
	    
	    
	    public void calibrateColor() {
	        System.out.println("Demmarage du calibrage, mettez le capteur sur la cible a calibrer");

	        // Capture calibration values
	        rgbMode.fetchSample(sample, 0);
	        float redValue = sample[0];
	        float greenValue = sample[1];
	        float blueValue = sample[2];

	        System.out.println("Calibration faite - R: " + redValue + " G: " + greenValue + " B: " + blueValue);

	        //Enregistrement des valeurs RGB detecter
	        try (BufferedWriter writer = new BufferedWriter(new FileWriter(calibratedColorsFileName))) {
	            writer.write(String.format("%.2f;%.2f;%.2f", redValue, greenValue, blueValue));
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
    
	    

	
	
	public int getDetectedColor() {
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
		return -1;
	}

	/**
	 * Checks if the color passed as a parameter is the detected color.
	 *
	 * @param colorName The name of the color
	 * @return true if colorName is the detected color, otherwise false.
	 */
	private boolean isDetecte(int color) {
		return getDetectedColor() == (color);
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
