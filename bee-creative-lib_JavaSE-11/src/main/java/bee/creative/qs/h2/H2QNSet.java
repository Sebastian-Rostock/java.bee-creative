package bee.creative.qs.h2;

import static bee.creative.util.Iterables.filteredIterable;
import java.sql.ResultSet;
import java.sql.SQLException;
import bee.creative.qs.QN;
import bee.creative.qs.QNSet;
import bee.creative.qs.QTSet;
import bee.creative.qs.QVSet;
import bee.creative.util.Filter;
import bee.creative.util.Setter;

/** Diese Klasse implementiert ein {@link QNSet} als Sicht auf das ergebnis einer SQL-Anfrage.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class H2QNSet extends H2QOSet<QN, QNSet> implements QNSet {

	@Override
	public boolean popAll() {
		var that = this.copy();
		return this.owner.markPopValue(new H2QQ().push("DELETE FROM QN WHERE N IN (").push(that).push(")").update(this.owner)) //
			| new H2QQ().push("DELETE FROM QE WHERE C IN (").push(that).push(")").update(this.owner) //
			| new H2QQ().push("DELETE FROM QE WHERE P IN (").push(that).push(")").update(this.owner) //
			| new H2QQ().push("DELETE FROM QE WHERE S IN (").push(that).push(")").update(this.owner) //
			| new H2QQ().push("DELETE FROM QE WHERE O IN (").push(that).push(")").update(this.owner);
	}

	@Override
	public H2QVSet values() {
		return new H2QVSet(this.owner, new H2QQ().push("SELECT V FROM QN WHERE N IN (").push(this).push(")"));
	}

	@Override
	public void values(Setter<? super QN, ? super String> values) {
		try (var rset = new H2QQ().push("SELECT N, V FROM QN WHERE N IN (").push(this).push(")").select(this.owner)) {
			while (rset.next()) {
				values.set(this.owner.newNode(rset.getInt(1)), rset.getString(2));
			}
		} catch (SQLException cause) {
			throw new IllegalStateException(cause);
		}
	}

	@Override
	public QTSet tuples(String name) throws NullPointerException, IllegalArgumentException {
		return new H2QTSet(this.owner, new H2QTSetNames(name), new H2QQ().push("SELECT N C0 FROM (").push(this).push(")"));
	}

	@Override
	public H2QNSet havingValue() {
		return this.intersect(this.owner.nodes());
	}

	@Override
	public H2QNSet havingValues(QVSet values) throws NullPointerException, IllegalArgumentException {
		return this.intersect(values.nodes());
	}

	@Override
	public H2QNSet havingState(boolean state) {
		return state ? this.intersect(this.owner.nodes()) : this.except(this.owner.nodes());
	}

	@Override
	public H2QNSet2 copy() {
		return this.owner.newNodes(this);
	}

	@Override
	public H2QNSet2 copy(Filter<? super QN> filter) throws NullPointerException {
		return this.owner.newNodes(filteredIterable(this, filter));
	}

	@Override
	public H2QNSet order() {
		return new H2QNSetOrder(this);
	}

	@Override
	public H2QNSet union(QNSet set) throws NullPointerException, IllegalArgumentException {
		var that = this.owner.asQNSet(set);
		return new H2QNSet(this.owner, new H2QQ().push("(").push(this).push(") UNION (").push(that).push(")"));
	}

	@Override
	public H2QNSet except(QNSet set) throws NullPointerException, IllegalArgumentException {
		var that = this.owner.asQNSet(set);
		return new H2QNSet(this.owner, new H2QQ().push("(").push(this).push(") EXCEPT (").push(that).push(")"));
	}

	@Override
	public H2QNSet intersect(QNSet set) throws NullPointerException, IllegalArgumentException {
		var that = this.owner.asQNSet(set);
		return new H2QNSet(this.owner, new H2QQ().push("(").push(this).push(") INTERSECT (").push(that).push(")"));
	}

	/** Dieser Konstruktor initialisiert {@link #owner Graphspeicher} und {@link #table Tabelle}. Wenn letztre {@code null} ist, wird sie über
	 * {@link H2QQ#H2QQ(H2QS)} erzeugt. Die Tabelle muss die Spalten {@code (N BIGINT NOT NULL)} besitzen. */
	public H2QNSet(H2QS owner, H2QQ table) {
		super(owner, table);
	}

	@Override
	protected QN customItem(ResultSet item) throws SQLException {
		return this.owner.newNode(item.getInt(1));
	}

}
