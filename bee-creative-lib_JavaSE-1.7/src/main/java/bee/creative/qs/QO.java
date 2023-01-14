package bee.creative.qs;

/** Diese Schnittstelle definiert ein Objekt mit Bezg zu einem {@link #owner() Graphspeicher}.
 * <p>
 * Methoden, die solche Objekte verarbeiten, lösen generell eine {@link IllegalArgumentException} aus, wenn sich nicht alle beteiligten Objekte auf den gleichn
 * Graphspeicher beziehen. Ferner lösen sie eine {@link NullPointerException} aus, wenn eines der beteiligten Objekte {@code null} ist.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface QO {

	/** Diese Methode gibt den Graphspeicher zurück, der dieses Objekt verwaltet.
	 *
	 * @return Graphspeicher. */
	public QS owner();

}
