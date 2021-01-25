package bee.creative.util;

/** Diese Schnittstelle definiert einen Adapter zum Lesen und Schreiben einer Eigenschaft eines gegebenen Datensatzes.
 *
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ des Datensatzes.
 * @param <GValue> Typ des Werts der Eigenschaft. */
public interface Field<GItem, GValue> extends Getter<GItem, GValue>, Setter<GItem, GValue> {
}
