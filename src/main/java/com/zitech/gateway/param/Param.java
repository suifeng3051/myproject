package com.zitech.gateway.param;

import java.util.ArrayList;
import java.util.List;

public class Param {

    private String name;
    private Boolean required;
    private ParamType type;
    private IValidator validate;
    private List<Param> fields = new ArrayList<>();
    private Param parent;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public IValidator getValidate() {
        return validate;
    }

    public void setValidate(IValidator validate) {
        this.validate = validate;
    }

    public ParamType getType() {
        return type;
    }

    public void setType(ParamType type) {
        this.type = type;
    }

    public List<Param> getFields() {
        return fields;
    }

    public void setFields(List<Param> fields) {
        this.fields = fields;
    }

    public Param getParent() {
        return parent;
    }

    public void setParent(Param parent) {
        this.parent = parent;
    }
}
