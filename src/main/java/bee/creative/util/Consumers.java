package bee.creative.util;

import bee.creative.util.Objects.BaseObject;
import bee.creative.util.Setters.AggregatedSetter;
import bee.creative.util.Setters.ConditionalSetter;

public class Consumers {

	/** Diese Klasse implementiert einen {@link Consumer} als {@link BaseObject}. */
	@SuppressWarnings ("javadoc")
	public static abstract class BaseConsumer<GValue> extends BaseObject implements Consumer<GValue> {
	}

	public static <GValue> Consumer<GValue> emptyConsumer() {
		return null;
	}

	public static <GValue> Consumer<GValue> nativeConsumer(String memberText) {
		return null;
	}

	public static <GValue> Consumer<GValue> nativeConsumer(java.lang.reflect.Field field) {
		return null;
	}

	public static <GValue> Consumer<GValue> nativeConsumer(java.lang.reflect.Method method) {
		return null;
	}

	public static <GValue, GValue2> Consumer<GValue> navigatedConsumer(Getter<? super GValue, ? extends GValue2> navigator, Consumer<? super GValue2> consumer) {
		return null;
	}

	public static <GValue> Consumer<GValue> synchronizedConsumer(Consumer<? super GValue> consumer) {
		return synchronizedConsumer(consumer, consumer);
	}

	public static <GValue> Consumer<GValue> synchronizedConsumer(final Object mutex, Consumer<? super GValue> consumer) {
		return null;
	}

	public static <GValue> Setter<Object, GValue> toSetter(Consumer<? super GValue> consumer) {
		return null;
	}

}
