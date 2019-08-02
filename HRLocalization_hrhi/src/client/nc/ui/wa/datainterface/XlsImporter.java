package nc.ui.wa.datainterface;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.hr.utils.ResHelper;
import nc.itf.hi.IPsndocQryService;
import nc.ui.pub.bill.BillItem;
import nc.ui.wa.salaryadjmgt.WaSalaryadjmgtUtility;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.hr.datainterface.BooleanEnum;
import nc.vo.hr.datainterface.FormatItemVO;
import nc.vo.hr.datainterface.HrIntfaceVO;
import nc.vo.hr.dataio.Validator;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.wa.datainterface.DataIOconstant;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author xuanlt
 *
 */
public class XlsImporter extends DefaultImporter
{ 

	protected java.io.File file = null;
	protected java.io.FileInputStream fileOut = null;
	private boolean error = false;
	protected Workbook wb = null;
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	protected String version = "2003";

	@Override
	protected void openFile() throws Exception
	{
		try
		{
			file = new java.io.File(getParas().getFileLocation());
			if (!file.exists())
			{
				throw new BusinessException(ResHelper.getString("6013datainterface","06013datainterface0119")/*@res "Դ�ļ������ڣ�������ʾ"*/);
			}
			if (!file.canRead())
			{
				throw new BusinessException(getParas().getFileLocation() + ResHelper.getString("6013datainterface","06013datainterface0120")/*@res "���ɶ�,���ȹرո��ļ�"*/);
			}
			if (file.isDirectory()) {
				throw new BusinessException(getParas().getFileLocation()
						+ ResHelper.getString("6013datainterface",
								"06013datainterface0123")/* @res "��ָ�������ļ���" */);
			}

			fileOut = new java.io.FileInputStream(file);

			//psifs = new POIFSFileSystem(fileOut);
			version=(file.getName().endsWith(".xls")?"2003":"2007");

		}
		catch (Exception e)
		{
			nc.vo.logging.Debug.error(e.getMessage(), e);

			closeFile();

			throw new BusinessException(e.getMessage());
		}
	}

	@Override
	protected void closeFile() throws Exception
	{
		if (fileOut != null)
		{
			fileOut.close();
		}
	}

	protected void beforeReadData() throws Exception
	{

	}

	protected void afterReadData() throws Exception
	{

	}

	@Override
	protected ArrayList<HashMap<String, Object>> readData() throws Exception
	{

		if(version.equals("2003")){
			wb=new HSSFWorkbook(fileOut);
		}else if(version.equals("2007")){
			wb=new XSSFWorkbook(fileOut);
		}
		//wb = new Workbook(psifs);
		Sheet sht = wb.getSheetAt(0);

		FormatItemVO[] vos = getFormatItemVOs();
		//Vector<SuperVO> vector = new Vector<SuperVO>();

		ArrayList<HashMap<String, Object>> datas = new ArrayList<HashMap<String, Object>>();

		StringBuffer sbMessage = null;
		
		//CCDC���Ի�����, �͹�����ʹ���Լ���Excel���ʸ��Ի����� add by weiningc 20190130 
		//��������Ϊ���ػ�������,����Ҫ�������÷��Ͽͻ�Ҫ��ĸ�ʽ��������Ա���룬���ˣ����У����������������Ա���뵥Ԫ��Ϊ�գ����й���
		//1.��ѯ��Ա����������index
		Integer psncodeindex = this.queryPsnCodeIndex(sht, vos);
		//2. ��ѯ��Ա����
		List<String> psncodelist = this.queryPsnCodeForImport(sht, vos, psncodeindex);
		
		// ѭ������ÿһ������
		for (int index = 0; index <= sht.getLastRowNum(); index++)
		{	
			HrIntfaceVO itfVO = (HrIntfaceVO) getIntfaceInf().getParentVO();
			// �����ͷ
			if (itfVO.getIouthead() != null && itfVO.getIouthead().equals(BooleanEnum.YES.value()) && index == 0)
			{
				continue;
			}
			// �õ�һ������
			Row row = sht.getRow(index);
			// SuperVO vo =(SuperVO)newInstance(voClass);
			
			FormatItemVO vo = new FormatItemVO();
			
			// �����к�
			boolean linflag = false;// �Ƿ��Ѿ������к�
			// int offset = 0;// ���кţ������ƫ��
			
			if (itfVO.getIouthead() != null && itfVO.getIouthead().equals(BooleanEnum.YES.value()) && !linflag)
			{
				// offset++;
				linflag = true;
			}
			
			HashMap<String, Object> map = new HashMap<String, Object>();
			
			if(vos.length > 0){
				sbMessage = new StringBuffer();
			}
			//����Ա�������ޱ���,����Ϊ��
			Boolean isContinueRowData = this.wetherRowBlank(row, psncodeindex, psncodelist);
			if(isContinueRowData) {
				continue;
			}
			
			for (int temp = 0; temp < vos.length; temp++)
			{
				
				// ѭ������ÿһ����Ԫ��
				Cell cell = row.getCell((short) (temp/* + offset */));
				
				String sValue = getCellValue(cell);
				
				// ��֤�����Ƿ���ȷ
				if (!checkData(vos[temp], sValue, sbMessage))
				{
					renderCell(cell, wb);
					
				}
				// vo.setAttributeValue(getFieldCode(vos[temp].getVcontent()),
				// sValue);
				vo.setAttributeValue(vos[temp].getVcontent().replace(",", ""), sValue);
				map.put(vos[temp].getVcontent().replace(".", ""), sValue);
				
			}
			
			
			String strErrorMessage = sbMessage.toString();
			if (StringUtils.isNotEmpty(strErrorMessage)) {
				Cell cell = row.createCell(vos.length, Cell.CELL_TYPE_STRING);
				cell.setCellValue(strErrorMessage);
				renderCell(cell, wb);
			}
			
			datas.add(map);
			//vector.add(vo);
		}
			
		return datas;
		// return vector.toArray((T[])
		// java.lang.reflect.Array.newInstance(FormatItemVO.class,
		// vector.size()));
	}

	private Boolean wetherRowBlank(Row row, Integer psncodeindex, List<String> psncodelist) {
		Cell cell = row.getCell(psncodeindex - 1);
		int cellType = cell.getCellType();
		if(Cell.CELL_TYPE_BLANK == cellType) {
			return true;
		}
		if(Cell.CELL_TYPE_STRING == cellType && StringUtils.isBlank(cell.getStringCellValue())) {
			return true;
		}
		if(Cell.CELL_TYPE_STRING == cellType && !psncodelist.contains(cell.getStringCellValue().trim())) {
			return true;
		}
		return false;
	}

	private Integer queryPsnCodeIndex(Sheet sht, FormatItemVO[] vos) {
		//��Ա�������ڵ���
		Integer psncodeindex = 0;
		for(FormatItemVO vo : vos) {
			if("bd_psndoc.code".equals(vo.getVcontent())) {
				psncodeindex = vo.getIseq();
			}
		}
		if(psncodeindex == 0) {
			ExceptionUtils.wrappBusinessException("Must have a staff code column, check please��");
		}
		return psncodeindex;
	}

	private List<String> queryPsnCodeForImport(Sheet sht, FormatItemVO[] vos, Integer psncodeindex) {
		List<String> psncodelist = new ArrayList<String>();
		// ѭ������ÿһ������
		for (int index = 0; index <= sht.getLastRowNum(); index++){	
			HrIntfaceVO itfVO = (HrIntfaceVO) getIntfaceInf().getParentVO();
			// �����ͷ
			if (itfVO.getIouthead() != null && itfVO.getIouthead().equals(BooleanEnum.YES.value()) && index == 0){
				continue;
			}
			// �õ�һ������
			Row row = sht.getRow(index);
			FormatItemVO vo = new FormatItemVO();
			for (int temp = 0; temp < vos.length; temp++){
				//��Ա����
				if(psncodeindex == (temp+1)) {
					// ѭ������ÿһ����Ԫ��
					Cell cell = row.getCell((short) (temp/* + offset */));
					String sValue = getCellValue(cell);
					if(!StringUtils.isBlank(sValue)) {
						psncodelist.add(sValue);
					}
				}
			}
		}
		//��Ҫ��������չ���
		IPsndocQryService service = NCLocator.getInstance()
				.lookup(IPsndocQryService.class);
		try {
			if(psncodelist.size() == 0) {
				ExceptionUtils.wrappBusinessException("Please confirm has a personal code.");
			}
			PsndocVO[] psnDocVOs = service.queryPsndocVOByCondition(psncodelist.toArray(new String[0]));
			//У����Ա add by weiningc 20190621 ����Ա������,�쳣����
			this.verifyPsnCode(psnDocVOs, psncodelist);
			psncodelist.clear();
			for(PsndocVO docvo : psnDocVOs) {
				psncodelist.add(docvo.getCode());
			}
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		return psncodelist;
	}

	private void verifyPsnCode(PsndocVO[] psnDocVOs, List<String> psncodelist) {
		if(psnDocVOs == null || psnDocVOs.length == 0) {
			ExceptionUtils.wrappBusinessException("Please confirm has personal code.");
		}
		//��������ϵͳ����Ա
		List<String> existcodes = new ArrayList<String>();
		List<String> notexistcodes = new ArrayList<String>();
		for(PsndocVO vo : psnDocVOs) {
			existcodes.add(vo.getCode());
		}
		for(String str : psncodelist) {
			if(!existcodes.contains(str)) {
				notexistcodes.add(str);
			}
		}
		if(notexistcodes != null && notexistcodes.size() > 0) {
			ExceptionUtils.wrappBusinessException("Below person code not exist system: " + notexistcodes.toString());
		}
	}

	/**
	 * �÷����� VisualAge �д����� ��������: ���ļ��ж���һ������ �������˵��: ���ֵ˵��:
	 *
	 * @return java.lang.String
	 * @param raf
	 *            java.io.Reader
	 * @param separator
	 *            java.lang.String
	 * @exception java.lang.Throwable
	 *                �쳣������
	 */
	protected String readLine(java.io.Reader raf, String separator) throws Exception
	{
		try
		{

			char[] cBuffer = new char[10000];
			int i = 0;
			while (raf.read(cBuffer, i, 1) > 0 && i < 10000)
			{
				if (i > 0)
				{
					if (separator.equals(String.valueOf(cBuffer, i - 1, 2)))
					{
						break;
					}
				}
				i++;
			}
			return String.valueOf(cBuffer, 0, i);
		}
		catch (Exception e)
		{
			throw new Exception(ResHelper.getString("6013datainterface","06013datainterface0117")/*@res "������ʱ����"*/);
		}
	}

	/**
	 * �Ƿ���ҪС����
	 */
	protected boolean isNeedDot()
	{
		int dot = ((HrIntfaceVO) getIntfaceInf().getParentVO()).getIifdot();
		return (dot == 1) ? true : false;
	}

	/**
	 * �Ƿ���Ҫǧλ�ָ��
	 */
	protected boolean isNeedKilobit()
	{
		int dot = ((HrIntfaceVO) getIntfaceInf().getParentVO()).getIifkilobit();
		return (dot == 1) ? true : false;
	}

	/**
	 * �Ƿ���Ҫͳһ��λ��
	 *
	 * @return
	 */
	protected boolean isNeedCaret()
	{
		int dot = ((HrIntfaceVO) getIntfaceInf().getParentVO()).getIifcaret();
		return (dot == 1) ? true : false;
	}

	/**
	 * �Ƿ�ʹ��ͳһ��Ŀ�ָ��
	 *
	 * @return
	 */
	protected boolean isNeedSeperator()
	{
		int dot = ((HrIntfaceVO) getIntfaceInf().getParentVO()).getIifseparator();
		return (dot == 1) ? true : false;
	}

	protected String getUnifySeperator()
	{
		int dot = ((HrIntfaceVO) getIntfaceInf().getParentVO()).getIseparator();
		return DataIOconstant.ITEMSEPERATOR.get(dot).toString();
	}

	/**
	 * ȷ��һ���ַ����Ƿ�ȫ�����������
	 *
	 * @param str
	 * @return
	 */
	private static boolean isDigit(char c)
	{
		int x = c;

		if ((x >= 48) && (x <= 57))
		{
			return true;
		}

		return false;
	}

	private static boolean isDigit(String s)
	{
		if (isNull(s))
		{
			return false;
		}

		char[] c = s.toCharArray();

		for (int i = 0; i < c.length; i++)
		{
			if (!isDigit(c[i]))
			{
				return false;
			}
		}

		return true;
	}

	private static boolean isNull(String s)
	{
		if (s == null || s.equals(""))
		{
			return true;
		}
		if ((s.trim().equals("null")))
		{
			return true;
		}

		return false;
	}

	/**
	 * �����������͵õ���Ӧ��ֵ
	 *
	 * @param value
	 * @param type
	 * @return
	 */
	protected Object getValueByType(String value, int type)
	{
		if (value == null)
		{
			value = "";
		}
		return value.trim();

	}

	// /**
	// *
	// * �˲��û������������Ч��
	// * ������ڴ������ݣ���ͨ���Ի��������ʾ
	// * �ռ�������Ϣ��ʹ�÷�����ģʽ�� ���������ࣺVisitor
	// * @param model ��ģ��
	// * @param tableData xls�������
	// * @return
	// */
	// private UIDialog checkData2(BillModel model, SuperVO[] tableData) {
	// if (tableData == null || model == null) {
	// return null;
	// }
	// BillItem[] items = model.getBodyItems();
	// Visitor visitor = new Visitor(items);
	// for (int index = 0; index < tableData.length; index++) {
	// BankSheetVO tempData = tableData[index];
	// String columns = "";
	// for (int temp = 0; temp < items.length; temp++) {
	// Object aValue = tempData
	// .getAttributeValue(items[temp].getKey());
	// if(aValue==null){aValue="";}
	// int datatype = items[temp].getDataType();
	// int length = items[temp].getLength();
	// int decimals = items[temp].getDecimalDigits();
	// if (aValue != null&&!aValue.toString().endsWith("ERROR")) {
	// switch (datatype) {
	// case BillItem.INTEGER:
	// // С��ʱ��ʽ��
	// if (!(aValue instanceof Integer)) {
	// if (!Validator.isInteger( aValue.toString().trim())) {
	// columns+=","+temp;
	// }else{//��֤���ݳ���
	// if(aValue.toString().length()>length){
	// columns+=","+temp;
	// }
	// }
	//
	// }
	// break;
	//
	// case BillItem.DECIMAL:
	// // -----Ϊ���þ������ӵĴ��룬����С�����������¼���------------------
	//
	// if (!(aValue instanceof UFDouble)) {
	// if (!Validator.isUFDouble(aValue.toString().trim())) {
	// columns+=","+temp;
	// }else{//��֤���ݳ�����С��λ��
	// //���ݳ�����ָ�������ֵĳ���,С��λ����ָС��λ�ĳ���
	//
	// int digitsLength=0;//С��λ��Ĭ��Ϊ0;
	// int intLength = 0;//��������Ĭ����0
	// int dotPosition = aValue.toString().trim().indexOf(".");//С�����λ��
	//
	// if(decimals>0){//��Ϊ�����е����ݳ���=����λ��+С��λ��+1,����������л�ԭ.
	// length = length-decimals-1;
	// }
	// if(dotPosition>0){
	// digitsLength =
	// aValue.toString().trim().substring(dotPosition).length()-1;
	// intLength = aValue.toString().trim().substring(0,dotPosition).length();
	// }else{//û��С��
	// digitsLength = 0;
	// intLength = aValue.toString().trim().length();
	// }
	// //��֤���ݳ���,С��λ��
	// if(intLength>length || digitsLength>decimals){
	// columns+=","+temp;
	// }
	// }
	// }
	// break;
	// case BillItem.BOOLEAN:
	// if (!(aValue instanceof UFBoolean)) {
	// if (!Validator.isUFBoolean(aValue.toString().trim())) {
	// columns+=","+temp;
	// }else{//��֤���ݳ���
	// if(aValue.toString().length()>length){
	// columns+=","+temp;
	// }
	// }
	// }
	// break;
	// case BillItem.DATE:
	// if (!(aValue instanceof UFDate)) {
	// String strValue = aValue.toString().trim();
	// if (!strValue.equals("")&& !Validator.isUFDate(strValue)) {
	// columns+=","+temp;
	// }else{//��֤���ݳ���
	// if(aValue.toString().length()>length){
	// columns+=","+temp;
	// }
	// }
	// }
	// break;
	//
	// //�ַ����Ͳ���Ҫ��������Ϊ aValue ����һ���ַ�����
	// default:
	// if(aValue.toString().length()>length){//��֤���ݳ���
	// columns+=","+temp;
	// }
	// break;
	// }
	// }else if(aValue!=null&& aValue.toString().endsWith("ERROR")){
	// columns+=","+temp;
	// }
	// }
	// // ��ӣ�
	// visitor.add(tempData, index+1, columns);
	// }
	//
	// if (visitor.haveError()) {
	// return new ReportDialogForDataIn(this, visitor.getColumns(),
	// visitor.toArray());
	// } else {
	// return null;
	// }
	//
	// }

	/**
	 * �˲鵥Ԫֵ�Ƿ�Ϸ� ������Ϸ�������Ӧ�ĵ�Ԫcell ��Ϊ��ɫ
	 *
	 * @param item
	 * @param aValue
	 * @return
	 */
	private boolean checkData(FormatItemVO item, String aValue, StringBuffer sbMessage)
	{

		if (aValue == null)
		{
			aValue = "";
		}
		// �������Ͳ�ͳһ
		int datatype = item.getIfieldtype();
		int length = item.getIfldwidth();
		int decimals = item.getIflddecimal();
		// �����Ƿ�ͨ��
		boolean isLost = true;
		switch (datatype)
		{
		case BillItem.DECIMAL:// ��ֵ��
			// -----Ϊ���þ������ӵĴ��룬����С�����������¼���------------------
			if (!Validator.isUFDouble(aValue.toString().trim()))
			{
				isLost = false;
				sbMessage.append( ResHelper.getString("60130adjmtc","060130adjmtc0159",aValue)/*@res "[{0}] ������ֵ���� �������趨�ĸ�ʽ!"*/);
			}
			else
			{// ��֤���ݳ�����С��λ��
				// ���ݳ�����ָ�������ֵĳ���,С��λ����ָС��λ�ĳ���

				int digitsLength = 0;// С��λ��Ĭ��Ϊ0;
				int intLength = 0;// ��������Ĭ����0
				int dotPosition = aValue.toString().trim().indexOf(".");// С�����λ��

				if (decimals > 0)
				{// ��Ϊ�����е����ݳ���=����λ��+С��λ��+1,����������л�ԭ.
					length = length - decimals - 1;
				}
				if (dotPosition > 0)
				{
					digitsLength = aValue.toString().trim().substring(dotPosition).length() - 1;
					intLength = aValue.toString().trim().substring(0, dotPosition).length();
				}
				else
				{// û��С��
					digitsLength = 0;
					intLength = aValue.toString().trim().length();
				}
				// ��֤���ݳ���,С��λ��
				if (intLength > length || (decimals !=0 && digitsLength > decimals))
				{
					isLost = false;
					sbMessage.append(ResHelper.getString("60130adjmtc","060130adjmtc0160",aValue)/*@res "[{0}]���ݳ��Ȼ���С��λ��  �������趨�ĸ�ʽ!"*/);
				}
			}
			break;
		case BillItem.BOOLEAN:
			if (!Validator.isUFBoolean(aValue.toString().trim()))
			{
				isLost = false;
				sbMessage.append( ResHelper.getString("60130adjmtc","060130adjmtc0161",aValue)/*@res "[{0}]�������趨�ĸ�ʽ  ��������ݱ�����{N}����{Y}!"*/);
			}
			else
			{// ��֤���ݳ���
				if (aValue.toString().length() > length)
				{
					isLost = false;
					sbMessage.append(ResHelper.getString("60130adjmtc","060130adjmtc0162",aValue)/*@res "[{0}]���ݳ���  �������趨�ĸ�ʽ!"*/);
				}
			}
			break;
		case BillItem.DATE:
			String strValue = aValue.toString().trim();

			if (!StringUtils.isBlank(strValue) && !WaSalaryadjmgtUtility.isUFDate(strValue))
			{
				isLost = false;
				sbMessage.append(ResHelper.getString("60130adjmtc","060130adjmtc0163",aValue)/*@res "[{0}]�����������ͣ��������趨�ĸ�ʽ!"*/);
			}

			if (aValue.toString().length() > length)
			{
				isLost = false;
				sbMessage.append( ResHelper.getString("60130adjmtc","060130adjmtc0164",aValue)/*@res "[{0}]���ݳ��Ȳ���ȷ���������趨�ĸ�ʽ!"*/);
			}
			break;
			// �ַ����Ͳ���Ҫ��������Ϊ aValue ����һ���ַ�����
		default:
			if (aValue.toString().length() > length)
			{// ��֤���ݳ���
				isLost = false;
				sbMessage.append(ResHelper.getString("60130adjmtc","060130adjmtc0164",aValue)/*@res "[{0}]���ݳ��Ȳ���ȷ���������趨�ĸ�ʽ!"*/);
			}
			break;
		}
		if (!isLost)
		{
			sbMessage.append("\n");
		}
		return isLost;
	}

	private String getCellValue(Cell cell)
	{
		if (cell == null)
		{
			return null;
		}
		int type = cell.getCellType();
		String value = "";
		switch (type)
		{
		case Cell.CELL_TYPE_NUMERIC:
			if (HSSFDateUtil.isCellDateFormatted(cell))
			{
				value = this.sdf.format(cell.getDateCellValue());
				break;
			}
			value = String.valueOf(cell.getNumericCellValue());
			if (value.indexOf("E") > 0)
			{
				value = parseDouble(value);
			}
			break;
		case Cell.CELL_TYPE_STRING:
			value = cell.getStringCellValue().trim();
			break;
		case Cell.CELL_TYPE_BOOLEAN:
			value = String.valueOf(cell.getBooleanCellValue());
			break;
			//add chenth 20190619 ����excel�й�ʽ�����
		case Cell.CELL_TYPE_FORMULA:
			try{
				if (HSSFDateUtil.isCellDateFormatted(cell))
				{
					value = this.sdf.format(cell.getDateCellValue());
					break;
				}
				value = String.valueOf(cell.getNumericCellValue());
				if (value.indexOf("E") > 0)
				{
					value = parseDouble(value);
				}
			}catch (IllegalStateException e) {
				value = cell.toString().trim();
            }
			break;
		//add end
		default:
		}
		return value;
	}

	/**
	 * ������ֵ�͵Ŀ�ѧ������
	 *
	 * @author xuhw on 2010-8-3
	 * @param strDigt
	 * @return
	 */
	private String parseDouble(String strDigt)
	{
		int pos = strDigt.indexOf("E");
		int dotPos = strDigt.indexOf(".");
		String betweenValue = strDigt.substring(dotPos + 1, pos);
		String temp = "";
		int power = Integer.parseInt(strDigt.substring(pos + 1));
		if (power > 0)
		{
			StringBuilder sbd = new StringBuilder();
			sbd.append(strDigt.substring(0, dotPos));
			for (int index = 0; index < power; index++)
			{
				if (index < betweenValue.length())
				{
					sbd.append(betweenValue.charAt(index));
				}
				else
				{
					sbd.append("0");
				}
			}

			if (power < betweenValue.length())
			{
				temp = betweenValue.substring(power);
				sbd.append((new StringBuilder()).append(".").append(temp).toString());
			}
			return sbd.toString();
		}
		else
		{
			return strDigt;
		}
	}

	private Cell renderCell(Cell cell, Workbook wb)
	{
		CellStyle titleCellStyle = wb.createCellStyle();
		Font font = wb.createFont();
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		font.setColor(Font.COLOR_RED);
		titleCellStyle.setFont(font);
		cell.setCellStyle(titleCellStyle);
		// ��֪ϵͳ���ڴ���
		if (!error)
		{
			error = true;
		}
		return cell;

	}

	@Override
	protected void beforeSetData() throws Exception
	{
		java.io.FileOutputStream bakfileOut = null;
		// ���ڴ���
		try
		{

			if (error)
			{
				String type = ".xls";
				if(null != version && version.equals("2003")) {
					type = ".xls";
				}else if(null != version && version.equals("2007")) {
					type = ".xlsx";
				}
				String newFilename = getParas().getFileLocation().replaceFirst(type, "_bak"+type);
				String newFilename2 = getParas().getFileLocation().replaceFirst(type, ""+type);
				java.io.File file2 = new java.io.File(newFilename);
				// if (!file.exists()) {
				// throw new BusinessException(error1);
				// }
				// if(!file.canRead()){
				// throw new
				// BusinessException(getParas().getFileLocation()+"���ɶ�,���ȹرո��ļ�");
				// }
				bakfileOut = new java.io.FileOutputStream(file2);
				// java.io.FileInputStream fileOut = new
				// java.io.FileInputStream(file);
				wb.write(bakfileOut);
				// ����Դ�ļ�Ϊ _bak.xls
				file.renameTo(new File(newFilename2));

				file2.renameTo(new File(getParas().getFileLocation()));
				throw new BusinessException(ResHelper.getString("6013datainterface","06013datainterface0121")/*@res "Դ�ļ����ݴ��ڴ��󣬴��������Ѿ��ú�ɫ�����ע������ϸ��飡"*/);
			}
		}
		finally
		{
			if (bakfileOut != null)
			{
				// file.
				bakfileOut.close();
			}
		}
	}

}