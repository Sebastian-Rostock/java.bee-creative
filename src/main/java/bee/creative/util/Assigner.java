package bee.creative.util;

/** Diese Schnittstelle definiert eine Methode, mit der die Informationen des Quellobjekts eines gegebenen {@link Assignment}s auf ein gegebenes Zielobjekt
 * übertragen werden können.
 *
 * @see Assignment
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GTarget> Typ des Zielobjekts, auf welche die Informationen übertragen werden.
 * @param <GSource> Typ des Quellobjekts, dessen Informationen übertragen werden. */
public interface Assigner<GTarget, GSource> {

	/** Diese Methode überträgt die Informationen des Quellobjekts des gegebenen {@link Assignment}s auf das gegebene Zielobjekt.
	 *
	 * @see Assignment#value()
	 * @param target Zielobjekt.
	 * @param assignment {@link Assignment} mit dem {@link Assignment#value() Quellobjekt}.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
	 * @throws IllegalArgumentException Wenn das {@link Assignment#value() Quellobjekt} ungültig ist. */
	public void assign(GTarget target, Assignment<? extends GSource> assignment) throws NullPointerException, IllegalArgumentException;

}
