package bee.creative.function;

import java.util.Arrays;
import bee.creative.function.Scopes.BaseScope;
import bee.creative.function.Values.BaseValue;
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
	 * @param  Typ des Kontextobjekts.
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
	 * Diese Klasse implementiert eine projezierende {@link Function Funktion}, deren {@link Value Ergebniswert} einem der
	 * {@link Value Parameterwerte} des {@link Scope Ausführungskontexts} entspricht.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param  Typ des Kontextobjekts.
	 */
	public static final class ParamFunction implements Function {

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
			if(!(object instanceof ParamFunction)) return false;
			final ParamFunction data = (ParamFunction)object;
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
	 * Diese Klasse definiert eine verkettete {@link Function Funktion}, die den Aufruf einer gegebenen {@link Function
	 * Funktion} mit den {@link Value Ergebniswerten} mehrerer gegebener {@link Function Parameterfunktionen} berechnet.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param  Typ des Ausführungskontextes.
	 */
	public static final class ChainedFunction implements Function {

		/**
		 * Diese Klasse implementiert den {@link Value Ergebniswert} einer {@link Function Funktion}, die von einem
		 * parametrisierten {@link ExecuteScope Ausführungskontext} aufgerufen wird.
		 * 
		 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 */
		static final class ExecuteValue extends BaseValue {

			/**
			 * Dieses Feld speichert den {@link Value Wert} (Cache).
			 */
			Value value;

			/**
			 * Dieses Feld speichert den parametrisierten {@link ExecuteScope Ausführungskontext} einer {@link Function
			 * Funktion}.
			 */
			ExecuteScope scope;

			/**
			 * Dieser Konstrukteur initialisiert den parametrisierten {@link ExecuteScope Ausführungskontext}.
			 * 
			 * @param scope {@link ExecuteScope Ausführungskontext}
			 */
			public ExecuteValue(final ExecuteScope scope) {
				this.scope = scope;
			}

			/**
			 * Diese Methode gibt den {@link Value Ergebniswert} der vom parametrisierten {@link ExecuteScope
			 * Ausführungskontext} referenzierten {@link Function Funktion} zurück. Dieser {@link Value Wert} wird gepuffert.
			 * 
			 * @return {@link Value Rückgabewert}.
			 */
			public Value value() {
				Value value = this.value;
				if(value != null) return value;
				this.value = (value = this.scope.execute());
				this.scope = null;
				return value;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public int type() {
				return this.value().type();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Object data() {
				return this.value().data();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Value[] arrayData() {
				return this.value().arrayData();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public String stringData() {
				return this.value().stringData();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Number numberData() {
				return this.value().numberData();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Boolean booleanData() {
				return this.value().booleanData();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public int hashCode() {
				return this.value().hashCode();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public String toString() {
				return Objects.toStringCall("executeValue", this.value, this.scope);
			}

		}

		/**
		 * Diese Klasse implementiert einen parametrisierten {@link Scope Ausführungskontext}, welcher die mit ihm
		 * aufzurufende {@link Function Funktion} kennt und wessen {@link Value Parameterwerte} mit Hilfe eines gegebenen
		 * {@link Scope Ausführungskontexts} und gegebener {@link Function Parameterfunktionen} ermittelt werden. Die
		 * ermittelten {@link Value Parameterwerte} werden hierbei gepuffert.
		 * 
		 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param  Typ des Quellobjekts.
		 */
		static final class ExecuteScope extends BaseScope {

			/**
			 * Dieses Feld speichert den aufrufenden {@link Scope Ausführungskontext}, der für die {@link Function
			 * Parameterfunktionen} genutzt wird.
			 */
			final Scope scope;

			/**
			 * Dieses Feld speichert die {@link Value Parameterwerte} (Cache).
			 */
			final Value[] values;

			/**
			 * Dieses Feld speichert die mit diesem {@link Scope Ausführungskontext} aufzurufende {@link Function Funktion}.
			 */
			final Function function;

			/**
			 * Dieses Feld speichert die {@link Function Parameterfunktionen}, deren {@link Value Ergebniswerte} als
			 * {@link Value Parameterwerte} verwendet werden sollen.
			 */
			final Function[] functions;

			/**
			 * Dieser Konstrukteur initialisiert die {@link Function Parameterfunktionen}, deren {@link Value Ergebniswerte}
			 * als {@link Value Parameterwerte} verwendet werden, den {@link Scope Ausführungskontext} für diese
			 * {@link Function Parameterfunktionen} sowie die mit diesem {@link Scope Ausführungskontext} aufzurufende
			 * {@link Function Funktion}.
			 * 
			 * @param scope {@link Scope Ausführungskontext} der {@link Function Parameterfunktionen}.
			 * @param function {@link Function Funktion}.
			 * @param functions {@link Function Parameterfunktionen}.
			 */
			public ExecuteScope(final Scope scope, final Function function,
				final Function[] functions) {
				this.scope = scope;
				this.values = new Value[functions.length];
				this.function = function;
				this.functions = functions.clone();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public int size() {
				return this.values.length;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Value get(final int index) throws IndexOutOfBoundsException {
				if(index < 0) throw new IndexOutOfBoundsException("index out of range: " + index);
				final Value[] values = this.values;
				if(index >= values.length) throw new IndexOutOfBoundsException("index out of range: " + index);
				Value value = values[index];
				if(value != null) return value;
				values[index] = (value = Values.value(this.functions[index].execute(this.scope)));
				this.functions[index] = null;
				return value;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Object context() {
				return this.scope.context();
			}

			/**
			 * Diese Methode ruft die {@link Function#execute(Scope) Berechnungsmethode} der {@link Function Funktion} mit
			 * diesem {@link Scope Ausführungskontext} auf und gibt deren {@link Value Rückgabewert} zurück.
			 * 
			 * @return {@link Value Rückgabewert} der {@link Function Funktion}.
			 */
			public Value execute() {
				return Values.value(this.function.execute(this));
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public String toString() {
				return Objects.toStringCall("executeScope", this.function, this.functions, this.values);
			}

		}

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
		public ChainedFunction(final Function function, final Function... functions)
			throws NullPointerException {
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
			return new ExecuteValue(new ExecuteScope(scope, this.function, this.functions));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return this.function.hashCode() + Arrays.hashCode(this.functions);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof ChainedFunction)) return false;
			final ChainedFunction data = (ChainedFunction)object;
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
	static final Function[] PARAM_FUNCTIONS = {new ParamFunction(0), new ParamFunction(1),
		new ParamFunction(2), new ParamFunction(3), new ParamFunction(4),
		new ParamFunction(5), new ParamFunction(6), new ParamFunction(7),
		new ParamFunction(8), new ParamFunction(9)};

	/**
	 * Diese Methode gibt die leere {@link Function Funktion} zurück, welche immer den {@link Values#voidValue() leeren
	 * Wert} als {@link Value Ergebniswert} liefert.
	 * 
	 * @see Values#voidValue()
	 * @param  Typ des Kontextobjekts.
	 * @return {@link Function Funktion}.
	 */
	@SuppressWarnings ("unchecked")
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
	 * @param  Typ des Kontextobjekts.
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
	 * Diese Methode erzeugt eine eine projezierende {@link Function Funktion}, deren {@link Value Ergebniswert} einem der
	 * {@link Value Parameterwerte} des {@link Scope Ausführungskontexts} entspricht und gibt diese zurück.
	 * 
	 * @param  Typ des Kontextobjekts.
	 * @param index Index des {@link Value Parameterwerts}.
	 * @return {@link ParamFunction Param-Funktion}.
	 * @throws IndexOutOfBoundsException Wenn der gegebene Index negativ ist.
	 */
	@SuppressWarnings ("unchecked")
	public static final  Function paramFunctions(final int index) throws IndexOutOfBoundsException {
		if(index < 0) throw new IndexOutOfBoundsException("index out of range: " + index);
		if(index < Functions.PARAM_FUNCTIONS.length) return (Function)Functions.PARAM_FUNCTIONS[index];
		return new ParamFunction(index);
	}

	/**
	 * Diese Methode erzeugt eine verkettete {@link Function Funktion}, die den Aufruf der gegebenen {@link Function
	 * Funktion} mit den {@link Value Ergebniswerten} der gegebenen {@link Function Parameterfunktionen} als Parameter
	 * berechnet, und gibt diese zurück.
	 * 
	 * @param  Typ des Kontextobjekts.
	 * @param function {@link Function Funktion}.
	 * @param functions {@link Function Parameterfunktionen}.
	 * @return {@link ChainedFunction Chained-Funktion}.
	 * @throws NullPointerException Wenn eine der gegebenen {@link Function Funktionen} {@code null} ist.
	 */
	public static final  Function chainedFunction(final Function  function,
		final Function... functions) throws NullPointerException {
		return new ChainedFunction(function, functions);
	}

	/**
	 * Dieser Konstrukteur ist versteckt und verhindert damit die Erzeugung von Instanzen der Klasse.
	 */
	Functions() {
	}

}
