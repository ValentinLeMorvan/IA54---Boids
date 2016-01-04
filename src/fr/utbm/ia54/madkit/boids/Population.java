package fr.utbm.ia54.madkit.boids;

import java.awt.Color;

import fr.utbm.ia54.madkit.boids.utils.Vector2d;

/**
 * Les caractéristiques d'une population/groupe de boids
 * 
 * @author Nicolas Gaud nicolas.gaud@utbm.fr
 *
 */
public class Population
{
    public static final double DEFAULT_REPULSION_FORCE = 5.0;
    public static final double DEFAULT_SEPARATION_FORCE = 1.0;
    public static final double DEFAULT_COHESION_FORCE = 0.0001;
    public static final double DEFAULT_ALIGNEMENT_FORCE = 1.0;
    public static final double DEFAULT_OBJECTIVE_FORCE = 0.0001;

    public static final double DEFAULT_REPULSION_DIST = 100.0;
    public static final double DEFAULT_SEPARATION_DIST = 10.0;
    public static final double DEFAULT_COHESION_DIST = 100.0;
    public static final double DEFAULT_ALIGNEMENT_DIST = 100.0;

    public static final int DEFAULT_BOIDS_NB = 1000;

    public Color color;
    public double maxSpeed;
    public double maxForce;
    public double distSeparation;
    public double distCohesion;
    public double distAlignement;
    public double distRepulsion;
    public double visibleAngle;
    public double visibleAngleCos;
    public double separationForce;
    public double cohesionForce;
    public double alignementForce;
    public double repulsionForce;
    public double masse;
    public double vecteurAccel;
    public double objectiveForce;
    
    public Vector2d objective;

    public boolean cohesionOn = true;
    public boolean repulsionOn = true;
    public boolean alignementOn = true;
    public boolean separationOn = true;
    public boolean objectiveOn = false;

    public int nb = DEFAULT_BOIDS_NB;

    public Population(Color col)
    {
		color = col;
		maxSpeed = 0.5;
		maxForce = 1.7;
		distSeparation = DEFAULT_SEPARATION_DIST;
		distCohesion = DEFAULT_COHESION_DIST;
		distAlignement = DEFAULT_ALIGNEMENT_DIST;
		distRepulsion = DEFAULT_REPULSION_DIST;
		visibleAngle = 90.0;
		separationForce = DEFAULT_SEPARATION_FORCE;
		cohesionForce = DEFAULT_COHESION_FORCE;
		alignementForce = DEFAULT_ALIGNEMENT_FORCE;
		repulsionForce = DEFAULT_REPULSION_FORCE;
		objectiveForce = DEFAULT_OBJECTIVE_FORCE;
		masse = 1.0;
		vecteurAccel = 0.85;
		visibleAngleCos = Math.cos(visibleAngle);
    }
    
    public void setObjective(Vector2d objective){
    	objectiveOn = true;
    	this.objective = objective;
    }

}
