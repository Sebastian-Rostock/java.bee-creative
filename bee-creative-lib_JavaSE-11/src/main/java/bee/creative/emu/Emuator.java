package bee.creative.emu;

/** Diese Schnittstelle definiert eine Methode zur Ermittlung des geschätzten Speicherverbrauchs eines gegebenen Objekts. Dieser Speicherverbrauch sollte den
 * der ausschließlich intern verwalteten Objekte mit einschließen.
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GInput> Typ des gegebenen Objekts. */
public interface Emuator<GInput> {

	/** Diese Methode gibt den geschätzten Speicherverbrauch des gegebenen Objekts zurück.
	 *
	 * @param input Objekt.
	 * @return Speicherverbrauch.
	 * @throws NullPointerException Wenn {@code input} {@code null} ist. */
	long emu(GInput input) throws NullPointerException;

}
