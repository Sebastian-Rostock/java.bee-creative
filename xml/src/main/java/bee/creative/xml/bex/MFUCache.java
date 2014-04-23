package bee.creative.xml.bex;

/**
 * Diese Klasse implementiert ein Objekt zur Verwaltung und Vorhaltung von {@link MFUCachePage}s.<br>
 * Die verdrängung überzähliger {@link MFUCachePage}s erfolgt gemäß einer <i>most frequently used</i> Strategie.
 * 
 * @see MFUCachePage
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public abstract class MFUCache {

	/**
	 * Dieses Feld speichert die maximale Anzahl der gleichzeitig verwalteten {@link MFUCachePage}s.
	 */
	protected int pageLimit;

	/**
	 * Dieses Feld speichert die Anzahl der aktuell verwalteten {@link MFUCachePage}s. Dies wird in {@link #set(MFUCachePage[], int, MFUCachePage)} modifiziert.
	 */
	protected int pageCount;

	/**
	 * Dieser Konstruktor initialisiert {@link #pageLimit}.
	 * 
	 * @param pageLimit {@link #pageLimit}.
	 */
	public MFUCache(final int pageLimit) {
		this.pageLimit = Math.max(pageLimit, 1);
		this.pageCount = 0;
	}

	/**
	 * Diese Methode setzt die {@code index}-te {@link MFUCachePage} und verdrängt ggf. überzählige {@link MFUCachePage}s. <br>
	 * Es wird nicht geprüft, ob die gegebene bzw. die {@code index}-te {@link MFUCachePage} im gegebenen Array {@code null} ist.
	 * 
	 * @param pages Array der {@link MFUCachePage}s, in welchen aktuell {@link #pageCount} {@link MFUCachePage}s verwaltet werden.
	 * @param index Index der zusetzenden {@link MFUCachePage}.
	 * @param page zusetzende {@link MFUCachePage}.
	 */
	protected final void set(final MFUCachePage[] pages, final int index, final MFUCachePage page) {
		int pageCount = this.pageCount, pageLimit = this.pageLimit;
		if(pageCount >= pageLimit){
			pageLimit = pageLimit < 0 ? 1 : (pageLimit + 1) / 2;
			final int size = pages.length;
			while(pageCount > pageLimit){
				int uses = 0;
				final int maxUses = Integer.MAX_VALUE / pageCount;
				for(int i = 0; i < size; i++){
					final MFUCachePage item = pages[i];
					if(item != null){
						uses += (item.uses = Math.min(item.uses, maxUses - i));
					}
				}
				final int minUses = uses / pageCount;
				for(int i = 0; i < size; i++){
					final MFUCachePage item = pages[i];
					if((item != null) && ((item.uses -= minUses) <= 0)){
						pages[i] = null;
						pageCount--;
					}
				}
			}
		}
		this.pageCount = pageCount + 1;
		pages[index] = page;
	}

	/**
	 * Diese Methode gibt die maximale Anzahl der gleichzeitig verwalteten {@link MFUCachePage}s zurück.
	 * 
	 * @return maximale Anzahl der gleichzeitig verwalteten {@link MFUCachePage}s.
	 */
	public int getPageLimit() {
		return this.pageLimit;
	}

	/**
	 * Diese Methode setzt die maximale Anzahl der gleichzeitig verwalteten {@link MFUCachePage}s. <br>
	 * Wenn die Anzahl der aktuell verwalteten {@link MFUCachePage}s die maximale Anzahl überschreitet, wird die hälfte der {@link MFUCachePage}s entfernt.
	 * 
	 * @param value maximale Anzahl der gleichzeitig verwalteten {@link MFUCachePage}s.
	 */
	public void setPageLimit(final int value) {
		this.pageLimit = value;
	}

}