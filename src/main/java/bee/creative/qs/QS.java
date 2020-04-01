package bee.creative.qs;

/** Diese Klasse implementiert einen einfachen Quad-Store, d.h. einen Graphspeicher für Hypergraphen vierter Ordnung, dessen {@link QN Knoten} über eine
 * optionale externe {@link QN#value() Kennung} verfügen und dessen {@link QE Kanten} vier Knoten in den Rollen {@link QE#context() Kontext},
 * {@link QE#predicate() Prädikat}, {@link QE#subject() Subjekt} und {@link QE#object() Objekt} referenzieren. Ein Knoten kann in jeder dieser Rollen vorkommen.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
interface QS {

	QN next();

	/** Diese Methode gibt die veränderbare Menge der Kanten zurück. Änderungen an dieser Menge wirken direkt auf den Inhalt des Graphspeichers und umgekehrt.
	 *
	 * @return Menge aller Kanten. */
	QESet edges();

	QESet edges(final QN context, final QN predicate, final QN subject, final QN object) throws IllegalArgumentException;

	QN node(final String value) throws NullPointerException, IllegalArgumentException;

	QNSet nodes(final String value) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode gibt die verkleinerbare Menge aller nichtleeren {@link QN#value() Textwerte} zurück. Änderungen an dieser Menge wirken direkt auf den Inhalt
	 * des Graphspeichers und umgekehrt.
	 *
	 * @return Menge der Textwerte. */
	QVSet values();
}
