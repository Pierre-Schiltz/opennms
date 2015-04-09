/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2011-2014 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2014 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.protocols.xml.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.opennms.netmgt.collection.api.AttributeGroupType;

/**
 * The Class XmlGroup.
 * 
 * @author <a href="mailto:agalue@opennms.org">Alejandro Galue</a>
 */
@XmlRootElement(name="xml-group")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlGroup implements Serializable, Comparable<XmlGroup> {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 2716588565159391498L;

    /** The Constant OF_XML_OBJECTS. */
    @XmlTransient
    private static final XmlObject[] OF_XML_OBJECTS = new XmlObject[0];

    /** The group name. */
    @XmlAttribute(name="name", required=true)
    private String m_name;

    /** The resource type. */
    @XmlAttribute(name="resource-type", required=true)
    private String m_resourceType;

    /** The resource XPath. */
    @XmlAttribute(name="resource-xpath", required=true)
    private String m_resourceXpath;

    /** The key XPath (for resource instance). */
    @XmlAttribute(name="key-xpath", required=false)
    private String m_keyXpath;

    /** The Resource Time XPath (for RRD updates). */
    @XmlAttribute(name="timestamp-xpath", required=false)
    private String m_timestampXpath;

    /** The Resource Time Format (for RRD updates). */
    @XmlAttribute(name="timestamp-format", required=false)
    private String m_timestampFormat;

    /** The XML objects list. */
    @XmlElement(name="xml-object", required=true)
    private List<XmlObject> m_xmlObjects = new ArrayList<XmlObject>();
    
    /** The m_xml resource key. */
    @XmlElement(name="resource-key", required=false)
    private XmlResourceKey m_xmlResourceKey;
    
    
    /**
     * Unique resource name for the hole group collect.
     * (A resource is a division of the collected elements for one or more names. It can be used to represent an equipment name.)
     * @author Capgemini - pschiltz
     */
    @XmlAttribute(name="csv-resource-name", required=false)
    private String m_csvResourceName;
    
    /**
     * CSV column index to extract the resource name for each row.
     * If two CSV row share the same resource name, then the same resource will be used.
     * When the column doesn't exist, the default resource named 'node' is used.
     * (A resource is a division of the collected elements for one or more names. It can be used to represent an equipment name.)
     * @author Capgemini - pschiltz
     */
    @XmlAttribute(name="csv-resource-index", required=false)
    private Integer m_csvResourceIndex;
    
    /**
     * Calculable expression to retrieve the resource name.
     * It will be calculated for each row.
     * When the expression return an error, the default resource named 'node' is used.
     * (A resource is a division of the collected elements for one or more names. It can be used to represent an equipment name.)
     * @author Capgemini - pschiltz
     */
    @XmlAttribute(name="csv-resource-expression", required=false)
    private String m_csvResourceExpression;
    
    /**
     * If the current date of the starting CSV collect is used for the timestamp.
     * @author Capgemini - pschiltz
     */
    @XmlAttribute(name="csv-timestamp-now", required=false)
    private Boolean m_csvTimestampNow;
    
    /**
     * Row index in collected CSV file from where to extract the timestamp.
     * If not set the timestamp may be retrieved for each row.
     * @author Capgemini - pschiltz
     */
    @XmlAttribute(name="csv-timestamp-row-index", required=false)
    private Integer m_csvTimestampRowIndex;
    
    /**
     * Column index in collected CSV file from where to extract the timestamp.
     * @author Capgemini - pschiltz
     */
    @XmlAttribute(name="csv-timestamp-column-index", required=false)
    private Integer m_csvTimestampColumnIndex;
    
    /**
     * Calculable expression on one CSV row whose result is the timestamp.
     * @author Capgemini - pschiltz
     */
    @XmlAttribute(name="csv-timestamp-expression", required=false)
    private String m_csvTimestampExpression;
    
    /**
     * Number of starting CSV file row to ignore.
     * @author Capgemini - pschiltz
     */
    @XmlAttribute(name="csv-trim-start", required=false)
    private Integer m_csvTrimStart;
    
    /**
     * Number of ending CSV file row to ignore.
     * @author Capgemini - pschiltz
     */
    @XmlAttribute(name="csv-trim-end", required=false)
    private Integer m_csvTrimEnd;
    
    /**
     * Expected number of columns of the CSV file.
     * A row is ignored when it doesn't have the expected column count.
     * @author Capgemini - pschiltz
     */
    @XmlAttribute(name="csv-columns-count", required=false)
    private Integer m_csvColumnsCount;
    
    /**
     * Calculable expression triggering the start of CSV collect.
     * @author Capgemini - pschiltz
     */
    @XmlAttribute(name="csv-start-when", required=false)
    private String m_csvStartWhen;
    
    /**
     * Calculable expression triggering the stop of CSV collect.
     * @author Capgemini - pschiltz
     */
    @XmlAttribute(name="csv-stop-when", required=false)
    private String m_csvStopWhen;
    
    /**
     * Expression engine full calss name used for calculation.
     * (Used for triggering CSV collect start/stop and for calculation xml-object value).
     * @author Capgemini - pschiltz
     */
    @XmlAttribute(name="csv-expression-engine", required=false)
    private String m_csvExpressionEngine;
    
    /**
     * List of calculated expression rules for collect (at least the CSV collect).
     * @author Capgemini - pschiltz
     */
    @XmlElement(name="rule", required=false)
    private List<Rule> m_rules = new ArrayList<>();
    
    
    
    
    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return m_name;
    }

    /**
     * Sets the name.
     *
     * @param name the new name
     */
    public void setName(String name) {
        m_name = name;
    }

    /**
     * Gets the XML objects.
     *
     * @return the XML objects
     */
    public List<XmlObject> getXmlObjects() {
        return m_xmlObjects;
    }

    /**
     * For CSV collect it sort the objects for calculation purpose.
     * @return sorted list of this group objects.
     * @author Capgemini - pschiltz
     */
    public List<XmlObject> getCsvObjects() {
        List<XmlObject> csvObjects = new ArrayList<>(getXmlObjects());
        Collections.sort(csvObjects, new Comparator<XmlObject>() {
                @Override
                public int compare(XmlObject o1, XmlObject o2) {
                    return Integer.compare(o1.getCsvOrder(), o2.getCsvOrder());
                }
            });
        return csvObjects;
    }
    
    /**
     * Sets the XML objects.
     *
     * @param xmlObjects the new XML objects
     */
    public void setXmlObjects(List<XmlObject> xmlObjects) {
        m_xmlObjects = xmlObjects;
    }

    /**
     * Adds a new XML object.
     *
     * @param xmlObject the XML object
     */
    public void addXmlObject(XmlObject xmlObject) {
        m_xmlObjects.add(xmlObject);
    }

    /**
     * Removes a XML object.
     *
     * @param xmlObject the XML object
     */
    public void removeXmlObject(XmlObject xmlObject) {
        m_xmlObjects.remove(xmlObject);
    }

    /**
     * Removes a XML object by name.
     *
     * @param name the XML object name
     */
    public void removeObjectByName(String name) {
        for (Iterator<XmlObject> itr = m_xmlObjects.iterator(); itr.hasNext(); ) {
            XmlObject column = itr.next();
            if(column.getName().equals(name)) {
                m_xmlObjects.remove(column);
                return;
            }
        }
    }

    /**
     * Gets the resource type.
     *
     * @return the resource type
     */
    public String getResourceType() {
        return m_resourceType;
    }

    /**
     * Sets the resource type.
     *
     * @param resourceType the new resource type
     */
    public void setResourceType(String resourceType) {
        m_resourceType = resourceType;
    }

    /**
     * Gets the resource XPath.
     *
     * @return the resource XPath
     */
    public String getResourceXpath() {
        return m_resourceXpath;
    }

    /**
     * Sets the resource XPath.
     *
     * @param resourceXpath the new resource XPath
     */
    public void setResourceXpath(String resourceXpath) {
        this.m_resourceXpath = resourceXpath;
    }

    /**
     * Gets the key XPath (for resource instance).
     * 
     * @return the key XPath
     */
    public String getKeyXpath() {
        return m_keyXpath;
    }

    /**
     * Sets the key XPath.
     *
     * @param keyXpath the new key XPath
     */
    public void setKeyXpath(String keyXpath) {
        this.m_keyXpath = keyXpath;
    }

    /**
     * Gets the timestamp XPath.
     *
     * @return the timestamp XPath
     */
    public String getTimestampXpath() {
        return m_timestampXpath;
    }

    /**
     * Sets the timestamp XPath.
     *
     * @param timestampXpath the new timestamp XPath
     */
    public void setTimestampXpath(String timestampXpath) {
        this.m_timestampXpath = timestampXpath;
    }

    /**
     * Gets the timestamp format.
     *
     * @return the timestamp format
     */
    public String getTimestampFormat() {
        return m_timestampFormat;
    }

    /**
     * Sets the timestamp format.
     *
     * @param timestampFormat the new timestamp format
     */
    public void setTimestampFormat(String timestampFormat) {
        this.m_timestampFormat = timestampFormat;
    }

    /**
     * Gets the if type.
     *
     * @return the if type
     */
    public String getIfType() {
        return m_resourceType.equals("node") ? AttributeGroupType.IF_TYPE_IGNORE : AttributeGroupType.IF_TYPE_ALL;
    }

    /**
     * Gets the XML resource key.
     *
     * @return the XML resource key
     */
    public XmlResourceKey getXmlResourceKey() {
        return m_xmlResourceKey;
    }

    /**
     * Sets the XML resource key.
     *
     * @param xmlResourceKey the new XML resource key
     */
    public void setXmlResourceKey(XmlResourceKey xmlResourceKey) {
        this.m_xmlResourceKey = xmlResourceKey;
    }
    
    
    /**
     * Unique resource name for the hole group collect.
     * If not set then the resource index is used, then the resource expression finally the default resource named 'node'.
     * (A resource is a division of the collected elements for one or more names. It can be used to represent an equipment name.)
     * @return the configured resource named.
     * @author Capgemini - pschiltz
     */
    public String getCsvResourceName() { return this.m_csvResourceName; }
    /**
     * Unique resource name for the hole group collect.
     * If not set then the resource index is used, then the resource expression finally the default resource named 'node'.
     * (A resource is a division of the collected elements for one or more names. It can be used to represent an equipment name.)
     * @param name the new configured resource name.
     * @author Capgemini - pschiltz
     */
    public void setCsvResourceName(String name) { this.m_csvResourceName = name; }
    
    /**
     * CSV column index to extract the resource name for each row.
     * If two CSV row share the same resource name, then the same resource will be used.
     * When the column doesn't exist, the default resource named 'node' is used.
     * (A resource is a division of the collected elements for one or more names. It can be used to represent an equipment name.)
     * @return the configured resource index.
     * @author Capgemini - pschiltz
     */
    public Integer getCsvResourceIndex() { return this.m_csvResourceIndex; }
    /**
     * CSV column index to extract the resource name for each row.
     * If two CSV row share the same resource name, then the same resource will be used.
     * When the column doesn't exist, the default resource named 'node' is used.
     * (A resource is a division of the collected elements for one or more names. It can be used to represent an equipment name.)
     * @param index, the new resource index.
     * @author Capgemini - pschiltz
     */
    public void setCsvResourceIndex(Integer index) { this.m_csvResourceIndex = index; }
    
    /**
     * Calculable expression to retrieve the resource name.
     * It will be calculated for each row.
     * When the expression return an error, the default resource named 'node' is used.
     * (A resource is a division of the collected elements for one or more names. It can be used to represent an equipment name.)
     * @return the configured resource expression.
     * @author Capgemini - pschiltz
     */
    public String getCsvResourceExpression() { return this.m_csvResourceExpression; }
    /**
     * Calculable expression to retrieve the resource name.
     * It will be calculated for each row.
     * When the expression return an error, the default resource named 'node' is used.
     * (A resource is a division of the collected elements for one or more names. It can be used to represent an equipment name.)
     * @param name the new configured resource expression.
     * @author Capgemini - pschiltz
     */
    public void setCsvResourceExpression(String expression) { this.m_csvResourceExpression = expression; }
    
    
    /**
     * If the current date of the starting CSV collect is used for the timestamp.
     * @return if the collect date is used for timestamp.
     * @author Capgemini - pschiltz
     */
    public Boolean getCsvTimestampNow() { return this.m_csvTimestampNow; }
    /**
     * If the current date of the starting CSV collect is used for the timestamp.
     * @param useNow, the new TimestampNow rule activation.
     * @author Capgemini - pschiltz
     */
    public void setCsvTimestampNow(Boolean useNow) { this.m_csvTimestampNow = useNow; }
    
    /**
     * Row index in collected CSV file from where to extract the timestamp.
     * If not set the timestamp may be retrieved for each row.
     * @return the CSV file row index.
     * @author Capgemini - pschiltz
     */
    public Integer getCsvTimestampRowIndex() { return this.m_csvTimestampRowIndex; }
    /**
     * Row index in collected CSV file from where to extract the timestamp.
     * If not set the timestamp may be retrieved for each row.
     * @param index, the new CSV file row index to retrieve timestamp.
     * @author Capgemini - pschiltz
     */
    public void setCsvTimestampRowIndex(Integer index) { this.m_csvTimestampRowIndex = index; }
    
    /**
     * Column index in collected CSV file from where to extract the timestamp.
     * @return the CSV file column index.
     * @author Capgemini - pschiltz
     */
    public Integer getCsvTimestampColumnIndex() { return this.m_csvTimestampColumnIndex; }
    /**
     * Column index in collected CSV file from where to extract the timestamp.
     * @param index, the new CSV file column index to retrieve the timestamp.
     * @author Capgemini - pschiltz
     */
    public void setCsvTimestampColumnIndex(Integer index) { this.m_csvTimestampColumnIndex = index; }
    
    /**
     * Calculable expression on one CSV row whose result is the timestamp.
     * @return the calculable expression for timestamp.
     * @author Capgemini - pschiltz
     */
    public String getCsvTimestampExpression() { return this.m_csvTimestampExpression; }
    /**
     * Calculable expression on one CSV row whose result is the timestamp.
     * @param expression, the new calculable expression for timestamp.
     * @author Capgemini - pschiltz
     */
    public void setCsvTimestampExpression(String expression) { this.m_csvTimestampExpression = expression; }
    
    
    /**
     * Number of starting CSV file row to ignore.
     * @return Number of starting row to ignore.
     * @author Capgemini - pschiltz
     */
    public Integer getCsvTrimStart() { return this.m_csvTrimStart; }
    /**
     * Number of starting CSV file row to ignore.
     * @param index the new number of starting row to ignore.
     * @author Capgemini - pschiltz
     */
    public void setCsvTrimStart(Integer index) { this.m_csvTrimStart = index; }
    
    /**
     * Number of ending CSV file row to ignore.
     * @return Number of ending row to ignore.
     * @author Capgemini - pschiltz
     */
    public Integer getCsvTrimEnd() { return this.m_csvTrimEnd; }
    /**
     * Number of ending CSV file row to ignore.
     * @param index the new number of ending row to ignore.
     * @author Capgemini - pschiltz
     */
    public void setCsvTrimEnd(Integer index) { this.m_csvTrimEnd = index; }
    
    /**
     * Expected number of columns of the CSV file.
     * A row is ignored when it doesn't have the expected column count.
     * @return number of expected columns.
     * @author Capgemini - pschiltz
     */
    public Integer getCsvColmunsCount() { return this.m_csvColumnsCount; }
    /**
     * Expected number of columns of the CSV file.
     * A row is ignored when it doesn't have the expected column count.
     * @param count, the new expected count of columns.
     * @author Capgemini - pschiltz
     */
    public void setCsvColmunsCount(Integer count) { this.m_csvColumnsCount = count; }
    
    /**
     * Calculable expression triggering the start of CSV collect.
     * @return calculable expression triggering the collect start.
     * @author Capgemini - pschiltz
     */
    public String getCsvStartWhen() { return this.m_csvStartWhen; }
    /**
     * Calculable expression triggering the start of CSV collect.
     * @param expression, the new expression triggering collect start.
     * @author Capgemini - pschiltz
     */
    public void setCsvStartWhen(String expression) { this.m_csvStartWhen = expression; }
    
    /**
     * Calculable expression triggering the sop of CSV collect.
     * @return calculable expression triggering the collect stop.
     * @author Capgemini - pschiltz
     */
    public String getCsvStopWhen() { return this.m_csvStopWhen; }
    /**
     * Calculable expression triggering the stop of CSV collect.
     * @param expression, the new expression triggering collect stop.
     * @author Capgemini - pschiltz
     */
    public void setCsvStopWhen(String expression) { this.m_csvStopWhen = expression; }
    
    /**
     * Expression engine full class name used for calculation.
     * (Used for triggering CSV collect start/stop and for calculation xml-object value).
     * @return expression engine full class name.
     * @author Capgemini - pschiltz
     */
    public String getExpressionEngine() { return this.m_csvExpressionEngine; }
    /**
     * Expression engine full class name used for calculation.
     * (Used for triggering CSV collect start/stop and for calculation xml-object value).
     * @param engineFullClassName, the new expression engine to use.
     * @author Capgemini - pschiltz
     */
    public void setExpressionEngine(String engineFullClassName) { this.m_csvExpressionEngine = engineFullClassName; }
    
    
    /**
     * List of calculated expression rules for collect (at least the CSV collect).
     * @return this group list of expression rules.
     * @author Capgemini - pschiltz
     */
    public List<Rule> getRules() { return this.m_rules; }
    /**
     * List of calculated expression rules for collect (at least the CSV collect).
     * @param rules, the new expression rules.
     * @author Capgemini - pschiltz
     */
    public void setRules(List<Rule> rules) { this.m_rules = rules; }
    /**
     * List of calculated expression rules for collect (at least the CSV collect).
     * @param rule, the rule to be added to this group list of rules.
     * @author Capgemini - pschiltz
     */
    public void addRule(Rule rule) { this.m_rules.add(rule); }
    /**
     * List of calculated expression rules for collect (at least the CSV collect).
     * @param rule, the rule to be removed from this group list of rules.
     * @author Capgemini - pschiltz
     */
    public void removeRule(Rule rule) { this.m_rules.remove(rule); }
    /**
     * List of calculated expression rules for collect (at least the CSV collect).
     * @param ruleName, the rule name to be removed from this group list of rules (only the first matching name will be removed).
     * @author Capgemini - pschiltz
     */
    public void removeRule(String ruleName) {
        for (Rule rule : this.m_rules)
            if (rule.getName().equals(ruleName)) {
                this.m_rules.remove(rule);
                break;
            }
    }
    
    /**
     * Clean previous rules results.
     * @author Capgemini - pschiltz
     */
    public void cleanRulesResult() {
        for (Rule rule : this.getRules())
            rule.clearResult();
    }
    
    
    /**
     * @return if this group contain (one or more) CSV object/rule needing to be calculated with an expression.
     * @author Capgemini - pschiltz
     */
    @SuppressWarnings("boxing")
    public boolean hasCsvExpressions() {
        // When there are expression rules
        if (!this.m_rules.isEmpty())
            return true;
        
        // When there are expression ressource
        if (this.m_csvResourceName == null && this.m_csvResourceIndex == null && this.m_csvResourceExpression != null && !this.m_csvResourceExpression.isEmpty())
            return true;
        
        // When there are expression timestamp
        if (!(this.m_csvTimestampNow != null && this.m_csvTimestampNow))
            if (this.m_csvTimestampRowIndex != null && this.m_csvTimestampRowIndex >= 0)
                if (!(this.m_csvTimestampColumnIndex != null && this.m_csvTimestampColumnIndex >= 0))
                    if (this.m_csvTimestampExpression != null && !this.m_csvTimestampExpression.isEmpty())
                        return true;
        
        // When there are calculated objects
        for (XmlObject object : this.m_xmlObjects)
            if (object.isCsvExpression())
                return true;
        
        // Otherwise there aren't calculated expression
        return false;
    }
    
    
    /**
     * Checks for multiple resource key.
     *
     * @return true, if successful
     */
    public boolean hasMultipleResourceKey() {
        return m_xmlResourceKey != null && !m_xmlResourceKey.getKeyXpathList().isEmpty();
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(XmlGroup obj) {
        return new CompareToBuilder()
        .append(getName(), obj.getName())
        .append(getResourceType(), obj.getResourceType())
        .append(getResourceXpath(), obj.getResourceXpath())
        .append(getKeyXpath(), obj.getKeyXpath())
        .append(getXmlObjects().toArray(OF_XML_OBJECTS), obj.getXmlObjects().toArray(OF_XML_OBJECTS))
        .toComparison();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof XmlGroup) {
            XmlGroup other = (XmlGroup) obj;
            return new EqualsBuilder()
            .append(getName(), other.getName())
            .append(getResourceType(), other.getResourceType())
            .append(getResourceXpath(), other.getResourceXpath())
            .append(getKeyXpath(), other.getKeyXpath())
            .append(getXmlObjects().toArray(OF_XML_OBJECTS), other.getXmlObjects().toArray(OF_XML_OBJECTS))
            .isEquals();
        }
        return false;
    }
}
