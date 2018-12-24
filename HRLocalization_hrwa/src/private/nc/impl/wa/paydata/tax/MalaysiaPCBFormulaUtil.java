package nc.impl.wa.paydata.tax;

import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.impl.wa.paydata.precacu.MalaysiaTaxFormulaVO;
import nc.vo.hr.func.FunctionVO;
import nc.vo.pub.BusinessException;
import nc.vo.wa.classitem.WaClassItemVO;

/**
 *
 * @author: zhangg
 * @date: 2010-1-14 ����10:43:32
 * @since: eHR V6.0
 * @�߲���:
 * @�߲�����:
 * @�޸���:
 * @�޸�����:
 */
public class MalaysiaPCBFormulaUtil {
	/**
	 *
	 * @author zhangg on 2010-1-12
	 * @param taxFormula
	 * @return
	 * @throws BusinessException
	 * @throws ParseException
	 */
	public static MalaysiaTaxFormulaVO translate2FormulaVO(FunctionVO taxFunctionVO, String taxFormula) throws BusinessException {

		MessageFormat format = new MessageFormat(taxFunctionVO.getArguments());
		try {
			Object[] parts = format.parse(taxFormula);
			if (parts.length != 2) {
				throw new BusinessException(taxFormula + ResHelper.getString("6013salarypmt","06013salarypmt0271")/*@res "�еĲ����������߸�ʽ����ȷ!"*/);
			}
			//��ͨ���� 0����η��� 2�����𷢷� 1
			String class_type = parts[0].toString().trim();
			if (class_type != null) {
				if (class_type.equals(MalaysiaTaxFormulaVO.CLASS_TYPE_NORMAL) || class_type.equals(MalaysiaTaxFormulaVO.CLASS_TYPE_YEAR) ||
						class_type.equals(MalaysiaTaxFormulaVO.CLASS_TYPE_REDATA)) {
					Logger.info(taxFormula + "Class_type ����ȡֵ��ȷ!");
				} else {
					throw new BusinessException(taxFormula + ResHelper.getString("6013salarypmt","06013salarypmt0272")/*@res "Class_type ����ȡֵ����ȷ!, ӦΪ0  ���� 1"*/);
				}
			}
			MalaysiaTaxFormulaVO taxvo = new MalaysiaTaxFormulaVO();
			taxvo.setClass_wagetype(class_type);
			return taxvo;
		} catch (ParseException e) {
			throw new BusinessException("Pares PCB formula error, please check.");
		}
	}



	public static String getCheckTaxFormula(FunctionVO taxFunctionVO,WaClassItemVO itemVO) throws BusinessException {
		String taxFormula = TaxFormulaUtil.getTaxFormula(taxFunctionVO, itemVO.getVformula());

		return null;
	}

	/**
	 * ����ʽ�滻����Ӧ�Ŀ�����ִ�е����
	 *
	 * @author zhangg on 2010-1-12
	 * @see nc.vo.wa.paydata.IFormula#checkReplace(java.lang.String)
	 */

	public static String getTaxFormula(FunctionVO taxFunctionVO, String itemFormula) throws BusinessException {

		// ��1��������Ƿ����tax��������
		// tax����.x��ʾ���ż���0�������ַ��� �������ʽΪ���ƥ��
		// \\s*��ʾtax�ͣ�����0�����Ͽո�
		Pattern pattern = Pattern.compile(taxFunctionVO.getPattern());
		Matcher matcher = pattern.matcher(itemFormula);
		if (!matcher.matches()) {
			return null;
		}

		Vector<String> formulaVector = new Vector<String>();

		// ����tax����
		while (matcher.find()) {
			String formula = matcher.group();
			formulaVector.add(formula);
		}

		if (formulaVector.size() > 1) {
			throw new BusinessException(itemFormula + ResHelper.getString("6013salarypmt","06013salarypmt0274")/*@res "����1������tax����, ��֧��!"*/);
		}

		String taxFormula = formulaVector.get(0);

		return taxFormula;
	}
}