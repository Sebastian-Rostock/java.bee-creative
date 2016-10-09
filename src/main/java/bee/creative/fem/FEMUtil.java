package bee.creative.fem;

/** Diese Klasse implementiert allgemeinen Hilfsfunktionen.
 * 
 * @see FEMFunction
 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class FEMUtil {

	/** Dieses Feld speichert eine Funktion mit der Signatur {@code (condition: FEMBoolean; trueResult: FEMValue; falseResult: FEMValue): FEMValue}, deren
	 * Ergebniswert via {@code (condition ? trueResult : falseResult)} ermittelt wird. */
	public static final FEMFunction IF = new FEMFunction() {
	
		@Override
		public FEMValue invoke(final FEMFrame frame) throws NullPointerException {
			if (frame.size() != 3) throw new IllegalArgumentException("frame.size() != 3");
			return frame.get(FEMBoolean.from(frame.get(0), frame.context()).value() ? 1 : 2).result();
		}
	
		@Override
		public void toScript(final FEMFormatter target) throws IllegalArgumentException {
			target.put("IF");
		}
	
	};
	/** Dieses Feld speichert eine Funktion mit der Signatur {@code (param1; ...; paramN; result: FEMValue): FEMValue}, welche die gegebenen Parameter in der
	 * gegebenen Reigenfolge {@link FEMValue#result(boolean) rekursiv auswertet} und den letzten als Ergebniswert liefert. */
	public static final FEMFunction EVAL = new FEMFunction() {
	
		@Override
		public FEMValue invoke(final FEMFrame frame) throws NullPointerException {
			final int size = frame.size() - 1;
			for (int i = 0; i < size; i++) {
				frame.get(i).result();
			}
			return frame.get(size).result();
		}
	
		@Override
		public void toScript(final FEMFormatter target) throws IllegalArgumentException {
			target.put("EVAL");
		}
	
	};
	/** Dieses Feld speichert eine Funktion mit der Signatur {@code (method: FEMHandler; params: FEMArray): FEMValue}, deren Ergebniswert via
	 * {@code method(params[0], params[1], ...)} ermittelt wird, d.h. über den Aufruf der als ersten Parameter gegeben Funktion mit den im zweiten Parameter
	 * gegebenen Parameterwertliste. */
	public static final FEMFunction CALL = new FEMFunction() {
	
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
		public void toScript(final FEMFormatter target) throws IllegalArgumentException {
			target.put("CALL");
		}
	
	};
	/** Dieses Feld speichert eine Funktion mit der Signatur {@code (param1; ...; paramN: FEMValue; method: FEMHandler): FEMValue}, deren Ergebniswert via
	 * {@code method(param1, ..., paramN)} ermittelt wird, d.h. über den Aufruf der als letzten Parameter gegeben Funktion mit den davor liegenden Parametern. */
	public static final FEMFunction APPLY = new FEMFunction() {
	
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
		public void toScript(final FEMFormatter target) throws IllegalArgumentException {
			target.put("APPLY");
		}
	
	};
	/** Dieses Feld speichert eine Funktion mit der Signatur {@code (method: FEMHandler): FEMInteger}, deren Ergebniswert der Anzahl der parameterlosen Aufrufe der
	 * gegebenen Methode entspricht. Die Methode wird mindestens ein Mal aufgerufen und muss immer einen {@link FEMBoolean} liefern. Sie wird nur dann wiederholt
	 * aufgerufen, wenn sie {@link FEMBoolean#TRUE} liefert. Die Ermittlung des Ergebniswerts {@code result} entspricht damit in etwa
	 * {@code for(result = 1; method(); result++);} */
	public static final FEMFunction REPEAT = new FEMFunction() {
	
		@Override
		public FEMValue invoke(final FEMFrame frame) throws NullPointerException {
			if (frame.size() != 1) throw new IllegalArgumentException("frame.size() != 1");
			final FEMContext context = frame.context();
			final FEMFunction method = FEMHandler.from(frame.get(0), context).value();
			for (int count = 0; true; count++) {
				final FEMBoolean repeat = FEMBoolean.from(method.invoke(frame.withoutParams()), context);
				if (!repeat.value()) return FEMInteger.from(count);
			}
		}
	
		@Override
		public void toScript(final FEMFormatter target) throws IllegalArgumentException {
			target.put("REPEAT");
		}
	
	};
	/** Dieses Feld speichert eine Funktion mit der Signatur {@code (pointer: FEMPointer): FEMValue}, deren Ergebniswert via {@code pointer.get()} ermittelt wird. */
	public static final FEMFunction GETVALUE = new FEMFunction() {
	
		@Override
		public FEMValue invoke(final FEMFrame frame) throws NullPointerException {
			if (frame.size() != 1) throw new IllegalArgumentException("frame.size() != 1");
			final FEMPointer pointer = FEMPointer.from(frame.get(0), frame.context());
			return pointer.get().result();
		}
	
		@Override
		public void toScript(final FEMFormatter target) throws IllegalArgumentException {
			target.put("GETVALUE");
		}
	
	};
	/** Dieses Feld speichert eine Funktion mit der Signatur {@code (pointer: FEMPointer; value: FEMValue): FEMValue}, welche über {@code pointer.set(value)} den
	 * Wert des gegebenen {@link FEMPointer} setzt und den gegebenen Ergebniswert {@code value} liefert. */
	public static final FEMFunction SETVALUE = new FEMFunction() {
	
		@Override
		public FEMValue invoke(final FEMFrame frame) throws NullPointerException {
			if (frame.size() != 2) throw new IllegalArgumentException("frame.size() != 2");
			final FEMPointer pointer = FEMPointer.from(frame.get(0), frame.context());
			final FEMValue value = frame.get(1).result();
			pointer.set(value);
			return value;
		}
	
		@Override
		public void toScript(final FEMFormatter target) throws IllegalArgumentException {
			target.put("SETVALUE");
		}
	
	};

}
