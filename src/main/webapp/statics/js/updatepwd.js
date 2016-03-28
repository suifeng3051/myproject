$(document).ready(function() {
    $("#updatepwd").click(function() {
        $("#message").html("");
        var user = new Object();
        user.password = $("#password").val();
        user.oldpwd = $("#oldpwd").val();
        user.repassword = $("#repassword").val();

        if(user.password == "" || user.oldpwd == "" || user.repassword == "") {
            $("#message").html("请填写密码信息");
            return;
        }
        if (user.password !=user.repassword){
                 $("#message").html("输入密码不一致");
                 return;
        }
        $.post("/user/dopwdUp", {"password": user.password,
           "oldpwd": user.oldpwd, "repassword": user.repassword}, function(r) {
            //console.log(r);
            $("#message").html(r.message);
        }, "json");
        
    });

     $('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
          var theme = $(e.target).html(); // 当前激活按钮显示的关键字
          var env = getEnv();
          var group = $("#currentGroup").val();
          if("Monitor" == theme){
             window.location.href = "monitor?env=" + env;
          } else if("Release" == theme) {
            window.location.href = "release?env=" + env;
          } else if("Config" == theme) {
            window.location.href = "createapi?env=" + env + "&group=" + group;
          } else if("LogView" == theme) {
            window.location.href = "pipelog?env=" + env;
          } else if("Users" == theme) {
            window.location.href = "user?env=" + env;
          } else if("Manual" == theme) {
            window.location.href = "manual?env=" + env;
          } else if("Cache" == theme) {
            window.location.href = "cachemanage?env=" + env;
          } else if("Instance" == theme) {
            window.location.href = "instancedetail?env=" + env;
          } else if("RecoverApi" == theme) {
            window.location.href = "recoverapi?env=" + env;
          }else if("safety"==theme){
             window.location.href = "updatepwd?env=" + env;
             }
        });
});

    function getEnv() {
        var env = $("#env").val();
        return env;
    }