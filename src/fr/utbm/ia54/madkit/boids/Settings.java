package fr.utbm.ia54.madkit.boids;

/**
 * Différentse caractéristiques de configuration générale de la simulation
 * @author Nicolas Gaud nicolas.gaud@utbm.fr
 *
 */
public interface Settings {
	/**
     * Autorisation de "traverser" les murs
     */
	public static final boolean traversMurs   =  false;
	
	
	/**
     * Distance utilise pour les obstacles par les boids
     */
	public  static double distObstacles  =  20;
	
	/**
     * Facteur correctif de la force due aux obstacles pour les boids.
     */
    public  static double obstaclesForce  =    80.0;
    
    
    public static final String BoidsBehavioralMethodName = "live";
    
    public static final int EnvtLargeur = 800;
    public static final int EnvtHauteur = 600;
    
    public static final boolean isLogActivated = false;
}
