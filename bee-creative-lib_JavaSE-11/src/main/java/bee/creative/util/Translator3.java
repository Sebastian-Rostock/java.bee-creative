package bee.creative.util;

import static bee.creative.util.Translators.concatTranslator;
import static bee.creative.util.Translators.optionalizedTranslator;
import static bee.creative.util.Translators.synchronizedTranslator;
import static bee.creative.util.Translators.translatorFrom;

/** Diese Schnittstelle ergänzt einen {@link Translator} insb. um eine Anbindung an Methoden von {@link Translators}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <S> Typ der Quellobjekte.
 * @param <T> Typ der Zielobjekte. */
public interface Translator3<S, T> extends Translator<S, T> {

	/** Diese Methode delegiert an {@link #asTargetFilter()}. */
	@Override
	default boolean isTarget(Object object) {
		return this.asTargetFilter().accepts(object);
	}

	/** Diese Methode delegiert an {@link #asSourceFilter()}. */
	@Override
	default boolean isSource(Object object) {
		return this.asSourceFilter().accepts(object);
	}

	/** Diese Methode delegiert an {@link #asTargetGetter()}. */
	@Override
	default T toTarget(Object object) throws ClassCastException, IllegalArgumentException {
		return this.asTargetGetter().get(object);
	}

	/** Diese Methode delegiert an {@link #asSourceGetter()}. */
	@Override
	default S toSource(Object object) throws ClassCastException, IllegalArgumentException {
		return this.asSourceGetter().get(object);
	}

	/** Diese Methode ist eine Abkürzung für {@link Translators#concatTranslator(Translator, Translator) concatTranslator(this, trans)}. */
	default <GTarget2> Translator3<S, GTarget2> concat(Translator<T, GTarget2> trans) throws NullPointerException {
		return concatTranslator(this, trans);
	}

	/** Diese Methode liefert einen {@link Translator3}, der die Übersetzung dises {@link Translator} umkehrt. Sie ist eine Abkürzung für
	 * {@link Translators#translatorFrom translatorFrom(this.asSourceFilter(), this.asTargetFilter(), this.asSourceGetter(), this.asTargetGetter())} */
	default Translator3<T, S> reverse() {
		return translatorFrom(this.asSourceFilter(), this.asTargetFilter(), this.asSourceGetter(), this.asTargetGetter());
	}

	/** Diese Methode ist eine Abkürzung für {@link Translators#optionalizedTranslator(Translator) optionalizedTranslator(this)}. */
	default Translator3<S, T> optionalize() {
		return optionalizedTranslator(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link #synchronize(Object) this.synchronize(this)}. */
	default Translator3<S, T> synchronize() {
		return this.synchronize(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Translators#synchronizedTranslator(Translator, Object) synchronizedTranslator(this, mutex)}. */
	default Translator3<S, T> synchronize(Object mutex) {
		return synchronizedTranslator(this, mutex);
	}

	/** Diese Methode liefert den {@link Filter3} zu {@link #isTarget(Object)}. */
	default Filter3<Object> asTargetFilter() {
		return this::isTarget;
	}

	/** Diese Methode liefert den {@link Filter3} zu {@link #isSource(Object)}. */
	default Filter3<Object> asSourceFilter() {
		return this::isSource;
	}

	/** Diese Methode liefert den {@link Getter3} zu {@link #toTarget(Object)}. */
	default Getter3<Object, T> asTargetGetter() {
		return this::toTarget;
	}

	/** Diese Methode liefert den {@link Getter3} zu {@link #toSource(Object)}. */
	default Getter3<Object, S> asSourceGetter() {
		return this::toSource;
	}

}