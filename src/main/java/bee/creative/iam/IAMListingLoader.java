package bee.creative.iam;

import bee.creative.emu.EMU;
import bee.creative.emu.Emuable;
import bee.creative.mmi.MMIArray;

/** Diese Klasse implementiert ein {@link IAMListing}, das seine Daten aus einem {@link MMIArray} dekodiert.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class IAMListingLoader extends IAMListing implements Emuable {

	/** Dieses Feld speichert den leeren {@link IAMListingLoader}. */
	public static final IAMListingLoader EMPTY = new IAMListingLoader();

	/** Dieses Feld speichert den {@link IAMHeader} einer {@code IAM_LISTING} Datenstruktur. */
	public static final IAMHeader HEADER = new IAMHeader(0xFFFFFFF0, 0xF00D2000);

	/** Dieses Feld speichert die Zahlen der Elemente. */
	final MMIArray itemData;

	/** Dieses Feld speichert die Startpositionen der Elemente. */
	final MMIArray itemOffset;

	/** Dieses Feld speichert die Länge der Elemente. */
	final int itemLength;

	/** Dieses Feld speichert die Anzahl der Elemente. */
	final int itemCount;

	/** Dieser Konstruktor initialisiert die leere Auflistung. */
	IAMListingLoader() {
		this.itemData = null;
		this.itemOffset = null;
		this.itemLength = 0;
		this.itemCount = 0;
	}

	/** Dieser Kontrukteur initialisiert diese {@link IAMListing} als Sicht auf den gegebenen Speicherbereich.
	 *
	 * @param array Speicherbereich mit {@code INT32} Zahlen.
	 * @throws IAMException Wenn beim dekodieren des Speicherbereichs ein Fehler erkannt wird.
	 * @throws NullPointerException Wenn {@code array} {@code null} ist. */
	public IAMListingLoader(MMIArray array) throws IAMException, NullPointerException {
		array = array.asINT32();
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
		final MMIArray itemOffset;
		if (itemSizeType != 0) {

			itemValue = IAMIndexLoader.byteAlign((itemCount + 1) * IAMIndexLoader.byteCount(itemSizeType));
			itemLength = 0;
			itemOffset = IAMIndexLoader.sizeArray(array.section(offset, itemValue), itemSizeType).section(0, itemCount + 1);
			offset += itemValue;
			if (array.length() < offset) throw new IAMException(IAMException.INVALID_LENGTH);

			// IAMLoader.checkArray(itemOffset);
			itemValue = itemOffset.get(itemCount);

		} else {

			itemValue = array.get(offset);
			offset++;
			if ((itemValue < 0) || (itemValue > 0x3FFFFFFF)) throw new IAMException(IAMException.INVALID_VALUE);

			itemLength = itemValue;
			itemValue = itemCount * itemValue;
			itemOffset = null;

		}

		itemValue = IAMIndexLoader.byteAlign(itemValue * IAMIndexLoader.byteCount(itemDataType));
		final MMIArray itemData = IAMIndexLoader.dataArray(array.section(offset, itemValue), itemDataType);
		offset += itemValue;
		if (array.length() != offset) throw new IAMException(IAMException.INVALID_LENGTH);

		this.itemData = itemData;
		this.itemOffset = itemOffset;
		this.itemLength = itemLength;
		this.itemCount = itemCount;

	}

	@Override
	public long emu() {
		return EMU.fromObject(this) + EMU.fromAll(this.itemData, this.itemOffset);
	}

	/** Diese Methode prüft die Kodierung der {@link #itemLength(int) Längen der Zahlenfolgen}.
	 *
	 * @see IAMIndexLoader#checkArray(IAMArray)
	 * @throws IAMException Wenn die Kodierung ungültig ist. */
	public final void check() throws IAMException {
		IAMIndexLoader.checkArray(this.itemOffset);
	}

	@Override
	public final MMIArray item(final int itemIndex) {
		if ((itemIndex < 0) || (itemIndex >= this.itemCount)) return MMIArray.EMPTY;
		final MMIArray itemOffset = this.itemOffset;
		if (itemOffset != null) {
			final int offset = itemOffset.customGet(itemIndex);
			final int length = itemOffset.customGet(itemIndex + 1) - offset;
			return this.itemData.section(offset, length);
		} else {
			final int length = this.itemLength;
			final int offset = length * itemIndex;
			return this.itemData.section(offset, length);
		}
	}

	@Override
	public final int itemCount() {
		return this.itemCount;
	}

}