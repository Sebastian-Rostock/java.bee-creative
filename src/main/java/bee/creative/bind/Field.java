package bee.creative.bind;

/** Diese Schnittstelle definiert einen Adapter zum Lesen und Schreiben einer Eigenschaft einer gegebenen Eingabe.
 *
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ der Eingabe.
 * @param <GValue> Typ des Werts der Eigenschaft. */
public interface Field<GItem, GValue> extends Getter<GItem, GValue>, Setter<GItem, GValue> {
}
