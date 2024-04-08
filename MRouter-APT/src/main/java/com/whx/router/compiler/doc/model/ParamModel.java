package com.whx.router.compiler.doc.model;

import org.apache.commons.lang3.StringUtils;

public class ParamModel {

    private String name;
    private String type;
    private String intentType;
    private boolean required;
    private String remark;

    public ParamModel() {
    }

    public ParamModel(String name, String type, boolean required, String remark) {
        this.name = name;
        this.type = type;
        this.required = required;
        if (!StringUtils.isEmpty(remark)) {
            this.remark = remark;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIntentType() {
        return intentType;
    }

    public void setIntentType(String intentType) {
        this.intentType = intentType;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
