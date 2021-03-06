$(document).ready(function(){

        $('body > .container-fluid ').hide(); // 先隐藏所有页面内容，等页面文档准备好再显示


    // 获取第 2 步的多选数据
    $.get('getServeInner', function(data){

        if(data.code == 0){ // 成功
            var len = data.data.length;
            var str = '';
            for(name in data.data){
                str += '<div class="checkbox col-sm-4">'+
                    '<label><input type="checkbox" value="'+ data.data[name] +'" name="innerParams" >'+name+'</label>'+
                    '</div>';
            }

            $('#serviceParamList').html(str); // 写入之后再根据，注入的id为innerParams的input的值

            setTimeout(function(){
                var defaultInnerParams = $('#innerParams').val().split(' ');

                $('#serviceParamList input[type="checkbox"]').each(function(){

                    if($.inArray($(this).val(), defaultInnerParams) != -1 ){
                        $(this).prop('checked', true);
                    }
                });


                $('body > .container-fluid ').show();
                isReady();

            }, 300);
        }
    });

    function isReady(){




        if($('#edit').val() == 1){
            $('#json-input').trigger('blur');
        }

        jsonInputView( 'json-input', 'json-input-jsonshow' );
        jsonInputView('resultDemo', 'resultDemo-jsonshow' );






        var currentStep = 1;
        var totalStep = $('#apiconfig-box > div').length;

        $('.nextStep').click(function(){

    //        if($('#apiconfig-box > div:eq('+(currentStep-1)+') .form-control.error').length > 0) return;

            switch(currentStep) {
                case 1:  // 检测namespace是否存在
                    if(
                        ($('#edit').val() == 1)  // 正在修改API 不进行检测
                        ||
                        ($('.hasApi').attr('data-exists') == 'no')  // 没有冲突，是新建
                        ||
                        (($('.hasApi').attr('data-exists') == 'yes') && ($('#edit').val() == 1)) // 已经存在并正在修改
                    ){  // namespace 检测不存在时（没有冲突）

                        // 跳转到下一步


                    }else {  // 未检测
                        $('.hasApi').trigger('click');
                        return;
                    }

                break;
                case 2:
                     if($('#requestType').val() != 'POST'){  // 如果是GET 方式则路过第 3 步
                        currentStep++;
                     }
                    $('#json-input').trigger('blur');
                break;
                case 3:  // 验证类型选择，是否为空；若为空，则提示用户

                    if($('#editor').html().length > 0){
                        validJSON('json-input', 'editor', 'parsedJSON', 'jsonParseInfo', window.JSONresult());  // 请求后台验证JSON格式是否正确，如果不正确提示并阻止下一步
                    } else {
                        $('#JSONparse-btn').trigger('click');
                    }
                    $('#resultDemo').trigger('blur');
                    return;
                break;
                case 4:
                    if($('#resulteditor').html().length > 0){
                        validJSON('resultDemo', 'resulteditor', 'resultparsedJSON', 'resultconfigInfo', window.resultJSONresult());  // 请求后台验证JSON格式是否正确，如果不正确提示并阻止下一步
                    } else {
                        $('#result-JSONparse-btn').trigger('click');
                    }
                   return;
                break;
            }
            currentStep++;
            changeStepTo(currentStep);
        });



        $('.preStep').click(function(){
            switch(currentStep){
                case 4:
                     if(($('#requestType').val() != 'POST')){  // 如果是GET 方式则路过第 3 步
                        currentStep--;
                     }
                break;
            }
            currentStep --;
            changeStepTo(currentStep);
        });

        function validJSON(input, editor, parsedJSON, jsonInfo, jsonResultStr){

            var errorInfo = '';

            // 对JSON解析树进行表单验证
            window.JSONerror = false;
            $('#'+editor+' select').each(function(){
                if(!$(this).val()){
                    $(this).addClass('error');
                    window.JSONerror = true;
                } else {
                    $(this).removeClass('error');
                }
            });

            $('#'+parsedJSON).text(JSON.stringify(jsonResultStr));  // 将修改后的树数据放入隐藏textarea

            if(window.JSONerror) { errorInfo += ' 未选择所有数据类型 '; }

            $.post(
                '/validateJsonStr',
                {
                    'jsonStr': $('#'+input).val(),
                    'struct': $('#'+parsedJSON).val()
                },
                function(data){
                    if(data.code == 1){
                        $('#'+input).addClass('error');
                        errorInfo += data.data;
                        window.JSONerror = true;
                    } else {

                        $('#'+input).removeClass('error');
                    }
                    if(window.JSONerror){
                        $('#'+jsonInfo).html('<p>'+ errorInfo +'</p>').show();
                        return false;
                    } else {
                        // 直接进入下一步，检验成功不再进行提示
                        //$('#'+jsonInfo).removeClass('alert-danger').addClass('alert-success').html('<p>'+ data.data +'</p>').show();
                        currentStep++;
                        changeStepTo(currentStep);
                    }
                }
            );
        }


        function changeStepTo(step){  // 跳转到指定页面
            var stepTitle = '';
            switch(step){
                case 1: stepTitle = 'API接口配置'; break;
                case 2:
                    stepTitle = '内部方法及参数配置';

                break;
                case 3:
                    stepTitle = '请求参数配置';

                    if($('#json-input').val().length > 0){
                        $('#json-input').val(formatJson($('#json-input').val()));  // 自动格式化输入框中的 json 字符串
                        window.JSONresult = $('#editor').jsonEditorByTreeJson( JSON.parse($('#parsedJSON').val()));  // 将保存的数据取出来解析成“树”进行修改
                    }

                    $('#editor .item .property').mouseenter(function(){
                        $(this).parent().addClass('grey');
                    });
                    $('#editor .item .property').mouseleave(function(){
                        $(this).parent().removeClass('grey');
                    });
                break;
                case 4:
                    stepTitle = '请求结果配置';
                    if($('#resultDemo').val().length > 0){
                        $('#resultDemo').val(formatJson($('#resultDemo').val()));

                        try{
                             var jsonstr = JSON.parse($('#resultparsedJSON').val());
                             window.resultJSONresult = $('#resulteditor').jsonEditorByTreeJson(jsonstr);  // 将保存的数据取出来解析成“树”进行修改
                        } catch(e) {
                            $('#resultconfigInfo').html('<p>JSON格式错误：'+ e +'</p>').removeClass('alert-success').addClass('alert-danger').show();
                        }
                    }

                    $('#resulteditor .item .property').mouseenter(function(){
                        $(this).parent().addClass('grey');
                    });
                    $('#resulteditor .item .property').mouseleave(function(){
                        $(this).parent().removeClass('grey');
                    });
                break;
                case 5:
                    stepTitle = '预览';
                    if($('#detail').val() == 1){
                        return;
                    }
                break;
            }
            $('#step-title').text(stepTitle);

            $('.nextStep').show();
            $('#save').hide();
            if(step == 1){
                $('.preStep').hide();
            } else if(step == totalStep){
                $('.nextStep').hide();
                $('#save').show();
                finalReview(); // 将所有数据展示出来
            }else{
                $('.preStep').show();
            }

            $('.alert').hide();

            $('#step-nav-box li').eq(step-1).addClass('active').siblings().removeClass('active');
            $('#apiconfig-box > div').eq(step-1).addClass('active').siblings().removeClass('active');
        }

        $('#namespace, #name').keyup(function(){
            $('.hasApi').removeAttr('data-exists');
        });
        $('#version').change(function(){
            $('.hasApi').removeAttr('data-exists');
        });





        $('#JSONparse-btn').click(function(){
            try {
                var json = JSON.parse( $('#json-input').val());
            }catch (e){
                $('#json-input').addClass('error');
                $('#jsonParseInfo').html('<p>解析JSON时发生错误：'+e+'</p>').show();
                return;
            }
            var oldJson = '';
            if($('#editor').html().length > 0){
                oldJson = window.JSONresult();
            }

            $('#json-input').removeClass('error');
            $('#jsonParseInfo').hide();

            window.JSONresult = $('#editor').jsonEditor(json, oldJson);  // 解析JSON，生成JSON树
            window.JSONresultStr = window.JSONresult();

            $('.item .property').mouseenter(function(){
                $(this).parent().addClass('grey');
            });
            $('.item .property').mouseleave(function(){
                $(this).parent().removeClass('grey');
            });

        });




        $('#result-JSONparse-btn').click(function(){
            try {
                var json = JSON.parse( $('#resultDemo').val());
            }catch (e){
                $('#resultDemo').addClass('error');
                $('#resultconfigInfo').html('<p>解析JSON时发生错误：'+e+'</p>').show();
                return;
            }
            var oldJson = '';
            if($('#resulteditor').html().length > 0){
                oldJson = window.resultJSONresult();
            }

            $('#resultDemo').removeClass('error');
            $('#resultconfigInfo').hide();

            window.resultJSONresult = $('#resulteditor').jsonEditor(json, oldJson);  // 解析JSON，生成JSON树
            window.resultJSONresultStr = window.resultJSONresult();

            $('.item .property').mouseenter(function(){
                $(this).parent().addClass('grey');
            });
            $('.item .property').mouseleave(function(){
                $(this).parent().removeClass('grey');
            });
        });







        // 给检测API是否存在按钮绑定事件
        $(".hasApi").on("click", function () {
            $("#apiInfo").css("display","none");

            var apiNamespace = $("#namespace").val();
            var apiName = $("#name").val();
            var apiVersion = $("#version").val();
            if(!$("#apiInterfaceConfig").valid()) {
                $("#apiInfo").html("<p>请把参数填写完整</p>").show().removeClass("alert-success").addClass("alert-danger");
                return;
            }
            $("#apiInfo").html('<p>正在检查...</p>');
            $.post("checkapi", {"namespace":apiNamespace, "name":apiName, "version":apiVersion, "env": getEnv()}, function (d) {


                if("success" == d.message) {
                    $("#apiInfo").html("<p>当前不存在此API，请继续配置</p>");
                    $("#apiInfo").css("display","block");

                    $("#apiInfo").removeClass("alert-danger");
                    $("#apiInfo").addClass("alert-success");
                    $(".nextStep").css("display", "block");

                    $('.hasApi').attr('data-exists', 'no');
                } else {
                    $("#apiInfo").html("<p>此API已存在，请修改后继续配置</p>");
                    $("#apiInfo").css("display","block");

                    $("#apiInfo").removeClass("alert-success");
                    $("#apiInfo").addClass("alert-danger");
                    $('.hasApi').attr('data-exists', 'yes');

                }
            }, "json");
        });


        if($('#detail').val() == 1){

            $('#step-title').text('预览');
            $('#goback-btn').show().click(function(){
               window.history.back();
            })

            $('#step-nav-box, #save, .nextStep').hide();

//            if(($('#json-input').val().length >0) && ($('#parsedJSON').val().length > 0)){
//                window.JSONresultStr = $('#editor').jsonEditorByTreeJson( JSON.parse($('#parsedJSON').val()) );
//            }
//            if(($('#resultDemo').val().length >0) && ($('#resultparsedJSON').val().length > 0)){
//                window.resultJSONresultStr = $('#resulteditor').jsonEditorByTreeJson( JSON.parse($('#resultparsedJSON').val()) );
//            }
            $('#apiconfig-box > div').eq(4).addClass('active').siblings().removeClass('active');

            finalReview(); //
        }


        var apiObj, serviceObj, paramObj, resultObj;

        function finalReview(){


            // API接口信息配置
            apiObj = formdataToJSON($('#apiInterfaceConfig').serializeArray());

            var str = objToStr(apiObj);

            $('#final-review-apiconfig').html(str);

            // 将resultDemo 结果结构 加入api接口信息对象，保存
            resultObj = formdataToJSON($('#resultconfigForm').serializeArray());
            resultObj.resultStruct = $('#resultparsedJSON').val();
            for(k in resultObj){  //  第四步数据合并到第一步数据对象，分开预览
                apiObj[k] = resultObj[k];
            }

            var str = objToStr(resultObj);
            $('#final-review-resultjson').html(str);

            if($('#detail').val() == 1){
                $('#resultStructureJson').html('').jsonEditorByTreeJson(JSON.parse($('#resultparsedJSON').val()), true);
            }else {
                // 结果结构 JSON 树
                $('#result_struct').text(JSON.stringify(resultJSONresult()));

                $('#resultStructureJson').html('').jsonEditorByTreeJson(JSON.parse($('#result_struct').val()), true);
            }

            $('#resultStructureJson').addClass('json-editor expanded ');
            if($('#result-treeHead-view').length == 0){
                var treeHead = $('<p style="text-align:right; color:#aaa; margin-bottom:0;" id="result-treeHead-view"><span style="margin-right:30px">数据类型</span><span style="margin-right:28px">是否必需</span><span style="margin-right:245px">描述</span></p>');
                $('#resultStructureJson').before(treeHead);
            }






            // 服务配置
            serviceObj = formdataToJSON($('#methodconfigform').serializeArray());

            if($('#detail').val() == 1){
                serviceObj.innerParams = $('#innerParams').val();
            } else {
                var arr = [];
                $('#serviceParamList input[type="checkbox"]:checked').each(function(){
                    arr.push($(this).val());
                });
                serviceObj.innerParams = arr.join(' ');
            }
            var str = objToStr(serviceObj);
            $('#final-review-method').html( str );


            jsonProcess('resultDemoJson', $('#resultDemo').val());

            if(apiObj.requestType == 'POST' ) {  // 请求方式 POST，加入json数据

                paramObj = formdataToJSON($('#jsonparseForm').serializeArray());
                paramObj.requestStructure = $('#parsedJSON').val();


                var str = objToStr(paramObj);
                $('#final-review-json').html( str );

                $('#review-jsonparse').show();

                $('#requestStructureJson').html('').jsonEditorByTreeJson(JSON.parse($('#parsedJSON').val()), true);

                $('#requestStructureJson').addClass('json-editor expanded ');
                if($('#treeHead-view').length == 0){
                    var treeHead = $('<p style="text-align:right; color:#aaa; margin-bottom:0;" id="treeHead-view"><span style="margin-right:30px">数据类型</span><span style="margin-right:28px">是否必填</span><span style="margin-right:245px">描述</span></p>');
                    $('#requestStructureJson').before(treeHead);
                }

                jsonProcess('requestDemoJson', $('#json-input').val());

            } else {  // 请求方式 GET
                paramObj = null;
                $('#review-jsonparse').hide();
            }
        }

        function formdataToJSON (arr){
            var len = arr.length;
            var obj = {};

            for(var i=0;i<len; i++){
                obj[arr[i].name] = arr[i].value;
            }
            return obj;
        }

        function objToStr(obj){
            var str = '', id='';
            for(o in obj){
                id = '';
                var label = '', value = '';
                switch(o){
                    case 'id': continue; break;

                    case 'requestType': label = '请求方式'; break;
                    case 'groupId':
                        label = 'API分组';
                        value = $('#apiGroup option[value='+obj[o]+']').text();
                    break;
                    case 'currentId': continue; break;
                    case 'login':
                        label = '是否需要登录';
                        value = $('#isLogin option[value='+obj[o]+']').text();
                    break;
                    case 'frequencyControl':
                        label = '频率控制';
                        value = $('#isFreq option[value='+obj[o]+']').text();
                    break;
                    case 'checkInner':
                        label = '区分内外请求';
                        value = $('#isInnerOuter option[value='+obj[o]+']').text();
                    break;
                    case 'apiDescription': label = '功能描述'; break;
                    case 'apiScene': label = '使用场景'; break;

                    case 'innerParams':
                       label = '内部参数';

                        if(obj[o].length > 0){
                            var arr = $.trim(obj[o]);

                            arr = arr.split(' ');
                            for(var i=0;i<arr.length; i++){
                                arr[i] = $('#serviceParamList input[value="'+arr[i]+'"]').parent().text();
                            }
                            value = arr.join(', ');
                        }
                        if(value == '') { value='无'; }
                    break;

                    case 'requestDemo':
                        label = '请求示例';
                        id = 'requestDemoJson';
                        obj[o] = html_encode(obj[o]+''); // 防止 json 字符串中有HTML标签，在预览写入页面时，浏览器当正常文档结构解析
                    break;
                    case 'requestStructure':
                         label = '请求结构';
                         id = 'requestStructureJson';
                     break;
                    case 'requestMemo':
                        label = '请求备注';
                        if(obj[o] == '') {
                            value = '无';
                        }
                    break;

                    case 'resultDemo':
                        label = '结果示例';
                         id = 'resultDemoJson';
                        obj[o] = html_encode(obj[o]+''); // 防止 json 字符串中有HTML标签，在预览写入页面时，浏览器当正常文档结构解析
                    break;
                    case 'resultStruct':
                        label = '结果结构';
                        id = 'resultStructureJson';
                    break;
                    case 'resultMemo':
                        label = '结果备注';
                        if(obj[o] == '') {
                            value = '无';
                        }
                    break;

                    default: label = o; break;
                }


                str += '<div class="form-group"><label class="col-sm-2 control-label">'+
                 label + '</label><div class="col-sm-10"><p class="form-control-static" id="'+ id +'">'+
                 (value== '' ? obj[o] : value) + '</p></div></div>';
            }
            return str;
        }






        //********************************************** 方法&方法参数配置 ******************************************
//        var carmenMethodParamTable = $("#carmenMethodParam").DataTable({
//            paging: false,
//            //ordering: true,
//            language: {"infoEmpty": "","info": ""}
//        });
//

        // 保存所有的配置信息
        $("#save").on("click", function () {
            $("#updateStatus").html("正在保存...");
            $("#myHideModal").modal("show");
            var flag = $(this).attr("flag"); // 1代表保存， 2代表保存并继续

            $.post(
                "saveResult",
                {
                    'apiObj': JSON.stringify( apiObj ),
                    'paramObj': JSON.stringify(paramObj),
                    'serviceObj': JSON.stringify(serviceObj),
                    "env":getEnv()
                },
                 function (d) {
                    if("success" == d.message) {
                        var env = getEnv();
                        window.location.href = "apilist?env=" + env;
                        return;
                    }else {
                        $("#updateStatus").html("保存失败!");
                        $("#myHideModal").delay(6000);
                        $("#myHideModal").modal("hide");
                        $("#apiParamMappingInfo").html('<p>更新失败<br/>' + d + '</p>');
                        $("#apiParamMappingInfo").css("display", 'block');
                    }
                });
        });



        // 批量增加API参数
//        $("#batchAdd").on("click", function(){
//            $("#myPHPBatchModal").modal("show");
//        });
//
//        $("#sureBatchAdd").on("click", function(){
//            var content = $("#phpCode").val();
//            if("" == content || "undefined" == typeof(content)) {
//                return;
//            }
//            var result = content.replace(/ => /g, ":");
//            result = result.replace(/\[/g, "{");
//            result = result.replace(/\]/g, "}");
//            result = result.replace(/,$/, "");
//            result = "{" + result + "}";
//            var jsonStr = eval("(" + result + ")");
//            var params = jsonStr.params;
//            for(var key in params) {
//                var values = params[key];
//                addBatchRow(key, values.type, values.desc, values.required);
//            }
//            $("#myPHPBatchModal").modal("hide");
//        });
//
//        function addBatchRow(paramName, type, desc, required) {
//
//            $("#apiParamInfo").html("");
//            $("#apiParamInfo").css("display","none");
//            var operate = '<a href="#" class="needTip" data-toggle="tooltip" data-placement="top" title="编辑" style="margin-left:5px;display:none;" ><span class="glyphicon glyphicon-pencil" aria-hidden="true" ></span></a><a href="#" class="needTip deleteCurrentRow" flag="step2" apiIdValue="0" data-toggle="tooltip" data-placement="top" title="删除当前行" style="margin-left:5px;"><span class="glyphicon glyphicon-trash" aria-hidden="true"></span></a>';
//
//            var paramNameElement = '<input style="max-width:160px;" type="text"  name="apiParamName" value="' + paramName + '" >';
//            var paramTypeElement = '<input style="max-width:160px;" type="text"  name="apiParamType" value="' + type + '" >';
//            var describleElement = '<input style="max-width:100px;" type="text"  name="describle" value="' + desc + '" >';
//            if(required) { // 必须填写的参数
//                var isRequiredElement = '<select style="min-width:50px;max-height: 25px;"  name="apiIsRequired" class="form-control" ><option value="1" selected>是</option><option value="0" >否</option></select>';
//            } else {
//                var isRequiredElement = '<select style="min-width:50px;max-height: 25px;"  name="apiIsRequired" class="form-control" ><option value="1" >是</option><option value="0" selected>否</option></select>';
//            }
//
//            var ruleElement = '<input style="max-width:140px;" type="text"  name="rule" >';
//            var timestamp = Math.round(new Date().getTime()/1000);
//            var isStructureElement = '<select style="min-width:50px;max-height: 25px;"  name="apiIsStructure" class="form-control" ><option value="0" selected>是</option><option value="1" >否</option></select><input type="hidden" name="currentId" value="0"><input type="hidden" name="currentSequence" value="' + timestamp + '">';
//            carmenParamDivTable.row.add( [
//               paramNameElement,paramTypeElement,describleElement,isRequiredElement,ruleElement,isStructureElement,operate
//            ] ).draw();
//            $('[data-toggle="tooltip"]').tooltip();
//        }
//
//        // 批量增加方法参数
//        $("#batchAddMethod").on("click", function(){
//            $("#myPHPBatchMethodModal").modal("show");
//        });
//
//        $("#sureBatchAddMethod").on("click", function(){
//                var content = $("#phpMethodCode").val();
//                if("" == content || "undefined" == typeof(content)) {
//                    return;
//                }
//                var result = content.replace(/=>/g, ":");
//                result = result.replace(/^\$[a-zA-Z]+ =/g, "'params':");
//                result = result.replace(/\[/g, "{");
//                result = result.replace(/\]/g, "}");
//                result = result.replace(/,$/, "");
//                result = result.replace(/;$/, "");
//                result = result.replace(/->/g, "");
//                result = result.replace(/\$.*,?/g, "'test',");
//                result = "{" + result + "}";
//                var jsonStr = eval("(" + result + ")");
//                var params = jsonStr.params;
//                var count = 0;
//                for(var key in params) {
//                    var values = params[key];
//                    addBatchMethodRow(key, count);
//                    count = count + 1;
//                }
//                $("#myPHPBatchMethodModal").modal("hide");
//            });
//        function addBatchMethodRow(key, count) {
//            $("#apiMethodParamsInfo").html("");
//            $("#apiMethodParamsInfo").css("display","none");
//            var operate = '<a href="#" class="needTip" data-toggle="tooltip" data-placement="top" title="编辑" style="margin-left:5px;display:none;" ><span class="glyphicon glyphicon-pencil" aria-hidden="true" ></span></a><a href="#" class="needTip deleteCurrentRow" flag="step4" apiIdValue="0" data-toggle="tooltip" data-placement="top" title="删除当前行" style="margin-left:5px;"><span class="glyphicon glyphicon-trash" aria-hidden="true"></span></a>';
//            var paramNameElement = '<input style="max-width:160px;" type="text"  name="apiParamName" value="' + key + '" />';
//            var paramTypeElement = '<input style="max-width:160px;" type="text"  name="apiParamType" />';
//            var sequenceElement = '<input style="max-width:160px;" type="text"  name="sequence" value="' + count + '" />';
//            var isStructureElement = '<select style="min-width:50px;max-height: 25px;"  name="apiIsStructure" class="form-control" ><option value="0" selected>是</option><option value="1" >否</option></select><input type="hidden" name="currentId" value="0">';
//
//            carmenMethodParamTable.row.add( [
//               paramNameElement,paramTypeElement,sequenceElement,isStructureElement,operate
//            ] ).draw();
//            $('[data-toggle="tooltip"]').tooltip();
//         }
//
//         // 批量增加方法参数
//         $("#batchAddMethod").on("click", function(){
//             $("#myPHPBatchMethodModal").modal("show");
//         });
//
//         $("#sureBatchAddMethod").on("click", function(){
//                 var content = $("#phpMethodCode").val();
//                 if("" == content || "undefined" == typeof(content)) {
//                     return;
//                 }
//                 var result = content.replace(/=>/g, ":");
//                 result = result.replace(/^\$[a-zA-Z]+ =/g, "'params':");
//                 result = result.replace(/\[/g, "{");
//                 result = result.replace(/\]/g, "}");
//                 result = result.replace(/,$/, "");
//                 result = result.replace(/;$/, "");
//                 result = result.replace(/->/g, "");
//                 result = result.replace(/\$.*,?/g, "'test',");
//                 result = "{" + result + "}";
//                 var jsonStr = eval("(" + result + ")");
//                 var params = jsonStr.params;
//                 var count = 0;
//                 for(var key in params) {
//                     var values = params[key];
//                     addBatchMethodRow(key, count);
//                     count = count + 1;
//                 }
//                 $("#myPHPBatchMethodModal").modal("hide");
//             });
//         function addBatchMethodRow(key, count) {
//             $("#apiMethodParamsInfo").html("");
//             $("#apiMethodParamsInfo").css("display","none");
//             var operate = '<a href="#" class="needTip" data-toggle="tooltip" data-placement="top" title="编辑" style="margin-left:5px;display:none;" ><span class="glyphicon glyphicon-pencil" aria-hidden="true" ></span></a><a href="#" class="needTip deleteCurrentRow" flag="step4" apiIdValue="0" data-toggle="tooltip" data-placement="top" title="删除当前行" style="margin-left:5px;"><span class="glyphicon glyphicon-trash" aria-hidden="true"></span></a>';
//             var paramNameElement = '<input style="max-width:160px;" type="text"  name="apiParamName" value="' + key + '" />';
//             var paramTypeElement = '<input style="max-width:160px;" type="text"  name="apiParamType" />';
//             var sequenceElement = '<input style="max-width:160px;" type="text"  name="sequence" value="' + count + '" />';
//             var isStructureElement = '<select style="min-width:50px;max-height: 25px;"  name="apiIsStructure" class="form-control" ><option value="0" selected>是</option><option value="1" >否</option></select><input type="hidden" name="currentId" value="0">';
//
//             carmenMethodParamTable.row.add( [
//                paramNameElement,paramTypeElement,sequenceElement,isStructureElement,operate
//             ] ).draw();
//             $('[data-toggle="tooltip"]').tooltip();
//         }


    } // 结束 isReady 函数



    // 获取环境变量的值
    function getEnv() {
        var env = $("#env").serialize();
        var envValue = env.split("=")[1];
        //console.log("value: " + envValue);
        return envValue;
    }


    function html_encode(str)
     {
       var s = "";
       if (str.length == 0) return "";
       s = str.replace(/&/g, "&gt;");
       s = s.replace(/</g, "&lt;");
       s = s.replace(/>/g, "&gt;");
       s = s.replace(/\'/g, "&#39;");
       s = s.replace(/\"/g, "&quot;");
       return s;
     }





    /* ******************************** JSON 着色开始 ***************************************** */


    function jsonInputView( inputId, viewId ){

        $('#'+inputId).blur(function(){

            $(this).val(formatJson($(this).val()));  // 自动格式化JSON字符串

            var proced = jsonProcess(viewId, $('#'+inputId).val());

            if(proced) {
                $(this).hide().siblings('#'+viewId).show();
                $('#'+viewId).siblings('textarea').removeClass('error');
            }else {
                $('#'+viewId).siblings('textarea').addClass('error');
            }
        });

        $('#'+viewId).click(function(){
            $(this).hide().siblings('#'+inputId).show().focus();
        });
    }


    function jsonProcess(eleId, jsonStr){
//		  var json = document.getElementById("RawJson").value;
      var json = jsonStr;
      var html = "";
      try{
        if(json == "") return false;
        var obj = eval("["+json+"]");
        html = ProcessObject(obj[0], 0, false, false, false);
        document.getElementById(eleId).innerHTML = "<PRE class='CodeContainer'>"+html+"</PRE>";
        return true;
      }catch(e){
        console.log(eleId+" JSON语法错误,不能格式化,错误信息:\n"+e.message);
        document.getElementById(eleId).innerHTML = "";
        return false;
      }
    }


    /*翻译：球*/
    // 这里需要制表符来表示空格，而不是用CSS的magin-left属性
    // 这是为了确保直接复制和粘贴时的格式(译注：若用margin-left会导致复制时复制不到空白)。
    window.TAB = "  ";

    function IsArray(obj) {
      return obj &&
              typeof obj === 'object' &&
              typeof obj.length === 'number' &&
              !(obj.propertyIsEnumerable('length'));
    }

    function ProcessObject(obj, indent, addComma, isArray, isPropertyContent){
      var html = "";
      var comma = (addComma) ? "<span class='Comma'>,</span> " : "";
      var type = typeof obj;

      if(IsArray(obj)){
        if(obj.length == 0){
          html += GetRow(indent, "<span class='ArrayBrace'>[ ]</span>"+comma, isPropertyContent);
        }else{
          html += GetRow(indent, "<span class='ArrayBrace'>[</span>", isPropertyContent);
          for(var i = 0; i < obj.length; i++){
            html += ProcessObject(obj[i], indent + 1, i < (obj.length - 1), true, false);
          }
          html += GetRow(indent, "<span class='ArrayBrace'>]</span>"+comma);
        }
      }else if(type == 'object' && obj == null){
        html += FormatLiteral("null", "", comma, indent, isArray, "Null");
      }else if(type == 'object'){
        var numProps = 0;
        for(var prop in obj) numProps++;
        if(numProps == 0){
          html += GetRow(indent, "<span class='ObjectBrace'>{ }</span>"+comma, isPropertyContent);
        }else{
          html += GetRow(indent, "<span class='ObjectBrace'>{</span>", isPropertyContent);
          var j = 0;
          for(var prop in obj){
            html += GetRow(indent + 1, "<span class='PropertyName'>"+prop+"</span>: "+ProcessObject(obj[prop], indent + 1, ++j < numProps, false, true));
          }
          html += GetRow(indent, "<span class='ObjectBrace'>}</span>"+comma);
        }
      }else if(type == 'number'){
        html += FormatLiteral(obj, "", comma, indent, isArray, "Number");
      }else if(type == 'boolean'){
        html += FormatLiteral(obj, "", comma, indent, isArray, "Boolean");
      }else if(type == 'function'){
        obj = FormatFunction(indent, obj);
        html += FormatLiteral(obj, "", comma, indent, isArray, "Function");
      }else if(type == 'undefined'){
        html += FormatLiteral("undefined", "", comma, indent, isArray, "Null");
      }else{
        html += FormatLiteral(obj, "\"", comma, indent, isArray, "String");
      }
      return html;
    }
    function FormatLiteral(literal, quote, comma, indent, isArray, style){
      if(typeof literal == 'string')
        literal = literal.split("<").join("&lt;").split(">").join("&gt;");
      var str = "<span class='"+style+"'>"+quote+literal+quote+comma+"</span>";
      if(isArray) str = GetRow(indent, str);
      return str;
    }
    function FormatFunction(indent, obj){
      var tabs = "";
      for(var i = 0; i < indent; i++) tabs += window.TAB;
      var funcStrArray = obj.toString().split("\n");
      var str = "";
      for(var i = 0; i < funcStrArray.length; i++){
        str += ((i==0)?"":tabs) + funcStrArray[i] + "\n";
      }
      return str;
    }
    function GetRow(indent, data, isPropertyContent){
      var tabs = "";
      for(var i = 0; i < indent && !isPropertyContent; i++) tabs += window.TAB;
      if(data != null && data.length > 0 && data.charAt(data.length-1) != "\n")
        data = data+"\n";
      return tabs+data;
    }

    /* ******************************** JSON 着色结束 ***************************************** */



 });
