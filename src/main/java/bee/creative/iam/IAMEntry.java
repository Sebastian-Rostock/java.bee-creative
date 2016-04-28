package bee.creative.iam;

/** Diese Klasse implementiert einen abstrakten Eintrag einer Abbildung ({@link IAMMapping}) und besteht aus einem Schlüssel sowie einem Wert, welche selbst
 * Zahlenfolgen ({@link IAMArray}) sind.
 * <p>
 * Die Methoden {@link #key(int)} und {@link #keyLength()} delegieren an {@link #key()}. Die Methoden {@link #value(int)} und {@link #valueLength()} delegieren
 * an {@link #value()}.
 * 
 * @see IAMMapping#entry(int)
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public abstract class IAMEntry {

	@SuppressWarnings ("javadoc")
	static final class EmptyEntry extends IAMEntry {

		@Override
		public final IAMArray value() {
			return IAMArray.EMPTY;
		}

		@Override
		public final IAMArray key() {
			return IAMArray.EMPTY;
		}

	}

	@SuppressWarnings ("javadoc")
	static final class SimpleEntry extends IAMEntry {

		final IAMArray _key_;

		final IAMArray _value_;

		SimpleEntry(final IAMArray key, final IAMArray value) {
			this._key_ = key;
			this._value_ = value;
		}

		{}

		@Override
		public final IAMArray key() {
			return this._key_;
		}

		@Override
		public final IAMArray value() {
			return this._value_;
		}

	}

	{}

	/** Dieses Feld speichert das leere {@link IAMEntry}. */
	public static final IAMEntry EMPTY = new EmptyEntry();

	{}

	/** Diese Methode ein neues {@link IAMEntry} als Sicht auf den gegebenen Schlüssel sowie dem gegebenen Wert zurück.
	 * 
	 * @param key Schlüssel.
	 * @param value Wert.
	 * @return {@link IAMEntry}-Sicht auf {@code key} und {@code value}.
	 * @throws NullPointerException Wenn {@code key} bzw. {@code value} {@code null} ist. */
	public static IAMEntry from(final IAMArray key, final IAMArray value) throws NullPointerException {
		if ((key.length() == 0) && (value.length() == 0)) return IAMEntry.EMPTY;
		return new SimpleEntry(key, value);
	}

	{}

	/** Diese Methode gibt den Schlüssel als Zahlenfolge zurück.
	 * 
	 * @see IAMMapping#key(int)
	 * @return Schlüssel. */
	public abstract IAMArray key();

	/** Diese Methode gibt die {@code index}-te Zahl des Schlüssels zurück. Bei einem ungültigen {@code index} wird {@code 0} geliefert.
	 * 
	 * @see IAMMapping#key(int, int)
	 * @param index Index der Zahl.
	 * @return {@code index}-te Zahl des Schlüssels. */
	public final int key(final int index) {
		return this.key().get(index);
	}

	/** Diese Methode gibt die Länge der Zahlenfolge des Schlüssels zurück ({@code 0..1073741823}).
	 * 
	 * @see IAMMapping#keyLength(int)
	 * @return Größe der Schlüssel. */
	public final int keyLength() {
		return this.key().length();
	}

	/** Diese Methode gibt den Wert als Zahlenfolge zurück.
	 * 
	 * @see IAMMapping#value(int)
	 * @return Wert. */
	public abstract IAMArray value();

	/** Diese Methode gibt die {@code index}-te Zahl des Werts zurück. Bei einem ungültigen {@code index} wird {@code 0} geliefert.
	 * 
	 * @see IAMMapping#value(int, int)
	 * @param index Index der Zahl.
	 * @return {@code index}-te Zahl des Werts. */
	public final int value(final int index) {
		return this.value().get(index);
	}

	/** Diese Methode gibt die Länge der Zahlenfolge des Werts zurück ({@code 0..1073741823}).
	 * 
	 * @see IAMMapping#valueLength(int)
	 * @return Größe der Werte. */
	public final int valueLength() {
		return this.value().length();
	}

	{}

	/** {@inheritDoc} */
	@Override
	public final int hashCode() {
		return this.key().hash() ^ this.value().hash();
	}

	/** {@inheritDoc} */
	@Override
	public final boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof IAMEntry)) return false;
		final IAMEntry that = (IAMEntry)object;
		return this.key().equals(that.key()) && this.value().equals(that.value());
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return this.key() + "=" + this.value();
	}

}