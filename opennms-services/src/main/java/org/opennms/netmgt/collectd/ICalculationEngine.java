package org.opennms.netmgt.collectd;

import java.util.HashMap;

import javax.script.ScriptException;

/**
 * Interface to implement a script engine calculator for the KPI functionality.
 * 
 * @author Capgemini - pschiltz
 */
public interface ICalculationEngine {
    /**
     * Execute calculation of the KPI expression with the list of attributes.
     * 
     * @param expression that will to be calculated
     * @param parameters a dictionary of parameters alias and their value to be replaced in the expression
     * @return the result of the expression
     * @author Capgemini - pschiltz
     */
    public Object calculation(String expression, HashMap<String,Object> attributes) throws ScriptException;
}
