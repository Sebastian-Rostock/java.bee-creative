package bee.creative.util;

import java.util.Comparator;
import java.util.List;

/** Diese Schnittstelle definiert das Paar aus Ein- und Ausgabe eines {@link Getter}.
 * <p>
 * Im nachfolgenden Beispiel wird aus den gegebenen Elementen {@code entries} mit Hilfe des {@link Getter} {@code converter} eine {@link List} aus
 * {@link Conversion} erzeugt. Diese {@link Conversion} werden anschließend bezüglich ihrer {@link Conversion#output() Ausgabe} gemäß dem {@link Comparator}
 * {@code comparator} sortiert. Abschließend werden je ein {@link Iterable} für die {@link Conversion#input() Eingabe} und die {@link Conversion#output()
 * Ausgabe} der {@link Conversion} erzeugt. Wenn die Berechnung der Eigenschaft (Ausgabe), auf der die Sortierung erfolgt, sehr Aufwändig ist, kann diese Form
 * des Pufferns zu einer verringerung der Rechenzeit führen. <pre>{@literal
 * Iterable<I> entries = // ...
 * Converter<I, O> converter = // ...
 * Comparator<O> comparator = // ...
 * List<Conversion<I, O>> conversions = new ArrayList<>();
 * Iterables.appendAll(conversions, Iterables.navigatedIterable(Conversions.staticGetter(converter), entries));
 * Collections.sort(conversions, Comparators.navigatedComparator(Conversions.<O>outputGetter(), comparator));
 * Iterable<I> inputs = Iterables.navigatedIterable(Conversions.<I>inputGetter(), conversions);
 * Iterable<O> outputs = Iterables.navigatedIterable(Conversions.<O>outputGetter(), conversions);}
 * </pre>
 *
 * @see Getter
 * @see Getters
 * @see Conversions
 * @author [cc-by] 2010 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GInput> Typ des Eingabe.
 * @param <GOutput> Typ der Ausgabe. */
public interface Conversion<GInput, GOutput> {

	/** Diese Methode gibt die Eingabe eines {@link Getter} zurück.
	 *
	 * @return Eingabe. */
	public GInput input();

	/** Diese Methode gibt die Ausgabe eines {@link Getter} zurück.
	 *
	 * @return Ausgabe. */
	public GOutput output();

	/** Der Streuwert entspricht dem der {@link #output() Ausgabe}. {@inheritDoc} */
	@Override
	public int hashCode();

	/** Die Äquivalenz dieses und der gegebenen {@link Conversion} basiert auf der Äquivalenz ihrer {@link #output() Ausgaben}. {@inheritDoc} */
	@Override
	public boolean equals(Object object);

}
