package bee.creative.util;

/** Diese Schnittstelle definiert einen Adapter zum Lesen und Schreiben einer Eigenschaft einer gegebenen Eingabe.
 *
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GInput> Typ der Eingabe.
 * @param <GValue> Typ des Werts der Eigenschaft. */
public interface Field<GInput, GValue> extends Getter<GInput, GValue>, Setter<GInput, GValue> {
}
