package bee.creative.iam;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import bee.creative.mmf.MMFArray;
import bee.creative.util.Bytes;

/**
 * Diese Klasse implementiert die Klassen und Methoden zur Dekodierung der {@code Integer Array Model} Datenstrukturen.
 * 
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class IAMDecoder {

	/**
	 * Diese Klasse implementiert ein Objekt zur Analyse und Prüfung der Kennung einer Datenstruktur in den Kopfdaten von {@code IAM_MAP}, {@code IAM_LIST} oder
	 * {@code IAM_INDEX}.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class IAMHeader {

		/**
		 * Dieses Feld speichert die Bitmaske.
		 */
		final int __mask;

		/**
		 * Dieses Feld speichert den Vergleichswert.
		 */
		final int __value;

		@SuppressWarnings ("javadoc")
		IAMHeader(final int mask, final int value) {
			this.__mask = mask;
			this.__value = value;
		}

		{}

		/**
		 * Diese Methode gibt nur dann {@code true} zurück, wenn die gegebenen Kopfdaten eine gültige Datenstrukturkennung enthalten.
		 * 
		 * @param header Kopfdaten.
		 * @return {@code true}, wenn die Kopfdaten eine gültige Kennunge enthalten.
		 */
		public final boolean isValid(final int header) {
			return (header & this.__mask) == this.__value;
		}

		/**
		 * Diese Methode gibt die Bytereihenfolge zur Interpretation der gegebenen Quelldaten zurück, für welche die Kopfdaten in den ersten vier Byte der
		 * Quelldaten die {@link #isValid(int) gültige} Datenstrukturkennung enthalten. Wenn die Kopfdaten {@link #isValid(int) ungültig} sind, wird {@code null}
		 * geliefert.
		 * 
		 * @see #orderOf(byte[])
		 * @param file Datei mit den Quelldaten.
		 * @return Bytereihenfolge oder {@code null}.
		 * @throws IOException Wenn {@link RandomAccessFile#RandomAccessFile(File, String)} bzw. {@link RandomAccessFile#read(byte[])} eine entsprechende Ausnahme
		 *         auslöst.
		 * @throws NullPointerException Wenn {@code file} {@code null} ist.
		 * @throws IllegalArgumentException Wenn die Quelldaten unzureichend sind.
		 */
		public final ByteOrder orderOf(final File file) throws IOException, NullPointerException, IllegalArgumentException {
			try (RandomAccessFile source = new RandomAccessFile(file.getAbsoluteFile(), "r")) {
				final byte[] bytes = new byte[4];
				if (source.read(bytes) < 4) throw new IllegalArgumentException();
				return this.orderOf(bytes);
			}
		}

		/**
		 * Diese Methode gibt die Bytereihenfolge zur Interpretation der gegebenen Quelldaten zurück, für welche die Kopfdaten in den ersten vier Byte der
		 * Quelldaten die {@link #isValid(int) gültige} Datenstrukturkennung enthalten. Wenn die Kopfdaten {@link #isValid(int) ungültig} sind, wird {@code null}
		 * geliefert.
		 * 
		 * @param bytes Quelldaten.
		 * @return Bytereihenfolge oder {@code null}.
		 * @throws NullPointerException Wenn {@code bytes} {@code null} ist.
		 * @throws IllegalArgumentException Wenn die Quelldaten unzureichend sind.
		 */
		public final ByteOrder orderOf(final byte[] bytes) throws NullPointerException, IllegalArgumentException {
			if (bytes.length < 4) throw new IllegalArgumentException();
			if (this.isValid(Bytes.getInt4BE(bytes, 0))) return ByteOrder.BIG_ENDIAN;
			if (this.isValid(Bytes.getInt4LE(bytes, 0))) return ByteOrder.LITTLE_ENDIAN;
			return null;
		}

		/**
		 * Diese Methode gibt die Bytereihenfolge zur Interpretation der gegebenen Quelldaten zurück, für welche die Kopfdaten in den ersten vier Byte der
		 * Quelldaten die {@link #isValid(int) gültige} Datenstrukturkennung enthalten. Wenn die Kopfdaten {@link #isValid(int) ungültig} sind, wird {@code null}
		 * geliefert.
		 * 
		 * @see #orderOf(byte[])
		 * @param buffer Puffer mit den Quelldaten.
		 * @return Bytereihenfolge oder {@code null}.
		 * @throws NullPointerException Wenn {@code buffer} {@code null} ist.
		 * @throws IllegalArgumentException Wenn die Quelldaten unzureichend sind.
		 */
		public final ByteOrder orderOf(final ByteBuffer buffer) throws NullPointerException, IllegalArgumentException {
			if (buffer.remaining() < 4) throw new IllegalArgumentException();
			final byte[] bytes = new byte[4];
			buffer.get(bytes);
			return this.orderOf(bytes);
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final String toString() {
			return Integer.toHexString(this.__value);
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link IAMMap}, die ihre Daten aus einem {@link MMFArray} dekodiert.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class IAMMapDecoder extends IAMMap {

		/**
		 * Dieses Feld speichert den leeren {@link IAMMapDecoder}.
		 */
		public static final IAMMapDecoder EMPTY = new IAMMapDecoder();

		/**
		 * Dieses Feld speichert den {@link IAMHeader} einer {@code IAM_MAP} Datenstruktur.
		 */
		public static final IAMHeader HEADER = new IAMHeader(0xFFFFFC00, 0xF00D1000);

		{}

		/**
		 * Dieses Feld speichert die Zahlen der Schlüssel.
		 */
		final MMFArray __keyData;

		/**
		 * Dieses Feld speichert die Startpositionen der Schlüssel.
		 */
		final MMFArray __keyOffset;

		/**
		 * Dieses Feld speichert die Länge der Schlüssel.
		 */
		final int __keyLength;

		/**
		 * Dieses Feld speichert die Zahlen der Werte.
		 */
		final MMFArray __valueData;

		/**
		 * Dieses Feld speichert die Startpositionen der Werte.
		 */
		final MMFArray __valueOffset;

		/**
		 * Dieses Feld speichert die Länge der Werte.
		 */
		final int __valueLength;

		/**
		 * Dieses Feld speichert die Bitmaske der Schlüsselbereiche.
		 */
		final int __rangeMask;

		/**
		 * Dieses Feld speichert die Startpositionen der Schlüsselbereiche.
		 */
		final MMFArray __rangeOffset;

		/**
		 * Dieses Feld speichert die Anzahl der Einträge.
		 */
		final int __entryCount;

		/**
		 * Dieser Konstruktor initialisiert die leere Abbildung.
		 */
		IAMMapDecoder() {
			this.__keyData = null;
			this.__keyOffset = null;
			this.__keyLength = 0;
			this.__valueData = null;
			this.__valueOffset = null;
			this.__valueLength = 0;
			this.__rangeMask = 0;
			this.__rangeOffset = null;
			this.__entryCount = 0;
		}

		/**
		 * Dieser Kontrukteur initialisiert diese {@link IAMMap} als Sicht auf den gegebenen Speicherbereich.
		 * 
		 * @param array Speicherbereich mit {@code INT32} Zahlen.
		 * @throws IAMException Wenn beim dekodieren des Speicherbereichs ein Fehler erkannt wird.
		 * @throws NullPointerException Wenn {@code array} {@code null} ist.
		 */
		public IAMMapDecoder(MMFArray array) throws IAMException, NullPointerException {
			array = array.toINT32();
			if (array.length() < 4) throw new IAMException(IAMException.INVALID_LENGTH);

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
			final MMFArray rangeOffset;
			if (rangeSizeType != 0) {

				if (array.length() <= offset) throw new IAMException(IAMException.INVALID_LENGTH);

				rangeMask = array.get(offset);
				offset++;
				if ((rangeMask < 1) || (rangeMask > 0x1FFFFFFF) || (((rangeMask + 1) & rangeMask) != 0)) throw new IAMException(IAMException.INVALID_VALUE);

				rangeValue = IAM.__byteAlign((rangeMask + 2) * IAM.__byteCount(rangeSizeType));
				rangeOffset = IAM.__sizeArray(array.section(offset, rangeValue), rangeSizeType).section(0, rangeMask + 2);
				offset += rangeValue;
				if (array.length() <= offset) throw new IAMException(IAMException.INVALID_LENGTH);

				IAM.__checkArray(rangeOffset);
				rangeValue = rangeOffset.get(rangeMask + 1);
				if (rangeValue != entryCount) throw new IAMException(IAMException.INVALID_OFFSET);

			} else {

				rangeMask = 0;
				rangeOffset = null;

			}

			if (array.length() <= offset) throw new IAMException(IAMException.INVALID_LENGTH);

			int keyValue;
			final int keyLength;
			final MMFArray keyOffset;
			if (keySizeType != 0) {

				keyValue = IAM.__byteAlign((entryCount + 1) * IAM.__byteCount(keySizeType));
				keyLength = 0;
				keyOffset = IAM.__sizeArray(array.section(offset, keyValue), keySizeType).section(0, entryCount + 1);
				offset += keyValue;
				if (array.length() <= offset) throw new IAMException(IAMException.INVALID_LENGTH);

				IAM.__checkArray(keyOffset);
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

			keyValue = IAM.__byteAlign(keyValue * IAM.__byteCount(keyDataType));
			final MMFArray keyData = IAM.__dataArray(array.section(offset, keyValue), keyDataType);
			offset += keyValue;
			if (array.length() <= offset) throw new IAMException(IAMException.INVALID_LENGTH);

			int valueValue;
			final int valueLength;
			final MMFArray valueOffset;
			if (valueSizeType != 0) {

				valueValue = IAM.__byteAlign((entryCount + 1) * IAM.__byteCount(valueSizeType));
				valueLength = 0;
				valueOffset = IAM.__sizeArray(array.section(offset, valueValue), valueSizeType).section(0, entryCount + 1);
				offset += valueValue;
				if (array.length() < offset) throw new IAMException(IAMException.INVALID_LENGTH);

				IAM.__checkArray(valueOffset);
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

			valueValue = IAM.__byteAlign(valueValue * IAM.__byteCount(valueDataType));
			final MMFArray valueData = IAM.__dataArray(array.section(offset, valueValue), valueDataType);
			offset += valueValue;
			if (array.length() != offset) throw new IAMException(IAMException.INVALID_LENGTH);

			this.__keyData = keyData;
			this.__keyOffset = keyOffset;
			this.__keyLength = keyLength;
			this.__valueData = valueData;
			this.__valueOffset = valueOffset;
			this.__valueLength = valueLength;
			this.__rangeMask = rangeMask;
			this.__rangeOffset = rangeOffset;
			this.__entryCount = entryCount;

		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final boolean mode() {
			return this.__rangeMask != 0;
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final MMFArray key(final int entryIndex) {
			if ((entryIndex < 0) || (entryIndex >= this.__entryCount)) return MMFArray.EMPTY;
			final IAMArray keyOffset = this.__keyOffset;
			if (keyOffset != null) {
				final int offset = keyOffset.get(entryIndex);
				final int length = keyOffset.get(entryIndex + 1) - offset;
				return this.__keyData.section(offset, length);
			} else {
				final int length = this.__keyLength;
				final int offset = length * entryIndex;
				return this.__keyData.section(offset, length);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final MMFArray value(final int entryIndex) {
			if ((entryIndex < 0) || (entryIndex >= this.__entryCount)) return MMFArray.EMPTY;
			final IAMArray keyOffset = this.__valueOffset;
			if (keyOffset != null) {
				final int offset = keyOffset.get(entryIndex);
				final int length = keyOffset.get(entryIndex + 1) - offset;
				return this.__valueData.section(offset, length);
			} else {
				final int length = this.__valueLength;
				final int offset = length * entryIndex;
				return this.__valueData.section(offset, length);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final int entryCount() {
			return this.__entryCount;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final int find(final IAMArray key) throws NullPointerException {
			if (key == null) throw new NullPointerException("key = null");
			int i = this.__rangeMask;
			if (i != 0) {
				final IAMArray range = this.__rangeOffset;
				i = key.hash() & i;
				for (int l = range.get(i), r = range.get(i + 1); l < r; l++) {
					if (this.key(l).equals(key)) return l;
				}
			} else {
				int l = 0, r = this.__entryCount;
				while (l < r) {
					i = (l + r) >> 1;
					i = key.compare(this.key(i));
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
	 * Diese Klasse implementiert eine {@link IAMList}, die ihre Daten aus einem {@link MMFArray} dekodiert.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class IAMListDecoder extends IAMList {

		/**
		 * Dieses Feld speichert den leeren {@link IAMListDecoder}.
		 */
		public static final IAMListDecoder EMPTY = new IAMListDecoder();

		/**
		 * Dieses Feld speichert den {@link IAMHeader} einer {@code IAM_LIST} Datenstruktur.
		 */
		public static final IAMHeader HEADER = new IAMHeader(0xFFFFFFF0, 0xF00D2000);

		{}

		/**
		 * Dieses Feld speichert die Zahlen der Elemente.
		 */
		final MMFArray __itemData;

		/**
		 * Dieses Feld speichert die Startpositionen der Elemente.
		 */
		final MMFArray __itemOffset;

		/**
		 * Dieses Feld speichert die Länge der Elemente.
		 */
		final int __itemLength;

		/**
		 * Dieses Feld speichert die Anzahl der Elemente.
		 */
		final int __itemCount;

		/**
		 * Dieser Konstruktor initialisiert die leere Liste.
		 */
		IAMListDecoder() {
			this.__itemData = null;
			this.__itemOffset = null;
			this.__itemLength = 0;
			this.__itemCount = 0;
		}

		/**
		 * Dieser Kontrukteur initialisiert diese {@link IAMList} als Sicht auf den gegebenen Speicherbereich.
		 * 
		 * @param array Speicherbereich mit {@code INT32} Zahlen.
		 * @throws IAMException Wenn beim dekodieren des Speicherbereichs ein Fehler erkannt wird.
		 * @throws NullPointerException Wenn {@code array} {@code null} ist.
		 */
		public IAMListDecoder(MMFArray array) throws IAMException, NullPointerException {
			array = array.toINT32();
			if (array.length() < 3) throw new IAMException(IAMException.INVALID_LENGTH);

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
			final MMFArray itemOffset;
			if (itemSizeType != 0) {

				itemValue = IAM.__byteAlign((itemCount + 1) * IAM.__byteCount(itemSizeType));
				itemLength = 0;
				itemOffset = IAM.__sizeArray(array.section(offset, itemValue), itemSizeType).section(0, itemCount + 1);
				offset += itemValue;
				if (array.length() < offset) throw new IAMException(IAMException.INVALID_LENGTH);

				IAM.__checkArray(itemOffset);
				itemValue = itemOffset.get(itemCount);

			} else {

				itemValue = array.get(offset);
				offset++;
				if ((itemValue < 0) || (itemValue > 0x3FFFFFFF)) throw new IAMException(IAMException.INVALID_VALUE);

				itemLength = itemValue;
				itemValue = itemCount * itemValue;
				itemOffset = null;

			}

			itemValue = IAM.__byteAlign(itemValue * IAM.__byteCount(itemDataType));
			final MMFArray itemData = IAM.__dataArray(array.section(offset, itemValue), itemDataType);
			offset += itemValue;
			if (array.length() != offset) throw new IAMException(IAMException.INVALID_LENGTH);

			this.__itemData = itemData;
			this.__itemOffset = itemOffset;
			this.__itemLength = itemLength;
			this.__itemCount = itemCount;

		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final MMFArray item(final int itemIndex) {
			if ((itemIndex < 0) || (itemIndex >= this.__itemCount)) return MMFArray.EMPTY;
			final IAMArray itemOffset = this.__itemOffset;
			if (itemOffset != null) {
				final int offset = itemOffset.get(itemIndex);
				final int length = itemOffset.get(itemIndex + 1) - offset;
				return this.__itemData.section(offset, length);
			} else {
				final int length = this.__itemLength;
				final int offset = length * itemIndex;
				return this.__itemData.section(offset, length);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final int itemCount() {
			return this.__itemCount;
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link IAMIndex}, der seine Daten aus einem {@link MMFArray} dekodiert.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class IAMIndexDecoder extends IAMIndex {

		/**
		 * Dieses Feld speichert den leeren {@link IAMIndexDecoder}.
		 */
		public static final IAMIndexDecoder EMPTY = new IAMIndexDecoder();

		/**
		 * Dieses Feld speichert den {@link IAMHeader} einer {@code IAM_INDEX} Datenstruktur.
		 */
		public static final IAMHeader HEADER = new IAMHeader(0xFFFFFFFF, 0xF00DBA5E);

		{}

		/**
		 * Dieses Feld speichert die Abbildungen.
		 */
		final IAMMapDecoder[] __maps;

		/**
		 * Dieses Feld speichert die Listen.
		 */
		final IAMListDecoder[] __lists;

		/**
		 * Dieser Konstruktor initialisiert das leere Inhaltsverzeichnis.
		 */
		IAMIndexDecoder() {
			this.__maps = new IAMMapDecoder[0];
			this.__lists = new IAMListDecoder[0];
		}

		/**
		 * Dieser Kontrukteur initialisiert diesen {@link IAMIndex} als Sicht auf den gegebenen Speicherbereich.
		 * 
		 * @param array Speicherbereich.
		 * @throws IAMException Wenn beim dekodieren des Speicherbereichs ein Fehler erkannt wird.
		 * @throws NullPointerException Wenn {@code array} {@code null} ist.
		 */
		public IAMIndexDecoder(MMFArray array) throws IAMException, NullPointerException {
			array = array.toINT32();
			if (array.length() < 5) throw new IAMException(IAMException.INVALID_LENGTH);

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

			final MMFArray mapData = array.section(offset, mapDataLength).toINT32();
			offset += mapDataLength;
			if (array.length() < offset) throw new IAMException(IAMException.INVALID_LENGTH);

			final MMFArray listData = array.section(offset, listDataLength).toINT32();
			offset += listDataLength;
			if (array.length() != offset) throw new IAMException(IAMException.INVALID_LENGTH);

			this.__maps = new IAMMapDecoder[mapCount];
			for (int i = 0; i < mapCount; i++) {
				final int offset2 = mapOffset.get(i);
				final int length2 = mapOffset.get(i + 1) - offset2;
				this.__maps[i] = new IAMMapDecoder(mapData.section(offset2, length2).toINT32());
			}

			this.__lists = new IAMListDecoder[listCount];
			for (int i = 0; i < listCount; i++) {
				final int offset2 = listOffset.get(i);
				final int length2 = listOffset.get(i + 1) - offset2;
				this.__lists[i] = new IAMListDecoder(listData.section(offset2, length2).toINT32());
			}

		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final IAMMapDecoder map(final int index) {
			if ((index < 0) || (index >= this.__maps.length)) return IAMMapDecoder.EMPTY;
			return this.__maps[index];
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final int mapCount() {
			return this.__maps.length;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final IAMListDecoder list(final int index) {
			if ((index < 0) || (index >= this.__lists.length)) return IAMListDecoder.EMPTY;
			return this.__lists[index];
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final int listCount() {
			return this.__lists.length;
		}

	}

}
