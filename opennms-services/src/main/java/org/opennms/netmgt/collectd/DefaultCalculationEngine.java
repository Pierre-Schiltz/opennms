package org.opennms.netmgt.collectd;

import java.util.HashMap;
import java.util.Map.Entry;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Default calculation engine implementation (use of the JavaScript engine from javax.script.ScriptEngine).
 * 
 * @author Capgemini - pschiltz
 */
public class DefaultCalculationEngine implements ICalculationEngine {
    
    /**
     * Logging utility parameter.
     * Done with the name of the interface {@link org.opennms.netmgt.collectd.ICalculationEngine} to ease logging filters.
     */
    private static final Logger LOG = LoggerFactory.getLogger(DefaultCalculationEngine.class);
    
    /**
     * Static final parameter to hold the Engine creator instance.
     * @author Capgemini - pschiltz
     */
    public final static ScriptEngineManager scm = new ScriptEngineManager();
    
    /**
     * Calculator engine instance.
     * @author Capgemini - pschiltz
     */
    private ScriptEngine se;
    
    /**
     * Implementation of the calculation method used to retrieve the result of a KPI expression.
     * 
     * @author Capgemini - pschiltz
     * @throws ScriptException 
     */
    @Override
    public Object calculation(String expression, HashMap<String,Object> attributes) throws ScriptException {
        // 1) Retrieve the engine
        this.se = scm.getEngineByName("JavaScript");
        
        // 2) Evaluate expression without use of bindings as they doesn't want to work
        Object result = null;
        // Replace attribute names by their value
        String replacedExpression = expression;
        if (attributes != null)
            for (Entry<String, Object> attr : attributes.entrySet())
                if (replacedExpression.contains(attr.getKey()))
                    replacedExpression = replacedExpression.replace(attr.getKey(), attr.getValue().toString());
        
        // Call engine calculation method
        LOG.debug("expression \"{}\" with replaced variables values \"{}\"", expression, replacedExpression);
        result = se.eval(replacedExpression);
        LOG.debug("calculated expression '{}={}'", replacedExpression, result);
        
        // Return the result
        return result;
    }
}
