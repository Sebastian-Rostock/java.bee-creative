package bee.creative.util;

/** Diese Schnittstelle definiert einen Adapter zum Lesen und Schreiben einer Eigenschaft eines gegebenen Datensatzes.
 *
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <ITEM> Typ des Datensatzes.
 * @param <VALUE> Typ des Werts der Eigenschaft. */
public interface Field<ITEM, VALUE> extends Getter<ITEM, VALUE>, Setter2<ITEM, VALUE> {
}
