package fr.utbm.ia54.madkit.boids.gui;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;

import madkit.kernel.AgentAddress;
import fr.utbm.ia54.madkit.boids.message.ActionMessage;
import fr.utbm.ia54.madkit.boids.organization.Environment;
import fr.utbm.ia54.madkit.boids.organization.Environment.PerceivedBoidBody;
import fr.utbm.ia54.madkit.boids.utils.Vector2d;

/**
 * L'interface graphique de la simulation de boids
 * 
 * @author Nicolas Gaud nicolas.gaud@utbm.fr
 *
 */
public class EnvironmentGui extends Frame {
	private static final long serialVersionUID = 6166914441888970939L;
	
	private Environment environment;
	/**
	 * Handler de fermeture de fenetre
	 */
	private Closer handler;
	
	/**
	 * Panel d'affiche du canvas representant l'environnement
	 */
	EnvironmentGuiPanel panel;
	
	public EnvironmentGui(int ihauteur, int ilargeur,Map<AgentAddress,PerceivedBoidBody> iboids, Environment ienvironment) {
		super();
		handler = new Closer ();
		panel = new EnvironmentGuiPanel(ihauteur,ilargeur,iboids);
		this.environment = ienvironment;
		
		this.setTitle("Boids Simulation");
		this.setSize(ilargeur,ihauteur);
		addWindowListener(handler);
        
	
		add("Center", panel);
		this.setVisible(true);
	//add("East", new ControlPanel(this));
		
		 panel.addMouseListener(new MouseAdapter() {
			 @Override
             public void mousePressed(MouseEvent e) {
				 environment.clickOn(new Vector2d(e.getX(),e.getY()), 0);
					
             }
		 });
    }
	
	public void setBoids(Map<AgentAddress, PerceivedBoidBody> boids) {
		this.panel.setBoids(boids);
	}

	@Override
	public void paint(Graphics g) {
		
		super.paint(g);
		this.panel.paint(g);
	}
}

class Closer extends WindowAdapter {
    public void windowClosing (WindowEvent event) {
        System.exit (0);
    }
}

/**
 * Interface graphique asocié à l'environnement de la simulation
 * @author Nicolas GAUD &lt;nicolas.gaud@utbm.fr&gt;
 *
 */
class EnvironmentGuiPanel extends Panel {

	private static final long serialVersionUID = 1147541712721356182L;
	
	/**
	 * La zone d'affichage graphique a l'ecran.
	 */
	private Graphics myGraphics;
	/**
	 * La zone de travail (gestion du double-buffering pour l'affichage).
	 */
	private Graphics myCanvas;
	/**
	 * L'image de la zone de travail (gestion du double-buffering pour l'affichage).
	 */
	private Image	 myImage;
	
	/**
	 * La demi-largeur du monde.
	 */
	private  int	 largeur;
	/**
	 * La demi-hauteur du monde.
	 */
	private  int	 hauteur;
	
	
	private Map<AgentAddress,PerceivedBoidBody> boids;


	public void setBoids(Map<AgentAddress, PerceivedBoidBody> boids) {
		this.boids = boids;
	}
	
	public EnvironmentGuiPanel(int ihauteur, int ilargeur,Map<AgentAddress,PerceivedBoidBody> iboids) {
		super();
		setLargeur(ilargeur);
		setHauteur(ihauteur);
		boids = iboids;
	}
	
	
	/**
	 * Affichage.
	 * @param Graphics g: le contexte d'affichage du Monde.
	 */	
	public void paint(Graphics g)
	{
	    if((myCanvas != null) && (myGraphics != null))
	    {
			Color	bgColor;

			// effacement de la zone de travail
			bgColor = new Color(0.6F, 0.6F, 0.6F);
			myCanvas.setColor(bgColor);
			myCanvas.fillRect(0, 0, getLargeur() * 2 - 1, getHauteur() * 2 - 1);
			myCanvas.setColor(Color.black);
			myCanvas.drawRect(0, 0, getLargeur() * 2 - 1, getHauteur() * 2 - 1);
						
			// affichage des Boids
			for (PerceivedBoidBody boid : boids.values()){
				paintBoid(myCanvas,boid);
			}
				
			// affichage de la zone de travail
			myGraphics.drawImage(myImage, 0, 0, this);
	    }
	}

	/**
	 * Reactualisation de l'affichage.
	 * @param Graphics g: le contexte d'affichage du Monde.
	 */	
	public void update(Graphics g)
	{
	    paint(g);
	}

	/**
	 * Creation du Layout
	 */	
	public void doLayout()
	{
		super.doLayout();

		// initialisation des variables largeur et hauteur.
		setLargeur((int) (getSize().width  / 2));
		setHauteur((int) (getSize().height / 2));

		// mise en place du double buffering pour l'affichage.
		myImage    = createImage(getLargeur() * 2, getHauteur() * 2);
		myCanvas   = myImage.getGraphics();
		myGraphics = getGraphics();
	}
	
	/**
	 * Affichage d'un Boid.
	 * @param Graphics g: le contexte d'affichage du Monde.
	 * @param PerceivedBoidBody boid : le boid a afficher
	 */
	public void paintBoid(Graphics g,PerceivedBoidBody boid)
	{
		int    posX;
		int    posY;
		double direction;
		double cos;
		double sin;

		posX	  = getLargeur() + new Double(boid.getPosition().x).intValue();
		posY	  = getHauteur() + new Double(boid.getPosition().y).intValue();
		direction = boid.getVitesse().getAngle();
		cos	  = Math.cos(direction);
		sin	  = Math.sin(direction);
		
		g.setColor(boid.getGroupe().color);
		
		g.drawLine(	posX + (int) ( 5 * cos ),
				posY + (int) ( 5 * sin ),
				posX - (int) ( 2 * cos + 2 * sin ),
				posY - (int) ( 2 * sin - 2 * cos ) );
		g.drawLine(	posX + (int) ( 5 * cos ),
				posY + (int) ( 5 * sin ),
				posX - (int) ( 2 * cos - 2 * sin ),
				posY - (int) ( 2 * sin + 2 * cos ) );
		g.drawLine(	posX - (int) ( 2 * cos + 2 * sin ),
				posY - (int) ( 2 * sin - 2 * cos ),
				posX - (int) ( 2 * cos - 2 * sin ),
				posY - (int) ( 2 * sin + 2 * cos ) );
	}

	public int getLargeur() {
		return largeur;
	}

	public void setLargeur(int largeur) {
		this.largeur = largeur;
	}

	public int getHauteur() {
		return hauteur;
	}

	public void setHauteur(int hauteur) {
		this.hauteur = hauteur;
	}
}
