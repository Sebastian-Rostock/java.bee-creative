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

	/** Diese Methode liefert den Graphspeicher zu der Datenbankverbindung, die mit dem {@code jdbc:h2:}-Protokoll zum gegebenen Dateipfad erzeugt wurde. Dazu
	 * wird das Laden der Klasse {@code org.h2.Driver} erzwungen. */
	public static H2QS from(final String file) throws SQLException, NullPointerException, ClassNotFoundException {
		Class.forName("org.h2.Driver");
		return new H2QS(DriverManager.getConnection("jdbc:h2:" + file, "", ""));
	}

	/** Dieses Feld speichert die Datenbankverbindung. */
	final Connection conn;

	/** Dieses Feld speichert die in {@link #execImpl(String)} wiederverwendete Anweisung. */
	final Statement exec;

	final PreparedStatement selectSaveEdge;

	final PreparedStatement selectSaveNode;

	final PreparedStatement selectSaveValue;

	final PreparedStatement insertSaveEdge;

	final PreparedStatement insertSaveNode;

	final PreparedStatement deleteSaveEdge;

	final PreparedStatement deleteSaveEdges;

	final PreparedStatement deleteSaveNode;

	final PreparedStatement createSaveNode;

	final PreparedStatement createTempEdgesKey;

	final PreparedStatement createTempNodesKey;

	final PreparedStatement createTempValuesKey;

	/** Dieser Konstruktor initialisiert die Datenbankverbindung und erstellt bei Bedarf das Tabellenschema.
	 *
	 * @param conn Datenbankverbindung. */
	public H2QS(final Connection conn) throws SQLException, NullPointerException {
		this.conn = conn;
		this.exec = conn.createStatement();
		this.execImpl(H2QQ.createAll());
		this.selectSaveEdge = this.conn.prepareStatement(H2QQ.selectSaveEdge());
		this.selectSaveNode = this.conn.prepareStatement(H2QQ.selectSaveNode());
		this.selectSaveValue = this.conn.prepareStatement(H2QQ.selectSaveValue());
		this.insertSaveEdge = this.conn.prepareStatement(H2QQ.insertSaveEdge());
		this.insertSaveNode = this.conn.prepareStatement(H2QQ.insertSaveNode());
		this.deleteSaveEdge = this.conn.prepareStatement(H2QQ.deleteSaveEdge());
		this.deleteSaveEdges = this.conn.prepareStatement(H2QQ.deleteSaveEdges());
		this.deleteSaveNode = this.conn.prepareStatement(H2QQ.deleteSaveNode());
		this.createSaveNode = this.conn.prepareStatement(H2QQ.createSaveNode());
		this.createTempEdgesKey = this.conn.prepareStatement(H2QQ.createTempEdgesKey());
		this.createTempNodesKey = this.conn.prepareStatement(H2QQ.createTempNodesKey());
		this.createTempValuesKey = this.conn.prepareStatement(H2QQ.createTempValuesKey());
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
		try {
			return this.exec.executeUpdate(query) != 0;
		} catch (final SQLException cause) {
			throw new IllegalStateException(cause);
		}
	}

	/** Diese Methode leert den Graphspeicher. */
	public void reset() throws SQLException {
		this.execImpl(H2QQ.deleteSave());
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
			if (edges instanceof H2QESet) {
				final H2QESet set = this.asQESet(edges);
				final int key = this.keyImpl(this.createTempEdgesKey);
				this.execImpl(H2QQ.createTempEdges(key));
				this.execImpl(H2QQ.insertTempEdges(key, set));
				this.execImpl(H2QQ.createTempEdgesIndex(key));
				return new H2QESet.Temp(this, key);
			}
			final int key2 = this.keyImpl(this.createTempEdgesKey);
			this.execImpl(H2QQ.createTempEdges(key2));
			try (final PreparedStatement stmt = this.conn.prepareStatement(H2QQ.insertTempEdges(key2))) {
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
			final int key = this.keyImpl(this.createTempEdgesKey);
			this.execImpl(H2QQ.createTempEdges(key));
			this.execImpl(H2QQ.insertTempEdges(key, key2));
			this.execImpl(H2QQ.deleteTempEdges(key2));
			this.execImpl(H2QQ.createTempEdgesIndex(key));
			return new H2QESet.Temp(this, key);
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
			if (nodes instanceof H2QNSet) {
				final H2QNSet set = this.asQNSet(nodes);
				final int key = this.keyImpl(this.createTempNodesKey);
				this.execImpl(H2QQ.createTempNodes(key));
				this.execImpl(H2QQ.insertTempNodes(key, set));
				this.execImpl(H2QQ.createTempNodesIndex(key));
				return new H2QNSet.Temp(this, key);
			}
			final int key2 = this.keyImpl(this.createTempNodesKey);
			this.execImpl(H2QQ.createTempNodes(key2));
			try (final PreparedStatement stmt = this.conn.prepareStatement(H2QQ.insertTempNodes(key2))) {
				for (final Object item: nodes) {
					stmt.setInt(1, this.asQN(item).key);
					stmt.addBatch();
				}
				stmt.executeBatch();
			}
			final int key = this.keyImpl(this.createTempNodesKey);
			this.execImpl(H2QQ.createTempNodes(key));
			this.execImpl(H2QQ.insertTempNodes(key, key2));
			this.execImpl(H2QQ.deleteTempNodes(key2));
			this.execImpl(H2QQ.createTempNodesIndex(key));
			return new H2QNSet.Temp(this, key);
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
			if (values instanceof H2QVSet) {
				final H2QVSet set = (H2QVSet)values;
				if (set.owner == this) {
					final int key = this.keyImpl(this.createTempValuesKey);
					this.execImpl(H2QQ.createTempValues(key));
					this.execImpl(H2QQ.insertTempValues(key, set));
					this.execImpl(H2QQ.createTempValuesIndex(key));
					return new H2QVSet.Temp(this, key);
				}
			}
			final int key2 = this.keyImpl(this.createTempValuesKey);
			this.execImpl(H2QQ.createTempValues(key2));
			try (final PreparedStatement stmt = this.conn.prepareStatement(H2QQ.insertTempValues(key2))) {
				for (final Object item: values) {
					stmt.setString(1, this.asQV(item));
					stmt.addBatch();
				}
				stmt.executeBatch();
			}
			final int key = this.keyImpl(this.createTempValuesKey);
			this.execImpl(H2QQ.createTempValues(key));
			this.execImpl(H2QQ.insertTempValues(key, key2));
			this.execImpl(H2QQ.deleteTempValues(key2));
			this.execImpl(H2QQ.createTempValuesIndex(key));
			return new H2QVSet.Temp(this, key);
		} catch (final SQLException cause) {
			throw new IllegalStateException(cause);
		}
	}

	@Override
	public String toString() {
		return Objects.toStringCall(false, true, this, "edges", this.edges().size(), "values", this.values().size());
	}

}
