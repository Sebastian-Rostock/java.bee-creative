package bee.creative.xml;

import java.io.File;
import java.net.URI;
import bee.creative.util.Builders.BaseValueBuilder;

/** Diese Klasse implementiert den Konfigurator eines {@link File}.
 *
 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GOwner> Typ des konkreten Nachfahren dieser Klasse. */
public abstract class BaseFilepathData<GOwner> extends BaseValueBuilder<File, GOwner> {

	/** Diese Methode setzt den Wert auf {@link File#File(URI) new File(uri)} und gibt {@code this} zurück.
	 *
	 * @see #useValue(Object)
	 * @see File#File(URI)
	 * @param uri Dateiadresse.
	 * @return {@code this}.
	 * @throws NullPointerException Wenn {@link File#File(URI)} eine entsprechende Ausnahme auslöst. */
	public GOwner useUri(final URI uri) throws NullPointerException {
		return this.useFile(new File(uri));
	}

	public GOwner useFile(final File filepath) throws NullPointerException {
		return this.useValue(filepath);
	}

	/** Diese Methode setzt den Wert auf {@link File#File(File, String) new File(parentFile, fileName)} und gibt {@code this} zurück.
	 *
	 * @see #useValue(Object)
	 * @see File#File(File, String)
	 * @param parentFile Elternpfad.
	 * @param fileName Dateiname.
	 * @return {@code this}.
	 * @throws NullPointerException Wenn {@link File#File(File, String)} eine entsprechende Ausnahme auslöst. */
	public GOwner useFile(final File parentFile, final String fileName) throws NullPointerException {
		return this.useFile(new File(parentFile, fileName));
	}

	/** Diese Methode setzt den Wert auf {@link File#File(String) new File(filePath)} und gibt {@code this} zurück.
	 *
	 * @see #useValue(Object)
	 * @see File#File(String)
	 * @param filePath Dateipfad.
	 * @return {@code this}.
	 * @throws NullPointerException Wenn {@link File#File(String)} eine entsprechende Ausnahme auslöst. */
	public GOwner useString(final String filePath) throws NullPointerException {
		return this.useFile(new File(filePath));
	}

}