/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2012 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2012 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.netmgt.linkd.snmp;

import static org.junit.Assert.assertEquals;

import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennms.core.test.OpenNMSJUnit4ClassRunner;
import org.opennms.core.test.snmp.annotations.JUnitSnmpAgent;
import org.opennms.core.test.snmp.annotations.JUnitSnmpAgents;
import org.opennms.core.utils.BeanUtils;
import org.opennms.core.utils.InetAddressUtils;
import org.opennms.netmgt.config.SnmpPeerFactory;
import org.opennms.netmgt.linkd.nb.Nms0001NetworkBuilder;
import org.opennms.netmgt.linkd.snmp.IsIsSystemObjectGroup.IsisAdminState;
import org.opennms.netmgt.snmp.CollectionTracker;
import org.opennms.netmgt.snmp.SnmpAgentConfig;
import org.opennms.netmgt.snmp.SnmpUtils;
import org.opennms.netmgt.snmp.SnmpWalker;
import org.opennms.test.JUnitConfigurationEnvironment;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OpenNMSJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/META-INF/opennms/applicationContext-proxy-snmp.xml"
})
@JUnitConfigurationEnvironment(systemProperties="org.opennms.provisiond.enableDiscovery=false")
public class Nms0001IsisTest extends Nms0001NetworkBuilder implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        BeanUtils.assertAutowiring(this);
    }

    @Test
    @JUnitSnmpAgents(value = {
            @JUnitSnmpAgent(host = FROH_IP, port = 161, resource = "classpath:linkd/nms0001/" + FROH_NAME + "-"+FROH_IP + "-walk.txt"),
            @JUnitSnmpAgent(host = OEDIPUS_IP, port = 161, resource = "classpath:linkd/nms0001/" + OEDIPUS_NAME + "-"+OEDIPUS_IP + "-walk.txt"),
            @JUnitSnmpAgent(host = SIEGFRIE_IP, port = 161, resource = "classpath:linkd/nms0001/" + SIEGFRIE_NAME + "-"+SIEGFRIE_IP + "-walk.txt")
    })
    public void testIsisSysObjGroupCollection() throws Exception {

        String name = "isisSystemObjectGroup";
        IsIsSystemObjectGroup m_isisSystemObjectGroup = new IsIsSystemObjectGroup(InetAddressUtils.addr(FROH_IP));
        CollectionTracker[] tracker = new CollectionTracker[0];
        tracker = new CollectionTracker[]{m_isisSystemObjectGroup};
        SnmpAgentConfig snmpAgent = SnmpPeerFactory.getInstance().getAgentConfig(InetAddressUtils.addr(FROH_IP));
        SnmpWalker walker = SnmpUtils.createWalker(snmpAgent, name, tracker);
        walker.start();

        try {
            walker.waitFor();
        } catch (final InterruptedException e) {

        }

        assertEquals(IsisAdminState.ON, m_isisSystemObjectGroup.getIsisSysAdminState());
        assertEquals(FROH_ISIS_SYS_ID, m_isisSystemObjectGroup.getIsisSysId());
    }

    @Test
    @JUnitSnmpAgents(value = {
            @JUnitSnmpAgent(host = FROH_IP, port = 161, resource = "classpath:linkd/nms0001/" + FROH_NAME + "-"+FROH_IP + "-walk.txt"),
            @JUnitSnmpAgent(host = OEDIPUS_IP, port = 161, resource = "classpath:linkd/nms0001/" + OEDIPUS_NAME + "-"+OEDIPUS_IP + "-walk.txt"),
            @JUnitSnmpAgent(host = SIEGFRIE_IP, port = 161, resource = "classpath:linkd/nms0001/" + SIEGFRIE_NAME + "-"+SIEGFRIE_IP + "-walk.txt")
    })
    public void testIsisISAdjTableCollection() throws Exception {

        String name = "isisISAdjTable";
        IsisISAdjTable m_isisISAdjTable = new IsisISAdjTable(InetAddressUtils.addr(FROH_IP));
        CollectionTracker[] tracker = new CollectionTracker[0];
        tracker = new CollectionTracker[]{m_isisISAdjTable};
        SnmpAgentConfig snmpAgent = SnmpPeerFactory.getInstance().getAgentConfig(InetAddressUtils.addr(FROH_IP));
        SnmpWalker walker = SnmpUtils.createWalker(snmpAgent, name, tracker);
        walker.start();

        try {
            walker.waitFor();
        } catch (final InterruptedException e) {
            assertEquals(false, true);
        }

        final Collection<IsisISAdjTableEntry> lldpTableEntryCollection = m_isisISAdjTable.getEntries();
        assertEquals(2, lldpTableEntryCollection.size());
    }

}
