package nc.vo.wa.item;

import java.math.BigDecimal;
import java.math.RoundingMode;

import nc.vo.pub.lang.UFDouble;

/**
 * Maysia PCB��EPF�ȵ� ���ȴ���Ƚ����⣬
 * 1,2,3,4��λΪ5�� 6,7,8,9��λΪ10���� 2.22Ϊ2.25�� 2.26Ϊ2.30
 * @author weiningc
 *
 */
public class UFDoubleScaleUtils extends UFDouble {
	
	public static UFDouble setScale(UFDouble value, UFDouble rounding, RoundingMode mode) {
		BigDecimal bd = value.toBigDecimal();
		BigDecimal deciaml = rounding.toBigDecimal();
		BigDecimal scaled = deciaml.signum()==0 ? bd :
			(bd.divide(deciaml,0,mode)).multiply(deciaml);
		return new UFDouble(scaled);
	}
	
}
