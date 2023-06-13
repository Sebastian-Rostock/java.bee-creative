package bee.creative.qs.h2.fem;

import java.sql.ResultSet;
import java.sql.SQLException;
import bee.creative.fem.FEMDuration;
import bee.creative.qs.h2.H2QDBag;
import bee.creative.qs.h2.H2QQ;
import bee.creative.qs.h2.H2QS;

public class FEMDurationBag extends H2QDBag<FEMDuration, FEMDurationBag> {

	public FEMDurationBag(final H2QS owner) {
		this(owner, new H2QQ().push("SELECT * FROM QD_FEMDURATION"), "QI_FEMDURATION");
	}

	@Override
	protected FEMDuration customItem(final ResultSet next) throws SQLException {
		return new FEMDuration(next.getLong(2));
	}

	@Override
	protected void customSetup() {
		new H2QQ().push("CREATE TABLE IF NOT EXISTS QD_FEMDURATION (N BIGINT NOT NULL, DURATIONVALUE BIGINT NOT NULL, " + //
			"DURATIONMINIMUM BIGINT NOT NULL, DURATIONMAXIMUM BIGINT NOT NULL, PRIMARY KEY (N));" + //
			"CREATE INDEX IF NOT EXISTS QD_FEMDURATION_INDEX_1 ON QD_FEMINTEGER (DURATIONMINIMUM, DURATIONMAXIMUM);" + //
			"CREATE INDEX IF NOT EXISTS QD_FEMDURATION_INDEX_2 ON QD_FEMINTEGER (DURATIONMAXIMUM);").update(this.owner);
	}

	@Override
	protected void customInsert(final InsertSet putItemSet) throws SQLException {
		try (final var stmt =
			new H2QQ().push("MERGE INTO QD_FEMDURATION (N, DURATIONVALUE, DURATIONMINIMUM, DURATIONMAXIMUM) VALUES (?, ?, ?, ?)").prepare(this.owner)) {
			for (final var entry: putItemSet) {
				try {
					final var item = FEMDuration.from(entry.getValue());
					stmt.setObject(1, entry.getKey());
					stmt.setLong(2, item.value());
					stmt.setObject(3, this.durationminimum(item));
					stmt.setObject(4, this.durationmaximum(item));
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
		table.push("DURATIONMINIMUM=").push(this.durationminimum(item)).push("AND DURATIONMAXIMUM=").push(this.durationmaximum(item));
	}

	@Override
	protected void customHavingItemLT(final H2QQ table, final FEMDuration item) throws NullPointerException, IllegalArgumentException {
		table.push("DURATIONMAXIMUM<").push(this.durationminimum(item));
	}

	@Override
	protected void customHavingItemGT(final H2QQ table, final FEMDuration item) throws NullPointerException, IllegalArgumentException {
		table.push("DURATIONMINIMUM>").push(this.durationmaximum(item));
	}

	private FEMDurationBag(final H2QS owner, final H2QQ table, final String cache) {
		super(owner, table, cache);
	}

	private Long durationminimum(final FEMDuration item) {
		return (FEMDuration.minLengthOf(item.durationmonthsValue()) * 86400000L) + item.durationmillisValue();
	}

	private Long durationmaximum(final FEMDuration item) {
		return (FEMDuration.maxLengthOf(item.durationmonthsValue()) * 86400000L) + item.durationmillisValue();
	}

}
