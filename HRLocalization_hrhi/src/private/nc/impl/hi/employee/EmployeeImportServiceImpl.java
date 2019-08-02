package nc.impl.hi.employee;

import java.util.ArrayList;
import java.util.List;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.itf.hi.IPsndocQryService;
import nc.itf.hi.IPsndocService;
import nc.itf.hi.employee.IEmployeeImportService;
import nc.md.model.MetaDataException;
import nc.md.persist.framework.IMDPersistenceService;
import nc.md.persist.framework.MDPersistenceService;
import nc.pub.hi.employeeimport.vo.EmployeeFormatVO;
import nc.pub.templet.converter.util.helper.ExceptionUtils;
import nc.util.fi.pub.SqlUtils;
import nc.vo.hi.psndoc.PsndocAggVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.ic.pub.util.CollectionUtils;
import nc.vo.pp.util.StringUtils;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pubapp.pattern.pub.SqlBuilder;

public class EmployeeImportServiceImpl implements IEmployeeImportService {

	private BaseDAO dao = null;

	public EmployeeImportServiceImpl() {
		if (dao == null) {
			dao = new BaseDAO();
		}
	}

	@Override
	public List<EmployeeFormatVO> queryEmployeeFormatVO(String[] columnNames)
			throws BusinessException {
		List<EmployeeFormatVO> results = null;
		SqlBuilder sb = new SqlBuilder();
		sb.append(" deflistcode", EmployeeFormatVO.DEFDOCLISTCODE);
		sb.append(" and defname", columnNames);
		try {
			results = (List<EmployeeFormatVO>) dao.retrieveByClause(
					EmployeeFormatVO.class, sb.toString());
		} catch (DAOException e) {
			ExceptionUtils.wrapException(
					"query employee failed! please contact consultant.", e);
		}
		return results;
	}

	@Override
	public int saveImportVOs(PsndocAggVO[] aggvos) throws BusinessException {
		int num = 0;
		if (aggvos == null || aggvos.length < 1) {
			return num;
		}
		IPsndocService service = NCLocator.getInstance().lookup(
				IPsndocService.class);
		for (PsndocAggVO agg : aggvos) {
			service.savePsndocForImport(agg);
			num++;
		}
		return num;
	}

	@Override
	public int updateImportVOs(SuperVO[] updateaggvos) throws MetaDataException {
		int num = 0;
		IMDPersistenceService service = MDPersistenceService
				.lookupPersistenceService();
		//���µ��ֶ�
		String[] attributeNames = updateaggvos[0].getAttributeNames();
		List<String> fields = new ArrayList<String>();
		for(String attr : attributeNames) {
			if(PsndocVO.PK_PSNDOC.equals(attr) || PsndocVO.CODE.equals(attr) || PsndocVO.PK_ORG.equals(attr) 
					|| PsndocVO.PK_GROUP.equals(attr)) {
				continue;
			}
			if(updateaggvos[0].getAttributeValue(attr) != null) {
				fields.add(attr);
			}
		}
		//��������
		if(fields.size() < 0) {
			return 0;
		}
		if(fields.size() > 0 && updateaggvos[0] instanceof PsndocVO) {
			try {
				new BaseDAO().updateVOArray(updateaggvos, fields.toArray(new String[0]));
			} catch (DAOException e) {
				ExceptionUtils.wrapException(e);
			}
//			service.updateBillWithAttrs(updateaggvos, fields.toArray(new String[0]));
			num = updateaggvos.length;
		} else {
			//�Ӽ�Ϊ��������
			//����:�Ӽ�����ʱ����Ҫ����ʷ�Ӽ���Ϣ�����±�־�޸�ΪN����ǰ�����Ӽ�ΪY
			SuperVO[] vos = this.fillValueForUpdate(updateaggvos);
			service.saveBill(vos);
			//���ӱ�仯ʱ����Ӧ���޸���������
			List<String> pks = new ArrayList<String>();
			for(SuperVO vo : vos) {
				pks.add((String)vo.getAttributeValue("pk_psndoc"));
			}
			IPsndocService lookup = NCLocator.getInstance().lookup(IPsndocService.class);
			try {
				lookup.updateDataAfterSubDataChanged(vos[0].getTableName(), pks.toArray(new String[0]));
			} catch (BusinessException e) {
				ExceptionUtils.wrapException(e);
			}
			num = updateaggvos.length;
		}
		return num;
	}

	private SuperVO[] fillValueForUpdate(SuperVO[] updateaggvos) {
		IPsndocQryService qryservice = NCLocator.getInstance().lookup(IPsndocQryService.class);
		StringBuffer sb = new StringBuffer();
		List<String> pks = new ArrayList<String>();
		for(SuperVO vo : updateaggvos) {
			pks.add((String) vo.getAttributeValue("pk_psndoc"));
		}
		try {
			sb.append(SqlUtils.getInStr("pk_psndoc", pks));
			SuperVO[] supervos= qryservice.querySubVO(updateaggvos[0].getClass(), sb.toString(), null);
			//�޸����±�־ΪN
			if(supervos == null || supervos.length == 0) {
				return updateaggvos;
			}
			for(SuperVO vo : supervos) {
				if(vo.getAttributeValue("lastflag") != null && UFBoolean.TRUE.equals((UFBoolean) vo.getAttributeValue("lastflag"))) {
					vo.setAttributeValue("lastflag", UFBoolean.FALSE);
					vo.setStatus(VOStatus.UPDATED);
				}
			}
			//�ϲ�SuperVO
			return CollectionUtils.combineArrs(updateaggvos, supervos);
		} catch (BusinessException e) {
			ExceptionUtils.wrapException(e);
		}
		return null;
	}

}
