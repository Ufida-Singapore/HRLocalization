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
public class IfsettopVO extends SuperVO
{
    private java.lang.String ifid;
    private java.lang.String pk_hr_ifsettop;
    private java.lang.Integer iseq;
    private java.lang.String vcontent;
    private java.lang.String vfieldname;
    private java.lang.Integer ifldwidth;
    private java.lang.Integer iflddecimal;
    private java.lang.Integer vseparator;
    private java.lang.Integer icaretpos;
    private java.lang.String vcaret;
    private java.lang.String dateformat;
    private java.lang.Integer iifsum;
    private java.lang.Integer dr = 0;
    private nc.vo.pub.lang.UFDateTime ts;
    // HR���ػ�����������к��Ƿ�Ϊ��/β���ֶ�
    private java.lang.Integer inextline;
    private java.lang.Integer itoplineposition;
    
    public static final String IFID = "ifid";
    public static final String PK_HR_IFSETTOP = "pk_hr_ifsettop";
    public static final String ISEQ = "iseq";
    public static final String VCONTENT = "vcontent";
    public static final String VFIELDNAME = "vfieldname";
    public static final String IFLDWIDTH = "ifldwidth";
    public static final String IFLDDECIMAL = "iflddecimal";
    public static final String VSEPARATOR = "vseparator";
    public static final String ICARETPOS = "icaretpos";
    public static final String VCARET = "vcaret";
    public static final String DATEFORMAT = "dateformat";
    public static final String IIFSUM = "iifsum";
    // HR���ػ�����������к��Ƿ�Ϊ��/β���ֶ�
    public static final String INEXTLINE = "inextline";
    public static final String ITOPLINEPOSITION = "itoplineposition";
    
    /**
     * <p>
     * ���ر�����.
     * <p>
     * ��������:
     * @return java.lang.String
     */
    public static java.lang.String getDefaultTableName()
    {
        return "hr_ifsettop";
    }
    
    /**
     * ����Ĭ�Ϸ�ʽ����������.
     * ��������:
     */
    public IfsettopVO()
    {
        super();
    }
    
    /**
     * ����dateformat��Getter����.�����������ڸ�ʽ
     * ��������:
     * @return java.lang.String
     */
    public java.lang.String getDateformat()
    {
        return dateformat;
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
     * ����iifsum��Getter����.���������ϼ���
     * ��������:
     * @return java.lang.Integer
     */
    public java.lang.Integer getIifsum()
    {
        return iifsum;
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
     * ����pk_wa_ifsettop��Getter����.����������ĩ�����ñ�����
     * ��������:
     * @return java.lang.String
     */
    public java.lang.String getPk_hr_ifsettop()
    {
        return pk_hr_ifsettop;
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
        return "pk_hr_ifsettop";
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
        return "hr_ifsettop";
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
     * ����vseparator��Getter����.����������Ŀ�ָ��
     * ��������:
     * @return java.lang.Integer
     */
    public java.lang.Integer getVseparator()
    {
        return vseparator;
    }
    
    /**
     * ����dateformat��Setter����.�����������ڸ�ʽ
     * ��������:
     * @param newDateformat java.lang.String
     */
    public void setDateformat(java.lang.String newDateformat)
    {
        this.dateformat = newDateformat;
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
     * ����iifsum��Setter����.���������ϼ���
     * ��������:
     * @param newIifsum java.lang.Integer
     */
    public void setIifsum(java.lang.Integer newIifsum)
    {
        this.iifsum = newIifsum;
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
     * ����pk_wa_ifsettop��Setter����.����������ĩ�����ñ�����
     * ��������:
     * @param newPk_wa_ifsettop java.lang.String
     */
    public void setPk_hr_ifsettop(java.lang.String newPk_hr_ifsettop)
    {
        this.pk_hr_ifsettop = newPk_hr_ifsettop;
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
     * ����vseparator��Setter����.����������Ŀ�ָ��
     * ��������:
     * @param newVseparator java.lang.Integer
     */
    public void setVseparator(java.lang.Integer newVseparator)
    {
        this.vseparator = newVseparator;
    }

    // HR���ػ��������к��Ƿ�Ϊ��/β���ֶε�getter��setter start
	public java.lang.Integer getInextline() {
		return inextline;
	}

	public void setInextline(java.lang.Integer inextline) {
		this.inextline = inextline;
	}

	public java.lang.Integer getItoplineposition() {
		return itoplineposition;
	}

	public void setItoplineposition(java.lang.Integer itoplineposition) {
		this.itoplineposition = itoplineposition;
	}
    // HR���ػ��������к��Ƿ�Ϊ��/β���ֶε�getter��setter end
    
    
    
}
