package dotty.tools.dottydoc
package model

import spray.json._

/** This object provides a protocol for serializing the package AST to JSON,
 *  this is supposed to be one-way for performance reasons
 */
object json extends DefaultJsonProtocol {
  import model._
  import model.comment._
  import model.internal._

  implicit val commentFormat = jsonFormat2(Comment.apply)
  implicit val textFormat: JsonFormat[Text] = lazyFormat(jsonFormat(Text, "text"))
  implicit object InlineJsonFormat extends RootJsonFormat[Inline] {
    def write(i: Inline) = i match {
      case i: Text => i.toJson
      case _ => JsString("could not serialize")
    }
    def read(json: JsValue) = ??? // The json serialization is supposed to be one way
  }

  implicit val matLinkFormat = lazyFormat(jsonFormat(MaterializedLink, "title", "target"))
  implicit val noLinkFormat  = lazyFormat(jsonFormat(NoLink, "title", "target"))
  implicit val unsLinkFormat = lazyFormat(jsonFormat(UnsetLink, "title", "query"))

  implicit object MaterializableLinkFormat extends RootJsonFormat[MaterializableLink] {
    def write(obj: MaterializableLink) = obj match {
      case obj: MaterializedLink => addKind(obj.toJson, "MaterializedLink")
      case obj: UnsetLink => addKind(obj.toJson, "UnsetLink")
      case obj: NoLink => addKind(obj.toJson, "NoLink")
    }
    def read(json: JsValue) = ??? // The json serialization is supposed to be one way
  }

  implicit object ReferenceFormat extends RootJsonFormat[Reference] {
    def write(obj: Reference) = obj match {
      case obj: AndTypeReference  => addKind(obj.toJson, "AndTypeReference")
      case obj: OrTypeReference   => addKind(obj.toJson, "OrTypeReference")
      case obj: TypeReference     => addKind(obj.toJson, "TypeReference")
      case obj: NamedReference    => addKind(obj.toJson, "NamedReference")
      case obj: ConstantReference => addKind(obj.toJson, "ConstantReference")
    }
    def read(json: JsValue) = ??? // The json serialization is supposed to be one way
  }

  implicit val tpeRefFormat   = lazyFormat(jsonFormat(TypeReference, "title", "tpeLink", "paramLinks"))
  implicit val orRefFormat    = lazyFormat(jsonFormat(OrTypeReference, "left", "right"))
  implicit val andRefFormat   = lazyFormat(jsonFormat(AndTypeReference, "left", "right"))
  implicit val namedRefFormat = lazyFormat(jsonFormat(NamedReference, "title", "ref"))
  implicit val constRefFormat = lazyFormat(jsonFormat(ConstantReference, "title"))

  implicit object EntityJsonFormat extends RootJsonFormat[Entity] {

    def write(e: Entity) = e match {
      case e: PackageImpl   => addKind(e.toJson, "package")
      case e: ClassImpl     => addKind(e.toJson, "class")
      case e: CaseClassImpl => addKind(e.toJson, "case class")
      case e: TraitImpl     => addKind(e.toJson, "trait")
      case e: ObjectImpl    => addKind(e.toJson, "object")
      case e: DefImpl       => addKind(e.toJson, "def")
      case e: ValImpl       => addKind(e.toJson, "val")
    }
    def read(json: JsValue) = ??? // The json serialization is supposed to be one way
  }

  implicit object PackageFormat extends RootJsonFormat[Package] {
    def write(obj: Package) = obj match { case obj: PackageImpl => addKind(obj.toJson, "package") }
    def read(json: JsValue) = ??? // The json serialization is supposed to be one way
  }

  private def addKind(json: JsValue, kind: String): JsValue = json match {
    case json: JsObject => JsObject(json.fields + ("kind" -> JsString(kind)))
    case other => other
  }

  implicit val valFormat: JsonFormat[ValImpl] =
    lazyFormat(jsonFormat(ValImpl, "name", "modifiers", "path", "returnValue", "comment"))
  implicit val defFormat: JsonFormat[DefImpl] =
    lazyFormat(jsonFormat(DefImpl, "name", "modifiers", "path", "returnValue", "typeParams", "paramLists", "comment"))
  implicit val objFormat: JsonFormat[ObjectImpl] =
    lazyFormat(jsonFormat(ObjectImpl, "name", "members", "modifiers", "path", "superTypes", "comment"))
  implicit val traitormat: JsonFormat[TraitImpl] =
    lazyFormat(jsonFormat(TraitImpl, "name", "members", "modifiers", "path", "typeParams", "superTypes", "comment"))
  implicit val cclassFormat: JsonFormat[CaseClassImpl] =
    lazyFormat(jsonFormat(CaseClassImpl, "name", "members", "modifiers", "path", "typeParams", "superTypes", "comment"))
  implicit val classFormat: JsonFormat[ClassImpl] =
    lazyFormat(jsonFormat(ClassImpl, "name", "members", "modifiers", "path", "typeParams", "superTypes", "comment"))
  implicit val packageFormat: JsonFormat[PackageImpl] =
    lazyFormat(jsonFormat(PackageImpl, "name", "members", "path", "comment"))
}

