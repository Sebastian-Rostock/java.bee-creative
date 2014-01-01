package bee.creative.util;

/**
 * Diese Schnittstelle definiert ein Objekt, auf das die Informationen eines {@link Assignment#value() gegebenen Objekts} übertragen werden können.
 * 
 * @see Assignment
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GValue> Typ des gegebenen Objekts, dessen Informationen auf dieses übertragen werden können.
 */
public interface Assignable<GValue> {

	/**
	 * Diese Methode überträgt die Informationen des gegebenen {@link Assignment#value() Quellobjekts} auf dieses Objekt.
	 * 
	 * @see Assignment#value()
	 * @param assignment {@link Assignment} mit dem {@link Assignment#value() Quellobjekt}.
	 * @throws NullPointerException Wenn das {@link Assignment} {@code null} ist.
	 * @throws IllegalArgumentException Wenn das {@link Assignment#value() Quellobjekt} ungültig ist.
	 */
	public void assign(Assignment<? extends GValue> assignment) throws NullPointerException, IllegalArgumentException;

}
