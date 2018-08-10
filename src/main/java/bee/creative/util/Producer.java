package bee.creative.util;

/** Diese Schnittstelle definiert eine Methode zur Bereitstellung eines Wertes.
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GValue> Typ des Wertes. */
public interface Producer<GValue> {

	/** Diese Methode gibt den Wert zurück.
	 * 
	 * @return Wert. */
	public GValue get();

}
