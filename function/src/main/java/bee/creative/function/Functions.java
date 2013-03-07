package bee.creative.function;

import bee.creative.function.Scopes.AbstractScope.ReturnValue;
import bee.creative.function.Scopes.CompositeScope;
import bee.creative.function.Values.ArrayValue;
import bee.creative.function.Values.NullValue;
import bee.creative.util.Objects;

/**
 * Diese Klasse implementiert Hilfsklassen und Hilfsmethoden zur Erzeugung von {@link Function}{@code s}.
 * 
 * @see Value
 * @see Scopes
 * @see Function
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class Functions {

	/**
	 * Diese Klasse implementiert eine {@link Function}, deren {@link Value Ergebniswert} dem {@link ArrayValue} der {@link Scope#get(int) Parameterwerte} des {@link Scope Ausführungskontexts} entspricht.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ArrayFunction implements Function {

		/**
		 * Dieses Feld speichert die {@link ArrayFunction}.
		 */
		public static final Function INSTANCE = new ArrayFunction();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ArrayValue execute(final Scope scope) {
			final int size = scope.size();
			final Value[] array = new Value[size];
			for(int i = 0; i < size; i++){
				array[i] = scope.get(i);
			}
			return new ArrayValue(0, array);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return 0;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			return (object == this) || (object instanceof ArrayFunction);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this);
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link Function} mit konstantem {@link Value Ergebniswert}.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ValueFunction implements Function {

		/**
		 * Dieses Feld speichert die {@code null}-{@link Function}.
		 */
		static final Function NULL_FUNCTION = new ValueFunction(NullValue.INSTANCE);

		/**
		 * Diese Methode erzeugt eine {@link Function} mit konstantem {@link Value Ergebniswert} und gibt diese zurück.
		 * 
		 * @see Values#valueOf(Object)
		 * @param data {@link Value Ergebniswert}.
		 * @return {@link ValueFunction}.
		 */
		public static Function valueOf(final Object data) {
			if(data == null) return ValueFunction.NULL_FUNCTION;
			return new ValueFunction(Values.valueOf(data));
		}

		/**
		 * Diese Methode erzeugt eine {@link Function} mit konstantem {@link Value Ergebniswert} und gibt diese zurück.
		 * 
		 * @param data {@link Value Ergebniswert}.
		 * @return {@link ValueFunction}.
		 */
		public static Function valueOf(final Value data) {
			if((data == null) || (data.data() == null)) return ValueFunction.NULL_FUNCTION;
			return new ValueFunction(data);
		}

		/**
		 * Dieses Feld speichert den {@link Value Ergebniswert}.
		 */
		final Value value;

		/**
		 * Dieser Konstruktor initialisiert den {@link Value Ergebniswert}.
		 * 
		 * @param value {@link Value}.
		 * @throws NullPointerException Wenn der gegebene {@link Value} {@code null} ist.
		 */
		public ValueFunction(final Value value) throws NullPointerException {
			if(value == null) throw new NullPointerException();
			this.value = value;
		}

		/**
		 * Diese Methode gibt den {@link Value Ergebniswert} zurück.
		 * 
		 * @return {@link Value Ergebniswert}.
		 */
		public Value value() {
			return this.value;
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
			return Objects.hash(this.value);
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
			return Objects.toStringCall(this, this.value);
		}

	}

	/**
	 * Diese Klasse implementiert eine projizierende {@link Function}, deren {@link Value Ergebniswert} einem der {@link Scope#get(int) Parameterwerte} des {@link Scope Ausführungskontexts} entspricht.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ParamFunction implements Function {

		/**
		 * Dieses Feld speichert die projezierenden {@link Function Funktionen} für die Indizes {@code 0} bis {@code 9}.
		 */
		static final Function[] INSTANCES = {new ParamFunction(0), new ParamFunction(1), new ParamFunction(2), new ParamFunction(3), new ParamFunction(4),
			new ParamFunction(5), new ParamFunction(6), new ParamFunction(7), new ParamFunction(8), new ParamFunction(9)};

		/**
		 * Diese Methode erzeugt eine eine projizierende {@link Function}, deren {@link Value Ergebniswert} einem der {@link Scope#get(int) Parameterwerte} des {@link Scope Ausführungskontexts} entspricht und gibt diese zurück.
		 * 
		 * @param index Index des {@link Scope#get(int) Parameterwerts}.
		 * @return {@link ParamFunction}.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index negativ ist.
		 */
		public static Function valueOf(final int index) throws IndexOutOfBoundsException {
			if(index < 0) throw new IndexOutOfBoundsException();
			if(index < ParamFunction.INSTANCES.length) return ParamFunction.INSTANCES[index];
			return new ParamFunction(index);
		}

		/**
		 * Dieses Feld speichert den Index des {@link Scope#get(int) Parameterwerts}.
		 */
		final int index;

		/**
		 * Dieser Konstruktor initialisiert den Index des {@link Scope#get(int) Parameterwerts}.
		 * 
		 * @param index Index des {@link Scope#get(int) Parameterwerts}.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index negativ ist.
		 */
		public ParamFunction(final int index) throws IndexOutOfBoundsException {
			if(index < 0) throw new IndexOutOfBoundsException();
			this.index = index;
		}

		/**
		 * Diese Methode gibt den Index des {@link Scope#get(int) Parameterwerts} zurück.
		 * 
		 * @return Index des {@link Scope#get(int) Parameterwerts}.
		 */
		public int index() {
			return this.index;
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
			if(!(object instanceof ParamFunction)) return false;
			final ParamFunction data = (ParamFunction)object;
			return this.index == data.index;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this.index);
		}

	}

	/**
	 * Diese Klasse definiert eine komponierte {@link Function}, die den Aufruf einer gegebenen {@link Function Funktion} mit den {@link Value Ergebniswerten} mehrerer gegebener {@link Function Parameterfunktionen} als {@link Scope#get(int) Parameterwerte} berechnet.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class CompositeFunction implements Function {

		/**
		 * Diese Methode erzeugt eine {@link Function}, die den Aufruf der gegebenen {@link Function Funktion} mit den {@link Value Ergebniswerten} der gegebenen {@link Function Parameterfunktionen} als {@link Scope#get(int) Parameterwerte} berechnet, und gibt diese zurück.
		 * 
		 * @param function {@link Function Funktion}.
		 * @param functions {@link Function Parameterfunktionen}, deren {@link Value Ergebniswerte} als {@link Scope#get(int) Parameterwerte} verwendet beim Aufruf der {@link Function Funktion} werden sollen.
		 * @return {@link CompositeFunction komponierte Funktion}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public static CompositeFunction valueOf(final Function function, final Function... functions) throws NullPointerException {
			return new CompositeFunction(function, functions);
		}

		/**
		 * Dieses Feld speichert die aufzurufende {@link Function Funktion}.
		 */
		final Function function;

		/**
		 * Dieses Feld speichert die {@link Function Parameterfunktionen}, deren {@link Value Ergebniswerte} als {@link Scope#get(int) Parameterwerte} verwendet werden sollen.
		 */
		final Function[] functions;

		/**
		 * Dieser Konstruktor initialisiert die aufzurufende {@link Function Funktion} und die {@link Function Parameterfunktionen}.
		 * 
		 * @param function {@link Function Funktion}.
		 * @param functions {@link Function Parameterfunktionen}, deren {@link Value Ergebniswerte} als {@link Scope#get(int) Parameterwerte} verwendet beim Aufruf der {@link Function Funktion} werden sollen.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public CompositeFunction(final Function function, final Function... functions) throws NullPointerException {
			if((function == null) || (functions == null)) throw new NullPointerException();
			this.function = function;
			this.functions = functions;
		}

		/**
		 * Diese Methode gibt die aufzurufende {@link Function Funktion} zurück.
		 * 
		 * @return aufzurufende {@link Function Funktion}.
		 */
		public Function function() {
			return this.function;
		}

		/**
		 * Diese Methode gibt eine Kopie der {@link Function Parameterfunktionen} zurück.
		 * 
		 * @return Kopie der {@link Function Parameterfunktionen}.
		 */
		public Function[] functions() {
			return this.functions.clone();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ReturnValue execute(final Scope scope) {
			return new ReturnValue(new CompositeScope(scope, this.functions), this.function);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return Objects.hashEx(this.function, this.functions);
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
			return Objects.toStringCall(true, true, this, new Object[]{"function", this.function, "functions", this.functions});
		}

	}

	/**
	 * Dieser Konstruktor ist versteckt und verhindert damit die Erzeugung von Instanzen der Klasse.
	 */
	Functions() {
	}

}
