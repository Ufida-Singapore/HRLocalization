/***************************************************************
 * \
 * \
 ***************************************************************/
package nc.vo.hr.datainterface;

import nc.vo.pub.SuperVO;

/**
 * <b> �ڴ˴���Ҫ��������Ĺ��� </b>
 * <p>
 * �ڴ˴���Ӵ����������Ϣ
 * </p>
 * ��������:
 * @author
 * @version NCPrj ??
 */
@SuppressWarnings("serial")
public class HrIntfaceVO extends SuperVO
{
    private java.lang.String pk_dataio_intface;
    private java.lang.String pk_bankdoc;
    private java.lang.String vifname;
    private java.lang.String vifname2;
    private java.lang.String vifname3;
    private java.lang.String vifname4;
    private java.lang.String vifname5;
    private java.lang.String vifname6;
    private java.lang.Integer iiftype;
    private nc.vo.pub.lang.UFDouble vfilename;
    private java.lang.Integer ifiletype;
    private java.lang.Integer iifdot;
    private java.lang.Integer iifkilobit;
    private java.lang.Integer iifseparator;
    private java.lang.Integer iseparator;
    private java.lang.Integer iiftop;
    private java.lang.Integer toplineposition;
    private java.lang.Integer toplinenum;
    
    // Add an extra flag line start
    private java.lang.Integer iiftop2;
    private java.lang.Integer toplineposition2;
    private java.lang.Integer toplinenum2;
    // Add an extra flag line end
    
    private java.lang.String vmemo;
    private java.lang.String operatorid;
    private java.lang.String vtable;
    private java.lang.String vcol;
    private java.lang.Integer iifcaret;
    private java.lang.String classid;
    private java.lang.String cyear;
    private java.lang.String cperiod;
    private java.lang.Integer idefault;
    private java.lang.String pk_group;
    private java.lang.String pk_org;
    private java.lang.Integer iouthead;
    private java.lang.Integer iheadadjustbody;
    private nc.vo.pub.lang.UFDate date1;
    private nc.vo.pub.lang.UFDate date2;
    private java.lang.Integer dr = 0;
    private nc.vo.pub.lang.UFDateTime ts;
    
    public static final String PK_DATAIO_INTFACE = "pk_dataio_intface";
    public static final String PK_BANKDOC = "pk_bankdoc";
    public static final String VIFNAME = "vifname";
    public static final String VIFNAME2 = "vifname2";
    public static final String VIFNAME3 = "vifname3";
    public static final String VIFNAME4 = "vifname4";
    public static final String VIFNAME5 = "vifname5";
    public static final String VIFNAME6 = "vifname6";
    public static final String IIFTYPE = "iiftype";
    public static final String VFILENAME = "vfilename";
    public static final String IFILETYPE = "ifiletype";
    public static final String IIFDOT = "iifdot";
    public static final String IIFKILOBIT = "iifkilobit";
    public static final String IIFSEPARATOR = "iifseparator";
    public static final String ISEPARATOR = "iseparator";
    public static final String IIFTOP = "iiftop";
    public static final String TOPLINEPOSITION = "toplineposition";
    public static final String TOPLINENUM = "toplinenum";
    public static final String VMEMO = "vmemo";
    public static final String OPERATORID = "operatorid";
    public static final String VTABLE = "vtable";
    public static final String VCOL = "vcol";
    public static final String IIFCARET = "iifcaret";
    public static final String CLASSID = "classid";
    public static final String CYEAR = "cyear";
    public static final String CPERIOD = "cperiod";
    public static final String IDEFAULT = "idefault";
    public static final String PK_GROUP = "pk_group";
    public static final String PK_ORG = "pk_org";
    public static final String IOUTHEAD = "iouthead";
    public static final String IHEADADJUSTBODY = "iheadadjustbody";
    public static final String DATE1 = "date1";
    public static final String DATE2 = "date2";
    
    /**
     * <p>
     * ���ر�����.
     * <p>
     * ��������:
     * @return java.lang.String
     */
    public static java.lang.String getDefaultTableName()
    {
        return "hr_dataio_intface";
    }
    
    /**
     * ����Ĭ�Ϸ�ʽ����������.
     * ��������:
     */
    public HrIntfaceVO()
    {
        super();
    }
    
    /**
     * ����classid��Getter����.��������н�ʷ���
     * ��������:
     * @return java.lang.String
     */
    public java.lang.String getClassid()
    {
        return classid;
    }
    
    /**
     * ����cperiod��Getter����.��������н���¶�
     * ��������:
     * @return java.lang.String
     */
    public java.lang.String getCperiod()
    {
        return cperiod;
    }
    
    /**
     * ����cyear��Getter����.��������н�����
     * ��������:
     * @return java.lang.String
     */
    public java.lang.String getCyear()
    {
        return cyear;
    }
    
    /**
     * ����date1��Getter����.����������չ3
     * ��������:
     * @return nc.vo.pub.lang.UFDate
     */
    public nc.vo.pub.lang.UFDate getDate1()
    {
        return date1;
    }
    
    /**
     * ����date2��Getter����.����������չ4
     * ��������:
     * @return nc.vo.pub.lang.UFDate
     */
    public nc.vo.pub.lang.UFDate getDate2()
    {
        return date2;
    }
    
    /**
     * ����dr��Getter����.��������dr
     * ��������:
     * @return java.lang.Integer
     */
    public java.lang.Integer getDr()
    {
        return dr;
    }
    
    /**
     * ����idefault��Getter����.��������Ĭ�Ͻӿ�
     * ��������:
     * @return java.lang.Integer
     */
    public java.lang.Integer getIdefault()
    {
        return idefault;
    }
    
    /**
     * ����ifiletype��Getter����.���������ⲿ�ļ�����
     * ��������:
     * @return java.lang.Integer
     */
    public java.lang.Integer getIfiletype()
    {
        return ifiletype;
    }
    
    /**
     * ����iheadadjustbody��Getter����.����������ͷ����һ��
     * ��������:
     * @return java.lang.Integer
     */
    public java.lang.Integer getIheadadjustbody()
    {
        return iheadadjustbody;
    }
    
    /**
     * ����iifcaret��Getter����.����������Ҫ��λ��
     * ��������:
     * @return java.lang.Integer
     */
    public java.lang.Integer getIifcaret()
    {
        return iifcaret;
    }
    
    /**
     * ����iifdot��Getter����.����������ҪС����
     * ��������:
     * @return java.lang.Integer
     */
    public java.lang.Integer getIifdot()
    {
        return iifdot;
    }
    
    /**
     * ����iifkilobit��Getter����.����������Ҫǧλ�ָ���
     * ��������:
     * @return java.lang.Integer
     */
    public java.lang.Integer getIifkilobit()
    {
        return iifkilobit;
    }
    
    /**
     * ����iifseparator��Getter����.����������Ҫ��Ŀ�ָ���
     * ��������:
     * @return java.lang.Integer
     */
    public java.lang.Integer getIifseparator()
    {
        return iifseparator;
    }
    
    /**
     * ����iiftop��Getter����.���������б�־��
     * ��������:
     * @return java.lang.Integer
     */
    public java.lang.Integer getIiftop()
    {
        return iiftop;
    }
    
    /**
     * ����iiftype��Getter����.���������ӿ�����
     * ��������:
     * @return java.lang.Integer
     */
    public java.lang.Integer getIiftype()
    {
        return iiftype;
    }
    
    /**
     * ����iouthead��Getter����.�������������ͷ
     * ��������:
     * @return java.lang.Integer
     */
    public java.lang.Integer getIouthead()
    {
        return iouthead;
    }
    
    /**
     * ����iseparator��Getter����.����������Ŀ�ָ���
     * ��������:
     * @return java.lang.Integer
     */
    public java.lang.Integer getIseparator()
    {
        return iseparator;
    }
    
    /**
     * ����operatorid��Getter����.������������Ա
     * ��������:
     * @return java.lang.String
     */
    public java.lang.String getOperatorid()
    {
        return operatorid;
    }
    
    /**
     * <p>
     * ȡ�ø�VO�����ֶ�.
     * <p>
     * ��������:
     * @return java.lang.String
     */
    @Override
    public java.lang.String getParentPKFieldName()
    {
        return null;
    }
    
    /**
     * ����pk_bankdoc��Getter����.����������������
     * ��������:
     * @return java.lang.String
     */
    public java.lang.String getPk_bankdoc()
    {
        return pk_bankdoc;
    }
    
    /**
     * ����pk_dataio_intface��Getter����.���������ӿ����ñ�����
     * ��������:
     * @return java.lang.String
     */
    public java.lang.String getPk_dataio_intface()
    {
        return pk_dataio_intface;
    }
    
    /**
     * ����pk_group��Getter����.����������������
     * ��������:
     * @return java.lang.String
     */
    public java.lang.String getPk_group()
    {
        return pk_group;
    }
    
    /**
     * ����pk_org��Getter����.��������������֯
     * ��������:
     * @return java.lang.String
     */
    public java.lang.String getPk_org()
    {
        return pk_org;
    }
    
    /**
     * <p>
     * ȡ�ñ�����.
     * <p>
     * ��������:
     * @return java.lang.String
     */
    @Override
    public java.lang.String getPKFieldName()
    {
        return "pk_dataio_intface";
    }
    
    /**
     * <p>
     * ���ر�����.
     * <p>
     * ��������:
     * @return java.lang.String
     */
    @Override
    public java.lang.String getTableName()
    {
        return "hr_dataio_intface";
    }
    
    /**
     * ����toplinenum��Getter����.����������־�����
     * ��������:
     * @return java.lang.Integer
     */
    public java.lang.Integer getToplinenum()
    {
        return toplinenum;
    }
    
    /**
     * ����toplineposition��Getter����.����������־��λ��
     * ��������:
     * @return java.lang.Integer
     */
    public java.lang.Integer getToplineposition()
    {
        return toplineposition;
    }
    
    /**
     * ����ts��Getter����.��������ts
     * ��������:
     * @return nc.vo.pub.lang.UFDateTime
     */
    public nc.vo.pub.lang.UFDateTime getTs()
    {
        return ts;
    }
    
    /**
     * ����vcol��Getter����.��������������Ŀ
     * ��������:
     * @return java.lang.String
     */
    public java.lang.String getVcol()
    {
        return vcol;
    }
    
    /**
     * ����vfilename��Getter����.���������ⲿ�ļ�����
     * ��������:
     * @return nc.vo.pub.lang.UFDouble
     */
    public nc.vo.pub.lang.UFDouble getVfilename()
    {
        return vfilename;
    }
    
    /**
     * ����vifname��Getter����.��������$map.displayName
     * ��������:
     * @return java.lang.String
     */
    public java.lang.String getVifname()
    {
        return vifname;
    }
    
    /**
     * ����vifname2��Getter����.��������$map.displayName
     * ��������:
     * @return java.lang.String
     */
    public java.lang.String getVifname2()
    {
        return vifname2;
    }
    
    /**
     * ����vifname3��Getter����.��������$map.displayName
     * ��������:
     * @return java.lang.String
     */
    public java.lang.String getVifname3()
    {
        return vifname3;
    }
    
    /**
     * ����vifname4��Getter����.��������$map.displayName
     * ��������:
     * @return java.lang.String
     */
    public java.lang.String getVifname4()
    {
        return vifname4;
    }
    
    /**
     * ����vifname5��Getter����.��������$map.displayName
     * ��������:
     * @return java.lang.String
     */
    public java.lang.String getVifname5()
    {
        return vifname5;
    }
    
    /**
     * ����vifname6��Getter����.��������$map.displayName
     * ��������:
     * @return java.lang.String
     */
    public java.lang.String getVifname6()
    {
        return vifname6;
    }
    
    /**
     * ����vmemo��Getter����.����������ע
     * ��������:
     * @return java.lang.String
     */
    public java.lang.String getVmemo()
    {
        return vmemo;
    }
    
    /**
     * ����vtable��Getter����.��������������
     * ��������:
     * @return java.lang.String
     */
    public java.lang.String getVtable()
    {
        return vtable;
    }
    
    /**
     * ����classid��Setter����.��������н�ʷ���
     * ��������:
     * @param newClassid java.lang.String
     */
    public void setClassid(java.lang.String newClassid)
    {
        this.classid = newClassid;
    }
    
    /**
     * ����cperiod��Setter����.��������н���¶�
     * ��������:
     * @param newCperiod java.lang.String
     */
    public void setCperiod(java.lang.String newCperiod)
    {
        this.cperiod = newCperiod;
    }
    
    /**
     * ����cyear��Setter����.��������н�����
     * ��������:
     * @param newCyear java.lang.String
     */
    public void setCyear(java.lang.String newCyear)
    {
        this.cyear = newCyear;
    }
    
    /**
     * ����date1��Setter����.����������չ3
     * ��������:
     * @param newDate1 nc.vo.pub.lang.UFDate
     */
    public void setDate1(nc.vo.pub.lang.UFDate newDate1)
    {
        this.date1 = newDate1;
    }
    
    /**
     * ����date2��Setter����.����������չ4
     * ��������:
     * @param newDate2 nc.vo.pub.lang.UFDate
     */
    public void setDate2(nc.vo.pub.lang.UFDate newDate2)
    {
        this.date2 = newDate2;
    }
    
    /**
     * ����dr��Setter����.��������dr
     * ��������:
     * @param newDr java.lang.Integer
     */
    public void setDr(java.lang.Integer newDr)
    {
        this.dr = newDr;
    }
    
    /**
     * ����idefault��Setter����.��������Ĭ�Ͻӿ�
     * ��������:
     * @param newIdefault java.lang.Integer
     */
    public void setIdefault(java.lang.Integer newIdefault)
    {
        this.idefault = newIdefault;
    }
    
    /**
     * ����ifiletype��Setter����.���������ⲿ�ļ�����
     * ��������:
     * @param newIfiletype java.lang.Integer
     */
    public void setIfiletype(java.lang.Integer newIfiletype)
    {
        this.ifiletype = newIfiletype;
    }
    
    /**
     * ����iheadadjustbody��Setter����.����������ͷ����һ��
     * ��������:
     * @param newIheadadjustbody java.lang.Integer
     */
    public void setIheadadjustbody(java.lang.Integer newIheadadjustbody)
    {
        this.iheadadjustbody = newIheadadjustbody;
    }
    
    /**
     * ����iifcaret��Setter����.����������Ҫ��λ��
     * ��������:
     * @param newIifcaret java.lang.Integer
     */
    public void setIifcaret(java.lang.Integer newIifcaret)
    {
        this.iifcaret = newIifcaret;
    }
    
    /**
     * ����iifdot��Setter����.����������ҪС����
     * ��������:
     * @param newIifdot java.lang.Integer
     */
    public void setIifdot(java.lang.Integer newIifdot)
    {
        this.iifdot = newIifdot;
    }
    
    /**
     * ����iifkilobit��Setter����.����������Ҫǧλ�ָ���
     * ��������:
     * @param newIifkilobit java.lang.Integer
     */
    public void setIifkilobit(java.lang.Integer newIifkilobit)
    {
        this.iifkilobit = newIifkilobit;
    }
    
    /**
     * ����iifseparator��Setter����.����������Ҫ��Ŀ�ָ���
     * ��������:
     * @param newIifseparator java.lang.Integer
     */
    public void setIifseparator(java.lang.Integer newIifseparator)
    {
        this.iifseparator = newIifseparator;
    }
    
    /**
     * ����iiftop��Setter����.���������б�־��
     * ��������:
     * @param newIiftop java.lang.Integer
     */
    public void setIiftop(java.lang.Integer newIiftop)
    {
        this.iiftop = newIiftop;
    }
    
    /**
     * ����iiftype��Setter����.���������ӿ�����
     * ��������:
     * @param newIiftype java.lang.Integer
     */
    public void setIiftype(java.lang.Integer newIiftype)
    {
        this.iiftype = newIiftype;
    }
    
    /**
     * ����iouthead��Setter����.�������������ͷ
     * ��������:
     * @param newIouthead java.lang.Integer
     */
    public void setIouthead(java.lang.Integer newIouthead)
    {
        this.iouthead = newIouthead;
    }
    
    /**
     * ����iseparator��Setter����.����������Ŀ�ָ���
     * ��������:
     * @param newIseparator java.lang.Integer
     */
    public void setIseparator(java.lang.Integer newIseparator)
    {
        this.iseparator = newIseparator;
    }
    
    /**
     * ����operatorid��Setter����.������������Ա
     * ��������:
     * @param newOperatorid java.lang.String
     */
    public void setOperatorid(java.lang.String newOperatorid)
    {
        this.operatorid = newOperatorid;
    }
    
    /**
     * ����pk_bankdoc��Setter����.����������������
     * ��������:
     * @param newPk_bankdoc java.lang.String
     */
    public void setPk_bankdoc(java.lang.String newPk_bankdoc)
    {
        this.pk_bankdoc = newPk_bankdoc;
    }
    
    /**
     * ����pk_dataio_intface��Setter����.���������ӿ����ñ�����
     * ��������:
     * @param newPk_dataio_intface java.lang.String
     */
    public void setPk_dataio_intface(java.lang.String newPk_dataio_intface)
    {
        this.pk_dataio_intface = newPk_dataio_intface;
    }
    
    /**
     * ����pk_group��Setter����.����������������
     * ��������:
     * @param newPk_group java.lang.String
     */
    public void setPk_group(java.lang.String newPk_group)
    {
        this.pk_group = newPk_group;
    }
    
    /**
     * ����pk_org��Setter����.��������������֯
     * ��������:
     * @param newPk_org java.lang.String
     */
    public void setPk_org(java.lang.String newPk_org)
    {
        this.pk_org = newPk_org;
    }
    
    /**
     * ����toplinenum��Setter����.����������־�����
     * ��������:
     * @param newToplinenum java.lang.Integer
     */
    public void setToplinenum(java.lang.Integer newToplinenum)
    {
        this.toplinenum = newToplinenum;
    }
    
    /**
     * ����toplineposition��Setter����.����������־��λ��
     * ��������:
     * @param newToplineposition java.lang.Integer
     */
    public void setToplineposition(java.lang.Integer newToplineposition)
    {
        this.toplineposition = newToplineposition;
    }
    
    /**
     * ����ts��Setter����.��������ts
     * ��������:
     * @param newTs nc.vo.pub.lang.UFDateTime
     */
    public void setTs(nc.vo.pub.lang.UFDateTime newTs)
    {
        this.ts = newTs;
    }
    
    /**
     * ����vcol��Setter����.��������������Ŀ
     * ��������:
     * @param newVcol java.lang.String
     */
    public void setVcol(java.lang.String newVcol)
    {
        this.vcol = newVcol;
    }
    
    /**
     * ����vfilename��Setter����.���������ⲿ�ļ�����
     * ��������:
     * @param newVfilename nc.vo.pub.lang.UFDouble
     */
    public void setVfilename(nc.vo.pub.lang.UFDouble newVfilename)
    {
        this.vfilename = newVfilename;
    }
    
    /**
     * ����vifname��Setter����.��������$map.displayName
     * ��������:
     * @param newVifname java.lang.String
     */
    public void setVifname(java.lang.String newVifname)
    {
        this.vifname = newVifname;
    }
    
    /**
     * ����vifname2��Setter����.��������$map.displayName
     * ��������:
     * @param newVifname2 java.lang.String
     */
    public void setVifname2(java.lang.String newVifname2)
    {
        this.vifname2 = newVifname2;
    }
    
    /**
     * ����vifname3��Setter����.��������$map.displayName
     * ��������:
     * @param newVifname3 java.lang.String
     */
    public void setVifname3(java.lang.String newVifname3)
    {
        this.vifname3 = newVifname3;
    }
    
    /**
     * ����vifname4��Setter����.��������$map.displayName
     * ��������:
     * @param newVifname4 java.lang.String
     */
    public void setVifname4(java.lang.String newVifname4)
    {
        this.vifname4 = newVifname4;
    }
    
    /**
     * ����vifname5��Setter����.��������$map.displayName
     * ��������:
     * @param newVifname5 java.lang.String
     */
    public void setVifname5(java.lang.String newVifname5)
    {
        this.vifname5 = newVifname5;
    }
    
    /**
     * ����vifname6��Setter����.��������$map.displayName
     * ��������:
     * @param newVifname6 java.lang.String
     */
    public void setVifname6(java.lang.String newVifname6)
    {
        this.vifname6 = newVifname6;
    }
    
    /**
     * ����vmemo��Setter����.����������ע
     * ��������:
     * @param newVmemo java.lang.String
     */
    public void setVmemo(java.lang.String newVmemo)
    {
        this.vmemo = newVmemo;
    }
    
    /**
     * ����vtable��Setter����.��������������
     * ��������:
     * @param newVtable java.lang.String
     */
    public void setVtable(java.lang.String newVtable)
    {
        this.vtable = newVtable;
    }


    
    // Add getter setter for Bank Upload start
	public java.lang.Integer getIiftop2() {
		return iiftop2;
	}

	public void setIiftop2(java.lang.Integer iiftop2) {
		this.iiftop2 = iiftop2;
	}

	public java.lang.Integer getToplineposition2() {
		return toplineposition2;
	}

	public void setToplineposition2(java.lang.Integer toplineposition2) {
		this.toplineposition2 = toplineposition2;
	}

	public java.lang.Integer getToplinenum2() {
		return toplinenum2;
	}

	public void setToplinenum2(java.lang.Integer toplinenum2) {
		this.toplinenum2 = toplinenum2;
	}
	// Add getter setter for Bank Upload end
}
