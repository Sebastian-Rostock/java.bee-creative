package bee.creative.util;

import java.util.Comparator;
import java.util.List;

/**
 * Diese Schnittstelle definiert das Paar aus Ein- und Ausgabe eines {@link Converter}s.
 * <p>
 * Im nachfolgenden Beispiel wird aus den gegebenen Elementen {@code entries} mit Hilfe des {@link Converter}s
 * {@code converter} eine {@link List} aus {@link Conversion}s erzeugt. Diese {@link Conversion}s werden anschließend
 * bezüglich ihrer Ausgabe ({@link Conversion#output()}) gemäß dem {@link Comparator} {@code comparator} sortiert.
 * Abschließend werden je ein {@link Iterable} für die Eingabe ({@link Conversion#input()}) und die Ausgabe (
 * {@link Conversion#output()}) der {@link Conversion}s erzeugt. Wenn die Berechnung der Eigenschaft (Ausgabe), auf der
 * die Sortierung erfolgt, sehr Aufwändig ist, kann diese Form des Pufferns zu einer verringerung der Rechenzeit führen.
 * 
 * <pre>
 * Iterable&lt;I&gt; entries = // ...
 * Converter&lt;I, O&gt; converter = // ...
 * Comparator&lt;O&gt; comparator = // ...
 * List&lt;Conversion&lt;I, O&gt;&gt; conversions = new ArrayList&lt;Conversion&lt;I, O&gt;&gt;();
 * Iterables.appendAll(Iterables.convertedIterable(Conversions.staticConversionConverter(converter), entries), conversions);
 * Collections.sort(conversions, Comparators.convertedComparator(Conversions.&lt;O&gt;conversionOutputConverter(), comparator));
 * Iterable&lt;I&gt; inputs = Iterables.convertedIterable(Conversions.&lt;I&gt;conversionInputConverter(), conversions);
 * Iterable&lt;O&gt; outputs = Iterables.convertedIterable(Conversions.&lt;O&gt;conversionOutputConverter(), conversions);
 * </pre>
 * 
 * @see Converter
 * @see Converters
 * @see Conversions
 * @author [cc-by] 2010 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GInput> Typ des Eingabe.
 * @param <GOutput> Typ der Ausgabe.
 */
public interface Conversion<GInput, GOutput> {

	/**
	 * Diese Methode gibt die Eingabe eines {@link Converter}s zurück.
	 * 
	 * @return Eingabe.
	 */
	public GInput input();

	/**
	 * Diese Methode gibt die Ausgabe eines {@link Converter}s zurück.
	 * 
	 * @return Ausgabe.
	 */
	public GOutput output();

	/**
	 * Der Streuwert entspricht dem der Ausgabe. {@inheritDoc}
	 */
	@Override
	public int hashCode();

	/**
	 * Die Äquivalenz dieses und der gegebenen {@link Conversion} basiert auf der Äquivalenz ihrer Ausgaben. {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj);

}
