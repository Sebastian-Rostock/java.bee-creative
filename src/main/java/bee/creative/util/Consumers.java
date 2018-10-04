package bee.creative.util;

import bee.creative.util.Objects.BaseObject;

public class Consumers {

	/** Diese Klasse implementiert einen abstrakten {@link Consumer} als {@link BaseObject}. */
	@SuppressWarnings ("javadoc")
	public static abstract class BaseConsumer<GValue> extends BaseObject implements Consumer<GValue> {
	}

	public static <GValue> Consumer<GValue> emptyConsumer() {
		return null;
	}

	public static <GValue> Consumer<GValue> nativeConsumer(final String memberText) {
		return null;
	}

	public static <GValue> Consumer<GValue> nativeConsumer(final java.lang.reflect.Field field) {
		// static field
		return null;
	}

	public static <GValue> Consumer<GValue> nativeConsumer(final java.lang.reflect.Method method) {
		// static method
		return null;
	}

	public static <GTarget, GSource> Consumer<GTarget> translatedConsumer(final Getter<? super GTarget, ? extends GSource> toSource,
		final Consumer<? super GSource> consumer) {
		return null;
	}

	public static <GValue> Consumer<GValue> synchronizedConsumer(final Consumer<? super GValue> consumer) {
		return Consumers.synchronizedConsumer(consumer, consumer);
	}

	public static <GValue> Consumer<GValue> synchronizedConsumer(final Object mutex, final Consumer<? super GValue> consumer) {
		return null;
	}

	public static <GValue> Field<Object, GValue> toField(final Consumer<? super GValue> consumer) {
		return Setters.toField(Consumers.toSetter(consumer));
	}

	public static <GValue> Setter<Object, GValue> toSetter(final Consumer<? super GValue> consumer) {
		return null;
	}

	public static <GValue> Property<GValue> toProperty(final Consumer<? super GValue> consumer) {
		return null;
	}

}
