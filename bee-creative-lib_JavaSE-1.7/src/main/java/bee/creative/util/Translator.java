package bee.creative.util;

/** Diese Schnittstelle definiert einen Übersetzer, der Quellobjekte in Zielobjekte und umgekehrt übersetzen kann. Ein {@link Translator} ähnelt damit einem
 * bidirektionalen {@link Getter}.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GSource> Typ der Quellobjekte.
 * @param <GTarget> Typ der Zielobjekte. */
public interface Translator<GSource, GTarget> {

	/** Diese Methode gibt nur dann {@code true} zurück, wenn das gegebene Objekt ein gültiges Zielobjekt ist.
	 *
	 * @param object Objekt.
	 * @return {@code true}, wenn {@code object} ein Zielobjekt ist. */
	public boolean isTarget(Object object);

	/** Diese Methode gibt nur dann {@code true} zurück, wenn das gegebene Objekt ein gültiges Quellobjekt ist.
	 *
	 * @param object Objekt.
	 * @return {@code true}, wenn {@code object} ein Quellobjekt ist. */
	public boolean isSource(Object object);

	/** Diese Methode übersetzt das gegebene Quellobjekt in den Zieldatentyp und gibt das so ermittelte Zielobjekt zurück.
	 *
	 * @param object Quellobjekt.
	 * @return Zielobjekt.
	 * @throws ClassCastException Wenn {@code object} kein {@link #isSource(Object) Quellobjekt} ist.
	 * @throws IllegalArgumentException Wenn {@code object} nicht übersetzt werden kann. */
	public GTarget toTarget(Object object) throws ClassCastException, IllegalArgumentException;

	/** Diese Methode übersetzt das gegebene Zielobjekt in den Quelldatentyp und gibt das so ermittelte Quellobjekt zurück.
	 *
	 * @param object Zielobjekt.
	 * @return Quellobjekt.
	 * @throws ClassCastException Wenn {@code object} kein {@link #isTarget(Object) Zielobjekt} ist.
	 * @throws IllegalArgumentException Wenn {@code object} nicht übersetzt werden kann. */
	public GSource toSource(Object object) throws ClassCastException, IllegalArgumentException;

}