package bee.creative.lang;

import java.nio.ByteOrder;

/** Diese Klasse implementiert Methoden zur Interpretation von Bytefolgen als Dezimalzahlen in {@link ByteOrder#BIG_ENDIAN} sowie
 * {@link ByteOrder#LITTLE_ENDIAN} unterschiedlicher Längen.
 *
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class Bytes {

	/** Diese Methode ließt {@code 1 byte} aus der gegebenen Bytefolge und gib diese als {@code int} interpretiert zurück.
	 *
	 * @see #setInt1(byte[], int, int)
	 * @param byteArray Bytefolge.
	 * @param byteOffset Position des ersten Byte.
	 * @return {@code 1 byte}-Wert als {@code int}. */
	public static int getInt1(final byte[] byteArray, final int byteOffset) {
		return byteArray[byteOffset] & 0xFF;
	}

	/** Diese Methode ließt {@code 2 byte} aus der gegebenen Bytefolge und gib diese als {@link ByteOrder#BIG_ENDIAN} {@code int} interpretiert zurück.
	 *
	 * @see #setInt2BE(byte[], int, int)
	 * @param byteArray Bytefolge.
	 * @param byteOffset Position des ersten Byte.
	 * @return {@code 2 byte}-Wert als {@code int}. */
	public static int getInt2BE(final byte[] byteArray, final int byteOffset) {
		return 0 //
			| ((byteArray[byteOffset + 0] & 0xFF) << 8) //
			| ((byteArray[byteOffset + 1] & 0xFF) << 0);
	}

	/** Diese Methode ließt {@code 2 byte} aus der gegebenen Bytefolge und gib diese als {@link ByteOrder#LITTLE_ENDIAN} {@code int} interpretiert zurück.
	 *
	 * @see #setInt2LE(byte[], int, int)
	 * @param byteArray Bytefolge.
	 * @param byteOffset Position des ersten Byte.
	 * @return {@code 2 byte}-Wert als {@code int}. */
	public static int getInt2LE(final byte[] byteArray, final int byteOffset) {
		return 0 //
			| ((byteArray[byteOffset + 1] & 0xFF) << 8) //
			| ((byteArray[byteOffset + 0] & 0xFF) << 0);
	}

	/** Diese Methode ließt {@code 3 byte} aus der gegebenen Bytefolge und gib diese als {@link ByteOrder#BIG_ENDIAN} {@code int} interpretiert zurück.
	 *
	 * @see #setInt3BE(byte[], int, int)
	 * @param byteArray Bytefolge.
	 * @param byteOffset Position des ersten Byte.
	 * @return {@code 3 byte}-Wert als {@code int}. */
	public static int getInt3BE(final byte[] byteArray, final int byteOffset) {
		return 0 //
			| ((byteArray[byteOffset + 0] & 0xFF) << 16) //
			| ((byteArray[byteOffset + 1] & 0xFF) << 8) //
			| ((byteArray[byteOffset + 2] & 0xFF) << 0);
	}

	/** Diese Methode ließt {@code 3 byte} aus der gegebenen Bytefolge und gib diese als {@link ByteOrder#LITTLE_ENDIAN} {@code int} interpretiert zurück.
	 *
	 * @see #setInt3LE(byte[], int, int)
	 * @param byteArray Bytefolge.
	 * @param byteOffset Position des ersten Byte.
	 * @return {@code 3 byte}-Wert als {@code int}. */
	public static int getInt3LE(final byte[] byteArray, final int byteOffset) {
		return 0 //
			| ((byteArray[byteOffset + 2] & 0xFF) << 16) //
			| ((byteArray[byteOffset + 1] & 0xFF) << 8) //
			| ((byteArray[byteOffset + 0] & 0xFF) << 0);
	}

	/** Diese Methode ließt {@code 4 byte} aus der gegebenen Bytefolge und gib diese als {@link ByteOrder#BIG_ENDIAN} {@code int} interpretiert zurück.
	 *
	 * @see #setInt4BE(byte[], int, int)
	 * @param byteArray Bytefolge.
	 * @param byteOffset Position des ersten Byte.
	 * @return {@code 4 byte}-Wert als {@code int}. */
	public static int getInt4BE(final byte[] byteArray, final int byteOffset) {
		return 0 //
			| ((byteArray[byteOffset + 0] & 0xFF) << 24) //
			| ((byteArray[byteOffset + 1] & 0xFF) << 16) //
			| ((byteArray[byteOffset + 2] & 0xFF) << 8) //
			| ((byteArray[byteOffset + 3] & 0xFF) << 0);
	}

	/** Diese Methode ließt {@code 4 byte} aus der gegebenen Bytefolge und gib diese als {@link ByteOrder#LITTLE_ENDIAN} {@code int} interpretiert zurück.
	 *
	 * @see #setInt4LE(byte[], int, int)
	 * @param byteArray Bytefolge.
	 * @param byteOffset Position des ersten Byte.
	 * @return {@code 4 byte}-Wert als {@code int}. */
	public static int getInt4LE(final byte[] byteArray, final int byteOffset) {
		return 0 //
			| ((byteArray[byteOffset + 3] & 0xFF) << 24) //
			| ((byteArray[byteOffset + 2] & 0xFF) << 16) //
			| ((byteArray[byteOffset + 1] & 0xFF) << 8) //
			| ((byteArray[byteOffset + 0] & 0xFF) << 0);
	}

	/** Diese Methode ließt die gegebene Anzahl an Byte aus der gegebenen Bytefolge und gib diese als {@link ByteOrder#BIG_ENDIAN} {@code int} interpretiert
	 * zurück.
	 *
	 * @see #setIntBE(byte[], int, int, int)
	 * @param byteArray Bytefolge.
	 * @param byteOffset Position des ersten Byte.
	 * @param byteCount Anzahl der Byte (0..4).
	 * @return {@code byte} als {@code int}. */
	public static int getIntBE(final byte[] byteArray, final int byteOffset, final int byteCount) {
		switch (byteCount) {
			case 0:
				return 0;
			case 1:
				return Bytes.getInt1(byteArray, byteOffset);
			case 2:
				return Bytes.getInt2BE(byteArray, byteOffset);
			case 3:
				return Bytes.getInt3BE(byteArray, byteOffset);
			case 4:
				return Bytes.getInt4BE(byteArray, byteOffset);
			default:
				throw new IllegalArgumentException();
		}
	}

	/** Diese Methode ließt die gegebene Anzahl an Byte aus der gegebenen Bytefolge und gib diese als {@link ByteOrder#LITTLE_ENDIAN} {@code int} interpretiert
	 * zurück.
	 *
	 * @see #setIntLE(byte[], int, int, int)
	 * @param byteArray Bytefolge.
	 * @param byteOffset Position des ersten Byte.
	 * @param byteCount Anzahl der Byte (0..4).
	 * @return {@code byte} als {@code int}. */
	public static int getIntLE(final byte[] byteArray, final int byteOffset, final int byteCount) {
		switch (byteCount) {
			case 0:
				return 0;
			case 1:
				return Bytes.getInt1(byteArray, byteOffset);
			case 2:
				return Bytes.getInt2LE(byteArray, byteOffset);
			case 3:
				return Bytes.getInt3LE(byteArray, byteOffset);
			case 4:
				return Bytes.getInt4LE(byteArray, byteOffset);
			default:
				throw new IllegalArgumentException();
		}
	}

	/** Diese Methode ließt {@code 5 byte} aus der gegebenen Bytefolge und gib diese als {@link ByteOrder#BIG_ENDIAN} {@code long} interpretiert zurück.
	 *
	 * @see #setLong5BE(byte[], int, long)
	 * @param byteArray Bytefolge.
	 * @param byteOffset Position des ersten Byte.
	 * @return {@code 5 byte}-Wert als {@code long}. */
	public static long getLong5BE(final byte[] byteArray, final int byteOffset) {
		return 0 //
			| ((long)Bytes.getInt1(byteArray, byteOffset + 0) << 32) //
			| (Bytes.getInt4BE(byteArray, byteOffset + 1) & 0xFFFFFFFFL);
	}

	/** Diese Methode ließt {@code 5 byte} aus der gegebenen Bytefolge und gib diese als {@link ByteOrder#LITTLE_ENDIAN} {@code long} interpretiert zurück.
	 *
	 * @see #setLong5LE(byte[], int, long)
	 * @param byteArray Bytefolge.
	 * @param byteOffset Position des ersten Byte.
	 * @return {@code 5 byte}-Wert als {@code long}. */
	public static long getLong5LE(final byte[] byteArray, final int byteOffset) {
		return 0 //
			| ((long)Bytes.getInt1(byteArray, byteOffset + 4) << 32) //
			| (Bytes.getInt4LE(byteArray, byteOffset + 0) & 0xFFFFFFFFL);
	}

	/** Diese Methode ließt {@code 6 byte} aus der gegebenen Bytefolge und gib diese als {@link ByteOrder#BIG_ENDIAN} {@code long} interpretiert zurück.
	 *
	 * @see #setLong6BE(byte[], int, long)
	 * @param byteArray Bytefolge.
	 * @param byteOffset Position des ersten Byte.
	 * @return {@code 6 byte}-Wert als {@code long}. */
	public static long getLong6BE(final byte[] byteArray, final int byteOffset) {
		return 0 //
			| ((long)Bytes.getInt2BE(byteArray, byteOffset + 0) << 32) //
			| (Bytes.getInt4BE(byteArray, byteOffset + 2) & 0xFFFFFFFFL);
	}

	/** Diese Methode ließt {@code 6 byte} aus der gegebenen Bytefolge und gib diese als {@link ByteOrder#LITTLE_ENDIAN} {@code long} interpretiert zurück.
	 *
	 * @see #setLong6LE(byte[], int, long)
	 * @param byteArray Bytefolge.
	 * @param byteOffset Position des ersten Byte.
	 * @return {@code 6 byte}-Wert als {@code long}. */
	public static long getLong6LE(final byte[] byteArray, final int byteOffset) {
		return 0 //
			| ((long)Bytes.getInt2LE(byteArray, byteOffset + 4) << 32) //
			| (Bytes.getInt4LE(byteArray, byteOffset + 0) & 0xFFFFFFFFL);
	}

	/** Diese Methode ließt {@code 7 byte} aus der gegebenen Bytefolge und gib diese als {@link ByteOrder#BIG_ENDIAN} {@code long} interpretiert zurück.
	 *
	 * @see #setLong7BE(byte[], int, long)
	 * @param byteArray Bytefolge.
	 * @param byteOffset Position des ersten Byte.
	 * @return {@code 7 byte}-Wert als {@code long}. */
	public static long getLong7BE(final byte[] byteArray, final int byteOffset) {
		return 0 //
			| ((long)Bytes.getInt3BE(byteArray, byteOffset + 0) << 32) //
			| (Bytes.getInt4BE(byteArray, byteOffset + 3) & 0xFFFFFFFFL);
	}

	/** Diese Methode ließt {@code 7 byte} aus der gegebenen Bytefolge und gib diese als {@link ByteOrder#LITTLE_ENDIAN} {@code long} interpretiert zurück.
	 *
	 * @see #setLong7LE(byte[], int, long)
	 * @param byteArray Bytefolge.
	 * @param byteOffset Position des ersten Byte.
	 * @return {@code 7 byte}-Wert als {@code long}. */
	public static long getLong7LE(final byte[] byteArray, final int byteOffset) {
		return 0 //
			| ((long)Bytes.getInt3LE(byteArray, byteOffset + 4) << 32) //
			| (Bytes.getInt4LE(byteArray, byteOffset + 0) & 0xFFFFFFFFL);
	}

	/** Diese Methode ließt {@code 8 byte} aus der gegebenen Bytefolge und gib diese als {@link ByteOrder#BIG_ENDIAN} {@code long} interpretiert zurück.
	 *
	 * @see #setLong8BE(byte[], int, long)
	 * @param byteArray Bytefolge.
	 * @param byteOffset Position des ersten Byte.
	 * @return {@code 8 byte}-Wert als {@code long}. */
	public static long getLong8BE(final byte[] byteArray, final int byteOffset) {
		return 0 //
			| ((long)Bytes.getInt4BE(byteArray, byteOffset + 0) << 32) //
			| (Bytes.getInt4BE(byteArray, byteOffset + 4) & 0xFFFFFFFFL);
	}

	/** Diese Methode ließt {@code 8 byte} aus der gegebenen Bytefolge und gib diese als {@link ByteOrder#LITTLE_ENDIAN} {@code long} interpretiert zurück.
	 *
	 * @see #setLong8LE(byte[], int, long)
	 * @param byteArray Bytefolge.
	 * @param byteOffset Position des ersten Byte.
	 * @return {@code 8 byte}-Wert als {@code long}. */
	public static long getLong8LE(final byte[] byteArray, final int byteOffset) {
		return 0 //
			| ((long)Bytes.getInt4LE(byteArray, byteOffset + 4) << 32) //
			| (Bytes.getInt4LE(byteArray, byteOffset + 0) & 0xFFFFFFFFL);
	}

	/** Diese Methode ließt die gegebene Anzahl an Byte aus der gegebenen Bytefolge und gib diese als {@link ByteOrder#BIG_ENDIAN} {@code long} interpretiert
	 * zurück.
	 *
	 * @see #setLongBE(byte[], int, int, long)
	 * @param byteArray Bytefolge.
	 * @param byteOffset Position des ersten Byte.
	 * @param byteCount Anzahl der Byte (0..8).
	 * @return {@code byte} als {@code long}. */
	public static long getLongBE(final byte[] byteArray, final int byteOffset, final int byteCount) {
		switch (byteCount) {
			case 0:
				return 0;
			case 1:
				return Bytes.getInt1(byteArray, byteOffset);
			case 2:
				return Bytes.getInt2BE(byteArray, byteOffset);
			case 3:
				return Bytes.getInt3BE(byteArray, byteOffset);
			case 4:
				return Bytes.getInt4BE(byteArray, byteOffset);
			case 5:
				return Bytes.getLong5BE(byteArray, byteOffset);
			case 6:
				return Bytes.getLong6BE(byteArray, byteOffset);
			case 7:
				return Bytes.getLong7BE(byteArray, byteOffset);
			case 8:
				return Bytes.getLong8BE(byteArray, byteOffset);
			default:
				throw new IllegalArgumentException();
		}
	}

	/** Diese Methode ließt die gegebene Anzahl an Byte aus der gegebenen Bytefolge und gib diese als {@link ByteOrder#LITTLE_ENDIAN} {@code long} interpretiert
	 * zurück.
	 *
	 * @see #setLongLE(byte[], int, int, long)
	 * @param byteArray Bytefolge.
	 * @param byteOffset Position des ersten Byte.
	 * @param byteCount Anzahl der Byte (0..8).
	 * @return {@code byte} als {@code long}. */
	public static long getLongLE(final byte[] byteArray, final int byteOffset, final int byteCount) {
		switch (byteCount) {
			case 0:
				return 0;
			case 1:
				return Bytes.getInt1(byteArray, byteOffset);
			case 2:
				return Bytes.getInt2LE(byteArray, byteOffset);
			case 3:
				return Bytes.getInt3LE(byteArray, byteOffset);
			case 4:
				return Bytes.getInt4LE(byteArray, byteOffset);
			case 5:
				return Bytes.getLong5LE(byteArray, byteOffset);
			case 6:
				return Bytes.getLong6LE(byteArray, byteOffset);
			case 7:
				return Bytes.getLong7LE(byteArray, byteOffset);
			case 8:
				return Bytes.getLong8LE(byteArray, byteOffset);
			default:
				throw new IllegalArgumentException();
		}
	}

	/** Diese Methode schreibt {@code 1 byte} des gegebenen {@code int} in die gegebene Bytefolge.
	 *
	 * @see #getInt1(byte[], int)
	 * @param byteArray Bytefolge.
	 * @param byteOffset Position des ersten Byte.
	 * @param value {@code int} mit {@code 1 byte}-Wert. */
	public static void setInt1(final byte[] byteArray, final int byteOffset, final int value) {
		byteArray[byteOffset + 0] = (byte)(value >>> 0);
	}

	/** Diese Methode schreibt {@code 2 byte} des gegebenen {@link ByteOrder#BIG_ENDIAN} {@code int} in die gegebene Bytefolge.
	 *
	 * @see #getInt2BE(byte[], int)
	 * @param byteArray Bytefolge.
	 * @param byteOffset Position des ersten Byte.
	 * @param value {@code int} mit {@code 2 byte}-Wert. */
	public static void setInt2BE(final byte[] byteArray, final int byteOffset, final int value) {
		byteArray[byteOffset + 0] = (byte)(value >>> 8);
		byteArray[byteOffset + 1] = (byte)(value >>> 0);
	}

	/** Diese Methode schreibt {@code 2 byte} des gegebenen {@link ByteOrder#LITTLE_ENDIAN} {@code int} in die gegebene Bytefolge.
	 *
	 * @see #getInt2LE(byte[], int)
	 * @param byteArray Bytefolge.
	 * @param byteOffset Position des ersten Byte.
	 * @param value {@code int} mit {@code 2 byte}-Wert. */
	public static void setInt2LE(final byte[] byteArray, final int byteOffset, final int value) {
		byteArray[byteOffset + 1] = (byte)(value >>> 8);
		byteArray[byteOffset + 0] = (byte)(value >>> 0);
	}

	/** Diese Methode schreibt {@code 3 byte} des gegebenen {@link ByteOrder#BIG_ENDIAN} {@code int} in die gegebene Bytefolge.
	 *
	 * @see #getInt3BE(byte[], int)
	 * @param byteArray Bytefolge.
	 * @param byteOffset Position des ersten Byte.
	 * @param value {@code int} mit {@code 3 byte}-Wert. */
	public static void setInt3BE(final byte[] byteArray, final int byteOffset, final int value) {
		byteArray[byteOffset + 0] = (byte)(value >>> 16);
		byteArray[byteOffset + 1] = (byte)(value >>> 8);
		byteArray[byteOffset + 2] = (byte)(value >>> 0);
	}

	/** Diese Methode schreibt {@code 3 byte} des gegebenen {@link ByteOrder#LITTLE_ENDIAN} {@code int} in die gegebene Bytefolge.
	 *
	 * @see #getInt3LE(byte[], int)
	 * @param byteArray Bytefolge.
	 * @param byteOffset Position des ersten Byte.
	 * @param value {@code int} mit {@code 3 byte}-Wert. */
	public static void setInt3LE(final byte[] byteArray, final int byteOffset, final int value) {
		byteArray[byteOffset + 2] = (byte)(value >>> 16);
		byteArray[byteOffset + 1] = (byte)(value >>> 8);
		byteArray[byteOffset + 0] = (byte)(value >>> 0);
	}

	/** Diese Methode schreibt {@code 4 byte} des gegebenen {@link ByteOrder#BIG_ENDIAN} {@code int} in die gegebene Bytefolge.
	 *
	 * @see #getInt4BE(byte[], int)
	 * @param byteArray Bytefolge.
	 * @param byteOffset Position des ersten Byte.
	 * @param value {@code int} mit {@code 4 byte}-Wert. */
	public static void setInt4BE(final byte[] byteArray, final int byteOffset, final int value) {
		byteArray[byteOffset + 0] = (byte)(value >>> 24);
		byteArray[byteOffset + 1] = (byte)(value >>> 16);
		byteArray[byteOffset + 2] = (byte)(value >>> 8);
		byteArray[byteOffset + 3] = (byte)(value >>> 0);
	}

	/** Diese Methode schreibt {@code 4 byte} des gegebenen {@link ByteOrder#LITTLE_ENDIAN} {@code int} in die gegebene Bytefolge.
	 *
	 * @see #getInt4LE(byte[], int)
	 * @param byteArray Bytefolge.
	 * @param byteOffset Position des ersten Byte.
	 * @param value {@code int} mit {@code 4 byte}-Wert. */
	public static void setInt4LE(final byte[] byteArray, final int byteOffset, final int value) {
		byteArray[byteOffset + 3] = (byte)(value >>> 24);
		byteArray[byteOffset + 2] = (byte)(value >>> 16);
		byteArray[byteOffset + 1] = (byte)(value >>> 8);
		byteArray[byteOffset + 0] = (byte)(value >>> 0);
	}

	/** Diese Methode schreibt die gegebene Anzahl an Byte des gegebenen {@link ByteOrder#BIG_ENDIAN} {@code int} in die gegebene Bytefolge.
	 *
	 * @see #getIntBE(byte[], int, int)
	 * @param byteArray Bytefolge.
	 * @param byteOffset Position des ersten Byte.
	 * @param value {@code int}.
	 * @param byteCount Anzahl der Byte (0..4). */
	public static void setIntBE(final byte[] byteArray, final int byteOffset, final int byteCount, final int value) {
		switch (byteCount) {
			case 0:
				return;
			case 1:
				Bytes.setInt1(byteArray, byteOffset, value);
			break;
			case 2:
				Bytes.setInt2BE(byteArray, byteOffset, value);
			break;
			case 3:
				Bytes.setInt3BE(byteArray, byteOffset, value);
			break;
			case 4:
				Bytes.setInt4BE(byteArray, byteOffset, value);
			break;
			default:
				throw new IllegalArgumentException();
		}
	}

	/** Diese Methode schreibt die gegebene Anzahl an Byte des gegebenen {@link ByteOrder#LITTLE_ENDIAN} {@code int} in die gegebene Bytefolge.
	 *
	 * @see #getIntLE(byte[], int, int)
	 * @param byteArray Bytefolge.
	 * @param byteOffset Position des ersten Byte.
	 * @param value {@code int}.
	 * @param byteCount Anzahl der Byte (0..4). */
	public static void setIntLE(final byte[] byteArray, final int byteOffset, final int byteCount, final int value) {
		switch (byteCount) {
			case 0:
				return;
			case 1:
				Bytes.setInt1(byteArray, byteOffset, value);
			break;
			case 2:
				Bytes.setInt2LE(byteArray, byteOffset, value);
			break;
			case 3:
				Bytes.setInt3LE(byteArray, byteOffset, value);
			break;
			case 4:
				Bytes.setInt4LE(byteArray, byteOffset, value);
			break;
			default:
				throw new IllegalArgumentException();
		}
	}

	/** Diese Methode schreibt {@code 5 byte} des gegebenen {@link ByteOrder#BIG_ENDIAN} {@code long} in die gegebene Bytefolge.
	 *
	 * @see #getLong5BE(byte[], int)
	 * @param byteArray {@code long}-Array.
	 * @param byteOffset Position des ersten Byte.
	 * @param value {@code long} mit {@code 5 byte}-Wert. */
	public static void setLong5BE(final byte[] byteArray, final int byteOffset, final long value) {
		Bytes.setInt1(byteArray, byteOffset + 0, (int)(value >>> 32));
		Bytes.setInt4BE(byteArray, byteOffset + 1, (int)(value >>> 0));
	}

	/** Diese Methode schreibt {@code 5 byte} des gegebenen {@link ByteOrder#LITTLE_ENDIAN} {@code long} in die gegebene Bytefolge.
	 *
	 * @see #getLong5BE(byte[], int)
	 * @param byteArray {@code long}-Array.
	 * @param byteOffset Position des ersten Byte.
	 * @param value {@code long} mit {@code 5 byte}-Wert. */
	public static void setLong5LE(final byte[] byteArray, final int byteOffset, final long value) {
		Bytes.setInt1(byteArray, byteOffset + 4, (int)(value >>> 32));
		Bytes.setInt4LE(byteArray, byteOffset + 0, (int)(value >>> 0));
	}

	/** Diese Methode schreibt {@code 6 byte} des gegebenen {@link ByteOrder#BIG_ENDIAN} {@code long} in die gegebene Bytefolge.
	 *
	 * @see #getLong6BE(byte[], int)
	 * @param byteArray {@code long}-Array.
	 * @param byteOffset Position des ersten Byte.
	 * @param value {@code long} mit {@code 6 byte}-Wert. */
	public static void setLong6BE(final byte[] byteArray, final int byteOffset, final long value) {
		Bytes.setInt2BE(byteArray, byteOffset + 0, (int)(value >>> 32));
		Bytes.setInt4BE(byteArray, byteOffset + 2, (int)(value >>> 0));
	}

	/** Diese Methode schreibt {@code 6 byte} des gegebenen {@link ByteOrder#LITTLE_ENDIAN} {@code long} in die gegebene Bytefolge.
	 *
	 * @see #getLong6BE(byte[], int)
	 * @param byteArray {@code long}-Array.
	 * @param byteOffset Position des ersten Byte.
	 * @param value {@code long} mit {@code 6 byte}-Wert. */
	public static void setLong6LE(final byte[] byteArray, final int byteOffset, final long value) {
		Bytes.setInt2LE(byteArray, byteOffset + 4, (int)(value >>> 32));
		Bytes.setInt4LE(byteArray, byteOffset + 0, (int)(value >>> 0));
	}

	/** Diese Methode schreibt {@code 7 byte} des gegebenen {@link ByteOrder#BIG_ENDIAN} {@code long} in die gegebene Bytefolge.
	 *
	 * @see #getLong7BE(byte[], int)
	 * @param byteArray {@code long}-Array.
	 * @param byteOffset Position des ersten Byte.
	 * @param value {@code long} mit {@code 7 byte}-Wert. */
	public static void setLong7BE(final byte[] byteArray, final int byteOffset, final long value) {
		Bytes.setInt3BE(byteArray, byteOffset + 0, (int)(value >>> 32));
		Bytes.setInt4BE(byteArray, byteOffset + 3, (int)(value >>> 0));
	}

	/** Diese Methode schreibt {@code 7 byte} des gegebenen {@link ByteOrder#LITTLE_ENDIAN} {@code long} in die gegebene Bytefolge.
	 *
	 * @see #getLong7BE(byte[], int)
	 * @param byteArray {@code long}-Array.
	 * @param byteOffset Position des ersten Byte.
	 * @param value {@code long} mit {@code 7 byte}-Wert. */
	public static void setLong7LE(final byte[] byteArray, final int byteOffset, final long value) {
		Bytes.setInt3LE(byteArray, byteOffset + 4, (int)(value >>> 32));
		Bytes.setInt4LE(byteArray, byteOffset + 0, (int)(value >>> 0));
	}

	/** Diese Methode schreibt {@code 8 byte} des gegebenen {@link ByteOrder#BIG_ENDIAN} {@code long} in die gegebene Bytefolge.
	 *
	 * @see #getLong8BE(byte[], int)
	 * @param byteArray {@code long}-Array.
	 * @param byteOffset Position des ersten Byte.
	 * @param value {@code long} mit {@code 8 byte}-Wert. */
	public static void setLong8BE(final byte[] byteArray, final int byteOffset, final long value) {
		Bytes.setInt4BE(byteArray, byteOffset + 0, (int)(value >>> 32));
		Bytes.setInt4BE(byteArray, byteOffset + 4, (int)(value >>> 0));
	}

	/** Diese Methode schreibt {@code 8 byte} des gegebenen {@link ByteOrder#LITTLE_ENDIAN} {@code long} in die gegebene Bytefolge.
	 *
	 * @see #getLong8BE(byte[], int)
	 * @param byteArray {@code long}-Array.
	 * @param byteOffset Position des ersten Byte.
	 * @param value {@code long} mit {@code 8 byte}-Wert. */
	public static void setLong8LE(final byte[] byteArray, final int byteOffset, final long value) {
		Bytes.setInt4LE(byteArray, byteOffset + 4, (int)(value >>> 32));
		Bytes.setInt4LE(byteArray, byteOffset + 0, (int)(value >>> 0));
	}

	/** Diese Methode schreibt die gegebene Anzahl an Byte des gegebenen {@link ByteOrder#BIG_ENDIAN} {@code long} in die gegebene Bytefolge.
	 *
	 * @see #getIntBE(byte[], int, int)
	 * @param byteArray Bytefolge.
	 * @param byteOffset Position des ersten Byte.
	 * @param value {@code long}.
	 * @param byteCount Anzahl der Byte (0..8). */
	public static void setLongBE(final byte[] byteArray, final int byteOffset, final int byteCount, final long value) {
		switch (byteCount) {
			case 0:
				return;
			case 1:
				Bytes.setInt1(byteArray, byteOffset, (int)value);
			break;
			case 2:
				Bytes.setInt2BE(byteArray, byteOffset, (int)value);
			break;
			case 3:
				Bytes.setInt3BE(byteArray, byteOffset, (int)value);
			break;
			case 4:
				Bytes.setInt4BE(byteArray, byteOffset, (int)value);
			break;
			case 5:
				Bytes.setLong5BE(byteArray, byteOffset, value);
			break;
			case 6:
				Bytes.setLong6BE(byteArray, byteOffset, value);
			break;
			case 7:
				Bytes.setLong7BE(byteArray, byteOffset, value);
			break;
			case 8:
				Bytes.setLong8BE(byteArray, byteOffset, value);
			break;
			default:
				throw new IllegalArgumentException();
		}
	}

	/** Diese Methode schreibt die gegebene Anzahl an Byte des gegebenen {@link ByteOrder#LITTLE_ENDIAN} {@code long} in die gegebene Bytefolge.
	 *
	 * @see #getIntBE(byte[], int, int)
	 * @param byteArray Bytefolge.
	 * @param byteOffset Position des ersten Byte.
	 * @param value {@code long}.
	 * @param byteCount Anzahl der Byte (0..8). */
	public static void setLongLE(final byte[] byteArray, final int byteOffset, final int byteCount, final long value) {
		switch (byteCount) {
			case 0:
				return;
			case 1:
				Bytes.setInt1(byteArray, byteOffset, (int)value);
			break;
			case 2:
				Bytes.setInt2LE(byteArray, byteOffset, (int)value);
			break;
			case 3:
				Bytes.setInt3LE(byteArray, byteOffset, (int)value);
			break;
			case 4:
				Bytes.setInt4LE(byteArray, byteOffset, (int)value);
			break;
			case 5:
				Bytes.setLong5LE(byteArray, byteOffset, value);
			break;
			case 6:
				Bytes.setLong6LE(byteArray, byteOffset, value);
			break;
			case 7:
				Bytes.setLong7LE(byteArray, byteOffset, value);
			break;
			case 8:
				Bytes.setLong8LE(byteArray, byteOffset, value);
			break;
			default:
				throw new IllegalArgumentException();
		}
	}

	/** Diese Methode gibt die Anzahl der Byte zurück, um den gegebenen positiven Wert abzubilden.
	 *
	 * @param value positiver Wert.
	 * @return Länge (0..4). */
	public static int lengthOf(final int value) {
		return (value > 0xFFFF) ? (value > 0xFFFFFF ? 4 : 3) : (value > 0xFF ? 2 : value > 0x00 ? 1 : 0);
	}

	/** Diese Methode gibt die Anzahl der Byte zurück, um den gegebenen positiven Wert abzubilden.
	 *
	 * @param value positiver Wert.
	 * @return Länge (0..8). */
	public static int lengthOf(final long value) {
		return value > 0xFFFFFFFFL ? Bytes.lengthOf((int)(value >> 32)) + 4 : Bytes.lengthOf((int)value);
	}

}
