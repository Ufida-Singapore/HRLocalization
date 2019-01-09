package nc.ui.bd.holiday.view;

import nc.hr.utils.ResHelper;
import nc.ui.hr.formula.variable.AbstractContentCreator;
import nc.ui.hr.formula.variable.Content;

/**
 * ���������
 * ��Ա������Ϣ��item������
 * @author
 */
public class HolidayPsnjobContentCreator extends
				AbstractContentCreator
{

	//ƽ̨�� �粻��ʹ����Ϣ������ֻ��д������������ķ�ʽ
	@Override
	public Content[] createContents(Object... params) {
		Content[] contents = new Content[1];
		Content jobGradeContent = new Content();
		jobGradeContent.setContentName("Job Grade");
		jobGradeContent.setColName("JOBGRADE");
		jobGradeContent.setDescription("Job Grade");
		jobGradeContent.setRefModelClass(nc.ui.om.ref.JobGradeRefModel2.class.getName());
		contents[0]=jobGradeContent;
		return contents;
	}

}