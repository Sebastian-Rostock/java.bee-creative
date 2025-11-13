package bee.creative.kb;

import java.util.function.Consumer;
import bee.creative.emu.EMU;
import bee.creative.emu.Emuable;
import bee.creative.fem.FEMString;
import bee.creative.lang.Objects;
import bee.creative.lang.Objects.UseToString;
import bee.creative.util.Iterable3;
import bee.creative.util.Iterator3;

/** Diese Klasse implementiert das {@link Iterable3} der Textwerte eines {@link KBState Wissensstands}.
 *
 * @author [cc-by] 2024 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class KBValues implements Iterable3<KBValue>, Emuable, UseToString {

	/** Diese Methode liefert den {@link KBState Wissensstand}, dessen {@link FEMString Texterte} iteriert werden. */
	public KBState owner() {
		return this.owner;
	}

	/** Diese Methode liefert einen {@link KBValues Textwertauswahl}, die nur {@link FEMString Textwerte} mit den Referenzen liefert, die in den gegebenen
	 * Referenzen {@code selectValueRefs} enthalten sind. */
	public KBValues selectValueRefs(int... selectValueRefs) {
		return KBState.computeSelect(selectValueRefs, this.acceptValueRefset, this.refuseValueRefset,
			acceptValueRefset -> new KBValues(this.owner, acceptValueRefset, null));
	}

	/** Diese Methode liefert einen {@link KBValues Textwertauswahl}, die nur {@link FEMString Textwerte} mit den Referenzen liefert, die nicht in den gegebenen
	 * Referenzen {@code exceptValueRefs} enthalten sind. */
	public KBValues exceptValueRefs(int... exceptValueRefs) {
		return KBState.computeExcept(exceptValueRefs, this.acceptValueRefset, this.refuseValueRefset,
			acceptValueRefset -> new KBValues(this.owner, acceptValueRefset, null), refuseValueRefset -> new KBValues(this.owner, null, refuseValueRefset));
	}

	/** Diese Methode übergibt die Textwerte und deren Referenzen an {@link KBValuesTask#run(int, FEMString) task.run()}. */
	public void forEach(KBValuesTask task) {
		this.owner.forEachValue(this.acceptValueRefset, this.refuseValueRefset, task);
	}

	@Override
	public void forEach(Consumer<? super KBValue> action) {
		this.forEach((valueRef, valueStr) -> action.accept(new KBValue(valueRef, valueStr)));
	}

	@Override
	public Iterator3<KBValue> iterator() {
		return this.owner.valueIterator(this.acceptValueRefset, this.refuseValueRefset);
	}

	@Override
	public long emu() {
		return EMU.fromObject(this) + REFSET.emu(this.acceptValueRefset) + REFSET.emu(this.refuseValueRefset);
	}

	@Override
	public String toString() {
		return Objects.printIterable(true, 20, this);
	}

	KBValues(KBState owner) {
		this(owner, null, null);
	}

	private final KBState owner;

	private final int[] acceptValueRefset;

	private final int[] refuseValueRefset;

	private KBValues(KBState owner, int[] acceptValueRefset, int[] refuseValueRefset) {
		this.owner = owner;
		this.acceptValueRefset = acceptValueRefset;
		this.refuseValueRefset = refuseValueRefset;
	}

}