package bee.creative.fem;

import java.util.Iterator;
import bee.creative.fem.FEM.VirtualValue;
import bee.creative.util.Comparables.Items;
import bee.creative.util.Iterators;
import bee.creative.util.Objects.UseToString;

/**
 * Diese Klasse implementiert einen Stapelrahmen ({@code stack-frame}), über welchen einer Funktion eine Liste von Parameterwerten sowie ein Kontextobjekt zur
 * Verfügung gestellt werden.<br>
 * Über die {@link #size() Anzahl der zugesicherten Parameterwerte} hinaus können von der Methode {@link #get(int)} auch zusätzliche Parameterwerte aus dem
 * {@link #parent() übergeordneten Stapelrahmen} bereitgestellt werden.<br>
 * Die Methode {@link #get(int)} liefert für einen gegebenen Index immer den gleichen Wert bzw. löst immer die gleiche Ausnahme aus. Analoges gilt für die
 * Methoden {@link #size()}, {@link #params()} und {@link #context()}.
 * 
 * @see FEMValue
 * @see FEMFunction
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public abstract class FEMFrame implements Items<FEMValue>, Iterable<FEMValue>, UseToString, FEM.ScriptFormatterInput {

	@SuppressWarnings ("javadoc")
	static final class ArrayFrame extends FEMFrame {

		final FEMArray __params;

		ArrayFrame(final FEMFrame parent, final FEMArray params, final FEMContext context) {
			super(parent, context);
			this.__params = params;
		}

		{}

		@Override
		public FEMValue get(final int index) throws IndexOutOfBoundsException {
			final int index2 = index - this.__params.__length;
			if (index2 >= 0) return this.__parent.get(index2);
			return this.__params.__get(index);
		}

		@Override
		public int size() {
			return this.__params.__length;
		}

		@Override
		public FEMArray params() {
			return this.__params;
		}

		@Override
		public FEMFrame withContext(final FEMContext context) throws NullPointerException {
			if (context == null) throw new NullPointerException("context = null");
			return new ArrayFrame(this.__parent, this.__params, context);
		}

	}

	@SuppressWarnings ("javadoc")
	static final class EmptyFrame extends FEMFrame {

		@Override
		public FEMValue get(final int index) throws IndexOutOfBoundsException {
			throw new IndexOutOfBoundsException();
		}

		@Override
		public int size() {
			return 0;
		}

		@Override
		public FEMArray params() {
			return FEMArray.EMPTY;
		}

		@Override
		public FEMFrame withContext(final FEMContext context) throws NullPointerException {
			if (context == null) throw new NullPointerException("context = null");
			return new ArrayFrame(this, FEMArray.EMPTY, context);
		}

	}

	@SuppressWarnings ("javadoc")
	static final class InvokeFrame extends FEMFrame {

		static final class Params extends FEMArray {

			final FEMFrame __frame;

			final FEMValue[] __values;

			final FEMFunction[] __params;

			Params(final FEMFrame frame, final FEMFunction[] params) {
				super(params.length);
				this.__frame = frame;
				this.__params = params;
				this.__values = new FEMValue[params.length];
			}

			{}

			@Override
			protected FEMValue __get(final int index) {
				synchronized (this.__values) {
					FEMValue result = this.__values[index];
					if (result != null) return result;
					final FEMFunction param = this.__params[index];
					if (param == null) throw new NullPointerException("params[index] = null");
					result = new FEM.VirtualValue(this.__frame, param);
					this.__values[index] = result;
					return result;
				}
			}

			protected FEMValue __get2(final int index) {
				final int index2 = index - this.__length;
				if (index2 >= 0) return this.__frame.get(index2);
				synchronized (this.__values) {
					FEMValue result = this.__values[index];
					if (result != null) return result;
					final FEMFunction param = this.__params[index];
					if (param == null) throw new NullPointerException("params[index] = null");
					result = param.invoke(this.__frame);
					if (result == null) throw new NullPointerException("params[index].invoke(frame) = null");
					this.__values[index] = result;
					return result;
				}
			}

		}

		{}

		final Params __params;

		InvokeFrame(final FEMFrame parent, final Params params, final FEMContext context) {
			super(parent, context);
			this.__params = params;
		}

		InvokeFrame(final FEMFrame parent, final FEMFunction[] params, final FEMContext context) {
			super(parent, context);
			this.__params = new Params(parent, params);
		}

		{}

		@Override
		public FEMValue get(final int index) throws IndexOutOfBoundsException {
			final int index2 = index - this.__params.__length;
			if (index2 >= 0) return this.__parent.get(index2);
			return this.__params.__get2(index);
		}

		@Override
		public int size() {
			return this.__params.__length;
		}

		@Override
		public FEMArray params() {
			return this.__params;
		}

		@Override
		public FEMFrame withContext(final FEMContext context) throws NullPointerException {
			if (context == null) throw new NullPointerException("context = null");
			return new InvokeFrame(this.__parent, this.__params, context);
		}

	}

	{}

	/**
	 * Dieses Feld speichert den leeren Stapelrahmen, der keine Parameterwerte bereitstellt, das Kontextobjekt {@link FEMContext#EMPTY} verwendet und sich selbst
	 * als {@link #parent()} nutzt.
	 */
	public static final FEMFrame EMPTY = new EmptyFrame();

	{}

	/**
	 * Diese Methode gibt einen Stapelrahmen mit dem gegebenen {@link #context() Kontextobjekt} zurück.<br>
	 * Sie ist eine Abkürzung für {@code EMPTY.withContext(context)}.
	 * 
	 * @param context Kontextobjekt.
	 * @return neue Stapelrahmen.
	 * @throws NullPointerException Wenn {@code context} {@code null} ist.
	 */
	public static final FEMFrame from(final FEMContext context) throws NullPointerException {
		return FEMFrame.EMPTY.withContext(context);
	}

	{}

	/**
	 * Dieses Feld speichert die übergeordneten Parameterdaten.
	 */
	protected final FEMFrame __parent;

	/**
	 * Dieses Feld speichert das Kontextobjekt.
	 */
	protected final FEMContext __context;

	@SuppressWarnings ("javadoc")
	FEMFrame() {
		this.__parent = this;
		this.__context = FEMContext.EMPTY;
	}

	@SuppressWarnings ("javadoc")
	FEMFrame(final FEMFrame parent, final FEMContext context) {
		this.__parent = parent;
		this.__context = context;
	}

	{}

	/**
	 * Diese Methode gibt die Anzahl der Parameterwerte zurück, die zur Verwendung durch eine aufgerufene Funktion bestimmt sind.<br>
	 * Über die Methode {@link #get(int)} werden mindestens soviele Parameterwerte bereitgestellt.
	 * 
	 * @return Anzahl der zugesicherten Parameterwert.
	 */
	public abstract int size();

	/**
	 * Diese Methode gibt die übergeordneten Parameterdaten zurück.
	 * 
	 * @return übergeordnete Parameterdaten oder {@code this}.
	 */
	public final FEMFrame parent() {
		return this.__parent;
	}

	/**
	 * Diese Methode gibt die Wertliste der zugesicherten Parameterwerte zurück.
	 * 
	 * @see #get(int)
	 * @see #size()
	 * @return {@link FEMArray} der Parameterwerte.
	 */
	public abstract FEMArray params();

	/**
	 * Diese Methode gibt das Kontextobjekt zurück.<<br>
	 * Funktionen können aus diesem Objekt Informationen für ihre Berechnungen extrahieren oder auch den Zustand dieses Objekts modifizieren.<br>
	 * Das Kontextobjekt entspricht dem Kontext {@code this} in {@code Java}-Methoden.
	 * 
	 * @return Kontextobjekt.
	 */
	public final FEMContext context() {
		return this.__context;
	}

	/**
	 * Diese Methode gibt einen neuen Stapelrahmen zurück, welcher keine {@link #params() zugesicherten Parameterwerte} besitzt, das {@link #context()
	 * Kontextobjekt} dieses Stapelrahmens übernimmt und diesen als {@link #parent() übergeordneten Stapelrahmen} nutzt.
	 * 
	 * @return neuer Stapelrahmen.
	 */
	public final FEMFrame newFrame() {
		return new ArrayFrame(this, FEMArray.EMPTY, this.__context);
	}

	/**
	 * Diese Methode gibt einen neuen Stapelrahmen zurück, welcher die gegebenen {@link #params() zugesicherten Parameterwerte} besitzt, das {@link #context()
	 * Kontextobjekt} dieses Stapelrahmens übernimmt und diesen als {@link #parent() übergeordneten Stapelrahmen} nutzt.
	 * 
	 * @param params zugesicherte Parameterwerte.
	 * @return neuer Stapelrahmen.
	 * @throws NullPointerException Wenn {@code params} {@code null} ist.
	 */
	public final FEMFrame newFrame(final FEMArray params) throws NullPointerException {
		return new ArrayFrame(this, params.__length == 0 ? FEMArray.EMPTY : params, this.__context);
	}

	/**
	 * Diese Methode gibt einen neuen Stapelrahmen zurück, welcher die gegebenen {@link #params() zugesicherten Parameterwerte} besitzt, das {@link #context()
	 * Kontextobjekt} dieses Stapelrahmens übernimmt und diesen als {@link #parent() übergeordneten Stapelrahmen} nutzt.
	 * 
	 * @see FEMArray#from(FEMValue...)
	 * @param params zugesicherte Parameterwerte.
	 * @return neuer Stapelrahmen.
	 * @throws NullPointerException Wenn {@code params} {@code null} ist.
	 */
	public final FEMFrame newFrame(final FEMValue... params) throws NullPointerException {
		return new ArrayFrame(this, FEMArray.from(params), this.__context);
	}

	/**
	 * Diese Methode gibt einen neuen Stapelrahmen zurück, welcher die gegebenen Parameterfunktionen zur Berechnung der {@link #params() zugesicherten
	 * Parameterwerte} verwendet, das {@link #context() Kontextobjekt} dieses Stapelrahmens übernimmt und diesen als {@link #parent() übergeordneten Stapelrahmen}
	 * nutzt.
	 * <p>
	 * Die zugesicherten Parameterwerte werden mit Hilfe dieses Stapelrahmens und der gegebener Parameterfunktionen ermittelt. Eine Parameterfunktion wird zur
	 * Ermittlung eines Parameterwerts einmalig mit diesem Stapelrahmen {@link FEMFunction#invoke(FEMFrame) ausgewertet}. Genauer entspricht der {@code index}-te
	 * zugesicherte Parameterwert dem Ergebnis von {@code params[index].invoke(this)}. Der Ergebniswert wird zur Wiederverwendung zwischengespeichert.<br>
	 * Die über {@link #params()} bereitgestellte Liste der Parameterwerte des erzeugten Stapelrahmen liefert die noch nicht über {@link #get(int)} ermittelten
	 * Parameterwerte als {@link VirtualValue}.
	 * 
	 * @param params Parameterfunktionen zur Berechnung der zugesicherten Parameterwerte.
	 * @return neuer Stapelrahmen.
	 * @throws NullPointerException Wenn {@code params} {@code null} ist.
	 */
	public final FEMFrame newFrame(final FEMFunction... params) throws NullPointerException {
		return new InvokeFrame(this, params, this.__context);
	}

	/**
	 * Diese Methode gibt diesen Stapelrahmen mit den gegebenen {@link #params() zugesicherten Parameterwerten} zurück.<br>
	 * Sie ist eine Abkürzung für {@code this.parent().newFrame(params).withContext(this.context())}.
	 * 
	 * @see #newFrame(FEMArray)
	 * @see #withContext(FEMContext)
	 * @param params zugesicherte Parameterwerte.
	 * @return neuer Stapelrahmen.
	 * @throws NullPointerException Wenn {@code params} {@code null} ist.
	 */
	public final FEMFrame withParams(final FEMArray params) throws NullPointerException {
		return new ArrayFrame(this.__parent, params.__length == 0 ? FEMArray.EMPTY : params, this.__context);
	}

	/**
	 * Diese Methode gibt diesen Stapelrahmen mit den gegebenen {@link #params() zugesicherten Parameterwerten} zurück.<br>
	 * Sie ist eine Abkürzung für {@code this.parent().newFrame(params).withContext(this.context())}.
	 * 
	 * @see #newFrame(FEMValue...)
	 * @see #withContext(FEMContext)
	 * @param params zugesicherte Parameterwerte.
	 * @return neuer Stapelrahmen.
	 * @throws NullPointerException Wenn {@code params} {@code null} ist.
	 */
	public final FEMFrame withParams(final FEMValue... params) throws NullPointerException {
		return new ArrayFrame(this.__parent, FEMArray.from(params), this.__context);
	}

	/**
	 * Diese Methode gibt diesen Stapelrahmen mit den gegebenen Parameterfunktionen zur Berechnung der {@link #params() zugesicherten Parameterwerte} zurück.<br>
	 * Sie ist eine Abkürzung für {@code this.parent().newFrame(params).withContext(this.context())}.
	 * 
	 * @see #newFrame(FEMFunction...)
	 * @see #withContext(FEMContext)
	 * @param params zugesicherte Parameterwerte.
	 * @return neuer Stapelrahmen.
	 * @throws NullPointerException Wenn {@code params} {@code null} ist.
	 */
	public final FEMFrame withParams(final FEMFunction... params) throws NullPointerException {
		return new InvokeFrame(this.__parent, params, this.__context);
	}

	/**
	 * Diese Methode gibt diesen Stapelrahmen mit dem gegebenen {@link #context() Kontextobjekt} zurück.
	 * 
	 * @param context Kontextobjekt.
	 * @return neuer Stapelrahmen.
	 * @throws NullPointerException Wenn {@code context} {@code null} ist.
	 */
	public abstract FEMFrame withContext(final FEMContext context) throws NullPointerException;

	{}

	/**
	 * Diese Methode gibt den {@code index}-ten Parameterwert zurück.<br>
	 * Über die {@link #size() Anzahl der zugesicherten Parameterwerte} hinaus, können auch zusätzliche Parameterwerte des {@link #parent() übergeordneten
	 * Stapelrahmens} bereitgestellt werden. Genauer wird für einen {@code index >= this.size()} der Parameterwert {@code this.parent().get(index - this.size())}
	 * des übergeordneten Stapelrahmens geliefert.
	 * 
	 * @param index Index.
	 * @return {@code index}-ter Parameterwert.
	 * @throws IndexOutOfBoundsException Wenn für den gegebenen Index kein Parameterwert existiert.
	 */
	@Override
	public abstract FEMValue get(final int index) throws IndexOutOfBoundsException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Iterator<FEMValue> iterator() {
		return Iterators.itemsIterator(this, 0, this.size());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int hashCode() {
		return this.params().hashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof FEMFrame)) return false;
		final FEMFrame that = (FEMFrame)object;
		return this.params().equals(that.params());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void toScript(final FEM.ScriptFormatter target) throws IllegalArgumentException {
		target.putScope(this.params());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		return FEM.formatScope(this);
	}

}
