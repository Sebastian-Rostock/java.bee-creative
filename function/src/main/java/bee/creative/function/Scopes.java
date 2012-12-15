package bee.creative.function;

import java.util.Arrays;
import java.util.Iterator;
import bee.creative.function.Functions.CompositeFunction;
import bee.creative.function.Types.ArrayType;
import bee.creative.function.Values.ArrayValue;
import bee.creative.function.Values.ReturnValue;
import bee.creative.function.Values.NullValue;
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
		public final Value execute(final Function function, final int deleteCount, final Value... insertValues) {
			return this.execute(this.context(), function, deleteCount, insertValues);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final Value execute(final Object context, final Function function, final int deleteCount, final Value... insertValues) throws NullPointerException,
			IllegalArgumentException {
			if(function == null) throw new NullPointerException();
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
		 * Dieses Feld speichert das Kontextobjekt.
		 */
		final Object context;

		/**
		 * Dieses Feld speichert die {@link Value Parameterwerte}.
		 */
		final Value[] values;

		/**
		 * Dieser Konstrukteur initialisiert Kontextobjekt und {@link Value Parameterwerte}.
		 * 
		 * @param context Kontextobjekt.
		 * @param values {@link Value Parameterwerte}.
		 * @throws NullPointerException Wenn einer der gegebenen {@link Value Parameterwerte} {@code null} ist.
		 */
		public DefaultScope(final Object context, final Value... values) throws NullPointerException {
			if(values == null) throw new NullPointerException("values is null");
			if(Arrays.asList(values).contains(null)) throw new NullPointerException("values contains null");
			this.context = context;
			this.values = values;
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
			return values[index];
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
			return Objects.toStringCall(true, true, "defaultScope", "context", this.context, "values", this.values);
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
			if(scope == null) throw new NullPointerException("scope is null");
			if(deleteCount < 0) throw new IllegalArgumentException("deleteCount < 0");
			if(deleteCount > scope.size()) throw new IllegalArgumentException("deleteCount > scope.size()");
			if(insertValues == null) throw new NullPointerException("insertValues is null");
			if(Arrays.asList(insertValues).contains(null)) throw new NullPointerException("insertValues contains null");
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
			if(index < 0) throw new IndexOutOfBoundsException("index out of range: " + index);
			final Value[] insertValues = this.insertValues;
			final int length = insertValues.length;
			if(index >= length) return this.scope.get((index + this.deleteCount) - length);
			return insertValues[index];
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
			return Objects.toStringCall(true, true, "executeScope", "scope", this.scope, "context", this.context, "deleteCount", this.deleteCount, "insertValues",
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
		final Value[] values;

		/**
		 * Dieses Feld speichert die {@link Function Parameterfunktionen}, deren {@link Value Ergebniswerte} als {@link Value Parameterwerte} verwendet werden.
		 */
		final Function[] functions;

		/**
		 * Dieser Konstrukteur initialisiert {@link Scope Ausführungskontext} und {@link Function Parameterfunktionen}. Die {@link Function Parameterfunktionen} werden nicht geprüft.
		 * 
		 * @param scope {@link Scope Ausführungskontext} der {@link Function Parameterfunktionen}.
		 * @param functions {@link Function Parameterfunktionen}.
		 * @throws NullPointerException Wenn der gegebene {@link Scope Ausführungskontext} {@code null} ist.
		 */
		CompositeScope(final Function[] functions, final Scope scope) throws NullPointerException {
			if(scope == null) throw new NullPointerException("scope is null");
			this.scope = scope;
			this.values = new Value[functions.length];
			this.functions = functions;
		}

		/**
		 * Dieser Konstrukteur initialisiert {@link Scope Ausführungskontext} und {@link Function Parameterfunktionen}.
		 * 
		 * @param scope {@link Scope Ausführungskontext} der {@link Function Parameterfunktionen}.
		 * @param functions {@link Function Parameterfunktionen}.
		 * @throws NullPointerException Wenn der gegebene {@link Scope Ausführungskontext} bzw. eine der gegebenen {@link Function Parameterfunktionen} {@code null} ist.
		 */
		public CompositeScope(final Scope scope, final Function... functions) throws NullPointerException {
			if(scope == null) throw new NullPointerException("scope is null");
			if(functions == null) throw new NullPointerException("functions is null");
			if(Arrays.asList(functions).contains(null)) throw new NullPointerException("functions contains null");
			this.scope = scope;
			this.values = new Value[functions.length];
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
		public Value get(final int index) throws IndexOutOfBoundsException {
			if(index < 0) throw new IndexOutOfBoundsException("index out of range: " + index);
			final Value[] values = this.values;
			if(index >= values.length) throw new IndexOutOfBoundsException("index out of range: " + index);
			Value value = values[index];
			if(value != null) return value;
			value = new ReturnValue(this.scope, this.functions[index]);
			// if(value == null) throw new NullPointerException("value is null");
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
			return Objects.toStringCall(true, true, "compositeScope", "scope", this.scope, "functions", this.functions, "values", this.values);
		}

	}

	/**
	 * Dieses Feld speichert den leeren {@link Scope Ausführungskontext}.
	 */
	static final Scope VOID_SCOPE = new AbstractScope() {

		@Override
		public Value get(final int index) throws IndexOutOfBoundsException {
			throw new IndexOutOfBoundsException("index out of range: " + index);
		}

		@Override
		public int size() {
			return 0;
		}

		@Override
		public Object context() {
			return null;
		}

		@Override
		public String toString() {
			return Objects.toStringCall("voidScope");
		}

	};

	/**
	 * Diese Methode gibt den leeren {@link Scope Ausführungskontext} ohne Kontextobjekt und ohne {@link Value Parameterwerte} zurück.
	 * 
	 * @return {@code void}-{@link Scope Ausführungskontext}.
	 */
	public static Scope voidScope() {
		return Scopes.VOID_SCOPE;
	}

	/**
	 * Diese Methode erzeugt einen neuen {@link Scope Ausführungskontext} mit dem gegebenen Kontextobjekt und gibt ihn zurück.
	 * 
	 * @see Scopes#voidScope()
	 * @see Scopes#createScope(Object, Value...)
	 * @see Value#arrayData()
	 * @see Values#voidValue()
	 * @param context Kontextobjekt.
	 * @return {@link Scope Ausführungskontext}.
	 */
	public static Scope createScope(final Object context) {
		if(context == null) return Scopes.voidScope();
		return Scopes.createScope(context, new Value[0]);
	}

	/**
	 * Diese Methode erzeugt einen neuen {@link Scope Ausführungskontext} mit dem gegebenen Kontextobjekt sowie den gegebenen {@link Value Parameterwerten} und gibt ihn zurück.
	 * 
	 * @see Scopes#voidScope()
	 * @see Scopes#createScope(Object, Value...)
	 * @see Value#arrayData()
	 * @see Values#arrayValue(Object...)
	 * @param context Kontextobjekt.
	 * @param values {@link Value Parameterwerte}.
	 * @return {@link Scope Ausführungskontext}.
	 * @throws NullPointerException Wenn die gegebenen {@link Value Parameterwerte} {@code null} sind.
	 */
	public static Scope createScope(final Object context, final Object... values) throws NullPointerException {
		return Scopes.createScope(context, ArrayValue.valueOf(values).dataTo(ArrayType.INSTANCE));
	}

	/**
	 * Diese Methode erzeugt einen neuen {@link Scope Ausführungskontext} mit dem gegebenen Kontextobjekt sowie den gegebenen {@link Value Parameterwerten} und gibt ihn zurück.
	 * 
	 * @see Scopes#voidScope()
	 * @param context Kontextobjekt.
	 * @param values {@link Value Parameterwerte}.
	 * @return {@link Scope Ausführungskontext}.
	 * @throws NullPointerException Wenn die gegebenen {@link Value Parameterwerte} {@code null} sind.
	 */
	public static Scope createScope(final Object context, final Value... values) throws NullPointerException {
		if(values == null) throw new NullPointerException("values is null");
		if((context == null) && (values.length == 0)) return Scopes.voidScope();
		return new DefaultScope(context, values);
	}

	/**
	 * Dieser Konstrukteur ist versteckt und verhindert damit die Erzeugung von Instanzen der Klasse.
	 */
	Scopes() {
	}

}
