/**
 * Highlights all code blocks on the page.
 */
function wtfCode_highlightPage() {
  jQuery(document).ready(function() {
    $("pre code").each(function(i, e) { hljs.highlightBlock(e); });
  });
}
/**
 * Highlights one specific code block.
 */
function wtfCode_highlightBlock(parentId) {
  jQuery('#' + parentId + " pre code").each(function(i, e) {
    hljs.highlightBlock(e);
  });
}