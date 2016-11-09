package bee.creative.iam;

import java.io.IOException;
import java.nio.ByteOrder;
import bee.creative.mmf.MMFArray;
import bee.creative.util.Bytes;

/** Diese Klasse implementiert die Klassen und Methoden zur Dekodierung der {@code Integer Array Model} Datenstrukturen.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class IAMLoader {

	/** Diese Klasse implementiert ein Objekt zur Analyse und Prüfung der Kennung einer Datenstruktur in den Kopfdaten von {@code IAM_MAP}, {@code IAM_LIST} oder
	 * {@code IAM_INDEX}.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static final class IAMHeader {

		/** Dieses Feld speichert die Bitmaske. */
		final int _mask_;

		/** Dieses Feld speichert den Vergleichswert. */
		final int _value_;

		/** Dieser Konstruktor initialisiert Bitmaske und Vergleichswert.
		 *
		 * @param mask Bitmaske.
		 * @param value Vergleichswert. */
		public IAMHeader(final int mask, final int value) {
			this._mask_ = mask;
			this._value_ = value;
		}

		{}

		/** Diese Methode gibt nur dann {@code true} zurück, wenn die gegebenen Kopfdaten eine gültige Datenstrukturkennung enthalten.
		 *
		 * @param header Kopfdaten.
		 * @return {@code true}, wenn die Kopfdaten eine gültige Kennunge enthalten. */
		public final boolean isValid(final int header) {
			return (header & this._mask_) == this._value_;
		}

		/** Diese Methode gibt die Bytereihenfolge zur Interpretation der gegebenen Quelldaten zurück, für welche die Kopfdaten in den ersten vier Byte der
		 * Quelldaten die {@link #isValid(int) gültige} Datenstrukturkennung enthalten. Wenn die Kopfdaten {@link #isValid(int) ungültig} sind, wird {@code null}
		 * geliefert.
		 *
		 * @param bytes Quelldaten.
		 * @return Bytereihenfolge oder {@code null}.
		 * @throws NullPointerException Wenn {@code bytes} {@code null} ist. */
		public final ByteOrder orderOf(final byte[] bytes) throws NullPointerException {
			if (bytes.length < 4) return null;
			if (this.isValid(Bytes.getInt4BE(bytes, 0))) return ByteOrder.BIG_ENDIAN;
			if (this.isValid(Bytes.getInt4LE(bytes, 0))) return ByteOrder.LITTLE_ENDIAN;
			return null;
		}

		/** Diese Methode gibt die Bytereihenfolge zur Interpretation der gegebenen Quelldaten zurück, für welche die Kopfdaten in den ersten vier Byte der
		 * Quelldaten die {@link #isValid(int) gültige} Datenstrukturkennung enthalten. Wenn die Kopfdaten {@link #isValid(int) ungültig} sind, wird {@code null}
		 * geliefert.
		 *
		 * @see #orderOf(byte[])
		 * @see MMFArray#from(Object)
		 * @param object Quelldaten.
		 * @return Bytereihenfolge oder {@code null}.
		 * @throws IOException Wenn {@link MMFArray#from(Object)} eine entsprechende Ausnahme auslöst. **/
		public final ByteOrder orderOf(final Object object) throws IOException {
			final byte[] bytes = MMFArray.from(object).toUINT8().section(0, 4).toBytes();
			return this.orderOf(bytes);
		}

		{}

		/** {@inheritDoc} */
		@Override
		public final String toString() {
			return Integer.toHexString(this._value_);
		}

	}

	/** Diese Klasse implementiert einen {@link IAMIndex}, der seine Daten aus einem {@link MMFArray} dekodiert.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static final class IAMIndexLoader extends IAMIndex {

		/** Dieses Feld speichert den leeren {@link IAMIndexLoader}. */
		public static final IAMIndexLoader EMPTY = new IAMIndexLoader();

		/** Dieses Feld speichert den {@link IAMHeader} einer {@code IAM_INDEX} Datenstruktur. */
		public static final IAMHeader HEADER = new IAMHeader(0xFFFFFFFF, 0xF00DBA5E);

		{}

		/** Dieses Feld speichert die Abbildungen. */
		final IAMMappingLoader[] _mappings_;

		/** Dieses Feld speichert die Listen. */
		final IAMListingLoader[] _listings_;

		/** Dieser Konstruktor initialisiert das leere Inhaltsverzeichnis. */
		IAMIndexLoader() {
			this._mappings_ = new IAMMappingLoader[0];
			this._listings_ = new IAMListingLoader[0];
		}

		/** Dieser Kontrukteur initialisiert diesen {@link IAMIndex} als Sicht auf den gegebenen Speicherbereich.
		 *
		 * @param array Speicherbereich.
		 * @throws IAMException Wenn beim dekodieren des Speicherbereichs ein Fehler erkannt wird.
		 * @throws NullPointerException Wenn {@code array} {@code null} ist. */
		public IAMIndexLoader(MMFArray array) throws IAMException, NullPointerException {
			array = array.toINT32();
			if (array.length() < 5) throw new IAMException(IAMException.INVALID_LENGTH);

			int offset = 0;
			final int header = array.get(offset);
			offset++;
			if (header != 0xF00DBA5E) throw new IAMException(IAMException.INVALID_HEADER);

			final int mappingCount = array.get(offset);
			offset++;
			if ((mappingCount < 0) || (mappingCount > 0x3FFFFFFF)) throw new IAMException(IAMException.INVALID_VALUE);

			final int listingCount = array.get(offset);
			offset++;
			if ((listingCount < 0) || (listingCount > 0x3FFFFFFF)) throw new IAMException(IAMException.INVALID_VALUE);

			final IAMArray mappingOffset = array.section(offset, mappingCount + 1);
			offset += mappingCount + 1;
			if (array.length() < offset) throw new IAMException(IAMException.INVALID_LENGTH);

			final int mappingDataLength = mappingOffset.get(mappingCount);
			if ((mappingDataLength < 0) || (mappingDataLength > 0x3FFFFFFF)) throw new IAMException(IAMException.INVALID_VALUE);

			final IAMArray listingOffset = array.section(offset, listingCount + 1);
			offset += listingCount + 1;
			if (array.length() < offset) throw new IAMException(IAMException.INVALID_LENGTH);

			final int listingDataLength = listingOffset.get(listingCount);
			if ((listingDataLength < 0) || (listingDataLength > 0x3FFFFFFF)) throw new IAMException(IAMException.INVALID_VALUE);

			final MMFArray mappingData = array.section(offset, mappingDataLength).toINT32();
			offset += mappingDataLength;
			if (array.length() < offset) throw new IAMException(IAMException.INVALID_LENGTH);

			final MMFArray listingData = array.section(offset, listingDataLength).toINT32();
			offset += listingDataLength;
			if (array.length() != offset) throw new IAMException(IAMException.INVALID_LENGTH);

			this._mappings_ = new IAMMappingLoader[mappingCount];
			for (int i = 0; i < mappingCount; i++) {
				final int offset2 = mappingOffset.get(i);
				final int length2 = mappingOffset.get(i + 1) - offset2;
				this._mappings_[i] = new IAMMappingLoader(mappingData.section(offset2, length2).toINT32());
			}

			this._listings_ = new IAMListingLoader[listingCount];
			for (int i = 0; i < listingCount; i++) {
				final int offset2 = listingOffset.get(i);
				final int length2 = listingOffset.get(i + 1) - offset2;
				this._listings_[i] = new IAMListingLoader(listingData.section(offset2, length2).toINT32());
			}

		}

		{}

		/** {@inheritDoc} */
		@Override
		public final IAMMappingLoader mapping(final int index) {
			if ((index < 0) || (index >= this._mappings_.length)) return IAMMappingLoader.EMPTY;
			return this._mappings_[index];
		}

		/** {@inheritDoc} */
		@Override
		public final int mappingCount() {
			return this._mappings_.length;
		}

		/** {@inheritDoc} */
		@Override
		public final IAMListingLoader listing(final int index) {
			if ((index < 0) || (index >= this._listings_.length)) return IAMListingLoader.EMPTY;
			return this._listings_[index];
		}

		/** {@inheritDoc} */
		@Override
		public final int listingCount() {
			return this._listings_.length;
		}

	}

	/** Diese Klasse implementiert eine {@link IAMListing}, die ihre Daten aus einem {@link MMFArray} dekodiert.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static final class IAMListingLoader extends IAMListing {

		/** Dieses Feld speichert den leeren {@link IAMListingLoader}. */
		public static final IAMListingLoader EMPTY = new IAMListingLoader();

		/** Dieses Feld speichert den {@link IAMHeader} einer {@code IAM_LIST} Datenstruktur. */
		public static final IAMHeader HEADER = new IAMHeader(0xFFFFFFF0, 0xF00D2000);

		{}

		/** Dieses Feld speichert die Zahlen der Elemente. */
		final MMFArray _itemData_;

		/** Dieses Feld speichert die Startpositionen der Elemente. */
		final MMFArray _itemOffset_;

		/** Dieses Feld speichert die Länge der Elemente. */
		final int _itemLength_;

		/** Dieses Feld speichert die Anzahl der Elemente. */
		final int _itemCount_;

		/** Dieser Konstruktor initialisiert die leere Liste. */
		IAMListingLoader() {
			this._itemData_ = null;
			this._itemOffset_ = null;
			this._itemLength_ = 0;
			this._itemCount_ = 0;
		}

		/** Dieser Kontrukteur initialisiert diese {@link IAMListing} als Sicht auf den gegebenen Speicherbereich.
		 *
		 * @param array Speicherbereich mit {@code INT32} Zahlen.
		 * @throws IAMException Wenn beim dekodieren des Speicherbereichs ein Fehler erkannt wird.
		 * @throws NullPointerException Wenn {@code array} {@code null} ist. */
		public IAMListingLoader(MMFArray array) throws IAMException, NullPointerException {
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

				itemValue = IAMLoader._byteAlign_((itemCount + 1) * IAMLoader._byteCount_(itemSizeType));
				itemLength = 0;
				itemOffset = IAMLoader.sizeArray(array.section(offset, itemValue), itemSizeType).section(0, itemCount + 1);
				offset += itemValue;
				if (array.length() < offset) throw new IAMException(IAMException.INVALID_LENGTH);

				IAMLoader._checkArray_(itemOffset);
				itemValue = itemOffset.get(itemCount);

			} else {

				itemValue = array.get(offset);
				offset++;
				if ((itemValue < 0) || (itemValue > 0x3FFFFFFF)) throw new IAMException(IAMException.INVALID_VALUE);

				itemLength = itemValue;
				itemValue = itemCount * itemValue;
				itemOffset = null;

			}

			itemValue = IAMLoader._byteAlign_(itemValue * IAMLoader._byteCount_(itemDataType));
			final MMFArray itemData = IAMLoader._dataArray_(array.section(offset, itemValue), itemDataType);
			offset += itemValue;
			if (array.length() != offset) throw new IAMException(IAMException.INVALID_LENGTH);

			this._itemData_ = itemData;
			this._itemOffset_ = itemOffset;
			this._itemLength_ = itemLength;
			this._itemCount_ = itemCount;

		}

		{}

		/** {@inheritDoc} */
		@Override
		public final MMFArray item(final int itemIndex) {
			if ((itemIndex < 0) || (itemIndex >= this._itemCount_)) return MMFArray.EMPTY;
			final IAMArray itemOffset = this._itemOffset_;
			if (itemOffset != null) {
				final int offset = itemOffset._get_(itemIndex);
				final int length = itemOffset._get_(itemIndex + 1) - offset;
				return this._itemData_.section(offset, length);
			} else {
				final int length = this._itemLength_;
				final int offset = length * itemIndex;
				return this._itemData_.section(offset, length);
			}
		}

		/** {@inheritDoc} */
		@Override
		public final int itemCount() {
			return this._itemCount_;
		}

	}

	/** Diese Klasse implementiert eine {@link IAMMapping}, die ihre Daten aus einem {@link MMFArray} dekodiert.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static final class IAMMappingLoader extends IAMMapping {

		/** Dieses Feld speichert den leeren {@link IAMMappingLoader}. */
		public static final IAMMappingLoader EMPTY = new IAMMappingLoader();

		/** Dieses Feld speichert den {@link IAMHeader} einer {@code IAM_MAP} Datenstruktur. */
		public static final IAMHeader HEADER = new IAMHeader(0xFFFFFC00, 0xF00D1000);

		{}

		/** Dieses Feld speichert die Zahlen der Schlüssel. */
		final MMFArray _keyData_;

		/** Dieses Feld speichert die Startpositionen der Schlüssel. */
		final MMFArray _keyOffset_;

		/** Dieses Feld speichert die Länge der Schlüssel. */
		final int _keyLength_;

		/** Dieses Feld speichert die Zahlen der Werte. */
		final MMFArray _valueData_;

		/** Dieses Feld speichert die Startpositionen der Werte. */
		final MMFArray _valueOffset_;

		/** Dieses Feld speichert die Länge der Werte. */
		final int _valueLength_;

		/** Dieses Feld speichert die Bitmaske der Schlüsselbereiche. */
		final int _rangeMask_;

		/** Dieses Feld speichert die Startpositionen der Schlüsselbereiche. */
		final MMFArray _rangeOffset_;

		/** Dieses Feld speichert die Anzahl der Einträge. */
		final int _entryCount_;

		/** Dieser Konstruktor initialisiert die leere Abbildung. */
		IAMMappingLoader() {
			this._keyData_ = null;
			this._keyOffset_ = null;
			this._keyLength_ = 0;
			this._valueData_ = null;
			this._valueOffset_ = null;
			this._valueLength_ = 0;
			this._rangeMask_ = 0;
			this._rangeOffset_ = null;
			this._entryCount_ = 0;
		}

		/** Dieser Kontrukteur initialisiert diese {@link IAMMapping} als Sicht auf den gegebenen Speicherbereich.
		 *
		 * @param array Speicherbereich mit {@code INT32} Zahlen.
		 * @throws IAMException Wenn beim dekodieren des Speicherbereichs ein Fehler erkannt wird.
		 * @throws NullPointerException Wenn {@code array} {@code null} ist. */
		public IAMMappingLoader(MMFArray array) throws IAMException, NullPointerException {
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

				rangeValue = IAMLoader._byteAlign_((rangeMask + 2) * IAMLoader._byteCount_(rangeSizeType));
				rangeOffset = IAMLoader.sizeArray(array.section(offset, rangeValue), rangeSizeType).section(0, rangeMask + 2);
				offset += rangeValue;
				if (array.length() <= offset) throw new IAMException(IAMException.INVALID_LENGTH);

				IAMLoader._checkArray_(rangeOffset);
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

				keyValue = IAMLoader._byteAlign_((entryCount + 1) * IAMLoader._byteCount_(keySizeType));
				keyLength = 0;
				keyOffset = IAMLoader.sizeArray(array.section(offset, keyValue), keySizeType).section(0, entryCount + 1);
				offset += keyValue;
				if (array.length() <= offset) throw new IAMException(IAMException.INVALID_LENGTH);

				IAMLoader._checkArray_(keyOffset);
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

			keyValue = IAMLoader._byteAlign_(keyValue * IAMLoader._byteCount_(keyDataType));
			final MMFArray keyData = IAMLoader._dataArray_(array.section(offset, keyValue), keyDataType);
			offset += keyValue;
			if (array.length() <= offset) throw new IAMException(IAMException.INVALID_LENGTH);

			int valueValue;
			final int valueLength;
			final MMFArray valueOffset;
			if (valueSizeType != 0) {

				valueValue = IAMLoader._byteAlign_((entryCount + 1) * IAMLoader._byteCount_(valueSizeType));
				valueLength = 0;
				valueOffset = IAMLoader.sizeArray(array.section(offset, valueValue), valueSizeType).section(0, entryCount + 1);
				offset += valueValue;
				if (array.length() < offset) throw new IAMException(IAMException.INVALID_LENGTH);

				IAMLoader._checkArray_(valueOffset);
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

			valueValue = IAMLoader._byteAlign_(valueValue * IAMLoader._byteCount_(valueDataType));
			final MMFArray valueData = IAMLoader._dataArray_(array.section(offset, valueValue), valueDataType);
			offset += valueValue;
			if (array.length() != offset) throw new IAMException(IAMException.INVALID_LENGTH);

			this._keyData_ = keyData;
			this._keyOffset_ = keyOffset;
			this._keyLength_ = keyLength;
			this._valueData_ = valueData;
			this._valueOffset_ = valueOffset;
			this._valueLength_ = valueLength;
			this._rangeMask_ = rangeMask;
			this._rangeOffset_ = rangeOffset;
			this._entryCount_ = entryCount;

		}

		{}

		/** {@inheritDoc} */
		@Override
		public final boolean mode() {
			return this._rangeMask_ != 0;
		}

		{}

		/** {@inheritDoc} */
		@Override
		public final MMFArray key(final int entryIndex) {
			if ((entryIndex < 0) || (entryIndex >= this._entryCount_)) return MMFArray.EMPTY;
			final IAMArray keyOffset = this._keyOffset_;
			if (keyOffset != null) {
				final int offset = keyOffset._get_(entryIndex);
				final int length = keyOffset._get_(entryIndex + 1) - offset;
				return this._keyData_.section(offset, length);
			} else {
				final int length = this._keyLength_;
				final int offset = length * entryIndex;
				return this._keyData_.section(offset, length);
			}
		}

		/** {@inheritDoc} */
		@Override
		public final MMFArray value(final int entryIndex) {
			if ((entryIndex < 0) || (entryIndex >= this._entryCount_)) return MMFArray.EMPTY;
			final IAMArray keyOffset = this._valueOffset_;
			if (keyOffset != null) {
				final int offset = keyOffset._get_(entryIndex);
				final int length = keyOffset._get_(entryIndex + 1) - offset;
				return this._valueData_.section(offset, length);
			} else {
				final int length = this._valueLength_;
				final int offset = length * entryIndex;
				return this._valueData_.section(offset, length);
			}
		}

		/** {@inheritDoc} */
		@Override
		public final int entryCount() {
			return this._entryCount_;
		}

		/** {@inheritDoc} */
		@Override
		public final int find(final IAMArray key) throws NullPointerException {
			if (key == null) throw new NullPointerException("key = null");
			int i = this._rangeMask_;
			if (i != 0) {
				final IAMArray range = this._rangeOffset_;
				i = key.hash() & i;
				for (int l = range._get_(i), r = range._get_(i + 1); l < r; l++) {
					if (this.key(l).equals(key)) return l;
				}
			} else {
				int l = 0, r = this._entryCount_;
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

	{}

	/** Diese Methode gibt den Speicherbereich der gegebenen Zahlenfolge als {@link MMFArray} mit {@code UINT8}, {@code UINT16} bzw. {@code INT32} Zahlen zurück.
	 *
	 * @see MMFArray#toUINT8()
	 * @see MMFArray#toUINT16()
	 * @see MMFArray#toINT32()
	 * @param array Zahlenfolge.
	 * @param sizeType Größentyp ({@code 1..3}).
	 * @return Folge von {@code UINT8}, {@code UINT16} bzw. {@code INT32} Zahlen.
	 * @throws IllegalArgumentException Wenn der gegebene Größentyp ungültig ist. */
	public static MMFArray sizeArray(final MMFArray array, final int sizeType) throws IllegalArgumentException {
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

	/** Diese Methode gibt den Speicherbereich der gegebenen Zahlenfolge als {@link MMFArray} mit {@code INT8}, {@code INT16} bzw. {@code INT32} Zahlen zurück.
	 *
	 * @see MMFArray#toINT8()
	 * @see MMFArray#toINT16()
	 * @see MMFArray#toINT32()
	 * @param array Zahlenfolge.
	 * @param dataType Datentyp ({@code 1..3}).
	 * @return Folge von {@code INT8}, {@code INT16} bzw. {@code INT32} Zahlen.
	 * @throws IllegalArgumentException Wenn der gegebene Datentyp ungültig ist. */
	static MMFArray _dataArray_(final MMFArray array, final int dataType) throws IllegalArgumentException {
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

	/** Diese Methode prüft die Monotonität der gegebenen Zahlenfolge.
	 *
	 * @param array Zahlenfolge.
	 * @throws IAMException Wenn die erste Zahl nicht {@code 0} ist oder die Zahlen nicht monoton steigen. */
	static void _checkArray_(final IAMArray array) throws IAMException {
		int value = array.get(0);
		if (value != 0) throw new IAMException(IAMException.INVALID_OFFSET);
		for (int i = 0, length = array.length(); i < length; i++) {
			final int value2 = array.get(i);
			if (value > value2) throw new IAMException(IAMException.INVALID_OFFSET);
			value = value2;
		}
	}

	/** Diese Methode gibt die kleinste Länge eines {@code INT32} Arrays zurück, in dessen Speicherbereich ein {@code INT8} Array mit der gegebenen Länge passen.
	 *
	 * @param byteCount Länge eines {@code INT8} Arrays.
	 * @return Länge des {@code INT32} Arrays. */
	static int _byteAlign_(final int byteCount) {
		return (byteCount + 3) >> 2;
	}

	/** Diese Methode gibt die Byteanzahl des gegebenen Datengrößentyps zurück.
	 *
	 * @param dataType Datengrößentyps ({@code 1}, {@code 2} oder {@code 3}).
	 * @return Byteanzahl ({@code 1}, {@code 2} oder {@code 4}). */
	static int _byteCount_(final int dataType) {
		return (1 << dataType) >> 1;
	}

}
