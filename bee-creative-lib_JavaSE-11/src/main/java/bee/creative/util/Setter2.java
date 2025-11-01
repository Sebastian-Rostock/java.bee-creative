package bee.creative.util;

import static bee.creative.util.Setters.setterFrom;

/** Diese Schnittstelle definiert einen {@link Setter} mit {@link Setter3}-Schnittstelle.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <T> Typ des Datensatzes.
 * @param <V> Typ des Werts der Eigenschaft. */
public interface Setter2<T, V> extends Setter<T, V> {

	/** Diese Methode ist eine Abkürzung für {@link Setters#setterFrom(Setter) setterFrom(this)}. */
	default Setter3<T, V> asSetter() {
		return setterFrom(this);
	}

}
