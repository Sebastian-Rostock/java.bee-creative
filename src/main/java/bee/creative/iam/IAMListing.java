package bee.creative.iam;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;
import bee.creative.lang.Bytes;
import bee.creative.lang.Objects;
import bee.creative.mmi.MMIArray;

/** Diese Klasse implementiert eine abstrakte geordnete Auflistung von Elementen, welche selbst Zahlenfolgen ({@link IAMArray}) sind.
 * <p>
 * Die von {@link #items()} gelieferte {@link List} delegiert an {@link #item(int)} und {@link #itemCount()}. Die Methoden {@link #item(int, int)} und
 * {@link #itemLength(int)} delegieren an {@link #item(int)}.
 *
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public abstract class IAMListing implements Iterable<IAMArray> {

	static final class EmptyListing extends IAMListing {

		@Override
		public final IAMArray item(final int itemIndex) {
			return IAMArray.EMPTY;
		}

		@Override
		public final int itemCount() {
			return 0;
		}

		@Override
		public final IAMListing toListing() {
			return this;
		}

	}

	static final class CompactListing extends IAMListing {

		final int[] itemData;

		final int[] itemOffset;

		final int itemLength;

		final int itemCount;

		public CompactListing(final IAMListing that) {
			final int itemCount = that.itemCount();
			final int[] itemOffset = new int[itemCount + 1];
			int itemLength = that.itemLength(0), itemDatalength = 0;
			for (int index = 0; index < itemCount;) {
				final int itemLength2 = that.itemLength(index);
				itemDatalength += itemLength2;
				index++;
				itemOffset[index] = itemDatalength;
				if (itemLength2 != itemLength) {
					itemLength = -1;
				}
			}
			final int[] itemData = new int[itemDatalength];
			for (int index = 0; index < itemCount; index++) {
				that.item(index).get(itemData, itemOffset[index]);
			}
			this.itemData = itemData;
			this.itemOffset = itemLength < 0 ? itemOffset : null;
			this.itemLength = itemLength;
			this.itemCount = itemCount;
		}

		@Override
		public final IAMArray item(final int itemIndex) {
			if ((itemIndex < 0) || (itemIndex >= this.itemCount)) return IAMArray.EMPTY;
			if (this.itemOffset == null) {
				final int length = this.itemLength;
				return IAMArray.from(this.itemData, length * itemIndex, length);
			} else {
				final int offset = this.itemOffset[itemIndex];
				return IAMArray.from(this.itemData, offset, this.itemOffset[itemIndex + 1] - offset);
			}
		}

		@Override
		public final int item(final int itemIndex, final int index) {
			if ((index < 0) || (itemIndex < 0) || (itemIndex >= this.itemCount)) return 0;
			if (this.itemOffset == null) {
				if (index >= this.itemLength) return 0;
				return this.itemData[(itemIndex * this.itemLength) + index];
			} else {
				final int offset = this.itemOffset[itemIndex] + index;
				if (index >= this.itemOffset[itemIndex + 1]) return 0;
				return this.itemData[offset];
			}
		}

		@Override
		public final int itemLength(final int itemIndex) {
			if ((itemIndex < 0) || (itemIndex >= this.itemCount)) return 0;
			if (this.itemOffset == null) return this.itemLength;
			return this.itemOffset[itemIndex + 1] - this.itemOffset[itemIndex];
		}

		@Override
		public final int itemCount() {
			return this.itemCount;
		}

		@Override
		public final IAMListing toListing() {
			return this;
		}

	}

	static final class Items extends AbstractList<IAMArray> implements RandomAccess {

		final IAMListing owner;

		Items(final IAMListing owner) {
			this.owner = owner;
		}

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

	/** Dieses Feld speichert das leere {@link IAMListing}. */
	public static final IAMListing EMPTY = new EmptyListing();

	/** Diese Methode erzeugt aus dem gegebenen Objekt ein {@link IAMListing} und gibt dieses zurück. Wenn das Objekt ein {@link IAMListing} ist, wird dieses
	 * geliefert. Wenn es ein {@link MMIArray} ist, wird zu diesem ein {@link IAMListingLoader} erzeugt. Andernfalls wird das {@link MMIArray} über
	 * {@link MMIArray#from(Object)} ermittelt und in die Bytereihenfolge passend zu {@link IAMListingLoader#HEADER} überführt.
	 *
	 * @param object Objekt.
	 * @return {@link IAMListing}.
	 * @throws IOException Wenn {@link MMIArray#from(Object)} eine entsprechende Ausnahme auslöst.
	 * @throws IAMException Wenn {@link IAMListingLoader#IAMListingLoader(MMIArray)} eine entsprechende Ausnahme auslöst. */
	public static IAMListing from(final Object object) throws IOException, IAMException {
		if (object instanceof IAMListing) return (IAMListing)object;
		if (object instanceof MMIArray) return new IAMListingLoader((MMIArray)object);
		final MMIArray array = MMIArray.from(object);
		return new IAMListingLoader(array.as(IAMListingLoader.HEADER.orderOf(array)));
	}

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
	public int item(final int itemIndex, final int index) {
		return this.item(index).get(index);
	}

	/** Diese Methode gibt die Länge der Zahlenfolge des {@code itemIndex}-ten Elements zurück. Bei einem ungültigen {@code itemIndex} wird {@code 0} geliefert.
	 *
	 * @see #item(int)
	 * @see #item(int, int)
	 * @see #itemCount()
	 * @param itemIndex Index des Elements.
	 * @return Länge des {@code itemIndex}-ten Elements. */
	public int itemLength(final int itemIndex) {
		return this.item(itemIndex).length();
	}

	/** Diese Methode gibt die Anzahl der Elemente zurück ({@code 0..1073741823}).
	 *
	 * @see #item(int)
	 * @see #item(int, int)
	 * @return Anzahl der Elemente. */
	public abstract int itemCount();

	/** Diese Methode ist eine Abkürzung für {@link #find(IAMArray) this.find(IAMArray.from(item))}.
	 *
	 * @see IAMArray#from(int...) */
	public final int find(final int... item) throws NullPointerException {
		return this.find(IAMArray.from(item));
	}

	/** Diese Methode gibt den Index des Elements zurück, das äquivalenten zum gegebenen ist. Die Suche erfolgt linear vom ersten zum letzten Element. Bei
	 * erfolgloser Suche wird {@code -1} geliefert.
	 *
	 * @param item Element.
	 * @return Index des Elements.
	 * @throws NullPointerException Wenn {@code item} {@code null} ist. */
	public int find(final IAMArray item) throws NullPointerException {
		Objects.notNull(item);
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

	/** Diese Methode ist eine Ankürzung für {@code this.toBytes(Bytes.NATIVE_ORDER)}.
	 *
	 * @return Binärdatenformat {@code IAM_LISTING}. */
	public final byte[] toBytes() {
		return this.toBytes(Bytes.NATIVE_ORDER);
	}

	/** Diese Methode kodiert dieses {@link IAMListing} in das binäre optimierte Datenformat {@code IAM_LISTING} und gibt dieses als Bytefolge zurück.
	 *
	 * @param order Bytereihenfolge.
	 * @return {@code IAM_LISTING}. */
	public final byte[] toBytes(final ByteOrder order) {

		final int itemCount = this.itemCount();
		final int[][] itemArray = new int[itemCount][];
		for (int i = 0; i < itemCount; i++) {
			itemArray[i] = this.item(i).toInts();
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

	/** Diese Methode kodiert dieses {@link IAMListing} in eine für den Arbeitsspeicher optimierte Datenstruktur aus {@code int[]} und gibt diese zurück.
	 *
	 * @return optimiertes {@link IAMListing}. */
	public IAMListing toListing() {
		if (this.itemCount() == 0) return this;
		return new CompactListing(this);
	}

	/** {@inheritDoc} */
	@Override
	public final Iterator<IAMArray> iterator() {
		return this.items().iterator();
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return Objects.toInvokeString(this, this.itemCount());
	}

}
