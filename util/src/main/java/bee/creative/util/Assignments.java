package bee.creative.util;

import java.util.HashMap;
import java.util.Map;

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
	public static final class ChildAssignment<GSource> implements Assignment<GSource> {

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
		 * @throws NullPointerException wenn das gegebene {@link Assignment} {@code null} ist.
		 */
		public ChildAssignment(final Assignment<?> parent, final GSource source) throws NullPointerException {
			if(parent == null) throw new NullPointerException("parent is null");
			this.parent = parent;
			this.source = source;
		}

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
		public <GValue> GValue get(final GValue source) {
			return this.parent.get(source);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public <GValue> void set(final GValue source, final GValue target) {
			this.parent.set(source, target);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public <GSource2> void assign(final GSource2 source, final Assignable<? super GSource2> target) throws NullPointerException, IllegalArgumentException {
			this.parent.assign(source, target);
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
	public static final class ParentAssignment implements Assignment<Object> {

		/**
		 * Dieses Feld speichert die {@link Map} zur Abbildung der Quellobjekte auf die Zielobjekte.
		 */
		final Map<Object, Object> map;

		/**
		 * Dieser Konstruktor initialisiert das {@link Assignment} mit einer {@link HashMap}.
		 */
		public ParentAssignment() {
			this(new HashMap<Object, Object>());
		}

		/**
		 * Dieser Konstruktor initialisiert die {@link Map}. Diese muss keine {@code null} Schlüssel unterstützen.
		 * 
		 * @param map {@link Map}.
		 * @throws NullPointerException Wenn die gegebene {@link Map} {@code null} ist.
		 */
		public ParentAssignment(final Map<Object, Object> map) throws NullPointerException {
			if(map == null) throw new NullPointerException("map is null");
			this.map = map;
		}

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
		public <GValue> GValue get(final GValue source) {
			if(source == null) return null;
			@SuppressWarnings ("unchecked")
			final GValue target = (GValue)this.map.get(source);
			return target == null ? source : target;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public <GValue> void set(final GValue source, final GValue target) throws NullPointerException {
			if(source == null) throw new NullPointerException("source is null");
			this.map.put(source, target);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public <GObject> void assign(final GObject source, final Assignable<? super GObject> target) throws NullPointerException, IllegalArgumentException {
			this.set(source, target);
			target.assign(this.assignment(source));
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
	 * Dieser Konstruktor ist versteckt und verhindert damit die Erzeugung von Instanzen der Klasse.
	 */
	Assignments() {
	}

}
