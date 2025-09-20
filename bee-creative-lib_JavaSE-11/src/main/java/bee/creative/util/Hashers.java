package bee.creative.util;

import bee.creative.lang.Objects;

/** Diese Klasse implementiert grundlegende {@link Hasher}.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Hashers {

	/** Diese Methode liefert den {@link DeepHasher}. */
	public static Hasher2 deepHasher() {
		return DeepHasher.INSTANCE;
	}

	/** Diese Methode liefert den {@link NaturalHasher}. */
	public static Hasher2 naturalHasher() {
		return NaturalHasher.INSTANCE;
	}

	/** Diese Methode liefert den {@link IdentityHasher}. */
	public static Hasher2 identityHasher() {
		return IdentityHasher.INSTANCE;
	}

	/** Diese Methode ist eine Abkürzung für {@link TranslatedHasher new TranslatedHasher(that, trans)}. */
	public static Hasher2 translatedHasher(Hasher that, Getter<? super Object, ?> trans) throws NullPointerException {
		return new TranslatedHasher(that, trans);
	}

	/** Diese Klasse implementiert einen {@link Hasher}, der an {@link Objects#deepHash(Object)} und {@link Objects#deepEquals(Object, Object)} delegiert. */

	public static class DeepHasher extends AbstractHasher {

		public static final Hasher2 INSTANCE = new DeepHasher();

		@Override
		public int hash(Object input) {
			return Objects.deepHash(input);
		}

		@Override
		public boolean equals(Object input1, Object input2) {
			return Objects.deepEquals(input1, input2);
		}

	}

	/** Diese Klasse implementiert einen {@link Hasher}, der an {@link Objects#hash(Object)} und {@link Objects#equals(Object, Object)} delegiert. */

	public static class NaturalHasher extends AbstractHasher {

		public static final Hasher2 INSTANCE = new NaturalHasher();

	}

	/** Diese Klasse implementiert einen {@link Hasher}, der an {@link Objects#identityHash(Object)} und {@link Objects#identityEquals(Object, Object)}
	 * delegiert. */

	public static class IdentityHasher extends AbstractHasher {

		public static final Hasher2 INSTANCE = new IdentityHasher();

		@Override
		public int hash(Object input) {
			return Objects.identityHash(input);
		}

		@Override
		public boolean equals(Object input1, Object input2) {
			return Objects.identityEquals(input1, input2);
		}

	}

	/** Diese Klasse implementiert einen übersetzten {@link Hasher}, der Streuwert und Äquivalenz über einen gegebenen {@link Hasher} zu den über einen gegebenen
	 * {@link Getter} umgewandelten Objekten ermittelt. */

	public static class TranslatedHasher extends AbstractHasher {

		public TranslatedHasher(Hasher that, Getter<? super Object, ?> trans) throws NullPointerException {
			this.that = Objects.notNull(that);
			this.trans = Objects.notNull(trans);
		}

		@Override
		public int hash(Object input) {
			return this.that.hash(this.trans.get(input));
		}

		@Override
		public boolean equals(Object input1, Object input2) {
			return this.that.equals(this.trans.get(input1), this.trans.get(input2));
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that, this.trans);
		}

		private final Hasher that;

		private final Getter<? super Object, ?> trans;

	}

}
