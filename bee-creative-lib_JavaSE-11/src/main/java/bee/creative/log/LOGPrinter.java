package bee.creative.log;

import bee.creative.lang.Strings;

/** Diese Klasse implementiert den Generator der Textdarstellungen für {@link LOGEntry#toString()} und {@link LOGBuilder#toString()}.
 *
 * @author [cc-by] 2019 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class LOGPrinter {

	/** Diese Methode wird in {@link #toArgs(Object[])} zur Anpassung von Textbausteinen bzw. Formatargumenten eingesetzt.
	 *
	 * @param object Objekt.
	 * @return Textbaustein bzw. Formatargument. */
	public Object toArg(Object object) {
		return object;
	}

	/** Diese Methode liefert die Textbausteine bzw. die Formatargumente zur gegebenen Objektliste. Sie wird von {@link #toString(Object, Object[])} zur Anpassung
	 * der Textbausteine bzw. Formatargumente einer Protokollzeile verwendet. Dies kann notwendig werden, wenn für besondere Objekte bessere Textbausteine
	 * verwendet werden sollen, als deren {@link Object#toString() Textdarstellung}. Die gegebene Objektliste wird dazu elementweise über {@link #toArg(Object)}
	 * umgewandelt.
	 *
	 * @param objects Objektliste.
	 * @return Textbausteine bzw. Formatargumente.
	 * @throws NullPointerException Wenn {@code args} {@code null} ist. */
	public Object[] toArgs(Object[] objects) throws NullPointerException {
		var length = objects.length;
		if (length == 0) return objects;
		var result = new Object[length];
		for (var i = 0; i < length; i++) {
			result[i] = this.toArg(objects[i]);
		}
		return result;
	}

	/** Diese Methode liefert die Zeichenkette zum gegebenen Objekt. Sie wird in {@link #toString(Object, Object[])} zur Ermittlung des Textbausteins bzw.
	 * Formattexts einer Protokollzeile verwendet. Nachfahren sollten diese Methode überschreiben, wenn bspw. für {@link Throwable} oder andere besondere Objekte
	 * eine bessere Zeichenkette ermittelt werden soll, als deren {@link Object#toString() Textdarstellung}. Wenn das gegebene Objekt {@code null} ist, wird
	 * {@code null} geliefert.
	 *
	 * @param object Objekt oder {@code null}.
	 * @return Zeichenkette oder {@code null}. */
	public String toText(Object object) {
		return object != null ? object.toString() : null;
	}

	/** Diese Methode liefert Textdarstellung der gegebenen Protokollzeile mit den gegebenen Merkmalen.
	 * <ul>
	 * <li>Wenn das Objekt {@code text} ein {@link LOGBuilder} ist, werden dessen Protokollzeilen rekursiv geliefert. Die Rekursion ist nicht gegen
	 * Endlosschleifen abgesichert.</li>
	 * <li>Wenn die Objektliste {@code args} {@code null} ist, wird die Textdarstellung über {@link #toText(Object)} erzeugt. Solche Protokollzeilen werden durch
	 * {@link LOGBuilder#enterScope(Object)}, {@link LOGBuilder#pushEntry(Object)}, und {@link LOGBuilder#leaveScope(Object)} bereitgestellt.</li>
	 * <li>Wenn das Objekt {@code text} {@code null} ist, wird die Verkettung der Textdarstellungen der über {@link #toArgs(Object[])} erzeugte Objekte geliefert.
	 * Solche Protokollzeilen können durch {@link LOGBuilder#enterScope(String, Object...)}, {@link LOGBuilder#pushEntry(String, Object...)},
	 * {@link LOGBuilder#pushError(Throwable, String, Object...)} und {@link LOGBuilder#leaveScope(String, Object...)} erzeugt werden, wenn als Formattext
	 * {@code null} eingesetzt wird.</li>
	 * <li>Andernfalls wird die über {@code String.format(this.toText(text), this.toArgs(args)))} erzeugte Zeichenkette geliefert.</li>
	 * </ul>
	 *
	 * @param text {@link LOGBuilder}, Textbaustein, Formattext oder {@code null}.
	 * @param args Textbausteine, Formatargumente oder {@code null}. */
	public String toString(Object text, Object[] args) {
		if (text instanceof LOGBuilder) return text.toString();
		if (args == null) return this.toText(text);
		if (text == null) return Strings.join(this.toArgs(args));
		return String.format(this.toText(text), this.toArgs(args));
	}

}