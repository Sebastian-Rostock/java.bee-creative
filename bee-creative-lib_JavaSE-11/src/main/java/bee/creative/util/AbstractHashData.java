package bee.creative.util;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import bee.creative.emu.EMU;
import bee.creative.emu.Emuable;
import bee.creative.iam.IAMMapping;
import bee.creative.lang.Objects;

/** Diese abstrakte Klasse implementiert eine transponierte {@link Object#hashCode() streuwertbasierte} Datenhaltung als Grundlage einer {@link Map} oder eines
 * {@link Set}.
 * <p>
 * Um die Verwaltungsdaten je Eintrag zu minimieren, werden die Tabelle mit den Eigenschaften der Einträge spaltenweise in mehreren Arrays
 * {@link #allocateImpl(int) reserviert} und die Verweise auf die nächsten Einträge in den verketteten Listen über Indexpositionen abgebildet. Nachfahren müssen
 * die Spalten zur verwaltung der Schlüssel und Werte ergänzen. Über die Methoden {@link #customHash(Object)} und {@link #customEqualsKey(int, Object)} kann die
 * Berechnung von {@link Object#hashCode() Streuwert} und {@link Object#equals(Object) Äquivalenz} der Schlüssel angepasst werden. Die nachfolgende Tabelle
 * zeigt den Vergleich der genäherten Speicherbelegung (32 Bit).
 * <p>
 * <table border="1" cellpadding="2" cellspacing="0">
 * <tr>
 * <th rowspan="2">Klasse<br>
 * (JavaSE-1.7)</th>
 * <th colspan="4">Speicherverbrauch in Byte</th>
 * </tr>
 * <tr>
 * <th>capacity = 0</th>
 * <th>capacity = 1..500M</th>
 * <th>capacity = 500M..1G</th>
 * <th>capacity = 1G..2G</th>
 * </tr>
 * <tr>
 * <td colspan="5"></td>
 * </tr>
 * <tr>
 * <td>{@link java.util.HashMap}</td>
 * <td>72..120</td>
 * <td colspan="2">36 x capacity + 64..112 ( 100 % )</td>
 * <td>34..36 x capacity + 64..112 ( 94..100 % )</td>
 * </tr>
 * <tr>
 * <td>{@link bee.creative.util.HashMap}<br>
 * {@link bee.creative.util.HashMap3}<br>
 * {@link bee.creative.util.HashMapII}<br>
 * {@link bee.creative.util.HashMapIO}<br>
 * {@link bee.creative.util.HashMapOI}</td>
 * <td>32</td>
 * <td>16 x capacity + 96 ( 44 % )</td>
 * <td colspan="2">13..16 x capacity + 96 ( 36..44 % )</td>
 * </tr>
 * <tr>
 * <td>{@link bee.creative.util.HashMapLO}<br>
 * {@link bee.creative.util.HashMapOL}</td>
 * <td>32</td>
 * <td>20 x capacity + 96 ( 38 % )</td>
 * <td colspan="2">17..20 x capacity + 96 ( 33..38 %)</td>
 * </tr>
 * <tr>
 * <td>{@link bee.creative.util.HashMapLL}</td>
 * <td>32</td>
 * <td>24 x capacity + 96 ( 35 % )</td>
 * <td colspan="2">21..24 x capacity + 96 ( 31..35 %)</td>
 * </tr>
 * <tr>
 * <td>{@link bee.creative.util.HashMap2}</td>
 * <td>40</td>
 * <td>20 x capacity + 120 ( 56 % )</td>
 * <td colspan="2">17..20 x capacity + 120 ( 47..56 %)</td>
 * </tr>
 * <tr>
 * <td colspan="5"></td>
 * <tr>
 * <td>{@link java.util.HashSet}</td>
 * <td>88..136</td>
 * <td colspan="2">36 x capacity + 80..128 ( 100 % )</td>
 * <td>34..36 x capacity + 80..128 ( 95..100 % )</td>
 * </tr>
 * <tr>
 * <td>{@link bee.creative.util.HashSet}<br>
 * {@link bee.creative.util.HashSet3}<br>
 * {@link bee.creative.util.HashSetI}</td>
 * <td>32</td>
 * <td>12 x capacity + 80 ( 33 % )</td>
 * <td colspan="2">9..12 x capacity + 80 ( 25..33 % )</td>
 * </tr>
 * <tr>
 * <td>{@link bee.creative.util.HashSetL}</td>
 * <td>32</td>
 * <td>16 x capacity + 80 ( 31 % )</td>
 * <td colspan="2">13..16 x capacity + 80 ( 25..31 % )</td>
 * </tr>
 * <tr>
 * <td>{@link bee.creative.util.HashSet2}</td>
 * <td>40</td>
 * <td>16 x capacity + 104 ( 44 % )</td>
 * <td colspan="2">13..16 x capacity + 104 ( 36..44 % )</td>
 * </tr>
 * </table>
 *
 * @see HashMap
 * @see HashMap2
 * @see HashSet
 * @see HashSet2
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GKey> Typ der Schlüssel.
 * @param <GValue> Typ der Werte. */
public abstract class AbstractHashData<GKey, GValue> implements Emuable {

	/** Diese Klasse implementiert das {@link Entry} für {@link AbstractHashData.HashIterator#nextEntry()}. */
	
	protected static class HashEntry<GKey, GValue> implements Entry<GKey, GValue> {

		protected final AbstractHashData<GKey, GValue> entryData;

		protected final int entryIndex;

		public HashEntry(AbstractHashData<GKey, GValue> entryData, int entryIndex) {
			this.entryData = entryData;
			this.entryIndex = entryIndex;
		}

		@Override
		public GKey getKey() {
			return this.entryData.customGetKey(this.entryIndex);
		}

		@Override
		public GValue getValue() {
			return this.entryData.customGetValue(this.entryIndex);
		}

		@Override
		public GValue setValue(GValue value) {
			return this.entryData.customSetValue(this.entryIndex, value);
		}

		@Override
		public int hashCode() {
			return this.entryData.customHashKey(this.entryIndex) ^ this.entryData.customHashValue(this.entryIndex);
		}

		@Override
		public boolean equals(Object object) {
			if (!(object instanceof Entry)) return false;
			var that = (Entry<?, ?>)object;
			return this.entryData.customEqualsKey(this.entryIndex, that.getKey()) && this.entryData.customEqualsValue(this.entryIndex, that.getValue());
		}

		@Override
		public String toString() {
			return this.getKey() + "=" + this.getValue();
		}

	}

	/** Diese Klasse implementiert das unveränderliche {@link AbstractHashData.HashEntry}. */
	
	protected static class HashEntry2<GKey, GValue> extends HashEntry<GKey, GValue> {

		public HashEntry2(AbstractHashData<GKey, GValue> entryData, int entryIndex) {
			super(entryData, entryIndex);
		}

		@Override
		public GValue setValue(GValue value) {
			throw new UnsupportedOperationException();
		}

	}

	/** Diese Klasse implementiert den abstrakten {@link Iterator} über die Schlüssel, Werte und Einträge. */
	
	protected static abstract class HashIterator<GKey, GValue, GItem> extends AbstractIterator<GItem> {

		protected final AbstractHashData<GKey, GValue> entryData;

		/** Dieses Feld speichert den Index des nächsten Eintrags in {@link AbstractHashData#customGetKey(int)}. */
		protected int nextEntry;

		/** Dieses Feld speichert den Index des aktuellen Eintrags in {@link AbstractHashData#table}. */
		protected int nextTable = -1;

		/** Dieses Feld speichert den Index des aktuellen Eintrags in {@link AbstractHashData#customGetKey(int)}. */
		protected int prevEntry = -1;

		protected int prevTable;

		public HashIterator(AbstractHashData<GKey, GValue> entryData) {
			this.entryData = entryData;
			this.nextIndex2();
		}

		/** Diese Methode gibt den nächsten Schlüssel zurück. */
		protected final GKey nextKey() {
			return this.entryData.customGetKey(this.nextIndex());
		}

		/** Diese Methode gibt den nächsten Eintrag zurück. */
		protected final Entry<GKey, GValue> nextEntry() {
			return this.entryData.customGetEntry(this.nextIndex());
		}

		/** Diese Methode gibt den nächsten Wert zurück. */
		protected final GValue nextValue() {
			return this.entryData.customGetValue(this.nextIndex());
		}

		/** Diese Methode ermitteln den Index des nächsten Eintrags und gibt den des aktuellen zurück. */
		protected final int nextIndex() {
			var result = this.nextEntry;
			this.prevEntry = result;
			if (result < 0) throw new NoSuchElementException();
			var nextEntry = this.entryData.nexts[result];
			if (nextEntry >= 0) {
				this.prevTable = this.nextTable;
				this.nextEntry = nextEntry;
			} else {
				this.nextIndex2();
			}
			return result;
		}

		/** Diese Methode sucht den Index des nächsten Eintrags. */
		final void nextIndex2() {
			var table = this.entryData.table;
			var length = table.length;
			for (var nextTable = (this.prevTable = this.nextTable) + 1; nextTable < length; ++nextTable) {
				var nextEntry = table[nextTable];
				if (nextEntry >= 0) {
					this.nextEntry = nextEntry;
					this.nextTable = nextTable;
					return;
				}
			}
			this.nextEntry = -1;
			this.nextTable = -1;
		}

		@Override
		public boolean hasNext() {
			return this.nextEntry >= 0;
		}

		@Override
		public void remove() {
			if (this.entryData.popEntryImpl(this.prevTable, this.prevEntry)) return;
			this.prevEntry = -1;
			throw new IllegalStateException();
		}

	}

	/** Diese Klasse implementiert {@link AbstractHashData#newKeysImpl()}. */
	
	protected static class Keys<GKey> extends AbstractSet2<GKey> {

		protected final AbstractHashData<GKey, ?> entryData;

		public Keys(AbstractHashData<GKey, ?> entryData) {
			this.entryData = entryData;
		}

		@Override
		public int size() {
			return this.entryData.countImpl();
		}

		@Override
		public void clear() {
			this.entryData.clearImpl();
		}

		@Override
		public Iterator2<GKey> iterator() {
			return this.entryData.newKeysIteratorImpl();
		}

		@Override
		public boolean contains(Object item) {
			return this.entryData.hasKeyImpl(item);
		}

		@Override
		public boolean remove(Object item) {
			return this.entryData.popKeyImpl(item);
		}

		@Override
		public int hashCode() {
			var entryData = this.entryData;
			var result = 0;
			for (var iter = entryData.newKeysIteratorImpl(); iter.hasNext();) {
				result += entryData.customHashKey(iter.nextIndex());
			}
			return result;
		}

		@Override
		public boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof Set)) return false;
			var that = (Set<?>)object;
			if (this.size() != that.size()) return false;
			return this.containsAll(that);
		}

	}

	/** Diese Klasse implementiert {@link AbstractHashData#newKeysIteratorImpl()}. */
	protected static class KeysIterator<GKey, GValue> extends HashIterator<GKey, GValue, GKey> {

		public KeysIterator(AbstractHashData<GKey, GValue> entryData) {
			super(entryData);
		}

		@Override
		public GKey next() {
			return this.nextKey();
		}

	}

	/** Diese Klasse implementiert {@link AbstractHashData#newValuesImpl()}. */
	protected static class Values<GValue> extends AbstractCollection2<GValue> {

		protected final AbstractHashData<?, GValue> entryData;

		public Values(AbstractHashData<?, GValue> entryData) {
			this.entryData = entryData;
		}

		@Override
		public int size() {
			return this.entryData.countImpl();
		}

		@Override
		public void clear() {
			this.entryData.clearImpl();
		}

		@Override
		public Iterator2<GValue> iterator() {
			return this.entryData.newValuesIteratorImpl();
		}

		@Override
		public boolean contains(Object o) {
			return this.entryData.hasValueImpl(o);
		}

		@Override
		public boolean remove(Object o) {
			return this.entryData.popValueImpl(o);
		}

	}

	/** Diese Klasse implementiert {@link AbstractHashData#newValuesIteratorImpl()}. */
	
	protected static class ValuesIterator<GKey, GValue> extends HashIterator<GKey, GValue, GValue> {

		public ValuesIterator(AbstractHashData<GKey, GValue> entryData) {
			super(entryData);
		}

		@Override
		public GValue next() {
			return this.nextValue();
		}

	}

	/** Diese Klasse implementiert {@link AbstractHashData#newEntriesImpl()}. */
	
	protected static class Entries<GKey, GValue> extends AbstractSet2<Entry<GKey, GValue>> {

		protected final AbstractHashData<GKey, GValue> entryData;

		public Entries(AbstractHashData<GKey, GValue> entryData) {
			this.entryData = entryData;
		}

		@Override
		public int size() {
			return this.entryData.countImpl();
		}

		@Override
		public void clear() {
			this.entryData.clearImpl();
		}

		@Override
		public Iterator2<Entry<GKey, GValue>> iterator() {
			return this.entryData.newEntriesIteratorImpl();
		}

		@Override
		public boolean remove(Object object) {
			if (!(object instanceof Entry)) return false;
			var entry = (Entry<?, ?>)object;
			return this.entryData.popEntryImpl(entry.getKey(), entry.getValue());
		}

		@Override
		public boolean contains(Object object) {
			if (!(object instanceof Entry)) return false;
			var entry = (Entry<?, ?>)object;
			return this.entryData.hasEntryImpl(entry.getKey(), entry.getValue());
		}

	}

	/** Diese Klasse implementiert {@link AbstractHashData#newEntriesIteratorImpl()}. */
	
	protected static class EntriesIterator<GKey, GValue> extends HashIterator<GKey, GValue, Entry<GKey, GValue>> {

		public EntriesIterator(AbstractHashData<GKey, GValue> entryData) {
			super(entryData);
		}

		@Override
		public Entry<GKey, GValue> next() {
			return this.nextEntry();
		}

	}

	/** Diese Klasse implementiert {@link AbstractHashData#newMappingImpl()}. */
	protected static class Mapping<GKey, GValue> extends AbstractMap<GKey, GValue> implements Map3<GKey, GValue> {

		protected final AbstractHashData<GKey, GValue> entryData;

		public Mapping(AbstractHashData<GKey, GValue> entryData) {
			this.entryData = entryData;
		}

		@Override
		public int size() {
			return this.entryData.countImpl();
		}

		@Override
		public void clear() {
			this.entryData.clearImpl();
		}

		@Override
		public boolean isEmpty() {
			return this.entryData.countImpl() == 0;
		}

		@Override
		public boolean containsKey(Object key) {
			return this.entryData.hasKeyImpl(key);
		}

		@Override
		public boolean containsValue(Object value) {
			return this.values().contains(value);
		}

		@Override
		public GValue get(Object key) {
			return this.entryData.getImpl(key);
		}

		@Override
		public Keys<GKey> keySet() {
			return this.entryData.newKeysImpl();
		}

		@Override
		public GValue put(GKey key, GValue value) {
			return this.entryData.putImpl(key, value);
		}

		@Override
		public GValue remove(Object key) {
			return this.entryData.popImpl(key);
		}

		@Override
		public Values<GValue> values() {
			return this.entryData.newValuesImpl();
		}

		@Override
		public Entries<GKey, GValue> entrySet() {
			return this.entryData.newEntriesImpl();
		}

	}

	/** Diese Schnittstelle definiert den Allokator zur Reservierung und Aktualisierung der Schlüssel- und Wertlisten eines {@link AbstractHashData}. */
	protected static interface HashAllocator {

		/** Diese Methode kopiert Schlüssel und Wert des gegebenen Quelleintrags zum gegebenen Zieleintrag.
		 *
		 * @see AbstractHashData#allocateImpl(int)
		 * @param sourceIndex Index des Quelleintrags.
		 * @param targetIndex Index des Zieleintrags. */
		void copy(int sourceIndex, int targetIndex);

		/** Diese Methode überträgt die Schlüssel- und Wertlisten auf den Erzeuger dieses Allokators.
		 *
		 * @see AbstractHashData#allocateImpl(int) **/
		void apply();

	}

	/** Dieses Feld speichert die maximale Kapazität. */
	private static final int MAX_CAPACITY = Integer.MAX_VALUE - 8;

	/** Dieses Feld speichert den initialwert für {@link #table}. */
	private static final int[] EMPTY_TABLE = {-1};

	/** Dieses Feld speichert den initialwert für eine leere Zahlenliste. */
	protected static final long[] EMPTY_LONGS = {};

	/** Dieses Feld speichert den initialwert für eine Objektliste. */
	protected static final Object[] EMPTY_OBJECTS = {};

	/** Dieses Feld speichert den initialwert für eine leere Zahlenliste. */
	protected static final int[] EMPTY_INTEGERS = {};

	private static void setupNextsImpl(int[] array) {
		for (int i = 0, size = array.length; i < size; array[i] = ++i) {}
	}

	private static void setupTableImpl(int[] array) {
		Arrays.fill(array, -1);
	}

	/** Dieses Feld bildet vom maskierten Streuwert eines Schlüssels auf den Index des Eintrags ab, dessen Schlüssel den gleichen maskierten Streuwert besitzt.
	 * Die Länge dieser Liste entspricht stets einer Potenz von 2. Ungenutzte Elemente sind {@code -1}. */
	transient int[] table;

	/** Dieses Feld bildet vom Index eines Eintrags auf den Index des nächsten Eintrags ab. Für alle anderen Indizes bildet es auf den Index des nächsten
	 * reservierten Speicherbereiches ab. Ungenutzte Elemente sind {@code -1}. */
	transient int[] nexts;

	/** Dieses Feld speichert die Anzahl der Einträge. */
	transient int count;

	/** Dieses Feld speichert den Index des nächsten freien Speicherbereiches in {@link #nexts}. Die ungenutzten Speicherbereiche bilden über {@link #nexts} eine
	 * einfach verkettete Liste. */
	transient int empty;

	/** Dieser Konstruktor initialisiert das die {@link #capacityImpl() Kapazität} mit {@code 0}. */
	public AbstractHashData() {
		this.table = AbstractHashData.EMPTY_TABLE;
		this.nexts = AbstractHashData.EMPTY_INTEGERS;
		this.customAllocator(0).apply();
	}

	/** Diese Methode wird in {@link HashEntry#getKey()} sowie {@link HashIterator#nextKey()} genutzt und gibt den Schlüssel des gegebenen Eintrags zurück.
	 *
	 * @param entryIndex Index eines Eintrags.
	 * @return Schlüssel oder {@code null}. */
	protected abstract GKey customGetKey(int entryIndex);

	/** Diese Methode wird in {@link HashIterator#nextEntry()} genutzt und gibt den Eintrag zum gegebenen Index zurück.
	 *
	 * @param entryIndex Index eines Eintrags.
	 * @return Eintrag. */
	protected Entry<GKey, GValue> customGetEntry(int entryIndex) {
		return new HashEntry<>(this, entryIndex);
	}

	/** Diese Methode wird in {@link #getImpl(Object)}, {@link #popImpl(Object)}, {@link HashEntry#getValue()} sowie {@link HashIterator#nextValue()} genutzt und
	 * gibt den Wert des gegebenen Eintrags zurück.
	 *
	 * @param entryIndex Index eines Eintrags.
	 * @return Wert oder {@code null}. */
	protected abstract GValue customGetValue(int entryIndex);

	/** Diese Methode wird von {@link #customSetKey(int, Object)} genutzt und ersetzt den Wert des gegebenen Schlüssels.
	 *
	 * @param entryIndex Index eines Eintrags.
	 * @param key neuer Schlüssel oder {@code null}. */
	protected abstract void customSetKey(int entryIndex, GKey key);

	/** Diese Methode wird von {@link #putIndexImpl(Object)} genutzt und ersetzt den Wert des gegebenen Schlüssels.
	 *
	 * @param entryIndex Index eines Eintrags.
	 * @param key neuer Schlüssel oder {@code null}.
	 * @param keyHash Streuwert des Schlüssels. */
	protected void customSetKey(int entryIndex, GKey key, int keyHash) {
		this.customSetKey(entryIndex, key);
	}

	/** Diese Methode wird in {@link #putImpl(Object, Object)} sowie {@link HashEntry#setValue(Object)} genutzt, ersetzt den Wert des gegebenen Eintrags und gibt
	 * den vorherigen Wert zurück.
	 *
	 * @param entryIndex Index eines Eintrags.
	 * @param value neuer Wert oder {@code null}.
	 * @return alter Wert oder {@code null}. */
	protected abstract GValue customSetValue(int entryIndex, GValue value);

	/** Diese Methode wird in {@link #getIndexImpl(Object)}, {@link #putIndexImpl(Object)}, {@link #popIndexImpl(Object)} sowie
	 * {@link #popEntryImpl(Object, Object)} genutzt und gibt den {@link Object#hashCode() Streuwert} des gegebenen Schlüssels zurück.
	 *
	 * @param key Schlüssel oder {@code null}.
	 * @return {@link Object#hashCode() Streuwert} des Schlüssels. */
	protected int customHash(Object key) {
		return Objects.hash(key);
	}

	/** Diese Methode wird in {@link #allocateImpl(int)} sowie {@link HashEntry#hashCode()} genutzt und gibt den {@link Object#hashCode() Streuwert} des
	 * Schlüssels des gegebenen Eintrags zurück.
	 *
	 * @param entryIndex Index eines Eintrags.
	 * @return {@link Object#hashCode() Streuwert} des Schlüssels. */
	protected int customHashKey(int entryIndex) {
		return this.customHash(this.customGetKey(entryIndex));
	}

	/** Diese Methode wird in {@link HashEntry#hashCode()} genutzt und gibt den {@link Object#hashCode() Streuwert} des Werts des gegebenen Eintrags zurück.
	 *
	 * @param entryIndex Index eines Eintrags.
	 * @return {@link Object#hashCode() Streuwert} des Werts. */
	protected int customHashValue(int entryIndex) {
		return Objects.hash(this.customGetValue(entryIndex));
	}

	/** Diese Methode wird in {@link #clearImpl()} genutzt und leert die Schlüssel- und Wertspalten. Dabei werden Objektverweise auf {@code null} gesetzt. */
	protected void customClear() {
	}

	/** Diese Methode wird in {@link #popIndexImpl(Object)}, {@link #popEntryImpl(int, int)} sowie {@link #popEntryImpl(Object, Object)} genutzt und leert den
	 * Schlüssel des gegebenen Eintrags. Dabei werden Objektverweise auf {@code null} gesetzt.
	 *
	 * @param entryIndex Index eines Eintrags. */
	protected void customClearKey(int entryIndex) {
	}

	/** Diese Methode wird in {@link #popImpl(Object)}, {@link #popEntryImpl(int, int)} sowie {@link #popEntryImpl(Object, Object)} genutzt und leert den Wert des
	 * gegebenen Eintrags. Dabei werden Objektverweise auf {@code null} gesetzt.
	 *
	 * @param entryIndex Index eines Eintrags. */
	protected void customClearValue(int entryIndex) {
	}

	/** Diese Methode wird in {@link HashEntry#equals(Object)} genutzt und gibt nur dann {@code true} zurück, wenn der {@link #customGetKey(int) Schlüssel des
	 * gegebenen Eintrags} {@link Object#equals(Object) äquivalent} zum gegebenen Schlüssel ist.
	 *
	 * @param entryIndex Index eines Eintrags.
	 * @param key Schlüssel oder {@code null}.
	 * @return Äquivalenz der Schlüssel. */
	protected boolean customEqualsKey(int entryIndex, Object key) {
		return Objects.equals(this.customGetKey(entryIndex), key);
	}

	/** Diese Methode wird in {@link #getIndexImpl(Object)}, {@link #putIndexImpl(Object)} sowie {@link #popIndexImpl(Object)} genutzt und gibt nur dann
	 * {@code true} zurück, wenn der {@link #customGetKey(int) Schlüssel des gegebenen Eintrags} {@link Object#equals(Object) äquivalent} zum gegebenen Schlüssel
	 * ist.
	 *
	 * @param entryIndex Index eines Eintrags.
	 * @param key Schlüssel oder {@code null}.
	 * @param keyHash Streuwert des gegebenen Schlüssels.
	 * @return Äquivalenz der Schlüssel. */
	protected boolean customEqualsKey(int entryIndex, Object key, int keyHash) {
		return this.customEqualsKey(entryIndex, key);
	}

	/** Diese Methode wird in {@link #hasValueImpl(Object)}, {@link #hasEntryImpl(Object, Object)}, {@link #popValueImpl(Object)},
	 * {@link #popEntryImpl(Object, Object)} sowie {@link HashEntry#equals(Object)} genutzt und gibt nur dann {@code true} zurück, wenn der
	 * {@link #customGetValue(int) Wert des gegebenen Eintrags} {@link Object#equals(Object) äquivalent} zum gegebenen Wert ist.
	 *
	 * @param entryIndex Index eines Eintrags.
	 * @param value Wert oder {@code null}.
	 * @return Äquivalenz der Werte. */
	protected boolean customEqualsValue(int entryIndex, Object value) {
		return Objects.equals(this.customGetValue(entryIndex), value);
	}

	/** Diese Methode wird von {@link #installImpl(Object)} aufgerufen und soll den gegebenen Schlüssel in den einzutragenden Schlüssel überführen.
	 * <p>
	 * Die Implementation in {@link AbstractHashData} liefert {@code key}.
	 *
	 * @param key Schlüssel zur Ermittlung des Eintrags.
	 * @return Schlüssel des neuen Eintrags. */
	protected GKey customInstallKey(GKey key) {
		return key;
	}

	/** Diese Methode wird von {@link #installImpl(Object)} aufgerufen und soll den gegebenen Schlüssel in den einzutragenden Wert überführen.
	 * <p>
	 * Die Implementation in {@link AbstractHashData} liefert {@code null}.
	 *
	 * @param key Schlüssel des neuen Eintrags.
	 * @return Wert des neuen Eintrags. */
	protected GValue customInstallValue(GKey key) {
		return null;
	}

	/** Diese Methode wird von {@link #installImpl(Object)} aufgerufen und signalisiert die Wiederverwendung des gegebenen Eintrags.
	 *
	 * @param entryIndex Index eines Eintrags. */
	protected void customReuseEntry(int entryIndex) {
	}

	/** Diese Methode wird in {@link #allocateImpl(int)} sowie {@link #AbstractHashData()} genutzt und gibt einen neuen Allokator mit der gegebenen Kapazität
	 * zurück.
	 *
	 * @param capacity Kapazität ans Anzahl der zu reservierenden Schlüssel bzw. Werte.
	 * @return Allokator. */
	protected abstract HashAllocator customAllocator(int capacity);

	/** Diese Methode sucht den Eintrag mit dem gegebenen Schlüssel und gibt dessen Position zurück. Wenn ein solcher Eintrag bereits existiert, wird dessen
	 * Wiederverwendung über {@link #customReuseEntry(int)} angezeigt. Andernfalls wird er erzeugt. Der dabei verwendete Schlüssel über
	 * {@link #customInstallKey(Object)} ermittelt. Daraus wird über {@link #customInstallValue(Object)} der initiale Wert des Eintrags abgeleitet.<br>
	 * <b>Achtung:</b> Innerhalb der Methoden {@link #customInstallKey(Object)} und {@link #customInstallValue(Object)} dürfen Einträge nicht
	 * {@link #putKeyImpl(Object) eingefügt}, {@link #popIndexImpl(Object) entfernt} oder {@link #allocateImpl(int) reserviert} werden.
	 *
	 * @param key Schlüssel des Eintrags.
	 * @return Index des gefundenen oder erzeugten Eintrags. */
	protected final int installImpl(GKey key) {
		var count = this.count;
		var index = this.putIndexImpl(key);
		if (count == this.count) {
			this.customReuseEntry(index);
		} else {
			this.customSetKey(index, key = this.customInstallKey(key));
			this.customSetValue(index, this.customInstallValue(key));
		}
		return index;
	}

	/** Diese Methode sucht den Eintrag mit dem gegebenen Schlüssel und gibt dessen Position zurück. Wenn kein solcher Eintrag existiert, wird er erzeugt. Der
	 * dabei verwendete Schlüssel wird über über {@code installKey} ermittelt. Daraus wird über {@code installValue} der initiale Wert des Eintrags
	 * abgeleitet.<br>
	 * <b>Achtung:</b> Innerhalb der Methoden {@code installKey} und {@code installValue} dürfen Einträge nicht {@link #putKeyImpl(Object) eingefügt},
	 * {@link #popIndexImpl(Object) entfernt} oder {@link #allocateImpl(int) reserviert} werden.
	 *
	 * @param key Schlüssel des Eintrags.
	 * @param installKey Methode zur Überführung des gegebenen Schlüssels in den einzutragenden Schlüssel.
	 * @param installValue Methode zur Überführung des einzutragenden Schlüssels in den einzutragenden Wert.
	 * @return Index des gefundenen oder erzeugten Eintrags. */
	protected final int installImpl(GKey key, Getter<? super GKey, ? extends GKey> installKey, Getter<? super GKey, ? extends GValue> installValue) {
		var count = this.count;
		var index = this.putIndexImpl(key);
		if (count == this.count) return index;
		var key2 = installKey.get(key);
		this.customSetKey(index, key2);
		var value = installValue.get(key2);
		this.customSetValue(index, value);
		return index;
	}

	protected final GValue updateImpl(GKey key, Getter<? super GKey, ? extends GKey> installKey, Reducer<? super GKey, GValue> updateValue) {
		var index = this.getIndexImpl(key);
		GValue value;
		if (index < 0) {
			value = updateValue.get(key = installKey.get(key), null);
			if (value == null) return null;
			index = this.putIndexImpl(key);
			this.customSetValue(index, value);
		} else {
			value = updateValue.get(key = this.customGetKey(index), this.customGetValue(index));
			if (value == null) {
				index = this.popIndexImpl(key);
				if (index < 0) return null;
				this.customClearValue(index);
				return null;
			}
		}
		this.customSetValue(index, value);
		return value;
	}

	/** Diese Methode gibt die Anzahl der Einträge zurück.
	 *
	 * @return Anzahl der Einträge. */
	protected final int countImpl() {
		return this.count;
	}

	/** Diese Methode gibt die Anzahl der aktuell verwaltbaren Einträge zurück.
	 *
	 * @return Kapazität. */
	protected final int capacityImpl() {
		return this.nexts.length;
	}

	/** Diese Methode setzt die Kapazität, sodass dieses die gegebene Anzahl an Einträgen verwaltet werden kann.
	 *
	 * @param capacity Anzahl der maximal verwaltbaren Einträge.
	 * @throws IllegalArgumentException Wenn die gegebene Kapazität kleiner als die aktuelle Anzahl an Einträgen ist. */
	protected final void allocateImpl(int capacity) throws IllegalArgumentException {
		if (capacity < this.count) throw new IllegalArgumentException();
		var oldNexts = this.nexts;
		if (oldNexts.length == capacity) return;
		var allocator = this.customAllocator(capacity);
		if (capacity == 0) {
			this.empty = 0;
			this.table = AbstractHashData.EMPTY_TABLE;
			this.nexts = AbstractHashData.EMPTY_INTEGERS;
		} else if (capacity <= AbstractHashData.MAX_CAPACITY) {
			var newMask = IAMMapping.mask(capacity);
			var oldTable = this.table;
			var newTable = new int[newMask + 1];
			var newNexts = new int[capacity];
			AbstractHashData.setupTableImpl(newTable);
			AbstractHashData.setupNextsImpl(newNexts);
			var newEntryIndex = 0;
			for (int element: oldTable) {
				for (var oldEntryIndex = element; 0 <= oldEntryIndex; oldEntryIndex = oldNexts[oldEntryIndex]) {
					var hash = this.customHashKey(oldEntryIndex);
					var index = hash & newMask;
					newNexts[newEntryIndex] = newTable[index];
					newTable[index] = newEntryIndex;
					allocator.copy(oldEntryIndex, newEntryIndex);
					newEntryIndex++;
				}
			}
			this.empty = newEntryIndex;
			this.table = newTable;
			this.nexts = newNexts;
		} else throw new OutOfMemoryError();
		allocator.apply();
	}

	/** Diese Methode sucht den Eintrag mit dem gegebenen Schlüssel und gibt nur dann {@code true} zurück, wenn ein solcher Eintrag existiert.
	 *
	 * @see Map#containsKey(Object)
	 * @param key Schlüssel des Eintrags.
	 * @return {@code true}, wenn der Eintrag gefundenen wurde. */
	protected final boolean hasKeyImpl(Object key) {
		return 0 <= this.getIndexImpl(key);
	}

	/** Diese Methode sucht einen Eintrag mit dem gegebenen Wert und gibt nur dann {@code true} zurück, wenn ein solcher Eintrag existiert.
	 *
	 * @see Map#containsValue(Object)
	 * @param value Wert des Eintrags.
	 * @return {@code true}, wenn der Eintrag gefundenen wurde. */
	protected final boolean hasValueImpl(Object value) {
		var table = this.table;
		var nexts = this.nexts;
		for (var i = table.length - 1; 0 <= i; --i) {
			for (var entryIndex = table[i]; 0 <= entryIndex; entryIndex = nexts[entryIndex]) {
				if (this.customEqualsValue(entryIndex, value)) return true;
			}
		}
		return false;
	}

	/** Diese Methode sucht den Eintrag mit dem gegebenen Schlüssel sowie Wert und gibt nur dann {@code true} zurück, wenn ein solcher Eintrag existiert.
	 *
	 * @see Set#contains(Object)
	 * @param key Schlüssel des Eintrags.
	 * @param value Wert des Eintrags.
	 * @return {@code true}, wenn der Eintrag gefundenen wurde. */
	protected final boolean hasEntryImpl(Object key, Object value) {
		var entryIndex = this.getIndexImpl(key);
		return (entryIndex >= 0) && this.customEqualsValue(entryIndex, value);
	}

	/** Diese Methode sucht den Eintrag mit dem gegebenen Schlüssel und gibt dessen Wert zurück. Wenn kein solcher Eintrag existiert, wird {@code null} geliefert.
	 *
	 * @see Map#get(Object)
	 * @param key Schlüssel des Eintrags.
	 * @return Wert des gefundenen Eintrags oder {@code null}. */
	protected final GValue getImpl(Object key) {
		var entryIndex = this.getIndexImpl(key);
		return entryIndex < 0 ? null : this.customGetValue(entryIndex);
	}

	/** Diese Methode sucht den Eintrag mit dem gegebenen Schlüssel und gibt dessen Position zurück. Wenn kein solcher Eintrag existiert, wird {@code -1}
	 * geliefert.
	 *
	 * @param key Schlüssel des Eintrags.
	 * @return Index des gefundenen Eintrags oder {@code -1}. */
	protected final int getIndexImpl(Object key) {
		return this.getIndexImpl2(key, this.customHash(key));
	}

	private int getIndexImpl2(Object key, int keyHash) {
		var table = this.table;
		var nexts = this.nexts;
		var index = keyHash & (table.length - 1);
		var entry = table[index];
		for (var result = entry; 0 <= result; result = nexts[result]) {
			if (this.customEqualsKey(result, key, keyHash)) return result;
		}
		return -1;
	}

	/** Diese Methode sucht den Eintrag mit dem gegebenen Schlüssel, setzt dessen Wert und gibt seinen vorherigen Wert zurück. Wenn kein solcher Eintrag
	 * existierte, wird {@code null} geliefert.
	 *
	 * @see Map#put(Object, Object)
	 * @param key Schlüssel des Eintrags.
	 * @param value neuer Wert des Eintrags.
	 * @return alert Wert des gefundenen Eintrags oder {@code null}. */
	protected final GValue putImpl(GKey key, GValue value) {
		var entryIndex = this.putIndexImpl(key);
		return this.customSetValue(entryIndex, value);
	}

	/** Diese Methode sucht den Eintrag mit dem gegebenen Schlüssel und gibt nur dann {@code false} zurück, wenn ein solcher Eintrag existiert. Wenn kein solcher
	 * Eintrag existierte, wird er erzeugt und {@code true} geliefert.
	 *
	 * @see Set#add(Object)
	 * @param key Schlüssel des Eintrags.
	 * @return {@code true}, wenn der Eintrag erzeugt wurde. */
	protected final boolean putKeyImpl(GKey key) {
		var count = this.count;
		this.putIndexImpl(key);
		return count != this.count;
	}

	/** Diese Methode sucht den Eintrag mit dem gegebenen Schlüssel und gibt dessen Position zurück. Wenn kein solcher Eintrag existiert, wird er erzeugt.
	 *
	 * @param key Schlüssel des Eintrags.
	 * @return Index des gefundenen oder erzeugten Eintrags. */
	protected final int putIndexImpl(GKey key) {
		var keyHash = this.customHash(key);
		var result = this.getIndexImpl2(key, keyHash);
		if (result >= 0) return result;
		var count = this.count + 1;
		var capacity = this.capacityImpl();
		if (count > AbstractHashData.MAX_CAPACITY) throw new OutOfMemoryError();
		this.count = count;
		if (count <= capacity) return this.putIndexImpl2(key, keyHash);
		var allocate = count + (count >> 1);
		this.allocateImpl((allocate < 0) || (allocate > AbstractHashData.MAX_CAPACITY) ? AbstractHashData.MAX_CAPACITY : allocate);
		return this.putIndexImpl2(key, keyHash);
	}

	private int putIndexImpl2(GKey key, int keyHash) {
		var table = this.table;
		var nexts = this.nexts;
		var index = keyHash & (table.length - 1);
		var result = this.empty;
		this.empty = nexts[result];
		nexts[result] = table[index];
		table[index] = result;
		this.customSetKey(result, key, keyHash);
		return result;
	}

	/** Diese Methode entfernt den Eintrag mit dem gegebenen Schlüssel und gibt den Wert des Eintrags zurück. Wenn kein solcher Eintrag existiert, wird
	 * {@code null} geliefert.
	 *
	 * @see Map#remove(Object)
	 * @param key Schlüssel.
	 * @return Wert oder {@code null}. */
	protected final GValue popImpl(Object key) {
		var entryIndex = this.popIndexImpl(key);
		if (entryIndex < 0) return null;
		var result = this.customGetValue(entryIndex);
		this.customClearValue(entryIndex);
		return result;
	}

	/** Diese Methode entfernt den gegeben Schlüssel und liefet nur dann {@code true}, wenn dieser zuvor über {@link #putKeyImpl(Object)} hinzugefügt wurde.
	 *
	 * @see Set#remove(Object)
	 * @param key Schlüssel.
	 * @return {@code true}, wenn der Eintrag existierte. */
	protected final boolean popKeyImpl(Object key) {
		var count = this.count;
		this.popIndexImpl(key);
		return count != this.count;
	}

	/** Diese Methode entfernt einen Eintrag mit dem gegebenen Wert.
	 *
	 * @see Map#containsValue(Object)
	 * @param value Wert des Eintrags.
	 * @return {@code true}, wenn der Eintrag gefunden und entfernt wurde. */
	protected final boolean popValueImpl(Object value) {
		var table = this.table;
		var nexts = this.nexts;
		for (var tableIndex = table.length - 1; 0 <= tableIndex; --tableIndex) {
			for (var entryIndex = table[tableIndex]; 0 <= entryIndex; entryIndex = nexts[entryIndex]) {
				if (this.customEqualsValue(entryIndex, value)) return this.popEntryImpl(tableIndex, entryIndex);
			}
		}
		return false;
	}

	/** Diese Methode entfernt den gegebenen Eintrag.
	 *
	 * @see HashIterator#remove()
	 * @param tableIndex Index der Liste in {@link #table}.
	 * @param entryIndex Index des Eintrags.
	 * @return {@code true}, wenn der Eintrag gefunden und entfernt wurde. */
	protected final boolean popEntryImpl(int tableIndex, int entryIndex) {
		var table = this.table;
		var nexts = this.nexts;
		if ((tableIndex < 0) || (entryIndex < 0)) return false;
		var prevIndex = table[tableIndex];
		if (prevIndex < 0) return false;
		if (prevIndex == entryIndex) {
			table[tableIndex] = nexts[prevIndex];
			nexts[prevIndex] = this.empty;
			this.customClearKey(prevIndex);
			this.customClearValue(prevIndex);
			this.empty = prevIndex;
			this.count--;
			return true;
		}
		for (var nextIndex = nexts[prevIndex]; 0 <= nextIndex; prevIndex = nextIndex, nextIndex = nexts[nextIndex]) {
			if (nextIndex == entryIndex) {
				nexts[prevIndex] = nexts[nextIndex];
				nexts[nextIndex] = this.empty;
				this.customClearKey(nextIndex);
				this.customClearValue(nextIndex);
				this.empty = nextIndex;
				this.count--;
				return true;
			}
		}
		return false;
	}

	/** Diese Methode entfernt den Eintrag mit dem gegebenen Schlüssel sowie dem gegebenen Wert und liefet nur dann {@code true}, wenn dieser zuvor über
	 * {@link #putImpl(Object, Object)} hinzugefügt wurde.
	 *
	 * @param key Schlüssel des Eintrags.
	 * @param value Wert des Eintrags.
	 * @return {@code true}, wenn der Eintrag existierte. */
	protected final boolean popEntryImpl(Object key, Object value) {
		var hash = this.customHash(key);
		var table = this.table;
		var nexts = this.nexts;
		var index = hash & (table.length - 1);
		var prevIndex = table[index];
		if (prevIndex < 0) return false;
		if (this.customEqualsKey(prevIndex, key, hash) && this.customEqualsValue(prevIndex, value)) {
			table[index] = nexts[prevIndex];
			nexts[prevIndex] = this.empty;
			this.customClearKey(prevIndex);
			this.customClearValue(prevIndex);
			this.empty = prevIndex;
			this.count--;
			return true;
		}
		for (var nextIndex = nexts[prevIndex]; 0 <= nextIndex; prevIndex = nextIndex, nextIndex = nexts[nextIndex]) {
			if (this.customEqualsKey(nextIndex, key, hash) && this.customEqualsValue(nextIndex, value)) {
				nexts[prevIndex] = nexts[nextIndex];
				nexts[nextIndex] = this.empty;
				this.customClearKey(nextIndex);
				this.customClearValue(nextIndex);
				this.empty = nextIndex;
				this.count--;
				return true;
			}
		}
		return false;
	}

	/** Diese Methode entfernt den Eintrag mit dem gegebenen Schlüssel und gibt dessen Position zurück. Wenn kein solcher Eintrag existiert, wird {@code -1}
	 * geliefert.
	 *
	 * @param key Schlüssel des Eintrags.
	 * @return Index des entfernten Eintrags oder {@code -1}. */
	protected final int popIndexImpl(Object key) {
		return this.popIndexImpl2(key, this.customHash(key));
	}

	private int popIndexImpl2(Object key, int keyHash) {
		var table = this.table;
		var nexts = this.nexts;
		var index = keyHash & (table.length - 1);
		var prevIndex = table[index];
		if (prevIndex < 0) return -1;
		if (this.customEqualsKey(prevIndex, key, keyHash)) {
			this.customClearKey(prevIndex);
			table[index] = nexts[prevIndex];
			nexts[prevIndex] = this.empty;
			this.empty = prevIndex;
			this.count--;
			return prevIndex;
		}
		for (var nextIndex = nexts[prevIndex]; 0 <= nextIndex; prevIndex = nextIndex, nextIndex = nexts[nextIndex]) {
			if (this.customEqualsKey(nextIndex, key, keyHash)) {
				this.customClearKey(nextIndex);
				nexts[prevIndex] = nexts[nextIndex];
				nexts[nextIndex] = this.empty;
				this.empty = nextIndex;
				this.count--;
				return nextIndex;
			}
		}

		return -1;
	}

	/** Diese Methode gibt das {@link Set} der Schlüssel zurück.
	 *
	 * @return Schlüssel. */
	protected final Keys<GKey> newKeysImpl() {
		return new Keys<>(this);
	}

	/** Diese Methode gibt den {@link Iterator} über die Schlüssel zurück.
	 *
	 * @return Interator für {@link #newKeysImpl()}. */
	protected final KeysIterator<GKey, GValue> newKeysIteratorImpl() {
		return new KeysIterator<>(this);
	}

	/** Diese Methode gibt die {@link Collection} der Werte zurück.
	 *
	 * @return Werte. */
	protected final Values<GValue> newValuesImpl() {
		return new Values<>(this);
	}

	/** Diese Methode gibt den {@link Iterator} über die Werte zurück.
	 *
	 * @return Interator für {@link #newValuesImpl()}. */
	protected final ValuesIterator<GKey, GValue> newValuesIteratorImpl() {
		return new ValuesIterator<>(this);
	}

	/** Diese Methode gibt das {@link Set} der Einträge zurück.
	 *
	 * @return Einträge. */
	protected final Entries<GKey, GValue> newEntriesImpl() {
		return new Entries<>(this);
	}

	/** Diese Methode gibt den {@link Iterator} über die Einträge zurück.
	 *
	 * @return Interator für {@link #newEntriesImpl()}. */
	protected final EntriesIterator<GKey, GValue> newEntriesIteratorImpl() {
		return new EntriesIterator<>(this);
	}

	/** Diese Methode gibt die {@link Map} zu den Schlüsseln und Werten zurück.
	 *
	 * @return Abbildung. */
	protected final Mapping<GKey, GValue> newMappingImpl() {
		return new Mapping<>(this);
	}

	/** Diese Methode entfernt alle Einträge. Hierbei werden die Anzahl der Einträge auf {@code 0} gesetzt und die Tabellen initialisiert. */
	protected final void clearImpl() {
		if (this.count == 0) return;
		AbstractHashData.setupTableImpl(this.table);
		AbstractHashData.setupNextsImpl(this.nexts);
		this.empty = 0;
		this.customClear();
		this.count = 0;
	}

	@Override
	protected AbstractHashData<GKey, GValue> clone() {
		try {
			@SuppressWarnings ("unchecked")
			var result = (AbstractHashData<GKey, GValue>)super.clone();
			if (this.capacityImpl() == 0) return result;
			result.table = this.table.clone();
			result.nexts = this.nexts.clone();
			return result;
		} catch (Exception cause) {
			throw new IllegalStateException(cause);
		}
	}

	@Override
	public long emu() {
		return EMU.fromObject(this) + EMU.fromArray(this.table) + EMU.fromArray(this.nexts);
	}

	/** Diese Methode liefert eine Abbildung, die jeder in diesem Objekt vorkommenden Anzahl von Streuwertkollissionen die Anzahl der betroffenen Datensätze
	 * zuordnet.
	 *
	 * @param permille {@code true}, wenn die Datensatzanzahl relativ in aufgerundeten Promille angegeben werden soll; {@code false}, wenn die Datensatzanzahl
	 *        absolut angegeben werden soll.
	 * @return Abbildung von Kollissionsanzahl auf Datensatzanzahl. */
	public Map<Integer, Integer> collisions(boolean permille) {
		var result = new HashMapII();
		var table = this.table;
		var nexts = this.nexts;
		var length = table.length;
		for (var index = 0; index < length; ++index) {
			var count = 0;
			for (var entry = table[index]; entry >= 0; entry = nexts[entry]) {
				count++;
			}
			if (count != 0) {
				result.add(count, count);
			}
		}
		var count = this.count;
		if (!permille || (count == 0)) return result;
		var mod = 0;
		for (var entry: result.entrySet()) {
			var val = (entry.getValue() * 1000) + mod;
			mod = val % count;
			entry.setValue(val / count);
		}
		return result;
	}

}