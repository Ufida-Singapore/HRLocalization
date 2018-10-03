package nc.impl.hr.infoset.localization;

import java.util.ArrayList;
import java.util.Arrays;

import nc.itf.hr.infoset.localization.IAddLocalizationFieldStrategy;
import nc.vo.hr.infoset.InfoItemVO;
import nc.vo.hr.infoset.InfoSetVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;

/***************************************************************************
 * HR���ػ���������������HR��Ŀ��Ҫ����Ա������Ϣ�ֶ�<br>
 * Created on 2018-10-02 18:36:01pm
 * @author Ethan Wu
 ***************************************************************************/

public class MalaysiaFieldStrategy extends AddFieldAbstractStrategy implements IAddLocalizationFieldStrategy {

	public MalaysiaFieldStrategy() {
		defdocMap = getDefdocList();
	}
	
	@Override
	public InfoSetVO[] addLocalField(InfoSetVO[] vos) throws BusinessException {
		if (vos == null || vos.length == 0) {
			return null;
		}
		for (InfoSetVO infoSet : vos) {
			if (infoSet.getInfoset_code()
					.equals(IAddLocalizationFieldStrategy.PERSONAL_INFO_TABLE)) {
				InfoItemVO[] bodyVOs = infoSet.getInfo_item();
				
				// У�鿴�Ƿ��ֶ��Ѿ��ӹ����ж��������жϱ����Ƿ���m_��ͷ
				for (InfoItemVO bvo : bodyVOs) {
					if (bvo.getItem_code().startsWith("m_")) {
						throw new BusinessException("Malaysia field already synced. If you face any discrepancy, please contact support.");
					}
				}
				
				ArrayList<InfoItemVO> newBodyVOsList = 
						new ArrayList<InfoItemVO>(Arrays.asList(bodyVOs));
				
				newBodyVOsList.add(addField("m_category", "Personnel Category", "��Ա���", 
						5, 20, "hrlocal-000009", "6007psn", UFBoolean.FALSE, UFBoolean.FALSE,
						newBodyVOsList.size(), 0, defdocMap.get("SEALOCAL003")));
				newBodyVOsList.add(addField("m_entrydate", "Entry Date", "�뾳����", 3, 19,
						"hrlocal-000010", "6007psn", UFBoolean.FALSE, UFBoolean.FALSE,
						newBodyVOsList.size(), 0, null));
				newBodyVOsList.add(addField("m_numberofchildren", "Number of children", "��Ů��",
						1, 8, "hrlocal-000011", "6007psn", UFBoolean.FALSE, UFBoolean.FALSE,
						newBodyVOsList.size(), 0, null));
				newBodyVOsList.add(addField("m_religion", "Religion", "�ڽ�����", 5, 20,
						"hrlocal-000012", "6007psn", UFBoolean.FALSE, UFBoolean.FALSE,
						newBodyVOsList.size(), 0, defdocMap.get("SEALOCAL004")));
				newBodyVOsList.add(addField("m_isdisabled", "Disabled", "�Ƿ�м�", 4, 1, "hrlocal-000013",
						"6007psn", UFBoolean.FALSE, UFBoolean.FALSE, newBodyVOsList.size(), 0, null));
				newBodyVOsList.add(addField("m_isspousedisabled", "Spouse Disabled", "��ż�Ƿ�м�",
						4, 1, "hrlocal-000014", "6007psn", UFBoolean.FALSE, UFBoolean.FALSE,
						newBodyVOsList.size(), 0, null));
				newBodyVOsList.add(addField("m_isspouseworking", "Spouse Working", "��ż�Ƿ���",
						4, 1, "hrlocal-000015", "6007psn", UFBoolean.FALSE, UFBoolean.FALSE,
						newBodyVOsList.size(), 0, null));
				newBodyVOsList.add(addField("m_epfgroup", "EPF Group", "EPF Group", 5, 20,
						"hrlocal-000016", "6007psn", UFBoolean.FALSE, UFBoolean.FALSE,
						newBodyVOsList.size(), 0, defdocMap.get("SEALOCAL005")));
				newBodyVOsList.add(addField("m_socsogroup", "Socso Group", "Socso Group", 5, 20,
						"hrlocal-000017", "6007psn", UFBoolean.FALSE, UFBoolean.FALSE,
						newBodyVOsList.size(), 0, defdocMap.get("SEALOCAL007")));
				newBodyVOsList.add(addField("m_eisgroup", "EIS Group", "EIS Group", 5, 20,
						"hrlocal-000018", "6007psn", UFBoolean.FALSE, UFBoolean.FALSE,
						newBodyVOsList.size(), 0, defdocMap.get("SEALOCAL008")));
				newBodyVOsList.add(addField("m_pcbgroup", "PCB Group", "PCB Group", 5, 20,
						"hrlocal-000019", "6007psn", UFBoolean.FALSE, UFBoolean.FALSE,
						newBodyVOsList.size(), 0, defdocMap.get("SEALOCAL006")));
				newBodyVOsList.add(addField("m_epfno", "EPF No.", "EPF No.", 0, 101, "hrlocal-000020",
						"6007psn", UFBoolean.FALSE, UFBoolean.FALSE, newBodyVOsList.size(), 0, null));
				newBodyVOsList.add(addField("m_socsono", "Socso No.", "Socso No.", 0, 101, "hrlocal-000021",
						"6007psn", UFBoolean.FALSE, UFBoolean.FALSE, newBodyVOsList.size(), 0, null));
				newBodyVOsList.add(addField("m_taxno", "Tax No.", "Tax No.", 0, 101, "hrlocal-000022",
						"6007psn", UFBoolean.FALSE, UFBoolean.FALSE, newBodyVOsList.size(), 0, null));
				newBodyVOsList.add(addField("m_taxbranch", "Tax Branch", "Tax Branch", 5, 20,
						"hrlocal-000023", "6007psn", UFBoolean.FALSE, UFBoolean.FALSE,
						newBodyVOsList.size(), 0, defdocMap.get("SEALOCAL009")));
				newBodyVOsList.add(addField("m_spousetaxno", "Spouse Tax No.", "Spouse Tax No.", 0, 101,
						"hrlocal-000024", "6008psn", UFBoolean.FALSE, UFBoolean.FALSE,
						newBodyVOsList.size(), 0, null));
				newBodyVOsList.add(addField("m_spousetaxbranch", "Spouse Tax Branch", "Spouse Tax Branch", 5, 20,
						"hrlocal-000025", "6007psn", UFBoolean.FALSE, UFBoolean.FALSE,
						newBodyVOsList.size(), 0, defdocMap.get("SEALOCAL009")));
				newBodyVOsList.add(addField("m_totalpayable", "Total Payable (previous employer)", "ǰ������Ӧ���ϼ�",
						2, 28, "hrlocal-000026", "6007psn", UFBoolean.FALSE, UFBoolean.FALSE,
						newBodyVOsList.size(), 8, null));
				newBodyVOsList.add(addField("m_taxexemption", "Total Tax Exemption (previous employer)", "ǰ��������˰�ϼ�",
						2, 28, "hrlocal-000027", "6007psn", UFBoolean.FALSE, UFBoolean.FALSE,
						newBodyVOsList.size(), 8, null));
				newBodyVOsList.add(addField("m_totalepf", "Total EPF (previous employer)", "ǰ������EPF�ϼ�",
						2, 28, "hrlocal-000028", "6007psn", UFBoolean.FALSE, UFBoolean.FALSE,
						newBodyVOsList.size(), 8, null));
				newBodyVOsList.add(addField("m_totaleis", "Total EIS (previous employer)", "ǰ������EIS�ϼ�",
						2, 28, "hrlocal-000029", "6007psn", UFBoolean.FALSE, UFBoolean.FALSE,
						newBodyVOsList.size(), 8, null));
				newBodyVOsList.add(addField("m_totalsocso", "Total SOCSO (previous employer)", "ǰ������SOCSO�ϼ�",
						2, 28, "hrlocal-000030", "6007psn", UFBoolean.FALSE, UFBoolean.FALSE,
						newBodyVOsList.size(), 8, null));
				newBodyVOsList.add(addField("m_totalzakat", "Total Zakat (previous employer)", "ǰ�������ڽ�˰",
						2, 28, "hrlocal-000031", "6007psn", UFBoolean.FALSE, UFBoolean.FALSE,
						newBodyVOsList.size(), 8, null));
				newBodyVOsList.add(addField("m_totalpcb", "Total PCB (previous employer)", "ǰ�����ܸ�������˰",
						2, 28, "hrlocal-000032", "6007psn", UFBoolean.FALSE, UFBoolean.FALSE,
						newBodyVOsList.size(), 8, null));
				
				infoSet.setInfo_item(newBodyVOsList.toArray(new InfoItemVO[0]));
			}
		}
		return vos;
	}
	
}
