package bee.creative.ref;

import java.lang.ref.Reference;
import bee.creative.lang.Objects;
import bee.creative.util.Producer;

/** Diese Schnittstelle definiert einen Verweis auf einen Datensatz. Sie wird zur Verbindung von {@link Reference} und {@link Producer} verwendet.
 *
 * @see References
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GValue> Typ des Datensatzes. */
public interface Reference2<GValue> extends Producer<GValue> {

	/** Diese Methode gibt den Datensatz zurück.
	 *
	 * @return Datensatz. */
	@Override
	public GValue get();

	/** Diese Methode gibt den über {@link Objects#deepHash(Object)} berechneten {@link Object#hashCode() Streuwert} des Datensatzes zurück.
	 *
	 * @return {@link Object#hashCode() Streuwert} des {@link #get() Datensatzes}. */
	@Override
	public int hashCode();

	/** Diese Methode gibt die über {@link Objects#deepEquals(Object, Object)} berechnete {@link Object#equals(Object) Äquivalenz} der Datensätze dieser und der
	 * gegebenenen {@link Reference2} zurück.
	 *
	 * @param object {@link Reference2}.
	 * @return {@link Object#equals(Object) Äquivalenz} der {@link #get() Datensätze}. */
	@Override
	public boolean equals(Object object);

}
