package bee.creative.qs.h2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import bee.creative.qs.QE;
import bee.creative.qs.QN;
import bee.creative.qs.QO;
import bee.creative.qs.QS;
import bee.creative.qs.QT;
import bee.creative.qs.QTSet;
import bee.creative.qs.h2.H2QTSet.Names;
import bee.creative.util.HashSet;

/** Diese Klasse implementiert einen {@link QS Graphspeicher}, dessen Hyperkanten und Textwerte in einer Datenbank (vorzugsweise embedded H2) gespeichert sind.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class H2QS implements QS, AutoCloseable {

	/** Diese Methode liefert den Graphspeicher zu der Datenbankverbindung, die mit dem {@code jdbc:h2:}-Protokoll zum gegebenen Dateipfad erzeugt wurde. Dazu
	 * wird das Laden der Klasse {@code org.h2.Driver} erzwungen. */
	public static H2QS from(final String file) throws SQLException, NullPointerException, ClassNotFoundException {
		Class.forName("org.h2.Driver");
		return new H2QS(DriverManager.getConnection("jdbc:h2:" + file, "", ""));
	}

	/** Dieses Feld speichert die über den Konstruktor bereitgestellte Datenbankverbindung. */
	public final Connection conn;

	/** Dieser Konstruktor initialisiert die Datenbankverbindung und erstellt bei Bedarf das Tabellenschema.
	 *
	 * @param conn Datenbankverbindung. */
	public H2QS(final Connection conn) throws SQLException, NullPointerException {
		this.conn = conn;
		new H2QQ() //
			.push("CREATE TABLE IF NOT EXISTS QN (N BIGINT NOT NULL, V VARCHAR(1G) NOT NULL, PRIMARY KEY (N));") //
			.push("CREATE UNIQUE INDEX IF NOT EXISTS QN_INDEX_V ON QN (V);") //
			.push("CREATE SEQUENCE IF NOT EXISTS QN_SEQUENCE START WITH 1;") //
			.push("CREATE TABLE IF NOT EXISTS QE (C BIGINT NOT NULL, P BIGINT NOT NULL, S BIGINT NOT NULL, O BIGINT NOT NULL, PRIMARY KEY (C, P, S, O));") //
			.push("CREATE INDEX IF NOT EXISTS QE_INDEX_CPO ON QE (C, P, O, S);") //
			.push("CREATE INDEX IF NOT EXISTS QE_INDEX_CSP ON QE (C, S, P, O);") //
			.push("CREATE INDEX IF NOT EXISTS QE_INDEX_COP ON QE (C, O, P, S);") //
			.push("CREATE SEQUENCE IF NOT EXISTS QT_SEQUENCE START WITH 1") //
			.update(this);

		this.nodes = new H2QNSet.Main(this);
		this.edges = new H2QESet.Main(this);

		this.selectQN_V = this.conn.prepareStatement("SELECT N FROM QN WHERE V=?");
		this.getQV_N = this.conn.prepareStatement("SELECT V FROM QN WHERE N=?");
		this.getQE_CPSO = this.conn.prepareStatement("SELECT TOP 1 * FROM QE WHERE C=? AND P=? AND S=? AND O=?");
		this.putQE_CPSO = this.conn.prepareStatement("MERGE INTO QE (C, P, S, O) VALUES (?, ?, ?, ?)");
		this.insertSaveNode = this.conn.prepareStatement("INSERT INTO QN (N, V) VALUES (?, ?)");
		this.popQE_CPSO = this.conn.prepareStatement("DELETE FROM QE WHERE C=? AND P=? AND S=? AND O=?");
		this.popQE_N = this.conn.prepareStatement("DELETE FROM QE WHERE C=?1 OR P=?1 OR S=?1 OR O=?1");
		this.popQV_N = this.conn.prepareStatement("DELETE FROM QN WHERE N=?");
		this.createNode = this.conn.prepareStatement("SELECT NEXT VALUE FOR QN_SEQUENCE");
		this.createTemp = this.conn.prepareStatement("SELECT NEXT VALUE FOR QT_SEQUENCE");
	}

	/** Diese Methode liefert das gegebene Objekt als {@link H2QE} dieses {@link QO Graphspeichers} oder löst eine Ausnahme aus. */
	public final H2QE asQE(final Object src) throws NullPointerException, IllegalArgumentException {
		try {
			final H2QE res = (H2QE)src;
			if (res.owner == this) return res;
		} catch (final ClassCastException cause) {}
		throw new IllegalArgumentException();
	}

	/** Diese Methode liefert das gegebene Objekt als {@link H2QESet} dieses {@link QO Graphspeichers} oder löst eine Ausnahme aus. */
	public final H2QESet asQESet(final Object src) throws NullPointerException, IllegalArgumentException {
		try {
			final H2QESet res = (H2QESet)src;
			if (res.owner == this) return res;
		} catch (final ClassCastException cause) {}
		throw new IllegalArgumentException();
	}

	/** Diese Methode liefert das gegebene Objekt als {@link H2QN} dieses {@link QO Graphspeichers} oder löst eine Ausnahme aus. */
	public final H2QN asQN(final Object src) throws NullPointerException, IllegalArgumentException {
		try {
			final H2QN res = (H2QN)src;
			if (res.owner == this) return res;
		} catch (final ClassCastException cause) {}
		throw new IllegalArgumentException();
	}

	/** Diese Methode liefert das gegebene Objekt als {@link H2QNSet} dieses {@link QO Graphspeichers} oder löst eine Ausnahme aus. */
	public final H2QNSet asQNSet(final Object src) throws NullPointerException, IllegalArgumentException {
		try {
			final H2QNSet res = (H2QNSet)src;
			if (res.owner == this) return res;
		} catch (final ClassCastException cause) {}
		throw new IllegalArgumentException();
	}

	/** Diese Methode liefert die {@link Object#toString() Textdarstellung} des gegebenen Objekts oder löst eine Ausnahme aus. */
	public final String asQV(final Object src) throws NullPointerException {
		return src.toString();
	}

	/** Diese Methode liefert das gegebene Objekt als {@link H2QVSet} dieses {@link QO Graphspeichers} oder löst eine Ausnahme aus. */
	public final H2QVSet asQVSet(final Object src) throws NullPointerException, IllegalArgumentException {
		try {
			final H2QVSet res = (H2QVSet)src;
			if (res.owner == this) return res;
		} catch (final ClassCastException cause) {}
		throw new IllegalArgumentException();
	}

	/** Diese Methode liefert das gegebene Objekt als {@link H2QT} dieses {@link QO Graphspeichers} oder löst eine Ausnahme aus. */
	public final H2QT asQT(final Object src) throws NullPointerException, IllegalArgumentException {
		try {
			final H2QT res = (H2QT)src;
			if (res.owner == this) return res;
		} catch (final ClassCastException cause) {}
		throw new IllegalArgumentException();
	}

	/** Diese Methode liefert das gegebene Objekt als {@link H2QTSet} dieses {@link QO Graphspeichers} oder löst eine Ausnahme aus. */
	public final H2QTSet asQTSet(final Object src) throws NullPointerException, IllegalArgumentException {
		try {
			final H2QTSet res = (H2QTSet)src;
			if (res.owner == this) return res;
		} catch (final ClassCastException cause) {}
		throw new IllegalArgumentException();
	}

	/** Diese Methode liefert das gegebene Objekt als {@link H2QTSet} dieses {@link QO Graphspeichers} mit der gegebene Anzahl an {@link QTSet#names() Rollen}
	 * oder löst eine Ausnahme aus. */
	public final H2QTSet asQTSet(final Object src, final int roles) throws NullPointerException, IllegalArgumentException {
		final H2QTSet res = this.asQTSet(src);
		if (res.names().size() == roles) return res;
		throw new IllegalArgumentException();
	}

	/** Diese Methode liefert das gegebene Objekt als {@link H2QTSet} dieses {@link QO Graphspeichers} oder löst eine Ausnahme aus. Das {@link H2QTSet} muss die
	 * gegebenen {@link QTSet#names() Rollennamen} besitzen. */
	public final H2QTSet asQTSet(final Object src, final List<?> names) throws NullPointerException, IllegalArgumentException {
		final H2QTSet res = this.asQTSet(src, names.size());
		if (res.names().equals(names)) return res;
		throw new IllegalArgumentException();
	}

	/** Diese Methode leert den Graphspeicher. */
	public void reset() throws IllegalStateException {
		try (Statement stmt = this.conn.createStatement()) {
			this.popValueMark = new Object();
			stmt.executeUpdate("DELETE FROM QN;DELETE FROM QE;ALTER SEQUENCE QN_SEQUENCE RESTART WITH 1");
		} catch (final SQLException cause) {
			throw new IllegalStateException(cause);
		}
	}

	@Override
	public void close() throws SQLException {
		synchronized (this.tempTables) {
			for (final String name: new ArrayList<>(this.tempTables)) {
				this.deleteTemp(name);
			}
		}
		this.conn.close();
	}

	/** Diese Methode entfernt alle Hyperknoten mit Textwert, die nich in Hyperkanten verwendet werden. */
	public void compact() {
		final H2QESet edges = this.edges();
		this.nodes().except(edges.contexts().union(edges.predicates()).union(edges.subjects()).union(edges.objects())).popAll();
	}

	@Override
	public H2QESet edges() {
		return this.edges;
	}

	@Override
	public H2QNSet nodes() {
		return this.nodes;
	}

	@Override
	public H2QVSet values() {
		return new H2QVSet.Main(this);
	}

	@Override
	public H2QE newEdge() {
		final long key = this.newKey(this.createNode);
		return this.newEdge(key, key, key, key);
	}

	@Override
	public H2QE newEdge(final QN node) throws NullPointerException, IllegalArgumentException {
		final long key = this.asQN(node).key;
		return this.newEdge(key, key, key, key);
	}

	@Override
	public H2QE newEdge(final QN context, final QN predicate, final QN subject, final QN object) throws NullPointerException, IllegalArgumentException {
		return this.newEdge(this.asQN(context).key, this.asQN(predicate).key, this.asQN(subject).key, this.asQN(object).key);
	}

	/** Diese Methode liefert eine neue {@link H2QE Hyperkante} mit den gegebenen Knotenkennungen. */
	public final H2QE newEdge(final long context, final long predicate, final long subject, final long object) {
		return new H2QE(this, context, predicate, subject, object);
	}

	@Override
	public H2QESet newEdges() {
		return this.newEdges(this.newEdge());
	}

	@Override
	public H2QESet newEdges(final QN node) {
		return this.newEdges(this.newEdge(node));
	}

	@Override
	public H2QESet newEdges(final QN context, final QN predicate, final QN subject, final QN object) {
		return this.newEdges(this.newEdge(context, predicate, subject, object));
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
				if (set instanceof H2QESet.Temp) return (H2QESet.Temp)set;
				final H2QESet.Temp res = new H2QESet.Temp(this);
				new H2QQ().push("INSERT INTO ").push(res.table).push(" SELECT * FROM (").push(set).push(")").update(this);
				return res;
			}
			final H2QESet.Temp buf = new H2QESet.Temp(this);
			try (final PreparedStatement stmt = new H2QQ().push("INSERT INTO ").push(buf.table).push(" (C, P, S, O) VALUES (?, ?, ?, ?)").prepare(this)) {
				for (final Object item: edges) {
					final H2QE edge = this.asQE(item);
					stmt.setLong(1, edge.context);
					stmt.setLong(2, edge.predicate);
					stmt.setLong(3, edge.subject);
					stmt.setLong(4, edge.object);
					stmt.addBatch();
				}
				stmt.executeBatch();
			}
			final H2QESet.Temp res = new H2QESet.Temp(this);
			new H2QQ().push("INSERT INTO ").push(res.table).push(" SELECT DISTINCT * FROM ").push(buf.table).update(this);
			return res;
		} catch (final SQLException cause) {
			throw new IllegalStateException(cause);
		}
	}

	@Override
	public H2QN newNode() {
		return this.newNode(this.newKey(this.createNode));
	}

	@Override
	public H2QN newNode(final Object value) {
		try {
			final String string = this.asQV(value);
			final PreparedStatement stmt = this.selectQN_V;
			stmt.setString(1, string);
			final ResultSet res = stmt.executeQuery();
			if (res.next()) return this.newNode(res.getLong(1));
			final PreparedStatement stmt2 = this.insertSaveNode;
			final long key = this.newKey(this.createNode);
			stmt2.setLong(1, key);
			stmt2.setString(2, string);
			this.markPutValue(stmt2.executeUpdate() != 0);
			return this.newNode(key);
		} catch (final SQLException cause) {
			throw new IllegalStateException(cause);
		}
	}

	/** Diese Methode liefert einen neuen {@link H2QN Hyperknoten} mit der gegebenen Kennung. */
	public final H2QN newNode(final long key) {
		return new H2QN(this, key);
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
				if (set instanceof H2QNSet.Temp) return set;
				final H2QNSet res = new H2QNSet.Temp(this);
				new H2QQ().push("INSERT INTO ").push(res.table).push(" SELECT * FROM (").push(set).push(")").update(this);
				return res;
			}
			final H2QNSet buf = new H2QNSet.Temp(this);
			try (final PreparedStatement stmt = new H2QQ().push("INSERT INTO ").push(buf.table).push(" (N) VALUES (?)").prepare(this)) {
				for (final Object item: nodes) {
					stmt.setLong(1, this.asQN(item).key);
					stmt.addBatch();
				}
				stmt.executeBatch();
			}
			final H2QNSet res = new H2QNSet.Temp(this);
			new H2QQ().push("INSERT INTO ").push(res.table).push(" SELECT DISTINCT * FROM ").push(buf.table).update(this);
			return res;
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
					if (set instanceof H2QVSet.Temp) return (H2QVSet.Temp)set;
					final H2QVSet.Temp res = new H2QVSet.Temp(this);
					new H2QQ().push("INSERT INTO ").push(res.table).push(" SELECT V FROM (").push(set).push(")").update(this);
					return res;
				}
			}
			final H2QVSet.Temp buf = new H2QVSet.Temp(this);
			try (final PreparedStatement stmt = new H2QQ().push("INSERT INTO ").push(buf.table).push(" (V) VALUES (?)").prepare(this)) {
				for (final Object item: values) {
					stmt.setString(1, this.asQV(item));
					stmt.addBatch();
				}
				stmt.executeBatch();
			}
			final H2QVSet.Temp res = new H2QVSet.Temp(this);
			new H2QQ().push("INSERT INTO ").push(res.table).push(" SELECT DISTINCT * FROM ").push(buf.table).update(this);
			return res;
		} catch (final SQLException cause) {
			throw new IllegalStateException(cause);
		}
	}

	@Override
	public H2QT newTuple(final QN... nodes) throws NullPointerException, IllegalArgumentException {
		final int size = nodes.length;
		final long[] keys = new long[size];
		for (int i = 0; i < size; i++) {
			keys[i] = this.asQN(nodes[i]).key;
		}
		return this.newTuple(keys);
	}

	@Override
	public H2QT newTuple(final List<? extends QN> nodes) throws NullPointerException, IllegalArgumentException {
		final int size = nodes.size();
		final long[] keys = new long[size];
		for (int i = 0; i < size; i++) {
			keys[i] = this.asQN(nodes.get(i)).key;
		}
		return this.newTuple(keys);
	}

	/** Diese Methode liefert ein neues {@link H2QT Hypertupel} mit den gegebenen Knotenkennungen. */
	public final H2QT newTuple(final long[] keys) {
		return new H2QT(this, keys);
	}

	@Override
	public H2QTSet newTuples(final List<String> names, final QN... tuples) throws NullPointerException, IllegalArgumentException {
		return this.newTuples(new Names(names), null, tuples);
	}

	@Override
	public H2QTSet newTuples(final List<String> names, final Iterable<? extends QT> tuples) throws NullPointerException, IllegalArgumentException {
		if (tuples instanceof H2QTSet) {
			final H2QTSet set = this.asQTSet(tuples, names.size());
			if (set instanceof H2QTSet.Temp) return set.withNames(names);
			final H2QTSet.Temp res = new H2QTSet.Temp(this, new Names(names));
			new H2QQ().push("INSERT INTO ").push(res.table).push(" SELECT * FROM (").push(set).push(")").update(this);
			return res;
		}
		return this.newTuples(new Names(names), tuples, null);
	}

	private final H2QESet edges;

	private final H2QNSet nodes;

	final PreparedStatement getQV_N;

	final PreparedStatement selectQN_V;

	final PreparedStatement getQE_CPSO;

	final PreparedStatement putQE_CPSO;

	final PreparedStatement insertSaveNode;

	final PreparedStatement popQE_CPSO;

	final PreparedStatement popQE_N;

	final PreparedStatement popQV_N;

	private final HashSet<String> tempTables = new HashSet<>();

	private final PreparedStatement createNode;

	private final PreparedStatement createTemp;

	Object putValueMark;

	Object popValueMark;

	boolean markPutValue(final boolean changed) {
		if (!changed) return false;
		this.putValueMark = new Object();
		return true;
	}

	boolean markPopValue(final boolean changed) {
		if (!changed) return false;
		this.popValueMark = new Object();
		return true;
	}

	Object createTemp() {
		synchronized (this.tempTables) {
			final String name = "QT" + this.newKey(this.createTemp);
			this.tempTables.add(name);
			return new Object() {

				@Override
				protected void finalize() throws Throwable {
					H2QS.this.deleteTemp(name);
				}

				@Override
				public String toString() {
					return name;
				}

			};
		}
	}

	/** Diese Methode entfernt die Tabelle mit dem gegebenen Namen, sofern dieser Name über {@link #createTemp()} erzeugt wurde. */
	void deleteTemp(final String name) throws SQLException {
		synchronized (this.tempTables) {
			if (this.tempTables.remove(name)) {
				try (Statement stmt = this.conn.createStatement()) {
					stmt.executeUpdate("DROP TABLE IF EXISTS " + name);
				}
			}
		}
	}

	/** Diese Methode führt die gegebene Anfrage {@link PreparedStatement#executeQuery() aus} und gibt den {@link ResultSet#getLong(int) Zahlenwert} des ersten
	 * Ergebnisses zurück. */
	private final long newKey(final PreparedStatement stmt) throws NullPointerException, IllegalStateException {
		try (final ResultSet rset = stmt.executeQuery()) {
			if (rset.next()) return rset.getLong(1);
		} catch (final SQLException cause) {
			throw new IllegalStateException(cause);
		}
		throw new IllegalStateException();
	}

	private final H2QTSet newTuples(final Names names, final Iterable<? extends QT> tuples1, final QN[] tuples2)
		throws NullPointerException, IllegalArgumentException {
		try {
			final int size = names.size();
			final H2QTSet.Temp buf = new H2QTSet.Temp(this, names);
			final H2QQ qry = new H2QQ().push("INSERT INTO ").push(buf.table).push(" (C0");
			for (int i = 1; i < size; i++) {
				qry.push(", C").push(i);
			}
			qry.push(") VALUES (?");
			for (int i = 1; i < size; i++) {
				qry.push(", ?");
			}
			qry.push(")");
			try (final PreparedStatement stmt = qry.prepare(this)) {
				if (tuples2 != null) {
					final int count = tuples2.length;
					if ((count % size) != 0) throw new IllegalArgumentException();
					for (int r = 0; r < count; r += size) {
						for (int i = 0; i < size; i++) {
							stmt.setLong(i + 1, this.asQN(tuples2[r + i]).key);
						}
						stmt.addBatch();
					}
				} else {
					for (final Object item: tuples1) {
						final long[] keys = this.asQT(item).keys;
						if (keys.length != size) throw new IllegalArgumentException();
						for (int i = 0; i < size; i++) {
							stmt.setLong(i + 1, keys[i]);
						}
						stmt.addBatch();
					}
				}
				stmt.executeBatch();
			}
			final H2QTSet.Temp res = new H2QTSet.Temp(this, names);
			new H2QQ().push("INSERT INTO ").push(res.table).push(" SELECT DISTINCT * FROM ").push(buf.table).update(this);
			return res;
		} catch (final SQLException cause) {
			throw new IllegalStateException(cause);
		}
	}

}