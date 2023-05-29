package bee.creative.qs.h2.fem;

import java.sql.ResultSet;
import java.sql.SQLException;
import bee.creative.fem.FEMInteger;
import bee.creative.qs.h2.H2QDBag;
import bee.creative.qs.h2.H2QQ;
import bee.creative.qs.h2.H2QS;

public class H2QDFEMIntegerBag extends H2QDBag<FEMInteger> {

	public static H2QDFEMIntegerBag from(final H2QS owner) {
		final var result = new H2QDFEMIntegerBag(owner, new H2QQ().push("SELECT N, V FROM QD_FEMINTEGER_VALUE"), "SELECT N FROM QD_FEMINTEGER_CACHE");
		new H2QQ() //
			.push("CREATE TABLE IF NOT EXISTS QD_FEMINTEGER_CACHE (N BIGINT NOT NULL, PRIMARY KEY (N));") //
			.push("CREATE TABLE IF NOT EXISTS QD_FEMINTEGER_VALUE (N BIGINT NOT NULL, V BIGINT NOT NULL, PRIMARY KEY (N));") //
			.push("CREATE INDEX IF NOT EXISTS QD_FEMINTEGER_VALUE_V ON QD_FEMINTEGER_VALUE (V);") //
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
		System.out.println(putItemSet);
		super.putItems(putItemSet);
	}

	@Override
	protected void popItems(final PopItemSet popItemSet) throws SQLException {
		System.out.println(popItemSet);
		super.popItems(popItemSet);
	}

}
