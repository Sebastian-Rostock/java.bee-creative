package bee.creative.util;

/** Diese Schnittstelle definiert einen Adapter zum Lesen einer Eigenschaft einer gegebenen Eingabe. Dieser Adapter kann auch als eine Konvertierungsmethode
 * verstanden werden, die gegebene Objekte vom Typ {@code GInput} in Objekte vom Typ {@code GValue} umwandelt. <br>
 * Bem Lesen bzw. Konvertieren kann es sich bspw. um eine Navigation in einem Objektgraphen oder auch Parsen bzw. Formatieren eines Objektes handel.
 *
 * @author [cc-by] 2017 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GInput> Typ der Eingabe.
 * @param <GValue> Typ des Werts der Eigenschaft. */
public interface Getter<GInput, GValue> {

	/** Diese Methode gibt den Wert der Eigenschaft der gegebenen Eingabe zurück.
	 *
	 * @param input Eingabe.
	 * @return Wert der Eigenschaft. */
	public abstract GValue get(GInput input);

}