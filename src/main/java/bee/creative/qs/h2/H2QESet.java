package bee.creative.qs.h2;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import bee.creative.qs.QE;
import bee.creative.qs.QESet;
import bee.creative.qs.QN;
import bee.creative.qs.QNSet;

/** Diese Klasse implementiert ein {@link QESet} als Sicht auf das ergebnis einer SQL-Anfrage.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class H2QESet extends H2QXSet<QE, QESet> implements QESet {

	static class Iter extends H2QXIter<QE> {

		public Iter(final H2QESet owner) {
			super(owner);
		}

		@Override
		public QE next(final ResultSet item) throws SQLException {
			return new H2QE(this.owner.owner, item.getInt(1), item.getInt(2), item.getInt(3), item.getInt(4));
		}

	}

	static class Set1 extends H2QESet {

		final Object set1;

		public Set1(final H2QS owner, final String select, final Object set1) {
			super(owner, select);
			this.set1 = set1;
		}

	}

	static class Set2 extends Set1 {

		final Object set2;

		public Set2(final H2QS owner, final String select, final Object set1, final Object set2) {
			super(owner, select, set1);
			this.set2 = set2;
		}

	}

	static class Save extends H2QESet {

		public Save(final H2QS owner) {
			super(owner, H2QS.selectEdges());
		}

	}

	static class Copy extends H2QESet {

		final int key;

		public Copy(final H2QS owner, final int key) {
			super(owner, H2QS.selectEdgesCopy(key));
			this.key = key;
		}

		@Override
		protected void finalize() throws Throwable {
			this.owner.updateImpl(H2QS.deleteEdges(this.key));
		}

		@Override
		public H2QESet copy() {
			return this;
		}

	}

	static class Order extends Set1 {

		public Order(final H2QESet set) {
			super(set.owner, H2QS.selectEdgesOrder(set), set);
		}

		@Override
		public H2QESet order() {
			return this;
		}

	}

	H2QESet(final H2QS owner, final String select) {
		super(owner, select);
	}

	@Override
	public boolean putAll() {
		return this.owner.putAllImpl(this);
	}

	@Override
	public boolean popAll() {
		return this.owner.popAllImpl(this);
	}

	@Override
	public H2QNSet nodes() {
		return this.contexts().union(this.predicates()).union(this.subjects()).union(this.objects());
	}

	@Override
	public H2QNSet contexts() {
		return new H2QNSet.Set1(this.owner, H2QS.selectEdgesContexts(this), this);
	}

	@Override
	public H2QNSet predicates() {
		return new H2QNSet.Set1(this.owner, H2QS.selectEdgesPredicates(this), this);
	}

	@Override
	public H2QNSet subjects() {
		return new H2QNSet.Set1(this.owner, H2QS.selectEdgesSubjects(this), this);
	}

	@Override
	public H2QNSet objects() {
		return new H2QNSet.Set1(this.owner, H2QS.selectEdgesObjects(this), this);
	}

	@Override
	public H2QESet havingState(final boolean state) {
		return state ? this.intersect(this.owner.edges()) : this.except(this.owner.edges());
	}

	@Override
	public H2QESet withContext(final QN context) throws NullPointerException, IllegalArgumentException {
		return new Set1(this.owner, H2QS.selectEdgesWithContext(this, this.owner.asQN(context)), this);
	}

	@Override
	public H2QESet withContexts(final QNSet contexts) throws NullPointerException, IllegalArgumentException {
		return new Set2(this.owner, H2QS.selectEdgesWithContexts(this, this.owner.asQNSet(contexts)), this, contexts);
	}

	@Override
	public H2QESet withPredicate(final QN predicate) throws NullPointerException, IllegalArgumentException {
		return new Set1(this.owner, H2QS.selectEdgesWithPredicate(this, this.owner.asQN(predicate)), this);
	}

	@Override
	public H2QESet withPredicates(final QNSet predicates) throws NullPointerException, IllegalArgumentException {
		return new Set2(this.owner, H2QS.selectEdgesWithPredicates(this, this.owner.asQNSet(predicates)), this, predicates);
	}

	@Override
	public H2QESet withSubject(final QN subject) throws NullPointerException, IllegalArgumentException {
		return new Set1(this.owner, H2QS.selectEdgesWithSubject(this, this.owner.asQN(subject)), this);
	}

	@Override
	public H2QESet withSubjects(final QNSet subjects) throws NullPointerException, IllegalArgumentException {
		return new Set2(this.owner, H2QS.selectEdgesWithSubjects(this, this.owner.asQNSet(subjects)), this, subjects);
	}

	@Override
	public H2QESet withObject(final QN object) throws NullPointerException, IllegalArgumentException {
		return new Set1(this.owner, H2QS.selectEdgesWithObject(this, this.owner.asQN(object)), this);
	}

	@Override
	public H2QESet withObjects(final QNSet objects) throws NullPointerException, IllegalArgumentException {
		return new Set2(this.owner, H2QS.selectEdgesWithObjects(this, this.owner.asQNSet(objects)), this, objects);
	}

	@Override
	public H2QESet havingNode(final QN node) throws NullPointerException, IllegalArgumentException {
		return this.havingContext(node).union(this.havingPredicate(node)).union(this.havingSubject(node)).union(this.havingObject(node));
	}

	@Override
	public H2QESet havingNodes(final QNSet nodes) throws NullPointerException, IllegalArgumentException {
		return this.havingContexts(nodes).union(this.havingPredicates(nodes)).union(this.havingSubjects(nodes)).union(this.havingObjects(nodes));
	}

	@Override
	public H2QESet havingContext(final QN context) throws NullPointerException, IllegalArgumentException {
		return new Set1(this.owner, H2QS.selectEdgesHavingContext(this, this.owner.asQN(context)), this);
	}

	@Override
	public H2QESet havingContexts(final QNSet contexts) throws NullPointerException, IllegalArgumentException {
		return new Set2(this.owner, H2QS.selectEdgesHavingContexts(this, this.owner.asQNSet(contexts)), this, contexts);
	}

	@Override
	public H2QESet havingPredicate(final QN predicate) throws NullPointerException, IllegalArgumentException {
		return new Set1(this.owner, H2QS.selectEdgesHavingPredicate(this, this.owner.asQN(predicate)), this);
	}

	@Override
	public H2QESet havingPredicates(final QNSet predicates) throws NullPointerException, IllegalArgumentException {
		return new Set2(this.owner, H2QS.selectEdgesHavingPredicates(this, this.owner.asQNSet(predicates)), this, predicates);
	}

	@Override
	public H2QESet havingSubject(final QN subject) throws NullPointerException, IllegalArgumentException {
		return new Set1(this.owner, H2QS.selectEdgesHavingSubject(this, this.owner.asQN(subject)), this);
	}

	@Override
	public H2QESet havingSubjects(final QNSet subjects) throws NullPointerException, IllegalArgumentException {
		return new Set2(this.owner, H2QS.selectEdgesHavingSubjects(this, this.owner.asQNSet(subjects)), this, subjects);
	}

	@Override
	public H2QESet havingObject(final QN object) throws NullPointerException, IllegalArgumentException {
		return new Set1(this.owner, H2QS.selectEdgesHavingObject(this, this.owner.asQN(object)), this);
	}

	@Override
	public H2QESet havingObjects(final QNSet objects) throws NullPointerException, IllegalArgumentException {
		return new Set2(this.owner, H2QS.selectEdgesHavingObjects(this, this.owner.asQNSet(objects)), this, objects);
	}

	@Override
	public H2QESet copy() {
		return this.owner.newEdges(this);
	}

	@Override
	public H2QESet order() {
		return new Order(this);
	}

	@Override
	public H2QESet union(final QESet set) throws NullPointerException, IllegalArgumentException {
		return new Set2(this.owner, H2QS.selectUnion(this, this.owner.asQESet(set)), this, set);
	}

	@Override
	public H2QESet except(final QESet set) throws NullPointerException, IllegalArgumentException {
		return new Set2(this.owner, H2QS.selectExcept(this, this.owner.asQESet(set)), this, set);
	}

	@Override
	public H2QESet intersect(final QESet set) throws NullPointerException, IllegalArgumentException {
		return new Set2(this.owner, H2QS.selectIntersect(this, this.owner.asQESet(set)), this, set);
	}

	@Override
	public Iterator<QE> iterator() {
		return new Iter(this);
	}

}
