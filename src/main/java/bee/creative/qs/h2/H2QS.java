package bee.creative.qs.h2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import bee.creative.lang.Objects;
import bee.creative.qs.QE;
import bee.creative.qs.QN;
import bee.creative.qs.QS;

/** Diese Klasse implementiert einen {@link QS Graphspeicher}, dessen Hyperkanten und Textwerte in einer Datenbank (vorzugsweise embedded H2) gespeichert sind.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class H2QS implements QS {

	public static H2QS from(final String file, final boolean setup) throws SQLException, NullPointerException, ClassNotFoundException {
		Class.forName("org.h2.Driver");
		return new H2QS(DriverManager.getConnection("jdbc:h2:" + file, "", ""));
	}

	/** Dieses Feld speichert die Datenbankverbindung. */
	final Connection conn;

	final PreparedStatement selectEdgeSave;

	final PreparedStatement selectSaveNode;

	final PreparedStatement selectNodeValue;

	final PreparedStatement insertEdgeSave;

	final PreparedStatement insertSaveNode;

	final PreparedStatement insertCopyEdges;

	final PreparedStatement insertCopyNodes;

	final PreparedStatement insertCopyValues;

	final PreparedStatement deleteEdgeSave;

	final PreparedStatement deleteEdgesHavingNode;

	final PreparedStatement deleteValueHavingNode;

	final PreparedStatement createSaveNode;

	final PreparedStatement createCopyEdges;

	final PreparedStatement createCopyNodes;

	final PreparedStatement createCopyValues;

	/** Dieser Konstruktor initialisiert die Datenbankverbindung und erstellt bei Bedarf das Tabellenschema.
	 *
	 * @param conn Datenbankverbindung. */
	public H2QS(final Connection conn) throws SQLException, NullPointerException {
		this.conn = conn;
		this.execImpl(H2QQ.createAll());
		this.selectEdgeSave = this.conn.prepareStatement(H2QQ.selectEdgeValue());
		this.selectSaveNode = this.conn.prepareStatement(H2QQ.selectNode());
		this.selectNodeValue = this.conn.prepareStatement(H2QQ.selectNodeValue());
		this.insertEdgeSave = this.conn.prepareStatement(H2QQ.insertEdgeSave());
		this.insertSaveNode = this.conn.prepareStatement(H2QQ.insertNode());
		this.insertCopyNodes = this.conn.prepareStatement(H2QQ.insertCopyNodes());
		this.insertCopyValues = this.conn.prepareStatement(H2QQ.insertCopyValues());
		this.deleteEdgeSave = this.conn.prepareStatement(H2QQ.deleteEdgeSave());
		this.deleteEdgesHavingNode = this.conn.prepareStatement(H2QQ.deleteEdgesHavingNode());
		this.deleteValueHavingNode = this.conn.prepareStatement(H2QQ.deleteNodeSave());

		this.createSaveNode = this.conn.prepareStatement(H2QQ.selectNextNode());
		this.createCopyEdges = this.conn.prepareStatement(H2QQ.createCopyEdges());
		this.createCopyNodes = this.conn.prepareStatement(H2QQ.createCopyNodes());
		this.createCopyValues = this.conn.prepareStatement(H2QQ.createCopyValues());
	}

	@Override
	protected void finalize() throws Throwable {
		this.selectEdgeSave.close();
		this.selectSaveNode.close();
		this.selectNodeValue.close();
		this.insertEdgeSave.close();
		this.insertSaveNode.close();
		this.deleteEdgeSave.close();
		this.deleteEdgesHavingNode.close();
		this.deleteValueHavingNode.close();
	}

	final H2QE asQE(final Object src) throws NullPointerException, IllegalArgumentException {
		try {
			final H2QE res = (H2QE)src;
			if (res.owner == this) return res;
		} catch (final ClassCastException cause) {}
		throw new IllegalArgumentException();
	}

	final H2QESet asQESet(final Object src) throws NullPointerException, IllegalArgumentException {
		try {
			final H2QESet res = (H2QESet)src;
			if (res.owner == this) return res;
		} catch (final ClassCastException cause) {}
		throw new IllegalArgumentException();
	}

	final H2QN asQN(final Object src) throws NullPointerException, IllegalArgumentException {
		try {
			final H2QN res = (H2QN)src;
			if (res.owner == this) return res;
		} catch (final ClassCastException cause) {}
		throw new IllegalArgumentException();
	}

	final H2QNSet asQNSet(final Object src) throws NullPointerException, IllegalArgumentException {
		try {
			final H2QNSet res = (H2QNSet)src;
			if (res.owner == this) return res;
		} catch (final ClassCastException cause) {}
		throw new IllegalArgumentException();
	}

	final String asQV(final Object src) throws NullPointerException {
		return src.toString();
	}

	final H2QVSet asQVSet(final Object src) throws NullPointerException, IllegalArgumentException {
		try {
			final H2QVSet res = (H2QVSet)src;
			if (res.owner == this) return res;
		} catch (final ClassCastException cause) {}
		throw new IllegalArgumentException();
	}

	/** Diese Methode führt die gegebene Anfrage {@link PreparedStatement#executeQuery() aus} und gibt den {@link ResultSet#getInt(int) Zahlenwert} des ersten
	 * Ergebnisses zurück. */
	final int keyImpl(final PreparedStatement stmt) throws NullPointerException, IllegalStateException {
		try (final ResultSet rset = stmt.executeQuery()) {
			if (rset.next()) return rset.getInt(1);
		} catch (final SQLException cause) {
			throw new IllegalStateException(cause);
		}
		throw new IllegalStateException();
	}

	/** Diese Methode führt die gegebene Anfrage {@link Statement#executeUpdate(String) aus} und gibt nur dann {@code true} zurück, wenn dadurch Tabellenzeilen
	 * verändert wurden. */
	final boolean execImpl(final String query) {
		try (final Statement stmt = this.conn.createStatement()) {
			return stmt.executeUpdate(query) != 0;
		} catch (final SQLException cause) {
			throw new IllegalStateException(cause);
		}
	}

	/** Diese Methode implementiert {@link H2QN#pop()}. */
	final boolean popImpl(final H2QN node) {
		try {
			final PreparedStatement stmt1 = this.deleteEdgesHavingNode, stmt2 = this.deleteValueHavingNode;
			stmt1.setInt(1, node.key);
			stmt2.setInt(1, node.key);
			return (stmt1.executeUpdate() | stmt2.executeUpdate()) != 0;
		} catch (final SQLException cause) {
			throw new IllegalStateException(cause);
		}
	}

	/** Diese Methode leert den Graphspeicher. */
	public void reset() throws SQLException {
		this.execImpl(H2QQ.deleteAll());
	}

	/** Diese Methode entfernt alle temporären Textwerte, Hyperknoten und Hyperkanten. */
	public void cleanup() {
		this.execImpl(H2QQ.deleteCopy());
	}

	/** Diese Methode entfernt alle Hyperknoten mit Textwert, die nich in Hyperkanten verwendet werden. */
	public void compact() {
		this.nodes().except(this.edges().nodes()).popAll();
	}

	/** Diese Methode gibt die im Konstruktor übergebene Datenbankverbindung zurück. */
	public Connection connection() {
		return this.conn;
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
	public H2QE newEdge() {
		final int key = this.keyImpl(this.createSaveNode);
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
	public H2QESet newEdges(final Iterable<? extends QE> edges) throws NullPointerException, IllegalArgumentException {
		try {
			final int key = this.keyImpl(this.createCopyEdges);
			if (edges instanceof H2QESet) {
				final H2QESet set = this.asQESet(edges);
				this.execImpl(H2QQ.insertEdges(key, set));
				return new H2QESet.Copy(this, key);
			}
			try (final PreparedStatement stmt = this.conn.prepareStatement(H2QQ.insertEdges(key))) {
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
			return new H2QESet.Copy(this, key);
		} catch (final SQLException cause) {
			throw new IllegalStateException(cause);
		}
	}

	@Override
	public H2QN newNode() {
		return new H2QN(this, this.keyImpl(this.createSaveNode));
	}

	@Override
	public H2QN newNode(final Object value) {
		try {
			final String string = this.asQV(value);
			final PreparedStatement stmt = this.selectSaveNode;
			stmt.setString(1, string);
			final ResultSet res = stmt.executeQuery();
			if (res.next()) return new H2QN(this, res.getInt(1));
			final PreparedStatement stmt2 = this.insertSaveNode;
			final int key = this.keyImpl(this.createSaveNode);
			stmt2.setInt(1, key);
			stmt2.setString(2, string);
			stmt2.executeUpdate();
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
	public H2QNSet newNodes(final Iterable<? extends QN> nodes) throws NullPointerException, IllegalArgumentException {
		try {
			final int key = this.keyImpl(this.createCopyNodes);
			if (nodes instanceof H2QNSet) {
				final H2QNSet set = this.asQNSet(nodes);
				this.execImpl(H2QQ.insertCopyNodes(key, set));
				return new H2QNSet.Copy(this, key);
			}
			final PreparedStatement stmt = this.insertCopyNodes;
			for (final Object item: nodes) {
				stmt.setInt(1, key);
				stmt.setInt(2, this.asQN(item).key);
				stmt.addBatch();
			}
			stmt.executeBatch();
			return new H2QNSet.Copy(this, key);
		} catch (final SQLException cause) {
			throw new IllegalStateException(cause);
		}
	}

	@Override
	public H2QVSet newValues(final Object... values) throws NullPointerException, IllegalArgumentException {
		return this.newValues(Arrays.asList(values));
	}

	@Override
	public H2QVSet newValues(final Iterable<?> values) throws NullPointerException, IllegalArgumentException {
		try {
			final int key = this.keyImpl(this.createCopyValues);
			if (values instanceof H2QVSet) {
				final H2QVSet set = (H2QVSet)values;
				if (set.owner == this) {
					this.execImpl(H2QQ.insertCopyValues(key, set));
					return new H2QVSet.Copy(this, key);
				}
			}
			final PreparedStatement stmt = this.insertCopyValues;
			for (final Object item: values) {
				stmt.setInt(1, key);
				stmt.setString(2, this.asQV(item));
				stmt.addBatch();
			}
			stmt.executeBatch();
			return new H2QVSet.Copy(this, key);
		} catch (final SQLException cause) {
			throw new IllegalStateException(cause);
		}
	}

	@Override
	public String toString() {
		return Objects.toFormatString(false, true, this, "edges", this.edges().size(), "values", this.values().size());
	}

}
