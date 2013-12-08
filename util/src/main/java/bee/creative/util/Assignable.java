package bee.creative.util;

/**
 * Diese Schnittstelle definiert ein Objekt, auf das die Informationen eines {@link Assigner#source() gegebenen Objekts} übertragen werden können.
 * 
 * @see Assigner
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GSource> Typ des gegebenen Objekts, dessen Informationen auf dieses übertragen werden können.
 */
public interface Assignable<GSource> {

	/**
	 * Diese Methode überträgt die Informationen des gegebenen {@link Assigner#source() Quellobjekts} auf dieses Objekt.
	 * 
	 * @see Assigner#source()
	 * @param assigner {@link Assigner} mit dem {@link Assigner#source() Quellobjekt}.
	 * @throws NullPointerException Wenn der {@link Assigner} {@code null} ist.
	 * @throws IllegalArgumentException Wenn das {@link Assigner#source() Quellobjekt} ungültig ist.
	 */
	public void assign(Assigner<? extends GSource> assigner) throws NullPointerException, IllegalArgumentException;

}
