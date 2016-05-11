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

});
