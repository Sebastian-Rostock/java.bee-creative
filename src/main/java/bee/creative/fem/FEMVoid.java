package bee.creative.fem;

/**
 * Diese Klasse implementiert einen Leerwert.
 * 
 * @author Sebastian Rostock 2015.
 */
public class FEMVoid {

	/**
	 * Dieses Feld speichert den Wahrheitswert {@code true}.
	 */
	public static final FEMVoid INSTANCE = new FEMVoid();

	{}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int hashCode() {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof FEMVoid)) return false;
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return FEM.formatVoid(this);
	}

}
