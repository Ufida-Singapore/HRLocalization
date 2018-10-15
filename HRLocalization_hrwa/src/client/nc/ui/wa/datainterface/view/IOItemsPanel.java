package nc.ui.wa.datainterface.view;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.ui.hr.datainterface.itf.IDisplayColumns;
import nc.ui.hr.datainterface.itf.INavigatee;
import nc.ui.hr.frame.util.BillPanelUtils;
import nc.ui.pub.beans.UIButton;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UITable;
import nc.ui.pub.beans.UITextField;
import nc.ui.pub.beans.border.UITitledBorder;
import nc.ui.pub.bill.BillCellEditor;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillEditListener;
import nc.ui.pub.bill.BillEditListener2;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillListData;
import nc.ui.pub.bill.BillListPanel;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.BillScrollPane;
import nc.ui.pub.bill.IBillItem;
import nc.ui.uif2.model.BillManageModel;
import nc.ui.wa.datainterface.model.WaDrawItemsStrategy;
import nc.vo.hr.datainterface.AggHrIntfaceVO;
import nc.vo.hr.datainterface.BooleanEnum;
import nc.vo.hr.datainterface.CaretposEnum;
import nc.vo.hr.datainterface.DataFromEnum;
import nc.vo.hr.datainterface.DataIOItemVO;
import nc.vo.hr.datainterface.FieldTypeEnum;
import nc.vo.hr.datainterface.FormatItemVO;
import nc.vo.hr.datainterface.HrIntfaceVO;
import nc.vo.hr.datainterface.IfsettopVO;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.bill.BillTempletVO;
import nc.vo.uif2.LoginContext;
import nc.vo.wa.datainterface.DataIOconstant;

import org.apache.commons.lang.StringUtils;

/**
 * ���ݽӿ� ���������ý���
 */
public class IOItemsPanel extends UIPanel implements BillEditListener, INavigatee, IDisplayColumns, ActionListener, ItemListener, BillEditListener2
{
	/**
	 * @author xuanlt on 2010-1-7
	 */
	private String nodekey = "dataio";
	private String lineKey = "dline";
	private BillManageModel model = null;

	private AggHrIntfaceVO aggVO = null;

	private String vcontentName = "";

	private DataIOItemVO[] vos = null;

	private BillListPanel billListPanel = null;

	private SignLinePanel signLinePanel = null;
	// HR���ػ����󣺶��һ����־�е����ý���
	private SignLinePanel signLinePanel2 = null;

	//	private UINavigator navigator = null;

	private UIPanel westPanel = null;

	protected ModuleItemStrategy drawItemsCreator = null;

	private UIButton ivjbnAddBankRow;
	private UIButton ivjbnDelBankRow;

	private UIButton ivjbnDown;
	private UIButton ivjbnUp;

	 /*������Դ������һ��*/
//	private static final String SINGLETYPE = "0";
	 /**������Դ������ʽ��*/
	 private static final String FORMULARTYPE = "1";
	//
	// /*��������*/
	// private static final int STRINGTYPE = 0;
	// private static final int DECIMALTYPE = 1;
	// private static final int BOOLEANTYPE = 2;
	// private static final int DATETYPE = 3;

	// private static final IConstEnum[] DATASOURCE =
	// new DefaultConstEnum[]{
	// new DefaultConstEnum(SINGLETYPE, "��һ��"),
	// new DefaultConstEnum(FORMULARTYPE, "��ʽ��")};
	// private static final IConstEnum[] FIELDTYPE =
	// new DefaultConstEnum[]{
	// new DefaultConstEnum(STRINGTYPE, "�ַ��� "),
	// new DefaultConstEnum(DECIMALTYPE, "������"),
	// new DefaultConstEnum(BOOLEANTYPE, "������ "),
	// new DefaultConstEnum(DATETYPE, "������"),
	// };

	// 0-���� 1-��ǰ 2-����
	// UIComboBox cmb = new UIComboBox(new String[] {"����","��ǰ", "����"});

	public IOItemsPanel(BillManageModel appModel, AggHrIntfaceVO aggVO)
	{
		super();
		this.model = appModel;
		this.aggVO = aggVO;
		initUI();
		initValue();
	}

	public void initUI()
	{
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		add(createItemsPanel());
		if (needSignLinePanel())
		{
			SignLinePanel spanel = getSignLinePanel();
			spanel.setModel(getModel());
			spanel.initUI();
			add(spanel);
			
			// HR���ػ����󣺶��һ����־�е��������
			SignLinePanel spanel2 = getSignLinePanel2();
			spanel2.setModel(getModel());
			spanel2.initUI();
			add(spanel2);
		}

		WaDrawItemsStrategy drawitem = new WaDrawItemsStrategy(model);

		// WaLoginContext context = (WaLoginContext)model.getContext();
		// drawitem.setContext(context);

		setDrawItemsCreator(drawitem);
		javax.swing.JScrollPane scrollPane = getBillListPanel().getBodyTabbedPane().getSelectedScrollPane();
		if (scrollPane instanceof BillScrollPane)
		{
			BillScrollPane billScrollPane = (BillScrollPane) scrollPane;
			billScrollPane.addEditListener(this);
			billScrollPane.addEditListener2(this);
		}
		// initColumnEditor();

		// //�����޸�
		// setFullTableEdit(true);
		// setFullTableEditable(getBillListPanel().getBodyBillModel(), 0);
		// setName(DataIOconstant.IOITEMSPANEL);
		// initConnection();

	}

	public CircularlyAccessibleValueObject[] getFormatsVO()
	{
		CircularlyAccessibleValueObject changedVOs[] = getBillListPanel().getBodyBillModel(DataIOconstant.HR_DATAINTFACE_B).getBodyValueChangeVOs(FormatItemVO.class.getName());
		resetVOsState(changedVOs);
		return changedVOs;
	}

	protected void resetVOsState(CircularlyAccessibleValueObject childrenVO2[])
	{
		try
		{
			for (int i = 0; i < childrenVO2.length; i++)
			{
				// û����������������������
				if (childrenVO2[i].getPrimaryKey() == null )
				{
					if(childrenVO2[i].getStatus() == nc.vo.pub.VOStatus.DELETED){
						childrenVO2[i].setStatus(nc.vo.pub.VOStatus.UNCHANGED);
					}else{
						childrenVO2[i].setStatus(nc.vo.pub.VOStatus.NEW);
					}
				}
			}
		}
		catch (Exception e)
		{
			Logger.error(e.getMessage(), e);
		}
	}

	public CircularlyAccessibleValueObject[] getIfsettopvo()
	{
		if (needSignLinePanel())
		{
			return getSignLinePanel().getIfsettopvo();
		}
		else
		{
			return new IfsettopVO[0];
		}
	}

	public UIPanel createItemsPanel()
	{
		UIPanel panel = new UIPanel();
		panel.setBorder(new UITitledBorder(ResHelper.getString("6013bnkitf","06013bnkitf0112")/*@res "��Ŀ����ʽ����"*/));
		panel.setLayout(new BorderLayout());
		billListPanel = getBillListPanel();
		BillListData bld = billListPanel.getBillListData();
		BillItem[] bodyItems = bld.getBodyItemsForTable(DataIOconstant.HR_DATAINTFACE_B);
		if (bodyItems != null)
		{
			for (int temp = 0; bodyItems != null && temp < bodyItems.length; temp++)
			{
				/**
				 * ������������
				 */
				if (bodyItems[temp].getKey().equals(DataIOconstant.VCONTENT))
				{
					BillItem tempItem = bodyItems[temp];
					this.vcontentName = tempItem.getName();
					bodyItems[temp] = new DataIOComboxBillItem();
					bodyItems[temp].setKey(tempItem.getKey());
					bodyItems[temp].setDataType(IBillItem.COMBO);
					// bodyItems[temp].setDecimalDigits(tempItem.getDecimalDigits());
					bodyItems[temp].setLength(tempItem.getLength());
					int i = tempItem.getForeground();
					bodyItems[temp].setName(tempItem.getName());
					bodyItems[temp].setForeground(i);
					bodyItems[temp].setWidth(tempItem.getWidth());
					bodyItems[temp].setEdit(true);
					bodyItems[temp].setShowOrder(tempItem.getShowOrder());
				}
				// Ĭ������ ���������� ��Ų����Ա༭
				// ��Ŀ�ָ���벹λ��Ҳ���ɱ༭
				if (bodyItems[temp].getKey().equals(DataIOconstant.ISEQ) || bodyItems[temp].getKey().equals(DataIOconstant.IFIELDTYPE) || bodyItems[temp].getKey().equals(DataIOconstant.VSEPARATOR) || bodyItems[temp].getKey().equals(DataIOconstant.VCARET))
				{
					bodyItems[temp].setEdit(false);
				}
				if (model.getContext().getNodeCode().equals(DataIOconstant.NODE_DATAIO)&&bodyItems[temp].getKey().equals(DataIOconstant.VCONTENT) ) {
					bodyItems[temp].setEdit(false);
				}

			}
			
			
			
			
			
			for (int i = 0; i < bodyItems.length; i++) {
				if(bodyItems[i].getKey().equals("icaretpos")){
					if(bodyItems[i].getComponent() instanceof UIComboBox)
						((UIComboBox) bodyItems[i].getComponent()).removeItemAt(0);
				}
			}
			
			// ��������bld��
			bld.setBodyItems(DataIOconstant.HR_DATAINTFACE_B, bodyItems);
			
			
		}

		billListPanel.setListData(bld);
		billListPanel.addBodyEditListener(this);

		billListPanel.getUISplitPane().getLeftComponent().setVisible(false);
		panel.add(billListPanel);
		if (!model.getContext().getNodeCode().equals(DataIOconstant.NODE_DATAIO)) {
			panel.add(getWestPanel(), BorderLayout.EAST);
		}
		return panel;
	}

	public BillListPanel getBillListPanel()
	{
		if (billListPanel == null)
		{
			billListPanel = new BillListPanel();

			billListPanel.setBillType(model.getContext().getNodeCode());
			billListPanel.setOperator(model.getContext().getPk_loginUser());
			billListPanel.setCorp(model.getContext().getPk_group());
			BillTempletVO template = billListPanel.getDefaultTemplet(billListPanel.getBillType(), null, billListPanel.getOperator(), billListPanel.getCorp(), getNodekey(), null);

			if (template == null)
			{
				Logger.error("û���ҵ�nodekey��" + nodekey + "��Ӧ�Ŀ�Ƭģ��");
				throw new IllegalArgumentException(ResHelper.getString("6013bnkitf","06013bnkitf0113")/*@res "û���ҵ����õĵ���ģ����Ϣ"*/);
			}

			billListPanel.setListData(new BillListData(template));

			billListPanel.setEnabled(true);
			billListPanel.setBorder(null);
			billListPanel.getHeadTable().setBorder(null);

			// ����Ӧ�Զ����м����¼�
			billListPanel.getParentListPanel().setAutoAddLine(false);

			// �͵���ģ��ͬ������
			billListPanel.getHeadBillModel().addSortRelaObjectListener(model);

			billListPanel.getHeadTable().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			// billlistPanel.set

		}
		//20151223 shenliangc NCdp205564067 н��δ�����Ҽ��˵��ͱ���лس����еĹ��ܵĽڵ�
		BillPanelUtils.disabledRightMenuAndAutoAddLine(billListPanel);
		return billListPanel;

	}

	public void initValue()
	{
		if (getAggVO() == null)
		{
			return;
		}

		getBillListPanel().setHeaderValueVO(new CircularlyAccessibleValueObject[]
				{ getAggVO().getParentVO() });

		// getBilllistPanel().getHeadBillModel().addLine();
		// getBilllistPanel().getHeadBillModel().setBodyRowObjectByMetaData(getAggVO().getParentVO(),
		// 0);

		// ������ʾ��������
		// getBillListPanel().getHeadBillModel().loadLoadRelationItemValue();

		getBillListPanel().setBodyValueVO(getAggVO().getTableVO(DataIOconstant.HR_DATAINTFACE_B));
		getBillListPanel().getBodyBillModel().updateValue();

		convertDrawItem();

		// ����ͳһ����Ŀ��ͳһ����Ŀ�ָ����ͳһ�Ĳ�λ���������ͳһ�����һ���������ݽӿ����������ܽ��
		setUniform();
		setColumEnable();

		if (needSignLinePanel())
		{
			getSignLinePanel().setAggVO(getAggVO());
			getSignLinePanel().initValue();
		}
	}

	public void setDrawItemsCreator(ModuleItemStrategy itemsCreator)
	{
		this.drawItemsCreator = itemsCreator;
		this.vos = itemsCreator.getCorrespondingItems();
		BillPanelUtils.initComboBox(getBillListPanel(), IBillItem.BODY, DataIOconstant.HR_DATAINTFACE_B, DataIOconstant.VCONTENT, this.vos, Boolean.FALSE);
	}

	private void convertDrawItem()
	{
		// �õ��������������key�����ݿ��Եõ���Ӧ��,�õ�����Ӧ����
		UITable table = getBillListPanel().getBodyTable();
		DefaultTableColumnModel model = (DefaultTableColumnModel) table.getColumnModel();

		// �õ�vcontent����Ӧ����
		int column = model.getColumnIndex(this.vcontentName);
		for (int index = 0; index < table.getRowCount(); index++)
		{
			Object value = table.getValueAt(index, column);
			table.setValueAt(getDataioitem(value), index, column);
		}
	}

	protected Object getDataioitem(Object key)
	{
		for (int index = 0; vos != null && index < vos.length; index++)
		{
			if (vos[index] != null && vos[index].getPrimaryKey() != null)
			{
				if (vos[index].getPrimaryKey().equals(key))
				{
					return vos[index];
				}
			}
		}
		return key;
	}

	private void setUniform()
	{
		UITable table = getBillListPanel().getBodyTable();
		HrIntfaceVO vo = getHeaderVO();
		FormatItemVO[] formatitemvos = (FormatItemVO[]) getAggVO().getTableVO(DataIOconstant.HR_DATAINTFACE_B);
 
		boolean isEditSepa = vo.getIifseparator().equals(BooleanEnum.YES.value());
		boolean isEditCaret = vo.getIifcaret().equals(BooleanEnum.YES.value());
		LoginContext context = this.model.getContext();
		for (int index = 0; index < table.getRowCount(); index++)
		{
			/* ����ÿһ�ж�Ϊ�޸ı�ʶ */
			getBillListPanel().getBodyBillModel(DataIOconstant.HR_DATAINTFACE_B).setRowState(index, BillModel.MODIFICATION);

			if (isEditSepa)
			{
				// ʹ����Ŀ�ָ��
//				setBodyValueAt(DataIOconstant.VSEPARATOR,index,DataIOconstant.ITEMSEPERATOR.get(getHeaderVO().getIseparator()));//
				setBodyValueAt(DataIOconstant.VSEPARATOR, index, getHeaderVO().getIseparator());//
			}
			else
			{
				// �� ͳһʹ����Ŀ�ָ�� �����ݽӿھͲ���empty
				if (context.getNodeCode().equals(DataIOconstant.NODE_DATAIO))
				{
					setBodyValueAt(DataIOconstant.VSEPARATOR, index,formatitemvos[index].getVseparator());
				}
			}

			if (isEditCaret)
			{
				// ͳһʹ�ò�λ������λλ��ͳһ��ǰ��λ
				setBodyValueAt(DataIOconstant.ICARETPOS, index, CaretposEnum.BEFORE.value());
				// ������������ȷ����λ��

				Integer fieldType = formatitemvos[index].getIfieldtype();
				if (fieldType.intValue() == (Integer) FieldTypeEnum.DEC.value())
				{
					setBodyValueAt(DataIOconstant.VCARET, index, "0");//
				}
				else
				{
					setBodyValueAt(DataIOconstant.VCARET, index, " ");//
				}
			}
			else
			{
				// �� ͳһʹ����Ŀ�ָ�� �����ݽӿھͲ�����CARETPOS[0]��
				if (context.getNodeCode().equals(DataIOconstant.NODE_DATAIO))
				{
					// setBodyValueAt(DataIOconstant.ICARETPOS, index,
					// CaretposEnum.NO.value());

				}
			}
		}
	}

	// �����Ƿ�ͳһʹ����Ŀ�ָ������ �Ƿ�ͳһʹ�ò�λ�� �����á���Ŀ�ָ�����롰��λ�����Ƿ���Ա༭
	private void setColumEnable()
	{
		HrIntfaceVO vo = getHeaderVO();
		if (vo != null)
		{
			boolean isEditSepa = vo.getIifseparator().equals(BooleanEnum.NO.value());
			getBillListPanel().getBodyItem(DataIOconstant.VSEPARATOR).setEdit(isEditSepa);

			boolean isEditCaret = vo.getIifcaret().equals(BooleanEnum.NO.value());
			getBillListPanel().getBodyItem(FormatItemVO.VCARET).setEdit(isEditCaret);
			getBillListPanel().getBodyItem(FormatItemVO.ICARETPOS).setEdit(isEditCaret);
		}
	}

	public String getNodekey()
	{
		return nodekey;
	}

	public void setNodekey(String nodekey)
	{
		this.nodekey = nodekey;
	}

	public String getLineKey()
	{
		return lineKey;
	}

	public void setLineKey(String lineKey)
	{
		this.lineKey = lineKey;
	}

	protected UIPanel getTailPanel()
	{

		return null;
	}

	protected void initConnection()
	{

	}

	//	public UINavigator getNavigator()
	//	{
	//		return navigator;
	//	}
	//
	//	public void setNavigator(UINavigator navigator)
	//	{
	//		this.navigator = navigator;
	//	}

	private boolean needSignLinePanel()
	{
		return this.getModel().getContext().getNodeCode().equals(DataIOconstant.NODE_BANK);

	}

	public SignLinePanel getSignLinePanel()
	{
		if (signLinePanel == null)
		{
			signLinePanel = new SignLinePanel();
		}
		return signLinePanel;
	}
	
	// HR���ػ����󣺶��һ����־������
	public SignLinePanel getSignLinePanel2() 
	{
		if (signLinePanel2 == null)
		{
			signLinePanel2 = new SignLinePanel();
		}
		return signLinePanel2;
	}

	public void actionPerformed(ActionEvent e)
	{

		//
		// if(e.getSource() == getUIBnTxtPre2()){
		// //���ݽӿ��ļ����;�����һ��
		// int fielType = getHeaderVO().getIfiletype();
		// if(fielType == DataIOconstant.TXTFILE){
		// navigator.ShowPrePanel();
		// }else if(fielType == DataIOconstant.XLSFILE){
		// navigator.show(DataIOconstant.BASEINFPANEL);
		// }
		//
		// }else if (e.getSource() == getUIBnTxtCancel2()){
		// navigator.ShowCancelPanel();
		// }else if (e.getSource() == getUIBnTxtOK()){
		// try {
		// Object obj = getValueChangedData();
		// getParentUI().getDataModel().onSave(obj);
		// ((DataIOTemplateUI)getParentUI()).refreshData();
		// navigator.ShowOKPanel();
		// } catch (Exception ept) {
		// getParentUI().showErrorMessage(ept.getMessage());
		// Logger.error(ept.getMessage(),ept);
		// }
		// }

	}

	private UIPanel getWestPanel() {
		if (westPanel == null) {
			westPanel = new UIPanel();
			westPanel.setLayout(new BoxLayout(westPanel, BoxLayout.Y_AXIS));
			if (!getModel().getContext().getNodeCode().equals(DataIOconstant.NODE_DATAIO)) {
				westPanel.add(getbnAddBankRow());
				westPanel.add(getbnDelBankRow());
			}
			westPanel.add(getbnUp());
			westPanel.add(getbnDown());
		}
		return westPanel;
	}

	private nc.ui.pub.beans.UIButton getbnAddBankRow()
	{
		if (ivjbnAddBankRow == null)
		{
			try
			{
				ivjbnAddBankRow = new nc.ui.pub.beans.UIButton();
				ivjbnAddBankRow.setName("bnAddBankRow");
				ivjbnAddBankRow.setText(ResHelper.getString("common","UC001-0000012")/*@res "����"*/);

				ivjbnAddBankRow.setMargin(new java.awt.Insets(2, 0, 2, 0));
				ivjbnAddBankRow.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{

						addLine(DataIOconstant.HR_DATAINTFACE_B);

						initDefaultValue();
					}

				});

			}
			catch (java.lang.Throwable ivjExc)
			{

			}
		}
		return ivjbnAddBankRow;
	}

	private nc.ui.pub.beans.UIButton getbnDelBankRow()
	{
		if (ivjbnDelBankRow == null)
		{
			try
			{
				ivjbnDelBankRow = new nc.ui.pub.beans.UIButton();
				ivjbnDelBankRow.setName("bnDelBankRow");
				ivjbnDelBankRow.setText(ResHelper.getString("common","UC001-0000013")/*@res "ɾ��"*/);

				ivjbnDelBankRow.setMargin(new java.awt.Insets(2, 0, 2, 0));
				ivjbnDelBankRow.addActionListener(new ActionListener()
				{

					public void actionPerformed(ActionEvent e)
					{
						// getBilllistPanel()
						int row = getBillListPanel().getBodyTable().getSelectedRow();
						int rowCount = getBillListPanel().getBodyTable().getRowCount();
						if (row < 0)
						{
							return;
						}
						getBillListPanel().getBodyBillModel().delLine(new int[]
								{ row });
						// int[] rows =
						// getBodySelectedRows(DataIOconstant.WA_IFSETTOP);
						// if(rows.length>0){
						// deleteLine(DataIOconstant.WA_IFSETTOP);
						// TableUtil.setSelectedRow(getBillListPanel().getBodyTable(DataIOconstant.WA_IFSETTOP),rows[0]);
						// //row[0] �Ժ���к���֮�仯
						delLineNum(row);

						row = row - 1;
						if (row < 0 && rowCount > 1)
						{
							row = 0;
						}
						if (row >= 0)
						{
							getBillListPanel().getBodyTable().setRowSelectionInterval(row, row);
						}
					}

				});

			}
			catch (java.lang.Throwable ivjExc)
			{

			}
		}
		return ivjbnDelBankRow;
	}

	public void moveUp(String strCode, int row)
	{
		if (row > 0 && row <= getBillListPanel().getBodyBillModel(strCode).getRowCount() - 1)
		{
			BillModel billmodel = getBillListPanel().getBodyBillModel(strCode);
			billmodel.moveRow(row, row, row - 1);
			getBillListPanel().getBodyTable().getSelectionModel().setSelectionInterval(row - 1, row - 1);

			// �����к�
			billmodel.setValueAt(row, row - 1, DataIOconstant.ISEQ);
			billmodel.setValueAt(row + 1, row, DataIOconstant.ISEQ);
			// ����row-1 �� row �е�״̬

			String pk = (String) billmodel.getValueAt(row - 1, FormatItemVO.PK_DATAINTFACE_B);

			int vostatus = BillModel.MODIFICATION;
			if (StringUtils.isBlank(pk))
			{
				vostatus = BillModel.ADD;
			}
			getBillListPanel().getBodyBillModel(DataIOconstant.HR_DATAINTFACE_B).setRowState(row - 1, vostatus);

			pk = (String) billmodel.getValueAt(row, FormatItemVO.PK_DATAINTFACE_B);
			if (StringUtils.isBlank(pk))
			{
				vostatus = BillModel.ADD;
			}
			getBillListPanel().getBodyBillModel(DataIOconstant.HR_DATAINTFACE_B).setRowState(row, vostatus);

		}
	}

	public void moveDown(String strCode, int row)
	{
		if (row >= 0 && row <= getBillListPanel().getBodyBillModel(strCode).getRowCount() - 2)
		{
			BillModel billmodel = getBillListPanel().getBodyBillModel(strCode);
			billmodel.moveRow(row, row, row + 1);
			getBillListPanel().getBodyTable().getSelectionModel().setSelectionInterval(row + 1, row + 1);
			// �����к�
			billmodel.setValueAt(row + 2, row + 1, DataIOconstant.ISEQ);
			billmodel.setValueAt(row + 1, row, DataIOconstant.ISEQ);

			String pk = (String) billmodel.getValueAt(row + 1, FormatItemVO.PK_DATAINTFACE_B);

			int vostatus = BillModel.MODIFICATION;
			if (StringUtils.isBlank(pk))
			{
				vostatus = BillModel.ADD;
			}
			// ����row+1 �� row �е�״̬
			getBillListPanel().getBodyBillModel(DataIOconstant.HR_DATAINTFACE_B).setRowState(row + 1, vostatus);

			pk = (String) billmodel.getValueAt(row, FormatItemVO.PK_DATAINTFACE_B);

			vostatus = BillModel.MODIFICATION;
			if (StringUtils.isBlank(pk))
			{
				vostatus = BillModel.ADD;
			}

			getBillListPanel().getBodyBillModel(DataIOconstant.HR_DATAINTFACE_B).setRowState(row, vostatus);

		}
	}

	public void addLine(String strTableCode)
	{
		getBillListPanel().getBodyBillModel().setEnabled(true);
		BillScrollPane headPanel = getBillListPanel().getBodyScrollPane(strTableCode);
		headPanel.addLine();

		//
		// BillModel billModel =
		// getBilllistPanel().getBodyBillModel(strTableCode);
		//
		// setFullTableEditable(billModel, getBodySelectedRow(strTableCode));
	}

	/**
	 * row �Ժ���кż�һ
	 *
	 * @param row
	 */
	private void delLineNum(int row)
	{
		int count = getBillListPanel().getBodyBillModel().getRowCount();
		for (int i = row; i < count; i++)
		{
			getBillListPanel().getBodyBillModel().setValueAt(i + 1, i, DataIOconstant.ISEQ);
			// ����row+1 �� row �е�״̬
			getBillListPanel().getBodyBillModel().setRowState(i, BillModel.MODIFICATION);
		}
	}

	/**
	 * ��ʼ��ÿһ�еĹ������� ��� ������(�ַ���)�����ȣ�20����С��λ����0����������Դ����ʽ�ͣ� ���ͳһ��Ŀ�ָ������������ʼͳһ��Ŀ�ָ���
	 *
	 *���ʹ��ͳһ��λ����λλ��Ĭ�ϲ�ǰ��Ĭ�ϲ��ո� ����ʹ��ͳһ��λ���� ����
	 */
	private void initDefaultValue()
	{
		int row = getBillListPanel().getBodyTable(DataIOconstant.HR_DATAINTFACE_B).getSelectedRow();
		setBodyValueAt(DataIOconstant.ISEQ, row, row + 1);
		setBodyValueAt(DataIOconstant.IFIELDTYPE, row, FieldTypeEnum.STR.value());//
		setBodyValueAt(DataIOconstant.IFLDWIDTH, row, 20);
		setBodyValueAt(DataIOconstant.IFLDDECIMAL, row, 0);
		// setBodyValueAt(DataIOconstant.ICARETPOS, row,
		// CaretposEnum.NO.value());//
		setBodyValueAt(DataIOconstant.ISOURCETYPE, row, DataFromEnum.FORMULAR.value());//
		HrIntfaceVO vo = getHeaderVO();

		if (vo != null)
		{
			// ͳһʹ����Ŀ�ָ���
			if (vo.getIifseparator().intValue() == 1)
			{
				setBodyValueAt(DataIOconstant.VSEPARATOR, row, vo.getIseparator());//
			}
			// ͳһʹ�ò�λ��
			if (vo.getIifcaret().intValue() == 1)
			{
				setBodyValueAt(DataIOconstant.ICARETPOS, row, CaretposEnum.BEFORE.value());//
				setBodyValueAt(DataIOconstant.VCARET, row, " ");//
			}
		}
	}

	private void setBodyValueAt(String strItemKey, int iRowIndex, Object objValue)
	{
		// �õ�Billmodel
		BillModel billModel = getBillListPanel().getBodyBillModel();
		// �趨ֵ
		billModel.setValueAt(objValue, iRowIndex, strItemKey);
	}

	private Object getBodyValueAt(String strItemKey, int iRowIndex)
	{
		// �õ�Billmodel
		BillModel billModel = getBillListPanel().getBodyBillModel();
		// �趨ֵ
		return billModel.getValueAt(iRowIndex, strItemKey);
	}

	protected HrIntfaceVO getHeaderVO()
	{
		if (getAggVO() == null)
		{
			return null;
		}
		return (HrIntfaceVO) getAggVO().getParentVO();
	}

	/**
	 * ���� bnDown ����ֵ��
	 *
	 * @return nc.ui.pub.beans.UIButton
	 */
	/* ���棺�˷������������ɡ� */
	private nc.ui.pub.beans.UIButton getbnDown()
	{
		if (ivjbnDown == null)
		{
			try
			{
				ivjbnDown = new nc.ui.pub.beans.UIButton();
				ivjbnDown.setName("bnDown");
				ivjbnDown.setText(ResHelper.getString("6013bnkitf","06013bnkitf0114")/*@res "����"*/);

				ivjbnDown.addActionListener(new ActionListener()
				{

					public void actionPerformed(ActionEvent e)
					{
						int row = getBillListPanel().getBodyTable().getSelectedRow();
						if (row < 0)
						{
							return;
						}
						moveDown(DataIOconstant.HR_DATAINTFACE_B, row);
						// moveDown(
						// getBodySelectedRow(DataIOconstant.HR_DATAINTFACE_B));
					}

				});

			}
			catch (java.lang.Throwable ivjExc)
			{

			}
		}
		return ivjbnDown;
	}

	private nc.ui.pub.beans.UIButton getbnUp()
	{
		if (ivjbnUp == null)
		{
			try
			{
				ivjbnUp = new nc.ui.pub.beans.UIButton();
				ivjbnUp.setName("bnUp");
				ivjbnUp.setText(ResHelper.getString("6013bnkitf","06013bnkitf0115")/*@res "����"*/);
				ivjbnUp.addActionListener(this);
				ivjbnUp.addActionListener(new ActionListener()
				{

					public void actionPerformed(ActionEvent e)
					{
						int row = getBillListPanel().getBodyTable().getSelectedRow();
						if (row < 0)
						{
							return;
						}
						moveUp(DataIOconstant.HR_DATAINTFACE_B, row);
					}

				});

			}
			catch (java.lang.Throwable ivjExc)
			{

			}
		}
		return ivjbnUp;
	}

	public BillManageModel getModel()
	{
		return model;
	}

	public void setModel(BillManageModel model)
	{
		this.model = model;

	}

	public ModuleItemStrategy getDrawItemsCreator()
	{
		return drawItemsCreator;
	}

	// public void setDrawItemsCreator(ModuleItemStrategy itemsCreator) {
	// this.drawItemsCreator = itemsCreator;
	// this.drawItemsCreator.getCorrespondingItems();
	//
	// // //name
	// // ConstEnumFactory<T> factory= new ConstEnumFactory();
	//
	// // initComboBox(IBillItem.BODY, DataIOconstant.VCONTENT,
	// this.drawItemsCreator.getCorrespondingItems(), Boolean.FALSE);
	// }

	/**
	 * �������ݸı�ʱ�������л���֮�ı� ���������͡����ȡ�С��λ���� ���ʹ��ͳһ��λ������λ��Ҳ����֮�ı�(�����Ͳ��㡢�������Ͳ��ո�)��
	 *
	 * @return
	 */
	@Override
	public void afterEdit(BillEditEvent billEditEvent)
	{

		if (billEditEvent.getKey().equals(DataIOconstant.ISOURCETYPE))
		{

			// clearValue();
			Integer dataFrom = (Integer) billEditEvent.getValue();
			UITable table = getBillListPanel().getBodyTable();
			// getBilllistPanel().getBodyItem("").setItemEditor(itemEditor)
			TableColumn tablecolumn = table.getColumnModel().getColumn(1);
			// TableColumn tablecolumn =
			// table.getColumn(DataIOconstant.VCONTENT); //hardcode
			if (dataFrom.equals(DataFromEnum.FORMULAR.value()))
			{
				// Ϊѡ����Ԫ�����ñ༭��
				UIComboBox box = new UIComboBox();
				box.addItems(this.vos);
				tablecolumn.setCellEditor(new BillCellEditor(box));
				getBillListPanel().getBodyItem(DataIOconstant.IFIELDTYPE).setEdit(false);
			}
			else
			{
				tablecolumn.setCellEditor(new BillCellEditor(new UITextField()));
				// ���ñ༭��
				getBillListPanel().getBodyItem(DataIOconstant.IFIELDTYPE).setEdit(true);
			}

			setBodyValueAt(DataIOconstant.VFIELDNAME, billEditEvent.getRow(), "");
			setBodyValueAt(DataIOconstant.VCONTENT, billEditEvent.getRow(), "");
		}

		Object obj = billEditEvent.getValue();
		if (obj instanceof DataIOItemVO)
		{
			DataIOItemVO vo = (DataIOItemVO) obj;
			setBodyValueAt(DataIOconstant.VFIELDNAME, billEditEvent.getRow(), vo.getVname());

			// setBodyValueAt(DataIOconstant.VCONTENT,billEditEvent.getRow(),vo.getPrimaryKey());

			setBodyValueAt(DataIOconstant.IFIELDTYPE, billEditEvent.getRow(), vo.getIitemtype());
			setBodyValueAt(DataIOconstant.IFLDWIDTH, billEditEvent.getRow(), vo.getIfldwidth());
			setBodyValueAt(DataIOconstant.IFLDDECIMAL, billEditEvent.getRow(), vo.getIflddecimal());

			HrIntfaceVO headerVO = getHeaderVO();
			// ͳһʹ�ò�λ��
			if (headerVO.getIifcaret().intValue() == 1)
			{
				String caret = " ";
				if (vo.getIitemtype() != 0)
				{// ��������
					caret = "0";
				}
				setBodyValueAt(DataIOconstant.ICARETPOS, billEditEvent.getRow(), CaretposEnum.BEFORE.value());//
				setBodyValueAt(DataIOconstant.VCARET, billEditEvent.getRow(), caret);//
			}
		}

	}




	public AggHrIntfaceVO getAggVO()
	{
		return aggVO;
	}

	public void setAggVO(AggHrIntfaceVO aggVO)
	{
		this.aggVO = aggVO;
	}

	/**
	 * @author xuanlt on 2009-12-30
	 * @see nc.ui.pub.bill.BillEditListener#bodyRowChange(nc.ui.pub.bill.BillEditEvent)
	 */
	@Override
	public void bodyRowChange(BillEditEvent billEditEvent)
	{}

	public boolean beforeEdit(BillEditEvent billEditEvent)
	{
		if (getModel().getContext().getNodeCode().equals(DataIOconstant.NODE_BANK))
		{
			UITable table = getBillListPanel().getBodyTable();
			TableColumn tablecolumn = table.getColumn(vcontentName);
			int currentRow = billEditEvent.getRow();
			
			//NCdp205527341  ֱ�ӱȽ��ַ���ֵ���ܻ���ֶ�������,��Ϊ�Ƚ�����.   0Ϊ��һ��,1Ϊ��ʽ��   ---lizt
//			Object isourcetype = getBillListPanel().getBodyBillModel().getValueAt(currentRow, DataIOconstant.ISOURCETYPE);
//			if (isourcetype.toString().trim().equals(ResHelper.getString("6013bnkitf","06013bnkitf0116")/*@res "��ʽ��"*/))
			
			Map<String, Object>[] valueMap = getBillListPanel().getBodyBillModel().getBodyChangeValueByMetaData();
			Object isourcetype = valueMap[currentRow].get(DataIOconstant.ISOURCETYPE);
			if(isourcetype.toString().trim().equals(FORMULARTYPE))
			{
				UIComboBox box = new UIComboBox();
				box.addItems(vos);
				tablecolumn.setCellEditor(new BillCellEditor(box));
				getBillListPanel().getBodyItem(DataIOconstant.IFIELDTYPE).setEdit(false);
			}
			else
			{
				tablecolumn.setCellEditor(new BillCellEditor(new UITextField()));
				getBillListPanel().getBodyItem(DataIOconstant.IFIELDTYPE).setEdit(true);
			}
		}
		return true;
	}

	/**
	 * @author xuanlt on 2010-1-8
	 * @see nc.ui.hr.datainterface.itf.IDisplayColumns#getBillItems()
	 */
	@Override
	public String[] getBillItems()
	{
		return null;
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		// TODO Auto-generated method stub

	}

}