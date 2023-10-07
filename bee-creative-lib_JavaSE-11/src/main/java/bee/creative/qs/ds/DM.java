package bee.creative.qs.ds;

import bee.creative.qs.QN;
import bee.creative.qs.QO;

/** Diese Schnittstelle definiert ein Domänenmodell ({@code domain-model}).
 *
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface DM extends QO {

	QN model(); // sobjekt des domänenmodells

	QN context(); // kontext für alles

	DH history(); // log oder null

	/** Diese Methode liefet das {@link DL Datenfeld} mit dem gegebenen {@link DT#node() Feldknoten}. */
	DL link(QN node);

	DLSet links(); // datenfelder, beziehungen

	/** Diese Methode liefet den {@link DL Datentyp} mit dem gegebenen {@link DT#node() Typknoten}. */
	DT type(QN node);

	DTSet types(); // datentypen

	/** Diese Methode lieferrrt das Datenfeld zur Verbindung eines beliebigen {@link QN Hyperknoten} mit dem {@link DT#node() Typknoten} seines {@link DT
	 * Datentyps}.
	 *
	 * @return Datentypfeld. */
	DL nodeTypeLink(); // in impl

	DL itemLabelLink();
	
	DL itemIdentLink();
	
}
