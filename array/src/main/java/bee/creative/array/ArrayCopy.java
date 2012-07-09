package bee.creative.array;

/**
 * Diese Klasse implementiert Methoden zum konvertierenden Kopieren von {@code byte}-Arrays.
 * 
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class ArrayCopy {

	/**
	 * Diese Methode ließt {@code 2 byte} aus dem gegebenen Array und gib diese als {@code int} interpretiert zurück. Das
	 * erste {@code byte} besitzt das {@code most significant bit}, das letzte hat das {@code least significant bit}.
	 * 
	 * @see #set2(byte[], int, int)
	 * @param array {@code byte}-Array.
	 * @param index Index.
	 * @return {@code 2 byte} als {@code int}.
	 */
	public static int get2(final byte[] array, final int index) {
		return (array[index] << 8) | (array[index + 1] & 0xFF);
	}

	/**
	 * Diese Methode ließt {@code 4 byte} aus dem gegebenen Array und gib diese als {@code int} interpretiert zurück. Das
	 * erste {@code byte} besitzt das {@code most significant bit}, das letzte hat das {@code least significant bit}.
	 * 
	 * @see #set4(byte[], int, int)
	 * @param array {@code byte}-Array.
	 * @param index Index.
	 * @return {@code 4 byte} als {@code int}.
	 */
	public static int get4(final byte[] array, final int index) {
		return (array[index] << 24) | ((array[index + 1] & 0xFF) << 16) | ((array[index + 2] & 0xFF) << 8)
			| ((array[index + 3] & 0xFF) << 0);
	}

	/**
	 * Diese Methode ließt {@code 8 byte} aus dem gegebenen Array und gib diese als {@code long} interpretiert zurück. Das
	 * erste {@code byte} besitzt das {@code most significant bit}, das letzte hat das {@code least significant bit}.
	 * 
	 * @see #set8(byte[], int, long)
	 * @param array {@code byte}-Array.
	 * @param index Index.
	 * @return {@code 8 byte} als {@code long}.
	 */
	public static long get8(final byte[] array, final int index) {
		return (((long)ArrayCopy.get4(array, index)) << 32) | ArrayCopy.get4(array, index + 4);
	}

	/**
	 * Diese Methode schreibt den {@code 2 byte}-Wert des gegebenen {@code int} in das gegebene Array. Das erste
	 * {@code byte} stammt von den Bitporitionen {@code 15..8}, das letzte von {@code 7..0}.
	 * 
	 * @see #get2(byte[], int)
	 * @param array {@code byte}-Array.
	 * @param index Index.
	 * @param value {@code int} mit {@code 2 byte} Wert.
	 */
	public static void set2(final byte[] array, final int index, final int value) {
		array[index + 0] = (byte)(value >> 8);
		array[index + 1] = (byte)(value >> 0);
	}

	/**
	 * Diese Methode schreibt den {@code 4 byte}-Wert des gegebenen {@code int} in das gegebene Array. Das erste
	 * {@code byte} stammt von den Bitporitionen {@code 31..24}, das letzte von {@code 7..0}.
	 * 
	 * @see #get4(byte[], int)
	 * @see #set4(byte[], int, int)
	 * @param array {@code byte}-Array.
	 * @param index Index.
	 * @param value {@code int} mit {@code 2 byte} Wert.
	 */
	public static void set4(final byte[] array, final int index, final int value) {
		array[index + 0] = (byte)(value >> 24);
		array[index + 1] = (byte)(value >> 16);
		array[index + 2] = (byte)(value >> 8);
		array[index + 3] = (byte)(value >> 0);
	}

	/**
	 * Diese Methode schreibt den {@code 8 byte}-Wert des gegebenen {@code int} in das gegebene Array. Das erste
	 * {@code byte} stammt von den Bitporitionen {@code 63..56}, das letzte von {@code 7..0}.
	 * 
	 * @see #get4(byte[], int)
	 * @see #set4(byte[], int, int)
	 * @param array {@code byte}-Array.
	 * @param index Index.
	 * @param value {@code int} mit {@code 2 byte} Wert.
	 */
	public static void set8(final byte[] array, final int index, final long value) {
		ArrayCopy.set4(array, index + 0, (int)(value >> 32));
		ArrayCopy.set4(array, index + 4, (int)(value >> 0));
	}

	/**
	 * Diese Methode kopiert die gegebene Anzahl an {@code char}s aus dem gegebenen {@code byte}-Array in das gegebene
	 * {@code char}-Array, beginnend an den gegebenen Positionen. Der gleiche Effekt kann mit deutlich größerer Rechenzeit
	 * auch durch folgende Anweisung erreicht werden:
	 * 
	 * <pre>ByteBuffer.wrap(source, sourceOffset, source.length - sourceOffset).asCharBuffer().put(target, targetOffset, targetCount);</pre>
	 * 
	 * @see #get2(byte[], int)
	 * @param source {@code byte}-Array als Quelle.
	 * @param sourceOffset Index des ersten gelesenen {@code byte}s.
	 * @param target {@code char}-Array als Ziel.
	 * @param targetOffset Index des ersten geschriebenen {@code char}s.
	 * @param targetCount Anzahl der kopierten {@code char}s.
	 */
	static public void copy(final byte[] source, int sourceOffset, final char[] target, int targetOffset, int targetCount) {
		for(targetCount += targetOffset; targetOffset < targetCount; sourceOffset += 2, targetOffset += 1){
			target[targetOffset] = (char)ArrayCopy.get2(source, sourceOffset);
		}
	}

	/**
	 * Diese Methode kopiert die gegebene Anzahl an {@code short}s aus dem gegebenen {@code byte}-Array in das gegebene
	 * {@code short}-Array, beginnend an den gegebenen Positionen. Der gleiche Effekt kann mit deutlich größerer
	 * Rechenzeit auch durch folgende Anweisung erreicht werden:
	 * 
	 * <pre>ByteBuffer.wrap(source, sourceOffset, source.length - sourceOffset).asShortBuffer().put(target, targetOffset, targetCount);</pre>
	 * 
	 * @see #get2(byte[], int)
	 * @param source {@code byte}-Array als Quelle.
	 * @param sourceOffset Index des ersten gelesenen {@code byte}s.
	 * @param target {@code short}-Array als Ziel.
	 * @param targetOffset Index des ersten geschriebenen {@code short}s.
	 * @param targetCount Anzahl der kopierten {@code short}s.
	 */
	static public void copy(final byte[] source, int sourceOffset, final short[] target, int targetOffset, int targetCount) {
		for(targetCount += targetOffset; targetOffset < targetCount; sourceOffset += 2, targetOffset += 1){
			target[targetOffset] = (short)ArrayCopy.get2(source, sourceOffset);
		}
	}

	/**
	 * Diese Methode kopiert die gegebene Anzahl an {@code int}s aus dem gegebenen {@code byte}-Array in das gegebene
	 * {@code int}-Array, beginnend an den gegebenen Positionen. Der gleiche Effekt kann mit deutlich größerer Rechenzeit
	 * auch durch folgende Anweisung erreicht werden:
	 * 
	 * <pre>ByteBuffer.wrap(source, sourceOffset, source.length - sourceOffset).asIntBuffer().put(target, targetOffset, targetCount);</pre>
	 * 
	 * @see #get4(byte[], int)
	 * @param source {@code byte}-Array als Quelle.
	 * @param sourceOffset Index des ersten gelesenen {@code byte}s.
	 * @param target {@code int}-Array als Ziel.
	 * @param targetOffset Index des ersten geschriebenen {@code int}s.
	 * @param targetCount Anzahl der kopierten {@code int}s.
	 */
	static public void copy(final byte[] source, int sourceOffset, final int[] target, int targetOffset, int targetCount) {
		for(targetCount += targetOffset; targetOffset < targetCount; sourceOffset += 4, targetOffset += 1){
			target[targetOffset] = ArrayCopy.get4(source, sourceOffset);
		}
	}

	/**
	 * Diese Methode kopiert die gegebene Anzahl an {@code long}s aus dem gegebenen {@code byte}-Array in das gegebene
	 * {@code long}-Array, beginnend an den gegebenen Positionen. Der gleiche Effekt kann mit deutlich größerer Rechenzeit
	 * auch durch folgende Anweisung erreicht werden:
	 * 
	 * <pre>ByteBuffer.wrap(source, sourceOffset, source.length - sourceOffset).asLongBuffer().put(target, targetOffset, targetCount);</pre>
	 * 
	 * @see #get8(byte[], int)
	 * @param source {@code byte}-Array als Quelle.
	 * @param sourceOffset Index des ersten gelesenen {@code byte}s.
	 * @param target {@code long}-Array als Ziel.
	 * @param targetOffset Index des ersten geschriebenen {@code long}s.
	 * @param targetCount Anzahl der kopierten {@code long}s.
	 */
	static public void copy(final byte[] source, int sourceOffset, final long[] target, int targetOffset, int targetCount) {
		for(targetCount += targetOffset; targetOffset < targetCount; sourceOffset += 8, targetOffset += 1){
			target[targetOffset] = ArrayCopy.get8(source, sourceOffset);
		}
	}

	/**
	 * Diese Methode kopiert die gegebene Anzahl an {@code float}s aus dem gegebenen {@code byte}-Array in das gegebene
	 * {@code float}-Array, beginnend an den gegebenen Positionen. Der gleiche Effekt kann mit deutlich größerer
	 * Rechenzeit auch durch folgende Anweisung erreicht werden:
	 * 
	 * <pre>ByteBuffer.wrap(source, sourceOffset, source.length - sourceOffset).asFloatBuffer().put(target, targetOffset, targetCount);</pre>
	 * 
	 * @see #get4(byte[], int)
	 * @see Float#intBitsToFloat(int)
	 * @param source {@code byte}-Array als Quelle.
	 * @param sourceOffset Index des ersten gelesenen {@code byte}s.
	 * @param target {@code float}-Array als Ziel.
	 * @param targetOffset Index des ersten geschriebenen {@code float}s.
	 * @param targetCount Anzahl der kopierten {@code float}s.
	 */
	static public void copy(final byte[] source, int sourceOffset, final float[] target, int targetOffset, int targetCount) {
		for(targetCount += targetOffset; targetOffset < targetCount; sourceOffset += 4, targetOffset += 1){
			target[targetOffset] = Float.intBitsToFloat(ArrayCopy.get4(source, sourceOffset));
		}
	}

	/**
	 * Diese Methode kopiert die gegebene Anzahl an {@code double}s aus dem gegebenen {@code byte}-Array in das gegebene
	 * {@code double}-Array, beginnend an den gegebenen Positionen. Der gleiche Effekt kann mit deutlich größerer
	 * Rechenzeit auch durch folgende Anweisung erreicht werden:
	 * 
	 * <pre>ByteBuffer.wrap(source, sourceOffset, source.length - sourceOffset).asDoubleBuffer().put(target, targetOffset, targetCount);</pre>
	 * 
	 * @see #get8(byte[], int)
	 * @see Double#longBitsToDouble(long)
	 * @param source {@code byte}-Array als Quelle.
	 * @param sourceOffset Index des ersten gelesenen {@code byte}s.
	 * @param target {@code double}-Array als Ziel.
	 * @param targetOffset Index des ersten geschriebenen {@code double}s.
	 * @param targetCount Anzahl der kopierten {@code double}s.
	 */
	static public void copy(final byte[] source, int sourceOffset, final double[] target, int targetOffset,
		int targetCount) {
		for(targetCount += targetOffset; targetOffset < targetCount; sourceOffset += 8, targetOffset += 1){
			target[targetOffset] = Double.longBitsToDouble(ArrayCopy.get8(source, sourceOffset));
		}
	}

	/**
	 * Diese Methode kopiert die gegebene Anzahl an {@code byte}s aus dem gegebenen {@code char}-Array in das gegebene
	 * {@code byte}-Array, beginnend an den gegebenen Positionen. Der gleiche Effekt kann mit deutlich größerer Rechenzeit
	 * auch durch folgende Anweisung erreicht werden:
	 * 
	 * <pre>ByteBuffer.wrap(target, targetOffset, targetCount).asCharBuffer().put(source, sourceOffset, targetCount >> 1);</pre>
	 * 
	 * @see #set2(byte[], int, int)
	 * @param source {@code char}-Array als Quelle.
	 * @param sourceOffset Index des ersten gelesenen {@code char}s.
	 * @param target {@code byte}-Array als Ziel.
	 * @param targetOffset Index des ersten geschriebenen {@code byte}s.
	 * @param targetCount Anzahl der kopierten {@code byte}s.
	 */
	static public void copy(final char[] source, int sourceOffset, final byte[] target, int targetOffset, int targetCount) {
		for(targetCount += targetOffset; targetOffset < targetCount; sourceOffset += 1, targetOffset += 2){
			ArrayCopy.set2(target, targetCount, source[sourceOffset]);
		}
	}

	/**
	 * Diese Methode kopiert die gegebene Anzahl an {@code byte}s aus dem gegebenen {@code short}-Array in das gegebene
	 * {@code byte}-Array, beginnend an den gegebenen Positionen. Der gleiche Effekt kann mit deutlich größerer Rechenzeit
	 * auch durch folgende Anweisung erreicht werden:
	 * 
	 * <pre>ByteBuffer.wrap(target, targetOffset, targetCount).asShortBuffer().put(source, sourceOffset, targetCount >> 1);</pre>
	 * 
	 * @see #set2(byte[], int, int)
	 * @param source {@code short}-Array als Quelle.
	 * @param sourceOffset Index des ersten gelesenen {@code short}s.
	 * @param target {@code byte}-Array als Ziel.
	 * @param targetOffset Index des ersten geschriebenen {@code byte}s.
	 * @param targetCount Anzahl der kopierten {@code byte}s.
	 */
	static public void copy(final short[] source, int sourceOffset, final byte[] target, int targetOffset, int targetCount) {
		for(targetCount += targetOffset; targetOffset < targetCount; sourceOffset += 1, targetOffset += 2){
			ArrayCopy.set2(target, targetCount, source[sourceOffset]);
		}
	}

	/**
	 * Diese Methode kopiert die gegebene Anzahl an {@code byte}s aus dem gegebenen {@code int}-Array in das gegebene
	 * {@code byte}-Array, beginnend an den gegebenen Positionen. Der gleiche Effekt kann mit deutlich größerer Rechenzeit
	 * auch durch folgende Anweisung erreicht werden:
	 * 
	 * <pre>ByteBuffer.wrap(target, targetOffset, targetCount).asIntBuffer().put(source, sourceOffset, targetCount >> 2);</pre>
	 * 
	 * @see #set4(byte[], int, int)
	 * @param source {@code int}-Array als Quelle.
	 * @param sourceOffset Index des ersten gelesenen {@code int}s.
	 * @param target {@code byte}-Array als Ziel.
	 * @param targetOffset Index des ersten geschriebenen {@code byte}s.
	 * @param targetCount Anzahl der kopierten {@code byte}s.
	 */
	static public void copy(final int[] source, int sourceOffset, final byte[] target, int targetOffset, int targetCount) {
		for(targetCount += targetOffset; targetOffset < targetCount; sourceOffset += 1, targetOffset += 4){
			ArrayCopy.set4(target, targetOffset, source[sourceOffset]);
		}
	}

	/**
	 * Diese Methode kopiert die gegebene Anzahl an {@code byte}s aus dem gegebenen {@code float}-Array in das gegebene
	 * {@code byte}-Array, beginnend an den gegebenen Positionen. Der gleiche Effekt kann mit deutlich größerer Rechenzeit
	 * auch durch folgende Anweisung erreicht werden:
	 * 
	 * <pre>ByteBuffer.wrap(target, targetOffset, targetCount).asFloatBuffer().put(source, sourceOffset, targetCount >> 2);</pre>
	 * 
	 * @see #set4(byte[], int, int)
	 * @see Float#floatToRawIntBits(float)
	 * @param source {@code float}-Array als Quelle.
	 * @param sourceOffset Index des ersten gelesenen {@code float}s.
	 * @param target {@code byte}-Array als Ziel.
	 * @param targetOffset Index des ersten geschriebenen {@code byte}s.
	 * @param targetCount Anzahl der kopierten {@code byte}s.
	 */
	static public void copy(final float[] source, int sourceOffset, final byte[] target, int targetOffset, int targetCount) {
		for(targetCount += targetOffset; targetOffset < targetCount; sourceOffset += 1, targetOffset += 4){
			ArrayCopy.set4(target, targetOffset, Float.floatToRawIntBits(source[sourceOffset]));
		}
	}

	/**
	 * Diese Methode kopiert die gegebene Anzahl an {@code byte}s aus dem gegebenen {@code long}-Array in das gegebene
	 * {@code byte}-Array, beginnend an den gegebenen Positionen. Der gleiche Effekt kann mit deutlich größerer Rechenzeit
	 * auch durch folgende Anweisung erreicht werden:
	 * 
	 * <pre>ByteBuffer.wrap(target, targetOffset, targetCount).asLongBuffer().put(source, sourceOffset, targetCount >> 2);</pre>
	 * 
	 * @see #set8(byte[], int, long)
	 * @param source {@code long}-Array als Quelle.
	 * @param sourceOffset Index des ersten gelesenen {@code long}s.
	 * @param target {@code byte}-Array als Ziel.
	 * @param targetOffset Index des ersten geschriebenen {@code byte}s.
	 * @param targetCount Anzahl der kopierten {@code byte}s.
	 */
	static public void copy(final long[] source, int sourceOffset, final byte[] target, int targetOffset, int targetCount) {
		for(targetCount += targetOffset; targetOffset < targetCount; sourceOffset += 1, targetOffset += 8){
			ArrayCopy.set8(target, targetOffset, source[sourceOffset]);
		}
	}

	/**
	 * Diese Methode kopiert die gegebene Anzahl an {@code byte}s aus dem gegebenen {@code double}-Array in das gegebene
	 * {@code byte}-Array, beginnend an den gegebenen Positionen. Der gleiche Effekt kann mit deutlich größerer Rechenzeit
	 * auch durch folgende Anweisung erreicht werden:
	 * 
	 * <pre>ByteBuffer.wrap(target, targetOffset, targetCount).asDoubleBuffer().put(source, sourceOffset, targetCount >> 2);</pre>
	 * 
	 * @see #set8(byte[], int, long)
	 * @see Double#doubleToRawLongBits(double)
	 * @param source {@code double}-Array als Quelle.
	 * @param sourceOffset Index des ersten gelesenen {@code double}s.
	 * @param target {@code byte}-Array als Ziel.
	 * @param targetOffset Index des ersten geschriebenen {@code byte}s.
	 * @param targetCount Anzahl der kopierten {@code byte}s.
	 */
	static public void copy(final double[] source, int sourceOffset, final byte[] target, int targetOffset,
		int targetCount) {
		for(targetCount += targetOffset; targetOffset < targetCount; sourceOffset += 1, targetOffset += 8){
			ArrayCopy.set8(target, targetOffset, Double.doubleToRawLongBits(source[sourceOffset]));
		}
	}

	/**
	 * Dieser Konstrukteur ist versteckt und verhindert damit die Erzeugung von Instanzen der Klasse.
	 */
	ArrayCopy() {
	}

}
