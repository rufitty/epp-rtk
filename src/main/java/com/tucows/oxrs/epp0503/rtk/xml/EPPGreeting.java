/*
**
** EPP RTK Java
** Copyright (C) 2001, Tucows, Inc.
**
**
** This library is free software; you can redistribute it and/or
** modify it under the terms of the GNU Lesser General Public
** License as published by the Free Software Foundation; either
** version 2.1 of the License, or (at your option) any later version.
** 
** This library is distributed in the hope that it will be useful,
** but WITHOUT ANY WARRANTY; without even the implied warranty of
** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
** Lesser General Public License for more details.
** 
** You should have received a copy of the GNU Lesser General Public
** License along with this library; if not, write to the Free Software
** Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
** 
*/

/*
 * $Header: /cvsroot/epp-rtk/epp-rtk/java/src/com/tucows/oxrs/epp0503/rtk/xml/EPPGreeting.java,v 1.1 2003/03/21 16:18:22 tubadanm Exp $
 * $Revision: 1.1 $
 * $Date: 2003/03/21 16:18:22 $
 */

package com.tucows.oxrs.epp0503.rtk.xml;

import java.io.*;
import java.util.*;
import java.lang.reflect.*;

import com.tucows.oxrs.epp0503.rtk.*;
import org.openrtk.idl.epp0503.*;

import org.w3c.dom.*;
import org.w3c.dom.traversal.*;
import org.apache.xerces.parsers.*;
import org.xml.sax.*;
import org.apache.xerces.dom.*;
import org.apache.xml.serialize.*;

/**
 * Class to represent the EPP greeting from the server.
 *
 * @author Daniel Manley
 * @version $Revision: 1.1 $ $Date: 2003/03/21 16:18:22 $
 * @see com.tucows.oxrs.epp0503.rtk.EPPClient
 */
public class EPPGreeting extends EPPXMLBase implements epp_Hello
{

    private epp_Greeting greeting_;
    private static Hashtable dcp_access_string_to_type_hash_ = null;
    private static Hashtable dcp_purpose_string_to_type_hash_ = null;
    private static Hashtable dcp_recipient_string_to_type_hash_ = null;
    private static Hashtable dcp_retention_string_to_type_hash_ = null;

    static
    {
	dcp_access_string_to_type_hash_ = new Hashtable();
	dcp_access_string_to_type_hash_.put("all", epp_dcpAccessType.ALL);
	dcp_access_string_to_type_hash_.put("noaccess", epp_dcpAccessType.NO_ACCESS);
	dcp_access_string_to_type_hash_.put("null", epp_dcpAccessType.NULL_ACCESS);
	dcp_access_string_to_type_hash_.put("social", epp_dcpAccessType.SOCIAL);
	dcp_access_string_to_type_hash_.put("technical", epp_dcpAccessType.TECHNICAL);

	dcp_purpose_string_to_type_hash_ = new Hashtable();
	dcp_purpose_string_to_type_hash_.put("contact", epp_dcpPurposeType.CONTACT);
	dcp_purpose_string_to_type_hash_.put("dnreg", epp_dcpPurposeType.DN_REG);
	dcp_purpose_string_to_type_hash_.put("ipreg", epp_dcpPurposeType.IP_REG);
	dcp_purpose_string_to_type_hash_.put("other", epp_dcpPurposeType.OTHER_PURPOSE);
	dcp_purpose_string_to_type_hash_.put("tmreg", epp_dcpPurposeType.TM_REG);

	dcp_recipient_string_to_type_hash_ = new Hashtable();
	dcp_recipient_string_to_type_hash_.put("other", epp_dcpRecipientType.OTHER_RECIPIENT);
	dcp_recipient_string_to_type_hash_.put("ours", epp_dcpRecipientType.OURS);
	dcp_recipient_string_to_type_hash_.put("public", epp_dcpRecipientType.PUBLIK);
	dcp_recipient_string_to_type_hash_.put("same", epp_dcpRecipientType.SAME);
	dcp_recipient_string_to_type_hash_.put("unrelated", epp_dcpRecipientType.UNRELATED);

	dcp_retention_string_to_type_hash_ = new Hashtable();
	dcp_retention_string_to_type_hash_.put("business", epp_dcpRetentionType.BUSINESS);
	dcp_retention_string_to_type_hash_.put("functional", epp_dcpRetentionType.FUNCTIONAL);
	dcp_retention_string_to_type_hash_.put("indefinite", epp_dcpRetentionType.INDEFINITE);
	dcp_retention_string_to_type_hash_.put("legal", epp_dcpRetentionType.LEGAL);
	dcp_retention_string_to_type_hash_.put("none", epp_dcpRetentionType.NONE);
    }

    /**
     * Default constructor.
     */
    public EPPGreeting () {}

    /**
     * Constructor with greeting XML string to automatically parse.
     * @param xml The EPP Greeting XML String
     * @throws org.openrtk.idl.epp0503.epp_XMLException if the response XML is not parsable or does not contain the expected data
     * @throws org.openrtk.idl.epp0503.epp_Exception if the server has responded with an error code 
     * @see #fromXML(String)
     */
    public EPPGreeting (String xml) throws epp_XMLException, epp_Exception
    {
        String method_name = "EPPGreeting(String)";
        debug(DEBUG_LEVEL_TWO,method_name,"xml is ["+xml+"]");
        fromXML(xml);
    }

    /**
     * Accessor method for the greeting data.
     * @return value org.openrtk.idl.epp0503.epp_Greeting
     */
    public epp_Greeting getResponseData() { return greeting_; }

    /**
     * Builds request XML from the request data.
     * Implemented method from org.openrtk.idl.epp0503.epp_Action interface.
     * Note that there is no request data to set for this action.
     * @throws epp_XMLException if required data is missing
     * @see org.openrtk.idl.epp0503.epp_Action
     */
    public String toXML() throws epp_XMLException
    {
        String method_name = "buildRequestXML()";
        debug(DEBUG_LEVEL_THREE,method_name,"Entered");

        Document doc = new DocumentImpl();
        Element root = createDocRoot(doc);
        
        Element hello = doc.createElement("hello");

        root.appendChild( hello );
        doc.appendChild( root );
        
        String request_xml;
        
            try
        {
            request_xml = createXMLFromDoc(doc);
        }
        catch (IOException xcp)
        {
            throw new epp_XMLException("IOException in building XML ["+xcp.getMessage()+"]");
        }

        debug(DEBUG_LEVEL_THREE,method_name,"Leaving");

        return request_xml;
    }

    /**
     * Parses a new XML String and populates the response data member.
     * Implemented method from org.openrtk.idl.epp0503.epp_Action interface.
     * @param A new XML String to parse
     * @throws epp_XMLException if the response XML is not parsable or does not contain the expected data
     * @throws org.openrtk.idl.epp0503.epp_Exception if the server has responded with an error code 
     * @see org.openrtk.idl.epp0503.epp_Action
     */
    public void fromXML(String xml) throws epp_XMLException, epp_Exception
    {
        String method_name = "fromXML()";
        debug(DEBUG_LEVEL_THREE,method_name,"Entered");

        xml_ = xml;

        try
        {
            greeting_ = new epp_Greeting();
            
            Element epp_node = getDocumentElement();
            NodeList greeting_nodes = epp_node.getElementsByTagName("greeting").item(0).getChildNodes();

            if ( greeting_nodes == null || greeting_nodes.getLength() == 0 )
            {
                throw new epp_XMLException("unparsable or missing greeting");
            }

            Node service_menu = null;
	    Node dcp = null;
            
            for (int count = 0; count < greeting_nodes.getLength(); count++)
            {
                Node a_node = greeting_nodes.item(count);
    
                if ( a_node.getNodeName().equals("svID") ) { greeting_.m_server_id = a_node.getFirstChild().getNodeValue(); }
                if ( a_node.getNodeName().equals("svDate") ) { greeting_.m_server_date = a_node.getFirstChild().getNodeValue(); }
                if ( a_node.getNodeName().equals("svcMenu") ) { service_menu = a_node; }
                if ( a_node.getNodeName().equals("dcp") ) { dcp = a_node; }
            }

            if (service_menu != null)
            {
		epp_ServiceMenu svc_menu = new epp_ServiceMenu();
                NodeList inner_nodes = service_menu.getChildNodes();
                
                List service_list = (List)new ArrayList();
		List extension_list = (List)new ArrayList();
                List version_list = (List)new ArrayList();
                List lang_list = (List)new ArrayList();
	        Node service_extension = null;
                
                for (int count = 0; count < inner_nodes.getLength(); count++)
                {
                    Node a_node = inner_nodes.item(count);
        
                    if ( a_node.getNodeName().equals("version") ) { version_list.add(a_node.getFirstChild().getNodeValue()); }
                    if ( a_node.getNodeName().equals("lang") ) { lang_list.add(a_node.getFirstChild().getNodeValue()); }
		    if ( a_node.getNodeName().equals("svcExtension") ) { service_extension = a_node; }
                    if ( a_node.getNodeName().equals("objURI") ) { service_list.add(a_node.getFirstChild().getNodeValue()); }
                }
                
                svc_menu.m_versions = convertListToStringArray(version_list);
                svc_menu.m_langs = convertListToStringArray(lang_list);
		svc_menu.m_services = convertListToStringArray(service_list);

		if (service_extension != null) {
		    NodeList ext_nodes = service_extension.getChildNodes();

		    for (int count = 0; count < ext_nodes.getLength(); count++)
		    {
			Node a_node = ext_nodes.item(count);
			if ( a_node.getNodeName().equals("extURI") ) { extension_list.add(a_node.getFirstChild().getNodeValue()); }
		    }
		}

		if (extension_list.size() > 0)
		{
		    svc_menu.m_extensions = convertListToStringArray(extension_list);
		}

		greeting_.m_svc_menu = svc_menu;
            }
	    
            if (dcp != null)
            {
		epp_DataCollectionPolicy dc_policy = new epp_DataCollectionPolicy();
                NodeList inner_nodes = dcp.getChildNodes();
                
                List statement_list = (List)new ArrayList();
                
                for (int count = 0; count < inner_nodes.getLength(); count++)
                {
                    Node a_node = inner_nodes.item(count);
        
                    if ( a_node.getNodeName().equals("access") )
		    { 
			String type = a_node.getFirstChild().getNodeName().toLowerCase();
			dc_policy.m_access = (epp_dcpAccessType)dcp_access_string_to_type_hash_.get(type);
		    }
                    if ( a_node.getNodeName().equals("statement") )
		    {
			NodeList s_inner_nodes = a_node.getChildNodes();
			NodeList purpose_nodes = null;
			NodeList recipients_nodes = null;
			List purpose_list = (List)new ArrayList();
			List recipients_list = (List)new ArrayList();
			epp_dcpStatement dcp_statement = new epp_dcpStatement();
			for (int i = 0; i < s_inner_nodes.getLength(); i++)
			{
			    Node s_node = s_inner_nodes.item(i);
			    if (s_node.getNodeName().equals("purpose")) { purpose_nodes = s_node.getChildNodes(); }
			    if (s_node.getNodeName().equals("recipient")) { recipients_nodes = s_node.getChildNodes(); }
			    if (s_node.getNodeName().equals("retention"))
			    {
				String type = s_node.getFirstChild().getNodeName().toLowerCase();
				dcp_statement.m_retention = (epp_dcpRetentionType)dcp_retention_string_to_type_hash_.get(type);
			    }
			}
			if ( purpose_nodes != null )
			{
			    for (int i = 0; i < purpose_nodes.getLength(); i++)
			    {
				String type = purpose_nodes.item(i).getNodeName().toLowerCase();
				purpose_list.add(dcp_purpose_string_to_type_hash_.get(type));
			    }
			    dcp_statement.m_purposes = (epp_dcpPurposeType[])convertListToArray(epp_dcpPurposeType.class, purpose_list);
			}
			if ( recipients_nodes != null )
			{
			    for (int i = 0; i < recipients_nodes.getLength(); i++)
			    {
				String type = recipients_nodes.item(i).getNodeName().toLowerCase();
				recipients_list.add(dcp_recipient_string_to_type_hash_.get(type));
			    }
			    dcp_statement.m_recipients = (epp_dcpRecipientType[])convertListToArray(epp_dcpRecipientType.class, recipients_list);
			}

			statement_list.add(dcp_statement);
                    }
		}
		dc_policy.m_statements = (epp_dcpStatement[])convertListToArray(epp_dcpStatement.class, statement_list);
		greeting_.m_dcp = dc_policy;
	    }
        }
        catch (SAXException xcp)
        {
            debug(DEBUG_LEVEL_ONE,method_name,xcp);
            throw new epp_XMLException("unable to parse xml ["+xcp.getClass().getName()+"] ["+xcp.getMessage()+"]");
        }
        catch (IOException xcp)
        {
            debug(DEBUG_LEVEL_ONE,method_name,xcp);
            throw new epp_XMLException("unable to parse xml ["+xcp.getClass().getName()+"] ["+xcp.getMessage()+"]");
        }

    }
    
}
