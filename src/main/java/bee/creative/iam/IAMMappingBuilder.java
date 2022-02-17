package bee.creative.iam;

import java.util.List;
import bee.creative.emu.EMU;
import bee.creative.emu.Emuable;
import bee.creative.iam.IAMIndexBuilder.BaseItem;
import bee.creative.iam.IAMIndexBuilder.BasePool;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert ein modifizierbares {@link IAMMapping}.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class IAMMappingBuilder extends IAMMapping implements Emuable {

	static class EntryItem extends BaseItem {

		public IAMArray key;

		public IAMArray value;

		public EntryItem(final int index, final IAMArray key) {
			super(index);
			this.key = key;
			this.value = IAMArray.EMPTY;
		}

		@Override
		public long emu() {
			return EMU.fromObject(this) + EMU.from(this.key) + EMU.from(this.value);
		}

	}

	static class EntryPool extends BasePool<EntryItem> {

		private static final long serialVersionUID = -1058496449455779296L;

		EntryPool(final IAMBuffer buffer) {
			super(buffer);
		}

		@Override
		protected EntryItem customInstallItem(final int index, final IAMArray source) {
			return new EntryItem(index, source);
		}

	}

	/** Dieses Feld speichert den Modus. */
	protected boolean mode = IAMMapping.MODE_HASHED;

	/** Dieses Feld speichert die Einträge. */
	protected final EntryPool entries;

	/** Dieser Konstruktor initialisiert den Zahlenfolgenpuffer mit {@link IAMBuffer#EMPTY}. */
	public IAMMappingBuilder() {
		this(IAMBuffer.EMPTY);
	}

	/** Dieser Konstruktor initialisiert den Zahlenfolgenpuffer mit dem gegebenen. */
	public IAMMappingBuilder(final IAMBuffer buffer) {
		this.entries = new EntryPool(buffer);
	}

	/** Diese Methode fügt einen Eintrag mit dem gegebenen Schlüssel sowie dem gegebenen Wert hinzu. Wenn bereits ein Eintrag mit diesem Schlüssel existiert, wird
	 * dessen Wert ersetzt.
	 *
	 * @param key Schlüssel.
	 * @param value Wert.
	 * @throws NullPointerException Wenn {@code key} bzw. {@code value} {@code null} ist. */
	public void put(final IAMArray key, final IAMArray value) throws NullPointerException {
		Objects.notNull(value);
		this.entries.getItem(key).value = this.entries.buffer.get(value);
	}

	@Override
	public boolean mode() {
		return this.mode;
	}

	/** Diese Methode setzt den Modus.
	 *
	 * @see #MODE_HASHED
	 * @see #MODE_SORTED
	 * @param mode Modus. */
	public void mode(final boolean mode) {
		this.mode = mode;
	}

	@Override
	public long emu() {
		return EMU.fromObject(this) + EMU.from(this.entries);
	}

	/** Diese Methode entfernt alle bisher zusammengestellten Daten. */
	public void clear() {
		this.entries.clear();
	}

	@Override
	public IAMArray key(final int entryIndex) {
		final List<EntryItem> datas = this.entries.items;
		if ((entryIndex < 0) || (entryIndex >= datas.size())) return IAMArray.EMPTY;
		return datas.get(entryIndex).key;
	}

	@Override
	public IAMArray value(final int entryIndex) {
		final List<EntryItem> datas = this.entries.items;
		if ((entryIndex < 0) || (entryIndex >= datas.size())) return IAMArray.EMPTY;
		return datas.get(entryIndex).value;
	}

	@Override
	public int entryCount() {
		return this.entries.items.size();
	}

	@Override
	public int find(final IAMArray key) throws NullPointerException {
		final EntryItem result = this.entries.get(Objects.notNull(key));
		return result != null ? result.index : -1;
	}

}