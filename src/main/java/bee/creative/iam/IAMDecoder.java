package bee.creative.iam;

import bee.creative.iam.IAM.IAMBaseArray;
import bee.creative.iam.IAM.IAMBaseIndex;
import bee.creative.iam.IAM.IAMBaseList;
import bee.creative.iam.IAM.IAMBaseMap;
import bee.creative.mmf.MMFArray;

/**
 * Diese Klasse implementiert die Klassen und Methoden zur Dekodierung der {@link IAM} Datenstrukturen.
 * 
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class IAMDecoder {

	/**
	 * Diese Klasse implementiert eine {@link IAMMap}, die ihre Daten aus einem {@link MMFArray} dekodiert.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class IAMMapDecoder extends IAMBaseMap {

		/**
		 * Dieses Feld speichert den leeren {@link IAMMapDecoder}.
		 */
		public static final IAMMapDecoder EMPTY = new IAMMapDecoder();

		{}

		/**
		 * Dieses Feld speichert die Zahlen der Schlüssel.
		 */
		protected final MMFArray keyData;

		/**
		 * Dieses Feld speichert die Startpositionen der Schlüssel.
		 */
		protected final MMFArray keyOffset;

		/**
		 * Dieses Feld speichert die Länge der Schlüssel.
		 */
		protected final int keyLength;

		/**
		 * Dieses Feld speichert die Zahlen der Werte.
		 */
		protected final MMFArray valueData;

		/**
		 * Dieses Feld speichert die Startpositionen der Werte.
		 */
		protected final MMFArray valueOffset;

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
		protected final MMFArray rangeOffset;

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
			final MMFArray keyOffset;
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
			final MMFArray keyData = IAMDecoder.dataArray(array.section(offset, keyValue), keyDataType);
			offset += keyValue;
			if (array.length() <= offset) throw new IAMException(IAMException.INVALID_LENGTH);

			int valueValue;
			final int valueLength;
			final MMFArray valueOffset;
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
			final MMFArray valueData = IAMDecoder.dataArray(array.section(offset, valueValue), valueDataType);
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

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public MMFArray key(final int entryIndex) {
			if ((entryIndex < 0) || (entryIndex >= this.entryCount)) return MMFArray.EMPTY;
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
		public MMFArray value(final int entryIndex) {
			if ((entryIndex < 0) || (entryIndex >= this.entryCount)) return MMFArray.EMPTY;
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
		public int find(final int... key) throws NullPointerException {
			final IAMArray array = key.length == 0 ? IAMBaseArray.EMPTY : IAM.toArray(key);
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
	 * Diese Klasse implementiert eine {@link IAMList}, die ihre Daten aus einem {@link MMFArray} dekodiert.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class IAMListDecoder extends IAMBaseList {

		/**
		 * Dieses Feld speichert den leeren {@link IAMListDecoder}.
		 */
		public static final IAMListDecoder EMPTY = new IAMListDecoder();

		{}

		/**
		 * Dieses Feld speichert die Zahlen der Elemente.
		 */
		protected final MMFArray itemData;

		/**
		 * Dieses Feld speichert die Startpositionen der Elemente.
		 */
		protected final MMFArray itemOffset;

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
			final MMFArray itemData = IAMDecoder.dataArray(array.section(offset, itemValue), itemDataType);
			offset += itemValue;
			if (array.length() != offset) throw new IAMException(IAMException.INVALID_LENGTH);

			this.itemData = itemData;
			this.itemOffset = itemOffset;
			this.itemLength = itemLength;
			this.itemCount = itemCount;

		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public MMFArray item(final int itemIndex) {
			if ((itemIndex < 0) || (itemIndex >= this.itemCount)) return MMFArray.EMPTY;
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
	 * Diese Klasse implementiert einen {@link IAMIndex}, der seine Daten aus einem {@link MMFArray} dekodiert.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class IAMIndexDecoder extends IAMBaseIndex {

		/**
		 * Dieses Feld speichert den leeren {@link IAMIndexDecoder}.
		 */
		public static final IAMIndexDecoder EMPTY = new IAMIndexDecoder();

		{}

		/**
		 * Dieses Feld speichert die Abbildungen.
		 */
		protected final IAMMapDecoder[] maps;

		/**
		 * Dieses Feld speichert die Listen.
		 */
		protected final IAMListDecoder[] lists;

		/**
		 * Dieser Konstruktor initialisiert das leere Inhaltsverzeichnis.
		 */
		protected IAMIndexDecoder() {
			this.maps = new IAMMapDecoder[0];
			this.lists = new IAMListDecoder[0];
		}

		/**
		 * Dieser Kontrukteur initialisiert diesen {@link IAMIndex} als Sicht auf den gegebenen Speicherbereich.
		 * 
		 * @param array Speicherbereich mit {@code INT32} Zahlen.
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

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IAMMapDecoder map(final int index) {
			if ((index < 0) || (index >= this.maps.length)) return IAMMapDecoder.EMPTY;
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
			if ((index < 0) || (index >= this.lists.length)) return IAMListDecoder.EMPTY;
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
	 * Diese Methode gibt den Speicherbereich der gegebenen Zahlenfolge als {@link MMFArray} mit {@code UINT8}, {@code UINT16} bzw. {@code INT32} Zahlen zurück.
	 * 
	 * @see MMFArray#toUINT8()
	 * @see MMFArray#toUINT16()
	 * @see MMFArray#toINT32()
	 * @param array Zahlenfolge.
	 * @param sizeType Größentyp ({@code 1..3}).
	 * @return Folge von {@code UINT8}, {@code UINT16} bzw. {@code INT32} Zahlen.
	 * @throws IllegalArgumentException Wenn der gegebene Größentyp ungültig ist.
	 */
	static MMFArray sizeArray(final MMFArray array, final int sizeType) throws IllegalArgumentException {
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
	 * Diese Methode gibt den Speicherbereich der gegebenen Zahlenfolge als {@link MMFArray} mit {@code INT8}, {@code INT16} bzw. {@code INT32} Zahlen zurück.
	 * 
	 * @see MMFArray#toINT8()
	 * @see MMFArray#toINT16()
	 * @see MMFArray#toINT32()
	 * @param array Zahlenfolge.
	 * @param dataType Datentyp ({@code 1..3}).
	 * @return Folge von {@code INT8}, {@code INT16} bzw. {@code INT32} Zahlen.
	 * @throws IllegalArgumentException Wenn der gegebene Datentyp ungültig ist.
	 */
	static MMFArray dataArray(final MMFArray array, final int dataType) throws IllegalArgumentException {
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
	static void checkArray(final IAMArray array) throws IAMException {
		int value = array.get(0);
		if (value != 0) throw new IAMException(IAMException.INVALID_OFFSET);
		for (int i = 0, length = array.length(); i < length; i++) {
			final int value2 = array.get(i);
			if (value > value2) throw new IAMException(IAMException.INVALID_OFFSET);
			value = value2;
		}
	}

}
