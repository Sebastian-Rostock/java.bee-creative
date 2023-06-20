package bee.creative.qs.h2.fem;

import java.sql.ResultSet;
import java.sql.SQLException;
import bee.creative.fem.FEMDatetime;
import bee.creative.qs.h2.H2QDBag;
import bee.creative.qs.h2.H2QQ;
import bee.creative.qs.h2.H2QS;

public class FEMDatetimeBag extends H2QDBag<FEMDatetime, FEMDatetimeBag> {

	public FEMDatetimeBag(final H2QS owner) {
		this(owner, new H2QQ().push("SELECT * FROM QD_FEMDATETIME"), "QI_FEMDATETIME");
	}

	@Override
	protected FEMDatetime customItem(final ResultSet next) throws SQLException {
		return new FEMDatetime(next.getLong(2));
	}

	@Override
	protected void customSetup() {
		new H2QQ().push("CREATE TABLE IF NOT EXISTS QD_FEMDATETIME (N BIGINT NOT NULL, DATETIMEVALUE BIGINT NOT NULL, " + //
			"DATEMINIMUM BIGINT, DATEMAXIMUM BIGINT, TIMEMINIMUM INT, TIMEMAXIMUM INT, PRIMARY KEY (N));" + //
			"CREATE INDEX IF NOT EXISTS QD_FEMDATETIME_INDEX_1 ON QD_FEMINTEGER (DATEMINIMUM, DATEMAXIMUM);" + //
			"CREATE INDEX IF NOT EXISTS QD_FEMDATETIME_INDEX_2 ON QD_FEMINTEGER (DATEMAXIMUM);" + //
			"CREATE INDEX IF NOT EXISTS QD_FEMDATETIME_INDEX_3 ON QD_FEMINTEGER (TIMEMINIMUM, TIMEMAXIMUM);" + //
			"CREATE INDEX IF NOT EXISTS QD_FEMDATETIME_INDEX_4 ON QD_FEMINTEGER (TIMEMAXIMUM);").update(this.owner);
	}

	@Override
	protected void customInsert(final InsertSet putItemSet) throws SQLException {
		try (final var stmt = new H2QQ()
			.push("MERGE INTO QD_FEMDATETIME (N, DATETIMEVALUE, DATEMINIMUM, DATEMAXIMUM, TIMEMINIMUM, TIMEMAXIMUM) VALUES (?, ?, ?, ?, ?, ?)").prepare(this.owner)) {
			for (final var entry: putItemSet) {
				try {
					final var item = FEMDatetime.from(entry.getValue());
					if (item.hasDate() || item.hasTime()) {
						stmt.setObject(1, entry.getKey());
						stmt.setLong(2, item.value());
						stmt.setObject(3, this.dateminimum(item));
						stmt.setObject(4, this.datemaximum(item));
						stmt.setObject(5, this.timeminimum(item));
						stmt.setObject(6, this.timemaximum(item));
						stmt.addBatch();
					}
				} catch (final Exception ignore) {}
			}
			stmt.executeBatch();
		}
	}

	@Override
	protected void customDelete(final DeleteSet popItemSet) throws SQLException {
		new H2QQ().push("DELETE FROM QD_FEMDATETIME WHERE N IN (").push(popItemSet).push(")").update(this.owner);
	}

	@Override
	protected FEMDatetimeBag customHaving(final H2QQ table) throws NullPointerException, IllegalArgumentException {
		return new FEMDatetimeBag(this.owner, table, null);
	}

	@Override
	protected void customHavingItemEQ(final H2QQ table, final FEMDatetime item) throws NullPointerException, IllegalArgumentException {
		if (item.hasDate()) {
			table.push("DATEMINIMUM=").push(this.dateminimum(item)).push("AND DATEMAXIMUM=").push(this.datemaximum(item));
		} else if (item.hasTime()) {
			table.push("TIMEMINIMUM=").push(this.timeminimum(item)).push("AND TIMEMAXIMUM=").push(this.timemaximum(item));
		} else throw new IllegalArgumentException();
	}

	@Override
	protected void customHavingItemLT(final H2QQ table, final FEMDatetime item) throws NullPointerException, IllegalArgumentException {
		if (item.hasDate()) {
			table.push("DATEMAXIMUM<").push(this.dateminimum(item));
		} else if (item.hasTime()) {
			table.push("TIMEMAXIMUM<").push(this.timeminimum(item));
		} else throw new IllegalArgumentException();
	}

	@Override
	protected void customHavingItemGT(final H2QQ table, final FEMDatetime item) throws NullPointerException, IllegalArgumentException {
		if (item.hasDate()) {
			table.push("DATEMINIMUM>").push(this.datemaximum(item));
		} else if (item.hasTime()) {
			table.push("TIMEMINIMUM>").push(this.timemaximum(item));
		} else throw new IllegalArgumentException();
	}

	private FEMDatetimeBag(final H2QS owner, final H2QQ table, final String cache) {
		super(owner, table, cache);
	}

	private Long dateminimum(final FEMDatetime item) {
		return item.hasDate() ? this.datemillis(item) + this.timemillis(item, 0) + this.zonemillis(item, -50400000) : null;
	}

	private Long datemaximum(final FEMDatetime item) {
		return item.hasDate() ? this.datemillis(item) + this.timemillis(item, 86400000) + this.zonemillis(item, +50400000) : null;
	}

	private Integer timeminimum(final FEMDatetime item) {
		return item.hasTime() ? item.daymillisValue() + this.zonemillis(item, -50400000) : null;
	}

	private Integer timemaximum(final FEMDatetime item) {
		return item.hasTime() ? item.daymillisValue() + this.zonemillis(item, +50400000) : null;
	}

	private long datemillis(final FEMDatetime item) {
		return item.calendardayValue() * 86400000L;
	}

	private int timemillis(final FEMDatetime item, final int undefined) {
		return item.hasTime() ? item.daymillisValue() : undefined;
	}

	private int zonemillis(final FEMDatetime item, final int undefined) {
		return item.hasZone() ? -item.zoneValue() : undefined;
	}

}
