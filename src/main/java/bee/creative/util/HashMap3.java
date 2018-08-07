package bee.creative.util;

import java.util.IdentityHashMap;
import java.util.Map;

/** Diese Klasse implementiert eine {@link HashMap} mit Referenzvergleich und {@link System#identityHashCode(Object)} zur Streuwertberechnung.
 *
 * @see IdentityHashMap
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GKey> Typ der Schlüssel.
 * @param <GValue> Typ der Werte. */
public class HashMap3<GKey, GValue> extends HashMap<GKey, GValue> {

	@SuppressWarnings ("javadoc")
	private static final long serialVersionUID = 5981031863657384681L;

	/** Dieser Konstruktor initialisiert die Kapazität mit {@code 0}. */
	public HashMap3() {
	}

	/** Dieser Konstruktor initialisiert die Kapazität.
	 *
	 * @param capacity Kapazität. */
	public HashMap3(final int capacity) {
		super(capacity);
	}

	/** Dieser Konstruktor initialisiert die {@link HashMap3} mit dem Inhalt der gegebenen {@link Map}.
	 *
	 * @param source gegebene Einträge. */
	public HashMap3(final Map<? extends GKey, ? extends GValue> source) {
		super(source);
	}

	/** {@inheritDoc} */
	@Override
	protected int customHash(final Object key) {
		return System.identityHashCode(key);
	}

	/** {@inheritDoc} */
	@Override
	protected int customHashKey(final int entryIndex) {
		return System.identityHashCode(this.keys[entryIndex]);
	}

	/** {@inheritDoc} */
	@Override
	protected boolean customEqualsKey(final int entryIndex, final Object key) {
		return this.keys[entryIndex] == key;
	}

	/** {@inheritDoc} */
	@Override
	protected boolean customEqualsKey(final int entryIndex, final Object key, final int keyHash) {
		return this.keys[entryIndex] == key;
	}

}
