package nc.constant.hrrp.report;

/**
 * <p>Title: Constant</P>
 * <p>Description: ������</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * @author 
 * @version 1.0
 */
public class Constant {
	
	/**
	 * ������������ʵ��
	 */
	protected Constant() {
	}

	/**
	 * <p>Title: FORMAT</P>
	 * <p>Description: ���ݸ�ʽ</p>
	 * <p>Copyright: Copyright (c) 2008</p>
	 * @author 
	 * @version 1.0
	 * @since 2014-10-29
	 */
	public interface FORMAT{
		/**
		 * ԭ����
		 */
		public static final String DATA = "1001ZZ1000000001D5EF";
		/**
		 * ԭֵ
		 */
		public static final String NONE = "1001ZZ1000000001D5EG";
		/**
		 * ��λС������
		 */
		public static final String NUMBER = "1001ZZ1000000001D5EH";
		/**
		 * ����
		 */
		public static final String BOOL = "1001ZZ1000000001D5EI";
		/**
		 * ���ַ�
		 */
		public static final String SINGLE = "1001ZZ1000000001D5EJ";		
		/**
		 * ����
		 */
		public static final String INT = "1001ZZ1000000001D5EK";
		/**
		 * С��
		 */
		public static final String DECIMAL = "1001ZZ1000000001D5EL";
		/**
		 * ��ǰҳ��
		 */
		public static final String PAGE_NOW = "1001ZZ1000000001D5EM";
		/**
		 * ��ҳ��
		 */
		public static final String TOTAL = "1001ZZ1000000001D5EN";
		/**
		 * ����
		 */
		public static final String DATE = "1001ZZ1000000001D5EO";
		/**
		 * �ָ��ʽ
		 */
		public static final String SPLITE = "1001ZZ1000000001D5EP";
		
		/**
		 * �ո�ָ�
		 */
		public static final String SPACE = "1001ZZ1000000001D5EQ";
		/**
		 * ̩�ĸ�ʽ
		 */
		public static final String TAIWEN="1001ZZ1000000001D5ER";
	}
	
	/**
	 * <p>Title: BOOL</P>
	 * <p>Description: ����ֵ</p>
	 * @author 
	 * @version 1.0
	 */
	public interface BOOL{
		/**
		 *  ��
		 */
		public static final String YES = "1001ZZ1000000001D5ET";
		
		/**
		 *  ��
		 */
		public static final String NO = "1001ZZ1000000001D5EU";
		/**
		 *  �ı�
		 */
		public static final String TEXT = "1001ZZ1000000001D5EV";
	}
	
	public interface TFCONSTANT{
		/**
		 *  DEFDOCLIST
		 */
		public static final String TFPKDEFDOCLIST = "1001ZZ10000000001O6Q";
		/**
		 *  ��Ա��Ӧ�ֶ���
		 */
		public static final String PSN_TAXFREE_FIELD = "psn_field";
		/**
		 *  TAX8��Ӧ�ֶ���
		 */
		public static final String PSN_TAX8_FIELD = "psn_tax8";
		/**
		 *  TAX��Ӧ�ֶ�ǰ׺
		 */
		public static final String PSN_TAX_FIELD = "psn_tax";
		
		public static final String PSN_SNIC_FIELD = "psn_snic";
	}
	
	public interface REPCONSTANT{
		/**
		 *  �����ļ���
		 */
		public static final String FONT_NAME = "cordia.ttf";
		/**
		 *  ϵͳ����·��
		 */
		public static final String FONT_SYS_PATH = "C:\\Windows\\Fonts\\cordia.ttf";
	}
}
