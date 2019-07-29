package nc.ui.wa.datainterface.action;

import java.awt.event.ActionEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Action;

import nc.funcnode.ui.action.INCAction;
import nc.hr.utils.ResHelper;
import nc.ui.hr.datainterface.FileParas;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.uif2.UIState;
import nc.ui.wa.datainterface.DefaultImporter;
import nc.ui.wa.datainterface.ImportStrategy;
import nc.ui.wa.datainterface.TxtImporter;
import nc.ui.wa.datainterface.XlsImporter;
import nc.ui.wa.datainterface.model.DataIOAppModel;
import nc.ui.wa.datainterface.view.DataIOPanel;
import nc.ui.wa.datainterface.view.DefaultDataIOHeadPanel;
import nc.vo.hr.datainterface.AggHrIntfaceVO;
import nc.vo.hr.datainterface.FileTypeEnum;
import nc.vo.hr.datainterface.HrIntfaceVO;
import nc.vo.pub.BusinessException;
import nc.vo.wa.datainterface.DataInterfaceVo;

import org.apache.commons.lang.StringUtils;

/**
 *
 * @author: xuanlt
 * @date: 2010-1-14 ����09:17:33
 * @since: eHR V6.0
 * @�߲���:
 * @�߲�����:
 * @�޸���:
 * @�޸�����:
 */
public class InDataAction extends HrAction
{
	private DefaultDataIOHeadPanel ioHeadPanel;
	private DataIOPanel ioMainPanel;

	public InDataAction()
	{
		super();
		this.setBtnName(ResHelper.getString("6013bnkitf","06013bnkitf0048")/*@res "��ʾ"*/);
		this.putValue(INCAction.CODE, "ShowData");
		this.putValue(Action.SHORT_DESCRIPTION, ResHelper.getString("6013bnkitf","06013bnkitf0049")/*@res "��ʾ(Ctrl+U)"*/);
	}

	/**
	 * @author xuanlt on 2010-1-14
	 * @see nc.ui.uif2.NCAction#doAction(java.awt.event.ActionEvent)
	 */
	@Override
	public void doAction(ActionEvent e) throws Exception
	{
		ImportStrategy strategy = createImporter();
		if (strategy != null)
		{
			strategy.importFromFile();
			ArrayList<HashMap<String, Object>> objs = strategy.getDatas();
			//��֤��Ա����������Ķ�Ӧ��ϵ by wangqim
			//patch to v636 wangqim  NCdp205217332
			String[] psndoccodes = new String[objs.size()];
			Map<String,String> tempMap = new HashMap<String, String>();
			for(int i=0;i<objs.size();i++){
				String psncode = (String)objs.get(i).get("bd_psndoccode");
				String value = tempMap.get(psncode);
				if(value == null){
					tempMap.put(psncode, psncode);
				}else{
					//20150925 shenliangc Ϊ������ӣ�ҵ����ʾ���ﴦ��
					throw new BusinessException(ResHelper.getString("6013datainterface","06013datainterface0125")/*@res "���������д�����Ա������ͬ�����ݣ�"*/);
				}
				if(StringUtils.isBlank(psncode)){
					//20150925 shenliangc Ϊ������ӣ�ҵ����ʾ���ﴦ��
					throw new BusinessException(ResHelper.getString("6013datainterface","06013datainterface0126")/*@res "���������д�����Ա����Ϊ�գ�"*/);
				}
				psndoccodes[i] = psncode;
			}
			List<String[]> psndoc_code_names = ((DataIOAppModel)getModel()).queryPsndocBycode(psndoccodes);
			//20150925 shenliang Ϊ����ͬѧ��ӡ�ƴװSQL֮ǰ��Ҫ��Ա���������пա� 
			Map<String,String> map = new HashMap<String, String>();
			if(psndoc_code_names!= null && !psndoc_code_names.isEmpty()){
				for(String[] str : psndoc_code_names){
					map.put(str[0], str[1]);
				}
			}

			//У��
			for(HashMap<String, Object> hashMap : objs){
				String psndocCode = (String)hashMap.get("bd_psndoccode");
				String psndocName = (String)hashMap.get("bd_psndocname");
				psndocName = StringUtils.isBlank(psndocName) ? "" : psndocName;
				//20150925 shenliang Ϊ����ͬѧ��ӡ�ƴװSQL֮ǰ��Ҫ��Ա���������пա� 
				if(!map.isEmpty()&&!psndocName.equals(map.get(psndocCode))){
					//20150925 shenliangc Ϊ������ӣ�ҵ����ʾ���ﴦ��
					//�����У�� modify by weiningc 20190213 start
//					throw new BusinessException(MessageFormat.format(ResHelper.getString("6013datainterface","06013datainterface0127")/*@res "������{0}����Ա��������벻��Ӧ�����飡"*/, psndocCode));
					//end
				}
			}
			
			AggHrIntfaceVO aggVO = (AggHrIntfaceVO) this.getModel().getSelectedData();

			((DataIOAppModel) getModel()).getResults().put(((HrIntfaceVO) aggVO.getParentVO()).getPk_dataio_intface(), objs);

			AggHrIntfaceVO[] aggVOs = new AggHrIntfaceVO[]
			{ aggVO };
			for (int i = 0; aggVOs != null && i < aggVOs.length; i++)
			{
				if (aggVOs[i] != null)
				{
					HrIntfaceVO vo = (HrIntfaceVO) aggVOs[i].getParentVO();
					BillModel billModel = ((DataIOAppModel) getModel()).getBillModelMap().get(vo.getPk_dataio_intface());
					if (billModel == null)
					{
						return;
					}
					BillItem[] items = billModel.getBodyItems();
					
					for (int j = 0; objs != null && j < objs.size(); j++){
						// Ϊ�кŸ�ֵ
						objs.get(j).put("otherrownum", "" + (j + 1));
					}

					for (int k = 0; items != null && k < items.length; k++)
					{
						items[k].setEdit(true);
					}
					
					billModel.setBodyDataVO(this.getVos(objs));
					
					((DataIOAppModel) getModel()).getResults().put(vo.getPk_dataio_intface(), objs);
				}

			}
			getModel().setUiState(UIState.EDIT);
			putValue(HrAction.MESSAGE_AFTER_ACTION, ResHelper.getString("6013bnkitf", "06013bnkitf0148"));
		}
	}
	
	private DataInterfaceVo[] getVos(ArrayList<HashMap<String, Object>> objs){
		DataInterfaceVo[] vos = null;
		if (objs != null)
		{
			vos = new DataInterfaceVo[objs.size()];
			for (int i = 0; i < vos.length; i++) {
				vos[i] = new DataInterfaceVo(objs.get(i));
			}
		}
		return vos;
	}


	private ImportStrategy createImporter() {
		DefaultImporter importer = null;
		Object obj = this.getModel().getSelectedData();
		if (obj == null) {
			return null;
		}
		HrIntfaceVO headvo = (HrIntfaceVO) ((AggHrIntfaceVO) obj).getParentVO();

		if (headvo.getIfiletype().equals(FileTypeEnum.TXT.value())) {
			importer = new TxtImporter();

		} else {// excel
			importer = new XlsImporter();
		}
		FileParas paras = getIoHeadPanel().getParasConfig();
		paras.setOutputHead(headvo.getIouthead() == 0 ? true : false);
		paras.setHeadAjtBodyFmat(headvo.getIheadadjustbody() == 0 ? true
				: false);
		importer.setParas(paras);
		importer.setIntfaceInf((AggHrIntfaceVO) obj);
		return importer;

	}

	public DefaultDataIOHeadPanel getIoHeadPanel()
	{
		return ioHeadPanel;
	}

	public void setIoHeadPanel(DefaultDataIOHeadPanel ioHeadPanel)
	{
		this.ioHeadPanel = ioHeadPanel;
	}

	public DataIOPanel getIoMainPanel()
	{
		return ioMainPanel;
	}

	public void setIoMainPanel(DataIOPanel ioMainPanel)
	{
		this.ioMainPanel = ioMainPanel;
	}

}