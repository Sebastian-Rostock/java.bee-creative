package bee.creative.function;

import java.util.Iterator;
import bee.creative.function.Scripts.ScriptFormatter;
import bee.creative.function.Scripts.ScriptFormatterInput;
import bee.creative.function.Values.VirtualValue;
import bee.creative.util.Comparables.Items;
import bee.creative.util.Iterators;
import bee.creative.util.Objects.UseToString;

/**
 * Diese Klasse implementiert den abstrakten Ausführungskontext einer Funktion.<br>
 * Ein Ausführungskontext stellt eine unveränderliche Liste von Parameterwerten sowie ein konstantes Kontextobjekt zur Verfügung. Über die {@link #size() Anzahl
 * der Parameterwerte} hinaus, können von der Methode {@link #get(int)} auch zusätzliche Parameterwerte eines übergeordneten Ausführungskontexts bereitgestellt
 * werden. Die Methode {@link #get(int)} muss für einen gegebenen Index immer den gleichen Wert liefern bzw. immer eine Ausnahme auslösen. Analoges gilt für die
 * Methoden {@link #size()} und {@link #context()}.
 * 
 * @see Value
 * @see Function
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public abstract class Scope implements Items<Value>, Iterable<Value>, UseToString, ScriptFormatterInput {

	/**
	 * Dieses Feld speichert den leeren Ausführungskontext, der keine Parameterwerte bereitstellt und als Kontextobjekt {@link Context#DEFAULT} verwendet.
	 */
	public static final Scope EMPTY = new Scope() {

		@Override
		public Context context() {
			return Context.DEFAULT;
		}

	};

	{}

	/**
	 * Diese Methode gibt einen neuen Ausführungskontexts zurück, welcher einer Funktion die gegebenen Parameterwerte sowie das Kontextobjekt des gegebenen
	 * Ausführungskontexts zur Verfügung stellt.<br>
	 * Sie ist eine Abkürzung für {@code valueScope(scope, values, true)}.
	 * 
	 * @see #arrayScope(Scope, Array, boolean)
	 * @param scope übergeordneter Ausführungskontext, dessen Kontextobjekt und Parameterwerte genutzt werden.
	 * @param values zugesicherte Parameterwerte des erzeugten Ausführungskontexts.
	 * @return {@code value}-{@link Scope}.
	 * @throws NullPointerException Wenn {@code scope} bzw. {@code values} {@code null} ist.
	 */
	public static Scope arrayScope(final Scope scope, final Array values) throws NullPointerException {
		return Scope.arrayScope(scope, values, true);
	}

	/**
	 * Diese Methode gibt einen neuen Ausführungskontexts zurück, welcher einer Funktion die gegebenen Parameterwerte sowie das Kontextobjekt des gegebenen
	 * Ausführungskontexts zur Verfügung stellt.<br>
	 * Für einen {@code index >= size()} liefert die Methode {@link #get(int)} abhängig von {@code replace} einen beliebigen oder nur einen zusätzlichen
	 * Parameterwert von {@code scope}, d.h. wenn {@code replace} {@code true} ist, liefert sie {@code scope.get(index - size())} und wenn {@code replace}
	 * {@code false} ist, liefert sie {@code scope.get(index - size() + scope.size())}. Für einen {@code index < size()} liefert die Methode {@link #get(int)}
	 * stets {@code values.get(index)}.
	 * 
	 * @param scope übergeordneter Ausführungskontext, dessen Kontextobjekt und Parameterwerte genutzt werden.
	 * @param values zugesicherte Parameterwerte des erzeugten Ausführungskontexts.
	 * @param replace {@code true}, wenn die zugesicherten Parameterwerte von {@code scope} virtuell durch die gegebenen Parameterwerte in {@code values} ersetzt
	 *        werden sollen und die zusätzlichen Parameterwerte von {@code scope} damit gleich den zusätzlichen Parameterwerten des erzeugten Ausführungskontexts
	 *        bilden sollen. {@code false}, wenn alle Parameterwerte von {@code scope} die zusätzlichen Parameterwerten des erzeugten Ausführungskontexts bilden
	 *        sollen.
	 * @return {@code value}-{@link Scope}.
	 * @throws NullPointerException Wenn {@code scope} bzw. {@code values} {@code null} ist.
	 */
	public static Scope arrayScope(final Scope scope, final Array values, boolean replace) throws NullPointerException {
		if (scope == null) throw new NullPointerException("scope = null");
		if (values == null) throw new NullPointerException("values = null");
		final int length = values.length();
		final int offset = (replace ? scope.size() : 0) - length;
		replace = false;
		return new Scope() {

			@Override
			public Value get(final int index) throws NullPointerException, IndexOutOfBoundsException {
				return index >= length ? scope.get(index + offset) : values.get(index);
			}

			@Override
			public int size() {
				return length;
			}

			@Override
			public Context context() {
				return scope.context();
			}

			@Override
			public Array array() {
				return values;
			}

		};
	}

	/**
	 * Diese Methode gibt einen neuen Ausführungskontext zurück, welcher einer Funktion die mit den gegebenen Parameterfunktionen ermittelten Werte als
	 * zugesicherte Parameterwerte, die Parameterwerte des gegebenen Ausführungskontext als zusätzliche Parameterwerte sowie das Kontextobjekt des gegebenen
	 * Ausführungskontexts zur Verfügung stellt.<br>
	 * Genauer werden die zugesicherten Parameterwerte mit Hilfe eines gegebenen Ausführungskontexts und der gegebener Parameterfunktionen ermittelt werden. Eine
	 * Parameterfunktion wird zur Ermittlung eines Parameterwerts einmalig mit dem gegebenen Ausführungskontext aufgerufen. Der Ergebniswert werden anschließend
	 * zur Wiederverwendung zwischengespeichert.<br>
	 * Für einen {@code index >= size()} liefert die Methode {@link #get(int)} einen beliebigen Parameterwert von {@code scope}, d.h.
	 * {@code scope.get(index - size())}. Für einen {@code index < size()} liefert die Methode {@link #get(int)} das Ergebnis von
	 * {@code params[index].execute(scope)}.<br>
	 * Die über {@link #array()} erzeugte Wertliste liefert für die noch nicht über {@link #get(int)} ermittelten Parameterwerte {@link VirtualValue}.
	 * 
	 * @param scope übergeordneter Ausführungskontext, dessen Kontextobjekt und Parameterwerte genutzt werden.
	 * @param params Parameterfunktionen, deren Ergebniswerte als zugesicherte Parameterwerte genutzt werden.
	 * @return {@code invoke}-{@link Scope}.
	 * @throws NullPointerException Wenn {@code scope} bzw. {@code functions} {@code null} ist.
	 */
	public static Scope invokeScope(final Scope scope, final Function... params) throws NullPointerException {
		if (scope == null) throw new NullPointerException("scope = null");
		if (params == null) throw new NullPointerException("params = null");
		final Value[] values = new Value[params.length];
		return new Scope() {

			@Override
			public Value get(final int index) throws NullPointerException, IndexOutOfBoundsException {
				final int length = values.length;
				if (index >= length) return scope.get(index - length);
				synchronized (values) {
					Value result = values[index];
					if (result != null) return result;
					final Function param = params[index];
					if (param == null) throw new NullPointerException("params[index] = null");
					result = param.execute(scope);
					if (result == null) throw new NullPointerException("params[index].execute(scope) = null");
					return values[index] = result;
				}
			}

			@Override
			public int size() {
				return values.length;
			}

			@Override
			public Context context() {
				return scope.context();
			}

			@Override
			public Array array() {
				return new Array() {

					@Override
					public Value get(final int index) throws IndexOutOfBoundsException {
						if (index < 0) throw new IndexOutOfBoundsException();
						final int length = values.length;
						if (index >= length) throw new IndexOutOfBoundsException();
						synchronized (values) {
							Value result = values[index];
							if (result != null) return result;
							result = new VirtualValue(scope, params[index]);
							return values[index] = result;
						}
					}

					@Override
					public int length() {
						return values.length;
					}

				};
			}

		};
	}

	/**
	 * Diese Methode gibt einen neuen Ausführungskontext zurück, welcher einer Funktion die Parameterwerte des gegebenen Ausführungskontexts sowie das gegebene
	 * Kontextobjekt zur Verfügung stellt.
	 * 
	 * @param scope Ausführungskontext.
	 * @param context Kontextobjekt.
	 * @return {@code context}-{@link Scope} mit den Parameterwerten von {@code scope} sowie dem Kontextobjekt {@code context}.
	 * @throws NullPointerException Wenn {@code scope} bzw. {@code context} {@code null} ist.
	 */
	public static Scope contextScope(final Scope scope, final Context context) throws NullPointerException {
		if (scope == null) throw new NullPointerException("scope = null");
		if (context == null) throw new NullPointerException("context = null");
		return new Scope() {

			@Override
			public Value get(final int index) throws IndexOutOfBoundsException {
				return scope.get(index);
			}

			@Override
			public int size() {
				return scope.size();
			}

			@Override
			public Context context() {
				return context;
			}

			@Override
			public Array array() {
				return scope.array();
			}

		};
	}

	{}

	/**
	 * Diese Methode gibt die Anzahl der Parameterwerte zurück, die zur Verwendung durch eine aufgerufene Funktion bestimmt sind. Über die Methode
	 * {@link #get(int)} werden mindestens soviele Parameterwerte bereitgestellt.
	 * 
	 * @return Anzahl der zugesicherten Parameterwert.
	 */
	public int size() {
		return 0;
	}

	/**
	 * Diese Methode gibt eine Wertliste als Sicht auf die zugesicherten Parameterwerte zurück.
	 * 
	 * @see #get(int)
	 * @see #size()
	 * @return {@link Array} der Parameterwerte.
	 */
	public Array array() {
		if (this.size() == 0) return Array.EMPTY;
		return new Array() {
	
			@Override
			public Value get(final int index) throws IndexOutOfBoundsException {
				if ((index < 0) || (index >= Scope.this.size())) throw new IndexOutOfBoundsException();
				return Scope.this.get(index);
			}
	
			@Override
			public int length() {
				return Scope.this.size();
			}
	
		};
	}

	/**
	 * Diese Methode gibt das Kontextobjekt zurück. Funktionen können aus diesem Objekt Informationen für ihre Berechnungen extrahieren oder auch den Zustand
	 * dieses Objekts modifizieren. Das Kontextobjekt entspricht dem Kontext {@code this} in {@code Java}-Methoden.
	 * 
	 * @return Kontextobjekt.
	 */
	public abstract Context context();

	{}

	/**
	 * Diese Methode gibt den {@code index}-ten Parameterwert zurück. Über die {@link #size() Anzahl der Parameterwerte} hinaus, können auch zusätzliche
	 * Parameterwerte bereitgestellt werden.
	 * 
	 * @see Value
	 * @param index Index.
	 * @return {@code index}-ter Parameterwert.
	 * @throws IndexOutOfBoundsException Wenn für den gegebenen Index kein Parameterwert existiert.
	 */
	@Override
	public Value get(final int index) throws IndexOutOfBoundsException {
		throw new IndexOutOfBoundsException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<Value> iterator() {
		return Iterators.itemsIterator(this, 0, this.size());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return this.array().hashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof Scope)) return false;
		final Scope data = (Scope)object;
		return this.array().equals(data.array());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void toScript(final ScriptFormatter target) throws IllegalArgumentException {
		target.putScope(this.array());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return new ScriptFormatter().formatData((Object)this);
	}

}
