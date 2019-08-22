package nc.ui.wa.datainterface;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import nc.hr.utils.ResHelper;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.wa.pub.WADelegator;
import nc.vo.hr.append.AppendableVO;
import nc.vo.hr.datainterface.BooleanEnum;
import nc.vo.hr.datainterface.CaretposEnum;
import nc.vo.hr.datainterface.DateFormatEnum;
import nc.vo.hr.datainterface.FieldTypeEnum;
import nc.vo.hr.datainterface.FormatItemVO;
import nc.vo.hr.datainterface.HrIntfaceVO;
import nc.vo.hr.datainterface.IfsettopVO;
import nc.vo.hr.datainterface.ItemSeprtorEnum;
import nc.vo.hr.datainterface.LineTopEnum;
import nc.vo.hr.datainterface.LineTopPositionEnum;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.wa.datainterface.DataIOconstant;

import org.apache.commons.lang.StringUtils;


public class TxtExporterForBank extends DefaultExporter
{

	public static final String crlf = System.getProperties().getProperty("line.separator"); // ���з�
	java.io.Writer raf = null;
	java.io.FileOutputStream fileOut = null;

	@Override
	protected void openFile() throws Exception
	{
		try
		{
			getAppModel().setBlnIsCancel(false);
			if (getIntfaceInfs() != null && (getReadIndex() + 1) <= getIntfaceInfs().length)
			{
				if (getIntfaceInfs()[getReadIndex()] != null)
				{
					HrIntfaceVO itfVO = (HrIntfaceVO) getIntfaceInfs()[getReadIndex()].getParentVO();
					String fileName = getParas().getFileLocation() + "\\" + itfVO.getVifname() + ".txt";
					java.io.File file = new java.io.File(fileName);
					if (file.exists())
					{
						int i = MessageDialog.showOkCancelDlg(null, null, ResHelper.getString("6013datainterface","06013datainterface0107")/*@res "Ҫ�������ļ�["*/ + file.getName() + ResHelper.getString("6013datainterface","06013datainterface0108")/*@res "]�Ѵ��ڣ�����ִ�н��滻ԭ���ļ���Ҫ������?"*/);
						if (i != 1)
						{
							fileOut = null;
							raf = null;
							getAppModel().setBlnIsCancel(true);
							return;
						}
						// file.delete();
					}
					fileOut = new java.io.FileOutputStream(fileName);
					raf = new java.io.OutputStreamWriter(fileOut);
				}
			}

		}
		catch (Exception e)
		{
			// ���������
			closeFile();
			throw new Exception(ResHelper.getString("6013datainterface","06013datainterface0109")/*@res "�ļ������ڻ��ļ�·�����ƴ���"*/);
		}
	}

	@Override
	protected void closeFile() throws Exception
	{
		if (raf != null)
		{
			raf.close();
		}

		if (fileOut != null)
		{

			fileOut.close();
		}
	}

	/**
	 * ��������Ŀ���ַ�����ʾ
	 *
	 * @param aDigit
	 * @param maxFieldLen
	 * @param ifDot
	 *            //�Ƿ�����ҪС����
	 * @param ifQwfg
	 *            //�Ƿ���Ҫǧλ�ָ��
	 * @param caretPos
	 * @param caret
	 * @param decimalNum
	 * @param iIfcaret
	 * @return
	 * @throws Exception
	 */
	protected String getStringDigit(String aDigit, FormatItemVO vo, boolean ifDot, boolean ifQwfg)
	{
		// ����ÿһ�е�������� = �ж���Ŀ��
		int maxFieldLen = vo.getIfldwidth();
		int decimalNum = vo.getIflddecimal(); // С��λ��
//		String includeAfter = vo.getVincludeafter();// ��λ�ָ���
//		String includeBefore = vo.getVincludebefore();// ǰλ�ָ���
		nc.vo.pub.lang.UFDouble d = new nc.vo.pub.lang.UFDouble(aDigit);
		d = d.setScale(-1 * decimalNum, UFDouble.ROUND_CEILING);
		aDigit = d.toString();

		/* ����ǧλ�ָ��� */
		if (ifQwfg)
		{
			aDigit = getAddQWFG(aDigit, ifQwfg);
		}

		/* ����С���� */
		int dotPos = aDigit.indexOf(".");
		if (dotPos < 0)
		{
			aDigit = aDigit + ".";
		}
		dotPos = aDigit.indexOf(".");

		if (!ifDot)
		{// ����Ҫ
			aDigit = aDigit.substring(0, dotPos) + aDigit.substring(dotPos + 1);
		}

		int len = getStrlength(aDigit);

		if (len > maxFieldLen)
		{// ����������ȣ������н�ȡ
			/* �Ѿ��ڽ�������ʾ�ĳ��ȣ����û��������ĳ��ȡ� */
			// return getStringCutByByte(aDigit, maxFieldLen);
			return aDigit;
			// return getStringCutByByte(aDigit, len);
		}
		else if (len == maxFieldLen)
		{
			return aDigit;
		}

//		if (!StringUtils.isBlank(includeBefore))
		String temp = getStringStr(aDigit, vo);
//		{
//			temp = includeBefore + temp;
//		}
//		if (!StringUtils.isBlank(includeAfter))
//		{
//			temp = temp + includeAfter;
//		}
		return temp;

	}

	/**
	 * ����ǧλ�ָ��
	 *
	 * @param num
	 * @param txtQWFG
	 * @return
	 * @throws Exception
	 */
	protected String getAddQWFG(String num, boolean txtQWFG)
	{
		if (!txtQWFG)
		{// ����Ҫ
			return num;
		}
		else
		{
			String retNum = num;
			int posDec = num.indexOf(".");
			if (posDec < 0)
			{
				posDec = num.length();
			}
			posDec -= 3;
			while (posDec > 0)
			{
				retNum = num.substring(0, posDec) + "," + num.substring(posDec);
				posDec -= 3;
			}
			return retNum;
		}
	}

	/**
	 * ��������: ��BYTEΪ��λ��ȡ�ִ����� �������˵��:s �ִ�, l��BYTE��
	 */
	public String getStringCutByByte(String s, int l)
	{
		if (s == null)
		{
			return null;
		}
		int len = s.length();
		for (int i = len; i > 0; i--)
		{
			if (getStrlength(s) <= l)
			{
				return s;
			}
			s = s.substring(0, i);
		}
		return s;
	}

	protected String getStringStr(String aStr, FormatItemVO vo)
	{
		int maxFieldLen = vo.getIfldwidth();
		int caretPos = vo.getIcaretpos() == null ? (Integer) CaretposEnum.NO.value() : vo.getIcaretpos();
		String caret = vo.getVcaret();
		String includeAfter = vo.getVincludeafter();// ��λ�ָ���
		String includeBefore = vo.getVincludebefore();// ǰλ�ָ���

		int i, len;

		len = getStrlength(aStr);

		if (len > maxFieldLen)
		{
			aStr = getStringCutByByte(aStr, maxFieldLen);
		}

		len = getStrlength(aStr);

		/* ���Ӳ�λ�� */
		/**
		 * Modified by Young 2005-06-28 Start
		 */
		if (caretPos != 0)
		{
			i = 0;

			if (len < maxFieldLen)
			{
				if (caret == null || caret.trim().equals(""))
				{
					caret = BLANK;
				}

				while (i < maxFieldLen - len)
				{
					if (caretPos == 1) // ��ǰ
					{
						aStr = caret + aStr;
					}
					else
					{
						aStr = aStr + caret;
					}
					i++;
				}
			}

		}
		if (!StringUtils.isBlank(includeBefore))
		{
			aStr = includeBefore + aStr;
		}
		if (!StringUtils.isBlank(includeAfter))
		{
			aStr = aStr + includeAfter;
		}
		return aStr;
	}

	/**
	 * �Ƿ���ҪС����
	 */
	protected boolean isNeedDot()
	{
		if (getIntfaceInfs() != null && (getReadIndex() + 1) <= getIntfaceInfs().length)
		{
			if (getIntfaceInfs()[getReadIndex()] != null)
			{
				HrIntfaceVO itfVO = (HrIntfaceVO) getIntfaceInfs()[getReadIndex()].getParentVO();
				int value = itfVO.getIifdot();
				return (value == 1) ? true : false;
			}
		}
		return false;
	}

	/**
	 * �Ƿ���Ҫǧλ�ָ��
	 */
	protected boolean isNeedKilobit()
	{
		if (getIntfaceInfs() != null && (getReadIndex() + 1) <= getIntfaceInfs().length)
		{
			if (getIntfaceInfs()[getReadIndex()] != null)
			{
				HrIntfaceVO itfVO = (HrIntfaceVO) getIntfaceInfs()[getReadIndex()].getParentVO();
				int value = itfVO.getIifkilobit();
				return (value == 1) ? true : false;
			}
		}
		return false;
	}

	/**
	 * �Ƿ���Ҫͳһ��λ��
	 *
	 * @return
	 */
	protected boolean isNeedCaret()
	{
		if (getIntfaceInfs() != null && (getReadIndex() + 1) <= getIntfaceInfs().length)
		{
			if (getIntfaceInfs()[getReadIndex()] != null)
			{
				HrIntfaceVO itfVO = (HrIntfaceVO) getIntfaceInfs()[getReadIndex()].getParentVO();
				int value = itfVO.getIifcaret();
				return (value == 1) ? true : false;
			}
		}
		return false;
	}

	/**
	 * �Ƿ�ʹ��ͳһ��Ŀ�ָ��
	 *
	 * @return
	 */
	protected boolean isNeedSeperator()
	{
		if (getIntfaceInfs() != null && (getReadIndex() + 1) <= getIntfaceInfs().length)
		{
			if (getIntfaceInfs()[getReadIndex()] != null)
			{
				HrIntfaceVO itfVO = (HrIntfaceVO) getIntfaceInfs()[getReadIndex()].getParentVO();
				int value = itfVO.getIifseparator();
				return (value == 1) ? true : false;
			}
		}
		return false;
	}

	protected String getUnifySeperator()
	{

		if (getIntfaceInfs() != null && (getReadIndex() + 1) <= getIntfaceInfs().length)
		{
			if (getIntfaceInfs()[getReadIndex()] != null)
			{
				HrIntfaceVO itfVO = (HrIntfaceVO) getIntfaceInfs()[getReadIndex()].getParentVO();
				int value = itfVO.getIseparator();
				return DataIOconstant.ITEMSEPERATOR.get(value);
			}
		}
		return DataIOconstant.ITEMSEPERATOR.get(ItemSeprtorEnum.COMMA.value());
	}

	@Override
	protected void beforeOutputHead()
	{
		if (raf == null)
		{
			return;
		}
		// �ӿ���Ϣ���Ƿ�����к� ����������
		if (isUseTopLine())
		{
			// �ǣ���õ����е��к���Ϣ
			String signline = getFlagLine(LineTopPositionEnum.HEAD.toIntValue());
			// ѭ�������к���Ϣ
			signline = signline + (crlf);
			try
			{
				raf.write(signline.toString());
				raf.flush();
			}
			catch (IOException e)
			{}
		}
	}

	@Override
	protected void afterOutputBody()
	{
		// н�ʸ���������Ҫ��־��
		// �ӿ���Ϣ���Ƿ�����к� ������ĩ��
		if (raf == null)
		{
			return;
		}
		if (isUseBottomLine())
		{
			// �ǣ���õ����е��к���Ϣ
			String signline = getFlagLine(LineTopPositionEnum.TAIL.toIntValue());
			// ѭ�������к���Ϣ
			signline = signline + (crlf);
			try
			{
				raf.write(signline.toString());
				raf.flush();
			}
			catch (IOException e)
			{

			}
		}
	}

	private boolean isUseTopLine()
	{

		if (getIntfaceInfs() != null && (getReadIndex() + 1) <= getIntfaceInfs().length)
		{
			if (getIntfaceInfs()[getReadIndex()] != null)
			{
				HrIntfaceVO itfVO = (HrIntfaceVO) getIntfaceInfs()[getReadIndex()].getParentVO();
				int value = itfVO.getIiftop();
				return (value == 1) ? true : false;
			}
		}
		return false;
	}
	
	// HR���ػ����жϱ�־��2�Ƿ�����
	private boolean isUseBottomLine()
	{

		if (getIntfaceInfs() != null && (getReadIndex() + 1) <= getIntfaceInfs().length)
		{
			if (getIntfaceInfs()[getReadIndex()] != null)
			{
				HrIntfaceVO itfVO = (HrIntfaceVO) getIntfaceInfs()[getReadIndex()].getParentVO();
				int value = itfVO.getIiftop2();
				return (value == 1) ? true : false;
			}
		}
		return false;
	}

	private boolean isTheFirst()
	{
		if (getIntfaceInfs() != null && (getReadIndex() + 1) <= getIntfaceInfs().length)
		{
			if (getIntfaceInfs()[getReadIndex()] != null)
			{
				HrIntfaceVO itfVO = (HrIntfaceVO) getIntfaceInfs()[getReadIndex()].getParentVO();
				int value = itfVO.getToplineposition();
				return (value == (Integer) LineTopPositionEnum.HEAD.value()) ? true : false;
			}
		}
		return false;
	}

	private boolean isSline()
	{
		if (getIntfaceInfs() != null && (getReadIndex() + 1) <= getIntfaceInfs().length)
		{
			if (getIntfaceInfs()[getReadIndex()] != null)
			{
				HrIntfaceVO itfVO = (HrIntfaceVO) getIntfaceInfs()[getReadIndex()].getParentVO();
				int value = itfVO.getToplinenum();
				return (value == (Integer) LineTopEnum.MLINE.value()) ? false : true;
			}
		}
		return false;
	}

	
	// HR���ػ��Ķ������ݲ�������ȡ�������β����
	private IfsettopVO[] getSignlineItems(int toplinePosition)
	{
		IfsettopVO[] ret = null;
		if (getIntfaceInfs() != null && (getReadIndex() + 1) <= getIntfaceInfs().length)
		{
			if (getIntfaceInfs()[getReadIndex()] != null)
			{
				ret = (IfsettopVO[]) getIntfaceInfs()[getReadIndex()].getTableVO(DataIOconstant.HR_IFSETTOP);
				ArrayList<IfsettopVO> temp = new ArrayList<IfsettopVO>(Arrays.asList(ret));
				for (int i = temp.size() - 1; i >= 0; i--) {
					if (temp.get(i).getItoplineposition().equals(toplinePosition)) {
						continue;
					} else {
						temp.remove(i);
					}
				}
				ret = temp.toArray(new IfsettopVO[0]);
			}
		}

		return ret;
	}

	protected String getStringDigit4TopLine(String aDigit, IfsettopVO vo, boolean ifDot, boolean ifQwfg)
	{
		// ����ÿһ�е�������� = �ж���Ŀ��
		int maxFieldLen = vo.getIfldwidth();
		int decimalNum = 0;
		if (vo.getIflddecimal() != null)
		{
			decimalNum = vo.getIflddecimal(); // С��λ��
		}

		String includeAfter = "";// ��λ�ָ���
		String includeBefore = "";// ǰλ�ָ���
		nc.vo.pub.lang.UFDouble d = new nc.vo.pub.lang.UFDouble(aDigit);
		d = d.setScale(-1 * decimalNum, UFDouble.ROUND_CEILING);
		aDigit = d.toString();

		/* ����ǧλ�ָ��� */
		if (ifQwfg)
		{
			aDigit = getAddQWFG(aDigit, ifQwfg);
		}

		/* ����С���� */
		int dotPos = aDigit.indexOf(".");
		if (dotPos < 0)
		{
			aDigit = aDigit + ".";
		}
		dotPos = aDigit.indexOf(".");

		if (!ifDot)
		{// ����Ҫ
			aDigit = aDigit.substring(0, dotPos) + aDigit.substring(dotPos + 1);
		}

		int len = getStrlength(aDigit);

		if (len > maxFieldLen)
		{// ����������ȣ������н�ȡ
			/* �Ѿ��ڽ�������ʾ�ĳ��ȣ����û��������ĳ��ȡ� */
			// return getStringCutByByte(aDigit, maxFieldLen);
			return aDigit;
			// return getStringCutByByte(aDigit, len);
		}
		else if (len == maxFieldLen)
		{
			return aDigit;
		}

		String temp = getStringStr4TopLine(aDigit, vo);
		if (!StringUtils.isBlank(includeBefore))
		{
			temp = includeBefore + temp;
		}
		if (!StringUtils.isBlank(includeAfter))
		{
			temp = temp + includeAfter;
		}
		return temp;
	}

	protected String getStringStr4TopLine(String aStr, IfsettopVO vo)
	{
		int maxFieldLen = vo.getIfldwidth();
		int caretPos = vo.getIcaretpos() == null ? (Integer) CaretposEnum.BEFORE.value() : vo.getIcaretpos();
		String caret = vo.getVcaret();
		String includeAfter = "";// ������
		String includeBefore = "";// ǰ����

		int i, len;

		len = getStrlength(aStr);

		if (len > maxFieldLen)
		{
			aStr = getStringCutByByte(aStr, maxFieldLen);
		}

		/* ���Ӳ�λ�� */
		/**
		 * Modified by Young 2005-06-28 Start
		 */
		if (caretPos != 0)
		{
			i = 0;

			if (len < maxFieldLen)
			{
				if (caret == null || caret.trim().equals(""))
				{
					caret = BLANK;
				}

				while (i < maxFieldLen - len)
				{
					if (caretPos == 1) // ��ǰ
					{
						aStr = caret + aStr;
					}
					else
					{
						aStr = aStr + caret;
					}
					i++;
				}
			}

		}
		if (!StringUtils.isBlank(includeBefore))
		{
			aStr = includeBefore + aStr;
		}
		if (!StringUtils.isBlank(includeAfter))
		{
			aStr = aStr + includeAfter;
		}
		return aStr;
	}

	public String[] getTblAndCol(String tblAndCol)
	{
		int index = tblAndCol.indexOf(".");

		String tbl = tblAndCol.substring(0, index);

		String col = tblAndCol.substring(index + 1);

		String[] newTblAndCol = new String[2];

		newTblAndCol[0] = tbl;
		newTblAndCol[1] = col;

		return newTblAndCol;
	}

	// HR���ػ����������Դ����ȫ��ע���� ��������ʵ����һ��
	protected String getItemSum(String[] tabAndCol)
	{
		HrIntfaceVO itfVO = (HrIntfaceVO) getIntfaceInfs()[getReadIndex()].getParentVO();
		ArrayList<HashMap<String, Object>> datas = getAppModel().getResults().get(itfVO.getPk_dataio_intface());
		
		String key = tabAndCol[0] + tabAndCol[1];
		UFDouble sum = new UFDouble(UFDouble.ZERO_DBL);
		if (datas != null && datas.size() > 0) {
			for (HashMap<String, Object> entry : datas) {
				if (entry.containsKey(key)) {
					if (entry.get(key) != null) {
						sum = sum.add(new UFDouble(entry.get(key).toString()));
					}
				} else {
					ExceptionUtils.wrappBusinessException("The total sum item key does not exist!");
				}
			}
		}

		return sum.toString();
	}
	
	// HR���ػ������һ������ȡһЩ�����ַ����Ķ���
	protected String getFirstLineContent(String[] tabAndCol) {
		HrIntfaceVO itfVO = (HrIntfaceVO) getIntfaceInfs()[getReadIndex()].getParentVO();
		ArrayList<HashMap<String, Object>> datas = getAppModel().getResults().get(itfVO.getPk_dataio_intface());
		
		String key = tabAndCol[0] + tabAndCol[1];
		String result = null;
		if (datas != null && datas.size() > 0) {
			HashMap<String, Object> entry = datas.get(0);
			if (entry.containsKey(key)) {
				if (entry.get(key) != null) {
					result = entry.get(key) == null ? null : entry.get(key).toString();
				}
			} else {
				ExceptionUtils.wrappBusinessException("The first line content item key does not exist!");
			}
		}
		return result;
	}

	// ���ݸ�ʽ����ʽ������
	private String formatDate(UFDate date, String dateFormat)
	{
		// ȡ�� dateformat�е� YYYY MM DD
		String year = String.valueOf(date.getYear());
		String yearTwoDigit = year.substring(2);
		String month = String.valueOf(date.getMonth());
		if (month.trim().length() == 1)
		{
			month = "0" + month;
		}

		String day = String.valueOf(date.getDay());
		if (day.trim().length() == 1)
		{
			day = "0" + day;
		}
		return dateFormat.replaceAll("YYYY", year).replaceAll("YY", yearTwoDigit).replaceAll("MM", month).replaceAll("DD", day);

	}

	public String getFlagLine(int lineTopPosition)
	{
		IfsettopVO[] data_top = getSignlineItems(lineTopPosition);
		if (data_top == null)
		{
			return "";
		}
		boolean line = isSline();
		StringBuilder topLine = new StringBuilder();
		

		try
		{

			for (int i = 0; i < data_top.length; i++)
			{
				// �����߼�����д��һ�� ֮ǰβ�д��������¼ӵ�����
				if (data_top[i].getInextline().equals(BooleanEnum.YES.toIntValue()) && line) {
					topLine.append(crlf);
				} else if (i < data_top.length - 1 && !line && i != 0) {
					topLine.append(crlf);
				}
				Object objitemSumTableAndCol = data_top[i].getVfieldname();
				String itemSumTableAndCol = "";
				if (objitemSumTableAndCol != null)
				{
					itemSumTableAndCol = objitemSumTableAndCol.toString();
				}
				// String topSeperater = null;
				// �õ���Ŀ�ָ��
				// if(!StringHelper.isEmpty(data_top[i].getVseparator())){
				// topSeperater = data_top[i].getVseparator()
				// }
				String topSeperater = DataIOconstant.ITEMSEPERATOR.get(data_top[i].getVseparator());
				if (topSeperater == null)
				{
					topSeperater = DataIOconstant.ITEMSEPERATOR.get(ItemSeprtorEnum.COMMA.value());
				}

				// ����������
				if (data_top[i].getVcontent().equals(DataIOconstant.PSNCOUNT))
				{// ����
					// ���ڷ������������� "��Ŀ����" ����λλ�á� �� ����λ����
					// int length = getFormatItemVOs().length;
					HrIntfaceVO itfVO = (HrIntfaceVO) getIntfaceInfs()[getReadIndex()].getParentVO();
					ArrayList<HashMap<String, Object>> datas = getAppModel().getResults().get(itfVO.getPk_dataio_intface());

					topLine.append(getStringDigit4TopLine(String.valueOf(datas.size()), data_top[i], isNeedDot(), false));
				}
				else if (data_top[i].getVcontent().equals(DataIOconstant.ITEMSUM))
				{// ��Ŀ�ϼ�
					// ������Ŀ�ϼ�
					// �õ��������ֶ�
					if (!StringUtils.isBlank(itemSumTableAndCol))
					{
						String[] stArrayTabAndCol = getTblAndCol(itemSumTableAndCol);
						String itemSum = getItemSum(stArrayTabAndCol);
						itemSum = getStringDigit4TopLine(itemSum, data_top[i], isNeedDot(), false);
						topLine.append(itemSum);
					}
				}
				else if (data_top[i].getVcontent().equals(DataIOconstant.FIRSTLINECONTENT))
				{// ��������
					if (!StringUtils.isBlank(itemSumTableAndCol))
					{
						String[] stArrayTabAndCol = getTblAndCol(itemSumTableAndCol);
						String firstLineContent = getFirstLineContent(stArrayTabAndCol);
						firstLineContent = getStringStr4TopLine(firstLineContent, data_top[i]);
						topLine.append(firstLineContent);
					}
				}
				// ����λ����
				else if (data_top[i].getVcontent().equals(DataIOconstant.UNITCODE))
				{
					topLine.append(getStringStr4TopLine(itemSumTableAndCol, data_top[i]));
				}
				// �������ڣ�ע�����ڸ�ʽ
				else if (data_top[i].getVcontent().equals(DataIOconstant.DATE))
				{
					String content = itemSumTableAndCol;
					String datef = data_top[i].getDateformat();
					if (datef == null)
					{
						datef = (String) DateFormatEnum.Y_M_D.value();
					}
					if (StringUtils.isBlank(content))
					{
						topLine.append(getStringStr4TopLine("", data_top[i]));
					}
					else
					{
						topLine.append(getStringStr4TopLine(formatDate(new UFDate(content), datef), data_top[i]));
					}

				}
				// ��ӷָ��
				if (topSeperater != null && i < data_top.length - 1)
				{
					topLine.append(topSeperater);
				}
				// �Ƿ��������
				// ��ߵ��߼���Ա��ػ�������ע��
//				if (i < data_top.length - 1 && !line )
//				{
//					topLine.append(crlf);
//				}
			}

		}
		catch (Exception ex)
		{
			// throw ex;
		}
		return topLine.toString();
	}

	private FormatItemVO[] getFormatItemVOs()
	{
		if (getIntfaceInfs() != null && (getReadIndex() + 1) <= getIntfaceInfs().length)
		{
			if (getIntfaceInfs()[getReadIndex()] != null)
			{
				return (FormatItemVO[]) getIntfaceInfs()[getReadIndex()].getTableVO(DataIOconstant.HR_DATAINTFACE_B);
			}
		}

		return null;
	}

	@Override
	protected void outPutHead() throws IOException
	{
		if (raf == null)
		{
			return;
		}
		if (getIntfaceInfs() == null || (getReadIndex() + 1) > getIntfaceInfs().length || getIntfaceInfs()[getReadIndex()] == null)
		{
			return;
		}
		HrIntfaceVO itfVO = (HrIntfaceVO) getIntfaceInfs()[getReadIndex()].getParentVO();
		if (itfVO.getIouthead() == null || itfVO.getIouthead().equals(BooleanEnum.NO.value()))
		{
			return;
		}

		// �����ͷ
		FormatItemVO[] vos = getFormatItemVOs();
		StringBuilder headline = new StringBuilder();

		// ѭ��������Ŀ����
		// ��Ŀ�ָ��
		// ��λ��
		boolean isAdjust = (itfVO.getIheadadjustbody() != null && (itfVO.getIheadadjustbody().equals(BooleanEnum.YES.value())));
		// �����к�
		if (isSetLnsraLength() && isAdjust)
		{
			// ��������кų��ȡ����ұ�ͷ������ʽһ�� ���ͷ���в�λ
			for (int index = 0; index < getLnsraLength(); index++)
			{
				headline.append(BLANK);
			}
			if (isNeedSeperator())
			{// ͳһʹ����Ŀ�ָ��
				// Ĭ�ϵ���Ŀ�ָ�����ȶ���һλ
				headline.append(BLANK);
			}

		}

		for (int index = 0; index < vos.length; index++)
		{
			FormatItemVO formatItemVO = vos[index];
			String name = formatItemVO.getVfieldname().trim();

			// ȡ��Ŀ�ָ���
			String topSeperater = DataIOconstant.ITEMSEPERATOR.get(formatItemVO.getVseparator());
			if (topSeperater == null)
			{
				topSeperater = "";
			};

			// ֵ��ʵ�ʿ��
			int nameLen = getStrlength(name);
			// ȷ����Ŀ�ָ��
			if (!isAdjust)
			{// ��ͷ������ʽһ��,�����һ�� ���˲��Ƿ�ͳһʹ�÷ָ��

				if (isNeedSeperator())
				{// �����һ�� ���˲��Ƿ�ͳһʹ�÷ָ��
					topSeperater = getUnifySeperator();// ͳһ��Ŀ�ָ��
				}
				else
				{
					topSeperater = DataIOconstant.ITEMSEPERATOR.get(ItemSeprtorEnum.COMMA.value());
				}
			}

			// �������У����е��������= �ж�����+ǰ�������+���������
			int fieldLen = formatItemVO.getIfldwidth().intValue(); // ���
			String includeBefore = formatItemVO.getVincludebefore();
			String includeAfter = formatItemVO.getVincludeafter();

			if (includeBefore != null)
			{
				fieldLen += getStrlength(includeBefore);
			}
			if (includeAfter != null)
			{
				fieldLen += getStrlength(includeAfter);
			}

			// ʵ�ʿ�ȳ��� �����н�ȡ
			if (fieldLen < nameLen)
			{
				// headline.append(getStringCutByByte(name, fieldLen));
				headline.append(name);

			}
			else
			{// ʵ�ʿ�Ȳ���
				if (isAdjust)
				{// �Ƿ��ͷ������ʽһ��
					// ��ͷ�ǲ��ܲ������ֵ�,���Խ����еĵ������ֶ��滻Ϊ�ո�
					String caret = formatItemVO.getVcaret();
					if (caret != null)
					{
						caret = caret.replaceAll("\\d", " ");
					}

					name = complementString(name, fieldLen, formatItemVO.getIcaretpos() == null ? (Integer) CaretposEnum.NO.value() : formatItemVO.getIcaretpos(), caret);
				}
				else
				{
					if (isNeedCaret())
					{// ͳһʹ�ò�λ����������ǰ��0���ַ���ǰ���ո�
						name = complementString(name, fieldLen, (Integer) CaretposEnum.BEFORE.value(), DataIOconstant.ITEMSEPERATOR.get(ItemSeprtorEnum.COMMA.value()));//
					}
				}// ���򲻽��в�λ
				headline.append(name);
			}
			// ��ӷָ��
			if (topSeperater != null && index < vos.length - 1)
			{
				headline.append(topSeperater);
			}
		}
		headline.append(crlf);
		raf.write(headline.toString());
		raf.flush();
	}

	@Override
	protected void outPutBody() throws IOException
	{
		if (raf == null)
		{
			return;
		}
		boolean dot = isNeedDot();
		boolean kilobit = isNeedKilobit();
		// boolean needLineNo = getParas().isOutPutLineNo();

		if (getIntfaceInfs() == null || (getReadIndex() + 1) > getIntfaceInfs().length || getIntfaceInfs()[getReadIndex()] == null)
		{
			return;
		}
		HrIntfaceVO itfVO = (HrIntfaceVO) getIntfaceInfs()[getReadIndex()].getParentVO();
		ArrayList<HashMap<String, Object>> datas = getAppModel().getResults().get(itfVO.getPk_dataio_intface());

		AppendableVO[] appendVOs = (AppendableVO[]) (getAppModel()).getBillModelMap().get(itfVO.getPk_dataio_intface()).getBodyValueVOs(AppendableVO.class.getName());

		for (int index = 0; appendVOs != null && index < appendVOs.length; index++)
		{
			StringBuilder sbd = new StringBuilder();
			// HashMap<String, Object> map = datas.get(index);
			// CircularlyAccessibleValueObject rowVO = getBillmodel()
			// .getBodyValueRowVO(index, getBodyVOName());
			// ѭ��fomatVOS������ÿһ����Ԫ��
			FormatItemVO[] vos = getFormatItemVOs();
			// if (needLineNo) {// �Ƿ���Ҫ�к�
			// sbd.append(getLineNo(index + 1));
			// if (isNeedSeperator()) {
			// sbd.append(getUnifySeperator());
			// } else {
			// sbd.append(BLANK);// ��ͳһʹ�÷ָ���������һ���ո�
			// }
			// }
			for (int temp = 0; temp < vos.length; temp++)
			{
				FormatItemVO formatItemVO = vos[temp];
				// ���д���
				if (formatItemVO.getInextline().equals(BooleanEnum.YES.toIntValue())) {
					sbd.append(crlf);
				}
				// ��ȡ��Ԫֵ
				Object value = appendVOs[index].getAttributeValue(formatItemVO.getVcontent().replace(".", ""));

				if (formatItemVO.getIfieldtype().equals(FieldTypeEnum.DEC.value()))
				{// ������

					Object b = null;
					if (value == null)
					{
						b = new BigDecimal(0.00);
					}
					else
					{
						if (value instanceof BigDecimal)
						{
							b = value;
						}
						else
						{
							b = value;
						}
					}

					value = getStringDigit(b != null ? b.toString() : "", formatItemVO, dot, kilobit);

				}
				else if (formatItemVO.getIfieldtype().equals(FieldTypeEnum.DATE.value()))
				{
					if (value == null) {
						value = "";
					} else {
						value = value.toString().substring(0, 10);
					}
					formatItemVO.setVcaret(null);
					// HR���ػ��Ķ�������Ҳ��������ڣ���ֱ�в� ���������б��̻�Ҫ������ô
					value = formatDate(new UFDate(value.toString()), formatItemVO.getDateformat());
					value = getStringStr((String)value, formatItemVO);
				}
				else if (formatItemVO.getIfieldtype().equals(FieldTypeEnum.BOO.value()))
				{

				}
				else
				{// //�ַ���
					if (value == null)
					{
						value = "";
					}
					value = getStringStr((String) value, formatItemVO);
				}

				// ��ӵ�Ԫֵ
				sbd.append(value);

				if (temp < vos.length - 1)
				{
					String topSeperater = DataIOconstant.ITEMSEPERATOR.get(formatItemVO.getVseparator());
					if (topSeperater == null)
					{
						topSeperater = "";
					}

					// �����Ŀ�ָ��
					sbd.append(topSeperater);
				}

			}
			// ���sbd
			sbd.append(crlf);
			raf.write(sbd.toString());
			raf.flush();
		}

		// for (int index = 0; index < getBillmodel().getRowCount(); index++) {
		// StringBuilder sbd = new StringBuilder();
		// CircularlyAccessibleValueObject rowVO = getBillmodel()
		// .getBodyValueRowVO(index, getBodyVOName());
		// // ѭ��fomatVOS������ÿһ����Ԫ��
		// FormatItemVO[] vos = getFormatItemVOs();
		// if(needLineNo){//�Ƿ���Ҫ�к�
		// sbd.append(getLineNo(index+1));
		// if(isNeedSeperator()){
		// sbd.append(getUnifySeperator());
		// }else{
		// sbd.append(BLANK);//��ͳһʹ�÷ָ���������һ���ո�
		// }
		// }
		// for (int temp = 0; temp < vos.length; temp++) {
		// FormatItemVO formatItemVO = vos[temp];
		//
		// // ��ȡ��Ԫֵ
		// Object obj = rowVO.getAttributeValue(
		// getFieldCode(formatItemVO.getVcontent()));
		// String value = "";
		// if(obj!=null){
		// value = rowVO.getAttributeValue(
		// getFieldCode(formatItemVO.getVcontent())).toString();
		// }
		//
		// if (formatItemVO.getIfieldtype() == 1) {//������
		// value = getStringDigit(value, formatItemVO, dot, kilobit);
		// }else {////�ַ���
		// value = getStringStr(value, formatItemVO);
		// }
		// //��ӵ�Ԫֵ
		// sbd.append(value);
		//
		// if(temp < vos.length-1){
		// //�����Ŀ�ָ��
		// sbd.append(formatItemVO.getVseparator());
		// }
		//
		//
		// }
		// //���sbd
		// sbd.append(crlf);
		// raf.write(sbd.toString());
		// raf.flush();
		// }

		// for (int index = 0; index < getBillmodel().getRowCount(); index++) {
		// StringBuilder sbd = new StringBuilder();
		// CircularlyAccessibleValueObject rowVO = getBillmodel()
		// .getBodyValueRowVO(index, getBodyVOName());
		// // ѭ��fomatVOS������ÿһ����Ԫ��
		// FormatItemVO[] vos = getFormatItemVOs();
		// if(needLineNo){//�Ƿ���Ҫ�к�
		// sbd.append(getLineNo(index+1));
		// if(isNeedSeperator()){
		// sbd.append(getUnifySeperator());
		// }else{
		// sbd.append(BLANK);//��ͳһʹ�÷ָ���������һ���ո�
		// }
		// }
		// for (int temp = 0; temp < vos.length; temp++) {
		// FormatItemVO formatItemVO = vos[temp];
		//
		// // ��ȡ��Ԫֵ
		// Object obj = rowVO.getAttributeValue(
		// getFieldCode(formatItemVO.getVcontent()));
		// String value = "";
		// if(obj!=null){
		// value = rowVO.getAttributeValue(
		// getFieldCode(formatItemVO.getVcontent())).toString();
		// }
		//
		// if (formatItemVO.getIfieldtype() == 1) {//������
		// value = getStringDigit(value, formatItemVO, dot, kilobit);
		// }else {////�ַ���
		// value = getStringStr(value, formatItemVO);
		// }
		// //��ӵ�Ԫֵ
		// sbd.append(value);
		//
		// if(temp < vos.length-1){
		// //�����Ŀ�ָ��
		// sbd.append(formatItemVO.getVseparator());
		// }
		//
		//
		// }
		// //���sbd
		// sbd.append(crlf);
		// raf.write(sbd.toString());
		// raf.flush();
		// }
	}

	/**
	 * �õ�ĳһ�е��к�
	 */
	// protected String getLineNo(int lineNo){
	// String str= String.valueOf(lineNo);
	// if(isSetLnsraLength()){
	// str=complementString(String.valueOf(lineNo), getLnsraLength(),
	// DataIOconstant.BEFORECARET, getLnsraCaret()) ;
	// }
	// return str;
	// }
	/**
	 * �Ƿ����ñ�־�г���
	 *
	 * @return
	 */
	protected boolean isSetLnsraLength()
	{
		// int dot = ((HrIntfaceVO)getIntfaceInf().getParentVO()).getIiflnsra();
		int dot = 0;
		return (dot == 1) ? true : false;
	}

	/**
	 * �õ���־�г���
	 *
	 * @return
	 */
	protected int getLnsraLength()
	{
		// return ((HrIntfaceVO)getIntfaceInf().getParentVO()).getLnlength();
		return 20;

	}

	/**
	 * �õ���־�в�λ��
	 *
	 * @return
	 */
	protected String getLnsraCaret()
	{
		// return ((HrIntfaceVO)getIntfaceInf().getParentVO()).getLncaret();
		return "";

	}
}