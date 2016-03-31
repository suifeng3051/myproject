$(document).ready(function(){


    $("#apiResourceConfig").validate();
    $("#apiResourceGroupConfig").validate();


    $("#newCreateTask").click("click", function () {
        var group = $("#currentGroup").val();
        window.location.href = "createapi?env=" + getEnv() + "&group=" + group;
    });


    $('[data-toggle="tooltip"]').tooltip(); // 绑定工具提示js插件




    // 读取 oauthClient 列表
    $.post('getOauthClient', function(data){

        if(data.status === 'success'){
            var html = '';
            for(var i=0; i<data.size; i++){
                html += '<tr>'+
                     '<td>'+data.allOauthClient[i].clientName+'</td>'+
                     '<td>'+data.allOauthClient[i].redirectUri+'</td>'+
                     '<td>'+data.allOauthClient[i].grantTypes+'</td>'+
                     '<td>'+data.allOauthClient[i].clientNum+'</td>'+
                     '<td>'+data.allOauthClient[i].defaultScope+'</td>'+
                     '<td>'+
                         '<a data-original-title="编辑" class="needTip updateCurrentRow" href="#" OAuthClientId="'+data.allOauthClient[i].id+'" data-toggle="tooltip" data-placement="top" title="" >'+
                              '<span version="1.0" class="glyphicon glyphicon-edit editIcon"></span>'+
                          '</a>'+
                         '<a href="#" class="needTip deleteCurrentRow" OAuthClientId="'+data.allOauthClient[i].id+'"  data-toggle="tooltip" data-placement="top" title="删除行" style="margin-left:8px;">'+
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
                        $.post('deleteOauthClient', {id: OAuthClientId}, function(data){
                            if(data === 'success'){
                                window.location.reload();
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

                $.post('getOauthClientById',{ id:OAuthClientId }, function(OAuthClientData){

                    // 把数据塞入更新对话框 data
                    if(OAuthClientData.status === 'success'){
                        console.log(OAuthClientData); // 所保存的数据

                        // 自动选中已经保存的 default_scope 选项
                        $.post('getGroupAlias', function(aliasesData){
                            if(aliasesData.status === 'success'){
                                console.log(aliasesData);

                            var scopeArr = OAuthClientData.oauthClient.defaultScope.split(' ');


                                var len = aliasesData.aliases.length;
                                var html = '';
                                for(var i=0; i<len; i++){
                                    var currentAlias = aliasesData.aliases[i];
                                    var check = in_array(currentAlias,scopeArr) ?  'checked' : '';

                                    html += '<label class="col-sm-3"><input type="checkbox" name="default_scope" '+check+' value="'+currentAlias+'" />'+currentAlias+'</label>';
                                }
                                $('#defaultScopeArea').html(html);
                            }
                        });

                        // 自动选中已经保存的 GrantTypes
                        var grantTypesArr = ['authorization_code','refresh_token','implicit','client_credentials','password'];
                        var grantTypeExists = OAuthClientData.oauthClient.grantTypes.split(' ');
                        var html = '';
                        for(var i=0;i<grantTypesArr.length; i++){
                            var currentGrantType = grantTypesArr[i];
                            var check = in_array(currentGrantType,grantTypeExists) ?  'checked' : '';
                            html += '<label class="col-sm-4"><input type="checkbox" name="grant_types" '+ check+' value="'+currentGrantType+'" />'+currentGrantType+'</label>';

                        }
                        $('#grantTypesArea').html(html);

                        // 自动填入Cient Name、 Redirect Uri、 Client Num
                        $('input[name="client_name"]').val(OAuthClientData.oauthClient.clientName);
                        $('input[name="redirect_uri"]').val(OAuthClientData.oauthClient.redirectUri);
                        $('input[name="client_num"]').val(OAuthClientData.oauthClient.clientNum);



                        $("#OAuthClientModal").modal("show");
                    } else {
                        alert('fail');
                    }
                });
            });

        }
    });





    // OAuthClient 对话框
    $('#addOAuthClientBtn').click(function(){
        $('#OAuthClientConfig')[0].reset();
        $('#OAuthClientInfo2').hide();
        $('#checkOAuthBtn').show();
        $.post('getGroupAlias', function(data){
            if(data.status === 'success'){
                var json = data.aliases;
                var html = '';
                for(var i=0; i<json.length; i++){
                    html += '<label class="col-sm-3"><input type="checkbox" name="default_scope" value="'+json[i]+'" />'+json[i]+'</label>';
                }
                $('#defaultScopeArea').html(html);
                $("#OAuthClientModal").modal("show");
            }
        });
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
            var url = 'updateOauthClient';
        } else {
            var url = 'addOauthClient';
        }

        $.post(url, {'oauthclient': formContent}, function(data){
            if ( data.indexOf('fail') != -1 ) {
                $("#OAuthClientInfo2").html("<p>失败</p>"+data)
                .css("display", "block")
                .removeClass("alert-success")
                .addClass("alert-danger");

            } else {
                $("#OAuthClientInfo2").html("<p>成功</p>")
                .css("display", "block")
                .removeClass("alert-danger")
                .addClass("alert-success");

                setTimeout(function(){
                    window.location.reload();
                }, 1000);
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


});