package bee.creative.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Diese Klasse implementiert Hilfsmethoden und Hilfsklassen zur Konstruktion und Verarbeitung von {@link Assigner}n und {@link Assignable}s.
 * 
 * @see Assigner
 * @see Assignable
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class Assignables {

	/**
	 * Diese Klasse implementiert einen {@link Assigner}, der die Abbildung der Quellobjekte auf die Zielobjekte durch einen gegebenen {@link Assigner} verwalten
	 * lässt.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GSource> Typ des Quellobjekt.
	 */
	public static final class ChildAssigner<GSource> implements Assigner<GSource> {

		/**
		 * Dieses Feld speichert den {@link Assigner}, der die Abbildung der Quellobjekte auf die Zielobjekte verwaltet.
		 */
		final Assigner<?> parent;

		/**
		 * Dieses Feld speichert das Quellobjekt.
		 */
		final GSource source;

		/**
		 * Dieser Konstruktor initialisiert {@link Assigner} und Quellobjekt.
		 * 
		 * @param parent {@link Assigner}, der Abbildung der Quellobjekte auf die Zielobjekte verwaltet.
		 * @param source Quellobjekt.
		 * @throws NullPointerException wenn der gegebene {@link Assigner} {@code null} ist.
		 */
		public ChildAssigner(final Assigner<?> parent, final GSource source) throws NullPointerException {
			if(parent == null) throw new NullPointerException("parent is null");
			this.parent = parent;
			this.source = source;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GSource source() {
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
			target.assign(new ChildAssigner<GSource2>(this, source));
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
	 * Diese Klasse implementiert einen {@link Assigner}, der zur Abbildung der Quellobjekte auf die Zielobjekte eine {@link Map} verwendet. Das Quellobjekt
	 * dieses {@link Assigner}s ist {@code null}.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ParentAssigner implements Assigner<Object> {

		/**
		 * Dieses Feld speichert die {@link Map} zur Abbildung der Quellobjekte auf die Zielobjekte.
		 */
		final Map<Object, Object> map;

		/**
		 * Dieser Konstruktor initialisiert den {@link Assigner} mit einer {@link HashMap}.
		 */
		public ParentAssigner() {
			this(new HashMap<Object, Object>());
		}

		/**
		 * Dieser Konstruktor initialisiert die {@link Map}. Diese muss keine {@code null} Schlüssel unterstützen.
		 * 
		 * @param map {@link Map}.
		 * @throws NullPointerException Wenn die gegebene {@link Map} {@code null} ist.
		 */
		public ParentAssigner(final Map<Object, Object> map) throws NullPointerException {
			if(map == null) throw new NullPointerException("map is null");
			this.map = map;
		}

		/**
		 * Diese Methode gibt {@code null} zurück.
		 */
		@Override
		public Object source() {
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
		public <GSource2> void assign(final GSource2 source, final Assignable<? super GSource2> target) throws NullPointerException, IllegalArgumentException {
			target.assign(new ChildAssigner<GSource2>(this, source));
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
	 * Diese Methode gibt einen neuen {@link Assigner} ohne Quellobjekt zurück.
	 * 
	 * @see ParentAssigner
	 * @return {@link ParentAssigner}.
	 */
	public static ParentAssigner assigner() {
		return new ParentAssigner();
	}

	/**
	 * Diese Methode gibt einen neuen {@link Assigner} mit dem gegebenen Quellobjekt zurück.
	 * 
	 * @see ChildAssigner
	 * @see #assigner()
	 * @param <GSource> Typ des Quellobjekts.
	 * @param source Quellobjekt.
	 * @return {@link ChildAssigner}.
	 */
	public static <GSource> ChildAssigner<GSource> assigner(final GSource source) {
		return new ChildAssigner<GSource>(Assignables.assigner(), source);
	}

	/**
	 * Diese Methode gibt einen neuen {@link Assigner} mit dem gegebenen Quellobjekt zurück. Die {@link Map} wird zur Abbildung der Quellobjekte auf die
	 * Zielobjekte verwendet und muss keine {@code null} Schlüssel unterstützen.
	 * 
	 * @see ChildAssigner
	 * @see ParentAssigner
	 * @param <GSource> Typ des Quellobjekts.
	 * @param map {@link Map} zur Abbildung der Quellobjekte auf die Zielobjekte.
	 * @param source Quellobjekt.
	 * @return {@link ChildAssigner}.
	 * @throws NullPointerException Wenn die gegebene {@link Map} {@code null} ist.
	 */
	public static <GSource> ChildAssigner<GSource> assigner(final Map<Object, Object> map, final GSource source) throws NullPointerException {
		return new ChildAssigner<GSource>(new ParentAssigner(map), source);
	}

	/**
	 * Diese Methode überträgt die Informationen des gegebenen Quellobjekts auf das gegebene Zielobjekt. Dazu wird über die Methode {@link #assigner()} ein neuer
	 * {@link Assigner} erzeugt und dessen {@link Assigner#assign(Object, Assignable)}-Methode mit den gegebenen Parametern aufgerufen.
	 * 
	 * @see Assignables#assigner()
	 * @see Assigner#assign(Object, Assignable)
	 * @param <GSource> Typ des Quellobjekts.
	 * @param source Quellobjekt.
	 * @param target Zielobjekt.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
	 * @throws IllegalArgumentException Wenn das Quellobjekt vom Zielobjekt nicht unterstützt wird.
	 */
	public static <GSource> void assign(final GSource source, final Assignable<? super GSource> target) throws NullPointerException, IllegalArgumentException {
		Assignables.assigner().assign(source, target);
	}

	/**
	 * Dieser Konstruktor ist versteckt und verhindert damit die Erzeugung von Instanzen der Klasse.
	 */
	Assignables() {
	}

}
