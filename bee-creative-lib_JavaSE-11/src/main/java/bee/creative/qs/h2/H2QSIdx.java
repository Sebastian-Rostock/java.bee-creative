package bee.creative.qs.h2;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map.Entry;
import bee.creative.util.Entries;

public class H2QSIdx {


	static class IndexInsert extends H2QISet<Entry<Long, String>> {

		IndexInsert(final H2QS owner) {
			super(owner, new H2QQ().push("select N, V from QV where not exists (select N from QI where QI.N=QN.N)"));
		}

		@Override
		protected Entry<Long, String> item(final ResultSet next) throws SQLException {
			return Entries.from(next.getLong(1), next.getString(2));
		}

	}

	static class IndexDelete extends H2QISet<Long> {

		IndexDelete(final H2QS owner) {
			super(owner, new H2QQ().push("select * from QI"));
		}

		@Override
		protected Long item(final ResultSet next) throws SQLException {
			return null;
		}

	}
	
}
