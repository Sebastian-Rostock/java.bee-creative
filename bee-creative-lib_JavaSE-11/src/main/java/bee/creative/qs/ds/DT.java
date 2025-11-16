package bee.creative.qs.ds;

import bee.creative.qs.QE;
import bee.creative.qs.QN;
import bee.creative.qs.QNSet;
import bee.creative.util.Property3;
import bee.creative.util.Set2;

/** Diese Schnittstelle definiert einen Datentyp (Domain-Type) als {@link #labelAsNode() beschriftete} und {@link #identsAsNodes() erkennbare}
 * {@link #instancesAsNodes() Instanzmenge}.
 *
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface DT extends DE {

	/** Dieses Feld speichert den Textwert eines {@link DE#identsAsNodes() Erkennungsknoten} für den {@link DT Datentyp} von {@link DT}. */
	String IDENT_IsType = "DS:IsType";

	/** Dieses Feld speichert den Textwert eines {@link DE#identsAsNodes() Erkennungsknoten} für das {@link DE#labelAsNode()}-{@link DL Datenfeld}. */
	String IDENT_IsTypeWithLabel = "DS:IsTypeWithLabel";

	/** Dieses Feld speichert den Textwert eines {@link DE#identsAsNodes() Erkennungsknoten} für das {@link DE#identsAsNodes()}-{@link DL Datenfeld}. */
	String IDENT_IsTypeWithIdent = "DS:IsTypeWithIdent";

	/** Dieses Feld speichert den Textwert eines {@link DE#identsAsNodes() Erkennungsknoten} für das {@link DT#instancesAsNodes()}-{@link DL Datenfeld}. */
	String IDENT_IsTypeWithInstance = "DS:IsTypeWithInstance";

	/** {@inheritDoc}
	 *
	 * @see #IDENT_IsTypeWithLabel */
	@Override
	default Property3<QN> labelAsNode() {
		return this.parent().getLink(DT.IDENT_IsTypeWithLabel).asObjectProperty(this.node());
	}

	/** {@inheritDoc}
	 *
	 * @see #IDENT_IsTypeWithIdent */
	@Override
	default Set2<QN> identsAsNodes() {
		return this.parent().getLink(DT.IDENT_IsTypeWithIdent).asObjectSet(this.node());
	}

	/** Diese Methode erlaubt Zugriff auf die {@link QN Hyperknoten} der explizit diesem Datentyp zugeordneten Instanzen. Eine Instanz darf nur einen Datentyp
	 * besitzen. Hyperknoten mit {@link QN#value() Textwert} sind als Instanz nicht zulässig.
	 *
	 * @see DT#IDENT_IsTypeWithInstance
	 * @see DL#asObjectSet(QN)
	 * @return Instanzknoten. */
	default Set2<QN> instancesAsNodes() {
		return this.parent().getLink(DT.IDENT_IsTypeWithInstance).asObjectSet(this.node());
	}

	/** Diese Methode erlaubt Zugriff auf die {@link QN Hyperknoten}, die als {@link QE#subject() Subjektknoten} der Kanten mit {@link QE#subject()
	 * Prädikatknoten} aus {@link #subjectLinksAsNodes()} und die als {@link QE#object() Objektknoten} der Kanten mit {@link QE#subject() Prädikatknoten} aus
	 * {@link #objectLinksAsNodes()}.
	 *
	 * @return Exemplarknoten. */
	default QNSet specimens() {
		var owner = this.owner();
		var edges = owner.edges();
		return edges.havingPredicates(owner.newNodes(this.subjectLinksAsNodes())).subjects()
			.union(edges.havingPredicates(owner.newNodes(this.objectLinksAsNodes())).objects());
	}

	/** Diese Methode erlaubt Zugriff auf die diesen Datentyp als {@link DL#subjectType() Subjektdatentyp} zulassenden {@link DL Datenfelder}.
	 *
	 * @see DT#subjectLinksAsNodes()
	 * @see DM#linkTrans()
	 * @return Subjektdatenfelder. */
	default Set2<DL> subjectLinks() {
		return this.subjectLinksAsNodes().asTranslatedSet(this.parent().linkTrans());
	}

	/** Diese Methode erlaubt Zugriff auf die {@link DL#node() Hyperknoten} der diesen Datentyp als {@link DL#subjectType() Subjektdatentyp} zulassenden {@link DL
	 * Datenfelder}.
	 *
	 * @see DL#IDENT_IsLinkWithSubjectType
	 * @see DL#asSubjectSet(QN)
	 * @return Subjektdatenfeldknoten. */
	default Set2<QN> subjectLinksAsNodes() {
		return this.parent().getLink(DL.IDENT_IsLinkWithSubjectType).asSubjectSet(this.node());
	}

	/** Diese Methode erlaubt Zugriff auf die diesen Datentyp als {@link DL#objectType() Objektdatentyp} zulassenden {@link DL Datenfelder}.
	 *
	 * @see DT#objectLinksAsNodes()
	 * @see DM#linkTrans()
	 * @return Objektdatenfelder. */
	default Set2<DL> objectLinks() {
		return this.objectLinksAsNodes().asTranslatedSet(this.parent().linkTrans());
	}

	/** Diese Methode erlaubt Zugriff auf die {@link DL#node() Hyperknoten} der diesen Datentyp als {@link DL#objectType() Objektdatentyp} zulassenden {@link DL
	 * Datenfelder}.
	 *
	 * @see DL#IDENT_IsLinkWithObjectType
	 * @see DL#asSubjectSet(QN)
	 * @return Objektdatenfeldknoten. */
	default Set2<QN> objectLinksAsNodes() {
		return this.parent().getLink(DL.IDENT_IsLinkWithObjectType).asSubjectSet(this.node());
	}

}
