package bee.creative.qs.h2;

/** Diese Klasse implementiert alle SQL-Anfragen des {@link H2QS}.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class H2QQ {

	/** Diese Methode liefert die Anfrage zur Erstellung aller Tabellen, Indizes und Sequenzen. */
	static String createAll() {
		return "" //
			+ "create table if not exists QN (N int not null, V varchar(1G) not null, primary key (N));" //
			+ "create table if not exists QE (C int not null, P int not null, S int not null, O int not null, primary key (C, P, S, O));" //
			+ "create table if not exists QNT (T int not null, N int not null, primary key (T, N)); "//
			+ "create table if not exists QVT (T int not null, V varchar(1G) not null, primary key (T, V));" //
			+ "create table if not exists QET (T int not null, C int not null, P int not null, S int not null, O int not null, primary key (T, C, P, S, O));"//
			+ "create index if not exists QE_INDEX_CPO on QE (C, P, O, S);" //
			+ "create index if not exists QE_INDEX_CSP on QE (C, S, P, O);" //
			+ "create index if not exists QE_INDEX_COP on QE (C, O, P, S);" //
			+ "create index if not exists QET_INDEX_TP on QET (T, P);" //
			+ "create index if not exists QET_INDEX_TS on QET (T, S);" //
			+ "create index if not exists QET_INDEX_TO on QET (T, O);" //
			+ "create unique index if not exists QN_INDEX_V on QN (V);" //
			+ "create sequence if not exists QN_SEQUENCE_K start with 1 maxvalue 2000000000;" //
			+ "create sequence if not exists QET_SEQUENCE_T start with 1 maxvalue 2000000000;" //
			+ "create sequence if not exists QNT_SEQUENCE_T start with 1 maxvalue 2000000000;" //
			+ "create sequence if not exists QVT_SEQUENCE_T start with 1 maxvalue 2000000000;" //
		;
	}

	/** Diese Methode liefert die Anfrage zum Finden des ersten Elements der gegebenenen Menge. */
	static String selectAny(final H2QXSet<?, ?> set) {
		return "(select top 1 from " + set.select + ")";
	}

	/** Diese Methode liefert die Anfrage zum Zählen der Elemente der gegebenenen Menge. */
	static String selectSize(final H2QXSet<?, ?> set) {
		return "(select count(*) from " + set.select + ")";
	}

	/** Diese Methode liefert die Anfrage zur Ermittlung der Vereinigungsmenge der gegebenenen Mengen. */
	static String selectUnion(final H2QXSet<?, ?> set1, final H2QXSet<?, ?> set2) {
		return "(select * from " + set1.select + " union select * from " + set2.select + ")";
	}

	/** Diese Methode liefert die Anfrage zur Ermittlung der Differenzmenge der gegebenenen Mengen. */
	static String selectExcept(final H2QXSet<?, ?> set1, final H2QXSet<?, ?> set2) {
		return "(select * from " + set1.select + " except select * from " + set2.select + ")";
	}

	/** Diese Methode liefert die Anfrage zur Ermittlung der Schnittmenge der gegebenenen Mengen. */
	static String selectIntersect(final H2QXSet<?, ?> set1, final H2QXSet<?, ?> set2) {
		return "(select * from " + set1.select + " intersect select * from " + set2.select + ")";
	}

	
	
	/** Diese Methode liefert die Anfrage zur Ermittlung der nächsten Knotenkennung. */
	static String selectNextNode() {
		return "select next value for QN_SEQUENCE_K";
	}

	int ________CopyEdges;
	
	/** Diese Methode liefert die Anfrage zur Ermittlung der nächsten Kantentabellenkennung. */
	static String createCopyEdges() {
		return "select next value for QET_SEQUENCE_T";
	}

	
	/** Diese Methode liefert die Anfrage zum Entfernen der temporären Hyperkantenmenge mit der gegebenenen Kennung. */
	static String deleteEdgesCopy(final int key) {
		return "delete from QET whete T = " + key;
	}

	int ________CopyNodes;

	
	public static String insertCopyNodes() {
		return "merge into QNT (T, N) values (?, ?)";
	}

	public static String insertCopyNodes(final int key, final H2QNSet nodes) {
		return "merge into QNT select " + key + " T, N from " + nodes.select;
	}

	public static String selectCopyNodes(final int key) {
		return "(select N from QNT where T = " + key + ")";
	}

	public static String deleteCopyNodes(final int key) {
		return "delete from QNT whete T = " + key;
	}

	/** Diese Methode liefert die Anfrage zur Ermittlung der nächsten Knotentabellenkennung. */
	static String createCopyNodes() {
		return "select next value for QNT_SEQUENCE_T";
	}

	int ________D;

	
	public static String insertCopyValues() {
		return "merge into QVT (T, V) values (?, ?)";
	}

	public static String insertCopyValues(final int key, final H2QVSet values) {
		return "merge into QVT select " + key + " T, V from " + values.select;
	}

	public static String selectCopyValues(final int key) {
		return "(select V from QVT where T = " + key + ")";
	}

	/** Diese Methode liefert die Anfrage zum Entfernen der temporären Textwertmenge mit der gegebenenen Kennung. */
	static String deleteCopyValues(final int key) {
		return "delete from QVT whete T = " + key;
	}

	/** Diese Methode liefert die Anfrage zur Ermittlung der nächsten Werttabellenkennung. */
	static String createCopyValues() {
		return "select next value for QVT_SEQUENCE_T";
	}

	int ________W;

	public static String selectEdgeValue() {
		return "select top 1 * from QE where C = ? and P = ? and S = ? and O = ?";
	}

	public static String selectEdgesSave() {
		return "(select C, P, S, O from QE)";
	}

	public static String selectEdgesCopy(final int key) {
		return "(select C, P, S, O from QET where T = " + key + ")";
	}

	public static String selectEdgesOrder(final H2QESet edges) {
		return "(select * from " + edges.select + " order by C, P, S, O)";
	}

	public static String selectEdgesContexts(final H2QESet edges) {
		return "(select distinct C N from " + edges.select + ")";
	}

	public static String selectEdgesPredicates(final H2QESet edges) {
		return "(select distinct P N from " + edges.select + ")";
	}

	public static String selectEdgesSubjects(final H2QESet edges) {
		return "(select distinct S N from " + edges.select + ")";
	}

	public static String selectEdgesObjects(final H2QESet edges) {
		return "(select distinct O N from " + edges.select + ")";
	}

	public static String selectEdgesWithContext(final H2QESet edges, final H2QN node) {
		return "(select distinct " + node.key + " C, P, S, O from " + edges.select + ")";
	}

	public static String selectEdgesWithContexts(final H2QESet edges, final H2QNSet nodes) {
		return "(select distinct B.N C, A.P, A.S, A.O from " + edges.select + " A, " + nodes.select + " B)";
	}

	public static String selectEdgesWithPredicate(final H2QESet edges, final H2QN node) {
		return "(select distinct C, " + node.key + " P, S, O from " + edges.select + ")";
	}

	public static String selectEdgesWithPredicates(final H2QESet edges, final H2QNSet nodes) {
		return "(select distinct A.C, B.N P, A.S, A.O from " + edges.select + " A, " + nodes.select + " B)";
	}

	public static String selectEdgesWithSubject(final H2QESet edges, final H2QN node) {
		return "(select distinct C, P, " + node.key + " S, O from " + edges.select + ")";
	}

	public static String selectEdgesWithSubjects(final H2QESet edges, final H2QNSet nodes) {
		return "(select distinct A.C, A.P, B.N S, A.O from " + edges.select + " A, " + nodes.select + " B)";
	}

	public static String selectEdgesWithObject(final H2QESet edges, final H2QN node) {
		return "(select distinct C, P, S, " + node.key + " O from " + edges.select + ")";
	}

	public static String selectEdgesWithObjects(final H2QESet edges, final H2QNSet nodes) {
		return "(select distinct A.C, A.P, A.S, B.N O from " + edges.select + " A, " + nodes.select + " B)";
	}

	public static String selectEdgesHavingContext(final H2QESet edges, final H2QN node) {
		return "(select * from " + edges.select + " where C = " + node.key + ")";
	}

	public static String selectEdgesHavingContexts(final H2QESet edges, final H2QNSet node) {
		return "(select * from " + edges.select + " where C in " + node.select + ")";
	}

	public static String selectEdgesHavingPredicate(final H2QESet edges, final H2QN node) {
		return "(select * from " + edges.select + " where P = " + node.key + ")";
	}

	public static String selectEdgesHavingPredicates(final H2QESet set, final H2QNSet node) {
		return "(select * from " + set.select + " where P in " + node.select + ")";
	}

	public static String selectEdgesHavingSubject(final H2QESet set, final H2QN node) {
		return "(select * from " + set.select + " where S = " + node.key + ")";
	}

	public static String selectEdgesHavingSubjects(final H2QESet set, final H2QNSet node) {
		return "(select * from " + set.select + " where S in " + node.select + ")";
	}

	public static String selectEdgesHavingObject(final H2QESet set, final H2QN node) {
		return "(select * from " + set.select + " where O = " + node.key + ")";
	}

	public static String selectEdgesHavingObjects(final H2QESet set, final H2QNSet node) {
		return "(select * from " + set.select + " where O in " + node.select + ")";
	}

	public static String insertNode() {
		return "insert into QN (N, V) values (?, ?)";
	}

	public static String selectNode() {
		return "select N from QN where V = ?";
	}

	public static String selectNodeValue() {
		return "select V from QN where N = ?";
	}

	public static String selectNodesSave() {
		return "(select N from QN)";
	}

	public static String selectNodesOrder(final H2QNSet set) {
		return "(select * from " + set.select + " order by N)";
	}

	public static String selectNodesValues(final H2QNSet set) {
		return "(select V from QN A inner join " + set.select + " B on A.N = B.N)";
	}

	public static String deleteValuesHavingNodes(final H2QNSet nodes) {
		return "delete from QN where N in " + nodes.select;
	}

	public static String deleteNodeSave() {
		return "delete from QN where N = ?";
	}

	/** Diese Methode liefert die Anfrage zum Entfernen aller Hyperknoten mit den gegebenenen Textwerten. */
	static String deleteNodesHavingValues(final H2QVSet values) {
		return "delete from QN where V in " + values.select;
	}

	public static String selectValuesSave() {
		return "(select V from QN)";
	}

	public static String selectValuesOrder(final H2QVSet set) {
		return "(select * from " + set.select + " order by V)";
	}

	public static String selectValuesNodes(final H2QVSet set) {
		return "(select N from QN A inner join " + set.select + " B on A.V = B.V)";
	}

	public static String insertEdgeSave() {
		return "merge into QE (C, P, S, O) values (?, ?, ?, ?)";
	}

	public static String insertEdges(final int key) {
		return "merge into QET (T, C, P, S, O) values (" + key + ", ?, ?, ?, ?)";
	}

	public static String insertEdges(final int key, final H2QESet edges) {
		return "merge into QET select " + key + " T, C, P, S, O from " + edges.select;
	}

	public static String insertEdges(final H2QESet edges) {
		return "merge into QE " + edges.select;
	}

	public static String deleteEdgeItems(final H2QESet set) {
		return "delete from QE A where exists (select 0 from " + set.select + " B where A.C = B.C and A.P = B.P and A.S = B.S and A.O = B.O)";
	}

	/** Diese Methode liefert die parametrisierte Anfrage zum Entfernen aller Hyperkanten mit dem gegebenen Kontext-, Prädikat-, Subjekt bzw. Objektknoten. */
	static String deleteEdgesHavingNode() {
		return "delete from QE where C = ?1 or P = ?1 or S = ?1 or O = ?1";
	}

	/** Diese Methode liefert die Anfrage zum Entfernen aller Textwerte, Hyperknoten und Hyperkanten. */
	static String deleteAll() {
		return "delete from QN;delete from QE;" + H2QQ.deleteCopy();
	}

	/** Diese Methode liefert die Anfrage zum Entfernen aller temporären Textwerte, Hyperknoten und Hyperkanten. */
	static String deleteCopy() {
		return "delete from QVT;delete from QNT;delete from QET;";
	}

	/** Diese Methode liefert die parametrisierte Anfrage zum Entfernen der Hyperkante mit den gegebenen Kontext-, Prädikat-, Subjekt und Objektknoten. */
	static String deleteEdgeSave() {
		return "delete from QE where C = ? and P = ? and S = ? and O = ?";
	}

	/** Diese Methode liefert die Anfrage zum Entfernen aller {@link H2QE Hyperkanten} mit den gegebenenen {@link H2QE#context() Kontextknoten}. */
	static String deleteEdgesHavingContexts(final H2QNSet nodes) {
		return "delete from QE where C in " + nodes.select;
	}

	/** Diese Methode liefert die Anfrage zum Entfernen aller {@link H2QE Hyperkanten} mit den gegebenenen {@link H2QE#predicate() Prädikatknoten}. */
	static String deleteEdgesHavingPredicates(final H2QNSet nodes) {
		return "delete from QE where P in " + nodes.select;
	}

	/** Diese Methode liefert die Anfrage zum Entfernen aller {@link H2QE Hyperkanten} mit den gegebenenen {@link H2QE#subject() Subjektknoten}. */
	static String deleteEdgesHavingSubjects(final H2QNSet nodes) {
		return "delete from QE where S in " + nodes.select;
	}

	/** Diese Methode liefert die Anfrage zum Entfernen aller {@link H2QE Hyperkanten} mit den gegebenenen {@link H2QE#object() Objektknoten}. */
	static String deleteEdgesHavingObjects(final H2QNSet nodes) {
		return "delete from QE where O in " + nodes.select;
	}

}
