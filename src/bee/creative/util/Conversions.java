package bee.creative.util;

import bee.creative.util.Converters.ConverterLink;

/**
 * Diese Klasse implementiert Hilfsmethoden und Hilfsklassen zur Konstruktion von {@link Conversion Conversions}.
 * 
 * @see Converter
 * @see Converters
 * @see Conversion
 * @see Conversions
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class Conversions {

	/**
	 * Diese Klasse implementiert eine abstrakte {@link Conversion Conversion}.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 */
	static abstract class BaseConversion<GInput, GOutput> implements Conversion<GInput, GOutput> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final int hashCode() {
			return Objects.hash(this.output());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof Conversion<?, ?>)) return false;
			final Conversion<?, ?> data = (Conversion<?, ?>)object;
			return Objects.equals(this.output(), data.output());
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Converter Converter}, der seine Eingabe mit Hilfe der Methode
	 * {@link Conversions#staticConversion(Object, Object)} in seine Ausgabe überführt.
	 * 
	 * @see Conversions#staticConversion(Object, Object)
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 */
	static final class StaticConversionConverter<GInput, GOutput> extends ConverterLink<GInput, GOutput> implements
		Converter<GInput, Conversion<GInput, GOutput>> {

		/**
		 * Dieser Konstrukteur initialisiert den {@link Converter Converter}.
		 * 
		 * @param converter {@link Converter Converter}.
		 * @throws NullPointerException Wenn der gegebene {@link Converter Converter} <code>null</code> ist.
		 */
		public StaticConversionConverter(final Converter<? super GInput, ? extends GOutput> converter)
			throws NullPointerException {
			super(converter);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Conversion<GInput, GOutput> convert(final GInput input) {
			return Conversions.<GInput, GOutput>staticConversion(input, this.converter.convert(input));
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

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("staticConversionConverter", this.converter);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Converter Converter}, der seine Eingabe mit Hilfe der Methode
	 * {@link Conversions#dynamicConversion(Object, Converter)} in seine Ausgabe überführt.
	 * 
	 * @see Conversions#dynamicConversion(Object, Converter)
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 */
	static final class DynamicConversionConverter<GInput, GOutput> extends Converters.ConverterLink<GInput, GOutput>
		implements Converter<GInput, Conversion<GInput, GOutput>> {

		/**
		 * Dieser Konstrukteur initialisiert den {@link Converter Converter}.
		 * 
		 * @param converter {@link Converter Converter}.
		 * @throws NullPointerException Wenn der gegebene {@link Converter Converter} <code>null</code> ist.
		 */
		public DynamicConversionConverter(final Converter<? super GInput, ? extends GOutput> converter)
			throws NullPointerException {
			super(converter);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Conversion<GInput, GOutput> convert(final GInput input) {
			return Conversions.dynamicConversion(input, this.converter);
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

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("dynamicConversionConverter", this.converter);
		}

	}

	/**
	 * Diese Klasse implementiert eine statische {@link Conversion Conversion}, deren Ein- und Ausgabe konstant sind.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 */
	public static final class StaticConversion<GInput, GOutput> extends BaseConversion<GInput, GOutput> {

		/**
		 * Dieses Feld speichert die Eingabe.
		 */
		final GInput input;

		/**
		 * Dieses Feld speichert die Ausgeb.
		 */
		final GOutput output;

		/**
		 * Dieser Konstrukteur initialisiert Eingabe und Ausgabe.
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
			return Objects.toStringCall("staticConversion", this.input, this.output);
		}

	}

	/**
	 * Diese Klasse implementiert eine inverse {@link Conversion Conversion}, deren Ein- und Ausgabe aus der Aus- bzw.
	 * Eingabe einer gegebenen {@link Conversion Conversion} ermittelt werden.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 */
	public static final class InverseConversion<GInput, GOutput> extends BaseConversion<GInput, GOutput> {

		/**
		 * Dieses Feld speichert die {@link Conversion Conversion}.
		 */
		final Conversion<? extends GOutput, ? extends GInput> conversion;

		/**
		 * Dieser Konstrukteur initialisiert die {@link Conversion Conversion}.
		 * 
		 * @param conversion {@link Conversion Conversion}.
		 * @throws NullPointerException Wenn die gegebene {@link Conversion Conversion} <code>null</code> ist.
		 */
		public InverseConversion(final Conversion<? extends GOutput, ? extends GInput> conversion)
			throws NullPointerException {
			if(conversion == null) throw new NullPointerException();
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
			return Objects.toStringCall("inverseConversion", this.conversion);
		}

	}

	/**
	 * Diese Klasse implementiert eine dynamische {@link Conversion Conversion}, deren Ausgabe mit Hilfe eines gegebenen
	 * {@link Converter Converters} aus der gegebenen Eingabe ermittelt wird.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 */
	public static final class DynamicConversion<GInput, GOutput> extends BaseConversion<GInput, GOutput> {

		/**
		 * Dieses Feld speichert die Eingabe.
		 */
		final GInput input;

		/**
		 * Dieses Feld speichert den {@link Converter Converter}.
		 */
		final Converter<? super GInput, ? extends GOutput> converter;

		/**
		 * Dieser Konstrukteur initialisiert Eingabe und {@link Converter Converter}.
		 * 
		 * @param input Eingabe.
		 * @param converter {@link Converter Converter}.
		 * @throws NullPointerException Wenn der gegebenen {@link Converter Converter} <code>null</code> ist.
		 */
		public DynamicConversion(final GInput input, final Converter<? super GInput, ? extends GOutput> converter)
			throws NullPointerException {
			if(converter == null) throw new NullPointerException();
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
			return Objects.toStringCall("dynamicConversion", this.input, this.converter);
		}

	}

	/**
	 * Dieses Feld speichert den {@link Converter Converter}, der die Eingabe einer {@link Conversion Conversion}
	 * ermittelt.
	 */
	static final Converter<?, ?> CONVERSION_INPUT_CONVERTER = new Converter<Conversion<?, ?>, Object>() {

		@Override
		public Object convert(final Conversion<?, ?> input) {
			return input.input();
		}

		@Override
		public String toString() {
			return Objects.toStringCall("conversionInputConverter");
		}

	};

	/**
	 * Dieses Feld speichert den {@link Converter Converter}, der die Ausgabe einer {@link Conversion Conversion}
	 * ermittelt.
	 */
	static final Converter<?, ?> CONVERSION_OUTPUT_CONVERTER = new Converter<Conversion<?, ?>, Object>() {

		@Override
		public Object convert(final Conversion<?, ?> input) {
			return input.output();
		}

		@Override
		public String toString() {
			return Objects.toStringCall("conversionOutputConverter");
		}

	};

	/**
	 * Dieses Feld speichert den {@link Converter Converter}, der seine Eingabe mit Hilfe der Methode
	 * {@link Conversions#inverseConversion(Conversion)} in seine Ausgabe überführt.
	 */
	static final Converter<?, ?> INVERSE_CONVERSION_CONVERTER =
		new Converter<Conversion<Object, Object>, Conversion<Object, Object>>() {

			@Override
			public Conversion<Object, Object> convert(final Conversion<Object, Object> input) {
				return Conversions.inverseConversion(input);
			}

			@Override
			public String toString() {
				return Objects.toStringCall("inverseConversionConverter");
			}

		};

	/**
	 * Diese Methode erzeugt eine statische {@link Conversion Conversion}, deren Eingabe und Ausgabe konstant sind, und
	 * gibt sie zurück.
	 * 
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param input Eingabe.
	 * @param output Ausgabe.
	 * @return {@link StaticConversion Static-Conversion}.
	 */
	public static <GInput, GOutput> Conversion<GInput, GOutput> staticConversion(final GInput input, final GOutput output) {
		return new StaticConversion<GInput, GOutput>(input, output);
	}

	/**
	 * Diese Methode erzeugt einen {@link Converter Converter}, der seine Eingabe mit Hilfe der Methode
	 * {@link Conversions#staticConversion(Object, Object)} in seine Ausgabe überführt, und gibt ihh zurück.
	 * 
	 * @see Conversions#staticConversion(Object, Object)
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param converter {@link Converter Converter}.
	 * @return {@link Conversions#staticConversion(Object, Object)}-{@link Converter Converter}.
	 * @throws NullPointerException Wenn der gegebene {@link Converter Converter} <code>null</code> ist.
	 */
	public static <GInput, GOutput> Converter<GInput, Conversion<GInput, GOutput>> staticConversionConverter(
		final Converter<? super GInput, ? extends GOutput> converter) throws NullPointerException {
		return new StaticConversionConverter<GInput, GOutput>(converter);
	}

	/**
	 * Diese Methode erzeugt eine inverse {@link Conversion Conversion}, deren Ein- und Ausgabe aus der Aus- bzw. Eingabe
	 * einer gegebenen {@link Conversion Conversion} ermittelt werden, und gibt sie zurück.
	 * 
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param conversion {@link Conversion Conversion}.
	 * @return {@link InverseConversion Inverse-Conversion}.
	 * @throws NullPointerException Wenn der gegebenen {@link Conversion Conversion} <code>null</code> ist.
	 */
	public static <GInput, GOutput> Conversion<GInput, GOutput> inverseConversion(
		final Conversion<? extends GOutput, ? extends GInput> conversion) {
		return new InverseConversion<GInput, GOutput>(conversion);
	}

	/**
	 * Diese Methode gibt einen {@link Converter Converter} zurück, der seine Eingabe mit Hilfe der Methode
	 * {@link Conversions#inverseConversion(Conversion)} in seine Ausgabe überführt.
	 * 
	 * @see Conversions#inverseConversion(Conversion)
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @return {@link Conversions#inverseConversion(Conversion)}-{@link Converter Converter} .
	 */
	@SuppressWarnings ("unchecked")
	public static <GInput, GOutput> Converter<Conversion<? extends GOutput, ? extends GInput>, Conversion<GInput, GOutput>> inverseConversionConverter() {
		return (Converter<Conversion<? extends GOutput, ? extends GInput>, Conversion<GInput, GOutput>>)Conversions.INVERSE_CONVERSION_CONVERTER;
	}

	/**
	 * Diese Methode erzeugt eine dynamische {@link Conversion Conversion}, deren Ausgabe mit Hilfe des gegebenen
	 * {@link Converter Converters} aus der gegebenen Eingabe ermittelt wird, und gibt sie zurück.
	 * 
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param input Eingabe.
	 * @param converter {@link Converter Converter}.
	 * @return {@link DynamicConversion Dynamic-Conversion}.
	 * @throws NullPointerException Wenn der gegebenen {@link Converter Converter} <code>null</code> ist.
	 */
	public static <GInput, GOutput> Conversion<GInput, GOutput> dynamicConversion(final GInput input,
		final Converter<? super GInput, ? extends GOutput> converter) throws NullPointerException {
		return new DynamicConversion<GInput, GOutput>(input, converter);
	}

	/**
	 * Diese Methode erzeugt einen {@link Converter Converter}, der seine Eingabe mit Hilfe der Methode
	 * {@link Conversions#dynamicConversion(Object, Converter)} in seine Ausgabe überführt, und gibt ihh zurück.
	 * 
	 * @see Conversions#dynamicConversion(Object, Converter)
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param converter {@link Converter Converter}.
	 * @return {@link Conversions#dynamicConversion(Object, Converter)}-{@link Converter Converter}.
	 * @throws NullPointerException Wenn der gegebene {@link Converter Converter} <code>null</code> ist.
	 */
	public static <GInput, GOutput> Converter<GInput, Conversion<GInput, GOutput>> dynamicConversionConverter(
		final Converter<? super GInput, ? extends GOutput> converter) throws NullPointerException {
		return new DynamicConversionConverter<GInput, GOutput>(converter);
	}

	/**
	 * Diese Methode gibt den {@link Converter Converter} zurück, der die Eingabe einer {@link Conversion Conversion}
	 * ermittelt.
	 * 
	 * @see Conversion#input()
	 * @param <GInput> Typ des Eingabe.
	 * @return {@link Conversion#input()}-{@link Converter Converter}.
	 */
	@SuppressWarnings ("unchecked")
	public static <GInput> Converter<Conversion<? extends GInput, ?>, GInput> conversionInputConverter() {
		return (Converter<Conversion<? extends GInput, ?>, GInput>)Conversions.CONVERSION_INPUT_CONVERTER;
	}

	/**
	 * Diese Methode gibt den {@link Converter Converter} zurück, der die Ausgabe einer {@link Conversion Conversion}
	 * ermittelt.
	 * 
	 * @see Conversion#output()
	 * @param <GOutput> Typ der Ausgabe.
	 * @return {@link Conversion#output()}-{@link Converter Converter}.
	 */
	@SuppressWarnings ("unchecked")
	public static <GOutput> Converter<Conversion<?, ? extends GOutput>, GOutput> conversionOutputConverter() {
		return (Converter<Conversion<?, ? extends GOutput>, GOutput>)Conversions.CONVERSION_OUTPUT_CONVERTER;
	}

	/**
	 * Dieser Konstrukteur ist versteckt und verhindert damit die Erzeugung von Instanzen der Klasse.
	 */
	Conversions() {
	}

}
