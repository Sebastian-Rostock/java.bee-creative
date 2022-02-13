package bee.creative.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;
import bee.creative.emu.EMU;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert eine auf {@link AbstractHashMap} aufbauende {@link Map} mit beliebigen Schlüsselobjekten, Wertobjekten und geringem
 * {@link AbstractHashData Speicherverbrauch}. Das {@link #get(Object) Finden} von Einträgen benötigt ca. 75 % der Rechenzeit, die eine
 * {@link java.util.HashMap} benötigen würde. {@link #put(Object, Object) Einfügen} und {@link #remove(Object) Entfernen} von Einträge liegen dazu bei ca. 55 %
 * bzw. 95 % der Rechenzeit. Der Speicerverbrauch liegt bei ca. 44 % (16 Byte je {@link #capacity() reservierten} Eintrag).
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GKey> Typ der Schlüssel.
 * @param <GValue> Typ der Werte. */
public class HashMap<GKey, GValue> extends AbstractHashMap<GKey, GValue> implements Serializable, Cloneable {

	private static final long serialVersionUID = -8792297171308603896L;

	/** Diese Methode ist eine Abkürzung für {@link #from(Hasher, Getter, Getter, Setter) HashMap.from(hasher, Getters.neutral(), Getters.empty(), null)}. */
	public static <GKey, GValue> HashMap<GKey, GValue> from(final Hasher hasher) throws NullPointerException {
		return HashMap.from(hasher, Getters.<GKey>neutral(), Getters.<GKey, GValue>empty(), null);
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(Hasher, Getter, Getter, Setter) HashMap.from(hasher, Getters.neutral(), installValue, null)}. */
	public static <GKey, GValue> HashMap<GKey, GValue> from(final Hasher hasher, final Getter<? super GKey, ? extends GValue> installValue)
		throws NullPointerException {
		return HashMap.from(hasher, Getters.<GKey>neutral(), installValue, null);
	}

	/** Diese Methode liefert eine neue {@link HashMap}, welche Streuwert, Äquivalenz, Installation und Wiederverwendung von Schlüsseln, Werten bzw. Einträgen an
	 * die gegebenen Methoden delegiert.
	 *
	 * @param hasher Methoden zur Berechnung von {@link #customHash(Object) Streuwert} und {@link #customEqualsKey(int, Object) Äquivalenz} der Schlüssel.
	 * @param installKey Methode zur {@link #customInstallKey(Object) Installation} des Schlüssels.
	 * @param installValue Methode zur {@link #customInstallValue(Object) Installation} des Werts.
	 * @param reuseEntry Methode zur Anzeige der {@link #customReuseEntry(int) Wiederverwendung} des Eintrags oder {@code null}. */
	public static <GKey, GValue> HashMap<GKey, GValue> from(final Hasher hasher, final Getter<? super GKey, ? extends GKey> installKey,
		final Getter<? super GKey, ? extends GValue> installValue, final Setter<? super GKey, ? super GValue> reuseEntry) throws NullPointerException {
		Objects.notNull(hasher);
		Objects.notNull(installKey);
		Objects.notNull(installValue);
		return new HashMap<GKey, GValue>() {

			private static final long serialVersionUID = 3593309667398085167L;

			@Override
			protected int customHash(final Object key) {
				return hasher.hash(key);
			}

			@Override
			protected boolean customEqualsKey(final int entryIndex, final Object key) {
				return hasher.equals(this.customGetKey(entryIndex), key);
			}

			@Override
			protected GKey customInstallKey(final GKey key) {
				return installKey.get(key);
			}

			@Override
			protected GValue customInstallValue(final GKey key) {
				return installValue.get(key);
			}

			@Override
			protected void customReuseEntry(final int entryIndex) {
				if (reuseEntry == null) return;
				reuseEntry.set(this.customGetKey(entryIndex), this.customGetValue(entryIndex));
			}

		};
	}

	/** Dieses Feld bildet vom Index eines Eintrags auf dessen Schlüssel ab. Für alle anderen Indizes bildet es auf {@code null} ab. */
	transient Object[] keys = AbstractHashData.EMPTY_OBJECTS;

	/** Dieses Feld bildet vom Index eines Eintrags auf dessen Wert ab. Für alle anderen Indizes bildet es auf {@code null} ab. */
	transient Object[] values = AbstractHashData.EMPTY_OBJECTS;

	/** Dieser Konstruktor initialisiert die Kapazität mit {@code 0}. */
	public HashMap() {
	}

	/** Dieser Konstruktor initialisiert die Kapazität.
	 *
	 * @param capacity Kapazität. */
	public HashMap(final int capacity) {
		this.allocateImpl(capacity);
	}

	/** Dieser Konstruktor initialisiert die {@link HashMap} mit dem Inhalt der gegebenen {@link Map}.
	 *
	 * @param source gegebene Einträge. */
	public HashMap(final Map<? extends GKey, ? extends GValue> source) {
		this(source.size());
		this.putAll(source);
	}

	@SuppressWarnings ("unchecked")
	private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
		final int count = stream.readInt();
		this.allocateImpl(count);
		for (int i = 0; i < count; i++) {
			final Object key = stream.readObject();
			final Object value = stream.readObject();
			this.putImpl((GKey)key, (GValue)value);
		}
	}

	private void writeObject(final ObjectOutputStream stream) throws IOException {
		stream.writeInt(this.countImpl());
		for (final Entry<GKey, GValue> entry: this.newEntriesImpl()) {
			stream.writeObject(entry.getKey());
			stream.writeObject(entry.getValue());
		}
	}

	@Override
	@SuppressWarnings ("unchecked")
	protected GKey customGetKey(final int entryIndex) {
		return (GKey)this.keys[entryIndex];
	}

	@Override
	@SuppressWarnings ("unchecked")
	protected GValue customGetValue(final int entryIndex) {
		return (GValue)this.values[entryIndex];
	}

	@Override
	protected void customSetKey(final int entryIndex, final GKey key) {
		this.keys[entryIndex] = key;
	}

	@Override
	@SuppressWarnings ("unchecked")
	protected GValue customSetValue(final int entryIndex, final GValue value) {
		final Object[] values = this.values;
		final Object result = values[entryIndex];
		values[entryIndex] = value;
		return (GValue)result;
	}

	@Override
	protected void customClear() {
		Arrays.fill(this.keys, null);
		Arrays.fill(this.values, null);
	}

	@Override
	protected void customClearKey(final int entryIndex) {
		this.keys[entryIndex] = null;
	}

	@Override
	protected void customClearValue(final int entryIndex) {
		this.values[entryIndex] = null;
	}

	@Override
	protected HashAllocator customAllocator(final int capacity) {
		final Object[] keys2;
		final Object[] values2;
		if (capacity == 0) {
			keys2 = AbstractHashData.EMPTY_OBJECTS;
			values2 = AbstractHashData.EMPTY_OBJECTS;
		} else {
			keys2 = new Object[capacity];
			values2 = new Object[capacity];
		}
		return new HashAllocator() {

			@Override
			public void copy(final int sourceIndex, final int targetIndex) {
				keys2[targetIndex] = HashMap.this.keys[sourceIndex];
				values2[targetIndex] = HashMap.this.values[sourceIndex];
			}

			@Override
			public void apply() {
				HashMap.this.keys = keys2;
				HashMap.this.values = values2;
			}

		};
	}

	@Override
	public long emu() {
		return super.emu() + EMU.fromArray(this.keys) + EMU.fromArray(this.values);
	}

	@Override
	public HashMap<GKey, GValue> clone() throws CloneNotSupportedException {
		try {
			final HashMap<GKey, GValue> result = (HashMap<GKey, GValue>)super.clone();
			if (this.capacityImpl() == 0) return result;
			result.keys = this.keys.clone();
			result.values = this.values.clone();
			return result;
		} catch (final Exception cause) {
			throw new CloneNotSupportedException(cause.getMessage());
		}
	}

}
