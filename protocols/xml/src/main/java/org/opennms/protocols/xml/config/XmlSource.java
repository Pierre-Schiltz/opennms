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
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;

/**
 * The Class XmlSource.
 * 
 * @author <a href="mailto:agalue@opennms.org">Alejandro Galue</a>
 */
@XmlRootElement(name="xml-source")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlSource extends XmlGroups implements Serializable, Comparable<XmlSource> {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -9220561601381984080L;

    /** The Constant OF_XML_GROUPS. */
    @XmlTransient
    private static final XmlGroup[] OF_XML_GROUPS = new XmlGroup[0];

    /** Import Groups List. */
    @XmlElement(name="import-groups", required=false)
    private List<String> m_importGroupsList = new ArrayList<String>();

    /** The request object. */
    @XmlElement(name="request", required=false)
    private Request m_request;

    /** The source URL. */
    @XmlAttribute(name="url", required=true)
    private String m_url;
    
    /**
     * Specific separator to use when collecting data from CSV file.
     * @author Capgemini - pschiltz
     */
    @XmlAttribute(name="csv-separator", required=false)
    private Character m_csvSeparator;
    
    /**
     * Encoding for CSV file.
     * @author Capgemini - pschiltz
     */
    @XmlAttribute(name="csv-encoding", required=false)
    private String m_csvEncoding;
    
    /**
     * Specific quote character to use when collecting data from CSV file.
     * @author Capgemini - pschiltz
     */
    @XmlAttribute(name="csv-quote-char", required=false)
    private Character m_csvQuoteChar;
    
    /**
     * If caracaters ouside quotes are ignored when collecting data from CSV file.
     * @author Capgemini - pschiltz
     */
    @XmlAttribute(name="csv-strict-quote", required=false)
    private Boolean m_csvStrictQuote;
    
    /**
     * Specific escaping character (to esacape separator and quote characters) to use when collecting data from CSV file.
     * @author Capgemini - pschiltz
     */
    @XmlAttribute(name="csv-escape-char", required=false)
    private Character m_csvEscapeChar;
    
    /**
     * If leading white space caracaters before quotes are ignored when collecting data from CSV file.
     * @author Capgemini - pschiltz
     */
    @XmlAttribute(name="csv-trim-white-space", required=false)
    private Boolean m_csvTrimWhiteSpace;
    
    
    /**
     * Instantiates a new XML source.
     */
    public XmlSource() {
    }

    /**
     * Gets the URL.
     *
     * @return the URL
     */
    public String getUrl() {
        return m_url;
    }

    /**
     * Sets the URL.
     *
     * @param url the new URL
     */
    public void setUrl(String url) {
        m_url = url;
    }

    /**
     * Gets the request.
     *
     * @return the request
     */
    public Request getRequest() {
        return m_request;
    }

    /**
     * Sets the request.
     *
     * @param request the new request
     */
    public void setRequest(Request request) {
        this.m_request = request;
    }

    /**
     * Gets the import groups list.
     *
     * @return the import groups list
     */
    public List<String> getImportGroupsList() {
        return m_importGroupsList;
    }

    /**
     * Sets the import groups list.
     *
     * @param importGroupsList the new import groups list
     */
    public void setImportGroupsList(List<String> importGroupsList) {
        this.m_importGroupsList = importGroupsList;
    }
    
    /**
     * Specific separator to use when collecting data from CSV file.
     * @return this group current CSV separator.
     * @author Capgemini - pschiltz
     */
    public Character getCsvSeparator() { return this.m_csvSeparator; }
    /**
     * Specific separator to use when collecting data from CSV file.
     * @param separator, the new CSV separator.
     * @author Capgemini - pschiltz
     */
    public void setCsvSeparator(Character separator) { this.m_csvSeparator = separator; }
    
    /**
     * Encoding for CSV file.
     * @return this group current CSV file encoding.
     * @author Capgemini - pschiltz
     */
    public String getCsvEncoding() { return this.m_csvEncoding; }
    /**
     * Encoding for CSV file.
     * @param encoding, the new CSV file encoding.
     * @author Capgemini - pschiltz
     */
    public void setCsvEncoding(String encoding) { this.m_csvEncoding = encoding; }
    
    /**
     * Specific quote character to use when collecting data from CSV file.
     * @return the quote character used.
     * @author Capgemini - pschiltz
     */
    public Character getCsvQuoteChar() { return this.m_csvQuoteChar; }
    /**
     * Specific quote character to use when collecting data from CSV file.
     * @param quote, the new quote character.
     * @author Capgemini - pschiltz
     */
    public void setCsvQuoteChar(Character quote) { this.m_csvQuoteChar = quote; }
    
    /**
     * If caracaters ouside quotes are ignored when collecting data from CSV file.
     * @return if caracaters ouside quotes are ignored.
     * @author Capgemini - pschiltz
     */
    public Boolean getCsvStrictQuote() { return this.m_csvStrictQuote; }
    /**
     * If caracaters ouside quotes are ignored when collecting data from CSV file.
     * @param strictQuote, new activation value for strict quote rule.
     * @author Capgemini - pschiltz
     */
    public void setCsvStrictQuote(Boolean strictQuote) { this.m_csvStrictQuote = strictQuote; }
    
    /**
     * Specific escaping character (to esacape separator and quote characters) to use when collecting data from CSV file.
     * @return the escape character used.
     * @author Capgemini - pschiltz
     */
    public Character getCsvEscapeChar() { return this.m_csvEscapeChar; }
    /**
     * Specific escaping character (to esacape separator and quote characters) to use when collecting data from CSV file.
     * @param escape, the new escape character.
     * @author Capgemini - pschiltz
     */
    public void setCsvEscapeChar(Character escape) { this.m_csvEscapeChar = escape; }
    
    /**
     * If leading white space caracaters before quotes are ignored when collecting data from CSV file.
     * @return 
     * @author Capgemini - pschiltz
     */
    public Boolean getCsvTrimWhiteSpace() { return this.m_csvTrimWhiteSpace; }
    /**
     * If leading white space caracaters before quotes are ignored when collecting data from CSV file.
     * @param trimWhiteSpace, new activation value for trim of leading space rule.
     * @author Capgemini - pschiltz
     */
    public void setCsvTrimWhiteSpace(Boolean trimWhiteSpace) { this.m_csvTrimWhiteSpace = trimWhiteSpace; }
    
    
    /**
     * Checks for import groups.
     *
     * @return true, if successful
     */
    public boolean hasImportGroups() {
        return m_importGroupsList != null && !m_importGroupsList.isEmpty();
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(XmlSource obj) {
        return new CompareToBuilder()
        .append(getUrl(), obj.getUrl())
        .append(getXmlGroups().toArray(OF_XML_GROUPS), obj.getXmlGroups().toArray(OF_XML_GROUPS))
        .toComparison();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof XmlSource) {
            XmlSource other = (XmlSource) obj;
            return new EqualsBuilder()
            .append(getUrl(), other.getUrl())
            .append(getXmlGroups().toArray(OF_XML_GROUPS), other.getXmlGroups().toArray(OF_XML_GROUPS))
            .isEquals();
        }
        return false;
    }

}
