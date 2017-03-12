package bee.creative.util;

/** Diese Schnittstelle definiert einen Adapter zum Schreiben einer Eigenschaft einer gegebenen Eingabe.
 *
 * @author [cc-by] 2017 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GInput> Typ der Eingabe.
 * @param <GValue> Typ des Werts der Eigenschaft. */
public interface Setter<GInput, GValue> {

	/** Diese Methode setzt den Wert der Eigenschaft der gegebenen Eingabe.
	 *
	 * @param input Eingabe.
	 * @param value Wert der Eigenschaft. */
	public abstract void set(GInput input, GValue value);

}