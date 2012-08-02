function wtfCode_disableAddCommentButton() {
    $("#add-comment-button").addClass("disabled").attr("disabled", "disabled");
}
function wtfCode_enableAddCommentButton() {
    $("#add-comment-button").removeClass("disabled").removeAttr("disabled");
}
$("#add-comment-button").live("click", function() {
    $("#add-comment textarea").val("");
    $("#add-comment").show();
    wtfCode_disableAddCommentButton();
});
$("#add-comment .btn-cancel").live("click", function() {
    $("#add-comment").hide();
    $("#preview").remove();
    wtfCode_enableAddCommentButton();
});