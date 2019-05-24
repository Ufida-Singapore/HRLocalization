package nc.impl.wa.classitem;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nc.bs.bd.cache.CacheProxy;
import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.ml.NCLangResOnserver;
import nc.hr.frame.persistence.IValidatorFactory;
import nc.hr.frame.persistence.PersistenceDAO;
import nc.hr.frame.persistence.SimpleDocServiceTemplate;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.impl.wa.category.WaClassDAO;
import nc.impl.wa.classitempower.ClassItemPowerServiceImpl;
import nc.impl.wa.common.WaCommonImpl;
import nc.impl.wa.item.ItemServiceImpl;
import nc.impl.wa.paydata.PaydataServiceImpl;
import nc.impl.wa.payslip.PayslipDAO;
import nc.itf.bd.defdoc.IDefdocQryService;
import nc.itf.hr.frame.PersistenceDbException;
import nc.itf.hr.wa.IClassItemManageService;
import nc.itf.hr.wa.IClassItemQueryService;
import nc.itf.hr.wa.IItemManageService;
import nc.itf.hr.wa.IPaydataManageService;
import nc.itf.hr.wa.IWaClass;
import nc.itf.hr.wa.IWaSalaryctymgtConstant;
import nc.jdbc.framework.JdbcSession;
import nc.jdbc.framework.PersistenceManager;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.exception.DbException;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.md.model.MetaDataException;
import nc.md.persist.framework.IMDPersistenceService;
import nc.md.persist.framework.MDPersistenceService;
import nc.ui.wa.item.util.ItemUtils;
import nc.vo.bd.defdoc.DefdocVO;
import nc.vo.bd.pub.NODE_TYPE;
import nc.vo.hr.formula.FormulaXmlHelper;
import nc.vo.hr.formula.FunctionKey;
import nc.vo.hr.func.FunctionVO;
import nc.vo.hr.func.HrFormula;
import nc.vo.hr.itemsource.TypeEnumVO;
import nc.vo.hr.pub.FormatVO;
import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.hr.tools.pub.GeneralVOProcessor;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.formulaset.ContentVO;
import nc.vo.pub.formulaset.ItemVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.uif2.LoginContext;
import nc.vo.util.AuditInfoUtil;
import nc.vo.util.BDPKLockUtil;
import nc.vo.util.BDVersionValidationUtil;
import nc.vo.wa.category.WaClassVO;
import nc.vo.wa.category.WaInludeclassVO;
import nc.vo.wa.classitem.RoundTypeEnum;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.classitempower.ItemPowerUtil;
import nc.vo.wa.classitempower.ItemPowerVO;
import nc.vo.wa.formula.HrWaXmlReader;
import nc.vo.wa.func.WaDatasourceManager;
import nc.vo.wa.item.FromEnumVO;
import nc.vo.wa.item.PropertyEnumVO;
import nc.vo.wa.item.WaItemConstant;
import nc.vo.wa.item.WaItemVO;
import nc.vo.wa.pub.WaLoginContext;
import nc.vo.wa.pub.WaLoginVOHelper;
import nc.vo.wabm.util.WaCacheUtils;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * <b> ˵����н����Ŀ�ڱ����ʱ�� ����������Ҫ��ס��ǰҪ���ĵ�н����Ŀ
 * ��Ҫ��ס��ǰ������е�н����Ŀ����Ϊн����Ŀ֮����ڽ�ת��ϵ�����֮���������ϵ�� �� ����ֱ�Ӷ�н�������������</b>
 *
 * @author: wh
 *
 * @date: 2009-12-8 ����01:29:38
 * @since: eHR V6.0
 * @�߲���:
 * @�߲�����:
 * @�޸���:
 * @�޸�����:
 */
public class ClassItemManageServiceImpl implements IClassItemManageService, IClassItemQueryService {

	private final ItemServiceImpl itemServiceImpl = new ItemServiceImpl();
	WaCommonImpl waCommonImpl = new WaCommonImpl();
	ClassItemPowerServiceImpl classItemPowerServiceImpl = new ClassItemPowerServiceImpl();
	PaydataServiceImpl paydataServiceImpl = new PaydataServiceImpl();
	ClassitemDAO classitemDAO = new ClassitemDAO();
	IValidatorFactory validatorFactory = new ClassItemValidatorFactory();
	private final String DOC_NAME = "Classitem";

	public IValidatorFactory getValidatorFactory() {
		if(validatorFactory==null){
			validatorFactory = new ClassItemValidatorFactory();
		}
		return validatorFactory;
	}

	private ClassitemDAO getClassitemDAO(){
		if(classitemDAO==null){
			classitemDAO = new ClassitemDAO();
		}

		return classitemDAO;
	}

	private SimpleDocServiceTemplate serviceTemplate;

	private SimpleDocServiceTemplate getServiceTemplate() {
		if (serviceTemplate == null) {
			serviceTemplate = new SimpleDocServiceTemplate(DOC_NAME);
			serviceTemplate.setDispatchEvent(false);
			serviceTemplate.setLocker(new ClassItemDocLocker());
			serviceTemplate.setValidatorFactory(new ClassItemValidatorFactory());
		}

		return serviceTemplate;
	}

	@Override
	public WaClassItemVO[] queryClassItemByCondition(LoginContext context, String condition) throws BusinessException {

		return getClassitemDAO().queryByCondition(context,condition);
	}


	/**
	 * ����н�����н���ڼ䡢������֯����Ŀ�����Լ�Ȩ������ ��ѯн�ʷ�����Ŀ ������Ŀ������ʾ˳������ʹ��order by�Ϳ����ˣ���
	 *
	 * @param typePk
	 *            : ��Ŀ����
	 * @author xuanlt on 2010-1-21
	 * @see nc.itf.hr.wa.IClassItemQueryService#queryByWaItemType(nc.vo.wa.pub.WaLoginContext,
	 *      java.lang.String)
	 */
	@Override
	@SuppressWarnings( { "unchecked", "serial" })
	public WaClassItemVO[] queryByWaItemType(WaLoginContext context, String typePk) throws BusinessException {

		return getClassitemDAO().queryByWaItemType(context, typePk);
	}


	@Override
	public WaClassItemVO queryWaClassItemVOByPk(String pk_waclassitem)
			throws BusinessException {
		return getClassitemDAO().queryWaClassItemVOByPk(pk_waclassitem);
	}

	@Override
	public WaClassItemVO queryByPk(String pk) throws BusinessException {
		return getServiceTemplate().queryByPk(WaClassItemVO.class, pk);
	}
	
	//20160113 shenliangc NCdp205572656  ��ְ��н�ڵ�ɾ��������Ա���ݺ󣬷�����Ŀ�����»��б�ɾ������ְ��н�����µ�������Ŀ
	//����ɾ��������ɾ��н�ʷ�������
	//Ŀǰ����д����SONAR���⣬�����ڲ��߼��Ƚϸ��ӣ���д�����߼�����̫����ʱ��������ɡ�
	@Override
	public void deleteWaClassItemVOs(WaClassItemVO[] vos) throws BusinessException {
		if(!ArrayUtils.isEmpty(vos)){
			for(WaClassItemVO vo : vos){
				WaClassItemVO newvo = this.queryByPk(vo.getPk_wa_classitem());
				newvo.setStatus(vo.getStatus());
				deleteWaClassItemVO(newvo);
			}
		}
	}
	

	@Override
	public void deleteWaClassItemVO(WaClassItemVO vo) throws BusinessException {
		BDPKLockUtil.lockString(vo.getPk_wa_class());
		if(checkGroupItem(vo)){
			throw new BusinessException(ResHelper.getString("60130payitem","060130payitem0222")
					/*@res "��֯����ɾ�����ŷ������Ŀ��"*/);
		}

		//		if(checkLeaveItem(vo)){
		//			throw new BusinessException(ResHelper.getString("60130payitem","060130payitem0223")
		///*@res "����Ŀ�Ѵ�����ְ��н���ݣ�����ɾ����"*/);
		//		}

		//������ո���Ŀ�ķ�������
		clearClassItemData(vo);


		getServiceTemplate().delete(vo);
		CacheProxy.fireDataDeleted(vo.getTableName(), vo.getPk_wa_classitem());
		if (needRegenFormula(vo)) {
			regenerateSystemFormula(vo.getPk_org(), vo.getPk_wa_class(), vo.getCyear(), vo.getCperiod());
		}
		
		// HR���ػ������л�����Ĺ�ʽȫ������
		generateTotalItemFormula(vo.getPk_org(), vo.getPk_wa_class(), vo.getCyear(), vo.getCperiod());
		
		// HR���ػ���������EPF��EIS��SOCSO������ܵ�Ԥ�õ�н����Ŀ
		
		resetPaydataFlag(vo.getPk_wa_class(),vo.getCyear(),vo.getCperiod());

		synParentClassItem(VOStatus.DELETED, vo);



	}


	@Override
	public boolean checkGroupItem(WaClassItemVO vo) throws BusinessException{
		boolean flag = false;
		String sql = "select pk_wa_class from wa_classitem "
				+ "where pk_wa_class = (select pk_sourcecls from wa_assigncls,wa_inludeclass "
				+ "where wa_assigncls.classid = wa_inludeclass.pk_parentclass and wa_inludeclass.pk_childclass= ? and cyear = ? and cperiod = ?) "
				+ "and itemkey = ? and cyear = ? and cperiod = ?";
		SQLParameter param = new SQLParameter();
		param.addParam(vo.getPk_wa_class());
		param.addParam(vo.getCyear());
		param.addParam(vo.getCperiod());
		param.addParam(vo.getItemkey());
		param.addParam(vo.getCyear());
		param.addParam(vo.getCperiod());
		flag = getClassitemDAO().isValueExist(sql,param);  //��η�������

		if(!flag){
			sql = "select pk_wa_class from wa_classitem "
					+ "where pk_wa_class = (select pk_sourcecls from wa_assigncls "
					+ "where wa_assigncls.classid = ? ) "
					+ "and itemkey = ? and cyear = ? and cperiod = ?";
			param = new SQLParameter();
			param.addParam(vo.getPk_wa_class());
			param.addParam(vo.getItemkey());
			param.addParam(vo.getCyear());
			param.addParam(vo.getCperiod());
			return getClassitemDAO().isValueExist(sql,param);
		}

		return flag;


	}

	public boolean checkLeaveItem(WaClassItemVO vo) throws BusinessException{
		String sql = "SELECT pk_wa_classitem "
				+ "FROM wa_classitem "
				+ "WHERE pk_wa_class IN(SELECT pk_childclass "
				+ "						  FROM wa_inludeclass "
				+ "						 WHERE pk_parentclass = (SELECT pk_parentclass "
				+ "												   FROM wa_inludeclass "
				+ "												  WHERE pk_childclass = ? "
				+ "                                                 and cyear = wa_classitem.cyear "
				+ "                                                 and cperiod = wa_classitem.cperiod)  "
				+ "                       and cyear = wa_classitem.cyear "
				+ "                       and cperiod = wa_classitem.cperiod  and batch >100 )" // ������ְ��н����
				+ "	AND cyear = ? "
				+ "	AND cperiod = ? "
				+ "	AND itemkey = ? "
				+ "	AND pk_wa_class <> ?  ";
		SQLParameter param = new SQLParameter();
		param.addParam(vo.getPk_wa_class());
		param.addParam(vo.getCyear());
		param.addParam(vo.getCperiod());
		param.addParam(vo.getItemkey());
		param.addParam(vo.getPk_wa_class());
		return getClassitemDAO().isValueExist(sql,param);
	}

	private void clearClassItemData(WaClassItemVO vo) throws BusinessException{
		NCLocator.getInstance().lookup(IPaydataManageService.class).clearClassItemData(vo);
	}

	/**
	 * ��������н�ʷ������ݵı�־ ��������н���ڼ�״̬
	 * @param pk_wa_class
	 * @param cyear
	 * @param cperiod
	 * @throws BusinessException
	 */
	private void resetPaydataFlag(String   pk_wa_class, String cyear,String cperiod) throws BusinessException{
		NCLocator.getInstance().lookup(IPaydataManageService.class).updatePaydataFlag(pk_wa_class, cyear, cperiod);
	}

	@Override
	public WaClassItemVO insertWaClassItemVO(WaClassItemVO vo)
			throws BusinessException {

		BDPKLockUtil.lockString(vo.getPk_wa_class());
		vo = insertS(vo);
		// ͬ������
		WaCacheUtils.synCache(vo.getTableName());

		return vo;
	}


	private  WaClassItemVO insertS(WaClassItemVO vo) throws BusinessException {

		//�����ж��Ƿ�����˽����Ŀ
		if(isaddPrivateItem(vo)){
			WaItemVO itemvo = vo.abstracts();
			int oldIfromflag = itemvo.getIfromflag();
			String oldVformula = itemvo.getVformula();
			String oldVformulastr = itemvo.getVformulastr();
			//�޸ģ�Ĭ����itemkey����code
			//			IdGenerator idGenerator = DBAUtil.getIdGenerator();
			//			String code = idGenerator.generate();
			//			itemvo.setCode(code);

			//���ifromflag����������Դ�������� FORMULA��WA_WAGEFORM��USER_INPUT��FIX_VALUE��WA_GRADE��TIMESCOLLECT����

			//��Ifromflag ����ΪUSER_INPUT�� ��ʽ����Ϊ��
			if(WaDatasourceManager.isOtherDatasource(itemvo.getIfromflag())){
				itemvo.setIfromflag(FromEnumVO.USER_INPUT.value());
				itemvo.setVformula(null);
				itemvo.setVformulastr(null);
			}
			itemvo =  NCLocator.getInstance().lookup(IItemManageService.class).insertWaItemVO(itemvo);
			oldVformula = StringUtils.replace(oldVformula, "itemkey", itemvo.getItemkey());
			vo.merge(itemvo);
			vo.setIfromflag(oldIfromflag);
			vo.setVformula(oldVformula);
			vo.setVformulastr(oldVformulastr);
		}
		vo =  insert(vo, true);


		resetPaydataFlag(vo.getPk_wa_class(),vo.getCyear(),vo.getCperiod());

		//��ѯ������.����������Ƕ�η�н .��ͬ��������
		//		synParentClassItem(VOStatus.NEW,vo);



		return vo;
	}




	private void synParentClassItem(int action, WaClassItemVO vo) throws BusinessException {
		//ȷ���Ƿ���ڸ����
		IWaClass  waClass  = NCLocator.getInstance().lookup(IWaClass.class);
		WaClassVO parentvo = waClass.queryParentClass(vo.getPk_wa_class(),vo.getCyear()	, vo.getCperiod());
		if(parentvo == null){
			if ( action == VOStatus.NEW  ) {
				insertItemPower(vo);
			}
			if ( action == VOStatus.UPDATED    ) {
				//				 deleteItemPower(vo);
				//				 insertItemPower(vo);     //�������˵�ȫɾ�ˣ�����Լ���Ȩ�ޣ���֪����ʲô�õģ������Ρ�
				
//				20151028  xiejie3  NCdp205519152 ����Ҫ���޸�Ϊ�����ֹ�������ʱ��Ĭ��Ϊ���޸ġ�begin
				//20151201 shenliangc NCdp205519152 �������⣬������ˡ�
//				updateItemPowerEditflag(vo);
//				end
				//
				
			}
			if (  action == VOStatus.DELETED    ) {

				if(!getClassitemDAO().isItemExistDifPeriod(vo)){
					//ɾ����ĿȨ��
					deleteItemPower(vo);
				}

				//ֻ��ո�������н��������
				new PayslipDAO().deletePayslipItemData(vo);

			}
			//û�в�ѯ��������,ֱ�ӷ���
			return ;
		}

		WaClassItemVO newvo =(WaClassItemVO) vo.clone();
		newvo.setPk_wa_class(parentvo.getPk_wa_class());

		//����������Ҫ����, ���еļ���˳�򶼺���
		newvo.setIcomputeseq(0);

		//		//����н����Ŀ��������Դ�ǣ����Ŵ�������
		//������Դ���ֲ��� ��
		//		newvo.setIfromflag(8);

		if ( action == VOStatus.NEW  ) {
			//������Ƿ��и���Ŀ
			if(getClassitemDAO().isItemExist(parentvo, newvo.getItemkey())){
				//����������и���Ŀ.ֱ�ӷ���
				return ;
			}

			newvo.setPk_wa_classitem(null);

			//������,��ͬ�����������
			newvo.setStatus(VOStatus.NEW);

			getMDPersistenceService().saveBill(newvo);
			//������ĿĬ����Ȩ��
			insertItemPower(newvo);

		}

		if ( action == VOStatus.UPDATED    ) {
			//������Ƿ��и���Ŀ
			if(getClassitemDAO().isItemExist(parentvo, newvo.getItemkey())){
				//��,��Ҫ����

				newvo.setStatus(VOStatus.UPDATED);

				//���¸�������Ŀ��Ҫ��һ���Ż�. ���� ʹ����2�� sql����
				WaClassItemVO   newvoold =   getClassitemDAO().queryClassItemVO(newvo.getPk_wa_item(),newvo.getCyear(),newvo.getCperiod(),newvo.getPk_wa_class());
				newvo.setPk_wa_classitem(newvoold.getPk_wa_classitem());

				//				  deleteItemPower(newvo);
				//				  insertItemPower(newvo);    //�������˵�ȫɾ�ˣ�����Լ���Ȩ�ޣ���֪����ʲô�õģ������Ρ�
//				20151028  xiejie3  NCdp205519152 ����Ҫ���޸�Ϊ�����ֹ�������ʱ��Ĭ��Ϊ���޸ġ�begin
				//20151201 shenliangc NCdp205519152 �������⣬������ˡ�
//				updateItemPowerEditflag(newvo);
//				end
				getMDPersistenceService().saveBill(newvo);

			}
		}

		if (  action == VOStatus.DELETED    ) {
			//�Ƿ����ɾ��.���������ӷ��������Ƿ��� .���û��,�Ϳ���ɾ��
			if(getClassitemDAO().isExistInOtherChildClass(parentvo.getPk_wa_class(),vo.getPk_wa_class(),newvo)){
				//������Ƿ��и���Ŀ
				if(getClassitemDAO().isItemExist(parentvo, newvo.getItemkey())){  //daicy 9-18
					//��,��Ҫ����
					//ɾ����Ŀ�ǻ�����Ŀ����Ϊ��һ�ε���Ŀ
					WaInludeclassVO[] subclasses = new WaClassDAO().querySubClasses(parentvo.getPk_wa_class(), newvo.getCyear(),newvo.getCperiod(),false);
					if(null != subclasses ){
						int i = 0;
						for(;  i < subclasses.length ;i++){
							if(subclasses[i].getPk_childclass().equals(vo.getPk_wa_class())){
								break;
							}
						}
						if( i < subclasses.length){
							int batch = subclasses[i].getBatch();
							if(batch > 1 ){
								i = i - 1;
								if(i >=0 && i < subclasses.length){
									WaClassItemVO   chilenewvoold =   getClassitemDAO().queryClassItemVO(
											newvo.getPk_wa_item(),newvo.getCyear(),newvo.getCperiod(),subclasses[i].getPk_childclass());
									WaClassItemVO   parentnewvoold =   getClassitemDAO().queryClassItemVO(
											newvo.getPk_wa_item(),newvo.getCyear(),newvo.getCperiod(),subclasses[i].getPk_parentclass());
									// 2015-12-28 NCdp205565481 zhousze н�ʷ�����Ŀ����ְ��н�е���Ŀɾ����δ֪���������пմ��� begin
									if(chilenewvoold != null && parentnewvoold != null){
							    		chilenewvoold.setStatus(VOStatus.UPDATED);
								    	chilenewvoold.setPk_wa_classitem(parentnewvoold.getPk_wa_classitem());
							    		chilenewvoold.setPk_wa_class(parentnewvoold.getPk_wa_class());
							     		//getServiceTemplate().update(chilenewvoold,true);
							       		getMDPersistenceService().saveBill(chilenewvoold);
									}
									// end
								}

							}
						}

					}

				}

				return;
			}




			//���н�ʷ��ű��е�����
			clearClassItemData(newvo);

			if(!getClassitemDAO().isItemExistDifPeriod(newvo)){
				//ɾ����ĿȨ��
				deleteItemPower(newvo);
			}


			//ֻ��ո�������н��������
			new PayslipDAO().deletePayslipItemData(vo);


			getClassitemDAO().deleteWaclassItem(newvo);
		}

		//����������ʾ
		resetPaydataFlag(newvo.getPk_wa_class(),newvo.getCyear(),newvo.getCperiod());
	}



	/**
	 * ���ŷ���������Ŀ
	 * (1)�鿴���е��ӷ�������ǰ�ڼ䣩�Ƿ����Ѿ���˷������ݵġ�������������ʾ
	 * (2)û�У�����ִ����������
	 *    �ӷ�����û���ظ���Ŀ��ֱ������
	 *    �ӷ������еģ���Ҫ���и���
	 *
	 *    �����Ŀ�а���Ϊ�������Ŀ��ô�죿
	 *
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	@Override
	public WaClassItemVO insertGroupClassItem(WaClassItemVO vo) throws BusinessException {

		/**
		 * �鿴���е��ӷ�������ǰ�ڼ䣩�Ƿ����Ѿ���˷������ݵġ�������������ʾ
		 */
		WaClassVO classvo =new WaClassVO();
		classvo.setPk_wa_class(vo.getPk_wa_class());
		classvo.setCyear(vo.getCyear());
		classvo.setCperiod(vo.getCperiod());

		WaClassVO[] vos = getClassitemDAO().subClassHasCheckedData(classvo);
		if(!ArrayUtils.isEmpty(vos)){
			//�׳��쳣��������ʾ
			//String  names = FormatVO.formatArrayToString(vos, SQLHelper.getMultiLangNameColumn(WaClassVO.NAME), "");
			throw new BusinessException(ResHelper.getString("60130classpower","060130classpower0173")/*@res "�ӷ�����"*/+ ResHelper.getString("60130classpower","060130classpower0174")/*@res "�����Ѿ���˵����ݣ�����������Ŀ "*/);
		}

		/**
		 * ���Ȳ����Լ�����Ŀ
		 */
		vo = insertS(vo);

		/**
		 * ��������֯����Ŀ
		 */

		WaClassVO[] allvos =  getClassitemDAO().queryGroupAssignedWaclass(classvo);
		if(allvos!=null){
			for (WaClassVO waClassVO : allvos) {
				//guoqt NCZX����NCdp205050978���ŷ������䵽�����֯�����֯�ڼ䲻һ��ʱ������ʱ��ʾ������Ŀδ��ӵ�����
				if(waClassVO.getCyear().equals(vo.getCyear())&&waClassVO.getCperiod().equals(vo.getCperiod())){
					insertClassItem2SubClass(vo,waClassVO);
				}
			}
		}

		//ͬ������
		WaCacheUtils.synCache(vo.getTableName());
		return vo;
	}


	private void insertClassItem2SubClass(WaClassItemVO vo,WaClassVO subClassvo) throws BusinessException{

		try {
			WaClassItemVO newvo =(WaClassItemVO) vo.clone();
			newvo.setPk_group(subClassvo.getPk_group());
			newvo.setPk_org(subClassvo.getPk_org());
			newvo.setPk_wa_class(subClassvo.getPk_wa_class());
			newvo.setPk_wa_classitem(null);
			newvo.getPk_wa_item();


			//����Ѿ��и���Ŀ������£�����Ͳ���
			WaClassItemVO  oldItemvo =  getClassitemDAO().queryClassItemVO(newvo.getPk_wa_item(), newvo.getCyear(),newvo.getCperiod(),newvo.getPk_wa_class());
			if(oldItemvo==null){
				newvo.setStatus(VOStatus.NEW);

				insertS(newvo);
			}else{
				newvo.setStatus(VOStatus.UPDATED);
				newvo.setPk_wa_classitem(oldItemvo.getPk_wa_classitem());
				//�汾У��
				newvo.setTs(oldItemvo.getTs());
				updateWaClassItemVO(newvo);

			}
		} catch (DAOException e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(MessageFormat.format(ResHelper.getString("60130classpower","060130classpower0175"), subClassvo.getMultilangName()));/*@res "���ӷ���{0}������������Ŀʧ��*/
		}
	}

	//	20150728 xiejie3 �����ϲ���NCdp205382570��н�ʷ����У���ʾ��������н����Ŀ��Щ������ʾ���ݣ�begin
	//guoqtͬ��н�ʷ�����Ŀ��ʾ˳��
		private void updateClassItem2SubClass(WaClassItemVO[] ordervo,WaClassVO subClassvo) throws BusinessException{
			try {
				IClassItemQueryService queryItem = NCLocator.getInstance().lookup(IClassItemQueryService.class);
				WaClassItemVO[] allItems = queryItem.queryItemInfoVO(subClassvo.getPk_org(), subClassvo.getPk_wa_class(), subClassvo.getCyear(), subClassvo.getCperiod());
				List<String> vositemkey = new ArrayList(ordervo.length);
				for(int i=0;i<ordervo.length;i++){
					vositemkey.addAll(Arrays.asList(ordervo[i].getItemkey()));
				}
				List<WaClassItemVO> vos = new ArrayList(allItems.length);
				for(int j=0;j<allItems.length;j++){
					//���жϸ�н����Ŀ�Ƿ��Ǽ�����Ŀ
					if(vositemkey.contains(allItems[j].getItemkey())){
						for(int k=0;k<vositemkey.size();k++){
							if(allItems[j].getItemkey().equals(ordervo[k].getItemkey())){
								allItems[j].setStatus(VOStatus.UPDATED);
								//����˳��
								allItems[j].setIdisplayseq(ordervo[k].getIdisplayseq());
								vos.addAll(Arrays.asList(allItems[j]));
							}
						}
					}else{
						//��֯�Լ����ӵ���Ŀ
						allItems[j].setStatus(VOStatus.UPDATED);
						//����˳��
						allItems[j].setIdisplayseq(allItems[j].getIdisplayseq()+allItems.length);
						vos.addAll(Arrays.asList(allItems[j]));
					}
				}
				WaClassItemVO[] updatevos=vos.toArray(new WaClassItemVO[0]);
				this.getClassitemDAO().getBaseDao().updateVOArray(updatevos);
			} catch (DAOException e) {
				Logger.error(e.getMessage(), e);
				throw new BusinessException(MessageFormat.format(ResHelper.getString("60130classpower","060130classpower0175"), subClassvo.getMultilangName()));/*@res "���ӷ���{0}������������Ŀʧ��*/
			}
		}
//		end
	

	private void deleteClassItem2SubClass(WaClassItemVO vo,WaClassVO subClassvo) throws BusinessException{

		try {
			WaClassItemVO newvo =(WaClassItemVO) vo.clone();
			newvo.setPk_group(subClassvo.getPk_group());
			newvo.setPk_org(subClassvo.getPk_org());
			newvo.setPk_wa_class(subClassvo.getPk_wa_class());
			newvo.setPk_wa_classitem(null);
			newvo.getPk_wa_item();


			//����Ѿ��и���Ŀ������£�����Ͳ���
			WaClassItemVO oldItemvo = getClassitemDAO().queryClassItemVO(
					newvo.getPk_wa_item(), newvo.getCyear(),
					newvo.getCperiod(), newvo.getPk_wa_class());
			if (oldItemvo != null) {
				deleteWaClassItemVO(oldItemvo);
			}
		} catch (DAOException e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(MessageFormat.format(ResHelper.getString("60130classpower","060130classpower0175"), subClassvo.getMultilangName()));/*@res "���ӷ���{0}������������Ŀʧ��*/
		}
	}

	private static IMDPersistenceService getMDPersistenceService() {
		return MDPersistenceService.lookupPersistenceService();
	}


	private boolean isaddPrivateItem(WaClassItemVO vo){
		return StringUtils.isBlank(vo.getPk_wa_item());

	}
	private WaClassItemVO insert(WaClassItemVO vo, boolean regenFormula) throws BusinessException {


		String categoryId = vo.getCategory_id();
		// set idisplayorder
		vo.setIdisplayseq(getMaxDisplayOrder(vo));
		//�趨Ĭ�ϼ���˳��
		vo.setIcomputeseq(0);
		Integer iitemtype = vo.getIitemtype();
		Integer iproperty = vo.getIproperty();
		// 2015-09-28 zhousze "н�ʷ�����Ŀ"�����������Ҫˢ��һ�Σ���֤������������������µ� begin
//		vo = getServiceTemplate().insert(vo);
		WaClassItemVO oldvo = getServiceTemplate().insert(vo);
		vo = NCLocator.getInstance().lookup(IClassItemQueryService.class).queryWaClassItemVOByPk(oldvo.getPrimaryKey());
		// end
		vo.setIitemtype(iitemtype);
		vo.setIproperty(iproperty);

		if (regenFormula && // �Ƿ���Ҫ��������ϵͳ��ʽ
				needRegenFormula(vo)) {
			regenerateSystemFormula(vo.getPk_org(), vo.getPk_wa_class(), vo.getCyear(), vo.getCperiod());
		}
		
		// HR���ػ������л�����Ĺ�ʽȫ������
		generateTotalItemFormula(vo.getPk_org(), vo.getPk_wa_class(), vo.getCyear(), vo.getCperiod());

		//
		if (!StringUtils.isBlank(categoryId)) {
			DefdocVO[] doc = NCLocator.getInstance().lookup(IDefdocQryService.class).queryDefdocByPk(
					new String[] { categoryId });
			if (doc != null) {
				vo.setCategoryVO(doc[0]);
			} else {
				throw new IllegalStateException(ResHelper.getString("60130classpower","060130classpower0177")/*@res "û�в�ѯ����Ӧ����Ŀ���ࣺ"*/ + categoryId);
			}
		}

		//��Ƽ���˳��
		if(regenFormula){
			resetCompuSeq(vo);
		}


		//��ѯ������.����������Ƕ�η�н .��ͬ��������
		synParentClassItem(VOStatus.NEW,vo);
		return vo;
	}

	private void insertItemPower(WaClassItemVO vo) throws BusinessException{
		ItemPowerVO itemPowerVO = new ItemPowerVO();
		itemPowerVO.setPk_wa_class(vo.getPk_wa_class());
		itemPowerVO.setPk_wa_item(vo.getPk_wa_item());
		itemPowerVO.setModuleflag(Integer.valueOf(0));
		itemPowerVO.setPk_group(vo.getPk_group());
		itemPowerVO.setPk_org(vo.getPk_org());
		itemPowerVO.setPk_subject(PubEnv.getPk_user());
		itemPowerVO.setSubject_type(IWaSalaryctymgtConstant.SUB_TYPE_USER);
		itemPowerVO.setEditflag(vo.getIfromflag().intValue() == 2/* �ֹ�������Ŀ */? UFBoolean.TRUE : UFBoolean.FALSE);
		classItemPowerServiceImpl.insertItemPowerVO(itemPowerVO);
	}

	private void deleteItemPower(WaClassItemVO vo) throws BusinessException{
		ItemPowerVO itemPowerVO = new ItemPowerVO();
		itemPowerVO.setPk_wa_class(vo.getPk_wa_class());
		itemPowerVO.setPk_wa_item(vo.getPk_wa_item());
		itemPowerVO.setModuleflag(Integer.valueOf(0));
		itemPowerVO.setPk_org(vo.getPk_org());

		classItemPowerServiceImpl.deleteItemPowerVO(itemPowerVO);
	}
	
	//20151031 shenliangc н�ʷ�����Ŀ�޸�������Դ�����ͬʱ���·�����ĿȨ�����ݡ�
	//�ֹ����롪�����ֹ����룬���ֹ����롪�������ֹ����룬��ĿȨ�����ݱ��ֲ��䣻
	//���ֹ����롪�����ֹ����룬�ֹ����롪�������ֹ����룬����޸ĺ�Ϊ�ֹ����룬��ɱ༭Ȩ����ΪY���������ΪN��
	//20151201 shenliangc NCdp205519152 �������⣬������ˡ�
	private void updateItemPowerEditflag(WaClassItemVO vo) throws BusinessException{
		WaClassItemVO oldVO = this.queryWaClassItemVOByPk(vo.getPk_wa_classitem());
		if((oldVO.getIfromflag().intValue() != vo.getIfromflag().intValue()) && (oldVO.getIfromflag().intValue() == FromEnumVO.USER_INPUT.toIntValue() 
				|| vo.getIfromflag().intValue() == FromEnumVO.USER_INPUT.toIntValue())){
			ItemPowerVO itemPowerVO = new ItemPowerVO();
			itemPowerVO.setPk_wa_class(vo.getPk_wa_class());
			itemPowerVO.setPk_wa_item(vo.getPk_wa_item());
			itemPowerVO.setModuleflag(Integer.valueOf(0));
			itemPowerVO.setPk_org(vo.getPk_org());
			itemPowerVO.setPk_subject(PubEnv.getPk_user());
			itemPowerVO.setEditflag(vo.getIfromflag().intValue() == FromEnumVO.USER_INPUT.toIntValue()/* �ֹ�������Ŀ */? UFBoolean.TRUE : UFBoolean.FALSE);
			classItemPowerServiceImpl.updateItemPowerVOEditflag(itemPowerVO);
			//���ֹ����롪�����ֹ����룬��Ҫ��wa_data�е���ϸ���ݸ���Ϊ��ʼֵ��
			if(vo.getIfromflag().intValue() == FromEnumVO.USER_INPUT.toIntValue()){
				this.paydataServiceImpl.clearPaydataByClassitem(vo);
			}
		}
	}
	
	
	/**
	 * �Ƿ���Ҫ�������ü���˳��
	 *
	 * @author xuanlt on 2010-5-28
	 * @param vo
	 * @return
	 * @return  boolean
	 */
	private boolean  needResetCompuSeq(WaClassItemVO vo){
		//		if(vo.getFromEnumVO().equals(FromEnumVO.FORMULA)
		//				|| vo.getFromEnumVO().equals(FromEnumVO.OTHER_SYSTEM)
		//				|| vo.getFromEnumVO().equals(FromEnumVO.WAORTHER)
		//				|| vo.getFromEnumVO().equals(FromEnumVO.HI)
		//				|| vo.getFromEnumVO().equals(FromEnumVO.WA_WAGEFORM) ){
		//			return true;
		//		}
		//		return false;

		return true;
	}
	/**
	 * �������ü���˳��
	 * @author xuanlt on 2010-5-28
	 * @param vo
	 * @throws BusinessException
	 * @return  void
	 */
	private void resetCompuSeq(WaClassItemVO vo) throws BusinessException{
		//�������ü���˳��
		WaClassVO classVO = new WaClassVO();
		classVO.setPk_wa_class(vo.getPk_wa_class());
		classVO.setCyear(vo.getCyear());
		classVO.setCperiod(vo.getCperiod());

		//�õ���Ҫ���������н�ʷ�����Ŀ
		resetCompuSeq(classVO);

	}

	/**
	 * ��������ĳ��н�����ļ���˳��
	 * Pk_wa_class
	 * Cyear
	 * Cperiod
	 * @param classVO
	 * @throws BusinessException
	 */
	@Override
	public void resetCompuSeq(WaClassVO classVO) throws BusinessException{

		//�õ���Ҫ���������н�ʷ�����Ŀ
		updateItemCaculateSeu(classVO);
		//		WaClassItemVO[] vos = getClassitemVOsForSequ(classVO);
		//		//���ü���˳��
		//		 vos = new ItemSort().getSortedWaClassItemVOs(vos,classVO);
		//
		//		//�������˳��
		//		classitemDAO.updateItemCaculateSeu(vos);
	}



	@Override
	public  WaClassItemVO[] getClassitemVOsForSequ(WaClassVO vo) throws BusinessException{
		String where = " pk_wa_class = '" + vo.getPk_wa_class()  + "' and cperiod = '" + vo.getCperiod() + "' and cyear = '"+vo.getCyear()+"'";


		return getServiceTemplate().queryByCondition(WaClassItemVO.class, where);
	}

	public  WaClassItemVO[] getClassitemsBySeq(WaClassVO vo) throws BusinessException{
		String where = " pk_wa_class = '" + vo.getPk_wa_class()  + "' and cperiod = '" + vo.getCperiod() + "' and cyear = '"+vo.getCyear()+"'  order by icomputeseq";


		return getServiceTemplate().queryByCondition(WaClassItemVO.class, where);
	}


	public boolean isItemExist(WaClassVO waclassVO, String itemKey) throws BusinessException {
		try {

			return classitemDAO.isItemExist(waclassVO, itemKey);
		} catch (Exception e) {

			throw new BusinessException(e.getMessage());
		}
	}

	private boolean needRegenFormula(WaClassItemVO vo) {
		return vo.getPropertyEnumVO() != PropertyEnumVO.OTHER;
	}

	@Override
	public void regenerateSystemFormula(String pk_org, String pk_wa_class, String cyear, String cperiod)
			throws BusinessException {

		try {

			WaClassItemVO[] items = null;
			try {
				items = classitemDAO.queryItemInfoVO(pk_org, pk_wa_class, cyear, cperiod,
						" wa_item.itemkey in ("+getRegenerateItem()+") and wa_classitem.issysformula = 'Y'");
			} catch (DAOException e) {
				Logger.error(e);
				throw new BusinessRuntimeException(ResHelper.getString("60130classpower","060130classpower0178")/*@res "��������ϵͳ��Ŀ�Ĺ�ʽʧ�ܡ�"*/);
			}
			if (items == null || items.length == 0) {
				return;
			}
			BDPKLockUtil.lockSuperVO(items);

			for (int i = 0; i < items.length; i++) {
				HrFormula formula = new FormulaUtils().getSystemFormula(pk_org, pk_wa_class, cyear, cperiod, items[i]
						.getItemkey());
				items[i].setVformula(formula.getScirptLang());
				//20151208 shenliangc NCdp205556679  н�ʲ�����η���н�ʷ�����ֻ��һ�η��ŵ�н����Ŀֵ����ֵû���ۼơ�
				//����ԭ������ĩ�����������ڼ䷢����Ŀ��ϵͳ��Ŀ��ʽû�а�����η����ӷ�������ӵķ�����Ŀ��
				//20151209 �޸ĺ����¹��첹��
				items[i].setVformulastr(formula.getBusinessLang());
			}
			BaseDAO baseDAO = new BaseDAO();
			baseDAO.updateVOArray(items, new String[] { WaClassItemVO.VFORMULA, WaClassItemVO.VFORMULASTR});
			WaCacheUtils.synCache(items[0].getTableName());
		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
			throw e;
		}catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(ResHelper.getString("60130classpower","060130classpower0179")/*@res "����ϵͳ��Ŀ��Ĭ�Ϲ�ʽʧ�ܣ�"*/);
		}finally{

		}
	}
	
	private void generateTotalItemFormula(String pk_org, String pk_wa_class, String cyear, String cperiod) throws BusinessException {
		try {
			WaClassItemVO[] items = null;
			try {
				// ��ȡ���ڼ䣬��н�ʷ���������н�ʷ�����Ŀ
				items = classitemDAO.queryItemInfoVO(pk_org, pk_wa_class, cyear, cperiod);
			} catch (DAOException e) {
				Logger.error(e);
				throw new BusinessRuntimeException(ResHelper.getString("60130classpower","060130classpower0178")/*@res "��������ϵͳ��Ŀ�Ĺ�ʽʧ�ܡ�"*/);
			}
			if (items == null || items.length == 0) {
				return;
			}
			BDPKLockUtil.lockSuperVO(items);
				
			for (WaClassItemVO parent : items) {
				if (parent.getG_istotalitem() == null ? false : parent.getG_istotalitem().booleanValue()) {
					StringBuilder formulaSb = new StringBuilder();
					StringBuilder formulaStrSb = new StringBuilder();
					
					// ���vformula��vformulaStr�ֶ�
					parent.setVformula(null);
					parent.setVformulastr(null);
					
					// ��������н�ʷ�����Ŀ���ع���ʽ
					for (WaClassItemVO child : items) {
						if (child.getG_totaltoitem() != null && child.getG_totaltoitem().equals(parent.getItemkey())) {
							if (!child.getItemkey().equals(parent.getItemkey())) {
								if (child.getIproperty().intValue() == PropertyEnumVO.MINUS.toIntValue()) {
									formulaSb.append(" - wa_data." + child.getItemkey());
									formulaStrSb.append(" - wa_data." + child.getItemkey());
								} else {
									formulaSb.append(" + wa_data." + child.getItemkey());
									formulaStrSb.append(" + wa_data." + child.getItemkey());
								}
							}
						}
					}
					parent.setVformula(formulaSb.toString());
					parent.setVformulastr(formulaStrSb.toString());
				} else {
					continue;
				}
			}
			
			summingEPFNormalItems(items);
			summingEPFAdditionalItems(items);
			summingEISItems(items);
			summingSOCSOItems(items);
			
			BaseDAO baseDAO = new BaseDAO();
			baseDAO.updateVOArray(items, new String[] { WaClassItemVO.VFORMULA, WaClassItemVO.VFORMULASTR});
			WaCacheUtils.synCache(items[0].getTableName());
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(ResHelper.getString("60130classpower","060130classpower0179")/*@res "����ϵͳ��Ŀ��Ĭ�Ϲ�ʽʧ�ܣ�"*/);
		}
	}
	
	private void summingEPFNormalItems(WaClassItemVO[] items) {
		for (WaClassItemVO parent : items) {
			if (parent.getCode().equals("sealocal_epf_normal_base")) {
				StringBuilder formulaSb = new StringBuilder();
				StringBuilder formulaStrSb = new StringBuilder();
				
				// ���vformula��vformulaStr�ֶ�
				parent.setVformula(null);
				parent.setVformulastr(null);
				
				// ��������н�ʷ�����Ŀ���ع���ʽ
				for (WaClassItemVO child : items) {
					if (child.getMy_isepf_n() != null && child.getMy_isepf_n().booleanValue()) {
						if (child.getIproperty().intValue() == PropertyEnumVO.MINUS.toIntValue()) {
							formulaSb.append(" - wa_data." + child.getItemkey());
							formulaStrSb.append(" - wa_data." + child.getItemkey());
						} else {
							formulaSb.append(" + wa_data." + child.getItemkey());
							formulaStrSb.append(" + wa_data." + child.getItemkey());
						}
					}
				}
				parent.setVformula(formulaSb.toString());
				parent.setVformulastr(formulaStrSb.toString());
			} else {
				continue;
			}
		}
	}
	
	private void summingEPFAdditionalItems(WaClassItemVO[] items) {
		for (WaClassItemVO parent : items) {
			if (parent.getCode().equals("sealocal_epf_bonus_base")) {
				StringBuilder formulaSb = new StringBuilder();
				StringBuilder formulaStrSb = new StringBuilder();
				
				// ���vformula��vformulaStr�ֶ�
				parent.setVformula(null);
				parent.setVformulastr(null);
				
				// ��������н�ʷ�����Ŀ���ع���ʽ
				for (WaClassItemVO child : items) {
					if (child.getMy_isepf_a() != null && child.getMy_isepf_a().booleanValue()) {
						if (child.getIproperty().intValue() == PropertyEnumVO.MINUS.toIntValue()) {
							formulaSb.append(" - wa_data." + child.getItemkey());
							formulaStrSb.append(" - wa_data." + child.getItemkey());
						} else {
							formulaSb.append(" + wa_data." + child.getItemkey());
							formulaStrSb.append(" + wa_data." + child.getItemkey());
						}
					}
				}
				parent.setVformula(formulaSb.toString());
				parent.setVformulastr(formulaStrSb.toString());
			} else {
				continue;
			}
		}
	}
	
	private void summingEISItems(WaClassItemVO[] items) {
		for (WaClassItemVO parent : items) {
			if (parent.getCode().equals("sealocal_eis_base")) {
				StringBuilder formulaSb = new StringBuilder();
				StringBuilder formulaStrSb = new StringBuilder();
				
				// ���vformula��vformulaStr�ֶ�
				parent.setVformula(null);
				parent.setVformulastr(null);
				
				// ��������н�ʷ�����Ŀ���ع���ʽ
				for (WaClassItemVO child : items) {
					if (child.getMy_iseis() != null && child.getMy_iseis().booleanValue()) {
						if (child.getIproperty().intValue() == PropertyEnumVO.MINUS.toIntValue()) {
							formulaSb.append(" - wa_data." + child.getItemkey());
							formulaStrSb.append(" - wa_data." + child.getItemkey());
						} else {
							formulaSb.append(" + wa_data." + child.getItemkey());
							formulaStrSb.append(" + wa_data." + child.getItemkey());
						}
					}
				}
				parent.setVformula(formulaSb.toString());
				parent.setVformulastr(formulaStrSb.toString());
			} else {
				continue;
			}
		}
	}
	
	private void summingSOCSOItems(WaClassItemVO[] items) {
		for (WaClassItemVO parent : items) {
			if (parent.getCode().equals("sealocal_socso_base")) {
				StringBuilder formulaSb = new StringBuilder();
				StringBuilder formulaStrSb = new StringBuilder();
				
				// ���vformula��vformulaStr�ֶ�
				parent.setVformula(null);
				parent.setVformulastr(null);
				
				// ��������н�ʷ�����Ŀ���ع���ʽ
				for (WaClassItemVO child : items) {
					if (child.getMy_issocso() != null && child.getMy_issocso().booleanValue()) {
						if (child.getIproperty().intValue() == PropertyEnumVO.MINUS.toIntValue()) {
							formulaSb.append(" - wa_data." + child.getItemkey());
							formulaStrSb.append(" - wa_data." + child.getItemkey());
						} else {
							formulaSb.append(" + wa_data." + child.getItemkey());
							formulaStrSb.append(" + wa_data." + child.getItemkey());
						}
					}
				}
				parent.setVformula(formulaSb.toString());
				parent.setVformulastr(formulaStrSb.toString());
			} else {
				continue;
			}
		}
	}

	private String getRegenerateItem (){
		//Ӧ���ϼƣ��ۿ�ϼƣ����ο�˰����
		return WaItemConstant.CustomFormularSysItemKeyStr;
	}
	@Override
	public WaClassItemVO[] insertClassItemVOs(WaClassItemVO[] vos) throws BusinessException {
		if (vos != null && vos.length > 0) {
			BDPKLockUtil.lockString(vos[0].getPk_wa_class());
		}
		/**
		 * �鿴���е��ӷ�������ǰ�ڼ䣩�Ƿ����Ѿ���˷������ݵġ�������������ʾ
		 */
		WaClassVO classvo =new WaClassVO();
		classvo.setPk_wa_class(vos[0].getPk_wa_class());
		classvo.setCyear(vos[0].getCyear());
		classvo.setCperiod(vos[0].getCperiod());
		WaClassVO[] subvos = getClassitemDAO().subClassHasCheckedData(classvo);
		if(!ArrayUtils.isEmpty(subvos)){
			//�׳��쳣��������ʾ
			//String  names = FormatVO.formatArrayToString(vos, SQLHelper.getMultiLangNameColumn(WaClassVO.NAME), "");
			throw new BusinessException(ResHelper.getString("60130classpower","060130classpower0173")/*@res "�ӷ�����"*/+ ResHelper.getString("60130classpower","060130classpower0174")/*@res "�����Ѿ���˵����ݣ�����������Ŀ "*/);
		}
		//boolean needReGenFormula = false;
		for (int i = 0; i < vos.length; i++) {
			if(vos[i].getPk_group().equals(vos[i].getPk_org())){
				insertGroupClassItem(vos[i]);
			}else{
				vos[i] = insert(vos[i], false);  //����regenerateSystemFormula
			}
		}
		//tͳһ����
		regenerateSystemFormula(vos[0].getPk_org(), vos[0].getPk_wa_class(), vos[0].getCyear(), vos[0]
				.getCperiod());
		
		// HR���ػ������л�����Ĺ�ʽȫ������
		generateTotalItemFormula(vos[0].getPk_org(), vos[0].getPk_wa_class(), vos[0].getCyear(), vos[0]
				.getCperiod());

		//�õ���Ҫ���������н�ʷ�����Ŀ
		resetCompuSeq(classvo);

		resetPaydataFlag(vos[0].getPk_wa_class(),vos[0].getCyear(),vos[0].getCperiod());
		//ͬ������
		WaCacheUtils.synCache(vos[0].getTableName());
		return queryItemInfoVO(vos[0].getPk_org(), vos[0].getPk_wa_class(), vos[0].getCyear(), vos[0].getCperiod(),null);
	}

	@Override
	public WaClassItemVO[] updateWaClassItemVO(WaClassItemVO vo) throws BusinessException {
		BDPKLockUtil.lockString(vo.getPk_wa_class());
		vo = updateS(vo);
		WaCacheUtils.synCache(vo.getTableName());
		return queryItemInfoVO(vo.getPk_org(), vo.getPk_wa_class(), vo.getCyear(), vo.getCperiod(),null);
	}

	private WaClassItemVO updateS(WaClassItemVO vo) throws BusinessException {
		//20151028  xiejie3  NCdp205519152 ����Ҫ���޸�Ϊ�����ֹ�������ʱ��Ĭ��Ϊ���޸ġ�begin
		//20151201 shenliangc NCdp205519152 �������⣬������ˡ�
//		updateItemPowerEditflag(vo);
		//end
		vo = getServiceTemplate().update(vo,true);
		/**
		 * �������޸Ĺ�ʽ�����ü���˳��
		 */
		//�������ֵ�� ���Ҹ��ļ�˰��ʶ����Ҫ�������ÿ�˰�����Ĺ�ʽ
		if(needRegenerateSystemFormula(vo)){
			regenerateSystemFormula(vo.getPk_org(), vo.getPk_wa_class(), vo.getCyear(), vo.getCperiod());
		}
		// HR���ػ������л�����Ĺ�ʽȫ������
		generateTotalItemFormula(vo.getPk_org(), vo.getPk_wa_class(), vo.getCyear(), vo.getCperiod());

		//��Ƽ���˳��
		if(needResetCompuSeq(vo)){
			resetCompuSeq(vo);
		}
		//�޸���Ŀ��������Ŀ Ӧ�ý�  ��Ӧ��н��������ռ����ʶ����ˡ�����
		resetPaydataFlag(vo.getPk_wa_class(),vo.getCyear(),vo.getCperiod());

		//ͬ��������
		synParentClassItem(VOStatus.UPDATED, vo);
		return vo;
	}

	private boolean 	needRegenerateSystemFormula(WaClassItemVO vo){
		return vo.getIitemtype()==0 && !ItemUtils.isSystemItemKey(vo.getItemkey());
	}


	@Override
	public WaClassItemVO[] batchAddClassItemVOs(WaLoginContext context, String[] pk_wa_items) throws BusinessException {
		BDPKLockUtil.lockString(pk_wa_items);

		//������Щ��Ŀʹ�õ�����Ŀ���ࡣ�鿴��Ŀ�����Ƿ񻹴���

		//�鿴��������Ŀ�Ƿ����
		WaItemVO[] item = NCLocator.getInstance().lookup(IItemManageService.class).queryWaItemVOByPks(pk_wa_items);
		if (item == null || item.length != pk_wa_items.length) {
			throw new BusinessException(ResHelper.getString("60130classpower","060130classpower0180")/*@res "�����Ѳ�ͬ���������´򿪽ڵ�����"*/);
		}

		if(context.getNodeType().equals(NODE_TYPE.ORG_NODE)){
			return batchAddParentAndCurrentclass(context.getPk_group(),context.getPk_org(),context.getPk_wa_class(),
					context.getCyear(),context.getCperiod(), item);

		}else{
			//���ŵ�
			/**
			 * �鿴���ŷ����ȥ����֯��������ǰ�ڼ䣩�Ƿ����Ѿ���˷������ݵġ�������������ʾ
			 */
			//guoqt����ڼ�Ӧ����ѡ�������ڼ䣬�����Ǽ��ŷ���������ڼ�
			context.getWaLoginVO().setCyear(context.getCyear());
			context.getWaLoginVO().setCperiod(context.getCperiod());
			WaClassVO[] vos = getClassitemDAO().subClassHasCheckedData(context.getWaLoginVO());
			if(!ArrayUtils.isEmpty(vos)){
				//�׳��쳣��������ʾ
				//String  names = FormatVO.formatArrayToString(vos, SQLHelper.getMultiLangNameColumn(WaClassVO.NAME), "");
				throw new BusinessException(ResHelper.getString("60130classpower","060130classpower0173")/*@res "�ӷ�����"*/+ ResHelper.getString("60130classpower","060130classpower0174")/*@res "�����Ѿ���˵����ݣ�����������Ŀ "*/);
			}

			//���ȸ����ŷ���ִ�в���
			WaClassItemVO[]  resultvos = batchAddParentAndCurrentclass(context.getPk_group(),
					context.getPk_org(),context.getPk_wa_class(),context.getCyear(),context.getCperiod(),item);

			//Ȼ������еķ����ȥ����֯����ִ�в���
			WaClassVO[] allvos =  getClassitemDAO().queryGroupAssignedWaclass(context.getWaLoginVO());
			if(allvos!=null){
				for (WaClassVO waClassVO : allvos) {
					//guoqt NCZX����NCdp205050978���ŷ������䵽�����֯�����֯�ڼ䲻һ��ʱ������ʱ��ʾ������Ŀδ��ӵ�����
					if(waClassVO.getCyear().equals(context.getCyear())&&waClassVO.getCperiod().equals(context.getCperiod())){
						batchAddParentAndCurrentclass(waClassVO.getPk_group(),waClassVO.getPk_org(),waClassVO.getPk_wa_class(),
								waClassVO.getCyear(),waClassVO.getCperiod(), item);
					}
					
				}
			}

			return resultvos;
		}


	}


	private  WaClassItemVO[]  batchAddParentAndCurrentclass(String pk_group,String pk_org,String pk_wa_class,String cyear,String cperiod ,WaItemVO[] item) throws BusinessException{
		WaClassItemVO[] classItems = new WaClassItemVO[item.length];
		IWaClass  waClass  = NCLocator.getInstance().lookup(IWaClass.class);



		//���Ȳ���������Ŀ
		WaClassItemVO 	tempclassitem = new WaClassItemVO();
		tempclassitem.setPk_org(pk_org);
		tempclassitem.setPk_group(pk_group);
		tempclassitem.setPk_wa_class(pk_wa_class);
		tempclassitem.setCyear(cyear);
		tempclassitem.setCperiod(cperiod);
		int beginseq = getMaxDisplayOrder(tempclassitem);

		for (int i = 0; i < classItems.length; i++) {

			classItems[i] = new WaClassItemVO();
			classItems[i].setPk_org(pk_org);
			classItems[i].setPk_group(pk_group);
			classItems[i].setPk_wa_class(pk_wa_class);
			classItems[i].setCyear(cyear);
			classItems[i].setCperiod(cperiod);
			classItems[i].merge(item[i]);
			classItems[i].setIdisplayseq(beginseq+i);

			classItems[i].setIcomputeseq(0);
			classItems[i].setStatus(VOStatus.NEW);
			//Round_typeĬ��ֵΪ0
			//if(classItems[i].getIitemtype().equals(TypeEnumVO.FLOATTYPE.value())){
			classItems[i].setRound_type(RoundTypeEnum.ROUND.value());
			//}

		}

		WaClassVO tempvo = new WaClassVO();
		tempvo.setPk_org(pk_org);
		tempvo.setPk_group(pk_group);
		tempvo.setPk_wa_class(pk_wa_class);
		tempvo.setCyear(cyear);
		tempvo.setCperiod(cperiod);
		batchadd(tempvo, classItems);

		//����������Ŀ��ϵͳ��ʽ
		regenerateSystemFormula(pk_org, pk_wa_class, cyear, cperiod);//(tempclassitem);
		
		// HR���ػ������л�����Ĺ�ʽȫ������
		generateTotalItemFormula(pk_org, pk_wa_class, cyear, cperiod);

		//�����趨����˳��
		resetCompuSeq(tempclassitem);

		//��ǰ���������趨���ű�ʾ
		resetPaydataFlag(tempvo.getPk_wa_class(),tempvo.getCyear(),tempvo.getCperiod());

		//������нн����ĿĬ��Ȩ��
		if(WaLoginVOHelper.isNormalClass(tempvo)){
			insertItemPower(classItems);
		}

		//ͬ��������	.����������Ҫ�����趨����˳��������ϵͳ��ʽ
		WaClassVO parentvo = waClass.queryParentClass(pk_wa_class,cyear,cperiod);

		if(parentvo!=null){
			for (int i = 0; i < classItems.length; i++) {
				classItems[i] = new WaClassItemVO();
				classItems[i].setPk_org(pk_org);
				classItems[i].setPk_group(pk_group);
				classItems[i].setPk_wa_class(parentvo.getPk_wa_class());
				classItems[i].setCyear(parentvo.getCyear());
				classItems[i].setCperiod(parentvo.getCperiod());
				classItems[i].merge(item[i]);
				classItems[i].setIdisplayseq(beginseq+i);

				classItems[i].setIcomputeseq(0);
				classItems[i].setStatus(VOStatus.NEW);
				//Round_typeĬ��ֵΪ0
				//if(classItems[i].getIitemtype().equals(TypeEnumVO.FLOATTYPE.value())){
				classItems[i].setRound_type(RoundTypeEnum.ROUND.value());
				//}
			}

			ArrayList<WaClassItemVO> classItemVOList = new ArrayList<WaClassItemVO>();
			HashMap<String, String> map = getClassitemDAO().isItemExist(parentvo, classItems);
			for(WaClassItemVO waClassItemVO:classItems){
				if(map.get(waClassItemVO.getItemkey())==null){
					classItemVOList.add(waClassItemVO);
				}
			}

			batchadd(parentvo, classItems);

			//������Ĭ�Ϸ���Ȩ��
			if(!classItemVOList.isEmpty())
				insertItemPower(classItemVOList.toArray( new WaClassItemVO[classItemVOList.size()]));
			//�����趨���ű�ʾ���������ģ�
			resetPaydataFlag(parentvo.getPk_wa_class(),tempvo.getCyear(),tempvo.getCperiod());

		}

		//���»���
		WaCacheUtils.synCache(WaClassItemVO.TABLE_NAME);


		return queryItemInfoVO( pk_org,pk_wa_class,cyear,cperiod,null);
	}

	private void insertItemPower(WaClassItemVO[] vos) throws BusinessException {
		ArrayList<ItemPowerVO> itemPowerVOs = new ArrayList<ItemPowerVO>();
		for (WaClassItemVO vo : vos) {
			ItemPowerVO itemPowerVO = new ItemPowerVO();
			itemPowerVO.setPk_wa_class(vo.getPk_wa_class());
			itemPowerVO.setPk_wa_item(vo.getPk_wa_item());
			itemPowerVO.setModuleflag(Integer.valueOf(0));
			itemPowerVO.setPk_group(vo.getPk_group());
			itemPowerVO.setPk_org(vo.getPk_org());
			itemPowerVO.setPk_subject(AuditInfoUtil.getCurrentUser());
			itemPowerVO.setSubject_type(IWaSalaryctymgtConstant.SUB_TYPE_USER);
			itemPowerVO.setEditflag(vo.getIfromflag().intValue() == 2/* �ֹ�������Ŀ */? UFBoolean.TRUE : UFBoolean.FALSE);
			itemPowerVOs.add(itemPowerVO);
		}
		new ClassItemPowerServiceImpl().insertItemPowerVOs(
				itemPowerVOs.toArray(new ItemPowerVO[0]),
				itemPowerVOs.toArray(new ItemPowerVO[0]));
	}

	/**
	 * @throws DAOException
	 * @throws MetaDataException
	 * 
	 */
	private void batchadd(WaClassVO waclassvo,WaClassItemVO[] classItems) throws DAOException, MetaDataException{
		//����ɾ�����е���Ŀ
		String delete  = "  delete from wa_classitem where pk_wa_class = '"+waclassvo.getPk_wa_class()+"' and cyear = '"
				+waclassvo.getCyear()+"' and cperiod = '"+waclassvo.getCperiod()+"' and itemkey in ("+FormatVO.formatArrayToString(classItems, WaClassItemVO.ITEMKEY)+") ";
		getClassitemDAO().getBaseDao().executeUpdate(delete);

		//��������
		getMDPersistenceService().saveBill(classItems);

	}





	@Override
	public WaClassItemVO[] setDisplayOrder(WaClassItemVO[] data) throws BusinessException {
		BDPKLockUtil.lockSuperVO(data);
		BDVersionValidationUtil.validateSuperVO(data);
		String[] pks = new String[data.length];
		for (int i = 0; i < data.length; i++) {
			data[i].setIdisplayseq(i);
			pks[i] = data[i].getPrimaryKey();
		}
		new BaseDAO().updateVOArray(data, new String[] { WaClassItemVO.IDISPLAYSEQ });
		data = getServiceTemplate().queryByPks(WaClassItemVO.class, pks);
		Arrays.sort(data, new DisplayOrderComparator());
		return data;
	}

	/**
	 *
	 * @author: wh
	 * @date: 2009-12-11 ����02:35:39
	 * @since: eHR V6.0
	 * @�߲���:
	 * @�߲�����:
	 * @�޸���:
	 * @�޸�����:
	 */
	private final class DisplayOrderComparator implements Comparator<WaClassItemVO> {
		@Override
		public int compare(WaClassItemVO o1, WaClassItemVO o2) {

			return o1.getIdisplayseq() - o2.getIdisplayseq();
		}
	}

	/**
	 * @author wh on 2009-12-18
	 * @see nc.itf.hr.wa.IClassItemQueryService#queryForCopy(java.lang.String,
	 *      nc.vo.wa.pub.WaLoginContext)
	 */
	@Override
	public WaClassItemVO[] queryForCopy(String src_pk_wa_class, WaLoginContext context)
			throws BusinessException {
		String where = "  wa_classitem.pk_wa_item not in (select pk_wa_item from wa_classitem where pk_wa_class = '"
				+ context.getPk_wa_class() + "' and pk_org = '" + context.getPk_org() + "' and cyear = '"
				+ context.getWaYear() + "' and cperiod = '" + context.getWaPeriod() + "')";


		return getClassitemDAO().queryItemInfoVO(context.getPk_org(),src_pk_wa_class,context.getWaYear() ,context.getWaPeriod(),where);
	}

	@Override
	public WaClassItemVO[] queryCustomItemInfos(String pk_org, String pk_wa_class, String cyear,
			String cperiod) throws BusinessException {
		return getClassitemDAO().queryItemInfoVO(pk_org, pk_wa_class, cyear, cperiod,
				" wa_item.defaultflag = 'N'");
	}

	synchronized private int getMaxDisplayOrder(WaClassItemVO vo) throws BusinessException {

		String sql = "select max(idisplayseq)+1 from wa_classitem where pk_org = ? and pk_wa_class = ? and cyear = ? and cperiod = ?";
		SQLParameter par = new SQLParameter();
		par.addParam(vo.getPk_org());
		par.addParam(vo.getPk_wa_class());
		par.addParam(vo.getCyear());
		par.addParam(vo.getCperiod());
		Integer seq = (Integer) new BaseDAO().executeQuery(sql, par, new ColumnProcessor());
		if (seq == null)
			return 0;
		return seq;
	}

	@Override
	public String queryItemKeyByPk(String pk_wa_classitem) throws BusinessException {

		String sql = "select wa_item.itemkey from wa_item inner join wa_classitem on wa_item.pk_wa_item = wa_classitem.pk_wa_item where pk_wa_classitem = ?";
		SQLParameter par = new SQLParameter();
		par.addParam(pk_wa_classitem);
		BaseDAO dao = new BaseDAO();
		String itemKey = (String) dao.executeQuery(sql, par, new ColumnProcessor());

		return itemKey;
	}

	/**
	 * @author xuanlt on 2010-1-18
	 * @see nc.itf.hr.wa.IClassItemQueryService#queryAllClassItemInfos(nc.vo.uif2.LoginContext,
	 *      java.lang.String)
	 */
	@Override
	public WaClassItemVO[] queryAllClassItemInfos(String pk_org, String pk_wa_class, String cyear,
			String cperiod) throws BusinessException {

		return getClassitemDAO().queryItemInfoVO(pk_org, pk_wa_class, cyear, cperiod, null);

	}

	@Override
	public WaClassItemVO[] queryItemInfoVO(String pk_org,
			String pk_wa_class, String year, String period, String condition)
					throws BusinessException {
		return getClassitemDAO().queryItemInfoVO(pk_org, pk_wa_class, year, period, condition);
	}

//	20151104 xiejie3  ��ʾ��������н����ĿȨ�޿��ơ�
	@Override
	public WaClassItemVO[] queryAllClassItemInfosByPower(WaLoginContext context) throws BusinessException {

		return getClassitemDAO().queryItemInfoVO(context.getPk_org(), context.getPk_wa_class(), context
				.getWaYear(), context.getWaPeriod(), " wa_item.itemkey  in ("+ItemPowerUtil.getItemPowerCode(context)+")   ");

	}
//	end
	@Override
	public WaClassItemVO[] queryAllClassItemInfos(WaLoginContext context) throws BusinessException {

		return getClassitemDAO().queryItemInfoVO(context.getPk_org(), context.getPk_wa_class(), context
				.getWaYear(), context.getWaPeriod(), null);

	}

	//2015-08-17 zhousze �ϲ����� NCdp205246061   ���ֲ�ѯ����Ĳ�ѯ������ѡ��������н����Ŀ��(���ַ���н����Ŀ��)ʱ��
	// ���ܹ�ѡ����Ȩ�޿�������Ŀ begin
	public WaClassItemVO[] queryAllClassItemPowerInfos(WaLoginContext context) throws BusinessException {

		return getClassitemDAO().queryItemInfoVO(context.getPk_org(), context.getPk_wa_class(), context
				.getWaYear(), context.getWaPeriod(), " wa_item.pk_wa_item in ("+ItemPowerUtil.getItemPower(context)+")");
	}
	// end

	@Override
	public ItemVO[] getFormulaInitVO(WaLoginContext context) throws BusinessException {
		ArrayList<ItemVO> itemList = new ArrayList<ItemVO>();

		initClassItem(context, itemList);

		if (waCommonImpl.isHiEnabled(context.getPk_group())) {
			// TODO ��ѯ��Ϣ����Ŀǰ��Ϣ����û����
		} else {
			// ֻ��װн�ʲ���װԱ����Ϣ����Ҫ�ڹ�ʽ���õ���Ŀһ�����롰��Ա��𡱡������ڲ��š�
			itemServiceImpl.initItemByPsncl(context, itemList);
			itemServiceImpl.initItemByDept(context, itemList);
		}

		return itemList.toArray(new ItemVO[0]);
	}

	public void initClassItem(WaLoginContext context, ArrayList<ItemVO> itemList) throws BusinessException {
		// �õ����е�н�ʷ�����Ŀ
		WaClassItemVO[] clsItems = queryAllClassItemInfos(context);
		// ��ӷ�����Ŀ

		if (ArrayUtils.isEmpty(clsItems)) {
			Logger.warn("û�в�ѯ��н�ʷ�����Ŀ�����ǲ������ģ�");
			return;
		}
		ContentVO[] contentVOs = new ContentVO[clsItems.length];
		for (int i = 0; i < contentVOs.length; i++) {
			contentVOs[i] = buildByWaItemVO(clsItems[i]);
		}
		ItemVO waItem = new ItemVO(NCLangResOnserver.getInstance().getStrByID("common", "UC000-0003385")/*
		 * @res
		 * "н����Ŀ"
		 */);
		waItem.setContent(contentVOs);
		itemList.add(waItem);

	}

	private ContentVO buildByWaItemVO(WaClassItemVO item) {
		ContentVO content = new ContentVO(item.getMultilangName());
		content.setColNameFlag(true); //
		content.setTableName("wa_data");
		content.setDigitFlag(item.getTypeEnumVO() == TypeEnumVO.FLOATTYPE);
		content.setColName(item.getItemkey());
		return content;
	}

	/**
	 * @author xuanlt on 2010-2-24
	 * @see nc.itf.hr.wa.IClassItemQueryService#queryAllClassItems(java.lang.String)
	 */
	@Override
	public WaClassItemVO[] queryAllClassItems(String pk_wa_class) throws BusinessException {
		return getClassitemDAO().queryItemsByClassId(pk_wa_class, null);
	}

	/**
	 * @author xuanlt on 2010-2-24
	 * @see nc.itf.hr.wa.IClassItemQueryService#queryAllClassItems(java.lang.String)
	 */
	@Override
	public WaClassItemVO[] queryAllClassItemsForFormular(String pk_wa_class) throws BusinessException {
		return getClassitemDAO().queryItemsByClassIdForFormular(pk_wa_class, null);
	}

	/**
	 * @author liangxr on 2010-3-17
	 * @see nc.itf.hr.wa.IClassItemQueryService#queryItemInfoVO(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public WaClassItemVO[] queryItemInfoVO(String pk_org, String pk_wa_class, String year, String period)
			throws BusinessException {
		return getClassitemDAO().queryItemInfoVO(pk_org, pk_wa_class, year, period);
	}

	/**
	 * @author xuanlt on 2009-12-22
	 * @see nc.itf.hr.wa.IItemQueryService#queryMaxversionItems(nc.vo.wa.category.WaClassVO)
	 */
	@Override
	public WaClassItemVO[] queryMaxversionItems(WaClassVO vo) throws BusinessException {
		try {
			PersistenceDAO dao = new PersistenceDAO();
			StringBuilder sbd = new StringBuilder();
			sbd.append(" select * from wa_classitem  where cyear||cperiod=  (select max(cyear||cperiod) from wa_classitem where pk_wa_class = ? ) and pk_wa_class = ? and pk_group = ? and pk_org = ? ");
			SQLParameter para = new SQLParameter();
			para.addParam(vo.getPk_wa_class());
			para.addParam(vo.getPk_wa_class());
			para.addParam(vo.getPk_group());
			para.addParam(vo.getPk_org());

			return dao.retrieveBySQL(WaClassItemVO.class, sbd.toString(), para);
		} catch (PersistenceDbException e) {
			Logger.error(e);
			throw new BusinessException(ResHelper.getString("60130classpower","060130classpower0181")/*@res "��ѯ����н�ʷ����������Ĺ�����Ŀʧ��"*/,e);
		}

	}

	/**
	 * @author xuanlt on 2009-12-22
	 * @see nc.itf.hr.wa.IItemQueryService#queryMaxversionItems(nc.vo.wa.category.WaClassVO)
	 */
	@Override
	public WaClassItemVO[] queryVersionItems(WaClassVO vo,String cyear,String cperiod) throws BusinessException {
		try {
			PersistenceDAO dao = new PersistenceDAO();
			StringBuilder sbd = new StringBuilder();
			sbd.append(" select * from wa_classitem  where cyear = ? and cperiod = ? and pk_wa_class = ? and pk_group = ? and pk_org = ? ");
			SQLParameter para = new SQLParameter();
			para.addParam(cyear);
			para.addParam(cperiod);
			para.addParam(vo.getPk_wa_class());
			para.addParam(vo.getPk_group());
			para.addParam(vo.getPk_org());

			return dao.retrieveBySQL(WaClassItemVO.class, sbd.toString(), para);
		} catch (PersistenceDbException e) {
			Logger.error(e);
			throw new BusinessException(ResHelper.getString("60130classpower","060130classpower0181")/*@res "��ѯ����н�ʷ����������Ĺ�����Ŀʧ��"*/,e);
		}

	}

	@Override
	public WaItemVO[] queryCustomItems(WaLoginContext context) throws BusinessException{
		return new QueryByWaLoginContextAction(context," and defaultflag = 'N'").query();
	}


	/**
	 *
	 * @author: wh
	 * @date: 2009-12-29 ����02:43:25
	 * @since: eHR V6.0
	 * @�߲���:
	 * @�߲�����:
	 * @�޸���:
	 * @�޸�����:
	 */
	private final class QueryByWaLoginContextAction implements ItemQueryAction {
		/**
		 * @author wh on 2009-12-29
		 */
		private final String	pk_org;
		/**
		 * @author wh on 2009-12-29
		 */
		private final String	period;
		/**
		 * @author wh on 2009-12-29
		 */
		private final String	year;
		/**
		 * @author wh on 2009-12-29
		 */
		private final String	pk_wa_class;

		private final String	condition;

		private QueryByWaLoginContextAction(WaLoginContext context,String condition){
			this(context.getPk_org(),context.getWaPeriod(),context.getWaYear(),context.getPk_wa_class(),condition);
		}

		/**
		 * @author wh on 2009-12-29
		 * @param pk_org
		 * @param period
		 * @param year
		 * @param pk_wa_class
		 */
		private QueryByWaLoginContextAction(String pk_org, String period, String year, String pk_wa_class,String condition) {
			this.pk_org = pk_org;
			this.period = period;
			this.year = year;
			this.pk_wa_class = pk_wa_class;
			this.condition = condition == null ? "" : condition;
		}

		@Override
		public WaItemVO[] query() {

			try {
				String where = " pk_wa_item in (select pk_wa_item from wa_classitem where pk_org = '" + pk_org
						+ "' and pk_wa_class = '" + pk_wa_class + "' and cyear = '" + year + "' and cperiod = '"
						+ period +"')"+condition+"  order by code";
				return getServiceTemplate().queryByCondition(WaItemVO.class, where);
			} catch (BusinessException e) {
				Logger.error(e);
				return null;
			}
		}
	}

	interface ItemQueryAction{
		WaItemVO[] query();
	}



	/**
	 *
	 * @param pk_class
	 * @param year
	 * @param period
	 * @return
	 * @throws BusinessException
	 */
	@Override
	public GeneralVO[] getItemCaculateSeu(WaClassVO vo ) throws BusinessException {
		try {
			//updateItemCaculateSeu(vo);

			List<GeneralVO> generalVOList = new LinkedList<GeneralVO>();
			WaClassItemVO[] classitemVOs =getClassitemsBySeq(vo);
			if (classitemVOs != null && classitemVOs.length > 0) {
				WaClassVO waclassVO = NCLocator.getInstance().lookup(IWaClass.class).queryWaClassByPK(vo.getPk_wa_class());
				waclassVO.setCyear(vo.getCyear());
				waclassVO.setCperiod(vo.getCperiod());

				ItemSort itemSort = new ItemSort();
				HashMap<WaClassItemVO, List<WaClassItemVO>> map = itemSort.getItemHashMap(classitemVOs, waclassVO);


				Iterator<WaClassItemVO> iterator = map.keySet().iterator();
				while (iterator.hasNext()) {
					GeneralVO generalVO = new GeneralVO();
					WaClassItemVO classitemVO = iterator.next();
					generalVO.setAttributeValue("itemName", classitemVO.getMultilangName());
					List<WaClassItemVO> list = map.get(classitemVO);
					if (list != null) {
						generalVO.setAttributeValue("dependItems", FormatVO.formatArrayToString(list.toArray(new WaClassItemVO[list.size()]), "name", "", ""));
					}
					generalVO.setAttributeValue("dataFrom", classitemVO.getFromEnumVO().getName());
					generalVOList.add(generalVO);
				}
			}
			return generalVOList.size() == 0 ? null : generalVOList.toArray(new GeneralVO[generalVOList.size()]);
		} catch (DAOException de) {
			Logger.error(de.getMessage(),de);
			throw new BusinessException(ResHelper.getString("60130classpower","060130classpower0182")/*@res "�õ�н�ʷ�����Ŀ�ļ���˳��ʧ��"*/);
		}catch(BusinessException be){
			Logger.error(be.getMessage(),be);
			throw  be;
		}catch(Exception e){
			Logger.error(e.getMessage(),e);
			throw new BusinessException(ResHelper.getString("60130classpower","060130classpower0182")/*@res "�õ�н�ʷ�����Ŀ�ļ���˳��ʧ��"*/);
		}
	}


	/**
	 * �������ݵļ���˳��
	 * @param pk_class
	 * @param year
	 * @param period
	 * @throws BusinessException
	 */
	public void updateItemCaculateSeu(WaClassVO vo ) throws BusinessException {
		try {
			WaClassItemVO[] classitemVOs =getClassitemVOsForSequ(vo);
			if (classitemVOs != null && classitemVOs.length > 0) {

				//���н�ʹ���ļ��㹫ʽ
				classitemVOs = fillWageFormRule(classitemVOs);
				WaClassVO waclassVO = NCLocator.getInstance().lookup(IWaClass.class).queryWaClassByPK(vo.getPk_wa_class());
				waclassVO.setCyear(vo.getCyear());
				waclassVO.setCperiod(vo.getCperiod());
				ItemSort itemSort = new ItemSort();
				classitemVOs = itemSort.getSortedWaClassItemVOs(classitemVOs, waclassVO);
				updateItemCaculateSeu(classitemVOs);

				//����н�ʷ�����Ŀ˳��Ҫͬ������
				if(!ArrayUtils.isEmpty(classitemVOs)){
					WaCacheUtils.synCache(classitemVOs[0].getTableName());
				}
			}
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage());
		}
	}


	private  WaClassItemVO[] fillWageFormRule(WaClassItemVO[] classitemVOs) throws BusinessException{
		//���н�ʹ���ļ��㹫ʽ
		for (WaClassItemVO classitemVO : classitemVOs) {
			if(classitemVO.getFromEnumVO().equals(FromEnumVO.WA_WAGEFORM)){//н�ʹ����
				String formula = classitemVO.getVformula();
				if (!StringUtils.isBlank(formula)) {
					Map<String, FunctionVO> map =  HrWaXmlReader.getInstance().getFormulaParser();
					FunctionVO functionVO =		map.get(FunctionKey.WAGEFORM);
					String[] pks = FormulaXmlHelper.getArguments(
							functionVO, formula);
					if (classitemVO.getVformula() != null) {
						classitemVO.setVformula(getFormulaFromWageForm(pks[0]));
					}
				} else {
					classitemVO.setVformula("");
				}
			}
		}

		return classitemVOs;

	}

	/**
	 * н�ʹ������ļ���˳��
	 *
	 * @param pk_wa_wageForm
	 * @return
	 * @throws BusinessException
	 */
	public String getFormulaFromWageForm(String pk_wa_wageForm) throws BusinessException {
		StringBuffer sqlB = new StringBuffer("select  vformula  from wa_wageformdet where pk_wa_wageform = '" + pk_wa_wageForm + "'");
		GeneralVO[] generalVOs = (GeneralVO[])new BaseDAO().executeQuery(sqlB.toString(), new GeneralVOProcessor(GeneralVO.class));

		if (generalVOs != null) {
			return FormatVO.formatArrayToString(generalVOs, "vformula", "");
		} else {
			return null;
		}
	}

	public void updateItemCaculateSeu(WaClassItemVO[] classitemVOs) throws BusinessException {
		PersistenceManager sessionManager = null;
		try {
			StringBuffer sqlB = new StringBuffer();
			sqlB.append("update wa_classitem set wa_classitem.icomputeseq = ? where wa_classitem.pk_wa_classitem = ?"); // 1
			sessionManager = PersistenceManager.getInstance();
			JdbcSession session = sessionManager.getJdbcSession();
			for (WaClassItemVO classitemVO : classitemVOs) {
				SQLParameter parameters = new SQLParameter();
				parameters.addParam(classitemVO.getIcomputeseq());
				parameters.addParam(classitemVO.getPk_wa_classitem());
				session.addBatch(sqlB.toString(), parameters);
			}
			session.executeBatch();
		} catch (DbException e) {
			throw new nc.vo.pub.BusinessException(e.getMessage());
		} finally {
			if (sessionManager != null) {
				sessionManager.release();
			}
		}

	}

	@Override
	public HrFormula getSystemFormula(String pk_org, String pk_wa_class,
			String cyear, String cperiod, String itemKey) {
		HrFormula f =  new FormulaUtils().getSystemFormula(pk_org, pk_wa_class, cyear,
				cperiod, itemKey);

		return f;
	}


	/**
	 * ����һ��н����Ŀ���õ������ڸ���Ŀ��н�ʷ�����Ŀ
	 *  ����ϵͳ��Ŀʹ��Ĭ�Ϲ�ʽ�ģ���ʹ�����ڸ���Ŀ��Ҳ��ͳ�ƣ�����ɾ������Ŀ������ɾ�����������ü��㹫ʽ��
	 *  	    ����ϵͳ��Ŀ��ʹ��Ĭ�Ϲ�ʽ�ģ���ͳ��
	 *
	 * @param classItemVO
	 * @return
	 * @throws BusinessException
	 */
	@Override
	public WaClassItemVO[]  getDependorItems(WaClassItemVO classItemVO) throws BusinessException{
		String itemkey = classItemVO.getItemkey();

		ArrayList<WaClassItemVO> list = new ArrayList<WaClassItemVO>();
		WaClassVO vo = new WaClassVO();
		vo.setPk_wa_class(classItemVO.getPk_wa_class());
		vo.setCyear(classItemVO.getCyear());
		vo.setCperiod(classItemVO.getCperiod());
		WaClassItemVO[] classitemVOs =getClassitemVOsForSequ(vo);
		if (classitemVOs != null && classitemVOs.length > 0) {
			ISortHelper  helper = new WaOrtherFuncSortHelper();


			//���н�ʹ���ļ��㹫ʽ
			classitemVOs = fillWageFormRule(classitemVOs);
			//�����Դ������ϵͳн�ʣ���Ҫ���⴦��
			for (int index = 0; index < classitemVOs.length; index++) {
				if(!classitemVOs[index].getIssysformula().booleanValue()){
					if(classitemVOs[index].getIfromflag()==6){
						//ʹ�ð�������⴦��,��������Ŀ�Ƿ�����itemkey
						List<String> ll = 	helper.getDependItemKeys(classitemVOs[index]);
						if(ll.contains(itemkey)){
							list.add(classitemVOs[index]);
						}

					}else{

						String formular=classitemVOs[index].getVformula();
						if (!StringUtils.isBlank(formular)) {
							Pattern p = Pattern.compile(itemkey + "[0-9]");
							Matcher m = p.matcher(formular);
							if (formular.contains(itemkey) && !m.find())
								list.add(classitemVOs[index]);
						}

					}
				}
			}
		}

		return list.toArray(new WaClassItemVO[list.size()] );
	}


	/**
	 * ����
	 * @param vo
	 * @return
	 */
	@Override
	public WaClassItemVO[] updateGroupClassItem(WaClassItemVO vo) throws BusinessException{
		WaClassVO classvo =new WaClassVO();
		classvo.setPk_wa_class(vo.getPk_wa_class());
		classvo.setCyear(vo.getCyear());
		classvo.setCperiod(vo.getCperiod());

		WaClassVO[] vos = getClassitemDAO().subClassHasCheckedData(classvo);
		if(!ArrayUtils.isEmpty(vos)){
			//�׳��쳣��������ʾ
			//String  names = FormatVO.formatArrayToString(vos, SQLHelper.getMultiLangNameColumn(WaClassVO.NAME), "");
			throw new BusinessException(ResHelper.getString("60130classpower","060130classpower0173")/*@res "�ӷ�����"*/+ ResHelper.getString("60130classpower","060130classpower0183")/*@res "�����Ѿ���˵����ݣ������޸���Ŀ "*/);
		}

		//���ȸ������Լ���
		vo = updateS(vo);

		//Ȼ������ӷ�����
		/**
		 * ��������֯����Ŀ
		 */

		WaClassVO[] allvos =  getClassitemDAO().queryGroupAssignedWaclass(classvo);


		if(!ArrayUtils.isEmpty(allvos)){
			for (WaClassVO waClassVO : allvos) {
				//guoqt NCZX����NCdp205050978���ŷ������䵽�����֯�����֯�ڼ䲻һ��ʱ������ʱ��ʾ������Ŀδ��ӵ�����
				if(waClassVO.getCyear().equals(vo.getCyear())&&waClassVO.getCperiod().equals(vo.getCperiod())){
					insertClassItem2SubClass(vo,waClassVO);
				}
			}
		}

		WaCacheUtils.synCache(vo.getTableName());
		return queryItemInfoVO(vo.getPk_org(),vo.getPk_wa_class(),vo.getCyear(),vo.getCperiod(),null);
		
		//	20150728 xiejie3 �����ϲ���NCdp205382570��н�ʷ����У���ʾ��������н����Ŀ��Щ������ʾ���ݣ�begin 	
	}
	//guoqtͬ��н�ʷ�����Ŀ��ʾ˳��
	public WaClassItemVO[] updateGroupClassItemdisp(WaClassItemVO[] ordervo) throws BusinessException{
		WaClassVO classvo =new WaClassVO();
		classvo.setPk_wa_class(ordervo[0].getPk_wa_class());
		classvo.setCyear(ordervo[0].getCyear());
		classvo.setCperiod(ordervo[0].getCperiod());
		//�ж�н�ʷ����Ƿ����
		WaClassVO[] vos = getClassitemDAO().subClassHasCheckedData(classvo);
		if(!ArrayUtils.isEmpty(vos)){
			//�׳��쳣��������ʾ
			//String  names = FormatVO.formatArrayToString(vos, SQLHelper.getMultiLangNameColumn(WaClassVO.NAME), "");
			throw new BusinessException(ResHelper.getString("60130classpower","060130classpower0173")/*@res "�ӷ�����"*/+ ResHelper.getString("60130classpower","060130classpower0183")/*@res "�����Ѿ���˵����ݣ������޸���Ŀ "*/);
		}

		//Ȼ������ӷ�������ʾ˳��
		WaClassVO[] allvos =  getClassitemDAO().queryGroupAssignedWaclass(classvo);
		//������ʾ˳��
		for(int i = 0;i<ordervo.length;i++){
			ordervo[i].setIdisplayseq(i);
		}
		if(!ArrayUtils.isEmpty(allvos)){
			for (WaClassVO waClassVO : allvos) {
				updateClassItem2SubClass(ordervo,waClassVO);
			}
		}

		WaCacheUtils.synCache(ordervo[0].getTableName());
		return queryItemInfoVO(ordervo[0].getPk_org(),ordervo[0].getPk_wa_class(),ordervo[0].getCyear(),ordervo[0].getCperiod(),null);
//		end

	}


	/**
	 * ɾ������н�ʷ���
	 * @param vo
	 * @return
	 */
	@Override
	public WaClassItemVO[] deleteGroupClassItem(WaClassItemVO vo) throws BusinessException{
		WaClassVO classvo =new WaClassVO();
		classvo.setPk_wa_class(vo.getPk_wa_class());
		classvo.setCyear(vo.getCyear());
		classvo.setCperiod(vo.getCperiod());

		WaClassVO[] vos = getClassitemDAO().subClassHasCheckedData(classvo);
		if(!ArrayUtils.isEmpty(vos)){
			//�׳��쳣��������ʾ
			//String  names = FormatVO.formatArrayToString(vos, SQLHelper.getMultiLangNameColumn(WaClassVO.NAME), "");
			throw new BusinessException(ResHelper.getString("60130classpower","060130classpower0173")/*@res "�ӷ�����"*/+ ResHelper.getString("60130classpower","060130classpower0184")/*@res "�����Ѿ���˵����ݣ�����ɾ����Ŀ "*/);
		}

		//���ȸ�ɾ���Լ���
		deleteWaClassItemVO(vo);
		CacheProxy.fireDataDeleted(vo.getTableName(), vo.getPk_wa_classitem());
		//Ȼ������ӷ�����
		/**
		 * ��������֯����Ŀ
		 */

		WaClassVO[] allvos = getClassitemDAO().queryGroupAssignedWaclass(classvo);
		if (allvos != null) {
			for (WaClassVO waClassVO : allvos) {
				deleteClassItem2SubClass(vo, waClassVO);
			}
		}
		return queryItemInfoVO(vo.getPk_org(),vo.getPk_wa_class(),vo.getCyear(),vo.getCperiod(),null);

	}

	@Override
	public boolean copyClassItems(WaClassVO srcVO,WaClassVO destVO) throws BusinessException{
		//��ѯ�����еķ�����Ŀ
		WaClassItemVO[] newvos = 	getClassitemDAO().queryItemInfoVO(srcVO.getPk_org(), srcVO.getPk_wa_class(), srcVO.getCyear(), srcVO.getCperiod());
		//�滻н�ʷ�����н������  ״̬ ��
		for (int i = 0; i < newvos.length; i++) {
			WaClassItemVO waClassItemVO = newvos[i];
			waClassItemVO.setPk_wa_class(destVO.getPk_wa_class());
			waClassItemVO.setCyear(destVO.getCyear());
			waClassItemVO.setCperiod(destVO.getCperiod());
			waClassItemVO.setPk_wa_classitem(null);
			waClassItemVO.setStatus(VOStatus.NEW);

		}
		getMDPersistenceService().saveBillWithRealDelete(newvos);
		WaCacheUtils.synCache(newvos[0].getTableName());
		return true;

		//����

	}

	@Override
	public WaClassItemVO[] queryByCondition(LoginContext context, String strFromCond,String strWhereCond,String strOrderCond)
			throws BusinessException {
		return getClassitemDAO().queryByCondition(context, strFromCond, strWhereCond, strOrderCond);
	}

	@Override
	public WaClassItemVO[] queryItemWithAccount(String pk_org,
			String pk_wa_class, String year, String period)
					throws BusinessException {
		return getClassitemDAO().queryItemWithAccount(pk_org, pk_wa_class, year, period);
	}

	//2014/05/23 shenliangcΪ���н�ʷ��Žڵ���ʾ���öԻ�������Ŀ�����뱾�ڼ䷢����Ŀ���Ʋ�ͬ��������޸ġ�
	//���Ʋ�ͬ����ԭ���ǲ�ѯ��Ŀ�����߼�û���������ڼ����ƣ�ȫ��ȡ������ʼ�ڼ�ķ�����Ŀ���ơ�
	@Override
	public WaClassItemVO[] queryItemsByPK_wa_class(String pk_wa_class, String cyear, String cperiod) throws BusinessException
	{

		return this.getClassitemDAO().queryItemsByPK_wa_class(pk_wa_class, cyear, cperiod);
	}

	public WaClassItemVO queryItemWithItemkeyAndPK_wa_class(String itemkey, String pk_wa_class, String cyear, String cperiod) throws BusinessException
	{
		return null;
	}
	//	public WaClassItemVO[] batchAddGroupClassItem(WaLoginContext context , String[] pk_wa_items) throws BusinessException{
	////		for (int i = 0; i < pk_wa_items.length; i++) {
	////		    insertGroupClassItem(vo)
	////		}
	//	}
	
	
	//shenliangc 20140823 ���������ڼ���н�ʷ�����Ŀ�����Ѵ���
	public boolean itemNameIsExist(String pk_wa_class, String cyear, String cperiod, WaClassItemVO vo) throws BusinessException{
		return this.getClassitemDAO().itemNameIsExist(pk_wa_class, cyear, cperiod, vo);
	}

}
