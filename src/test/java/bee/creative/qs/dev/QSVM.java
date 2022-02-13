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

@SuppressWarnings ("javadoc")
public class QSVM {

	final QS qs;

	private QN activeVersion;

	/** Dieses Feld speichert den {@link QE#context() Kontextknoten} der {@link QE Hyperkanten}, die gegenüber der vorherigen in der {@link #activeVersion
	 * aktuellen Version} eingefügt wurden. */
	QN insertContext;

	/** Dieses Feld speichert den {@link QE#context() Kontextknoten} der {@link QE Hyperkanten}, die gegenüber der vorherigen in der {@link #activeVersion
	 * aktuellen Version} entfernt wurden. */
	QN deleteContext;

	/** Dieses Feld speichert den {@link QE#context() Kontextknoten} der {@link QE Hyperkanten} in der {@link #activeVersion aktuellen Version}. Diese beinhaltet
	 * die Hyperkanten der vorherigen Version mit den darin {@link #insertContext eingefügten} und ohne den daraus {@link #deleteContext entfernten}. */
	QN mergedContext;

	private final QN activePredicate;

	private final QN insertPredicate;

	private final QN deletePredicate;

	private final QN sourcePredicate;

	private final QN mergedPredicate;

	private final QN qsvmContext;

	public QSVM(final QS qs) throws SQLException {
		this.qs = qs;
		this.qsvmContext = qs.newNode("QSVM:CORE");
		this.activePredicate = qs.newNode("QSVM:ACTIVE");
		this.insertPredicate = qs.newNode("QSVM:INSERT");
		this.deletePredicate = qs.newNode("QSVM:DELETE");
		this.sourcePredicate = qs.newNode("QSVM:SOURCE");
		this.mergedPredicate = qs.newNode("QSVM:MERGED");

		final List<QN> activeList =
			qs.edges().havingContext(this.qsvmContext).havingPredicate(this.activePredicate).havingSubject(this.qsvmContext).objects().toList();
		if (activeList.isEmpty()) {

		} else if (activeList.size() == 1) {

		} else throw new SQLException();

	}

	public void putEdges(final QE... edges) {
		this.putEdges(this.qs.newEdges(edges));
	}

	public void putEdges(final Iterable<? extends QE> edges) {
		this.putEdges(this.mergedContext, this.insertContext, this.deleteContext, this.qs.newEdges(edges));
	}

	void putEdges(final QN mergedContext, final QN insertContext_or_null, final QN deleteContext_or_null, final QESet edges) {
		final QESet oldEdges = this.qs.edges().havingContext(mergedContext);
		final QESet newEdges = edges.withContext(mergedContext);
		final QESet putEdges = newEdges.except(oldEdges).copy();
		putEdges.putAll();
		if ((insertContext_or_null == null) || (deleteContext_or_null == null)) return;
		putEdges.withContext(deleteContext_or_null).popAll();
		putEdges.withContext(insertContext_or_null).putAll();
	}

	void popEdges(final QN mergedContext, final QN insertContext_or_null, final QN deleteContext_or_null, final QESet edges) {
		final QESet oldEdges = this.qs.edges().havingContext(mergedContext);
		final QESet newEdges = edges.withContext(mergedContext);
		final QESet popEdges = oldEdges.except(newEdges).copy();
		popEdges.popAll();
		if ((insertContext_or_null == null) || (deleteContext_or_null == null)) return;
		popEdges.withContext(insertContext_or_null).popAll();
		popEdges.withContext(deleteContext_or_null).putAll();
	}

	public void updateProperties() {
		// bestimmt die property knoten neu

	}

	public void revert() {
		// insert/delete rückgängig machen und verwerfen
		final QESet popEdges1 = this.qs.edges().havingContext(this.deleteContext);
		final QESet popEdges2 = this.qs.edges().havingContext(this.insertContext);
		final QESet popEdges3 = popEdges2.withContext(this.mergedContext);
		final QESet putEdges1 = popEdges1.withContext(this.mergedContext);
		putEdges1.putAll();
		popEdges3.union(popEdges2).union(putEdges1).popAll();
	}

	public void commit() {
		this.commitImpl_MOVE();
	}

	public void commit(final boolean moveMergedKnowledge) throws IllegalStateException {
		if (moveMergedKnowledge) {
			this.commitImpl_MOVE();
		} else {
			this.commitImpl_COPY();
		}
	}

	final void commitImpl_COPY() {
		// schließt akteulle version ab, erzeugt neue, wählt diese als aktuell und kopiert die das merged wissen
		final QN sourceVersion = this.activeVersion, activeVersion = this.qs.newNode();
		final QN insertKnowledge = this.qs.newNode(), deleteKnowledge = this.qs.newNode(), mergedKnowledge = this.qs.newNode();
		final QESet putEdges1 = this.qs.newEdges( //
			this.newEdgeImpl(this.activePredicate, this.qsvmContext, activeVersion), //
			this.newEdgeImpl(this.sourcePredicate, activeVersion, sourceVersion), //
			this.newEdgeImpl(this.insertPredicate, activeVersion, insertKnowledge), //
			this.newEdgeImpl(this.deletePredicate, activeVersion, deleteKnowledge), //
			this.newEdgeImpl(this.mergedPredicate, activeVersion, mergedKnowledge));
		final QESet putEdges2 = this.qs.edges().havingContext(this.mergedContext).withContext(mergedKnowledge);
		final QE popEdge = this.newEdgeImpl(this.activePredicate, this.qsvmContext, sourceVersion);
		putEdges1.putAll();
		putEdges2.putAll();
		popEdge.pop();
		this.activeVersion = activeVersion;
		this.insertContext = insertKnowledge;
		this.deleteContext = deleteKnowledge;
		this.mergedContext = mergedKnowledge;
	}

	final void commitImpl_MOVE() {
		// schließt akteulle version ab, erzeugt neue, wählt diese als aktuell und verschiebt die das merged wissen
		final QN sourceVersion = this.activeVersion, activeVersion = this.qs.newNode();
		final QN insertKnowledge = this.qs.newNode(), deleteKnowledge = this.qs.newNode(), mergedKnowledge = this.mergedContext;
		final QESet putEdges = this.qs.newEdges( //
			this.newEdgeImpl(this.activePredicate, this.qsvmContext, activeVersion), //
			this.newEdgeImpl(this.sourcePredicate, activeVersion, sourceVersion), //
			this.newEdgeImpl(this.insertPredicate, activeVersion, insertKnowledge), //
			this.newEdgeImpl(this.deletePredicate, activeVersion, deleteKnowledge), //
			this.newEdgeImpl(this.mergedPredicate, activeVersion, mergedKnowledge));
		final QESet popEdges = this.qs.newEdges( //
			this.newEdgeImpl(this.activePredicate, this.qsvmContext, sourceVersion), //
			this.newEdgeImpl(this.mergedPredicate, sourceVersion, mergedKnowledge));
		putEdges.putAll();
		popEdges.popAll();
		this.activeVersion = activeVersion;
		this.insertContext = insertKnowledge;
		this.deleteContext = deleteKnowledge;
	}

	final QE newEdgeImpl(final QN predicate, final QN subject, final QN object) {
		return this.qs.newEdge(this.qsvmContext, predicate, subject, object);
	}

	/** Diese Methode gibt den {@link QE#subject() Subjektknoten} der aktuellen Version, welcher auf die {@link QE#context() Kontextknoten} des
	 * {@link #mergedContext effektiven}, {@link #insertContext eingefügten} und {@link #deleteContext entfernten} Wissens verweist. */
	public QN getActiveVersion() {
		return this.activeVersion;
	}

	public void setActiveVersion(final QN activeVersion) {
		// TODO knowledge nodes
		this.activeVersion = activeVersion;
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
		return this.getPredicatesFromObjectsImpl(this.mergedContext, this.qs.newNodes(objects));
	}

	Set<QN> getPredicatesFromObjectsImpl(final QN mergedContext, final QNSet objects) {
		return this.qs.edges().havingContext(mergedContext).havingObjects(objects).predicates().toSet();
	}

	public Set<QN> getPredicatesFromSubjects(final QNSet subjects) {
		return this.getPredicatesFromSubjects(this.mergedContext, subjects);
	}

	Set<QN> getPredicatesFromSubjects(final QN mergedContext, final QNSet subjects) {
		// (verwendete Objekte bzw. verwendende Subjekte; inverse Prädikate)
		return this.qs.edges().havingContext(mergedContext).havingSubjects(subjects).predicates().toSet();
	}

	Set<QN> getObjectsFromSubjectsMin(final QN mc, final QN pn, final QNSet si) {
		// - Ermittteln der über das Prädikat von allen Subjekten aus erreichbaren Objekte
		final Iterator<QN> i = si.iterator();
		final HashSet<QN> r = new HashSet<>();
		if (!i.hasNext()) return r;
		final QESet es = this.qs.edges().havingContext(mc).havingPredicate(pn);
		Iterables.addAll(r, es.havingSubject(i.next()).objects());
		while (!r.isEmpty() && i.hasNext()) {
			r.retainAll(es.withSubject(i.next()).objects().toSet());
		}
		return r;
	}

	Set<QN> getObjectsFromSubjectsMax(final QN mergedContext, final QN predicate, final QNSet subjects) {
		/// lesen einer Menge von Objekten/
		return this.qs.edges().havingContext(mergedContext).havingPredicate(predicate).havingSubjects(subjects).objects().toSet();
	}

	Map<List<QN>, List<QN>> getObjectsFromSubjectsMap(final QN mergedContext, final QN predicate, final QNSet subjectSet) {
		// - Ermitteln der über ein Prädikat von allen Subjekten aus erreichbaren Objektmengen und ableitung des Spektrums
		// (Abbildung von Objektlisten auf die Sibjekte, die auf diese verweisen)
		final H2QNUniqueList result = new H2QNUniqueList();
		final QESet edges = this.qs.edges().havingContext(mergedContext).havingPredicate(predicate);
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
		final QESet oldEdges = this.qs.edges().havingContext(mergedContext).havingPredicate(predicate).havingSubjects(subjects).copy();
		final QESet newEdges = this.qs.newEdges().withContext(mergedContext).withPredicate(predicate).withSubjects(subjects).withObjects(objects).copy();
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

		final QESet me_set0 = this.qs.edges().havingContext(mc).havingPredicate(pn).havingSubjects(sn_set).havingObjects(on_set);
		final QESet me_set1 = this.qs.newEdges().withContext(mc).withPredicate(pn).withSubjects(sn_set).withObjects(on_set);
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
		final QESet popEdges = this.qs.edges().havingContext(mergedContext).havingPredicate(predicate).havingSubjects(subjects).havingObjects(objects).copy();
		popEdges.popAll();
		if ((insertContext_or_null == null) || (deleteContext_or_null == null)) return;
		popEdges.withContext(insertContext_or_null).popAll();
		popEdges.withContext(deleteContext_or_null).putAll();
	}

}

class H2QNUniqueList extends HashMap2<List<QN>, List<QN>> {

	@Override
	protected List<QN> customInstallValue(List<QN> key) {
		return new ArrayList<>();
	}

}
