package bee.creative.util;

/** Diese Schnittstelle definiert einen Adapter zum Lesen und Schreiben einer Eigenschaft eines gegebenen Datensatzes.
 *
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <T> Typ des Datensatzes.
 * @param <V> Typ des Werts der Eigenschaft. */
public interface Field<T, V> extends Getter2<T, V>, Setter2<T, V> {
}
