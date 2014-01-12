package bee.creative.util;

/**
 * Diese Schnittstelle definiert eine Methode, mit der die Informationen des Quellobjekts eines gegebenen {@link Assignment}s auf ein gegebenes Zielobjekt
 * übertragen werden können.
 * 
 * @see Assignment
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GInput> Typ des Zielobjekts als Eingabe, auf welche die Informationen übertragen werden.
 * @param <GValue> Typ des Quellobjekts als Wert, dessen die Informationen übertragen werden.
 */
public interface Assigner<GInput, GValue> {

	/**
	 * Diese Methode überträgt die Informationen des Quellobjekts des gegebenen {@link Assignment}s auf das gegebene Zielobjekt.
	 * 
	 * @see Assignment#value()
	 * @param input Zielobjekt.
	 * @param assignment {@link Assignment} mit dem {@link Assignment#value() Quellobjekt}.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
	 * @throws IllegalArgumentException Wenn das {@link Assignment#value() Quellobjekt} ungültig ist.
	 */
	public void assign(GInput input, Assignment<? extends GValue> assignment) throws NullPointerException, IllegalArgumentException;

}
