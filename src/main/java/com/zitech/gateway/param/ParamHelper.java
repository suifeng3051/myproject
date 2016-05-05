package com.zitech.gateway.param;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.zitech.gateway.common.BaseException;
import com.zitech.gateway.common.LogicalException;
import com.zitech.gateway.common.ParamException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ParamHelper {

    public static final String REQUIRED = "require";
    public static final String TYPE = "type";
    public static final String FIELDS = "fields";
    private static final Logger logger = LoggerFactory.getLogger(ParamHelper.class);

    /**
     * 根据json结构字符串构造参数树 注意: 根节点不属于结构的一部分
     *
     * @param struct 结构字符串
     * @return 根节点
     */
    public static Param buildTree(String struct) {

        Param root = new Param();
        root.setType(ParamType.ROOT);

        JSONObject object = JSON.parseObject(struct);
        ParamHelper.parseObject(object, root);

        return root;
    }

    /**
     * parse json object
     *
     * @param object object
     * @param root   root
     */
    private static void parseObject(JSONObject object, Param root) {
        for (String key : object.keySet()) {
            JSONObject struct = object.getJSONObject(key);
            Param param = new Param();
            param.setName(key);
            param.setRequired(struct.getBoolean(REQUIRED));

            try {
                param.setType(ParamType.from(struct.getString(TYPE)));
            } catch (BaseException e) {
                logger.info("unknown param type: ", e);
                throw new ParamException(e.getCode(), "field: " + param.getName() + ", " + e.getDescription());
            }

            param.setValidate(getValidate(param.getType()));
            param.setParent(root);
            root.getFields().add(param);

            if (param.getType() == ParamType.OBJECT) {
                JSONObject fields = struct.getJSONObject(FIELDS);
                parseObject(fields, param);
            } else if (param.getType() == ParamType.ARRAY) {
                JSONArray fields = struct.getJSONArray(FIELDS);
                if (fields.size() == 0)
                    throw new LogicalException(Constants.Code.NO_ARRAY_FIELD,
                            "no array field for " + param.getName());
                parseArray((JSONObject) fields.get(0), param);
            }
        }
    }

    /**
     * parse json array
     *
     * @param object object
     * @param root   root
     */
    private static void parseArray(JSONObject object, Param root) {
        Param param = new Param();
        param.setName(null);
        param.setRequired(object.getBoolean(REQUIRED));

        try {
            param.setType(ParamType.from(object.getString(TYPE)));
        } catch (BaseException e) {
            logger.info("unknown param type: ", e);
            throw new ParamException(e.getCode(), "field: " + param.getName() + ", " + e.getDescription());
        }

        param.setValidate(getValidate(param.getType()));
        param.setParent(root);
        root.getFields().add(param);

        if (param.getType() == ParamType.OBJECT) {
            JSONObject fields = object.getJSONObject(FIELDS);
            parseObject(fields, param);
        } else if (param.getType() == ParamType.ARRAY) {
            JSONArray fields = object.getJSONArray(FIELDS);
            if (fields.size() == 0)
                throw new LogicalException(Constants.Code.NO_ARRAY_FIELD,
                        "no array field for " + param.getName());
            parseArray((JSONObject) fields.get(0), param);
        }
    }

    /**
     * get validation by param type
     *
     * @param type type
     * @return validate
     */
    private static IValidator getValidate(ParamType type) {
        switch (type) {
            case INT:
                return new IntValidator();
            case STRING:
                return new StringValidator();
            case ARRAY:
                return new ArrayValidator();
            case OBJECT:
                return new ObjectValidator();
            case BOOL:
                return new BoolValidator();
        }

        throw new LogicalException(Constants.Code.UNKNOWN_PARAM_TYPE,
                "unknown param type");
    }

    /**
     * 验证参数的合法性,遍历json串,验证json的合法性
     *
     * @param source json
     * @param param  参数树
     */
    private static void validate(JSONObject source, Param param) {
        ParamHelper.validateObject(source, param);
    }

    /**
     * validate judging by json object
     *
     * @param object object
     * @param param  param
     */
    private static void validateObject(Object object, Param param) {
        JSONObject jj = (JSONObject) object;
        for (String key : jj.keySet()) {
            Object o = jj.get(key);

            Param vp = ParamHelper.getParam(param, key);
            IValidator validator = vp.getValidate();
            validator.v(o, vp);
            if (o instanceof JSONObject)
                ParamHelper.validateObject(o, vp);
            else if (o instanceof JSONArray)
                ParamHelper.validateArray(o, vp);
        }
    }

    /**
     * validate judging by json array
     *
     * @param object object
     * @param param  param
     */
    private static void validateArray(Object object, Param param) {
        JSONArray ja = (JSONArray) object;
        Param vp = param.getFields().get(0);
        IValidator validator = vp.getValidate();
        for (Object o : ja) {
            validator.v(o, param);
            if (o instanceof JSONObject)
                ParamHelper.validateObject(o, vp);
            else if (o instanceof JSONArray)
                ParamHelper.validateArray(o, vp);
        }
    }

    /**
     * get param from current list
     *
     * @param param param
     * @param name  name
     * @return param of the name
     */
    private static Param getParam(Param param, String name) {
        List<Param> fields = param.getFields();
        for (Param p : fields) {
            if (p.getName().equals(name)) {
                return p;
            }
        }

        String path = getPath(param);
        if(StringUtils.isEmpty(path))
            path = name;
        else
            path = path + "/" + name;
        throw new ParamException(Constants.Code.UNKNOWN_PARAM,
                "unknown param " + path);
    }

    /**
     * get parent including itself
     *
     * @param param param
     * @return parent list
     */
    private static List<Param> getParents(Param param) {
        Param p = param;
        List<Param> paramList = new ArrayList<>();
        paramList.add(p);
        while ((p = p.getParent()) != null) {
            if(p.getName() != null)
                paramList.add(p);
        }

        Collections.reverse(paramList);
        return paramList;
    }

    /**
     * get path from root to current param including current param
     *
     * @param param param
     * @return path
     */
    private static String getPath(Param param) {
        List<Param> paramList = ParamHelper.getParents(param);
        List<String> nameList = new ArrayList<>(paramList.size());
        paramList.stream().forEach(e -> {
            nameList.add(e.getName());
        });
        return StringUtils.join(nameList, "/");
    }

    /**
     * 验证参数的合法性,遍历param,检查json串的合法性
     *
     * @param param  param
     * @param source json
     */
    private static void validate(Param param, JSONObject source) {
        List<Param> paramList = param.getFields();
        for (Param p : paramList) {
            Object so = source.get(p.getName());
            if (p.getType() == ParamType.OBJECT)
                validateObject(p, so);
            else if (p.getType() == ParamType.ARRAY)
                validateArray(p, so);
            else {
                if (p.getRequired() && so == null)
                    throw new ParamException(Constants.Code.PARAM_REQUIRED,
                            "param " + ParamHelper.getPath(p) + " required");

                if (!p.getRequired() && so == null)
                    continue; // some other action?
            }
        }
    }

    /**
     * validate json object judging by param
     *
     * @param param  param
     * @param object object
     */
    private static void validateObject(Param param, Object object) {
        if (param.getRequired() && object == null)
            throw new ParamException(Constants.Code.PARAM_REQUIRED,
                    "param " + ParamHelper.getPath(param) + " required");

        if (!param.getRequired() && object == null)
            return;

        if (!(object instanceof JSONObject))
            throw new ParamException(Constants.Code.PARAM_SHOULD_OBJECT,
                    "param " + ParamHelper.getPath(param) + " not an object");

        JSONObject source = (JSONObject) object;
        List<Param> paramList = param.getFields();
        for (Param p : paramList) {
            Object so = source.get(p.getName());
            if (p.getType() == ParamType.OBJECT)
                validateObject(p, so);
            else if (p.getType() == ParamType.ARRAY)
                validateArray(p, so);
            else {
                if (p.getRequired() && so == null)
                    throw new ParamException(Constants.Code.PARAM_REQUIRED,
                            "param " + ParamHelper.getPath(p) + " required");

                if (!p.getRequired() && so == null)
                    continue; // some other action?
            }
        }
    }

    /**
     * validate json array judging by param
     *
     * @param param  param
     * @param object object
     */
    private static void validateArray(Param param, Object object) {
        if (param.getRequired() && object == null)
            throw new ParamException(Constants.Code.PARAM_REQUIRED,
                    "param " + ParamHelper.getPath(param) + " required");

        if (!param.getRequired() && object == null)
            return;

        if (!(object instanceof JSONArray))
            throw new ParamException(Constants.Code.PARAM_SHOULD_ARRAY,
                    "param " + ParamHelper.getPath(param) + " not an array");

        JSONArray source = (JSONArray) object;
        for (Param p : param.getFields()) { // only one parameter
            for (Object so : source) {
                if (p.getType() == ParamType.OBJECT)
                    validateObject(p, so);
                else if (p.getType() == ParamType.ARRAY)
                    validateArray(p, so);
                else {
                    if (param.getRequired() && so == null)
                        throw new ParamException(Constants.Code.PARAM_REQUIRED,
                                "param " + ParamHelper.getPath(param) + " required");

                    if (!param.getRequired() && so == null)
                        continue; // some other action?
                }
            }
        }
    }

    /**
     * validate request params
     *
     * @param source request json
     * @param param  param struct tree
     */
    public static void validate(String source, Param param) {
        try {
            JSONObject jsonObject = JSON.parseObject(source);
            ParamHelper.validate(jsonObject, param);
            ParamHelper.validate(param, jsonObject);
        } catch (JSONException e) {
            throw new ParamException(Constants.Code.JSON_PARSE_ERROR,
                    "json parse error: " + e.getMessage());
        } catch (ParamException e) {
            throw e;
        } catch (Exception e) {
            logger.error("unknown exception: ", e);
            throw new ParamException(1, "unknown exception: " + e.getMessage());
        }
    }

    public static void main(String[] args) throws Exception {
        FileInputStream fisStruct = new FileInputStream("/Users/bobo/Desktop/struct.txt");
        String struct = IOUtils.toString(fisStruct);
        FileInputStream fisJson = new FileInputStream("/Users/bobo/Desktop/json.txt");
        String json = IOUtils.toString(fisJson);
        Param tree = ParamHelper.buildTree(struct);

        ParamHelper.validate(json, tree);
    }
}
