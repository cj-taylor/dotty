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
  implicit val unsLinkFormat = lazyFormat(jsonFormat(UnsetLink, "title", "query"))

  implicit object MaterializableLinkFormat extends RootJsonFormat[MaterializableLink] {
    def write(obj: MaterializableLink) = obj match {
      case obj: MaterializedLink => obj.toJson
      case obj: UnsetLink => obj.toJson
    }
    def read(json: JsValue) = ??? // The json serialization is supposed to be one way
  }

  implicit object EntityJsonFormat extends RootJsonFormat[Entity] {
    def write(e: Entity) = e match {
      case e: PackageImpl   => e.toJson
      case e: ClassImpl     => e.toJson
      case e: CaseClassImpl => e.toJson
      case e: TraitImpl     => e.toJson
      case e: ObjectImpl    => e.toJson
      case e: DefImpl       => e.toJson
      case e: ValImpl       => e.toJson
    }
    def read(json: JsValue) = ??? // The json serialization is supposed to be one way
  }

  implicit object PackageFormat extends RootJsonFormat[Package] {
    def write(obj: Package) = obj match { case obj: PackageImpl => obj.toJson }
    def read(json: JsValue) = ??? // The json serialization is supposed to be one way
  }

  implicit val valFormat: JsonFormat[ValImpl] = lazyFormat(jsonFormat(ValImpl, "name", "modifiers", "path", "returnValue", "comment"))
  implicit val defFormat: JsonFormat[DefImpl] = lazyFormat(jsonFormat(DefImpl, "name", "modifiers", "path", "returnValue", "comment"))
  implicit val objFormat: JsonFormat[ObjectImpl] = lazyFormat(jsonFormat(ObjectImpl, "name", "members", "modifiers", "path", "comment"))
  implicit val traitormat: JsonFormat[TraitImpl] = lazyFormat(jsonFormat(TraitImpl, "name", "members", "modifiers", "path", "comment"))
  implicit val cclassFormat: JsonFormat[CaseClassImpl] = lazyFormat(jsonFormat(CaseClassImpl, "name", "members", "modifiers", "path", "comment"))
  implicit val classFormat: JsonFormat[ClassImpl] = lazyFormat(jsonFormat(ClassImpl, "name", "members", "modifiers", "path", "comment"))
  implicit val packageFormat: JsonFormat[PackageImpl] = lazyFormat(jsonFormat(PackageImpl, "name", "members", "path", "comment"))
}

