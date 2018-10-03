package nc.itf.hr.infoset;

import nc.vo.hr.infoset.InfoItemMapVO;
import nc.vo.hr.infoset.InfoItemVO;
import nc.vo.hr.infoset.InfoSetVO;
import nc.vo.hr.infoset.InfoSortVO;
import nc.vo.pub.BusinessException;

/***************************************************************************
 * <br>
 * Created on 2009-12-4 14:29:34<br>
 * @author Rocex Wang
 ***************************************************************************/
public interface IInfoSet
{
    /***************************************************************************
     * ɾ����Ϣ��<br>
     * Created on 2009-12-9 11:03:48<br>
     * @param blDispatchEvent
     * @param infoItemVOs
     * @throws BusinessException
     * @author Rocex Wang
     ***************************************************************************/
    void deleteInfoItem(boolean blDispatchEvent, InfoItemVO... infoItemVOs) throws BusinessException;
    
    /***************************************************************************
     * ɾ����Ϣ��<br>
     * Created on 2009-12-4 14:33:20<br>
     * @param blDispatchEvent
     * @param infoSetVOs
     * @throws BusinessException
     * @author Rocex Wang
     ***************************************************************************/
    void deleteInfoSet(boolean blDispatchEvent, InfoSetVO... infoSetVOs) throws BusinessException;
    
    /***************************************************************************
     * ɾ����Ϣ������<br>
     * Created on 2009-12-9 10:59:24<br>
     * @param blDispatchEvent
     * @param infoSortVO ��Ϣ������
     * @throws BusinessException
     * @author Rocex Wang
     ***************************************************************************/
    void deleteInfoSort(boolean blDispatchEvent, InfoSortVO infoSortVO) throws BusinessException;
    
    /***************************************************************************
     * ������Ϣ��<br>
     * Created on 2009-12-9 11:04:10<br>
     * @param blDispatchEvent
     * @param infoItemVO
     * @return InfoItemVO
     * @throws BusinessException
     * @author Rocex Wang
     ***************************************************************************/
    InfoItemVO insertInfoItem(boolean blDispatchEvent, InfoItemVO infoItemVO) throws BusinessException;
    
    /***************************************************************************
     * ������Ϣ��<br>
     * Created on 2009-12-4 14:33:26<br>
     * @param blDispatchEvent
     * @param infoSetVO
     * @return InfoSetVO
     * @throws BusinessException
     * @author Rocex Wang
     ***************************************************************************/
    InfoSetVO insertInfoSet(boolean blDispatchEvent, InfoSetVO infoSetVO) throws BusinessException;
    
    /***************************************************************************
     * ������Ϣ������<br>
     * Created on 2009-12-9 11:02:39<br>
     * @param blDispatchEvent
     * @param infoSortVO
     * @return InfoSortVO
     * @throws BusinessException
     * @author Rocex Wang
     ***************************************************************************/
    InfoSortVO insertInfoSort(boolean blDispatchEvent, InfoSortVO infoSortVO) throws BusinessException;
    
    /**************************************************************
     * ��������V5 �� V6 �ֶζ�Ӧ��ϵ��¼<br>
     * Created on 2012-10-20 10:32:24<br>
     * @param infoItemMapVOs
     * @throws BusinessException
     * @author Rocex Wang
     **************************************************************/
    void saveInfoItemMap(InfoItemMapVO infoItemMapVOs[]) throws BusinessException;
    
    /***************************************************************************
     * ͬ������ģ��<br>
     * Created on 2009-12-24 9:28:51<br>
     * @param infoSortVO
     * @param infoSetVOs
     * @author Rocex Wang
     * @param blDispatchEvent
     * @throws BusinessException
     ***************************************************************************/
    void syncBillTemplet(boolean blDispatchEvent, InfoSortVO infoSortVO, InfoSetVO... infoSetVOs) throws BusinessException;
    
    /***************************************************************************
     * ͬ��Ԫ����<br>
     * Created on 2009-12-22 15:41:14<br>
     * @param blDispatchEvent
     * @param blChangeAuditInfo
     * @param infoSortVO
     * @param infoSetVOs
     * @return InfoSetVO[]
     * @throws BusinessException
     * @author Rocex Wang
     ***************************************************************************/
    InfoSetVO[] syncMetaData(boolean blDispatchEvent, boolean blChangeAuditInfo, InfoSortVO infoSortVO, InfoSetVO... infoSetVOs) throws BusinessException;
    
    /**************************************************************
     * ͨ����Ϣ���������ͬ��Ԫ����<br>
     * Created on 2013-5-29 10:12:49<br>
     * @param blDispatchEvent
     * @param blChangeAuditInfo
     * @param strPk_sort
     * @return InfoSetVO[]
     * @throws BusinessException
     * @author Rocex Wang
     **************************************************************/
    InfoSetVO[] syncMetaDataBySort(boolean blDispatchEvent, boolean blChangeAuditInfo, String strPk_sort) throws BusinessException;
    
    /***************************************************************************
     * ͬ����ӡģ��<br>
     * Created on 2009-12-24 9:28:45<br>
     * @param infoSetVOs
     * @param infoSortVO
     * @author Rocex Wang
     * @param blDispatchEvent
     * @throws BusinessException
     ***************************************************************************/
    void syncPrintTemplet(boolean blDispatchEvent, InfoSortVO infoSortVO, InfoSetVO... infoSetVOs) throws BusinessException;
    
    /***************************************************************************
     * ͬ����ѯģ��<br>
     * Created on 2009-12-24 9:28:29<br>
     * @param infoSetVOs
     * @author Rocex Wang
     * @param blDispatchEvent
     * @param infoSortVO
     * @throws BusinessException
     ***************************************************************************/
    void syncQueryTemplet(boolean blDispatchEvent, InfoSortVO infoSortVO, InfoSetVO... infoSetVOs) throws BusinessException;
    
    /***************************************************************************
     * ͬ��ģ�壬��������ģ�塢��ѯģ��<br>
     * Created on 2009-12-24 9:24:25<br>
     * @param blDispatchEvent
     * @param infoSortVO
     * @param infoSetVOs
     * @author Rocex Wang
     * @throws BusinessException
     ***************************************************************************/
    void syncTemplet(boolean blDispatchEvent, InfoSortVO infoSortVO, InfoSetVO... infoSetVOs) throws BusinessException;
    
    /***************************************************************************
     * ͬ��ģ�壬��������ģ�塢��ѯģ��<br>
     * Created on 2015-8-6 10:42:47<br>
     * @param blDispatchEvent
     * @param strPk_sort
     * @throws BusinessException
     * @author Rocex Wang
     ***************************************************************************/
    void syncTempletBySort(boolean blDispatchEvent, String strPk_sort) throws BusinessException;
    
    /***************************************************************************
     * ͬ����Ϣ����Ӧ��VO<br>
     * Created on 2010-1-8 14:05:35<br>
     * @param infoSortVO
     * @param infoSetVOs
     * @author Rocex Wang
     * @throws BusinessException
     ***************************************************************************/
    void syncVO(InfoSortVO infoSortVO, InfoSetVO... infoSetVOs) throws BusinessException;
    
    /***************************************************************************
     * <br>
     * Created on 2009-12-9 11:04:37<br>
     * @param blDispatchEvent
     * @param blChangeAuditInfo
     * @param infoItemVO
     * @return InfoItemVO
     * @throws BusinessException
     * @author Rocex Wang
     ***************************************************************************/
    InfoItemVO updateInfoItem(boolean blDispatchEvent, boolean blChangeAuditInfo, InfoItemVO infoItemVO) throws BusinessException;
    
    /***************************************************************************
     * <br>
     * Created on 2009-12-4 14:33:44<br>
     * @param blDispatchEvent
     * @param blChangeAuditInfo
     * @param infoSetVOs
     * @return InfoSetVO
     * @throws BusinessException
     * @author Rocex Wang
     ***************************************************************************/
    InfoSetVO[] updateInfoSet(boolean blDispatchEvent, boolean blChangeAuditInfo, InfoSetVO... infoSetVOs) throws BusinessException;
    
    /***************************************************************************
     * <br>
     * Created on 2009-12-9 11:03:07<br>
     * @param blDispatchEvent
     * @param blChangeAuditInfo
     * @param infoSortVO
     * @return InfoSortVO
     * @throws BusinessException
     * @author Rocex Wang
     ***************************************************************************/
    InfoSortVO updateInfoSort(boolean blDispatchEvent, boolean blChangeAuditInfo, InfoSortVO infoSortVO) throws BusinessException;
    
    /***************************************************************************
     * ��Ӷ����Ǳ��ػ��ֶ�<br>
     * Created on 2018-10-02 02:16:35<br>
     * @author Ethan Wu
     * @param country
     * @throws BusinessException
     ***************************************************************************/
    void addLocalizationFields(String country) throws BusinessException;
}
