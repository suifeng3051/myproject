$(document).ready(function(){



    // DataTable关闭一些默认配置
    $.extend($.fn.dataTable.defaults, {
        searching: false,
        ordering: false
    });

    //  给API配置DIV绑定dataTable插件
    var carmenApiTable = $("#cacheTable").DataTable({
        paging: false,
        language: {"infoEmpty": "","info": ""}
        //info: false
        //info: false
    });

    // 全选
    $(".selectAll").on("click", function(){
        var value = $(this).is(':checked');
        updateCheckbox(value);
    });

    // 选中或更新所有checkbox
    function updateCheckbox(flag) {
        var elements = document.getElementsByClassName("select");
        for(var i=0; i<elements.length; i++) {
            elements[i].checked = flag;
        }
    }

    // 清除缓存
    $("#clearCache").on("click", function(d){
        var instances = getInstance();
        if("" == instances) {
            return;
        }
        $("#updateStatus").html("正在清除,请稍后..");
        $("#myCacheModal").modal("show");
        $.post("clearcache", {"names": instances}, function(d) {
            if("success" == d) { // 处理成功
                $("#updateStatus").html("恭喜，清除成功");
            } else { // 处理失败
                $("#updateStatus").html("Sorry,清除失败");
            }
            $("#myCacheModal").modal("hide");
            location.reload();
        });
    });

    // 预加载缓存
    $("#preloadCache").on("click", function(d){
        var instances = getInstance();
        if("" == instances) {
            return;
        }
        $("#updateStatus").html("正在预加载,请稍后..");
        $("#myCacheModal").modal("show");
        $.post("preloadcache", {"names": instances}, function(d) {
            if("success" == d) { // 处理成功
                $("#updateStatus").html("恭喜，预加载成功");
            } else { // 处理失败
                $("#updateStatus").html("Sorry,预加载失败");
            }
            $("#myCacheModal").modal("hide");
            location.reload();
        });
    });

    // 获取选中的实例名，并返回字符串
    function getInstance() {
        var selectedInstance = new Array();
        var elements = document.getElementsByClassName("select");
        for(var i=0; i<elements.length; i++) {
            if(elements[i].checked) {
                selectedInstance.push($(elements[i]).attr('cacheName'));
            }
        }
        var result = selectedInstance.join(",");
        return result;
    }
});