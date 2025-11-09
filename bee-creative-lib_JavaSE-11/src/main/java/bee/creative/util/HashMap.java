package bee.creative.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;
import bee.creative.emu.EMU;

/** Diese Klasse implementiert eine auf {@link AbstractHashMap} aufbauende {@link Map} mit beliebigen Schlüsselobjekten, Wertobjekten und geringem
 * {@link AbstractHashData Speicherverbrauch}. Das {@link #get(Object) Finden} von Einträgen benötigt ca. 75 % der Rechenzeit, die eine
 * {@link java.util.HashMap} benötigen würde. {@link #put(Object, Object) Einfügen} und {@link #remove(Object) Entfernen} von Einträge liegen dazu bei ca. 55 %
 * bzw. 95 % der Rechenzeit. Der Speicerverbrauch liegt bei ca. 44 % (16 Byte je {@link #capacity() reservierten} Eintrag).
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <K> Typ der Schlüssel.
 * @param <V> Typ der Werte. */
public class HashMap<K, V> extends AbstractHashMap<K, V> implements Serializable, Cloneable {

	/** Dieser Konstruktor initialisiert die Kapazität mit {@code 0}. */
	public HashMap() {
	}

	/** Dieser Konstruktor initialisiert die Kapazität.
	 *
	 * @param capacity Kapazität. */
	public HashMap(int capacity) {
		this.allocateImpl(capacity);
	}

	/** Dieser Konstruktor initialisiert die {@link HashMap} mit dem Inhalt der gegebenen {@link Map}.
	 *
	 * @param source gegebene Einträge. */
	public HashMap(Map<? extends K, ? extends V> source) {
		this(source.size());
		this.putAll(source);
	}

	@Override
	public long emu() {
		return super.emu() + EMU.fromArray(this.keys) + EMU.fromArray(this.values);
	}

	@Override
	public HashMap<K, V> clone() {
		var result = (HashMap<K, V>)super.clone();
		if (this.capacityImpl() == 0) return result;
		result.keys = this.keys.clone();
		result.values = this.values.clone();
		return result;
	}

	@Override
	@SuppressWarnings ("unchecked")
	protected K customGetKey(int entryIndex) {
		return (K)this.keys[entryIndex];
	}

	@Override
	@SuppressWarnings ("unchecked")
	protected V customGetValue(int entryIndex) {
		return (V)this.values[entryIndex];
	}

	@Override
	protected void customSetKey(int entryIndex, K key) {
		this.keys[entryIndex] = key;
	}

	@Override
	protected void customSetValue(int entryIndex, V value) {
		this.values[entryIndex] = value;
	}

	@Override
	protected void customClear() {
		Arrays.fill(this.keys, null);
		Arrays.fill(this.values, null);
	}

	@Override
	protected void customClearKey(int entryIndex) {
		this.keys[entryIndex] = null;
	}

	@Override
	protected void customClearValue(int entryIndex) {
		this.values[entryIndex] = null;
	}

	@Override
	protected HashAllocator customAllocator(int capacity) {
		Object[] keys2;
		Object[] values2;
		if (capacity == 0) {
			keys2 = AbstractHashData.EMPTY_OBJECTS;
			values2 = AbstractHashData.EMPTY_OBJECTS;
		} else {
			keys2 = new Object[capacity];
			values2 = new Object[capacity];
		}
		return new HashAllocator() {

			@Override
			public void copy(int sourceIndex, int targetIndex) {
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

	/** Dieses Feld bildet vom Index eines Eintrags auf dessen Schlüssel ab. Für alle anderen Indizes bildet es auf {@code null} ab. */
	transient Object[] keys = AbstractHashData.EMPTY_OBJECTS;

	/** Dieses Feld bildet vom Index eines Eintrags auf dessen Wert ab. Für alle anderen Indizes bildet es auf {@code null} ab. */
	transient Object[] values = AbstractHashData.EMPTY_OBJECTS;

	private static final long serialVersionUID = -8792297171308603896L;

	@SuppressWarnings ("unchecked")
	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		var count = stream.readInt();
		this.allocateImpl(count);
		for (var i = 0; i < count; i++) {
			var key = stream.readObject();
			var value = stream.readObject();
			this.putValueImpl((K)key, (V)value);
		}
	}

	private void writeObject(ObjectOutputStream stream) throws IOException {
		stream.writeInt(this.countImpl());
		for (var entry: this.newEntriesImpl()) {
			stream.writeObject(entry.getKey());
			stream.writeObject(entry.getValue());
		}
	}

}
