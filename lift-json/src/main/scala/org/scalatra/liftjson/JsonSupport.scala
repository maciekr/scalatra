package org.scalatra
package liftjson

import net.liftweb.json._
import scala.io.Codec.UTF8

@deprecated("Use LiftJsonSupport instead", "2.1.0")
trait JsonSupport extends LiftJsonOutput

private[liftjson] trait LiftJsonOutput extends ApiFormats {

  /**
   * If a request is made with a parameter in jsonpCallbackParameterNames it will
   * be assumed that it is a JSONP request and the json will be returned as the
   * argument to a function with the name specified in the corresponding parameter.
   *
   * By default no parameterNames will be checked
   */
  def jsonpCallbackParameterNames:  Iterable[String] = Nil

  override protected def contentTypeInferrer = ({
    case _ : JValue => "application/json; charset=utf-8"
  }: ContentTypeInferrer) orElse super.contentTypeInferrer


  override protected def renderPipeline = ({
    case jv : JValue =>
      val jsonString = compact(render(jv))

      val jsonWithCallback = for {
        paramName <- jsonpCallbackParameterNames
        callback <- params.get(paramName)
      } yield "%s(%s);" format (callback, jsonString)

      (jsonWithCallback.headOption.getOrElse(jsonString)).getBytes(UTF8)
  }: RenderPipeline) orElse super.renderPipeline
}
