package nc.bs.er.exp.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nc.bs.er.exp.quickshare.listeners.ref.ShareRuleRefListener;
import nc.bs.er.exp.util.ExpRegisterUtil;
import nc.bs.er.exp.util.ExpUtil;
import nc.bs.er.util.YerMenuUtil;
import nc.bs.er.util.YerUtil;
import nc.bs.erm.util.ErUtil;
import nc.bs.erm.util.ErmDjlxConst;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.exception.ComponentException;
import nc.bs.logging.Logger;
import nc.bs.pf.pub.PfDataCache;
import nc.bs.pub.formulaparse.FormulaParse;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.er.pub.IArapBillTypePublic;
import nc.itf.er.reimtype.IReimTypeService;
import nc.itf.erm.ntb.IErmLinkBudgetService;
import nc.itf.fi.pub.Currency;
import nc.uap.cpb.org.exception.CpbBusinessException;
import nc.uap.ctrl.tpl.qry.SimpleQueryController;
import nc.uap.lfw.core.LfwRuntimeEnvironment;
import nc.uap.lfw.core.cmd.UifDatasetLoadCmd;
import nc.uap.lfw.core.comp.FormComp;
import nc.uap.lfw.core.comp.GridColumn;
import nc.uap.lfw.core.comp.GridComp;
import nc.uap.lfw.core.comp.IGridColumn;
import nc.uap.lfw.core.comp.MenuItem;
import nc.uap.lfw.core.comp.MenubarComp;
import nc.uap.lfw.core.comp.WebComponent;
import nc.uap.lfw.core.comp.WebElement;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Field;
import nc.uap.lfw.core.data.LfwParameter;
import nc.uap.lfw.core.data.UnmodifiableMdField;
import nc.uap.lfw.core.event.GridEvent;
import nc.uap.lfw.core.event.conf.DatasetRule;
import nc.uap.lfw.core.event.conf.EventConf;
import nc.uap.lfw.core.event.conf.EventSubmitRule;
import nc.uap.lfw.core.event.conf.FormRule;
import nc.uap.lfw.core.event.conf.ViewRule;
import nc.uap.lfw.core.exception.LfwRuntimeException;
import nc.uap.lfw.core.formular.LfwFormulaParser;
import nc.uap.lfw.core.log.LfwLogger;
import nc.uap.lfw.core.model.PageModel;
import nc.uap.lfw.core.page.Connector;
import nc.uap.lfw.core.page.IPluginDesc;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.page.LfwWindow;
import nc.uap.lfw.core.page.PluginDesc;
import nc.uap.lfw.core.refnode.MasterFieldInfo;
import nc.uap.lfw.core.refnode.NCRefNode;
import nc.uap.lfw.core.refnode.RefNode;
import nc.uap.lfw.core.refnode.RefNodeRelation;
import nc.uap.lfw.core.refnode.RefNodeRelations;
import nc.uap.lfw.core.uimodel.WindowConfig;
import nc.uap.lfw.jsp.uimeta.UIElement;
import nc.uap.lfw.jsp.uimeta.UIFlowvLayout;
import nc.uap.lfw.jsp.uimeta.UIFlowvPanel;
import nc.uap.lfw.jsp.uimeta.UILayoutPanel;
import nc.uap.lfw.jsp.uimeta.UIMeta;
import nc.uap.lfw.jsp.uimeta.UIView;
import nc.uap.wfm.constant.WfmConstants;
import nc.uap.wfm.constant.WfmTaskStatus;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.bd.ref.RefInfoVO;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.er.exp.IExpConst;
import nc.vo.er.fysq.IFysqConst;
import nc.vo.er.linkntb.LinkNtbParamVO;
import nc.vo.er.reimrule.ReimRuleDimVO;
import nc.vo.er.reimrule.ReimRulerVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.fipub.rulecontrol.RuleDataCacheEx;
import nc.vo.pub.BusinessException;
import nc.vo.pub.billtype.BilltypeVO;
import nc.vo.pub.formulaset.VarryVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.tb.obj.NtbParamVO;
import nc.vo.wfengine.definition.WorkflowTypeEnum;

import org.apache.commons.lang.StringUtils;

import uap.web.bd.pub.AppUtil;

public abstract class ERPageModel extends PageModel {
	
	
	
	@Override
	protected void initPageMetaStruct() {
		
		super.initPageMetaStruct();
		
		UIMeta uiMeta = (UIMeta)getUIMeta();
		
		String includejs = uiMeta.getIncludejs();
		if(!StringUtils.isEmpty(includejs)){
			includejs += ",";
		}else{
			includejs = "";
		}
		
		Boolean isCopy = LfwRuntimeEnvironment.getWebContext().
							getAppSession().getOriginalParameter("isCopy") == null ? 
									false : Boolean.valueOf(LfwRuntimeEnvironment.getWebContext().getAppSession().getOriginalParameter("isCopy"));
		AppUtil.addAppAttr("isCopy", isCopy);
		//�Ѿ�û�����壬������ȥ����������Ի�ģ��������������⣬��6.1���������selfDefImageBtnRender="ExpDefImageBtnRender"
		uiMeta.setIncludejs(includejs+"../sync/yer/weberm/html/nodes/includejs/themes/webclassic/ermybill/erbill.js");
		
		//����ʱ�ô˲���
		String flowTypePk = (String) LfwRuntimeEnvironment.getWebContext().getAppSession().getOriginalParameter("billType");
		String taskPk =  (String) LfwRuntimeEnvironment.getWebContext().getAppSession().getOriginalParameter("taskPk");
		AppUtil.addAppAttr(WfmConstants.WfmAppAttr_FolwTypePk, flowTypePk);
		AppUtil.addAppAttr(WfmConstants.WfmAppAttr_TaskPk, taskPk);
		String billId = LfwRuntimeEnvironment.getWebContext().getOriginalParameter(UifDatasetLoadCmd.OPEN_BILL_ID);
		AppUtil.addAppAttr("billId", billId);
		AppUtil.addAppAttr("NC", "Y");
		
		
		String islinkjkd =  (String) LfwRuntimeEnvironment.getWebContext().getAppSession().getOriginalParameter("islinkjkd");
		AppUtil.addAppAttr("IS_LINKJKD", islinkjkd);
		
		if ("Y".equals(islinkjkd)){ 	
			LfwView menuWidget = getPageMeta().getView("jk_menu");
			MenubarComp menuComp = menuWidget.getViewMenus().getMenuBar("jkzb_menu");
			List<MenuItem> menuList = menuComp.getMenuList();
			
			for(MenuItem menu : menuList){
				if(!"filemanager".equals(menu.getId()) && !"link".equals(menu.getId())){
					menu.setEnabled(false);
					menu.setStateManager(null);
				}
			}	
		}
		
		
		String islinkbxd =  (String) LfwRuntimeEnvironment.getWebContext().getAppSession().getOriginalParameter("islinkbxd");
		AppUtil.addAppAttr("IS_LINKBXD", islinkbxd);
		if ("Y".equals(islinkbxd)){ 	
			LfwView menuWidget = getPageMeta().getView("bx_menu");
			MenubarComp menuComp = menuWidget.getViewMenus().getMenuBar("bxzb_menu");
			List<MenuItem> menuList = menuComp.getMenuList();
			
			for(MenuItem menu : menuList){
				if(!"filemanager".equals(menu.getId()) && !"link".equals(menu.getId()) && !"otheroperate".equals(menu.getId())){
					menu.setEnabled(false);
					menu.setStateManager(null);
				}else{
					if("otheroperate".equals(menu.getId())){
						List<MenuItem> childList = menu.getChildList();
						for(MenuItem childmMenu : childList){
							if(!"filemanager".equals(childmMenu.getId())){
								childmMenu.setEnabled(false);
								childmMenu.setStateManager(null);
							}
						}
						
					}
				}
				
				
			}	
		}
		
		//��ȡ���ܽڵ�url
		String appName = (String)LfwRuntimeEnvironment.getWebContext().getAppSession().getAttribute("appId");
		String nodecode = (String)LfwRuntimeEnvironment.getWebContext().getAppSession().getAttribute("nodecode");
		AppUtil.addAppAttr("DJURL", "app/"+appName + "?billType="+flowTypePk + "&nodecode="+nodecode);
		
		AppUtil.addAppAttr(IExpConst.CURRENT_MASTER_DS, getDatasetID());
		
		//У�鲢��ȡDjLXVO
		DjLXVO djlxvo = getDjLXVOAndCheck(flowTypePk);
		BilltypeVO billtypevo = PfDataCache.getBillType(flowTypePk);
		
		//���浥���������ƣ������ʼ�����ݱ���Ҫ��
		AppUtil.addAppAttr("EXP_DJLXMC", billtypevo == null ? djlxvo.getDjlxmc() : billtypevo.getBilltypenameOfCurrLang());
		AppUtil.addAppAttr(IExpConst.IS_ADJUST, "N");
		if (djlxvo.getBxtype()!=null && ErmDjlxConst.BXTYPE_ADJUST==djlxvo.getBxtype()) {//Ϊ���õ�����
			AppUtil.addAppAttr(IExpConst.IS_ADJUST, "Y");
		}
		
		
		
		//���ݴ���
		AppUtil.addAppAttr(IExpConst.YERDJDL, djlxvo.getDjdl());
		
		
		//--V631
		//�Ƿ��������
		UFBoolean is_mactrl = djlxvo.getIs_mactrl();
		if (is_mactrl != null && is_mactrl.booleanValue()) {
			AppUtil.addAppAttr(IExpConst.EXP_IS_MACTRL, "Y");
		}else{
			AppUtil.addAppAttr(IExpConst.EXP_IS_MACTRL, null);
		}
		
		String[] psnArr = getPersonInfo();
		String pk_org = getDefaultPermissionOrg(psnArr[2]);
		
		AppUtil.addAppAttr(IExpConst.PSN_JKBXR, psnArr[0]);
		AppUtil.addAppAttr(IExpConst.PSN_DEPT, psnArr[1]); //Ĭ�ϲ���
		AppUtil.addAppAttr(IExpConst.PSN_PERMISSION_ORG, pk_org);//Ĭ����֯
		AppUtil.addAppAttr(IExpConst.PSN_NO_PERMISSION_ORG, psnArr[2]);//δ��ȨĬ����֯
		AppUtil.addAppAttr(IExpConst.PSN_GROUP, psnArr[3]);
		AppUtil.addAppAttr(IExpConst.DEFAULT_BZBM, getDefaultCurrency(pk_org, djlxvo));//Ĭ�ϱ���
		AppUtil.addAppAttr(IExpConst.DEFAULT_DJLXBM, flowTypePk);//Ĭ�ϵ������ͱ���
		
		
		
		//--V631
		
		LfwWindow pageMeta = getPageMeta();
		LfwView mainWidget = pageMeta.getView("main");
		//���Ի����ÿɽ���������������, �ж��Ƿ��б���ҵ��gird.
		String dsID = getDatasetID();
		Dataset masterDs = mainWidget.getViewModels().getDataset(dsID);
		//������幫ʽ��Ϣ
		saveBusitemDsFormular(mainWidget);
		
		//���� main view �еĿؼ�
		moidfyMainViewComps(mainWidget);
		
		//�������ݻ�ȡ������׼������
		if((djlxvo.getBxtype()==null || (djlxvo.getBxtype()!=null && ErmDjlxConst.BXTYPE_ADJUST!=djlxvo.getBxtype()))
				&& !"2647".equals(djlxvo.getDjlxbm())) {
			AppUtil.addAppAttr(IExpConst.EXP_REIMRULE_FLAG, "Y");
			getReimRuleDataMap(masterDs);
		}
		
		//���� main view ��ViewModels
		moidfyMainViewModels(mainWidget);
		
		
		//�����˵�������view
		moidfyOtherViews(pageMeta);
		
		//����ݣ�Ĭ�ϲ���ʾ�ĵ�����ϸ��,��ť����
		if("2647".equals(flowTypePk)){
			dealHKBill(pageMeta, uiMeta);
		}
		//�����ԭʼ����ɨ�輰�������Ĵ򿪵����
		dealOpenfromXYZ(pageMeta, uiMeta);
		
//		String pageId = LfwRuntimeEnvironment.getWebContext().getPageId();
//		if(pageId.equals("fyadjust")){
//			this.dealFyjz(pageMeta, uiMeta);
//		}
		//TODO
		ExpRegisterUtil.registerListenerAll(this);
		//ע����ռ���
		ExpRegisterUtil.registerListener(this);
		
		
		
		boolean hasBusitemGrid = ExpUtil.hasBusitemGrid(mainWidget, masterDs);
		boolean hasBusiGridInUIMeta = ExpUtil.isHasBusiGridInUIMeta();
		if (!hasBusitemGrid || !hasBusiGridInUIMeta) {
			AppUtil.addAppAttr("ExpHasBusitemGrid", "N");
		}
		
		
		//����-���棬�ύ ʹ�ã�ÿ����������Ҫ���
		LfwRuntimeEnvironment.getWebContext().getRequest().getSession().removeAttribute(IExpConst.CJK_SELECTED_JKHEADVOS_LIST);
		//����-���棬�ύ ʹ�ã�ÿ����������Ҫ���
		LfwRuntimeEnvironment.getWebContext().getRequest().getSession().removeAttribute(IExpConst.ADDFROMMT_SELECTED_MTHEADVO);
		
		RuleDataCacheEx.getInstance().getRuledatamap().clear();
		RuleDataCacheEx.getInstance().getRulesmap().clear();
		RuleDataCacheEx.getInstance().getBillruleMap().clear();
		RuleDataCacheEx.getInstance().getFactorruleMap().clear();
		RuleDataCacheEx.getInstance().getAccasoaruleMap().clear();
		RuleDataCacheEx.getInstance().getBusinormruleMap().clear();
		RuleDataCacheEx.getInstance().getRule_assmapMap().clear();
		RuleDataCacheEx.getInstance().getRule_assid_valMap().clear();
		RuleDataCacheEx.getInstance().getItembindmap().clear();
		
	}
	
	
	/**
	 * ��ȡ��Ա��Ϣ
	 * @return ��Ա����Ա���ڲ��ţ���Ա������֯����Ա���ڼ���
	 */
	private String[] getPersonInfo () {
		
		String[] str = {null,null,null,null};
		try {
			str = NCLocator.getInstance().lookup(IBXBillPrivate.class).queryPsnidAndDeptid(YerUtil.getPk_user(), YerUtil.getPK_group());
		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
			throw new LfwRuntimeException(e.getMessage());
		}
		return str;
		
	}
	
	
	/**
	 * ��ȡĬ�ϱ���
	 * @param pk_org
	 * @return
	 */
	private String getDefaultCurrency(String pk_org, DjLXVO djlxvo) {
		
		String org = pk_org == null ? "": pk_org;
		/**���ñ���**/
		String defcurrency = djlxvo.getDefcurrency();
		if(defcurrency==null || defcurrency.trim().length()==0){
			if(org!=null && org.length()!=0){
				try {
					defcurrency=Currency.getOrgLocalCurrPK(org);
				} catch (BusinessException e) {
					Logger.error(e.getMessage(), e);
					throw new LfwRuntimeException(e.getMessage());
				}
			} 
			/*else if(BXUiUtil.getDefaultOrgUnit()!=null){
				defcurrency=Currency.getOrgLocalCurrPK(BXUiUtil.getDefaultOrgUnit());
			} */
		}
		
		return defcurrency;
		
	}
	
	/**
	 * ��ȡĬ����֯,�����Ȩ�޵��ж�
	 * @param defaultOrg
	 * @return ������֯Ȩ���ڷ���null
	 */
	private String getDefaultPermissionOrg(String defaultOrg) {
		String pk_org = null;
		try{
			//���Ĭ����֯�Ƿ�����Ȩ
			if (defaultOrg != null && defaultOrg.length() > 0) {
				String[] values = ExpUtil.getPermissionOrgsPortal();
				if (values != null && values.length > 0) {
					List<String> permissionOrgList = Arrays.asList(values);
					if (permissionOrgList.contains(defaultOrg)) {
						pk_org = defaultOrg;
					}
				} 
				//����֯Ȩ�޲���ʾ
			}
		} catch (CpbBusinessException e) {
			Logger.error(e.getMessage(), e);
			pk_org = defaultOrg;
		}
		return pk_org;
	}
	
	
	/**
	 * У�鵥������
	 */
	private DjLXVO getDjLXVOAndCheck(String billType) {
		
		DjLXVO djLXVO = null;
		 try {
			 djLXVO = NCLocator.getInstance().lookup(IArapBillTypePublic.class).getDjlxvoByDjlxbm(billType, YerUtil.getPK_group());
		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
		}
		if (djLXVO == null) {
			//�ýڵ㽻�������ѱ�ͣ�ã����ɲ����ڵ㣡
			throw new LfwRuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
					.getStrByID("COMMON", "UPP2011-000171")/*@res "�ýڵ㵥�������ѱ���棬���ɲ����ڵ㣡"*/);
		}
		
		UFBoolean fcbz = djLXVO.getFcbz();
		if (fcbz != null && fcbz.booleanValue()) {
			throw new LfwRuntimeException(nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("COMMON", "UPP2011-000429")/*
																		 * @res
																		 * "�ýڵ㵥�������Ѿ���棬���ܽ��в���!"
																		 */);
		}
		return djLXVO;
		
	}
	
	private void moidfyMainViewComps(LfwView mainWidget) {
		
		//���ݵ���״̬���жϵ����Ƿ�Ϊֻ��̬
		String djzt = LfwRuntimeEnvironment.getWebContext().getOriginalParameter(JKBXHeaderVO.DJZT);
		Boolean isCopy = LfwRuntimeEnvironment.getWebContext().
				getAppSession().getOriginalParameter("isCopy") == null ? 
						false : Boolean.valueOf(LfwRuntimeEnvironment.getWebContext().getAppSession().getOriginalParameter("isCopy"));
		if (djzt!= null && !"0".equals(djzt) && !"1".equals(djzt) && !isCopy) {  //0 �ݴ�  1 ����
			WebComponent[] components = mainWidget.getViewComponents().getComponents();
			if (components!= null) {
				for (WebComponent com: components) {
					if (com instanceof FormComp) {
						((FormComp) com).setRenderType(6);
						((FormComp) com).setLabelMinWidth(153);
					}
					//���ر���Ҽ��ϰ�ť
					if (com instanceof GridComp) {
						((GridComp) com).setShowImageBtn(false);
					}
				}
			}
		} else {
			WebComponent[] components = mainWidget.getViewComponents().getComponents();
			if (components!= null) {
				for (WebComponent com: components) {
					if (com instanceof FormComp) {
						((FormComp) com).setRenderType(5);
						((FormComp) com).setLabelMinWidth(153);
					}
				}
			}
		}
		
		WebComponent[] components = mainWidget.getViewComponents().getComponents();
		if (components!= null) {
			for (WebComponent com: components) {
				if (com instanceof GridComp) {
					
					
					//���ݱ��column��datatype�����޸�ds�Ķ�Ӧfield    datatypeΪufdouble��Ϊfield��Ӿ���
					//��̬���þ��ȼ�����ǰ�޸�double���Զ������ֵʹ��,������ 13.00  �浽���л�Ϊ 13
					
					String gridDsId = ((GridComp) com).getDataset();
					Dataset gridDs = mainWidget.getViewModels().getDataset(gridDsId);
					
					for (IGridColumn column: ((GridComp) com).getColumnList()) {
						GridColumn thisColumn = (GridColumn)column;
						//if ("UFDouble".equals(thisColumn.getDataType())) {
						if ("DecimalText".equals(thisColumn.getEditorType())) {  //����Ի�ģ���ϲ�֧������dataType�����Ը�Ϊ�Ƚ�EditorType
							
							Field field = gridDs.getFieldSet().getField(thisColumn.getField());
							
							if(field!=null) {
								
								YerUtil.modifyField(gridDs, "Precision", field.getId(), "8");
							}
						}
						
						//if ("Integer".equals(thisColumn.getDataType())) {  
						if ("IntegerText".equals(thisColumn.getEditorType())) { 
							
							//�Զ�������ȷ��������ֵ����ִ�й�ʽDataset2XmlSerializer  processFormular
							//<ModifyField editFormular="amount-%3Ezeroifnull%28defitem5%29*zeroifnull%28defitem4%29" id="defitem4" precision="2">
							//�������� ȷ��  ���͵��Զ��������ù�ʽ�� ���ñ���Ӿ���ֵҲ��ȷ����ʽִ��,����˾��Ⱥ󱣴�ʱ���޸��Զ������ֵ ExpUtil.modifySelfDefineValue(jkbxvo, widget, masterDsId);
							
//							Field field = gridDs.getFieldSet().getField(thisColumn.getField());
//							if (field instanceof UnmodifiableMdField) {
//								Field newField = ((UnmodifiableMdField) field).getMDField();
//								newField.setDataType("Integer");
//								gridDs.getFieldSet().updateField(newField.getId(), newField);
//								
//							} else {
//							    field.setDataType("Integer");
//							}
							
							
							//�������£�����ᱨ //java.lang.ClassCastException: java.lang.Integer cannot be cast to java.lang.String
							//at nc.vo.pub.SerializeWriter.writeValue(SerializeWriter.java:241)
							Field field = gridDs.getFieldSet().getField(thisColumn.getField());
							if(field!=null) {
								YerUtil.modifyField(gridDs, "Precision", field.getId(), "-1");
							}
							
							
						}
//						if ("String".equals(thisColumn.getDataType()) || "SelfDefine".equals(thisColumn.getDataType())) {
//							thisColumn.setMaxLength("101");//���ñ����ַ������������󳤶ȣ�ͬnc����ģ���ϵĳ���
//						}
					}
					
					//��ӱ���Ҽ��ϵİ�ť
					if ("contrast_grid".equals(com.getId()) || "bx_accrued_verify_grid".equals(com.getId()) ) {
						((GridComp)com).setShowImageBtn(false);  //������ϸ�в���Ӱ�ť
						continue;
					}else {
						((GridComp)com).setShowImageBtn(true); 
					}
					
					MenubarComp menuBarComp = ((GridComp) com).getMenuBar();
					if (menuBarComp!= null) {
						
						List<MenuItem> menuList = menuBarComp.getMenuList();
						for(int i=0; i<menuList.size(); i++) {
							MenuItem item = menuList.get(i);
							//�༭��ťIDֵΪnull$HeaderBtn_Edit����ԭ������gridHeaderBtn_Editȥƥ�䣬�Է��´��ڱ䣬��������
							//if ("gridHeaderBtn_Edit".equals(item.getId())) {
							if (item.getId().endsWith("HeaderBtn_Edit")) {
								 menuList.remove(i);
									i--;
							 }
							 
							 if (item.getId().endsWith("HeaderBtn_Add")) {
								 menuList.remove(i);
									i--;
							 }

							 if (item.getId().endsWith("HeaderBtn_Delete")) {
								 menuList.remove(i);
									i--;
							 }
						}
					}
					initExpGridMenubar((GridComp)com);
				}
				
			}
		}
		
		//Ϊҵ���б�����onLastCellEnter�¼�
		addBusitemGridEvent(mainWidget);
	}
	
	private void moidfyMainViewModels(LfwView mainWidget) {
		
		String dsID = getDatasetID();
		Dataset masterDs = mainWidget.getViewModels().getDataset(dsID);
		//��̯����̯���Թ�֧������������ҪӰ��ɨ��
		String[] fieldStr= new String[]{JKBXHeaderVO.ISCOSTSHARE,JKBXHeaderVO.ISEXPAMT,"isexpedited","isneedimag"}; 
		//���� ��ѡ��field��Ĭ��ֵ�� Ԫ�����е�Ĭ��ֵ��"N",  �޸�ΪUFBoolean.FALSE Ԫ�����е�Ĭ��ֵ��"Y",�޸�ΪUFBoolean.TRUE;
		for (int i=0;i<fieldStr.length;i++) {
			Field tField = masterDs.getFieldSet().getField(fieldStr[i]);
			if (tField != null) {
				if ("UFBoolean".equals(tField.getDataType()) && "N".equals(tField.getDefaultValue())) {
					if (tField instanceof UnmodifiableMdField) {
						Field newField = ((UnmodifiableMdField) tField).getMDField();
						newField.setDefaultValue(UFBoolean.FALSE);
						masterDs.getFieldSet().updateField(newField.getId(), newField);
					} else {
						tField.setDefaultValue(UFBoolean.FALSE);
					}
				}else if ("UFBoolean".equals(tField.getDataType()) && "Y".equals(tField.getDefaultValue())) {
					if (tField instanceof UnmodifiableMdField) {
						Field newField = ((UnmodifiableMdField) tField).getMDField();
						newField.setDefaultValue(UFBoolean.TRUE);
						masterDs.getFieldSet().updateField(newField.getId(), newField);
					} else {
						tField.setDefaultValue(UFBoolean.TRUE);
					}
				}
			}
		}
		
		//����֧������
		Field field = masterDs.getFieldSet().getField(JKBXHeaderVO.ZY);
		if (field != null) {
			field.setExtendAttribute(Field.MAX_LENGTH, "256"); //�޸���󳤶ȣ���ֹ����ϳ�ʱУ�����
		}
		RefNode zyRef  = (RefNode)mainWidget.getViewModels().getRefNode("refnode_bxzb_zy_summaryname");
		if (zyRef == null) {
			zyRef  = (RefNode)mainWidget.getViewModels().getRefNode("refnode_jkzb_zy_summaryname");

		}
		if (zyRef!=null) {
			zyRef.setReadFields("summaryname,summaryname"); //pk_summary,summaryname -> summaryname,summaryname
			masterDs.getFieldRelations().removeFieldRelation("zy_mc_rel");
			masterDs.getFieldRelations().removeFieldRelation("zy_rel");
			zyRef.setAllowInput(true);
		}
		
		
		//Ϊ����pluginplugin_exetask����ύ����
		List<EventConf> eventConfList = mainWidget.getEventConfList();
		if (eventConfList!= null) {  //��������Ӧ�ö�����һ��wd��������
			EventConf eventConf =eventConfList.get(0);
			if (eventConf!= null && "pluginplugin_exetask".equals(eventConf.getMethodName())) {
				eventConf.setName("approvePlugin");//������ƣ�����addWebElementEvent��ᱨ�쳣
				addWebElementEvent(mainWidget, "approvePlugin", "pluginplugin_exetask",null);
			}
		}
		
		//�޸�����ds�¼��ύ����
		addWebElementEvent(masterDs, "onAfterDataChange", "onAfterZBDataChange","nc.uap.lfw.core.event.conf.DatasetListener");
		addWebElementEvent(masterDs, "onDataLoad", "onDataLoad","nc.uap.lfw.core.event.conf.DatasetListener");
		addWebElementEvent(masterDs, "onAfterRowSelect", "onAfterRowSelect","nc.uap.lfw.core.event.conf.DatasetListener");
		
		//Ϊ�ӱ�ds�����Ӧ�¼�   ��Ҫ��Ϊ�˸����Ի�����µ�ҵ��ҳǩ�����Ӧ���¼�
		Dataset[] busitemDss = ExpUtil.getBusitemDss(mainWidget, getDatasetID());
		if (busitemDss != null) {
			for (Dataset busitemDs:busitemDss) {
				addWebElementEvent(busitemDs, "onAfterDataChange", "onAfterBusitemDsChange","nc.uap.lfw.core.event.conf.DatasetListener");
				addWebElementEvent(busitemDs, "onAfterRowDelete", "onAfterBusitemDsRowDelete","nc.uap.lfw.core.event.conf.DatasetListener");
				addWebElementEvent(busitemDs, "onAfterRowInsert", "onAfterBusitemDsRowInsert","nc.uap.lfw.core.event.conf.DatasetListener");
			}
		}
		
		
		//TODO �޸�PK_COSTSHARE�ֶΣ�����Ϊnull�������ύʱ��У�鲻��
		Dataset cShareDs = mainWidget.getViewModels().getDataset(IExpConst.BX_CSHARE_DS_ID);
		if (cShareDs!=null) {
			Field cShareField = cShareDs.getFieldSet().getField(CShareDetailVO.PK_COSTSHARE);
			if (cShareField != null) {
				if (cShareField instanceof UnmodifiableMdField) {
					Field newField = ((UnmodifiableMdField) cShareField).getMDField();
					newField.setNullAble(true);
					cShareDs.getFieldSet().updateField(newField.getId(), newField);
				} else {
				    cShareField.setNullAble(true);
				}
			}
		}
		//����������
		String bxdj= (String) AppUtil.getAppAttr(IExpConst.EXP_REIMRULE_FLAG);
		EventConf eventConf = masterDs.getEventConf("onAfterDataChange", "onAfterZBDataChange");
		//ֻ������ָ����ֵ�����仯��Ż���ú�̨onAfterZBDataChange����
		String[] changFields = {JKBXHeaderVO.BBHL,JKBXHeaderVO.BZBM,JKBXHeaderVO.DEPTID,JKBXHeaderVO.DEPTID_V,JKBXHeaderVO.DJRQ,
				JKBXHeaderVO.DWBM,JKBXHeaderVO.DWBM_V,JKBXHeaderVO.FYDEPTID_V,JKBXHeaderVO.FYDWBM,JKBXHeaderVO.FYDWBM_V,
				JKBXHeaderVO.ISCOSTSHARE,JKBXHeaderVO.ISEXPAMT,JKBXHeaderVO.JKBXR,JKBXHeaderVO.PK_ORG,JKBXHeaderVO.PK_ORG_V,
				JKBXHeaderVO.PK_PAYORG_V,JKBXHeaderVO.PK_PCORG_V,JKBXHeaderVO.RECEIVER,JKBXHeaderVO.SZXMID,JKBXHeaderVO.TOTAL,
				JKBXHeaderVO.YBJE,JKBXHeaderVO.JOBID,JKBXHeaderVO.HBBM,JKBXHeaderVO.CUSTOMER,JKBXHeaderVO.PROJECTTASK,
				JKBXHeaderVO.PK_PCORG,JKBXHeaderVO.PK_CHECKELE,JKBXHeaderVO.PK_RESACOSTCENTER,JKBXHeaderVO.FYDEPTID,
				JKBXHeaderVO.CASHPROJ,JKBXHeaderVO.FREECUST,JKBXHeaderVO.GROUPBBHL,JKBXHeaderVO.GLOBALBBHL,/*JKBXHeaderVO.ISCUSUPPLIER,*/
				JKBXHeaderVO.CUSTACCOUNT,JKBXHeaderVO.SKYHZH,JKBXHeaderVO.PAYTARGET};
		String changFieldStr = "";
		for (int i=0; i<changFields.length; i++) {
			changFieldStr += changFields[i];
			changFieldStr +=",";
		}
		changFieldStr = changFieldStr.substring(0, changFieldStr.length()-1);
		if (bxdj!=null && "Y".equals(bxdj)) {
			Map<String,String> ReimRuleMap = (Map<String, String>) LfwRuntimeEnvironment.getWebContext().getRequest().getSession().getAttribute("yer_ReimRuleHeadMap");
			if(ReimRuleMap!=null  && ReimRuleMap.size()>0) {
				Set<String> keySet1 = ReimRuleMap.keySet();
				for (String key : keySet1) {
					changFieldStr += ",";
					changFieldStr+=key;
				}
			}
		}
		LfwParameter parameter = new LfwParameter(EventConf.PARAM_DATASET_FIELD_ID, changFieldStr);  
		eventConf.getExtendParamList().add(parameter);
		eventConf.addExtendParam(parameter);
		
		String busitemDsID = "";
		if (IExpConst.BXZB_DS_ID.equals(getDatasetID())) {
			busitemDsID = IExpConst.BX_BUSITEM_DS_ID;
		} else {
			busitemDsID = IExpConst.JK_BUSITEM_DS_ID;
		}
		
		
		Map<String,String> formularMap = (HashMap<String,String>)LfwRuntimeEnvironment.getWebContext().getRequest().getSession().getAttribute("yer_formularMap");
		Set<String> keySet = formularMap.keySet();
		String busitemChangFieldStr = "amount,ybje,pk_reimtype,jobid,pk_pcorg,pk_pcorg_v,dwbm,deptid,jkbxr,paytarget,receiver,skyhzh,hbbm,customer,custaccount";
		for (String key : keySet) {
			busitemChangFieldStr += ",";
			busitemChangFieldStr+=key;
		}
		
		if (bxdj!=null && "Y".equals(bxdj)) {
			Map<String,String> ReimRuleMap = (Map<String, String>) LfwRuntimeEnvironment.getWebContext().getRequest().getSession().getAttribute("yer_ReimRuleBusitemMap");
			if(ReimRuleMap!=null  && ReimRuleMap.size()>0) {
				Set<String> keySet1 = ReimRuleMap.keySet();
				for (String key : keySet1) {
					busitemChangFieldStr += ",";
					busitemChangFieldStr+=key;
				}
			}
		}
		Dataset busitemDs = mainWidget.getViewModels().getDataset(busitemDsID);
		EventConf busitemDsChangeEventConf = busitemDs.getEventConf("onAfterDataChange", "onAfterBusitemDsChange");
		LfwParameter busitemParameter = new LfwParameter(EventConf.PARAM_DATASET_FIELD_ID, busitemChangFieldStr);  
		busitemDsChangeEventConf.getExtendParamList().add(busitemParameter);
		busitemDsChangeEventConf.addExtendParam(busitemParameter);
		
		if (cShareDs!=null) {
			EventConf cShareDsChangeEventConf = cShareDs.getEventConf("onAfterDataChange", "onAfterCshareDsChange");
			LfwParameter cShareDsParameter = new LfwParameter(EventConf.PARAM_DATASET_FIELD_ID, "assume_org,assume_dept,assume_amount,jobid,pk_pcorg,bbhl,groupbbhl,globalbbhl");  
			if( cShareDsChangeEventConf !=null ) {
				cShareDsChangeEventConf.addExtendParam(cShareDsParameter);
			}
		}
		//����Ԥ��ռ���ڼ���ϸ
		UIFlowvPanel tbbdetailpanel = ExpUtil.getUIFlowvPanel(IExpConst.TBB_DETAIL_VPANEL); 
		if (tbbdetailpanel != null ) {
			tbbdetailpanel.setVisible(false);
		}
		
	}
	
	
	private void moidfyOtherViews(LfwWindow pageMeta) {
		
		/**
		 * �޸Ĳ˵���ť���ύ������Ҫ��Ϊ�˸��Ի����ú�����µ��ӱ�ds����Ӧ�Ĳ˵���Ӧ�Ĳ����ܹ����¼ӵ��ӱ�ds������
		 */
		LfwView jkbxMenuWidget = null;
		if ("Y".equals((String)AppUtil.getAppAttr(IExpConst.IS_ADJUST))) {
			jkbxMenuWidget =  pageMeta.getView(IExpConst.FYADJUST_MENU_VIEW);
		} else {
			jkbxMenuWidget = pageMeta.getView(IExpConst.BX_MENU_VIEW);
			if (jkbxMenuWidget == null) {
				jkbxMenuWidget = pageMeta.getView(IExpConst.JK_MENU_VIEW);
			}
		}
		MenubarComp jkbxMenubar;
		if (IExpConst.JK_MENU_VIEW.equals(jkbxMenuWidget.getId())) {
			jkbxMenubar = jkbxMenuWidget.getViewMenus().getMenuBar("jkzb_menu");
		} else {
			jkbxMenubar = jkbxMenuWidget.getViewMenus().getMenuBar("bxzb_menu");
		}
		addWebElementEvent(jkbxMenubar.getItem(IExpConst.MENU_COMMIT), "onclick", "commit","nc.uap.lfw.core.event.conf.MouseListener");//�ύ
		addWebElementEvent(jkbxMenubar.getItem(IExpConst.MENU_SAVE), "onclick", "tempSave","nc.uap.lfw.core.event.conf.MouseListener");//����
		addWebElementEvent(jkbxMenubar.getItem(IExpConst.MENU_PRINT), "onclick", "print","nc.uap.lfw.core.event.conf.MouseListener");//��ӡ
		addWebElementEvent(jkbxMenubar.getItem(IExpConst.MENU_COPY), "onclick", "copy","nc.uap.lfw.core.event.conf.MouseListener");//����
		//~

		//����ѯģ���menu view֮��� Connector 
		//--V631
		addInlineAdvqueryWinConnector(jkbxMenuWidget);
		//����ѯģ���fysqlist view֮��� Connector 
		LfwView fysqlistView = pageMeta.getView(IExpConst.YER_FYSQLIST_VIEW);
		addInlineAdvqueryWinConnector(fysqlistView);
		//��ѯģ���main view֮��� Connector 
		LfwView mainView = pageMeta.getView(IExpConst.EXP_WIDGET_ID);
		addInlineAdvqueryWinConnector(mainView);
		//��ѯģ���hxyt view֮��� Connector 
		LfwView hxytView = pageMeta.getView(IExpConst.YER_HXYT_VIEW);
		addInlineAdvqueryWinConnector(hxytView);
		
		//--V631
		//���Է������뵥���ط��������б�
		if (fysqlistView!=null) {
			Dataset fysqzb = fysqlistView.getViewModels().getDataset("fysqzb");
			Field pk_tradetype_text = fysqzb.getFieldSet().getField(fysqzb.nameToIndex("pk_tradetype_text"));
			if(pk_tradetype_text != null){
				String billtypename = ExpUtil.getCurrentLangNameColumn("billtypename");
				String formular = "pk_tradetype_text->getColValue2(bd_billtype" + "," + billtypename + "," + "pk_billtypecode ,pk_tradetype,pk_group,pk_group)";
				pk_tradetype_text.setLoadFormular(formular);
			}
		}
		
		// �������Ĵ� ��Ӧ���صķ��������б�
		LfwView appRoveFysqInfoView = pageMeta.getView(IExpConst.APPROVE_FYSQ_INFO);
		if (appRoveFysqInfoView!=null) {
			Dataset fysqzb = appRoveFysqInfoView.getViewModels().getDataset(IExpConst.YER_FYSQLIST_HEAD_DS);
			Field pk_tradetype_text = fysqzb.getFieldSet().getField(fysqzb.nameToIndex("pk_tradetype_text"));
			if(pk_tradetype_text != null){
				String billtypename = ExpUtil.getCurrentLangNameColumn("billtypename");
				String formular = "pk_tradetype_text->getColValue2(bd_billtype" + "," + billtypename + "," + "pk_billtypecode ,pk_tradetype,pk_group,pk_group)";
				pk_tradetype_text.setLoadFormular(formular);
			}
		}
		//����Ԥ�����
		if (hxytView!=null) {
			Dataset ytzb = hxytView.getViewModels().getDataset(IExpConst.EXP_HXZB_DS);
			Field pk_tradetype_text = ytzb.getFieldSet().getField(ytzb.nameToIndex("pk_tradetype_text"));
			if(pk_tradetype_text != null){
				String billtypename = ExpUtil.getCurrentLangNameColumn("billtypename");
				String formular = "pk_tradetype_text->getColValue2(bd_billtype" + "," + billtypename + "," + "pk_billtypecode ,pk_tradetype,pk_group,pk_group)";
				pk_tradetype_text.setLoadFormular(formular);
			}
		}
		
		LfwView quickShareView = pageMeta.getView("quickshare");
		
		if (quickShareView!=null) {
			NCRefNode quickShareRefNode = (NCRefNode)quickShareView.getViewModels().getRefNode("refnode_quickshare_rule");
			if (quickShareRefNode != null) {
				quickShareRefNode.setDataListener(ShareRuleRefListener.class.getName());
			}
		}
	}
	
	private void dealOpenfromXYZ(LfwWindow pageMeta, UIMeta uiMeta) {
		
		Logger.info("nc.bs.er.exp.model.ERPageModel.dealOpenfromXYZ start");
		//Start by changzhx Ӱ�����,�򿪵�����ϸ��Ϣҳ��ʱ������ʾ�˵�����������Ϣ
		String pageFlag = LfwRuntimeEnvironment.getWebContext().getParameter("sourcePage");
		AppUtil.addAppAttr("pageFlag", pageFlag);
		
		Logger.info("pageFlag=" + pageFlag);
		
		//��ΪMAKEBILLʱ������ǲ��صĵ���
		String actionType = LfwRuntimeEnvironment.getWebContext().getParameter("actiontype");
		if(actionType==null){
			
		};
		AppUtil.addAppAttr("actionType", actionType);
		
		//����ҵƽ̨��
		String isfromssc = LfwRuntimeEnvironment.getWebContext().getParameter("isfromssc");
	    AppUtil.addAppAttr("isfromssc", isfromssc);
		//����ҵƽ̨�� �ܷ�༭
		String iseditssc = LfwRuntimeEnvironment.getWebContext().getParameter("iseditssc");
		
		LfwView menuView = YerMenuUtil.getMenuView("jkbx");
		try {
		
			boolean canModify = YerMenuUtil.getMenuPermission("save", menuView);
			String state = (String) AppUtil.getAppAttr(WfmConstants.WfmAppAttr_NCState);
			//�������򿪵ĵ��ݲ��ܱ༭
			if(WfmTaskStatus.State_End.equals(state)) {
				AppUtil.addAppAttr("canModify", false);
			}else {
				AppUtil.addAppAttr("canModify", canModify);
			}
			
			if("N".equals(iseditssc)) {
				AppUtil.addAppAttr("canModify", false);
			}
		} catch (Exception e) {
			 LfwLogger.error(e);
		     throw new LfwRuntimeException(e.getMessage());
		}
		
		if (actionType!=null && pageFlag!=null && "workflow".equals(pageFlag) && "MAKEBILL".equals(actionType)) {  //���ص��Ƶ��˵ĵ��ݿ��޸�
			AppUtil.addAppAttr("canModify", true);
		}
		
		
		
		UIFlowvLayout flowvLayout = (UIFlowvLayout)uiMeta.getElement();
		List<UILayoutPanel> list = flowvLayout.getPanelList();
		Logger.info("pagelist=" + (list == null ? "" : list.toString()));
		
		if(pageFlag != null && "image".equals(pageFlag)){
			List<UILayoutPanel> removePanelList = new ArrayList<UILayoutPanel>();
			for(UILayoutPanel panel : list){
				UIView widget = (UIView)panel.getElement();
				if(widget == null){
					continue;
				}
				String id = widget.getId();
				//����ʾ������Ϣ,��ҳ����ɾ��
				if("pubview_exetask".equals(id)){
					removePanelList.add(panel);
				}
				
				//ֻ��ʾ��������ť
				if("bx_menu".equals(id) || "jk_menu".equals(id)){
					LfwView menuWidget = pageMeta.getView(id);
					
					MenubarComp[] menubars = menuWidget.getViewMenus().getMenuBars();
					for(MenubarComp menubar : menubars){
						List<MenuItem> menuList = menubar.getMenuList();
						List<MenuItem> removeMenuList = new ArrayList<MenuItem>();
						for (MenuItem item : menuList) {
							if(!"filemanager".equals(item.getId())){
								removeMenuList.add(item);
							}
						}
						
						for(MenuItem item : removeMenuList){
							menuList.remove(item);
						}
					}
				}
			}
			
			for(UILayoutPanel panel : removePanelList){
				flowvLayout.removePanel(panel);
			}

			
			LfwView mainWidget = pageMeta.getView("main");
			WebComponent[] components = mainWidget.getViewComponents().getComponents();
			//�鿴ģʽ�����ͱ�񶼲����Ա༭
			if (components!= null) {
				for (WebComponent com: components) {
					if (com instanceof FormComp) {
						((FormComp) com).setRenderType(6);
						((FormComp) com).setLabelMinWidth(153);
					}
					//���ر���Ҽ��ϰ�ť
					if (com instanceof GridComp) {
						((GridComp) com).setShowImageBtn(false);
						
						List<IGridColumn> columnList = ((GridComp) com).getColumnList();
						
						for(IGridColumn gridColumn : columnList){
							((GridColumn)gridColumn).setEditable(false);
						}
					}
				}
			}
		} 
		//End by changzhx Ӱ�����,�򿪵�����ϸ��Ϣҳ��ʱ������ʾ�˵�����������Ϣ
		
		
		LfwView menuWidget;
		MenubarComp menubar;
		
		if (ExpUtil.isFyadjust()) {
			menuWidget = getPageMeta().getWidget("fyadjust_menu");
			menubar = menuWidget.getViewMenus().getMenuBar("bxzb_menu");
		} else {
			menuWidget = getPageMeta().getWidget(IExpConst.BX_MENU_VIEW);
			if (menuWidget == null) {
				menuWidget = getPageMeta().getWidget(IExpConst.JK_MENU_VIEW);
			}
			if (IExpConst.BX_MENU_VIEW.equals(menuWidget.getId())) {
				menubar = menuWidget.getViewMenus().getMenuBar("bxzb_menu");
			} else {
				menubar = menuWidget.getViewMenus().getMenuBar("jkzb_menu");
			}
			
		}
		//���������ģ���Ӱ�����򿪵���ʱ����ʾ  ����view
		if (pageFlag == null || (!"workflow".equals(pageFlag) /*&& !"image".equals(pageFlag)*/)) { ////Ӱ��Ҳ�Ƴ�����
			for(int i =0;i<list.size();i++){
				UILayoutPanel panel = list.get(i);
				
				UIElement element = panel.getElement();
				if (element instanceof UIView) {
					UIView widget = (UIView)element;
					String id = widget.getId();
					if ("pubview_exetask".equals(id)) {
						flowvLayout.removePanel(panel);
						i--;
					}
					if ("approvefysqinfo".equals(id)) {
						flowvLayout.removePanel(panel);
						i--;
					}
					if ("workflow_task".equals(id)) {
						flowvLayout.removePanel(panel);
						i--;
					}
					
				}
			}
			
			MenuItem item  = menubar.getItem("save");
			if (item!=null) {  //�Ƶ�����save��ť
				item.setVisible(false);
			}
		}
		//�������Ĵ򿪵���    ���ز��ְ�ť
		if ("workflow".equals(pageFlag)) { 
			
			Integer workflowtype = -1;
			Object o = LfwRuntimeEnvironment.getWebContext().getParameter("workflowtype");
			if(o != null){
				workflowtype =Integer.parseInt((String)o);
			}
			
			if (999999==workflowtype) {
				AppUtil.addAppAttr("canModify", false);
			}
			
			String billID = LfwRuntimeEnvironment.getWebContext().getParameter("openBillId");
			
			AppUtil.addAppAttr(WfmConstants.WfmAppAttr_BillID, billID);
			AppUtil.addAppAttr("workflow_type", workflowtype);
			
			
			for(int i =0;i<list.size();i++){
				UILayoutPanel panel = list.get(i);
				UIElement element = panel.getElement();
				if (element instanceof UIView) {
					UIView widget = (UIView)element;
					String id = widget.getId();
					
					if(workflowtype != -1){
						
						if (WorkflowTypeEnum.Approveflow.getIntValue() == workflowtype || WorkflowTypeEnum.SubApproveflow.getIntValue() == workflowtype || WorkflowTypeEnum.SubWorkApproveflow.getIntValue() == workflowtype){
							if ("workflow_task".equals(id)) {
								flowvLayout.removePanel(panel);
								Logger.info("remove pageId=" + id + " and workflowtype=" + workflowtype);
								i--;
							}
						} else {
							if ("pubview_exetask".equals(id)) {
								flowvLayout.removePanel(panel);
								Logger.info("remove pageId=" + id);
								i--;
							}
							
						}
						
						if ((actionType!=null && "MAKEBILL".equals(actionType)) || (isfromssc!=null && "Y".equals(isfromssc)) || (999999==workflowtype)) {  //�����Ƶ��� ������������  & ��ҵƽ̨�������������� & �Ƶ����յ�֪ͨ��Ϣ
							if ("workflow_task".equals(id) ||"pubview_exetask".equals(id)|| "approvefysqinfo".equals(id)) {
								flowvLayout.removePanel(panel);
								Logger.info("remove pageId=" + id + " and workflowtype=" + workflowtype + id + " and isfromssc=" + isfromssc + id + " and actionType=" + actionType);
								i--;
							}
						}
					}
					
				}
				
			}
			
			AppUtil.addAppAttr("pageFlag", "workflow");  //ExpMenuitemStateManager ���õ�
			
			//�Ƴ����水ť�������ύ��ť���Ƹ�Ϊ���棬��ֹ������������ύ��ɻ���
			
			
//			MenuItem item  = menubar.getItem("commit");
//			if (item!=null) {
////				item.setText("����");//���ύ��ť���Ƹ�Ϊ����
//				item.setI18nName("p_bx_menu-000005");
//			}
			
			//�Ƴ�ԭ���水ť
			MenuItem item =  menubar.getItem("tempsave");
			List<MenuItem> menuList = menubar.getMenuList();
			menuList.remove(item);
			
			if(isfromssc==null){ //��ssc�򿪲��������߼�
				
				//����������ʾԤ����Ϣ
				doApproveYsinfo();
				doApproveFysqinfoDisplay( flowvLayout);
			}
			
		}
		
		Logger.info("nc.bs.er.exp.model.ERPageModel.dealOpenfromXYZ end");
	}
	
	/**
	 * ��Ի�������⴦��
	 */
	private void dealHKBill(LfwWindow pageMeta, UIMeta uiMeta) {
		
		LfwView jkbxMenuWidget = pageMeta.getView(IExpConst.BX_MENU_VIEW);
		if (jkbxMenuWidget == null) {
			jkbxMenuWidget = pageMeta.getView(IExpConst.JK_MENU_VIEW);
		}
		MenubarComp jkbxMenubar;
		if (IExpConst.BX_MENU_VIEW.equals(jkbxMenuWidget.getId())) {
			jkbxMenubar = jkbxMenuWidget.getViewMenus().getMenuBar("bxzb_menu");
		} else {
			jkbxMenubar = jkbxMenuWidget.getViewMenus().getMenuBar("jkzb_menu");
		}
		
		
		UIFlowvLayout flowvLayout = (UIFlowvLayout)uiMeta.getElement();
		List<UILayoutPanel> list = flowvLayout.getPanelList();
		
		if(list != null){
			for(UILayoutPanel panel : list){
				UIView widget = (UIView)panel.getElement();
				if (widget == null  ) {
					continue;
				}
				String id = widget.getId();
				if("main".equals(id)){
					UIMeta hkUiMeta = widget.getUimeta();
					
					UIFlowvLayout hkLayout = (UIFlowvLayout)hkUiMeta.getElement();
					
					List<UILayoutPanel> removePanelList = new ArrayList<UILayoutPanel>();
					List<UILayoutPanel> hkPanelList = hkLayout.getPanelList();
					
					for(UILayoutPanel hkPanel : hkPanelList){
						//������ϸ�����ڵ�Panel
						if("panelv10322".equals(hkPanel.getId())){
							removePanelList.add(hkPanel);
						}
					}
					for(UILayoutPanel ppp : removePanelList){
						hkLayout.removePanel(ppp);
					}
				}
			}
		}
		
		if(jkbxMenubar.getItem("addgroup") != null){
			//ȥ��"���Է������뵥"��"����"��ť
			List<MenuItem> menuList = (List<MenuItem>) jkbxMenubar.getItem("addgroup").getChildList();
			jkbxMenubar.getItem("addgroup").getChildList().removeAll(menuList);
			//��������ť����¼�������IDֵ��Ϊԭ��"����"��ť��IDֵ,���ں������״̬
			MenuItem  addgroup = jkbxMenubar.getItem("addgroup");
			addgroup.setId("add");
			addWebElementEvent(addgroup, "onclick", "add","nc.uap.lfw.core.event.conf.MouseListener");
		}
		MenuItem cjk = null;
		MenuItem split2 = jkbxMenubar.getItem("split2");
		MenuItem link = jkbxMenubar.getItem("link");
		MenuItem split3 = jkbxMenubar.getItem("split3");
		MenuItem print = jkbxMenubar.getItem("print");
		MenuItem invalid = jkbxMenubar.getItem("invalid");
		MenuItem query = jkbxMenubar.getItem("query");
		//ȥ������Ԥ�� ���ٷ�̯��ť
		if (jkbxMenubar.getItem("otheroperate") != null) {
			List<MenuItem> menuList = (List<MenuItem>) jkbxMenubar.getItem("otheroperate").getChildList();
			for (int i=0;i<menuList.size();i++) {
				MenuItem item = menuList.get(i);
				if ("cjk".equals(item.getId()) ) {
					cjk = menuList.get(i);
				}
			}
			jkbxMenubar.getItem("otheroperate").getChildList().removeAll(menuList);
		}
		//ȥ��ҵ����ť��
		MenuItem  otheroperate = jkbxMenubar.getItem("otheroperate");
		//���µ�����ť˳��
		jkbxMenubar.getMenuList().remove(split2);
		jkbxMenubar.getMenuList().remove(link);
		jkbxMenubar.getMenuList().remove(split3);
		jkbxMenubar.getMenuList().remove(print);
		jkbxMenubar.getMenuList().remove(invalid);
		jkbxMenubar.getMenuList().remove(query);
		jkbxMenubar.getMenuList().remove(otheroperate);
		jkbxMenubar.getMenuList().add(cjk);
		jkbxMenubar.getMenuList().add(split2);
		jkbxMenubar.getMenuList().add(link);
		jkbxMenubar.getMenuList().add(split3);
		jkbxMenubar.getMenuList().add(print);
		jkbxMenubar.getMenuList().add(invalid);
		jkbxMenubar.getMenuList().add(query);
		
	}
	
	
//	private void dealFyjz(LfwWindow pageMeta ,UIMeta uiMeta){
//		UIFlowvLayout flowvLayout = (UIFlowvLayout)uiMeta.getElement();
//		List<UILayoutPanel> list = flowvLayout.getPanelList();
//		if(list != null){
//			for(UILayoutPanel panel : list){
//				UIView widget = (UIView)panel.getElement();
//				if (widget == null  ) {
//					continue;
//				}
//				String id = widget.getId();
//				if("approvefysqinfo".equals(id)){
//					UIMeta jzUiMeta = widget.getUimeta();
//					
//					UIFlowvLayout jzLayout = (UIFlowvLayout)jzUiMeta.getElement();
//					
//					List<UILayoutPanel> removePanelList = new ArrayList<UILayoutPanel>();
//					List<UILayoutPanel> jzPanelList = jzLayout.getPanelList();
//					
//					for(UILayoutPanel jzPanel : jzPanelList){
//						//������ϸ�����ڵ�Panel
//						if("flovwPanel2".equals(jzPanel.getId())){
//							removePanelList.add(jzPanel);
//						}
//					}
//					for(UILayoutPanel ppp : removePanelList){
//						jzLayout.removePanel(ppp);
//					}
//				}
//			}
//		}
//	}
	
	/**
	 * ��ʱ���ã��޸�formԪ�ز��ɱ༭����������ύ����
	 */
	private void modifySubmitRule() {
		LfwView menuWidget = getPageMeta().getWidget(IExpConst.BX_MENU_VIEW);
		if (menuWidget == null) {
			menuWidget = getPageMeta().getWidget(IExpConst.JK_MENU_VIEW);
		}
		MenubarComp menubar;
		if (IExpConst.BX_MENU_VIEW.equals(menuWidget.getId())) {
			menubar = menuWidget.getViewMenus().getMenuBar("bxzb_menu");
		} else {
			menubar = menuWidget.getViewMenus().getMenuBar("jkzb_menu");
		}
		
		EventConf eventConfig = menubar.getItem("tempsave").getEventConf("onclick", "tempSave");

		EventSubmitRule submitRule = eventConfig.getSubmitRule();
		
		ViewRule wr =submitRule.getWidgetRule("main");
		
		FormRule fr = new FormRule();
		if (IExpConst.BX_MENU_VIEW.equals(menuWidget.getId())) {
			fr.setId(IExpConst.BXZB_BASE_INFO_FORM_ID);
		} else {
			fr.setId(IExpConst.JKZB_BASE_INFO_FORM_ID);
		}
		fr.setType(FormRule.ALL_CHILD);
		if (wr != null) {
			wr.addFormRule(fr);
		}
	
		
	
		
	}
	
	
	/**
	 * ��ӱ��尴ť���ύ����  ��Ҫ��Ա�����׼��
	 * @param item
	 * @param methodName
	 * @param DsId
	 */
	private void addBodyMenuAubmitRule(MenuItem item, String methodName,String DsId){
		
		EventConf eventConfig = item.getEventConf("onclick", methodName);

		EventSubmitRule submitRule = eventConfig.getSubmitRule();
		if (submitRule == null) {
			submitRule = new EventSubmitRule();
			eventConfig.setSubmitRule(submitRule);
		}
		
		ViewRule wr = new ViewRule();
		wr.setId("main");
		
		DatasetRule dsRule = new DatasetRule();
		dsRule.setId(DsId);
		dsRule.setType(DatasetRule.TYPE_CURRENT_PAGE);
		
		wr.addDsRule(dsRule);
		submitRule.addWidgetRule(wr);
		
	}
	
	/**
	 * ��ȡ�������ݼ�ID
	 * @return
	 */
	public abstract String getDatasetID();
	
	/**
	 * ��ȡ�˵�view id
	 * @return
	 */
	public abstract String getMenuViewName();
	
	
	/**
	 * ���slaveFieldId��Ӧ���� ��RefNodeRelation
	 * @param masterField
	 * @param slaveFieldId
	 * @param meta
	 */
	private void addRefNodeRelation(String masterField, String slaveFieldId, LfwWindow meta) {
		Dataset masterDs = meta.getWidget(IExpConst.EXP_WIDGET_ID).getViewModels().getDataset(getDatasetID());
		if (masterDs.getFieldSet().getField(slaveFieldId) != null) {

			RefNodeRelation  rnr = new RefNodeRelation();
			rnr.setId("relation_"+masterField+"_"+slaveFieldId);
			rnr.setDetailRefNode("refnode_"+getDatasetID()+"_"+slaveFieldId+"_name");
			MasterFieldInfo mfi = new MasterFieldInfo();
			mfi.setDsId(getDatasetID());
			mfi.setFieldId(masterField);
			mfi.setFilterSql("1=1");  //�������˵Ļ� ǰ̨ ds.addReqParameter("relationWhereSql", refRelationSql); �м���һ��null��ֵ,��������
			//refscript.jsp  getFromCache   //���masterField û��ѡֵ,����ǰ̨�ŵ�relationWhereSql�е�sql�����ֹ������������ھ������listener�д���
			mfi.setNullProcess(MasterFieldInfo.IGNORE);
			rnr.addMasterFieldInfo(mfi);
			RefNodeRelations refNodeRelations = meta.getWidget("main").getViewModels().getRefNodeRelations();


			if (refNodeRelations != null) {
				refNodeRelations.addRefNodeRelation(rnr);
			} else {
				RefNodeRelations newRefNodeRelations  = new RefNodeRelations();
				meta.getWidget("main").getViewModels().setRefnodeRelations(newRefNodeRelations);
				newRefNodeRelations.addRefNodeRelation(rnr);
			}



		}

	}
	
	
	private  void initExpGridMenubar(GridComp gc){
		MenubarComp menubarComp =  gc.getMenuBar();
		if (menubarComp ==null) {
			return;
		}
		if (menubarComp.getMenuList().size() > 4) {
			return;
		}
		String[] itemIds = new String[]{"new_row", "delete_row", "insert_row", "copy_row", "paste_row"};
//		String[] itemCaptions = new String[]{"������", "������", "ճ����"};
//		String[] itemCaptions = new String[]{nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("yer","0E010YER-0031")/*@res "������"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("yer","0E010YER-0032")/*@res "������"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("yer","0E010YER-0033")/*@res "ճ����"*/};
//		String[] itemI18nNames = new String[]{"0E010YER-0031", "0E010YER-0032", "0E010YER-0033"};
		
//		String[] itemRealImgIcons = new String[]{"/portal/sync/yer/weberm/html/nodes/includecss/themes/webclassic/ermybill/images/insert_row.png", "/sync/yer/weberm/html/nodes/includecss/themes/webclassic/ermybill/images/copy_row.png", "/sync/yer/weberm/html/nodes/includecss/themes/webclassic/ermybill/images/paste_row.png"};
		String[] eventMethodNames = new String[]{"onGridAddClick","onGridDeleteClick","onGridInsertClick", "onGridCopyClick", "onGridPasteClick"};
		for(int i=0; i<itemIds.length; i++){
			MenuItem item = new MenuItem(itemIds[i]);
			item.setI18nName(itemIds[i]);
			item.setTipI18nName(itemIds[i]);
			item.setLangDir("lfwbuttons");
			
			item.setShowModel(MenuItem.SHOW_IMG);
			
//			item.setText(itemCaptions[i]);//����ʾ����
//			item.setI18nName(itemI18nNames[i]);
//			item.setTip(itemCaptions[i]);
//			item.setTipI18nName(itemI18nNames[i]);
//			item.setLangDir("yer");
//			item.setImgIcon(itemRealImgIcons[i]);
			EventConf event = new EventConf();
			event.setMethodName(eventMethodNames[i]);
//			event.setJsEventClaszz("nc.uap.lfw.core.event.conf.MouseListener");
			event.setOnserver(true);
			event.setName("onclick");
			item.addEventConf(event);
			gc.getMenuBar().addMenuItem(item);
			
			
			addBodyMenuAubmitRule(item, eventMethodNames[i], gc.getDataset());
		}
	}
	
	
	/**
	 * ��������ds����¼�������Ӧ���ӱ��ύ����ΪTYPE_CURRENT_PAGE
	 * @param masterDs
	 * @param eventName
	 * @param methodName
	 */
	private void modifyMasterDsSubmitRule(Dataset masterDs,String eventName, String methodName) {
		EventConf event = masterDs.getEventConf(eventName, methodName);
		if (event!=null) {
			EventSubmitRule submitRule = event.getSubmitRule();

			if (submitRule == null) {
				submitRule = new EventSubmitRule();
				event.setSubmitRule(submitRule);
			}
			
			ViewRule wr = submitRule.getWidgetRule("main");
			if (wr==null) {
			 wr = new ViewRule();
				wr.setId("main");
				submitRule.addWidgetRule(wr);
			}
			
			List<String> DsID = ExpUtil.getSlaveDsIDs(getPageMeta().getWidget("main"), masterDs.getId());
			for (String dsID : DsID ) {
				if (IExpConst.BX_CONTRAST_DS_ID.equals(dsID) || IExpConst.JK_CONTRAST_DS_ID.equals(dsID)) {
					continue;
				}
				
				DatasetRule dsRule = new DatasetRule();
				dsRule.setId(dsID);
				dsRule.setType(DatasetRule.TYPE_CURRENT_PAGE);
				wr.addDsRule(dsRule);
			}
			
		}
		
	}
	
	
	/**
	 * ΪWebElement���ָ�����¼����������Ӧ���ύ����
	 * @param webElement  ����Ϊ ds,menuitem,pagemeta  �ȵ�
	 * @param eventName
	 * @param methodName
	 * @param JsEventClaszz
	 */
	protected void addWebElementEvent(WebElement webElement,String eventName, String methodName,String JsEventClaszz) {
		
		if (webElement == null) {
			return;
		}
		
		EventConf event = webElement.getEventConf(eventName, methodName);
		if (event == null) {
			
			event = new EventConf();
			event.setMethodName(methodName);
//			event.setJsEventClaszz(JsEventClaszz);
			event.setOnserver(true);
			event.setName(eventName);
			webElement.addEventConf(event);
		}
		
		EventSubmitRule submitRule = event.getSubmitRule();

		if (submitRule == null) {
			submitRule = new EventSubmitRule();
			event.setSubmitRule(submitRule);
		}
		
		ViewRule wr = submitRule.getWidgetRule("main");
		if (wr==null) {
			wr = new ViewRule();
			wr.setId("main");
			submitRule.addWidgetRule(wr);
		}
		
		List<String> DsID = ExpUtil.getSlaveDsIDs(getPageMeta().getWidget("main"), getDatasetID());
		for (String dsID : DsID ) {
			DatasetRule dsRule = new DatasetRule();
			dsRule.setId(dsID);
			dsRule.setType(DatasetRule.TYPE_CURRENT_PAGE);
			wr.addDsRule(dsRule);
		}
		
	}
	
	
	/**
	 * ΪmenuItem���ָ�����¼����������Ӧ���ύ����
	 * @deprecated
	 * @param menuItem
	 * @param eventName
	 * @param methodName
	 */
	protected void addMenuItemEvent(MenuItem menuItem,String eventName, String methodName) {
		if (menuItem == null) {
			return;
		}
		
		EventConf event = menuItem.getEventConf(eventName, methodName);
		if (event == null) {
			event = new EventConf();
			event.setMethodName(methodName);
//			event.setJsEventClaszz("nc.uap.lfw.core.event.conf.MouseListener");
			event.setOnserver(true);
			event.setName(eventName);
			menuItem.addEventConf(event);
		}
		
		EventSubmitRule submitRule = event.getSubmitRule();

		if (submitRule == null) {
			submitRule = new EventSubmitRule();
			event.setSubmitRule(submitRule);
		}
		
		ViewRule wr = submitRule.getWidgetRule("main");
		if (wr==null) {
			wr = new ViewRule();
			wr.setId("main");
			submitRule.addWidgetRule(wr);
		}
		
		List<String> DsID = ExpUtil.getSlaveDsIDs(getPageMeta().getWidget("main"), getDatasetID());
		for (String dsID : DsID ) {
			DatasetRule dsRule = new DatasetRule();
			dsRule.setId(dsID);
			dsRule.setType(DatasetRule.TYPE_CURRENT_PAGE);
			wr.addDsRule(dsRule);
		}
		
	}
	
	
	/**
	 * Ϊҵ���б�����onLastCellEnter�¼��������в������
	 * @param widget
	 */
	private void addBusitemGridEvent(LfwView widget) {
		WebComponent[] wcs = widget.getViewComponents().getComponents();
		for (WebComponent webComp : wcs) {
			if (webComp instanceof GridComp) {
				String dsID = ((GridComp) webComp).getDataset();
				if (dsID == null) {
					continue;
				}
				if (IExpConst.BX_CONTRAST_DS_ID.equals(dsID) || IExpConst.JK_CONTRAST_DS_ID.equals(dsID)) {
					continue;
				}
				
				LfwParameter param = new LfwParameter("gridEvent", "nc.uap.lfw.core.event.GridEvent");
				EventConf eventConfig = new EventConf(GridEvent.ON_LAST_CELL_ENTER, param, null);
				eventConfig.setAsync(true);
//				eventConfig.setJsEventClaszz("nc.uap.lfw.core.event.conf.GridListener");
				eventConfig.setMethodName("lastCellEnter");
				eventConfig.setOnserver(true);
				EventSubmitRule submitRule = new EventSubmitRule();
				eventConfig.setSubmitRule(submitRule);

				((GridComp) webComp).addEventConf(eventConfig);
				
				
//				LfwParameter beforEditParam = new LfwParameter("gridCellEvent", "nc.uap.lfw.core.event.CellEvent");
//				EventConf beforEditEventConfig = new EventConf(GridCellEvent.BEFORE_EDIT, beforEditParam, null);
//				beforEditEventConfig.setAsync(true);
////				eventConfig.setJsEventClaszz("nc.uap.lfw.core.event.conf.GridListener");
//				beforEditEventConfig.setMethodName("busiGridBeforeEdit");
//				beforEditEventConfig.setOnserver(true);
//				EventSubmitRule beforeEditSubmitRule = new EventSubmitRule();
//				beforEditEventConfig.setSubmitRule(beforeEditSubmitRule);
//
//				((GridComp) webComp).addEventConf(beforEditEventConfig);
				
				
				
			}
		}
	}
	
	
	/**
	 * Ϊָ����view��������߼���ѯwindowConnector
	 * @param targetView
	 */
	private void addInlineAdvqueryWinConnector (LfwView targetView){
		if (targetView == null) {
			return;
		}
		PluginDesc pluginDesc = new PluginDesc();
		pluginDesc.setId("conditionQueryPlugin");
		targetView.addPluginDescs(pluginDesc);
		pluginDesc.setMethodName("conditionQueryPlugin");
		
		
		WindowConfig winConf = new WindowConfig();
		winConf.setCaption(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("yer","yer_seniorQuery")/*@res "�߼���ѯ"*/);
		winConf.setId(SimpleQueryController.ADVANCED_QUERY_NODE);
		targetView.addInlineWindow(winConf);
		
		Connector conn = new Connector();
		conn.setId("adv_simple_conn");
		conn.setConnType(Connector.INLINEWINDOW_VIEW);
		conn.setPluginId("conditionQueryPlugin");
		conn.setPlugoutId(IPluginDesc.PROXY_PRE + "qryout");
		conn.setSource(winConf.getId());
		conn.setTarget(targetView.getId());
		targetView.addConnector(conn);
	}
	
	
	private void saveBusitemDsFormular(LfwView view) {
		
		String busitemDsID = "";
		if (IExpConst.BXZB_DS_ID.equals(getDatasetID())) {
			busitemDsID = IExpConst.BX_BUSITEM_DS_ID;
		} else {
			busitemDsID = IExpConst.JK_BUSITEM_DS_ID;
		}
		
		Map<String,String> formularMap = new HashMap<String,String>();
		Dataset ds = view.getViewModels().getDataset(busitemDsID);
		List<Field> fieldList = ds.getFieldSet().getFieldList();
		for (Field f : fieldList) {
			String editFormular = f.getEditFormular();
			if (editFormular !=null ) {
//			if (editFormular !=null && editFormular!=null && editFormular.startsWith("amount->")) {
				formularMap.put(f.getId(), editFormular);
			}
			
		}
		
		LfwRuntimeEnvironment.getWebContext().getRequest().getSession().setAttribute("yer_formularMap", formularMap);
		
	}
	
	
	/***====================����������ʾԤ����Ϣ����begin========================****/
	 
	
//	public void doApproveYsinfo(UIFlowvLayout flowvLayout) {
//		String djdl = IExpConst.BXZB_DS_ID.equals(getDatasetID())?BXConstans.BX_DJDL:BXConstans.JK_DJDL;
//		String billId  = LfwRuntimeEnvironment.getWebContext().getOriginalParameter(UifDatasetLoadCmd.OPEN_BILL_ID);
//
//		if (billId == null || "".equals(billId)) {
//			return;
//		}
//
//
//		JKBXVO bxvo = null;
//		try {
//			List<JKBXVO> bxvos = getIBXBillPrivate().queryVOsByPrimaryKeys(new String[]{billId},djdl);
//			if(bxvos == null || bxvos.size() == 0)
//				return;
//			bxvo = bxvos.get(0);
//		} catch (ComponentException e1) {
//			Logger.error(e1.getMessage(), e1);
//			throw new LfwRuntimeException(e1);
//		} catch (BusinessException e1) {
//			Logger.error(e1.getMessage(), e1);
//			throw new LfwRuntimeException(e1);
//		}
//
//		if (bxvo == null) {
//			return;
//		}
//		
//
//		//boolean istbbused = BXUtil.isProductInstalled(bxvo.getParentVO().getPk_group(),BXConstans.TBB_FUNCODE);
//		boolean istbbused = ErUtil.isProductTbbInstalled(IFysqConst.TBB_FUNCODE);
//		if(!istbbused){
//			return;
//		} else {
//
//			String actionCode = getActionCode(bxvo);
//			LinkNtbParamVO[] linkNtbParamVO = null;
//			
//			
//			
//			try {
//				IFYControl[] items = null;
//				// ���з���Ԥ������
//				if (bxvo.getcShareDetailVo() != null && bxvo.getcShareDetailVo().length != 0) {
//					// ��ѯ���ý�ת��vo
//					AggCostShareVO[] csVos = NCLocator.getInstance().lookup(IErmCostShareBillQuery.class)
//							.queryBillByWhere(CostShareVO.SRC_ID + "='" + bxvo.getParentVO().getPk_jkbx() + "'");
//					
//					if (csVos != null) {
//						for (AggCostShareVO csVo : csVos) {
//							if (((CostShareVO) csVo.getParentVO()).getBillstatus() == BXStatusConst.DJZT_TempSaved) {
//								((CostShareVO) csVo.getParentVO()).setBillstatus(BXStatusConst.DJZT_Saved);
//							}
//						}
//						items = ErCostBudgetUtil.getCostControlVOByCSVO(csVos, AuditInfoUtil.getCurrentUser());
//					}
//				}else {
//					
//					JKBXHeaderVO[] jkbxItems = ErVOUtils.prepareBxvoItemToHeaderClone(bxvo);
//					for (JKBXHeaderVO vo : jkbxItems) {
//						if (vo.getShrq() == null) {
//							vo.setShrq(new UFDateTime());
//						}
//					}
//					items = jkbxItems;
//				}
//				//items Ϊnull���߿�ʱ����ǰ�û�û��Ȩ�޲�ѯԤ������
//				if (items != null && items.length > 0) {
//					// ����Ԥ��ӿڲ�ѯ���Ʋ��ԡ��������ֵΪ�ձ�ʾ�޿��Ʋ��ԣ������ơ����һ������Ϊfalse�������Ͳ���������β���
//					DataRuleVO[] ruleVos = NCLocator.getInstance().lookup(IBudgetControl.class)
//							.queryControlTactics(items[0].getDjlxbm(), actionCode, false);
//					
//					if (ruleVos == null || ruleVos.length == 0) {
//						return;
//					}
//					List<FiBillAccessableBusiVOProxy> voProxys = new ArrayList<FiBillAccessableBusiVOProxy>();
//					
//					YsControlVO[] controlVos =  ErBudgetUtil.getCtrlVOs(items, true, ruleVos);
//					
//					if (controlVos != null) {
//						for (YsControlVO vo : controlVos) {
//							voProxys.add(getFiBillAccessableBusiVOProxy(vo, vo.getParentBillType()));
//						}
//					}
//					
//					NtbParamVO[] vos = ErmProxy.getILinkQuery().getLinkDatas(voProxys.toArray(new IAccessableBusiVO[] {}));
//					
//					if (null == vos || vos.length == 0) {
//						return;
//					}
//					linkNtbParamVO = convert2WebbxVo(vos);
//					
////				getGlobalContext().getWebContext().getRequest().getSession().setAttribute("link_ntb_vos", linkNtbParamVO);
//					AppUtil.addAppAttr("link_ntb_vos_Ysinfo", linkNtbParamVO);
//					modifyMeta();
//				}else {
//					//û��Ȩ�޲�ѯԤ�����ݵ��û���Ԥ������չʾ
//					UIView mainUIView = (UIView) flowvLayout.getElementFromPanel("approvefysqinfo");
//					UIMeta mainUIMeta = mainUIView.getUimeta();
//					UIFlowvLayout mainFlowvLayout = (UIFlowvLayout) mainUIMeta.getElement();
//					List<UILayoutPanel> panelList = mainFlowvLayout.getPanelList();
//					UIFlowvPanel tempVpanel = null;
//					for (UILayoutPanel panel : panelList) {
//						UIFlowvPanel vpanel = (UIFlowvPanel) panel;
//						if ("approveinfo_ys_fysq".equals(vpanel.getId())) {
//							tempVpanel = vpanel;
//							break;
//						}
//					}
//					UIFlowvLayout tUIFlowvLayout = (UIFlowvLayout) tempVpanel.getElement();
//					List<UILayoutPanel> tPanelList = tUIFlowvLayout.getPanelList();
//
//					UIFlowvPanel tUIFlowvPanel = null;
//					for (UILayoutPanel panel : tPanelList) {
//						UIFlowvPanel vpanel = (UIFlowvPanel) panel;
//						if ("panelv04542".equals(vpanel.getId())) {
//							tUIFlowvPanel = vpanel;
//							break;
//						}
//					}
//					tUIFlowvPanel.setVisible(false);
//				}
//			} catch (Exception e1) {
//				Logger.error(e1.getMessage(), e1);
//				if (e1 instanceof LfwRuntimeException) {
//					throw (LfwRuntimeException)e1;
//				} else {
//					throw new LfwRuntimeException(e1);
//				}
//			}
//		}
//
//	}
	
	
	public void doApproveYsinfo() {


		String djdl = IExpConst.BXZB_DS_ID.equals(getDatasetID())?BXConstans.BX_DJDL:BXConstans.JK_DJDL;
		String billId  = LfwRuntimeEnvironment.getWebContext().getOriginalParameter(UifDatasetLoadCmd.OPEN_BILL_ID);

		if (billId == null || "".equals(billId)) {
			return;
		}


		JKBXVO bxvo = null;
		try {
			List<JKBXVO> bxvos = getIBXBillPrivate().queryVOsByPrimaryKeys(new String[]{billId},djdl);
			if(bxvos == null || bxvos.size() == 0)
				return;
			bxvo = bxvos.get(0);
		} catch (ComponentException e1) {
			Logger.error(e1.getMessage(), e1);
			throw new LfwRuntimeException(e1);
		} catch (BusinessException e1) {
			Logger.error(e1.getMessage(), e1);
			throw new LfwRuntimeException(e1);
		}

		if (bxvo == null) {
			return;
		}
		

		//boolean istbbused = BXUtil.isProductInstalled(bxvo.getParentVO().getPk_group(),BXConstans.TBB_FUNCODE);
		boolean istbbused = ErUtil.isProductTbbInstalled(IFysqConst.TBB_FUNCODE);
		if(!istbbused){
			return;
		} else {

			String actionCode = getActionCode(bxvo);
			LinkNtbParamVO[] linkNtbParamVO = null;
			
			
			
			try {
			
				NtbParamVO[] vos = NCLocator.getInstance().lookup(IErmLinkBudgetService.class).getBudgetLinkParams(bxvo, actionCode, null);

				if (null == vos || vos.length == 0) {
					return;
				}
		
				linkNtbParamVO = convert2WebbxVo(vos);

//				getGlobalContext().getWebContext().getRequest().getSession().setAttribute("link_ntb_vos", linkNtbParamVO);
				AppUtil.addAppAttr("link_ntb_vos_Ysinfo", linkNtbParamVO);
				modifyMeta();
			} catch (Exception e1) {
				Logger.error(e1.getMessage(), e1);
				if (e1 instanceof LfwRuntimeException) {
					throw (LfwRuntimeException)e1;
				} else {
					throw new LfwRuntimeException(e1);
				}
			}
		}

	}
	
	private IBXBillPrivate getIBXBillPrivate()throws ComponentException{
	      return ((IBXBillPrivate) NCLocator.getInstance().lookup(IBXBillPrivate.class.getName()));
	}
	private String getActionCode(JKBXVO bxvo) {
		JKBXHeaderVO headVO = bxvo.getParentVO();
		int billStatus = headVO.getDjzt();
		switch (billStatus) {
			case BXStatusConst.DJZT_Sign:
				return BXConstans.ERM_NTB_APPROVE_KEY;
			case BXStatusConst.DJZT_Verified:
				return BXConstans.ERM_NTB_APPROVE_KEY;
			default:
				return BXConstans.ERM_NTB_SAVE_KEY;
		}
	}
//	private FiBillAccessableBusiVOProxy getFiBillAccessableBusiVOProxy(FiBillAccessableBusiVO vo, String parentBillType) {
//		FiBillAccessableBusiVOProxy voProxy;
////		voProxy = new FiBillAccessableBusiVOProxy(vo, parentBillType);
//		voProxy = new FiBillAccessableBusiVOProxy(vo);
//		return voProxy;
//	}
	
	/**
	 * ת��Ϊweb����VO
	 * @param vos
	 * @return
	 */
	private LinkNtbParamVO[] convert2WebbxVo(NtbParamVO[] vos) {
		LinkNtbParamVO[] wvos = new LinkNtbParamVO[vos.length];
		//��ȡ��ʾ���������Ա���������ʾʱ��ȫ�����س���
		int temp=0;
		NtbParamVO tempVo=null;
		for(int i=0;i<vos.length;i++){
			if (vos[i].getPkDim().length>temp) {
				tempVo = vos[i];
				temp= vos[i].getPkDim().length;
			}
		}
		AppUtil.addAppAttr("discdim_most", tempVo);
		for(int i=0;i<vos.length;i++){
			LinkNtbParamVO lnvo = new LinkNtbParamVO();
			lnvo.setBegindate(vos[i].getBegDate());
			lnvo.setEnddate(vos[i].getEndDate());
			int currtype = vos[i].getCurr_type();
			UFDouble runvalue = vos[i].getRundata()[currtype];
			UFDouble readyvalue = vos[i].getReadydata()[currtype];
			lnvo.setRundata(runvalue.setScale(2,nc.vo.pub.lang.UFDouble.ROUND_UP));//��ʾִ����,ֻ������λС��
			lnvo.setReadydata(readyvalue.setScale(2,nc.vo.pub.lang.UFDouble.ROUND_UP));//��ʾԤռ��,ֻ������λС��
			lnvo.setPlanname(vos[i].getPlanname());
			lnvo.setBalance(vos[i].getBalance().setScale(2,nc.vo.pub.lang.UFDouble.ROUND_UP));//��ʾ���,ֻ������λС��

			lnvo.setPlandata(vos[i].getPlanData().setScale(2, nc.vo.pub.lang.UFDouble.ROUND_UP));

			//����pk
			String[] pkdim = vos[i].getPkDim();
			lnvo.setPkdim(pkdim);
			//��������ֵ
			String[] pkdiscdim = new String[pkdim.length];
			HashMap map = vos[i].getHashDescription();
			for(int k=0;k<pkdiscdim.length;k++){
				pkdiscdim[k] = (String) map.get(pkdim[k]);
			}
			lnvo.setPkdiscdim(pkdiscdim);

			lnvo.setTypedim(vos[i].getTypeDim());
			lnvo.setBusiAttrs(vos[i].getBusiAttrs());
			wvos[i] = lnvo;
		}
		return wvos;
	}
	
	/**
	 * ��̬�������
	 * @param wvos
	 * @param meta
	 */
	private void modifyMeta() {
		LfwWindow meta = LfwRuntimeEnvironment.getWebContext().getPageMeta();
		GridComp grid = (GridComp) meta.getWidget("approvefysqinfo").getViewComponents().getComponent("ntbGrid");
		Dataset ds = meta.getWidget("approvefysqinfo").getViewModels().getDataset("ntbDs");
		NtbParamVO tempVo = (NtbParamVO)AppUtil.getAppAttr("discdim_most");
		Map<String,String> typesMap = new HashMap<String,String>();
		if (tempVo !=null ) {
			String[] attrs = tempVo.getBusiAttrs();
			String[] types = tempVo.getTypeDim();
			int k=0;
			for(int i=0;i<attrs.length;i++){
				//ȥ���ظ�����
				if (typesMap.get(types[i]) !=null) {
					continue;
				}else {
					typesMap.put(types[i], types[i]);
				}
				String id = attrs[i].replace(".", "_");
				GridColumn col = new GridColumn();
				col.setId(id);
				col.setField(id);
				col.setEditorType("StringText");
				col.setDataType("String");
				col.setText(types[i]);
				
				RefInfoVO  vo = nc.ui.bd.ref.RefPubUtil.getRefinfoVO(types[i]);
				if(vo!=null){
					String path = vo.getResidPath();
					String resid = vo.getResid();
					col.setI18nName(resid);
					col.setLangDir(path);
				}
				//			col.setI18nName(types[i]);
				col.setWidth(80);
				col.setEditable(false);
				//����λ��
				grid.getColumnList().add(k, col);
				
				Field field = new Field();
				field.setId(id);
				field.setField(id);
				field.setDataType("String");
				ds.getFieldSet().addField(field);
				k++;
			}
		}
	}
	
	/***====================����������ʾԤ����Ϣ����end========================****/
	
	/***====================����������ʾ����������Ϣbegin========================****/
	
	public void doApproveFysqinfo() {
		
		List<String> fysqPkList = new ArrayList<String>();
		String pkStr = "";
		
		String djdl = IExpConst.BXZB_DS_ID.equals(getDatasetID())?BXConstans.BX_DJDL:BXConstans.JK_DJDL;
		String billId  = LfwRuntimeEnvironment.getWebContext().getOriginalParameter(UifDatasetLoadCmd.OPEN_BILL_ID);

		if (billId == null || "".equals(billId)) {
			return;
		}


		JKBXVO bxvo = null;
		try {
			List<JKBXVO> bxvos = getIBXBillPrivate().queryVOsByPrimaryKeys(new String[]{billId},djdl);
			if(bxvos == null || bxvos.size() == 0)
				return;
			bxvo = bxvos.get(0);
		} catch (ComponentException e1) {
			Logger.error(e1.getMessage(), e1);
			throw new LfwRuntimeException(e1);
		} catch (BusinessException e1) {
			Logger.error(e1.getMessage(), e1);
			throw new LfwRuntimeException(e1);
		}

		if (bxvo == null) {
			return;
		}
		BXBusItemVO[] busitemArr = bxvo.getBxBusItemVOS();
		if (busitemArr == null) {
			return;
		}
		for (int i=0; i<busitemArr.length;i++) {
			String pkfysq = busitemArr[i].getPk_item();
			if (pkfysq != null) {
				
				if (!fysqPkList.contains(pkfysq)) {
					
					fysqPkList.add(pkfysq);
					pkStr += ",,,"+pkfysq;
				}
			}
			
		}
		AppUtil.addAppAttr("Yer_Weberm_ApproveFysqinfo", pkStr);
		
	}
	
	/***====================����������ʾ����������Ϣend========================****/
	/**
	 * ���������������
	 */
	public void doApproveFysqinfoDisplay(UIFlowvLayout flowvLayout) {
		// ���õ�������չʾ�����������
		if (ExpUtil.isFyadjust()) {
			UIView mainUIView = (UIView) flowvLayout.getElementFromPanel("approvefysqinfo");
			if(mainUIView == null) return;
			UIMeta mainUIMeta = mainUIView.getUimeta();
			UIFlowvLayout mainFlowvLayout = (UIFlowvLayout) mainUIMeta.getElement();
			List<UILayoutPanel> panelList = mainFlowvLayout.getPanelList();
			UIFlowvPanel tempVpanel = null;
			for (UILayoutPanel panel : panelList) {
				UIFlowvPanel vpanel = (UIFlowvPanel) panel;
				if ("approveinfo_ys_fysq".equals(vpanel.getId())) {
					tempVpanel = vpanel;
					break;
				}
			}
			UIFlowvLayout tUIFlowvLayout = (UIFlowvLayout) tempVpanel.getElement();
			List<UILayoutPanel> tPanelList = tUIFlowvLayout.getPanelList();

			UIFlowvPanel tUIFlowvPanel = null;
			for (UILayoutPanel panel : tPanelList) {
				UIFlowvPanel vpanel = (UIFlowvPanel) panel;
				if ("flovwPanel2".equals(vpanel.getId())) {
					tUIFlowvPanel = vpanel;
					break;
				}
			}
			tUIFlowvPanel.setVisible(false);
		} else {
			doApproveFysqinfo();
		}
	}
	
	/**
	 * ��ȡ������׼������
	 */
	private void getReimRuleDataMap(Dataset masterDs) {
		Map<String,String> ReimRuleBusitemMap = new HashMap<String,String>();
		Map<String,String> ReimRuleHeadMap = new HashMap<String,String>();
		String billtype = (String)AppUtil.getAppAttr(IExpConst.DEFAULT_DJLXBM);
		String pk_org = (String)AppUtil.getAppAttr(IExpConst.PSN_PERMISSION_ORG);
//		try {// ���ݼ��ż�������������׼���ù���,��ȡ��֯
//			String PARAM_ER8 = SysInit.getParaString(ExpUtil.getPKGroup(), BXParamConstant.PARAM_ER_REIMRULE);
//			String[] str = {(String)AppUtil.getAppAttr(IExpConst.PSN_JKBXR),
//			        (String)AppUtil.getAppAttr(IExpConst.PSN_DEPT),
//			        (String)AppUtil.getAppAttr(IExpConst.PSN_PERMISSION_ORG),
//			        (String)AppUtil.getAppAttr(IExpConst.PSN_GROUP)};
//			if (PARAM_ER8 != null) {
//				if (PARAM_ER8.equals(BXParamConstant.ER_ER_REIMRULE_PK_ORG)) {
//					pk_org = str[2];
//				} else if (PARAM_ER8.equals(BXParamConstant.ER_ER_REIMRULE_OPERATOR_ORG)) {
//					pk_org = str[2];
//				} else if (PARAM_ER8.equals(BXParamConstant.ER_ER_REIMRULE_ASSUME_ORG)) {
//					pk_org = str[2];  
//				}
//			}
//		} catch (BusinessException e1) {
//			ExceptionHandler.consume(e1);
//		}
		if (pk_org ==null || pk_org.trim().length()<=0) {
			pk_org = (String) AppUtil.getAppAttr(IExpConst.PSN_NO_PERMISSION_ORG);
		}
		if (pk_org != null) {
			try {
				List<ReimRuleDimVO> vos= NCLocator.getInstance().lookup(IReimTypeService.class).queryReimDim(billtype,null,pk_org);
				List<ReimRulerVO> rules= NCLocator.getInstance().lookup(IReimTypeService.class).queryReimRuler(billtype,null,pk_org);
				
				//�ѽ�ʽ�е��ֶν�������,��ΪӰ���ֶ�
				FormulaParse fp = LfwFormulaParser.getInstance();
				Set<String> fomulas = new HashSet<String>();
				if(rules != null){
					for (ReimRulerVO rule : rules) {
						if(!StringUtils.isEmpty(rule.getControlformula())){
							fomulas.add(rule.getControlformula());
						}
					}
				}
				if(fomulas.size()>0){
					for (String formula : fomulas) {
						LfwFormulaParser.addExpr(formula, fp);
						VarryVO[] varryVOs = fp.getVarryArray();
						if(varryVOs != null){
							for (VarryVO varryVO : varryVOs) {
								if(varryVO != null){
									String[] varrays = varryVO.getVarry();
									if(varrays != null){
										for (String varray : varrays) {
											if(!StringUtils.isEmpty(varray)){
												ReimRuleBusitemMap.put(varray, varray);
											}
										}
									}
								}
							}
						}
					}
				}
				
				if (vos!=null ) {
					for (int i=0;i<vos.size();i++) {
						ReimRuleDimVO temp = vos.get(i);
						if (temp.getBillrefcode() !=null  && (temp.getBillrefcode().startsWith("er_busitem") 
								|| temp.getBillrefcode().startsWith("jk_busitem"))) {
							String billrefcode = temp.getBillrefcode();
							if (temp.getBillrefcode().startsWith("er_busitem")) {
								billrefcode = billrefcode.substring("er_busitem".length()+1);
							}
							if (temp.getBillrefcode().startsWith("jk_busitem")) {
								billrefcode = billrefcode.substring("jk_busitem".length()+1);
							}
							ReimRuleBusitemMap.put(billrefcode, billrefcode);
						}
						if ( temp.getBillrefcode() !=null  && ((masterDs.nameToIndex(temp.getBillrefcode()) >0 )
								|| (temp.getBillrefcode().startsWith("er_bxzb")) || (temp.getBillrefcode().startsWith("er_jkzb"))) ) {
							String billrefcode = temp.getBillrefcode();
							if (billrefcode.startsWith("er_bxzb")) {
								billrefcode = billrefcode.substring("er_bxzb".length()+1);
							}
							if (billrefcode.startsWith("er_jkzb")) {
								billrefcode = billrefcode.substring("er_jkzb".length()+1);
							}
//							String rr = billrefcode.substring("er_busitem".length()+1);
							ReimRuleHeadMap.put(billrefcode, billrefcode);
						}
					}
				}
			} catch (BusinessException e) {
				Logger.error(e.getMessage(), e);
			}
		}
		LfwRuntimeEnvironment.getWebContext().getRequest().getSession().setAttribute("yer_ReimRuleBusitemMap", ReimRuleBusitemMap);
		LfwRuntimeEnvironment.getWebContext().getRequest().getSession().setAttribute("yer_ReimRuleHeadMap", ReimRuleHeadMap);
		
	}
}
