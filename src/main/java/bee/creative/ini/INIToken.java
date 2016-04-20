package bee.creative.ini;

import bee.creative.util.Objects;

/** Diese Klasse implementiert ein abstraktes Element, welches über einen {@link INIReader} aus einer {@code INI}-Datenstruktur gelesene und für einen Abschnitt,
 * eine Eigenschaft oder einen Kommentar stehen kann.
 * 
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public abstract class INIToken {

	@SuppressWarnings ("javadoc")
	static final class SectionToken extends INIToken {

		final String section;

		SectionToken(final String section) {
			this.section = section;
		}

		{}

		@Override
		public final int type() {
			return INIToken.SECTION;
		}

		@Override
		public final String section() {
			return this.section;
		}

		@Override
		public final boolean isSection() {
			return true;
		}

	}

	@SuppressWarnings ("javadoc")
	static final class PropertyToken extends INIToken {

		final String key;

		final String value;

		PropertyToken(final String key, final String value) {
			this.key = key;
			this.value = value;
		}

		{}

		@Override
		public final int type() {
			return INIToken.PROPERTY;
		}

		@Override
		public final String key() {
			return this.key;
		}

		@Override
		public final String value() {
			return this.value;
		}

		@Override
		public final boolean isProperty() {
			return true;
		}

	}

	@SuppressWarnings ("javadoc")
	static final class CommentToken extends INIToken {

		final String comment;

		CommentToken(final String comment) {
			this.comment = comment;
		}

		{}

		@Override
		public final int type() {
			return INIToken.COMMENT;
		}

		@Override
		public final String comment() {
			return this.comment;
		}

		@Override
		public final boolean isComment() {
			return true;
		}

	}

	{}

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

	{}

	/** Diese Methode gibt einen Abschnitt mit dem gegebenen Namen als {@link INIToken} zurück.
	 * 
	 * @see #section()
	 * @param section Name des Abschnitts.
	 * @return Abschnitt.
	 * @throws NullPointerException Wenn {@code section} {@code null} ist. */
	public static final INIToken fromSection(final String section) throws NullPointerException {
		return new SectionToken(section.intern());
	}

	/** Diese Methode gibt einen Kommentar mit dem gegebenen Schlüssel und dem gegebenen Wert als {@link INIToken} zurück.
	 * 
	 * @see #key()
	 * @see #value()
	 * @param key Schlüssel der Eigenschaft.
	 * @param value Wert der Eigenschaft.
	 * @return Eigenschaft
	 * @throws NullPointerException Wenn {@code key} bzw. {@code value} {@code null} ist. */
	public static final INIToken fromProperty(final String key, final String value) throws NullPointerException {
		return new PropertyToken(key.intern(), value.intern());
	}

	/** Diese Methode gibt einen Kommentar mit dem gegebenen Text als {@link INIToken} zurück.
	 * 
	 * @see #comment()
	 * @param comment Text des Kommentar.
	 * @return Kommentar.
	 * @throws NullPointerException Wenn {@code comment} {@code null} ist. */
	public static final INIToken fromComment(final String comment) throws NullPointerException {
		return new CommentToken(comment.intern());
	}

	{}

	@SuppressWarnings ("javadoc")
	INIToken() {
	}

	{}

	/** Diese Methode gibt die Typkennung des Elements zurück.
	 * 
	 * @see #SECTION
	 * @see #PROPERTY
	 * @see #COMMENT
	 * @return Typkennung. */
	public abstract int type();

	/** Diese Methode gibt den Schlüssel der Eigenschaft zurück, wenn dieses Element ein {@link #PROPERTY} ist. Andernfalls wird {@code null} geliefert.
	 * 
	 * @see #fromProperty(String, String)
	 * @return Schlüssel der Eigenschaft oder {@code null}. */
	public String key() {
		return null;
	}

	/** Diese Methode gibt den Wert der Eigenschaft zurück, wenn dieses Element ein {@link #PROPERTY} ist. Andernfalls wird {@code null} geliefert.
	 * 
	 * @see #fromProperty(String, String)
	 * @return Wert der Eigenschaft oder {@code null}. */
	public String value() {
		return null;
	}

	/** Diese Methode gibt den Namen des Abschnitts zurück, wenn dieses Element eine {@link #SECTION} ist. Andernfalls wird {@code null} geliefert.
	 * 
	 * @see #fromSection(String)
	 * @return Namen des Abschnitts oder {@code null}. */
	public String section() {
		return null;
	}

	/** Diese Methode gibt den Text des Kommentars zurück, wenn dieses Element ein {@link #COMMENT} ist. Andernfalls wird {@code null} geliefert.
	 * 
	 * @see #fromComment(String)
	 * @return Text des Kommentars oder {@code null}. */
	public String comment() {
		return null;
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn dieses Element ein {@link #SECTION Anschnitt} {@link #type() ist}.
	 * 
	 * @return {@code true}, wenn {@link #type()} {@code ==} {@link #SECTION}. */
	public boolean isSection() {
		return false;
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn dieses Element eine {@link #PROPERTY Eigenschaft} {@link #type() ist}.
	 * 
	 * @return {@code true}, wenn {@link #type()} {@code ==} {@link #PROPERTY}. */
	public boolean isProperty() {
		return false;
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn dieses Element ein {@link #COMMENT Kommentar} {@link #type() ist}.
	 * 
	 * @return {@code true}, wenn {@link #type()} {@code ==} {@link #COMMENT}. */
	public boolean isComment() {
		return false;
	}

	{}

	/** {@inheritDoc} */
	@Override
	public final int hashCode() {
		return Objects.hash(this.key(), this.value(), this.section(), this.comment());
	}

	/** {@inheritDoc} */
	@Override
	public final boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof INIToken)) return false;
		final INIToken that = (INIToken)object;
		return Objects.equals(this.key(), that.key()) && Objects.equals(this.value(), that.value()) && //
			Objects.equals(this.section(), that.section()) && Objects.equals(this.comment(), that.comment());
	}

	/** {@inheritDoc} */
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