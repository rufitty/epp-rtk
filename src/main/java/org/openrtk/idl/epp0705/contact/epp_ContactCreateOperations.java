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

package org.openrtk.idl.epp0705.contact;


/**
 * Internal IDL interface which is never referenced externally.</p>
 * Sub-interface epp_ContactCreate is implemented by EPPContactCreate.
 * $Header: /cvsroot/epp-rtk/epp-rtk/java/src/org/openrtk/idl/epp0705/contact/epp_ContactCreateOperations.java,v 1.1 2003/03/20 22:42:24 tubadanm Exp $<br>
 * $Revision: 1.1 $<br>
 * $Date: 2003/03/20 22:42:24 $<br>
 * @see org.openrtk.idl.epp0705.contact.epp_ContactCreate
 */
public interface epp_ContactCreateOperations  extends org.openrtk.idl.epp0705.epp_ActionOperations
{
  /**
   * Sets the request data for an outgoing Contact Create EPP request.
   * The implementor of this method is responsible for translating
   * the request parms into equivalent Contact Create EPP XML.
   * @param parms The EPP request data
   */
  void setRequestData (org.openrtk.idl.epp0705.contact.epp_ContactCreateReq parms);
  /**
   * Accessor for the data representing EPP response from a server for the
   * contact create command.
   * The implementor of this method is responsible for translating
   * the response EPP XML into an instance of epp_ContactCreateRsp.
   * @returns The Contact Create response
   */
  org.openrtk.idl.epp0705.contact.epp_ContactCreateRsp getResponseData ();
} // interface epp_ContactCreateOperations
