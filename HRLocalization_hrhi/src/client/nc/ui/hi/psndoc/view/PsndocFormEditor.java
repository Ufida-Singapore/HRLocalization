package nc.ui.hi.psndoc.view;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.frame.persistence.SimpleDocServiceTemplate;
import nc.hr.utils.HRCMTermUnitUtils;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.hr.utils.SQLHelper;
import nc.itf.hi.IPsndocQryService;
import nc.itf.hi.IPsndocService;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.itf.org.IOrgConst;
import nc.md.MDBaseQueryFacade;
import nc.md.data.access.DASFacade;
import nc.md.data.access.NCObject;
import nc.md.model.IBusinessEntity;
import nc.md.model.IComponent;
import nc.md.model.access.javamap.BeanStyleEnum;
import nc.md.model.type.IEnumType;
import nc.pub.tools.HiCacheUtils;
import nc.pub.tools.HiSQLHelper;
import nc.pub.tools.PinYinHelper;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.bd.ref.BatchMatchContext;
import nc.ui.bd.ref.IRefConst;
import nc.ui.bd.ref.model.RegionDefaultRefTreeModel;
import nc.ui.cp.cpindi.ref.CPindiGradeRefModel;
import nc.ui.hi.psndoc.model.PsndocDataManager;
import nc.ui.hi.psndoc.model.PsndocModel;
import nc.ui.hi.pub.EvalUtils;
import nc.ui.hi.pub.HiAppEventConst;
import nc.ui.hr.tools.supervalidator.SuperFormEditorValidatorUtil;
import nc.ui.hr.tools.uilogic.FieldRelationUtil;
import nc.ui.hr.uif2.view.HrBillFormEditor;
import nc.ui.hr.uif2.view.HrPsnclTemplateContainer;
import nc.ui.om.ref.HRDeptRefModel;
import nc.ui.om.ref.JobGradeRefModel2;
import nc.ui.om.ref.JobRankRefModel;
import nc.ui.om.ref.PostRefModel;
import nc.ui.pub.beans.ExtTabbedPane;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.UITable;
import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.ui.pub.beans.constenum.IConstEnum;
import nc.ui.pub.bill.BillCardBeforeEditListener;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillEditListener2;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillItemEvent;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.IBillItem;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.AppEventConst;
import nc.vo.bd.psnid.PsnIdtypeVO;
import nc.vo.bd.pub.IPubEnumConst;
import nc.vo.cp.cpindi.CPIndiGradeVO;
import nc.vo.cp.cpindi.CPIndiVO;
import nc.vo.hi.psndoc.AssVO;
import nc.vo.hi.psndoc.CapaVO;
import nc.vo.hi.psndoc.CertVO;
import nc.vo.hi.psndoc.CtrtVO;
import nc.vo.hi.psndoc.KeyPsnVO;
import nc.vo.hi.psndoc.PartTimeVO;
import nc.vo.hi.psndoc.PsnChgVO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsnOrgVO;
import nc.vo.hi.psndoc.PsndocAggVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.hi.psndoc.QulifyVO;
import nc.vo.hi.psndoc.ReqVO;
import nc.vo.hi.psndoc.RetireVO;
import nc.vo.hi.psndoc.TrainVO;
import nc.vo.hi.psndoc.TrialVO;
import nc.vo.hi.psndoc.enumeration.TrnseventEnum;
import nc.vo.hi.pub.HICommonValue;
import nc.vo.hr.psnclrule.PsnclinfosetVO;
import nc.vo.hr.validator.CommnonValidator;
import nc.vo.om.job.JobVO;
import nc.vo.om.joblevelsys.FilterTypeEnum;
import nc.vo.om.post.PostVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.bill.BillTempletBodyVO;
import nc.vo.pub.lang.MultiLangText;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFLiteralDate;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

/***************************************************************************
 * <br>
 * Created on 2010-2-2 9:38:01<br>
 * @author Rocex Wang
 ***************************************************************************/
public class PsndocFormEditor extends HrBillFormEditor implements BillCardBeforeEditListener, BillEditListener2, FocusListener
{
    private PsndocDataManager dataManger;
    
    private FieldRelationUtil fieldRelationUtil; // �ֶ��߼�������
    
    // ������¼�е�ҵ�����ݣ����š���λ��ְ��ְ�����ְ����ְ�ȡ���λ���еȣ�����Աά����ʱ�����޸�
    private final String strBusiFieldInJobs[] = {
        PsnJobVO.PK_DEPT,
        PsnJobVO.PK_POST,
        PsnJobVO.PK_JOB,
        PsnJobVO.SERIES,
        PsnJobVO.PK_JOBGRADE,
        PsnJobVO.PK_JOBRANK,
        PsnJobVO.PK_POSTSERIES};
    
    private String strPk_psncl; // ��¼��ǰ����ά������Ա���
    
    private SuperFormEditorValidatorUtil superValidator; // У����
    
    // ��ͬ�Ӽ���������ص��ֶ�
    private final String[] ctrtTrialFlds = {
        CtrtVO.PROMONTH,
        CtrtVO.PROBEGINDATE,
        CtrtVO.PROBENDDATE,
        CtrtVO.PROBSALARY,
        CtrtVO.STARTSALARY,
        CtrtVO.PROP_UNIT};
    
    // ��ͬ�к�ͬ��������ֶ�
    private final String[] ctrtFlds = {CtrtVO.TERMMONTH, CtrtVO.BEGINDATE, CtrtVO.ENDDATE, CtrtVO.CONT_UNIT};
    
    private HashSet<String> hashSubHaveLoad = new HashSet<String>();
    // �༭̬���ع����Ӽ�
    
    private final String[] fldBlastList = new String[]{
        PsndocVO.ISHISKEYPSN,
        PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_PSNDOC,
        PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_DEPT_V,
        PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_ORG_V,
        PsnJobVO.PK_DEPT_V,
        PsnJobVO.PK_ORG_V,
        PsnJobVO.PK_PSNDOC};
    
    private int selectedRow = 0;// ��¼�ӱ��иı����
    
    private boolean isEditBeginDate;
    private boolean isEditEndDate;
    
    /**
     * ���ֱ�ҳǩ��ť����Ȩ��
     */
    // @Override
    // public List<NCAction> getTabActions()
    // {
    // List<NCAction> al = new ArrayList<NCAction>();
    // List<NCAction> actions = super.getTabActions();
    // if (actions == null || actions.size() == 0)
    // {
    // return al;
    // }
    // for (NCAction action : actions)
    // {
    // FuncPermissionState state = getModel().getContext().getFuncInfo().getButtonPermissionState((String)
    // action.getValue(INCAction.CODE));
    // if (FuncPermissionState.REGISTERD_HASPERMISSION == state || FuncPermissionState.NOREGISTERD == state)
    // {
    // // ûע��Ļ�����Ȩ�޵Ŀ�����ʾ
    // al.add(action);
    // }
    // }
    // return al;
    // }
    
    /***************************************************************************
     * <br>
     * Created on 2010-6-8 9:07:52<br>
     * @param evt
     * @author Rocex Wang
     ***************************************************************************/
    private void afterBodyChange(BillEditEvent evt)
    {
        try
        {
            if (PartTimeVO.getDefaultTableName().equals(evt.getTableCode()) && PsnJobVO.PK_GROUP.equals(evt.getKey()))
            {
                // ����
                clearBodyItemValue(evt.getTableCode(), evt.getRow(), PsnJobVO.PK_ORG, PsnJobVO.PK_DEPT);
                // ��� ��֯�����š���λ��ְ��ְ�����ְ����ְ��
                Object obj = getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_POST, evt.getRow());
                if (obj != null)
                {
                    clearBodyItemValue(evt.getTableCode(), evt.getRow(), PsnJobVO.PK_POST, PsnJobVO.PK_POSTSERIES, PsnJobVO.PK_JOB,
                        PsnJobVO.SERIES, PsnJobVO.PK_JOBGRADE, PsnJobVO.PK_JOBRANK);
                }
            }
            else if (ArrayUtils
                .contains(new String[]{PsnJobVO.getDefaultTableName(), PartTimeVO.getDefaultTableName()}, evt.getTableCode())
                && PsnJobVO.PK_ORG.equals(evt.getKey()))
            {
                // ��֯
                clearBodyItemValue(evt.getTableCode(), evt.getRow(), PsnJobVO.PK_DEPT);
                // ��� ���š���λ��ְ��ְ�����ְ����ְ��
                Object obj = getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_POST, evt.getRow());
                if (obj != null)
                {
                    clearBodyItemValue(evt.getTableCode(), evt.getRow(), PsnJobVO.PK_POST, PsnJobVO.PK_POSTSERIES, PsnJobVO.PK_JOB,
                        PsnJobVO.SERIES, PsnJobVO.PK_JOBGRADE, PsnJobVO.PK_JOBRANK);
                }
            }
            else if (ArrayUtils
                .contains(new String[]{PsnJobVO.getDefaultTableName(), PartTimeVO.getDefaultTableName()}, evt.getTableCode())
                && PsnJobVO.PK_DEPT.equals(evt.getKey()))
            {
                // ����
                // ��� ��λ��ְ��ְ�����ְ�ȡ�ְ��
                Object obj = getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_POST, evt.getRow());
                if (obj != null)
                {
                    clearBodyItemValue(evt.getTableCode(), evt.getRow(), PsnJobVO.PK_POST, PsnJobVO.PK_POSTSERIES, PsnJobVO.PK_JOB,
                        PsnJobVO.SERIES, PsnJobVO.PK_JOBGRADE, PsnJobVO.PK_JOBRANK);
                }
            }
            else if (ArrayUtils
                .contains(new String[]{PsnJobVO.getDefaultTableName(), PartTimeVO.getDefaultTableName()}, evt.getTableCode())
                && PsnJobVO.PK_POST.equals(evt.getKey()))
            {
                // ��λ
                String pk_post = getStrValue(evt.getValue());
                PostVO post = pk_post == null ? null : getService().queryByPk(PostVO.class, pk_post, true);
                if (post != null)
                {
                    setBodyValue(evt.getTableCode(), evt.getRow(), PsnJobVO.PK_DEPT, post.getPk_dept());// ����
                    setBodyValue(evt.getTableCode(), evt.getRow(), PsnJobVO.PK_POSTSERIES, post.getPk_postseries());// ��λ����
                    setBodyValue(evt.getTableCode(), evt.getRow(), PsnJobVO.PK_JOB, post.getPk_job());// ְ��
                    JobVO jobVO = post.getPk_job() == null ? null : getService().queryByPk(JobVO.class, post.getPk_job(), true);
                    if (jobVO != null)
                    {
                        setBodyValue(evt.getTableCode(), evt.getRow(), PsnJobVO.SERIES, jobVO.getPk_jobtype());// ְ�����
                    }
                    if (post.getEmployment() != null)
                    {
                        setBodyValue(evt.getTableCode(), evt.getRow(), PsnJobVO.OCCUPATION, post.getEmployment());// ְҵ
                    }
                    if (post.getWorktype() != null)
                    {
                        setBodyValue(evt.getTableCode(), evt.getRow(), PsnJobVO.WORKTYPE, post.getWorktype());// ����
                    }
                    
                    String defaultlevel = "";
                    String defaultrank = "";
                    Map<String, String> resultMap =
                        NCLocator.getInstance().lookup(IPsndocQryService.class).getDefaultLevelRank(null, null, null, pk_post, null);
                    if (!resultMap.isEmpty())
                    {
                        defaultlevel = resultMap.get("defaultlevel");
                        defaultrank = resultMap.get("defaultrank");
                    }
                    
                    setBodyValue(evt.getTableCode(), evt.getRow(), PsnJobVO.PK_JOBGRADE, defaultlevel);// ְ��
                    setBodyValue(evt.getTableCode(), evt.getRow(), PsnJobVO.PK_JOBRANK, defaultrank);// ְ��
                }
                else
                {
                    setBodyValue(evt.getTableCode(), evt.getRow(), PsnJobVO.SERIES, null);// ְ�����
                    setBodyValue(evt.getTableCode(), evt.getRow(), PsnJobVO.PK_JOB, null);
                    setBodyValue(evt.getTableCode(), evt.getRow(), PsnJobVO.PK_POSTSERIES, null);
                }
                
                if (post == null)
                {
                    BillEditEvent event =
                        new BillEditEvent(getBillCardPanel().getBodyItem(evt.getTableCode(), PsnJobVO.PK_POST), post == null ? null
                            : post.getPk_job(), PsnJobVO.PK_JOB, evt.getRow(), evt.getPos());
                    event.setTableCode(evt.getTableCode());
                    afterBodyChange(event);
                }
            }
            else if (ArrayUtils
                .contains(new String[]{PsnJobVO.getDefaultTableName(), PartTimeVO.getDefaultTableName()}, evt.getTableCode())
                && PsnJobVO.SERIES.equals(evt.getKey()))
            {
                // ְ�����
                String series = getStrValue(evt.getValue());
                String pk_job = (String) getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_JOB + IBillItem.ID_SUFFIX, evt.getRow());
                String pk_post = (String) getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_POST + IBillItem.ID_SUFFIX, evt.getRow());
                if (StringUtils.isBlank(pk_job) && StringUtils.isNotBlank(series))
                {
                    String defaultlevel = "";
                    String defaultrank = "";
                    Map<String, String> resultMap =
                        NCLocator.getInstance().lookup(IPsndocQryService.class).getDefaultLevelRank(series, pk_job, null, pk_post, null);
                    if (!resultMap.isEmpty())
                    {
                        
                        defaultlevel = resultMap.get("defaultlevel");
                        defaultrank = resultMap.get("defaultrank");
                    }
                    
                    setBodyValue(evt.getTableCode(), evt.getRow(), PsnJobVO.PK_JOBGRADE, defaultlevel);// ְ��
                    setBodyValue(evt.getTableCode(), evt.getRow(), PsnJobVO.PK_JOBRANK, defaultrank);// ְ��
                }
                else if (StringUtils.isBlank(pk_job) && StringUtils.isBlank(series))
                {
                    setBodyValue(evt.getTableCode(), evt.getRow(), PsnJobVO.PK_JOBGRADE, null);// ְ��
                    setBodyValue(evt.getTableCode(), evt.getRow(), PsnJobVO.PK_JOBRANK, null);// ְ��
                }
            }
            else if (ArrayUtils
                .contains(new String[]{PsnJobVO.getDefaultTableName(), PartTimeVO.getDefaultTableName()}, evt.getTableCode())
                && PsnJobVO.PK_POSTSERIES.equals(evt.getKey()))
            {
                // ��λ����
                String pk_postseries = getStrValue(evt.getValue());
                String series = (String) getBodyItemValue(evt.getTableCode(), PsnJobVO.SERIES + IBillItem.ID_SUFFIX, evt.getRow());
                String pk_job = (String) getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_JOB + IBillItem.ID_SUFFIX, evt.getRow());
                String pk_post = (String) getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_POST + IBillItem.ID_SUFFIX, evt.getRow());
                if (StringUtils.isBlank(pk_job) && StringUtils.isBlank(series) && StringUtils.isBlank(pk_post)
                    && StringUtils.isNotBlank(pk_postseries))
                {
                    String defaultlevel = "";
                    String defaultrank = "";
                    Map<String, String> resultMap =
                        NCLocator.getInstance().lookup(IPsndocQryService.class)
                            .getDefaultLevelRank(series, pk_job, pk_postseries, pk_post, null);
                    if (!resultMap.isEmpty())
                    {
                        defaultlevel = resultMap.get("defaultlevel");
                        defaultrank = resultMap.get("defaultrank");
                    }
                    
                    setBodyValue(evt.getTableCode(), evt.getRow(), PsnJobVO.PK_JOBGRADE, defaultlevel);// ְ��
                    setBodyValue(evt.getTableCode(), evt.getRow(), PsnJobVO.PK_JOBRANK, defaultrank);// ְ��
                }
                else if (StringUtils.isBlank(pk_job) && StringUtils.isBlank(series) && StringUtils.isBlank(pk_post)
                    && StringUtils.isBlank(pk_postseries))
                {
                    setBodyValue(evt.getTableCode(), evt.getRow(), PsnJobVO.PK_JOBGRADE, null);// ְ��
                    setBodyValue(evt.getTableCode(), evt.getRow(), PsnJobVO.PK_JOBRANK, null);// ְ��
                }
            }
            else if (ArrayUtils
                .contains(new String[]{PsnJobVO.getDefaultTableName(), PartTimeVO.getDefaultTableName()}, evt.getTableCode())
                && PsnJobVO.PK_JOB.equals(evt.getKey()))
            {
                // ְ��
                String pk_job = getStrValue(evt.getValue());
                String pk_post = (String) getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_POST + IBillItem.ID_SUFFIX, evt.getRow());
                JobVO job = pk_job == null ? null : getService().queryByPk(JobVO.class, pk_job, true);
                if (job != null)
                {
                    String defaultlevel = "";
                    String defaultrank = "";
                    Map<String, String> resultMap =
                        NCLocator.getInstance().lookup(IPsndocQryService.class).getDefaultLevelRank(null, pk_job, null, pk_post, null);
                    if (!resultMap.isEmpty())
                    {
                        defaultlevel = resultMap.get("defaultlevel");
                        defaultrank = resultMap.get("defaultrank");
                    }
                    
                    setBodyValue(evt.getTableCode(), evt.getRow(), PsnJobVO.SERIES, job.getPk_jobtype());// ְ�����
                    setBodyValue(evt.getTableCode(), evt.getRow(), PsnJobVO.PK_JOBGRADE, defaultlevel);// ְ��
                    setBodyValue(evt.getTableCode(), evt.getRow(), PsnJobVO.PK_JOBRANK, defaultrank);// ְ��
                }
                else
                {
                    setBodyValue(evt.getTableCode(), evt.getRow(), PsnJobVO.SERIES, null);// ְ�����
                    setBodyValue(evt.getTableCode(), evt.getRow(), PsnJobVO.PK_JOBGRADE, null);
                    setBodyValue(evt.getTableCode(), evt.getRow(), PsnJobVO.PK_JOBRANK, null);
                }
            }
            else if (ArrayUtils
                .contains(new String[]{PsnJobVO.getDefaultTableName(), PartTimeVO.getDefaultTableName()}, evt.getTableCode())
                && PsnJobVO.PK_JOBGRADE.equals(evt.getKey()))
            {
                // ְ��
                String pk_jobgrage = getStrValue(evt.getValue());
                if (StringUtils.isNotBlank(pk_jobgrage))
                {
                    String series = (String) getBodyItemValue(evt.getTableCode(), PsnJobVO.SERIES + IBillItem.ID_SUFFIX, evt.getRow());
                    String pk_job = (String) getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_JOB + IBillItem.ID_SUFFIX, evt.getRow());
                    String pk_postseries =
                        (String) getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_POSTSERIES + IBillItem.ID_SUFFIX, evt.getRow());
                    String pk_post = (String) getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_POST + IBillItem.ID_SUFFIX, evt.getRow());
                    String defaultrank = "";
                    Map<String, String> resultMap =
                        NCLocator.getInstance().lookup(IPsndocQryService.class)
                            .getDefaultLevelRank(series, pk_job, pk_postseries, pk_post, pk_jobgrage);
                    if (!resultMap.isEmpty())
                    {
                        defaultrank = resultMap.get("defaultrank");
                    }
                    setBodyValue(evt.getTableCode(), evt.getRow(), PsnJobVO.PK_JOBRANK, defaultrank);// ְ��
                }
                else
                {
                    setBodyValue(evt.getTableCode(), evt.getRow(), PsnJobVO.PK_JOBRANK, null);// ְ��
                }
            }
            else if (ArrayUtils
                .contains(new String[]{PsnJobVO.getDefaultTableName(), PartTimeVO.getDefaultTableName()}, evt.getTableCode())
                && PsnJobVO.TRNSEVENT.equals(evt.getKey()))
            {
                // �춯�¼�
                afterTrnsEventChange(evt);
            }
            else if (ReqVO.getDefaultTableName().equals(evt.getTableCode()) && ReqVO.PK_POSTREQUIRE_H.equals(evt.getKey()))
            {
                // ʤ������
                // ��� �ﵽ����
                clearBodyItemValue(evt.getTableCode(), evt.getRow(), ReqVO.PK_POSTREQUIRE_B);
            }
            else if (CapaVO.getDefaultTableName().equals(evt.getTableCode()))
            {
                if (CapaVO.PK_PE_INDI.equals(evt.getKey()))
                {
                    // ʤ������
                    // ��� �ﵽ����
                    clearBodyItemValue(evt.getTableCode(), evt.getRow(), CapaVO.PK_PE_SCOGRDITEM);
                    if (evt.getValue() == null)
                    {
                        clearBodyItemValue(evt.getTableCode(), evt.getRow(), CapaVO.INDICODE);
                        clearBodyItemValue(evt.getTableCode(), evt.getRow(), CapaVO.PK_INDI_TYPE);
                        clearBodyItemValue(evt.getTableCode(), evt.getRow(), CapaVO.SCORESTANDARD);
                    }
                    else
                    {
                        CPIndiVO indi =
                            (CPIndiVO) NCLocator.getInstance().lookup(IPersistenceRetrieve.class)
                                .retrieveByPk(null, CPIndiVO.class, (String) evt.getValue());
                        setBodyValue(evt.getTableCode(), evt.getRow(), CapaVO.INDICODE, indi.getIndicode());
                        setBodyValue(evt.getTableCode(), evt.getRow(), CapaVO.PK_INDI_TYPE, indi.getPk_indi_type());
                        setBodyValue(evt.getTableCode(), evt.getRow(), CapaVO.SCORESTANDARD, indi.getScorestandard());
                    }
                }
                else if (CapaVO.PK_PE_SCOGRDITEM.equals(evt.getKey()))
                {
                    if (evt.getValue() == null)
                    {
                        clearBodyItemValue(evt.getTableCode(), evt.getRow(), CapaVO.SCORE);
                    }
                    else
                    {
                        CPIndiGradeVO grade =
                            (CPIndiGradeVO) NCLocator.getInstance().lookup(IPersistenceRetrieve.class)
                                .retrieveByPk(null, CPIndiGradeVO.class, (String) evt.getValue());
                        setBodyValue(evt.getTableCode(), evt.getRow(), CapaVO.SCORE, grade.getGradeseq());
                    }
                }
            }
            else if (TrialVO.getDefaultTableName().equals(evt.getTableCode()) && TrialVO.TRIALRESULT.equals(evt.getKey()))
            {
                // ���� ---���ý��
                Integer trialResult = (Integer) evt.getValue();
                int rowCount = getBillCardPanel().getBillTable(TrialVO.getDefaultTableName()).getRowCount();
                int editRow = evt.getRow();
                if (editRow < rowCount - 1 && (trialResult == null || trialResult == 2))
                {
                    // �������ü�¼ʱ����ѡ���ӳ�ʹ����
                    MessageDialog.showWarningDlg(getModel().getContext().getEntranceUI(), null,
                        ResHelper.getString("6007psn", "06007psn0164")/*
                                                                       * @res "��ʷ���ü�¼����ѡ���ӳ������ڵ����ý����ѡ�����ý��"
                                                                       */);
                    getBillCardPanel().getBillModel(TrialVO.getDefaultTableName()).setValueAt(evt.getOldValue(), evt.getRow(),
                        TrialVO.TRIALRESULT);
                    return;
                }
                // ת��ͨ����ת��δͨ��ʱ���ý���
                getBillCardPanel().getBillModel(TrialVO.getDefaultTableName()).setValueAt(
                    UFBoolean.valueOf(trialResult != null && (trialResult == 1 || trialResult == 3)), evt.getRow(), TrialVO.ENDFLAG);
            }
            else if (CtrtVO.getDefaultTableName().equals(evt.getTableCode()))
            {
                afterCtrtEdit(evt);
            }
            else if (PartTimeVO.getDefaultTableName().equals(evt.getTableCode()))
            {
                if (PartTimeVO.ENDFLAG.equals(evt.getKey()))
                {
                    // ������־ �������Ƿ��ڸ�Ϊfalse
                    Boolean endflag = (Boolean) evt.getValue();
                    if (endflag != null && endflag.booleanValue())
                    {
                        getBillCardPanel().getBillModel(evt.getTableCode()).setValueAt(UFBoolean.FALSE, evt.getRow(), PartTimeVO.POSTSTAT);
                    }
                }
            }
            getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_JOBGRADE + IBillItem.ID_SUFFIX, evt.getRow());
            String series = (String) getBodyItemValue(evt.getTableCode(), PsnJobVO.SERIES, evt.getRow());
            String postseries = (String) getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_POSTSERIES, evt.getRow());
            String pk_job = (String) getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_JOB, evt.getRow());
            String pk_post = (String) getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_POST, evt.getRow());
            if (StringUtils.isBlank(series) && StringUtils.isBlank(postseries) && StringUtils.isBlank(pk_job)
                && StringUtils.isBlank(pk_post))
            {
                setBodyItemEdit(evt.getTableCode(), evt.getRow(), false, PsnJobVO.PK_JOBGRADE);
            }
            else
            {
                setBodyItemEdit(evt.getTableCode(), evt.getRow(), true, PsnJobVO.PK_JOBGRADE);
                setBodyItemEdit(evt.getTableCode(), evt.getRow(), true, PsnJobVO.PK_JOB);
            }
            getBillCardPanel().getBillModel(evt.getTableCode()).loadLoadRelationItemValue();
            
            getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_JOBGRADE + IBillItem.ID_SUFFIX, evt.getRow());
        }
        catch (Exception e)
        {
            Logger.error(e.getMessage(), e);
        }
    }
    
    /**
     * ��ͬҳǩ�����Ŀ�仯����
     */
    private void afterCtrtEdit(BillEditEvent evt)
    {
        // ������صĲ���
        if (ArrayUtils.contains(ctrtTrialFlds, evt.getKey()) || CtrtVO.IFPROP.equals(evt.getKey()))
        {
            String unitName = (String) getBillCardPanel().getBillModel(evt.getTableCode()).getValueAt(evt.getRow(), CtrtVO.PROP_UNIT);
            Integer trialtype = HRCMTermUnitUtils.getTermUnit(unitName);
            // Ĭ������
            trialtype = trialtype == null ? HRCMTermUnitUtils.TERMUNIT_MONTH : trialtype;
            
            float days = HRCMTermUnitUtils.getDaysByUnit(trialtype);
            UFLiteralDate begindate =
                (UFLiteralDate) getBillCardPanel().getBillModel(evt.getTableCode()).getValueAt(evt.getRow(), CtrtVO.PROBEGINDATE);
            UFLiteralDate enddate =
                (UFLiteralDate) getBillCardPanel().getBillModel(evt.getTableCode()).getValueAt(evt.getRow(), CtrtVO.PROBENDDATE);
            Integer promonth = (Integer) getBillCardPanel().getBillModel(evt.getTableCode()).getValueAt(evt.getRow(), CtrtVO.PROMONTH);
            if (CtrtVO.IFPROP.equals(evt.getKey()))
            {
                if (!(Boolean) evt.getValue())
                {
                    clearBodyItemValue(evt.getTableCode(), evt.getRow(), ctrtTrialFlds);
                }
                else
                {// �����Ƿ����á�����ʱ�����������޵�λ��Ĭ��Ϊ���¡�������Ϊ3
                    getBillCardPanel().getBillModel(CtrtVO.getDefaultTableName()).setValueAt(HRCMTermUnitUtils.TERMUNIT_MONTH,
                        evt.getRow(), CtrtVO.PROP_UNIT);
                    getBillCardPanel().getBodyItem(CtrtVO.getDefaultTableName(), CtrtVO.PROMONTH).setLength(
                        HRCMTermUnitUtils.TERMUNIT_MONTH_LENGTH);
                }
            }
            else if (evt.getKey().equals(CtrtVO.PROMONTH))
            {// ��������
                if (promonth == null)
                {
                    if (begindate != null && enddate != null)
                    {
                        getBillCardPanel().getBillModel().setValueAt(Math.round(UFLiteralDate.getDaysBetween(begindate, enddate) / days),
                            evt.getRow(), CtrtVO.PROMONTH);
                    }
                }
                else if (begindate != null)
                {
                    getBillCardPanel().getBillModel().setValueAt(HRCMTermUnitUtils.getDateAfterMonth(begindate, promonth, trialtype),
                        evt.getRow(), CtrtVO.PROBENDDATE);
                }
                else if (enddate != null)
                {
                    // �������ڲ�Ϊ��,���㿪ʼ����
                    getBillCardPanel().getBillModel().setValueAt(HRCMTermUnitUtils.getDateBeforeMonth(begindate, promonth, trialtype),
                        evt.getRow(), CtrtVO.PROBEGINDATE);
                }
            }
            else if (evt.getKey().equals(CtrtVO.PROBEGINDATE))
            {// ���ÿ�ʼ����
                if (promonth != null)
                {
                    getBillCardPanel().getBillModel().setValueAt(HRCMTermUnitUtils.getDateAfterMonth(begindate, promonth, trialtype),
                        evt.getRow(), CtrtVO.PROBENDDATE);
                }
                else if (enddate != null)
                {
                    getBillCardPanel().getBillModel().setValueAt(Math.round(UFLiteralDate.getDaysBetween(begindate, enddate) / days),
                        evt.getRow(), CtrtVO.PROMONTH);
                }
            }
            else if (evt.getKey().equals(CtrtVO.PROBENDDATE))
            {// ���ý�������
                if (enddate == null)
                {
                    if (begindate != null && promonth != null)
                    {
                        getBillCardPanel().getBillModel().setValueAt(HRCMTermUnitUtils.getDateAfterMonth(begindate, promonth, trialtype),
                            evt.getRow(), CtrtVO.PROBENDDATE);
                    }
                }
                else if (begindate != null)
                {
                    getBillCardPanel().getBillModel().setValueAt(Math.round(UFLiteralDate.getDaysBetween(begindate, enddate) / days),
                        evt.getRow(), CtrtVO.PROMONTH);
                }
                else
                {
                    getBillCardPanel().getBillModel().setValueAt(HRCMTermUnitUtils.getDateBeforeMonth(enddate, promonth, trialtype),
                        evt.getRow(), CtrtVO.PROBEGINDATE);
                }
            }
            else if (evt.getKey().equals(CtrtVO.PROP_UNIT))
            {// �������޵�λ
             // �������������޵�λ���������޵ĳ���ҲҪ��Ӧ�ķ����仯
                Integer termUnit = (Integer) evt.getValue();
                getBillCardPanel().getBodyItem(CtrtVO.getDefaultTableName(), CtrtVO.PROMONTH).setLength(
                    HRCMTermUnitUtils.getLengthByTermUnit(termUnit));
                
                if (promonth == null)
                {// ��������Ϊ��
                    if (begindate != null && enddate != null)
                    {
                        getBillCardPanel().getBillModel().setValueAt(Math.round(UFLiteralDate.getDaysBetween(begindate, enddate) / days),
                            evt.getRow(), CtrtVO.PROMONTH);
                    }
                }
                else if (begindate == null)
                {// ��ʼ����Ϊ��
                    if (enddate != null && promonth != null)
                    {
                        getBillCardPanel().getBillModel().setValueAt(HRCMTermUnitUtils.getDateBeforeMonth(enddate, promonth, trialtype),
                            evt.getRow(), CtrtVO.PROBEGINDATE);
                    }
                }
                else
                {
                    getBillCardPanel().getBillModel().setValueAt(HRCMTermUnitUtils.getDateAfterMonth(begindate, promonth, trialtype),
                        evt.getRow(), CtrtVO.PROBENDDATE);
                }
            }
        }
        else if (ArrayUtils.contains(ctrtFlds, evt.getKey()) || CtrtVO.TERMTYPE.equals(evt.getKey()))
        {
            String unitName = (String) getBillCardPanel().getBillModel(evt.getTableCode()).getValueAt(evt.getRow(), CtrtVO.CONT_UNIT);
            Integer trialtype = HRCMTermUnitUtils.getTermUnit(unitName);
            // Ĭ������
            trialtype = trialtype == null ? HRCMTermUnitUtils.TERMUNIT_MONTH : trialtype;
            
            float days = HRCMTermUnitUtils.getDaysByUnit(trialtype);
            UFLiteralDate begindate =
                (UFLiteralDate) getBillCardPanel().getBillModel(evt.getTableCode()).getValueAt(evt.getRow(), CtrtVO.BEGINDATE);
            UFLiteralDate enddate =
                (UFLiteralDate) getBillCardPanel().getBillModel(evt.getTableCode()).getValueAt(evt.getRow(), CtrtVO.ENDDATE);
            Integer termmonth = (Integer) getBillCardPanel().getBillModel(evt.getTableCode()).getValueAt(evt.getRow(), CtrtVO.TERMMONTH);
            /*
             * if (CtrtVO.PK_TERMTYPE.equals(evt.getKey())) { refreshTermTypeStat1(evt); if (termmonth != null
             * && begindate != null) { getBillCardPanel().getBillModel().setValueAt(
             * HRCMTermUnitUtils.getDateAfterMonth(begindate, termmonth, trialtype), evt.getRow(),
             * CtrtVO.ENDDATE); } }else
             */if (CtrtVO.TERMTYPE.equals(evt.getKey()))
            {// ��ͬ����
                refreshTermTypeStat(evt);
                if (termmonth != null && begindate != null)
                {
                    getBillCardPanel().getBillModel().setValueAt(HRCMTermUnitUtils.getDateAfterMonth(begindate, termmonth, trialtype),
                        evt.getRow(), CtrtVO.ENDDATE);
                }
            }
            else if (evt.getKey().equals(CtrtVO.TERMMONTH))
            {// ��ͬ����
                if (termmonth == null)
                {
                    if (begindate != null && enddate != null)
                    {
                        getBillCardPanel().getBillModel().setValueAt(Math.round(UFLiteralDate.getDaysBetween(begindate, enddate) / days),
                            evt.getRow(), CtrtVO.TERMMONTH);
                    }
                }
                else if (begindate != null /* && enddate == null */)
                {
                    // ��ʼ���ڲ�Ϊ��,�����������
                    getBillCardPanel().getBillModel().setValueAt(HRCMTermUnitUtils.getDateAfterMonth(begindate, termmonth, trialtype),
                        evt.getRow(), CtrtVO.ENDDATE);
                }
                else if (enddate != null)
                {
                    // �������ڲ�Ϊ��,���㿪ʼ����
                    getBillCardPanel().getBillModel().setValueAt(HRCMTermUnitUtils.getDateBeforeMonth(enddate, termmonth, trialtype),
                        evt.getRow(), CtrtVO.BEGINDATE);
                }
                
            }
            else if (evt.getKey().equals(CtrtVO.BEGINDATE))
            {// ��ͬ��ʼ����
                if (termmonth != null)
                {
                    if (enddate == null)
                    {
                        getBillCardPanel().getBillModel().setValueAt(HRCMTermUnitUtils.getDateAfterMonth(begindate, termmonth, trialtype),
                            evt.getRow(), CtrtVO.ENDDATE);
                    }
                    else
                    {
                        if (begindate != null && (begindate.beforeDate(enddate) || begindate.compareTo(enddate) == 0))
                        {
                            getBillCardPanel().getBillModel().setValueAt(
                                Math.round(UFLiteralDate.getDaysBetween(begindate, enddate) / days), evt.getRow(), CtrtVO.TERMMONTH);
                        }
                        else
                        {
                            MessageDialog.showWarningDlg(this, null, ResHelper.getString("6007psn", "06007psn0351") /* "�������ڱ����ڿ�ʼ����֮��" */);
                            getBillCardPanel().getBillModel().setValueAt(null, evt.getRow(), CtrtVO.BEGINDATE);
                        }
                    }
                }
                else if (enddate != null)
                {
                    getBillCardPanel().getBillModel().setValueAt(Math.round(UFLiteralDate.getDaysBetween(begindate, enddate) / days),
                        evt.getRow(), CtrtVO.TERMMONTH);
                }
            }
            else if (evt.getKey().equals(CtrtVO.ENDDATE))
            {// ��ͬ��������
                if (enddate == null)
                {
                    if (begindate != null && termmonth != null)
                    {
                        // getBillCardPanel().getBillModel().setValueAt(VOUtils.getDateAfterMonth(begindate,
                        // termmonth, trialtype), evt.getRow(),
                        // CtrtVO.ENDDATE);
                    }
                }
                else if (begindate != null)
                {
                    if (termmonth == null)
                    {
                        getBillCardPanel().getBillModel().setValueAt(Math.round(UFLiteralDate.getDaysBetween(begindate, enddate) / days),
                            evt.getRow(), CtrtVO.TERMMONTH);
                    }
                }
                else
                {
                    getBillCardPanel().getBillModel().setValueAt(HRCMTermUnitUtils.getDateBeforeMonth(enddate, termmonth, trialtype),
                        evt.getRow(), CtrtVO.BEGINDATE);
                }
            }
            else if (evt.getKey().equals(CtrtVO.CONT_UNIT))
            {// ��ͬ���޵�λ
             // �����˺�ͬ���޵�λ����ͬ���޵ĳ���ҲҪ��Ӧ�ķ����仯
                Integer termUnit = (Integer) evt.getValue();
                getBillCardPanel().getBodyItem(CtrtVO.getDefaultTableName(), CtrtVO.TERMMONTH).setLength(
                    HRCMTermUnitUtils.getLengthByTermUnit(termUnit));
                
                if (termmonth == null)
                {// ��������Ϊ��
                    if (begindate != null && enddate != null)
                    {
                        getBillCardPanel().getBillModel().setValueAt(Math.round(UFLiteralDate.getDaysBetween(begindate, enddate) / days),
                            evt.getRow(), CtrtVO.TERMMONTH);
                    }
                }
                else if (begindate == null)
                {// ��ʼ����Ϊ��
                    if (enddate != null && termmonth != null)
                    {
                        getBillCardPanel().getBillModel().setValueAt(HRCMTermUnitUtils.getDateBeforeMonth(enddate, termmonth, trialtype),
                            evt.getRow(), CtrtVO.BEGINDATE);
                    }
                }
                else
                {
                    getBillCardPanel().getBillModel().setValueAt(HRCMTermUnitUtils.getDateAfterMonth(begindate, termmonth, trialtype),
                        evt.getRow(), CtrtVO.ENDDATE);
                }
            }
        }
    }
    
    // private void refreshTermTypeStat(BillEditEvent evt) {
    // String pkTermtype = (String) evt.getValue();
    // TermTypeVO vo = null;
    // try {
    // vo = StringUtils.isBlank(pkTermtype) ? null : NCLocator
    // .getInstance().lookup(ITermtypeQueryService.class)
    // .queryTermType(pkTermtype);
    // } catch (Exception e) {
    // Logger.error(e.getMessage(), e);
    // }
    // if (vo == null || vo.getTermcode() == null) {
    // return;
    // }
    // if (ITermTypePub.TERMTYPE_NONFIXED == vo.getTermtype()) { // �޹̶�����
    // clearBodyItemValue(evt.getTableCode(), evt.getRow(), new String[] {
    // CtrtVO.TERMMONTH, CtrtVO.ENDDATE });
    // setBodyItemEdit(evt.getTableCode(), evt.getRow(), false,
    // new String[] { CtrtVO.TERMMONTH, CtrtVO.ENDDATE });
    // setBodyItemEdit(evt.getTableCode(), evt.getRow(), true,
    // new String[] { CtrtVO.IFPROP });// �Ƿ����ÿɱ༭
    // } else if (ITermTypePub.TERMTYPE_TASK == vo.getTermtype()) { // �����һ����������Ϊ����
    // setBodyItemEdit(evt.getTableCode(), evt.getRow(), true,
    // new String[] { CtrtVO.TERMMONTH, CtrtVO.ENDDATE });
    // setBodyItemEdit(evt.getTableCode(), evt.getRow(), false,
    // new String[] { CtrtVO.IFPROP });// �Ƿ������ûҲ��ɱ༭
    // getBillCardPanel().getBillModel(evt.getTableCode()).setValueAt(
    // UFBoolean.FALSE, evt.getRow(), CtrtVO.IFPROP);
    // clearBodyItemValue(evt.getTableCode(), evt.getRow(), ctrtTrialFlds);
    // } else { // �̶�����
    // setBodyItemEdit(evt.getTableCode(), evt.getRow(), true,
    // new String[] { CtrtVO.TERMMONTH, CtrtVO.ENDDATE,
    // CtrtVO.IFPROP });
    // }
    // }
    
    private void refreshTermTypeStat(BillEditEvent evt)
    {
        String termtype = (String) evt.getValue();
        
        if (HRCMTermUnitUtils.TERM_TYPE_NONFIXED.equals(termtype))
        { // �޹̶�����
            clearBodyItemValue(evt.getTableCode(), evt.getRow(), new String[]{CtrtVO.TERMMONTH, CtrtVO.ENDDATE});
            setBodyItemEdit(evt.getTableCode(), evt.getRow(), false, new String[]{CtrtVO.TERMMONTH, CtrtVO.ENDDATE});
            setBodyItemEdit(evt.getTableCode(), evt.getRow(), true, new String[]{CtrtVO.IFPROP});// �Ƿ����ÿɱ༭
        }
        else if (HRCMTermUnitUtils.TERM_TYPE_TASK.equals(termtype))
        { // �����һ����������Ϊ����
            setBodyItemEdit(evt.getTableCode(), evt.getRow(), true, new String[]{CtrtVO.TERMMONTH, CtrtVO.ENDDATE});
            setBodyItemEdit(evt.getTableCode(), evt.getRow(), false, new String[]{CtrtVO.IFPROP});// �Ƿ������ûҲ��ɱ༭
            getBillCardPanel().getBillModel(evt.getTableCode()).setValueAt(UFBoolean.FALSE, evt.getRow(), CtrtVO.IFPROP);
            clearBodyItemValue(evt.getTableCode(), evt.getRow(), ctrtTrialFlds);
        }
        else
        { // �̶�����
            setBodyItemEdit(evt.getTableCode(), evt.getRow(), true, new String[]{CtrtVO.TERMMONTH, CtrtVO.ENDDATE, CtrtVO.IFPROP});
        }
    }
    
    private void setBodyItemEdit(String strTabCode, int iRowIndex, boolean isEdit, String... strBodyItemKeys)
    {
        if (strBodyItemKeys == null || strBodyItemKeys.length == 0)
        {
            return;
        }
        BillModel billModel = strTabCode == null ? getBillCardPanel().getBillModel() : getBillCardPanel().getBillModel(strTabCode);
        if (billModel == null)
        {
            return;
        }
        for (String strItemKey : strBodyItemKeys)
        {
            billModel.setCellEditable(iRowIndex, strItemKey, isEdit);
        }
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-5-20 15:16:41<br>
     * @see nc.ui.hr.uif2.view.HrBillFormEditor#afterEdit(nc.ui.pub.bill.BillEditEvent)
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public void afterEdit(BillEditEvent evt)
    {
        if (IBillItem.HEAD == evt.getPos())
        {
            afterHeadChange(evt);
        }
        else if (IBillItem.BODY == evt.getPos())
        {
            afterBodyChange(evt);
            
            BillItem item = this.getBillCardPanel().getBodyItem(evt.getTableCode(), evt.getKey());
            if (item != null)
            {
                // enddate->dateadd( begindate, glbdef1, "D")
                this.getBillCardPanel().getBillModel(evt.getTableCode()).execFormula(evt.getRow(), item.getEditFormulas());
                // this.getBillCardPanel().execBodyFormulas(evt.getRow(), item.getEditFormulas());
            }
        }
        super.afterEdit(evt);
        
    }
    
    /***************************************************************************
     * <br>
     * Created on 2010-6-8 9:07:36<br>
     * @param evt
     * @author Rocex Wang
     ***************************************************************************/
    private void afterHeadChange(BillEditEvent evt)
    {
        
        try
        {
            if (PsndocVO.NAME.equals(evt.getKey()))
            {
                // ����
                MultiLangText multiLangText = (MultiLangText) evt.getValue();
                BillItem item = getBillCardPanel().getHeadItem(PsndocVO.SHORTNAME);
                if (item != null && multiLangText != null)
                {
                    item.setValue(PinYinHelper.getPinYinHeadChar(multiLangText.getText(multiLangText.getCurrLangIndex())));
                }
            }
            else if ((PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_ORG).equals(evt.getKey()))
            {
                // ��֯
                // ��� ����
                clearHeadItemValue(new String[]{PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_DEPT});
                // �����λ��Ϊ�� ����ա���λ��ְ��ְ�����ְ�ȡ�ְ��
                Object obj = getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_POST);
                if (obj != null)
                {
                    clearHeadItemValue(new String[]{
                        PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_POST,
                        PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_POSTSERIES,
                        PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOB,
                        PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.SERIES,
                        PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBGRADE,
                        PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBRANK});
                }
            }
            else if ((PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_DEPT).equals(evt.getKey()))
            {
                // ����
                // ��� ��λ��ְ��ְ�����ְ�ȡ�ְ��
                Object obj = getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_POST);
                if (obj != null)
                {
                    clearHeadItemValue(new String[]{
                        PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_POST,
                        PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOB,
                        PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.SERIES,
                        PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBGRADE,
                        PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBRANK,
                        PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_POSTSERIES});
                }
            }
            else if ((PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_POST).equals(evt.getKey()))
            {
                // ��λ
                String pk_post = getStrValue(evt.getValue());
                PostVO post = pk_post == null ? null : getService().queryByPk(PostVO.class, pk_post, true);
                if (post != null)
                {
                    setHeadValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_DEPT, post.getPk_dept());// ����
                    setHeadValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_POSTSERIES, post.getPk_postseries());// ��λ����
                    setHeadValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOB, post.getPk_job());// ְ��
                    JobVO jobVO = post.getPk_job() == null ? null : getService().queryByPk(JobVO.class, post.getPk_job(), true);
                    if (jobVO != null)
                    {
                        setHeadValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.SERIES, jobVO.getPk_jobtype());// ְ�����
                    }
                    if (post.getEmployment() != null)
                    {
                        setHeadValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.OCCUPATION, post.getEmployment());// ְҵ
                    }
                    if (post.getWorktype() != null)
                    {
                        setHeadValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.WORKTYPE, post.getWorktype());// ����
                    }
                    
                    String defaultlevel = "";
                    String defaultrank = "";
                    Map<String, String> resultMap =
                        NCLocator.getInstance().lookup(IPsndocQryService.class).getDefaultLevelRank(null, null, null, pk_post, null);
                    if (!resultMap.isEmpty())
                    {
                        defaultlevel = resultMap.get("defaultlevel");
                        defaultrank = resultMap.get("defaultrank");
                    }
                    
                    setHeadValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBGRADE, defaultlevel);// ְ��
                    setHeadValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBRANK, defaultrank);// ְ��
                    
                    // ְ��Ϊ��ʱ,ְ�����ɱ༭
                    getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBGRADE).setEnabled(true);
                    // ���ְ��,ͬʱ���ְ��,��ʱְ�ȿ��Ա༭
                    getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBRANK).setEnabled(true);
                    getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_POSTSERIES).setEnabled(false);
                }
                else
                {
                    setHeadValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_POSTSERIES, null);// ��λ����
                    setHeadValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOB, null);// ְ��
                    setHeadValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBGRADE, null);
                    setHeadValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBRANK, null);
                    setHeadValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.SERIES, null);// ְ�����
                    getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOB).setEnabled(true);
                    getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_POSTSERIES).setEnabled(true);
                }
                
                if (post == null)
                {
                    BillEditEvent event =
                        new BillEditEvent(getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOB),
                            post == null ? null : post.getPk_job(), PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOB, evt.getRow(),
                            evt.getPos());
                    event.setTableCode(evt.getTableCode());
                    afterHeadChange(event);
                }
            }
            else if ((PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOB).equals(evt.getKey()))
            {
                // ְ��
                String pk_job = getStrValue(evt.getValue());
                String pk_post = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_POST);
                JobVO job = pk_job == null ? null : getService().queryByPk(JobVO.class, pk_job, true);
                if (job != null)
                {
                    String defaultlevel = "";
                    String defaultrank = "";
                    Map<String, String> resultMap =
                        NCLocator.getInstance().lookup(IPsndocQryService.class).getDefaultLevelRank(null, pk_job, null, pk_post, null);
                    if (!resultMap.isEmpty())
                    {
                        defaultlevel = resultMap.get("defaultlevel");
                        defaultrank = resultMap.get("defaultrank");
                    }
                    
                    setHeadValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.SERIES, job.getPk_jobtype());// ְ�����
                    setHeadValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBGRADE, defaultlevel);// ְ��
                    setHeadValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBRANK, defaultrank);// ְ��
                    
                    getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.SERIES).setEnabled(false);
                }
                else
                {
                    setHeadValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.SERIES, null);// ְ�����
                    setHeadValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBGRADE, null);// ְ��
                    setHeadValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBRANK, null);// ְ��
                    getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.SERIES).setEnabled(true);
                }
                getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBRANK).setEnabled(true);
                getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBGRADE).setEnabled(true);
            }
            else if ((PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.SERIES).equals(evt.getKey()))
            {
                // ְ�����
                String series = getStrValue(evt.getValue());
                String pk_job = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOB);
                String pk_post = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_POST);
                if (StringUtils.isBlank(pk_job) && StringUtils.isNotBlank(series))
                {
                    String defaultlevel = "";
                    String defaultrank = "";
                    Map<String, String> resultMap =
                        NCLocator.getInstance().lookup(IPsndocQryService.class).getDefaultLevelRank(series, pk_job, null, pk_post, null);
                    if (!resultMap.isEmpty())
                    {
                        defaultlevel = resultMap.get("defaultlevel");
                        defaultrank = resultMap.get("defaultrank");
                    }
                    
                    setHeadValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBGRADE, defaultlevel);// ְ��
                    setHeadValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBRANK, defaultrank);// ְ��
                }
                else if (StringUtils.isBlank(pk_job) && StringUtils.isBlank(series))
                {
                    setHeadValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBGRADE, null);// ְ��
                    setHeadValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBRANK, null);// ְ��
                }
            }
            else if ((PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_POSTSERIES).equals(evt.getKey()))
            {
                // ��λ����
                String pk_postseries = getStrValue(evt.getValue());
                String pk_job = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOB);
                String pk_post = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_POST);
                String series = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.SERIES);
                if (StringUtils.isBlank(pk_job) && StringUtils.isBlank(series) && StringUtils.isBlank(pk_post)
                    && StringUtils.isNotBlank(pk_postseries))
                {
                    String defaultlevel = "";
                    String defaultrank = "";
                    Map<String, String> resultMap =
                        NCLocator.getInstance().lookup(IPsndocQryService.class)
                            .getDefaultLevelRank(series, pk_job, pk_postseries, pk_post, null);
                    if (!resultMap.isEmpty())
                    {
                        defaultlevel = resultMap.get("defaultlevel");
                        defaultrank = resultMap.get("defaultrank");
                    }
                    
                    setHeadValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBGRADE, defaultlevel);// ְ��
                    setHeadValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBRANK, defaultrank);// ְ��
                }
                else if (StringUtils.isBlank(pk_job) && StringUtils.isBlank(series) && StringUtils.isBlank(pk_post)
                    && StringUtils.isBlank(pk_postseries))
                {
                    setHeadValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBGRADE, null);// ְ��
                    setHeadValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBRANK, null);// ְ��
                }
            }
            else if ((PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBGRADE).equals(evt.getKey()))
            {
                // ְ��
                String pk_jobgrage = getStrValue(evt.getValue());
                if (StringUtils.isNotBlank(pk_jobgrage))
                {
                    String pk_postseries = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_POSTSERIES);
                    String pk_job = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOB);
                    String pk_post = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_POST);
                    String series = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.SERIES);
                    String defaultrank = "";
                    Map<String, String> resultMap =
                        NCLocator.getInstance().lookup(IPsndocQryService.class)
                            .getDefaultLevelRank(series, pk_job, pk_postseries, pk_post, pk_jobgrage);
                    if (!resultMap.isEmpty())
                    {
                        defaultrank = resultMap.get("defaultrank");
                    }
                    setHeadValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBRANK, defaultrank);// ְ��
                }
                else
                {
                    // ְ����պ�,��ְ�������ְ��,����ְ���ϵ�ְ��
                    setHeadValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBRANK, null);// ְ��
                    getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBRANK).setEnabled(true);
                }
            }
            else if ((PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.TRNSEVENT).equals(evt.getKey()))
            {
                // �춯�¼�
                Object objValue = getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.TRNSEVENT);
                BillItem item = getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.TRNSTYPE);
                if (item != null)
                {
                    item.clearViewData();
                    ((UIRefPane) item.getComponent()).getRefModel().setWherePart(PsnJobVO.TRNSEVENT + "=" + objValue);
                }
            }
            else if ((PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.ENDFLAG).equals(evt.getKey()))
            {
                // �Ƿ����
                Object objValue = getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.ENDFLAG);
                BillItem item = getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.POSTSTAT);
                if (item != null)
                {
                    item.setValue(UFBoolean.valueOf(!((Boolean) objValue).booleanValue()));
                }
                item = getBillCardPanel().getHeadItem(PsnOrgVO.getDefaultTableName() + "_" + PsnOrgVO.ENDFLAG);
                if (item != null)
                {
                    item.setValue(objValue);
                }
            }
            else if ((PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.TRIAL_FLAG).equals(evt.getKey()))
            {
                // �Ƿ�����
                Object objValue = getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.TRIAL_FLAG);
                BillItem item = getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.TRIAL_TYPE);
                if (item != null)
                {
                    item.clearViewData();
                    item.setEdit(objValue instanceof Boolean ? (Boolean) objValue : false);
                }
            }
            // ������д֤�����ͻ���֤����ʱ���������֤���Ӽ�����
            else if (PsndocVO.ID.equals(evt.getKey()))
            {
                // ֤����
                // clearBodyItemValue(CertVO.getDefaultTableName(), 0, CertVO.ID);
                Object obj = getHeadItemValue(PsndocVO.ID);
                // setBodyValue(CertVO.getDefaultTableName(), 0, CertVO.ID, obj);
                Object objidtype = getHeadItemValue(PsndocVO.IDTYPE);
                HashMap<String, Object> map = generateGenderAndBirthdayFromID((String) obj, (String) objidtype);
                if (map != null)
                {
                    setHeadItemValue(PsndocVO.SEX, map.get("sex"));
                    setHeadItemValue(PsndocVO.BIRTHDATE, map.get("birthday"));
                }
            }
            else if (PsndocVO.IDTYPE.equals(evt.getKey()))
            {
                // ֤������
                // clearBodyItemValue(CertVO.getDefaultTableName(), 0, CertVO.IDTYPE);
                Object obj = getHeadItemValue(PsndocVO.IDTYPE);
                // setBodyValue(CertVO.getDefaultTableName(), 0, CertVO.IDTYPE, obj);
                Object objid = getHeadItemValue(PsndocVO.ID);
                HashMap<String, Object> map = generateGenderAndBirthdayFromID((String) objid, (String) obj);
                if (map != null)
                {
                    setHeadItemValue(PsndocVO.SEX, map.get("sex"));
                    setHeadItemValue(PsndocVO.BIRTHDATE, map.get("birthday"));
                }
            }
            
            else if("sg_dprapprovaldate".equals(evt.getKey())){
            	//�����PR,�������������ڵĻ�, ��Ҫ�Զ����ɱ�������� add by weiningc 20200326 start
            	PsnIdtypeVO psnIdtypeVO =
            			(PsnIdtypeVO) NCLocator.getInstance(). 
            			lookup(IPersistenceRetrieve.class).retrieveByPk(null, PsnIdtypeVO.class, getHeadItemValue(PsndocVO.IDTYPE).toString());
            	String sgprcode = psnIdtypeVO.getCode();
            	if("NIRC-BLUE".equals(psnIdtypeVO.getCode())) {
            		UFDate sgapprovedate = (UFDate) getHeadItemValue("sg_dprapprovaldate");
            		String id = (String) getHeadItemValue(PsndocVO.ID);
            		this.createCert(sgapprovedate, id, psnIdtypeVO);
            	}
            }
            //end
            
            String series = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.SERIES);
            String postseries = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_POSTSERIES);
            String pk_job = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOB);
            String pk_post = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_POST);
            if (StringUtils.isBlank(series) && StringUtils.isBlank(postseries) && StringUtils.isBlank(pk_job)
                && StringUtils.isBlank(pk_post))
            {
                getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBGRADE).setEnabled(false);
            }
            else
            {
                getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBGRADE).setEnabled(true);
            }
            
        }
        catch (Exception ex)
        {
            Logger.error(ex.getMessage(), ex);
        }
    }
    
    private void createCert(UFDate sgapprovedate, String id, PsnIdtypeVO idtypevo) {
    	//����Ӽ���PR�������Ŀ,������ ���ȫ������PR����Ŀ��ɾ������������
    	Boolean isautoadd = false;
    	BillModel certmodel = getBillCardPanel().getBillModel(CertVO.getDefaultTableName());
    	if(certmodel != null) {
    		int rowCount = certmodel.getRowCount();
    		for(int i=0; i<rowCount; i++) {
    			String prcode = (String) getBillCardPanel().getBodyValueAt(i, CertVO.MEMO);
    			if(!StringUtils.isBlank(prcode) && prcode.startsWith("PR0")) {
    				isautoadd = true;
    			}
    		}
    	}
    	
    	if(!isautoadd) {
    		certmodel.clearBodyData();
    		int recount = certmodel.getRowCount();
    		int count = 1;
    		//effectivedate
    		UFDate effectivedate_pr01 = sgapprovedate.getDateAfter(365);
    		UFDate effectivedate_pr02 = effectivedate_pr01.getDateAfter(365);
    		UFDate effectivedate_pr03 = effectivedate_pr02.getDateAfter(365);
    		Map<String, UFDate> prdate = new HashMap<String, UFDate>();
    		prdate.put("PR01", getUFDateByCondiftion(effectivedate_pr01, true, false, null));
    		prdate.put("PR02", getUFDateByCondiftion(effectivedate_pr02, false, true, 1));
    		prdate.put("PR03", getUFDateByCondiftion(effectivedate_pr03, false, true, 1));
    		
    		for(int i=recount; i<recount + 3; i++) {
    			certmodel.addLine();
    			getBillCardPanel().setBodyValueAt(idtypevo.getPk_identitype(), i, CertVO.IDTYPE);
    			getBillCardPanel().setBodyValueAt(id, i, CertVO.ID);
    			getBillCardPanel().setBodyValueAt(getModel().getContext().getPk_group(), i, CertVO.PK_GROUP);
    			getBillCardPanel().setBodyValueAt(getModel().getContext().getPk_org(), i, CertVO.PK_ORG);
    			getBillCardPanel().setBodyValueAt(UFBoolean.TRUE, i, CertVO.ISEFFECT);
    			getBillCardPanel().setBodyValueAt(UFBoolean.TRUE, i, CertVO.ISSTART);
    			getBillCardPanel().setBodyValueAt(getModel().getContext().getPk_loginUser(), i, CertVO.CREATOR);
    			getBillCardPanel().setBodyValueAt(PubEnv.getServerTime(), i, CertVO.CREATIONTIME);
    			getBillCardPanel().setBodyValueAt("PR0" + count, i, CertVO.MEMO);
    			getBillCardPanel().setBodyValueAt(prdate.get("PR0" + count), i, "enddate");
    			count ++;
    		}
    	}
    	
//    	CertVO cert = new CertVO();
//        cert.setIdtype(psndocVO.getIdtype());
//        cert.setId(psndocVO.getId());
//        cert.setPk_group(getModel().getContext().getPk_group());
//        cert.setPk_org(getModel().getContext().getPk_org());
//        cert.setIseffect(UFBoolean.TRUE);
//        cert.setIsstart(UFBoolean.TRUE);
//        cert.setCreator(getModel().getContext().getPk_loginUser());
//        cert.setCreationtime(PubEnv.getServerTime());
		
	}
    
    private UFDate getUFDateByCondiftion(UFDate ufdate, Boolean monthstart, Boolean nextmonth, Integer n_month) {
    	SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
    	Calendar calendar = java.util.Calendar.getInstance(); 
    	calendar.setTime(ufdate.toDate());
    	if(monthstart) {
    		int dayofstart = calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
    		calendar.set(Calendar.DATE, dayofstart);
    		calendar.set(Calendar.HOUR_OF_DAY, 00);
    		calendar.set(Calendar.MINUTE, 00);
    		calendar.set(Calendar.SECOND, 00);
    	}
    	if(nextmonth != null) {
    		calendar.add(Calendar.DAY_OF_MONTH, n_month);
    		int dayofstart = calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
    		calendar.set(Calendar.DATE, dayofstart);
    		calendar.set(Calendar.HOUR_OF_DAY, 00);
    		calendar.set(Calendar.MINUTE, 00);
    		calendar.set(Calendar.SECOND, 00);
    	}
    	
    	return new UFDate(df.format(calendar.getTime()));
    }

	private HashMap<String, Object> generateGenderAndBirthdayFromID(String id, String idtype)
    {
        HashMap<String, Object> map = new HashMap<String, Object>();
        if (id == null || id.length() < 1 || idtype == null || !idtype.equals("1001Z01000000000AI36"))
        {
            return null;
        }
        map.put("sex", getSex(id));
        UFLiteralDate birthday = null;
        try
        {
            birthday = getBirthdate(id) == null ? null : UFLiteralDate.getDate(getBirthdate(id));
        }
        catch (Exception e)
        {
            birthday = null;
        }
        map.put("birthday", birthday);
        return map;
    }
    
    private Integer getSex(String ID)
    {
        if (ID.length() != 15 && ID.length() != 18)
        {
            return null;
        }
        int isex = 2;
        isex = ID.length() == 15 ? Integer.parseInt(ID.substring(14)) : Integer.parseInt(ID.substring(16, 17));
        return isex % 2 == 0 ? 2 : 1;
    }
    
    private String getBirthdate(String ID)
    {
        if (ID.length() != 15 && ID.length() != 18)
        {
            // ����15λ��18λ����null
            return null;
        }
        String birth = ID.length() == 15 ? "19" + ID.substring(6, 12) : ID.substring(6, 14);
        String year = birth.substring(0, 4);
        String month = birth.substring(4, 6);
        String date = birth.substring(6);
        return year + "-" + month + "-" + date;
    }
    
    /**
     * Ϊ��ͷ��Ŀ��ֵ
     */
    protected void setHeadValue(String itemKey, Object value)
    {
        getBillCardPanel().getHeadItem(itemKey).setValue(null);
        if (value != null)
        {
            getBillCardPanel().getHeadItem(itemKey).setValue(value);
        }
    }
    
    /**
     * Ϊ������Ŀ��ֵ
     */
    protected void setBodyValue(String tabCode, int row, String itemKey, Object value)
    {
        getBillCardPanel().getBillModel(tabCode).setValueAt(null, row, itemKey);
        if (value != null)
        {
            getBillCardPanel().getBillModel(tabCode).setValueAt(value, row, itemKey);
        }
    }
    
    /**
     * �õ����յ�ֵ
     */
    private String getStrValue(Object value)
    {
        if (value == null)
        {
            return null;
        }
        if (value instanceof String)
        {
            return (String) value;
        }
        else if (value instanceof String[])
        {
            return ((String[]) value)[0];
        }
        return value.toString();
    }
    
    /***************************************************************************
     * �춯�¼� �仯���¼�<br>
     * Created on 2010-6-8 8:55:22<br>
     * @param evt
     * @author Rocex Wang
     ***************************************************************************/
    private void afterTrnsEventChange(BillEditEvent evt)
    {
        Object objValue = evt.getValue();
        // ��ְ����ְ��䶯 �����ﲻ��ѡ
        if (ArrayUtils.contains(new Object[]{TrnseventEnum.DISMISSION.value(), TrnseventEnum.TRANSAFTERDIS.value()}, objValue))
        {
            String msg = ResHelper.getString("6007psn", "06007psn0165")/*
                                                                        * @res "����ְ��������ְ��䶯������ѡ��"
                                                                        */;
            ShowStatusBarMsgUtil.showErrorMsgWithClear(msg, msg, getModel().getContext());
            getBillCardPanel().getBillModel(evt.getTableCode()).setValueAt(evt.getOldValue(), evt.getRow(), PsnJobVO.TRNSEVENT);
            return;
        }
        BillItem item = getBillCardPanel().getBodyItem(evt.getTableCode(), PsnJobVO.TRNSTYPE);
        if (item != null)
        {
            getBillCardPanel().setBodyValueAt(null, evt.getRow(), PsnJobVO.TRNSTYPE, evt.getTableCode());
            ((UIRefPane) item.getComponent()).getRefModel().setWherePart(PsnJobVO.TRNSEVENT + "=" + objValue);
        }
    }
    
    /****************************************************************************
     * �����ֶα༭ǰ�¼�{@inheritDoc}<br>
     * Created on 2010-6-8 9:31:01<br>
     * @see nc.ui.pub.bill.BillEditListener2#beforeEdit(nc.ui.pub.bill.BillEditEvent)
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public boolean beforeEdit(BillEditEvent evt)
    {
        BillItem billItemSource = (BillItem) evt.getSource();
        if (HICommonValue.FUNC_CODE_REGISTER.equals(getModel().getContext().getNodeCode())
            && PsnJobVO.getDefaultTableName().equals(evt.getTableCode()))// ���µ���ְ��¼�������ӱ���ά��
        {
            PsndocAggVO psndocAggVO = (PsndocAggVO) getModel().getSelectedData();
            BillModel billModel = getBillCardPanel().getBillModel(PsnJobVO.getDefaultTableName());
            if ((psndocAggVO == null || psndocAggVO.getParentVO() == null || psndocAggVO.getParentVO().getPsnJobVO() == null || billModel == null)
                && getModel().getUiState() != UIState.ADD)
            {
                // ��������������²��ܱ༭
                return false;
            }
            int roeCount = getBillCardPanel().getBillTable(evt.getTableCode()).getRowCount();
            if (roeCount > 0 && evt.getRow() == roeCount - 1 && getModel().getUiState() != UIState.ADD)
            {
                // �༭ʱ ���¼�¼�����޸�
                return false;
            }
        }
        
        if (PartTimeVO.getDefaultTableName().equals(evt.getTableCode()) && PsnJobVO.PK_ORG.equals(evt.getKey()))
        {
            // ��֯
            beforePartTimePk_OrgEdit(evt);
        }
        else if (PsnJobVO.getDefaultTableName().equals(evt.getTableCode()) && PsnJobVO.PK_ORG.equals(evt.getKey()))
        {
            // ��֯
            BillItem item = (BillItem) evt.getSource();
            if (item != null)
            {
                String enableSql = " and pk_adminorg in (select pk_adminorg from org_admin_enable) ";
                String powerSql =
                    HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), HICommonValue.RESOUCECODE_ORG, IRefConst.DATAPOWEROPERATION_CODE,
                        "org_orgs");
                if (!StringUtils.isBlank(powerSql))
                {
                    enableSql = enableSql + " and pk_adminorg in ( select pk_org from org_orgs where " + powerSql + ")";
                }
                // �𼶹ܿ�(��ְ�Ǽǡ���������Ϣ-��֯)
                try
                {
                    String gkSql =
                        NCLocator.getInstance().lookup(IPsndocService.class)
                            .queryControlSql("@@@@Z710000000006M1Y", getModel().getContext().getPk_org(), true);
                    if (!StringUtils.isEmpty(gkSql))
                    {
                        enableSql += " and org_adminorg.pk_adminorg in ( " + gkSql + " )";
                    }
                }
                catch (BusinessException e1)
                {
                    Logger.error(e1.getMessage(), e1);
                }
                ((UIRefPane) item.getComponent()).getRefModel().setUseDataPower(false);
                ((UIRefPane) item.getComponent()).getRefModel().addWherePart(enableSql);
            }
        }
        else if (ArrayUtils.contains(new String[]{PsnJobVO.getDefaultTableName(), PartTimeVO.getDefaultTableName()}, evt.getTableCode())
            && PsnJobVO.PK_DEPT.equals(evt.getKey()))
        {
            // ����
            beforePkDeptEdit(evt, evt.getTableCode());
        }
        else if (ArrayUtils.contains(new String[]{PsnJobVO.getDefaultTableName(), PartTimeVO.getDefaultTableName()}, evt.getTableCode())
            && PsnJobVO.TRNSTYPE.equals(evt.getKey()))
        {
            // �춯����
            beforeTrnsEventEdit(evt);
        }
        else if (ArrayUtils.contains(new String[]{PsnJobVO.getDefaultTableName(), PartTimeVO.getDefaultTableName()}, evt.getTableCode())
            && PsnJobVO.PK_POST.equals(evt.getKey()))
        {
            // ��λ
            beforePkPostEdit(evt);
        }
        else if (ArrayUtils.contains(new String[]{PsnJobVO.getDefaultTableName(), PartTimeVO.getDefaultTableName()}, evt.getTableCode())
            && PsnJobVO.PK_JOB.equals(evt.getKey()))
        {
            // ְ��
            PsnJobVO psnjob =
                (PsnJobVO) getBillCardPanel().getBillModel(evt.getTableCode()).getBodyValueRowVO(evt.getRow(), PsnJobVO.class.getName());
            // if (psnjob.getPk_post() != null)
            // {
            // return false;
            // }
            beforePkJobEdit(evt);
        }
        else if (ArrayUtils.contains(new String[]{PsnJobVO.getDefaultTableName(), PartTimeVO.getDefaultTableName()}, evt.getTableCode())
            && PsnJobVO.SERIES.equals(evt.getKey()))
        {
            // ְ�����
            String pk_job = (String) getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_JOB + IBillItem.ID_SUFFIX, evt.getRow());
            String pk_post = (String) getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_POST + IBillItem.ID_SUFFIX, evt.getRow());
            if (StringUtils.isNotBlank(pk_job) || StringUtils.isNotBlank(pk_post))
            {
                return false;
            }
        }
        else if (ArrayUtils.contains(new String[]{PsnJobVO.getDefaultTableName(), PartTimeVO.getDefaultTableName()}, evt.getTableCode())
            && PsnJobVO.PK_POSTSERIES.equals(evt.getKey()))
        {
            // ��λ����
            String pk_post = (String) getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_POST + IBillItem.ID_SUFFIX, evt.getRow());
            if (StringUtils.isNotBlank(pk_post))
            {
                return false;
            }
        }
        else if (ArrayUtils.contains(new String[]{PsnJobVO.getDefaultTableName(), PartTimeVO.getDefaultTableName()}, evt.getTableCode())
            && PsnJobVO.PK_JOBGRADE.equals(evt.getKey()))
        {
            // ְ��
            beforePkJobGradeEdit(evt);
        }
        else if (ArrayUtils.contains(new String[]{PsnJobVO.getDefaultTableName(), PartTimeVO.getDefaultTableName()}, evt.getTableCode())
            && PsnJobVO.PK_JOBRANK.equals(evt.getKey()))
        {
            // ְ��
            BillItem item = (BillItem) evt.getSource();
            String pk_jobrank = (String) getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_JOBRANK + IBillItem.ID_SUFFIX, evt.getRow());
            if (StringUtils.isBlank(pk_jobrank))
            {
                ((JobRankRefModel) ((UIRefPane) item.getComponent()).getRefModel()).setPk_joblevel("");
                return true;
            }
            
            String pk_jobgrade = (String) getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_JOBGRADE + IBillItem.ID_SUFFIX, evt.getRow());
            String pk_job = (String) getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_JOB + IBillItem.ID_SUFFIX, evt.getRow());
            String pk_post = (String) getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_POST + IBillItem.ID_SUFFIX, evt.getRow());
            String pk_postseries =
                (String) getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_POSTSERIES + IBillItem.ID_SUFFIX, evt.getRow());
            String pk_jobtype = (String) getBodyItemValue(evt.getTableCode(), PsnJobVO.SERIES + IBillItem.ID_SUFFIX, evt.getRow());
            if (item != null)
            {
                FilterTypeEnum filterType = null;
                String gradeSource = "";
                Map<String, Object> resultMap = null;
                try
                {
                    resultMap =
                        NCLocator.getInstance().lookup(IPsndocQryService.class)
                            .getLevelRankCondition(pk_jobtype, pk_job, pk_postseries, pk_post);
                }
                catch (BusinessException e)
                {
                    Logger.error(e.getMessage(), e);
                }
                
                if (!resultMap.isEmpty())
                {
                    filterType = (FilterTypeEnum) resultMap.get("filterType");
                    gradeSource = (String) resultMap.get("gradeSource");
                }
                ((JobRankRefModel) ((UIRefPane) item.getComponent()).getRefModel()).setPk_joblevel(pk_jobgrade);
                ((JobRankRefModel) ((UIRefPane) item.getComponent()).getRefModel()).setPk_filtertype(gradeSource, filterType);
            }
        }
        else if (ReqVO.getDefaultTableName().equals(evt.getTableCode()) && ReqVO.PK_POSTREQUIRE_B.equals(evt.getKey()))
        {
            // �ﵽ����
            beforePkPostRequire_b(evt);
        }
        else if (CapaVO.getDefaultTableName().equals(evt.getTableCode()) && CapaVO.PK_PE_SCOGRDITEM.equals(evt.getKey()))
        {
            // Ա���ﵽ�ȼ�
            BillItem item = (BillItem) evt.getSource();
            // Object objValue = getBillCardPanel().getBodyItem(evt.getTableCode(),
            // CapaVO.PK_PE_INDI).getValueObject();
            Object objValue = getBodyItemValue(evt.getTableCode(), CapaVO.PK_PE_INDI + IBillItem.ID_SUFFIX, evt.getRow());
            if (objValue != null)
            {
                ((CPindiGradeRefModel) ((UIRefPane) item.getComponent()).getRefModel()).setPk_indi((String) objValue);
            }
        }
        else if (PsnChgVO.getDefaultTableName().endsWith(evt.getTableCode()) && PsnChgVO.PK_CORP.equals(evt.getKey()))
        {
            // ��ְ��λ
            // AdminOrgDefaultRefModel model = new AdminOrgDefaultRefModel();
            ((UIRefPane) ((BillItem) evt.getSource()).getComponent()).getRefModel().setPk_group(PubEnv.getPk_group());
            String powerSql =
                HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), HICommonValue.RESOUCECODE_ORG, IRefConst.DATAPOWEROPERATION_CODE,
                    "org_orgs");
            ((UIRefPane) ((BillItem) evt.getSource()).getComponent()).getRefModel().setUseDataPower(false);
            if (!StringUtils.isBlank(powerSql))
            {
                ((UIRefPane) ((BillItem) evt.getSource()).getComponent()).getRefModel().addWherePart(
                    " and pk_adminorg in ( select pk_org from org_orgs where " + powerSql + ")");
            }
            String where =
                " and pk_adminorg in ( select pk_adminorg from org_admin_enable ) and pk_adminorg in ( select pk_corp from org_corp where enablestate = 2 )";
            ((UIRefPane) ((BillItem) evt.getSource()).getComponent()).getRefModel().addWherePart(where);
            // ((UIRefPane) ((BillItem)
            // evt.getSource()).getComponent()).setRefModel(model);
        }
        else if (CtrtVO.getDefaultTableName().endsWith(evt.getTableCode()))
        {
            return beforeCtrtEdit(evt);
        }
        else if (CertVO.getDefaultTableName().endsWith(evt.getTableCode()))
        {
            Boolean isStart = (Boolean) getBodyItemValue(CertVO.getDefaultTableName(), CertVO.ISSTART, evt.getRow());
            
            if (null != isStart)
            {
                if (Boolean.TRUE.equals(isStart))
                {
                    setBodyItemEdit(evt.getTableCode(), evt.getRow(), false, new String[]{CertVO.IDTYPE, CertVO.ID});
                }
            }
        }
        else if (ArrayUtils.contains(new String[]{PsnJobVO.getDefaultTableName(), PartTimeVO.getDefaultTableName()}, evt.getTableCode())
            && PsnJobVO.PK_PSNCL.equals(evt.getKey()))
        {
            // ��Ա���
            Object grpObjValue = getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_GROUP + IBillItem.ID_SUFFIX, evt.getRow());
            BillItem item = (BillItem) evt.getSource();
            if (item != null)
            {
                String powerSql =
                    HiSQLHelper.getPsnPowerSql((String) grpObjValue, HICommonValue.RESOUCECODE_PSNCL, IRefConst.DATAPOWEROPERATION_CODE,
                        "bd_psncl");
                if (!StringUtils.isBlank(powerSql))
                {
                    ((UIRefPane) item.getComponent()).getRefModel().addWherePart(" and " + powerSql);
                }
            }
        }
        else if (PsnJobVO.POSTSTAT.equals(evt.getKey()))
        {
            // �Ƿ��ڸ�
            if (PartTimeVO.getDefaultTableName().equals(evt.getTableCode()))
            {
                Boolean isEnd = (Boolean) getBillCardPanel().getBillModel(evt.getTableCode()).getValueAt(evt.getRow(), PsnJobVO.ENDFLAG);
                return isEnd != null && !isEnd;
            }
        }
        else if (KeyPsnVO.getDefaultTableName().endsWith(evt.getTableCode()))
        {
            Boolean endflag = (Boolean) getBillCardPanel().getBillModel(evt.getTableCode()).getValueAt(evt.getRow(), KeyPsnVO.ENDFLAG);
            if (endflag != null && endflag.booleanValue() && !evt.getKey().equals(KeyPsnVO.MEMO))
            {
                return false;
            }
        }
        
        selectedRow = evt.getRow();
        return billItemSource.isEdit();
    }
    
    /**
     * ��ͬҳǩ�༭ǰ����
     * @return
     */
    private boolean beforeCtrtEdit(BillEditEvent evt)
    {
        if (evt.getRow() == 0 && CtrtVO.CONTTYPE.equals(evt.getKey()))
        {
            return false;
        }
        if (ArrayUtils.contains(ctrtTrialFlds, evt.getKey()))
        {
            Boolean ifProp = (Boolean) getBillCardPanel().getBillModel(evt.getTableCode()).getValueAt(evt.getRow(), CtrtVO.IFPROP);
            return ifProp != null && ifProp.booleanValue();
        }
        
        if (CtrtVO.PK_CONTTEXT.equals(evt.getKey()))
        {
            BillItem item = (BillItem) evt.getSource();
            UIRefPane rp = (UIRefPane) item.getComponent();
            /** 1002Z710000000017GUF�Ǻ�ͬģ������ */
            rp.getRefModel().addWherePart(
                " and hrcm_contmodel.pk_org in ('" + IOrgConst.GLOBEORG + "','" + getModel().getContext().getPk_group() + "','"
                    + getModel().getContext().getPk_org() + "') and hrcm_contmodel.VMODELTYPE = '1002Z710000000017GUF' ");
            return item.isEdit();
        }
        
        String termtype = (String) getBodyItemValue(CtrtVO.getDefaultTableName(), CtrtVO.TERMTYPE, evt.getRow());
        
        if (StringUtils.isNotBlank(termtype))
        {
            if (HRCMTermUnitUtils.TERM_TYPE_NONFIXED.equals(termtype))
            { // �޹̶�����
                setBodyItemEdit(evt.getTableCode(), evt.getRow(), false, new String[]{CtrtVO.TERMMONTH, CtrtVO.ENDDATE});
                setBodyItemEdit(evt.getTableCode(), evt.getRow(), true, new String[]{CtrtVO.IFPROP});// �Ƿ����ÿɱ༭
            }
            else if (HRCMTermUnitUtils.TERM_TYPE_TASK.equals(termtype))
            { // �����һ����������Ϊ����
                setBodyItemEdit(evt.getTableCode(), evt.getRow(), true, new String[]{CtrtVO.TERMMONTH, CtrtVO.ENDDATE});
                setBodyItemEdit(evt.getTableCode(), evt.getRow(), false, new String[]{CtrtVO.IFPROP});// �Ƿ������ûҲ��ɱ༭
            }
            else
            { // �̶�����
                setBodyItemEdit(evt.getTableCode(), evt.getRow(), true, new String[]{CtrtVO.TERMMONTH, CtrtVO.ENDDATE, CtrtVO.IFPROP});
            }
        }
        
        return getBillCardPanel().getBillModel(evt.getTableCode()).getItemByKey(evt.getKey()).isEdit();
    }
    
    /****************************************************************************
     * ��ͷ�ֶα༭ǰ�¼�{@inheritDoc}<br>
     * Created on 2010-5-20 15:03:32<br>
     * @see nc.ui.pub.bill.BillCardBeforeEditListener#beforeEdit(nc.ui.pub.bill.BillItemEvent)
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public boolean beforeEdit(BillItemEvent evt)
    {
        if ((PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_DEPT).equals(evt.getItem().getKey()))
        {
            // ����
            Object objValue = getHeadItemValue((PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_ORG));
            if (evt.getItem() != null)
            {
                ((UIRefPane) evt.getItem().getComponent()).getRefModel().setPk_org((String) objValue);
                String cond = " and ( " + SQLHelper.getNullSql("hrcanceled") + " or hrcanceled = 'N' ) and depttype <> 1 ";
                String powerSql =
                    HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), HICommonValue.RESOUCECODE_DEPT, IRefConst.DATAPOWEROPERATION_CODE,
                        "org_dept");
                // getPowerSql(HICommonValue.RESOUCECODE_DEPT);
                if (!StringUtils.isBlank(powerSql))
                {
                    cond += " and " + powerSql;
                }
                ((UIRefPane) evt.getItem().getComponent()).getRefModel().addWherePart(cond);
            }
        }
        else if ((PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.TRNSTYPE).equals(evt.getItem().getKey()))
        {
            // ��ְ����
            String strTrnEvent = PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.TRNSEVENT;
            Object objValue = getHeadItemValue(strTrnEvent);
            if (objValue != null)
            {
                BillItem item = evt.getItem();
                if (item != null)
                {
                    ((UIRefPane) item.getComponent()).getRefModel().addWherePart(" and " + PsnJobVO.TRNSEVENT + "=" + objValue);
                }
            }
        }
        else if ((PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_POST).equals(evt.getItem().getKey()))
        {
            // ��λ
            String pk_org = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_ORG);
            String pk_dept = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_DEPT);
            BillItem item = evt.getItem();
            if (item != null)
            {
                PostRefModel postModel = (PostRefModel) ((UIRefPane) item.getComponent()).getRefModel();
                postModel.setPk_org(pk_org);
                postModel.setPkdept(pk_dept);
                String cond =
                    " and ( " + SQLHelper.getNullSql(PostVO.TABLENAME + ".hrcanceled") + " or " + PostVO.TABLENAME + ".hrcanceled = 'N' ) ";
                String powerSql =
                    HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), HICommonValue.RESOUCECODE_DEPT, IRefConst.DATAPOWEROPERATION_CODE,
                        "org_dept");
                // getPowerSql(HICommonValue.RESOUCECODE_DEPT);
                if (!StringUtils.isBlank(powerSql))
                {
                    cond += " and om_post.pk_dept in ( select pk_dept from org_dept where  " + powerSql + " ) ";
                }
                postModel.addWherePart(cond);
            }
        }
        else if ((PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOB).equals(evt.getItem().getKey()))
        {
            // ְ��
            Object objValue = getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_ORG);
            BillItem item = evt.getItem();
            if (item != null)
            {
                if (objValue != null)
                {
                    ((UIRefPane) item.getComponent()).setPk_org(objValue.toString());
                }
                else
                {
                    ((UIRefPane) item.getComponent()).setPk_org(getModel().getContext().getPk_group());
                }
            }
        }
        else if ((PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBGRADE).equals(evt.getItem().getKey()))
        {
            // ְ��
            String pk_job = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOB);
            String pk_post = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_POST);
            String pk_jobtype = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.SERIES);
            String pk_postseries = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_POSTSERIES);
            
            BillItem item = (BillItem) evt.getSource();
            if (item != null)
            {
                FilterTypeEnum filterType = null;
                String gradeSource = "";
                Map<String, Object> resultMap = null;
                try
                {
                    resultMap =
                        NCLocator.getInstance().lookup(IPsndocQryService.class)
                            .getLevelRankCondition(pk_jobtype, pk_job, pk_postseries, pk_post);
                }
                catch (BusinessException e)
                {
                    Logger.error(e.getMessage(), e);
                }
                
                if (!resultMap.isEmpty())
                {
                    filterType = (FilterTypeEnum) resultMap.get("filterType");
                    gradeSource = (String) resultMap.get("gradeSource");
                }
                
                ((JobGradeRefModel2) ((UIRefPane) item.getComponent()).getRefModel()).setPk_filtertype(gradeSource, filterType);
            }
            if (StringUtils.isBlank(pk_jobtype) && StringUtils.isBlank(pk_postseries) && StringUtils.isBlank(pk_job)
                && StringUtils.isBlank(pk_post)) item.setEnabled(false);
            else
                item.setEnabled(true);
        }
        else if ((PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBRANK).equals(evt.getItem().getKey()))
        {
            // ְ��
            BillItem item = evt.getItem();
            String pk_jobrank = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBRANK);
            if (StringUtils.isBlank(pk_jobrank))
            {
                ((JobRankRefModel) ((UIRefPane) item.getComponent()).getRefModel()).setPk_filtertype(null, null);
                // ((JobRankRefModel) ((UIRefPane) item.getComponent()).getRefModel()).setPk_joblevel("");
                return true;
            }
            
            String pk_jobgrade = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBGRADE);
            String pk_job = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOB);
            String pk_post = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_POST);
            String pk_jobtype = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.SERIES);
            String pk_postseries = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_POSTSERIES);
            if (item != null)
            {
                FilterTypeEnum filterType = null;
                String gradeSource = "";
                Map<String, Object> resultMap = null;
                try
                {
                    resultMap =
                        NCLocator.getInstance().lookup(IPsndocQryService.class)
                            .getLevelRankCondition(pk_jobtype, pk_job, pk_postseries, pk_post);
                }
                catch (BusinessException e)
                {
                    Logger.error(e.getMessage(), e);
                }
                
                if (!resultMap.isEmpty())
                {
                    filterType = (FilterTypeEnum) resultMap.get("filterType");
                    gradeSource = (String) resultMap.get("gradeSource");
                }
                ((JobRankRefModel) ((UIRefPane) item.getComponent()).getRefModel()).setPk_joblevel(pk_jobgrade);
                ((JobRankRefModel) ((UIRefPane) item.getComponent()).getRefModel()).setPk_filtertype(gradeSource, filterType);
            }
        }
        else if ((PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_PSNCL).equals(evt.getItem().getKey()))
        {
            // ��Ա���
            BillItem item = evt.getItem();
            if (item != null)
            {
                String powerSql =
                    HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), HICommonValue.RESOUCECODE_PSNCL, IRefConst.DATAPOWEROPERATION_CODE,
                        "bd_psncl");
                if (!StringUtils.isBlank(powerSql))
                {
                    ((UIRefPane) item.getComponent()).getRefModel().addWherePart(" and " + powerSql);
                }
            }
        }
        else if ((PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_ORG).equals(evt.getItem().getKey()))
        {
            // ��֯
            BillItem item = evt.getItem();
            if (item != null)
            {
                String enableSql = " and pk_adminorg in (select pk_adminorg from org_admin_enable) ";
                
                String powerSql =
                    HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), HICommonValue.RESOUCECODE_ORG, IRefConst.DATAPOWEROPERATION_CODE,
                        "org_orgs");
                if (!StringUtils.isBlank(powerSql))
                {
                    enableSql = enableSql + " and pk_adminorg in ( select pk_org from org_orgs where " + powerSql + ") ";
                }
                ((UIRefPane) item.getComponent()).getRefModel().setUseDataPower(false);
                
                // �𼶹ܿ�(��ְ�Ǽǡ���������Ϣ-��֯)
                try
                {
                    String gkSql =
                        NCLocator.getInstance().lookup(IPsndocService.class)
                            .queryControlSql("@@@@Z710000000006M1Y", getModel().getContext().getPk_org(), true);
                    if (!StringUtils.isEmpty(gkSql))
                    {
                        enableSql += " and org_adminorg.pk_adminorg in ( " + gkSql + " )";
                    }
                }
                catch (BusinessException e1)
                {
                    Logger.error(e1.getMessage(), e1);
                }
                ((UIRefPane) item.getComponent()).getRefModel().addWherePart(enableSql);
            }
        }
        else if ((PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_POSTSERIES).equals(evt.getItem().getKey()))
        {
            String pk_post = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_POST);
            if (StringUtils.isNotBlank(pk_post))
            {
                return false;
            }
        }
        else if ((PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.SERIES).equals(evt.getItem().getKey()))
        {
            String pk_job = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOB);
            if (StringUtils.isNotBlank(pk_job))
            {
                return false;
            }
        }
        else if (("nativeplace").equals(evt.getItem().getKey()))
        {
            // ���ᡢ�������ڵ��ֶ�ѡ�����ʱ���ҵ������ó�ΪĬ��ֵΪ�й��� add by yanglt 20140809
            AbstractRefModel nativeRefModel = ((UIRefPane) getBillCardPanel().getHeadItem("nativeplace").getComponent()).getRefModel();
            ((RegionDefaultRefTreeModel) nativeRefModel).setPk_country("0001Z010000000079UJJ");// ����
        }
        else if ("permanreside".equals(evt.getItem().getKey()))
        {
            
            AbstractRefModel refModel = ((UIRefPane) getBillCardPanel().getHeadItem("permanreside").getComponent()).getRefModel();
            ((RegionDefaultRefTreeModel) refModel).setPk_country("0001Z010000000079UJJ");// �������ڵ�
        }
        return true;
    }
    
    /***************************************************************************
     * <br>
     * Created on 2010-7-27 10:37:53<br>
     * @param evt
     * @author Rocex Wang
     ***************************************************************************/
    private void beforePartTimePk_OrgEdit(BillEditEvent evt)
    {
        Object objValue = getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_GROUP + IBillItem.ID_SUFFIX, evt.getRow());
        if (objValue != null && evt.getSource() != null)
        {
            BillItem item = (BillItem) evt.getSource();
            ((UIRefPane) item.getComponent()).getRefModel().setPk_group(objValue.toString());
            String enableSql = "  ";
            String powerSql =
                HiSQLHelper.getPsnPowerSql(objValue.toString(), HICommonValue.RESOUCECODE_ORG, HICommonValue.OPERATIONCODE_PARTDEFAULT,
                    "org_orgs");
            if (!StringUtils.isBlank(powerSql))
            {
                enableSql = enableSql + " and pk_adminorg in ( select pk_org from org_orgs where " + powerSql + ")";
            }
            ((UIRefPane) item.getComponent()).getRefModel().setUseDataPower(false);
            ((UIRefPane) item.getComponent()).getRefModel().addWherePart(enableSql);
        }
    }
    
    /***************************************************************************
     * <br>
     * Created on 2010-7-27 14:37:32<br>
     * @param evt
     * @author Rocex Wang
     ***************************************************************************/
    private void beforePkDeptEdit(BillEditEvent evt, String defaultTableName)
    {
        Object orgObjValue = getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_ORG + IBillItem.ID_SUFFIX, evt.getRow());
        Object grpObjValue = getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_GROUP + IBillItem.ID_SUFFIX, evt.getRow());
        // if (orgObjValue != null && evt.getSource() != null) {
        HRDeptRefModel deptRefModel = (HRDeptRefModel) ((UIRefPane) ((BillItem) evt.getSource()).getComponent()).getRefModel();
        deptRefModel.setPk_org((String) orgObjValue);
        // and ( " + SQLHelper.getNullSql("hrcanceled") + " or hrcanceled = 'N'
        // ) �ӱ�Ĳ��Ų������Ƿ���,����ʱ����
        String cond = " and depttype <> 1 ";
        String powerSql =
            HiSQLHelper.getPsnPowerSql((String) grpObjValue, HICommonValue.RESOUCECODE_DEPT, IRefConst.DATAPOWEROPERATION_CODE, "org_dept");
        // getPowerSql(HICommonValue.RESOUCECODE_DEPT);
        if (!StringUtils.isBlank(powerSql))
        {
            cond += " and " + powerSql;
        }
        if (PartTimeVO.getDefaultTableName().equals(defaultTableName))
        {
            deptRefModel.setShowDisbleOrg(Boolean.TRUE);
        }
        else
        {
            deptRefModel.setShowDisbleOrg(Boolean.FALSE);
        }
        deptRefModel.addWherePart(cond);
        // }
    }
    
    /***************************************************************************
     * <br>
     * Created on 2010-7-27 11:15:13<br>
     * @param evt
     * @author Rocex Wang
     ***************************************************************************/
    private void beforePkJobEdit(BillEditEvent evt)
    {
        Object objValue = getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_GROUP + IBillItem.ID_SUFFIX, evt.getRow());
        BillItem item = (BillItem) evt.getSource();
        if (item != null)
        {
            ((UIRefPane) item.getComponent()).getRefModel().setPk_group((String) objValue);
            // if (objValue != null) {
            // ((UIRefPane) item.getComponent()).setPk_org(objValue.toString());
            // } else {
            // ((UIRefPane)
            // item.getComponent()).setPk_org(getModel().getContext().getPk_group());
            // }
        }
    }
    
    /***************************************************************************
     * <br>
     * Created on 2010-7-14 9:23:49<br>
     * @param evt
     * @author Rocex Wang
     ***************************************************************************/
    private void beforePkJobGradeEdit(BillEditEvent evt)
    {
        // ְ��
        String pk_job = (String) getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_JOB + IBillItem.ID_SUFFIX, evt.getRow());
        String pk_post = (String) getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_POST + IBillItem.ID_SUFFIX, evt.getRow());
        String pk_jobtype = (String) getBodyItemValue(evt.getTableCode(), PsnJobVO.SERIES + IBillItem.ID_SUFFIX, evt.getRow());
        String pk_postseries = (String) getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_POSTSERIES + IBillItem.ID_SUFFIX, evt.getRow());
        
        BillItem item = (BillItem) evt.getSource();
        /** 20150812 �޸�sonar��blocker���� */
        if (item == null) return;
        FilterTypeEnum filterType = null;
        String gradeSource = "";
        Map<String, Object> resultMap = null;
        try
        {
            resultMap =
                NCLocator.getInstance().lookup(IPsndocQryService.class).getLevelRankCondition(pk_jobtype, pk_job, pk_postseries, pk_post);
        }
        catch (BusinessException e)
        {
            Logger.error(e.getMessage(), e);
        }
        
        if (!resultMap.isEmpty())
        {
            filterType = (FilterTypeEnum) resultMap.get("filterType");
            gradeSource = (String) resultMap.get("gradeSource");
        }
        
        ((JobGradeRefModel2) ((UIRefPane) item.getComponent()).getRefModel()).setPk_filtertype(gradeSource, filterType);
        
        if (StringUtils.isBlank(pk_jobtype) && StringUtils.isBlank(pk_postseries) && StringUtils.isBlank(pk_job)
            && StringUtils.isBlank(pk_post)) item.setEnabled(false);
        else
            item.setEnabled(true);
        
    }
    
    /***************************************************************************
     * �����λ�༭ǰ�¼�<br>
     * Created on 2010-7-8 19:25:59<br>
     * @param evt
     * @author Rocex Wang
     ***************************************************************************/
    private void beforePkPostEdit(BillEditEvent evt)
    {
        String pk_org = (String) getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_ORG + IBillItem.ID_SUFFIX, evt.getRow());
        String pk_dept = (String) getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_DEPT + IBillItem.ID_SUFFIX, evt.getRow());
        String pk_group = (String) getBodyItemValue(evt.getTableCode(), PsnJobVO.PK_GROUP + IBillItem.ID_SUFFIX, evt.getRow());
        BillItem item = (BillItem) evt.getSource();
        if (item != null)
        {
            PostRefModel postModel = (PostRefModel) ((UIRefPane) item.getComponent()).getRefModel();
            postModel.setPk_group(pk_group);
            postModel.setPk_org(pk_org);
            if (!StringUtils.isBlank(pk_dept))
            {
                postModel.setPkdept(pk_dept);
            }
            else
            {
                postModel.setPkdept(null);
                String powerSql =
                    HiSQLHelper.getPsnPowerSql(pk_group, HICommonValue.RESOUCECODE_DEPT, IRefConst.DATAPOWEROPERATION_CODE, "org_dept");
                // getPowerSql(HICommonValue.RESOUCECODE_DEPT);
                if (!StringUtils.isBlank(powerSql))
                {
                    String cond = " and om_post.pk_dept in ( select pk_dept from org_dept where  " + powerSql + " ) ";
                    postModel.addWherePart(cond);
                }
            }
        }
    }
    
    /***************************************************************************
     * �ﵽ����<br>
     * Created on 2010-7-26 16:49:47<br>
     * @param evt
     * @author Rocex Wang
     ***************************************************************************/
    private void beforePkPostRequire_b(BillEditEvent evt)
    {
        BillItem billItemSource = (BillItem) evt.getSource();
        Object objValue = getBillCardPanel().getBodyItem(evt.getTableCode(), ReqVO.PK_POSTREQUIRE_H).getValueObject();
        if (objValue != null)
        {
            ((UIRefPane) billItemSource.getComponent()).getRefModel().addWherePart(" and pk_cindex" + "='" + objValue + "'");
        }
    }
    
    /***************************************************************************
     * <br>
     * Created on 2010-6-8 10:06:50<br>
     * @param evt
     * @author Rocex Wang
     ***************************************************************************/
    private void beforeTrnsEventEdit(BillEditEvent evt)
    {
        BillItem billItemSource = (BillItem) evt.getSource();
        Object objValue = getBillCardPanel().getBodyItem(evt.getTableCode(), PsnJobVO.TRNSEVENT).getValueObject();
        if (objValue != null)
        {
            ((UIRefPane) billItemSource.getComponent()).getRefModel().addWherePart(
                " and " + PsnJobVO.TRNSEVENT + "=" + objValue + " and enablestate = " + IPubEnumConst.ENABLESTATE_ENABLE);
        }
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-4-20 14:04:00<br>
     * @see nc.ui.hr.uif2.view.HrBillFormEditor#canBeHidden()
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public boolean canBeHidden()
    {
        if (ArrayUtils.contains(new UIState[]{UIState.ADD, UIState.EDIT}, getModel().getUiState()))
        {
            return false;
        }
        return super.canBeHidden();
    }
    
    /***************************************************************************
     * ��ձ����ֶ��е�ֵ<br>
     * Created on 2010-7-19 10:38:58<br>
     * @param strTabCode
     * @param iRowIndex
     * @param strBodyItemKeys
     * @author Rocex Wang
     ***************************************************************************/
    private void clearBodyItemValue(String strTabCode, int iRowIndex, String... strBodyItemKeys)
    {
        if (strBodyItemKeys == null || strBodyItemKeys.length == 0)
        {
            return;
        }
        BillModel billModel = strTabCode == null ? getBillCardPanel().getBillModel() : getBillCardPanel().getBillModel(strTabCode);
        if (billModel == null)
        {
            return;
        }
        for (String strItemKey : strBodyItemKeys)
        {
            billModel.setValueAt(null, iRowIndex, strItemKey);
        }
    }
    
    /***************************************************************************
     * ��ձ�ͷ�ֶ��е�ֵ<br>
     * Created on 2010-7-19 10:34:21<br>
     * @param strHeadItemKeys
     * @author Rocex Wang
     ***************************************************************************/
    private void clearHeadItemValue(String... strHeadItemKeys)
    {
        if (strHeadItemKeys == null || strHeadItemKeys.length == 0)
        {
            return;
        }
        for (String strItemKey : strHeadItemKeys)
        {
            BillItem item = getBillCardPanel().getHeadItem(strItemKey);
            if (item != null)
            {
                item.clearViewData();
            }
        }
    }
    
    /***************************************************************************
     * <br>
     * Created on 2010-5-14 11:58:44<br>
     * @author Rocex Wang
     ***************************************************************************/
    private void filteByPsncl()
    {
        
        // �����Ա��֧�ָ�����Ա����л�ģ��
        
        if (HICommonValue.FUNC_CODE_POI.equals(getModel().getNodeCode()) || HICommonValue.FUNC_CODE_KEYPSN.equals(getModel().getNodeCode()))
        {
            disableHeaditems();
            // getBillCardPanel().setBillData(getBillCardPanel().getBillData());
            if (HICommonValue.FUNC_CODE_KEYPSN.equals(getModel().getNodeCode()))
            {
                PsndocViewHelper.changeBusiness(getBillCardPanel().getBodyTabbedPane(), getModel().getBusinessInfoSet());
            }
            return;
        }
        
        disableHeaditems();
        
        if (ObjectUtils.equals(strPk_psncl, getModel().getPk_psncl()))
        {
            return;
        }
        strPk_psncl = getModel().getPk_psncl();
        
        BillTempletBodyVO[] billTempletBodyVOs = getBillCardPanel().getBillData().getBillTempletVO().getBodyVO();
        if (billTempletBodyVOs == null || billTempletBodyVOs.length <= 0)
        {
            return;
        }
        
        HashMap<String, PsnclinfosetVO> getConfigMap =
            new HrPsnclTemplateContainer().getPsnclConfigMap(getModel().getContext().getPk_org(), strPk_psncl);
        
        if (getConfigMap == null || getConfigMap.isEmpty())
        {
            
            // ûȡ��Ϊ��ԭģ������
            for (BillTempletBodyVO billTempletBodyVO : billTempletBodyVOs)
            {
                int pos = billTempletBodyVO.getPos();
                BillItem item = null;
                if (BillItem.HEAD == pos)
                {
                    item = getBillCardPanel().getHeadItem(billTempletBodyVO.getItemkey());
                }
                else if (BillItem.BODY == pos)
                {
                    item = getBillCardPanel().getBodyItem(billTempletBodyVO.getTable_code(), billTempletBodyVO.getItemkey());
                }
                if (item == null)
                {
                    continue;
                }
                if (item.getTableCode().equals(KeyPsnVO.getDefaultTableName()))
                {
                    item.setShow(false);
                    item.setNull(false);
                }
                else if (ArrayUtils.contains(fldBlastList, item.getKey()))
                {
                    item.setShow(false);
                    item.setNull(false);
                }
                else
                {
                    item.setShow(billTempletBodyVO.getShowflag());
                    item.setNull(billTempletBodyVO.getNullflag());
                }
            }
            
            afterFilterPsncl();
            
            return;
        }
        
        for (BillTempletBodyVO billTempletBodyVO : billTempletBodyVOs)
        {
            int pos = billTempletBodyVO.getPos();
            BillItem item = null;
            if (BillItem.HEAD == pos)
            {
                item = getBillCardPanel().getHeadItem(billTempletBodyVO.getItemkey());
            }
            else if (BillItem.BODY == pos)
            {
                item = getBillCardPanel().getBodyItem(billTempletBodyVO.getTable_code(), billTempletBodyVO.getItemkey());
            }
            if (item == null)
            {
                continue;
            }
            
            if (BillItem.BODY == pos
                && (PsnOrgVO.getDefaultTableName().equals(billTempletBodyVO.getTable_code()) || KeyPsnVO.getDefaultTableName().equals(
                    billTempletBodyVO.getTable_code())))
            {
                item.setShow(false);
                item.setNull(false);
                continue;
            }
            
            if (ArrayUtils.contains(fldBlastList, item.getKey()))
            {
                item.setShow(false);
                item.setNull(false);
                continue;
            }
            
            item.setShow(billTempletBodyVO.getShowflag());
            item.setNull(billTempletBodyVO.getNullflag());
            // ���� ����ģ���metadata����Map��ȡ PsnclinfosetVO
            PsnclinfosetVO configVO = getConfigMap.get(billTempletBodyVO.getMetadataproperty());
            if (configVO == null)
            {
                // Map��û�У����ǲ����Զ����������Զ��������ʾ
                continue;
            }
            
            item.setShow(configVO.getUsedflag() != null && configVO.getUsedflag().booleanValue() && billTempletBodyVO.getShowflag());
            if (!item.isShow())
            {
                item.setNull(false);
            }
            
            item.setNull(configVO.getMustflag() != null && configVO.getMustflag().booleanValue() && item.isShow());
            
        }
        afterFilterPsncl();
    }
    
    private void afterFilterPsncl()
    {
        
        hideQutifySet();
        
        getBillCardPanel().setBillData(getBillCardPanel().getBillData());
        
        // ����ҵ���Ӽ�ҳǩ����ɫ
        PsndocViewHelper.changeBusiness(getBillCardPanel().getBodyTabbedPane(), getModel().getBusinessInfoSet());
        // ���е��Ӽ���֧�ֱ������,��֧���Ҽ��˵�
        String strTabCodes[] = getBillCardPanel().getBillData().getBodyTableCodes();
        if (strTabCodes != null)
        {
            for (String strTabCode : strTabCodes)
            {
                getBillCardPanel().getBillTable(strTabCode).removeSortListener();
                getBillCardPanel().getBodyPanel(strTabCode).setBBodyMenuShow(false);
            }
        }
        
        setCellRenderer();
    }
    
    private void hideQutifySet()
    {
        boolean isJQStart = false;
        isJQStart = PubEnv.isModuleStarted(PubEnv.getPk_group(), PubEnv.MODULE_HRJQ);
        if (isJQStart)
        {
            return;
        }
        // û��������ְ�ʸ� ����ʾ��ְ�ʸ�ҳǩ
        BillModel bm = getBillCardPanel().getBillModel(QulifyVO.getDefaultTableName());
        if (bm == null)
        {
            return;
        }
        BillItem[] items = bm.getBodyItems();
        for (int i = 0; items != null && i < items.length; i++)
        {
            items[i].setShow(false);
        }
        
    }
    
    /***************************************************************************
     * <br>
     * Created on 2010-7-8 19:27:44<br>
     * @param strTabCode
     * @param strItemKey
     * @return Object
     * @author Rocex Wang
     ***************************************************************************/
    private Object getBodyItemValue(String strTabCode, String strItemKey, int iRowIndex)
    {
        return getBillCardPanel().getBillModel(strTabCode).getValueAt(iRowIndex, strItemKey);
    }
    
    /***************************************************************************
     * <br>
     * Created on 2010-2-5 11:01:31<br>
     * @author Rocex Wang
     * @return the dataManger
     ***************************************************************************/
    public PsndocDataManager getDataManger()
    {
        return dataManger;
    }
    
    /***************************************************************************
     * <br>
     * Created on 2010-2-5 11:00:30<br>
     * @author Rocex Wang
     * @return the fieldRelationUtil
     ***************************************************************************/
    public FieldRelationUtil getFieldRelationUtil()
    {
        return fieldRelationUtil;
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-5-10 9:16:03<br>
     * @see nc.ui.uif2.editor.BillForm#getModel()
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public PsndocModel getModel()
    {
        return (PsndocModel) super.getModel();
    }
    
    /***************************************************************************
     * <br>
     * Created on 2010-2-5 11:01:31<br>
     * @author Rocex Wang
     * @return the superValidator
     ***************************************************************************/
    public SuperFormEditorValidatorUtil getSuperValidator()
    {
        return superValidator;
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-4-20 14:04:00<br>
     * @see nc.ui.uif2.editor.BillForm#getValue()
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public Object getValue()
    {
        
        // ���ı�����ݣ����������ġ��޸ĵġ�ɾ����
        PsndocAggVO psndocChangedAggVO = (PsndocAggVO) super.getValue();
        
        // ά���ڵ� ����������ʱ��ı����򸳳�ֱ������
        if (getModel().getContext().getNodeCode().equals(HICommonValue.FUNC_CODE_EMPLOYEE))
        {
            SuperVO[] capa = psndocChangedAggVO.getTableVO(CapaVO.getDefaultTableName());
            for (int i = 0; capa != null && i < capa.length; i++)
            {
                if (capa[i].getStatus() == VOStatus.UPDATED)
                {
                    capa[i].setAttributeValue(CapaVO.ASSSOURCETYPE, 3);
                }
            }
        }
        
        // ���������е�ȫ�����ݣ����������ġ��޸ĵģ��Լ�û�иı�ģ����ǲ�����ɾ����
        PsndocAggVO psndocAggVO = (PsndocAggVO) getBillCardPanel().getBillData().getBillObjectByMetaData();
        
        // ��ɾ�����Ӽ��е����ݺϲ�����
        psndocAggVO.mergeDeletedAggVO(psndocChangedAggVO);
        
        try
        {
            validateData(psndocAggVO);
        }
        catch (BusinessException ex)
        {
            throw new BusinessRuntimeException(ex.getMessage(), ex);
        }
        
        // �ҵ������е�����
        PsndocVO psndocVO = psndocAggVO.getParentVO();
        // �ѱ�ͷ����֯��ϵ�͹�����¼��ϢҲ�ռ�����
        getBillCardPanel().getBillData().getHeaderValueVO(psndocVO);
        psndocVO.setPk_hrorg(getModel().getContext().getPk_org());
        psndocVO.getPsnOrgVO().setPk_hrorg(getModel().getContext().getPk_org());
        psndocVO.getPsnJobVO().setPk_hrorg(getModel().getContext().getPk_org());
        resetMacaId(psndocAggVO);
        // ����ְ��¼�е�pk_orgͬ����������Ϣ����֯��ϵ��pk_org��,����ǰ̨��,�ŵ���̨��
        if (psndocVO.getPsnOrgVO().getPk_group() == null)
        {
            psndocVO.getPsnOrgVO().setPk_group(psndocVO.getPk_group());
        }
        if (psndocVO.getPsnJobVO().getPk_group() == null)
        {
            psndocVO.getPsnJobVO().setPk_group(psndocVO.getPk_group());
        }
        // psndoc������ȫ����UIState������Ϊ�п��ܲɼ�������ϵͳ�е���Ա��
        psndocVO.setStatus(psndocVO.getPk_psndoc() == null ? VOStatus.NEW : VOStatus.UPDATED);
        if (HICommonValue.JOB_HIRE.equals(getModel().getInJobType()))
        {
            // ������ְ���޸ġ���Ƹ��Ƹ�޸ĵ�ʱ��3��VO״̬����һ�µ�
            psndocVO.getPsnJobVO().setStatus(psndocVO.getStatus());
            psndocVO.getPsnOrgVO().setStatus(psndocVO.getStatus());
        }
        else if (HICommonValue.JOB_REHIRE.equals(getModel().getInJobType()))
        {
            // ��Ƹ����Ƹ�����������ʱ����Ա��Ϣ״̬���޸ģ���֯��ϵ����ְ��¼��״̬������
            psndocVO.getPsnJobVO().setStatus(VOStatus.NEW);
            psndocVO.getPsnOrgVO().setStatus(VOStatus.NEW);
        }
        
        if (HICommonValue.JOB_REHIRE.equals(getModel().getInJobType()))
        {
            // ��Ƹ��Ƹ�������ݿ����Ѵ��ڵ�����״̬��Ϊupdate
            SuperVO[] childVO = psndocAggVO.getAllChildrenVO();
            for (int i = 0; childVO != null && i < childVO.length; i++)
            {
                if (childVO[i].getPrimaryKey() != null && VOStatus.NEW == childVO[i].getStatus())
                {
                    childVO[i].setStatus(VOStatus.UPDATED);
                }
            }
        }
        // ��aggvo�кϲ���������
        psndocAggVO.setParentVO(psndocVO);
        
        // Ϊ�˷�ֹ�ǿ�У�飬�ȸ�һ����ʱ��ֵ
        this.setBodyValue("hi_psndoc_cert", 0, psndocVO.IDTYPE, psndocVO.getIdtype());
        this.setBodyValue("hi_psndoc_cert", 0, psndocVO.ID, psndocVO.getId());
        
        return psndocAggVO;
    }
    
    /**
     * <br>
     * ����ǰȥ���������֤�����ӵ�������� Created on 2013-12-12 15:37:55<br>
     * @param psndocAggVO
     * @author caiqm
     */
    private void resetMacaId(PsndocAggVO psndocAggVO)
    {
        SuperVO[] subVOs = psndocAggVO.getAllChildrenVO();
        PsndocVO psndocVO = psndocAggVO.getParentVO();
        String pid = psndocVO.getId();
        if (HICommonValue.IDTYPE_MACAU.equals(psndocVO.getIdtype()) && pid.endsWith(")")
            && "(".equals(String.valueOf(pid.charAt(pid.length() - 3))))
        {
            psndocVO.setId(pid.substring(0, pid.length() - 3) + pid.charAt(pid.length() - 2));
        }
        
        for (int m = 0; m < subVOs.length; m++)
        {
            if (subVOs[m] instanceof CertVO && HICommonValue.IDTYPE_MACAU.equals(((CertVO) subVOs[m]).getIdtype()))
            {
                String id = ((CertVO) subVOs[m]).getId();
                if (id.endsWith(")") && "(".equals(String.valueOf(id.charAt(id.length() - 3))))
                {
                    ((CertVO) subVOs[m]).setId(id.substring(0, id.length() - 3) + id.charAt(id.length() - 2));
                }
            }
        }
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-4-20 14:04:00<br>
     * @see nc.ui.hr.uif2.view.HrBillFormEditor#handleEvent(nc.ui.uif2.AppEvent)
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public void handleEvent(AppEvent evt)
    {
        
        if (AppEventConst.SHOW_EDITOR == evt.getType())
        {
            getModel().fireEvent(new AppEvent(HiAppEventConst.TAB_CHANGED, evt.getSource(), null));
        }
        
        if (AppEventConst.SELECTION_CHANGED == evt.getType())
        {
            onSelectionChanged();
            // Ĭ�ϼ������֤���Ӽ����Ա�֤������Ϣ������ȷ��д���Ӽ���
            SuperVO subVOs[] = null;
            BillModel billModel = getBillCardPanel().getBillModel(PsnJobVO.getDefaultTableName());
            try
            {
                subVOs = getDataManger().querySubVO(PsnJobVO.getDefaultTableName(), null);
            }
            catch (BusinessException ex)
            {
                throw new BusinessRuntimeException(ex.getMessage(), ex);
            }
            if (subVOs != null && subVOs.length > 0)
            {
                billModel.clearBodyData();
                billModel.addLine(subVOs.length);
                for (int i = 0; i < subVOs.length; i++)
                {
                    billModel.setBodyRowObjectByMetaData(subVOs[i], i);
                    billModel.setRowState(i, BillModel.NORMAL);
                }
                billModel.execLoadFormula();
            }
            getHashSubHaveLoad().add(PsnJobVO.getDefaultTableName());
        }
        else
        {
            super.handleEvent(evt);
        }
    }
    
    @Override
    protected void onNotEdit()
    {
        super.onNotEdit();
        getBillCardPanel().getBillData().clearShowWarning();
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-4-20 14:04:07<br>
     * @see nc.ui.hr.uif2.view.HrBillFormEditor#initUI()
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public void initUI()
    {
        
        if (getTemplateContainer() instanceof HrPsnclTemplateContainer)
        {
            HrPsnclTemplateContainer templateContainer = (HrPsnclTemplateContainer) getTemplateContainer();
            templateContainer.setPk_org(getModel().getContext().getPk_org());
        }
        super.initUI();
        // BillItem itemBeginDate=getBillCardPanel().getHeadItem(PsnOrgVO.getDefaultTableName() + "_" +
        // PsnOrgVO.BEGINDATE);
        isEditBeginDate = getBillCardPanel().getHeadItem(PsnOrgVO.getDefaultTableName() + "_" + PsnOrgVO.BEGINDATE).isEdit();
        isEditEndDate = getBillCardPanel().getHeadItem(PsnOrgVO.getDefaultTableName() + "_" + PsnOrgVO.ENDDATE).isEdit();
        
        hideQutifySet();
        getBillCardPanel().setBillData(getBillCardPanel().getBillData());
        
        getBillCardPanel().addBodyEditListener2(this);
        getBillCardPanel().setBillBeforeEditListenerHeadTail(this);
        String strTabCodes[] = getBillCardPanel().getBillData().getBodyTableCodes();
        if (strTabCodes != null && strTabCodes.length > 0)
        {
            for (String strTabCode : strTabCodes)
            {
                getBillCardPanel().addEditListener(strTabCode, this);
                getBillCardPanel().addBodyEditListener2(strTabCode, this);
            }
        }
        // �����ҳǩ����
        getBillCardPanel().getBodyTabbedPane().addChangeListener(new ChangeListener()
        {
            @Override
            public void stateChanged(ChangeEvent event)
            {
                subTabChanged(event);
            }
        });
        // �����ֶα仯�߼�������
        if (getFieldRelationUtil() != null)
        {
            getFieldRelationUtil().setFormeditor(this);
            getFieldRelationUtil().putBombToFormEditor();
        }
        // У�������ڴ˴��������ڲ���Ա����Ȼ�������ط�Ҳע����У���������ǵ����ģ������ط�ʹ�ô�У����ʱ��initUIӦ���Ѿ���ɣ�
        if (superValidator != null)
        {
            superValidator.setFormeditor(this);
            superValidator.getComponentMap().put("model", getModel());
            superValidator.getComponentMap().put("utils", new EvalUtils(this.getModel().getContext()));
        }
        // ����ҵ���Ӽ�ҳǩ����ɫ
        PsndocViewHelper.changeBusiness(getBillCardPanel().getBodyTabbedPane(), getModel().getBusinessInfoSet());
        getBillCardPanel().getBodyTabbedPane().setTabLayoutPolicy(ExtTabbedPane.SCROLL_TAB_LAYOUT);
        
        // ȥ���ֱ���Ҽ��˵�
        String[] tabCodes = getBillCardPanel().getBillData().getBodyTableCodes();
        for (int i = 0; tabCodes != null && i < tabCodes.length; i++)
        {
            getBillCardPanel().getBillTable(tabCodes[i]).removeSortListener();
            getBillCardPanel().getBodyPanel(tabCodes[i]).setBBodyMenuShow(false);
        }
        
        disableHeaditems();
        
        setCellRenderer();
        
        if (HICommonValue.FUNC_CODE_REGISTER.equals(getModel().getContext().getNodeCode()))
        {
            // ֻ������ְ�Ǽ�ʱ����Ϊ�춯�¼���������˵�����ְ���͡���ְ��䶯������item
            DefaultConstEnum[] enumItems = initTransevent();
            BillItem item = getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.TRNSEVENT);
            UIComboBox combobox = (UIComboBox) item.getComponent();
            combobox.removeAllItems();
            combobox.addItems(enumItems);
        }
        
        ((UIRefPane) getBillCardPanel().getBodyItem(CertVO.getDefaultTableName(), PsndocVO.ID).getComponent()).getUITextField()
            .addFocusListener(this);
    }
    
    /**
     * Ϊ�춯�¼���������˵�����ְ���͡���ְ��䶯������item
     * @return
     */
    private DefaultConstEnum[] initTransevent()
    {
        List<DefaultConstEnum> items = new ArrayList<DefaultConstEnum>();
        // ����Ԫ�����ж���ġ��춯�¼���ö�٣���ʼ�������б�����
        try
        {
            IComponent ibean = MDBaseQueryFacade.getInstance().getComponentByID("f57904bd-0037-4cea-842d-f33708084ab8");
            List<IEnumType> enums = ibean.getEnums();
            
            // �ڷ��ص����е�ö������в���ָ�����Ƶ�ö��
            IConstEnum[] agreementTypeEnum = null;
            for (IEnumType iet : enums)
            {
                if ("trnsevent".equals(iet.getName()))
                {
                    agreementTypeEnum = iet.getConstEnums();
                    break;
                }
            }
            // ����ö��ֵ�����������б���ֵ�����
            items = new ArrayList<DefaultConstEnum>();
            for (IConstEnum pte : agreementTypeEnum)
            {
                
                if ((Integer) pte.getValue() == 4/* ��ְ */|| (Integer) pte.getValue() == 5/* ��ְ��䶯 */)
                {
                    continue;
                }
                items.add(new DefaultConstEnum(pte.getValue(), pte.getName()));
            }
        }
        catch (Throwable t)
        {
            Logger.error("[�춯�¼�]ö�ټ���ʧ�ܣ�������ָ����Ԫ���ݲ����ڣ���ע���ö�����ƴ���");
        }
        return items.toArray(new DefaultConstEnum[items.size()]);
    }
    
    private void disableHeaditems()
    {
        try
        {
            String[] keys = getModel().getHiddenKeys();
            
            for (int i = 0; keys != null && i < keys.length; i++)
            {
                BillItem item = getBillCardPanel().getHeadItem(keys[i]);
                if (item != null)
                {
                    item.setEnabled(false);
                }
            }
        }
        catch (BusinessException e)
        {
            Logger.error(e.getMessage(), e);
        }
        
    }
    
    /***************************************************************************
     * �����Ӽ���������<br>
     * Created on 2010-3-2 11:03:11<br>
     * @author Rocex Wang
     ***************************************************************************/
    public void loadCurrentRowSubData()
    {
        // if (!this.isShowing())
        // {
        // // ��Ƭ����ʾ��ʱ�򲻼����ֱ�
        // return;
        // }
        
        int tabIndex = getBillCardPanel().getBodyTabbedPane().getSelectedIndex();
        if (tabIndex < 0)
        {
            // �����ǰѡ���ҳǩΪ-1 ����
            return;
        }
        BillModel billModel = getBillCardPanel().getBillModel();
        String strTabCode = billModel.getTabvo().getTabcode();
        // ���ж��Ƿ��Ѽ���,ÿ�ζ���
        if (getModel().getBusinessInfoSet().contains(strTabCode) && UIState.ADD == getModel().getUiState())
        {
            // ��������²�����ҵ���Ӽ�
            return;
        }
        
        if ((UIState.EDIT == getModel().getUiState() || UIState.ADD == getModel().getUiState())
            && (getHashSubHaveLoad().contains(strTabCode)) && billModel.getRowCount() > 0)
        {
            // �༭̬,������ع��Ͳ��ټ���
            return;
        }
        
        SuperVO subVOs[] = null;
        try
        {
            subVOs = getDataManger().querySubVO(strTabCode, null);
        }
        catch (BusinessException ex)
        {
            throw new BusinessRuntimeException(ex.getMessage(), ex);
        }
        if (subVOs != null && subVOs.length > 0)
        {
            billModel.clearBodyData();
            billModel.addLine(subVOs.length);
            for (int i = 0; i < subVOs.length; i++)
            {
                billModel.setBodyRowObjectByMetaData(subVOs[i], i);
                billModel.setRowState(i, BillModel.NORMAL);
            }
            billModel.execLoadFormula();
        }
        getHashSubHaveLoad().add(strTabCode);
        getModel().fireEvent(new AppEvent("changeBtnState"));
    }
    
    @Override
    public void showMeUp()
    {
        super.showMeUp();
        if (UIState.ADD != getModel().getUiState())
        {
            synchronizeDataFromModel();
        }
        loadCurrentRowSubData();
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-5-14 11:58:28<br>
     * @see nc.ui.hr.uif2.view.HrBillFormEditor#onAdd()
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    protected void onAdd()
    {
        getHashSubHaveLoad().clear();
        filteByPsncl();
        super.onAdd();
        
        // ���ý���Ĭ��ֵ
        BillItem[] items = getBillCardPanel().getBillData().getHeadTailItems();
        if (items != null)
        {
            for (int i = 0; i < items.length; i++)
            {
                BillItem item = items[i];
                if ("hi_psnorg_orgrelaid".equals(item.getKey()) || "hi_psnjob_poststat".equals(item.getKey()))
                {
                    continue;
                }
                Object value = item.getDefaultValueObject();
                if (value != null)
                {
                    item.setValue(value);
                }
                
            }
        }
        
        // ������Ա�����Ա�����ɱ�������Զ�����ʱ����Ҫִ��һ����Щ�ֶ��ϵı༭��ʽ heqiaoa 20150522
        if (getModel().getPsndocCodeContext() != null)
        {
            BillItem billItemCode = getBillCardPanel().getHeadItem(PsndocVO.CODE);
            if (billItemCode != null && HICommonValue.JOB_HIRE.equals(getModel().getInJobType()))
            {
                // ��Ա����
                billItemCode.setEdit(getModel().getPsndocCodeContext().isEditable());
                getBillCardPanel().execHeadEditFormulas();
            }
        }
        if (getModel().getPsndocClerkCodeContext() != null)
        {
            BillItem item2 = getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.CLERKCODE);
            if (item2 != null && HICommonValue.JOB_HIRE.equals(getModel().getInJobType()))
            {
                // Ա�����Զ�����
                item2.setEdit(getModel().getPsndocClerkCodeContext().isEditable());
                getBillCardPanel().execHeadEditFormulas();
            }
        }
        disableHeaditems();
        getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBGRADE).setEnabled(false);
        // ��������չ�ֽ���ʱ�Զ����ع�ʽ(��ʾ��ʽ)
        /**
         * @author yangzxa 2015��9��24��09:29:44 ��Ϊ�޸���ְ�Ǽ��а������֤��Ա����������ʾ��Ρ�</br> ���������һ�д���ע�͵����ᵼ�¹�ʽִ������
         */
        // getBillCardPanel().execHeadTailLoadFormulas();
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-5-14 11:58:28<br>
     * @see nc.ui.hr.uif2.view.HrBillFormEditor#onEdit()
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    protected void onEdit()
    {
        getHashSubHaveLoad().clear();
        filteByPsncl();
        super.onEdit();
        // Ա��ά����ʱ�򣬹�����¼�е�ҵ���ֶβ������޸�
        if (HICommonValue.FUNC_CODE_EMPLOYEE.equals(getModel().getNodeCode())
            || HICommonValue.FUNC_CODE_KEYPSN.equals(getModel().getNodeCode()))
        {
            for (String strFieldCode : strBusiFieldInJobs)
            {
                BillItem billItem = getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + strFieldCode);
                if (billItem != null)
                {
                    billItem.setEdit(false);
                }
            }
            BillItem[] item = getBillCardPanel().getHeadShowItems("hi_psnjob");
            for (int i = 0; item != null && i < item.length; i++)
            {
                if ((PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.CLERKCODE).equals(item[i].getKey()))
                {
                    continue;
                }
                item[i].setEdit(false);
            }
            
            // �����ǰ��Ա����ְ��Ա,����֯��ϵ�еĿ�ʼ�������ڲ����޸�
            Boolean isEnd =
                (Boolean) getBillCardPanel().getHeadItem(PsnOrgVO.getDefaultTableName() + "_" + PsnOrgVO.ENDFLAG).getValueObject();
            if (isEnd != null && isEnd.booleanValue())
            {
                getBillCardPanel().getHeadItem(PsnOrgVO.getDefaultTableName() + "_" + PsnOrgVO.BEGINDATE).setEdit(false);
                getBillCardPanel().getHeadItem(PsnOrgVO.getDefaultTableName() + "_" + PsnOrgVO.ENDDATE).setEdit(false);
            }
            else
            {
                if (isEditBeginDate == true)
                    getBillCardPanel().getHeadItem(PsnOrgVO.getDefaultTableName() + "_" + PsnOrgVO.BEGINDATE).setEdit(true);
                if (isEditEndDate == true)
                    getBillCardPanel().getHeadItem(PsnOrgVO.getDefaultTableName() + "_" + PsnOrgVO.ENDDATE).setEdit(true);
            }
        }
        
        if (getModel().getPsndocCodeContext() != null)
        {
            BillItem billItemCode = getBillCardPanel().getHeadItem(PsndocVO.CODE);
            if (billItemCode != null)
            {
                billItemCode.setEdit(getModel().getPsndocCodeContext().isEditable());
            }
        }
        
        if (getModel().getPsndocClerkCodeContext() != null)
        {
            BillItem item2 = getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.CLERKCODE);
            if (item2 != null)
            {
                // Ա�����Զ�����
                item2.setEdit(getModel().getPsndocClerkCodeContext().isEditable());
            }
        }
        
        String[] codes = new String[]{/* PsndocVO.ID, PsndocVO.IDTYPE, */PsndocVO.NAME};
        for (String strFieldCode : codes)
        {
            BillItem billItem = getBillCardPanel().getHeadItem(strFieldCode);
            if (billItem != null)
            {
                billItem.setEdit(billItem.isEdit());
            }
        }
        // BillItem billItemID = getBillCardPanel().getHeadItem(PsndocVO.ID);
        // if (billItemID != null)
        // {
        // billItemID.setEdit(false);
        // }
        //
        // BillItem billItemIDType = getBillCardPanel().getHeadItem(PsndocVO.IDTYPE);
        // if (billItemIDType != null)
        // {
        // billItemIDType.setEdit(false);
        // }
        
        BillItem billItemPsncl = getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_PSNCL);
        if (billItemPsncl != null)
        {
            billItemPsncl.setEdit(false);
        }
        
        // Ա����Ϣά������ѯ�ڵ㲻�ܶ�ҵ���Ӽ�����ά��
        if (ArrayUtils.contains(new String[]{
            HICommonValue.FUNC_CODE_EMPLOYEE,
            HICommonValue.FUNC_CODE_PSN_INFO,
            HICommonValue.FUNC_CODE_KEYPSN}, getModel().getContext().getNodeCode()))
        {
            for (Iterator iterator = getModel().getBusinessInfoSet().iterator(); iterator.hasNext();)
            {
                String strTabCode = (String) iterator.next();
                if ((CtrtVO.getDefaultTableName().equals(strTabCode) && !HiCacheUtils.isModuleStarted(PubEnv.MODULE_HRCM))
                    || (CapaVO.getDefaultTableName().equals(strTabCode) && !HiCacheUtils.isModuleStarted(PubEnv.MODULE_HRCP))
                    || (TrainVO.getDefaultTableName().equals(strTabCode) && !HiCacheUtils.isModuleStarted(PubEnv.MODULE_HRTRM))
                    || (AssVO.getDefaultTableName().equals(strTabCode) && !HiCacheUtils.isModuleStarted(PubEnv.MODULE_HRPE)))
                {
                    continue;
                }
                
                if (HICommonValue.FUNC_CODE_KEYPSN.equals(getModel().getContext().getNodeCode())
                    && KeyPsnVO.getDefaultTableName().equals(strTabCode))
                {
                    continue;
                }
                
                BillModel billModel = getBillCardPanel().getBillModel(strTabCode);
                if (billModel != null)
                {
                    billModel.setEnabled(false);
                }
            }
        }
        // ְ��
        BillItem itemJOBGRADE = getBillCardPanel().getHeadItem(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOBGRADE);
        String pk_job = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_JOB);
        String pk_post = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_POST);
        String pk_jobtype = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.SERIES);
        String pk_postseries = (String) getHeadItemValue(PsnJobVO.getDefaultTableName() + "_" + PsnJobVO.PK_POSTSERIES);
        
        if (StringUtils.isBlank(pk_jobtype) && StringUtils.isBlank(pk_postseries) && StringUtils.isBlank(pk_job)
            && StringUtils.isBlank(pk_post)) itemJOBGRADE.setEnabled(false);
        else
            itemJOBGRADE.setEnabled(true);
        disableHeaditems();
        
        // Ĭ�ϼ������֤���Ӽ����Ա�֤������Ϣ������ȷ��д���Ӽ���
        SuperVO subVOs[] = null;
        BillModel billModel = getBillCardPanel().getBillModel(CertVO.getDefaultTableName());
        try
        {
            subVOs = getDataManger().querySubVO(CertVO.getDefaultTableName(), null);
        }
        catch (BusinessException ex)
        {
            throw new BusinessRuntimeException(ex.getMessage(), ex);
        }
        if (subVOs != null && subVOs.length > 0)
        {
            billModel.clearBodyData();
            billModel.addLine(subVOs.length);
            for (int i = 0; i < subVOs.length; i++)
            {
                billModel.setBodyRowObjectByMetaData(subVOs[i], i);
                billModel.setRowState(i, BillModel.NORMAL);
            }
            billModel.execLoadFormula();
        }
        getHashSubHaveLoad().add(CertVO.getDefaultTableName());
        getModel().fireEvent(new AppEvent("changeBtnState"));
    }
    
    /***************************************************************************
     * <br>
     * Created on 2010-2-5 11:01:31<br>
     * @author Rocex Wang
     * @param dataManger the dataManger to set
     ***************************************************************************/
    public void setDataManger(PsndocDataManager dataManger)
    {
        this.dataManger = dataManger;
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-5-19 15:48:39<br>
     * @see nc.ui.hr.uif2.view.HrBillFormEditor#setDefaultValue()
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    protected void setDefaultValue()
    {
        super.setDefaultValue();
        // ����ͻ������ڵ�����Ĭ�Ϲ���Ϊ�й�����heqiaoa 2014-09-23
        BillItem nativeplace_item = getBillCardPanel().getHeadItem("nativeplace");
        BillItem permanreside_item = getBillCardPanel().getHeadItem("permanreside");
        if (null != nativeplace_item && null != nativeplace_item.getComponent())
        {
            RegionDefaultRefTreeModel regoin = (RegionDefaultRefTreeModel) ((UIRefPane) nativeplace_item.getComponent()).getRefModel();
            regoin.setPk_country("0001Z010000000079UJJ"); // Ĭ�����õ�ǰ���������������й�����
        }
        if (null != permanreside_item && null != permanreside_item.getComponent())
        {
            RegionDefaultRefTreeModel regoin = (RegionDefaultRefTreeModel) ((UIRefPane) permanreside_item.getComponent()).getRefModel();
            regoin.setPk_country("0001Z010000000079UJJ"); // Ĭ�����õ�ǰ���������������й�����
        }
    }
    
    /***************************************************************************
     * <br>
     * Created on 2010-2-5 11:00:30<br>
     * @author Rocex Wang
     * @param fieldRelationUtil the fieldRelationUtil to set
     ***************************************************************************/
    public void setFieldRelationUtil(FieldRelationUtil fieldRelationUtil)
    {
        this.fieldRelationUtil = fieldRelationUtil;
    }
    
    /***************************************************************************
     * <br>
     * Created on 2010-5-10 9:16:28<br>
     * @param model
     * @author Rocex Wang
     ***************************************************************************/
    public void setModel(PsndocModel model)
    {
        super.setModel(model);
    }
    
    /***************************************************************************
     * <br>
     * Created on 2010-2-5 11:01:31<br>
     * @author Rocex Wang
     * @param superValidator the superValidator to set
     ***************************************************************************/
    public void setSuperValidator(SuperFormEditorValidatorUtil superValidator)
    {
        this.superValidator = superValidator;
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-4-20 14:04:00<br>
     * @see nc.ui.uif2.editor.BillForm#setValue(java.lang.Object)
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public void setValue(Object object)
    {
        getBillCardPanel().getBillData().setHeaderValueVO(null);
        String strTabCodes[] = getBillCardPanel().getBillData().getBodyTableCodes();
        if (strTabCodes != null)
        {
            for (String strTabCode : strTabCodes)
            {
                getBillCardPanel().getBillModel(strTabCode).clearBodyData();
            }
        }
        super.setValue(object);
        IBusinessEntity be = getBillCardPanel().getBillData().getBillTempletVO().getHeadVO().getBillMetaDataBusinessEntity();
        BillItem headItems[] = getBillCardPanel().getBillData().getHeadTailItems();
        if (headItems == null)
        {
            return;
        }
        PsndocVO psndocVO = null;
        NCObject ncobject = null;
        if (be.getBeanStyle().getStyle() == BeanStyleEnum.AGGVO_HEAD)
        {
            ncobject = DASFacade.newInstanceWithContainedObject(be, object);
        }
        else if (be.getBeanStyle().getStyle() == BeanStyleEnum.NCVO || be.getBeanStyle().getStyle() == BeanStyleEnum.POJO)
        {
            if (object instanceof AggregatedValueObject)
            {
                object = ((AggregatedValueObject) object).getParentVO();
                ncobject = DASFacade.newInstanceWithContainedObject(be, object);
            }
            else
            {
                ncobject = DASFacade.newInstanceWithContainedObject(be, object);
            }
        }
        if (ncobject == null)
        {
            return;
        }
        psndocVO = (PsndocVO) ncobject.getModelConsistObject();
        // ���ñ�ͷ����
        try
        {
            BatchMatchContext.getShareInstance().setInBatchMatch(true);
            BatchMatchContext.getShareInstance().clear();
            for (BillItem item : headItems)
            {
                if ((item.getKey().startsWith("hi_psnorg_") || item.getKey().startsWith("hi_psnjob_"))
                    && item.getMetaDataProperty() != null)
                {
                    Object value = psndocVO.getAttributeValue(item.getKey());
                    if (item.isIsDef())
                    {
                        value = item.converType(value);
                    }
                    item.setValue(value);
                }
            }
            BatchMatchContext.getShareInstance().executeBatch();
        }
        finally
        {
            BatchMatchContext.getShareInstance().setInBatchMatch(false);
        }
        // nc.ui.uif2.editor.BillForm.setValue(Object)����ִ�й���ʽ
        // ����heqiaoa 2014-08-23
        // if (isAutoExecLoadFormula())
        // {
        // getBillCardPanel().getBillData().getBillModel().execLoadFormula();
        // execLoadFormula();
        // }
        
    }
    
    /***************************************************************************
     * �л��ӱ�ҳǩ�¼�<br>
     * Created on 2010-4-16 15:18:20<br>
     * @param evt
     * @author Rocex Wang
     ***************************************************************************/
    private void subTabChanged(ChangeEvent evt)
    {
        loadCurrentRowSubData();
        getModel().fireEvent(new AppEvent(HiAppEventConst.TAB_CHANGED, evt.getSource(), null));
    }
    
    private void fillData()
    {
        
        if (!this.isShowing())
        {
            return;
        }
        try
        {
            // ѡ�е����ݲ�Ϊ��ʱ�ż�������
            if (getModel().getSelectedData() != null)
            {
                Object agg = NCLocator.getInstance().lookup(IPsndocService.class).fillData4Psndoc(getModel().getSelectedData());
                int i = agg == null ? -1 : getModel().findBusinessData(agg);
                if (i >= 0)
                {
                    getModel().getData().set(i, agg);
                }
            }
        }
        catch (BusinessException e)
        {
            Logger.error(e.getMessage(), e);
        }
        
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-5-14 11:58:28<br>
     * @see nc.ui.uif2.editor.BillForm#synchronizeDataFromModel()
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    protected void synchronizeDataFromModel()
    {
        fillData();
        
        Object selectedData = getModel().getSelectedData();
        
        if (selectedData == null)
        {
            getModel().setPk_psncl(null);
            getModel().setCurrentPkPsndoc(null);
        }
        
        if (selectedData instanceof PsndocAggVO)
        {
            getModel().setPk_psncl(((PsndocAggVO) selectedData).getParentVO().getPsnJobVO().getPk_psncl());
            getModel().setCurrentPkPsndoc(((PsndocAggVO) selectedData).getParentVO().getPk_psndoc());
        }
        
        filteByPsncl();
        super.synchronizeDataFromModel();
        this.getBillCardPanel().getBillData().loadLoadHeadRelation();
        
        // ˢ�µ�����,�����ӱ�����
        PsndocAggVO seldata = (PsndocAggVO) getModel().getSelectedData();
        if (seldata == null)
        {
            return;
        }
        int tabIndex = getBillCardPanel().getBodyTabbedPane().getSelectedIndex();
        if (tabIndex < 0)
        {
            // �����ǰѡ���ҳǩΪ-1 ����
            return;
        }
        else
        {
            BillModel billModel = getBillCardPanel().getBillModel();
            String strTabCode = billModel.getTabvo().getTabcode();
            if (seldata.getTableVO(strTabCode) == null || seldata.getTableVO(strTabCode).length == 0)
            {
                loadCurrentRowSubData();
            }
        }
    }
    
    private SimpleDocServiceTemplate getService()
    {
        return new SimpleDocServiceTemplate("PsndocFormEditor");
    }
    
    /***************************************************************************
     * <br>
     * Created on 2010-7-13 9:07:21<br>
     * @throws BusinessException
     * @author Rocex Wang
     * @param psndocAggVO
     ***************************************************************************/
    private void validateData(PsndocAggVO psndocAggVO) throws BusinessException
    {
        
        // �˴���У���ͬ�Ӽ�
        String[] busiSet =
            new String[]{
                PsnOrgVO.getDefaultTableName(),
                PsnJobVO.getDefaultTableName(),
                TrialVO.getDefaultTableName(),
                PsnChgVO.getDefaultTableName(),/* CtrtVO.getDefaultTableName(), */
                RetireVO.getDefaultTableName()};
        
        String[] checkSet = new String[]{PsnJobVO.getDefaultTableName(), PsnChgVO.getDefaultTableName(), RetireVO.getDefaultTableName()};
        
        String[] tabCodes = psndocAggVO.getTableCodes();
        for (int i = 0; tabCodes != null && i < tabCodes.length; i++)
        {
            if (psndocAggVO.getTableVO(tabCodes[i]) == null || psndocAggVO.getTableVO(tabCodes[i]).length == 0)
            {
                continue;
            }
            if (CtrtVO.getDefaultTableName().equals(tabCodes[i]))
            {
                continue;
            }
            
            BillItem begin = getBillCardPanel().getBodyItem(tabCodes[i], "begindate");
            BillItem end = getBillCardPanel().getBodyItem(tabCodes[i], "enddate");
            if (begin == null || end == null)
            {
                // ��û�п�ʼ�������ڵ��Ӽ�
                continue;
            }
            
            boolean isBusinessSub = ArrayUtils.contains(busiSet, tabCodes[i]);
            boolean isCheckBtwRds = ArrayUtils.contains(checkSet, tabCodes[i]);
            String tableName = getBillCardPanel().getBillData().getBodyTableName(tabCodes[i]);
            String beginName = begin.getName();
            String endName = end.getName();
            CommnonValidator.validateLiteralDate(psndocAggVO.getTableVO(tabCodes[i]), "begindate", beginName, "enddate", endName,
                tableName, isBusinessSub, isCheckBtwRds);
        }
        
    }
    
    public void setHashSubHaveLoad(HashSet<String> hashSubHaveLoad)
    {
        this.hashSubHaveLoad = hashSubHaveLoad;
    }
    
    public HashSet<String> getHashSubHaveLoad()
    {
        return hashSubHaveLoad;
    }
    
    private void setCellRenderer()
    {
        BillModel bm = getBillCardPanel().getBillModel(QulifyVO.getDefaultTableName());
        if (bm == null)
        {
            return;
        }
        int colIndex = bm.getBodyColByKey(QulifyVO.AUTHENYEAR);
        UITable bt = getBillCardPanel().getBillTable(QulifyVO.getDefaultTableName());
        if (bt == null)
        {
            return;
        }
        colIndex = bt.convertColumnIndexToView(colIndex);
        if (colIndex < 0)
        {
            return;
        }
        bt.getColumnModel().getColumn(colIndex).setCellRenderer(new AuthenyearCellRenderer());
        
    }
    
    /**
     * {@inheritDoc}<br>
     * Created on 2013-12-11 16:59:17<br>
     * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
     * @author caiqm
     */
    @Override
    public void focusGained(FocusEvent arg0)
    {
        String id = (String) getBillCardPanel().getBodyItem(CertVO.getDefaultTableName(), PsndocVO.ID).getValueObject();
        if (StringUtils.isNotEmpty(id))
        {
            if (id.endsWith(")"))
            {
                id = id.substring(0, id.length() - 3) + id.charAt(id.length() - 2);
            }
            getBillCardPanel().getBodyItem(CertVO.getDefaultTableName(), PsndocVO.ID).setValue(id);
        }
    }
    
    /**
     * {@inheritDoc}<br>
     * Created on 2013-12-11 16:59:17<br>
     * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
     * @author caiqm
     */
    @Override
    public void focusLost(FocusEvent arg0)
    {
        String[] formulas = getBillCardPanel().getBodyItem(CertVO.ID).getLoadFormula();
        getBillCardPanel().getBillData().getBillModel(CertVO.getDefaultTableName()).execFormulas(selectedRow, formulas);
    }
    
    private IPersistenceRetrieve retrieveService;
    
    private IPersistenceRetrieve getServiece()
    {
        if (retrieveService == null)
        {
            return NCLocator.getInstance().lookup(IPersistenceRetrieve.class);
        }
        return retrieveService;
    }
    
}
