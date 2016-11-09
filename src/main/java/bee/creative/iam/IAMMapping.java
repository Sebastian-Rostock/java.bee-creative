package bee.creative.iam;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import bee.creative.iam.IAMIndex.DataStats;
import bee.creative.iam.IAMIndex.SizeStats;
import bee.creative.iam.IAMLoader.IAMMappingLoader;
import bee.creative.mmf.MMFArray;
import bee.creative.util.Objects;

/** Diese Klasse implementiert eine abstrakte Abbildung von Schlüsseln auf Werte, welche beide selbst Zahlenfolgen ({@link IAMArray}) sind.
 * <p>
 * Die Methode {@link #entry(int)} liefert einen {@link IAMEntry} mit den von {@link #key(int)} und {@link #value(int)} gelieferten Zahlenfolgen, welcher über
 * {@link IAMEntry#from(IAMArray, IAMArray)} erzeugt wird.<br>
 * Die von {@link #entries()} gelieferte {@link List} delegiert an {@link #entry(int)} und {@link #entryCount()}.
 *
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public abstract class IAMMapping implements Iterable<IAMEntry> {

	@SuppressWarnings ("javadoc")
	static final class EmptyMapping extends IAMMapping {

		@Override
		public boolean mode() {
			return IAMMapping.MODE_HASHED;
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

	@SuppressWarnings ("javadoc")
	static final class Entries extends AbstractList<IAMEntry> {

		final IAMMapping _owner_;

		Entries(final IAMMapping owner) {
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

	{}

	/** Dieses Feld speichert die leere {@link IAMMapping}. */
	public static final IAMMapping EMPTY = new EmptyMapping();

	/** Dieses Feld speichert den Mods einer Abbildung, deren Einträge über den Streuwert ihrer Schlüssel gesucht werden.
	 *
	 * @see #mode() */
	public static final boolean MODE_HASHED = true;

	/** Dieses Feld speichert den Mods einer Abbildung, deren Einträge binär über die Ordnung ihrer Schlüssel gesucht werden.
	 *
	 * @see #mode() */
	public static final boolean MODE_SORTED = false;

	{}

	/** Diese Methode ist eine Abkürzung für {@code new IAMMappingLoader(MMFArray.from(object))}.
	 *
	 * @see MMFArray#from(Object)
	 * @see IAMMappingLoader#IAMMappingLoader(MMFArray)
	 * @param object Objekt.
	 * @return {@link IAMMappingLoader}.
	 * @throws IOException Wenn {@link MMFArray#from(Object)} eine entsprechende Ausnahme auslöst.
	 * @throws IAMException Wenn {@link IAMMappingLoader#IAMMappingLoader(MMFArray)} eine entsprechende Ausnahme auslöst. */
	public static IAMMappingLoader from(final Object object) throws IOException, IAMException {
		return new IAMMappingLoader(MMFArray.from(object));
	}

	{}

	/** Diese Methode gibt nur dann {@link #MODE_HASHED} zurück, wenn Einträge über den Streuwert ihrer Schlüssel gesucht werden.<br>
	 * Wenn sie {@link #MODE_SORTED} liefert, werden Einträge binär über die Ordnung ihrer Schlüssel gesucht.
	 *
	 * @see #find(IAMArray)
	 * @see #MODE_HASHED
	 * @see #MODE_SORTED
	 * @return {@link #MODE_HASHED} bei Nutzung von {@link IAMArray#hash()} bzw. {@link #MODE_SORTED} bei Nutzung von {@link IAMArray#compare(IAMArray)} in
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

	/** Diese Methode gibt den Wert des {@code entryIndex}-ten Eintrags als Zahlenfolge zurück. Bei einem ungültigen {@code entryIndex} wird eine leere
	 * Zahlenfolge geliefert.
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
		return new Entries(this);
	}

	/** Diese Methode gibt den Index des Eintrags zurück, dessen Schlüssel äquivalenten zum gegebenen Schlüssel ist. Die Suche erfolgt ordnungs- oder
	 * streuwertbasiert, d.h. indiziert. Bei erfolgloser Suche wird {@code -1} geliefert.
	 *
	 * @param key Schlüssel.
	 * @return Index des Entrags.
	 * @throws NullPointerException Wenn {@code key} {@code null} ist. */
	public abstract int find(final IAMArray key) throws NullPointerException;

	/** Diese Methode ist eine Ankürzung für {@code this.toBytes(ByteOrder.nativeOrder())}.
	 *
	 * @return Binärdatenformat {@code IAM_LISTING}. */
	public final byte[] toBytes() {
		return this.toBytes(ByteOrder.nativeOrder());
	}

	/** Diese Methode kodiert dieses {@link IAMMapping} in das binäre optimierte Datenformat {@code IAM_MAPPING} und gibt dieses als Bytefolge zurück.
	 *
	 * @param order Bytereihenfolge.
	 * @return {@code IAM_MAPPING}. */
	public final byte[] toBytes(final ByteOrder order) {

		final int entryCount = this.entryCount();
		final int[][] keyArray = new int[entryCount][];
		final int[][] valueArray = new int[entryCount][];
		final Integer[] indexArray = new Integer[entryCount];
		for (int i = 0; i < entryCount; i++) {
			keyArray[i] = this.key(i).toArray();
			valueArray[i] = this.value(i).toArray();
			indexArray[i] = new Integer(i);
		}

		final int[][] keyArray2 = keyArray.clone();
		final int[][] valueArray2 = valueArray.clone();

		final int rangeMask;
		final int rangeCount;
		final int[] rangeData;
		final int rangeDataType;
		final int rangeDataBytes;
		final int rangeBytes;

		if (this.mode()) {

			int limit = 2;
			while (limit < entryCount) {
				limit <<= 1;
			}
			rangeMask = (limit - 1) & 536870911;

			rangeCount = rangeMask + 2;
			rangeData = new int[rangeCount];
			rangeDataType = SizeStats.computeSizeType(entryCount);
			rangeDataBytes = rangeCount * IAMLoader._byteCount_(rangeDataType);
			rangeBytes = ((rangeDataBytes + 3) & -4) + 4;

			final int[] rangeIndex = new int[entryCount];
			for (int i = 0; i < entryCount; i++) {
				final int index = IAMBuilder.hash(keyArray[i]) & rangeMask;
				rangeData[index]++;
				rangeIndex[i] = index;
			}

			int offset = 0;
			for (int i = 0; i < rangeCount; i++) {
				final int value = rangeData[i];
				rangeData[i] = offset;
				offset += value;
			}

			Arrays.sort(indexArray, new Comparator<Integer>() {

				@Override
				public int compare(final Integer index1, final Integer index2) {
					return rangeIndex[index1.intValue()] - rangeIndex[index2.intValue()];
				}

			});

		} else {

			Arrays.sort(indexArray, new Comparator<Integer>() {

				@Override
				public int compare(final Integer index1, final Integer index2) {
					return IAMBuilder.compare(keyArray[index1.intValue()], keyArray[index2.intValue()]);
				}

			});

			rangeMask = 0;
			rangeData = null;
			rangeDataType = 0;
			rangeDataBytes = 0;
			rangeBytes = 0;

		}

		for (int i = 0; i < entryCount; i++) {
			final int index = indexArray[i].intValue();
			keyArray[i] = keyArray2[index];
			valueArray[i] = valueArray2[index];
		}

		final IAMIndex.DataStats keyData = new IAMIndex.DataStats(Arrays.asList(keyArray));
		final IAMIndex.SizeStats keySize = keyData.dataSize;

		final IAMIndex.DataStats valueData = new IAMIndex.DataStats(Arrays.asList(valueArray));
		final IAMIndex.SizeStats valueSize = valueData.dataSize;

		final int length = 8 + rangeBytes + keySize.bytes + keyData.bytes + valueSize.bytes + valueData.bytes;
		final byte[] result = new byte[length];

		final ByteBuffer buffer = ByteBuffer.wrap(result).order(order);
		buffer.putInt(0xF00D1000 | (keyData.type << 8) | (keySize.type << 6) | (rangeDataType << 4) | (valueData.type << 2) | (valueSize.type << 0));
		buffer.putInt(entryCount);
		if (rangeDataType != 0) {
			buffer.putInt(rangeMask);
			DataStats.putData(buffer, rangeDataType, rangeData);
		}
		keySize.putSize(buffer);
		keyData.putData(buffer);
		valueSize.putSize(buffer);
		valueData.putData(buffer);

		return result;
	}

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
