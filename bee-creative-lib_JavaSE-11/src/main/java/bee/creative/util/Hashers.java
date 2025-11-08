package bee.creative.util;

import static bee.creative.lang.Objects.notNull;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert grundlegende {@link Hasher3}.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Hashers {

	/** Diese Methode liefert das gegebene {@link Hasher} als {@link Hasher3}. */
	public static Hasher3 hasherFrom(Hasher that) throws NullPointerException {
		notNull(that);
		if (that instanceof Hasher3) return (Hasher3)that;
		return hasherFrom(that, that);
	}

	/** Diese Methode liefert einen {@link Hasher3} mit den gegebenen Methoden. */
	public static Hasher3 hasherFrom(HasherHash hash, HasherEquals equals) throws NullPointerException {
		notNull(hash);
		notNull(equals);
		return new Hasher3() {

			@Override
			public int hash(Object input) {
				return hash.hash(input);
			}

			@Override
			public boolean equals(Object input1, Object input2) {
				return equals.equals(input1, input2);
			}

		};
	}

	/** Diese Methode liefert einen {@link Hasher3}, der an {@link Objects#deepHash(Object)} und {@link Objects#deepEquals(Object, Object)} delegiert. */
	public static Hasher3 deepHasher() {
		return deepHasher;
	}

	/** Diese Methode liefert einen {@link Hasher3}, der an {@link Objects#hash(Object)} und {@link Objects#equals(Object, Object)} delegiert. */
	public static Hasher3 naturalHasher() {
		return naturalHasher;
	}

	/** Diese Methode liefert einen {@link Hasher3}, der an {@link Objects#identityHash(Object)} und {@link Objects#identityEquals(Object, Object)} delegiert. */
	public static Hasher3 identityHasher() {
		return identityHasher;
	}

	/** Diese Methode liefert einen übersetzten {@link Hasher3}, der Streuwert und Äquivalenz über einen gegebenen {@link Hasher} zu den über einen gegebenen
	 * {@link Getter} umgewandelten Objekten ermittelt. */
	public static Hasher3 translatedHasher(Hasher that, Getter<? super Object, ?> trans) throws NullPointerException {
		notNull(that);
		notNull(trans);
		return hasherFrom(input -> that.hash(trans.get(input)), (input1, input2) -> that.equals(trans.get(input1), trans.get(input2)));
	}

	private static final Hasher3 deepHasher = new Hasher3() {

		@Override
		public int hash(Object input) {
			return Objects.deepHash(input);
		}

		@Override
		public boolean equals(Object input1, Object input2) {
			return Objects.deepEquals(input1, input2);
		}

	};

	private static final Hasher3 naturalHasher = new Hasher3() {

		@Override
		public int hash(Object input) {
			return Objects.hash(input);
		}

		@Override
		public boolean equals(Object input1, Object input2) {
			return Objects.equals(input1, input2);
		}

	};

	private static final Hasher3 identityHasher = new Hasher3() {

		@Override
		public int hash(Object input) {
			return Objects.identityHash(input);
		}

		@Override
		public boolean equals(Object input1, Object input2) {
			return Objects.identityEquals(input1, input2);
		}

	};

}
