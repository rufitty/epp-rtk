package org.openrtk.idl.epprtk.contact;

public class epp_ContactDisclose implements org.omg.CORBA.portable.IDLEntity {
  public boolean m_flag;
  public epp_ContactDiscloseType[] m_typeList;

  public epp_ContactDisclose(boolean m_flag, epp_ContactDiscloseType[] m_typeList) {
    this.m_flag = m_flag;
    this.m_typeList = m_typeList;
  }

  public epp_ContactDisclose() {
  }

  public boolean getFlag() {
    return m_flag;
  }

  public void setFlag(boolean m_flag) {
    this.m_flag = m_flag;
  }

  public epp_ContactDiscloseType[] getTypeList() {
    return m_typeList;
  }

  public void setTypeList(epp_ContactDiscloseType[] m_typeList) {
    this.m_typeList = m_typeList;
  }

}
