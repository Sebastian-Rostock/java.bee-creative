package bee.creative.util;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import bee.creative.util.Converters.ConverterLink;

/**
 * Diese Klasse implementiert Hilfsmethoden und Hilfsklassen zur Konstruktion und Verarbeitung von {@link Pointer}n.
 * 
 * @see Pointer
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class Pointers {

	/**
	 * Diese Klasse implementiert einen abstrakten {@link Pointer}.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GData> Typ des Datensatzes.
	 */
	static abstract class BasePointer<GData> implements Pointer<GData> {

		/**
		 * Diese Methode gibt den {@link Object#hashCode() Streuwert} des gegebenen {@link Pointer}s zurück.
		 * 
		 * @see Pointers#hashCode()
		 * @param pointer {@link Pointer}.
		 * @return {@link Object#hashCode() Streuwert}.
		 */
		static public int hashCode(final Pointer<?> pointer) {
			return Objects.hash(pointer.data());
		}

		/**
		 * Diese Methode gibt die {@link Object#equals(Object) Äquivalenz} der gegebenen Objekte zurück.
		 * 
		 * @see Pointers#equals(Object)
		 * @param pointer {@link Pointer}.
		 * @param object Objekt.
		 * @return {@link Object#equals(Object) Äquivalenz}.
		 */
		static public boolean equals(final Pointer<?> pointer, final Object object) {
			if(object == pointer) return true;
			if(!(object instanceof Pointer<?>)) return false;
			final Pointer<?> data = (Pointer<?>)object;
			return Objects.equals(pointer.data(), data.data());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return BasePointer.hashCode(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			return BasePointer.equals(this, object);
		}

	}

	/**
	 * Diese Klasse implementiert einen harten {@link Pointer} auf einen Datensatz. Die Referenz auf den Datensatz eines
	 * solcher {@link Pointer}s wird nicht automatisch aufgelöst.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GData> Typ des Datensatzes.
	 */
	public static final class HardPointer<GData> extends BasePointer<GData> {

		/**
		 * Dieses Feld speichert den Datensatz.
		 */
		final GData data;

		/**
		 * Dieser Konstrukteur initialisiert den Datensatz.
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
		public String toString() {
			return Objects.toStringCall("hardPointer", this.data);
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link WeakReference} als {@link Pointer} auf einen Datensatz. Die Referenz auf den
	 * Datensatz eines solcher {@link Pointer}s wird nur dann automatisch aufgelöst, wenn der Datensatz nur noch über
	 * {@link WeakReference}s erreichbar ist.
	 * 
	 * @see WeakReference
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GData> Typ des Datensatzes.
	 */
	public static final class WeakPointer<GData> extends WeakReference<GData> implements Pointer<GData> {

		/**
		 * Dieser Konstrukteur initialisiert den Datensatz.
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
			return BasePointer.hashCode(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			return BasePointer.equals(this, object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("weakPointer", this.data());
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link SoftReference} als {@link Pointer} auf einen Datensatz. Die Referenz auf den
	 * Datensatz eines solcher {@link Pointer}s wird nur dann automatisch aufgelöst, wenn der Datensatz nur noch über
	 * {@link SoftReference}s erreichbar ist und der Garbage Collector dies entscheidet.
	 * 
	 * @see SoftReference
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GData> Typ des Datensatzes.
	 */
	public static final class SoftPointer<GData> extends SoftReference<GData> implements Pointer<GData> {

		/**
		 * Dieser Konstrukteur initialisiert den Datensatz.
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
			return BasePointer.hashCode(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			return BasePointer.equals(this, object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("softPointer", this.data());
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Pointer}, dessen Datensatz mit Hilfe eines {@link Converter}s aus einem
	 * gegebenen {@link Pointer} ermittelt wird.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe des gegebenen {@link Converter}s sowie des Datensatzes des gegebenen
	 *        {@link Pointer}s.
	 * @param <GOutput> Typ der Ausgabe des gegebenen {@link Converter}s sowie des Datensatzes.
	 */
	public static final class ConvertedPointer<GInput, GOutput> extends ConverterLink<GInput, GOutput> implements
		Pointer<GOutput> {

		/**
		 * Dieses Feld speichert den {@link Pointer}.
		 */
		final Pointer<? extends GInput> pointer;

		/**
		 * Dieser Konstrukteur initialisiert {@link Pointer} und {@link Converter}.
		 * 
		 * @param converter {@link Converter}.
		 * @param pointer {@link Pointer}.
		 * @throws NullPointerException Wenn der gegebenen {@link Pointer} bzw. der gegebenen {@link Converter} {@code null}
		 *         ist.
		 */
		public ConvertedPointer(final Converter<? super GInput, ? extends GOutput> converter,
			final Pointer<? extends GInput> pointer) throws NullPointerException {
			super(converter);
			if(pointer == null) throw new NullPointerException("Pointer is null");
			this.pointer = pointer;
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
			return BasePointer.hashCode(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			return BasePointer.equals(this, object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("convertedPointer", this.converter, this.pointer);
		}

	}

	/**
	 * Dieses Feld speichert den {@link Pointer} auf {@code null}.
	 */
	static final Pointer<?> NULL_POINTER = new BasePointer<Object>() {

		@Override
		public Object data() {
			return null;
		}

		@Override
		public String toString() {
			return Objects.toStringCall("nullPointer");
		}

	};

	/**
	 * Dieses Feld speichert den {@link Converter}, der seine Eingabe via {@link Pointers#hardPointer(Object)} in einen
	 * {@link HardPointer Hard-Pointer} umwandelt.
	 */
	static final Converter<?, ?> HARD_POINTER_CONVERTER = new Converter<Object, Pointer<Object>>() {

		@Override
		public Pointer<Object> convert(final Object input) {
			return Pointers.hardPointer(input);
		}

		@Override
		public String toString() {
			return Objects.toString("hardPointerConverter");
		}

	};

	/**
	 * Dieses Feld speichert den {@link Converter}, der seine Eingabe via {@link Pointers#weakPointer(Object)} in einen
	 * {@link WeakPointer Weak-Pointer} umwandelt.
	 */
	static final Converter<?, ?> WEAK_POINTER_CONVERTER = new Converter<Object, Pointer<Object>>() {

		@Override
		public Pointer<Object> convert(final Object input) {
			return Pointers.weakPointer(input);
		}

		@Override
		public String toString() {
			return Objects.toString("weakPointerConverter");
		}

	};

	/**
	 * Dieses Feld speichert den {@link Converter}, der seine Eingabe via {@link Pointers#softPointer(Object)} in einen
	 * {@link SoftPointer} umwandelt.
	 */
	static final Converter<?, ?> SOFT_POINTER_CONVERTER = new Converter<Object, Pointer<Object>>() {

		@Override
		public Pointer<Object> convert(final Object input) {
			return Pointers.softPointer(input);
		}

		@Override
		public String toString() {
			return Objects.toString("softPointerConverter");
		}

	};

	/**
	 * Dieses Feld speichert den {@link Converter}, der den Datensatz eines {@link Pointer}s ermitelt.
	 */
	static final Converter<?, ?> POINTER_DATA_CONVERTER = new Converter<Pointer<?>, Object>() {

		@Override
		public Object convert(final Pointer<?> input) {
			return input.data();
		}

		@Override
		public String toString() {
			return Objects.toStringCall("pointerDataConverter");
		}

	};

	/**
	 * Dieses Feld speichert den Modus der Methode {@link Pointers#pointer(int, Object)} zur Erzeugung eines
	 * {@link HardPointer}s. Die Referenz auf den Datensatz eines solcher {@link Pointer}s wird nicht automatisch
	 * aufgelöst.
	 */
	public static final int HARD = 0;

	/**
	 * Dieses Feld speichert den Modus der Methode {@link Pointers#pointer(int, Object)} zur Erzeugung eines
	 * {@link WeakPointer}s. Die Referenz auf den Datensatz eines solcher {@link Pointer}s wird nur dann automatisch
	 * aufgelöst, wenn der Datensatz nur noch über {@link WeakReference}s erreichbar ist.
	 */
	public static final int WEAK = 1;

	/**
	 * Dieses Feld speichert den Modus der Methode {@link Pointers#pointer(int, Object)} zur Erzeugung eines
	 * {@link SoftPointer}s. Die Referenz auf den Datensatz eines solcher {@link Pointer}s wird nur dann automatisch
	 * aufgelöst, wenn der Datensatz nur noch über {@link SoftReference}s erreichbar ist und der Garbage Collector dies
	 * entscheidet.
	 */
	public static final int SOFT = 2;

	/**
	 * Diese Methode gibt nur dann {@code true} zurück, wenn der gegebene {@link Pointer} gleich dem {@code null}-
	 * {@link Pointer} oder sein Datensatz nicht {@code null} ist.
	 * 
	 * @param pointer {@link Pointer}.
	 * @return {@link Pointer}-Validität.
	 * @throws NullPointerException Wenn der gegebenen {@link Pointer} {@code null} ist.
	 */
	public static boolean valid(final Pointer<?> pointer) throws NullPointerException {
		if(pointer == null) throw new NullPointerException("Pointer is null");
		return (pointer == Pointers.NULL_POINTER) || (pointer.data() != null);
	}

	/**
	 * Diese Methode gibt den gegebenen {@link Pointer} oder den {@link Pointer} auf {@code null} zurück.
	 * 
	 * @see Pointers#nullPointer()
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
	 * @param <GData> Typ des Datensatzes.
	 * @param mode Modus ({@link Pointers#HARD}, {@link Pointers#WEAK}, {@link Pointers#SOFT}).
	 * @param data Datensatz.
	 * @return {@link Pointer} auf den Datensatz.
	 * @throws IllegalArgumentException Wenn der gegebenen Modus ungültig ist.
	 */
	public static <GData> Pointer<GData> pointer(final int mode, final GData data) throws IllegalArgumentException {
		switch(mode){
			case HARD:
				return Pointers.hardPointer(data);
			case WEAK:
				return Pointers.weakPointer(data);
			case SOFT:
				return Pointers.softPointer(data);
		}
		throw new IllegalArgumentException("Mode out of range: " + mode);
	}

	/**
	 * Diese Methode gibt einen {@link Converter} zurück, der seine Eingabe via {@link Pointers#pointer(int, Object)} in
	 * einen {@link Pointer} umwandelt.
	 * 
	 * @see Converter
	 * @see Pointers#pointer(int, Object)
	 * @see Pointers#hardPointerConverter()
	 * @see Pointers#weakPointerConverter()
	 * @see Pointers#softPointerConverter()
	 * @param <GData> Typ des Datensatzes.
	 * @param mode Modus ({@link Pointers#HARD}, {@link Pointers#WEAK}, {@link Pointers#SOFT}).
	 * @return {@link Pointers#pointer(int, Object)}-{@link Converter}.
	 * @throws IllegalArgumentException Wenn der gegebenen Modus ungültig ist.
	 */
	public static <GData> Converter<GData, Pointer<GData>> pointerConverter(final int mode) {
		switch(mode){
			case HARD:
				return Pointers.hardPointerConverter();
			case WEAK:
				return Pointers.weakPointerConverter();
			case SOFT:
				return Pointers.softPointerConverter();
		}
		throw new IllegalArgumentException("Mode out of range: " + mode);
	}

	/**
	 * Diese Methode gibt einen {@link Converter} zurück, der den Datensatz eines {@link Pointer}s ermitelt.
	 * 
	 * @see Converter
	 * @see Pointer#data()
	 * @param <GData> Typ des Datensatzes.
	 * @return {@link Pointer#data()}-{@link Converter}.
	 */
	@SuppressWarnings ("unchecked")
	public static <GData> Converter<Pointer<GData>, GData> pointerDataConverter() {
		return (Converter<Pointer<GData>, GData>)Pointers.POINTER_DATA_CONVERTER;
	}

	/**
	 * Diese Methode gibt den {@link Pointer} auf {@code null} zurück.
	 * 
	 * @param <GData> Typ des Datensatzes.
	 * @return {@code null}-{@link Pointer}.
	 */
	@SuppressWarnings ("unchecked")
	public static <GData> Pointer<GData> nullPointer() {
		return (Pointer<GData>)Pointers.NULL_POINTER;
	}

	/**
	 * Diese Methode erzeugt einen harten {@link Pointer} auf den gegebenen Datensatz und gibt ihn zurück. Die Referenz
	 * auf den Datensatz eines solcher {@link Pointer}s wird nicht automatisch aufgelöst.
	 * 
	 * @param <GData> Typ des Datensatzes.
	 * @param data Datensatz.
	 * @return {@link HardPointer}.
	 */
	public static <GData> Pointer<GData> hardPointer(final GData data) {
		return ((data == null) ? Pointers.<GData>nullPointer() : new HardPointer<GData>(data));
	}

	/**
	 * Diese Methode gibt einen {@link Converter} zurück, der seine Eingabe via {@link Pointers#hardPointer(Object)} in
	 * einen {@link HardPointer} umwandelt.
	 * 
	 * @see Converter
	 * @see Pointers#hardPointer(Object)
	 * @param <GData> Typ des Datensatzes.
	 * @return {@link Pointers#hardPointer(Object)}-{@link Converter}.
	 */
	@SuppressWarnings ("unchecked")
	public static <GData> Converter<GData, Pointer<GData>> hardPointerConverter() {
		return (Converter<GData, Pointer<GData>>)Pointers.HARD_POINTER_CONVERTER;
	}

	/**
	 * Diese Methode erzeugt einen {@link WeakPointer} auf den gegebenen Datensatz und gibt ihn zurück. Die Referenz auf
	 * den Datensatz eines solcher {@link Pointer}s wird nur dann automatisch aufgelöst, wenn der Datensatz nur noch über
	 * {@link WeakReference}s erreichbar ist.
	 * 
	 * @param <GData> Typ des Datensatzes.
	 * @param data Datensatz.
	 * @return {@link WeakPointer}.
	 */
	public static <GData> Pointer<GData> weakPointer(final GData data) {
		return ((data == null) ? Pointers.<GData>nullPointer() : new WeakPointer<GData>(data));
	}

	/**
	 * Diese Methode gibt einen {@link Converter} zurück, der seine Eingabe via {@link Pointers#weakPointer(Object)} in
	 * einen {@link WeakPointer} umwandelt.
	 * 
	 * @see Converter
	 * @see Pointers#weakPointer(Object)
	 * @param <GData> Typ des Datensatzes.
	 * @return {@link Pointers#weakPointer(Object)}-{@link Converter}.
	 */
	@SuppressWarnings ("unchecked")
	public static <GData> Converter<GData, Pointer<GData>> weakPointerConverter() {
		return (Converter<GData, Pointer<GData>>)Pointers.WEAK_POINTER_CONVERTER;
	}

	/**
	 * Diese Methode erzeugt einen {@link SoftPointer} auf den gegebenen Datensatz und gibt ihn zurück. Die Referenz auf
	 * den Datensatz eines solcher {@link Pointer}s wird nur dann automatisch aufgelöst, wenn der Datensatz nur noch über
	 * {@link SoftReference}s erreichbar ist und der Garbage Collector dies entscheidet.
	 * 
	 * @param <GData> Typ des Datensatzes.
	 * @param data Datensatz.
	 * @return {@link SoftPointer}.
	 */
	public static <GData> Pointer<GData> softPointer(final GData data) {
		return ((data == null) ? Pointers.<GData>nullPointer() : new SoftPointer<GData>(data));
	}

	/**
	 * Diese Methode gibt einen {@link Converter} zurück, der seine Eingabe via {@link Pointers#softPointer(Object)} in
	 * einen {@link SoftPointer} umwandelt.
	 * 
	 * @see Converter
	 * @see Pointers#softPointer(Object)
	 * @param <GData> Typ des Datensatzes.
	 * @return {@link Pointers#softPointer(Object)}-{@link Converter}.
	 */
	@SuppressWarnings ("unchecked")
	public static <GData> Converter<GData, Pointer<GData>> softPointerConverter() {
		return (Converter<GData, Pointer<GData>>)Pointers.SOFT_POINTER_CONVERTER;
	}

	/**
	 * Diese Methode erzeugt {@link Pointer}, dessen Datensatz mit Hilfe eines {@link Converter}s aus einem gegebenen
	 * {@link Pointer} ermittelt wird, und gibt ihn zurück.
	 * 
	 * @param <GInput> Typ der Eingabe des gegebenen {@link Converter}s sowie des Datensatzes des gegebenen
	 *        {@link Pointer}s.
	 * @param <GOutput> Typ der Ausgabe des gegebenen {@link Converter}s sowie des Datensatzes.
	 * @param converter {@link Converter}.
	 * @param pointer {@link Pointer}.
	 * @return {@link ConvertedPointer Converted-Pointer}.
	 * @throws NullPointerException Wenn der gegebenen {@link Pointer} bzw. der gegebenen {@link Converter} {@code null}
	 *         ist.
	 */
	public static <GInput, GOutput> Pointer<GOutput> convertedPointer(
		final Converter<? super GInput, ? extends GOutput> converter, final Pointer<? extends GInput> pointer)
		throws NullPointerException {
		return new ConvertedPointer<GInput, GOutput>(converter, pointer);

	}

	/**
	 * Dieser Konstrukteur ist versteckt und verhindert damit die Erzeugung von Instanzen der Klasse.
	 */
	Pointers() {
	}

}
