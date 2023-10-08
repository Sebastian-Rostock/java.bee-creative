package bee.creative.qs.ds;

import bee.creative.qs.QE;
import bee.creative.qs.QESet;
import bee.creative.qs.QN;
import bee.creative.qs.QO;
import bee.creative.qs.QS;
import bee.creative.util.Set2;
import bee.creative.util.Translator2;

/** Diese Schnittstelle definiert ein Domänenmodell ({@code domain-model}).
 *
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface DM extends QO {

	/** Diese Methode liefert die Mengensicht auf alle gespeicherten {@link QE Hyperkanten} mit dem {@link #context() Kontextknoten} dieses Datenmodells.
	 *
	 * @return Hyperkanten mit {@link #context()}. */
	default QESet edges() {
		var context = this.context();
		return context.owner().edges().havingContext(context);
	}

	@Override
	default QS owner() {
		return this.context().owner();
	}

	QN model(); // sobjekt des domänenmodells

	QN context(); // kontext für alles

	DH history(); // log oder null

	/** Diese Methode lieferrrt das Datenfeld zur Verbindung eines beliebigen {@link QN Hyperknoten} mit dem {@link DT#node() Typknoten} seines {@link DT
	 * Datentyps}.
	 *
	 * @return Datentypfeld. */
	DL nodeTypeLink(); // in impl

	/** Diese Methode liefert das {@link DL Datenfeld} zu {@link DE#label()}.
	 *
	 * @see DE#IDENT_ITEM_HAS_LABEL
	 * @return Beschriftungsdatenfeld. */
	DL itemLabelLink();

	DL itemIdentLink();

	default Set2<QN> links() { // datenfelder, beziehungen
		return this.modelLinkLink().getTargetProxy(this.model());
	}

	default Set2<DL> linksAsLinks() {
		return this.links().translate(this.linkTrans());
	}

	Translator2<QN, DL> linkTrans();

	DL linkSourceLink();

	DL linkTargetLink();

	DL linkClonabilityLink();

	DL linkMultiplicityLink();

	default Set2<QN> types() { // datentypen
		return this.modelLinkLink().getTargetProxy(this.model());
	}

	default Set2<DT> typesAsTypes() {
		return this.types().translate(this.typeTrans());
	}

	Translator2<QN, DT> typeTrans();

	DL modelTypeLink();

	DL modelLinkLink();

	default boolean putEdges(Iterable<? extends QE> edges) throws NullPointerException, IllegalArgumentException {
		var history = this.history();
		return DS.putEdges(this.context(), edges, history != null ? history.putContext() : null, history != null ? history.popContext() : null);
	}

	default boolean popEdges(Iterable<? extends QE> edges) throws NullPointerException, IllegalArgumentException {
		var history = this.history();
		return DS.popEdges(this.context(), edges, history != null ? history.putContext() : null, history != null ? history.popContext() : null);
	}

}
