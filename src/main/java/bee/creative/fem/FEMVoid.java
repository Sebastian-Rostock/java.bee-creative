package bee.creative.fem;

/**
 * Diese Klasse implementiert einen Leerwert.
 * 
 * @author Sebastian Rostock 2015.
 */
public final class FEMVoid {

	/**
	 * Dieses Feld speichert den Leerwert.
	 */
	public static final FEMVoid INSTANCE = new FEMVoid();

	{}

	/**
	 * Diese Methode gibt den Leerwert nur dann zurück, wenn die gegebenen Zeichenkette gleich {@code "void"} ist.
	 * 
	 * @param value Zeichenkette.
	 * @return Leerwert.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Zeichenkette ungültig ist.
	 */
	public static final FEMVoid from(final String value) throws NullPointerException, IllegalArgumentException {
		if (value.equals("void")) return FEMVoid.INSTANCE;
		throw new IllegalArgumentException();
	}

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
	public final String toString() {
		return "void";
	}

}
