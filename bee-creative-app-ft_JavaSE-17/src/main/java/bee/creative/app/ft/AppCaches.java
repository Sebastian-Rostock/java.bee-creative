package bee.creative.app.ft;

import java.io.File;
import java.util.ArrayList;
import bee.creative.util.HashMap2;

class AppCaches implements AppStorable {

	public AppCaches() {
		this.hashes.add(new AppHashes(""));
	}

	public String get(String filePath, long hashSize) {
		return this.getHashes(filePath).get(filePath, hashSize);
	}

	@Override
	public void persist() {
		this.hashes.forEach(AppHashes::persist);
	}

	@Override
	public void restore() {
		this.hashes.forEach(AppHashes::restore);
	}

	private HashMap2<String, AppHashes> table = new HashMap2<>(512);

	private ArrayList<AppHashes> hashes = new ArrayList<>(16);

	private ArrayList<String> rootpaths = new ArrayList<>(16);

	private AppHashes getHashes(String filePath) {
		this.rootpaths.clear();
		for (var rootpath = filePath; true;) {
			var index = rootpath.lastIndexOf(File.separatorChar);
			if (index <= 0) return this.putHashes(this.hashes.get(0));
			rootpath = rootpath.substring(0, index);
			var hashes = this.table.get(rootpath);
			if (hashes != null) return this.putHashes(hashes);
			var file = AppHashes.fileFrom(rootpath);
			if (file.isFile()) return this.putHashes(rootpath);
			this.rootpaths.add(rootpath);
		}
	}

	private AppHashes putHashes(String rootpath) {
		var hashes = new AppHashes(rootpath);
		hashes.restore();
		this.table.put(rootpath, hashes);
		this.hashes.add(hashes);
		return this.putHashes(hashes);
	}

	private AppHashes putHashes(AppHashes hashes) {
		this.rootpaths.forEach(rootpath -> this.table.put(rootpath, hashes));
		this.rootpaths.clear();
		return hashes;
	}

}
