package bee.creative.ref;

import java.lang.ref.Reference;
import bee.creative.util.Objects;
import bee.creative.util.Producer;

/** Diese Schnittstelle definiert einen Zeiger bzw. Verweis auf einen Datensatz. Sie wird zur verbindung von {@link Reference} mit {@link Producer} verwendet.
 *
 * @see Pointers
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GValue> Typ des Datensatzes. */
public interface Pointer<GValue> extends Producer<GValue> {

	/** Diese Methode gibt den Datensatz zurück.
	 *
	 * @return Datensatz. */
	@Override
	public GValue get();

	/** Diese Methode gibt den via {@link Objects#deepHash(Object)} berechneten {@link Object#hashCode() Streuwert} des Datensatzes zurück.
	 *
	 * @return {@link Object#hashCode() Streuwert} des Datensatzes. */
	@Override
	public int hashCode();

	/** Diese Methode gibt die via {@link Objects#deepEquals(Object, Object)} berechnete {@link Object#equals(Object) Äquivalenz} der Datensätze dieses und des
	 * gegebenenen {@link Pointer} zurück.
	 *
	 * @param object {@link Pointer}.
	 * @return {@link Object#equals(Object) Äquivalenz} der Datensätze. */
	@Override
	public boolean equals(Object object);

}
