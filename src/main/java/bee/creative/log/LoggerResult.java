package bee.creative.log;

import java.util.ArrayList;
import java.util.List;
import bee.creative.util.Producer;
import bee.creative.util.Strings;

public class LoggerResult implements Producer<String[]> {

	/** Dieses Feld speichert die erfassten Protokolleinträge. */
	private final List<String> result = new ArrayList<>();

	/** Dieses Feld speichert die Anzahl der Protokollebenen. */
	private int indent;

	/** Dieses Feld puffert die Zeichenketten zur Einrückung für {@link #toIndent(int)}. */
	private final String[] indents = new String[1000];

	@Override
	public String[] get() {
		return this.result.toArray(new String[this.result.size()]);
	}

	/** Diese Methode erfasst die gegebene Zeichenkette mit der aktuellen Einrückung als Protokolleintrag nur dann, wenn diese Zeichenkette nicht {@code null} und
	 * nicht leer ist.
	 *
	 * @param text Zeichenkette oder {@code null}. */
	public void push(final String text) {
		if ((text == null) || text.isEmpty()) return;
		final String indent = this.toIndent(this.indent);
		this.result.add(indent.concat(text));
	}

	/** Diese Methode ist eine Abkürzung für {@link #push(String) this.push(this.toText(text))}. */
	public void push(final Object text) {
		this.push(this.toText(text));
	}

	/** Diese Methode überführt die gegebenen Parameter in eine Zeichenkette und delegiert diese an {@link #push(String)}.
	 * <ul>
	 * <li>Wenn die Objektliste {@code args} {@code null} ist, wird die Zeichenkette über {@link #toText(Object) this.toText(text)} ermittelt. Solche Zeilen
	 * werden durch {@link Logger#openScope()}, {@link Logger#openScope(Object)}, {@link Logger#pushEntry(Object)}, {@link Logger#pushEntry(Throwable, Object)},
	 * {@link Logger#closeScope()} und {@link Logger#closeScope(Object)} bereitgestellt.</li>
	 * <li>Wenn das Objekt {@code text} {@code null} ist, wird die Zeichenkette über {@link Strings#join(Object[]) Strings.join(this.toArgs(args))} berechnet.
	 * Solche Zeilen können durch {@link Logger#openScope(String, Object...)}, {@link Logger#pushEntry(String, Object...)},
	 * {@link Logger#pushEntry(Throwable, String, Object...)} und {@link Logger#closeScope(String, Object...)} erzeugt werden, wenn als Formattext {@code null}
	 * eingesetzt wird.</li>
	 * <li>Andernfalls wird die Zeichenkette über {@link String#format(String, Object...) String.format(this.toText(text), this.toArgs(args)))} erzeugt.</li>
	 * </ul>
	 *
	 * @see #toText(Object)
	 * @see #toArgs(Object[])
	 * @param text Textbaustein, Formattext oder {@code null}.
	 * @param args Textbausteine, Formatargumente oder {@code null}. */
	public void push(final Object text, final Object[] args) {
		if (args == null) {
			this.push(this.toText(text));
		} else if (text == null) {
			this.push(Strings.join(this.toArgs(args)));
		} else {
			this.push(String.format(this.toText(text), this.toArgs(args)));
		}
	}

	@SuppressWarnings ("javadoc")
	void pushImpl(final LoggerNode node) {
		this.push(node.text, node.args);
		if (node.isOpen()) {
			this.indent++;
		} else if (node.isClose()) {
			this.indent--;
		}
	}

	/** Diese Methode gibt die Zeichenkette zum gegebenen Objekt zurück. Sie wird in {@link #push(Object)} und {@link #push(Object, Object[])} zur Ermittlung des
	 * Textbausteins bzw. Formattexts einer Protokollzeile verwendet. Nachfahren sollten diese überschreiben, wenn bspw. für {@link Throwable} eine bessere
	 * Zeichenkette ermittelt werden soll, als deren {@link Object#toString() Textdarstellung}.
	 *
	 * @param text Objekt oder {@code null}.
	 * @return Zeichenkette oder {@code null}. */
	public String toText(final Object text) {
		if (text == null) return null;
		return text.toString();
	}

	/** Diese Methode TODO
	 * <p>
	 * Nachfahren sollten diese überschreiben, wenn Textbausteine bzw. Formatargumente eine bessere Zeichenkette ermittelt werden soll, als deren
	 * {@link Object#toString() Textdarstellung}.
	 *
	 * @param text Objekt.
	 * @return Textbaustein bzw. Formatargument. */
	public Object toArg(final Object text) {
		return text;
	}

	/** Diese Methode wandelt die gegebene Objektliste elementweise über {@link #toArg(Object)} in eine neue Objektliste um und gibt diese zurück. Sie wird von
	 * {@link #push(Object, Object[])} zur anpassung der Textbausteine bzw. Formatargumente verwendet.
	 *
	 * @param args Objektliste.
	 * @return Textbausteine bzw. Formatargumente.
	 * @throws NullPointerException Wenn {@code args} {@code null} ist. */
	public Object[] toArgs(final Object[] args) throws NullPointerException {
		final int length = args.length;
		if (length == 0) return args;
		final Object[] result = new Object[length];
		for (int i = 0; i < length; i++) {
			result[i] = this.toArg(args[i]);
		}
		return result;
	}

	/** Diese Methode gibt die Zeichenkette zur Einrückung um die gegebene Anzahl der Protokollebenen zurück. Die ersten 1000 werden zur schnellen
	 * Wiederverwendung gepuffert. Nachfahren können die Berechnung der Zeichenkette in {@link #customIndent(int)} anpassen. Eine negative Anzahl wird wie 0
	 * behandelt.
	 *
	 * @param indent Anzahl der Protokollebenen.
	 * @return Zeichenkette zur Einrückung. */
	public String toIndent(final int indent) {
		if (indent < 0) return this.toIndent(0);
		final String[] indents = this.indents;
		if (indent >= indents.length) return this.customIndent(indent);
		String result = indents[indent];
		if (result != null) return result;
		result = this.customIndent(indent);
		indents[indent] = result;
		return result;
	}

	/** Diese Methode liefert die Zeichenkette zur Einrückung um die gegebene Anzahl der Protokollebenen.
	 *
	 * @param indent Anzahl der Protokollebenen.
	 * @return Zeichenkette zur Einrückung. */
	protected String customIndent(final int indent) {
		final String space = ")                                        "; // 40 x Space
		if (indent <= 20) return space.substring(1, indent + indent + 1);
		return "(" + indent + space.substring(0, 40);
	}

}