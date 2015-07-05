package bee.creative.data;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.EOFException;
import java.io.IOException;

/**
 * Diese Schnittstelle definiert die modifizierbare Navigationsposition zum wahlfreien Lesen bzw. Schreben von Daten.
 * 
 * @see DataInput
 * @see DataOutput
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public interface Data {

	/**
	 * Diese Methode gibt die intern verwalteten Nutzdaten zurück.
	 * 
	 * @return Nutzdaten.
	 */
	public Object data();

	/**
	 * Diese Methode setzt die Navigationsposition der Eingabe, ab der die nächsten {@code byte}s gelesen bzw. geschrieben werden können.
	 * 
	 * @see #index()
	 * @param index Leseposition.
	 * @throws IOException Wenn die gegebene Position negativ ist oder ein I/O-Fehler auftritt.
	 */
	public void seek(long index) throws IOException;

	/**
	 * Diese Methode gibt die aktuelle Navigationsposition zurück, ab der die nächsten {@code byte}s gelesen bzw. geschrieben werden können.
	 * 
	 * @see #seek(long)
	 * @return Leseposition.
	 * @throws IOException Wenn ein I/O-Fehler auftritt.
	 */
	public long index() throws IOException;

	/**
	 * Diese Methode gibt die aktuelle Länge der Nutzdaten als Anzahl von {@code byte}s zurück. Wenn die Navigationsposition bem Lesen größer oder gleich dieser
	 * Anzahl werden würde, wird beim Lesen eine {@link EOFException} ausgelöst. Beim Schreiben wird die Anzahl automatisch vergrößert, wenn dies nötig wird.
	 * 
	 * @see #index()
	 * @return Anzahl verfügbarer {@code byte}s.
	 * @throws IOException Wenn ein I/O-Fehler auftritt.
	 */
	public long length() throws IOException;

}