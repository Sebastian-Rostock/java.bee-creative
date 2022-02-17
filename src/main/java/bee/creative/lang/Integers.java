package bee.creative.lang;

/** Diese Klasse stellt Methoden zum Parsen und Formatieren von Dezimalzahlen zur Verfügung.
 *
 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Integers {

	final static byte[] digitTenArray = {'0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '2', '2', '2', '2',
		'2', '2', '2', '2', '2', '2', '3', '3', '3', '3', '3', '3', '3', '3', '3', '3', '4', '4', '4', '4', '4', '4', '4', '4', '4', '4', '5', '5', '5', '5', '5',
		'5', '5', '5', '5', '5', '6', '6', '6', '6', '6', '6', '6', '6', '6', '6', '7', '7', '7', '7', '7', '7', '7', '7', '7', '7', '8', '8', '8', '8', '8', '8',
		'8', '8', '8', '8', '9', '9', '9', '9', '9', '9', '9', '9', '9', '9'};

	final static byte[] digitOneArray = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3',
		'4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4',
		'5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5',
		'6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

	final static String[] sizeUnitArray = {" B", " KB", " MB", " GB", " TB", " PB", " EB"};

	final static String[] timeUnitArray = {" ns", " µs", " ms", " s", " ks", " MS", " GS"};

	/** Diese Methode gibt die Anzahl an Zeichen zurück, die zur Darstellung der gegebenen positiven Dezimalzahl nötig sind.
	 * <p>
	 * <b>Ungültige Eingaben werden nicht geprüft!</b>
	 *
	 * @param value positive Dezimalzahl.
	 * @return Zeichenanzahl. */
	public static int getSize(final int value) {
		return (value > 99999 //
			? (value > 9999999 //
				? (value > 999999999//
					? 10 //
					: (value > 99999999 ? 9 : 8)) //
				: (value > 999999 ? 7 : 6)) //
			: (value > 99 //
				? (value > 9999 //
					? 5 //
					: (value > 999 ? 4 : 3)) //
				: (value > 9 ? 2 : 1)));
	}

	/** Diese Methode gibt die Anzahl an Zeichen zurück, die zur Darstellung der gegebenen positiven Dezimalzahl nötig sind.
	 * <p>
	 * <b>Ungültige Eingaben werden nicht geprüft!</b>
	 *
	 * @param value positive Dezimalzahl.
	 * @return Zeichenanzahl. */
	public static int getSize(final long value) {
		final long div = value / 1000000000, mod = value % 1000000000;
		return div != 0 ? Integers.getSize(div) + 9 : Integers.getSize((int)mod);
	}

	/** Diese Methode gibt die Anzahl der Dezimalziffern ab der gegebenen Position des gegebenen Puffers zurück.
	 * <p>
	 * Ungültige Eingaben werden nicht geprüft!
	 *
	 * @param buffer Puffer mit Dezimalziffern.
	 * @param offset Position des ersten untersuchten Zeichens.
	 * @param length Anzahl der zu untersuchenden Zeichen.
	 * @return Anzahl der Dezimalziffern. */
	public static int getSize(final char[] buffer, final int offset, final int length) {
		final int limit = offset + length;
		int index = offset;
		while (index < limit) {
			final char digit = buffer[index];
			if ((digit < '0') || (digit > '9')) return index - offset;
			index++;
		}
		return length;
	}

	/** Diese Methode gibt die Anzahl der Dezimalziffern ab der gegebenen Position der gegebenen Zeichenkette zurück.
	 * <p>
	 * Ungültige Eingaben werden nicht geprüft!
	 *
	 * @param buffer Zeichenkette mit Dezimalziffern.
	 * @param offset Position des ersten untersuchten Zeichens.
	 * @param length Anzahl der zu untersuchenden Zeichen.
	 * @return Anzahl der Dezimalziffern. */
	public static int getSize(final String buffer, final int offset, final int length) {
		final int limit = offset + length;
		int index = offset;
		while (index < limit) {
			final char digit = buffer.charAt(index);
			if ((digit < '0') || (digit > '9')) return index - offset;
			index++;
		}
		return length;
	}

	/** Diese Methode liest die positiven Dezimalzahl aus dem gegebenen Bereich des gegebenen Puffers und gibt sie zurück.
	 * <p>
	 * <b>Ungültige Eingaben werden nicht geprüft!</b>
	 *
	 * @param buffer Puffer mit Dezimalziffern im gegebenen Bereich.
	 * @param offset Position der ersten Dezimalziffer.
	 * @param length Anzahl der einzulesenden Dezimalziffern.
	 * @return positiven Dezimalzahl. */
	public static int parseInt(final char[] buffer, int offset, int length) {
		if (length == 0) return 0;
		int result = 0;
		length += offset;
		while (true) {
			result += buffer[offset] - '0';
			offset += 1;
			if (offset >= length) return result;
			result *= 10;
		}
	}

	/** Diese Methode liest die positiven Dezimalzahl aus dem gegebenen Bereich der gegebenen Zeichenkette und gibt sie zurück.
	 * <p>
	 * <b>Ungültige Eingaben werden nicht geprüft!</b>
	 *
	 * @param buffer Zeichenkette mit Dezimalziffern im gegebenen Bereich.
	 * @param offset Position der ersten Dezimalziffer.
	 * @param length Anzahl der einzulesenden Dezimalziffern.
	 * @return positiven Dezimalzahl. */
	public static int parseInt(final String buffer, int offset, int length) {
		if (length == 0) return 0;
		int result = 0;
		length += offset;
		while (true) {
			result += buffer.charAt(offset) - '0';
			offset += 1;
			if (offset >= length) return result;
			result *= 10;
		}
	}

	/** Diese Methode liest die positiven Dezimalzahl aus dem gegebenen Bereich des gegebenen Puffers und gibt sie zurück.
	 * <p>
	 * <b>Ungültige Eingaben werden nicht geprüft!</b>
	 *
	 * @param buffer Puffer mit Dezimalziffern im gegebenen Bereich.
	 * @param offset Position der ersten Dezimalziffer.
	 * @param length Anzahl der einzulesenden Dezimalziffern.
	 * @return positiven Dezimalzahl. */
	public static long parseLong(final char[] buffer, final int offset, final int length) {
		if (length < 10) return Integers.parseInt(buffer, offset, length);
		return (Integers.parseLong(buffer, offset, length - 9) * 1000000000) + Integers.parseLong(buffer, (offset + length) - 9, 9);
	}

	/** Diese Methode liest die positiven Dezimalzahl aus dem gegebenen Bereich der gegebenen Zeichenkette und gibt sie zurück.
	 * <p>
	 * <b>Ungültige Eingaben werden nicht geprüft!</b>
	 *
	 * @param buffer Zeichenkette mit Dezimalziffern im gegebenen Bereich.
	 * @param offset Position der ersten Dezimalziffer.
	 * @param length Anzahl der einzulesenden Dezimalziffern.
	 * @return positiven Dezimalzahl. */
	public static long parseLong(final String buffer, final int offset, final int length) {
		if (length < 10) return Integers.parseInt(buffer, offset, length);
		return (Integers.parseLong(buffer, offset, length - 9) * 1000000000) + Integers.parseLong(buffer, (offset + length) - 9, 9);
	}

	/** Diese Methode schreibt die Zeichenkette der gegebene positiven Dezimalzahl vor der gegebenen Position in den gegebenen Puffer.
	 * <p>
	 * <b>Ungültige Eingaben werden nicht geprüft!</b>
	 *
	 * @param value positiven Dezimalzahl.
	 * @param buffer Puffer für die Zeichenkette.
	 * @param offset Position des ersten Zeichens hinter der geschriebenen Dezimalzahl. */
	public static void printInt(int value, final char[] buffer, int offset) {
		int div, mod;
		while (true) {
			div = value / 100;
			mod = value % 100;
			if (div != 0) {
				buffer[--offset] = (char)Integers.digitOneArray[mod];
				buffer[--offset] = (char)Integers.digitTenArray[mod];
				value = div;
			} else {
				buffer[--offset] = (char)Integers.digitOneArray[mod];
				if (mod < 10) return;
				buffer[--offset] = (char)Integers.digitTenArray[mod];
				return;
			}
		}
	}

	/** Diese Methode schreibt die Zeichenkette der gegebene positiven Dezimalzahl mit der gegebenen Länge um führenden Nullen ergänzt an die gegebenen Position
	 * in den gegebenen Puffer.
	 * <p>
	 * <b>Ungültige Eingaben werden nicht geprüft!</b>
	 *
	 * @param value positiven Dezimalzahl.
	 * @param buffer Puffer für die Zeichenkette.
	 * @param offset Position des ersten zu schreibenden Zeichens.
	 * @param length Anzahl der zu schreibenden Zeichen, welche mindestend der für die gegebene Dezimalzahl neötigten Anzahl an Zeichen entsprechen muss. */
	public static void printInt(final int value, final char[] buffer, final int offset, final int length) {
		int fill = length - Integers.getSize(value);
		while (--fill >= 0) {
			buffer[offset + fill] = '0';
		}
		Integers.printInt(value, buffer, offset + length);
	}

	/** Diese Methode schreibt die Zeichenkette der gegebene positiven Dezimalzahl vor der gegebenen Position in den gegebenen Puffer.
	 * <p>
	 * <b>Ungültige Eingaben werden nicht geprüft!</b>
	 *
	 * @param value positiven Dezimalzahl.
	 * @param buffer Puffer für die Zeichenkette.
	 * @param offset Position des ersten Zeichens hinter der geschriebenen Dezimalzahl. */
	public static void printLong(long value, final char[] buffer, int offset) {
		long div, mod;
		while (true) {
			div = value / 1000000000;
			mod = value % 1000000000;
			if (div != 0) {
				offset -= 9;
				Integers.printInt((int)mod, buffer, offset, 9);
				value = div;
			} else {
				Integers.printInt((int)mod, buffer, offset);
				return;
			}
		}
	}

	/** Diese Methode schreibt die Zeichenkette der gegebene positiven Dezimalzahl mit der gegebenen Länge um führenden Nullen ergänzt an die gegebenen Position
	 * in den gegebenen Puffer.
	 * <p>
	 * <b>Ungültige Eingaben werden nicht geprüft!</b>
	 *
	 * @param value positiven Dezimalzahl.
	 * @param buffer Puffer für die Zeichenkette.
	 * @param offset Position des ersten zu schreibenden Zeichens.
	 * @param length Anzahl der zu schreibenden Zeichen, welche mindestend der für die gegebene Dezimalzahl neötigten Anzahl an Zeichen entsprechen muss. */
	public static void printLong(final long value, final char[] buffer, final int offset, final int length) {
		int fill = length - Integers.getSize(value);
		while (--fill >= 0) {
			buffer[offset + fill] = '0';
		}
		Integers.printLong(value, buffer, offset + length);
	}

	/** Diese Methode gibt die gegebene Speichergröße als Zeichenkette mit Maßeinheit {@code B}, {@code KB}, {@code MB}, {@code GB}, {@code TB}, {@code PB} bzw.
	 * {@code EB} zurück.
	 *
	 * @param value Speichergröße in Byte.
	 * @return formatierte Speichergröße. */
	public static String printSize(final long value) {
		final StringBuilder res = new StringBuilder();
		Integers.printSize(res, value);
		return res.toString();
	}

	/** Diese Methode fügt die gegebene Speichergröße mit {@link #printSize(long) Maßeinheit formatiert} an den gegebenen Puffer an.
	 *
	 * @param res Speichergröße in Byte.
	 * @param value Puffer. */
	public static void printSize(final StringBuilder res, final long value) {
		if (value < 0) {
			Integers.appendSizeImpl(res.append('-'), value < -1024 ? ~value : -value);
		} else {
			Integers.appendSizeImpl(res, value);
		}
	}

	/** Diese Methode gibt die gegebene Zeitspanne als Zeichenkette mit Maßeinheit {@code ns}, {@code µs}, {@code ms}, {@code s}, {@code ks}, {@code Ms} bzw.
	 * {@code Gs} zurück.
	 *
	 * @param value Zeitspanne in Nanosekunden.
	 * @return formatierte Zeitspanne. */
	public static String printTime(final long value) {
		final StringBuilder res = new StringBuilder();
		Integers.printTime(res, value);
		return res.toString();
	}

	/** Diese Methode fügt die gegebene Zeitspanne mit {@link #printTime(long) Maßeinheit formatiert} an den gegebenen Puffer an.
	 *
	 * @param res Zeitspanne in Nanosekunden.
	 * @param value Puffer. */
	public static void printTime(final StringBuilder res, final long value) {
		if (value < 0) {
			Integers.appendTimeImpl(res.append('-'), value < -1000 ? ~value : -value);
		} else {
			Integers.appendTimeImpl(res, value);
		}
	}

	static StringBuilder appendSizeImpl(final StringBuilder res, long value) {
		if (value < 1024) return res.append((int)value).append(Integers.sizeUnitArray[0]);
		int unit = 1;
		if (value < 102400) {
			value = (value * 100) / 1024;
		} else {
			value = (value / 1024) * 100;
		}
		while (value >= 102400) {
			unit++;
			value /= 1024;
		}
		return Integers.appendPercentImpl(res, (int)value).append(Integers.sizeUnitArray[unit]);
	}

	static StringBuilder appendTimeImpl(final StringBuilder res, long value) {
		if (value < 1000) return res.append((int)value).append(Integers.timeUnitArray[0]);
		value /= 10;
		int unit = 1;
		while (value >= 100000) {
			unit++;
			value /= 1000;
		}
		return Integers.appendPercentImpl(res, (int)value).append(Integers.timeUnitArray[unit]);
	}

	static StringBuilder appendPercentImpl(final StringBuilder res, final int value) {
		final int div = value / 100, mod = value % 100;
		res.append(div);
		if ((div >= 100) || (mod == 0) || ((div >= 10) && (mod < 10))) return res;
		res.append('.').append((char)Integers.digitTenArray[mod]);
		if ((div >= 10) || ((mod % 10) == 0)) return res;
		return res.append((char)Integers.digitOneArray[mod]);
	}

	/** Diese Methode gibt die gegebennen 16-Bit-Werte als 32-Bit-Wert zurück.
	 *
	 * @param int16H MSB 16-Bit-Wert.
	 * @param int16L LSB 16-Bit-Wert.
	 * @return 32-Bit-Wert */
	public static int toInt(final int int16H, final int int16L) {
		return (int16H << 16) | (int16L & 0xFFFF);
	}

	/** Diese Methode ist eine Abkürzung für {@link #toInt(int, int) toInt(toShort(byte3, byte2), toShort(byte1, byte0))}.
	 *
	 * @see #toShort(int, int) */
	public static int toInt(final int byte3, final int byte2, final int byte1, final int byte0) {
		return Integers.toInt(Integers.toShort(byte3, byte2), Integers.toShort(byte1, byte0));
	}

	/** Diese Methode gibt den LSB 32-Bit-Wert des gegebenen 64-Bit-Werts zurück.
	 *
	 * @param int64 64-Bit-Wert.
	 * @return LSB 32-Bit-Wert. */
	public static int toIntL(final long int64) {
		return (int)int64;
	}

	/** Diese Methode gibt den MSB 32-Bit-Wert des gegebenen 64-Bit-Werts zurück.
	 *
	 * @param int64 64-Bit-Wert.
	 * @return MSB 32-Bit-Wert. */
	public static int toIntH(final long int64) {
		return Integers.toIntL(int64 >> 32);
	}

	/** Diese Methode gibt den LSB 8-Bit-Wert des gegebenen 16-Bit-Werts zurück.
	 *
	 * @param int16 16-Bit-Wert.
	 * @return LSB 8-Bit-Wert. */
	public static int toByteL(final int int16) {
		return int16 & 0xFF;
	}

	/** Diese Methode gibt den MSB 8-Bit-Wert des gegebenen 16-Bit-Werts zurück.
	 *
	 * @param int16 16-Bit-Wert.
	 * @return MSB 8-Bit-Wert. */
	public static int toByteH(final int int16) {
		return Integers.toByteL(int16 >> 8);
	}

	/** Diese Methode gibt die gegebennen 8-Bit-Werte als 16-Bit-Wert zurück.
	 *
	 * @param int8H MSB 8-Bit-Wert.
	 * @param int8L LSB 8-Bit-Wert.
	 * @return 16-Bit-Wert */
	public static int toShort(final int int8H, final int int8L) {
		return (int8H << 8) | (int8L & 0xFF);
	}

	/** Diese Methode gibt den LSB 16-Bit-Wert des gegebenen 32-Bit-Werts zurück.
	 *
	 * @param int32 32-Bit-Wert.
	 * @return LSB 16-Bit-Wert. */
	public static int toShortL(final int int32) {
		return int32 & 0xFFFF;
	}

	/** Diese Methode gibt den MSB 16-Bit-Wert des gegebenen 32-Bit-Werts zurück.
	 *
	 * @param int32 32-Bit-Wert.
	 * @return MSB 16-Bit-Wert. */
	public static int toShortH(final int int32) {
		return Integers.toShortL(int32 >> 16);
	}

	/** Diese Methode gibt die gegebennen 32-Bit-Werte als 64-Bit-Wert zurück.
	 *
	 * @param int32H MSB 32-Bit-Wert.
	 * @param int32L LSB 32-Bit-Wert.
	 * @return 64-Bit-Wert */
	public static long toLong(final int int32H, final int int32L) {
		return ((long)int32H << 32) | (int32L & 0xFFFFFFFFL);
	}

}
