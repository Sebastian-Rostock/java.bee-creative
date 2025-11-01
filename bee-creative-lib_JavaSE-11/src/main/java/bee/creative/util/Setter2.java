package bee.creative.util;

import static bee.creative.util.Setters.setterFrom;

/** Diese Schnittstelle ergänzt einen {@link Setter} insb. um eine Anbindung an Methoden von {@link Setters}.
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
