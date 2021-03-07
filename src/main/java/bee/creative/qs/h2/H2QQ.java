package bee.creative.qs.h2;

/** Diese Klasse implementiert alle SQL-Anfragen des {@link H2QS} und seiner Bestandteile.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
class H2QQ {

	static String createAll() {
		return "" //
			+ "create table if not exists QN (N int not null, V varchar(1G) not null, primary key (N));" //
			+ "create table if not exists QE (C int not null, P int not null, S int not null, O int not null, primary key (C, P, S, O));" //
			+ "create index if not exists QE_INDEX_CPO on QE (C, P, O, S);" //
			+ "create index if not exists QE_INDEX_CSP on QE (C, S, P, O);" //
			+ "create index if not exists QE_INDEX_COP on QE (C, O, P, S);" //
			+ "create unique index if not exists QN_INDEX_V on QN (V);" //
			+ "create sequence if not exists QN_SEQUENCE minvalue 1 maxvalue 2000000000 nocycle;" //
			+ "create sequence if not exists QET_SEQUENCE minvalue 1 maxvalue 2000000000 cycle;" //
			+ "create sequence if not exists QNT_SEQUENCE minvalue 1 maxvalue 2000000000 cycle;" //
			+ "create sequence if not exists QVT_SEQUENCE minvalue 1 maxvalue 2000000000 cycle;" //
			+ "create sequence if not exists QTT_SEQUENCE minvalue 1 maxvalue 2000000000 cycle"; //
	}

	static String deleteSave() {
		return "delete from QN;delete from QE";
	}

	static String selectAny(final H2QOSet<?, ?> set) {
		return "(select top 1 from " + set.select + ")";
	}

	static String selectSize(final H2QOSet<?, ?> set) {
		return "(select count(*) from " + set.select + ")";
	}

	static String selectUnion(final H2QOSet<?, ?> set1, final H2QOSet<?, ?> set2) {
		return "(" + set1.select + " union " + set2.select + ")";
	}

	static String selectExcept(final H2QOSet<?, ?> set1, final H2QOSet<?, ?> set2) {
		return "(" + set1.select + " except " + set2.select + ")";
	}

	static String selectIntersect(final H2QOSet<?, ?> set1, final H2QOSet<?, ?> set2) {
		return "(" + set1.select + " intersect " + set2.select + ")";
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

	static String selectEdgesHavingNode(final H2QESet edges, final H2QN node) {
		return "(select * from " + edges.select + " where C = " + node.key + " or P = " + node.key + " or S = " + node.key + " or O = " + node.key + ")";
	}

	static String selectEdgesHavingNodes(final H2QESet edges, final H2QNSet node) {
		return "(select * from " + edges.select + " where exists (select N from " + node.select + " where C = N or P = N or S = N or O = N))";
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

	static String selectNodesOrder(final H2QNSet set) {
		return "(select * from " + set.select + " order by N)";
	}

	static String selectValuesOrder(final H2QVSet set) {
		return "(select * from " + set.select + " order by V)";
	}

	static String selectTuplesOrder(final H2QTSet set, final int size) {
		final StringBuilder res = new StringBuilder(set.select.length() + 25 + (size * 5));
		res.append("(select * from ").append(set.select).append(" order by C0");
		for (int i = 1; i < size; i++) {
			res.append(", C").append(i);
		}
		return res.append(")").toString();
	}

	static String insertSaveEdge() {
		return "merge into QE (C, P, S, O) values (?, ?, ?, ?)";
	}

	static String insertSaveEdges(final H2QESet edges) {
		return "merge into QE " + edges.select;
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

	static String selectSaveEdge() {
		return "select top 1 * from QE where C = ? and P = ? and S = ? and O = ?";
	}

	static String selectSaveEdges() {
		return "(select C, P, S, O from QE)";
	}

	static String createSaveNodeKey() {
		return "select next value for QN_SEQUENCE";
	}

	static String insertSaveNode() {
		return "insert into QN (N, V) values (?, ?)";
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

	static String selectSaveNode() {
		return "select N from QN where V = ?";
	}

	static String selectSaveNodes() {
		return "(select N from QN)";
	}

	static String deleteSaveValuesHavingNodes(final H2QNSet nodes) {
		return "delete from QN where N in " + nodes.select;
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

	static String createTempEdges(final int key) {
		return "create cached local temporary table QET" + key + " (C int not null, P int not null, S int not null, O int not null)";
	}

	static String createTempEdgesKey() {
		return "select next value for QET_SEQUENCE";
	}

	static String createTempEdgesIndex(final int key) {
		return "" //
			+ "create index QET" + key + "_INDEX_CPS on QET" + key + " (C, P, S, O);" //
			+ "create index QET" + key + "_INDEX_CPO on QET" + key + " (C, P, O, S);" //
			+ "create index QET" + key + "_INDEX_CSP on QET" + key + " (C, S, P, O);" //
			+ "create index QET" + key + "_INDEX_COP on QET" + key + " (C, O, P, S)";
	}

	static String insertTempEdges(final int key) {
		return "insert into QET" + key + " (C, P, S, O) values (?, ?, ?, ?)";
	}

	static String insertTempEdges(final int key, final int key2) {
		return "insert into QET" + key + " select distinct C, P, S, O from QET" + key2;
	}

	static String insertTempEdges(final int key, final H2QESet edges) {
		return "insert into QET" + key + " select C, P, S, O from " + edges.select;
	}

	static String deleteTempEdges(final int key) {
		return "drop table if exists QET" + key + " cascade";
	}

	static String selectTempEdges(final int key) {
		return "(select C, P, S, O from QET" + key + ")";
	}

	static String createTempNodes(final int key) {
		return "create cached local temporary table QNT" + key + " (N int not null)";
	}

	static String createTempNodesKey() {
		return "select next value for QNT_SEQUENCE";
	}

	static String createTempNodesIndex(final int key) {
		return "create index QNT" + key + "_INDEX_N on QNT" + key + " (N)";
	}

	static String insertTempNodes(final int key) {
		return "insert into QNT" + key + " (N) values (?)";
	}

	static String insertTempNodes(final int key, final int key2) {
		return "insert into QNT" + key + " select distinct N from QNT" + key2;
	}

	static String insertTempNodes(final int key, final H2QNSet nodes) {
		return "insert into QNT" + key + " select N from " + nodes.select;
	}

	static String deleteTempNodes(final int key) {
		return "drop table if exists QNT" + key + " cascade";
	}

	static String selectTempNodes(final int key) {
		return "(select N from QNT" + key + ")";
	}

	static String createTempValues(final int key) {
		return "create cached local temporary table QVT" + key + " (V varchar(1G) not null)";
	}

	static String createTempValuesKey() {
		return "select next value for QVT_SEQUENCE";
	}

	static String createTempValuesIndex(final int key) {
		return "create index QVT" + key + "_INDEX_V on QVT" + key + " (V)";
	}

	static String insertTempValues(final int key) {
		return "insert into QVT" + key + " (V) values (?)";
	}

	static String insertTempValues(final int key, final int key2) {
		return "insert into QVT" + key + " select distinct V from QVT" + key2;
	}

	static String insertTempValues(final int key, final H2QVSet values) {
		return "insert into QVT" + key + " select V from " + values.select;
	}

	static String deleteTempValues(final int key) {
		return "drop table if exists QVT" + key + " cascade";
	}

	static String selectTempValues(final int key) {
		return "(select V from QVT" + key + ")";
	}

	static String createTempTuples(final int key, final int size) {
		final StringBuilder res = new StringBuilder(50 + (size * 18));
		res.append("create cached local temporary table QTT").append(key).append(" (C0 int not null");
		for (int i = 1; i < size; i++) {
			res.append(", C").append(i).append(" int not null");
		}
		return res.append(")").toString();
	}

	public static void main(final String[] args) throws Exception {
		System.out.println(H2QQ.selectTempTuples(123456789, 1).length());
		System.out.println(H2QQ.selectTempTuples(123456789, 2).length());
	}

	static String createTempTuplesKey() {
		return "select next value for QTT_SEQUENCE";
	}

	static String selectTempTuples(final int key, final int size) {
		final StringBuilder res = new StringBuilder(25 + (size * 5));
		res.append("(select *");
//		res.append("(select C0");
//		for (int i = 1; i < size; i++) {
//			res.append(", C").append(i);
//		}
		return res.append(" from QTT").append(key).append(")").toString();
	}

	static String selectTuplesSelect(final H2QTSet set, final int[] roles) {
		final int size = roles.length;
		final StringBuilder res = new StringBuilder(set.select.length() + 25 + (size * 10));
		res.append("(select distinct C").append(roles[0]).append(" C0");
		for (int i = 1; i < size; i++) {
			res.append(", C").append(roles[i]).append(" C").append(i);
		}
		return res.append(" from ").append(set.select).append(")").toString();
	}

	static String deleteTempTuples(final int key) {
		return "drop table if exists QTT" + key + " cascade";
	}

	public static String insertTempTuples(final int key, final H2QTSet set) {
		final int size = set.names.size();
		final StringBuilder res = new StringBuilder(set.select.length() + 30 + (size * 5));
		res.append("insert into QTT").append(key).append(" select C0");
		for (int i = 1; i < size; i++) {
			res.append(", C").append(i);
		}
		return res.append(" from ").append(set.select).toString();
	}

	public static String insertTempTuples(int key, final int size) {
		final StringBuilder res = new StringBuilder(35 + (size * 7));
		res.append("insert into QTT").append(key).append(" (C0");
		for (int i = 1; i < size; i++) {
			res.append(", C").append(i);
		}
		res.append(") values (?");
		for (int i = 1; i < size; i++) {
			res.append(", ?");
		}
		return res.append(")").toString();
	}

	public static String insertTempTuples(int key, int key2, int size) {
		return "insert into QTT" + key + " select distinct * from QTT" + key2;
	}

}
