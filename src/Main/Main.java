package Main;

import Robot.Robot;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;

public class Main {

	// brick.getPower().getVoltage();

	private static int menu(int choix) {
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
	private static int pliersMenu(int choix) {
		LCD.clear();
		char c0=choix==0?'x':'o',c1=choix==1?'x':'o';
		LCD.drawString("Selectionner un choix :", 0, 0);
		LCD.drawString(c0+" : Ouvre les pinces", 0, 1);
		LCD.drawString(c1+" : Ferme les pinces", 0, 2);
		return Button.waitForAnyPress();
	}
	private static char selectStartingLing() {
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

	private static int nbChoix = 5;
	public static void main(String[] args) {
		// TODO : All, good luck guys *u*

		char side, line;
		int choix = 0, button;
		do {
			button = menu(choix);
			if(button == Button.ID_DOWN) 
				choix = (choix+1)%nbChoix;
			else if (button == Button.ID_UP)
				choix = choix==0?nbChoix-1:choix-1;        
		} while(button != Button.ID_ENTER && button != Button.ID_ESCAPE);

		// Open / Close pliers
		if(choix == 0) {
			do {
				button = pliersMenu(choix);
				if(button == Button.ID_DOWN || button == Button.ID_UP) 
					choix = (choix+1)%2;
			} while(button != Button.ID_ENTER && button != Button.ID_ESCAPE);
			if(button != Button.ID_ENTER) {
				if(choix == 0)
					; // TODO new Robot().closePliers();
				else
					; // TODO new Robot().openPliers();
			}
		} // Do color calibration
		else if(choix == 1) {
			; // TODO new Robot().calibrateColors();
		} // Play Green Side
		else if(choix == 2) {
			char color = selectStartingLing();
			if(color!='x') new Robot().start('g',color);
		} // Play Blue Side
		else if(choix == 3) {
			char color = selectStartingLing();
			if(color!='x') new Robot().start('b',color);
		} // Do some test
		else if(choix == 4) {
			new Robot().test();
		}
	}
}