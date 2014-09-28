package bee.creative.util;

import java.nio.ByteOrder;

/**
 * Diese Klasse implementiert Methoden zum konvertierenden Kopieren von {@code byte}-, {@code int} und {@code long}-Arrays in {@link ByteOrder#BIG_ENDIAN}.
 * 
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class Bytes {

	/**
	 * Diese Methode ließt die gegebene Anzahl an {@code byte}s aus dem gegebenen {@code byte}-Array und gib diese als {@code int} interpretiert zurück. Das
	 * {@code index}-te {@code byte} besitzt das {@code most significant bit}.
	 * 
	 * @see #setInt(byte[], int, int, int)
	 * @param array {@code byte}-Array.
	 * @param index Index.
	 * @param size Anzahl an {@code byte}s (0..4).
	 * @return {@code byte}s als {@code int}.
	 */
	public static int getInt(final byte[] array, final int index, final int size) {
		switch(size){
			case 0:
				return 0;
			case 1:
				return Bytes.get1(array, index);
			case 2:
				return Bytes.get2(array, index);
			case 3:
				return Bytes.get3(array, index);
			case 4:
				return Bytes.get4(array, index);
			default:
				throw new IllegalArgumentException();
		}
	}

	/**
	 * Diese Methode ließt die gegebene Anzahl an {@code byte}s aus dem gegebenen {@code byte}-Array und gib diese als {@code long} interpretiert zurück. Das
	 * {@code index}-te {@code byte} besitzt das {@code most significant bit}.
	 * 
	 * @see #setLong(byte[], int, long, int)
	 * @param array {@code byte}-Array.
	 * @param index Index.
	 * @param size Anzahl an {@code byte}s (0..8).
	 * @return {@code byte}s als {@code long}.
	 */
	public static long getLong(final byte[] array, final int index, final int size) {
		switch(size){
			case 0:
				return 0;
			case 1:
				return Bytes.get1(array, index);
			case 2:
				return Bytes.get2(array, index);
			case 3:
				return Bytes.get3(array, index);
			case 4:
				return Bytes.get4(array, index);
			case 5:
				return Bytes.get5(array, index);
			case 6:
				return Bytes.get6(array, index);
			case 7:
				return Bytes.get7(array, index);
			case 8:
				return Bytes.get8(array, index);
			default:
				throw new IllegalArgumentException();
		}
	}

	/**
	 * Diese Methode ließt {@code 1 byte} aus dem gegebenen {@code byte}-Array und gib diese als {@code int} interpretiert zurück.
	 * 
	 * @see #set1(byte[], int, int)
	 * @param array {@code byte}-Array.
	 * @param index Index.
	 * @return {@code 1 byte}-Wert als {@code int}.
	 */
	public static int get1(final byte[] array, final int index) {
		return array[index] & 0xFF;
	}

	/**
	 * Diese Methode kopiert die gegebene Anzahl an {@code int}s aus dem gegebenen {@code byte}-Array in das gegebene {@code int}-Array, beginnend an den
	 * gegebenen Positionen. Für jeden geschriebenen {@code int} wird {@code 1 byte} gelesen.
	 * 
	 * @see #get1(byte[], int)
	 * @param source {@code byte}-Array als Quelle.
	 * @param sourceOffset Index des ersten gelesenen {@code byte}s.
	 * @param target {@code int}-Array als Ziel.
	 * @param targetOffset Index des ersten geschriebenen {@code int}s.
	 * @param targetCount Anzahl der kopierten {@code int}s.
	 */
	static public void get1(final byte[] source, int sourceOffset, final int[] target, int targetOffset, int targetCount) {
		for(targetCount += targetOffset; targetOffset < targetCount; sourceOffset += 1, targetOffset += 1){
			target[targetOffset] = Bytes.get1(source, sourceOffset);
		}
	}

	/**
	 * Diese Methode ließt {@code 2 byte} aus dem gegebenen {@code byte}-Array und gib diese als {@code int} interpretiert zurück. Das {@code index}-te
	 * {@code byte} besitzt das {@code most significant bit}.
	 * 
	 * @see #set2(byte[], int, int)
	 * @param array {@code byte}-Array.
	 * @param index Index.
	 * @return {@code 2 byte}-Wert als {@code int}.
	 */
	public static int get2(final byte[] array, final int index) {
		return ((array[index] & 0xFF) << 8) | (array[index + 1] & 0xFF);
	}

	/**
	 * Diese Methode kopiert die gegebene Anzahl an {@code int}s aus dem gegebenen {@code byte}-Array in das gegebene {@code int}-Array, beginnend an den
	 * gegebenen Positionen. Für jeden geschriebenen {@code int} werden {@code 2 byte} gelesen.
	 * 
	 * @see #get2(byte[], int)
	 * @param source {@code byte}-Array als Quelle.
	 * @param sourceOffset Index des ersten gelesenen {@code byte}s.
	 * @param target {@code int}-Array als Ziel.
	 * @param targetOffset Index des ersten geschriebenen {@code int}s.
	 * @param targetCount Anzahl der kopierten {@code int}s.
	 */
	static public void get2(final byte[] source, int sourceOffset, final int[] target, int targetOffset, int targetCount) {
		for(targetCount += targetOffset; targetOffset < targetCount; sourceOffset += 2, targetOffset += 1){
			target[targetOffset] = Bytes.get2(source, sourceOffset);
		}
	}

	/**
	 * Diese Methode ließt {@code 3 byte} aus dem gegebenen {@code byte}-Array und gib diese als {@code int} interpretiert zurück. Das {@code index}-te
	 * {@code byte} besitzt das {@code most significant bit}.
	 * 
	 * @see #set3(byte[], int, int)
	 * @param array {@code byte}-Array.
	 * @param index Index.
	 * @return {@code 3 byte}-Wert als {@code int}.
	 */
	public static int get3(final byte[] array, final int index) {
		return ((array[index] & 0xFF) << 16) | ((array[index + 1] & 0xFF) << 8) | (array[index + 2] & 0xFF);
	}

	/**
	 * Diese Methode kopiert die gegebene Anzahl an {@code int}s aus dem gegebenen {@code byte}-Array in das gegebene {@code int}-Array, beginnend an den
	 * gegebenen Positionen. Für jeden geschriebenen {@code int} werden {@code 3 byte} gelesen.
	 * 
	 * @see #get3(byte[], int)
	 * @param source {@code byte}-Array als Quelle.
	 * @param sourceOffset Index des ersten gelesenen {@code byte}s.
	 * @param target {@code int}-Array als Ziel.
	 * @param targetOffset Index des ersten geschriebenen {@code int}s.
	 * @param targetCount Anzahl der kopierten {@code int}s.
	 */
	static public void get3(final byte[] source, int sourceOffset, final int[] target, int targetOffset, int targetCount) {
		for(targetCount += targetOffset; targetOffset < targetCount; sourceOffset += 3, targetOffset += 1){
			target[targetOffset] = Bytes.get3(source, sourceOffset);
		}
	}

	/**
	 * Diese Methode ließt {@code 4 byte} aus dem gegebenen {@code byte}-Array und gib diese als {@code int} interpretiert zurück. Das {@code index}-te
	 * {@code byte} besitzt das {@code most significant bit}.
	 * 
	 * @see #set4(byte[], int, int)
	 * @param array {@code byte}-Array.
	 * @param index Index.
	 * @return {@code 4 byte}-Wert als {@code int}.
	 */
	public static int get4(final byte[] array, final int index) {
		return ((array[index] & 0xFF) << 24) | ((array[index + 1] & 0xFF) << 16) | ((array[index + 2] & 0xFF) << 8) | (array[index + 3] & 0xFF);
	}

	/**
	 * Diese Methode kopiert die gegebene Anzahl an {@code int}s aus dem gegebenen {@code byte}-Array in das gegebene {@code int}-Array, beginnend an den
	 * gegebenen Positionen. Für jeden geschriebenen {@code int} werden {@code 4 byte} gelesen.
	 * 
	 * @see #get4(byte[], int)
	 * @param source {@code byte}-Array als Quelle.
	 * @param sourceOffset Index des ersten gelesenen {@code byte}s.
	 * @param target {@code int}-Array als Ziel.
	 * @param targetOffset Index des ersten geschriebenen {@code int}s.
	 * @param targetCount Anzahl der kopierten {@code int}s.
	 */
	static public void get4(final byte[] source, int sourceOffset, final int[] target, int targetOffset, int targetCount) {
		for(targetCount += targetOffset; targetOffset < targetCount; sourceOffset += 4, targetOffset += 1){
			target[targetOffset] = Bytes.get4(source, sourceOffset);
		}
	}

	/**
	 * Diese Methode ließt {@code 5 byte} aus dem gegebenen {@code byte}-Array und gib diese als {@code long} interpretiert zurück. Das {@code index}-te
	 * {@code byte} besitzt das {@code most significant bit}.
	 * 
	 * @see #set5(byte[], int, long)
	 * @param array {@code byte}-Array.
	 * @param index Index.
	 * @return {@code 5 byte}-Wert als {@code long}.
	 */
	public static long get5(final byte[] array, final int index) {
		return ((long)Bytes.get1(array, index) << 32) | Bytes.get4(array, index + 1);
	}

	/**
	 * Diese Methode kopiert die gegebene Anzahl an {@code long}s aus dem gegebenen {@code byte}-Array in das gegebene {@code long}-Array, beginnend an den
	 * gegebenen Positionen. Für jeden geschriebenen {@code long} werden {@code 5 byte} gelesen.
	 * 
	 * @see #get5(byte[], int)
	 * @param source {@code byte}-Array als Quelle.
	 * @param sourceOffset Index des ersten gelesenen {@code byte}s.
	 * @param target {@code long}-Array als Ziel.
	 * @param targetOffset Index des ersten geschriebenen {@code long}s.
	 * @param targetCount Anzahl der kopierten {@code long}s.
	 */
	static public void get5(final byte[] source, int sourceOffset, final long[] target, int targetOffset, int targetCount) {
		for(targetCount += targetOffset; targetOffset < targetCount; sourceOffset += 5, targetOffset += 1){
			target[targetOffset] = Bytes.get5(source, sourceOffset);
		}
	}

	/**
	 * Diese Methode ließt {@code 6 byte} aus dem gegebenen {@code byte}-Array und gib diese als {@code long} interpretiert zurück. Das {@code index}-te
	 * {@code byte} besitzt das {@code most significant bit}.
	 * 
	 * @see #set6(byte[], int, long)
	 * @param array {@code byte}-Array.
	 * @param index Index.
	 * @return {@code 6 byte}-Wert als {@code long}.
	 */
	public static long get6(final byte[] array, final int index) {
		return ((long)Bytes.get2(array, index) << 32) | Bytes.get4(array, index + 2);
	}

	/**
	 * Diese Methode kopiert die gegebene Anzahl an {@code long}s aus dem gegebenen {@code byte}-Array in das gegebene {@code long}-Array, beginnend an den
	 * gegebenen Positionen. Für jeden geschriebenen {@code long} werden {@code 6 byte} gelesen.
	 * 
	 * @see #get6(byte[], int)
	 * @param source {@code byte}-Array als Quelle.
	 * @param sourceOffset Index des ersten gelesenen {@code byte}s.
	 * @param target {@code long}-Array als Ziel.
	 * @param targetOffset Index des ersten geschriebenen {@code long}s.
	 * @param targetCount Anzahl der kopierten {@code long}s.
	 */
	static public void get6(final byte[] source, int sourceOffset, final long[] target, int targetOffset, int targetCount) {
		for(targetCount += targetOffset; targetOffset < targetCount; sourceOffset += 6, targetOffset += 1){
			target[targetOffset] = Bytes.get6(source, sourceOffset);
		}
	}

	/**
	 * Diese Methode ließt {@code 7 byte} aus dem gegebenen {@code byte}-Array und gib diese als {@code long} interpretiert zurück. Das {@code index}-te
	 * {@code byte} besitzt das {@code most significant bit}.
	 * 
	 * @see #set7(byte[], int, long)
	 * @param array {@code byte}-Array.
	 * @param index Index.
	 * @return {@code 7 byte}-Wert als {@code long}.
	 */
	public static long get7(final byte[] array, final int index) {
		return ((long)Bytes.get3(array, index) << 32) | Bytes.get4(array, index + 3);
	}

	/**
	 * Diese Methode kopiert die gegebene Anzahl an {@code long}s aus dem gegebenen {@code byte}-Array in das gegebene {@code long}-Array, beginnend an den
	 * gegebenen Positionen. Für jeden geschriebenen {@code long} werden {@code 7 byte} gelesen.
	 * 
	 * @see #get7(byte[], int)
	 * @param source {@code byte}-Array als Quelle.
	 * @param sourceOffset Index des ersten gelesenen {@code byte}s.
	 * @param target {@code long}-Array als Ziel.
	 * @param targetOffset Index des ersten geschriebenen {@code long}s.
	 * @param targetCount Anzahl der kopierten {@code long}s.
	 */
	static public void get7(final byte[] source, int sourceOffset, final long[] target, int targetOffset, int targetCount) {
		for(targetCount += targetOffset; targetOffset < targetCount; sourceOffset += 7, targetOffset += 1){
			target[targetOffset] = Bytes.get7(source, sourceOffset);
		}
	}

	/**
	 * Diese Methode ließt {@code 8 byte} aus dem gegebenen {@code byte}-Array und gib diese als {@code long} interpretiert zurück. Das {@code index}-te
	 * {@code byte} besitzt das {@code most significant bit}.
	 * 
	 * @see #set8(byte[], int, long)
	 * @param array {@code byte}-Array.
	 * @param index Index.
	 * @return {@code 8 byte}-Wert als {@code long}.
	 */
	public static long get8(final byte[] array, final int index) {
		return ((long)Bytes.get4(array, index) << 32) | Bytes.get4(array, index + 4);
	}

	/**
	 * Diese Methode kopiert die gegebene Anzahl an {@code long}s aus dem gegebenen {@code byte}-Array in das gegebene {@code long}-Array, beginnend an den
	 * gegebenen Positionen. Für jeden geschriebenen {@code long} werden {@code 8 byte} gelesen.
	 * 
	 * @see #get8(byte[], int)
	 * @param source {@code byte}-Array als Quelle.
	 * @param sourceOffset Index des ersten gelesenen {@code byte}s.
	 * @param target {@code long}-Array als Ziel.
	 * @param targetOffset Index des ersten geschriebenen {@code long}s.
	 * @param targetCount Anzahl der kopierten {@code long}s.
	 */
	static public void get8(final byte[] source, int sourceOffset, final long[] target, int targetOffset, int targetCount) {
		for(targetCount += targetOffset; targetOffset < targetCount; sourceOffset += 8, targetOffset += 1){
			target[targetOffset] = Bytes.get8(source, sourceOffset);
		}
	}

	/**
	 * Diese Methode schreibt die gegebene Anzahl an {@code byte}s des gegebenen {@code int} in das gegebene {@code byte}-Array. Das {@code index}-te {@code byte}
	 * besitzt das {@code most significant bit}.
	 * 
	 * @see #getInt(byte[], int, int)
	 * @param array {@code byte}-Array.
	 * @param index Index.
	 * @param value {@code int}.
	 * @param size Anzahl an {@code byte}s (0..4).
	 */
	public static void setInt(final byte[] array, final int index, final int value, final int size) {
		switch(size){
			case 0:
				return;
			case 1:
				Bytes.set1(array, index, value);
				break;
			case 2:
				Bytes.set2(array, index, value);
				break;
			case 3:
				Bytes.set3(array, index, value);
				break;
			case 4:
				Bytes.set4(array, index, value);
				break;
			default:
				throw new IllegalArgumentException();
		}
	}

	/**
	 * Diese Methode schreibt die gegebene Anzahl an {@code byte}s des gegebenen {@code long} in das gegebene {@code byte}-Array. Das {@code index}-te
	 * {@code byte} besitzt das {@code most significant bit}.
	 * 
	 * @see #getInt(byte[], int, int)
	 * @param array {@code byte}-Array.
	 * @param index Index.
	 * @param value {@code long}.
	 * @param size Anzahl an {@code byte}s (0..8).
	 */
	public static void setLong(final byte[] array, final int index, final long value, final int size) {
		switch(size){
			case 0:
				return;
			case 1:
				Bytes.set1(array, index, (int)value);
				break;
			case 2:
				Bytes.set2(array, index, (int)value);
				break;
			case 3:
				Bytes.set3(array, index, (int)value);
				break;
			case 4:
				Bytes.set4(array, index, (int)value);
				break;
			case 5:
				Bytes.set5(array, index, value);
				break;
			case 6:
				Bytes.set6(array, index, value);
				break;
			case 7:
				Bytes.set7(array, index, value);
				break;
			case 8:
				Bytes.set8(array, index, value);
				break;
			default:
				throw new IllegalArgumentException();
		}
	}

	/**
	 * Diese Methode schreibt {@code 1 byte} des gegebenen {@code int} in das gegebene {@code byte}-Array.
	 * 
	 * @see #get1(byte[], int)
	 * @param array {@code byte}-Array.
	 * @param index Index.
	 * @param value {@code int} mit {@code 1 byte}-Wert.
	 */
	public static void set1(final byte[] array, final int index, final int value) {
		array[index + 0] = (byte)(value >>> 0);
	}

	/**
	 * Diese Methode kopiert die gegebene Anzahl an {@code byte}s aus dem gegebenen {@code int}-Array in das gegebene {@code byte}-Array, beginnend an den
	 * gegebenen Positionen. Jeder {@code int} wird mit {@code 1 byte} geschrieben.
	 * 
	 * @see #set1(byte[], int, int)
	 * @param source {@code int}-Array als Quelle.
	 * @param sourceOffset Index des ersten gelesenen {@code int}s.
	 * @param target {@code byte}-Array als Ziel.
	 * @param targetOffset Index des ersten geschriebenen {@code byte}s.
	 * @param targetCount Anzahl der kopierten {@code byte}s.
	 */
	static public void set1(final int[] source, int sourceOffset, final byte[] target, int targetOffset, int targetCount) {
		for(targetCount += targetOffset; targetOffset < targetCount; sourceOffset += 1, targetOffset += 1){
			Bytes.set1(target, targetOffset, source[sourceOffset]);
		}
	}

	/**
	 * Diese Methode schreibt {@code 2 byte} des gegebenen {@code int} in das gegebene {@code byte}-Array. Das {@code index}-te {@code byte} besitzt das
	 * {@code most significant bit}.
	 * 
	 * @see #get2(byte[], int)
	 * @param array {@code byte}-Array.
	 * @param index Index.
	 * @param value {@code int} mit {@code 2 byte}-Wert.
	 */
	public static void set2(final byte[] array, final int index, final int value) {
		array[index + 0] = (byte)(value >>> 8);
		array[index + 1] = (byte)(value >>> 0);
	}

	/**
	 * Diese Methode kopiert die gegebene Anzahl an {@code byte}s aus dem gegebenen {@code int}-Array in das gegebene {@code byte}-Array, beginnend an den
	 * gegebenen Positionen. Jeder {@code int} wird mit {@code 2 byte} geschrieben.
	 * 
	 * @see #set2(byte[], int, int)
	 * @param source {@code int}-Array als Quelle.
	 * @param sourceOffset Index des ersten gelesenen {@code int}s.
	 * @param target {@code byte}-Array als Ziel.
	 * @param targetOffset Index des ersten geschriebenen {@code byte}s.
	 * @param targetCount Anzahl der kopierten {@code byte}s.
	 */
	static public void set2(final int[] source, int sourceOffset, final byte[] target, int targetOffset, int targetCount) {
		for(targetCount += targetOffset; targetOffset < targetCount; sourceOffset += 1, targetOffset += 2){
			Bytes.set2(target, targetOffset, source[sourceOffset]);
		}
	}

	/**
	 * Diese Methode schreibt {@code 3 byte} des gegebenen {@code int} in das gegebene {@code byte}-Array. Das {@code index}-te {@code byte} besitzt das
	 * {@code most significant bit}.
	 * 
	 * @see #get3(byte[], int)
	 * @param array {@code byte}-Array.
	 * @param index Index.
	 * @param value {@code int} mit {@code 3 byte}-Wert.
	 */
	public static void set3(final byte[] array, final int index, final int value) {
		array[index + 0] = (byte)(value >>> 16);
		array[index + 1] = (byte)(value >>> 8);
		array[index + 2] = (byte)(value >>> 0);
	}

	/**
	 * Diese Methode kopiert die gegebene Anzahl an {@code byte}s aus dem gegebenen {@code int}-Array in das gegebene {@code byte}-Array, beginnend an den
	 * gegebenen Positionen. Jeder {@code int} wird mit {@code 3 byte} geschrieben.
	 * 
	 * @see #set3(byte[], int, int)
	 * @param source {@code int}-Array als Quelle.
	 * @param sourceOffset Index des ersten gelesenen {@code int}s.
	 * @param target {@code byte}-Array als Ziel.
	 * @param targetOffset Index des ersten geschriebenen {@code byte}s.
	 * @param targetCount Anzahl der kopierten {@code byte}s.
	 */
	static public void set3(final int[] source, int sourceOffset, final byte[] target, int targetOffset, int targetCount) {
		for(targetCount += targetOffset; targetOffset < targetCount; sourceOffset += 1, targetOffset += 3){
			Bytes.set3(target, targetOffset, source[sourceOffset]);
		}
	}

	/**
	 * Diese Methode schreibt {@code 4 byte} des gegebenen {@code int} in das gegebene {@code byte}-Array. Das {@code index}-te {@code byte} besitzt das
	 * {@code most significant bit}.
	 * 
	 * @see #get4(byte[], int)
	 * @param array {@code byte}-Array.
	 * @param index Index.
	 * @param value {@code int} mit {@code 4 byte}-Wert.
	 */
	public static void set4(final byte[] array, final int index, final int value) {
		array[index + 0] = (byte)(value >>> 24);
		array[index + 1] = (byte)(value >>> 16);
		array[index + 2] = (byte)(value >>> 8);
		array[index + 3] = (byte)(value >>> 0);
	}

	/**
	 * Diese Methode kopiert die gegebene Anzahl an {@code byte}s aus dem gegebenen {@code int}-Array in das gegebene {@code byte}-Array, beginnend an den
	 * gegebenen Positionen. Jeder {@code int} wird mit {@code 4 byte} geschrieben.
	 * 
	 * @see #set4(byte[], int, int)
	 * @param source {@code int}-Array als Quelle.
	 * @param sourceOffset Index des ersten gelesenen {@code int}s.
	 * @param target {@code byte}-Array als Ziel.
	 * @param targetOffset Index des ersten geschriebenen {@code byte}s.
	 * @param targetCount Anzahl der kopierten {@code byte}s.
	 */
	static public void set4(final int[] source, int sourceOffset, final byte[] target, int targetOffset, int targetCount) {
		for(targetCount += targetOffset; targetOffset < targetCount; sourceOffset += 1, targetOffset += 4){
			Bytes.set4(target, targetOffset, source[sourceOffset]);
		}
	}

	/**
	 * Diese Methode schreibt {@code 5 byte} des gegebenen {@code long} in das gegebene {@code byte}-Array. Das {@code index}-te {@code byte} besitzt das
	 * {@code most significant bit}.
	 * 
	 * @see #get5(byte[], int)
	 * @param array {@code long}-Array.
	 * @param index Index.
	 * @param value {@code long} mit {@code 5 byte}-Wert.
	 */
	public static void set5(final byte[] array, final int index, final long value) {
		Bytes.set1(array, index + 0, (int)(value >>> 32));
		Bytes.set4(array, index + 1, (int)(value >>> 0));
	}

	/**
	 * Diese Methode kopiert die gegebene Anzahl an {@code byte}s aus dem gegebenen {@code long}-Array in das gegebene {@code byte}-Array, beginnend an den
	 * gegebenen Positionen. Jeder {@code long} wird mit {@code 5 byte} geschrieben.
	 * 
	 * @see #set5(byte[], int, long)
	 * @param source {@code long}-Array als Quelle.
	 * @param sourceOffset Index des ersten gelesenen {@code long}s.
	 * @param target {@code byte}-Array als Ziel.
	 * @param targetOffset Index des ersten geschriebenen {@code byte}s.
	 * @param targetCount Anzahl der kopierten {@code byte}s.
	 */
	static public void set5(final long[] source, int sourceOffset, final byte[] target, int targetOffset, int targetCount) {
		for(targetCount += targetOffset; targetOffset < targetCount; sourceOffset += 1, targetOffset += 5){
			Bytes.set5(target, targetOffset, source[sourceOffset]);
		}
	}

	/**
	 * Diese Methode schreibt {@code 6 byte} des gegebenen {@code long} in das gegebene {@code byte}-Array. Das {@code index}-te {@code byte} besitzt das
	 * {@code most significant bit}.
	 * 
	 * @see #get6(byte[], int)
	 * @param array {@code long}-Array.
	 * @param index Index.
	 * @param value {@code long} mit {@code 6 byte}-Wert.
	 */
	public static void set6(final byte[] array, final int index, final long value) {
		Bytes.set2(array, index + 0, (int)(value >>> 32));
		Bytes.set4(array, index + 2, (int)(value >>> 0));
	}

	/**
	 * Diese Methode kopiert die gegebene Anzahl an {@code byte}s aus dem gegebenen {@code long}-Array in das gegebene {@code byte}-Array, beginnend an den
	 * gegebenen Positionen. Jeder {@code long} wird mit {@code 6 byte} geschrieben.
	 * 
	 * @see #set6(byte[], int, long)
	 * @param source {@code long}-Array als Quelle.
	 * @param sourceOffset Index des ersten gelesenen {@code long}s.
	 * @param target {@code byte}-Array als Ziel.
	 * @param targetOffset Index des ersten geschriebenen {@code byte}s.
	 * @param targetCount Anzahl der kopierten {@code byte}s.
	 */
	static public void set6(final long[] source, int sourceOffset, final byte[] target, int targetOffset, int targetCount) {
		for(targetCount += targetOffset; targetOffset < targetCount; sourceOffset += 1, targetOffset += 6){
			Bytes.set6(target, targetOffset, source[sourceOffset]);
		}
	}

	/**
	 * Diese Methode schreibt {@code 7 byte} des gegebenen {@code long} in das gegebene {@code byte}-Array. Das {@code index}-te {@code byte} besitzt das
	 * {@code most significant bit}.
	 * 
	 * @see #get7(byte[], int)
	 * @param array {@code long}-Array.
	 * @param index Index.
	 * @param value {@code long} mit {@code 7 byte}-Wert.
	 */
	public static void set7(final byte[] array, final int index, final long value) {
		Bytes.set3(array, index + 0, (int)(value >>> 32));
		Bytes.set4(array, index + 3, (int)(value >>> 0));
	}

	/**
	 * Diese Methode kopiert die gegebene Anzahl an {@code byte}s aus dem gegebenen {@code long}-Array in das gegebene {@code byte}-Array, beginnend an den
	 * gegebenen Positionen. Jeder {@code long} wird mit {@code 7 byte} geschrieben.
	 * 
	 * @see #set7(byte[], int, long)
	 * @param source {@code long}-Array als Quelle.
	 * @param sourceOffset Index des ersten gelesenen {@code long}s.
	 * @param target {@code byte}-Array als Ziel.
	 * @param targetOffset Index des ersten geschriebenen {@code byte}s.
	 * @param targetCount Anzahl der kopierten {@code byte}s.
	 */
	static public void set7(final long[] source, int sourceOffset, final byte[] target, int targetOffset, int targetCount) {
		for(targetCount += targetOffset; targetOffset < targetCount; sourceOffset += 1, targetOffset += 7){
			Bytes.set7(target, targetOffset, source[sourceOffset]);
		}
	}

	/**
	 * Diese Methode schreibt {@code 8 byte} des gegebenen {@code long} in das gegebene {@code byte}-Array. Das {@code index}-te {@code byte} besitzt das
	 * {@code most significant bit}.
	 * 
	 * @see #get8(byte[], int)
	 * @param array {@code long}-Array.
	 * @param index Index.
	 * @param value {@code long} mit {@code 8 byte}-Wert.
	 */
	public static void set8(final byte[] array, final int index, final long value) {
		Bytes.set4(array, index + 0, (int)(value >>> 32));
		Bytes.set4(array, index + 4, (int)(value >>> 0));
	}

	/**
	 * Diese Methode kopiert die gegebene Anzahl an {@code byte}s aus dem gegebenen {@code long}-Array in das gegebene {@code byte}-Array, beginnend an den
	 * gegebenen Positionen. Jeder {@code long} wird mit {@code 8 byte} geschrieben.
	 * 
	 * @see #set8(byte[], int, long)
	 * @param source {@code long}-Array als Quelle.
	 * @param sourceOffset Index des ersten gelesenen {@code long}s.
	 * @param target {@code byte}-Array als Ziel.
	 * @param targetOffset Index des ersten geschriebenen {@code byte}s.
	 * @param targetCount Anzahl der kopierten {@code byte}s.
	 */
	static public void set8(final long[] source, int sourceOffset, final byte[] target, int targetOffset, int targetCount) {
		for(targetCount += targetOffset; targetOffset < targetCount; sourceOffset += 1, targetOffset += 8){
			Bytes.set8(target, targetOffset, source[sourceOffset]);
		}
	}

	/**
	 * Diese Methode gibt die Anzahl der Byte zurück, um den gegebenen positiven Wert abzubilden.
	 * 
	 * @param value positiver Wert.
	 * @return Länge (0..4).
	 */
	public static int lengthOf(final int value) {
		return (value > 0xFFFF) ? (value > 0xFFFFFF ? 4 : 3) : (value > 0xFF ? 2 : value > 0x00 ? 1 : 0);
	}

	/**
	 * Diese Methode gibt die Anzahl der Byte zurück, um den gegebenen positiven Wert abzubilden.
	 * 
	 * @param value positiver Wert.
	 * @return Länge (0..8).
	 */
	public static int lengthOf(final long value) {
		return value > 0xFFFFFFFFL ? Bytes.lengthOf((int)(value >> 32)) + 4 : Bytes.lengthOf((int)value);
	}

	/**
	 * Dieser Konstruktor ist versteckt und verhindert damit die Erzeugung von Instanzen der Klasse.
	 */
	Bytes() {
	}

}
