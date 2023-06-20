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
import bee.creative.util.HashMap2;
import bee.creative.util.HashSet2;
import bee.creative.util.Iterables;

public class DS {

	/** Diese Methode speichert die als {@link QE Hyperkanten} gegebenen Prädikat-Subjekt-Objekt-Tripel mit dem gegebenen {@link QE#context() Kontextknoten} in
	 * {@link QE#owner() dessen} Graphspeicher. Mithilfe der beiden anderen Kontextknoten kann der Unterschied gegenüber eines vorherigen Datenstandes erfasst
	 * werden.
	 *
	 * @param context Kontextknoten, mit dem die Prädikat-Subjekt-Objekt-Tripel gespeichert werden sollen.
	 * @param putContextOrNull Kontextknoten zur Erfassung der gegenüber des vorherigen Datenstandes gespeicherten Prädikat-Subjekt-Objekt-Tripel.
	 * @param popContextOrNull Kontextknoten zur Erfassung der gegenüber des vorherigen Datenstandes entfernten Prädikat-Subjekt-Objekt-Tripel.
	 * @param edges Hinzuzufügende Prädikat-Subjekt-Objekt-Tripel.
	 * @return {@code true} bei Änderung des Graphspeicherinhalts bzw. {@code false} sonst.
	 * @see QESet#putAll() */
	public boolean putEdges_DONE(final QN context, final QN putContextOrNull, final QN popContextOrNull, final Iterable<? extends QE> edges)
		throws NullPointerException, IllegalArgumentException {
		return this.putEdgesImpl(context, putContextOrNull, popContextOrNull, context.owner().newEdges(edges).withContext(context));
	}

	/** Diese Methode entfernt die als {@link QE Hyperkanten} gegebenen Prädikat-Subjekt-Objekt-Tripel mit dem gegebenen {@link QE#context() Kontextknoten} aus
	 * {@link QE#owner() dessen} Graphspeicher. Mithilfe der beiden anderen Kontextknoten kann der Unterschied gegenüber eines vorherigen Datenstandes erfasst
	 * werden.
	 *
	 * @param context Kontextknoten, mit dem die Prädikat-Subjekt-Objekt-Tripel entfernt werden sollen.
	 * @param putContextOrNull Kontextknoten zur Erfassung der gegenüber des vorherigen Datenstandes gespeicherten Prädikat-Subjekt-Objekt-Tripel.
	 * @param popContextOrNull Kontextknoten zur Erfassung der gegenüber des vorherigen Datenstandes entfernten Prädikat-Subjekt-Objekt-Tripel.
	 * @param edges Hinzuzufügende Prädikat-Subjekt-Objekt-Tripel.
	 * @return {@code true} bei Änderung des Graphspeicherinhalts bzw. {@code false} sonst.
	 * @see QESet#putAll() */
	public boolean popEdges_DONE(final QN context, final QN putContextOrNull, final QN popContextOrNull, final Iterable<? extends QE> edges)
		throws NullPointerException, IllegalArgumentException {
		return this.popEdgesImpl(context, putContextOrNull, popContextOrNull, context.owner().newEdges(edges).withContext(context));
	}

	/** Diese Methode fügt an die gegebene Liste die im gegebenen {@link QS Graphspeicher} hinterlegten {@link QN Hyperknoten} mit den gegebenen {@link QN#value()
	 * Textwerten} an.
	 *
	 * @param result Liste der ermittelten Hyperknoten.
	 * @param values Liste der gegebenen Textwerte.
	 * @param owner Besitzer der Hyperknoten.
	 * @param dirty {@code true}, wenn für Textwerte ohne Hyperknoten {@code null} angefügt werden soll.<br>
	 *        {@code false}, wenn Textwerte ohne Hyperknoten ignoriert werden sollen. */
	public void getNodes_DONE(final List<QN> result, final List<String> values, final QS owner, final boolean dirty)
		throws NullPointerException, IllegalArgumentException {
		final var sourceToResult = new HashMap2<String, QN>(values.size());
		owner.newValues(values).nodes(sourceToResult::put);
		this.getItems(result, values, sourceToResult, dirty);
	}

	/** Diese Methode fügt in die gegebene Abbilding die im gegebenen {@link QS Graphspeicher} hinterlegten {@link QN Hyperknoten} mit den gegebenen
	 * {@link QN#value() Textwerten} ein.
	 *
	 * @param result Abbilding von Schlüsseln auf ermittelte Hyperknoten.
	 * @param values Abbilding von Schlüsseln auf gegebene Textwerte.
	 * @param owner Besitzer der Hyperknoten.
	 * @param dirty {@code true}, wenn für Textwerte ohne Hyperknoten {@code null} eingefügt werden soll.<br>
	 *        {@code false}, wenn Textwerte ohne Hyperknoten ignoriert werden sollen. */
	public <K> void getNodes_DONE(final Map<K, QN> result, final Map<K, String> values, final QS owner, final boolean dirty)
		throws NullPointerException, IllegalArgumentException {
		final var sourceToResult = new HashMap2<String, QN>(values.size());
		owner.newValues(values.values()).nodes(sourceToResult::put);
		this.getItems(result, values, sourceToResult, dirty);
	}

	/** Diese Methode fügt an die gegebene Liste die im gegebenen {@link QS Graphspeicher} hinterlegten {@link QN#value() Textwerte} der gegebenen {@link QN
	 * Hyperknoten} an.
	 *
	 * @param result Liste der ermittelten Textwerte.
	 * @param nodes Liste der gegebenen Hyperknoten.
	 * @param owner Besitzer der Hyperknoten.
	 * @param dirty {@code true}, wenn für Hyperknoten ohne Textwerte {@code null} angefügt werden soll.<br>
	 *        {@code false}, wenn Hyperknoten ohne Textwerte ignoriert werden sollen. */
	public void getValues_DONE(final List<String> result, final List<QN> nodes, final QS owner, final boolean dirty)
		throws NullPointerException, IllegalArgumentException {
		final var sourceToResult = new HashMap2<QN, String>(nodes.size());
		owner.newNodes(nodes).values(sourceToResult::put);
		this.getItems(result, nodes, sourceToResult, dirty);
	}

	/** Diese Methode fügt in die gegebene Abbilding die im gegebenen {@link QS Graphspeicher} hinterlegten {@link QN#value() Textwerte} der gegebenen {@link QN
	 * Hyperknoten} ein.
	 *
	 * @param result Abbilding von Schlüsseln auf ermittelte Textwerte.
	 * @param nodes Abbilding von Schlüsseln auf gegebenen Hyperknoten.
	 * @param owner Besitzer der Hyperknoten.
	 * @param dirty {@code true}, wenn für Hyperknoten ohne Textwerte {@code null} eingefügt werden soll.<br>
	 *        {@code false}, wenn Hyperknoten ohne Textwerte ignoriert werden sollen. */
	public <K> void getValues_DONE(final Map<K, String> result, final Map<K, QN> nodes, final QS owner, final boolean dirty)
		throws NullPointerException, IllegalArgumentException {
		final var sourceToResult = new HashMap2<QN, String>(nodes.size());
		owner.newNodes(nodes.values()).values(sourceToResult::put);
		this.getItems(result, nodes, sourceToResult, dirty);
	}

	/** Diese Methode liefert zu jedem der gegebenen {@link QE#subject() Subjektknoten} einen diesem über {@link QE Hyperkanten} mit dem gegebenen
	 * {@link QE#context() Kontextknoten} und dem gegebenen {@link QE#predicate() Prädikatknoten} zugeordneten {@link QE#object() Objektknoten}. Zu jedem
	 * Subjektknoten, dem kein Objektknoten zugeordnet ist, wird {@code null} geliefert.
	 *
	 * @param context Kontextknoten der betrachteten Hyperkanten.
	 * @param predicate Prädikatknoten der betrachteten Hyperkanten.
	 * @param subjects Subjektknoten der betrachteten Hyperkanten.
	 * @return Abbildung von Subjektknoten auf Objektknoten oder {@code null}. */
	public HashMap2<QN, QN> getObject_DONE(final QN context, final QN predicate, final Iterable<? extends QN> subjects)
		throws NullPointerException, IllegalArgumentException {
		final var result = new HashMap2<QN, QN>(100);
		for (final var subject: subjects) {
			result.put(subject, null);
		}
		for (final var edge: this.oldObjectEdgeSet(context, predicate, result)) {
			result.put(edge.subject(), edge.object());
		}
		return result;
	}

	/** Diese Methode liefert zu jedem der gegebenen {@link QE#subject() Subjektknoten} alle diesem über {@link QE Hyperkanten} mit dem gegebenen
	 * {@link QE#context() Kontextknoten} und dem gegebenen {@link QE#predicate() Prädikatknoten} zugeordneten {@link QE#object() Objektknoten}. Zu jedem
	 * Subjektknoten, dem kein Objektknoten zugeordnet ist, wird eine leere Liste geliefert.
	 *
	 * @param context Kontextknoten der betrachteten Hyperkanten.
	 * @param predicate Prädikatknoten der betrachteten Hyperkanten.
	 * @param subjects Subjektknoten der betrachteten Hyperkanten.
	 * @return Abbildung von Subjektknoten auf Objektknotenlisten. */
	public HashMap2<QN, List<QN>> getObjects_DONE(final QN context, final QN predicate, final Iterable<? extends QN> subjects)
		throws NullPointerException, IllegalArgumentException {
		final var result = new HashMap2<QN, List<QN>>(100);
		for (final var subject: subjects) {
			result.put(subject, new ArrayList<QN>());
		}
		for (final var edge: this.oldObjectEdgeSet(context, predicate, result)) {
			result.get(edge.subject()).add(edge.object());
		}
		return result;
	}

	public void setObject(final QN context, final QN predicate, final Map<? extends QN, ? extends QN> object, final QN putContextOrNull,
		final QN popContextOrNull) {
		this.setEdgesImpl(context, putContextOrNull, popContextOrNull, //
			this.oldObjectEdgeSet(context, predicate, object).copy(), this.newObjectEdgeSet(context, predicate, object).copy());
	}

	public void setObjects(final QN context, final QN predicate, final Map<? extends QN, ? extends Iterable<? extends QN>> object, final QN putContextOrNull,
		final QN popContextOrNull) {
		final QESet oldEdges = this.oldObjectEdgeSet(context, predicate, object).copy();
		final QESet newEdges = this.newObjectsEdgeSet(context, predicate, object).copy();
		this.setEdgesImpl(context, putContextOrNull, popContextOrNull, oldEdges, newEdges);
	}

	/** Diese Methode ergänzt zu jedem der gegebenen {@link QE#subject() Subjektknoten} die {@link QE Hyperkanten} zu den gegebenen zugeordneten
	 * {@link QE#object() Objektknoten} mit dem gegebenen {@link QE#context() Kontextknoten} und dem gegebenen {@link QE#predicate() Prädikatknoten}.
	 *
	 * @see #getObject_DONE(QN, QN, Iterable)
	 * @param context Kontextknoten der betrachteten Hyperkanten.
	 * @param predicate Prädikatknoten der betrachteten Hyperkanten.
	 * @param objects Abbildung von Subjektknoten auf Objektknoten oder {@code null}. */
	public void putObject_DONE(final QN context, final QN putContextOrNull, final QN popContextOrNull, final QN predicate,
		final Map<? extends QN, ? extends QN> objects) {
		this.putEdgesImpl(context, putContextOrNull, popContextOrNull, this.newObjectEdgeSet(context, predicate, objects));
	}

	/** Diese Methode ergänzt zu jedem der gegebenen {@link QE#subject() Subjektknoten} die {@link QE Hyperkanten} zu allen gegebenen zugeordneten
	 * {@link QE#object() Objektknoten} mit dem gegebenen {@link QE#context() Kontextknoten} und dem gegebenen {@link QE#predicate() Prädikatknoten}.
	 *
	 * @see #getObjects_DONE(QN, QN, Iterable)
	 * @param context Kontextknoten der betrachteten Hyperkanten.
	 * @param predicate Prädikatknoten der betrachteten Hyperkanten.
	 * @param objects Abbildung von Subjektknoten auf Objektknotenlisten. */
	public void putObjects_DONE(final QN context, final QN putContextOrNull, final QN popContextOrNull, final QN predicate,
		final Map<? extends QN, ? extends Iterable<? extends QN>> objects) {
		this.putEdgesImpl(context, putContextOrNull, popContextOrNull, this.newObjectsEdgeSet(context, predicate, objects));
	}

	public void popObjects(final QN context, final QN putContextOrNull, final QN popContextOrNull, final QN predicate,
		final Map<? extends QN, ? extends Iterable<? extends QN>> objects) {
		this.popEdgesImpl(context, putContextOrNull, popContextOrNull, this.newObjectsEdgeSet(context, predicate, objects));
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
		final var nodesToCheckForValue = new HashSet2<QN>(100);

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
				this.getCloneWithObject_DONE(context, cloneEdgeWithObject, cloneNodeWithObject, predicatesFromEdgesHavingObjectsToCloneForUpdate);
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
				this.getCloneWithSubject_DONE(context, cloneEdgeWithSubject, cloneNodeWithSubject, predicatesFromEdgesHavingSubjectsToCloneForUpdate);
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

	/** Diese Methode ermittelt zu jedem der gegebenen {@link QE#predicate() Prädikatknoten}, ob beim {@link #getCloneEdges(Map, QN, Iterable) Klonen} von
	 * {@link QE#object() Objektknoten} im Rahmen des gegebenen {@link QE#context() Kontextknoten} auch die {QE Hyperkanten} mit diesen Kontext, Prädikat- und
	 * Objektknoten geklont werden sollen und ob zudem noch deren {@link QE#subject() Subjektknoten} geklont werden sollen.
	 *
	 * @param context Kontextknoten.
	 * @param cloneEdgeWithObject Methode zur Markierung eines Prädikatknoten zum Klonen seiner Hyperkanten.
	 * @param cloneNodeWithObject Methode zur Markierung eines Prädikatknoten zum Klonen seiner Hyperkanten und derer Subjektknoen.
	 * @param predicates Prädikatknoten. */
	protected void getCloneWithObject_DONE(final QN context, final Consumer<QN> cloneEdgeWithObject, final Consumer<QN> cloneNodeWithObject,
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
	protected void getCloneWithSubject_DONE(final QN context, final Consumer<QN> cloneEdgeWithSubject, final Consumer<QN> cloneNodeWithSubject,
		final Iterable<QN> predicates) {
	}

	private void setEdgesImpl(final QN context, final QN putContextOrNull, final QN popContextOrNull, final QESet oldEdges, final QESet newEdges) {
		this.putEdgesImpl(context, putContextOrNull, popContextOrNull, newEdges.except(oldEdges));
		this.popEdgesImpl(context, putContextOrNull, popContextOrNull, oldEdges.except(newEdges));
	}

	private boolean putEdgesImpl(final QN context, final QN putContextOrNull, final QN popContextOrNull, final QESet putEdges) {
		if (!this.useAllContexts(context, putContextOrNull, popContextOrNull)) return putEdges.putAll();
		final QESet putEdges2 = putEdges.except(context.owner().edges()).copy();
		return putEdges2.putAll() | putEdges2.withContext(putContextOrNull).putAll() | putEdges2.withContext(popContextOrNull).popAll();
	}

	private boolean popEdgesImpl(final QN context, final QN putContextOrNull, final QN popContextOrNull, final QESet popEdges) {
		if (!this.useAllContexts(context, putContextOrNull, popContextOrNull)) return popEdges.popAll();
		final QESet popEdges2 = popEdges.intersect(context.owner().edges()).copy();
		return popEdges2.popAll() | popEdges2.withContext(putContextOrNull).popAll() | popEdges2.withContext(popContextOrNull).putAll();
	}

	private boolean useAllContexts(final QN context, final QN putContextOrNull, final QN popContextOrNull) throws NullPointerException, IllegalArgumentException {
		if ((context == null) || ((putContextOrNull == null) != (popContextOrNull == null))) throw new NullPointerException();
		if ((context == putContextOrNull) || (context == popContextOrNull)) throw new IllegalArgumentException();
		if (putContextOrNull == null) return false;
		if (putContextOrNull != popContextOrNull) return true;
		throw new IllegalArgumentException();
	}

	private QESet oldObjectEdgeSet(final QN context, final QN predicate, final Map<? extends QN, ?> subjects) {
		final QS qs = context.owner();
		return qs.edges().havingContext(context).havingPredicate(predicate).havingSubjects(qs.newNodes(subjects.keySet()));
	}

	private QESet newObjectEdgeSet(final QN context, final QN predicate, final Map<? extends QN, ? extends QN> object) {
		final var owner = context.owner();
		final ArrayList<QE> edges = new ArrayList<>(object.size());
		for (final Entry<? extends QN, ? extends QN> entry: object.entrySet()) {
			final QN subject = entry.getValue();
			if (subject != null) {
				edges.add(owner.newEdge(context, predicate, entry.getKey(), subject));
			}
		}
		return owner.newEdges(edges);
	}

	private QESet newObjectsEdgeSet(final QN context, final QN predicate, final Map<? extends QN, ? extends Iterable<? extends QN>> objects) {
		final var owner = context.owner();
		final var predicateEdge = owner.newEdge(context, predicate, predicate, predicate);
		return owner.newEdges(Iterables.concatAll(Iterables.translate(objects.entrySet(), entry -> {
			final var subjectEdge = predicateEdge.withSubject(entry.getKey());
			return Iterables.translate(entry.getValue(), subjectEdge::withObject);
		})));
	}

	private <R, S> void getItems(final List<R> results, final List<S> sources, final Map<S, R> sourceToResult, final boolean keepNull) {
		for (final var source: sources) {
			final var result = sourceToResult.get(source);
			if ((result != null) || keepNull) {
				results.add(result);
			}
		}
	}

	private <K, R, S> void getItems(final Map<K, R> results, final Map<K, S> sources, final Map<S, R> sourceToResult, final boolean keepNull) {
		for (final var source: sources.entrySet()) {
			final var result = sourceToResult.get(source.getValue());
			if ((result != null) || keepNull) {
				results.put(source.getKey(), result);
			}
		}
	}

}
