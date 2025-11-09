package bee.creative.util;

import java.util.IdentityHashMap;
import java.util.Map;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert eine {@link HashMap} mit geringem {@link AbstractHashData Speicherverbrauch}, deren Schlüssel über Reverenzvergleich und
 * {@link System#identityHashCode(Object)} abgeglichen werden.
 *
 * @see IdentityHashMap
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <K> Typ der Schlüssel.
 * @param <V> Typ der Werte. */
public class HashMap3<K, V> extends HashMap<K, V> {

	/** Dieser Konstruktor initialisiert die Kapazität mit {@code 0}. */
	public HashMap3() {
	}

	/** Dieser Konstruktor initialisiert die Kapazität.
	 *
	 * @param capacity Kapazität. */
	public HashMap3(int capacity) {
		super(capacity);
	}

	/** Dieser Konstruktor initialisiert die {@link HashMap3} mit dem Inhalt der gegebenen {@link Map}.
	 *
	 * @param source gegebene Einträge. */
	public HashMap3(Map<? extends K, ? extends V> source) {
		super(source);
	}

	@Override
	protected int customHash(Object key) {
		return Objects.identityHash(key);
	}

	@Override
	protected boolean customEqualsKey(int entryIndex, Object key) {
		return Objects.identityEquals(this.customGetKey(entryIndex), key);
	}

	private static final long serialVersionUID = 5981031863657384681L;

}
