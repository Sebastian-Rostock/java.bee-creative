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

	static class Set1 extends H2QESet {

		final Object set1;

		Set1(final H2QS owner, final String select, final Object set1) {
			super(owner, select);
			this.set1 = set1;
		}

	}

	static class Set2 extends Set1 {

		final Object set2;

		Set2(final H2QS owner, final String select, final Object set1, final Object set2) {
			super(owner, select, set1);
			this.set2 = set2;
		}

	}

	static class Save extends H2QESet {

		Save(final H2QS owner) {
			super(owner, "select * from QE");
		}

	}

	/** Diese Klasse implementiert ein {@link QESet} als temporäre {@link #index(String) indizierbare} Tabelle. */
	public static class Temp extends H2QESet {

		Temp(final H2QS owner) {
			super(owner, null);
			this.owner.exec("create table " + this.name + " (C int not null, P int not null, S int not null, O int not null)");
		}

		@Override
		public H2QESet copy() {
			return this;
		}

		/** Diese Methode erzeugt den Index über die gegebenen Spalten in der gegebenen Reihenfolge und gibt {@code this} zurück. Die Spaltenliste {@code cols} muss
		 * dazu aus den Zeichen {@code C}, {@code P}, {@code S} und {@code O} bestehen, welche für Kontext, Prädikat, Subjekt bzw. Objekt stehen. */
		public Temp index(final String cols) throws NullPointerException, IllegalArgumentException {
			if ((cols.length() != 4) || ((cols.indexOf('C') | cols.indexOf('P') | cols.indexOf('S') | cols.indexOf('O')) < 0)) throw new IllegalArgumentException();
			this.owner.exec("create index if not exists " + this.name + "_INDEX_" + cols + " on " + this.name + " (" + cols.charAt(0) + ", " + cols.charAt(1) + ", "
				+ cols.charAt(2) + ", " + cols.charAt(3) + ")");
			return this;
		}

		/** Diese Methode ist eine Abkürzung für {@link #index(String) this.index("CPSO").index("CPOS").index("CSPO").index("COPS")}. */
		public Temp index() {
			return this.index("CPSO").index("CPOS").index("CSPO").index("COPS");
		}

	}

	static class Order extends Set1 {

		Order(final H2QS owner, final String select, final Object set1) {
			super(owner, select, set1);

		}

		@Override
		public H2QESet order() {
			return this;
		}

	}

	/** Dieser Konstruktor initialisiert den Graphspeicher sowie die Anfrage des {@code VIEW} (oder {@code null}). */
	protected H2QESet(final H2QS owner, final String select) {
		super(owner, select);
	}

	@Override
	protected QE next(final ResultSet item) throws SQLException {
		return this.owner.newEdge(item.getInt(1), item.getInt(2), item.getInt(3), item.getInt(4));
	}

	@Override
	public boolean putAll() {
		return this.owner.exec("merge into QE select * from " + this.name);
	}

	@Override
	public boolean popAll() {
		return this.owner.exec("delete from QE A where exists (select 0 from " + this.name + " B where A.C=B.C and A.P=B.P and A.S=B.S and A.O=B.O)");
	}

	@Override
	public H2QNSet contexts() {
		return new H2QNSet.Set1(this.owner, "select distinct C N from " + this.name, this);
	}

	@Override
	public H2QNSet predicates() {
		return new H2QNSet.Set1(this.owner, "select distinct P N from " + this.name, this);
	}

	@Override
	public H2QNSet subjects() {
		return new H2QNSet.Set1(this.owner, "select distinct S N from " + this.name, this);
	}

	@Override
	public H2QNSet objects() {
		return new H2QNSet.Set1(this.owner, "select distinct O N from " + this.name, this);
	}

	@Override
	public QTSet tuples(final String context, final String predicate, final String subject, final String object)
		throws NullPointerException, IllegalArgumentException {
		return new H2QTSet.Set1(this.owner, new Names(context, predicate, subject, object), "select C C0, P C1, S C2, O C3 from " + this.name, this);
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
		return new Set1(this.owner, "select distinct " + key + " C, P, S, O from " + this.name, this);
	}

	@Override
	public H2QESet withContexts(final QNSet contexts) throws NullPointerException, IllegalArgumentException {
		final H2QNSet that = this.owner.asQNSet(contexts);
		return new Set2(this.owner, "select distinct B.N C, A.P, A.S, A.O from " + this.name + " A, " + that.name + " B", this, that);
	}

	@Override
	public H2QESet withPredicate(final QN predicate) throws NullPointerException, IllegalArgumentException {
		final int key = this.owner.asQN(predicate).key;
		return new Set1(this.owner, "select distinct C, " + key + " P, S, O from " + this.name, this);
	}

	@Override
	public H2QESet withPredicates(final QNSet predicates) throws NullPointerException, IllegalArgumentException {
		final H2QNSet that = this.owner.asQNSet(predicates);
		return new Set2(this.owner, "select distinct A.C, B.N P, A.S, A.O from " + this.name + " A, " + that.name + " B", this, that);
	}

	@Override
	public H2QESet withSubject(final QN subject) throws NullPointerException, IllegalArgumentException {
		final int key = this.owner.asQN(subject).key;
		return new Set1(this.owner, "select distinct C, P, " + key + " S, O from " + this.name, this);
	}

	@Override
	public H2QESet withSubjects(final QNSet subjects) throws NullPointerException, IllegalArgumentException {
		final H2QNSet that = this.owner.asQNSet(subjects);
		return new Set2(this.owner, "select distinct A.C, A.P, B.N S, A.O from " + this.name + " A, " + that.name + " B", this, that);
	}

	@Override
	public H2QESet withObject(final QN object) throws NullPointerException, IllegalArgumentException {
		final int key = this.owner.asQN(object).key;
		return new Set1(this.owner, "select distinct C, P, S, " + key + " O from " + this.name, this);
	}

	@Override
	public H2QESet withObjects(final QNSet objects) throws NullPointerException, IllegalArgumentException {
		final H2QNSet that = this.owner.asQNSet(objects);
		return new Set2(this.owner, "select distinct A.C, A.P, A.S, B.N O from " + this.name + " A, " + that.name + " B", this, that);
	}

	@Override
	public H2QESet havingNode(final QN node) throws NullPointerException, IllegalArgumentException {
		final int key = this.owner.asQN(node).key;
		return new Set1(this.owner, "select * from " + this.name + " where C=" + key + " or P=" + key + " or S=" + key + " or O=" + key, this);
	}

	@Override
	public H2QESet havingNodes(final QNSet nodes) throws NullPointerException, IllegalArgumentException {
		final H2QNSet that = this.owner.asQNSet(nodes);
		return new Set2(this.owner, "select * from " + this.name + " where exists (select N from " + that.name + " where C=N or P=N or S=N or O=N)", this, that);
	}

	@Override
	public H2QESet havingContext(final QN context) throws NullPointerException, IllegalArgumentException {
		final int key = this.owner.asQN(context).key;
		return new Set1(this.owner, "select * from " + this.name + " where C=" + key, this);
	}

	@Override
	public H2QESet havingContexts(final QNSet contexts) throws NullPointerException, IllegalArgumentException {
		final H2QNSet that = this.owner.asQNSet(contexts);
		return new Set2(this.owner, "select * from " + this.name + " where C in (select * from " + that.name + ")", this, that);
	}

	@Override
	public H2QESet havingPredicate(final QN predicate) throws NullPointerException, IllegalArgumentException {
		final int key = this.owner.asQN(predicate).key;
		return new Set1(this.owner, "select * from " + this.name + " where P=" + key, this);
	}

	@Override
	public H2QESet havingPredicates(final QNSet predicates) throws NullPointerException, IllegalArgumentException {
		final H2QNSet that = this.owner.asQNSet(predicates);
		final H2QNSet node = that;
		return new Set2(this.owner, "select * from " + this.name + " where P in (select * from " + node.name + ")", this, that);
	}

	@Override
	public H2QESet havingSubject(final QN subject) throws NullPointerException, IllegalArgumentException {
		final int key = this.owner.asQN(subject).key;
		return new Set1(this.owner, "select * from " + this.name + " where S=" + key, this);
	}

	@Override
	public H2QESet havingSubjects(final QNSet subjects) throws NullPointerException, IllegalArgumentException {
		final H2QNSet that = this.owner.asQNSet(subjects);
		return new Set2(this.owner, "select * from " + this.name + " where S in (select * from " + that.name + ")", this, that);
	}

	@Override
	public H2QESet havingObject(final QN object) throws NullPointerException, IllegalArgumentException {
		final int key = this.owner.asQN(object).key;
		return new Set1(this.owner, "select * from " + this.name + " where O=" + key, this);
	}

	@Override
	public H2QESet havingObjects(final QNSet objects) throws NullPointerException, IllegalArgumentException {
		final H2QNSet that = this.owner.asQNSet(objects);
		return new Set2(this.owner, "select * from " + this.name + " where O in (select * from " + that.name + ")", this, that);
	}

	@Override
	public H2QESet copy() {
		return this.owner.newEdges(this);
	}

	@Override
	public H2QESet order() {
		return new Order(this.owner, "select * from " + this.name + " order by C, P, S, O", this);
	}

	@Override
	public H2QESet union(final QESet set) throws NullPointerException, IllegalArgumentException {
		final H2QESet that = this.owner.asQESet(set);
		return new Set2(this.owner, "(select * from " + this.name + ") union (select * from " + that.name + ")", this, that);
	}

	@Override
	public H2QESet except(final QESet set) throws NullPointerException, IllegalArgumentException {
		final H2QESet that = this.owner.asQESet(set);
		return new Set2(this.owner, "(select * from " + this.name + ") except (select * from " + that.name + ")", this, that);
	}

	@Override
	public H2QESet intersect(final QESet set) throws NullPointerException, IllegalArgumentException {
		final H2QESet that = this.owner.asQESet(set);
		return new Set2(this.owner, "(select * from " + this.name + ") intersect (select * from " + that.name + ")", this, that);
	}

}
