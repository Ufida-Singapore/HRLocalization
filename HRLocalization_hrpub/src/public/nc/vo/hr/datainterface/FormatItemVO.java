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
public class FormatItemVO extends SuperVO
{
    private java.lang.String ifid;
    private java.lang.String pk_dataintface_b;
    private java.lang.Integer iseq;
    private java.lang.Integer isourcetype = 1;
    private java.lang.String vcontent;
    private java.lang.String vfieldname;
    private java.lang.Integer ifieldtype;
    private java.lang.Integer ifldwidth;
    private java.lang.Integer iflddecimal;
    private java.lang.Integer vseparator;
    private java.lang.Integer icaretpos = 0;
    private java.lang.String vcaret;
    private java.lang.String vincludebefore;
    private java.lang.String vincludeafter;
    private java.lang.Integer iiforder;
    private java.lang.String vformulastr;
    private java.lang.Integer dr = 0;
    private nc.vo.pub.lang.UFDateTime ts;
    
    // HR���ػ������б��̸Ķ�����������ֶ�
    private java.lang.Integer inextline = 0;
    
    public static final String IFID = "ifid";
    public static final String PK_DATAINTFACE_B = "pk_dataintface_b";
    public static final String ISEQ = "iseq";
    public static final String ISOURCETYPE = "isourcetype";
    public static final String VCONTENT = "vcontent";
    public static final String VFIELDNAME = "vfieldname";
    public static final String IFIELDTYPE = "ifieldtype";
    public static final String IFLDWIDTH = "ifldwidth";
    public static final String IFLDDECIMAL = "iflddecimal";
    public static final String VSEPARATOR = "vseparator";
    public static final String ICARETPOS = "icaretpos";
    public static final String VCARET = "vcaret";
    public static final String VINCLUDEBEFORE = "vincludebefore";
    public static final String VINCLUDEAFTER = "vincludeafter";
    public static final String IIFORDER = "iiforder";
    public static final String VFORMULASTR = "vformulastr";
    
    // HR���ػ������б��̸Ķ�����������ֶ�
    public static final String INEXTLINE = "inextline";
    
    /**
     * <p>
     * ���ر�����.
     * <p>
     * ��������:
     * @return java.lang.String
     */
    public static java.lang.String getDefaultTableName()
    {
        return "hr_dataintface_b";
    }
    
    /**
     * ����Ĭ�Ϸ�ʽ����������.
     * ��������:
     */
    public FormatItemVO()
    {
        super();
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
     * ����icaretpos��Getter����.����������λ��λ��
     * ��������:
     * @return java.lang.Integer
     */
    public java.lang.Integer getIcaretpos()
    {
        return icaretpos;
    }
    
    /**
     * ����ifid��Getter����.��������parentPK
     * ��������:
     * @return java.lang.String
     */
    public java.lang.String getIfid()
    {
        return ifid;
    }
    
    /**
     * ����ifieldtype��Getter����.���������ֶ�����
     * ��������:
     * @return java.lang.Integer
     */
    public java.lang.Integer getIfieldtype()
    {
        return ifieldtype;
    }
    
    /**
     * ����iflddecimal��Getter����.��������С��λ��
     * ��������:
     * @return java.lang.Integer
     */
    public java.lang.Integer getIflddecimal()
    {
        return iflddecimal;
    }
    
    /**
     * ����ifldwidth��Getter����.����������Ŀ���
     * ��������:
     * @return java.lang.Integer
     */
    public java.lang.Integer getIfldwidth()
    {
        return ifldwidth;
    }
    
    /**
     * ����iiforder��Getter����.������������
     * ��������:
     * @return java.lang.Integer
     */
    public java.lang.Integer getIiforder()
    {
        return iiforder;
    }
    
    /**
     * ����iseq��Getter����.�����������
     * ��������:
     * @return java.lang.Integer
     */
    public java.lang.Integer getIseq()
    {
        return iseq;
    }
    
    /**
     * ����isourcetype��Getter����.��������������Դ
     * ��������:
     * @return java.lang.Integer
     */
    public java.lang.Integer getIsourcetype()
    {
        return isourcetype;
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
        return "ifid";
    }
    
    /**
     * ����pk_dataintface_b��Getter����.���������ӿ������ӱ�����
     * ��������:
     * @return java.lang.String
     */
    public java.lang.String getPk_dataintface_b()
    {
        return pk_dataintface_b;
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
        return "pk_dataintface_b";
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
        return "hr_dataintface_b";
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
     * ����vcaret��Getter����.����������λ������
     * ��������:
     * @return java.lang.String
     */
    public java.lang.String getVcaret()
    {
        return vcaret;
    }
    
    /**
     * ����vcontent��Getter����.����������Ŀ
     * ��������:
     * @return java.lang.String
     */
    public java.lang.String getVcontent()
    {
        return vcontent;
    }
    
    /**
     * ����vfieldname��Getter����.���������ֶ�����
     * ��������:
     * @return java.lang.String
     */
    public java.lang.String getVfieldname()
    {
        return vfieldname;
    }
    
    /**
     * ����vformulastr��Getter����.����������ʽ��ʾ
     * ��������:
     * @return java.lang.String
     */
    public java.lang.String getVformulastr()
    {
        return vformulastr;
    }
    
    /**
     * ����vincludeafter��Getter����.���������������
     * ��������:
     * @return java.lang.String
     */
    public java.lang.String getVincludeafter()
    {
        return vincludeafter;
    }
    
    /**
     * ����vincludebefore��Getter����.��������ǰ������
     * ��������:
     * @return java.lang.String
     */
    public java.lang.String getVincludebefore()
    {
        return vincludebefore;
    }
    
    /**
     * ����vseparator��Getter����.����������Ŀ�ָ���
     * ��������:
     * @return java.lang.Integer
     */
    public java.lang.Integer getVseparator()
    {
        return vseparator;
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
     * ����icaretpos��Setter����.����������λ��λ��
     * ��������:
     * @param newIcaretpos java.lang.Integer
     */
    public void setIcaretpos(java.lang.Integer newIcaretpos)
    {
        this.icaretpos = newIcaretpos;
    }
    
    /**
     * ����ifid��Setter����.��������parentPK
     * ��������:
     * @param newIfid java.lang.String
     */
    public void setIfid(java.lang.String newIfid)
    {
        this.ifid = newIfid;
    }
    
    /**
     * ����ifieldtype��Setter����.���������ֶ�����
     * ��������:
     * @param newIfieldtype java.lang.Integer
     */
    public void setIfieldtype(java.lang.Integer newIfieldtype)
    {
        this.ifieldtype = newIfieldtype;
    }
    
    /**
     * ����iflddecimal��Setter����.��������С��λ��
     * ��������:
     * @param newIflddecimal java.lang.Integer
     */
    public void setIflddecimal(java.lang.Integer newIflddecimal)
    {
        this.iflddecimal = newIflddecimal;
    }
    
    /**
     * ����ifldwidth��Setter����.����������Ŀ���
     * ��������:
     * @param newIfldwidth java.lang.Integer
     */
    public void setIfldwidth(java.lang.Integer newIfldwidth)
    {
        this.ifldwidth = newIfldwidth;
    }
    
    /**
     * ����iiforder��Setter����.������������
     * ��������:
     * @param newIiforder java.lang.Integer
     */
    public void setIiforder(java.lang.Integer newIiforder)
    {
        this.iiforder = newIiforder;
    }
    
    /**
     * ����iseq��Setter����.�����������
     * ��������:
     * @param newIseq java.lang.Integer
     */
    public void setIseq(java.lang.Integer newIseq)
    {
        this.iseq = newIseq;
    }
    
    /**
     * ����isourcetype��Setter����.��������������Դ
     * ��������:
     * @param newIsourcetype java.lang.Integer
     */
    public void setIsourcetype(java.lang.Integer newIsourcetype)
    {
        this.isourcetype = newIsourcetype;
    }
    
    /**
     * ����pk_dataintface_b��Setter����.���������ӿ������ӱ�����
     * ��������:
     * @param newPk_dataintface_b java.lang.String
     */
    public void setPk_dataintface_b(java.lang.String newPk_dataintface_b)
    {
        this.pk_dataintface_b = newPk_dataintface_b;
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
     * ����vcaret��Setter����.����������λ������
     * ��������:
     * @param newVcaret java.lang.String
     */
    public void setVcaret(java.lang.String newVcaret)
    {
        this.vcaret = newVcaret;
    }
    
    /**
     * ����vcontent��Setter����.����������Ŀ
     * ��������:
     * @param newVcontent java.lang.String
     */
    public void setVcontent(java.lang.String newVcontent)
    {
        this.vcontent = newVcontent;
    }
    
    /**
     * ����vfieldname��Setter����.���������ֶ�����
     * ��������:
     * @param newVfieldname java.lang.String
     */
    public void setVfieldname(java.lang.String newVfieldname)
    {
        this.vfieldname = newVfieldname;
    }
    
    /**
     * ����vformulastr��Setter����.����������ʽ��ʾ
     * ��������:
     * @param newVformulastr java.lang.String
     */
    public void setVformulastr(java.lang.String newVformulastr)
    {
        this.vformulastr = newVformulastr;
    }
    
    /**
     * ����vincludeafter��Setter����.���������������
     * ��������:
     * @param newVincludeafter java.lang.String
     */
    public void setVincludeafter(java.lang.String newVincludeafter)
    {
        this.vincludeafter = newVincludeafter;
    }
    
    /**
     * ����vincludebefore��Setter����.��������ǰ������
     * ��������:
     * @param newVincludebefore java.lang.String
     */
    public void setVincludebefore(java.lang.String newVincludebefore)
    {
        this.vincludebefore = newVincludebefore;
    }
    
    /**
     * ����vseparator��Setter����.����������Ŀ�ָ���
     * ��������:
     * @param newVseparator java.lang.Integer
     */
    public void setVseparator(java.lang.Integer newVseparator)
    {
        this.vseparator = newVseparator;
    }

    // HR���ػ����������ֶε�getter��setter start
	public java.lang.Integer getInextline() {
		return inextline;
	}

	public void setInextline(java.lang.Integer inextline) {
		this.inextline = inextline;
	}
	// HR���ػ����������ֶε�getter��setter end
    
}
