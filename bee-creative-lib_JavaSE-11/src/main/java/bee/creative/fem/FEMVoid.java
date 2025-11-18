package bee.creative.fem;

/** Diese Klasse implementiert den unveränderlichen Leerwert.
 *
 * @author Sebastian Rostock 2015. */
public final class FEMVoid implements FEMValue {

	/** Dieses Feld speichert den {@link #type() Datentyp}. */
	public static final FEMType<FEMVoid> TYPE = new FEMType<>(FEMVoid.TYPE_ID);

	/** Dieses Feld speichert den Identifikator von {@link #TYPE}. */
	public static final int TYPE_ID = 0;

	/** Dieses Feld speichert den Leerwert. */
	public static final FEMVoid VALUE = new FEMVoid();

	/** Diese Methode gibt {@code this} zurück. */
	@Override
	public FEMVoid data() {
		return this;
	}

	@Override
	public FEMType<FEMVoid> type() {
		return TYPE;
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

	/** Diese Methode liefert {@code "null"}. */
	@Override
	public String toString() {
		return "null";
	}

	private FEMVoid() {
	}

}
