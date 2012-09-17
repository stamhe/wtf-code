var Highlighter;

(function($) {
    function countLines(elem) {
	var txt = elem.innerHTML;
	return txt.split("\n").length;
    }

    function renderNumber(n) {
	if (n < 10) return "0" + n + ".";
	else return n + ".";
    }

    Highlighter = {

	/**
	 * Generates line numbers for code tag.
	 *
	 * @param code - code tag to work with.
	 * @return you shouldn't care
	 */
	numberLines: function(code) {
	    // TODO(Roman): wtf code on wtf-code, fix that recursion!
	    var pre = code.parentNode;
	    var parent = pre.parentNode;
	    var count = countLines(code);

	    if ("td" == parent.tagName.toLowerCase()) {
		return "Already numbered";
	    }

	    var i = 0;
	    var ol = document.createElement("ol"), li;
	    var table = document.createElement("table");
	    var tr = document.createElement("tr");
	    var numTd = document.createElement("td");
	    var codeTd = document.createElement("td");
	    table.className = "code-table";
	    numTd.className = "num-column";
	    ol.className = "line-nums";
	    while (i++ < count) {
		li = document.createElement("li");
		li.appendChild(document.createTextNode(renderNumber(i)));
		ol.appendChild(li);
	    }
	    numTd.appendChild(ol);
	    tr.appendChild(numTd);
	    tr.appendChild(codeTd);
	    table.appendChild(tr);

	    parent.removeChild(pre);
	    codeTd.appendChild(pre);

	    parent.appendChild(table);
	    return "Ok";
	},

	/**
	 * Highlights all code blocks on the page.
	 *
	 * @return nothing
	 */
	highlightPage: function() {
	    $(document).ready(function() {
		$("pre code").each(function(i, e) {
		    hljs.highlightBlock(e);
		    Highlighter.numberLines(e);
		});
	    });
	},

	/**
	 * Highlights one specific code block.
	 *
	 * @param parentId - id of element to start code search from
	 * @return nothing
	 */
	highlightBlock: function(parentId) {
	    $('#' + parentId + " pre code").each(function(i, e) {
		hljs.highlightBlock(e);
		Highlighter.numberLines(e);
	    });
	}
    };
})(jQuery);
