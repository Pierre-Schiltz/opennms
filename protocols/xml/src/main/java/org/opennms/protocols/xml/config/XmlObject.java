/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2010-2014 The OpenNMS Group, Inc.
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
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;

/**
 * The Class XmlObject.
 * 
 * @author <a href="mailto:agalue@opennms.org">Alejandro Galue</a>
 */
@XmlRootElement(name="xml-object")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlObject implements Serializable, Comparable<XmlObject> {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -774378322863486535L;

    /** The object name (or alias). */
    @XmlAttribute(name="name", required=true)
    private String m_name;

    /** The data type. */
    @XmlAttribute(name="type", required=true)    
    private String m_dataType;

    /** The XPath. */
    @XmlAttribute(name="xpath", required=true)
    private String m_xpath;

    /**
     * The CSV file column index where to extract data.
     * @author Capgemini - pschiltz
     */
    @XmlAttribute(name="csv-index", required=false)
    private Integer m_csvIndex;
    
    /**
     * The calculation expression to retrieve this object value from CSV file.
     * @author Capgemini - pschiltz
     */
    @XmlAttribute(name="csv-expression", required=false)
    private String m_csvExpression;
    
    /**
     * Order between calculated objects from CSV from collect.
     * @author Capgemini - pschiltz
     */
    @XmlAttribute(name="csv-order", required=false)
    private int m_csvOrder;
    
    
    /**
     * Instantiates a new XML object.
     */
    public XmlObject() {
        super();
        this.m_csvOrder = 0;
    }

    /**
     * Instantiates a new XML object.
     *
     * @param name the object name
     * @param dataType the data type
     */
    public XmlObject(String name, String dataType) {
        this();
        this.m_name = name;
        this.m_dataType = dataType;
    }

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
     * Gets the data type.
     *
     * @return the data type
     */
    public String getDataType() {
        return m_dataType;
    }

    /**
     * Sets the data type.
     *
     * @param dataType the new data type
     */
    public void setDataType(String dataType) {
        m_dataType = dataType;
    }


    /**
     * Gets the XPath.
     *
     * @return the XPath
     */
    public String getXpath() {
        return m_xpath;
    }

    /**
     * Sets the XPath.
     *
     * @param xpath the new XPath
     */
    public void setXpath(String xpath) {
        m_xpath = xpath;
    }
    
    /**
     * The CSV file column index where to extract data.
     * @return the column index to collect.
     * @author Capgemini - pschiltz
     */
    public Integer getCsvIndex() { return this.m_csvIndex; }
    /**
     * The CSV file column index where to extract data.
     * @param index, the new column index.
     * @author Capgemini - pschiltz
     */
    public void setCsvIndex(Integer index) { this.m_csvIndex = index; }
    
    /**
     * The calculation expression to retrieve this object value from CSV file.
     * @return the expression calulcating this object value.
     * @author Capgemini - pschiltz
     */
    public String getCsvExpression() { return this.m_csvExpression; }
    /**
     * The calculation expression to retrieve this object value from CSV file.
     * @param expression, the new expression.
     * @author Capgemini - pschiltz
     */
    public void setCsvExpression(String expression) { this.m_csvExpression = expression; }
    
    /**
     * Order between calculated objects from CSV from collect.
     * @return order of this object in calculation process.
     * @author Capgemini - pschiltz
     */
    public int getCsvOrder() { return this.m_csvOrder; }
    /**
     * Order between calculated objects from CSV from collect.
     * @param order, the new order of this object.
     * @author Capgemini - pschiltz
     */
    public void setCsvOrder(int order) { this.m_csvOrder = order; }
    
    
    /**
     * @return if this CSV object will be calculated using it's expression.
     * @author Capgemini - pschiltz
     */
    public boolean isCsvExpression() {
        return this.m_csvExpression != null && !this.m_csvExpression.isEmpty();
    }
    
    
    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(XmlObject obj) {
        return new CompareToBuilder()
        .append(getName(), obj.getName())
        .append(getDataType(), obj.getDataType())
        .append(getXpath(), obj.getXpath())
        .toComparison();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof XmlObject) {
            XmlObject other = (XmlObject) obj;
            return new EqualsBuilder()
            .append(getName(), other.getName())
            .append(getDataType(), other.getDataType())
            .append(getXpath(), other.getXpath())
            .isEquals();
        }
        return false;
    }
}
