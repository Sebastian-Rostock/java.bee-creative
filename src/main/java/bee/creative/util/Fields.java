package bee.creative.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import bee.creative.util.Objects.BaseObject;
import bee.creative.util.Properties.BaseProperty;

/** Diese Klasse implementiert Hilfsmethoden und Hilfsklassen zur Konstruktion und Verarbeitung von {@link Field}-Instanzen.
 *
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class Fields {

	/** Diese Schnittstelle definiert einen Adapter zur Modifikation eines {@link Set}, welches über ein {@link Field} einer gegebenen Eingabe gelesen bzw.
	 * geschrieben wird. Die Modifikation erfolgt an einer Kopie des {@link Set}, welche nach ihrer Modifikation über {@link #set(Object, Object)} zugewiesen
	 * wird.
	 *
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ des Datensatzes.
	 * @param <GEntry> Typ der Elemente. */
	public static interface SetField<GItem, GEntry> extends ItemsField<GItem, GEntry>, Field<GItem, Set<GEntry>> {
	}

	/** Diese Schnittstelle definiert einen Adapter zur Modifikation einer {@link List}, welche über ein {@link Field} einer gegebenen Eingabe gelesen bzw.
	 * geschrieben wird. Die Modifikation erfolgt an einer Kopie der {@link List}, welche nach ihrer Modifikation über {@link #set(Object, Object)} zugewiesen
	 * wird.
	 *
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ des Datensatzes.
	 * @param <GEntry> Typ der Elemente. */
	public static interface ListField<GItem, GEntry> extends ItemsField<GItem, GEntry>, Field<GItem, List<GEntry>> {

		/** Diese Methode verändert die Sammlung analog zu {@link List#add(int, Object)}.
		 *
		 * @param item Eingabe.
		 * @param index Index.
		 * @param entry Element. */
		public void put(final GItem item, final int index, final GEntry entry);

		/** Diese Methode verändert die Sammlung analog zu {@link List#addAll(int, Collection)}.
		 *
		 * @param item Eingabe.
		 * @param index Index.
		 * @param items Elemente. */
		public void putAll(final GItem item, final int index, final Iterable<? extends GEntry> items);

		/** Diese Methode verändert die Sammlung analog zu {@link List#remove(int)}.
		 *
		 * @param item Eingabe.
		 * @param index Index. */
		public void pop(final GItem item, final int index);

	}

	/** Diese Schnittstelle definiert einen Adapter zur Modifikation einer {@link Map}, welche über ein {@link Field} einer gegebenen Eingabe gelesen bzw.
	 * geschrieben wird. Die Modifikation erfolgt an einer Kopie der {@link Map}, welche nach ihrer Modifikation über {@link #set(Object, Object)} zugewiesen
	 * wird.
	 *
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ des Datensatzes.
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GValue> Typ der Werte. */
	public static interface MapField<GItem, GKey, GValue> extends EntriesField<GItem, GKey, GValue>, Field<GItem, Map<GKey, GValue>> {
	}

	/** Diese Schnittstelle definiert einen Adapter zur Modifikation einer {@link Collection}, welche über ein {@link Field} einer gegebenen Eingabe gelesen bzw.
	 * geschrieben wird. Die Modifikation erfolgt an einer Kopie der Objektsammlung, welche nach ihrer Modifikation über {@link Field#set(Object, Object)}
	 * zugewiesen wird.
	 *
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ des Datensatzes.
	 * @param <GEntry> Typ der Elemente. */
	public static interface ItemsField<GItem, GEntry> {

		/** Diese Methode verändert die Sammlung analog zu {@link Collection#add(Object)}.
		 *
		 * @param item Eingabe.
		 * @param entry Element. */
		public void put(final GItem item, final GEntry entry);

		/** Diese Methode verändert die Sammlung analog zu {@link Collection#addAll(Collection)}.
		 *
		 * @param item Eingabe.
		 * @param items Elemente. */
		public void putAll(final GItem item, final Iterable<? extends GEntry> items);

		/** Diese Methode verändert die Sammlung analog zu {@link Collection#remove(Object)}.
		 *
		 * @param item Eingabe.
		 * @param entry Element. */
		public void pop(final GItem item, final Object entry);

		/** Diese Methode verändert die Sammlung analog zu {@link Collection#removeAll(Collection)}.
		 *
		 * @param item Eingabe.
		 * @param items Elemente. */
		public void popAll(final GItem item, final Iterable<?> items);

		/** Diese Methode verändert die Sammlung analog zu {@link Collection#clear()}.
		 *
		 * @param item Eingabe. */
		public void clear(final GItem item);

	}

	/** Diese Schnittstelle definiert einen Adapter zur Modifikation einer {@link Map}, welche über ein {@link Field} einer gegebenen Eingabe gelesen bzw.
	 * geschrieben wird. Die Modifikation erfolgt an einer Kopie der {@link Map}, welche nach ihrer Modifikation über {@link Field#set(Object, Object)} zugewiesen
	 * wird.
	 *
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ des Datensatzes.
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GValue> Typ der Werte. */
	public static interface EntriesField<GItem, GKey, GValue> {

		/** Diese Methode verändert die {@link Map} analog zu {@link Map#put(Object, Object)}.
		 *
		 * @param item Eingabe.
		 * @param key Schlüssel.
		 * @param value Wert. */
		public void put(final GItem item, final GKey key, GValue value);

		/** Diese Methode verändert die {@link Map} analog zu {@link Map#putAll(Map)}.
		 *
		 * @param item Eingabe.
		 * @param entries Elemente. */
		public void putAll(final GItem item, final Iterable<? extends Entry<? extends GKey, ? extends GValue>> entries);

		/** Diese Methode verändert die {@link Map} analog zu {@link Map#remove(Object)}.
		 *
		 * @param item Eingabe.
		 * @param key Schlüssel. */
		public void pop(final GItem item, final Object key);

		/** Diese Methode verändert die {@link Map} analog zu {@link Map#keySet()} mit {@link Set#removeAll(Collection)}.
		 *
		 * @param item Eingabe.
		 * @param keys Schlüssel. */
		public void popAll(final GItem item, final Iterable<?> keys);

		/** Diese Methode verändert die {@link Map} analog zu {@link Map#clear()}.
		 *
		 * @param item Eingabe. */
		public void clear(final GItem item);

	}

	/** Diese Klasse implementiert ein abstraktes {@link Field} als {@link BaseObject}. */
	@SuppressWarnings ("javadoc")
	public static abstract class BaseField<GItem, GValue> extends BaseObject implements Field<GItem, GValue> {
	}

	/** Diese Klasse implementiert ein abstraktes {@link SetField}.
	 *
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ des Datensatzes.
	 * @param <GEntry> Typ der Elemente. */
	public static abstract class BaseSetField<GItem, GEntry> extends BaseField<GItem, Set<GEntry>> implements SetField<GItem, GEntry> {

		/** Diese Methode gibt ein {@link SetField} zurück, welches das {@link Set} über das gegebene {@link Field} liest und schreibt.
		 *
		 * @param <GItem> Typ des Datensatzes.
		 * @param <GEntry> Typ der Elemente.
		 * @param field {@link Field} zum Lesen und Schreiben des {@link Set}.
		 * @return {@link SetField}.
		 * @throws NullPointerException Wenn {@code field} {@code null} ist. */
		public static <GItem, GEntry> BaseSetField<GItem, GEntry> from(final Field<? super GItem, Set<GEntry>> field) throws NullPointerException {
			Objects.notNull(field);
			return new BaseSetField<GItem, GEntry>() {

				@Override
				public Set<GEntry> get(final GItem item) {
					return field.get(item);
				}

				@Override
				public void set(final GItem item, final Set<GEntry> value) {
					field.set(item, value);
				}

				@Override
				public String toString() {
					return field.toString();
				}

			};
		}

		/** Diese Methode gibt eine Bearbeitungskopie des gegebenen {@link Set} zurück.
		 *
		 * @param value {@link Set}.
		 * @return Bearbeitungskopie. */
		protected Set<GEntry> customCopy(final Set<GEntry> value) {
			return new HashSet2<>(value);
		}

		/** {@inheritDoc} */
		@Override
		public void clear(final GItem item) {
			if (this.get(item).isEmpty()) return;
			this.set(item, Collections.<GEntry>emptySet());
		}

		/** {@inheritDoc} */
		@Override
		public void put(final GItem item, final GEntry entry) {
			Set<GEntry> value = this.get(item);
			if (value.contains(entry)) return;
			value = this.customCopy(this.get(item));
			value.add(entry);
			this.set(item, value);
		}

		/** {@inheritDoc} */
		@Override
		public void putAll(final GItem item, final Iterable<? extends GEntry> items) {
			final Set<GEntry> value = this.customCopy(this.get(item));
			if (!Iterables.addAll(value, items)) return;
			this.set(item, value);
		}

		/** {@inheritDoc} */
		@Override
		public void pop(final GItem item, final Object entry) {
			Set<GEntry> value = this.get(item);
			if (!value.contains(entry)) return;
			value = this.customCopy(this.get(item));
			value.remove(entry);
			this.set(item, value);
		}

		/** {@inheritDoc} */
		@Override
		public void popAll(final GItem item, final Iterable<?> items) {
			final Set<GEntry> value = this.customCopy(this.get(item));
			if (!Iterables.removeAll(value, items)) return;
			this.set(item, value);
		}

	}

	/** Diese Klasse implementiert ein abstraktes {@link ListField}.
	 *
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ des Datensatzes.
	 * @param <GEntry> Typ der Elemente. */
	public static abstract class BaseListField<GItem, GEntry> extends BaseField<GItem, List<GEntry>> implements ListField<GItem, GEntry> {

		/** Diese Methode gibt ein {@link ListField} zurück, welches das {@link List} über das gegebene {@link Field} liest und schreibt.
		 *
		 * @param <GItem> Typ des Datensatzes.
		 * @param <GEntry> Typ der Elemente.
		 * @param field {@link Field} zum Lesen und Schreiben einer {@link List}.
		 * @return {@link ListField}.
		 * @throws NullPointerException Wenn {@code field} {@code null} ist. */
		public static <GItem, GEntry> BaseListField<GItem, GEntry> from(final Field<? super GItem, List<GEntry>> field) throws NullPointerException {
			Objects.notNull(field);
			return new BaseListField<GItem, GEntry>() {

				@Override
				public List<GEntry> get(final GItem item) {
					return field.get(item);
				}

				@Override
				public void set(final GItem item, final List<GEntry> value) {
					field.set(item, value);
				}

				@Override
				public String toString() {
					return field.toString();
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
		public void clear(final GItem item) {
			if (this.get(item).isEmpty()) return;
			this.set(item, Collections.<GEntry>emptyList());
		}

		/** {@inheritDoc} */
		@Override
		public void put(final GItem item, final GEntry entry) {
			final List<GEntry> value = this.customCopy(this.get(item));
			value.add(entry);
			this.set(item, value);
		}

		/** {@inheritDoc} */
		@Override
		public void putAll(final GItem item, final Iterable<? extends GEntry> items) {
			final List<GEntry> value = this.customCopy(this.get(item));
			if (!Iterables.addAll(value, items)) return;
			this.set(item, value);
		}

		/** {@inheritDoc} */
		@Override
		public void put(final GItem item, final int index, final GEntry entry) {
			final List<GEntry> value = this.customCopy(this.get(item));
			value.add(index, entry);
			this.set(item, value);
		}

		/** {@inheritDoc} */
		@Override
		public void putAll(final GItem item, final int index, final Iterable<? extends GEntry> items) {
			final List<GEntry> value = this.customCopy(this.get(item));
			if (!Iterables.addAll(value.subList(index, index), items)) return;
			this.set(item, value);
		}

		/** {@inheritDoc} */
		@Override
		public void pop(final GItem item, final int index) {
			final List<GEntry> value = this.customCopy(this.get(item));
			value.remove(index);
			this.set(item, value);
		}

		/** {@inheritDoc} */
		@Override
		public void pop(final GItem item, final Object entry) {
			final List<GEntry> value = this.get(item);
			final int index = value.indexOf(entry);
			if (index < 0) return;
			this.pop(item, index);
		}

		/** {@inheritDoc} */
		@Override
		public void popAll(final GItem item, final Iterable<?> items) {
			final List<GEntry> value = this.customCopy(this.get(item));
			if (!Iterables.removeAll(value, items)) return;
			this.set(item, value);
		}

	}

	/** Diese Klasse implementiert ein abstraktes {@link MapField}.
	 *
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ des Datensatzes.
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GValue> Typ der Werte. */
	public static abstract class BaseMapField<GItem, GKey, GValue> extends BaseField<GItem, Map<GKey, GValue>> implements MapField<GItem, GKey, GValue> {

		/** Diese Methode gibt ein {@link MapField} zurück, welches die {@link Map} über das gegebene {@link Field} liest und schreibt.
		 *
		 * @param <GItem> Typ des Datensatzes.
		 * @param <GKey> Typ der Schlüssel.
		 * @param <GValue> Typ der Werte.
		 * @param field {@link Field} zum Lesen und Schreiben einer {@link Map}.
		 * @return {@link MapField}.
		 * @throws NullPointerException Wenn {@code field} {@code null} ist. */
		public static <GItem, GKey, GValue> BaseMapField<GItem, GKey, GValue> from(final Field<? super GItem, Map<GKey, GValue>> field)
			throws NullPointerException {
			Objects.notNull(field);
			return new BaseMapField<GItem, GKey, GValue>() {

				@Override
				public Map<GKey, GValue> get(final GItem item) {
					return field.get(item);
				}

				@Override
				public void set(final GItem item, final Map<GKey, GValue> value) {
					field.set(item, value);
				}

				@Override
				public String toString() {
					return field.toString();
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
		public void clear(final GItem item) {
			if (this.get(item).isEmpty()) return;
			this.set(item, Collections.<GKey, GValue>emptyMap());
		}

		/** {@inheritDoc} */
		@Override
		public void put(final GItem item, final GKey key, final GValue value) {
			Map<GKey, GValue> map = this.get(item);
			if (Objects.equals(map.get(key), value) && ((value != null) || map.containsKey(key))) return;
			map = this.customCopy(this.get(item));
			map.put(key, value);
			this.set(item, map);
		}

		/** {@inheritDoc} */
		@Override
		public void putAll(final GItem item, final Iterable<? extends Entry<? extends GKey, ? extends GValue>> entries) {
			final Map<GKey, GValue> map = this.customCopy(this.get(item));
			boolean modified = false;
			for (final Entry<? extends GKey, ? extends GValue> entry: entries) {
				modified = !Objects.equals(map.put(entry.getKey(), entry.getValue()), entry.getValue()) || modified;
			}
			if (!modified) return;
			this.set(item, map);
		}

		/** {@inheritDoc} */
		@Override
		public void pop(final GItem item, final Object key) {
			Map<GKey, GValue> map = this.get(item);
			if (!map.containsKey(key)) return;
			map = this.customCopy(map);
			map.remove(key);
			this.set(item, map);
		}

		/** {@inheritDoc} */
		@Override
		public void popAll(final GItem item, final Iterable<?> keys) {
			final Map<GKey, GValue> map = this.customCopy(this.get(item));
			if (!Iterables.removeAll(map.keySet(), keys)) return;
			this.set(item, map);
		}

	}

	/** Diese Klasse implementiert {@link Fields#valueField(Object)}. */
	@SuppressWarnings ("javadoc")
	public static class ValueField<GValue> implements Field<Object, GValue> {

		public static final Field<?, ?> EMPTY = new ValueField<>(null);

		public final GValue value;

		public ValueField(final GValue value) {
			this.value = value;
		}

		@Override
		public GValue get(final Object item) {
			return this.value;
		}

		@Override
		public void set(final Object item, final GValue value) {
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.value);
		}

	}

	/** Diese Klasse implementiert {@link Fields#nativeField(java.lang.reflect.Field)}. */
	@SuppressWarnings ("javadoc")
	public static class NativeField<GItem, GValue> implements Field<GItem, GValue> {

		public final java.lang.reflect.Field field;

		public NativeField(final java.lang.reflect.Field field, final boolean forceAccessible) {
			if (forceAccessible) {
				try {
					field.setAccessible(true);
				} catch (final SecurityException cause) {
					throw new IllegalArgumentException(cause);
				}
			}
			this.field = Objects.notNull(field);
		}

		@Override
		@SuppressWarnings ("unchecked")
		public GValue get(final GItem item) {
			try {
				return (GValue)this.field.get(item);
			} catch (final IllegalAccessException cause) {
				throw new IllegalArgumentException(cause);
			}
		}

		@Override
		public void set(final GItem item, final GValue value) {
			try {
				this.field.set(item, value);
			} catch (final IllegalAccessException cause) {
				throw new IllegalArgumentException(cause);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.field);
		}

	}

	/** Diese Klasse implementiert {@link Fields#setupField(Field, Getter)}. */
	@SuppressWarnings ("javadoc")
	public static class SetupField<GItem, GValue> implements Field<GItem, GValue> {

		public final Getter<? super GItem, ? extends GValue> setup;

		public final Field<? super GItem, GValue> field;

		public SetupField(final Getter<? super GItem, ? extends GValue> setup, final Field<? super GItem, GValue> field) {
			this.setup = Objects.notNull(setup);
			this.field = Objects.notNull(field);
		}

		@Override
		public GValue get(final GItem item) {
			GValue result = this.field.get(item);
			if (result != null) return result;
			result = this.setup.get(item);
			this.field.set(item, result);
			return result;
		}

		@Override
		public void set(final GItem item, final GValue value) {
			this.field.set(item, value);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.setup, this.field);
		}

	}

	/** Diese Klasse implementiert {@link Fields#defaultField(Field, Object)}. */
	@SuppressWarnings ("javadoc")
	public static class DefaultField<GItem, GValue> implements Field<GItem, GValue> {

		public final Field<? super GItem, GValue> field;

		public final GValue value;

		public DefaultField(final Field<? super GItem, GValue> field, final GValue value) {
			this.field = Objects.notNull(field);
			this.value = value;
		}

		@Override
		public GValue get(final GItem item) {
			if (item == null) return this.value;
			return this.field.get(item);
		}

		@Override
		public void set(final GItem item, final GValue value) {
			if (item == null) return;
			this.field.set(item, value);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.field, this.value);
		}

	}

	/** Diese Klasse implementiert {@link Fields#mappingField(Map)}. */
	@SuppressWarnings ("javadoc")
	public static class MappingField<GItem, GValue> implements Field<GItem, GValue> {

		public final Map<GItem, GValue> mapping;

		public MappingField(final Map<GItem, GValue> mapping) {
			this.mapping = Objects.notNull(mapping);
		}

		@Override
		public GValue get(final GItem item) {
			return this.mapping.get(item);
		}

		@Override
		public void set(final GItem item, final GValue value) {
			this.mapping.put(item, value);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.mapping);
		}

	}

	/** Diese Klasse implementiert {@link Fields#navigatedField(Getter, Field)}. */
	@SuppressWarnings ("javadoc")
	public static class NavigatedField<GSource, GTarget, GValue> implements Field<GSource, GValue> {

		public final Getter<? super GSource, ? extends GTarget> toTarget;

		public final Field<? super GTarget, GValue> field;

		public NavigatedField(final Getter<? super GSource, ? extends GTarget> toTarget, final Field<? super GTarget, GValue> field) {
			this.toTarget = Objects.notNull(toTarget);
			this.field = Objects.notNull(field);
		}

		@Override
		public GValue get(final GSource item) {
			return this.field.get(this.toTarget.get(item));
		}

		@Override
		public void set(final GSource item, final GValue value) {
			this.field.set(this.toTarget.get(item), value);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.toTarget, this.field);
		}

	}

	/** Diese Klasse implementiert {@link Fields#compositeField(Getter, Setter)}. */
	@SuppressWarnings ("javadoc")
	public static class CompositeField<GItem, GValue> implements Field<GItem, GValue> {

		public final Getter<? super GItem, ? extends GValue> getter;

		public final Setter<? super GItem, ? super GValue> setter;

		public CompositeField(final Getter<? super GItem, ? extends GValue> getter, final Setter<? super GItem, ? super GValue> setter) {
			this.getter = Objects.notNull(getter);
			this.setter = Objects.notNull(setter);
		}

		@Override
		public GValue get(final GItem item) {
			return this.getter.get(item);
		}

		@Override
		public void set(final GItem item, final GValue value) {
			this.setter.set(item, value);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.getter, this.setter);
		}

	}

	/** Diese Klasse implementiert {@link Fields#translatedField(Field, Translator)}. */
	@SuppressWarnings ("javadoc")
	public static class TranslatedField<GItem, GTarget, GSource> implements Field<GItem, GTarget> {

		public final Field<? super GItem, GSource> field;

		public final Translator<GSource, GTarget> translator;

		public TranslatedField(final Field<? super GItem, GSource> field, final Translator<GSource, GTarget> translator) {
			this.field = Objects.notNull(field);
			this.translator = Objects.notNull(translator);
		}

		@Override
		public GTarget get(final GItem item) {
			return this.translator.toTarget(this.field.get(item));
		}

		@Override
		public void set(final GItem item, final GTarget value) {
			this.field.set(item, this.translator.toSource(value));
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.field, this.translator);
		}

	}

	/** Diese Klasse implementiert {@link Fields#conditionalField(Filter, Field, Field)}. */
	@SuppressWarnings ("javadoc")
	public static class ConditionalField<GItem, GValue> implements Field<GItem, GValue> {

		public final Filter<? super GItem> condition;

		public final Field<? super GItem, GValue> acceptField;

		public final Field<? super GItem, GValue> rejectField;

		public ConditionalField(final Filter<? super GItem> condition, final Field<? super GItem, GValue> acceptField,
			final Field<? super GItem, GValue> rejectField) {
			this.condition = Objects.notNull(condition);
			this.acceptField = Objects.notNull(acceptField);
			this.rejectField = Objects.notNull(rejectField);
		}

		@Override
		public GValue get(final GItem item) {
			if (this.condition.accept(item)) return this.acceptField.get(item);
			return this.rejectField.get(item);
		}

		@Override
		public void set(final GItem item, final GValue value) {
			if (this.condition.accept(item)) {
				this.acceptField.set(item, value);
			} else {
				this.rejectField.set(item, value);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.condition, this.acceptField, this.rejectField);
		}

	}

	/** Diese Klasse implementiert {@link Fields#synchronizedField(Object, Field)}. */
	@SuppressWarnings ("javadoc")
	public static class SynchronizedField<GItem, GValue> implements Field<GItem, GValue> {

		public final Object mutex;

		public final Field<? super GItem, GValue> field;

		public SynchronizedField(final Object mutex, final Field<? super GItem, GValue> field) {
			this.mutex = Objects.notNull(mutex, this);
			this.field = Objects.notNull(field);
		}

		@Override
		public GValue get(final GItem item) {
			synchronized (this.mutex) {
				return this.field.get(item);
			}
		}

		@Override
		public void set(final GItem item, final GValue value) {
			synchronized (this.mutex) {
				this.field.set(item, value);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.field);
		}

	}

	/** Diese Klasse implementiert {@link Fields#toProperty(Object, Field)}. */
	@SuppressWarnings ("javadoc")
	static class FieldProperty<GValue, GItem> extends BaseProperty<GValue> {

		public final GItem item;

		public final Field<? super GItem, GValue> field;

		public FieldProperty(final GItem item, final Field<? super GItem, GValue> field) {
			this.item = item;
			this.field = Objects.notNull(field);
		}

		@Override
		public GValue get() {
			return this.field.get(this.item);
		}

		@Override
		public void set(final GValue value) {
			this.field.set(this.item, value);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.item, this.field);
		}

	}

	/** Diese Methode gibt das leere {@link Field} zurück, das stets {@code null} liefert und das Schreiben ignoriert.
	 *
	 * @param <GValue> Typ des Werts.
	 * @return {@code empty}-{@link Field}. */
	@SuppressWarnings ("unchecked")
	public static <GValue> Field<Object, GValue> emptyField() {
		return (Field<Object, GValue>)ValueField.EMPTY;
	}

	/** Diese Methode gibt ein {@link Field} zurück, das stets den gegebenen Wert liefert und das Schreiben ignoriert.
	 *
	 * @param <GValue> Typ des Werts.
	 * @param value Wert.
	 * @return {@code value}-{@link Field}. */
	public static <GValue> Field<Object, GValue> valueField(final GValue value) {
		if (value == null) return Fields.emptyField();
		return new ValueField<>(value);
	}

	/** Diese Methode gibt ein initialisierendes {@link Field} zurück.<br>
	 * Das Schreiben wird direkt an das gegebene {@link Field Datenfeld} {@code field} delegiert. Beim Lesen wird der Wert zuerst über das gegebene {@link Field
	 * Datenfeld} ermittelt. Wenn dieser Wert {@code null} ist, wird er initialisiert, d.h. gemäß der gegebenen {@link Getter Initialisierung} {@code setup}
	 * ermittelt, über das {@link Field Datenfeld} {@code field} geschrieben und zurückgegeben. Andernfalls wird der Wertt er direkt zurückgegeben.
	 *
	 * @param <GItem> Typ des Datensatzes.
	 * @param <GValue> Typ des Werts.
	 * @param field Datenfeld zur Manipulation.
	 * @param setup Methode zur Initialisierung.
	 * @return {@code setup}-{@link Field}.
	 * @throws NullPointerException Wenn {@code field} bzw. {@code setup} {@code null} ist. */
	public static <GItem, GValue> Field<GItem, GValue> setupField(final Field<? super GItem, GValue> field, final Getter<? super GItem, ? extends GValue> setup)
		throws NullPointerException {
		return new SetupField<>(setup, field);
	}

	public static <GItem, GValue> Field<GItem, GValue> nativeField(final java.lang.reflect.Field field) throws NullPointerException, IllegalArgumentException {
		return Fields.nativeField(field, true);
	}

	/** Diese Methode gibt ein {@link Field} zum gegebenen {@link java.lang.reflect.Field nativen Datenfeld} zurück.<br>
	 * Für eine Eingabe {@code item} erfolgt das Lesen des gelieferten {@link Field} über {@code field.get(item)}. Das Schreiben eines Werts {@code value} erfolgt
	 * hierbei über {@code field.set(item, value)}. Bei Klassenfeldern wird die Eingabe ignoriert.
	 *
	 * @see java.lang.reflect.Field#get(Object)
	 * @see java.lang.reflect.Field#set(Object, Object)
	 * @param <GItem> Typ des Datensatzes.
	 * @param <GValue> Typ des Werts der Eigenschaft.
	 * @param field Datenfeld.
	 * @param forceAccessible
	 * @return {@code native}-{@link Field}.
	 * @throws NullPointerException Wenn {@code field} {@code null} ist.
	 * @throws IllegalArgumentException Wenn das native Datenfeld nicht zugrifbar ist. */
	public static <GItem, GValue> Field<GItem, GValue> nativeField(final java.lang.reflect.Field field, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return new NativeField<>(field, forceAccessible);
	}

	/** Diese Methode ist eine Abkürzung für {@code Fields.nativeField(Natives.parseField(fieldOwner, fieldName))}.
	 *
	 * @see #nativeField(java.lang.reflect.Field)
	 * @see Natives#parseField(Class, String) */
	@SuppressWarnings ("javadoc")
	public static <GItem, GValue> Field<GItem, GValue> nativeField(final Class<? extends GItem> fieldOwner, final String fieldName)
		throws NullPointerException, IllegalArgumentException {
		return Fields.nativeField(Natives.parseField(fieldOwner, fieldName));
	}

	/** Diese Methode ist eine Abkürzung für {@code Fields.nativeField(Natives.parseField(fieldOwner, fieldName), forceAccessible)}.
	 *
	 * @see #nativeField(java.lang.reflect.Field)
	 * @see Natives#parseField(Class, String) */
	@SuppressWarnings ("javadoc")
	public static <GItem, GValue> Field<GItem, GValue> nativeField(final Class<? extends GItem> fieldOwner, final String fieldName, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return Fields.nativeField(Natives.parseField(fieldOwner, fieldName), forceAccessible);
	}

	/** Diese Methode ist eine Abkürzung für {@link #compositeField(Getter, Setter) Fields.compositeField(Getters.nativeGetter(getMethod),
	 * Setters.nativeSetter(setMethod))}.
	 *
	 * @see Getters#nativeGetter(java.lang.reflect.Method)
	 * @see Setters#nativeSetter(java.lang.reflect.Method) */
	@SuppressWarnings ("javadoc")
	public static <GItem, GValue> Field<GItem, GValue> nativeField(final java.lang.reflect.Method getMethod, final java.lang.reflect.Method setMethod)
		throws NullPointerException, IllegalArgumentException {
		return Fields.compositeField(Getters.<GItem, GValue>nativeGetter(getMethod), Setters.<GItem, GValue>nativeSetter(setMethod));
	}

	/** Diese Methode ist eine Abkürzung für {@code Fields.compositeField(Getters.nativeGetter(getMemberText), Setters.nativeSetter(setMemberText))}.
	 *
	 * @see #compositeField(Getter, Setter)
	 * @see Getters#nativeGetter(String)
	 * @see Setters#nativeSetter(String) */
	@SuppressWarnings ("javadoc")
	public static <GItem, GValue> Field<GItem, GValue> nativeField(final String getMemberText, final String setMemberText)
		throws NullPointerException, IllegalArgumentException {
		return Fields.compositeField(Getters.<GItem, GValue>nativeGetter(getMemberText), Setters.<GItem, GValue>nativeSetter(setMemberText));
	}

	/** Diese Methode ist eine Abkürzung für {@code Fields.defaultField(field, null)}.
	 *
	 * @see #defaultField(Field, Object) */
	@SuppressWarnings ("javadoc")
	public static <GItem, GValue> Field<GItem, GValue> defaultField(final Field<? super GItem, GValue> field) throws NullPointerException {
		return Fields.defaultField(field, null);
	}

	/** Diese Methode ist eine effiziente Alternative zu {@code Fields.compositeField(Fields.defaultGetter(field, value), Fields.defaultSetter(field))}.
	 *
	 * @see #compositeField(Getter, Setter)
	 * @see Getters#defaultGetter(Getter, Object)
	 * @see Setters#defaultSetter(Setter) */
	@SuppressWarnings ("javadoc")
	public static <GItem, GValue> Field<GItem, GValue> defaultField(final Field<? super GItem, GValue> field, final GValue value) throws NullPointerException {
		return new DefaultField<>(field, value);
	}

	/** Diese Methode gibt ein {@link Field} zurück, welches beim Lesen am {@link Map#get(Object)} sowie beim Schreiben an {@link Map#put(Object, Object)}
	 * delegiert.
	 *
	 * @param <GEntry> Typ der Eingabe.
	 * @param <GValue> Typ des Werts.
	 * @param mapping {@link Map} zur Abbildung von einer Eingabe auf einen Wert.
	 * @return {@code mapping}-{@link Field}.
	 * @throws NullPointerException Wenn {@code mapping} {@code null} ist. */
	public static <GEntry, GValue> Field<GEntry, GValue> mappingField(final Map<GEntry, GValue> mapping) throws NullPointerException {
		return new MappingField<>(mapping);
	}

	/** Diese Methode ist eine effiziente Alternative zu {@link #compositeField(Getter, Setter) Fields.compositeField(Getters.navigatedGetter(toTarget, field),
	 * Setters.navigatedSetter(toTarget, field))}.
	 *
	 * @see Getters#navigatedGetter(Getter, Getter)
	 * @see Setters#navigatedSetter(Getter, Setter) */
	public static <GSource, GTarget, GValue> Field<GSource, GValue> navigatedField(final Getter<? super GSource, ? extends GTarget> toTarget,
		final Field<? super GTarget, GValue> field) throws NullPointerException {
		return new NavigatedField<>(toTarget, field);
	}

	/** Diese Methode ist eine effiziente Alternative zu {@link #translatedField(Field, Getter, Getter) Fields.translatedField(field,
	 * Translators.toTargetGetter(translator), Translators.toSourceGetter(translator))}.
	 *
	 * @see Translators#toTargetGetter(Translator)
	 * @see Translators#toSourceGetter(Translator) */
	public static <GItem, GSource, GTarget> Field<GItem, GTarget> translatedField(final Field<? super GItem, GSource> field,
		final Translator<GSource, GTarget> translator) throws NullPointerException {
		return new TranslatedField<>(field, translator);
	}

	/** Diese Methode gibt ein übersetztes {@link Field} zurück. Das erzeugte {@link Field} liefert beim Lesen den Wert, der über den gegebenen {@link Getter}
	 * {@code toTarget} aus dem über das gegebene {@link Field} ermittelten Wert berechnet wird. Beim Schreiben eines Werts wird dieser über dem gegebenen
	 * {@link Getter} {@code toSource} in einen Wert überfüght, welcher anschließend an das gegebene {@link Field} delegiert wird.
	 *
	 * @param <GItem> Typ des Datensatzes.
	 * @param <GTarget> Typ des Werts des erzeugten {@link Field}.
	 * @param <GSource> Typ des Werts des gegebenen {@link Field}.
	 * @param field {@link Field} zur Modifikation.
	 * @param toTarget {@link Getter} zum Umwandeln des Wert beim Lesen.
	 * @param toSource {@link Getter} zum Umwandeln des Wert beim Schreiben.
	 * @return {@code translated}-{@link Field}.
	 * @throws NullPointerException Wenn {@code field}, {@code toTarget} bzw. {@code toSource} {@code null} ist. */
	public static <GItem, GSource, GTarget> Field<GItem, GTarget> translatedField(final Field<? super GItem, GSource> field,
		final Getter<? super GSource, ? extends GTarget> toTarget, final Getter<? super GTarget, ? extends GSource> toSource) throws NullPointerException {
		return Fields.compositeField(Getters.translatedGetter(toTarget, field), Setters.translatedSetter(toSource, field));
	}

	/** Diese Methode gibt ein zusammengesetztes {@link Field} zurück, dessen Methoden an die des gegebenen {@link Getter} und {@link Setter} delegieren.
	 *
	 * @param <GItem> Typ des Datensatzes.
	 * @param <GValue> Typ des Werts der Eigenschaft.
	 * @param getter {@link Getter} für {@link Field#get(Object)}.
	 * @param setter {@link Setter} für {@link Field#set(Object, Object)}.
	 * @return {@code composite}-{@link Field}.
	 * @throws NullPointerException Wenn {@code getter} bzw. {@code setter} {@code null} ist. */
	public static <GItem, GValue> Field<GItem, GValue> compositeField(final Getter<? super GItem, ? extends GValue> getter,
		final Setter<? super GItem, ? super GValue> setter) throws NullPointerException {
		return new CompositeField<>(getter, setter);
	}

	/** Diese Methode ist eine Abkürzung für
	 * {@code #aggregatedField(Getter, Getter, Field) Fields.aggregatedField(Getters.neutralGetter(), Getters.neutralGetter(), field)}. */
	public static <GItem, GValue> Field<Iterable<? extends GItem>, GValue> aggregatedField(final Field<? super GItem, GValue> field) throws NullPointerException {
		return Fields.aggregatedField(Getters.<GValue>neutralGetter(), Getters.<GValue>neutralGetter(), field);
	}

	/** Diese Methode ist eine Abkürzung für {@link #aggregatedField(Getter, Getter, Object, Object, Field) Fields.aggregatedField(Getters.neutralGetter(),
	 * Getters.neutralGetter(), emptyTarget, mixedTarget, field)}. */
	public static <GEntry, GValue> Field<Iterable<? extends GEntry>, GValue> aggregatedField(final GValue emptyTarget, final GValue mixedTarget,
		final Field<? super GEntry, GValue> field) throws NullPointerException {
		return Fields.aggregatedField(Getters.<GValue>neutralGetter(), Getters.<GValue>neutralGetter(), emptyTarget, mixedTarget, field);
	}

	/** Diese Methode ist eine Abkürzung für {@link #aggregatedField(Getter, Getter, Object, Object, Field) Fields.aggregatedField(toTarget, toSource, null, null,
	 * field)}. */
	public static <GEntry, GSource, GTarget> Field<Iterable<? extends GEntry>, GTarget> aggregatedField(final Getter<? super GSource, ? extends GTarget> toTarget,
		final Getter<? super GTarget, ? extends GSource> toSource, final Field<? super GEntry, GSource> field) throws NullPointerException {
		return Fields.aggregatedField(toTarget, toSource, null, null, field);
	}

	/** Diese Methode ist eine Abkürzung für {@link #compositeField(Getter, Setter) Fields.compositeField(Getters.aggregatedGetter(toTarget, emptyTarget,
	 * mixedTarget, field), Setters.aggregatedSetter(toSource, field))}. Mit einem aggregierten {@link Field} können die Elemente des iterierbaren Datensatzes
	 * parallel modifiziert werden.
	 *
	 * @see Getters#aggregatedGetter(Getter, Object, Object, Getter)
	 * @see Setters#aggregatedSetter(Getter, Setter) */
	public static <GEntry, GSource, GTarget> Field<Iterable<? extends GEntry>, GTarget> aggregatedField(final Getter<? super GSource, ? extends GTarget> toTarget,
		final Getter<? super GTarget, ? extends GSource> toSource, final GTarget emptyTarget, final GTarget mixedTarget, final Field<? super GEntry, GSource> field)
		throws NullPointerException {
		return Fields.compositeField(Getters.aggregatedGetter(toTarget, emptyTarget, mixedTarget, field), Setters.aggregatedSetter(toSource, field));
	}

	/** Diese Methode ist eine effiziente Alternative zu {@link #compositeField(Getter, Setter) Fields.compositeField(Getters.conditionalGetter(condition,
	 * acceptField, rejectField), Setters.conditionalSetter(condition, acceptField, rejectField))}.
	 *
	 * @see Getters#conditionalGetter(Filter, Getter, Getter)
	 * @see Setters#conditionalSetter(Filter, Setter, Setter) */
	public static <GItem, GValue> Field<GItem, GValue> conditionalField(final Filter<? super GItem> condition, final Field<? super GItem, GValue> acceptField,
		final Field<? super GItem, GValue> rejectField) throws NullPointerException {
		return new ConditionalField<>(condition, acceptField, rejectField);
	}

	/** Diese Methode ist eine Abkürzung für {@link #synchronizedField(Object, Field) Fields.synchronizedField(field, field)}. */
	public static <GItem, GValue> Field<GItem, GValue> synchronizedField(final Field<? super GItem, GValue> field) throws NullPointerException {
		return Fields.synchronizedField(field, field);
	}

	/** Diese Methode gibt einen {@link Field} zurück, welcher das gegebenen {@link Field} über {@code synchronized(mutex)} synchronisiert. Wenn das
	 * Synchronisationsobjekt {@code null} ist, wird das erzeugte {@link Field} als Synchronisationsobjekt verwendet.
	 *
	 * @param <GItem> Typ des Datensatzes.
	 * @param <GValue> Typ des Werts.
	 * @param field {@link Field}.
	 * @param mutex Synchronisationsobjekt oder {@code null}.
	 * @return {@code synchronized}-{@link Field}.
	 * @throws NullPointerException Wenn {@code field} {@code null} ist. */
	public static <GItem, GValue> Field<GItem, GValue> synchronizedField(final Object mutex, final Field<? super GItem, GValue> field)
		throws NullPointerException {
		return new SynchronizedField<>(mutex, field);
	}

	/** Diese Methode ist eine Abkürzung für {@link Fields#toProperty(Object, Field) Fields.toProperty(null, field)}. */
	public static <GItem, GValue> Property<GValue> toProperty(final Field<? super GItem, GValue> field) throws NullPointerException {
		return Fields.toProperty(null, field);
	}

	/** Diese Methode gibt ein {@link Property} zurück, dessen Methoden mit dem gegebenen Datensatz an das gegebene {@link Field} delegieren.
	 *
	 * @param <GItem> Typ des Datensatzes.
	 * @param <GValue> Typ des Werts.
	 * @param item Datensatz.
	 * @param field {@link Field}.
	 * @return {@link Field}-{@link Property}.
	 * @throws NullPointerException Wenn {@code field} {@code null} ist. */
	public static <GItem, GValue> Property<GValue> toProperty(final GItem item, final Field<? super GItem, GValue> field) throws NullPointerException {
		return new FieldProperty<>(item, field);
	}

}