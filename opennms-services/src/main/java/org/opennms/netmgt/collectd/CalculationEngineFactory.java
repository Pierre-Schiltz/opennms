package org.opennms.netmgt.collectd;

import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory class to manage calculation engine instances.
 * @author Capgemini - pschiltz
 */
public final class CalculationEngineFactory {
    
    /**
     * Logging utility parameter.
     */
    private static final Logger LOG = LoggerFactory.getLogger(CalculationEngineFactory.class);
    
    /**
     * KPI calculation engine instances.
     */
    private static TreeMap<String,ICalculationEngine>   m_engines; 
    
    /**
     * Configuration Key to access configured calculation engine full class name. 
     */
    public static final String              kpiCalculationEngineKey     = "org.opennms.netmgt.collectd.CalculationEngine";
    /**
     * Default calculation engine full class name.
     */
    public static final String              defaultKpiCalculationEngine = "org.opennms.netmgt.collectd.DefaultCalculationEngine";
    
    /**
     * Return the KPI calculation engine.
     *  It create a default engine when needed and/or try to instanciate an engine with the given class name.
     * @param kpiEngine the engine full class name instance to use, specific for the current kpi
     * @return the KPI calculation instance to use.
     * @author Capgemini - pschiltz
     */
    public static ICalculationEngine getEngine(String kpiEngine) {
        // Initialize the engine map if needed
        if (CalculationEngineFactory.m_engines == null) {
            CalculationEngineFactory.m_engines              = new TreeMap<>();
            String calculationEngineClassName       = System.getProperty(CalculationEngineFactory.kpiCalculationEngineKey);
            ICalculationEngine calculationEngine = null;
            
            // Try creating the engine configured in global configuration
            if (calculationEngineClassName!= null)
                try {
                    calculationEngine = (ICalculationEngine) Class.forName(calculationEngineClassName).newInstance();
                } catch (SecurityException e)           { LOG.error("Bad KPI engine '{}' configuration: SecurityException {}",         calculationEngineClassName, e);
                } catch (IllegalArgumentException e)    { LOG.error("Bad KPI engine '{}' configuration: IllegalArgumentException {}",  calculationEngineClassName, e);
                } catch (ClassNotFoundException e)      { LOG.error("Bad KPI engine '{}' configuration: ClassNotFoundException {}",    calculationEngineClassName, e);
                } catch (InstantiationException e)      { LOG.error("Bad KPI engine '{}' configuration: InstantiationException {}",    calculationEngineClassName, e);
                } catch (IllegalAccessException e)      { LOG.error("Bad KPI engine '{}' configuration: IllegalAccessException {}",    calculationEngineClassName, e);
                } catch (ClassCastException e)          { LOG.error("Bad KPI engine '{}' configuration: ClassCastException {}",        calculationEngineClassName, e); }
            
            // When the configuration
            if (calculationEngine == null) {
                LOG.warn("KPI engine configuration='{}' in file \"opennms.properties\" is not valid. Default class used instead '{}'.", calculationEngineClassName, CalculationEngineFactory.defaultKpiCalculationEngine);
                calculationEngineClassName = CalculationEngineFactory.defaultKpiCalculationEngine;
                try {
                    calculationEngine = (ICalculationEngine) Class.forName(calculationEngineClassName).newInstance();
                } catch (SecurityException e)           { LOG.error("Bad KPI engine '{}' configuration: SecurityException {}",         calculationEngineClassName, e);
                } catch (IllegalArgumentException e)    { LOG.error("Bad KPI engine '{}' configuration: IllegalArgumentException {}",  calculationEngineClassName, e);
                } catch (ClassNotFoundException e)      { LOG.error("Bad KPI engine '{}' configuration: ClassNotFoundException {}",    calculationEngineClassName, e);
                } catch (InstantiationException e)      { LOG.error("Bad KPI engine '{}' configuration: InstantiationException {}",    calculationEngineClassName, e);
                } catch (IllegalAccessException e)      { LOG.error("Bad KPI engine '{}' configuration: IllegalAccessException {}",    calculationEngineClassName, e);
                } catch (ClassCastException e)          { LOG.error("Bad KPI engine '{}' configuration: ClassCastException {}",        calculationEngineClassName, e); }
            }
            
            // Add the created engine to the list of engines
            m_engines.put(calculationEngineClassName, calculationEngine);
            LOG.debug("default KPI configuration engine used is '{}'", calculationEngineClassName);
        }
        
        // Check if the kpi specific engine is valid
        if (kpiEngine != null && !kpiEngine.isEmpty())
            if (CalculationEngineFactory.m_engines.containsKey(kpiEngine))
                // Return the specific engine if it exist
                return CalculationEngineFactory.m_engines.get(kpiEngine);
            else {
                // Else try to create the given engine
                try {
                    ICalculationEngine calculationEngine = (ICalculationEngine) Class.forName(kpiEngine).newInstance();
                    CalculationEngineFactory.m_engines.put(kpiEngine, calculationEngine);
                    return calculationEngine;
                } catch (SecurityException e)           { LOG.warn("Bad KPI engine '{}' configuration: SecurityException {}",         kpiEngine, e);
                } catch (IllegalArgumentException e)    { LOG.warn("Bad KPI engine '{}' configuration: IllegalArgumentException {}",  kpiEngine, e);
                } catch (ClassNotFoundException e)      { LOG.warn("Bad KPI engine '{}' configuration: ClassNotFoundException {}",    kpiEngine, e);
                } catch (InstantiationException e)      { LOG.warn("Bad KPI engine '{}' configuration: InstantiationException {}",    kpiEngine, e);
                } catch (IllegalAccessException e)      { LOG.warn("Bad KPI engine '{}' configuration: IllegalAccessException {}",    kpiEngine, e);
                } catch (ClassCastException e)          { LOG.warn("Bad KPI engine '{}' configuration: ClassCastException {}",        kpiEngine, e); }
                LOG.warn("Could not create engine out of '{}' class name. Use default engine instead.", kpiEngine);
            }
        
        return CalculationEngineFactory.m_engines.firstEntry().getValue();
    }
    
}
