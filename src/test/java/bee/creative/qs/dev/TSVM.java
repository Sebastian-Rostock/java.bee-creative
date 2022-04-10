package bee.creative.qs.dev;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import bee.creative.qs.QE;
import bee.creative.qs.QESet;
import bee.creative.qs.QN;
import bee.creative.qs.QNSet;
import bee.creative.qs.QS;
import bee.creative.util.HashMap2;
import bee.creative.util.HashSet;
import bee.creative.util.Iterables;

// lesen der daten einer revision
public class TSVM {

	final QS target;

	@Deprecated
	private QN activeVersion;

	@Deprecated
	QN insertContext;

	@Deprecated
	QN deleteContext;

	@Deprecated
	QN mergedContext;

	final QN activePredicate;

	/** Dieses Feld speichert den {@link QE#predicate() Prädikatknoten} zur Verbindung einer {@link QE#subject() Nachfolgerevision} mit seiner {@link QE#object()
	 * Vorgängerrevision}. */
	final QN sourcePredicate;

	/** Dieses Feld speichert den {@link QE#predicate() Prädikatknoten} zur Verbindung eines {@link QE#subject() Revisionsknoten} mit seinem {@link QE#object()
	 * Ergänzungskontext}. */
	final QN insertPredicate;

	/** Dieses Feld speichert den {@link QE#predicate() Prädikatknoten} zur Verbindung eines {@link QE#subject() Revisionsknoten} mit seinem {@link QE#object()
	 * Entfernungskontext}. */
	final QN deletePredicate;

	@Deprecated
	final QN mergedPredicate;

	final QN domainContext;

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

	public TSVM(final QS store) throws SQLException {
		this.target = store;
		this.domainContext = store.newNode("QSVM:DOMAIN");
		this.insertPredicate = store.newNode("QSVM:INSERT");
		this.deletePredicate = store.newNode("QSVM:DELETE");
		this.sourcePredicate = store.newNode("QSVM:SOURCE");

		this.activePredicate = store.newNode("QSVM:ACTIVE");

		this.mergedPredicate = store.newNode("QSVM:MERGED");

		this.targetEdges = store.edges();
		this.domainEdges = this.targetEdges.havingContext(this.domainContext);
		this.activeEdges = this.domainEdges.havingPredicate(this.activePredicate);
		this.sourceEdges = this.domainEdges.havingPredicate(this.sourcePredicate);
		this.insertEdges = this.domainEdges.havingPredicate(this.insertPredicate);
		this.deleteEdges = this.domainEdges.havingPredicate(this.deletePredicate);

		final List<QN> activeList =
			store.edges().havingContext(this.domainContext).havingPredicate(this.activePredicate).havingSubject(this.domainContext).objects().toList();
		if (activeList.isEmpty()) {

		} else if (activeList.size() == 1) {

		} else throw new SQLException();

	}




	/** Diese Methode gibt den {@link QE#subject() Subjektknoten} der aktuellen Version, welcher auf die {@link QE#context() Kontextknoten} des
	 * {@link #mergedContext effektiven}, {@link #insertContext eingefügten} und {@link #deleteContext entfernten} Wissens verweist. */
	public QN getActiveVersion() {
		return this.activeVersion;
	}

	public QN getInsertKnowledge() {
		return this.insertContext;
	}

	public QN getDeleteKnowledge() {
		return this.deleteContext;
	}

	/** Diese Methode liefert den {@link QE#context() Kontextknoten} der {@link QE Hyperkanten} in der {@link #activeVersion aktuellen Version}. Diese Version
	 * beinhaltet die Hyperkanten der vorherigen Version mit den darin {@link #getInsertKnowledge() eingefügten} und ohne den daraus {@link #getDeleteKnowledge()
	 * entfernten}. */
	public QN getMergedKnowledge() {
		return this.mergedContext;
	}

	public Set<QN> getPredicatesFromObjects(final QN... objects) {
		return this.getPredicatesFromObjects(Arrays.asList(objects));
	}

	/** Diese Methode gibt die Menge der {@link QE#predicate() Prädikatknote} der {@link QE Hyperkanten} mit den gegebenen {@link QE#object() Objektknoten}
	 * zurück. Dabei werden nur die Hyperkanten im {@link #getMergedKnowledge() effektiven Wissen} der {@link #getActiveVersion() aktuellen Version}
	 * berücksichtigt.
	 *
	 * @param objects Objektknoten.
	 * @return Prädikatknoten. */
	public Set<QN> getPredicatesFromObjects(final Iterable<? extends QN> objects) {
		return this.getPredicatesFromObjectsImpl(this.mergedContext, this.target.newNodes(objects));
	}

	Set<QN> getPredicatesFromObjectsImpl(final QN mergedContext, final QNSet objects) {
		return this.target.edges().havingContext(mergedContext).havingObjects(objects).predicates().toSet();
	}

	public Set<QN> getPredicatesFromSubjects(final QNSet subjects) {
		return this.getPredicatesFromSubjects(this.mergedContext, subjects);
	}

	Set<QN> getPredicatesFromSubjects(final QN mergedContext, final QNSet subjects) {
		// (verwendete Objekte bzw. verwendende Subjekte; inverse Prädikate)
		return this.target.edges().havingContext(mergedContext).havingSubjects(subjects).predicates().toSet();
	}

	Set<QN> getObjectsFromSubjectsMin(final QN mc, final QN pn, final QNSet si) {
		// - Ermittteln der über das Prädikat von allen Subjekten aus erreichbaren Objekte
		final Iterator<QN> i = si.iterator();
		final HashSet<QN> r = new HashSet<>();
		if (!i.hasNext()) return r;
		final QESet es = this.target.edges().havingContext(mc).havingPredicate(pn);
		Iterables.addAll(r, es.havingSubject(i.next()).objects());
		while (!r.isEmpty() && i.hasNext()) {
			r.retainAll(es.withSubject(i.next()).objects().toSet());
		}
		return r;
	}

	Set<QN> getObjectsFromSubjectsMax(final QN mergedContext, final QN predicate, final QNSet subjects) {
		/// lesen einer Menge von Objekten/
		return this.target.edges().havingContext(mergedContext).havingPredicate(predicate).havingSubjects(subjects).objects().toSet();
	}

	Map<List<QN>, List<QN>> getObjectsFromSubjectsMap(final QN mergedContext, final QN predicate, final QNSet subjectSet) {
		// - Ermitteln der über ein Prädikat von allen Subjekten aus erreichbaren Objektmengen und ableitung des Spektrums
		// (Abbildung von Objektlisten auf die Sibjekte, die auf diese verweisen)
		final H2QNUniqueList result = new H2QNUniqueList();
		final QESet edges = this.target.edges().havingContext(mergedContext).havingPredicate(predicate);
		for (final QN subject: subjectSet) {
			result.install(edges.havingSubject(subject).objects().toList()).add(subject);
		}
		return result;
	}

	void setObjectsFromSubjects(final QN predicate, final QNSet subjectSet, final QNSet objectSet) {
		this.setObjectsFromSubjectsImpl(this.mergedContext, this.insertContext, this.deleteContext, predicate, subjectSet, objectSet);
	}

	void setObjectsFromSubjectsImpl(final QN mergedContext, final QN insertContext_or_null, final QN deleteContext_or_null, final QN predicate,
		final QNSet subjects, final QNSet objects) {
		// setzen einer Menge von Objekten/
		final QESet oldEdges = this.target.edges().havingContext(mergedContext).havingPredicate(predicate).havingSubjects(subjects).copy();
		final QESet newEdges = this.target.newEdges().withContext(mergedContext).withPredicate(predicate).withSubjects(subjects).withObjects(objects).copy();
		final QESet popEdges = oldEdges.except(newEdges).copy();
		final QESet putEdges = newEdges.except(oldEdges).copy();
		popEdges.popAll();
		putEdges.putAll();
		if ((insertContext_or_null == null) || (deleteContext_or_null == null)) return;
		putEdges.withContext(deleteContext_or_null).popAll();
		popEdges.withContext(insertContext_or_null).popAll();
		putEdges.withContext(insertContext_or_null).putAll();
		popEdges.withContext(deleteContext_or_null).putAll();
	}

	void put(final QN mc, final QN ic, final QN dc, final QN pn, final QNSet sn_set, final QNSet on_set) {
		/// einfügen in eine Menge von Objekten/

		final QESet me_set0 = this.target.edges().havingContext(mc).havingPredicate(pn).havingSubjects(sn_set).havingObjects(on_set);
		final QESet me_set1 = this.target.newEdges().withContext(mc).withPredicate(pn).withSubjects(sn_set).withObjects(on_set);
		final QESet me_set_put = me_set1.except(me_set0).copy();

		me_set_put.putAll();

		// if ic & dc
		me_set_put.havingContext(ic).putAll();
		me_set_put.havingContext(dc).popAll();

	}

	public void popSubjectsFromObjects(final QN predicate, final QNSet subjects, final QNSet objects) {
		this.popSubjectsFromObjects(this.mergedContext, this.insertContext, this.deleteContext, predicate, subjects, objects);
	}

	void popSubjectsFromObjects(final QN mergedContext, final QN insertContext_or_null, final QN deleteContext_or_null, final QN predicate, final QNSet subjects,
		final QNSet objects) {
		this.popObjectsFromSubjects(mergedContext, insertContext_or_null, deleteContext_or_null, predicate, subjects, objects);
	}

	void popObjectsFromSubjects(final QN mergedContext, final QN insertContext_or_null, final QN deleteContext_or_null, final QN predicate, final QNSet subjects,
		final QNSet objects) {
		/// entfernen aus einer Menge von Objekten/
		final QESet popEdges = this.target.edges().havingContext(mergedContext).havingPredicate(predicate).havingSubjects(subjects).havingObjects(objects).copy();
		popEdges.popAll();
		if ((insertContext_or_null == null) || (deleteContext_or_null == null)) return;
		popEdges.withContext(insertContext_or_null).popAll();
		popEdges.withContext(deleteContext_or_null).putAll();
	}

}

class H2QNUniqueList extends HashMap2<List<QN>, List<QN>> {

	@Override
	protected List<QN> customInstallValue(final List<QN> key) {
		return new ArrayList<>();
	}

}
