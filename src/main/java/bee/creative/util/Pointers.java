package bee.creative.util;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

/**
 * Diese Klasse implementiert Hilfsmethoden und Hilfsklassen zur Konstruktion und Verarbeitung von {@link Pointer}n.
 * 
 * @see Pointer
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class Pointers {

	/**
	 * Diese Klasse implementiert den {@link Pointer} auf {@code null}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class NullPointer implements Pointer<Object> {

		/**
		 * Dieses Feld speichert den {@link NullPointer}.
		 */
		public static final Pointer<?> INSTANCE = new NullPointer();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object data() {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return Pointers.hash(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			return Pointers.equals(this, object);
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
	 * Diese Klasse implementiert einen harten {@link Pointer} auf einen Datensatz. Die Referenz auf den Datensatz eines solcher {@link Pointer}s wird nicht
	 * automatisch aufgelöst.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GData> Typ des Datensatzes.
	 */
	public static final class HardPointer<GData> implements Pointer<GData> {

		/**
		 * Dieses Feld speichert den Datensatz.
		 */
		final GData data;

		/**
		 * Dieser Konstruktor initialisiert den Datensatz.
		 * 
		 * @param data Datensatz.
		 */
		public HardPointer(final GData data) {
			this.data = data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GData data() {
			return this.data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return Pointers.hash(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			return Pointers.equals(this, object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this.data);
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link WeakReference} als {@link Pointer} auf einen Datensatz. Die Referenz auf den Datensatz eines solcher {@link Pointer}
	 * s wird nur dann automatisch aufgelöst, wenn der Datensatz nur noch über {@link WeakReference}s erreichbar ist.
	 * 
	 * @see WeakReference
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GData> Typ des Datensatzes.
	 */
	public static final class WeakPointer<GData> extends WeakReference<GData> implements Pointer<GData> {

		/**
		 * Dieser Konstruktor initialisiert den Datensatz.
		 * 
		 * @param data Datensatz.
		 */
		public WeakPointer(final GData data) {
			super(data);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GData data() {
			return this.get();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return Pointers.hash(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			return Pointers.equals(this, object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this.data());
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link SoftReference} als {@link Pointer} auf einen Datensatz. Die Referenz auf den Datensatz eines solcher {@link Pointer}
	 * s wird nur dann automatisch aufgelöst, wenn der Datensatz nur noch über {@link SoftReference}s erreichbar ist und der Garbage Collector dies entscheidet.
	 * 
	 * @see SoftReference
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GData> Typ des Datensatzes.
	 */
	public static final class SoftPointer<GData> extends SoftReference<GData> implements Pointer<GData> {

		/**
		 * Dieser Konstruktor initialisiert den Datensatz.
		 * 
		 * @param data Datensatz.
		 */
		public SoftPointer(final GData data) {
			super(data);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GData data() {
			return this.get();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return Pointers.hash(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			return Pointers.equals(this, object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this.data());
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Pointer}, dessen Datensatz mit Hilfe eines {@link Converter}s aus einem gegebenen {@link Pointer} ermittelt wird.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe des gegebenen {@link Converter}s sowie des Datensatzes des gegebenen {@link Pointer}s.
	 * @param <GOutput> Typ der Ausgabe des gegebenen {@link Converter}s sowie des Datensatzes.
	 */
	public static final class ConvertedPointer<GInput, GOutput> implements Pointer<GOutput> {

		/**
		 * Dieses Feld speichert den {@link Pointer}.
		 */
		final Pointer<? extends GInput> pointer;

		/**
		 * Dieses Feld speichert den {@link Converter}.
		 */
		final Converter<? super GInput, ? extends GOutput> converter;

		/**
		 * Dieser Konstruktor initialisiert {@link Pointer} und {@link Converter}.
		 * 
		 * @param converter {@link Converter}.
		 * @param pointer {@link Pointer}.
		 * @throws NullPointerException Wenn der gegebenen {@link Pointer} bzw. der gegebenen {@link Converter} {@code null} ist.
		 */
		public ConvertedPointer(final Converter<? super GInput, ? extends GOutput> converter, final Pointer<? extends GInput> pointer) throws NullPointerException {
			if ((pointer == null) || (converter == null)) throw new NullPointerException();
			this.pointer = pointer;
			this.converter = converter;
		}

		/**
		 * Diese Methode gibt den {@link Pointer} zurück.
		 * 
		 * @return {@link Pointer}.
		 */
		public Pointer<? extends GInput> pointer() {
			return this.pointer;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GOutput data() {
			return this.converter.convert(this.pointer.data());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return Pointers.hash(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			return Pointers.equals(this, object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this.converter, this.pointer);
		}

	}

	/**
	 * Dieses Feld speichert den Modus der Methode {@link Pointers#pointer(int, Object)} zur Erzeugung eines {@link HardPointer}s. Die Referenz auf den Datensatz
	 * eines solcher {@link Pointer}s wird nicht automatisch aufgelöst.
	 */
	public static final int HARD = 0;

	/**
	 * Dieses Feld speichert den Modus der Methode {@link Pointers#pointer(int, Object)} zur Erzeugung eines {@link WeakPointer}s. Die Referenz auf den Datensatz
	 * eines solcher {@link Pointer}s wird nur dann automatisch aufgelöst, wenn der Datensatz nur noch über {@link WeakReference}s erreichbar ist.
	 */
	public static final int WEAK = 1;

	/**
	 * Dieses Feld speichert den Modus der Methode {@link Pointers#pointer(int, Object)} zur Erzeugung eines {@link SoftPointer}s. Die Referenz auf den Datensatz
	 * eines solcher {@link Pointer}s wird nur dann automatisch aufgelöst, wenn der Datensatz nur noch über {@link SoftReference}s erreichbar ist und der Garbage
	 * Collector dies entscheidet.
	 */
	public static final int SOFT = 2;

	/**
	 * Diese Methode gibt den {@link Object#hashCode() Streuwert} des gegebenen {@link Pointer}s zurück.
	 * 
	 * @see Pointer#hashCode()
	 * @param pointer {@link Pointer}.
	 * @return {@link Object#hashCode() Streuwert}.
	 */
	static int hash(final Pointer<?> pointer) {
		return Objects.hash(pointer.data());
	}

	/**
	 * Diese Methode gibt die {@link Object#equals(Object) Äquivalenz} der gegebenen Objekte zurück.
	 * 
	 * @see Pointer#equals(Object)
	 * @param pointer {@link Pointer}.
	 * @param object Objekt.
	 * @return {@link Object#equals(Object) Äquivalenz}.
	 */
	static boolean equals(final Pointer<?> pointer, final Object object) {
		if (object == pointer) return true;
		if (!(object instanceof Pointer<?>)) return false;
		final Pointer<?> data = (Pointer<?>)object;
		return Objects.equals(pointer.data(), data.data());
	}

	/**
	 * Diese Methode gibt nur dann {@code true} zurück, wenn der gegebene {@link Pointer} gleich dem {@code null}-{@link Pointer} oder sein Datensatz nicht
	 * {@code null} ist.
	 * 
	 * @param pointer {@link Pointer}.
	 * @return {@link Pointer}-Validität.
	 * @throws NullPointerException Wenn der gegebenen {@link Pointer} {@code null} ist.
	 */
	public static boolean isValid(final Pointer<?> pointer) throws NullPointerException {
		if (pointer == null) throw new NullPointerException();
		return (pointer == NullPointer.INSTANCE) || (pointer.data() != null);
	}

	/**
	 * Diese Methode gibt den gegebenen {@link Pointer} oder den {@link Pointer} auf {@code null} zurück.
	 * 
	 * @see #nullPointer()
	 * @param <GData> Typ des Datensatzes.
	 * @param pointer {@link Pointer}
	 * @return gegebener {@link Pointer} oder {@link Pointer} auf {@code null}.
	 */
	public static <GData> Pointer<GData> pointer(final Pointer<GData> pointer) {
		return ((pointer == null) ? Pointers.<GData>nullPointer() : pointer);
	}

	/**
	 * Diese Methode erzeugt einen {@link Pointer} auf den gegebenen Datensatz im gegebenen Modus ung gibt ihn zurück.
	 * 
	 * @see #hardPointer(Object)
	 * @see #weakPointer(Object)
	 * @see #softPointer(Object)
	 * @param <GData> Typ des Datensatzes.
	 * @param mode Modus ({@link Pointers#HARD}, {@link Pointers#WEAK}, {@link Pointers#SOFT}).
	 * @param data Datensatz.
	 * @return {@link Pointer} auf den Datensatz.
	 * @throws IllegalArgumentException Wenn der gegebenen Modus ungültig ist.
	 */
	public static <GData> Pointer<GData> pointer(final int mode, final GData data) throws IllegalArgumentException {
		switch (mode) {
			case HARD:
				return Pointers.hardPointer(data);
			case WEAK:
				return Pointers.weakPointer(data);
			case SOFT:
				return Pointers.softPointer(data);
		}
		throw new IllegalArgumentException();
	}

	/**
	 * Diese Methode gibt den {@link Pointer} auf {@code null} zurück.
	 * 
	 * @see NullPointer
	 * @param <GData> Typ des Datensatzes.
	 * @return {@code null}-{@link Pointer}.
	 */
	@SuppressWarnings ("unchecked")
	public static <GData> Pointer<GData> nullPointer() {
		return (Pointer<GData>)NullPointer.INSTANCE;
	}

	/**
	 * Diese Methode erzeugt einen harten {@link Pointer} auf den gegebenen Datensatz und gibt ihn zurück. Die Referenz auf den Datensatz eines solcher
	 * {@link Pointer}s wird nicht automatisch aufgelöst.
	 * 
	 * @see HardPointer
	 * @param <GData> Typ des Datensatzes.
	 * @param data Datensatz.
	 * @return {@link HardPointer}.
	 */
	public static <GData> Pointer<GData> hardPointer(final GData data) {
		if (data == null) return Pointers.nullPointer();
		return new HardPointer<>(data);
	}

	/**
	 * Diese Methode erzeugt einen {@link WeakPointer} auf den gegebenen Datensatz und gibt ihn zurück. Die Referenz auf den Datensatz eines solcher
	 * {@link Pointer}s wird nur dann automatisch aufgelöst, wenn der Datensatz nur noch über {@link WeakReference}s erreichbar ist.
	 * 
	 * @see WeakPointer
	 * @param <GData> Typ des Datensatzes.
	 * @param data Datensatz.
	 * @return {@link WeakPointer}.
	 */
	public static <GData> Pointer<GData> weakPointer(final GData data) {
		if (data == null) return Pointers.nullPointer();
		return new WeakPointer<>(data);
	}

	/**
	 * Diese Methode erzeugt einen {@link SoftPointer} auf den gegebenen Datensatz und gibt ihn zurück. Die Referenz auf den Datensatz eines solcher
	 * {@link Pointer}s wird nur dann automatisch aufgelöst, wenn der Datensatz nur noch über {@link SoftReference}s erreichbar ist und der Garbage Collector dies
	 * entscheidet.
	 * 
	 * @see SoftPointer
	 * @param <GData> Typ des Datensatzes.
	 * @param data Datensatz.
	 * @return {@link SoftPointer}.
	 */
	public static <GData> Pointer<GData> softPointer(final GData data) {
		if (data == null) return Pointers.nullPointer();
		return new SoftPointer<>(data);
	}

	/**
	 * Diese Methode erzeugt {@link Pointer}, dessen Datensatz mit Hilfe eines {@link Converter}s aus einem gegebenen {@link Pointer} ermittelt wird, und gibt ihn
	 * zurück.
	 * 
	 * @param <GInput> Typ der Eingabe des gegebenen {@link Converter}s sowie des Datensatzes des gegebenen {@link Pointer}s.
	 * @param <GOutput> Typ der Ausgabe des gegebenen {@link Converter}s sowie des Datensatzes.
	 * @param converter {@link Converter}.
	 * @param pointer {@link Pointer}.
	 * @return {@link ConvertedPointer Converted-Pointer}.
	 * @throws NullPointerException Wenn der gegebenen {@link Pointer} bzw. der gegebenen {@link Converter} {@code null} ist.
	 */
	public static <GInput, GOutput> Pointer<GOutput> convertedPointer(final Converter<? super GInput, ? extends GOutput> converter,
		final Pointer<? extends GInput> pointer) throws NullPointerException {
		return new ConvertedPointer<GInput, GOutput>(converter, pointer);

	}

	/**
	 * Dieser Konstruktor ist versteckt und verhindert damit die Erzeugung von Instanzen der Klasse.
	 */
	Pointers() {
	}

}