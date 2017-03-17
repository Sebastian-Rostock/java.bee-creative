package bee.creative.iam;

/** Diese Klasse implementiert die {@link RuntimeException}, die bei Dekodierungsfehlern ausgelöst wird.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class IAMException extends RuntimeException {

	@SuppressWarnings ("javadoc")
	private static final long serialVersionUID = -4587723938056298739L;

	/** Dieses Feld identifiziert die Ausnahme bei der Erkennugn einer ungültigen Anzahl oder eines ungültigen Werts. */
	public static final int INVALID_VALUE = 1;

	/** Dieses Feld identifiziert die Ausnahme bei der Erkennugn ungültiger Startpositionen. */
	public static final int INVALID_OFFSET = 2;

	/** Dieses Feld identifiziert die Ausnahme bei der Erkennugn eines ungenügend großen Speicherbereichs. */
	public static final int INVALID_LENGTH = 4;

	/** Dieses Feld identifiziert die Ausnahme bei der Erkennugn einer unbekannten Datentypkennung oder Datenstruktur. */
	public static final int INVALID_HEADER = 8;

	{}

	/** Dieses Feld speichert die Kennungen der Fehlerursache. */
	final int code;

	/** Dieser Konstrukteur initialisiert die Kennungen der Fehlerursachen.
	 *
	 * @see #INVALID_VALUE
	 * @see #INVALID_OFFSET
	 * @see #INVALID_LENGTH
	 * @see #INVALID_HEADER
	 * @param code Kennungen der Fehlerursachen ({@code |}-verknüpft). */
	public IAMException(final int code) {
		this.code = code;
	}

	{}

	/** Diese Methode gibt die Kennungen der Fehlerursachen zurück.
	 *
	 * @return Kennungen der Fehlerursachen. */
	public final int code() {
		return this.code;
	}

}
