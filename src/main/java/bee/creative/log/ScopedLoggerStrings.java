package bee.creative.log;

import java.util.ArrayList;
import java.util.List;
import bee.creative.bind.Producer;
import bee.creative.lang.Integers;
import bee.creative.lang.Strings;

/** Diese Klasse implementiert den Generator der Textdarstellungen eines {@link ScopedLogger}.
 *
 * @author [cc-by] 2019 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class ScopedLoggerStrings implements Producer<String[]> {

	/** Dieses Feld speichert die erfassten Protokollzeilen. */
	private final List<String> result = new ArrayList<>();

	/** Dieses Feld speichert die Anzahl der Protokollebenen. */
	private int indent;

	/** Dieses Feld puffert die Zeichenketten zur Einrückung für {@link #toIndent(int)}. */
	private final String[] indents = new String[100];

	/** Diese Methode gibt die Textdarstellungen der {@link #push(String) erfassten} Protokollzeilen zurück.
	 *
	 * @return Textdarstellungen. */
	@Override
	public String[] get() {
		return this.result.toArray(new String[this.result.size()]);
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

	/** Diese Methode ist eine Abkürzung für {@link #push(Object, Object[]) this.push(text, null)}. */
	public void push(final Object object) {
		this.push(object, null);
	}

	/** Diese Methode erfasst die gegebene Protokollzeile.
	 * <ul>
	 * <li>Wenn das Objekt {@code text} ein {@link ScopedLogger} ist, werden dessen Protokollzeilen rekursiv erfasst. Die Rekursion ist nicht gegen
	 * Endlosschleifen abgesichert.</li>
	 * <li>Wenn die Objektliste {@code args} {@code null} ist, wird die Protokollzeile über {@link #push(Object) this.push(toString(text))} erfasst.<br>
	 * Solche Protokollzeilen werden durch  {@link ScopedLogger#enterScope(Object)}, {@link ScopedLogger#pushEntry(Object)},
	 *  und {@link ScopedLogger#leaveScope(Object)} bereitgestellt.</li>
	 * <li>Wenn das Objekt {@code text} {@code null} ist, wird die über {@link Strings#join(Object[]) Strings.join(this.toObjects(args))} erzeugte Zeichenkette
	 * über {@link #push(String)} erfast.<br>
	 * Solche Protokollzeilen können durch {@link ScopedLogger#enterScope(String, Object...)}, {@link ScopedLogger#pushEntry(String, Object...)},
	 * {@link ScopedLogger#pushError(Throwable, String, Object...)} und {@link ScopedLogger#leaveScope(String, Object...)} erzeugt werden, wenn als Formattext
	 * {@code null} eingesetzt wird.</li>
	 * <li>Andernfalls wird die über {@link String#format(String, Object...) String.format(this.toString(text), this.toObjects(args)))} erzeugte Zeichenkette über
	 * {@link #push(String)} erfasst.</li>
	 * </ul>
	 *
	 * @see #toString(Object)
	 * @see #toObjects(Object[])
	 * @param text {@link ScopedLogger}, Textbaustein, Formattext oder {@code null}.
	 * @param args Textbausteine, Formatargumente oder {@code null}. */
	public void push(final Object text, final Object[] args) {
		if (text instanceof ScopedLogger) {
			((ScopedLogger)text).toStringImpl(this);
		} else if (args == null) {
			this.push(this.toString(text));
		} else if (text == null) {
			this.push(Strings.join(this.toObjects(args)));
		} else {
			this.push(String.format(this.toString(text), this.toObjects(args)));
		}
	}

	@SuppressWarnings ("javadoc")
	void pushImpl(final ScopedEntryNode node) {
		this.push(node.text, node.args);
		this.indent += node.indent();
	}

	/** Diese Methode gibt die Zeichenkette zum gegebenen Objekt zurück. Sie wird in {@link #push(Object)} und {@link #push(Object, Object[])} zur Ermittlung des
	 * Textbausteins bzw. Formattexts einer Protokollzeile verwendet. Nachfahren sollten diese Methode überschreiben, wenn bspw. für {@link Throwable} oder andere
	 * besondere Objekte eine bessere Zeichenkette ermittelt werden soll, als deren einfache {@link Object#toString() Textdarstellung}. Wenn das gegebene Objekt
	 * {@code null} ist, wird {@code null} geliefert.
	 *
	 * @param object Objekt oder {@code null}.
	 * @return Zeichenkette oder {@code null}. */
	public String toString(final Object object) {
		if (object == null) return null;
		return object.toString();
	}

	/** Diese Methode gibt die Zeichenkette zur Einrückung um die gegebene Anzahl an Protokollebenen zurück. Die ersten {@code 100} Zeichenkette werden zur
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

	/** Diese Methode gibt die Textbausteine bzw. Formatargumente zur gegebenen Objektliste zurück. Sie wird von {@link #push(Object, Object[])} zur Anpassung der
	 * Textbausteine bzw. Formatargumente einer Protokollzeile verwendet. Dies kann notwendig werden, wenn für besondere Objekte ein bessere Textbaustein
	 * ermittelt werden soll, als die seiner {@link Object#toString() Textdarstellung}. Die gegebene Objektliste wird dazu elementweise über
	 * {@link #customObject(Object)} umgewandelt.
	 *
	 * @param objects Objektliste.
	 * @return Textbausteine bzw. Formatargumente.
	 * @throws NullPointerException Wenn {@code args} {@code null} ist. */
	public Object[] toObjects(final Object[] objects) throws NullPointerException {
		final int length = objects.length;
		if (length == 0) return objects;
		final Object[] result = new Object[length];
		for (int i = 0; i < length; i++) {
			result[i] = this.customObject(objects[i]);
		}
		return result;
	}

	/** Diese Methode liefert die Zeichenkette zur Einrückung um die gegebene Anzahl der Protokollebenen.
	 *
	 * @param count Anzahl der Protokollebenen.
	 * @return Zeichenkette zur Einrückung. */
	protected String customIndent(final int count) {
		final String space = "                                        "; // 40 x Space
		if (count <= 20) return space.substring(0, count << 1);
		return "(" + count + ")" + space.substring(0, 40 - Integers.stringSize(count));
	}

	/** Diese Methode ird in {@link #toObjects(Object[])} zur Anpassung von Textbausteinen bzw. Formatargumenten eingesetzt.
	 *
	 * @param object Objekt.
	 * @return Textbaustein bzw. Formatargument. */
	protected Object customObject(final Object object) {
		return object;
	}

}