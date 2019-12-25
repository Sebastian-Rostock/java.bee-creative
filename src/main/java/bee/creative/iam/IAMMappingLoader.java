package bee.creative.iam;

import bee.creative.emu.EMU;
import bee.creative.emu.Emuable;
import bee.creative.lang.Objects;
import bee.creative.mmi.MMIArray;

/** Diese Klasse implementiert ein {@link IAMMapping}, das seine Daten aus einem {@link IAMArray} dekodiert.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class IAMMappingLoader extends IAMMapping implements Emuable {

	/** Dieses Feld speichert den leeren {@link IAMMappingLoader}. */
	public static final IAMMappingLoader EMPTY = new IAMMappingLoader();

	/** Dieses Feld speichert den {@link IAMHeader} einer {@code IAM_MAPPING} Datenstruktur. */
	public static final IAMHeader HEADER = new IAMHeader(0xFFFFFC00, 0xF00D1000);

	/** Dieses Feld speichert die Zahlen der Schlüssel. */
	final IAMArray keyData;

	/** Dieses Feld speichert die Startpositionen der Schlüssel. */
	final IAMArray keyOffset;

	/** Dieses Feld speichert die Länge der Schlüssel. */
	final int keyLength;

	/** Dieses Feld speichert die Zahlen der Werte. */
	final IAMArray valueData;

	/** Dieses Feld speichert die Startpositionen der Werte. */
	final IAMArray valueOffset;

	/** Dieses Feld speichert die Länge der Werte. */
	final int valueLength;

	/** Dieses Feld speichert die Bitmaske der Schlüsselbereiche. */
	final int rangeMask;

	/** Dieses Feld speichert die Startpositionen der Schlüsselbereiche. */
	final IAMArray rangeOffset;

	/** Dieses Feld speichert die Anzahl der Einträge. */
	final int entryCount;

	/** Dieser Konstruktor initialisiert die leere Abbildung. */
	IAMMappingLoader() {
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

	/** Dieser Kontrukteur initialisiert diese {@link IAMMapping} als Sicht auf den gegebenen Speicherbereich.
	 *
	 * @param array Speicherbereich mit {@code INT32} Zahlen.
	 * @throws IAMException Wenn beim dekodieren des Speicherbereichs ein Fehler erkannt wird.
	 * @throws NullPointerException Wenn {@code array} {@code null} ist. */
	public IAMMappingLoader(MMIArray array) throws IAMException, NullPointerException {
		array = array.asINT32();
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
		final IAMArray rangeOffset;
		if (rangeSizeType != 0) {

			if (array.length() <= offset) throw new IAMException(IAMException.INVALID_LENGTH);

			rangeMask = array.get(offset);
			offset++;
			if ((rangeMask < 1) || (rangeMask > 0x1FFFFFFF) || (((rangeMask + 1) & rangeMask) != 0)) throw new IAMException(IAMException.INVALID_VALUE);

			rangeValue = IAMIndexLoader.byteAlign((rangeMask + 2) * IAMIndexLoader.byteCount(rangeSizeType));
			rangeOffset = IAMIndexLoader.sizeArray(array.section(offset, rangeValue), rangeSizeType).section(0, rangeMask + 2);
			offset += rangeValue;
			if (array.length() <= offset) throw new IAMException(IAMException.INVALID_LENGTH);

			// IAMLoader.checkArray(rangeOffset);
			rangeValue = rangeOffset.get(rangeMask + 1);
			if (rangeValue != entryCount) throw new IAMException(IAMException.INVALID_OFFSET);

		} else {

			rangeMask = 0;
			rangeOffset = null;

		}

		if (array.length() <= offset) throw new IAMException(IAMException.INVALID_LENGTH);

		int keyValue;
		final int keyLength;
		final IAMArray keyOffset;
		if (keySizeType != 0) {

			keyValue = IAMIndexLoader.byteAlign((entryCount + 1) * IAMIndexLoader.byteCount(keySizeType));
			keyLength = 0;
			keyOffset = IAMIndexLoader.sizeArray(array.section(offset, keyValue), keySizeType).section(0, entryCount + 1);
			offset += keyValue;
			if (array.length() <= offset) throw new IAMException(IAMException.INVALID_LENGTH);

			// IAMLoader.checkArray(keyOffset);
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

		keyValue = IAMIndexLoader.byteAlign(keyValue * IAMIndexLoader.byteCount(keyDataType));
		final IAMArray keyData = IAMIndexLoader.dataArray(array.section(offset, keyValue), keyDataType);
		offset += keyValue;
		if (array.length() <= offset) throw new IAMException(IAMException.INVALID_LENGTH);

		int valueValue;
		final int valueLength;
		final IAMArray valueOffset;
		if (valueSizeType != 0) {

			valueValue = IAMIndexLoader.byteAlign((entryCount + 1) * IAMIndexLoader.byteCount(valueSizeType));
			valueLength = 0;
			valueOffset = IAMIndexLoader.sizeArray(array.section(offset, valueValue), valueSizeType).section(0, entryCount + 1);
			offset += valueValue;
			if (array.length() < offset) throw new IAMException(IAMException.INVALID_LENGTH);

			// IAMLoader.checkArray(valueOffset);
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

		valueValue = IAMIndexLoader.byteAlign(valueValue * IAMIndexLoader.byteCount(valueDataType));
		final IAMArray valueData = IAMIndexLoader.dataArray(array.section(offset, valueValue), valueDataType);
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

	@Override
	public long emu() {
		return EMU.fromObject(this) + EMU.fromAll(this.keyData, this.keyOffset, this.valueData, this.valueOffset, this.rangeOffset);
	}

	/** Diese Methode prüft die Kodierung der {@link #keyLength(int) Längen der Schlüssel} und {@link #valueLength(int) Werte}.
	 *
	 * @see IAMIndexLoader#checkArray(IAMArray)
	 * @throws IAMException Wenn die Kodierung ungültig ist. */
	public final void check() throws IAMException {
		IAMIndexLoader.checkArray(this.rangeOffset);
		IAMIndexLoader.checkArray(this.keyOffset);
		IAMIndexLoader.checkArray(this.valueOffset);
	}

	@Override
	public final boolean mode() {
		return this.rangeMask != 0;
	}

	@Override
	public final IAMArray key(final int entryIndex) {
		if ((entryIndex < 0) || (entryIndex >= this.entryCount)) return IAMArray.EMPTY;
		final IAMArray keyOffset = this.keyOffset;
		if (keyOffset != null) {
			final int offset = keyOffset.customGet(entryIndex);
			final int length = keyOffset.customGet(entryIndex + 1) - offset;
			return this.keyData.section(offset, length);
		} else {
			final int length = this.keyLength;
			final int offset = length * entryIndex;
			return this.keyData.section(offset, length);
		}
	}

	@Override
	public final IAMArray value(final int entryIndex) {
		if ((entryIndex < 0) || (entryIndex >= this.entryCount)) return IAMArray.EMPTY;
		final IAMArray keyOffset = this.valueOffset;
		if (keyOffset != null) {
			final int offset = keyOffset.customGet(entryIndex);
			final int length = keyOffset.customGet(entryIndex + 1) - offset;
			return this.valueData.section(offset, length);
		} else {
			final int length = this.valueLength;
			final int offset = length * entryIndex;
			return this.valueData.section(offset, length);
		}
	}

	@Override
	public final int entryCount() {
		return this.entryCount;
	}

	@Override
	public final int find(final IAMArray key) throws NullPointerException {
		Objects.notNull(key);
		int i = this.rangeMask;
		if (i != 0) {
			final IAMArray range = this.rangeOffset;
			i = key.hash() & i;
			for (int l = range.customGet(i), r = range.customGet(i + 1); l < r; l++) {
				if (this.key(l).equals(key)) return l;
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

}