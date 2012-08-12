var Comments;

(function($) {
     Comments = {
	 disableAddButton: function() {
	     $("#add-comment-button").addClass("disabled").attr("disabled", "disabled");	     
	 },
	 enableAddButton: function() {
             $("#add-comment-button").removeClass("disabled").removeAttr("disabled");
         },
	 reply: function(commentId) {
	     this.setParent(commentId);
	     this.placeFormTo("#comment_" + commentId);
	 },
	 clearTextarea: function() {
             $("#add-comment textarea").val("");	     
	 },
	 setParent: function(parentId) {
	     $("#add-comment").find("form input[name='parentId']").val(parentId);
	 },
	 placeFormTo: function(containerId) {
	     $("#add-comment").detach().appendTo(containerId);
	     $("#add-comment").show();
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
	 $(".reply").live("click", function() {
	     Comments.reply($(this).data("comment-id"));
	 });
     });
})(jQuery);