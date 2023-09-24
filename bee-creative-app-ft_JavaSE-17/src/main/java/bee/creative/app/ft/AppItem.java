package bee.creative.app.ft;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

class AppItem {

	public final String text;

	public AppItem(String text) {
		this.text = text.toString();
	}

	public boolean isFile() {
		return (this.fileOrNull() != null) && this.file.isFile();
	}

	public boolean isFolder() {
		return (this.fileOrNull() != null) && this.file.isDirectory();
	}

	public File fileOrNull() {
		if (this.file != AppItem.NO_FILE) return this.file;
		var file = new File(this.text);
		return this.file = file.isAbsolute() ? file : null;
	}

	public Long sizeOrNull() {
		if (this.size != AppItem.NO_LONG) return this.size;
		return this.size = this.isFile() ? this.file.length() : null;
	}

	public Path pathOrNull() {
		if (this.path != AppItem.NO_PATH) return this.path;
		return this.path = this.fileOrNull() != null ? this.file.toPath() : null;
	}

	public Long timeOrNull() {
		if (this.time != AppItem.NO_LONG) return this.time;
		return this.time = this.isFile() ? this.file.lastModified() : null;
	}

	public Long madeOrNull() {
		if (this.made != AppItem.NO_LONG) return this.made;
		try {
			return this.made = this.isFile() && (this.pathOrNull() != null) //
				? Files.readAttributes(this.path, BasicFileAttributes.class).creationTime().toMillis() : null;
		} catch (Exception ignore) {}
		return this.made = null;
	}

	@Override
	public String toString() {
		return this.text;
	}

	private static final File NO_FILE = new File("");

	private static final Path NO_PATH = AppItem.NO_FILE.toPath();

	private static final Long NO_LONG = Long.MIN_VALUE;

	private File file = AppItem.NO_FILE;

	private Long size = AppItem.NO_LONG;

	private Long time = AppItem.NO_LONG;

	private Path path = AppItem.NO_PATH;

	private Long made = AppItem.NO_LONG;

}