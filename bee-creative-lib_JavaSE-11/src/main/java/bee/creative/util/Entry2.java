package bee.creative.util;

import static bee.creative.util.Entries.entryFrom;
import java.util.Map.Entry;

/** Diese Schnittstelle definiert einen {@link Entry} mit {@link Entry3}-Schnittstelle.
 *
 * @author [cc-by] 2025 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <K> Typ des Schlüssels.
 * @param <V> Typ des Werts. */
public interface Entry2<K, V> extends Entry<K, V> {

	/** Diese Methode liefert die {@link Entry3}-Schnittstelle zu {@link #getKey()}, {@link #getValue()} und {@link #setValue(Object)}. */
	default Entry3<K, V> asEntry() {
		return entryFrom(this);
	}

}
