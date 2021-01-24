package bee.creative.bind;

import bee.creative.lang.Objects.BaseObject;

/** Diese Klasse implementiert einen abstrakten {@link Translator2} als {@link BaseObject}.
 * 
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GSource> Typ der Quellobjekte.
 * @param <GTarget> Typ der Zielobjekte. */
public abstract class AbstractTranslator<GSource, GTarget> extends BaseObject implements Translator2<GSource, GTarget> {

	@Override
	public boolean isTarget(Object object) {
		return false;
	}

	@Override
	public boolean isSource(Object object) {
		return false;
	}

	@Override
	public GTarget toTarget(Object object) throws ClassCastException, IllegalArgumentException {
		throw new IllegalArgumentException();
	}

	@Override
	public GSource toSource(Object object) throws ClassCastException, IllegalArgumentException {
		throw new IllegalArgumentException();
	}

	@Override
	public <GTarget2> Translator2<GSource, GTarget2> concat(Translator<GTarget, GTarget2> trans) throws NullPointerException {
		return Translators.concat(this, trans);
	}

	@Override
	public Translator2<GTarget, GSource> toReverse() {
		return Translators.toReverse(this);
	}

	@Override
	public Translator2<GSource, GTarget> toSynchronized() {
		return Translators.toSynchronized(this);
	}

	@Override
	public Translator2<GSource, GTarget> toSynchronized(Object mutex) {
		return Translators.toSynchronized(this, mutex);
	}

}
