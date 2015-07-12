package bee.creative.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Diese Klasse implementiert Hilfsmethoden und Hilfsklassen zur Konstruktion und Verarbeitung von {@link Assignment}n und {@link Assignable}s.
 * 
 * @see Assignment
 * @see Assignable
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class Assignments {

	/**
	 * Diese Klasse implementiert einen {@link Assignment}, das die Abbildung der Quellobjekte auf die Zielobjekte durch einen gegebenen {@link Assignment}
	 * verwalten lässt.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GSource> Typ des Quellobjekt.
	 */
	public static class ChildAssignment<GSource> implements Assignment<GSource> {

		/**
		 * Dieses Feld speichert das {@link Assignment}, das die Abbildung der Quellobjekte auf die Zielobjekte verwaltet.
		 */
		final Assignment<?> parent;

		/**
		 * Dieses Feld speichert das Quellobjekt.
		 */
		final GSource source;

		/**
		 * Dieser Konstruktor initialisiert {@link Assignment} und Quellobjekt.
		 * 
		 * @param parent {@link Assignment}, das die Abbildung der Quellobjekte auf die Zielobjekte verwaltet.
		 * @param source Quellobjekt.
		 * @throws NullPointerException Wenn {@code parent} {@code null} ist.
		 */
		public ChildAssignment(final Assignment<?> parent, final GSource source) throws NullPointerException {
			if (parent == null) throw new NullPointerException();
			this.parent = parent;
			this.source = source;
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GSource value() {
			return this.source;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public <GObject> GObject get(final GObject source) {
			return this.parent.get(source);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public <GObject> void set(final GObject source, final GObject target) {
			this.parent.set(source, target);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public <GObject> void assign(final GObject source, final Assignable<? super GObject> target) throws NullPointerException, IllegalArgumentException {
			this.parent.assign(source, target);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public <GObject> void assign(final GObject source, final Assignable<? super GObject> target, final boolean commit) throws NullPointerException,
			IllegalArgumentException {
			this.parent.assign(source, target, commit);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public <GObject> void assign(final GObject source, final GObject target, final Assigner<? super GObject, ? super GObject> assigner)
			throws NullPointerException, IllegalArgumentException {
			this.parent.assign(source, target, assigner);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public <GObject> void assign(final GObject source, final GObject target, final Assigner<? super GObject, ? super GObject> assigner, final boolean commit)
			throws NullPointerException, IllegalArgumentException {
			this.parent.assign(source, target, assigner, commit);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void commit() throws IllegalArgumentException {
			this.parent.commit();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public <GObject> Assignment<GObject> assignment(final GObject source) {
			return this.parent.assignment(source);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this.source);
		}

	}

	/**
	 * Diese Klasse implementiert ein {@link Assignment}, der zur Abbildung der Quellobjekte auf die Zielobjekte eine {@link Map} verwendet. Das Quellobjekt
	 * dieses {@link Assignment}s ist {@code null}.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class ParentAssignment implements Assignment<Object> {

		@SuppressWarnings ("javadoc")
		static interface Entry {

			public abstract void assign(Assignment<Object> scope);

		}

		{}

		@SuppressWarnings ("javadoc")
		static <GObject> Entry newEntry(final GObject source, final GObject target, final Assigner<? super GObject, ? super GObject> assigner) {
			return new Entry() {

				@Override
				public void assign(final Assignment<Object> scope) {
					assigner.assign(target, scope.assignment(source));
				}

			};
		}

		@SuppressWarnings ("javadoc")
		static <GObject> Entry newEntry(final GObject source, final Assignable<? super GObject> target) {
			return new Entry() {

				@Override
				public void assign(final Assignment<Object> scope) {
					target.assign(scope.assignment(source));
				}

			};
		}

		{}

		/**
		 * Dieses Feld speichert die {@link Map} zur Abbildung der Quellobjekte auf die Zielobjekte.
		 */
		final Map<Object, Object> map = new HashMap<Object, Object>();

		/**
		 * Dieses Feld speichert die in {@link #assign(Object, Assignable, boolean)} und {@link #assign(Object, Object, Assigner, boolean)} gesammelten
		 * {@link Entry}s.
		 */
		final List<Entry> entries = new LinkedList<Entry>();

		{}

		/**
		 * Diese Methode gibt {@code null} zurück.
		 */
		@Override
		public Object value() {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public <GObject> GObject get(final GObject source) {
			if (source == null) return null;
			@SuppressWarnings ("unchecked")
			final GObject target = (GObject)this.map.get(source);
			return target == null ? source : target;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public <GObject> void set(final GObject source, final GObject target) throws NullPointerException {
			if (source == null) throw new NullPointerException();
			if (target == null) {
				this.map.remove(source);
			} else {
				this.map.put(source, target);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public <GObject> void assign(final GObject source, final Assignable<? super GObject> target) throws NullPointerException, IllegalArgumentException {
			this.assign(source, target, true);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public <GObject> void assign(final GObject source, final Assignable<? super GObject> target, final boolean commit) throws NullPointerException,
			IllegalArgumentException {
			if ((source == null) || (target == null)) throw new NullPointerException();
			this.set(source, target);
			if (commit) {
				target.assign(this.assignment(source));
			} else {
				this.entries.add(ParentAssignment.newEntry(source, target));
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public <GObject> void assign(final GObject source, final GObject target, final Assigner<? super GObject, ? super GObject> assigner)
			throws NullPointerException, IllegalArgumentException {
			this.set(source, target);
			assigner.assign(target, this.assignment(source));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public <GObject> void assign(final GObject source, final GObject target, final Assigner<? super GObject, ? super GObject> assigner, final boolean commit)
			throws NullPointerException, IllegalArgumentException {
			if ((source == null) || (target == null) || (assigner == null)) throw new NullPointerException();
			this.set(source, target);
			if (commit) {
				assigner.assign(target, this.assignment(source));
			} else {
				this.entries.add(ParentAssignment.newEntry(source, target, assigner));
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void commit() throws IllegalArgumentException {
			final List<Entry> entries = this.entries;
			while (!entries.isEmpty()) {
				entries.remove(0).assign(this);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public <GObject> Assignment<GObject> assignment(final GObject source) {
			return new ChildAssignment<GObject>(this, source);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this.map);
		}

	}

	{}

	/**
	 * Diese Methode gibt ein neues {@link Assignment} ohne Quellobjekt zurück.
	 * 
	 * @see ParentAssignment
	 * @return {@link ParentAssignment}.
	 */
	public static ParentAssignment assignment() {
		return new ParentAssignment();
	}

	/**
	 * Diese Methode leert die gegebenen Zielabbildung und fügt anschließend alle via {@link Assignment#get(Object)} zu den Schlüsseln und Werten der Einträge der
	 * gegebenen Quellabbildung ermittelten Schlüssel-Wert-Paare in die Zielabbildung ein. Die Implementation entspricht:
	 * 
	 * <pre>
	 * target.clear();
	 * for(Entry<GKey, GValue> entry: source.entrySet())target.put(assigner.get(entry.getKey()), assigner.get(entry.getValue()));
	 * </pre>
	 * 
	 * @see Assignment
	 * @see Map
	 * @param <GInput> Typ der Eingabe.
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GValue> Typ der Werte.
	 * @param assignment {@link Assignment}.
	 * @param source Quellabbildung.
	 * @param target Zielabbildung.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
	 */
	public static <GInput, GKey, GValue> void assignEntries(final Assignment<?> assignment, final Map<GKey, GValue> source, final Map<GKey, GValue> target)
		throws NullPointerException {
		if (assignment == null) throw new NullPointerException();
		target.clear();
		for (final Entry<GKey, GValue> entry: source.entrySet()) {
			target.put(assignment.get(entry.getKey()), assignment.get(entry.getValue()));
		}
	}

	/**
	 * Diese Methode leert die gegebenen Zielsammlung und fügt anschließend alle via {@link Assignment#get(Object)} zu den Elemente der gegebenen Quellsammlung
	 * ermittelten Zielobjekte in die Zielsammlung ein. Die Implementation entspricht:
	 * 
	 * <pre>
	 * target.clear();
	 * for(GValue value: source)target.add(assigner.get(value));
	 * </pre>
	 * 
	 * @see Assignment
	 * @see Collection
	 * @param <GValue> Typ der Elemente.
	 * @param assignment {@link Assignment}.
	 * @param source Quellsammlung.
	 * @param target Zielsammlung.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
	 */
	public static <GValue> void assignValues(final Assignment<?> assignment, final Collection<GValue> source, final Collection<GValue> target)
		throws NullPointerException {
		if (assignment == null) throw new NullPointerException();
		target.clear();
		for (final GValue value: source) {
			target.add(assignment.get(value));
		}
	}

}
