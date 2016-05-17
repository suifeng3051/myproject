$(document).ready(function(){

    var env =1;
    var envName="dev";
    var role = getCookie("gateway_role");
    var username = getCookie("gateway_username");
    $("#nav_user").html(username);
    var home_flag = "false";

    if(window.location.href.indexOf("apilist")>0){
        home_flag ="true";
    }

    if(getCookie("gateway_env")!=''||getCookie("gateway_env")!=null){
        env =parseInt(getCookie("gateway_env"));
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

   // console.log("envName:"+envName);

    if(role=="1"&&home_flag=="true"){

        $("#radioDiv").html('<label><input class="envRadio" type="radio" name="env"  value="1">'+
            '<span class="text-primary">dev&nbsp;&nbsp;</span></label>'+
            '<label><input class="envRadio" type="radio" name="env"  value="2">'+
            '<span class="text-primary">test&nbsp;&nbsp;</span></label>'+
            '<label><input class="envRadio" type="radio" name="env"  value="3">'+
            '<span class="text-primary">product&nbsp;&nbsp;</span></label>');

        $(".envRadio[value="+env+"]").attr("checked","checked").parent().children('span').attr("class","text-warning");

    }else if(role=="1"&&home_flag=="false"){
        $("#radioDiv").html('<label><input class="envRadio" type="radio" name="env"  value="'+env+'" checked>'+
            '<span class="text-warning">'+envName+'</span></label>');
    }else{
        $("#radioDiv").html('<label><input class="envRadio" type="radio" name="env"  value="'+env+'" checked>'+
            '<span class="text-warning">'+envName+'</span></label>').hide();
    }


    if(role=="1"){

        $("#myTitle").html('<li role="presentation" ><a href="#" role="tab" data-toggle="tab" class="titles">Home</a></li>' +
            '<li role="presentation" ><a href="#" role="tab" data-toggle="tab" class="titles">Config</a></li>' +
            '<li role="presentation" ><a href="#" role="tab" data-toggle="tab" class="titles">Release</a></li>' +
            '<li role="presentation" class="dropdown active">' +
            '<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="tab" aria-haspopup="true" aria-expanded="false">' +
            '<span id="consoleText">Console</span> <span class="caret"></span></a>' +
            '<ul class="dropdown-menu" >' +
            '<li><a href="#" role="tab" data-toggle="tab" class="titles console ">Monitor</a></li>' +
            '<li><a href="#" role="tab" data-toggle="tab" class="titles console">Cache</a></li>' +
            '<li><a href="#" role="tab" data-toggle="tab" class="titles console">Instance</a></li>' +
            '<li><a href="#" role="tab" data-toggle="tab" class="titles console">Recover</a></li>' +
            '<li><a href="#" role="tab" data-toggle="tab" class="titles console">User</a></li>' +
            '<li><a href="#" role="tab" data-toggle="tab" class="titles console">Client</a></li></ul>');

    }else if(role=="2"&&env==3){

        $("#myTitle").html('<li role="presentation" ><a href="#" role="tab" data-toggle="tab" class="titles">Home</a></li>');

    }else if(role=="2"&&env!=3){

        $("#myTitle").html('<li role="presentation" ><a href="#" role="tab" data-toggle="tab" class="titles">Home</a></li>' +
            '<li role="presentation" ><a href="#" role="tab" data-toggle="tab" class="titles">Config</a></li>' +
            '<li role="presentation" ><a href="#" role="tab" data-toggle="tab" class="titles">Release</a></li>' +
            '<li role="presentation" class="dropdown active">' +
            '<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="tab" aria-haspopup="true" aria-expanded="false">' +
            '<span id="consoleText">Console</span> <span class="caret"></span></a>' +
            '<ul class="dropdown-menu" >' +
            '<li><a href="#" role="tab" data-toggle="tab" class="titles console ">Monitor</a></li>' +
            '<li><a href="#" role="tab" data-toggle="tab" class="titles console">Cache</a></li>' +
            '<li><a href="#" role="tab" data-toggle="tab" class="titles console">Instance</a></li></ul>');

    }



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
