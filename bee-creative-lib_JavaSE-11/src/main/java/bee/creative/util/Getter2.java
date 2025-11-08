package bee.creative.util;

import static bee.creative.util.Getters.getterFrom;

/** Diese Schnittstelle definiert einen {@link Getter} mit {@link Getter3}-Schnittstelle.
 *
 * @author [cc-by] 2025 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <T> Typ des Datensatzes.
 * @param <V> Typ des Werts der Eigenschaft. */
public interface Getter2<T, V> extends Getter<T, V> {

	/** Diese Methode liefert die {@link Getter3}-Schnittstelle zu {@link #get(Object)}. */
	default Getter3<T, V> asGetter() {
		return getterFrom(this);
	}

}
