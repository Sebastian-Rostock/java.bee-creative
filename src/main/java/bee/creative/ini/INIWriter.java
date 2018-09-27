package bee.creative.ini;

import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;
import bee.creative.io.IO;
import bee.creative.util.Objects;

/** Diese Klasse implementiert ein Objekt zum Schreiben einer {@code INI}-Datenstruktur über einen {@link Writer}, analog zu einem {@link INIReader}.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class INIWriter implements Closeable {

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

	/** Dieses Feld speichert den {@link Writer}. */
	protected final Writer writer;

	/** Dieser Konstruktor initialisiert den {@link Writer} für die {@code INI}-Datenstruktur.
	 *
	 * @param writer {@link Writer}.
	 * @throws NullPointerException Wenn {@code writer} {@code null} ist. */
	public INIWriter(final Writer writer) throws NullPointerException {
		this.writer = Objects.notNull(writer);
	}

	/** Diese Methode schreibt das gegebene Element und gibt {@code this} zurück.
	 *
	 * @param token Abschnitt, Eigenschaft oder Kommentar.
	 * @return {@code this}.
	 * @throws IOException Wenn {@link Writer#write(int)} eine entsprechende Ausnahme auslöst.
	 * @throws NullPointerException Wenn {@code token} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Typkennung des Elements ungültig ist. */
	public INIWriter write(final INIToken token) throws IOException, NullPointerException, IllegalArgumentException {
		synchronized (this.writer) {
			this.writeImpl(token);
		}
		return this;
	}

	@SuppressWarnings ("javadoc")
	final void writeImpl(final INIToken token) throws IOException, NullPointerException, IllegalArgumentException {
		switch (token.type()) {
			case INIToken.SECTION:
				this.writeSectionImpl(token.section());
			break;
			case INIToken.PROPERTY:
				this.writePropertyImpl(token.key(), token.value());
			break;
			case INIToken.COMMENT:
				this.writeCommentImpl(token.comment());
			break;
		}
		throw new IllegalArgumentException();
	}

	/** Diese Methode schreibt die gegebene Zeichenkette mit Maskierung.
	 *
	 * @param string Zeichenkette.
	 * @throws IOException Wenn {@link Writer#write(int)} eine entsprechende Ausnahme auslöst. */
	final void writeStringImpl(final String string) throws IOException {
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

	/** Diese Methode schreibt einen Abschnitt mit dem gegebenen Namen.
	 *
	 * @param section Name eines Abschnitts.
	 * @return {@code this}.
	 * @throws IOException Wenn {@link Writer#write(int)} eine entsprechende Ausnahme auslöst. */
	public INIWriter writeSection(final Object section) throws IOException {
		synchronized (this.writer) {
			this.writeSectionImpl(section.toString());
		}
		return this;
	}

	@SuppressWarnings ("javadoc")
	final void writeSectionImpl(final String section) throws IOException {
		this.writer.write('[');
		this.writeStringImpl(section);
		this.writer.write("]\r\n");
	}

	/** Diese Methode schreibt eine Eigenschaft und gibt {@code this} zurück.
	 *
	 * @param key Schlüssel der Eigenschaft.
	 * @param value Wert der Eigenschaft.
	 * @return {@code this}.
	 * @throws IOException Wenn {@link Writer#write(int)} eine entsprechende Ausnahme auslöst. */
	public INIWriter writeProperty(final Object key, final Object value) throws IOException {
		synchronized (this.writer) {
			this.writePropertyImpl(key.toString(), value.toString());
		}
		return this;
	}

	@SuppressWarnings ("javadoc")
	final void writePropertyImpl(final String key, final String value) throws IOException {
		this.writeStringImpl(key);
		this.writer.write('=');
		this.writeStringImpl(value);
		this.writer.write("\r\n");
	}

	/** Diese Methode schreibt einen Kommentar und gibt {@code this} zurück.
	 *
	 * @param comment Kommentar.
	 * @throws IOException Wenn {@link Writer#write(int)} eine entsprechende Ausnahme auslöst. */
	public void writeComment(final Object comment) throws IOException {
		synchronized (this.writer) {
			this.writeCommentImpl(comment.toString());
		}
	}

	@SuppressWarnings ("javadoc")
	final void writeCommentImpl(final String comment) throws IOException {
		this.writer.write(';');
		this.writeStringImpl(comment);
		this.writer.write("\r\n");
	}

	/** {@inheritDoc} */
	@Override
	public void close() throws IOException {
		synchronized (this.writer) {
			this.writer.close();
		}
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		synchronized (this.writer) {
			return Objects.toInvokeString(this, this.writer);
		}
	}

}
