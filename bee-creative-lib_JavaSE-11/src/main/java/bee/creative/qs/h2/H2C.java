package bee.creative.qs.h2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Diese Klasse erzeugt eine H2-Datenbankverbindung.
 *
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class H2C {

	/** Diese Methode liefert die H2-Datenbankverbindung zum gegebenen Dateipfad. Dieser wird mit dem {@code jdbc:h2:}-Protokoll erzeugt. Dazu wird das Laden der
	 * Klasse {@code org.h2.Driver} erzwungen. */
	public static Connection from(String file) throws SQLException, NullPointerException, ClassNotFoundException {
		Class.forName("org.h2.Driver");
		return DriverManager.getConnection("jdbc:h2:" + file.toString(), "", "");
	}

}
