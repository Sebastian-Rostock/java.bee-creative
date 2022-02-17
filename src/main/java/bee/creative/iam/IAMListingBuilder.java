package bee.creative.iam;

import java.util.List;
import bee.creative.emu.EMU;
import bee.creative.emu.Emuable;
import bee.creative.iam.IAMIndexBuilder.BaseItem;
import bee.creative.iam.IAMIndexBuilder.BasePool;

/** Diese Klasse implementiert ein modifizierbares {@link IAMListing}.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class IAMListingBuilder extends IAMListing implements Emuable {

	static class ArrayItem extends BaseItem {

		public IAMArray data;

		public ArrayItem(final int index, final IAMArray data) {
			super(index);
			this.data = data;
		}

		@Override
		public long emu() {
			return EMU.fromObject(this) + EMU.from(this.data);
		}

	}

	static class ArrayPool extends BasePool<ArrayItem> {

		private static final long serialVersionUID = -3449526649674452016L;

		ArrayPool(final IAMBuffer buffer) {
			super(buffer);
		}

		@Override
		protected ArrayItem customInstallItem(int index, IAMArray source) {
			return new ArrayItem(index, source);
		}

	}

	/** Dieses Feld speichert die bisher gesammelten Elemente. */
	protected final ArrayPool arrays;

	/** Dieser Konstruktor initialisiert den Zahlenfolgenpuffer mit {@link IAMBuffer#EMPTY}. */
	public IAMListingBuilder() {
		this(IAMBuffer.EMPTY);
	}

	/** Dieser Konstruktor initialisiert den Zahlenfolgenpuffer mit dem gegebenen. */
	public IAMListingBuilder(final IAMBuffer buffer) {
		this.arrays = new ArrayPool(buffer);
	}

	/** Diese Methode fügt das Element mit den gegebenen Daten hinzu und gibt die Position zurück, unter welcher dieses verwaltet wird. Wenn bereits ein Element
	 * mit den gleichen Daten über diese Methode hinzugefügt wurde, wird dessen Position geliefert.
	 * <p>
	 * <u>Achtung:</u> Die gelieferte Position ist garantiert der gleiche, die im {@link IAMListingLoader} verwerden wird.
	 *
	 * @see #put(int, IAMArray)
	 * @param item Daten des Elements.
	 * @return Position des Elements.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist. */
	public int put(final IAMArray item) throws NullPointerException {
		return this.arrays.getItem(item).index;
	}

	/** Diese Methode setzt die Daten des Elements an der gegebenen Position und gibt diese Position zurück. Wenn die Position negativ ist, werden ein neues
	 * Element mit diesen Daten hinzugefügt und die Position geliefert, unter welcher dieses verwaltet wird.
	 * <p>
	 * <u>Achtung:</u> Die gelieferte Position ist garantiert der gleiche, die im {@link IAMListingLoader} verwerden wird.
	 *
	 * @see #put(IAMArray)
	 * @param index Position des anzupassenden Elements oder {@code -1}.
	 * @param item Daten des Elements.
	 * @return Position des Elements.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn {@code index} ungültig ist. */
	public int put(final int index, final IAMArray item) throws NullPointerException, IndexOutOfBoundsException {
		if (index < 0) return this.arrays.putItem(item).index;
		this.arrays.items.get(index).data = this.arrays.buffer.get(item);
		return index;
	}

	@Override
	public long emu() {
		return EMU.fromObject(this) + EMU.from(this.arrays);
	}

	/** Diese Methode entfernt alle bisher zusammengestellten Daten. */
	public void clear() {
		this.arrays.clear();
	}

	@Override
	public IAMArray item(final int itemIndex) {
		final List<ArrayItem> datas = this.arrays.items;
		if ((itemIndex < 0) || (itemIndex >= datas.size())) return IAMArray.EMPTY;
		return datas.get(itemIndex).data;
	}

	@Override
	public int itemCount() {
		return this.arrays.items.size();
	}

}