package bee.creative.fem;

import java.util.Iterator;
import bee.creative.fem.FEMArray.HashArray;
import bee.creative.lang.Array2;
import bee.creative.lang.Objects;
import bee.creative.lang.Objects.UseToString;
import bee.creative.util.Iterators;

/** Diese Klasse implementiert einen Stapelrahmen ({@code stack-frame}), über welchen einer Funktion eine Liste von Parameterwerten sowie ein Kontextobjekt zur
 * Verfügung gestellt werden. Über die {@link #size() Anzahl der zugesicherten Parameterwerte} hinaus können von der Methode {@link #get(int)} auch zusätzliche
 * Parameterwerte aus dem {@link #parent() übergeordneten Stapelrahmen} bereitgestellt werden. Die Methode {@link #get(int)} liefert für einen gegebenen Index
 * immer den gleichen Wert bzw. löst immer die gleiche Ausnahme aus. Analoges gilt für die Methoden {@link #size()}, {@link #params()} und {@link #context()}.
 *
 * @see FEMValue
 * @see FEMFunction
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public abstract class FEMFrame implements Array2<FEMValue>, UseToString {

	@SuppressWarnings ("javadoc")
	public static class ArrayFrame extends FEMFrame {

		public final FEMArray params;

		ArrayFrame(final FEMFrame parent, final FEMArray params, final FEMContext context) {
			super(parent, context);
			this.params = params;
		}

		@Override
		public FEMValue get(final int index) throws IndexOutOfBoundsException {
			final int index2 = index - this.params.length;
			return index2 >= 0 ? this.parent.get(index2) : this.params.customGet(index);
		}

		@Override
		public int size() {
			return this.params.length;
		}

		@Override
		public FEMArray params() {
			return this.params;
		}

		@Override
		public FEMFrame withContext(final FEMContext context) throws NullPointerException {
			if (this.context == context) return this;
			return new ArrayFrame(this.parent, this.params, Objects.notNull(context));
		}

	}

	@SuppressWarnings ("javadoc")
	public static class EmptyFrame extends FEMFrame {

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
			if (this.context == context) return this;
			return new ArrayFrame(this, FEMArray.EMPTY, Objects.notNull(context));
		}

	}

	@SuppressWarnings ("javadoc")
	public static class InvokeFrame extends FEMFrame {

		public final InvokeParams params;

		InvokeFrame(final FEMFrame parent, final InvokeParams params, final FEMContext context) {
			super(parent, context);
			this.params = params;
		}

		InvokeFrame(final FEMFrame parent, final FEMFunction[] params, final FEMContext context) {
			super(parent, context);
			this.params = new InvokeParams(parent, params);
		}

		@Override
		public FEMValue get(final int index) throws IndexOutOfBoundsException {
			final int index2 = index - this.params.length;
			return index2 >= 0 ? this.parent.get(index2) : this.params.frameGet(index);
		}

		@Override
		public int size() {
			return this.params.length;
		}

		@Override
		public FEMArray params() {
			return this.params;
		}

		@Override
		public FEMFrame withContext(final FEMContext context) throws NullPointerException {
			if (this.context == context) return this;
			return new InvokeFrame(this.parent, this.params, Objects.notNull(context));
		}

	}

	@SuppressWarnings ("javadoc")
	public static class InvokeParams extends HashArray {

		public final FEMFrame frame;

		/** Dieses Feld speichert das Array der Parameterwerte, das nicht verändert werden darf. */
		final FEMValue[] values;

		/** Dieses Feld speichert das Array der Parameterfunktionen, das nicht verändert werden darf. */
		final FEMFunction[] functions;

		InvokeParams(final FEMFrame frame, final FEMFunction[] params) {
			super(params.length);
			this.frame = frame;
			this.functions = params;
			this.values = new FEMValue[params.length];
		}

		FEMValue frameGet(final int index) {
			synchronized (this.values) {
				FEMValue result = this.values[index];
				if (result != null) return result;
				result = this.functions[index].invoke(this.frame);
				this.values[index] = Objects.notNull(result);
				return result;
			}
		}

		@Override
		protected FEMValue customGet(final int index) {
			synchronized (this.values) {
				FEMValue result = this.values[index];
				if (result != null) return result;
				result = this.functions[index].toFuture(this.frame);
				this.values[index] = Objects.notNull(result);
				return result;
			}
		}

	}

	/** Dieses Feld speichert den leeren Stapelrahmen, der keine Parameterwerte bereitstellt, das Kontextobjekt {@link FEMContext#EMPTY} verwendet und sich selbst
	 * als {@link #parent()} nutzt. */
	public static final FEMFrame EMPTY = new EmptyFrame();

	/** Dieses Feld speichert eine Funktion, deren Ergebniswert einer Sicht auf die Parameterwerte des Stapelrahmens {@code frame} entspricht, d.h.
	 * {@code frame.params()}.
	 *
	 * @see FEMFrame#params() */
	public static final FEMFunction FUNCTION = new FEMFunction() {

		@Override
		public FEMValue invoke(final FEMFrame frame) {
			return frame.params();
		}

		@Override
		public String toString() {
			return "$";
		}

	};

	/** Diese Methode gibt einen Stapelrahmen mit dem gegebenen {@link #context() Kontextobjekt} zurück. Sie ist eine Abkürzung für
	 * {@code EMPTY.withContext(context)}.
	 *
	 * @param context Kontextobjekt.
	 * @return neue Stapelrahmen.
	 * @throws NullPointerException Wenn {@code context} {@code null} ist. */
	public static FEMFrame from(final FEMContext context) throws NullPointerException {
		return FEMFrame.EMPTY.withContext(context);
	}

	/** Dieses Feld speichert die übergeordneten Parameterdaten. */
	protected final FEMFrame parent;

	/** Dieses Feld speichert das Kontextobjekt. */
	protected final FEMContext context;

	FEMFrame() {
		this.parent = this;
		this.context = FEMContext.EMPTY;
	}

	@SuppressWarnings ("javadoc")
	protected FEMFrame(final FEMFrame parent, final FEMContext context) {
		this.parent = parent;
		this.context = context;
	}

	/** Diese Methode gibt die Anzahl der Parameterwerte zurück, die zur Verwendung durch eine aufgerufene Funktion bestimmt sind. Über die Methode
	 * {@link #get(int)} werden mindestens so viele Parameterwerte bereitgestellt.
	 *
	 * @return Anzahl der zugesicherten Parameterwert. */
	@Override
	public abstract int size();

	/** Diese Methode gibt die übergeordneten Parameterdaten zurück.
	 *
	 * @return übergeordnete Parameterdaten oder {@code this}. */
	public final FEMFrame parent() {
		return this.parent;
	}

	/** Diese Methode gibt eine Wertliste als Sicht auf die zugesicherten Parameterwerte zurück. Die Elemente dieser Wertliste können der {@link FEMFuture
	 * <em>return-by-reference</em>}-Semantik angehören.
	 *
	 * @see #get(int)
	 * @see #size()
	 * @return {@link FEMArray} der Parameterwerte. */
	public abstract FEMArray params();

	/** Diese Methode gibt das Kontextobjekt zurück.< Funktionen können aus diesem Objekt Informationen für ihre Berechnungen extrahieren oder auch den Zustand
	 * dieses Objekts modifizieren. Das Kontextobjekt entspricht dem Kontext {@code this} in {@code Java}-Methoden.
	 *
	 * @return Kontextobjekt. */
	public final FEMContext context() {
		return this.context;
	}

	/** Diese Methode gibt einen neuen Stapelrahmen zurück, welcher keine {@link #params() zugesicherten Parameterwerte} besitzt, das {@link #context()
	 * Kontextobjekt} dieses Stapelrahmens übernimmt und diesen als {@link #parent() übergeordneten Stapelrahmen} nutzt.
	 *
	 * @return neuer Stapelrahmen. */
	public final FEMFrame newFrame() {
		return new ArrayFrame(this, FEMArray.EMPTY, this.context);
	}

	/** Diese Methode gibt einen neuen Stapelrahmen zurück, welcher die gegebenen {@link #params() zugesicherten Parameterwerte} besitzt, das {@link #context()
	 * Kontextobjekt} dieses Stapelrahmens übernimmt und diesen als {@link #parent() übergeordneten Stapelrahmen} nutzt.
	 *
	 * @param params zugesicherte Parameterwerte.
	 * @return neuer Stapelrahmen.
	 * @throws NullPointerException Wenn {@code params} {@code null} ist. */
	public final FEMFrame newFrame(final FEMArray params) throws NullPointerException {
		return new ArrayFrame(this, params.length == 0 ? FEMArray.EMPTY : params, this.context);
	}

	/** Diese Methode ist eine Abkürzung für {@link #newFrame(FEMArray) this.newFrame(FEMArray.from(params))}.
	 *
	 * @see FEMArray#from(FEMValue...) */
	public final FEMFrame newFrame(final FEMValue... params) throws NullPointerException {
		return this.newFrame(FEMArray.from(params));
	}

	/** Diese Methode ist eine Abkürzung für {@link #newFrame(FEMArray) this.newFrame(FEMArray.from(params))}.
	 *
	 * @see FEMArray#from(Iterable) */
	public final FEMFrame newFrame(final Iterable<? extends FEMValue> params) throws NullPointerException {
		return this.newFrame(FEMArray.from(params));
	}

	/** Diese Methode gibt einen neuen Stapelrahmen zurück, welcher die gegebenen Parameterfunktionen zur Berechnung der {@link #params() zugesicherten
	 * Parameterwerte} verwendet, das {@link #context() Kontextobjekt} dieses Stapelrahmens übernimmt und diesen als {@link #parent() übergeordneten Stapelrahmen}
	 * nutzt.
	 * <p>
	 * Die zugesicherten Parameterwerte werden mit Hilfe dieses Stapelrahmens und der gegebener Parameterfunktionen ermittelt. Eine Parameterfunktion wird zur
	 * Ermittlung eines Parameterwerts einmalig mit diesem Stapelrahmen {@link FEMFunction#invoke(FEMFrame) ausgewertet}. Genauer entspricht der {@code index}-te
	 * zugesicherte Parameterwert dem Ergebnis von {@code params[index].invoke(this)}. Der Ergebniswert wird zur Wiederverwendung zwischengespeichert. Die über
	 * {@link #params()} bereitgestellte Liste der Parameterwerte des erzeugten Stapelrahmen liefert die noch nicht über {@link #get(int)} ermittelten
	 * Parameterwerte als {@link FEMFuture}.
	 *
	 * @param params Parameterfunktionen zur Berechnung der zugesicherten Parameterwerte.
	 * @return neuer Stapelrahmen.
	 * @throws NullPointerException Wenn {@code params} {@code null} ist. */
	public final FEMFrame newFrame(final FEMFunction[] params) throws NullPointerException {
		return new InvokeFrame(this, params, this.context);
	}

	/** Diese Methode gibt diesen Stapelrahmen mit den gegebenen {@link #params() zugesicherten Parameterwerten} zurück. Sie ist eine Abkürzung für
	 * {@code this.parent().newFrame(params).withContext(this.context())}.
	 *
	 * @see #newFrame(FEMArray)
	 * @see #withContext(FEMContext)
	 * @param params zugesicherte Parameterwerte.
	 * @return neuer Stapelrahmen.
	 * @throws NullPointerException Wenn {@code params} {@code null} ist. */
	public final FEMFrame withParams(final FEMArray params) throws NullPointerException {
		return new ArrayFrame(this.parent, params.length == 0 ? FEMArray.EMPTY : params, this.context);
	}

	/** Diese Methode ist eine Abkürzung für {@link #withParams(FEMArray) this.withParams(FEMArray.from(params))}.
	 *
	 * @see FEMArray#from(FEMValue...) */
	public final FEMFrame withParams(final FEMValue... params) throws NullPointerException {
		return this.withParams(FEMArray.from(params));
	}

	/** Diese Methode ist eine Abkürzung für {@link #withParams(FEMArray) this.withParams(FEMArray.from(params))}.
	 *
	 * @see FEMArray#from(Iterable) */
	public final FEMFrame withParams(final Iterable<? extends FEMValue> params) throws NullPointerException {
		return this.withParams(FEMArray.from(params));
	}

	/** Diese Methode gibt diesen Stapelrahmen mit den gegebenen Parameterfunktionen zur Berechnung der {@link #params() zugesicherten Parameterwerte} zurück. Sie
	 * ist eine Abkürzung für {@code this.parent().newFrame(params).withContext(this.context())}.
	 *
	 * @see #newFrame(FEMFunction...)
	 * @see #withContext(FEMContext)
	 * @param params zugesicherte Parameterwerte.
	 * @return neuer Stapelrahmen.
	 * @throws NullPointerException Wenn {@code params} {@code null} ist. */
	public final FEMFrame withParams(final FEMFunction[] params) throws NullPointerException {
		return new InvokeFrame(this.parent, params, this.context);
	}

	/** Diese Methode gibt diesen Stapelrahmen ohne {@link #params() zugesicherte Parameterwerte} zurück. Sie ist eine Abkürzung für
	 * {@code this.parent().newFrame().withContext(this.context())}.
	 *
	 * @see #newFrame()
	 * @see #withContext(FEMContext)
	 * @return neuer Stapelrahmen. */
	public final FEMFrame withoutParams() {
		return new ArrayFrame(this.parent, FEMArray.EMPTY, this.context);
	}

	/** Diese Methode gibt diesen Stapelrahmen mit dem gegebenen {@link #context() Kontextobjekt} zurück.
	 *
	 * @param context Kontextobjekt.
	 * @return neuer Stapelrahmen.
	 * @throws NullPointerException Wenn {@code context} {@code null} ist. */
	public abstract FEMFrame withContext(final FEMContext context) throws NullPointerException;

	/** Diese Methode gibt den Wert des {@code index}-ten Parameters zurück. Über die {@link #size() Anzahl der zugesicherten Parameterwerte} hinaus, können auch
	 * zusätzliche Parameterwerte des {@link #parent() übergeordneten Stapelrahmens} bereitgestellt werden. Genauer wird für einen {@code index >= this.size()}
	 * der Parameterwert {@code this.parent().get(index - this.size())} des übergeordneten Stapelrahmens geliefert.
	 *
	 * @param index Index.
	 * @return {@code index}-ter Parameterwert.
	 * @throws IndexOutOfBoundsException Wenn für den gegebenen Index kein Parameterwert existiert. */
	@Override
	public abstract FEMValue get(final int index) throws IndexOutOfBoundsException;

	@Override
	public final Iterator<FEMValue> iterator() {
		return Iterators.fromItems(this, 0, this.size());
	}

	@Override
	public final String toString() {
		final FEMPrinter res = new FEMPrinter();
		FEMDomain.DEFAULT.printFrame(res, this.params());
		return res.print();
	}

}
