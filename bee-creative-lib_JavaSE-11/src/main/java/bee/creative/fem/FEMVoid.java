package bee.creative.fem;

/** Diese Klasse implementiert den unveränderlichen Leerwert.
 *
 * @author Sebastian Rostock 2015. */
public final class FEMVoid implements FEMValue {

	/** Dieses Feld speichert den Identifikator von {@link #TYPE}. */
	public static final int ID = 0;

	/** Dieses Feld speichert den {@link #type() Datentyp}. */
	public static final FEMType<FEMVoid> TYPE = FEMType.from(FEMVoid.ID);

	/** Dieses Feld speichert den Leerwert. */
	public static final FEMVoid INSTANCE = new FEMVoid();

	/** Diese Methode gibt den Leerwert nur dann zurück, wenn die gegebenen Zeichenkette gleich {@code "void"} ist.
	 *
	 * @param value Zeichenkette.
	 * @return Leerwert.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Zeichenkette ungültig ist. */
	public static FEMVoid from(String value) throws NullPointerException, IllegalArgumentException {
		if (value.equals("void")) return FEMVoid.INSTANCE;
		throw new IllegalArgumentException();
	}

	/** Diese Methode gibt {@code this} zurück. */
	@Override
	public FEMVoid data() {
		return this;
	}

	@Override
	public FEMType<FEMVoid> type() {
		return FEMVoid.TYPE;
	}

	@Override
	public int hashCode() {
		return 0;
	}

	@Override
	public boolean equals(Object object) {
		if ((object == this) || (object instanceof FEMVoid)) return true;
		if (!(object instanceof FEMValue)) return false;
		object = ((FEMValue)object).data();
		return object instanceof FEMVoid;
	}

	/** Diese Methode gibt {@code "null"} zurück. */
	@Override
	public String toString() {
		return "null";
	}

	FEMVoid() {
	}

}
