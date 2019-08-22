package nc.impl.wa.item;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nc.bs.businessevent.BusinessEvent;
import nc.bs.businessevent.EventDispatcher;
import nc.bs.businessevent.IEventType;
import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.logging.Logger;
import nc.hr.frame.persistence.HrBatchService;
import nc.hr.utils.ResHelper;
import nc.impl.wa.expandtable.ExpandTableDAO;
import nc.itf.hr.wa.ISeaLocalItemManageService;
import nc.jdbc.framework.JdbcPersistenceManager;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.pub.templet.converter.util.helper.ExceptionUtils;
import nc.vo.hr.datatable.FreefldVO;
import nc.vo.hr.itemsource.TypeEnumVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pubapp.pattern.pub.SqlBuilder;
import nc.vo.util.BDPKLockUtil;
import nc.vo.wa.item.SeaLocalCommonItemVO;
import nc.vo.wa.item.WaItemVO;
import nc.vo.wa.paydata.DataVO;
import nc.vo.wa.pub.HRWACommonConstants;
import nc.vo.wa.repay.ReDataVO;
import nc.vo.wabm.util.WaCacheUtils;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author weiningc
 *
 */
public class SeaLocaltemServiceImpl implements ISeaLocalItemManageService  {
	
	private BaseDAO dao;
	
	public SeaLocaltemServiceImpl() {
		super();
		if(dao == null) {
			dao = new BaseDAO();
		}
	}

	@Override
	public void saveBactchItemForSeaLocal(WaItemVO vo, String countryitem) throws BusinessException {
		//0.У���Ƿ���Ҫ���뺣�⹫��н����Ŀ.
		if(!validateIsNeedInertSeaItem(countryitem)) {
			throw new BusinessException("Already exist sea local common salary item, can't create again.");
		}
		//1.��ѯӳ��VO
		Collection<SeaLocalCommonItemVO> sealocals = dao.
				retrieveByClause(SeaLocalCommonItemVO.class, " 1=1 and item_code like '" + countryitem +"%'", "itemkey asc");
		//2.�������Ĺ���н����Ŀ
		WaItemVO[] batchvos = this.constructBatchWaItemVOForInsert(sealocals, vo);
		HrBatchService service = new HrBatchService("Waitem");
		WaItemVO[] vos = service.insert(batchvos);
		//3.��������
		EventDispatcher.fireEvent(new BusinessEvent(HRWACommonConstants.WAITEMSOURCEID, IEventType.TYPE_INSERT_AFTER, vos));
		//4.��������
		//��չwa_data
		this.expansionTable(sealocals);
		//5.���»���
		WaCacheUtils.synCache(vo.getTableName());
		
		
	}
	
	/**
	 * ������չwa_data������hr_freefld���£���Ӧ�Ĵ�ӡģ�壬hrwa���ṩ�Ľӿڲ�����Ŀǰ��ʹ�������� �ʵ���д
	 * @param sealocals
	 * @throws Exception 
	 */
	private void expansionTable(Collection<SeaLocalCommonItemVO> sealocals){
		try{
			ExpandTableDAO dmo = new ExpandTableDAO();
			int beginitem = 0;
			int enditem = 0;
			List<SeaLocalCommonItemVO> seavos = new ArrayList<SeaLocalCommonItemVO>(sealocals);
			SeaLocalCommonItemVO sealocalvo = seavos.get(0);
			Integer seperator = sealocalvo.getItemkey().indexOf("_") + 1;
			beginitem = Integer.valueOf(sealocalvo.getItemkey().substring(seperator, sealocalvo.getItemkey().length()));
			enditem = Integer.valueOf(seavos.get(seavos.size() - 1).getItemkey().substring(seperator, sealocalvo.getItemkey().length()));
			
			FreefldVO freefld2  = dmo.getFreefldVO(0,sealocalvo.getItem_class());
			freefld2.setIaddcount(enditem - beginitem + 1);//count
			freefld2.setIproducttype(0);//0 ����wa
			freefld2.setIfieldtype(sealocalvo.getItem_class());//��������
			freefld2.setStartNO(beginitem);
			freefld2.setEndNO(enditem);
			
			//�����޸Ŀ��Ʊ�Ȼ�������չ
			freefld2.setLmaxno(enditem);			
			//hard code   1  ����  2 �ַ�  3  ���� 
			int result = dmo.updateFreeFldVO(freefld2);
			//���ؽ��1 ������ȷ�ĸ�����һ�м�¼
			if(result==1){
				//�޸ı�
				dmo.alterTable(freefld2);
				//�����ӡģ��
				dmo.insertPrintDataItem(freefld2);
				
				//���JdbcPersistenceManager�еı�ṹ����
				JdbcPersistenceManager.clearColumnTypes(DataVO.getDefaultTableName());
				JdbcPersistenceManager.clearColumnTypes(ReDataVO.getDefaultTableName());
			}
		} catch(Exception e) {
			Logger.error(e.getMessage(), e);
			ExceptionUtils.wrapException(e.getMessage(), e);
		}
	}
	
	
	/**
	 * 
	 * @param sealocals
	 * @param waitemvo 
	 * @param itemId 
	 * @return
	 */
	private WaItemVO[] constructBatchWaItemVOForInsert(Collection<SeaLocalCommonItemVO> sealocals, WaItemVO waitemvo) {
		List<WaItemVO> itemvoslist = new ArrayList<WaItemVO>();
		for(SeaLocalCommonItemVO vo : sealocals) {
			WaItemVO itemvo = new WaItemVO();
			
			//pk_org,pk_group, categoryid
			itemvo.setPk_group(waitemvo.getPk_group());
			itemvo.setPk_org(waitemvo.getPk_org());
			itemvo.setCategory_id(waitemvo.getCategory_id());
			//ӳ����е��ֶ�
			itemvo.setCode(vo.getItem_code());
			itemvo.setName(vo.getItem_name());
			itemvo.setName2(vo.getItem_name());
			itemvo.setIitemtype(vo.getItem_class());//��������
			itemvo.setIproperty(vo.getItem_type());//�������� 
			itemvo.setIfldwidth(vo.getItem_length());
			itemvo.setClearflag(vo.getItem_isclearnextmonth());
			//itemkey�ȵ� ToDo
			itemvo.setItemkey(vo.getItemkey());
			itemvo.setTotalitem(null);
			
			itemvoslist.add(itemvo);
			
		}
		return itemvoslist.toArray(new WaItemVO[0]);
	}

	/**
	 * �Ƿ���Ҫ���뺣�⹫��н����ĿУ��
	 * @param countryitem 
	 * @return
	 */
	private Boolean validateIsNeedInertSeaItem(String countryitem) {
		SqlBuilder sb = new SqlBuilder();
		sb.append("select 1 from wa_item where itemkey like ");
		sb.append("'");
		sb.append(countryitem + "%");
		sb.append("'");
		try {
			Object result = dao.executeQuery(sb.toString(), new ColumnProcessor());
			if(result != null) {
				return false;
			}
		} catch (DAOException e) {
			ExceptionUtils.wrapException(e.getMessage(), e);
		}
		return true;
	}

	@SuppressWarnings("serial")
	private Integer requestItemId(WaItemVO itemvo) throws BusinessException
	{
		BDPKLockUtil.lockString("requestItemId");
		TypeEnumVO type = itemvo.getTypeEnumVO();
		// �Ǽ�����ӣ�������֯���
		String condition = getRequestItemIdCondition(itemvo.getPk_group(), itemvo.getPk_org());

		final int iType = type.value();
		/**
		 * max Ĭ��ֵȡ1 ����ȡ0
		 */
		String sql = "select isnull(max(cast(substring(itemkey,3,len(itemkey)) as integer))+1,1) maxid from wa_item " + " where iitemtype = ?   and " + condition + " having  isnull(max(cast(substring(itemkey,3,len(itemkey)) as integer))+1,1) <= (select lmaxno from "+FreefldVO.getDefaultTableName()+" where iproducttype = 0 and ifieldtype = ?)";
		BaseDAO dao = new BaseDAO();
		SQLParameter par = new SQLParameter();
		par.addParam(iType);
		par.addParam(iType);

		try
		{
			return (Integer) dao.executeQuery(sql, par, new ResultSetProcessor()
			{

				@Override
				public Object handleResultSet(ResultSet rs) throws SQLException
				{
					if (!rs.next())
					{
						return null;
					}
					// ȡ�ѱ�ռ�õ������
					Integer no = rs.getInt(1);
					//��ֵ����Ŀ��Ԥ��0-14����Ϊϵͳ��Ŀ
					if(iType==TypeEnumVO.FLOATTYPE.value() && no!=null  && no.intValue()<15){
						no = 15;
					}
					return no;
				}

			});
		}
		catch (DAOException e)
		{
			Logger.error(e);
			throw new BusinessRuntimeException(ResHelper.getString("60130glbitem","060130glbitem0036")/*@res "�����µĹ�����Ŀ�ֶ�ʧ��"*/);
		}
	}
	
	/**
	 * ��1�������ڲ������ص� ������֮������ص� ��������Ŀ-���ſ���ȷ��itemid ( pk_group = '����pk' or pk_org =
	 * 'GLOBLE00000000000000' �� ��2�� ��˾���������������ظ���ͬһ�������ڵĸ�����˾֮������ص���pk_org =
	 * '��˾pk' or pk_org = '����pk' or pk_org = 'GLOBLE00000000000000'��
	 */

	private String getRequestItemIdCondition(String pk_group, String pk_org)
	{
		if (IsGroupItem(pk_group, pk_org))
		{
			return "(" + WaItemVO.PK_GROUP + "='" + pk_group + "' or  " + WaItemVO.PK_ORG + " = 'GLOBLE00000000000000' )";
		}
		else
		{
			return "(" + WaItemVO.PK_ORG + "='" + pk_org + "' or " + WaItemVO.PK_ORG + "='" + pk_group + "' or  " + WaItemVO.PK_ORG + " = 'GLOBLE00000000000000' )";
		}

	}

	private boolean IsGroupItem(String pk_group, String pk_org) {
		return pk_group.equals(pk_org);
	}

}
