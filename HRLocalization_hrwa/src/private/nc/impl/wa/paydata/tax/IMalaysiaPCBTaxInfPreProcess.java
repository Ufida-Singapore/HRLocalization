package nc.impl.wa.paydata.tax;

import nc.bs.dao.DAOException;
import nc.impl.wa.paydata.precacu.MalaysiaTaxFormulaVO;
import nc.vo.pub.BusinessException;
import nc.vo.wa.pub.WaLoginContext;

/**
 * ����˰�������Ϣ�� cacu_value, taxtableid, tax_base, taxtype, isndebuct, isderate, derateptg�� ���͵��м��
 * Malaysia PCB����--��˰����
 * @author xuanlt
 *
 */
public interface IMalaysiaPCBTaxInfPreProcess {
	
	public abstract void transferTaxCacuData(MalaysiaTaxFormulaVO malaysiaFormulaVO,
			WaLoginContext context) throws DAOException, BusinessException;	

}
