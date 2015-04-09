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

package org.opennms.netmgt.collectd;

import java.util.HashMap;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.script.ScriptException;
import org.opennms.netmgt.collection.api.AttributeGroup;
import org.opennms.netmgt.collection.api.AttributeGroupType;
import org.opennms.netmgt.collection.api.CollectionAttribute;
import org.opennms.netmgt.collection.api.CollectionResource;
import org.opennms.netmgt.collection.api.CollectionSetVisitor;
import org.opennms.netmgt.snmp.SnmpUtils;
import org.opennms.netmgt.snmp.SnmpValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This attribute group overrides {@link #doShouldPersist()} so that persistence
 * can be enabled if the SNMP ifType matches the value of ifType on the 
 * {@link CollectionResource}.
 */
public class SnmpAttributeGroup extends AttributeGroup {
    
    /**
     * Logging utility parameter.
     */
    private static final Logger LOG = LoggerFactory.getLogger(SnmpAttributeGroup.class);
    
    /**
     * List of KPI to be calculated using this group attributes.
     * @author Capgemini - pschiltz
     */
    private SortedSet<SnmpKpi>      m_kpis;
    
    /**
     * Private parameter to hold the temporary default map of attribute <name,value>.
     * @author Capgemini - pschiltz
     */
    private HashMap<String,Object>          attributesAliasValues;
    /**
     * Private parameter to hold the temporary default map of available <isntance,resource>.
     * @author Capgemini - pschiltz
     */
    private HashMap<String,ResourceType>    defaultResourceTypeInstance;
    /**
     * Private parameter to hold the temporary default collection name.
     * @author Capgemini - pschiltz
     */
    private String                          defaultCollectionName;
    /**
     * Private parameter to hold the temporary default instance name.
     * @author Capgemini - pschiltz
     */
    private String                          defaultInstance;
    /**
     * Private parameter to hold the temporary default resource type.
     * @author Capgemini - pschiltz
     */
    private ResourceType                    defaultResourceType;
    /**
     * Private parameter to hold the temporary default attribute type.
     * @author Capgemini - pschiltz
     */
    private String                          defaultType;
    
    
    /**
     * If not initialized, the KPI list is retrieved from the configuration.
     * @return The initialized KPI list.
     * @author Capgemini - pschiltz
     */
    public Set<SnmpKpi> getKpis() {
        // Check if the KPI list is initialized
        if (this.m_kpis == null) {
            // Initialization of parameters
            this.m_kpis                         = new TreeSet<>();
            OnmsSnmpCollection  collector       = null;
            String              collectionName  = null;
            
            // Retrieve the SNMP collector and collection
            for (CollectionAttribute attr : this.getAttributes())
                if (attr.getAttributeType() instanceof SnmpAttributeType)
                    if (((SnmpAttributeType) attr.getAttributeType()).getResourceType() != null)
                        if (((SnmpAttributeType) attr.getAttributeType()).getResourceType().getCollection() != null) {
                            // Retrieve values
                            if (collector == null) collector = ((SnmpAttributeType) attr.getAttributeType()).getResourceType().getCollection();
                            if (collectionName == null) collectionName = ((SnmpAttributeType) attr.getAttributeType()).getCollectionName();
                            // Stop loop if values have been retrieved
                            if (collector != null && collectionName != null) break;
                        }
            
            // Retrieve the KPI list for this group from the configuration
            if (collector != null && collectionName != null && this.getName() != null)
                // Retrieve the KPI and loop on them
                for (SnmpKpi kpi : collector.loadKpiGroup(collectionName, this.getName()))
                    // Add each configuration KPI
                    if (kpi != null) this.addKpi(kpi);
        }
        
        return this.m_kpis;
    }
    
    /**
     * Add a KPI to the group.
     * @param kpi a {@link org.opennms.netmgt.collectd.SnmpKpi} to be added to the current group.
     * @author Capgemini - pschiltz
     */
    public void addKpi(SnmpKpi kpi) {
        // Set the group of the KPI
        kpi.setGroup(this);
        // Add the KPI to the group
        this.m_kpis.add(kpi);
    }
    
    
	public SnmpAttributeGroup(SnmpCollectionResource resource, AttributeGroupType groupType) {
		super(resource, groupType);
	}
	
	/**
	 * 
	 */
	@Override
	protected boolean doShouldPersist() {
		boolean shouldPersist = super.doShouldPersist();

		if (shouldPersist) {
			return shouldPersist;
		} else {
			String type = String.valueOf(((SnmpCollectionResource)getResource()).getSnmpIfType());

			if (type.equals(getIfType())) return true;

			StringTokenizer tokenizer = new StringTokenizer(getIfType(), ",");
			while(tokenizer.hasMoreTokens()) {
				if (type.equals(tokenizer.nextToken()))
					return true;
			}
			return false;
		}
	}
	
	
	/**
	 * Visit method from visitor pattern.
	 * Override to manage the visit-able attributes stored in this group.
	 * 
	 * @param visitor {@link org.opennms.netmgt.collection.api.CollectionSetVisitor}
	 * @author Modified by Capgemini - pschiltz
	 */
    @Override
    public void visit(CollectionSetVisitor visitor) {
        LOG.debug("Visiting Group {}", this);
        LOG.debug("Visiting Object Group {}", ((Object)this).toString());
        visitor.visitGroup(this);
        
        // Test if there are KPI
        if (this.getKpis().isEmpty())
            visitGroupWithoutKpi(visitor);
        else
            visitGroupWithKpi(visitor);
        
        visitor.completeGroup(this);
        
        // Clean KPI for next iteration
        cleanKpi();
    }
    
    /**
     * Visit method from visitor pattern.
     * This method doesn't manage KPI contained in this group.
     * 
     * @param visitor {@link org.opennms.netmgt.collection.api.CollectionSetVisitor}
     * @author Capgemini - pschiltz
     */
    protected void visitGroupWithoutKpi(CollectionSetVisitor visitor) {
        LOG.debug("Visiting Group without kpi {}", this);
        for(CollectionAttribute attr : getAttributes())
            attr.visit(visitor);
                
        return;
    }
    
    /**
     * Visit method from visitor pattern.
     * This method manage KPI contained in this group.
     * 
     * @param visitor {@link org.opennms.netmgt.collection.api.CollectionSetVisitor}
     * @author Capgemini - pschiltz
     */
    protected void visitGroupWithKpi(CollectionSetVisitor visitor) {
        LOG.debug("Visiting Group with kpi {}", this);
        
        // 1) Initialize parameters
        this.attributesAliasValues       = new HashMap<>();
        this.defaultResourceTypeInstance = new HashMap<>();
        this.defaultCollectionName       = null;
        this.defaultInstance             = this.getResource() == null ? null : this.getResource().getResourceTypeName();
        this.defaultResourceType         = this.getResource() == null ? null : this.getResource() instanceof SnmpCollectionResource ? ((SnmpCollectionResource)this.getResource()).getResourceType() : null;
        this.defaultType                 = "gauge";
        
        if (defaultInstance != null && defaultResourceType != null)
            defaultResourceTypeInstance.put(defaultInstance, defaultResourceType);
        
        
        // 2) Retrieve data while visiting attributes
        this.visitAttributesForKpi(visitor);
        
        
        // 3) Manage default parameters
        LOG.debug("default instance={}", defaultInstance);
        LOG.debug("default resourceType={}", defaultResourceType);
        
        
        // 4) Calculate while visiting all KPI and store their result as 'fake' attribute
        this.visitKpis(visitor);
    }
    
    /**
     * While visiting attribute of this group, it retrieve data for KPI:
     * <ul>
     * <li>Map of attributes &lt name , value &gt</li>
     * <li>Map of available &lt instance , resource &gt</li>
     * <li>Collection name</li>
     * </ul>
     * @param visitor {@link org.opennms.netmgt.collection.api.CollectionSetVisitor}
     * @author Capgemini - pschiltz
     */
    private void visitAttributesForKpi(CollectionSetVisitor visitor) {
        // Loop on each attribute to retrieve it's data and visit it
        for(CollectionAttribute attr : getAttributes()) {
            // Determine the type of attribute to extract it's correct value
            if (attr.getAttributeType() instanceof NumericAttributeType) retrieveNumericAttributeData(attr);
            if (attr.getAttributeType() instanceof StringAttributeType) retrieveStringAttributeData(attr);
            
            // Visit the current attributes
            attr.visit(visitor);
        }
    }
    
    /**
     * Retrieve data for KPI from a numeric attribute.
     * 
     * @param attr {@link org.opennms.netmgt.collection.api.CollectionAttribute} numeric
     * @author Capgemini - pschiltz
     */
    private void retrieveNumericAttributeData(CollectionAttribute attr) {
        // Add the attribute alias and value to the map that will be used to calculate the KPIs
        attributesAliasValues.put(String.format("${%s}", attr.getName()), attr.getNumericValue());
        LOG.debug("save Numeric attribute={} value={} for KPI calculations", attr.getName(), attr.getNumericValue());
        
        // Retrieve others values needed for KPI configuration of a 'fake' attribute storing KPI result
        try {
            String instance = ((NumericAttributeType)attr.getAttributeType()).m_mibObj.getInstance();
            if (!defaultResourceTypeInstance.containsKey(instance)) {
                defaultInstance = instance;
                defaultResourceType = ((NumericAttributeType) attr.getAttributeType()).getResourceType();
                defaultResourceTypeInstance.put(defaultInstance, defaultResourceType);
                LOG.debug("retrieving default value from attributes: instanceName={}", defaultInstance);
                LOG.debug("retrieving default value from attributes: resourceType={}", defaultResourceType);
            }
            if (defaultCollectionName   == null) {
                defaultCollectionName       = ((NumericAttributeType) attr.getAttributeType()).getCollectionName();
                LOG.debug("retrieving default value from attributes: collectionName={}", defaultCollectionName);
            }
        } catch (Exception e) {
            LOG.info("on attribute={} retrieveing resourceType={} and collectionName={} failed: {}", attr.getName(), defaultResourceTypeInstance, defaultCollectionName, e);
        }
    }
    /**
     * Retrieve data for KPI from a string attribute.
     * 
     * @param attr {@link org.opennms.netmgt.collection.api.CollectionAttribute} numeric
     * @author Capgemini - pschiltz
     */
    private void retrieveStringAttributeData(CollectionAttribute attr) {
        // Add the attribute alias and value to the map that will be used to calculate the KPIs
        attributesAliasValues.put(String.format("${%s}", attr.getName()), attr.getStringValue());
        LOG.debug("save String attribute={} value={} for KPI calculations", attr.getName(), attr.getStringValue());
        
        // Retrieve others values needed for KPI configuration of a 'fake' attribute
        try {
            String instance = ((NumericAttributeType)attr.getAttributeType()).m_mibObj.getInstance();
            if (!defaultResourceTypeInstance.containsKey(instance)) {
                defaultInstance = instance;
                defaultResourceType = ((StringAttributeType) attr.getAttributeType()).getResourceType();
                defaultResourceTypeInstance.put(defaultInstance, defaultResourceType);
                LOG.debug("retrieving default value from attributes: instanceName={}", defaultInstance);
                LOG.debug("retrieving default value from attributes: resourceType={}", defaultResourceType);
            }
            if (defaultCollectionName   == null) {
                defaultCollectionName       = ((StringAttributeType) attr.getAttributeType()).getCollectionName();
                LOG.debug("retrieving default value from attributes: collectionName={}", defaultCollectionName);
            }
        } catch (Exception e) {
            LOG.info("on attribute={} retrieveing resourceType={} and collectionName={} failed: {}", attr.getName(), defaultResourceTypeInstance, defaultCollectionName, e);
        }
    }
    
    
    /**
     * Calculate all KPI, then create their 'fake' attribute to be visited for persistence of KPI result.
     * 
     * @param visitor {@link org.opennms.netmgt.collection.api.CollectionSetVisitor}
     * @author Capgemini - pschiltz
     */
    private void visitKpis(CollectionSetVisitor visitor) {
        // Loop on each KPI of this group
        for (SnmpKpi kpi : this.m_kpis) {
            // The KPI list is sorted in order for calculation dependancy
               LOG.debug("Calculating KPI {}", kpi);
               
               // A) Save data that can be set to default
               String          previousInstance        = kpi.getInstance();
               ResourceType    previousResourceType    = kpi.getResType();
               
               // B) Set default parameters values if missing
               setKpiDefaultData(kpi);
               
               // C) Check, calculate and visit the kpi
               visitKpi(visitor, kpi);
               
               // D) Set back previous values before default ones ; it's needed for next instances calls when the KPI isn't specific for an instance
               kpi.setInstance(previousInstance);
               kpi.setResType(previousResourceType);
           }
    }
    
    /**
     * Manage default parameters for a KPI.
     * 
     * @param kpi {@link org.opennms.netmgt.collectd.SnmpKpi} to be managed with default parameters
     * @author Capgemini - pschiltz
     */
    private void setKpiDefaultData(SnmpKpi kpi) {
        // Manage instance and resource
        if (kpi.getResType() == null)
            if (kpi.getInstance() == null || kpi.getInstance().isEmpty() || kpi.getInstance().equals("ifIndex")) {
                // When instance isn't configured we use the default one
                // 'ifIndex' instances must use the default one
                kpi.setResType(defaultResourceType);
                kpi.setInstance(defaultInstance);
                LOG.debug("for KPI={} parameter set to default: instance={}", kpi.getAlias(), kpi.getInstance());
                LOG.debug("for KPI={} parameter set to default: resource type={}", kpi.getAlias(), kpi.getResType());
            } else {
                // Retrieve resource type matching this KPI instance
                ResourceType resType = defaultResourceTypeInstance.get(kpi.getInstance());
                if (resType != null) kpi.setResType(resType);
                else
                    // As it look like distinct instances are run separatly, then forcing another resourceType will not work
                    //  If there are exception to the rule above, the default instance&resourceType is used without having to set it manually
                    LOG.warn("KPI={} can only be calculated for instance={} (current instance={})", kpi.getAlias(), kpi.getInstance(), defaultInstance);
            }
        
        // Manage collection
        if (kpi.getCollectionName() == null || kpi.getCollectionName().isEmpty()) {
            kpi.setCollectionName(defaultCollectionName);
            LOG.debug("for KPI={} parameter set to default: collectionName={}", kpi.getAlias(), kpi.getCollectionName());
        }
        
        // Manage type
        if (kpi.getAttributeType()  == null || kpi.getAttributeType().isEmpty()) {
            kpi.setAttributeType(defaultType);
            LOG.debug("for KPI={} parameter set to default: type={}", kpi.getAlias(), kpi.getAttributeType());
        }
    }
    
    /**
     * Check if a string value can be parsed to an integer.
     * @param text value to be checked.
     * @return if the string value can be parsed to an integer.
     */
    public static boolean tryParseInt(String value)  
    {
         try  {
             Integer.parseInt(value);  
             return true;  
          } catch(NumberFormatException nfe)   {  
              return false;  
          }
    }
    
    
    /**
     * Testing method for a KPI, to check if it can be calculated.
     * 
     * @param kpi {@link org.opennms.netmgt.collectd.SnmpKpi} to be tested
     * @return if the kpi can be calculated
     * @author Capgemini - pschiltz
     */
    private boolean testKpi(SnmpKpi kpi) {
        boolean canCalculate = true;
        // Check if the resource type is missing
        if (kpi.getResType()        == null) {
            LOG.debug("test KPI={}: missing resource type", kpi.getAlias());
            canCalculate = false;
        }
        // Check if the collection name is missing
        if (kpi.getCollectionName() == null || kpi.getCollectionName().isEmpty()) {
            LOG.debug("test KPI={}: missing collection name for group={}", kpi.getAlias(), this);
            canCalculate = false;
        }
        return canCalculate;
    }
    
    /**
     * Visiting method for a KPI.
     *  It will check if the KPI is valid.
     *  Calculate the KPI value.
     *  Visit the KPI. (It mean, creating a 'fake' attribute holding KPI's name and result and visiting the attribute it for persistance.)
     * 
     * @param visitor {@link org.opennms.netmgt.collection.api.CollectionSetVisitor}
     * @param kpi {@link org.opennms.netmgt.collectd.SnmpKpi} to be checked, calculated and visited
     */
    private void visitKpi(CollectionSetVisitor visitor, SnmpKpi kpi) {
        // Check if KPI can be visited
        if (!testKpi(kpi)) {
            LOG.warn("KPI={} can't be calculated because resource={} or collection={} not found (Collection={} Group={})", kpi.getAlias(), kpi.getResType(), kpi.getCollectionName(), kpi.getCollectionName(), this.getName());
            return;
        }
        
        // Calculate the KPI value, using the map of attributes <name,value> as parameters
        kpi.setValue(calculateKpi(kpi));
        
        // Check if the calculated value is valid
        if (kpi.getValue() == null || kpi.getValue().isNull()) {
            LOG.warn("KPI={} doesn't have a valid calculated value (Collection={} Group={})", kpi.getAlias(), kpi.getCollectionName(), this.getName());
            return;
        }
        
        // Add the KPI calculated value to the parameters list, because next KPI can use this KPI value
        attributesAliasValues.put(String.format("${%s}", kpi.getAlias()), kpi.getValue());
        
        // Visit the KPI, it will create a 'fake' attribute and visit it for persitance
        kpi.visit(visitor);
    }
    
    /**
     * Calculate the KPI expression using an engine.
     * (Anyone can create an engine it only need to inherit {@link org.opennms.netmgt.collectd.ICalculationEngine})
     * 
     * @param kpiName the alias name of the KPI.
     * @param expression the expression to calculate the KPI value.
     * @param attributesAliasValues the list of key value pair of attributes alias and values.
     * @return {@link org.opennms.netmgt.snmp.SnmpValue} holding the KPI calculation result.
     * @author Capgemini - pschiltz
     */
    protected SnmpValue calculateKpi(SnmpKpi kpi) {
        try {
            // 1) Retrieve the engine class for calculating the KPI expression
            ICalculationEngine calculationEngine = CalculationEngineFactory.getEngine(kpi.getExpressionEngine());
            
            // 2) Run the engine to retrieve the result
            Object result = calculationEngine.calculation(kpi.getExpression(), attributesAliasValues);
            
            // 3) Convert retrieved value to an SnmpValue
            if (result == null) {
                LOG.warn("Could not calculate KPI={} Expression engine returned null value.", kpi.getAlias());
                return SnmpUtils.getValueFactory().getNull();
            } else
                LOG.debug("Calculated KPI={} Expression engine returned value={}", kpi.getAlias(), result);
            
            // To convert we use the octet/byte array that can manage all value types
            return SnmpUtils.getValueFactory().getOctetString(result.toString().getBytes());
            
        } catch (SecurityException e)           { LOG.error("Could not calculate KPI={} Bad engine configuration: SecurityException {}",         kpi.getAlias(), e);
        } catch (IllegalArgumentException e)    { LOG.error("Could not calculate KPI={} Bad engine configuration: IllegalArgumentException {}",  kpi.getAlias(), e);
        } catch (ClassCastException e)          { LOG.error("Could not calculate KPI={} Bad engine configuration: ClassCastException {}",        kpi.getAlias(), e);
        } catch (ScriptException e)             { LOG.error("Group={} KPI={} calculation failed: ScriptException={}",       this.getName(),      kpi.getAlias(), e); }
        // When error occured, return a null value
        return SnmpUtils.getValueFactory().getNull();
    }
    
    /**
     * Clean KPI created sub-elements and temporary values.
     * @author Capgemini - pschiltz
     */
    protected void cleanKpi() {
        // Delete created 'fake' attributes to avoid any problems
        for (SnmpKpi kpi : this.m_kpis)
            kpi.clean();
    }
    
}
