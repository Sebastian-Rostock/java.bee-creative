package bee.creative.qs.h2;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import bee.creative.lang.Array;
import bee.creative.lang.Objects;
import bee.creative.qs.QN;
import bee.creative.qs.QNSet;
import bee.creative.qs.QT;
import bee.creative.qs.QTSet;
import bee.creative.util.Filter;
import bee.creative.util.HashSet;
import bee.creative.util.Iterables;

/** Diese Klasse implementiert ein {@link QTSet} als Sicht auf das ergebnis einer SQL-Anfrage.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class H2QTSet extends H2QOSet<QT, QTSet> implements QTSet {

	@Override
	public H2QTSet copy() {
		return this.owner.newTuples(this.names(), this);
	}

	@Override
	public H2QTSet copy(final Filter<? super QT> filter) throws NullPointerException {
		return this.owner.newTuples(this.names(), Iterables.filter(this, filter));
	}

	@Override
	public H2QTSet order() {
		return new Order(this.owner, this.names);
	}

	@Override
	public H2QTSet index() {
		return this.index(this.names());
	}

	/** Diese Methode indiziert diese Menge bezüglich der gegebenen Rollen in der gegebenen Reihenfolge und gibt {@code this} zurück. */
	public H2QTSet index(final int... roles) throws NullPointerException, IllegalArgumentException {
		return this.copy().index(roles);
	}

	/** Diese Methode ist eine Abkürzung für {@link #index(int...) this.index(this.roles(names))}.
	 *
	 * @see #roles(String...) */
	public H2QTSet index(final String... names) throws NullPointerException, IllegalArgumentException {
		return this.index(this.roles(names));
	}

	/** Diese Methode ist eine Abkürzung für {@link #index(int...) this.index(this.roles(names))}.
	 *
	 * @see #roles(List) */
	public H2QTSet index(final List<String> names) throws NullPointerException, IllegalArgumentException {
		return this.index(this.roles(names));
	}

	@Override
	public H2QTSet union(final QTSet set) throws NullPointerException, IllegalArgumentException {
		final H2QTSet that = this.owner.asQTSet(set, this.names());
		return new H2QTSet(this.owner, this.names, new H2QQ().push("(").push(this).push(") UNION (").push(that).push(")"));
	}

	@Override
	public H2QTSet except(final QTSet set) throws NullPointerException, IllegalArgumentException {
		final H2QTSet that = this.owner.asQTSet(set, this.names());
		return new H2QTSet(this.owner, this.names, new H2QQ().push("(").push(this).push(") EXCEPT (").push(that).push(")"));
	}

	@Override
	public H2QTSet intersect(final QTSet set) throws NullPointerException, IllegalArgumentException {
		final H2QTSet that = this.owner.asQTSet(set, this.names());
		return new H2QTSet(this.owner, this.names, new H2QQ().push("(").push(this).push(") INTERSECT (").push(that).push(")"));
	}

	@Override
	public int role(final String name) throws NullPointerException {
		return this.names.role(name);
	}

	@Override
	public int[] roles(final String... names) throws NullPointerException {
		return this.roles(Arrays.asList(names));
	}

	@Override
	public int[] roles(final List<String> names) throws NullPointerException {
		final int size = names.size();
		final int[] roles = new int[size];
		for (int i = 0; i < size; i++) {
			roles[i] = this.role(names.get(i));
		}
		return roles;
	}

	@Override
	public String name(final int role) throws IllegalArgumentException {
		return this.names.name(role);
	}

	@Override
	public List<String> names() {
		return this.names.list;
	}

	@Override
	public String[] names(final int... roles) throws IllegalArgumentException {
		final int size = roles.length;
		final String[] names = new String[size];
		for (int i = 0; i < size; i++) {
			names[i] = this.name(roles[i]);
		}
		return names;
	}

	@Override
	public H2QESet edges(final int context, final int predicate, final int subject, final int object) throws NullPointerException, IllegalArgumentException {
		this.name(context);
		this.name(predicate);
		this.name(subject);
		this.name(object);
		return new H2QESet(this.owner, new H2QQ().push("SELECT DISTINCT C").push(context).push(" AS C, C").push(predicate).push(" AS P, C").push(subject)
			.push("AS S, C").push(object).push(" AS O FROM (").push(this).push(")"));
	}

	@Override
	public H2QESet edges(final String context, final String predicate, final String subject, final String object)
		throws NullPointerException, IllegalArgumentException {
		return this.edges(this.role(context), this.role(predicate), this.role(subject), this.role(object));
	}

	@Override
	public H2QNSet nodes(final int role) throws IllegalArgumentException {
		this.name(role);
		return new H2QNSet(this.owner, new H2QQ().push("SELECT DISTINCT C").push(role).push(" AS N FROM (").push(this).push(")"));
	}

	@Override
	public H2QNSet nodes(final String name) throws NullPointerException, IllegalArgumentException {
		return this.nodes(this.role(name));
	}

	@Override
	public H2QTSet join(final QTSet set) throws NullPointerException, IllegalArgumentException {
		final H2QTSet that = this.owner.asQTSet(set);
		final int[] roles = this.roles(that.names.list);
		final int size1 = this.names.size();
		final int size2 = roles.length;
		final ArrayList<String> list = new ArrayList<>(size1 + size2);
		list.addAll(this.names.list);
		for (int i2 = 0; i2 < size2; i2++) {
			if (roles[i2] < 0) { // add new from that
				list.add(that.names.name(i2));
			}
		}
		final Names names = new Names(list);
		final H2QQ qry = new H2QQ().push("(SELECT A.C0");
		for (int i = 1; i < size1; i++) {
			qry.push(", A.C").push(i); // add all from this
		}
		for (int i = size1, i2 = 0; i2 < size2; i2++) {
			if (roles[i2] < 0) { // add new from that
				qry.push(", B.C").push(i2).push(" AS C").push(i++);
			}
		}
		qry.push(" FROM (").push(this).push(") AS A INNER JOIN (").push(that).push(") AS B ON TRUE");
		for (int i2 = 0; i2 < size2; i2++) {
			if (roles[i2] >= 0) {
				qry.push(" AND A.C").push(roles[i2]).push("=B.C").push(i2);
			}
		}
		qry.push(")");
		return new H2QTSet(this.owner, names, qry);
	}

	@Override
	public H2QTSet select(final int... roles) throws NullPointerException, IllegalArgumentException {
		final Names names = new Names(this.names(roles));
		final int size = names.size();
		final H2QQ qry = new H2QQ().push("SELECT DISTINCT C").push(roles[0]).push(" AS C0");
		for (int i = 1; i < size; i++) {
			qry.push(", C").push(roles[i]).push(" AS C").push(i);
		}
		qry.push(" FROM (").push(this).push(")");
		return new H2QTSet(this.owner, names, qry);
	}

	@Override
	public H2QTSet select(final String... names) throws NullPointerException, IllegalArgumentException {
		return this.select(this.roles(names));
	}

	@Override
	public H2QTSet select(final List<String> names) throws NullPointerException, IllegalArgumentException {
		return this.select(this.roles(names));
	}

	@Override
	public H2QTSet withNode(final int role, final QN node) throws NullPointerException, IllegalArgumentException {
		this.name(role);
		final Long key = this.owner.asQN(node).key;
		final int size = this.names.size();
		final H2QQ qry = new H2QQ().push(role != 0 ? "SELECT DISTINCT C0" : "SELECT DISTINCT ");
		for (int i = 1; i < role; i++) {
			qry.push(", C").push(i);
		}
		if (role == 0) {
			qry.push(key).push(" AS C0");
		} else {
			qry.push(", ").push(key).push(" AS C").push(role);
		}
		for (int i = role + 1; i < size; i++) {
			qry.push(", C").push(i);
		}
		qry.push(" FROM (").push(this).push(")");
		return new H2QTSet(this.owner, this.names, qry);
	}

	@Override
	public H2QTSet withNode(final String name, final QN node) throws NullPointerException, IllegalArgumentException {
		return this.withNode(this.role(name), node);
	}

	@Override
	public H2QTSet withNodes(final int role, final QNSet nodes) throws NullPointerException, IllegalArgumentException {
		this.name(role);
		final H2QTSet that = this.owner.asQTSet(nodes);
		final int size = this.names.size();
		final H2QQ qry = new H2QQ().push(role != 0 ? "SELECT DISTINCT A.C0" : "SELECT DISTINCT B.N AS C0");
		for (int i = 1; i < role; i++) {
			qry.push(", A.C").push(i);
		}
		if (role != 0) {
			qry.push(", B.N AS C").push(role);
		}
		for (int i = role + 1; i < size; i++) {
			qry.push(", A.C").push(i);
		}
		qry.push(" FROM (").push(this).push(") AS A, (").push(that).push(") AS B");
		return new H2QTSet(this.owner, this.names, qry);
	}

	@Override
	public H2QTSet withNodes(final String name, final QNSet nodes) throws NullPointerException, IllegalArgumentException {
		return this.withNodes(this.role(name), nodes);
	}

	@Override
	public H2QTSet withNames(final String... names) throws NullPointerException, IllegalArgumentException {
		return this.withNames(Arrays.asList(names));
	}

	@Override
	public H2QTSet withNames(final List<String> names) throws NullPointerException, IllegalArgumentException {
		if (this.names().equals(names)) return this;
		final Names names2 = new Names(names);
		if (this.names.size() != names2.size()) throw new IllegalArgumentException();
		return new H2QTSet(this.owner, names2, this.table);
	}

	@Override
	public H2QTSet havingNode(final int role, final QN node) throws NullPointerException, IllegalArgumentException {
		this.name(role);
		final Long key = this.owner.asQN(node).key;
		return new H2QTSet(this.owner, this.names, new H2QQ().push("SELECT * FROM (").push(this).push(") WHERE C").push(role).push("=").push(key));
	}

	@Override
	public H2QTSet havingNode(final String name, final QN node) throws NullPointerException, IllegalArgumentException {
		return this.havingNode(this.role(name), node);
	}

	@Override
	public H2QTSet havingNodes(final int role, final QNSet nodes) throws NullPointerException, IllegalArgumentException {
		this.name(role);
		final H2QTSet that = this.owner.asQTSet(nodes);
		return new H2QTSet(this.owner, this.names, new H2QQ().push("SELECT * FROM (").push(this).push(") WHERE C").push(role).push(" IN (").push(that).push(")"));
	}

	@Override
	public H2QTSet havingNodes(final String name, final QNSet nodes) throws NullPointerException, IllegalArgumentException {
		return this.havingNodes(this.role(name), nodes);
	}

	/** Dieser Konstruktor initialisiert {@link #owner Graphspeicher} und {@link #table Tabelle}. Wenn letztre {@code null} ist, wird sie über
	 * {@link H2QQ#H2QQ(H2QS)} erzeugt. Die Tabelle muss die Spalten {@code (C0 BIGINT NOT NULL, C1 BIGINT NOT NULL, ...)} besitzen. */
	protected H2QTSet(final H2QS owner, final Names names, final H2QQ select) {
		super(owner, select);
		this.names = names;
	}

	@Override
	protected QT customItem(final ResultSet item) throws SQLException {
		final int size = this.names.size();
		final long[] keys = new long[size];
		for (int i = 0; i < size; i++) {
			keys[i] = item.getLong(i + 1);
		}
		return this.owner.newTuple(keys);
	}

	private final Names names;

	static class Temp extends H2QTSet {

		public Temp(final H2QS owner, final Names names) {
			super(owner, names, null);
			final int size = names.size();
			final H2QQ qry = new H2QQ().push("CREATE TEMPORARY TABLE ").push(this.table).push(" (C0 BIGINT NOT NULL");
			for (int i = 1; i < size; i++) {
				qry.push(", C").push(i).push(" BIGINT NOT NULL");
			}
			qry.push(")").update(owner);
		}

		@Override
		public H2QTSet copy() {
			return this;
		}

		@Override
		public H2QTSet index(final int... roles) throws NullPointerException, IllegalArgumentException {
			if (roles.length == 0) return this;
			final int size = new Names(this.names(roles)).size();
			final H2QQ qry = new H2QQ().push("CREATE INDEX IF NOT EXISTS ").push(this.table).push("_INDEX_");
			for (int i = 0; i < size; i++) {
				qry.push("C").push(roles[i]);
			}
			qry.push(" ON ").push(this.table).push(" (C").push(roles[0]);
			for (int i = 1; i < size; i++) {
				qry.push(", C").push(roles[i]);
			}
			qry.push(")").update(this.owner);
			return this;
		}

	}

	static class Order extends H2QTSet {

		public Order(final H2QS owner, final Names names) {
			super(owner, names, new H2QQ());
			final int size = names.size();
			final H2QQ qry = this.table.push("SELECT * FROM (").push(this).push(") ORDER BY C0");
			for (int i = 1; i < size; i++) {
				qry.push(", C").push(i);
			}
		}

		@Override
		public H2QTSet order() {
			return this;
		}

	}

	static class Names extends HashSet<String> implements Array<String> {

		public final List<String> list = new AbstractList<>() {

			@Override
			public String get(final int index) {
				return Names.this.get(index);
			}

			@Override
			public int size() {
				return Names.this.size();
			}

		};

		public Names(final String... names) throws NullPointerException, IllegalArgumentException {
			this(Arrays.asList(names));
		}

		public Names(final List<String> names) throws NullPointerException, IllegalArgumentException {
			final int size = names.size();
			this.allocate(size);
			for (int i = 0; i < size; i++) {
				this.putIndexImpl(Objects.notNull(names.get(i)));
			}
			if ((size == 0) || (size != this.size())) throw new IllegalArgumentException();
		}

		@Override
		public String get(final int index) {
			return this.customGetKey(index);
		}

		public int role(final String name) throws NullPointerException {
			return this.getIndexImpl(Objects.notNull(name));
		}

		public String name(final int index) throws IllegalArgumentException {
			try {
				return this.get(index);
			} catch (final IndexOutOfBoundsException cause) {
				throw new IllegalArgumentException(cause);
			}
		}

		private static final long serialVersionUID = 2586580774778548666L;

	}

}
