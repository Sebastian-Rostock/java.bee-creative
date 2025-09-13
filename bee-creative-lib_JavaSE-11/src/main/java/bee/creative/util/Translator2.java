package bee.creative.util;

import static bee.creative.util.Filters.filterFrom;
import static bee.creative.util.Getters.getterFrom;
import static bee.creative.util.Translators.concatTranslator;
import static bee.creative.util.Translators.optionalizeTranslator;
import static bee.creative.util.Translators.reverseTranslator;
import static bee.creative.util.Translators.synchronizeTranslator;

/** Diese Schnittstelle ergänzt einen {@link Translator} insb. um eine Anbindung an Methoden von {@link Translators}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GSource> Typ der Quellobjekte.
 * @param <GTarget> Typ der Zielobjekte. */
public interface Translator2<GSource, GTarget> extends Translator<GSource, GTarget> {

	/** Diese Methode ist eine Abkürzung für {@link Filters#filterFrom(Filter) filterFrom(this::isTarget)}. */
	default Filter2<Object> isTarget() {
		return filterFrom(this::isTarget);
	}

	/** Diese Methode ist eine Abkürzung für {@link Filters#filterFrom(Filter) filterFrom(this::isSource)}. */
	default Filter2<Object> isSource() {
		return filterFrom(this::isSource);
	}

	/** Diese Methode ist eine Abkürzung für {@link Getters#getterFrom(Getter) getterFrom(item -> this.toTarget(item))}. */
	default Getter3<Object, GTarget> toTarget() {
		return getterFrom(item -> this.toTarget(item));
	}

	/** Diese Methode ist eine Abkürzung für {@link Getters#getterFrom(Getter) getterFrom(item -> this.toSource(item))}. */
	default Getter3<Object, GSource> toSource() {
		return getterFrom(item -> this.toSource(item));
	}

	/** Diese Methode ist eine Abkürzung für {@link Translators#concatTranslator(Translator, Translator) concatTranslator(this, trans)}. */
	default <GTarget2> Translator2<GSource, GTarget2> concat(Translator<GTarget, GTarget2> trans) throws NullPointerException {
		return concatTranslator(this, trans);
	}

	/** Diese Methode ist eine Abkürzung für {@link Translators#reverseTranslator(Translator) reverseTranslator(this)}. */
	default Translator2<GTarget, GSource> reverse() {
		return reverseTranslator(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Translators#optionalizeTranslator(Translator) optionalizeTranslator(this)}. */
	default Translator2<GSource, GTarget> optionalize() {
		return optionalizeTranslator(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Translators#synchronizeTranslator(Translator) synchronizeTranslator(this)}. */
	default Translator2<GSource, GTarget> synchronize() {
		return synchronizeTranslator(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Translators#synchronizeTranslator(Translator) synchronizeTranslator(this, mutex)}. */
	default Translator2<GSource, GTarget> synchronize(Object mutex) {
		return synchronizeTranslator(this, mutex);
	}

}