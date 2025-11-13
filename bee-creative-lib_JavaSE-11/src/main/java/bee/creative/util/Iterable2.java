package bee.creative.util;

import static bee.creative.util.Iterables.iterableFrom;

/** Diese Schnittstelle definiert einen {@link Iterable} mit {@link Iterable3}-Schnittstelle.
 *
 * @author [cc-by] 2025 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <T> Typ der Elemente. */
public interface Iterable2<T> extends Iterable<T> {

	/** Diese Methode liefert die {@link Iterable3}-Schnittstelle zu {@link #iterator()}. */
	default Iterable3<T> asIterable() {
		return iterableFrom(this);
	}

}
