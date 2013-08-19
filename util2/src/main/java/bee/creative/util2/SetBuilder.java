package bee.creative.util2;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import bee.creative.util.Builder;
import bee.creative.util.Iterables;

/**
 * Diese Klasse implementiert Methoden zur Bereitstellung eines {@link Set}-{@link Builder}s.
 * 
 * @see SetBuilder1
 * @see SetBuilder2
 * @see SetBuilder3
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class SetBuilder {

	/**
	 * Diese Schnittstelle definiert einen {@link Set}-{@link Builder}, der ein konfiguriertes {@link Set} in ein {@code checked}-, {@code synchronized}- oder {@code unmodifiable}-{@link Set} umwandeln sowie durch das Hinzufügen von Werten modifizieren kann.
	 * 
	 * @see Collections#checkedSet(Set, Class)
	 * @see Collections#synchronizedSet(Set)
	 * @see Collections#unmodifiableSet(Set)
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GValue> Typ der Werte.
	 * @param <GSet> Typ des {@link Set}.
	 */
	public static interface SetBuilder1<GValue, GSet extends Set<GValue>> extends ValuesBuilder<GValue>, SetBuilder3<GValue, Set<GValue>> {

		/**
		 * {@inheritDoc}
		 * 
		 * @return {@link Set}-{@link Builder}.
		 */
		@Override
		public SetBuilder1<GValue, GSet> add(GValue value);

		/**
		 * {@inheritDoc}
		 * 
		 * @return {@link Set}-{@link Builder}.
		 */
		@Override
		public SetBuilder1<GValue, GSet> addAll(GValue... value);

		/**
		 * {@inheritDoc}
		 * 
		 * @return {@link Set}-{@link Builder}.
		 */
		@Override
		public SetBuilder1<GValue, GSet> addAll(Iterable<? extends GValue> value);

		/**
		 * {@inheritDoc}
		 * 
		 * @return {@link Set}-{@link Builder}.
		 */
		@Override
		public SetBuilder1<GValue, GSet> remove(GValue value);

		/**
		 * {@inheritDoc}
		 * 
		 * @return {@link Set}-{@link Builder}.
		 */
		@Override
		public SetBuilder1<GValue, GSet> removeAll(GValue... value);

		/**
		 * {@inheritDoc}
		 * 
		 * @return {@link Set}-{@link Builder}.
		 */
		@Override
		public SetBuilder1<GValue, GSet> removeAll(Iterable<? extends GValue> value);

		/**
		 * {@inheritDoc}
		 */
		@Override
		public SetBuilder1<GValue, Set<GValue>> asCheckedSet(Class<GValue> type);

		/**
		 * {@inheritDoc}
		 */
		@Override
		public SetBuilder1<GValue, Set<GValue>> asSynchronizedSet();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public SetBuilder3<GValue, Set<GValue>> asUnmodifiableSet();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GSet build();

	}

	/**
	 * Diese Schnittstelle definiert einen {@link SortedSet}-{@link Builder}, der ein konfiguriertes {@link SortedSet} in ein {@code checked}-, {@code synchronized}- oder {@code unmodifiable}-{@link Set} umwandeln sowie durch das Hinzufügen von Werten modifizieren kann.
	 * 
	 * @see Collections#checkedSortedSet(SortedSet, Class)
	 * @see Collections#synchronizedSortedSet(SortedSet)
	 * @see Collections#unmodifiableSortedSet(SortedSet)
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GValue> Typ der Werte.
	 * @param <GSet> Typ des {@link Set}.
	 */
	public static interface SetBuilder2<GValue, GSet extends SortedSet<GValue>> extends ValuesBuilder<GValue>, SetBuilder3<GValue, SortedSet<GValue>> {

		/**
		 * {@inheritDoc}
		 * 
		 * @return {@link Set}-{@link Builder}.
		 */
		@Override
		public SetBuilder2<GValue, GSet> add(GValue value);

		/**
		 * {@inheritDoc}
		 * 
		 * @return {@link Set}-{@link Builder}.
		 */
		@Override
		public SetBuilder2<GValue, GSet> addAll(GValue... value);

		/**
		 * {@inheritDoc}
		 * 
		 * @return {@link Set}-{@link Builder}.
		 */
		@Override
		public SetBuilder2<GValue, GSet> addAll(Iterable<? extends GValue> value);

		/**
		 * {@inheritDoc}
		 * 
		 * @return {@link Set}-{@link Builder}.
		 */
		@Override
		public SetBuilder2<GValue, GSet> remove(GValue value);

		/**
		 * {@inheritDoc}
		 * 
		 * @return {@link Set}-{@link Builder}.
		 */
		@Override
		public SetBuilder2<GValue, GSet> removeAll(GValue... value);

		/**
		 * {@inheritDoc}
		 * 
		 * @return {@link Set}-{@link Builder}.
		 */
		@Override
		public SetBuilder2<GValue, GSet> removeAll(Iterable<? extends GValue> value);

		/**
		 * Diese Methode konvertiert das konfigurierte {@link Set} via {@link Collections#checkedSortedSet(SortedSet, Class)}.
		 * 
		 * @see Collections#checkedSortedSet(SortedSet, Class)
		 * @param type {@link Class} der Werte.
		 * @return {@link Set}-{@link Builder}.
		 */
		@Override
		public SetBuilder2<GValue, SortedSet<GValue>> asCheckedSet(Class<GValue> type);

		/**
		 * {@inheritDoc}
		 */
		@Override
		public SetBuilder2<GValue, SortedSet<GValue>> asSynchronizedSet();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public SetBuilder2<GValue, SortedSet<GValue>> asUnmodifiableSet();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GSet build();

	}

	/**
	 * Diese Schnittstelle definiert einen {@link Set}-{@link Builder}, der ein konfiguriertes {@link Set} in ein {@code checked}-, {@code synchronized}- oder {@code unmodifiable}-{@link Set} umwandeln kann.
	 * 
	 * @see Collections#checkedSet(Set, Class)
	 * @see Collections#synchronizedSet(Set)
	 * @see Collections#unmodifiableSet(Set)
	 * @see Collections#checkedSortedSet(SortedSet, Class)
	 * @see Collections#synchronizedSortedSet(SortedSet)
	 * @see Collections#unmodifiableSortedSet(SortedSet)
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GValue> Typ der Werte.
	 * @param <GSet> Typ des {@link Set}.
	 */
	public static interface SetBuilder3<GValue, GSet extends Set<GValue>> extends Builder<GSet> {

		/**
		 * Diese Methode konvertiert das konfigurierte {@link Set} via {@link Collections#checkedSet(Set, Class)} bzw. {@link Collections#checkedSortedSet(SortedSet, Class)}.
		 * 
		 * @see Collections#checkedSet(Set, Class)
		 * @see Collections#checkedSortedSet(SortedSet, Class)
		 * @param type {@link Class} der Werte.
		 * @return {@link Set}-{@link Builder}.
		 */
		public SetBuilder3<GValue, GSet> asCheckedSet(Class<GValue> type);

		/**
		 * Diese Methode konvertiert das konfigurierte {@link Set} via {@link Collections#synchronizedSet(Set)} bzw. {@link Collections#synchronizedSortedSet(SortedSet)}.
		 * 
		 * @see Collections#synchronizedSet(Set)
		 * @see Collections#synchronizedSortedSet(SortedSet)
		 * @return {@link Set}-{@link Builder}.
		 */
		public SetBuilder3<GValue, GSet> asSynchronizedSet();

		/**
		 * Diese Methode konvertiert das konfigurierte {@link Set} via {@link Collections#unmodifiableSet(Set)} bzw. {@link Collections#unmodifiableSortedSet(SortedSet)}.
		 * 
		 * @see Collections#unmodifiableSet(Set)
		 * @see Collections#unmodifiableSortedSet(SortedSet)
		 * @return {@link Set}-{@link Builder}.
		 */
		public SetBuilder3<GValue, GSet> asUnmodifiableSet();

		/**
		 * Diese Methode gibt das konfigurierte {@link Set} zurück.
		 * 
		 * @return {@link Set}.
		 */
		@Override
		public GSet build() throws IllegalStateException;

	}

	/**
	 * Diese Klasse implementiert einen {@link SetBuilder1}.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GValue> Typ der Werte.
	 * @param <GSet> Typ des {@link Set}.
	 */
	static final class SetBuilderAImpl<GValue, GSet extends Set<GValue>> implements SetBuilder1<GValue, GSet> {

		/**
		 * Dieses Feld speichert das {@link Set}.
		 */
		final GSet set;

		/**
		 * Dieser Konstruktor initialisiert das {@link Set}.
		 * 
		 * @param set {@link Set}.
		 */
		public SetBuilderAImpl(final GSet set) {
			this.set = set;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public SetBuilderAImpl<GValue, GSet> add(final GValue value) {
			this.set.add(value);
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public SetBuilderAImpl<GValue, GSet> addAll(final GValue... value) {
			this.set.addAll(Arrays.asList(value));
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public SetBuilderAImpl<GValue, GSet> addAll(final Iterable<? extends GValue> value) {
			Iterables.appendAll(this.set, value);
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public SetBuilderAImpl<GValue, GSet> remove(final GValue value) {
			this.set.remove(value);
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public SetBuilderAImpl<GValue, GSet> removeAll(final GValue... value) {
			this.set.removeAll(Arrays.asList(value));
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public SetBuilderAImpl<GValue, GSet> removeAll(final Iterable<? extends GValue> value) {
			Iterables.appendAll(this.set, value);
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public SetBuilderAImpl<GValue, Set<GValue>> asCheckedSet(final Class<GValue> type) {
			return new SetBuilderAImpl<GValue, Set<GValue>>(Collections.checkedSet(this.set, type));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public SetBuilderAImpl<GValue, Set<GValue>> asSynchronizedSet() {
			return new SetBuilderAImpl<GValue, Set<GValue>>(Collections.synchronizedSet(this.set));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public SetBuilderAImpl<GValue, Set<GValue>> asUnmodifiableSet() {
			return new SetBuilderAImpl<GValue, Set<GValue>>(Collections.unmodifiableSet(this.set));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GSet build() {
			return this.set;
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link SetBuilder2}.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GValue> Typ der Werte.
	 * @param <GSet> Typ des {@link Set}.
	 */
	static final class SetBuilderBImpl<GValue, GSet extends SortedSet<GValue>> implements SetBuilder2<GValue, GSet> {

		/**
		 * Dieses Feld speichert das {@link Set}.
		 */
		final GSet set;

		/**
		 * Dieser Konstruktor initialisiert das {@link Set}.
		 * 
		 * @param set {@link Set}.
		 */
		public SetBuilderBImpl(final GSet set) {
			this.set = set;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public SetBuilderBImpl<GValue, GSet> add(final GValue value) {
			this.set.add(value);
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public SetBuilderBImpl<GValue, GSet> addAll(final GValue... value) {
			this.set.addAll(Arrays.asList(value));
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public SetBuilderBImpl<GValue, GSet> addAll(final Iterable<? extends GValue> value) {
			Iterables.appendAll(this.set, value);
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public SetBuilderBImpl<GValue, GSet> remove(final GValue value) {
			this.set.remove(value);
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public SetBuilderBImpl<GValue, GSet> removeAll(final GValue... value) {
			this.set.removeAll(Arrays.asList(value));
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public SetBuilderBImpl<GValue, GSet> removeAll(final Iterable<? extends GValue> value) {
			Iterables.appendAll(this.set, value);
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public SetBuilderBImpl<GValue, SortedSet<GValue>> asCheckedSet(final Class<GValue> type) {
			return new SetBuilderBImpl<GValue, SortedSet<GValue>>(Collections.checkedSortedSet(this.set, type));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public SetBuilderBImpl<GValue, SortedSet<GValue>> asSynchronizedSet() {
			return new SetBuilderBImpl<GValue, SortedSet<GValue>>(Collections.synchronizedSortedSet(this.set));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public SetBuilderBImpl<GValue, SortedSet<GValue>> asUnmodifiableSet() {
			return new SetBuilderBImpl<GValue, SortedSet<GValue>>(Collections.unmodifiableSortedSet(this.set));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GSet build() {
			return this.set;
		}

	}

	/**
	 * Diese Methode gibt einen neuen {@link SetBuilder} zurück.
	 * 
	 * @return {@link SetBuilder}.
	 */
	public static SetBuilder use() {
		return new SetBuilder();
	}

	/**
	 * Diese Methode gibt einen {@link Set}-{@link Builder} für das gegebene {@link Set} zurück.
	 * 
	 * @param <GValue> Typ der Werte.
	 * @param <GSet> Typ des {@link Set}.
	 * @param set {@link Set}.
	 * @return {@link Set}-{@link Builder}.
	 * @throws NullPointerException Wenn das gegebene {@link Set} {@code null} ist.
	 */
	public <GValue, GSet extends Set<GValue>> SetBuilder1<GValue, GSet> set(final GSet set) throws NullPointerException {
		if(set == null) throw new NullPointerException();
		return new SetBuilderAImpl<GValue, GSet>(set);
	}

	/**
	 * Diese Methode gibt einen {@link Set}-{@link Builder} für das gegebene {@link SortedSet} zurück.
	 * 
	 * @param <GValue> Typ der Werte.
	 * @param <GSet> Typ des {@link SortedSet}.
	 * @param set {@link SortedSet}.
	 * @return {@link SortedSet}-{@link Builder}.
	 * @throws NullPointerException Wenn das gegebene {@link SortedSet} {@code null} ist.
	 */
	public <GValue, GSet extends SortedSet<GValue>> SetBuilder2<GValue, GSet> sortedSet(final GSet set) throws NullPointerException {
		if(set == null) throw new NullPointerException();
		return new SetBuilderBImpl<GValue, GSet>(set);
	}

	/**
	 * Diese Methode einen neuen {@link TreeSet}-{@link Builder} zurück.
	 * 
	 * @param <GValue> Typ der Werte.
	 * @return {@link TreeSet}-{@link Builder}.
	 */
	public <GValue extends Comparable<?>> SetBuilder2<GValue, TreeSet<GValue>> treeSet() {
		return this.sortedSet(new TreeSet<GValue>());
	}

	/**
	 * Diese Methode einen neuen {@link TreeSet}-{@link Builder} zurück.
	 * 
	 * @param <GValue> Typ der Werte.
	 * @param comparator {@link Comparator}.
	 * @return {@link TreeSet}-{@link Builder}.
	 */
	public <GValue> SetBuilder2<GValue, TreeSet<GValue>> treeSet(final Comparator<? super GValue> comparator) {
		return this.sortedSet(new TreeSet<GValue>(comparator));
	}

	/**
	 * Diese Methode einen neuen {@link HashSet}-{@link Builder} zurück.
	 * 
	 * @param <GValue> Typ der Werte.
	 * @return {@link HashSet}-{@link Builder}.
	 */
	public <GValue> SetBuilder1<GValue, HashSet<GValue>> hashSet() {
		return this.set(new HashSet<GValue>());
	}

	/**
	 * Diese Methode einen neuen {@link HashSet}-{@link Builder} zurück.
	 * 
	 * @param <GValue> Typ der Werte.
	 * @param capacity Kapazität.
	 * @return {@link HashSet}-{@link Builder}.
	 * @throws IllegalArgumentException Wenn die gegebene Kapazität negativ ist.
	 */
	public <GValue> SetBuilder1<GValue, HashSet<GValue>> hashSet(final int capacity) throws IllegalArgumentException {
		return this.set(new HashSet<GValue>(capacity));
	}

	/**
	 * Diese Methode einen neuen {@link HashSet}-{@link Builder} zurück.
	 * 
	 * @param <GValue> Typ der Werte.
	 * @param capacity Kapazität.
	 * @param factor Ladefaktor.
	 * @return {@link HashSet}-{@link Builder}.
	 * @throws IllegalArgumentException Wenn eine der Eingaben negativ ist.
	 */
	public <GValue> SetBuilder1<GValue, HashSet<GValue>> hashSet(final int capacity, final float factor) throws IllegalArgumentException {
		return this.set(new HashSet<GValue>(capacity, factor));
	}

	/**
	 * Diese Methode einen neuen {@link LinkedHashSet}-{@link Builder} zurück.
	 * 
	 * @param <GValue> Typ der Werte.
	 * @return {@link LinkedHashSet}-{@link Builder}.
	 */
	public <GValue> SetBuilder1<GValue, LinkedHashSet<GValue>> linkedHashSet() {
		return this.set(new LinkedHashSet<GValue>());
	}

	/**
	 * Diese Methode einen neuen {@link LinkedHashSet}-{@link Builder} zurück.
	 * 
	 * @param <GValue> Typ der Werte.
	 * @param capacity Kapazität.
	 * @return {@link LinkedHashSet}-{@link Builder}.
	 * @throws IllegalArgumentException Wenn die gegebene Kapazität negativ ist.
	 */
	public <GValue> SetBuilder1<GValue, LinkedHashSet<GValue>> linkedHashSet(final int capacity) throws IllegalArgumentException {
		return this.set(new LinkedHashSet<GValue>(capacity));
	}

	/**
	 * Diese Methode einen neuen {@link LinkedHashSet}-{@link Builder} zurück.
	 * 
	 * @param <GValue> Typ der Werte.
	 * @param capacity Kapazität.
	 * @param factor Ladefaktor.
	 * @return {@link LinkedHashSet}-{@link Builder}.
	 * @throws IllegalArgumentException Wenn eine der Eingaben negativ ist.
	 */
	public <GValue> SetBuilder1<GValue, LinkedHashSet<GValue>> linkedHashSet(final int capacity, final float factor) throws IllegalArgumentException {
		return this.set(new LinkedHashSet<GValue>(capacity, factor));
	}

}