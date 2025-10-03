package bee.creative.util;

import static bee.creative.util.Translators.concatTranslator;
import static bee.creative.util.Translators.optionalizedTranslator;
import static bee.creative.util.Translators.reversedTranslator;
import static bee.creative.util.Translators.synchronizedTranslator;

/** Diese Schnittstelle ergänzt einen {@link Translator} insb. um eine Anbindung an Methoden von {@link Translators}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <SOURCE> Typ der Quellobjekte.
 * @param <TARGET> Typ der Zielobjekte. */
public interface Translator2<SOURCE, TARGET> extends Translator<SOURCE, TARGET> {

	/** Diese Methode ist eine Abkürzung für {@link #isTarget(Object) this::isTarget}. */
	default Filter<Object> isTarget() {
		return this::isTarget;
	}

	/** Diese Methode ist eine Abkürzung für {@link Filters#filterFrom(Filter) filterFrom(this::isSource)}. */
	default Filter<Object> isSource() {
		return this::isSource;
	}

	/** Diese Methode ist eine Abkürzung für {@link Getters#getterFrom(Getter) getterFrom(item -> this.toTarget(item))}. */
	default Getter3<Object, TARGET> toTarget() {
		return item -> this.toTarget(item);
	}

	/** Diese Methode ist eine Abkürzung für {@link Getters#getterFrom(Getter) getterFrom(item -> this.toSource(item))}. */
	default Getter3<Object, SOURCE> toSource() {
		return item -> this.toSource(item);
	}

	/** Diese Methode ist eine Abkürzung für {@link Translators#concatTranslator(Translator, Translator) concatTranslator(this, trans)}. */
	default <GTarget2> Translator2<SOURCE, GTarget2> concat(Translator<TARGET, GTarget2> trans) throws NullPointerException {
		return concatTranslator(this, trans);
	}

	/** Diese Methode ist eine Abkürzung für {@link Translators#reversedTranslator(Translator) reversedTranslator(this)}. */
	default Translator2<TARGET, SOURCE> reverse() {
		return reversedTranslator(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Translators#optionalizedTranslator(Translator) optionalizedTranslator(this)}. */
	default Translator2<SOURCE, TARGET> optionalize() {
		return optionalizedTranslator(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link #synchronize(Object) this.synchronize(this)}. */
	default Translator2<SOURCE, TARGET> synchronize() {
		return this.synchronize(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Translators#synchronizedTranslator(Translator) synchronizedTranslator(this, mutex)}. */
	default Translator2<SOURCE, TARGET> synchronize(Object mutex) {
		return synchronizedTranslator(this, mutex);
	}

}