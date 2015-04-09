package org.opennms.netmgt.config.datacollection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.opennms.core.xml.ValidateUsing;


/**
 * A KPI object, holding data for KPI calculation.
 * 
 * @author Capgemini - pschiltz
 */
@XmlRootElement(name="kpiObj", namespace="http://xmlns.opennms.org/xsd/config/datacollection")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder={"m_alias", "m_expression", "m_type", "m_instance", "m_oid", "m_maxval", "m_minval", "m_order", "m_visit", "m_engine"})
@ValidateUsing("datacollection-config.xsd")
public class KpiObj {
	/**
	 * 
	 */
	@SuppressWarnings("unused")
    private static final long serialVersionUID = -7133492973683810404L;

    /**
     * a human readable name for the object (such as "ifOctetsIn"). NOTE: This
     * value is used as the RRD file name and data source name. RRD only
     * supports data source names up to 19 chars in length. If the SNMP data
     * collector encounters an alias which exceeds 19 characters it will be
     * truncated.
     */
    @XmlAttribute(name="alias", required=true)
    private String m_alias;
	
    /**
     * an expression to be evaluated.
     * Example: "${mibObjA} / (${mibObjA} + ${mibObjB})"
     */
    @XmlAttribute(name="exp", required=true)
    private String m_expression;
    
    /**
     * SNMP data type SNMP supported types: counter, gauge, timeticks,
     * integer, octetstring, string. The SNMP type is mapped to one of two RRD
     * supported data types COUNTER or GAUGE, or the string.properties file.
     * The mapping is as follows: SNMP counter -&gt; RRD COUNTER; SNMP gauge,
     * timeticks, integer, octetstring -&gt; RRD GAUGE; SNMP string -&gt; String
     * properties file
     */
    @XmlAttribute(name="type", required=true)
    private String m_type;
    
    /**
     * optional instance identifier. Only valid instance identifier values are a
     * positive integer value or the keyword "ifIndex" which indicates that
     * the ifIndex of the interface is to be substituted for the instance
     * value for each interface the oid is retrieved for.
     */
    @XmlAttribute(name="instance", required=false)
    private String m_instance;
    
    /**
     * optional 'Fake' mib object identifier, if not set the value .1.1.1.1 is used.
     */
    @XmlAttribute(name="oid", required=false)
    private String m_oid;
    
    /**
     * optional Maximum Value. In order to correctly manage counter wraps, it is
     * possible to add a maximum value for a collection. For example, a 32-bit
     * counter would have a max value of 4294967295.
     */
    @XmlAttribute(name="maxval", required=false)
    private String m_maxval;

    /**
     * optional Minimum Value. For completeness, adding the ability to use a minimum
     * value.
     */
    @XmlAttribute(name="minval", required=false)
    private String m_minval;
    
    /**
     * optional order calculation between KPI. Default to 0.
     * i.e. If a KPI 'k' depend on KPI 'm' result, then 'k' must have a greater order than 'm'.
     * Otherwise alphabetical order is used.
     */
    @XmlAttribute(name="order", required=false)
    private int m_order;
    
    /**
     * optional flag to manage this KPI, if it's value is persisted (in RRD file) or not.
     * By default it's persisted.
     */
    @XmlAttribute(name="visit", required=false)
    private boolean m_visit;
    
    /**
     * optional expression engine full class name implementing IKpiCalculationEngine interface.
     * If not set, a default JavaScript engine will be used to evaluate the expression.
     */
    @XmlAttribute(name="engine", required=false)
    private String m_engine;
    
    
    public KpiObj() {
        this.m_order = 0;
        this.m_visit = true;
    }
    
    public KpiObj(String alias, String expression, String type) {
    	this();
    	this.m_alias 		= alias == null 		? null : alias.intern();
    	this.m_expression 	= expression == null 	? null : expression.intern();
    	this.m_type 		= type == null 			? null : type.intern();
    }
    
    /**
     * a human readable name for the object (such as "ifOctetsIn"). NOTE: This
     * value is used as the RRD file name and data source name. RRD only
     * supports data source names up to 19 chars in length. If the SNMP data
     * collector encounters an alias which exceeds 19 characters it will be
     * truncated.
     */
    public String getAlias() 						{ return this.m_alias; }
    public void setAlias(String alias) 				{ this.m_alias = alias; }
    
    /**
     * an expression to be evaluated.
     * Example: "${mibObjA} / (${mibObjA} + ${mibObjB})"
     */
    public String getExpression() 					{ return this.m_expression; }
    public void setExpression(String expression) 	{ this.m_expression = expression; }
    
    /**
     * SNMP data type SNMP supported types: counter, gauge, timeticks,
     * integer, octetstring, string. The SNMP type is mapped to one of two RRD
     * supported data types COUNTER or GAUGE, or the string.properties file.
     * The mapping is as follows: SNMP counter -&gt; RRD COUNTER; SNMP gauge,
     * timeticks, integer, octetstring -&gt; RRD GAUGE; SNMP string -&gt; String
     * properties file
     */
    public String getType() 						{ return this.m_type; }
    public void setType(String type) 				{ this.m_type = type; }
    
    /**
     * optional instance identifier. Only valid instance identifier values are a
     * positive integer value or the keyword "ifIndex" which indicates that
     * the ifIndex of the interface is to be substituted for the instance
     * value for each interface the oid is retrieved for.
     */
    public String getInstance()                     { return this.m_instance; }
    public void setInstance(String instance)        { this.m_instance = instance; }
    
    /**
     * optional 'Fake' mib object identifier, if not set the value .1.1.1.1 is used.
     */
    public String getOid() 							{ return this.m_oid; }
    public void setOid(String oid) 					{ this.m_oid = oid; }
    
    /**
     * optional Maximum Value. In order to correctly manage counter wraps, it is
     * possible to add a maximum value for a collection. For example, a 32-bit
     * counter would have a max value of 4294967295.
     */
    public String getMaxVal() 						{ return this.m_maxval; }
    public void setMaxVal(String maxVal) 			{ this.m_maxval = maxVal; }
    
    /**
     * optional Minimum Value. For completeness, adding the ability to use a minimum
     * value.
     */
    public String getMinVal() 						{ return this.m_minval; }
    public void setMinVal(String minVal) 			{ this.m_minval = minVal; }
    
    /**
     * optional order calculation between KPI. Default to 0.
     * i.e. If a KPI 'k' depend on KPI 'm' result, then 'k' must have a greater order than 'm'.
     * Otherwise alphabetical order is used.
     */
    public int getOrder()                           { return this.m_order; }
    public void setOrder(int order)                 { this.m_order = order; }
    
    /**
     * optional flag to manage this KPI, if it's persisted (in RRD file) or not.
     * By default it's persisted.
     */
    public boolean getVisit()                       { return this.m_visit; }
    public void setVisit(boolean visit)             { this.m_visit = visit; }
    
    /**
     * optional expression engine full class name implementing IKpiCalculationEngine interface.
     * If not set, a default JavaScript engine will be used to evaluate the expression.
     */
    public String getEngine()                       { return this.m_engine; }
    public void setEngine(String engine)            { this.m_engine = engine; }
    
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((m_alias == null) 		? 0 : m_alias.hashCode());
        result = prime * result + ((m_expression == null) 	? 0 : m_expression.hashCode());
        result = prime * result + ((m_maxval == null) 		? 0 : m_maxval.hashCode());
        result = prime * result + ((m_minval == null) 		? 0 : m_minval.hashCode());
        result = prime * result + ((m_oid == null) 			? 0 : m_oid.hashCode());
        result = prime * result + ((m_type == null) 		? 0 : m_type.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        
        if (!(obj instanceof KpiObj)) return false;
        final KpiObj other = (KpiObj) obj;
        
        if (this.getAlias().equals(other.getAlias())) return true;
        
        return false;
    }
    
    @Override
    public String toString() {
        return "KpiObj [alias=" + m_alias + ", expression=" + m_expression + ", type=" + m_type + ", instance=" + m_instance + ", oid=" + m_oid + ", maxval=" + m_maxval + ", minval=" + m_minval + ", order=" + m_order + ", visit=" + m_visit + "]";
    }
    
}
