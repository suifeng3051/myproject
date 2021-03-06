$(document).ready(function(){


    $("#apiResourceConfig").validate();
    $("#apiResourceGroupConfig").validate();


    $("#newCreateTask").click("click", function () {
        var group = $("#currentGroup").val();
        window.location.href = "createapi?env=" + getEnv() + "&group=" + group;
    });


    $('[data-toggle="tooltip"]').tooltip(); // 绑定工具提示js插件

    // 读取 oauthClient 列表
    $.post('get/client', function(jsondata){

        if(jsondata.code == 0){
            var html = '';
            var data = jsondata.data;
            for(var i=0; i<data.size; i++){
                html += '<tr>'+
                    '<td>'+data.allClient[i].clientName+'</td>'+
                    '<td>'+data.allClient[i].redirectUri+'</td>'+
                    '<td>'+data.allClient[i].grantTypes+'</td>'+
                    '<td>'+data.allClient[i].clientNum+'</td>'+
                    '<td>'+data.allClient[i].defaultScope+'</td>'+
                    '<td>'+
                    '<a data-original-title="编辑" class="needTip updateCurrentRow" href="#" OAuthClientId="'+data.allClient[i].id+'" data-toggle="tooltip" data-placement="top" title="" >'+
                    '<span version="1.0" class="glyphicon glyphicon-edit editIcon"></span>'+
                    '</a>'+
                    '<a href="#" class="needTip deleteCurrentRow" OAuthClientId="'+data.allClient[i].id+'"  data-toggle="tooltip" data-placement="top" title="删除行" style="margin-left:8px;">'+
                    '<span class="glyphicon glyphicon-trash" aria-hidden="true"></span>'+
                    '</a>'+
                    '</td>'+
                    '</tr>';
            }



            $('#OAuthClientConfigBody').html(html);


            // 删除 oauthClient
            $('.deleteCurrentRow').click(function(e){

                //        e.preventDefault();
                var OAuthClientId = $(this).attr('OAuthClientId');
                var OAuthName = $(this).parent().parent().find('td').eq(0).text();

                if(confirm('确定删除 '+OAuthName+ ' 吗?')){
                    $.post('delete/client', {id: OAuthClientId}, function(data){
                        if(data.code == 0){
                            window.location.reload();
                        }
                        else
                        {
                            alert(data.message);
                        }
                    });
                }
            });



            // 更新 oauthClient
            $('.updateCurrentRow').click(function(){
                $('#OAuthClientConfig')[0].reset();
                $('#OAuthClientInfo2').hide();

                $('#checkOAuthBtn').hide();


                var OAuthClientId = $(this).attr('OAuthClientId');

                $('#sureAddOAuthClient').attr('action', OAuthClientId);

                $.post('getClientById',{ id:OAuthClientId }, function(clientData){

                    // 把数据塞入更新对话框 data
                    if(clientData.code == 0){
                        console.log(clientData); // 所保存的数据

                        // 自动选中已经保存的 default_scope 选项
                        $.post('getGroupTree', function(allGroupData){
                            if(allGroupData.code == 0){
                                console.log(allGroupData);
                                var html = '';
                                var scopeArr = clientData.data.defaultScope.split(' ');

                                var groupTree = allGroupData.data;
                                console.log(groupTree);
                                console.log(scopeArr);

                                html = foreachGroupTree(groupTree,html,true,scopeArr);

                                $('#defaultScopeArea').html(html);
                            }
                        });

                        // 自动选中已经保存的 GrantTypes
                        var grantTypesArr = ['authorization_code','refresh_token','implicit','client_credentials','password'];
                        var grantTypeExists = clientData.data.grantTypes.split(' ');
                        var html = '';
                        for(var i=0;i<grantTypesArr.length; i++){
                            var currentGrantType = grantTypesArr[i];
                            var check = in_array(currentGrantType,grantTypeExists) ?  'checked' : '';
                            html += '<label class="col-sm-4"><input type="checkbox" name="grant_types" '+ check+' value="'+currentGrantType+'" />'+currentGrantType+'</label>';

                        }
                        $('#grantTypesArea').html(html);

                        // 自动填入Cient Name、 Redirect Uri、 Client Num
                        $('input[name="client_name"]').val(clientData.data.clientName);
                        $('input[name="redirect_uri"]').val(clientData.data.redirectUri);
                        $('input[name="client_num"]').val(clientData.data.clientNum);



                        $("#OAuthClientModal").modal("show");
                    } else {
                        alert(clientData.message);
                    }
                });
            });

        }
        else
        {
            alert(jsondata.message);
        }
    });





    // OAuthClient 对话框
    $('#addOAuthClientBtn').click(function(){
         $('#sureAddOAuthClient').attr('action','');
         $('#OAuthClientConfig')[0].reset();
        $('#OAuthClientConfig input').attr("checked",false);

        $('#OAuthClientInfo2').hide();
        $('#checkOAuthBtn').show();
         $.post('getGroupTree', function(data){
            if(data.code == 0){
                var html = '';
                var groupTree = data.data;

                html = foreachGroupTree(groupTree,html,false,null);
                 $('#defaultScopeArea').html(html);
                $("#OAuthClientModal").modal("show");
            }
        });

        var formContent = $("#OAuthClientConfig").serialize();
     });


    $('#checkOAuthBtn').click(function(){
        var name = $.trim($('input[name="client_name"]').val());
        $.post('checkOauthClient', {name: name}, function(data){
            if(data != 'success'){
                $("#OAuthClientInfo2").html("<p>呃~ Client 已存在！</p>")
                    .css("display", "block")
                    .removeClass("alert-success")
                    .addClass("alert-danger");
            } else {
                $("#OAuthClientInfo2").html("<p>耶！Client 可用！</p>")
                    .css("display", "block")
                    .removeClass("alert-danger")
                    .addClass("alert-success");
            }
        });
    });

    $('#sureAddOAuthClient').click(function(){
        var formContent = $("#OAuthClientConfig").serialize();
         if($(this).attr('action')){
            formContent += '&id='+ $(this).attr('action');
            var url = 'update/client';
        } else {
            var url = 'add/client';
        }

         $.post(url, {'oauthclient': formContent}, function(data){
            if(data.code == 0)
            {
                $("#OAuthClientInfo2").html("<p>成功</p>")
                    .css("display", "block")
                    .removeClass("alert-danger")
                    .addClass("alert-success");

                setTimeout(function(){
                    window.location.reload();
                }, 1000);
            }
            else
            {
                $("#OAuthClientInfo2").html(data.message)
                    .css("display", "block")
                    .removeClass("alert-success")
                    .addClass("alert-danger");
            }
        }, 'json'); 
    });

    function in_array(search,array){
        for(var i in array){
            if(array[i]==search){
                return true;
            }
        }
        return false;
    }




	$('#defaultScopeArea label').click(function(){

		// 子对象被选中父级对象自动选中
		//$(this).parent().siblings('label').children('input').prop('checked', true);
        //$(this).parent().parent().siblings('label').children('input').prop('checked', true);
        //$(this).parent().parent().parent().siblings('label').children('input').prop('checked', true);

		// 父级对象取消选中，子对象全部取消选中
		//$(this).siblings('.level-box').find('[type="checkbox"]').prop('checked', false);

		//父级对象选中，子对象全部选中
		// if($(this).find('input').is(':checked'))
		// 	$(this).siblings('.level-box').find('[type="checkbox"]').prop('checked', true);


	});

    function foreachGroupTree(groupTree,html,flag_check,scopeArr){

        if(groupTree!=null){

            if(flag_check){
                var check = in_array(groupTree.alias, scopeArr) ? 'checked' : '';
/*                console.log(groupTree.alias);
                console.log(check);*/
                if(check){
                    html += '<div class="level-box"><label> <input name="default_scope" value="'+groupTree.alias+'" type="checkbox" checked="checked" />'+groupTree.name+'</label>';
                }else{
                    html += '<div class="level-box"><label> <input name="default_scope" value="'+groupTree.alias+'" type="checkbox"   />'+groupTree.name+'</label>';
                }
            }else{
                html += '<div class="level-box"><label> <input name="default_scope" value="'+groupTree.alias+'" type="checkbox"   />'+groupTree.name+'</label>';
            }

            var children = groupTree.children;
            //第一次取下级，即取所有的group节点
            if(children!=null&&children.length>0) {
                $.each(children, function (i, item) {

                    html  = foreachGroupTree(item,html,flag_check,scopeArr);

                });// end foreach
            }//end if
            html += '</div>'
        }
        return html;

    }



});