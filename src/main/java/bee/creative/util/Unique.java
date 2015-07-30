package bee.creative.util;

import java.util.AbstractMap;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Diese Klasse implementiert ein abstraktes Objekt zur Ermittlung und Verwaltung einzigartiger Ausgaben zu gegebenen Eingaben. Hierfür werden gegebene Eingaben
 * über eine interne {@link Data Abbildung} mit {@link #compile(Object) berechneten} Ausgaben assoziiert. Wenn via {@link #get(Object)} die mit einer gegebenen
 * Eingabe assoziierte Ausgabe ermittelt werden soll und diese Ausgabe zuvor bereits via {@link #compile(Object)} erzeugt wurde, wird deren Wiederverwendung via
 * {@link #reuse(Object, Object)} signalisiert.
 * 
 * @see Unique#get(Object)
 * @see Unique#reuse(Object, Object)
 * @see Unique#compile(Object)
 * @see Hasher
 * @see Converter
 * @see Comparator
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GInput> Typ der Eingabe.
 * @param <GOutput> Typ der Ausgabe.
 */
public abstract class Unique<GInput, GOutput> implements Hasher<GInput>, Converter<GInput, GOutput>, Comparator<GInput>, Iterable<GOutput> {

	/**
	 * Diese Schnittstelle definiert ein Objekt zur Haltung der Einträge eines {@link Unique}s.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 */
	public static interface Data<GInput, GOutput> extends Iterable<Entry<GInput, GOutput>> {

		/**
		 * Diese Methode gibt die Ausgabe zurück, die der gegebenen Eingabe zugeordnet ist.
		 * 
		 * @param key Eingabe.
		 * @return Ausgabe oder {@code null}.
		 */
		public GOutput get(GInput key);

		/**
		 * Diese Methode ordnet der gegebenen Eingabe die gegebene Ausgabe zu. Wenn die Ausgabe {@code null} ist, wird die Zuordnung entfernt.
		 * 
		 * @param key Eingabe.
		 * @param value Ausgabe oder {@code null}.
		 */
		public void set(GInput key, GOutput value);

		/**
		 * Diese Methode gibt die Anzahl der Einträge zurück.
		 * 
		 * @return Anzahl der Einträge.
		 */
		public int size();

		/**
		 * Diese Methode gibt einen {@link Iterator} über die Paare aus Ein- und Ausgaben zurück.
		 */
		@Override
		public Iterator<Entry<GInput, GOutput>> iterator();

	}

	/**
	 * Diese Klasse implementiert das {@link Set} zu {@link Unique#data}.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 */
	static final class EntrySet<GInput, GOutput> extends AbstractSet<Entry<GInput, GOutput>> {

		/**
		 * Dieses Feld speichert den Besitzer.
		 */
		final Unique<GInput, GOutput> owner;

		/**
		 * Dieser Konstruktor initialisiert den Besitzer.
		 * 
		 * @param owner Besitzer.
		 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist.
		 */
		public EntrySet(final Unique<GInput, GOutput> owner) throws NullPointerException {
			if (owner == null) throw new NullPointerException();
			this.owner = owner;
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int size() {
			return this.owner.data.size();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<Entry<GInput, GOutput>> iterator() {
			return this.owner.data.iterator();
		}

	}

	/**
	 * Diese Klasse implementiert die {@link Map}-Sicht auf die Einträge von {@link Unique#data}.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 */
	static final class EntryMap<GInput, GOutput> extends AbstractMap<GInput, GOutput> {

		/**
		 * Dieses Feld speichert den Besitzer.
		 */
		final Unique<GInput, GOutput> owner;

		/**
		 * Dieses Feld speichert das {@link Set} zu {@link Map#entrySet()}.
		 */
		final EntrySet<GInput, GOutput> entrySet;

		/**
		 * Dieser Konstruktor initialisiert den Besitzer.
		 * 
		 * @param owner Besitzer.
		 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist.
		 */
		public EntryMap(final Unique<GInput, GOutput> owner) throws NullPointerException {
			if (owner == null) throw new NullPointerException();
			this.owner = owner;
			this.entrySet = new EntrySet<GInput, GOutput>(owner);
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int size() {
			return this.owner.data.size();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Set<Entry<GInput, GOutput>> entrySet() {
			return this.entrySet;
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ("unchecked")
		@Override
		public GOutput get(final Object key) {
			if (!this.owner.check(key)) return null;
			return this.owner.data.get((GInput)key);
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ("unchecked")
		@Override
		public GOutput remove(final Object key) {
			if (!this.owner.check(key)) return null;
			final GOutput value = this.owner.data.get((GInput)key);
			if (value == null) return null;
			this.owner.data.set((GInput)key, null);
			return value;
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ("unchecked")
		@Override
		public boolean containsKey(final Object key) {
			return this.owner.check(key) && (this.owner.data.get((GInput)key) != null);
		}

	}

	/**
	 * Diese Klasse implementiert ein abstraktes Objekt zur Verwaltung der Einträge eines {@link Unique}s in {@link List}s mit Zugriff über eine binäre Suche.
	 * 
	 * @see Unique#compare(Object, Object)
	 * @see Comparables#binarySearch(List, Comparable)
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 */
	protected static abstract class ListData<GInput, GOutput> implements Data<GInput, GOutput> {

		/**
		 * Dieses Feld speichert den Besitzer.
		 */
		protected final Unique<GInput, GOutput> owner;

		/**
		 * Dieses Feld speichert die Eingaben.
		 */
		protected final List<GInput> inputs;

		/**
		 * Dieses Feld speichert die Ausgaben.
		 */
		protected final List<GOutput> outputs;

		/**
		 * Dieser Konstruktor initialisiert Besitzer, Eingaben und Ausgaben.
		 * 
		 * @param owner Besitzer.
		 * @param inputs Eingaben.
		 * @param outputs Ausgaben.
		 * @throws NullPointerException Wenn eines der Argumente {@code null} ist.
		 */
		public ListData(final Unique<GInput, GOutput> owner, final List<GInput> inputs, final List<GOutput> outputs) throws NullPointerException {
			if ((owner == null) || (inputs == null) || (outputs == null)) throw new NullPointerException();
			this.owner = owner;
			this.inputs = inputs;
			this.outputs = outputs;
		}

		{}

		/**
		 * Diese Methode ermittelt via {@link Comparables#binarySearch(List, Comparable)} den Index der gegebenen Eingabe in {@link #inputs} und gibt diesen oder
		 * <code>(-(<i>Einfügeposition</i>)-1)</code> zurück.
		 * 
		 * @param input Eingabe.
		 * @return Index oder <code>(-(<i>Einfügeposition</i>)-1)</code>.
		 */
		protected int index(final GInput input) {
			return Comparables.binarySearch(this.inputs, Comparables.itemComparable(input, this.owner));
		}

		/**
		 * Diese Methode fügt die gegebene Ein- und Ausgabe an der gegebenen Position in {@link #inputs} bzw. {@link #outputs} ein.
		 * 
		 * @param index Position.
		 * @param key Eingabe.
		 * @param value Ausgebe.
		 */
		protected abstract void insert(final int index, final GInput key, final GOutput value);

		/**
		 * Diese Methode entfernt den Eintrag an der gegebenen Position aus {@link #inputs} und {@link #outputs}.
		 * 
		 * @param index Position.
		 */
		protected abstract void remove(final int index);

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GOutput get(final GInput key) {
			final int index = this.index(key);
			return index >= 0 ? this.outputs.get(index) : null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void set(final GInput key, final GOutput value) {
			int index = this.index(key);
			if (index < 0) {
				if (value == null) return;
				index = -index - 1;
				this.insert(index, key, value);
			} else if (value == null) {
				this.remove(index);
			} else {
				this.outputs.set(index, value);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int size() {
			return this.inputs.size();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<Entry<GInput, GOutput>> iterator() {
			return new Iterator<Entry<GInput, GOutput>>() {

				int index = 0;

				int count = ListData.this.size();

				@Override
				public boolean hasNext() {
					return this.index < this.count;
				}

				@Override
				public Entry<GInput, GOutput> next() {
					final int index = this.index++;
					return new SimpleImmutableEntry<GInput, GOutput>(ListData.this.inputs.get(index), ListData.this.outputs.get(index));
				}

				@Override
				public void remove() {
					ListData.this.remove(this.index - 1);
					this.count--;
				}

			};
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this);
		}

	}

	/**
	 * Diese Klasse implementiert ein abstraktes Objekt zur Verwaltung der Einträge eines {@link Unique.UniqueSet}s in {@link List}s mit Zugriff über eine binäre
	 * Suche.
	 * 
	 * @see Unique#compare(Object, Object)
	 * @see Comparables#binarySearch(List, Comparable)
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GValue> Typ der Werte.
	 */
	protected static final class ListSetData<GValue> extends ListData<GValue, GValue> {

		/**
		 * Dieser Konstruktor initialisiert Besitzer und Werte.
		 * 
		 * @param owner Besitzer.
		 * @param values Werte.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public ListSetData(final Unique<GValue, GValue> owner, final List<GValue> values) throws NullPointerException {
			super(owner, values, values);
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void insert(final int index, final GValue key, final GValue value) {
			this.inputs.add(index, key);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void remove(final int index) {
			this.inputs.remove(index);
		}

	}

	/**
	 * Diese Klasse implementiert ein abstraktes Objekt zur Verwaltung der Einträge einer {@link Unique.UniqueMap} in {@link List}s mit Zugriff über eine binäre
	 * Suche.
	 * 
	 * @see Unique#compare(Object, Object)
	 * @see Comparables#binarySearch(List, Comparable)
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 */
	protected static final class ListMapData<GInput, GOutput> extends ListData<GInput, GOutput> {

		/**
		 * Dieser Konstruktor initialisiert Besitzer, Eingaben und Ausgaben.
		 * 
		 * @param owner Besitzer.
		 * @param inputs Eingaben.
		 * @param outputs Ausgaben.
		 * @throws NullPointerException Wenn eines der Argumente {@code null} ist.
		 */
		public ListMapData(final Unique<GInput, GOutput> owner, final List<GInput> inputs, final List<GOutput> outputs) throws NullPointerException {
			super(owner, inputs, outputs);
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void insert(final int index, final GInput key, final GOutput value) {
			this.inputs.add(index, key);
			this.outputs.add(index, value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void remove(final int index) {
			this.inputs.remove(index);
			this.outputs.remove(index);
		}

	}

	/**
	 * Diese Klasse implementiert ein abstraktes Objekt zur Verwaltung der Einträge eines {@link Unique}s in einem {@link Hash}.
	 * 
	 * @see Unique#hash(Object)
	 * @see Unique#equals(Object, Object)
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param <GEntry> Typ der Einträge.
	 */
	protected static abstract class HashData<GInput, GOutput, GEntry> extends Hash<GInput, GOutput, GEntry> implements Data<GInput, GOutput>,
		Converter<GEntry, Entry<GInput, GOutput>> {

		/**
		 * Dieses Feld speichert den Besitzer.
		 */
		protected final Unique<GInput, GOutput> owner;

		/**
		 * Dieser Konstruktor initialisiert den Besitzer.
		 * 
		 * @param owner Besitzer.
		 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist.
		 */
		public HashData(final Unique<GInput, GOutput> owner) throws NullPointerException {
			if (owner == null) throw new NullPointerException();
			this.owner = owner;
			this.verifyLength(16);
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected int getKeyHash(final GInput key) {
			return this.owner.hash(key);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GOutput get(final GInput input) {
			final GEntry entry = this.findEntry(input);
			return entry != null ? this.getEntryValue(entry) : null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void set(final GInput input, final GOutput output) {
			if (output != null) {
				this.appendEntry(input, output, true);
			} else {
				this.removeEntry(input, true);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int size() {
			return this.getSize();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<Entry<GInput, GOutput>> iterator() {
			return Iterators.convertedIterator(this, this.getEntries());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Entry<GInput, GOutput> convert(final GEntry input) {
			return new SimpleImmutableEntry<GInput, GOutput>(this.getEntryKey(input), this.getEntryValue(input));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this);
		}

	}

	/**
	 * Diese Klasse implementiert ein abstraktes Objekt zur Verwaltung der Einträge eines {@link Unique.UniqueSet}s in einem {@link Hash}.
	 * 
	 * @see Unique#hash(Object)
	 * @see Unique#equals(Object, Object)
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GValue> Typ der Werte.
	 */
	protected static final class HashSetData<GValue> extends HashData<GValue, GValue, HashSetData.Entry<GValue>> {

		/**
		 * Diese Klasse implementiert einen Eintrag mit Wert.
		 * 
		 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <GValue> Typ des Werts.
		 */
		static final class Entry<GValue> {

			/**
			 * Dieses Feld speichert den nächsten Eintrag oder {@code null}.
			 */
			Entry<GValue> next;

			/**
			 * Dieses Feld speichert den {@link Unique#hashCode() Streuwert} von {@link #value}.
			 */
			int hash;

			/**
			 * Dieses Feld speichert den Wert.
			 */
			GValue value;

			{}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public String toString() {
				return this.value.toString();
			}

		}

		{}

		/**
		 * Dieser Konstruktor initialisiert den Besitzer.
		 * 
		 * @param owner Besitzer.
		 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist.
		 */
		public HashSetData(final Unique<GValue, GValue> owner) throws NullPointerException {
			super(owner);
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected GValue getEntryKey(final Entry<GValue> entry) {
			return entry.value;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected Entry<GValue> getEntryNext(final Entry<GValue> entry) {
			return entry.next;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void setEntryNext(final Entry<GValue> entry, final Entry<GValue> next) {
			entry.next = next;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected GValue getEntryValue(final Entry<GValue> entry) {
			return entry.value;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean getEntryEquals(final Entry<GValue> entry, final GValue key, final int hash) {
			return (entry.hash == hash) && this.owner.equals(entry.value, key);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected Entry<GValue> createEntry(final GValue key, final GValue value, final Entry<GValue> next, final int hash) {
			final Entry<GValue> entry = new Entry<GValue>();
			entry.next = next;
			entry.hash = hash;
			entry.value = value;
			return entry;
		}

	}

	/**
	 * Diese Klasse implementiert ein abstraktes Objekt zur Verwaltung der Einträge einer {@link Unique.UniqueMap} in einem {@link Hash}.
	 * 
	 * @see Unique#hash(Object)
	 * @see Unique#equals(Object, Object)
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 */
	protected static final class HashMapData<GInput, GOutput> extends HashData<GInput, GOutput, HashMapData.Entry<GInput, GOutput>> {

		/**
		 * Diese Klasse implementiert einen Eintrag mit Ein- und Ausgabe.
		 * 
		 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <GInput> Typ der Eingabe.
		 * @param <GOutput> Typ der Ausgabe.
		 */
		static final class Entry<GInput, GOutput> {

			/**
			 * Dieses Feld speichert den nächsten Eintrag oder {@code null}.
			 */
			Entry<GInput, GOutput> next;

			/**
			 * Dieses Feld speichert den {@link Unique#hashCode() Streuwert} von {@link #input}.
			 */
			int hash;

			/**
			 * Dieses Feld speichert die Eingabe.
			 */
			GInput input;

			/**
			 * Dieses Feld speichert die Ausgabe.
			 */
			GOutput output;

			{}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public String toString() {
				return this.input + "=" + this.output;
			}

		}

		/**
		 * Dieser Konstruktor initialisiert den Besitzer.
		 * 
		 * @param owner Besitzer.
		 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist.
		 */
		public HashMapData(final Unique<GInput, GOutput> owner) throws NullPointerException {
			super(owner);
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected GInput getEntryKey(final Entry<GInput, GOutput> entry) {
			return entry.input;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected Entry<GInput, GOutput> getEntryNext(final Entry<GInput, GOutput> entry) {
			return entry.next;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void setEntryNext(final Entry<GInput, GOutput> entry, final Entry<GInput, GOutput> next) {
			entry.next = next;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected GOutput getEntryValue(final Entry<GInput, GOutput> entry) {
			return entry.output;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean getEntryEquals(final bee.creative.util.Unique.HashMapData.Entry<GInput, GOutput> entry, final GInput key, final int hash) {
			return (entry.hash == hash) && this.owner.equals(entry.input, key);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected Entry<GInput, GOutput> createEntry(final GInput key, final GOutput value, final Entry<GInput, GOutput> next, final int hash) {
			final Entry<GInput, GOutput> entry = new Entry<GInput, GOutput>();
			entry.next = next;
			entry.hash = hash;
			entry.input = key;
			entry.output = value;
			return entry;
		}

	}

	/**
	 * Diese Klasse implementiert ein {@link Unique} mit gleichem Typ für Ein- und Ausgabe.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GValue> Typ der Werte.
	 */
	public static class UniqueSet<GValue> extends Unique<GValue, GValue> {

		/**
		 * Dieser Konstruktor initialisiert die Nutzdaten mit {@link Unique.HashSetData}.
		 * 
		 * @see Unique.HashSetData
		 */
		public UniqueSet() {
			this.data = new HashSetData<GValue>(this);
		}

		/**
		 * Dieser Konstruktor initialisiert die Nutzdaten mit {@link Unique.HashSetData} oder {@link Unique.ListSetData}.
		 * 
		 * @see Unique#LISTDATA
		 * @see Unique#HASHDATA
		 * @see Unique.HashSetData
		 * @see Unique.ListSetData
		 * @param mode {@code true}, wenn {@link Unique.ListMapData} verwendet werden soll.
		 */
		public UniqueSet(final boolean mode) {
			this.data = mode ? new ListSetData<GValue>(this, new ArrayList<GValue>()) : new HashSetData<GValue>(this);
		}

		/**
		 * Dieser Konstruktor initialisiert die Nutzdaten mit {@link Unique.ListSetData} und den gegebenen Werten.
		 * 
		 * @see Unique.ListSetData
		 * @param values Werte.
		 * @throws NullPointerException Wenn die gegebenen Werte {@code null} sind.
		 */
		public UniqueSet(final List<GValue> values) throws NullPointerException {
			this.data = new ListSetData<GValue>(this, values);
		}

		/**
		 * Dieser Konstruktor initialisiert die Nutzdaten mit den gegebenen {@link Unique.Data}.
		 * 
		 * @param data {@link Unique.Data}.
		 * @throws NullPointerException Wenn die gegebenen {@link Unique.Data} {@code null} ist.
		 */
		public UniqueSet(final Data<GValue, GValue> data) throws NullPointerException {
			if (data == null) throw new NullPointerException();
			this.data = data;
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected GValue compile(final GValue input) {
			return input;
		}

	}

	/**
	 * Diese Klasse implementiert ein {@link Unique} mit unterschiedlichen Typen für Ein- und Ausgabe.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 */
	public static abstract class UniqueMap<GInput, GOutput> extends Unique<GInput, GOutput> {

		/**
		 * Dieser Konstruktor initialisiert die Nutzdaten mit {@link Unique.HashMapData}.
		 * 
		 * @see Unique.HashMapData
		 */
		public UniqueMap() {
			this.data = new HashMapData<GInput, GOutput>(this);
		}

		/**
		 * Dieser Konstruktor initialisiert die Nutzdaten mit {@link Unique.HashMapData} oder {@link Unique.ListMapData}.
		 * 
		 * @see Unique#LISTDATA
		 * @see Unique#HASHDATA
		 * @see Unique.HashMapData
		 * @see Unique.ListMapData
		 * @param mode {@code true}, wenn {@link Unique.ListMapData} verwendet werden soll.
		 */
		public UniqueMap(final boolean mode) {
			this.data = mode ? new ListMapData<GInput, GOutput>(this, new ArrayList<GInput>(), new ArrayList<GOutput>()) : new HashMapData<GInput, GOutput>(this);
		}

		/**
		 * Dieser Konstruktor initialisiert die Nutzdaten mit {@link Unique.ListMapData} und den gegebenen Eingaben und Ausgaben.
		 * 
		 * @see Unique.ListMapData
		 * @param inputs Eingaben.
		 * @param outputs Ausgaben.
		 * @throws NullPointerException Wenn eines der Argumente {@code null} ist.
		 */
		public UniqueMap(final List<GInput> inputs, final List<GOutput> outputs) throws NullPointerException {
			this.data = new ListMapData<GInput, GOutput>(this, inputs, outputs);
		}

		/**
		 * Dieser Konstruktor initialisiert die Nutzdaten mit den gegebenen {@link Unique.Data}.
		 * 
		 * @param data {@link Unique.Data}.
		 * @throws NullPointerException Wenn die gegebenen {@link Unique.Data} {@code null} ist.
		 */
		public UniqueMap(final Data<GInput, GOutput> data) throws NullPointerException {
			if (data == null) throw new NullPointerException();
			this.data = data;
		}

	}

	{}

	/**
	 * Dieses Feld speichert den Modus zur Erzeugung eines {@link Unique} mit interner {@code List}-{@link Data}.
	 * 
	 * @see ListSetData
	 * @see ListMapData
	 */
	public static final boolean LISTDATA = true;

	/**
	 * Dieses Feld speichert den Modus zur Erzeugung eines {@link Unique} mit interner {@code Hash}-{@link Data}.
	 * 
	 * @see HashSetData
	 * @see HashMapData
	 */
	public static final boolean HASHDATA = false;

	{}

	/**
	 * Dieses Feld speichert die {@link Data}.
	 */
	protected Data<GInput, GOutput> data;

	/**
	 * Dieses Feld speichert die {@link Map}-Sicht auf die internen Einträge, aus welcher zwar Einträge entfernt, aber in welche keine neuen Einträge eingefügt
	 * werden können.
	 */
	protected final Map<GInput, GOutput> entryMap = new EntryMap<GInput, GOutput>(this);

	{}

	/**
	 * Diese Methode gibt nur dann {@code true} zurück, wenn ein {@code cast} des gegebenen {@link Object}s nach {@code GInput} zulässig ist. Sie wird von der
	 * {@link Map}-Sich {@link #entryMap()} verwendet.
	 * 
	 * @param input {@link Object}.
	 * @return {@code true}, wenn {@code input instanceOf GInput}.
	 */
	protected boolean check(final Object input) {
		return true;
	}

	/**
	 * Diese Methode wird bei der Wiederverwendung der gegebenen Ausgabe für die gegebene Eingabe von aufgerufen. Ein- und Ausgabe sind nie {@code null}.
	 * 
	 * @see Unique#get(Object)
	 * @param input Eingabe
	 * @param output Ausgabe.
	 */
	protected void reuse(final GInput input, final GOutput output) {
	}

	/**
	 * Diese Methode kompiliert die gegebene Eingabe in die zugeordnete Ausgabe und gibt diese zurück. Die Eingabe ist nie {@code null}.
	 * 
	 * @see Unique#get(Object)
	 * @param input Eingabe.
	 * @return Ausgabe.
	 */
	protected abstract GOutput compile(GInput input);

	/**
	 * Diese Methode gibt die mit der gegebenen Eingabe assoziierte Ausgabe zurück. Wenn der gegebenen Eingabe bereits eine Ausgabe zugeordnet ist, w ird deren
	 * Wiederverwendung via {@link Unique#reuse(Object, Object)} signalisiert. Sollte der Eingabe jedoch noch keine Ausgabe zugeordnet sein, wird diese via
	 * {@link Unique#compile(Object)} erzeugt und mit der Eingabe assoziiert.
	 * 
	 * @see Unique#hash(Object)
	 * @see Unique#equals(Object, Object)
	 * @see Unique#compare(Object, Object)
	 * @param input Eingabe.
	 * @return Ausgabe.
	 * @throws NullPointerException Wenn die gegebene Eingabe oder die erzeugte Ausgabe {@code null} sind.
	 */
	@SuppressWarnings ("unchecked")
	public GOutput get(final GInput input) throws NullPointerException {
		if (input == null) throw new NullPointerException();
		final Object value = this.data.get(input);
		if (value == null) {
			final GOutput output = this.compile(input);
			if (output == null) throw new NullPointerException();
			this.data.set(input, output);
			return output;
		}
		final GOutput output = (GOutput)value;
		this.reuse(input, output);
		return output;
	}

	/**
	 * Diese Methode gibt die {@link Map}-Sicht auf die internen Einträge zurück, aus welcher zwar Einträge entfernt, aber in welche keine neuen Einträge
	 * eingefügt werden können.
	 * 
	 * @return {@link Map}-Sicht.
	 */
	public Map<GInput, GOutput> entryMap() {
		return this.entryMap;
	}

	{}

	/**
	 * Der berechnete {@link Object#hashCode() Streuwert} wird von den Schlüsseln der {@link Map} verwendet. Die Eingabe ist nie {@code null}.
	 * <p>
	 * {@inheritDoc}
	 * 
	 * @see HashSetData
	 * @see HashMapData
	 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
	 */
	@Override
	public int hash(final GInput input) throws NullPointerException {
		return input.hashCode();
	}

	/**
	 * Die berechnete {@link Object#equals(Object) Äquivalenz} wird von den Schlüsseln der {@link Map} verwendet.
	 * <p>
	 * {@inheritDoc}
	 * 
	 * @see HashSetData
	 * @see HashMapData
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
	 */
	@Override
	public boolean equals(final GInput input1, final GInput input2) throws NullPointerException {
		return Objects.equals(input1, input2);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<GOutput> iterator() {
		return this.entryMap.values().iterator();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Der Rückgabewert entspricht {@code this.get(input)}.
	 */
	@Override
	public GOutput convert(final GInput input) {
		return this.get(input);
	}

	/**
	 * Die berechnete {@link Comparator#compare(Object, Object) Vergleichswert} wird von den Schlüsseln der {@link Map} verwendet.
	 * <p>
	 * {@inheritDoc}
	 * 
	 * @see ListSetData
	 * @see ListMapData
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
	 */
	@Override
	public int compare(final GInput input1, final GInput input2) throws NullPointerException {
		return Comparators.compare(this.hash(input1), this.hash(input2));
	}

}
