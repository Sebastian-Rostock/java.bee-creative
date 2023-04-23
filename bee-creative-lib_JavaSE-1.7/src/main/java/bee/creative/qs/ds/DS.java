package bee.creative.qs.ds;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import bee.creative.qs.QE;
import bee.creative.qs.QESet;
import bee.creative.qs.QN;
import bee.creative.qs.QNSet;
import bee.creative.qs.QS;
import bee.creative.util.Filters;
import bee.creative.util.Getter;
import bee.creative.util.HashMap2;
import bee.creative.util.HashSet2;
import bee.creative.util.Iterable2;
import bee.creative.util.Iterables;

public class DS {

	public static final Object CLONE_WITH_EDGE = "CLONE_EDGE";

	public static final Object CLONE_WITH_NODE = "CLONE_NODE";

	public QESet getCloneEdges(final Map<QN, QN> clones, final QN context, final Iterable<? extends QN> nodes) {

		final Map<QN, Object> cloneWithObject = new HashMap2<>(100);
		final Map<QN, Object> cloneWithSubject = new HashMap2<>(100);

		final ArrayList<QN> nodesToClone = new ArrayList<>(100);

		final HashSet2<QN> nodesToCheckForClone = new HashSet2<>(100);
		final HashSet2<QN> nodesToCheckForValue = new HashSet2<>();

		final QS qs = context.owner();
		final QESet edges = qs.edges().havingContext(context);

		Iterables.addAll(nodesToCheckForClone, nodes);

		while (!nodesToCheckForClone.isEmpty()) {

			nodesToCheckForValue.allocate(Math.max(nodesToCheckForValue.size(), nodesToCheckForClone.size()));
			for (final QN node: nodesToCheckForClone) {
				if (clones.get(node) == null) {
					nodesToCheckForValue.add(node);
				}
			}
			nodesToCheckForClone.clear();

			if (nodesToCheckForValue.isEmpty()) {
				break;
			}

			for (final QN node: qs.newNodes(nodesToCheckForValue).havingValue()) {
				clones.put(node, node);
				nodesToCheckForValue.remove(node);
			}

			if (nodesToCheckForValue.isEmpty()) {
				break;
			}

			nodesToClone.ensureCapacity(nodesToClone.size() + nodesToCheckForValue.size());
			for (final QN node: nodesToCheckForValue) {
				clones.put(node, qs.newNode());
				nodesToClone.add(node);
			}

			final QNSet nodesToCloneWithoutValue = qs.newNodes(nodesToCheckForValue);
			nodesToCheckForValue.clear();

			final QESet edgesHavingObjectsToClone = edges.havingObjects(nodesToCloneWithoutValue);
			final List<QN> predicatesFromEdgesHavingObjectsToClone = edgesHavingObjectsToClone.predicates().toList();
			final List<QN> predicatesFromEdgesHavingObjectsToCloneForUpdate = new ArrayList<>(predicatesFromEdgesHavingObjectsToClone.size());
			for (final QN node: predicatesFromEdgesHavingObjectsToClone) {
				if (!cloneWithObject.containsKey(node)) {
					cloneWithObject.put(node, null);
					predicatesFromEdgesHavingObjectsToCloneForUpdate.add(node);
				}
			}
			if (!predicatesFromEdgesHavingObjectsToCloneForUpdate.isEmpty()) {
				this.getCloneWithObject(cloneWithObject, predicatesFromEdgesHavingObjectsToCloneForUpdate);
			}
			final List<QN> predicatesFromEdgesHavingObjectsToCloneAndSubjectsToClone = new ArrayList<>(predicatesFromEdgesHavingObjectsToClone.size());
			for (final QN node: predicatesFromEdgesHavingObjectsToClone) {
				if (cloneWithObject.get(node) == DS.CLONE_WITH_NODE) {
					predicatesFromEdgesHavingObjectsToCloneAndSubjectsToClone.add(node);
				}
			}

			final QNSet subjectsFromEdgesHavingObjectsToCloneWithSubjectsToClone =
				edgesHavingObjectsToClone.havingPredicates(qs.newNodes(predicatesFromEdgesHavingObjectsToCloneAndSubjectsToClone)).subjects();

			Iterables.addAll(nodesToCheckForClone, subjectsFromEdgesHavingObjectsToCloneWithSubjectsToClone);

			final QESet edgesHavingSubjectsToClone = edges.havingSubjects(nodesToCloneWithoutValue);
			final List<QN> predicatesFromEdgesHavingSubjectsToClone = edgesHavingSubjectsToClone.predicates().toList();
			final List<QN> predicatesFromEdgesHavingSubjectsToCloneForUpdate = new ArrayList<>(predicatesFromEdgesHavingSubjectsToClone.size());
			for (final QN node: predicatesFromEdgesHavingSubjectsToClone) {
				if (!cloneWithSubject.containsKey(node)) {
					cloneWithSubject.put(node, null);
					predicatesFromEdgesHavingSubjectsToCloneForUpdate.add(node);
				}
			}
			if (!predicatesFromEdgesHavingSubjectsToCloneForUpdate.isEmpty()) {
				this.getCloneWithSubject(cloneWithSubject, predicatesFromEdgesHavingSubjectsToCloneForUpdate);
			}
			final List<QN> predicatesFromEdgesHavingSubjectsToCloneWithObjectsToClone = new ArrayList<>(predicatesFromEdgesHavingSubjectsToClone.size());
			for (final QN node: predicatesFromEdgesHavingSubjectsToClone) {
				if (cloneWithSubject.get(node) == DS.CLONE_WITH_NODE) {
					predicatesFromEdgesHavingSubjectsToCloneWithObjectsToClone.add(node);
				}
			}

			final QNSet objectsToClone =
				edgesHavingSubjectsToClone.havingPredicates(qs.newNodes(predicatesFromEdgesHavingSubjectsToCloneWithObjectsToClone)).objects();

			Iterables.addAll(nodesToCheckForClone, objectsToClone);

		}

		nodesToCheckForClone.compact();
		nodesToCheckForValue.compact();

		final QNSet nodesToCloneWithoutValue = qs.newNodes(nodesToClone);
		nodesToClone.clear();
		nodesToClone.trimToSize();

		final QESet edgesHavingObjectsToClone = edges.havingObjects(nodesToCloneWithoutValue);

		final Iterable2<QE> edgesHavingObjectsToCloneIterable = Iterables.translate(edgesHavingObjectsToClone, new Getter<QE, QE>() {

			@Override
			public QE get(final QE edge) {
				final Object cloneWith = cloneWithObject.get(edge.predicate());
				if ((cloneWith != DS.CLONE_WITH_EDGE) && (cloneWith != DS.CLONE_WITH_NODE)) return null;
				final QN sourceObject = edge.object(), targetObject = clones.get(sourceObject);
				if (sourceObject == targetObject) return null;
				final QN sourceSubject = edge.subject(), targetSubject = clones.get(sourceSubject);
				if (sourceSubject == targetSubject) return null;
				if (targetSubject == null) return edge.withObject(targetObject);
				return edge.withObject(targetObject).withSubject(targetSubject);
			}

		});

		final QESet edgesHavingSubjectsToClone = edges.havingSubjects(nodesToCloneWithoutValue);

		final Iterable2<QE> edgesHavingSubjectsToCloneIterable = Iterables.translate(edgesHavingSubjectsToClone, new Getter<QE, QE>() {

			@Override
			public QE get(final QE edge) {
				final Object cloneWith = cloneWithObject.get(edge.predicate());
				if ((cloneWith != DS.CLONE_WITH_EDGE) && (cloneWith != DS.CLONE_WITH_NODE)) return null;
				final QN sourceObject = edge.object(), targetObject = clones.get(sourceObject);
				if (sourceObject == targetObject) return null;
				final QN sourceSubject = edge.subject(), targetSubject = clones.get(sourceSubject);
				if (sourceSubject == targetSubject) return null;
				if (targetObject == null) return edge.withSubject(targetSubject);
				return edge.withObject(targetObject).withSubject(targetSubject);
			}

		});

		return qs.newEdges(edgesHavingObjectsToCloneIterable.concat(edgesHavingSubjectsToCloneIterable).filter(Filters.empty()));
	}

	public void getCloneWithObject(final Map<QN, Object> result, final Iterable<QN> predicates) {
	}

	/** DONE Diese Methode ermittelt zu jedem der gegebenen {@link QE#predicate() Prädikatknoten}, ob beim {@link #getCloneEdges(Map, QN, Iterable) Klonen} eines
	 * {@link QE#subject() Subjektknoten} auch die {QE Hyperkanten} mit diesem {@link QE#subject() Subjektknoten} und dem {@link QE#predicate() Prädikatknoten}
	 * geklont werden sollen und ob zudem auch noch deren {@link QE#object() Objektknoten} geklont werden sollen.
	 *
	 * @param result Abbildung von einem {@link QE#predicate() Prädikatknoten} auf {@link #CLONE_WITH_EDGE}, {@link #CLONE_WITH_NODE} oder {@code null}.
	 * @param predicates {@link QE#predicate() Prädikatknoten}. */
	public void getCloneWithSubject(final Map<QN, Object> result, final Iterable<QN> predicates) {
	}

	/** DONE
	 *
	 * @param context
	 * @param putContextOrNull
	 * @param popContextOrNull
	 * @param edges */
	public void putEdges(final QN context, final QN putContextOrNull, final QN popContextOrNull, final Iterable<? extends QE> edges) {
		final QS owner = context.owner();
		final QESet newEdges = owner.newEdges(edges).withContext(context);
		if (this.useAllContexts(context, putContextOrNull, popContextOrNull)) {
			final QESet putEdges = newEdges.except(owner.edges()).copy();
			putEdges.putAll();
			putEdges.withContext(putContextOrNull).putAll();
			putEdges.withContext(popContextOrNull).popAll();
		} else {
			newEdges.putAll();
		}
	}

	/** DONE
	 *
	 * @param context
	 * @param putContextOrNull
	 * @param popContextOrNull
	 * @param edges */
	public void popEdges(final QN context, final QN putContextOrNull, final QN popContextOrNull, final Iterable<? extends QE> edges) {
		final QS owner = context.owner();
		final QESet newEdges = owner.newEdges(edges).withContext(context);
		if (this.useAllContexts(context, putContextOrNull, popContextOrNull)) {
			final QESet popEdges2 = newEdges.intersect(owner.edges()).copy();
			popEdges2.popAll();
			popEdges2.withContext(putContextOrNull).popAll();
			popEdges2.withContext(popContextOrNull).putAll();
		} else {
			newEdges.popAll();
		}
	}

	/** DONE Diese Methode gibt das zurück.
	 *
	 * @param context
	 * @param predicate
	 * @param subjects
	 * @return */
	public Map<QN, QN> getObject(final QN context, final QN predicate, final Iterable<? extends QN> subjects) {
		final Map<QN, QN> object = new HashMap2<>(100);
		for (final QN subject: subjects) {
			object.put(subject, null);
		}
		for (final QE edge: this.oldObjectEdgeSet(context, predicate, object)) {
			object.put(edge.subject(), edge.object());
		}
		return object;
	}

	/** DONE Diese Methode gibt das zurück.
	 *
	 * @param context
	 * @param predicate
	 * @param subjects
	 * @return */
	public Map<QN, List<QN>> getObjects(final QN context, final QN predicate, final Iterable<? extends QN> subjects) {
		final Map<QN, List<QN>> objects = new HashMap2<>(100);
		for (final QN subject: subjects) {
			objects.put(subject, new ArrayList<QN>());
		}
		for (final QE edge: this.oldObjectEdgeSet(context, predicate, objects)) {
			objects.get(edge.subject()).add(edge.object());
		}
		return objects;
	}

	/** DONE Diese Methode gibt das zurück.
	 *
	 * @param context
	 * @param putContextOrNull
	 * @param popContextOrNull
	 * @param predicate
	 * @param objects */
	public void putObjects(final QN context, final QN putContextOrNull, final QN popContextOrNull, final QN predicate,
		final Map<? extends QN, ? extends Iterable<? extends QN>> objects) {
		final QESet newEdges = this.newObjectsEdgeSet(context, predicate, objects);
		if (this.useAllContexts(context, putContextOrNull, popContextOrNull)) {
			final QESet oldEdges = context.owner().edges();
			final QESet putEdges = newEdges.except(oldEdges).copy();
			putEdges.putAll();
			putEdges.withContext(putContextOrNull).putAll();
			putEdges.withContext(popContextOrNull).popAll();
		} else {
			newEdges.putAll();
		}
	}

	/** DONE Diese Methode gibt das zurück.
	 * 
	 * @param context
	 * @param putContextOrNull
	 * @param popContextOrNull
	 * @param predicate
	 * @param objects */
	public void popObjects(final QN context, final QN putContextOrNull, final QN popContextOrNull, final QN predicate,
		final Map<? extends QN, ? extends Iterable<? extends QN>> objects) {
		final QESet newEdges = this.newObjectsEdgeSet(context, predicate, objects);
		if (this.useAllContexts(context, putContextOrNull, popContextOrNull)) {
			final QESet oldEdges = context.owner().edges();
			final QESet popEdges = oldEdges.intersect(newEdges).copy();
			popEdges.popAll();
			popEdges.withContext(putContextOrNull).popAll();
			popEdges.withContext(popContextOrNull).putAll();
		} else {
			newEdges.popAll();
		}
	}

	/** Diese Methode gibt das zurück.
	 *
	 * @param context
	 * @param predicate
	 * @param object Abbildung von Subjekten auf Objekte.
	 * @param putContextOrNull
	 * @param popContextOrNull */
	public void setObject(final QN context, final QN predicate, final Map<? extends QN, ? extends QN> object, final QN putContextOrNull,
		final QN popContextOrNull) {
		final QESet oldEdges = this.oldObjectEdgeSet(context, predicate, object).copy();
		final QESet newEdges = this.newObjectEdgeSet(context, predicate, object).copy();
		final QESet putEdges = newEdges.except(oldEdges);
		final QESet popEdges = oldEdges.except(newEdges);
		if (this.useAllContexts(context, putContextOrNull, popContextOrNull)) {
			final QESet putEdges2 = putEdges.copy();
			final QESet popEdges2 = popEdges.copy();
			putEdges2.putAll();
			putEdges2.withContext(putContextOrNull).putAll();
			putEdges2.withContext(popContextOrNull).popAll();
			popEdges2.popAll();
			popEdges2.withContext(popContextOrNull).putAll();
			popEdges2.withContext(putContextOrNull).popAll();
		} else {
			putEdges.putAll();
			popEdges.popAll();
		}
	}

	public void setObjects(final QN context, final QN predicate, final Map<? extends QN, ? extends Iterable<? extends QN>> object, final QN putContextOrNull,
		final QN popContextOrNull) {
		final QESet oldEdges = this.oldObjectEdgeSet(context, predicate, object).copy();
		final QESet newEdges = this.newObjectsEdgeSet(context, predicate, object).copy();
		final QESet putEdges = newEdges.except(oldEdges);
		final QESet popEdges = oldEdges.except(newEdges);
		if (this.useAllContexts(context, putContextOrNull, popContextOrNull)) {
			final QESet putEdges2 = putEdges.copy();
			final QESet popEdges2 = popEdges.copy();
			putEdges2.putAll();
			putEdges2.withContext(putContextOrNull).putAll();
			putEdges2.withContext(popContextOrNull).popAll();
			popEdges2.popAll();
			popEdges2.withContext(popContextOrNull).putAll();
			popEdges2.withContext(putContextOrNull).popAll();
		} else {
			putEdges.putAll();
			popEdges.popAll();
		}
	}

	/** DONE Diese Methode gibt das zurück.
	 *
	 * @param context
	 * @param putContextOrNull
	 * @param popContextOrNull
	 * @return
	 * @throws NullPointerException
	 * @throws IllegalArgumentException */
	private boolean useAllContexts(final QN context, final QN putContextOrNull, final QN popContextOrNull) throws NullPointerException, IllegalArgumentException {
		if ((context == null) || ((putContextOrNull == null) != (popContextOrNull == null))) throw new NullPointerException();
		if ((context == putContextOrNull) || (context == popContextOrNull)) throw new IllegalArgumentException();
		if (putContextOrNull == null) return false;
		if (putContextOrNull != popContextOrNull) return true;
		throw new IllegalArgumentException();
	}

	private QESet oldObjectEdgeSet(final QN context, final QN predicate, final Map<? extends QN, ?> object) {
		final QS qs = context.owner();
		return qs.edges().havingContext(context).havingPredicate(predicate).havingSubjects(qs.newNodes(object.keySet()));
	}

	private QESet newObjectEdgeSet(final QN context, final QN predicate, final Map<? extends QN, ? extends QN> object) {
		final QS qs = context.owner();
		final ArrayList<QE> edges = new ArrayList<>(object.size());
		for (final Entry<? extends QN, ? extends QN> entry: object.entrySet()) {
			final QN subject = entry.getValue();
			if (subject != null) {
				edges.add(qs.newEdge(context, predicate, entry.getKey(), subject));
			}
		}
		return qs.newEdges(edges);
	}

	private QESet newObjectsEdgeSet(final QN context, final QN predicate, final Map<? extends QN, ? extends Iterable<? extends QN>> objects) {
		final QS owner = context.owner();
		final QE predicateEdge = owner.newEdge(context, predicate, predicate, predicate);
		return owner.newEdges(Iterables.concatAll(Iterables.translate( //
			objects.entrySet(), //
			new Getter<Entry<? extends QN, ? extends Iterable<? extends QN>>, Iterable<QE>>() {

				@Override
				public Iterable<QE> get(final Entry<? extends QN, ? extends Iterable<? extends QN>> entry) {
					final QE subjectEdge = predicateEdge.withSubject(entry.getKey());
					return Iterables.translate(entry.getValue(), new Getter<QN, QE>() {

						@Override
						public QE get(final QN object) {
							return subjectEdge.withObject(object);
						}

					});
				}

			})));
	}

}
