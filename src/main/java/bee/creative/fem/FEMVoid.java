package bee.creative.fem;

/** Diese Klasse implementiert den unveränderlichen Leerwert.
 *
 * @author Sebastian Rostock 2015. */
public final class FEMVoid extends FEMValue {

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
	public static FEMVoid from(final String value) throws NullPointerException, IllegalArgumentException {
		if (value.equals("void")) return FEMVoid.INSTANCE;
		throw new IllegalArgumentException();
	}

	FEMVoid() {
	}

	/** Diese Methode gibt {@code this} zurück. */
	@Override
	public final FEMVoid data() {
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public final FEMType<FEMVoid> type() {
		return FEMVoid.TYPE;
	}

	/** {@inheritDoc} */
	@Override
	public final int hashCode() {
		return 0;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean equals(Object object) {
		if (object == this) return true;
		if (object instanceof FEMVoid) return true;
		if (!(object instanceof FEMValue)) return false;
		object = ((FEMValue)object).data();
		return object instanceof FEMVoid;
	}

	/** Diese Methode gibt {@code "void"} zurück. */
	@Override
	public final String toString() {
		return "void";
	}

}
