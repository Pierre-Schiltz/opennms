/**
 * 
 */
package org.opennms.netmgt.collectd;

import org.opennms.netmgt.collection.api.CollectionAttribute;
import org.opennms.netmgt.collection.api.CollectionSetVisitor;
import org.opennms.netmgt.collection.api.CollectionVisitable;
import org.opennms.netmgt.config.MibObject;
import org.opennms.netmgt.config.datacollection.KpiObj;
import org.opennms.netmgt.snmp.SnmpValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to store KPI related parameters.
 * One SnmpKPI is used to create one SnmpAttribute from a
 * calculation expression using other SnmpAttributes of the
 * same SnmpAttributeGroup.
 * 
 * @author Capgemini - pschiltz
 *
 */
public class SnmpKpi implements CollectionVisitable, Comparable<SnmpKpi> {
    
    /**
     * Logging utility parameter.
     */
    private static final Logger LOG = LoggerFactory.getLogger(SnmpKpi.class);
    
    // I/   CONTAINED PARAMETERS
    
    // Mandatory business parameters
    private String      m_alias;
    private String      m_expression;
    private String      m_type;
    
    // Optional business parameters
    private String      m_instance;
    private String      m_oid;
    private String      m_minVal;
    private String      m_maxVal;
    
    // Optional execution parameters
    private int         m_order;
    private boolean     m_visit;
    private String      m_expressionEngine;
    
    // Calculated parameter
    private SnmpValue   m_value;

    public String getAlias()                            { return m_alias; }
    public void setAlias(String alias)                  { this.m_alias = alias; }
    public String getExpression()                       { return m_expression; }
    public void setExpression(String expression)        { this.m_expression = expression; }
    public String getAttributeType()                    { return m_type; }
    public void setAttributeType(String attributeType)  { this.m_type = attributeType; }

    public String getInstance()                         { return m_instance; }
    public void setInstance(String instance)            { this.m_instance = instance; }
    public String getOid()                              { return m_oid; }
    public void setOid(String oid)                      { this.m_oid = oid; }
    public String getMinVal()                           { return m_minVal; }
    public void setMinVal(String minVal)                { this.m_minVal = minVal; }
    public String getMaxVal()                           { return m_maxVal; }
    public void setMaxVal(String maxVal)                { this.m_maxVal = maxVal; }
    
    public int getOrder()                               { return m_order; }
    public void setOrder(int order)                     { this.m_order = order; }
    public boolean getVisit()                           { return m_visit; }
    public void setVisit(boolean visit)                 { this.m_visit = visit; }
    public String getExpressionEngine()                 { return m_expressionEngine; }
    public void setExpressionEngine(String expressionEngine) { this.m_expressionEngine = expressionEngine; }
    
    public SnmpValue getValue()                         { return m_value; }
    public void setValue(SnmpValue val)                 { m_value = val; }
    
    // Following contained parameters are retrieved from referenced parameters
    private ResourceType m_resType;
    private String       m_collectionName;
    private String       m_ifType;

    
    public ResourceType getResType()                    { return m_resType; }
    public void setResType(ResourceType resType)        { this.m_resType = resType; }
    public String getCollectionName()                   { return m_collectionName; }
    public void setCollectionName(String collectionName){ this.m_collectionName = collectionName; }

    
    public String getIfType()                           { return m_ifType; }
    public void setIfType(String ifType)                { this.m_ifType = ifType; }

    
    // II/  REFERENCED PARAMETERS
    
    private SnmpAttributeGroup m_group;
    public SnmpAttributeGroup getGroup()                { return this.m_group; }
    public void setGroup(SnmpAttributeGroup group)      { this.m_group = group; }
    
    private SnmpAttribute m_attribute;
    public SnmpAttribute getAttribute()                 { return this.m_attribute; }
    public void setAttribute(SnmpAttribute attribute)   { this.m_attribute = attribute; }
    
    
    // III/ CONSTRUCTORS
    
    public SnmpKpi(String alias, String expression, String type) {
        this.setAlias(alias);
        this.setExpression(expression);
        this.setAttributeType(type);
        
        this.setOrder(0);
        this.setVisit(true);
    }
    
    public SnmpKpi(KpiObj kpi) {
        this.setAlias(kpi.getAlias());
        this.setExpression(kpi.getExpression());
        this.setAttributeType(kpi.getType());
        
        this.setInstance(kpi.getInstance());
        
        this.setOrder(kpi.getOrder());
        this.setVisit(kpi.getVisit());
        this.setExpressionEngine(kpi.getEngine());
    }
    
    public SnmpKpi(SnmpAttributeGroup group, String alias, String expression, String type) {
        this(alias, expression, type);
        this.setGroup(group);
    }
    
    // IV/  METHODS
    
    /**
     * Create a 'fake' CollectionAttribute object to store the calculated KPI value.
     * 
     * @param visitor {@link org.opennms.netmgt.collection.api.CollectionSetVisitor}
     * @author Capgemini - pschiltz
     */
    public void createKpiAttribute(CollectionSetVisitor visitor) {
        // When attribute already exist (it should not)
        if (this.getAttribute() != null) {
            this.setAttribute(null);
        }
        
        // Check there aren't duplicate (on the Alias)
        for (CollectionAttribute attr : this.getGroup().getAttributes()) {
            if (this.getAlias().equals(attr.getName())) {
                LOG.error("KPI Alias {} already used by another attribute", this.getAlias());
                return;
            }
        }
        
        if (this.getAttribute() == null) {
            // Create needed parameter for the 'fake' attribute
            NumericAttributeType kpiType = new NumericAttributeType(this.getResType(), this.getCollectionName(), this.createKpiMibObject(), this.getGroup().getGroupType());
            // Create the 'fake' KPI attribute
            this.setAttribute(new SnmpAttribute(this.getGroup().getResource(), kpiType, this.getValue()));
            
            LOG.debug("created 'fake' attribute to store KPI={} result={}: SnmpAttribute={}", this.getAlias(), this.getValue(), this.getAttribute());
            
            // Add the Attribute to the Group
            this.getGroup().addAttribute(this.getAttribute());
            
            // Visit the created attribute
            try {
                this.getAttribute().visit(visitor);
            } catch (Exception e) {
                LOG.error("exception occured while visiting 'fake' attribute for KPI={}: {}", this.getAlias(), e);
            }
        }
    }
    
    /**
     * Creating a 'fake' MIB object used to store the calculated KPI value into a CollectionAttribute.
     * 
     * @return a 'fake' mibObject
     * @author Capgemini - pschiltz
     */
    private MibObject createKpiMibObject() {
        MibObject kpiMib = new MibObject();
        
        // Set default values
        if (this.getInstance() == null)
            this.setInstance(this.getGroup().getResource() == null || this.getGroup().getResource().getInstance() == null ? "0" : this.getGroup().getResource().getInstance());
        if (this.getOid() == null)
            this.setOid(".1.1.1.1");
        
        kpiMib.setAlias(this.getAlias());
        kpiMib.setGroupIfType(this.getIfType());
        kpiMib.setGroupName(this.getGroup().getName());
        kpiMib.setInstance(this.getInstance());
        kpiMib.setType(this.getAttributeType());
        kpiMib.setOid(this.getOid());
        kpiMib.setMinval(this.getMinVal());
        kpiMib.setMaxval(this.getMaxVal());
        
        LOG.debug("created 'fake' mibObject to store KPI={} result={}: MibObject={}", this.getAlias(), this.getValue(), kpiMib);
        
        return kpiMib;
    }
    
    
    @Override
    public void visit(CollectionSetVisitor visitor) {
        // Create or retrieve if it exist the KPI 'fake' attribute (when it's configured)
        if (this.getVisit())
            createKpiAttribute(visitor);
    }
    
    /**
     * Delete the 'fake' CollectionAttribute created to store the KPI result. And clean the result value.
     * @author Capgemini - pschiltz
     */
    public void clean() {
        
        if (this.getAttribute() != null) {
            LOG.debug("delete 'fake' attribute {}", this.getAttribute());
            // Remove the attribute from the Group
            this.getGroup().getAttributes().remove(this.getAttribute());
            
            // Set it's value to null
            this.setAttribute(null);
        }
        
        // Set the result value to null
        this.setValue(null);
    }
    
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        
        if (!(obj instanceof SnmpKpi)) return false;
        final SnmpKpi other = (SnmpKpi) obj;
        
        if (this.m_alias != null && this.m_alias.equals(other.m_alias)) return true;
        
        return false;
    }
    
    @Override
    public String toString() {
        return "SnmpKpi [alias=" + m_alias + ", expression=" + m_expression + ", type=" + m_type + ", instance=" + m_instance + ", oid=" + m_oid + ", maxval=" + m_maxVal + ", minval=" + m_minVal + ", order=" + m_order + ", visit=" + m_visit + "]";
    }
    
    /**
     * Comparison method used to order the KPI for calculation.
     * It's sorted by order, then if orders are equal it's sorted by alias.
     */
    @Override
    public int compareTo(SnmpKpi o) {
        if (this.getOrder() == o.getOrder()) return this.getAlias().compareTo(o.getAlias()) ;
        
        return Integer.compare(this.getOrder(), o.getOrder());
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((m_alias == null)        ? 0 : m_alias.hashCode());
        result = prime * result + ((m_expression == null)   ? 0 : m_expression.hashCode());
        result = prime * result + ((m_type == null)         ? 0 : m_type.hashCode());
        result = prime * result + ((m_instance == null)     ? 0 : m_instance.hashCode());
        result = prime * result + ((m_oid == null)          ? 0 : m_oid.hashCode());
        result = prime * result + ((m_maxVal == null)       ? 0 : m_maxVal.hashCode());
        result = prime * result + ((m_minVal == null)       ? 0 : m_minVal.hashCode());
        result = prime * result + ((m_group == null)        ? 0 : m_group.hashCode());
        return result;
    }
    
}
