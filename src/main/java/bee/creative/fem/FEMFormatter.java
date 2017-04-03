package bee.creative.fem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import bee.creative.util.Getter;
import bee.creative.util.Iterables;
import bee.creative.util.Objects;

/** Diese Klasse implementiert einen Formatierer, der Daten, Werten und Funktionen in eine Zeichenkette überführen kann.<br>
 * Er realisiert damit die entgegengesetzte Operation zur Kombination von {@link FEMParser} und {@link FEMCompiler}.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMFormatter {

	/** Diese Klasse implementiert eine Markierung, mit welcher die Tiefe und Aktivierung der Einrückung definiert werden kann.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	static final class Mark {

		/** Dieses Feld speichert das Objekt, dass in {@link #items} vor jeder Markierung eingefügt wird. */
		static final FEMFormatter.Mark EMPTY = new FEMFormatter.Mark(0, false, false, false);

		{}

		/** Dieses Feld speichert die Eigenschaften dieser Markierung. */
		int data;

		/** Dieser Konstruktor initialisiert die Markierung.
		 *
		 * @param level Einrücktiefe ({@link #level()}).
		 * @param last Endmarkierung ({@link #isLast()}).
		 * @param space Leerzeichen ({@link #isSpace()}).
		 * @param enabled Aktivierung ({@link #isEnabled()}). */
		public Mark(final int level, final boolean last, final boolean space, final boolean enabled) {
			this.data = (level << 3) | (last ? 1 : 0) | (enabled ? 2 : 0) | (space ? 4 : 0);
		}

		{}

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

		{}

		/** {@inheritDoc} */
		@Override
		public final String toString() {
			return "M" + (this.level() == 0 ? "" : (this.isLast() ? "D" : this.isSpace() ? "S" : "I") + (this.isEnabled() ? "E" : ""));
		}

	}

	{}

	/** Dieses Feld speichert die bisher gesammelten Zeichenketten und Markierungen. */
	final List<Object> items = new ArrayList<Object>();

	/** Dieses Feld speichert den Puffer für {@link #format()}. */
	final StringBuilder string = new StringBuilder();

	/** Dieses Feld speichert den Stack der Hierarchieebenen. */
	final LinkedList<Boolean> indents = new LinkedList<Boolean>();

	/** Dieses Feld speichert die Zeichenkette zur Einrückung, z.B. {@code "\t"} oder {@code "  "}. */
	String indent;

	/** Dieses Feld speichert die Formatierungsmethoden. */
	FEMDomain domain = FEMDomain.NORMAL;

	{}

	/** Dieser Konstruktor initialisiert einen neuen Formetter, welcher {@link FEMDomain#NORMAL} sowie keine {@link FEMFormatter#useIndent(String) Einrückung}
	 * nutzt und über. {@link #reset()} zurückgesetzt wurde. */
	public FEMFormatter() {
		this.reset();
	}

	/** Diese Methode setzt den Formatieren zurück und gibt {@code this} zurück.
	 *
	 * @return {@code this}. */
	public synchronized final FEMFormatter reset() {
		this.items.clear();
		this.string.setLength(0);
		this.indents.clear();
		this.indents.addLast(Boolean.FALSE);
		return this;
	}

	/** Diese Methode fügt die gegebenen Markierung an und gibt {@code this} zurück.
	 *
	 * @param object Markierung.
	 * @return {@code this}. */
	final FEMFormatter putMark(final FEMFormatter.Mark object) {
		this.items.add(object);
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
		return this.putMark(Mark.EMPTY).putMark(new Mark(indents.size(), false, false, false));
	}

	/** Diese Methode markiert das Ende der aktuellen Hierarchieebene, reduziert die Tiefe der Einrückung um eins und gibt {@code this} zurück. Wenn über
	 * {@link #putIndent()} die Einrückung für eine der tieferen Hierarchieebenen aktiviert wurde, wird ein Zeilenumbruch gefolgt von der zur aktuellen Ebene
	 * passenden Einrückung angefügt.
	 *
	 * @see #putBreakInc()
	 * @see #putBreakSpace()
	 * @see #putIndent()
	 * @return {@code this}.
	 * @throws IllegalStateException Wenn zuvor keine Hierarchieebene begonnen wurde. */
	public final FEMFormatter putBreakDec() throws IllegalStateException {
		final LinkedList<Boolean> indents = this.indents;
		final int value = indents.size();
		if (value <= 1) throw new IllegalStateException();
		return this.putMark(Mark.EMPTY).putMark(new Mark(value, true, false, indents.removeLast().booleanValue()));
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
		return this.putMark(Mark.EMPTY).putMark(new Mark(indents.size(), false, true, indents.getLast().booleanValue()));
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
		final int value = indents.size();
		for (int i = 0; i < value; i++) {
			indents.set(i, Boolean.TRUE);
		}
		final List<Object> items = this.items;
		for (int i = items.size() - 2; i >= 0; i--) {
			final Object item = items.get(i);
			if (item == Mark.EMPTY) {
				final FEMFormatter.Mark token = (FEMFormatter.Mark)items.get(i + 1);
				if (token.level() <= value) { // ALTERNATIV: else if (token.level() < value) return this;
					if (token.isEnabled()) return this;
					token.enable();
				}
				i--;
			}
		}
		return this;
	}

	/** Diese Methode fügt die Zeichenkette des gegebenen Objekts an und gibt {@code this} zurück.<br>
	 *
	 * @see Object#toString()
	 * @param part Objekt.
	 * @return {@code this}.
	 * @throws IllegalArgumentException Wenn {@code part} nicht formatiert werden kann. */
	public final FEMFormatter put(final Object part) throws IllegalArgumentException {
		this.items.add(part.toString());
		return this;
	}

	/** Diese Methode fügt die Zeichenkette des gegebenen Objekts an und gibt {@code this} zurück.<br>
	 *
	 * @see FEMDomain#formatData(FEMFormatter, Object)
	 * @param data Objekt.
	 * @return {@code this}.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code data} nicht formatiert werden kann. */
	public final FEMFormatter putData(final Object data) throws NullPointerException, IllegalArgumentException {
		this.domain.formatData(this, Objects.assertNotNull(data));
		return this;
	}

	/** Diese Methode fügt die gegebenen Wertliste an und gibt {@code this} zurück.<br>
	 * Wenn die Liste leer ist, wird {@code "[]"} angefügt. Andernfalls werden die Werte in {@code "["} und {@code "]"} eingeschlossen sowie mit {@code ";"}
	 * separiert über {@link #putFunction(FEMFunction)} angefügt. Nach der öffnenden Klammer {@link #putBreakInc() beginnt} dabei eine neue Hierarchieebene, die
	 * vor der schließenden Klammer {@link #putBreakDec() endet}. Nach jedem Trennzeichen wird ein {@link #putBreakSpace() bedingtes Leerzeichen} eingefügt.<br>
	 * Die aktuelle Hierarchieebene wird als einzurücken {@link #putIndent() markiert}, wenn die Wertliste mehr als ein Element enthält.
	 *
	 * @see #putFunction(FEMFunction)
	 * @see #putBreakInc()
	 * @see #putBreakDec()
	 * @see #putBreakSpace()
	 * @see #putIndent()
	 * @param array Wertliste.
	 * @return {@code this}.
	 * @throws NullPointerException Wenn {@code array} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code array} nicht formatiert werden kann. */
	public final FEMFormatter putArray(final Iterable<? extends FEMValue> array) throws NullPointerException, IllegalArgumentException {
		final Iterator<? extends FEMValue> iter = array.iterator();
		if (iter.hasNext()) {
			FEMValue item = iter.next();
			if (iter.hasNext()) {
				this.putIndent().put("[").putBreakInc().putFunction(item);
				do {
					item = iter.next();
					this.put(";").putBreakSpace().putFunction(item);
				} while (iter.hasNext());
				this.putBreakDec().put("]");
			} else {
				this.put("[").putBreakInc().putFunction(item).putBreakDec().put("]");
			}
		} else {
			this.put("[]");
		}
		return this;
	}

	/** Diese Methode fügt den Quelltext der Liste der gegebenen zugesicherten Parameterwerte eines Stapelrahmens an und gibt {@code this} zurück.<br>
	 * Wenn diese Liste leer ist, wird {@code "()"} angefügt. Andernfalls werden die nummerierten Parameterwerte in {@code "("} und {@code ")"} eingeschlossen,
	 * sowie mit {@code ";"} separiert über {@link #putFunction(FEMFunction)} angefügt. Vor jedem Parameterwert wird dessen logische Position {@code i} als
	 * {@code "$i: "} angefügt. Nach der öffnenden Klammer {@link #putBreakInc() beginnt} dabei eine neue Hierarchieebene, die vor der schließenden Klammer
	 * {@link #putBreakDec() endet}. Nach jedem Trennzeichen wird ein {@link #putBreakSpace() bedingtes Leerzeichen} eingefügt.<br>
	 * Die aktuelle Hierarchieebene wird als einzurücken {@link #putIndent() markiert}, wenn die Wertliste mehr als ein Element enthält.
	 *
	 * @see #putFunction(FEMFunction)
	 * @see #putBreakInc()
	 * @see #putBreakDec()
	 * @see #putBreakSpace()
	 * @see #putIndent()
	 * @param params Stapelrahmen.
	 * @return {@code this}.
	 * @throws NullPointerException Wenn {@code params} {@code null} ist oder enthält.
	 * @throws IllegalArgumentException Wenn {@code params} nicht formatiert werden kann. */
	public final FEMFormatter putFrame(final Iterable<? extends FEMValue> params) throws NullPointerException, IllegalArgumentException {
		final Iterator<? extends FEMValue> iter = params.iterator();
		if (iter.hasNext()) {
			FEMValue item = iter.next();
			if (iter.hasNext()) {
				this.putIndent().put("(").putBreakInc().put("$1: ").putFunction(item);
				int index = 2;
				do {
					item = iter.next();
					this.put(";").putBreakSpace().put("$").put(new Integer(index)).put(": ").putFunction(item);
					index++;
				} while (iter.hasNext());
				this.putBreakDec().put(")");
			} else {
				this.put("(").putBreakInc().put("$1: ").putFunction(item).putBreakDec().put(")");
			}
		} else {
			this.put("()");
		}
		return this;
	}

	/** Diese Methode fügt den Quelltext der Liste der gegebenen Parameterfunktionen an und gibt {@code this} zurück.<br>
	 * Wenn die Liste leer ist, wird {@code "()"} angefügt. Andernfalls werden die Parameterfunktionen in {@code "("} und {@code ")"} eingeschlossen sowie mit
	 * {@code ";"} separiert über {@link #putFunction(FEMFunction)} angefügt. Nach der öffnenden Klammer {@link #putBreakInc() beginnt} dabei eine neue
	 * Hierarchieebene, die vor der schließenden Klammer {@link #putBreakDec() endet}. Nach jedem Trennzeichen wird ein {@link #putBreakSpace() bedingtes
	 * Leerzeichen} eingefügt.<br>
	 * Die aktuelle Hierarchieebene wird als einzurücken {@link #putIndent() markiert}, wenn die Funktionsliste mehr als ein Element enthält.
	 *
	 * @see #putFunction(FEMFunction)
	 * @see #putBreakInc()
	 * @see #putBreakDec()
	 * @see #putBreakSpace()
	 * @see #putIndent()
	 * @param params Funktionsliste.
	 * @return {@code this}.
	 * @throws NullPointerException Wenn {@code params} {@code null} ist oder enthält.
	 * @throws IllegalArgumentException Wenn {@code params} nicht formatiert werden kann. */
	public final FEMFormatter putParams(final Iterable<? extends FEMFunction> params) throws NullPointerException, IllegalArgumentException {
		final Iterator<? extends FEMFunction> iter = params.iterator();
		if (iter.hasNext()) {
			FEMFunction item = iter.next();
			if (iter.hasNext()) {
				this.putIndent().put("(").putBreakInc().putFunction(item);
				do {
					item = iter.next();
					this.put(";").putBreakSpace().putFunction(item);
				} while (iter.hasNext());
				this.putBreakDec().put(")");
			} else {
				this.put("(").putBreakInc().putFunction(item).putBreakDec().put(")");
			}
		} else {
			this.put("()");
		}
		return this;
	}

	/** Diese Methode fügt die gegebenen, parametrisierte Funktion an und gibt {@code this} zurück.<br>
	 * Die parametrisierte Funktion wird dabei in <code>"{: "</code> und <code>"}"</code> eingeschlossen und über {@link #putFunction(FEMFunction)} angefügt.
	 *
	 * @see #putFunction(FEMFunction)
	 * @param function parametrisierte Funktion.
	 * @return {@code this}.
	 * @throws NullPointerException Wenn {@code function} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code function} nicht formatiert werden kann. */
	public final FEMFormatter putHandler(final FEMFunction function) throws NullPointerException, IllegalArgumentException {
		Objects.assertNotNull(function);
		return this.put("{: ").putFunction(function).put("}");
	}

	/** Diese Methode fügt den Quelltext der gegebenen Funktion an und gibt {@code this} zurück.<br>
	 *
	 * @see FEMDomain#formatFunction(FEMFormatter, FEMFunction)
	 * @param function Funktion.
	 * @return {@code this}.
	 * @throws NullPointerException Wenn {@code function} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code function} nicht formatiert werden kann. */
	public final FEMFormatter putFunction(final FEMFunction function) throws NullPointerException, IllegalArgumentException {
		this.domain.formatFunction(this, Objects.assertNotNull(function));
		return this;
	}

	/** Diese Methode gibt die Zeichenkette zur Einrückung einer Hierarchieebene zurück. Diese ist {@code null}, wenn nicht eingerückt wird.
	 *
	 * @return Zeichenkette zur Einrückung oder {@code null}. */
	public final String getIndent() {
		return this.indent;
	}

	/** Diese Methode gibt die genutzten Formatierungsmethoden zurück.
	 *
	 * @return Formatierungsmethoden. */
	public final FEMDomain getDomain() {
		return this.domain;
	}

	/** Diese Methode setzt die Zeichenkette zur Einrückung einer Hierarchieebene und gibt {@code this} zurück.<br>
	 * Wenn diese {@code null} ist, wird nicht eingerückt.
	 *
	 * @param indent Zeichenkette zur Einrückung (z.B. {@code null}, {@code "\t"} oder {@code "  "}).
	 * @return {@code this}. */
	public synchronized final FEMFormatter useIndent(final String indent) {
		this.indent = indent;
		return this;
	}

	/** Diese Methode gibt setzt die zu nutzenden Formatierungsmethoden und gibt {@code this} zurück.
	 *
	 * @param value Formatierungsmethoden.
	 * @return {@code this}.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist. */
	public synchronized final FEMFormatter useDomain(final FEMDomain value) throws NullPointerException {
		this.domain = Objects.assertNotNull(value);
		return this;
	}

	/** Diese Methode gibt die Verkettung der bisher gesammelten Zeichenketten als Quelltext zurück.
	 *
	 * @see #put(Object)
	 * @return Quelltext. */
	public final String format() {
		final String indent = this.indent;
		final List<Object> items = this.items;
		final StringBuilder string = this.string;
		final int size = items.size();
		for (int i = 0; i < size;) {
			final Object item = items.get(i++);
			if (item == Mark.EMPTY) {
				final FEMFormatter.Mark token = (FEMFormatter.Mark)items.get(i++);
				if (token.isEnabled() && (indent != null)) {
					string.append('\n');
					for (int count = token.level() - (token.isLast() ? 2 : 1); count > 0; count--) {
						string.append(indent);
					}
				} else if (token.isSpace()) {
					string.append(' ');
				}
			} else {
				string.append(item);
			}
		}
		return string.toString();
	}

	/** Diese Methode formatiert die gegebenen Elemente in einen Quelltext und gibt diesen zurück.<br>
	 * Die Elemente werden über den gegebenen {@link Getter} angefügt und mit {@code ';'} separiert. In der Methode {@link Getter#get(Object)} sollten hierfür
	 * {@link #putData(Object)} bzw. {@link #putFunction(FEMFunction)} aufgerufen werden.
	 *
	 * @see #formatDatas(Iterable)
	 * @see #formatFunctions(Iterable)
	 * @param <GItem> Typ der Elemente.
	 * @param items Elemente.
	 * @param formatter {@link Getter} zur Aufruf der spetifischen Formatierungsmethoden je Element.
	 * @return formatierter Quelltext.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist oder enthält.
	 * @throws IllegalArgumentException Wenn ein Element nicht formatiert werden kann. */
	final <GItem> String formatIterable(final Iterable<? extends GItem> items, final Getter<GItem, ?> formatter)
		throws NullPointerException, IllegalArgumentException {
		final Iterator<? extends GItem> iter = items.iterator();
		if (!iter.hasNext()) return "";
		GItem item = iter.next();
		if (iter.hasNext()) {
			this.putIndent();
			formatter.get(item);
			do {
				item = iter.next();
				this.put(";").putBreakSpace();
				formatter.get(item);
			} while (iter.hasNext());
		} else {
			formatter.get(item);
		}
		return this.format();
	}

	/** Diese Methode formatiert das gegebene Objekt in einen Quelltext und gibt diesen zurück.<br>
	 * Der Rückgabewert entspricht {@code this.formatDatas(Iterables.itemIterable(object))}.
	 *
	 * @see #formatDatas(Iterable)
	 * @param object Objekt.
	 * @return formatierter Quelltext.
	 * @throws NullPointerException Wenn {@code object} {@code null} ist.
	 * @throws IllegalArgumentException Wenn das Object nicht formatiert werden kann. */
	public final String formatData(final Object object) throws NullPointerException, IllegalArgumentException {
		return this.formatDatas(Iterables.itemIterable(object));
	}

	/** Diese Methode formatiert die gegebenen Objekt in einen Quelltext und gibt diesen zurück.<br>
	 * Die Objekt werden über {@link #putData(Object)} angefügt und mit {@code ';'} separiert.
	 *
	 * @see #putData(Object)
	 * @param objects Objekte.
	 * @return formatierter Quelltext.
	 * @throws NullPointerException Wenn {@code objects} {@code null} ist oder enthält.
	 * @throws IllegalArgumentException Wenn ein Objekt nicht formatiert werden kann. */
	public final String formatDatas(final Iterable<?> objects) throws NullPointerException, IllegalArgumentException {
		return this.formatIterable(objects, new Getter<Object, Object>() {

			@Override
			public Object get(final Object input) {
				return FEMFormatter.this.putData(input);
			}

		});
	}

	/** Diese Methode formatiert die gegebene Funktion in einen Quelltext und gibt diesen zurück.<br>
	 * Der Rückgabewert entspricht {@code this.formatFunction(Iterables.itemIterable(function))}.
	 *
	 * @see #formatFunctions(Iterable)
	 * @param function Funktion.
	 * @return formatierter Quelltext.
	 * @throws NullPointerException Wenn {@code function} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Funktion nicht formatiert werden kann. */
	public final String formatFunction(final FEMFunction function) throws NullPointerException, IllegalArgumentException {
		return this.formatFunctions(Iterables.itemIterable(function));
	}

	/** Diese Methode formatiert die gegebenen Funktionen in einen Quelltext und gibt diesen zurück.<br>
	 * Die Funktionen werden über {@link #putFunction(FEMFunction)} angefügt und mit {@code ';'} separiert.
	 *
	 * @see #putFunction(FEMFunction)
	 * @param functions Funktionen.
	 * @return formatierter Quelltext.
	 * @throws NullPointerException Wenn {@code functions} {@code null} ist oder enthält.
	 * @throws IllegalArgumentException Wenn eine Funktion nicht formatiert werden kann. */
	public final String formatFunctions(final Iterable<? extends FEMFunction> functions) throws NullPointerException, IllegalArgumentException {
		return this.formatIterable(functions, new Getter<FEMFunction, Object>() {

			@Override
			public Object get(final FEMFunction input) {
				return FEMFormatter.this.putFunction(input);
			}

		});
	}

	{}

	/** {@inheritDoc} */
	@Override
	public final String toString() {
		return Objects.toInvokeString(this, this.domain, this.indent, this.items);
	}

}