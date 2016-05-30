package bee.creative.fem;

import java.util.Arrays;
import bee.creative.fem.FEM.ScriptFormatter;
import bee.creative.fem.FEMTraceFunction.Tracer;
import bee.creative.fem.FEMTraceFunction.TracerInput;

/** Diese Klasse implementiert eine Funktion, die den Aufruf einer gegebenen Funktion mit den Ergebniswerten mehrerer gegebener Parameterfunktionen berechnet.
 * 
 * @see #invoke(FEMFrame)
 * @see FEMFrame#withParams(FEMFunction[])
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMInvokeFunction extends FEMBaseFunction implements TracerInput {

	/** Dieses Feld speichert eine Funktion mit der Signatur {@code (method: FEMFunction, params: FEMArray): FEMValue}, deren Ergebniswert via
	 * {@code method(params[0], params[1], ...)} ermittelt wird, d.h. über den Aufruf der als ersten Parameter gegeben Funktion mit den im zweiten Parameter
	 * gegebenen Parameterwertliste. */
	public static final FEMBaseFunction CALL = new FEMBaseFunction() {

		@Override
		public FEMValue invoke(final FEMFrame frame) {
			if (frame.size() != 2) throw new IllegalArgumentException("frame.size() != 2");
			final FEMContext context = frame.context();
			final FEMArray array = FEMArray.from(frame.get(1), context);
			final FEMFunction method = FEMHandler.from(frame.get(0), context).value();
			final FEMFrame params = frame.withParams(array);
			return method.invoke(params);
		}

		@Override
		public void toScript(final FEM.ScriptFormatter target) throws IllegalArgumentException {
			target.put("CALL");
		}

	};

	/** Dieses Feld speichert eine Funktion mit der Signatur {@code (param1, ..., paramN: FEMValue, method: FEMFunction): FEMValue}, deren Ergebniswert via
	 * {@code method(param1, ..., paramN)} ermittelt wird, d.h. über den Aufruf der als letzten Parameter gegeben Funktion mit den davor liegenden Parametern. */
	public static final FEMBaseFunction APPLY = new FEMBaseFunction() {

		@Override
		public FEMValue invoke(final FEMFrame frame) {
			final int index = frame.size() - 1;
			if (index < 0) throw new IllegalArgumentException("frame.size() < 1");
			final FEMContext context = frame.context();
			final FEMArray array = frame.params().section(0, index);
			final FEMFunction method = FEMHandler.from(frame.get(index), context).value();
			final FEMFrame params = frame.withParams(array);
			return method.invoke(params);
		}

		@Override
		public void toScript(final FEM.ScriptFormatter target) throws IllegalArgumentException {
			target.put("APPLY");
		}

	};

	{}

	/** Diese Methode gibt eine neue {@link FEMInvokeFunction} mit den gegebenen Parametern zurück.
	 * 
	 * @param function aufzurufende Funktion.
	 * @param direct {@code true}, wenn die aufzurufende Funktion direkt mit den Ergebnissen der Parameterfunktionen ausgewertet werden soll, und {@code false},
	 *        wenn die aufzurufende Funktion mit den Stapelrahmen zu einer Funktion ausgewertet werden soll, welche dann mit den Ergebnissen der
	 *        Parameterfunktionen ausgewertet werden soll.
	 * @param params Parameterfunktionen, deren Ergebniswerte als Parameterwerte beim Aufruf der Funktion verwendet werden sollen.
	 * @return {@link FEMInvokeFunction}.
	 * @throws NullPointerException Wenn {@code function} bzw. {@code params} {@code null} ist. */
	public static FEMInvokeFunction from(final FEMFunction function, final boolean direct, final FEMFunction... params) throws NullPointerException {
		return new FEMInvokeFunction(function, direct, params);
	}

	{}

	/** Dieses Feld speichert {@code true}, wenn die Verkettung aktiviert ist. */
	final boolean _direct_;

	/** Dieses Feld speichert die aufzurufende Funktion. */
	final FEMFunction _function_;

	/** Dieses Feld speichert die Parameterfunktionen, deren Ergebniswerte als Parameterwerte verwendet werden sollen. */
	final FEMFunction[] _params_;

	/** Dieser Konstruktor initialisiert die aufzurufende Funktion, die Verkettung und die Parameterfunktionen.
	 * 
	 * @param function aufzurufende Funktion.
	 * @param direct {@code true}, wenn die aufzurufende Funktion direkt mit den Ergebnissen der Parameterfunktionen ausgewertet werden soll, und {@code false},
	 *        wenn die aufzurufende Funktion mit den Stapelrahmen zu einer Funktion ausgewertet werden soll, welche dann mit den Ergebnissen der
	 *        Parameterfunktionen ausgewertet werden soll.
	 * @param params Parameterfunktionen, deren Ergebniswerte als Parameterwerte beim Aufruf der Funktion verwendet werden sollen.
	 * @throws NullPointerException Wenn {@code function} bzw. {@code params} {@code null} ist. */
	public FEMInvokeFunction(final FEMFunction function, final boolean direct, final FEMFunction... params) throws NullPointerException {
		if (function == null) throw new NullPointerException("function = null");
		if (params == null) throw new NullPointerException("params = null");
		this._direct_ = direct;
		this._function_ = function;
		this._params_ = params;
	}

	{}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn die {@link #function() aufzurufende Funktion} direkt mit den Ergebnissen der {@link #params()
	 * Parameterfunktionen} aufgerufen wird. Andernfalls wird die aufzurufende Funktion mit den in {@link #invoke(FEMFrame)} gegebenen Stapelrahmen zu einer
	 * Funktion ausgewertet, welche dann mit den Ergebnissen der Parameterfunktionen aufgerufen wird.
	 * 
	 * @return Verkettung.
	 * @see #invoke(FEMFrame) */
	public final boolean direct() {
		return this._direct_;
	}

	/** Diese Methode gibt eine Kopie der Parameterfunktionen zurück.
	 * 
	 * @return Kopie der Parameterfunktionen.
	 * @see #invoke(FEMFrame) */
	public final FEMFunction[] params() {
		return this._params_.clone();
	}

	/** Diese Methode gibt die aufzurufende Funktion zurück.
	 * 
	 * @return aufzurufende Funktion.
	 * @see #invoke(FEMFrame) */
	public final FEMFunction function() {
		return this._function_;
	}

	/** Diese Methode gibt eine zu dieser Funktion gleichwertige {@link FEMInvokeFunction} zurück, bei welcher {@link #function()} und jede Parameterfunktion in
	 * {@link #params()} in eine {@link FEMResultFunction} konvertiert wurde.
	 * 
	 * @see FEMResultFunction#from(FEMFunction)
	 * @return neue {@link FEMInvokeFunction} Funktion mit Parameterfunktionen, die {@link FEMResultFunction} sind. */
	public final FEMInvokeFunction toResult() {
		final FEMFunction[] functions = this._params_.clone();
		for (int i = 0, size = functions.length; i < size; i++) {
			functions[i] = FEMResultFunction.from(functions[i]);
		}
		return FEMInvokeFunction.from(FEMResultFunction.from(this._function_), this._direct_, functions);
	}

	{}

	/** {@inheritDoc} <br>
	 * Bei der direkten Aufrufart entspricht der Ergebniswert:<br>
	 * {@code this.function().invoke(frame.newFrame(this.params())}.<br>
	 * Damit wird die aufzurufende Funktion direkt mit den Parameterfunktionen aufgerufen. Andernfalls entspricht der Ergebniswert:<br>
	 * {@code FEMHandler.from(this.function().invoke(frame), frame.context()).value().invoke(frame.newFrame(this.params())}.<br>
	 * Damit wird die aufzurufende Funktion mit dem gegebenen Stapelrahmen in eine Funktion ausgewertet, die anschließend mit den Parameterfunktionen aufgerufen
	 * wird.
	 * 
	 * @see #direct()
	 * @see #params()
	 * @see #function()
	 * @see FEMFrame#newFrame(FEMFunction...) */
	@Override
	public final FEMValue invoke(FEMFrame frame) {
		final FEMFunction function;
		if (this._direct_) {
			function = this._function_;
		} else {
			final FEMValue value = this._function_.invoke(frame);
			if (value == null) throw new NullPointerException("this.function().invoke(frame) = null");
			function = FEMHandler.from(value, frame._context_).value();
		}
		frame = frame.newFrame(this._params_);
		final FEMValue result = function.invoke(frame);
		if (result == null) throw new NullPointerException("function.invoke(frame.newFrame(this.params()) = null");
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public final FEMFunction toTrace(final Tracer tracer) throws NullPointerException {
		final FEMFunction[] params = this._params_;
		for (int i = 0, size = params.length; i < size; i++) {
			params[i] = tracer.trace(params[i]);
		}
		return FEMInvokeFunction.from(tracer.trace(this._function_), this._direct_, params);
	}

	/** {@inheritDoc} */
	@Override
	public final void toScript(final ScriptFormatter target) throws IllegalArgumentException {
		target.putFunction(this._function_).putParams(Arrays.asList(this._params_));
	}

}