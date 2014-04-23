package bee.creative.function;

import java.util.Iterator;
import bee.creative.function.Functions.CompositeFunction;
import bee.creative.util.Iterators.GetIterator;
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
		public Iterator<Value> iterator() {
			return new GetIterator<Value>(this, size());
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
	 * Diese Klasse implementiert den leeren {@link Scope Ausführungskontext} zum Aufruf einer {@link Function Funktion} ohne Kontextobjekt und ohne {@link Value
	 * Parameterwerte}.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class VoidScope extends AbstractScope {

		/**
		 * Dieses Feld speichert den leeren {@link Scope Ausführungskontext} ohne Kontextobjekt und ohne {@link Value Parameterwerte}.
		 */
		public static final VoidScope INSTANCE = new VoidScope();

		/**
		 * Diese Methode gibt den {@code index}-ten {@link Value Parameterwert} zurück.
		 */
		@Override
		public Value get(final int index) throws IndexOutOfBoundsException {
			throw new IndexOutOfBoundsException();
		}

		/**
		 * {@inheritDoc}
		 * <p>
		 * Der Rückgabewert ist {@code 0}.
		 */
		@Override
		public int size() {
			return 0;
		}

		/**
		 * {@inheritDoc}
		 * <p>
		 * Der Rückgabewert ist {@code null}.
		 */
		@Override
		public Object context() {
			return null;
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Scope Ausführungskontext} zum Aufruf einer {@link Function Funktion} mit gegebenen {@link Value Parameterwerten} im
	 * Kontext eines übergeordneten {@link Scope Ausführungskontexts}. Über die Methode {@link #get(int)} kann dabei auf die zusätzlichen {@link Value
	 * Parameterwerte} des übergeordneten {@link Scope Ausführungskontexts} zugegriffen werden.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ValueScope extends AbstractScope {

		/**
		 * Dieses Feld speichert den übergeordneten {@link Scope Ausführungskontext}, dessen {@link Value Parameterwerte} virtuell ersetzt werden.
		 */
		final Scope scope;

		/**
		 * Dieses Feld speichert die {@link Value Parameterwerte}.
		 */
		final Value[] values;

		/**
		 * Dieser Konstruktor initialisiert den übergeordneten {@link Scope Ausführungskontext} und die {@link Value Parameterwerte}. Das Kontextobjekt sowie die
		 * zusätzlichen {@link Value Parameterwerte} entsprechen denen des gegebenen {@link Scope Ausführungskontexts}.
		 * 
		 * @param scope übergeordneter {@link Scope Ausführungskontext}.
		 * @param values {@link Value Parameterwerte}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public ValueScope(final Scope scope, final Value... values) throws NullPointerException {
			if((scope == null) || (values == null)) throw new NullPointerException();
			this.scope = scope;
			this.values = values;
		}

		/**
		 * {@inheritDoc} Diese entsprechen hierbei dem zusätzlichen {@link Value Parameterwerten} des übergeordneten {@link Scope Ausführungskontext}, die über
		 * {@code this.scope().get(index - this.size() + this.scope.size())} ermittelt werden.
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
		 * {@inheritDoc}
		 */
		@Override
		public int size() {
			return this.values.length;
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
			return this.scope.context();
		}

		/**
		 * Diese Methode gibt eine Kopie der {@link Value Parameterwerte} zurück.
		 * 
		 * @return Kopie der {@link Value Parameterwerte}.
		 */
		public Value[] values() {
			return this.values.clone();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCallFormat(true, true, this, new Object[]{"scope", this.scope, "values", this.values});
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Scope Ausführungskontext} zum Aufruf einer {@link Function Funktion} mit den {@link Value Parameterwerten} eines
	 * gegebenen {@link Scope Ausführungskontexts} und einem gegebenem Kontextobjekt. Über die Methode {@link #get(int)} kann auf die {@link Value Parameterwerte}
	 * des gegebenen {@link Scope Ausführungskontexts} zugegriffen werden.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ContextScope extends AbstractScope {

		/**
		 * Dieses Feld speichert den {@link Scope Ausführungskontext}.
		 */
		final Scope scope;

		/**
		 * Dieses Feld speichert das Kontextobjekt.
		 */
		final Object context;

		/**
		 * Dieser Konstruktor initialisiert den {@link Scope Ausführungskontext} und das Kontextobjekt.
		 * 
		 * @param scope {@link Scope Ausführungskontext}.
		 * @param context Kontextobjekt.
		 * @throws NullPointerException Wenn der gegebene {@link Scope Ausführungskontext} {@code null} ist.
		 */
		public ContextScope(final Scope scope, final Object context) throws NullPointerException {
			if(scope == null) throw new NullPointerException();
			this.scope = scope;
			this.context = context;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Value get(final int index) throws IndexOutOfBoundsException {
			return this.scope.get(index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int size() {
			return this.scope.size();
		}

		/**
		 * Diese Methode gibt den {@link Scope Ausführungskontext} zurück, dessen {@link Value Parameterwerte} übernommen werden.
		 * 
		 * @return {@link Scope Ausführungskontext}.
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
		public String toString() {
			return Objects.toStringCallFormat(true, true, this, new Object[]{"scope", this.scope, "context", this.context});
		}

	}

	/**
	 * Diese Klasse implementiert den {@link Scope Ausführungskontext} einer {@link CompositeFunction Komposition}, deren {@link Value Parameterwerte} mit Hilfe
	 * eines gegebenen {@link Scope Ausführungskontexts} und gegebener {@link Function Parameterfunktionen} ermittelt werden. Die {@link Function
	 * Parameterfunktionen} werden zur Ermittlung der {@link Value Parameterwerte} einmalig mit dem gegebenen {@link Scope Ausführungskontext} aufgerufen. Deren
	 * {@link Value Ergebniswerte} werden dann zur Wiederverwendung zwischengespeichert.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class CompositeScope extends AbstractScope {

		/**
		 * Dieses Feld speichert den übergeordneten {@link Scope Ausführungskontext}, der für die {@link Function Parameterfunktionen} genutzt wird.
		 */
		final Scope scope;

		/**
		 * Dieses Feld speichert die {@link Value Parameterwerte}.
		 */
		final Value[] values;

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
		 * {@inheritDoc} Diese entsprechen hierbei den {@link Value Parameterwerten} des übergeordneten {@link Scope Ausführungskontext}, die über
		 * {@code this.scope().get(index - this.size())} ermittelt werden.
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
			// return values[index] = new ReturnValue(this.scope, this.functions[index]);
			value = this.functions[index].execute(this.scope);
			if(value == null) throw new NullPointerException();
			return values[index] = value;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int size() {
			return this.values.length;
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
			return Objects.toStringCallFormat(true, true, this, new Object[]{"scope", this.scope, "values", this.values, "functions", this.functions});
		}

	}

}
