package bee.creative.log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import bee.creative.util.Producer;
import bee.creative.util.Strings;

public class LoggerResult implements Producer<String[]> {

	private int indent;

	String[] indents = {};

	final List<String> strings = new ArrayList<>();

	@Override
	public String[] get() {
		return this.strings.toArray(new String[this.strings.size()]);
	}

	public void push(final String text) {
		if (text == null) return;
		// TODO indent
		this.strings.add(text);
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
	 * @param text Baustein, Formattext oder {@code null}
	 * @param args Bausteine, Formatargumente oder {@code null}. */
	public void push(final Object text, final Object[] args) {
		if (args == null) {
			this.push(this.toText(text));
		} else if (text == null) {
			this.push(Strings.join(this.toArgs(args)));
		} else {
			this.push(String.format(this.toText(text), this.toArgs(args)));
		}
	}

	void pushImpl(final LoggerNode node) {
		this.push(node.text, node.args);
		if (node.isOpen()) {
			this.indent++;
		} else if (node.isClose()) {
			this.indent--;
		}
	}

	public String toText(final Object text) {
		if (text == null) return null;
		return text.toString();
	}

	public Object toArg(final Object text) {
		return text;
	}

	public Object[] toArgs(final Object[] args) {
		if (args == null) return null;
		final int length = args.length;
		if (length == 0) return args;
		final Object[] result = new Object[length];
		for (int i = 0; i < length; i++) {
			result[i] = this.toArg(args[i]);
		}
		return result;
	}

	public String toIndent(final int indent) {
		if (indent <= 0) return "";
		if (indent >= 20) return "(" + indent + ")                                    ";
		final char[] result = new char[indent * 2];
		Arrays.fill(result, ' ');
		return new String(result);
	}

}