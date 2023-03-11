package bee.creative.util;

/** Diese Schnittstelle ergänzt einen {@link Hasher} insb. um eine Anbindung an Methoden von {@link Hashers}.
 *
 * @author [cc-by] 2022 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface Hasher2 extends Hasher {

	/** Diese Methode ist eine Abkürzung für {@link Hashers#translate(Hasher, Getter) Hashers.translate(this, trans)}. */
	public Hasher2 translate(Getter<? super Object, ?> trans) throws NullPointerException;

}
