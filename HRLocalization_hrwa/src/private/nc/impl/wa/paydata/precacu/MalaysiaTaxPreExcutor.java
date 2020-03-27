package nc.impl.wa.paydata.precacu;

import nc.impl.wa.paydata.caculate.AbstractFormulaExecutor;
import nc.impl.wa.paydata.tax.FixTaxRateRemitting;
import nc.impl.wa.paydata.tax.FixTaxRateWithholding;
import nc.impl.wa.paydata.tax.IMalaysiaPCBTaxInfPreProcess;
import nc.impl.wa.paydata.tax.ITaxInfPreProcess;
import nc.impl.wa.paydata.tax.ITaxRateProcess;
import nc.impl.wa.paydata.tax.TaxFormulaVO;
import nc.impl.wa.paydata.tax.TaxTableTaxRateRemitting;
import nc.impl.wa.paydata.tax.TaxTableTaxRateWithHolding;
import nc.impl.wa.paydata.tax.WorkTaxRateRemitting;
import nc.impl.wa.paydata.tax.WorkTaxRateWithholding;
import nc.impl.wa.taxrate.TaxQueryServiceImpl;
import nc.jdbc.framework.DataSourceCenter;
import nc.jdbc.framework.util.DBConsts;
import nc.pubitf.uapbd.CurrencyRateUtil;
import nc.vo.bd.currinfo.CurrinfoVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.paydata.PsnTaxTypeVO;
import nc.vo.wa.payfile.Taxtype;
import nc.vo.wa.pub.WaLoginContext;
import nc.vo.wa.pub.WaLoginVOHelper;
import nc.vo.wa.taxrate.TaxTableTypeEnum;

/**
 * PCB��������
 *
 * @author: zhangg
 * @date: 2010-4-21 ����01:12:26
 * @since: eHR V6.0
 * @�߲���:
 * @�߲�����:
 * @�޸���:
 * @�޸�����:
 */
public class MalaysiaTaxPreExcutor extends AbstractFormulaExecutor {
	
	public void excute(Object inTaxFormulaVO, WaLoginContext context) throws BusinessException {
		if (inTaxFormulaVO instanceof MalaysiaTaxFormulaVO) {
			// ���ݿ�˰��Ϣ���м��
			MalaysiaTaxFormulaVO taxFormulaVO = (MalaysiaTaxFormulaVO) inTaxFormulaVO;
			IMalaysiaPCBTaxInfPreProcess  taxInfPreProcess = this.createMYTaxInfPreProcess(taxFormulaVO.getClass_wagetype(), context);
			taxInfPreProcess.transferTaxCacuData(taxFormulaVO, context);
		}
	}


	private  String getParentPkClass(WaLoginContext context,TaxFormulaVO  taxFormulaVO ){
		//������ ��ͨн�ʼ��㲻һ�����������м���ʱ����Ҫʹ�ø�������PK
		String pk_wa_class = "";
		if(taxFormulaVO.getClass_type().equals(TaxFormulaVO.CLASS_TYPE_REDATA)){
			pk_wa_class  = WaLoginVOHelper.getParentClassPK(context.getWaLoginVO());
		}else{
			pk_wa_class = context.getPk_wa_class();
		}
		return pk_wa_class;

	}
	
	/**
	 * ��ͨ���Ż����������ͷ���
	 * @param context 
	 */
	public IMalaysiaPCBTaxInfPreProcess createMYTaxInfPreProcess(String type, WaLoginContext context) {
		//֧�ֶ�η��� ��Ҫ�ж��Ƿ��η��� ����� �߶�η��ŵ��߼�,PCB�������õ���н����Ŀȡ��ֵ
		Boolean ismutiple = false;
		if(context.getWaLoginVO().getBatch() != null && context.getWaLoginVO().getBatch() > 1) {
			ismutiple = true;
		}
		if(MalaysiaTaxFormulaVO.CLASS_TYPE_NORMAL.equals(type) && !ismutiple){
			//��ͨ��˰
			return new MY_NormalPCBTaxInfPreProcess();
		}else if (MalaysiaTaxFormulaVO.CLASS_TYPE_YEAR.equals(type)){
			//���ս�
			return new MY_AwardPCBTaxInfPreProcess();
		}else if(ismutiple){
			//��η���
			return new MY_MutiPCBTaxInfPreProcess();
		}
		return null;
	}
}