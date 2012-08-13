var Comments;

(function($) {
    Comments = {
        disableAddButton : function() {
            $("#add-comment-button").addClass("disabled").attr("disabled",
                    "disabled");
        },
        enableAddButton : function() {
            $("#add-comment-button").removeClass("disabled").removeAttr(
                    "disabled");
        },
        reply : function(commentId) {
            this.setParent(commentId);
            this.placeFormTo("#comment_" + commentId);
        },
        clearTextarea : function() {
            $("#add-comment textarea").val("");
        },
        setParent : function(parentId) {
            $("#add-comment").find("form input[name='parentId']").val(parentId);
        },
        placeFormTo : function(containerId) {
            $("#add-comment").detach().appendTo(containerId);
            $("#add-comment").show();
        },
        toggleSubtree: function(commentId) {
            $("#comment_" + commentId + " .replies").toggle;
        }
    };
    $(function() {
        $("#add-comment-button").live("click", function() {
            Comments.clearTextarea();
            $("#add-comment").show();
            Comments.setParent(0);
            Comments.disableAddButton();
            Comments.placeFormTo("#add");
        });
        $("#add-comment .btn-cancel").live("click", function() {
            $("#add-comment").hide();
            $("#preview").remove();
            Comments.enableAddButton();
            Comments.placeFormTo("#add");
        });
        $(".toggleSubtree").live("click", function() {
            var foldMarker = "...";
            var comment = $(this).closest(".comment");
            var toggleLink = comment.find(".toggleSubtree");
            var linkText = toggleLink.val() || "";
            comment.find(".replies").toggle();
            
            if (linkText.indexOf(foldMarker) != -1) {
                toggleLink.val(linkText.replace(foldMarker, ""));
            } else {
                toggleLink.val(linkText + "...");
            }
        });
        $(".reply").live("click", function() {
            Comments.reply($(this).data("comment-id"));
        });
    });
})(jQuery);