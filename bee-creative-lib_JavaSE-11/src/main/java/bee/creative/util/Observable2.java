package bee.creative.util;

/** Diese Schnittstelle definiert ein {@link Observable}, dessen Methoden an ein {@link #observers()} delegieren.
 *
 * @author [cc-by] 2025 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <M> Typ des Ereignisses bzw. der Ereignisnachricht.
 * @param <O> Typ der Ereignisempfänger. Dieser darf kein {@code Object[]} sein. */
public interface Observable2<M, O> extends Observable<M, O> {

	/** {@inheritDoc} Sie realisiert {@link Observers#put(Object, Object) this.observers().put(this, observer)} */
	@Override
	default <O2 extends O> O2 put(O2 observer) throws IllegalArgumentException {
		return this.observers().put(this, observer);
	}

	/** {@inheritDoc} Sie realisiert {@link Observers#putWeak(Object, Object) this.observers().putWeak(this, observer)} */
	@Override
	default <O2 extends O> O2 putWeak(O2 observer) throws IllegalArgumentException {
		return this.observers().putWeak(this, observer);
	}

	/** {@inheritDoc} Sie realisiert {@link Observers#pop(Object, Object) this.observers().pop(this, observer)} */
	@Override
	default void pop(Object observer) throws IllegalArgumentException {
		this.observers().pop(this, observer);
	}

	/** {@inheritDoc} Sie realisiert {@link Observers#fire(Object, Object) this.observers().fire(this, message)} */
	@Override
	default M fire(M message) throws NullPointerException, UnsupportedOperationException {
		return this.observers().fire(this, message);
	}

	/** Diese Methode liefert das {@link Observers}, an das die von {@link Observable} geerbten Methoden delegieren. */
	Observers<M, O> observers();

}
