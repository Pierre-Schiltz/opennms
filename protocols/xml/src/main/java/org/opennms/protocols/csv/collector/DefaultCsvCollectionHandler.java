package org.opennms.protocols.csv.collector;

import org.opennms.netmgt.collection.api.AttributeGroupType;
import org.opennms.netmgt.collection.api.CollectionAgent;
import org.opennms.protocols.xml.collector.XmlCollectionResource;
import org.opennms.protocols.xml.collector.XmlCollectionSet;
import org.opennms.protocols.xml.config.Request;
import org.opennms.protocols.xml.config.XmlSource;

/**
 * The default implementation of the interface XmlCollectionHandler based on AbstractCsvCollectionHandler.
 * 
 * @author Capgemini - jdemazière, pschiltz
 */
public class DefaultCsvCollectionHandler extends AbstractCsvCollectionHandler {

    /* (non-Javadoc)
     * @see org.opennms.protocols.xml.collector.AbstractXmlCollectionHandler#fillCollectionSet(java.lang.String, org.opennms.protocols.xml.config.Request, org.opennms.netmgt.collection.api.CollectionAgent, org.opennms.protocols.xml.collector.XmlCollectionSet, org.opennms.protocols.xml.config.XmlSource)
     */
    @Override
    protected void fillCollectionSet(String urlString, Request request, CollectionAgent agent, XmlCollectionSet collectionSet, XmlSource source) throws Exception {
        fillCollectionSet(agent, collectionSet, source, getCsvLines(source, urlString));
    }

    /* (non-Javadoc)
     * @see org.opennms.protocols.xml.collector.AbstractXmlCollectionHandler#processXmlResource(org.opennms.protocols.xml.collector.XmlCollectionResource, org.opennms.netmgt.config.collector.AttributeGroupType)
     */
    protected void processXmlResource(XmlCollectionResource collectionResource, AttributeGroupType attribGroupType) {}

}
