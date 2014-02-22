package bee.creative.xml.bex;

import java.io.DataOutput;
import java.io.IOException;

/**
 * Diese Schnittstelle definiert die Ausgabe eines {@link Encoder}s.
 * 
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public interface EncodeTarget {

	/**
	 * Diese Methode kopiert die gegebene Anzahl an {@code byte}s aus dem gegebenen {@code byte}-Array ab dem gegebenen Index in die Ausgabe an deren aktuelle
	 * Schreibposition und setzt diese Schreibposition anschließend an das Ende des soeben geschriebenen Datenbereiches.
	 * 
	 * @see DataOutput#write(byte[], int, int)
	 * @param array {@code byte}-Array.
	 * @param offset Index des ersten geschriebenen {@code byte}s.
	 * @param length Anzahl der geschriebenen {@code byte}s.
	 * @throws IOException Wenn beim Schreiben ein Fehler auftritt.
	 */
	public void write(byte[] array, int offset, int length) throws IOException;

}