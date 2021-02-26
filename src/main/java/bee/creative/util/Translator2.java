package bee.creative.util;

/** Diese Schnittstelle ergänzt einen {@link Translator} insb. um eine Anbindung an Methoden von {@link Translators}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GSource> Typ der Quellobjekte.
 * @param <GTarget> Typ der Zielobjekte. */
public interface Translator2<GSource, GTarget> extends Translator<GSource, GTarget> {

	/** Diese Methode ist eine Abkürzung für {@link Translators#concat(Translator, Translator) Translators.concat(this, trans)}. */
	public <GTarget2> Translator2<GSource, GTarget2> concat(Translator<GTarget, GTarget2> trans) throws NullPointerException;

	/** Diese Methode ist eine Abkürzung für {@link Translators#reverse(Translator) Translators.reverse(this)}. */
	public Translator2<GTarget, GSource> reverse();

	/** Diese Methode ist eine Abkürzung für {@link Translators#synchronize(Translator) Translators.synchronize(this)}. */
	public Translator2<GSource, GTarget> synchronize();

	/** Diese Methode ist eine Abkürzung für {@link Translators#synchronize(Translator) Translators.synchronize(this, mutex)}. */
	public Translator2<GSource, GTarget> synchronize(Object mutex);

}