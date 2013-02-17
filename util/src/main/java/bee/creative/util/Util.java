package bee.creative.util;

import java.util.Comparator;

public class Util {

	public static Object filterBuilder() {

		return null;
	}
	
	public static Object builderBuilder() {

		return null;
	}


	public static Object iteratorBuilder() {

		return null;
	}

	public static Object iterableBuilder() {

		return null;
	}

	public static Object converterBuilder() {

		return null;
	}

	public static ComparatorBuilderUse comparatorBuilder() {
		return new ComparatorBuilderUseImpl();
	}

	public static Object comparableBuilder() {

		return null;
	}

	public static void main(final String[] args) {

System.out.println( //
	Util.comparatorBuilder() //
			.useNaturalComparator().asIs().create() //
			);
System.out.println( //
		Util.comparatorBuilder() //
			.useNaturalComparator().asReverseComparator().create() //
		);
System.out.println( //
		Util.comparatorBuilder() //
			.useStringAlphabeticalComparator().chained().with(Comparators.stringAlphanumericalComparator()).asIs().asReverseComparator().create() //
		);

	}

	public static interface ComparatorBuilderAs<V, P> extends ComparatorBuilderNavi<V, P>, ComparatorBuilderCommit<P> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public P asIs();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ComparatorBuilderClose<V, P> asNullComparator();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ComparatorBuilderClose<V, P> asReverseComparator();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ComparatorBuilderClose<Iterable<? extends V>, P> asIterableComparator();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public <N> ComparatorBuilderClose<N, P> asConvertedComparator(Converter<? super N, ? extends V> converter);

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ComparatorBuilderWith<V, ? extends ComparatorBuilderAs<V, P>> chained();

	}

	public static interface ComparatorBuilderUse {

		public <V> ComparatorBuilderAs<V, ? extends ComparatorBuilderCreate<V>> use(final Comparator<? super V> comparator);

		public ComparatorBuilderAs<Comparable<?>, ? extends ComparatorBuilderCreate<Comparable<?>>> useNaturalComparator();

		public ComparatorBuilderAs<String, ? extends ComparatorBuilderCreate<String>> useStringNumericalComparator();

		public ComparatorBuilderAs<String, ? extends ComparatorBuilderCreate<String>> useStringAlphabeticalComparator();

		public ComparatorBuilderAs<String, ? extends ComparatorBuilderCreate<String>> useStringAlphanumericalComparator();

		public ComparatorBuilderAs<Number, ? extends ComparatorBuilderCreate<Number>> useNumberLongComparator();

		public ComparatorBuilderAs<Number, ? extends ComparatorBuilderCreate<Number>> useNumberFloatComparator();

		public ComparatorBuilderAs<Number, ? extends ComparatorBuilderCreate<Number>> useNumberDoubleComparator();

		public ComparatorBuilderAs<Number, ? extends ComparatorBuilderCreate<Number>> useNumberIntegerComparator();

	}

	public static interface ComparatorBuilderNavi<V, P> extends ComparatorBuilderWrap<V, P>, ComparatorBuilderChain<V, P> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ComparatorBuilderNavi<V, P> asNullComparator();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ComparatorBuilderNavi<V, P> asReverseComparator();

		public ComparatorBuilderNavi<Iterable<? extends V>, P> asIterableComparator();

		public <N> ComparatorBuilderNavi<N, P> asConvertedComparator(Converter<? super N, ? extends V> converter);

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ComparatorBuilderWith<V, ? extends ComparatorBuilderNavi<V, P>> chained();

	}

	public static interface ComparatorBuilderClose<V, P> extends ComparatorBuilderNavi<V, P>, ComparatorBuilderCreate<V> {

	}

	public static interface ComparatorBuilderAnd<V, P> extends ComparatorBuilderWrap<V, P>, ComparatorBuilderCommit<P> {

		public ComparatorBuilderAnd<V, P> and(Comparator<? super V> comparator);

		/**
		 * {@inheritDoc}
		 */
		@Override
		public P asIs();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ComparatorBuilderAnd<V, P> asNullComparator();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ComparatorBuilderAnd<V, P> asReverseComparator();

	}

	public static interface ComparatorBuilderNext<V, P> extends ComparatorBuilderAnd<V, P>, ComparatorBuilderChain<V, P> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ComparatorBuilderAnd<V, P> and(Comparator<? super V> comparator);

		/**
		 * {@inheritDoc}
		 */
		@Override
		public P asIs();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ComparatorBuilderAnd<V, P> asNullComparator();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ComparatorBuilderAnd<V, P> asReverseComparator();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ComparatorBuilderWith<V, ? extends ComparatorBuilderNext<V, P>> chained();

	}

	public static interface ComparatorBuilderWith<V, P> {

		public ComparatorBuilderAnd<V, P> with(Comparator<? super V> comparator);

	}

	public static interface ComparatorBuilderWrap<V, P> {

		public ComparatorBuilderWrap<V, P> asNullComparator();

		public ComparatorBuilderWrap<V, P> asReverseComparator();

	}

	public static interface ComparatorBuilderChain<V, P> {

		public ComparatorBuilderWith<V, ? extends ComparatorBuilderChain<V, P>> chained();

	}

	/**
	 * Diese Schnittstelle definiert einen {@link Comparator}-{@link Builder}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <V> Typ der vom {@link Comparator} verarbeitet Werte.
	 */
	public static interface ComparatorBuilderCreate<V> extends Builder<Comparator<V>> {

		/**
		 * Diese Methode erzeugt einen {@link Comparator} und gibt ihn zurück.
		 * 
		 * @return {@link Comparator}.
		 */
		@Override
		public Comparator<V> create();

	}

	/**
	 * Diese Schnittstelle definiert eine Methode zur Bestätigung des bisher zusammengebauten {@link Comparator}s, die den aufrufenden {@code Parent} zurück gibt.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <P> Typ des {@code Parent}.
	 */
	public static interface ComparatorBuilderCommit<P> {

		/**
		 * Diese Methode Bestätigung des bisher zusammengebauten {@link Comparator} und gibt den {@code Parent} zurück.
		 * 
		 * @return {@code Parent}.
		 */
		public P asIs();

	}

	static class ComparatorBuilderUseImpl implements ComparatorBuilderUse {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public <V> ComparatorBuilderAs<V, ? extends ComparatorBuilderCreate<V>> use(final Comparator<? super V> comparator) {
			return new ComparatorBuilderAsCloseImpl<V, ComparatorBuilderCreateImpl<V>>(new ComparatorBuilderCreateImpl<V>(), comparator);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ComparatorBuilderAs<Comparable<?>, ? extends ComparatorBuilderCreate<Comparable<?>>> useNaturalComparator() {
			return this.use(Comparators.naturalComparator());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ComparatorBuilderAs<String, ? extends ComparatorBuilderCreate<String>> useStringNumericalComparator() {
			return this.use(Comparators.stringNumericalComparator());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ComparatorBuilderAs<String, ? extends ComparatorBuilderCreate<String>> useStringAlphabeticalComparator() {
			return this.use(Comparators.stringAlphabeticalComparator());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ComparatorBuilderAs<String, ? extends ComparatorBuilderCreate<String>> useStringAlphanumericalComparator() {
			return this.use(Comparators.stringAlphanumericalComparator());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ComparatorBuilderAs<Number, ? extends ComparatorBuilderCreate<Number>> useNumberLongComparator() {
			return this.use(Comparators.numberLongComparator());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ComparatorBuilderAs<Number, ? extends ComparatorBuilderCreate<Number>> useNumberFloatComparator() {
			return this.use(Comparators.numberFloatComparator());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ComparatorBuilderAs<Number, ? extends ComparatorBuilderCreate<Number>> useNumberDoubleComparator() {
			return this.use(Comparators.numberDoubleComparator());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ComparatorBuilderAs<Number, ? extends ComparatorBuilderCreate<Number>> useNumberIntegerComparator() {
			return this.use(Comparators.numberIntegerComparator());
		}

	}

	static class ComparatorBuilderCreateImpl<V> implements ComparatorBuilderCreate<V> {

		Comparator<? super V> comparator;

		@SuppressWarnings ("unchecked")
		@Override
		public Comparator<V> create() {
			if(this.comparator == null) throw new IllegalStateException();
			return (Comparator<V>)this.comparator;
		}

	}

	static class ComparatorBuilderCommitImpl<V, P extends ComparatorBuilderCreateImpl<V>> extends ComparatorBuilderCreateImpl<V> implements
		ComparatorBuilderCommit<P> {

		P parent;

		public ComparatorBuilderCommitImpl(final P parent, final Comparator<? super V> comparator) {
			this.parent = parent;
			this.comparator = comparator;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public P asIs() {
			this.parent.comparator = this.comparator;
			return this.parent;
		}

	}

	static class ComparatorBuilderAsCloseImpl<V, P extends ComparatorBuilderCreateImpl<V>> extends ComparatorBuilderCommitImpl<V, P> implements
		ComparatorBuilderAs<V, P>, ComparatorBuilderClose<V, P> {

		public ComparatorBuilderAsCloseImpl(final P parent, final Comparator<? super V> comparator) {
			super(parent, comparator);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ComparatorBuilderClose<V, P> asNullComparator() {
			return new ComparatorBuilderAsCloseImpl<V, P>(this.parent, Comparators.nullComparator(this.comparator));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ComparatorBuilderClose<V, P> asReverseComparator() {
			return new ComparatorBuilderAsCloseImpl<V, P>(this.parent, Comparators.reverseComparator(this.comparator));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ComparatorBuilderClose<Iterable<? extends V>, P> asIterableComparator() {
			return new ComparatorBuilderAsCloseImpl<Iterable<? extends V>, P>(this.parent, Comparators.iterableComparator(this.comparator));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public <N> ComparatorBuilderClose<N, P> asConvertedComparator(final Converter<? super N, ? extends V> converter) {
			return new ComparatorBuilderAsCloseImpl<N, P>(this.parent, Comparators.convertedComparator(converter, this.comparator));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ComparatorBuilderWith<V, ? extends ComparatorBuilderAsCloseImpl<V, P>> chained() {
			return new ComparatorBuilderAndNextWithImpl<V, ComparatorBuilderAsCloseImpl<V, P>>(this, null);
		}

	}

	static class ComparatorBuilderAndNextWithImpl<V, P extends ComparatorBuilderCreateImpl<V>> extends ComparatorBuilderCommitImpl<V, P> implements
		ComparatorBuilderAnd<V, P>, ComparatorBuilderNext<V, P>, ComparatorBuilderWith<V, P> {

		public ComparatorBuilderAndNextWithImpl(final P parent, final Comparator<? super V> comparator) {
			super(parent, comparator);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public P asIs() {
			this.parent.comparator = Comparators.chainedComparator(this.parent.comparator, this.comparator);
			return this.parent;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ComparatorBuilderAnd<V, P> asNullComparator() {
			return new ComparatorBuilderAndNextWithImpl<V, P>(this.parent, Comparators.nullComparator(this.comparator));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ComparatorBuilderAnd<V, P> asReverseComparator() {
			return new ComparatorBuilderAndNextWithImpl<V, P>(this.parent, Comparators.reverseComparator(this.comparator));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ComparatorBuilderAnd<V, P> and(final Comparator<? super V> comparator) {
			if(comparator == null) throw new NullPointerException();
			this.asIs();
			return this.with(comparator);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ComparatorBuilderAnd<V, P> with(final Comparator<? super V> comparator) {
			if(comparator == null) throw new NullPointerException();
			return new ComparatorBuilderAndNextWithImpl<V, P>(this.parent, comparator);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ComparatorBuilderWith<V, ? extends ComparatorBuilderNext<V, P>> chained() {
			return new ComparatorBuilderAndNextWithImpl<V, ComparatorBuilderAndNextWithImpl<V, P>>(this, null);
		}

	}

}
