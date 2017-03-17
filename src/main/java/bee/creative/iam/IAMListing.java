package bee.creative.iam;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import bee.creative.iam.IAMLoader.IAMListingLoader;
import bee.creative.mmf.MMFArray;
import bee.creative.util.Objects;

/** Diese Klasse implementiert eine abstrakte geordnete Liste von Elementen, welche selbst Zahlenfolgen ({@link IAMArray}) sind.
 * <p>
 * Die von {@link #items()} gelieferte {@link List} delegiert an {@link #item(int)} und {@link #itemCount()}.<br>
 * Die Methoden {@link #item(int, int)} und {@link #itemLength(int)} delegieren an {@link #item(int)}.
 *
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public abstract class IAMListing implements Iterable<IAMArray> {

	@SuppressWarnings ("javadoc")
	static final class EmptyListing extends IAMListing {

		@Override
		public final IAMArray item(final int itemIndex) {
			return IAMArray.EMPTY;
		}

		@Override
		public final int itemCount() {
			return 0;
		}

	}

	@SuppressWarnings ("javadoc")
	static final class Items extends AbstractList<IAMArray> {

		final IAMListing owner;

		Items(final IAMListing owner) {
			this.owner = owner;
		}

		{}

		@Override
		public final IAMArray get(final int index) {
			if ((index < 0) || (index >= this.owner.itemCount())) throw new IndexOutOfBoundsException();
			return this.owner.item(index);
		}

		@Override
		public final int size() {
			return this.owner.itemCount();
		}

	}

	{}

	/** Dieses Feld speichert das leere {@link IAMListing}. */
	public static final IAMListing EMPTY = new EmptyListing();

	{}

	/** Diese Methode ist eine Abkürzung für {@code new IAMListingLoader(MMFArray.from(object))}.
	 *
	 * @see MMFArray#from(Object)
	 * @see IAMListingLoader#IAMListingLoader(MMFArray)
	 * @param object Objekt.
	 * @return {@link IAMListingLoader}.
	 * @throws IOException Wenn {@link MMFArray#from(Object)} eine entsprechende Ausnahme auslöst.
	 * @throws IAMException Wenn {@link IAMListingLoader#IAMListingLoader(MMFArray)} eine entsprechende Ausnahme auslöst. */
	public static IAMListingLoader from(final Object object) throws IOException, IAMException {
		return new IAMListingLoader(MMFArray.from(object));
	}

	{}

	/** Diese Methode gibt das {@code itemIndex}-te Element als Zahlenfolge zurück. Bei einem ungültigen {@code itemIndex} wird eine leere Zahlenfolge geliefert.
	 *
	 * @see #item(int, int)
	 * @see #itemLength(int)
	 * @see #itemCount()
	 * @param itemIndex Index des Elements.
	 * @return {@code itemIndex}-tes Element. */
	public abstract IAMArray item(final int itemIndex);

	/** Diese Methode gibt die {@code index}-te Zahl des {@code itemIndex}-ten Elements zurück. Bei einem ungültigen {@code index} oder {@code itemIndex} wird
	 * {@code 0} geliefert.
	 *
	 * @see #itemLength(int)
	 * @see #itemCount()
	 * @param itemIndex Index des Elements.
	 * @param index Index der Zahl.
	 * @return {@code index}-te Zahl des {@code itemIndex}-ten Elements. */
	public final int item(final int itemIndex, final int index) {
		return this.item(index).get(index);
	}

	/** Diese Methode gibt die Länge der Zahlenfolge des {@code itemIndex}-ten Elements zurück. Bei einem ungültigen {@code itemIndex} wird {@code 0} geliefert.
	 *
	 * @see #item(int)
	 * @see #item(int, int)
	 * @see #itemCount()
	 * @param itemIndex Index des Elements.
	 * @return Länge des {@code itemIndex}-ten Elements. */
	public final int itemLength(final int itemIndex) {
		return this.item(itemIndex).length();
	}

	/** Diese Methode gibt die Anzahl der Elemente zurück ({@code 0..1073741823}).
	 *
	 * @see #item(int)
	 * @see #item(int, int)
	 * @return Anzahl der Elemente. */
	public abstract int itemCount();

	/** Diese Methode gibt den Index des Elements zurück, das äquivalenten zum gegebenen ist. Die Suche erfolgt linear vom ersten zum letzten Element. Bei
	 * erfolgloser Suche wird {@code -1} geliefert.
	 *
	 * @param item Element.
	 * @return Index des Elements.
	 * @throws NullPointerException Wenn {@code item} {@code null} ist. */
	public final int find(final IAMArray item) throws NullPointerException {
		item.length();
		for (int i = 0, count = this.itemCount(); i < count; i++) {
			if (item.equals(this.item(i))) return i;
		}
		return -1;
	}

	/** Diese Methode gibt {@link List}-Sicht auf die Elemente zurück.
	 *
	 * @see #item(int)
	 * @see #itemCount()
	 * @return Elemente. */
	public final List<IAMArray> items() {
		return new Items(this);
	}

	/** Diese Methode ist eine Ankürzung für {@code this.toBytes(ByteOrder.nativeOrder())}.
	 *
	 * @return Binärdatenformat {@code IAM_LISTING}. */
	public final byte[] toBytes() {
		return this.toBytes(ByteOrder.nativeOrder());
	}

	/** Diese Methode kodiert dieses {@link IAMListing} in das binäre optimierte Datenformat {@code IAM_LISTING} und gibt dieses als Bytefolge zurück.
	 *
	 * @param order Bytereihenfolge.
	 * @return {@code IAM_LISTING}. */
	public final byte[] toBytes(final ByteOrder order) {

		final int itemCount = this.itemCount();
		final int[][] itemArray = new int[itemCount][];
		for (int i = 0; i < itemCount; i++) {
			itemArray[i] = this.item(i).toArray();
		}

		final IAMIndex.DataStats itemData = new IAMIndex.DataStats(Arrays.asList(itemArray));
		final IAMIndex.SizeStats itemSize = itemData.dataSize;

		final int length = 8 + itemSize.bytes + itemData.bytes;
		final byte[] result = new byte[length];

		final ByteBuffer buffer = ByteBuffer.wrap(result).order(order);
		buffer.putInt(0xF00D2000 | (itemData.type << 2) | (itemSize.type << 0));
		buffer.putInt(itemCount);
		itemSize.putSize(buffer);
		itemData.putData(buffer);

		return result;
	}

	{}

	/** {@inheritDoc} */
	@Override
	public final Iterator<IAMArray> iterator() {
		return this.items().iterator();
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return Objects.toInvokeString("IAMList", this.itemCount());
	}

}
