package com.hyc.plugin.persistence;

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
@XmlRootElement(name = "codeTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class CodeTemplate {

    @XmlAttribute
    public static final String VERSION = "0.1";

    public final String uuid;

    public String name = "NewTemplate";

    public String className = "";

    public String libPath = "";

    public String code = "";

    @XCollection(propertyElementName = "classBeanList")
    public List<ClassBean> classBeanList = Lists.newArrayList();

    public CodeTemplate(String uuid) {
        this.uuid = uuid;
    }

    public CodeTemplate() {
        this(UUID.randomUUID().toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CodeTemplate)) {
            return false;
        }
        CodeTemplate that = (CodeTemplate) o;
        return Objects.equal(uuid, that.uuid)
            && Objects.equal(name, that.name)
            && Objects.equal(className, that.className)
            && Objects.equal(libPath, that.libPath)
            && Objects.equal(code, that.code)
            && Objects.equal(classBeanList, that.classBeanList);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uuid, name, className, libPath, code, classBeanList);
    }
}
