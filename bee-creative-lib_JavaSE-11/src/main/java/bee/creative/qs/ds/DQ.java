package bee.creative.qs.ds;

import static bee.creative.util.Translators.translatorFrom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import bee.creative.fem.FEMDatetime;
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
import bee.creative.util.Translator2;

// TODO
public class DQ {

	public static final Translator2<String, FEMDatetime> datetimeTrans =
		translatorFrom(String.class, FEMDatetime.class, FEMDatetime::from, FEMDatetime::toString).optionalize();

	public static List<QN> getObjectSet(QN context, QN predicate, QN subject) throws NullPointerException, IllegalArgumentException {
		return DQ.getEdgesHavingContextAndPredicate(context, predicate).withSubject(subject).objects().toList();
	}

	public static HashMap2<QN, QN> getObjectMap(QN context, QN predicate) throws NullPointerException, IllegalArgumentException {
		var subjectObjectMap = new HashMap2<QN, QN>(10);
		DQ.getEdgesHavingContextAndPredicate(context, predicate).forEach(edge -> subjectObjectMap.put(edge.subject(), edge.object()));
		return subjectObjectMap;
	}

	/** Diese Methode liefert zu jedem der gegebenen {@link QE#subject() Subjektknoten} einen diesem über {@link QE Hyperkanten} mit dem gegebenen
	 * {@link QE#context() Kontextknoten} und dem gegebenen {@link QE#predicate() Prädikatknoten} zugeordneten {@link QE#object() Objektknoten}. Zu jedem
	 * Subjektknoten, dem kein Objektknoten zugeordnet ist, wird {@code null} geliefert.
	 *
	 * @param context Kontextknoten der betrachteten Hyperkanten.
	 * @param predicate Prädikatknoten der betrachteten Hyperkanten.
	 * @param subjectSet Subjektknoten der betrachteten Hyperkanten.
	 * @return Abbildung von Subjektknoten auf Objektknoten oder {@code null}. */
	public static HashMap2<QN, QN> getObjectMap(QN context, QN predicate, Iterable<? extends QN> subjectSet)
		throws NullPointerException, IllegalArgumentException {
		var subjectObjectMap = DQ.newNodeNodeMap(subjectSet);
		DQ.getEdgesHavingContextAndPredicateAndSubjects(context, predicate, subjectObjectMap.keySet())
			.forEach(edge -> subjectObjectMap.put(edge.subject(), edge.object()));
		return subjectObjectMap;
	}

	public static HashMap2<QN, List<QN>> getObjectSetMap(QN context, QN predicate) throws NullPointerException, IllegalArgumentException {
		var objectSet = (Getter<QN, List<QN>>)object -> new ArrayList<>();
		var subjectObjectSetMap = new HashMap2<QN, List<QN>>(10);
		DQ.getEdgesHavingContextAndPredicate(context, predicate).forEach(edge -> subjectObjectSetMap.install(edge.subject(), objectSet).add(edge.object()));
		return subjectObjectSetMap;
	}

	/** Diese Methode liefert zu jedem der gegebenen {@link QE#subject() Subjektknoten} alle diesem über {@link QE Hyperkanten} mit dem gegebenen
	 * {@link QE#context() Kontextknoten} und dem gegebenen {@link QE#predicate() Prädikatknoten} zugeordneten {@link QE#object() Objektknoten}. Zu jedem
	 * Subjektknoten, dem kein Objektknoten zugeordnet ist, wird eine leere Liste geliefert.
	 *
	 * @param context Kontextknoten der betrachteten Hyperkanten.
	 * @param predicate Prädikatknoten der betrachteten Hyperkanten.
	 * @param subjectSet Subjektknoten der betrachteten Hyperkanten.
	 * @return Abbildung von Subjektknoten auf Objektknotenlisten. */
	public static HashMap2<QN, List<QN>> getObjectSetMap(QN context, QN predicate, Iterable<? extends QN> subjectSet)
		throws NullPointerException, IllegalArgumentException {
		var subjectObjectSetMap = DQ.newNodeNodeSetMap(subjectSet);
		DQ.getEdgesHavingContextAndPredicateAndSubjects(context, predicate, subjectObjectSetMap.keySet())
			.forEach(edge -> subjectObjectSetMap.get(edge.subject()).add(edge.object()));
		return subjectObjectSetMap;
	}

	public static List<QN> getSubjectSet(QN context, QN predicate, QN object) throws NullPointerException, IllegalArgumentException {
		return DQ.getEdgesHavingContextAndPredicate(context, predicate).withObject(object).objects().toList();
	}

	public static HashMap2<QN, QN> getSubjectMap(QN context, QN predicate) throws NullPointerException, IllegalArgumentException {
		var objectSubjectMap = new HashMap2<QN, QN>(10);
		DQ.getEdgesHavingContextAndPredicate(context, predicate).forEach(edge -> objectSubjectMap.put(edge.object(), edge.subject()));
		return objectSubjectMap;
	}

	public static HashMap2<QN, QN> getSubjectMap(QN context, QN predicate, Iterable<? extends QN> objectSet)
		throws NullPointerException, IllegalArgumentException {
		var objectSubjectMap = DQ.newNodeNodeMap(objectSet);
		DQ.getEdgesHavingContextAndPredicateAndObjects(context, predicate, objectSubjectMap.keySet())
			.forEach(edge -> objectSubjectMap.put(edge.object(), edge.subject()));
		return objectSubjectMap;
	}

	public static HashMap2<QN, List<QN>> getSubjectSetMap(QN context, QN predicate) throws NullPointerException, IllegalArgumentException {
		var subjectSet = (Getter<QN, List<QN>>)object -> new ArrayList<>();
		var objectSubjectSetMap = new HashMap2<QN, List<QN>>(10);
		DQ.getEdgesHavingContextAndPredicate(context, predicate).forEach(edge -> objectSubjectSetMap.install(edge.object(), subjectSet).add(edge.subject()));
		return objectSubjectSetMap;
	}

	public static HashMap2<QN, List<QN>> getSubjectSetMap(QN context, QN predicate, Iterable<? extends QN> objectSet)
		throws NullPointerException, IllegalArgumentException {
		var objectSubjectSetMap = DQ.newNodeNodeSetMap(objectSet);
		DQ.getEdgesHavingContextAndPredicateAndObjects(context, predicate, objectSubjectSetMap.keySet())
			.forEach(edge -> objectSubjectSetMap.get(edge.object()).add(edge.subject()));
		return objectSubjectSetMap;
	}

	public static boolean setObjectMap(QN context, QN predicate, Map<? extends QN, ? extends QN> subjectObjectMap, QN putContextOrNull, QN popContextOrNull) {
		return DQ.setEdgesImpl(context, putContextOrNull, popContextOrNull, //
			DQ.getEdgesHavingContextAndPredicateAndSubjects(context, predicate, subjectObjectMap.keySet()).copy(),
			DQ.newSubjectObjectMapEdges(context, predicate, subjectObjectMap).copy());
	}

	public static boolean setObjectSetMap(QN context, QN predicate, Map<? extends QN, ? extends Iterable<? extends QN>> subjectObjectSetMap, QN putContextOrNull,
		QN popContextOrNull) {
		return DQ.setEdgesImpl(context, putContextOrNull, popContextOrNull, //
			DQ.getEdgesHavingContextAndPredicateAndSubjects(context, predicate, subjectObjectSetMap.keySet()).copy(),
			DQ.newSubjectObjectSetMapEdges(context, predicate, subjectObjectSetMap).copy());
	}

	public static boolean setSubjectMap(QN context, QN predicate, Map<? extends QN, ? extends QN> objectSubjectMap, QN putContextOrNull, QN popContextOrNull) {
		return DQ.setEdgesImpl(context, putContextOrNull, popContextOrNull, //
			DQ.getEdgesHavingContextAndPredicateAndObjects(context, predicate, objectSubjectMap.keySet()).copy(),
			DQ.newObjectSubjectMapEdges(context, predicate, objectSubjectMap).copy());
	}

	public static boolean setSubjectSetMap(QN context, QN predicate, Map<? extends QN, ? extends Iterable<? extends QN>> objectSubjectSetMap, QN putContextOrNull,
		QN popContextOrNull) {
		return DQ.setEdgesImpl(context, putContextOrNull, popContextOrNull, //
			DQ.getEdgesHavingContextAndPredicateAndObjects(context, predicate, objectSubjectSetMap.keySet()).copy(),
			DQ.newObjectSubjectSetMapEdges(context, predicate, objectSubjectSetMap).copy());
	}

	/** Diese Methode ergänzt zu jedem der gegebenen {@link QE#subject() Subjektknoten} die {@link QE Hyperkanten} zu den gegebenen zugeordneten
	 * {@link QE#object() Objektknoten} mit dem gegebenen {@link QE#context() Kontextknoten} und dem gegebenen {@link QE#predicate() Prädikatknoten}.
	 *
	 * @see #getObjectMap(QN, QN, Iterable)
	 * @param context Kontextknoten der betrachteten Hyperkanten.
	 * @param predicate Prädikatknoten der betrachteten Hyperkanten.
	 * @param subjectObjectMap Abbildung von Subjektknoten auf Objektknoten oder {@code null}.
	 * @return */
	public static boolean putObjectMap(QN context, QN predicate, Map<? extends QN, ? extends QN> subjectObjectMap, QN putContextOrNull, QN popContextOrNull) {
		return DQ.putEdgesImpl(context, putContextOrNull, popContextOrNull, DQ.newSubjectObjectMapEdges(context, predicate, subjectObjectMap));
	}

	/** Diese Methode ergänzt zu jedem der gegebenen {@link QE#subject() Subjektknoten} die {@link QE Hyperkanten} zu allen gegebenen zugeordneten
	 * {@link QE#object() Objektknoten} mit dem gegebenen {@link QE#context() Kontextknoten} und dem gegebenen {@link QE#predicate() Prädikatknoten}.
	 *
	 * @see #getObjectSetMap(QN, QN, Iterable)
	 * @param context Kontextknoten der betrachteten Hyperkanten.
	 * @param predicate Prädikatknoten der betrachteten Hyperkanten.
	 * @param subjectObjectSetMap Abbildung von Subjektknoten auf Objektknotenlisten.
	 * @return */
	public static boolean putObjectSetMap(QN context, QN predicate, Map<? extends QN, ? extends Iterable<? extends QN>> subjectObjectSetMap, QN putContextOrNull,
		QN popContextOrNull) {
		return DQ.putEdgesImpl(context, putContextOrNull, popContextOrNull, DQ.newSubjectObjectSetMapEdges(context, predicate, subjectObjectSetMap));
	}

	public static boolean putSubjectMap(QN context, QN predicate, Map<? extends QN, ? extends QN> objectSubjectMap, QN putContextOrNull, QN popContextOrNull) {
		return DQ.putEdgesImpl(context, putContextOrNull, popContextOrNull, DQ.newObjectSubjectMapEdges(context, predicate, objectSubjectMap));
	}

	public static boolean putSubjectSetMap(QN context, QN predicate, Map<? extends QN, ? extends Iterable<? extends QN>> objectSubjectSetMap, QN putContextOrNull,
		QN popContextOrNull) {
		return DQ.putEdgesImpl(context, putContextOrNull, popContextOrNull, DQ.newObjectSubjectSetMapEdges(context, predicate, objectSubjectSetMap));
	}

	public static boolean popObjectMap(QN context, QN predicate, Map<? extends QN, ? extends QN> subjectObjectMap, QN putContextOrNull, QN popContextOrNull) {
		return DQ.popEdgesImpl(context, putContextOrNull, popContextOrNull, DQ.newSubjectObjectMapEdges(context, predicate, subjectObjectMap));
	}

	public static boolean popObjectSetMap(QN context, QN predicate, Map<? extends QN, ? extends Iterable<? extends QN>> subjectObjectSetMap, QN putContextOrNull,
		QN popContextOrNull) {
		return DQ.popEdgesImpl(context, putContextOrNull, popContextOrNull, DQ.newSubjectObjectSetMapEdges(context, predicate, subjectObjectSetMap));
	}

	public static boolean popSubjectMap(QN context, QN predicate, Map<? extends QN, ? extends QN> objectSubjectMap, QN putContextOrNull, QN popContextOrNull) {
		return DQ.popEdgesImpl(context, putContextOrNull, popContextOrNull, DQ.newObjectSubjectMapEdges(context, predicate, objectSubjectMap));
	}

	public static boolean popSubjectSetMap(QN context, QN predicate, Map<? extends QN, ? extends Iterable<? extends QN>> objectSubjectSetMap, QN putContextOrNull,
		QN popContextOrNull) {
		return DQ.popEdgesImpl(context, putContextOrNull, popContextOrNull, DQ.newObjectSubjectSetMapEdges(context, predicate, objectSubjectSetMap));
	}

	static QESet getEdgesHavingContextAndPredicate(QN context, QN predicate) {
		return context.owner().edges().havingContext(context).havingPredicate(predicate);
	}

	static QESet getEdgesHavingContextAndPredicateAndObjects(QN context, QN predicate, Iterable<? extends QN> objectSet) {
		return DQ.getEdgesHavingContextAndPredicate(context, predicate).havingObjects(context.owner().newNodes(objectSet));
	}

	static QESet getEdgesHavingContextAndPredicateAndSubjects(QN context, QN predicate, Iterable<? extends QN> subjectSet) {
		return DQ.getEdgesHavingContextAndPredicate(context, predicate).havingSubjects(context.owner().newNodes(subjectSet));
	}

	static HashMap2<QN, QN> newNodeNodeMap(Iterable<? extends QN> nodeSet) {
		var nodeMap = new HashMap2<QN, QN>(10);
		nodeSet.forEach(node -> nodeMap.put(node, null));
		return nodeMap;
	}

	static HashMap2<QN, List<QN>> newNodeNodeSetMap(Iterable<? extends QN> nodeSet) {
		var nodeSetMap = new HashMap2<QN, List<QN>>(10);
		nodeSet.forEach(node -> nodeSetMap.put(node, new ArrayList<>()));
		return nodeSetMap;
	}

	static QESet newObjectSubjectMapEdges(QN context, QN predicate, Map<? extends QN, ? extends QN> objectSubjectMap) {
		var owner = context.owner();
		return owner.newEdges(Iterables.translatedIterable(objectSubjectMap.entrySet(), entry -> {
			var subject = entry.getValue();
			return subject != null ? owner.newEdge(context, predicate, subject, entry.getKey()) : null;
		}).filter(Filters.nullFilter()));
	}

	static QESet newObjectSubjectSetMapEdges(QN context, QN predicate, Map<? extends QN, ? extends Iterable<? extends QN>> objectSubjectSetMap) {
		var owner = context.owner();
		return owner.newEdges(Iterables.concatAll(Iterables.translatedIterable(objectSubjectSetMap.entrySet(), entry -> {
			var object = entry.getKey();
			return Iterables.translatedIterable(entry.getValue(), subject -> owner.newEdge(context, predicate, subject, object));
		})));
	}

	static QESet newSubjectObjectMapEdges(QN context, QN predicate, Map<? extends QN, ? extends QN> subjectObjectMap) {
		var owner = context.owner();
		return owner.newEdges(Iterables.translatedIterable(subjectObjectMap.entrySet(), entry -> {
			var object = entry.getValue();
			return object != null ? owner.newEdge(context, predicate, entry.getKey(), object) : null;
		}).filter(Filters.nullFilter()));
	}

	static QESet newSubjectObjectSetMapEdges(QN context, QN predicate, Map<? extends QN, ? extends Iterable<? extends QN>> subjectObjectSetMap) {
		var owner = context.owner();
		return owner.newEdges(Iterables.concatAll(Iterables.translatedIterable(subjectObjectSetMap.entrySet(), entry -> {
			var subject = entry.getKey();
			return Iterables.translatedIterable(entry.getValue(), object -> owner.newEdge(context, predicate, subject, object));
		})));
	}

	/** Diese Methode speichert die als {@link QE Hyperkanten} gegebenen Prädikat-Subjekt-Objekt-Tripel mit dem gegebenen {@link QE#context() Kontextknoten} in
	 * {@link QE#owner() dessen} Graphspeicher. Mithilfe der beiden anderen Kontextknoten kann der Unterschied gegenüber eines vorherigen Datenstandes erfasst
	 * werden.
	 *
	 * @param context Kontextknoten, mit dem die Prädikat-Subjekt-Objekt-Tripel gespeichert werden sollen.
	 * @param edges Hinzuzufügende Prädikat-Subjekt-Objekt-Tripel.
	 * @param putContextOrNull Kontextknoten zur Erfassung der gegenüber des vorherigen Datenstandes gespeicherten Prädikat-Subjekt-Objekt-Tripel.
	 * @param popContextOrNull Kontextknoten zur Erfassung der gegenüber des vorherigen Datenstandes entfernten Prädikat-Subjekt-Objekt-Tripel.
	 * @return {@code true} bei Änderung des Graphspeicherinhalts bzw. {@code false} sonst.
	 * @see QESet#putAll() */
	public static boolean putEdges(QN context, Iterable<? extends QE> edges, QN putContextOrNull, QN popContextOrNull)
		throws NullPointerException, IllegalArgumentException {
		return DQ.putEdgesImpl(context, putContextOrNull, popContextOrNull, context.owner().newEdges(edges).withContext(context));
	}

	/** Diese Methode entfernt die als {@link QE Hyperkanten} gegebenen Prädikat-Subjekt-Objekt-Tripel mit dem gegebenen {@link QE#context() Kontextknoten} aus
	 * {@link QE#owner() dessen} Graphspeicher. Mithilfe der beiden anderen Kontextknoten kann der Unterschied gegenüber eines vorherigen Datenstandes erfasst
	 * werden.
	 *
	 * @param context Kontextknoten, mit dem die Prädikat-Subjekt-Objekt-Tripel entfernt werden sollen.
	 * @param edges Zuentfernende Prädikat-Subjekt-Objekt-Tripel.
	 * @param putContextOrNull Kontextknoten zur Erfassung der gegenüber des vorherigen Datenstandes gespeicherten Prädikat-Subjekt-Objekt-Tripel.
	 * @param popContextOrNull Kontextknoten zur Erfassung der gegenüber des vorherigen Datenstandes entfernten Prädikat-Subjekt-Objekt-Tripel.
	 * @return {@code true} bei Änderung des Graphspeicherinhalts bzw. {@code false} sonst.
	 * @see QESet#popAll() */
	public static boolean popEdges(QN context, Iterable<? extends QE> edges, QN putContextOrNull, QN popContextOrNull)
		throws NullPointerException, IllegalArgumentException {
		return DQ.popEdgesImpl(context, putContextOrNull, popContextOrNull, context.owner().newEdges(edges).withContext(context));
	}

	static boolean setEdgesImpl(QN context, QN putContextOrNull, QN popContextOrNull, QESet oldEdges, QESet newEdges) {
		return DQ.putEdgesImpl(context, putContextOrNull, popContextOrNull, newEdges.except(oldEdges))
			| DQ.popEdgesImpl(context, putContextOrNull, popContextOrNull, oldEdges.except(newEdges));
	}

	static boolean putEdgesImpl(QN context, QN putContextOrNull, QN popContextOrNull, QESet putEdges) {
		if (!DQ.useAllContexts(context, putContextOrNull, popContextOrNull)) return putEdges.putAll();
		var putEdges2 = putEdges.except(context.owner().edges()).copy();
		return putEdges2.putAll() | putEdges2.withContext(putContextOrNull).putAll() | putEdges2.withContext(popContextOrNull).popAll();
	}

	static boolean popEdgesImpl(QN context, QN putContextOrNull, QN popContextOrNull, QESet popEdges) {
		if (!DQ.useAllContexts(context, putContextOrNull, popContextOrNull)) return popEdges.popAll();
		var popEdges2 = popEdges.intersect(context.owner().edges()).copy();
		return popEdges2.popAll() | popEdges2.withContext(putContextOrNull).popAll() | popEdges2.withContext(popContextOrNull).putAll();
	}

	static boolean useAllContexts(QN context, QN putContextOrNull, QN popContextOrNull) throws NullPointerException, IllegalArgumentException {
		if ((context == null) || ((putContextOrNull == null) != (popContextOrNull == null))) throw new NullPointerException();
		if ((context == putContextOrNull) || (context == popContextOrNull)) throw new IllegalArgumentException();
		if (putContextOrNull == null) return false;
		if (putContextOrNull != popContextOrNull) return true;
		throw new IllegalArgumentException();
	}

	int TODO;

	/** Diese Methode fügt an die gegebene Liste die im gegebenen {@link QS Graphspeicher} hinterlegten {@link QN Hyperknoten} mit den gegebenen {@link QN#value()
	 * Textwerten} an.
	 *
	 * @param result Liste der ermittelten Hyperknoten.
	 * @param values Liste der gegebenen Textwerte.
	 * @param owner Besitzer der Hyperknoten.
	 * @param keepNull {@code true}, wenn für Textwerte ohne Hyperknoten {@code null} angefügt werden soll.<br>
	 *        {@code false}, wenn Textwerte ohne Hyperknoten ignoriert werden sollen. */
	public void getNodes_DONE(List<QN> result, List<String> values, QS owner, boolean keepNull) throws NullPointerException, IllegalArgumentException {
		var sourceToResult = new HashMap2<String, QN>(values.size());
		owner.newValues(values).nodes(sourceToResult::put);
		DQ.getItems(result, values, sourceToResult, keepNull);
	}

	/** Diese Methode fügt in die gegebene Abbilding die im gegebenen {@link QS Graphspeicher} hinterlegten {@link QN Hyperknoten} mit den gegebenen
	 * {@link QN#value() Textwerten} ein.
	 *
	 * @param result Abbilding von Schlüsseln auf ermittelte Hyperknoten.
	 * @param values Abbilding von Schlüsseln auf gegebene Textwerte.
	 * @param owner Besitzer der Hyperknoten.
	 * @param keepNull {@code true}, wenn für Textwerte ohne Hyperknoten {@code null} eingefügt werden soll.<br>
	 *        {@code false}, wenn Textwerte ohne Hyperknoten ignoriert werden sollen. */
	public <K> void getNodes_DONE(Map<K, QN> result, Map<K, String> values, QS owner, boolean keepNull) throws NullPointerException, IllegalArgumentException {
		var sourceToResult = new HashMap2<String, QN>(values.size());
		owner.newValues(values.values()).nodes(sourceToResult::put);
		DQ.getItems(result, values, sourceToResult, keepNull);
	}

	/** Diese Methode fügt an die gegebene Liste die im gegebenen {@link QS Graphspeicher} hinterlegten {@link QN#value() Textwerte} der gegebenen {@link QN
	 * Hyperknoten} an.
	 *
	 * @param result Liste der ermittelten Textwerte.
	 * @param nodes Liste der gegebenen Hyperknoten.
	 * @param owner Besitzer der Hyperknoten.
	 * @param keepNull {@code true}, wenn für Hyperknoten ohne Textwerte {@code null} angefügt werden soll.<br>
	 *        {@code false}, wenn Hyperknoten ohne Textwerte ignoriert werden sollen. */
	public void getValues_DONE(List<String> result, List<QN> nodes, QS owner, boolean keepNull) throws NullPointerException, IllegalArgumentException {
		var sourceToResult = new HashMap2<QN, String>(nodes.size());
		owner.newNodes(nodes).values(sourceToResult::put);
		DQ.getItems(result, nodes, sourceToResult, keepNull);
	}

	/** Diese Methode fügt in die gegebene Abbilding die im gegebenen {@link QS Graphspeicher} hinterlegten {@link QN#value() Textwerte} der gegebenen {@link QN
	 * Hyperknoten} ein.
	 *
	 * @param result Abbilding von Schlüsseln auf ermittelte Textwerte.
	 * @param nodes Abbilding von Schlüsseln auf gegebenen Hyperknoten.
	 * @param owner Besitzer der Hyperknoten.
	 * @param keepNull {@code true}, wenn für Hyperknoten ohne Textwerte {@code null} eingefügt werden soll.<br>
	 *        {@code false}, wenn Hyperknoten ohne Textwerte ignoriert werden sollen. */
	public <K> void getValues_DONE(Map<K, String> result, Map<K, QN> nodes, QS owner, boolean keepNull) throws NullPointerException, IllegalArgumentException {
		var sourceToResult = new HashMap2<QN, String>(nodes.size());
		owner.newNodes(nodes.values()).values(sourceToResult::put);
		DQ.getItems(result, nodes, sourceToResult, keepNull);
	}

	static <R, S> void getItems(List<R> results, List<S> sources, Map<S, R> sourceToResult, boolean keepNull) {
		for (var source: sources) {
			var result = sourceToResult.get(source);
			if ((result != null) || keepNull) {
				results.add(result);
			}
		}
	}

	static <K, R, S> void getItems(Map<K, R> results, Map<K, S> sources, Map<S, R> sourceToResult, boolean keepNull) {
		for (var source: sources.entrySet()) {
			var result = sourceToResult.get(source.getValue());
			if ((result != null) || keepNull) {
				results.put(source.getKey(), result);
			}
		}
	}

	public QESet getCloneEdges(Map<QN, QN> clones, QN context, Iterable<? extends QN> nodes) {

		var CLONE_WITH_EDGE = new Object();
		var CLONE_WITH_NODE = new Object();

		var cloneWithObject = new HashMap2<QN, Object>(10);
		var cloneEdgeWithObject = (Consumer<QN>)predicate -> cloneWithObject.put(predicate, CLONE_WITH_EDGE);
		var cloneNodeWithObject = (Consumer<QN>)predicate -> cloneWithObject.put(predicate, CLONE_WITH_NODE);

		var cloneWithSubject = new HashMap2<QN, Object>(10);
		var cloneEdgeWithSubject = (Consumer<QN>)predicate -> cloneWithSubject.put(predicate, CLONE_WITH_EDGE);
		var cloneNodeWithSubject = (Consumer<QN>)predicate -> cloneWithSubject.put(predicate, CLONE_WITH_NODE);

		var nodesToClone = new ArrayList<QN>(10);

		var nodesToCheckForClone = new HashSet2<QN>(10);
		var nodesToCheckForValue = new HashSet2<QN>(10);

		var qs = context.owner();
		var edges = qs.edges().havingContext(context);

		Iterables.addAll(nodesToCheckForClone, nodes);

		while (!nodesToCheckForClone.isEmpty()) {

			nodesToCheckForValue.allocate(Math.max(nodesToCheckForValue.size(), nodesToCheckForClone.size()));
			for (var node: nodesToCheckForClone) {
				if (clones.get(node) == null) {
					nodesToCheckForValue.add(node);
				}
			}
			nodesToCheckForClone.clear();

			if (nodesToCheckForValue.isEmpty()) {
				break;
			}

			for (var node: qs.newNodes(nodesToCheckForValue).havingValue()) {
				clones.put(node, node);
				nodesToCheckForValue.remove(node);
			}

			if (nodesToCheckForValue.isEmpty()) {
				break;
			}

			nodesToClone.ensureCapacity(nodesToClone.size() + nodesToCheckForValue.size());
			for (var node: nodesToCheckForValue) {
				clones.put(node, qs.newNode());
				nodesToClone.add(node);
			}

			var nodesToCloneWithoutValue = qs.newNodes(nodesToCheckForValue);
			nodesToCheckForValue.clear();

			var edgesHavingObjectsToClone = edges.havingObjects(nodesToCloneWithoutValue);
			var predicatesFromEdgesHavingObjectsToClone = edgesHavingObjectsToClone.predicates().toList();
			var predicatesFromEdgesHavingObjectsToCloneForUpdate = new ArrayList<QN>(predicatesFromEdgesHavingObjectsToClone.size());
			for (var node: predicatesFromEdgesHavingObjectsToClone) {
				if (!cloneWithObject.containsKey(node)) {
					cloneWithObject.put(node, null);
					predicatesFromEdgesHavingObjectsToCloneForUpdate.add(node);
				}
			}
			if (!predicatesFromEdgesHavingObjectsToCloneForUpdate.isEmpty()) {
				this.getCloneWithObject_DONE(context, cloneEdgeWithObject, cloneNodeWithObject, predicatesFromEdgesHavingObjectsToCloneForUpdate);
			}
			var predicatesFromEdgesHavingObjectsToCloneAndSubjectsToClone = new ArrayList<QN>(predicatesFromEdgesHavingObjectsToClone.size());
			for (var node: predicatesFromEdgesHavingObjectsToClone) {
				if (cloneWithObject.get(node) == CLONE_WITH_NODE) {
					predicatesFromEdgesHavingObjectsToCloneAndSubjectsToClone.add(node);
				}
			}

			var subjectsFromEdgesHavingObjectsToCloneWithSubjectsToClone =
				edgesHavingObjectsToClone.havingPredicates(qs.newNodes(predicatesFromEdgesHavingObjectsToCloneAndSubjectsToClone)).subjects();

			Iterables.addAll(nodesToCheckForClone, subjectsFromEdgesHavingObjectsToCloneWithSubjectsToClone);

			var edgesHavingSubjectsToClone = edges.havingSubjects(nodesToCloneWithoutValue);
			var predicatesFromEdgesHavingSubjectsToClone = edgesHavingSubjectsToClone.predicates().toList();
			var predicatesFromEdgesHavingSubjectsToCloneForUpdate = new ArrayList<QN>(predicatesFromEdgesHavingSubjectsToClone.size());
			for (var node: predicatesFromEdgesHavingSubjectsToClone) {
				if (!cloneWithSubject.containsKey(node)) {
					cloneWithSubject.put(node, null);
					predicatesFromEdgesHavingSubjectsToCloneForUpdate.add(node);
				}
			}
			if (!predicatesFromEdgesHavingSubjectsToCloneForUpdate.isEmpty()) {
				this.getCloneWithSubject_DONE(context, cloneEdgeWithSubject, cloneNodeWithSubject, predicatesFromEdgesHavingSubjectsToCloneForUpdate);
			}
			var predicatesFromEdgesHavingSubjectsToCloneWithObjectsToClone = new ArrayList<QN>(predicatesFromEdgesHavingSubjectsToClone.size());
			for (var node: predicatesFromEdgesHavingSubjectsToClone) {
				if (cloneWithSubject.get(node) == CLONE_WITH_NODE) {
					predicatesFromEdgesHavingSubjectsToCloneWithObjectsToClone.add(node);
				}
			}

			var objectsToClone = edgesHavingSubjectsToClone.havingPredicates(qs.newNodes(predicatesFromEdgesHavingSubjectsToCloneWithObjectsToClone)).objects();

			Iterables.addAll(nodesToCheckForClone, objectsToClone);

		}

		nodesToCheckForClone.compact();
		nodesToCheckForValue.compact();

		var nodesToCloneWithoutValue = qs.newNodes(nodesToClone);
		nodesToClone.clear();
		nodesToClone.trimToSize();

		var edgesHavingObjectsToClone = edges.havingObjects(nodesToCloneWithoutValue);

		var edgesHavingObjectsToCloneIterable = Iterables.translatedIterable(edgesHavingObjectsToClone, edge -> {
			var cloneWith = cloneWithObject.get(edge.predicate());
			if ((cloneWith != CLONE_WITH_EDGE) && (cloneWith != CLONE_WITH_NODE)) return null;
			QN sourceObject = edge.object(), targetObject = clones.get(sourceObject);
			if (sourceObject == targetObject) return null;
			QN sourceSubject = edge.subject(), targetSubject = clones.get(sourceSubject);
			if (sourceSubject == targetSubject) return null;
			if (targetSubject == null) return edge.withObject(targetObject);
			return edge.withObject(targetObject).withSubject(targetSubject);
		});

		var edgesHavingSubjectsToClone = edges.havingSubjects(nodesToCloneWithoutValue);

		var edgesHavingSubjectsToCloneIterable = Iterables.translatedIterable(edgesHavingSubjectsToClone, edge -> {
			var cloneWith = cloneWithObject.get(edge.predicate());
			if ((cloneWith != CLONE_WITH_EDGE) && (cloneWith != CLONE_WITH_NODE)) return null;
			QN sourceObject = edge.object(), targetObject = clones.get(sourceObject);
			if (sourceObject == targetObject) return null;
			QN sourceSubject = edge.subject(), targetSubject = clones.get(sourceSubject);
			if (sourceSubject == targetSubject) return null;
			if (targetObject == null) return edge.withSubject(targetSubject);
			return edge.withObject(targetObject).withSubject(targetSubject);
		});

		return qs.newEdges(edgesHavingObjectsToCloneIterable.concat(edgesHavingSubjectsToCloneIterable).filter(Filters.nullFilter()));
	}

	/** Diese Methode ermittelt zu jedem der gegebenen {@link QE#predicate() Prädikatknoten}, ob beim {@link #getCloneEdges(Map, QN, Iterable) Klonen} von
	 * {@link QE#object() Objektknoten} im Rahmen des gegebenen {@link QE#context() Kontextknoten} auch die {QE Hyperkanten} mit diesen Kontext, Prädikat- und
	 * Objektknoten geklont werden sollen und ob zudem noch deren {@link QE#subject() Subjektknoten} geklont werden sollen.
	 *
	 * @param context Kontextknoten.
	 * @param cloneEdgeWithObject Methode zur Markierung eines Prädikatknoten zum Klonen seiner Hyperkanten.
	 * @param cloneNodeWithObject Methode zur Markierung eines Prädikatknoten zum Klonen seiner Hyperkanten und derer Subjektknoen.
	 * @param predicates Prädikatknoten. */
	protected void getCloneWithObject_DONE(QN context, Consumer<QN> cloneEdgeWithObject, Consumer<QN> cloneNodeWithObject, Iterable<QN> predicates) {
	}

	/** Diese Methode ermittelt zu jedem der gegebenen {@link QE#predicate() Prädikatknoten}, ob beim {@link #getCloneEdges(Map, QN, Iterable) Klonen} von
	 * {@link QE#subject() Subjektknoten} im Rahmen des gegebenen {@link QE#context() Kontextknoten} auch die {QE Hyperkanten} mit diesen Kontext, Prädikat- und
	 * Subjektknoten geklont werden sollen und ob zudem noch deren {@link QE#object() Objektknoten} geklont werden sollen.
	 *
	 * @param context Kontextknoten.
	 * @param cloneEdgeWithSubject Methode zur Markierung eines Prädikatknoten zum Klonen seiner Hyperkanten.
	 * @param cloneNodeWithSubject Methode zur Markierung eines Prädikatknoten zum Klonen seiner Hyperkanten und derer Objektknoen.
	 * @param predicates Prädikatknoten. */
	protected void getCloneWithSubject_DONE(QN context, Consumer<QN> cloneEdgeWithSubject, Consumer<QN> cloneNodeWithSubject, Iterable<QN> predicates) {
	}

}
