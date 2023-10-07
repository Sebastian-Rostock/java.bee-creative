package bee.creative.qs.ds;

import bee.creative.qs.QESet;
import bee.creative.qs.QN;
import bee.creative.qs.QO;
import bee.creative.qs.QS;

/** Diese Schnittstelle definiert ein Domänenmodell ({@code domain-model}).
 *
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface DM extends QO {

	@Override
	default QS owner() {
		return this.context().owner();
	}

	default QESet edges() {
		var context = this.context();
		return context.owner().edges().havingContext(context);
	}

	QN model(); // sobjekt des domänenmodells

	QN context(); // kontext für alles

	/** Diese Methode liefet das {@link DL Datenfeld} mit dem gegebenen {@link DT#node() Feldknoten}. */
	DL link(QN node);

	DLSet links(); // datenfelder, beziehungen

	/** Diese Methode liefet den {@link DL Datentyp} mit dem gegebenen {@link DT#node() Typknoten}. */
	DT type(QN node);

	DTSet types(); // datentypen

	DH history(); // log oder null

	DL nodeAsLink(QN node);
	DT nodeAsType(QN node);

	DLSet nodesAsLinks(Iterable<? extends QN> nodes);

	DTSet nodesAsTypes(Iterable<? extends QN> nodes);

	/** Diese Methode lieferrrt das Datenfeld zur Verbindung eines beliebigen {@link QN Hyperknoten} mit dem {@link DT#node() Typknoten} seines {@link DT
	 * Datentyps}.
	 *
	 * @return Datentypfeld. */
	DL nodeTypeLink(); // in impl

	/** Diese Methode liefert das {@link DL Datenfeld} zu {@link DE#label()}.
	 *
	 * @see DE#IDENT_ITEM_HAS_LABEL
	 * @return {@link DE#label()}-{@link DL Datenfeld}. */
	DL itemLabelLink();

	DL itemIdentLink();

	DL linkSourceLink();

	DL linkTargetLink();

	DL linkClonabilityLink();

	DL linkMultiplicityLink();

}
