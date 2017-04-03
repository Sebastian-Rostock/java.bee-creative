package bee.creative.ini;

import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;
import bee.creative.util.IO;
import bee.creative.util.Objects;

/** Diese Klasse implementiert ein Objekt zum Schreiben einer {@code INI}-Datenstruktur über einen {@link Writer}, analog zu einem {@link INIReader}.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class INIWriter implements Closeable {

	/** Diese Methode erzeugt aus dem gegebenen Objekt einen {@link INIWriter} und gibt diesen zurück.<br>
	 * Wenn das Objekt ein {@link INIWriter} ist, wird dieser geliefert. Andernfalls wird das Objekt in einen {@link Writer} {@link IO#outputWriterFrom(Object)
	 * überführt}.
	 *
	 * @see IO#outputWriterFrom(Object)
	 * @see INIWriter#INIWriter(Writer)
	 * @param object Objekt.
	 * @return {@link INIWriter}.
	 * @throws IOException Wenn der {@link INIWriter} nicht erzeugt werden kann. */
	public static INIWriter from(final Object object) throws IOException {
		if (object instanceof INIWriter) return (INIWriter)object;
		return new INIWriter(IO.outputWriterFrom(object));
	}

	{}

	/** Dieses Feld speichert den {@link Writer}. */
	final Writer writer;

	/** Dieser Konstruktor initialisiert den {@link Writer} für die {@code INI}-Datenstruktur.
	 *
	 * @param writer {@link Writer}.
	 * @throws NullPointerException Wenn {@code writer} {@code null} ist. */
	public INIWriter(final Writer writer) throws NullPointerException {
		this.writer = Objects.assertNotNull(writer);
	}

	{}

	/** Diese Methode schreibt die gegebene Zeichenkette mit Maskierung.
	 *
	 * @param string Zeichenkette.
	 * @throws IOException Wenn {@link Writer#write(int)} eine entsprechende Ausnahme auslöst. */
	final void write(final String string) throws IOException {
		final Writer target = this.writer;
		for (int i = 0, length = string.length(); i < length; i++) {
			final char symbol = string.charAt(i);
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
					target.append(symbol);
			}
		}
	}

	/** Diese Methode schreibt das gegebene Element.
	 *
	 * @param token Abschnitt, Eigenschaft oder Kommentar.
	 * @throws IOException Wenn {@link Writer#write(int)} eine entsprechende Ausnahme auslöst.
	 * @throws NullPointerException Wenn {@code token} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Typkennung des Elements ungültig ist. */
	public final void write(final INIToken token) throws IOException, NullPointerException, IllegalArgumentException {
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

	/** Diese Methode schreibt einen Abschnitt mit dem gegebenen Namen.
	 *
	 * @param section Name eines Abschnitts.
	 * @throws IOException Wenn {@link Writer#write(int)} eine entsprechende Ausnahme auslöst. */
	public final void writeSection(final String section) throws IOException {
		Objects.assertNotNull(section);
		final Writer target = this.writer;
		target.write('[');
		this.write(section);
		target.write("]\r\n");
	}

	/** Diese Methode gibt das zurück.
	 *
	 * @param key Schlüssel der Eigenschaft.
	 * @param value Wert der Eigenschaft.
	 * @throws IOException Wenn {@link Writer#write(int)} eine entsprechende Ausnahme auslöst. */
	public final void writeProperty(final String key, final String value) throws IOException {
		Objects.assertNotNull(key);
		Objects.assertNotNull(value);
		final Writer target = this.writer;
		this.write(key);
		target.write('=');
		this.write(value);
		target.write("\r\n");
	}

	/** Diese Methode gibt das zurück.
	 *
	 * @param comment Text des Kommentar.
	 * @throws IOException Wenn {@link Writer#write(int)} eine entsprechende Ausnahme auslöst. */
	public final void writeComment(final String comment) throws IOException {
		Objects.assertNotNull(comment);
		final Writer target = this.writer;
		target.write(';');
		this.write(comment);
		target.write("\r\n");
	}

	{}

	/** {@inheritDoc} */
	@Override
	public final void close() throws IOException {
		this.writer.close();
	}

	/** {@inheritDoc} */
	@Override
	public final String toString() {
		return Objects.toInvokeString(this, this.writer);
	}

}
