
/** @param {JavadocInfo} javadocInfo
 * @returns {JavadocNode} */
function createJavadocNode(javadocInfo) {

    /** @type {JavadocNode} */
    var javadocNode = { ...javadocInfo, nodeItems: {}, nodePackages: [] };

    /** @param {string} name */
    var createElem = (name) => document.createElement(name);

    var setupItemNodes = () => {

        var nodeItems = javadocNode.nodeItems;

        /**
         * @param {{href: string}} a 
         * @param {{href: string}} b 
         * @returns {number} */
        var compareHref = (a, b) => a.href.localeCompare(b.href);

        /**
         * @param {any} a 
         * @param {any} b 
         * @param {string} key 
         * @returns {number} */
        var compareField = (a, b, key) => a[key] ? (b[key] ? 0 : +1) : (b[key] ? -1 : 0);

        /**
       * @template {{href: string}} T
       * @param {T[] | undefined} infos 
       * @param {(item: T) => unknown} setup */
        var createNodes = (infos, setup) => (infos || [])?.map(info => (setup(nodeItems[info.href] = (info = { ...info })), info));

        /**
         * @param {ItemNode} templateNode  */
        var setupParameterNode = (templateNode) => {
            templateNode.nodeAsParameter = templateNode;
        };

        /**
         * @param {ItemNode} templateNode  */
        var setupTemplateNode = (templateNode) => {
            templateNode.nodeAsTemplate = templateNode;
        };

        /**
         * @param {MemberNode} memberNode 
         * @param {string} asMemberKey */
        var setupMemberNode = (memberNode, asMemberKey) => {
            memberNode.nodeLabel = createElem("H3");
            memberNode.nodeInfos = createElem("div");
            memberNode.nodeAsMember = memberNode;
            memberNode[asMemberKey] = memberNode;
            createNodes(memberNode.vars, setupTemplateNode);
            createNodes(memberNode.params, setupParameterNode);
        };

        /**
         * @param {MemberNode} fieldNode  */
        var setupFieldNode = (fieldNode) => {
            setupMemberNode(fieldNode);
            fieldNode.nodeAsField = fieldNode;
        };

        /**
          * @param {MemberNode} methodNode  */
        var setupMethodNode = (methodNode) => {
            setupMemberNode(methodNode);
            methodNode.nodeAsMethod = methodNode;
        };

        /**
          * @param {MemberNode} constructorNode  */
        var setupConstructorNode = (constructorNode) => {
            setupMemberNode(constructorNode);
            constructorNode.nodeAsConstructor = constructorNode;
        };

        /** 
                 * @param {ClassNode} classNode */
        var setupClassNode = (classNode) => {
            classNode.nodeAsClass = classNode;
            classNode.nodeTitle = createElem("H2");
            classNode.nodeLabel = createElem("H3");
            classNode.nodeInfos = createElem("div");
            classNode.nodeMembers = [
                ...createNodes(classNode.fields, setupFieldNode),
                ...createNodes(classNode.methods, setupMethodNode),
                ...createNodes(classNode.constructors, setupConstructorNode)
            ].sort((a, b) => 0 ||
                compareField(b, a, "isPublic") ||
                compareField(b, a, "isProtected") ||
                compareField(a, b, "isPrivate") ||
                compareField(b, a, "isStatic") ||
                compareField(b, a, "nodeAsField") ||
                compareField(a, b, "nodeAsMethod") ||
                compareHref(a, b)
            );
            createNodes(classNode.vars, setupTemplateNode);
        }

        /**
         * @param {PackageNode} packageNode  */
        var setupPackageNode = (packageNode) => {
            packageNode.nodeAsPackage = packageNode;
            packageNode.nodeTitle = createElem("H1");
            packageNode.nodeInfos = createElem("div");
            packageNode.nodeClasses = createNodes(packageNode.classes, setupClassNode).sort(compareHref);
        };

        javadocNode.nodePackages.push(...createNodes(javadocInfo.packages, setupPackageNode).sort(compareHref));

    };

    var updateItemNodes = () => {

        javadocNode.nodeAsJavadoc = javadocNode;

        javadocNode.nodePackages.forEach(packageNode => {
            packageNode.nodeParent = javadocNode;
            packageNode.nodeClasses.forEach(classNode => {
                classNode.nodeParent = javadocNode.nodeItems[classNode.parent] || packageNode;
                classNode.nodeMembers.forEach(memberNode => memberNode.nodeParent = classNode);
            });
        });

    };

    var updateItemInfos = () => {

        /** 
         * @param {HTMLElement} element 
         * @param {(html: string[]) => unknown} updateHtml
         * @param {((html: string[]) => unknown)|undefined} updateClass */
        var updateElem = (element, updateHtml, updateClass) => {
            let html = [], attr = [];
            updateHtml(html);
            updateClass?.(attr);
            element.innerHTML = html.join('');
            element.className = attr.join('');
        };

        var toText = (src) => (toTextNode.textContent = src, toTextNode.innerHTML);

        var toTextNode = createElem("div");

        /**
         * @template T
         * @param {string[]} res 
         * @param {T[]|undefined} items 
         * @param {string} enter 
         * @param {string} comma 
         * @param {string} leave
         * @param {(res: string[], item: T, index?: number) => unknown} update */
        var pushList = (res, items, enter, comma, leave, update) => {
            if (!items?.length) return;
            res.push(enter);
            items.forEach((item, index) => (index && res.push(comma), update(res, item, index)));
            res.push(leave);
        };

        /**
          * @param {string[]} res 
         * @param {DocInfo[]|undefined} docs */
        var pushDocs = (res, docs) => {
            if (!docs?.length) return;
            docs.forEach(({ text, code, href, type, html }) => {
                if (text) {
                    res.push(toText(text));
                } else if (code) {
                    res.push('<code>', toText(code), '</code>');
                } else if (href) {
                    res.push('<a href="#', href, '">', html, '</a>');
                } else if (type) {
                    res.push('<span class="TYPE-', type, '">', html, '</span>');
                } else {
                    res.push(html);
                }
            });
        };

        /**
         * 
         * @param {string[]} res 
         * @param {TagInfo[]|undefined} tags */
        var pushTags = (res, tags) => pushList(res, tags, '<dl class="TAGS">', '', '</dl>', ({ type, href, html, docs }) => {
            res.push('<dt class="TAG-', type, '">');
            href ? res.push('<a href="#', href, '">', html, '</a>') : res.push(type);
            res.push('</dt><dd>');
            pushDocs(res, docs);
            res.push('</dd>');
        });

        /**
         * 
         * @param {string[]} res 
         * @param {TemplateInfo[]|undefined} vars */
        var pushVars = (res, vars) => pushList(res, vars?.filter(({ docs }) => docs), '<dl class="VARS">', '', '</dl>', (_, { href, name, docs }) => {
            res.push('<dt><a name="', href, '">', name, '</a></dt><dd>');
            pushDocs(res, docs);
            console.log(name)
            res.push('</dd>');
        });

        /**
          * 
          * @param {string[]} res 
          * @param {ParameterInfo[]|undefined} params */
        var pushParams = (res, params) => {
            pushList(res, params?.filter(({ docs }) => docs), '<dl class="PARAMS">', '', '</dl>', (_, { href, name, docs }) => {
                res.push('<dt><a name="', href, '">', name, '</a></dt><dd>');
                pushDocs(res, docs);
                res.push('</dd>');
            });
        };

        /**
         * 
         * @param {string[]} res 
         * @param {ReturnInfo|undefined} returns */
        var pushReturns = (res, returns) => {
            if (!returns?.docs) return;
            res.push('<dl class="RETURN"><dd>');
            pushDocs(res, returns.docs);
            res.push('</dd></dl>');
        };

        /**
         * 
         * @param {string[]} res 
         * @param {ReturnInfo[]|undefined} throws */
        var pushThrows = (res, throws) => {
            pushList(res, throws, '<dl class="THROWS">', '', '</dl>', (_, { type, docs }) => {
                res.push('<dt>');
                pushType(res, type);
                res.push('</dt><dd>');
                pushDocs(res, docs);
                res.push('</dd>');
            });
        };

        /**
          * 
          * @param {string[]} res 
          * @param {ParameterInfo[]|undefined} params */
        var pushMemberParams = (res, params) => {
            if (params) {
                res.push('(');
                pushList(res, params, '', ', ', '', (res, parameterInfo) => {
                    res.push('<a href="#', parameterInfo.href, '">', parameterInfo.name, '</a>: ');
                    pushType(res, parameterInfo.type);
                });
                res.push(')');
            }
        };

        /**
         * 
         * @param {string[]} res 
         * @param {ClassNode} classNode */
        var pushName = (res, classNode) => {
            res.push(classNode.name);
        };

        /**
         * 
         * @param {string[]} res 
         * @param {TypeInfo2|undefined} type */
        var pushType = (res, type) => {
            if (!type) return;
            if (type.super?.length) {
                pushList(res, type.super, '?% ', '&amp;', '', pushType);
            } else if (type.extends) {
                pushList(res, type.extends, '?: ', '&amp;', '', pushType);
            } else {
                // TODO link
                var nodeItem = javadocNode.nodeItems[type.href];
                nodeItem ? res.push('<a href="#', nodeItem.href, '">', nodeItem.name, '</a>') : res.push(type.name);
                pushList(res, type.args, '&lt;', ', ', '&gt;', pushType);
                res.push(type.dims || "");
            }
        };

        /**
        * @param {string[]} res 
        * @param {TemplateInfo} templateInfo */
        var pushTemplate = (res, templateInfo) => {
            res.push('<a href="#', templateInfo.href, '">', templateInfo.name, '</a>');
            pushList(res, templateInfo.extends, ': ', '&amp;', '', pushType);
        };

        /**
         * @param {ItemNode | undefined} itemNode 
         * @returns {ItemNode[]} */
        var computePath = (itemNode) => itemNode ? [...computePath(itemNode.nodeParent), itemNode] : [];

        /** @param {MemberNode} memberNode */
        var updateMemberNode = (memberNode) => {
            updateElem(memberNode.nodeLabel, res => {
                res.push(memberNode.isPublic ? '+ ' : memberNode.isProtected ? '# ' : memberNode.isPrivate ? '- ' : '~ ');
                res.push('<a name="', memberNode.href, '">', memberNode.name, '</a>');
                pushList(res, memberNode.vars, '&lt;', ', ', '&gt;', pushTemplate);
                if (!memberNode.nodeAsField) {
                    res.push('(');
                    pushList(res, memberNode.params, '', ', ', '', (res, parameterInfo) => {
                        res.push('<a href="#', parameterInfo.href, '">', parameterInfo.name, '</a>: ');
                        pushType(res, parameterInfo.type);
                    });
                    res.push(')');
                }
                if (memberNode.returns) {
                    res.push(': ');
                    pushType(res, memberNode.returns.type);
                }
            }, res => {
                res.push('S', memberNode.isStatic ? 'T' : 'F', ' A', memberNode.isAbstract ? 'T' : 'F');
            });
            updateElem(memberNode.nodeInfos, res => {
                pushDocs(res, memberNode.docs);
                pushTags(res, memberNode.tags);
                pushVars(res, memberNode.vars);
                pushParams(res, memberNode.params);
                pushReturns(res, memberNode.returns);
                pushThrows(res, memberNode.throws);
                //  TODO  returns, throws
            });
        };

        /** @param {ClassNode} classNode */
        var updateClassNode = (classNode) => {
            updateElem(classNode.nodeTitle, res => {
                res.push('<a name="', classNode.href, '">');
                pushList(res, computePath(classNode).slice(2), '', '.', '', pushName);
                res.push('</a>');
            });
            updateElem(classNode.nodeLabel, res => {
                res.push(classNode.isPublic ? '+ ' : classNode.isProtected ? '# ' : classNode.isPrivate ? '- ' : '~ ');
                pushList(res, computePath(classNode).slice(1), '', '.', '', pushName);
                pushList(res, classNode.vars, '&lt;', ', ', '&gt;', pushTemplate);
                pushList(res, [...(classNode.superclass ? [classNode.superclass] : []), ...(classNode.interfaces || [])], '<br>: ', ', ', '', pushType);
            }, res => {
                res.push('A', classNode.isAbstract ? 'T' : 'F');
            });
            updateElem(classNode.nodeInfos, res => {
                pushDocs(res, classNode.docs);
                pushTags(res, classNode.tags);
                pushVars(res, classNode.vars);
            });
            classNode.nodeMembers.forEach(updateMemberNode);
        };

        /** @param {PackageNode} packageNode */
        var updatePackageNode = (packageNode) => {
            updateElem(packageNode.nodeTitle, html => {
                html.push('<a name="', packageNode.href, '">', packageNode.name, '</a>');
            });
            updateElem(packageNode.nodeInfos, html => {
                pushDocs(html, packageNode.docs);
                pushTags(html, packageNode.tags);
            });
            packageNode.nodeClasses.forEach(updateClassNode);
        };

        javadocNode.nodePackages.forEach(updatePackageNode);

    };

    setupItemNodes();
    updateItemNodes();
    updateItemInfos();

    return javadocNode;
}
