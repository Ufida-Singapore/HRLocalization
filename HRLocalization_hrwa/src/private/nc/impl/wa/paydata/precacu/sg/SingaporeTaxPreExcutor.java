package nc.impl.wa.paydata.precacu.sg;

import nc.impl.wa.paydata.caculate.AbstractFormulaExecutor;
import nc.vo.pub.BusinessException;
import nc.vo.wa.pub.WaLoginContext;

public class SingaporeTaxPreExcutor extends AbstractFormulaExecutor {
	
	public void excute(Object inTaxFormulaVO, WaLoginContext context) throws BusinessException {
		if (inTaxFormulaVO instanceof SingaporeFormulaVO) {
			// ���ݿ�˰��Ϣ���м��
			SingaporeFormulaVO taxFormulaVO = (SingaporeFormulaVO) inTaxFormulaVO;
			ISingaporeTaxInfPreProcess  taxInfPreProcess = this.createSingaporeTaxInfPreProcess(taxFormulaVO);
			taxInfPreProcess.transferTaxCacuData(taxFormulaVO, context);
		}
	}

	
	/**
	 * ��ͨ���Ż����������ͷ���
	 */
	public ISingaporeTaxInfPreProcess createSingaporeTaxInfPreProcess(SingaporeFormulaVO taxFormulaVO) {
		
		return new SG_TaxInfPreProcess();
	}
}
