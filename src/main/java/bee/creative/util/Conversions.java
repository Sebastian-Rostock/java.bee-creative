package bee.creative.util;

import java.util.Iterator;
import bee.creative.util.Getters.BaseGetter;
import bee.creative.util.Objects.BaseObject;
import bee.creative.util.Objects.UseToString;

/** Diese Klasse implementiert grundlegende {@link Conversion}.
 *
 * @see Getter
 * @see Getters
 * @see Conversion
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Conversions {

	static class InpitGetter extends BaseGetter<Conversion<?, ?>, Object> {

		@Override
		public Object get(final Conversion<?, ?> input) {
			return input.input();
		}

	}

	static class OutputGetter extends BaseGetter<Conversion<?, ?>, Object> {

		@Override
		public Object get(final Conversion<?, ?> input) {
			return input.output();
		}

	}

	static class VirtualGetter<GInput, GOutput> extends BaseGetter<GInput, Conversion<GInput, GOutput>> {

		public final Getter<? super GInput, ? extends GOutput> converter;

		public VirtualGetter(final Getter<? super GInput, ? extends GOutput> converter) {
			this.converter = Objects.assertNotNull(converter);
		}

		@Override
		public Conversion<GInput, GOutput> get(final GInput input) {
			return Conversions.<GInput, GOutput>virtualConversion(input, this.converter);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.converter);
		}

	}

	static class ReverseGetter extends BaseGetter<Conversion<?, ?>, Object> {

		@Override
		public Object get(final Conversion<?, ?> input) {
			return Conversions.reverseConversion(input);
		}

	}

	static class CompositeGetter<GInput, GOutput> extends BaseGetter<GInput, Conversion<GInput, GOutput>> {

		public final Getter<? super GInput, ? extends GOutput> converter;

		public CompositeGetter(final Getter<? super GInput, ? extends GOutput> converter) {
			this.converter = Objects.assertNotNull(converter);
		}

		@Override
		public Conversion<GInput, GOutput> get(final GInput input) {
			return Conversions.<GInput, GOutput>compositeConversion(input, this.converter.get(input));
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.converter);
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

	public static class VirtualConversion<GInput, GOutput> extends BaseConversion<GInput, GOutput> {

		public final GInput input;

		public final Getter<? super GInput, ? extends GOutput> converter;

		public VirtualConversion(final GInput input, final Getter<? super GInput, ? extends GOutput> converter) throws NullPointerException {
			this.input = input;
			this.converter = Objects.assertNotNull(converter);
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

	public static class ReverseConversion<GInput, GOutput> extends BaseConversion<GInput, GOutput> {

		public final Conversion<? extends GOutput, ? extends GInput> conversion;

		public ReverseConversion(final Conversion<? extends GOutput, ? extends GInput> conversion) throws NullPointerException {
			this.conversion = Objects.assertNotNull(conversion);
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

	/** Dieses Feld speichert den {@link Getter} für {@link Conversions#inputGetter()} */
	static final Getter<?, ?> INPUT_GETTER = new InpitGetter();

	/** Dieses Feld speichert den {@link Getter} für {@link Conversions#outputGetter()} */
	static final Getter<?, ?> OUTPUT_GETTER = new OutputGetter();

	/** Dieses Feld speichert den {@link Getter} für {@link Conversions#reverseGetter()} */
	static final Getter<?, ?> REVERSE_GETTER = new ReverseGetter();

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
		return (Getter<Conversion<? extends GInput, ?>, GInput>)Conversions.INPUT_GETTER;
	}

	/** Diese Methode gibt den {@link Getter} zurück, der die Ausgabe einer {@link Conversion} ermittelt.
	 *
	 * @see Conversion#output()
	 * @param <GOutput> Typ der Ausgabe.
	 * @return {@link Getter} zu {@link Conversion#output()}. */
	@SuppressWarnings ("unchecked")
	public static <GOutput> Getter<Conversion<?, ? extends GOutput>, GOutput> outputGetter() {
		return (Getter<Conversion<?, ? extends GOutput>, GOutput>)Conversions.OUTPUT_GETTER;
	}

	/** Diese Methode gibt einen {@link Getter} zurück, der seine Eingabe mit dem gegebenen {@link Getter} via {@link #virtualConversion(Object, Getter)} in seine
	 * Ausgabe überführt.
	 *
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param converter {@link Getter}.
	 * @return {@link Getter} zu {@link #virtualConversion(Object, Getter)}.
	 * @throws NullPointerException Wenn {@code converter} {@code null} ist. */
	public static <GInput, GOutput> Getter<GInput, Conversion<GInput, GOutput>> virtualGetter(final Getter<? super GInput, ? extends GOutput> converter)
		throws NullPointerException {
		return new VirtualGetter<>(converter);
	}

	/** Diese Methode gibt einen {@link Getter} zurück, der eine gegebene {@link Conversion} umkehrt.
	 *
	 * @see #reverseConversion(Conversion)
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @return {@link Getter} zu {@link #reverseConversion(Conversion)}. */
	@SuppressWarnings ("unchecked")
	public static <GInput, GOutput> Getter<Conversion<? extends GOutput, ? extends GInput>, Conversion<GInput, GOutput>> reverseGetter() {
		return (Getter<Conversion<? extends GOutput, ? extends GInput>, Conversion<GInput, GOutput>>)Conversions.REVERSE_GETTER;
	}

	/** Diese Methode gibt einen {@link Getter} zurück, der seine Eingabe in mit dem gegebenen {@link Getter} umwandelt und das Paar dieser beiden Objekte als
	 * {@link Conversion} liefert.
	 *
	 * @see #compositeConversion(Object, Object)
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param converter {@link Getter}.
	 * @return {@link Getter} zu {@link #compositeConversion(Object, Object)}.
	 * @throws NullPointerException Wenn {@code converter} {@code null} ist. */
	public static <GInput, GOutput> Getter<GInput, Conversion<GInput, GOutput>> compositeGetter(final Getter<? super GInput, ? extends GOutput> converter)
		throws NullPointerException {
		return new CompositeGetter<>(converter);
	}

}
