package bee.creative.util;

/** Diese Schnittstelle ergänzt einen {@link Translator} insb. um eine Anbindung an Methoden von {@link Translators}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GSource> Typ der Quellobjekte.
 * @param <GTarget> Typ der Zielobjekte. */
public interface Translator2<GSource, GTarget> extends Translator<GSource, GTarget> {

	/** Diese Methode ist eine Abkürzung für {@link Filters#from(Filter) Filters.from(this::isTarget)}. */
	default Filter2<Object> isTarget() {
		return Filters.from(this::isTarget);
	}

	/** Diese Methode ist eine Abkürzung für {@link Filters#from(Filter) Filters.from(this::isSource)}. */
	default Filter2<Object> isSource() {
		return Filters.from(this::isSource);
	}

	/** Diese Methode ist eine Abkürzung für {@link Getters#from(Getter) Getters.from(item -> this.toTarget(item))}. */
	default Getter3<Object, GTarget> toTarget() {
		return Getters.from(item -> this.toTarget(item));
	}

	/** Diese Methode ist eine Abkürzung für {@link Getters#from(Getter) Getters.from(item -> this.toSource(item))}. */
	default Getter3<Object, GSource> toSource() {
		return Getters.from(item -> this.toSource(item));
	}

	/** Diese Methode ist eine Abkürzung für {@link Translators#concat(Translator, Translator) Translators.concat(this, trans)}. */
	default <GTarget2> Translator2<GSource, GTarget2> concat(Translator<GTarget, GTarget2> trans) throws NullPointerException {
		return Translators.concat(this, trans);
	}

	/** Diese Methode ist eine Abkürzung für {@link Translators#reverse(Translator) Translators.reverse(this)}. */
	default Translator2<GTarget, GSource> reverse() {
		return Translators.reverse(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Translators#optionalize(Translator) Translators.optionalize(this)}. */
	default Translator2<GSource, GTarget> optionalize() {
		return Translators.optionalize(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Translators#synchronize(Translator) Translators.synchronize(this)}. */
	default Translator2<GSource, GTarget> synchronize() {
		return Translators.synchronize(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Translators#synchronize(Translator) Translators.synchronize(this, mutex)}. */
	default Translator2<GSource, GTarget> synchronize(Object mutex) {
		return Translators.synchronize(this, mutex);
	}

}