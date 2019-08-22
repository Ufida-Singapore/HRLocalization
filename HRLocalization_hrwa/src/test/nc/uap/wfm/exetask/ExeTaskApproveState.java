package nc.uap.wfm.exetask;

import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.uap.cpb.log.CpLogger;
import nc.uap.cpb.org.exception.CpbBusinessException;
import nc.uap.cpb.org.itf.ICpSysinitQry;
import nc.uap.cpb.org.vos.CpSysinitVO;
import nc.uap.lfw.core.LfwRuntimeEnvironment;
import nc.uap.lfw.core.combodata.CombItem;
import nc.uap.lfw.core.combodata.DynamicComboDataConf;
import nc.uap.lfw.core.combodata.StaticComboData;
import nc.uap.lfw.core.common.StringDataTypeConst;
import nc.uap.lfw.core.comp.ButtonComp;
import nc.uap.lfw.core.comp.LabelComp;
import nc.uap.lfw.core.comp.LinkComp;
import nc.uap.lfw.core.comp.ListViewComp;
import nc.uap.lfw.core.comp.RadioGroupComp;
import nc.uap.lfw.core.comp.ReferenceComp;
import nc.uap.lfw.core.comp.TextAreaComp;
import nc.uap.lfw.core.comp.WebPartComp;
import nc.uap.lfw.core.comp.text.ComboBoxComp;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Field;
import nc.uap.lfw.core.event.ComboBoxEvent;
import nc.uap.lfw.core.event.DatasetEvent;
import nc.uap.lfw.core.event.FocusEvent;
import nc.uap.lfw.core.event.LinkEvent;
import nc.uap.lfw.core.event.MouseEvent;
import nc.uap.lfw.core.event.TextEvent;
import nc.uap.lfw.core.event.conf.EventConf;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.refnode.NCRefNode;
import nc.uap.lfw.core.refnode.SelfDefRefNode;
import nc.uap.lfw.core.uimodel.WindowConfig;
import nc.uap.lfw.jsp.uimeta.UIBorder;
import nc.uap.lfw.jsp.uimeta.UICardLayout;
import nc.uap.lfw.jsp.uimeta.UICardPanel;
import nc.uap.lfw.jsp.uimeta.UIConstant;
import nc.uap.lfw.jsp.uimeta.UIFlowhLayout;
import nc.uap.lfw.jsp.uimeta.UIFlowhPanel;
import nc.uap.lfw.jsp.uimeta.UIFlowvLayout;
import nc.uap.lfw.jsp.uimeta.UIFlowvPanel;
import nc.uap.lfw.jsp.uimeta.UILabelComp;
import nc.uap.lfw.jsp.uimeta.UILinkComp;
import nc.uap.lfw.jsp.uimeta.UIListViewComp;
import nc.uap.lfw.jsp.uimeta.UIMeta;
import nc.uap.lfw.jsp.uimeta.UIPanel;
import nc.uap.lfw.jsp.uimeta.UIPartComp;
import nc.uap.lfw.jsp.uimeta.UITextField;
import nc.uap.portal.plugins.PluginManager;
import nc.uap.portal.plugins.model.PtExtension;
import nc.uap.wfm.constant.WfmConstants;
import nc.uap.wfm.constant.WfmOperator;
import nc.uap.wfm.constant.WfmTaskStatus;
import nc.uap.wfm.pubview.CommonWordProvider;
import nc.uap.wfm.pubview.ExecuteTaskWidgetProvider;
import nc.uap.wfm.pubview.ITaskMessageTmp;
import nc.uap.wfm.utils.WfmBillUtil;
import nc.uap.wfm.utils.WfmClassUtil;
import nc.uap.wfm.utils.WfmProDefUtil;
import nc.uap.wfm.utils.WfmProinsUtil;
import nc.uap.wfm.utils.WfmPublicViewUtil;
import nc.uap.wfm.utils.WfmTaskUtil;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.lang.UFBoolean;
import uap.lfw.core.ml.LfwResBundle;
import uap.lfw.wfm.ncuserrefctrl.NcUserRefCtrl;
import uap.web.bd.pub.AppUtil;
/**
 * ����״̬����
 * @author zhaohb
 *
 */
public class ExeTaskApproveState extends ExeTaskBaseState{
	
	/**
	 * ����̬��widget
	 * @param widget
	 * @param taskPk
	 * @param isNC
	 */
	public void createApproveWidget(LfwView widget, String taskPk) {
		boolean isNC = WfmBillUtil.isNCBill();
		Object task = null;
		if(!isNC)
			task = WfmTaskUtil.getTaskFromSessionCache(taskPk);
		//������
		createRefNode(widget, isNC);
		//������ֵ�����������������ѡ��
		createComboData(widget, task, isNC);
		//�����������
		createComp(widget, task, isNC);
		//������ʷ
		createHistory2(widget);
		//�ɵ�������ʷ
		createHistory(widget);
	}
	/**
	 * ǰ��ǩ�����ǩ��Э���UIMeta
	 * @param widget
	 * @param task
	 * @param widgetId
	 * @param um
	 * @param isNC
	 */
	public void generateApproveSimpleUIMeta(LfwView widget, String taskPk,
			String widgetId, UIMeta um, boolean isNC) {
		UIFlowhLayout gflowh = new UIFlowhLayout();
		String state = null;
		try{
			state =  (String) AppUtil.getAppAttr(WfmConstants.WfmAppAttr_NCState);
		}catch (Exception e) {
			CpLogger.error(e.getMessage(),e);
		}
		gflowh.setAutoFill(0);
		gflowh.setId("gflowh");
		um.setElement(gflowh);
		{   
			UIFlowhPanel leftPaddingPanel = gflowh.addElementToPanel(null);
			leftPaddingPanel.setWidth("10");
		}
		{
			//������������
			//������������
			UIFlowhLayout bgFlowh = new UIFlowhLayout();
			UIPanel approvePanel=createApprovePanel(gflowh,bgFlowh,null);
			//����Ϊ�գ����Զ�����
			if(taskPk == null)
				approvePanel.setExpand(UIConstant.FALSE);
			if(!isNC){
				approvePanel.setRenderType("wfTitleRender"); 
				//approvePanel.setExpand(UIConstant.TRUE);
			}else {
				approvePanel.setRenderType(null); 
				approvePanel.setExpand(UIConstant.TRUE);
			}
			
			Object task = WfmTaskUtil.getTaskFromSessionCache(taskPk);
			if(taskPk != null){
				boolean isExpand=WfmProDefUtil.isExpand(WfmTaskUtil.getHumActByTask(task));
				if(WfmTaskUtil.isEndState(task) || WfmTaskUtil.isReadedState(task)||isExpand){
					approvePanel.setRenderType("wfTitleRender"); 
					approvePanel.setExpand(UIConstant.TRUE);
				}
				//���ݻȨ������
				if(WfmTaskUtil.isOpinionNeed(task)){
					approvePanel.setExpand(UIConstant.TRUE);
				}
			}
			//������������
			UIFlowhLayout contentFlowh = new UIFlowhLayout();
			contentFlowh.setId("contentFlowh");
			approvePanel.addElementToPanel(contentFlowh);
			//������������ͬ��ֱ����==contentFlowhP
			UIFlowvLayout realContentFlowV = this.createApproveContentPanel(contentFlowh,"contentFlowhP");
			{
					//������������+������
					UIFlowhLayout execFlowh =this.createApproveUIFlowhLayout(realContentFlowV, "execflowh");
					//������������
					this.createAppoveAction(execFlowh,"500");
					//������
					if(!isNC)
						this.createCommonWordPanel(execFlowh);
					this.createOptionPanel(realContentFlowV);		
			}
			{//Ĭ�ϡ�ת����ǰ��ǩ ��Ƭ
				this.createCardPanel(realContentFlowV, isNC, false);
				
				UIFlowhLayout flowh = new UIFlowhLayout();
				flowh.setId("flowhlayout5101");
				realContentFlowV.addElementToPanel(flowh);
				//����
				this.createlinkCompPanel(widget, flowh, isNC, false);
				//��������ײ����򣬳����û������������ǩ�û���Ϣ�Լ���ť
				this.createBottomPanel(realContentFlowV, isNC, widget, task, state);
			}
			
			this.createHistoryPanel(widget, bgFlowh, contentFlowh);
			
			UIFlowhPanel rightPaddingPanel = bgFlowh.addElementToPanel(null);
			rightPaddingPanel.setId("rightPaddingPanel_45666111");
			rightPaddingPanel.setWidth("12");
		}
			UIFlowhPanel rightPaddingPanel = gflowh.addElementToPanel(null);
			rightPaddingPanel.setWidth("10");
		
		um.adjustUI(widgetId);
	}
	/**
	 * ���������Ľ����UIMeta
	 * @param widget
	 * @param task
	 * @param widgetId
	 * @param um
	 * @param isNC
	 */
	public void generateApproveUIMeta(LfwView widget, String taskPk, String widgetId, UIMeta um, boolean isNC) {
		// �����
		UIFlowvLayout outflowv = new UIFlowvLayout();
		String state = null;
		try{
			state =  (String) AppUtil.getAppAttr(WfmConstants.WfmAppAttr_NCState);
		}catch (Exception e) {
			CpLogger.error(e.getMessage(),e);
		}
		outflowv.setId("outflowv");
		UIBorder gborder=this.createUIBorder(outflowv);
		// ���ݲ�
		UIFlowvLayout gflowv = new UIFlowvLayout();
		gflowv.setId("gflowv");
		gborder.addElementToPanel(gflowv);
		um.setElement(outflowv);
	
		{
			//������������
			UIPanel approvePanel=createApprovePanel(gflowv,null);
			if(taskPk == null)
				approvePanel.setExpand(UIConstant.FALSE);
			Object task = WfmTaskUtil.getTaskFromSessionCache(taskPk);
			boolean isExpand=WfmProDefUtil.isExpand(WfmTaskUtil.getHumActByTask(task));
			if(!isNC){
				//��ǩ����̬����ǩ�У�����ʾ�����ݴ棬�ύ
				if(isExpand||task != null && (WfmTaskUtil.isBeforeAddSignSend(task) || WfmTaskUtil.isBeforeAddSignPlmnt(task))){
//					approvePanel.setRenderType(null); 
					approvePanel.setExpand(UIConstant.TRUE);
				}
				else{
					approvePanel.setRenderType("wfTitleRender"); 
					approvePanel.setExpand(UIConstant.FALSE);
				}
			}else {
				approvePanel.setRenderType(null); 
				approvePanel.setExpand(UIConstant.TRUE);
			}
			if(task != null){
				if(WfmTaskUtil.isEndState(task) || WfmTaskUtil.isReadedState(task)){
					approvePanel.setRenderType(null); 
					approvePanel.setExpand(UIConstant.TRUE);
				}
				if(WfmTaskUtil.isOpinionNeed(task)||isExpand){
					approvePanel.setExpand(UIConstant.TRUE);
				}
			}
			
			UIFlowhLayout contentFlowh = new UIFlowhLayout();
			contentFlowh.setId("contentFlowh");
			approvePanel.addElementToPanel(contentFlowh);
			
			UIFlowvLayout realContentFlowV =this.createApproveContentPanel(contentFlowh, "contentFlowhP");
			{
				{
					if(!(isNC && WfmTaskStatus.State_End.equals(state))){
						UIFlowhLayout execFlowh = new UIFlowhLayout();
						execFlowh.setId("execflowh");
						realContentFlowV.addElementToPanel(execFlowh);
						//������������
						this.createAppoveAction(execFlowh, "450");
						if(!isNC){
							//������
							 this.createCommonWordPanel(execFlowh);
						}
					}
				}
				//���������������
				this.createOptionPanel(realContentFlowV);
			
			}
			{	//Ĭ�ϡ�ת����ǰ��ǩ ��Ƭ
				this.createCardPanel(realContentFlowV, isNC,true);
				
				UIFlowhLayout flowh = new UIFlowhLayout();
				flowh.setId("flowhlayout5101");
				realContentFlowV.addElementToPanel(flowh);
				//���� 
				this.createlinkCompPanel(widget, flowh, isNC,true);
				
				//��������ײ����򣬳����û������������ǩ�û���Ϣ�Լ���ť
				this.createBottomPanel(realContentFlowV, isNC, widget, task, state);
			}
			
			//������ʷ��ʾ����
			this.createHistoryPanel(widget, realContentFlowV);
		}
		
		um.adjustUI(widgetId);
	}
	/**
	 * �������������Ŀؼ�
	 * @param widget
	 */
	public void createComp(LfwView widget, Object  task, boolean isNC) {
		if(!isNC){
			//������
			createCommonWord(widget);
			//ָ��
			createAssignUser(widget);
			//����--v631foroa--nc���޳���
			createCopySend(widget, task,isNC);
		}
		
		//��������������ѡ���
		createExeGroup(widget, task);
		//������������
		createOpinion(widget, task,isNC);
		//ת���û�
		createTransmit(widget);
		//ǰ��ǩ
		createBeforeAddSign(widget, task,isNC);
		//����
		createAttachFile(widget, task);
		//���ǩ
		createAfterAddSign(widget, task);
		//��ǩ����
		createAddSignManage(widget, task);
		//���̽���
		createFlowImg(widget, task);
		//�߰���ʷ
		createUrgencyHistory(widget, task);
		//��ϸ���
//		if(WfmTaskUtil.allowStepCommit(task)){
//			stepOpinion(widget, task);
//		}
		//��ǩ
		createScratchPad(widget, task);
		//������ֻ̬��NC�Ŵ����ջذ�ť
		if(isNC){
			createReCall(widget, task, isNC);
		}
		//���ʱ������߰�
		boolean isEnd=WfmTaskUtil.isEndState(task)&&WfmProinsUtil.isEndStateProins(WfmTaskUtil.getProInsByTask(task));
		//�߰찴ť����NC����
		if(!isEnd&&!isNC){
			//������״̬,�Ǵ���
			if(!WfmProinsUtil.isCancellationStateProins(WfmTaskUtil.getProInsByTask(task))
					&&!(WfmTaskUtil.isPlmntState(task)||WfmTaskUtil.isRunState(task)||WfmTaskUtil.isBeforeAddSignCmpltState(task)))
				createUrgencyBtn(widget, task, isNC);
		}
		//ȡ��		
		createReBack(widget, task);
		//�ݴ�
		createBinSave(widget, task);
		//�׶��ύ
		if(WfmTaskUtil.allowStepCommit(task))
			createStepSubmit(widget, task, isNC);
		//�ύ
		createSubmit(widget, task);
		//�Ƿ�ʵ��������Эͬ��Ϣ���
		List<PtExtension> exs = PluginManager.newIns().getExtensions(ITaskMessageTmp.TASK_MESSAGE_PID);
		if(exs != null && exs.size() > 0){
			createTaskMessage(widget);
		}
	}
	
	public LinkComp stepOpinion(LfwView widget, Object task) {
		LinkComp stepOpinionLink = new LinkComp();
		stepOpinionLink.setText(getText(WfmConstants.STEPOPINION, task));
		stepOpinionLink.setText(LfwResBundle.getInstance().getStrByID("wfm", "DispStrategy-z30091")/*�׶����*/);
		stepOpinionLink.setI18nName(getText(WfmConstants.STEPOPINION, task));
		stepOpinionLink.setId(ExecuteTaskWidgetProvider.LINK_STEPOPINION);
		
		EventConf e1 = LinkEvent.getOnClickEvent();
		e1.setMethodName("openStepOpinion");
		stepOpinionLink.addEventConf(e1);
		widget.getViewComponents().addComponent(stepOpinionLink);
		
		//�߰���ʷ��Window
		String windowId = "wfm_stepopinion";
		WindowConfig window = new WindowConfig();
		window.setId(windowId);
		widget.addInlineWindow(window);
		return stepOpinionLink;
	}
	/**
	 * �׶��ύ
	 * @param widget
	 * @param task
	 * @param isNC
	 */
	public ButtonComp createStepSubmit(LfwView widget,Object task, boolean isNC) {
		ButtonComp stepSubmitBt = new ButtonComp();
		stepSubmitBt.setId(ExecuteTaskWidgetProvider.BTN_STEP_OK);
		stepSubmitBt.setText(getText(WfmConstants.STEP_SUBMIT, task));
		EventConf oke = MouseEvent.getOnClickEvent();
		oke.setMethodName(ExecuteTaskWidgetProvider.BTN_STEP_OK_CLICK);
		stepSubmitBt.addEventConf(oke);
		widget.getViewComponents().addComponent(stepSubmitBt);
		if(!isNC){
			if(task == null)
				return stepSubmitBt;
			//�ݴ�
			if(WfmTaskUtil.isTempSaveMakeBill(task)){
				stepSubmitBt.setEnabled(true);
			}
			//����
			else if(WfmTaskUtil.isRunState(task)) {
				stepSubmitBt.setEnabled(true);
			}
			//�Ѱ�
			else if(WfmTaskUtil.isEndState(task)) {
				stepSubmitBt.setEnabled(true);	
			}
			//���
			else if(WfmTaskUtil.isFinishState(task)) {
				stepSubmitBt.setEnabled(true);
			}//�ڰ�
			else if(WfmTaskUtil.isPlmntState(task)) {
				stepSubmitBt.setEnabled(true);
			}
			//����
			else if(WfmTaskUtil.isSuspendedState(task)) {
				stepSubmitBt.setEnabled(false);
			}
			//��ǩ����
			else if(WfmTaskUtil.isBeforeAddSignSend(task)) {
				stepSubmitBt.setEnabled(false);
			}
			//��ǩ��
			else if(WfmTaskUtil.isBeforeAddSignPlmnt(task)) {
				stepSubmitBt.setEnabled(false);
			}//��ǩ���
			else if(WfmTaskUtil.isBeforeAddSignCmpltState(task)) {
				stepSubmitBt.setEnabled(true);
			}//��ǩ��ֹ
			else if(WfmTaskUtil.isBeforeAddSignStop(task)) {
				stepSubmitBt.setEnabled(true);
			}
			//����
			else if(WfmTaskUtil.isUnreadState(task)) {
				stepSubmitBt.setEnabled(true);
			}
			//����
			else if(WfmTaskUtil.isReadedState(task)) {
				stepSubmitBt.setEnabled(true);
			}//�ı�
			else if(WfmTaskUtil.isReadEndState(task)) {
				stepSubmitBt.setEnabled(true);
			}
		}
		return stepSubmitBt;
	}
	/**
	 * ������ʷ
	 * @param widget
	 */
	public void createHistory(LfwView widget) {
		LabelComp hisLabelComp=new LabelComp();
		hisLabelComp.setId(ExecuteTaskWidgetProvider.LABEL_HISTORY);
		hisLabelComp.setText(NCLangRes4VoTransl.getNCLangRes().getStrByID("wfm", "ExecuteTaskWidgetProvider-000118")/*������ʷ*/);
		widget.getViewComponents().addComponent(hisLabelComp);
		
		createMoreHistory(widget);
		WebPartComp webpart = new WebPartComp();
		webpart.setContentFetcher("nc.uap.wfm.history.WebPartContentFetcherImpl");
		webpart.setId(ExecuteTaskWidgetProvider.HTML_CONTENT);
		widget.getViewComponents().addComponent(webpart);
		
	}
	public void createMoreHistory(LfwView widget) {
		LinkComp moreHisLink = new LinkComp();
		moreHisLink.setId(ExecuteTaskWidgetProvider.LINK_MOREHISTORY);
		moreHisLink.setText(NCLangRes4VoTransl.getNCLangRes().getStrByID("wfm", "ExecuteTaskWidgetProvider-000218")/*����*/+">>");
		EventConf moreHisLinkEvent = LinkEvent.getOnClickEvent();
		moreHisLinkEvent.setMethodName("link_morehistory");
		moreHisLink.addEventConf(moreHisLinkEvent);
		widget.getViewComponents().addComponent(moreHisLink);
	}
	
	/**
	 * ��д��������ʷ
	 * @param widget
	 */
	public void createHistory2(LfwView widget) {
		Dataset ds = new Dataset();
		buildDataSet(widget, ds);
		
		WebPartComp webpart = new WebPartComp();
		webpart.setContentFetcher("nc.uap.wfm.history.HistoryWebPartContentFetcherImpl");
		webpart.setId("historyWebpart");
		widget.getViewComponents().addComponent(webpart);
		
 		ListViewComp list = new ListViewComp();
		list.setId("historyList");
		list.setDataset("ds_history");
		list.setRenderType("historyRender");
		widget.getViewComponents().addComponent(list);
		
	}
	
	private void buildDataSet(LfwView view,Dataset ds) {
		ds.setId("ds_history");
		ds.setVoMeta("nc.uap.wfm.vo.WfmMyHistoryVO");
		ds.setLazyLoad(false);
		ds.setEnabled(true);
		ds.setPageSize(100);
		if(ds.getFieldSet()!=null && ds.getFieldSet().getDataSet()==null){
			if(ds.getView()==null){
				ds.setView(view);
			}
			ds.getFieldSet().setDataSet(ds);		
		}
		//������
		Field pk_user = new Field();
		pk_user.setId("pk_user");
		pk_user.setField("pk_user");
		pk_user.setDataType(StringDataTypeConst.STRING);
		ds.getFieldSet().addField(pk_user);
		//��Ӧ����
		Field pk_task = new Field();
		pk_task.setId("pk_task");
		pk_task.setField("pk_task");
		pk_task.setDataType(StringDataTypeConst.STRING);
		ds.getFieldSet().addField(pk_task);
		//���
		Field opinion = new Field();
		opinion.setId("opinion");
		opinion.setField("opinion");
		opinion.setDataType(StringDataTypeConst.STRING);
		ds.getFieldSet().addField(opinion);
		// ����ʱ��
		Field createTime = new Field();
		createTime.setId("createTime");
		createTime.setField("createTime");
		createTime.setDataType(StringDataTypeConst.UFDATETIME);
		ds.getFieldSet().addField(createTime);
		// ������
		Field operator = new Field();
		operator.setId("operator");
		operator.setField("operator");
		operator.setDataType(StringDataTypeConst.STRING);
		ds.getFieldSet().addField(operator);
		// ������Code
		Field operatorCode = new Field();
		operatorCode.setId("operatorCode");
		operatorCode.setField("operatorCode");
		operatorCode.setDataType(StringDataTypeConst.STRING);
		ds.getFieldSet().addField(operatorCode);
		// ����
		Field attachFiles = new Field();
		attachFiles.setId("attachFiles");
		attachFiles.setField("attachFiles");
		attachFiles.setDataType(StringDataTypeConst.STRING);
		ds.getFieldSet().addField(attachFiles);
		// ����
		Field deliverHistory = new Field();
		deliverHistory.setId("deliverHistory");
		deliverHistory.setField("deliverHistory");
		deliverHistory.setDataType(StringDataTypeConst.STRING);
		ds.getFieldSet().addField(deliverHistory);
		// ǰ��ǩ
		Field beforeAddSignHistory = new Field();
		beforeAddSignHistory.setId("beforeAddSignHistory");
		beforeAddSignHistory.setField("beforeAddSignHistory");
		beforeAddSignHistory.setDataType(StringDataTypeConst.STRING);
		ds.getFieldSet().addField(beforeAddSignHistory);
		// ���ǩ
		Field afterAddSignHistory = new Field();
		afterAddSignHistory.setId("afterAddSignHistory");
		afterAddSignHistory.setField("afterAddSignHistory");
		afterAddSignHistory.setDataType(StringDataTypeConst.STRING);
		ds.getFieldSet().addField(afterAddSignHistory);
		// ����
		Field deliverActionHistory = new Field();
		deliverActionHistory.setId("deliverActionHistory");
		deliverActionHistory.setField("deliverActionHistory");
		deliverActionHistory.setDataType(StringDataTypeConst.STRING);
		ds.getFieldSet().addField(deliverActionHistory);
		// ת��
		Field transHistory = new Field();
		transHistory.setId("transHistory");
		transHistory.setField("transHistory");
		transHistory.setDataType(StringDataTypeConst.STRING);
		ds.getFieldSet().addField(transHistory);

		// ��������
		Field runningDes = new Field();
		runningDes.setId("runningDes");
		runningDes.setField("runningDes");
		runningDes.setDataType(StringDataTypeConst.STRING);
		ds.getFieldSet().addField(runningDes);


		
		view.getViewModels().addDataset(ds);
		
		EventConf loadEvent =  DatasetEvent.getOnDataLoadEvent();
		loadEvent.setMethodName("onDataLoad_ds_history");
		ds.addEventConf(loadEvent);
	}
	
	
	
	/**
	 * Эͬ��Ϣ
	 * @param widget
	 */
	private void createTaskMessage(LfwView widget) {
		LinkComp taskMessage = new LinkComp();
		taskMessage.setId(ExecuteTaskWidgetProvider.BTN_TASKMESSAGE);
		taskMessage.setText(NCLangRes4VoTransl.getNCLangRes().getStrByID("wfm", "ExecuteTaskWidgetProvider-000018")/*Эͬ��Ϣ*/);
		EventConf taskMessageEvt = LinkEvent.getOnClickEvent();
		taskMessageEvt.setMethodName("link_taskmessage");
		taskMessage.addEventConf(taskMessageEvt);
		widget.getViewComponents().addComponent(taskMessage);
	}
	/**
	 * ָ��
	 * @param widget
	 */
	private void createAssignUser(LfwView widget) {
		LinkComp link_addsign = new LinkComp();
		link_addsign.setId(ExecuteTaskWidgetProvider.LINK_ASSIGNUSER);
		link_addsign.setI18nName(NCLangRes4VoTransl.getNCLangRes().getStrByID("wfm", "ExecuteTaskWidgetProvider-000004")/*ָ��*/);
		EventConf lae = LinkEvent.getOnClickEvent();
		lae.setMethodName("onAssignUserClick");
		link_addsign.addEventConf(lae);
		widget.getViewComponents().addComponent(link_addsign);
		
	}
	/**
	 * ��ǩ����
	 * @param widget
	 */
	private void createAddSignManage(LfwView widget,Object task) {
		ButtonComp okBt = new ButtonComp();
		okBt.setId("addsignmanage");
		okBt.setText(getText(WfmConstants.ADDSIGNMGR, task));
		EventConf oke = MouseEvent.getOnClickEvent();
		oke.setMethodName("btnok_addsignManage");
		okBt.addEventConf(oke);
		widget.getViewComponents().addComponent(okBt);
			
		//��ǩ����window
		String windowId = "addsignpm";
		WindowConfig deliverWindow = new WindowConfig();
		deliverWindow.setId(windowId);
		widget.addInlineWindow(deliverWindow);
	}
	/**
	 * ȡ��
	 * @param widget
	 * @param task
	 */
	private void createReBack(LfwView widget,Object task) {
		ButtonComp reBackBt = new ButtonComp();
		reBackBt.setId(ExecuteTaskWidgetProvider.BTN_REBACK);
		reBackBt.setText(getText(WfmConstants.RETRACT, task));
		EventConf oke = MouseEvent.getOnClickEvent();
		oke.setMethodName("btnreback_click");
		reBackBt.addEventConf(oke);
		widget.getViewComponents().addComponent(reBackBt);
		//�ݴ�
		if(WfmTaskUtil.isTempSaveMakeBill(task)){
			reBackBt.setEnabled(false);
		}
		//����
		else if(WfmTaskUtil.isRunState(task)) {
			reBackBt.setEnabled(false);
		}
		//�Ѱ�
		else if(WfmTaskUtil.isEndState(task)) {
			reBackBt.setEnabled(true);	
		}
		//���
		else if (WfmTaskUtil.isFinishState(task)) {
			reBackBt.setEnabled(false);
		}//�ڰ�
		else if (WfmTaskUtil.isPlmntState(task)) {
			reBackBt.setEnabled(true);
		}
		//����
		else if (WfmTaskUtil.isSuspendedState(task)) {
			reBackBt.setEnabled(true);
		}
		//��ǩ����
		else if(WfmTaskUtil.isBeforeAddSignSend(task)) {
			reBackBt.setEnabled(false);
		}
		//��ǩ��
		else if (WfmTaskUtil.isBeforeAddSignPlmnt(task)) {
			reBackBt.setEnabled(false);
		}//��ǩ���
		else if(WfmTaskUtil.isBeforeAddSignCmpltState(task)) {
			reBackBt.setEnabled(false);
		}//��ǩ��ֹ
		else if(WfmTaskUtil.isBeforeAddSignStop(task)) {
			reBackBt.setEnabled(false);
		}
		//����
		else if(WfmTaskUtil.isUnreadState(task)) {
			reBackBt.setEnabled(false);
		}
		//����
		else if(WfmTaskUtil.isReadedState(task)) {
			reBackBt.setEnabled(false);
		}//�ı�
		else if(WfmTaskUtil.isReadEndState(task)) {
			reBackBt.setEnabled(false);
		}
		if(task!=null){
			boolean allowReverseAudit=WfmTaskUtil.isRecallTaskOnEndProins(task)
			||(WfmTaskUtil.allowReverseAudit(task)&&WfmProinsUtil.isRunStateProins(WfmTaskUtil.getProInsByTask(task)));
			if(!allowReverseAudit){
				reBackBt.setEnabled(false);
			}
		}
	}
	/**
	 * ǰ��ǩ
	 * @param widget
	 * @param task
	 * @param isNC
	 */
	private void createBeforeAddSign(LfwView widget,Object task, boolean isNC ) {
		if(!isNC){
			RadioGroupComp rgc1 = new RadioGroupComp();
			rgc1.setId(ExecuteTaskWidgetProvider.TEXT_LOGIC);
			rgc1.setValue("or");
			rgc1.setComboDataId(ExecuteTaskWidgetProvider.COMBODATA_LOGIC);
			widget.getViewComponents().addComponent(rgc1);
			
			//ϵͳ�������Ƽ�ǩĬ���߼� Serial����(����ʱ��˳��)��Parallel����(����ʱû��˳��ͬʱ�յ���ǩ��Ϣ)
			ICpSysinitQry qryservice = NCLocator.getInstance().lookup(ICpSysinitQry.class);
			try {
				CpSysinitVO sysinitvo = qryservice.getSysinitByCodeAndPkorg("addsignlogic", null);
				if(sysinitvo!=null){
					if("Serial".equals(sysinitvo.getValue())){ 
						rgc1.setValue("and");
					}else if("Parallel".equals(sysinitvo.getValue())){
						rgc1.setValue("or");
					}
						
				}
			} catch (CpbBusinessException e) {
				CpLogger.error(e);
			}
		}
		ReferenceComp rc = new ReferenceComp();
		rc.setId(ExecuteTaskWidgetProvider.TEXT_BEFOREADDSIGNUSER);
		rc.setRefcode(ExecuteTaskWidgetProvider.REFNODE_BEFOREADDSIGNUSER);
		if(isNC){
			rc.setText(NCLangRes4VoTransl.getNCLangRes().getStrByID("wfm", "ExecuteTaskWidgetProvider-000017")/*��ǩ�û�*/);
		}else
			rc.setText(NCLangRes4VoTransl.getNCLangRes().getStrByID("wfm", "ExecuteTaskWidgetProvider-000015")/*ǰ��ǩ�û�*/);
		rc.setTextWidth(60);
		widget.getViewComponents().addComponent(rc);
		//��ǩ����
		createAddSignMgr(widget, task);
	}
	/**
	 * ��ǩ����
	 * @param widget
	 * @param task
	 */
	private void createAddSignMgr(LfwView widget,Object task ){
		LinkComp addsignmgr = new LinkComp();
		addsignmgr.setText(getText(WfmConstants.ADDSIGNMGR, task));
		addsignmgr.setI18nName(getText(WfmConstants.ADDSIGNMGR, task));
		addsignmgr.setId(ExecuteTaskWidgetProvider.LINK_ADDSIGNMGR); 
		EventConf addsigne1 = LinkEvent.getOnClickEvent();
		addsigne1.setMethodName("addsignMgrClick");
		addsignmgr.addEventConf(addsigne1);
		widget.getViewComponents().addComponent(addsignmgr);
		
		//�ݴ�
		if(WfmTaskUtil.isTempSaveMakeBill(task)){
			addsignmgr.setEnabled(false);
		}
		//����
		else if(WfmTaskUtil.isRunState(task)) {
			addsignmgr.setEnabled(false);
		}
		//�Ѱ�
		else if(WfmTaskUtil.isEndState(task)) {
			addsignmgr.setEnabled(true);	
		}
		//���
		else if(WfmTaskUtil.isFinishState(task)) {
			addsignmgr.setEnabled(true);
		}//�ڰ�--��ǩ��ֹ����ǩ����
		else if(WfmTaskUtil.isPlmntState(task)) {
			if(task!=null){
				String operCode=(String)WfmClassUtil.invokeMethod(task, "getSysext12", null);
				if(WfmOperator.BEFOREADDSIGN.equals(operCode)){
					addsignmgr.setEnabled(true);
				}else{
					addsignmgr.setEnabled(false);
				}
			}
			
		}
		//����
		else if(WfmTaskUtil.isSuspendedState(task)) {
			addsignmgr.setEnabled(false);
		}
		//��ǩ����
		else if(WfmTaskUtil.isBeforeAddSignSend(task)) {
			addsignmgr.setEnabled(true);
		}
		//��ǩ��
		else if(WfmTaskUtil.isBeforeAddSignPlmnt(task)) {
			addsignmgr.setEnabled(true);
		}//��ǩ���
		else if(WfmTaskUtil.isBeforeAddSignCmpltState(task)) {
			addsignmgr.setEnabled(true);
		}//��ǩ��ֹ
		else if(WfmTaskUtil.isBeforeAddSignStop(task)) {
			addsignmgr.setEnabled(true);
		}
		//����
		else if(WfmTaskUtil.isUnreadState(task)) {
			addsignmgr.setEnabled(false);
		}
		//����
		else if(WfmTaskUtil.isReadedState(task)) {
			addsignmgr.setEnabled(false);
		}//�ı�
		else if(WfmTaskUtil.isReadEndState(task)) {
			addsignmgr.setEnabled(false);
		}
		if(task!=null){
			if(!WfmTaskUtil.allowBeforeAddSign(task)){
				addsignmgr.setEnabled(false);
			}
		}
	}
	
	
	/**
	 * ��������������ѡ���
	 * @param widget
	 */
	public void createExeGroup(LfwView widget,Object task) {
		RadioGroupComp rgc = new RadioGroupComp();
		rgc.setComboDataId(ExecuteTaskWidgetProvider.COMBODATA_EXECUTION);
		rgc.setId(ExecuteTaskWidgetProvider.TEXT_EXEACTION);
		StaticComboData staticComboData = (StaticComboData) widget.getViewModels().getComboData(ExecuteTaskWidgetProvider.COMBODATA_EXECUTION);
		if (staticComboData != null) {
			CombItem[] combItems = staticComboData.getAllCombItems();
			
		//��ʱ�����ʾ���ƹ��������⣬�ײ�ؼ��������������޸�
		if(combItems != null && combItems.length > 5){
			for (CombItem combItem : combItems) {
				String text=combItem.getText();
				text=text.trim();
				//�ַ����ȳ�������ʱ��ȡ3���ַ�
				if(text.length()>3){
					text=text.substring(0,3)+".";
					//��������ʱ
					if(text.getBytes().length>4){
						text=text.substring(0,2)+".";
					}
				}
				combItem.setText(text);
			}
			}
		if(combItems != null && combItems.length > 0)
			rgc.setValue(combItems[0].getValue());
		}
		
		rgc.setSepWidth(5);
		EventConf vce = TextEvent.getValueChangedEvent();
		vce.setMethodName("textValueChanged");
		rgc.addEventConf(vce);
		widget.getViewComponents().addComponent(rgc);
		if(WfmTaskUtil.isUnreadState(task)) {
			//������������Ĭ�Ϲ�ѡ
			rgc.setValue(ExecuteTaskWidgetProvider.Exe_ReadEnd);
		}
		//����
		else if(WfmTaskUtil.isReadedState(task)) {
			//������������Ĭ�Ϲ�ѡ
			rgc.setValue(ExecuteTaskWidgetProvider.Exe_ReadEnd);
		}
		//�ı�
		else if (WfmTaskUtil.isReadEndState(task)) {
			//������������Ĭ�Ϲ�ѡ
			rgc.setValue(ExecuteTaskWidgetProvider.Exe_ReadEnd);
		}
	}
	/**
	 * ������
	 * @param widget
	 */
	public void createCommonWord(LfwView widget) {
		ComboBoxComp cbc1 = new ComboBoxComp();
		cbc1.setId(ExecuteTaskWidgetProvider.TEXT_COMMONWORD1);
		cbc1.setRefComboData(ExecuteTaskWidgetProvider.COMBODATA_COMMONWORD);
//		EventConf cbce1 = TextEvent.getOnSelectEvent();
		EventConf cbce1 = ComboBoxEvent.getOnItemClickEvent();
		cbce1.setMethodName("commonword1_valueChanged");
		cbc1.addEventConf(cbce1);
		cbc1.setValue(NCLangRes4VoTransl.getNCLangRes().getStrByID("wfm", "ExecuteTaskWidgetProvider-000021")/*������*/);
		widget.getViewComponents().addComponent(cbc1);
		
		
		//������windowconfig
		String windowId = "commonWordPm";
		WindowConfig commonWindow = new WindowConfig();
		commonWindow.setId(windowId);
		widget.addInlineWindow(commonWindow);
		
	}
	/**
	 * �������,�ֶο����ɽ���ctrl����
	 * @param widget
	 */
	public void createOpinion(LfwView widget,Object task, boolean isNC) {
		TextAreaComp textAreaComp = new TextAreaComp();
		textAreaComp.setId(ExecuteTaskWidgetProvider.TEXT_OPINION);
		EventConf onfocus = FocusEvent.getOnFocusEvent();
		onfocus.setMethodName(ExecuteTaskWidgetProvider.STEP_APPROVE_OPINION_EDIT);
		textAreaComp.addEventConf(onfocus);
		
//		String opinion=WfmTaskUtil.getOpinion(task);
//		if(StringUtils.isBlank(opinion))opinion="";
//		IWfmStepOpinionQry sevice = NCLocator.getInstance().lookup(IWfmStepOpinionQry.class);
//		try {
//			SuperVO[] vos = sevice.queryStepOpinionByTaskPK(WfmTaskUtil.getTaskPk(task));
//			if(vos!=null && vos.length>0){
//				WfmStepOpinionVO wfmStepOpinionVO=(WfmStepOpinionVO)vos[vos.length-1];
//				String creator=wfmStepOpinionVO.getCreator();
//				CpUserVO cpUserVO=NCLocator.getInstance().lookup(ICpUserQry.class).getUserByPk(creator);
//				String userName=BDLanguageHelper.getStrOnCurLangCode(cpUserVO, CpUserVO.USER_NAME);
//				String time=wfmStepOpinionVO.getCreatetime().toString();
//				String stepOpinion=wfmStepOpinionVO.getOpinion();
//				String temp="PS:"+stepOpinion +" "+userName+" "+time+" ";
//				opinion=opinion+"\n\n"+temp;
//			}
//		} catch (WfmServiceException e2) {
//			Logger.error(e2);
//		} catch (CpbBusinessException e) {
//			Logger.error(e);
//		}
//		if(StringUtils.isNotBlank(opinion))
//			textAreaComp.setValue(opinion);
		if(!isNC){
			if(task != null){
				if(WfmTaskUtil.allowOpinionEdit(task)){
					textAreaComp.setEnabled(true);
				}else{
					textAreaComp.setEnabled(false);
				}
				if(WfmTaskUtil.isOpinionNeed(task)){
					textAreaComp.setNullable(false);
				}
			}
			
		}
		//1000�ֽ�
		textAreaComp.setMaxSize(1000);
		widget.getViewComponents().addComponent(textAreaComp);
	}

	/**
	 * ת���û�
	 * @param widget
	 */
	private void createTransmit(LfwView widget) {
		ReferenceComp transref = new ReferenceComp();
		transref.setId(ExecuteTaskWidgetProvider.TEXT_TRANSMITUSER);
		transref.setRefcode(ExecuteTaskWidgetProvider.REFNODE_TRANSUSER);
		transref.setText(NCLangRes4VoTransl.getNCLangRes().getStrByID("wfm", "ExecuteTaskWidgetProvider-000019")/*ת���û�*/);
		widget.getViewComponents().addComponent(transref);
	}
	/**
	 * ���ݲ�ͬ��״̬��������ֵ�����������������ѡ��
	 * @param widget
	 * @param task
	 * @param isNC
	 */
	public void createComboData(LfwView widget, Object task, boolean isNC) {
		DynamicComboDataConf dcd = new DynamicComboDataConf();
		dcd.setId(ExecuteTaskWidgetProvider.COMBODATA_COMMONWORD);
		dcd.setClassName(CommonWordProvider.class.getName());
		widget.getViewModels().addComboData(dcd);
		StaticComboData staticComboData = new StaticComboData();
		staticComboData.setId(ExecuteTaskWidgetProvider.COMBODATA_LOGIC);
		widget.getViewModels().addComboData(staticComboData);
		CombItem item1 = new CombItem();
		item1.setText(NCLangRes4VoTransl.getNCLangRes().getStrByID("wfm", "ExecuteTaskWidgetProvider-000022")/*����*/);
		item1.setValue("and");
		staticComboData.addCombItem(item1);
		CombItem item2 = new CombItem();
		item2.setText(NCLangRes4VoTransl.getNCLangRes().getStrByID("wfm", "ExecuteTaskWidgetProvider-000023")/*����*/);
		item2.setValue("or");
		staticComboData.addCombItem(item2);
		
		staticComboData = new StaticComboData();
		staticComboData.setId(ExecuteTaskWidgetProvider.COMBODATA_EXECUTION);
		widget.getViewModels().addComboData(staticComboData);
		//***************************************
		CombItem supOperatorCombItem =new CombItem(ExecuteTaskWidgetProvider.EXE_SUPOPERATOR, getText(WfmConstants.SUPOPINIONBTN, task), null);
		CombItem readCombItem =new CombItem(ExecuteTaskWidgetProvider.EXE_READ, getText(WfmConstants.READ, task), null);
		CombItem agreeCombItem =new CombItem(ExecuteTaskWidgetProvider.EXE_AGREE, getText(WfmConstants.AGREE, task), null);
		CombItem unagreeCombItem =new CombItem(ExecuteTaskWidgetProvider.EXE_DISAGREE, getText(WfmConstants.UNAGREE, task), null);
		CombItem rejectCombItem = new CombItem(ExecuteTaskWidgetProvider.EXE_REJECT, getText(WfmConstants.REJECT, task), null);
		CombItem transmitCombItem = new CombItem(ExecuteTaskWidgetProvider.EXE_TRANSMIT, getText(WfmConstants.TRANSMIT, task), null);
		CombItem beforeAddSignCombItem = new CombItem(ExecuteTaskWidgetProvider.EXE_BEFORE_ADD_SIGN, getText(WfmConstants.BEFOREADDSIGN, task), null);
		CombItem stopCombItem =new CombItem(ExecuteTaskWidgetProvider.EXE_STOP, getText(WfmConstants.STOP, task), null);
		CombItem readOverCombItem = new CombItem(ExecuteTaskWidgetProvider.Exe_ReadEnd, getText(WfmConstants.READOVER, task), null);
		//*******************************************
		if(isNC){
			boolean beforeAddsign=WfmTaskUtil.allowBeforeAddSign(task);
			boolean transmit=WfmTaskUtil.allowTransmit(task);
			boolean agree=WfmTaskUtil.allowArgree(task);
			boolean disagree=WfmTaskUtil.allowDisArgree(task);
			boolean reject=WfmTaskUtil.allowReject(task);
			if(agree)
				staticComboData.addCombItem(agreeCombItem);
			if(disagree)
				staticComboData.addCombItem(unagreeCombItem);
			if(reject){
			//���ص����� ���� ��ǩ ��ť  add by weiningc 20190801 start
//			staticComboData.addCombItem(rejectCombItem);
			
			}
			if(transmit) {
				//����
//				staticComboData.addCombItem(transmitCombItem);
			}
			if(beforeAddsign) {
				//��ǩ
//				staticComboData.addCombItem(beforeAddSignCombItem);
			}
			//end
			beforeAddSignCombItem.setText(NCLangRes4VoTransl.getNCLangRes().getStrByID("wfm", "DispStrategy-000027")/*��ǩ*/);
		}
		else{
			if(WfmTaskUtil.isEndState(task)){
				staticComboData.addCombItem(supOperatorCombItem);
			}//�ύ/�����Ƶ����ݴ�
			else if (WfmTaskUtil.isReadEndState(task)) {
				staticComboData.addCombItem(supOperatorCombItem);
			}
			//�Ƶ�̬�����أ��ջ�
			else if (WfmTaskUtil.isBillMakerState(task)) {
				
				staticComboData.addCombItem(agreeCombItem);
				staticComboData.addCombItem(unagreeCombItem);
//				staticComboData.addCombItem(transmitCombItem);
				//staticComboData.addCombItem(beforeAddSignCombItem);
				staticComboData.addCombItem(stopCombItem);
				//��������
			}
			else if(WfmTaskUtil.isRunState(task)||WfmTaskUtil.isPlmntState(task)) {
				//��ǩ
				if (WfmTaskUtil.isAfterAddSignCreatedTask(task)){
					staticComboData.addCombItem(readCombItem);
					staticComboData.addCombItem(agreeCombItem);
					staticComboData.addCombItem(unagreeCombItem);
					staticComboData.addCombItem(rejectCombItem);
				} else if(WfmTaskUtil.isBeforeAddSignCreatedTask(task) || WfmTaskUtil.isAssistActionType(task)){
					staticComboData.addCombItem(readCombItem);
					staticComboData.addCombItem(agreeCombItem);
					staticComboData.addCombItem(unagreeCombItem);
				} else {
					staticComboData.addCombItem(readCombItem);
					staticComboData.addCombItem(agreeCombItem);
					staticComboData.addCombItem(unagreeCombItem);
					staticComboData.addCombItem(rejectCombItem);
					staticComboData.addCombItem(transmitCombItem);
					staticComboData.addCombItem(beforeAddSignCombItem);
					staticComboData.addCombItem(stopCombItem);
				}
			} 
			//������
			else if (WfmTaskUtil.isPlmntState(task) && !WfmTaskUtil.isDeliverCreatedTask(task)) {
				staticComboData.addCombItem(readCombItem);
				staticComboData.addCombItem(agreeCombItem);
				staticComboData.addCombItem(unagreeCombItem);
				staticComboData.addCombItem(rejectCombItem);
				staticComboData.addCombItem(transmitCombItem);
				staticComboData.addCombItem(beforeAddSignCombItem);
				staticComboData.addCombItem(stopCombItem);
			}
			//����
			else if (WfmTaskUtil.isSuspendedState(task)) {
			}
			//��ǩ����
			else if (WfmTaskUtil.isBeforeAddSignSend(task)) {
			}
			//��ǩ��
			else if (WfmTaskUtil.isBeforeAddSignPlmnt(task)) {
			}
			//��ǩ���
			else if (WfmTaskUtil.isBeforeAddSignCmpltState(task)) {
				staticComboData.addCombItem(readCombItem);
				staticComboData.addCombItem(agreeCombItem);
				staticComboData.addCombItem(unagreeCombItem);
				staticComboData.addCombItem(rejectCombItem);
				staticComboData.addCombItem(transmitCombItem);
				staticComboData.addCombItem(beforeAddSignCombItem);
				staticComboData.addCombItem(stopCombItem);
			} 
			//��ǩ��ֹ
			else if (WfmTaskUtil.isBeforeAddSignStop(task)) {			
				staticComboData.addCombItem(agreeCombItem);			
				staticComboData.addCombItem(unagreeCombItem);			
				staticComboData.addCombItem(rejectCombItem);			
				staticComboData.addCombItem(transmitCombItem);			
				staticComboData.addCombItem(beforeAddSignCombItem);			
				staticComboData.addCombItem(stopCombItem);
			} 
			//����
			else if (WfmTaskUtil.isUnreadState(task)) {			
				staticComboData.addCombItem(readOverCombItem);
			} 
			//����
			else if (WfmTaskUtil.isReadedState(task)) {
				staticComboData.addCombItem(readOverCombItem);
			} 
			//�ı�
			else if (WfmTaskUtil.isReadEndState(task)) {
//				staticComboData.addCombItem(supOperatorCombItem);
			}else if(WfmTaskUtil.isEndState(task)){
				staticComboData.addCombItem(supOperatorCombItem);
			} 
		}
		//���ݻ�϶����Ȩ�޿���-
		if(task != null){
//			if(currentHumanAct != null){
			if(!WfmTaskUtil.allowBeforeAddSign(task)){
				staticComboData.removeComboItem(ExecuteTaskWidgetProvider.EXE_BEFORE_ADD_SIGN);
			}
			if(!WfmTaskUtil.allowTransmit(task)||WfmTaskUtil.isAssistActionType(task)){
				staticComboData.removeComboItem(ExecuteTaskWidgetProvider.EXE_TRANSMIT);
			}
			if(!WfmTaskUtil.allowSupOperate(task)||WfmTaskUtil.isCanceledState(task)||WfmProinsUtil.isCancellationStateProins(WfmTaskUtil.getProInsByTask(task))){
				staticComboData.removeComboItem(ExecuteTaskWidgetProvider.EXE_SUPOPERATOR);
			}
		}
		else{
			String billStatus = null;
			try{
				billStatus=(String) AppUtil.getAppAttr(WfmConstants.WfmAppAttr_Status);
			}catch (Exception e) {
				CpLogger.error(e.getMessage(),e);
			}
			if(NOTT_START.equals(billStatus)){
				//ǰ��ǩ
				staticComboData.removeComboItem(ExecuteTaskWidgetProvider.EXE_BEFORE_ADD_SIGN);
				//ת��
				staticComboData.removeComboItem(ExecuteTaskWidgetProvider.EXE_TRANSMIT);
			}
		}
		if(!isNC){
			// "��ͬ��"ѡ�������߼����˹����ɲ���Ϊ��ǩ������ʱ(������ռʱ)��Ҫ��"��ͬ��"ѡ�(ǰ���ǩ��Э��ʱ����)
			int completeStrategy = WfmTaskUtil.getCompleteStrategy(task);
			boolean beforeAddSignFlag = WfmTaskUtil.isBeforeAddSignCreatedTask(task);
			boolean afterAddSignFlag = WfmTaskUtil.isAfterAddSignCreatedTask(task);
			boolean assistActionTypeFlag = WfmTaskUtil.isAssistActionType(task)||"SelfCircle".equals(WfmProDefUtil.getHumActType(WfmTaskUtil.getHumActByTask(task)));
			boolean isAllowStop=WfmProDefUtil.allowStopByHumAct(WfmTaskUtil.getHumActByTask(task));
			if(!isAllowStop){
				staticComboData.removeComboItem(stopCombItem);
			}
			boolean isNeedUnagree=task != null && CompleteSgy_Occupy != completeStrategy && !beforeAddSignFlag && !afterAddSignFlag && !assistActionTypeFlag;
			if(isNeedUnagree){
				//���л�ǩ�Ƿ�ȥ����ͬ��
				Object humact=WfmTaskUtil.getHumActByTask(task);
				Object completeStrategyObj=WfmClassUtil.invokeMethod(humact, "getCompleteStrategy", null);
				Object isNotBunchObj=WfmClassUtil.invokeMethod(completeStrategyObj, "getIsNotBunch", null);
				
				if(!WfmPublicViewUtil.isNeedUnaGreeNotBunch()&&completeStrategy!=CompleteSgy_Occupy&&!UFBoolean.valueOf((String)isNotBunchObj).booleanValue()){
					isNeedUnagree=false;
				}
			}
			if (!isNeedUnagree) {
				staticComboData.removeComboItem(unagreeCombItem);
			}
			//ϵͳ���������Ƿ���Ҫ����
			ICpSysinitQry qryservice = NCLocator.getInstance().lookup(ICpSysinitQry.class);
			try {
				CpSysinitVO sysinitvo = qryservice.getSysinitByCodeAndPkorg("needReadApprove", null);
				if(sysinitvo!=null){
					if(!UFBoolean.valueOf(sysinitvo.getValue()).booleanValue()||isNC){
						staticComboData.removeComboItem(readCombItem);
					}
				}
			} catch (CpbBusinessException e) {
				CpLogger.error(e);
			}
		}
		
	}
	/**
	 * �������գ�ת������ǩ��
	 * @param widget
	 * @param isNC
	 */
	public void createRefNode(LfwView widget, boolean isNC) {
		if(isNC){
			NCRefNode nr = new NCRefNode();
			nr.setAllowInput(false);
			nr.setId(ExecuteTaskWidgetProvider.REFNODE_TRANSUSER);
			nr.setPagemeta("reference");
			nr.setReadDs("masterDs");
			/**����*/
			nr.setReadFields("userid,user_name");
			nr.setMultiSel(false);
			nr.setRefcode("�û�");
			nr.setOrgs(true);
			nr.setDataListener(NcUserRefCtrl.class.getName());
			nr.setText(NCLangRes4VoTransl.getNCLangRes().getStrByID("wfm", "ExecuteTaskWidgetProvider-000024")/*�û�*/);
			widget.getViewModels().addRefNode(nr);
		}else{
			 SelfDefRefNode snr = new SelfDefRefNode();
			  snr.setId(ExecuteTaskWidgetProvider.REFNODE_TRANSUSER);
			  snr.setPath("aftaddsignpm?model=nc.uap.wfm.pubview.CpWfmTransPageModel");
			  snr.setText(NCLangRes4VoTransl.getNCLangRes().getStrByID("wfm", "ExecuteTaskWidgetProvider-000026")/*Эͬ�û�_Эͬ�û�*/);
			  snr.setWidth("1000");
			  snr.setHeight("500");
			  widget.getViewModels().addRefNode(snr);
//			NCRefNode nr = new NCRefNode();
//			nr.setAllowInput(false);
//			nr.setId(ExecuteTaskWidgetProvider.REFNODE_TRANSUSER);
//			nr.setPagemeta("reference");
//			nr.setReadDs("masterDs");
//			nr.setReadFields("cuserid,user_name");
//			nr.setRefcode("Эͬ�û�");
//			nr.setText(NCLangRes4VoTransl.getNCLangRes().getStrByID("wfm", "ExecuteTaskWidgetProvider-000026")/*Эͬ�û�_Эͬ�û�*/);
//			widget.getViewModels().addRefNode(nr);
		}
//		SelfDefRefNode snr = new SelfDefRefNode();
//		snr.setId(ExecuteTaskWidgetProvider.REFNODE_BEFOREADDSIGNUSER);
//		snr.setPath("bfaddsign?");
//		snr.setText(NCLangRes4VoTransl.getNCLangRes().getStrByID("wfm", "ExecuteTaskWidgetProvider-000027")/*ѡ���ǩ�û�*/);
//		snr.setWidth("1000");
//		snr.setHeight("500");
//		widget.getViewModels().addRefNode(snr);
		
		
	  SelfDefRefNode snr = new SelfDefRefNode();
	  snr.setId(ExecuteTaskWidgetProvider.REFNODE_BEFOREADDSIGNUSER);
	  snr.setPath("aftaddsignpm?model=nc.uap.wfm.pubview.CpWfmbefAddSignPageModel");
	  snr.setText(NCLangRes4VoTransl.getNCLangRes().getStrByID("wfm", "ExecuteTaskWidgetProvider-000027")/*ѡ���ǩ�û�*/);
	  snr.setWidth("1000");
	  snr.setHeight("500");
	  snr.setExtendAttribute("refcomid", "");
	  widget.getViewModels().addRefNode(snr);
	}
	
	public void createCardPanel(UIFlowvLayout realContentFlowV, boolean isNC,boolean isAll){
		UICardLayout card1 = new UICardLayout();
		card1.setId("card1");
		card1.setCurrentItem("0");
		realContentFlowV.addElementToPanel(card1);
		UICardPanel cp1 = new UICardPanel();
		cp1.setId("cardpanel1");
		card1.addPanel(cp1);
		if(isAll){
			UICardPanel cp2 = new UICardPanel();
			cp2.setId("cardpanel2");
			card1.addPanel(cp2);
			UIFlowvLayout cp2flowv = new UIFlowvLayout();
			cp2flowv.setId("flowvlayout1683");
			cp2.setElement(cp2flowv);
			UITextField transuser = new UITextField();
			transuser.setId(ExecuteTaskWidgetProvider.TEXT_TRANSMITUSER);
			transuser.setWidth("300");
			UIFlowvPanel cp2flowvPanel = cp2flowv.addElementToPanel(transuser);
			cp2flowvPanel.setHeight("30");
		}
		if(isAll){
			UICardPanel cp3 = new UICardPanel();
			cp3.setId("cardpanel3");
			card1.addPanel(cp3);
			UIFlowvLayout cp3flowv = new UIFlowvLayout();
			cp3flowv.setId("flowvlayout1783");
			cp3.setElement(cp3flowv);
			UIFlowhLayout addsignFlowh = new UIFlowhLayout();
			addsignFlowh.setId("addsignFlowh");
			UIFlowvPanel cp3flowvP1 = cp3flowv.addElementToPanel(addsignFlowh);
			cp3flowvP1.setHeight("46");
			UITextField addsignUser = new UITextField();
			addsignUser.setId(ExecuteTaskWidgetProvider.TEXT_BEFOREADDSIGNUSER);
			addsignUser.setWidth("300");
			UIFlowhPanel  afterAddsignUserpanel=addsignFlowh.addElementToPanel(addsignUser);
			
//			UIFlowhPanel addsignPempty = new UIFlowhPanel();
//			addsignPempty.setWidth("20");
//			addsignFlowh.addPanel(addsignPempty);
			
			if(!isNC){
				UITextField logic = new UITextField();
				logic.setId(ExecuteTaskWidgetProvider.TEXT_LOGIC);
				logic.setWidth("180");
				addsignFlowh.addElementToPanel(logic);
			}
		}
	}
	/**
	 * ������ʷUI
	 * @param widget
	 * @param bgFlowh
	 * @param contentFlowh
	 */
	private void  createHistoryPanel(LfwView widget,UIFlowhLayout bgFlowh,UIFlowhLayout contentFlowh){
		String withHistoryStr = (String) widget.getExtendAttributeValue(ExecuteTaskWidgetProvider.WITH_HISTORY);
		boolean withHistory = withHistoryStr == null || withHistoryStr.equals("true");
		
		if(withHistory){
			this.createHistoryPanel(contentFlowh);
		}
	}
	
	/**
	 * ��д��������ʷUI
	 * @param widget
	 * @param bgFlowh
	 * @param contentFlowh
	 */
	public void  createHistoryPanel(LfwView widget,UIFlowvLayout gFlowv){
		String withHistoryStr = (String) widget.getExtendAttributeValue(ExecuteTaskWidgetProvider.WITH_HISTORY);
		boolean withHistory = withHistoryStr == null || withHistoryStr.equals("true");
		
		if(withHistory){
			this.createHistoryPanel(gFlowv);
		}
	}
	/**
	 * �ײ�UI
	 * @param realContentFlowV
	 * @param isNC
	 * @param widget
	 * @param task
	 * @param state
	 */
	public void createBottomPanel(UIFlowvLayout realContentFlowV, boolean isNC,LfwView widget, Object task, String state){
		UIFlowvLayout dynFlowv = new UIFlowvLayout();
		dynFlowv.setId(ExecuteTaskWidgetProvider.DYN_FLOWVLAYOUT);
		realContentFlowV.addElementToPanel(dynFlowv);
		//����
		LinkComp linkComp = (LinkComp) widget.getViewComponents().getComponent(ExecuteTaskWidgetProvider.LINK_COPYSEND);
		if(linkComp!=null){
			createCopySendPanel(dynFlowv);
		}
		//����
		this.createAttachPanel(dynFlowv, isNC);
		//���ǩ
		this.createAffterSignPanel(dynFlowv, isNC);
		{//�ݴ桢�ύ��ť
			UIFlowhLayout bottomFlowh = new UIFlowhLayout();
			bottomFlowh.setId("bottomFlowh");
			dynFlowv.addElementToPanel(bottomFlowh);
			//��ť�����
			UIFlowhPanel uINullFlowhPanel = bottomFlowh.addElementToPanel(null);
			ButtonComp urgency = (ButtonComp) widget.getViewComponents().getComponent(ExecuteTaskWidgetProvider.BTN_URGENCY);
			
			if(urgency != null){
				//�߰�
				createUIButton(widget,bottomFlowh,ExecuteTaskWidgetProvider.BTN_URGENCY,"64",null,null,"0",null,"0",null);	
			}	
			if(isNC){
				UIFlowhPanel bottomRecallBtPanel=createUIButton(widget,bottomFlowh,ExecuteTaskWidgetProvider.BTN_RECALL,"64",null,null,null,null,null,null);
				if(WfmTaskStatus.State_Run.equals(state)){
					bottomRecallBtPanel.setVisible(false);
				}else{
					bottomRecallBtPanel.setWidth("130");
				}
			}
			//��ǩ��
			else if(!WfmTaskUtil.isAddSignOrAssist(task) ){
				createUIButton(widget,bottomFlowh,ExecuteTaskWidgetProvider.BTN_SAVE,"64",null,null,null,null,null,null);
						
			}
			if(isNC){
				if(WfmTaskUtil.isShowOk(task)){
					UIFlowhPanel uINullFlowhPanel2=createUIButton(widget,bottomFlowh,ExecuteTaskWidgetProvider.BTN_OK,"64",null,null,null,null,null,"blue_button_div");
					uINullFlowhPanel2.setWidth("130");
				}
			}
			else{
				ButtonComp stepButton = (ButtonComp) widget.getViewComponents().getComponent(ExecuteTaskWidgetProvider.BTN_STEP_OK);
				if(stepButton!=null){
					createUIButton(widget,bottomFlowh,ExecuteTaskWidgetProvider.BTN_STEP_OK,"64",null,null,null,null,null,"blue_button_div");
				}
				createUIButton(widget,bottomFlowh,ExecuteTaskWidgetProvider.BTN_OK,"64",null,null,null,null,null,"blue_button_div");
			}
		}
	}
	/**
	 * �������UI
	 * @param widget
	 * @param flowh
	 * @param isNC
	 * @param isAll
	 */
	public void createlinkCompPanel(LfwView widget,UIFlowhLayout flowh, boolean isNC,boolean isAll){
		//����
		if(isAll)
			if(isVisible(widget,ExecuteTaskWidgetProvider.LINK_COPYSEND)){
				LinkComp linkComp = (LinkComp) widget.getViewComponents().getComponent(ExecuteTaskWidgetProvider.LINK_COPYSEND);
				if(linkComp!=null)
				createUILinkComp(widget,flowh,ExecuteTaskWidgetProvider.LINK_COPYSEND,"40",null,null,null,null,null);
			}
		//����
		if(isVisible(widget,ExecuteTaskWidgetProvider.LINK_ADDATTACH)){
			//createUILinkComp(widget,flowh,ExecuteTaskWidgetProvider.LINK_ADDATTACH,"60",null,null,null,null,null);
			createUIAttachAndImageComp(widget,flowh,"80",null,null,null,null,null);
		}
		//���ǩ
		if(isAll)
			if(isVisible(widget,ExecuteTaskWidgetProvider.LINK_AFTADDSIGN) && !isNC){
				createUILinkComp(widget,flowh,ExecuteTaskWidgetProvider.LINK_AFTADDSIGN,"50",null,null,null,null,null);
			}
		//��ǩ����
		if(isAll)
			if(isVisible(widget,ExecuteTaskWidgetProvider.LINK_ADDSIGNMGR) && !isNC){
				createUILinkComp(widget,flowh,ExecuteTaskWidgetProvider.LINK_ADDSIGNMGR,"60",null,null,null,null,null);
			}
		//���̽���
		if(isVisible(widget,ExecuteTaskWidgetProvider.LINK_FLOWIMG)){
			createUILinkComp(widget,flowh,ExecuteTaskWidgetProvider.LINK_FLOWIMG,"120",null,null,null,null,null);
		}
		
		if (!isNC) {
			//�߰���ʷ
			if(isVisible(widget,ExecuteTaskWidgetProvider.LINK_URGENCYHISTORY)){
				createUILinkComp(widget,flowh,ExecuteTaskWidgetProvider.LINK_URGENCYHISTORY,"60",null,null,null,null,null);
			}
			//�׶����
			if(isVisible(widget,ExecuteTaskWidgetProvider.LINK_STEPOPINION)){
				createUILinkComp(widget,flowh,ExecuteTaskWidgetProvider.LINK_STEPOPINION,"60",null,null,null,null,null);
			}
		}
		//��ǩ
		if(isVisible(widget,ExecuteTaskWidgetProvider.TEXT_SCRATCHPAD) && !(isNC)){
			createUILinkComp(widget,flowh,ExecuteTaskWidgetProvider.TEXT_SCRATCHPAD,"40",null,null,null,null,null);
		}
		if(isAll){
			//ָ��
			if(isVisible(widget,ExecuteTaskWidgetProvider.LINK_ASSIGNUSER) && !(isNC)){
				createUILinkComp(widget,flowh,ExecuteTaskWidgetProvider.LINK_ASSIGNUSER,"50",null,null,null,null,null);
			}
			List<PtExtension> exs = PluginManager.newIns().getExtensions(ITaskMessageTmp.TASK_MESSAGE_PID);
			if(exs != null && exs.size() > 0){
				UILinkComp taskmessageLink = new UILinkComp();
				taskmessageLink.setId(ExecuteTaskWidgetProvider.BTN_TASKMESSAGE);
				taskmessageLink.setWidth("64");
				taskmessageLink.setClassName("blue_button_div");
				UIFlowhPanel messagePanel = flowh.addElementToPanel(taskmessageLink);
				messagePanel.setWidth("70");
			}
		}
	}
	
	/**
	 * ������ʷ
	 * @param contentFlowh
	 */
	public void createHistoryPanel(UIFlowhLayout contentFlowh){

			
			UIFlowvLayout historyLayout = new UIFlowvLayout();
			historyLayout.setId("historyLayout");
			contentFlowh.addElementToPanel(historyLayout);
			
			//������ʷ���⡰������ʷ��
			{
				UIFlowvPanel historyPanel0 = new UIFlowvPanel();
				historyPanel0.setId("historyPanel0");
				historyPanel0.setHeight("24");
				historyLayout.addPanel(historyPanel0);
				
				
				UILabelComp uiHisLabelComp=new UILabelComp();
				uiHisLabelComp.setId(ExecuteTaskWidgetProvider.LABEL_HISTORY);
				historyPanel0.setElement(uiHisLabelComp);
			}
			//������ʷ
			UIFlowvPanel historyPanel1 = new UIFlowvPanel();
			historyPanel1.setId("historyPanel1");
			
			//"����"
			UIFlowvPanel historyPanel2 = new UIFlowvPanel();
			historyPanel2.setId("historyPanel2");
			
			
			UIFlowhLayout historyHlayout = new UIFlowhLayout();
			historyHlayout.setId("historyHlayout");
			
			UIFlowhPanel historyHpanel1 = new UIFlowhPanel();
			historyHpanel1.setId("historyHpanel1");
			
			UIFlowhPanel moreHistoryHpanel = new UIFlowhPanel();
			moreHistoryHpanel.setId("historyHpanel2");
			historyHlayout.addPanel(historyHpanel1);
			historyHlayout.addPanel(moreHistoryHpanel);
			
			moreHistoryHpanel.setWidth("80");
			UILinkComp uIMoreHisLinkComp=new UILinkComp();
			uIMoreHisLinkComp.setId(ExecuteTaskWidgetProvider.LINK_MOREHISTORY);
			uIMoreHisLinkComp.setAlign("right");
			moreHistoryHpanel.setElement(uIMoreHisLinkComp);
			
			
			historyLayout.addPanel(historyPanel1);
			historyLayout.addPanel(historyPanel2);
			
			historyPanel2.setElement(historyHlayout);
			
			
			UIPartComp part = new UIPartComp();
			part.setId(ExecuteTaskWidgetProvider.HTML_CONTENT);
			historyPanel1.setLeftPadding("10");
			historyPanel1.setRightPadding("10");
			historyPanel1.setBorder("1px solid #D1DFE4");
			historyPanel1.setHeight("125");
			historyPanel1.setElement(part);
			
		
	}
	
	/**
	 * ��д��������ʷ
	 * @param contentFlowh
	 */
	public void createHistoryPanel(UIFlowvLayout bgFlowv){

		/*
		UIPanel historyPanel = createHistoryPanelPanel(bgFlowv, null);
		historyPanel.setExpand((UIConstant.TRUE));
		historyPanel.setRenderType("historyTitleRender");
		
		UIFlowvLayout historyContentFlowv = new UIFlowvLayout();
		historyContentFlowv.setId("historyContentFlowh");
		historyPanel.addElementToPanel(historyContentFlowv);
		
		UIListViewComp uilistComp = new UIListViewComp();
		uilistComp.setId("historyList");
//		uilistComp.setWidgetId("main");
		historyContentFlowv.addElementToPanel(uilistComp);
		*/	
		
		UIPartComp part = new UIPartComp();
		part.setId("historyWebpart");
		bgFlowv.addElementToPanel(part);
		
		UIListViewComp uilistComp = new UIListViewComp();
		uilistComp.setId("historyList");
//		uilistComp.setWidgetId("main");
		bgFlowv.addElementToPanel(uilistComp);
		
	}
	
	/**
	 * ������ֱ���ֵ�������������
	 * @param contentFlowh
	 */
	public UIFlowvLayout createApproveContentPanel(UIFlowhLayout contentFlowh,String panelID){
		UIFlowvLayout flowv = new UIFlowvLayout();
		flowv.setId("flowvlayout0191");
		UIFlowhPanel contentFlowhP = contentFlowh.addElementToPanel(flowv);
		contentFlowhP.setId(panelID);
		contentFlowhP.setWidth("100%");
		return flowv;
	}
	
	/**
	 * �������ϴ��ļ��б�
	 */
	public void createAttachPanel(UIFlowvLayout dynFlowv, boolean isNC){
		UIFlowvPanel dynp2 = new UIFlowvPanel();
		dynp2.setId(ExecuteTaskWidgetProvider.DYN_FLOWVPANEL2);
		dynp2.setVisible(false);
		dynFlowv.addPanel(dynp2);
		UIFlowhLayout layout_attach = new UIFlowhLayout();
		layout_attach.setId("layout_attach");
		dynp2.setElement(layout_attach);
		UILabelComp attachText = new UILabelComp();
		attachText.setId(ExecuteTaskWidgetProvider.LABEL_ATTACH);
		UIFlowhPanel attach0 = layout_attach.addElementToPanel(attachText);
		attach0.setWidth("80");
		UILabelComp attachValue = new UILabelComp();
		attachValue.setId(ExecuteTaskWidgetProvider.VALUE_ATTACH);
		layout_attach.addElementToPanel(attachValue);
	}
	/**
	 * ������ѡ�û���Ϣ��Ϣ����,ȥ��nc
	 * @param dynFlowv
	 */
	public void createCopySendPanel(UIFlowvLayout dynFlowv){
		UIFlowvPanel dynp1 = new UIFlowvPanel();
		dynp1.setId(ExecuteTaskWidgetProvider.DYN_FLOWVPANEL1);
		dynFlowv.addPanel(dynp1);
		dynp1.setVisible(false);
		
		UIFlowhLayout layout_copySend = new UIFlowhLayout();
		layout_copySend.setId("layout_copysend");
		dynp1.setElement(layout_copySend);
		
		UILabelComp csTextL = new UILabelComp();
		csTextL.setId(ExecuteTaskWidgetProvider.LABEL_COPYSEND);
		csTextL.setWidth(null);
		UIFlowhPanel copySend = layout_copySend.addElementToPanel(csTextL);
		copySend.setWidth("50");

		UILabelComp csTextName = new UILabelComp();
		csTextName.setWidth(null);
		csTextName.setId(ExecuteTaskWidgetProvider.NAME_COPYSEND);
		UIFlowhPanel  nameFlowhPanel =layout_copySend.addElementToPanel(csTextName);
		nameFlowhPanel.setWidth("200");
		
		UILabelComp csText = new UILabelComp();
		csText.setId(ExecuteTaskWidgetProvider.VALUE_COPYSEND);
		csText.setWidth("2");
		layout_copySend.addElementToPanel(csText);
	}
	/**
	 * ���ǩ��ѡ���û�
	 * @param dynFlowv
	 * @param isNC
	 */
	public void createAffterSignPanel(UIFlowvLayout dynFlowv,boolean isNC){
		UIFlowvPanel dynp3 = new UIFlowvPanel();
		dynp3.setId(ExecuteTaskWidgetProvider.DYN_FLOWVPANEL3);
		dynp3.setVisible(false);
		dynFlowv.addPanel(dynp3);
		dynp3.setCssStyle("width:300px;");
		UIFlowhLayout layout_afteraddsign = new UIFlowhLayout();
		layout_afteraddsign.setId("layout_afteraddsign");
		dynp3.setElement(layout_afteraddsign);
		
		UILabelComp afterSignText = new UILabelComp();
		afterSignText.setId(ExecuteTaskWidgetProvider.LABEL_AFTERADDSIGN);
		afterSignText.setWidth(null);
		UIFlowhPanel attach0 = layout_afteraddsign.addElementToPanel(afterSignText);
		attach0.setWidth("80");
		
		//���ǩ�û�name
		UILabelComp afterSignName = new UILabelComp();
		afterSignName.setId(ExecuteTaskWidgetProvider.NAME_AFTERADDSIGN);
		afterSignName.setWidth(null);
		UIFlowhPanel  nameFlowhPanel= layout_afteraddsign.addElementToPanel(afterSignName);
		nameFlowhPanel.setWidth("200");
		
		//���ǩ�û�pk
		UILabelComp affterSignValue = new UILabelComp();
		affterSignValue.setId(ExecuteTaskWidgetProvider.VALUE_AFTERADDSIGN);
		affterSignValue.setWidth("2");
		affterSignValue.setVisible(false);
		layout_afteraddsign.addElementToPanel(affterSignValue);
	}
	
	/**
	 * ��������
	 */
	private UIFlowhLayout createApproveUIFlowhLayout(UIFlowvLayout realContentFlowV,String id){
		UIFlowhLayout execFlowh = new UIFlowhLayout();
		execFlowh.setId(id);
		realContentFlowV.addElementToPanel(execFlowh);
		return execFlowh;
	}
	/**
	 * ������������
	 * @param execFlowh
	 */
	public void createAppoveAction(UIFlowhLayout execFlowh,String width){
		UITextField text = new UITextField();
		text.setId(ExecuteTaskWidgetProvider.TEXT_EXEACTION);
		text.setHeight("22");
		text.setWidth("100%");
		UIFlowhPanel panel1 = execFlowh.addElementToPanel(text);
		panel1.setWidth(width);
		panel1.setId(ExecuteTaskWidgetProvider.TEXT_EXEACTION + "_1112");
	}
		
	/**
	 * ������
	 * @param execFlowh
	 */
	public void createCommonWordPanel(UIFlowhLayout execFlowh){
		 UITextField tf1 = new UITextField();
		 tf1.setId(ExecuteTaskWidgetProvider.TEXT_COMMONWORD1);
		 tf1.setWidth("240");
		 tf1.setAlign("right");
		 UIFlowhPanel fp1 = execFlowh.addElementToPanel(tf1);
		 fp1.setId(ExecuteTaskWidgetProvider.TEXT_COMMONWORD1);
	}
	/**
	 * ���
	 */
	public void createOptionPanel(UIFlowvLayout realContentFlowV){
		UITextField tf2 = new UITextField();
		tf2.setId(ExecuteTaskWidgetProvider.TEXT_OPINION);
		tf2.setWidth("100%");
		tf2.setHeight("60");
		realContentFlowV.addElementToPanel(tf2);
	}
	/**
	 * ������������
	 * @param gflowh
	 * @return
	 */
	public UIBorder createUIBorder(UIFlowhLayout gflowh){
		UIBorder gborder = new UIBorder();
		gborder.setId("gborder");
		gborder.setCssStyle("background:#FCFCFC");
		gborder.setRoundBorder(UIConstant.TRUE);
		gflowh.addElementToPanel(gborder);
		return gborder;
	}
	public UIPanel createApprovePanel(UIFlowhLayout gflowh,UIFlowhLayout bgFlowh,String title){
		//������������
		UIBorder gborder=this.createUIBorder(gflowh);
		
		bgFlowh.setId("bgflowh");
		gborder.addElementToPanel(bgFlowh);
		//���϶
		UIFlowhPanel bgLeftPadding = bgFlowh.addElementToPanel(null);
		bgLeftPadding.setWidth("10");
		//������������
		UIPanel approvePanel = new UIPanel();
		approvePanel.setId("panel1");
		if(title==null){
			title=NCLangRes4VoTransl.getNCLangRes().getStrByID("wfm", "ExecuteTaskWidgetProvider-000020")/*�������*/;
		}
		approvePanel.setTitle(title);
		bgFlowh.addElementToPanel(approvePanel);
		return approvePanel;
	}	
	/**
	 * ������������
	 * @param gflowh
	 * @return
	 */
	public UIBorder createUIBorder(UIFlowvLayout gflowv){
		UIBorder gborder = new UIBorder();
		gborder.setId("gborder");
		gborder.setCssStyle("background:#FCFCFC");
		gborder.setRoundBorder(UIConstant.TRUE);
		gflowv.addElementToPanel(gborder);
		return gborder;
	}
	public UIPanel createApprovePanel(UIFlowvLayout gflowv,String title){
		// �������panelpanel
		UIPanel approvePanel = new UIPanel();
		approvePanel.setId("approvePanelPanel");
		if(title==null){
			title=NCLangRes4VoTransl.getNCLangRes().getStrByID("wfm", "ExecuteTaskWidgetProvider-000020")/*�������*/;
		}
		approvePanel.setTitle(title);
		
		// �������vpanel
		UIFlowvPanel approveVPanel = gflowv.addElementToPanel(approvePanel);
		approveVPanel.setLeftPadding("12");
		approveVPanel.setRightPadding("12");
		return approvePanel;
	}	
	
	// ����������ʷ��panelpanel
	public UIPanel createHistoryPanelPanel(UIFlowvLayout gflowv,String title){
		// ������ʷpanelpanel
		UIPanel historyPanel = new UIPanel();
		historyPanel.setId("historyPanel");
		if(title==null){
			title = NCLangRes4VoTransl.getNCLangRes().getStrByID("wfm", "ExecuteTaskWidgetProvider-000029")/*������ʷ*/;
		}
		historyPanel.setTitle(title);
		
		// �������vpanel
		UIFlowvPanel historyVPanel = gflowv.addElementToPanel(historyPanel);
		historyVPanel.setLeftPadding("12");
		historyVPanel.setRightPadding("12");
		return historyPanel;
	}	

}
