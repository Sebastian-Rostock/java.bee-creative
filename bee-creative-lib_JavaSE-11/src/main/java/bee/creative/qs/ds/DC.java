package bee.creative.qs.ds;

import bee.creative.qs.QESet;
import bee.creative.qs.QN;
import bee.creative.util.Property3;

/** Diese Schnittstelle definiert eine Domänenänderung (domain-change).
 *
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface DC extends DN {

	/** Dieses Feld speichert den Textwert eines {@link DE#identsAsNodes() Erkennungsknoten} für den {@link DT Datentyp} von {@link DC}. */
	String IDENT_IsChange = "DS:IsChange";

	/** Dieses Feld speichert den Textwert eines {@link DE#identsAsNodes() Erkennungsknoten} für das {@link #recordingPutContext()}-{@link DL Datenfeld}. */
	String IDENT_IsChangeWithPutContext = "DS:IsChangeWithPutContext";

	/** Dieses Feld speichert den Textwert eines {@link DE#identsAsNodes() Erkennungsknoten} für das {@link #recordingPopContext()}-{@link DL Datenfeld}. */
	String IDENT_IsChangeWithPopContext = "DS:IsChangeWithPopContext";

	/** Dieses Feld speichert den Textwert eines {@link DE#identsAsNodes() Erkennungsknoten} für das {@link #nextChangeAsNode()}-{@link DL Datenfeld}. */
	String IDENT_IsChangeWithNextChange = "DS:IsChangeWithNextChange";

	default boolean isEmpty() {
		return this.recordedPutEdges().isEmpty() && this.recordedPopEdges().isEmpty();
	}

	default void undoChanges() {
		var parent = this.parent();
		parent.putEdges(this.recordedPopEdges());
		parent.popEdges(this.recordedPutEdges());
	}

	default void redoChanges() {
		var parent = this.parent();
		parent.putEdges(this.recordedPutEdges());
		parent.popEdges(this.recordedPopEdges());
	}

	default Property3<DC> nextChange() {
		return this.nextChangeAsNode().translate(this.parent().changeTrans());
	}

	default Property3<QN> nextChangeAsNode() {
		return this.parent().getLink(DC.IDENT_IsChangeWithNextChange).getObjects(this.node()).asNode();
	}

	default Property3<DC> prevChange() {
		return this.prevChangeAsNode().translate(this.parent().changeTrans());
	}

	default Property3<QN> prevChangeAsNode() {
		return this.parent().getLink(DC.IDENT_IsChangeWithNextChange).getSubjects(this.node()).asNode();
	}

	default QESet recordedPutEdges() {
		return this.owner().edges().havingContext(this.recordingPutContext().get());
	}

	default QESet recordedPopEdges() {
		return this.owner().edges().havingContext(this.recordingPopContext().get());
	}

	default Property3<QN> recordingPutContext() {
		return this.parent().getLink(DC.IDENT_IsChangeWithPutContext).getObjects(this.node()).asNode();
	}

	default Property3<QN> recordingPopContext() {
		return this.parent().getLink(DC.IDENT_IsChangeWithPopContext).getObjects(this.node()).asNode();
	}

}
