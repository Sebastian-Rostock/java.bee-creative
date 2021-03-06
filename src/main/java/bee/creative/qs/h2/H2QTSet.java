package bee.creative.qs.h2;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import bee.creative.lang.Array;
import bee.creative.lang.Objects;
import bee.creative.qs.QN;
import bee.creative.qs.QNSet;
import bee.creative.qs.QT;
import bee.creative.qs.QTSet;
import bee.creative.util.AbstractHashData;
import bee.creative.util.Filter;
import bee.creative.util.HashMapOI;
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
			this.owner.execImpl(H2QQ.deleteTempNodes(this.key));
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

		public Names(final List<String> names) {
			final int size = names.size();
			this.allocate(size);
			for (int i = 0; i < size; i++) {
				this.putIndexImpl(Objects.notNull(names.get(i)));
			}
			if (size != this.size()) throw new IllegalArgumentException();
		}

		@Override
		public String get(final int index) {
			return customGetKey(index);
		}

		public int role(final String name) throws NullPointerException {
			return getIndexImpl(Objects.notNull(name));
		}

		public List<String> names() {
			return new AbstractList<String>() {

				@Override
				public String get(final int index) {
					return Names.this.get(index);
				}

				@Override
				public int size() {
					return Names.this.size();
				}

			};
		}

	}

	final Names names;

	H2QTSet(final H2QS owner, final String select, final Names names) {
		super(owner, select);
		this.names = names;
	}

	@Override
	public H2QTSet copy() {
		return this.owner.newTuples(this.names(), this);
	}

	@Override
	public QTSet order() {
		return new Order(this);
	}

	@Override
	public QTSet union(final QTSet set) throws NullPointerException, IllegalArgumentException {
		return new Set2(this.owner, H2QQ.selectUnion(this, this.owner.asQTSet(set,names)), names, this, set);
	}

	@Override
	public QTSet except(final QTSet set) throws NullPointerException, IllegalArgumentException {
		return new Set2(this.owner, H2QQ.selectExcept(this, this.owner.asQTSet(set, names)), names, this, set);
	}

	@Override
	public QTSet intersect(final QTSet set) throws NullPointerException, IllegalArgumentException {
		return new Set2(this.owner, H2QQ.selectIntersect(this, this.owner.asQTSet(set)), names, this, set);
	}

	@Override
	public int role(final String name) throws NullPointerException {
		return names.role(name);
	}

	@Override
	public int[] roles(final String... names) throws NullPointerException {
		return this.roles(Arrays.asList(names));
	}

	@Override
	public int[] roles(final List<String> names) throws NullPointerException {
		// TODO
		return null;
	}

	@Override
	public List<String> names() {
		return names.names();
	}

	@Override
	public QNSet nodes() {
		// TODO
		return null;
	}

	@Override
	public QNSet nodes(final int role) throws IllegalArgumentException {
		// TODO
		return null;
	}

	@Override
	public QNSet nodes(final String name) throws NullPointerException, IllegalArgumentException {
		return this.nodes(this.role(name));
	}

	@Override
	public H2QTSet join(final QTSet that) throws NullPointerException, IllegalArgumentException {
		// TODO
		return null;
	}

	@Override
	public H2QTSet join(final QTSet that, final int... roles) throws NullPointerException, IllegalArgumentException {
		// TODO
		return null;
	}

	@Override
	public H2QTSet join(final QTSet that, final String... names) throws NullPointerException, IllegalArgumentException {
		return this.join(that, this.roles(names));
	}

	@Override
	public H2QTSet join(final QTSet that, final List<String> names) throws NullPointerException, IllegalArgumentException {
		return this.join(that, this.roles(names));
	}

	@Override
	public H2QTSet select(final int... roles) throws NullPointerException, IllegalArgumentException {
		// TODO
		return null;
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
		// TODO
		return null;
	}

	@Override
	public H2QTSet withNode(final String name, final QN node) throws NullPointerException, IllegalArgumentException {
		return this.withNode(this.role(name), node);
	}

	@Override
	public H2QTSet withNodes(final int role, final QNSet nodes) throws NullPointerException, IllegalArgumentException {
		// TODO
		return null;
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
		// TODO
		return null;
	}

	@Override
	public H2QTSet having(final Filter<? super QT> filter) throws NullPointerException {
		return this.owner.newTuples(this.names(), Iterables.filter(this, filter));
	}

	@Override
	public H2QTSet havingNode(final QN node) throws NullPointerException, IllegalArgumentException {
		// TODO
		return null;
	}

	@Override
	public H2QTSet havingNode(final int role, final QN node) throws NullPointerException, IllegalArgumentException {
		// TODO
		return null;
	}

	@Override
	public H2QTSet havingNode(final String name, final QN node) throws NullPointerException, IllegalArgumentException {
		return this.havingNode(this.role(name), node);
	}

	@Override
	public H2QTSet havingNodes(final QNSet nodes) throws NullPointerException, IllegalArgumentException {
		// TODO
		return null;
	}

	@Override
	public H2QTSet havingNodes(final int role, final QNSet nodes) throws NullPointerException, IllegalArgumentException {
		// TODO
		return null;
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
