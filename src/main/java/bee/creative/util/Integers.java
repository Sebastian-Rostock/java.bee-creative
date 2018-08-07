package bee.creative.util;

/** Diese Klasse implementiert stellt Methoden zum Parsen und Formatieren von Tezimalzahlen zur Verfügung.
 *
 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Integers {

	/** Dieses Feld speichert die Zehnerstelle der ersten 100 positiven Dezimanzahlen. */
	final static byte[] digitTenArray = {'0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '2', '2', '2', '2',
		'2', '2', '2', '2', '2', '2', '3', '3', '3', '3', '3', '3', '3', '3', '3', '3', '4', '4', '4', '4', '4', '4', '4', '4', '4', '4', '5', '5', '5', '5', '5',
		'5', '5', '5', '5', '5', '6', '6', '6', '6', '6', '6', '6', '6', '6', '6', '7', '7', '7', '7', '7', '7', '7', '7', '7', '7', '8', '8', '8', '8', '8', '8',
		'8', '8', '8', '8', '9', '9', '9', '9', '9', '9', '9', '9', '9', '9'};

	/** Dieses Feld speichert die Einerstelle der ersten 100 positiven Dezimanzahlen. */
	final static byte[] digitOneArray = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3',
		'4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4',
		'5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5',
		'6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

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
	public static void formatInt(int value, final char[] buffer, int offset) {
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
	public static void formatInt(final int value, final char[] buffer, final int offset, final int length) {
		int fill = length - Integers.stringSize(value);
		while (--fill >= 0) {
			buffer[offset + fill] = '0';
		}
		Integers.formatInt(value, buffer, offset + length);
	}

	/** Diese Methode schreibt die Zeichenkette der gegebene positiven Dezimalzahl vor der gegebenen Position in den gegebenen Puffer.
	 * <p>
	 * <b>Ungültige Eingaben werden nicht geprüft!</b>
	 *
	 * @param value positiven Dezimalzahl.
	 * @param buffer Puffer für die Zeichenkette.
	 * @param offset Position des ersten Zeichens hinter der geschriebenen Dezimalzahl. */
	public static void formatLong(long value, final char[] buffer, int offset) {
		long div, mod;
		while (true) {
			div = value / 1000000000;
			mod = value % 1000000000;
			if (div != 0) {
				offset -= 9;
				Integers.formatInt((int)mod, buffer, offset, 9);
				value = div;
			} else {
				Integers.formatInt((int)mod, buffer, offset);
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
	public static void formatLong(final long value, final char[] buffer, final int offset, final int length) {
		int fill = length - Integers.stringSize(value);
		while (--fill >= 0) {
			buffer[offset + fill] = '0';
		}
		Integers.formatLong(value, buffer, offset + length);
	}

	/** Diese Methode gibt die Anzahl an Zeichen zurück, die zur Darstellung der gegebenen positiven Dezimalzahl nötig sind.
	 * <p>
	 * <b>Ungültige Eingaben werden nicht geprüft!</b>
	 *
	 * @param value positive Dezimalzahl.
	 * @return Zeichenanzahl. */
	public static int stringSize(final int value) {
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
	public static int stringSize(final long value) {
		final long div = value / 1000000000, mod = value % 1000000000;
		return div != 0 ? Integers.stringSize(div) + 9 : Integers.stringSize((int)mod);
	}

	/** Diese Methode gibt die Anzahl der Dezimalziffern ab der gegebenen Position des gegebenen Puffers zurück.
	 * <p>
	 * Ungültige Eingaben werden nicht geprüft!
	 *
	 * @param buffer Puffer mit Dezimalziffern.
	 * @param offset Position des ersten untersuchten Zeichens.
	 * @param length Anzahl der zu untersuchenden Zeichen.
	 * @return Anzahl der Dezimalziffern. */
	public static int integerSize(final char[] buffer, final int offset, final int length) {
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
	public static int integerSize(final String buffer, final int offset, final int length) {
		final int limit = offset + length;
		int index = offset;
		while (index < limit) {
			final char digit = buffer.charAt(index);
			if ((digit < '0') || (digit > '9')) return index - offset;
			index++;
		}
		return length;
	}

}
