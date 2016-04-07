$(document).ready(function(){

var path = location.pathname.substr(1);
switch(path){
    case 'apilist': addClass('Home'); break;
    case 'createapi': addClass('Config'); break;
    case 'release': addClass('Release'); break;
    case 'monitor': path = 'Monitor';break;
    case 'manual': path = 'Manual';break;
    case 'cachemanage': path = 'Cache';break;
    case 'instancedetail': path = 'Instance';break;
    case 'pipelog': path = 'LogView';break;
    case 'user': path = 'users';break;
    case 'recoverapi': path = 'RecoverApi';break;
    case 'oauthclient': path = 'Client';break;
    case 'updatepwd': path = 'Safety';break;
    default : break;
}
 // 如果是下拉菜单项为当前页，则修改下拉菜单文字，否则默认显示 Console
if($('#myTitle li:last-child').hasClass('active')){
    $("#consoleText").html(path);
}

function addClass(el){
    $('#myTitle a.titles').each(function(){
        if($(this).text() ===  el){
            $(this).parents('li[role="presentation"]').addClass('active').siblings().removeClass('active');
        }
    });
}
    // 导航跳转事件
    // 当前激活按钮绑定事件
    $('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
        var theme = $(e.target).html(); // 当前激活按钮显示的关键字
        var env = getEnv();
        var group = $("#currentGroup").val();
        if ("Monitor" == theme) {
            window.location.href = "monitor?env=" + env;
        } else if ("Release" == theme) {
            window.location.href = "release?env=" + env;
        } else if ("Config" == theme) {
            window.location.href = "createapi?env=" + env + "&group=" + group;
        } else if ("LogView" == theme) {
            window.location.href = "pipelog?env=" + env;
        } else if ("Home" == theme) {
             window.location.href = "apilist?env=" + env;
         } else if ("Users" == theme) {
            window.location.href = "user?env=" + env;
        } else if ("Manual" == theme) {
            window.location.href = "manual?env=" + env;
        } else if ("Cache" == theme) {
            window.location.href = "cachemanage?env=" + env;
        } else if ("Instance" == theme) {
            window.location.href = "instancedetail?env=" + env;
        } else if ("RecoverApi" == theme) {
            window.location.href = "recoverapi?env=" + env;
        } else if ("Client" == theme) {
             window.location.href = "oauthclient?env=" + env;
        }else if("safety"==theme){
         window.location.href = "updatepwd?env=" + env;
         }
    });
    // 跳转到帮助页面
    $(".help").on("click", function(e){
        e.preventDefault();
        var env = getEnv();
        location.href = "manual?env=" + env;
    });
    $('#safety-user-btn').click(function(e){
        e.preventDefault();
        var env = getEnv();
        location.href="updatepwd?env=" + env;
    });

    $(".console").on("click", function(e) {
        e.preventDefault();
        var content = $(this).html();
        $("#consoleText").html(content);
    });

    function getEnv() {
        var env = $("input[name='env']:checked").val();
        return env;
    }



});