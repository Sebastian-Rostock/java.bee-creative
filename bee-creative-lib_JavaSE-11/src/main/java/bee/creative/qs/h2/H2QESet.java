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

	@Override
	public boolean putAll() {
		return new H2QQ().push("MERGE INTO QE SELECT * FROM (").push(this.index()).push(")").update(this.owner);
	}

	@Override
	public boolean popAll() {
		return new H2QQ().push("DELETE FROM QE AS A WHERE EXISTS (").push(this).push(" AS B WHERE A.C=B.C AND A.P=B.P AND A.S=B.S AND A.O=B.O)")
			.update(this.owner);
	}

	@Override
	public H2QNSet contexts() {
		return new H2QNSet(this.owner, new H2QQ().push("SELECT DISTINCT C AS N FROM (").push(this).push(")"));
	}

	@Override
	public H2QNSet predicates() {
		return new H2QNSet(this.owner, new H2QQ().push("SELECT DISTINCT P AS N FROM (").push(this).push(")"));
	}

	@Override
	public H2QNSet subjects() {
		return new H2QNSet(this.owner, new H2QQ().push("SELECT DISTINCT S AS N FROM (").push(this).push(")"));
	}

	@Override
	public H2QNSet objects() {
		return new H2QNSet(this.owner, new H2QQ().push("SELECT DISTINCT O AS N FROM (").push(this).push(")"));
	}

	@Override
	public QTSet tuples(final String context, final String predicate, final String subject, final String object)
		throws NullPointerException, IllegalArgumentException {
		return new H2QTSet(this.owner, new Names(context, predicate, subject, object),
			new H2QQ().push("SELECT C AS C0, P AS C1, S AS C2, O AS C3 FROM (").push(this).push(")"));
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
		final long key = this.owner.asQN(context).key;
		return new H2QESet(this.owner, new H2QQ().push("SELECT DISTINCT ").push(key).push(" C, P, S, O FROM (").push(this).push(")"));
	}

	@Override
	public H2QESet withContexts(final QNSet contexts) throws NullPointerException, IllegalArgumentException {
		final H2QNSet that = this.owner.asQNSet(contexts);
		return new H2QESet(this.owner, new H2QQ().push("SELECT DISTINCT B.N AS C, A.P, A.S, A.O FROM (").push(this).push(") AS A, (").push(that).push(") AS B"));
	}

	@Override
	public H2QESet withPredicate(final QN predicate) throws NullPointerException, IllegalArgumentException {
		final Long key = this.owner.asQN(predicate).key;
		return new H2QESet(this.owner, new H2QQ().push("SELECT DISTINCT C, ").push(key).push(" AS P, S, O FROM (").push(this).push(")"));
	}

	@Override
	public H2QESet withPredicates(final QNSet predicates) throws NullPointerException, IllegalArgumentException {
		final H2QNSet that = this.owner.asQNSet(predicates);
		return new H2QESet(this.owner, new H2QQ().push("SELECT DISTINCT A.C, B.N AS P, A.S, A.O FROM (").push(this).push(") AS A, (").push(that).push(") AS B"));
	}

	@Override
	public H2QESet withSubject(final QN subject) throws NullPointerException, IllegalArgumentException {
		final Long key = this.owner.asQN(subject).key;
		return new H2QESet(this.owner, new H2QQ().push("SELECT DISTINCT C, P, ").push(key).push(" AS S, O FROM (").push(this).push(")"));
	}

	@Override
	public H2QESet withSubjects(final QNSet subjects) throws NullPointerException, IllegalArgumentException {
		final H2QNSet that = this.owner.asQNSet(subjects);
		return new H2QESet(this.owner, new H2QQ().push("SELECT DISTINCT A.C, A.P, B.N AS S, A.O FROM (").push(this).push(") AS A, (").push(that).push(") AS B"));
	}

	@Override
	public H2QESet withObject(final QN object) throws NullPointerException, IllegalArgumentException {
		final Long key = this.owner.asQN(object).key;
		return new H2QESet(this.owner, new H2QQ().push("SELECT DISTINCT C, P, S, ").push(key).push(" AS O FROM (").push(this).push(")"));
	}

	@Override
	public H2QESet withObjects(final QNSet objects) throws NullPointerException, IllegalArgumentException {
		final H2QNSet that = this.owner.asQNSet(objects);
		return new H2QESet(this.owner, new H2QQ().push("SELECT DISTINCT A.C, A.P, A.S, B.N AS O FROM (").push(this).push(") AS A, (").push(that).push(") AS B"));
	}

	@Override
	public H2QESet havingNode(final QN node) throws NullPointerException, IllegalArgumentException {
		final Long key = this.owner.asQN(node).key;
		return new H2QESet(this.owner,
			new H2QQ().push("SELECT * FROM (").push(this).push(") WHERE C=").push(key).push(" OR P=").push(key).push(" OR S=").push(key).push(" OR O=").push(key));
	}

	@Override
	public H2QESet havingNodes(final QNSet nodes) throws NullPointerException, IllegalArgumentException {
		final H2QNSet that = this.owner.asQNSet(nodes);
		return new H2QESet(this.owner,
			new H2QQ().push("SELECT * FROM (").push(this).push(") WHERE EXISTS (SELECT N FROM (").push(that).push(") WHERE C=N OR P=N OR S=N OR O=N)"));
	}

	@Override
	public H2QESet havingContext(final QN context) throws NullPointerException, IllegalArgumentException {
		final Long key = this.owner.asQN(context).key;
		return new H2QESet(this.owner, new H2QQ().push("SELECT * FROM (").push(this).push(") WHERE C=").push(key));
	}

	@Override
	public H2QESet havingContexts(final QNSet contexts) throws NullPointerException, IllegalArgumentException {
		final H2QNSet that = this.owner.asQNSet(contexts);
		return new H2QESet(this.owner, new H2QQ().push("SELECT * FROM (").push(this).push(") WHERE C IN (SELECT N FROM (").push(that).push("))"));
	}

	@Override
	public H2QESet havingPredicate(final QN predicate) throws NullPointerException, IllegalArgumentException {
		final Long key = this.owner.asQN(predicate).key;
		return new H2QESet(this.owner, new H2QQ().push("SELECT * FROM (").push(this).push(") WHERE P=").push(key));
	}

	@Override
	public H2QESet havingPredicates(final QNSet predicates) throws NullPointerException, IllegalArgumentException {
		final H2QNSet that = this.owner.asQNSet(predicates);
		return new H2QESet(this.owner, new H2QQ().push("SELECT * FROM (").push(this).push(") WHERE P IN (SELECT N FROM (").push(that).push("))"));
	}

	@Override
	public H2QESet havingSubject(final QN subject) throws NullPointerException, IllegalArgumentException {
		final Long key = this.owner.asQN(subject).key;
		return new H2QESet(this.owner, new H2QQ().push("SELECT * FROM (").push(this).push(") WHERE S=").push(key));
	}

	@Override
	public H2QESet havingSubjects(final QNSet subjects) throws NullPointerException, IllegalArgumentException {
		final H2QNSet that = this.owner.asQNSet(subjects);
		return new H2QESet(this.owner, new H2QQ().push("SELECT * FROM (").push(this).push(" WHERE S IN (SELECT N FROM (").push(that).push("))"));
	}

	@Override
	public H2QESet havingObject(final QN object) throws NullPointerException, IllegalArgumentException {
		final Long key = this.owner.asQN(object).key;
		return new H2QESet(this.owner, new H2QQ().push("SELECT * FROM (").push(this).push(") WHERE O=").push(key));
	}

	@Override
	public H2QESet havingObjects(final QNSet objects) throws NullPointerException, IllegalArgumentException {
		final H2QNSet that = this.owner.asQNSet(objects);
		return new H2QESet(this.owner, new H2QQ().push("SELECT * FROM (").push(this).push(") WHERE O IN (SELECT N FROM (").push(that).push("))"));
	}

	@Override
	public H2QESet copy() {
		return this.owner.newEdges(this);
	}

	@Override
	public H2QESet order() {
		return new Order(this);
	}

	/** {@inheritDoc} Sie ist eine Abkürzung für {@link #index(String) this.index("CPSO")}. */
	@Override
	public H2QESet index() {
		return this.index("CPSO");
	}

	/** Diese Methode indiziert diese temporäre Menge zur schnelleren Suche über die gegebenen Spalten in der gegebenen Reihenfolge und gibt diese bzw. eine
	 * derart indizierte temporäre Kopie zurück. Die Spaltenliste {@code cols} muss dazu aus den Zeichen {@code C}, {@code P}, {@code S} und {@code O} bestehen,
	 * welche für Kontext, Prädikat, Subjekt bzw. Objekt stehen. */
	public H2QESet index(final String cols) throws NullPointerException, IllegalArgumentException {
		return this.copy().index(cols);
	}

	@Override
	public H2QESet union(final QESet set) throws NullPointerException, IllegalArgumentException {
		final H2QESet that = this.owner.asQESet(set);
		return new H2QESet(this.owner, new H2QQ().push("(").push(this).push(") UNION (").push(that).push(")"));
	}

	@Override
	public H2QESet except(final QESet set) throws NullPointerException, IllegalArgumentException {
		final H2QESet that = this.owner.asQESet(set);
		return new H2QESet(this.owner, new H2QQ().push("(").push(this).push(") EXCEPT (").push(that).push(")"));
	}

	@Override
	public H2QESet intersect(final QESet set) throws NullPointerException, IllegalArgumentException {
		final H2QESet that = this.owner.asQESet(set);
		return new H2QESet(this.owner, new H2QQ().push("(").push(this).push(") INTERSECT (").push(that).push(")"));
	}

	/** Dieser Konstruktor initialisiert {@link #owner Graphspeicher} und {@link #table Tabelle}. Wenn letztre {@code null} ist, wird sie über
	 * {@link H2QQ#H2QQ(H2QS)} erzeugt. Die Tabelle muss die Spalten {@code (C BIGINT NOT NULL, P BIGINT NOT NULL, S BIGINT NOT NULL, O BIGINT NOT NULL)}
	 * besitzen. */
	protected H2QESet(final H2QS owner, final H2QQ select) {
		super(owner, select);
	}

	@Override
	protected QE item(final ResultSet item) throws SQLException {
		return this.owner.newEdge(item.getInt(1), item.getInt(2), item.getInt(3), item.getInt(4));
	}

	private static void check(final String cols) {
		if ((cols.length() != 4) || ((cols.indexOf('C') | cols.indexOf('P') | cols.indexOf('S') | cols.indexOf('O')) < 0)) throw new IllegalArgumentException();
	}

	static class Main extends H2QESet {

		public Main(final H2QS owner) {
			super(owner, new H2QQ().push("SELECT * FROM QE"));
		}

		@Override
		public H2QESet index(final String cols) throws NullPointerException, IllegalArgumentException {
			H2QESet.check(cols);
			return this;
		}

	}

	static class Temp extends H2QESet {

		public Temp(final H2QS owner) {
			super(owner, null);
			new H2QQ().push("CREATE TEMPORARY TABLE ").push(this.table).push(" (C BIGINT NOT NULL, P BIGINT NOT NULL, S BIGINT NOT NULL, O BIGINT NOT NULL)")
				.update(owner);
		}

		@Override
		public H2QESet copy() {
			return this;
		}

		@Override
		public H2QESet index(final String cols) throws NullPointerException, IllegalArgumentException {
			H2QESet.check(cols);
			new H2QQ().push("CREATE INDEX IF NOT EXISTS ").push(this.table).push("_INDEX_").push(cols).push(" ON ").push(this.table).push(" (").push(cols.charAt(0))
				.push(", ").push(cols.charAt(1)).push(", ").push(cols.charAt(2)).push(", ").push(cols.charAt(3)).push(")").update(this.owner);
			return this;
		}

	}

	static class Order extends H2QESet {

		public Order(final H2QESet that) {
			super(that.owner, new H2QQ().push("SELECT * FROM (").push(that).push(") ORDER BY C, P, S, O"));
		}

		@Override
		public H2QESet order() {
			return this;
		}

	}

}
