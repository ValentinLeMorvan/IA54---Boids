package fr.utbm.ia54.madkit.boids;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.utbm.ia54.madkit.boids.organization.Boid;
import fr.utbm.ia54.madkit.boids.organization.BoidScheduler;
import fr.utbm.ia54.madkit.boids.organization.Environment;
import fr.utbm.ia54.madkit.boids.utils.Vector2d;

import madkit.kernel.Kernel;

/**
 * Classe regroupant l'ensemble des éléments nécessaire à une simulation de boids sous madkit :
 * <ul>
 * <li> le noyau madkit
 * <li> les agents de la simulation : boids, scheduler et l'environnement
 * <li> Ainsi qu'un ensemble de méthodes nécessaire à l'ajout de ces agents à la simulation
 * </ul>
 * @author Nicolas Gaud nicolas.gaud@utbm.fr
 *
 */
public class BoidsSimulation {

	private static final long serialVersionUID = 3199300720615742873L;

	/**
	 * Madkit Kernel
	 */
	private Kernel kernel;
	
	/**
	 * Scheduler
	 */	
	private BoidScheduler scheduler;
		
	/**
	 * L'environnement de la simulation
	 */
	private Environment environment;
		
	/**
	 * La demi-largeur de la grille environnementale.
	 */
	private  int largeur = Settings.EnvtLargeur;
	/**
	 * La demi-hauteur de la grille environnementale.
	 */
	private  int hauteur = Settings.EnvtHauteur;
	
	/**
	 * Les boids et leur population associé qui devront être ajouté à la simulation lorsqu'elle sera lançée
	 */
	private Map<Population,List<Boid>> boidsToLaunch;
	
	/**
	 * Un compteur de boids
	 */
	private int boidsCount;
	
	/**
	 * Booléen précisant si la simulation a déjà démarrée ou pas
	 */
	private boolean isSimulationStarted = false;
	
	public BoidsSimulation() {
		boidsCount = 0;
		
		kernel = new Kernel("Boids Simulation");
		
		environment = new Environment(hauteur,largeur);
		scheduler = new BoidScheduler();
		
		boidsToLaunch = new HashMap<Population,List<Boid>>();
	}
	
	/**
	 * Démarrer la simulation
	 */
	public void start() {
		launchAllAgents();
		isSimulationStarted = true;
	}
	
	/**
	 * Arrêter la simulation
	 */
	public void stop() {
		killAllAgents();
		isSimulationStarted = false;
	}
	
	/**
	 * Ajouter le boid <code>b</code> de la population <code>p</code> à la simulation
	 * @param b - le boid à ajouter
	 * @param p - la population auquel il appartient
	 */
	public void addBoid(Boid b, Population p) {
		boidsCount++;				
		if(!isSimulationStarted){
			List<Boid> currentBoidList = boidsToLaunch.get(p);
			if(currentBoidList != null){
				currentBoidList.add(b);
			} else {
				currentBoidList = new ArrayList<Boid>();
				currentBoidList.add(b);
				boidsToLaunch.put(p, currentBoidList);
			}
		} else {
			launchBoid(b,p,"Boid"+boidsCount);
		}
	}
	
	/**
	 * Lancement de tous les agents de la simulation
	 */
	private void launchAllAgents() {
		kernel.launchAgent(environment, "EnvironmentAgent", null, true);
		kernel.launchAgent(scheduler, "SchedulerAgent", null, false);
		launchAllBoids();
	}
	
	/**
	 * Lancement de tous les boids de la simulation
	 */
	private void launchAllBoids() {
		int boidNum = 0;
		for(Map.Entry<Population,List<Boid>> e : boidsToLaunch.entrySet()) {			
			for(Boid b : e.getValue()) {
				boidNum++;
				launchBoid(b,e.getKey(),"Boid"+boidNum);
			}
		}
	}
	
	/**
	 * Lancement du boid <code>b</code> appartenant à la population <code>p</code> et se nommant <code>boidName</code>
	 * @param b - le boid à lancer
	 * @param p - la population du boid à lancer
	 * @param boidName - le nom du boid à lancer
	 */
	private void launchBoid(Boid b, Population p,String boidName) {
		Vector2d initialPosition = new Vector2d((Math.random() - 0.5)*largeur, (Math.random() - 0.5)*hauteur);
		Vector2d initialVitesse = new Vector2d(Math.random() - 0.5, Math.random() - 0.5);
		
		b.initBoid(environment.getAddress(), hauteur, largeur, p,initialPosition,initialVitesse);
		kernel.launchAgent(b, boidName,null,true);
		environment.addBoid(p, b.getAddress(), initialPosition, initialVitesse);
		if(Settings.isLogActivated) System.out.println("Lancement d'un boid à la position "+initialPosition+" et avec une vitesse de "+initialVitesse );
	}
	
	private void killAllAgents() {
		kernel.killAgent(environment);
		kernel.killAgent(scheduler);
		//Ajouter l'élimination des boids
	}
	

}
