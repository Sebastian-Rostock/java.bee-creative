package bee.creative.util;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

/**
 * Diese Klasse implementiert grundlegende {@link Pointer}.
 * 
 * @see Pointer
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class Pointers {

	/**
	 * Diese Klasse implementiert einen abstrakten {@link Pointer}.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GData> Typ des Datensatzes.
	 */
	public static abstract class BasePointer<GData> implements Pointer<GData> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return Pointers._hash_(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			return Pointers._equals_(this, object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return String.valueOf(this.data());
		}

	}

	/**
	 * Diese Klasse implementiert einen harten {@link Pointer} auf einen Datensatz. Die Referenz auf den Datensatz eines solcher {@link Pointer} wird nicht
	 * automatisch aufgelöst.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GData> Typ des Datensatzes.
	 */
	public static final class HardPointer<GData> extends BasePointer<GData> {

		/**
		 * Dieses Feld speichert den Datensatz.
		 */
		final GData _data_;

		/**
		 * Dieser Konstruktor initialisiert den Datensatz.
		 * 
		 * @param data Datensatz.
		 */
		public HardPointer(final GData data) {
			this._data_ = data;
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GData data() {
			return this._data_;
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link WeakReference} als {@link Pointer} auf einen Datensatz. Die Referenz auf den Datensatz eines solcher {@link Pointer}
	 * wird nur dann automatisch aufgelöst, wenn der Datensatz nur noch über {@link WeakReference} erreichbar ist.
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

		{}

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
			return Pointers._hash_(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			return Pointers._equals_(this, object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return String.valueOf(this.data());
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link SoftReference} als {@link Pointer} auf einen Datensatz. Die Referenz auf den Datensatz eines solcher {@link Pointer}
	 * wird nur dann automatisch aufgelöst, wenn der Datensatz nur noch über {@link SoftReference} erreichbar ist und der Garbage Collector dies entscheidet.
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

		{}

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
			return Pointers._hash_(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			return Pointers._equals_(this, object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return String.valueOf(this.data());
		}

	}

	{}

	/**
	 * Dieses Feld speichert den Modus der Methode {@link Pointers#pointer(int, Object)} zur Erzeugung eines {@link HardPointer}. Die Referenz auf den Datensatz
	 * eines solcher {@link Pointer} wird nicht automatisch aufgelöst.
	 */
	public static final int HARD = 0;

	/**
	 * Dieses Feld speichert den Modus der Methode {@link Pointers#pointer(int, Object)} zur Erzeugung eines {@link WeakPointer}. Die Referenz auf den Datensatz
	 * eines solcher {@link Pointer} wird nur dann automatisch aufgelöst, wenn der Datensatz nur noch über {@link WeakReference} erreichbar ist.
	 */
	public static final int WEAK = 1;

	/**
	 * Dieses Feld speichert den Modus der Methode {@link Pointers#pointer(int, Object)} zur Erzeugung eines {@link SoftPointer}. Die Referenz auf den Datensatz
	 * eines solcher {@link Pointer} wird nur dann automatisch aufgelöst, wenn der Datensatz nur noch über {@link SoftReference} erreichbar ist und der Garbage
	 * Collector dies entscheidet.
	 */
	public static final int SOFT = 2;

	/**
	 * Dieses Feld speichert den {@link Pointer} auf {@code null}.
	 */
	static final Pointer<?> _null_ = new HardPointer<>(null);

	{}

	/**
	 * Diese Methode gibt den {@link Object#hashCode() Streuwert} des gegebenen {@link Pointer} zurück.
	 * 
	 * @see Pointer#hashCode()
	 * @param pointer {@link Pointer}.
	 * @return {@link Object#hashCode() Streuwert}.
	 */
	static final int _hash_(final Pointer<?> pointer) {
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
	static final boolean _equals_(final Pointer<?> pointer, final Object object) {
		if (object == pointer) return true;
		if (!(object instanceof Pointer<?>)) return false;
		final Pointer<?> data = (Pointer<?>)object;
		return Objects.equals(pointer.data(), data.data());
	}

	/**
	 * Diese Methode gibt nur dann {@code true} zurück, wenn der gegebene {@link Pointer} gleich {@link #_null_} oder sein Datensatz nicht {@code null} ist.
	 * 
	 * @param pointer {@link Pointer}.
	 * @return {@link Pointer}-Validität.
	 * @throws NullPointerException Wenn {@code pointer} {@code null} ist.
	 */
	public static final boolean isValid(final Pointer<?> pointer) throws NullPointerException {
		if (pointer == null) throw new NullPointerException("pointer = null");
		return (pointer == Pointers._null_) || (pointer.data() != null);
	}

	/**
	 * Diese Methode gibt den gegebenen {@link Pointer} oder {@link #_null_} zurück.
	 * 
	 * @see #nullPointer()
	 * @param <GData> Typ des Datensatzes.
	 * @param pointer {@link Pointer} oder {@code null}.
	 * @return gegebener {@link Pointer} oder {@link #_null_}.
	 */
	public static final <GData> Pointer<GData> pointer(final Pointer<GData> pointer) {
		if (pointer == null) return Pointers.nullPointer();
		return pointer;
	}

	/**
	 * Diese Methode gibt einen {@link Pointer} auf den gegebenen Datensatz im gegebenen Modus zurück.
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
	public static final <GData> Pointer<GData> pointer(final int mode, final GData data) throws IllegalArgumentException {
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
	 * @param <GData> Typ des Datensatzes.
	 * @return {@link #_null_}.
	 */
	@SuppressWarnings ("unchecked")
	public static final <GData> Pointer<GData> nullPointer() {
		return (Pointer<GData>)Pointers._null_;
	}

	/**
	 * Diese Methode gibt einen harten {@link Pointer} auf den gegebenen Datensatz zurück. Die Referenz auf den Datensatz eines solcher {@link Pointer} wird nicht
	 * automatisch aufgelöst.
	 * 
	 * @see HardPointer
	 * @param <GData> Typ des Datensatzes.
	 * @param data Datensatz.
	 * @return {@link HardPointer} oder {@link #_null_}.
	 */
	public static final <GData> Pointer<GData> hardPointer(final GData data) {
		if (data == null) return Pointers.nullPointer();
		return new HardPointer<>(data);
	}

	/**
	 * Diese Methode gibt einen {@link WeakPointer} auf den gegebenen Datensatz zurück. Die Referenz auf den Datensatz eines solcher {@link Pointer} wird nur dann
	 * automatisch aufgelöst, wenn der Datensatz nur noch über {@link WeakReference} erreichbar ist.
	 * 
	 * @see WeakPointer
	 * @param <GData> Typ des Datensatzes.
	 * @param data Datensatz.
	 * @return {@link WeakPointer} oder {@link #_null_}.
	 */
	public static final <GData> Pointer<GData> weakPointer(final GData data) {
		if (data == null) return Pointers.nullPointer();
		return new WeakPointer<>(data);
	}

	/**
	 * Diese Methode gibt einen {@link SoftPointer} auf den gegebenen Datensatz zurück. Die Referenz auf den Datensatz eines solcher {@link Pointer} wird nur dann
	 * automatisch aufgelöst, wenn der Datensatz nur noch über {@link SoftReference} erreichbar ist und der Garbage Collector dies entscheidet.
	 * 
	 * @see SoftPointer
	 * @param <GData> Typ des Datensatzes.
	 * @param data Datensatz.
	 * @return {@link SoftPointer} oder {@link #_null_}.
	 */
	public static final <GData> Pointer<GData> softPointer(final GData data) {
		if (data == null) return Pointers.nullPointer();
		return new SoftPointer<>(data);
	}

	/**
	 * Diese Methode gibt einen {@link Pointer} zurück, dessen Datensatz mit Hilfe des gegebenen {@link Converter} aus dem des gegebenen {@link Pointer} ermittelt
	 * wird.
	 * 
	 * @param <GInput> Typ der Eingabe des gegebenen {@link Converter} sowie des Datensatzes des gegebenen {@link Pointer}.
	 * @param <GOutput> Typ der Ausgabe des gegebenen {@link Converter} sowie des Datensatzes.
	 * @param converter {@link Converter}.
	 * @param pointer {@link Pointer}.
	 * @return {@code converted}-{@link Pointer}.
	 * @throws NullPointerException Wenn {@code converter} bzw. {@code pointer} {@code null} ist.
	 */
	public static final <GInput, GOutput> Pointer<GOutput> convertedPointer(final Converter<? super GInput, ? extends GOutput> converter,
		final Pointer<? extends GInput> pointer) throws NullPointerException {
		if (converter == null) throw new NullPointerException("converter = null");
		if (pointer == null) throw new NullPointerException("pointer = null");
		return new BasePointer<GOutput>() {

			@Override
			public GOutput data() {
				return converter.convert(pointer.data());
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("convertedPointer", converter, pointer);
			}

		};

	}

}
