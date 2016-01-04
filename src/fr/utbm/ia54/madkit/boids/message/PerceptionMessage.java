package fr.utbm.ia54.madkit.boids.message;

import java.util.HashMap;
import java.util.Map;

import madkit.kernel.AgentAddress;
import madkit.kernel.Message;
import fr.utbm.ia54.madkit.boids.organization.Environment.PerceivedBoidBody;

/**
 * Mesage transportant la perception d'un boid donné
 * 
 * @author Nicolas GAUD &lt;nicolas.gaud@utbm.fr&gt;
 *
 */
public class PerceptionMessage extends Message {

	private static final long serialVersionUID = -1164698611711945160L;
	
	private Map<AgentAddress,PerceivedBoidBody> otherBoids;

	public PerceptionMessage(Map<AgentAddress,PerceivedBoidBody> boids) {
		otherBoids = new HashMap<AgentAddress,PerceivedBoidBody>(boids);
	}

	public Map<AgentAddress,PerceivedBoidBody> getOtherBoids() {
		return otherBoids;
	}	
}
