
interface ItemNode {
    nodeParent?: ItemNode
    nodeAsClass?: ClassNode
    nodeAsMember?: MemberNode
    nodeAsPackage?: PackageNode
    nodeAsJavadoc?: JavadocNode
    nodeAsField?: FieldInfo
    nodeAsMethod?: MethodInfo
    nodeAsConstructor?: ConstructorInfo
}

interface MemberNode extends ItemNode, Partial<FieldInfo>, Partial<MethodInfo>, Partial<ConstructorInfo> {

    // <h3> + name: type
    nodeLabel: HTMLDivElement

    // <div>  docs, tags, vars, params, returns, throws
    nodeInfos: HTMLDivElement
}

interface ClassNode extends ItemNode, ClassInfo {

    // <h2> outername.innername<gen>
    nodeTitle: HTMLElement

    // <h3> + path.name<get>: type, ...
    nodeLabel: HTMLElement

    // <div> docs, tags, vars
    nodeInfos: HTMLElement

    // public vor private, static vor instance, field vor method 
    nodeMembers: MemberNode[]
}

interface PackageNode extends ItemNode, PackageInfo {

    // <h1> name
    nodeTitle: HTMLElement

    // <div> docs, tags
    nodeInfos: HTMLElement

    nodeClasses: ClassNode[]
}

interface JavadocNode extends ItemNode, JavadocInfo {

    nodeItems: Record<string, ItemNode>

    nodePackages: PackageNode[]
}




