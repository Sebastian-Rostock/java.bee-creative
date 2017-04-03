package bee.creative.util;

/** Diese Klasse implementiert grundlegende {@link Conversion}.
 *
 * @see Getter
 * @see Getters
 * @see Conversion
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Conversions {

	/** Diese Klasse implementiert {@link #hashCode()} und {@link #equals(Object)} einer abstracten {@link Conversion}.
	 *
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe. */
	public static abstract class BaseConversion<GInput, GOutput> implements Conversion<GInput, GOutput> {

		/** {@inheritDoc} */
		@Override
		public int hashCode() {
			return Objects.hash(this.output());
		}

		/** {@inheritDoc} */
		@Override
		public boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof Conversion<?, ?>)) return false;
			final Conversion<?, ?> data = (Conversion<?, ?>)object;
			return Objects.equals(this.output(), data.output());
		}

	}

	{}

	/** Dieses Feld speichert den {@link Getter} zurück, der eine gegebene {@link Conversion} umkehrt.
	 *
	 * @see #inverseConversion(Conversion) */
	public static final Getter<?, ?> INVERSE_CONVERSION = new Getter<Conversion<?, ?>, Object>() {

		@Override
		public Object get(final Conversion<?, ?> input) {
			return Conversions.inverseConversion(input);
		}

		@Override
		public String toString() {
			return "INVERSE_CONVERSION";
		}

	};

	/** Dieses Feld speichert den {@link Getter} zurück, der die Eingabe einer {@link Conversion} ermittelt. */
	public static final Getter<?, ?> CONVERSION_INPUT = new Getter<Conversion<?, ?>, Object>() {

		@Override
		public Object get(final Conversion<?, ?> input) {
			return input.input();
		}

		@Override
		public String toString() {
			return "CONVERSION_INPUT";
		}

	};

	/** Dieses Feld speichert den {@link Getter} zurück, der die Ausgabe einer {@link Conversion} ermittelt. */
	public static final Getter<?, ?> CONVERSION_OUTPUT = new Getter<Conversion<?, ?>, Object>() {

		@Override
		public Object get(final Conversion<?, ?> input) {
			return input.output();
		}

		@Override
		public String toString() {
			return "CONVERSION_OUTPUT";
		}

	};

	{}

	/** Diese Methode gibt eine statische {@link Conversion} zurück, deren Eingabe und Ausgabe konstant sind.
	 *
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param input Eingabe.
	 * @param output Ausgabe.
	 * @return {@code static}-{@link Conversion}. */
	public static <GInput, GOutput> Conversion<GInput, GOutput> staticConversion(final GInput input, final GOutput output) {
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

	/** Diese Methode gibt einen {@link Getter} zurück, der seine Eingabe in mit dem gegebenen {@link Getter} umwandelt und das Paar dieser beiden Objekte als
	 * {@link Conversion} liefert.
	 *
	 * @see #staticConversion(Object, Object)
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param converter {@link Getter}.
	 * @return {@link Getter} zu {@link #staticConversion(Object, Object)}.
	 * @throws NullPointerException Wenn {@code converter} {@code null} ist. */
	public static <GInput, GOutput> Getter<GInput, Conversion<GInput, GOutput>> staticConversion(final Getter<? super GInput, ? extends GOutput> converter)
		throws NullPointerException {
		if (converter == null) throw new NullPointerException("converter = null");
		return new Getter<GInput, Conversion<GInput, GOutput>>() {

			@Override
			public Conversion<GInput, GOutput> get(final GInput input) {
				return Conversions.<GInput, GOutput>staticConversion(input, converter.get(input));
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("staticConversion", converter);
			}

		};
	}

	/** Diese Methode gibt eine inverse {@link Conversion} zurück, deren Ein- und Ausgabe aus der Aus- bzw. Eingabe der gegebenen {@link Conversion} ermittelt
	 * werden.
	 *
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param conversion {@link Conversion}.
	 * @return {@code inverse}-{@link Conversion}.
	 * @throws NullPointerException Wenn {@code conversion} {@code null} ist. */
	public static <GInput, GOutput> Conversion<GInput, GOutput> inverseConversion(final Conversion<? extends GOutput, ? extends GInput> conversion) {
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

	/** Diese Methode gibt eine dynamische {@link Conversion} zurück, deren Ausgabe stats mit Hilfe des gegebenen {@link Getter} aus der gegebenen Eingabe
	 * ermittelt wird.
	 *
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param input Eingabe.
	 * @param converter {@link Getter}.
	 * @return {@code dynamic}-{@link Conversion}.
	 * @throws NullPointerException Wenn {@code converter} {@code null} ist. */
	public static <GInput, GOutput> Conversion<GInput, GOutput> dynamicConversion(final GInput input, final Getter<? super GInput, ? extends GOutput> converter)
		throws NullPointerException {
		if (converter == null) throw new NullPointerException("converter = null");
		return new Conversion<GInput, GOutput>() {

			@Override
			public GInput input() {
				return input;
			}

			@Override
			public GOutput output() {
				return converter.get(input);
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("dynamicConversion", input, converter);
			}

		};
	}

	/** Diese Methode gibt einen {@link Getter} zurück, der seine Eingabe mit dem gegebenen {@link Getter} via {@link #dynamicConversion(Object, Getter)} in seine
	 * Ausgabe überführt.
	 *
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param converter {@link Getter}.
	 * @return {@link Getter} zu {@link #dynamicConversion(Object, Getter)}.
	 * @throws NullPointerException Wenn {@code converter} {@code null} ist. */
	public static <GInput, GOutput> Getter<GInput, Conversion<GInput, GOutput>> dynamicConversion(final Getter<? super GInput, ? extends GOutput> converter)
		throws NullPointerException {
		if (converter == null) throw new NullPointerException("converter = null");
		return new Getter<GInput, Conversion<GInput, GOutput>>() {

			@Override
			public Conversion<GInput, GOutput> get(final GInput input) {
				return Conversions.<GInput, GOutput>dynamicConversion(input, converter);
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("dynamicConversion", converter);
			}

		};
	}

	/** Diese Methode gibt den {@link Getter} zurück, der die Eingabe einer {@link Conversion} ermittelt.
	 *
	 * @see Conversion#input()
	 * @param <GInput> Typ des Eingabe.
	 * @return {@link Getter} zu {@link Conversion#input()}. */
	@SuppressWarnings ("unchecked")
	public static <GInput> Getter<Conversion<? extends GInput, ?>, GInput> inputGetter() {
		return (Getter<Conversion<? extends GInput, ?>, GInput>)Conversions.CONVERSION_INPUT;
	}

	/** Diese Methode gibt den {@link Getter} zurück, der die Ausgabe einer {@link Conversion} ermittelt.
	 *
	 * @see Conversion#output()
	 * @param <GOutput> Typ der Ausgabe.
	 * @return {@link Getter} zu {@link Conversion#output()}. */
	@SuppressWarnings ("unchecked")
	public static <GOutput> Getter<Conversion<?, ? extends GOutput>, GOutput> outputGetter() {
		return (Getter<Conversion<?, ? extends GOutput>, GOutput>)Conversions.CONVERSION_OUTPUT;
	}

	/** Diese Methode gibt einen {@link Getter} zurück, der eine gegebene {@link Conversion} umkehrt.
	 *
	 * @see #inverseConversion(Conversion)
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @return {@link Getter} zu {@link #inverseConversion(Conversion)}. */
	@SuppressWarnings ("unchecked")
	public static <GInput, GOutput> Getter<Conversion<? extends GOutput, ? extends GInput>, Conversion<GInput, GOutput>> inverseGetter() {
		return (Getter<Conversion<? extends GOutput, ? extends GInput>, Conversion<GInput, GOutput>>)Conversions.INVERSE_CONVERSION;
	}

}
