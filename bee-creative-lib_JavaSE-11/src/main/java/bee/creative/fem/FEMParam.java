package bee.creative.fem;

/** Diese Klasse implementiert eine projizierende Funktion, deren Ergebniswert einem der Parameterwerte des Stapelrahmens entspricht.
 *
 * @see #index()
 * @see #invoke(FEMFrame)
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMParam implements FEMFunction {

	/** Dieses Feld speichert eine Funktion mit der Signatur {@code (index: Integer): Value}, deren Ergebniswert dem {@code index}-ten Parameterwert des
	 * übergeordneten Stapelrahmens entspricht. */
	public static final FEMFunction FUNCTION = new FEMFunction() {

		@Override
		public FEMValue invoke(final FEMFrame frame) {
			if (frame.size() != 1) throw new IllegalArgumentException("frame.size() <> 1");
			var integer = frame.context().dataFrom(frame.get(0), FEMInteger.TYPE).value();
			var index = integer < Integer.MIN_VALUE ? Integer.MIN_VALUE : integer > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)integer;
			var result = frame.parent().get(index);
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
	public static FEMParam from(int index) throws IllegalArgumentException {
		if (index < 0) throw new IllegalArgumentException("index < 0");
		if (index < FEMParam.CACHE.length) return FEMParam.CACHE[index];
		return new FEMParam(index);
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
	public FEMValue invoke(FEMFrame frame) {
		return frame.get(this.index);
	}

	@Override
	public FEMFunction compose(FEMFunction... params) throws NullPointerException {
		if (this.index < params.length) return params[this.index];
		return FEMParam.from(this.index - params.length);
	}

	@Override
	public int hashCode() {
		return this.index;
	}

	@Override
	public boolean equals(Object object) {
		if (object == this) return true;
		if (!(object instanceof FEMParam)) return false;
		var that = (FEMParam)object;
		return this.index == that.index;
	}

	@Override
	public String toString() {
		return "$" + (this.index + 1);
	}

	/** Dieses Feld speichert die projezierenden Funktionen für die Indizes {@code 0..15}. */
	static final FEMParam[] CACHE = { //
		new FEMParam(0), new FEMParam(1), new FEMParam(2), new FEMParam(3), new FEMParam(4), new FEMParam(5), new FEMParam(6), new FEMParam(7), //
		new FEMParam(8), new FEMParam(9), new FEMParam(10), new FEMParam(11), new FEMParam(12), new FEMParam(13), new FEMParam(14), new FEMParam(15)};

	/** Dieses Feld speichert den Index des Parameterwerts. */
	final int index;

	FEMParam(int index) {
		this.index = index;
	}

}