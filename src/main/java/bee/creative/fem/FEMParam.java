package bee.creative.fem;

/** Diese Klasse implementiert eine projizierende Funktion, deren Ergebniswert einem der Parameterwerte des Stapelrahmens entspricht.
 *
 * @see #index()
 * @see #invoke(FEMFrame)
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMParam extends FEMFunction {

	/** Dieses Feld speichert die projezierenden Funktionen für die Indizes {@code 0..15}. */
	static final FEMParam[] CACHE = { //
		new FEMParam(0), new FEMParam(1), new FEMParam(2), new FEMParam(3), new FEMParam(4), new FEMParam(5), new FEMParam(6), new FEMParam(7), new FEMParam(8),
		new FEMParam(9), new FEMParam(10), new FEMParam(11), new FEMParam(12), new FEMParam(13), new FEMParam(14), new FEMParam(15)};

	/** Dieses Feld speichert eine Funktion mit der Signatur {@code (index: Integer): Value}, deren Ergebniswert dem {@code index}-ten Parameterwert des
	 * Stapelrahmens entspricht. */
	public static final FEMFunction FUNCTION = new FEMFunction() {

		@Override
		public FEMValue invoke(final FEMFrame frame) {
			if (frame.size() != 1) throw new IllegalArgumentException("frame.size() <> 1");
			final long integer = FEMInteger.from(frame.get(0), frame.context()).value();
			final int index = integer < Integer.MIN_VALUE ? Integer.MIN_VALUE : integer > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)integer;
			final FEMValue result = frame.get(index);
			return result;
		}

		@Override
		public String toString() {
			return "$#";
		}

	};

	/** Diese Methode gibt eine Funktion zurück, welche den {@code index}-ten Parameterwert des Stapelrahmens als Ergebniswert liefert.
	 *
	 * @param index Index des Parameterwerts.
	 * @return {@link FEMParam}.
	 * @throws IllegalArgumentException Wenn {@code index < 0} ist. */
	public static FEMParam from(final int index) throws IllegalArgumentException {
		if (index < 0) throw new IllegalArgumentException("index < 0");
		if (index < FEMParam.CACHE.length) return FEMParam.CACHE[index];
		return new FEMParam(index);
	}

	/** Dieses Feld speichert den Index des Parameterwerts. */
	final int index;

	@SuppressWarnings ("javadoc")
	FEMParam(final int index) {
		this.index = index;
	}

	/** Diese Methode gibt den Index des Parameterwerts zurück.
	 *
	 * @return Index des Parameterwerts.
	 * @see #invoke(FEMFrame) */
	public int index() {
		return this.index;
	}

	/** {@inheritDoc}
	 * <p>
	 * Der Ergebniswert entspricht {@code frame.get(this.index())}.
	 *
	 * @see #index() */
	@Override
	public FEMValue invoke(final FEMFrame frame) {
		return frame.get(this.index);
	}

	/** {@inheritDoc} */
	@Override
	public final int hashCode() {
		return this.index;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof FEMParam)) return false;
		return this.index == ((FEMParam)object).index;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "$" + (this.index + 1);
	}

}