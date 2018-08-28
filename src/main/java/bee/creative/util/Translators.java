package bee.creative.util;

/** Diese Klasse implementiert grundlegende {@link Translator}.
 *
 * @see Translator
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Translators {

	public static final class NeutralTranslator<GValue> implements Translator<GValue, GValue> {

		private final Class<GValue> itemClass;

		public NeutralTranslator(Class<GValue> itemClass) {
			this.itemClass = 		Objects.assertNotNull(itemClass);
		}

		@Override
		public boolean isTarget(final Object object) {
			return itemClass.isInstance(object);
		}

		@Override
		public boolean isSource(final Object object) {
			return itemClass.isInstance(object);
		}

		@Override
		public GValue toTarget(final Object object) throws ClassCastException, IllegalArgumentException {
			return itemClass.cast(object);
		}

		@Override
		public GValue toSource(final Object object) throws ClassCastException, IllegalArgumentException {
			return itemClass.cast(object);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, itemClass);
		}
		
	}

	public static final class ReverseTranslator<GSource, GTarget> implements Translator<GSource, GTarget> {

		private final Translator<GTarget, GSource> translator;

		public ReverseTranslator(Translator<GTarget, GSource> translator) {
			this.translator = Objects.assertNotNull(translator);
		}

		@Override
		public boolean isTarget(final Object object) {
			return translator.isSource(object);
		}

		@Override
		public boolean isSource(final Object object) {
			return translator.isTarget(object);
		}

		@Override
		public GTarget toTarget(final Object object) throws ClassCastException, IllegalArgumentException {
			return translator.toSource(object);
		}

		@Override
		public GSource toSource(final Object object) throws ClassCastException, IllegalArgumentException {
			return translator.toTarget(object);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, translator);
		}
	}

	public static final class SynchronizedTranslator<GSource, GTarget> implements Translator<GSource, GTarget> {

		private final Translator<GSource, GTarget> translator;

		private final Object mutex;

		public SynchronizedTranslator(Translator<GSource, GTarget> translator, Object mutex) {
			this.translator = Objects.assertNotNull(translator);
			this.mutex = Objects.assertNotNull(mutex);
		}

		@Override
		public boolean isTarget(final Object object) {
			synchronized (mutex) {
				return translator.isSource(object);
			}
		}

		@Override
		public boolean isSource(final Object object) {
			synchronized (mutex) {
				return translator.isTarget(object);
			}
		}

		@Override
		public GTarget toTarget(final Object object) throws ClassCastException, IllegalArgumentException {
			synchronized (mutex) {
				return translator.toTarget(object);
			}
		}

		@Override
		public GSource toSource(final Object object) throws ClassCastException, IllegalArgumentException {
			synchronized (mutex) {
				return translator.toSource(object);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, translator, mutex);
		}

	}

	/** Diese Methode gibt einen {@link Translator} zurück, dessen Metoden an die gegebenen Objekte delegieren.<br>
	 * Die Methoden {@link Translator#isSource(Object)} und {@link Translator#isTarget(Object)} delegieren an {@link Class#isInstance(Object)} der gegebenen
	 * Klassen. Die Methoden {@link Translator#toSource(Object)} und {@link Translator#toTarget(Object)} delegieren an {@link Class#cast(Object)} der gegebenen
	 * Klassen sowie {@link Getter#get(Object)} der gegebenen Konvertierungsmethode.
	 *
	 * @param <GSource> Typ der Quellobjekte.
	 * @param <GTarget> Typ der Zielobjekte.
	 * @param sourceClass {@link Class} der Quellobjekte.
	 * @param targetClass {@link Class} der Zielobjekte.
	 * @param sourceGetter {@link Getter} zur übersetzung von Quellobjekten in Zielobjekte.
	 * @param targetGetter {@link Getter} zur übersetzung von Zielobjekten in Quellobjekte.
	 * @return {@link Translator}.
	 * @throws NullPointerException Wenn {@code sourceClass}, {@code targetClass}, {@code sourceGetter} bzw. {@code targetGetter} {@code null} ist. */
	public static <GSource, GTarget> Translator<GSource, GTarget> simpleTranslator(final Class<GSource> sourceClass, final Class<GTarget> targetClass,
		final Getter<GSource, GTarget> sourceGetter, final Getter<GTarget, GSource> targetGetter) throws NullPointerException {
		Objects.assertNotNull(sourceClass);
		Objects.assertNotNull(targetClass);
		Objects.assertNotNull(sourceGetter);
		Objects.assertNotNull(targetGetter);
		return new Translator<GSource, GTarget>() {

			@Override
			public boolean isTarget(final Object object) {
				return targetClass.isInstance(object);
			}

			@Override
			public boolean isSource(final Object object) {
				return sourceClass.isInstance(object);
			}

			@Override
			public GTarget toTarget(final Object object) throws ClassCastException, IllegalArgumentException {
				return sourceGetter.get(sourceClass.cast(object));
			}

			@Override
			public GSource toSource(final Object object) throws ClassCastException, IllegalArgumentException {
				return targetGetter.get(targetClass.cast(object));
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("simpleTranslator", sourceClass, targetClass, sourceGetter, targetGetter);
			}

		};
	}

	/** Diese Methode gibt einen neutralen {@link Translator} zurück, dessen Quellobjekte gleich seinen Zielobjekten sind.<br>
	 * Die Methoden {@link Translator#isSource(Object)} und {@link Translator#isTarget(Object)} delegieren an {@link Class#isInstance(Object)}. Die Methoden
	 * {@link Translator#toSource(Object)} und {@link Translator#toTarget(Object)} delegieren an {@link Class#cast(Object)}.
	 *
	 * @param <GValue> Typ der Quell- und Zielobjekte.
	 * @param itemClass {@link Class} der Quell- und Zielobjekte.
	 * @return {@code neutral}-{@link Translator}.
	 * @throws NullPointerException Wenn {@code itemClass} {@code null} ist. */
	public static <GValue> Translator<GValue, GValue> neutralTranslator(final Class<GValue> itemClass) throws NullPointerException {
		return new NeutralTranslator<>(itemClass);
	}

	/** Diese Methode gibt einen {@link Translator} zurück, der die Übersetzung des gegebenen {@link Translator} umkehrt, d.h. dessen Quellobjekte gleich den
	 * Zielobjekten von {@code translator} und dessen Zielobjekte gleich den Quellobjekten von {@code translator} sind.
	 *
	 * @param <GSource> Typ der Quellobjekte des erzeugten sowie der Zielobjekte des gegebenen {@link Translator}.
	 * @param <GTarget> Typ der Zielobjekte des erzeugten sowie der Quellobjekte des gegebenen {@link Translator}.
	 * @param translator {@link Translator}.
	 * @return {@code reverse}-{@link Translator}.
	 * @throws NullPointerException Wenn {@code translator} {@code null} ist. */
	public static <GSource, GTarget> Translator<GSource, GTarget> reverseTranslator(final Translator<GTarget, GSource> translator) throws NullPointerException {
		return new ReverseTranslator<>(translator);
	}

	/** Diese Methode gibt einen verkettenden {@link Translator} zurück, der bei der Umwandlung von Quellobjekten über
	 * {@code translator2.toTarget(translator1.toTarget(object))} in Zielobjekte sowie Zielobjekte über {@code translator1.toSource(translator2.toSource(object))}
	 * in Quellobjekte überführt.
	 *
	 * @param <GSource> Typ der Quellobjekte des erzeugten sowie des ersten {@link Translator}.
	 * @param <GCenter> Typ der Zielobjekte des ersten sowie der Quellobjekte zweiten {@link Translator}.
	 * @param <GTarget> Typ der Zielobjekte des erzeugten sowie des zweiten {@link Translator}.
	 * @param translator1 erster {@link Translator}.
	 * @param translator2 zweiter {@link Translator}.
	 * @return {@code chained}-{@link Translator}.
	 * @throws NullPointerException Wenn {@code translator1} bzw. {@code translator2} {@code null} ist. */
	public static <GSource, GCenter, GTarget> Translator<GSource, GTarget> chainedTranslator(final Translator<GSource, GCenter> translator1,
		final Translator<GCenter, GTarget> translator2) throws NullPointerException {
		Objects.assertNotNull(translator1);
		Objects.assertNotNull(translator2);
		return new Translator<GSource, GTarget>() {

			@Override
			public boolean isTarget(final Object object) {
				return translator2.isTarget(object);
			}

			@Override
			public boolean isSource(final Object object) {
				return translator1.isSource(object);
			}

			@Override
			public GTarget toTarget(final Object object) throws ClassCastException, IllegalArgumentException {
				return translator2.toTarget(translator1.toTarget(object));
			}

			@Override
			public GSource toSource(final Object object) throws ClassCastException, IllegalArgumentException {
				return translator1.toSource(translator2.toSource(object));
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("chainedTranslator", translator1, translator2);
			}

		};
	}

	/** Diese Methode ist eine Abkürzung für {@code Translators.synchronizedTranslator(translator, translator)}.
	 *
	 * @see #synchronizedTranslator(Translator, Object) */
	@SuppressWarnings ("javadoc")
	public static <GSource, GTarget> Translator<GSource, GTarget> synchronizedTranslator(final Translator<GSource, GTarget> translator)
		throws NullPointerException {
		return Translators.synchronizedTranslator(translator, translator);
	}

	/** Diese Methode gibt einen {@link Translator} zurück, der den gegebenen {@link Translator} via {@code synchronized(mutex)} synchronisiert.
	 *
	 * @param <GSource> Typ der Quellobjekte.
	 * @param <GTarget> Typ der Zielobjekte.
	 * @param translator {@link Translator}.
	 * @param mutex Synchronisationsobjekt.
	 * @return {@code synchronized}-{@link Translator}.
	 * @throws NullPointerException Wenn der {@code translator} bzw. {@code mutex} {@code null} ist. */
	public static <GSource, GTarget> Translator<GSource, GTarget> synchronizedTranslator(final Translator<GSource, GTarget> translator, final Object mutex)
		throws NullPointerException {
		return new SynchronizedTranslator<>(translator, mutex);
	}

	/** Diese Methode gibt einen {@link Filter} zu {@link Translator#isSource(Object)} des gegebenen {@link Translator} zurück.<br>
	 * Die Akzeptanz einer Eingabe {@code input} ist {@code translator.isSource(input)}.
	 *
	 * @param translator {@link Translator}.
	 * @return {@link Filter}, der nur Quellobjekte von {@code translator} akzeptiert.
	 * @throws NullPointerException Wenn {@code translator} {@code null} ist. */
	public static Filter<Object> toSourceFilter(final Translator<?, ?> translator) throws NullPointerException {
		Objects.assertNotNull(translator);
		return new Filter<Object>() {

			@Override
			public boolean accept(final Object input) {
				return translator.isSource(input);
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("isSource", translator);
			}

		};
	}

	/** Diese Methode gibt einen {@link Getter} zu {@link Translator#toSource(Object)} des gegebenen {@link Translator} zurück.<br>
	 * Für eine Eingabe {@code input} liefert er die Ausgabe {@code translator.toSource(input)}.
	 *
	 * @param <GSource> Typ der Quellobjekte des {@link Translator}.
	 * @param translator {@link Translator}.
	 * @return {@link Getter}, der Zielobjekte in Quellobjekte des {@code translator} umwandelt.
	 * @throws NullPointerException Wenn {@code translator} {@code null} ist. */
	public static <GSource> Getter<Object, GSource> toSourceGetter(final Translator<GSource, ?> translator) throws NullPointerException {
		Objects.assertNotNull(translator);
		return new Getter<Object, GSource>() {

			@Override
			public GSource get(final Object input) {
				return translator.toSource(input);
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("toSource", translator);
			}

		};
	}

	/** Diese Methode gibt einen {@link Filter} zu {@link Translator#isTarget(Object)} des gegebenen {@link Translator} zurück.<br>
	 * Die Akzeptanz einer Eingabe {@code input} ist {@code translator.isTarget(input)}.
	 *
	 * @param translator {@link Translator}.
	 * @return {@link Filter}, der nur Zielobjekte von {@code translator} akzeptiert.
	 * @throws NullPointerException Wenn {@code translator} {@code null} ist. */
	public static Filter<Object> toTargetFilter(final Translator<?, ?> translator) throws NullPointerException {
		Objects.assertNotNull(translator);
		return new Filter<Object>() {

			@Override
			public boolean accept(final Object input) {
				return translator.isTarget(input);
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("isTarget", translator);
			}

		};
	}

	/** Diese Methode gibt einen {@link Getter} zu {@link Translator#toTarget(Object)} des gegebenen {@link Translator} zurück.<br>
	 * Für eine Eingabe {@code input} liefert er die Ausgabe {@code translator.toTarget(input)}.
	 *
	 * @param <GTarget> Typ der Zielobjekte des {@link Translator}.
	 * @param translator {@link Translator}.
	 * @return {@link Getter}, der Quellobjekte in Zielobjekte des {@code translator} umwandelt.
	 * @throws NullPointerException Wenn {@code translator} {@code null} ist. */
	public static <GTarget> Getter<Object, GTarget> toTargetGetter(final Translator<?, GTarget> translator) throws NullPointerException {
		Objects.assertNotNull(translator);
		return new Getter<Object, GTarget>() {

			@Override
			public GTarget get(final Object input) {
				return translator.toTarget(input);
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("toTarget", translator);
			}

		};
	}

}
