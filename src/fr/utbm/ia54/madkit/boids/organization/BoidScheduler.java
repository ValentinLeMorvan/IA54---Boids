package fr.utbm.ia54.madkit.boids.organization;

import madkit.kernel.Message;
import madkit.kernel.ReferenceableAgent;
import madkit.kernel.Scheduler;
import madkit.simulation.activators.TurboMethodActivator;
import fr.utbm.ia54.madkit.boids.Settings;
import fr.utbm.ia54.madkit.boids.message.SimulationStepMessage;
import fr.utbm.ia54.madkit.boids.message.SimulationStepMessageACK;

/**
 * L'agent scheduler jouant le rôle Scheduler
 * Il est en charge de l'ordonnancement des boids
 * Cet agent est "threadé" et se synchronise par message avec l'autre agent lourd de la simulation : l'environnement
 * 
 * @author Nicolas Gaud nicolas.gaud@utbm.fr
 * @see OrganizationalSettings
 */
public class BoidScheduler extends Scheduler implements ReferenceableAgent {

	private static final long serialVersionUID = 5002886091290786406L;

	/**
	 * L'activateur utilise pour scheduler les boids
	 * Politique d'ordonnancement des boids
	 */
	private TurboMethodActivator boidsActivator;
	
	/**
	 * Le temps d'attente entre deux pas de simulation.
	 */
	private int delay;
	
	/**
	 * Le nombre de pas de simulation dejà effectué.
	 */
	private int Iteration;
	
	/**
	 * le constructeur par  défaut
	 */
	public BoidScheduler() {
		Iteration = 0; //artifice car increment iteration est lance en debut de cycle
		delay = 0;
	}
	
	public final void activate() {
		if(Settings.isLogActivated) println("Scheduler Activate");
		
		if(this.isGroup(OrganizationalSettings.boidsAndEnvtAndSchedulingGroupName.getName())) {
			this.createGroup(false, OrganizationalSettings.boidsAndEnvtAndSchedulingGroupName.getName(), "Groupe des scheduler", null);			
		}		
		this.requestRole(OrganizationalSettings.boidsAndEnvtAndSchedulingGroupName.getName(), OrganizationalSettings.schedulerRoleName.getName(), null);
		
		//Creation de l'activateur pour les boids			
		this.boidsActivator = new TurboMethodActivator(Boid.BehavioralMethodName,OrganizationalSettings.boidsAndEnvtAndSchedulingGroupName.getName(), OrganizationalSettings.boidRoleName.getName());
		this.addActivator(this.boidsActivator);
	}
	
	public final void live()
	{
		if(Settings.isLogActivated) println("Scheduler Live");
		while (true)
		{
			exitImmediatlyOnKill();
			if (getDelay() == 0)
				Thread.yield();
			else
				pause(getDelay());

			scheduleWorld();
		}	
	} 

	/**
	 * Tue ce scheduler.
	 */
	public void end()
	{
		if(Settings.isLogActivated) println("Scheduler End");
		removeAllActivators();
		leaveGroup(OrganizationalSettings.boidsAndEnvtAndSchedulingGroupName.getName());
	}
	
	/**
	 * Schedule tous les agents géré par ce scheduler
	 */
	protected void scheduleWorld()
	{
		incrementeIteration();
		executeBoids();	
	}
	
	/**
	 * Schedule les boids
	 */
	protected final void executeBoids()
	{
		this.boidsActivator.execute();
	}
	
	/**
	 * Passe au pas de simulation suivant et synchronization avec l'environnement
	 */
	protected final synchronized void incrementeIteration()
	{
		Iteration++;
		//println("Iteration "+Iteration);
		//Synchronization entre l'environnement(Agent lourd) et le scheduler
		if(Settings.isLogActivated) println("Scheduler a envoyer un SimulationStepMessage, il attend un ACK");
		this.sendMessage(OrganizationalSettings.boidsAndEnvtAndSchedulingGroupName.getName(), 
				OrganizationalSettings.environmentRoleName.getName(), 
				new SimulationStepMessage());
		
		//Attente de la réponse de l'envt : ACK
		boolean wait = true;
		Message m;		
		while(wait) {
			m  = this.waitNextMessage();
			if(m instanceof SimulationStepMessageACK) {
				wait = false;
				if(Settings.isLogActivated) println("ACK recu");
			}
		}
	}
	
	/**
	 * @return le temps de pause de chaque pas de simulation en millisecondes
	 */
	public synchronized int getDelay()
	{
		return delay;
	}

	/**
	 * @param i le nouveau temps de pause de chaque pas de simulation en millisecondes
	 */
	public synchronized void setDelay(int i)
	{
		delay = i;
	}

	
	/**
	 * @return le nombre de pas de simulation déjà effectués
	 */
	public synchronized int getIteration()
	{
		return Iteration;
	}
	
}
