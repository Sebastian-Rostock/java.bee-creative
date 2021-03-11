package bee.creative.qs.h2;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
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

	static class Iter extends H2QOIter<QT, H2QTSet> {

		public Iter(final H2QTSet owner) {
			super(owner);
		}

		@Override
		public QT next(final ResultSet item) throws SQLException {
			final int size = this.owner.names.size();
			final int[] ints = new int[size];
			for (int i = 0; i < size; i++) {
				ints[i] = item.getInt(i + 1);
			}
			return this.owner.owner.newQT(ints);
		}

	}

	static class Set1 extends H2QTSet {

		final Object set1;

		public Set1(final H2QS owner, final Names names, final String select, final Object set1) {
			super(owner, names, select);
			this.set1 = set1;
		}

	}

	static class Set2 extends Set1 {

		final Object set2;

		public Set2(final H2QS owner, final Names names, final String select, final Object set1, final Object set2) {
			super(owner, names, select, set1);
			this.set2 = set2;
		}

	}

	static class Temp extends H2QTSet {

		public Temp(final H2QS owner, final Names names) {
			super(owner, names, null);
			final int size = names.size();
			final StringBuilder sql = new StringBuilder(50 + (size * 18));
			sql.append("create cached temporary table ").append(this.name).append(" (");
			for (int i = 0; i < size; i++) {
				sql.append("C").append(i).append(" int not null, ");
			}
			sql.setLength(sql.length() - 2);
			sql.append(")");
			this.owner.exec(sql.toString());
		}

		@Override
		public H2QTSet copy() {
			return this;
		}

	}

	static class Order extends Set1 {

		public Order(final H2QS owner, final Names names, final String select, final Object set1) {
			super(owner, names, select, set1);
		}

		@Override
		public H2QTSet order() {
			return this;
		}

	}

	static class Names extends HashSet<String> implements Array<String> {

		private static final long serialVersionUID = 2586580774778548666L;

		public final List<String> list = new AbstractList<String>() {

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
			if (size != this.size()) throw new IllegalArgumentException();
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

	}

	final Names names;

	H2QTSet(final H2QS owner, final Names names, final String select) {
		super(owner, select);
		this.names = names;
	}

	@Override
	public H2QTSet copy() {
		return this.owner.newTuples(this, this.names());
	}

	@Override
	public H2QTSet order() {
		final int size = this.names.size();
		final StringBuilder sql = new StringBuilder(this.name.length() + 50 + (size * 5));
		sql.append("table ").append(this.name).append(" order by ");
		for (int i = 0; i < size; i++) {
			sql.append("C").append(i).append(", ");
		}
		sql.setLength(sql.length() - 2);
		return new Order(this.owner, this.names, sql.toString(), this);
	}

	@Override
	public H2QTSet union(final QTSet set) throws NullPointerException, IllegalArgumentException {
		final H2QTSet that = this.owner.asQTSet(set, this.names);
		return new Set2(this.owner, this.names, "(table " + this.name + ") union (table " + that.name + ")", this, that);
	}

	@Override
	public H2QTSet except(final QTSet set) throws NullPointerException, IllegalArgumentException {
		final H2QTSet that = this.owner.asQTSet(set, this.names);
		return new Set2(this.owner, this.names, "(table " + this.name + ") except (table " + that.name + ")", this, that);
	}

	@Override
	public H2QTSet intersect(final QTSet set) throws NullPointerException, IllegalArgumentException {
		final H2QTSet that = this.owner.asQTSet(set, this.names);
		return new Set2(this.owner, this.names, "(table " + this.name + ") intersect (table " + that.name + ")", this, that);
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
		return new H2QESet.Set1(this.owner, "select distinct C" + context + " C, C" + predicate + " P, C" + subject + "S, C" + object + " O from " + this.name,
			this);
	}

	@Override
	public H2QESet edges(final String context, final String predicate, final String subject, final String object)
		throws NullPointerException, IllegalArgumentException {
		return this.edges(this.role(context), this.role(predicate), this.role(subject), this.role(object));
	}

	@Override
	public H2QNSet nodes(final int role) throws IllegalArgumentException {
		this.name(role);
		return new H2QNSet.Set1(this.owner, "select distinct C" + role + " N from " + this.name, this);
	}

	@Override
	public H2QNSet nodes(final String name) throws NullPointerException, IllegalArgumentException {
		return this.nodes(this.role(name));
	}

	@Override
	public H2QTSet join(final QTSet set) throws NullPointerException, IllegalArgumentException {
		final H2QTSet that = this.owner.asQTSet(set); // TODO
		final Names names2 = that.names;
		final int size2 = names2.size();
		final int[] roles2 = this.roles(names2.list);
		final ArrayList<String> list = new ArrayList<>(this.names.size() + size2);
		list.addAll(this.names());
		for (int i = 0; i < size2; i++) {
			if (roles2[i] < 0) {
				list.add(names2.get(i));
			}
		}
		final Names names = new Names(list);
		final int size = this.names.size();
		final int size21 = roles2.length;
		final StringBuilder sql = new StringBuilder(this.name.length() + that.name.length() + 48 + (size * 8) + (size21 * 16));
		sql.append("(select A.C0");
		for (int i = 1; i < size; i++) {
			sql.append(", A.C").append(i);
		}
		for (int i = 0, j = size; i < size21; i++) {
			if (roles2[i] < 0) {
				sql.append(", B.C").append(i).append(" C").append(j++);
			}
		}
		sql.append(" from ").append(this.name).append(" A inner join ").append(that.name).append(" B on true");
		for (int i = 0; i < size21; i++) {
			if (roles2[i] >= 0) {
				sql.append(" and A.C").append(roles2[i]).append("=B.C").append(i);
			}
		}
		sql.append(")");
		return new Set2(this.owner, names, sql.toString(), this, that);
	}

	@Override
	public H2QTSet select(final int... roles) throws NullPointerException, IllegalArgumentException {
		final Names names = new Names(this.names(roles));
		final int size = names.size();
		final StringBuilder sql = new StringBuilder(this.name.length() + 50 + (size * 9));
		sql.append("select distinct ");
		for (int i = 0; i < size; i++) {
			sql.append("C").append(roles[i]).append(" C").append(i).append(", ");
		}
		sql.setLength(sql.length() - 2);
		sql.append(" from ").append(this.name);
		return new Set1(this.owner, names, sql.toString(), this);
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
		final int key = this.owner.asQN(node).key;
		final int size = this.names.size();
		final StringBuilder sql = new StringBuilder(this.name.length() + 30 + (size * 5));
		sql.append("select distinct ");
		for (int i = 0; i < role; i++) {
			sql.append("C").append(i).append(", ");
		}
		sql.append(key).append(" C").append(role).append(", ");
		for (int i = role + 1; i < size; i++) {
			sql.append("C").append(i).append(", ");
		}
		sql.setLength(sql.length() - 2);
		sql.append(" from ").append(this.name);
		return new Set1(this.owner, this.names, sql.toString(), this);
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
		final StringBuilder sql = new StringBuilder(this.name.length() + 30 + (size * 7));
		sql.append("select distinct ");
		for (int i = 0; i < role; i++) {
			sql.append("A.C").append(i).append(", ");
		}
		sql.append("B.N C").append(role).append(", ");
		for (int i = role + 1; i < size; i++) {
			sql.append("A.C").append(i).append(", ");
		}
		sql.setLength(sql.length() - 2);
		sql.append(" from ").append(this.name).append(" A, ").append(that.name).append(" B");
		return new Set2(this.owner, this.names, sql.toString(), this, that);
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
		final Names names2 = new Names(names);
		if (this.names.size() != names2.size()) throw new IllegalArgumentException();
		return new Set1(this.owner, names2, "table " + this.name, this);
	}

	@Override
	public H2QTSet having(final Filter<? super QT> filter) throws NullPointerException {
		return this.owner.newTuples(Iterables.filter(this, filter), this.names());
	}

	@Override
	public H2QTSet havingNode(final int role, final QN node) throws NullPointerException, IllegalArgumentException {
		this.name(role);
		final int key = this.owner.asQN(node).key;
		return new Set1(this.owner, this.names, "select * from " + this.name + " where C" + role + "=" + key, this);
	}

	@Override
	public H2QTSet havingNode(final String name, final QN node) throws NullPointerException, IllegalArgumentException {
		return this.havingNode(this.role(name), node);
	}

	@Override
	public H2QTSet havingNodes(final int role, final QNSet nodes) throws NullPointerException, IllegalArgumentException {
		this.name(role);
		final H2QTSet that = this.owner.asQTSet(nodes);
		return new Set2(this.owner, this.names, "select * from " + this.name + " where C" + role + " in (table " + that.name + ")", this, that);
	}

	@Override
	public H2QTSet havingNodes(final String name, final QNSet nodes) throws NullPointerException, IllegalArgumentException {
		return this.havingNodes(this.role(name), nodes);
	}

	@Override
	public Iterator<QT> iterator() {
		return new Iter(this);
	}

}
