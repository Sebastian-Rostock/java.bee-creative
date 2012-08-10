package bee.creative.xml.coder;

import java.nio.charset.Charset;
import bee.creative.array.ArrayCopy;
import bee.creative.xml.coder.Decoder.DecodeDocument;
import bee.creative.xml.coder.Decoder.DecodeLabel;
import bee.creative.xml.coder.Decoder.DecodeValue;
import bee.creative.xml.coder.Encoder.EncodeLabel;
import bee.creative.xml.coder.Encoder.EncodeValue;

/**
 * Diese Klasse implementiert Methoden zur Kodierung und Dekodierung von Indices und Zeichenketten.
 * 
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class Coder {

	/**
	 * Dieses Feld speichert das leere {@code int}-Array.
	 */
	static final int[] VOID_INTS = new int[0];

	/**
	 * Dieses Feld speichert das leere {@code byte}-Array.
	 */
	static final byte[] VOID_BYTES = new byte[0];

	/**
	 * Dieses Feld speichert das in {@link #encodeChars(String)} und {@link #decodeChars(byte[])} verwendete
	 * {@link Charset}.
	 */
	static final Charset CHARSET = Charset.forName("UTF-8");

	static final String MESSAGE_NULL_VALUE = "value is null";

	static final String MESSAGE_NULL_INDICES = "indices is null";

	/**
	 * Diese Methode gibt den {@link Object#hashCode() Streuwert} des gegebenen {@link String}s zurück, der in den
	 * {@code Hash}es des {@link DecodeDocument}s verwendet werden.
	 * 
	 * @see EncodeValue#value()
	 * @see DecodeValue#value()
	 * @see DecodeDocument#uriHash()
	 * @see DecodeDocument#valueHash()
	 * @see DecodeDocument#xmlnsNameHash()
	 * @see DecodeDocument#elementNameHash()
	 * @see DecodeDocument#attributeNameHash()
	 * @param value {@link String}.
	 * @return {@link Object#hashCode() Streuwert} des {@link String}s.
	 * @throws NullPointerException Wenn der gegebene {@link String} {@code null} ist.
	 */
	public static int hashValue(final String value) throws NullPointerException {
		if(value == null) throw new NullPointerException(MESSAGE_NULL_VALUE);
		int hash = 0x811C9DC5;
		for(int i = 0, length = value.length(); i < length; i++){
			hash = (hash * 0x01000193) ^ value.charAt(i);
		}
		return hash;
	}

	/**
	 * Diese Methode gibt den {@link Object#hashCode() Streuwert} der gegebenen Indices zurück, der in den {@code Hash}es
	 * des {@link DecodeDocument}s verwendet werden.
	 * 
	 * @see EncodeLabel#uri()
	 * @see EncodeLabel#name()
	 * @see DecodeLabel#uri()
	 * @see DecodeLabel#name()
	 * @see DecodeDocument#xmlnsNameHash()
	 * @see DecodeDocument#elementNameHash()
	 * @see DecodeDocument#attributeNameHash()
	 * @param uri {@code URI}-Index.
	 * @param name {@code Name}-Index.
	 * @return {@link Object#hashCode() Streuwert} der Indices.
	 */
	public static int hashLabel(final int uri, final int name) {
		return (uri * 23) ^ (name * 97);
	}

	/**
	 * Diese Methode kodiert den gegebenen {@link String} in ein {@code byte}-Array und gibt dieses zurück.
	 * 
	 * @see String#getBytes(Charset)
	 * @param value {@link String}.
	 * @return {@code byte}-Array.
	 * @throws NullPointerException Wenn der gegebene {@link String} {@code null} ist.
	 */
	public static byte[] encodeChars(final String value) throws NullPointerException {
		if(value == null) throw new NullPointerException(MESSAGE_NULL_VALUE);
		return value.getBytes(Coder.CHARSET);
	}

	/**
	 * Diese Methode dekodiert das gegebene {@code int}-Array in eine {@code byte}-Array und gibt dieses zurück.
	 * 
	 * @see ArrayCopy#copy(int[], int, byte[], int, int)
	 * @param value {@code int}-Array.
	 * @return {@code byte}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code int}-Array {@code null} ist.
	 */
	public static byte[] encodeIndices(final int... value) throws NullPointerException {
		if(value == null) throw new NullPointerException(MESSAGE_NULL_VALUE);
		final int length = value.length << 2;
		if(length == 0) return Coder.VOID_BYTES;
		final byte[] array = new byte[length];
		ArrayCopy.copy(value, 0, array, 0, length);
		return array;
	}

	/**
	 * Diese Methode dekodiert das gegebene {@code byte}-Array in einen {@link String} und gibt diesen zurück.
	 * 
	 * @see String#String(byte[], Charset)
	 * @param value {@code byte}-Array.
	 * @return {@link String}.
	 * @throws NullPointerException Wenn das gegebene {@code byte}-Array {@code null} ist.
	 */
	public static String decodeChars(final byte[] value) throws NullPointerException {
		if(value == null) throw new NullPointerException(MESSAGE_NULL_VALUE);
		return new String(value, Coder.CHARSET);
	}

	/**
	 * Diese Methode dekodiert das gegebene {@code byte}-Array in eine {@code int}-Array und gibt dieses zurück.
	 * 
	 * @see ArrayCopy#copy(byte[], int, int[], int, int)
	 * @param value {@code byte}-Array.
	 * @return {@code int}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code byte}-Array {@code null} ist.
	 */
	public static int[] decodeIndices(final byte... value) throws NullPointerException {
		if(value == null) throw new NullPointerException(MESSAGE_NULL_VALUE);
		final int length = value.length >> 2;
		if(length == 0) return Coder.VOID_INTS;
		final int[] array = new int[length];
		ArrayCopy.copy(value, 0, array, 0, length);
		return array;
	}

}
