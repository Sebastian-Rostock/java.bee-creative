package bee.creative.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/** Diese Klasse implementiert Hilfsmethoden und Hilfsklassen zur Konstruktion und Verarbeitung von {@link Field}s.
 *
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class Fields {

	private static final class NavigatedField<GSource, GTarget, GValue> implements Field<GSource, GValue> {

		private final Getter<? super GSource, ? extends GTarget> navigator;

		private final Field<? super GTarget, GValue> field;

		private NavigatedField(Getter<? super GSource, ? extends GTarget> navigator, Field<? super GTarget, GValue> field) {
			Objects.assertNotNull(navigator);
			Objects.assertNotNull(field);
			this.navigator = navigator;
			this.field = field;
		}

		@Override
		public GValue get(final GSource input) {
			return field.get(navigator.get(input));
		}

		@Override
		public void set(final GSource input, final GValue value) {
			field.set(navigator.get(input), value);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString("navigatedField", navigator, field);
		}
	}

	private static final class SetupField<GInput, GValue> implements Field<GInput, GValue> {

		private final Field<? super GInput, GValue> field;

		private final Getter<? super GInput, GValue> setupGetter;

		SetupField(Field<? super GInput, GValue> field, Getter<? super GInput, GValue> setupGetter) {
			Objects.assertNotNull(field);
			Objects.assertNotNull(setupGetter);
			this.setupGetter = setupGetter;
			this.field = field;
		}

		@Override
		public GValue get(final GInput input) {
			GValue result = field.get(input);
			if (result != null) return result;
			result = setupGetter.get(input);
			field.set(input, result);
			return result;
		}

		@Override
		public void set(final GInput input, final GValue value) {
			field.set(input, value);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString("setupField", field, setupGetter);
		}
	}

	private static final class ValueField<GValue> implements Field<Object, GValue> {

		private final GValue value;

		private ValueField(GValue value) {
			this.value = value;
		}

		@Override
		public GValue get(Object input) {
			return value;
		}

		@Override
		public void set(Object input, GValue value) {
		}
	}

	private static final class TranslatedField2<GInput, GValue, GValue2> implements Field<GInput, GValue> {

		private final Getter<? super GValue, ? extends GValue2> setFormat;

		private final Getter<? super GValue2, ? extends GValue> getFormat;

		private final Field<? super GInput, GValue2> field;

		TranslatedField2(Field<? super GInput, GValue2> field, Getter<? super GValue, ? extends GValue2> setFormat,
			Getter<? super GValue2, ? extends GValue> getFormat) {
			Objects.assertNotNull(field);
			Objects.assertNotNull(getFormat);
			Objects.assertNotNull(setFormat);
			this.setFormat = setFormat;
			this.getFormat = getFormat;
			this.field = field;
		}

		@Override
		public GValue get(final GInput input) {
			return getFormat.get(field.get(input));
		}

		@Override
		public void set(final GInput input, final GValue value) {
			field.set(input, setFormat.get(value));
		}

		@Override
		public String toString() {
			return Objects.toInvokeString("translatedField", field, getFormat, setFormat);
		}
	}

	private static final class ConditionalField<GInput, GValue> implements Field<GInput, GValue> {

		private final Field<? super GInput, GValue> acceptField;

		private final Filter<? super GInput> condition;

		private final Field<? super GInput, GValue> rejectField;

		ConditionalField(Filter<? super GInput> condition, Field<? super GInput, GValue> acceptField, Field<? super GInput, GValue> rejectField) {
			this.condition = Objects.assertNotNull(condition);
			this.acceptField = Objects.assertNotNull(acceptField);
			this.rejectField = Objects.assertNotNull(rejectField);
		}

		@Override
		public GValue get(final GInput input) {
			if (condition.accept(input)) return acceptField.get(input);
			return rejectField.get(input);
		}

		@Override
		public void set(final GInput input, final GValue value) {
			if (condition.accept(input)) {
				acceptField.set(input, value);
			} else {
				rejectField.set(input, value);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString("conditionalField", condition, acceptField, rejectField);
		}
	}

	public static final class SynchronizedField<GInput, GValue> implements Field<GInput, GValue> {

		public final Field<? super GInput, GValue> field;

		public final Object mutex;

		SynchronizedField(final Object mutex, final Field<? super GInput, GValue> field) {
			this.field = Objects.assertNotNull(field);
			this.mutex = mutex != null ? mutex : this;
		}

		@Override
		public GValue get(final GInput input) {
			synchronized (this.mutex) {
				return this.field.get(input);
			}
		}

		@Override
		public void set(final GInput input, final GValue value) {
			synchronized (this.mutex) {
				this.field.set(input, value);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.field);
		}

	}

	/** Diese Schnittstelle definiert einen Adapter zur Modifikation eines {@link Set}, welches über ein {@link Field} einer gegebenen Eingabe gelesen bzw.
	 * geschrieben wird. Die Modifikation erfolgt an einer Kopie des {@link Set}, welche nach ihrer Modifikation über {@link #set(Object, Object)} zugewiesen
	 * wird.
	 *
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 * @param <GItem> Typ der Elemente. */
	public static interface SetField<GInput, GItem> extends ItemsField<GInput, GItem>, Field<GInput, Set<GItem>> {

	}

	/** Diese Schnittstelle definiert einen Adapter zur Modifikation einer {@link List}, welche über ein {@link Field} einer gegebenen Eingabe gelesen bzw.
	 * geschrieben wird. Die Modifikation erfolgt an einer Kopie der {@link List}, welche nach ihrer Modifikation über {@link #set(Object, Object)} zugewiesen
	 * wird.
	 *
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 * @param <GItem> Typ der Elemente. */
	public static interface ListField<GInput, GItem> extends ItemsField<GInput, GItem>, Field<GInput, List<GItem>> {

		/** Diese Methode verändert die Sammlung analog zu {@link List#add(int, Object)}.
		 *
		 * @param input Eingabe.
		 * @param index Index.
		 * @param item Element. */
		public void put(final GInput input, final int index, final GItem item);

		/** Diese Methode verändert die Sammlung analog zu {@link List#addAll(int, Collection)}.
		 *
		 * @param input Eingabe.
		 * @param index Index.
		 * @param items Elemente. */
		public void putAll(final GInput input, final int index, final Iterable<? extends GItem> items);

		/** Diese Methode verändert die Sammlung analog zu {@link List#remove(int)}.
		 *
		 * @param input Eingabe.
		 * @param index Index. */
		public void pop(final GInput input, final int index);

	}

	/** Diese Schnittstelle definiert einen Adapter zur Modifikation einer {@link Map}, welche über ein {@link Field} einer gegebenen Eingabe gelesen bzw.
	 * geschrieben wird. Die Modifikation erfolgt an einer Kopie der {@link Map}, welche nach ihrer Modifikation über {@link #set(Object, Object)} zugewiesen
	 * wird.
	 *
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GValue> Typ der Werte. */
	public static interface MapField<GInput, GKey, GValue> extends EntriesField<GInput, GKey, GValue>, Field<GInput, Map<GKey, GValue>> {

	}

	/** Diese Schnittstelle definiert einen Adapter zur Modifikation einer {@link Collection}, welche über ein {@link Field} einer gegebenen Eingabe gelesen bzw.
	 * geschrieben wird. Die Modifikation erfolgt an einer Kopie der Objektsammlung, welche nach ihrer Modifikation über {@link Field#set(Object, Object)}
	 * zugewiesen wird.
	 *
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 * @param <GItem> Typ der Elemente. */
	public static interface ItemsField<GInput, GItem> {

		/** Diese Methode verändert die Sammlung analog zu {@link Collection#add(Object)}.
		 *
		 * @param input Eingabe.
		 * @param item Element. */
		public void put(final GInput input, final GItem item);

		/** Diese Methode verändert die Sammlung analog zu {@link Collection#addAll(Collection)}.
		 *
		 * @param input Eingabe.
		 * @param items Elemente. */
		public void putAll(final GInput input, final Iterable<? extends GItem> items);

		/** Diese Methode verändert die Sammlung analog zu {@link Collection#remove(Object)}.
		 *
		 * @param input Eingabe.
		 * @param item Element. */
		public void pop(final GInput input, final Object item);

		/** Diese Methode verändert die Sammlung analog zu {@link Collection#removeAll(Collection)}.
		 *
		 * @param input Eingabe.
		 * @param items Elemente. */
		public void popAll(final GInput input, final Iterable<?> items);

		/** Diese Methode verändert die Sammlung analog zu {@link Collection#clear()}.
		 *
		 * @param input Eingabe. */
		public void clear(final GInput input);

	}

	/** Diese Schnittstelle definiert einen Adapter zur Modifikation einer {@link Map}, welche über ein {@link Field} einer gegebenen Eingabe gelesen bzw.
	 * geschrieben wird. Die Modifikation erfolgt an einer Kopie der {@link Map}, welche nach ihrer Modifikation über {@link Field#set(Object, Object)} zugewiesen
	 * wird.
	 *
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GValue> Typ der Werte. */
	public static interface EntriesField<GInput, GKey, GValue> {

		/** Diese Methode verändert die {@link Map} analog zu {@link Map#put(Object, Object)}.
		 *
		 * @param input Eingabe.
		 * @param key Schlüssel.
		 * @param value Wert. */
		public void put(final GInput input, final GKey key, GValue value);

		/** Diese Methode verändert die {@link Map} analog zu {@link Map#putAll(Map)}.
		 *
		 * @param input Eingabe.
		 * @param entries Elemente. */
		public void putAll(final GInput input, final Iterable<? extends Entry<? extends GKey, ? extends GValue>> entries);

		/** Diese Methode verändert die {@link Map} analog zu {@link Map#remove(Object)}.
		 *
		 * @param input Eingabe.
		 * @param key Schlüssel. */
		public void pop(final GInput input, final Object key);

		/** Diese Methode verändert die {@link Map} analog zu {@link Map#keySet()} mit {@link Set#removeAll(Collection)}.
		 *
		 * @param input Eingabe.
		 * @param keys Schlüssel. */
		public void popAll(final GInput input, final Iterable<?> keys);

		/** Diese Methode verändert die {@link Map} analog zu {@link Map#clear()}.
		 *
		 * @param input Eingabe. */
		public void clear(final GInput input);

	}

	/** Diese Klasse implementiert ein abstraktes {@link SetField}.
	 *
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 * @param <GItem> Typ der Elemente. */
	public static abstract class BaseSetField<GInput, GItem> implements SetField<GInput, GItem> {

		/** Diese Methode gibt ein {@link SetField} zurück, welches das {@link Set} über das gegebene {@link Field} liest und schreibt.
		 *
		 * @param <GInput> Typ der Eingabe.
		 * @param <GItem> Typ der Elemente.
		 * @param field {@link Field} zum Lesen und Schreiben des {@link Set}.
		 * @return {@link SetField}.
		 * @throws NullPointerException Wenn {@code field} {@code null} ist. */
		public static <GInput, GItem> BaseSetField<GInput, GItem> from(final Field<? super GInput, Set<GItem>> field) throws NullPointerException {
			Objects.assertNotNull(field);
			return new BaseSetField<GInput, GItem>() {

				@Override
				public Set<GItem> get(final GInput input) {
					return field.get(input);
				}

				@Override
				public void set(final GInput input, final Set<GItem> value) {
					field.set(input, value);
				}

				@Override
				public String toString() {
					return Objects.toInvokeString("setField", field);
				}

			};
		}

		/** Diese Methode gibt eine Bearbeitungskopie des gegebenen {@link Set} zurück.
		 *
		 * @param value {@link Set}.
		 * @return Bearbeitungskopie. */
		protected Set<GItem> customCopy(final Set<GItem> value) {
			return new HashSet2<>(value);
		}

		/** {@inheritDoc} */
		@Override
		public void clear(final GInput input) {
			if (this.get(input).isEmpty()) return;
			this.set(input, Collections.<GItem>emptySet());
		}

		/** {@inheritDoc} */
		@Override
		public void put(final GInput input, final GItem item) {
			Set<GItem> value = this.get(input);
			if (value.contains(item)) return;
			value = this.customCopy(this.get(input));
			value.add(item);
			this.set(input, value);
		}

		/** {@inheritDoc} */
		@Override
		public void putAll(final GInput input, final Iterable<? extends GItem> items) {
			final Set<GItem> value = this.customCopy(this.get(input));
			if (!Iterables.addAll(value, items)) return;
			this.set(input, value);
		}

		/** {@inheritDoc} */
		@Override
		public void pop(final GInput input, final Object item) {
			Set<GItem> value = this.get(input);
			if (!value.contains(item)) return;
			value = this.customCopy(this.get(input));
			value.remove(item);
			this.set(input, value);
		}

		/** {@inheritDoc} */
		@Override
		public void popAll(final GInput input, final Iterable<?> items) {
			final Set<GItem> value = this.customCopy(this.get(input));
			if (!Iterables.removeAll(value, items)) return;
			this.set(input, value);
		}

	}

	/** Diese Klasse implementiert ein abstraktes {@link ListField}.
	 *
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 * @param <GEntry> Typ der Elemente. */
	public static abstract class BaseListField<GInput, GEntry> implements ListField<GInput, GEntry> {

		/** Diese Methode gibt ein {@link ListField} zurück, welches das {@link List} über das gegebene {@link Field} liest und schreibt.
		 *
		 * @param <GInput> Typ der Eingabe.
		 * @param <GItem> Typ der Elemente.
		 * @param field {@link Field} zum Lesen und Schreiben einer {@link List}.
		 * @return {@link ListField}.
		 * @throws NullPointerException Wenn {@code field} {@code null} ist. */
		public static <GInput, GItem> BaseListField<GInput, GItem> from(final Field<? super GInput, List<GItem>> field) throws NullPointerException {
			Objects.assertNotNull(field);
			return new BaseListField<GInput, GItem>() {

				@Override
				public List<GItem> get(final GInput input) {
					return field.get(input);
				}

				@Override
				public void set(final GInput input, final List<GItem> value) {
					field.set(input, value);
				}

				@Override
				public String toString() {
					return Objects.toInvokeString("listField", field);
				}

			};
		}

		/** Diese Methode gibt eine Bearbeitungskopie der gegebenen {@link List} zurück.
		 *
		 * @param value {@link List}.
		 * @return Bearbeitungskopie. */
		protected List<GEntry> customCopy(final List<GEntry> value) {
			return new ArrayList<>(value);
		}

		/** {@inheritDoc} */
		@Override
		public void clear(final GInput input) {
			if (this.get(input).isEmpty()) return;
			this.set(input, Collections.<GEntry>emptyList());
		}

		/** {@inheritDoc} */
		@Override
		public void put(final GInput input, final GEntry item) {
			final List<GEntry> value = this.customCopy(this.get(input));
			value.add(item);
			this.set(input, value);
		}

		/** {@inheritDoc} */
		@Override
		public void putAll(final GInput input, final Iterable<? extends GEntry> items) {
			final List<GEntry> value = this.customCopy(this.get(input));
			if (!Iterables.addAll(value, items)) return;
			this.set(input, value);
		}

		/** {@inheritDoc} */
		@Override
		public void put(final GInput input, final int index, final GEntry item) {
			final List<GEntry> value = this.customCopy(this.get(input));
			value.add(index, item);
			this.set(input, value);
		}

		/** {@inheritDoc} */
		@Override
		public void putAll(final GInput input, final int index, final Iterable<? extends GEntry> items) {
			final List<GEntry> value = this.customCopy(this.get(input));
			if (!Iterables.addAll(value.subList(index, index), items)) return;
			this.set(input, value);
		}

		/** {@inheritDoc} */
		@Override
		public void pop(final GInput input, final int index) {
			final List<GEntry> value = this.customCopy(this.get(input));
			value.remove(index);
			this.set(input, value);
		}

		/** {@inheritDoc} */
		@Override
		public void pop(final GInput input, final Object item) {
			final List<GEntry> value = this.get(input);
			final int index = value.indexOf(item);
			if (index < 0) return;
			this.pop(input, index);
		}

		/** {@inheritDoc} */
		@Override
		public void popAll(final GInput input, final Iterable<?> items) {
			final List<GEntry> value = this.customCopy(this.get(input));
			if (!Iterables.removeAll(value, items)) return;
			this.set(input, value);
		}

	}

	/** Diese Klasse implementiert ein abstraktes {@link MapField}.
	 *
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GValue> Typ der Werte. */
	public static abstract class BaseMapField<GInput, GKey, GValue> implements MapField<GInput, GKey, GValue> {

		/** Diese Methode gibt ein {@link MapField} zurück, welches die {@link Map} über das gegebene {@link Field} liest und schreibt.
		 *
		 * @param <GInput> Typ der Eingabe.
		 * @param <GKey> Typ der Schlüssel.
		 * @param <GValue> Typ der Werte.
		 * @param field {@link Field} zum Lesen und Schreiben einer {@link Map}.
		 * @return {@link MapField}.
		 * @throws NullPointerException Wenn {@code field} {@code null} ist. */
		public static <GInput, GKey, GValue> BaseMapField<GInput, GKey, GValue> from(final Field<? super GInput, Map<GKey, GValue>> field)
			throws NullPointerException {
			Objects.assertNotNull(field);
			return new BaseMapField<GInput, GKey, GValue>() {

				@Override
				public Map<GKey, GValue> get(final GInput input) {
					return field.get(input);
				}

				@Override
				public void set(final GInput input, final Map<GKey, GValue> value) {
					field.set(input, value);
				}

				@Override
				public String toString() {
					return Objects.toInvokeString("mapField", field);
				}

			};
		}

		/** Diese Methode gibt eine Bearbeitungskopie der gegebenen {@link Map} zurück.
		 *
		 * @param value {@link Map}.
		 * @return Bearbeitungskopie. */
		protected Map<GKey, GValue> customCopy(final Map<GKey, GValue> value) {
			return new HashMap2<>(value);
		}

		/** {@inheritDoc} */
		@Override
		public void clear(final GInput input) {
			if (this.get(input).isEmpty()) return;
			this.set(input, Collections.<GKey, GValue>emptyMap());
		}

		/** {@inheritDoc} */
		@Override
		public void put(final GInput input, final GKey key, final GValue value) {
			Map<GKey, GValue> map = this.get(input);
			if (Objects.equals(map.get(key), value) && ((value != null) || map.containsKey(key))) return;
			map = this.customCopy(this.get(input));
			map.put(key, value);
			this.set(input, map);
		}

		/** {@inheritDoc} */
		@Override
		public void putAll(final GInput input, final Iterable<? extends Entry<? extends GKey, ? extends GValue>> entries) {
			final Map<GKey, GValue> map = this.customCopy(this.get(input));
			boolean modified = false;
			for (final Entry<? extends GKey, ? extends GValue> entry: entries) {
				modified = !Objects.equals(map.put(entry.getKey(), entry.getValue()), entry.getValue()) || modified;
			}
			if (!modified) return;
			this.set(input, map);
		}

		/** {@inheritDoc} */
		@Override
		public void pop(final GInput input, final Object key) {
			Map<GKey, GValue> map = this.get(input);
			if (!map.containsKey(key)) return;
			map = this.customCopy(map);
			map.remove(key);
			this.set(input, map);
		}

		/** {@inheritDoc} */
		@Override
		public void popAll(final GInput input, final Iterable<?> keys) {
			final Map<GKey, GValue> map = this.customCopy(this.get(input));
			if (!Iterables.removeAll(map.keySet(), keys)) return;
			this.set(input, map);
		}

	}

	/** Diese Klasse implementiert ein überwachbares {@link Field Datenfeld}.
	 *
	 * @author [cc-by] 2017 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 * @param <GValue> Typ des Werts der Eigenschaft. */
	public static class ObservableField<GInput, GValue>
		implements Field<GInput, GValue>, Listenable<ObservableField.ChangeFieldEvent, ObservableField.ChangeFieldListener> {

		public static class ChangeFieldEvent {

			public final ObservableField<?, ?> sender;

			public final Field<?, ?> field;

			public final Object input;

			public final Object oldValue;

			public final Object newValue;

			public ChangeFieldEvent(final ObservableField<?, ?> sender, final Field<?, ?> field, final Object input, final Object oldValue, final Object newValue) {
				this.sender = sender;
				this.field = field;
				this.input = input;
				this.oldValue = oldValue;
				this.newValue = newValue;
			}

		}

		public static interface ChangeFieldListener {

			public void onChangeField(ChangeFieldEvent event);

		}

		public static final Listenables<ChangeFieldEvent, ChangeFieldListener> OnChangeField = new Listenables<ChangeFieldEvent, ChangeFieldListener>() {

			@Override
			protected void customFire(final Object sender, final ChangeFieldEvent event, final ChangeFieldListener listener) {
				listener.onChangeField(event);
			}

		};

		/** Dieses Feld speichert das Datenfel, an das in {@link #get(Object)} und {@link #set(Object, Object)} delegiert wird. */
		public final Field<? super GInput, GValue> field;

		/** Dieser Konstruktor initialisiert das überwachte Datenfeld.
		 *
		 * @param field überwachtes Datenfeld. */
		public ObservableField(final Field<? super GInput, GValue> field) {
			this.field = Objects.assertNotNull(field);
		}

		protected GValue customClone(final GValue value) {
			return value;
		}

		protected boolean customEquals(final GValue value1, final GValue value2) {
			return Objects.equals(value1, value2);
		}

		/** {@inheritDoc} */
		@Override
		public GValue get(final GInput input) {
			return this.field.get(input);
		}

		/** {@inheritDoc}
		 *
		 * @throws IllegalStateException Wenn der Wert während */
		@Override
		public void set(final GInput input, final GValue newValue) throws IllegalStateException {
			GValue oldValue = this.field.get(input);
			if (this.customEquals(oldValue, newValue)) return;
			oldValue = this.customClone(oldValue);
			this.field.set(input, newValue);
			this.fire(new ChangeFieldEvent(this, this.field, input, oldValue, newValue));
		}

		/** {@inheritDoc} */
		@Override
		public ChangeFieldListener put(final ChangeFieldListener listener) throws IllegalArgumentException {
			return ObservableField.OnChangeField.put(this, listener);
		}

		/** {@inheritDoc} */
		@Override
		public ChangeFieldListener putWeak(final ChangeFieldListener listener) throws IllegalArgumentException {
			return ObservableField.OnChangeField.putWeak(this, listener);
		}

		/** {@inheritDoc} */
		@Override
		public void pop(final ChangeFieldListener listener) throws IllegalArgumentException {
			ObservableField.OnChangeField.pop(this, listener);
		}

		/** {@inheritDoc} */
		@Override
		public ChangeFieldEvent fire(final ChangeFieldEvent event) throws NullPointerException {
			return ObservableField.OnChangeField.fire(this, event);
		}

		/** {@inheritDoc} */
		@Override
		public String toString() {
			return this.field.toString();
		}

	}

	/** Dieses Feld speichert das leere {@link Field}, welches stets {@code null} liefert und das Schreiben ignoriert. */
	public static final Field<?, ?> EMPTY_FIELD = new ValueField(null);

	/** Diese Methode gibt das leere {@link Field} zurück, das stets {@code null} liefert und das Schreiben ignoriert.
	 *
	 * @param <GValue> Typ des Werts.
	 * @return {@link #EMPTY_FIELD}. */
	@SuppressWarnings ("unchecked")
	public static <GValue> Field<Object, GValue> emptyField() {
		return (Field<Object, GValue>)Fields.EMPTY_FIELD;
	}

	/** Diese Methode ist eine Abkürzung für {@code Fields.compositeField(Getters.valueGetter(value), Setters.neutralSetter())}.
	 *
	 * @see #compositeField(Getter, Setter)
	 * @see Getters#valueGetter(Object)
	 * @see Setters#emptySetter() */
	@SuppressWarnings ("javadoc")
	public static <GValue> Field<Object, GValue> valueField(final GValue value) {
		if (value == null) return emptyField();
		return new ValueField<>(value);
	}

	/** Diese Methode gibt ein initialisierendes {@link Field} zurück.<br>
	 * Das Schreiben wird direkt an das gegebene {@link Field Datenfeld} {@code field} delegiert. Beim Lesen wird der Wert zuerst über das gegebene {@link Field
	 * Datenfeld} ermittelt. Wenn dieser Wert {@code null} ist, wird er initialisiert, d.h. gemäß der gegebenen {@link Getter Initialisierung} {@code setupGetter}
	 * ermittelt, über das {@link Field Datenfeld} {@code field} geschrieben und zurückgegeben. Andernfalls wird der Wertt er direkt zurückgegeben.
	 *
	 * @param <GInput> Typ der Eingabe.
	 * @param <GValue> Typ des Werts.
	 * @param field Datenfeld zur Manipulation.
	 * @param setupGetter Methode zur Initialisierung.
	 * @return {@code setup}-{@link Field}.
	 * @throws NullPointerException Wenn {@code field} bzw. {@code setupGetter} {@code null} ist. */
	public static <GInput, GValue> Field<GInput, GValue> setupField(final Field<? super GInput, GValue> field, final Getter<? super GInput, GValue> setupGetter)
		throws NullPointerException {
		return new SetupField<>(field, setupGetter);
	}

	/** Diese Methode ist eine Abkürzung für {@code Fields.nativeField(Natives.parseField(fieldText))}.
	 *
	 * @see #nativeField(java.lang.reflect.Field)
	 * @see Natives#parseField(String) */
	@SuppressWarnings ("javadoc")
	public static <GInput, GValue> Field<GInput, GValue> nativeField(final String fieldText) throws NullPointerException, IllegalArgumentException {
		return Fields.nativeField(Natives.parseField(fieldText));
	}

	/** Diese Methode gibt ein {@link Field} zum gegebenen {@link java.lang.reflect.Field nativen Datenfeld} zurück.<br>
	 * Für eine Eingabe {@code input} erfolgt das Lesen des gelieferten {@link Field} über {@code field.get(input)}. Das Schreiben eines Werts {@code value}
	 * erfolgt hierbei über {@code field.set(input, value)}. Bei Klassenfeldern wird die Eingabe ignoriert.
	 *
	 * @see java.lang.reflect.Field#get(Object)
	 * @see java.lang.reflect.Field#set(Object, Object)
	 * @param <GInput> Typ der Eingabe.
	 * @param <GValue> Typ des Werts der Eigenschaft.
	 * @param field Datenfeld.
	 * @return {@code native}-{@link Field}.
	 * @throws NullPointerException Wenn {@code field} {@code null} ist.
	 * @throws IllegalArgumentException Wenn das native Datenfeld nicht zugrifbar ist. */
	public static <GInput, GValue> Field<GInput, GValue> nativeField(final java.lang.reflect.Field field) throws NullPointerException, IllegalArgumentException {
		try {
			field.setAccessible(true);
		} catch (final SecurityException cause) {
			throw new IllegalArgumentException(cause);
		}
		return new Field<GInput, GValue>() {

			@Override
			public GValue get(final GInput input) {
				try {
					@SuppressWarnings ("unchecked")
					final GValue result = (GValue)field.get(input);
					return result;
				} catch (final IllegalAccessException cause) {
					throw new IllegalArgumentException(cause);
				}
			}

			@Override
			public void set(final GInput input, final GValue value) {
				try {
					field.set(input, value);
				} catch (final IllegalAccessException cause) {
					throw new IllegalArgumentException(cause);
				}
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("nativeField", Natives.formatField(field));
			}

		};
	}

	/** Diese Methode ist eine Abkürzung für {@code Fields.compositeField(Getters.nativeGetter(getMemberText), Setters.nativeSetter(setMemberText))}.
	 *
	 * @see #compositeField(Getter, Setter)
	 * @see Getters#nativeGetter(String)
	 * @see Setters#nativeSetter(String) */
	@SuppressWarnings ("javadoc")
	public static <GInput, GValue> Field<GInput, GValue> nativeField(final String getMemberText, final String setMemberText)
		throws NullPointerException, IllegalArgumentException {
		return Fields.compositeField(Getters.<GInput, GValue>nativeGetter(getMemberText), Setters.<GInput, GValue>nativeSetter(setMemberText));
	}

	/** Diese Methode ist eine Abkürzung für {@code Fields.compositeField(Getters.nativeGetter(getMethod), Setters.nativeSetter(setMethod))}.
	 *
	 * @see #compositeField(Getter, Setter)
	 * @see Getters#nativeGetter(java.lang.reflect.Method)
	 * @see Setters#nativeSetter(java.lang.reflect.Method) */
	@SuppressWarnings ("javadoc")
	public static <GInput, GValue> Field<GInput, GValue> nativeField(final java.lang.reflect.Method getMethod, final java.lang.reflect.Method setMethod)
		throws NullPointerException, IllegalArgumentException {
		return Fields.compositeField(Getters.<GInput, GValue>nativeGetter(getMethod), Setters.<GInput, GValue>nativeSetter(setMethod));
	}

	/** Diese Methode ist eine Abkürzung für {@code Fields.defaultField(field, null)}.
	 *
	 * @see #defaultField(Field, Object) */
	@SuppressWarnings ("javadoc")
	public static <GInput, GValue> Field<GInput, GValue> defaultField(final Field<? super GInput, GValue> field) throws NullPointerException {
		return Fields.defaultField(field, null);
	}

	/** Diese Methode ist eine effiziente Alternative zu {@code Fields.compositeField(Fields.defaultGetter(field, value), Fields.defaultSetter(field))}.
	 *
	 * @see #compositeField(Getter, Setter)
	 * @see Getters#defaultGetter(Getter, Object)
	 * @see Setters#defaultSetter(Setter) */
	@SuppressWarnings ("javadoc")
	public static <GInput, GValue> Field<GInput, GValue> defaultField(final Field<? super GInput, GValue> field, final GValue value) throws NullPointerException {
		Objects.assertNotNull(field);
		return new Field<GInput, GValue>() {

			@Override
			public GValue get(final GInput input) {
				if (input == null) return value;
				return field.get(input);
			}

			@Override
			public void set(final GInput input, final GValue value) {
				if (input == null) return;
				field.set(input, value);
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("defaultField", field, value);
			}

		};
	}

	/** Diese Methode gibt ein {@link Field} zurück, welches beim Lesen am {@link Map#get(Object)} sowie beim Schreiben an {@link Map#put(Object, Object)}
	 * delegiert.
	 *
	 * @param <GInput> Typ der Eingabe.
	 * @param <GValue> Typ des Werts.
	 * @param mapping {@link Map} zur Abbildung von einer Eingabe auf einen Wert.
	 * @return {@code mapping}-{@link Field}.
	 * @throws NullPointerException Wenn {@code mapping} {@code null} ist. */
	public static <GInput, GValue> Field<GInput, GValue> mappingField(final Map<GInput, GValue> mapping) throws NullPointerException {
		Objects.assertNotNull(mapping);
		return new Field<GInput, GValue>() {

			@Override
			public GValue get(final GInput input) {
				return mapping.get(input);
			}

			@Override
			public void set(final GInput input, final GValue value) {
				mapping.put(input, value);
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("mappingField", mapping);
			}

		};
	}

	/** Diese Methode ist eine effiziente Alternative zu
	 * {@code Fields.compositeField(Getters.navigatedGetter(navigator, field), Setters.navigatedField(navigator, field))}.
	 *
	 * @see #compositeField(Getter, Setter)
	 * @see Getters#navigatedGetter(Getter, Getter)
	 * @see Setters#navigatedSetter(Getter, Setter) */
	@SuppressWarnings ("javadoc")
	public static <GSource, GTarget, GValue> Field<GSource, GValue> navigatedField(final Getter<? super GSource, ? extends GTarget> navigator,
		final Field<? super GTarget, GValue> field) throws NullPointerException {
		return new NavigatedField<>(navigator, field);
	}

	/** Diese Methode gibt ein zusammengesetztes {@link Field} zurück, dessen Methoden an die des gegebenen {@link Getter} und {@link Setter} delegieren.
	 *
	 * @param <GInput> Typ der Eingabe.
	 * @param <GValue> Typ des Werts der Eigenschaft.
	 * @param getter {@link Getter} für {@link Field#get(Object)}.
	 * @param setter {@link Setter} für {@link Field#set(Object, Object)}.
	 * @return {@code composite}-{@link Field}. */
	public static <GInput, GValue> Field<GInput, GValue> compositeField(final Getter<? super GInput, ? extends GValue> getter,
		final Setter<? super GInput, ? super GValue> setter) {
		Objects.assertNotNull(getter);
		Objects.assertNotNull(setter);
		return new Field<GInput, GValue>() {

			@Override
			public GValue get(final GInput input) {
				return getter.get(input);
			}

			@Override
			public void set(final GInput input, final GValue value) {
				setter.set(input, value);
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("compositeField", getter, setter);
			}

		};
	}

	/** Diese Methode ist eine effiziente Alternative zu
	 * {@code Fields.translatedField(field, Translators.toTargetGetter(translator), Translators.toSourceGetter(translator))}.
	 *
	 * @see #translatedField(Field, Getter, Getter)
	 * @see Translators#toTargetGetter(Translator)
	 * @see Translators#toSourceGetter(Translator) */
	@SuppressWarnings ("javadoc")
	public static <GInput, GSource, GTarget> Field<GInput, GTarget> translatedField(final Field<? super GInput, GSource> field,
		final Translator<GSource, GTarget> translator) throws NullPointerException {
		Objects.assertNotNull(field);
		Objects.assertNotNull(translator);
		return new Field<GInput, GTarget>() {

			@Override
			public GTarget get(final GInput input) {
				return translator.toTarget(field.get(input));
			}

			@Override
			public void set(final GInput input, final GTarget value) {
				field.set(input, translator.toSource(value));
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("translatedField", translator, field);
			}

		};
	}

	/** Diese Methode gibt ein übersetztes {@link Field} zurück, welches die gegebenen {@link Getter} zum Parsen und Formatieren nutzt.
	 * <p>
	 * Das erzeugte {@link Field} liefert beim Lesen den (externen) Wert, der gemäß dem gegebenen {@link Getter Leseformat} {@code getFormat} aus dem über das
	 * gegebene {@link Field Datenfeld} ermittelten (internen) Wert berechnet wird. Beim Schreiben eines (externen) Werts wird dieser gemäß dem gegebenen
	 * {@link Getter Schreibformat} {@code setFormat} in einen (internen) Wert überfüght, welcher anschließend an das gegebene {@link Field Datenfeld} delegiert
	 * wird.
	 *
	 * @param <GInput> Typ der Eingabe.
	 * @param <GValue> Typ des Werts des erzeugten Datenfelds.
	 * @param <GValue2> Typ des Werts des gegebenen Datenfelds.
	 * @param field Datenfeld.
	 * @param getFormat {@link Getter} zum Umwandeln des internen in den externen Wert (Formatieren) für das Lesen.
	 * @param setFormat {@link Getter} zum Umwandeln des externen in den internen Wert (Parsen) für das Schreiben.
	 * @return {@code translated}-{@link Field}.
	 * @throws NullPointerException Wenn {@code field} {@code null} ist. */
	public static <GInput, GValue, GValue2> Field<GInput, GValue> translatedField(final Field<? super GInput, GValue2> field,
		final Getter<? super GValue2, ? extends GValue> getFormat, final Getter<? super GValue, ? extends GValue2> setFormat) throws NullPointerException {
		return new TranslatedField2<GInput, GValue, GValue2>(field, setFormat, getFormat);
	}

	/** Diese Methode ist eine Abkürzung für {@code Fields.aggregatedField(field, Getters.neutralGetter(), Getters.neutralGetter(), null, null)}. <br>
	 *
	 * @see #aggregatedField(Field, Getter, Getter, Object, Object) */
	@SuppressWarnings ("javadoc")
	public static <GInput, GValue> Field<Iterable<? extends GInput>, GValue> aggregatedField(final Field<? super GInput, GValue> field)
		throws NullPointerException {
		return Fields.aggregatedField(field, Getters.<GValue>neutralGetter(), Getters.<GValue>neutralGetter(), null, null);
	}

	/** Diese Methode ist eine Abkürzung für {@code Fields.aggregatedField(field, Getters.neutralGetter(), Getters.neutralGetter(), emptyValue, mixedValue)}. <br>
	 *
	 * @see #aggregatedField(Field, Getter, Getter, Object, Object) */
	@SuppressWarnings ("javadoc")
	public static <GItem, GValue> Field<Iterable<? extends GItem>, GValue> aggregatedField(final Field<? super GItem, GValue> field, final GValue emptyValue,
		final GValue mixedValue) throws NullPointerException {
		return Fields.aggregatedField(field, Getters.<GValue>neutralGetter(), Getters.<GValue>neutralGetter(), emptyValue, mixedValue);
	}

	/** Diese Methode ist eine Abkürzung für {@code Fields.aggregatedField(field, getFormat, setFormat, null, null)}. <br>
	 *
	 * @see #aggregatedField(Field, Getter, Getter, Object, Object) */
	@SuppressWarnings ("javadoc")
	public static <GItem, GValue, GValue2> Field<Iterable<? extends GItem>, GValue> aggregatedField(final Field<? super GItem, GValue2> field,
		final Getter<? super GValue2, ? extends GValue> getFormat, final Getter<? super GValue, ? extends GValue2> setFormat) throws NullPointerException {
		return Fields.aggregatedField(field, getFormat, setFormat, null, null);
	}

	/** Diese Methode ist eine Abkürzung für
	 * {@code Fields.compositeField(Getters.aggregatedGetter(field, formatter, emptyValue, mixedValue), Setters.aggregatedSetter(field, parser))}. <br>
	 * Mit einem aggregierten {@link Field} können die Elemente der iterierbaren Eingabe parallel modifiziert werden.
	 *
	 * @see #compositeField(Getter, Setter)
	 * @see Getters#aggregatedGetter(Getter, Getter, Object, Object)
	 * @see Setters#aggregatedSetter(Setter, Getter) */
	@SuppressWarnings ("javadoc")
	public static <GItem, GValue, GValue2> Field<Iterable<? extends GItem>, GValue> aggregatedField(final Field<? super GItem, GValue2> field,
		final Getter<? super GValue2, ? extends GValue> getFormat, final Getter<? super GValue, ? extends GValue2> setFormat, final GValue emptyValue,
		final GValue mixedValue) throws NullPointerException {
		return Fields.compositeField(Getters.aggregatedGetter(field, getFormat, emptyValue, mixedValue), Setters.aggregatedSetter(field, setFormat));
	}

	/** Diese Methode ist eine effiziente Alternative zu
	 * {@code Fields.compositeField(Getters.conditionalGetter(condition, acceptField, rejectField), Setters.conditionalSetter(condition, acceptField, rejectField))}.
	 *
	 * @see #compositeField(Getter, Setter)
	 * @see Getters#conditionalGetter(Filter, Getter, Getter)
	 * @see Setters#conditionalSetter(Filter, Setter, Setter) */
	@SuppressWarnings ("javadoc")
	public static <GInput, GValue> Field<GInput, GValue> conditionalField(final Filter<? super GInput> condition, final Field<? super GInput, GValue> acceptField,
		final Field<? super GInput, GValue> rejectField) throws NullPointerException {
		return new ConditionalField<GInput, GValue>(condition, acceptField, rejectField);
	}

	/** Diese Methode gibt ein {@link Field} zurück, welches das gegebene {@link Field} via {@code synchronized(field)} synchronisiert.
	 *
	 * @param <GInput> Typ der Eingabe.
	 * @param <GValue> Typ des Werts der Eigenschaft.
	 * @param field {@link Field}.
	 * @return {@code synchronized}-{@link Field}.
	 * @throws NullPointerException Wenn {@code field} {@code null} ist. */
	public static <GInput, GValue> Field<GInput, GValue> synchronizedField(final Field<? super GInput, GValue> field) throws NullPointerException {
		return Fields.synchronizedField(field, field);
	}

	public static <GInput, GValue> Field<GInput, GValue> synchronizedField(final Object mutex, final Field<? super GInput, GValue> field)
		throws NullPointerException {
		return new SynchronizedField<>(mutex, field);
	}

}