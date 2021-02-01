package com.hyc.plugin.persistence;

import java.util.UUID;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import com.google.common.base.Objects;
/**
 * @author hyc
 */
@XmlRootElement(name = "codeTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class CodeTemplate {

    @XmlAttribute
    public static final String VERSION = "1.3";

    private final UUID uuid;

    public String name = "NewTemplate";

    public String className = "";

    public String code = "";

    public CodeTemplate(UUID id) {
        this.uuid = id;
    }

    public CodeTemplate(String id) {
        this.uuid = UUID.fromString(id);
    }

    public CodeTemplate() {
        this(UUID.randomUUID());
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
        return Objects.equal(uuid, that.uuid) &&
            Objects.equal(name, that.name) &&
            Objects.equal(className, that.className) &&
            Objects.equal(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uuid, name, className, code);
    }
}
