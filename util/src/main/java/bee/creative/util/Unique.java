package bee.creative.util;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Diese Klasse implementiert ein abstraktes Objekt zur Ermittlung und Verwaltung einzigartiger Ausgaben zu gegebenen
 * Eingaben. Hierfür werden gegebene Eingaben über eine interne {@link Map} mit berechneten Ausgaben assoziiert. Wenn
 * via {@link #get(Object)} die mit einer gegebenen Eingabe assoziierte Ausgabe ermittelt werden soll und diese Ausgabe
 * bereits via {@link #compile(Object)} erzeugt wurde, wird deren Wiederverwendung via {@link #reuse(Object, Object)}
 * signalisiert.
 * <p>
 * Als Schlüssel für die interne {@link Map} werden {@link UniqueKey}s als Wrapper der Eingaben verwendet. Wenn intern
 * eine {@link HashMap} verwendet wird, sollten {@link #hash(Object)} und {@link #equals(Object, Object)} auf die
 * Eingaben angepasst werden. Wenn dagegen eine {@link TreeMap} intern genutzt werden soll, muss nur
 * {@link #compare(Object, Object)} auf die Eingaben angepasst werden.
 * 
 * @see Unique#get(Object)
 * @see Unique#reuse(Object, Object)
 * @see Unique#compile(Object)
 * @see Unique#hash(Object)
 * @see Unique#equals(Object, Object)
 * @see Unique#compare(Object, Object)
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GInput> Typ der Eingabe.
 * @param <GOutput> Typ der Ausgabe.
 */
public abstract class Unique<GInput, GOutput> {

	/**
	 * Diese Klasse implementiert den Schlüssel, der in der {@link Map} eines {@link Unique} verwendet wird.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 */
	protected static final class UniqueKey<GInput> implements Comparable<UniqueKey<GInput>>, Comparator<GInput> {

		/**
		 * Dieses Feld speichert den Besitzer.
		 */
		protected final Unique<GInput, ?> owner;

		/**
		 * Dieses Feld speichert die Eingabe.
		 */
		protected final GInput input;

		/**
		 * Dieser Konstrukteur initialisiert Besitzer und Eingabe.
		 * 
		 * @param owner Besitzer.
		 * @param input Eingabe.
		 */
		public UniqueKey(final Unique<GInput, ?> owner, final GInput input) {
			this.owner = owner;
			this.input = input;
		}

		/**
		 * {@inheritDoc} Der {@link Comparator#compare(Object, Object) Vergleichswert} wird via
		 * {@link Unique#compare(Object, Object)} ermittelt.
		 * 
		 * @see Unique#compare(Object, Object)
		 */
		@Override
		public int compare(final GInput input1, final GInput input2) {
			return this.owner.compare(input1, input2);
		}

		/**
		 * {@inheritDoc} Der {@link Comparable#compareTo(Object) Navigationswert} wird via
		 * {@link Comparators#compare(Object, Object, Comparator)} und {@link Unique#compare(Object, Object)} ermittelt.
		 * 
		 * @see Unique#compare(Object, Object)
		 * @see Comparators#compare(Object, Object, Comparator)
		 */
		@Override
		public int compareTo(final UniqueKey<GInput> object) {
			return Comparators.compare(this.input, object.input, this);
		}

		/**
		 * {@inheritDoc} Der {@link Object#hashCode() Streuwert} wird via {@link Unique#hash(Object)} ermittelt.
		 * 
		 * @see Unique#hash(Object)
		 */
		@Override
		public int hashCode() {
			final GInput input = this.input;
			if(input == null) return 0;
			return this.owner.hash(input);
		}

		/**
		 * {@inheritDoc} Die {@link Object#equals(Object) Äquivalenz} wird via {@link Unique#equals(Object, Object)}
		 * ermittelt.
		 * 
		 * @see Unique#equals(Object, Object)
		 */
		@SuppressWarnings ({"unchecked"})
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof UniqueKey<?>)) return false;
			final GInput input1 = this.input, input2 = ((UniqueKey<GInput>)object).input;
			if(input1 == null) return input2 == null;
			return (input1 == input2) || ((input2 != null) && this.owner.equals(input1, input2));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return String.valueOf(this.input);
		}

	}

	/**
	 * Diese Klasse implementiert die {@link Map}-Sicht auf die Einträge von {@link Unique#map}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 */
	protected static final class UniqueEntryMap<GInput, GOutput> extends AbstractMap<GInput, GOutput> {

		/**
		 * Diese Klasse implementiert das {@link Set} zu {@link Map#entrySet()}.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <GInput> Typ der Eingabe.
		 * @param <GOutput> Typ der Ausgabe.
		 */
		protected static final class UniqueEntrySet<GInput, GOutput> extends AbstractSet<Entry<GInput, GOutput>> implements
			Converter<Entry<UniqueKey<GInput>, Object>, Entry<GInput, GOutput>> {

			/**
			 * Dieses Feld speichert den Besitzer.
			 */
			protected final Unique<GInput, GOutput> owner;

			/**
			 * Dieser Konstrukteur initialisiert den Besitzer.
			 * 
			 * @param owner Besitzer.
			 */
			protected UniqueEntrySet(final Unique<GInput, GOutput> owner) {
				this.owner = owner;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public int size() {
				return this.owner.map.size();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void clear() {
				this.owner.map.clear();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public boolean isEmpty() {
				return this.owner.map.isEmpty();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Iterator<Entry<GInput, GOutput>> iterator() {
				return Iterators.convertedIterator(this, this.owner.map.entrySet().iterator());
			}

			/**
			 * {@inheritDoc}
			 */
			@SuppressWarnings ("unchecked")
			@Override
			public Entry<GInput, GOutput> convert(final Entry<UniqueKey<GInput>, Object> input) {
				final Object value = input.getValue();
				if(value == Unique.NULL) return new SimpleImmutableEntry<GInput, GOutput>(input.getKey().input, null);
				return new SimpleImmutableEntry<GInput, GOutput>(input.getKey().input, (GOutput)value);
			}

		}

		/**
		 * Dieses Feld speichert den Besitzer.
		 */
		protected final Unique<GInput, GOutput> owner;

		/**
		 * Dieses Feld speichert das {@link Set} zu {@link Map#entrySet()}.
		 */
		protected final UniqueEntrySet<GInput, GOutput> entrySet;

		/**
		 * Dieser Konstrukteur initialisiert den Besitzer.
		 * 
		 * @param owner Besitzer.
		 */
		public UniqueEntryMap(final Unique<GInput, GOutput> owner) {
			this.owner = owner;
			this.entrySet = new UniqueEntrySet<GInput, GOutput>(owner);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int size() {
			return this.owner.map.size();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void clear() {
			this.owner.map.clear();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isEmpty() {
			return this.owner.map.isEmpty();
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
			if(!this.owner.check(key)) return null;
			final Object value = this.owner.map.get(new UniqueKey<GInput>(this.owner, (GInput)key));
			if(value == Unique.NULL) return null;
			return (GOutput)value;
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ("unchecked")
		@Override
		public GOutput remove(final Object key) {
			if(!this.owner.check(key)) return null;
			final Object value = this.owner.map.remove(new UniqueKey<GInput>(this.owner, (GInput)key));
			if(value == Unique.NULL) return null;
			return (GOutput)value;
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ("unchecked")
		@Override
		public boolean containsKey(final Object key) {
			return this.owner.check(key) && this.owner.map.containsKey(new UniqueKey<GInput>(this.owner, (GInput)key));
		}

	}

	/**
	 * Dieses Feld speichert den {@code null}-Wert.
	 */
	static final Object NULL = new Object();

	/**
	 * Dieses Feld speichert den Modus zur Erzeugung eines {@link Unique} mit interner {@link HashMap}.
	 */
	public static final boolean HASHMAP = false;

	/**
	 * Dieses Feld speichert den Modus zur Erzeugung eines {@link Unique} mit interner {@link TreeMap}.
	 */
	public static final boolean TREEMAP = true;

	/**
	 * Dieses Feld speichert die {@link Map}.
	 */
	protected final Map<UniqueKey<GInput>, Object> map;

	/**
	 * Dieses Feld speichert die {@link Map}-Sicht auf die internen Einträge, aus welcher zwar Einträge entfernt, aber in
	 * welche keine neuen Einträge eingefügt werden können.
	 */
	protected final Map<GInput, GOutput> entryMap;

	/**
	 * Dieser Konstrukteur initialisiert die interne {@link Map}.
	 * 
	 * @param map {@link Map}.
	 * @throws NullPointerException Wenn die gegebene {@link Map} {@code null} ist.
	 */
	public Unique(final Map<UniqueKey<GInput>, Object> map) throws NullPointerException {
		if(map == null) throw new NullPointerException("map is null");
		this.map = map;
		this.entryMap = new UniqueEntryMap<GInput, GOutput>(this);
	}

	/**
	 * Dieser Konstrukteur initialisiert die interne {@link Map} dem gegebenen Modus entsprechend mit einer
	 * {@link HashMap} oder einer {@link TreeMap}.
	 * 
	 * @see Unique#HASHMAP
	 * @see Unique#TREEMAP
	 * @see HashMap
	 * @see TreeMap
	 * @param mode Modus ({@link Unique#HASHMAP} oder {@link Unique#TREEMAP}).
	 */
	public Unique(final boolean mode) {
		this(mode ? new TreeMap<UniqueKey<GInput>, Object>() : new HashMap<UniqueKey<GInput>, Object>());
	}

	/**
	 * Diese Methode gibt die mit der gegebenen Eingabe assoziierte Ausgabe zurück. Wenn der gegebenen Eingabe bereits
	 * eine Ausgabe zugeordnet ist, wird deren Wiederverwendung via {@link Unique#reuse(Object, Object)} signalisiert.
	 * Sollte der Eingabe jedoch noch keine Ausgabe zugeordnet sein, wird diese via {@link Unique#compile(Object)} erzeugt
	 * und mit der Eingabe assoziiert.
	 * 
	 * @see Unique#hash(Object)
	 * @see Unique#equals(Object, Object)
	 * @see Unique#compare(Object, Object)
	 * @param input Eingabe oder {@code null}.
	 * @return Ausgabe oder {@code null}.
	 */
	@SuppressWarnings ("unchecked")
	public GOutput get(final GInput input) {
		final UniqueKey<GInput> key = new UniqueKey<GInput>(this, input);
		final Object value = this.map.get(key);
		if(value == null){
			final GOutput output = this.compile(input);
			this.map.put(key, ((output == null) ? Unique.NULL : output));
			return output;
		}
		if(value == Unique.NULL){
			this.reuse(input, null);
			return null;
		}
		final GOutput output = (GOutput)value;
		this.reuse(input, output);
		return output;
	}

	/**
	 * Diese Methode gibt die {@link Map}-Sicht auf die internen Einträge zurück, aus welcher zwar Einträge entfernt, aber
	 * in welche keine neuen Einträge eingefügt werden können.
	 * 
	 * @return {@link Map}-Sicht.
	 */
	public Map<GInput, GOutput> entryMap() {
		return this.entryMap;
	}

	/**
	 * Diese Methode gibt nur dann {@code true} zurück, wenn das gegebene {@link Object} gültig ist und nach
	 * {@code GInput} gekastet werden kann. Sie wird von der {@link Map}-Sich {@link #entryMap()} verwendet.
	 * 
	 * @param input {@link Object}.
	 * @return {@code true}, wenn {@code input instanceOf GInput}.
	 */
	protected boolean check(final Object input) {
		return true;
	}

	/**
	 * Diese Methode wird bei der Wiederverwendung der gegebenen Ausgabe für die gegebene Eingabe von aufgerufen.
	 * 
	 * @see Unique#get(Object)
	 * @param input Eingabe oder {@code null}
	 * @param output Ausgabe oder {@code null}.
	 */
	protected void reuse(final GInput input, final GOutput output) {
	}

	/**
	 * Diese Methode kompiliert die gegebene Eingabe in die zugeordnete Ausgabe und gibt diese zurück.
	 * 
	 * @see Unique#get(Object)
	 * @param input Eingabe oder {@code null}.
	 * @return Ausgabe oder {@code null}.
	 */
	protected abstract GOutput compile(GInput input);

	/**
	 * Diese Methode gibt den {@link Object#hashCode() Streuwert} der gegebenen Eingabe zurück. Der berechnete
	 * {@link Object#hashCode() Streuwert} wird von den Schlüsseln der {@link Map} verwendet. Die Eingabe ist nie
	 * {@code null}.
	 * 
	 * @see UniqueKey#hashCode()
	 * @param input Eingabe.
	 * @return {@link Object#hashCode() Streuwert}.
	 */
	protected int hash(final GInput input) {
		return input.hashCode();
	}

	/**
	 * Diese Methode gibt die {@link Object#equals(Object) Äquivalenz} der gegebenen Eingaben zurück. Die berechnete
	 * {@link Object#equals(Object) Äquivalenz} wird von den Schlüsseln der {@link Map} verwendet. Die Eingaben sind nie
	 * {@code null}.
	 * 
	 * @see UniqueKey#equals(Object)
	 * @param input1 Eingabe 1.
	 * @param input2 Eingabe 2.
	 * @return {@link Object#equals(Object) Äquivalenz} der gegebenen Eingaben.
	 */
	protected boolean equals(final GInput input1, final GInput input2) {
		return Objects.equals(input1, input2);
	}

	/**
	 * Diese Methode gibt eine Zahl kleiner als, gleich oder größer als {@code 0} zurück, wenn das erste Objekt kleienr
	 * als, gleich bzw. größer als das zweite Objekt ist. Die berechnete {@link Comparator#compare(Object, Object)
	 * Vergleichswert} wird von den Schlüsseln der {@link Map} verwendet. Die Eingaben sind nie {@code null}.
	 * 
	 * @see UniqueKey#compareTo(UniqueKey)
	 * @param input1 Eingabe 1.
	 * @param input2 Eingabe 2.
	 * @return Vergleichswert.
	 */
	protected int compare(final GInput input1, final GInput input2) {
		return Comparators.compare(this.hash(input1), this.hash(input2));
	}

}
