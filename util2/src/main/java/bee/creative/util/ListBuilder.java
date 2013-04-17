package bee.creative.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Diese Klasse implementiert Methoden zur Bereitstellung eines {@link List}-{@link Builder}s.
 * 
 * @see ListBuilder1
 * @see ListBuilder2
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class ListBuilder {

	/**
	 * Diese Schnittstelle definiert einen {@link List}-{@link Builder}, der eine konfigurierte {@link List} in eine {@code reverse}-, {@code checked}-, {@code synchronized}- oder {@code unmodifiable}-{@link List} umwandeln sowie durch das Hinzufügen von Werten modifizieren kann.
	 * 
	 * @see bee.creative.util.Collections#reverseList(List)
	 * @see Collections#checkedList(List, Class)
	 * @see Collections#synchronizedList(List)
	 * @see Collections#unmodifiableList(List)
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GValue> Typ der Werte.
	 * @param <GList> Typ der {@link List}.
	 */
	public static interface ListBuilder1<GValue, GList extends List<GValue>> extends ValuesBuilder<GValue>, ListBuilder2<GValue, GList> {

		/**
		 * {@inheritDoc}
		 * 
		 * @return {@link List}-{@link Builder}.
		 */
		@Override
		public ListBuilder1<GValue, GList> add(GValue value);

		/**
		 * {@inheritDoc}
		 * 
		 * @return {@link List}-{@link Builder}.
		 */
		@Override
		public ListBuilder1<GValue, GList> addAll(GValue... value);

		/**
		 * {@inheritDoc}
		 * 
		 * @return {@link List}-{@link Builder}.
		 */
		@Override
		public ListBuilder1<GValue, GList> addAll(Iterable<? extends GValue> value);

		/**
		 * {@inheritDoc}
		 * 
		 * @return {@link List}-{@link Builder}.
		 */
		@Override
		public ListBuilder1<GValue, GList> remove(GValue value);

		/**
		 * {@inheritDoc}
		 * 
		 * @return {@link List}-{@link Builder}.
		 */
		@Override
		public ListBuilder1<GValue, GList> removeAll(GValue... value);

		/**
		 * {@inheritDoc}
		 * 
		 * @return {@link List}-{@link Builder}.
		 */
		@Override
		public ListBuilder1<GValue, GList> removeAll(Iterable<? extends GValue> value);

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ListBuilder1<GValue, List<GValue>> asReverseList();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ListBuilder1<GValue, List<GValue>> asCheckedList(Class<GValue> type);

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ListBuilder1<GValue, List<GValue>> asSynchronizedList();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ListBuilder2<GValue, List<GValue>> asUnmodifiableList();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GList build();

	}

	/**
	 * Diese Schnittstelle definiert einen {@link List}-{@link Builder}, der eine konfigurierte {@link List} in eine {@code reverse}-, {@code checked}-, {@code synchronized}- oder {@code unmodifiable}-{@link List} umwandeln kann.
	 * 
	 * @see bee.creative.util.Collections#reverseList(List)
	 * @see Collections#checkedList(List, Class)
	 * @see Collections#synchronizedList(List)
	 * @see Collections#unmodifiableList(List)
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GValue> Typ der Werte.
	 * @param <GList> Typ der {@link List}.
	 */
	public static interface ListBuilder2<GValue, GList extends List<GValue>> extends Builder<GList> {

		/**
		 * Diese Methode konvertiert die konfigurierte {@link List} via {@link bee.creative.util.Collections#reverseList(List)}.
		 * 
		 * @see bee.creative.util.Collections#reverseList(List)
		 * @return {@link List}-{@link Builder}.
		 */
		public ListBuilder2<GValue, List<GValue>> asReverseList();

		/**
		 * Diese Methode konvertiert die konfigurierte {@link List} via {@link Collections#checkedList(List, Class)}.
		 * 
		 * @see Collections#checkedList(List, Class)
		 * @param type {@link Class} der Werte.
		 * @return {@link List}-{@link Builder}.
		 */
		public ListBuilder2<GValue, List<GValue>> asCheckedList(Class<GValue> type);

		/**
		 * Diese Methode konvertiert die konfigurierte {@link List} via {@link Collections#synchronizedList(List)}.
		 * 
		 * @see Collections#synchronizedList(List)
		 * @return {@link List}-{@link Builder}.
		 */
		public ListBuilder2<GValue, List<GValue>> asSynchronizedList();

		/**
		 * Diese Methode konvertiert die konfigurierte {@link List} via {@link Collections#unmodifiableList(List)}.
		 * 
		 * @see Collections#unmodifiableList(List)
		 * @return {@link ListBuilder2}.
		 */
		public ListBuilder2<GValue, List<GValue>> asUnmodifiableList();

		/**
		 * Diese Methode gibt die konfigurierte {@link List} zurück.
		 * 
		 * @return {@link List}.
		 */
		@Override
		public GList build() throws IllegalStateException;

	}

	/**
	 * Diese Klasse implementiert den {@link ListBuilder1}.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GValue> Typ der Werte.
	 * @param <GList> Typ der {@link List}.
	 */
	static final class ListBuilderImpl<GValue, GList extends List<GValue>> implements ListBuilder1<GValue, GList> {

		/**
		 * Dieses Feld speichert die {@link List}.
		 */
		final GList list;

		/**
		 * Dieser Konstruktor initialisiert die {@link List}.
		 * 
		 * @param list {@link List}.
		 */
		public ListBuilderImpl(final GList list) {
			this.list = list;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ListBuilder1<GValue, GList> add(final GValue value) {
			this.list.add(value);
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ListBuilder1<GValue, GList> addAll(final GValue... value) {
			this.list.addAll(Arrays.asList(value));
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ListBuilder1<GValue, GList> addAll(final Iterable<? extends GValue> value) {
			Iterables.appendAll(this.list, value);
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ListBuilder1<GValue, GList> remove(final GValue value) {
			this.list.remove(value);
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ListBuilder1<GValue, GList> removeAll(final GValue... value) {
			this.list.removeAll(Arrays.asList(value));
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ListBuilder1<GValue, GList> removeAll(final Iterable<? extends GValue> value) {
			Iterables.appendAll(this.list, value);
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ListBuilder1<GValue, List<GValue>> asReverseList() {
			return new ListBuilderImpl<GValue, List<GValue>>(bee.creative.util.Collections.reverseList(this.list));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ListBuilder1<GValue, List<GValue>> asCheckedList(final Class<GValue> type) {
			return new ListBuilderImpl<GValue, List<GValue>>(Collections.checkedList(this.list, type));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ListBuilder1<GValue, List<GValue>> asSynchronizedList() {
			return new ListBuilderImpl<GValue, List<GValue>>(Collections.synchronizedList(this.list));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ListBuilder1<GValue, List<GValue>> asUnmodifiableList() {
			return new ListBuilderImpl<GValue, List<GValue>>(Collections.unmodifiableList(this.list));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GList build() {
			return this.list;
		}

	}

	/**
	 * Diese Methode gibt einen neuen {@link ListBuilder} zurück.
	 * 
	 * @return {@link ListBuilder}.
	 */
	public static ListBuilder use() {
		return new ListBuilder();
	}

	/**
	 * Diese Methode gibt einen {@link List}-{@link Builder} für die gegebene {@link List} zurück.
	 * 
	 * @param <GValue> Typ der Werte.
	 * @param <GList> Typ der {@link List}.
	 * @param list {@link List}.
	 * @return {@link List}-{@link Builder}.
	 * @throws NullPointerException Wenn die gegebene {@link List} {@code null} ist.
	 */
	public <GValue, GList extends List<GValue>> ListBuilder1<GValue, GList> list(final GList list) throws NullPointerException {
		if(list == null) throw new NullPointerException();
		return new ListBuilderImpl<GValue, GList>(list);
	}

	/**
	 * Diese Methode einen neuen {@link ArrayList}-{@link Builder} zurück.
	 * 
	 * @param <GValue> Typ der Werte.
	 * @return {@link ArrayList}-{@link Builder}.
	 */
	public <GValue> ListBuilder1<GValue, ArrayList<GValue>> newArrayList() {
		return this.newArrayList(10);
	}

	/**
	 * Diese Methode einen neuen {@link ArrayList}-{@link Builder} mit der gegebenen Kapazität zurück.
	 * 
	 * @param <GValue> Typ der Werte.
	 * @param capacity Kapazität.
	 * @return {@link ArrayList}-{@link Builder}.
	 * @throws IllegalArgumentException Wenn die gegebene Kapazität negativ ist.
	 */
	public <GValue> ListBuilder1<GValue, ArrayList<GValue>> newArrayList(final int capacity) throws IllegalArgumentException {
		return this.list(new ArrayList<GValue>(capacity));
	}

	/**
	 * Diese Methode einen neuen {@link LinkedList}-{@link Builder} zurück.
	 * 
	 * @param <GValue> Typ der Werte.
	 * @return {@link LinkedList}-{@link Builder}.
	 */
	public <GValue> ListBuilder1<GValue, LinkedList<GValue>> newLinkedList() {
		return this.list(new LinkedList<GValue>());
	}

}