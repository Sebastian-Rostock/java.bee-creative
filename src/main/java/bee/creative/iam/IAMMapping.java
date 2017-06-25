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
			Objects.assertNotNull(key);
			return -1;
		}

		@Override
		public final IAMMapping toMapping() {
			return this;
		}

	}

	@SuppressWarnings ("javadoc")
	static final class CompactMapping extends IAMMapping {

		final int[] keyData;

		final int[] keyOffset;

		final int keyLength;

		final int[] valueData;

		final int[] valueOffset;

		final int valueLength;

		final int rangeMask;

		final int[] rangeOffset;

		final int entryCount;

		public CompactMapping(final IAMMapping that) {
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
				that.key(index).toArray(keyData, keyOffset[i]);
				that.value(index).toArray(valueData, valueOffset[i]);
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
		public final boolean mode() {
			return this.rangeMask != 0;
		}

		@Override
		public final int find(final IAMArray key) throws NullPointerException {
			Objects.assertNotNull(key);
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
		public final IAMArray key(final int entryIndex) {
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
		public final int key(final int entryIndex, final int index) {
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
		public final int keyLength(final int entryIndex) {
			if ((entryIndex < 0) || (entryIndex >= this.entryCount)) return 0;
			if (this.keyOffset == null) return this.keyLength;
			return this.keyOffset[entryIndex + 1] - this.keyOffset[entryIndex];
		}

		@Override
		public final IAMArray value(final int entryIndex) {
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
		public final int value(final int entryIndex, final int index) {
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
		public final int valueLength(final int entryIndex) {
			if ((entryIndex < 0) || (entryIndex >= this.entryCount)) return 0;
			if (this.valueOffset == null) return this.valueLength;
			return this.valueOffset[entryIndex + 1] - this.valueOffset[entryIndex];
		}

		@Override
		public final int entryCount() {
			return this.entryCount;
		}

		@Override
		public final IAMMapping toMapping() {
			return this;
		}

	}

	@SuppressWarnings ("javadoc")
	static final class Entries extends AbstractList<IAMEntry> {

		final IAMMapping owner;

		Entries(final IAMMapping owner) {
			this.owner = owner;
		}

		{}

		@Override
		public final IAMEntry get(final int index) {
			if ((index < 0) || (index >= this.owner.entryCount())) throw new IndexOutOfBoundsException();
			return this.owner.entry(index);
		}

		@Override
		public final int size() {
			return this.owner.entryCount();
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

	/** Diese Methode erzeugt aus dem gegebenen Objekt ein {@link IAMMapping} und gibt dieses zurück.<br>
	 * Wenn das Objekt ein {@link IAMMapping} ist, wird dieses geliefert. Andernfalls wird {@code new IAMMappingLoader(MMFArray.from(object).withOrder(...))}
	 * geliefert, wobei die Bytereihenfolge über {@link IAMMappingLoader#HEADER} ermittelt wird.
	 *
	 * @see MMFArray#from(Object)
	 * @see IAMMappingLoader#IAMMappingLoader(MMFArray)
	 * @param object Objekt.
	 * @return {@link IAMMapping}.
	 * @throws IOException Wenn {@link MMFArray#from(Object)} eine entsprechende Ausnahme auslöst.
	 * @throws IAMException Wenn {@link IAMMappingLoader#IAMMappingLoader(MMFArray)} eine entsprechende Ausnahme auslöst. */
	public static IAMMapping from(final Object object) throws IOException, IAMException {
		if (object instanceof IAMMapping) return (IAMMapping)object;
		final MMFArray array = MMFArray.from(object);
		return new IAMMappingLoader(array.withOrder(IAMMappingLoader.HEADER.orderOf(array)));
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

	/** Diese Methode ist eine Abkürzung für {@code this.find(IAMArray.from(key))}.
	 *
	 * @see #find(IAMArray)
	 * @see IAMArray#from(int...) */
	@SuppressWarnings ("javadoc")
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

			rangeMask = IAMMapping.mask(entryCount);
			rangeCount = rangeMask + 2;
			rangeData = new int[rangeCount];
			rangeDataType = SizeStats.computeSizeType(entryCount);
			rangeDataBytes = rangeCount * IAMLoader.byteCount(rangeDataType);
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

	/** Diese Methode kodiert dieses {@link IAMMapping} in eine für den Arbeitsspeicher optimierte Datenstruktur aus {@code int[]} und gibt diese zurück.
	 *
	 * @return optimiertes {@link IAMMapping}. */
	public IAMMapping toMapping() {
		if (this.entryCount() == 0) return IAMMapping.EMPTY;
		return new CompactMapping(this);
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
		return Objects.toInvokeString(this, this.entryCount());
	}

}
