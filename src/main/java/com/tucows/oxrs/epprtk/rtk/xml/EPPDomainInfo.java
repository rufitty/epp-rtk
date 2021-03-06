/*
**
** EPP RTK Java
** Copyright (C) 2001-2002, Tucows, Inc.
** Copyright (C) 2003, Liberty RMS
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
 * $Header: /cvsroot/epp-rtk/epp-rtk/java/src/com/tucows/oxrs/epprtk/rtk/xml/EPPDomainInfo.java,v 1.3 2006/04/24 20:13:57 ewang2004 Exp $
 * $Revision: 1.3 $
 * $Date: 2006/04/24 20:13:57 $
 */

package com.tucows.oxrs.epprtk.rtk.xml;

import java.io.*;
import java.util.*;
import java.text.*;

import com.tucows.oxrs.epprtk.rtk.*;
import org.openrtk.idl.epprtk.*;
import org.openrtk.idl.epprtk.contact.epp_ContactDisclose;
import org.openrtk.idl.epprtk.domain.*;
import org.openrtk.idl.epprtk.host.epp_HostAddress;
import org.openrtk.idl.epprtk.host.epp_HostAddressType;

import org.w3c.dom.*;
import org.w3c.dom.traversal.*;
import org.apache.xerces.parsers.*;
import org.xml.sax.*;
import org.apache.xerces.dom.*;
import org.apache.xml.serialize.*;

/**
 * Class for the EPP Domain Info command and response.
 * Extends the epp_DomainInfo interface from the EPP IDLs to provide
 * the XML translation for the EPP Domain Info command.</P>
 * The Info command provides a way of retrieving extended information on an object.
 * Usage is demonstrated in the com.tucows.oxrs.epprtk.rtk.example.DomainExample
 * class.
 * @see com.tucows.oxrs.epprtk.rtk.example.DomainExample
 * @see org.openrtk.idl.epprtk.epp_Action
 * @see org.openrtk.idl.epprtk.domain.epp_DomainInfo
 * @see org.openrtk.idl.epprtk.domain.epp_DomainInfoReq
 * @see org.openrtk.idl.epprtk.domain.epp_DomainInfoRsp
 * @see EPP Domain Spec for more information
 */
public class EPPDomainInfo extends EPPDomainBase implements epp_DomainInfo
{

    private epp_DomainInfoReq action_request_;
    private epp_DomainInfoRsp action_response_;

    /**
     * Default constructor
     */
    public EPPDomainInfo () {}

    /**
     * Constructor with response XML string to automatically parse.
     * @param xml The EPP Domain Info response XML String
     * @throws org.openrtk.idl.epprtk.epp_XMLException if the response XML is not parsable or does not contain the expected data
     * @throws org.openrtk.idl.epprtk.epp_Exception if the server has responded with an error code 
     * @see #fromXML(String)
     */
    public EPPDomainInfo (String xml) throws epp_XMLException, epp_Exception
    {
        String method_name = "EPPDomainInfo(String)";
        debug(DEBUG_LEVEL_TWO,method_name,"xml is ["+xml+"]");
        fromXML(xml);
    }

    /**
     * Accessor method for the domain info request data.
     * Must be set to for this command.
     * @param value org.openrtk.idl.epprtk.epp_DomainInfoReq
     */
    public void setRequestData(epp_DomainInfoReq value) { action_request_ = value; }
    /**
     * Accessor method for the domain info response data.
     * @return value org.openrtk.idl.epprtk.epp_DomainInfoRsp
     */
    public epp_DomainInfoRsp getResponseData() { return action_response_; }

    /**
     * Builds request XML from the request data.
     * Implemented method from org.openrtk.idl.epprtk.epp_Action interface.
     * @throws epp_XMLException if required data is missing
     * @see #setRequestData(epp_DomainInfoReq)
     * @see org.openrtk.idl.epprtk.epp_Action
     */
    public String toXML() throws epp_XMLException
    {
        String method_name = "buildRequestXML()";
        debug(DEBUG_LEVEL_THREE,method_name,"Entered");

        if ( action_request_ == null || 
             action_request_.m_cmd == null ||
             action_request_.m_name == null )
        {
            throw new epp_XMLException("missing request data or domain name");
        }

        Document doc = new DocumentImpl();
        Element root = createDocRoot(doc);

        Element command = doc.createElement("command");
        Element info = doc.createElement("info");

        epp_Command command_data = action_request_.m_cmd;

        Element domain_info = doc.createElement("domain:info");
        setCommonAttributes(domain_info);

        Element domain_name = addXMLElement(doc, domain_info, "domain:name", action_request_.m_name);

        //if registry does not support hostObj, then do not specify hosts type
        if( action_request_.m_hosts_type != null )
        {
            domain_name.setAttribute("hosts", action_request_.m_hosts_type.toString());
        }
		if (action_request_.m_auth_info != null) 
		{
			domain_info.appendChild( prepareAuthInfo( doc, "domain", action_request_.m_auth_info ) ); 
		}

        info.appendChild( domain_info );

        command.appendChild( info );

        prepareExtensionElement( doc, command, command_data.m_extensions );

        if ( command_data.m_client_trid != null )
        {
            addXMLElement(doc, command, "clTRID", command_data.m_client_trid);
        }

        root.appendChild( command );
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
     * Implemented method from org.openrtk.idl.epprtk.epp_Action interface.
     * @param A new XML String to parse
     * @throws epp_XMLException if the response XML is not parsable or does not contain the expected data
     * @throws org.openrtk.idl.epprtk.epp_Exception if the server has responded with an error code 
     * @see org.openrtk.idl.epprtk.epp_Action
     */
    public void fromXML(String xml) throws epp_XMLException, epp_Exception
    {
        String method_name = "fromXML()";
        debug(DEBUG_LEVEL_THREE,method_name,"Entered");

        xml_ = xml;

        try
        {
            Element epp_node = getDocumentElement();
            Node response_node = epp_node.getElementsByTagNameNS(EPP_NS, "response").item(0);

            if ( response_node == null )
            {
                throw new epp_XMLException("unparsable or missing response");
            }

            action_response_ = new epp_DomainInfoRsp();
            
            action_response_.m_rsp = parseGenericResult(response_node);

            if ( action_response_.m_rsp.m_results[0].m_code >= epp_Session.EPP_UNKNOWN_COMMAND )
            {
                throw new epp_Exception(action_response_.m_rsp.m_results);
            }

            Element response_data_element = getElement(response_node.getChildNodes(), "resData");

            NodeList domain_info_result_list = response_data_element.getElementsByTagNameNS(EPP_DOMAIN_NS, "infData").item(0).getChildNodes();

            debug(DEBUG_LEVEL_TWO,method_name,"domain:infData's node count ["+domain_info_result_list.getLength()+"]");

            if ( domain_info_result_list.getLength() == 0 )
            {
                throw new epp_XMLException("missing info results");
            }

            List statuses = (List)new ArrayList();
            List contacts = (List)new ArrayList();
            List name_servers = (List)new ArrayList();
            List hosts = (List)new ArrayList();
            List<epp_DomainHostAttr> hostAttrList = new ArrayList<epp_DomainHostAttr>();

            for (int count = 0; count < domain_info_result_list.getLength(); count++)
            {
                Node a_node = domain_info_result_list.item(count);

                if ( a_node.getLocalName().equals("name") ) { action_response_.m_name = a_node.getFirstChild().getNodeValue(); }
                if ( a_node.getLocalName().equals("roid") ) { action_response_.m_roid = a_node.getFirstChild().getNodeValue(); }

                if ( a_node.getLocalName().equals("registrant") ) { action_response_.m_registrant = a_node.getFirstChild().getNodeValue(); }
                if ( a_node.getLocalName().equals("contact") )
                {
                    epp_DomainContact domain_contact = new epp_DomainContact();
                    domain_contact.m_id = a_node.getTextContent();
                    domain_contact.m_type = (epp_DomainContactType)contact_type_hash_.get( ((Element)a_node).getAttribute("type") );
                    contacts.add(domain_contact);
                }

                if ( a_node.getLocalName().equals("status") )
                {
                    epp_DomainStatus status = new epp_DomainStatus();
                    Node status_value_node = a_node.getFirstChild();
                    if ( status_value_node != null )
                    {
                        status.m_value = status_value_node.getNodeValue();
                    }
                    String lang = ((Element)a_node).getAttribute("lang");
                    if (lang != null && lang.length() > 0)
                    {
                        status.m_lang = lang;
                    }
                    if ( ! domain_status_hash_.containsKey( ((Element)a_node).getAttribute("s") ) )
                    {
                        status.m_value = "invalid domain status from server";
                        status.m_lang = "en";
                        status.m_type = null;
                    }
                    else
                    {
                        status.m_type = (epp_DomainStatusType)domain_status_hash_.get( ((Element)a_node).getAttribute("s") );
                    }
                    statuses.add(status);
                }

                if ( a_node.getLocalName().equals("ns") ) 
                { 
                    NodeList hostObjectsNodes = a_node.getChildNodes();
                    if ( hostObjectsNodes.getLength() == 0 )
                    {
                        throw new epp_XMLException("missing domain:hostObj in domain:ns results");             
                    }

                    for (int i = 0; i < hostObjectsNodes.getLength(); i++)
                    {
                        Node hostObject = hostObjectsNodes.item(i);
                        if ( hostObject.getLocalName().equals("hostObj") ) 
                        {
                            name_servers.add(hostObject.getFirstChild().getNodeValue());
                        } else if ( hostObject.getLocalName().equals("hostAttr") )
                        {
                            String name = null;
                            List<epp_HostAddress> hostAddressList = new LinkedList<epp_HostAddress>();
                            NodeList hostAttrNodeList = hostObject.getChildNodes();
                            for ( int iHA = 0; iHA < hostAttrNodeList.getLength(); iHA++ )
                            {
                                Node hostAttrNode = hostAttrNodeList.item(iHA);
                                String hostAttrNodeName = hostAttrNode.getLocalName();

                                if ( hostAttrNodeName.equals("hostName") )
                                {
                                    name = hostAttrNode.getFirstChild().getNodeValue();
                                } else if ( hostAttrNodeName.equals("hostAddr") )
                                {
                                    String ip = hostAttrNode.getFirstChild().getNodeValue();
                                    Node type = hostAttrNode.getAttributes().getNamedItem("ip");
                                    String typeString = (type == null) ? "v4" : type.getNodeValue();

                                    hostAddressList.add(new epp_HostAddress("v6".equals(typeString) ? epp_HostAddressType.IPV6 : epp_HostAddressType.IPV4, ip));
                                } else
                                {
                                    throw new epp_XMLException("not supporting " + hostObject.getNodeName() + " in domain:hostAttr results");
                                }
                            }
                            hostAttrList.add(new epp_DomainHostAttr(name, hostAddressList.toArray(new epp_HostAddress[0])));
                        } else {
                          throw new epp_XMLException("not supporting " + hostObject.getNodeName() + " in domain:ns results");
                        }
                    }
                }
                if ( a_node.getLocalName().equals("host") ) { hosts.add(a_node.getFirstChild().getNodeValue()); }

                if ( a_node.getLocalName().equals("clID") ) { action_response_.m_client_id = a_node.getFirstChild().getNodeValue(); }

                if ( a_node.getLocalName().equals("crID") ) { action_response_.m_created_by = a_node.getFirstChild().getNodeValue(); }
                if ( a_node.getLocalName().equals("crDate") ) { action_response_.m_created_date = a_node.getFirstChild().getNodeValue(); }
                if ( a_node.getLocalName().equals("upID") ) { action_response_.m_updated_by = a_node.getFirstChild().getNodeValue(); }
                if ( a_node.getLocalName().equals("upDate") ) { action_response_.m_updated_date = a_node.getFirstChild().getNodeValue(); }

                if ( a_node.getLocalName().equals("trDate") ) { action_response_.m_transfer_date = a_node.getFirstChild().getNodeValue(); }
                if ( a_node.getLocalName().equals("exDate") ) { action_response_.m_expiration_date = a_node.getFirstChild().getNodeValue(); }

                if ( a_node.getLocalName().equals("authInfo") )
                {
                    action_response_.m_auth_info = new epp_AuthInfo();
                    Node auth_info_child = a_node.getFirstChild();
                    if ( auth_info_child == null )
                    {
                        throw new epp_XMLException("authInfo element missing sub-element");
                    }
                    action_response_.m_auth_info.m_value = auth_info_child.getTextContent();
                    action_response_.m_auth_info.m_type = (epp_AuthInfoType)auth_type_string_to_type_hash_.get( ((Element)auth_info_child).getLocalName() );
                    action_response_.m_auth_info.m_roid = ((Element)auth_info_child).getAttribute("roid");
                }
            }

            if ( name_servers.size() > 0 ) { action_response_.m_name_servers = convertListToStringArray(name_servers); }
            if ( hostAttrList.size() > 0 ) { action_response_.m_host_attrs   = hostAttrList.toArray(new epp_DomainHostAttr[0]); }
            if ( hosts.size()        > 0 ) { action_response_.m_hosts        = convertListToStringArray(hosts); }

            if ( contacts.size() > 0 ) { action_response_.m_contacts = (epp_DomainContact[]) convertListToArray((new epp_DomainContact()).getClass(), contacts); }
            if ( statuses.size() > 0 ) { action_response_.m_status   = (epp_DomainStatus[])  convertListToArray((new epp_DomainStatus()).getClass(),  statuses); }

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
