package bee.creative.iam;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import bee.creative.data.DataTarget;
import bee.creative.iam.IAMLoader.IAMIndexLoader;
import bee.creative.mmf.MMFArray;
import bee.creative.util.IO;

@SuppressWarnings ("javadoc")
class IAMCodec_BIN {

	/** Diese Klasse implementiert ein Objekt zur Ermittlung der Längen gegebener Zahlenlisten.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	static final class LengthStats {

		/** Dieses Feld speichert den Größentyp.<br>
		 * Der Wert {@code 0} legt fest, dass alle Zahlenfolgen die gleiche Länge {@link #dataLength} besitzen.<br>
		 * Die Werte {@code 1}, {@code 2} und {@code 2} legen fest, dass die kummulierten Längen der Zahlenlisten als {@code UINT8}, {@code UINT16} bzw.
		 * {@code UINT32} in {@link #dataOffset} gespeichert sind.
		 * 
		 * @see IAMCodec_BIN#_computeSizeType_(int) */
		public final int type;

		/** Dieses Feld speichert die Größe des Speicherbereichs für {@link #dataOffset}. */
		public final int bytes;

		/** Dieses Feld speichert die homogene Länge der Zahlenlisten. */
		public final int dataLength;

		/** Dieses Feld speichert die heterogenen Längen der Zahlenlisten. */
		public final int[] dataOffset;

		/** Dieser Konstruktor analysiert die gegebene Zahlenliste und initialisiert die Felder.
		 * 
		 * @param arrays Zahlenliste. */
		public LengthStats(final List<int[]> arrays) {
			final int count = arrays.size();
			this.dataOffset = new int[count + 1];
			int minLength = 1073741823, maxLength = 0;
			for (int i = 0, offset = 0; i < count;) {
				final int length = arrays.get(i++).length;
				offset += length;
				this.dataOffset[i] = offset;
				if (length > maxLength) {
					maxLength = length;
				}
				if (length < minLength) {
					minLength = length;
				}
			}
			if (minLength >= maxLength) {
				this.type = 0;
				this.dataLength = maxLength;
				this.bytes = 4;
			} else {
				this.type = IAMCodec_BIN._computeSizeType_(this.dataOffset[count]);
				this.dataLength = 0;
				final int dataOffsetBytes = this.dataOffset.length * IAMLoader._byteCount_(this.type);
				this.bytes = (dataOffsetBytes + 3) & -4;
			}

		}

		{}

		/** Diese Methode schreibt die {@link #dataLength} bzw. das {@link #dataOffset} gemäß {@link #type} in den gegebenen Puffer.
		 * 
		 * @param buffer Puffer. */
		public final void putSize(final ByteBuffer buffer) {
			if (this.type == 0) {
				buffer.putInt(this.dataLength);
			} else {
				IAMCodec_BIN._putContent_(buffer, this.type, this.dataOffset);
			}
		}

	}

	/** Diese Klasse implementiert ein Objekt zur Ermittlung der Kodierung gegebener Zahlenlisten.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	static final class ContentStats {

		/** Dieses Feld speichert den Datentyp.<br>
		 * Die Werte {@code 1}, {@code 2} und {@code 2} legen fest, dass die Werte der Zahlenlisten als {@code INT8}, {@code INT16} bzw. {@code INT32} in
		 * {@link #dataValue} gespeichert sind.
		 * 
		 * @see IAMCodec_BIN#_computeDataType_(int) */
		public final int type;

		/** Dieses Feld speichert die Größe des Speicherbereichs für {@link #dataValue}. */
		public final int bytes;

		/** Dieses Feld speichert die Längen gegebener Zahlenlisten. */
		public final LengthStats dataSize;

		/** Dieses Feld speichert die Werte der Zahlenlisten. */
		public final int[] dataValue;

		/** Dieser Konstruktor analysiert die gegebene Zahlenliste und initialisiert die Felder.
		 * 
		 * @param arrays Zahlenliste. */
		public ContentStats(final List<int[]> arrays) {
			this.dataSize = new LengthStats(arrays);
			final int count = arrays.size();
			this.dataValue = new int[this.dataSize.dataOffset[count]];
			int minValue = 0, maxValue = 0;
			for (int i = 0, offset = 0; i < count; i++) {
				final int[] item = arrays.get(i);
				for (int i2 = 0, length = item.length; i2 < length; i2++) {
					final int value = item[i2];
					if (value > maxValue) {
						maxValue = value;
					}
					if (value < minValue) {
						minValue = value;
					}
					this.dataValue[offset++] = value;
				}
			}
			this.type = Math.max(IAMCodec_BIN._computeDataType_(minValue), IAMCodec_BIN._computeDataType_(maxValue));
			final int dataValueBytes = this.dataValue.length * IAMLoader._byteCount_(this.type);
			this.bytes = (dataValueBytes + 3) & -4;
		}

		{}

		/** Diese Methode schreibt die {@link #dataValue} gemäß {@link #type} in den gegebenen Puffer.
		 * 
		 * @param buffer Puffer. */
		public final void putData(final ByteBuffer buffer) {
			IAMCodec_BIN._putContent_(buffer, this.type, this.dataValue);
		}

	}

	{}

	/** Diese Methode füht die Startpositionen der gegebenen Zahlenfolgen an den gegebenen {@link ByteBuffer} an.<br>
	 * Die Zahlenfolgen repräsentieren die von {@link #_encodeListing_(IAMListing, ByteOrder)} bzw. {@link #_encodeMapping_(IAMMapping, ByteOrder)} kodierten
	 * Datenstrukturen.
	 * 
	 * @param buffer {@link ByteBuffer}.
	 * @param source Zahlenfolgen. */
	static final void _putLength_(final ByteBuffer buffer, final byte[][] source) {
		int offset = 0;
		buffer.putInt(0);
		for (final byte[] data: source) {
			offset += data.length >> 2;
			buffer.putInt(offset);
		}
	}

	/** Diese Methode speichert die gegebene Zahlenfolge an den gegebenen {@link ByteBuffer} an.<br>
	 * Der geschriebene Speicherbereich wird mit Nullwerten ergänzt, um eine restlos durch vier teilbare Größe zu erreichen.
	 * 
	 * @see ByteBuffer#put(byte)
	 * @see ByteBuffer#putInt(int)
	 * @see ByteBuffer#putShort(short)
	 * @param buffer {@link ByteBuffer}.
	 * @param type Datentyp ({@code 1=INT8/UINT8}, {@code 2=INT16/UINT16}, {@code 3=INT32}).
	 * @param values Zahlenfolge. */
	static final void _putContent_(final ByteBuffer buffer, final int type, final int[] values) {
		switch (type) {
			case 1:
				for (int i = 0, length = values.length; i < length; i++) {
					buffer.put((byte)values[i]);
				}
				switch (values.length & 3) {
					case 1:
						buffer.put((byte)0);
					case 2:
						buffer.put((byte)0);
					case 3:
						buffer.put((byte)0);
				}
			break;
			case 2:
				for (int i = 0, length = values.length; i < length; i++) {
					buffer.putShort((short)values[i]);
				}
				switch (values.length & 1) {
					case 1:
						buffer.putShort((short)0);
				}
			break;
			case 3:
				for (int i = 0, length = values.length; i < length; i++) {
					buffer.putInt(values[i]);
				}
			break;
		}
	}

	final byte[] _encodeListing_(final IAMListing source, final ByteOrder order) {

		final int itemCount = source.itemCount();
		final int[][] itemArray = new int[itemCount][];
		for (int i = 0; i < itemCount; i++) {
			itemArray[i] = source.item(i).toArray();
		}

		final ContentStats itemData = new ContentStats(Arrays.asList(itemArray));
		final LengthStats itemSize = itemData.dataSize;

		final int length = 8 + itemSize.bytes + itemData.bytes;
		final byte[] result = new byte[length];

		final ByteBuffer buffer = ByteBuffer.wrap(result).order(order);
		buffer.putInt(0xF00D2000 | (itemData.type << 2) | (itemSize.type << 0));
		buffer.putInt(itemCount);
		itemSize.putSize(buffer);
		itemData.putData(buffer);

		return result;
	}

	final byte[] _encodeMapping_(final IAMMapping source, final ByteOrder order) {

		final int entryCount = source.entryCount();
		final int[][] keyArray = new int[entryCount][];
		final int[][] valueArray = new int[entryCount][];
		final Integer[] indexArray = new Integer[entryCount];
		for (int i = 0; i < entryCount; i++) {
			keyArray[i] = source.key(i).toArray();
			valueArray[i] = source.value(i).toArray();
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

		if (source.mode() == IAMMapping.MODE_HASHED) {

			rangeMask = IAMCodec_BIN._computeRangeMask_(entryCount);
			rangeCount = rangeMask + 2;
			rangeData = new int[rangeCount];
			rangeDataType = IAMCodec_BIN._computeSizeType_(entryCount);
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
				public int compare(final Integer o1, final Integer o2) {
					return rangeIndex[o1.intValue()] - rangeIndex[o2.intValue()];
				}

			});

		} else {

			Arrays.sort(indexArray, new Comparator<Integer>() {

				@Override
				public int compare(final Integer o1, final Integer o2) {
					return IAMBuilder.compare(keyArray[o1.intValue()], keyArray[o2.intValue()]);
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

		final ContentStats keyData = new ContentStats(Arrays.asList(keyArray));
		final LengthStats keySize = keyData.dataSize;

		final ContentStats valueData = new ContentStats(Arrays.asList(valueArray));
		final LengthStats valueSize = valueData.dataSize;

		final int length = 8 + rangeBytes + keySize.bytes + keyData.bytes + valueSize.bytes + valueData.bytes;
		final byte[] result = new byte[length];

		final ByteBuffer buffer = ByteBuffer.wrap(result).order(order);
		buffer.putInt(0xF00D1000 | (keyData.type << 8) | (keySize.type << 6) | (rangeDataType << 4) | (valueData.type << 2) | (valueSize.type << 0));
		buffer.putInt(entryCount);
		if (rangeDataType != 0) {
			buffer.putInt(rangeMask);
			IAMCodec_BIN._putContent_(buffer, rangeDataType, rangeData);
		}
		keySize.putSize(buffer);
		keyData.putData(buffer);
		valueSize.putSize(buffer);
		valueData.putData(buffer);

		return result;
	}

	/** Diese Methode gibt den Datentyp für die gegebene Größe zurück.<br>
	 * Die Datentypen {@code 1}, {@code 2} und {@code 3} stehen für {@code UINT8}, {@code UINT16} und {@code UINT32}.
	 * 
	 * @param value Größe.
	 * @return Datentyp ({@code 1..3}). */
	static final int _computeSizeType_(final int value) {
		if (value <= 255) return 1;
		if (value <= 65535) return 2;
		return 3;
	}

	/** Diese Methode gibt den Datentyp für den gegebenen Wert zurück.<br>
	 * Die Datentypen {@code 1}, {@code 2} und {@code 3} stehen für {@code INT8}, {@code INT16} und {@code INT32}.
	 * 
	 * @param value Wert.
	 * @return Datengrößentyps ({@code 1..3}). */
	static final int _computeDataType_(final int value) {
		if ((-128 <= value) && (value <= 127)) return 1;
		if ((-32768 <= value) && (value <= 32767)) return 2;
		return 3;
	}

	/** Diese Methode gibt die Bitmaske zur Umrechnung von Streuwerten zurück.
	 * 
	 * @param entryCount Anzahl der Einträge der Abbildung.
	 * @return Bitmaske. */
	static final int _computeRangeMask_(final int entryCount) {
		int result = 2;
		while (result < entryCount) {
			result <<= 1;
		}
		return (result - 1) & 536870911;
	}

	public final IAMIndex decode(final IAMCodec codec) throws IOException, IllegalStateException, IllegalArgumentException {
		final MMFArray array = MMFArray.from(codec.getSourceData());
		final ByteOrder _order_ = IAMIndexLoader.HEADER.orderOf(array);
		codec.useByteOrder(_order_);
		return new IAMIndexLoader(array.withOrder(_order_));
	}

	public final void encode(final IAMCodec codec, final IAMIndex source) throws IOException, IllegalStateException, IllegalArgumentException {
		try (DataTarget target = IO.outputDataFrom(codec.getTargetData())) {

			final ByteOrder order = codec.getByteOrder();

			final int mappingCount = source.mappingCount();
			final byte[][] mappingBytes = new byte[mappingCount][];
			for (int i = 0; i < mappingCount; i++) {
				mappingBytes[i] = this._encodeMapping_(source.mapping(i), order);
			}

			final int listingCount = source.listingCount();
			final byte[][] listingBytes = new byte[listingCount][];
			for (int i = 0; i < listingCount; i++) {
				listingBytes[i] = this._encodeListing_(source.listing(i), order);
			}

			int length = 12;
			length += (mappingCount + listingCount + 2) << 2;
			final byte[] result = new byte[length];

			final ByteBuffer buffer = ByteBuffer.wrap(result).order(order);
			buffer.putInt(0xF00DBA5E);
			buffer.putInt(mappingCount);
			buffer.putInt(listingCount);
			IAMCodec_BIN._putLength_(buffer, mappingBytes);
			IAMCodec_BIN._putLength_(buffer, listingBytes);

			target.write(result);

			for (final byte[] data: mappingBytes) {
				target.write(data);
			}
			for (final byte[] data: listingBytes) {
				target.write(data);
			}

		}
	}

}
