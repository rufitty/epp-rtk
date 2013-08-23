package org.openrtk.idl.epprtk.domain;

import java.util.Arrays;

import org.openrtk.idl.epprtk.host.epp_HostAddress;

/**
 * 
 * (rfc5731, section 1.1)
 * 
 * A <domain:hostAttr> element contains the following child elements:
 * 
 * - A <domain:hostName> element that contains the fully qualified name of a
 * host.
 * 
 * - Zero or more OPTIONAL <domain:hostAddr> {@link epp_HostAddress} elements
 * that contain the IP addresses to be associated with the host
 * 
 * @author Tatyana Nurtdinova (Rufanova)
 * 
 */
public class epp_DomainHostAttr {
  private String _name;
  private epp_HostAddress[] _addresses;

  /**
   * creates empty hostAttr object
   */
  public epp_DomainHostAttr() {
  }

  /**
   * creates hostAttr object with specified name
   * 
   * @param name
   *          fully qualified name
   */
  public epp_DomainHostAttr(String name) {
    this(name, null);
  }

  /**
   * creates hostAttr object with specified name and address
   * 
   * @param name
   *          fully qualified name
   * @param addresses
   *          elements that contain the IP addresses to be associated with the
   *          host
   */
  public epp_DomainHostAttr(String name, epp_HostAddress[] addresses) {
    _name = name;
    _addresses = addresses;
  }

  /**
   * Getter for hostName
   * 
   * @return hostName
   */
  public String getName() {
    return _name;
  }

  /**
   * Setter for host name
   * 
   * @param name
   *          host name
   */
  public void setName(String name) {
    this._name = name;
  }

  /**
   * getter for host addresses
   * 
   * @return
   */
  public epp_HostAddress[] getAddresses() {
    return _addresses;
  }

  public void setHostAddresses(epp_HostAddress[] addresses) {
    this._addresses = addresses;
  }

  @Override
  public String toString() {
    return "{" + _name + ": " + Arrays.toString(_addresses) + "}";
  }
}