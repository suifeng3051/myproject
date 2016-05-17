// Simple yet flexible JSON editor plugin.
// Turns any element into a stylable interactive JSON editor.

// Copyright (c) 2013 David Durman

// Licensed under the MIT license (http://www.opensource.org/licenses/mit-license.php).

// Dependencies:

// * jQuery
// * JSON (use json2 library for browsers that do not support JSON natively)

// Example:

//     var myjson = { any: { json: { value: 1 } } };
//     var opt = { change: function() { /* called on every change */ } };
//     /* opt.propertyElement = '<textarea>'; */ // element of the property field, <input> is default
//     /* opt.valueElement = '<textarea>'; */  // element of the value field, <input> is default
//     $('#mydiv').jsonEditor(myjson, opt);

(function($) {
    $.fn.jsonEditor = function(json, oldJson, isEdite) {
        isEditeing = isEdite ? true : false;
        var jsonObject = parse(stringify(json));
        var treeJson = getTreeJsonByDataJson(jsonObject);
        treeJson = oldJson ? getTreeJsonByOldTreeJson(treeJson, oldJson) : treeJson;
        JSONEditor($(this), treeJson);
        return function() {
            return treeJson;
        };
    };

    $.fn.jsonEditorByTreeJson = function(json, isEdite) {
        isEditeing = isEdite ? true : false;
        treeJson = json;
        JSONEditor($(this), treeJson);
        return function() {
            return treeJson;
        };
    };

    function getTreeJsonByOldTreeJson(json, oldJson) {
        for (var key in json) {
            if (!json.hasOwnProperty(key)) continue;
            if (oldJson[key] != undefined) {
                json[key].require = oldJson[key].require != undefined ? oldJson[key].require : json[key].require;
                json[key].type = oldJson[key].type ? oldJson[key].type : json[key].type;
                json[key].des = oldJson[key].des ? oldJson[key].des : json[key].des;
                if (isArray(json[key])) {
                    getTreeJsonByOldTreeJson(json[key]['fields'][0], oldJson[key]['fields'][0]);
                } else if (isObject(json[key])) {
                    getTreeJsonByOldTreeJson(json[key]['fields'], oldJson[key]['fields']);
                }
            }
        }
        return json;
    }

    function getTreeJsonByDataJson(json) {
        if (!isObject(json) && !isArray(json)) {
            return '';
        }
        if (isArray(json)) {
            json.length = 1;
        }
        for (var key in json) {
            if (!json.hasOwnProperty(key)) continue;
            json[key] = {
                'require': 1,
                'type': assignType(json[key]),
                'des': "",
                'fields': getTreeJsonByDataJson(json[key])
            }
        }
        return json;
    }


    function JSONEditor(target, json) {
        construct(target, json);
    }

    function construct(root, json, path) {
        path = path || '';
        jsonObj = path ? jsonObj : json;
        root.children('.item').remove();

        for (var key in json) {
            if (!json.hasOwnProperty(key)) continue;

            var item = $('<div>', {
                    'class': 'item',
                    'data-path': path
                }),
                property = $('<input>', {
                    'class': 'property',
                    'readonly': 'true'
                }),
                typeSelect = isEditeing ? $('<input>', {
                    'class': 'type',
                    'readonly': 'true'
                }) : $('<select>', {
                    'class': 'type form-control'
                }),
                typeValue = isEditeing ? '' : $('<option value="INT">INT</option><option value="STRING">STRING</option><option value="BOOL">BOOL</option><option value="OBJECT">OBJECT</option><option value="ARRAY">ARRAY</option>'),
                requireCheckBox = isEditeing ? $('<input>', {
                    'class': 'type',
                    'readonly': 'true'
                }) : $('<input type="checkbox" value="" checked="true"/>'),
                descInput = isEditeing ? $('<input>', {
                    'class': 'type des-show',
                    'readonly': 'true'
                }) : $('<input type="text" class="des-show" />');
            if (isObject(json[key].fields) || isArray(json[key].fields)) {
                addExpander(item);
            }
            property.val(key).attr('title', key);
            descInput.val(json[key].des ? json[key].des : '无');

            if(isEditeing){descInput.attr('title', json[key].des);}

            isEditeing ? (json[key].require ? requireCheckBox.val('是') : requireCheckBox.val('否')) : (json[key].require ? requireCheckBox.attr('checked', 'true') : requireCheckBox.removeAttr('checked'));
            typeSelect.append(typeValue).val(json[key].type);
            item.append(property).append(descInput).append(requireCheckBox).append(typeSelect);
            root.append(item);

            typeSelect.change(typeChanged(jsonObj));
            requireCheckBox.change(requireChanged(jsonObj));
            descInput.change(descChanged(jsonObj));
            if (json[key].fields != undefined && json[key].fields != '') {
                construct(item, json[key].fields, (path ? path + '.' : '') + key);
            }
        }
    }

    function typeChanged(json) {
        return function() {
            var key = $(this).prev().prev().prev().val(),
                val = $(this).find('option:selected').val(),
                item = $(this).parent(),
                path = item.data('path');
            feed(json, (path ? path + '.' : '') + key, 'type', val);
        }
    }

    function requireChanged(json) {
        return function() {
            var key = $(this).prev().prev().val(),
                val = $(this).prop('checked') ? 1 : 0,
                item = $(this).parent(),
                path = item.data('path');
            feed(json, (path ? path + '.' : '') + key, 'require', val);
        }
    }

    function descChanged(json) {
        return function() {
            var key = $(this).prev().val(),
                val = $(this).val(),
                item = $(this).parent(),
                path = item.data('path');
            feed(json, (path ? path + '.' : '') + key, 'des', val);
        }
    }

    function feed(o, path, property, val) {
        if (path.indexOf('.') > -1) {
            var diver = o,
                i = 0,
                parts = path.split('.');
            for (var len = parts.length; i < len; i++) {
                if (diver.hasOwnProperty('fields')) {
                    diver = diver['fields'][parts[i]];
                } else {
                    diver = diver[parts[i]];
                }
            }
        } else {
            diver = o[path];
        }
        diver[property] = val;
        return o;
    }

    function addExpander(item) {
        if (item.children('.expander').length == 0) {
            var expander = $('<span>', {
                'class': 'expander'
            });
            expander.bind('click', function() {
                var item = $(this).parent();
                item.toggleClass('expanded');
            });
            item.prepend(expander);
        }
    }

    function isObject(o) {
        return Object.prototype.toString.call(o) == '[object Object]';
    }

    function isArray(o) {
        return Object.prototype.toString.call(o) == '[object Array]';
    }

    function isBoolean(o) {
        return Object.prototype.toString.call(o) == '[object Boolean]';
    }

    function isNumber(o) {
        return Object.prototype.toString.call(o) == '[object Number]';
    }

    function isString(o) {
        return Object.prototype.toString.call(o) == '[object String]';
    }

    function assignType(val) {
        var typeName = 'null';
        if (isObject(val)) typeName = 'OBJECT';
        else if (isArray(val)) typeName = 'ARRAY';
        else if (isBoolean(val)) typeName = 'BOOL';
        else if (isString(val)) typeName = 'STRING';
        else if (isNumber(val)) typeName = 'INT';
        return typeName;
    }

    function parse(str) {
        var res;
        try {
            res = JSON.parse(str);
        } catch (e) {
            res = null;
            error('JSON parse failed.');
        }
        return res;
    }

    function stringify(obj) {
        var res;
        try {
            res = JSON.stringify(obj);
        } catch (e) {
            res = 'null';
            error('JSON stringify failed.');
        }
        return res;
    }

    function error(reason) {
        if (window.console) {
            console.error(reason);
        }
    }
})(jQuery);
