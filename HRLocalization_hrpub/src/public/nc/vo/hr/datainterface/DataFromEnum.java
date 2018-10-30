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
public class DataFromEnum extends MDEnum
{
    public static final DataFromEnum SINGLE = MDEnum.valueOf(DataFromEnum.class, 0);
    
    public static final DataFromEnum FORMULAR = MDEnum.valueOf(DataFromEnum.class, 1);
    
    public static final DataFromEnum BD_PSNDOC = MDEnum.valueOf(DataFromEnum.class, 2);
    
    public static final DataFromEnum WA_ITEM = MDEnum.valueOf(DataFromEnum.class, 3);
    
    public DataFromEnum(IEnumValue enumvalue)
    {
        super(enumvalue);
    }
    
}
