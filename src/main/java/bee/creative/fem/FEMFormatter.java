package bee.creative.fem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import bee.creative.fem.FEM.ScriptCompiler;
import bee.creative.fem.FEM.ScriptParser;
import bee.creative.util.Converter;
import bee.creative.util.Iterables;
import bee.creative.util.Objects;

/** Diese Klasse implementiert einen Formatierer, der Daten, Werten und Funktionen in eine Zeichenkette überführen kann.<br>
 * Er realisiert damit die entgegengesetzte Operation zur Kombination von {@link ScriptParser} und {@link ScriptCompiler}.
 * 
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMFormatter {

	/** Diese Klasse implementiert eine Markierung, mit welcher die Tiefe und Aktivierung der Einrückung definiert werden kann.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	static final class Mark {

		/** Dieses Feld speichert das Objekt, dass in {@link #_items_} vor jeder Markierung eingefügt wird. */
		static final FEMFormatter.Mark EMPTY = new FEMFormatter.Mark(0, false, false, false);

		{}

		/** Dieses Feld speichert die Eigenschaften dieser Markierung. */
		int _data_;

		/** Dieser Konstruktor initialisiert die Markierung.
		 * 
		 * @param level Einrücktiefe ({@link #level()}).
		 * @param last Endmarkierung ({@link #isLast()}).
		 * @param space Leerzeichen ({@link #isSpace()}).
		 * @param enabled Aktivierung ({@link #isEnabled()}). */
		public Mark(final int level, final boolean last, final boolean space, final boolean enabled) {
			this._data_ = (level << 3) | (last ? 1 : 0) | (enabled ? 2 : 0) | (space ? 4 : 0);
		}

		{}

		/** Diese Methode gibt die Tiefe der Einrückung zurück.
		 * 
		 * @return Tiefe der Einrückung. */
		public final int level() {
			return this._data_ >> 3;
		}

		/** Diese Methode aktiviert die Einrückung. */
		public final void enable() {
			this._data_ |= 2;
		}

		/** Diese Methode gibt nur dann {@code true} zurück, wenn dieses Objekt das Ende einer Einrückungsebene markiert.
		 * 
		 * @return {@code true} bei einer Endmarkierung. */
		public final boolean isLast() {
			return (this._data_ & 1) != 0;
		}

		/** Diese Methode gibt nur dann {@code true} zurück, wenn dieses Objekt ein bedingtes Leerzeichen markiert.
		 * 
		 * @return {@code true} bei einem bedingten Leerzeichen. */
		public final boolean isSpace() {
			return (this._data_ & 4) != 0;
		}

		/** Diese Methode gibt nur dann {@code true} zurück, wenn die Einrückung aktiviert ist.
		 * 
		 * @return Aktivierung. */
		public final boolean isEnabled() {
			return (this._data_ & 2) != 0;
		}

		{}

		/** {@inheritDoc} */
		@Override
		public final String toString() {
			return "M" + (this.level() == 0 ? "" : (this.isLast() ? "D" : this.isSpace() ? "S" : "I") + (this.isEnabled() ? "E" : ""));
		}

	}

	/** Diese Schnittstelle definiert ein Objekt, welches sich selbst in seine Quelltextdarstellung überführen und diese an einen {@link FEMFormatter} anfügen
	 * kann.
	 * 
	 * @see FEMFormatterHelper#formatData(FEMFormatter, Object)
	 * @see FEMFormatterHelper#formatValue(FEMFormatter, FEMValue)
	 * @see FEMFormatterHelper#formatFunction(FEMFormatter, FEMFunction)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static interface FEMFormatterInput {

		/** Diese Methode formatiert dieses Objekt in einen Quelltext und fügt diesen an den gegebenen {@link FEMFormatter} an.<br>
		 * Sie kann von einem {@link FEMFormatterHelper} im Rahmen folgender Methoden aufgerufen:
		 * <ul>
		 * <li>{@link FEMFormatterHelper#formatData(FEMFormatter, Object)}</li>
		 * <li>{@link FEMFormatterHelper#formatValue(FEMFormatter, FEMValue)}</li>
		 * <li>{@link FEMFormatterHelper#formatFunction(FEMFormatter, FEMFunction)}</li>
		 * </ul>
		 * 
		 * @param target {@link FEMFormatter}.
		 * @throws IllegalArgumentException Wenn das Objekt nicht formatiert werden kann. */
		public void toScript(final FEMFormatter target) throws IllegalArgumentException;

	}

	/** Diese Schnittstelle definiert Formatierungsmethoden, die in den Methoden {@link FEMFormatter#putValue(FEMValue)} und
	 * {@link FEMFormatter#putFunction(FEMFunction)} zur Übersetzung von Werten und Funktionen in Quelltexte genutzt werden.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static interface FEMFormatterHelper {

		/** Dieses Feld speichert den {@link FEMFormatterHelper}, dessen Methoden ihre Eingeben über {@link String#valueOf(Object)} formatieren.<br>
		 * {@link FEMScript Aufbereitete Quelltexte} werden in {@link #formatData(FEMFormatter, Object)} analog zur Interpretation des {@link ScriptCompiler}
		 * formatiert. Daten, Werte und Funktionen mit der Schnittstelle {@link FEMFormatterInput} werden über deren
		 * {@link FEMFormatterInput#toScript(FEMFormatter)}-Methode formatiert. */
		static FEMFormatter.FEMFormatterHelper EMPTY = new FEMFormatterHelper() {

			@Override
			public void formatData(final FEMFormatter target, final Object data) throws IllegalArgumentException {
				if (data instanceof FEMScript) {
					FEM.scriptCompiler().useScript((FEMScript)data).formatScript(target);
				} else if (data instanceof FEMFormatter.FEMFormatterInput) {
					((FEMFormatter.FEMFormatterInput)data).toScript(target);
				} else {
					target.put(String.valueOf(data));
				}
			}

			@Override
			public void formatValue(final FEMFormatter target, final FEMValue value) throws IllegalArgumentException {
				value.toScript(target);
			}

			@Override
			public void formatFunction(final FEMFormatter target, final FEMFunction function) throws IllegalArgumentException {
				function.toScript(target);
			}

			@Override
			public String toString() {
				return "EMPTY";
			}

		};

		/** Diese Methode formatiert das gegebene Objekt in einen Quelltext und fügt diesen an den gegebenen {@link FEMFormatter} an.
		 * 
		 * @param target {@link FEMFormatter}.
		 * @param data Objekt.
		 * @throws IllegalArgumentException Wenn {@code data} nicht formatiert werden kann. */
		public void formatData(FEMFormatter target, Object data) throws IllegalArgumentException;

		/** Diese Methode formatiert den gegebenen Wert in einen Quelltext und fügt diesen an den gegebenen {@link FEMFormatter} an.
		 * 
		 * @param target {@link FEMFormatter}.
		 * @param value Wert.
		 * @throws IllegalArgumentException Wenn {@code value} nicht formatiert werden kann. */
		public void formatValue(FEMFormatter target, FEMValue value) throws IllegalArgumentException;

		/** Diese Methode formatiert die gegebene Funktion in einen Quelltext und fügt diesen an den gegebenen {@link FEMFormatter} an.
		 * 
		 * @param target {@link FEMFormatter}.
		 * @param function Funktion.
		 * @throws IllegalArgumentException Wenn {@code function} nicht formatiert werden kann. */
		public void formatFunction(FEMFormatter target, FEMFunction function) throws IllegalArgumentException;

	}

	{}

	/** Dieses Feld speichert die bisher gesammelten Zeichenketten und Markierungen. */
	final List<Object> _items_ = new ArrayList<Object>();

	/** Dieses Feld speichert den Puffer für {@link #format()}. */
	final StringBuilder _string_ = new StringBuilder();

	/** Dieses Feld speichert den Stack der Hierarchieebenen. */
	final LinkedList<Boolean> _indents_ = new LinkedList<Boolean>();

	/** Dieses Feld speichert die Zeichenkette zur Einrückung, z.B. {@code "\t"} oder {@code "  "}. */
	String _indent_;

	/** Dieses Feld speichert die Formatierungsmethoden. */
	FEMFormatter.FEMFormatterHelper _helper_ = FEMFormatterHelper.EMPTY;

	{}

	/** Diese Methode beginnt das Formatieren und sollte nur in Verbindung mit {@link #stop()} verwendet werden.
	 * 
	 * @return {@code this}.
	 * @throws IllegalStateException Wenn aktuell formatiert wird. */
	public synchronized final FEMFormatter start() throws IllegalStateException {
		this._check_(true);
		this._indents_.addLast(Boolean.FALSE);
		return this;
	}

	/** Diese Methode beendet das Formatieren und sollte nur in Verbindung mit {@link #start()} verwendet werden.
	 * 
	 * @return {@code this}. */
	public synchronized final FEMFormatter stop() {
		this._items_.clear();
		this._string_.setLength(0);
		this._indents_.clear();
		return this;
	}

	@SuppressWarnings ("javadoc")
	final void _check_(final boolean idling) throws IllegalStateException {
		if (this._indents_.isEmpty() != idling) throw new IllegalStateException();
	}

	/** Diese Methode fügt die gegebenen Markierung an und gibt {@code this} zurück.
	 * 
	 * @param object Markierung.
	 * @return {@code this}. */
	final FEMFormatter _putMark_(final FEMFormatter.Mark object) {
		this._items_.add(object);
		return this;
	}

	/** Diese Methode markiert den Beginn einer neuen Hierarchieebene, erhöht die Tiefe der Einrückung um eins und gibt {@code this} zurück. Wenn über
	 * {@link #putIndent()} die Einrückung für diese Hierarchieebene aktiviert wurde, wird ein Zeilenumbruch gefolgt von der zur Ebene passenden Einrückung
	 * angefügt.
	 * 
	 * @see #putBreakDec()
	 * @see #putBreakSpace()
	 * @see #putIndent()
	 * @return {@code this}.
	 * @throws IllegalStateException Wenn aktuell nicht formatiert wird. */
	public final FEMFormatter putBreakInc() throws IllegalStateException {
		this._check_(false);
		final LinkedList<Boolean> indents = this._indents_;
		indents.addLast(Boolean.FALSE);
		return this._putMark_(Mark.EMPTY)._putMark_(new Mark(indents.size(), false, false, false));
	}

	/** Diese Methode markiert das Ende der aktuellen Hierarchieebene, reduziert die Tiefe der Einrückung um eins und gibt {@code this} zurück. Wenn über
	 * {@link #putIndent()} die Einrückung für eine der tieferen Hierarchieebenen aktiviert wurde, wird ein Zeilenumbruch gefolgt von der zur aktuellen Ebene
	 * passenden Einrückung angefügt.
	 * 
	 * @see #putBreakInc()
	 * @see #putBreakSpace()
	 * @see #putIndent()
	 * @return {@code this}.
	 * @throws IllegalStateException Wenn zuvor keine Hierarchieebene begonnen wurde oder aktuell nicht formatiert wird. */
	public final FEMFormatter putBreakDec() throws IllegalStateException {
		this._check_(false);
		final LinkedList<Boolean> indents = this._indents_;
		final int value = indents.size();
		if (value <= 1) throw new IllegalStateException();
		return this._putMark_(Mark.EMPTY)._putMark_(new Mark(value, true, false, indents.removeLast().booleanValue()));
	}

	/** Diese Methode fügt ein bedingtes Leerzeichen an und gibt {@code this} zurück. Wenn über {@link #putIndent()} die Einrückung für die aktuelle
	 * Hierarchieebene aktiviert wurde, wird statt eines Leerzeichens ein Zeilenumbruch gefolgt von der zur Ebene passenden Einrückung angefügt.
	 * 
	 * @see #putBreakInc()
	 * @see #putBreakDec()
	 * @see #putIndent()
	 * @return {@code this}.
	 * @throws IllegalStateException Wenn aktuell nicht formatiert wird. */
	public final FEMFormatter putBreakSpace() throws IllegalStateException {
		this._check_(false);
		final LinkedList<Boolean> indents = this._indents_;
		return this._putMark_(Mark.EMPTY)._putMark_(new Mark(indents.size(), false, true, indents.getLast().booleanValue()));
	}

	/** Diese Methode markiert die aktuelle sowie alle übergeordneten Hierarchieebenen als einzurücken und gibt {@code this} zurück. Beginn und Ende einer
	 * Hierarchieebene werden über {@link #putBreakInc()} und {@link #putBreakDec()} markiert.
	 * 
	 * @see #putBreakSpace()
	 * @see #putBreakInc()
	 * @see #putBreakDec()
	 * @return {@code this}.
	 * @throws IllegalStateException Wenn aktuell nicht formatiert wird. */
	public final FEMFormatter putIndent() throws IllegalStateException {
		this._check_(false);
		final LinkedList<Boolean> indents = this._indents_;
		if (this._indents_.getLast().booleanValue()) return this;
		final int value = indents.size();
		for (int i = 0; i < value; i++) {
			indents.set(i, Boolean.TRUE);
		}
		final List<Object> items = this._items_;
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
	 * @throws IllegalStateException Wenn aktuell nicht formatiert wird.
	 * @throws IllegalArgumentException Wenn {@code part} nicht formatiert werden kann. */
	public final FEMFormatter put(final Object part) throws IllegalStateException, IllegalArgumentException {
		if (part == null) throw new NullPointerException("part = null");
		this._check_(false);
		this._items_.add(part.toString());
		return this;
	}

	/** Diese Methode fügt die Zeichenkette des gegebenen Objekts an und gibt {@code this} zurück.<br>
	 * 
	 * @see FEMFormatterHelper#formatData(FEMFormatter, Object)
	 * @param data Objekt.
	 * @return {@code this}.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 * @throws IllegalStateException Wenn aktuell nicht formatiert wird.
	 * @throws IllegalArgumentException Wenn {@code data} nicht formatiert werden kann. */
	public final FEMFormatter putData(final Object data) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		if (data == null) throw new NullPointerException("data = null");
		this._check_(false);
		this._helper_.formatData(this, data);
		return this;
	}

	/** Diese Methode fügt die gegebenen Wertliste an und gibt {@code this} zurück.<br>
	 * Wenn die Liste leer ist, wird {@code "[]"} angefügt. Andernfalls werden die Werte in {@code "["} und {@code "]"} eingeschlossen sowie mit {@code ";"}
	 * separiert über {@link #putValue(FEMValue)} angefügt. Nach der öffnenden Klammer {@link #putBreakInc() beginnt} dabei eine neue Hierarchieebene, die vor der
	 * schließenden Klammer {@link #putBreakDec() endet}. Nach jedem Trennzeichen wird ein {@link #putBreakSpace() bedingtes Leerzeichen} eingefügt.<br>
	 * Die aktuelle Hierarchieebene wird als einzurücken {@link #putIndent() markiert}, wenn die Wertliste mehr als ein Element enthält.
	 * 
	 * @see #putValue(FEMValue)
	 * @see #putBreakInc()
	 * @see #putBreakDec()
	 * @see #putBreakSpace()
	 * @see #putIndent()
	 * @param array Wertliste.
	 * @return {@code this}.
	 * @throws NullPointerException Wenn {@code array} {@code null} ist.
	 * @throws IllegalStateException Wenn aktuell nicht formatiert wird.
	 * @throws IllegalArgumentException Wenn {@code array} nicht formatiert werden kann. */
	public final FEMFormatter putArray(final Iterable<? extends FEMValue> array) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		if (array == null) throw new NullPointerException("array = null");
		this._check_(false);
		final Iterator<? extends FEMValue> iter = array.iterator();
		if (iter.hasNext()) {
			FEMValue item = iter.next();
			if (iter.hasNext()) {
				this.putIndent().put("[").putBreakInc().putValue(item);
				do {
					item = iter.next();
					this.put(";").putBreakSpace().putValue(item);
				} while (iter.hasNext());
				this.putBreakDec().put("]");
			} else {
				this.put("[").putBreakInc().putValue(item).putBreakDec().put("]");
			}
		} else {
			this.put("[]");
		}
		return this;
	}

	/** Diese Methode fügt den Quelltext des gegebenen Werts an und gibt {@code this} zurück.<br>
	 * 
	 * @see FEMFormatterHelper#formatValue(FEMFormatter, FEMValue)
	 * @param value Wert.
	 * @return {@code this}.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist.
	 * @throws IllegalStateException Wenn aktuell nicht formatiert wird.
	 * @throws IllegalArgumentException Wenn {@code value} nicht formatiert werden kann. */
	public final FEMFormatter putValue(final FEMValue value) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		if (value == null) throw new NullPointerException("value = null");
		this._check_(false);
		this._helper_.formatValue(this, value);
		return this;
	}

	/** Diese Methode fügt den Quelltext der Liste der gegebenen zugesicherten Parameterwerte eines Stapelrahmens an und gibt {@code this} zurück.<br>
	 * Wenn diese Liste leer ist, wird {@code "()"} angefügt. Andernfalls werden die nummerierten Parameterwerte in {@code "("} und {@code ")"} eingeschlossen,
	 * sowie mit {@code ";"} separiert über {@link #putValue(FEMValue)} angefügt. Vor jedem Parameterwert wird dessen logische Position {@code i} als
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
	 * @throws IllegalStateException Wenn aktuell nicht formatiert wird.
	 * @throws IllegalArgumentException Wenn {@code params} nicht formatiert werden kann. */
	public final FEMFormatter putFrame(final Iterable<? extends FEMValue> params) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		if (params == null) throw new NullPointerException("params = null");
		this._check_(false);
		final Iterator<? extends FEMValue> iter = params.iterator();
		if (iter.hasNext()) {
			FEMValue item = iter.next();
			if (iter.hasNext()) {
				this.putIndent().put("(").putBreakInc().put("$1: ").putValue(item);
				int index = 2;
				do {
					item = iter.next();
					this.put(";").putBreakSpace().put("$").put(new Integer(index)).put(": ").putValue(item);
					index++;
				} while (iter.hasNext());
				this.putBreakDec().put(")");
			} else {
				this.put("(").putBreakInc().put("$1: ").putValue(item).putBreakDec().put(")");
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
	 * @throws IllegalStateException Wenn aktuell nicht formatiert wird.
	 * @throws IllegalArgumentException Wenn {@code params} nicht formatiert werden kann. */
	public final FEMFormatter putParams(final Iterable<? extends FEMFunction> params) throws NullPointerException, IllegalStateException,
		IllegalArgumentException {
		if (params == null) throw new NullPointerException("params = null");
		this._check_(false);
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
	 * @throws IllegalStateException Wenn aktuell nicht formatiert wird.
	 * @throws IllegalArgumentException Wenn {@code function} nicht formatiert werden kann. */
	public final FEMFormatter putHandler(final FEMFunction function) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		if (function == null) throw new NullPointerException("function = null");
		return this.put("{: ").putFunction(function).put("}");
	}

	/** Diese Methode fügt den Quelltext der gegebenen Funktion an und gibt {@code this} zurück.<br>
	 * 
	 * @see FEMFormatterHelper#formatFunction(FEMFormatter, FEMFunction)
	 * @param function Funktion.
	 * @return {@code this}.
	 * @throws NullPointerException Wenn {@code function} {@code null} ist.
	 * @throws IllegalStateException Wenn aktuell nicht formatiert wird.
	 * @throws IllegalArgumentException Wenn {@code function} nicht formatiert werden kann. */
	public final FEMFormatter putFunction(final FEMFunction function) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		if (function == null) throw new NullPointerException("function = null");
		this._check_(false);
		this._helper_.formatFunction(this, function);
		return this;
	}

	/** Diese Methode gibt die Zeichenkette zur Einrückung einer Hierarchieebene zurück. Diese ist {@code null}, wenn nicht eingerückt wird.
	 * 
	 * @return Zeichenkette zur Einrückung oder {@code null}. */
	public final String getIndent() {
		return this._indent_;
	}

	/** Diese Methode gibt die genutzten Formatierungsmethoden zurück.
	 * 
	 * @return Formatierungsmethoden. */
	public final FEMFormatter.FEMFormatterHelper getHelper() {
		return this._helper_;
	}

	/** Diese Methode setzt die Zeichenkette zur Einrückung einer Hierarchieebene und gibt {@code this} zurück.<br>
	 * Wenn diese {@code null} ist, wird nicht eingerückt.
	 * 
	 * @param indent Zeichenkette zur Einrückung (z.B. {@code null}, {@code "\t"} oder {@code "  "}).
	 * @return {@code this}.
	 * @throws IllegalStateException Wenn aktuell formatiert wird. */
	public synchronized final FEMFormatter useIndent(final String indent) throws IllegalStateException {
		this._check_(true);
		this._indent_ = indent;
		return this;
	}

	/** Diese Methode gibt setzt die zu nutzenden Formatierungsmethoden und gibt {@code this} zurück.
	 * 
	 * @param helper Formatierungsmethoden.
	 * @return {@code this}.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist.
	 * @throws IllegalStateException Wenn aktuell formatiert wird. */
	public synchronized final FEMFormatter useHelper(final FEMFormatter.FEMFormatterHelper helper) throws NullPointerException, IllegalStateException {
		if (helper == null) throw new NullPointerException("helper = null");
		this._check_(true);
		this._helper_ = helper;
		return this;
	}

	/** Diese Methode gibt die Verkettung der bisher gesammelten Zeichenketten als Quelltext zurück.
	 * 
	 * @see #put(Object)
	 * @return Quelltext. */
	public final String format() {
		final String indent = this._indent_;
		final List<Object> items = this._items_;
		final StringBuilder string = this._string_;
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
	 * Die Elemente werden über den gegebenen {@link Converter} angefügt und mit {@code ';'} separiert. In der Methode {@link Converter#convert(Object)} sollten
	 * hierfür {@link #putData(Object)}, {@link #putValue(FEMValue)} bzw. {@link #putFunction(FEMFunction)} aufgerufen werden.
	 * 
	 * @see #formatDatas(Iterable)
	 * @see #formatValues(Iterable)
	 * @see #formatFunctions(Iterable)
	 * @param <GItem> Typ der Elemente.
	 * @param items Elemente.
	 * @param formatter {@link Converter} zur Aufruf der spetifischen Formatierungsmethoden je Element.
	 * @return formatierter Quelltext.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist oder enthält.
	 * @throws IllegalStateException Wenn aktuell formatiert wird.
	 * @throws IllegalArgumentException Wenn ein Element nicht formatiert werden kann. */
	final <GItem> String _format_(final Iterable<? extends GItem> items, final Converter<GItem, ?> formatter) throws NullPointerException, IllegalStateException,
		IllegalArgumentException {
		this.start();
		try {
			final Iterator<? extends GItem> iter = items.iterator();
			if (!iter.hasNext()) return "";
			GItem item = iter.next();
			if (iter.hasNext()) {
				this.putIndent();
				formatter.convert(item);
				do {
					item = iter.next();
					this.put(";").putBreakSpace();
					formatter.convert(item);
				} while (iter.hasNext());
			} else {
				formatter.convert(item);
			}
			return this.format();
		} finally {
			this.stop();
		}
	}

	/** Diese Methode formatiert das gegebene Objekt in einen Quelltext und gibt diesen zurück.<br>
	 * Der Rückgabewert entspricht {@code this.formatDatas(Iterables.itemIterable(object))}.
	 * 
	 * @see #formatDatas(Iterable)
	 * @param object Objekt.
	 * @return formatierter Quelltext.
	 * @throws NullPointerException Wenn {@code object} {@code null} ist.
	 * @throws IllegalStateException Wenn aktuell formatiert wird.
	 * @throws IllegalArgumentException Wenn das Object nicht formatiert werden kann. */
	public final String formatData(final Object object) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		return this.formatDatas(Iterables.itemIterable(object));
	}

	/** Diese Methode formatiert die gegebenen Objekt in einen Quelltext und gibt diesen zurück.<br>
	 * Die Objekt werden über {@link #putData(Object)} angefügt und mit {@code ';'} separiert.
	 * 
	 * @see #putData(Object)
	 * @param objects Objekte.
	 * @return formatierter Quelltext.
	 * @throws NullPointerException Wenn {@code objects} {@code null} ist oder enthält.
	 * @throws IllegalStateException Wenn aktuell formatiert wird.
	 * @throws IllegalArgumentException Wenn ein Objekt nicht formatiert werden kann. */
	public final String formatDatas(final Iterable<?> objects) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		return this._format_(objects, new Converter<Object, Object>() {

			@Override
			public Object convert(final Object input) {
				return FEMFormatter.this.putData(input);
			}

		});
	}

	/** Diese Methode formatiert den gegebenen Wert in einen Quelltext und gibt diesen zurück.<br>
	 * Der Rückgabewert entspricht {@code this.formatValue(Iterables.itemIterable(value))}.
	 * 
	 * @see #formatValues(Iterable)
	 * @param value Wert.
	 * @return formatierter Quelltext.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist.
	 * @throws IllegalStateException Wenn aktuell formatiert wird.
	 * @throws IllegalArgumentException Wenn der Wert nicht formatiert werden kann. */
	public final String formatValue(final FEMValue value) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		return this.formatValues(Iterables.itemIterable(value));
	}

	/** Diese Methode formatiert die gegebenen Wert in einen Quelltext und gibt diesen zurück.<br>
	 * Die Werte werden über {@link #putValue(FEMValue)} angefügt und mit {@code ';'} separiert.
	 * 
	 * @see #putValue(FEMValue)
	 * @param values Werte.
	 * @return formatierter Quelltext.
	 * @throws NullPointerException Wenn {@code values} {@code null} ist oder enthält.
	 * @throws IllegalStateException Wenn aktuell formatiert wird.
	 * @throws IllegalArgumentException Wenn ein Wert nicht formatiert werden kann. */
	public final String formatValues(final Iterable<? extends FEMValue> values) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		return this._format_(values, new Converter<FEMValue, Object>() {

			@Override
			public Object convert(final FEMValue input) {
				return FEMFormatter.this.putValue(input);
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
	 * @throws IllegalStateException Wenn aktuell formatiert wird.
	 * @throws IllegalArgumentException Wenn die Funktion nicht formatiert werden kann. */
	public final String formatFunction(final FEMFunction function) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		return this.formatFunctions(Iterables.itemIterable(function));
	}

	/** Diese Methode formatiert die gegebenen Funktionen in einen Quelltext und gibt diesen zurück.<br>
	 * Die Funktionen werden über {@link #putFunction(FEMFunction)} angefügt und mit {@code ';'} separiert.
	 * 
	 * @see #putFunction(FEMFunction)
	 * @param functions Funktionen.
	 * @return formatierter Quelltext.
	 * @throws NullPointerException Wenn {@code functions} {@code null} ist oder enthält.
	 * @throws IllegalStateException Wenn aktuell formatiert wird.
	 * @throws IllegalArgumentException Wenn eine Funktion nicht formatiert werden kann. */
	public final String formatFunctions(final Iterable<? extends FEMFunction> functions) throws NullPointerException, IllegalStateException,
		IllegalArgumentException {
		return this._format_(functions, new Converter<FEMFunction, Object>() {

			@Override
			public Object convert(final FEMFunction input) {
				return FEMFormatter.this.putFunction(input);
			}

		});
	}

	{}

	/** {@inheritDoc} */
	@Override
	public final String toString() {
		return Objects.toInvokeString(this, this._helper_, this._indent_, this._items_);
	}

}