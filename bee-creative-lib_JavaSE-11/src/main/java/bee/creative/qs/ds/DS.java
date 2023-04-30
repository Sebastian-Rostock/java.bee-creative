package bee.creative.qs.ds;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import bee.creative.qs.QE;
import bee.creative.qs.QESet;
import bee.creative.qs.QN;
import bee.creative.qs.QS;
import bee.creative.util.Consumer;
import bee.creative.util.Filters;
import bee.creative.util.Getter;
import bee.creative.util.HashMap2;
import bee.creative.util.HashSet2;
import bee.creative.util.Iterables;

public class DS {

	/** Diese Methode fügt an die gegebene Liste die im gegebenen {@link QS Graphspeicher} hinterlegten {@link QN Hyperknoten} mit den gegebenen {@link QN#value()
	 * Textwerten} an.
	 *
	 * @param results Liste der ermittelten Hyperknoten.
	 * @param sources Liste der gegebenen Textwerte.
	 * @param owner Besitzer der Hyperknoten.
	 * @param dirty {@code true}, wenn für Textwerte ohne Hyperknoten {@code null} angefügt werden soll.<br>
	 *        {@code false}, wenn Textwerte ohne Hyperknoten ignoriert werden sollen. */
	public void getNodes(final List<QN> results, final List<String> sources, final QS owner, final boolean dirty) {
		final var sourceToResult = new HashMap2<String, QN>(sources.size());
		owner.newValues(sources).nodes(sourceToResult);
		this.__getItems(results, sources, sourceToResult, dirty);
	}

	/** Diese Methode fügt in die gegebene Abbilding die im gegebenen {@link QS Graphspeicher} hinterlegten {@link QN Hyperknoten} mit den gegebenen
	 * {@link QN#value() Textwerten} ein.
	 *
	 * @param results Abbilding von Schlüsseln auf ermittelte Hyperknoten.
	 * @param sources Abbilding von Schlüsseln auf gegebene Textwerte.
	 * @param owner Besitzer der Hyperknoten.
	 * @param dirty {@code true}, wenn für Textwerte ohne Hyperknoten {@code null} eingefügt werden soll.<br>
	 *        {@code false}, wenn Textwerte ohne Hyperknoten ignoriert werden sollen. */
	public <K> void getNodes(final Map<K, QN> results, final Map<K, String> sources, final QS owner, final boolean dirty) {
		final var sourceToResult = new HashMap2<String, QN>(sources.size());
		owner.newValues(sources.values()).nodes(sourceToResult);
		this.__getItems(results, sources, sourceToResult, dirty);
	}

	/** Diese Methode fügt an die gegebene Liste die im gegebenen {@link QS Graphspeicher} hinterlegten {@link QN#value() Textwerte} der gegebenen {@link QN
	 * Hyperknoten} an.
	 *
	 * @param results Liste der ermittelten Textwerte.
	 * @param sources Liste der gegebenen Hyperknoten.
	 * @param owner Besitzer der Hyperknoten.
	 * @param dirty {@code true}, wenn für Hyperknoten ohne Textwerte {@code null} angefügt werden soll.<br>
	 *        {@code false}, wenn Hyperknoten ohne Textwerte ignoriert werden sollen. */
	public void getValues(final List<String> results, final List<QN> sources, final QS owner, final boolean dirty) {
		final var sourceToResult = new HashMap2<QN, String>(sources.size());
		owner.newNodes(sources).values(sourceToResult);
		this.__getItems(results, sources, sourceToResult, dirty);
	}

	/** Diese Methode fügt in die gegebene Abbilding die im gegebenen {@link QS Graphspeicher} hinterlegten {@link QN#value() Textwerte} der gegebenen {@link QN
	 * Hyperknoten} ein.
	 *
	 * @param results Abbilding von Schlüsseln auf ermittelte Textwerte.
	 * @param sources Abbilding von Schlüsseln auf gegebenen Hyperknoten.
	 * @param owner Besitzer der Hyperknoten.
	 * @param dirty {@code true}, wenn für Hyperknoten ohne Textwerte {@code null} eingefügt werden soll.<br>
	 *        {@code false}, wenn Hyperknoten ohne Textwerte ignoriert werden sollen. */
	public <K> void getValues(final Map<K, String> results, final Map<K, QN> sources, final QS owner, final boolean dirty) {
		final var sourceToResult = new HashMap2<QN, String>(sources.size());
		owner.newNodes(sources.values()).values(sourceToResult);
		this.__getItems(results, sources, sourceToResult, dirty);
	}

	/** Diese Methode ergänzt die gegebene Abbildung von Hyperknoten auf Kennungen.
	 * 
	 * @param idents
	 * @param context
	 * @param nodes */
	public void getIdents(final Map<QN, Object> idents, final QN context, final Iterable<? extends QN> nodes) {
		// TODO
	}
	
	
	protected Object newIdent(Object parentIdent, Object localIdent) {
		// TODO hash cache
		return null;
	}
	
	
	protected Object getIdent(final QN context, Getter<QN, Object> idents,  QN node) {
	// TODO local ident ableiten udn mit dem des parent verbinden	
		return null;
	}

	public QESet getCloneEdges(final Map<QN, QN> clones, final QN context, final Iterable<? extends QN> nodes) {

		final var CLONE_WITH_EDGE = new Object();
		final var CLONE_WITH_NODE = new Object();

		final var cloneWithObject = new HashMap2<QN, Object>(100);
		final var cloneEdgeWithObject = (Consumer<QN>)predicate -> cloneWithObject.put(predicate, CLONE_WITH_EDGE);
		final var cloneNodeWithObject = (Consumer<QN>)predicate -> cloneWithObject.put(predicate, CLONE_WITH_NODE);

		final var cloneWithSubject = new HashMap2<QN, Object>(100);
		final var cloneEdgeWithSubject = (Consumer<QN>)predicate -> cloneWithSubject.put(predicate, CLONE_WITH_EDGE);
		final var cloneNodeWithSubject = (Consumer<QN>)predicate -> cloneWithSubject.put(predicate, CLONE_WITH_NODE);

		final var nodesToClone = new ArrayList<QN>(100);

		final var nodesToCheckForClone = new HashSet2<QN>(100);
		final var nodesToCheckForValue = new HashSet2<QN>();

		final var qs = context.owner();
		final var edges = qs.edges().havingContext(context);

		Iterables.addAll(nodesToCheckForClone, nodes);

		while (!nodesToCheckForClone.isEmpty()) {

			nodesToCheckForValue.allocate(Math.max(nodesToCheckForValue.size(), nodesToCheckForClone.size()));
			for (final var node: nodesToCheckForClone) {
				if (clones.get(node) == null) {
					nodesToCheckForValue.add(node);
				}
			}
			nodesToCheckForClone.clear();

			if (nodesToCheckForValue.isEmpty()) {
				break;
			}

			for (final var node: qs.newNodes(nodesToCheckForValue).havingValue()) {
				clones.put(node, node);
				nodesToCheckForValue.remove(node);
			}

			if (nodesToCheckForValue.isEmpty()) {
				break;
			}

			nodesToClone.ensureCapacity(nodesToClone.size() + nodesToCheckForValue.size());
			for (final var node: nodesToCheckForValue) {
				clones.put(node, qs.newNode());
				nodesToClone.add(node);
			}

			final var nodesToCloneWithoutValue = qs.newNodes(nodesToCheckForValue);
			nodesToCheckForValue.clear();

			final var edgesHavingObjectsToClone = edges.havingObjects(nodesToCloneWithoutValue);
			final var predicatesFromEdgesHavingObjectsToClone = edgesHavingObjectsToClone.predicates().toList();
			final var predicatesFromEdgesHavingObjectsToCloneForUpdate = new ArrayList<QN>(predicatesFromEdgesHavingObjectsToClone.size());
			for (final var node: predicatesFromEdgesHavingObjectsToClone) {
				if (!cloneWithObject.containsKey(node)) {
					cloneWithObject.put(node, null);
					predicatesFromEdgesHavingObjectsToCloneForUpdate.add(node);
				}
			}
			if (!predicatesFromEdgesHavingObjectsToCloneForUpdate.isEmpty()) {
				this.getCloneWithObject(context, cloneEdgeWithObject, cloneNodeWithObject, predicatesFromEdgesHavingObjectsToCloneForUpdate);
			}
			final var predicatesFromEdgesHavingObjectsToCloneAndSubjectsToClone = new ArrayList<QN>(predicatesFromEdgesHavingObjectsToClone.size());
			for (final var node: predicatesFromEdgesHavingObjectsToClone) {
				if (cloneWithObject.get(node) == CLONE_WITH_NODE) {
					predicatesFromEdgesHavingObjectsToCloneAndSubjectsToClone.add(node);
				}
			}

			final var subjectsFromEdgesHavingObjectsToCloneWithSubjectsToClone =
				edgesHavingObjectsToClone.havingPredicates(qs.newNodes(predicatesFromEdgesHavingObjectsToCloneAndSubjectsToClone)).subjects();

			Iterables.addAll(nodesToCheckForClone, subjectsFromEdgesHavingObjectsToCloneWithSubjectsToClone);

			final var edgesHavingSubjectsToClone = edges.havingSubjects(nodesToCloneWithoutValue);
			final var predicatesFromEdgesHavingSubjectsToClone = edgesHavingSubjectsToClone.predicates().toList();
			final var predicatesFromEdgesHavingSubjectsToCloneForUpdate = new ArrayList<QN>(predicatesFromEdgesHavingSubjectsToClone.size());
			for (final var node: predicatesFromEdgesHavingSubjectsToClone) {
				if (!cloneWithSubject.containsKey(node)) {
					cloneWithSubject.put(node, null);
					predicatesFromEdgesHavingSubjectsToCloneForUpdate.add(node);
				}
			}
			if (!predicatesFromEdgesHavingSubjectsToCloneForUpdate.isEmpty()) {
				this.getCloneWithSubject(context, cloneEdgeWithSubject, cloneNodeWithSubject, predicatesFromEdgesHavingSubjectsToCloneForUpdate);
			}
			final var predicatesFromEdgesHavingSubjectsToCloneWithObjectsToClone = new ArrayList<QN>(predicatesFromEdgesHavingSubjectsToClone.size());
			for (final var node: predicatesFromEdgesHavingSubjectsToClone) {
				if (cloneWithSubject.get(node) == CLONE_WITH_NODE) {
					predicatesFromEdgesHavingSubjectsToCloneWithObjectsToClone.add(node);
				}
			}

			final var objectsToClone = edgesHavingSubjectsToClone.havingPredicates(qs.newNodes(predicatesFromEdgesHavingSubjectsToCloneWithObjectsToClone)).objects();

			Iterables.addAll(nodesToCheckForClone, objectsToClone);

		}

		nodesToCheckForClone.compact();
		nodesToCheckForValue.compact();

		final var nodesToCloneWithoutValue = qs.newNodes(nodesToClone);
		nodesToClone.clear();
		nodesToClone.trimToSize();

		final var edgesHavingObjectsToClone = edges.havingObjects(nodesToCloneWithoutValue);

		final var edgesHavingObjectsToCloneIterable = Iterables.translate(edgesHavingObjectsToClone, edge -> {
			final Object cloneWith = cloneWithObject.get(edge.predicate());
			if ((cloneWith != CLONE_WITH_EDGE) && (cloneWith != CLONE_WITH_NODE)) return null;
			final QN sourceObject = edge.object(), targetObject = clones.get(sourceObject);
			if (sourceObject == targetObject) return null;
			final QN sourceSubject = edge.subject(), targetSubject = clones.get(sourceSubject);
			if (sourceSubject == targetSubject) return null;
			if (targetSubject == null) return edge.withObject(targetObject);
			return edge.withObject(targetObject).withSubject(targetSubject);
		});

		final var edgesHavingSubjectsToClone = edges.havingSubjects(nodesToCloneWithoutValue);

		final var edgesHavingSubjectsToCloneIterable = Iterables.translate(edgesHavingSubjectsToClone, edge -> {
			final Object cloneWith = cloneWithObject.get(edge.predicate());
			if ((cloneWith != CLONE_WITH_EDGE) && (cloneWith != CLONE_WITH_NODE)) return null;
			final QN sourceObject = edge.object(), targetObject = clones.get(sourceObject);
			if (sourceObject == targetObject) return null;
			final QN sourceSubject = edge.subject(), targetSubject = clones.get(sourceSubject);
			if (sourceSubject == targetSubject) return null;
			if (targetObject == null) return edge.withSubject(targetSubject);
			return edge.withObject(targetObject).withSubject(targetSubject);
		});

		return qs.newEdges(edgesHavingObjectsToCloneIterable.concat(edgesHavingSubjectsToCloneIterable).filter(Filters.empty()));
	}

	protected void getCloneWithObject(final QN context, final Consumer<QN> cloneEdgeWithObject, final Consumer<QN> cloneNodeWithObject,
		final Iterable<QN> predicates) {
	}

	/** Diese Methode ermittelt zu jedem der gegebenen {@link QE#predicate() Prädikatknoten}, ob beim {@link #getCloneEdges(Map, QN, Iterable) Klonen} von
	 * {@link QE#subject() Subjektknoten} im Rahmen des gegebenen {@link QE#context() Kontextknoten} auch die {QE Hyperkanten} mit diesen Kontext, Prädikat- und
	 * Subjektknoten geklont werden sollen und ob zudem noch deren {@link QE#object() Objektknoten} geklont werden sollen.
	 *
	 * @param context Kontextknoten.
	 * @param cloneEdgeWithSubject Methode zur Markierung eines Prädikatknoten zum Klonen seiner Hyperkanten.
	 * @param cloneNodeWithSubject Methode zur Markierung eines Prädikatknoten zum Klonen seiner Hyperkanten und derer Objektknoen.
	 * @param predicates Prädikatknoten. */
	protected void getCloneWithSubject(final QN context, final Consumer<QN> cloneEdgeWithSubject, final Consumer<QN> cloneNodeWithSubject,
		final Iterable<QN> predicates) {
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
		final var qs = context.owner();
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
		final var owner = context.owner();
		final var predicateEdge = owner.newEdge(context, predicate, predicate, predicate);
		return owner.newEdges(Iterables.concatAll(Iterables.translate(objects.entrySet(), entry -> {
			final var subjectEdge = predicateEdge.withSubject(entry.getKey());
			return Iterables.translate(entry.getValue(), subjectEdge::withObject);
		})));
	}

	private <R, S> void __getItems(final List<R> results, final List<S> sources, final Map<S, R> sourceToResult, final boolean keepNull) {
		for (final var source: sources) {
			final var result = sourceToResult.get(source);
			if ((result != null) || keepNull) {
				results.add(result);
			}
		}
	}

	private <K, R, S> void __getItems(final Map<K, R> results, final Map<K, S> sources, final Map<S, R> sourceToResult, final boolean keepNull) {
		for (final var source: sources.entrySet()) {
			final var result = sourceToResult.get(source.getValue());
			if ((result != null) || keepNull) {
				results.put(source.getKey(), result);
			}
		}
	}

}
