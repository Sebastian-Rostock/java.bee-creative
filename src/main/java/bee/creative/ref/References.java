package bee.creative.ref;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

/** Diese Klasse implementiert grundlegende {@link Reference2}.
 *
 * @see Reference2
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class References {

	/** Dieses Feld speichert den Modus der Methode {@link References#from(int, Object)} zur Erzeugung eines {@link HardReference3}. Die Referenz auf den
	 * Datensatz eines solcher {@link Reference2} wird nicht automatisch aufgelöst. */
	public static final int HARD = 0;

	/** Dieses Feld speichert den Modus der Methode {@link References#from(int, Object)} zur Erzeugung eines {@link WeakReference3}. Die Referenz auf den
	 * Datensatz eines solcher {@link Reference2} wird nur dann automatisch aufgelöst, wenn der Datensatz nur noch über {@link WeakReference} erreichbar ist. */
	public static final int WEAK = 1;

	/** Dieses Feld speichert den Modus der Methode {@link References#from(int, Object)} zur Erzeugung eines {@link SoftReference3}. Die Referenz auf den
	 * Datensatz eines solcher {@link Reference2} wird nur dann automatisch aufgelöst, wenn der Datensatz nur noch über {@link SoftReference} erreichbar ist und
	 * der Garbage Collector dies entscheidet. */
	public static final int SOFT = 2;

	/** Dieses Feld speichert den {@link Reference2} auf {@code null}. */
	public static final Reference2<?> NULL = new HardReference3<>(null);

	/** Diese Methode gibt nur dann {@code true} zurück, wenn der gegebene {@link Reference2} gleich {@link #NULL} ist oder sein Datensatz nicht {@code null} ist.
	 *
	 * @param pointer {@link Reference2}.
	 * @return {@link Reference2}-Validität.
	 * @throws NullPointerException Wenn {@code pointer} {@code null} ist. */
	public static boolean isValid(final Reference2<?> pointer) throws NullPointerException {
		return (pointer == References.NULL) || (pointer.get() != null);
	}

	/** Diese Methode gibt den gegebenen {@link Reference2} oder {@link #NULL} zurück.
	 *
	 * @see #fromNull()
	 * @param <GData> Typ des Datensatzes.
	 * @param pointer {@link Reference2} oder {@code null}.
	 * @return gegebener {@link Reference2} oder {@link #NULL}. */
	public static <GData> Reference2<GData> from(final Reference2<GData> pointer) {
		if (pointer == null) return References.fromNull();
		return pointer;
	}

	/** Diese Methode gibt einen {@link Reference2} auf den gegebenen Datensatz im gegebenen Modus zurück.
	 *
	 * @see #fromHard(Object)
	 * @see #fromWeak(Object)
	 * @see #fromSoft(Object)
	 * @param <GData> Typ des Datensatzes.
	 * @param mode Modus ({@link References#HARD}, {@link References#WEAK}, {@link References#SOFT}).
	 * @param data Datensatz.
	 * @return {@link Reference2} auf den Datensatz.
	 * @throws IllegalArgumentException Wenn der gegebenen Modus ungültig ist. */
	public static <GData> Reference2<GData> from(final int mode, final GData data) throws IllegalArgumentException {
		switch (mode) {
			case HARD:
				return References.fromHard(data);
			case WEAK:
				return References.fromWeak(data);
			case SOFT:
				return References.fromSoft(data);
		}
		throw new IllegalArgumentException();
	}

	/** Diese Methode gibt den {@link Reference2} auf {@code null} zurück.
	 *
	 * @param <GData> Typ des Datensatzes.
	 * @return {@link #NULL}. */
	@SuppressWarnings ("unchecked")
	public static <GData> Reference2<GData> fromNull() {
		return (Reference2<GData>)References.NULL;
	}

	/** Diese Methode gibt einen harten {@link Reference2} auf den gegebenen Datensatz zurück. Die Referenz auf den Datensatz eines solcher {@link Reference2}
	 * wird nicht automatisch aufgelöst.
	 *
	 * @see HardReference3
	 * @param <GData> Typ des Datensatzes.
	 * @param data Datensatz.
	 * @return {@link HardReference3} oder {@link #NULL}. */
	public static <GData> Reference2<GData> fromHard(final GData data) {
		if (data == null) return References.fromNull();
		return new HardReference3<>(data);
	}

	/** Diese Methode gibt einen {@link WeakReference3} auf den gegebenen Datensatz zurück. Die Referenz auf den Datensatz eines solcher {@link Reference2} wird
	 * nur dann automatisch aufgelöst, wenn der Datensatz nur noch über {@link WeakReference} erreichbar ist.
	 *
	 * @see WeakReference3
	 * @param <GData> Typ des Datensatzes.
	 * @param data Datensatz.
	 * @return {@link WeakReference3} oder {@link #NULL}. */
	public static <GData> Reference2<GData> fromWeak(final GData data) {
		if (data == null) return References.fromNull();
		return new WeakReference3<>(data);
	}

	/** Diese Methode gibt einen {@link SoftReference3} auf den gegebenen Datensatz zurück. Die Referenz auf den Datensatz eines solcher {@link Reference2} wird
	 * nur dann automatisch aufgelöst, wenn der Datensatz nur noch über {@link SoftReference} erreichbar ist und der Garbage Collector dies entscheidet.
	 *
	 * @see SoftReference3
	 * @param <GData> Typ des Datensatzes.
	 * @param data Datensatz.
	 * @return {@link SoftReference3} oder {@link #NULL}. */
	public static <GData> Reference2<GData> fromSoft(final GData data) {
		if (data == null) return References.fromNull();
		return new SoftReference3<>(data);
	}

}
