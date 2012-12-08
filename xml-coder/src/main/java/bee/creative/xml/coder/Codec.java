package bee.creative.xml.coder;

/**
 * Diese Schnittstelle definiert definiert zwei Methoden zur Umwandlung eines {@link String}s in ein {@code byte}-Array und umgekehrt.
 * 
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public interface Codec {

	/**
	 * Diese Methode versucht den gegebenen {@link String} in ein {@code byte}-Array umzuwandeln und gibt dieses oder {@code null} zurück. Der Rückgabewert {@code null} zeigt dabei an, dass die Eingabe nicht kodiert werden konnte.
	 * 
	 * @param value {@link String}.
	 * @return {@code byte}-Array oder {@code null}.
	 * @throws NullPointerException Wenn der gegebene {@link String} {@code null} ist.
	 */
	public byte[] encode(String value) throws NullPointerException;

	/**
	 * Diese Methode versucht das gegebene {@code byte}-Array in einen {@link String} umzuwandeln und gibt diesen oder {@code null} zurück. Der Rückgabewert {@code null} zeigt dabei an, dass die Eingabe nicht dekodiert werden konnte.
	 * 
	 * @param value {@code byte}-Array.
	 * @return {@link String} oder {@code null}.
	 * @throws NullPointerException Wenn das gegebene {@code byte}-Array {@code null} ist.
	 */
	public String decode(byte[] value) throws NullPointerException;

}
