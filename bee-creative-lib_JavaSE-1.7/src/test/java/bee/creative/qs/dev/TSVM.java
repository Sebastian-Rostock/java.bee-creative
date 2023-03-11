package bee.creative.qs.dev;

import java.sql.SQLException;
import java.util.List;
import bee.creative.qs.QE;
import bee.creative.qs.QESet;
import bee.creative.qs.QN;
import bee.creative.qs.QS;

// lesen der daten einer revision
public class TSVM {

	final QS target;

	final QN domainContext;

	final QN activePredicate;

	/** Dieses Feld speichert den {@link QE#predicate() Prädikatknoten} zur Verbindung einer {@link QE#subject() Zweigversion} mit seiner {@link QE#object()
	 * Stammversion}. */
	final QN branchPredicate;

	/** Dieses Feld speichert den {@link QE#predicate() Prädikatknoten} zur Verbindung einer {@link QE#subject() Nachfolgeversion} mit seiner {@link QE#object()
	 * Vorgängerversion}. */
	final QN sourcePredicate;

	/** Dieses Feld speichert den {@link QE#predicate() Prädikatknoten} zur Verbindung eines {@link QE#subject() Versionseintrags} mit seinem {@link QE#object()
	 * Ergänzungskontext}. */
	final QN insertPredicate;

	/** Dieses Feld speichert den {@link QE#predicate() Prädikatknoten} zur Verbindung eines {@link QE#subject() Versionseintrags} mit seinem {@link QE#object()
	 * Entfernungskontext}. */
	final QN deletePredicate;

	/** Dieses Feld speichert die Sicht auf {@link QS#edges() alle Hyperkanten} des {@link #target}. */
	final QESet targetEdges;

	/** Dieses Feld speichert die Sicht auf alle Hyperkanten in {@link #targetEdges} mit dem {@link #domainContext}. */
	final QESet domainEdges;

	/** Dieses Feld speichert die Sicht auf alle Hyperkanten in {@link #domainEdges} mit dem {@link #activePredicate}. */
	final QESet activeEdges;

	/** Dieses Feld speichert die Sicht auf alle Hyperkanten in {@link #domainEdges} mit dem {@link #sourcePredicate}. */
	final QESet sourceEdges;

	/** Dieses Feld speichert die Sicht auf alle Hyperkanten in {@link #domainEdges} mit dem {@link #insertPredicate}. */
	final QESet insertEdges;

	/** Dieses Feld speichert die Sicht auf alle Hyperkanten in {@link #domainEdges} mit dem {@link #deletePredicate}. */
	final QESet deleteEdges;

	QESet branchEdges;

	public TSVM(final QS target) throws SQLException {
		this.target = target;
		this.domainContext = target.newNode("TSVM:DOMAIN");
		this.activePredicate = target.newNode("TSVM:ACTIVE");
		this.branchPredicate = target.newNode("TSVM:BRANCH");
		this.sourcePredicate = target.newNode("TSVM:SOURCE");
		this.insertPredicate = target.newNode("TSVM:INSERT");
		this.deletePredicate = target.newNode("TSVM:DELETE");
		this.targetEdges = target.edges();
		this.domainEdges = this.targetEdges.havingContext(this.domainContext);
		this.activeEdges = this.domainEdges.havingPredicate(this.activePredicate);
		this.branchEdges = this.domainEdges.havingPredicate(this.branchPredicate);
		this.sourceEdges = this.domainEdges.havingPredicate(this.sourcePredicate);
		this.insertEdges = this.domainEdges.havingPredicate(this.insertPredicate);
		this.deleteEdges = this.domainEdges.havingPredicate(this.deletePredicate);

		final List<QN> activeList =
			target.edges().havingContext(this.domainContext).havingPredicate(this.activePredicate).havingSubject(this.domainContext).objects().toList();
		if (activeList.isEmpty()) {

		} else if (activeList.size() == 1) {

		} else throw new SQLException();

	}

	public void check() {

		// todo alle TSVH iterieren und check aufrufen

	}

}
