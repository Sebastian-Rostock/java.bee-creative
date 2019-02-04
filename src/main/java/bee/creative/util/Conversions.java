package bee.creative.util;

import bee.creative.util.Getters.BaseGetter;
import bee.creative.util.Objects.BaseObject;

/** Diese Klasse implementiert grundlegende {@link Conversion}.
 *
 * @see Conversion
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Conversions {

	/** Diese Klasse implementiert eine abstrakte {@link Conversion} als {@link BaseObject}. */
	public static abstract class BaseConversion<GSource, GTarget> extends BaseObject implements Conversion<GSource, GTarget> {

		@Override
		public int hashCode() {
			return Objects.hash(this.target());
		}

		@Override
		public boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof Conversion<?, ?>)) return false;
			final Conversion<?, ?> that = (Conversion<?, ?>)object;
			return Objects.equals(this.target(), that.target());
		}

	}

	/** Diese Klasse implementiert {@link Conversions#virtualConversion(Object, Getter)} */
	@SuppressWarnings ("javadoc")
	public static class VirtualConversion<GSource, GTarget> extends BaseConversion<GSource, GTarget> {

		public final GSource input;

		public final Getter<? super GSource, ? extends GTarget> getter;

		public VirtualConversion(final GSource input, final Getter<? super GSource, ? extends GTarget> getter) throws NullPointerException {
			this.input = input;
			this.getter = Objects.notNull(getter);
		}

		@Override
		public GSource source() {
			return this.input;
		}

		@Override
		public GTarget target() {
			return this.getter.get(this.input);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.input, this.getter);
		}

	}

	/** Diese Klasse implementiert {@link Conversions#reverseConversion(Conversion)} */
	@SuppressWarnings ("javadoc")
	public static class ReverseConversion<GSource, GTarget> extends BaseConversion<GSource, GTarget> {

		public final Conversion<? extends GTarget, ? extends GSource> conversion;

		public ReverseConversion(final Conversion<? extends GTarget, ? extends GSource> conversion) throws NullPointerException {
			this.conversion = Objects.notNull(conversion);
		}

		@Override
		public GSource source() {
			return this.conversion.target();
		}

		@Override
		public GTarget target() {
			return this.conversion.source();
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.conversion);
		}

	}

	/** Diese Klasse implementiert {@link Conversions#compositeConversion(Object, Object)} */
	@SuppressWarnings ("javadoc")
	public static class CompositeConversion<GSource, GTarget> extends BaseConversion<GSource, GTarget> {

		public GSource source;

		public GTarget target;

		public CompositeConversion(final GSource source, final GTarget target) {
			this.source = source;
			this.target = target;
		}

		@Override
		public GSource source() {
			return this.source;
		}

		@Override
		public GTarget target() {
			return this.target;
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.source, this.target);
		}

	}

	/** Diese Klasse implementiert {@link Conversions#inputGetter()} */
	@SuppressWarnings ("javadoc")
	static class InputGetter extends BaseGetter<Conversion<?, ?>, Object> {

		static final Getter<?, ?> INSTANCE = new InputGetter();

		@Override
		public Object get(final Conversion<?, ?> input) {
			return input.source();
		}

	}

	/** Diese Klasse implementiert {@link Conversions#outputGetter()} */
	@SuppressWarnings ("javadoc")
	static class OutputGetter extends BaseGetter<Conversion<?, ?>, Object> {

		static final Getter<?, ?> INSTANCE = new OutputGetter();

		@Override
		public Object get(final Conversion<?, ?> input) {
			return input.target();
		}

	}

	/** Diese Methode gibt eine dynamische {@link Conversion} zurück, deren Ausgabe stats mit Hilfe des gegebenen {@link Getter} aus der gegebenen Eingabe
	 * ermittelt wird.
	 *
	 * @param <GSource> Typ des Eingabe.
	 * @param <GTarget> Typ der Ausgabe.
	 * @param input Eingabe.
	 * @param getter {@link Getter}.
	 * @return {@code dynamic}-{@link Conversion}.
	 * @throws NullPointerException Wenn {@code converter} {@code null} ist. */
	public static <GSource, GTarget> Conversion<GSource, GTarget> virtualConversion(final GSource input, final Getter<? super GSource, ? extends GTarget> getter)
		throws NullPointerException {
		return new VirtualConversion<>(input, getter);
	}

	/** Diese Methode gibt eine inverse {@link Conversion} zurück, deren Ein- und Ausgabe aus der Aus- bzw. Eingabe der gegebenen {@link Conversion} ermittelt
	 * werden.
	 *
	 * @param <GSource> Typ des Eingabe.
	 * @param <GTarget> Typ der Ausgabe.
	 * @param conversion {@link Conversion}.
	 * @return {@code inverse}-{@link Conversion}.
	 * @throws NullPointerException Wenn {@code conversion} {@code null} ist. */
	public static <GSource, GTarget> Conversion<GSource, GTarget> reverseConversion(final Conversion<? extends GTarget, ? extends GSource> conversion) {
		return new ReverseConversion<>(conversion);
	}

	/** Diese Methode gibt eine statische {@link Conversion} zurück, deren Eingabe und Ausgabe konstant sind.
	 *
	 * @param <GSource> Typ des Eingabe.
	 * @param <GTarget> Typ der Ausgabe.
	 * @param source Eingabe.
	 * @param target Ausgabe.
	 * @return {@code static}-{@link Conversion}. */
	public static <GSource, GTarget> Conversion<GSource, GTarget> compositeConversion(final GSource source, final GTarget target) {
		return new CompositeConversion<>(source, target);
	}

	/** Diese Methode gibt den {@link Getter} zurück, der die Eingabe einer {@link Conversion} ermittelt.
	 *
	 * @see Conversion#source()
	 * @param <GSource> Typ des Eingabe.
	 * @return {@link Getter} zu {@link Conversion#source()}. */
	@SuppressWarnings ("unchecked")
	public static <GSource> Getter<Conversion<? extends GSource, ?>, GSource> inputGetter() {
		return (Getter<Conversion<? extends GSource, ?>, GSource>)InputGetter.INSTANCE;
	}

	/** Diese Methode gibt den {@link Getter} zurück, der die Ausgabe einer {@link Conversion} ermittelt.
	 *
	 * @see Conversion#target()
	 * @param <GTarget> Typ der Ausgabe.
	 * @return {@link Getter} zu {@link Conversion#target()}. */
	@SuppressWarnings ("unchecked")
	public static <GTarget> Getter<Conversion<?, ? extends GTarget>, GTarget> outputGetter() {
		return (Getter<Conversion<?, ? extends GTarget>, GTarget>)OutputGetter.INSTANCE;
	}

}
