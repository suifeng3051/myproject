$(document).ready(function(){


    // DataTable关闭一些默认配置
    $.extend($.fn.dataTable.defaults, {
        searching: false,
        ordering: false
    });

    //  给API配置DIV绑定dataTable插件
    var instanceDetailTable = $("#instanceTable").DataTable({
        paging: false,
        language: {"infoEmpty": "","info": ""}
        //info: false
        //info: false
    });
});