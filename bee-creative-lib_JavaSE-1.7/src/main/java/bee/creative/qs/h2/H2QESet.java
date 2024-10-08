package bee.creative.qs.h2;

import java.sql.ResultSet;
import java.sql.SQLException;
import bee.creative.qs.QE;
import bee.creative.qs.QESet;
import bee.creative.qs.QN;
import bee.creative.qs.QNSet;
import bee.creative.qs.QTSet;
import bee.creative.qs.h2.H2QTSet.Names;
import bee.creative.util.Filter;
import bee.creative.util.Iterables;

/** Diese Klasse implementiert ein {@link QESet} als Sicht auf das ergebnis einer SQL-Anfrage.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class H2QESet extends H2QOSet<QE, QESet> implements QESet {

	static class Save extends H2QESet {

		Save(final H2QS owner) {
			super(owner, new H2QQ().push("select * from QE"));
		}

	}

	/** Diese Klasse implementiert ein {@link QESet} als temporäre {@link #index(String) indizierbare} Tabelle. */
	public static class Temp extends H2QESet {

		Temp(final H2QS owner) {
			super(owner, null);
			new H2QQ().push("create temporary table ").push(this.table).push(" (C int not null, P int not null, S int not null, O int not null)").update(owner);
		}

		@Override
		public Temp copy() {
			return this;
		}

		/** Diese Methode erzeugt den Index über die gegebenen Spalten in der gegebenen Reihenfolge und gibt {@code this} zurück. Die Spaltenliste {@code cols} muss
		 * dazu aus den Zeichen {@code C}, {@code P}, {@code S} und {@code O} bestehen, welche für Kontext, Prädikat, Subjekt bzw. Objekt stehen. */
		public Temp index(final String cols) throws NullPointerException, IllegalArgumentException {
			if ((cols.length() != 4) || ((cols.indexOf('C') | cols.indexOf('P') | cols.indexOf('S') | cols.indexOf('O')) < 0)) throw new IllegalArgumentException();
			new H2QQ().push("create index if not exists ").push(this.table).push("_INDEX_").push(cols).push(" on ").push(this.table).push(" (").push(cols.charAt(0))
				.push(", ").push(cols.charAt(1)).push(", ").push(cols.charAt(2)).push(", ").push(cols.charAt(3)).push(")").update(this.owner);
			return this;
		}

		/** Diese Methode ist eine Abkürzung für {@link #index(String) this.index("CPSO").index("CPOS").index("CSPO").index("COPS")}. */
		public Temp index() {
			return this.index("CPSO").index("CPOS").index("CSPO").index("COPS");
		}

	}

	static class Order extends H2QESet {

		Order(final H2QESet that) {
			super(that.owner, new H2QQ().push("select * from (").push(that).push(") order by C, P, S, O"));
		}

		@Override
		public H2QESet order() {
			return this;
		}

	}

	/** Dieser Konstruktor initialisiert den Graphspeicher sowie die Anfrage des {@code VIEW} (oder {@code null}). */
	protected H2QESet(final H2QS owner, final H2QQ select) {
		super(owner, select);
	}

	@Override
	protected QE next(final ResultSet item) throws SQLException {
		return this.owner.newEdge(item.getInt(1), item.getInt(2), item.getInt(3), item.getInt(4));
	}

	@Override
	public boolean putAll() {
		return new H2QQ().push("merge into QE select * from (").push(this).push(")").update(this.owner);
	}

	@Override
	public boolean popAll() {
		return new H2QQ().push("delete from QE A where exists (select 0 from (").push(this).push(") B where A.C=B.C and A.P=B.P and A.S=B.S and A.O=B.O)")
			.update(this.owner);
	}

	@Override
	public H2QNSet contexts() {
		return new H2QNSet(this.owner, new H2QQ().push("select distinct C N from (").push(this).push(")"));
	}

	@Override
	public H2QNSet predicates() {
		return new H2QNSet(this.owner, new H2QQ().push("select distinct P N from (").push(this).push(")"));
	}

	@Override
	public H2QNSet subjects() {
		return new H2QNSet(this.owner, new H2QQ().push("select distinct S N from (").push(this).push(")"));
	}

	@Override
	public H2QNSet objects() {
		return new H2QNSet(this.owner, new H2QQ().push("select distinct O N from (").push(this).push(")"));
	}

	@Override
	public QTSet tuples(final String context, final String predicate, final String subject, final String object)
		throws NullPointerException, IllegalArgumentException {
		return new H2QTSet(this.owner, new Names(context, predicate, subject, object),
			new H2QQ().push("select C C0, P C1, S C2, O C3 from (").push(this).push(")"));
	}

	@Override
	public H2QESet having(final Filter<? super QE> filter) throws NullPointerException {
		return this.owner.newEdges(Iterables.filter(this, filter));
	}

	@Override
	public H2QESet havingState(final boolean state) {
		return state ? this.intersect(this.owner.edges()) : this.except(this.owner.edges());
	}

	@Override
	public H2QESet withContext(final QN context) throws NullPointerException, IllegalArgumentException {
		final int key = this.owner.asQN(context).key;
		return new H2QESet(this.owner, new H2QQ().push("select distinct ").push(key).push(" C, P, S, O from (").push(this).push(")"));
	}

	@Override
	public H2QESet withContexts(final QNSet contexts) throws NullPointerException, IllegalArgumentException {
		final H2QNSet that = this.owner.asQNSet(contexts);
		return new H2QESet(this.owner, new H2QQ().push("select distinct B.N C, A.P, A.S, A.O from (").push(this).push(") A, (").push(that).push(") B"));
	}

	@Override
	public H2QESet withPredicate(final QN predicate) throws NullPointerException, IllegalArgumentException {
		final int key = this.owner.asQN(predicate).key;
		return new H2QESet(this.owner, new H2QQ().push("select distinct C, ").push(key).push(" P, S, O from (").push(this).push(")"));
	}

	@Override
	public H2QESet withPredicates(final QNSet predicates) throws NullPointerException, IllegalArgumentException {
		final H2QNSet that = this.owner.asQNSet(predicates);
		return new H2QESet(this.owner, new H2QQ().push("select distinct A.C, B.N P, A.S, A.O from (").push(this).push(") A, (").push(that).push(") B"));
	}

	@Override
	public H2QESet withSubject(final QN subject) throws NullPointerException, IllegalArgumentException {
		final int key = this.owner.asQN(subject).key;
		return new H2QESet(this.owner, new H2QQ().push("select distinct C, P, ").push(key).push(" S, O from (").push(this).push(")"));
	}

	@Override
	public H2QESet withSubjects(final QNSet subjects) throws NullPointerException, IllegalArgumentException {
		final H2QNSet that = this.owner.asQNSet(subjects);
		return new H2QESet(this.owner, new H2QQ().push("select distinct A.C, A.P, B.N S, A.O from (").push(this).push(") A, (").push(that).push(") B"));
	}

	@Override
	public H2QESet withObject(final QN object) throws NullPointerException, IllegalArgumentException {
		final int key = this.owner.asQN(object).key;
		return new H2QESet(this.owner, new H2QQ().push("select distinct C, P, S, ").push(key).push(" O from (").push(this).push(")"));
	}

	@Override
	public H2QESet withObjects(final QNSet objects) throws NullPointerException, IllegalArgumentException {
		final H2QNSet that = this.owner.asQNSet(objects);
		return new H2QESet(this.owner, new H2QQ().push("select distinct A.C, A.P, A.S, B.N O from (").push(this).push(") A, (").push(that).push(") B"));
	}

	@Override
	public H2QESet havingNode(final QN node) throws NullPointerException, IllegalArgumentException {
		final int key = this.owner.asQN(node).key;
		return new H2QESet(this.owner,
			new H2QQ().push("select * from (").push(this).push(") where C=").push(key).push(" or P=").push(key).push(" or S=").push(key).push(" or O=").push(key));
	}

	@Override
	public H2QESet havingNodes(final QNSet nodes) throws NullPointerException, IllegalArgumentException {
		final H2QNSet that = this.owner.asQNSet(nodes);
		return new H2QESet(this.owner,
			new H2QQ().push("select * from (").push(this).push(") where exists (select N from (").push(that).push(") where C=N or P=N or S=N or O=N)"));
	}

	@Override
	public H2QESet havingContext(final QN context) throws NullPointerException, IllegalArgumentException {
		final int key = this.owner.asQN(context).key;
		return new H2QESet(this.owner, new H2QQ().push("select * from (").push(this).push(") where C=").push(key));
	}

	@Override
	public H2QESet havingContexts(final QNSet contexts) throws NullPointerException, IllegalArgumentException {
		final H2QNSet that = this.owner.asQNSet(contexts);
		return new H2QESet(this.owner, new H2QQ().push("select * from (").push(this).push(") where C in (select * from (").push(that).push("))"));
	}

	@Override
	public H2QESet havingPredicate(final QN predicate) throws NullPointerException, IllegalArgumentException {
		final int key = this.owner.asQN(predicate).key;
		return new H2QESet(this.owner, new H2QQ().push("select * from (").push(this).push(") where P=").push(key));
	}

	@Override
	public H2QESet havingPredicates(final QNSet predicates) throws NullPointerException, IllegalArgumentException {
		final H2QNSet that = this.owner.asQNSet(predicates);
		return new H2QESet(this.owner, new H2QQ().push("select * from (").push(this).push(") where P in (select * from (").push(that).push("))"));
	}

	@Override
	public H2QESet havingSubject(final QN subject) throws NullPointerException, IllegalArgumentException {
		final int key = this.owner.asQN(subject).key;
		return new H2QESet(this.owner, new H2QQ().push("select * from (").push(this).push(") where S=").push(key));
	}

	@Override
	public H2QESet havingSubjects(final QNSet subjects) throws NullPointerException, IllegalArgumentException {
		final H2QNSet that = this.owner.asQNSet(subjects);
		return new H2QESet(this.owner, new H2QQ().push("select * from (").push(this).push(" where S in (select * from (").push(that).push("))"));
	}

	@Override
	public H2QESet havingObject(final QN object) throws NullPointerException, IllegalArgumentException {
		final int key = this.owner.asQN(object).key;
		return new H2QESet(this.owner, new H2QQ().push("select * from (").push(this).push(") where O=").push(key));
	}

	@Override
	public H2QESet havingObjects(final QNSet objects) throws NullPointerException, IllegalArgumentException {
		final H2QNSet that = this.owner.asQNSet(objects);
		return new H2QESet(this.owner, new H2QQ().push("select * from (").push(this).push(") where O in (select * from (").push(that).push("))"));
	}

	@Override
	public H2QESet.Temp copy() {
		return this.owner.newEdges(this);
	}

	@Override
	public H2QESet order() {
		return new Order(this);
	}

	@Override
	public H2QESet union(final QESet set) throws NullPointerException, IllegalArgumentException {
		final H2QESet that = this.owner.asQESet(set);
		return new H2QESet(this.owner, new H2QQ().push("(").push(this).push(") union (").push(that).push(")"));
	}

	@Override
	public H2QESet except(final QESet set) throws NullPointerException, IllegalArgumentException {
		final H2QESet that = this.owner.asQESet(set);
		return new H2QESet(this.owner, new H2QQ().push("(").push(this).push(") except (").push(that).push(")"));
	}

	@Override
	public H2QESet intersect(final QESet set) throws NullPointerException, IllegalArgumentException {
		final H2QESet that = this.owner.asQESet(set);
		return new H2QESet(this.owner, new H2QQ().push("(").push(this).push(") intersect (").push(that).push(")"));
	}

}
