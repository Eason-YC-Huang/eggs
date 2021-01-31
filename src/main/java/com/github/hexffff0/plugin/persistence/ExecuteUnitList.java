package com.github.hexffff0.plugin.persistence;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
/**
 * @author hyc
 * @since 2021/2/4
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ExecuteUnitList {

    @XmlElement(type = ExecuteUnit.class)
    @XmlElementWrapper
    private List<ExecuteUnit> executeUnitList = new ArrayList<>();

    public ExecuteUnitList() {}

    public ExecuteUnitList(List<ExecuteUnit> executeUnits) {
        this.executeUnitList.addAll(executeUnits);
    }

    public ExecuteUnitList(ExecuteUnit executeUnit) {
        this.executeUnitList.add(executeUnit);
    }

    public List<ExecuteUnit> getExecuteUnitList() {
        executeUnitList.forEach(ExecuteUnit::regenerateId);
        return executeUnitList;
    }

    public void setExecuteUnitList(List<ExecuteUnit> executeUnitList) {
        this.executeUnitList = executeUnitList;
    }

    public static List<ExecuteUnit> fromXml(String xml) {
        ExecuteUnitList list = JAXB.unmarshal(new StringReader(xml), ExecuteUnitList.class);
        return list.executeUnitList;
    }

    public static String toXml(List<ExecuteUnit> executeUnits) {
        ExecuteUnitList executeUnitList = new ExecuteUnitList(executeUnits);
        StringWriter sw = new StringWriter();
        JAXB.marshal(executeUnitList, sw);
        return sw.toString();
    }

    public static String toXml(ExecuteUnit templates) {
        ExecuteUnitList executeUnitList = new ExecuteUnitList(templates);
        StringWriter sw = new StringWriter();
        JAXB.marshal(executeUnitList, sw);
        return sw.toString();
    }

}
