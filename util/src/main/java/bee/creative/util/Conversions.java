package bee.creative.util;

import bee.creative.util.Converters.AbstractConverter;
import bee.creative.util.Converters.AbstractDelegatingConverter;

/**
 * Diese Klasse implementiert Hilfsmethoden und Hilfsklassen zur Konstruktion von {@link Conversion}s.
 * 
 * @see Converter
 * @see Converters
 * @see Conversion
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class Conversions {

	/**
	 * Diese Klasse implementiert eine abstracte {@link Conversion}.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 */
	static abstract class AbstractConversion<GInput, GOutput> implements Conversion<GInput, GOutput> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return Objects.hash(this.output());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof Conversion<?, ?>)) return false;
			final Conversion<?, ?> data = (Conversion<?, ?>)object;
			return Objects.equals(this.output(), data.output());
		}

	}

	/**
	 * Diese Klasse implementiert eine statische {@link Conversion}, deren Ein- und Ausgabe konstant sind.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 */
	public static final class StaticConversion<GInput, GOutput> extends AbstractConversion<GInput, GOutput> {

		/**
		 * Dieses Feld speichert die Eingabe.
		 */
		final GInput input;

		/**
		 * Dieses Feld speichert die Ausgeb.
		 */
		final GOutput output;

		/**
		 * Dieser Konstruktor initialisiert Eingabe und Ausgabe.
		 * 
		 * @param input Eingabe.
		 * @param output Ausgabe.
		 */
		public StaticConversion(final GInput input, final GOutput output) {
			this.input = input;
			this.output = output;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GInput input() {
			return this.input;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GOutput output() {
			return this.output;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this.input, this.output);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Converter}, der seine Eingabe in eine {@link StaticConversion} überführt, deren Ausgabe mit einem gegebenen {@link Converter} ermittelt wird.
	 * 
	 * @see StaticConversion
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 */
	public static final class StaticConversionConverter<GInput, GOutput> extends
		AbstractDelegatingConverter<GInput, Conversion<GInput, GOutput>, GInput, GOutput> {

		/**
		 * Dieser Konstruktor initialisiert den {@link Converter}.
		 * 
		 * @param converter {@link Converter}.
		 * @throws NullPointerException Wenn der gegebene {@link Converter} {@code null} ist.
		 */
		public StaticConversionConverter(final Converter<? super GInput, ? extends GOutput> converter) throws NullPointerException {
			super(converter);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Conversion<GInput, GOutput> convert(final GInput input) {
			return new StaticConversion<GInput, GOutput>(input, this.converter.convert(input));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof StaticConversionConverter<?, ?>)) return false;
			return super.equals(object);
		}

	}

	/**
	 * Diese Klasse implementiert eine inverse {@link Conversion}, deren Ein- und Ausgabe aus der Aus- bzw. Eingabe einer gegebenen {@link Conversion} ermittelt werden.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 */
	public static final class InverseConversion<GInput, GOutput> extends AbstractConversion<GInput, GOutput> {

		/**
		 * Dieses Feld speichert die {@link Conversion}.
		 */
		final Conversion<? extends GOutput, ? extends GInput> conversion;

		/**
		 * Dieser Konstruktor initialisiert die {@link Conversion}.
		 * 
		 * @param conversion {@link Conversion}.
		 * @throws NullPointerException Wenn die gegebene {@link Conversion} {@code null} ist.
		 */
		public InverseConversion(final Conversion<? extends GOutput, ? extends GInput> conversion) throws NullPointerException {
			if(conversion == null) throw new NullPointerException("conversion is null");
			this.conversion = conversion;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GInput input() {
			return this.conversion.output();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GOutput output() {
			return this.conversion.input();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this.conversion);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link Converter}, der seine Eingabe in eine {@link InverseConversion} überführt.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 */
	public static final class InverseConversionConverter<GInput, GOutput> extends
		AbstractConverter<Conversion<GInput, GOutput>, InverseConversion<GOutput, GInput>> {

		/**
		 * Dieses Feld speichert den {@link InverseConversionConverter}.
		 */
		public static final Converter<?, ?> INSTANCE = new InverseConversionConverter<Object, Object>();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public InverseConversion<GOutput, GInput> convert(final Conversion<GInput, GOutput> input) {
			return new InverseConversion<GOutput, GInput>(input);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			return (object == this) || (object instanceof InverseConversionConverter);
		}

	}

	/**
	 * Diese Klasse implementiert eine dynamische {@link Conversion}, deren Ausgabe mit Hilfe eines gegebenen {@link Converter}s aus der gegebenen Eingabe ermittelt wird.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 */
	public static final class DynamicConversion<GInput, GOutput> extends AbstractConversion<GInput, GOutput> {

		/**
		 * Dieses Feld speichert die Eingabe.
		 */
		final GInput input;

		/**
		 * Dieses Feld speichert den {@link Converter}.
		 */
		final Converter<? super GInput, ? extends GOutput> converter;

		/**
		 * Dieser Konstruktor initialisiert Eingabe und {@link Converter}.
		 * 
		 * @param input Eingabe.
		 * @param converter {@link Converter}.
		 * @throws NullPointerException Wenn der gegebenen {@link Converter} {@code null} ist.
		 */
		public DynamicConversion(final GInput input, final Converter<? super GInput, ? extends GOutput> converter) throws NullPointerException {
			if(converter == null) throw new NullPointerException("converter is null");
			this.input = input;
			this.converter = converter;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GInput input() {
			return this.input;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GOutput output() {
			return this.converter.convert(this.input);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this.input, this.converter);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Converter}, der seine Eingabe in eine {@link DynamicConversion} mit einem gegebenen {@link Converter} überführt.
	 * 
	 * @see DynamicConversion
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 */
	public static final class DynamicConversionConverter<GInput, GOutput> extends
		AbstractDelegatingConverter<GInput, Conversion<GInput, GOutput>, GInput, GOutput> {

		/**
		 * Dieser Konstruktor initialisiert den {@link Converter}.
		 * 
		 * @param converter {@link Converter}.
		 * @throws NullPointerException Wenn der gegebene {@link Converter} {@code null} ist.
		 */
		public DynamicConversionConverter(final Converter<? super GInput, ? extends GOutput> converter) throws NullPointerException {
			super(converter);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Conversion<GInput, GOutput> convert(final GInput input) {
			return new DynamicConversion<GInput, GOutput>(input, this.converter);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof DynamicConversionConverter<?, ?>)) return false;
			return super.equals(object);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link Converter}, der die Eingabe einer {@link Conversion} ermittelt.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ des Eingabe.
	 */
	public static final class ConversionInputConverter<GInput> extends AbstractConverter<Conversion<? extends GInput, ?>, GInput> {

		/**
		 * Dieses Feld speichert den {@link ConversionInputConverter}.
		 */
		public static final Converter<?, ?> INSTANCE = new ConversionInputConverter<Object>();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GInput convert(final Conversion<? extends GInput, ?> input) {
			return input.input();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			return (object == this) || (object instanceof ConversionInputConverter);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link Converter}, der die Ausgabe einer {@link Conversion} ermittelt.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOutput> Typ der Ausgabe.
	 */
	public static final class ConversionOutputConverter<GOutput> extends AbstractConverter<Conversion<?, ? extends GOutput>, GOutput> {

		/**
		 * Dieses Feld speichert den {@link ConversionOutputConverter}.
		 */
		public static final Converter<?, ?> INSTANCE = new ConversionOutputConverter<Object>();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GOutput convert(final Conversion<?, ? extends GOutput> input) {
			return input.output();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			return (object == this) || (object instanceof ConversionOutputConverter);
		}

	}

	/**
	 * Diese Methode erzeugt eine statische {@link Conversion}, deren Eingabe und Ausgabe konstant sind, und gibt sie zurück.
	 * 
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param input Eingabe.
	 * @param output Ausgabe.
	 * @return {@link StaticConversion}.
	 */
	public static <GInput, GOutput> StaticConversion<GInput, GOutput> staticConversion(final GInput input, final GOutput output) {
		return new StaticConversion<GInput, GOutput>(input, output);
	}

	/**
	 * Diese Methode erzeugt einen {@link Converter}, der seine Eingabe in eine {@link StaticConversion} überführt, und gibt ihh zurück. Die Ausgabe der {@link StaticConversion} wird hierbei mit dem gegebenen {@link Converter} ermittelt.
	 * 
	 * @see Conversions#staticConversion(Object, Object)
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param converter {@link Converter}.
	 * @return {@link StaticConversionConverter}.
	 * @throws NullPointerException Wenn der gegebene {@link Converter} {@code null} ist.
	 */
	public static <GInput, GOutput> Converter<GInput, Conversion<GInput, GOutput>> staticConversionConverter(
		final Converter<? super GInput, ? extends GOutput> converter) throws NullPointerException {
		return new StaticConversionConverter<GInput, GOutput>(converter);
	}

	/**
	 * Diese Methode erzeugt eine inverse {@link Conversion}, deren Ein- und Ausgabe aus der Aus- bzw. Eingabe einer gegebenen {@link Conversion} ermittelt werden, und gibt sie zurück.
	 * 
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param conversion {@link Conversion}.
	 * @return {@link InverseConversion}.
	 * @throws NullPointerException Wenn der gegebenen {@link Conversion} {@code null} ist.
	 */
	public static <GInput, GOutput> InverseConversion<GInput, GOutput> inverseConversion(final Conversion<? extends GOutput, ? extends GInput> conversion) {
		return new InverseConversion<GInput, GOutput>(conversion);
	}

	/**
	 * Diese Methode gibt einen {@link Converter} zurück, der seine Eingabe in eine {@link InverseConversion} überführt.
	 * 
	 * @see Conversions#inverseConversion(Conversion)
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @return {@link InverseConversionConverter}.
	 */
	@SuppressWarnings ("unchecked")
	public static <GInput, GOutput> Converter<Conversion<? extends GOutput, ? extends GInput>, Conversion<GInput, GOutput>> inverseConversionConverter() {
		return (Converter<Conversion<? extends GOutput, ? extends GInput>, Conversion<GInput, GOutput>>)InverseConversionConverter.INSTANCE;
	}

	/**
	 * Diese Methode erzeugt eine dynamische {@link Conversion}, deren Ausgabe mit Hilfe des gegebenen {@link Converter}s aus der gegebenen Eingabe ermittelt wird, und gibt sie zurück.
	 * 
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param input Eingabe.
	 * @param converter {@link Converter}.
	 * @return {@link DynamicConversion}.
	 * @throws NullPointerException Wenn der gegebenen {@link Converter} {@code null} ist.
	 */
	public static <GInput, GOutput> DynamicConversion<GInput, GOutput> dynamicConversion(final GInput input,
		final Converter<? super GInput, ? extends GOutput> converter) throws NullPointerException {
		return new DynamicConversion<GInput, GOutput>(input, converter);
	}

	/**
	 * Diese Methode erzeugt einen {@link Converter}, der seine Eingabe in eine {@link DynamicConversion} mit dem gegebenen {@link Converter} überführt.
	 * 
	 * @see Conversions#dynamicConversion(Object, Converter)
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param converter {@link Converter}.
	 * @return {@link DynamicConversionConverter}.
	 * @throws NullPointerException Wenn der gegebene {@link Converter} {@code null} ist.
	 */
	public static <GInput, GOutput> Converter<GInput, Conversion<GInput, GOutput>> dynamicConversionConverter(
		final Converter<? super GInput, ? extends GOutput> converter) throws NullPointerException {
		return new DynamicConversionConverter<GInput, GOutput>(converter);
	}

	/**
	 * Diese Methode gibt den {@link Converter} zurück, der die Eingabe einer {@link Conversion} ermittelt.
	 * 
	 * @see Conversion#input()
	 * @param <GInput> Typ des Eingabe.
	 * @return {@link Conversion#input()}-{@link Converter}.
	 */
	@SuppressWarnings ("unchecked")
	public static <GInput> Converter<Conversion<? extends GInput, ?>, GInput> conversionInputConverter() {
		return (Converter<Conversion<? extends GInput, ?>, GInput>)ConversionInputConverter.INSTANCE;
	}

	/**
	 * Diese Methode gibt den {@link Converter} zurück, der die Ausgabe einer {@link Conversion} ermittelt.
	 * 
	 * @see Conversion#output()
	 * @param <GOutput> Typ der Ausgabe.
	 * @return {@link Conversion#output()}-{@link Converter}.
	 */
	@SuppressWarnings ("unchecked")
	public static <GOutput> Converter<Conversion<?, ? extends GOutput>, GOutput> conversionOutputConverter() {
		return (Converter<Conversion<?, ? extends GOutput>, GOutput>)ConversionOutputConverter.INSTANCE;
	}

	/**
	 * Dieser Konstruktor ist versteckt und verhindert damit die Erzeugung von Instanzen der Klasse.
	 */
	Conversions() {
	}

}
