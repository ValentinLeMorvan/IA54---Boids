package fr.utbm.ia54.madkit.boids.message;

import madkit.kernel.Message;
import fr.utbm.ia54.madkit.boids.utils.Vector2d;

/**
 * Message transportant la force calculé par le comportement d'un boids
 * 
 * @author Nicolas GAUD &lt;nicolas.gaud@utbm.fr&gt;
 *
 */
public class ActionMessage extends Message {

	private static final long serialVersionUID = -8596108743195909605L;
	
	private Vector2d force;
	
	public ActionMessage(Vector2d iforce) {
		force = iforce;
	}
	
	public Vector2d getForce() {
		return force;
	}
}
