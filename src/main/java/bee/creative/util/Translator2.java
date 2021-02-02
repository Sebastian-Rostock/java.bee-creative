package bee.creative.util;

/** Diese Schnittstelle ergänzt einen {@link Translator} insb. um eine Anbindung an Methoden von {@link Translators}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GSource> Typ der Quellobjekte.
 * @param <GTarget> Typ der Zielobjekte. */
public interface Translator2<GSource, GTarget> extends Translator<GSource, GTarget> {

	/** Diese Methode ist eine Abkürtung für {@link Translators#toConcat(Translator, Translator) Translators.concat(this, trans)}. */
	public <GTarget2> Translator2<GSource, GTarget2> toConcat(final Translator<GTarget, GTarget2> trans) throws NullPointerException;

	/** Diese Methode ist eine Abkürtung für {@link Translators#toReverse(Translator) Translators.toReverse(this)}. */
	public Translator2<GTarget, GSource> toReverse();

	/** Diese Methode ist eine Abkürtung für {@link Translators#toSynchronized(Translator) Translators.toSynchronized(this)}. */
	public Translator2<GSource, GTarget> toSynchronized();

	/** Diese Methode ist eine Abkürtung für {@link Translators#toSynchronized(Translator) Translators.toSynchronized(this, mutex)}. */
	public Translator2<GSource, GTarget> toSynchronized(final Object mutex);

}