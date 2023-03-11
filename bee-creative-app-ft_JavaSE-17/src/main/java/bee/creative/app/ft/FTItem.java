package bee.creative.app.ft;

/** @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
class FTItem {

	public final String sourcePath;

	public Long sourceSize;

	public Object sourceHash;

	public Object sourceData;

	public FTItem previousItem;

	public FTItem(final String sourcePath) {
		this.sourcePath = sourcePath;
	}

	@Override
	public String toString() {
		return this.sourcePath;
	}

}