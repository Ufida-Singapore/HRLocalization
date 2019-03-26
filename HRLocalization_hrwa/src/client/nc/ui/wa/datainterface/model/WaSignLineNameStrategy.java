package nc.ui.wa.datainterface.model;

import java.util.Vector;

import nc.hr.utils.ResHelper;
import nc.ui.wa.datainterface.view.ModuleItemStrategy;
import nc.vo.hr.datainterface.DataIOItemVO;
import nc.vo.hr.datainterface.FieldTypeEnum;
import nc.vo.wa.datainterface.DataIOconstant;


public class WaSignLineNameStrategy implements ModuleItemStrategy{
//	public static String unitCode = "0";
//
//	public static String payoffDate = "1";
//	public static String payoffNumber = "2";
	public WaSignLineNameStrategy(){

	}

	public DataIOItemVO[] getCorrespondingItems() {

	        return proFixWa();
	}

	private DataIOItemVO[] proFixWa()  {
		Vector<DataIOItemVO> itms = new Vector<DataIOItemVO>();
//		0  ��λ����
//		1  ��������
//		2  ��������
		//��������Ŀ
		DataIOItemVO tmpitem = new DataIOItemVO();
		tmpitem.setVname(ResHelper.getString("6013bnkitf","06013bnkitf0069")/*@res "��λ����"*/);
		tmpitem.setPrimaryKey(DataIOconstant.UNITCODE);
		tmpitem.setIflddecimal(Integer.valueOf(0));
		tmpitem.setIitemtype((Integer)FieldTypeEnum.STR.value());
		tmpitem.setIfldwidth(Integer.valueOf(20));
		itms.add(tmpitem);

		tmpitem = new DataIOItemVO();
		tmpitem.setVname(ResHelper.getString("6013bnkitf","06013bnkitf0070")/*@res "��������"*/);
		tmpitem.setPrimaryKey(DataIOconstant.DATE);
		tmpitem.setIflddecimal(Integer.valueOf(0));
		tmpitem.setIitemtype((Integer)FieldTypeEnum.STR.value());
		tmpitem.setIfldwidth(Integer.valueOf(10));
		itms.add(tmpitem);

		tmpitem = new DataIOItemVO();
		tmpitem.setVname(ResHelper.getString("6013bnkitf","06013bnkitf0071")/*@res "��������"*/);
		tmpitem.setPrimaryKey(DataIOconstant.PSNCOUNT);
		tmpitem.setIflddecimal(Integer.valueOf(0));
		tmpitem.setIitemtype((Integer)FieldTypeEnum.STR.value());
		tmpitem.setIfldwidth(Integer.valueOf(20));
		itms.add(tmpitem);
		
		// ���ػ��Ķ�����ӻ����������ֵ start
		tmpitem = new DataIOItemVO();
		tmpitem.setVname("Summary Item");
		tmpitem.setPrimaryKey(DataIOconstant.ITEMSUM);
		tmpitem.setIflddecimal(Integer.valueOf(2));
		tmpitem.setIitemtype((Integer)FieldTypeEnum.DEC.value());
		tmpitem.setIfldwidth(Integer.valueOf(20));
		itms.add(tmpitem);
		
		tmpitem = new DataIOItemVO();
		tmpitem.setVname("First Line Content");
		tmpitem.setPrimaryKey(DataIOconstant.FIRSTLINECONTENT);
		tmpitem.setIflddecimal(Integer.valueOf(0));
		tmpitem.setIitemtype((Integer)FieldTypeEnum.STR.value());
		tmpitem.setIfldwidth(Integer.valueOf(101));
		itms.add(tmpitem);
		// ���ػ��Ķ�����ӻ����������ֵ end

		DataIOItemVO[] allitemstmp = new DataIOItemVO[itms.size()];
		return itms.toArray(allitemstmp);
	}

}