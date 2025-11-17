package bee.creative.qs.h2.fem;

import java.sql.ResultSet;
import java.sql.SQLException;
import bee.creative.fem.FEMDatetime;
import bee.creative.qs.h2.H2QIRangeBag;
import bee.creative.qs.h2.H2QQ;
import bee.creative.qs.h2.H2QS;

public class FEMDatetimeBag extends H2QIRangeBag<FEMDatetime, FEMDatetimeBag> {

	public FEMDatetimeBag(H2QS owner) {
		this(owner, new H2QQ().push("SELECT * FROM QD_FEMDATETIME"), "QI_FEMDATETIME");
	}

	@Override
	protected FEMDatetime customItem(ResultSet next) throws SQLException {
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
	protected void customInsert(InsertSet putItemSet) throws SQLException {
		try (var stmt = new H2QQ()
			.push("MERGE INTO QD_FEMDATETIME (N, DATETIMEVALUE, DATEMINIMUM, DATEMAXIMUM, TIMEMINIMUM, TIMEMAXIMUM) VALUES (?, ?, ?, ?, ?, ?)").prepare(this.owner)) {
			for (var entry: putItemSet) {
				try {
					var item = FEMDatetime.from(entry.getValue());
					if (item.hasDate() || item.hasTime()) {
						stmt.setObject(1, entry.getKey());
						stmt.setLong(2, item.value());
						stmt.setObject(3, this.dateminimum(item));
						stmt.setObject(4, this.datemaximum(item));
						stmt.setObject(5, this.timeminimum(item));
						stmt.setObject(6, this.timemaximum(item));
						stmt.addBatch();
					}
				} catch (Exception ignore) {}
			}
			stmt.executeBatch();
		}
	}

	@Override
	protected void customDelete(DeleteSet popItemSet) throws SQLException {
		new H2QQ().push("DELETE FROM QD_FEMDATETIME WHERE N IN (").push(popItemSet).push(")").update(this.owner);
	}

	@Override
	protected FEMDatetimeBag customHaving(H2QQ table) throws NullPointerException, IllegalArgumentException {
		return new FEMDatetimeBag(this.owner, table, null);
	}

	@Override
	protected void customHavingItemEQ(H2QQ table, FEMDatetime item) throws NullPointerException, IllegalArgumentException {
		if (item.hasDate()) {
			table.push("DATEMINIMUM=").push(this.dateminimum(item)).push("AND DATEMAXIMUM=").push(this.datemaximum(item));
		} else if (item.hasTime()) {
			table.push("TIMEMINIMUM=").push(this.timeminimum(item)).push("AND TIMEMAXIMUM=").push(this.timemaximum(item));
		} else throw new IllegalArgumentException();
	}

	@Override
	protected void customHavingItemLT(H2QQ table, FEMDatetime item) throws NullPointerException, IllegalArgumentException {
		if (item.hasDate()) {
			table.push("DATEMAXIMUM<").push(this.dateminimum(item));
		} else if (item.hasTime()) {
			table.push("TIMEMAXIMUM<").push(this.timeminimum(item));
		} else throw new IllegalArgumentException();
	}

	@Override
	protected void customHavingItemGT(H2QQ table, FEMDatetime item) throws NullPointerException, IllegalArgumentException {
		if (item.hasDate()) {
			table.push("DATEMINIMUM>").push(this.datemaximum(item));
		} else if (item.hasTime()) {
			table.push("TIMEMINIMUM>").push(this.timemaximum(item));
		} else throw new IllegalArgumentException();
	}

	private FEMDatetimeBag(H2QS owner, H2QQ table, String cache) {
		super(owner, table, cache);
	}

	private Long dateminimum(FEMDatetime item) {
		return item.hasDate() ? this.datemillis(item) + this.timemillis(item, 0) + this.zonemillis(item, -50400000) : null;
	}

	private Long datemaximum(FEMDatetime item) {
		return item.hasDate() ? this.datemillis(item) + this.timemillis(item, 86400000) + this.zonemillis(item, +50400000) : null;
	}

	private Integer timeminimum(FEMDatetime item) {
		return item.hasTime() ? item.daymillisValue() + this.zonemillis(item, -50400000) : null;
	}

	private Integer timemaximum(FEMDatetime item) {
		return item.hasTime() ? item.daymillisValue() + this.zonemillis(item, +50400000) : null;
	}

	private long datemillis(FEMDatetime item) {
		return item.calendardayValue() * 86400000L;
	}

	private int timemillis(FEMDatetime item, int undefined) {
		return item.hasTime() ? item.daymillisValue() : undefined;
	}

	private int zonemillis(FEMDatetime item, int undefined) {
		return item.hasZone() ? -item.zoneValue() : undefined;
	}

}
