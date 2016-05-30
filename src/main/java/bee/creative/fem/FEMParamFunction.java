package bee.creative.fem;

import bee.creative.fem.FEM.ScriptFormatter;

/** Diese Klasse implementiert eine projizierende Funktion, deren Ergebniswert einem der Parameterwerte des Stapelrahmens entspricht.
 * 
 * @see #index()
 * @see #invoke(FEMFrame)
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMParamFunction extends FEMBaseFunction {

	/** Dieses Feld speichert die projezierenden Funktionen für die Indizes {@code 0..9}. */
	static final FEMParamFunction[] _cache_ = {new FEMParamFunction(0), new FEMParamFunction(1), new FEMParamFunction(2), new FEMParamFunction(3),
		new FEMParamFunction(4), new FEMParamFunction(5), new FEMParamFunction(6), new FEMParamFunction(7), new FEMParamFunction(8), new FEMParamFunction(9)};

	/** Dieses Feld speichert eine Funktion mit der Signatur {@code (index: Integer): Value}, deren Ergebniswert dem {@code index}-ten Parameterwert des
	 * Stapelrahmens entspricht. */
	public static final FEMBaseFunction ITEM = new FEMBaseFunction() {

		@Override
		public FEMValue invoke(final FEMFrame frame) {
			if (frame.size() != 1) throw new IllegalArgumentException("frame.size() <> 1");
			final long integer = FEMInteger.from(frame.get(0), frame.context()).value();
			final int index = integer < Integer.MIN_VALUE ? Integer.MIN_VALUE : integer > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)integer;
			final FEMValue result = frame.get(index);
			return result;
		}

		@Override
		public void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			target.put("ITEM");
		}

	};

	/** Dieses Feld speichert eine Funktion, deren Ergebniswert einer Kopie der Parameterwerte des Stapelrahmens {@code frame} entspricht, d.h.
	 * {@code frame.params().result(true)}.
	 * 
	 * @see FEMArray#from(FEMValue...)
	 * @see FEMFrame#params() */
	public static final FEMBaseFunction COPY = new FEMBaseFunction() {

		@Override
		public FEMValue invoke(final FEMFrame frame) {
			return frame.params().result(true);
		}

		@Override
		public void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			target.put("$");
		}

	};

	/** Dieses Feld speichert eine Funktion, deren Ergebniswert einer Sicht auf die Parameterwerte des Stapelrahmens {@code frame} entspricht, d.h.
	 * {@code frame.params()}.
	 * 
	 * @see FEMFrame#params() */
	public static final FEMBaseFunction VIEW = new FEMBaseFunction() {

		@Override
		public FEMValue invoke(final FEMFrame frame) {
			return frame.params();
		}

		@Override
		public void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			target.put("$");
		}

	};

	{}

	/** Diese Methode gibt eine Funktion zurück, welche den {@code index}-ten Parameterwert des Stapelrahmens als Ergebniswert liefert.
	 * 
	 * @param index Index des Parameterwerts.
	 * @return {@link FEMParamFunction}.
	 * @throws IndexOutOfBoundsException Wenn {@code index < 0} ist. */
	public static FEMParamFunction from(final int index) throws IndexOutOfBoundsException {
		if (index < 0) throw new IndexOutOfBoundsException("index < 0");
		if (index < FEMParamFunction._cache_.length) return FEMParamFunction._cache_[index];
		return new FEMParamFunction(index);
	}

	{}

	/** Dieses Feld speichert den Index des Parameterwerts. */
	final int _index_;

	/** Dieser Konstruktor initialisiert den Index des Parameterwerts.
	 * 
	 * @param index Index des Parameterwerts.
	 * @throws IndexOutOfBoundsException Wenn {@code index < 0} ist. */
	public FEMParamFunction(final int index) throws IndexOutOfBoundsException {
		if (index < 0) throw new IndexOutOfBoundsException("index < 0");
		this._index_ = index;
	}

	{}

	/** Diese Methode gibt den Index des Parameterwerts zurück.
	 * 
	 * @return Index des Parameterwerts.
	 * @see #invoke(FEMFrame) */
	public final int index() {
		return this._index_;
	}

	{}

	/** {@inheritDoc}
	 * <p>
	 * Der Ergebniswert entspricht {@code frame.get(this.index())}.
	 * 
	 * @see #index() */
	@Override
	public final FEMValue invoke(final FEMFrame frame) {
		return frame.get(this._index_);
	}

	/** {@inheritDoc} */
	@Override
	public final void toScript(final ScriptFormatter target) throws IllegalArgumentException {
		target.put("$").put(new Integer(this._index_ + 1));
	}

}