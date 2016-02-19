package bee.creative.iam;

import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;
import bee.creative.util.Objects;

/** Diese Klasse implementiert eine abstrakte Abbildung von Schlüsseln auf Werte, welche beide selbst Zahlenfolgen ({@link IAMArray}) sind.
 * <p>
 * Die Methode {@link #entry(int)} liefert einen {@link IAMEntry} mit den von {@link #key(int)} und {@link #value(int)} gelieferten Zahlenfolgen, welcher über
 * {@link IAMEntry#from(IAMArray, IAMArray)} erzeugt wird.<br>
 * Die von {@link #entries()} gelieferte {@link List} delegiert an {@link #entry(int)} und {@link #entryCount()}.
 * 
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public abstract class IAMMap implements Iterable<IAMEntry> {

	@SuppressWarnings ("javadoc")
	static final class ListView extends AbstractList<IAMEntry> {

		final IAMMap _owner_;

		ListView(final IAMMap owner) {
			this._owner_ = owner;
		}

		{}

		@Override
		public final IAMEntry get(final int index) {
			if ((index < 0) || (index >= this._owner_.entryCount())) throw new IndexOutOfBoundsException();
			return this._owner_.entry(index);
		}

		@Override
		public final int size() {
			return this._owner_.entryCount();
		}

	}

	@SuppressWarnings ("javadoc")
	static final class EmptyMap extends IAMMap {

		@Override
		public boolean mode() {
			return IAMMap.MODE_HASHED;
		}

		@Override
		public final IAMArray key(final int entryIndex) {
			return IAMArray.EMPTY;
		}

		@Override
		public final IAMArray value(final int entryIndex) {
			return IAMArray.EMPTY;
		}

		@Override
		public final int entryCount() {
			return 0;
		}

		@Override
		public final int find(final IAMArray key) throws NullPointerException {
			if (key == null) throw new NullPointerException("key = null");
			return -1;
		}

	}

	{}

	/** Dieses Feld speichert die leere {@link IAMMap}. */
	public static final IAMMap EMPTY = new EmptyMap();

	/** Dieses Feld speichert den Mods einer Abbildung, deren Einträge über den Streuwert ihrer Schlüssel gesucht werden. */
	public static final boolean MODE_HASHED = true;

	/** Dieses Feld speichert den Mods einer Abbildung, deren Einträge binär über die Ordnung ihrer Schlüssel gesucht werden. */
	public static final boolean MODE_SORTED = false;

	{}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn Einträge über den Streuwert ihrer Schlüssel gesucht werden.<br>
	 * Wenn sie {@code false} liefert, werden Einträge binär über die Ordnung ihrer Schlüssel gesucht.
	 * 
	 * @see #find(IAMArray)
	 * @see #MODE_HASHED
	 * @see #MODE_SORTED
	 * @return {@code true} bei Nutzung von {@link IAMArray#hash()} und {@code false} bei Nutzung von {@link IAMArray#compare(IAMArray)} in
	 *         {@link #find(IAMArray)}. */
	public abstract boolean mode();

	/** Diese Methode gibt den Schlüssel des {@code entryIndex}-ten Eintrags als Zahlenfolge zurück. Bei einem ungültigen {@code entryIndex} wird eine leere
	 * Zahlenfolge geliefert.
	 * 
	 * @param entryIndex Index des Eintrags.
	 * @return Schlüssel des {@code entryIndex}-ten Eintrags. */
	public abstract IAMArray key(final int entryIndex);

	/** Diese Methode gibt die {@code index}-te Zahl des Schlüssels des {@code entryIndex}-ten Eintrags zurück. Bei einem ungültigen {@code index} oder
	 * {@code entryIndex} wird {@code 0} geliefert.
	 * 
	 * @see #keyLength(int)
	 * @see #entryCount()
	 * @param entryIndex Index des Eintrags.
	 * @param index Index der Zahl.
	 * @return {@code index}-te Zahl des Schlüssels des {@code entryIndex}-ten Eintrags. */
	public final int key(final int entryIndex, final int index) {
		return this.key(entryIndex).get(index);
	}

	/** Diese Methode gibt die Länge der Zahlenfolge des Schlüssels des {@code entryIndex}-ten Eintrags zurück ({@code 0..1073741823}). Bei einem ungültigen
	 * {@code entryIndex} wird {@code 0} geliefert.
	 * 
	 * @param entryIndex Index des Eintrags.
	 * @return Länge eines Schlüssel. */
	public final int keyLength(final int entryIndex) {
		return this.key(entryIndex).length();
	}

	/** Diese Methode gibt den Wert des {@code entryIndex}-ten Eintrags als Zahlenfolge zurück. Bei einem ungültigen {@code entryIndex} wird eine leere Zahlenfolge
	 * geliefert.
	 * 
	 * @param entryIndex Index des Eintrags.
	 * @return Wert des {@code entryIndex}-ten Eintrags. */
	public abstract IAMArray value(final int entryIndex);

	/** Diese Methode gibt die {@code index}-te Zahl des Werts des {@code entryIndex}-ten Eintrags zurück. Bei einem ungültigen {@code index} oder
	 * {@code entryIndex} wird {@code 0} geliefert.
	 * 
	 * @see #valueLength(int)
	 * @see #entryCount()
	 * @param entryIndex Index des Eintrags.
	 * @param index Index der Zahl.
	 * @return {@code index}-te Zahl des Werts des {@code entryIndex}-ten Eintrags. */
	public final int value(final int entryIndex, final int index) {
		return this.value(entryIndex).get(index);
	}

	/** Diese Methode gibt die Länge der Zahlenfolge des Werts des {@code entryIndex}-ten Eintrags zurück ({@code 0..1073741823}). Bei einem ungültigen
	 * {@code entryIndex} wird {@code 0} geliefert.
	 * 
	 * @param entryIndex Index des Eintrags.
	 * @return Länge eines Werts. */
	public final int valueLength(final int entryIndex) {
		return this.value(entryIndex).length();
	}

	/** Diese Methode gibt den {@code entryIndex}-ten Eintrag zurück. Bei einem ungültigen {@code entryIndex} wird ein leerer Eintrag geliefert.
	 * 
	 * @see #entryCount()
	 * @param entryIndex Index des Eintrags.
	 * @return {@code entryIndex}-ter Eintrag. */
	public final IAMEntry entry(final int entryIndex) {
		if ((entryIndex < 0) || (entryIndex >= this.entryCount())) return IAMEntry.EMPTY;
		return IAMEntry.from(this.key(entryIndex), this.value(entryIndex));
	}

	/** Diese Methode gibt die Anzahl der Einträge zurück ({@code 0..1073741823}).
	 * 
	 * @return Anzahl der Einträge. */
	public abstract int entryCount();

	/** Diese Methode gibt {@link List}-Sicht auf die Einträge zurück.
	 * 
	 * @see #entry(int)
	 * @see #entryCount()
	 * @return Einträge. */
	public final List<IAMEntry> entries() {
		return new ListView(this);
	}

	/** Diese Methode gibt den Index des Eintrags zurück, dessen Schlüssel äquivalenten zum gegebenen Schlüssel ist. Bei erfolgloser Suche wird {@code -1}
	 * geliefert.
	 * 
	 * @param key Schlüssel.
	 * @return Index des Entrags.
	 * @throws NullPointerException Wenn {@code key} {@code null} ist. */
	public abstract int find(final IAMArray key) throws NullPointerException;

	{}

	/** {@inheritDoc} */
	@Override
	public final Iterator<IAMEntry> iterator() {
		return this.entries().iterator();
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return Objects.toInvokeString("IAMMap", this.entryCount());
	}

}
