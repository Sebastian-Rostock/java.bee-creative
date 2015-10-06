package bee.creative.ini;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import bee.creative.util.Objects;

/**
 * Diese Klasse implementiert ein Objekt zum Schreiben einer {@code INI}-Datenstruktur über einen {@link Writer}, analog zu einem {@link INIReader}.
 * 
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class INIWriter implements Closeable {

	/**
	 * Dieses Feld speichert den {@link Writer}.
	 */
	final Writer writer;

	/**
	 * Dieser Konstruktor initialisiert das {@link File} für die {@code INI}-Datenstruktur.
	 * 
	 * @param file {@link File}.
	 * @throws IOException Wenn {@link FileWriter#FileWriter(File)} eine entsprechende Ausnahme auslöst.
	 * @throws NullPointerException Wenn {@code file} {@code null} ist.
	 */
	public INIWriter(final File file) throws IOException, NullPointerException {
		this(new BufferedWriter(new FileWriter(file)));
	}

	/**
	 * Dieser Konstruktor initialisiert den {@link Writer} für die {@code INI}-Datenstruktur.
	 * 
	 * @param writer {@link Writer}.
	 * @throws NullPointerException Wenn {@code writer} {@code null} ist.
	 */
	public INIWriter(final Writer writer) throws NullPointerException {
		if (writer == null) throw new NullPointerException("writer = null");
		this.writer = writer;
	}

	{}

	/**
	 * Diese Methode schreibt die gegebene Zeichenkette mit Maskierung.
	 * 
	 * @param string Zeichenkette.
	 * @throws IOException Wenn {@link Writer#write(int)} eine entsprechende Ausnahme auslöst.
	 */
	void write(final String string) throws IOException {
		final Writer target = this.writer;
		for (int i = 0, length = string.length(); i < length; i++) {
			final int symbol = string.charAt(i);
			switch (symbol) {
				case '\t':
					target.append("\\t");
				break;
				case '\r':
					target.append("\\r");
				break;
				case '\n':
					target.append("\\n");
				break;
				case '\\':
					target.append("\\\\");
				break;
				case '=':
				case ';':
				case '[':
				case ']':
					target.append('\\');
				default:
					target.append((char)symbol);
			}
		}
	}

	/**
	 * Diese Methode schreibt das gegebene Element.
	 * 
	 * @param token Abschnitt, Eigenschaft oder Kommentar.
	 * @throws IOException Wenn {@link Writer#write(int)} eine entsprechende Ausnahme auslöst.
	 * @throws NullPointerException Wenn {@code token} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Typkennung des Elements ungültig ist.
	 */
	public void write(final INIToken token) throws IOException, NullPointerException, IllegalArgumentException {
		if (token == null) throw new NullPointerException("token = null");
		switch (token.type()) {
			case INIToken.SECTION:
				this.writeSection(token.section());
			break;
			case INIToken.PROPERTY:
				this.writeProperty(token.key(), token.value());
			break;
			case INIToken.COMMENT:
				this.writeComment(token.comment());
			break;
		}
		throw new IllegalArgumentException();
	}

	/**
	 * Diese Methode schreibt einen Abschnitt mit dem gegebenen Namen.
	 * 
	 * @param section Name eines Abschnitts.
	 * @throws IOException Wenn {@link Writer#write(int)} eine entsprechende Ausnahme auslöst.
	 */
	public void writeSection(final String section) throws IOException {
		if (section == null) throw new NullPointerException("section = null");
		final Writer target = this.writer;
		target.write('[');
		this.write(section);
		target.write("]\r\n");
	}

	/**
	 * Diese Methode gibt das zurück.
	 * 
	 * @param key Schlüssel der Eigenschaft.
	 * @param value Wert der Eigenschaft.
	 * @throws IOException Wenn {@link Writer#write(int)} eine entsprechende Ausnahme auslöst.
	 */
	public void writeProperty(final String key, final String value) throws IOException {
		if (key == null) throw new NullPointerException("key = null");
		if (value == null) throw new NullPointerException("value = null");
		final Writer target = this.writer;
		this.write(key);
		target.write('=');
		this.write(value);
		target.write("\r\n");
	}

	/**
	 * Diese Methode gibt das zurück.
	 * 
	 * @param comment Text des Kommentar.
	 * @throws IOException Wenn {@link Writer#write(int)} eine entsprechende Ausnahme auslöst.
	 */
	public void writeComment(final String comment) throws IOException {
		if (comment == null) throw new NullPointerException("comment = null");
		final Writer target = this.writer;
		target.write(';');
		this.write(comment);
		target.write("\r\n");
	}

	{}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() throws IOException {
		this.writer.close();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return Objects.toInvokeString(this, this.writer);
	}

}
