package bee.creative.ini;

import bee.creative.util.Objects;

/**
 * Diese Klasse implementiert ein abstraktes Element, welches über einen {@link INIReader} aus einer {@code INI}-Datenstruktur gelesene und für einen Abschnitt,
 * eine Eigenschaft oder einen Kommentar stehen kann.
 * 
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class INIToken {

	/**
	 * Dieses Feld speichert die Typkennung eines Abschnitts.
	 * 
	 * @see #section()
	 * @see #sectionToken(String)
	 */
	public static final int SECTION = 1;

	/**
	 * Dieses Feld speichert die Typkennung einer Eigenschaft.
	 * 
	 * @see #key()
	 * @see #value()
	 * @see #propertyToken(String, String)
	 */
	public static final int PROPERTY = 2;

	/**
	 * Dieses Feld speichert die Typkennung eines Kommentars.
	 * 
	 * @see #comment()
	 * @see #commentToken(String)
	 */
	public static final int COMMENT = 4;

	{}

	/**
	 * Diese Methode gibt einen Abschnitt mit dem gegebenen Namen als {@link INIToken} zurück.
	 * 
	 * @see #section()
	 * @param section Name des Abschnitts.
	 * @return Abschnitt.
	 * @throws NullPointerException Wenn {@code section} {@code null} ist.
	 */
	public static final INIToken sectionToken(final String section) throws NullPointerException {
		if (section == null) throw new NullPointerException("section = null");
		return new INIToken(null, null, section, null);
	}

	/**
	 * Diese Methode gibt einen Kommentar mit dem gegebenen Schlüssel und dem gegebenen Wert als {@link INIToken} zurück.
	 * 
	 * @see #key()
	 * @see #value()
	 * @param key Schlüssel der Eigenschaft.
	 * @param value Wert der Eigenschaft.
	 * @return Eigenschaft
	 * @throws NullPointerException Wenn {@code key} bzw. {@code value} {@code null} ist.
	 */
	public static final INIToken propertyToken(final String key, final String value) throws NullPointerException {
		if (key == null) throw new NullPointerException("key = null");
		if (value == null) throw new NullPointerException("value = null");
		return new INIToken(key, value, null, null);
	}

	/**
	 * Diese Methode gibt einen Kommentar mit dem gegebenen Text als {@link INIToken} zurück.
	 * 
	 * @see #comment()
	 * @param comment Text des Kommentar.
	 * @return Kommentar.
	 * @throws NullPointerException Wenn {@code comment} {@code null} ist.
	 */
	public static final INIToken commentToken(final String comment) throws NullPointerException {
		if (comment == null) throw new NullPointerException("comment = null");
		return new INIToken(null, null, null, comment);
	}

	{}

	/**
	 * Dieses Feld speichert den Schlüssel der Eigenschaft oder {@code null}.
	 */
	final String __key;

	/**
	 * Dieses Feld speichert den Wert der Eigenschaft oder {@code null}.
	 */
	final String __value;

	/**
	 * Dieses Feld speichert den Namen des Abschnitts oder {@code null}.
	 */
	final String __section;

	/**
	 * Dieses Feld speichert den Text des Kommentars oder {@code null}.
	 */
	final String __comment;

	@SuppressWarnings ("javadoc")
	INIToken(final String key, final String value, final String section, final String comment) {
		this.__key = key;
		this.__value = value;
		this.__section = section;
		this.__comment = comment;
	}

	{}

	/**
	 * Diese Methode gibt die Typkennung des Elements zurück.
	 * 
	 * @see #SECTION
	 * @see #PROPERTY
	 * @see #COMMENT
	 * @return Typkennung.
	 */
	public final int type() {
		return this.__section != null ? INIToken.SECTION : this.__comment != null ? INIToken.COMMENT : INIToken.PROPERTY;
	}

	/**
	 * Diese Methode gibt den Schlüssel der Eigenschaft zurück, wenn dieses Element ein {@link #PROPERTY} ist. Andernfalls wird {@code null} geliefert.
	 * 
	 * @see #propertyToken(String, String)
	 * @return Schlüssel der Eigenschaft oder {@code null}.
	 */
	public final String key() {
		return this.__key;
	}

	/**
	 * Diese Methode gibt den Wert der Eigenschaft zurück, wenn dieses Element ein {@link #PROPERTY} ist. Andernfalls wird {@code null} geliefert.
	 * 
	 * @see #propertyToken(String, String)
	 * @return Wert der Eigenschaft oder {@code null}.
	 */
	public final String value() {
		return this.__value;
	}

	/**
	 * Diese Methode gibt den Namen des Abschnitts zurück, wenn dieses Element eine {@link #SECTION} ist. Andernfalls wird {@code null} geliefert.
	 * 
	 * @see #sectionToken(String)
	 * @return Namen des Abschnitts oder {@code null}.
	 */
	public final String section() {
		return this.__section;
	}

	/**
	 * Diese Methode gibt den Text des Kommentars zurück, wenn dieses Element ein {@link #COMMENT} ist. Andernfalls wird {@code null} geliefert.
	 * 
	 * @see #commentToken(String)
	 * @return Text des Kommentars oder {@code null}.
	 */
	public final String comment() {
		return this.__comment;
	}

	{}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int hashCode() {
		return Objects.hash(this.__key, this.__value, this.__section, this.__comment);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof INIToken)) return false;
		final INIToken that = (INIToken)object;
		return Objects.equals(this.__key, that.__key) && Objects.equals(this.__value, that.__value) && //
			Objects.equals(this.__section, that.__section) && Objects.equals(this.__comment, that.__comment);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		switch (this.type()) {
			case SECTION:
				return "[" + Objects.toString(this.section()) + "]";
			case PROPERTY:
				return Objects.toString(this.key()) + "=" + Objects.toString(this.value());
			case COMMENT:
				return ";" + Objects.toString(this.comment());
			default:
				throw new IllegalStateException();
		}
	}

}