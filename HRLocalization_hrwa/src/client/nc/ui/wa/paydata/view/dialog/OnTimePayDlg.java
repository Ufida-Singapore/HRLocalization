package nc.ui.wa.paydata.view.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.message.reconstruction.ButtomLineBorder;
import nc.ui.hr.frame.util.table.SelectableBillScrollPane;
import nc.ui.ml.NCLangRes;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIButton;
import nc.ui.pub.beans.UICheckBox;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UITextArea;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillEditListener;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.BillModelCellEditableController;
import nc.ui.pub.bill.IBillModelDecimalListener2;
import nc.ui.pub.style.Style;
import nc.ui.wa.paydata.view.WaClasssItemDecimalAdapter;
import nc.ui.wa.pub.WADelegator;
import nc.vo.jcom.util.Convertor;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.wa.paydata.PsncomputeVO;
import nc.vo.wa.paydata.PsndocWaVO;
import nc.vo.wa.pub.WaLoginVO;
/**
 * ʱ��н��
 * @author: zhangg
 * @date: 2009-12-7 ����02:53:46
 * @since: eHR V6.0
 * @�߲���:
 * @�߲�����:
 * @�޸���:
 * @�޸�����:
 */
public class OnTimePayDlg extends UIDialog implements ActionListener {

	private static final long serialVersionUID = 6630519046205874174L;
	private javax.swing.JPanel dialogContentPane = null;
	private nc.ui.pub.beans.UIPanel panBottom = null;
	private nc.ui.pub.beans.UIPanel panCompute = null;
	private nc.ui.pub.beans.UIPanel panTop = null;
	private nc.ui.pub.beans.UIButton btClear = null;
	private nc.ui.pub.beans.UIButton btReplace = null;
	private nc.ui.pub.beans.UIButton btCompute = null;

	private nc.ui.pub.beans.UIButton btSave = null;
	private nc.ui.pub.beans.UIButton btCancel = null;

	private String[] saBodyColKeyName = null;
	private String[] saBodyColName = null;
	private SelectableBillScrollPane billscropane = null;
	private nc.ui.pub.beans.UIPanel panBody = null;
	private WaLoginVO waLoginVO = null;

	private nc.ui.pub.beans.UIRadioButton radioDay = null;
	private nc.ui.pub.beans.UIRadioButton radioHand = null;

	private PsncomputeVO[] vos = null;
	
	private Color c = Style.getDlgBgColor();

	public OnTimePayDlg(java.awt.Container parent, WaLoginVO waLoginVO ) {
		super(parent);
		this.waLoginVO = waLoginVO;

		initialize();
		this.setResizable(true);
	}


	/**
	 * Invoked when an action occurs.
	 */
	public void actionPerformed(java.awt.event.ActionEvent e) {
		try {
			if (e.getSource() == getUIBtClear()) {
				initTableData();
			} else if (e.getSource() == getUIBtReplace()) {
				onReplace();
			} else if (e.getSource() == getUIBtCompute()) {
				onCaculate();
				//add by weiningc ʱ��н�ʼ��㱣�水ť����һ,���ر��水ť 20191011 start
				onSave();
				//end
			} else if (e.getSource() == getIvjUIBtCancel()) {
				this.closeOK();
			} else if (e.getSource() == getIvjUIBtSave()) {
				onSave();
			}else if (e.getSource() == getbnOk()) {
				onBnOk();
			}
		} catch (Exception ee) {
			reportException(ee);
			MessageDialog.showErrorDlg(this, null, ee.getMessage());
		}

	}



	private void addListener() {
		getbillscropane().getTable().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
		getbillscropane().getTable().setColumnSelectionAllowed(false);

		getbillscropane().addEditListener(new BillEditListener() {

			public void afterEdit(BillEditEvent e) {

				int rowIndex = getbillscropane().getTable().getSelectedRow();

				if (rowIndex >= 0 && e.getKey() != "select") {
					getbillscropane().setBodyCellValue(UFBoolean.valueOf(false), rowIndex, "iscompute");
				}

			}

			public void bodyRowChange(BillEditEvent e) {}

		});

		//���С��λ������
		String[] billitems = new String[]{"wadocnmoney","wanmoney","oldwadocnmoney"};
		IBillModelDecimalListener2 bmd = new WaClasssItemDecimalAdapter(PsndocWaVO.PK_WA_ITEM, billitems,waLoginVO);

		getbillscropane().getTableModel().addDecimalListener(bmd);

	}

	/**
	 * ���� billscropane ����ֵ��
	 *
	 * @return nc.ui.pub.bill.BillScrollPane
	 */
	private SelectableBillScrollPane getbillscropane() {
		if (billscropane == null) {
			try {
				billscropane = new SelectableBillScrollPane();
				billscropane.setName("billscropane");
				LineBorder border = new LineBorder(Color.getColor("#b5b5b8"));
				billscropane.setBorder(border);
//				billscropane.setBounds(10, 80,700, 280);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return billscropane;
	}

	/**
	 * ���� UIRadioDay ����ֵ��
	 *
	 * @return nc.ui.pub.beans.UIRadioButton
	 */
	private nc.ui.pub.beans.UIRadioButton getUIRadioDay() {
		if (radioDay == null) {
			try {
				radioDay = new nc.ui.pub.beans.UIRadioButton();
				radioDay.setName("UIRadioDay");
				radioDay.setText(ResHelper.getString("60130paydata","060130paydata0392")/*@res "��ָ������������н"*/);
				radioDay.setBounds(6, 0, 164, 22);
				radioDay.addActionListener(this);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return radioDay;
	}

	/**
	 * ���� UIRadioHand ����ֵ��
	 *
	 * @return nc.ui.pub.beans.UIRadioButton
	 */
	private nc.ui.pub.beans.UIRadioButton getUIRadioHand() {
		if (radioHand == null) {
			try {
				radioHand = new nc.ui.pub.beans.UIRadioButton();
				radioHand.setName("UIRadioHand");
				radioHand.setText(ResHelper.getString("60130paydata","060130paydata0393")/*@res "�ֹ�ָ����н"*/);
				radioHand.setBounds(280, 0, 143, 22);
				radioHand.addActionListener(this);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return radioHand;
	}

	/**
	 * �˴����뷽��˵���� �������ڣ�(2004-6-16 13:54:04)
	 *
	 * @return java.lang.String[]
	 */
	public String[] getColKey() {
		return new String[] {
				"ts",
				"select",
				"deptname",
				"clerkcode",
				"psnname",
				"itemvname",
				"begindate",
				"nowjdname",
				"wadocnmoney",
				"oldjdname",
				"oldwadocnmoney",
				"iscompute",
				"wanmoney",
				"wanbeforemoney",
				"wanceforedays",
				"wanaftermoney",
				"wanafterdays",
				"daywage",
				"basedays",
				"pk_psncompute",
				"pk_psndoc_sub",
				"pk_wa_item",
				"pk_psndoc",
				"sub_ts",
				"pre_sub_id",
				"pre_sub_ts", "assgid" };
	}

	public int getColType(String colKey){
		if(colKey.equalsIgnoreCase("wadocnmoney") ){
			return BillItem.DECIMAL;
		}else if(colKey.equalsIgnoreCase("oldwadocnmoney") ){
			return BillItem.DECIMAL;
		}else if(colKey.equalsIgnoreCase("wanmoney") ){
			return BillItem.DECIMAL;
		}else if(colKey.equalsIgnoreCase("wanbeforemoney") ){
			return BillItem.DECIMAL;
		}else if(colKey.equalsIgnoreCase("wanaftermoney") ){
			return BillItem.DECIMAL;
		}else if(colKey.equalsIgnoreCase("daywage") ){
			return BillItem.INTEGER;
		}else if(colKey.equalsIgnoreCase("wanceforedays") ){
			return BillItem.DECIMAL;
		}else if(colKey.equalsIgnoreCase("wanafterdays")|| colKey.equalsIgnoreCase("basedays") ){
			return BillItem.DECIMAL;
		}else if(colKey.equalsIgnoreCase("iscompute") ){
			return BillItem.BOOLEAN;
		}else if(colKey.equalsIgnoreCase("select") ){
			return BillItem.BOOLEAN;
		}else if(colKey.equalsIgnoreCase("begindate") ){
			return BillItem.DATE;
		}else if(colKey.equalsIgnoreCase("ts") ){
			return BillItem.DATETIME;
		}else if(colKey.equalsIgnoreCase("sub_ts") ){
			return BillItem.DATETIME;
		}else if(colKey.equalsIgnoreCase("pre_sub_ts") ){
			return BillItem.DATETIME;
		} else if (colKey.equalsIgnoreCase("assgid")) {
			return BillItem.INTEGER;
		}else{
			return BillItem.STRING;
		}
	}

	/**
	 * �˴����뷽��˵���� �������ڣ�(2004-6-16 13:54:04)
	 *
	 * @return java.lang.String[]
	 */
	public String getColName(String colKey) {
		if(colKey.equalsIgnoreCase("wanbeforemoney") ){
			return ResHelper.getString("60130paydata","060130paydata0400")/*@res "��нǰ��н"*/;
		}else if(colKey.equalsIgnoreCase("wanaftermoney") ){
			return ResHelper.getString("60130paydata","060130paydata0402")/*@res "��н����н"*/ ;
		}else if(colKey.equalsIgnoreCase("basedays") ){
			return ResHelper.getString("60130paydata","060130paydata0405")/*@res "��׼����"*/;
		}else if(colKey.equalsIgnoreCase("wanceforedays") ){
			return ResHelper.getString("60130paydata","060130paydata0401")/*@res "��нǰ����"*/;
		}else if(colKey.equalsIgnoreCase("wanafterdays") ){
			return ResHelper.getString("60130paydata","060130paydata0403")/*@res "��н������"*/;
		}else{
			return "";
		}
	}
	public String[] getColName() {
		return new String[] {
				"ts",
				ResHelper.getString("common","UC000-0004044")/*@res "ѡ��"*/,
				nc.ui.ml.NCLangRes.getInstance().getStrByID("common", "UC000-0004064")/* "����" */,
				ResHelper.getString("60130adjapprove","160130adjapprove0009")/*@res "Ա����"*/ ,
				ResHelper.getString("common","UC000-0001403")/*@res "����"*/ ,
				nc.ui.ml.NCLangRes.getInstance().getStrByID("common", "UC000-0003385")/* "н����Ŀ" */,
				ResHelper.getString("60130paydata","060130paydata0394")/*@res "��н����"*/ ,
				ResHelper.getString("60130paydata","060130paydata0395")/*@res "�ּ�/��"*/ ,
				ResHelper.getString("60130paydata","060130paydata0396")/*@res "�ֽ��"*/ ,
				ResHelper.getString("60130paydata","060130paydata0397")/*@res "ԭ��/��"*/ ,
				ResHelper.getString("60130paydata","060130paydata0398")/*@res "ԭ���"*/ ,
				ResHelper.getString("60130paydata","060130paydata0380")/*@res "�����־"*/ ,
				ResHelper.getString("60130paydata","060130paydata0399")/*@res "���Ž��"*/ ,
				ResHelper.getString("60130paydata","060130paydata0400")/*@res "��нǰ��н"*/ ,
				ResHelper.getString("60130paydata","060130paydata0401")/*@res "��нǰ����"*/ ,
				ResHelper.getString("60130paydata","060130paydata0402")/*@res "��н����н"*/ ,
				ResHelper.getString("60130paydata","060130paydata0403")/*@res "��н������"*/ ,
				ResHelper.getString("60130paydata","060130paydata0404")/*@res "��нָ����ʽ"*/ ,
				ResHelper.getString("60130paydata","060130paydata0405")/*@res "��׼����"*/,
				ResHelper.getString("common","UC000-0000085")/*@res "����"*/ ,
				ResHelper.getString("60130paydata","060130paydata0406")/*@res "hi_psndoc_wadoc����"*/ ,
				ResHelper.getString("60130paydata","060130paydata0407")/*@res "н����Ŀ����"*/ ,
				ResHelper.getString("common","UC000-0000131")/*@res "��Ա����"*/,
				"sub_ts",
				"pre_sub_id",
				"pre_sub_ts", "assgid"

		};
	}
	/**
	 * ���� UIBtCompute ����ֵ��
	 *
	 * @return nc.ui.pub.beans.UIButton
	 */
	private nc.ui.pub.beans.UIButton getUIBtClear() {
		if (btCompute == null) {
			try {
				char cHotKey = 'O';
				btClear = new nc.ui.pub.beans.UIButton();
				btClear.setName("UIBtClear");
				btClear.setText(ResHelper.getString("60130paydata","060130paydata0478")/*@res "����"*/);
//								btClear.setBounds(6, 4, 70, 20);
				btClear.setMnemonic(cHotKey);
				btClear.addActionListener(this);
				btClear.setToolTipText(ResHelper.getString("60130paydata","060130paydata0478")/*@res "����"*/+"(ALT+O)");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return btClear;
	}

	/**
	 * ���� UIBtCompute ����ֵ��
	 *
	 * @return nc.ui.pub.beans.UIButton
	 */
	private nc.ui.pub.beans.UIButton getUIBtReplace() {
		if (btCompute == null) {
			try {
				char cHotKey = 'R';
				btReplace = new nc.ui.pub.beans.UIButton();
				btReplace.setName("UIBtReplace");
				btReplace.setText(ResHelper.getString("60130paydata","060130paydata0479")/*@res "�滻"*/);
//								btReplace.setBounds(86, 4, 70, 20);
				btReplace.setMnemonic(cHotKey);
				btReplace.addActionListener(this);
				btReplace.setToolTipText(ResHelper.getString("60130paydata","060130paydata0479")/*@res "�滻"*/+"(ALT+R)");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return btReplace;
	}

	/**
	 * ���� UIBtCompute ����ֵ��
	 *
	 * @return nc.ui.pub.beans.UIButton
	 */
	private nc.ui.pub.beans.UIButton getUIBtCompute() {
		if (btCompute == null) {
			try {
				char cHotKey = 'C';
				btCompute = new nc.ui.pub.beans.UIButton();
				btCompute.setName("UIBtCompute");
				btCompute.setText(ResHelper.getString("60130paydata","060130paydata0408")/*@res "����"*/);
//				btCompute.setBounds(166, 4, 70, 20);
				btCompute.setMnemonic(cHotKey);
				btCompute.addActionListener(this);
				btCompute.setToolTipText(ResHelper.getString("60130paydata","060130paydata0408")/*@res "����"*/+"(ALT+C)");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return btCompute;
	}

	/**
	 * ���� UIDialogContentPane ����ֵ��
	 *
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getUIDialogContentPane() {
		if (dialogContentPane == null) {
			try {
				dialogContentPane = new javax.swing.JPanel();
				dialogContentPane.setName("UIDialogContentPane");
				dialogContentPane.setLayout(new java.awt.BorderLayout());
				getUIDialogContentPane().add(getUIPanCompute(), "Center");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return dialogContentPane;
	}

	/**
	 * ���� UIPanBody ����ֵ��
	 *
	 * @return nc.ui.pub.beans.UIPanel
	 */
	private nc.ui.pub.beans.UIPanel getUIPanBody() {
		if (panBody == null) {
			try {
				panBody = new nc.ui.pub.beans.UIPanel();
				panBody.setName("UIPanBody");
				panBody.setLayout(new BorderLayout());
//				panBody.setBounds(0, 80, 700, 300);
				panBody.setBackground(c);
				getUIPanBody().add(getbillscropane(),BorderLayout.CENTER);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return panBody;
	}
	public void onReplace() throws BusinessException {

		PsncomputeVO[]  psncomputeVOs = (PsncomputeVO[])getbillscropane().getSelectedBodyVOs(PsncomputeVO.class);
		if(psncomputeVOs == null || psncomputeVOs.length <=0){
			throw new BusinessException( ResHelper.getString("60130paydata","060130paydata0484")/*@res "û��ѡ��Ҫ�滻����!"*/);
		}
		getReplaceDlg();
		initItem();
		getReplaceDlg().showModal();

		if (getReplaceDlg().getResult() == UIDialog.ID_OK) {

			nc.vo.pub.lang.UFDouble douNum = new nc.vo.pub.lang.UFDouble(replaceValue);
			int rowCount = getbillscropane().getTable().getRowCount();
			if (rowCount > 0) {
				for (int i = 0; i < rowCount; i++) {
					if (getbillscropane().isSelected(i)) {
						getbillscropane().setBodyCellValue(douNum, i, replaceItem);
					}
				}
			}
		}

	}

	/**
	 *
	 * @author zhangg on 2009-5-7
	 * @throws BusinessException
	 */
	public void onCaculate() throws BusinessException {


		int rowCount = getbillscropane().getTable().getRowCount();
		if (rowCount > 0) {
			for (int i = 0; i < rowCount; i++) {
				if (getbillscropane().isSelected(i)) {
					setCacaluteValue(i);
				}
			}
		}

	}


	/**
	 *
	 */
	private void setCacaluteValue(int rowIndex) throws BusinessException {
		//��ָ������������н��
		if( getUIRadioDay().isSelected()){
			//��нǰн��
			UFDouble wadocnmoney = getCellDoubleValue(rowIndex, "wadocnmoney");//wadocnmoney
			//��н��н��
			UFDouble oldwadocnmoney = getCellDoubleValue(rowIndex, "oldwadocnmoney");
			//��׼����
			UFDouble basedays = getCellDoubleValue(rowIndex, "basedays");
			//��нǰ��н
			if(basedays.equals(UFDouble.ZERO_DBL)){
				//�����׼����Ϊ0 ������нΪ0
				getbillscropane().setBodyCellValue(UFDouble.ZERO_DBL, rowIndex, "wanbeforemoney");
				//��н����н
				getbillscropane().setBodyCellValue(UFDouble.ZERO_DBL, rowIndex, "wanaftermoney");
			}else{
				getbillscropane().setBodyCellValue(oldwadocnmoney.div(basedays), rowIndex, "wanbeforemoney");
				//��н����н
				getbillscropane().setBodyCellValue(wadocnmoney.div(basedays), rowIndex, "wanaftermoney");
			}

		}
		UFDouble oldvalue = getCellDoubleValue(rowIndex, "wanbeforemoney").multiply(getCellDoubleValue(rowIndex, "wanceforedays"));
		UFDouble newvalue = getCellDoubleValue(rowIndex, "wanaftermoney").multiply(getCellDoubleValue(rowIndex, "wanafterdays"));

		getbillscropane().setBodyCellValue(oldvalue.add(newvalue), rowIndex, "wanmoney");
		getbillscropane().setBodyCellValue(UFBoolean.valueOf(true), rowIndex, "iscompute");

		getbillscropane().setBodyCellValue(getUIRadioDay().isSelected()? 0: 1, rowIndex, "daywage");

	}
	/**
	 *
	 * @author zhangg on 2009-5-7
	 * @param rowIndex
	 * @param key
	 * @return
	 * @throws BusinessException
	 */
	private UFDouble getCellDoubleValue(int rowIndex, String key) throws BusinessException{
		Object valueObject = getbillscropane().getBodyCellValue(rowIndex, key);
		if(valueObject == null || valueObject.toString().trim().length() == 0){
			String message = nc.ui.ml.NCLangRes.getInstance().getStrByID("60130paydata",
					"060130paydata0547", null,
					new String[] { nc.vo.format.Format.indexFormat(rowIndex + 1) })/* @res "��{0}�У������ݲ�����Ϊ��"��" */;

			throw new BusinessException(message);
		}
		return new UFDouble(valueObject.toString());

	}
	/**
	 * ���� UIPanBottom ����ֵ��
	 *
	 * @return nc.ui.pub.beans.UIPanel
	 */
	private nc.ui.pub.beans.UIPanel getUIPanBottom() {
		if (panBottom == null) {
			try {
				panBottom = new nc.ui.pub.beans.UIPanel();
				panBottom.setName("UIPanBottom");
				panBottom.setPreferredSize(new java.awt.Dimension(100, 30));
				java.awt.FlowLayout ivjUIPanel1FlowLayout = new java.awt.FlowLayout(FlowLayout.RIGHT);
				ivjUIPanel1FlowLayout.setHgap(10);
				panBottom.setLayout(ivjUIPanel1FlowLayout);

				panBottom.add(getUIBtClear());
				panBottom.add(getUIBtReplace());
				panBottom.add(getUIBtCompute());
				panBottom.add(getIvjUIBtSave());
				panBottom.add(getIvjUIBtCancel());
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return panBottom;
	}

	/**
	 * ���� UIPanCompute ����ֵ��
	 *
	 * @return nc.ui.pub.beans.UIPanel
	 */
	private nc.ui.pub.beans.UIPanel getUIPanCompute() {
		if (panCompute == null) {
			try {
				panCompute = new nc.ui.pub.beans.UIPanel();
				panCompute.setName("UIPanCompute");
				
				panCompute.setLayout(new java.awt.BorderLayout());
				getUIPanCompute().add(getUIPanTop(), "North");
				getUIPanCompute().add(getUIPanBottom(), "South");
				getUIPanCompute().add(getUIPanBody(), "Center");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return panCompute;
	}

	/**
	 * ���� UIPanTop ����ֵ��
	 *
	 * @return nc.ui.pub.beans.UIPanel
	 */
	private nc.ui.pub.beans.UIPanel getUIPanTop() {
		if (panTop == null) {
			try {
				panTop = new nc.ui.pub.beans.UIPanel();
				panTop.setName("UIPanTop");
				panTop.setPreferredSize(new java.awt.Dimension(100, 30));
				panTop.setLayout(new FlowLayout(FlowLayout.LEFT));
				
				getUIPanTop().add(getUIRadioDay());
				getUIPanTop().add(getUIRadioHand());
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return panTop;
	}

	/**
	 * ÿ�������׳��쳣ʱ������
	 *
	 * @param exception
	 *            java.lang.Throwable
	 */
	private void handleException(java.lang.Throwable exception) {

		Logger.error(exception.getMessage(),exception);
	}



	public void initColKeyName()   {
		// �ؼ���
		Vector<String> vecKeyName = new Vector<String>();

		for (int i = 0; i < getColKey().length; i++) {
			vecKeyName.addElement(getColKey()[i]);
		}
		saBodyColKeyName = (String[])Convertor.convertVectorToArray(vecKeyName);
	}

	public void initColName()  {
		// ��ʾ����
		Vector<String> vecColName = new Vector<String>();
		for (int i = 0; i < getColName().length; i++) {
			vecColName.addElement(getColName()[i]);
		}
		saBodyColName = (String[]) Convertor.convertVectorToArray(vecColName);

	}



	/**
	 * ��ʼ���ࡣ
	 */
	/* ���棺�˷������������ɡ� */
	private void initialize() {
		try {
			setName("DlgShowResult");
			setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			setSize(800, 435);
			setTitle(ResHelper.getString("60130paydata","060130paydata0409")/*@res "Ա��н�ʼ���"*/ );
			setContentPane(getUIDialogContentPane());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
		try {
			javax.swing.ButtonGroup g = new javax.swing.ButtonGroup();
			g.add(getUIRadioDay());
			g.add(getUIRadioHand());
			getUIRadioDay().setSelected(true);

			initTable();
			initTableData();
		} catch (BusinessException e) {
			reportException(e);
			nc.ui.pub.beans.MessageDialog.showHintDlg(this, "error", e.getMessage());
		}
	}

	public void initTable()  {

		// ��ʾ����
		initColName();

		// �ؼ���
		initColKeyName();

		BillItem[] biaBody = new BillItem[saBodyColName.length];
		String[] notshow = { "daywage", "pk_psncompute", "pk_psndoc_sub",
				"pk_wa_item", "pk_psndoc", "ts", "sub_ts", "pre_sub_id",
				"pre_sub_ts", "assgid" };
		List<String> list = Arrays.asList(notshow);
		for (int i = 0; i < saBodyColName.length; i++) {
			biaBody[i] = new BillItem();
			biaBody[i].setName(saBodyColName[i]);
			biaBody[i].setKey(saBodyColKeyName[i]);
			if(saBodyColName[i].length()>3){
				biaBody[i].setWidth(80);
			}else{
				biaBody[i].setWidth(60);
			}

			if (list.contains(saBodyColKeyName[i])) {
				biaBody[i].setShow(false);
			} else {
				biaBody[i].setShow(true);
			}

			biaBody[i].setDataType(getColType(saBodyColKeyName[i]));
			if (biaBody[i].getKey().equals("select")) {
				biaBody[i].setComponent(new UICheckBox());
			}
			if (getColType(saBodyColKeyName[i]) == BillItem.DECIMAL) {
				if(saBodyColKeyName[i].equalsIgnoreCase("wanaftermoney")
						|| saBodyColKeyName[i].equalsIgnoreCase("wanbeforemoney") ){
					biaBody[i].setDecimalDigits(4);
				}else{
					//					//�ֽ�ԭ�����Ž���С��λ�����⡣
					//					if(saBodyColKeyName[i].equalsIgnoreCase("wadocnmoney")
					//							|| saBodyColKeyName[i].equalsIgnoreCase("wanmoney")
					//							|| saBodyColKeyName[i].equalsIgnoreCase("oldwadocnmoney")){
					//						biaBody[i].setDecimalDigits(8);
					//					}else{
					biaBody[i].setDecimalDigits(2);
					//					}


				}

			}
			if (biaBody[i].getDataType() == BillItem.BOOLEAN) {
				((UICheckBox) (biaBody[i].getComponent())).setHorizontalAlignment(UICheckBox.CENTER);
			}

			biaBody[i].setTatol(false);
			biaBody[i].setEdit(true);

			if (saBodyColKeyName[i].equalsIgnoreCase("wanceforedays") || saBodyColKeyName[i].equalsIgnoreCase("wanafterdays") || saBodyColKeyName[i].equalsIgnoreCase("wanaftermoney")
					|| saBodyColKeyName[i].equalsIgnoreCase("wanbeforemoney") || saBodyColKeyName[i].equalsIgnoreCase("basedays")) {
				biaBody[i].setNull(true);
			} else {
				biaBody[i].setNull(false);

			}
		}
		BillModel billModel = new BillModel();

		billModel.setCellEditableController(new BillModelCellEditableController() {

			public boolean isCellEditable(boolean blIsEditable, int iRowIndex, String strItemKey) {

				return isBodyCellEditable(blIsEditable, iRowIndex, strItemKey);
			}

		});
		billModel.setBodyItems(biaBody);
		getbillscropane().setTableModel(billModel);
		getbillscropane().setRowNOShow(true);
		addListener();
		getbillscropane().setSelectRowCode("select");

	}


	private boolean isBodyCellEditable(boolean blIsEditable, int iRowIndex, String strItemKey) {
		if (strItemKey.equalsIgnoreCase("select")) {
			return true;
		}
		boolean isDay = getUIRadioDay().isSelected();

		Object select = getbillscropane().getBodyCellValue(iRowIndex, "select");
		if (select == null || !UFBoolean.valueOf(select.toString()).booleanValue()) {
			return false;
		}
		if (isDay) {
			if (strItemKey.equalsIgnoreCase("wanceforedays") || strItemKey.equalsIgnoreCase("wanafterdays")) {
				return true;
			}
		} else {
			if (strItemKey.equalsIgnoreCase("wanceforedays") || strItemKey.equalsIgnoreCase("wanafterdays") || strItemKey.equalsIgnoreCase("wanaftermoney")
					|| strItemKey.equalsIgnoreCase("wanbeforemoney")) {
				return true;
			}
		}
		if(strItemKey.equalsIgnoreCase("basedays")){
			return true;
		}
		return false;

	}


	public void initTableData() throws BusinessException {
		//�˴�Ӧ�ø���н����Ŀ��������С��λ����
		vos = WADelegator.getPsndocWa().queryAllShowResult(waLoginVO, waLoginVO.getDeptpower(),  waLoginVO.getPsnclpower() );

		if (vos != null && vos.length > 0) {
			getbillscropane().getTableModel().clearBodyData();
			getbillscropane().getTableModel().setBodyDataVO(vos);
		}

	}

	public nc.ui.pub.beans.UIButton getIvjUIBtSave() {
		if (btSave == null) {
			btSave = new UIButton();
			char cHotKey = 'S';
			btSave.setText(NCLangRes.getInstance().getStrByID("common", "UC001-0000001")/* "����" */);
			btSave.setMnemonic(cHotKey);
			btSave.addActionListener(this);
			btSave.setToolTipText(NCLangRes.getInstance().getStrByID("common", "UC001-0000001")/* "����" */+"(ALT+S)");
//			btSave.setBounds(240, 4, 70, 20);
		}
		//add by weiningc ʱ��н�ʼ��㱣�水ť����һ,���ر��水ť 20191011 start
		btSave.hide();
		//end
		return btSave;
	}

	public nc.ui.pub.beans.UIButton getIvjUIBtCancel() {
		if (btCancel == null) {
			char cHotKey = 'Z';
			btCancel = new UIButton();
			btCancel.setText(nc.ui.ml.NCLangRes.getInstance().getStrByID("60130paydata", "060130paydata0530")/* "����" */);
			btCancel.setMnemonic(cHotKey);
			btCancel.addActionListener(this);
			btCancel.setToolTipText(nc.ui.ml.NCLangRes.getInstance().getStrByID("60130paydata", "060130paydata0530")/* "����" */+"(ALT+Z)");
//			btCancel.setBounds(280, 4, 70, 20);
		}
		return btCancel;
	}

	public void onSave() throws BusinessException{
		PsncomputeVO[]  psncomputeVOs = (PsncomputeVO[])getbillscropane().getSelectedBodyVOs(PsncomputeVO.class);
		if(psncomputeVOs == null || psncomputeVOs.length <=0){
			throw new BusinessException(ResHelper.getString("60130paydata","060130paydata0410")/*@res "û��ѡ��Ҫ�������!"*/);
		}

		PsndocWaVO[] psndocWaVOs = convertComputeVO(psncomputeVOs);
		WADelegator.getPsndocWa().updatePsndocWas(psndocWaVOs);
		//shenliangc 20140826 ʱ��н�ʼ��㱣��֮������������Ӧ��Ա�ķ������ݼ����־
		WADelegator.getPaydata().updateCalFlag4OnTime(psndocWaVOs);
		//��α��浽н�ʷ������ݡ�
		this.closeOK();
	}

	/**
	 *
	 * @author zhangg on 2009-5-11
	 * @param psncomputeVOs
	 * @return
	 * @throws BusinessException
	 */

	public PsndocWaVO[] convertComputeVO(PsncomputeVO[] psncomputeVOs) throws BusinessException {

		PsndocWaVO[] psndocWaVOs = new PsndocWaVO[psncomputeVOs.length];
		for (int i = 0; i < psncomputeVOs.length; i++) {
			PsncomputeVO result = psncomputeVOs[i];
			PsndocWaVO psndocWa = new PsndocWaVO();
			for(PsncomputeVO vo:vos){
				if(vo.getPk_psndoc().equals(result.getPk_psndoc())&&vo.getPk_wa_item().equals(result.getPk_wa_item()) )
					psndocWa = vo.getPsndocwavo();
			}

			if(result.getIscompute() == null || !result.getIscompute().booleanValue()){
				throw new BusinessException(ResHelper.getString("60130paydata","060130paydata0411")/*@res "ѡ�е��У� �е�û�м��㣡���ȼ��㡣"*/);
			}
			boolean bool=WADelegator.getPsndocWa().isCheckPsndocWaHave(waLoginVO,result.getPk_psndoc());
			if(bool==true){
				throw new BusinessException(result.getPsnname()+ ResHelper.getString("60130paydata","060130paydata0412")/*@res "�Ѿ���˲��ܹ����棬 �벻Ҫѡ��"*/);
			}
			psndocWa.setPk_psndoc_sub(result.getPk_psndoc_sub());
			psndocWa.setPsnname(result.getPsnname());
			psndocWa.setPsncode(result.getPsncode());
			psndocWa.setPk_psndoc(result.getPk_psndoc());
			psndocWa.setPk_psndoc_wa(result.getPk_psncompute());
			psndocWa.setPk_wa_item(result.getPk_wa_item());
			psndocWa.setItemname(result.getItemvname());
			psndocWa.setOldmoney(result.getOldwadocnmoney());
			psndocWa.setNowmoney(result.getWadocnmoney());
			psndocWa.setNmoney(result.getWanmoney());
			psndocWa.setCyear(waLoginVO.getCyear());
			psndocWa.setCperiod(waLoginVO.getCperiod());
			psndocWa.setPk_wa_class(waLoginVO.getPeriodVO().getPk_wa_class());
			psndocWa.setWaclassname(waLoginVO.getName());
			UFLiteralDate startDate = waLoginVO.getPeriodVO().getCstartdate();

			UFLiteralDate endDate = waLoginVO.getPeriodVO().getCenddate();

			psndocWa.setBegindate(startDate);
			psndocWa.setEnddate(endDate);


			psndocWa.setNceforedays(result.getWanceforedays());
			psndocWa.setNbeforemoney(result.getWanbeforemoney());
			psndocWa.setNafterdays(result.getWanafterdays());
			psndocWa.setNaftermoney(result.getWanaftermoney());
			psndocWa.setDaywage(result.getDaywage());
			psndocWa.setIwatype(Integer.valueOf(0));
			psndocWa.setTs(result.getTs());
			psndocWa.setBasedays(result.getBasedays());
			psndocWa.setSub_ts(result.getSub_ts());
			psndocWa.setPre_sub_id(result.getPre_sub_id());
			psndocWa.setPre_sub_ts(result.getPre_sub_ts());
			psndocWa.setAssgid(result.getAssgid());
			result.setPsndocwavo(psndocWa);

			psndocWaVOs[i] = psndocWa;
		}
		return psndocWaVOs;

	}

	private UIDialog replaceDlg = null;
	private JPanel ivjUIDialogContentPane = null;
	private JPanel northPanel = null;
	private UIComboBox ivjcmbEditableItem = null;
	private UITextArea ivjtaFormula = null;
	private UIButton ivjbnOk = null;
	private UIButton ivjbnCancel = null;
	private String[] editableName = null;
	private String[] editableItem = null;
	private String replaceValue = null;
	private String replaceItem = null;
	private UIDialog getReplaceDlg() {
		if(replaceDlg==null){
			try{
				replaceDlg = new UIDialog(this);
				replaceDlg.setName("ReplaceDlg");
				replaceDlg.setTitle("�滻"/* -=notranslate=- */);

				replaceDlg.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
				replaceDlg.setSize(new Dimension(510, 205));
				replaceDlg.setContentPane(getUIDialogContentPane1());
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return replaceDlg;
	}
	private javax.swing.JPanel getUIDialogContentPane1() {
		if(ivjUIDialogContentPane == null){
			try{
				ivjUIDialogContentPane = new javax.swing.JPanel();
				ivjUIDialogContentPane.setName("UIDialogContentPane");
				ivjUIDialogContentPane.setLayout(null);
				ivjUIDialogContentPane.add(getNorthPanel());
				getNorthPanel().setBorder(new ButtomLineBorder(Color.getColor("#b5b5b8")));
				ivjUIDialogContentPane.add(getbnOk(), null);
				ivjUIDialogContentPane.add(getbnCancel(), null);
				ivjUIDialogContentPane.setBackground(c);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjUIDialogContentPane;
	}
	private javax.swing.JPanel getNorthPanel() {
		if(northPanel == null){
			try{
				northPanel = new UIPanel();
				northPanel.setName("northPanel");
				northPanel.setLayout(null);
				northPanel.setBounds(0, 6, 510, 163);
//				northPanel.setBorder(BorderFactory.createTitledBorder(ResHelper.getString("60130paydata","060130paydata0353")/*@res "�滻"*/));
				northPanel.setBackground(c);
				nc.ui.pub.beans.UILabel ivjUILabel1 = new nc.ui.pub.beans.UILabel();
				ivjUILabel1.setName("UILabel1");
				ivjUILabel1.setText(ResHelper.getString("60130paydata","060130paydata0485")/*@res "����Ŀ:"*/);
				ivjUILabel1.setBounds(31, 24, 72, 22);
				/*nc.ui.pub.beans.UIComboBox ivjcmbEditableItem = new nc.ui.pub.beans.UIComboBox();
				ivjcmbEditableItem.setName("cmbEditableItem");
				ivjcmbEditableItem.setBounds(85, 24, 397, 22);*/
				nc.ui.pub.beans.UILabel ivjUILabel2 = new nc.ui.pub.beans.UILabel();
				ivjUILabel2.setName("UILabel2");
				ivjUILabel2.setText(ResHelper.getString("60130paydata","060130paydata0431")/*@res "�滻Ϊ:"*/);
				ivjUILabel2.setBounds(31, 57, 49, 22);
				nc.ui.pub.beans.UIScrollPane ivjUIScrollPane1 = new nc.ui.pub.beans.UIScrollPane();
				ivjUIScrollPane1.setName("UIScrollPane1");
				ivjUIScrollPane1.setBounds(78, 57, 397, 64);
				/*				nc.ui.pub.beans.UITextArea ivjtaFormula = new nc.ui.pub.beans.UITextArea();
				ivjtaFormula.setName("taFormula");
				ivjtaFormula.setLineWrap(true);
				ivjtaFormula.setBounds(0, 0, 189, 120);*/
				ivjUIScrollPane1.setViewportView(gettaFormula());


				northPanel.add(ivjUILabel1, ivjUILabel1.getName());
				northPanel.add(getcmbEditableItem(), getcmbEditableItem().getName());
				northPanel.add(ivjUILabel2, ivjUILabel2.getName());
				northPanel.add(ivjUIScrollPane1, ivjUIScrollPane1.getName());

			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return northPanel;
	}
	private nc.ui.pub.beans.UIButton getbnOk() {
		if(ivjbnOk ==null){
			try{
				char cHotkey = 'Y';
				ivjbnOk = new nc.ui.pub.beans.UIButton();
				ivjbnOk.setName("bnOk");
				ivjbnOk.setText(nc.ui.ml.NCLangRes.getInstance().getStrByID("common", "UC001-0000044")+"(" + cHotkey + ")"/*																						 */);
				ivjbnOk.setMnemonic(cHotkey);
				ivjbnOk.setBounds(new Rectangle(340, 175, 75, 22));
				ivjbnOk.setMargin(new java.awt.Insets(2, 0, 2, 0));
				ivjbnOk.addActionListener(this);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjbnOk;
	}
	public void onBnOk() throws BusinessException{
		replaceValue = gettaFormula().getText().trim();
		replaceItem = getEditableItem()[getcmbEditableItem().getSelectedIndex()];

		if(replaceItem ==null){
			throw new BusinessException(ResHelper.getString("60130paydata","060130paydata0486")/*@res "��ѡ��Ҫ�滻����Ŀ"*/);
		}
		if(replaceValue ==null){
			throw new BusinessException(ResHelper.getString("60130paydata","060130paydata0487")/*@res "������Ҫ�滻��ֵ"*/);
		}
		nc.vo.pub.lang.UFDouble douNum = null;
		try{
			douNum = new nc.vo.pub.lang.UFDouble(replaceValue);
		}catch(NumberFormatException e){
			throw new BusinessException(ResHelper.getString("60130paydata","060130paydata0488")/*@res "ֵ���ô������Ͳ�ƥ�䣬������������ֵ��"*/);
		}
		if (douNum == null || douNum.toString().trim().length() < 1) {
			// ����ת��Ϊ����
			throw new BusinessException(ResHelper.getString("60130paydata","060130paydata0488")/*@res "ֵ���ô������Ͳ�ƥ�䣬������������ֵ��"*/);

		}
		getReplaceDlg().closeOK();
	}
	private nc.ui.pub.beans.UIButton getbnCancel() {
		if(ivjbnCancel ==null){
			try{
				char cHotkey = 'C';
				ivjbnCancel = new nc.ui.pub.beans.UIButton();
				ivjbnCancel.setName("bnCancel");
				ivjbnCancel.setText(nc.ui.ml.NCLangRes.getInstance().getStrByID("common", "UC001-0000008")+"(" + cHotkey + ")"/*
				 * @res
				 * "ȡ��"
				 */);
				ivjbnCancel.setMnemonic(cHotkey);
				ivjbnCancel.setBounds(new Rectangle(426, 175, 75, 22));
				ivjbnCancel.setMargin(new java.awt.Insets(2, 0, 2, 0));
				ivjbnCancel.addActionListener(this);
				ivjbnCancel.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						getReplaceDlg().closeCancel();
					}

				});
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjbnCancel;
	}

	public nc.ui.pub.beans.UIComboBox getcmbEditableItem() {
		if (ivjcmbEditableItem == null) {
			try {
				ivjcmbEditableItem = new nc.ui.pub.beans.UIComboBox();
				ivjcmbEditableItem.setName("cmbEditableItem");
				ivjcmbEditableItem.setBounds(78, 24, 397, 22);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjcmbEditableItem;
	}
	public nc.ui.pub.beans.UITextArea gettaFormula() {
		if (ivjtaFormula == null) {
			try {
				ivjtaFormula = new nc.ui.pub.beans.UITextArea();
				ivjtaFormula.setName("taFormula");
				ivjtaFormula.setLineWrap(true);
				ivjtaFormula.setBounds(0, 0, 189, 120);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjtaFormula;
	}


	private void initItem() throws BusinessException{
		boolean isDay = getUIRadioDay().isSelected();
		ArrayList<String> replaceItem = new ArrayList<String>();
		if (isDay) {
			replaceItem.add("wanceforedays");
			replaceItem.add("wanafterdays");
			replaceItem.add("basedays");
		} else {
			replaceItem.add("wanceforedays");
			replaceItem.add("wanafterdays");
			replaceItem.add("wanaftermoney");
			replaceItem.add("wanbeforemoney");
			replaceItem.add("basedays");
		}
		editableName = new String[replaceItem.size()];
		editableItem = new String[replaceItem.size()];

		for (int i = 0; i < replaceItem.size() ; i++) {

			editableName[i] = getColName(replaceItem.get(i));
			editableItem[i] =  replaceItem.get(i);
		}
		getcmbEditableItem().removeAllItems();

		if (editableName != null && editableName.length > 0) {
			for (int i = 0; i < editableName.length; i++) {
				getcmbEditableItem().addItem(editableName[i]);
			}
		}
	}
	public String[] getEditableItem() {
		return editableItem;
	}
}