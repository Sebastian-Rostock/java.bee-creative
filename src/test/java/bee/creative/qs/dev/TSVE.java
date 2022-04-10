package bee.creative.qs.dev;

import bee.creative.lang.Objects;
import bee.creative.qs.QE;
import bee.creative.qs.QN;

/** Diese Klasse implementiert den Versionseintrag (Triple-Store-Version-Entry) eines {@code TSVH Versionsverlaufs}.
 *
 * @author [cc-by] 2022 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
class TSVE {

	/** Dieses Feld speichert den {@link QE#subject() Subjektknoten} der Version. Diesem ist in {@link TSVM#insertEdges} der {@link #insert}, in
	 * {@link TSVM#deleteEdges} der {@link #delete} und in {@link TSVM#sourceEdges} die Vorgängerversion jeweils als {@link QE#object() Objektknoten}
	 * zugeordnet. */
	final QN entry;

	/** Dieses Feld speichert den {@link QE#context() Kontextknoten} der {@link QE Hyperkanten}, die gegenüber der vorherigen Version eingefügt wurden. */
	final QN insert;

	/** Dieses Feld speichert den {@link QE#context() Kontextknoten} der {@link QE Hyperkanten}, die gegenüber der vorherigen Version entfernt wurden. */
	final QN delete;

	/** Dieses Feld puffert die Nachfolgeversion. */
	TSVE next;

	TSVE(final TSVM owner) {
		this.entry = owner.target.newNode();
		this.insert = owner.target.newNode();
		this.delete = owner.target.newNode();
		owner.target.newEdge(owner.domainContext, owner.insertPredicate, this.entry, this.insert).put();
		owner.target.newEdge(owner.domainContext, owner.deletePredicate, this.entry, this.delete).put();
	}

	TSVE(final TSVM owner, final QN node) {
		this.entry = node;
		this.insert = Objects.notNull(owner.insertEdges.havingSubject(node).objects().first());
		this.delete = Objects.notNull(owner.deleteEdges.havingSubject(node).objects().first());
	}

}