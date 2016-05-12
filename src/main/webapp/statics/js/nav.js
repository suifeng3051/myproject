$(document).ready(function(){

    var env =1;
    var envName="dev";
    var isAdmin = getCookie("isAdmin");

    var url_now = window.location.href;
    var home_flag = "false";

    if(window.location.href.indexOf("apilist")>0){
        home_flag ="true";
    }


    if(getCookie("env")!=''||getCookie("env")!=null){
        env =parseInt(getCookie("env"));
    }


    switch(env)
    {
        case 1:
            envName="dev";
            break;
        case 2:
            envName="test";
            break;
        case 3:
            envName="product";
            break;
    }

    console.log("nav_env:"+env);


    if(isAdmin=="true"&&home_flag=="true"){

        $("#radioDiv").html('<label><input class="envRadio" type="radio" name="env"  value="1">'+
            '<span class="text-primary">dev&nbsp;&nbsp;</span></label>'+
            '<label><input class="envRadio" type="radio" name="env"  value="2">'+
            '<span class="text-primary">test&nbsp;&nbsp;</span></label>'+
            '<label><input class="envRadio" type="radio" name="env"  value="3">'+
            '<span class="text-primary">product&nbsp;&nbsp;</span></label>');

        $(".envRadio[value="+env+"]").attr("checked","checked").parent().children('span').attr("class","text-warning");

    }else if(isAdmin=="true"&&home_flag=="false"){
        $("#radioDiv").html('<label><input class="envRadio" type="radio" name="env"  value="'+env+'" checked>'+
            '<span class="text-warning">'+envName+'</span></label>');
    }else{
        $("#radioDiv").html('<label><input class="envRadio" type="radio" name="env"  value="'+env+'" checked>'+
            '<span class="text-warning">'+envName+'</span></label>').hide();

    }






/*    $("#goHome").click(function(){

        var env =getCookie("env");

        if(env==''||env==null){
            env =1;
        }

        window.location.href = "apilist";

    });*/




});

function getCookie(c_name) {
    if (document.cookie.length > 0) {
        var c_start = document.cookie.indexOf(c_name + "=");
        if (c_start != -1) {
            c_start = c_start + c_name.length + 1;
            var c_end = document.cookie.indexOf(";", c_start);
            if (c_end == -1) c_end = document.cookie.length;
            return unescape(document.cookie.substring(c_start, c_end));
        }
    }
    return "";
}
