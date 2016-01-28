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
	final String _key_;

	/**
	 * Dieses Feld speichert den Wert der Eigenschaft oder {@code null}.
	 */
	final String _value_;

	/**
	 * Dieses Feld speichert den Namen des Abschnitts oder {@code null}.
	 */
	final String _section_;

	/**
	 * Dieses Feld speichert den Text des Kommentars oder {@code null}.
	 */
	final String _comment_;

	@SuppressWarnings ("javadoc")
	INIToken(final String key, final String value, final String section, final String comment) {
		this._key_ = key;
		this._value_ = value;
		this._section_ = section;
		this._comment_ = comment;
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
		return this._section_ != null ? INIToken.SECTION : this._comment_ != null ? INIToken.COMMENT : INIToken.PROPERTY;
	}

	/**
	 * Diese Methode gibt den Schlüssel der Eigenschaft zurück, wenn dieses Element ein {@link #PROPERTY} ist. Andernfalls wird {@code null} geliefert.
	 * 
	 * @see #propertyToken(String, String)
	 * @return Schlüssel der Eigenschaft oder {@code null}.
	 */
	public final String key() {
		return this._key_;
	}

	/**
	 * Diese Methode gibt den Wert der Eigenschaft zurück, wenn dieses Element ein {@link #PROPERTY} ist. Andernfalls wird {@code null} geliefert.
	 * 
	 * @see #propertyToken(String, String)
	 * @return Wert der Eigenschaft oder {@code null}.
	 */
	public final String value() {
		return this._value_;
	}

	/**
	 * Diese Methode gibt den Namen des Abschnitts zurück, wenn dieses Element eine {@link #SECTION} ist. Andernfalls wird {@code null} geliefert.
	 * 
	 * @see #sectionToken(String)
	 * @return Namen des Abschnitts oder {@code null}.
	 */
	public final String section() {
		return this._section_;
	}

	/**
	 * Diese Methode gibt den Text des Kommentars zurück, wenn dieses Element ein {@link #COMMENT} ist. Andernfalls wird {@code null} geliefert.
	 * 
	 * @see #commentToken(String)
	 * @return Text des Kommentars oder {@code null}.
	 */
	public final String comment() {
		return this._comment_;
	}

	{}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int hashCode() {
		return Objects.hash(this._key_, this._value_, this._section_, this._comment_);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof INIToken)) return false;
		final INIToken that = (INIToken)object;
		return Objects.equals(this._key_, that._key_) && Objects.equals(this._value_, that._value_) && //
			Objects.equals(this._section_, that._section_) && Objects.equals(this._comment_, that._comment_);
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