package com.hyc.plugin.persistence;

import com.google.common.base.Objects;
/**
 * @author hyc
 * @since 2021/2/1
 */
public class ClassBean {

    private String className;

    private String content;

    public ClassBean() {
    }

    public ClassBean(String className, String content) {
        this.className = className;
        this.content = content;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
            Objects.equal(getContent(), classBean.getContent());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getClassName(), getContent());
    }
}
