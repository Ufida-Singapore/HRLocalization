package nc.itf.hr.wa;

import nc.vo.pub.BusinessException;
import nc.vo.wa.item.WaItemVO;

/**
 * �������Ӻ��⹫��н����Ŀ
 * @author weiningc
 *
 */
public interface ISeaLocalItemManageService {

	void saveBactchItemForSeaLocal(WaItemVO vo, String countryitem) throws BusinessException, Exception;

}
