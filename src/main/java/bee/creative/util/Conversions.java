package bee.creative.util;

import bee.creative.util.Getters.BaseGetter;
import bee.creative.util.Objects.BaseObject;

/** Diese Klasse implementiert grundlegende {@link Conversion}.
 *
 * @see Conversion
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Conversions {

	/** Diese Klasse implementiert {@link Conversions#inputGetter()} */
	@SuppressWarnings ("javadoc")
	static class InputGetter extends BaseGetter<Conversion<?, ?>, Object> {

		static final Getter<?, ?> INSTANCE = new InputGetter();

		@Override
		public Object get(final Conversion<?, ?> input) {
			return input.input();
		}

	}

	/** Diese Klasse implementiert {@link Conversions#outputGetter()} */
	@SuppressWarnings ("javadoc")
	static class OutputGetter extends BaseGetter<Conversion<?, ?>, Object> {

		static final Getter<?, ?> INSTANCE = new OutputGetter();

		@Override
		public Object get(final Conversion<?, ?> input) {
			return input.output();
		}

	}

	/** Diese Klasse implementiert eine abstrakte {@link Conversion} als {@link BaseObject}. */
	@SuppressWarnings ("javadoc")
	public static abstract class BaseConversion<GInput, GValue> extends BaseObject implements Conversion<GInput, GValue> {

		@Override
		public int hashCode() {
			return Objects.hash(this.output());
		}

		@Override
		public boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof Conversion<?, ?>)) return false;
			return Objects.equals(this.output(), ((Conversion<?, ?>)object).output());
		}

	}

	/** Diese Klasse implementiert {@link Conversions#virtualConversion(Object, Getter)} */
	@SuppressWarnings ("javadoc")
	public static class VirtualConversion<GInput, GOutput> extends BaseConversion<GInput, GOutput> {

		public final GInput input;

		public final Getter<? super GInput, ? extends GOutput> converter;

		public VirtualConversion(final GInput input, final Getter<? super GInput, ? extends GOutput> converter) throws NullPointerException {
			this.input = input;
			this.converter = Objects.notNull(converter);
		}

		@Override
		public GInput input() {
			return this.input;
		}

		@Override
		public GOutput output() {
			return this.converter.get(this.input);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.input, this.converter);
		}

	}

	/** Diese Klasse implementiert {@link Conversions#reverseConversion(Conversion)} */
	@SuppressWarnings ("javadoc")
	public static class ReverseConversion<GInput, GOutput> extends BaseConversion<GInput, GOutput> {

		public final Conversion<? extends GOutput, ? extends GInput> conversion;

		public ReverseConversion(final Conversion<? extends GOutput, ? extends GInput> conversion) throws NullPointerException {
			this.conversion = Objects.notNull(conversion);
		}

		@Override
		public GInput input() {
			return this.conversion.output();
		}

		@Override
		public GOutput output() {
			return this.conversion.input();
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.conversion);
		}

	}

	/** Diese Klasse implementiert {@link Conversions#compositeConversion(Object, Object)} */
	@SuppressWarnings ("javadoc")
	public static class CompositeConversion<GInput, GOutput> extends BaseConversion<GInput, GOutput> {

		public GInput input;

		public GOutput output;

		public CompositeConversion(final GInput input, final GOutput output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GInput input() {
			return this.input;
		}

		@Override
		public GOutput output() {
			return this.output;
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.input, this.output);
		}

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
	public static <GInput, GOutput> Conversion<GInput, GOutput> virtualConversion(final GInput input, final Getter<? super GInput, ? extends GOutput> converter)
		throws NullPointerException {
		return new VirtualConversion<>(input, converter);
	}

	/** Diese Methode gibt eine inverse {@link Conversion} zurück, deren Ein- und Ausgabe aus der Aus- bzw. Eingabe der gegebenen {@link Conversion} ermittelt
	 * werden.
	 *
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param conversion {@link Conversion}.
	 * @return {@code inverse}-{@link Conversion}.
	 * @throws NullPointerException Wenn {@code conversion} {@code null} ist. */
	public static <GInput, GOutput> Conversion<GInput, GOutput> reverseConversion(final Conversion<? extends GOutput, ? extends GInput> conversion) {
		return new ReverseConversion<>(conversion);
	}

	/** Diese Methode gibt eine statische {@link Conversion} zurück, deren Eingabe und Ausgabe konstant sind.
	 *
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param input Eingabe.
	 * @param output Ausgabe.
	 * @return {@code static}-{@link Conversion}. */
	public static <GInput, GOutput> Conversion<GInput, GOutput> compositeConversion(final GInput input, final GOutput output) {
		return new CompositeConversion<>(input, output);
	}

	/** Diese Methode gibt den {@link Getter} zurück, der die Eingabe einer {@link Conversion} ermittelt.
	 *
	 * @see Conversion#input()
	 * @param <GInput> Typ des Eingabe.
	 * @return {@link Getter} zu {@link Conversion#input()}. */
	@SuppressWarnings ("unchecked")
	public static <GInput> Getter<Conversion<? extends GInput, ?>, GInput> inputGetter() {
		return (Getter<Conversion<? extends GInput, ?>, GInput>)InputGetter.INSTANCE;
	}

	/** Diese Methode gibt den {@link Getter} zurück, der die Ausgabe einer {@link Conversion} ermittelt.
	 *
	 * @see Conversion#output()
	 * @param <GOutput> Typ der Ausgabe.
	 * @return {@link Getter} zu {@link Conversion#output()}. */
	@SuppressWarnings ("unchecked")
	public static <GOutput> Getter<Conversion<?, ? extends GOutput>, GOutput> outputGetter() {
		return (Getter<Conversion<?, ? extends GOutput>, GOutput>)OutputGetter.INSTANCE;
	}

}
