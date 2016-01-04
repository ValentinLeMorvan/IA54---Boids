package fr.utbm.ia54.madkit.boids.organization;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import madkit.kernel.Agent;
import madkit.kernel.AgentAddress;
import madkit.kernel.Message;
import fr.utbm.ia54.madkit.boids.Population;
import fr.utbm.ia54.madkit.boids.Settings;
import fr.utbm.ia54.madkit.boids.gui.EnvironmentGui;
import fr.utbm.ia54.madkit.boids.message.ActionMessage;
import fr.utbm.ia54.madkit.boids.message.PerceptionMessage;
import fr.utbm.ia54.madkit.boids.message.SimulationStepMessage;
import fr.utbm.ia54.madkit.boids.message.SimulationStepMessageACK;
import fr.utbm.ia54.madkit.boids.utils.Vector2d;
/**
 * Cette classe représente l'environnement (agent environnement jouant le role environnement) de la simulation de boid
 * Cet agent est "threadé" et se synchronise par message avec l'autre agent lourd de la simulation : le scheduler
 * @author Nicolas Gaud nicolas.gaud@utbm.fr
 * @see OrganizationalSettings
 * @see BoidScheduler
 */
public class Environment extends Agent {
	
	private static final long serialVersionUID = -3225306000340037206L;

	/**
	 * Un boid tel qu'il est manipuler par l'environnement
	 * Cela représente en quelques sorte le corps de l'agent dans l'environnement
	 * Rappel : un des principes des SMA consiste à ne jamais disposer d'une référence directe sur les agents
	 * on travaille avec les adresses des agents <code>AgentAddress</code>
	 * @author Nicolas Gaud nicolas.gaud@utbm.fr
	 *
	 */
	public class PerceivedBoidBody {
		/**
		 * La position de l'agent
		 */
		private Vector2d position;
		/**
		 * L'adresse de l'agent propriétaire de ce corps
		 */
		private AgentAddress address;
		/**
		 * La vitesse de l'agent
		 */
		private Vector2d vitesse;
		/**
		 * l'accélération
		 */
		private Vector2d acceleration;
		/**
		 * La population auquel appartient l'agent
		 */
		private Population groupe;
		
		public PerceivedBoidBody(Population igroupe,AgentAddress iaddress,Vector2d iposition,Vector2d ivitesse) {
			position = iposition;
			address = iaddress;
			vitesse = ivitesse;
			acceleration = new Vector2d();
			groupe = igroupe;
		}

		public AgentAddress getAddress() { return address; }
		public Vector2d getPosition() { return position; }
		public Vector2d getVitesse() { return vitesse; }
		public Population getGroupe() { return groupe; }
		public Vector2d getAcceleration() { return acceleration; }

		void setAcceleration(Vector2d acceleration) { this.acceleration = acceleration; }
		void setAddress(AgentAddress address) { this.address = address; }
		void setGroupe(Population groupe) { this.groupe = groupe; }
		void setPosition(Vector2d position) { this.position = position; }
		void setVitesse(Vector2d vitesse) { this.vitesse = vitesse; }
	}
	
	/**
	 * La demi-largeur du monde.
	 */
	private  int	 largeur;
	/**
	 * La demi-hauteur du monde.
	 */
	private  int	 hauteur;
	
	/**
	 * Compte le nombre d'agents ayant agit par pas de simulation
	 */
	private int actionsCount;
	
	private EnvironmentGui myGUI;
	
	/**
	 * Double Map associant les population aux boids qu'elles contiennent
	 * et les boids à leur position dans cet environnement
	 */
	private ConcurrentHashMap<AgentAddress,PerceivedBoidBody> boids;
	private ArrayList<Population> populations;
	
	public Environment(int ihauteur, int ilargeur) {
		this.largeur = ilargeur;
		this.hauteur = ihauteur;
		this.boids = new ConcurrentHashMap<AgentAddress,PerceivedBoidBody>();
		this.myGUI = new EnvironmentGui(ihauteur,ilargeur,this.boids,this);
		this.populations = new ArrayList<Population>();
	}
	
	public final void activate()
	{
		if(Settings.isLogActivated) println("Envt Activate");
		
		if(!this.isGroup(OrganizationalSettings.boidsAndEnvtAndSchedulingGroupName.getName())) {
			this.createGroup(false, OrganizationalSettings.boidsAndEnvtAndSchedulingGroupName.getName(), "Groupe des boids", null);			
		}
				
		this.requestRole(OrganizationalSettings.boidsAndEnvtAndSchedulingGroupName.getName(),OrganizationalSettings.environmentRoleName.getName(),null);
		
	}

	
	public void live() {
		if(Settings.isLogActivated) println("Envt Live");
		while(true) {
			actionsCount = 0;
			behavior();
			
			this.myGUI.repaint();
			Thread.yield();
		}
	}

	public final void end()
	{
		if(Settings.isLogActivated)println("Envt End");
		leaveGroup(OrganizationalSettings.boidsAndEnvtAndSchedulingGroupName.getName());
	}
	
	/**
	 * Ajouter un boid à la simulation
	 * @param groupe - la population auxuqel appartient le boid
	 * @param address - l'adresse de l'agent en charge de simuler le comportement du boid
	 * @param position - la position initiale du boid
	 * @param vitesse - la vitesse initiale du boid
	 */
	public void addBoid(Population groupe,AgentAddress address,Vector2d position,Vector2d vitesse) {
		this.boids.put(address,new PerceivedBoidBody(groupe,address,position,vitesse));
	}
	
	/**
	 * Le comportement proprement dit de l'environnement
	 */
	public void behavior() {
		Message m = this.waitNextMessage();
		AgentAddress actor;
		while (m != null) {
			//Il reçoît un message correspondant à une action d'un boid
			if(m instanceof ActionMessage) {
				actor = ((ActionMessage)m).getSender();
				
				if(Settings.isLogActivated)println("Envt validateActions de l'agent"+actor.getName());
				//TODO [TP IA54 Boids] Appliquer la force reçue au boids correspondant Compléter la Synchronization entre l'environnement(Agent lourd) et le scheduler
				if(boids.containsKey(actor)) {
					actionsCount++;
					appliquerForce(((ActionMessage)m).getForce(), boids.get(actor));
				}
				
			//Il reçoît un message de synchronization du scheduler	
			} else if(m instanceof SimulationStepMessage) {
				if(Settings.isLogActivated)println("Envt a recu un SimulationStepMessage, il envoit un ACK");
				//TODO [TP IA54 Boids] Compléter la Synchronization entre l'environnement(Agent lourd) et le scheduler : Envoie ACK
				this.sendMessage(m.getSender(), new SimulationStepMessageACK());
				
				generatePerception();
			}
			
			m = this.nextMessage();
		}
		
		if (actionsCount == boids.size()) {
			if(Settings.isLogActivated)println("Envt : tous les agents ont agit");
		}
		
	}
	
	
	/**
	 * Applique la force calculée <code>force</code> et fait avancer le Boid <code>b</code>.
	 * @param force - la force calculé par le boid <code>b</code> et soumis à l'environnement
	 * @param b - le boid auquel s'applique la force <code>force</code>
	 */
	private void appliquerForce(Vector2d force,PerceivedBoidBody b)
	{
		// on borne la force appliquee.
		if (force.length() > b.getGroupe().maxForce)
		{
			force.normaliser();
			force.fois(b.getGroupe().maxForce);
		}
		
		// contribution de la masse.
		//force.fois( 1 / b.getGroupe().masse );
		
		// mise a jour de l'acceleration et de la vitesse.
		Vector2d acceleration = b.getAcceleration();
		acceleration.setXY(force);
		Vector2d vitesse = b.getVitesse();
		vitesse.plus(acceleration);
		
		// on borne la vitesse.
		if (vitesse.length() > b.getGroupe().maxSpeed)
		{
			vitesse.normaliser();
			vitesse.fois(b.getGroupe().maxSpeed);
		}
		
		// on met a jour la position
		Vector2d position = b.getPosition();
		position.plus(vitesse);
		
		boids.get(b.getAddress()).setAcceleration(acceleration);
		boids.get(b.getAddress()).setVitesse(vitesse);
		boids.get(b.getAddress()).setPosition(position);

		// on s'ajuste en fonction des dimension du Monde.
		ajusteAuMonde(b);
	}
	
    /**
     * Reajuste la position pour etre dans la fenetre de vue du Monde.
     */
	private void ajusteAuMonde(PerceivedBoidBody b)
    {
            double posX;
            double posY;

            posX = b.getPosition().x;
            posY = b.getPosition().y;
            if (Settings.traversMurs)
            {
                    if ( posX > largeur )           posX -= 2 * largeur;
                    if ( posX < ( -1 * largeur ) )  posX += 2 * largeur;
                    if ( posY > hauteur )           posY -= 2 * hauteur;
                    if ( posY < ( -1 * hauteur ) )  posY += 2 * hauteur;
            }
            else
            {
                    if ( posX > largeur )           posX = largeur-0.1;
                    if ( posX < ( -1 * largeur ) )  posX = -largeur+0.1;
                    if ( posY > hauteur )           posY = hauteur-0.1;
                    if ( posY < ( -1 * hauteur ) )  posY = -hauteur+0.1;
            }

            boids.get(b.getAddress()).setPosition(new Vector2d(posX,posY));
    }
	
	
	/**
	 * Envoie les perceptions à tous les boids
	 */
	private void generatePerception() {
		if(Settings.isLogActivated)println("Envt generatePerception");
		this.broadcastMessage(OrganizationalSettings.boidsAndEnvtAndSchedulingGroupName.getName(), 
				OrganizationalSettings.boidRoleName.getName(), new PerceptionMessage(boids));
	}

	public void clickOn(Vector2d position, int i) {
		
		populations.get(i).objective = position;
	}

	public void addPop(Population p) {
			this.populations.add(p);
	}
	
	
}
