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

	/** Dieses Feld speichert den leeren Stapelrahmen, der keine Parameterwerte bereitstellt, das Kontextobjekt {@link FEMContext#EMPTY} verwendet und sich selbst
	 * als {@link #parent()} nutzt. */
	public static final FEMFrame EMPTY = new ArrayFrame();

	/** Dieses Feld speichert eine Funktion, deren Ergebniswert einer Sicht auf die Parameterwerte des Stapelrahmens {@code frame} entspricht, d.h.
	 * {@code frame.params()}.
	 *
	 * @see FEMFrame#params() */
	public static final FEMFunction FUNCTION = new FEMFunction() {

		@Override
		public FEMValue invoke(FEMFrame frame) {
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
	public static FEMFrame from(FEMContext context) throws NullPointerException {
		return FEMFrame.EMPTY.withContext(context);
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
		return new ArrayFrame(this, FEMArray.EMPTY, this.context());
	}

	/** Diese Methode gibt einen neuen Stapelrahmen zurück, welcher die gegebenen {@link #params() zugesicherten Parameterwerte} besitzt, das {@link #context()
	 * Kontextobjekt} dieses Stapelrahmens übernimmt und diesen als {@link #parent() übergeordneten Stapelrahmen} nutzt.
	 *
	 * @param params zugesicherte Parameterwerte.
	 * @return neuer Stapelrahmen.
	 * @throws NullPointerException Wenn {@code params} {@code null} ist. */
	public final FEMFrame newFrame(FEMArray params) throws NullPointerException {
		return new ArrayFrame(this, params.length == 0 ? FEMArray.EMPTY : params, this.context());
	}

	/** Diese Methode ist eine Abkürzung für {@link #newFrame(FEMArray) this.newFrame(FEMArray.from(params))}.
	 *
	 * @see FEMArray#from(FEMValue...) */
	public final FEMFrame newFrame(FEMValue... params) throws NullPointerException {
		return this.newFrame(FEMArray.from(params));
	}

	/** Diese Methode ist eine Abkürzung für {@link #newFrame(FEMArray) this.newFrame(FEMArray.from(params))}.
	 *
	 * @see FEMArray#from(Iterable) */
	public final FEMFrame newFrame(Iterable<? extends FEMValue> params) throws NullPointerException {
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
	public final FEMFrame newFrame(FEMFunction[] params) throws NullPointerException {
		return new InvokeFrame(this, params, this.context());
	}

	/** Diese Methode gibt diesen Stapelrahmen mit den gegebenen {@link #params() zugesicherten Parameterwerten} zurück. Sie ist eine Abkürzung für
	 * {@code this.parent().newFrame(params).withContext(this.context())}.
	 *
	 * @see #newFrame(FEMArray)
	 * @see #withContext(FEMContext)
	 * @param params zugesicherte Parameterwerte.
	 * @return neuer Stapelrahmen.
	 * @throws NullPointerException Wenn {@code params} {@code null} ist. */
	public final FEMFrame withParams(FEMArray params) throws NullPointerException {
		return new ArrayFrame(this.parent(), params.length == 0 ? FEMArray.EMPTY : params, this.context());
	}

	/** Diese Methode ist eine Abkürzung für {@link #withParams(FEMArray) this.withParams(FEMArray.from(params))}.
	 *
	 * @see FEMArray#from(FEMValue...) */
	public final FEMFrame withParams(FEMValue... params) throws NullPointerException {
		return this.withParams(FEMArray.from(params));
	}

	/** Diese Methode ist eine Abkürzung für {@link #withParams(FEMArray) this.withParams(FEMArray.from(params))}.
	 *
	 * @see FEMArray#from(Iterable) */
	public final FEMFrame withParams(Iterable<? extends FEMValue> params) throws NullPointerException {
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
	public final FEMFrame withParams(FEMFunction[] params) throws NullPointerException {
		return new InvokeFrame(this.parent(), params, this.context());
	}

	/** Diese Methode gibt diesen Stapelrahmen ohne {@link #params() zugesicherte Parameterwerte} zurück. Sie ist eine Abkürzung für
	 * {@code this.parent().newFrame().withContext(this.context())}.
	 *
	 * @see #newFrame()
	 * @see #withContext(FEMContext)
	 * @return neuer Stapelrahmen. */
	public final FEMFrame withoutParams() {
		return new ArrayFrame(this.parent(), FEMArray.EMPTY, this.context());
	}

	/** Diese Methode gibt diesen Stapelrahmen mit dem gegebenen {@link #context() Kontextobjekt} zurück.
	 *
	 * @param context Kontextobjekt.
	 * @return neuer Stapelrahmen.
	 * @throws NullPointerException Wenn {@code context} {@code null} ist. */
	public abstract FEMFrame withContext(FEMContext context) throws NullPointerException;

	/** Diese Methode gibt den Wert des {@code index}-ten Parameters zurück. Über die {@link #size() Anzahl der zugesicherten Parameterwerte} hinaus, können auch
	 * zusätzliche Parameterwerte des {@link #parent() übergeordneten Stapelrahmens} bereitgestellt werden. Genauer wird für einen {@code index >= this.size()}
	 * der Parameterwert {@code this.parent().get(index - this.size())} des übergeordneten Stapelrahmens geliefert.
	 *
	 * @param index Index.
	 * @return {@code index}-ter Parameterwert.
	 * @throws IndexOutOfBoundsException Wenn für den gegebenen Index kein Parameterwert existiert. */
	@Override
	public abstract FEMValue get(int index) throws IndexOutOfBoundsException;

	/** Diese Methode liefert die {@link FEMFunction Funktion} zur Berechnung des {@code index}-ten {@link #size() zugesicherten} Parameterwerts.
	 *
	 * @param index Index.
	 * @return Funktion zur Berechnung des {@code index}-ter Parameterwerts.
	 * @throws IndexOutOfBoundsException Wenn für den gegebenen Index kein Parameterwert existiert. */
	public abstract FEMFunction param(int index) throws IndexOutOfBoundsException;

	@Override
	public final String toString() {
		final var res = new FEMPrinter();
		FEMDomain.DEFAULT.printFrame(res, this.params());
		return res.print();
	}

	/** Dieses Feld speichert die übergeordneten Parameterdaten. */
	private final FEMFrame parent;

	/** Dieses Feld speichert das Kontextobjekt. */
	private final FEMContext context;

	private FEMFrame() {
		this.parent = this;
		this.context = FEMContext.EMPTY;
	}

	private FEMFrame(FEMFrame parent, FEMContext context) {
		this.parent = parent;
		this.context = context;
	}

	private static class ArrayFrame extends FEMFrame {

		@Override
		public FEMValue get(int index) throws IndexOutOfBoundsException {
			final var index2 = index - this.params.length;
			return index2 >= 0 ? this.parent().get(index2) : this.params.customGet(index);
		}

		@Override
		public FEMFunction param(int index) throws IndexOutOfBoundsException {
			return this.params.get(index);
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
		public FEMFrame withContext(FEMContext context) throws NullPointerException {
			if (this.context() == context) return this;
			return new ArrayFrame(this.parent(), this.params, Objects.notNull(context));
		}

		private final FEMArray params;

		private ArrayFrame() {
			this.params = FEMArray.EMPTY;
		}

		private ArrayFrame(FEMFrame parent, FEMArray params, FEMContext context) {
			super(parent, context);
			this.params = params;
		}

	}

	private static class InvokeFrame extends FEMFrame {

		@Override
		public FEMValue get(int index) throws IndexOutOfBoundsException {
			final var index2 = index - this.params.length;
			return index2 >= 0 ? this.parent().get(index2) : this.params.frameGet(index);
		}

		@Override
		public FEMFunction param(int index) throws IndexOutOfBoundsException {
			if ((index < 0) || (index >= this.params.length)) throw new IndexOutOfBoundsException();
			return this.params.functions[index];
		}

		@Override
		public int size() {
			return this.params.length;
		}

		@Override
		public InvokeParams params() {
			return this.params;
		}

		@Override
		public FEMFrame withContext(FEMContext context) throws NullPointerException {
			if (this.context() == context) return this;
			return new InvokeFrame(this.parent(), this.params, Objects.notNull(context));
		}

		private final InvokeParams params;

		private InvokeFrame(FEMFrame parent, InvokeParams params, FEMContext context) {
			super(parent, context);
			this.params = params;
		}

		private InvokeFrame(FEMFrame parent, FEMFunction[] params, FEMContext context) {
			super(parent, context);
			this.params = new InvokeParams(parent, params);
		}

	}

	private static class InvokeParams extends HashArray {

		@Override
		protected FEMValue customGet(int index) {
			synchronized (this.values) {
				var result = this.values[index];
				if (result != null) return result;
				result = this.functions[index].toFuture(this.frame);
				this.values[index] = Objects.notNull(result);
				return result;
			}
		}

		private final FEMFrame frame;

		private final FEMValue[] values;

		private final FEMFunction[] functions;

		private InvokeParams(FEMFrame frame, FEMFunction[] params) {
			super(params.length);
			this.frame = frame;
			this.functions = params;
			this.values = new FEMValue[params.length];
		}

		private FEMValue frameGet(int index) {
			synchronized (this.values) {
				var result = this.values[index];
				if (result != null) return result;
				result = this.functions[index].invoke(this.frame);
				this.values[index] = Objects.notNull(result);
				return result;
			}
		}

	}

}
