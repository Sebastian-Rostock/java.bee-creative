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

		public Set1(final H2QS owner, final String select, final Names names, final Object set1) {
			super(owner, select, names);
			this.set1 = set1;
		}

	}

	static class Set2 extends Set1 {

		final Object set2;

		public Set2(final H2QS owner, final String select, final Names names, final Object set1, final Object set2) {
			super(owner, select, names, set1);
			this.set2 = set2;
		}

	}

	static class Temp extends H2QTSet {

		final int key;

		public Temp(final H2QS owner, final Names names, final int key) {
			super(owner, H2QQ.selectTempTuples(key, names.size()), names);
			this.key = key;
		}

		@Override
		protected void finalize() throws Throwable {
			this.owner.execImpl(H2QQ.deleteTempTuples(this.key));
		}

		@Override
		public H2QTSet copy() {
			return this;
		}

	}

	static class Order extends Set1 {

		public Order(final H2QTSet set) {
			super(set.owner, H2QQ.selectTuplesOrder(set, set.names.size()), set.names, set);
		}

		@Override
		public H2QTSet order() {
			return this;
		}

	}

	static class Names extends HashSet<String> implements Array<String> {

		private static final long serialVersionUID = 2586580774778548666L;

		public final List<String> names = new AbstractList<String>() {

			@Override
			public String get(final int index) {
				return Names.this.get(index);
			}

			@Override
			public int size() {
				return Names.this.size();
			}

		};

		public Names(final List<String> names) throws NullPointerException, IllegalArgumentException {
			final int size = names.size();
			this.allocate(size);
			for (int i = 0; i < size; i++) {
				this.putIndexImpl(Objects.notNull(names.get(i)));
			}
			if (size != this.size()) throw new IllegalArgumentException();
		}

		public Names(final Names names, final int[] roles) throws NullPointerException, IllegalArgumentException {
			final int size = roles.length;
			this.allocate(size);
			for (int i = 0; i < size; i++) {
				try {
					this.putIndexImpl(names.get(roles[i]));
				} catch (final IndexOutOfBoundsException cause) {
					throw new IllegalArgumentException(cause);
				}
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

	}

	final Names names;

	H2QTSet(final H2QS owner, final String select, final Names names) {
		super(owner, select);
		this.names = names;
	}

	@Override
	public H2QTSet copy() {
		return this.owner.newTuples(this, this.names());
	}

	@Override
	public H2QTSet order() {
		return new Order(this);
	}

	@Override
	public H2QTSet union(final QTSet set) throws NullPointerException, IllegalArgumentException {
		return new Set2(this.owner, H2QQ.selectUnion(this, this.owner.asQTSet(set, this.names)), this.names, this, set);
	}

	@Override
	public H2QTSet except(final QTSet set) throws NullPointerException, IllegalArgumentException {
		return new Set2(this.owner, H2QQ.selectExcept(this, this.owner.asQTSet(set, this.names)), this.names, this, set);
	}

	@Override
	public H2QTSet intersect(final QTSet set) throws NullPointerException, IllegalArgumentException {
		return new Set2(this.owner, H2QQ.selectIntersect(this, this.owner.asQTSet(set, this.names)), this.names, this, set);
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
	public List<String> names() {
		return this.names.names;
	}

	@Override
	public H2QNSet nodes() {
		// TODO
		return null;
	}

	@Override
	public H2QNSet nodes(final int role) throws IllegalArgumentException {
		// TODO
		return null;
	}

	@Override
	public H2QNSet nodes(final String name) throws NullPointerException, IllegalArgumentException {
		return this.nodes(this.role(name));
	}

	@Override
	public H2QTSet join(final QTSet that) throws NullPointerException, IllegalArgumentException {
		final H2QTSet set2 = this.owner.asQTSet(that);
		final Names names2 = set2.names;
		final int size2 = names2.size();
		final int[] roles2 = this.roles(names2.names);
		final ArrayList<String> list = new ArrayList<>(this.names.size() + size2);
		list.addAll(this.names());
		for (int i = 0; i < size2; i++) {
			if (roles2[i] < 0) {
				list.add(names2.get(i));
			}
		}
		final Names names = new Names(list);
		return new Set2(this.owner, H2QQ.selectTuplesJoin(this, this.names.size(), set2, roles2), names, this, that);
	}

	@Override
	public H2QTSet select(final int... roles) throws NullPointerException, IllegalArgumentException {
		final int size = roles.length;
		if (size == 0) throw new IllegalArgumentException();
		final Names names = new Names(this.names, roles);
		return new Set1(this.owner, H2QQ.selectTuplesSelect(this, roles), names, this);
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
		if ((role < 0) || (role >= this.names.size())) throw new IllegalArgumentException();
		return new Set1(this.owner, H2QQ.selectTuplesWithNode(this, this.owner.asQN(node), role), this.names, this);
	}

	@Override
	public H2QTSet withNode(final String name, final QN node) throws NullPointerException, IllegalArgumentException {
		return this.withNode(this.role(name), node);
	}

	@Override
	public H2QTSet withNodes(final int role, final QNSet nodes) throws NullPointerException, IllegalArgumentException {
		if ((role < 0) || (role >= this.names.size())) throw new IllegalArgumentException();
		return new Set2(this.owner, H2QQ.selectTuplesWithNodes(this, this.owner.asQTSet(nodes), role), this.names, this, nodes);
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
		return new Set1(this.owner, this.select, names2, this);
	}

	@Override
	public H2QTSet having(final Filter<? super QT> filter) throws NullPointerException {
		return this.owner.newTuples(Iterables.filter(this, filter), this.names());
	}

	@Override
	public H2QTSet havingNode(final QN node) throws NullPointerException, IllegalArgumentException {
		return new Set1(this.owner, H2QQ.selectTuplesHavingNode(this, this.owner.asQN(node)), this.names, this);
	}

	@Override
	public H2QTSet havingNode(final int role, final QN node) throws NullPointerException, IllegalArgumentException {
		if ((role < 0) || (role >= this.names.size())) throw new IllegalArgumentException();
		return new Set1(this.owner, H2QQ.selectTuplesHavingNode(this, this.owner.asQN(node), role), this.names, this);
	}

	@Override
	public H2QTSet havingNode(final String name, final QN node) throws NullPointerException, IllegalArgumentException {
		return this.havingNode(this.role(name), node);
	}

	@Override
	public H2QTSet havingNodes(final QNSet nodes) throws NullPointerException, IllegalArgumentException {
		return new Set2(this.owner, H2QQ.selectTuplesHavingNodes(this, this.owner.asQTSet(nodes)), this.names, this, nodes);
	}

	@Override
	public H2QTSet havingNodes(final int role, final QNSet nodes) throws NullPointerException, IllegalArgumentException {
		if ((role < 0) || (role >= this.names.size())) throw new IllegalArgumentException();
		return new Set2(this.owner, H2QQ.selectTuplesHavingNodes(this, this.owner.asQTSet(nodes), role), this.names, this, nodes);
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
