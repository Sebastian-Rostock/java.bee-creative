package bee.creative.qs.h2;

/** Diese Klasse implementiert alle SQL-Anfragen des {@link H2QS} und seiner Bestandteile.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
class H2QQ {

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

	static String deleteSave() {
		return "delete from QN;delete from QE;" + H2QQ.deleteTemp();
	}

	static String deleteTemp() {
		return "delete from QVT;delete from QNT;delete from QET;";
	}

	static String selectAny(final H2QXSet<?, ?> set) {
		return "(select top 1 from " + set.select + ")";
	}

	static String selectSize(final H2QXSet<?, ?> set) {
		return "(select count(*) from " + set.select + ")";
	}

	static String selectUnion(final H2QXSet<?, ?> set1, final H2QXSet<?, ?> set2) {
		return "(select * from " + set1.select + " union select * from " + set2.select + ")";
	}

	static String selectExcept(final H2QXSet<?, ?> set1, final H2QXSet<?, ?> set2) {
		return "(select * from " + set1.select + " except select * from " + set2.select + ")";
	}

	static String selectIntersect(final H2QXSet<?, ?> set1, final H2QXSet<?, ?> set2) {
		return "(select * from " + set1.select + " intersect select * from " + set2.select + ")";
	}

	static String selectEdgesOrder(final H2QESet edges) {
		return "(select * from " + edges.select + " order by C, P, S, O)";
	}

	static String selectEdgesContexts(final H2QESet edges) {
		return "(select distinct C N from " + edges.select + ")";
	}

	static String selectEdgesPredicates(final H2QESet edges) {
		return "(select distinct P N from " + edges.select + ")";
	}

	static String selectEdgesSubjects(final H2QESet edges) {
		return "(select distinct S N from " + edges.select + ")";
	}

	static String selectEdgesObjects(final H2QESet edges) {
		return "(select distinct O N from " + edges.select + ")";
	}

	static String selectEdgesWithContext(final H2QESet edges, final H2QN node) {
		return "(select distinct " + node.key + " C, P, S, O from " + edges.select + ")";
	}

	static String selectEdgesWithContexts(final H2QESet edges, final H2QNSet nodes) {
		return "(select distinct B.N C, A.P, A.S, A.O from " + edges.select + " A, " + nodes.select + " B)";
	}

	static String selectEdgesWithPredicate(final H2QESet edges, final H2QN node) {
		return "(select distinct C, " + node.key + " P, S, O from " + edges.select + ")";
	}

	static String selectEdgesWithPredicates(final H2QESet edges, final H2QNSet nodes) {
		return "(select distinct A.C, B.N P, A.S, A.O from " + edges.select + " A, " + nodes.select + " B)";
	}

	static String selectEdgesWithSubject(final H2QESet edges, final H2QN node) {
		return "(select distinct C, P, " + node.key + " S, O from " + edges.select + ")";
	}

	static String selectEdgesWithSubjects(final H2QESet edges, final H2QNSet nodes) {
		return "(select distinct A.C, A.P, B.N S, A.O from " + edges.select + " A, " + nodes.select + " B)";
	}

	static String selectEdgesWithObject(final H2QESet edges, final H2QN node) {
		return "(select distinct C, P, S, " + node.key + " O from " + edges.select + ")";
	}

	static String selectEdgesWithObjects(final H2QESet edges, final H2QNSet nodes) {
		return "(select distinct A.C, A.P, A.S, B.N O from " + edges.select + " A, " + nodes.select + " B)";
	}

	static String selectEdgesHavingContext(final H2QESet edges, final H2QN node) {
		return "(select * from " + edges.select + " where C = " + node.key + ")";
	}

	static String selectEdgesHavingContexts(final H2QESet edges, final H2QNSet node) {
		return "(select * from " + edges.select + " where C in " + node.select + ")";
	}

	static String selectEdgesHavingPredicate(final H2QESet edges, final H2QN node) {
		return "(select * from " + edges.select + " where P = " + node.key + ")";
	}

	static String selectEdgesHavingPredicates(final H2QESet set, final H2QNSet node) {
		return "(select * from " + set.select + " where P in " + node.select + ")";
	}

	static String selectEdgesHavingSubject(final H2QESet set, final H2QN node) {
		return "(select * from " + set.select + " where S = " + node.key + ")";
	}

	static String selectEdgesHavingSubjects(final H2QESet set, final H2QNSet node) {
		return "(select * from " + set.select + " where S in " + node.select + ")";
	}

	static String selectEdgesHavingObject(final H2QESet set, final H2QN node) {
		return "(select * from " + set.select + " where O = " + node.key + ")";
	}

	static String selectEdgesHavingObjects(final H2QESet set, final H2QNSet node) {
		return "(select * from " + set.select + " where O in " + node.select + ")";
	}

	static String insertSaveEdge() {
		return "merge into QE (C, P, S, O) values (?, ?, ?, ?)";
	}

	static String insertSaveEdges(final H2QESet edges) {
		return "merge into QE " + edges.select;
	}

	static String selectSaveEdge() {
		return "select top 1 * from QE where C = ? and P = ? and S = ? and O = ?";
	}

	static String selectSaveEdges() {
		return "(select C, P, S, O from QE)";
	}

	static String deleteSaveEdge() {
		return "delete from QE where C = ? and P = ? and S = ? and O = ?";
	}

	static String deleteSaveEdges() {
		return "delete from QE where C = ?1 or P = ?1 or S = ?1 or O = ?1";
	}

	static String deleteSaveEdges(final H2QESet set) {
		return "delete from QE A where exists (select 0 from " + set.select + " B where A.C = B.C and A.P = B.P and A.S = B.S and A.O = B.O)";
	}

	static String deleteSaveEdgesHavingContexts(final H2QNSet nodes) {
		return "delete from QE where C in " + nodes.select;
	}

	static String deleteSaveEdgesHavingPredicates(final H2QNSet nodes) {
		return "delete from QE where P in " + nodes.select;
	}

	static String deleteSaveEdgesHavingSubjects(final H2QNSet nodes) {
		return "delete from QE where S in " + nodes.select;
	}

	static String deleteSaveEdgesHavingObjects(final H2QNSet nodes) {
		return "delete from QE where O in " + nodes.select;
	}

	static String insertTempEdges() {
		return "merge into QET (T, C, P, S, O) values (?, ?, ?, ?, ?)";
	}

	static String insertTempEdges(final int key, final H2QESet edges) {
		return "merge into QET select " + key + " T, C, P, S, O from " + edges.select;
	}

	static String selectTempEdges(final int key) {
		return "(select C, P, S, O from QET where T = " + key + ")";
	}

	static String deleteTempEdges(final int key) {
		return "delete from QET whete T = " + key;
	}

	static String createTempEdges() {
		return "select next value for QET_SEQUENCE_T";
	}

	static String selectNodesOrder(final H2QNSet set) {
		return "(select * from " + set.select + " order by N)";
	}

	static String insertSaveNode() {
		return "insert into QN (N, V) values (?, ?)";
	}

	static String selectSaveNode() {
		return "select N from QN where V = ?";
	}

	static String selectSaveNodes() {
		return "(select N from QN)";
	}

	static String selectSaveNodesHavingValues(final H2QVSet set) {
		return "(select N from QN A inner join " + set.select + " B on A.V = B.V)";
	}

	static String deleteSaveNode() {
		return "delete from QN where N = ?";
	}

	static String deleteSaveNodesHavingValues(final H2QVSet values) {
		return "delete from QN where V in " + values.select;
	}

	static String createSaveNode() {
		return "select next value for QN_SEQUENCE_K";
	}

	static String insertTempNodes() {
		return "merge into QNT (T, N) values (?, ?)";
	}

	static String insertTempNodes(final int key, final H2QNSet nodes) {
		return "merge into QNT select " + key + " T, N from " + nodes.select;
	}

	static String selectTempNodes(final int key) {
		return "(select N from QNT where T = " + key + ")";
	}

	static String deleteTempNodes(final int key) {
		return "delete from QNT whete T = " + key;
	}

	static String createTempNodes() {
		return "select next value for QNT_SEQUENCE_T";
	}

	static String selectValuesOrder(final H2QVSet set) {
		return "(select * from " + set.select + " order by V)";
	}

	static String selectSaveValue() {
		return "select V from QN where N = ?";
	}

	static String selectSaveValues() {
		return "(select V from QN)";
	}

	static String selectSaveValuesHavingNodes(final H2QNSet set) {
		return "(select V from QN A inner join " + set.select + " B on A.N = B.N)";
	}

	static String deleteSaveValuesHavingNodes(final H2QNSet nodes) {
		return "delete from QN where N in " + nodes.select;
	}

	static String insertTempValues() {
		return "merge into QVT (T, V) values (?, ?)";
	}

	static String insertTempValues(final int key, final H2QVSet values) {
		return "merge into QVT select " + key + " T, V from " + values.select;
	}

	static String selectTempValues(final int key) {
		return "(select V from QVT where T = " + key + ")";
	}

	static String deleteTempValues(final int key) {
		return "delete from QVT whete T = " + key;
	}

	static String createTempValues() {
		return "select next value for QVT_SEQUENCE_T";
	}

}
