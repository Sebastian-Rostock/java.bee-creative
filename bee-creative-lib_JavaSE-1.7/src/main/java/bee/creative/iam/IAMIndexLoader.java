package bee.creative.iam;

import bee.creative.emu.EMU;
import bee.creative.emu.Emuable;
import bee.creative.mmi.MMIArray;

/** Diese Klasse implementiert einen {@link IAMIndex}, der seine Daten aus einem {@link IAMArray} dekodiert.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class IAMIndexLoader extends IAMIndex implements Emuable {

	/** Dieses Feld speichert den leeren {@link IAMIndexLoader}. */
	public static final IAMIndexLoader EMPTY = new IAMIndexLoader();

	/** Dieses Feld speichert den {@link IAMHeader} einer {@code IAM_INDEX} Datenstruktur. */
	public static final IAMHeader HEADER = new IAMHeader(0xFFFFFFFF, 0xF00DBA5E);

	/** Diese Methode gibt den Speicherbereich der gegebenen Zahlenfolge als {@link IAMArray} mit {@code UINT8}, {@code UINT16} bzw. {@code INT32} Zahlen zurück.
	 *
	 * @see MMIArray#asUINT8()
	 * @see MMIArray#asUINT16()
	 * @see MMIArray#asINT32()
	 * @param array Zahlenfolge.
	 * @param sizeType Größentyp ({@code 1..3}).
	 * @return Folge von {@code UINT8}, {@code UINT16} bzw. {@code INT32} Zahlen.
	 * @throws IllegalArgumentException Wenn der gegebene Größentyp ungültig ist. */
	public static MMIArray sizeArray(final MMIArray array, final int sizeType) throws IllegalArgumentException {
		if (sizeType == 1) return array.asUINT8();
		if (sizeType == 2) return array.asUINT16();
		if (sizeType == 3) return array.asINT32();
		throw new IllegalArgumentException();
	}

	/** Diese Methode gibt den Speicherbereich der gegebenen Zahlenfolge als {@link IAMArray} mit {@code INT8}, {@code INT16} bzw. {@code INT32} Zahlen zurück.
	 *
	 * @see MMIArray#asINT8()
	 * @see MMIArray#asINT16()
	 * @see MMIArray#asINT32()
	 * @param array Zahlenfolge.
	 * @param dataType Datentyp ({@code 1..3}).
	 * @return Folge von {@code INT8}, {@code INT16} bzw. {@code INT32} Zahlen.
	 * @throws IllegalArgumentException Wenn der gegebene Datentyp ungültig ist. */
	public static MMIArray dataArray(final MMIArray array, final int dataType) throws IllegalArgumentException {
		if (dataType == 1) return array.asINT8();
		if (dataType == 2) return array.asINT16();
		if (dataType == 3) return array.asINT32();
		throw new IllegalArgumentException();
	}

	/** Diese Methode prüft die Monotonität der gegebenen Zahlenfolge.
	 *
	 * @param array Zahlenfolge.
	 * @throws IAMException Wenn die erste Zahl nicht {@code 0} ist oder die Zahlen nicht monoton steigen. */
	public static void checkArray(final IAMArray array) throws IAMException {
		if (array == null) return;
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
	public static int byteAlign(final int byteCount) {
		return (byteCount + 3) >> 2;
	}

	/** Diese Methode gibt die Byteanzahl des gegebenen Datengrößentyps zurück.
	 *
	 * @param dataType Datengrößentyps ({@code 1}, {@code 2} oder {@code 3}).
	 * @return Byteanzahl ({@code 1}, {@code 2} oder {@code 4}). */
	public static int byteCount(final int dataType) {
		return (1 << dataType) >> 1;
	}

	/** Dieses Feld speichert die Abbildungen. */
	final IAMMappingLoader[] mappings;

	/** Dieses Feld speichert die Auflistungen. */
	final IAMListingLoader[] listings;

	/** Dieser Konstruktor initialisiert das leere Inhaltsverzeichnis. */
	IAMIndexLoader() {
		this.mappings = new IAMMappingLoader[0];
		this.listings = new IAMListingLoader[0];
	}

	/** Dieser Kontrukteur initialisiert diesen {@link IAMIndex} als Sicht auf den gegebenen Speicherbereich.
	 *
	 * @param array Speicherbereich.
	 * @throws IAMException Wenn beim dekodieren des Speicherbereichs ein Fehler erkannt wird.
	 * @throws NullPointerException Wenn {@code array} {@code null} ist. */
	public IAMIndexLoader(MMIArray array) throws IAMException, NullPointerException {
		array = array.asINT32();
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

		final MMIArray mappingData = array.section(offset, mappingDataLength).asINT32();
		offset += mappingDataLength;
		if (array.length() < offset) throw new IAMException(IAMException.INVALID_LENGTH);

		final MMIArray listingData = array.section(offset, listingDataLength).asINT32();
		offset += listingDataLength;
		if (array.length() != offset) throw new IAMException(IAMException.INVALID_LENGTH);

		this.mappings = new IAMMappingLoader[mappingCount];
		for (int i = 0; i < mappingCount; i++) {
			final int offset2 = mappingOffset.get(i);
			final int length2 = mappingOffset.get(i + 1) - offset2;
			this.mappings[i] = new IAMMappingLoader(mappingData.section(offset2, length2).asINT32());
		}

		this.listings = new IAMListingLoader[listingCount];
		for (int i = 0; i < listingCount; i++) {
			final int offset2 = listingOffset.get(i);
			final int length2 = listingOffset.get(i + 1) - offset2;
			this.listings[i] = new IAMListingLoader(listingData.section(offset2, length2).asINT32());
		}

	}

	@Override
	public long emu() {
		return EMU.fromObject(this) + EMU.fromArray(this.mappings) + EMU.fromArray(this.listings) + EMU.fromAll(this.mappings) + EMU.fromAll(this.listings);
	}

	/** Diese Methode prüft die Kodierung der Längen der Zahlenfolgen in den Abbildungen und Auflistungen.
	 *
	 * @see IAMListingLoader#check()
	 * @see IAMMappingLoader#check()
	 * @throws IAMException Wenn die Kodierung ungültig ist. */
	public final void check() throws IAMException {
		for (final IAMListingLoader listing: this.listings) {
			listing.check();
		}
		for (final IAMMappingLoader mapping: this.mappings) {
			mapping.check();
		}
	}

	@Override
	public final IAMMappingLoader mapping(final int index) {
		if ((index < 0) || (index >= this.mappings.length)) return IAMMappingLoader.EMPTY;
		return this.mappings[index];
	}

	@Override
	public final int mappingCount() {
		return this.mappings.length;
	}

	@Override
	public final IAMListingLoader listing(final int index) {
		if ((index < 0) || (index >= this.listings.length)) return IAMListingLoader.EMPTY;
		return this.listings[index];
	}

	@Override
	public final int listingCount() {
		return this.listings.length;
	}

}