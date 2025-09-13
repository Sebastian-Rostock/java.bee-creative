package bee.creative.util;

import java.util.Map;

/** Diese Klasse implementiert eine auf {@link AbstractHashData} aufbauende {@link Map} mit geringem {@link AbstractHashData Speicherverbrauch}.
 * <p>
 * <b>Achtung:</b> Die Ermittlung von {@link Object#hashCode() Streuwerte} und {@link Object#equals(Object) Äquivalenz} der Schlüssel erfolgt nicht wie in
 * {@link Map} beschrieben über die Methoden der Schlüssel, sondern über die Methoden {@link #customHash(Object)} bzw. {@link #customEqualsKey(int, Object)}.
 *
 * @author [cc-by] 2017 Sebastian Rostock [http://cureativecommons.org/licenses/by/3.0/de/]
 * @param <GKey> Typ der Schlüssel.
 * @param <GValue> Typ der Werte. */
public abstract class AbstractHashMap<GKey, GValue> extends AbstractHashData<GKey, GValue> implements Map3<GKey, GValue> {

	/** Diese Methode setzt die Kapazität, sodass dieses die gegebene Anzahl an Einträgen verwaltet werden kann.
	 *
	 * @param capacity Anzahl der maximal verwaltbaren Einträge.
	 * @throws IllegalArgumentException Wenn die gegebene Kapazität kleiner als die aktuelle Anzahl an Einträgen ist. */
	public void allocate(int capacity) throws IllegalArgumentException {
		this.allocateImpl(capacity);
	}

	/** Diese Methode gibt die Anzahl der Einträge zurück, die ohne erneuter Speicherreservierung verwaltet werden kann.
	 *
	 * @return Kapazität. */
	public int capacity() {
		return this.capacityImpl();
	}

	/** Diese Methode verkleinert die Kapazität auf das Minimum. */
	public void compact() {
		this.allocateImpl(this.countImpl());
	}

	/** Diese Methode liefert den zum gegebenen Schlüssel hinterlegten Wert, analog zu {@link #get(Object)}. Wenn zu diesem Schlüssel noch kein Wert hinterlegt
	 * ist, wird ein neuer Wert erzeugt und dem Schlüssel zugeordnet.<br>
	 * Durch Überschreiben von {@link #customInstallKey(Object)} bzw. {@link #customInstallValue(Object)} können Schlüssel und Wert des neu angelegten Eintrags
	 * angepasst werden. Zudem kann durch Überschreiben von {@link #customReuseEntry(int)} auf die Wiederverwendung des Eintrags zum gegebenen Schlüssel reagiert
	 * werden.
	 *
	 * @see #installImpl(Object)
	 * @param key gesuchter Schlüssel.
	 * @return enthaltener und ggf. erzeugter Wert. */
	public GValue install(GKey key) {
		return this.customGetValue(this.installImpl(key));
	}

	public GValue install(GKey key, Producer<? extends GValue> installValue) {
		return this.install(key, Getters.<GKey>neutralGetter(), value -> installValue.get());
	}

	/** Diese Methode liefert den zum gegebenen Schlüssel hinterlegten Wert, analog zu {@link #get(Object)}. Wenn zu diesem Schlüssel noch kein Wert hinterlegt
	 * ist, wird diesem Schlüssel der daraus über {@code installValue} erzeugte Wert zugeordnet.
	 *
	 * @param key Schlüssel des Eintrags.
	 * @param installValue Methode zur Überführung des einzutragenden Schlüssels in den einzutragenden Wert.
	 * @return enthaltener und ggf. erzeugter Wert. */
	public GValue install(GKey key, Getter<? super GKey, ? extends GValue> installValue) {
		return this.install(key, Getters.<GKey>neutralGetter(), installValue);
	}

	/** Diese Methode liefert den zum gegebenen Schlüssel hinterlegten Wert, analog zu {@link #get(Object)}. Wenn zu diesem Schlüssel noch kein Wert hinterlegt
	 * ist, wird dem über {@code installKey} aus dem gegebenen Schlüssel abgeleiteten Schlüssel der daraus über {@code installValue} erzeugte Wert zugeordnet.<br>
	 * <b>Achtung:</b> Innerhalb der Methoden {@code installKey} und {@code installValue} dürfen Einträge nicht {@link #put(Object, Object) eingefügt},
	 * {@link #remove(Object) entfernt} oder {@link #allocate(int) reserviert} werden.
	 *
	 * @param key Schlüssel des Eintrags.
	 * @param installKey Methode zur Überführung des gegebenen Schlüssels in den einzutragenden Schlüssel.
	 * @param installValue Methode zur Überführung des einzutragenden Schlüssels in den einzutragenden Wert.
	 * @return enthaltener und ggf. erzeugter Wert. */
	public GValue install(GKey key, Getter<? super GKey, ? extends GKey> installKey, Getter<? super GKey, ? extends GValue> installValue) {
		return this.customGetValue(this.installImpl(key, installKey, installValue));
	}

	public GValue update(GKey key, Getter<GValue, GValue> updateValue) {
		return this.update(key, (key2, value) -> updateValue.get(value));
	}

	public GValue update(GKey key, Reducer<? super GKey, GValue> updateValue) {
		return this.update(key, Getters.<GKey>neutralGetter(), updateValue);
	}

	public GValue update(GKey key, Getter<? super GKey, ? extends GKey> installKey, Reducer<? super GKey, GValue> updateValue) {
		return this.updateImpl(key, installKey, updateValue);
	}

	@Override
	public int size() {
		return this.countImpl();
	}

	@Override
	public boolean isEmpty() {
		return this.countImpl() == 0;
	}

	@Override
	public boolean containsKey(Object key) {
		return this.hasKeyImpl(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return this.hasValueImpl(value);
	}

	@Override
	public GValue get(Object key) {
		return this.getImpl(key);
	}

	@Override
	public GValue put(GKey key, GValue value) {
		return this.putImpl(key, value);
	}

	@Override
	public GValue remove(Object key) {
		return this.popImpl(key);
	}

	@Override
	public void clear() {
		this.clearImpl();
	}

	@Override
	public Set2<GKey> keySet() {
		return this.newKeysImpl();
	}

	@Override
	public Collection2<GValue> values() {
		return this.newValuesImpl();
	}

	@Override
	public Set2<Entry<GKey, GValue>> entrySet() {
		return this.newEntriesImpl();
	}

	@Override
	public int hashCode() {
		return this.newEntriesImpl().hashCode();
	}

	@Override
	public boolean equals(Object object) {
		if (object == this) return true;
		if (!(object instanceof Map<?, ?>)) return false;
		var that = (Map<?, ?>)object;
		if (that.size() != this.size()) return false;
		for (var entry: that.entrySet()) {
			if (!this.hasEntryImpl(entry.getKey(), entry.getValue())) return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return this.newMappingImpl().toString();
	}

}