package bee.creative.fem;

/** Diese Klasse implementiert einen unveränderlichen Wahrheitswert. Intern wird der Wahrheitswert als {@code boolean} dargestellt.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMBoolean implements FEMValue, Comparable<FEMBoolean> {

	/** Dieses Feld speichert den {@link #type() Datentyp}. */
	public static final FEMType<FEMBoolean> TYPE = new FEMType<>(FEMBoolean.TYPE_ID);

	/** Dieses Feld speichert den Identifikator von {@link #TYPE}. */
	public static final int TYPE_ID = 3;

	/** Dieses Feld speichert den Wahrheitswert {@code true}. */
	public static final FEMBoolean TRUE = new FEMBoolean(true);

	/** Dieses Feld speichert den Wahrheitswert {@code false}. */
	public static final FEMBoolean FALSE = new FEMBoolean(false);

	/** Diese Methode gibt einen neuen Wahrheitswert mit dem gegebenen Wert zurück.
	 *
	 * @param data Wert.
	 * @return Wahrheitswert. */
	public static FEMBoolean from(boolean data) {
		return data ? TRUE : FALSE;
	}

	/** Diese Methode gibt einen neuen Wahrheitswert mit dem gegebenen Wert zurück.
	 *
	 * @see #from(boolean)
	 * @see Boolean#booleanValue()
	 * @param data Wert.
	 * @return Wahrheitswert.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist. */
	public static FEMBoolean from(Boolean data) throws NullPointerException {
		return from(data.booleanValue());
	}

	/** Diese Methode gibt einen neuen Wahrheitswert mit dem in der gegebenen Zeichenkette kodierten Wert zurück.
	 *
	 * @param value Zeichenkette ({@code "true"} oder {@code "false"}).
	 * @return Wahrheitswert.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Zeichenkette ungültig ist. */
	public static FEMBoolean from(String value) throws NullPointerException, IllegalArgumentException {
		if (value.equals("true")) return TRUE;
		if (value.equals("false")) return FALSE;
		throw new IllegalArgumentException();
	}

	/** Diese Methode gibt {@code this} zurück. */
	@Override
	public FEMBoolean data() {
		return this;
	}

	@Override
	public FEMType<FEMBoolean> type() {
		return TYPE;
	}

	/** Diese Methode gibt die interne Darstellung des Wahrheitswerts zurück. Diese ist ein {@code boolean}.
	 *
	 * @return interne Darstellung des Wahrheitswerts. */
	public boolean value() {
		return this.value;
	}

	@Override
	public int hashCode() {
		return this.value ? 1231 : 1237;
	}

	@Override
	public boolean equals(Object object) {
		if (object == this) return true;
		if (!(object instanceof FEMBoolean)) {
			if (!(object instanceof FEMValue)) return false;
			object = ((FEMValue)object).data();
			if (!(object instanceof FEMBoolean)) return false;
		}
		var that = (FEMBoolean)object;
		return this.value == that.value;
	}

	/** Diese Methode gibt {@code -1}, {@code 0} bzw. {@code +1} zurück, wenn dieser Wahrheitswert kleiner, gleich oder größer als der gegebene Wahrheitswert ist.
	 *
	 * @param that Wahrheitswert.
	 * @return Vergleichswert.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist. */
	@Override
	public int compareTo(FEMBoolean that) throws NullPointerException {
		return Boolean.compare(this.value, that.value);
	}

	/** Diese Methode gibt die Textdarstellung dieses Wahrheitswerts zurück. Für die Wahrheitswerte {@code true} und {@code false} sind die Textdarstellungen
	 * {@code "true"} und {@code "false"}.
	 *
	 * @return Textdarstellung. */
	@Override
	public String toString() {
		return this.value ? "true" : "false";
	}

	/** Diese Methode gibt diesen Wahrheitswert als {@link Boolean} zurück.
	 *
	 * @return {@link Boolean}. */
	public Boolean toBoolean() {
		return this.value;
	}

	private final boolean value;

	private FEMBoolean(boolean value) {
		this.value = value;
	}

}
