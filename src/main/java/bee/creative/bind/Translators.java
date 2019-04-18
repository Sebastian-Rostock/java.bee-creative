package bee.creative.bind;

import bee.creative.lang.Objects;
import bee.creative.util.Filter;

/** Diese Klasse implementiert grundlegende {@link Translator}.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Translators {

	/** Diese Klasse implementiert {@link Translators#reverseTranslator(Translator)}. */
	@SuppressWarnings ("javadoc")
	public static class ReverseTranslator<GSource, GTarget> implements Translator<GSource, GTarget> {

		public final Translator<GTarget, GSource> translator;

		public ReverseTranslator(final Translator<GTarget, GSource> translator) throws NullPointerException {
			this.translator = Objects.notNull(translator);
		}

		@Override
		public boolean isTarget(final Object object) {
			return this.translator.isSource(object);
		}

		@Override
		public boolean isSource(final Object object) {
			return this.translator.isTarget(object);
		}

		@Override
		public GTarget toTarget(final Object object) throws ClassCastException, IllegalArgumentException {
			return this.translator.toSource(object);
		}

		@Override
		public GSource toSource(final Object object) throws ClassCastException, IllegalArgumentException {
			return this.translator.toTarget(object);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.translator);
		}

	}

	/** Diese Klasse implementiert {@link Translators#chainedTranslator(Translator, Translator)}. */
	@SuppressWarnings ("javadoc")
	public static class ChainedTranslator<GSource, GTarget, GCenter> implements Translator<GSource, GTarget> {

		public final Translator<GSource, GCenter> translator1;

		public final Translator<GCenter, GTarget> translator2;

		public ChainedTranslator(final Translator<GSource, GCenter> translator1, final Translator<GCenter, GTarget> translator2) throws NullPointerException {
			this.translator1 = Objects.notNull(translator1);
			this.translator2 = Objects.notNull(translator2);
		}

		@Override
		public boolean isTarget(final Object object) {
			return this.translator2.isTarget(object);
		}

		@Override
		public boolean isSource(final Object object) {
			return this.translator1.isSource(object);
		}

		@Override
		public GTarget toTarget(final Object object) throws ClassCastException, IllegalArgumentException {
			return this.translator2.toTarget(this.translator1.toTarget(object));
		}

		@Override
		public GSource toSource(final Object object) throws ClassCastException, IllegalArgumentException {
			return this.translator1.toSource(this.translator2.toSource(object));
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.translator1, this.translator2);
		}

	}

	/** Diese Klasse implementiert {@link Translators#compositeTranslator(Class, Class, Getter, Getter)}. */
	@SuppressWarnings ("javadoc")
	public static class CompositeTranslator<GSource, GTarget> implements Translator<GSource, GTarget> {

		public final Class<GSource> isSource;

		public final Class<GTarget> isTarget;

		public final Getter<? super GSource, ? extends GTarget> toTarget;

		public final Getter<? super GTarget, ? extends GSource> toSource;

		public CompositeTranslator(final Class<GSource> isSource, final Class<GTarget> isTarget, final Getter<? super GSource, ? extends GTarget> toTarget,
			final Getter<? super GTarget, ? extends GSource> toSource) throws NullPointerException {
			this.isSource = Objects.notNull(isSource);
			this.isTarget = Objects.notNull(isTarget);
			this.toTarget = Objects.notNull(toTarget);
			this.toSource = Objects.notNull(toSource);
		}

		@Override
		public boolean isTarget(final Object object) {
			return this.isTarget.isInstance(object);
		}

		@Override
		public boolean isSource(final Object object) {
			return this.isSource.isInstance(object);
		}

		@Override
		public GTarget toTarget(final Object object) throws ClassCastException, IllegalArgumentException {
			return this.toTarget.get(this.isSource.cast(object));
		}

		@Override
		public GSource toSource(final Object object) throws ClassCastException, IllegalArgumentException {
			return this.toSource.get(this.isTarget.cast(object));
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.isSource, this.isTarget, this.toTarget, this.toSource);
		}

	}

	/** Diese Klasse implementiert {@link Translators#synchronizedTranslator(Object, Translator)}. */
	@SuppressWarnings ("javadoc")
	public static class SynchronizedTranslator<GSource, GTarget> implements Translator<GSource, GTarget> {

		public final Object mutex;

		public final Translator<GSource, GTarget> translator;

		public SynchronizedTranslator(final Object mutex, final Translator<GSource, GTarget> translator) throws NullPointerException {
			this.mutex = Objects.notNull(mutex, this);
			this.translator = Objects.notNull(translator);
		}

		@Override
		public boolean isTarget(final Object object) {
			synchronized (this.mutex) {
				return this.translator.isSource(object);
			}
		}

		@Override
		public boolean isSource(final Object object) {
			synchronized (this.mutex) {
				return this.translator.isTarget(object);
			}
		}

		@Override
		public GTarget toTarget(final Object object) throws ClassCastException, IllegalArgumentException {
			synchronized (this.mutex) {
				return this.translator.toTarget(object);
			}
		}

		@Override
		public GSource toSource(final Object object) throws ClassCastException, IllegalArgumentException {
			synchronized (this.mutex) {
				return this.translator.toSource(object);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.translator);
		}

	}

	/** Diese Klasse implementiert {@link Translators#toSourceFilter(Translator)}. */
	static class SourceFilter implements Filter<Object> {

		public final Translator<?, ?> translator;

		public SourceFilter(final Translator<?, ?> translator) throws NullPointerException {
			this.translator = Objects.notNull(translator);
		}

		@Override
		public boolean accept(final Object item) {
			return this.translator.isSource(item);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.translator);
		}

	}

	/** Diese Klasse implementiert {@link Translators#toSourceGetter(Translator)}. */
	static class SourceGetter<GSource, GTarget> implements Getter<GTarget, GSource> {

		public final Translator<? extends GSource, ? super GTarget> translator;

		public SourceGetter(final Translator<? extends GSource, ? super GTarget> translator) throws NullPointerException {
			this.translator = Objects.notNull(translator);
		}

		@Override
		public GSource get(final Object input) {
			return this.translator.toSource(input);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.translator);
		}

	}

	/** Diese Klasse implementiert {@link Translators#toTargetFilter(Translator)}. */
	static class TargetFilter implements Filter<Object> {

		public final Translator<?, ?> translator;

		public TargetFilter(final Translator<?, ?> translator) throws NullPointerException {
			this.translator = Objects.notNull(translator);
		}

		@Override
		public boolean accept(final Object item) {
			return this.translator.isTarget(item);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.translator);
		}

	}

	/** Diese Klasse implementiert {@link Translators#toTargetGetter(Translator)}. */
	static class TargetGetter<GSource, GTarget> implements Getter<GSource, GTarget> {

		public final Translator<? super GSource, ? extends GTarget> translator;

		public TargetGetter(final Translator<? super GSource, ? extends GTarget> translator) {
			this.translator = Objects.notNull(translator);
		}

		@Override
		public GTarget get(final Object input) {
			return this.translator.toTarget(input);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.translator);
		}

	}

	/** Diese Methode gibt einen neutralen {@link Translator} zurück und ist eine Abkürzung für
	 * {@link Translators#compositeTranslator(Class, Class, Getter, Getter) Translators.compositeTranslator(valueClass, valueClass,
	 * Getters.<GValue>neutralGetter(), Getters.<GValue>neutralGetter())}.
	 *
	 * @see Getters#neutralGetter() */
	public static <GValue> Translator<GValue, GValue> neutralTranslator(final Class<GValue> valueClass) throws NullPointerException {
		return Translators.compositeTranslator(valueClass, valueClass, Getters.<GValue>neutralGetter(), Getters.<GValue>neutralGetter());
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

	/** Diese Methode gibt einen verkettenden {@link Translator} zurück, welcher Quellobjekte über {@code translator2.toTarget(translator1.toTarget(object))} in
	 * Zielobjekte sowei Zielobjekte über {@code translator1.toSource(translator2.toSource(object))} in Quellobjekte überführt.
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
		return new ChainedTranslator<>(translator1, translator2);
	}

	/** Diese Methode gibt einen zusammengesetzten {@link Translator} zurück, dessen Metoden an die entsprechenden der gegebenen Objekte delegieren. Die Methoden
	 * {@link Translator#isSource(Object)} und {@link Translator#isTarget(Object)} delegieren an {@link Class#isInstance(Object)} der gegebenen Klassen. Die
	 * Methoden {@link Translator#toSource(Object)} und {@link Translator#toTarget(Object)} delegieren an {@link Class#cast(Object)} der gegebenen Klassen sowie
	 * {@link Getter#get(Object)} der gegebenen Konvertierungsmethode.
	 *
	 * @param <GSource> Typ der Quellobjekte.
	 * @param <GTarget> Typ der Zielobjekte.
	 * @param isSource {@link Class} der Quellobjekte.
	 * @param isTarget {@link Class} der Zielobjekte.
	 * @param toTarget {@link Getter} zur Übersetzung von Quellobjekten in Zielobjekte.
	 * @param toSource {@link Getter} zur Übersetzung von Zielobjekten in Quellobjekte.
	 * @return {@code composite}-{@link Translator}.
	 * @throws NullPointerException Wenn {@code isSource}, {@code isTarget}, {@code toTarget} bzw. {@code toSource} {@code null} ist. */
	public static <GSource, GTarget> Translator<GSource, GTarget> compositeTranslator(final Class<GSource> isSource, final Class<GTarget> isTarget,
		final Getter<? super GSource, ? extends GTarget> toTarget, final Getter<? super GTarget, ? extends GSource> toSource) throws NullPointerException {
		return new CompositeTranslator<>(isSource, isTarget, toTarget, toSource);
	}

	/** Diese Methode ist eine Abkürzung für {@link #synchronizedTranslator(Object, Translator) Translators.synchronizedTranslator(translator, translator)}. */
	public static <GSource, GTarget> Translator<GSource, GTarget> synchronizedTranslator(final Translator<GSource, GTarget> translator)
		throws NullPointerException {
		return Translators.synchronizedTranslator(translator, translator);
	}

	/** Diese Methode gibt einen synchronisierten {@link Translator} zurück, dessen Methoden synchronisiert über {@code synchronized(mutex)} an die entsprechenden
	 * des gegebenen {@link Translator} delegieren. Wenn das Synchronisationsobjekt {@code null} ist, wird der erzeugte {@link Translator} als
	 * Synchronisationsobjekt verwendet.
	 *
	 * @param <GSource> Typ der Quellobjekte.
	 * @param <GTarget> Typ der Zielobjekte.
	 * @param mutex Synchronisationsobjekt oder {@code null}.
	 * @param translator {@link Translator}.
	 * @return {@code synchronized}-{@link Translator}.
	 * @throws NullPointerException Wenn {@code translator} {@code null} ist. */
	public static <GSource, GTarget> Translator<GSource, GTarget> synchronizedTranslator(final Object mutex, final Translator<GSource, GTarget> translator)
		throws NullPointerException {
		return new SynchronizedTranslator<>(mutex, translator);
	}

	/** Diese Methode gibt einen {@link Filter} zu {@link Translator#isSource(Object)} des gegebenen {@link Translator} zurück. Die Akzeptanz eines Datensatzes
	 * {@code item} ist {@code translator.isSource(item)}.
	 *
	 * @param translator {@link Translator}.
	 * @return {@link Filter}, der nur Quellobjekte des {@code translator} akzeptiert.
	 * @throws NullPointerException Wenn {@code translator} {@code null} ist. */
	public static Filter<Object> toSourceFilter(final Translator<?, ?> translator) throws NullPointerException {
		return new SourceFilter(translator);
	}

	/** Diese Methode gibt einen {@link Getter} zu {@link Translator#toSource(Object)} des gegebenen {@link Translator} zurück. Für einen Datensatz {@code item}
	 * liefert er den Wert {@code translator.toSource(item)}.
	 *
	 * @param <GSource> Typ der Quellobjekte des {@link Translator} sowie des Werts des erzeugten {@link Getter}.
	 * @param <GTarget> Typ der Zielobjekte des {@link Translator} sowie des Datensatzes des erzeugten {@link Getter}.
	 * @param translator {@link Translator}.
	 * @return {@link Getter}, der Zielobjekte in Quellobjekte des {@code translator} umwandelt.
	 * @throws NullPointerException Wenn {@code translator} {@code null} ist. */
	public static <GSource, GTarget> Getter<GTarget, GSource> toSourceGetter(final Translator<? extends GSource, ? super GTarget> translator)
		throws NullPointerException {
		return new SourceGetter<>(translator);
	}

	/** Diese Methode gibt einen {@link Filter} zu {@link Translator#isTarget(Object)} des gegebenen {@link Translator} zurück. Die Akzeptanz eines Datensatzes
	 * {@code item} ist {@code translator.isTarget(item)}.
	 *
	 * @param translator {@link Translator}.
	 * @return {@link Filter}, der nur Zielobjekte des {@code translator} akzeptiert.
	 * @throws NullPointerException Wenn {@code translator} {@code null} ist. */
	public static Filter<Object> toTargetFilter(final Translator<?, ?> translator) throws NullPointerException {
		return new TargetFilter(translator);
	}

	/** Diese Methode gibt einen {@link Getter} zu {@link Translator#toTarget(Object)} des gegebenen {@link Translator} zurück. Für einen Datensatz {@code item}
	 * liefert er den Wert {@code translator.toTarget(item)}.
	 *
	 * @param <GSource> Typ der Quellobjekte des {@link Translator} sowie des Datensatzes des erzeugten {@link Getter}.
	 * @param <GTarget> Typ der Zielobjekte des {@link Translator} sowie des Werts des erzeugten {@link Getter}.
	 * @param translator {@link Translator}.
	 * @return {@link Getter}, der Quellobjekte in Zielobjekte des {@code translator} umwandelt.
	 * @throws NullPointerException Wenn {@code translator} {@code null} ist. */
	public static <GSource, GTarget> Getter<GSource, GTarget> toTargetGetter(final Translator<? super GSource, ? extends GTarget> translator)
		throws NullPointerException {
		return new TargetGetter<>(translator);
	}

}
