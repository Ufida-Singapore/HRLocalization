/***************************************************************\
 *     The skeleton of this class is generated by an automatic *
 * code generator for NC product. It is based on Velocity.     *
\***************************************************************/
package nc.vo.wa.item;

import nc.hr.utils.MultiLangHelper;
import nc.md.model.impl.MDEnum;
import nc.vo.hr.itemsource.TypeEnumVO;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;

/**
 * <b> 在此处简要描述此类的功能 </b>
 * <p>
 * 在此处添加此类的描述信息
 * </p>
 * 创建日期:2009-08-26 16:52:43
 * 
 * @author zhoucx
 * @version NCPrj ??
 */
@SuppressWarnings("serial")
public class WaItemVO extends SuperVO
{
	private java.lang.String pk_wa_item;
	private java.lang.String code;
	private java.lang.String name;
	private java.lang.String name2;
	private java.lang.String name3;
	private java.lang.String name4;
	private java.lang.String name5;
	private java.lang.String name6;
	private java.lang.String pk_group;
	private java.lang.String pk_org;
	private java.lang.String creator;
	private nc.vo.pub.lang.UFDateTime creationtime;
	private java.lang.String modifier;
	private nc.vo.pub.lang.UFDateTime modifiedtime;
	private java.lang.String category_id;
	private nc.vo.pub.lang.UFBoolean mid = UFBoolean.FALSE;
	private java.lang.String itemkey;
	private java.lang.Integer iitemtype = TypeEnumVO.FLOATTYPE.toIntValue();
	private nc.vo.pub.lang.UFBoolean defaultflag = UFBoolean.FALSE;
	private java.lang.Integer iproperty = PropertyEnumVO.ADD.toIntValue();
	private java.lang.Integer iflddecimal = 2;
	private java.lang.Integer iprivil;
	private nc.vo.pub.lang.UFBoolean isinhi = UFBoolean.FALSE;
	private java.lang.Integer ifromflag = 2;
	private nc.vo.pub.lang.UFBoolean taxflag = UFBoolean.FALSE;
	private java.lang.String vformula;
	private java.lang.String vformulastr;
	
	// 添加自定义项
	private java.lang.String def1;
	private java.lang.String def2;
	private java.lang.String def3;
	private java.lang.String def4;
	private java.lang.String def5;
	private java.lang.String def6;
	private java.lang.String def7;
	private java.lang.String def8;
	private java.lang.String def9;
	private java.lang.String def10;
	private java.lang.String def11;
	private java.lang.String def12;
	private java.lang.String def13;
	private java.lang.String def14;
	private java.lang.String def15;
	private java.lang.String def16;
	private java.lang.String def17;
	private java.lang.String def18;
	private java.lang.String def19;
	private java.lang.String def20;
	private java.lang.String def21;
	private java.lang.String def22;
	private java.lang.String def23;
	private java.lang.String def24;
	private java.lang.String def25;
	private java.lang.String def26;
	private java.lang.String def27;
	private java.lang.String def28;
	private java.lang.String def29;
	private java.lang.String def30;
	private java.lang.String def31;
	private java.lang.String def32;
	private java.lang.String def33;
	private java.lang.String def34;
	private java.lang.String def35;
	private java.lang.String def36;
	private java.lang.String def37;
	private java.lang.String def38;
	private java.lang.String def39;
	private java.lang.String def40;

	private nc.vo.pub.lang.UFBoolean clearflag = UFBoolean.FALSE;

	private nc.vo.pub.lang.UFBoolean sumceilflag = UFBoolean.FALSE;
	private nc.vo.pub.lang.UFDouble nsumceil;
	private nc.vo.pub.lang.UFBoolean sumfloorflag = UFBoolean.FALSE;
	private nc.vo.pub.lang.UFDouble nsumfloor;
	private nc.vo.pub.lang.UFBoolean psnceilflag = UFBoolean.FALSE;
	private nc.vo.pub.lang.UFDouble npsnceil;
	private nc.vo.pub.lang.UFBoolean psnfloorflag = UFBoolean.FALSE;
	private nc.vo.pub.lang.UFDouble npsnfloor;
	private java.lang.Integer ifldwidth = 12;
	private java.lang.Integer dr = 0;
	private nc.vo.pub.lang.UFDateTime ts;
	private nc.vo.pub.lang.UFBoolean intotalitem = UFBoolean.FALSE;
	private String totalitem = "";
	private String totalitemstr = "";
	private Integer idisplayseq = 0;

	

	public static final String PK_WA_ITEM = "pk_wa_item";
	public static final String CODE = "code";
	public static final String NAME = "name";
	public static final String NAME2 = "name2";
	public static final String NAME3 = "name3";
	public static final String NAME4 = "name4";
	public static final String NAME5 = "name5";
	public static final String NAME6 = "name6";
	public static final String PK_GROUP = "pk_group";
	public static final String PK_ORG = "pk_org";
	public static final String CREATOR = "creator";
	public static final String CREATIONTIME = "creationtime";
	public static final String MODIFIER = "modifier";
	public static final String MODIFIEDTIME = "modifiedtime";
	public static final String CATEGORY_ID = "category_id";
	public static final String MID = "mid";
	public static final String ITEMKEY = "itemkey";
	public static final String IITEMTYPE = "iitemtype";
	public static final String DEFAULTFLAG = "defaultflag";
	public static final String IPROPERTY = "iproperty";
	public static final String IFLDDECIMAL = "iflddecimal";
	public static final String IPRIVIL = "iprivil";
	public static final String ISINHI = "isinhi";
	public static final String IFROMFLAG = "ifromflag";
	public static final String TAXFLAG = "taxflag";
	public static final String VFORMULA = "vformula";
	public static final String VFORMULASTR = "vformulastr";
	public static final String NFIXAMOUNT = "nfixamount";
	public static final String CLEARFLAG = "clearflag";

	public static final String SUMCEILFLAG = "sumceilflag";
	public static final String NSUMCEIL = "nsumceil";
	public static final String SUMFLOORFLAG = "sumfloorflag";
	public static final String NSUMFLOOR = "nsumfloor";
	public static final String PSNCEILFLAG = "psnceilflag";
	public static final String NPSNCEIL = "npsnceil";
	public static final String PSNFLOORFLAG = "psnfloorflag";
	public static final String NPSNFLOOR = "npsnfloor";
	public static final String PK_WA_GRADE = "pk_wa_grade";
	public static final String PK_WA_WAGEFORM = "pk_wa_wageform";
	public static final String IFLDWIDTH = "ifldwidth";

	public static final String INTOTALITEM = "intotalitem";
	public static final String TOTALITEM = "totalitem";
	public static final String IDISPLAYSEQ = "idisplayseq";
	public static final String TABLE_NAME = "wa_item";
	
	public static final String DEF1 = "def1";
	public static final String DEF2 = "def2";
	public static final String DEF3 = "def3";
	public static final String DEF4 = "def4";
	public static final String DEF5 = "def5";
	public static final String DEF6 = "def6";
	public static final String DEF7 = "def7";
	public static final String DEF8 = "def8";
	public static final String DEF9 = "def9";
	public static final String DEF10 = "def10";
	public static final String DEF11 = "def11";
	public static final String DEF12 = "def12";
	public static final String DEF13 = "def13";
	public static final String DEF14 = "def14";
	public static final String DEF15 = "def15";
	public static final String DEF16 = "def16";
	public static final String DEF17 = "def17";
	public static final String DEF18 = "def18";
	public static final String DEF19 = "def19";
	public static final String DEF20 = "def20";
	public static final String DEF21 = "def21";
	public static final String DEF22 = "def22";
	public static final String DEF23 = "def23";
	public static final String DEF24 = "def24";
	public static final String DEF25 = "def25";
	public static final String DEF26 = "def26";
	public static final String DEF27 = "def27";
	public static final String DEF28 = "def28";
	public static final String DEF29 = "def29";
	public static final String DEF30 = "def30";
	public static final String DEF31 = "def31";
	public static final String DEF32 = "def32";
	public static final String DEF33 = "def33";
	public static final String DEF34 = "def34";
	public static final String DEF35 = "def35";
	public static final String DEF36 = "def36";
	public static final String DEF37 = "def37";
	public static final String DEF38 = "def38";
	public static final String DEF39 = "def39";
	public static final String DEF40 = "def40";
	
	
	

	public Integer getIdisplayseq()
	{
		return idisplayseq;
	}

	public void setIdisplayseq(Integer idisplayseq)
	{
		this.idisplayseq = idisplayseq;
	}

	/**
	 * 属性pk_wa_item的Getter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getPk_wa_item()
	{
		return pk_wa_item;
	}

	/**
	 * 属性pk_wa_item的Setter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @param newPk_wa_item
	 *            java.lang.String
	 */
	public void setPk_wa_item(java.lang.String newPk_wa_item)
	{
		this.pk_wa_item = newPk_wa_item;
	}

	/**
	 * 属性code的Getter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getCode()
	{
		return code;
	}

	/**
	 * 属性code的Setter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @param newCode
	 *            java.lang.String
	 */
	public void setCode(java.lang.String newCode)
	{
		this.code = newCode;
	}

	/**
	 * 属性name的Getter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @return java.lang.String
	 */
	public String getName()
    {
        return name;
    } 
	
	public String getMultilangName() {
		return MultiLangHelper.getName(this);
	}
	
	
	

	
	
	/**
	 * 属性name的Setter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @param newName
	 *            java.lang.String
	 */
	public void setName(java.lang.String newName)
	{
		this.name = newName;
	}

	

	/**
	 * 属性name2的Getter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getName2()
	{
		return name2;
	}

	public nc.vo.pub.lang.UFBoolean getIntotalitem()
	{
		return intotalitem;
	}

	public void setIntotalitem(nc.vo.pub.lang.UFBoolean intotalitem)
	{
		this.intotalitem = intotalitem;
	}

	public String getTotalitem()
	{
		return totalitem;
	}

	public void setTotalitem(String totalitem)
	{
		this.totalitem = totalitem;
	}

	/**
	 * 属性name2的Setter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @param newName2
	 *            java.lang.String
	 */
	public void setName2(java.lang.String newName2)
	{
		this.name2 = newName2;
	}

	/**
	 * 属性name3的Getter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getName3()
	{
		return name3;
	}

	/**
	 * 属性name3的Setter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @param newName3
	 *            java.lang.String
	 */
	public void setName3(java.lang.String newName3)
	{
		this.name3 = newName3;
	}

	/**
	 * 属性pk_group的Getter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getPk_group()
	{
		return pk_group;
	}

	/**
	 * 属性pk_group的Setter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @param newPk_group
	 *            java.lang.String
	 */
	public void setPk_group(java.lang.String newPk_group)
	{
		this.pk_group = newPk_group;
	}

	/**
	 * 属性pk_org的Getter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getPk_org()
	{
		return pk_org;
	}

	/**
	 * 属性pk_org的Setter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @param newPk_org
	 *            java.lang.String
	 */
	public void setPk_org(java.lang.String newPk_org)
	{
		this.pk_org = newPk_org;
	}

	/**
	 * 属性creator的Getter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getCreator()
	{
		return creator;
	}

	/**
	 * 属性creator的Setter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @param newCreator
	 *            java.lang.String
	 */
	public void setCreator(java.lang.String newCreator)
	{
		this.creator = newCreator;
	}

	/**
	 * 属性creationtime的Getter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public nc.vo.pub.lang.UFDateTime getCreationtime()
	{
		return creationtime;
	}

	/**
	 * 属性creationtime的Setter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @param newCreationtime
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setCreationtime(nc.vo.pub.lang.UFDateTime newCreationtime)
	{
		this.creationtime = newCreationtime;
	}

	/**
	 * 属性modifier的Getter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getModifier()
	{
		return modifier;
	}

	/**
	 * 属性modifier的Setter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @param newModifier
	 *            java.lang.String
	 */
	public void setModifier(java.lang.String newModifier)
	{
		this.modifier = newModifier;
	}

	/**
	 * 属性modifiedtime的Getter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public nc.vo.pub.lang.UFDateTime getModifiedtime()
	{
		return modifiedtime;
	}

	/**
	 * 属性modifiedtime的Setter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @param newModifiedtime
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setModifiedtime(nc.vo.pub.lang.UFDateTime newModifiedtime)
	{
		this.modifiedtime = newModifiedtime;
	}

	/**
	 * 属性category_id的Getter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getCategory_id()
	{
		return category_id;
	}

	/**
	 * 属性category_id的Setter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @param newCategory_id
	 *            java.lang.String
	 */
	public void setCategory_id(java.lang.String newCategory_id)
	{
		this.category_id = newCategory_id;
	}

	/**
	 * 属性mid的Getter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public nc.vo.pub.lang.UFBoolean getMid()
	{
		return mid;
	}

	/**
	 * 属性mid的Setter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @param newMid
	 *            nc.vo.pub.lang.UFBoolean
	 */
	public void setMid(nc.vo.pub.lang.UFBoolean newMid)
	{
		this.mid = newMid;
	}

	/**
	 * 属性itemkey的Getter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getItemkey()
	{
		return itemkey;
	}

	/**
	 * 属性itemkey的Setter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @param newItemkey
	 *            java.lang.String
	 */
	public void setItemkey(java.lang.String newItemkey)
	{
		this.itemkey = newItemkey;
	}

	/**
	 * 属性iitemtype的Getter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getIitemtype()
	{
		return iitemtype;
	}

	/**
	 * 属性iitemtype的Setter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @param newIitemtype
	 *            java.lang.Integer
	 */
	public void setIitemtype(java.lang.Integer newIitemtype)
	{
		this.iitemtype = newIitemtype;
	}

	/**
	 * 属性defaultflag的Getter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public nc.vo.pub.lang.UFBoolean getDefaultflag()
	{
		return defaultflag;
	}

	/**
	 * 属性defaultflag的Setter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @param newDefaultflag
	 *            nc.vo.pub.lang.UFBoolean
	 */
	public void setDefaultflag(nc.vo.pub.lang.UFBoolean newDefaultflag)
	{
		this.defaultflag = newDefaultflag;
	}

	/**
	 * 属性iproperty的Getter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getIproperty()
	{
		return iproperty;
	}

	/**
	 * 属性iproperty的Setter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @param newIproperty
	 *            java.lang.Integer
	 */
	public void setIproperty(java.lang.Integer newIproperty)
	{
		this.iproperty = newIproperty;
	}

	/**
	 * 属性iflddecimal的Getter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getIflddecimal()
	{
		return iflddecimal;
	}

	/**
	 * 属性iflddecimal的Setter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @param newIflddecimal
	 *            java.lang.Integer
	 */
	public void setIflddecimal(java.lang.Integer newIflddecimal)
	{
		this.iflddecimal = newIflddecimal;
	}

	/**
	 * 属性iprivil的Getter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getIprivil()
	{
		return iprivil;
	}

	/**
	 * 属性iprivil的Setter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @param newIprivil
	 *            java.lang.Integer
	 */
	public void setIprivil(java.lang.Integer newIprivil)
	{
		this.iprivil = newIprivil;
	}

	/**
	 * 属性isinhi的Getter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public nc.vo.pub.lang.UFBoolean getIsinhi()
	{
		return isinhi;
	}

	/**
	 * 属性isinhi的Setter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @param newIsinhi
	 *            nc.vo.pub.lang.UFBoolean
	 */
	public void setIsinhi(nc.vo.pub.lang.UFBoolean newIsinhi)
	{
		this.isinhi = newIsinhi;
	}

	/**
	 * 属性ifromflag的Getter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getIfromflag()
	{
		return ifromflag;
	}

	/**
	 * 属性ifromflag的Setter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @param newIfromflag
	 *            java.lang.Integer
	 */
	public void setIfromflag(java.lang.Integer newIfromflag)
	{
		this.ifromflag = newIfromflag;
	}

	/**
	 * 属性taxflag的Getter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public nc.vo.pub.lang.UFBoolean getTaxflag()
	{
		return taxflag;
	}

	/**
	 * 属性taxflag的Setter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @param newTaxflag
	 *            nc.vo.pub.lang.UFBoolean
	 */
	public void setTaxflag(nc.vo.pub.lang.UFBoolean newTaxflag)
	{
		this.taxflag = newTaxflag;
	}

	/**
	 * 属性vformula的Getter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVformula()
	{
		return vformula;
	}

	/**
	 * 属性vformula的Setter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @param newVformula
	 *            java.lang.String
	 */
	public void setVformula(java.lang.String newVformula)
	{
		this.vformula = newVformula;
	}

	/**
	 * 属性vformulastr的Getter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getVformulastr()
	{
		return vformulastr;
	}

	/**
	 * 属性vformulastr的Setter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @param newVformulastr
	 *            java.lang.String
	 */
	public void setVformulastr(java.lang.String newVformulastr)
	{
		this.vformulastr = newVformulastr;
	}

	/**
	 * 属性nfixamount的Getter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	// public nc.vo.pub.lang.UFDouble getNfixamount () {
	// return nfixamount;
	// }
	/**
	 * 属性nfixamount的Setter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @param newNfixamount
	 *            nc.vo.pub.lang.UFDouble
	 */
	// public void setNfixamount (nc.vo.pub.lang.UFDouble newNfixamount ) {
	// this.nfixamount = newNfixamount;
	// }
	/**
	 * 属性clearflag的Getter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public nc.vo.pub.lang.UFBoolean getClearflag()
	{
		return clearflag;
	}

	/**
	 * 属性clearflag的Setter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @param newClearflag
	 *            nc.vo.pub.lang.UFBoolean
	 */
	public void setClearflag(nc.vo.pub.lang.UFBoolean newClearflag)
	{
		this.clearflag = newClearflag;
	}

	/**
	 * 属性sumceilflag的Getter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public nc.vo.pub.lang.UFBoolean getSumceilflag()
	{
		return sumceilflag;
	}

	/**
	 * 属性sumceilflag的Setter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @param newSumceilflag
	 *            nc.vo.pub.lang.UFBoolean
	 */
	public void setSumceilflag(nc.vo.pub.lang.UFBoolean newSumceilflag)
	{
		this.sumceilflag = newSumceilflag;
	}

	/**
	 * 属性nsumceil的Getter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getNsumceil()
	{
		return nsumceil;
	}

	/**
	 * 属性nsumceil的Setter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @param newNsumceil
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setNsumceil(nc.vo.pub.lang.UFDouble newNsumceil)
	{
		this.nsumceil = newNsumceil;
	}

	/**
	 * 属性sumfloorflag的Getter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public nc.vo.pub.lang.UFBoolean getSumfloorflag()
	{
		return sumfloorflag;
	}

	/**
	 * 属性sumfloorflag的Setter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @param newSumfloorflag
	 *            nc.vo.pub.lang.UFBoolean
	 */
	public void setSumfloorflag(nc.vo.pub.lang.UFBoolean newSumfloorflag)
	{
		this.sumfloorflag = newSumfloorflag;
	}

	/**
	 * 属性nsumfloor的Getter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getNsumfloor()
	{
		return nsumfloor;
	}

	/**
	 * 属性nsumfloor的Setter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @param newNsumfloor
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setNsumfloor(nc.vo.pub.lang.UFDouble newNsumfloor)
	{
		this.nsumfloor = newNsumfloor;
	}

	/**
	 * 属性psnceilflag的Getter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public nc.vo.pub.lang.UFBoolean getPsnceilflag()
	{
		return psnceilflag;
	}

	/**
	 * 属性psnceilflag的Setter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @param newPsnceilflag
	 *            nc.vo.pub.lang.UFBoolean
	 */
	public void setPsnceilflag(nc.vo.pub.lang.UFBoolean newPsnceilflag)
	{
		this.psnceilflag = newPsnceilflag;
	}

	/**
	 * 属性npsnceil的Getter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getNpsnceil()
	{
		return npsnceil;
	}

	/**
	 * 属性npsnceil的Setter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @param newNpsnceil
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setNpsnceil(nc.vo.pub.lang.UFDouble newNpsnceil)
	{
		this.npsnceil = newNpsnceil;
	}

	/**
	 * 属性psnfloorflag的Getter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public nc.vo.pub.lang.UFBoolean getPsnfloorflag()
	{
		return psnfloorflag;
	}

	/**
	 * 属性psnfloorflag的Setter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @param newPsnfloorflag
	 *            nc.vo.pub.lang.UFBoolean
	 */
	public void setPsnfloorflag(nc.vo.pub.lang.UFBoolean newPsnfloorflag)
	{
		this.psnfloorflag = newPsnfloorflag;
	}

	/**
	 * 属性npsnfloor的Getter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getNpsnfloor()
	{
		return npsnfloor;
	}

	/**
	 * 属性npsnfloor的Setter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @param newNpsnfloor
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setNpsnfloor(nc.vo.pub.lang.UFDouble newNpsnfloor)
	{
		this.npsnfloor = newNpsnfloor;
	}

	// /**
	// * 属性pk_wa_grade的Getter方法.
	// * 创建日期:2009-08-26 16:52:43
	// * @return java.lang.String
	// */
	// public java.lang.String getPk_wa_grade () {
	// return pk_wa_grade;
	// }
	// /**
	// * 属性pk_wa_grade的Setter方法.
	// * 创建日期:2009-08-26 16:52:43
	// * @param newPk_wa_grade java.lang.String
	// */
	// public void setPk_wa_grade (java.lang.String newPk_wa_grade ) {
	// this.pk_wa_grade = newPk_wa_grade;
	// }
	// /**
	// * 属性pk_wa_wageform的Getter方法.
	// * 创建日期:2009-08-26 16:52:43
	// * @return java.lang.String
	// */
	// public java.lang.String getPk_wa_wageform () {
	// return pk_wa_wageform;
	// }
	// /**
	// * 属性pk_wa_wageform的Setter方法.
	// * 创建日期:2009-08-26 16:52:43
	// * @param newPk_wa_wageform java.lang.String
	// */
	// public void setPk_wa_wageform (java.lang.String newPk_wa_wageform ) {
	// this.pk_wa_wageform = newPk_wa_wageform;
	// }

	/**
	 * 属性ifldwidth的Getter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getIfldwidth()
	{
		return ifldwidth;
	}

	/**
	 * 属性ifldwidth的Setter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @param newIfldwidth
	 *            java.lang.Integer
	 */
	public void setIfldwidth(java.lang.Integer newIfldwidth)
	{
		this.ifldwidth = newIfldwidth;
	}

	/**
	 * 属性dr的Getter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getDr()
	{
		return dr;
	}

	/**
	 * 属性dr的Setter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @param newDr
	 *            java.lang.Integer
	 */
	public void setDr(java.lang.Integer newDr)
	{
		this.dr = newDr;
	}

	/**
	 * 属性ts的Getter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public nc.vo.pub.lang.UFDateTime getTs()
	{
		return ts;
	}

	/**
	 * 属性ts的Setter方法. 创建日期:2009-08-26 16:52:43
	 * 
	 * @param newTs
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setTs(nc.vo.pub.lang.UFDateTime newTs)
	{
		this.ts = newTs;
	}

	/**
	 * <p>
	 * 取得父VO主键字段.
	 * <p>
	 * 创建日期:2009-08-26 16:52:43
	 * 
	 * @return java.lang.String
	 */
	@Override
	public java.lang.String getParentPKFieldName()
	{
		return null;
	}

	/**
	 * <p>
	 * 取得表主键.
	 * <p>
	 * 创建日期:2009-08-26 16:52:43
	 * 
	 * @return java.lang.String
	 */
	@Override
	public java.lang.String getPKFieldName()
	{
		return "pk_wa_item";
	}

	/**
	 * <p>
	 * 返回表名称.
	 * <p>
	 * 创建日期:2009-08-26 16:52:43
	 * 
	 * @return java.lang.String
	 */
	@Override
	public java.lang.String getTableName()
	{
		return "wa_item";
	}

	/**
	 * 按照默认方式创建构造子.
	 * 
	 * 创建日期:2009-08-26 16:52:43
	 */
	public WaItemVO()
	{
		super();
	}

	public TypeEnumVO getTypeEnumVO()
	{
		return MDEnum.valueOf(TypeEnumVO.class, getIitemtype());
	}

	public void setTypeEnumVO(TypeEnumVO type)
	{
		setIitemtype(type.value());
	}

	public FromEnumVO getFromEnumVO()
	{
		return MDEnum.valueOf(FromEnumVO.class, getIfromflag());
	}

	public void setFromEnumVO(FromEnumVO from)
	{
		setIfromflag(from.value());
	}

	public PropertyEnumVO getPropertyEnumVO()
	{
		return MDEnum.valueOf(PropertyEnumVO.class, getIproperty());
	}

	@Override
	public String toString()
	{

		return getMultilangName();
	}

	public void setName4(java.lang.String name4) {
		this.name4 = name4;
	}

	public java.lang.String getName4() {
		return name4;
	}

	public void setName5(java.lang.String name5) {
		this.name5 = name5;
	}

	public java.lang.String getName5() {
		return name5;
	}

	public void setName6(java.lang.String name6) {
		this.name6 = name6;
	}

	public java.lang.String getName6() {
		return name6;
	}

	public String getTotalitemstr() {
		return totalitemstr;
	}

	public void setTotalitemstr(String totalitemstr) {
		this.totalitemstr = totalitemstr;
	}

	public java.lang.String getDef1() {
		return def1;
	}

	public void setDef1(java.lang.String def1) {
		this.def1 = def1;
	}

	public java.lang.String getDef2() {
		return def2;
	}

	public void setDef2(java.lang.String def2) {
		this.def2 = def2;
	}

	public java.lang.String getDef3() {
		return def3;
	}

	public void setDef3(java.lang.String def3) {
		this.def3 = def3;
	}

	public java.lang.String getDef4() {
		return def4;
	}

	public void setDef4(java.lang.String def4) {
		this.def4 = def4;
	}

	public java.lang.String getDef5() {
		return def5;
	}

	public void setDef5(java.lang.String def5) {
		this.def5 = def5;
	}

	public java.lang.String getDef6() {
		return def6;
	}

	public void setDef6(java.lang.String def6) {
		this.def6 = def6;
	}

	public java.lang.String getDef7() {
		return def7;
	}

	public void setDef7(java.lang.String def7) {
		this.def7 = def7;
	}

	public java.lang.String getDef8() {
		return def8;
	}

	public void setDef8(java.lang.String def8) {
		this.def8 = def8;
	}

	public java.lang.String getDef9() {
		return def9;
	}

	public void setDef9(java.lang.String def9) {
		this.def9 = def9;
	}

	public java.lang.String getDef10() {
		return def10;
	}

	public void setDef10(java.lang.String def10) {
		this.def10 = def10;
	}

	public java.lang.String getDef11() {
		return def11;
	}

	public void setDef11(java.lang.String def11) {
		this.def11 = def11;
	}

	public java.lang.String getDef12() {
		return def12;
	}

	public void setDef12(java.lang.String def12) {
		this.def12 = def12;
	}

	public java.lang.String getDef13() {
		return def13;
	}

	public void setDef13(java.lang.String def13) {
		this.def13 = def13;
	}

	public java.lang.String getDef14() {
		return def14;
	}

	public void setDef14(java.lang.String def14) {
		this.def14 = def14;
	}

	public java.lang.String getDef15() {
		return def15;
	}

	public void setDef15(java.lang.String def15) {
		this.def15 = def15;
	}

	public java.lang.String getDef16() {
		return def16;
	}

	public void setDef16(java.lang.String def16) {
		this.def16 = def16;
	}

	public java.lang.String getDef17() {
		return def17;
	}

	public void setDef17(java.lang.String def17) {
		this.def17 = def17;
	}

	public java.lang.String getDef18() {
		return def18;
	}

	public void setDef18(java.lang.String def18) {
		this.def18 = def18;
	}

	public java.lang.String getDef19() {
		return def19;
	}

	public void setDef19(java.lang.String def19) {
		this.def19 = def19;
	}

	public java.lang.String getDef20() {
		return def20;
	}

	public void setDef20(java.lang.String def20) {
		this.def20 = def20;
	}

	public java.lang.String getDef21() {
		return def21;
	}

	public void setDef21(java.lang.String def21) {
		this.def21 = def21;
	}

	public java.lang.String getDef22() {
		return def22;
	}

	public void setDef22(java.lang.String def22) {
		this.def22 = def22;
	}

	public java.lang.String getDef23() {
		return def23;
	}

	public void setDef23(java.lang.String def23) {
		this.def23 = def23;
	}

	public java.lang.String getDef24() {
		return def24;
	}

	public void setDef24(java.lang.String def24) {
		this.def24 = def24;
	}

	public java.lang.String getDef25() {
		return def25;
	}

	public void setDef25(java.lang.String def25) {
		this.def25 = def25;
	}

	public java.lang.String getDef26() {
		return def26;
	}

	public void setDef26(java.lang.String def26) {
		this.def26 = def26;
	}

	public java.lang.String getDef27() {
		return def27;
	}

	public void setDef27(java.lang.String def27) {
		this.def27 = def27;
	}

	public java.lang.String getDef28() {
		return def28;
	}

	public void setDef28(java.lang.String def28) {
		this.def28 = def28;
	}

	public java.lang.String getDef29() {
		return def29;
	}

	public void setDef29(java.lang.String def29) {
		this.def29 = def29;
	}

	public java.lang.String getDef30() {
		return def30;
	}

	public void setDef30(java.lang.String def30) {
		this.def30 = def30;
	}

	public java.lang.String getDef31() {
		return def31;
	}

	public void setDef31(java.lang.String def31) {
		this.def31 = def31;
	}

	public java.lang.String getDef32() {
		return def32;
	}

	public void setDef32(java.lang.String def32) {
		this.def32 = def32;
	}

	public java.lang.String getDef33() {
		return def33;
	}

	public void setDef33(java.lang.String def33) {
		this.def33 = def33;
	}

	public java.lang.String getDef34() {
		return def34;
	}

	public void setDef34(java.lang.String def34) {
		this.def34 = def34;
	}

	public java.lang.String getDef35() {
		return def35;
	}

	public void setDef35(java.lang.String def35) {
		this.def35 = def35;
	}

	public java.lang.String getDef36() {
		return def36;
	}

	public void setDef36(java.lang.String def36) {
		this.def36 = def36;
	}

	public java.lang.String getDef37() {
		return def37;
	}

	public void setDef37(java.lang.String def37) {
		this.def37 = def37;
	}

	public java.lang.String getDef38() {
		return def38;
	}

	public void setDef38(java.lang.String def38) {
		this.def38 = def38;
	}

	public java.lang.String getDef39() {
		return def39;
	}

	public void setDef39(java.lang.String def39) {
		this.def39 = def39;
	}

	public java.lang.String getDef40() {
		return def40;
	}

	public void setDef40(java.lang.String def40) {
		this.def40 = def40;
	}
	
	
}
