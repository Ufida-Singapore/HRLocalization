package nc.impl.hr.infoset.localization;

import nc.itf.hr.infoset.localization.IAddLocalizationFieldStrategy;
import nc.vo.hr.infoset.InfoSetVO;
import nc.vo.pub.BusinessException;

/***************************************************************************
 * HR���ػ��������¼���HR��Ŀ��Ҫ����Ա������Ϣ�ֶ�<br>
 * Created on 2018-10-02 18:36:01pm
 * @author Ethan Wu
 ***************************************************************************/
public class SingaporeFieldStrategy extends AbstractAddFieldStrategy implements IAddLocalizationFieldStrategy {
	
	public SingaporeFieldStrategy() throws BusinessException {
		countryCode = "SG";
	}
}
