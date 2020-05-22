package bee.creative.qs;

/** Diese Schnittstelle definiert ein Objekt, das in einen {@link #owner() Graphspeicher} {@link #state() enthalten} sein sowie aus diesem {@link #pop()
 * entfernt} werden kann.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface QX extends QO {

	/** Diese Methode entfernt dieses Objekt aus dem {@link #owner() Graphspeicher} und gibt nur dann {@code true} zurück, wenn dadurch der Inhalt des
	 * Graphspeichers verändert wurde.
	 *
	 * @see QXSet#popAll()
	 * @return {@code true} bei Änderung des Graphspeicherinhalts bzw. {@code false} sonst. */
	public boolean pop();

	/** Diese Methode gibt nur dann {@code true} zurück, wenn dieses Objekt im {@link #owner() Graphspeicher} gespeichert ist. Andernfalls ist dieses Objekt ein
	 * temporäres.
	 *
	 * @see QXSet#havingState(boolean)
	 * @return {@code true}, wenn {@link #pop()} {@code true} liefern würde bzw. {@code false}, wenn {@link #pop()} {@code false} liefern würde. */
	public boolean state();

}
