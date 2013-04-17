package bee.creative.function;

import java.util.Iterator;
import bee.creative.function.Functions.CompositeFunction;
import bee.creative.function.Functions.ParamFunction;
import bee.creative.function.Functions.ValueFunction;
import bee.creative.function.Types.ArrayType;
import bee.creative.function.Values.ArrayValue;
import bee.creative.util.Iterators;
import bee.creative.util.Objects;
import bee.creative.util.Objects.UseToString;

/**
 * Diese Klasse implementiert Hilfsmethoden und Hilfsklassen zur Verarbeitung von {@link Scope Ausführungskontexten}.
 * 
 * @see Value
 * @see Scope
 * @see Function
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class Scopes {

	/**
	 * Diese Klasse implementiert einen abstrakten {@link Scope Ausführungskontext}.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static abstract class AbstractScope implements Scope, UseToString {

		/**
		 * Dieses Feld speichert die {@link Value Parameterwerte}.
		 */
		protected Value[] values;

		/**
		 * Diese Methode gibt eine Kopie der {@link Value Parameterwerte} zurück.
		 * 
		 * @return Kopie der {@link Value Parameterwerte}.
		 */
		protected Value[] values() {
			return this.values.clone();
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
		public Iterator<Value> iterator() {
			if(this.size() == 0) return Iterators.voidIterator();
			return new Iterator<Value>() {

				int size = AbstractScope.this.size();

				int index = 0;

				@Override
				public boolean hasNext() {
					return this.index < this.size;
				}

				@Override
				public Value next() {
					return AbstractScope.this.get(this.index++);
				}

				@Override
				public void remove() {
					throw new UnsupportedOperationException();
				}

			};
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Value execute(final Function function, final Value... values) throws NullPointerException {
			return function.execute(new ExecuteScope(this, values));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Value execute(final Object context, final Function function, final Value... values) throws NullPointerException {
			return function.execute(new ExecuteScope(this, context, values));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			int hash = 0x811C9DC5;
			for(int i = 0, size = this.size(); i < size; i++){
				hash = (hash * 0x01000193) ^ Objects.hash(this.get(i));
			}
			return hash;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof Scope)) return false;
			final Scope data = (Scope)object;
			final int size = this.size();
			if(data.size() != size) return false;
			for(int i = 0; i < size; i++)
				if(!Objects.equals(this.get(i), data.get(i))) return false;
			return true;
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
	 * Diese Klasse implementiert einen {@link Scope Ausführungskontext} zum Aufruf einer {@link Function Funktion} mit gegebenem Kontextobjekt und gegebenen {@link Value Parameterwerten}.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class DefaultScope extends AbstractScope {

		/**
		 * Dieses Feld speichert den leeren {@link Scope Ausführungskontext} ohne {@link Value Parameterwerte} und mit {@code null} als Kontextobjekt.
		 */
		public static final DefaultScope INSTANCE = new DefaultScope(null, ArrayType.NULL_DATA);

		/**
		 * Dieses Feld speichert das Kontextobjekt.
		 */
		final Object context;

		/**
		 * Dieser Konstruktor initialisiert Kontextobjekt und {@link Value Parameterwerte}.
		 * 
		 * @see ArrayValue#valueOf(Object[])
		 * @param context Kontextobjekt.
		 * @param values {@link Value Parameterwerte}.
		 * @throws NullPointerException Wenn die gegebenen {@link Value Parameterwerte} {@code null} sind.
		 */
		public DefaultScope(final Object context, final Object... values) throws NullPointerException {
			this.values = ArrayValue.valueOf(values).data();
			this.context = context;
		}

		/**
		 * Dieser Konstruktor initialisiert Kontextobjekt und {@link Value Parameterwerte}.
		 * 
		 * @param context Kontextobjekt.
		 * @param values {@link Value Parameterwerte}.
		 * @throws NullPointerException Wenn die gegebenen {@link Value Parameterwerte} {@code null} sind.
		 */
		public DefaultScope(final Object context, final Value[] values) throws NullPointerException {
			if(values == null) throw new NullPointerException();
			this.values = values;
			this.context = context;
		}

		/**
		 * Diese Methode gibt den {@code index}-ten {@link Value Parameterwert} zurück.
		 * 
		 * @throws NullPointerException Wenn der {@code index}-te {@link Value Parameterwert} {@code null} ist.
		 */
		@Override
		public Value get(final int index) throws NullPointerException, IndexOutOfBoundsException {
			final Value value = this.values[index];
			if(value == null) throw new NullPointerException();
			return value;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Value[] values() {
			return super.values();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object context() {
			return this.context;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(true, true, this, new Object[]{"context", this.context, "values", this.values});
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Scope Ausführungskontext} zum Aufruf einer {@link Function Funktion} mit gegebenem Kontextobjekt sowie gegebenen {@link Value Parameterwerten} im Kontext eines übergeordneten {@link Scope Ausführungskontexts}
	 * 
	 * @see Scope#execute(Function, Value...)
	 * @see Scope#execute(Object, Function, Value...)
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ExecuteScope extends AbstractScope {

		/**
		 * Dieses Feld speichert den übergeordneten {@link Scope Ausführungskontext}, dessen erste {@link Value Parameterwerte} virtuell ersetzt werden.
		 */
		final Scope scope;

		/**
		 * Dieses Feld speichert das Kontextobjekt.
		 */
		final Object context;

		/**
		 * Dieser Konstruktor initialisiert den übergeordneten {@link Scope Ausführungskontext} und die {@link Value Parameterwerte}. Das Kontextobjekt des gegebenen {@link Scope Ausführungskontext} wird übernommen
		 * 
		 * @see Scope#execute(Function, Value...)
		 * @see Scope#execute(Object, Function, Value...)
		 * @param scope übergeordneter {@link Scope Ausführungskontext}.
		 * @param values {@link Value Parameterwerte}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public ExecuteScope(final Scope scope, final Value[] values) throws NullPointerException {
			if((scope == null) || (values == null)) throw new NullPointerException();
			this.scope = scope;
			this.values = values;
			this.context = scope.context();
		}

		/**
		 * Dieser Konstruktor initialisiert den übergeordneten {@link Scope Ausführungskontext}, das Kontextobjekt und die {@link Value Parameterwerte}.
		 * 
		 * @see Scope#execute(Function, Value...)
		 * @see Scope#execute(Object, Function, Value...)
		 * @param scope übergeordneter {@link Scope Ausführungskontext}.
		 * @param context Kontextobjekt.
		 * @param values {@link Value Parameterwerte}.
		 * @throws NullPointerException Wenn der {@link Scope Ausführungskontext} oder die {@link Value Parameterwerte} {@code null} sind.
		 */
		public ExecuteScope(final Scope scope, final Object context, final Value[] values) throws NullPointerException {
			if((scope == null) || (values == null)) throw new NullPointerException();
			this.scope = scope;
			this.values = values;
			this.context = context;
		}

		/**
		 * {@inheritDoc} Diese entsprechen hierbei dem zusätzlichen {@link Value Parameterwerten} des übergeordneten {@link Scope Ausführungskontext}, die über {@code this.scope().get(index - this.size() + this.scope.size())} ermittelt werden.
		 * 
		 * @see #scope()
		 * @throws NullPointerException Wenn der {@code index}-te {@link Value Parameterwert} {@code null} ist.
		 */
		@Override
		public Value get(final int index) throws NullPointerException, IndexOutOfBoundsException {
			final Value[] values = this.values;
			final int length = values.length;
			if(index >= length){
				final Scope scope = this.scope;
				return scope.get((index - length) + scope.size());
			}
			final Value value = values[index];
			if(value == null) throw new NullPointerException();
			return value;
		}

		/**
		 * Diese Methode gibt den übergeordneten {@link Scope Ausführungskontext} zurück, dessen zusätzliche {@link Value Parameterwerte} übernommen werden.
		 * 
		 * @return übergeordneter {@link Scope Ausführungskontext}.
		 */
		public Scope scope() {
			return this.scope;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object context() {
			return this.context;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Value[] values() {
			return super.values();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			if(this.scope.context() == this.context) return Objects.toStringCall(true, true, this, new Object[]{"scope", this.scope, "values", this.values});
			return Objects.toStringCall(true, true, this, new Object[]{"scope", this.scope, "context", this.context, "values", this.values});
		}

	}

	/**
	 * Diese Klasse implementiert den {@link Scope Ausführungskontext} einer {@link CompositeFunction Komposition}, deren {@link Value Parameterwerte} mit Hilfe eines gegebenen {@link Scope Ausführungskontexts} und gegebener {@link Function Parameterfunktionen} ermittelt werden. Die {@link Function Parameterfunktionen} werden zur Ermittlung der {@link Value Parameterwerte} einmalig mit dem gegebenen {@link Scope Ausführungskontext} aufgerufen und zur Wiederverwendung zwischengespeichert.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class CompositeScope extends AbstractScope {

		/**
		 * Diese Klasse implementiert den {@link Value Ergebniswert} einer {@link Function Funktion} mit {@code call-by-reference}-Semantik, welcher eine gegebene {@link Function Funktion} erst dann mit einem gegebenen {@link Scope Ausführungskontext} einmalig auswertet, wenn {@link #type() Datentyp} oder {@link #data() Datensatz} gelesen werden. Der von der {@link Function Funktion} berechnete {@link Value Ergebniswert} wird zur schnellen Wiederverwendung zwischengespeichert. Nach der einmaligen Auswertung der {@link Function Funktion} werden die Verweise auf {@link Scope Ausführungskontext} und {@link Function Funktion} aufgelöst.
		 * 
		 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 */
		public static final class ReturnValue implements Value {

			/**
			 * Dieses Feld speichert das von der {@link Function Funktion} berechnete Ergebnis oder {@code null}.
			 * 
			 * @see Function#execute(Scope)
			 */
			Value value;

			/**
			 * Dieses Feld speichert den {@link Scope Ausführungskontext} zum Aufruf der {@link Function Funktion} oder {@code null}.
			 * 
			 * @see Function#execute(Scope)
			 */
			Scope scope;

			/**
			 * Dieses Feld speichert die {@link Function Funktion} oder {@code null}.
			 * 
			 * @see Function#execute(Scope)
			 */
			Function function;

			/**
			 * Dieser Konstruktor initialisiert {@link Scope Ausführungskontext} und {@link Function Funktion}.
			 * 
			 * @param scope {@link Scope Ausführungskontext}.
			 * @param function {@link Function Funktion}.
			 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
			 */
			public ReturnValue(final Scope scope, final Function function) throws NullPointerException {
				if((scope == null) || (function == null)) throw new NullPointerException();
				this.scope = scope;
				this.function = function;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Type<?> type() {
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
			public <GData> GData dataAs(final Type<GData> type) throws NullPointerException, ClassCastException {
				return this.value().dataAs(type);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public <GData> GData dataTo(final Type<GData> type) throws NullPointerException, IllegalArgumentException {
				return this.value().dataTo(type);
			}

			/**
			 * Diese Methode gibt den {@link Value Ergebniswert} der Ausführung der {@link Function Funktion} mit dem {@link Scope Ausführungskontext} zurück.
			 * 
			 * @see Function#execute(Scope)
			 * @return {@link Value Ergebniswert}.
			 * @throws NullPointerException Wenn der berechnete {@link Value Ergebniswert} {@code null} ist.
			 */
			public Value value() throws NullPointerException {
				Value value = this.value;
				if(value != null) return value;
				value = this.function.execute(this.scope);
				if(value == null) throw new NullPointerException();
				this.value = value;
				this.scope = null;
				this.function = null;
				return value;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Value valueTo(final Type<?> type) throws NullPointerException, IllegalArgumentException {
				return this.value().valueTo(type);
			}

			/**
			 * Diese Methode gibt den {@link Scope Ausführungskontext} oder {@code null} zurück. Der erste Aufruf von {@link #value()} setzt den {@link Scope Ausführungskontext} auf {@code null}.
			 * 
			 * @return {@link Scope Ausführungskontext} oder {@code null}.
			 */
			public Scope scope() {
				return this.scope;
			}

			/**
			 * Diese Methode gibt die {@link Function Funktion} oder {@code null} zurück. Der erste Aufruf von {@link #value()} setzt die {@link Function Funktion} auf {@code null}.
			 * 
			 * @return {@link Function Funktion} oder {@code null}.
			 */
			public Function function() {
				return this.function;
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
			public boolean equals(final Object object) {
				if(object == this) return true;
				if(!(object instanceof Value)) return false;
				return this.value().equals(object);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public String toString() {
				if(this.value != null) return Objects.toStringCall(true, true, this, new Object[]{"value", this.value});
				return Objects.toStringCall(true, true, this, new Object[]{"scope", this.scope, "function", this.function});
			}

		}

		/**
		 * Dieses Feld speichert den übergeordneten {@link Scope Ausführungskontext}, der für die {@link Function Parameterfunktionen} genutzt wird.
		 */
		final Scope scope;

		/**
		 * Dieses Feld speichert die {@link Function Parameterfunktionen}, deren {@link Value Ergebniswerte} als {@link Value Parameterwerte} verwendet werden.
		 */
		final Function[] functions;

		/**
		 * Dieser Konstruktor initialisiert den übergeordneten {@link Scope Ausführungskontext} und die {@link Function Parameterfunktionen}.
		 * 
		 * @param scope übergeordneter {@link Scope Ausführungskontext}.
		 * @param functions {@link Function Parameterfunktionen}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public CompositeScope(final Scope scope, final Function... functions) throws NullPointerException {
			if((scope == null) || (functions == null)) throw new NullPointerException();
			this.scope = scope;
			this.values = new Value[functions.length];
			this.functions = functions;
		}

		/**
		 * {@inheritDoc} Diese entsprechen hierbei den {@link Value Parameterwerten} des übergeordneten {@link Scope Ausführungskontext}, die über {@code this.scope().get(index - this.size())} ermittelt werden.
		 * 
		 * @see #scope()
		 * @throws NullPointerException Wenn die {@code index}-te {@link Function Parameterfunktion} {@code null} ist.
		 */
		@Override
		public Value get(final int index) throws NullPointerException, IndexOutOfBoundsException {
			final Value[] values = this.values;
			final int length = values.length;
			if(index >= length) return this.scope.get(index - length);
			Value value = values[index];
			if(value != null) return value;
			final Function function = this.functions[index];
			final Object functionClass = function.getClass();
			if(functionClass == ValueFunction.class) return values[index] = function.execute(this.scope);
			if(functionClass != ParamFunction.class) return values[index] = new ReturnValue(this.scope, function);
			value = function.execute(this.scope);
			if(value == null) throw new NullPointerException();
			return values[index] = value;
		}

		/**
		 * Diese Methode gibt den {@link Scope Ausführungskontext} der {@link Function Parameterfunktionen} zurück.
		 * 
		 * @return {@link Scope Ausführungskontext} der {@link Function Parameterfunktionen}.
		 */
		public Scope scope() {
			return this.scope;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object context() {
			return this.scope.context();
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
		public String toString() {
			return Objects.toStringCall(true, true, this, new Object[]{"scope", this.scope, "functions", this.functions, "values", this.values});
		}

	}

	/**
	 * Dieser Konstruktor ist versteckt und verhindert damit die Erzeugung von Instanzen der Klasse.
	 */
	Scopes() {
	}

}
