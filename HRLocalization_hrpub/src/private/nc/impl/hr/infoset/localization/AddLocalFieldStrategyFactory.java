package nc.impl.hr.infoset.localization;

import nc.itf.hr.infoset.localization.IAddLocalizationFieldStrategy;
import nc.vo.pub.BusinessException;

/***************************************************************************
 * ���ݲ�ͬ���ҷ�������ֶβ��Թ�����
 * @author Ethan Wu
 * Created on 2018-10-02 18:02:54 pm
***************************************************************************/
public class AddLocalFieldStrategyFactory {
	
	// To make it only available for static method calls
	private AddLocalFieldStrategyFactory() {
		
	}
	
	public static IAddLocalizationFieldStrategy getStrategy(String country) throws BusinessException {
		if (country.equals("MY")) {
			return new MalaysiaFieldStrategy();
		} else if (country.equals("SG")) {
			return new SingaporeFieldStrategy();
		} else {
			return null;
		}
	}
	
}
