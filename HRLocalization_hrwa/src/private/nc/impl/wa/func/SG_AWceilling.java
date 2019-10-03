package nc.impl.wa.func;

import nc.impl.pubapp.pattern.database.DataAccessUtils;
import nc.vo.hr.func.FunctionReplaceVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.data.IRowSet;
import nc.vo.wa.pub.WaLoginContext;

/**
 * 
 * @author weiningc
 *
 */
public class SG_AWceilling extends AbstractPreExcutorFormulaParse {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6681306834481349471L;

	@Override
	public FunctionReplaceVO getReplaceStr(String formula)
			throws BusinessException {
		FunctionReplaceVO fvo = new FunctionReplaceVO();
		String[] arguments = getArguments(formula.toString());
		//OW �ֶ�
		String ow_item = arguments[0];
		//AW�ֶ�
		String aw_item = arguments[1];
		//CF OW�ֶ�  �����ѷ����ۼ�OW
		String cfow_item = arguments[2];
		//CF AW�ֶ�  �����ѷ����ۼ�AW
		String cfaw_item = arguments[3];
		//���ڼ���ow ceilling��aw�Ĺ̶�ֵ����Ϊaw��ceilling�Ǹ�����
		UFDouble[] owandaw_ceilling = this.getOwAndAwCeilling();
		String sql = this.getFormulaForAwCeillingSql(ow_item, aw_item, owandaw_ceilling, cfow_item, cfaw_item);
		fvo.setReplaceStr(sql);
		return fvo;
	}
	
	private UFDouble[] getOwAndAwCeilling() {
		
		DataAccessUtils util = new DataAccessUtils();
		UFDouble[] owandaw_ceilling = new UFDouble[2];
		StringBuffer sb = new StringBuffer(); 
		sb.append(" select s.ow_ceilling, s.aw_fixvalue from wa_sgcpf_rate s where rownum = 1");
		IRowSet result = util.query(sb.toString());
		if(result.next()) {
			owandaw_ceilling[0] = result.getUFDouble(0);
			owandaw_ceilling[1] = result.getUFDouble(1);
		}
		return owandaw_ceilling;
	}

	/**
	 * =IF(B14<6000,102000-((CFOW+OW) + OW*(12-CURRENTMONTH))- CFAW,102000-((CFOW+6000) + 6000*(12-CURRENTMONTH)))
	 * @param ow_item
	 * @param aw_item
	 * @param owandaw_ceilling 
	 * @param cfaw_item 
	 * @param cfow_item 
	 * @return
	 */
	private String getFormulaForAwCeillingSql(String ow_item, String aw_item, UFDouble[] owandaw_ceilling, String cfow_item, String cfaw_item) {
		//TODO  �������Ա����ְ  ��Ҫ����AWCeilling�� ���ǲ���Ҫ����δ��Ԥ���·ݵ������
		String cperiod = this.context.getCperiod();
		Integer n = 12 - Integer.valueOf(cperiod);
		StringBuffer sb = new StringBuffer();
		sb.append(" case when wa_data."  +  ow_item + "<" + owandaw_ceilling[0] );
		sb.append(" then " + owandaw_ceilling[1] + " -((wa_data." + cfow_item + " +wa_data." + ow_item + ")");
		sb.append(" + wa_data." + ow_item + " *" + n + ")");
		sb.append(" - wa_data." + cfaw_item);
		sb.append(" else " + owandaw_ceilling[1] + " -((wa_data." + cfow_item + "+" + owandaw_ceilling[0] + ") + ");
		sb.append(owandaw_ceilling[0] + " *" + n + ")");
		sb.append(" - wa_data." + cfaw_item + " end");
		return sb.toString();
	}


	@Override
	public void excute(Object arguments, WaLoginContext context)
			throws BusinessException {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * ȡ����ȱ�����ǰ�� period
	 * @param type 
	 * @param endperiod 
	 * @param begingperiod 
	 */
	private String[] getBeforCurrentYearPeriod() {
		
		String endperiod = this.getContext().getCperiod();
		//����һ�·ݣ� ��ֱ�ӷ���
		if("01".equals(endperiod)) {
			return null;
		}
		String[] arr_period = new String[2];
		String year = this.getContext().getCyear();
		arr_period[0] = year + "01";
		int end = Integer.valueOf(endperiod) - 1;
		if(end <= 9) {
			endperiod = 0 + String.valueOf(end);
		} else {
			endperiod = String.valueOf(end);
		}
		arr_period[1] = year + endperiod;
		return arr_period;
		
	}

}
