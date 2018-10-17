package nc.ui.hr.infoset.ref;

import nc.hr.utils.ResHelper;
import nc.ui.bd.ref.AbstractRefModel;
import nc.vo.bd.ref.RefVO_mlang;
import nc.vo.hr.infoset.InfoItemVO;
import nc.vo.hr.infoset.InfoSetVO;

/***************************************************************************
 * HR���ػ�����Ա����������Ϣ������ ר���������б��̵Ĳ���<br>
 * Created on 2018-10-17 16:34:22<br>
 * @author Ethan Wu
 ***************************************************************************/
public class BDPsndocInfoItemRefModel extends AbstractRefModel
{
    
    public BDPsndocInfoItemRefModel()
    {
        super();
        
        reset();
    }

    @Override
    public void reset()
    {
        setRefTitle(ResHelper.getString("6001infset", "2infoset-000053")/* @res "��Ϣ��" */);
        setRefNodeName("��Ա����������Ϣ��"); /*-=notranslate=-*/
        setDefaultFieldCount(3);
        setMutilLangNameRef(false);
        
        // ��ߴ�����ϵͳ�Դ��Ĺ�ʽ���� ����ȶ��Ĺ�ʽ��ʽ Ȼ�󵼳�
        setPkFieldCode(InfoItemVO.PK_INFOSET_ITEM);
        setRefCodeField("concat('bd_psndoc.'," + InfoItemVO.ITEM_CODE + ")");
        setRefNameField("concat('bd_psndoc.'," + InfoItemVO.ITEM_CODE + ")");
        setTableName(InfoItemVO.getDefaultTableName());
        setFieldCode(new String[]{ "concat('bd_psndoc.'," + InfoItemVO.ITEM_CODE + ")", InfoItemVO.ITEM_NAME, InfoItemVO.META_DATA});
        setHiddenFieldCode(new String[]{
        		InfoItemVO.PK_INFOSET_ITEM,
        		InfoItemVO.PK_INFOSET,
        		InfoItemVO.RESPATH,
        		InfoItemVO.RESID});
        setWherePart(" 1 = 1 and meta_data like 'hrhi.bd_psndoc.%' ");
        setFieldName(new String[]{ResHelper.getString("6001infset", "2infset-000052")
        /* @res "��Ϣ������" */, ResHelper.getString("6001infset", "2infset-000053")
        /* @res "��Ϣ������" */, ResHelper.getString("6001infset", "2infset-000032")
        /* @res "��ӦԪ����" */});
        
        resetFieldName();
    }
}
