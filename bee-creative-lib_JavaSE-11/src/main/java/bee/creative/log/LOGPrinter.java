package bee.creative.log;

import java.util.ArrayList;
import java.util.List;
import bee.creative.lang.Integers;
import bee.creative.lang.Strings;
import bee.creative.util.Getter;
import bee.creative.util.Producer;

/** Diese Klasse implementiert den Generator der {@link LOGBuilder#toStrings() Textdarstellungen} eines {@link LOGBuilder}.
 *
 * @see #get()
 * @see #get(Iterable)
 * @author [cc-by] 2019 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class LOGPrinter implements Producer<String[]>, Getter<Iterable<? extends LOGEntry>, String[]> {

	/** Diese Methode gibt die Textdarstellungen der {@link #push(String) erfassten} Protokollzeilen zurück.
	 *
	 * @return Textdarstellungen. */
	@Override
	public String[] get() {
		return this.result.toArray(new String[this.result.size()]);
	}

	/** Diese Methode {@link #printAll(Iterable) erfasst} die gegebenen Protokollzeilen und gibt deren {@link #get() Textdarstellungen } zurück.
	 *
	 * @param entries Protokollzeilen.
	 * @return Textdarstellungen. */
	@Override
	public String[] get(Iterable<? extends LOGEntry> entries) {
		this.printAll(entries);
		return this.get();
	}

	/** Diese Methode erfasst die gegebene Zeichenkette mit der aktuellen Einrückung als Protokollzeile nur dann, wenn sie nicht {@code null} ist. Die Einrückung
	 * wird dazu über {@link #customIndent(int)} ermittelt.
	 *
	 * @param text Zeichenkette oder {@code null}. */
	public void push(String text) {
		if (text == null) return;
		this.result.add(indent(text));
	}
	
	// TODO push zum anfügen mit einrückung vs. print zur erzeugung des textes ohne einrückung
	// wenn entry ein logger ist, dann seine entries iterieren, per print und seinem printer formatieren, dann push mit indent seiner entries
	
	public void push(String text, int indent) {
		
		this.indent += indent;
	}

	private String indent(String text) {
		return this.customIndent(this.indent).concat(text);
	}

	/** Diese Methode {@link #customPrint(Object, Object[]) erfasst} die gegebene Protokollzeile.
	 *
	 * @param node Protokollzeile. */
	public void print(LOGEntry node) {
		this.customPrint(node.text, node.args);
		this.indent += node.indent();
	}

	/** Diese Methode {@link #print(LOGEntry) erfasst} die gegebenen Protokollzeilen.
	 *
	 * @param entries Protokollzeilen. */
	public void printAll(Iterable<? extends LOGEntry> entries) {
		for (var entry: entries) {
			this.print(entry);
		}
	}

	/** Dieses Feld speichert die erfassten Protokollzeilen. */
	protected final List<String> result = new ArrayList<>();

	/** Dieses Feld speichert die Anzahl der Protokollebenen. */
	protected int indent;

	/** Diese Methode erfasst die gegebene Protokollzeile.
	 * <ul>
	 * <li>Wenn das Objekt {@code text} ein {@link LOGBuilder} ist, werden dessen Protokollzeilen rekursiv {@link #printAll(Iterable) erfasst}. Die Rekursion ist
	 * nicht gegen Endlosschleifen abgesichert.</li>
	 * <li>Wenn die Objektliste {@code args} {@code null} ist, wird die Protokollzeile über {@link #customText(Object) this.print(this.customText(text))}
	 * {@link #push(String) erfasst}. Solche Protokollzeilen werden durch {@link LOGBuilder#enterScope(Object)}, {@link LOGBuilder#pushEntry(Object)}, und
	 * {@link LOGBuilder#leaveScope(Object)} bereitgestellt.</li>
	 * <li>Wenn das Objekt {@code text} {@code null} ist, wird die über {@link #customArgs(Object[]) Strings.join(this.customArgs(args))} erzeugte Zeichenkette
	 * {@link #push(String) erfast}. Solche Protokollzeilen können durch {@link LOGBuilder#enterScope(String, Object...)},
	 * {@link LOGBuilder#pushEntry(String, Object...)}, {@link LOGBuilder#pushError(Throwable, String, Object...)} und
	 * {@link LOGBuilder#leaveScope(String, Object...)} erzeugt werden, wenn als Formattext {@code null} eingesetzt wird.</li>
	 * <li>Andernfalls wird die über {@code String.format(this.customText(text), this.customArgs(args)))} erzeugte Zeichenkette {@link #push(String)
	 * erfasst}.</li>
	 * </ul>
	 *
	 * @param text {@link LOGBuilder}, Textbaustein, Formattext oder {@code null}.
	 * @param args Textbausteine, Formatargumente oder {@code null}. */
	protected void customPrint(Object text, Object[] args) {
		if (text instanceof LOGBuilder) {
			this.printAll((LOGBuilder)text); // TODO
		} else if (args == null) {
			this.push(this.customText(text));
		} else if (text == null) {
			this.push(Strings.join(this.customArgs(args)));
		} else {
			this.push(String.format(this.customText(text), this.customArgs(args)));
		}
	}

	/** Diese Methode gibt die Zeichenkette zum gegebenen Objekt zurück. Sie wird in {@link #customPrint(Object, Object[])} zur Ermittlung des Textbausteins bzw.
	 * Formattexts einer Protokollzeile verwendet. Nachfahren sollten diese Methode überschreiben, wenn bspw. für {@link Throwable} oder andere besondere Objekte
	 * eine bessere Zeichenkette ermittelt werden soll, als deren {@link Object#toString() Textdarstellung}. Wenn das gegebene Objekt {@code null} ist, wird
	 * {@code null} geliefert.
	 *
	 * @param object Objekt oder {@code null}.
	 * @return Zeichenkette oder {@code null}. */
	protected String customText(Object object) {
		return object != null ? object.toString() : null;
	}

	/** Diese Methode wird in {@link #customArgs(Object[])} zur Anpassung von Textbausteinen bzw. Formatargumenten eingesetzt.
	 *
	 * @param object Objekt.
	 * @return Textbaustein bzw. Formatargument. */
	protected Object customArg(Object object) {
		return object;
	}

	/** Diese Methode gibt die Textbausteine bzw. Formatargumente zur gegebenen Objektliste zurück. Sie wird von {@link #customPrint(Object, Object[])} zur
	 * Anpassung der Textbausteine bzw. Formatargumente einer Protokollzeile verwendet. Dies kann notwendig werden, wenn für besondere Objekte ein bessere
	 * Textbaustein verwendet werden soll, als seine {@link Object#toString() Textdarstellung}. Die gegebene Objektliste wird dazu elementweise über
	 * {@link #customArg(Object)} umgewandelt.
	 *
	 * @param objects Objektliste.
	 * @return Textbausteine bzw. Formatargumente.
	 * @throws NullPointerException Wenn {@code args} {@code null} ist. */
	protected Object[] customArgs(Object[] objects) throws NullPointerException {
		var length = objects.length;
		if (length == 0) return objects;
		var result = new Object[length];
		for (var i = 0; i < length; i++) {
			result[i] = this.customArg(objects[i]);
		}
		return result;
	}

	/** Diese Methode liefert die Zeichenkette zur Einrückung um die gegebene Anzahl an Protokollebenen.
	 *
	 * @param count Anzahl der Protokollebenen.
	 * @return Zeichenkette zur Einrückung. */
	protected String customIndent(int count) {
		if (count <= 0) return "";
		var space = "                                        "; // 40 x Space
		if (count <= 20) return space.substring(0, count * 2);
		return "(" + count + ")" + space.substring(0, 40 - Integers.getSize(count));
	}

}