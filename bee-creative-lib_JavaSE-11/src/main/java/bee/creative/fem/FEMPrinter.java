package bee.creative.fem;

import java.io.IOException;
import java.util.ArrayList;
import bee.creative.lang.Objects;
import bee.creative.util.Parser.Result;

/** Diese Klasse implementiert ein Objekt zur {@link #push(Object) Erfassung} und {@link #print(Appendable) Ausgabe} der Textdarstellung von {@link FEMFunction
 * Funktionen}.
 *
 * @see FEMDomain#toPrinter(Iterable)
 * @see FEMDomain#toPrinter(Result)
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class FEMPrinter {

	/** Dieser Konstruktor initialisiert einen neuen Formetter, welcher keine {@link FEMPrinter#useIndent(String) Einrückung} nutzt. */
	public FEMPrinter() {
		this.reset();
	}

	/** Diese Methode gibt die Zeichenkette zur Einrückung einer Ebene zurück. Diese ist {@code null}, wenn nicht eingerückt wird.
	 *
	 * @return Zeichenkette zur Einrückung oder {@code null}. */
	public String getIndent() {
		return this.indent;
	}

	/** Diese Methode setzt die Zeichenkette zur Einrückung einer Ebene und gibt {@code this} zurück. Wenn diese {@code null} ist, wird nicht eingerückt. Sie wird
	 * erst bei der {@link #print(Appendable) Textausgabe} wirksam.
	 *
	 * @param indent Zeichenkette zur Einrückung (z.B. {@code null}, {@code "\t"} oder {@code "  "}).
	 * @return {@code this}. */
	public FEMPrinter useIndent(String indent) {
		this.indent = indent;
		return this;
	}

	/** Diese Methode entfernt alle bisher geöffneten Ebenen sowie erfassten Objekte und gibt {@code this} zurück.
	 *
	 * @return {@code this}. */
	public final FEMPrinter reset() {
		this.list = new ArrayList<>();
		this.level = new Level(this, null);
		this.depth = 2;
		return this;
	}

	/** Diese Methode erfasst das gegebene Objekt und gibt {@code this} zurück. Wenn das Objekt {@code null} ist, wird es nicht erfasst. Andernfalls wird seine
	 * {@link Object#toString() Textdarstellung} {@link #print(Appendable) ausgegeben}.
	 *
	 * @param src Objekt oder {@code null}.
	 * @return {@code this}. */
	public final FEMPrinter push(Object src) {
		if (src == null) return this;
		this.list.add(src);
		return this;
	}

	/** Diese Methode markiert den Beginn einer neuen Ebene, erhöht die Tiefe der Einrückung um eins und gibt {@code this} zurück. Bei {@link #pushIndent()
	 * aktivierter} Einrückung wird dieser als ein Zeilenumbruch gefolgt von der zur aktuellen Ebenentiefe passenden Einrückung {@link #print(Appendable)
	 * ausgegeben}. Andernfalls wird der als leere Zeichenkette ausgegeben.
	 *
	 * @see #pushBreakDec()
	 * @see #pushBreakSpc()
	 * @return {@code this}. */
	public final FEMPrinter pushBreakInc() {
		var level = new Level(this, this.level);
		this.depth = Math.max(this.depth, level.depth);
		this.list.add(level.inc);
		this.level = level;
		return this;
	}

	/** Diese Methode markiert das Ende der aktuellen Ebene, reduziert die Tiefe der Einrückung um eins und gibt {@code this} zurück. Bei {@link #pushIndent()
	 * aktivierter} Einrückung wird dieser als ein Zeilenumbruch gefolgt von der zur aktuellen Ebenentiefe passenden Einrückung {@link #print(Appendable)
	 * ausgegeben}. Andernfalls wird der als leere Zeichenkette ausgegeben.
	 *
	 * @see #pushBreakInc()
	 * @see #pushBreakSpc()
	 * @return {@code this}. */
	public final FEMPrinter pushBreakDec() {
		var level = this.level;
		this.list.add(level.dec);
		level = level.parent;
		if (level == null) return this;
		this.level = level;
		return this;
	}

	/** Diese Methode erfasst ein bedingtes Leerzeichen und gibt {@code this} zurück. Bei {@link #pushIndent() aktivierter} Einrückung wird dieser als ein
	 * Zeilenumbruch gefolgt von der zur aktuellen Ebenentiefe passenden Einrückung {@link #print(Appendable) ausgegeben}. Andernfalls wird der als Leerzeichen
	 * ausgegeben.
	 *
	 * @see #pushBreakInc()
	 * @see #pushBreakDec()
	 * @return {@code this}. */
	public final FEMPrinter pushBreakSpc() {
		this.list.add(this.level.spc);
		return this;
	}

	/** Diese Methode markiert alle übergeordneten Ebenen als einzurücken und gibt {@code this} zurück. Beginn und Ende einer Ebene werden über
	 * {@link #pushBreakInc()} und {@link #pushBreakDec()} markiert.
	 *
	 * @see #pushBreakSpc()
	 * @see #pushBreakInc()
	 * @see #pushBreakDec()
	 * @return {@code this}. */
	public final FEMPrinter pushIndent() {
		var level = this.level;
		for (var parent = level.parent; (parent != null) && !parent.enabled; parent = parent.parent) {
			parent.enabled = true;
		}
		return this;
	}

	/** Diese Methode gibt die Verkettung der bisher gesammelten Zeichenketten als Quelltext zurück.
	 *
	 * @see #push(Object)
	 * @return Quelltext. */
	public final String print() throws IllegalArgumentException {
		var res = new StringBuilder();
		try {
			this.print(res);
		} catch (IllegalArgumentException cause) {
			throw cause;
		} catch (Exception cause) {
			throw new IllegalArgumentException(cause);
		}
		return res.toString();
	}

	/** Diese Methode {@link Appendable#append(CharSequence) fügt} die {@link Object#toString() Textdarstellungen} der {@link #push(Object) erfastten} Objekte und
	 * Einrückungen an den gegebenen Puffer an. */
	public final void print(Appendable res) throws IOException {
		var length = this.depth + 1;
		var indent = this.indent;
		var caches = new Indent[length];
		caches[0] = Indent.DISABLED;
		caches[1] = Indent.from(indent);
		Indent parent = null;
		for (var i = 2; i < length; i++) {
			parent = Indent.from(indent, parent);
			caches[i] = parent;
		}
		this.caches = caches;
		try {
			for (var item: this.list) {
				res.append(item.toString());
			}
		} finally {
			this.caches = null;
		}
	}

	@Override
	public String toString() {
		return Objects.toInvokeString(this, this.indent);
	}

	/** Dieses Feld speichert die erfassten Objekte. */
	private ArrayList<Object> list;

	/** Dieses Feld speichert die aktuelle Einrückebene. */
	private Level level;

	/** Dieses Feld speichert die maximale Einrücktiefe. */
	private int depth;

	private String indent;

	private Indent[] caches;

	private static final class Level extends Indent {

		/** Dieses Feld speichert die Tiefe der Ebene, beginnend mit 1. */
		final int depth;

		/** Dieses Feld speichert die übergeordnete Ebene oder {@code null}. */
		final Level parent;

		/** Dieses Feld speichert die Aktivierung der Einrückung dieser Ebene. */
		boolean enabled;

		Level(FEMPrinter owner, Level parent) {
			this.depth = parent != null ? parent.depth + 1 : 1;
			this.parent = parent;
			this.inc = new TokenInc(owner, this);
			this.dec = new TokenDec(owner, this);
			this.spc = new TokenSpc(owner, this);
		}

	}

	private static abstract class Token {

		final FEMPrinter owner;

		final Level level;

		Token(FEMPrinter owner, Level level) {
			this.owner = owner;
			this.level = level;
		}

		final Indent toIdent() {
			return this.owner.caches[this.level.enabled ? this.level.depth : 0];
		}

	}

	private static class TokenInc extends Token {

		@Override
		public String toString() {
			return this.toIdent().inc.toString();
		}

		TokenInc(FEMPrinter owner, Level level) {
			super(owner, level);
		}

	}

	private static class TokenDec extends Token {

		@Override
		public String toString() {
			return this.toIdent().dec.toString();
		}

		TokenDec(FEMPrinter owner, Level level) {
			super(owner, level);
		}

	}

	private static class TokenSpc extends Token {

		@Override
		public String toString() {
			return this.toIdent().spc.toString();
		}

		TokenSpc(FEMPrinter owner, Level level) {
			super(owner, level);
		}

	}

	private static class Indent {

		static final Indent ENABLED = Indent.from("", "", "\n");

		static final Indent DISABLED = Indent.from("", "", " ");

		static Indent from(String indent_or_null) {
			return indent_or_null != null ? Indent.ENABLED : Indent.DISABLED;
		}

		static Indent from(String indent_or_null, Indent parent_or_null) {
			if (indent_or_null == null) return Indent.DISABLED;
			var res = new Indent();
			res.dec = parent_or_null == null ? "\n" : parent_or_null.inc;
			res.inc = res.dec + indent_or_null;
			res.spc = res.inc;
			return res;
		}

		static Indent from(String inc, String dec, String spc) {
			var res = new Indent();
			res.inc = inc;
			res.dec = dec;
			res.spc = spc;
			return res;
		}

		/** Dieses Feld speichert die Einrückung für {@link FEMPrinter#pushBreakInc()}. */
		Object inc;

		/** Dieses Feld speichert die Einrückung für {@link FEMPrinter#pushBreakDec()}. */
		Object dec;

		/** Dieses Feld speichert die Einrückung für {@link FEMPrinter#pushBreakSpc()}. */
		Object spc;

	}

}