package bee.creative.function;

import java.util.Arrays;
import java.util.Iterator;
import bee.creative.util.Objects;
import bee.creative.util.Objects.UseToString;

/**
 * Diese Klasse implementiert Hilfsmethoden und Hilfsklassen zur Verarbeitung von {@link Scope Ausführungskontexten} .
 * 
 * @see Value
 * @see Scope
 * @see Function
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class Scopes {

	/**
	 * Diese Klasse implementiert einen abstrakten, leeren {@link Scope Ausführungskontext}.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static abstract class BaseScope implements Scope, UseToString {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int size() {
			return 0;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Value get(final int index) throws IndexOutOfBoundsException {
			throw new IndexOutOfBoundsException("index out of range: " + index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object context() {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final Iterator<Value> iterator() {
			return new Iterator<Value>() {

				int index = 0;

				@Override
				public boolean hasNext() {
					return this.index < BaseScope.this.size();
				}

				@Override
				public Value next() {
					return BaseScope.this.get(this.index++);
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
		public final int hashCode() {
			int hash = 0;
			for(int i = 0, size = this.size(); i < size; i++){
				hash = (hash * 31) + Objects.hash(this.get(i));
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
	 * Diese Klasse implementiert einen {@link Scope Ausführungskontext} mit Kontextobjekt und {@link Value
	 * Parameterwerten}.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class DefaultScope extends BaseScope {

		/**
		 * Dieses Feld speichert das Kontextobjekt.
		 */
		final Object context;

		/**
		 * Dieses Feld speichert das values.
		 */
		final Value[] values;

		/**
		 * Dieser Konstrukteur initialisiert den {@link Scope Ausführungskontext} der aufrufenden {@link Function Funktion}
		 * sowie die {@link Value Parameterwerte}.
		 * 
		 * @param context {@link Scope Ausführungskontext}.
		 * @param values {@link Value Parameterwerte}.
		 * @throws NullPointerException Wenn der gegebene {@link Scope Ausführungskontext} oder einer der gegebenen
		 *         {@link Value Parameterwerte} {@code null} sind.
		 */
		public DefaultScope(final Object context, final Value... values) throws NullPointerException {
			if(context == null) throw new NullPointerException("context is null");
			if(values == null) throw new NullPointerException("values is null");
			if(Arrays.asList(values).contains(null)) throw new NullPointerException("values contains null");
			this.context = context;
			this.values = values;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final int size() {
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
		public final Object context() {
			return this.context;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("defaultScope", this.context, this.values);
		}

	}

	
	
	/**
	 * Dieses Feld speichert den leeren {@link Scope Ausführungskontext}.
	 */
	static final Scope VOID_SCOPE = new BaseScope() {

		@Override
		public String toString() {
			return Objects.toStringCall("voidScope");
		}

	};

	/**
	 * Diese Methode gibt den leeren {@link Scope Ausführungskontext} ohne Kontextobjekt und ohne {@link Value
	 * Parameterwerte} zurück.
	 * 
	 * @param Typ des Kontextobjekts.
	 * @return {@code void}-{@link Scope Ausführungskontext}.
	 */
	@SuppressWarnings ("unchecked")
	public static final Scope voidScope() {
		return Scopes.VOID_SCOPE;
	}

	/**
	 * Diese Methode erzeugt einen neuen {@link Scope Ausführungskontext} mit dem gegebenen Kontextobjekt und gibt ihn
	 * zurück.
	 * 
	 * @see Scopes#voidScope()
	 * @see Scopes#createScope(Object, Value...)
	 * @see Value#arrayData()
	 * @see Values#voidValue()
	 * @param Typ des Kontextobjekts.
	 * @param context Kontextobjekt.
	 * @return {@link Scope Ausführungskontext}.
	 */
	public static final Scope createScope(final Object context) {
		if(context == null) return Scopes.voidScope();
		return Scopes.createScope(context, Values.voidValue().arrayData());
	}

	/**
	 * Diese Methode erzeugt einen neuen {@link Scope Ausführungskontext} mit dem gegebenen Kontextobjekt sowie den
	 * gegebenen {@link Value Parameterwerten} und gibt ihn zurück.
	 * 
	 * @see Scopes#voidScope()
	 * @see Scopes#createScope(Object, Value...)
	 * @see Value#arrayData()
	 * @see Values#arrayValue(Object...)
	 * @param Typ des Kontextobjekts.
	 * @param context Kontextobjekt.
	 * @param values {@link Value Parameterwerte}.
	 * @return {@link Scope Ausführungskontext}.
	 * @throws NullPointerException Wenn die gegebenen {@link Value Parameterwerte} {@code null} sind.
	 */
	public static final Scope createScope(final Object context, final Object... values) throws NullPointerException {
		if((context == null) && (values.length == 0)) return Scopes.voidScope();
		return Scopes.createScope(context, Values.arrayValue(values).arrayData());
	}

	/**
	 * Diese Methode erzeugt einen neuen {@link Scope Ausführungskontext} mit dem gegebenen Kontextobjekt sowie den
	 * gegebenen {@link Value Parameterwerten} und gibt ihn zurück.
	 * 
	 * @see Scopes#voidScope()
	 * @param Typ des Kontextobjekts.
	 * @param context Kontextobjekt.
	 * @param values {@link Value Parameterwerte}.
	 * @return {@link Scope Ausführungskontext}.
	 * @throws NullPointerException Wenn die gegebenen {@link Value Parameterwerte} {@code null} sind.
	 */
	public static final Scope createScope(final Object context, final Value... values) throws NullPointerException {
		if((context == null) && (values.length == 0)) return Scopes.voidScope();
		return new DefaultScope(context, values);
	}

	/**
	 * Dieser Konstrukteur ist versteckt und verhindert damit die Erzeugung von Instanzen der Klasse.
	 */
	Scopes() {
	}

}
