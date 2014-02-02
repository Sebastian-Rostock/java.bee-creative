package bee.creative.xml.bex;

import java.io.DataInput;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Diese Schnittstelle definiert die Eingabe eines {@link Decoder}s.
 * 
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public interface DecodeSource {

	/**
	 * Diese Methode liest die gegebene Anzahl an {@code byte}s ab der aktuellen Leseposition aus der Eingabe in das gegebene {@code byte}-Array an die gegebene
	 * Position ein und vergrößert die Leseposition um die gegebene Anzahl.
	 * 
	 * @see #index()
	 * @see DataInput#readFully(byte[], int, int)
	 * @param array {@code byte}-Array.
	 * @param offset Index des ersten gelesenen {@code byte}s.
	 * @param length Anzahl der zulesenden {@code byte}s.
	 * @throws IOException Wenn beim Lesen ein Fehler auftritt.
	 */
	public void read(byte[] array, int offset, int length) throws IOException;

	/**
	 * Diese Methode setzt die Leseposition der Eingabe, ab der via {@link #read(byte[], int, int)} die nächsten {@code byte}s gelesen werden können.
	 * 
	 * @see #index()
	 * @see RandomAccessFile#seek(long)
	 * @param index Leseposition.
	 * @throws IOException Wenn die gegebene Position negativ ist oder ein I/O-Fehler auftritt.
	 */
	public void seek(long index) throws IOException;

	/**
	 * Diese Methode gibt die aktuelle Leseposition zurück, ab der via {@link #read(byte[], int, int)} die nächsten {@code byte}s gelesen werden können.
	 * 
	 * @see #seek(long)
	 * @see RandomAccessFile#getFilePointer()
	 * @return Leseposition.
	 * @throws IOException Wenn ein I/O-Fehler auftritt.
	 */
	public long index() throws IOException;

}