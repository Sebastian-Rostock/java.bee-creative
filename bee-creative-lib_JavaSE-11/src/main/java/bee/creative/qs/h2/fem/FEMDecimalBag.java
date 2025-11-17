package bee.creative.qs.h2.fem;

import java.sql.ResultSet;
import java.sql.SQLException;
import bee.creative.fem.FEMDecimal;
import bee.creative.qs.h2.H2QIRangeBag;
import bee.creative.qs.h2.H2QQ;
import bee.creative.qs.h2.H2QS;

public class FEMDecimalBag extends H2QIRangeBag<FEMDecimal, FEMDecimalBag> {

	public FEMDecimalBag(H2QS owner) {
		this(owner, new H2QQ().push("SELECT * FROM QD_FEMDECIMAL"), "QI_FEMDECIMAL");
	}

	@Override
	protected FEMDecimal customItem(ResultSet next) throws SQLException {
		return FEMDecimal.from(next.getDouble(2));
	}

	@Override
	protected void customSetup() {
		new H2QQ().push("CREATE TABLE IF NOT EXISTS QD_FEMDECIMAL (N BIGINT NOT NULL, DECIMALVALUE DOUBLE PRECISION NOT NULL, PRIMARY KEY (N));" + //
			"CREATE INDEX IF NOT EXISTS QD_FEMDECIMAL_INDEX_1 ON QD_FEMDECIMAL (DECIMALVALUE);").update(this.owner);
	}

	@Override
	protected void customInsert(InsertSet putItemSet) throws SQLException {
		try (var stmt = new H2QQ().push("MERGE INTO QD_FEMDECIMAL (N, DECIMALVALUE) VALUES (?, ?)").prepare(this.owner)) {
			for (var entry: putItemSet) {
				try {
					var item = Double.valueOf(entry.getValue());
					stmt.setObject(1, entry.getKey());
					stmt.setObject(2, item);
					stmt.addBatch();
				} catch (Exception ignore) {}
			}
			stmt.executeBatch();
		}
	}

	@Override
	protected void customDelete(DeleteSet popItemSet) throws SQLException {
		new H2QQ().push("DELETE FROM QD_FEMDECIMAL WHERE N IN (").push(popItemSet).push(")").update(this.owner);
	}

	@Override
	protected FEMDecimalBag customHaving(H2QQ table) throws NullPointerException, IllegalArgumentException {
		return new FEMDecimalBag(this.owner, table, null);
	}

	@Override
	protected void customHavingItemEQ(H2QQ table, FEMDecimal item) throws NullPointerException, IllegalArgumentException {
		table.push("DECIMALVALUE=").push(item);
	}

	@Override
	protected void customHavingItemLT(H2QQ table, FEMDecimal item) throws NullPointerException, IllegalArgumentException {
		table.push("DECIMALVALUE<").push(item);
	}

	@Override
	protected void customHavingItemGT(H2QQ table, FEMDecimal item) throws NullPointerException, IllegalArgumentException {
		table.push("DECIMALVALUE>").push(item);
	}

	private FEMDecimalBag(H2QS owner, H2QQ table, String cache) {
		super(owner, table, cache);
	}

}
