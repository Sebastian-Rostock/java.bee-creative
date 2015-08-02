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
 * Eingabe assoziierte Ausgabe ermittelt werden soll und diese Ausgabe zuvor bereits {@link #compile(Object) erzeugt} wurde, wird deren Wiederverwendung via
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
	 * Diese Schnittstelle definiert die Verwaltung der Einträge eines {@link Unique} als Abbildung von Eingaben auf Ausgaben, analog zu einer {@link Map}.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 */
	public static interface Data<GInput, GOutput> extends Iterable<Entry<GInput, GOutput>> {

		/**
		 * Diese Methode wählt die gegebene Eingabe.
		 * 
		 * @param input Eingabe.
		 * @throws RuntimeException Wenn {@code input} ungültig ist, z.B. {@code null}.
		 */
		public void forInput(GInput input) throws RuntimeException;

		/**
		 * Diese Methode gibt die Ausgabe zur gewählten Eingabe zurück.
		 * 
		 * @see #forInput(Object)
		 * @see #hasOutput()
		 * @return Ausgabe.
		 * @throws RuntimeException Wenn keine Eingabe {@link #forInput(Object) gewählt} ist oder keine Ausgabe {@link #hasOutput() existiert}.
		 */
		public GOutput getOutput() throws RuntimeException;

		/**
		 * Diese Methode gibt nur dann {@code true} zurück, wenn zur {@link #forInput(Object) gewählten} Eingabe eine Ausgabe existiert.
		 * 
		 * @see #forInput(Object)
		 * @return {@code true}, wenn eine Ausgabe existiert.
		 */
		public boolean hasOutput();

		/**
		 * Diese Methode ordnet der {@link #forInput(Object) gewählten} Eingabe die gegebene Ausgabe zu.
		 * 
		 * @see #forInput(Object)
		 * @param output Ausgabe.
		 * @throws RuntimeException Wenn {@code output} ungültig ist, z.B. {@code null}.
		 */
		public void setOutput(GOutput output) throws RuntimeException;

		/**
		 * Diese Methode entfernt die Zuordnung der {@link #forInput(Object) gewählten} Eingabe zu ihrer Ausgabe.
		 * 
		 * @see #forInput(Object)
		 */
		public void popOutput();

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
	 * Diese Klasse implementiert eine abstrakte Verwaltung der Einträge eines {@link Unique} in sortierten Listen mit Zugriff über eine binäre Suche.
	 * 
	 * @see Unique#compare(Object, Object)
	 * @see Comparables#binarySearch(List, Comparable)
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 */
	public static abstract class BaseListData<GInput, GOutput> implements Data<GInput, GOutput> {

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
		 * Dieses Feld speichert die gewählte Eingabe.
		 */
		protected GInput input;

		/**
		 * Dieses Feld speichert den Index der Eingabe.
		 */
		protected int index;

		/**
		 * Dieser Konstruktor initialisiert Besitzer, Eingaben und Ausgaben.
		 * 
		 * @param owner Besitzer.
		 * @param inputs Eingaben.
		 * @param outputs Ausgaben.
		 * @throws NullPointerException Wenn {@code owner}, {@code inputs} bzw. {@code outputs} {@code null} ist.
		 */
		public BaseListData(final Unique<GInput, GOutput> owner, final List<GInput> inputs, final List<GOutput> outputs) throws NullPointerException {
			if (owner == null) throw new NullPointerException("owner = null");
			if (inputs == null) throw new NullPointerException("inputs = null");
			if (outputs == null) throw new NullPointerException("outputs = null");
			this.owner = owner;
			this.inputs = inputs;
			this.outputs = outputs;
		}

		{}

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
		public void forInput(final GInput input) throws RuntimeException {
			this.index = Comparables.binarySearch(this.inputs, Comparables.itemComparable(input, this.owner));
			this.input = input;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GOutput getOutput() throws RuntimeException {
			final int index = this.index;
			if (index < 0) throw new IllegalStateException();
			return this.outputs.get(index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasOutput() {
			return this.index >= 0;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setOutput(final GOutput output) throws RuntimeException {
			int index = this.index;
			if (index < 0) {
				index = -index - 1;
				this.insert(index, this.input, output);
				this.input = null;
			} else {
				this.outputs.set(index, output);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void popOutput() {
			final int index = this.index;
			if (index < 0) return;
			this.remove(index);
			this.input = null;
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

				int count = BaseListData.this.size();

				@Override
				public boolean hasNext() {
					return this.index < this.count;
				}

				@Override
				public Entry<GInput, GOutput> next() {
					final int index = this.index++;
					return new SimpleImmutableEntry<>(BaseListData.this.inputs.get(index), BaseListData.this.outputs.get(index));
				}

				@Override
				public void remove() {
					BaseListData.this.remove(this.index - 1);
					this.count--;
				}

			};
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toInvokeString(this);
		}

	}

	/**
	 * Diese Klasse implementiert eine abstrakte Verwaltung der Einträge eines {@link Unique} in einem {@link Hash}.
	 * 
	 * @see Unique#hash(Object)
	 * @see Unique#equals(Object, Object)
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param <GEntry> Typ der Einträge.
	 */
	public static abstract class BaseHashData<GInput, GOutput, GEntry> extends Hash<GInput, GOutput, GEntry> implements Data<GInput, GOutput>,
		Converter<GEntry, Entry<GInput, GOutput>> {

		/**
		 * Dieses Feld speichert den Besitzer.
		 */
		protected final Unique<GInput, GOutput> owner;

		/**
		 * Dieses Feld speichert die gewählte Eingabe.
		 */
		protected GInput input;

		/**
		 * Dieses Feld speichert den gewählten Eintrag.
		 */
		protected GEntry entry;

		/**
		 * Dieser Konstruktor initialisiert den Besitzer.
		 * 
		 * @param owner Besitzer.
		 * @throws NullPointerException Wenn {@code owner} {@code null} ist.
		 */
		public BaseHashData(final Unique<GInput, GOutput> owner) throws NullPointerException {
			if (owner == null) throw new NullPointerException("owner = null");
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
		public void forInput(final GInput input) throws RuntimeException {
			this.entry = this.findEntry(input);
			this.input = input;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GOutput getOutput() throws RuntimeException {
			final GEntry entry = this.entry;
			if (entry == null) throw new IllegalStateException();
			return this.getEntryValue(entry);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasOutput() {
			return this.entry != null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setOutput(final GOutput output) {
			this.appendEntry(this.input, output, true);
			this.input = null;
			this.entry = null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void popOutput() {
			final GEntry entry = this.entry;
			if (entry == null) return;
			this.removeEntry(this.input, true);
			this.input = null;
			this.entry = null;
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
			return Objects.toInvokeString(this);
		}

	}

	/**
	 * Diese Klasse implementiert eine Verwaltung der Einträge eines {@link Unique.UniqueSet} Listen mit Zugriff über eine binäre Suche.
	 * 
	 * @see Unique#compare(Object, Object)
	 * @see Comparables#binarySearch(List, Comparable)
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GValue> Typ der Werte.
	 */
	public static final class ListSetData<GValue> extends BaseListData<GValue, GValue> {

		/**
		 * Dieser Konstruktor initialisiert den Besitzer.
		 * 
		 * @param owner Besitzer.
		 * @throws NullPointerException Wenn {@code owner} {@code null} ist.
		 */
		public ListSetData(final Unique<GValue, GValue> owner) throws NullPointerException {
			this(owner, new ArrayList<GValue>());
		}

		/**
		 * Dieser Konstruktor initialisiert Besitzer und Werte.
		 * 
		 * @param owner Besitzer.
		 * @param values Werte.
		 * @throws NullPointerException Wenn {@code owner} bzw. {@code values} {@code null} ist.
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
			this.inputs.add(index, value);
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
	 * Diese Klasse implementiert die Verwaltung der Einträge einer {@link Unique.UniqueMap} in Listen mit Zugriff über eine binäre Suche.
	 * 
	 * @see Unique#compare(Object, Object)
	 * @see Comparables#binarySearch(List, Comparable)
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 */
	public static final class ListMapData<GInput, GOutput> extends BaseListData<GInput, GOutput> {

		/**
		 * Dieser Konstruktor initialisiert den Besitzer.
		 * 
		 * @param owner Besitzer.
		 * @throws NullPointerException Wenn {@code owner} {@code null} ist.
		 */
		public ListMapData(final Unique<GInput, GOutput> owner) throws NullPointerException {
			this(owner, new ArrayList<GInput>(), new ArrayList<GOutput>());
		}

		/**
		 * Dieser Konstruktor initialisiert Besitzer, Eingaben und Ausgaben.
		 * 
		 * @param owner Besitzer.
		 * @param inputs Eingaben.
		 * @param outputs Ausgaben.
		 * @throws NullPointerException Wenn {@code owner}, {@code inputs} bzw. {@code outputs} {@code null} ist.
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
	 * Diese Klasse implementiert eine Verwaltung der Einträge eines {@link Unique.UniqueSet} in einem {@link Hash}.
	 * 
	 * @see Unique#hash(Object)
	 * @see Unique#equals(Object, Object)
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GValue> Typ der Werte.
	 */
	public static final class HashSetData<GValue> extends BaseHashData<GValue, GValue, Hash.SetEntry<GValue>> {

		/**
		 * Dieser Konstruktor initialisiert den Besitzer.
		 * 
		 * @param owner Besitzer.
		 * @throws NullPointerException Wenn {@code owner} {@code null} ist.
		 */
		public HashSetData(final Unique<GValue, GValue> owner) throws NullPointerException {
			super(owner);
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected GValue getEntryKey(final SetEntry<GValue> entry) {
			return entry.value;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected SetEntry<GValue> getEntryNext(final SetEntry<GValue> entry) {
			return entry.next;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void setEntryNext(final SetEntry<GValue> entry, final SetEntry<GValue> next) {
			entry.next = next;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected GValue getEntryValue(final SetEntry<GValue> entry) {
			return entry.value;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean getEntryEquals(final SetEntry<GValue> entry, final GValue key, final int hash) {
			return (entry.hash == hash) && this.owner.equals(entry.value, key);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected SetEntry<GValue> createEntry(final GValue key, final GValue value, final SetEntry<GValue> next, final int hash) {
			final SetEntry<GValue> entry = new SetEntry<>();
			entry.next = next;
			entry.hash = hash;
			entry.value = value;
			return entry;
		}

	}

	/**
	 * Diese Klasse implementiert die Verwaltung der Einträge einer {@link Unique.UniqueMap} in einem {@link Hash}.
	 * 
	 * @see Unique#hash(Object)
	 * @see Unique#equals(Object, Object)
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 */
	public static final class HashMapData<GInput, GOutput> extends BaseHashData<GInput, GOutput, Hash.MapEntry<GInput, GOutput>> {

		/**
		 * Dieser Konstruktor initialisiert den Besitzer.
		 * 
		 * @param owner Besitzer.
		 * @throws NullPointerException Wenn {@code owner} {@code null} ist.
		 */
		public HashMapData(final Unique<GInput, GOutput> owner) throws NullPointerException {
			super(owner);
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected GInput getEntryKey(final MapEntry<GInput, GOutput> entry) {
			return entry.input;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected MapEntry<GInput, GOutput> getEntryNext(final MapEntry<GInput, GOutput> entry) {
			return entry.next;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void setEntryNext(final MapEntry<GInput, GOutput> entry, final MapEntry<GInput, GOutput> next) {
			entry.next = next;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected GOutput getEntryValue(final MapEntry<GInput, GOutput> entry) {
			return entry.output;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean getEntryEquals(final bee.creative.util.Unique.HashMapData.MapEntry<GInput, GOutput> entry, final GInput key, final int hash) {
			return (entry.hash == hash) && this.owner.equals(entry.input, key);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected MapEntry<GInput, GOutput> createEntry(final GInput key, final GOutput value, final MapEntry<GInput, GOutput> next, final int hash) {
			final MapEntry<GInput, GOutput> entry = new MapEntry<GInput, GOutput>();
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
			this(Unique.HASH);
		}

		/**
		 * Dieser Konstruktor initialisiert die Nutzdaten mit {@link Unique.HashSetData} oder {@link Unique.ListSetData}.
		 * 
		 * @see Unique#LIST
		 * @see Unique#HASH
		 * @see Unique.HashSetData
		 * @see Unique.ListSetData
		 * @param mode {@code true}, wenn {@link Unique.ListSetData} bzw. {@code false}, wenn {@link Unique.HashSetData} verwendet werden soll.
		 */
		public UniqueSet(final boolean mode) {
			this.data = mode ? new ListSetData<>(this) : new HashSetData<>(this);
		}

		/**
		 * Dieser Konstruktor initialisiert die Nutzdaten mit {@link Unique.ListSetData} und den gegebenen Werten.
		 * 
		 * @see Unique.ListSetData
		 * @param values Werte.
		 * @throws NullPointerException Wenn {@code values} {@code null} sind.
		 */
		public UniqueSet(final List<GValue> values) throws NullPointerException {
			if (values == null) throw new NullPointerException("values = null");
			this.data = new ListSetData<GValue>(this, values);
		}

		/**
		 * Dieser Konstruktor initialisiert die Nutzdaten mit den gegebenen {@link Unique.Data}.
		 * 
		 * @param data Nutzdaten.
		 * @throws NullPointerException Wenn {@code data} {@code null} ist.
		 */
		public UniqueSet(final Data<GValue, GValue> data) throws NullPointerException {
			if (data == null) throw new NullPointerException("data = null");
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
			this(Unique.HASH);
		}

		/**
		 * Dieser Konstruktor initialisiert die Nutzdaten mit {@link Unique.HashMapData} oder {@link Unique.ListMapData}.
		 * 
		 * @see Unique#LIST
		 * @see Unique#HASH
		 * @see Unique.HashMapData
		 * @see Unique.ListMapData
		 * @param mode {@code true}, wenn {@link Unique.ListMapData} bzw. {@code false}, wenn {@link Unique.HashMapData} verwendet werden soll.
		 */
		public UniqueMap(final boolean mode) {
			this.data = mode ? new ListMapData<>(this) : new HashMapData<>(this);
		}

		/**
		 * Dieser Konstruktor initialisiert die Nutzdaten mit {@link Unique.ListMapData} und den gegebenen Eingaben und Ausgaben.
		 * 
		 * @see Unique.ListMapData
		 * @param inputs Eingaben.
		 * @param outputs Ausgaben.
		 * @throws NullPointerException Wenn {@code inputs} bzw. {@code outputs} {@code null} ist.
		 */
		public UniqueMap(final List<GInput> inputs, final List<GOutput> outputs) throws NullPointerException {
			this.data = new ListMapData<>(this, inputs, outputs);
		}

		/**
		 * Dieser Konstruktor initialisiert die Nutzdaten mit den gegebenen {@link Unique.Data}.
		 * 
		 * @param data {@link Unique.Data}.
		 * @throws NullPointerException Wenn {@code data} {@code null} ist.
		 */
		public UniqueMap(final Data<GInput, GOutput> data) throws NullPointerException {
			if (data == null) throw new NullPointerException("data = null");
			this.data = data;
		}

	}

	{}

	/**
	 * Dieses Feld speichert den Modus zur Erzeugung eines {@link Unique} mit interner {@code List}-{@link Data}.<br>
	 * Derartige Nutzdaten basieren auf {@link #compare(Object, Object)}.
	 * 
	 * @see ListSetData
	 * @see ListMapData
	 */
	public static final boolean LIST = true;

	/**
	 * Dieses Feld speichert den Modus zur Erzeugung eines {@link Unique} mit interner {@code Hash}-{@link Data}.<br>
	 * Derartige Nutzdaten basieren auf {@link #hash(Object)} und {@link #equals(Object, Object)}.
	 * 
	 * @see HashSetData
	 * @see HashMapData
	 */
	public static final boolean HASH = false;

	{}

	/**
	 * Dieses Feld speichert die {@link Data}.
	 */
	protected Data<GInput, GOutput> data;

	/**
	 * Dieses Feld speichert das {@link Set} zu {@link Map#entrySet()}.
	 */
	protected final Set<Entry<GInput, GOutput>> entrySet = new AbstractSet<Entry<GInput, GOutput>>() {

		@Override
		public int size() {
			return Unique.this.data.size();
		}

		@Override
		public Iterator<Entry<GInput, GOutput>> iterator() {
			return Unique.this.data.iterator();
		}

	};

	/**
	 * Dieses Feld speichert die {@link Map}-Sicht auf die internen Einträge, aus welcher zwar Einträge entfernt, aber in welche keine neuen Einträge eingefügt
	 * werden können.
	 */
	protected final Map<GInput, GOutput> entryMap = new AbstractMap<GInput, GOutput>() {

		@Override
		public int size() {
			return Unique.this.data.size();
		}

		@Override
		public Set<Entry<GInput, GOutput>> entrySet() {
			return Unique.this.entrySet;
		}

		@SuppressWarnings ("unchecked")
		@Override
		public GOutput get(final Object key) {
			if (!Unique.this.check(key)) return null;
			final Data<GInput, GOutput> data = Unique.this.data;
			data.forInput((GInput)key);
			if (!data.hasOutput()) return null;
			return data.getOutput();
		}

		@SuppressWarnings ("unchecked")
		@Override
		public GOutput remove(final Object key) {
			if (!Unique.this.check(key)) return null;
			final Data<GInput, GOutput> data = Unique.this.data;
			data.forInput((GInput)key);
			if (!data.hasOutput()) return null;
			final GOutput value = data.getOutput();
			data.popOutput();
			return value;
		}

		@SuppressWarnings ("unchecked")
		@Override
		public boolean containsKey(final Object key) {
			if (!Unique.this.check(key)) return false;
			final Data<GInput, GOutput> data = Unique.this.data;
			data.forInput((GInput)key);
			return data.hasOutput();
		}

	};

	{}

	/**
	 * Diese Methode gibt nur dann {@code true} zurück, wenn ein {@code cast} des gegebenen {@link Object} nach {@code GInput} zulässig ist. Sie wird von der
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
	 * Diese Methode gibt die mit der gegebenen Eingabe assoziierte Ausgabe zurück. Wenn der gegebenen Eingabe bereits eine Ausgabe zugeordnet
	 * {@link Data#hasOutput() ist}, wird deren Wiederverwendung via {@link Unique#reuse(Object, Object)} signalisiert. Sollte der Eingabe noch keine Ausgabe
	 * zugeordnet sein, wird diese via {@link Unique#compile(Object)} erzeugt und mit der Eingabe {@link Data#setOutput(Object) assoziiert}.
	 * 
	 * @see Unique#hash(Object)
	 * @see Unique#equals(Object, Object)
	 * @see Unique#compare(Object, Object)
	 * @param input Eingabe.
	 * @return Ausgabe.
	 * @throws RuntimeException Wenn die gegebene Eingabe bzw. die erzeugte Ausgabe ungültig ist.
	 */
	public GOutput get(final GInput input) throws RuntimeException {
		final Data<GInput, GOutput> data = this.data;
		data.forInput(input);
		if (data.hasOutput()) {
			final GOutput output = data.getOutput();
			this.reuse(input, output);
			return output;
		} else {
			final GOutput output = this.compile(input);
			data.setOutput(output);
			return output;
		}
	}

	/**
	 * Diese Methode gibt eine {@link Map} als Sicht auf die internen Einträge zurück, aus welcher zwar Einträge entfernt, aber in welche keine neuen Einträge
	 * eingefügt werden können.
	 * 
	 * @return {@link Map} der Einträge.
	 */
	public Map<GInput, GOutput> entryMap() {
		return this.entryMap;
	}

	{}

	/**
	 * Der berechnete {@link Object#hashCode() Streuwert} wird von den Schlüsseln der {@link Map} verwendet.
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
	 * Der berechnete {@link Comparator#compare(Object, Object) Vergleichswert} wird von den Schlüsseln der {@link Map} verwendet.
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return Objects.toInvokeString(this, this.data);
	}

}
