package bee.creative.qs.h2.fem;

import java.sql.ResultSet;
import java.sql.SQLException;
import bee.creative.fem.FEMInteger;
import bee.creative.qs.h2.H2QDBag;
import bee.creative.qs.h2.H2QQ;
import bee.creative.qs.h2.H2QS;

public class FEMIntegerBag extends H2QDBag<FEMInteger, FEMIntegerBag> {

	public FEMIntegerBag(final H2QS owner) {
		this(owner, new H2QQ().push("SELECT * FROM QD_FEMINTEGER"), "QI_FEMINTEGER");
	}

	@Override
	protected FEMInteger customItem(final ResultSet next) throws SQLException {
		return new FEMInteger(next.getLong(2));
	}

	@Override
	protected void customSetup() {
		new H2QQ().push("CREATE TABLE IF NOT EXISTS QD_FEMINTEGER (N BIGINT NOT NULL, INTEGERVALUE BIGINT NOT NULL, PRIMARY KEY (N));" + //
			"CREATE INDEX IF NOT EXISTS QD_FEMINTEGER_INDEX_1 ON QD_FEMINTEGER (INTEGERVALUE);").update(this.owner);
	}

	@Override
	protected void customInsert(final InsertSet putItemSet) throws SQLException {
		try (final var stmt = new H2QQ().push("MERGE INTO QD_FEMINTEGER (N, INTEGERVALUE) VALUES (?, ?)").prepare(this.owner)) {
			for (final var entry: putItemSet) {
				try {
					final Long item = Long.valueOf(entry.getValue());
					stmt.setObject(1, entry.getKey());
					stmt.setObject(2, item);
					stmt.addBatch();
				} catch (final Exception ignore) {}
			}
			stmt.executeBatch();
		}
	}

	@Override
	protected void customDelete(final DeleteSet popItemSet) throws SQLException {
		new H2QQ().push("DELETE FROM QD_FEMINTEGER WHERE N IN (").push(popItemSet).push(")").update(this.owner);
	}

	@Override
	protected FEMIntegerBag customHaving(final H2QQ table) throws NullPointerException, IllegalArgumentException {
		return new FEMIntegerBag(this.owner, table, null);
	}

	@Override
	protected void customHavingItemEQ(final H2QQ table, final FEMInteger item) throws NullPointerException, IllegalArgumentException {
		table.push("INTEGERVALUE=").push(item);
	}

	@Override
	protected void customHavingItemLT(final H2QQ table, final FEMInteger item) throws NullPointerException, IllegalArgumentException {
		table.push("INTEGERVALUE<").push(item);
	}

	@Override
	protected void customHavingItemGT(final H2QQ table, final FEMInteger item) throws NullPointerException, IllegalArgumentException {
		table.push("INTEGERVALUE>").push(item);
	}

	private FEMIntegerBag(final H2QS owner, final H2QQ table, final String cache) {
		super(owner, table, cache);
	}

}
