package nc.impl.wa.paydata.precacu;

import java.io.Serializable;

/**
 * 
 * @author weiningc
 * @version 1.0
 * @data:2018.11.16 14:33
 */
public class MalaysiaTaxFormulaVO  implements Serializable{
	private static final long serialVersionUID = 1L;
	
	//PCB_GROUPA:Non-Resident (δ��182��)
	public final static String PCB_GROUPA = "PCB_GROUPA";
	
	//PCB_GROUPB:Resident
	public final static String PCB_GROUPB = "PCB_GROUPB";
	
	//PCB_GROUPC:Returning Expert Programe(REP)
	public final static String PCB_GROUPC= "PCB_GROUPC";
	
	//PCB_GROUPD:Knowledge worker(IRDA)
	public final static String PCB_GROUPD= "PCB_GROUPD";
	
	// Class_type: ȡֵ��Χ��0, 1�� 0Ϊ��ͨ��� 1Ϊ��Ƚ������    2 Ϊн�ʲ�������  
	public final static String CLASS_TYPE_NORMAL = "0";
	public final static String CLASS_TYPE_YEAR = "1";
	public final static String CLASS_TYPE_REDATA= "2";

	//PCB Group
	private String pcbgroup;
	
	//н�ʷ������
	private String class_wagetype;
	
	//��Ӧ������
	private String month_grosspay;
	
	//��ʵ������
	private String month_netpay;
	
	//����ȱ�����ǰ���ۼ�ë����
	private String beforcurrent_totalwage;
	
	//����ȱ�����ǰ���ۼƿۿ�
	private String beforcurrent_totaldeduction;
	
	//�ڽ�˰
	private String zaket;

	public String getPcbgroup() {
		return pcbgroup;
	}

	public void setPcbgroup(String pcbgroup) {
		this.pcbgroup = pcbgroup;
	}

	public String getMonth_grosspay() {
		return month_grosspay;
	}

	public void setMonth_grosspay(String month_grosspay) {
		this.month_grosspay = month_grosspay;
	}

	public String getMonth_netpay() {
		return month_netpay;
	}

	public void setMonth_netpay(String month_netpay) {
		this.month_netpay = month_netpay;
	}

	public String getBeforcurrent_totalwage() {
		return beforcurrent_totalwage;
	}

	public void setBeforcurrent_totalwage(String beforcurrent_totalwage) {
		this.beforcurrent_totalwage = beforcurrent_totalwage;
	}

	public String getBeforcurrent_totaldeduction() {
		return beforcurrent_totaldeduction;
	}

	public void setBeforcurrent_totaldeduction(String beforcurrent_totaldeduction) {
		this.beforcurrent_totaldeduction = beforcurrent_totaldeduction;
	}

	public String getZaket() {
		return zaket;
	}

	public void setZaket(String zaket) {
		this.zaket = zaket;
	}

	public String getClass_wagetype() {
		return class_wagetype;
	}

	public void setClass_wagetype(String class_wagetype) {
		this.class_wagetype = class_wagetype;
	}
	
	

}
