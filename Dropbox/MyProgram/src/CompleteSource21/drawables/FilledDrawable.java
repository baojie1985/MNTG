package drawables;

/**
 * Abstract class for filled drawable primitives.
 *
 * @version	1.00	17.03.99	first version
 * @author Thomas Brinkhoff
 */

public abstract class FilledDrawable extends Drawable {

	/**
	 * Is the primitive filled?
	 */
	protected boolean filled = false;

/**
 * Gibt zurück, ob das Grafik-Primitiv gefüllt ist.
 * @return gefüllt?
 */
public boolean isFilled () {
	return filled;
}
/**
 * Schaltet die Füllung des Grafik-Primitivs an bzw. aus.
 * @param on gefüllt?
 */
public void setFilling (boolean on) {
	filled = on;
}
}
