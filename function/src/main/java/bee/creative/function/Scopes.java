package bee.creative.function;

import java.util.Iterator;
import bee.creative.function.Functions.CompositeFunction;
import bee.creative.util.Iterators.GetIterator;
import bee.creative.util.Objects;
import bee.creative.util.Objects.UseToString;

/**
 * Diese Klasse implementiert Hilfsmethoden und Hilfsklassen zur Verarbeitung von Ausführungskontexten.
 * 
 * @see Value
 * @see Scope
 * @see Function
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class Scopes {

	/**
	 * Diese Klasse implementiert einen abstrakten Ausführungskontext.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static abstract class AbstractScope implements Scope, UseToString {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<Value> iterator() {
			return new GetIterator<Value>(this, this.size());
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return Array.valueOf(this).hashCode();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof Scope)) return false;
			final Scope data = (Scope)object;
			return Array.valueOf(this).equals(Array.valueOf(data));
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
	 * Diese Klasse implementiert den leeren Ausführungskontext zum Aufruf einer Funktion ohne Parameterwerte. Als Kontextobjekt wird
	 * {@link Contexts#getDefaultContext()} verwendet.
	 * 
	 * @see #get(int)
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class VoidScope extends AbstractScope {

		/**
		 * Dieses Feld speichert den leeren Ausführungskontext, dass keine Parameterwerte bereitstellt und als Kontextobjekt {@link Contexts#getDefaultContext()}
		 * verwendet.
		 */
		public static final VoidScope INSTANCE = new VoidScope();

		{}

		/**
		 * Dieser Konstruktor ist versteckt.
		 */
		VoidScope() {
		}

		{}

		/**
		 * {@inheritDoc} Die {@link IndexOutOfBoundsException} wird immer ausgelöst.
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
		 * Der Rückgabewert ist {@link Contexts#getDefaultContext()}.
		 */
		@Override
		public Context context() {
			return Contexts.defaultContext;
		}

	}

	/**
	 * Diese Klasse implementiert einen Ausführungskontext zum Aufruf einer Funktion mit gegebenen Parameterwerten im Rahmen eines übergeordneten
	 * Ausführungskontexts. Über die Methode {@link #get(int)} kann dabei auf die zusätzlichen Parameterwerte des übergeordneten Ausführungskontexts zugegriffen
	 * werden.
	 * 
	 * @see #get(int)
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ValueScope extends AbstractScope {

		/**
		 * Dieses Feld speichert den übergeordneten Ausführungskontext, dessen zusätzlichen Parameterwerte genutzt werden.
		 */
		final Scope scope;

		/**
		 * Dieses Feld speichert die Parameterwerte.
		 */
		final Array values;

		/**
		 * Dieses Feld speichert die Anzahl der Parameterwerte.
		 */
		final int length;

		/**
		 * Dieses Feld speichert den Korrekturwert für den Index der zusätzlichen Parameterwerte.
		 */
		final int offset;

		/**
		 * Dieser Konstruktor initialisiert den übergeordneten Ausführungskontext und die Parameterwerte. Das Kontextobjekt sowie die zusätzlichen Parameterwerte
		 * entsprechen denen des gegebenen Ausführungskontexts.
		 * 
		 * @param scope übergeordneter Ausführungskontext, dessen zusätzlichen Parameterwerte genutzt werden.
		 * @param values Parameterwerte.
		 * @throws NullPointerException Wenn {@code scope} bzw. {@code values} {@code null} ist.
		 */
		public ValueScope(final Scope scope, final Array values) throws NullPointerException {
			this(scope, values, true);
		}

		/**
		 * Dieser Konstruktor initialisiert den übergeordneten Ausführungskontext und die Parameterwerte. Das Kontextobjekt sowie die zusätzlichen Parameterwerte
		 * stammen aus dem gegebenen Ausführungskontext.
		 * 
		 * @param scope übergeordneter Ausführungskontext, dessen (zusätzlichen) Parameterwerte genutzt werden.
		 * @param values Parameterwerte.
		 * @param replace {@code true}, wenn statt aller nur die zusätzlichen Parameterwerte des gegebenen Ausführungskontexts als zusätzliche Parameterwerte
		 *        genutzt werden sollen.
		 * @throws NullPointerException Wenn {@code scope} bzw. {@code values} {@code null} ist.
		 */
		public ValueScope(final Scope scope, final Array values, final boolean replace) throws NullPointerException {
			this.scope = scope;
			this.values = values;
			this.length = values.length();
			this.offset = (replace ? scope.size() : 0) - this.length;
		}

		{}

		/**
		 * {@inheritDoc} Diese entsprechen hierbei entweder allen oder nur den zusätzlichen Parameterwerten des übergeordneten Ausführungskontexts, welche über
		 * {@code this.scope().get(index - this.size())} bzw. {@code this.scope().get(index - this.size() + this.scope().size())} ermittelt werden.
		 * 
		 * @see #scope()
		 * @throws NullPointerException Wenn der {@code index}-te Parameterwert {@code null} ist.
		 */
		@Override
		public Value get(final int index) throws NullPointerException, IndexOutOfBoundsException {
			return index >= this.length ? this.scope.get(index + this.offset) : this.values.get(index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int size() {
			return this.length;
		}

		/**
		 * Diese Methode gibt den übergeordneten Ausführungskontext zurück, dessen Parameterwerte als zusätzliche übernommen werden.
		 * 
		 * @return übergeordneter Ausführungskontext.
		 */
		public Scope scope() {
			return this.scope;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Context context() {
			return this.scope.context();
		}

		/**
		 * Diese Methode gibt die Parameterwerte zurück.
		 * 
		 * @return Parameterwerte.
		 */
		public Array values() {
			return this.values;
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCallFormat(true, true, this, new Object[]{"scope", this.scope, "values", this.values});
		}

	}

	/**
	 * Diese Klasse implementiert einen Ausführungskontext zum Aufruf einer Funktion mit den Parameterwerten eines gegebenen Ausführungskontexts und einem
	 * gegebenem Kontextobjekt. Über die Methode {@link #get(int)} kann auf die Parameterwerte des gegebenen Ausführungskontexts zugegriffen werden.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ContextScope extends AbstractScope {

		/**
		 * Dieses Feld speichert den Ausführungskontext.
		 */
		final Scope scope;

		/**
		 * Dieses Feld speichert das Kontextobjekt.
		 */
		final Context context;

		/**
		 * Dieser Konstruktor initialisiert den Ausführungskontext und das Kontextobjekt.
		 * 
		 * @param scope Ausführungskontext.
		 * @param context Kontextobjekt.
		 * @throws NullPointerException Wenn {@code scope} {@code null} ist.
		 */
		public ContextScope(final Scope scope, final Context context) throws NullPointerException {
			if (scope == null) throw new NullPointerException();
			this.scope = scope;
			this.context = context;
		}

		{}

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
		 * Diese Methode gibt den Ausführungskontext zurück, dessen Parameterwerte übernommen werden.
		 * 
		 * @return Ausführungskontext.
		 */
		public Scope scope() {
			return this.scope;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Context context() {
			return this.context;
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCallFormat(true, true, this, new Object[]{"scope", this.scope, "context", this.context});
		}

	}

	/**
	 * Diese Klasse implementiert den Ausführungskontext einer Funktionskomposition, deren Parameterwerte mit Hilfe eines gegebenen Ausführungskontexts und
	 * gegebener Parameterfunktionen ermittelt werden. Die Parameterfunktionen werden zur Ermittlung der Parameterwerte einmalig mit dem gegebenen
	 * Ausführungskontext aufgerufen. Deren Ergebniswerte werden dann zur Wiederverwendung zwischengespeichert.
	 * 
	 * @see #get(int)
	 * @see CompositeFunction
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class CompositeScope extends AbstractScope {

		/**
		 * Dieses Feld speichert den übergeordneten Ausführungskontext, der für die Parameterfunktionen genutzt wird.
		 */
		final Scope scope;

		/**
		 * Dieses Feld speichert die Parameterwerte.
		 */
		final Value[] values;

		/**
		 * Dieses Feld speichert die Parameterfunktionen, deren Ergebniswerte als Parameterwerte verwendet werden.
		 */
		final Function[] functions;

		/**
		 * Dieser Konstruktor initialisiert den übergeordneten Ausführungskontext und die Parameterfunktionen.
		 * 
		 * @param scope übergeordneter Ausführungskontext.
		 * @param functions Parameterfunktionen.
		 * @throws NullPointerException Wenn {@code scope} bzw. {@code functions} {@code null} ist.
		 */
		public CompositeScope(final Scope scope, final Function... functions) throws NullPointerException {
			if ((scope == null) || (functions == null)) throw new NullPointerException();
			this.scope = scope;
			this.values = new Value[functions.length];
			this.functions = functions;
		}

		{}

		/**
		 * {@inheritDoc} Diese entsprechen hierbei den Parameterwerten des übergeordneten Ausführungskontext, die über {@code this.scope().get(index - this.size())}
		 * ermittelt werden.
		 * 
		 * @see #scope()
		 * @throws NullPointerException Wenn die {@code index}-te Parameterfunktion bzw. ihr Ergebniswert {@code null} ist.
		 */
		@Override
		public Value get(final int index) throws NullPointerException, IndexOutOfBoundsException {
			final Value[] values = this.values;
			final int length = values.length;
			if (index >= length) return this.scope.get(index - length);
			Value value = values[index];
			if (value != null) return value;
			value = this.functions[index].execute(this.scope);
			if (value == null) throw new NullPointerException();
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
		 * Diese Methode gibt den Ausführungskontext der Parameterfunktionen zurück.
		 * 
		 * @return Ausführungskontext der Parameterfunktionen.
		 */
		public Scope scope() {
			return this.scope;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Context context() {
			return this.scope.context();
		}

		/**
		 * Diese Methode gibt eine Kopie der berechneten Parameterwerte zurück.
		 * 
		 * @return Parameterwerte.
		 */
		public Value[] values() {
			return this.values.clone();
		}

		/**
		 * Diese Methode gibt eine Kopie der Parameterfunktionen zurück.
		 * 
		 * @return Kopie der Parameterfunktionen.
		 */
		public Function[] functions() {
			return this.functions.clone();
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCallFormat(true, true, this, new Object[]{"scope", this.scope, "values", this.values, "functions", this.functions});
		}

	}

}
