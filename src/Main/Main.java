package Main;

import Robot.Robot;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;

public class Main {

	// brick.getPower().getVoltage();
	private static Robot robot;

	private static class Menu {
		private static int nbChoixMain = 5;
		static char[] color;

		/**
		 * Displays the main menu and wait for a user input.
		 *
		 * @return The selected action code.
		 */
		static int mainMenu(int choix) {
			LCD.clear();
			char c0=choix==0?'x':'o',c1=choix==1?'x':'o',c2=choix==2?'x':'o',c3=choix==3?'x':'o',c4=choix==4?'x':'o';
			LCD.drawString("Selectionner un choix :", 0, 0);
			LCD.drawString(c0+" : Ouvrir/Fermer les pinces", 0, 1);
			LCD.drawString(c1+" : Calibrer les couleurs", 0, 2);
			LCD.drawString(c2+" : Cote vert", 0, 3);
			LCD.drawString(c3+" : Cote bleu", 0, 4);
			LCD.drawString(c4+" : Test", 0, 5);
			return Button.waitForAnyPress();
		}
		/**
		 * Displays the plier menu and wait for a user input.
		 *
		 * @return The selected action code.
		 */
		static int pliersMenu(int choix) {
			LCD.clear();
			char c0=choix==0?'x':'o',c1=choix==1?'x':'o';
			LCD.drawString("Selectionner un choix :", 0, 0);
			LCD.drawString(c0+" : Ouvre les pinces", 0, 1);
			LCD.drawString(c1+" : Ferme les pinces", 0, 2);
			return Button.waitForAnyPress();
		}
		/**
		 * Displays the select line menu and wait for a user input.
		 *
		 * @return The selected color on a char.
		 */
		static char selectStartingLing() {
			int choix = 0;
			int button;
			do {
				LCD.clear();
				char c0=choix==0?'x':'o',c1=choix==1?'x':'o',c2=choix==2?'x':'o';
				LCD.drawString("Selectionner un choix :", 0, 0);
				LCD.drawString(c0+" : Lignes Rouge", 0, 1);
				LCD.drawString(c1+" : Lignes Noir", 0, 2);
				LCD.drawString(c2+" : Lignes Jaune", 0, 3);
				button = Button.waitForAnyPress();
				if(button == Button.ID_UP) choix = (choix+2)%3;
				if(button == Button.ID_DOWN) choix = (choix+1)%3;
				if(button == Button.ID_ESCAPE) return 'x';
			} while(button != Button.ID_ENTER);
			if(choix == 0) return 'r';
			if(choix == 1) return 'n';
			if(choix == 2) return 'y';
			return 'x';
		}
		/**
		 * Displays the menu that allows you to move from one menu to another.
		 *
		 * @return The final action code. -2=end, -1=main menu, 0=open plier, 1=close plier, 2=start, 3=test
		 */
		static int menu(){
			int choix = 0, button;
			do {
				button = mainMenu(choix);
				if(choix==0 && button == Button.ID_ESCAPE) {
					return -2;
				}
				if(button == Button.ID_DOWN) 
					choix = (choix+1)%nbChoixMain;
				else if (button == Button.ID_UP)
					choix = choix==0?nbChoixMain-1:choix-1; 
				else if (button == Button.ID_ESCAPE)
					choix = 0;        
			} while(button != Button.ID_ENTER);

			// Open / Close pliers
			if(choix == 0) {
				do {
					button = pliersMenu(choix);
					if(button == Button.ID_DOWN || button == Button.ID_UP) 
						choix = (choix+1)%2;
					else if(button == Button.ID_ESCAPE) {
						if(choix==0)
							return -1;
						else choix=0;
					}
				} while(button != Button.ID_ENTER);
				if(choix == 0)
					return 0;
				else
					return 1;
			} // Do color calibration
			else if(choix == 1) {
				return -1; // TODO new Robot().calibrateColors();
			} // Play Green Side
			else if(choix == 2) {
				char color = selectStartingLing();
				if(color!='x') {
					Menu.color = new char[] {'g',color};
					return 2;
				}
			} // Play Blue Side
			else if(choix == 3) {
				char color = selectStartingLing();
				if(color!='x') {
					Menu.color= new char[] {'b',color};
					return 2;
				}
			} // Do some test
			else if(choix == 4) {
				return 3;
			}
			return -1;
		}
	}
	private static class RobotAction extends Thread {
		public static int action=-1;

		/**
		 * Runs the robot action thread.
		 */
		public void run() {
			LCD.drawString("Action = " + action, 0, 2);
			if (action == 0) {
				robot.getPliers().open(true);
			} else if (action == 1) {
				robot.getPliers().close(true);
			} else if (action == 2) {
				robot.start(Menu.color[0], Menu.color[1]);
			} else if (action == 3) {
				robot.test();
			}
		}
	}

	private static class ForcedStop extends Thread {
		static int button;

		/**
		 * Runs the forced stop thread for stop the robot
		 */
		public void run() {
			while (!isInterrupted()) {
				button = Button.waitForAnyPress();
			}
		}
	}

	public static void main(String[] args) {
		// TODO : All, good luck guys *u*
		robot = new Robot();
		int action;

		do{
			action = Menu.menu(); 
			// Exit : Action = -2
			if(action==-2) return;
			// Main Menu : Action = -1
		} while(action == -1); 

		// A robot action
		RobotAction.action = action;
		RobotAction ra = new RobotAction();
		ForcedStop fs= new ForcedStop();
		ra.start();
		fs.start();

		LCD.clear();
		LCD.drawString("Running ...", 0, 3);

		do {
			Delay.msDelay(1000);
		} while (ra.isAlive() && ForcedStop.button != Button.ID_ESCAPE);
		if(ra.isAlive()) ra.interrupt();
		fs.interrupt();

		LCD.clear();
		if(ForcedStop.button == Button.ID_ESCAPE) LCD.drawString("Escape", 0, 3);
		else LCD.drawString("End", 0, 3);
		Delay.msDelay(3000);
		LCD.clear();
	}
}