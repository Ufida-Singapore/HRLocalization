package nc.bs.er.exp.cmd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nc.bs.er.exp.cjk.ctrl.CjkMainViewCtrl;
import nc.bs.er.exp.util.ExpCommonUtil;
import nc.bs.er.exp.util.ExpDatasets2AggVOSerializer;
import nc.bs.er.exp.util.ExpReimruleUtil;
import nc.bs.er.exp.util.ExpUtil;
import nc.bs.er.exp.util.YerBxUIControlUtil;
import nc.bs.er.exp.util.YerMultiVersionUtil;
import nc.bs.er.util.YerUtil;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Log;
import nc.bs.logging.Logger;
import nc.bs.pub.formulaparse.FormulaParse;
import nc.itf.bd.psnbankacc.IPsnBankaccPubService;
import nc.itf.bd.psnbankacc.IPsnBankaccQueryService;
import nc.itf.fi.pub.Currency;
import nc.pubitf.uapbd.ICustomerPubService;
import nc.pubitf.uapbd.ISupplierPubService;
import nc.uap.lfw.core.AppInteractionUtil;
import nc.uap.lfw.core.LfwRuntimeEnvironment;
import nc.uap.lfw.core.cmd.base.UifCommand;
import nc.uap.lfw.core.common.StringDataTypeConst;
import nc.uap.lfw.core.comp.GridColumn;
import nc.uap.lfw.core.comp.GridComp;
import nc.uap.lfw.core.comp.IGridColumn;
import nc.uap.lfw.core.comp.WebComponent;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Field;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.data.RowData;
import nc.uap.lfw.core.event.DatasetCellEvent;
import nc.uap.lfw.core.event.DatasetEvent;
import nc.uap.lfw.core.exception.LfwRuntimeException;
import nc.uap.lfw.core.formular.LfwFormulaParser;
import nc.uap.lfw.core.log.LfwLogger;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.page.LfwWindow;
import nc.uap.lfw.core.serializer.impl.Dataset2SuperVOSerializer;
import nc.uap.lfw.core.serializer.impl.Datasets2AggVOSerializer;
import nc.uap.wfm.constant.WfmConstants;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BodyEditVO;
import nc.vo.arap.bx.util.ControlBodyEditVO;
import nc.vo.bd.bankaccount.BankAccSubVO;
import nc.vo.bd.bankaccount.BankAccbasVO;
import nc.vo.bd.psnbankacc.PsnBankaccUnionVO;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BXHeaderVO;
import nc.vo.ep.bx.BXVO;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.er.exp.IExpConst;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.ValidationException;
import nc.vo.pub.formulaset.VarryVO;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import uap.lfw.core.ml.LfwResBundle;
import uap.web.bd.pub.AppUtil;


/**
 * 
 * @author kongxl 2012-03-02
 */
public class ExpBodyValueChangeCmd extends UifCommand {
	
	
	private String masterDsID;
	private DatasetEvent datasetEvent;

	public ExpBodyValueChangeCmd(String masterDsID, DatasetEvent datasetCellEvent) {
		super();
		this.masterDsID = masterDsID;
		this.datasetEvent = datasetCellEvent;
	}


	@Override
	public void execute() {
		
		String hasBusitemGrid = (String)AppUtil.getAppAttr("ExpHasBusitemGrid");
		if ("N".equals(hasBusitemGrid)) {//�ޱ���ҵ��grid
			return;
		}
		DatasetCellEvent datasetCellEvent = (DatasetCellEvent)datasetEvent;
		if (datasetCellEvent.getNewValue()==null && datasetCellEvent.getOldValue() == null) {
			return;
		}
		if (datasetCellEvent.getNewValue()!=null && datasetCellEvent.getNewValue().equals(datasetCellEvent.getOldValue())){
			return;
		}
		Dataset busitemDs = datasetCellEvent.getSource();
		Logger.error("**********ExpBodyValueChangeCmd*******BEGIN****");
//		//��ԭ�е��ݻ��������������������ԭ���ݲ�һ�£��ᵼ�¸��¼��ٷ�����������ж�
//		if (busitemDs.getCurrentRowData()==null || busitemDs.getCurrentRowData().getRows() ==null || busitemDs.getCurrentRowData().getRows().length==0) {
//			return;
//		}
		LfwWindow jkbxWindow = AppLifeCycleContext.current().getWindowContext().getWindow();
		LfwView widget =  jkbxWindow.getView("main");
		Dataset masterDs = widget.getViewModels().getDataset(masterDsID);
		String pkItem = (String)masterDs.getSelectedRow().getValue(masterDs.nameToIndex(JKBXHeaderVO.PK_ITEM));
		if ((IExpConst.BX_BUSITEM_DS_ID.equals(busitemDs.getId()) || IExpConst.JK_BUSITEM_DS_ID.equals(busitemDs.getId()))&& pkItem!=null && !"".equals(pkItem)) {
			Map<String,String> formularMap = (HashMap<String,String>)LfwRuntimeEnvironment.getWebContext().getRequest().getSession().getAttribute("yer_formularMap");
			Set<String> keySet = formularMap.keySet();
			for (String key : keySet) {
				int keyIndex = busitemDs.nameToIndex(key);
				if(datasetCellEvent.getColIndex() == keyIndex) {
					
					YerUtil.modifyField(busitemDs, "EditFormular", key, formularMap.get(key));
//					busitemDs.getFieldSet().getField(key).setEditFormular(formularMap.get(key)); //������������Ĺ�ʽ��������ȷ����ʽ������
				}
			}
			//���ӶԹ�ʽ�Ĵ���ȷ���ӱ������Ļ�������������ʱ��һ�ε������ʱ���Խ��й�ʽ���㣬���򲻻���м���
			Field currentField = busitemDs.getFieldSet().getField(datasetCellEvent.getColIndex());
//			if (currentField.getEditFormular() != null && currentField.getEditFormular().indexOf("amount->")!=-1) {
			if (currentField.getEditFormular() != null ) {
				processEditorFormular(busitemDs);
			}
		}
		//add by weiningc 20191222 start
		String djlxbm = (String)AppUtil.getAppAttr("default_djlxbm");
        //if("264X-Cxx-01".equals(djlxbm))
        //{
    	Logger.error("**********ExpBodyValueChangeCmd*******BEGIN****�������ͱ��룺***"+djlxbm);
        int currtypeIndex = busitemDs.nameToIndex("defitem47");
        if(datasetCellEvent.getColIndex() == currtypeIndex)
            try
            {
            	int index = datasetCellEvent.getRowIndex();
                Row row = busitemDs.getCurrentRowData().getRow(index);
                String pk_org = (String)masterDs.getSelectedRow().getValue(masterDs.nameToIndex("pk_org"));
                String pk_currtype = (String)row.getValue(currtypeIndex);
                UFDate date = (UFDate)masterDs.getSelectedRow().getValue(masterDs.nameToIndex("djrq"));
                UFDouble rate = Currency.getRate(pk_org, pk_currtype, date);
                Logger.error("**********ExpBodyValueChangeCmd****��֯��****"+pk_org+"***���֣�***"+pk_currtype+"***���ڣ�***"+date);
                Logger.error("**********ExpBodyValueChangeCmd****���ʣ�****"+rate);
                int hlPrecision = Currency.getRateDigit(pk_org, pk_currtype, Currency.getOrgLocalCurrPK(pk_org));
                ExpCommonUtil.setDsPrecision(busitemDs, new String[] {
                    "defitem46"
                }, hlPrecision);
                row.setValue(busitemDs.nameToIndex("defitem46"), rate);
                Logger.error("**********ExpBodyValueChangeCmd*******END*******");
            }
            catch(BusinessException e)
            {
                Logger.error(e.getMessage(), e);
                throw new LfwRuntimeException(e);
            }
        //}
        
        int amountIndex = busitemDs.nameToIndex("amount");
        if(datasetCellEvent.getColIndex() == amountIndex)
        {
            setHeadTotalValue();
            int rowIndex = datasetCellEvent.getRowIndex();
            Row row = busitemDs.getCurrentRowData().getRow(rowIndex);
            UFDouble amount = (UFDouble)row.getValue(amountIndex);
            row.setValue(busitemDs.nameToIndex("ybje"), amount);
            modifyFinValues(busitemDs.nameToIndex("ybje"), rowIndex, busitemDs, row);
            try
            {
                doContract(busitemDs, row);
            }
            catch(BusinessException e1)
            {
                Logger.error(e1.getMessage(), e1);
                throw new LfwRuntimeException(e1);
            }
        }
        if(datasetCellEvent.getColIndex() == busitemDs.nameToIndex("ybje"))
        {
            setHeadYbjeValue();
            int rowIndex = datasetCellEvent.getRowIndex();
            Row row = busitemDs.getCurrentRowData().getRow(rowIndex);
            modifyFinValues(busitemDs.nameToIndex("ybje"), rowIndex, busitemDs, row);
            try
            {
                doContract(busitemDs, row);
            }
            catch(BusinessException e1)
            {
                Logger.error(e1.getMessage(), e1);
                throw new LfwRuntimeException(e1);
            }
        }
		//end
		//�������仯
		dealBusitemChange(datasetCellEvent, masterDs,busitemDs,widget);
	}
	/**
	 * �������仯
	 * @param datasetCellEvent
	 * @param masterDs
	 * @param busitemDs
	 * @param widget
	 */
	private void dealBusitemChange(DatasetCellEvent datasetCellEvent,Dataset masterDs ,Dataset busitemDs, LfwView widget) {
		/**AMOUNT�仯**/
		//���ñ������ֵ
		int index = datasetCellEvent.getRowIndex();
		if(index >= busitemDs.getRowCount()){
			return;
		}
		
		Field field = busitemDs.getFieldSet().getField(datasetCellEvent.getColIndex());
		if(field != null && field.getDataType() != null && "UFDouble".equals(field.getDataType())){
			//���ֻ�ǽ���ֶεľ��ȱ仯��ֵû�б仯���򲻴�����¼�
			UFDouble newValue = new UFDouble(datasetCellEvent.getNewValue() == null ? "" : datasetCellEvent.getNewValue().toString());
			UFDouble oldValue = new UFDouble(datasetCellEvent.getOldValue() == null ? "" : datasetCellEvent.getOldValue().toString());
			if(newValue.equals(oldValue)){
				return;
			}
		}
		
		Row row = busitemDs.getCurrentRowData().getRow(index);
		
		int amountIndex = busitemDs.nameToIndex(BXBusItemVO.AMOUNT);
		if (datasetCellEvent.getColIndex() == amountIndex) { // �ӱ������仯�������������ֵ
			dealReimRuleTips(index);
			//���ñ�ͷTotalֵ
			setHeadTotalValue();
			
			UFDouble amount = (UFDouble)row.getValue(amountIndex);
			row.setValue(busitemDs.nameToIndex(BXBusItemVO.YBJE), amount);
			modifyFinValues(busitemDs.nameToIndex(BXBusItemVO.YBJE), index, busitemDs, row);
			try {  //����ʱ����дҵ���н��ʱ��������ڣ����¼���������Ȳ���
				doContract(busitemDs, row);
			} catch (BusinessException e1) {
				Logger.error(e1.getMessage(), e1);
				throw new LfwRuntimeException(e1);
			}
		}
		//�ӱ�ԭ�ҽ��仯
		if (datasetCellEvent.getColIndex() == busitemDs.nameToIndex(BXBusItemVO.YBJE)) { // �ӱ������仯�������������ֵ
			//���ñ�ͷYbjeֵ
			setHeadYbjeValue();
			modifyFinValues(busitemDs.nameToIndex(BXBusItemVO.YBJE), index, busitemDs, row);
			try {  //����ʱ����дҵ���н��ʱ��������ڣ����¼���������Ȳ���
				doContract(busitemDs, row);
			} catch (BusinessException e1) {
				Logger.error(e1.getMessage(), e1);
				throw new LfwRuntimeException(e1);
			}
		}
		
		/**PK_REIMTYPE�仯**/
//		int reimtypeIndex = busitemDs.nameToIndex(BXBusItemVO.PK_REIMTYPE);
//		if (datasetCellEvent.getColIndex() == reimtypeIndex) { //�������ͱ仯  ����������׼
//			//��ͷ������׼
//			ExpReimruleUtil.processReimRule(masterDs, ExpUtil.getAllDetailDss(widget,  masterDs.getId()), masterDs.getSelectedRow());
//			//���屨����׼
//			ExpReimruleUtil.processReimRule(busitemDs, row, masterDsID);
//		}
			//kkk������屨����׼
		String bxdj= (String) AppUtil.getAppAttr(IExpConst.EXP_REIMRULE_FLAG);
		if (bxdj!=null && "Y".equals(bxdj)) {
			Map<String,String> ReimRuleMap = (Map<String, String>) LfwRuntimeEnvironment.getWebContext().getRequest().getSession().getAttribute("yer_ReimRuleBusitemMap");
			if(ReimRuleMap!=null  && ReimRuleMap.size()>0) {
				Set<String> keySet = ReimRuleMap.keySet();
				for (String key : keySet) {
					int itemIndex = busitemDs.nameToIndex(key);
					if (datasetCellEvent.getColIndex() == itemIndex) {
						//��ͷ������׼
						ExpReimruleUtil.processReimRule(masterDs, ExpUtil.getAllDetailDss(widget,  masterDs.getId()), masterDs.getSelectedRow());
						//���屨����׼
						ExpReimruleUtil.processReimRule(busitemDs, row, masterDsID);
						dealReimRuleTips(index);
						
					}
				}
			}
//			if (IExpConst.BXZB_DS_ID.equals(masterDsID)) {  //���������б�����׼�Ĵ���
//			}
		}
//		}
		//��Ŀ�����仯����Ŀ�������
		int jobid = busitemDs.nameToIndex(BXBusItemVO.JOBID);
		if (datasetCellEvent.getColIndex() == jobid) { 
			row.setValue(busitemDs.nameToIndex(BXBusItemVO.PROJECTTASK), null);
		}
		
		//�������İ汾
		if (datasetCellEvent.getColIndex() == busitemDs.nameToIndex(BXBusItemVO.PK_PCORG_V)) { 
			//������������
			String pk_pcorg_v = (String)datasetCellEvent.getNewValue();
			String pk_pcorg = YerMultiVersionUtil.getBillHeadFinanceOrg(BXBusItemVO.PK_PCORG_V, pk_pcorg_v, widget, masterDsID);
			ExpUtil.setRowValue(row, busitemDs, BXBusItemVO.PK_PCORG, pk_pcorg);
			//��պ���Ҫ��
			ExpUtil.setRowValue(row, busitemDs, BXBusItemVO.PK_CHECKELE, null);
		}
		
		//��������
		if (datasetCellEvent.getColIndex() == busitemDs.nameToIndex(BXBusItemVO.PK_PCORG)) { 
			//�����������İ汾
			String pk_pcorg = (String)datasetCellEvent.getNewValue();
			UFDate date = (UFDate) masterDs.getSelectedRow().getValue(masterDs.nameToIndex(BXHeaderVO.DJRQ));
			if(date==null||StringUtil.isEmpty(date.toString())){
				date = ExpUtil.getBusiDate();
			}
			String pk_org = (String) masterDs.getSelectedRow().getValue(masterDs.nameToIndex(BXHeaderVO.PK_ORG));
			String pk_pcorg_v_value = YerMultiVersionUtil.getBillHeadFinanceOrgVersion(JKBXHeaderVO.PK_PCORG_V, pk_pcorg, date, masterDs, pk_org, widget);
			ExpUtil.setRowValue(row, busitemDs, BXBusItemVO.PK_PCORG_V, pk_pcorg_v_value);
			String hesdPcorg = (String) masterDs.getSelectedRow().getValue(masterDs.nameToIndex(BXHeaderVO.PK_PCORG));
			//��ͷ�������������һ��һ��
			if (pk_pcorg.equals(hesdPcorg)) {
				String hesdpk_resacostcenter = (String) masterDs.getSelectedRow().getValue(masterDs.nameToIndex(BXHeaderVO.PK_RESACOSTCENTER));
				ExpUtil.setRowValue(row, busitemDs, BXBusItemVO.PK_RESACOSTCENTER, hesdpk_resacostcenter);
			} else {
				//��ճɱ�����
				ExpUtil.setRowValue(row, busitemDs, BXBusItemVO.PK_RESACOSTCENTER, null);
			}
		}
		if ("bxzb".equals(masterDs.getId())) {
			//�����˵�λ
			if (datasetCellEvent.getColIndex() == busitemDs.nameToIndex(BXBusItemVO.DWBM)) {
				ExpUtil.setRowValue(row, busitemDs, BXBusItemVO.DEPTID, null);
				ExpUtil.setRowValue(row, busitemDs, BXBusItemVO.JKBXR, null);
				ExpUtil.setRowValue(row, busitemDs, BXBusItemVO.SKYHZH, null);
				ExpUtil.setRowValue(row, busitemDs, BXBusItemVO.RECEIVER, null);
			}
			//�����˲���
			if (datasetCellEvent.getColIndex() == busitemDs.nameToIndex(BXBusItemVO.DEPTID)) {
				String deptid = (String)datasetCellEvent.getNewValue();
				String jkbxr = row.getString(busitemDs.nameToIndex(BXBusItemVO.JKBXR));
				if (jkbxr!=null && !"".equals(jkbxr)) {
					String[] values = ExpUtil.getPsnDocInfoById(jkbxr);
					if(values!=null&&values.length>0){
						if (!values[1].equals(deptid)){ //��ǰ���źͱ������������Ų�ͬ����ձ�����,����ѡ���������ű�����ʱ�����貿�ţ��ֻ���������
							ExpUtil.setRowValue(row, busitemDs, BXBusItemVO.JKBXR, null);
						}
					}
				}
			}
			//������
//			if (datasetCellEvent.getColIndex() == busitemDs.nameToIndex(BXBusItemVO.JKBXR)) {
//				String jkbxr = (String)datasetCellEvent.getNewValue();
//				if (jkbxr!= null && !"".equals(jkbxr)) {
//					String[] values = ExpUtil.getPsnDocInfoById(jkbxr);
//					if(values!=null&&values.length>0){
//						ExpUtil.setRowValue(row, busitemDs, BXBusItemVO.DEPTID, values[1]);
//						ExpUtil.setRowValue(row, busitemDs, BXBusItemVO.DWBM, values[2]);
//					}
//					ExpUtil.setRowValue(row, busitemDs, BXBusItemVO.RECEIVER, jkbxr);
//				}
//			}
			//�տ����
			if (datasetCellEvent.getColIndex() == busitemDs.nameToIndex(BXBusItemVO.PAYTARGET)) {
				int paytarget = (Integer) row.getValue(busitemDs.nameToIndex(BXBusItemVO.PAYTARGET));
				if (paytarget == 0) {
					ExpUtil.setRowValue(row, busitemDs, BXBusItemVO.HBBM, null);
					ExpUtil.setRowValue(row, busitemDs, BXBusItemVO.CUSTOMER, null);
					ExpUtil.setRowValue(row, busitemDs, BXBusItemVO.CUSTACCOUNT, null);
					ExpUtil.setRowValue(row, busitemDs, BXBusItemVO.FREECUST, null);
					ExpUtil.setRowValue(row, busitemDs, "freecustacc", null);
				} else if(paytarget == 1){
					ExpUtil.setRowValue(row, busitemDs, BXBusItemVO.RECEIVER, null);
					ExpUtil.setRowValue(row, busitemDs, BXBusItemVO.SKYHZH, null);
					ExpUtil.setRowValue(row, busitemDs, BXBusItemVO.CUSTOMER, null);
					ExpUtil.setRowValue(row, busitemDs, BXBusItemVO.CUSTACCOUNT, null);
				} else if(paytarget == 2){
					ExpUtil.setRowValue(row, busitemDs, BXBusItemVO.HBBM, null);
					ExpUtil.setRowValue(row, busitemDs, BXBusItemVO.RECEIVER, null);
					ExpUtil.setRowValue(row, busitemDs, BXBusItemVO.SKYHZH, null);
					ExpUtil.setRowValue(row, busitemDs, BXBusItemVO.CUSTACCOUNT, null);
				}
			}
			//�տ���
			if (datasetCellEvent.getColIndex() == busitemDs.nameToIndex(BXBusItemVO.RECEIVER)) {
				int paytarget = (Integer) row.getValue(busitemDs.nameToIndex(BXBusItemVO.PAYTARGET));
				if(paytarget == 0){
					ExpUtil.setRowValue(row, busitemDs, BXBusItemVO.HBBM, null);
					ExpUtil.setRowValue(row, busitemDs, BXBusItemVO.CUSTOMER, null);
					ExpUtil.setRowValue(row, busitemDs, BXBusItemVO.CUSTACCOUNT, null);
					String tSkyhzhAcc = getSkyhzhAcc(row, busitemDs);
					row.setValue(busitemDs.nameToIndex(BXBusItemVO.SKYHZH), tSkyhzhAcc);
				}else if(paytarget == 3){
					String tSkyhzhAcc = getSkyhzhAcc(row, busitemDs);
					row.setValue(busitemDs.nameToIndex(BXBusItemVO.SKYHZH), tSkyhzhAcc);
				}else {
					ExpUtil.setRowValue(row, busitemDs, BXBusItemVO.RECEIVER, null);
					ExpUtil.setRowValue(row, busitemDs, BXBusItemVO.SKYHZH, null);
				}
			}
			//��Ӧ��
			if (datasetCellEvent.getColIndex() == busitemDs.nameToIndex(BXBusItemVO.HBBM)) {
				String hbbm = (String)datasetCellEvent.getNewValue();
				if (hbbm  ==null || "".equals(hbbm.trim())) {
					String  customer = (String)row.getValue(busitemDs.nameToIndex(BXBusItemVO.CUSTOMER));
					if (customer  ==null || "".equals(customer.trim())) {
						ExpUtil.setRowValue(row, busitemDs, BXBusItemVO.CUSTACCOUNT, null);
						ExpUtil.setRowValue(row, busitemDs, BXBusItemVO.FREECUST, null);
						ExpUtil.setRowValue(row, busitemDs, "freecustacc", null);
					}
				}else {
					int paytarget = (Integer) row.getValue(busitemDs.nameToIndex(BXBusItemVO.PAYTARGET));
					if(paytarget == 1){
						ExpUtil.setRowValue(row, busitemDs, BXBusItemVO.RECEIVER, null);
						ExpUtil.setRowValue(row, busitemDs, BXBusItemVO.SKYHZH, null);
						ExpUtil.setRowValue(row, busitemDs, BXBusItemVO.CUSTOMER, null);
						String tAcc = getCustAcc(row, busitemDs, "hbbm");
						row.setValue(busitemDs.nameToIndex(BXBusItemVO.CUSTACCOUNT), tAcc);
					}else if(paytarget == 3){
						String tAcc = getCustAcc(row, busitemDs, "hbbm");
						row.setValue(busitemDs.nameToIndex(BXBusItemVO.CUSTACCOUNT), tAcc);
					}else {
						ExpUtil.setRowValue(row, busitemDs, BXBusItemVO.HBBM, null);
//					ExpUtil.setRowValue(row, busitemDs, BXBusItemVO.CUSTACCOUNT, null);
					}
				}
			}
			//�ͻ�
			if (datasetCellEvent.getColIndex() == busitemDs.nameToIndex(BXBusItemVO.CUSTOMER)) {
				String customer = (String)datasetCellEvent.getNewValue();
				if (customer  ==null || "".equals(customer.trim())) {
					String  hbbm = (String)row.getValue(busitemDs.nameToIndex(BXBusItemVO.HBBM));
					if (hbbm  ==null || "".equals(hbbm.trim())) {
						ExpUtil.setRowValue(row, busitemDs, BXBusItemVO.CUSTACCOUNT, null);
						ExpUtil.setRowValue(row, busitemDs, BXBusItemVO.FREECUST, null);
						ExpUtil.setRowValue(row, busitemDs, "freecustacc", null);
					}
				}else {
					int paytarget = (Integer) row.getValue(busitemDs.nameToIndex(BXBusItemVO.PAYTARGET));
					if(paytarget == 2){
						ExpUtil.setRowValue(row, busitemDs, BXBusItemVO.RECEIVER, null);
						ExpUtil.setRowValue(row, busitemDs, BXBusItemVO.SKYHZH, null);
						ExpUtil.setRowValue(row, busitemDs, BXBusItemVO.HBBM, null);
						String tAcc = getCustAcc(row, busitemDs, "customer");
						row.setValue(busitemDs.nameToIndex(BXBusItemVO.CUSTACCOUNT), tAcc);
					}else if(paytarget == 3){
						String tAcc = getCustAcc(row, busitemDs, "customer");
						row.setValue(busitemDs.nameToIndex(BXBusItemVO.CUSTACCOUNT), tAcc);
					}else {
						ExpUtil.setRowValue(row, busitemDs, BXBusItemVO.CUSTOMER, null);
//					ExpUtil.setRowValue(row, busitemDs, BXBusItemVO.CUSTACCOUNT, null);
					}
				}
			}
			//ɢ��
//			if (datasetCellEvent.getColIndex() == busitemDs.nameToIndex(BXBusItemVO.FREECUST)) {
//			}
		}
	}
	
	/**
	 * ��ȡ���������˻�
	 * @param row
	 * @param busitemDs
	 * @return
	 */
	private String getSkyhzhAcc(Row row, Dataset busitemDs) {
		String receiver = (String) row.getValue(busitemDs.nameToIndex(BXBusItemVO.RECEIVER));
		try {
			/**
			 * ��ȡ��ǰ��λ�µ����������˻�
			 */
			List<String> pkBankaccsubList = new ArrayList<String>();
			String dwbm = (String) row.getValue(busitemDs.nameToIndex(BXBusItemVO.DWBM));
			IPsnBankaccQueryService pbq = (IPsnBankaccQueryService) NCLocator.getInstance().lookup(IPsnBankaccQueryService.class.getName());
			PsnBankaccUnionVO[] pbus = pbq.queryPsnBankaccUnionVOsByBu(dwbm,
					false);
			if (pbus != null) {
				for (PsnBankaccUnionVO vo : pbus) {
					if (vo.getBankaccbasVO() != null) {
						BankAccSubVO[] bankAccSubVO = vo.getBankaccbasVO()
								.getBankaccsub();
						if (bankAccSubVO != null && bankAccSubVO.length > 0 && bankAccSubVO[0] != null) {
							pkBankaccsubList.add(bankAccSubVO[0].getPk_bankaccsub());
						}
					}
				}
			}
			IPsnBankaccPubService pa = (IPsnBankaccPubService) NCLocator.getInstance().lookup(IPsnBankaccPubService.class.getName());
			BankAccbasVO bankAccbasVO = pa.queryDefaultBankAccByPsnDoc(receiver);
			if (bankAccbasVO != null) {
				// �������������˻�ֻ�������п�����״̬�²���ʾ
				// ��ȡ����״̬ 1--�½� 2--���� 3--ͣ��
				Integer enAbleState = bankAccbasVO.getEnablestate();
				BankAccSubVO[] bankAccSubVO = bankAccbasVO.getBankaccsub();
				if (bankAccSubVO != null && bankAccSubVO.length > 0 && bankAccSubVO[0] != null) {
					// ��������״̬����ʾ����
					if (pkBankaccsubList.contains(bankAccSubVO[0].getPk_bankaccsub()) && enAbleState == 2) {
						return bankAccSubVO[0].getPk_bankaccsub();
					}
				}
			}
		} catch (Exception e) {
			ExpUtil.setRowValue(row, busitemDs, BXBusItemVO.SKYHZH, null);
			Logger.error(e.getMessage(), e);
		}
		return null;
	}
	/**
	 * ��ȡ���������˺�
	 * @param row
	 * @param busitemDs
	 * @return
	 */
	private String getCustAcc(Row row, Dataset busitemDs,String tFlag) {
		if ("hbbm".equals(tFlag)) {
			String hbbm = (String) row.getValue(busitemDs.nameToIndex(BXBusItemVO.HBBM));
			ISupplierPubService service = (ISupplierPubService) NCLocator.getInstance().lookup(ISupplierPubService.class.getName());
			try {
				String custaccount = service.getDefaultBankAcc(hbbm);
				return custaccount;
			} catch (Exception ex) {
				Log.getInstance(this.getClass()).error(ex);
			}
		}if ("customer".equals(tFlag)) {
			String customer = (String) row.getValue(busitemDs.nameToIndex(BXBusItemVO.CUSTOMER));
			ICustomerPubService service = (ICustomerPubService) NCLocator.getInstance().lookup(ICustomerPubService.class.getName());
			try {
				String custaccount = service.getDefaultBankAcc(customer);
				return custaccount;
			} catch (Exception ex) {
				Log.getInstance(this.getClass()).error(ex);
			}
		}
		return null;
	}
	
	/**
	 * @see -BxCardBodyEditListener doContract
	 * ����ʱ����дҵ���н��ʱ��������ڣ����¼���������Ȳ���
	 * @param bodyItem
	 * @throws ValidationException
	 * @throws BusinessException
	 */
	public void doContract(Dataset busitemDs, Row busitemRow) throws ValidationException, BusinessException {
		UFDouble ybje = busitemRow.getUFDobule(busitemDs.nameToIndex(BXBusItemVO.YBJE));

		 LfwWindow pageMeta = AppLifeCycleContext.current().getWindowContext().getWindow();
		 LfwView widget = pageMeta.getWidget("main");
		 
		 Dataset masterDs = widget.getViewModels().getDataset(masterDsID);
		 Row bxzbRow = masterDs.getSelectedRow();
		 if (bxzbRow == null) {
			 return;
		 }
		 String djdl = (String)bxzbRow.getValue(masterDs.nameToIndex(JKBXHeaderVO.DJDL));
		 
		
		if(ybje != null && !ybje.equals(UFDouble.ZERO_DBL) //ԭ��Ϊ0ʱ������������
				&& BXConstans.BX_DJDL.equals(djdl)//����ʱ���г�������
				){
			
			Dataset contractDs = widget.getViewModels().getDataset(IExpConst.BX_CONTRAST_DS_ID);
			Dataset2SuperVOSerializer ser = new Dataset2SuperVOSerializer();
			SuperVO[] pvos = ser.serialize(contractDs);
			if (pvos!= null && pvos.length>0) {
				
				List<BxcontrastVO> contrastsList = new ArrayList<BxcontrastVO>();
				for (int i=0; i<pvos.length; i++) {
					contrastsList.add((BxcontrastVO)pvos[i]);
				}
				
				
				new CjkMainViewCtrl().doContrast(masterDs, bxzbRow, contrastsList);
				
			}
			
		}
	}
	
	
	
	/**
	 * �������е�ԭ�ҽ�����������֧������ĸ�ֵ�е�ĳ��ֵ�����仯ʱ ���ø÷������¼���������ֵ
	 *
	 * @param key
	 *            �����仯��ֵ
	 * @param row
	 *            �����к�
	 */
	public void modifyFinValues(int jeIndex, int rowIndex, Dataset busitemDs, Row row) {
		UFDouble ybje = row.getValue(busitemDs.nameToIndex(BXBusItemVO.YBJE)) == null ?new UFDouble(0):(UFDouble)row.getValue(busitemDs.nameToIndex(BXBusItemVO.YBJE));
			
			
		UFDouble cjkybje = row.getValue(busitemDs.nameToIndex(BXBusItemVO.CJKYBJE)) == null ?new UFDouble(0):(UFDouble)row.getValue(busitemDs.nameToIndex(BXBusItemVO.CJKYBJE));
		
		UFDouble zfybje = row.getValue(busitemDs.nameToIndex(BXBusItemVO.ZFYBJE)) == null ?new UFDouble(0):(UFDouble)row.getValue(busitemDs.nameToIndex(BXBusItemVO.ZFYBJE));
			
		UFDouble hkybje = row.getValue(busitemDs.nameToIndex(BXBusItemVO.HKYBJE)) == null ?new UFDouble(0):(UFDouble)row.getValue(busitemDs.nameToIndex(BXBusItemVO.HKYBJE)); 
			
		

		// ���ԭ�ҽ����������仯
		if (busitemDs.nameToIndex(BXBusItemVO.YBJE) ==  jeIndex ||busitemDs.nameToIndex(BXBusItemVO.CJKYBJE) ==  jeIndex ) {
			if (ybje.getDouble() > cjkybje.getDouble()) {// ���ԭ�ҽ����ڳ�����
				ExpUtil.setRowValue(row, busitemDs, BXBusItemVO.ZFYBJE, ybje.sub(cjkybje));// ֧�����=ԭ�ҽ��-������
				ExpUtil.setRowValue(row, busitemDs, BXBusItemVO.HKYBJE, new UFDouble(0));
				ExpUtil.setRowValue(row, busitemDs, BXBusItemVO.CJKYBJE, cjkybje);
			} else {
				
				ExpUtil.setRowValue(row, busitemDs, BXBusItemVO.HKYBJE, cjkybje.sub(ybje));// ������=������-ԭ�ҽ��
				ExpUtil.setRowValue(row, busitemDs, BXBusItemVO.ZFYBJE, new UFDouble(0));
				ExpUtil.setRowValue(row, busitemDs, BXBusItemVO.CJKYBJE, cjkybje);
			}
		} 
//		else if (key.equals(BXBusItemVO.ZFYBJE)) {// �����֧�������仯
//			if (zfybje.toDouble() > ybje.toDouble()) {// ֧�����ܴ���ԭ�ҽ�����֧������ֵ��Ϊԭ�ҽ���ֵ
//				zfybje = ybje;
//				panel.setBodyValueAt(zfybje, rowIndex, BXBusItemVO.ZFYBJE);
//			}
//			panel.setBodyValueAt(ybje.sub(zfybje), rowIndex, BXBusItemVO.CJKYBJE);// ������=ԭ�ҽ��-֧�����
//			panel.setBodyValueAt("0", rowIndex, BXBusItemVO.HKYBJE);
//		} else if (key.equals(BXBusItemVO.HKYBJE)) {// ����ǻ�������仯
//			panel.setBodyValueAt(ybje.add(hkybje), rowIndex, BXBusItemVO.CJKYBJE);// ������=ԭ�ҽ��+������
//			panel.setBodyValueAt("0", rowIndex, BXBusItemVO.ZFYBJE);
//		}
		ExpUtil.setRowValue(row, busitemDs, BXBusItemVO.YBYE, ybje);// ԭ�����=ԭ�ҽ��

//		String bzbm = "null";
//		if (getHeadValue(JKBXHeaderVO.BZBM) != null) {
//			bzbm = getHeadValue(JKBXHeaderVO.BZBM).toString();
//		}
		transFinYbjeToBbje(rowIndex, busitemDs, row);
	}
	
	/**
	 * �������ҳǩ������ԭ�ҽ��㱾�ҽ��
	 *
	 * @param row
	 *            �����к�
	 * @param bzbm
	 *            ���ֱ���
	 */
	protected void transFinYbjeToBbje(int rowIndex, Dataset busitemDs, Row row) {
		UFDouble ybje = (UFDouble)row.getValue(busitemDs.nameToIndex(BXBusItemVO.YBJE));
		UFDouble cjkybje = (UFDouble)row.getValue(busitemDs.nameToIndex(BXBusItemVO.CJKYBJE));
		UFDouble hkybje = (UFDouble)row.getValue(busitemDs.nameToIndex(BXBusItemVO.HKYBJE));
		UFDouble zfybje =  (UFDouble)row.getValue(busitemDs.nameToIndex(BXBusItemVO.ZFYBJE)); 
			
		
		
//		ViewContext viewCtx = getLifeCycleContext().getViewContext();
//		LfwView widget = viewCtx.getView();
		LfwWindow jkbxWindow = AppLifeCycleContext.current().getWindowContext().getWindow();
		LfwView widget =  jkbxWindow.getView("main");
		Dataset masterDs = widget.getViewModels().getDataset(masterDsID);
		Row rowHead = masterDs.getCurrentRowData().getSelectedRow();
		
		String bzbm = (String)rowHead.getValue(masterDs.nameToIndex(BXHeaderVO.BZBM));
		if ("".equals(bzbm) || bzbm == null) {
			bzbm="null";
		}
		
		UFDouble hl=(UFDouble)rowHead.getValue(masterDs.nameToIndex(BXHeaderVO.BBHL));
		UFDouble grouphl = (UFDouble)rowHead.getValue(masterDs.nameToIndex(BXHeaderVO.GROUPBBHL));
		UFDouble globalhl= (UFDouble)rowHead.getValue(masterDs.nameToIndex(BXHeaderVO.GLOBALBBHL));
		
		String pk_org = (String)rowHead.getValue(masterDs.nameToIndex(BXHeaderVO.PK_ORG));
		String pk_group = ExpUtil.getPKGroup();
		
		try {
			UFDouble[] bbje = Currency.computeYFB(pk_org,Currency.Change_YBCurr, bzbm, ybje, null, null, null, hl,ExpUtil.getSysdate());
			
			ExpUtil.setRowValue(row, busitemDs, BXBusItemVO.BBJE, bbje[2]);
			ExpUtil.setRowValue(row, busitemDs, JKBXHeaderVO.BBYE, bbje[2]);
			
			
			bbje = Currency.computeYFB(pk_org, Currency.Change_YBCurr,
					bzbm, cjkybje, null, null, null, hl, ExpUtil.getSysdate());
			ExpUtil.setRowValue(row, busitemDs, BXBusItemVO.CJKBBJE, bbje[2]);
			
			bbje = Currency.computeYFB(pk_org, Currency.Change_YBCurr,
					bzbm, hkybje, null, null, null, hl, ExpUtil.getSysdate());
			ExpUtil.setRowValue(row, busitemDs, BXBusItemVO.HKBBJE, bbje[2]);
			
			
			bbje = Currency.computeYFB(pk_org, Currency.Change_YBCurr,
					bzbm, zfybje, null, null, null, hl, ExpUtil.getSysdate());
			ExpUtil.setRowValue(row, busitemDs, BXBusItemVO.ZFBBJE, bbje[2]);

			/**
			 * ����ȫ�ּ��ű�λ��
			 *
			 * @param amout
			 *            : ԭ�ҽ�� localAmount: ���ҽ�� currtype: ���� data:����
			 *            pk_org����֯
			 * @return ȫ�ֻ��߼��ŵı��� money
			 *
			 */
			UFDouble[] je = Currency.computeYFB(pk_org,
					Currency.Change_YBCurr, bzbm, ybje, null, null, null, hl,
					ExpUtil.getSysdate());
			UFDouble[] money = Currency.computeGroupGlobalAmount(je[0], je[2],
					bzbm, ExpUtil.getSysdate(), pk_org, pk_group,
					globalhl, grouphl);
			
			ExpUtil.setRowValue(row, busitemDs, BXBusItemVO.GROUPBBJE,money[0]);
			ExpUtil.setRowValue(row, busitemDs, BXBusItemVO.GLOBALBBJE,money[1]);
			

		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
		}
	}
	
	
	
	/**
	 * ���ñ�ͷtotal,ybjeֵ
	 */
	public void setHeadTotalValue() {
		
		
//			ViewContext viewCtx = getLifeCycleContext().getViewContext();
//			LfwView widget = viewCtx.getView();
			LfwWindow jkbxWindow = AppLifeCycleContext.current().getWindowContext().getWindow();
			LfwView widget =  jkbxWindow.getView("main");
			
			Dataset[] allDs = widget.getViewModels().getDatasets();
			List<Dataset> datasetList = new ArrayList<Dataset>();
			
			String totalItem = BXBusItemVO.AMOUNT;
			
			if (ExpUtil.isFyadjust()) { //���õ�����
				datasetList.add(widget.getViewModels().getDataset(IExpConst.BX_CSHARE_DS_ID));
				totalItem = CShareDetailVO.ASSUME_AMOUNT;
			} else {
				
				/*��ȡ����ҵ��ҳǩDs*/
				for (int i=0; i<allDs.length; i++) {
					String dsID = allDs[i].getId();
					if (!dsID.startsWith("$refds") && !dsID.equals(IExpConst.BXZB_DS_ID)  && !dsID.equals(IExpConst.JKZB_DS_ID)
							&& !dsID.equals(IExpConst.BX_CONTRAST_DS_ID) && !dsID.equals(IExpConst.BX_FINITEM_DS_ID) 
							&& !dsID.equals(IExpConst.JK_CONTRAST_DS_ID) && !dsID.equals(IExpConst.JK_FINITEM_DS_ID) 
							&& !dsID.equals(IExpConst.BX_CSHARE_DS_ID) && !dsID.equals(IExpConst.EXP_BX_ACCRUED_VERIFY_DS)
							&& !dsID.equals(IExpConst.EXP_BX_TBBDETAIL_DS)) {//�ų����з�ҵ��ҳǩ
						
						datasetList.add(allDs[i]);
					} 
				}
			}
			
			Row row = null;
			UFDouble totalAll=new UFDouble(0);
			for (int i=0; i<datasetList.size(); i++) {
				Dataset thisDs = datasetList.get(i);
				
				RowData rowData = thisDs.getCurrentRowData();
				if (rowData != null) {
					Row[] rowArr = rowData.getRows();
					for (int j=0; j<rowArr.length; j++) {
						row = rowArr[j];
						int index = thisDs.nameToIndex(totalItem);
						UFDouble amount = (UFDouble)row.getValue(index);
						//TODO 6.1 ��erm�ں�̨���������Ϣ�󣬿�ȥ��
//						ExpUtil.setRowValue(row, thisDs, BXBusItemVO.YBJE, amount);
						//~
						if (amount!=null) {
							totalAll = totalAll.add(amount);
						}
					}
				}
				
			}
			
			Dataset masterDs = widget.getViewModels().getDataset(masterDsID);
			Row row_bxzb = masterDs.getCurrentRowData().getSelectedRow();
			
			
			ExpUtil.setRowValue(row_bxzb, masterDs, BXHeaderVO.TOTAL, totalAll);
			ExpUtil.setRowValue(row_bxzb, masterDs, BXHeaderVO.YBJE, totalAll);
		  
	}
	
	/**
	 * ���ñ�ͷybjeֵ
	 */
	public void setHeadYbjeValue() {
		
		
			LfwWindow jkbxWindow = AppLifeCycleContext.current().getWindowContext().getWindow();
			LfwView widget =  jkbxWindow.getView("main");
			
			Dataset[] allDs = widget.getViewModels().getDatasets();
			List<Dataset> datasetList = new ArrayList<Dataset>();
			/*��ȡ����ҵ��ҳǩDs*/
			for (int i=0; i<allDs.length; i++) {
				String dsID = allDs[i].getId();
				if (!dsID.startsWith("$refds") && !dsID.equals(IExpConst.BXZB_DS_ID)  && !dsID.equals(IExpConst.JKZB_DS_ID)
						&& !dsID.equals(IExpConst.BX_CONTRAST_DS_ID) && !dsID.equals(IExpConst.BX_FINITEM_DS_ID) 
						&& !dsID.equals(IExpConst.JK_CONTRAST_DS_ID) && !dsID.equals(IExpConst.JK_FINITEM_DS_ID) 
						&& !dsID.equals(IExpConst.BX_CSHARE_DS_ID)&& !dsID.equals(IExpConst.EXP_BX_ACCRUED_VERIFY_DS)
						&& !dsID.equals(IExpConst.EXP_BX_TBBDETAIL_DS)) {//�ų����з�ҵ��ҳǩ
					
					datasetList.add(allDs[i]);
				}
			}
			Row row = null;
			UFDouble totalAll=new UFDouble(0);
			for (int i=0; i<datasetList.size(); i++) {
				Dataset thisDs = datasetList.get(i);
				
				RowData rowData = thisDs.getCurrentRowData();
				if (rowData != null) {
					Row[] rowArr = rowData.getRows();
					for (int j=0; j<rowArr.length; j++) {
						row = rowArr[j];
						int index = thisDs.nameToIndex(BXBusItemVO.YBJE);
						UFDouble amount = (UFDouble)row.getValue(index);
						if (amount!=null) {
							totalAll = totalAll.add(amount);
						}
					}
				}
				
			}
			
			Dataset masterDs = widget.getViewModels().getDataset(masterDsID);
			Row row_bxzb = masterDs.getCurrentRowData().getSelectedRow();
			
			ExpUtil.setRowValue(row_bxzb, masterDs, BXHeaderVO.YBJE, totalAll);
		  
	}
	
	
	/**
	 * ������屨����׼
	 * @param ds
	 * @param row
	 */
//	private void processReimRule(Dataset ds, Row row) {
//		//������׼
//
//		LfwView widget =  getLifeCycleContext().getWindowContext().getViewContext(IExpConst.EXP_WIDGET_ID).getView();
//		Dataset masterDs = widget.getViewModels().getDataset(masterDsID);
//
//
//		ArrayList<Dataset> allDetailDs = new ArrayList<Dataset>();
//		DatasetRelations dsRels = widget.getViewModels().getDsrelations();
//		if(dsRels != null){
//			DatasetRelation[] rels = dsRels.getDsRelations(masterDsID);
//			if(rels != null)
//			{
//				for (int i = 0; i < rels.length; i++) {
//					String detailDsId = rels[i].getDetailDataset();
//					Dataset detailDs = widget.getViewModels().getDataset(detailDsId);
//					if(detailDs != null) {
//						allDetailDs.add(detailDs);
//					}
//				}
//			}
//		}
//
//		Datasets2AggVOSerializer serializer = new ExpDatasets2AggVOSerializer();
//		Dataset[] detailDss = allDetailDs.toArray(new Dataset[0]);
//		AggregatedValueObject aggVo = serializer.serialize(masterDs, detailDss, BXVO.class.getName());
//		BXVO bxvo = (BXVO)aggVo;
//
//
//		try {
//
//			List<ReimRuleVO> vos = new ArrayList<ReimRuleVO>();
//			vos = NCLocator.getInstance().lookup(nc.itf.arap.prv.IBXBillPrivate.class).queryReimRule(null, (String)masterDs.getSelectedRow().getValue(masterDs.nameToIndex(ExpCommonUtil.getReimRuleOrg())));
//
//			Map<String, List<SuperVO>> reimRuleDataMap = VOUtils.changeCollectionToMapList(vos, "pk_billtype");
//
//			String pkGroup = ExpUtil.getPKGroup();
//			Collection<SuperVO> expenseType = NCLocator.getInstance().lookup(
//					IArapCommonPrivate.class).getVOs(ExpenseTypeVO.class,"pk_group='"+ pkGroup +"'", false);
//			Collection<SuperVO> reimType = NCLocator.getInstance().lookup(
//					IArapCommonPrivate.class).getVOs(ReimTypeHeaderVO.class,"pk_group='"+ pkGroup +"'", false);
//			Map<String, SuperVO> expenseMap = VOUtils.changeCollectionToMap(expenseType);
//			Map<String, SuperVO> reimtypeMap = VOUtils.changeCollectionToMap(reimType);
//
//			Object tabcode = ds.getExtendAttributeValue(ExtAttrConstants.TAB_CODE);
//			//TODO   Ŀǰ����в��Ƿ�ҳǩ��ȡ����tabcode�� ��tabcode ��δ���Ŀǰ�б�����׼�ĵ��ݶ���ȡ��tabcode
//			String tabcodeStr = tabcode == null ? "table_code" : (String)tabcode;
//	    	HashMap<String, String> hashMap = new HashMap<String, String>();
//	    	Field[] fields = ds.getFieldSet().getFields();
//	        for(int i=0; i<fields.length; i++)
//	        {
//	        	Field field = fields[i];
//	        	//��ȡ�Զ���1
//	        	Object attr = field.getExtendMap().get("$bill_template_field_def1");
//	        	if (attr == null) {
//	        		continue;
//	        	}
//	        	ExtAttribute extAtrr = (ExtAttribute)attr;
//
//	        	String userdefine1 = (String)extAtrr.getValue();
//
//
//	        	if(userdefine1 != null && userdefine1.startsWith("getReimvalue")){
//					String expenseName = userdefine1.substring(userdefine1.indexOf("(")+1,userdefine1.indexOf(")"));
//					Collection<SuperVO> values = expenseMap.values();
//					for(SuperVO vo:values){
//						if(("\""+vo.getAttributeValue(ExpenseTypeVO.CODE)+"\"").equals(expenseName)){
//							userdefine1=vo.getPrimaryKey();
//							hashMap.put(tabcodeStr+ReimRuleVO.REMRULE_SPLITER+field.getId(), userdefine1);
//						}
//					}
//	            }
//	        }
//
//
//	        List<BodyEditVO> result = BxUIControlUtil.doBodyReimAction(bxvo,reimRuleDataMap,hashMap);
//			for(BodyEditVO vo:result){
//				//getBillCardPanel().setBodyValueAt(vo.getValue(), vo.getRow(), vo.getItemkey(),vo.getTablecode());
//				int index = ds.getRowIndex(row);
//				if ( vo.getRow() == index) {
//					setRowValue(row, ds, vo.getItemkey(), vo.getValue());
//				}
//			}
//		} catch (BusinessException e) {
//			Logger.error(e.getMessage(), e);
//			throw new LfwRuntimeException(e.getMessage());
//		}
//
//	}
//	
//	
//
//	private void setRowValue(Row row, Dataset ds, String name, Object value) {
//		if (ds.nameToIndex(name) != -1) {
//			row.setValue(ds.nameToIndex(name), value);
//		}
//	}
	
	
	/**
	 * @see -Dataset2XmlBeforeProcessor processEditorFormular v6.3 2013-6-9
	 * ����༭��ʽ
	 * @param ds
	 */
	private void processEditorFormular(Dataset ds) {
		RowData rd = ds.getCurrentRowData();
		if(rd == null)
			return;
		Row selectedRow = ds.getSelectedRow();
		if(selectedRow == null)
			return;
		List<String> executedFpList = new ArrayList<String>();
		int fieldCount = ds.getFieldSet().getFieldCount();
		FormulaParse fp = LfwFormulaParser.getInstance();
		for(int i = 0; i < fieldCount; i ++){
			try{
				Field field = ds.getFieldSet().getField(i);
				String formular = field.getEditFormular();
				if(formular == null)
					continue;
				if(executedFpList.contains(formular))
					continue;
				executedFpList.add(formular);
				String[] expArr = formular.split(";");
				fp.setExpressArray(expArr);
				VarryVO[] varryVOs = fp.getVarryArray();
				if(varryVOs != null && varryVOs.length > 0){
					String[] formularNames = new String[varryVOs.length];
					
					Map<String,Integer> indexMap = getIndexMap(ds);
					for(int j = 0; j < varryVOs.length; j ++){
						String[] keys = varryVOs[j].getVarry();
						if(keys != null){
							for(String key : keys){
								List<Object> valueList = new ArrayList<Object>();
								//for(int k = 0; k < rcount; k ++){
									if(indexMap.get(key) == null)
										continue;
									Object value = selectedRow.getValue(indexMap.get(key));
									if(field.getExtendAttribute(field.getId()) != null){
										String refKey = ((Field)field.getExtendAttributeValue(field.getId())).getId();
										value = selectedRow.getValue(indexMap.get(refKey));
									}
									
									Field f = ds.getFieldSet().getField(key);
									if(f != null && value != null){
										//�����Double���ͣ�����һ��ת��
										if(StringDataTypeConst.UFDOUBLE.equals(f.getDataType()) || StringDataTypeConst.DOUBLE.equals(f.getDataType()) || StringDataTypeConst.Decimal.equals(f.getDataType()) || (StringDataTypeConst.CUSTOM.equals(f.getDataType()) && f.getPrecision() != null && !f.getPrecision().equals(""))){
											if(!(value instanceof UFDouble))
												value = new UFDouble(value.toString());
										}
										else if(StringDataTypeConst.INTEGER.equals(f.getDataType())){
											if(!(value instanceof Integer))
												value = new Integer((String)value);
										}
									}
									valueList.add( value );
								//}
								fp.addVariable(key, valueList);
							}
							formularNames[j] = varryVOs[j].getFormulaName();
						}
					}
					Object[][] result = fp.getValueOArray();
					if(result != null){
						for (int l = 0; l < formularNames.length; l++) {
							int index = ds.nameToIndex(formularNames[l]);
							if(index == -1){
								LfwLogger.error("can not find column:" + formularNames[l] + ", ds id:" + ds.getId());
								continue;
							}
							selectedRow.setValue(index, result[l][0]);
						}
					}
				} else {
					fp.getValueOArray();
				}
			} catch (Throwable e) {
				if(e instanceof LfwRuntimeException)
					throw (LfwRuntimeException)e;
				Logger.error(e.getMessage(), e);
			}
		}
	}
	
	/**
	 * ͨ��ds����Ӧfield
	 * @param ds
	 * @return Map<String,Integer>
	 */
	private Map<String,Integer> getIndexMap(Dataset ds) {
		Map<String,Integer> indexMap = new HashMap<String,Integer>();
		int count = ds.getFieldSet().getFieldCount();
		for(int i=0;i < count; i++){
			Field field = ds.getFieldSet().getField(i);
			String key = field.getId();
			indexMap.put(key,i);
		}
		return indexMap;
	}
	
	/**
	 * ������׼��ʾ
	 */
	public void dealReimRuleTips(int index){
		LfwWindow jkbxWindow = AppLifeCycleContext.current().getWindowContext().getWindow();
		String expreimruleInteractionFlag = LfwRuntimeEnvironment.getWebContext().getRequest().getParameter("expreimrule" + "interactflag");
		LfwView widget =  jkbxWindow.getView("main");
		Dataset masterDs = widget.getViewModels().getDataset(masterDsID);
		Row headSelRow = masterDs.getSelectedRow();
		String[] assignPks = (String[])AppUtil.getAppAttr(WfmConstants.ASSIGNALLUSERS);
		String bxdj= (String) AppUtil.getAppAttr(IExpConst.EXP_REIMRULE_FLAG);
		Dataset[] detailDss = ExpUtil.getAllDetailDss(widget, masterDsID);
		Datasets2AggVOSerializer serializer = new ExpDatasets2AggVOSerializer();
		AggregatedValueObject aggVo = serializer.serialize(masterDs, detailDss, BXVO.class.getName());
		BXVO jkbxvo = (BXVO)aggVo;
		if (bxdj!=null && "Y".equals(bxdj)) {
			List<BodyEditVO> result = YerBxUIControlUtil.doBodyReimAction(jkbxvo,ExpReimruleUtil.getReimRuleDataMap(masterDs, headSelRow),ExpUtil.getBusitemDss(widget, masterDsID),ExpReimruleUtil.getReimDimMap(masterDs, headSelRow));
			Dataset[] busitems = ExpUtil.getBusitemDss(widget, masterDsID);
			if (expreimruleInteractionFlag == null && assignPks ==null ) {
				String warnMessage = "";
				String errorMessage = "";
				String preMessage1 = LfwResBundle.getInstance().getStrByID("weberm", "ExpUifCommitCmd-000002")/*������ϸ*/;
				String preMessage2 = LfwResBundle.getInstance().getStrByID("weberm", "ExpUifCommitCmd-000003")/*��*/;
				String preMessage3 = LfwResBundle.getInstance().getStrByID("weberm", "ExpUifCommitCmd-000004")/*������*/;
				String preMessage4 = LfwResBundle.getInstance().getStrByID("weberm", "ExpUifCommitCmd-000005")/*������׼����������*/;
				String preMessage5 = LfwResBundle.getInstance().getStrByID("weberm", "ExpUifCommitCmd-000006")/*����׼���������Ϊ:*/;
				String preMessage6 = LfwResBundle.getInstance().getStrByID("weberm", "ExpUifCommitCmd-000007")/*�����ֶ��޸�*/;
				int tip = 0;
				UFDouble stdVlu = null;
				int JD = -1;
				for(BodyEditVO vo:result){
					if (vo instanceof ControlBodyEditVO && vo.getRow() == index) {
						tip = ((ControlBodyEditVO) vo).getTip();
						for (Dataset ds : busitems) {
							if (vo.getTablecode().equals(ds.getExtendAttributeValue("$TAB_CODE"))) {
								Object itemValue = (Object)ds.getCurrentRowData().getRow(vo.getRow()).getValue(ds.nameToIndex(vo.getItemkey()));
								if(itemValue == null){
									itemValue = UFDouble.ZERO_DBL;
								}
								UFDouble currentValue = new  UFDouble(itemValue.toString());
								UFDouble standardValue = new  UFDouble(UFDouble.ZERO_DBL);
								if (((ControlBodyEditVO) vo).getFormulaRule()!=null){
									String formular = vo.getItemkey() + "->" + ((ControlBodyEditVO) vo).getFormulaRule();
									Object newValue =YerBxUIControlUtil.execEditorFormular(ds, ds.getCurrentRowData().getRow(vo.getRow()), vo.getItemkey(), formular);
									if (newValue!=null) {
										standardValue= new UFDouble( newValue.toString());
									}
								}else {
									standardValue = new UFDouble( vo.getValue().toString());
								}
								//��ȡ��ǰ�����ֶ�����
								String fieldName = getFieldName(widget,vo.getItemkey());
								if (currentValue.compareTo(standardValue) == 1) {
									if (1==tip) {
										warnMessage +=  preMessage2+"��"+(vo.getRow()+1) + "��"+preMessage3+"��"+fieldName+"��" +",";
									}else if (2==tip){
										stdVlu = standardValue;
										JD = ds.getFieldSet().getField(vo.getItemkey()).getPrecision() == null? -1:
											Integer.parseInt(ds.getFieldSet().getField(vo.getItemkey()).getPrecision());
										if(JD != -1){
											stdVlu = new UFDouble(stdVlu.doubleValue(),JD);
										}
										errorMessage +=  preMessage2+"��"+(vo.getRow()+1) + "��"+preMessage3+"��"+fieldName+"��"+preMessage4 +":"+stdVlu+".";
									}
								}
							}
						} 
					}
				}
				if (errorMessage.length()>0) {
					errorMessage = preMessage1 +":"+errorMessage.substring(0, errorMessage.length()-1) + preMessage6;
					AppInteractionUtil.showMessageDialog(errorMessage);
				}
				if (warnMessage.length()>0) {
					warnMessage = preMessage1 +":"+warnMessage.substring(0, warnMessage.length()-1) + preMessage4;
					AppInteractionUtil.showMessageDialog(warnMessage);
				}
			}
		}
	}

	
	/**
	 * ��ȡgrid��Ӧ�ֶε�����
	 * @param mainWidget
	 * @param itemkey
	 * @return
	 */
	private String getFieldName(LfwView mainWidget ,String itemkey) {
		String fieldName = "";
		WebComponent[] components = mainWidget.getViewComponents().getComponents();
		if (components != null) {
			for (WebComponent com : components) {
				if (com instanceof GridComp) {
					if ("busitem_grid".equals(com.getId())){
						for (IGridColumn column : ((GridComp) com).getColumnList()) {
							GridColumn thisColumn = (GridColumn) column;
							if (thisColumn.getField().equals(itemkey)) {
								fieldName = thisColumn.getText();
								break;
							}
						}
					}
				}
			}
		}
		return fieldName;
	}

}
