package wtfcode.util

import xml._

/**
 * Set of combinators to render bootstrap forms.
 */
object BootstrapForms {

  object ControlGroup {
    def apply(label: String, controls: NodeSeq): Elem =
      <div class="control-group">
        <label class="control-label">{label}</label>
        <div class="controls">{controls}</div>
      </div>

    def apply(controls: NodeSeq): Elem =
      <div class="control-group">
        <div class="controls">{controls}</div>
      </div>
  }

  object Control {
    def apply(t: String, name: String, value: String): Elem =
      <input type={t} name={name}>{value}</input>
  }

  object FormActions {
    def apply(inner: NodeSeq): Elem = <div class="form-actions">{inner}</div>
  }

  object Submit {
    def apply(cls: String, inner: NodeSeq): Elem = <button type="submit" class={cls}>{inner}</button>
  }
}
