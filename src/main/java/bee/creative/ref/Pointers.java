package bee.creative.ref;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

/** Diese Klasse implementiert grundlegende {@link Pointer}.
 *
 * @see Pointer
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Pointers {

	/** Dieses Feld speichert den Modus der Methode {@link Pointers#pointer(int, Object)} zur Erzeugung eines {@link HardPointer}. Die Referenz auf den Datensatz
	 * eines solcher {@link Pointer} wird nicht automatisch aufgelöst. */
	public static final int HARD = 0;

	/** Dieses Feld speichert den Modus der Methode {@link Pointers#pointer(int, Object)} zur Erzeugung eines {@link WeakPointer}. Die Referenz auf den Datensatz
	 * eines solcher {@link Pointer} wird nur dann automatisch aufgelöst, wenn der Datensatz nur noch über {@link WeakReference} erreichbar ist. */
	public static final int WEAK = 1;

	/** Dieses Feld speichert den Modus der Methode {@link Pointers#pointer(int, Object)} zur Erzeugung eines {@link SoftPointer}. Die Referenz auf den Datensatz
	 * eines solcher {@link Pointer} wird nur dann automatisch aufgelöst, wenn der Datensatz nur noch über {@link SoftReference} erreichbar ist und der Garbage
	 * Collector dies entscheidet. */
	public static final int SOFT = 2;

	/** Dieses Feld speichert den {@link Pointer} auf {@code null}. */
	public static final Pointer<?> NULL = new HardPointer<>(null);

	/** Diese Methode gibt nur dann {@code true} zurück, wenn der gegebene {@link Pointer} gleich {@link #NULL} ist oder sein Datensatz nicht {@code null} ist.
	 *
	 * @param pointer {@link Pointer}.
	 * @return {@link Pointer}-Validität.
	 * @throws NullPointerException Wenn {@code pointer} {@code null} ist. */
	public static boolean isValid(final Pointer<?> pointer) throws NullPointerException {
		return (pointer == Pointers.NULL) || (pointer.get() != null);
	}

	/** Diese Methode gibt den gegebenen {@link Pointer} oder {@link #NULL} zurück.
	 *
	 * @see #nullPointer()
	 * @param <GData> Typ des Datensatzes.
	 * @param pointer {@link Pointer} oder {@code null}.
	 * @return gegebener {@link Pointer} oder {@link #NULL}. */
	public static <GData> Pointer<GData> pointer(final Pointer<GData> pointer) {
		if (pointer == null) return Pointers.nullPointer();
		return pointer;
	}

	/** Diese Methode gibt einen {@link Pointer} auf den gegebenen Datensatz im gegebenen Modus zurück.
	 *
	 * @see #hardPointer(Object)
	 * @see #weakPointer(Object)
	 * @see #softPointer(Object)
	 * @param <GData> Typ des Datensatzes.
	 * @param mode Modus ({@link Pointers#HARD}, {@link Pointers#WEAK}, {@link Pointers#SOFT}).
	 * @param data Datensatz.
	 * @return {@link Pointer} auf den Datensatz.
	 * @throws IllegalArgumentException Wenn der gegebenen Modus ungültig ist. */
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

	/** Diese Methode gibt den {@link Pointer} auf {@code null} zurück.
	 *
	 * @param <GData> Typ des Datensatzes.
	 * @return {@link #NULL}. */
	@SuppressWarnings ("unchecked")
	public static <GData> Pointer<GData> nullPointer() {
		return (Pointer<GData>)Pointers.NULL;
	}

	/** Diese Methode gibt einen harten {@link Pointer} auf den gegebenen Datensatz zurück. Die Referenz auf den Datensatz eines solcher {@link Pointer} wird
	 * nicht automatisch aufgelöst.
	 *
	 * @see HardPointer
	 * @param <GData> Typ des Datensatzes.
	 * @param data Datensatz.
	 * @return {@link HardPointer} oder {@link #NULL}. */
	public static <GData> Pointer<GData> hardPointer(final GData data) {
		if (data == null) return Pointers.nullPointer();
		return new HardPointer<>(data);
	}

	/** Diese Methode gibt einen {@link WeakPointer} auf den gegebenen Datensatz zurück. Die Referenz auf den Datensatz eines solcher {@link Pointer} wird nur
	 * dann automatisch aufgelöst, wenn der Datensatz nur noch über {@link WeakReference} erreichbar ist.
	 *
	 * @see WeakPointer
	 * @param <GData> Typ des Datensatzes.
	 * @param data Datensatz.
	 * @return {@link WeakPointer} oder {@link #NULL}. */
	public static <GData> Pointer<GData> weakPointer(final GData data) {
		if (data == null) return Pointers.nullPointer();
		return new WeakPointer<>(data);
	}

	/** Diese Methode gibt einen {@link SoftPointer} auf den gegebenen Datensatz zurück. Die Referenz auf den Datensatz eines solcher {@link Pointer} wird nur
	 * dann automatisch aufgelöst, wenn der Datensatz nur noch über {@link SoftReference} erreichbar ist und der Garbage Collector dies entscheidet.
	 *
	 * @see SoftPointer
	 * @param <GData> Typ des Datensatzes.
	 * @param data Datensatz.
	 * @return {@link SoftPointer} oder {@link #NULL}. */
	public static <GData> Pointer<GData> softPointer(final GData data) {
		if (data == null) return Pointers.nullPointer();
		return new SoftPointer<>(data);
	}

}
