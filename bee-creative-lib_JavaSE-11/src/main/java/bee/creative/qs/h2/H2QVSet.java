package bee.creative.qs.h2;

import static bee.creative.util.Iterables.filteredIterable;
import java.sql.ResultSet;
import java.sql.SQLException;
import bee.creative.qs.QN;
import bee.creative.qs.QVSet;
import bee.creative.util.Filter;
import bee.creative.util.Setter;

/** Diese Klasse implementiert ein {@link QVSet} als Sicht auf das ergebnis einer SQL-Anfrage.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class H2QVSet extends H2QOSet<String, QVSet> implements QVSet {

	@Override
	public boolean putAll() {
		return this.owner.markPutValue(new H2QQ().push("MERGE INTO QN A USING (").push(this)
			.push(") B ON A.V=B.V WHEN NOT MATCHED THEN INSERT (N, V) VALUES (SELECT NEXT VALUE FOR QN_SEQ AS N, V)").update(this.owner));
	}

	@Override
	public boolean popAll() {
		return this.owner.markPopValue(new H2QQ().push("DELETE FROM QN WHERE V IN (").push(this).push(")").update(this.owner));
	}

	@Override
	public H2QNSet nodes() {
		return new H2QNSet(this.owner, new H2QQ().push("SELECT N FROM QN WHERE V IN (").push(this).push(")"));
	}

	@Override
	public void nodes(Setter<? super String, ? super QN> nodes) {
		try (var rset = new H2QQ().push("SELECT V, N FROM QN WHERE V IN (").push(this).push(")").select(this.owner)) {
			while (rset.next()) {
				nodes.set(rset.getString(1), this.owner.newNode(rset.getLong(2)));
			}
		} catch (SQLException cause) {
			throw new IllegalStateException(cause);
		}
	}

	@Override
	public H2QVSet havingState(boolean state) {
		return state ? this.intersect(this.owner.values()) : this.except(this.owner.values());
	}

	@Override
	public H2QVSet2 copy() {
		return this.owner.newValues(this);
	}

	@Override
	public H2QVSet2 copy(Filter<? super String> filter) throws NullPointerException {
		return this.owner.newValues(filteredIterable(this, filter));
	}

	@Override
	public H2QVSet order() {
		return new H2QVSetOrder(this);
	}

	@Override
	public H2QVSet union(QVSet set) throws NullPointerException, IllegalArgumentException {
		var that = this.owner.asQVSet(set);
		return new H2QVSet(this.owner, new H2QQ().push("(").push(this).push(") UNION (").push(that).push(")"));
	}

	@Override
	public H2QVSet except(QVSet set) throws NullPointerException, IllegalArgumentException {
		var that = this.owner.asQVSet(set);
		return new H2QVSet(this.owner, new H2QQ().push("(").push(this).push(") EXCEPT (").push(that).push(")"));
	}

	@Override
	public H2QVSet intersect(QVSet set) throws NullPointerException, IllegalArgumentException {
		var that = this.owner.asQVSet(set);
		return new H2QVSet(this.owner, new H2QQ().push("(").push(this).push(") INTERSECT (").push(that).push(")"));
	}

	/** Dieser Konstruktor initialisiert {@link #owner Graphspeicher} und {@link #table Tabelle}. Wenn letztre {@code null} ist, wird sie über
	 * {@link H2QQ#H2QQ(H2QS)} erzeugt. Die Tabelle muss die Spalten {@code (V VARCHAR(1G) NOT NULL)} besitzen. */
	protected H2QVSet(H2QS owner, H2QQ table) {
		super(owner, table);
	}

	@Override
	protected String customItem(ResultSet item) throws SQLException {
		return item.getString(1);
	}

}
