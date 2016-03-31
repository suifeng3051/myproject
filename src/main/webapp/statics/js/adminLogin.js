$(document).ready(function() {
    $("#login").click(function() {
        $("#message").html("");
        var user = new Object();
        user.username = $("#username").val();
        user.password = $("#password").val();

        if(user.username == "" || user.password == "") {
            $("#message").html("账号、密码和验证码不能为空");
            return;
        }

//        $.post("/oauth/login/authorize", {"username": user.username,
//            "password": user.password, "captcha": user.captcha}, function(r) {
//            console.log(r);
//            if(r == false) {
//                $("#message").html("账号、密码或验证码错误");
//            } else {
//                var c = window.location.href;
//                var p = c.split("?")[1];
//                window.location.href = "/oauth/team?" + p;
//            }
//        }, "json");
        
        $.post("/user/doLogin", {"username": user.username,
           "password": user.password, "captcha": user.captcha}, function(r) {
            console.log(r);
            if(r.result == false) {
                $("#message").html("用户名或密码错误");
                if(r.to != "") {
                    window.location.href = r.to;
                }
            } else {
                window.location.href = r.to;
            }
        }, "json");
        
    });



});
    window.document.onkeydown = eventForLogin;
    // 按下回车键
    function eventForLogin(e){
        e = e ? e : window.event;
        if (e.keyCode==13 && document.activeElement.id=='password'){  //回车键的键值为13
            document.getElementById("login").click(); //调用登录按钮的登录事件
        }
    }