package nc.impl.hr.infoset.localization;

import nc.itf.hr.infoset.localization.IAddLocalizationFieldStrategy;
import nc.vo.pub.BusinessException;

/***************************************************************************
 * HR���ػ���������������HR��Ŀ��Ҫ����Ա������Ϣ�ֶ�<br>
 * Created on 2018-10-02 18:36:01pm
 * @author Ethan Wu
 ***************************************************************************/

public class MalaysiaFieldStrategy extends AbstractAddFieldStrategy implements IAddLocalizationFieldStrategy {
	
	public MalaysiaFieldStrategy() throws BusinessException {
		countryCode = "MY";
	}
}
