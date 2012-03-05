function commentSubmit() {
    var form = $("#commentForm2")[0];

    if (!validComment("title", "名字")) {
        return false;
    }

    if (!validComment("email", "email")) {
        return false;
    }

    if (!validComment("content", "评论")) {
        return false;
    }

    form.submit();
}

function validComment(id, title) {
    var value = $.trim($("#" + id).val());
    if (!value || value.length == 0) {
        alert(title + "是必填的!")
        return false;
    }
    return true;
}
$(function () {
    if ($.trim(jsTarget).length > 0) {
        var $body = $("html,body");
        var $target = $("#" + jsTarget);
        $body.scrollTop($target.offset().top)
    }
})


/////post js
function submitPost(){

}
function previewPost(){

}
function draftPost(){

}
function validateInput(){

}