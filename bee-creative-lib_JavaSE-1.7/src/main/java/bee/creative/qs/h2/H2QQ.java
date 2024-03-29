package bee.creative.qs.h2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert den Bauplan einer Datenbankanfrage als {@link ArrayList Auflistung} von Anfragetoken. Die {@link #toString() Textdarstellung} des
 * Bauplans entspricht der Verkettung der Textdarstellungen der Anfragetoken.
 *
 * @author [cc-by] 2022 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class H2QQ {

	private final Object name;

	private final ArrayList<Object> query = new ArrayList<>(10);

	/** Dieser Konstruktor initialisiert den Namen der temporären Tabelle mit {@code null}. */
	public H2QQ() {
		this.name = null;
	}

	/** Dieser Konstruktor initialisiert den Namen der temporären Tabelle über {@link H2QS#createTemp()} und bestückt die Anfrage anschließend mit
	 * {@code this.push("select * from ").push(this)}. */
	public H2QQ(final H2QS owner) {
		this.name = owner.createTemp();
		this.push("select * from ").push(this);
	}

	/** Diese Methode fügt den Namen der temporären Tabelle der gegebenen Anfrage an und gibt {@code this} zurück. */
	public H2QQ push(final H2QQ table) throws NullPointerException {
		return this.push(table.name);
	}

	/** Diese Methode fügt die Anfragetoken der {@link H2QOSet#table Anfrage} des gegebenen {@link H2QOSet} an und gibt {@code this} zurück. */
	public H2QQ push(final H2QOSet<?, ?> set) throws NullPointerException {
		this.query.addAll(set.table.query);
		return this;
	}

	/** Diese Methode fügt den gegebenen Anfragetoken an und gibt {@code this} zurück. Der Anfragetoken darf nicht {@code null}, kein {@link H2QQ} und kein
	 * {@link H2QOSet} sein. */
	public H2QQ push(final Object token) throws NullPointerException {
		this.query.add(Objects.notNull(token));
		return this;
	}

	/** Diese Methode überführt diese Anweisung in eine {@link Connection#prepareStatement(String) aufbereitete} und gibt sie zurück.
	 *
	 * @see Connection#prepareStatement(String) */
	public PreparedStatement prepare(final H2QS owner) throws SQLException {
		return owner.conn.prepareStatement(this.toString());
	}

	/** Diese Methode führt diese Anweisung als {@link Statement#executeUpdate(String) Aktualisierung} aus und gibt nur dann {@code true} zurück, wenn dadurch
	 * Tabellenzeilen verändert wurden.
	 *
	 * @see Statement#executeUpdate(String) */
	public boolean update(final H2QS owner) throws IllegalStateException {
		try {
			return owner.exec.executeUpdate(this.toString()) != 0;
		} catch (final SQLException cause) {
			throw new IllegalStateException(cause);
		}
	}

	/** Diese Methode führt diese Anweisung als {@link Statement#executeQuery(String) Anfrage} aus und gibt die dazu ermittelte {@link ResultSet Ergebnismenge}
	 * zurück.
	 *
	 * @see Statement#executeQuery(String) */
	public ResultSet select(final H2QS owner) throws SQLException {
		return owner.exec.executeQuery(this.toString());
	}

	@Override
	public String toString() {
		final StringBuilder res = new StringBuilder(512);
		this.query.trimToSize();
		for (final Object object: this.query) {
			res.append(object.toString());
		}
		{
			System.err.println(res);

		}
		return res.toString();
	}

}
