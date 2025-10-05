package bee.creative.util;

import static bee.creative.util.Translators.concatTranslator;
import static bee.creative.util.Translators.optionalizedTranslator;
import static bee.creative.util.Translators.reversedTranslator;
import static bee.creative.util.Translators.synchronizedTranslator;

/** Diese Schnittstelle ergänzt einen {@link Translator} insb. um eine Anbindung an Methoden von {@link Translators}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <S> Typ der Quellobjekte.
 * @param <T> Typ der Zielobjekte. */
public interface Translator3<S, T> extends Translator2<S, T> {

	/** Diese Methode liefert den {@link Filter3} zu {@link #isTarget(Object)}. */
	default Filter3<Object> isTarget() {
		return item -> this.isTarget(item);
	}

	/** Diese Methode liefert den {@link Filter3} zu {@link #isSource(Object)}. */
	default Filter3<Object> isSource() {
		return item -> this.isSource(item);
	}

	/** Diese Methode liefert den {@link Getter3} zu {@link #toTarget(Object)}. */
	default Getter3<Object, T> toTarget() {
		return item -> this.toTarget(item);
	}

	/** Diese Methode liefert den {@link Getter3} zu {@link #toSource(Object)}. */
	default Getter3<Object, S> toSource() {
		return item -> this.toSource(item);
	}

	/** Diese Methode ist eine Abkürzung für {@link Translators#concatTranslator(Translator, Translator) concatTranslator(this, trans)}. */
	default <GTarget2> Translator3<S, GTarget2> concat(Translator<T, GTarget2> trans) throws NullPointerException {
		return concatTranslator(this, trans);
	}

	/** Diese Methode ist eine Abkürzung für {@link Translators#reversedTranslator(Translator) reversedTranslator(this)}. */
	default Translator3<T, S> reverse() {
		return reversedTranslator(this);
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

	@Override
	default Translator3<S, T> asTranslator() {
		return this;
	}

}