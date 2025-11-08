package bee.creative.qs.h2;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map.Entry;
import bee.creative.lang.Objects;
import bee.creative.qs.QIBag;
import bee.creative.qs.QN;
import bee.creative.qs.QNSet;
import bee.creative.qs.QS;
import bee.creative.util.Entries;
import bee.creative.util.Setter;

public abstract class H2QIBag<GI, GIBag> extends H2QISet<GI> implements QIBag<GI, GIBag> {

	@Override
	public void items(Setter<? super QN, ? super GI> items) {
		try (var rset = this.table.select(this.owner)) {
			while (rset.next()) {
				items.set(this.owner.newNode(rset.getLong(1)), this.customItem(rset));
			}
		} catch (SQLException cause) {
			throw new IllegalStateException(cause);
		}
	}

	@Override
	public H2QNSet nodes() {
		return new H2QNSet(this.owner, new H2QQ().push("SELECT N FROM (").push(this).push(")"));
	}

	@Override
	public H2QVSet values() {
		return this.nodes().values();
	}

	@Override
	public GIBag havingNodes(QNSet nodes) throws NullPointerException, IllegalArgumentException {
		var that = this.owner.asQNSet(nodes);
		return this.customHaving(new H2QQ().push("SELECT * FROM (").push(this).push(") WHERE N IN (SELECT N FROM (").push(that).push("))"));
	}

	/** Dieser Konstruktor initialisiert {@link #owner Graphspeicher} und {@link #table Tabelle}. Die Tabelle muss mit der Spalte
	 * {@code N BIGINT NOT NULL PRIMARY KEY} beginnen. Der {@code index} gibt den Namen der automatisch erzeugten Tabelle zur Erfassung der indizierten
	 * {@link QS#nodes() Hperknoten mit Textwert} an. */
	protected H2QIBag(H2QS owner, H2QQ table, String index_or_null) {
		super(owner, Objects.notNull(table));
		if (index_or_null == null) return;
		synchronized (owner.cacheMap) {
			this.table.push(owner.cacheMap.install(index_or_null, index -> {
				new H2QQ().push("CREATE TABLE IF NOT EXISTS ").push(index).push(" (N BIGINT NOT NULL, PRIMARY KEY (N));").update(owner);
				this.customSetup();
				return new Cache(index);
			}));
		}
	}

	protected abstract void customSetup();

	protected abstract void customInsert(InsertSet insertSet) throws SQLException;

	protected abstract void customDelete(DeleteSet deleteSet) throws SQLException;

	protected abstract GIBag customHaving(H2QQ table) throws NullPointerException, IllegalArgumentException;

	protected final class Cache {

		@Override
		public String toString() {
			var that = H2QIBag.this;
			var owner = that.owner;
			while (true) {
				var putValueMark = owner.putValueMark;
				var popValueMark = owner.popValueMark;
				var putValueChanged = this.putValueMark != putValueMark;
				var popValueChanged = this.popValueMark != popValueMark;
				if (!putValueChanged && !popValueChanged) return "";
				this.putValueMark = putValueMark;
				this.popValueMark = popValueMark;
				try {
					if (popValueChanged) {
						var popItemSet = new DeleteSet(owner, this.index);
						new H2QQ().push("DELETE FROM ").push(this.index).push(" WHERE N IN (").push(popItemSet).push(")").update(owner);
						that.customDelete(popItemSet);
					}
					if (putValueChanged) {
						var putItemSet = new InsertSet(owner, this.index);
						new H2QQ().push("MERGE INTO ").push(this.index).push(" SELECT N FROM (").push(putItemSet).push(")").update(owner);
						that.customInsert(putItemSet);
					}
				} catch (NullPointerException | IllegalStateException | IllegalArgumentException cause) {
					throw cause;
				} catch (Exception cause) {
					throw new IllegalStateException(cause);
				}
			}
		}

		final String index;

		Object putValueMark;

		Object popValueMark;

		Cache(String index) {
			this.index = index;
		}

	}

	/** Diese Klasse implementiert eine temporäre Kopie der Menge der noch nicht indizierten {@link H2QS#nodes() Hyperknoten mit Textwert} für
	 * {@link H2QIBag#customInsert(InsertSet)}. Die {@link Entry#getKey() Schüssel} jedes Eintrags nennt die {@link H2QN#key Kennung} des {@link H2QE
	 * Hyperknoten}, dessen {@link H2QN#value() Textwert} im {@link Entry#getValue() Werte} des Eintrags angegeben ist. */
	protected static final class InsertSet extends H2QISet<Entry<Long, String>> {

		public void collectAll(Setter<Long, String> setter) throws SQLException {
			try (var rset = this.table.select(this.owner)) {
				while (rset.next()) {
					setter.set((Long)rset.getObject(1), rset.getString(2));
				}
			}
		}

		@Override
		protected Entry<Long, String> customItem(ResultSet next) throws SQLException {
			return Entries.entryWith((Long)next.getObject(1), next.getString(2));
		}

		InsertSet(H2QS owner, String index) {
			super(owner, null);
			new H2QQ().push("CREATE TEMPORARY TABLE ").push(this.table)
				.push(" (N BIGINT NOT NULL, V VARCHAR(1G) NOT NULL, PRIMARY KEY (N)) AS (SELECT N, V FROM QN where N NOT IN (SELECT N FROM ").push(index).push("))")
				.update(owner);
		}

	}

	/** Diese Klasse implementiert eine temporäre Kopie der Menge der indizierten {@link H2QS#nodes() Hyperknoten mit Textwert} für
	 * {@link H2QIBag#customDelete(DeleteSet)}. Jeder Eintrag nennt die {@link H2QN#key Kennung} eines gelöschten {@link H2QE Hyperknoten}. */
	protected static final class DeleteSet extends H2QISet<Long> {

		@Override
		protected Long customItem(ResultSet next) throws SQLException {
			return next.getLong(1);
		}

		DeleteSet(H2QS owner, String index) {
			super(owner, null);
			new H2QQ().push("CREATE TEMPORARY TABLE ").push(this.table).push(" (N BIGINT NOT NULL, PRIMARY KEY (N)) AS (SELECT N FROM ").push(index)
				.push(" WHERE N NOT IN (SELECT N FROM QN))").update(owner);
		}

	}

}
