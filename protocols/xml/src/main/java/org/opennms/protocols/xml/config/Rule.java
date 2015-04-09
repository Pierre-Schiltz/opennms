package org.opennms.protocols.xml.config;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Capgemini - pschiltz
 */
@XmlRootElement(name="rule")
@XmlAccessorType(XmlAccessType.FIELD)
public class Rule implements Serializable, Comparable<Rule> {
    
    /**
     * The Constant serialVersionUID.
     */
    private static final long serialVersionUID = -8220561501371984080L;
    
    /**
     * The rule type class.
     */
    @XmlEnum(String.class)
    public enum CollectRuleType { START, STOP, VALID }
    
    
    /**
     * The rule name.
     */
    @XmlAttribute(name="name", required=true)
    private String m_name;
    
    
    /**
     * The rule type.
     */
    @XmlAttribute(name="type", required=true)
    private CollectRuleType m_type;
    
    /**
     * The rule expression.
     */
    @XmlAttribute(name="expression", required=true)
    private String m_expression;
    
    
    /**
     * The rule result.
     */
    private Boolean m_result;
    
    
    
    
    /**
     * The rule name.
     * @return this rule name.
     */
    public String getName() { return this.m_name; }
    /**
     * The rule name.
     * @param name, the new name.
     */
    public void setName(String name) { this.m_name = name; }
    
    /**
     * The rule type.
     * @return this rule type.
     */
    public CollectRuleType getType() { return this.m_type; }
    /**
     * The rule type.
     * @param type, the new type.
     */
    public void setType(CollectRuleType type) { this.m_type = type; }
    
    /**
     * The rule expression.
     * @return this rule name.
     */
    public String getExpression() { return this.m_expression; }
    /**
     * The rule expression.
     * @param expression, the new expression.
     */
    public void setExpression(String expression) { this.m_expression = expression; }
    
    
    /**
     * The rule name.
     * @return this rule name.
     */
    public Boolean getResult() { return this.m_result; }
    /**
     * The rule name.
     * @param name, the new name.
     */
    public void setResult(Boolean result) { this.m_result = result; }
    /**
     * Clear the current rule result.
     */
    public void clearResult() { this.m_result = null; }
    
    
    /**
     * The rule are ordered by their type.
     */
    @Override
    public int compareTo(Rule o) {
        return this.getType().compareTo(o.getType());
    }
    
}
