package bee.creative.fem;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import bee.creative.util.Objects;

/** Diese Klasse implementiert einen Formatierer, der Daten, Werten und Funktionen in eine Zeichenkette überführen kann.<br>
 * Er realisiert damit die entgegengesetzte Operation zur Kombination von {@link FEMParser} und {@link FEMCompiler}.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMFormatter {

	/** Diese Klasse implementiert eine Markierung, mit welcher die Tiefe und Aktivierung der Einrückung definiert werden kann.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	static final class Token {

		/** Dieses Feld speichert die Eigenschaften dieser Markierung. */
		int data;

		/** Dieser Konstruktor initialisiert die Markierung.
		 *
		 * @param level Einrücktiefe ({@link #level()}).
		 * @param last Endmarkierung ({@link #isLast()}).
		 * @param space Leerzeichen ({@link #isSpace()}).
		 * @param enabled Aktivierung ({@link #isEnabled()}). */
		public Token(final int level, final boolean last, final boolean space, final boolean enabled) {
			this.data = (level << 3) | (last ? 1 : 0) | (enabled ? 2 : 0) | (space ? 4 : 0);
		}

		/** Diese Methode gibt die Tiefe der Einrückung zurück.
		 *
		 * @return Tiefe der Einrückung. */
		public final int level() {
			return this.data >> 3;
		}

		/** Diese Methode aktiviert die Einrückung. */
		public final void enable() {
			this.data |= 2;
		}

		/** Diese Methode gibt nur dann {@code true} zurück, wenn dieses Objekt das Ende einer Einrückungsebene markiert.
		 *
		 * @return {@code true} bei einer Endmarkierung. */
		public final boolean isLast() {
			return (this.data & 1) != 0;
		}

		/** Diese Methode gibt nur dann {@code true} zurück, wenn dieses Objekt ein bedingtes Leerzeichen markiert.
		 *
		 * @return {@code true} bei einem bedingten Leerzeichen. */
		public final boolean isSpace() {
			return (this.data & 4) != 0;
		}

		/** Diese Methode gibt nur dann {@code true} zurück, wenn die Einrückung aktiviert ist.
		 *
		 * @return Aktivierung. */
		public final boolean isEnabled() {
			return (this.data & 2) != 0;
		}

		/** {@inheritDoc} */
		@Override
		public final String toString() {
			return "T" + (this.level() == 0 ? "" : (this.isLast() ? "-" : this.isSpace() ? "=" : "+") + (this.isEnabled() ? "E" : ""));
		}

	}

	/** Dieses Feld speichert die bisher gesammelten Zeichenketten und Markierungen. */
	final List<Object> items = new ArrayList<>();

	/** Dieses Feld speichert den Stack der Hierarchieebenen. */
	final LinkedList<Boolean> indents = new LinkedList<>();

	/** Dieses Feld speichert die Zeichenkette zur Einrückung, z.B. {@code "\t"} oder {@code "  "}. */
	String indent;

	/** Dieser Konstruktor initialisiert einen neuen Formetter, welcher keine {@link FEMFormatter#useIndent(String) Einrückung} nutzt. */
	public FEMFormatter() {
		this.reset();
	}

	/** Diese Methode setzt den Formatieren zurück und gibt {@code this} zurück.
	 *
	 * @return {@code this}. */
	public final FEMFormatter reset() {
		this.items.clear();
		this.indents.clear();
		this.indents.addLast(Boolean.FALSE);
		return this;
	}

	/** Diese Methode fügt die Zeichenkette des gegebenen Objekts an und gibt {@code this} zurück.<br>
	 * Wenn das Objekt {@code null} ist, wird es ignoriert.
	 *
	 * @see Object#toString()
	 * @param token Objekt oder {@code null}.
	 * @return {@code this}.
	 * @throws IllegalArgumentException Wenn {@code part} nicht formatiert werden kann. */
	public final FEMFormatter putToken(final Object token) throws IllegalArgumentException {
		if (token == null) return this;
		this.items.add(token.toString());
		return this;
	}

	/** Diese Methode markiert den Beginn einer neuen Hierarchieebene, erhöht die Tiefe der Einrückung um eins und gibt {@code this} zurück. Wenn über
	 * {@link #putIndent()} die Einrückung für diese Hierarchieebene aktiviert wurde, wird ein Zeilenumbruch gefolgt von der zur Ebene passenden Einrückung
	 * angefügt.
	 *
	 * @see #putBreakDec()
	 * @see #putBreakSpace()
	 * @see #putIndent()
	 * @return {@code this}. */
	public final FEMFormatter putBreakInc() {
		final LinkedList<Boolean> indents = this.indents;
		indents.addLast(Boolean.FALSE);
		this.items.add(new Token(indents.size(), false, false, false));
		return this;
	}

	/** Diese Methode markiert das Ende der aktuellen Hierarchieebene, reduziert die Tiefe der Einrückung um eins und gibt {@code this} zurück. Wenn über
	 * {@link #putIndent()} die Einrückung für eine der tieferen Hierarchieebenen aktiviert wurde, wird ein Zeilenumbruch gefolgt von der zur aktuellen Ebene
	 * passenden Einrückung angefügt.
	 *
	 * @see #putBreakInc()
	 * @see #putBreakSpace()
	 * @see #putIndent()
	 * @return {@code this}. */
	public final FEMFormatter putBreakDec() {
		final LinkedList<Boolean> indents = this.indents;
		final int value = indents.size();
		this.items.add(new Token(value, true, false, indents.removeLast().booleanValue()));
		return this;
	}

	/** Diese Methode fügt ein bedingtes Leerzeichen an und gibt {@code this} zurück. Wenn über {@link #putIndent()} die Einrückung für die aktuelle
	 * Hierarchieebene aktiviert wurde, wird statt eines Leerzeichens ein Zeilenumbruch gefolgt von der zur Ebene passenden Einrückung angefügt.
	 *
	 * @see #putBreakInc()
	 * @see #putBreakDec()
	 * @see #putIndent()
	 * @return {@code this}. */
	public final FEMFormatter putBreakSpace() {
		final LinkedList<Boolean> indents = this.indents;
		this.items.add(new Token(indents.size(), false, true, indents.getLast().booleanValue()));
		return this;
	}

	/** Diese Methode markiert die aktuelle sowie alle übergeordneten Hierarchieebenen als einzurücken und gibt {@code this} zurück. Beginn und Ende einer
	 * Hierarchieebene werden über {@link #putBreakInc()} und {@link #putBreakDec()} markiert.
	 *
	 * @see #putBreakSpace()
	 * @see #putBreakInc()
	 * @see #putBreakDec()
	 * @return {@code this}. */
	public final FEMFormatter putIndent() {
		final LinkedList<Boolean> indents = this.indents;
		if (this.indents.getLast().booleanValue()) return this;
		int value = indents.size();
		for (int i = 0; i < value; i++) {
			indents.set(i, Boolean.TRUE);
		}
		final List<Object> items = this.items;
		for (int i = items.size() - 1; i >= 0; i--) {
			final Object item = items.get(i);
			if (item instanceof Token) {
				final Token token = (Token)item;
				final int level = token.level();
				if (level <= value) { // ALTERNATIV: else if (token.level() < value) return this;
					if (token.isEnabled()) return this;
					token.enable();
					value = level;
				}
				i--;
			}
		}
		return this;
	}

	/** Diese Methode gibt die Zeichenkette zur Einrückung einer Hierarchieebene zurück. Diese ist {@code null}, wenn nicht eingerückt wird.
	 *
	 * @return Zeichenkette zur Einrückung oder {@code null}. */
	public final String getIndent() {
		return this.indent;
	}

	/** Diese Methode setzt die Zeichenkette zur Einrückung einer Hierarchieebene und gibt {@code this} zurück.<br>
	 * Wenn diese {@code null} ist, wird nicht eingerückt.
	 *
	 * @param indent Zeichenkette zur Einrückung (z.B. {@code null}, {@code "\t"} oder {@code "  "}).
	 * @return {@code this}. */
	public final FEMFormatter useIndent(final String indent) {
		this.indent = indent;
		return this;
	}

	/** Diese Methode gibt die Verkettung der bisher gesammelten Zeichenketten als Quelltext zurück.
	 *
	 * @see #putToken(Object)
	 * @return Quelltext. */
	public final String format() {
		final String indent = this.indent;
		final List<Object> items = this.items;
		final StringBuilder result = new StringBuilder();
		final int size = items.size();
		for (int i = 0; i < size;) {
			final Object item = items.get(i++);
			if (item instanceof Token) {
				final Token token = (Token)item;
				if (token.isEnabled() && (indent != null)) {
					result.append('\n');
					for (int count = token.level() - (token.isLast() ? 3 : 2); count >= 0; count--) {
						result.append(indent);
					}
				} else if (token.isSpace()) {
					result.append(' ');
				}
			} else {
				result.append(item);
			}
		}
		return result.toString();
	}

	/** {@inheritDoc} */
	@Override
	public final String toString() {
		return Objects.toInvokeString(this, this.indent, this.items);
	}

}