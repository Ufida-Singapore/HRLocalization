package nc.itf.hr.wa;

import nc.vo.pub.BusinessException;
import nc.vo.wa.item.WaItemVO;

/**
 * �������Ӻ��⹫��н����Ŀ
 * @author weiningc
 *
 */
public interface ISeaLocalItemManageService {
	
	/**
	 * @param vo 
	 * @throws BusinessException 
	 * @throws Exception 
	 * 
	 */
	void saveBactchItemForSeaLocal(WaItemVO vo) throws BusinessException, Exception;

}
