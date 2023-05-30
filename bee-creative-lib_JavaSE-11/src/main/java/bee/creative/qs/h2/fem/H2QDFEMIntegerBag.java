package bee.creative.qs.h2.fem;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map.Entry;
import bee.creative.fem.FEMInteger;
import bee.creative.qs.h2.H2QDBag;
import bee.creative.qs.h2.H2QQ;
import bee.creative.qs.h2.H2QS;

public class H2QDFEMIntegerBag extends H2QDBag<FEMInteger> {

	public static H2QDFEMIntegerBag from(final H2QS owner) {
		final var result = new H2QDFEMIntegerBag(owner, new H2QQ().push("SELECT N, V FROM QD_FEMINTEGER"), "QI_FEMINTEGER");
		new H2QQ() //
			.push("CREATE TABLE IF NOT EXISTS QD_FEMINTEGER (N BIGINT NOT NULL, V BIGINT NOT NULL, PRIMARY KEY (N));") //
			.push("CREATE INDEX IF NOT EXISTS QD_FEMINTEGER_INDEX_V ON QD_FEMINTEGER (V);") //
			.update(owner);
		return result;
	}

	public H2QDFEMIntegerBag selectLT(final long value) {
		return new H2QDFEMIntegerBag(this.owner, new H2QQ().push("SELECT N, V FROM (").push(this).push(") WHERE V<").push(value), null);
	}

	public H2QDFEMIntegerBag selectLE(final long value) {
		return new H2QDFEMIntegerBag(this.owner, new H2QQ().push("SELECT N, V FROM (").push(this).push(") WHERE V<=").push(value), null);
	}

	public H2QDFEMIntegerBag selectGT(final long value) {
		return new H2QDFEMIntegerBag(this.owner, new H2QQ().push("SELECT N, V FROM (").push(this).push(") WHERE V>").push(value), null);
	}

	public H2QDFEMIntegerBag selectGE(final long value) {
		return new H2QDFEMIntegerBag(this.owner, new H2QQ().push("SELECT N, V FROM (").push(this).push(") WHERE V>=").push(value), null);
	}

	protected H2QDFEMIntegerBag(final H2QS owner, final H2QQ table, final String cache) {
		super(owner, table, cache);
	}

	@Override
	protected FEMInteger item(final ResultSet next) throws SQLException {
		return FEMInteger.from(next.getLong(2));
	}

	@Override
	protected void putItems(final PutItemSet putItemSet) throws SQLException {
		try (var stmt = new H2QQ().push("MERGE INTO QD_FEMINTEGER (N, V) VALUES (?, ?)").prepare(this.owner)) {
			for (final Entry<Long, String> i: putItemSet) {
				try {
					stmt.setObject(2, Long.valueOf(i.getValue()));
					stmt.setObject(1, i.getKey());
					stmt.addBatch();
				} catch (final Exception ignore) {}
			}
			stmt.executeBatch();
		}
		super.putItems(putItemSet);
	}

	@Override
	protected void popItems(final PopItemSet popItemSet) throws SQLException {
		new H2QQ().push("DELETE FROM QD_FEMINTEGER WHERE N IN (").push(popItemSet).push(")").update(this.owner);
		super.popItems(popItemSet);
	}

}
