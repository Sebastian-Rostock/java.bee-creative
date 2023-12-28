package bee.creative.qs.h2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import bee.creative.lang.Objects;
import bee.creative.util.Consumer;
import bee.creative.util.Setter;

/** Diese Klasse implementiert den Bauplan einer Datenbankanfrage als {@link ArrayList Auflistung} von Anfragetoken. Die {@link #toString() Textdarstellung} des
 * Bauplans entspricht der Verkettung der Textdarstellungen der Anfragetoken.
 *
 * @author [cc-by] 2022 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class H2QQ {

	/** Dieser Konstruktor initialisiert den Namen der temporären Tabelle mit {@code null}. */
	public H2QQ() {
		this(null);
	}

	/** Dieser Konstruktor initialisiert den Namen der temporären Tabelle über {@link H2QS#putTable()} und bestückt die Anfrage anschließend mit
	 * {@code this.push("select * from ").push(this)}. */
	public H2QQ(H2QS owner) throws NullPointerException {
		this(owner.putTable());
	}

	public H2QQ(Object name) {
		this.name = name;
		if (name == null) return;
		this.push("SELECT * FROM ").push(name);
	}

	/** Diese Methode fügt den Namen der temporären Tabelle der gegebenen Anfrage an und gibt {@code this} zurück. */
	public H2QQ push(H2QQ table) throws NullPointerException {
		return this.push(table.name);
	}

	/** Diese Methode fügt die Anfragetoken der {@link H2QISet#table Anfrage} des gegebenen {@link H2QISet} an und gibt {@code this} zurück. */
	public H2QQ push(H2QISet<?> set) throws NullPointerException {
		this.query.addAll(set.table.query);
		return this;
	}

	/** Diese Methode fügt den gegebenen Anfragetoken an und gibt {@code this} zurück. Der Anfragetoken darf nicht {@code null}, kein {@link H2QQ} und kein
	 * {@link H2QISet} sein. */
	public H2QQ push(Object token) throws NullPointerException {
		this.query.add(Objects.notNull(token));
		return this;
	}

	/** Diese Methode ruft die gegebene Methode mit diesem Objekt auf und gibt {@code this} zurück. */
	public H2QQ push(Consumer<H2QQ> token) throws NullPointerException {
		token.set(this);
		return this;
	}

	public H2QQ push(int incl, int excl, Setter<H2QQ, Integer> token) throws NullPointerException {
		while (incl < excl)
			token.set(this, incl++);
		return this;
	}

	/** Diese Methode überführt diese Anweisung in eine {@link Connection#prepareStatement(String) aufbereitete} und gibt sie zurück.
	 *
	 * @see Connection#prepareStatement(String) */
	public PreparedStatement prepare(H2QS owner) throws SQLException {
		return owner.conn.prepareStatement(this.toString());
	}

	/** Diese Methode führt diese Anweisung als {@link Statement#executeUpdate(String) Aktualisierung} aus und gibt nur dann {@code true} zurück, wenn dadurch
	 * Tabellenzeilen verändert wurden.
	 *
	 * @see Statement#executeUpdate(String) */
	public boolean update(H2QS owner) throws IllegalStateException {
		try (var stmt = owner.conn.createStatement()) {
			return stmt.executeUpdate(this.toString()) != 0;
		} catch (SQLException cause) {
			throw new IllegalStateException(cause);
		}
	}

	/** Diese Methode führt diese Anweisung als {@link Statement#executeQuery(String) Anfrage} aus und gibt die dazu ermittelte {@link ResultSet Ergebnismenge}
	 * zurück.
	 *
	 * @see Statement#executeQuery(String) */
	public ResultSet select(H2QS owner) throws SQLException {
		return owner.conn.createStatement().executeQuery(this.toString());
	}

	@Override
	public String toString() {
		var res = new StringBuilder(512);
		this.query.trimToSize();
		this.query.forEach(res::append);
		return res.toString();
	}

	private final Object name;

	private final ArrayList<Object> query = new ArrayList<>(10);

}
