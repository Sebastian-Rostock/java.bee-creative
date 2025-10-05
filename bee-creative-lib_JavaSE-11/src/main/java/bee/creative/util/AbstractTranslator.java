package bee.creative.util;

import bee.creative.lang.Objects.BaseObject;

/** Diese Klasse implementiert einen abstrakten {@link Translator3} als {@link BaseObject}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GSource> Typ der Quellobjekte.
 * @param <GTarget> Typ der Zielobjekte. */
public abstract class AbstractTranslator<GSource, GTarget> extends BaseObject implements Translator3<GSource, GTarget> {

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

}
