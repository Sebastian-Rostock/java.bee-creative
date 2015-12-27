package bee.creative.iam;

import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;
import bee.creative.util.Objects;

/**
 * Diese Klasse implementiert eine abstrakte geordnete Liste von Elementen, welche selbst Zahlenfolgen ({@link IAMArray}) sind.
 * <p>
 * Die von {@link #items()} gelieferte {@link List} delegiert an {@link #item(int)} und {@link #itemCount()}.<br>
 * Die Methoden {@link #item(int, int)} und {@link #itemLength(int)} delegieren an {@link #item(int)}.
 * 
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public abstract class IAMList implements Iterable<IAMArray> {

	@SuppressWarnings ("javadoc")
	static final class ListView extends AbstractList<IAMArray> {

		final IAMList __owner;

		ListView(final IAMList owner) {
			this.__owner = owner;
		}

		{}

		@Override
		public final IAMArray get(final int index) {
			if ((index < 0) || (index >= this.__owner.itemCount())) throw new IndexOutOfBoundsException();
			return this.__owner.item(index);
		}

		@Override
		public final int size() {
			return this.__owner.itemCount();
		}

	}

	@SuppressWarnings ("javadoc")
	static final class EmptyList extends IAMList {

		@Override
		public final IAMArray item(final int itemIndex) {
			return IAMArray.EMPTY;
		}

		@Override
		public final int itemCount() {
			return 0;
		}

	}

	{}

	/**
	 * Dieses Feld speichert die leere {@link IAMList}.
	 */
	public static final IAMList EMPTY = new EmptyList();

	{}

	/**
	 * Diese Methode gibt das {@code itemIndex}-te Element als Zahlenfolge zurück. Bei einem ungültigen {@code itemIndex} wird eine leere Zahlenfolge geliefert.
	 * 
	 * @see #item(int, int)
	 * @see #itemLength(int)
	 * @see #itemCount()
	 * @param itemIndex Index des Elements.
	 * @return {@code itemIndex}-tes Element.
	 */
	public abstract IAMArray item(final int itemIndex);

	/**
	 * Diese Methode gibt die {@code index}-te Zahl des {@code itemIndex}-ten Elements zurück. Bei einem ungültigen {@code index} oder {@code itemIndex} wird
	 * {@code 0} geliefert.
	 * 
	 * @see #itemLength(int)
	 * @see #itemCount()
	 * @param itemIndex Index des Elements.
	 * @param index Index der Zahl.
	 * @return {@code index}-te Zahl des {@code itemIndex}-ten Elements.
	 */
	public final int item(final int itemIndex, final int index) {
		return this.item(index).get(index);
	}

	/**
	 * Diese Methode gibt die Länge der Zahlenfolge des {@code itemIndex}-ten Elements zurück. Bei einem ungültigen {@code itemIndex} wird {@code 0} geliefert.
	 * 
	 * @see #item(int)
	 * @see #item(int, int)
	 * @see #itemCount()
	 * @param itemIndex Index des Elements.
	 * @return Länge des {@code itemIndex}-ten Elements.
	 */
	public final int itemLength(final int itemIndex) {
		return this.item(itemIndex).length();
	}

	/**
	 * Diese Methode gibt die Anzahl der Elemente zurück ({@code 0..1073741823}).
	 * 
	 * @see #item(int)
	 * @see #item(int, int)
	 * @return Anzahl der Elemente.
	 */
	public abstract int itemCount();

	/**
	 * Diese Methode gibt {@link List}-Sicht auf die Elemente zurück.
	 * 
	 * @see #item(int)
	 * @see #itemCount()
	 * @return Elemente.
	 */
	public final List<IAMArray> items() {
		return new ListView(this);
	}

	{}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Iterator<IAMArray> iterator() {
		return this.items().iterator();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return Objects.toInvokeString("IAMList", this.itemCount());
	}

}
