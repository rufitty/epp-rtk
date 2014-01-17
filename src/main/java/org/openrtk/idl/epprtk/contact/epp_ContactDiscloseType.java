package org.openrtk.idl.epprtk.contact;

public enum epp_ContactDiscloseType implements org.omg.CORBA.portable.IDLEntity {

  NAME_INT("name", "int"), NAME_LOC("name", "loc"), ORG_INT("org", "int"), ORG_LOC(
      "org", "loc"), ADDR_INT("addr", "int"), ADDR_LOC("addr", "loc"), VOICE(
      "voice", null), FAX("fax", null), EMAIL("email", null);
  
  private String name;
  private String type;

  private epp_ContactDiscloseType(String name, String type) {
    this.name = name;
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public String getType() {
    return type;
  }

}
