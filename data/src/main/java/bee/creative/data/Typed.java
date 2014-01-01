package bee.creative.data;

import java.util.Collection;

/**
 * Diese Schnittstelle definiert ein Objekt mit Datentyp, welcher den {@link Type} des Objekts selbst oder auch deen der darin enthaltenen Datensätze
 * beschreiben kann (z.B. bei einer {@link Collection}).
 * 
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public interface Typed {

	/**
	 * Diese Methode gibt den {@link Type Datentyp} dieses Datensatzes bzw. der in diesem Objekt enthaltenen Datensätze zurück.
	 * 
	 * @return Datentyp.
	 */
	public Type<?> type();

}
