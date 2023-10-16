
import Robot.Motor.CustomWheelsChassis;
import Robot.Motor.Pliers;
import Robot.Position.Position;
import Robot.Sensor.ColorSensor;
import Robot.Sensor.TouchSensor;
import Robot.Sensor.UltrasonSensor;
import lejos.hardware.Brick;
import lejos.hardware.BrickFinder;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;

public class Robot {

	public static final int WHEEL_DIAMETER= 56;
	public static final float WHEEL_OFFSET_VALUE = 61.5f;
	public static final int ACCEPTED_DISTANCE_ERROR = 50;	// in millimeter
	public static final int MIN_WALL_DISTANCE = 150;		// in millimeter

	private Brick brick;

	private Position position;

	private CustomWheelsChassis wheels;
	private Pliers pliers;

	private ColorSensor colorSensor;
	private TouchSensor touchSensor;
	private UltrasonSensor ultrasonSensor;

	public Robot() {
		brick = BrickFinder.getDefault();
		
		Wheel leftWheel = WheeledChassis.modelWheel(Motor.D, WHEEL_DIAMETER).offset(-WHEEL_OFFSET_VALUE);
		Wheel rightWheel = WheeledChassis.modelWheel(Motor.C, WHEEL_DIAMETER).offset(WHEEL_OFFSET_VALUE);

		wheels = new CustomWheelsChassis(new Wheel[]{leftWheel, rightWheel}, WheeledChassis.TYPE_DIFFERENTIAL);
		pliers = new Pliers(Motor.A);

		colorSensor = new ColorSensor(SensorPort.S1);	   
		touchSensor = new TouchSensor(SensorPort.S3);		
		ultrasonSensor = new UltrasonSensor(SensorPort.S4); 
		//ultrasonSensor.enable();
	}

	public boolean isWhite() {
		ColorSensor cs = new ColorSensor(SensorPort.S3);
		cs.setFloodlight(false);
		if (cs.isWhiteDetected()) {            
			return true;
		}     
		else
			return false;           
	}

	public void start(char side, char line) {
		position = new Position(side, line);
		boolean puckFound;
		int distance = 2500;
		do {
			// Go forward while nothing happen = green part
			puckFound = goFindPuck(distance);
			if(!puckFound) {
				distance = position.getExpectedDistance()-MIN_WALL_DISTANCE+50;
				continue;
			} else { // Suspect detection
				// TODO puck found
			}
			/*// Useless ?
				else { // Full travel
				verifyPosition(); // Check if the position is correct and modify the position if necessary
				rotateForFindPuck();
				distance = position.getExpectedDistance()-MIN_WALL_DISTANCE+50;
				continue;
			 */
		} while(true); // TODO : modify the condition : if we do all the map without find puck (go back home on the white line because it's fun)
	}

	/**
	 * Go forward and return the next action.
	 * 
	 * @param distance Max distance to do
	 * @return if a puck is found
	 */
	private boolean goFindPuck(int distance) {
		long newTime, time = System.currentTimeMillis();
		float[] front = new float[1];
		int detectedDistance, expectedDistance, numberOfSuspectDetection;
		wheels.travel(distance);
		while(wheels.isMoving()) {
			// Update the position if necessary (every 2 seconds)
			newTime = System.currentTimeMillis();
			if(newTime-time > 2000) {
				position.update(wheels.getLinearSpeed(),newTime-time);
				time = newTime;
			}

			// Get the detected distance & expected distance
			detectedDistance = ultrasonSensor.getDetectedDistance();
			expectedDistance = position.getExpectedDistance();

			// White line
			if(colorSensor.isWhiteDetected()) {
				wheels.stop();
				position.update(wheels.getLinearSpeed(),System.currentTimeMillis()-time);
				return false; // Need To Rotate
			} 
			// To close to wall
			if(/* Useless ? detectedDistance > expectedDistance-ACCEPTED_DISTANCE_ERROR &&*/ detectedDistance < MIN_WALL_DISTANCE) {
				wheels.stop();
				position.update(wheels.getLinearSpeed(),System.currentTimeMillis()-time);
				return false; // Need To Rotate
			} 
			// Suspect detection
			if (detectedDistance + ACCEPTED_DISTANCE_ERROR < expectedDistance){
				wheels.stop();
				newTime = System.currentTimeMillis();
				position.update(wheels.getLinearSpeed(),newTime-time);
				int res = suspectDetection();
				numberOfSuspectDetection++;
				if(res == 0 || numberOfSuspectDetection == 4) 
					return false; // Need To Rotate
				else if(res == 2) 
					return true; // Puck, need to rush
				time = System.currentTimeMillis();
				continue; // Nothing, error on the captor / a robot on a frame, continue
			}
		}
		wheels.stop();
		position.update(wheels.getLinearSpeed()*System.currentTimeMillis()-time);
		return false; // Normalement il n'y a pas a aller ici
	}
	/**
	 * Use the UltrasonSensor to determine the nature of the detection.
	 * 
	 * @return
	 *     0 = not a puck, need to rotate (close to wall, robot or angle)
	 *     1 = nothing, continue
	 *     2 = puck
	 */
	private int suspectDetection() {
		int expectedDistance = ultrasonSensor.getDetectedDistance();
		return ultrasonSensor.clarifySuspectDetection(expectedDistance);
	}

	/**
	 * Rotate by a certain angle and update the position.
	 * 
	 * @param angle
	 */
	private void rotate(int angle) {
		// TODO
	}
	/**
	 * Do the best rotation for find a puck.
	 */
	private void rotateForFindPuck() {
		// TODO
	}
	/**
	 * Do the best rotation to back home.
	 * 
	 * @param dodge If it's for dodge a object in front of the robot.
	 */
	private void rotateForBackHome(boolean dodge) { //TO DO : Cas ou on detecte un palais quand on se decale
		int angleRetour = position.calculateAngleToReturnHome();
		rotate(angleRetour);
		position.updateAngle();// update l'angle
		
		while(colorSensor.isWhiteDetected()==false) {
			if(suspectDetection()==0) {	//detection
				goForward(2500); // Creer methode pour calculer distance de la ligne blanche?? ou alors avancer jusqu a detecter la ligne blanche?
				position.updateLinear();}
			
			else if (suspectDetection()==2){
				// eviter par la droite
				if (position.getX()<1000 && (position.getHome()=='g')||position.getX()>1000 && (position.getHome()=='b')) { 
					rotate(45);
					position.updateAngle(); // update l'angle
					goForward(200); //avancer pour se decaler de 20cm
					position.updateLinear();//update x et y 
					rotate(-45);//tourner de 45 degres
					position.updateAngle();
				}

				// eviter par la gauche
				rotate(-45);
				position.updateAngle();// update l'angle
				goForward(200); //avancer pour se decaler de 20cm
				position.updateLinear();//update x et y 
				rotate(45);
				position.updateAngle(); //update l'angle
			}
		}
		
	}
	public void test() {
		pliers.open(true);
		while(pliers.isMoving()) {}
		pliers.close();
	}
	

	public class CustomRoueChassis extends WheeledChassis {

    /**
     * Constructeur for the CustomWheelsChassis class.
     * @param wheels An aray of wheels used for the chassis.
     * @param dim Dimension of the chassis.
     */
    public CustomRoueChassis(Wheel[] wheels, int dim) {
        super(wheels, dim);
    }
   public void ajusteRouesBaseSurCalibrationcouleur(int calibrationData) {
    	
        // algo pour ajuster les roues basées sur la couleur des données de la calibration
        // Cette méthode peut être utilisé pour la précision du manoeuvrage
    }

    public void controlPliersActions(String action) {
    	  // implémentation pour le controle de les actions de la pinces en fonction d'une action spécifique
        // Cette méthode permettra d'activer la precisionde la prise du palet par les pinces
    }

    // Method for adjusting the chassis based on specific environmental conditions
    public void adjustChassisForEnvironment() {
        // logique pour ajuster dynamiquement le chassis en fonction de l'environnement autour 
        // This method allows the robot to navigate efficiently through different terrains
    }

    // Méthode pour vérifier le statut des composants du chassis 
    public void checkChassisStatus() {
        // code pour gérer le status des composants du chassis comme les roues ou les moteurs
        // Cette méthode garantit que le châssis fonctionne correctement pendant la récupération du galet
    }

}
}
