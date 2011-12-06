package bee.creative.util;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

/**
 * Diese Klasse implementiert Hilfsmethoden und Hilfsklassen zur Konstruktion und Verarbeitung von {@link Pointer
 * Pointern}.
 * 
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class Pointers {

	/**
	 * Diese Klasse implementiert einen abstrakten {@link Pointer Pointer}.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GData> Typ des Datensatzes.
	 */
	static abstract class BasePointer<GData> implements Pointer<GData> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final boolean equals(final Object obj) {
			return (obj == this) || ((obj instanceof Pointer<?>) && Objects.equals(this.data(), ((Pointer<?>)obj).data()));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final int hashCode() {
			return Objects.hash(this.data());
		}

	}

	/**
	 * Diese Klasse implementiert einen harten {@link Pointer Pointer} auf einen Datensatz. Die Referenz auf den Datensatz
	 * eines solcher {@link Pointer Pointers} wird nicht automatisch aufgelöst.
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
	 * Diese Klasse implementiert einen {@link WeakReference schwachen} {@link Pointer Pointer} auf einen Datensatz. Die
	 * Referenz auf den Datensatz eines solcher {@link Pointer Pointers} wird nur dann automatisch aufgelöst, wenn der
	 * Datensatz nur noch über {@link WeakReference WeakReferences} erreichbar ist.
	 * 
	 * @see WeakReference
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GData> Typ des Datensatzes.
	 */
	public static final class WeakPointer<GData> extends BasePointer<GData> {

		/**
		 * Dieses Feld speichert die Referenz auf den Datensatz.
		 */
		final Reference<GData> data;

		/**
		 * Dieser Konstrukteur initialisiert den Datensatz.
		 * 
		 * @param data Datensatz.
		 */
		public WeakPointer(final GData data) {
			this.data = new WeakReference<GData>(data);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GData data() {
			return this.data.get();
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
	 * Diese Klasse implementiert einen {@link SoftReference weichen} {@link Pointer Pointer} auf einen Datensatz. Die
	 * Referenz auf den Datensatz eines solcher {@link Pointer Pointers} wird nur dann automatisch aufgelöst, wenn der
	 * Datensatz nur noch über {@link SoftReference SoftReferences} erreichbar ist und der Garbage Collector dies
	 * entscheidet.
	 * 
	 * @see SoftReference
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GData> Typ des Datensatzes.
	 */
	public static final class SoftPointer<GData> extends BasePointer<GData> {

		/**
		 * Dieses Feld speichert die Referenz auf den Datensatz.
		 */
		final Reference<GData> data;

		/**
		 * Dieser Konstrukteur initialisiert den Datensatz.
		 * 
		 * @param data Datensatz.
		 */
		public SoftPointer(final GData data) {
			this.data = new SoftReference<GData>(data);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GData data() {
			return this.data.get();
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
	 * Dieses Feld speichert den {@link Pointer Pointer} auf <code>null</code>.
	 */
	static final BasePointer<?> NULL_POINTER = new BasePointer<Object>() {

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
	 * Dieses Feld speichert den Modus der Methode {@link Pointers#pointer(int, Object)} zur Erzeugung eines harten
	 * {@link Pointer Pointers}.
	 */
	public static final int HARD = 0;

	/**
	 * Dieses Feld speichert den Modus der Methode {@link Pointers#pointer(int, Object)} zur Erzeugung eines
	 * {@link WeakReference schwachen} {@link Pointer Pointers}.
	 */
	public static final int WEAK = 1;

	/**
	 * Dieses Feld speichert den Modus der Methode {@link Pointers#pointer(int, Object)} zur Erzeugung eines
	 * {@link SoftReference weichen} {@link Pointer Pointers}.
	 */
	public static final int SOFT = 2;

	/**
	 * Diese Methode gibt den gegebenen {@link Pointer Pointer} oder den {@link Pointer Pointer} auf <code>null</code>
	 * zurück.
	 * 
	 * @see Pointers#nullPointer()
	 * @param <GData> Typ des Datensatzes.
	 * @param pointer {@link Pointer Pointer}
	 * @return gegebener {@link Pointer Pointer} oder {@link Pointer Pointer} auf <code>null</code>.
	 */
	public static <GData> Pointer<GData> pointer(final Pointer<GData> pointer) {
		return ((pointer == null) ? Pointers.<GData>nullPointer() : pointer);
	}

	/**
	 * Diese Methode erzeugt einen {@link Pointer Pointer} auf den gegebenen Datensatz im gegebenen Modus ung gibt ihn
	 * zurück. Erlaubte Modi sind {@link Pointers#HARD}, {@link Pointers#WEAK} und {@link Pointers#SOFT}.
	 * 
	 * @param <GData> Typ des Datensatzes.
	 * @param mode Modus.
	 * @param data Datensatz.
	 * @return {@link Pointer Pointer} auf den Datensatz.
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
		throw new IllegalArgumentException();
	}

	/**
	 * Diese Methode gibt den {@link Pointer Pointer} auf <code>null</code> zurück.
	 * 
	 * @param <GData> Typ des Datensatzes.
	 * @return <code>null</code>-{@link Pointer Pointer}.
	 */
	@SuppressWarnings ("unchecked")
	public static <GData> Pointer<GData> nullPointer() {
		return (Pointer<GData>)Pointers.NULL_POINTER;
	}

	/**
	 * Diese Methode erzeugt einen harten {@link Pointer Pointer} auf den gegebenen Datensatz und gibt ihn zurück. Die
	 * Referenz auf den Datensatz eines solcher {@link Pointer Pointers} wird nicht automatisch aufgelöst.
	 * 
	 * @param <GData> Typ des Datensatzes.
	 * @param data Datensatz.
	 * @return {@link HardPointer Hard-Pointer}.
	 */
	public static <GData> Pointer<GData> hardPointer(final GData data) {
		return ((data == null) ? Pointers.<GData>nullPointer() : new HardPointer<GData>(data));
	}

	/**
	 * Diese Methode erzeugt einen {@link WeakReference schwachen} {@link Pointer Pointer} auf den gegebenen Datensatz und
	 * gibt ihn zurück. Die Referenz auf den Datensatz eines solcher {@link Pointer Pointers} wird nur dann automatisch
	 * aufgelöst, wenn der Datensatz nur noch über {@link WeakReference WeakReferences} erreichbar ist.
	 * 
	 * @param <GData> Typ des Datensatzes.
	 * @param data Datensatz.
	 * @return {@link WeakPointer Weak-Pointer}.
	 */
	public static <GData> Pointer<GData> weakPointer(final GData data) {
		return ((data == null) ? Pointers.<GData>nullPointer() : new WeakPointer<GData>(data));
	}

	/**
	 * Diese Methode erzeugt einen {@link SoftReference weichen} {@link Pointer Pointer} auf den gegebenen Datensatz und
	 * gibt ihn zurück. Die Referenz auf den Datensatz eines solcher {@link Pointer Pointers} wird nur dann automatisch
	 * aufgelöst, wenn der Datensatz nur noch über {@link SoftReference SoftReferences} erreichbar ist und der Garbage
	 * Collector dies entscheidet.
	 * 
	 * @param <GData> Typ des Datensatzes.
	 * @param data Datensatz.
	 * @return {@link SoftPointer Soft-Pointer}.
	 */
	public static <GData> Pointer<GData> softPointer(final GData data) {
		return ((data == null) ? Pointers.<GData>nullPointer() : new SoftPointer<GData>(data));
	}

	/**
	 * Dieser Konstrukteur ist versteckt und verhindert damit die Erzeugung von Instanzen der Klasse.
	 */
	Pointers() {
	}

}
