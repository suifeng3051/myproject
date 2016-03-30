$(document).ready(function(){


    // 选择所有的单选框
    $("#selectAll").on("click", function() {
        var flag = $(this).attr("flag");
        if(1 == flag) { // 全选
            updateCheckbox(true);
            $(this).attr("flag", "0");
            $(this).html("全不选");
        } else { // 全不选
            updateCheckbox(false);
            $(this).attr("flag", "1");
            $(this).html("全选");
        }
    });

    function updateCheckbox(flag) {
        var elements = document.getElementsByClassName("select");
        for(var i=0; i<elements.length; i++) {
            elements[i].checked = flag;
        }
    }

    $("#recoverDeleteApi").on("click", function(){
        var ids = getReleasingApis();
        if("" == ids) {
            return;
        }
        $.post("executeapis", {"ids":ids}, function(d){
            console.log("d:" + d);
            location.reload();
        });
    });

    // 获取选中API的名字列表
    function getReleasingApis() {
        var elements = document.getElementsByClassName("select");
        var apiList = new Array();
        for(var i=0; i<elements.length; i++) {
            if(elements[i].checked) {
                var api=$(elements[i]).attr("apiid");
                apiList.push(api);
            }
        }
        return apiList.toString();
    }
});