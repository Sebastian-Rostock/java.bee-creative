package bee.creative.util;

import static bee.creative.util.Translators.translatorFrom;

/** Diese Schnittstelle definiert einen {@link Translator} mit {@link Translator3}-Schnittstelle.
 *
 * @author [cc-by] 2025 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <S> Typ der Quellobjekte.
 * @param <T> Typ der Zielobjekte. */
public interface Translator2<S, T> extends Translator<S, T> {

	/** Diese Methode liefert die {@link Translator3}-Schnittstelle zu {@link #isTarget(Object)}, {@link #isSource(Object)}, {@link #toTarget(Object)} und
	 * {@link #toSource(Object)}. */
	default Translator3<S, T> asTranslator() {
		return translatorFrom(object -> this.isTarget(object), object -> this.isSource(object), object -> this.toTarget(object), object -> this.toSource(object));
	}

}