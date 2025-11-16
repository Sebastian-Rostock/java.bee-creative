package bee.creative.qs.ds;

import bee.creative.qs.QN;
import bee.creative.util.Property3;
import bee.creative.util.Set2;
import bee.creative.util.Translator;
import bee.creative.util.Translator3;
import bee.creative.util.Translators;

// TODO
// domain-history
public interface DH extends DO {

	String IDENT_IsModelWithChange = "DS:IsChangeWithPrevious";

	String IDENT_IsModelWithHistory = "DS:IsChangeWithPrevious";

	// gab es änderungen?
	default boolean todo() {
		var current = this.current().get();
		return (current != null) && (!current.recordedPutEdges().isEmpty() || !current.recordedPopEdges().isEmpty());
	}

	// änderung abschließen und neue beginnen
	void done();

	default Property3<DC> current() {
		return this.currentAsNode().translate(this.changeTrans());
	}

	default Property3<QN> currentAsNode() {
		var parent = this.parent();
		return parent.getLink(DH.IDENT_IsModelWithChange).getObjects(parent.context()).asNode();
	}

	default QN currentPutContext() {
		var current = this.current().get();
		return current != null ? current.recordingPutContext().get() : null;
	}

	default QN currentPopContext() {
		var current = this.current().get();
		return current != null ? current.recordingPopContext().get() : null;
	}

	default Set2<DC> changes() {
		return this.changesAsNodes().asTranslatedSet(this.changeTrans());
	}

	default Set2<QN> changesAsNodes() {
		var parent = this.parent();
		return parent.getLink(DH.IDENT_IsModelWithHistory).getObjects(parent.context()).asNodeSet();
	}

	/** Diese Methode liefert den {@link Translators#optionalizedTranslator(Translator) optionalisierten} {@link DC#node() Änderungsknoten}-{@link DC
	 * Domänenänderung}-{@link Translator}. */
	Translator3<QN, DC> changeTrans();

}
