package com.hyc.plugin.persistence;

import com.google.common.base.Objects;
/**
 * @author hyc
 * @since 2021/2/1
 */
public class ClassBean {

    private String className;

    private String sourceCode;

    public ClassBean() {
    }

    public ClassBean(String className, String sourceCode) {
        this.className = className;
        this.sourceCode = sourceCode;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ClassBean)) {
            return false;
        }
        ClassBean classBean = (ClassBean) o;
        return Objects.equal(getClassName(), classBean.getClassName()) &&
            Objects.equal(getSourceCode(), classBean.getSourceCode());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getClassName(), getSourceCode());
    }
}
