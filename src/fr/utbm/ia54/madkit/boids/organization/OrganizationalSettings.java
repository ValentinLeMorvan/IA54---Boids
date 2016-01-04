package fr.utbm.ia54.madkit.boids.organization;

/**
 * Cette classe décrit la seule unique organisation que nous utilisont pour cette simulation.
 * <p>
 * Elle contient trois role :
 * <ul>
 * <li> le role boids : definissant le comportement d'un boid
 * <li> le role environnement : une simple rectangle de dimension fixe
 * <li> le role scheduler : en charge de l 'ordonnancement des agents
 * </ul>
 * 
 * <p>
 * Détails d'agentification et interactions
 * <ul>
 * <li> le Scheduler et l'environnement sont des agents lourds, il doivent donc se synchroniser voilà leur interaction.
 * <li> l'environnement fournit les perceptions aux boids et recupère les forces qu'il désirent emettre
 * </ul>
 * @author Nicolas Gaud nicolas.gaud@utbm.fr
 *
 */
public enum OrganizationalSettings {
	/**
	 * le nom du groupe des boids et de l'environnement
	 */
	boidsAndEnvtAndSchedulingGroupName("boidsGroup"),
	
	/**
	 * Le nom du role que joue les boids dans le groupe <code>boidsAndEnvtAndSchedulingGroupName</code>
	 */
	boidRoleName("Boid"),
	
	/**
	 * Le nom du role que joue l'environment dans le groupe <code>boidsAndEnvtAndSchedulingGroupName</code>
	 */
	environmentRoleName("Environment"),
	
	/**
	 * Le nom du role que joue le scheduler dans le groupe <code>boidsAndEnvtAndSchedulingGroupName</code>
	 */
	schedulerRoleName("Scheduler");
	
	private OrganizationalSettings(String iname) {
		name = iname;
	}
	
	private String name;
	
	public String getName() {
		return name;
	}
	
}
