package bee.creative.iam;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel.MapMode;
import bee.creative.iam.IAM.IAMBaseArray;
import bee.creative.iam.IAM.IAMBaseIndex;
import bee.creative.iam.IAM.IAMBaseList;
import bee.creative.iam.IAM.IAMBaseMap;
import bee.creative.iam.IAM.IAMValueArray;

/**
 * Diese Klasse implementiert die Klassen und Methoden zur Dekodierung der {@link IAM} Datenstrukturen.
 * 
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class IAMDecoder {

	/**
	 * Diese Klasse implementiert ein abstraktes {@link IAMArray}, dessen Zahlen durch einen {@link ByteBuffer} gegeben sind.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static abstract class IAMBufferArray extends IAMBaseArray {

		/**
		 * Dieses Feld speichert den Speicherbereich.
		 */
		protected final ByteBuffer byteBuffer;

		/**
		 * Dieses Feld speichert die Länge.
		 */
		protected final int byteLength;

		/**
		 * Dieses Feld speichert die Startposition.
		 */
		protected final int byteOffset;

		/**
		 * Dieser Konstruktor initialisiert den Speicherbereich.
		 * 
		 * @param buffer Speicherbereich.
		 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
		 */
		public IAMBufferArray(final ByteBuffer buffer) throws NullPointerException {
			this(buffer, 0, Math.min(buffer.limit(), 1073741823));
		}

		@SuppressWarnings ("javadoc")
		protected IAMBufferArray(final ByteBuffer byteBuffer, final int byteOffset, final int byteLength) throws NullPointerException, IllegalArgumentException {
			this.byteBuffer = byteBuffer;
			this.byteOffset = byteOffset;
			this.byteLength = byteLength;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected abstract IAMBufferArray newSection(int offset, int length);

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IAMBufferArray section(final int offset, final int length) {
			return (IAMBufferArray)super.section(offset, length);
		}

		/**
		 * Diese Methode gibt den Speicherbereich dieser Zahlenfolge als Folge von {@code INT8}-Zahlen interpretiert zurück.
		 * 
		 * @return {@link IAMBufferINT8Array}
		 */
		public final IAMBufferINT8Array toINT8() {
			return new IAMBufferINT8Array(this.byteBuffer, this.byteOffset, this.byteLength);
		}

		/**
		 * Diese Methode gibt den Speicherbereich dieser Zahlenfolge als Folge von {@code INT16}-Zahlen interpretiert zurück.
		 * 
		 * @return {@link IAMBufferINT16Array}
		 */
		public final IAMBufferINT16Array toINT16() {
			return new IAMBufferINT16Array(this.byteBuffer, this.byteOffset, this.byteLength);
		}

		/**
		 * Diese Methode gibt den Speicherbereich dieser Zahlenfolge als Folge von {@code INT32}-Zahlen interpretiert zurück.
		 * 
		 * @return {@link IAMBufferINT32Array}
		 */
		public final IAMBufferINT32Array toINT32() {
			return new IAMBufferINT32Array(this.byteBuffer, this.byteOffset, this.byteLength);
		}

		/**
		 * Diese Methode gibt den Speicherbereich dieser Zahlenfolge als Folge von {@code UINT8}-Zahlen interpretiert zurück.
		 * 
		 * @return {@link IAMBufferUINT8Array}
		 */
		public final IAMBufferUINT8Array toUINT8() {
			return new IAMBufferUINT8Array(this.byteBuffer, this.byteOffset, this.byteLength);
		}

		/**
		 * Diese Methode gibt den Speicherbereich dieser Zahlenfolge als Folge von {@code UINT16}-Zahlen interpretiert zurück.
		 * 
		 * @return {@link IAMBufferUINT16Array}
		 */
		public final IAMBufferUINT16Array toUINT16() {
			return new IAMBufferUINT16Array(this.byteBuffer, this.byteOffset, this.byteLength);
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link IAMArray}, welches einen gegebenen Speicherbereich als Folge von {@code INT8/byte} Zahlen interpretiert.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class IAMBufferINT8Array extends IAMBufferArray {

		/**
		 * Dieser Konstruktor initialisiert den Speicherbereich.
		 * 
		 * @param buffer Speicherbereich.
		 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
		 */
		public IAMBufferINT8Array(final ByteBuffer buffer) throws NullPointerException {
			super(buffer);
		}

		@SuppressWarnings ("javadoc")
		protected IAMBufferINT8Array(final ByteBuffer byteBuffer, final int byteOffset, final int byteLength) throws NullPointerException, IllegalArgumentException {
			super(byteBuffer, byteOffset, byteLength);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected IAMBufferArray newSection(final int offset, final int length) {
			return new IAMBufferINT8Array(this.byteBuffer, this.byteOffset + offset, length);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int get(final int index) {
			if ((index < 0) || (index >= this.byteLength)) return 0;
			return this.byteBuffer.get(this.byteOffset + index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int length() {
			return this.byteLength;
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link IAMArray}, welches einen gegebenen Speicherbereich als Folge von {@code INT16/short} Zahlen interpretiert.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class IAMBufferINT16Array extends IAMBufferArray {

		/**
		 * Dieser Konstruktor initialisiert den Speicherbereich.
		 * 
		 * @param buffer Speicherbereich.
		 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
		 */
		public IAMBufferINT16Array(final ByteBuffer buffer) {
			super(buffer);
		}

		@SuppressWarnings ("javadoc")
		protected IAMBufferINT16Array(final ByteBuffer byteBuffer, final int byteOffset, final int byteLength) throws NullPointerException,
			IllegalArgumentException {
			super(byteBuffer, byteOffset, byteLength);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected IAMBufferArray newSection(final int offset, final int length) {
			return new IAMBufferINT16Array(this.byteBuffer, this.byteOffset + (offset << 1), length << 1);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int get(int index) {
			index <<= 1;
			if ((index < 0) || (index >= this.byteLength)) return 0;
			return this.byteBuffer.getShort(this.byteOffset + index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int length() {
			return this.byteLength >> 1;
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link IAMArray}, welches einen gegebenen Speicherbereich als Folge von {@code INT32/int} Zahlen interpretiert.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class IAMBufferINT32Array extends IAMBufferArray {

		/**
		 * Dieser Konstruktor initialisiert den Speicherbereich.
		 * 
		 * @param buffer Speicherbereich.
		 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
		 */
		public IAMBufferINT32Array(final ByteBuffer buffer) throws NullPointerException {
			super(buffer);
		}

		@SuppressWarnings ("javadoc")
		protected IAMBufferINT32Array(final ByteBuffer byteBuffer, final int byteOffset, final int byteLength) throws NullPointerException,
			IllegalArgumentException {
			super(byteBuffer, byteOffset, byteLength);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected IAMBufferArray newSection(final int offset, final int length) {
			return new IAMBufferINT32Array(this.byteBuffer, this.byteOffset + (offset << 2), length << 2);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int get(int index) {
			index <<= 2;
			if ((index < 0) || (index >= this.byteLength)) return 0;
			return this.byteBuffer.getInt(this.byteOffset + index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int length() {
			return this.byteLength >> 2;
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link IAMArray}, welches einen gegebenen Speicherbereich als Folge von {@code UINT8} Zahlen interpretiert.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class IAMBufferUINT8Array extends IAMBufferINT8Array {

		/**
		 * Dieser Konstruktor initialisiert den Speicherbereich.
		 * 
		 * @param buffer Speicherbereich.
		 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
		 */
		public IAMBufferUINT8Array(final ByteBuffer buffer) throws NullPointerException {
			super(buffer);
		}

		@SuppressWarnings ("javadoc")
		protected IAMBufferUINT8Array(final ByteBuffer byteBuffer, final int byteOffset, final int byteLength) throws NullPointerException,
			IllegalArgumentException {
			super(byteBuffer, byteOffset, byteLength);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected IAMBufferArray newSection(final int offset, final int length) {
			return new IAMBufferUINT8Array(this.byteBuffer, this.byteOffset + offset, length);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int get(final int index) {
			return super.get(index) & 255;
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link IAMArray}, welches einen gegebenen Speicherbereich als Folge von {@code INT16/short} Zahlen interpretiert.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class IAMBufferUINT16Array extends IAMBufferINT16Array {

		/**
		 * Dieser Konstruktor initialisiert den Speicherbereich.
		 * 
		 * @param buffer Speicherbereich.
		 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
		 */
		public IAMBufferUINT16Array(final ByteBuffer buffer) {
			super(buffer);
		}

		@SuppressWarnings ("javadoc")
		protected IAMBufferUINT16Array(final ByteBuffer byteBuffer, final int byteOffset, final int byteLength) throws NullPointerException,
			IllegalArgumentException {
			super(byteBuffer, byteOffset, byteLength);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected IAMBufferArray newSection(final int offset, final int length) {
			return new IAMBufferUINT16Array(this.byteBuffer, this.byteOffset + (offset << 1), length << 1);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int get(final int index) {
			return super.get(index) & 65535;
		}

	}

	{}

	/**
	 * Diese Klasse implementiert eine {@link IAMMap}, die ihre Daten aus einem {@link IAMBufferINT32Array} dekodiert.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class IAMMapDecoder extends IAMBaseMap {

		/**
		 * Dieses Feld speichert die Zahlen der Schlüssel.
		 */
		protected final IAMBufferArray keyData;

		/**
		 * Dieses Feld speichert die Startpositionen der Schlüssel.
		 */
		protected final IAMBufferArray keyOffset;

		/**
		 * Dieses Feld speichert die Länge der Schlüssel.
		 */
		protected final int keyLength;

		/**
		 * Dieses Feld speichert die Zahlen der Werte.
		 */
		protected final IAMBufferArray valueData;

		/**
		 * Dieses Feld speichert die Startpositionen der Werte.
		 */
		protected final IAMBufferArray valueOffset;

		/**
		 * Dieses Feld speichert die Länge der Werte.
		 */
		protected final int valueLength;

		/**
		 * Dieses Feld speichert die Bitmaske der Schlüsselbereiche.
		 */
		protected final int rangeMask;

		/**
		 * Dieses Feld speichert die Startpositionen der Schlüsselbereiche.
		 */
		protected final IAMBufferArray rangeOffset;

		/**
		 * Dieses Feld speichert die Anzahl der Einträge.
		 */
		protected final int entryCount;

		/**
		 * Dieser Konstruktor initialisiert die leere Abbildung.
		 */
		protected IAMMapDecoder() {
			this.keyData = null;
			this.keyOffset = null;
			this.keyLength = 0;
			this.valueData = null;
			this.valueOffset = null;
			this.valueLength = 0;
			this.rangeMask = 0;
			this.rangeOffset = null;
			this.entryCount = 0;
		}

		/**
		 * Dieser Kontrukteur initialisiert diese {@link IAMMap} als Sicht auf den gegebenen Speicherbereich.
		 * 
		 * @param array Speicherbereich mit {@code INT32} Zahlen.
		 * @throws IAMException Wenn beim dekodieren des Speicherbereichs ein Fehler erkannt wird.
		 */
		public IAMMapDecoder(final IAMBufferINT32Array array) throws IAMException {
			if ((array == null) || (array.length() < 4)) throw new IAMException(IAMException.INVALID_LENGTH);

			int offset = 0;
			final int header = array.get(offset);
			offset++;
			if ((header & 0xFFFFFC00) != 0xF00D1000) throw new IAMException(IAMException.INVALID_HEADER);

			final int keyDataType = (header >> 8) & 3;
			final int keySizeType = (header >> 6) & 3;
			final int rangeSizeType = (header >> 4) & 3;
			final int valueDataType = (header >> 2) & 3;
			final int valueSizeType = (header >> 0) & 3;
			if ((keyDataType == 0) || (valueDataType == 0)) throw new IAMException(IAMException.INVALID_HEADER);

			final int entryCount = array.get(offset);
			offset++;
			if ((entryCount < 0) || (entryCount > 0x3FFFFFFF)) throw new IAMException(IAMException.INVALID_VALUE);

			int rangeValue;
			final int rangeMask;
			final IAMBufferArray rangeOffset;
			if (rangeSizeType != 0) {

				if (array.length() <= offset) throw new IAMException(IAMException.INVALID_LENGTH);

				rangeMask = array.get(offset);
				offset++;
				if ((rangeMask < 1) || (rangeMask > 0x1FFFFFFF) || (((rangeMask + 1) & rangeMask) != 0)) throw new IAMException(IAMException.INVALID_VALUE);

				rangeValue = IAM.byteAlign((rangeMask + 2) * IAM.byteCount(rangeSizeType));
				rangeOffset = IAMDecoder.sizeArray(array.section(offset, rangeValue), rangeSizeType).section(0, rangeMask + 2);
				offset += rangeValue;
				if (array.length() <= offset) throw new IAMException(IAMException.INVALID_LENGTH);

				IAMDecoder.checkArray(rangeOffset);
				rangeValue = rangeOffset.get(rangeMask + 1);
				if (rangeValue != entryCount) throw new IAMException(IAMException.INVALID_OFFSET);

			} else {

				rangeMask = 0;
				rangeOffset = null;

			}

			if (array.length() <= offset) throw new IAMException(IAMException.INVALID_LENGTH);

			int keyValue;
			final int keyLength;
			final IAMBufferArray keyOffset;
			if (keySizeType != 0) {

				keyValue = IAM.byteAlign((entryCount + 1) * IAM.byteCount(keySizeType));
				keyLength = 0;
				keyOffset = IAMDecoder.sizeArray(array.section(offset, keyValue), keySizeType).section(0, entryCount + 1);
				offset += keyValue;
				if (array.length() <= offset) throw new IAMException(IAMException.INVALID_LENGTH);

				IAMDecoder.checkArray(keyOffset);
				keyValue = keyOffset.get(entryCount);

			} else {

				keyValue = array.get(offset);
				offset++;
				if (array.length() <= offset) throw new IAMException(IAMException.INVALID_LENGTH);

				keyLength = keyValue;
				keyValue = entryCount * keyValue;
				keyOffset = null;

			}
			if ((keyValue < 0) || (keyValue > 0x3FFFFFFF)) throw new IAMException(IAMException.INVALID_VALUE);

			keyValue = IAM.byteAlign(keyValue * IAM.byteCount(keyDataType));
			final IAMBufferArray keyData = IAMDecoder.dataArray(array.section(offset, keyValue), keyDataType);
			offset += keyValue;
			if (array.length() <= offset) throw new IAMException(IAMException.INVALID_LENGTH);

			int valueValue;
			final int valueLength;
			final IAMBufferArray valueOffset;
			if (valueSizeType != 0) {

				valueValue = IAM.byteAlign((entryCount + 1) * IAM.byteCount(valueSizeType));
				valueLength = 0;
				valueOffset = IAMDecoder.sizeArray(array.section(offset, valueValue), valueSizeType).section(0, entryCount + 1);
				offset += valueValue;
				if (array.length() < offset) throw new IAMException(IAMException.INVALID_LENGTH);

				IAMDecoder.checkArray(valueOffset);
				valueValue = valueOffset.get(entryCount);

			} else {

				valueValue = array.get(offset);
				offset++;
				if (array.length() < offset) throw new IAMException(IAMException.INVALID_LENGTH);

				valueLength = valueValue;
				valueValue = entryCount * valueValue;
				valueOffset = null;

			}
			if ((valueValue < 0) || (valueValue > 0x3FFFFFFF)) throw new IAMException(IAMException.INVALID_VALUE);

			valueValue = IAM.byteAlign(valueValue * IAM.byteCount(valueDataType));
			final IAMBufferArray valueData = IAMDecoder.dataArray(array.section(offset, valueValue), valueDataType);
			offset += valueValue;
			if (array.length() != offset) throw new IAMException(IAMException.INVALID_LENGTH);

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

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IAMBufferArray key(final int entryIndex) {
			if ((entryIndex < 0) || (entryIndex >= this.entryCount)) return EMPTY_ARRAY;
			final IAMArray keyOffset = this.keyOffset;
			if (keyOffset != null) {
				final int offset = keyOffset.get(entryIndex);
				final int length = keyOffset.get(entryIndex + 1) - offset;
				return this.keyData.section(offset, length);
			} else {
				final int length = this.keyLength;
				final int offset = length * entryIndex;
				return this.keyData.section(offset, length);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IAMBufferArray value(final int entryIndex) {
			if ((entryIndex < 0) || (entryIndex >= this.entryCount)) return EMPTY_ARRAY;
			final IAMArray keyOffset = this.valueOffset;
			if (keyOffset != null) {
				final int offset = keyOffset.get(entryIndex);
				final int length = keyOffset.get(entryIndex + 1) - offset;
				return this.valueData.section(offset, length);
			} else {
				final int length = this.valueLength;
				final int offset = length * entryIndex;
				return this.valueData.section(offset, length);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int entryCount() {
			return this.entryCount;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int find(final int[] key) {
			final IAMArray array = new IAMValueArray(key);
			int i = this.rangeMask;
			if (i != 0) {
				final IAMArray range = this.rangeOffset;
				i = array.hash() & i;
				for (int l = range.get(i), r = range.get(i + 1); l < r; l++) {
					if (this.key(l).equals(array)) return l;
				}
			} else {
				int l = 0, r = this.entryCount;
				while (l < r) {
					i = (l + r) >> 1;
					i = array.compare(this.key(i));
					if (i < 0) {
						r = i;
					} else if (i > 0) {
						l = i + 1;
					} else return i;
				}
			}
			return -1;
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link IAMList}, die ihre Daten aus einem {@link IAMBufferINT32Array} dekodiert.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class IAMListDecoder extends IAMBaseList {

		/**
		 * Dieses Feld speichert die Zahlen der Elemente.
		 */
		protected final IAMBufferArray itemData;

		/**
		 * Dieses Feld speichert die Startpositionen der Elemente.
		 */
		protected final IAMBufferArray itemOffset;

		/**
		 * Dieses Feld speichert die Länge der Elemente.
		 */
		protected final int itemLength;

		/**
		 * Dieses Feld speichert die Anzahl der Elemente.
		 */
		protected final int itemCount;

		/**
		 * Dieser Konstruktor initialisiert die leere Liste.
		 */
		protected IAMListDecoder() {
			this.itemData = null;
			this.itemOffset = null;
			this.itemLength = 0;
			this.itemCount = 0;
		}

		/**
		 * Dieser Kontrukteur initialisiert diese {@link IAMList} als Sicht auf den gegebenen Speicherbereich.
		 * 
		 * @param array Speicherbereich mit {@code INT32} Zahlen.
		 * @throws IAMException Wenn beim dekodieren des Speicherbereichs ein Fehler erkannt wird.
		 */
		public IAMListDecoder(final IAMBufferINT32Array array) throws IAMException {
			if ((array == null) || (array.length() < 3)) throw new IAMException(IAMException.INVALID_LENGTH);

			int offset = 0;
			final int header = array.get(offset);
			offset++;
			if ((header & 0xFFFFFFF0) != 0xF00D2000) throw new IAMException(IAMException.INVALID_HEADER);

			final int itemDataType = (header >> 2) & 3;
			final int itemSizeType = (header >> 0) & 3;
			if (itemDataType == 0) throw new IAMException(IAMException.INVALID_HEADER);

			final int itemCount = array.get(offset);
			offset++;
			if ((itemCount < 0) || (itemCount > 0x3FFFFFFF)) throw new IAMException(IAMException.INVALID_VALUE);

			int itemValue;
			final int itemLength;
			final IAMBufferArray itemOffset;
			if (itemSizeType != 0) {

				itemValue = IAM.byteAlign((itemCount + 1) * IAM.byteCount(itemSizeType));
				itemLength = 0;
				itemOffset = IAMDecoder.sizeArray(array.section(offset, itemValue), itemSizeType).section(0, itemCount + 1);
				offset += itemValue;
				if (array.length() < offset) throw new IAMException(IAMException.INVALID_LENGTH);

				IAMDecoder.checkArray(itemOffset);
				itemValue = itemOffset.get(itemCount);

			} else {

				itemValue = array.get(offset);
				offset++;
				if ((itemValue < 0) || (itemValue > 0x3FFFFFFF)) throw new IAMException(IAMException.INVALID_VALUE);

				itemLength = itemValue;
				itemValue = itemCount * itemValue;
				itemOffset = null;

			}

			itemValue = IAM.byteAlign(itemValue * IAM.byteCount(itemDataType));
			final IAMBufferArray itemData = IAMDecoder.dataArray(array.section(offset, itemValue), itemDataType);
			offset += itemValue;
			if (array.length() != offset) throw new IAMException(IAMException.INVALID_LENGTH);

			this.itemData = itemData;
			this.itemOffset = itemOffset;
			this.itemLength = itemLength;
			this.itemCount = itemCount;

		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IAMBufferArray item(final int itemIndex) {
			if ((itemIndex < 0) || (itemIndex >= this.itemCount)) return IAMDecoder.EMPTY_ARRAY;
			final IAMArray itemOffset = this.itemOffset;
			if (itemOffset != null) {
				final int offset = itemOffset.get(itemIndex);
				final int length = itemOffset.get(itemIndex + 1) - offset;
				return this.itemData.section(offset, length);
			} else {
				final int length = this.itemLength;
				final int offset = length * itemIndex;
				return this.itemData.section(offset, length);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int itemCount() {
			return this.itemCount;
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link IAMIndex}, der seine Daten aus einem {@link IAMBufferINT32Array} dekodiert.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class IAMIndexDecoder extends IAMBaseIndex {

		/**
		 * Dieses Feld speichert die Abbildungen.
		 */
		protected final IAMMapDecoder[] maps;

		/**
		 * Dieses Feld speichert die Listen.
		 */
		protected final IAMListDecoder[] lists;

		/**
		 * Dieser Kontrukteur initialisiert diesen {@link IAMIndex} als Sicht auf den gegebenen Speicherbereich.
		 * 
		 * @param array Speicherbereich mit {@code INT32} Zahlen.
		 * @throws IAMException Wenn beim dekodieren des Speicherbereichs ein Fehler erkannt wird.
		 */
		public IAMIndexDecoder(final IAMBufferINT32Array array) throws IAMException {
			if ((array == null) || (array.length() < 5)) throw new IAMException(IAMException.INVALID_LENGTH);

			int offset = 0;
			final int header = array.get(offset);
			offset++;
			if (header != 0xF00DBA5E) throw new IAMException(IAMException.INVALID_HEADER);

			final int mapCount = array.get(offset);
			offset++;
			if ((mapCount < 0) || (mapCount > 0x3FFFFFFF)) throw new IAMException(IAMException.INVALID_VALUE);

			final int listCount = array.get(offset);
			offset++;
			if ((listCount < 0) || (listCount > 0x3FFFFFFF)) throw new IAMException(IAMException.INVALID_VALUE);

			final IAMArray mapOffset = array.section(offset, mapCount + 1);
			offset += mapCount + 1;
			if (array.length() < offset) throw new IAMException(IAMException.INVALID_LENGTH);

			final int mapDataLength = mapOffset.get(mapCount);
			if ((mapDataLength < 0) || (mapDataLength > 0x3FFFFFFF)) throw new IAMException(IAMException.INVALID_VALUE);

			final IAMArray listOffset = array.section(offset, listCount + 1);
			offset += listCount + 1;
			if (array.length() < offset) throw new IAMException(IAMException.INVALID_LENGTH);

			final int listDataLength = listOffset.get(listCount);
			if ((listDataLength < 0) || (listDataLength > 0x3FFFFFFF)) throw new IAMException(IAMException.INVALID_VALUE);

			final IAMBufferINT32Array mapData = array.section(offset, mapDataLength).toINT32();
			offset += mapDataLength;
			if (array.length() < offset) throw new IAMException(IAMException.INVALID_LENGTH);

			final IAMBufferINT32Array listData = array.section(offset, listDataLength).toINT32();
			offset += listDataLength;
			if (array.length() != offset) throw new IAMException(IAMException.INVALID_LENGTH);

			this.maps = new IAMMapDecoder[mapCount];
			for (int i = 0; i < mapCount; i++) {
				final int offset2 = mapOffset.get(i);
				final int length2 = mapOffset.get(i + 1) - offset2;
				this.maps[i] = new IAMMapDecoder(mapData.section(offset2, length2).toINT32());
			}

			this.lists = new IAMListDecoder[listCount];
			for (int i = 0; i < listCount; i++) {
				final int offset2 = listOffset.get(i);
				final int length2 = listOffset.get(i + 1) - offset2;
				this.lists[i] = new IAMListDecoder(listData.section(offset2, length2).toINT32());
			}

		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IAMMapDecoder map(final int index) {
			if ((index < 0) || (index >= this.maps.length)) return EMPTY_MAP;
			return this.maps[index];
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int mapCount() {
			return this.maps.length;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IAMListDecoder list(final int index) {
			if ((index < 0) || (index >= this.lists.length)) return EMPTY_LIST;
			return this.lists[index];
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int listCount() {
			return this.lists.length;
		}

	}

	{}

	/**
	 * Dieses Feld speichert den leeren {@link IAMMapDecoder}.
	 */
	public static final IAMMapDecoder EMPTY_MAP = //
		new IAMMapDecoder();

	/**
	 * Dieses Feld speichert den leeren {@link IAMListDecoder}.
	 */
	public static final IAMListDecoder EMPTY_LIST = //
		new IAMListDecoder();

	/**
	 * Dieses Feld speichert das leere IAMBufferArray.
	 */
	public static final IAMBufferArray EMPTY_ARRAY = //
		new IAMBufferINT8Array(ByteBuffer.wrap(new byte[0]));

	{}

	/**
	 * Diese Methode gibt den Speicherbereich der gegebenen Zahlenfolge als {@link IAMBufferUINT8Array}, {@link IAMBufferUINT16Array} bzw.
	 * {@link IAMBufferINT32Array} zurück.
	 * 
	 * @see IAMBufferArray#toUINT8()
	 * @see IAMBufferArray#toUINT16()
	 * @see IAMBufferArray#toINT32()
	 * @param array Zahlenfolge.
	 * @param sizeType Größentyp ({@code 1..3}).
	 * @return Folge von {@code UINT8}, {@code UINT16} bzw. {@code INT32} Zahlen.
	 * @throws IllegalArgumentException Wenn der gegebene Größentyp ungültig ist.
	 */
	protected static IAMBufferArray sizeArray(final IAMBufferArray array, final int sizeType) throws IllegalArgumentException {
		switch (sizeType) {
			case 1:
				return array.toUINT8();
			case 2:
				return array.toUINT16();
			case 3:
				return array.toINT32();
		}
		throw new IllegalArgumentException();
	}

	/**
	 * Diese Methode gibt den Speicherbereich der gegebenen Zahlenfolge als {@link IAMBufferINT8Array}, {@link IAMBufferINT16Array} bzw.
	 * {@link IAMBufferINT32Array} zurück.
	 * 
	 * @see IAMBufferArray#toINT8()
	 * @see IAMBufferArray#toINT16()
	 * @see IAMBufferArray#toINT32()
	 * @param array Zahlenfolge.
	 * @param dataType Datentyp ({@code 1..3}).
	 * @return Folge von {@code INT8}, {@code INT16} bzw. {@code INT32} Zahlen.
	 * @throws IllegalArgumentException Wenn der gegebene Datentyp ungültig ist.
	 */
	protected static IAMBufferArray dataArray(final IAMBufferArray array, final int dataType) throws IllegalArgumentException {
		switch (dataType) {
			case 1:
				return array.toINT8();
			case 2:
				return array.toINT16();
			case 3:
				return array.toINT32();
		}
		throw new IllegalArgumentException();
	}

	/**
	 * Diese Methode prüft die Monotonität der gegebenen Zahlenfolge.
	 * 
	 * @param array Zahlenfolge.
	 * @throws IAMException Wenn die erste Zahl nicht {@code 0} ist oder die Zahlen nicht monoton steigen.
	 */
	protected static void checkArray(final IAMArray array) throws IAMException {
		int value = array.get(0);
		if (value != 0) throw new IAMException(IAMException.INVALID_OFFSET);
		for (int i = 0, length = array.length(); i < length; i++) {
			final int value2 = array.get(i);
			if (value > value2) throw new IAMException(IAMException.INVALID_OFFSET);
			value = value2;
		}
	}

	{}

	/**
	 * Diese Methode öffnet die gegebene Datei als {@link MappedByteBuffer} in der nativen Bytereihenfolge und gibt das dazu erzeugte {@link IAMBufferINT8Array}
	 * zurück.
	 * 
	 * @see #arrayOf(File, ByteOrder)
	 * @param file Datei.
	 * @return {@link IAMBufferINT8Array}.
	 * @throws IOException Wenn die Datei nicht geöffnet werden kann.
	 */
	public static IAMBufferINT8Array arrayOf(final File file) throws IOException {
		return IAMDecoder.arrayOf(file, ByteOrder.nativeOrder());
	}

	/**
	 * Diese Methode öffnet die gegebene Datei als {@link MappedByteBuffer} in der gegebenen Bytereihenfolge und gibt das dazu erzeugte {@link IAMBufferINT8Array}
	 * zurück.
	 * 
	 * @see #arrayOf(ByteBuffer)
	 * @param file Datei.
	 * @param order Bytereihenfolge.
	 * @return {@link IAMBufferINT8Array}.
	 * @throws IOException Wenn die Datei nicht geöffnet werden kann.
	 */
	public static IAMBufferINT8Array arrayOf(final File file, final ByteOrder order) throws IOException {
		final RandomAccessFile data = new RandomAccessFile(file, "r");
		try {
			return IAMDecoder.arrayOf(data.getChannel().map(MapMode.READ_ONLY, 0, file.length()).order(ByteOrder.nativeOrder()));
		} finally {
			data.close();
		}
	}

	/**
	 * Diese Methode gibt den gegebenen Speicherbereich als {@link IAMBufferINT8Array} zurück.
	 * 
	 * @param buffer Speicherbereich.
	 * @return {@link IAMBufferINT8Array}.
	 */
	public static IAMBufferINT8Array arrayOf(final ByteBuffer buffer) {
		return new IAMBufferINT8Array(buffer);
	}

}
