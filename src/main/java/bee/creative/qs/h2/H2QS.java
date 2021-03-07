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
import bee.creative.util.Getter;
import bee.creative.util.Iterable2;
import bee.creative.util.Iterables;

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

	final PreparedStatement createSaveNodeKey;

	final PreparedStatement createTempEdgesKey;

	final PreparedStatement createTempNodesKey;

	final PreparedStatement createTempValuesKey;

	final PreparedStatement createTempTuplesKey;

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
		this.createSaveNodeKey = this.conn.prepareStatement(H2QQ.createSaveNodeKey());
		this.createTempEdgesKey = this.conn.prepareStatement(H2QQ.createTempEdgesKey());
		this.createTempNodesKey = this.conn.prepareStatement(H2QQ.createTempNodesKey());
		this.createTempValuesKey = this.conn.prepareStatement(H2QQ.createTempValuesKey());
		this.createTempTuplesKey = this.conn.prepareStatement(H2QQ.createTempTuplesKey());
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

	/** Diese Methode liefert das gegebene Objekt als {@link H2QTSet} dieses {@link QO Graphspeichers} oder löst eine Ausnahme aus. Das {@link H2QTSet} muss die
	 * gegebene Anzahl an {@link QTSet#names() Rollennamen} besitzen. */
	protected final H2QTSet asQTSet(final Object src, final int size) throws NullPointerException, IllegalArgumentException {
		final H2QTSet res = this.asQTSet(src);
		if (res.names.size() == size) return res;
		throw new IllegalArgumentException();
	}

	/** Diese Methode liefert das gegebene Objekt als {@link H2QTSet} dieses {@link QO Graphspeichers} oder löst eine Ausnahme aus. Das {@link H2QTSet} muss die
	 * gegebenen {@link QTSet#names() Rollennamen} besitzen. */
	protected final H2QTSet asQTSet(final Object src, final Names names) throws NullPointerException, IllegalArgumentException {
		final H2QTSet res = this.asQTSet(src, names.size());
		if (names.names.equals(res.names.names)) return res;
		throw new IllegalArgumentException();
	}

	protected final H2QE newQE(final int context, final int predicate, final int subject, final int object) {
		return new H2QE(this, context, predicate, subject, object);
	}

	protected final H2QN newQN(final int key) {
		return new H2QN(this, key);
	}

	protected final H2QT newQT(final int... keys) {
		return new H2QT(this, keys);
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
		final int key = this.keyImpl(this.createSaveNodeKey);
		return this.newQE(key, key, key, key);
	}

	@Override
	public H2QE newEdge(final QN node) throws NullPointerException, IllegalArgumentException {
		final int key = this.asQN(node).key;
		return this.newQE(key, key, key, key);
	}

	@Override
	public H2QE newEdge(final QN context, final QN predicate, final QN subject, final QN object) throws NullPointerException, IllegalArgumentException {
		return this.newQE(this.asQN(context).key, this.asQN(predicate).key, this.asQN(subject).key, this.asQN(object).key);
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
		return this.newQN(this.keyImpl(this.createSaveNodeKey));
	}

	@Override
	public H2QN newNode(final Object value) {
		try {
			final String string = this.asQV(value);
			final PreparedStatement stmt = this.selectSaveNode;
			stmt.setString(1, string);
			final ResultSet res = stmt.executeQuery();
			if (res.next()) return this.newQN(res.getInt(1));
			final PreparedStatement stmt2 = this.insertSaveNode;
			final int key = this.keyImpl(this.createSaveNodeKey);
			stmt2.setInt(1, key);
			stmt2.setString(2, string);
			stmt2.executeUpdate();
			return this.newQN(key);
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

	final H2QT newTuple(final Object[] nodes) throws NullPointerException, IllegalArgumentException {
		return this.newQT(this.toNodeKeys(nodes));
	}

	@Override
	public H2QT newTuple(final QN... nodes) throws NullPointerException, IllegalArgumentException {
		return this.newTuple((Object[])nodes);
	}

	@Override
	public H2QT newTuple(final Iterable<? extends QN> nodes) throws NullPointerException, IllegalArgumentException {
		return this.newTuple(Iterables.toArray(nodes));
	}

	@Override
	public H2QTSet newTuples(QN[][] tuples, String... names) throws NullPointerException, IllegalArgumentException {
		return newTuples(tuples, Arrays.asList(names));
	}

	@Override
	public H2QTSet newTuples(final QN[][] tuples, final List<String> names) throws NullPointerException, IllegalArgumentException {
		return this.newTuplesImpl(new Names(names), Iterables.translate(Arrays.asList(tuples), new Getter<QN[], int[]>() {

			@Override
			public int[] get(final QN[] item) {
				return H2QS.this.toNodeKeys(item);
			}

		}));
	}

	@Override
	public H2QTSet newTuples(Iterable<? extends QT> tuples, String... names) throws NullPointerException, IllegalArgumentException {
		return newTuples(tuples, Arrays.asList(names));
	}

	@Override
	public H2QTSet newTuples(final Iterable<? extends QT> tuples, final List<String> names) throws NullPointerException, IllegalArgumentException {
		Names names2 = new Names(names);
		int size = names2.size();
		if (tuples instanceof H2QTSet) {
			H2QTSet set = asQTSet(tuples, size);
			final int key = this.keyImpl(this.createTempTuplesKey);
			this.execImpl(H2QQ.createTempTuples(key, size));
			this.execImpl(H2QQ.insertTempTuples(key, size, set));
			return new H2QTSet.Temp(this, names2, key);

		}
		// TODO kopie konstruktor mit spaltenerkennung

		return this.newTuplesImpl(new Names(names), Iterables.translate(tuples, new Getter<QT, int[]>() {

			@Override
			public int[] get(final QT item) {
				return H2QS.this.asQT(item).keys;
			}

		}));
	}

	int[] toNodeKeys(final Object[] nodes) {
		final int size = nodes.length;
		final int[] keys = new int[size];
		for (int i = 0; i < size; i++) {
			keys[i] = this.asQN(nodes[i]).key;
		}
		return keys;
	}

	H2QTSet newTuplesImpl(final Names names, final Iterable<int[]> tuples) throws NullPointerException, IllegalArgumentException {
		try {
			int size = names.size();
			final int key2 = this.keyImpl(this.createTempTuplesKey);
			this.execImpl(H2QQ.createTempTuples(key2, size));
			try (final PreparedStatement stmt = this.conn.prepareStatement(H2QQ.insertTempTuples(key2, size))) {
				for (final int[] item: tuples) {
					if (item.length != size) throw new IllegalArgumentException();
					for (int i = 0; i < size; i++)
						stmt.setInt(i + 1, item[i]);
					stmt.addBatch();
				}
				stmt.executeBatch();
			}
			final int key = this.keyImpl(this.createTempTuplesKey);
			this.execImpl(H2QQ.createTempTuples(key, size));
			this.execImpl(H2QQ.insertTempTuples(key, size, key2));
			this.execImpl(H2QQ.deleteTempTuples(key2));
			return new H2QTSet.Temp(this, names, key);
		} catch (final SQLException cause) {
			throw new IllegalStateException(cause);
		}
	}

	@Override
	public String toString() {
		return Objects.toStringCall(false, true, this, "edges", this.edges().size(), "values", this.values().size());
	}

}
