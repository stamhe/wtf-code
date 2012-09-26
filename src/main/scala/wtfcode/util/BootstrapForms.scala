package wtfcode.util

import xml._

/**
 * Set of combinators to render bootstrap forms.
 */
object BootstrapForms {

  def controlGroup(label: String, controls: NodeSeq): Elem =
    <div class="control-group">
      <label class="control-label">{label}</label>
      <div class="controls">{controls}</div>
    </div>

  def controlGroup(controls: NodeSeq): Elem =
    <div class="control-group">
      <div class="controls">{controls}</div>
    </div>

  def control(t: String, name: String, value: String): Elem =
    <input type={t} name={name}>{value}</input>

  def formActions(inner: NodeSeq): Elem = <div class="form-actions">{inner}</div>

  def submit(cls: String, inner: NodeSeq): Elem = <button type="submit" class={cls}>{inner}</button>

}
