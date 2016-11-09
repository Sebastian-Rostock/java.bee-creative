package bee.creative.util;

/** Diese Schnittstelle definiert einen Adapter zum Lesen und Schreiben einer Eigenschaft einer gegebenen Eingabe.
 *
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GInput> Typ der Eingabe.
 * @param <GValue> Typ des Werts der Eigenschaft. */
public interface Field<GInput, GValue> {

	/** Diese Methode gibt den Wert der Eigenschaft der gegebenen Eingabe zurück.
	 *
	 * @param input Eingabe.
	 * @return Wert der Eigenschaft. */
	public abstract GValue get(GInput input);

	/** Diese Methode setzt den Wert der Eigenschaft der gegebenen Eingabe.
	 *
	 * @param input Eingabe.
	 * @param value Wert der Eigenschaft. */
	public abstract void set(GInput input, GValue value);

}
