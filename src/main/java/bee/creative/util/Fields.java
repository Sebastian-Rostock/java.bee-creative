package bee.creative.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import bee.creative.util.Converters.VoidConverter;

/**
 * Diese Klasse implementiert Hilfsmethoden und Hilfsklassen zur Konstruktion und Verarbeitung von {@link Field}s.
 * 
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class Fields {

	/**
	 * Diese Klasse implementiert ein abstraktes {@link Field}.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabeobjekts.
	 * @param <GValue> Typ des Werts der Eigenschaft.
	 */
	static abstract class AbstractField<GInput, GValue> implements Field<GInput, GValue> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return 0;
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
	 * Diese Klasse implementiert ein abstraktes {@link Field}, das seine Schnittstelle an ein gegebenes {@link Field} delegiert.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 * @param <GValue> Typ des Werts der Eigenschaft.
	 * @param <GInput2> Typ der Eingabe des gegebenen {@link Field}s.
	 * @param <GValue2> Typ des Werts des gegebenen {@link Field}s.
	 */
	static abstract class AbstractDelegatingField<GInput, GValue, GInput2, GValue2> implements Field<GInput, GValue> {

		/**
		 * Dieses Feld speichert das {@link Field}.
		 */
		final Field<? super GInput2, GValue2> field;

		/**
		 * Dieser Konstruktor initialisiert das {@link Field}.
		 * 
		 * @param field {@link Field}.
		 * @throws NullPointerException Wenn das gegebene {@link Field} {@code null} ist.
		 */
		public AbstractDelegatingField(final Field<? super GInput2, GValue2> field) throws NullPointerException {
			if (field == null) throw new NullPointerException();
			this.field = field;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return Objects.hash(this.field);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this.field);
		}

	}

	/**
	 * Diese Schnittstelle definiert einen Adapter zur Modifikation einer {@link Map}, die über ein {@link Field} einer gegebenen Eingabe gelesen bzw. geschrieben
	 * wird.<br>
	 * Die Modifikation erfolgt an einer Kopie der {@link Map}, welche nach ihrer Modifikation über {@link #set(Object, Object)} zugewiesen wird.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GValue> Typ der Werte.
	 */
	public static interface MapField<GInput, GKey, GValue> extends Field<GInput, Map<GKey, GValue>> {

		/**
		 * Diese Methode ist analog zu {@link Map#clear()}.
		 * 
		 * @param input Eingabe.
		 */
		public void clear(final GInput input);

		/**
		 * Diese Methode ist analog zu {@link Map#put(Object, Object)}.
		 * 
		 * @param input Eingabe.
		 * @param key Schlüssel.
		 * @param value Wert.
		 */
		public void append(final GInput input, final GKey key, GValue value);

		/**
		 * Diese Methode ist analog zu {@link Map#putAll(Map)}.
		 * 
		 * @param input Eingabe.
		 * @param entries Elemente.
		 */
		public void appendAll(final GInput input, final Iterable<? extends Entry<? extends GKey, ? extends GValue>> entries);

		/**
		 * Diese Methode ist analog zu {@link Map#remove(Object)}.
		 * 
		 * @param input Eingabe.
		 * @param key Schlüssel.
		 */
		public void remove(final GInput input, final Object key);

		/**
		 * Diese Methode ist analog zu {@link Map#keySet()} mit {@link Set#removeAll(Collection)}.
		 * 
		 * @param input Eingabe.
		 * @param keys Schlüssel.
		 */
		public void removeAll(final GInput input, final Iterable<?> keys);

	}

	/**
	 * Diese Schnittstelle definiert einen Adapter zur Modifikation eines {@link Set}, das über ein {@link Field} einer gegebenen Eingabe gelesen bzw. geschrieben
	 * wird.<br>
	 * Die Modifikation erfolgt an einer Kopie des {@link Set}, welche nach ihrer Modifikation über {@link #set(Object, Object)} zugewiesen wird.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 * @param <GEntry> Typ der Elemente.
	 */
	public static interface SetField<GInput, GEntry> extends ValuesField<GInput, Set<GEntry>, GEntry> {

		/**
		 * Diese Methode ist analog zu {@link Set#clear()}.
		 * 
		 * @param input Eingabe.
		 */
		@Override
		public void clear(final GInput input);

		/**
		 * Diese Methode ist analog zu {@link Set#add(Object)}.
		 * 
		 * @param input Eingabe.
		 * @param entry Element.
		 */
		@Override
		public void append(final GInput input, final GEntry entry);

		/**
		 * Diese Methode ist analog zu {@link Set#addAll(Collection)}.
		 * 
		 * @param input Eingabe.
		 * @param entries Elemente.
		 */
		@Override
		public void appendAll(final GInput input, final Iterable<? extends GEntry> entries);

		/**
		 * Diese Methode ist analog zu {@link Set#remove(Object)}.
		 * 
		 * @param input Eingabe.
		 * @param entry Element.
		 */
		@Override
		public void remove(final GInput input, final Object entry);

		/**
		 * Diese Methode ist analog zu {@link Set#removeAll(Collection)}.
		 * 
		 * @param input Eingabe.
		 * @param entries Elemente.
		 */
		@Override
		public void removeAll(final GInput input, final Iterable<?> entries);

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object);

	}

	/**
	 * Diese Schnittstelle definiert einen Adapter zur Modifikation einer {@link List}, das über ein {@link Field} einer gegebenen Eingabe gelesen bzw.
	 * geschrieben wird.<br>
	 * Die Modifikation erfolgt an einer Kopie der {@link List}, welche nach ihrer Modifikation über {@link #set(Object, Object)} zugewiesen wird.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 * @param <GEntry> Typ der Elemente.
	 */
	public static interface ListField<GInput, GEntry> extends ValuesField<GInput, List<GEntry>, GEntry> {

		/**
		 * Diese Methode ist analog zu {@link List#clear()}.
		 * 
		 * @param input Eingabe.
		 */
		@Override
		public void clear(final GInput input);

		/**
		 * Diese Methode ist analog zu {@link List#add(Object)}.
		 * 
		 * @param input Eingabe.
		 * @param entry Element.
		 */
		@Override
		public void append(final GInput input, final GEntry entry);

		/**
		 * Diese Methode ist analog zu {@link List#addAll(Collection)}.
		 * 
		 * @param input Eingabe.
		 * @param entries Elemente.
		 */
		@Override
		public void appendAll(final GInput input, final Iterable<? extends GEntry> entries);

		/**
		 * Diese Methode ist analog zu {@link List#add(int, Object)}.
		 * 
		 * @param input Eingabe.
		 * @param index Index.
		 * @param entry Element.
		 */
		public void append(final GInput input, final int index, final GEntry entry);

		/**
		 * Diese Methode ist analog zu {@link List#addAll(int, Collection)}.
		 * 
		 * @param input Eingabe.
		 * @param index Index.
		 * @param entries Elemente.
		 */
		public void appendAll(final GInput input, final int index, final Iterable<? extends GEntry> entries);

		/**
		 * Diese Methode ist analog zu {@link List#remove(int)}.
		 * 
		 * @param input Eingabe.
		 * @param index Index.
		 */
		public void remove(final GInput input, final int index);

		/**
		 * Diese Methode ist analog zu {@link List#remove(Object)}.
		 * 
		 * @param input Eingabe.
		 * @param entry Element.
		 */
		@Override
		public void remove(final GInput input, final Object entry);

		/**
		 * Diese Methode ist analog zu {@link List#removeAll(Collection)}.
		 * 
		 * @param input Eingabe.
		 * @param entries Elemente.
		 */
		@Override
		public void removeAll(final GInput input, final Iterable<?> entries);

	}

	/**
	 * Diese Schnittstelle definiert einen Adapter zur Modifikation einer Objektsammlung, die über ein {@link Field} einer gegebenen Eingabe gelesen bzw.
	 * geschrieben wird.<br>
	 * Die Modifikation erfolgt an einer Kopie der Objektsammlung, welche nach ihrer Modifikation über {@link #set(Object, Object)} zugewiesen wird.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 * @param <GValue> Typ der Objektsammlung.
	 * @param <GEntry> Typ der Elemente.
	 */
	public static interface ValuesField<GInput, GValue, GEntry> extends Field<GInput, GValue> {

		/**
		 * Diese Methode ist analog zu {@link Collection#clear()}.
		 * 
		 * @param input Eingabe.
		 */
		public void clear(final GInput input);

		/**
		 * Diese Methode ist analog zu {@link Collection#add(Object)}.
		 * 
		 * @param input Eingabe.
		 * @param entry Element.
		 */
		public void append(final GInput input, final GEntry entry);

		/**
		 * Diese Methode ist analog zu {@link Collection#addAll(Collection)}.
		 * 
		 * @param input Eingabe.
		 * @param entries Elemente.
		 */
		public void appendAll(final GInput input, final Iterable<? extends GEntry> entries);

		/**
		 * Diese Methode ist analog zu {@link Collection#remove(Object)}.
		 * 
		 * @param input Eingabe.
		 * @param entry Element.
		 */
		public void remove(final GInput input, final Object entry);

		/**
		 * Diese Methode ist analog zu {@link Collection#removeAll(Collection)}.
		 * 
		 * @param input Eingabe.
		 * @param entries Elemente.
		 */
		public void removeAll(final GInput input, final Iterable<?> entries);

	}

	/**
	 * Diese Klasse implementiert ein abstraktes {@link SetField}, dessen {@link #get(Object)}- und {@link #set(Object, Object)}-Methoden ergänzt werden müssen.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 * @param <GEntry> Typ der Elemente.
	 */
	public static abstract class AbstractSetField<GInput, GEntry> extends AbstractField<GInput, Set<GEntry>> implements SetField<GInput, GEntry> {

		/**
		 * Diese Methode gibt eine Bearbeitungskopie des gegebenen {@link Set}s zurück.
		 * 
		 * @param set {@link Set}.
		 * @return Bearbeitungskopie.
		 */
		protected Set<GEntry> copy(final Set<GEntry> set) {
			return new HashSet<GEntry>(set);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void clear(final GInput input) {
			if (this.get(input).isEmpty()) return;
			this.set(input, Collections.<GEntry>emptySet());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void append(final GInput input, final GEntry entry) {
			Set<GEntry> set = this.get(input);
			if (set.contains(entry)) return;
			set = this.copy(this.get(input));
			set.add(entry);
			this.set(input, set);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void appendAll(final GInput input, final Iterable<? extends GEntry> entries) {
			final Set<GEntry> set = this.copy(this.get(input));
			if (!Iterables.appendAll(set, entries)) return;
			this.set(input, set);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void remove(final GInput input, final Object entry) {
			Set<GEntry> set = this.get(input);
			if (!set.contains(entry)) return;
			set = this.copy(this.get(input));
			set.remove(entry);
			this.set(input, set);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void removeAll(final GInput input, final Iterable<?> entries) {
			final Set<GEntry> set = this.copy(this.get(input));
			if (!Iterables.removeAll(set, entries)) return;
			this.set(input, set);
		}

	}

	/**
	 * Diese Klasse implementiert ein abstraktes {@link ListField}, dessen {@link #get(Object)}- und {@link #set(Object, Object)}-Methoden ergänzt werden müssen.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 * @param <GEntry> Typ der Elemente.
	 */
	public static abstract class AbstractListField<GInput, GEntry> extends AbstractField<GInput, List<GEntry>> implements ListField<GInput, GEntry> {

		/**
		 * Diese Methode gibt eine Bearbeitungskopie der gegebenen {@link List} zurück.
		 * 
		 * @param list {@link List}.
		 * @return Bearbeitungskopie.
		 */
		protected List<GEntry> copy(final List<GEntry> list) {
			return new ArrayList<GEntry>(list);
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
		public void append(final GInput input, final GEntry entry) {
			final List<GEntry> list = this.copy(this.get(input));
			list.add(entry);
			this.set(input, list);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void appendAll(final GInput input, final Iterable<? extends GEntry> entries) {
			final List<GEntry> list = this.copy(this.get(input));
			if (!Iterables.appendAll(list, entries)) return;
			this.set(input, list);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void append(final GInput input, final int index, final GEntry entry) {
			final List<GEntry> list = this.copy(this.get(input));
			list.add(index, entry);
			this.set(input, list);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void appendAll(final GInput input, final int index, final Iterable<? extends GEntry> entries) {
			final List<GEntry> list = this.copy(this.get(input));
			if (!Iterables.appendAll(list.subList(index, index), entries)) return;
			this.set(input, list);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void remove(final GInput input, final int index) {
			final List<GEntry> list = this.copy(this.get(input));
			list.remove(index);
			this.set(input, list);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void remove(final GInput input, final Object entry) {
			final List<GEntry> list = this.get(input);
			final int index = list.indexOf(entry);
			if (index < 0) return;
			this.remove(input, index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void removeAll(final GInput input, final Iterable<?> entries) {
			final List<GEntry> list = this.copy(this.get(input));
			if (!Iterables.removeAll(list, entries)) return;
			this.set(input, list);
		}

	}

	/**
	 * Diese Klasse implementiert ein abstraktes {@link MapField}, dessen {@link #get(Object)}- und {@link #set(Object, Object)}-Methoden ergänzt werden müssen.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GValue> Typ der Werte.
	 */
	public static abstract class AbstractMapField<GInput, GKey, GValue> extends AbstractField<GInput, Map<GKey, GValue>> implements
		MapField<GInput, GKey, GValue> {

		/**
		 * Diese Methode gibt eine Bearbeitungskopie der gegebenen {@link Map} zurück.
		 * 
		 * @param map {@link Map}.
		 * @return Bearbeitungskopie.
		 */
		protected Map<GKey, GValue> copy(final Map<GKey, GValue> map) {
			return new HashMap<GKey, GValue>(map);
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

	/**
	 * Diese Klasse implementiert ein {@link SetField}, welches das {@link Set} über ein gegebenes {@link Field} liest und schreibt.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 * @param <GEntry> Typ der Elemente.
	 */
	public static final class DelegatingSetField<GInput, GEntry> extends AbstractSetField<GInput, GEntry> {

		/**
		 * Dieses Feld speichert das {@link Field}.
		 */
		final Field<? super GInput, Set<GEntry>> field;

		/**
		 * Dieser Konstruktor initialisiert das {@link Field}.
		 * 
		 * @param field {@link Field}.
		 * @throws NullPointerException Wenn das gegebene {@link Field} null ist.
		 */
		public DelegatingSetField(final Field<? super GInput, Set<GEntry>> field) throws NullPointerException {
			if (field == null) throw new NullPointerException();
			this.field = field;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Set<GEntry> get(final GInput input) {
			return this.field.get(input);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void set(final GInput input, final Set<GEntry> value) {
			this.field.set(input, value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return Objects.hash(this.field);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof DelegatingSetField<?, ?>)) return false;
			final DelegatingSetField<?, ?> data = (DelegatingSetField<?, ?>)object;
			return Objects.equals(this.field, data.field);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return this.field.toString();
		}

	}

	/**
	 * Diese Klasse implementiert ein {@link ListField}, welches die {@link List} über ein gegebenes {@link Field} liest und schreibt.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 * @param <GEntry> Typ der Elemente.
	 */
	public static final class DelegatingListField<GInput, GEntry> extends AbstractListField<GInput, GEntry> {

		/**
		 * Dieses Feld speichert das {@link Field}.
		 */
		final Field<? super GInput, List<GEntry>> field;

		/**
		 * Dieser Konstruktor initialisiert das {@link Field}.
		 * 
		 * @param field {@link Field}.
		 * @throws NullPointerException Wenn das gegebene {@link Field} null ist.
		 */
		public DelegatingListField(final Field<? super GInput, List<GEntry>> field) throws NullPointerException {
			if (field == null) throw new NullPointerException();
			this.field = field;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public List<GEntry> get(final GInput input) {
			return this.field.get(input);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void set(final GInput input, final List<GEntry> value) {
			this.field.set(input, value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return Objects.hash(this.field);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof DelegatingListField<?, ?>)) return false;
			final DelegatingListField<?, ?> data = (DelegatingListField<?, ?>)object;
			return Objects.equals(this.field, data.field);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return this.field.toString();
		}

	}

	/**
	 * Diese Klasse implementiert ein {@link MapField}, welches die {@link Map} über ein gegebenes {@link Field} liest und schreibt.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GValue> Typ der Werte.
	 */
	public static final class DelegatingMapField<GInput, GKey, GValue> extends AbstractMapField<GInput, GKey, GValue> {

		/**
		 * Dieses Feld speichert das {@link Field}.
		 */
		final Field<? super GInput, Map<GKey, GValue>> field;

		/**
		 * Dieser Konstruktor initialisiert das {@link Field}.
		 * 
		 * @param field {@link Field}.
		 * @throws NullPointerException Wenn das gegebene {@link Field} null ist.
		 */
		public DelegatingMapField(final Field<? super GInput, Map<GKey, GValue>> field) throws NullPointerException {
			if (field == null) throw new NullPointerException();
			this.field = field;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Map<GKey, GValue> get(final GInput input) {
			return this.field.get(input);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void set(final GInput input, final Map<GKey, GValue> value) {
			this.field.set(input, value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return Objects.hash(this.field);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof DelegatingMapField<?, ?, ?>)) return false;
			final DelegatingMapField<?, ?, ?> data = (DelegatingMapField<?, ?, ?>)object;
			return Objects.equals(this.field, data.field);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return this.field.toString();
		}

	}

	/**
	 * Diese Klasse implementiert das leere {@link Field}, das beim Lesen immer {@code null} liefert und das Schreiben ignoriert.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GValue> Typ des Werts der Eigenschaft.
	 */
	public static final class VoidField<GValue> extends AbstractField<Object, GValue> {

		/**
		 * Dieses Feld speichert den {@link VoidField}.
		 */
		public static final VoidField<?> INSTANCE = new VoidField<Object>();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GValue get(final Object input) {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void set(final Object input, final GValue value) {
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			return (object == this) || (object instanceof VoidField<?>);
		}

	}

	/**
	 * Diese Klasse implementiert ein {@link Field}, das beim Lesen immer eine gegebene Ausgabe liefert und das Schreiben ignoriert.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GValue> Typ des Werts der Eigenschaft.
	 */
	public static final class ValueField<GValue> implements Field<Object, GValue> {

		/**
		 * Dieses Feld speichert den Wert.
		 */
		final GValue value;

		/**
		 * Dieser Konstruktor initialisiert den Wert.
		 * 
		 * @param value Wert.
		 */
		public ValueField(final GValue value) {
			this.value = value;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GValue get(final Object input) {
			return this.value;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void set(final Object input, final GValue value) {
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return Objects.hash(this.value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof ValueField<?>)) return false;
			final ValueField<?> data = (ValueField<?>)object;
			return Objects.equals(this.value, data.value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this.value);
		}

	}

	/**
	 * Diese Klasse implementiert ein {@link TranscodedField umkodierendes} {@link Field} zum Lesen bzw. Schreiben einer Eigenschaft aller Elemente seiner
	 * iterierbaren Eingabe. Das {@link Field} gibt beim Lesen den Wert zurück, der unter allen Elementen {@link Objects#equals(Object) äquivalent} ist. Ermittelt
	 * wird der Wert der Eigenschaft eines Elements mit Hilfe eines gegebenen {@link Field}s. Wenn kein einheitlicher Wert existiert, wird ein gegebener Mischwert
	 * zurück gegeben. Beim Schreiben wird der Wert der Eigenschaft für alle Elemente über das gegebene {@link Field} zugewiesen. <br>
	 * Mit dem durch diese Klasse definierten {@link Field} können mehrere Elemente parallel bearbeitet werden, indem jedes aus der iterierbaren Eingabe stammende
	 * Elemente zur Bearbeitung an das gegebene {@link Field} weitergeleitet wird. Ein dabei gegebenenfalls notwendiges Standardverhalten wird dazu über
	 * entsprechende Werte bereit gestellt. <br>
	 * Für das Standardverhalten beim Lesen des Wert der Eigenschaft lassen sich drei Zustände unterscheiden:
	 * <ul>
	 * <li>Der {@link Converter} zum Umwandeln des internen in den externen Wert (Formatieren) ist {@code null}. Hier wird der Wert {@code null} geliefert.</li>
	 * <li>Die Eingabe ist leer. Hier wird der gegebene Leerwert geliefert.</li>
	 * <li>Die für jedes Element ermittelten Werte der Eigenschaft unterscheiden sich. Hier wird der gegebene Mischwert geliefert.</li>
	 * </ul>
	 * 
	 * @see TranscodedField
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Elemente in der iterierbaren Eingabe.
	 * @param <GValue> Typ des Werts dieses {@link Field}s.
	 * @param <GValue2> Typ des Werts der Elemente.
	 */
	public static final class IterableField<GInput, GValue, GValue2> extends AbstractDelegatingField<Iterable<? extends GInput>, GValue, GInput, GValue2> {

		/**
		 * Dieses Feld speichert den Wächter für das Lesen.
		 */
		static final Object SKIP = new Object();

		/**
		 * Dieses Feld speichert den {@link Converter} zum Umwandeln des externen in den internen Wert (Parsen) für das Schreiben oder {@code null}.
		 */
		final Converter<? super GValue, ? extends GValue2> parser;

		/**
		 * Dieses Feld speichert den {@link Converter} zum Umwandeln des internen in den externen Wert (Formatieren) für das Lesen oder {@code null}.
		 */
		final Converter<? super GValue2, ? extends GValue> formatter;

		/**
		 * Dieses Feld speichert den Leerwert.
		 */
		final GValue emptyValue;

		/**
		 * Dieses Feld speichert den Mischwert.
		 */
		final GValue mixedValue;

		/**
		 * Dieser Konstruktor initialisiert {@link Field}, {@link Converter}, Leerwert und Mischwert. Wenn der {@code parser} bzw. der {@code formatter}
		 * {@code null} ist, wird beim Lesen immer {@code null} geliefert bzw. das Schreiben ignoriert.
		 * 
		 * @param field {@link Field}.
		 * @param parser {@link Converter} zum Umwandeln des externen in den internen Wert (Parsen) für das Schreiben oder {@code null}.
		 * @param formatter {@link Converter} zum Umwandeln des internen in den externen Wert (Formatieren) für das Lesen oder {@code null}.
		 * @param emptyValue Leerwert.
		 * @param mixedValue Mischwert.
		 * @throws NullPointerException Wenn das gegebene {@link Field} {@code null} ist.
		 */
		public IterableField(final Field<? super GInput, GValue2> field, final Converter<? super GValue, ? extends GValue2> parser,
			final Converter<? super GValue2, ? extends GValue> formatter, final GValue emptyValue, final GValue mixedValue) throws NullPointerException {
			super(field);
			this.parser = parser;
			this.formatter = formatter;
			this.emptyValue = emptyValue;
			this.mixedValue = mixedValue;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GValue get(final Iterable<? extends GInput> input) {
			final Converter<? super GValue2, ? extends GValue> formatter = this.formatter;
			if (formatter == null) return null;
			final Field<? super GInput, GValue2> field = this.field;
			GValue2 next = null;
			Object last = IterableField.SKIP;
			for (final GInput input2: input) {
				next = field.get(input2);
				if ((last != IterableField.SKIP) && !Objects.equals(last, next)) return this.mixedValue;
				last = next;
			}
			if (last == IterableField.SKIP) return this.emptyValue;
			return formatter.convert(next);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void set(final Iterable<? extends GInput> input, final GValue value) {
			final Converter<? super GValue, ? extends GValue2> parser = this.parser;
			if (parser == null) return;
			final Field<? super GInput, GValue2> field = this.field;
			final GValue2 value2 = parser.convert(value);
			for (final GInput entry: input) {
				field.set(entry, value2);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return Objects.hash(this.field, this.parser, this.formatter, this.emptyValue, this.mixedValue);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof IterableField<?, ?, ?>)) return false;
			final IterableField<?, ?, ?> data = (IterableField<?, ?, ?>)object;
			return Objects.equals(this.field, data.field) && Objects.equals(this.parser, data.parser) && Objects.equals(this.formatter, data.formatter)
				&& Objects.equals(this.emptyValue, data.emptyValue) && Objects.equals(this.mixedValue, data.mixedValue);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this.field, this.parser, this.formatter, this.emptyValue, this.mixedValue);
		}

	}

	/**
	 * Diese Klasse implementiert ein navigierendes {@link Field}, dass mit einem {@link Converter} von der Eingabe zu einem Element navigiert, dessen Eigenschaft
	 * dann über ein gegebenes {@link Field} manipuliert wird.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 * @param <GOutput> Typ des Elements als Ausgabe des {@link Converter}s sowie als Eingabe des {@link Field}s.
	 * @param <GValue> Typ des Werts.
	 */
	public static final class ConvertedField<GInput, GOutput, GValue> extends AbstractDelegatingField<GInput, GValue, GOutput, GValue> {

		/**
		 * Dieses Feld speichert den {@link Converter} zur Navigation.
		 */
		final Converter<? super GInput, ? extends GOutput> converter;

		/**
		 * Dieser Konstruktor initialisiert {@link Converter} und {@link Field}.
		 * 
		 * @param converter {@link Converter} zur Navigation.
		 * @param field {@link Field} zur Manipulation.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public ConvertedField(final Converter<? super GInput, ? extends GOutput> converter, final Field<? super GOutput, GValue> field) throws NullPointerException {
			super(field);
			if (converter == null) throw new NullPointerException();
			this.converter = converter;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GValue get(final GInput input) {
			return this.field.get(this.converter.convert(input));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void set(final GInput input, final GValue value) {
			this.field.set(this.converter.convert(input), value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return Objects.hash(this.field, this.converter);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof ConvertedField<?, ?, ?>)) return false;
			final ConvertedField<?, ?, ?> data = (ConvertedField<?, ?, ?>)object;
			return Objects.equals(this.field, data.field) && Objects.equals(this.converter, data.converter);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this.field, this.converter);
		}

	}

	/**
	 * Diese Klasse implementiert ein umkodierendes {@link Field}, dessen Methoden zum Lesen und Schreiben des Werts mit je einem {@link Converter} an ein
	 * gegebenes {@link Field} angebunden sind. Beim Lesen des Werts der Eigenschaft einer Eingabe via {@link #get(Object)} wird der über das gegebene
	 * {@link Field} ermittelte interne Wert mit dem formatierenden {@link Converter} in den externen Wert überführt, sofern dieser {@link Converter} nicht
	 * {@code null} ist. Andernfalls wird immer {@code null} geliefert. Beim Schreiben des Werts via {@link #set(Object, Object)} wird der gegebene externe Wert
	 * mit dem parsenden {@link Converter} in den internen Wert überführt und der über das gegebene {@link Field} gesetzt, sofern dieser {@link Converter} nicht
	 * {@code null} ist. Andernfalls das Schreiben ignoriert.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 * @param <GValue> Typ des Werts der Eigenschaft.
	 * @param <GValue2> Typ des internen Werts des {@link Field}s.
	 */
	public static final class TranscodedField<GInput, GValue, GValue2> extends AbstractDelegatingField<GInput, GValue, GInput, GValue2> {

		/**
		 * Dieses Feld speichert den {@link Converter} zum Umwandeln des externen in den internen Wert (Parsen) für das Schreiben oder {@code null}.
		 */
		final Converter<? super GValue, ? extends GValue2> parser;

		/**
		 * Dieses Feld speichert den {@link Converter} zum Umwandeln des internen in den externen Wert (Formatieren) für das Lesen oder {@code null}.
		 */
		final Converter<? super GValue2, ? extends GValue> formatter;

		/**
		 * Dieser Konstruktor initialisiert {@link Field} und {@link Converter}. Wenn der {@code parser} bzw. der {@code formatter} {@code null} ist, wird beim
		 * Lesen immer {@code null} geliefert bzw. das Schreiben ignoriert.
		 * 
		 * @param field {@link Field}.
		 * @param parser {@link Converter} zum Umwandeln des externen in den internen Wert (Parsen) für das Schreiben oder {@code null}.
		 * @param formatter {@link Converter} zum Umwandeln des internen in den externen Wert (Formatieren) für das Lesen oder {@code null}.
		 * @throws NullPointerException Wenn das gegebene {@link Field} {@code null} ist.
		 */
		public TranscodedField(final Field<? super GInput, GValue2> field, final Converter<? super GValue, ? extends GValue2> parser,
			final Converter<? super GValue2, ? extends GValue> formatter) throws NullPointerException {
			super(field);
			this.parser = parser;
			this.formatter = formatter;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GValue get(final GInput input) {
			final Converter<? super GValue2, ? extends GValue> formatter = this.formatter;
			if (formatter == null) return null;
			return formatter.convert(this.field.get(input));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void set(final GInput input, final GValue value) {
			final Converter<? super GValue, ? extends GValue2> parser = this.parser;
			if (parser == null) return;
			this.field.set(input, parser.convert(value));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return Objects.hash(this.field, this.parser, this.formatter);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof TranscodedField<?, ?, ?>)) return false;
			final TranscodedField<?, ?, ?> data = (TranscodedField<?, ?, ?>)object;
			return Objects.equals(this.field, data.field) && Objects.equals(this.parser, data.parser) && Objects.equals(this.formatter, data.formatter);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this.field, this.parser, this.formatter);
		}

	}

	/**
	 * Diese Klasse implementiert ein bedingtes {@link Field}, das über die Weiterleitug der Eingabe mit Hilfe eines {@link Filter}s entscheiden. Wenn der
	 * gegebene {@link Filter} eine Eingabe akzeptiert, delegiert das {@link ConditionalField} diese an das gegebenen {@code Accept}-{@link Field}. Andernfalls
	 * wird sie an das gegebenen {@code Reject}-{@link Field} delegiert.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 * @param <GValue> Typ des Werts der Eigenschaft.
	 */
	public static final class ConditionalField<GInput, GValue> implements Field<GInput, GValue> {

		/**
		 * Dieses Feld speichert den {@link Filter}.
		 */
		final Filter<? super GInput> condition;

		/**
		 * Dieses Feld speichert den {@code Accept}-{@link Converter}.
		 */
		final Field<? super GInput, GValue> accept;

		/**
		 * Dieses Feld speichert den {@code Reject}-{@link Converter}.
		 */
		final Field<? super GInput, GValue> reject;

		/**
		 * Dieser Konstruktor initialisiert {@link Filter} und {@link Field}s.
		 * 
		 * @param condition {@link Filter}.
		 * @param accept {@code Accept}-{@link Field}.
		 * @param reject {@code Reject}-{@link Field}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public ConditionalField(final Filter<? super GInput> condition, final Field<? super GInput, GValue> accept, final Field<? super GInput, GValue> reject)
			throws NullPointerException {
			if ((condition == null) || (accept == null) || (reject == null)) throw new NullPointerException();
			this.condition = condition;
			this.accept = accept;
			this.reject = reject;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GValue get(final GInput input) {
			if (this.condition.accept(input)) return this.accept.get(input);
			return this.reject.get(input);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void set(final GInput input, final GValue value) {
			if (this.condition.accept(input)) {
				this.accept.set(input, value);
			} else {
				this.reject.set(input, value);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return Objects.hash(this.condition, this.accept, this.reject);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof ConditionalField<?, ?>)) return false;
			final ConditionalField<?, ?> data = (ConditionalField<?, ?>)object;
			return Objects.equals(this.condition, data.condition) && Objects.equals(this.accept, data.accept) && Objects.equals(this.reject, data.reject);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this.condition, this.accept, this.reject);
		}

	}

	/**
	 * Diese Klasse implementiert ein {@link Field}, das ein gegebenes {@link Field} synchronisiert. Die Synchronisation erfolgt via {@code synchronized(this)}.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 * @param <GValue> Typ des Werts der Eigenschaft.
	 */
	public static final class SynchronizedField<GInput, GValue> extends AbstractDelegatingField<GInput, GValue, GInput, GValue> {

		/**
		 * Dieser Konstruktor initialisiert das {@link Field}.
		 * 
		 * @param field {@link Field}.
		 * @throws NullPointerException Wenn das gegebene {@link Field} {@code null} ist.
		 */
		public SynchronizedField(final Field<? super GInput, GValue> field) throws NullPointerException {
			super(field);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GValue get(final GInput input) {
			synchronized (this) {
				return this.field.get(input);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void set(final GInput input, final GValue value) {
			synchronized (this) {
				this.field.set(input, value);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof SynchronizedField<?, ?>)) return false;
			final SynchronizedField<?, ?> data = (SynchronizedField<?, ?>)object;
			return Objects.equals(this.field, data.field);
		}

	}

	/**
	 * Diese Methode erzeugt ein {@link MapField}, welches eine {@link Map} über ein gegebenes {@link Field} liest und schreibt, und gibt es zurück.
	 * 
	 * @see MapField
	 * @see AbstractMapField
	 * @see DelegatingMapField
	 * @param <GInput> Typ der Eingabe.
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GValue> Typ der Werte.
	 * @param field {@link Field} zum Lesen und Schreiben einer {@link Map}.
	 * @return {@link DelegatingMapField}.
	 * @throws NullPointerException NullPointerException Wenn das gegebene {@link Field} null ist.
	 */
	public static <GInput, GKey, GValue> MapField<GInput, GKey, GValue> mapField(final Field<? super GInput, Map<GKey, GValue>> field)
		throws NullPointerException {
		return new DelegatingMapField<GInput, GKey, GValue>(field);
	}

	/**
	 * Diese Methode erzeugt ein {@link SetField}, welches ein {@link Set} über ein gegebenes {@link Field} liest und schreibt, und gibt es zurück.
	 * 
	 * @see SetField
	 * @see AbstractSetField
	 * @see DelegatingSetField
	 * @param <GInput> Typ der Eingabe.
	 * @param <GEntry> Typ der Elemente.
	 * @param field {@link Field} zum Lesen und Schreiben eines {@link Set}.
	 * @return {@link DelegatingSetField}.
	 * @throws NullPointerException NullPointerException Wenn das gegebene {@link Field} null ist.
	 */
	public static <GInput, GEntry> SetField<GInput, GEntry> setField(final Field<? super GInput, Set<GEntry>> field) throws NullPointerException {
		return new DelegatingSetField<GInput, GEntry>(field);
	}

	/**
	 * Diese Methode erzeugt ein {@link SetField}, welches eine {@link List} über ein gegebenes {@link Field} liest und schreibt, und gibt es zurück.
	 * 
	 * @see ListField
	 * @see AbstractListField
	 * @see DelegatingListField
	 * @param <GInput> Typ der Eingabe.
	 * @param <GEntry> Typ der Elemente.
	 * @param field {@link Field} zum Lesen und Schreiben einer {@link List}.
	 * @return {@link DelegatingListField}.
	 * @throws NullPointerException NullPointerException Wenn das gegebene {@link Field} null ist.
	 */
	public static <GInput, GEntry> ListField<GInput, GEntry> listField(final Field<? super GInput, List<GEntry>> field) throws NullPointerException {
		return new DelegatingListField<GInput, GEntry>(field);
	}

	/**
	 * Diese Methode gibt das leere {@link Field} zurück, das beim Lesen immer {@code null} liefert und das das Schreiben ignoriert.
	 * 
	 * @see VoidField
	 * @param <GValue> Typ des Werts der Eigenschaft.
	 * @return {@link VoidField}.
	 */
	@SuppressWarnings ("unchecked")
	public static <GValue> VoidField<GValue> voidField() {
		return (VoidField<GValue>)VoidField.INSTANCE;
	}

	/**
	 * Diese Methode erzeugt ein {@link ValueField}, das beim Lesen den gegebenen Wert liefert, und gibt es zurück.
	 * 
	 * @see ValueField
	 * @param <GValue> Typ des Werts.
	 * @param value Wert
	 * @return {@link ValueField}.
	 */
	public static <GValue> ValueField<GValue> valueField(final GValue value) {
		return new ValueField<GValue>(value);
	}

	/**
	 * Diese Methode erzeugt ein neues {@link IterableField} und gibt es zurück. Die externen und internen Werte sind vom gleichen Typ, weshalb zur Umwandlung in
	 * beide Richtungen ein {@link VoidConverter} genutzt wird. Leer- und Mischwert sind {@code null}.
	 * 
	 * @see IterableField
	 * @see #iterableField(Field, Converter, Converter, Object, Object)
	 * @param <GInput> Typ der Elemente in der iterierbaren Eingabe.
	 * @param <GValue> Typ des Werts der Eigenschaft der Elemente.
	 * @param field {@link IterableField}.
	 * @return {@link IterableField}.
	 * @throws NullPointerException Wenn das gegebene {@link Field} {@code null} ist.
	 */
	public static <GInput, GValue> IterableField<GInput, GValue, GValue> iterableField(final Field<? super GInput, GValue> field) throws NullPointerException {
		return Fields.iterableField(field, Converters.<GValue>voidConverter(), Converters.<GValue>voidConverter(), null, null);
	}

	/**
	 * Diese Methode erzeugt ein neues {@link IterableField} und gibt es zurück. Die externen und internen Werte sind vom gleichen Typ, weshalb zur Umwandlung in
	 * beide Richtungen ein {@link VoidConverter} genutzt wird.
	 * 
	 * @see IterableField
	 * @see #iterableField(Field, Converter, Converter, Object, Object)
	 * @param <GInput> Typ der Elemente in der iterierbaren Eingabe.
	 * @param <GValue> Typ des Werts der Eigenschaft der Elemente.
	 * @param field {@link IterableField}.
	 * @param emptyValue Leerwert.
	 * @param mixedValue Mischwert.
	 * @return {@link IterableField}.
	 * @throws NullPointerException Wenn das gegebene {@link Field} {@code null} ist.
	 */
	public static <GInput, GValue> IterableField<GInput, GValue, GValue> iterableField(final Field<? super GInput, GValue> field, final GValue emptyValue,
		final GValue mixedValue) throws NullPointerException {
		return Fields.iterableField(field, Converters.<GValue>voidConverter(), Converters.<GValue>voidConverter(), emptyValue, mixedValue);
	}

	/**
	 * Diese Methode erzeugt ein neues {@link IterableField} und gibt es zurück. Wenn der {@code parser} bzw. der {@code formatter} {@code null} ist, wird beim
	 * Lesen immer {@code null} geliefert bzw. das Schreiben ignoriert. Leer- und Mischwert sind {@code null}.
	 * 
	 * @see IterableField
	 * @see #iterableField(Field, Converter, Converter, Object, Object)
	 * @param <GInput> Typ der Elemente in der iterierbaren Eingabe.
	 * @param <GValue> Typ des Werts dieses {@link Field}s.
	 * @param <GValue2> Typ des Werts der Elemente.
	 * @param field {@link Field}.
	 * @param parser {@link Converter} zum Umwandeln des externen in den internen Wert (Parsen) für das Schreiben oder {@code null}.
	 * @param formatter {@link Converter} zum Umwandeln des internen in den externen Wert (Formatieren) für das Lesen oder {@code null}.
	 * @return {@link IterableField}.
	 * @throws NullPointerException Wenn das gegebene {@link Field} {@code null} ist.
	 */
	public static <GInput, GValue, GValue2> IterableField<GInput, GValue, GValue2> iterableField(final Field<? super GInput, GValue2> field,
		final Converter<? super GValue, ? extends GValue2> parser, final Converter<? super GValue2, ? extends GValue> formatter) throws NullPointerException {
		return Fields.iterableField(field, parser, formatter, null, null);
	}

	/**
	 * Diese Methode erzeugt ein neues {@link IterableField} und gibt es zurück. Wenn der {@code parser} bzw. der {@code formatter} {@code null} ist, wird beim
	 * Lesen immer {@code null} geliefert bzw. das Schreiben ignoriert.
	 * 
	 * @see IterableField
	 * @param <GInput> Typ der Elemente in der iterierbaren Eingabe.
	 * @param <GValue> Typ des Werts dieses {@link Field}s.
	 * @param <GValue2> Typ des Werts der Elemente.
	 * @param field {@link Field}.
	 * @param parser {@link Converter} zum Umwandeln des externen in den internen Wert (Parsen) für das Schreiben oder {@code null}.
	 * @param formatter {@link Converter} zum Umwandeln des internen in den externen Wert (Formatieren) für das Lesen oder {@code null}.
	 * @param emptyValue Leerwert.
	 * @param mixedValue Mischwert.
	 * @return {@link IterableField}.
	 * @throws NullPointerException Wenn das gegebene {@link Field} {@code null} ist.
	 */
	public static <GInput, GValue, GValue2> IterableField<GInput, GValue, GValue2> iterableField(final Field<? super GInput, GValue2> field,
		final Converter<? super GValue, ? extends GValue2> parser, final Converter<? super GValue2, ? extends GValue> formatter, final GValue emptyValue,
		final GValue mixedValue) throws NullPointerException {
		return new IterableField<GInput, GValue, GValue2>(field, parser, formatter, emptyValue, mixedValue);
	}

	/**
	 * Diese Methode erzeugt ein navigierendes {@link Field}, dass mit dem gegebenen {@link Converter} von der Eingabe zu einem mit dem gegebene {@link Field}
	 * manipulierten Element navigiert, und gibt es zurück.
	 * 
	 * @see ConvertedField
	 * @param <GInput> Typ der Eingabe.
	 * @param <GOutput> Typ des Elements als Ausgabe des {@link Converter}s sowie als Eingabe des {@link Field}s.
	 * @param <GValue> Typ des Werts.
	 * @param converter {@link Converter} zur Navigation.
	 * @param field {@link Field} zur Manipulation.
	 * @return {@link ConvertedField}.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
	 */
	public static <GInput, GOutput, GValue> ConvertedField<GInput, GOutput, GValue> convertedField(final Converter<? super GInput, ? extends GOutput> converter,
		final Field<? super GOutput, GValue> field) throws NullPointerException {
		return new ConvertedField<GInput, GOutput, GValue>(converter, field);
	}

	/**
	 * Diese Methode erzeugt ein umkodierendes {@link Field}, dessen Methoden zum Lesen und Schreiben des Werts mit je einem {@link Converter} an das gegebene
	 * {@link Field} angebunden sind, und gibt es zurück.
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
	public static <GInput, GValue, GValue2> TranscodedField<GInput, GValue, GValue2> transcodedField(final Field<? super GInput, GValue2> field,
		final Converter<? super GValue, ? extends GValue2> parser, final Converter<? super GValue2, ? extends GValue> formatter) throws NullPointerException {
		return new TranscodedField<GInput, GValue, GValue2>(field, parser, formatter);
	}

	/**
	 * Diese Methode erzeugt ein bedingtes {@link Field}, das über die Weiterleitug der Eingabe mit Hilfe eines {@link Filter}s entscheiden, und gibt es zurück.
	 * 
	 * @see ConditionalField
	 * @param <GInput> Typ der Eingabe.
	 * @param <GValue> Typ des Werts der Eigenschaft.
	 * @param condition {@link Filter}.
	 * @param accept {@code Accept}-{@link Field}.
	 * @param reject {@code Reject}-{@link Field}.
	 * @return {@link ConditionalField}.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
	 */
	public static <GInput, GValue> ConditionalField<GInput, GValue> conditionalField(final Filter<? super GInput> condition,
		final Field<? super GInput, GValue> accept, final Field<? super GInput, GValue> reject) throws NullPointerException {
		return new ConditionalField<GInput, GValue>(condition, accept, reject);
	}

	/**
	 * Diese Methode erzeugt ein {@link Field}, das das gegebene {@link Field} synchronisiert, und gibt es zurück.
	 * 
	 * @see SynchronizedField
	 * @param <GInput> Typ der Eingabe.
	 * @param <GValue> Typ des Werts der Eigenschaft.
	 * @param field {@link Field}.
	 * @return {@link SynchronizedField}.
	 * @throws NullPointerException Wenn das gegebene {@link Field} {@code null} ist.
	 */
	public static <GInput, GValue> SynchronizedField<GInput, GValue> synchronizedField(final Field<? super GInput, GValue> field) throws NullPointerException {
		return new SynchronizedField<GInput, GValue>(field);
	}

}