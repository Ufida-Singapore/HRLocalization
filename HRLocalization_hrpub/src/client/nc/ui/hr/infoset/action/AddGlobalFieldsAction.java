package nc.ui.hr.infoset.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import nc.bs.framework.common.NCLocator;
import nc.funcnode.ui.action.INCAction;
import nc.itf.hr.infoset.IInfoSet;
import nc.ui.hr.uif2.action.HrAction;

/***************************************************************************
 * ���HR�����Ǳ��ػ�������Ŀͨ���ֶΰ�ť<br>
 * Created on 2018-10-02 23:23:23pm<br>
 * @author Ethan Wu
 ***************************************************************************/
public class AddGlobalFieldsAction extends HrAction {

	private static final long serialVersionUID = -8228947221930560397L;

	public AddGlobalFieldsAction() {
		super();
		
		//TODO: ���콨����ť���ƺ������Ķ����ֶ�
		this.setBtnName("Global");
		
		putValue(INCAction.CODE, "GlobalFields");
		putValue(Action.SHORT_DESCRIPTION, "Add System Preset Fields for Global Requirement");
	}
	
	@Override
	public void doAction(ActionEvent e) throws Exception {
		// TODO ǰ��У���ж��Ƿ��Ѿ�����
		NCLocator.getInstance().lookup(IInfoSet.class).addLocalizationFields("GLOBAL");
	}
}
