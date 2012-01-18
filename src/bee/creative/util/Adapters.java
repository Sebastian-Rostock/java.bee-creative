package bee.creative.util;

import java.util.Iterator;
import bee.creative.util.Builders.BuilderLink;
import bee.creative.util.Converters.ConverterLink;
import bee.creative.util.Filters.FilterLink;

public class Adapters {

	/**
	 * Diese Klasse implementiert einen {@link Converter Converter}, der seine Eingabe mit Hilfe eines gegebenen
	 * {@link Filter Filters} in seine Ausgabe überführt.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 */
	static final class FilterConverterAdapter<GInput> extends FilterLink<GInput> implements Converter<GInput, Boolean> {

		/**
		 * Dieser Konstrukteur initialisiert den {@link Filter Filter}.
		 * 
		 * @param filter {@link Filter Filter}.
		 * @throws NullPointerException Wenn der gegebene {@link Filter Filter} {@code null} ist.
		 */
		public FilterConverterAdapter(final Filter<? super GInput> filter) throws NullPointerException {
			super(filter);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Boolean convert(final GInput input) {
			return Boolean.valueOf(this.filter.accept(input));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof FilterConverterAdapter<?>)) return false;
			return super.equals(object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("filterConverterAdapter", this.filter);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Filter Filter}, der seine Eingabeakzeptanz mit Hilfe eines gegebenen
	 * {@link Converter Converters} bestimmt.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 */
	static final class ConverterFilterAdapter<GInput> extends ConverterLink<GInput, Boolean> implements Filter<GInput> {

		/**
		 * Dieser Konstrukteur initialisiert den {@link Converter Converter}.
		 * 
		 * @param converter {@link Converter Converter}.
		 * @throws NullPointerException Wenn der gegebene {@link Converter Converter} {@code null} ist.
		 */
		public ConverterFilterAdapter(final Converter<? super GInput, Boolean> converter) throws NullPointerException {
			super(converter);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean accept(final GInput input) {
			return this.converter.convert(input).booleanValue();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof ConverterFilterAdapter<?>)) return false;
			return super.equals(object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("converterFilterAdapter", this.converter);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Iterable Iterable}, der seinen {@link Iterator Iterator} mit Hilfe eines
	 * gegebenen {@link Builder Builders} erzeugt.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente.
	 */
	static abstract class BuilderIterableAdapter<GEntry> extends BuilderLink<Iterator<GEntry>> implements
		Iterable<GEntry> {

		/**
		 * Dieser Konstrukteur initialisiert den {@link Builder Builder}.
		 * 
		 * @param builder {@link Builder Builder}.
		 * @throws NullPointerException Wenn der gegebene {@link Builder Builder} {@code null} ist.
		 */
		public BuilderIterableAdapter(final Builder<? extends Iterator<GEntry>> builder) throws NullPointerException {
			super(builder);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<GEntry> iterator() {
			return this.builder.build();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof BuilderIterableAdapter<?>)) return false;
			return super.equals(object);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Builder Builder}, der seinen Datensatz mit Hilfe eines gegebenen
	 * {@link Iterable Iterables} erzeugt.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente.
	 */
	static abstract class IterableBuilderAdapter<GEntry> implements Builder<Iterator<GEntry>> {

		/**
		 * Dieses Feld speichert den {@link Iterable Iterable}.
		 */
		final Iterable<GEntry> iterable;

		/**
		 * Dieser Konstrukteur initialisiert den {@link Iterable Iterable}.
		 * 
		 * @param iterable {@link Iterable Iterable}.
		 * @throws NullPointerException Wenn der gegebene {@link Iterable Iterable} {@code null} ist.
		 */
		public IterableBuilderAdapter(final Iterable<GEntry> iterable) throws NullPointerException {
			if(iterable == null) throw new NullPointerException("Iterable is null");
			this.iterable = iterable;
		}

		/**
		 * Diese Methode gibt den {@link Iterable Iterable} zurück.
		 * 
		 * @return {@link Iterable Iterable}.
		 */
		public Iterable<? extends GEntry> iterable() {
			return this.iterable;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<GEntry> build() {
			return this.iterable.iterator();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return Objects.hash(this.iterable);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof IterableBuilderAdapter)) return false;
			final IterableBuilderAdapter<?> data = (IterableBuilderAdapter<?>)object;
			return Objects.equals(this.iterable, data.iterable);
		}

	}

	public static final class ConverterComparableAdapter<GInput> extends ConverterLink<GInput, Number> implements
		Comparable<GInput> {

		/**
		 * Dieser Konstrukteur initialisiert den {@link Converter Converter}.
		 * 
		 * @param converter {@link Converter Converter}.
		 * @throws NullPointerException Wenn der gegebene {@link Converter Converter} {@code null} ist.
		 */
		public ConverterComparableAdapter(final Converter<? super GInput, ? extends Number> converter) {
			super(converter);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int compareTo(final GInput input) {
			return this.converter.convert(input).intValue();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof ConverterComparableAdapter<?>)) return false;
			return super.equals(object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("converterComparableAdapter", this.converter);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Converter Converter}, der seine Eingabe mit Hilfe eines gegebenen
	 * {@link Comparable Comparables} in seine Ausgabe überführt.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 */
	static final class ComparableConverterAdapter<GInput> extends Comparables.ComparableLink<GInput> implements
		Converter<GInput, Integer> {

		/**
		 * Dieser Konstrukteur initialisiert den {@link Comparable Comparable}.
		 * 
		 * @param comparable {@link Comparable Comparable}.
		 * @throws NullPointerException Wenn der gegebene {@link Comparable Comparable} {@code null} ist.
		 */
		public ComparableConverterAdapter(final Comparable<? super GInput> comparable) throws NullPointerException {
			super(comparable);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Integer convert(final GInput input) {
			return Integer.valueOf(this.comparable.compareTo(input));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof ComparableConverterAdapter<?>)) return false;
			return super.equals(object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("comparableConverter", this.comparable);
		}

	}

	/**
	 * Diese Methode erzeugt einen {@link Converter Converter}, der seine Eingabe mit Hilfe des gegebenen {@link Filter
	 * Filters} in seine Ausgabe überführt, und gibt ihn zurück.
	 * 
	 * @see Converter
	 * @param <GInput> Typ der Eingabe.
	 * @param filter {@link Filter Filter}.
	 * @return {@link FilterConverterAdapter Filter-Converter}.
	 * @throws NullPointerException Wenn der gegebene {@link Filter Filter} {@code null} ist.
	 */
	public static <GInput> Converter<GInput, Boolean> filterConverter(final Filter<? super GInput> filter)
		throws NullPointerException {
		return new FilterConverterAdapter<GInput>(filter);
	}

	/**
	 * Diese Methode erzeugt einen {@link Converter Converter}, der seine Eingabe mit Hilfe des gegebenen
	 * {@link Comparable Comparables} in seine Ausgabe überführt, und gibt ihn zurück.
	 * 
	 * @see Converter
	 * @param <GInput> Typ der Eingabe.
	 * @param comparable {@link Comparable Comparable}.
	 * @return {@link ComparableConverterAdapter Comparable-Converter}.
	 * @throws NullPointerException Wenn der gegebene {@link Comparable Comparable} {@code null} ist.
	 */
	public static <GInput> Converter<GInput, Integer> comparableConverter(final Comparable<? super GInput> comparable)
		throws NullPointerException {
		return new ComparableConverterAdapter<GInput>(comparable);
	}

}
