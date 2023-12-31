package bee.creative.qs.ds;

import bee.creative.qs.QN;
import bee.creative.util.Property2;
import bee.creative.util.Set2;
import bee.creative.util.Translator;
import bee.creative.util.Translator2;
import bee.creative.util.Translators.OptionalizedTranslator;

// TODO
// domain-history
public interface DH extends DO {

	String IDENT_IsModelWithChange = "DS:IsChangeWithPrevious";

	String IDENT_IsModelWithHistory = "DS:IsChangeWithPrevious";

	// gab es änderungen?
	default boolean todo() {
		var current = this.current().get();
		return !current.putEdges().isEmpty() || !current.popEdges().isEmpty();
	}

	// änderung abschließen und neue beginnen
	void done();

	default QN putContext() {
		var current = this.current().get();
		return current != null ? current.putContext().get() : null;
	}

	default QN popContext() {
		var current = this.current().get();
		return current != null ? current.popContext().get() : null;
	}

	default Property2<DC> current() {
		return this.currentAsNode().translate(this.changeTrans());
	}

	default Property2<QN> currentAsNode() {
		var parent = this.parent();
		return parent.getLink(DH.IDENT_IsModelWithChange).getTargets(parent.context()).asNode();
	}

	default Set2<DC> changes() {
		return this.changesAsNodes().translate(this.changeTrans());
	}

	default Set2<QN> changesAsNodes() {
		var parent = this.parent();
		return parent.getLink(DH.IDENT_IsModelWithHistory).getTargets(parent.context()).asNodeSet();
	}

	/** Diese Methode liefert den {@link OptionalizedTranslator optionalisierten} {@link DC#node() Änderungsknoten}-{@link DC
	 * Domänenänderung}-{@link Translator}. */
	Translator2<QN, DC> changeTrans();

}
