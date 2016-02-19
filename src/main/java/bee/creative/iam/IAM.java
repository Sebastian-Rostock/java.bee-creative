package bee.creative.iam;

import bee.creative.iam.IAMDecoder.IAMIndexDecoder;
import bee.creative.iam.IAMDecoder.IAMListDecoder;
import bee.creative.iam.IAMDecoder.IAMMapDecoder;
import bee.creative.iam.IAMEncoder.IAMIndexEncoder;
import bee.creative.iam.IAMEncoder.IAMListEncoder;
import bee.creative.iam.IAMEncoder.IAMMapEncoder;
import bee.creative.mmf.MMFArray;

/** Diese Klasse implementiert grundlegende Klassen und Methoden zur Umsetzung des {@code IAM - Integer Array Model}.
 * 
 * @see IAMMap
 * @see IAMList
 * @see IAMIndex
 * @see IAMArray
 * @see IAMEntry
 * @see IAMEncoder
 * @see IAMMapEncoder
 * @see IAMListEncoder
 * @see IAMIndexEncoder
 * @see IAMDecoder
 * @see IAMMapDecoder
 * @see IAMListDecoder
 * @see IAMIndexDecoder
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class IAM {

	/** Diese Methode gibt den Speicherbereich der gegebenen Zahlenfolge als {@link MMFArray} mit {@code UINT8}, {@code UINT16} bzw. {@code INT32} Zahlen zurück.
	 * 
	 * @see MMFArray#toUINT8()
	 * @see MMFArray#toUINT16()
	 * @see MMFArray#toINT32()
	 * @param array Zahlenfolge.
	 * @param sizeType Größentyp ({@code 1..3}).
	 * @return Folge von {@code UINT8}, {@code UINT16} bzw. {@code INT32} Zahlen.
	 * @throws IllegalArgumentException Wenn der gegebene Größentyp ungültig ist. */
	static final MMFArray _sizeArray_(final MMFArray array, final int sizeType) throws IllegalArgumentException {
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
	static final MMFArray _dataArray_(final MMFArray array, final int dataType) throws IllegalArgumentException {
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
	static final void _checkArray_(final IAMArray array) throws IAMException {
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
	static final int _byteAlign_(final int byteCount) {
		return (byteCount + 3) >> 2;
	}

	/** Diese Methode gibt die Byteanzahl des gegebenen Datengrößentyps zurück.
	 * 
	 * @param dataType Datengrößentyps ({@code 1}, {@code 2} oder {@code 3}).
	 * @return Byteanzahl ({@code 1}, {@code 2} oder {@code 4}). */
	static final int _byteCount_(final int dataType) {
		return (1 << dataType) >> 1;
	}

	/** Diese Methode gibt einen neuen {@link IAMIndexEncoder} zurück.
	 * 
	 * @see IAMIndexEncoder#IAMIndexEncoder()
	 * @return neuer {@link IAMIndexEncoder}. */
	public static final IAMIndexEncoder newEncoder() {
		return new IAMIndexEncoder();
	}

	/** Diese Methode gibt einen neuen {@link IAMIndexDecoder} zurück.
	 * 
	 * @see IAMIndexDecoder#IAMIndexDecoder(MMFArray)
	 * @param array Speicherbereich.
	 * @return neuer {@link IAMIndexDecoder}.
	 * @throws IAMException Wenn beim dekodieren des Speicherbereichs ein Fehler erkannt wird.
	 * @throws NullPointerException Wenn {@code array} {@code null} ist. */
	public static final IAMIndexDecoder newDecoder(final MMFArray array) throws IAMException, NullPointerException {
		return new IAMIndexDecoder(array);
	}

}