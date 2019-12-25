package bee.creative.iam;

import java.util.List;
import bee.creative.emu.EMU;
import bee.creative.emu.Emuable;

/** Diese Klasse implementiert ein modifizierbares {@link IAMListing}.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class IAMListingBuilder extends IAMListing implements Emuable {

	static class ItemData extends IAMIndexBuilder.BaseData {

		public IAMArray data;

		public ItemData(final int index, final IAMArray data) {
			this.index = index;
			this.data = data;
		}

		@Override
		public long emu() {
			return EMU.fromObject(this) + EMU.from(this.data);
		}

	}

	static class ItemPool extends IAMIndexBuilder.BasePool<ItemData> {

		ItemPool(final IAMArrayBuilder builder) {
			super(builder);
		}

		@Override
		protected ItemData customData(final int index, final IAMArray source) {
			return new ItemData(index, source);
		}

	}

	/** Dieses Feld speichert die bisher gesammelten Elemente. */
	protected final ItemPool items;

	/** Dieser Konstruktor initialisiert den Zahlenfolgenpuffer mit {@link IAMArrayBuilder#EMPTY}. */
	public IAMListingBuilder() {
		this(IAMArrayBuilder.EMPTY);
	}

	/** Dieser Konstruktor initialisiert den Zahlenfolgenpuffer mit dem gegebenen. */
	public IAMListingBuilder(final IAMArrayBuilder builder) {
		this.items = new ItemPool(builder);
	}

	/** Diese Methode fügt das Element mit den gegebenen Daten hinzu und gibt die Position zurück, unter welcher dieses verwaltet wird. Wenn bereits ein Element
	 * mit den gleichen Daten über diese Methode hinzugefügt wurde, wird dessen Position geliefert.
	 * <p>
	 * <u>Achtung:</u> Die gelieferte Position ist garantiert der gleiche, die im {@link IAMListingLoader} verwerden wird.
	 *
	 * @see #put(int, IAMArray)
	 * @param data Daten des Elements.
	 * @return Position des Elements.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist. */
	public int put(final IAMArray data) throws NullPointerException {
		return this.items.get(data).index;
	}

	/** Diese Methode setzt die Daten des Elements an der gegebenen Position und gibt diese Position zurück. Wenn die Position negativ ist, werden ein neues
	 * Element mit diesen Daten hinzugefügt und die Position geliefert, unter welcher dieses verwaltet wird.
	 * <p>
	 * <u>Achtung:</u> Die gelieferte Position ist garantiert der gleiche, die im {@link IAMListingLoader} verwerden wird.
	 *
	 * @see #put(IAMArray)
	 * @param index Position des anzupassenden Elements oder {@code -1}.
	 * @param data Daten des Elements.
	 * @return Position des Elements.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn {@code index} ungültig ist. */
	public int put(final int index, final IAMArray data) throws NullPointerException, IndexOutOfBoundsException {
		if (index < 0) return this.items.put(data).index;
		this.items.datas.get(index).data = this.items.builder.get(data);
		return index;
	}

	@Override
	public long emu() {
		return EMU.fromObject(this) + EMU.from(this.items);
	}

	/** Diese Methode entfernt alle bisher zusammengestellten Daten. */
	public void clear() {
		this.items.clear();
	}

	@Override
	public IAMArray item(final int itemIndex) {
		final List<ItemData> datas = this.items.datas;
		if ((itemIndex < 0) || (itemIndex >= datas.size())) return IAMArray.EMPTY;
		return datas.get(itemIndex).data;
	}

	@Override
	public int itemCount() {
		return this.items.datas.size();
	}

}