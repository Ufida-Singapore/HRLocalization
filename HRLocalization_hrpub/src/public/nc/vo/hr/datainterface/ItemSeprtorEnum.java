/***************************************************************
 * \
 * \
 ***************************************************************/
package nc.vo.hr.datainterface;

import nc.md.model.IEnumValue;
import nc.md.model.impl.MDEnum;

/**
 * <b> �ڴ˴���Ҫ��������Ĺ��� </b>
 * <p>
 * �ڴ˴���Ӵ����������Ϣ
 * </p>
 * ��������:
 * @author
 * @version NCPrj ??
 */
public class ItemSeprtorEnum extends MDEnum
{
    public static final ItemSeprtorEnum COMMA = MDEnum.valueOf(ItemSeprtorEnum.class, 0);
    
    public static final ItemSeprtorEnum SEM = MDEnum.valueOf(ItemSeprtorEnum.class, 1);
    public static final ItemSeprtorEnum ERECT = MDEnum.valueOf(ItemSeprtorEnum.class, 2);
    public static final ItemSeprtorEnum NULL = MDEnum.valueOf(ItemSeprtorEnum.class, 3);
//		20151016 xiejie3 NCdp205398656 ���б�����Ŀ�ָ�����ӡ��ո񡱣�����Ҫ�󳷵��˲�����
//    // 20150902 xiejie3 �����ϲ���NCdp205398656
//    //shenliangc ���б�����Ŀ�ָ�����ӡ��ո�
//    //�ո�
//    public static final ItemSeprtorEnum SPACE = MDEnum.valueOf(ItemSeprtorEnum.class, 3);
//    
    
    public ItemSeprtorEnum(IEnumValue enumvalue)
    {
        super(enumvalue);
    }
    
}
