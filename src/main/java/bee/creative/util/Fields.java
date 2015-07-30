package bee.creative.util;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Diese Klasse implementiert Hilfsmethoden und Hilfsklassen zur Konstruktion und Verarbeitung von {@link Field}s.
 * 
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class Fields {

	/**
	 * Diese Schnittstelle definiert einen Adapter zur Modifikation eines {@link Set}, welches über ein {@link Field} einer gegebenen Eingabe gelesen bzw.
	 * geschrieben wird. Die Modifikation erfolgt an einer Kopie des {@link Set}, welche nach ihrer Modifikation über {@link #set(Object, Object)} zugewiesen
	 * wird.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 * @param <GItem> Typ der Elemente.
	 */
	public static interface SetField<GInput, GItem> extends ItemsField<GInput, GItem>, Field<GInput, Set<GItem>> {

	}

	/**
	 * Diese Schnittstelle definiert einen Adapter zur Modifikation einer {@link List}, welche über ein {@link Field} einer gegebenen Eingabe gelesen bzw.
	 * geschrieben wird. Die Modifikation erfolgt an einer Kopie der {@link List}, welche nach ihrer Modifikation über {@link #set(Object, Object)} zugewiesen
	 * wird.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 * @param <GItem> Typ der Elemente.
	 */
	public static interface ListField<GInput, GItem> extends ItemsField<GInput, GItem>, Field<GInput, List<GItem>> {

		/**
		 * Diese Methode verändert die Sammlung analog zu {@link List#add(int, Object)}.
		 * 
		 * @param input Eingabe.
		 * @param index Index.
		 * @param item Element.
		 */
		public void append(final GInput input, final int index, final GItem item);

		/**
		 * Diese Methode verändert die Sammlung analog zu {@link List#addAll(int, Collection)}.
		 * 
		 * @param input Eingabe.
		 * @param index Index.
		 * @param items Elemente.
		 */
		public void appendAll(final GInput input, final int index, final Iterable<? extends GItem> items);

		/**
		 * Diese Methode verändert die Sammlung analog zu {@link List#remove(int)}.
		 * 
		 * @param input Eingabe.
		 * @param index Index.
		 */
		public void remove(final GInput input, final int index);

	}

	/**
	 * Diese Schnittstelle definiert einen Adapter zur Modifikation einer {@link Map}, welche über ein {@link Field} einer gegebenen Eingabe gelesen bzw.
	 * geschrieben wird. Die Modifikation erfolgt an einer Kopie der {@link Map}, welche nach ihrer Modifikation über {@link #set(Object, Object)} zugewiesen
	 * wird.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GValue> Typ der Werte.
	 */
	public static interface MapField<GInput, GKey, GValue> extends EntriesField<GInput, GKey, GValue>, Field<GInput, Map<GKey, GValue>> {

	}

	/**
	 * Diese Schnittstelle definiert einen Adapter zur Modifikation einer {@link Collection}, welche über ein {@link Field} einer gegebenen Eingabe gelesen bzw.
	 * geschrieben wird. Die Modifikation erfolgt an einer Kopie der Objektsammlung, welche nach ihrer Modifikation über {@link Field#set(Object, Object)}
	 * zugewiesen wird.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 * @param <GItem> Typ der Elemente.
	 */
	public static interface ItemsField<GInput, GItem> {

		/**
		 * Diese Methode verändert die Sammlung analog zu {@link Collection#clear()}.
		 * 
		 * @param input Eingabe.
		 */
		public void clear(final GInput input);

		/**
		 * Diese Methode verändert die Sammlung analog zu {@link Collection#add(Object)}.
		 * 
		 * @param input Eingabe.
		 * @param item Element.
		 */
		public void append(final GInput input, final GItem item);

		/**
		 * Diese Methode verändert die Sammlung analog zu {@link Collection#addAll(Collection)}.
		 * 
		 * @param input Eingabe.
		 * @param items Elemente.
		 */
		public void appendAll(final GInput input, final Iterable<? extends GItem> items);

		/**
		 * Diese Methode verändert die Sammlung analog zu {@link Collection#remove(Object)}.
		 * 
		 * @param input Eingabe.
		 * @param item Element.
		 */
		public void remove(final GInput input, final Object item);

		/**
		 * Diese Methode verändert die Sammlung analog zu {@link Collection#removeAll(Collection)}.
		 * 
		 * @param input Eingabe.
		 * @param items Elemente.
		 */
		public void removeAll(final GInput input, final Iterable<?> items);

	}

	/**
	 * Diese Schnittstelle definiert einen Adapter zur Modifikation einer {@link Map}, welche über ein {@link Field} einer gegebenen Eingabe gelesen bzw.
	 * geschrieben wird. Die Modifikation erfolgt an einer Kopie der {@link Map}, welche nach ihrer Modifikation über {@link Field#set(Object, Object)} zugewiesen
	 * wird.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GValue> Typ der Werte.
	 */
	public static interface EntriesField<GInput, GKey, GValue> {

		/**
		 * Diese Methode verändert die {@link Map} analog zu {@link Map#clear()}.
		 * 
		 * @param input Eingabe.
		 */
		public void clear(final GInput input);

		/**
		 * Diese Methode verändert die {@link Map} analog zu {@link Map#put(Object, Object)}.
		 * 
		 * @param input Eingabe.
		 * @param key Schlüssel.
		 * @param value Wert.
		 */
		public void append(final GInput input, final GKey key, GValue value);

		/**
		 * Diese Methode verändert die {@link Map} analog zu {@link Map#putAll(Map)}.
		 * 
		 * @param input Eingabe.
		 * @param entries Elemente.
		 */
		public void appendAll(final GInput input, final Iterable<? extends Entry<? extends GKey, ? extends GValue>> entries);

		/**
		 * Diese Methode verändert die {@link Map} analog zu {@link Map#remove(Object)}.
		 * 
		 * @param input Eingabe.
		 * @param key Schlüssel.
		 */
		public void remove(final GInput input, final Object key);

		/**
		 * Diese Methode verändert die {@link Map} analog zu {@link Map#keySet()} mit {@link Set#removeAll(Collection)}.
		 * 
		 * @param input Eingabe.
		 * @param keys Schlüssel.
		 */
		public void removeAll(final GInput input, final Iterable<?> keys);

	}

	/**
	 * Diese Klasse implementiert ein abstraktes {@link Field}, welches das Schreiben ignoriert.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabeobjekts.
	 * @param <GValue> Typ des Werts der Eigenschaft.
	 */
	public static abstract class BaseField<GInput, GValue> implements Field<GInput, GValue> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void set(final GInput input, final GValue value) {
		}

	}

	/**
	 * Diese Klasse implementiert ein abstraktes {@link SetField}.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 * @param <GItem> Typ der Elemente.
	 */
	public static abstract class BaseSetField<GInput, GItem> extends BaseField<GInput, Set<GItem>> implements SetField<GInput, GItem> {

		/**
		 * Diese Methode gibt ein {@link SetField} zurück, welches das {@link Set} über das gegebene {@link Field} liest und schreibt.
		 * 
		 * @param <GInput> Typ der Eingabe.
		 * @param <GItem> Typ der Elemente.
		 * @param field {@link Field} zum Lesen und Schreiben des {@link Set}.
		 * @return {@link SetField}.
		 * @throws NullPointerException Wenn {@code field} {@code null} ist.
		 */
		public static <GInput, GItem> SetField<GInput, GItem> wrap(final Field<? super GInput, Set<GItem>> field) throws NullPointerException {
			if (field == null) throw new NullPointerException("field = null");
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
					return Objects.toStringCall("setField", field);
				}

			};
		}

		{}

		/**
		 * Diese Methode gibt eine Bearbeitungskopie des gegebenen {@link Set}s zurück.
		 * 
		 * @param value {@link Set}.
		 * @return Bearbeitungskopie.
		 */
		protected Set<GItem> copy(final Set<GItem> value) {
			return new HashSet<GItem>(value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void clear(final GInput input) {
			if (this.get(input).isEmpty()) return;
			this.set(input, Collections.<GItem>emptySet());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void append(final GInput input, final GItem item) {
			Set<GItem> value = this.get(input);
			if (value.contains(item)) return;
			value = this.copy(this.get(input));
			value.add(item);
			this.set(input, value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void appendAll(final GInput input, final Iterable<? extends GItem> items) {
			final Set<GItem> value = this.copy(this.get(input));
			if (!Iterables.appendAll(value, items)) return;
			this.set(input, value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void remove(final GInput input, final Object item) {
			Set<GItem> value = this.get(input);
			if (!value.contains(item)) return;
			value = this.copy(this.get(input));
			value.remove(item);
			this.set(input, value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void removeAll(final GInput input, final Iterable<?> items) {
			final Set<GItem> value = this.copy(this.get(input));
			if (!Iterables.removeAll(value, items)) return;
			this.set(input, value);
		}

	}

	/**
	 * Diese Klasse implementiert ein abstraktes {@link ListField}.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 * @param <GEntry> Typ der Elemente.
	 */
	public static abstract class BaseListField<GInput, GEntry> extends BaseField<GInput, List<GEntry>> implements ListField<GInput, GEntry> {

		/**
		 * Diese Methode gibt ein {@link ListField} zurück, welches das {@link List} über das gegebene {@link Field} liest und schreibt.
		 * 
		 * @param <GInput> Typ der Eingabe.
		 * @param <GItem> Typ der Elemente.
		 * @param field {@link Field} zum Lesen und Schreiben einer {@link List}.
		 * @return {@link ListField}.
		 * @throws NullPointerException Wenn {@code field} {@code null} ist.
		 */
		public static <GInput, GItem> ListField<GInput, GItem> wrap(final Field<? super GInput, List<GItem>> field) throws NullPointerException {
			if (field == null) throw new NullPointerException("field = null");
			return new BaseListField<GInput, GItem>() {

				@Override
				public List<GItem> get(final GInput input) {
					return field.get(input);
				}

				/**
				 * {@inheritDoc}
				 */
				@Override
				public void set(final GInput input, final List<GItem> value) {
					field.set(input, value);
				}

				@Override
				public String toString() {
					return Objects.toStringCall("listField", field);
				}

			};
		}

		{}

		/**
		 * Diese Methode gibt eine Bearbeitungskopie der gegebenen {@link List} zurück.
		 * 
		 * @param value {@link List}.
		 * @return Bearbeitungskopie.
		 */
		protected List<GEntry> copy(final List<GEntry> value) {
			return new ArrayList<GEntry>(value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void clear(final GInput input) {
			if (this.get(input).isEmpty()) return;
			this.set(input, Collections.<GEntry>emptyList());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void append(final GInput input, final GEntry item) {
			final List<GEntry> value = this.copy(this.get(input));
			value.add(item);
			this.set(input, value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void appendAll(final GInput input, final Iterable<? extends GEntry> items) {
			final List<GEntry> value = this.copy(this.get(input));
			if (!Iterables.appendAll(value, items)) return;
			this.set(input, value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void append(final GInput input, final int index, final GEntry item) {
			final List<GEntry> value = this.copy(this.get(input));
			value.add(index, item);
			this.set(input, value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void appendAll(final GInput input, final int index, final Iterable<? extends GEntry> items) {
			final List<GEntry> value = this.copy(this.get(input));
			if (!Iterables.appendAll(value.subList(index, index), items)) return;
			this.set(input, value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void remove(final GInput input, final int index) {
			final List<GEntry> value = this.copy(this.get(input));
			value.remove(index);
			this.set(input, value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void remove(final GInput input, final Object item) {
			final List<GEntry> value = this.get(input);
			final int index = value.indexOf(item);
			if (index < 0) return;
			this.remove(input, index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void removeAll(final GInput input, final Iterable<?> items) {
			final List<GEntry> value = this.copy(this.get(input));
			if (!Iterables.removeAll(value, items)) return;
			this.set(input, value);
		}

	}

	/**
	 * Diese Klasse implementiert ein abstraktes {@link MapField}.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GValue> Typ der Werte.
	 */
	public static abstract class BaseMapField<GInput, GKey, GValue> extends BaseField<GInput, Map<GKey, GValue>> implements MapField<GInput, GKey, GValue> {

		/**
		 * Diese Methode gibt ein {@link MapField} zurück, welches die {@link Map} über das gegebene {@link Field} liest und schreibt.
		 * 
		 * @param <GInput> Typ der Eingabe.
		 * @param <GKey> Typ der Schlüssel.
		 * @param <GValue> Typ der Werte.
		 * @param field {@link Field} zum Lesen und Schreiben einer {@link Map}.
		 * @return {@link MapField}.
		 * @throws NullPointerException Wenn {@code field} {@code null} ist.
		 */
		public static <GInput, GKey, GValue> MapField<GInput, GKey, GValue> wrap(final Field<? super GInput, Map<GKey, GValue>> field) throws NullPointerException {
			if (field == null) throw new NullPointerException("field = null");
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
					return Objects.toStringCall("mapField", field);
				}

			};
		}

		{}

		/**
		 * Diese Methode gibt eine Bearbeitungskopie der gegebenen {@link Map} zurück.
		 * 
		 * @param value {@link Map}.
		 * @return Bearbeitungskopie.
		 */
		protected Map<GKey, GValue> copy(final Map<GKey, GValue> value) {
			return new HashMap<GKey, GValue>(value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void clear(final GInput input) {
			if (this.get(input).isEmpty()) return;
			this.set(input, Collections.<GKey, GValue>emptyMap());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void append(final GInput input, final GKey key, final GValue value) {
			Map<GKey, GValue> map = this.get(input);
			if (Objects.equals(map.get(key), value) && ((value != null) || map.containsKey(key))) return;
			map = this.copy(this.get(input));
			map.put(key, value);
			this.set(input, map);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void appendAll(final GInput input, final Iterable<? extends Entry<? extends GKey, ? extends GValue>> entries) {
			final Map<GKey, GValue> map = this.copy(this.get(input));
			boolean modified = false;
			for (final Entry<? extends GKey, ? extends GValue> entry: entries) {
				modified = !Objects.equals(map.put(entry.getKey(), entry.getValue()), entry.getValue()) || modified;
			}
			if (!modified) return;
			this.set(input, map);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void remove(final GInput input, final Object key) {
			Map<GKey, GValue> map = this.get(input);
			if (!map.containsKey(key)) return;
			map = this.copy(map);
			map.remove(key);
			this.set(input, map);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void removeAll(final GInput input, final Iterable<?> keys) {
			final Map<GKey, GValue> map = this.copy(this.get(input));
			if (!Iterables.removeAll(map.keySet(), keys)) return;
			this.set(input, map);
		}

	}

	{}

	/**
	 * Dieses Feld speichert das indifferente Objekt für {@link #aggregateField(Field, Converter, Converter, Object, Object)}.
	 */
	static final Object SKIP = new Object();

	{}

	/**
	 * Diese Methode gibt ein {@link Field} zurück, welches die über das native {@link java.lang.reflect.Field Datenfeld} gegebene Eigenschaft der Eingabe
	 * abbildet.
	 * 
	 * @see java.lang.reflect.Field#get(Object)
	 * @see java.lang.reflect.Field#set(Object, Object)
	 * @param <GInput> Typ der Eingabe.
	 * @param <GValue> Typ des Werts der Eigenschaft.
	 * @param field Datenfeld.
	 * @return {@code java}-{@link Field}.
	 * @throws NullPointerException Wenn {@code field} {@code null} ist.
	 */
	public static <GInput, GValue> Field<GInput, GValue> javaField(final java.lang.reflect.Field field) throws NullPointerException {
		if (field == null) throw new NullPointerException("field = null");
		return new Field<GInput, GValue>() {

			@Override
			public GValue get(final GInput input) {
				try {
					@SuppressWarnings ("unchecked")
					final GValue result = (GValue)field.get(input);
					return result;
				} catch (final IllegalAccessException e) {
					throw new IllegalArgumentException(e);
				}
			}

			@Override
			public void set(final GInput input, final GValue value) {
				try {
					field.set(input, value);
				} catch (final IllegalAccessException e) {
					throw new IllegalArgumentException(e);
				}
			}

			@Override
			public String toString() {
				return Objects.toStringCall("javaField", field);
			}

		};
	}

	/**
	 * Diese Methode gibt ein {@link Field} zurück, welches die über ein natives {@link java.lang.reflect.Field Datenfeld} beschriebene Eigenschaft der Eingabe
	 * abbildet. Das Datenfeld wird bei jedem Zugriff dynamisch über die {@link Class} der Eingabe ermittelt.
	 * 
	 * @see Class#getField(String)
	 * @param <GInput> Typ der Eingabe.
	 * @param <GValue> Typ des Werts der Eigenschaft.
	 * @param fieldName Name des Datenfelds.
	 * @return {@code java}-{@link Field}.
	 * @throws NullPointerException Wenn {@code field} {@code null} ist.
	 */
	public static <GInput, GValue> Field<GInput, GValue> javaField(final String fieldName) throws NullPointerException {
		if (fieldName == null) throw new NullPointerException("fieldName = null");
		return new Field<GInput, GValue>() {

			@Override
			public GValue get(final GInput input) {
				if (input == null) throw new NullPointerException("input = null");
				try {
					final java.lang.reflect.Field field = input.getClass().getField(fieldName);
					try {
						@SuppressWarnings ("unchecked")
						final GValue result = (GValue)field.get(input);
						return result;
					} catch (final IllegalAccessException e) {
						throw new IllegalArgumentException(e);
					}
				} catch (final SecurityException | NoSuchFieldException e) {
					throw new IllegalArgumentException(e);
				}
			}

			@Override
			public void set(final GInput input, final GValue value) {
				if (input == null) throw new NullPointerException("input = null");
				try {
					final java.lang.reflect.Field field = input.getClass().getField(fieldName);
					try {
						field.set(input, value);
					} catch (final IllegalAccessException e) {
						throw new IllegalArgumentException(e);
					}
				} catch (final SecurityException | NoSuchFieldException e) {
					throw new IllegalArgumentException(e);
				}
			}

			@Override
			public String toString() {
				return Objects.toStringCall("javaField", fieldName);
			}

		};
	}

	/**
	 * Diese Methode gibt ein {@link Field} zurück, welches die über zwei native {@link java.lang.reflect.Method Methoden} gegebene Eigenschaft der Eingabe
	 * abbildet.
	 * 
	 * @see java.lang.reflect.Method#invoke(Object, Object...)
	 * @param <GInput> Typ der Eingabe.
	 * @param <GValue> Typ des Werts der Eigenschaft.
	 * @param getMethod Methode zum Lesen der Eigenschaft, z.B. {@code input.get()}.
	 * @param setMethod Methode zum Schreiben der Eigenschaft, z.B. {@code input.set(value)}.
	 * @return {@code java}-{@link Field}.
	 * @throws NullPointerException Wenn {@code getMethod} bzw. {@code setMethod} {@code null} ist.
	 */
	public static <GInput, GValue> Field<GInput, GValue> javaField(final java.lang.reflect.Method getMethod, final java.lang.reflect.Method setMethod)
		throws NullPointerException {
		if (getMethod == null) throw new NullPointerException("getMethod = null");
		if (setMethod == null) throw new NullPointerException("setMethod = null");
		return new Field<GInput, GValue>() {

			@Override
			public GValue get(final GInput input) {
				try {
					@SuppressWarnings ("unchecked")
					final GValue result = (GValue)getMethod.invoke(input);
					return result;
				} catch (IllegalAccessException | InvocationTargetException cause) {
					throw new IllegalArgumentException(cause);
				}
			}

			@Override
			public void set(final GInput input, final GValue value) {
				try {
					setMethod.invoke(input, value);
				} catch (IllegalAccessException | InvocationTargetException cause) {
					throw new IllegalArgumentException(cause);
				}
			}

			@Override
			public String toString() {
				return Objects.toStringCall("javaField", getMethod, setMethod);
			}

		};
	}

	/**
	 * Diese Methode gibt ein {@link Field} zurück, welches die über ein natives {@link java.lang.reflect.Field Datenfeld} beschriebene Eigenschaft der Eingabe
	 * abbildet.
	 * 
	 * @param <GInput> Typ der Eingabe.
	 * @param <GValue> Typ des Werts der Eigenschaft.
	 * @param inputType Klasse der Eingabe.
	 * @param fieldName Name des Datenfelds.
	 * @return {@code java}-{@link Field}.
	 * @throws SecurityException Wenn {@link Class#getField(String)} eine entsprechende Ausnahme auslöst.
	 * @throws NoSuchFieldException Wenn {@link Class#getField(String)} eine entsprechende Ausnahme auslöst.
	 * @throws NullPointerException Wenn {@code inputType} bzw. {@code fieldName} {@code null} ist.
	 */
	public static <GInput, GValue> Field<GInput, GValue> javaField(final Class<GInput> inputType, final String fieldName) throws SecurityException,
		NoSuchFieldException, NullPointerException {
		if (inputType == null) throw new NullPointerException("inputType = null");
		if (fieldName == null) throw new NullPointerException("fieldName = null");
		return Fields.javaField(inputType.getField(fieldName));
	}

	/**
	 * Diese Methode gibt ein {@link Field} zurück, welches die über zwei native {@link java.lang.reflect.Method Methoden} beschriebene Eigenschaft der Eingabe
	 * abbildet. Die Methoden werden bei jedem Zugriff dynamisch über die {@link Class} der Eingabe ermittelt.
	 * 
	 * @param <GInput> Typ der Eingabe.
	 * @param <GValue> Typ des Werts der Eigenschaft.
	 * @param valueType Klasse des Werts der Eigenschaft.
	 * @param getName Name der Methode zum Lesen der Eigenschaft, z.B. {@code "get"}.
	 * @param setName Name der Methode zum Schreiben der Eigenschaft, z.B. {@code "set"}.
	 * @return {@code java}-{@link Field}.
	 * @throws NullPointerException Wenn {@code valueType}, {@code getName} bzw. {@code setName} {@code null} ist.
	 */
	public static <GInput, GValue> Field<GInput, GValue> javaField(final Class<GValue> valueType, final String getName, final String setName)
		throws NullPointerException {
		if (valueType == null) throw new NullPointerException("valueType = null");
		if (getName == null) throw new NullPointerException("getName = null");
		if (setName == null) throw new NullPointerException("setName = null");
		return new Field<GInput, GValue>() {

			@Override
			public GValue get(final GInput input) {
				if (input == null) throw new NullPointerException("input = null");
				try {
					final java.lang.reflect.Method method = input.getClass().getMethod(getName);
					try {
						@SuppressWarnings ("unchecked")
						final GValue result = (GValue)method.invoke(input);
						return result;
					} catch (IllegalAccessException | InvocationTargetException cause) {
						throw new IllegalArgumentException(cause);
					}
				} catch (final SecurityException | NoSuchMethodException cause) {
					throw new IllegalArgumentException(cause);
				}
			}

			@Override
			public void set(final GInput input, final GValue value) {
				if (input == null) throw new NullPointerException("input = null");
				try {
					final java.lang.reflect.Method method = input.getClass().getMethod(setName, valueType);
					try {
						method.invoke(input, value);
					} catch (IllegalAccessException | InvocationTargetException cause) {
						throw new IllegalArgumentException(cause);
					}
				} catch (final SecurityException | NoSuchMethodException cause) {
					throw new IllegalArgumentException(cause);
				}
			}

			@Override
			public String toString() {
				return Objects.toStringCall("javaField", getName, setName);
			}

		};
	}

	/**
	 * Diese Methode gibt ein {@link Field} zurück, welches die über zwei native {@link java.lang.reflect.Method Methoden} beschriebene Eigenschaft der Eingabe
	 * abbildet.
	 * 
	 * @param <GInput> Typ der Eingabe.
	 * @param <GValue> Typ des Werts der Eigenschaft.
	 * @param inputType Klasse der Eingabe.
	 * @param valueType Klasse des Werts der Eigenschaft.
	 * @param getName Name der Methode zum Lesen der Eigenschaft, z.B. {@code "get"}.
	 * @param setName Name der Methode zum Schreiben der Eigenschaft, z.B. {@code "set"}.
	 * @return {@code java}-{@link Field}.
	 * @throws SecurityException Wenn {@link Class#getMethod(String, Class...)} eine entsprechende Ausnahme auslöst.
	 * @throws NoSuchMethodException Wenn {@link Class#getMethod(String, Class...)} eine entsprechende Ausnahme auslöst.
	 * @throws NullPointerException Wenn {@code inputType}, {@code valueType}, {@code getName} bzw. {@code setName} {@code null} ist.
	 */
	public static <GInput, GValue> Field<GInput, GValue> javaField(final Class<GInput> inputType, final Class<GValue> valueType, final String getName,
		final String setName) throws SecurityException, NoSuchMethodException, NullPointerException {
		if (inputType == null) throw new NullPointerException("inputType = null");
		if (valueType == null) throw new NullPointerException("valueType = null");
		if (getName == null) throw new NullPointerException("getName = null");
		if (setName == null) throw new NullPointerException("setName = null");
		return Fields.javaField(inputType.getMethod(getName), inputType.getMethod(setName, valueType));
	}

	/**
	 * Diese Methode gibt ein {@link Field} zurück, welches beim Lesen den gegebenen Wert liefert und das Schreiben ignoriert.
	 * 
	 * @param <GValue> Typ des Werts.
	 * @param value Wert.
	 * @return {@code value}-{@link Field}.
	 */
	public static <GValue> Field<Object, GValue> valueField(final GValue value) {
		return new BaseField<Object, GValue>() {

			@Override
			public GValue get(final Object input) {
				return value;
			}

			@Override
			public String toString() {
				return Objects.toStringCall("valueField", value);
			}

		};
	}

	/**
	 * Diese Methode gibt ein navigierendes {@link Field} zurück, dass von seiner Eingabe mit dem gegebenen {@link Converter} zur Eingabe des gegebenen
	 * {@link Field} navigiert. Das Lesen der Eigenschaft einer Einagbe {@code input} erfolgt damit über {@code field.get(converter.convert(input))} und ein Wert
	 * {@code value} wird dann via {@code field.set(converter.convert(input), value)} geschrieben.
	 * 
	 * @param <GInput> Typ der Eingabe.
	 * @param <GOutput> Typ des Elements als Ausgabe des {@link Converter}s sowie als Eingabe des {@link Field}s.
	 * @param <GValue> Typ des Werts.
	 * @param converter {@link Converter} zur Navigation.
	 * @param field {@link Field} zur Manipulation.
	 * @return {@code converted}-{@link Field}.
	 * @throws NullPointerException Wenn {@code converter} bzw. {@code field} {@code null} ist.
	 */
	public static <GInput, GOutput, GValue> Field<GInput, GValue> navigatedField(final Converter<? super GInput, ? extends GOutput> converter,
		final Field<? super GOutput, GValue> field) throws NullPointerException {
		if (converter == null) throw new NullPointerException("converter = null");
		if (field == null) throw new NullPointerException("field = null");
		return new Field<GInput, GValue>() {

			@Override
			public GValue get(final GInput input) {
				return field.get(converter.convert(input));
			}

			@Override
			public void set(final GInput input, final GValue value) {
				field.set(converter.convert(input), value);
			}

			@Override
			public String toString() {
				return Objects.toStringCall("navigatedField", converter, field);
			}

		};
	}

	/**
	 * Diese Methode gibt ein aggregierendes {@link Field} zurück, welches keine Umwandlungen durchführt sowie {@code null} als Leer- und Mischwert nutzt.
	 * 
	 * @see #aggregateField(Field, Converter, Converter, Object, Object)
	 * @param <GInput> Typ der Elemente in der iterierbaren Eingabe.
	 * @param <GValue> Typ des Werts der Eigenschaft der Elemente.
	 * @param field {@link Field}.
	 * @return {@code aggregate}-{@link Field}.
	 * @throws NullPointerException Wenn {@code field} {@code null} ist.
	 */
	public static <GInput, GValue> Field<Iterable<? extends GInput>, GValue> aggregateField(final Field<? super GInput, GValue> field)
		throws NullPointerException {
		return Fields.aggregateField(field, Converters.<GValue>neutralConverter(), Converters.<GValue>neutralConverter(), null, null);
	}

	/**
	 * Diese Methode gibt ein aggregierendes {@link Field} zurück, welches keine Umwandlungen durchführt sowie die gegebenen Leer- und Mischwerte nutzt.
	 * 
	 * @see #aggregateField(Field, Converter, Converter, Object, Object)
	 * @param <GItem> Typ der Elemente in der iterierbaren Eingabe.
	 * @param <GValue> Typ des Werts der Eigenschaft der Elemente.
	 * @param field {@link Field}.
	 * @param emptyValue Leerwert.
	 * @param mixedValue Mischwert.
	 * @return {@code aggregate}-{@link Field}.
	 * @throws NullPointerException Wenn {@code field} {@code null} ist.
	 */
	public static <GItem, GValue> Field<Iterable<? extends GItem>, GValue> aggregateField(final Field<? super GItem, GValue> field, final GValue emptyValue,
		final GValue mixedValue) throws NullPointerException {
		return Fields.aggregateField(field, Converters.<GValue>neutralConverter(), Converters.<GValue>neutralConverter(), emptyValue, mixedValue);
	}

	/**
	 * Diese Methode gibt ein aggregierendes {@link Field} zurück, welches die gegebenen {@link Converter} zum Parsen und Formatieren sowie {@code null} als Leer-
	 * und Mischwert nutzt. Wenn {@code parser} {@code null} ist, wird das Schreiben ignoriert. Wenn {@code formatter} {@code null} ist, wird beim Lesen immer
	 * {@code null} geliefert.
	 * 
	 * @see #aggregateField(Field, Converter, Converter, Object, Object)
	 * @param <GItem> Typ der Elemente in der iterierbaren Eingabe.
	 * @param <GValue> Typ des Werts dieses {@link Field}s.
	 * @param <GValue2> Typ des Werts der Elemente.
	 * @param field {@link Field}.
	 * @param parser {@link Converter} zum Umwandeln des externen in den internen Wert (Parsen) für das Schreiben oder {@code null}.
	 * @param formatter {@link Converter} zum Umwandeln des internen in den externen Wert (Formatieren) für das Lesen oder {@code null}.
	 * @return {@code aggregate}-{@link Field}.
	 * @throws NullPointerException Wenn {@code field} {@code null} ist.
	 */
	public static <GItem, GValue, GValue2> Field<Iterable<? extends GItem>, GValue> aggregateField(final Field<? super GItem, GValue2> field,
		final Converter<? super GValue, ? extends GValue2> parser, final Converter<? super GValue2, ? extends GValue> formatter) throws NullPointerException {
		return Fields.aggregateField(field, parser, formatter, null, null);
	}

	/**
	 * Diese Methode gibt ein aggregierendes {@link Field} zurück, welches die gegebenen {@link Converter} zum Parsen und Formatieren sowie die gegebenen Leer-
	 * und Mischwerte nutzt. Wenn {@code parser} {@code null} ist, wird das Schreiben ignoriert. Wenn {@code formatter} {@code null} ist, wird beim Lesen immer
	 * {@code null} geliefert.
	 * <p>
	 * Das gelieferte {@link Field} gibt beim Lesen den Wert zurück, der unter allen Elementen der iterierbaren Eingabe {@link Objects#equals(Object) äquivalent}
	 * ist. Ermittelt wird der Wert der Eigenschaft eines Elements mit Hilfe des gegebenen {@link Field}. Wenn kein einheitlicher Wert existiert, wird beim Lesen
	 * der gegebene Mischwert geliefert. Beim Schreiben wird der Wert der Eigenschaft für alle Elemente der iterierbaren Eingabe über das gegebene {@link Field}
	 * zugewiesen.
	 * <p>
	 * Mit einem aggregierenden {@link Field} können mehrere Elemente parallel modifiziert werden, indem jedes aus der iterierbaren Eingabe stammende Elemente zur
	 * Bearbeitung an das gegebene {@link Field} weitergeleitet wird. Ein dabei gegebenenfalls notwendiges Standardverhalten wird dazu über entsprechende Werte
	 * bereit gestellt.<br>
	 * Für das Standardverhalten beim Lesen des Wert der Eigenschaft lassen sich drei Zustände unterscheiden:
	 * <ul>
	 * <li>Der {@link Converter} zum Umwandeln des internen in den externen Wert (Formatieren) ist {@code null}. Hier wird der Wert {@code null} geliefert.</li>
	 * <li>Die Eingabe ist leer oder {@code null}. Hier wird der gegebene Leerwert geliefert.</li>
	 * <li>Die für jedes Element ermittelten Werte der Eigenschaft unterscheiden sich. Hier wird der gegebene Mischwert geliefert.</li>
	 * </ul>
	 * 
	 * @param <GItem> Typ der Elemente in der iterierbaren Eingabe.
	 * @param <GValue> Typ des Werts dieses {@link Field}s.
	 * @param <GValue2> Typ des Werts der Elemente.
	 * @param field {@link Field}.
	 * @param parser {@link Converter} zum Umwandeln des externen in den internen Wert (Parsen) für das Schreiben oder {@code null}.
	 * @param formatter {@link Converter} zum Umwandeln des internen in den externen Wert (Formatieren) für das Lesen oder {@code null}.
	 * @param emptyValue Leerwert.
	 * @param mixedValue Mischwert.
	 * @return {@code aggregate}-{@link Field}.
	 * @throws NullPointerException Wenn {@code field} {@code null} ist.
	 */
	public static <GItem, GValue, GValue2> Field<Iterable<? extends GItem>, GValue> aggregateField(final Field<? super GItem, GValue2> field,
		final Converter<? super GValue, ? extends GValue2> parser, final Converter<? super GValue2, ? extends GValue> formatter, final GValue emptyValue,
		final GValue mixedValue) throws NullPointerException {
		if (field == null) throw new NullPointerException("field = null");
		return new Field<Iterable<? extends GItem>, GValue>() {

			@Override
			public GValue get(final Iterable<? extends GItem> input) {
				if (formatter == null) return null;
				if (input == null) return emptyValue;
				GValue2 next = null;
				Object last = Fields.SKIP;
				for (final GItem input2: input) {
					next = field.get(input2);
					if ((last != Fields.SKIP) && !Objects.equals(last, next)) return mixedValue;
					last = next;
				}
				if (last == Fields.SKIP) return emptyValue;
				return formatter.convert(next);
			}

			@Override
			public void set(final Iterable<? extends GItem> input, final GValue value) {
				if ((input == null) || (parser == null)) return;
				final GValue2 value2 = parser.convert(value);
				for (final GItem entry: Iterables.iterable(input)) {
					field.set(entry, value2);
				}
			}

			@Override
			public String toString() {
				return Objects.toStringCall("aggregateField", field, parser, formatter, emptyValue, mixedValue);
			}

		};
	}

	/**
	 * Diese Methode erzeugt ein umkodierendes {@link Field}, dessen Methoden zum Lesen und Schreiben des Werts mit je einem {@link Converter} an das gegebene
	 * {@link Field} angebunden sind, und gibt es zurück.
	 * <p>
	 * * Diese Klasse implementiert ein umkodierendes {@link Field}, dessen Methoden zum Lesen und Schreiben des Werts mit je einem {@link Converter} an ein
	 * gegebenes {@link Field} angebunden sind. Beim Lesen des Werts der Eigenschaft einer Eingabe via {@link #get(Object)} wird der über das gegebene
	 * {@link Field} ermittelte interne Wert mit dem formatierenden {@link Converter} in den externen Wert überführt, sofern dieser {@link Converter} nicht
	 * {@code null} ist. Andernfalls wird immer {@code null} geliefert. Beim Schreiben des Werts via {@link #set(Object, Object)} wird der gegebene externe Wert
	 * mit dem parsenden {@link Converter} in den internen Wert überführt und der über das gegebene {@link Field} gesetzt, sofern dieser {@link Converter} nicht
	 * {@code null} ist. Andernfalls das Schreiben ignoriert.
	 * 
	 * @see TranscodedField
	 * @param <GInput> Typ der Eingabe.
	 * @param <GValue> Typ des Werts der Eigenschaft.
	 * @param <GValue2> Typ des internen Werts des {@link Field}s.
	 * @param field {@link Field}.
	 * @param parser {@link Converter} zum Umwandeln des externen in den internen Wert (Parsen) für das Schreiben oder {@code null}.
	 * @param formatter {@link Converter} zum Umwandeln des internen in den externen Wert (Formatieren) für das Lesen oder {@code null}.
	 * @return {@link TranscodedField}.
	 * @throws NullPointerException Wenn das gegebene {@link Field} {@code null} ist.
	 */
	public static <GInput, GValue, GValue2> Field<GInput, GValue> transcodedField(final Field<? super GInput, GValue2> field,
		final Converter<? super GValue, ? extends GValue2> parser, final Converter<? super GValue2, ? extends GValue> formatter) throws NullPointerException {
		if (field == null) throw new NullPointerException("field = null");
		return new Field<GInput, GValue>() {

			@Override
			public GValue get(final GInput input) {
				if (formatter == null) return null;
				return formatter.convert(field.get(input));
			}

			@Override
			public void set(final GInput input, final GValue value) {
				if (parser == null) return;
				field.set(input, parser.convert(value));
			}

			@Override
			public String toString() {
				return Objects.toStringCall("transcodedField", field, parser, formatter);
			}

		};
	}

	/**
	 * Diese Methode gibt ein bedingtes {@link Field} zurück, welches über die Weiterleitug der Eingabe mit Hilfe eines {@link Filter} entscheiden. Wenn der
	 * gegebene {@link Filter} eine Eingabe akzeptiert, wird diese an {@code accept} delegiert. Andernfalls wird sie an {@code reject} delegiert.
	 * 
	 * @param <GInput> Typ der Eingabe.
	 * @param <GValue> Typ des Werts der Eigenschaft.
	 * @param condition {@link Filter}.
	 * @param accept {@code accept}-{@link Field}.
	 * @param reject {@code reject}-{@link Field}.
	 * @return {@code conditional}-{@link Field}.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
	 */
	public static <GInput, GValue> Field<GInput, GValue> conditionalField(final Filter<? super GInput> condition, final Field<? super GInput, GValue> accept,
		final Field<? super GInput, GValue> reject) throws NullPointerException {
		if (condition == null) throw new NullPointerException("condition = null");
		if (accept == null) throw new NullPointerException("accept = null");
		if (reject == null) throw new NullPointerException("reject = null");
		return new Field<GInput, GValue>() {

			@Override
			public GValue get(final GInput input) {
				return condition.accept(input) ? accept.get(input) : reject.get(input);
			}

			@Override
			public void set(final GInput input, final GValue value) {
				if (condition.accept(input)) {
					accept.set(input, value);
				} else {
					reject.set(input, value);
				}
			}

			@Override
			public String toString() {
				return Objects.toStringCall("conditionalField", condition, accept, reject);
			}

		};
	}

	/**
	 * Diese Methode gibt ein {@link Field} zurück, welches das gegebene {@link Field} via {@code synchronized(this)} synchronisiert.
	 * 
	 * @param <GInput> Typ der Eingabe.
	 * @param <GValue> Typ des Werts der Eigenschaft.
	 * @param field {@link Field}.
	 * @return {@code synchronized}-{@link Field}.
	 * @throws NullPointerException Wenn {@code field} {@code null} ist.
	 */
	public static <GInput, GValue> Field<GInput, GValue> synchronizedField(final Field<? super GInput, GValue> field) throws NullPointerException {
		if (field == null) throw new NullPointerException("field = null");
		return new Field<GInput, GValue>() {

			@Override
			public GValue get(final GInput input) {
				synchronized (this) {
					return field.get(input);
				}
			}

			@Override
			public void set(final GInput input, final GValue value) {
				synchronized (this) {
					field.set(input, value);
				}
			}

			@Override
			public String toString() {
				return Objects.toStringCall("synchronizedField", field);
			}

		};
	}

}