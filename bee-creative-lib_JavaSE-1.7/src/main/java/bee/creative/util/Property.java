package bee.creative.util;

/** Diese Schnittstelle definiert einen Adapter zum Lesen und Schreiben des Werts einer Eigenschaft, die durch dieses Objekt repräsentiert wird.
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GValue> Typ des Werts der Eigenschaft. */
public interface Property<GValue> extends Producer<GValue>, Consumer<GValue> {
}
