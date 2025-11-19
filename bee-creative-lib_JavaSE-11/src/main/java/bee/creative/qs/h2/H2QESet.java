package bee.creative.qs.h2;

import static bee.creative.util.Iterables.filteredIterable;
import java.sql.ResultSet;
import java.sql.SQLException;
import bee.creative.qs.QE;
import bee.creative.qs.QESet;
import bee.creative.qs.QN;
import bee.creative.qs.QNSet;
import bee.creative.qs.QTSet;
import bee.creative.util.Filter;

/** Diese Klasse implementiert ein {@link QESet} als Sicht auf das ergebnis einer SQL-Anfrage.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class H2QESet extends H2QOSet<QE, QESet> implements QESet {

	@Override
	public boolean putAll() {
		return new H2QQ().push("MERGE INTO QE SELECT * FROM ").push(this.copy().table).update(this.owner);
	}

	@Override
	public boolean popAll() {
		return new H2QQ().push("DELETE FROM QE AS A WHERE EXISTS (SELECT 1 FROM ").push(this.copy().table)
			.push(" AS B WHERE A.C=B.C AND A.P=B.P AND A.S=B.S AND A.O=B.O)").update(this.owner);
	}

	@Override
	public H2QNSet nodes() {
		return this.contexts().union(this.predicates()).union(this.subjects()).union(this.objects());
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
	public QTSet tuples(String context, String predicate, String subject, String object) throws NullPointerException, IllegalArgumentException {
		return new H2QTSet(this.owner, new H2QTSetNames(context, predicate, subject, object),
			new H2QQ().push("SELECT C AS C0, P AS C1, S AS C2, O AS C3 FROM (").push(this).push(")"));
	}

	@Override
	public H2QESet havingState(boolean state) {
		return state ? this.intersect(this.owner.edges()) : this.except(this.owner.edges());
	}

	@Override
	public H2QESet withContext(QN context) throws NullPointerException, IllegalArgumentException {
		var key = this.owner.asQN(context).key;
		return new H2QESet(this.owner, new H2QQ().push("SELECT DISTINCT ").push(key).push(" C, P, S, O FROM (").push(this).push(")"));
	}

	@Override
	public H2QESet withContexts(QNSet contexts) throws NullPointerException, IllegalArgumentException {
		var that = this.owner.asQNSet(contexts);
		return new H2QESet(this.owner, new H2QQ().push("SELECT DISTINCT B.N AS C, A.P, A.S, A.O FROM (").push(this).push(") AS A, (").push(that).push(") AS B"));
	}

	@Override
	public H2QESet withPredicate(QN predicate) throws NullPointerException, IllegalArgumentException {
		Long key = this.owner.asQN(predicate).key;
		return new H2QESet(this.owner, new H2QQ().push("SELECT DISTINCT C, ").push(key).push(" AS P, S, O FROM (").push(this).push(")"));
	}

	@Override
	public H2QESet withPredicates(QNSet predicates) throws NullPointerException, IllegalArgumentException {
		var that = this.owner.asQNSet(predicates);
		return new H2QESet(this.owner, new H2QQ().push("SELECT DISTINCT A.C, B.N AS P, A.S, A.O FROM (").push(this).push(") AS A, (").push(that).push(") AS B"));
	}

	@Override
	public H2QESet withSubject(QN subject) throws NullPointerException, IllegalArgumentException {
		Long key = this.owner.asQN(subject).key;
		return new H2QESet(this.owner, new H2QQ().push("SELECT DISTINCT C, P, ").push(key).push(" AS S, O FROM (").push(this).push(")"));
	}

	@Override
	public H2QESet withSubjects(QNSet subjects) throws NullPointerException, IllegalArgumentException {
		var that = this.owner.asQNSet(subjects);
		return new H2QESet(this.owner, new H2QQ().push("SELECT DISTINCT A.C, A.P, B.N AS S, A.O FROM (").push(this).push(") AS A, (").push(that).push(") AS B"));
	}

	@Override
	public H2QESet withObject(QN object) throws NullPointerException, IllegalArgumentException {
		Long key = this.owner.asQN(object).key;
		return new H2QESet(this.owner, new H2QQ().push("SELECT DISTINCT C, P, S, ").push(key).push(" AS O FROM (").push(this).push(")"));
	}

	@Override
	public H2QESet withObjects(QNSet objects) throws NullPointerException, IllegalArgumentException {
		var that = this.owner.asQNSet(objects);
		return new H2QESet(this.owner, new H2QQ().push("SELECT DISTINCT A.C, A.P, A.S, B.N AS O FROM (").push(this).push(") AS A, (").push(that).push(") AS B"));
	}

	@Override
	public H2QESet havingNode(QN node) throws NullPointerException, IllegalArgumentException {
		Long key = this.owner.asQN(node).key;
		return new H2QESet(this.owner,
			new H2QQ().push("SELECT * FROM (").push(this).push(") WHERE C=").push(key).push(" OR P=").push(key).push(" OR S=").push(key).push(" OR O=").push(key));
	}

	@Override
	public H2QESet havingNodes(QNSet nodes) throws NullPointerException, IllegalArgumentException {
		var that = this.owner.asQNSet(nodes);
		return new H2QESet(this.owner,
			new H2QQ().push("SELECT * FROM (").push(this).push(") WHERE EXISTS (SELECT N FROM (").push(that).push(") WHERE C=N OR P=N OR S=N OR O=N)"));
	}

	@Override
	public H2QESet havingContext(QN context) throws NullPointerException, IllegalArgumentException {
		Long key = this.owner.asQN(context).key;
		return new H2QESet(this.owner, new H2QQ().push("SELECT * FROM (").push(this).push(") WHERE C=").push(key));
	}

	@Override
	public H2QESet havingContexts(QNSet contexts) throws NullPointerException, IllegalArgumentException {
		var that = this.owner.asQNSet(contexts);
		return new H2QESet(this.owner, new H2QQ().push("SELECT * FROM (").push(this).push(") WHERE C IN (SELECT N FROM (").push(that).push("))"));
	}

	@Override
	public H2QESet havingPredicate(QN predicate) throws NullPointerException, IllegalArgumentException {
		Long key = this.owner.asQN(predicate).key;
		return new H2QESet(this.owner, new H2QQ().push("SELECT * FROM (").push(this).push(") WHERE P=").push(key));
	}

	@Override
	public H2QESet havingPredicates(QNSet predicates) throws NullPointerException, IllegalArgumentException {
		var that = this.owner.asQNSet(predicates);
		return new H2QESet(this.owner, new H2QQ().push("SELECT * FROM (").push(this).push(") WHERE P IN (SELECT N FROM (").push(that).push("))"));
	}

	@Override
	public H2QESet havingSubject(QN subject) throws NullPointerException, IllegalArgumentException {
		Long key = this.owner.asQN(subject).key;
		return new H2QESet(this.owner, new H2QQ().push("SELECT * FROM (").push(this).push(") WHERE S=").push(key));
	}

	@Override
	public H2QESet havingSubjects(QNSet subjects) throws NullPointerException, IllegalArgumentException {
		var that = this.owner.asQNSet(subjects);
		return new H2QESet(this.owner, new H2QQ().push("SELECT * FROM (").push(this).push(") WHERE S IN (SELECT N FROM (").push(that).push("))"));
	}

	@Override
	public H2QESet havingObject(QN object) throws NullPointerException, IllegalArgumentException {
		Long key = this.owner.asQN(object).key;
		return new H2QESet(this.owner, new H2QQ().push("SELECT * FROM (").push(this).push(") WHERE O=").push(key));
	}

	@Override
	public H2QESet havingObjects(QNSet objects) throws NullPointerException, IllegalArgumentException {
		var that = this.owner.asQNSet(objects);
		return new H2QESet(this.owner, new H2QQ().push("SELECT * FROM (").push(this).push(") WHERE O IN (SELECT N FROM (").push(that).push("))"));
	}

	@Override
	public H2QESet2 copy() {
		return this.owner.newEdges(this);
	}

	@Override
	public H2QESet2 copy(Filter<? super QE> filter) throws NullPointerException {
		return this.owner.newEdges(filteredIterable(this, filter));
	}

	@Override
	public H2QESet order() {
		return new H2QESetOrder(this);
	}

	/** Diese Methode indiziert diese temporäre Menge zur schnelleren Suche über die gegebenen Spalten in der gegebenen Reihenfolge und gibt diese bzw. eine
	 * derart indizierte temporäre Kopie zurück. Die Spaltenliste {@code cols} muss dazu aus den Zeichen {@code C}, {@code P}, {@code S} und {@code O} bestehen,
	 * welche für Kontext, Prädikat, Subjekt bzw. Objekt stehen. */
	public H2QESet index(String cols) throws NullPointerException, IllegalArgumentException {
		return this.copy().index(cols);
	}

	@Override
	public H2QESet union(QESet set) throws NullPointerException, IllegalArgumentException {
		var that = this.owner.asQESet(set);
		return new H2QESet(this.owner, new H2QQ().push("(").push(this).push(") UNION (").push(that).push(")"));
	}

	@Override
	public H2QESet except(QESet set) throws NullPointerException, IllegalArgumentException {
		var that = this.owner.asQESet(set);
		return new H2QESet(this.owner, new H2QQ().push("(").push(this).push(") EXCEPT (").push(that).push(")"));
	}

	@Override
	public H2QESet intersect(QESet set) throws NullPointerException, IllegalArgumentException {
		var that = this.owner.asQESet(set);
		return new H2QESet(this.owner, new H2QQ().push("(").push(this).push(") INTERSECT (").push(that).push(")"));
	}

	/** Dieser Konstruktor initialisiert {@link #owner Graphspeicher} und {@link #table Tabelle}. Wenn letztre {@code null} ist, wird sie über
	 * {@link H2QQ#H2QQ(H2QS)} erzeugt. Die Tabelle muss die Spalten {@code (C BIGINT NOT NULL, P BIGINT NOT NULL, S BIGINT NOT NULL, O BIGINT NOT NULL)}
	 * besitzen. */
	protected H2QESet(H2QS owner, H2QQ table) throws NullPointerException {
		super(owner, table);
	}

	@Override
	protected QE customItem(ResultSet item) throws SQLException {
		return this.owner.newEdge(item.getInt(1), item.getInt(2), item.getInt(3), item.getInt(4));
	}

}
