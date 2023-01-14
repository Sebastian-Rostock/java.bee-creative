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
import bee.creative.lang.Bytes;
import bee.creative.lang.Objects;
import bee.creative.mmi.MMIArray;
import bee.creative.util.Comparators;

/** Diese Klasse implementiert eine abstrakte Abbildung von Schlüsseln auf Werte, welche beide selbst Zahlenfolgen ({@link IAMArray}) sind.
 * <p>
 * Die Methode {@link #entry(int)} liefert einen {@link IAMEntry} mit den von {@link #key(int)} und {@link #value(int)} gelieferten Zahlenfolgen, welcher über
 * {@link IAMEntry#from(IAMArray, IAMArray)} erzeugt wird. Die von {@link #entries()} gelieferte {@link List} delegiert an {@link #entry(int)} und
 * {@link #entryCount()}.
 *
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public abstract class IAMMapping implements Iterable<IAMEntry> {

	static class EmptyMapping extends IAMMapping {

		@Override
		public boolean mode() {
			return IAMMapping.MODE_HASHED;
		}

		@Override
		public IAMArray key(final int entryIndex) {
			return IAMArray.EMPTY;
		}

		@Override
		public IAMArray value(final int entryIndex) {
			return IAMArray.EMPTY;
		}

		@Override
		public int entryCount() {
			return 0;
		}

		@Override
		public int find(final IAMArray key) throws NullPointerException {
			Objects.notNull(key);
			return -1;
		}

		@Override
		public IAMMapping toMapping() {
			return this;
		}

	}

	static class CompactMapping extends IAMMapping {

		final int[] keyData;

		final int[] keyOffset;

		final int keyLength;

		final int[] valueData;

		final int[] valueOffset;

		final int valueLength;

		final int rangeMask;

		final int[] rangeOffset;

		final int entryCount;

		CompactMapping(final IAMMapping that) {
			final int entryCount = that.entryCount();
			final Integer[] indexArray = new Integer[entryCount];
			for (int i = 0; i < entryCount; i++) {
				indexArray[i] = new Integer(i);
			}
			final int rangeMask;
			final int[] rangeOffset;
			if (that.mode()) {
				rangeMask = IAMMapping.mask(entryCount);
				final int rangeCount = rangeMask + 2;
				final int[] bucketIndex = new int[entryCount];
				rangeOffset = new int[rangeCount];
				for (int i = 0; i < entryCount; i++) {
					final int bucket = that.key(i).hash() & rangeMask;
					rangeOffset[bucket]++;
					bucketIndex[i] = bucket;
				}
				for (int i = 0, indexOffset = 0; i < rangeCount; i++) {
					final int bucketSize = rangeOffset[i];
					rangeOffset[i] = indexOffset;
					indexOffset += bucketSize;
				}
				Arrays.sort(indexArray, new Comparator<Integer>() {

					@Override
					public int compare(final Integer index1, final Integer index2) {
						return bucketIndex[index1.intValue()] - bucketIndex[index2.intValue()];
					}

				});
			} else {
				Arrays.sort(indexArray, new Comparator<Integer>() {

					@Override
					public int compare(final Integer index1, final Integer index2) {
						return that.key(index1.intValue()).compare(that.key(index2.intValue()));
					}

				});
				rangeMask = 0;
				rangeOffset = null;
			}
			final int[] keyOffset = new int[entryCount + 1], valueOffset = new int[entryCount + 1];
			int keyLength = that.keyLength(0), valueLength = that.valueLength(0), keyDatalength = 0, valueDatalength = 0;
			for (int i = 0; i < entryCount;) {
				final int index = indexArray[i].intValue();
				final int keyLength2 = that.keyLength(index), valueLength2 = that.valueLength(index);
				keyDatalength += keyLength2;
				valueDatalength += valueLength2;
				i++;
				keyOffset[i] = keyDatalength;
				valueOffset[i] = valueDatalength;
				if (keyLength2 != keyLength) {
					keyLength = -1;
				}
				if (valueLength2 != valueLength) {
					valueLength = -1;
				}
			}
			final int[] keyData = new int[keyDatalength], valueData = new int[valueDatalength];
			for (int i = 0; i < entryCount; i++) {
				final int index = indexArray[i].intValue();
				that.key(index).get(keyData, keyOffset[i]);
				that.value(index).get(valueData, valueOffset[i]);
			}
			this.keyData = keyData;
			this.keyOffset = keyOffset;
			this.keyLength = keyLength;
			this.valueData = valueData;
			this.valueOffset = valueOffset;
			this.valueLength = valueLength;
			this.rangeMask = rangeMask;
			this.rangeOffset = rangeOffset;
			this.entryCount = entryCount;
		}

		@Override
		public boolean mode() {
			return this.rangeMask != 0;
		}

		@Override
		public int find(final IAMArray key) throws NullPointerException {
			Objects.notNull(key);
			int i = this.rangeMask;
			if (i != 0) {
				final int[] range = this.rangeOffset;
				i = key.hash() & i;
				for (int l = range[i], r = range[i + 1]; l < r; l++) {
					if (key.equals(this.key(l))) return l;
				}
			} else {
				int l = 0, r = this.entryCount;
				while (l < r) {
					final int c = (l + r) >> 1;
					i = key.compare(this.key(c));
					if (i < 0) {
						r = c;
					} else if (i > 0) {
						l = c + 1;
					} else return c;
				}
			}
			return -1;
		}

		@Override
		public IAMArray key(final int entryIndex) {
			if ((entryIndex < 0) || (entryIndex >= this.entryCount)) return IAMArray.EMPTY;
			if (this.keyOffset == null) {
				final int length = this.keyLength;
				return IAMArray.from(this.keyData, length * entryIndex, length);
			} else {
				final int offset = this.keyOffset[entryIndex];
				return IAMArray.from(this.keyData, offset, this.keyOffset[entryIndex + 1] - offset);
			}
		}

		@Override
		public int key(final int entryIndex, final int index) {
			if ((index < 0) || (entryIndex < 0) || (entryIndex >= this.entryCount)) return 0;
			if (this.keyOffset == null) {
				if (index >= this.keyLength) return 0;
				return this.keyData[(entryIndex * this.keyLength) + index];
			} else {
				final int offset = this.keyOffset[entryIndex] + index;
				if (index >= this.keyOffset[entryIndex + 1]) return 0;
				return this.keyData[offset];
			}
		}

		@Override
		public int keyLength(final int entryIndex) {
			if ((entryIndex < 0) || (entryIndex >= this.entryCount)) return 0;
			if (this.keyOffset == null) return this.keyLength;
			return this.keyOffset[entryIndex + 1] - this.keyOffset[entryIndex];
		}

		@Override
		public IAMArray value(final int entryIndex) {
			if ((entryIndex < 0) || (entryIndex >= this.entryCount)) return IAMArray.EMPTY;
			if (this.valueOffset == null) {
				final int length = this.valueLength;
				return IAMArray.from(this.valueData, length * entryIndex, length);
			} else {
				final int offset = this.valueOffset[entryIndex];
				return IAMArray.from(this.valueData, offset, this.valueOffset[entryIndex + 1] - offset);
			}
		}

		@Override
		public int value(final int entryIndex, final int index) {
			if ((index < 0) || (entryIndex < 0) || (entryIndex >= this.entryCount)) return 0;
			if (this.valueOffset == null) {
				if (index >= this.valueLength) return 0;
				return this.valueData[(entryIndex * this.valueLength) + index];
			} else {
				final int offset = this.valueOffset[entryIndex] + index;
				if (index >= this.valueOffset[entryIndex + 1]) return 0;
				return this.valueData[offset];
			}
		}

		@Override
		public int valueLength(final int entryIndex) {
			if ((entryIndex < 0) || (entryIndex >= this.entryCount)) return 0;
			if (this.valueOffset == null) return this.valueLength;
			return this.valueOffset[entryIndex + 1] - this.valueOffset[entryIndex];
		}

		@Override
		public int entryCount() {
			return this.entryCount;
		}

		@Override
		public IAMMapping toMapping() {
			return this;
		}

	}

	static class Entries extends AbstractList<IAMEntry> {

		final IAMMapping owner;

		Entries(final IAMMapping owner) {
			this.owner = owner;
		}

		@Override
		public IAMEntry get(final int index) {
			if ((index < 0) || (index >= this.owner.entryCount())) throw new IndexOutOfBoundsException();
			return this.owner.entry(index);
		}

		@Override
		public int size() {
			return this.owner.entryCount();
		}

	}

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

	/** Diese Methode erzeugt aus dem gegebenen Objekt ein {@link IAMMapping} und gibt dieses zurück. Wenn das Objekt ein {@link IAMMapping} ist, wird dieses
	 * geliefert. Wenn es ein {@link MMIArray} ist, wird zu diesem ein {@link IAMMappingLoader} erzeugt. Andernfalls wird das {@link MMIArray} über
	 * {@link MMIArray#from(Object)} ermittelt und in die Bytereihenfolge passend zu {@link IAMMappingLoader#HEADER} überführt.
	 *
	 * @param object Objekt.
	 * @return {@link IAMMapping}.
	 * @throws IOException Wenn {@link MMIArray#from(Object)} eine entsprechende Ausnahme auslöst.
	 * @throws IAMException Wenn {@link IAMMappingLoader#IAMMappingLoader(MMIArray)} eine entsprechende Ausnahme auslöst. */
	public static IAMMapping from(final Object object) throws IOException, IAMException {
		if (object instanceof IAMMapping) return (IAMMapping)object;
		if (object instanceof MMIArray) return new IAMMappingLoader((MMIArray)object);
		final MMIArray array = MMIArray.from(object);
		return new IAMMappingLoader(array.as(IAMMappingLoader.HEADER.orderOf(array)));
	}

	/** Diese Methode gibt die Bitmaske zurück, die der Umrechnung des {@link IAMArray#hash() Streuwerts} eines gesuchten {@link #key(int) Schlüssels} in den
	 * Index des einzigen Schlüsselbereichs dient, in dem ein gesuchter Schlüssel enthalten sein kann. Die Bitmaske ist eine um {@code 1} verringerte Potenz von
	 * {@code 2}. Ein Algorithmus zur Ermittlung der Bitmaske ist:<pre>
	 * int result = 2;
	 * while (result < entryCount) result = result << 1;
	 * return (result – 1) & 536870911;</pre>
	 *
	 * @param entryCount Anzahl der Einträge der Abbildung.
	 * @return Bitmaske. */
	public static int mask(int entryCount) {
		if (entryCount <= 0) return 0;
		--entryCount;
		entryCount |= (entryCount >> 1);
		entryCount |= (entryCount >> 2);
		entryCount |= (entryCount >> 4);
		entryCount |= (entryCount >> 8);
		entryCount |= (entryCount >> 16);
		return entryCount & 536870911;
	}

	/** Diese Methode gibt den Streuwert der gegebenen Zahlenfolge zurück.
	 *
	 * @see IAMArray#hash()
	 * @param array Zahlenfolge.
	 * @return Streuwert.
	 * @throws NullPointerException Wenn {@code array} {@code null} ist. */
	public static int hash(final int[] array) throws NullPointerException {
		int hash = 0x811C9DC5;
		for (int i = 0, size = array.length; i < size; i++) {
			hash = (hash * 0x01000193) ^ array[i];
		}
		return hash;
	}

	/** Diese Methode gibt eine Zahl kleiner, gleich oder größer als {@code 0} zurück, wenn die Ordnung der ersten Zahlenfolge lexikografisch kleiner, gleich bzw.
	 * größer als die der zweiten Zahlenfolge ist.
	 *
	 * @see IAMArray#compare(IAMArray)
	 * @param array1 erste Zahlenfolge.
	 * @param array2 zweite Zahlenfolge.
	 * @return Vergleichswert der Ordnungen.
	 * @throws NullPointerException Wenn {@code array1} bzw. {@code array2} {@code null} ist. */
	public static int compare(final int[] array1, final int[] array2) throws NullPointerException {
		final int length1 = array1.length, length2 = array2.length;
		for (int i = 0, length = length1 < length2 ? length1 : length2, result; i < length; i++)
			if ((result = Comparators.compare(array1[i], array2[i])) != 0) return result;
		return length1 - length2;
	}

	/** Diese Methode gibt nur dann {@link #MODE_HASHED} zurück, wenn Einträge über den Streuwert ihrer Schlüssel gesucht werden. Wenn sie {@link #MODE_SORTED}
	 * liefert, werden Einträge binär über die Ordnung ihrer Schlüssel gesucht.
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
	public int key(final int entryIndex, final int index) {
		return this.key(entryIndex).get(index);
	}

	/** Diese Methode gibt die Länge der Zahlenfolge des Schlüssels des {@code entryIndex}-ten Eintrags zurück ({@code 0..1073741823}). Bei einem ungültigen
	 * {@code entryIndex} wird {@code 0} geliefert.
	 *
	 * @param entryIndex Index des Eintrags.
	 * @return Länge eines Schlüssel. */
	public int keyLength(final int entryIndex) {
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
	public int value(final int entryIndex, final int index) {
		return this.value(entryIndex).get(index);
	}

	/** Diese Methode gibt die Länge der Zahlenfolge des Werts des {@code entryIndex}-ten Eintrags zurück ({@code 0..1073741823}). Bei einem ungültigen
	 * {@code entryIndex} wird {@code 0} geliefert.
	 *
	 * @param entryIndex Index des Eintrags.
	 * @return Länge eines Werts. */
	public int valueLength(final int entryIndex) {
		return this.value(entryIndex).length();
	}

	/** Diese Methode gibt den {@code entryIndex}-ten Eintrag zurück. Bei einem ungültigen {@code entryIndex} wird ein leerer Eintrag geliefert.
	 *
	 * @see #entryCount()
	 * @param entryIndex Index des Eintrags.
	 * @return {@code entryIndex}-ter Eintrag. */
	public final IAMEntry entry(final int entryIndex) {
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

	/** Diese Methode ist eine Abkürzung für {@link #find(IAMArray) this.find(IAMArray.from(key))}.
	 *
	 * @see IAMArray#from(int...) */
	public final int find(final int... key) throws NullPointerException {
		return this.find(IAMArray.from(key));
	}

	/** Diese Methode gibt den Index des Eintrags zurück, dessen Schlüssel äquivalenten zum gegebenen Schlüssel ist. Die Suche erfolgt ordnungs- oder
	 * streuwertbasiert, d.h. indiziert. Bei erfolgloser Suche wird {@code -1} geliefert.
	 *
	 * @param key Schlüssel.
	 * @return Index des Entrags.
	 * @throws NullPointerException Wenn {@code key} {@code null} ist. */
	public abstract int find(final IAMArray key) throws NullPointerException;

	/** Diese Methode ist eine Ankürzung für {@code this.toBytes(Bytes.NATIVE_ORDER)}.
	 *
	 * @return Binärdatenformat {@code IAM_LISTING}. */
	public final byte[] toBytes() {
		return this.toBytes(Bytes.NATIVE_ORDER);
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
			keyArray[i] = this.key(i).toInts();
			valueArray[i] = this.value(i).toInts();
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

			rangeMask = IAMMapping.mask(entryCount);
			rangeCount = rangeMask + 2;
			rangeData = new int[rangeCount];
			rangeDataType = SizeStats.computeSizeType(entryCount);
			rangeDataBytes = rangeCount * IAMIndexLoader.byteCount(rangeDataType);
			rangeBytes = ((rangeDataBytes + 3) & -4) + 4;

			final int[] rangeIndex = new int[entryCount];
			for (int i = 0; i < entryCount; i++) {
				final int index = IAMMapping.hash(keyArray[i]) & rangeMask;
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
					return IAMMapping.compare(keyArray[index1.intValue()], keyArray[index2.intValue()]);
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

	/** Diese Methode kodiert dieses {@link IAMMapping} in eine für den Arbeitsspeicher optimierte Datenstruktur aus {@code int[]} und gibt diese zurück.
	 *
	 * @return optimiertes {@link IAMMapping}. */
	public IAMMapping toMapping() {
		if (this.entryCount() == 0) return IAMMapping.EMPTY;
		return new CompactMapping(this);
	}

	@Override
	public final Iterator<IAMEntry> iterator() {
		return this.entries().iterator();
	}

	@Override
	public String toString() {
		return Objects.toInvokeString(this, this.entryCount());
	}

}
