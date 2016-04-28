package bee.creative.util;

/** Diese Klasse implementiert grundlegende {@link Translator}.
 * 
 * @see Translator
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Translators {

	/** Diese Methode gibt einen {@link Translator} zurück, dessen Metoden an die gegebenen Objekte delegieren.<br>
	 * Die Methoden {@link Translator#isSource(Object)} und {@link Translator#isTarget(Object)} delegieren an {@link Class#isInstance(Object)} der gegebenen
	 * Klassen. Die Methoden {@link Translator#toSource(Object)} und {@link Translator#toTarget(Object)} delegieren an {@link Class#cast(Object)} der gegebenen
	 * Klassen sowie {@link Converter#convert(Object)} der gegebenen Konvertierungsmethode.
	 * 
	 * @param <GSource> Typ der Quellobjekte.
	 * @param <GTarget> Typ der Zielobjekte.
	 * @param sourceClass {@link Class} der Quellobjekte.
	 * @param targetClass {@link Class} der Zielobjekte.
	 * @param sourceConverter {@link Converter} zur übersetzung von Quellobjekten in Zielobjekte.
	 * @param targetConverter {@link Converter} zur übersetzung von Zielobjekten in Quellobjekte.
	 * @return {@link Translator}.
	 * @throws NullPointerException Wenn {@code sourceClass}, {@code targetClass}, {@code sourceConverter} bzw. {@code targetConverter} {@code null} ist. */
	public static <GSource, GTarget> Translator<GSource, GTarget> simpleTranslator(final Class<GSource> sourceClass, final Class<GTarget> targetClass,
		final Converter<GSource, GTarget> sourceConverter, final Converter<GTarget, GSource> targetConverter) throws NullPointerException {
		if (sourceClass == null) throw new NullPointerException("sourceClass = null");
		if (targetClass == null) throw new NullPointerException("targetClass = null");
		if (sourceConverter == null) throw new NullPointerException("sourceConverter = null");
		if (targetConverter == null) throw new NullPointerException("targetConverter = null");
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
				return sourceConverter.convert(sourceClass.cast(object));
			}

			@Override
			public GSource toSource(final Object object) throws ClassCastException, IllegalArgumentException {
				return targetConverter.convert(targetClass.cast(object));
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("simpleTranslator", sourceClass, targetClass, sourceConverter, targetConverter);
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
		if (itemClass == null) throw new NullPointerException("itemClass = null");
		return new Translator<GValue, GValue>() {

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
				return Objects.toInvokeString("neutralTranslator", itemClass);
			}

		};
	}

	/** Diese Methode gibt einen {@link Translator} zurück, der die Übersetzung des gegebenen {@link Translator} umkehrt, d.h. dessen Quellobjekte gleich den
	 * Zielobjekten von {@code translator} und dessen Zielobjekte gleich den Quellobjekten von {@code translator} sind.
	 * 
	 * @param <GSource> Typ der Quellobjekte des erzeugten sowie der Zielobjekte des gegebenen {@link Translator}.
	 * @param <GTarget> Typ der Zielobjekte des erzeugten sowie der Quellobjekte des gegebenen {@link Translator}.
	 * @param translator {@link Translator}.
	 * @return {@code reverse}-{@link Translator}.
	 * @throws NullPointerException Wenn {@code translator} {@code null} ist. */
	public static <GSource, GTarget> Translator<GSource, GTarget> reverseTranslator(final Translator<GTarget, GSource> translator)
		throws NullPointerException {
		if (translator == null) throw new NullPointerException("translator = null");
		return new Translator<GSource, GTarget>() {

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
				return Objects.toInvokeString("reverseTranslator", translator);
			}

		};
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
		if (translator1 == null) throw new NullPointerException("translator1 = null");
		if (translator2 == null) throw new NullPointerException("translator2 = null");
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

	/** Diese Methode gibt einen {@link Filter} zu {@link Translator#isSource(Object)} des gegebenen {@link Translator} zurück.<br>
	 * Die Akzeptanz einer Eingabe {@code input} ist {@code translator.isSource(input)}.
	 * 
	 * @param translator {@link Translator}.
	 * @return {@link Filter}, der nur Quellobjekte von {@code translator} akzeptiert.
	 * @throws NullPointerException Wenn {@code translator} {@code null} ist. */
	public static Filter<Object> isSource(final Translator<?, ?> translator) throws NullPointerException {
		if (translator == null) throw new NullPointerException("translator = null");
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

	/** Diese Methode gibt einen {@link Filter} zu {@link Translator#isTarget(Object)} des gegebenen {@link Translator} zurück.<br>
	 * Die Akzeptanz einer Eingabe {@code input} ist {@code translator.isTarget(input)}.
	 * 
	 * @param translator {@link Translator}.
	 * @return {@link Filter}, der nur Zielobjekte von {@code translator} akzeptiert.
	 * @throws NullPointerException Wenn {@code translator} {@code null} ist. */
	public static Filter<Object> isTarget(final Translator<?, ?> translator) throws NullPointerException {
		if (translator == null) throw new NullPointerException("translator = null");
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

	/** Diese Methode gibt einen {@link Converter} zu {@link Translator#toTarget(Object)} des gegebenen {@link Translator} zurück.<br>
	 * Für eine Eingabe {@code input} liefert er die Ausgabe {@code translator.toTarget(input)}.
	 * 
	 * @param <GTarget> Typ der Zielobjekte des {@link Translator}.
	 * @param translator {@link Translator}.
	 * @return {@link Converter}, der Quellobjekte in Zielobjekte des {@code translator} umwandelt.
	 * @throws NullPointerException Wenn {@code translator} {@code null} ist. */
	public static <GTarget> Converter<Object, GTarget> toTarget(final Translator<?, GTarget> translator) throws NullPointerException {
		if (translator == null) throw new NullPointerException("translator = null");
		return new Converter<Object, GTarget>() {

			@Override
			public GTarget convert(final Object input) {
				return translator.toTarget(input);
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("toTarget", translator);
			}

		};
	}

	/** Diese Methode gibt einen {@link Converter} zu {@link Translator#toSource(Object)} des gegebenen {@link Translator} zurück.<br>
	 * Für eine Eingabe {@code input} liefert er die Ausgabe {@code translator.toSource(input)}.
	 * 
	 * @param <GSource> Typ der Quellobjekte des {@link Translator}.
	 * @param translator {@link Translator}.
	 * @return {@link Converter}, der Zielobjekte in Quellobjekte des {@code translator} umwandelt.
	 * @throws NullPointerException Wenn {@code translator} {@code null} ist. */
	public static <GSource> Converter<Object, GSource> toSource(final Translator<GSource, ?> translator) throws NullPointerException {
		if (translator == null) throw new NullPointerException("translator = null");
		return new Converter<Object, GSource>() {

			@Override
			public GSource convert(final Object input) {
				return translator.toSource(input);
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("toSource", translator);
			}

		};
	}

}
