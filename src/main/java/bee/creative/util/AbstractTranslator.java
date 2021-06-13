package bee.creative.util;

import bee.creative.lang.Objects.BaseObject;

/** Diese Klasse implementiert einen abstrakten {@link Translator2} als {@link BaseObject}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GSource> Typ der Quellobjekte.
 * @param <GTarget> Typ der Zielobjekte. */
public abstract class AbstractTranslator<GSource, GTarget> extends BaseObject implements Translator2<GSource, GTarget> {

	@Override
	public boolean isTarget(final Object object) {
		return false;
	}

	@Override
	public boolean isSource(final Object object) {
		return false;
	}

	@Override
	public GTarget toTarget(final Object object) throws ClassCastException, IllegalArgumentException {
		throw new IllegalArgumentException();
	}

	@Override
	public GSource toSource(final Object object) throws ClassCastException, IllegalArgumentException {
		throw new IllegalArgumentException();
	}

	@Override
	public <GTarget2> Translator2<GSource, GTarget2> concat(final Translator<GTarget, GTarget2> trans) throws NullPointerException {
		return Translators.concat(this, trans);
	}

	@Override
	public Translator2<GTarget, GSource> reverse() {
		return Translators.reverse(this);
	}

	@Override
	public Translator2<GSource, GTarget> synchronize() {
		return Translators.synchronize(this);
	}

	@Override
	public Translator2<GSource, GTarget> synchronize(final Object mutex) {
		return Translators.synchronize(this, mutex);
	}

}
