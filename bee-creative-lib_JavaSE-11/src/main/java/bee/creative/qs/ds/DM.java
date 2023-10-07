package bee.creative.qs.ds;

import bee.creative.qs.QE;
import bee.creative.qs.QESet;
import bee.creative.qs.QN;
import bee.creative.qs.QO;
import bee.creative.qs.QS;
import bee.creative.util.AbstractTranslator;
import bee.creative.util.Set2;
import bee.creative.util.Translator2;

/** Diese Schnittstelle definiert ein Domänenmodell ({@code domain-model}).
 *
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface DM extends QO {

	@Override
	default QS owner() {
		return this.context().owner();
	}

	/** Diese Methode liefert die Mengensicht auf alle gespeicherten {@link QE Hyperkanten} mit dem {@link #context() Kontextknoten} dieses Datenmodells.
	 *
	 * @return Hyperkanten mit {@link #context()}. */
	default QESet edges() {
		var context = this.context();
		return context.owner().edges().havingContext(context);
	}

	QN model(); // sobjekt des domänenmodells

	QN context(); // kontext für alles

	DH history(); // log oder null

	/** Diese Methode liefet das {@link DL Datenfeld} mit dem gegebenen {@link DT#node() Feldknoten}. */
	DL nodeAsLink(QN node);

	/** Diese Methode liefet den {@link DL Datentyp} mit dem gegebenen {@link DT#node() Typknoten}. */
	DT nodeAsType(QN node);

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

	DL linkSourceLink();

	DL linkTargetLink();

	DL linkClonabilityLink();

	DL linkMultiplicityLink();

	DL modelTypesLink();

	DL modelLinksLink();

	default Translator2<QN, DL> linkTrans() {
		return new AbstractTranslator<>() {
	
			@Override
			public boolean isTarget(Object object) {
				return object instanceof DL;
			}
	
			@Override
			public boolean isSource(Object object) {
				return object instanceof QN;
			}
	
			@Override
			public DL toTarget(Object object) throws ClassCastException, IllegalArgumentException {
				return object != null ? DM.this.nodeAsLink((QN)object) : null;
			}
	
			@Override
			public QN toSource(Object object) throws ClassCastException, IllegalArgumentException {
				return object != null ? ((DL)object).node() : null;
			}
	
		};
	}

	default Set2<QN> links() { // datenfelder, beziehungen
		return this.modelLinksLink().getTargetProxy(this.model());
	}

	default Set2<DL> linksAsLinks() {
		return this.links().translate(this.linkTrans());
	}

	default Translator2<QN, DT> typeTrans() {
		return new AbstractTranslator<>() {
	
			@Override
			public boolean isTarget(Object object) {
				return object instanceof DT;
			}
	
			@Override
			public boolean isSource(Object object) {
				return object instanceof QN;
			}
	
			@Override
			public DT toTarget(Object object) throws ClassCastException, IllegalArgumentException {
				return object != null ? DM.this.nodeAsType((QN)object) : null;
			}
	
			@Override
			public QN toSource(Object object) throws ClassCastException, IllegalArgumentException {
				return object != null ? ((DT)object).node() : null;
			}
	
		};
	}

	default Set2<QN> types() { // datentypen
		return this.modelLinksLink().getTargetProxy(this.model());
	}

	default Set2<DT> typesAsTypes() {
		return this.types().translate(this.typeTrans());
	}

}
