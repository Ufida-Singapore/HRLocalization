package nc.impl.wa.item;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import nc.jdbc.framework.processor.MapProcessor;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.lic.LicenseControl;
import nc.lic.keygen.LicKeyGenerator;
import nc.lic.model.LicData;
import nc.lic.model.LicKey;
import nc.lic.model.License;
import nc.lic.model.LicenseContainer;
import nc.lic.model.LicenseRequest;
import nc.lic.toolkit.Encode;
import nc.lic.toolkit.LicToolKit;
import nc.lic.toolkit.LicenseMaker;
import nc.pub.templet.converter.util.helper.ExceptionUtils;
import nc.util.fi.pub.SqlUtils;
import nc.vo.hr.datatable.FreefldVO;
import nc.vo.hr.itemsource.TypeEnumVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pubapp.pattern.pub.SqlBuilder;
import nc.vo.util.BDPKLockUtil;
import nc.vo.wa.item.LocalLicenseUtil;
import nc.vo.wa.item.SeaLocalCommonItemVO;
import nc.vo.wa.item.WaItemVO;
import nc.vo.wa.paydata.DataVO;
import nc.vo.wa.pub.HRWACommonConstants;
import nc.vo.wa.repay.ReDataVO;
import nc.vo.wabm.util.WaCacheUtils;
import sun.misc.BASE64Decoder;

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
	public void saveBactchItemForSeaLocal(WaItemVO vo, String country) throws BusinessException {
		//0 check license
		LocalLicenseUtil.checkLocalLicense(country);
		      
		//1.查询映射VO
		SqlBuilder sb = new SqlBuilder();
		sb.append(" 1=1 and country = '" + country +"'");
		sb.append(" and itemkey not in(select wi.itemkey from wa_item wi)");
		Collection<SeaLocalCommonItemVO> sealocals = dao.
				retrieveByClause(SeaLocalCommonItemVO.class, sb.toString(), "itemkey asc");
		//2.构造插入的公共薪资项目
		WaItemVO[] batchvos = this.constructBatchWaItemVOForInsert(sealocals, vo);
		//0.校验是否需要插入海外公共薪资项目.
		if(!validateIsNeedInertSeaItem(batchvos)) {
			throw new BusinessException("Already exist sea local common salary item, can't create");
		}
		HrBatchService service = new HrBatchService("Waitem");
		WaItemVO[] vos = service.insert(batchvos);
		//3.关联表处理
		EventDispatcher.fireEvent(new BusinessEvent(HRWACommonConstants.WAITEMSOURCEID, IEventType.TYPE_INSERT_AFTER, vos));
		//4.批量插入
		//扩展wa_data
		this.expansionTable(sealocals);
		//5.更新缓存
		WaCacheUtils.synCache(vo.getTableName());
		
		
	}
	
	/**
	 * 除了扩展wa_data，还有hr_freefld更新，相应的打印模板，hrwa已提供的接口不满足目前的使用条件， 故单独写
	 * TODO 这里目前只支持了f_xxx的类型，即数字类型的扩展
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
			freefld2.setIproducttype(0);//0 代表数字型
			freefld2.setIfieldtype(sealocalvo.getItem_class());//数据类型 0表示f_xxx类型
			freefld2.setStartNO(beginitem);
			freefld2.setEndNO(enditem);
			
			//首先修改控制表，然后进行扩展
			freefld2.setLmaxno(enditem);			
			//hard code   1  数字  2 字符  3  日期 
			//场景: 海外本地化的字段从1000开始，为了本地化报表和薪资项目统一,故不更新该字段 add by weiningc 20200227 start
//			int result = dmo.updateFreeFldVO(freefld2);
			//end
			//返回结果1 代表正确的更新了一行记录
			//修改表
			dmo.alterTable(freefld2);
			//插入打印模板
			dmo.insertPrintDataItem(freefld2);
			
			//清空JdbcPersistenceManager中的表结构缓存
			JdbcPersistenceManager.clearColumnTypes(DataVO.getDefaultTableName());
			JdbcPersistenceManager.clearColumnTypes(ReDataVO.getDefaultTableName());
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
		//去重  以防自定义项的薪资项目重了, 如果真的重复薪资项目进入database删除起来很麻烦
		Set<String> existitemkeys = new HashSet<String>();
		for(SeaLocalCommonItemVO vo : sealocals) {
			if(existitemkeys.contains(vo.getItemkey())) {
				continue;
			}
			WaItemVO itemvo = new WaItemVO();
			
			//pk_org,pk_group, categoryid
			itemvo.setPk_group(waitemvo.getPk_group());
			itemvo.setPk_org(waitemvo.getPk_org());
			itemvo.setCategory_id(waitemvo.getCategory_id());
			//映射表中的字段
			itemvo.setCode(vo.getItem_code());
			itemvo.setName(vo.getItem_name());
			itemvo.setName2(vo.getItem_name2());
			itemvo.setIitemtype(vo.getItem_class());//数据类型
			itemvo.setIproperty(vo.getItem_type());//增减属性 
			itemvo.setIfldwidth(vo.getItem_length());
			itemvo.setClearflag(vo.getItem_isclearnextmonth());
			//itemkey等等 ToDo
			itemvo.setItemkey(vo.getItemkey());
			itemvo.setTotalitem(null);
			//是否公式
			if(vo.getIsformula() != null && vo.getIsformula().booleanValue()) {
				itemvo.setIfromflag(Integer.valueOf(0));//由公式计算
				itemvo.setVformula(vo.getVformula());
				itemvo.setVformulastr(vo.getVformulastr());
			}
			
			itemvoslist.add(itemvo);
			existitemkeys.add(vo.getItemkey());
			
		}
		return itemvoslist.toArray(new WaItemVO[0]);
	}

	/**
	 * 是否需要插入海外公共薪资项目校验
	 * @param batchvos 
	 * @return
	 */
	private Boolean validateIsNeedInertSeaItem(WaItemVO[] batchvos) {
		SqlBuilder sb = new SqlBuilder();
		List<String> itemkeys = new ArrayList<String>();
		List<String> codes = new ArrayList<String>();
		for(WaItemVO vo: batchvos) {
			itemkeys.add(vo.getItemkey());
			codes.add(vo.getCode());
		}
		sb.append("select code, itemkey from wa_item where 1=1");
		try {
			sb.append(SqlUtils.getInStr(" and code", codes));
			sb.append(SqlUtils.getInStr(" or itemkey", itemkeys));
			Map<String, String> results = (Map<String, String>) dao.executeQuery(sb.toString(), new MapProcessor());
			if(results != null && results.size() > 0) {
				return false;
			}
		} catch (BusinessException e) {
			throw new BusinessRuntimeException(e.getMessage());
		}
		return true;
	}

	@SuppressWarnings("serial")
	private Integer requestItemId(WaItemVO itemvo) throws BusinessException
	{
		BDPKLockUtil.lockString("requestItemId");
		TypeEnumVO type = itemvo.getTypeEnumVO();
		// 是集团添加，还是组织添加
		String condition = getRequestItemIdCondition(itemvo.getPk_group(), itemvo.getPk_org());

		final int iType = type.value();
		/**
		 * max 默认值取1 不能取0
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
					// 取已被占用的最大数
					Integer no = rs.getInt(1);
					//数值型项目，预留0-14个最为系统项目
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
			throw new BusinessRuntimeException(ResHelper.getString("60130glbitem","060130glbitem0036")/*@res "请求新的公共项目字段失败"*/);
		}
	}
	
	/**
	 * （1）集团内部不能重叠 ，集团之间可以重叠 （公共项目-集团可以确定itemid ( pk_group = '集团pk' or pk_org =
	 * 'GLOBLE00000000000000' ） （2） 公司不能与上属集团重复，同一个集团内的各个公司之间可以重叠（pk_org =
	 * '公司pk' or pk_org = '集团pk' or pk_org = 'GLOBLE00000000000000'）
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
