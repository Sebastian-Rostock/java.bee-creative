package bee.creative.qs.h2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import bee.creative.util.HashMap;
import bee.creative.util.HashSet;
import bee.creative.util.Translator2;
import bee.creative.util.Translators;

/** Diese Klasse implementiert einen {@link QS Graphspeicher}, dessen Hyperkanten und Textwerte in einer Datenbank (embedded H2) gespeichert sind.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class H2QS implements QS, AutoCloseable {

	/** Diese Methode liefert den Graphspeicher zum gegebenen {@link H2C#from(String) Dateipfad} und ohne {@link #owner() Besitzer}. */
	public static H2QS from(String file) throws SQLException, NullPointerException, ClassNotFoundException {
		return new H2QS(H2C.from(file), null);
	}

	/** Dieses Feld speichert die über den Konstruktor bereitgestellte Datenbankverbindung. */
	public final Connection conn;

	/** Dieses Feld speichert den Besitzer oder {@code null}. */
	public final Object owner;

	/** Dieser Konstruktor initialisiert die Datenbankverbindung und erstellt bei Bedarf das Tabellenschema.
	 *
	 * @param conn Datenbankverbindung.
	 * @param owner Besitzer oder {@code null}. */
	public H2QS(Connection conn, Object owner) throws SQLException, NullPointerException {
		this.conn = conn;
		this.owner = owner;
		new H2QQ().push("" + //
			"CREATE SEQUENCE IF NOT EXISTS QT_SEQ;" + //
			"CREATE SEQUENCE IF NOT EXISTS QN_SEQ;" + //
			"CREATE TABLE IF NOT EXISTS QN (N BIGINT NOT NULL, V VARCHAR(1G) NOT NULL, PRIMARY KEY (N));" + //
			"CREATE TABLE IF NOT EXISTS QE (C BIGINT NOT NULL, P BIGINT NOT NULL, S BIGINT NOT NULL, O BIGINT NOT NULL, PRIMARY KEY (C, P, S, O));" + //
			"CREATE UNIQUE INDEX IF NOT EXISTS QN_INDEX_V ON QN (V);" + //
			"CREATE INDEX IF NOT EXISTS QE_INDEX_CPO ON QE (C, P, O, S);" + //
			"CREATE INDEX IF NOT EXISTS QE_INDEX_CSP ON QE (C, S, P, O);" + //
			"CREATE INDEX IF NOT EXISTS QE_INDEX_COP ON QE (C, O, P, S);" //
		).update(this);

		this.nodes = new H2QNSet.Main(this);
		this.edges = new H2QESet.Main(this);
		this.values = new H2QVSet.Main(this);

		this.getQN = this.conn.prepareStatement("SELECT N FROM QN WHERE V=?");
		this.getQV = this.conn.prepareStatement("SELECT V FROM QN WHERE N=?");
		this.getQE = this.conn.prepareStatement("SELECT TOP 1 * FROM QE WHERE C=? AND P=? AND S=? AND O=?");
		this.putQN = this.conn.prepareStatement("SELECT NEXT VALUE FOR QN_SEQ");
		this.putQV = this.conn.prepareStatement("MERGE INTO QN (N, V) KEY (V) VALUES (NEXT VALUE FOR QN_SEQ, ?)");
		this.putQE = this.conn.prepareStatement("MERGE INTO QE (C, P, S, O) VALUES (?, ?, ?, ?)");
		this.putQT = this.conn.prepareStatement("SELECT NEXT VALUE FOR QT_SEQ");
		this.popQN = this.conn.prepareStatement("DELETE FROM QE WHERE C=?1 OR P=?1 OR S=?1 OR O=?1");
		this.popQV = this.conn.prepareStatement("DELETE FROM QN WHERE N=?");
		this.popQE = this.conn.prepareStatement("DELETE FROM QE WHERE C=? AND P=? AND S=? AND O=?");
	}

	/** Diese Methode liefert das gegebene Objekt als {@link H2QE} dieses {@link QO Graphspeichers} oder löst eine Ausnahme aus. */
	public final H2QE asQE(Object src) throws NullPointerException, IllegalArgumentException {
		try {
			var res = (H2QE)src;
			if (res.owner == this) return res;
			throw new IllegalArgumentException();
		} catch (ClassCastException cause) {
			throw new IllegalArgumentException();
		}
	}

	/** Diese Methode liefert das gegebene Objekt als {@link H2QESet} dieses {@link QO Graphspeichers} oder löst eine Ausnahme aus. */
	public final H2QESet asQESet(Object src) throws NullPointerException, IllegalArgumentException {
		try {
			var res = (H2QESet)src;
			if (res.owner == this) return res;
			throw new IllegalArgumentException();
		} catch (ClassCastException cause) {
			throw new IllegalArgumentException();
		}
	}

	/** Diese Methode liefert das gegebene Objekt als {@link H2QN} dieses {@link QO Graphspeichers} oder löst eine Ausnahme aus. */
	public final H2QN asQN(Object src) throws NullPointerException, IllegalArgumentException {
		try {
			var res = (H2QN)src;
			if (res.owner == this) return res;
			throw new IllegalArgumentException();
		} catch (ClassCastException cause) {
			throw new IllegalArgumentException();
		}
	}

	/** Diese Methode liefert das gegebene Objekt als {@link H2QNSet} dieses {@link QO Graphspeichers} oder löst eine Ausnahme aus. */
	public final H2QNSet asQNSet(Object src) throws NullPointerException, IllegalArgumentException {
		try {
			var res = (H2QNSet)src;
			if (res.owner == this) return res;
			throw new IllegalArgumentException();
		} catch (ClassCastException cause) {
			throw new IllegalArgumentException();
		}
	}

	/** Diese Methode liefert die {@link Object#toString() Textdarstellung} des gegebenen Objekts oder löst eine Ausnahme aus. */
	public final String asQV(Object src) throws NullPointerException {
		return src.toString();
	}

	/** Diese Methode liefert das gegebene Objekt als {@link H2QVSet} dieses {@link QO Graphspeichers} oder löst eine Ausnahme aus. */
	public final H2QVSet asQVSet(Object src) throws NullPointerException, IllegalArgumentException {
		try {
			var res = (H2QVSet)src;
			if (res.owner == this) return res;
			throw new IllegalArgumentException();
		} catch (ClassCastException cause) {
			throw new IllegalArgumentException();
		}
	}

	/** Diese Methode liefert das gegebene Objekt als {@link H2QT} dieses {@link QO Graphspeichers} oder löst eine Ausnahme aus. */
	public final H2QT asQT(Object src) throws NullPointerException, IllegalArgumentException {
		try {
			var res = (H2QT)src;
			if (res.owner == this) return res;
			throw new IllegalArgumentException();
		} catch (ClassCastException cause) {
			throw new IllegalArgumentException();
		}
	}

	/** Diese Methode liefert das gegebene Objekt als {@link H2QTSet} dieses {@link QO Graphspeichers} oder löst eine Ausnahme aus. */
	public final H2QTSet asQTSet(Object src) throws NullPointerException, IllegalArgumentException {
		try {
			var res = (H2QTSet)src;
			if (res.owner == this) return res;
			throw new IllegalArgumentException();
		} catch (ClassCastException cause) {
			throw new IllegalArgumentException();
		}
	}

	/** Diese Methode liefert das gegebene Objekt als {@link H2QTSet} dieses {@link QO Graphspeichers} mit der gegebene Anzahl an {@link QTSet#names() Rollen}
	 * oder löst eine Ausnahme aus. */
	public final H2QTSet asQTSet(Object src, int roles) throws NullPointerException, IllegalArgumentException {
		var res = this.asQTSet(src);
		if (res.names().size() == roles) return res;
		throw new IllegalArgumentException();
	}

	/** Diese Methode liefert das gegebene Objekt als {@link H2QTSet} dieses {@link QO Graphspeichers} oder löst eine Ausnahme aus. Das {@link H2QTSet} muss die
	 * gegebenen {@link QTSet#names() Rollennamen} besitzen. */
	public final H2QTSet asQTSet(Object src, List<?> names) throws NullPointerException, IllegalArgumentException {
		var res = this.asQTSet(src, names.size());
		if (res.names().equals(names)) return res;
		throw new IllegalArgumentException();
	}

	/** Diese Methode leert den Graphspeicher. */
	public void reset() throws IllegalStateException {
		this.popValueMark = new Object();
		new H2QQ().push("DELETE FROM QN;DELETE FROM QE").update(this);
	}

	@Override
	public void close() throws SQLException {
		try {
			synchronized (this.tables) {
				for (var name: new ArrayList<>(this.tables)) {
					this.popTable(name);
				}
			}
		} finally {
			this.conn.close();
		}
	}

	/** Diese Methode entfernt alle Hyperknoten mit Textwert, die nich in Hyperkanten verwendet werden. */
	public void compact() {
		this.nodes().except(this.edges().nodes()).popAll();
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
		return this.values;
	}

	@Override
	public Object owner() {
		return this.owner;
	}

	@Override
	public H2QN getNode(Object value) {
		try {
			var string = this.asQV(value);
			var getStmt = this.getQV;
			getStmt.setString(1, string);
			var res = getStmt.executeQuery();
			return res.next() ? this.newNode(res.getLong(1)) : null;
		} catch (SQLException cause) {
			throw new IllegalStateException(cause);
		}
	}

	@Override
	public H2QE newEdge() {
		var key = this.newKey(this.putQN);
		return this.newEdge(key, key, key, key);
	}

	@Override
	public H2QE newEdge(QN node) throws NullPointerException, IllegalArgumentException {
		var key = this.asQN(node).key;
		return this.newEdge(key, key, key, key);
	}

	@Override
	public H2QE newEdge(QN context, QN predicate, QN subject, QN object) throws NullPointerException, IllegalArgumentException {
		return this.newEdge(this.asQN(context).key, this.asQN(predicate).key, this.asQN(subject).key, this.asQN(object).key);
	}

	/** Diese Methode liefert eine neue {@link H2QE Hyperkante} mit den gegebenen Knotenkennungen. */
	public final H2QE newEdge(long context, long predicate, long subject, long object) {
		return new H2QE(this, context, predicate, subject, object);
	}

	@Override
	public H2QESet newEdges() {
		return this.newEdges(this.newEdge());
	}

	@Override
	public H2QESet newEdges(QN node) {
		return this.newEdges(this.newEdge(node));
	}

	@Override
	public H2QESet newEdges(QN context, QN predicate, QN subject, QN object) {
		return this.newEdges(this.newEdge(context, predicate, subject, object));
	}

	@Override
	public H2QESet newEdges(QE... edges) throws NullPointerException, IllegalArgumentException {
		return this.newEdges(Arrays.asList(edges));
	}

	@Override
	public H2QESet newEdges(Iterable<? extends QE> edges) throws NullPointerException, IllegalArgumentException {
		try {
			if (edges instanceof H2QESet) {
				var set = this.asQESet(edges);
				if (set instanceof H2QESet.Temp) return set;
				var res = new H2QESet.Temp(this);
				new H2QQ().push("INSERT INTO ").push(res.table).push(" SELECT * FROM (").push(set).push(")").update(this);
				return res;
			}
			var buf = new H2QESet.Temp(this);
			try (var stmt = new H2QQ().push("INSERT INTO ").push(buf.table).push(" (C, P, S, O) VALUES (?, ?, ?, ?)").prepare(this)) {
				for (Object item: edges) {
					var edge = this.asQE(item);
					stmt.setLong(1, edge.context);
					stmt.setLong(2, edge.predicate);
					stmt.setLong(3, edge.subject);
					stmt.setLong(4, edge.object);
					stmt.addBatch();
				}
				stmt.executeBatch();
			}
			var res = new H2QESet.Temp(this);
			new H2QQ().push("INSERT INTO ").push(res.table).push(" SELECT DISTINCT * FROM ").push(buf.table).update(this);
			return res;
		} catch (SQLException cause) {
			throw new IllegalStateException(cause);
		}
	}

	@Override
	public H2QN newNode() {
		return this.newNode(this.newKey(this.putQN));
	}

	@Override
	public H2QN newNode(Object value) {
		try {
			var string = this.asQV(value);
			var putStmt = this.putQV;
			putStmt.setString(1, string);
			this.markPutValue(putStmt.executeUpdate() != 0);
			var getStmt = this.getQN;
			getStmt.setString(1, string);
			var res = getStmt.executeQuery();
			if (res.next()) return this.newNode(res.getLong(1));
		} catch (SQLException cause) {
			throw new IllegalStateException(cause);
		}
		throw new IllegalStateException();
	}

	/** Diese Methode liefert einen neuen {@link H2QN Hyperknoten} mit der gegebenen Kennung. */
	public final H2QN newNode(long key) {
		return new H2QN(this, key);
	}

	@Override
	public H2QNSet newNodes(QN... nodes) throws NullPointerException, IllegalArgumentException {
		return this.newNodes(Arrays.asList(nodes));
	}

	@Override
	public H2QNSet newNodes(Iterable<? extends QN> nodes) throws NullPointerException, IllegalArgumentException {
		try {
			if (nodes instanceof H2QNSet) {
				var set = this.asQNSet(nodes);
				if (set instanceof H2QNSet.Temp) return set;
				H2QNSet res = new H2QNSet.Temp(this);
				new H2QQ().push("INSERT INTO ").push(res.table).push(" SELECT * FROM (").push(set).push(")").update(this);
				return res;
			}
			H2QNSet buf = new H2QNSet.Temp(this);
			try (var stmt = new H2QQ().push("INSERT INTO ").push(buf.table).push(" (N) VALUES (?)").prepare(this)) {
				for (Object item: nodes) {
					stmt.setLong(1, this.asQN(item).key);
					stmt.addBatch();
				}
				stmt.executeBatch();
			}
			H2QNSet res = new H2QNSet.Temp(this);
			new H2QQ().push("INSERT INTO ").push(res.table).push(" SELECT DISTINCT * FROM ").push(buf.table).update(this);
			return res;
		} catch (SQLException cause) {
			throw new IllegalStateException(cause);
		}
	}

	@Override
	public H2QVSet newValues(Object... values) throws NullPointerException, IllegalArgumentException {
		return this.newValues(Arrays.asList(values));
	}

	@Override
	public H2QVSet newValues(Iterable<?> values) throws NullPointerException, IllegalArgumentException {
		try {
			if (values instanceof H2QVSet) {
				var set = (H2QVSet)values;
				if (set.owner == this) {
					if (set instanceof H2QVSet.Temp) return set;
					var res = new H2QVSet.Temp(this);
					new H2QQ().push("INSERT INTO ").push(res.table).push(" SELECT V FROM (").push(set).push(")").update(this);
					return res;
				}
			}
			var buf = new H2QVSet.Temp(this);
			try (var stmt = new H2QQ().push("INSERT INTO ").push(buf.table).push(" (V) VALUES (?)").prepare(this)) {
				for (Object item: values) {
					stmt.setString(1, this.asQV(item));
					stmt.addBatch();
				}
				stmt.executeBatch();
			}
			var res = new H2QVSet.Temp(this);
			new H2QQ().push("INSERT INTO ").push(res.table).push(" SELECT DISTINCT * FROM ").push(buf.table).update(this);
			return res;
		} catch (SQLException cause) {
			throw new IllegalStateException(cause);
		}
	}

	@Override
	public H2QT newTuple(QN... nodes) throws NullPointerException, IllegalArgumentException {
		var size = nodes.length;
		var keys = new long[size];
		for (var i = 0; i < size; i++) {
			keys[i] = this.asQN(nodes[i]).key;
		}
		return this.newTuple(keys);
	}

	@Override
	public H2QT newTuple(List<? extends QN> nodes) throws NullPointerException, IllegalArgumentException {
		var size = nodes.size();
		var keys = new long[size];
		for (var i = 0; i < size; i++) {
			keys[i] = this.asQN(nodes.get(i)).key;
		}
		return this.newTuple(keys);
	}

	/** Diese Methode liefert ein neues {@link H2QT Hypertupel} mit den gegebenen Knotenkennungen. */
	public final H2QT newTuple(long[] keys) {
		return new H2QT(this, keys);
	}

	@Override
	public H2QTSet newTuples(List<String> names, QN... tuples) throws NullPointerException, IllegalArgumentException {
		return this.newTuples(new Names(names), null, tuples);
	}

	@Override
	public H2QTSet newTuples(List<String> names, Iterable<? extends QT> tuples) throws NullPointerException, IllegalArgumentException {
		if (tuples instanceof H2QTSet) {
			var set = this.asQTSet(tuples, names.size());
			if (set instanceof H2QTSet.Temp) return set.withNames(names);
			var res = new H2QTSet.Temp(this, new Names(names));
			new H2QQ().push("INSERT INTO ").push(res.table).push(" SELECT * FROM (").push(set).push(")").update(this);
			return res;
		}
		return this.newTuples(new Names(names), tuples, null);
	}

	@Override
	public Translator2<QN, String> valueTrans() {
		return this.valueTrans;
	}

	final PreparedStatement getQN;

	final PreparedStatement getQV;

	final PreparedStatement getQE;

	final PreparedStatement putQN;

	final PreparedStatement putQV;

	final PreparedStatement putQE;

	final PreparedStatement putQT;

	final PreparedStatement popQN;

	final PreparedStatement popQV;

	final PreparedStatement popQE;

	final H2QESet edges;

	final H2QNSet nodes;

	final H2QVSet values;

	final HashSet<String> tables = new HashSet<>();

	final HashMap<String, H2QIBag<?, ?>.Cache> cacheMap = new HashMap<>();

	final Translator2<QN, String> valueTrans = Translators.from(QN.class, String.class, QN::value, this::newNode).optionalize();

	Object putValueMark;

	Object popValueMark;

	Object putTable() {
		synchronized (this.tables) {
			var name = "QT" + this.newKey(this.putQT);
			this.tables.add(name);
			return new Object() {

				@Override
				protected void finalize() throws Throwable {
					H2QS.this.popTable(name);
				}

				@Override
				public String toString() {
					return name;
				}

			};
		}
	}

	/** Diese Methode entfernt die Tabelle mit dem gegebenen Namen, sofern dieser Name über {@link #putTable()} erzeugt wurde. */
	void popTable(String name) throws SQLException {
		synchronized (this.tables) {
			if (this.tables.remove(name)) {
				try (var stmt = this.conn.createStatement()) {
					stmt.executeUpdate("DROP TABLE IF EXISTS " + name);
				}
			}
		}
	}

	boolean markPutValue(boolean changed) {
		if (!changed) return false;
		this.putValueMark = new Object();
		return true;
	}

	boolean markPopValue(boolean changed) {
		if (!changed) return false;
		this.popValueMark = new Object();
		return true;
	}

	/** Diese Methode führt die gegebene Anfrage {@link PreparedStatement#executeQuery() aus} und gibt den {@link ResultSet#getLong(int) Zahlenwert} des ersten
	 * Ergebnisses zurück. */
	private long newKey(PreparedStatement stmt) throws NullPointerException, IllegalStateException {
		try (var rset = stmt.executeQuery()) {
			if (rset.next()) return rset.getLong(1);
		} catch (SQLException cause) {
			throw new IllegalStateException(cause);
		}
		throw new IllegalStateException();
	}

	private H2QTSet newTuples(Names names, Iterable<? extends QT> tuples1, QN[] tuples2) throws NullPointerException, IllegalArgumentException {
		try {
			var size = names.size();
			var buf = new H2QTSet.Temp(this, names);
			var qry = new H2QQ().push("INSERT INTO ").push(buf.table).push(" (C0");
			for (var i = 1; i < size; i++) {
				qry.push(", C").push(i);
			}
			qry.push(") VALUES (?");
			for (var i = 1; i < size; i++) {
				qry.push(", ?");
			}
			qry.push(")");
			try (var stmt = qry.prepare(this)) {
				if (tuples2 != null) {
					var count = tuples2.length;
					if ((count % size) != 0) throw new IllegalArgumentException();
					for (var r = 0; r < count; r += size) {
						for (var i = 0; i < size; i++) {
							stmt.setLong(i + 1, this.asQN(tuples2[r + i]).key);
						}
						stmt.addBatch();
					}
				} else {
					for (Object item: tuples1) {
						var keys = this.asQT(item).keys;
						if (keys.length != size) throw new IllegalArgumentException();
						for (var i = 0; i < size; i++) {
							stmt.setLong(i + 1, keys[i]);
						}
						stmt.addBatch();
					}
				}
				stmt.executeBatch();
			}
			var res = new H2QTSet.Temp(this, names);
			new H2QQ().push("INSERT INTO ").push(res.table).push(" SELECT DISTINCT * FROM ").push(buf.table).update(this);
			return res;
		} catch (SQLException cause) {
			throw new IllegalStateException(cause);
		}
	}

}
