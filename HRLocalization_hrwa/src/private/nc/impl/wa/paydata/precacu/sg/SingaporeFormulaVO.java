package nc.impl.wa.paydata.precacu.sg;

public class SingaporeFormulaVO {

	//˽�˹�˾
	public static final Integer PRIVATEORGMODE = 0;
	//�����ȹ�����λ
	public static final Integer PUBLICORGMODE = 1;
	//��Ա����
	public static final Integer EMPLOYEEPAY = 0;
	//�ܽ���
	public static final Integer TOTALPAY = 1;
	//2--OW Employee CPF Rate 
	//3--OW Total CPF Rate   4--AW Employee CPF Rate   5--AW Total Employee CPF Rate
	public static final Integer OWEMPLOYEE_CPFRATE = 2;
	public static final Integer OWTOTALRATE_CPFRATE = 3;
	public static final Integer AWEMPLOYEE_CPFRATE = 4;
	public static final Integer AWTOTAL_CPFRATE = 5;
	//PR��һ��
	public static final String FIRSTPR = "PR01";
	//PR�ڶ���
	public static final String SENCONDPR = "PR02";
	//PR������
	public static final String THIRDPR = "PR03";
	
	//����˰
	public static final String NONECPF = "Nil";
	//Singapore Citizen
	public static final String SINGAPORE_CITIZEN = "NRIC-PINK";
	//Singapore PR
	public static final String SINGAPORE_PR = "NRIC-BLUE";
	
	//private company mode
	public static final String PRIVATE_COMPANY = "PRIVATE";
	//public company mode
	public static final String PUBLIC_COMPANY = "PUBLIC";
	//PR ���
	public static final String PRCODE = "PR0";
	//G/G F/G
	public static final String FG_ContributionMode = "FG";
	public static final String GG_ContributionMode = "GG";
	public static final String OW = "OW";
	public static final String AW = "AW";
	public static final String TW = "TW";
	public static final String NPE = "NPE";
	/*
	 * �ܽ��ɲ��� or ��Ա���ɲ��� 0--�ܽ���CPF  1--��Ա����CPF  2--OW Employee CPF Rate 
	 * 3--OW Total CPF Rate   4--AW Employee CPF Rate   5--AW Total Employee CPF Rate
	 */
	private Integer payer; 
	
	//��˾����  0--private  1--public
	private Integer orgmode;
	
	//PR ���
	private Integer pryear;
	
	public Integer getOrgmode() {
		return orgmode;
	}
	public void setOrgmode(Integer orgmode) {
		this.orgmode = orgmode;
	}
	public Integer getPryear() {
		return pryear;
	}
	public void setPryear(Integer pryear) {
		this.pryear = pryear;
	}
	public Integer getPayer() {
		return payer;
	}
	public void setPayer(Integer payer) {
		this.payer = payer;
	}
	
	
	

}
