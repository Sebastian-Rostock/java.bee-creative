package bee.creative.app.ft;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import bee.creative.lang.Strings;
import bee.creative.util.Filters;
import bee.creative.util.Iterables;

/** Diese Klasse implementiert ein Paar aus {@link #source Eingabedatenpfad} und {@link #target Ausgabedatenpfad}
 *
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
class AppEntry {

	public static List<AppEntry> list() {
		return new ArrayList<>();
	}

	public static AppEntry parse(String src) {
		var parts = Strings.split(AppEntry.ITEM_PATTERN, src);
		var count = parts.size();
		if (count == 0) return null;
		if (count == 1) return new AppEntry(parts.get(0));
		return new AppEntry(parts.get(0), parts.get(1));
	}

	public static List<AppEntry> parseAll(String src) {
		return Iterables.translate(Strings.match(AppEntry.LINE_PATTERN, src), AppEntry::parse).filter(Filters.empty()).toList();
	}

	public static String printAll(Iterable<AppEntry> src) {
		var res = new StringBuilder(1 << 20);
		Strings.join(res, "\r\n", src, (res2, src2) -> {
			res2.append(src2.source).append(src2.target.text.isEmpty() ? "" : "\t").append(src2.target);
		});
		return res.toString();
	}

	/** Dieses Feld speichert den Eingabedatenpfad. */
	public final AppItem source;

	/** Dieses Feld speichert den Ausgabedatenpfad. */
	public final AppItem target;

	/** Dieser Konstruktor initialisiert {@link #source} und {@link #target}. */
	public AppEntry(String source) {
		this(source, "");
	}

	/** Dieser Konstruktor initialisiert {@link #source} und {@link #target}. */
	public AppEntry(String source, String target) {
		this(new AppItem(source), new AppItem(target));
	}

	public AppEntry(AppItem source, AppItem target) {
		this.source = source;
		this.target = target;
	}

	@Override
	public String toString() {
		return this.source.toString();
	}

	private static final Pattern LINE_PATTERN = Pattern.compile("[^\r\n]+");

	private static final Pattern ITEM_PATTERN = Pattern.compile("[\t]+");

}