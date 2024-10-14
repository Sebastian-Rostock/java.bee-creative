package bee.creative.kb;

import java.util.Map.Entry;
import java.util.function.Consumer;
import bee.creative.fem.FEMString;
import bee.creative.lang.Objects;
import bee.creative.util.Entries;
import bee.creative.util.Iterable2;
import bee.creative.util.Iterator2;

/** Diese Klasse implementiert das {@link Iterable2} der Textwerte eines {@link KBState}.
 *
 * @author [cc-by] 2024 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class KBValues implements Iterable2<Entry<Integer, FEMString>> {

	/** Diese Methode liefert den {@link KBState}, dessen {@link FEMString Texterte} iteriert werden. */
	public KBState owner() {
		return this.owner;
	}

	@Override
	public void forEach(Consumer<? super Entry<Integer, FEMString>> action) {
		this.forEach((valueRef, valueStr) -> action.accept(Entries.from(valueRef, valueStr)));
	}

	/** Diese Methode übergibt die Rextwerte und deren Referenzen an {@link RUN#run(int, FEMString) task.run()}. */
	public void forEach(RUN task) {
		this.owner.forEachValue(this.acceptValueRefset, this.refuseValueRefset, task);
	}

	@Override
	public Iterator2<Entry<Integer, FEMString>> iterator() {
		return this.owner.valueIterator(this.acceptValueRefset, this.refuseValueRefset);
	}

	@Override
	public String toString() {
		return Objects.printIterable(false, this);
	}

	public KBValues selectValueRefs(int... selectValueRefs) {
		return KBBuffer.computeSelect(this.acceptValueRefset, this.refuseValueRefset, selectValueRefs,
			acceptValueRefset -> new KBValues(this.owner, acceptValueRefset, null));
	}

	public KBValues exceptValueRefs(int... exceptValueRefs) {
		return KBBuffer.computeExcept(this.acceptValueRefset, this.refuseValueRefset, exceptValueRefs,
			acceptValueRefset -> new KBValues(this.owner, acceptValueRefset, null), refuseValueRefset -> new KBValues(this.owner, null, refuseValueRefset));
	}

	/** Diese Schnittstelle definiert den Empfänger für {@link KBValues#forEach(RUN)}. */
	public interface RUN {

		/** Diese Methode verarbeitet den gegebenen Textwert {@code valueStr} mit der gegebenen Textreferenz {@code valueRef}. */
		void run(int valueRef, FEMString valueStr);

	}

	KBValues(KBState owner) {
		this(owner, null, null);
	}

	KBValues(KBState owner, int[] acceptValueRefset, int[] refuseValueRefset) {
		this.owner = owner;
		this.acceptValueRefset = acceptValueRefset;
		this.refuseValueRefset = refuseValueRefset;
	}

	private final KBState owner;

	private final int[] acceptValueRefset;

	private final int[] refuseValueRefset;

}