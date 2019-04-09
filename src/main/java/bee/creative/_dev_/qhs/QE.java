package bee.creative._dev_.qhs;

import bee.creative.lang.Objects;

/** Diese Klasse implementiert den Quad-Edge.
 * 
 * @author [cc-by] 2019 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
final class QE {

	QN S;

	QN P;

	QN O;

	QN C;

	public QE(QN s, QN p, QN o, QN c) {
		S = Objects.notNull(s);
		P = Objects.notNull(p);
		O = Objects.notNull(o);
		C = Objects.notNull(c);
	}

	/** Diese Methode gibt {@link QN Knoten} zurück, der für das Subjekt bzw. den Beginn der Kante steht.
	 *
	 * @return Subjekt. */
	public QN S() {
		return this.S;
	}

	/** Diese Methode gibt {@link QN Knoten} zurück, der für das Prädikat bzw. die Bedeutung der Kante steht.
	 *
	 * @return Prädikat. */
	public QN P() {
		return this.P;
	}

	/** Diese Methode gibt {@link QN Knoten} zurück, der für das Objekt bzw. das Ziel der Kante steht.
	 *
	 * @return Objekt. */
	public QN O() {
		return this.O;
	}

	/** Diese Methode gibt {@link QN Knoten} zurück, der für das den Kontext bzw. das Wissen steht, in welchem die Kante bekannt ist.
	 *
	 * @return Objekt. */
	public QN C() {
		return this.C;
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		int result = Objects.hashInit();
		result = Objects.hashPush(result, this.S.hashCode());
		result = Objects.hashPush(result, this.P.hashCode());
		result = Objects.hashPush(result, this.O.hashCode());
		result = Objects.hashPush(result, this.C.hashCode());
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof QE)) return false;
		final QE that = (QE)object;
		return this.equalsImpl(that);
	}

	@SuppressWarnings ("javadoc")
	boolean equalsImpl(final QE that) {
		if (!this.S.equalsImpl(that.S)) return false;
		if (!this.P.equalsImpl(that.P)) return false;
		if (!this.O.equalsImpl(that.O)) return false;
		if (!this.C.equalsImpl(that.C)) return false;
		return true;
	}

	@Override
	public String toString() {
		return "<" + this.C + ":" + this.S + " " + this.P + " " + this.O + ">";
	}

}
