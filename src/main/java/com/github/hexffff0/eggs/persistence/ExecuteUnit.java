package com.github.hexffff0.eggs.persistence;

import java.util.List;
import java.util.UUID;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.intellij.util.xmlb.annotations.XCollection;
/**
 * @author hyc
 */
@XmlRootElement(name = "executeUnit")
@XmlAccessorType(XmlAccessType.FIELD)
public class ExecuteUnit {

    @XmlAttribute
    public static final String VERSION = "0.1";

    public String uuid;

    public String name = "NewTemplate";

    public String desc = "";

    public String libPath = "";

    public String className = "";

    public String sourceCode = "";

    @XCollection(propertyElementName = "classBeanList")
    public List<ClassBean> classBeanList = Lists.newArrayList();

    public ExecuteUnit(String uuid) {
        this.uuid = uuid;
    }

    public ExecuteUnit() {
        this(UUID.randomUUID().toString());
    }

    public void regenerateId() {
        this.uuid = UUID.randomUUID().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExecuteUnit)) {
            return false;
        }
        ExecuteUnit that = (ExecuteUnit) o;
        return Objects.equal(uuid, that.uuid)
            && Objects.equal(name, that.name)
            && Objects.equal(className, that.className)
            && Objects.equal(libPath, that.libPath)
            && Objects.equal(sourceCode, that.sourceCode)
            && Objects.equal(classBeanList, that.classBeanList);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uuid, name, className, libPath, sourceCode, classBeanList);
    }
}
