package bee.creative.util;

/** Diese Schnittstelle definiert ein {@link Comparable} mit {@link Comparable3}-Schnittstelle.
 *
 * @author [cc-by] 2025 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <T> Typ der Eingabe. */
public interface Comparable2<T> extends Comparable<T> {

	/** Diese Methode liefert die {@link Comparable3}-Schnittstelle zu {@link #compareTo(Object)}. */
	default Comparable3<T> asComparable() {
		return this::compareTo;
	}

}
