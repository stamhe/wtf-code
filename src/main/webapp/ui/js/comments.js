var Comments;

(function($) {
     Comments = {
	 disableAddButton: function() {
	     $("#add-comment-button").addClass("disabled").attr("disabled", "disabled");	     
	 },
	 enableAddButton: function() {
             $("#add-comment-button").removeClass("disabled").removeAttr("disabled");
         }
     };
     $(function() {
         $("#add-comment-button").live("click", function() {
             $("#add-comment textarea").val("");
             $("#add-comment").show();
             Comments.disableAddButton();
         });
         $("#add-comment .btn-cancel").live("click", function() {
             $("#add-comment").hide();
             $("#preview").remove();
             Comments.enableAddButton();
	 });
     });
})(jQuery);