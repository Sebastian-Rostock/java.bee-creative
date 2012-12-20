package bee.creative.function;

import java.util.Arrays;
import java.util.Iterator;
import bee.creative.function.Functions.CompositeFunction;
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
		 * Diese Klasse implementiert den {@link Value Ergebniswert} einer {@link Function Funktion} mit {@code call-by-reference}-Semantik, welcher eine gegebene {@link Function Funktion} erst dann mit einem gegebenen {@link Scope Ausführungskontext} einmalig auswertet, wenn {@link #type() Datentyp} oder {@link #data() Datensatz} gelesen werden.
		 * <p>
		 * Der von der {@link Function Funktion} berechnete {@link Value Ergebniswert} wird zur schnellen Wiederverwendung gepuffert. Nach der einmaligen Auswertung der {@link Function Funktion} werden die Verweise auf {@link Scope Ausführungskontext} und {@link Function Funktion} aufgelöst.
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
			 * Dieser Konstrukteur initialisiert {@link Scope Ausführungskontext} und {@link Function Funktion}.
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
			 * {@inheritDoc}
			 */
			@Override
			public Value valueTo(final Type<?> type) throws NullPointerException, IllegalArgumentException {
				return this.value().valueTo(type);
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
				return Objects.toStringCall(true, true, "ReturnValue", "value", this.value, "scope", this.scope, "function", this.function);
			}

		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final Iterator<Value> iterator() {
			if(this.size() == 0) return Iterators.voidIterator();
			return new Iterator<Value>() {

				int index = 0;

				@Override
				public boolean hasNext() {
					return this.index < AbstractScope.this.size();
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
		public final ReturnValue execute(final Function function, final int deleteCount, final Value... insertValues) {
			return this.execute(this.context(), function, deleteCount, insertValues);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final ReturnValue execute(final Object context, final Function function, final int deleteCount, final Value... insertValues)
			throws NullPointerException, IllegalArgumentException {
			return new ReturnValue(new ExecuteScope(this, context, deleteCount, insertValues), function);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final int hashCode() {
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
		public final boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof Scope)) return false;
			final Scope data = (Scope)object;
			final int size = this.size();
			if(data.size() != size) return false;
			for(int i = 0; i < size; i++)
				if(!Objects.equals(this.get(i), data.get(i))) return false;
			return true;
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Scope Ausführungskontext} mit Kontextobjekt und {@link Value Parameterwerten}.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class DefaultScope extends AbstractScope {

		/**
		 * Dieses Feld speichert den leeren {@link Scope Ausführungskontext} ohne {@link Value Parameterwerte} und mit {@code null} als Kontextobjekt.
		 */
		public static final DefaultScope INSTANCE = new DefaultScope(null, ArrayType.NULL_DATA);

		/**
		 * Dieses Feld speichert die {@link Value Parameterwerte}.
		 */
		final Value[] values;

		/**
		 * Dieses Feld speichert das Kontextobjekt.
		 */
		final Object context;

		/**
		 * Dieser Konstrukteur initialisiert Kontextobjekt und {@link Value Parameterwerte}.
		 * 
		 * @see ArrayValue#valueOf(Object...)
		 * @param context Kontextobjekt.
		 * @param values {@link Value Parameterwerte}.
		 */
		public DefaultScope(final Object context, final Object... values) {
			this.values = ArrayValue.valueOf(values).data();
			this.context = context;
		}

		/**
		 * Dieser Konstrukteur initialisiert Kontextobjekt und {@link Value Parameterwerte}.
		 * 
		 * @param context Kontextobjekt.
		 * @param values {@link Value Parameterwerte}.
		 * @throws NullPointerException Wenn einer der gegebenen {@link Value Parameterwerte} {@code null} ist.
		 */
		public DefaultScope(final Object context, final Value[] values) throws NullPointerException {
			if(Arrays.asList(values).contains(null)) throw new NullPointerException();
			this.values = values;
			this.context = context;
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
			return this.values[index];
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
			return Objects.toStringCall(true, true, "DefaultScope", "context", this.context, "values", this.values);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link Scope Ausführungskontext} zum Aufruf einer {@link Function Funktion} mit einer Liste von {@link Value Parameterwerten}, die durch das virtuelle Ersetzen der ersen {@link Value Parameterwerte} eines gegebenen {@link Scope Ausführungskontexts} mit gegebenen {@link Value Parameterwerten} entsteht.
	 * 
	 * @see Scope#execute(Function, int, Value...)
	 * @see Scope#execute(Object, Function, int, Value...)
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ExecuteScope extends AbstractScope {

		/**
		 * Dieses Feld speichert den aufrufenden {@link Scope Ausführungskontext}, dessen erste {@link Value Parameterwerte} virtuell ersetzt werden.
		 */
		final Scope scope;

		/**
		 * Dieses Feld speichert das Kontextobjekt.
		 */
		final Object context;

		/**
		 * Anzahl der virtuel zu entfernenden {@link Value Parameterwerte} des aufrufenden {@link Scope Ausführungskontexts} .
		 */
		final int deleteCount;

		/**
		 * Dieses Feld speichert ersten {@link Value Parameterwerte}.
		 */
		final Value[] insertValues;

		/**
		 * Dieser Konstrukteur initialisiert den aufrufenden {@link Scope Ausführungskontext}, das Kontextobjekt, die Anzahl der virtuel zu entfernenden {@link Value Parameterwerte} und das Array der ersten {@link Value Parameterwerte}.
		 * 
		 * @see Scope#execute(Function, int, Value...)
		 * @see Scope#execute(Object, Function, int, Value...)
		 * @param scope aufrufender {@link Scope Ausführungskontext}.
		 * @param context Kontextobjekt.
		 * @param deleteCount Anzahl der virtuel zu entfernenden {@link Value Parameterwerte} des gegebenen {@link Scope Ausführungskontexts}.
		 * @param insertValues Array der ersten {@link Value Parameterwerte}.
		 * @throws NullPointerException Wenn der gegebene {@link Scope Ausführungskontext}, die gegebene {@link Function Funktion} bzw. einer der gegebenen {@link Value Parameterwerte} {@code null} ist.
		 * @throws IllegalArgumentException Wenn die gegebene Anzahl ungültig ist.
		 */
		public ExecuteScope(final Scope scope, final Object context, final int deleteCount, final Value... insertValues) throws NullPointerException,
			IllegalArgumentException {
			if(scope == null || Arrays.asList(insertValues).contains(null)) throw new NullPointerException();
			if(deleteCount < 0 || deleteCount > scope.size()) throw new IllegalArgumentException();
			this.scope = scope;
			this.context = context;
			this.deleteCount = deleteCount;
			this.insertValues = insertValues;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int size() {
			return (this.scope.size() + this.insertValues.length) - this.deleteCount;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Value get(final int index) throws IndexOutOfBoundsException {
			final Value[] insertValues = this.insertValues;
			final int length = insertValues.length;
			if(index < length) return insertValues[index];
			return this.scope.get((index + this.deleteCount) - length);
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
			return Objects.toStringCall(true, true, "ExecuteScope", "scope", this.scope, "context", this.context, "deleteCount", this.deleteCount, "insertValues",
				this.insertValues);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link Scope Ausführungskontext} einer {@link CompositeFunction Komposition}, deren {@link Value Parameterwerte} mit Hilfe eines gegebenen {@link Scope Ausführungskontexts} und gegebener {@link Function Parameterfunktionen} ermittelt werden.
	 * <p>
	 * Die {@link Function Parameterfunktionen} werden zur Ermittlung der {@link Value Parameterwerte} einmalig mit dem gegebenen {@link Scope Ausführungskontext} aufgerufen. Die {@link Value Parameterwerte} werden zur schnellen Wiederverwendung gepuffert.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class CompositeScope extends AbstractScope {

		/**
		 * Dieses Feld speichert den aufrufenden {@link Scope Ausführungskontext}, der für die {@link Function Parameterfunktionen} genutzt wird.
		 */
		final Scope scope;

		/**
		 * Dieses Feld speichert die {@link Value Parameterwerte} als Ergebnisse der {@link Function Parameterfunktionen}.
		 */
		final ReturnValue[] values;

		/**
		 * Dieses Feld speichert die {@link Function Parameterfunktionen}, deren {@link Value Ergebniswerte} als {@link Value Parameterwerte} verwendet werden.
		 */
		final Function[] functions;

		/**
		 * Dieser Konstrukteur initialisiert {@link Scope Ausführungskontext} und {@link Function Parameterfunktionen}.
		 * 
		 * @param scope {@link Scope Ausführungskontext} der {@link Function Parameterfunktionen}.
		 * @param functions {@link Function Parameterfunktionen}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public CompositeScope(final Scope scope, final Function... functions) throws NullPointerException {
			if((scope == null) || Arrays.asList(functions).contains(null)) throw new NullPointerException();
			this.scope = scope;
			this.values = new ReturnValue[functions.length];
			this.functions = functions;
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
		public ReturnValue get(final int index) throws IndexOutOfBoundsException {
			final ReturnValue[] values = this.values;
			ReturnValue value = values[index];
			if(value != null) return value;
			value = new AbstractScope.ReturnValue(this.scope, this.functions[index]);
			values[index] = value;
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
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(true, true, "CompositeScope", "scope", this.scope, "functions", this.functions, "values", this.values);
		}

	}

	/**
	 * Dieser Konstrukteur ist versteckt und verhindert damit die Erzeugung von Instanzen der Klasse.
	 */
	Scopes() {
	}

}
