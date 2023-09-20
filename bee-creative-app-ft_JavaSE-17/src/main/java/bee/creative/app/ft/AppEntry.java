package bee.creative.app.ft;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import bee.creative.lang.Strings;
import bee.creative.util.Filters;
import bee.creative.util.Iterables;

class AppEntry {

	public static List<AppEntry> list() {
		return new ArrayList<>();
	}

	public static AppEntry parse(final String src) {
		final var parts = Strings.split(AppEntry.itemText, src.trim());
		final var count = parts.size();
		if (count == 0) return null;
		if (count == 1) return new AppEntry(parts.get(0), "");
		return new AppEntry(parts.get(0), parts.get(1));
	}

	public static List<AppEntry> parseAll(final String src) {
		return Iterables.translate(Strings.match(FTMain.lineText, src), AppEntry::parse).filter(Filters.empty()).toList();
	}

	public static String printAll(final Iterable<AppEntry> src) {
		final var res = new StringBuilder(1 << 20);
		Strings.join(res, "\r\n", src, (res2, src2) -> res2.append(src2.source).append(src2.target.text().isEmpty() ? "" : "\t").append(src2.target));
		return res.toString();
	}

	AppItem source;

	AppItem target;

	public AppEntry(String source, String target) {
		this.source = new AppItem(source);
		this.target = new AppItem(target);
	}

	static final Pattern lineText = Pattern.compile("[^\r\n]+");

	static final Pattern itemText = Pattern.compile("[\t]+");

}