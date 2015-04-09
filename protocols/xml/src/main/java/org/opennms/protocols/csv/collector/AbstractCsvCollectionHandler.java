package org.opennms.protocols.csv.collector;

import java.io.FileReader;
import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.script.ScriptException;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.opennms.netmgt.collectd.CalculationEngineFactory;
import org.opennms.netmgt.collectd.ICalculationEngine;
import org.opennms.netmgt.collection.api.AttributeGroupType;
import org.opennms.netmgt.collection.api.CollectionAgent;
import org.opennms.protocols.xml.collector.AbstractXmlCollectionHandler;
import org.opennms.protocols.xml.collector.XmlCollectionAttributeType;
import org.opennms.protocols.xml.collector.XmlCollectionResource;
import org.opennms.protocols.xml.collector.XmlCollectionSet;
import org.opennms.protocols.xml.collector.XmlCollectorException;
import org.opennms.protocols.xml.config.Rule;
import org.opennms.protocols.xml.config.XmlGroup;
import org.opennms.protocols.xml.config.XmlObject;
import org.opennms.protocols.xml.config.XmlSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVReader;

/**
 * Abstract CSV Collection Handler.
 * This handler override the XML collection handler to manage CSV files.
 * 
 * @author Capgemini - jdemazière, pschiltz
 */
public abstract class AbstractCsvCollectionHandler extends AbstractXmlCollectionHandler {
    
    private static final Logger LOG = LoggerFactory.getLogger(AbstractCsvCollectionHandler.class);
    
    /**
     * Active list of parameters used for epxression calculation.
     */
    private HashMap<String, Object> m_parameters = new HashMap<>();
    
    /**
     * Current map of resources for one collected group.
     */
    private HashMap<String, XmlCollectionResource> m_resources = new HashMap<>();
    
    /**
     * Current time stamp.
     */
    private Date m_timeStamp;
    
    /**
     * Specific enumeration for specifying result of the CSV check line.
     */
    public enum CollectCSVType { COLLECT, SKIP, STOP }
    
    
    /**
     * Fill collection set for CSV files.
     *
     * @param agent the agent
     * @param collectionSet the collection set
     * @param source the source
     * @param csvLines CSV lines
     * @throws ParseException the parse exception
     */
    @SuppressWarnings("boxing")
    protected void fillCollectionSet(CollectionAgent agent, XmlCollectionSet collectionSet, XmlSource source, List<String[]> csvLines) throws ParseException {
        // Loop on groups to collect
        for (XmlGroup group : source.getXmlGroups()) {
            
            LOG.debug("fillCollectionSet: getting resources for XML group {}", group.getName());
            
            // Manage timestamp (clean the previous time stamp and retrieve current if it's at group level)
            boolean isGroupTimestamp = getTimeStamp(source, csvLines, group);
            
            // Manage the CSV resources (clean the previous resources and add the default resource)
            m_resources.clear();
            m_resources.put("node", getCollectionResource(agent, "node", group.getResourceType(), m_timeStamp));
            
            // Manage default values
            setGroupDefaultValues(group);
            
            // Check if there are row to manage
            if (csvLines.size() < group.getCsvTrimStart() + group.getCsvTrimEnd())
                LOG.warn("fillCollectionSet: No row to collect for group={}", group.getName());
            else {
                LOG.warn("collecting CSV lines (size={}/{}) for group={}", csvLines.size() - group.getCsvTrimStart() - group.getCsvTrimEnd(), csvLines.size(), group.getName());
                // A)   Loop on collected CSV rows
                int csvLineIndex = -1;
                for (String[] csvLine : csvLines) {
                    // Manage parameters for calculated expression
                    csvLineIndex++;
                    manageParameters(source, group, csvLine, csvLineIndex);
                    
                    // Check if the current row can be collected
                    CollectCSVType check = checkCsvCollect(group, csvLines, csvLine, csvLineIndex, group.getRules(), m_parameters);
                    if (check.equals(CollectCSVType.STOP))
                        break;
                    if (check.equals(CollectCSVType.COLLECT)) {
                        // Calculate the timestamp (if it need to be calculated at row level)
                        if (!isGroupTimestamp) getRowTimeStamp(group, csvLine);
                        // Retrieve the resource
                        XmlCollectionResource collectionResource = getResource(agent, group, csvLine, m_timeStamp);
                        AttributeGroupType attribGroupType = new AttributeGroupType(group.getName(), group.getIfType());
                        
                        LOG.debug("fillCollectionSet: processing CSV resource {}", collectionResource.getResourceTypeName());
                        
                        // a)   Loop objects in order (the order is mandatory when there are interdependent expressions)
                        for (XmlObject object : group.getCsvObjects()) {
                            
                            // Collect the value
                            String value = collectObjectValue(group, csvLine, object);
                            
                            // Check object's value
                            if (value != null) {
                                LOG.debug("fillCollectionSet: in group={} for object={} retrieved CSV value={}", group.getName(), object.getName(), value);
                                
                                // Create an attribute to persist object value
                                XmlCollectionAttributeType attribType = new XmlCollectionAttributeType(object, attribGroupType);
                                collectionResource.setAttributeValue(attribType, value);
                                
                                // When there are calculated expression we need to store parameters values
                                if (group.hasCsvExpressions())
                                    m_parameters.put(String.format("${%s}", object.getName()), value);
                            }
                        }
                        
                        // Save resource holding attributes we want to persist
                        processXmlResource(collectionResource, attribGroupType);
                        collectionSet.getCollectionResources().add(collectionResource);
                        
                    }
                }
            }
        }
    }
    
    /**
     * Set CSV collect default values before collect.
     * 
     * @param source {@link XmlSource} object to manage CSV collect default value.
     */
    @SuppressWarnings("boxing")
    private static void setSourceDefaultValues(XmlSource source) {
        if (source.getCsvSeparator() == null)
            source.setCsvSeparator(CSVParser.DEFAULT_SEPARATOR);
        if (source.getCsvQuoteChar() == null)
            source.setCsvQuoteChar(CSVParser.DEFAULT_QUOTE_CHARACTER);
        if (source.getCsvStrictQuote() == null)
            source.setCsvStrictQuote(CSVParser.DEFAULT_STRICT_QUOTES);
        if (source.getCsvEscapeChar() == null)
            source.setCsvEscapeChar(CSVParser.DEFAULT_ESCAPE_CHARACTER);
        if (source.getCsvTrimWhiteSpace() == null)
            source.setCsvTrimWhiteSpace(CSVParser.DEFAULT_IGNORE_LEADING_WHITESPACE);
        
        LOG.debug("source='{}' CSV configuration: separator character={}", source.getUrl(), source.getCsvSeparator());
        LOG.debug("source='{}' CSV configuration: quote character={}", source.getUrl(), source.getCsvQuoteChar());
        LOG.debug("source='{}' CSV configuration: strict quote={}", source.getUrl(), source.getCsvStrictQuote());
        LOG.debug("source='{}' CSV configuration: escape character={}", source.getUrl(), source.getCsvEscapeChar());
        LOG.debug("source='{}' CSV configuration: trim leading white space={}", source.getUrl(), source.getCsvTrimWhiteSpace());
    }
    
    /**
     * Set CSV collect default values before collect.
     * 
     * @param group {@link XmlGroup} object to manage CSV collect default value.
     */
    private static void setGroupDefaultValues(XmlGroup group) {
        // Manages rules
        for (Rule rule : group.getRules()) {
            rule.clearResult();
            LOG.debug("group={} is using rule {} with expression '{}'", group.getName(), rule.getName(), rule.getExpression());
        }
        // Order rules
        Collections.sort(group.getRules());
    }
    
    /**
     * Manage parameters for calculation expression for a row.
     * 
     * @param source describing CSV file format.
     * @param group currently collecting.
     * @param csvLine from which to extract parameters.
     */
    @SuppressWarnings("boxing")
    private void manageParameters(XmlSource source, XmlGroup group, String[] csvLine, Integer csvLineIndex) {
        // Each row we need to clear previous parameters
        m_parameters.clear();
        
        // When there are calculated expression we need to retrieve CSV data
        if (group.hasCsvExpressions()) {
            String row = null;
            for (int i = 0; i < csvLine.length; i++) {
                m_parameters.put(String.format("${%d}", i), csvLine[i]);
                row = (i == 0) ? csvLine[0] : String.format("%s%c%s", row, source.getCsvSeparator(), csvLine[i]);
            }
            m_parameters.put("${row}", row);
            
            LOG.debug("manage parameters for row \"{}\"", row);
            
            if (csvLineIndex != null)
                m_parameters.put("${index}", csvLineIndex);
        }
    }
    
    /**
     * Check if the collect can be done for the given CSV ligne.
     * 
     * @param group the collect group.
     * @param csvLines the CSV content.
     * @param csvLine the CSV line.
     * @param csvLineIndex the current line index.
     * @param rules the list of rules.
     * @param parameters the list of parameters.
     * @return if CSV collect can be done.
     */
    @SuppressWarnings("boxing")
    private static CollectCSVType checkCsvCollect(XmlGroup group, List<String[]> csvLines, String[] csvLine, int csvLineIndex, List<Rule> rules, HashMap<String, Object> parameters) {
        int csvLinesSize = csvLines.size();
        // Check start index
        if (csvLineIndex <= group.getCsvTrimStart()) {
            LOG.debug("doesn't collect row[{}/{}]: index must be greater than '{}'", csvLineIndex, csvLinesSize, group.getCsvTrimStart());
            return CollectCSVType.SKIP;
        }
        // Check end index
        if (csvLineIndex >= csvLines.size() - group.getCsvTrimEnd()) {
            LOG.debug("doesn't collect row[{}/{}]: index must be lower than '{}'", csvLineIndex, csvLinesSize, csvLines.size() - group.getCsvTrimEnd());
            return CollectCSVType.STOP;
        }
        // Check column count
        if (group.getCsvColmunsCount() != null && csvLine.length != group.getCsvColmunsCount()) {
            LOG.debug("doesn't collect row[{}/{}]: expected colmun count '{}' doesn't match retrieved count '{}'", csvLineIndex, csvLinesSize, group.getCsvColmunsCount(), csvLine.length);
            return CollectCSVType.SKIP;
        }
        // Check rules
        Boolean canCollect = true;
        for (Rule rule : rules) {
            switch (rule.getType())
            {
                case START:
                    // Evaluate rule when needed
                    if (rule.getResult() == null || rule.getResult().equals(false))
                        canCollect = calculateExpressionRule(rule, parameters, group.getExpressionEngine());
                    // Return when rule isn't validated
                    if (canCollect != null && canCollect == false) {
                        LOG.debug("doesn't collect row[{}/{}]: collect isn't started for rule '{}'", csvLineIndex, csvLinesSize, rule.getName());
                        return CollectCSVType.SKIP;
                    }
                    break;
                case STOP:
                    // Evaluate rule when needed
                    if (rule.getResult() == null || rule.getResult().equals(false))
                        canCollect = !calculateExpressionRule(rule, parameters, group.getExpressionEngine());
                    // Return when rule isn't validated
                    if (canCollect != null && canCollect == false) {
                        LOG.debug("doesn't collect row[{}/{}]: collect stopped by rule '{}'", csvLineIndex, csvLinesSize, rule.getName());
                        return CollectCSVType.STOP;
                    }
                    break;
                case VALID:
                    // Evaluate rule when needed
                    canCollect = calculateExpressionRule(rule, parameters, group.getExpressionEngine());
                    // Return when rule isn't validated
                    if (canCollect != null && canCollect == false) {
                        LOG.debug("doesn't collect row[{}/{}]: collect isn't validated by rule '{}'", csvLineIndex, csvLinesSize, rule.getName());
                        return CollectCSVType.SKIP;
                    }
                    break;
            }
        }
        
        LOG.debug("do collect for row[{}/{}]", csvLineIndex, csvLinesSize);
        return CollectCSVType.COLLECT;
    }
    
    /**
     * Call the calculation engine for one rule expression.
     * 
     * @param rule to be calculated.
     * @param parameters map of parameters name and values.
     * @param engineName the expression engine full class name.
     * @return the calculated result of the rule expression.
     */
    public static Boolean calculateExpressionRule(Rule rule, HashMap<String, Object> parameters, String engineName) {
        ICalculationEngine engine = CalculationEngineFactory.getEngine(engineName);
        if (engine == null) return null;
        
        try {
            Object result;
            result = engine.calculation(rule.getExpression(), parameters);
            rule.setResult((Boolean)result);
        } catch (ScriptException e)     { LOG.error("Could not calculate rule={}: ScriptException {}",      rule.getName(), e);
        } catch (ClassCastException e)  { LOG.error("Could not calculate rule={}: ClassCastException {}",   rule.getName(), e); }
        
        LOG.debug("For rule={} calculated value={}", rule.getName(), rule.getResult());
        
        return rule.getResult();
    }
    
    /**
     * Collect an object value. The value can come from the CSV or a calculated expression.
     * 
     * @param group the object belong to.
     * @param csvLine currently collected.
     * @param object to be collected.
     * @return the collected object value.
     */
    @SuppressWarnings("boxing")
    public String collectObjectValue(XmlGroup group, String[] csvLine, XmlObject object) {
        String value = null;
        
        // Collect the value
        if (object.getCsvIndex() != null)
            // Collect the value from one CSV column
            if (object.getCsvIndex() < 0)
                LOG.warn("fillCollectionSet: Object={} of group={} can't be collect from CSV: csv column index can't be negative: {}", object.getName(), group.getName(), object.getCsvIndex());
            else if (group.getCsvColmunsCount() != null && group.getCsvColmunsCount() > 0 && object.getCsvIndex() > group.getCsvColmunsCount())
                LOG.warn("fillCollectionSet: Object={} of group={} can't be collect from CSV: csv column index can't be superior to configured columns count: index={} > colmuns={}", object.getName(), group.getName(), object.getCsvIndex(), group.getCsvColmunsCount());
            else
                try {
                    value = csvLine[object.getCsvIndex()];
                } catch (IndexOutOfBoundsException e) {
                    LOG.warn("fillCollectionSet: Object={} of group={} can't be collect from CSV: csv column index can't be found in CSV file: index={}, CSV columns={}", object.getName(), group.getName(), object.getCsvIndex(), csvLine.length);
                }
        else if (object.isCsvExpression()) {
            // Collect the value by calculating the object expression
            value = calculateExpressionObject(object, m_parameters, group.getExpressionEngine());
            if (value == null)
                LOG.warn("fillCollectionSet: Object={} of group={} can't be collect from CSV: calculated expression '{}' has returned a null value", object.getName(), group.getName(), object.getCsvExpression());
        }
        else
            LOG.warn("fillCollectionSet: Object={} of group={} can't be collect from CSV: column index and expression are missing", object.getName(), group.getName());
        
        return value;
    }
    
    
    /**
     * Call the calculation engine for one rule expression.
     * 
     * @param object to be calculated.
     * @param parameters map of parameters name and values.
     * @param engineName the expression engine full class name.
     * @return the calculated result of the object expression.
     */
    public static String calculateExpressionObject(XmlObject object, HashMap<String, Object> parameters, String engineName) {
        ICalculationEngine engine = CalculationEngineFactory.getEngine(engineName);
        if (engine == null) return null;
        
        Object result = null;
        String value = null;
        try {
            result = engine.calculation(object.getCsvExpression(), parameters);
            value = (String)result;
        } catch (ScriptException e) {
            LOG.error("Could not calculate object={}: ScriptException {}",      object.getName(), e);
        } catch (ClassCastException e) {
            if (result != null) {
                LOG.warn("Use string representation of calculated expression: Could not calculate object={}: ClassCastException {}",   object.getName(), e);
                value = result.toString();
            }
            else
                LOG.error("Could not calculate object={}: ClassCastException {}",   object.getName(), e);
        }
        
        LOG.debug("For object={} calculated value={}", object.getName(), value);
        
        return value;
    }
    
    /**
     * Retrieve the resource, the following resource name origin order is used
     * (A resource is a division of the collected elements for one or more names.
     * It can be used to represent an equipment name.)
     * <ul>
     * <li>named       : it's group's csv-resource-name</li>
     * <li>index       : it's CSV's cell matching group's csv-resource-index</li>
     * <li>expression  : it's the calculated expression from group's csv-resource-expression on the CSV current line</li>
     * <li>default     : 'node'</li>
     * </ul>
     * 
     * @param agent
     * @param group
     * @param csvLine
     * @param timestamp
     * @return
     */
    @SuppressWarnings("boxing")
    public XmlCollectionResource getResource(CollectionAgent agent, XmlGroup group, String[] csvLine, Date timestamp) {
        // 1)   Retrieve the resource name
        String resourceName = null;
        if (group.getCsvResourceName() != null && !group.getCsvResourceName().isEmpty())
            resourceName = group.getCsvResourceName();
        else if (group.getCsvResourceIndex() != null && group.getCsvResourceIndex() >= 0 && group.getCsvResourceIndex() <= csvLine.length)
            resourceName = csvLine[group.getCsvResourceIndex()];
        else if (group.getCsvResourceExpression() != null && !group.getCsvResourceExpression().isEmpty())
            resourceName = calculateExpressionResource(group, this.m_parameters, group.getExpressionEngine());
        
        if (resourceName == null || resourceName.isEmpty())
            resourceName = "node";
        
        // 2)   Retrieve the resource
        // A    Return the existing resource with the same name
        if (this.m_resources.containsKey(resourceName))
            return this.m_resources.get(resourceName);
        // B    Create the resource if if doesn't exist and add it to the map
        XmlCollectionResource newResource = getCollectionResource(agent, resourceName, group.getResourceType(), timestamp);
        this.m_resources.put(resourceName, newResource);
        
        return newResource;
    }
    
    /**
     * Call the calculation engine for resource name expression.
     * 
     * @param group whose resource is to be calculated.
     * @param parameters map of parameters name and values.
     * @param engineName the expression engine full class name.
     * @return the calculated result of the resource name expression.
     */
    public static String calculateExpressionResource(XmlGroup group, HashMap<String, Object> parameters, String engineName) {
        ICalculationEngine engine = CalculationEngineFactory.getEngine(engineName);
        if (engine == null) return null;
        
        Object result = null;
        String value = null;
        try {
            result = engine.calculation(group.getCsvResourceExpression(), parameters);
            value = (String)result;
        } catch (ScriptException e) {
            LOG.error("Could not calculate resource name for group={}: ScriptException {}",      group.getName(), e);
        } catch (ClassCastException e) {
            if (result != null) {
                LOG.warn("Use string representation of calculated expression: Could not cast resource name for group={}: ClassCastException {}",   group.getName(), e);
                value = result.toString();
            }
            else
                LOG.error("Could not cast resource name for group={}: ClassCastException {}",   group.getName(), e);
        }
        
        LOG.debug("For group={} calculated resource name={}", group.getName(), value);
        
        return value;
    }
    
    
    /**
     * Gets the time stamp (at group level).
     * 
     * @param context the JXPath context
     * @param group the group
     * @return the time stamp
     */
    @SuppressWarnings("boxing")
    protected boolean getTimeStamp(XmlSource source, List<String[]> csvLines, XmlGroup group) {
        m_timeStamp = null;
        
        // Set the current date when asked
        if (group.getCsvTimestampNow() != null && group.getCsvTimestampNow()) {
            m_timeStamp = new Date();
            return true;
        }
        
        String value = null;
        boolean isGroupTimeStamp = false;
        
        // Set a specific exctracted row when asked
        if (group.getCsvTimestampRowIndex() != null && group.getCsvTimestampRowIndex() >= 0 && group.getCsvTimestampRowIndex() <= csvLines.size()) {
            isGroupTimeStamp = true;
            String[] csvLine = csvLines.get(group.getCsvTimestampRowIndex());
            if (group.getCsvTimestampColumnIndex() != null && group.getCsvTimestampColumnIndex() >= 0 && group.getCsvTimestampColumnIndex() <= csvLine.length) {
                value = csvLine[group.getCsvTimestampColumnIndex()];
            }
            if ((value == null || value.isEmpty()) && group.getCsvTimestampExpression() != null && !group.getCsvTimestampExpression().isEmpty()) {
                manageParameters(source, group, csvLine, null);
                value = calculateExpressionTimestamp(group, this.m_parameters, group.getExpressionEngine());
            }
        }
        
        // Convert the specific timestamp value
        if (value != null && !value.isEmpty())
            m_timeStamp = convertTimeStamp(value, group.getTimestampFormat());
        
        return isGroupTimeStamp;
    }
    
    /**
     * Call the calculation engine for timestamp expression.
     * 
     * @param group whose timestamp is to be calculated.
     * @param parameters map of parameters name and values.
     * @param engineName the expression engine full class name.
     * @return the calculated result of the timestamp expression.
     */
    public static String calculateExpressionTimestamp(XmlGroup group, HashMap<String, Object> parameters, String engineName) {
        ICalculationEngine engine = CalculationEngineFactory.getEngine(engineName);
        if (engine == null) return null;
        
        Object result = null;
        String value = null;
        try {
            result = engine.calculation(group.getCsvTimestampExpression(), parameters);
            value = (String)result;
        } catch (ScriptException e) {
            LOG.error("Could not calculate timestamp for group={}: ScriptException {}",      group.getName(), e);
        } catch (ClassCastException e) {
            if (result != null) {
                LOG.warn("Use string representation of calculated expression: Could not cast timestamp for group={}: ClassCastException {}",   group.getName(), e);
                value = result.toString();
            }
            else
                LOG.error("Could not cast timestamp for group={}: ClassCastException {}",   group.getName(), e);
        }
        
        LOG.debug("For group={} calculated timestamp={}", group.getName(), value);
        
        return value;
    }
    
    /**
     * Gets the time stamp (at CSV row level).
     * 
     * @param group whose timestamp is to be calculated.
     * @param csvLine
     * @param defaultTimestamp
     * @return
     */
    @SuppressWarnings("boxing")
    private void getRowTimeStamp(XmlGroup group, String[] csvLine) {
        String value = null;
        if (group.getCsvTimestampColumnIndex() != null && group.getCsvTimestampColumnIndex() >= 0 && group.getCsvTimestampColumnIndex() <= csvLine.length) {
            value = csvLine[group.getCsvTimestampColumnIndex()];
        } else if (group.getCsvTimestampExpression() != null && !group.getCsvTimestampExpression().isEmpty()) {
            value = calculateExpressionTimestamp(group, this.m_parameters, group.getExpressionEngine());
        }
        
        if (value != null && !value.isEmpty())
            m_timeStamp = convertTimeStamp(value, group.getTimestampFormat());
    }
    
    /**
     * Convert string representation of the timestamp to a date representation.
     * 
     * @param value string representation of the timestamp.
     * @param format of the string representation timestamp.
     * @return the date representation of the timestamp.
     */
    private static Date convertTimeStamp(String value, String format) {
        String pattern = format == null || format.isEmpty() ? "yyyy-MM-dd HH:mm:ss" : format;
        LOG.debug("getTimeStamp: retrieving custom timestamp to be used when updating RRDs using value {} and pattern {}", value, pattern);
        Date date = null;
        try {
            DateTimeFormatter dtf = DateTimeFormat.forPattern(pattern);
            DateTime dateTime = dtf.parseDateTime(value);
            date = dateTime.toDate();
        } catch (Exception e) {
            LOG.warn("calculateTimeStamp: can't convert custom timestamp {} using pattern {}", value, pattern);
        }
        return date;
    }
    
    
    
    /**
     * Gets the CSV lines.
     *
     * @param filePath the file path.
     * @return the CSV lines.
     */
    @SuppressWarnings("boxing")
    protected static List<String[]> getCsvLines(XmlSource source, String filePath) {
        String file = filePath;
        
        // Remove the URI transformation
        if (filePath.startsWith("file:///"))
            file = filePath.substring(8);
        
        LOG.info("Collecting CSV lines from file '{}'", file);
        
        // Manage default values
        setSourceDefaultValues(source);
        
        // Read the whole CSV file for the possibly multiple group of collect
        try (final CSVReader reader = new CSVReader(new FileReader(file), source.getCsvSeparator(), source.getCsvQuoteChar(), source.getCsvEscapeChar(), 0, source.getCsvStrictQuote(), source.getCsvTrimWhiteSpace())) {
            return reader.readAll();
        } catch (Exception e) {
            throw new XmlCollectorException(e.getMessage(), e);
        }

    }

}
