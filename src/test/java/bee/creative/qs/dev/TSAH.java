package bee.creative.qs.dev;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import bee.creative.qs.QE;
import bee.creative.qs.QESet;
import bee.creative.qs.QN;
import bee.creative.qs.QNSet;
import bee.creative.qs.QS;
import bee.creative.util.Getter;
import bee.creative.util.HashMap;
import bee.creative.util.HashMap2;
import bee.creative.util.HashMapOI;
import bee.creative.util.HashSet;
import bee.creative.util.Hashers;

public class TSAH {

	static class InstallList<GItem> implements Getter<Object, List<GItem>> {

		@Override
		public List<GItem> get(final Object item) {
			return new ArrayList<>();
		}

	}

	private final QN node;

	private final QS target;

	private final TSVH history;

	public TSAH(final TSVH target) {
		this.history = target;
		this.node = target.context;
		this.target = target.owner.target;
	}

	public Set<QN> getObjectPredicateSet(final QN... objects) {
		return this.getObjectPredicateSet(this.newNodes(objects));
	}

	/** Diese Methode liefert die Menge der {@link QE#predicate() Prädikatknote} der {@link QE Hyperkanten} mit den gegebenen {@link QE#object() Objektknoten}. */
	public Set<QN> getObjectPredicateSet(final Iterable<? extends QN> objects) {
		return this.history.edges.havingObjects(this.newNodes(objects)).predicates().toSet();
	}

	public Set<QN> getPredicateSetFromSubjects(final QN... subjects) {
		return this.getPredicateSetFromSubjects(this.newNodes(subjects));
	}

	/** Diese Methode liefert die Menge der {@link QE#predicate() Prädikatknote} der {@link QE Hyperkanten} mit den gegebenen {@link QE#subject()
	 * Subjektknoten}. */
	public Set<QN> getPredicateSetFromSubjects(final Iterable<? extends QN> subjects) {
		return this.history.edges.havingSubjects(this.newNodes(subjects)).predicates().toSet();
	}

	// - Ermittteln der über das Prädikat von allen Subjekten aus erreichbaren Objekte
	public Set<QN> getObjectMinSet(final QN predicate, final QNSet subjects) {
		final QNSet subjectSet = subjects.copy();
		final HashSet<QN> objectSet = new HashSet<>();
		final HashMapOI<QN> countMap = new HashMapOI<>();
		final long count = subjectSet.size();
		for (final QE edge: this.history.edges.havingPredicate(predicate).havingSubjects(subjects)) {
			countMap.add(edge.subject(), 1);
		}
		for (final Entry<QN, Integer> entry: countMap.entrySet()) {
			if (entry.getValue().longValue() == count) {
				objectSet.add(entry.getKey());
			}
		}
		return objectSet;
	}

	/// lesen einer Menge von Objekten/
	public Set<QN> getObjectMaxSet(final QN predicate, final QNSet subjects) {
		return this.history.edges.havingPredicate(predicate).havingSubjects(subjects).objects().toSet();
	}

	public Map<QN, QN> getObjectItemMap(final QN predicate, final QNSet subjects) {
		final HashMap<QN, QN> objectMap = new HashMap<>(10);
		for (final QE edge: this.history.edges.havingPredicate(predicate).havingSubjects(subjects)) {
			objectMap.put(edge.subject(), edge.object());
		}
		return objectMap;
	}

	public Map<QN, List<QN>> getObjectListMap(final QN predicate, final Iterable<? extends QN> subjects) {
		final HashMap<QN, List<QN>> objectListMap = HashMap.from(Hashers.natural(), new InstallList<QN>());
		for (final QE edge: this.history.edges.havingPredicate(predicate).havingSubjects(this.newNodes(subjects))) {
			objectListMap.install(edge.subject()).add(edge.object());
		}
		return objectListMap;
	}

	public Map<List<QN>, List<QN>> getObjectsFromSubjectsMap(final QN predicate, final Iterable<? extends QN> subjects) {
		final HashMap<List<QN>, List<QN>> subjectListMap = HashMap.from(Hashers.natural(), new InstallList<QN>());
		// - Ermitteln der über ein Prädikat von allen Subjekten aus erreichbaren Objektmengen und ableitung des Spektrums
		// (Abbildung von Objektlisten auf die Sibjekte, die auf diese verweisen)
		for (final Entry<QN, List<QN>> entry: this.getObjectListMap(predicate, subjects).entrySet()) {
			subjectListMap.install(entry.getValue()).add(entry.getKey());
		}
		return subjectListMap;
	}

	void put(final QN pn, final QNSet sn_set, final QNSet on_set) {
		final QESet me_set1 = this.target.newEdges(this.history.context, pn, pn, pn).withSubjects(sn_set).withObjects(on_set);
		this.history.putEdges(me_set1);
	}

	public QE newEdge(final QN predicate, final QN subject, final QN object) {
		return this.target.newEdge(this.history.context, predicate, subject, object);
	}

	public QNSet newNodes(final QN... objects) {
		return this.target.newNodes(objects);
	}

	public QNSet newNodes(final Iterable<? extends QN> objects) {
		return this.target.newNodes(objects);
	}

	void setObjectsFromSubjects(final QN predicate, final QNSet subjects, final QNSet objects) {
		// setzen einer Menge von Objekten/
		final QESet oldEdges = this.history.edges.havingPredicate(predicate).havingSubjects(subjects);
		final QESet newEdges = this.target.newEdges(this.history.context, predicate, predicate, predicate).withSubjects(subjects).withObjects(objects);
		this.history.popEdges(newEdges.except(oldEdges));
		this.history.putEdges(oldEdges.except(newEdges));
	}

	void popObjectsFromSubjects(final QN predicate, final QNSet subjects, final QNSet objects) {
		/// entfernen aus einer Menge von Objekten/
		this.history.popEdges(this.history.edges.havingPredicate(predicate).havingSubjects(subjects).havingObjects(objects));
	}

}
