package bee.creative.qs.h2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import bee.creative.qs.QE;
import bee.creative.qs.QN;
import bee.creative.qs.QS;

public final class H2QS implements QS {

	static String selectTop(final H2QXSet<?, ?> set) {
		return "(select top 1 from " + set.select + ")";
	}

	static String selectCount(final H2QXSet<?, ?> set) {
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

	static String selectEdge() {
		return "select top 1 * from QE where C = ? and P = ? and S = ? and O = ?";
	}

	static String selectEdges() {
		return "(select C, P, S, O from QE)";
	}

	static String selectEdgesCopy(final int key) {
		return "(select C, P, S, O from TQE where T = " + key + ")";
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

	static String selectNode() {
		return "select N from QN where V = ?";
	}

	static String selectNodes() {
		return "(select N from QN)";
	}

	static String selectNodesCopy(final int key) {
		return "(select N from TQN where T = " + key + ")";
	}

	static String selectNodesOrder(final H2QNSet set) {
		return "(select * from " + set.select + " order by N)";
	}

	static String selectNodesValues(final H2QNSet set) {
		return "(select V from QN A inner join " + set.select + " B on A.N = B.N)";
	}

	static String selectValue() {
		return "select V from QN where N = ?";
	}

	static String selectValues() {
		return "(select V from QN)";
	}

	static String selectValuesCopy(final int key) {
		return "(select V from TQV where T = " + key + ")";
	}

	static String selectValuesOrder(final H2QVSet set) {
		return "(select * from " + set.select + " order by V)";
	}

	static String selectValuesNodes(final H2QVSet set) {
		return "(select N from QN A inner join " + set.select + " B on A.V = B.V)";
	}

	static String insertEdge() {
		return "merge into QE (C, P, S, O) values (?, ?, ?, ?)";
	}

	static String insertEdges(final int key) {
		return "merge into TQE (T, C, P, S, O) values (" + key + ", ?, ?, ?, ?)";
	}

	static String insertEdges(final int key, final H2QESet edges) {
		return "merge into TQE select " + key + " T, C, P, S, O from " + edges.select;
	}

	static String insertEdges(final H2QESet edges) {
		return "merge into QE " + edges.select;
	}

	static String insertNode() {
		return "insert into QN (N, V) values (?, ?)";
	}

	static String insertNodes(final int key) {
		return "merge into TQN (T, N) values (" + key + ", ?)";
	}

	static String insertNodes(final int key, final H2QNSet nodes) {
		return "merge into TQN select " + key + " T, N from " + nodes.select;
	}

	static String insertValues(final int key) {
		return "merge into TQV values (" + key + ", ?)";
	}

	static String insertValues(final int key, final H2QVSet values) {
		return "merge into TQV select " + key + " T, V from " + values.select;
	}

	static String deleteCopy() {
		return "delete from TQE; delete from TQN; delete from TQV";
	}

	static String deleteEdge() {
		return "delete from QE where C = ? and P = ? and S = ? and O = ?";
	}

	static String deleteEdges(final int key) {
		return "delete from TQE whete T = " + key;
	}

	static String deleteEdgeItems(final H2QESet set) {
		return "delete from QE A where exists (select 0 from " + set.select + " B where A.C = B.C and A.P = B.P and A.S = B.S and A.O = B.O)";
	}

	static String deleteNode() {
		return "delete from QE where C = ?1 or P = ?1 or S = ?1 or O = ?1";
	}

	static String deleteNodes(final int key) {
		return "delete from TQN whete T = " + key;
	}

	static String deleteNodes(final H2QNSet nodes) {
		return "delete from QN where N in " + nodes.select;
	}

	static String deleteValue() {
		return "delete from QN where N = ?";
	}

	static String deleteValues(final int key) {
		return "delete from TQV whete T = " + key;
	}

	static String deleteValues(final H2QVSet values) {
		return "delete from QN where V in " + values.select;
	}

	static String deleteContexts(final H2QNSet nodes) {
		return "delete from QE where C in " + nodes.select;
	}

	static String deletePredicates(final H2QNSet nodes) {
		return "delete from QE where P in " + nodes.select;
	}

	static String deleteSubjects(final H2QNSet nodes) {
		return "delete from QE where S in " + nodes.select;
	}

	static String deleteObjects(final H2QNSet nodes) {
		return "delete from QE where O in " + nodes.select;
	}

	/** Dieses Feld speichert die Datenbankverbindung. */
	final Connection conn;

	/** Dieses Feld speichert die nächste Knotenkennung. */
	int nextNodeKey = 1;

	/** Dieses Feld speichert die nächste Kantentabellenkennung. */
	int nextEdgesKey = 1;

	/** Dieses Feld speichert die nächste Knotentabellenkennung. */
	int nextNodesKey = 1;

	/** Dieses Feld speichert die nächste Werttabellenkennung. */
	int nextValuesKey = 1;

	PreparedStatement selectEdge;

	PreparedStatement selectNode;

	PreparedStatement selectValue;

	PreparedStatement insertEdge;

	PreparedStatement insertNode;

	PreparedStatement deleteEdge;

	PreparedStatement deleteNode;

	PreparedStatement deleteValue;

	public H2QS(final Connection conn, final boolean setup) throws SQLException {
		this.conn = conn;
		if (setup) {
			this.setupTableImpl();
		}
		this.setupQueryImpl();
	}

	@Override
	protected void finalize() throws Throwable {
		this.closeQueryImpl();
	}

	@Override
	public H2QESet edges() {
		return new H2QESet.Save(this);
	}

	@Override
	public H2QNSet nodes() {
		return new H2QNSet.Save(this);
	}

	@Override
	public H2QVSet values() {
		return new H2QVSet.Save(this);
	}

	@Override
	public synchronized H2QE newEdge() {
		final int key = this.nextNodeKey++;
		return new H2QE(this, key, key, key, key);
	}

	@Override
	public H2QE newEdge(final QN node) throws NullPointerException, IllegalArgumentException {
		final int key = this.asQN(node).key;
		return new H2QE(this, key, key, key, key);
	}

	@Override
	public H2QE newEdge(final QN context, final QN predicate, final QN subject, final QN object) throws NullPointerException, IllegalArgumentException {
		return new H2QE(this, this.asQN(context).key, this.asQN(predicate).key, this.asQN(subject).key, this.asQN(object).key);
	}

	@Override
	public H2QESet newEdges() {
		return this.newEdges(this.newEdge());
	}

	@Override
	public H2QESet newEdges(final QE... edges) throws NullPointerException, IllegalArgumentException {
		return this.newEdges(Arrays.asList(edges));
	}

	@Override
	public synchronized H2QESet newEdges(final Iterable<? extends QE> edges) throws NullPointerException, IllegalArgumentException {
		try {
			final int key = this.nextEdgesKey;
			if (edges instanceof H2QESet) {
				final H2QESet set = this.asQESet(edges);
				this.updateImpl(H2QS.insertEdges(key, set));
				this.nextEdgesKey = key + 1;
				return new H2QESet.Copy(this, key);
			}
			try (final PreparedStatement stmt = this.conn.prepareStatement(H2QS.insertEdges(key))) {
				for (final Object item: edges) {
					final H2QE edge = this.asQE(item);
					stmt.setInt(1, edge.context);
					stmt.setInt(2, edge.predicate);
					stmt.setInt(3, edge.subject);
					stmt.setInt(4, edge.object);
					stmt.addBatch();
				}
				stmt.executeBatch();
			}
			this.nextEdgesKey = key + 1;
			return new H2QESet.Copy(this, key);
		} catch (final SQLException cause) {
			throw new IllegalStateException(cause);
		}
	}

	@Override
	public synchronized H2QN newNode() {
		return new H2QN(this, this.nextNodeKey++);
	}

	@Override
	public synchronized H2QN newNode(final String value) {
		try {
			final PreparedStatement stmt = this.selectNode;
			stmt.setString(1, this.asQV(value));
			final ResultSet r = stmt.executeQuery();
			if (r.next()) return new H2QN(this, r.getInt(1));
			final PreparedStatement stmt2 = this.insertNode;
			final int key = this.nextNodeKey;
			stmt2.setInt(1, key);
			stmt2.setString(2, this.asQV(value));
			stmt2.executeUpdate();
			this.nextNodeKey = key + 1;
			return new H2QN(this, key);
		} catch (final SQLException cause) {
			throw new IllegalStateException(cause);
		}
	}

	@Override
	public H2QNSet newNodes(final QN... nodes) throws NullPointerException, IllegalArgumentException {
		return this.newNodes(Arrays.asList(nodes));
	}

	@Override
	public synchronized H2QNSet newNodes(final Iterable<? extends QN> nodes) throws NullPointerException, IllegalArgumentException {
		try {
			final int key = this.nextNodesKey;
			if (nodes instanceof H2QNSet) {
				final H2QNSet set = this.asQNSet(nodes);
				this.updateImpl(H2QS.insertNodes(key, set));
				this.nextNodesKey = key + 1;
				return new H2QNSet.Copy(this, key);
			}
			try (final PreparedStatement stmt = this.conn.prepareStatement(H2QS.insertNodes(key))) {
				for (final Object item: nodes) {
					stmt.setInt(1, this.asQN(item).key);
					stmt.addBatch();
				}
				stmt.executeBatch();
			}
			this.nextNodesKey = key + 1;
			return new H2QNSet.Copy(this, key);
		} catch (final SQLException cause) {
			throw new IllegalStateException(cause);
		}
	}

	@Override
	public H2QVSet newValues(final String... values) throws NullPointerException, IllegalArgumentException {
		return this.newValues(Arrays.asList(values));
	}

	@Override
	public synchronized H2QVSet newValues(final Iterable<? extends String> values) throws NullPointerException, IllegalArgumentException {
		try {
			final int key = this.nextValuesKey;
			if (values instanceof H2QVSet) {
				final H2QVSet set = (H2QVSet)values;
				if (set.owner == this) {
					this.updateImpl(H2QS.insertValues(key, set));
					this.nextValuesKey = key + 1;
					return new H2QVSet.Copy(this, key);
				}
			}
			try (final PreparedStatement stmt = this.conn.prepareStatement(H2QS.insertValues(key))) {
				for (final Object item: values) {
					stmt.setString(1, this.asQV(item));
					stmt.addBatch();
				}
				stmt.executeBatch();
			}
			this.nextValuesKey = key + 1;
			return new H2QVSet.Copy(this, key);
		} catch (final SQLException cause) {
			throw new IllegalStateException(cause);
		}
	}

	/** Diese Methode erstellt alle Tabellen des Graphspeichers und leert ihn dabei. */
	public void setup() throws SQLException {
		this.closeQueryImpl();
		this.setupTableImpl();
		this.setupQueryImpl();
	}

	// entfernt alle QN-String, die nicht in QE vorkommen
	/** Diese Methode entfernt alle Hyperknoten mit Textwert, die nich in Hyperkanten verwendet werden. */
	public void compact() {
		// TODO scnell
		this.nodes().except(this.edges().nodes()).popAll();
	}

	/** Diese Methode entfernt alle temporären Objekte aus dem Graphspeicher. */
	public void cleanup() {
		this.updateImpl(H2QS.deleteCopy());
	}

	private void closeQueryImpl() throws SQLException {
		this.selectEdge.close();
		this.selectNode.close();
		this.selectValue.close();
		this.insertEdge.close();
		this.insertNode.close();
		this.deleteEdge.close();
		this.deleteNode.close();
		this.deleteValue.close();
	}

	private void setupQueryImpl() throws SQLException {
		this.selectEdge = this.conn.prepareStatement(H2QS.selectEdge());
		this.selectNode = this.conn.prepareStatement(H2QS.selectNode());
		this.selectValue = this.conn.prepareStatement(H2QS.selectValue());
		this.insertEdge = this.conn.prepareStatement(H2QS.insertEdge());
		this.insertNode = this.conn.prepareStatement(H2QS.insertNode());
		this.deleteEdge = this.conn.prepareStatement(H2QS.deleteEdge());
		this.deleteNode = this.conn.prepareStatement(H2QS.deleteNode());
		this.deleteValue = this.conn.prepareStatement(H2QS.deleteValue());
	}

	private void setupTableImpl() throws SQLException {
		try (Statement stmt = this.conn.createStatement()) {
			stmt.execute("" //
				+ "drop table if exists QN, QE, TQV, TQN, TQE; " //
				+ "create table QN (N int not null, V varchar(1G) not null);" //
				+ "create primary key QN_INDEX_N on QN (N); " //
				+ "create unique index QN_INDEX_V on QN (V); " //
				+ "create table QE (C int not null, P int not null, S int not null, O int not null); " //
				+ "create primary key QE_INDEX_CPSO on QE (C, P, S, O); " //
				+ "create index QE_INDEX_CPO on QE (C, P, O, S); " //
				+ "create index QE_INDEX_CSP on QE (C, S, P, O); " //
				+ "create index QE_INDEX_COP on QE (C, O, P, S); " //
				+ "create table TQV (T int not null, V varchar(1G) not null); " //
				+ "create primary key TQV_INDEX_TV on TQV (T, V); " //
				+ "create table TQN (T int not null, N int not null); " //
				+ "create primary key TQN_INDEX_TN on TQN (T, N); " //
				+ "create table TQE (T int not null, C int not null, P int not null, S int not null, O int not null); "//
				+ "create primary key TQE_INDEX_TCPSO on TQE (T, C, P, S, O); " //
				+ "create index TQE_INDEX_CPO on TQE (T, P); " //
				+ "create index TQE_INDEX_CSP on TQE (T, S); " //
				+ "create index TQE_INDEX_COP on TQE (T, O)");
		}
	}

	H2QE asQE(final Object source) {
		try {
			final H2QE result = (H2QE)source;
			if (result.owner == this) return result;
		} catch (final ClassCastException cause) {}
		throw new IllegalArgumentException();
	}

	H2QESet asQESet(final Object source) {
		try {
			final H2QESet result = (H2QESet)source;
			if (result.owner == this) return result;
		} catch (final ClassCastException cause) {}
		throw new IllegalArgumentException();
	}

	H2QN asQN(final Object source) {
		try {
			final H2QN result = (H2QN)source;
			if (result.owner == this) return result;
		} catch (final ClassCastException cause) {}
		throw new IllegalArgumentException();
	}

	H2QNSet asQNSet(final Object source) {
		try {
			final H2QNSet result = (H2QNSet)source;
			if (result.owner == this) return result;
		} catch (final ClassCastException cause) {}
		throw new IllegalArgumentException();
	}

	String asQV(final Object source) {
		return source.toString();
	}

	H2QVSet asQVSet(final Object source) {
		try {
			final H2QVSet result = (H2QVSet)source;
			if (result.owner == this) return result;
		} catch (final ClassCastException cause) {}
		throw new IllegalArgumentException();
	}

	boolean updateImpl(final String update) {
		try (final Statement stmt = this.conn.createStatement()) {
			return stmt.executeUpdate(update) != 0;
		} catch (final SQLException cause) {
			throw new IllegalStateException(cause);
		}
	}

	boolean putImpl(final H2QE edge) {
		try {
			final PreparedStatement stmt = this.insertEdge;
			stmt.setInt(1, edge.context);
			stmt.setInt(2, edge.predicate);
			stmt.setInt(3, edge.subject);
			stmt.setInt(4, edge.object);
			return stmt.executeUpdate() != 0;
		} catch (final SQLException cause) {
			throw new IllegalStateException(cause);
		}
	}

	boolean popImpl(final H2QE edge) {
		try {
			final PreparedStatement stmt = this.deleteEdge;
			stmt.setInt(1, edge.context);
			stmt.setInt(2, edge.predicate);
			stmt.setInt(3, edge.subject);
			stmt.setInt(4, edge.object);
			return stmt.executeUpdate() != 0;
		} catch (final SQLException cause) {
			throw new IllegalStateException(cause);
		}
	}

	boolean popImpl(final H2QN node) {
		try {
			final PreparedStatement stmt1 = this.deleteNode, stmt2 = this.deleteValue;
			stmt1.setInt(1, node.key);
			stmt2.setInt(1, node.key);
			return (stmt1.executeUpdate() | stmt2.executeUpdate()) != 0;
		} catch (final SQLException cause) {
			throw new IllegalStateException(cause);
		}
	}

	boolean stateImpl(final H2QE edge) {
		try {
			final PreparedStatement stmt = this.selectEdge;
			stmt.setInt(1, edge.context);
			stmt.setInt(2, edge.predicate);
			stmt.setInt(3, edge.subject);
			stmt.setInt(4, edge.object);
			try (final ResultSet rset = stmt.executeQuery()) {
				return rset.next();
			}
		} catch (final SQLException cause) {
			throw new IllegalStateException(cause);
		}
	}

	boolean stateImpl(final H2QN node) {
		return this.valueImpl(node) != null;
	}

	String valueImpl(final H2QN node) {
		try {
			final PreparedStatement stmt = this.selectValue;
			stmt.setInt(1, node.key);
			try (final ResultSet rset = stmt.executeQuery()) {
				return rset.next() ? rset.getString(1) : null;
			}
		} catch (final SQLException cause) {
			throw new IllegalStateException(cause);
		}
	}

	boolean hasImpl(final H2QXSet<?, ?> set) {
		try (final Statement stmt = this.conn.createStatement(); final ResultSet rset = stmt.executeQuery(H2QS.selectTop(set))) {
			return rset.next();
		} catch (final SQLException cause) {
			throw new IllegalStateException(cause);
		}
	}

	long sizeImpl(final H2QXSet<?, ?> set) {
		try (final Statement stmt = this.conn.createStatement(); final ResultSet rset = stmt.executeQuery(H2QS.selectCount(set))) {
			return rset.next() ? rset.getLong(1) : 0;
		} catch (final SQLException cause) {
			throw new IllegalStateException(cause);
		}
	}

	boolean putAllImpl(final H2QESet edges) {
		return this.updateImpl(H2QS.insertEdges(edges));
	}

	boolean popAllImpl(final H2QESet edges) {
		return this.updateImpl(H2QS.deleteEdgeItems(edges));
	}

	boolean popAllImpl(final H2QNSet nodes) {
		return this.updateImpl(H2QS.deleteNodes(nodes)) | this.updateImpl(H2QS.deleteContexts(nodes)) | this.updateImpl(H2QS.deletePredicates(nodes))
			| this.updateImpl(H2QS.deleteSubjects(nodes)) | this.updateImpl(H2QS.deleteObjects(nodes));
	}

	boolean popAllImpl(final H2QVSet values) {
		return this.updateImpl(H2QS.deleteValues(values));
	}

}
