package bee.creative.qs.h2.fem;

import java.sql.ResultSet;
import java.sql.SQLException;
import bee.creative.fem.FEMDuration;
import bee.creative.fem.FEMInteger;
import bee.creative.qs.h2.H2QDBag;
import bee.creative.qs.h2.H2QQ;
import bee.creative.qs.h2.H2QS;

public class FEMDurationBag extends H2QDBag<FEMDuration, FEMDurationBag> {

	public FEMDurationBag(final H2QS owner) {
		this(owner, new H2QQ().push("SELECT * FROM QD_FEMDURATION"), "QI_FEMDURATION");
	}

	@Override
	protected FEMDuration customItem(final ResultSet next) throws SQLException {
		return FEMDuration.from(next.getInt(2), next.getLong(3));
	}

	@Override
	protected void customSetup() {
		new H2QQ().push("CREATE TABLE IF NOT EXISTS QD_FEMDURATION (N BIGINT NOT NULL, DURATIONMONTHS INT NOT NULL, DURATIONMILLIS BIGINT NOT NULL, " + //
			"DURATIONMILLISMIN BIGINT NOT NULL, DURATIONMILLISMAX BIGINT NOT NULL, PRIMARY KEY (N));" + //
			"CREATE INDEX IF NOT EXISTS QD_FEMDURATION_INDEX_1 ON QD_FEMINTEGER (DURATIONMILLISMIN, DURATIONMILLISMAX);").update(this.owner);
	}

	@Override
	protected void customInsert(final InsertSet putItemSet) throws SQLException {
		try (final var stmt = new H2QQ().push("MERGE INTO QD_FEMDURATION (N, DURATIONMONTHS, DURATIONMILLIS, DURATIONMILLISMIN, DURATIONMILLISMAX) " + //
			"VALUES (?, ?, ?, ?, ?)").prepare(this.owner)) {
			for (final var entry: putItemSet) {
				try {
					final var item = FEMDuration.from(entry.getValue());
					stmt.setObject(1, entry.getKey());
					stmt.setInt(2, item.durationmonthsValue());
					stmt.setLong(3, item.durationmillisValue());
					stmt.setLong(4, item.durationmillisMinValue());
					stmt.setLong(5, item.durationmillisMaxValue());
					stmt.addBatch();
				} catch (final Exception ignore) {}
			}
			stmt.executeBatch();
		}
	}

	@Override
	protected void customDelete(final DeleteSet popItemSet) throws SQLException {
		new H2QQ().push("DELETE FROM QD_FEMDURATION WHERE N IN (").push(popItemSet).push(")").update(this.owner);
	}

	@Override
	protected FEMDurationBag customHaving(final H2QQ table) throws NullPointerException, IllegalArgumentException {
		return new FEMDurationBag(this.owner, table, null);
	}

	@Override
	protected void customHavingItemEQ(final H2QQ table, final FEMDuration item) throws NullPointerException, IllegalArgumentException {
		table.push("DURATIONMILLISMIN=").push(item.durationmillisMinValue()).push("AND DURATIONMILLISMAX=").push(item.durationmillisMaxValue());
	}

	@Override
	protected void customHavingItemLT(final H2QQ table, final FEMDuration item) throws NullPointerException, IllegalArgumentException {
		table.push("DURATIONMILLISMAX<").push(item.durationmillisMinValue());
	}

	@Override
	protected void customHavingItemGT(final H2QQ table, final FEMDuration item) throws NullPointerException, IllegalArgumentException {
		table.push("DURATIONMILLISMIN>").push(item.durationmillisMaxValue());
	}

	private FEMDurationBag(final H2QS owner, final H2QQ table, final String cache) {
		super(owner, table, cache);
	}

}
