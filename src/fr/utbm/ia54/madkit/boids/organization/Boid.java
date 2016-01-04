package fr.utbm.ia54.madkit.boids.organization;


import java.util.Collection;
import java.util.Map;

import madkit.kernel.AbstractAgent;
import madkit.kernel.AgentAddress;
import madkit.kernel.Message;
import madkit.kernel.ReferenceableAgent;
import fr.utbm.ia54.madkit.boids.Population;
import fr.utbm.ia54.madkit.boids.Settings;
import fr.utbm.ia54.madkit.boids.message.ActionMessage;
import fr.utbm.ia54.madkit.boids.message.PerceptionMessage;
import fr.utbm.ia54.madkit.boids.organization.Environment.PerceivedBoidBody;
import fr.utbm.ia54.madkit.boids.utils.Vector2d;
/**
 * Cette classe représente un agent boid jouant le role boid
 * @author Nicolas Gaud nicolas.gaud@utbm.fr
 * @see OrganizationalSettings
 */
public class Boid extends AbstractAgent implements ReferenceableAgent {

	private static final long serialVersionUID = 5910738485625340995L;

	public static final String BehavioralMethodName = Settings.BoidsBehavioralMethodName;

	/**
	 * L'adresse de l'agent environnement (une grille)
	 */
	private AgentAddress Environment;
	
	/**
	 * La demi largeur de la grille environnementale
	 */
	private int EnvtLargeur;
	
	/**
	 * La demi hauteur de la grille environnementale
	 */
	private int EnvtHauteur;
	
	/**
	 * Le vecteur position.
	 */
	public Vector2d position;
	/**
	 * Le vecteur vitesse
	 */
	public Vector2d vitesse;
	
	/**
	 * La population a laquelle fait partie le Boid
	 */
	public Population groupe;
	
	/**
     * Autorisation de "traverser" les murs
     */
	private  static boolean traversMurs   =  Settings.traversMurs;
	
	/**
     * Distance utilise pour les obstacles
     */
	private  static double distObstacles  =  Settings.distObstacles;
	
	/**
     * Facteur correctif de la force due aux obstacles.
     */
	private  static double obstaclesForce  =    Settings.obstaclesForce;
	
	/**
	 * Costructeur par défait d'un boid
	 *
	 */
	public Boid() {
		position     = new Vector2d();
		vitesse      = new Vector2d();
	}
	
	/**
	 * Initialise un boid
	 * @param envt - l'addresse de l'agent environnement pour savoir à qui envoyer les influences
	 * @param EnvtGrilleHauteur - la hauteur de la grille environnementale
	 * @param EnvtGrilleLargeur - la largeur de la grille environnementale
	 * @param p - la population auquel appartient ce boid
	 * @param initialPosition - sa position initiale dans l'environnement
	 * @param initialVitesse - sa vitesse initiale
	 */
	public void initBoid(AgentAddress envt, int EnvtGrilleHauteur, int EnvtGrilleLargeur, Population p, Vector2d initialPosition, Vector2d initialVitesse) {
		this.setEnvironment(envt);
		this.setEnvtHauteur(EnvtGrilleHauteur);
		this.setEnvtLargeur(EnvtGrilleLargeur);
		this.setPopulation(p);
		
		// initialisation de la position
		position.setXY((Math.random() - 0.5)*EnvtLargeur, (Math.random() - 0.5)*EnvtHauteur);
				
		// initialisation de la vitesse
		vitesse.setXY(Math.random() - 0.5, Math.random() - 0.5	);
	}
	
	@Override
	public void activate() {
		if(Settings.isLogActivated) println("Boid Activate");

		vitesse.normaliser();
		vitesse.fois(0.25);
		vitesse.plus(new Vector2d(0,0.75));
		vitesse.fois(groupe.maxSpeed);
		
		if(!this.isGroup(OrganizationalSettings.boidsAndEnvtAndSchedulingGroupName.getName())) {
			this.createGroup(false, OrganizationalSettings.boidsAndEnvtAndSchedulingGroupName.getName(), "Groupe des boids", null);			
		}			
		this.requestRole(OrganizationalSettings.boidsAndEnvtAndSchedulingGroupName.getName(), OrganizationalSettings.boidRoleName.getName(), null);
	}

	public void live() {
		if(Settings.isLogActivated) println("Boid Live");
		think(perceive());
	}
	
	@Override
	public void end() {
		if(Settings.isLogActivated) println("Boid End");
		
		leaveGroup(OrganizationalSettings.boidsAndEnvtAndSchedulingGroupName.getName());
		System.out.println("End");
		
	}
	
	/**
	 * Cette méthode reçoît la perception de l'environnement et la traite
	 * @return la perception du boid : l'ensemble des boids de la simulation
	 */
	private Collection<PerceivedBoidBody> perceive() {		
		Message m = this.nextMessage();
		if ((m !=null) && (m instanceof PerceptionMessage)) {
			Map<AgentAddress,PerceivedBoidBody> boids = ((PerceptionMessage)m).getOtherBoids();			
			if(Settings.isLogActivated) println("Boid Perception");
			PerceivedBoidBody myboid = boids.get(this.getAddress());
			if ((myboid != null) && (myboid.getAddress().equals(this.getAddress()))){
				//Update les données internes de l'agent en fonction de l'environnement
				this.position = myboid.getPosition();
				this.vitesse = myboid.getVitesse();						
			}
			return boids.values();
		}
		return null;
	}
	
	/**
	 * Un instant de vie du Boid.
	 * Le corps du comportement du boid
	 */
	private void think(Collection<PerceivedBoidBody> perception)
	{
		if (perception != null) {
			Vector2d force;
			Vector2d influence = new Vector2d();;
			
			influence.setZero();
			
			if(groupe.separationOn)
		    {
				force = separation(perception);
			    force.fois(groupe.separationForce);
			    influence.plus(force);
			}
	
			if(groupe.cohesionOn)
			{
				force = cohesion(perception);
			    force.fois(groupe.cohesionForce);
			    influence.plus(force);
			}
			
			if(groupe.alignementOn)
			{
				force = alignement(perception);
			    force.fois(groupe.alignementForce);
			    influence.plus(force);
			}
	
			if(groupe.repulsionOn)
			{
				force = repulsion(perception);
			    force.fois(groupe.repulsionForce);
			    influence.plus(force);
			}
			
			if(!traversMurs)
			{
				force = obstacles();
			    force.fois(Boid.obstaclesForce);
			    influence.plus(force);
			}
	
			// on borne la force appliquee.
			if (influence.length() > groupe.maxForce)
			{
				influence.normaliser();
				influence.fois(groupe.maxForce);
			}
			
			// contribution de la masse.
			influence.fois( 1 / groupe.masse );
			
			//Agir : envoyer l'influence à l'environnement
			act(influence);
		}
	}
	
	/**
	 * Envoie à l'environnement l'influence émise par ce boid
	 * @param force - l'influence émies par le boid
	 */
	public void act(Vector2d force) {
		this.sendMessage(Environment, new ActionMessage(force));
		if(Settings.isLogActivated) println("Boid agit avec une force :"+force.toString());
	}


/*************** Getter and Setter ******************/
	
	public Population getPopulation() {
		return groupe;
	}


	public void setPopulation(Population groupe) {
		this.groupe = groupe;
	}


	public void setEnvtHauteur(int envtHauteur) {
		EnvtHauteur = envtHauteur;
	}


	public void setEnvtLargeur(int envtLargeur) {
		EnvtLargeur = envtLargeur;
	}


	public AgentAddress getEnvironment() {
		return Environment;
	}

	public void setEnvironment(AgentAddress environment) {
		Environment = environment;
	}
	
/************** Boid Methods *****************************/
	
	/**
	 * @param Boid otherBoid : le Boid dont on est peut-etre proche.
	 * @param double distance : la ditance de decision.
	 * @return TRUE si on est proche, FALSE sinon.
	 */
	private boolean proche(PerceivedBoidBody otherBoid, double distance) {
		Vector2d tmp;
		
		tmp = new Vector2d(position);
		tmp.moins(otherBoid.getPosition());
		
		// si on est trop loin tant-pis.
		if ( tmp.length() > distance ) {
			return false;
		}
		return true;
	}
	
	/**
	 * @param Boid otherBoid : le Boid que l'on voit peut-etre.
	 * @param double distance : la ditance de decision.
	 * @return TRUE si on le voit, FALSE sinon.
	 */
	private boolean visible(PerceivedBoidBody otherBoid, double distance) {
		Vector2d tmp;
		Vector2d tmp2;
		
		tmp = new Vector2d(otherBoid.getPosition());
		tmp.moins(position);
		
		// si on est trop loin tand-pis.
		if ( tmp.length() > distance )
			return false;
				
		tmp2 = new Vector2d(vitesse);
		tmp2.normaliser();
		
		// on regarde le produit scalaire...
		if ( tmp2.point(tmp) < groupe.visibleAngleCos)
			return false;
			
		return true;
	}

	/**
	 * @return Vector2d force : retourne la force nécessaire à la separation d'un groupe de boids.
	 */
	private Vector2d separation(Collection<PerceivedBoidBody> otherBoids) {
		Vector2d tmp = new Vector2d();
		Vector2d force = new Vector2d();
		double   len;

		force.setZero();
		for (PerceivedBoidBody otherBoid : otherBoids) {
			if ((otherBoid != null) && (otherBoid.getAddress() != this.getAddress()) 
					&& (visible(otherBoid,groupe.distSeparation))) {
				tmp.setXY(position);
				tmp.moins(otherBoid.getPosition());
				len = tmp.length();
				// force en 1/r
				tmp.fois( 1 / (len*len) );
				force.plus(tmp);
			}
		}
		return force;
	}

	/**
	 * @return Vector2d force : retourne la force nécessaire à la cohésion des boids.
	 */
	private Vector2d cohesion(Collection<PerceivedBoidBody> otherBoids)
	{
		int nbTot = 0;
		Vector2d force = new Vector2d();
		force.setZero();
		for (PerceivedBoidBody otherBoid : otherBoids) {
			if ((otherBoid != null) && (otherBoid.getAddress() != this.getAddress())
					&& (otherBoid.getGroupe() == groupe)&& (visible(otherBoid,groupe.distCohesion)) )
			{
				nbTot++;
				force.plus(otherBoid.getPosition());
			}
		}
		
		// calcul du barycentre...
		if (nbTot > 0)
		{
			force.fois(1 / nbTot);
			force.moins(position);
		}
		return force;
	}
	
	/**
	 * @return Vector2d force : retourne la force nécessaire à l'alignement des boids.
	 */
	private Vector2d alignement(Collection<PerceivedBoidBody> otherBoids)
	{
		int nbTot = 0;
		Vector2d tmp = new Vector2d();
		Vector2d force = new Vector2d();
		force.setZero();
		
		for (PerceivedBoidBody otherBoid : otherBoids) {
			if ((otherBoid != null) && (otherBoid.getAddress() != this.getAddress()) 
					&& (otherBoid.getGroupe() == groupe) && (visible(otherBoid,groupe.distAlignement))) {
				nbTot++;
				tmp.setXY(otherBoid.getVitesse());
				tmp.fois( 1 / tmp.length() );
				force.plus(tmp);
			}
		}
		
		if (nbTot > 0) {
			force.fois( 1 / nbTot );
		}
		return force;
	}
	
	/**
	 * Repulsion entre boids de population différente
	 * @return Vector2d force : retourne la force dûe à la repulsion entre boids
	 */
	private Vector2d repulsion(Collection<PerceivedBoidBody> otherBoids)
	{
		Vector2d force = new Vector2d();
		Vector2d tmp= new Vector2d();
		double   len;
		
		force.setZero();
		for (PerceivedBoidBody otherBoid : otherBoids) {
			if ((otherBoid != null) && (otherBoid.getAddress() != this.getAddress()) && (otherBoid.getGroupe() != groupe) && proche(otherBoid,groupe.distRepulsion)) {
					tmp.setXY(position);
					tmp.moins(otherBoid.getPosition());
					len = tmp.length();
					tmp.fois( 1 / (len*len) );
					force.plus(tmp);
			}
		}
		return force;
	}
		
    /**
     * Ici les obstacle sont materialiser pa les bords de l'environnement
     * On peut donc les determiner gràce a sa largeur et hauteur
     * Mais dans un cas réel il devrait être ajouter à la perception et serait gérer par l'environnement lui même
     * Ceci est une simplification
     * @return Vector2d force : retourne la force nécessaire à l'évitement des obtacles (murs).
     */
    private Vector2d obstacles()
    {
        Vector2d tmp= new Vector2d();
        Vector2d force= new Vector2d();
        force.setZero();

        if (((EnvtLargeur-position.x)<distObstacles) && vitesse.x>0)
        {
            tmp.setXY(-vitesse.x/(EnvtLargeur-position.x),0);
            force.plus(tmp);                    
        }
        if ((position.x<-EnvtLargeur+distObstacles) && vitesse.x<0)
        {
            tmp.setXY(-vitesse.x/(EnvtLargeur+position.x),0);
            force.plus(tmp);                    
        }

        if ((position.y>EnvtHauteur-distObstacles) && vitesse.y>0)
        {
            tmp.setXY(0,-vitesse.y/(EnvtHauteur-position.y));
            force.plus(tmp);                    
        }
    
        if ((position.y<-EnvtHauteur+distObstacles) && vitesse.y<0)
        {
            tmp.setXY(0,-vitesse.y/(EnvtHauteur+position.y));
            force.plus(tmp);                    
        }    

        force.fois(groupe.masse);
        return force;
    }
}
