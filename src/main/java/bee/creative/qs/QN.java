package bee.creative.qs;

import bee.creative.bind.Property;

/** Diese Schnittstelle definiert einen Hyperknoten vierter Ordnung (Quad-Node). Er kann als {@link QE#context() Kontext}, {@link QE#predicate Prädikat},
 * {@link QE#subject() Subjekt} oder {@link QE#object() Objekt} einer {@link QE Hyperkante} eingesetzt werden und über einen {@link #get() Textwert} als extere
 * Kennung verfügen. {@link #hashCode() Streuwert} und {@link #equals(Object) Äquivalenz} basieren auf {@link #id() Knotenkennung} und {@link #owner()
 * Graphspeicher}.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface QN extends QX, Property<String> {

	/** Diese Methode gibt die Kennugn des Hyperknoten zurück.
	 *
	 * @return Knotenkennung. */
	public long id();

	/** Diese Methode gibt den Textwert des Hyperknoten zurück. Wenn der Hyperknoten keinen Textwert besitzt, wird die leere Zeichenkette geliefert.
	 *
	 * @return Textwert. */
	@Override
	public String get();

	/** Diese Methode setzt den Textwert des Hyperknoten.
	 *
	 * @see QS#newValue(QN, String)
	 * @see QVSet#putAll()
	 * @param value Textwert. */
	@Override
	public void set(final String value) throws NullPointerException, IllegalArgumentException;

}
