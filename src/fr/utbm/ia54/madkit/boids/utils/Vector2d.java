package fr.utbm.ia54.madkit.boids.utils;


/**
 * Classe represente un vecteur dans un espace à 2 dimensions.
 * 
 * @author Gildas Cadin	<gildas.cadin@enst-bretagne.fr>
 * @author Lionel Deglise
 * @author Pierre Thebaud
 *
 */
public class Vector2d
{
	/**
	 * La coordonnee X du vecteur.
	 */
	public double x;

	/**
	 * La coordonnee Y du vecteur.
	 */
	public double y;
	
	/**
	 * Constructeur. Le vecteur construit est initialise a zero (x=0 et y=0).
	 */
	public Vector2d()
	{
		x = 0;
		y = 0;
	}

	/**
	 * Constructeur. Le nouveau vecteur est initialise avec les parametres.
	 * @param double xVal : valeur d'initialisation de la coordonnee x du vecteur.
	 * @param double yVal : valeur d'initialisation de la coordonnee y du vecteur.
	 */
	public Vector2d(double xVal, double yVal)
	{
		x = xVal;
		y = yVal;
	}
	
	/**
	 * Constructeur. Le nouveau vecteur est initialise par recopie
	 * @param Vector2d vect : vecteur a recopier.
	 */
	public Vector2d(Vector2d vect)
	{
		x = vect.x;
		y = vect.y;
	}
	
	/**
	 * Initialise le vecteur a zero (x=0 et y=0).
	 */
	public void setZero()
	{
		x = 0;
		y = 0;
	}
	
	/**
	 * Initialise le vecteur en fonction des parametres.
	 * @param double xVal : valeur d'initialisation de la coordonnee x du vecteur.
	 * @param double yVal : valeur d'initialisation de la coordonnee y du vecteur.
	 */
	public void setXY(double xVal, double yVal)
	{
		x = xVal;
		y = yVal;
	}

	/**
	 * Initialise le vecteur par recopie
	 * @param Vector2d vect : vecteur a recopier.
	 */
	public void setXY(Vector2d vect)
	{
		x = vect.x;
		y = vect.y;
	}
	
	/**
	 * Longueur du vecteur
	 * @return Retourne la longueur du vecteur.
	 */
	public double length()
	{
		return Math.sqrt(x*x + y*y);
	}
	
	/**
	 * Norme le vecteur a 1.
	 * Si la longueur est nulle, le vecteur reste tel quel.
	 */
	public void normaliser()
	{
		double zero = 1E-9;
		double len = length();
		
			// si longueur nulle, on ne peut rien faire...
		if ( len*len < zero ) return;
		fois( 1 / len );
	}
	
	/**
	 * Angle du vecteur
	 * @return Retourne l'angle du vecteur. C'est un valeur comprise entre -PI et PI.
	 */
	public double getAngle()
	{
		double zero = 1E-9;

		if ( (x*x) < zero )
		{
			if ( y >= 0 ) return (Math.PI / 2);
			return (-1 * Math.PI / 2);
		}
		
		if ( x >= 0 ) return Math.atan(y/x);
		if ( y >= 0 ) return ( Math.PI + Math.atan(y/x) );
		return ( Math.atan(y/x) - Math.PI );
	}
	
	/**
	 * Addition d'un vecteur.
	 * @param Vector2d vect : vecteur a additioner.
	 */
	public void plus(Vector2d vect)
	{
		x += vect.x;
		y += vect.y;
	}

	/**
	 * Soustraction d'un vecteur.
	 * @param Vector2d vect : vecteur a soustraire.
	 */
	public void moins(Vector2d vect)
	{
		x -= vect.x;
		y -= vect.y;
	}
	
	/**
	 * Homothetie d'un vecteur.
	 * @param double nbr : facteur de l'homothetie.
	 */
	public void fois(double nbr)
	{
		x *= nbr;
		y *= nbr;
	}
	
	/**
	 * Produit scalaire d'un vecteur.
	 * @param Vector2d vect : vecteur sur lequel se projeter.
	 * @return Retourne le produit scalaire du vecteur avec le parametre.
	 */
	public double point(Vector2d vect)
	{
		return ( x*vect.x + y*vect.y );
	}
	
	public String toString() {
		return "("+this.x+","+this.y+")";
	}
}
