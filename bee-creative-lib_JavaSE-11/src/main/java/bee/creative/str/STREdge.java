package bee.creative.str;

import bee.creative.lang.Objects;

/** Diese Klasse implementiert eine Hyperkante von einer {@link #sourceRef() Quellreferenz} zu einer {@link #targetRef() Zielreferenz}, typisiert mit einer
 * {@link #relationRef() Beziehungsreferenz}.
 *
 * @author [cc-by] 2024 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class STREdge {

	/** Diese Methode liefert eine neue Hyperkante mmit den gegebenen Refernezen, sofern diese ungleich {@code 0} sind.
	 *
	 * @param sourceRef Quellreferenz oder {@code 0}.
	 * @param targetRef Zielreferenz oder {@code 0}.
	 * @param relationRef Beziehungsreferenz oder {@code 0}.
	 * @return Hyperkante oder {@code null}. */
	public static STREdge from(int sourceRef, int targetRef, int relationRef) {
		if ((sourceRef == 0) || (targetRef == 0) || (relationRef == 0)) return null;
		return new STREdge(sourceRef, targetRef, relationRef);
	}

	/** Diese Methode liefert die Quellreferenz. */
	public int sourceRef() {
		return this.sourceRef;
	}

	/** Diese Methode liefert die Zielreferenz. */
	public int targetRef() {
		return this.targetRef;
	}

	/** Diese Methode liefert die Beziehungsreferenz. */
	public int relationRef() {
		return this.relationRef;
	}

	@Override
	public int hashCode() {
		return Objects.hashPush(Objects.hashPush(Objects.hashPush(Objects.hashInit(), this.sourceRef), this.targetRef), this.relationRef);
	}

	@Override
	public boolean equals(Object object) {
		if (object == this) return true;
		if (!(object instanceof STREdge)) return false;
		var that = (STREdge)object;
		return (this.sourceRef == that.sourceRef) && (this.targetRef == that.targetRef) && (this.relationRef == that.relationRef);
	}

	@Override
	public String toString() {
		return "(" + this.sourceRef + ", " + this.targetRef + ", " + this.relationRef + ")";
	}

	final int sourceRef;

	final int targetRef;

	final int relationRef;

	STREdge(int sourceRef, int targetRef, int relationRef) {
		this.sourceRef = sourceRef;
		this.targetRef = targetRef;
		this.relationRef = relationRef;
	}

}
