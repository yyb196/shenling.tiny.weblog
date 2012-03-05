function valid(id, title) {
    var value = $.trim($("#" + id).val());
    if (!value || value.length == 0) {
        alert(title + "是必填的!")
        return false;
    }
    return true;
}

function validTextArea(id, title) {
    var value = $.trim($("#" + id).text());
    if (!value || value.length == 0) {
        alert(title + "是必填的!")
        return false;
    }
    return true;
}

$(function () {
    if (isModify) {
        //已发布文章的修改不能再存草稿了
        $("#draftSave").hide();
        $("#link").hide().append($("<div>" + $("#link").val() + "</div>"));
    }
})


/////post js
function submitPost() {
    if (!validateInput()) {
        return false;
    }
    var url = "/index.htm?action=ArticleAction&event_submit_do_post=true"
    $("#postForm").attr({"action":url, "target":"_self"}).submit();
    return false;
}
function previewPost() {
    if (!validateInput()) {
        return false;
    }
    var url = "/index.htm?action=ArticleAction&event_submit_do_preview=true"
    $("#postForm").attr({"action":url, "target":"_blank"}).submit();
    return false;
}
function draftPost() {
    if (!validateInput()) {
        return false;
    }
    var url = "/index.htm?action=ArticleAction&event_submit_do_draft=true"
    $("#postForm").attr({"action":url, "target":"_self"}).submit();
    return false;
}
function validateInput() {
    if (!valid("title", "标题")) {
        return false;
    }
    if (!valid("link", "链接")) {
        return false;
    }
    if (!valid("tags", "标签")) {
        return false;
    }
//    if (!validTextArea("content", "正文")) {
//        return false;
//    }
    return true;
}