package bee.creative.doc;

import java.util.List;

class PackageNode implements Comparable<PackageNode> {

	String name;

	List<DocNode> docs;

	List<TagNode> tags;

	List<ClassNode> classes;

	@Override
	public int compareTo(final PackageNode that) {
		return this.name.compareTo(that.name);
	}

}
