package bee.creative.log;

import java.util.ArrayList;
import java.util.List;
import bee.creative.bind.Getter;
import bee.creative.bind.Producer;
import bee.creative.lang.Integers;
import bee.creative.lang.Strings;

/** Diese Klasse implementiert den Generator der {@link LOGBuilder#toStrings() Textdarstellungen} eines {@link LOGBuilder}.
 *
 * @author [cc-by] 2019 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class LOGPrinter implements Producer<String[]>, Getter<Iterable<? extends LOGEntry>, String[]> {

	/** Dieses Feld speichert die erfassten Protokollzeilen. */
	protected final List<String> result = new ArrayList<>();

	/** Dieses Feld speichert die Anzahl der Protokollebenen. */
	protected int indent;

	/** Dieses Feld puffert die Zeichenketten zur Einrückung für {@link #toIndent(int)}. */
	protected final String[] indents = new String[100];

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
	public String[] get(final Iterable<? extends LOGEntry> entries) {
		this.printAll(entries);
		return this.get();
	}

	public void print(final LOGEntry node) {
		this.customPrint(node.text, node.args);
		this.indent += node.indent();
	}

	public void printAll(final Iterable<? extends LOGEntry> entries) {
		for (LOGEntry entry: entries) {
			print(entry);
		}
	}

	/** Diese Methode erfasst die gegebene Zeichenkette mit der aktuellen Einrückung als Protokollzeile nur dann, wenn sie nicht {@code null} ist. Die Einrückung
	 * wird dazu über {@link #toIndent(int)} ermittelt.
	 *
	 * @param text Zeichenkette oder {@code null}. */
	public void push(final String text) {
		if (text == null) return;
		final String indent = this.toIndent(this.indent);
		this.result.add(indent.concat(text));
	}

	/** Diese Methode ist eine Abkürzung für {@link #customPrint(Object, Object[]) this.push(text, null)}. */
	public void push(final Object object) {
		this.customPrint(object, null);
	}

	/** Diese Methode erfasst die gegebene Protokollzeile.
	 * <ul>
	 * <li>Wenn das Objekt {@code text} ein {@link LOGBuilder} ist, werden dessen Protokollzeilen rekursiv erfasst. Die Rekursion ist nicht gegen Endlosschleifen
	 * abgesichert.</li>
	 * <li>Wenn die Objektliste {@code args} {@code null} ist, wird die Protokollzeile über {@link #push(Object) this.push(toString(text))} erfasst.<br>
	 * Solche Protokollzeilen werden durch {@link LOGBuilder#enterScope(Object)}, {@link LOGBuilder#pushEntry(Object)}, und {@link LOGBuilder#leaveScope(Object)}
	 * bereitgestellt.</li>
	 * <li>Wenn das Objekt {@code text} {@code null} ist, wird die über {@link Strings#join(Object[]) Strings.join(this.toObjects(args))} erzeugte Zeichenkette
	 * über {@link #push(String)} erfast.<br>
	 * Solche Protokollzeilen können durch {@link LOGBuilder#enterScope(String, Object...)}, {@link LOGBuilder#pushEntry(String, Object...)},
	 * {@link LOGBuilder#pushError(Throwable, String, Object...)} und {@link LOGBuilder#leaveScope(String, Object...)} erzeugt werden, wenn als Formattext
	 * {@code null} eingesetzt wird.</li>
	 * <li>Andernfalls wird die über {@link String#format(String, Object...) String.format(this.toString(text), this.toObjects(args)))} erzeugte Zeichenkette über
	 * {@link #push(String)} erfasst.</li>
	 * </ul>
	 *
	 * @see #customString(Object)
	 * @see #customObjects(Object[])
	 * @param text {@link LOGBuilder}, Textbaustein, Formattext oder {@code null}.
	 * @param args Textbausteine, Formatargumente oder {@code null}. */
	public void customPrint(final Object text, final Object[] args) {
		if (text instanceof LOGBuilder) {
			this.printAll((LOGBuilder)text);
		} else if (args == null) {
			this.push(this.customString(text));
		} else if (text == null) {
			this.push(Strings.join(this.customObjects(args)));
		} else {
			this.push(String.format(this.customString(text), this.customObjects(args)));
		}
	}

	/** Diese Methode gibt die Zeichenkette zur Einrückung um die gegebene Anzahl an Protokollebenen zurück. Die ersten {@code 100} Zeichenketten werden zur
	 * schnellen Wiederverwendung gepuffert. Eine negative Anzahl wird wie {@code 0} behandelt. Nachfahren können die Berechnung der Zeichenkette in
	 * {@link #customIndent(int)} anpassen.
	 *
	 * @param count Anzahl der Protokollebenen.
	 * @return Zeichenkette zur Einrückung. */
	public String toIndent(final int count) {
		if (count < 0) return this.toIndent(0);
		final String[] indents = this.indents;
		if (count >= indents.length) return this.customIndent(count);
		String result = indents[count];
		if (result != null) return result;
		indents[count] = (result = this.customIndent(count));
		return result;
	}

	/** Diese Methode liefert die Zeichenkette zur Einrückung um die gegebene Anzahl an Protokollebenen.
	 *
	 * @param count Anzahl der Protokollebenen.
	 * @return Zeichenkette zur Einrückung. */
	protected String customIndent(final int count) {
		final String space = "                                        "; // 40 x Space
		if (count <= 20) return space.substring(0, count << 1);
		return "(" + count + ")" + space.substring(0, 40 - Integers.stringSize(count));
	}

	/** Diese Methode gibt die Zeichenkette zum gegebenen Objekt zurück. Sie wird in {@link #push(Object)} und {@link #customPrint(Object, Object[])} zur
	 * Ermittlung des Textbausteins bzw. Formattexts einer Protokollzeile verwendet. Nachfahren sollten diese Methode überschreiben, wenn bspw. für
	 * {@link Throwable} oder andere besondere Objekte eine bessere Zeichenkette ermittelt werden soll, als deren {@link Object#toString() Textdarstellung}. Wenn
	 * das gegebene Objekt {@code null} ist, wird {@code null} geliefert.
	 *
	 * @param object Objekt oder {@code null}.
	 * @return Zeichenkette oder {@code null}. */
	public String customString(final Object object) {
		if (object == null) return null;
		return object.toString();
	}

	/** Diese Methode gibt die Textbausteine bzw. Formatargumente zur gegebenen Objektliste zurück. Sie wird von {@link #customPrint(Object, Object[])} zur
	 * Anpassung der Textbausteine bzw. Formatargumente einer Protokollzeile verwendet. Dies kann notwendig werden, wenn für besondere Objekte ein bessere
	 * Textbaustein verwendet werden soll, als seine {@link Object#toString() Textdarstellung}. Die gegebene Objektliste wird dazu elementweise über
	 * {@link #customObject(Object)} umgewandelt.
	 *
	 * @param objects Objektliste.
	 * @return Textbausteine bzw. Formatargumente.
	 * @throws NullPointerException Wenn {@code args} {@code null} ist. */
	protected Object[] customObjects(final Object[] objects) throws NullPointerException {
		final int length = objects.length;
		if (length == 0) return objects;
		final Object[] result = new Object[length];
		for (int i = 0; i < length; i++) {
			result[i] = this.customObject(objects[i]);
		}
		return result;
	}

	/** Diese Methode wird in {@link #customObjects(Object[])} zur Anpassung von Textbausteinen bzw. Formatargumenten eingesetzt.
	 *
	 * @param object Objekt.
	 * @return Textbaustein bzw. Formatargument. */
	protected Object customObject(final Object object) {
		return object;
	}

}