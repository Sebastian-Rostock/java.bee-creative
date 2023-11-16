package bee.creative.ini;

import bee.creative.lang.Objects;

/** Diese Klasse implementiert ein abstraktes Element, welches über einen {@link INIReader} aus einer {@code INI}-Datenstruktur gelesene und für einen
 * Abschnitt, eine Eigenschaft oder einen Kommentar stehen kann.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class INIToken {

	/** Dieses Feld speichert die Typkennung eines Abschnitts.
	 *
	 * @see #section()
	 * @see #fromSection(String) */
	public static final int SECTION = 1;

	/** Dieses Feld speichert die Typkennung einer Eigenschaft.
	 *
	 * @see #key()
	 * @see #value()
	 * @see #fromProperty(String, String) */
	public static final int PROPERTY = 2;

	/** Dieses Feld speichert die Typkennung eines Kommentars.
	 *
	 * @see #comment()
	 * @see #fromComment(String) */
	public static final int COMMENT = 4;

	/** Diese Methode gibt einen Abschnitt mit dem gegebenen Namen als {@link INIToken} zurück.
	 *
	 * @see #section()
	 * @param section Name des Abschnitts.
	 * @return Abschnitt.
	 * @throws NullPointerException Wenn {@code section} {@code null} ist. */
	public static INIToken fromSection(String section) throws NullPointerException {
		return new INIToken(section.toString(), null);
	}

	/** Diese Methode gibt einen Kommentar mit dem gegebenen Schlüssel und dem gegebenen Wert als {@link INIToken} zurück.
	 *
	 * @see #key()
	 * @see #value()
	 * @param key Schlüssel der Eigenschaft.
	 * @param value Wert der Eigenschaft.
	 * @return Eigenschaft
	 * @throws NullPointerException Wenn {@code key} bzw. {@code value} {@code null} ist. */
	public static INIToken fromProperty(String key, String value) throws NullPointerException {
		return new INIToken(key.toString(), value.toString());
	}

	/** Diese Methode gibt einen Kommentar mit dem gegebenen Text als {@link INIToken} zurück.
	 *
	 * @see #comment()
	 * @param comment Text des Kommentar.
	 * @return Kommentar.
	 * @throws NullPointerException Wenn {@code comment} {@code null} ist. */
	public static INIToken fromComment(String comment) throws NullPointerException {
		return new INIToken(null, comment.toString());
	}

	/** Diese Methode gibt die Typkennung des Elements zurück.
	 *
	 * @see #SECTION
	 * @see #PROPERTY
	 * @see #COMMENT
	 * @return Typkennung. */
	public int type() {
		if (this.string1 == null) return INIToken.COMMENT;
		if (this.string2 == null) return INIToken.SECTION;
		return INIToken.PROPERTY;
	}

	/** Diese Methode gibt den Schlüssel der Eigenschaft zurück, wenn dieses Element ein {@link #PROPERTY} ist. Andernfalls wird {@code null} geliefert.
	 *
	 * @see #fromProperty(String, String)
	 * @return Schlüssel der Eigenschaft oder {@code null}. */
	public String key() {
		return this.string2 != null ? this.string1 : null;
	}

	/** Diese Methode gibt den Wert der Eigenschaft zurück, wenn dieses Element ein {@link #PROPERTY} ist. Andernfalls wird {@code null} geliefert.
	 *
	 * @see #fromProperty(String, String)
	 * @return Wert der Eigenschaft oder {@code null}. */
	public String value() {
		return this.string1 != null ? this.string2 : null;
	}

	/** Diese Methode gibt den Namen des Abschnitts zurück, wenn dieses Element eine {@link #SECTION} ist. Andernfalls wird {@code null} geliefert.
	 *
	 * @see #fromSection(String)
	 * @return Namen des Abschnitts oder {@code null}. */
	public String section() {
		return this.string2 == null ? this.string1 : null;
	}

	/** Diese Methode gibt den Text des Kommentars zurück, wenn dieses Element ein {@link #COMMENT} ist. Andernfalls wird {@code null} geliefert.
	 *
	 * @see #fromComment(String)
	 * @return Text des Kommentars oder {@code null}. */
	public String comment() {
		return this.string1 == null ? this.string2 : null;
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn dieses Element ein {@link #SECTION Anschnitt} {@link #type() ist}.
	 *
	 * @return {@code true}, wenn {@link #type()} {@code ==} {@link #SECTION}. */
	public boolean isSection() {
		return this.string2 == null;
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn dieses Element eine {@link #PROPERTY Eigenschaft} {@link #type() ist}.
	 *
	 * @return {@code true}, wenn {@link #type()} {@code ==} {@link #PROPERTY}. */
	public boolean isProperty() {
		return (this.string1 != null) && (this.string2 != null);
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn dieses Element ein {@link #COMMENT Kommentar} {@link #type() ist}.
	 *
	 * @return {@code true}, wenn {@link #type()} {@code ==} {@link #COMMENT}. */
	public boolean isComment() {
		return this.string1 == null;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.string1, this.string2);
	}

	@Override
	public boolean equals(Object object) {
		if (object == this) return true;
		if (!(object instanceof INIToken)) return false;
		var that = (INIToken)object;
		return Objects.equals(this.string1, that.string1) && Objects.equals(this.string2, that.string2);
	}

	@Override
	public String toString() {
		if (this.string1 == null) return ";" + Objects.toString(this.string2);
		if (this.string2 == null) return "[" + Objects.toString(this.string1) + "]";
		return Objects.toString(this.string1) + "=" + Objects.toString(this.string2);
	}

	/** Dieses Feld speichert {@link #key()}, {@link #section()} oder {@code null}. */
	final String string1;

	/** Dieses Feld speichert {@link #value()}, {@link #comment()} oder {@code null}. */
	final String string2;

	INIToken(final String string1, final String string2) {
		this.string1 = string1;
		this.string2 = string2;
	}

}