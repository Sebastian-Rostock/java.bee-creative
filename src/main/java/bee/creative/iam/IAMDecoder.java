package bee.creative.iam;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import bee.creative.iam.IAMEncoder.IAMIndexEncoder;
import bee.creative.ini.INIWriter;
import bee.creative.mmf.MMFArray;
import bee.creative.util.Bytes;
import bee.creative.util.Objects;

/** Diese Klasse implementiert die Klassen und Methoden zur Dekodierung der {@code Integer Array Model} Datenstrukturen.
 * 
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class IAMDecoder {

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
		 * @see #orderOf(byte[])
		 * @param file Datei mit den Quelldaten.
		 * @return Bytereihenfolge oder {@code null}.
		 * @throws IOException Wenn {@link RandomAccessFile#RandomAccessFile(File, String)} bzw. {@link RandomAccessFile#read(byte[])} eine entsprechende Ausnahme
		 *         auslöst.
		 * @throws NullPointerException Wenn {@code file} {@code null} ist. */
		public final ByteOrder orderOf(final File file) throws IOException, NullPointerException {
			try (RandomAccessFile source = new RandomAccessFile(file.getAbsoluteFile(), "r")) {
				final byte[] bytes = new byte[4];
				if (source.read(bytes) < 4) return null;
				return this.orderOf(bytes);
			}
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
		 * @param buffer Puffer mit den Quelldaten.
		 * @return Bytereihenfolge oder {@code null}.
		 * @throws NullPointerException Wenn {@code buffer} {@code null} ist. */
		public final ByteOrder orderOf(final ByteBuffer buffer) throws NullPointerException {
			if (buffer.remaining() < 4) return null;
			final byte[] bytes = new byte[4];
			buffer.get(bytes);
			buffer.position(buffer.position() - 4);
			return this.orderOf(bytes);
		}

		{}

		/** {@inheritDoc} */
		@Override
		public final String toString() {
			return Integer.toHexString(this._value_);
		}

	}

	/** Diese Klasse implementiert eine {@link IAMMap}, die ihre Daten aus einem {@link MMFArray} dekodiert.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static final class IAMMapDecoder extends IAMMap {

		/** Dieses Feld speichert den leeren {@link IAMMapDecoder}. */
		public static final IAMMapDecoder EMPTY = new IAMMapDecoder();

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
		IAMMapDecoder() {
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

		/** Dieser Kontrukteur initialisiert diese {@link IAMMap} als Sicht auf den gegebenen Speicherbereich.
		 * 
		 * @param array Speicherbereich mit {@code INT32} Zahlen.
		 * @throws IAMException Wenn beim dekodieren des Speicherbereichs ein Fehler erkannt wird.
		 * @throws NullPointerException Wenn {@code array} {@code null} ist. */
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

				rangeValue = IAM._byteAlign_((rangeMask + 2) * IAM._byteCount_(rangeSizeType));
				rangeOffset = IAM._sizeArray_(array.section(offset, rangeValue), rangeSizeType).section(0, rangeMask + 2);
				offset += rangeValue;
				if (array.length() <= offset) throw new IAMException(IAMException.INVALID_LENGTH);

				IAM._checkArray_(rangeOffset);
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

				keyValue = IAM._byteAlign_((entryCount + 1) * IAM._byteCount_(keySizeType));
				keyLength = 0;
				keyOffset = IAM._sizeArray_(array.section(offset, keyValue), keySizeType).section(0, entryCount + 1);
				offset += keyValue;
				if (array.length() <= offset) throw new IAMException(IAMException.INVALID_LENGTH);

				IAM._checkArray_(keyOffset);
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

			keyValue = IAM._byteAlign_(keyValue * IAM._byteCount_(keyDataType));
			final MMFArray keyData = IAM._dataArray_(array.section(offset, keyValue), keyDataType);
			offset += keyValue;
			if (array.length() <= offset) throw new IAMException(IAMException.INVALID_LENGTH);

			int valueValue;
			final int valueLength;
			final MMFArray valueOffset;
			if (valueSizeType != 0) {

				valueValue = IAM._byteAlign_((entryCount + 1) * IAM._byteCount_(valueSizeType));
				valueLength = 0;
				valueOffset = IAM._sizeArray_(array.section(offset, valueValue), valueSizeType).section(0, entryCount + 1);
				offset += valueValue;
				if (array.length() < offset) throw new IAMException(IAMException.INVALID_LENGTH);

				IAM._checkArray_(valueOffset);
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

			valueValue = IAM._byteAlign_(valueValue * IAM._byteCount_(valueDataType));
			final MMFArray valueData = IAM._dataArray_(array.section(offset, valueValue), valueDataType);
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
				final int offset = keyOffset.get(entryIndex);
				final int length = keyOffset.get(entryIndex + 1) - offset;
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
				final int offset = keyOffset.get(entryIndex);
				final int length = keyOffset.get(entryIndex + 1) - offset;
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
				for (int l = range.get(i), r = range.get(i + 1); l < r; l++) {
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

	/** Diese Klasse implementiert eine {@link IAMList}, die ihre Daten aus einem {@link MMFArray} dekodiert.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static final class IAMListDecoder extends IAMList {

		/** Dieses Feld speichert den leeren {@link IAMListDecoder}. */
		public static final IAMListDecoder EMPTY = new IAMListDecoder();

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
		IAMListDecoder() {
			this._itemData_ = null;
			this._itemOffset_ = null;
			this._itemLength_ = 0;
			this._itemCount_ = 0;
		}

		/** Dieser Kontrukteur initialisiert diese {@link IAMList} als Sicht auf den gegebenen Speicherbereich.
		 * 
		 * @param array Speicherbereich mit {@code INT32} Zahlen.
		 * @throws IAMException Wenn beim dekodieren des Speicherbereichs ein Fehler erkannt wird.
		 * @throws NullPointerException Wenn {@code array} {@code null} ist. */
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

				itemValue = IAM._byteAlign_((itemCount + 1) * IAM._byteCount_(itemSizeType));
				itemLength = 0;
				itemOffset = IAM._sizeArray_(array.section(offset, itemValue), itemSizeType).section(0, itemCount + 1);
				offset += itemValue;
				if (array.length() < offset) throw new IAMException(IAMException.INVALID_LENGTH);

				IAM._checkArray_(itemOffset);
				itemValue = itemOffset.get(itemCount);

			} else {

				itemValue = array.get(offset);
				offset++;
				if ((itemValue < 0) || (itemValue > 0x3FFFFFFF)) throw new IAMException(IAMException.INVALID_VALUE);

				itemLength = itemValue;
				itemValue = itemCount * itemValue;
				itemOffset = null;

			}

			itemValue = IAM._byteAlign_(itemValue * IAM._byteCount_(itemDataType));
			final MMFArray itemData = IAM._dataArray_(array.section(offset, itemValue), itemDataType);
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
				final int offset = itemOffset.get(itemIndex);
				final int length = itemOffset.get(itemIndex + 1) - offset;
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

	/** Diese Klasse implementiert einen {@link IAMIndex}, der seine Daten aus einem {@link MMFArray} dekodiert.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static final class IAMIndexDecoder extends IAMIndex {

		/** Dieses Feld speichert den leeren {@link IAMIndexDecoder}. */
		public static final IAMIndexDecoder EMPTY = new IAMIndexDecoder();

		/** Dieses Feld speichert den {@link IAMHeader} einer {@code IAM_INDEX} Datenstruktur. */
		public static final IAMHeader HEADER = new IAMHeader(0xFFFFFFFF, 0xF00DBA5E);

		{}

		/** Dieses Feld speichert die Abbildungen. */
		final IAMMapDecoder[] _maps_;

		/** Dieses Feld speichert die Listen. */
		final IAMListDecoder[] _lists_;

		/** Dieser Konstruktor initialisiert das leere Inhaltsverzeichnis. */
		IAMIndexDecoder() {
			this._maps_ = new IAMMapDecoder[0];
			this._lists_ = new IAMListDecoder[0];
		}

		/** Dieser Kontrukteur initialisiert diesen {@link IAMIndex} als Sicht auf den gegebenen Speicherbereich.
		 * 
		 * @param array Speicherbereich.
		 * @throws IAMException Wenn beim dekodieren des Speicherbereichs ein Fehler erkannt wird.
		 * @throws NullPointerException Wenn {@code array} {@code null} ist. */
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

			this._maps_ = new IAMMapDecoder[mapCount];
			for (int i = 0; i < mapCount; i++) {
				final int offset2 = mapOffset.get(i);
				final int length2 = mapOffset.get(i + 1) - offset2;
				this._maps_[i] = new IAMMapDecoder(mapData.section(offset2, length2).toINT32());
			}

			this._lists_ = new IAMListDecoder[listCount];
			for (int i = 0; i < listCount; i++) {
				final int offset2 = listOffset.get(i);
				final int length2 = listOffset.get(i + 1) - offset2;
				this._lists_[i] = new IAMListDecoder(listData.section(offset2, length2).toINT32());
			}

		}

		{}

		/** {@inheritDoc} */
		@Override
		public final IAMMapDecoder map(final int index) {
			if ((index < 0) || (index >= this._maps_.length)) return IAMMapDecoder.EMPTY;
			return this._maps_[index];
		}

		/** {@inheritDoc} */
		@Override
		public final int mapCount() {
			return this._maps_.length;
		}

		/** {@inheritDoc} */
		@Override
		public final IAMListDecoder list(final int index) {
			if ((index < 0) || (index >= this._lists_.length)) return IAMListDecoder.EMPTY;
			return this._lists_[index];
		}

		/** {@inheritDoc} */
		@Override
		public final int listCount() {
			return this._lists_.length;
		}

	}

	{}

	@SuppressWarnings ("javadoc")
	static final String _formatInt_(final int value) {
		return Integer.toString(value);
	}

	@SuppressWarnings ("javadoc")
	static final String _formatArray_(final int[] value) {
		final int length = value.length;
		if (length == 0) return "";
		final StringBuilder result = new StringBuilder().append(value[0]);
		for (int index = 1; index < length; index++) {
			result.append('/').append(value[index]);
		}
		return result.toString();
	}

	@SuppressWarnings ("javadoc")
	static final String _formatMode_(final boolean mode) {
		return mode ? "H" : "S";
	}

	@SuppressWarnings ("javadoc")
	static final String _formatOrder_(final ByteOrder value) {
		return value == ByteOrder.BIG_ENDIAN ? "B" : "L";
	}

	{}

	/** Dieses Feld speichert die Bytereihenfolge. */
	ByteOrder _order_;

	/** Dieses Feld speichert die Eingabedaten. */
	Object _source_;

	/** Dieses Feld speichert die Ausgabedaten. */
	Object _target_;

	{}

	/** Diese Methode gibt die Bytereihenfolge zurück.
	 * 
	 * @see #useOrder(ByteOrder)
	 * @return Bytereihenfolge. */
	public final ByteOrder getOrder() {
		return this._order_;
	}

	/** Diese Methode setzt die Bytereihenfolge und gibt {@code this} zurück.<br>
	 * Wenn diese {@code null} ist, wird {@link ByteOrder#nativeOrder()} verwendet.
	 * 
	 * @see IAMIndexEncoder#encode(ByteOrder)
	 * @param order Bytereihenfolge.
	 * @return {@code this}. */
	public final IAMDecoder useOrder(final ByteOrder order) {
		this._order_ = order;
		return this;
	}

	/** Diese Methode gibt die Eingabedaten zurück.
	 * 
	 * @see #useSource(Object)
	 * @return Eingabedaten. */
	public final Object getSource() {
		return this._source_;
	}

	/** Diese Methode setzt die Eingabedaten (INI-Dokument) und gibt {@code this} zurück.
	 * 
	 * @see MMFArray#from(Object)
	 * @param source Eingabedaten.
	 * @return {@code this}. */
	public final IAMDecoder useSource(final Object source) {
		this._source_ = source;
		return this;
	}

	/** Diese Methode gibt die Ausgabedaten zurück.
	 * 
	 * @see #useTarget(Object)
	 * @return Ausgabedaten. */
	public final Object getTarget() {
		return this._target_;
	}

	/** Diese Methode setzt die Ausgabedaten (IAM-Dokument) und gibt {@code this} zurück.
	 * 
	 * @see INIWriter#from(Object)
	 * @param target Ausgabedaten.
	 * @return {@code this}. */
	public final IAMDecoder useTarget(final Object target) {
		this._target_ = target;
		return this;
	}

	/** Diese Methode überführt die {@link #getSource() Eingabedaten} (INI-Dokument) in die {@link #getTarget() Ausgabedaten} (IAM-Dokument) und gibt {@code this}
	 * zurück.
	 * 
	 * @see #decodeSource()
	 * @see #decodeTarget(IAMIndex)
	 * @return {@code this}.
	 * @throws IOException Wenn {@link #decodeSource()} bzw. {@link #decodeTarget(IAMIndex)} eine entsprechende Ausnahme auslöst.
	 * @throws IllegalArgumentException Wenn {@link #decodeSource()} eine entsprechende Ausnahme auslöst. */
	public final IAMDecoder decode() throws IOException, IllegalArgumentException {
		return this.decodeTarget(this.decodeSource());
	}

	/** Diese Methode überführt die {@link #getSource() Eingabedaten} in einen {@link IAMIndexEncoder} und gibt diesen zurück.<br>
	 * Hierbei wird die {@link #getOrder() Bytereihenfolge} aktualisiert.
	 * 
	 * @return {@link IAMIndexEncoder}.
	 * @throws IOException Wenn die Eingabedaten nicht gelesen werden können.
	 * @throws IAMException Wenn die Struktur der Eingabedaten ungültig ist. */
	public final IAMIndexDecoder decodeSource() throws IOException, IAMException {
		final MMFArray array = MMFArray.from(this._source_);
		return new IAMIndexDecoder(array.withOrder(IAMIndexDecoder.HEADER.orderOf(array.toINT8().section(0, 4).toBytes())));
	}

	/** Diese Methode überführt den gegebenen {@link IAMIndex} in die {@link #getTarget() Ausgabedaten} (INI-Dokument) und gibt {@code this} zurück.
	 * 
	 * @param source {@link IAMIndex}.
	 * @return {@code this}.
	 * @throws IOException Wenn die Ausgabedaten nicht erzeigt oder geschrieben werden können. */
	public final IAMDecoder decodeTarget(final IAMIndex source) throws IOException {
		final ByteOrder order = this._order_;
		try (final INIWriter target = INIWriter.from(this._target_)) {
			target.writeSection("IAM_INDEX");
			target.writeProperty("mapCount", IAMDecoder._formatInt_(source.mapCount()));
			target.writeProperty("listCount", IAMDecoder._formatInt_(source.listCount()));
			target.writeProperty("byteOrder", IAMDecoder._formatOrder_(order));
			for (int mapIndex = 0, mapCount = source.mapCount(); mapIndex < mapCount; mapIndex++) {
				final IAMMap map = source.map(mapIndex);
				target.writeSection("IAM_MAP/".concat(IAMDecoder._formatInt_(mapIndex)));
				target.writeProperty("findMode", IAMDecoder._formatMode_(map.mode()));
				for (int entryIndex = 0, entryCount = map.entryCount(); entryIndex < entryCount; entryIndex++) {
					target.writeProperty(IAMDecoder._formatArray_(map.key(entryIndex).toArray()), IAMDecoder._formatArray_(map.value(entryIndex).toArray()));
				}
			}
			for (int listIndex = 0, listCount = source.listCount(); listIndex < listCount; listIndex++) {
				final IAMList list = source.list(listIndex);
				target.writeSection("IAM_LIST/".concat(IAMDecoder._formatInt_(listIndex)));
				for (int itemIndex = 0, itemCount = list.itemCount(); itemIndex < itemCount; itemIndex++) {
					target.writeProperty(IAMDecoder._formatInt_(itemIndex), IAMDecoder._formatArray_(list.item(itemIndex).toArray()));
				}
			}
		}
		return this;
	}

	{}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return Objects.toInvokeString(this, this._order_, this._source_, this._target_);
	}

}
