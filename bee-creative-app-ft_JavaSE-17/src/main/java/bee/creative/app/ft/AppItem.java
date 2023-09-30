package bee.creative.app.ft;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

/** Diese Klasse implementiert ein Objekt, dass eine {@link #text Zeichenkette} als {@link #fileOrNull() absoluten Datenpfad} interpretieren und zu der damit
 * genannten Datei die {@link #sizeOrNull() Größe}, den {@link #timeOrNull() Änderungszeitpunkt} sowie den {@link #madeOrNull() Erzeugungszeitpunkt}
 * bereitstellen kann.
 *
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
class AppItem {

	/** Dieses Feld speichert die Zeichenkette. */
	public final String text;

	/** Dieser Konstruktor initialisiert den {@link #text}. */
	public AppItem(String text) {
		this.text = text.toString();
	}

	public File fileOrNull() {
		if (this.file != AppItem.NO_FILE) return this.file;
		try {
			var file = new File(this.text);
			return this.file = file.isAbsolute() ? file.getAbsoluteFile() : null;
		} catch (Exception ignore) {}
		return this.file = null;
	}

	public Long sizeOrNull() {
		if (this.size != AppItem.NO_LONG) return this.size;
		try {
			return this.size = (this.fileOrNull() != null) && this.file.isFile() ? this.file.length() : null;
		} catch (Exception ignore) {}
		return this.size = null;
	}

	public Path pathOrNull() {
		if (this.path != AppItem.NO_PATH) return this.path;
		return this.path = this.fileOrNull() != null ? this.file.toPath() : null;
	}

	public Long timeOrNull() {
		if (this.time != AppItem.NO_LONG) return this.time;
		return this.time = (this.fileOrNull() != null) && this.file.isFile() ? this.file.lastModified() : null;
	}

	public Long madeOrNull() {
		if (this.made != AppItem.NO_LONG) return this.made;
		try {
			return this.made = (this.fileOrNull() != null) && this.file.isFile() && (this.pathOrNull() != null) //
				? Files.readAttributes(this.path, BasicFileAttributes.class).creationTime().toMillis() : null;
		} catch (Exception ignore) {}
		return this.made = null;
	}

	@Override
	public String toString() {
		return this.text;
	}

	static final File NO_FILE = new File("");

	static final Path NO_PATH = AppItem.NO_FILE.toPath();

	static final Long NO_LONG = Long.MIN_VALUE;

	File file = AppItem.NO_FILE;

	Long size = AppItem.NO_LONG;

	Long time = AppItem.NO_LONG;

	Path path = AppItem.NO_PATH;

	Long made = AppItem.NO_LONG;

	Object hash;

	Object data;

	AppItem prev;

}