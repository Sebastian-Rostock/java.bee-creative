package bee.creative.qs.ds;

import bee.creative.qs.QESet;
import bee.creative.qs.QN;
import bee.creative.util.Property3;

/** Diese Schnittstelle definiert eine Domänenänderung (domain-change).
 *
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface DC extends DO {

	/** Dieses Feld speichert den Textwert eines {@link DE#identsAsNodes() Erkennungsknoten} für den {@link DT Datentyp} von {@link DC}. */
	String IDENT_IsChange = "DS:IsChange";

	/** Dieses Feld speichert den Textwert eines {@link DE#identsAsNodes() Erkennungsknoten} für das {@link #putContext()}-{@link DL Datenfeld}. */
	String IDENT_IsChangeWithPutContext = "DS:IsChangeWithPutContext";

	/** Dieses Feld speichert den Textwert eines {@link DE#identsAsNodes() Erkennungsknoten} für das {@link #popContext()}-{@link DL Datenfeld}. */
	String IDENT_IsChangeWithPopContext = "DS:IsChangeWithPopContext";

	/** Dieses Feld speichert den Textwert eines {@link DE#identsAsNodes() Erkennungsknoten} für das {@link #nextAsNode()}-{@link DL Datenfeld}. */
	String IDENT_IsChangeWithNextChange = "DS:IsChangeWithNextChange";

	QN node();

	default DH history() {
		return this.parent().history();
	}

	default Property3<DC> next() {
		return nextAsNode().translate(history().changeTrans());
	}

	default Property3<QN> nextAsNode() {
		return this.parent().getLink(DC.IDENT_IsChangeWithNextChange).getObjects(this.node()).asNode();
	}

	default Property3<DC> prev() {
		return this.prevAsNode().translate(this.history().changeTrans());
	}

	default Property3<QN> prevAsNode() {
		return this.parent().getLink(DC.IDENT_IsChangeWithNextChange).getSubjects(this.node()).asNode();
	}

	default QESet putEdges() {
		return this.owner().edges().havingContext(this.putContext().get());
	}

	default Property3<QN> putContext() {
		return this.parent().getLink(DC.IDENT_IsChangeWithPutContext).getObjects(this.node()).asNode();
	}

	default QESet popEdges() {
		return this.owner().edges().havingContext(this.popContext().get());
	}

	default Property3<QN> popContext() {
		return this.parent().getLink(DC.IDENT_IsChangeWithPopContext).getObjects(this.node()).asNode();
	}

}
