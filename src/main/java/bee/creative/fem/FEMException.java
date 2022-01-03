package bee.creative.fem;

import java.util.IllegalFormatException;
import bee.creative.lang.Exception2;

/** Diese Klasse implementiert eine Ausnahme, an welche mehrere {@link #getMessages() Nachrichten} {@link #push(Object) angefügt} werden können und welche mit
 * einem {@link #getValue() Wert} sowie einem {@link #getContext() Kontextobjekt} die {@link #getCause() Ursache} eines Ausnahmefalls genauer beschreiben kann.
 * Die von {@link #getMessage()} gelieferte Zeichenkette entspricht den mit dem Trennzeichen {@code '\n'} verketteten {@link #getMessages() Nachrichten}.
 *
 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMException extends Exception2 {

	private static final long serialVersionUID = -2654985371977072939L;

	/** Diese Methode gibt die gegebene Ausnahme als {@link FEMException} zurück. Wenn {@code cause} eine {@link FEMException} ist, wird diese unverändert
	 * geliefert. Andernfalls wird eine neue {@link FEMException} mit der gegebenen Ausnahme als {@link #useCause(Throwable) Ursache} erzeugt und geliefert.
	 *
	 * @param cause Ausnahme.
	 * @return {@link FEMException}. */
	public static FEMException from(final Throwable cause) {
		if (cause instanceof FEMException) return (FEMException)cause;
		return new FEMException().useCause(cause);
	}

	/** Dieses Feld speichert den Wert. */
	FEMValue value;

	/** Dieses Feld speichert den Kontextobjekt. */
	FEMContext context;

	/** Dieser Konstruktor initialisiert eine neue Ausnahme ohne {@link #getValue() Wert}, {@link #getContext() Kontextobjekt}, {@link #getCause() Ursache} und
	 * {@link #getMessages() Nachrichten}. */
	public FEMException() {
	}

	/** Diese Methode gibt den Wert zurück, der die {@link #getCause() Ursache} des Ausnahmefalls genauer beschreibt.
	 *
	 * @return Wert oder {@code null}. */
	public FEMValue getValue() {
		return this.value;
	}

	/** Diese Methode setzt den {@link #getValue() Wert} und gibt {@code this} zurück.
	 *
	 * @see #getValue()
	 * @param value Wert oder {@code null}.
	 * @return {@code this}. */
	public FEMException useValue(final FEMValue value) {
		this.value = value;
		return this;
	}

	/** Diese Methode gibt das Kontextobjekt zurück, das die {@link #getCause() Ursache} des Ausnahmefalls genauer beschreibt bzw. zur Umwandlung des
	 * {@link #getValue() Werts} eingesetzt werden kann.
	 *
	 * @return Kontextobjekt oder {@code null}. */
	public FEMContext getContext() {
		return this.context;
	}

	/** Diese Methode setzt das {@link #getContext() Kontextobjekt} und gibt {@code this} zurück.
	 *
	 * @see #getContext()
	 * @param context Kontextobjekt oder {@code null}.
	 * @return {@code this}. */
	public FEMException useContext(final FEMContext context) {
		this.context = context;
		return this;
	}

	@Override
	public FEMException useCause(final Throwable cause) {
		super.useCause(cause);
		return this;
	}

	@Override
	public FEMException useHandled(final boolean handled) {
		super.useHandled(handled);
		return this;
	}

	@Override
	public FEMException push(Object message) {
		super.push(message);
		return this;
	}

	@Override
	public FEMException push(String format, Object... args) throws NullPointerException, IllegalFormatException {
		super.push(format, args);
		return this;
	}

	@Override
	public FEMException pushAll(Iterable<?> messages) {
		super.pushAll(messages);
		return this;
	}

	@Override
	public FEMException pushAll(Object... messages) {
		super.pushAll(messages);
		return this;
	}

}
