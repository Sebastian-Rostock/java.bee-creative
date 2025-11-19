package bee.creative.qs.h2;

import static bee.creative.util.Iterables.filteredIterable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import bee.creative.qs.QN;
import bee.creative.qs.QNSet;
import bee.creative.qs.QT;
import bee.creative.qs.QTSet;
import bee.creative.util.Filter;

/** Diese Klasse implementiert ein {@link QTSet} als Sicht auf das ergebnis einer SQL-Anfrage.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class H2QTSet extends H2QOSet<QT, QTSet> implements QTSet {

	@Override
	public H2QTSet2 copy() {
		return this.owner.newTuples(this.names(), this);
	}

	@Override
	public H2QTSet2 copy(Filter<? super QT> filter) throws NullPointerException {
		return this.owner.newTuples(this.names(), filteredIterable(this, filter));
	}

	@Override
	public H2QTSet order() {
		return new H2QTSetOrder(this, this.names);
	}

	/** Diese Methode indiziert diese Menge bezüglich der gegebenen Rollen in der gegebenen Reihenfolge und gibt {@code this} zurück. */
	public H2QTSet2 index(int... roles) throws NullPointerException, IllegalArgumentException {
		return this.copy().index(roles);
	}

	/** Diese Methode ist eine Abkürzung für {@link #index(int...) this.index(this.roles(names))}.
	 *
	 * @see #roles(String...) */
	public H2QTSet2 index(String... names) throws NullPointerException, IllegalArgumentException {
		return this.index(this.roles(names));
	}

	/** Diese Methode ist eine Abkürzung für {@link #index(int...) this.index(this.roles(names))}.
	 *
	 * @see #roles(List) */
	public H2QTSet2 index(List<String> names) throws NullPointerException, IllegalArgumentException {
		return this.index(this.roles(names));
	}

	@Override
	public H2QTSet union(QTSet set) throws NullPointerException, IllegalArgumentException {
		var that = this.owner.asQTSet(set, this.names());
		return new H2QTSet(this.owner, this.names, new H2QQ().push("(").push(this).push(") UNION (").push(that).push(")"));
	}

	@Override
	public H2QTSet except(QTSet set) throws NullPointerException, IllegalArgumentException {
		var that = this.owner.asQTSet(set, this.names());
		return new H2QTSet(this.owner, this.names, new H2QQ().push("(").push(this).push(") EXCEPT (").push(that).push(")"));
	}

	@Override
	public H2QTSet intersect(QTSet set) throws NullPointerException, IllegalArgumentException {
		var that = this.owner.asQTSet(set, this.names());
		return new H2QTSet(this.owner, this.names, new H2QQ().push("(").push(this).push(") INTERSECT (").push(that).push(")"));
	}

	@Override
	public int role(String name) throws NullPointerException {
		return this.names.role(name);
	}

	@Override
	public int[] roles(String... names) throws NullPointerException {
		return this.roles(Arrays.asList(names));
	}

	@Override
	public int[] roles(List<String> names) throws NullPointerException {
		var size = names.size();
		var roles = new int[size];
		for (var i = 0; i < size; i++) {
			roles[i] = this.role(names.get(i));
		}
		return roles;
	}

	@Override
	public String name(int role) throws IllegalArgumentException {
		return this.names.name(role);
	}

	@Override
	public List<String> names() {
		return this.names.list;
	}

	@Override
	public String[] names(int... roles) throws IllegalArgumentException {
		var size = roles.length;
		var names = new String[size];
		for (var i = 0; i < size; i++) {
			names[i] = this.name(roles[i]);
		}
		return names;
	}

	@Override
	public H2QESet edges(int context, int predicate, int subject, int object) throws NullPointerException, IllegalArgumentException {
		this.names(context, predicate, subject, object);
		return new H2QESet(this.owner, new H2QQ().push("SELECT DISTINCT C").push(context).push(" AS C, C").push(predicate).push(" AS P, C").push(subject)
			.push("AS S, C").push(object).push(" AS O FROM (").push(this).push(")"));
	}

	@Override
	public H2QESet edges(String context, String predicate, String subject, String object) throws NullPointerException, IllegalArgumentException {
		return this.edges(this.role(context), this.role(predicate), this.role(subject), this.role(object));
	}

	@Override
	public H2QNSet nodes(int role) throws IllegalArgumentException {
		this.name(role);
		return new H2QNSet(this.owner, new H2QQ().push("SELECT DISTINCT C").push(role).push(" AS N FROM (").push(this).push(")"));
	}

	@Override
	public H2QNSet nodes(String name) throws NullPointerException, IllegalArgumentException {
		return this.nodes(this.role(name));
	}

	@Override
	public H2QTSet join(QTSet set) throws NullPointerException, IllegalArgumentException {
		var that = this.owner.asQTSet(set);
		var roles = this.roles(that.names.list);
		var size1 = this.names.size();
		var size2 = roles.length;
		var list = new ArrayList<String>(size1 + size2);
		list.addAll(this.names.list);
		for (var i2 = 0; i2 < size2; i2++) {
			if (roles[i2] < 0) { // add new from that
				list.add(that.names.name(i2));
			}
		}
		var names = new H2QTSetNames(list);
		var qry = new H2QQ().push("(SELECT A.C0");
		for (var i = 1; i < size1; i++) {
			qry.push(", A.C").push(i); // add all from this
		}
		for (int i = size1, i2 = 0; i2 < size2; i2++) {
			if (roles[i2] < 0) { // add new from that
				qry.push(", B.C").push(i2).push(" AS C").push(i++);
			}
		}
		qry.push(" FROM (").push(this).push(") AS A INNER JOIN (").push(that).push(") AS B ON TRUE");
		for (var i2 = 0; i2 < size2; i2++) {
			if (roles[i2] >= 0) {
				qry.push(" AND A.C").push(roles[i2]).push("=B.C").push(i2);
			}
		}
		qry.push(")");
		return new H2QTSet(this.owner, names, qry);
	}

	@Override
	public H2QTSet select(int... roles) throws NullPointerException, IllegalArgumentException {
		var names = new H2QTSetNames(this.names(roles));
		return new H2QTSet(this.owner, names, new H2QQ().push("SELECT DISTINCT C").push(roles[0]).push(" AS C0")
			.push(1, names.size(), (q, i) -> q.push(", C").push(roles[i]).push(" AS C").push(i)).push(" FROM (").push(this).push(")"));
	}

	@Override
	public H2QTSet select(String... names) throws NullPointerException, IllegalArgumentException {
		return this.select(this.roles(names));
	}

	@Override
	public H2QTSet select(List<String> names) throws NullPointerException, IllegalArgumentException {
		return this.select(this.roles(names));
	}

	@Override
	public H2QTSet withNode(int role, QN node) throws NullPointerException, IllegalArgumentException {
		this.name(role);
		Long key = this.owner.asQN(node).key;
		var size = this.names.size();
		var qry = new H2QQ().push(role != 0 ? "SELECT DISTINCT C0" : "SELECT DISTINCT ");
		for (var i = 1; i < role; i++) {
			qry.push(", C").push(i);
		}
		if (role == 0) {
			qry.push(key).push(" AS C0");
		} else {
			qry.push(", ").push(key).push(" AS C").push(role);
		}
		for (var i = role + 1; i < size; i++) {
			qry.push(", C").push(i);
		}
		qry.push(" FROM (").push(this).push(")");
		return new H2QTSet(this.owner, this.names, qry);
	}

	@Override
	public H2QTSet withNode(String name, QN node) throws NullPointerException, IllegalArgumentException {
		return this.withNode(this.role(name), node);
	}

	@Override
	public H2QTSet withNodes(int role, QNSet nodes) throws NullPointerException, IllegalArgumentException {
		this.name(role);
		var that = this.owner.asQTSet(nodes);
		return new H2QTSet(this.owner, this.names,
			new H2QQ().push(role != 0 ? "SELECT DISTINCT A.C0" : "SELECT DISTINCT B.N AS C0").push(1, role, (q, i) -> q.push(", A.C").push(i))
				.push(role != 0, q -> q.push(", B.N AS C").push(role)).push(role + 1, this.names.size(), (q, i) -> q.push(", A.C").push(i)).push(" FROM (").push(this)
				.push(") AS A, (").push(that).push(") AS B"));
	}

	@Override
	public H2QTSet withNodes(String name, QNSet nodes) throws NullPointerException, IllegalArgumentException {
		return this.withNodes(this.role(name), nodes);
	}

	@Override
	public H2QTSet withNames(String... names) throws NullPointerException, IllegalArgumentException {
		return this.withNames(Arrays.asList(names));
	}

	@Override
	public H2QTSet withNames(List<String> names) throws NullPointerException, IllegalArgumentException {
		if (this.names().equals(names)) return this;
		var names2 = new H2QTSetNames(names);
		if (this.names.size() != names2.size()) throw new IllegalArgumentException();
		return new H2QTSet(this.owner, names2, this.table);
	}

	@Override
	public H2QTSet havingNode(int role, QN node) throws NullPointerException, IllegalArgumentException {
		this.name(role);
		var key = this.owner.asQN(node).key;
		return new H2QTSet(this.owner, this.names, new H2QQ().push("SELECT * FROM (").push(this).push(") WHERE C").push(role).push("=").push(key));
	}

	@Override
	public H2QTSet havingNode(String name, QN node) throws NullPointerException, IllegalArgumentException {
		return this.havingNode(this.role(name), node);
	}

	@Override
	public H2QTSet havingNodes(int role, QNSet nodes) throws NullPointerException, IllegalArgumentException {
		this.name(role);
		var that = this.owner.asQTSet(nodes);
		return new H2QTSet(this.owner, this.names, new H2QQ().push("SELECT * FROM (").push(this).push(") WHERE C").push(role).push(" IN (").push(that).push(")"));
	}

	@Override
	public H2QTSet havingNodes(String name, QNSet nodes) throws NullPointerException, IllegalArgumentException {
		return this.havingNodes(this.role(name), nodes);
	}

	/** Dieser Konstruktor initialisiert {@link #owner Graphspeicher} und {@link #table Tabelle}. Wenn letztre {@code null} ist, wird sie über
	 * {@link H2QQ#H2QQ(H2QS)} erzeugt. Die Tabelle muss die Spalten {@code (C0 BIGINT NOT NULL, C1 BIGINT NOT NULL, ...)} besitzen. */
	protected H2QTSet(H2QS owner, H2QTSetNames names, H2QQ table) {
		super(owner, table);
		this.names = names;
	}

	@Override
	protected QT customItem(ResultSet item) throws SQLException {
		var size = this.names.size();
		var keys = new long[size];
		for (var i = 0; i < size; i++) {
			keys[i] = item.getLong(i + 1);
		}
		return this.owner.newTuple(keys);
	}

	private final H2QTSetNames names;

}
