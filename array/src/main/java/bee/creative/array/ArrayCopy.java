package bee.creative.array;

public class ArrayCopy {

	/**
	 * Diese Methode ließt {@code 2} Byte aus dem gegebenen Array und gib diese als {@code int} zurück.
	 * 
	 * @param array
	 * @param index
	 * @return
	 */
	static int get2(final byte[] array, final int index) {
		return (array[index] << 8) | (array[index + 1] & 0xFF);
	}

	static int get4(final byte[] array, final int index) {
		return (array[index] << 24) | ((array[index + 1] & 0xFF) << 16) | ((array[index + 2] & 0xFF) << 8)
			| ((array[index + 3] & 0xFF) << 0);
	}

	static long get8(final byte[] array, final int index) {
		return (((long)ArrayCopy.get4(array, index)) << 32) | ArrayCopy.get4(array, index + 4);
	}

	static void set2(final byte[] array, final int index, final int value) {
		array[index + 0] = (byte)(value >> 8);
		array[index + 1] = (byte)(value >> 0);
	}

	static void set4(final byte[] array, final int index, final int value) {
		array[index + 0] = (byte)(value >> 24);
		array[index + 1] = (byte)(value >> 16);
		array[index + 2] = (byte)(value >> 8);
		array[index + 3] = (byte)(value >> 0);
	}

	static void set8(final byte[] array, final int index, final long value) {
		ArrayCopy.set4(array, index + 0, (int)(value >> 32));
		ArrayCopy.set4(array, index + 4, (int)(value >> 0));
	}

	static public void copy(final byte[] source, int sourceOffset, final char[] target, int targetOffset, int targetCount) {
		for(targetCount += targetOffset; targetOffset < targetCount; sourceOffset += 2, targetOffset += 1){
			target[targetOffset] = (char)ArrayCopy.get2(source, sourceOffset);
		}
	}

	static public void copy(final byte[] source, int sourceOffset, final short[] target, int targetOffset, int targetCount) {
		for(targetCount += targetOffset; targetOffset < targetCount; sourceOffset += 2, targetOffset += 1){
			target[targetOffset] = (short)ArrayCopy.get2(source, sourceOffset);
		}
	}

	static public void copy(final byte[] source, int sourceOffset, final int[] target, int targetOffset, int targetCount) {
		for(targetCount += targetOffset; targetOffset < targetCount; sourceOffset += 4, targetOffset += 1){
			target[targetOffset] = ArrayCopy.get4(source, sourceOffset);
		}
	}

	static public void copy(final byte[] source, int sourceOffset, final long[] target, int targetOffset, int targetCount) {
		for(targetCount += targetOffset; targetOffset < targetCount; sourceOffset += 8, targetOffset += 1){
			target[targetOffset] = ArrayCopy.get8(source, sourceOffset);
		}
	}

	static public void copy(final byte[] source, int sourceOffset, final float[] target, int targetOffset, int targetCount) {
		for(targetCount += targetOffset; targetOffset < targetCount; sourceOffset += 4, targetOffset += 1){
			target[targetOffset] = Float.intBitsToFloat(ArrayCopy.get4(source, sourceOffset));
		}
	}

	static public void copy(final byte[] source, int sourceOffset, final double[] target, int targetOffset,
		int targetCount) {
		for(targetCount += targetOffset; targetOffset < targetCount; sourceOffset += 8, targetOffset += 1){
			target[targetOffset] = Double.longBitsToDouble(ArrayCopy.get8(source, sourceOffset));
		}
	}

	static public void copy(final char[] source, int sourceOffset, final byte[] target, int targetOffset, int targetCount) {
		for(targetCount += targetOffset; targetOffset < targetCount; sourceOffset += 1, targetOffset += 2){
			ArrayCopy.set2(target, targetCount, source[sourceOffset]);
		}
	}

	static public void copy(final short[] source, int sourceOffset, final byte[] target, int targetOffset, int targetCount) {
		for(targetCount += targetOffset; targetOffset < targetCount; sourceOffset += 1, targetOffset += 2){
			ArrayCopy.set2(target, targetCount, source[sourceOffset]);
		}
	}

	static public void copy(final int[] source, int sourceOffset, final byte[] target, int targetOffset, int targetCount) {
		for(targetCount += targetOffset; targetOffset < targetCount; sourceOffset += 1, targetOffset += 4){
			ArrayCopy.set4(target, targetOffset, source[sourceOffset]);
		}
	}

	static public void copy(final float[] source, int sourceOffset, final byte[] target, int targetOffset, int targetCount) {
		for(targetCount += targetOffset; targetOffset < targetCount; sourceOffset += 1, targetOffset += 4){
			ArrayCopy.set4(target, targetOffset, Float.floatToRawIntBits(source[sourceOffset]));
		}
	}

	static public void copy(final long[] source, int sourceOffset, final byte[] target, int targetOffset, int targetCount) {
		for(targetCount += targetOffset; targetOffset < targetCount; sourceOffset += 1, targetOffset += 8){
			ArrayCopy.set8(target, targetOffset, source[sourceOffset]);
		}
	}

	static public void copy(final double[] source, int sourceOffset, final byte[] target, int targetOffset,
		int targetCount) {
		for(targetCount += targetOffset; targetOffset < targetCount; sourceOffset += 1, targetOffset += 8){
			ArrayCopy.set8(target, targetOffset, Double.doubleToRawLongBits(source[sourceOffset]));
		}
	}

}
