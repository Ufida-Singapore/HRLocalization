package nc.itf.hr.infoset.localization;

import nc.vo.hr.infoset.InfoSetVO;

/***************************************************************************
 * ��Ϣ�����ػ����Ԥ���ֶβ��Խӿ� <br>
 * Created on 2018-10-02 14:44:30pm
 * @author Ethan Wu
 ***************************************************************************/
public interface IAddLocalizationFieldStrategy {

	public static final String PERSONAL_INFO_TABLE = "bd_psndoc";
	
	public InfoSetVO[] addLocalField(InfoSetVO[] vos);
}
