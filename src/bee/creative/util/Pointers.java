package bee.creative.util;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

/**
 * Diese Klasse implementiert Hilfsmethoden und Hilfsklassen zur Konstruktion und Verarbeitung von {@link Pointer
 * Verweisen}.
 * 
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class Pointers {

	/**
	 * Diese Klasse implementiert einen abstrakten Verweis auf eien Datensatz. Nachfahren dieser Klasse nutzen hierfür
	 * unterschiedlich starke Referenzen zu dem Datenzatz.
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
	 * Diese Klasse implementiert einen harten Verweis auf einen Datensatz. Ein solcher Verweis wird nicht automatisch
	 * aufgelöst.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GData> Typ des Datensatzes.
	 */
	static public final class HardPointer<GData> extends BasePointer<GData> {

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
	 * Diese Klasse implementiert einen {@link WeakReference schwachen} Verweis auf einen Datensatz. Ein solcher Verweis
	 * wird dann aufgelöst, wenn der Datensatz nur noch {@link WeakReference schwach} erreichbar ist.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GData> Typ des Datensatzes.
	 */
	static public final class WeakPointer<GData> extends BasePointer<GData> {

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
			return Objects.toStringCall("weakPointer", this.data.get());
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link SoftReference weichen} Verweis auf einen Datensatz. Ein solcher Verweis
	 * wird bei Speichermangel aufgelöst.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GData> Typ des Datensatzes.
	 */
	static public final class SoftPointer<GData> extends BasePointer<GData> {

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
			return Objects.toStringCall("softPointer", this.data.get());
		}

	}

	/**
	 * Dieses Feld speichert den Verweis auf <code>null</code>.
	 */
	static final BasePointer<?> NULL_POINTER = new BasePointer<Object>() {

		@Override
		public Object data() {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("nullPointer");
		}

	};

	/**
	 * Dieses Feld speichert den Modus der Methode {@link Pointers#pointer(int, Object)}, in welchem {@link HardPointer
	 * harte Verweise} auf Datensätze erzeugt bzw. genutzt werden. Ein solcher Verweis wird nicht automatisch aufgelöst.
	 */
	static public final int HARD = 0;

	/**
	 * Dieses Feld speichert den Modus der Methode {@link Pointers#pointer(int, Object)}, in welchem {@link WeakPointer
	 * schwache Verweise} auf Datensätze erzeugt bzw. genutzt werden. Ein solcher Verweis wird dann aufgelöst, wenn der
	 * Datensatz nur noch {@link WeakReference schwach} erreichbar ist.
	 */
	static public final int WEAK = 1;

	/**
	 * Dieses Feld speichert den Modus der Methode {@link Pointers#pointer(int, Object)}, in welchem {@link SoftPointer
	 * weich Verweise} auf Datensätze erzeugt bzw. genutzt werden. Ein solcher Verweis wird dann aufgelöst, wenn der
	 * Datensatz nur noch {@link WeakReference weich} erreichbar ist.
	 */
	static public final int SOFT = 2;

	/**
	 * Diese Methode erzeugt einen {@link Pointer Verweis} auf den gegebenen Datensatz im gegebenen Modus ung gibt ihn
	 * zurück. Erlaubte Modi sind {@link Pointers#HARD}, {@link Pointers#WEAK} und {@link Pointers#SOFT}.
	 * 
	 * @param <GData> Typ des Datensatzes.
	 * @param mode Modus.
	 * @param data Datensatz.
	 * @return {@link Pointer Verweis} auf den Datensatz.
	 * @throws IllegalArgumentException Wenn der gegebenen Modus ungültig ist.
	 */
	static public final <GData> Pointer<GData> pointer(final int mode, final GData data) throws IllegalArgumentException {
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
	 * Diese Methode gibt den {@link Pointer Verweis} auf <code>null</code> zurück.
	 * 
	 * @param <GData> Typ des Datensatzes.
	 * @return <code>null</code>-{@link Pointer Verweis}.
	 */
	@SuppressWarnings ("unchecked")
	static public final <GData> Pointer<GData> nullPointer() {
		return (Pointer<GData>)Pointers.NULL_POINTER;
	}

	/**
	 * Diese Methode erzeugt einen {@link HardPointer harten Verweis} auf den gegebenen Datensatz und gibt ihn zurück. Ein
	 * solcher Verweis wird nicht automatisch aufgelöst.
	 * 
	 * @param <GData> Typ des Datensatzes.
	 * @param data Datensatz.
	 * @return {@link HardPointer harter Verweis}.
	 */
	static public final <GData> Pointer<GData> hardPointer(final GData data) {
		return ((data == null) ? Pointers.<GData>nullPointer() : new HardPointer<GData>(data));
	}

	/**
	 * Diese Methode erzeugt einen {@link WeakPointer schwachen Verweis} auf den gegebenen Datensatz und gibt ihn zurück.
	 * Ein solcher Verweis wird dann aufgelöst, wenn der Datensatz nur noch {@link WeakReference schwach} erreichbar ist.
	 * 
	 * @param <GData> Typ des Datensatzes.
	 * @param data Datensatz.
	 * @return {@link WeakPointer schwachen Verweis}.
	 */
	static public final <GData> Pointer<GData> weakPointer(final GData data) {
		return ((data == null) ? Pointers.<GData>nullPointer() : new WeakPointer<GData>(data));
	}

	/**
	 * Diese Methode erzeugt einen {@link SoftPointer weichen Verweis} auf den gegebenen Datensatz und gibt ihn zurück.
	 * Ein solcher Verweis wird bei Speichermangel aufgelöst.
	 * 
	 * @param <GData> Typ des Datensatzes.
	 * @param data Datensatz.
	 * @return {@link SoftPointer weicher Verweis}.
	 */
	static public final <GData> Pointer<GData> softPointer(final GData data) {
		return ((data == null) ? Pointers.<GData>nullPointer() : new SoftPointer<GData>(data));
	}

	/**
	 * Dieser Konstrukteur ist versteckt und verhindert damit die Erzeugung von Instanzen der Klasse.
	 */
	Pointers() {
	}

}
