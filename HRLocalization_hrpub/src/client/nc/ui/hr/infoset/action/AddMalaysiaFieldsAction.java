package nc.ui.hr.infoset.action;

import java.awt.Event;
import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import nc.bs.framework.common.NCLocator;
import nc.funcnode.ui.action.INCAction;
import nc.hr.utils.ResHelper;
import nc.itf.hr.infoset.IInfoSet;
import nc.ui.hr.uif2.action.HrAction;

/***************************************************************************
 * �����������������Ŀ�ֶ�<br>
 * Created on 2018-10-02 01:40:27<br>
 * @author Ethan Wu
 ***************************************************************************/
public class AddMalaysiaFieldsAction extends HrAction{

	private static final long serialVersionUID = -2861185272007518586L;

	public AddMalaysiaFieldsAction() {
		super();
		
		//TODO: ���콨����ť���ƺ������Ķ����ֶ�
		this.setBtnName("Malaysia");
		
		putValue(INCAction.CODE, "MYFields");
		putValue(Action.SHORT_DESCRIPTION, "Add System Preset Fields for Malaysia");
	}
	
	@Override
	public void doAction(ActionEvent e) throws Exception {
		NCLocator.getInstance().lookup(IInfoSet.class).addLocalizationFields("MY");
	}

}
