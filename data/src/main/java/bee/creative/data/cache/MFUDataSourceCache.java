package bee.creative.data.cache;

import java.io.IOException;
import bee.creative.data.Data.DataSource;

/**
 * Diese Klasse implementiert einen {@link MFUCache} zur Verwaltung und Vorhaltung von Auszügen einer {@link DataSource}.
 * <p>
 * Die Auszüge werden in {@link MFUDataSourceCachePage}s verwaltet, welche bis zu {@link MFUDataSourceCachePage#SIZE} Byte vorhalten.
 * 
 * @see DataSource
 * @see MFUCache
 * @see MFUDataSourceCachePage
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class MFUDataSourceCache extends MFUCache {

	/**
	 * Dieses Feld speichert die {@link MFUDataSourceCachePage}s.
	 */
	MFUDataSourceCachePage[] pages = {};

	/**
	 * Dieses Feld speichert die Größe der Nutzdatenstrukturen in {@link #source}.
	 */
	int length;

	/**
	 * Dieses Feld speichert den Beginn der Nutzdatenstrukturen in {@link #source}.
	 */
	final long offset;

	/**
	 * Dieses Feld speichert die {@link DataSource}.
	 */
	final DataSource source;

	/**
	 * Dieser Konstruktor initialisiert {@link #source}, {@link #offset} und {@link #pageLimit}. Die {@link #pages} sowie die {@link #length} werden via
	 * {@link #allocate(int)} gesetzt.
	 * 
	 * @param source {@link DataSource}.
	 * @param pageLimit maximale Anzahl der gleichzeitig verwalteten {@link MFUDataSourceCachePage}s.
	 * @throws IOException Wenn ein I/O-Fehler auftritt.
	 * @throws NullPointerException Wenn die {@link DataSource} {@code null} ist.
	 */
	public MFUDataSourceCache(final DataSource source, final int pageLimit) throws IOException, NullPointerException {
		super(pageLimit);
		this.source = source;
		this.offset = source.index();
	}

	/**
	 * Diese Methode gibt den {@link MFUDataSourceCachePage#data Nutzdatenblock} der {@code index}-ten {@link MFUDataSourceCachePage} zurück. Diese wird bei
	 * Bedarf aus der {@link #source} nachgeladen. Die Vergrängung überzähliger {@link MFUDataSourceCachePage}s erfolgt gemäß
	 * {@link #set(MFUCachePage[], int, MFUCachePage)}.
	 * 
	 * @param pageIndex Index der {@link MFUDataSourceCachePage}.
	 * @return {@link MFUDataSourceCachePage#data Nutzdatenblock}.
	 */
	public byte[] get(final int pageIndex) {
		final MFUDataSourceCachePage[] pages = this.pages;
		{
			final MFUDataSourceCachePage page = pages[pageIndex];
			if(page != null){
				page.uses++;
				return page.data;
			}
		}
		{
			final MFUDataSourceCachePage page = new MFUDataSourceCachePage();
			final byte[] data = page.data;
			final int offset = pageIndex * MFUDataSourceCachePage.SIZE;
			try{
				final DataSource source = this.source;
				source.seek(this.offset + offset);
				source.readFully(data, 0, Math.min(MFUDataSourceCachePage.SIZE, this.length - offset));
			}catch(final Exception e){
				throw new IllegalStateException(e);
			}
			this.set(pages, pageIndex, page);
			return data;
		}
	}

	/**
	 * Diese Methode liest die gegebene Anzahl an {@code byte} via {@link DataSource#readInt(int)} aus {@link #source()} und gibt diese als {@code int}
	 * interpreteirt zurück.
	 * 
	 * @see DataSource#readInt(int)
	 * @param size Anzahl der {@code byte}s.
	 * @return {@code byte}s interpretiert als {@code int}.
	 * @throws IOException Wenn beim Lesen ein Fehler auftritt.
	 */
	public int read(final int size) throws IOException {
		return this.source.readInt(size);
	}

	/**
	 * Diese Methode gibt die {@link DataSource} mit den Nutzdatenstrukturen zurück.
	 * 
	 * @return {@link DataSource} mit den Nutzdatenstrukturen.
	 */
	public DataSource source() {
		return this.source;
	}

	/**
	 * Diese Methode gibt den Beginn der Nutzdatenstrukturen in {@link #source()} zurück.
	 * 
	 * @return Beginn der Nutzdatenstrukturen.
	 */
	public long offset() {
		return this.offset;
	}

	/**
	 * Diese Methode gibt die Größe der Nutzdatenstrukturen in {@link #source()} zurück. Diese kann via {@link #allocate(int)} gesetzt werden.
	 * 
	 * @return Größe der Nutzdatenstrukturen.
	 */
	public int length() {
		return this.length;
	}

	/**
	 * Diese Methode setzt die Größe der Nutzdatenstrukturen in {@link #source} und erzeugt dazu die passende Anzahl an {@link MFUDataSourceCachePage}s.
	 * 
	 * @param length Größe der Nutzdatenstrukturen in {@link #source}.
	 */
	public void allocate(final int length) {
		this.pages = new MFUDataSourceCachePage[((length + MFUDataSourceCachePage.SIZE) - 1) / MFUDataSourceCachePage.SIZE];
		this.pageCount = 0;
		this.length = length;
	}

}