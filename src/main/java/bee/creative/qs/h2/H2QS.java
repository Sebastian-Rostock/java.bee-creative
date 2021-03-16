package bee.creative.qs.h2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import bee.creative.lang.Objects;
import bee.creative.qs.QE;
import bee.creative.qs.QN;
import bee.creative.qs.QO;
import bee.creative.qs.QS;
import bee.creative.qs.QT;
import bee.creative.qs.QTSet;
import bee.creative.qs.h2.H2QTSet.Names;

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
	protected final Connection conn;

	/** Dieses Feld speichert die in {@link #exec(String)} wiederverwendete Anweisung. */
	final Statement exec;

	final PreparedStatement selectSaveEdge;

	final PreparedStatement selectSaveNode;

	final PreparedStatement selectSaveValue;

	final PreparedStatement insertSaveEdge;

	final PreparedStatement insertSaveNode;

	final PreparedStatement deleteSaveEdge;

	final PreparedStatement deleteSaveEdges;

	final PreparedStatement deleteSaveNode;

	final PreparedStatement createNode;

	final PreparedStatement createTemp;

	/** Dieser Konstruktor initialisiert die Datenbankverbindung und erstellt bei Bedarf das Tabellenschema.
	 *
	 * @param conn Datenbankverbindung. */
	public H2QS(final Connection conn) throws SQLException, NullPointerException {
		this.conn = conn;
		this.exec = conn.createStatement();
		this.exec("" //
			+ "create table if not exists QN (N int not null, V varchar(1G) not null, primary key (N));" //
			+ "create table if not exists QE (C int not null, P int not null, S int not null, O int not null, primary key (C, P, S, O));" //
			+ "create index if not exists QE_INDEX_CPO on QE (C, P, O, S);" //
			+ "create index if not exists QE_INDEX_CSP on QE (C, S, P, O);" //
			+ "create index if not exists QE_INDEX_COP on QE (C, O, P, S);" //
			+ "create unique index if not exists QN_INDEX_V on QN (V);" //
			+ "create sequence if not exists QN_SEQUENCE minvalue 1 maxvalue 1000000000000000 nocycle;" //
			+ "create sequence if not exists QT_SEQUENCE minvalue 1 maxvalue 1000000000000000 cycle");
		this.selectSaveEdge = this.conn.prepareStatement("select top 1 * from QE where C=? and P=? and S=? and O=?");
		this.selectSaveNode = this.conn.prepareStatement("select N from QN where V=?");
		this.selectSaveValue = this.conn.prepareStatement("select V from QN where N=?");
		this.insertSaveEdge = this.conn.prepareStatement("merge into QE (C, P, S, O) values (?, ?, ?, ?)");
		this.insertSaveNode = this.conn.prepareStatement("insert into QN (N, V) values (?, ?)");
		this.deleteSaveEdge = this.conn.prepareStatement("delete from QE where C=? and P=? and S=? and O=?");
		this.deleteSaveEdges = this.conn.prepareStatement("delete from QE where C=?1 or P=?1 or S=?1 or O=?1");
		this.deleteSaveNode = this.conn.prepareStatement("delete from QN where N=?");
		this.createNode = this.conn.prepareStatement("select next value for QN_SEQUENCE");
		this.createTemp = this.conn.prepareStatement("select next value for QT_SEQUENCE");
	}

	/** Diese Methode liefert das gegebene Objekt als {@link H2QE} dieses {@link QO Graphspeichers} oder löst eine Ausnahme aus. */
	protected final H2QE asQE(final Object src) throws NullPointerException, IllegalArgumentException {
		try {
			final H2QE res = (H2QE)src;
			if (res.owner == this) return res;
		} catch (final ClassCastException cause) {}
		throw new IllegalArgumentException();
	}

	/** Diese Methode liefert das gegebene Objekt als {@link H2QESet} dieses {@link QO Graphspeichers} oder löst eine Ausnahme aus. */
	protected final H2QESet asQESet(final Object src) throws NullPointerException, IllegalArgumentException {
		try {
			final H2QESet res = (H2QESet)src;
			if (res.owner == this) return res;
		} catch (final ClassCastException cause) {}
		throw new IllegalArgumentException();
	}

	/** Diese Methode liefert das gegebene Objekt als {@link H2QN} dieses {@link QO Graphspeichers} oder löst eine Ausnahme aus. */
	protected final H2QN asQN(final Object src) throws NullPointerException, IllegalArgumentException {
		try {
			final H2QN res = (H2QN)src;
			if (res.owner == this) return res;
		} catch (final ClassCastException cause) {}
		throw new IllegalArgumentException();
	}

	/** Diese Methode liefert das gegebene Objekt als {@link H2QNSet} dieses {@link QO Graphspeichers} oder löst eine Ausnahme aus. */
	protected final H2QNSet asQNSet(final Object src) throws NullPointerException, IllegalArgumentException {
		try {
			final H2QNSet res = (H2QNSet)src;
			if (res.owner == this) return res;
		} catch (final ClassCastException cause) {}
		throw new IllegalArgumentException();
	}

	/** Diese Methode liefert die {@link Object#toString() Textdarstellung} des gegebenen Objekts oder löst eine Ausnahme aus. */
	protected final String asQV(final Object src) throws NullPointerException {
		return src.toString();
	}

	/** Diese Methode liefert das gegebene Objekt als {@link H2QVSet} dieses {@link QO Graphspeichers} oder löst eine Ausnahme aus. */
	protected final H2QVSet asQVSet(final Object src) throws NullPointerException, IllegalArgumentException {
		try {
			final H2QVSet res = (H2QVSet)src;
			if (res.owner == this) return res;
		} catch (final ClassCastException cause) {}
		throw new IllegalArgumentException();
	}

	/** Diese Methode liefert das gegebene Objekt als {@link H2QT} dieses {@link QO Graphspeichers} oder löst eine Ausnahme aus. */
	protected final H2QT asQT(final Object src) throws NullPointerException, IllegalArgumentException {
		try {
			final H2QT res = (H2QT)src;
			if (res.owner == this) return res;
		} catch (final ClassCastException cause) {}
		throw new IllegalArgumentException();
	}

	/** Diese Methode liefert das gegebene Objekt als {@link H2QTSet} dieses {@link QO Graphspeichers} oder löst eine Ausnahme aus. */
	protected final H2QTSet asQTSet(final Object src) throws NullPointerException, IllegalArgumentException {
		try {
			final H2QTSet res = (H2QTSet)src;
			if (res.owner == this) return res;
		} catch (final ClassCastException cause) {}
		throw new IllegalArgumentException();
	}

	/** Diese Methode liefert das gegebene Objekt als {@link H2QTSet} dieses {@link QO Graphspeichers} mit der gegebene Anzahl an {@link QTSet#names() Rollen}
	 * oder löst eine Ausnahme aus. */
	protected final H2QTSet asQTSet(final Object src, final int roles) throws NullPointerException, IllegalArgumentException {
		final H2QTSet res = this.asQTSet(src);
		if (res.names.size() == roles) return res;
		throw new IllegalArgumentException();
	}

	/** Diese Methode liefert das gegebene Objekt als {@link H2QTSet} dieses {@link QO Graphspeichers} oder löst eine Ausnahme aus. Das {@link H2QTSet} muss die
	 * gegebenen {@link QTSet#names() Rollennamen} besitzen. */
	protected final H2QTSet asQTSet(final Object src, final List<?> names) throws NullPointerException, IllegalArgumentException {
		final H2QTSet res = this.asQTSet(src, names.size());
		if (res.names().equals(names)) return res;
		throw new IllegalArgumentException();
	}

	/** Diese Methode führt die gegebene Anfrage {@link Statement#executeUpdate(String) aus} und gibt nur dann {@code true} zurück, wenn dadurch Tabellenzeilen
	 * verändert wurden. */
	protected final boolean exec(final String query) throws IllegalStateException {
		try {
			return this.exec.executeUpdate(query) != 0;
		} catch (final SQLException cause) {
			throw new IllegalStateException(cause);
		}
	}

	/** Diese Methode leert den Graphspeicher. */
	public void reset() throws IllegalStateException {
		this.exec("delete from QN;delete from QE;alter sequence QN_SEQUENCE restart with 1");
	}

	/** Diese Methode entfernt alle Hyperknoten mit Textwert, die nich in Hyperkanten verwendet werden. */
	public void compact() {
		final H2QESet edges = this.edges();
		this.nodes().except(edges.contexts().union(edges.predicates()).union(edges.subjects()).union(edges.objects())).popAll();
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

	/** Diese Methode führt die gegebene Anfrage {@link PreparedStatement#executeQuery() aus} und gibt den {@link ResultSet#getInt(int) Zahlenwert} des ersten
	 * Ergebnisses zurück. */
	final int newKey(final PreparedStatement stmt) throws NullPointerException, IllegalStateException {
		try (final ResultSet rset = stmt.executeQuery()) {
			if (rset.next()) return rset.getInt(1);
		} catch (final SQLException cause) {
			throw new IllegalStateException(cause);
		}
		throw new IllegalStateException();
	}

	/** Diese Methode liefert eine neue {@link H2QE Hyperkante} mit den gegebenen Knotenkennungen. */
	protected final H2QE newEdge(final int context, final int predicate, final int subject, final int object) {
		return new H2QE(this, context, predicate, subject, object);
	}

	@Override
	public H2QE newEdge() {
		final int key = this.newKey(this.createNode);
		return this.newEdge(key, key, key, key);
	}

	@Override
	public H2QE newEdge(final QN node) throws NullPointerException, IllegalArgumentException {
		final int key = this.asQN(node).key;
		return this.newEdge(key, key, key, key);
	}

	@Override
	public H2QE newEdge(final QN context, final QN predicate, final QN subject, final QN object) throws NullPointerException, IllegalArgumentException {
		return this.newEdge(this.asQN(context).key, this.asQN(predicate).key, this.asQN(subject).key, this.asQN(object).key);
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
				if (set instanceof H2QESet.Temp) return set;
				final H2QESet.Temp res = new H2QESet.Temp(this);
				this.exec("insert into " + res.name + " select * from " + set.name);
				return res.index();
			}
			final H2QESet.Temp buf = new H2QESet.Temp(this);
			try (final PreparedStatement stmt = this.conn.prepareStatement("insert into " + buf.name + " (C, P, S, O) values (?, ?, ?, ?)")) {
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
			final H2QESet.Temp res = new H2QESet.Temp(this);
			this.exec("insert into " + res.name + " select distinct * from " + buf.name);
			return res.index();
		} catch (final SQLException cause) {
			throw new IllegalStateException(cause);
		}
	}

	/** Diese Methode liefert einen neuen {@link H2QN Hyperknoten} mit der gegebenen Kennung. */
	protected final H2QN newNode(final int key) {
		return new H2QN(this, key);
	}

	@Override
	public H2QN newNode() {
		return this.newNode(this.newKey(this.createNode));
	}

	@Override
	public H2QN newNode(final Object value) {
		try {
			final String string = this.asQV(value);
			final PreparedStatement stmt = this.selectSaveNode;
			stmt.setString(1, string);
			final ResultSet res = stmt.executeQuery();
			if (res.next()) return this.newNode(res.getInt(1));
			final PreparedStatement stmt2 = this.insertSaveNode;
			final int key = this.newKey(this.createNode);
			stmt2.setInt(1, key);
			stmt2.setString(2, string);
			stmt2.executeUpdate();
			return this.newNode(key);
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
				if (set instanceof H2QNSet.Temp) return set;
				final H2QNSet.Temp res = new H2QNSet.Temp(this);
				this.exec("insert into " + res.name + " select * from " + set.name);
				return res.index();
			}
			final H2QNSet.Temp buf = new H2QNSet.Temp(this);
			try (final PreparedStatement stmt = this.conn.prepareStatement("insert into " + buf.name + " (N) values (?)")) {
				for (final Object item: nodes) {
					stmt.setInt(1, this.asQN(item).key);
					stmt.addBatch();
				}
				stmt.executeBatch();
			}
			final H2QNSet.Temp res = new H2QNSet.Temp(this);
			this.exec("insert into " + res.name + " select distinct * from " + buf.name);
			return res.index();
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
					if (set instanceof H2QVSet.Temp) return set;
					final H2QVSet.Temp res = new H2QVSet.Temp(this);
					this.exec("insert into " + res.name + " select V from " + set.name);
					return res.index();
				}
			}
			final H2QVSet.Temp buf = new H2QVSet.Temp(this);
			try (final PreparedStatement stmt = this.conn.prepareStatement("insert into " + buf.name + " (V) values (?)")) {
				for (final Object item: values) {
					stmt.setString(1, this.asQV(item));
					stmt.addBatch();
				}
				stmt.executeBatch();
			}
			final H2QVSet.Temp res = new H2QVSet.Temp(this);
			this.exec("insert into " + res.name + " select distinct * from " + buf.name);
			return res.index();
		} catch (final SQLException cause) {
			throw new IllegalStateException(cause);
		}
	}

	/** Diese Methode liefert ein neues {@link H2QT Hypertupel} mit den gegebenen Knotenkennungen. */
	protected final H2QT newTuple(final int[] keys) {
		return new H2QT(this, keys);
	}

	@Override
	public H2QT newTuple(final QN... nodes) throws NullPointerException, IllegalArgumentException {
		return this.newTupleImpl(nodes);
	}

	@Override
	public H2QT newTuple(final List<? extends QN> nodes) throws NullPointerException, IllegalArgumentException {
		return this.newTupleImpl(nodes.toArray());
	}

	private H2QT newTupleImpl(final Object[] nodes) {
		final int size = nodes.length;
		final int[] keys = new int[size];
		for (int i = 0; i < size; i++) {
			keys[i] = this.asQN(nodes[i]).key;
		}
		return this.newTuple(keys);
	}

	@Override
	public H2QTSet newTuples(final List<String> names, final QN... tuples) throws NullPointerException, IllegalArgumentException {
		return this.newTuplesImpl(new Names(names), null, tuples);
	}

	@Override
	public H2QTSet newTuples(final List<String> names, final Iterable<? extends QT> tuples) throws NullPointerException, IllegalArgumentException {
		if (tuples instanceof H2QTSet) {
			final H2QTSet set = this.asQTSet(tuples, names.size());
			if (set instanceof H2QTSet.Temp) return set.withNames(names);
			final H2QTSet.Temp res = new H2QTSet.Temp(this, new Names(names));
			this.exec("insert into " + res.name + " select * from " + set.name);
			return res;
		}
		return this.newTuplesImpl(new Names(names), tuples, null);
	}

	private H2QTSet newTuplesImpl(final Names names, final Iterable<? extends QT> tuples1, final QN[] tuples2)
		throws NullPointerException, IllegalArgumentException {
		try {
			final int size = names.size();
			final H2QTSet.Temp buf = new H2QTSet.Temp(this, names);
			final StringBuilder sql = new StringBuilder(35 + (size * 8));
			sql.append("insert into ").append(buf.name).append(" (");
			for (int i = 0; i < size; i++) {
				sql.append("C").append(i).append(", ");
			}
			sql.setLength(sql.length() - 2);
			sql.append(") values (");
			for (int i = 0; i < size; i++) {
				sql.append("?, ");
			}
			sql.setLength(sql.length() - 2);
			sql.append(")");
			try (final PreparedStatement stmt = this.conn.prepareStatement(sql.toString())) {
				if (tuples2 != null) {
					final int count = tuples2.length;
					if ((count % size) != 0) throw new IllegalArgumentException();
					for (int r = 0; r < count; r += size) {
						for (int i = 0; i < size; i++) {
							stmt.setInt(i + 1, this.asQN(tuples2[r + i]).key);
						}
						stmt.addBatch();
					}
				} else {
					for (final Object item: tuples1) {
						final int[] keys = this.asQT(item).keys;
						if (keys.length != size) throw new IllegalArgumentException();
						for (int i = 0; i < size; i++) {
							stmt.setInt(i + 1, keys[i]);
						}
						stmt.addBatch();
					}
				}
				stmt.executeBatch();
			}
			final H2QTSet.Temp res = new H2QTSet.Temp(this, names);
			this.exec("insert into " + res.name + " select distinct * from " + buf.name);
			return res;
		} catch (final SQLException cause) {
			throw new IllegalStateException(cause);
		}
	}

	@Override
	public String toString() {
		return Objects.toStringCall(false, true, this, "edges", this.edges().size(), "values", this.values().size());
	}

}
