package bee.creative.util;

import java.util.Comparator;
import java.util.List;
import bee.creative.bind.Getter;

/** Diese Schnittstelle definiert das Paar aus Ein- und Ausgabedaten.
 * <p>
 * Im nachfolgenden Beispiel wird aus den gegebenen Elementen {@code entries} mit Hilfe des {@link Getter} {@code converter} eine {@link List} aus
 * {@link Conversion} {@link Iterables#addAll(java.util.Collection, Iterable) erzeugt}. Diese werden anschließend bezüglich ihrer {@link Conversion#target()
 * Ausgabe} gemäß dem {@link Comparator} {@code comparator} geordnet. Abschließend werden je ein {@link Iterable} für die so geordneten
 * {@link Conversion#source() Eingaben} und die {@link Conversion#target() Ausgaben} erzeugt. <pre>{@literal
 * Iterable<I> entries = // ...
 * Getter<I, O> converter = // ...
 * Comparator<O> comparator = // ...
 * List<Conversion<I, O>> conversions = new ArrayList<>();
 * Iterables.addAll(conversions, Iterables.translatedIterable((i) -> Conversions.compositeConversion(i, converter.get(i)), entries));
 * Collections.sort(conversions, Comparators.translatedComparator(Conversions.targetGetter(), comparator));
 * Iterable<I> inputs = Iterables.translatedIterable(Conversions.sourceGetter(), conversions);
 * Iterable<O> outputs = Iterables.translatedIterable(Conversions.targetGetter(), conversions);}
 * </pre>
 *
 * @see Conversions
 * @author [cc-by] 2010 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GSource> Typ des Eingabe.
 * @param <GTarget> Typ der Ausgabe. */
public interface Conversion<GSource, GTarget> {

	/** Diese Methode gibt die Eingabe zurück.
	 *
	 * @return Eingabe. */
	public GSource source();

	/** Diese Methode gibt die Ausgabe zurück.
	 *
	 * @return Ausgabe. */
	public GTarget target();

	@Override
	public int hashCode();

	@Override
	public boolean equals(Object object);

}
