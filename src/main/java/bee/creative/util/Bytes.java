package bee.creative.util;

import java.nio.ByteOrder;

/** Diese Klasse implementiert Methoden zum konvertierenden Kopieren von {@code byte}-, {@code int} und {@code long}-Arrays in {@link ByteOrder#BIG_ENDIAN} sowie
 * {@link ByteOrder#LITTLE_ENDIAN}.
 * 
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class Bytes {

	/** Diese Methode ließt {@code 1 byte} aus dem gegebenen {@code byte}-Array und gib diese als {@code int} interpretiert zurück.
	 * 
	 * @see #setInt1(byte[], int, int)
	 * @param array {@code byte}-Array.
	 * @param index Index.
	 * @return {@code 1 byte}-Wert als {@code int}. */
	public static int getInt1(final byte[] array, final int index) {
		return array[index] & 0xFF;
	}

	/** Diese Methode ließt {@code 2 byte} aus dem gegebenen {@code byte}-Array und gib diese als {@link ByteOrder#BIG_ENDIAN} {@code int} interpretiert zurück.
	 * 
	 * @see #setInt2BE(byte[], int, int)
	 * @param array {@code byte}-Array.
	 * @param index Index.
	 * @return {@code 2 byte}-Wert als {@code int}. */
	public static int getInt2BE(final byte[] array, final int index) {
		return 0 //
			| ((array[index + 0] & 0xFF) << 8) //
			| ((array[index + 1] & 0xFF) << 0);
	}

	/** Diese Methode ließt {@code 2 byte} aus dem gegebenen {@code byte}-Array und gib diese als {@link ByteOrder#LITTLE_ENDIAN} {@code int} interpretiert zurück.
	 * 
	 * @see #setInt2LE(byte[], int, int)
	 * @param array {@code byte}-Array.
	 * @param index Index.
	 * @return {@code 2 byte}-Wert als {@code int}. */
	public static int getInt2LE(final byte[] array, final int index) {
		return 0 //
			| ((array[index + 1] & 0xFF) << 8) //
			| ((array[index + 0] & 0xFF) << 0);
	}

	/** Diese Methode ließt {@code 3 byte} aus dem gegebenen {@code byte}-Array und gib diese als {@link ByteOrder#BIG_ENDIAN} {@code int} interpretiert zurück.
	 * 
	 * @see #setInt3BE(byte[], int, int)
	 * @param array {@code byte}-Array.
	 * @param index Index.
	 * @return {@code 3 byte}-Wert als {@code int}. */
	public static int getInt3BE(final byte[] array, final int index) {
		return 0 //
			| ((array[index + 0] & 0xFF) << 16) //
			| ((array[index + 1] & 0xFF) << 8) //
			| ((array[index + 2] & 0xFF) << 0);
	}

	/** Diese Methode ließt {@code 3 byte} aus dem gegebenen {@code byte}-Array und gib diese als {@link ByteOrder#LITTLE_ENDIAN} {@code int} interpretiert zurück.
	 * 
	 * @see #setInt3LE(byte[], int, int)
	 * @param array {@code byte}-Array.
	 * @param index Index.
	 * @return {@code 3 byte}-Wert als {@code int}. */
	public static int getInt3LE(final byte[] array, final int index) {
		return 0 //
			| ((array[index + 2] & 0xFF) << 16) //
			| ((array[index + 1] & 0xFF) << 8) //
			| ((array[index + 0] & 0xFF) << 0);
	}

	/** Diese Methode ließt {@code 4 byte} aus dem gegebenen {@code byte}-Array und gib diese als {@link ByteOrder#BIG_ENDIAN} {@code int} interpretiert zurück.
	 * 
	 * @see #setInt4BE(byte[], int, int)
	 * @param array {@code byte}-Array.
	 * @param index Index.
	 * @return {@code 4 byte}-Wert als {@code int}. */
	public static int getInt4BE(final byte[] array, final int index) {
		return 0 //
			| ((array[index + 0] & 0xFF) << 24) //
			| ((array[index + 1] & 0xFF) << 16) //
			| ((array[index + 2] & 0xFF) << 8) //
			| ((array[index + 3] & 0xFF) << 0);
	}

	/** Diese Methode ließt {@code 4 byte} aus dem gegebenen {@code byte}-Array und gib diese als {@link ByteOrder#LITTLE_ENDIAN} {@code int} interpretiert zurück.
	 * 
	 * @see #setInt4LE(byte[], int, int)
	 * @param array {@code byte}-Array.
	 * @param index Index.
	 * @return {@code 4 byte}-Wert als {@code int}. */
	public static int getInt4LE(final byte[] array, final int index) {
		return 0 //
			| ((array[index + 3] & 0xFF) << 24) //
			| ((array[index + 2] & 0xFF) << 16) //
			| ((array[index + 1] & 0xFF) << 8) //
			| ((array[index + 0] & 0xFF) << 0);
	}

	/** Diese Methode ließt die gegebene Anzahl an {@code byte}s aus dem gegebenen {@code byte}-Array und gib diese als {@link ByteOrder#BIG_ENDIAN} {@code int}
	 * interpretiert zurück.
	 * 
	 * @see #setIntBE(byte[], int, int, int)
	 * @param array {@code byte}-Array.
	 * @param index Index.
	 * @param size Anzahl an {@code byte}s (0..4).
	 * @return {@code byte}s als {@code int}. */
	public static int getIntBE(final byte[] array, final int index, final int size) {
		switch (size) {
			case 0:
				return 0;
			case 1:
				return Bytes.getInt1(array, index);
			case 2:
				return Bytes.getInt2BE(array, index);
			case 3:
				return Bytes.getInt3BE(array, index);
			case 4:
				return Bytes.getInt4BE(array, index);
			default:
				throw new IllegalArgumentException();
		}
	}

	/** Diese Methode ließt die gegebene Anzahl an {@code byte}s aus dem gegebenen {@code byte}-Array und gib diese als {@link ByteOrder#LITTLE_ENDIAN} {@code int}
	 * interpretiert zurück.
	 * 
	 * @see #setIntLE(byte[], int, int, int)
	 * @param array {@code byte}-Array.
	 * @param index Index.
	 * @param size Anzahl an {@code byte}s (0..4).
	 * @return {@code byte}s als {@code int}. */
	public static int getIntLE(final byte[] array, final int index, final int size) {
		switch (size) {
			case 0:
				return 0;
			case 1:
				return Bytes.getInt1(array, index);
			case 2:
				return Bytes.getInt2LE(array, index);
			case 3:
				return Bytes.getInt3LE(array, index);
			case 4:
				return Bytes.getInt4LE(array, index);
			default:
				throw new IllegalArgumentException();
		}
	}

	/** Diese Methode ließt {@code 5 byte} aus dem gegebenen {@code byte}-Array und gib diese als {@link ByteOrder#BIG_ENDIAN} {@code long} interpretiert zurück.
	 * 
	 * @see #setLong5BE(byte[], int, long)
	 * @param array {@code byte}-Array.
	 * @param index Index.
	 * @return {@code 5 byte}-Wert als {@code long}. */
	public static long getLong5BE(final byte[] array, final int index) {
		return 0 //
			| ((long)Bytes.getInt1(array, index + 0) << 32) //
			| (Bytes.getInt4BE(array, index + 1) & 0xFFFFFFFFL);
	}

	/** Diese Methode ließt {@code 5 byte} aus dem gegebenen {@code byte}-Array und gib diese als {@link ByteOrder#LITTLE_ENDIAN} {@code long} interpretiert
	 * zurück.
	 * 
	 * @see #setLong5LE(byte[], int, long)
	 * @param array {@code byte}-Array.
	 * @param index Index.
	 * @return {@code 5 byte}-Wert als {@code long}. */
	public static long getLong5LE(final byte[] array, final int index) {
		return 0 //
			| ((long)Bytes.getInt1(array, index + 4) << 32) //
			| (Bytes.getInt4LE(array, index + 0) & 0xFFFFFFFFL);
	}

	/** Diese Methode ließt {@code 6 byte} aus dem gegebenen {@code byte}-Array und gib diese als {@link ByteOrder#BIG_ENDIAN} {@code long} interpretiert zurück.
	 * 
	 * @see #setLong6BE(byte[], int, long)
	 * @param array {@code byte}-Array.
	 * @param index Index.
	 * @return {@code 6 byte}-Wert als {@code long}. */
	public static long getLong6BE(final byte[] array, final int index) {
		return 0 //
			| ((long)Bytes.getInt2BE(array, index + 0) << 32) //
			| (Bytes.getInt4BE(array, index + 2) & 0xFFFFFFFFL);
	}

	/** Diese Methode ließt {@code 6 byte} aus dem gegebenen {@code byte}-Array und gib diese als {@link ByteOrder#LITTLE_ENDIAN} {@code long} interpretiert
	 * zurück.
	 * 
	 * @see #setLong6LE(byte[], int, long)
	 * @param array {@code byte}-Array.
	 * @param index Index.
	 * @return {@code 6 byte}-Wert als {@code long}. */
	public static long getLong6LE(final byte[] array, final int index) {
		return 0 //
			| ((long)Bytes.getInt2LE(array, index + 4) << 32) //
			| (Bytes.getInt4LE(array, index + 0) & 0xFFFFFFFFL);
	}

	/** Diese Methode ließt {@code 7 byte} aus dem gegebenen {@code byte}-Array und gib diese als {@link ByteOrder#BIG_ENDIAN} {@code long} interpretiert zurück.
	 * 
	 * @see #setLong7BE(byte[], int, long)
	 * @param array {@code byte}-Array.
	 * @param index Index.
	 * @return {@code 7 byte}-Wert als {@code long}. */
	public static long getLong7BE(final byte[] array, final int index) {
		return 0 //
			| ((long)Bytes.getInt3BE(array, index + 0) << 32) //
			| (Bytes.getInt4BE(array, index + 3) & 0xFFFFFFFFL);
	}

	/** Diese Methode ließt {@code 7 byte} aus dem gegebenen {@code byte}-Array und gib diese als {@link ByteOrder#LITTLE_ENDIAN} {@code long} interpretiert
	 * zurück.
	 * 
	 * @see #setLong7LE(byte[], int, long)
	 * @param array {@code byte}-Array.
	 * @param index Index.
	 * @return {@code 7 byte}-Wert als {@code long}. */
	public static long getLong7LE(final byte[] array, final int index) {
		return 0 //
			| ((long)Bytes.getInt3LE(array, index + 4) << 32) //
			| (Bytes.getInt4LE(array, index + 0) & 0xFFFFFFFFL);
	}

	/** Diese Methode ließt {@code 8 byte} aus dem gegebenen {@code byte}-Array und gib diese als {@link ByteOrder#BIG_ENDIAN} {@code long} interpretiert zurück.
	 * 
	 * @see #setLong8BE(byte[], int, long)
	 * @param array {@code byte}-Array.
	 * @param index Index.
	 * @return {@code 8 byte}-Wert als {@code long}. */
	public static long getLong8BE(final byte[] array, final int index) {
		return 0 //
			| ((long)Bytes.getInt4BE(array, index + 0) << 32) //
			| (Bytes.getInt4BE(array, index + 4) & 0xFFFFFFFFL);
	}

	/** Diese Methode ließt {@code 8 byte} aus dem gegebenen {@code byte}-Array und gib diese als {@link ByteOrder#LITTLE_ENDIAN} {@code long} interpretiert
	 * zurück.
	 * 
	 * @see #setLong8LE(byte[], int, long)
	 * @param array {@code byte}-Array.
	 * @param index Index.
	 * @return {@code 8 byte}-Wert als {@code long}. */
	public static long getLong8LE(final byte[] array, final int index) {
		return 0 //
			| ((long)Bytes.getInt4LE(array, index + 4) << 32) //
			| (Bytes.getInt4LE(array, index + 0) & 0xFFFFFFFFL);
	}

	/** Diese Methode ließt die gegebene Anzahl an {@code byte}s aus dem gegebenen {@code byte}-Array und gib diese als {@link ByteOrder#BIG_ENDIAN} {@code long}
	 * interpretiert zurück.
	 * 
	 * @see #setLongBE(byte[], int, long, int)
	 * @param array {@code byte}-Array.
	 * @param index Index.
	 * @param size Anzahl an {@code byte}s (0..8).
	 * @return {@code byte}s als {@code long}. */
	public static long getLongBE(final byte[] array, final int index, final int size) {
		switch (size) {
			case 0:
				return 0;
			case 1:
				return Bytes.getInt1(array, index);
			case 2:
				return Bytes.getInt2BE(array, index);
			case 3:
				return Bytes.getInt3BE(array, index);
			case 4:
				return Bytes.getInt4BE(array, index);
			case 5:
				return Bytes.getLong5BE(array, index);
			case 6:
				return Bytes.getLong6BE(array, index);
			case 7:
				return Bytes.getLong7BE(array, index);
			case 8:
				return Bytes.getLong8BE(array, index);
			default:
				throw new IllegalArgumentException();
		}
	}

	/** Diese Methode ließt die gegebene Anzahl an {@code byte}s aus dem gegebenen {@code byte}-Array und gib diese als {@link ByteOrder#LITTLE_ENDIAN}
	 * {@code long} interpretiert zurück.
	 * 
	 * @see #setLongLE(byte[], int, long, int)
	 * @param array {@code byte}-Array.
	 * @param index Index.
	 * @param size Anzahl an {@code byte}s (0..8).
	 * @return {@code byte}s als {@code long}. */
	public static long getLongLE(final byte[] array, final int index, final int size) {
		switch (size) {
			case 0:
				return 0;
			case 1:
				return Bytes.getInt1(array, index);
			case 2:
				return Bytes.getInt2LE(array, index);
			case 3:
				return Bytes.getInt3LE(array, index);
			case 4:
				return Bytes.getInt4LE(array, index);
			case 5:
				return Bytes.getLong5LE(array, index);
			case 6:
				return Bytes.getLong6LE(array, index);
			case 7:
				return Bytes.getLong7LE(array, index);
			case 8:
				return Bytes.getLong8LE(array, index);
			default:
				throw new IllegalArgumentException();
		}
	}

	/** Diese Methode schreibt {@code 1 byte} des gegebenen {@code int} in das gegebene {@code byte}-Array.
	 * 
	 * @see #getInt1(byte[], int)
	 * @param array {@code byte}-Array.
	 * @param index Index.
	 * @param value {@code int} mit {@code 1 byte}-Wert. */
	public static void setInt1(final byte[] array, final int index, final int value) {
		array[index + 0] = (byte)(value >>> 0);
	}

	/** Diese Methode schreibt {@code 2 byte} des gegebenen {@link ByteOrder#BIG_ENDIAN} {@code int} in das gegebene {@code byte}-Array.
	 * 
	 * @see #getInt2BE(byte[], int)
	 * @param array {@code byte}-Array.
	 * @param index Index.
	 * @param value {@code int} mit {@code 2 byte}-Wert. */
	public static void setInt2BE(final byte[] array, final int index, final int value) {
		array[index + 0] = (byte)(value >>> 8);
		array[index + 1] = (byte)(value >>> 0);
	}

	/** Diese Methode schreibt {@code 2 byte} des gegebenen {@link ByteOrder#LITTLE_ENDIAN} {@code int} in das gegebene {@code byte}-Array.
	 * 
	 * @see #getInt2LE(byte[], int)
	 * @param array {@code byte}-Array.
	 * @param index Index.
	 * @param value {@code int} mit {@code 2 byte}-Wert. */
	public static void setInt2LE(final byte[] array, final int index, final int value) {
		array[index + 1] = (byte)(value >>> 8);
		array[index + 0] = (byte)(value >>> 0);
	}

	/** Diese Methode schreibt {@code 3 byte} des gegebenen {@link ByteOrder#BIG_ENDIAN} {@code int} in das gegebene {@code byte}-Array.
	 * 
	 * @see #getInt3BE(byte[], int)
	 * @param array {@code byte}-Array.
	 * @param index Index.
	 * @param value {@code int} mit {@code 3 byte}-Wert. */
	public static void setInt3BE(final byte[] array, final int index, final int value) {
		array[index + 0] = (byte)(value >>> 16);
		array[index + 1] = (byte)(value >>> 8);
		array[index + 2] = (byte)(value >>> 0);
	}

	/** Diese Methode schreibt {@code 3 byte} des gegebenen {@link ByteOrder#LITTLE_ENDIAN} {@code int} in das gegebene {@code byte}-Array.
	 * 
	 * @see #getInt3LE(byte[], int)
	 * @param array {@code byte}-Array.
	 * @param index Index.
	 * @param value {@code int} mit {@code 3 byte}-Wert. */
	public static void setInt3LE(final byte[] array, final int index, final int value) {
		array[index + 2] = (byte)(value >>> 16);
		array[index + 1] = (byte)(value >>> 8);
		array[index + 0] = (byte)(value >>> 0);
	}

	/** Diese Methode schreibt {@code 4 byte} des gegebenen {@link ByteOrder#BIG_ENDIAN} {@code int} in das gegebene {@code byte}-Array.
	 * 
	 * @see #getInt4BE(byte[], int)
	 * @param array {@code byte}-Array.
	 * @param index Index.
	 * @param value {@code int} mit {@code 4 byte}-Wert. */
	public static void setInt4BE(final byte[] array, final int index, final int value) {
		array[index + 0] = (byte)(value >>> 24);
		array[index + 1] = (byte)(value >>> 16);
		array[index + 2] = (byte)(value >>> 8);
		array[index + 3] = (byte)(value >>> 0);
	}

	/** Diese Methode schreibt {@code 4 byte} des gegebenen {@link ByteOrder#LITTLE_ENDIAN} {@code int} in das gegebene {@code byte}-Array.
	 * 
	 * @see #getInt4LE(byte[], int)
	 * @param array {@code byte}-Array.
	 * @param index Index.
	 * @param value {@code int} mit {@code 4 byte}-Wert. */
	public static void setInt4LE(final byte[] array, final int index, final int value) {
		array[index + 3] = (byte)(value >>> 24);
		array[index + 2] = (byte)(value >>> 16);
		array[index + 1] = (byte)(value >>> 8);
		array[index + 0] = (byte)(value >>> 0);
	}

	/** Diese Methode schreibt die gegebene Anzahl an {@code byte}s des gegebenen {@link ByteOrder#BIG_ENDIAN} {@code int} in das gegebene {@code byte}-Array.
	 * 
	 * @see #getIntBE(byte[], int, int)
	 * @param array {@code byte}-Array.
	 * @param index Index.
	 * @param value {@code int}.
	 * @param size Anzahl an {@code byte}s (0..4). */
	public static void setIntBE(final byte[] array, final int index, final int value, final int size) {
		switch (size) {
			case 0:
				return;
			case 1:
				Bytes.setInt1(array, index, value);
			break;
			case 2:
				Bytes.setInt2BE(array, index, value);
			break;
			case 3:
				Bytes.setInt3BE(array, index, value);
			break;
			case 4:
				Bytes.setInt4BE(array, index, value);
			break;
			default:
				throw new IllegalArgumentException();
		}
	}

	/** Diese Methode schreibt die gegebene Anzahl an {@code byte}s des gegebenen {@link ByteOrder#LITTLE_ENDIAN} {@code int} in das gegebene {@code byte}-Array.
	 * 
	 * @see #getIntLE(byte[], int, int)
	 * @param array {@code byte}-Array.
	 * @param index Index.
	 * @param value {@code int}.
	 * @param size Anzahl an {@code byte}s (0..4). */
	public static void setIntLE(final byte[] array, final int index, final int value, final int size) {
		switch (size) {
			case 0:
				return;
			case 1:
				Bytes.setInt1(array, index, value);
			break;
			case 2:
				Bytes.setInt2LE(array, index, value);
			break;
			case 3:
				Bytes.setInt3LE(array, index, value);
			break;
			case 4:
				Bytes.setInt4LE(array, index, value);
			break;
			default:
				throw new IllegalArgumentException();
		}
	}

	/** Diese Methode schreibt {@code 5 byte} des gegebenen {@link ByteOrder#BIG_ENDIAN} {@code long} in das gegebene {@code byte}-Array.
	 * 
	 * @see #getLong5BE(byte[], int)
	 * @param array {@code long}-Array.
	 * @param index Index.
	 * @param value {@code long} mit {@code 5 byte}-Wert. */
	public static void setLong5BE(final byte[] array, final int index, final long value) {
		Bytes.setInt1(array, index + 0, (int)(value >>> 32));
		Bytes.setInt4BE(array, index + 1, (int)(value >>> 0));
	}

	/** Diese Methode schreibt {@code 5 byte} des gegebenen {@link ByteOrder#LITTLE_ENDIAN} {@code long} in das gegebene {@code byte}-Array.
	 * 
	 * @see #getLong5BE(byte[], int)
	 * @param array {@code long}-Array.
	 * @param index Index.
	 * @param value {@code long} mit {@code 5 byte}-Wert. */
	public static void setLong5LE(final byte[] array, final int index, final long value) {
		Bytes.setInt1(array, index + 4, (int)(value >>> 32));
		Bytes.setInt4LE(array, index + 0, (int)(value >>> 0));
	}

	/** Diese Methode schreibt {@code 6 byte} des gegebenen {@link ByteOrder#BIG_ENDIAN} {@code long} in das gegebene {@code byte}-Array.
	 * 
	 * @see #getLong6BE(byte[], int)
	 * @param array {@code long}-Array.
	 * @param index Index.
	 * @param value {@code long} mit {@code 6 byte}-Wert. */
	public static void setLong6BE(final byte[] array, final int index, final long value) {
		Bytes.setInt2BE(array, index + 0, (int)(value >>> 32));
		Bytes.setInt4BE(array, index + 2, (int)(value >>> 0));
	}

	/** Diese Methode schreibt {@code 6 byte} des gegebenen {@link ByteOrder#LITTLE_ENDIAN} {@code long} in das gegebene {@code byte}-Array.
	 * 
	 * @see #getLong6BE(byte[], int)
	 * @param array {@code long}-Array.
	 * @param index Index.
	 * @param value {@code long} mit {@code 6 byte}-Wert. */
	public static void setLong6LE(final byte[] array, final int index, final long value) {
		Bytes.setInt2LE(array, index + 4, (int)(value >>> 32));
		Bytes.setInt4LE(array, index + 0, (int)(value >>> 0));
	}

	/** Diese Methode schreibt {@code 7 byte} des gegebenen {@link ByteOrder#BIG_ENDIAN} {@code long} in das gegebene {@code byte}-Array.
	 * 
	 * @see #getLong7BE(byte[], int)
	 * @param array {@code long}-Array.
	 * @param index Index.
	 * @param value {@code long} mit {@code 7 byte}-Wert. */
	public static void setLong7BE(final byte[] array, final int index, final long value) {
		Bytes.setInt3BE(array, index + 0, (int)(value >>> 32));
		Bytes.setInt4BE(array, index + 3, (int)(value >>> 0));
	}

	/** Diese Methode schreibt {@code 7 byte} des gegebenen {@link ByteOrder#LITTLE_ENDIAN} {@code long} in das gegebene {@code byte}-Array.
	 * 
	 * @see #getLong7BE(byte[], int)
	 * @param array {@code long}-Array.
	 * @param index Index.
	 * @param value {@code long} mit {@code 7 byte}-Wert. */
	public static void setLong7LE(final byte[] array, final int index, final long value) {
		Bytes.setInt3LE(array, index + 4, (int)(value >>> 32));
		Bytes.setInt4LE(array, index + 0, (int)(value >>> 0));
	}

	/** Diese Methode schreibt {@code 8 byte} des gegebenen {@link ByteOrder#BIG_ENDIAN} {@code long} in das gegebene {@code byte}-Array.
	 * 
	 * @see #getLong8BE(byte[], int)
	 * @param array {@code long}-Array.
	 * @param index Index.
	 * @param value {@code long} mit {@code 8 byte}-Wert. */
	public static void setLong8BE(final byte[] array, final int index, final long value) {
		Bytes.setInt4BE(array, index + 0, (int)(value >>> 32));
		Bytes.setInt4BE(array, index + 4, (int)(value >>> 0));
	}

	/** Diese Methode schreibt {@code 8 byte} des gegebenen {@link ByteOrder#LITTLE_ENDIAN} {@code long} in das gegebene {@code byte}-Array.
	 * 
	 * @see #getLong8BE(byte[], int)
	 * @param array {@code long}-Array.
	 * @param index Index.
	 * @param value {@code long} mit {@code 8 byte}-Wert. */
	public static void setLong8LE(final byte[] array, final int index, final long value) {
		Bytes.setInt4LE(array, index + 4, (int)(value >>> 32));
		Bytes.setInt4LE(array, index + 0, (int)(value >>> 0));
	}

	/** Diese Methode schreibt die gegebene Anzahl an {@code byte}s des gegebenen {@link ByteOrder#BIG_ENDIAN} {@code long} in das gegebene {@code byte}-Array.
	 * 
	 * @see #getIntBE(byte[], int, int)
	 * @param array {@code byte}-Array.
	 * @param index Index.
	 * @param value {@code long}.
	 * @param size Anzahl an {@code byte}s (0..8). */
	public static void setLongBE(final byte[] array, final int index, final long value, final int size) {
		switch (size) {
			case 0:
				return;
			case 1:
				Bytes.setInt1(array, index, (int)value);
			break;
			case 2:
				Bytes.setInt2BE(array, index, (int)value);
			break;
			case 3:
				Bytes.setInt3BE(array, index, (int)value);
			break;
			case 4:
				Bytes.setInt4BE(array, index, (int)value);
			break;
			case 5:
				Bytes.setLong5BE(array, index, value);
			break;
			case 6:
				Bytes.setLong6BE(array, index, value);
			break;
			case 7:
				Bytes.setLong7BE(array, index, value);
			break;
			case 8:
				Bytes.setLong8BE(array, index, value);
			break;
			default:
				throw new IllegalArgumentException();
		}
	}

	/** Diese Methode schreibt die gegebene Anzahl an {@code byte}s des gegebenen {@link ByteOrder#LITTLE_ENDIAN} {@code long} in das gegebene {@code byte}-Array.
	 * 
	 * @see #getIntBE(byte[], int, int)
	 * @param array {@code byte}-Array.
	 * @param index Index.
	 * @param value {@code long}.
	 * @param size Anzahl an {@code byte}s (0..8). */
	public static void setLongLE(final byte[] array, final int index, final long value, final int size) {
		switch (size) {
			case 0:
				return;
			case 1:
				Bytes.setInt1(array, index, (int)value);
			break;
			case 2:
				Bytes.setInt2LE(array, index, (int)value);
			break;
			case 3:
				Bytes.setInt3LE(array, index, (int)value);
			break;
			case 4:
				Bytes.setInt4LE(array, index, (int)value);
			break;
			case 5:
				Bytes.setLong5LE(array, index, value);
			break;
			case 6:
				Bytes.setLong6LE(array, index, value);
			break;
			case 7:
				Bytes.setLong7LE(array, index, value);
			break;
			case 8:
				Bytes.setLong8LE(array, index, value);
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
