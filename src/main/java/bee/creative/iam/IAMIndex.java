package bee.creative.iam;

import java.util.AbstractList;
import java.util.List;
import bee.creative.util.Objects;

/**
 * Diese Klasse implementiert eine abstrakte Zusammenstellung beliebig vieler Listen ({@link IAMList}) und Abbildungen ({@link IAMMap}).
 * <p>
 * Die Methoden {@link #maps()} und {@link #lists()} delegieren an {@link #map(int)} und {@link #mapCount()} bzw. {@link #list(int)} und {@link #listCount()}.
 * 
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public abstract class IAMIndex {

	@SuppressWarnings ("javadoc")
	static final class ListsView extends AbstractList<IAMList> {

		final IAMIndex __owner;

		ListsView(final IAMIndex owner) {
			this.__owner = owner;
		}

		{}

		@Override
		public IAMList get(final int index) {
			if ((index < 0) || (index >= this.__owner.listCount())) throw new IndexOutOfBoundsException();
			return this.__owner.list(index);
		}

		@Override
		public int size() {
			return this.__owner.listCount();
		}

	}

	@SuppressWarnings ("javadoc")
	static final class MapsView extends AbstractList<IAMMap> {

		final IAMIndex __owner;

		MapsView(final IAMIndex owner) {
			this.__owner = owner;
		}

		@Override
		public IAMMap get(final int index) {
			if ((index < 0) || (index >= this.__owner.mapCount())) throw new IndexOutOfBoundsException();
			return this.__owner.map(index);
		}

		@Override
		public int size() {
			return this.__owner.mapCount();
		}

	}

	@SuppressWarnings ("javadoc")
	static final class EmptyIndex extends IAMIndex {

		@Override
		public IAMMap map(final int index) {
			return IAMMap.EMPTY;
		}

		@Override
		public int mapCount() {
			return 0;
		}

		@Override
		public IAMList list(final int index) {
			return IAMList.EMPTY;
		}

		@Override
		public int listCount() {
			return 0;
		}

	}

	{}

	/**
	 * Dieses Feld speichert den leeren {@link IAMIndex}.
	 */
	public static final IAMIndex EMPTY = new EmptyIndex();

	{}

	/**
	 * Diese Methode gibt die {@code index}-te Abbildung zurück. Bei einem ungültigen {@code index} wird eine leere Abbildung geliefert.
	 * 
	 * @see #mapCount()
	 * @param index Index.
	 * @return {@code index}-te Abbildung.
	 */
	public abstract IAMMap map(final int index);

	/**
	 * Diese Methode gibt die Anzahl der Abbildungen zurück ({@code 0..1073741823}).
	 * 
	 * @see #map(int)
	 * @return Anzahl der Abbildungen.
	 */
	public abstract int mapCount();

	/**
	 * Diese Methode gibt eine {@link List}-Sicht auf die Abbildungen zurück.
	 * 
	 * @see #map(int)
	 * @see #mapCount()
	 * @return Abbildungen.
	 */
	public final List<IAMMap> maps() {
		return new MapsView(this);
	}

	/**
	 * Diese Methode gibt die {@code index}-te Liste zurück. Bei einem ungültigen {@code index} wird eine leere Liste geliefert.
	 * 
	 * @see #listCount()
	 * @param index Index.
	 * @return {@code index}-te Liste.
	 */
	public abstract IAMList list(final int index);

	/**
	 * Diese Methode gibt die Anzahl der Listen zurück.
	 * 
	 * @see #list(int)
	 * @return Anzahl der Listen.
	 */
	public abstract int listCount();

	/**
	 * Diese Methode gibt {@link List}-Sicht auf die Listen zurück.
	 * 
	 * @see #list(int)
	 * @see #listCount()
	 * @return Listen.
	 */
	public final List<IAMList> lists() {
		return new ListsView(this);
	}

	{}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return Objects.toInvokeString("IAMIndex", this.maps(), this.lists());
	}

}
