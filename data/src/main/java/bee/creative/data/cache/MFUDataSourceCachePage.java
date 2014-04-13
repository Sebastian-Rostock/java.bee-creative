package bee.creative.data.cache;

import bee.creative.data.Data.DataSource;

/**
 * Diese Klasse implementiert eine {@link MFUCachePage} zur Vorhaltung von Auszügen einer {@link DataSource}.
 * 
 * @see MFUDataSourceCache
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class MFUDataSourceCachePage extends MFUCachePage {

	/**
	 * Dieses Feld definiert die Anzahl der Byte in {@link #data}.
	 */
	public static final int SIZE = 2048;

	/**
	 * Dieses Feld speichert die Nutzdaten als einen Auszug einer {@link DataSource}.
	 */
	public final byte[] data = new byte[MFUDataSourceCachePage.SIZE];

}