package bee.creative.util;

/** Diese Schnittstelle definiert ein Objekt, auf das die Informationen eines {@link Assignment#value() gegebenen Quellobjekts} übertragen werden können.
 * 
 * @see Assignment
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GSource> Typ des gegebenen Quellobjekts, dessen Informationen übertragen werden können. */
public interface Assignable<GSource> {

	/** Diese Methode überträgt die Informationen des gegebenen {@link Assignment#value() Quellobjekts} auf dieses Objekt.
	 * 
	 * @see Assignment#value()
	 * @param assignment {@link Assignment} mit dem {@link Assignment#value() Quellobjekt}.
	 * @throws NullPointerException Wenn das {@link Assignment} {@code null} ist.
	 * @throws IllegalArgumentException Wenn das {@link Assignment#value() Quellobjekt} ungültig ist. */
	public void assign(Assignment<? extends GSource> assignment) throws NullPointerException, IllegalArgumentException;

}
