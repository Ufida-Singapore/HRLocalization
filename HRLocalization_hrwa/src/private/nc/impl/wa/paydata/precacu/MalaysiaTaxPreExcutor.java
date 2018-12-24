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

	
	int convMode = 0;
	public void excute(Object inTaxFormulaVO, WaLoginContext context) throws BusinessException {
		if (inTaxFormulaVO instanceof MalaysiaTaxFormulaVO) {
			// ���ݿ�˰��Ϣ���м��
			MalaysiaTaxFormulaVO taxFormulaVO = (MalaysiaTaxFormulaVO) inTaxFormulaVO;
			IMalaysiaPCBTaxInfPreProcess  taxInfPreProcess = this.createMYTaxInfPreProcess(taxFormulaVO.getClass_wagetype());
			taxInfPreProcess.transferTaxCacuData(taxFormulaVO, context);
		}
	}

	
	/**
	 * ����ģʽ��0 Ԫ���� �� ���� �� Ŀ�ı���
	 *          1 Ԫ���� / ���� �� Ŀ�ı���
	 * @param src_currency_pk
	 * @param dest_currency_pk
	 * @return
	 * @throws BusinessException 
	 */
	public  void setCurrenyConvmode(WaLoginContext context) throws BusinessException{
		
		String src_currency_pk = context.getWaLoginVO().getCurrid();
		String dest_currency_pk = context.getWaLoginVO().getTaxcurrid();		
		if(src_currency_pk.equals(dest_currency_pk)){
			convMode = 0;
			return ;
		}
		CurrencyRateUtil currencyRateUtil = CurrencyRateUtil.getInstanceByOrg(context.getPk_org());
		CurrinfoVO currinfoVO  = currencyRateUtil.getCurrinfoVO(src_currency_pk, dest_currency_pk);		
		convMode =  currinfoVO.getConvmode();
	}
	
	public int getConvMode() {
		return convMode;
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

	private void currencyrateBeforeCaculate(String pk_wa_class,String userid ) throws BusinessException{
		
		
        //ע��ת��ģʽ��
		int convmode = getConvMode();
		String convSign =null;
		if(convmode==1){
			convSign = "/";
		}else{
			convSign = "*";
		}
		
		StringBuilder sbd = new StringBuilder();
		//20150917 shenliangc ����Ĵ��̲�DB2�ֶγ������С��λ��ʧ���⡣NCdp205497893������������ֶ�Ҫǿת��������cast(tax_base as double)
		if(DBConsts.DB2 == DataSourceCenter.getInstance().getDatabaseType()){
			sbd.append("  update  wa_cacu_data set  tax_base = cast(tax_base as double)"+ convSign+"currencyrate, ");
			sbd.append(" taxed = cast(taxed as double)"+ convSign+"currencyrate, ");
			sbd.append("  taxedBase = cast(taxedBase as double)"+ convSign+"currencyrate, ");
			sbd.append(" retaxed = cast(retaxed as double)"+ convSign+"currencyrate, ");
			sbd.append(" redata = cast(redata as double)"+ convSign+"currencyrate, ");
			sbd.append(" redataLasttaxBase = cast(redataLasttaxBase as double)"+ convSign+"currencyrate, ");
			sbd.append(" redataLasttax = cast(redataLasttax as double)"+ convSign+"currencyrate ");
			sbd.append(" where pk_wa_class = '"+ pk_wa_class+"' and creator = '" + userid+ "' ");
		}else{
			sbd.append("  update  wa_cacu_data set  tax_base = tax_base"+ convSign+"currencyrate, ");
			sbd.append(" taxed = taxed"+ convSign+"currencyrate, ");
			sbd.append("  taxedBase = taxedBase"+ convSign+"currencyrate, ");
			sbd.append(" retaxed = retaxed"+ convSign+"currencyrate, ");
			sbd.append(" redata = redata"+ convSign+"currencyrate, ");
			sbd.append(" redataLasttaxBase = redataLasttaxBase"+ convSign+"currencyrate, ");
			sbd.append(" redataLasttax = redataLasttax"+ convSign+"currencyrate ");
			sbd.append(" where pk_wa_class = '"+ pk_wa_class+"' and creator = '" + userid+ "' ");
		}

		executeSQLs(sbd.toString());
	}


     private void currencyrateAfterCaculate(String pk_wa_class,String userid ) throws BusinessException{

    	 
    	//ע��ת��ģʽ��
 		int convmode = getConvMode();
 		String convSign =null;
 		if(convmode==1){
 			convSign = "*";
 		}else{
 			convSign = "/";
 		}
 		
 		
		StringBuilder sbd = new StringBuilder();
		//20150917 shenliangc ����Ĵ��̲�DB2�ֶγ������С��λ��ʧ���⡣NCdp205497893������������ֶ�Ҫǿת��������cast(tax_base as double)
		if(DBConsts.DB2 == DataSourceCenter.getInstance().getDatabaseType()){
			sbd.append("  update  wa_cacu_data set  tax_base = cast(tax_base as double)"+convSign+"currencyrate, ");
			sbd.append(" taxed = cast(taxed as double)"+convSign+"currencyrate, ");
			sbd.append("  taxedBase = cast(taxedBase as double)"+convSign+"currencyrate, ");
			sbd.append(" retaxed = cast(retaxed as double)"+convSign+"currencyrate, ");
			sbd.append(" redata = cast(redata as double)"+convSign+"currencyrate, ");
			sbd.append(" redataLasttaxBase = cast(redataLasttaxBase as double)"+convSign+"currencyrate, ");
			sbd.append(" redataLasttax =cast(redataLasttax as double)"+convSign+"currencyrate, ");
			//��˰Ҫ�������
			sbd.append(" cacu_value = cast(cacu_value as double)"+convSign+"currencyrate ");
			sbd.append(" where pk_wa_class = '"+ pk_wa_class+"' and creator = '" + userid+ "' ");
		}else{
			sbd.append("  update  wa_cacu_data set  tax_base = tax_base"+convSign+"currencyrate, ");
			sbd.append(" taxed = taxed"+convSign+"currencyrate, ");
			sbd.append("  taxedBase = taxedBase"+convSign+"currencyrate, ");
			sbd.append(" retaxed = retaxed"+convSign+"currencyrate, ");
			sbd.append(" redata = redata"+convSign+"currencyrate, ");
			sbd.append(" redataLasttaxBase = redataLasttaxBase"+convSign+"currencyrate, ");
			sbd.append(" redataLasttax = redataLasttax"+convSign+"currencyrate, ");
			//��˰Ҫ�������
			sbd.append(" cacu_value = cacu_value"+convSign+"currencyrate ");
			sbd.append(" where pk_wa_class = '"+ pk_wa_class+"' and creator = '" + userid+ "' ");
		}

		executeSQLs(sbd.toString());
	}


	/**
	 * ���ݸ���˰�ʱ�Ĳ�ͬ�Ϳ�˰��ʽ�Ĳ�ͬ��������ͬ�Ŀ�˰������
	 * Ӧ��˰={((Ӧ��˰���ö��˰��-����۳���) * ( 1- ��˰����))-�ѿ�˰}/����
	 *
	 *
	 * �������˰�ͻ���
	 *
	 * ����cacu_value ʱ ע�⾫������Ϊ��ʽ��
	 *
	 * @author zhangg on 2010-6-4
	 * @param context
	 * @throws BusinessException
	 */
		private StringBuffer getUpdateCurrencyRate(String pk_wa_class,String userid,WaClassItemVO taxItemvo) throws BusinessException{
			StringBuffer sqlBuffer = new StringBuffer();
			sqlBuffer.append("update wa_cacu_data ");                  //   1

			if(taxItemvo!=null){
				sqlBuffer.append("   set cacu_value = " +getRoundTaxSQL(taxItemvo) );  //   2
			}else{
				sqlBuffer.append("   set cacu_value = " +getTaxSQL() );  //   2
			}

			sqlBuffer.append("  where wa_cacu_data.pk_wa_class = '" + pk_wa_class + "' ");
			sqlBuffer.append("         and wa_cacu_data.creator = '" +userid + "'");
			return sqlBuffer;
		}


	    private String 	getTaxSQL(){
			return "( "+ getThisTimeTaxSQL() +"-taxed ) ";
		}

	  private String   getThisTimeTaxSQL(){
		return " (( taxable_income  * taxrate * 0.01  - nquickdebuct) * ((100 - derateptg) / 100))";
	    }
		private String getRoundTaxSQL(WaClassItemVO itemVO) throws BusinessException {
			   String sql = "";

				int digits = itemVO.getIflddecimal();
				/**
				 * �������룬 ��λ�� ��λ н���ṩ�Ľ��з�ʽͬ��ͨ�����ϵĽ�λ��ͬ�� �������£�
				 *
				 * ��С��λ�������ӽ�λ��ʽ����,����ѡ��,ϵͳ�ṩ��λ ��λ����������������λ��ʽ
				 * ��λ�������û��趨��С��λ��,����������С��λ���ĵĺ�һλ������0ʱС��λ�������һλ��1
				 * ��λ�������û��趨��С��λ��,���ۼ�������С��λ���ĺ�һλ�Ƿ����0,��ֱ������
				 * ��������,�����û��趨��С��λ��,��������С��λ���ĺ�һλ������������Ĺ�����н�λ����λ����
				 */

				if (itemVO.getRound_type() == null || itemVO.getRound_type().intValue() == 0) {// ��������
					 sql = "  ( round("+ getThisTimeTaxSQL() + ", " + digits + " ) - taxed)  " ;
				} else {
					//���ο�˰�϶���>=0
					UFDouble f = UFDouble.ZERO_DBL;
					if (itemVO.getRound_type().intValue() == 1) {// ��λ
						f = new UFDouble(0.4f);
					} else if (itemVO.getRound_type().intValue() == 2) {// ��λ����
						f = new UFDouble(-0.5f);
					} else {// Ĭ����������
						f = UFDouble.ZERO_DBL;
					}

					f = f.multiply(Math.pow(0.1, digits));

					 sql = "  ( round("+ getThisTimeTaxSQL() +"+("+ f + "), " + digits + " ) - taxed)   " ;
				}

				return sql;

		}

	/**
	 *
	 * @author zhangg on 2010-4-21
	 * @param condition
	 * @return
	 * @throws BusinessException
	 */
	private PsnTaxTypeVO[] getPsnTaxTypeVOs(String pk_wa_class,String userid) throws BusinessException {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select distinct wa_cacu_data.taxtype, "); // 1
		sqlBuffer.append("                wa_cacu_data.taxtableid, "); // 2
		sqlBuffer.append("                wa_cacu_data.isndebuct ,wa_cacu_data.isderate ,wa_cacu_data.derateptg "); // 3
		sqlBuffer.append("  from wa_cacu_data ");
		sqlBuffer.append("  where wa_cacu_data.pk_wa_class = '" + pk_wa_class + "' ");
		sqlBuffer.append("         and wa_cacu_data.creator = '" +userid + "'  and  wa_cacu_data.taxtableid <> '~' ");

		PsnTaxTypeVO[] psnTaxTypeVOs = executeQueryVOs(sqlBuffer.toString(), PsnTaxTypeVO.class);
		TaxQueryServiceImpl taxQueryServiceImpl = new TaxQueryServiceImpl();
		for (PsnTaxTypeVO psnTaxTypeVO : psnTaxTypeVOs) {
			psnTaxTypeVO.setTaxBaseVO(taxQueryServiceImpl.queryByPk(psnTaxTypeVO.getTaxtableid()));
		}
		return psnTaxTypeVOs;
	}


	/**
	 * ����˰�ʱ�Ĳ�ͬ�Ϳ�˰��ʽ�Ĳ�ͬ��������ͬ�Ŀ�˰������
	 *
	 * @author zhangg on 2010-4-9
	 * @param psnTaxTypeVO
	 * @return ITaxRateProcess
	 */
	private ITaxRateProcess getRateProcess(PsnTaxTypeVO psnTaxTypeVO) {
		ITaxRateProcess taxRateProcess = null;

		if (psnTaxTypeVO.getTaxBaseVO().getParentVO().getItbltype().equals(TaxTableTypeEnum.FIXTAX.value())) {// �̶�˰�ʱ�

			if (psnTaxTypeVO.getTaxtype().equals(Taxtype.WITHHOLDING.value())) {// ����˰
				taxRateProcess = new FixTaxRateWithholding();

			} else if (psnTaxTypeVO.getTaxtype().equals(Taxtype.REMITTING.value())) {// ����˰
				taxRateProcess = new FixTaxRateRemitting();
			}
		} else if (psnTaxTypeVO.getTaxBaseVO().getParentVO().getItbltype().equals(TaxTableTypeEnum.TAXTABLE.value())) {// �䶯˰�ʱ�
			if (psnTaxTypeVO.getTaxtype().equals(Taxtype.WITHHOLDING.value())) {// ����˰
				taxRateProcess = new TaxTableTaxRateWithHolding();
			} else if (psnTaxTypeVO.getTaxtype().equals(Taxtype.REMITTING.value())) {// ����˰
				taxRateProcess = new TaxTableTaxRateRemitting();
			}
		}else if(psnTaxTypeVO.getTaxBaseVO().getParentVO().getItbltype().equals(TaxTableTypeEnum.WORKTAX.value())){
			//����˰�ʱ�
			if (psnTaxTypeVO.getTaxtype().equals(Taxtype.WITHHOLDING.value())) {// ����˰
				taxRateProcess = new WorkTaxRateWithholding();
			} else if (psnTaxTypeVO.getTaxtype().equals(Taxtype.REMITTING.value())) {// ����˰
				taxRateProcess = new WorkTaxRateRemitting();
			}
		}
		
		return taxRateProcess;
	}
	
	/**
	 * ��ͨ���Ż����������ͷ���
	 */
	public IMalaysiaPCBTaxInfPreProcess createMYTaxInfPreProcess(String type) {
		if(MalaysiaTaxFormulaVO.CLASS_TYPE_NORMAL.equals(type)){
			//��ͨ��˰
			return new MY_NormalPCBTaxInfPreProcess();
		}else if (MalaysiaTaxFormulaVO.CLASS_TYPE_YEAR.equals(type)){
			//���ս�
			return new MY_AwardPCBTaxInfPreProcess();
		}else{
			//������˰
			return new MY_MutiPCBTaxInfPreProcess();
		}
	}
}