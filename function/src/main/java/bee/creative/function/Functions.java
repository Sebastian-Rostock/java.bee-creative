package bee.creative.function;

import bee.creative.function.Scopes.AbstractScope.ReturnValue;
import bee.creative.function.Scopes.CompositeScope;
import bee.creative.function.Values.ArrayValue;
import bee.creative.function.Values.NullValue;
import bee.creative.function.Values.ObjectValue;
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
	 * Diese Klasse implementiert die leere {@link Function Funktion}, deren {@link Value Ergebniswert} immer {@link NullValue#INSTANCE} ist.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class NullFunction implements Function {

		/**
		 * Dieses Feld speichert die {@link NullFunction}.
		 */
		public static final Function INSTANCE = new NullFunction();

		/**
		 * Diese Methode gibt die gegebene {@link Function} oder {@link NullFunction#INSTANCE} zurück. Wenn die Eingabe {@code null} ist, wird {@link NullFunction#INSTANCE} zurück gegeben.
		 * 
		 * @param value {@link Function} oder {@code null}.
		 * @return {@link Function}.
		 */
		public static Function valueOf(final Function value) {
			if(value == null) return INSTANCE;
			return value;
		}

		/**
		 * Dieser Konstrukteur ist versteckt und verhindert damit die Erzeugung von Instanzen der Klasse.
		 */
		NullFunction() {
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Value execute(final Scope scope) {
			return NullValue.INSTANCE;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("NullFunction");
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link Function Funktion}, deren {@link Value Ergebniswert} dem {@link ArrayValue} der {@link Value Parameterwerte} des {@link Scope Ausführungskontexts} entspricht.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ArrayFunction implements Function {

		/**
		 * Dieses Feld speichert die {@link ArrayFunction}.
		 */
		public static final Function INSTANCE = new ArrayFunction();

		/**
		 * Dieser Konstrukteur ist versteckt und verhindert damit die Erzeugung von Instanzen der Klasse.
		 */
		ArrayFunction() {
		}

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
			return ArrayValue.valueOf(array);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("ArrayFunction");
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link Function} mit konstantem {@link Value Ergebniswert}.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ValueFunction implements Function {

		/**
		 * Diese Methode erzeugt eine {@link Function Funktion} mit konstantem {@link Value Ergebniswert} und gibt diese zurück. Wenn die Eingabe {@code null} ist, wird {@link NullFunction#INSTANCE} zurück gegeben.
		 * 
		 * @see ObjectValue#valueOf(Object)
		 * @param data {@link Value Ergebniswert}.
		 * @return {@link Functions.ValueFunction Wert-Funktion}.
		 */
		public static Function valueOf(final Object data) {
			if(data == null) return NullFunction.INSTANCE;
			return new ValueFunction(ObjectValue.valueOf(data));
		}

		/**
		 * Diese Methode erzeugt eine {@link Function Funktion} mit konstantem {@link Value Ergebniswert} und gibt diese zurück. Wenn die Eingabe {@code null} ist, wird {@link NullFunction#INSTANCE} zurück gegeben.
		 * 
		 * @param data {@link Value Ergebniswert}.
		 * @return {@link Functions.ValueFunction Wert-Funktion}.
		 */
		public static Function valueOf(final Value data) {
			if(data == null) return NullFunction.INSTANCE;
			return new ValueFunction(data);
		}

		/**
		 * Dieses Feld speichert den {@link Value Ergebniswert}.
		 */
		final Value value;

		/**
		 * Dieser Konstrukteur initialisiert den {@link Value Ergebniswert}.
		 * 
		 * @param value {@link Value}.
		 * @throws NullPointerException Wenn der gegebene {@link Value} {@code null} ist.
		 */
		public ValueFunction(final Value value) throws NullPointerException {
			if(value == null) throw new NullPointerException();
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
			return Objects.toStringCall("ValueFunction", this.value);
		}

	}

	/**
	 * Diese Klasse implementiert eine projizierende {@link Function Funktion}, deren {@link Value Ergebniswert} einem der {@link Value Parameterwerte} des {@link Scope Ausführungskontexts} entspricht.
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
		 * Diese Methode erzeugt eine eine projizierende {@link Function Funktion}, deren {@link Value Ergebniswert} einem der {@link Value Parameterwerte} des {@link Scope Ausführungskontexts} entspricht und gibt diese zurück.
		 * 
		 * @param index Index des {@link Value Parameterwerts}.
		 * @return {@link ParamFunction projizierende Funktion}.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index negativ ist.
		 */
		public static Function valueOf(final int index) throws IndexOutOfBoundsException {
			if(index < 0) throw new IndexOutOfBoundsException();
			if(index < INSTANCES.length) return INSTANCES[index];
			return new ParamFunction(index);
		}

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
		public ParamFunction(final int index) throws IndexOutOfBoundsException {
			if(index < 0) throw new IndexOutOfBoundsException();
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
			if(!(object instanceof ParamFunction)) return false;
			final ParamFunction data = (ParamFunction)object;
			return this.index == data.index;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("ParamFunction", this.index);
		}

	}

	/**
	 * Diese Klasse definiert eine komponierte {@link Function Funktion}, die den Aufruf einer gegebenen {@link Function Funktion} mit den {@link Value Ergebniswerten} mehrerer gegebener {@link Function Parameterfunktionen} als {@link Value Parameterwerte} berechnet.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class CompositeFunction implements Function {

		/**
		 * Dieses Feld speichert die aufzurufende {@link Function Funktion}.
		 */
		final Function function;

		/**
		 * Dieses Feld speichert die {@link Function Parameterfunktionen}, deren {@link Value Ergebniswerte} als {@link Value Parameterwerte} verwendet werden sollen.
		 */
		final Function[] functions;

		/**
		 * Dieser Konstrukteur initialisiert die aufzurufende {@link Function Funktion} und die {@link Function Parameterfunktionen}.
		 * 
		 * @param function {@link Function Funktion}.
		 * @param functions {@link Function Parameterfunktionen}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public CompositeFunction(final Function function, final Function... functions) throws NullPointerException {
			if(function == null || functions == null) throw new NullPointerException();
			this.function = function;
			this.functions = functions;
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
			return Objects.equals(this.function, data.function) && Objects.equalsEx(this.functions, data.functions);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(true, true, "CompositeFunction", "function", this.function, "functions", this.functions);
		}

	}

	/**
	 * Dieser Konstrukteur ist versteckt und verhindert damit die Erzeugung von Instanzen der Klasse.
	 */
	Functions() {
	}

}
