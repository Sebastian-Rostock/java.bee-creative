// JDGItem(DOMNode)
// JDGItem(DOMNode, JDGItem)
function JDGItem(Elem, Parent) {
	this.children = [];
	if (Parent) {
		this.head = Elem;
		this.body = Elem.nextElementSibling;
		this.head._item_ = this;
		this.body._item_ = this;
		this.parent = Parent;
		this.hideFlat();
		Elem.onclick = JDGItem.clickHead;
		Parent.children.push(this);
		if (!JDGItem.findNodes(Elem, 'a[@name][1]')[0]) Parent.showChildItem = this;
	} else {
		this.body = Elem;
		function createChildren(Parent, XPath, Method) {
			JDGItem.findNodes(Parent.body, XPath).map(function(Node) {
				var item = new JDGItem(Node, Parent);
				Parent.children.push(item);
				if (Method) Method(item);
			});
		}
		createChildren(this, 'h1', function(Item) {
			createChildren(Item, 'h2', function(Item) {
				createChildren(Item, 'h3');
			});
		});
	}
}

// JDGItem.findItem(DOMNode): JDGItem
JDGItem.findItem = function(Elem) {
	for (item = null; Elem; Elem = Elem.parentNode)
		if (item = Elem._item_) return item;
	return null;
};

// JDGItem.findNodes(Elem: DOMNode, XPath: String): DOMNode[]
JDGItem.findNodes = document.evaluate ? function(Elem, XPath) {
	if (!Elem) return [];
	var nodes = document.evaluate(XPath, Elem, null, XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null), result = [];
	for (i = 0, size = nodes.snapshotLength; i < size; i++)
		result.push(nodes.snapshotItem(i));
	return result;
} : function(Elem, XPath) {
	if (!Elem) return [];
	var nodes = Elem.selectNodes(XPath), result = [];
	for (i = 0, size = nodes.length; i < size; i++)
		result.push(nodes.item(i++));
	return result;
};

// JDGItem.clickHead(DOMEvent): Void
JDGItem.clickHead = function(Evt) {
	if ((this != Evt.target) && !Evt.target.name) return;
	var item = this._item_;
	item.visible ? item.hideFlat() : item.showFlat();
};

// JDGItem.updateClass(DOMNode, String): Void
JDGItem.updateClass = function(Elem, Class) {
	if (Elem) Elem.className = Class;
};

// JDGItem.hideFlat(): Void
JDGItem.prototype.hideFlat = function() {
	this.visible = false;
	JDGItem.updateClass(this.head, 'HEAD_HIDE');
	JDGItem.updateClass(this.body, 'BODY_HIDE');
};

// JDGItem.hideDeep(): Void
JDGItem.prototype.hideDeep = function() {
	this.children.map(function(Item) {
		Item.hideFlat();
		Item.hideDeep();
	});
};

// JDGItem.showFlat(): Void
JDGItem.prototype.showFlat = function() {
	this.visible = true;
	JDGItem.updateClass(this.head, 'HEAD_SHOW');
	JDGItem.updateClass(this.body, 'BODY_SHOW');
};

// JDGItem.showPath(): Void
JDGItem.prototype.showPath = function() {
	this.showFlat();
	if (this.parent) this.parent.showPath();
};

// JDGItem.showDeep(): Void
JDGItem.prototype.showDeep = function() {
	this.children.map(function(Item) {
		Item.showFlat();
		Item.showDeep();
	});
};

// JDGItem.ROOT: JDGItem
JDGItem.ROOT = new JDGItem(document.body);

// JDGMenu()
function JDGMenu() {
	this.node = document.createElement('div');
	this.names = {};
	this.node.className = 'TOOL_MENU';
	this.node.innerHTML = '<datalist id="itemnames"></datalist>\
		<input onclick="JDGItem.ROOT.hideDeep()" type="button" value="\u25b2" title="Alle Elemente reduzieren"/>\
		<input onclick="JDGItem.ROOT.showDeep()" type="button" value="\u25bc" title="Alle Elemente erweitern"/>\
		<input oninput="JDGMenu.MENU.checkText()" type="text" list="itemnames" placeholder="field, method, ..."/>\
		<input oninput="JDGMenu.MENU.checkOptn()" type="checkbox" title="Details flach darstellen"/>\
		<input oninput="JDGMenu.MENU.checkOptn()" type="checkbox" title="Signatur ausblenden"/>\
		<input oninput="JDGMenu.MENU.checkOptn()" type="checkbox" title="Referenzen ausblenden"/>\
	';
	document.body.appendChild(this.node);
	var children = this.node.children;
	this.textNode = children[3];
	this.listNode = children[0];
	this.flatenNode = children[4];
	this.signatureNode = children[5];
	this.referencesNode = children[6];
	JDGItem.findNodes(document.body, '//a[@name]').map(function(Node) {
		var name = Node.name;
		this.names['#' + name] = Node;
		if (name.indexOf('-') >= 0) return;
		var child = document.createElement('option');
		child.value = name;
		this.listNode.appendChild(child);
	}, this);
	window.onhashchange = JDGMenu.checkHash;
}

// JDGMenu.checkHash(): Void
JDGMenu.prototype.checkHash = function() {
	var node = this.names[unescape(window.location.hash)], item = JDGItem.findItem(node), item2 = item ? item.showChildItem || item : null;
	if (item2) item2.showPath();
	if (node) node.scrollIntoView();
};

// JDGMenu.checkText(): Void
JDGMenu.prototype.checkText = function() {
	var name = '#' + this.textNode.value;
	if (this.names[name]) window.location.href = name;
};

// JDGMenu.checkText(): Void
JDGMenu.prototype.checkOptn = function(){
	document.body.className = [
		this.flatenNode.checked ? 'INFO_FLAT' : 'INFO_TREE',
		this.signatureNode.checked ? 'SIGN_HIDE' : 'SIGN_SHOW',
		this.referencesNode.checked ? 'REFS_HIDE' : 'REFS_SHOW'
	].join(' ');
};

// JDGMenu.MENU: JDGMenu
JDGMenu.MENU = new JDGMenu(document.body);
JDGMenu.MENU.checkHash();
JDGMenu.MENU.checkOptn();
