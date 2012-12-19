package bee.creative.function;

/**
 * Diese Schnittstelle definiert den Datentyp eines {@link Value}{@code s}, analog zur {@link Class} eines {@link Object}{@code s}. Ein {@link Type} besitzt Methoden zum Lesen und Konvertieren des {@link Value#data() Datensatzes} eines {@link Value}{@code s} sowie zur Prüfung der Kompatibilität zu anderen Datentypen.
 * 
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GData> Typ des Datensatzes.
 */
public interface Type<GData> {

	/**
	 * Diese Methode gibt den Identifikator dieses {@link Type}{@code s} zurück, dessen Zahlenwert über eine statische Konstante definiert werden sollte, um Fallunterscheidungen mit einem {@code switch}-Statement zu ermöglichen.
	 * 
	 * @return Identifikator dieses {@link Type}{@code s}.
	 */
	public int id();

	/**
	 * Diese Methode gibt nur dann {@code true} zurück, wenn ein {@code cast} in den gegebenen {@link Type} zulässig ist. Dies kann der Fall sein, wenn der generische Datentyp des gegebenen {@link Type}{@code s} gleich dem oder ein Vorfahre des generischen Datentyps dieses {@link Type}{@code s} ist. Wenn der gegebene {@link Type} {@code null} ist, wird {@code false} zurück gegeben
	 * 
	 * @see Class#isAssignableFrom(Class)
	 * @param type {@link Type}.
	 * @return {@code true}, wenn ein {@code cast} in den gegebenen {@link Type} zulässig ist.
	 */
	public boolean is(Type<?> type);

	/**
	 * Diese Methode gibt den in den generischen Datentyp dieses {@link Type}{@code s} konvertierten Datensatz des gegebenen {@link Value}{@code s} zurück.
	 * 
	 * @see Type#id()
	 * @see Value#type()
	 * @see Value#data()
	 * @param value {@link Value}.
	 * @return konvertierter Datensatz.
	 * @throws NullPointerException Wenn der gegebene {@link Value} {@code null} ist.
	 * @throws ClassCastException Wenn bei der Konvertierung ein znzulässiger {@code cast} vorkommt.
	 * @throws IllegalArgumentException Wenn der Datensatz des gegebenen {@link Value}{@code s} nicht in den generische Datentyp dieses {@link Type}{@code s} konvertiert werden kann.
	 */
	public GData dataOf(Value value) throws NullPointerException, ClassCastException, IllegalArgumentException;

	/**
	 * Diese Methode gibt den in den generischen Datentyp dieses {@link Type}{@code s} konvertierten Datensatz des gegebenen {@link Value}{@code s} als neuen {@link Value} zurück.
	 * 
	 * @see Type#id()
	 * @see Value#type()
	 * @see Value#data()
	 * @param value {@link Value}.
	 * @return {@link Value} mit konvertiertem Datensatz.
	 * @throws NullPointerException Wenn der gegebene {@link Value} {@code null} ist.
	 * @throws ClassCastException Wenn bei der Konvertierung ein znzulässiger {@code cast} vorkommt.
	 * @throws IllegalArgumentException Wenn der Datensatz des gegebenen {@link Value}{@code s} nicht in den generische Datentyp dieses {@link Type}{@code s} konvertiert werden kann.
	 */
	public Value valueOf(Value value) throws NullPointerException, ClassCastException, IllegalArgumentException;

}
