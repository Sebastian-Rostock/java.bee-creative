package bee.creative.fem;

import bee.creative.emu.EMU;
import bee.creative.emu.Emuable;
import bee.creative.lang.Objects;
import bee.creative.util.Property;

/** Diese Klasse implementiert den benannten Platzhalter einer Funktion, dessen {@link #invoke(FEMFrame)}-Methode an eine {@link #set(FEMFunction) gegebene
 * Funktion} delegiert. Der Platzhalter wird zur erzeugung rekursiver Funktionsaufrufe eingesetz, weshalb {@link #hashCode() Streuwert} und
 * {@link #equals(Object) Äquivalenz} nicht auf diesem, sondern auf einer extra hierfür bereitgestellten {@link #id() Kennung} beruhen.
 *
 * @see FEMParser#proxies()
 * @see FEMToken#proxies()
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMProxy extends FEMFunction implements Property<FEMFunction>, Emuable {

	/** Dieses Feld speichert den leeren Platzhalter mit {@link FEMVoid#INSTANCE undefinierter Kennung}, {@link FEMString#EMPTY leerem Namen} und
	 * {@link FEMVoid#INSTANCE undefinierter Funktion.}. */
	public static final FEMProxy EMPTY = new FEMProxy(FEMVoid.INSTANCE, FEMString.EMPTY, FEMVoid.INSTANCE);

	/** Diese Methode ist eine Abkürzung für {@link #from(FEMString) FEMProxy.from(FEMString.from(id))}. */
	public static FEMProxy from(final String id) throws NullPointerException {
		return FEMProxy.from(FEMString.from(id));
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(FEMValue, FEMString) FEMProxy.from(id, id)}. */
	public static FEMProxy from(final FEMString id) throws NullPointerException {
		return FEMProxy.from(id, id);
	}

	/** Diese Methode gibt einen neuen Platzhalter mit den gegebenen Eigenschaften zurück.
	 *
	 * @param id Kennung.
	 * @param name Name.
	 * @return Platzhalter.
	 * @throws NullPointerException Wenn {@code id} bzw. {@code name} {@code null} ist. */
	public static FEMProxy from(final FEMValue id, final FEMString name) throws NullPointerException {
		return new FEMProxy(id, name, null);
	}

	/** Dieses Feld speichert die Kennung des Platzhalters. */
	final FEMValue id;

	/** Dieses Feld speichert den Namen. */
	final FEMString name;

	/** Dieses Feld speichert die Funktion. */
	FEMFunction target;

	/** Dieser Konstruktor initialisiert den Plathhalter.
	 *
	 * @param id {@link #id() Kennung}.
	 * @param name {@link #name() Name}.
	 * @param target {@link #get() Funktion} oder {@code null}.
	 * @throws NullPointerException Wenn {@code id} bzw. {@code name} {@code null} ist. */
	public FEMProxy(final FEMValue id, final FEMString name, final FEMFunction target) throws NullPointerException {
		this.id = Objects.notNull(id);
		this.name = Objects.notNull(name);
		this.set(target);
	}

	/** Diese Methode gibt die Kennung des Platzhalters zurück. Diese Kennung wird im Konstruktor initialisiert und zur Berechnung von {@link #hashCode()
	 * Streuwert} und {@link #equals(Object) Äquivalenz} eingesetzt.
	 *
	 * @return Kennung. */
	public FEMValue id() {
		return this.id;
	}

	/** Diese Methode gibt den Namen des Platzhalters zurück. Dieser wird in der {@link #toString() Textdarstellung} eingesetzt.
	 *
	 * @return Name. */
	public FEMString name() {
		return this.name;
	}

	/** Diese Methode gibt die Funktion zurück, die in {@link #invoke(FEMFrame)} aufgerufen wird. Diese ist {@code null}, wenn {@link #set(FEMFunction)} noch
	 * nicht aufgerufen wurde.
	 *
	 * @return Funktion oder {@code null}. */
	@Override
	public FEMFunction get() {
		return this.target;
	}

	/** Diese Methode setzt die in {@link #invoke(FEMFrame)} aufzurufende Funktion.
	 *
	 * @param function Funktion oder {@code null}. */
	@Override
	public void set(final FEMFunction function) {
		this.target = function;
	}

	@Override
	public long emu() {
		return EMU.fromObject(this) + EMU.from(this.id) + EMU.from(this.name);
	}

	@Override
	public FEMValue invoke(final FEMFrame frame) {
		return this.target.invoke(frame);
	}

	@Override
	public int hashCode() {
		return this.id.hashCode();
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn die {@link #id() Kennung} dieses Platzhalters gleich der des gegebenen ist. */
	@Override
	public boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof FEMProxy)) return false;
		final FEMProxy object2 = (FEMProxy)object;
		final FEMProxy that = object2;
		return this.id.equals(that.id);
	}

	@Override
	public String toString() {
		return FEMDomain.DEFAULT.printConst(this.name.toString());
	}

}