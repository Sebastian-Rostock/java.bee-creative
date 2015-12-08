package bee.creative.util;

/**
 * Diese Klasse implementiert grundlegende {@link Conversion}.
 * 
 * @see Converter
 * @see Converters
 * @see Conversion
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class Conversions {

	/**
	 * Diese Klasse implementiert {@link #hashCode()} und {@link #equals(Object)} einer abstracten {@link Conversion}.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 */
	public static abstract class BaseConversion<GInput, GOutput> implements Conversion<GInput, GOutput> {

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
			if (object == this) return true;
			if (!(object instanceof Conversion<?, ?>)) return false;
			final Conversion<?, ?> data = (Conversion<?, ?>)object;
			return Objects.equals(this.output(), data.output());
		}

	}

	{}

	/**
	 * Dieses Feld speichert den {@link Converter} zurück, der eine gegebene {@link Conversion} umkehrt.
	 * 
	 * @see #inverseConversion(Conversion)
	 */
	public static final Converter<?, ?> INVERSE_CONVERSION = new Converter<Conversion<?, ?>, Object>() {

		@Override
		public Object convert(final Conversion<?, ?> input) {
			return Conversions.inverseConversion(input);
		}

		@Override
		public String toString() {
			return "INVERSE_CONVERSION";
		}

	};

	/**
	 * Dieses Feld speichert den {@link Converter} zurück, der die Eingabe einer {@link Conversion} ermittelt.
	 */
	public static final Converter<?, ?> CONVERSION_INPUT = new Converter<Conversion<?, ?>, Object>() {

		@Override
		public Object convert(final Conversion<?, ?> input) {
			return input.input();
		}

		@Override
		public String toString() {
			return "CONVERSION_INPUT";
		}

	};

	/**
	 * Dieses Feld speichert den {@link Converter} zurück, der die Ausgabe einer {@link Conversion} ermittelt.
	 */
	public static final Converter<?, ?> CONVERSION_OUTPUT = new Converter<Conversion<?, ?>, Object>() {

		@Override
		public Object convert(final Conversion<?, ?> input) {
			return input.output();
		}

		@Override
		public String toString() {
			return "CONVERSION_OUTPUT";
		}

	};

	{}

	/**
	 * Diese Methode gibt eine statische {@link Conversion} zurück, deren Eingabe und Ausgabe konstant sind.
	 * 
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param input Eingabe.
	 * @param output Ausgabe.
	 * @return {@code static}-{@link Conversion}.
	 */
	public static final <GInput, GOutput> Conversion<GInput, GOutput> staticConversion(final GInput input, final GOutput output) {
		return new BaseConversion<GInput, GOutput>() {

			@Override
			public GInput input() {
				return input;
			}

			@Override
			public GOutput output() {
				return output;
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("staticConversion", input, output);
			}

		};
	}

	/**
	 * Diese Methode gibt einen {@link Converter} zurück, der seine Eingabe in mit dem gegebenen {@link Converter} umwandelt und das Paar dieser beiden Objekte
	 * als {@link Conversion} liefert.
	 * 
	 * @see #staticConversion(Object, Object)
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param converter {@link Converter}.
	 * @return {@link Converter} zu {@link #staticConversion(Object, Object)}.
	 * @throws NullPointerException Wenn {@code converter} {@code null} ist.
	 */
	public static final <GInput, GOutput> Converter<GInput, Conversion<GInput, GOutput>> staticConversion(
		final Converter<? super GInput, ? extends GOutput> converter) throws NullPointerException {
		if (converter == null) throw new NullPointerException("converter = null");
		return new Converter<GInput, Conversion<GInput, GOutput>>() {

			@Override
			public Conversion<GInput, GOutput> convert(final GInput input) {
				return Conversions.<GInput, GOutput>staticConversion(input, converter.convert(input));
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("staticConversion", converter);
			}

		};
	}

	/**
	 * Diese Methode gibt einen {@link Converter} zurück, der eine gegebene {@link Conversion} umkehrt.
	 * 
	 * @see #inverseConversion(Conversion)
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @return {@link Converter} zu {@link #inverseConversion(Conversion)}.
	 */
	@SuppressWarnings ("unchecked")
	public static final <GInput, GOutput> Converter<Conversion<? extends GOutput, ? extends GInput>, Conversion<GInput, GOutput>> inverseConversion() {
		return (Converter<Conversion<? extends GOutput, ? extends GInput>, Conversion<GInput, GOutput>>)Conversions.INVERSE_CONVERSION;
	}

	/**
	 * Diese Methode gibt eine inverse {@link Conversion} zurück, deren Ein- und Ausgabe aus der Aus- bzw. Eingabe der gegebenen {@link Conversion} ermittelt
	 * werden.
	 * 
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param conversion {@link Conversion}.
	 * @return {@code inverse}-{@link Conversion}.
	 * @throws NullPointerException Wenn {@code conversion} {@code null} ist.
	 */
	public static final <GInput, GOutput> Conversion<GInput, GOutput> inverseConversion(final Conversion<? extends GOutput, ? extends GInput> conversion) {
		if (conversion == null) throw new NullPointerException("conversion = null");
		return new Conversion<GInput, GOutput>() {

			@Override
			public GInput input() {
				return conversion.output();
			}

			@Override
			public GOutput output() {
				return conversion.input();
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("inverseConversion", conversion);
			}

		};
	}

	/**
	 * Diese Methode gibt eine dynamische {@link Conversion} zurück, deren Ausgabe stats mit Hilfe des gegebenen {@link Converter} aus der gegebenen Eingabe
	 * ermittelt wird.
	 * 
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param input Eingabe.
	 * @param converter {@link Converter}.
	 * @return {@code dynamic}-{@link Conversion}.
	 * @throws NullPointerException Wenn {@code converter} {@code null} ist.
	 */
	public static final <GInput, GOutput> Conversion<GInput, GOutput> dynamicConversion(final GInput input,
		final Converter<? super GInput, ? extends GOutput> converter) throws NullPointerException {
		if (converter == null) throw new NullPointerException("converter = null");
		return new Conversion<GInput, GOutput>() {

			@Override
			public GInput input() {
				return input;
			}

			@Override
			public GOutput output() {
				return converter.convert(input);
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("dynamicConversion", input, converter);
			}

		};
	}

	/**
	 * Diese Methode gibt einen {@link Converter} zurück, der seine Eingabe mit dem gegebenen {@link Converter} via {@link #dynamicConversion(Object, Converter)}
	 * in seine Ausgabe überführt.
	 * 
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param converter {@link Converter}.
	 * @return {@link Converter} zu {@link #dynamicConversion(Object, Converter)}.
	 * @throws NullPointerException Wenn {@code converter} {@code null} ist.
	 */
	public static final <GInput, GOutput> Converter<GInput, Conversion<GInput, GOutput>> dynamicConversion(
		final Converter<? super GInput, ? extends GOutput> converter) throws NullPointerException {
		if (converter == null) throw new NullPointerException("converter = null");
		return new Converter<GInput, Conversion<GInput, GOutput>>() {

			@Override
			public Conversion<GInput, GOutput> convert(final GInput input) {
				return Conversions.<GInput, GOutput>dynamicConversion(input, converter);
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("dynamicConversion", converter);
			}

		};
	}

	/**
	 * Diese Methode gibt den {@link Converter} zurück, der die Eingabe einer {@link Conversion} ermittelt.
	 * 
	 * @see Conversion#input()
	 * @param <GInput> Typ des Eingabe.
	 * @return {@link Converter} zu {@link Conversion#input()}.
	 */
	@SuppressWarnings ("unchecked")
	public static final <GInput> Converter<Conversion<? extends GInput, ?>, GInput> conversionInput() {
		return (Converter<Conversion<? extends GInput, ?>, GInput>)Conversions.CONVERSION_INPUT;
	}

	/**
	 * Diese Methode gibt den {@link Converter} zurück, der die Ausgabe einer {@link Conversion} ermittelt.
	 * 
	 * @see Conversion#output()
	 * @param <GOutput> Typ der Ausgabe.
	 * @return {@link Converter} zu {@link Conversion#output()}.
	 */
	@SuppressWarnings ("unchecked")
	public static final <GOutput> Converter<Conversion<?, ? extends GOutput>, GOutput> conversionOutput() {
		return (Converter<Conversion<?, ? extends GOutput>, GOutput>)Conversions.CONVERSION_OUTPUT;
	}

}
