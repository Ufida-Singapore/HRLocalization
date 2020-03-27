package nc.ui.wa.paydata.action;

import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.Action;
import javax.swing.KeyStroke;

import org.apache.commons.lang.StringUtils;

import nc.bs.framework.common.NCLocator;
import nc.funcnode.ui.action.INCAction;
import nc.hr.utils.ResHelper;
import nc.itf.uap.busibean.ISysInitQry;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.pub.beans.UIDialog;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.wa.pub.PeriodStateVO;
import nc.vo.wa.pub.WaLoginVOHelper;
import nc.vo.wa.pub.WaState;
import nc.vo.wa.pub.WadataState;

/**
 * ����
 *
 * @author zhangg
 *
 */
public class PayAction extends PayDataBaseAction{

	private static final long serialVersionUID = 1L;

	public PayAction(){
		super();
		putValue(INCAction.CODE, "PayAction");
		setBtnName(ResHelper.getString("60130paydata","060130paydata0343")/*@res "����"*/);
		//setDisplayHotKey("f");
		putValue(Action.SHORT_DESCRIPTION, ResHelper.getString("60130paydata","060130paydata0343")/*@res "����"*/+"(Ctrl+Alt+P)");
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_P, Event.CTRL_MASK+Event.ALT_MASK));
	}

	@Override
	public void doActionForExtend(ActionEvent e) throws Exception {

		PeriodStateVO periodStateVO = (PeriodStateVO)getEditor().getValue();

		UFDate payDate = periodStateVO.getCpaydate();
		String payComment = periodStateVO.getVpaycomment();

		if ((payDate == null ) && (payComment == null || payComment.trim().length() < 1)) {
			if (showYesNoMessage(ResHelper.getString("60130paydata","060130paydata0489")/*@res "û�����뷢�����ںͷ���ԭ������˴β����룬�Ժ󽫲����޸ģ�Ҫ������"*/) != UIDialog.ID_YES) {//
				return;
			}
		} else if (payDate == null ) {
			if (showYesNoMessage(ResHelper.getString("60130paydata","060130paydata0345")/*@res "û�����뷢�����ڣ�����˴β����룬�Ժ󽫲����޸ģ�Ҫ������"*/) != UIDialog.ID_YES) {// "û�����뷢�����ڣ�����˴β����룬�Ժ󽫲����޸ģ�Ҫ������"
				return;
			}
		}

		else if (payComment == null || payComment.trim().length() < 1) {
			if (showYesNoMessage(ResHelper.getString("60130paydata","060130paydata0346")/*@res "û�����뷢��˵��������˴β����룬�Ժ󽫲����޸ģ�Ҫ������"*/) != UIDialog.ID_YES) {// "û�����뷢��˵��������˴β����룬�Ժ󽫲����޸ģ�Ҫ������"
				return;
			}
		}

		// н����ĿԤ��
		String keyName = ResHelper.getString("60130paydata","060130paydata0343")/*@res "����"*/;
		String[] files = getPaydataManager().getAlterFiles(keyName);
		showAlertInfo(files);

		getPeriodVO().setCpaydate(payDate);
		//���Ӳ����ж��Ƿ�н�ʷ������ڱ���	add by weiningc 20200203 start
		UFBoolean paraString = NCLocator.getInstance().lookup(ISysInitQry.class).getParaBoolean(super.getWaLoginVO().getPk_org(), "HRWA100");
		if(paraString != null && paraString.booleanValue() && StringUtils.isBlank(payComment)) {
			ExceptionUtils.wrappBusinessException("Payment date is blank, check please.");
		} else {
			getPeriodVO().setVpaycomment(payComment);
		}
		//end

		getPaydataManager().onPay();

	}
	/**
	 *
	 * @author zhangg on 2009-12-1
	 * @see nc.ui.wa.paydata.action.PayDataBaseAction#getEnableStateSet()
	 */
	@Override
	public Set<WaState> getEnableStateSet() {

		if(waStateSet == null){
			waStateSet = new HashSet<WaState>();
			waStateSet.add(WaState.CLASS_IS_APPROVED);
			waStateSet.add(WaState.CLASS_WITHOUT_PAY);
		}
		return waStateSet;
	}


	@Override
	protected boolean isActionEnable() {
		boolean enable = super.isActionEnable();
		if (enable) {
			//����Ƕ�η��ŵĻ��ܷ�������ˢ�¡���ѯ����ӡ������Ť��������

			//end
			Set<WadataState> set = getEnableDataStateSet();
			if (set != null) {
				enable = set.contains(getWadataState());
			}
		}
		return enable
				&& !WaLoginVOHelper.isMultiClass(getWaContext().getWaLoginVO());
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		super.doAction(e);
		putValue(HrAction.MESSAGE_AFTER_ACTION, ResHelper.getString("60130paydata","060130paydata0518")/*@res "���Ų����ɹ���"*/);
	}

}
