package bee.creative.app.ft;

import java.io.File;
import java.util.ArrayList;
import bee.creative.util.HashMap2;

class FTCaches implements FTStorable {

	public FTCaches() {
		this.hashes.add(new FTHashes(""));
	}

	public String get(final String filePath, final long hashSize) {
		return this.getHashes(filePath).get(filePath, hashSize);
	}

	@Override
	public void persist() {
		this.hashes.forEach(FTHashes::persist);
	}

	@Override
	public void restore() {
		this.hashes.forEach(FTHashes::restore);
	}

	private final HashMap2<String, FTHashes> table = new HashMap2<>(512);

	private final ArrayList<FTHashes> hashes = new ArrayList<>(16);

	private final ArrayList<String> rootpaths = new ArrayList<>(16);

	private FTHashes getHashes(final String filePath) {
		this.rootpaths.clear();
		for (var rootpath = filePath; true;) {
			final var index = rootpath.lastIndexOf(File.separatorChar);
			if (index <= 0) return this.putHashes(this.hashes.get(0));
			rootpath = rootpath.substring(0, index);
			final var hashes = this.table.get(rootpath);
			if (hashes != null) return this.putHashes(hashes);
			final var file = FTHashes.fileFrom(rootpath);
			if (file.isFile()) return this.putHashes(rootpath);
			this.rootpaths.add(rootpath);
		}
	}

	private FTHashes putHashes(final String rootpath) {
		final var hashes = new FTHashes(rootpath);
		hashes.restore();
		this.table.put(rootpath, hashes);
		this.hashes.add(hashes);
		return this.putHashes(hashes);
	}

	private FTHashes putHashes(final FTHashes hashes) {
		this.rootpaths.forEach(rootpath -> this.table.put(rootpath, hashes));
		this.rootpaths.clear();
		return hashes;
	}

}
