package Main;

import Robot.Robot;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;

public class Main {

	// brick.getPower().getVoltage();

	private static Robot robot;

	private static class Menu extends Thread {
		static char[] color;
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
		static int pliersMenu(int choix) {
			LCD.clear();
			char c0=choix==0?'x':'o',c1=choix==1?'x':'o';
			LCD.drawString("Selectionner un choix :", 0, 0);
			LCD.drawString(c0+" : Ouvre les pinces", 0, 1);
			LCD.drawString(c1+" : Ferme les pinces", 0, 2);
			return Button.waitForAnyPress();
		}
		static char selectStartingLing() {
			int choix = 0;
			int button;
			do {
				LCD.clear();
				char c0=choix==0?'x':'o',c1=choix==1?'x':'o',c2=choix==2?'x':'o';
				LCD.drawString("Selectionner un choix :", 0, 0);
				LCD.drawString(c0+" : Lignes Rouge", 0, 1);
				LCD.drawString(c1+" : Lignes Noir", 0, 2);
				LCD.drawString(c2+" : Lignes Jaune", 0, 2);
				button = Button.waitForAnyPress();
				if(button == Button.ID_UP) choix = (choix+1)%3;
				if(button == Button.ID_DOWN) choix = (choix+2)%3;
			} while(button != Button.ID_ENTER && button != Button.ID_ESCAPE);
			if(choix == 0) return 'r';
			if(choix == 0) return 'n';
			if(choix == 0) return 'y';
			return 'x';
		}
		static int menu(){
			int choix = 0, button;
			do {
				button = mainMenu(choix);
				if(choix==0 && button == Button.ID_ESCAPE) {
					return -2;
				}
				if(button == Button.ID_DOWN) 
					choix = (choix+1)%nbChoix;
				else if (button == Button.ID_UP)
					choix = choix==0?nbChoix-1:choix-1; 
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
						if(choix==0) {
							return -1;
						} else choix=0;
					}
				} while(button != Button.ID_ENTER);
				if(button != Button.ID_ENTER) {
					if(choix == 0) {
						//new Robot().getPliers().close(true);
						return 0;
					}
					else {
						//new Robot().getPliers().open(true);
						return 1;
					}
				}
			} // Do color calibration
			else if(choix == 1) {
				; // TODO new Robot().calibrateColors();
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
		public static int action;
		public void run() {
			LCD.drawString("Action = "+action, 0, 2);
			if(action == 0) {
				robot.test();
				//robot.getPliers().close(true);
			}else if(action == 1) {
				robot.test();
				//robot.getPliers().open(true);
			}else if(action == 2) {
				robot.test();
				//robot.start(Menu.color[0], Menu.color[1]);
			}else if(action == 3) {
				robot.test();
			}
		}
	}
	private static class ForcedStop extends Thread {
		static int button;
		public void run() {
			button = -1;
			while(true)	button = Button.waitForAnyPress();
		}
	}

	private static int nbChoix = 5;
	public static void main(String[] args) {
		// TODO : All, good luck guys *u*
		robot = new Robot();
		int action;

		do{
			action = Menu.menu(); 
			// Exit : Action = -2
			if(action==-2) break;
			// Main Menu : Action = -1
			if(action == -1) continue;

			// A robot action
			RobotAction.action = action;
			RobotAction ra = new RobotAction();
			ra.start();
			ForcedStop fs= new ForcedStop();
			fs.start();
			do {
				LCD.clear();
				LCD.drawString("Is runing ? "+ra.isAlive(), 0, 1);
				LCD.drawString("Last Press : "+ ForcedStop.button, 0, 2);
				LCD.drawString("Escape int : "+Button.ID_ESCAPE, 0, 3);
				Delay.msDelay(1000);
			} while (ra.isAlive() && ForcedStop.button != Button.ID_ESCAPE);
			if(ra.isAlive()) {
				ra.interrupt();
				LCD.clear();
				LCD.drawString("Escape", 0, 3);
				Delay.msDelay(3000);
			} else {
				LCD.clear();
				LCD.drawString("End", 0, 3);
				Delay.msDelay(3000);
			}
			LCD.clear();
		} while(true); 
	}
}