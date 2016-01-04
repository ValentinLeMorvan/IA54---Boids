package fr.utbm.ia54.madkit.boids;
import java.awt.Color;

import fr.utbm.ia54.madkit.boids.organization.Boid;
import fr.utbm.ia54.madkit.boids.utils.Vector2d;


/**
 * 
 * Cette simulation est inspiré d'une applet de simulation de boids réalisée dans le cadre de 
 * l'ouverture 12 Systèmes Multi-Agents de le 3ème année à l'ENST Bretagne et qui a été écrite par :
 * <ul>
 * <li> Gildas Cadin pour la partie comportement des Boids. 
 * <li> Lionel Deglise pour la partie interface. 
 * <li> Pierre Thebaud pour la partie évitement d'obstacle (gestion des murs).
 * </ul>
 * 
 * <p>
 * Elle a été modifié et adapté au principe OCMAS puis implementé sous MadKit
 * Cette classe est uniquement utilisé pour le lancement de la simulation.
 * Ainsi que la création et l'ajout des populations de boids
 * 
 * @author Nicolas Gaud nicolas.gaud@utbm.fr
 *
 */
public class BoidsSimulationLauncher {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		//3 populations de boids sont créées (Rouge,Verte et Bleu)
		BoidsSimulation simu = new BoidsSimulation();
		Population pRed = new Population(Color.RED);
		Population pGreen = new Population(Color.GREEN);
		Population pBlue = new Population(Color.BLUE);
		
		//Initialisation de la population de boid pRed : 20 boids
		for(int i =0; i<20;i++){
			simu.addBoid(new Boid(), pRed);
		}
		
		pRed.setObjective(new Vector2d(0,0));
		
		//Initialisation de la population de boid pGreen : 20 boids
		for(int i =0; i<20;i++){
			simu.addBoid(new Boid(), pGreen);
		}
		//Initialisation de la population de boid pBlue : 20 boids		
		
		for(int i =0; i<20;i++){
			simu.addBoid(new Boid(), pBlue);
		}

		pBlue.setObjective(new Vector2d(0,0));
		
		simu.start();

	}

}
