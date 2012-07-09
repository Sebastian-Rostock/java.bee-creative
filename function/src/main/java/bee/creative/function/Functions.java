package bee.creative.function;

import java.util.Arrays;
import bee.creative.function.Scopes.CompositeScope;
import bee.creative.function.Values.ReturnValue;
import bee.creative.util.Objects;

/**
 * Diese Klasse implementiert Hilfsklassen und Hilfsmethoden zur Erzeugung von {@link Function Funktionen}.
 * 
 * @see Value
 * @see Scopes
 * @see Function
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class Functions {

	/**
	 * Diese Klasse implementiert eine {@link Function Funktion} mit konstantem {@link Value Ergebniswert}.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ValueFunction implements Function {

		/**
		 * Dieses Feld speichert den {@link Value Ergebniswert}.
		 */
		final Value value;

		/**
		 * Dieser Konstrukteur initialisiert den {@link Value Ergebniswert}.
		 * 
		 * @param value {@link Value Ergebniswert}.
		 * @throws NullPointerException Wenn der gegebene {@link Value Ergebniswert} {@code null} ist.
		 */
		public ValueFunction(final Value value) throws NullPointerException {
			if(value == null) throw new NullPointerException("value is null");
			this.value = value;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Value execute(final Scope scope) {
			return this.value;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return this.value.hashCode();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof ValueFunction)) return false;
			final ValueFunction data = (ValueFunction)object;
			return Objects.equals(this.value, data.value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("valueFunction", this.value);
		}

	}

	/**
	 * Diese Klasse implementiert eine projizierende {@link Function Funktion}, deren {@link Value Ergebniswert} einem der
	 * {@link Value Parameterwerte} des {@link Scope Ausführungskontexts} entspricht.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ParameterFunction implements Function {

		/**
		 * Dieses Feld speichert den Index des {@link Value Parameterwerts}.
		 */
		final int index;

		/**
		 * Dieser Konstrukteur initialisiert den Index des {@link Value Parameterwerts}.
		 * 
		 * @param index Index des {@link Value Parameterwerts}.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index negativ ist.
		 */
		public ParameterFunction(final int index) throws IndexOutOfBoundsException {
			if(index < 0) throw new IndexOutOfBoundsException("index out of range: " + index);
			this.index = index;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Value execute(final Scope scope) {
			return scope.get(this.index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return this.index;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof ParameterFunction)) return false;
			final ParameterFunction data = (ParameterFunction)object;
			return this.index == data.index;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("paramFunction", this.index);
		}

	}

	/**
	 * Diese Klasse definiert eine komponierte {@link Function Funktion}, die den Aufruf einer gegebenen {@link Function
	 * Funktion} mit den {@link Value Ergebniswerten} mehrerer gegebener {@link Function Parameterfunktionen} als
	 * {@link Value Parameterwerte} berechnet.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class CompositeFunction implements Function {

		/**
		 * Dieses Feld speichert die aufzurufende {@link Function Funktion}.
		 */
		final Function function;

		/**
		 * Dieses Feld speichert die {@link Function Parameterfunktionen}, deren {@link Value Ergebniswerte} als
		 * {@link Value Parameterwerte} verwendet werden sollen.
		 */
		final Function[] functions;

		/**
		 * Dieser Konstrukteur initialisiert die aufzurufende {@link Function Funktion} und die {@link Function
		 * Parameterfunktionen}.
		 * 
		 * @param function {@link Function Funktion}.
		 * @param functions {@link Function Parameterfunktionen}.
		 * @throws NullPointerException Wenn eine der gegebenen {@link Function Funktionen} {@code null} ist.
		 */
		public CompositeFunction(final Function function, final Function... functions) throws NullPointerException {
			if(function == null) throw new NullPointerException("functions is null");
			if(functions == null) throw new NullPointerException("functions is null");
			if(Arrays.asList(functions).contains(null)) throw new NullPointerException("functions contains null");
			this.function = function;
			this.functions = functions;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Value execute(final Scope scope) {
			return new ReturnValue(this.function, new CompositeScope(this.functions, scope));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return Objects.hash(this.function) ^ Objects.hash((Object[])this.functions);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof CompositeFunction)) return false;
			final CompositeFunction data = (CompositeFunction)object;
			return Objects.equals(this.function, data.function) && Objects.equals(this.functions, data.functions);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("chainedFunction", this.function, this.functions);
		}

	}

	/**
	 * Dieses Feld speichert die {@code null}-{@link Function Funktion}.
	 */
	static final Function VOID_FUNCTION = new Function() {

		@Override
		public Value execute(final Scope scope) {
			return Values.voidValue();
		}

		@Override
		public int hashCode() {
			return 0;
		}

		@Override
		public String toString() {
			return Objects.toStringCall("voidFunction");
		}

	};

	/**
	 * Dieses Feld speichert die projezierenden {@link Function Funktionen} für die Indizes {@code 0} bis {@code 9}.
	 */
	static final Function[] PARAMETER_FUNCTIONS = {new ParameterFunction(0), new ParameterFunction(1),
		new ParameterFunction(2), new ParameterFunction(3), new ParameterFunction(4), new ParameterFunction(5),
		new ParameterFunction(6), new ParameterFunction(7), new ParameterFunction(8), new ParameterFunction(9)};

	/**
	 * Diese Methode gibt die leere {@link Function Funktion} zurück, deren {@link Value Ergebniswert}
	 * {@link Values#voidValue()} ist.
	 * 
	 * @see Values#voidValue()
	 * @return {@link Function Funktion}.
	 */
	public static final Function voidFunction() {
		return Functions.VOID_FUNCTION;
	}

	/**
	 * Diese Methode erzeugt eine {@link Function Funktion} mit konstantem {@link Value Ergebniswert} und gibt diese
	 * zurück.
	 * 
	 * @see Values#value(Object)
	 * @see Functions#voidFunction()
	 * @see Functions#valueFunction(Value)
	 * @param data {@link Value Ergebniswert}.
	 * @return {@link ValueFunction Value-Funktion}.
	 */
	public static final Function valueFunction(final Object data) {
		if(data == null) return Functions.voidFunction();
		return new ValueFunction(Values.value(data));
	}

	/**
	 * Diese Methode erzeugt eine {@link Function Funktion} mit konstantem {@link Value Ergebniswert} und gibt diese
	 * zurück.
	 * 
	 * @see Functions#voidFunction()
	 * @param data {@link Value Ergebniswert}.
	 * @return {@link ValueFunction Value-Funktion}.
	 */
	public static final Function valueFunction(final Value data) {
		if(data == null) return Functions.voidFunction();
		return new ValueFunction(data);
	}

	/**
	 * Diese Methode erzeugt eine eine projizierende {@link Function Funktion}, deren {@link Value Ergebniswert} einem der
	 * {@link Value Parameterwerte} des {@link Scope Ausführungskontexts} entspricht und gibt diese zurück.
	 * 
	 * @param index Index des {@link Value Parameterwerts}.
	 * @return {@link ParameterFunction projizierende Funktion}.
	 * @throws IndexOutOfBoundsException Wenn der gegebene Index negativ ist.
	 */
	public static final Function parameterFunctions(final int index) throws IndexOutOfBoundsException {
		if(index < 0) throw new IndexOutOfBoundsException("index out of range: " + index);
		if(index < Functions.PARAMETER_FUNCTIONS.length) return Functions.PARAMETER_FUNCTIONS[index];
		return new ParameterFunction(index);
	}

	/**
	 * Diese Methode erzeugt eine komponierte {@link Function Funktion}, die den Aufruf der gegebenen {@link Function
	 * Funktion} mit den {@link Value Ergebniswerten} der gegebenen {@link Function Parameterfunktionen} als als
	 * {@link Value Parameterwerte} berechnet, und gibt diese zurück.
	 * 
	 * @param function {@link Function Funktion}.
	 * @param functions {@link Function Parameterfunktionen}.
	 * @return {@link CompositeFunction komponierte Funktion}.
	 * @throws NullPointerException Wenn eine der gegebenen {@link Function Funktionen} {@code null} ist.
	 */
	public static final Function compositeFunction(final Function function, final Function... functions)
		throws NullPointerException {
		return new CompositeFunction(function, functions);
	}

	/**
	 * Dieser Konstrukteur ist versteckt und verhindert damit die Erzeugung von Instanzen der Klasse.
	 */
	Functions() {
	}

}
