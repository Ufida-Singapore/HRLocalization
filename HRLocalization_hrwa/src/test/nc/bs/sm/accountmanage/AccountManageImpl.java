package nc.bs.sm.accountmanage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.common.RuntimeEnv;
import nc.bs.framework.exception.ComponentException;
import nc.bs.framework.server.util.NewObjectService;
import nc.bs.logging.Logger;
import nc.bs.ml.NCLangResOnserver;
import nc.bs.sm.accountmanage.acccheck.AccCheckConfReader;
import nc.bs.sm.accountmanage.acccheck.AccCheckConfVO;
import nc.bs.sm.accountmanage.acccheck.AccChecker;
import nc.bs.sm.data.DBInstalledModule;
import nc.bs.update.db.table.TableUpdateProcessor;
import nc.bs.update.db.verify.Verify;
import nc.itf.uap.sfapp.IAccountCreateService;
import nc.itf.uap.sfapp.IAccountManageService;
import nc.itf.uap.sfapp.ModuleInfo;
import nc.jdbc.framework.JdbcPersistenceManager;
import nc.jdbc.framework.JdbcSession;
import nc.jdbc.framework.crossdb.CrossDBConnection;
import nc.jdbc.framework.crossdb.CrossDBPreparedStatement;
import nc.jdbc.framework.exception.DbException;
import nc.md.persist.designer.service.IPublishService;
import nc.newinstall.config.ConfigKey;
import nc.newinstall.config.ModuleConfig;
import nc.newinstall.config.ModuleConfig.RelatedModule;
import nc.newinstall.data.InstalledModule;
import nc.newinstall.util.CheckTreeNode;
import nc.newinstall.util.DBInstallTreeScaner;
import nc.newinstall.util.FileUtil;
import nc.vo.pub.BusinessException;
import nc.vo.sm.accountmanage.CodeVerinfoTreeNode;
import nc.vo.sm.accountmanage.DBInstallProgress;
import nc.vo.sm.accountmanage.DBInstallSetup;

/**
 * ��������:2006-3-22
 * 
 * @author licp
 * @since 5.0
 */
public class AccountManageImpl implements IAccountManageService,
		IUpdataAccountConstant, IBLOBProcessor {
	private ThreadLocal<String> langCodeThreadLocal = new ThreadLocal<String>();
	private ThreadLocal<Boolean> doneUAPCheckThreadLocal = new ThreadLocal<Boolean>();
	private ThreadLocal<DBInstallProgress> progressThreadLocal = new ThreadLocal<DBInstallProgress>();

	private ThreadLocal<CheckTreeNode> checkTreeRootNodeThreadLocal = new ThreadLocal<CheckTreeNode>();

	private ThreadLocal<ArrayList<String>> hadInstalledModuleThreadLocal = new ThreadLocal<ArrayList<String>>();

	private ThreadLocal<ArrayList<String>> hadInstalledModuleThreadLocalOrder = new ThreadLocal<ArrayList<String>>();

	private final String ncHome = RuntimeEnv.getInstance().getProperty(
			RuntimeEnv.SERVER_LOCATION_PROPERTY);

	// ����Ķ�����Ϣ
	private List<String> dbmllist = null;

	// ��Ҫ��װ�Ĳ�Ʒ��ģ�����Ϣ

	private static final String SPLITER = "#";

	/**
     *
     */
	public AccountManageImpl() {
		super();
	}

	private void registerDataSource(String dsName) {
		InstallLogTool.log("### ��ʼע������Դ: " + dsName + " ###");
		InvocationInfoProxy.getInstance().setUserDataSource(dsName);

	}

	private IAccountCreateService getAccountCreateService()
			throws BusinessException {
		IAccountCreateService service = null;
		try {
			service = (IAccountCreateService) NCLocator.getInstance().lookup(
					IAccountCreateService.class.getName());
		} catch (ComponentException e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage());
		}
		return service;
	}

	public IUpdateAccount[] getDefaultUpdateAccount(ModuleConfig config,
			boolean isNewDisk, boolean isUpdate) {
		IUpdateAccount[] updateAccounts = ClassAdjustSupport
				.getDefaultIUpdateAccounts(isNewDisk, isUpdate);
		int count = updateAccounts == null ? 0 : updateAccounts.length;
		for (int i = 0; i < count; i++) {
			if (updateAccounts[i] instanceof AbstractUpdateAccount) {
				AbstractUpdateAccount aua = (AbstractUpdateAccount) updateAccounts[i];
				aua.setConfig(config);
				// ���ô��ݶ���langcode ��List dbmllist
				aua.setMultilanglist(dbmllist);
				if (isUpdate)
					aua.setPatchVersions(getInstalledModulePatchVersionsByCode(config
							.getCode()));
			}

		}
		return updateAccounts;
	}

	private String[] getInstalledModulePatchVersionsByCode(String moduleCode) {
		AccountInstallDAO dao = null;
		String[] versions = new String[0];
		try {
			dao = new AccountInstallDAO();
			versions = dao.getInstalledModulePatchVersionsByCode(moduleCode);
		} catch (Exception e) {
			InstallLogTool.logException(e);
		} finally {
			if (dao != null)
				dao.close();
		}
		return versions;
	}

	private Object newInstance(String moduleName, String className) {
		Object obj = null;
		try {
			if (moduleName == null) {
				obj = NewObjectService.newInstance(className);
			} else {
				obj = NewObjectService.newInstance(moduleName, className);
			}
		} catch (Exception e) {
		}
		return obj;
	}

	private IUpdateAccount[] getUpdateAccounts(ModuleConfig config) {
		ArrayList al = new ArrayList();
		InstallLogTool
				.log("=======================���ش��������:======================================================");
		String[] classNames = config.getDataUpdateClassNames();
		int count = classNames == null ? 0 : classNames.length;
		String moduleStamp = config.getModuleStamp();
		// IUpdateAccount[] updateAccounts = new IUpdateAccount[count];
		for (int i = 0; i < count; i++) {
			String className = classNames[i];
			InstallLogTool.log("����" + className);
			Object obj = newInstance(moduleStamp, className);
			if (obj == null)
				obj = newInstance(null, className);
			if (obj == null) {
				String msg = "ģ��" + config.getName() + "ע��Ĵ��������û�м��سɹ���"
						+ className;
				InstallLogTool.log(msg);
				continue;
			}
			if (obj instanceof IUpdateAccount) {
				if (obj instanceof AbstractUpdateAccount) {
					AbstractUpdateAccount aua = (AbstractUpdateAccount) obj;
					aua.setConfig(config);
					aua.setMultilanglist(dbmllist);
					// �����������,�ǲ��������� patchversion
					if (!config.isNewDisk()) {
						aua.setPatchVersions(getInstalledModulePatchVersionsByCode(config
								.getCode()));
					}
				}
				al.add(obj);
			} else {
				String msg = "ģ��"
						+ config.getName()
						+ "ע��Ĵ��������û��û��ʵ��nc.bs.sm.accountmanage.IUpdateAccount�ӿڣ�"
						+ className;
				InstallLogTool.log(msg);
				continue;
			}

		}
		InstallLogTool
				.log("=====================================================���ش�����������==========================================================");
		return (IUpdateAccount[]) al.toArray(new IUpdateAccount[0]);
	}

	private INewInstallAdjust[] getNewInstallAdjustClass(ModuleConfig config) {
		ArrayList al = new ArrayList();
		InstallLogTool
				.log("=======================�����°�װ���������:======================================================");
		//
		String[] classNames = config.getAdjustClassNames();
		int count = classNames == null ? 0 : classNames.length;
		String moduleStamp = config.getModuleStamp();
		for (int i = 0; i < count; i++) {
			String className = classNames[i];
			InstallLogTool.log("����" + className);
			Object obj = newInstance(moduleStamp, className);
			if (obj == null)
				obj = newInstance(null, className);
			if (obj == null) {
				String msg = "ģ��" + config.getName() + "ע����°�װ���������û�м��سɹ���"
						+ className;
				InstallLogTool.log(msg);
				continue;
			}
			if (obj instanceof INewInstallAdjust) {
				if (obj instanceof AbstractNewInstallAdjust) {
					AbstractNewInstallAdjust adjust = (AbstractNewInstallAdjust) obj;
					adjust.setConfig(config);
				}
				al.add(obj);
			} else {
				String msg = "ģ��"
						+ config.getName()
						+ "ע��Ĵ��������û��û��ʵ��nc.bs.sm.accountmanage.INewInstallAdjust�ӿڣ�"
						+ className;
				InstallLogTool.log(msg);
				continue;
			}

		}
		InstallLogTool
				.log("=====================================================���ش�����������==========================================================");
		return (INewInstallAdjust[]) al.toArray(new INewInstallAdjust[0]);
	}

	private IPatchInstall[] getPatchInstall(ModuleConfig config) {
		ArrayList<Object> al = new ArrayList<Object>();
		InstallLogTool
				.log("=======================���ز�������:======================================================");
		String[] classNames = config.getPatchinstallclassnemes();
		int count = classNames == null ? 0 : classNames.length;
		String moduleStamp = config.getModuleStamp();
		for (int i = 0; i < count; i++) {
			String className = classNames[i];
			InstallLogTool.log("����" + className);
			Object obj = newInstance(moduleStamp, className);
			if (obj == null)
				obj = newInstance(null, className);
			if (obj == null) {
				String msg = "ģ��" + config.getName() + "ע��Ĳ�����װ������û�м��سɹ���"
						+ className;
				InstallLogTool.log(msg);
				continue;
			}
			if (obj instanceof IPatchInstall) {
				al.add(obj);
			} else {
				String msg = "ģ��"
						+ config.getName()
						+ "ע��Ĵ��������û��û��ʵ��nc.bs.sm.accountmanage.IPatchInstall�ӿڣ�"
						+ className;
				InstallLogTool.log(msg);
				continue;
			}

		}
		InstallLogTool
				.log("=====================================================���ش�����������==========================================================");
		return (IPatchInstall[]) al.toArray(new IPatchInstall[0]);
	}

	/**
	 * ��ȡ����װ�Ĳ�Ʒ��
	 */
	public CheckTreeNode getProductTree(String dsName) throws BusinessException {
		AccountInstallDAO dao = null;
		ArrayList<String> al = null;
		try {
			dao = new AccountInstallDAO(dsName);
			al = dao.getInstalledMoudleCodes();
		} catch (Exception e) {
		} finally {
			if (dao != null)
				dao.close();
		}
		if (al == null) {
			al = new ArrayList<String>();
		}
		return DBInstallTreeScaner.getBCManageTreeWithHidden(
				ISysConstant.ncHome, al);

	}

	public InstalledModule[] getInstalledModules(String dsName)
			throws BusinessException {
		AccountInstallDAO dao = null;
		try {
			if (dsName == null)
				dao = new AccountInstallDAO();
			else
				dao = new AccountInstallDAO(dsName);
			return dao.getInstalledModules();
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage());
		} finally {
			if (dao != null)
				dao.close();
		}

	}

	/**
	 * ���ݱ����ѯ�Ѱ�ת��ģ�飬���û�а�װ������null
	 * 
	 * @deprecated
	 */
	public InstalledModule getInstalledModuleByCode(String dsName,
			String moduleCode) throws BusinessException {
		AccountInstallDAO dao = null;
		try {
			if (dsName == null)
				dao = new AccountInstallDAO();
			else
				dao = new AccountInstallDAO(dsName);
			return dao.getInstalledModuleByCode(moduleCode);
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage());
		} finally {
			if (dao != null)
				dao.close();
		}
	}

	/**
	 * @deprecated
	 * @param dsName
	 * @param config
	 * @return
	 * @throws BusinessException
	 */
	private InstalledModule getInstalledModuleByInfo(String dsName,
			ModuleConfig config) throws BusinessException {
		AccountInstallDAO dao = null;
		try {
			if (dsName == null)
				dao = new AccountInstallDAO();
			else
				dao = new AccountInstallDAO(dsName);
			return dao.getInstalledModuleByInfo(config);
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage());
		} finally {
			if (dao != null)
				dao.close();
		}

	}

	private static CheckTreeNode[] getNeedInstallProduct(CheckTreeNode root) {
		return getNeedInstallNode(root);
	}

	// TODO::�˴���Map������װ�Ĳ�Ʒ�ָ��࣬ÿ������¶Բ㼶������......Tomorrow
	private static CheckTreeNode[] getNeedInstallNode(CheckTreeNode node) {
		ArrayList al = new ArrayList();
		int count = node.getChildCount();
		for (int i = 0; i < count; i++) {
			CheckTreeNode child = (CheckTreeNode) node.getChildAt(i);
			if (child.isSelected()) {
				ModuleInfo minfo = child.getModuleInfo();
				if (minfo != null && minfo.isNewDisk())
					al.add(child);
			}
		}
		return (CheckTreeNode[]) al.toArray(new CheckTreeNode[0]);

	}

	private static CheckTreeNode[] getNeedInstallPatch(CheckTreeNode root) {
		ArrayList al = new ArrayList();
		Enumeration enumer = root.breadthFirstEnumeration();
		while (enumer.hasMoreElements()) {
			CheckTreeNode node = (CheckTreeNode) enumer.nextElement();
			if (node.isLeaf() && node.isSelected()) {
				ModuleInfo minfo = node.getModuleInfo();
				if (minfo != null && !minfo.isNewDisk())
					al.add(node);
			}

		}

		return (CheckTreeNode[]) al.toArray(new CheckTreeNode[0]);

	}

	/**
	 * �������ݿ�
	 */
	public void doInstallDB(DBInstallSetup installSetup)
			throws BusinessException {
		// ע�ᵱǰ�̵߳�����Դ
		String dsName = installSetup.getDsName();
		registerDataSource(dsName);

		initInstalledInfo();
		//
		doneUAPCheckThreadLocal.set(false);
		langCodeThreadLocal.set(installSetup.getLangCode());
		//
		String progressID = installSetup.getProgressID();
		DBInstallProgress progress = InstallProgressCenter.getInstance()
				.getDBInstallProgress(progressID);
		progressThreadLocal.set(progress);
		// �������ݶ���
		dbmllist = installSetup.getDbmlcode();
		Logger.error("AccountManage.doInstallDB:need installed language: dbmllist===="
				+ dbmllist);
		// ��ʼ��װ
		CheckTreeNode root = installSetup.getProductScriptTreeRoot();
		checkTreeRootNodeThreadLocal.set(root);
		CheckTreeNode[] needInstallProducts = getNeedInstallProduct(root);
		CheckTreeNode[] needInstallPatchs = getNeedInstallPatch(root);

		// ��¼��װ˳��
		initInstalledInfoOrder();
		// ��¼���̰�װ˳��
		writeProductInstallOrder(needInstallProducts);

		// ��¼������װ˳��
		writePatchInstallOrder(needInstallPatchs);

		int count = needInstallProducts.length;
		int patchCount = needInstallPatchs.length;
		progress.productCount = count + (patchCount > 0 ? 1 : 0);

		// ��¼��Ʒ��װ˳��
		ArrayList<IAllUpdateAccount> needClasses = WholeUpdateClassFactory
				.getNeedClasses();
		/** ����ǰ������� */
		for (int i = 0; i < needClasses.size(); i++) {
			IAllUpdateAccount iAllUpdateAccount = needClasses.get(i);
			if (iAllUpdateAccount instanceof IAllUpdateAccount2) {
				IAllUpdateAccount2 update = (IAllUpdateAccount2) iAllUpdateAccount;
				Logger.error("ִ����������ǰ������" + update.getClass().getName());
				getAccountCreateService().wholeClassAdjust_RequiresNew(update,
						1);
			}

		}

		for (int i = 0; i < count; i++) {
			synchronized (InstallProgressCenter.class) {
				progress.currProductIndex = i;
				// progress.currProductName =
				// needInstallProducts[i].getModuleConfig().getName();
				progress.currProductName = NCLangResOnserver.getInstance()
						.getStrByID(
								"sfapp",
								needInstallProducts[i].getModuleInfo()
										.getModulecode());
				progress.currModuleIndex = 0;
			}
			doInstallProduct(needInstallProducts[i]);
		}
		if (patchCount > 0) {
			progress.currProductIndex = count;
			progress.currProductName = "ģ�鲹��";
			progress.currModuleIndex = 0;
			doInstallPatchs(needInstallPatchs);

		}
		progress.currProductIndex = progress.productCount;

		/** ������������ */
		for (int i = 0; i < needClasses.size(); i++) {
			IAllUpdateAccount iAllUpdateAccount = needClasses.get(i);
			Logger.error("ִ�����������������" + iAllUpdateAccount.getClass().getName());
			getAccountCreateService().wholeClassAdjust_RequiresNew(
					iAllUpdateAccount, 2);
		}
	}

	/**
	 * ��¼��Ʒ��װ˳��
	 * 
	 * @param needInstallProducts
	 */
	private void writeProductInstallOrder(CheckTreeNode[] needInstallProducts) {
		Logger.error("firewolf product install order");
		if (needInstallProducts != null && needInstallProducts.length > 0) {
			for (int i = 0; i < needInstallProducts.length; i++) {
				CheckTreeNode product = needInstallProducts[i];
				LogProductInstallOrder(product);
			}
		}
	}

	/**
	 * ��¼������װ˳��
	 * 
	 * @param needInstallProducts
	 */
	private void writePatchInstallOrder(CheckTreeNode[] needInstall) {
		Logger.error("firewolf patch install order");
		if (needInstall != null && needInstall.length > 0) {
			for (int i = 0; i < needInstall.length; i++) {
				CheckTreeNode product = needInstall[i];
				logInfo(product, i);
			}
		}
	}

	private void LogProductInstallOrder(CheckTreeNode product) {
		ModuleConfig config = product.getModuleInfo().getLvlConfig(0);
		Logger.error("order:  product=====code: " + config.getCode()
				+ "  name: " + config.getName() + "  level: "
				+ config.getLevel() + "  version: " + config.getVersion());
		CheckTreeNode[] needInstallModules = getNeedInstallNode(product);
		int count = needInstallModules.length;
		for (int i = 0; i < count; i++) {
			logInfo(needInstallModules[i], i);
		}
	}

	private void logInfo(CheckTreeNode module, int index) {
		ModuleInfo minfo = module.getModuleInfo();
		Integer[] levels = minfo.getLvlmap().keySet().toArray(new Integer[0]);
		Arrays.sort(levels);
		for (Integer integer : levels) {
			ModuleConfig lvlconfig = minfo.getLvlmap().get(integer);
			if (isHadInstalledLog(lvlconfig))
				continue;
			RelatedModule[] relatedModules = lvlconfig.getRelatedModules();
			for (int i = 0, count = relatedModules == null ? 0
					: relatedModules.length; i < count; i++) {
				String relatedCode = relatedModules[i].getCode();
				CheckTreeNode node = getNodeByCode(relatedCode);
				if (node != null && node.isSelected()) {
					if (node.isLeaf()) {// Ŀǰ���ڵ���Ҷ�ӣ�˵����ģ��򲹶���
//						logInfo(node, index++);
					} else if (node.getModuleInfo().isNewDisk()) {// ����Ҷ�ӵ����̣�Ӧ���ǲ�Ʒ
//						LogProductInstallOrder((node)); 
					}
				}
			}
			Logger.error("order:  module====code:" + lvlconfig.getCode()
					+ "  name: " + lvlconfig.getName() + "  level: "
					+ lvlconfig.getLevel() + "  hycode: "
					+ lvlconfig.getHyCode() + "  version: "
					+ lvlconfig.getVersion());
			markInstalledOrder(lvlconfig);
		}
	}

	private boolean isHadInstalledLog(ModuleConfig config) {
		ArrayList<String> al = hadInstalledModuleThreadLocalOrder.get();
		String code = config.getCode();
		String version = config.getVersion();
		String devlvl = String.valueOf(config.getLevel());
		boolean isNewDisk = config.isNewDisk();

		if (isNewDisk) {
			code = code + "_new" + devlvl;
		} else {
			code = code + "_patch" + devlvl;
		}
		code += version + SPLITER + config.getHyCode();
		if (al.contains(code)) {
			return true;
		} else {
			return false;
		}
	}

	private void markInstalledOrder(ModuleConfig config) {
		ArrayList<String> al = hadInstalledModuleThreadLocalOrder.get();

		String code = config.getCode();
		String version = config.getVersion();
		String devlvl = String.valueOf(config.getLevel());
		boolean isNewDisk = config.isNewDisk();

		if (isNewDisk) {
			code = code + "_new" + devlvl;
		} else {
			code = code + "_patch" + devlvl;
		}
		code += version + SPLITER + config.getHyCode();

		al.add(code);
	}

	private void initInstalledInfoOrder() throws BusinessException {
		hadInstalledModuleThreadLocalOrder.set(null);
		ArrayList hadInstalledModule = new ArrayList();
		hadInstalledModuleThreadLocalOrder.set(hadInstalledModule);
		InstalledModule[] installedMosules = getInstalledModules(null);

		if (installedMosules != null && installedMosules.length > 0) {
			String keycode = null;
			for (InstalledModule imo : installedMosules) {
				keycode = imo.getCode() + "_new" + imo.getLevel()
						+ imo.getVersion() + SPLITER + imo.getHycode();
				hadInstalledModuleThreadLocalOrder.get().add(keycode);
			}

		}

	}

	private void initInstalledInfo() throws BusinessException {
		hadInstalledModuleThreadLocal.set(null);
		ArrayList hadInstalledModule = new ArrayList();
		hadInstalledModuleThreadLocal.set(hadInstalledModule);
		InstalledModule[] installedMosules = getInstalledModules(null);

		if (installedMosules != null && installedMosules.length > 0) {
			String keycode = null;
			for (InstalledModule imo : installedMosules) {
				keycode = imo.getCode() + "_new" + imo.getLevel()
						+ imo.getVersion() + SPLITER + imo.getHycode();
				hadInstalledModuleThreadLocal.get().add(keycode);
			}
		}
	}

	private void doInstallPatchs(CheckTreeNode[] patchs)
			throws BusinessException {
		int count = patchs == null ? 0 : patchs.length;
		DBInstallProgress progress = (DBInstallProgress) progressThreadLocal
				.get();
		progress.moduleCount = count;
		for (int i = 0; i < count; i++) {
			doInstallModule(patchs[i], i);
		}

	}

	private void doInstallProduct(CheckTreeNode product)
			throws BusinessException {
		Logger.error("ȷ�������������doInstallProduct");
		// ��Ʒ�Ļ�����Ϊ��0��
		ModuleConfig config = product.getModuleInfo().getLvlConfig(0);
		CheckTreeNode[] needInstallModules = getNeedInstallNode(product);
		int count = needInstallModules.length;
		DBInstallProgress progress = (DBInstallProgress) progressThreadLocal
				.get();
		progress.moduleCount = count;
		for (int i = 0; i < count; i++) {
			doInstallModule(needInstallModules[i], i);
		}

		String dsName = InvocationInfoProxy.getInstance().getUserDataSource();
		getAccountCreateService().updateInstalledModuleVersion_RequiresNew(
				dsName, config);

	}

	private void createDB(TableUpdateProcessor tup, String moduleCode,
			String dbScriptPath) throws BusinessException {
		try {
			// ������ʷԭ�򣬶���10��ͷ��ģ�飬�ڽ����ݿ�ʱ��ͳһʹ�á�10����Ϊģ���
			if (moduleCode.trim().startsWith("10"))
				moduleCode = "10";

			// �������ݿ�������У��
			Verify verify = new Verify();
			InstallLogTool.log("��ʼ�������ݿ�������У��, moduleCode =" + moduleCode
					+ ", dbScriptPath=" + dbScriptPath);
			int verifyResult = verify.verify(moduleCode, dbScriptPath);
			if (verifyResult != 0)
				throw new Exception(NCLangResOnserver.getInstance().getStrByID(
						"102003", "UPP102003-000010")/*
													 * @res
													 * "�����ݿ�������У��ʧ�ܣ����ܽ���������ԭ���뿴��̨��־"
													 */);

			InstallLogTool.log("��ʼ�������ݿ�ṹ���� , moduleCode =" + moduleCode
					+ ", dbScriptPath=" + dbScriptPath);
			// ��ṹ����
			tup.process(moduleCode, dbScriptPath, null);
			InstallLogTool.log("���ݿ�ṹ��������");
		} catch (Exception e) {
			InstallLogTool.logException(e);
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage());
		}

	}

	private boolean isHadInstalled(ModuleConfig config) {
		ArrayList<String> al = hadInstalledModuleThreadLocal.get();
		String code = config.getCode();
		String version = config.getVersion();
		String devlvl = String.valueOf(config.getLevel());
		boolean isNewDisk = config.isNewDisk();

		if (isNewDisk) {
			code = code + "_new" + devlvl;
		} else {
			code = code + "_patch" + devlvl;
		}
		code += version + SPLITER + config.getHyCode();
		if (al.contains(code)) {
			return true;
		} else {
			return false;
		}
	}

	private void markInstalled(ModuleConfig config) {
		ArrayList<String> al = hadInstalledModuleThreadLocal.get();

		String code = config.getCode();
		String version = config.getVersion();
		String devlvl = String.valueOf(config.getLevel());
		boolean isNewDisk = config.isNewDisk();

		if (isNewDisk) {
			code = code + "_new" + devlvl;
		} else {
			code = code + "_patch" + devlvl;
		}
		code += version + SPLITER + config.getHyCode();

		al.add(code);
	}

	private CheckTreeNode getNodeByCode(String code) {
		CheckTreeNode node = null;
		CheckTreeNode root = checkTreeRootNodeThreadLocal.get();
		Enumeration enumer = root.depthFirstEnumeration();
		while (enumer.hasMoreElements()) {
			CheckTreeNode temp = (CheckTreeNode) enumer.nextElement();
			ModuleInfo minfo = temp.getModuleInfo();
			if (minfo != null && code.equals(minfo.getModulecode())) {
				Map<Integer, ModuleConfig> lvlmap = minfo.getLvlmap();
				for (Integer level : lvlmap.keySet()) {
					ModuleConfig moduleConfig = lvlmap.get(level);
					if (!moduleConfig.isVisible() || temp.isSelected()) {// ��ѡ�л���������ģ��,����Ϊ�Ѿ�ѡ�ˣ�
						node = temp;
						return node;
					}
				}
			}
		}
		return node;
	}

	private void doInstallModule(CheckTreeNode module, int progressIndex)
			throws BusinessException {
		Logger.error("ȷ�������������");

		ModuleInfo minfo = module.getModuleInfo();
		String moduleCode = minfo.getModulecode();

		Integer[] levels = minfo.getLvlmap().keySet().toArray(new Integer[0]);
		Arrays.sort(levels);

		for (Integer integer : levels) {
			ModuleConfig lvlconfig = minfo.getLvlmap().get(integer);
			if (!lvlconfig.isVisible() || minfo.getLevelSelect(integer)) {
				if (isHadInstalled(lvlconfig))
					continue;

				RelatedModule[] relatedModules = lvlconfig.getRelatedModules();
				for (int i = 0, count = relatedModules == null ? 0
						: relatedModules.length; i < count; i++) {
					String relatedCode = relatedModules[i].getCode();
					CheckTreeNode node = getNodeByCode(relatedCode);
					if (node != null) {
						if (node.isLeaf()) {// Ŀǰ���ڵ���Ҷ�ӣ�˵����ģ��򲹶���
//							doInstallModule(node, progressIndex++);
						} else if (node.getModuleInfo().isNewDisk()) {// ����Ҷ�ӵ����̣�Ӧ���ǲ�Ʒ
//							doInstallProduct(node);
						}
					}
				}

				CheckTreeNode parentNode = (CheckTreeNode) module.getParent();
				if (parentNode != null) {
					DBInstallProgress progress = (DBInstallProgress) progressThreadLocal
							.get();

					synchronized (InstallProgressCenter.class) {
						progress.currProductName = NCLangResOnserver
								.getInstance().getStrByID(
										"sfapp",
										parentNode.getModuleInfo()
												.getModulecode());
					}
				}
				doinstallSingleModule(progressIndex, lvlconfig);
			}

		}

	}

	private void doinstallSingleModule(int progressIndex, ModuleConfig config)
			throws BusinessException {

		String moduleName = config.getName();
		String moduleVersion = config.getVersion();
		String moduleCode = config.getCode();
		// liuxing0���
		boolean isNewDisk = true;
		boolean isUpdate = false;
		boolean isNeedSqltrans = true; // �Ƿ���Ҫ����SqlTrans�����ļ���insert���ı��update���
		int[] types = null;// ��һ�����飬type[0]������ʾ�ǰ�װ���ͣ�0��ʾ��װ��1��ʾ������type[1]��ʾ��װ���ǰģ����Ϣ����sm_product_version�Ĳ���:0��ʾinsert��1��ʾupdate
		// �¼�һ��type[2]������ʾ����business�е������ǲ���Ҫ����SqlTrans��ת����update������ֱ��ת����update��0��ʾǰ�ߣ�1��ʾ����

		// =========����һ��Ϊ���԰�װ���룬�Ƚ���������
		// if(config!=null){
		// System.out.println("XXXXXXXXXXX����־�����Ѿ���װ��ģ��"+config.getCode()+"##Level##  "+config.getLevel());
		// Logger.error("XXXXXXXXXXX����־�����Ѿ���װ��ģ��"+config.getCode()+"##Level##  "+config.getHyCode());
		// String dsName =
		// InvocationInfoProxy.getInstance().getUserDataSource();
		// getAccountCreateService().updateInstalledModuleVersion_RequiresNew(dsName,
		// config);
		// markInstalled(config);
		// return;
		// }
		// =========�����ǲ��԰�װ�ĺ�����=============

		DBInstallProgress progress = (DBInstallProgress) progressThreadLocal
				.get();
		synchronized (InstallProgressCenter.class) {
			progress.currModuleIndex = progressIndex;
			// progress.currModuleName = module.getModuleConfig().getName();
			progress.currModuleName = NCLangResOnserver.getInstance()
					.getStrByID("sfapp", config.getCode());
			progress.currSubstep = "";
			progress.subStepPercent = 0;
			progress.currDetail = "";

		}

		Logger.error("start install module : modulecode=" + moduleCode
				+ ",level=" + config.getLevel() + ",version=" + moduleVersion
				+ ",modulename=" + moduleName + ",configPath="
				+ config.getConfigFilePath());
		//
		InstallLogTool.log("��ʼ��װģ��:" + moduleName + ",   moduleCode='"
				+ moduleCode + "'");
		// ��ѯ��ǰ�İ汾��
		// String strCode = moduleCode;
		// if(moduleCode.startsWith("10")){
		// String preGeneCode = config.getPreviousGenerationCode();
		// if (preGeneCode != null && preGeneCode.trim().length() > 0) {
		// strCode = preGeneCode;
		// }
		// }
		// InstalledModule installedModule = null;
		DBInstalledModule installedModule = null;

		installedModule = getInstalledModuleInfoByInfo(null, config);
		if (installedModule == null) {
			String preGeneCode = config.getPreviousGenerationCode();
			if (preGeneCode != null && preGeneCode.trim().length() > 0) {
				StringTokenizer st = new StringTokenizer(preGeneCode, ",");
				while (st.hasMoreTokens()) {
					String tCode = st.nextToken().trim();
					installedModule = getInstalledModuleInfoByCode(null, tCode);
					if (installedModule != null) {
						break;
					}
				}
			}
		}

		// liuxing0ע��
		// if (installedModule == null)
		// installedModule = getInstalledModuleByInfo(null, config);
		// boolean isNewDisk = config.isNewDisk();
		// boolean isUpdate = !isNewDisk || installedModule != null;
		// liuxing0�¼�
		isNewDisk = config.isNewDisk();
		types = getInstallAndOperType(null, config); // ��ȡ��װ���ͺͶ����ݿ�Ĳ�������
		isUpdate = types[0] == 1 ? true : false;
		isUpdate = isUpdate || (installedModule != null);
		isNeedSqltrans = (types[2] == 1) ? false : true;
		isNeedSqltrans = isUpdate;

		if (isUpdate) {
			Logger.error("update module" + config.getCode());
			InstallLogTool.log("��ģ��:'" + moduleName + "'����������װ");
		} else {
			Logger.error("install module" + config.getCode());
			InstallLogTool.log("��ģ��:'" + moduleName + "'�����°�װ");
		}
		// ������鲻��ע��
		// if (moduleCode.startsWith("00") && isNewDisk && installedModule !=
		// null && !config.getVersion().equals(installedModule.getVersion())
		if (moduleCode.startsWith("00") && isNewDisk
				&& !doneUAPCheckThreadLocal.get()) {
			doneUAPCheckThreadLocal.set(true);
			String dsName = InvocationInfoProxy.getInstance()
					.getUserDataSource();
			doneUAPCheck(dsName, config);
		}
		// ��װ��Ʒ���ݿ�ṹ����ǰ�������
		if (!isUpdate) {
			progress.currSubstep = nc.bs.ml.NCLangResOnserver.getInstance()
					.getStrByID("accountmanager", "UPPaccountmanager-000012")/*
																			 * @res
																			 * "���ڽ����°�װ�Ĵ������"
																			 */;
			InstallLogTool.log(progress.currSubstep);
			INewInstallAdjust[] adjusts = getNewInstallAdjustClass(config);
			for (int i = 0; i < adjusts.length; i++) {
				if (adjusts[i] != null) {
					progress.currDetail = nc.bs.ml.NCLangResOnserver
							.getInstance().getStrByID("accountmanager",
									"UPPaccountmanager-000001")/*
																 * @res "��ʼ����"
																 */
							+ getString(adjusts[i].getClass().getName());
					InstallLogTool.log(progress.currDetail);
					try {
						getAccountCreateService().newInstallAdjust_RequiresNew(
								adjusts[i], moduleVersion, AT_BEFORE_UPDATE_DB);
					} catch (Exception e) {
						InstallLogTool.logException(e);
						throw new BusinessException(e.getMessage());
					}
				}
			}
		}

		//
		// DBInstallProgress progress = (DBInstallProgress)
		// progressThreadLocal.get();
		//
		IUpdateAccount[] defaultUpdateAccounts = getDefaultUpdateAccount(
				config, isNewDisk, isUpdate);

		int count = defaultUpdateAccounts == null ? 0
				: defaultUpdateAccounts.length;

		// AU�����õĴ������
		if (count > 0 && isNeedHandleAU(config)) {
			progress.currSubstep = nc.bs.ml.NCLangResOnserver.getInstance()
					.getStrByID("accountmanager", "UPPaccountmanager-000000")/*
																			 * @res
																			 * "���ڽ������ݿ�ṹ����ǰ��Ĭ�ϴ������"
																			 */;
			InstallLogTool.log(progress.currSubstep);
			progress.subStepPercent = 3;
			for (int i = 0; i < count; i++) {
				if (defaultUpdateAccounts[i] != null) {
					progress.currDetail = nc.bs.ml.NCLangResOnserver
							.getInstance().getStrByID("accountmanager",
									"UPPaccountmanager-000001")/*
																 * @res "��ʼ����"
																 */
							+ getString(defaultUpdateAccounts[i].getClass()
									.getName());
					InstallLogTool.log(progress.currDetail);
					try {
						String installedModuleVerison = "";
						if (installedModule != null)
							installedModuleVerison = installedModule
									.getVersion();
						getAccountCreateService().classAdjust_RequiresNew(
								defaultUpdateAccounts[i],
								installedModuleVerison, moduleVersion,
								AT_BEFORE_UPDATE_DB);
					} catch (Exception e) {
						InstallLogTool.logException(e);
						throw new BusinessException(e.getMessage());
					}
				}

			}
		}
		IUpdateAccount[] updateAccounts = getUpdateAccounts(config);
		// ���ݿ�ṹ����ǰ�Ĵ������
		if (isUpdate && updateAccounts.length > 0) {
			progress.currSubstep = nc.bs.ml.NCLangResOnserver.getInstance()
					.getStrByID("accountmanager", "UPPaccountmanager-000002")/*
																			 * @res
																			 * "���ڽ������ݿ�ṹ����ǰ�Ĵ������"
																			 */;
			InstallLogTool.log(progress.currSubstep);
			progress.subStepPercent = 5;
			for (int i = 0; i < updateAccounts.length; i++) {
				if (updateAccounts[i] != null) {
					progress.currDetail = nc.bs.ml.NCLangResOnserver
							.getInstance().getStrByID("accountmanager",
									"UPPaccountmanager-000001")/*
																 * @res "��ʼ����"
																 */
							+ getString(updateAccounts[i].getClass().getName());
					InstallLogTool.log(progress.currDetail);
					try {
						getAccountCreateService().classAdjust_RequiresNew(
								updateAccounts[i],
								installedModule.getVersion(), moduleVersion,
								AT_BEFORE_UPDATE_DB);
					} catch (Exception e) {
						InstallLogTool.logException(e);
						throw new BusinessException(e.getMessage());
					}
				}
			}
		}
		// ���ݿ�ṹ����
		TableUpdateProcessor tup = new TableUpdateProcessor();
		String configFile = config.getConfigFilePath();
		File configFileParent = new File(configFile).getParentFile();
		String dbCreateName = config
				.getUnicodeString(ConfigKey.CONFIG_DB_CREATE_SCRIPT);
		if (!isNullStr(dbCreateName)) {
			String dbScriptPath = configFileParent.getPath() + "/"
					+ dbCreateName + "/";
			dbScriptPath = dbScriptPath.replace('\\', '/');
			if (new File(dbScriptPath).exists()) {
				progress.currSubstep = nc.bs.ml.NCLangResOnserver.getInstance()
						.getStrByID("accountmanager",
								"UPPaccountmanager-000003")/*
															 * @res
															 * "���ڽ������ݿ�ṹ����"
															 */;
				progress.subStepPercent = 10;
				progress.currDetail = nc.bs.ml.NCLangResOnserver.getInstance()
						.getStrByID("accountmanager",
								"UPPaccountmanager-000004")/*
															 * @res "��ṹ�ű�·��:"
															 */
						+ getString(dbScriptPath);
				try {

					createDB(tup, moduleCode, dbScriptPath);
				} catch (BusinessException e) {
					Logger.error(e.getMessage(), e);
					throw e;
				}

				// ϵͳ������ʱ������˱�ṹ��������ɺ���Ҫ��ձ��棬�����п��ܵ���DAO������ʱ���ֶβ������µģ�ִ��ʧ��
				JdbcPersistenceManager.clearAllTableInfo();

			}
			// �����ֵ�����
			// 2010-5-6 ��ʱȥ�������ֵ���������Ԫ�����Ƿ�����ȫȡ���ٶ��Ƿ�ʹ��
			// String dataDictScriptPath = dbScriptPath + "SQLSERVER";
			// if(new File(dataDictScriptPath).exists()){
			// progress.currSubstep =
			// nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("accountmanager","UPPaccountmanager-000005")/*@res
			// "���ڽ��������ֵ�����"*/;
			// progress.subStepPercent = 20;
			// progress.currDetail =
			// nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("accountmanager","UPPaccountmanager-000006")/*@res
			// "�����ֵ�ű�·��:"*/ + getString(dataDictScriptPath);
			// getAccountCreateService().installDataDict_RequiresNew(dataDictScriptPath);
			// }
		}
		// ��װ��Ʒ���ݳ�ʼ��ǰ�������
		if (!isUpdate) {
			progress.currSubstep = nc.bs.ml.NCLangResOnserver.getInstance()
					.getStrByID("accountmanager", "UPPaccountmanager-000012")/*
																			 * @res
																			 * "���ڽ����°�װ�Ĵ������"
																			 */;
			InstallLogTool.log(progress.currSubstep);
			INewInstallAdjust[] adjusts = getNewInstallAdjustClass(config);
			for (int i = 0; i < adjusts.length; i++) {
				if (adjusts[i] != null) {
					progress.currDetail = nc.bs.ml.NCLangResOnserver
							.getInstance().getStrByID("accountmanager",
									"UPPaccountmanager-000001")/*
																 * @res "��ʼ����"
																 */
							+ getString(adjusts[i].getClass().getName());
					InstallLogTool.log(progress.currDetail);
					try {
						getAccountCreateService().newInstallAdjust_RequiresNew(
								adjusts[i], moduleVersion,
								AT_BEFORE_UPDATE_DATA);
					} catch (Exception e) {
						InstallLogTool.logException(e);
						throw new BusinessException(e.getMessage());
					}
				}
			}
		}

		// AU�д������
		if (count > 0 && isNeedHandleAU(config)) {
			for (int i = 0; i < count; i++) {
				progress.currSubstep = nc.bs.ml.NCLangResOnserver.getInstance()
						.getStrByID("accountmanager",
								"UPPaccountmanager-000007")/*
															 * @res
															 * "���ڽ�����������ǰ��Ĭ�ϴ������"
															 */;
				InstallLogTool.log(progress.currSubstep);
				progress.subStepPercent = 23;
				if (defaultUpdateAccounts[i] != null) {
					progress.currDetail = nc.bs.ml.NCLangResOnserver
							.getInstance().getStrByID("accountmanager",
									"UPPaccountmanager-000001")/*
																 * @res "��ʼ����"
																 */
							+ getString(defaultUpdateAccounts[i].getClass()
									.getName());
					InstallLogTool.log(progress.currDetail);
					try {
						String installedModuleVerison = "";
						if (installedModule != null)
							installedModuleVerison = installedModule
									.getVersion();
						getAccountCreateService().classAdjust_RequiresNew(
								defaultUpdateAccounts[i],
								installedModuleVerison, moduleVersion,
								AT_BEFORE_UPDATE_DATA);
					} catch (Exception e) {
						InstallLogTool.logException(e);
						throw new BusinessException(e.getMessage());
					}
				}

			}
		}

		// ��������ǰ�Ĵ������
		if (isUpdate && updateAccounts.length > 0) {
			progress.currSubstep = nc.bs.ml.NCLangResOnserver.getInstance()
					.getStrByID("accountmanager", "UPPaccountmanager-000008")/*
																			 * @res
																			 * "���ڽ�����������ǰ�Ĵ������"
																			 */;
			InstallLogTool.log(progress.currSubstep);
			progress.subStepPercent = 25;
			for (int i = 0; i < updateAccounts.length; i++) {
				if (updateAccounts[i] != null) {
					progress.currDetail = nc.bs.ml.NCLangResOnserver
							.getInstance().getStrByID("accountmanager",
									"UPPaccountmanager-000001")/*
																 * @res "��ʼ����"
																 */
							+ getString(updateAccounts[i].getClass().getName());
					InstallLogTool.log(progress.currDetail);
					try {
						getAccountCreateService().classAdjust_RequiresNew(
								updateAccounts[i],
								installedModule.getVersion(), moduleVersion,
								AT_BEFORE_UPDATE_DATA);
					} catch (Exception e) {
						InstallLogTool.logException(e);
						throw new BusinessException(e.getMessage());

					}

				}
			}
		}

		// ���ݿ��ɾ����
		// v502��ȥ��ɾ���еĲ���������ɾ���û����ӵ��ֶΣ�2008/1/19
		// if (isUpdate && !isNullStr(dbCreateName)) {
		// try {
		// tup.process_delcolumn();
		// } catch (Exception e1) {
		// e1.printStackTrace();
		// }
		// }

		// ��������
		progress.currSubstep = nc.bs.ml.NCLangResOnserver.getInstance()
				.getStrByID("accountmanager", "UPPaccountmanager-000009")/*
																		 * @res
																		 * "���ڽ�����������"
																		 */;
		progress.subStepPercent = 30;
		Logger.error("��ʼ��������");
		doDataUpdate(config, isUpdate, isNeedSqltrans);
		Logger.error("����������AU�������" + configFile);
		// AU�г�ʼ�����ݺ�������
		if (count > 0 && isNeedHandleAU(config)) {

			for (int i = 0; i < count; i++) {
				progress.currSubstep = nc.bs.ml.NCLangResOnserver.getInstance()
						.getStrByID("accountmanager",
								"UPPaccountmanager-000010")/*
															 * @res
															 * "���ڽ��������������Ĭ�ϴ������"
															 */;
				InstallLogTool.log(progress.currSubstep);
				progress.subStepPercent = 93;
				if (defaultUpdateAccounts[i] != null) {
					progress.currDetail = nc.bs.ml.NCLangResOnserver
							.getInstance().getStrByID("accountmanager",
									"UPPaccountmanager-000001")/*
																 * @res "��ʼ����"
																 */
							+ getString(defaultUpdateAccounts[i].getClass()
									.getName());
					InstallLogTool.log(progress.currDetail);
					try {
						String installedModuleVerison = "";
						if (installedModule != null)
							installedModuleVerison = installedModule
									.getVersion();
						getAccountCreateService().classAdjust_RequiresNew(
								defaultUpdateAccounts[i],
								installedModuleVerison, moduleVersion,
								AT_AFTER_UPDATE_DATA);
					} catch (Exception e) {
						InstallLogTool.logException(e);
						throw new BusinessException(e.getMessage());
					}
				}

			}
		}
		Logger.error("����������������");
		// ����������Ĵ������
		if (isUpdate && updateAccounts.length > 0) {

			progress.currSubstep = nc.bs.ml.NCLangResOnserver.getInstance()
					.getStrByID("accountmanager", "UPPaccountmanager-000011")/*
																			 * @res
																			 * "���ڽ�������������Ĵ������"
																			 */;
			InstallLogTool.log(progress.currSubstep);
			progress.subStepPercent = 95;
			for (int i = 0; i < updateAccounts.length; i++) {
				if (updateAccounts[i] != null) {
					progress.currDetail = nc.bs.ml.NCLangResOnserver
							.getInstance().getStrByID("accountmanager",
									"UPPaccountmanager-000001")/*
																 * @res "��ʼ����"
																 */
							+ getString(updateAccounts[i].getClass().getName());
					InstallLogTool.log(progress.currDetail);
					try {
						getAccountCreateService().classAdjust_RequiresNew(
								updateAccounts[i],
								installedModule.getVersion(), moduleVersion,
								AT_AFTER_UPDATE_DATA);
					} catch (Exception e) {
						InstallLogTool.logException(e);
						throw new BusinessException(e.getMessage());

					}

				}

			}
		}
		progress.subStepPercent = 98;

		// ִ���°�װ�Ĵ������
		if (!isUpdate) {
			progress.currSubstep = nc.bs.ml.NCLangResOnserver.getInstance()
					.getStrByID("accountmanager", "UPPaccountmanager-000012")/*
																			 * @res
																			 * "���ڽ����°�װ�Ĵ������"
																			 */;
			InstallLogTool.log(progress.currSubstep);
			INewInstallAdjust[] adjusts = getNewInstallAdjustClass(config);
			for (int i = 0; i < adjusts.length; i++) {
				if (adjusts[i] != null) {
					progress.currDetail = nc.bs.ml.NCLangResOnserver
							.getInstance().getStrByID("accountmanager",
									"UPPaccountmanager-000001")/*
																 * @res "��ʼ����"
																 */
							+ getString(adjusts[i].getClass().getName());
					InstallLogTool.log(progress.currDetail);
					try {
						getAccountCreateService()
								.newInstallAdjust_RequiresNew(adjusts[i],
										moduleVersion, AT_AFTER_UPDATE_DATA);
					} catch (Exception e) {
						InstallLogTool.logException(e);
						throw new BusinessException(e.getMessage());
					}
				}
			}
		}
		// ��¼��װ�汾��
		String dsName = InvocationInfoProxy.getInstance().getUserDataSource();
		// liuxing0ע��
		// getAccountCreateService().updateInstalledModuleVersion_RequiresNew(dsName,
		// config);
		getAccountCreateService().updateInstalledModuleInfo_RequiresNew(
				types[1], dsName, config);

		//
		markInstalled(config);
		// since 60 ע����iufo��portal��һ���һ��--------����һ�δ��룬��iufo��portal��������Դ������
		// if ("iufo".equalsIgnoreCase(moduleCode) ||
		// "99".equalsIgnoreCase(moduleCode)) {
		// IAppendProductConfService service = (IAppendProductConfService)
		// NCLocator.getInstance().lookup(IAppendProductConfService.class.getName());
		// if ("iufo".equalsIgnoreCase(moduleCode)) {
		// service.setIUFODsName(dsName);
		// }
		// // ewei since60 portal��һ�㴦��ʽ����
		// // else if("99".equalsIgnoreCase(moduleCode)){
		// // service.setNCPortalDsName(dsName);
		// // }
		// }
		//
		progress.subStepPercent = 100;
		progress.currDetail = "";
	}

	private DBInstalledModule getInstalledModuleInfoByCode(String dsName,
			String tCode) throws BusinessException {
		AccountInstallDAO dao = null;
		try {
			if (dsName == null)
				dao = new AccountInstallDAO();
			else
				dao = new AccountInstallDAO(dsName);
			return dao.getInstalledModuleInfoByCode(tCode);
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage());
		} finally {
			if (dao != null)
				dao.close();
		}
	}

	private DBInstalledModule getInstalledModuleInfoByInfo(String dsName,
			ModuleConfig config) throws BusinessException {
		DBInstalledModule installedModule = null;
		AccountInstallDAO dao = null;
		try {
			if (dsName == null)
				dao = new AccountInstallDAO();
			else
				dao = new AccountInstallDAO(dsName);
			installedModule = dao.getInstalledModuleInfoByInfo(config);
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			return installedModule;
			// throw new BusinessException(e.getMessage());
		} finally {
			if (dao != null)
				dao.close();
		}
		return installedModule;
	}

	/**
	 * ��ȡ�Ե�ǰģ��Ĵ���ʽ���������߰�װ�� �����������ݿ�Ĳ������ͣ�insert����update
	 * 
	 * @param config
	 * @return int[]
	 *         type[0]������ʾ�ǰ�װ���ͣ�0��ʾ��װ��1��ʾ������type[1]��ʾ��װ���ǰģ����Ϣ����sm_product_version�Ĳ���
	 *         :0��ʾinsert��1��ʾupdate
	 *         type[2]������ʾ��insert���ı��updateҪ��Ҫͨ��SqlTrans�����ļ���ȷ��
	 *         ��0��ʾ��Ҫ��1��ʾ����Ҫֱ�ӱ��update
	 * @throws BusinessException
	 */
	private int[] getInstallAndOperType(String dsName, ModuleConfig config)
			throws BusinessException {
		String currLevel = config.getLevel() + "";
		int[] types = new int[] { 0, 0, 0 };
		AccountInstallDAO dao = null;
		try {
			if (dsName == null)
				dao = new AccountInstallDAO();
			else
				dao = new AccountInstallDAO(dsName);
			ArrayList<String> levelsStr = dao.getdevLevelsStrByCode(config
					.getCode());
			if (null == levelsStr || 0 == levelsStr.size()) {// ���ݿ���û�е�ǰģ�����Ϣ����װ��insert
				types[0] = 0;
				types[1] = 0;
				types[2] = 0;
			} else if (1 == levelsStr.size()) {// ���ݿ�����һ����¼
				if (null == levelsStr.get(0)) {// �Ѿ���װ����63�ģ�������insert
					types[0] = 1;
					types[1] = 0;
					types[2] = 0;
				} else if (levelsStr.get(0).equals(currLevel)) {// �Ѿ���װ����һ���͵�ǰ��װ�Ĳ����ͬ��������update��ǰlevel��
					types[0] = 1;
					types[1] = 1;
					types[2] = 0;
				} else {// �Ѿ���װ�ĺ͵�ǰ�Ĳ�ͬ��Σ���װ��insert
					types[0] = 0;
					types[1] = 0;
					types[2] = 1;
				}
			} else { // ���ݿ����Ѿ����ڶ��
				if (levelsStr.contains(null)) { // ��63�Ľṹ��������update����levelΪ��ǰlevel����Ϊnull��
					types[0] = 1;
					types[1] = 1;
					types[2] = 1;
				} else {
					if (levelsStr.contains(currLevel)) { // Ҫ��װ�Ĳ���Ѿ���װ��������update
						types[0] = 1;
						types[1] = 1;
						types[2] = 1;
					} else {// Ҫ��װ�Ĳ����ڣ���װ��insert
						types[0] = 0;
						types[1] = 0;
						types[2] = 1;
					}
				}
			}
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage());
		} finally {
			if (dao != null)
				dao.close();
		}
		return types;
	}

	private boolean isNeedHandleAU(ModuleConfig config) {

		String handleflag = config.getUnicodeString("isNeedHandleAU");
		if (handleflag != null && "N".equalsIgnoreCase(handleflag)) {
			return false;
		}

		return true;
	}

	private void doneUAPCheck(String dsName, ModuleConfig config)
			throws BusinessException {
		HashMap<String, AccCheckConfVO> hm = AccCheckConfReader
				.loadAccCheckConfigs();
		try {
			List<String> installedModule = new AccountInstallDAO(dsName)
					.getInstalledMoudleCodes();
			Iterator<String> iter = hm.keySet().iterator();
			while (iter.hasNext()) {
				String code = (String) iter.next();
				if (installedModule.contains(code)) {
					AccChecker.runChecker(hm.get(code), config);
				}
			}
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		}

	}

	private boolean isNullStr(String s) {
		return s == null || s.trim().length() == 0;
	}

	private void doDataUpdate(ModuleConfig config, boolean isUpdate,
			boolean isNeedSqlTrans) throws BusinessException {
		// String dsName =
		// InvocationInfoProxy.getInstance().getUserDataSource();
		DBInstallProgress progress = (DBInstallProgress) progressThreadLocal
				.get();
		ArrayList<String> ignoredScriptFilePath = config
				.getIgnoredScriptFilePath();
		try {
			// dao = new AccountInstallDAO();
			String configFile = config.getConfigFilePath();
			File configFileParent = new File(configFile).getParentFile();
			boolean hasDynamicTempletData = config.isHasDynTempletData();
			// ����ģ�壺
			String billTempName = config
					.getUnicodeString(ConfigKey.CONFIG_BILL_TEMPLET_SCRIPT);
			String billTempletScriptFilePath = configFileParent.getPath() + "/"
					+ billTempName + "/";
			if (!isNullStr(billTempName)) {
				if (config.isNewDisk()) {
					InstallLogTool.log("��ʼ��ʼ������ģ������:"
							+ billTempletScriptFilePath);
					progress.currSubstep = nc.bs.ml.NCLangResOnserver
							.getInstance().getStrByID("accountmanager",
									"UPPaccountmanager-000013")/*
																 * @res
																 * "���ڰ�װ����ģ��"
																 */;
					progress.subStepPercent = 30;
					progress.currDetail = nc.bs.ml.NCLangResOnserver
							.getInstance().getStrByID("accountmanager",
									"UPPaccountmanager-000014")/*
																 * @res
																 * "����ģ��ű�·��:"
																 */
							+ getString(billTempletScriptFilePath);
					// installBillTemplet(new File(billTempletScriptFilePath),
					// isUpdate, hasDynamicTempletData, ignoredScriptFilePath);
					execScript(new File(billTempletScriptFilePath),
							ignoredScriptFilePath, true);
				} else {
					InstallLogTool.log("��ʼ��װ����ģ�油���ű�:"
							+ billTempletScriptFilePath);
					progress.currSubstep = nc.bs.ml.NCLangResOnserver
							.getInstance().getStrByID("accountmanager",
									"UPPaccountmanager-000013")/*
																 * @res
																 * "���ڰ�װ����ģ��"
																 */;
					progress.subStepPercent = 30;
					progress.currDetail = nc.bs.ml.NCLangResOnserver
							.getInstance().getStrByID("accountmanager",
									"UPPaccountmanager-000014")/*
																 * @res
																 * "����ģ��ű�·��:"
																 */
							+ getString(billTempletScriptFilePath);
					execScript(new File(billTempletScriptFilePath),
							ignoredScriptFilePath, true);
					// installBillTempletPatch(new
					// File(billTempletScriptFilePath), hasDynamicTempletData);
				}
				if (isNeedInstallBlob(config, billTempName)) {
					processMouduleBlob(new File(billTempletScriptFilePath));
				}
			}
			// ��ѯģ�壺
			String queryTempletName = config
					.getUnicodeString(ConfigKey.CONFIG_QUERY_TEMPLET_SCRIPT);
			if (!isNullStr(queryTempletName)) {
				String queryTempletScriptFilePath = configFileParent.getPath()
						+ "/" + queryTempletName + "/";
				if (config.isNewDisk()) {
					InstallLogTool.log("��ʼ��ʼ����ѯģ������:"
							+ queryTempletScriptFilePath);
					progress.currSubstep = nc.bs.ml.NCLangResOnserver
							.getInstance().getStrByID("accountmanager",
									"UPPaccountmanager-000015")/*
																 * @res
																 * "���ڰ�װ��ѯģ��"
																 */;
					progress.subStepPercent = 35;
					progress.currDetail = nc.bs.ml.NCLangResOnserver
							.getInstance().getStrByID("accountmanager",
									"UPPaccountmanager-000016")/*
																 * @res
																 * "��ѯģ��ű�·��:"
																 */
							+ getString(queryTempletScriptFilePath);
					execScript(new File(queryTempletScriptFilePath),
							ignoredScriptFilePath, true);
				} else {
					InstallLogTool.log("��ʼ��װ��ѯģ�油���ű�:"
							+ queryTempletScriptFilePath);
					progress.currSubstep = nc.bs.ml.NCLangResOnserver
							.getInstance().getStrByID("accountmanager",
									"UPPaccountmanager-000015")/*
																 * @res
																 * "���ڰ�װ��ѯģ��"
																 */;
					progress.subStepPercent = 35;
					progress.currDetail = nc.bs.ml.NCLangResOnserver
							.getInstance().getStrByID("accountmanager",
									"UPPaccountmanager-000016")/*
																 * @res
																 * "��ѯģ��ű�·��:"
																 */
							+ getString(queryTempletScriptFilePath);
					// installQuerytempletPatch(new
					// File(queryTempletScriptFilePath), hasDynamicTempletData);
					execScript(new File(queryTempletScriptFilePath),
							ignoredScriptFilePath, true);

				}
				// 2011-9-14,ewei ע�͵�����ѯģ��ҲĬ�ϰ�װblob
				// if(isNeedInstallBlob(config,queryTempletName)){
				processMouduleBlob(new File(queryTempletScriptFilePath));
				// }
			}
			// ����ģ�壺
			String reportTempletName = config
					.getUnicodeString(ConfigKey.CONFIG_REPORT_TEMPLET_SCRIPT);
			if (!isNullStr(reportTempletName)) {
				String reportTempletScriptFilePath = configFileParent.getPath()
						+ "/" + reportTempletName + "/";
				if (config.isNewDisk()) {
					InstallLogTool.log("��ʼ��ʼ������ģ������:"
							+ reportTempletScriptFilePath);
					progress.currSubstep = nc.bs.ml.NCLangResOnserver
							.getInstance().getStrByID("accountmanager",
									"UPPaccountmanager-000017")/*
																 * @res
																 * "���ڰ�װ����ģ��"
																 */;
					progress.subStepPercent = 40;
					progress.currDetail = nc.bs.ml.NCLangResOnserver
							.getInstance().getStrByID("accountmanager",
									"UPPaccountmanager-000018")/*
																 * @res
																 * "����ģ��ű�·��:"
																 */
							+ getString(reportTempletScriptFilePath);
					execScript(new File(reportTempletScriptFilePath),
							ignoredScriptFilePath, true);
				} else {
					InstallLogTool.log("��ʼ��ʼ������ģ�油���ű�:"
							+ reportTempletScriptFilePath);
					progress.currSubstep = nc.bs.ml.NCLangResOnserver
							.getInstance().getStrByID("accountmanager",
									"UPPaccountmanager-000017")/*
																 * @res
																 * "���ڰ�װ����ģ��"
																 */;
					progress.subStepPercent = 40;
					progress.currDetail = nc.bs.ml.NCLangResOnserver
							.getInstance().getStrByID("accountmanager",
									"UPPaccountmanager-000018")/*
																 * @res
																 * "����ģ��ű�·��:"
																 */
							+ getString(reportTempletScriptFilePath);
					// installReporttempletPatch(new
					// File(reportTempletScriptFilePath),
					// hasDynamicTempletData);
					execScript(new File(reportTempletScriptFilePath),
							ignoredScriptFilePath, true);
				}
				if (isNeedInstallBlob(config, reportTempletName)) {
					processMouduleBlob(new File(reportTempletScriptFilePath));
				}
			}
			// ��ӡģ�壺
			String printTempletName = config
					.getUnicodeString(ConfigKey.CONFIG_PRINT_TEMPLET_SCRIPT);
			if (!isNullStr(printTempletName)) {
				String printTempletScriptFilePath = configFileParent.getPath()
						+ "/" + printTempletName + "/";
				if (config.isNewDisk()) {
					InstallLogTool.log("��ʼ��ʼ����ӡģ������:"
							+ printTempletScriptFilePath);
					progress.currSubstep = nc.bs.ml.NCLangResOnserver
							.getInstance().getStrByID("accountmanager",
									"UPPaccountmanager-000019")/*
																 * @res
																 * "���ڰ�װ��ӡģ��"
																 */;
					progress.subStepPercent = 45;
					progress.currDetail = nc.bs.ml.NCLangResOnserver
							.getInstance().getStrByID("accountmanager",
									"UPPaccountmanager-000020")/*
																 * @res
																 * "��ӡģ��ű�·��:"
																 */
							+ getString(printTempletScriptFilePath);
					execScript(new File(printTempletScriptFilePath),
							ignoredScriptFilePath, true);
				} else {
					InstallLogTool.log("��ʼ��ʼ����ӡģ�油���ű�:"
							+ printTempletScriptFilePath);
					progress.currSubstep = nc.bs.ml.NCLangResOnserver
							.getInstance().getStrByID("accountmanager",
									"UPPaccountmanager-000019")/*
																 * @res
																 * "���ڰ�װ��ӡģ��"
																 */;
					progress.subStepPercent = 45;
					progress.currDetail = nc.bs.ml.NCLangResOnserver
							.getInstance().getStrByID("accountmanager",
									"UPPaccountmanager-000020")/*
																 * @res
																 * "��ӡģ��ű�·��:"
																 */
							+ getString(printTempletScriptFilePath);
					installPrinttempletPatch(new File(
							printTempletScriptFilePath), hasDynamicTempletData);

				}
				if (isNeedInstallBlob(config, printTempletName)) {
					processMouduleBlob(new File(printTempletScriptFilePath));
				}
			}
			// �������ͣ�
			String billTypeScriptName = config
					.getUnicodeString(ConfigKey.CONFIG_BILL_TYPE_SCRIPT);
			if (!isNullStr(billTypeScriptName)) {
				String billTypeScriptFilePath = configFileParent.getPath()
						+ "/" + billTypeScriptName + "/";
				InstallLogTool.log("��ʼ��ʼ��������������:" + billTypeScriptFilePath);
				progress.currSubstep = nc.bs.ml.NCLangResOnserver.getInstance()
						.getStrByID("accountmanager",
								"UPPaccountmanager-000021")/*
															 * @res "���ڰ�װ��������"
															 */;
				progress.subStepPercent = 50;
				progress.currDetail = nc.bs.ml.NCLangResOnserver.getInstance()
						.getStrByID("accountmanager",
								"UPPaccountmanager-000022")/*
															 * @res "�������ͽű�·��:"
															 */
						+ getString(billTypeScriptFilePath);
				// installBillType(new File(billTypeScriptFilePath), isUpdate,
				// ignoredScriptFilePath);
				execScript(new File(billTypeScriptFilePath),
						ignoredScriptFilePath, true);
				if (isNeedInstallBlob(config, billTypeScriptName)) {
					processMouduleBlob(new File(billTypeScriptFilePath));
				}
			}
			// ҵ�����ͣ�
			String busiTypeName = config
					.getUnicodeString(ConfigKey.CONFIG_BUSI_TYPE_SCRIPT);
			if (!isNullStr(busiTypeName)) {
				String busiTypeScriptFilePath = configFileParent.getPath()
						+ "/" + busiTypeName + "/";
				InstallLogTool.log("��ʼ��ʼ��ҵ����������:" + busiTypeScriptFilePath);
				progress.currSubstep = nc.bs.ml.NCLangResOnserver.getInstance()
						.getStrByID("accountmanager",
								"UPPaccountmanager-000023")/*
															 * @res "���ڰ�װҵ������"
															 */;
				progress.subStepPercent = 55;
				progress.currDetail = nc.bs.ml.NCLangResOnserver.getInstance()
						.getStrByID("accountmanager",
								"UPPaccountmanager-000024")/*
															 * @res "ҵ�����ͽű�·��:"
															 */
						+ getString(busiTypeScriptFilePath);
				execScript(new File(busiTypeScriptFilePath),
						ignoredScriptFilePath, true);
				if (isNeedInstallBlob(config, busiTypeName)) {
					processMouduleBlob(new File(busiTypeScriptFilePath));
				}
			}
			// ϵͳ���ͣ�
			String sysTypeName = config
					.getUnicodeString(ConfigKey.CONFIG_SYSTEM_TYPE_SCRIPT);
			if (!isNullStr(sysTypeName)) {
				String sysTypeScriptFilePath = configFileParent.getPath() + "/"
						+ sysTypeName + "/";
				InstallLogTool.log("��ʼ��ʼ��ϵͳ��������:" + sysTypeScriptFilePath);
				progress.currSubstep = nc.bs.ml.NCLangResOnserver.getInstance()
						.getStrByID("accountmanager",
								"UPPaccountmanager-000025")/*
															 * @res "���ڰ�װϵͳ����"
															 */;
				progress.subStepPercent = 60;
				progress.currDetail = nc.bs.ml.NCLangResOnserver.getInstance()
						.getStrByID("accountmanager",
								"UPPaccountmanager-000026")/*
															 * @res "ϵͳ���ͽű�·��:"
															 */
						+ getString(sysTypeScriptFilePath);
				execScript(new File(sysTypeScriptFilePath),
						ignoredScriptFilePath, true);

				if (isNeedInstallBlob(config, sysTypeName)) {
					processMouduleBlob(new File(sysTypeScriptFilePath));
				}
			}
			// ��Ŀ����
			String subClassName = config
					.getUnicodeString(ConfigKey.CONFIG_SUBJ_CLASS_SCRIPT);
			if (!isNullStr(subClassName)) {
				String subClassScriptFilePath = configFileParent.getPath()
						+ "/" + subClassName + "/";
				InstallLogTool.log("��ʼ��ʼ����Ŀ��������:" + subClassScriptFilePath);
				progress.currSubstep = nc.bs.ml.NCLangResOnserver.getInstance()
						.getStrByID("accountmanager",
								"UPPaccountmanager-000027")/*
															 * @res "���ڰ�װ��Ŀ����"
															 */;
				progress.subStepPercent = 65;
				progress.currDetail = nc.bs.ml.NCLangResOnserver.getInstance()
						.getStrByID("accountmanager",
								"UPPaccountmanager-000028")/*
															 * @res "��Ŀ����ű�·��:"
															 */
						+ getString(subClassScriptFilePath);
				execScript(new File(subClassScriptFilePath),
						ignoredScriptFilePath, true);

				if (isNeedInstallBlob(config, subClassName)) {
					processMouduleBlob(new File(subClassScriptFilePath));
				}
			}
			// ƾ֤ģ�壺
			String voucherTempName = config
					.getUnicodeString(ConfigKey.CONFIG_VOUCHER_TEMPLET_SCRIPT);
			if (!isNullStr(voucherTempName)) {
				String voucherTempletScriptFilePath = configFileParent
						.getPath() + "/" + voucherTempName + "/";
				InstallLogTool.log("��ʼ��ʼ��ƾ֤ģ������:"
						+ voucherTempletScriptFilePath);
				progress.currSubstep = nc.bs.ml.NCLangResOnserver.getInstance()
						.getStrByID("accountmanager",
								"UPPaccountmanager-000029")/*
															 * @res "���ڰ�װƾ֤ģ��"
															 */;
				progress.subStepPercent = 70;
				progress.currDetail = nc.bs.ml.NCLangResOnserver.getInstance()
						.getStrByID("accountmanager",
								"UPPaccountmanager-000030")/*
															 * @res "ƾ֤ģ��ű�·��:"
															 */
						+ getString(voucherTempletScriptFilePath);
				execScript(new File(voucherTempletScriptFilePath),
						ignoredScriptFilePath, true);

				if (isNeedInstallBlob(config, voucherTempName)) {
					processMouduleBlob(new File(voucherTempletScriptFilePath));
				}
			}
			// ��Ŀģ�壺
			String projectTempName = config
					.getUnicodeString(ConfigKey.CONFIG_PROJECT_TEMPLET_SCRIPT);
			if (!isNullStr(projectTempName)) {
				String projectTempletScriptFilePath = configFileParent
						.getPath() + "/" + projectTempName + "/";
				InstallLogTool.log("��ʼ��ʼ����Ŀģ������:"
						+ projectTempletScriptFilePath);
				progress.currSubstep = nc.bs.ml.NCLangResOnserver.getInstance()
						.getStrByID("accountmanager",
								"UPPaccountmanager-000031")/*
															 * @res "���ڰ�װ��Ŀģ��"
															 */;
				progress.subStepPercent = 75;
				progress.currDetail = nc.bs.ml.NCLangResOnserver.getInstance()
						.getStrByID("accountmanager",
								"UPPaccountmanager-000032")/*
															 * @res "��Ŀģ��ű�·��:"
															 */
						+ getString(projectTempletScriptFilePath);
				execScript(new File(projectTempletScriptFilePath),
						ignoredScriptFilePath, true);

				if (isNeedInstallBlob(config, projectTempName)) {
					processMouduleBlob(new File(projectTempletScriptFilePath));
				}
			}
			// ϵͳģ�壺
			String sysTempName = config
					.getUnicodeString(ConfigKey.CONFIG_SYS_TEMPLET_SCRIPT);
			if (!isNullStr(sysTempName)) {
				String sysTempletScriptFilePath = configFileParent.getPath()
						+ "/" + sysTempName + "/";
				InstallLogTool.log("��ʼ��ʼ��ϵͳģ������:" + sysTempletScriptFilePath);
				progress.currSubstep = nc.bs.ml.NCLangResOnserver.getInstance()
						.getStrByID("accountmanager",
								"UPPaccountmanager-000033")/*
															 * @res "���ڰ�װϵͳģ��"
															 */;
				progress.subStepPercent = 80;
				progress.currDetail = nc.bs.ml.NCLangResOnserver.getInstance()
						.getStrByID("accountmanager",
								"UPPaccountmanager-000034")/*
															 * @res "ϵͳģ��ű�·��:"
															 */
						+ getString(sysTempletScriptFilePath);
				execScript(new File(sysTempletScriptFilePath),
						ignoredScriptFilePath, true);

				if (isNeedInstallBlob(config, sysTempName)) {
					processMouduleBlob(new File(sysTempletScriptFilePath));
				}
			}
			// ��Ʒ���ڽű���
			String businessName = config
					.getUnicodeString(ConfigKey.CONFIG_BUSINESS_SCRIPT);
			if (!isNullStr(businessName)) {
				String businessScriptFilePath = configFileParent.getPath()
						+ "/" + businessName + "/";
				InstallLogTool.log("��ʼ��ʼ����Ʒ��������:" + businessScriptFilePath);
				progress.currSubstep = nc.bs.ml.NCLangResOnserver.getInstance()
						.getStrByID("accountmanager",
								"UPPaccountmanager-000035")/*
															 * @res "���ڰ�װ��Ʒ���ڽű�"
															 */;
				progress.subStepPercent = 85;
				progress.currDetail = nc.bs.ml.NCLangResOnserver.getInstance()
						.getStrByID("accountmanager",
								"UPPaccountmanager-000036")/*
															 * @res
															 * "��Ʒ���ڽű��ű�·��:"
															 */
						+ getString(businessScriptFilePath);
				execScript(new File(businessScriptFilePath),
						ignoredScriptFilePath, isUpdate, isNeedSqlTrans);
				InstallLogTool.logErr("================��ʼ��װģ����blob:"
						+ config.getUnicodeString(ConfigKey.CONFIG_CODE)
						+ "=============");
				processMouduleBlob(new File(businessScriptFilePath));
				InstallLogTool.logErr("================������װģ����blob:"
						+ config.getUnicodeString(ConfigKey.CONFIG_CODE)
						+ "=============");
			}
			// �˵��ű�
			String menuName = config
					.getUnicodeString(ConfigKey.CONFIG_MENU_SCRIPT);
			if (!isNullStr(menuName)) {
				String menuScriptFilePath = configFileParent.getPath() + "/"
						+ menuName + "/";
				InstallLogTool.log("��ʼ��ʼ���˵��ű�����:" + menuScriptFilePath);
				progress.currSubstep = nc.bs.ml.NCLangResOnserver.getInstance()
						.getStrByID("accountmanager",
								"UPPaccountmanager-000037")/*
															 * @res "���ڰ�װ�˵��ű�"
															 */;
				progress.subStepPercent = 90;
				progress.currDetail = nc.bs.ml.NCLangResOnserver.getInstance()
						.getStrByID("accountmanager",
								"UPPaccountmanager-000038")/*
															 * @res "�˵��ű�·��:"
															 */
						+ getString(menuScriptFilePath);
				execScript(new File(menuScriptFilePath), ignoredScriptFilePath,
						true);
				if (isNeedInstallBlob(config, menuName)) {
					processMouduleBlob(new File(menuScriptFilePath));
				}
			}
			// // Ԫ���ݽű�
			// String metadata =
			// config.getUnicodeString(ConfigKey.CONFIG_METADATA_SCRIPT);
			// if (!isNullStr(metadata)) {
			// String metadataScriptFilePath = configFileParent.getPath() + "/"
			// + metadata + "/";
			// InstallLogTool.log("��ʼ��ʼ��Ԫ���ݽű�����:" + metadataScriptFilePath);
			// progress.currSubstep =
			// nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("accountmanager","UPPaccountmanager-000075")/*@res
			// "���ڰ�װԪ���ݽű�"*/;
			// progress.subStepPercent = 93;
			// progress.currDetail =
			// nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("accountmanager","UPPaccountmanager-000076")/*@res
			// "Ԫ���ݽű�·��:"*/ + getString(metadataScriptFilePath);
			// execScript(new
			// File(metadataScriptFilePath),ignoredScriptFilePath);
			// }
			String updateMD = config.getUnicodeString("updateMetaData");
			// �������ԣ������ʶΪ������Ԫ���ݣ���ô����ʱҲ���ᷢ��,һ��Ϊfalse�Ų�����
			String publishMD = config.getUnicodeString("publishMetaData");
			IPatchInstall[] patches = getPatchInstall(config);
			if ((config.isNewDisk() && (publishMD == null || !publishMD.trim()
					.equalsIgnoreCase("false")))
					|| (updateMD != null && updateMD.trim().equalsIgnoreCase(
							"true"))) {
				try {
					InstallLogTool.log("��ʼ����Ԫ����");
					IPublishService service = (IPublishService) NCLocator
							.getInstance().lookup(
									IPublishService.class.getName());
					service.publishMetaDataForInstall(config.getModuleStamp());
				} catch (Exception e) {
					InstallLogTool.logException(e);
					throw new Exception("����Ԫ�����쳣��" + e.getMessage(), e);
				}
			} else if ((!config.isNewDisk())
					&& (patches != null && patches.length > 0)) {
				try {
					PatchInstallContext context = new PatchInstallContext();
					context.setConfig(config);
					context.setMultilanglist(dbmllist);
					context.setPatchVersions(getInstalledModulePatchVersionsByCode(config
							.getCode()));
					if (patches != null && patches.length > 0) {
						for (IPatchInstall iPatchInstall : patches) {
							iPatchInstall.pulishMetaData(context);
						}
					}
				} catch (Exception e) {
					InstallLogTool.logException(e);
					throw new Exception("��������Ԫ�����쳣��" + e.getMessage(), e);
				}
			}
			// ��������Ԫ����,������uapother��ʹ�õ�ģ�� ewei+
			InstallLogTool.log("================׼������"
					+ config.getConfigFilePath() + "����ģ��Ԫ���ݷ���=============");
			String publishModule = config.getUnicodeString("publishModule");
			processMDPublish(publishModule);
			InstallLogTool.log("================�������ù���ģ��Ԫ���ݷ���=============");

			// ���ﻯ�Ľű�
			String mlScript = config
					.getUnicodeString(ConfigKey.CONFIG_ML_SCRIPT);
			if (!isNullStr(mlScript)) {
				String mlDirPath = configFileParent.getPath() + "/" + mlScript
						+ "/";
				String defLang = (String) langCodeThreadLocal.get();
				if (defLang == null) {
					defLang = "simpchn";
				}
				mlDirPath += defLang;
				InstallLogTool.log("ִ�ж��ﻯ�ű���" + mlDirPath);
				progress.currSubstep = nc.bs.ml.NCLangResOnserver.getInstance()
						.getStrByID("accountmanager",
								"UPPaccountmanager-000039")/*
															 * @res "���ڰ�װ���ﻯ�ű�"
															 */;
				progress.subStepPercent = 95;
				progress.currDetail = nc.bs.ml.NCLangResOnserver.getInstance()
						.getStrByID("accountmanager",
								"UPPaccountmanager-000040")/*
															 * @res "���ﻯ�ű�·��:"
															 */
						+ getString(mlDirPath);
				execScript(new File(mlDirPath), ignoredScriptFilePath, true);
			}
			// �����ֵ��ʼ������
			// String ddcName =
			// config.getUnicodeString(ConfigKey.CONFIG_DDC_INITDATA);
			// if (!isNullStr(ddcName)) {
			// String ddcInitDataFilePath = configFileParent.getPath() + "/" +
			// ddcName + "/";
			// InstallLogTool.log("��ʼ��ʼ�������ֵ�����:" + ddcInitDataFilePath);
			// File ddcDataDir = new File(ddcInitDataFilePath);
			// if (ddcDataDir.exists() && ddcDataDir.isDirectory()) {
			// progress.currSubstep =
			// nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("accountmanager",
			// "UPPaccountmanager-000041")/*
			// * @res
			// * "���ڰ�װ�����ֵ��ʼ������"
			// */;
			// progress.subStepPercent = 97;
			// progress.currDetail =
			// nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("accountmanager",
			// "UPPaccountmanager-000042")/*
			// * @res
			// * "�����ֵ��ʼ������·��:"
			// */
			// + getString(ddcInitDataFilePath);
			// InitForkey ifor = new InitForkey();
			// java.io.File files[] = ddcDataDir.listFiles();
			// for (int i = 0, n = (files == null ? 0 : files.length); i < n;
			// i++) {
			// ifor.init(files[i]);
			// }
			// }
			// if(isNeedInstallBlob(config,ddcName)){
			// processMouduleBlob(new File(ddcInitDataFilePath));
			// }
			// }
			String dbmlName = config
					.getUnicodeString(ConfigKey.CONFIG_DBML_SCRIPT);
			if (!isNullStr(dbmlName)) {
				String dbmlpath = configFileParent.getPath() + "/" + dbmlName
						+ "/";
				InstallLogTool.log("��ʼ��װ���ʻ����ݶ�����Դ:" + dbmlpath);
				File dbmlDataDir = new File(dbmlpath);
				if (dbmlDataDir.exists() && dbmlDataDir.isDirectory()) {
					progress.currSubstep = NCLangResOnserver.getInstance()
							.getStrByID("accountmanager",
									"AccountManageImpl-000000")/* ���ڰ�װ���ݶ������� */;
					progress.subStepPercent = 98;
					progress.currDetail = NCLangResOnserver.getInstance()
							.getStrByID("accountmanager",
									"AccountManageImpl-000001", null,
									new String[] { getString(dbmlpath) })/*
																		 * ���ݶ���·��{
																		 * 0}
																		 */;
					Logger.error("��װ�������Ŀ¼��" + dbmlpath); /* -=notranslate=- */
					installDBML(new File(dbmlpath), isUpdate);
				}
			}
			Logger.error("��װ������ﰲװ���"); /* -=notranslate=- */
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
		}
	}

	/************************************** ����uapother��ģ��Ԫ���ݷ��� ***********************/

	private void processMDPublish(String modulestr) throws Exception {
		if (modulestr == null || modulestr.isEmpty())
			return;
		// InstallLogTool.log("��ʼ����uapotherԪ����: "+modulestr);
		InstallLogTool.log("��ʼ����ncpubԪ����: " + modulestr);
		String[] modules = modulestr.split(",");
		for (int i = 0; i < modules.length; i++) {
			try {
				// InstallLogTool.log("��ʼ����uapotherԪ����: "+modules[i]);
				InstallLogTool.log("��ʼ����ncpubԪ����: " + modules[i]);
				IPublishService service = (IPublishService) NCLocator
						.getInstance().lookup(IPublishService.class.getName());
				service.publishMetaDataForInstall(modules[i]);
			} catch (Exception e) {
				InstallLogTool.logException(e);
				throw new Exception("��ʼ����uncpubԪ���ݣ�" + modules[i]
						+ e.getMessage(), e);
			}
		}
	}

	/************************************** ����blob ****************************************/
	private boolean isNeedInstallBlob(ModuleConfig config, String dirname) {
		String blobscript = config
				.getUnicodeString(ConfigKey.CONFIG_BLOB_SCRIPT);
		if (blobscript == null || blobscript.length() == 0)
			return false;
		if (blobscript.indexOf(dirname) > -1) {
			InstallLogTool.logErr("=================" + dirname
					+ "��blob�ֶ���Ҫ������������===================");
		}
		return blobscript.indexOf(dirname) > -1;
	}

	private void processMouduleBlob(File file) throws FileNotFoundException,
			BusinessException, IOException {
		File[] files = file.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory()
						|| pathname.getName().endsWith(".blob");
			}
		});
		if (files != null && files.length > 0) {
			for (File file2 : files) {
				InstallLogTool.logErr("=================ɨ��blob�ļ�"
						+ file2.getName() + "===================");
				if (file2.isDirectory()) {
					processMouduleBlob(file2);
				} else {
					InstallLogTool.logErr("=================" + file
							+ "��blob�ֶ����ڽ�����������===================");
					BLOBParser parser = new BLOBParser("UTF-8");
					parser.setProcessor(this);
					parser.load(new FileInputStream(file2));
				}
			}
		}

	}

	@Override
	public void processBlobValue(InputStream is, int length, String tablename,
			String pkname, String pkvalue, String columname) {
		try {
			if (length == 0)
				return;
			Connection con = getSession().getConnection();
			InstallLogTool.logErr("=================blob����" + length
					+ "===================");
			InstallLogTool.logErr("=================blob����" + tablename
					+ "===================");
			InstallLogTool.logErr("=================blob������" + pkname
					+ "===================");
			InstallLogTool.logErr("=================blob����ֵ" + pkvalue
					+ "===================");
			InstallLogTool.logErr("=================blob�ֶ�����" + columname
					+ "===================");
			if (con instanceof CrossDBConnection) {
				String sql = "update " + tablename + " set " + columname
						+ "= ? where " + pkname + " = '" + pkvalue + "'";
				CrossDBPreparedStatement ps = null;
				try {
					byte[] datas = new byte[length];
					is.read(datas);
					InputStream iis = new ByteArrayInputStream(datas);
					CrossDBConnection crosscon = (CrossDBConnection) con;
					ps = (CrossDBPreparedStatement) crosscon
							.prepareStatement(sql);
					ps.setBinaryStream(1, iis);
					ps.execute();
				} catch (SQLException e) {
					Logger.error(e.getMessage(), e);
				} finally {
					try {
						if (ps != null) {
							ps.close();
						}
						if (con != null) {
							con.close();
						}
					} catch (Exception e) {
						Logger.error(e.getMessage());
					}
				}
			}
		} catch (Exception ex) {
			Logger.error(ex.getMessage(), ex);
		}
	}

	private JdbcSession getSession() throws DbException {
		String dsName = InvocationInfoProxy.getInstance().getUserDataSource();
		JdbcSession session = new JdbcSession(dsName);
		return session;
	}

	/***************************************************************************************************/

	private void installDBML(File dir, boolean isupdate)
			throws BusinessException {
		InstallLogTool.logErr("==================��ʼ��װ���ݶ���:"
				+ dir.getAbsolutePath() + "========================");
		Logger.error("dbmllist========" + dbmllist);
		if (dbmllist != null && dbmllist.size() > 0) {
			Map<String, Integer> langcodemap = new HashMap<String, Integer>();
			for (int i = 0; i < dbmllist.size(); i++) {
				langcodemap.put(dbmllist.get(i), i + 1);
			}
			Logger.error("��װ��������������£�" + langcodemap.toString()); /*
																	 * -=notranslate
																	 * =-
																	 */
			DBMLInstaller installer = new DBMLInstaller();
			installer.setLangmap(langcodemap);
			installer.exeGenScript(dir);
		}

	}

	/************************************ ewei+ �������ݿ����ű� ********************/

	private void execScript(File dir, ArrayList<String> ignorePathAL,
			boolean isupdate, boolean isNeedSqlTrans) throws BusinessException {
		if (!dir.exists())
			return;
		File[] childFiles = FileUtil.getChildDirAndSqlFiles(dir);
		int count = childFiles == null ? 0 : childFiles.length;
		childFiles = FileUtil.sortFileByName(childFiles);
		String dsName = InvocationInfoProxy.getInstance().getUserDataSource();
		for (int i = 0; i < count; i++) {
			File file = childFiles[i];
			if (file.isDirectory()) {
				execScript(file, ignorePathAL, isupdate, isNeedSqlTrans);
			} else {
				if (needExecScriptPath(file, ignorePathAL, isupdate)) {
					DBInstallProgress progress = (DBInstallProgress) progressThreadLocal
							.get();
					InstallLogTool.log(">>>��ʼִ��sql�ű�:" + file.getPath());
					Logger.error("start install sql,file path:"
							+ file.getPath());
					progress.currDetail = nc.bs.ml.NCLangResOnserver
							.getInstance().getStrByID("accountmanager",
									"UPPaccountmanager-000043")/*
																 * @res
																 * "��ʼִ��sql�ű�:"
																 */
							+ getString(file.getPath());
					ArrayList al = new ScriptFileReader().getSqlsFromFile(file);
					String[] sqls = (String[]) al.toArray(new String[0]);
					if (sqls.length > 0) {
						try {
							getAccountCreateService().execSqls_RequiresNew(
									dsName, sqls, isNeedSqlTrans);
						} catch (BusinessException e) {
							InstallLogTool.logException(e);
							Logger.error(e.getMessage(), e);
							throw e;
						}
					}
					InstallLogTool.log(">>>sql�ű�ִ�����:" + file.getPath());
				} else {
					InstallLogTool.log(">>>sql�ű��ļ�������:" + file.getPath());
				}
			}

		}
	}

	private void execScript(File dir, ArrayList<String> ignorePathAL,
			boolean isNeedSqlTrans) throws BusinessException {
		execScript(dir, ignorePathAL, false, isNeedSqlTrans);
		// if (!dir.exists())
		// return;
		// File[] childFiles = FileUtil.getChildDirAndSqlFiles(dir);
		// int count = childFiles == null ? 0 : childFiles.length;
		// childFiles = FileUtil.sortFileByName(childFiles);
		// String dsName =
		// InvocationInfoProxy.getInstance().getUserDataSource();
		// for (int i = 0; i < count; i++) {
		// File file = childFiles[i];
		// if (file.isDirectory()) {
		// execScript(file, ignorePathAL);
		// } else {
		// if (needExecScriptPath(file, ignorePathAL)) {
		// DBInstallProgress progress = (DBInstallProgress)
		// progressThreadLocal.get();
		// InstallLogTool.log(">>>��ʼִ��sql�ű�:" + file.getPath());
		// progress.currDetail =
		// nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("accountmanager",
		// "UPPaccountmanager-000043")/*
		// * @res
		// * "��ʼִ��sql�ű�:"
		// */
		// + getString(file.getPath());
		// ArrayList al = new ScriptFileReader().getSqlsFromFile(file);
		// String[] sqls = (String[]) al.toArray(new String[0]);
		// if (sqls.length > 0) {
		// try {
		// getAccountCreateService().execSqls_RequiresNew(dsName, sqls);
		// } catch (BusinessException e) {
		// InstallLogTool.logException(e);
		// Logger.error(e.getMessage(), e);
		// throw e;
		// }
		// }
		// InstallLogTool.log(">>>sql�ű�ִ�����:" + file.getPath());
		// } else {
		// InstallLogTool.log(">>>sql�ű��ļ�������:" + file.getPath());
		// }
		// }
		//
		// }

	}

	private boolean needExecScriptPath(File file, ArrayList<String> ignorePathAL) {
		return needExecScriptPath(file, ignorePathAL, false);
	}

	private boolean needExecScriptPath(File file,
			ArrayList<String> ignorePathAL, boolean isupdate) {

		String path = file.getPath();
		path = path.replace('\\', '/');
		boolean needExec = true;
		int size = ignorePathAL == null ? 0 : ignorePathAL.size();
		for (int i = 0; i < size; i++) {
			String pathModel = ignorePathAL.get(i);
			pathModel = pathModel.replace('\\', '/');
			// if(StringUtil.match(pathModel, path)){
			if (path.matches(pathModel)) {
				needExec = false;
				break;
			}
		}

		if (isupdate) {
			String ignore = "/pub_bcr_rulebase/";
			if (path.contains(ignore)) {
				needExec = false;
			}
		}

		return needExec;
	}

	private void installBillTemplet(File dir, boolean isUpdate,
			boolean isHasDynamicTempletData, ArrayList<String> ignorePathAL)
			throws BusinessException {
		// DBInstallProgress progress = (DBInstallProgress)
		// progressThreadLocal.get();
		// File[] childFiles = FileUtil.getChildDirAndSqlFiles(dir);
		// int count = childFiles == null ? 0 : childFiles.length;
		// childFiles = FileUtil.sortFileByName(childFiles);
		// if (!System.getProperties().contains(dir.getPath()))
		// System.setProperty(dir.getPath(), "0");
		// ITemplateInstall pfidu = (ITemplateInstall)
		// NCLocator.getInstance().lookup(ITemplateInstall.class.getName());
		// String dsName =
		// InvocationInfoProxy.getInstance().getUserDataSource();
		// for (int i = 0; i < count; i++) {
		// File file = childFiles[i];
		// if (file.isDirectory()) {
		// installBillTemplet(file, isUpdate, isHasDynamicTempletData,
		// ignorePathAL);
		// } else {
		// if (needExecScriptPath(file, ignorePathAL)) {
		// InstallLogTool.log(">>>��ʼִ��sql�ű�:" + file.getPath());
		// progress.currDetail =
		// nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("accountmanager",
		// "UPPaccountmanager-000044")/*
		// * @res
		// * "��ʼִ�е���ģ��sql�ű�:"
		// */
		// + getString(file.getPath());
		// ArrayList al = new ScriptFileReader().getSqlsFromFile(file);
		// String[] sqls = (String[]) al.toArray(new String[0]);
		// if (sqls.length > 0) {
		// if (isUpdate) {
		// String str = System.getProperty(dir.getPath());
		// if (str.equals("0")) {
		// String[] billTempletKeyValAry = new
		// SqlParser(sqls).getValueByCol("pk_billtemplet");
		// pfidu.installDeleteBillTemplet(billTempletKeyValAry,
		// isHasDynamicTempletData);
		// System.setProperty(dir.getPath(), "1");
		// }
		// }
		// try {
		// getAccountCreateService().execSqls_RequiresNew(dsName, sqls);
		// } catch (BusinessException e) {
		// InstallLogTool.logException(e);
		// Logger.error(e.getMessage(), e);
		// throw e;
		// }
		// }
		// InstallLogTool.log(">>>sql�ű�ִ�����:" + file.getPath());
		// } else {
		// InstallLogTool.log(">>>sql�ű�������:" + file.getPath());
		// }
		// }
		//
		// }
	}

	private void installBillTempletPatch(File dir,
			boolean isHasDynamicTempletData) throws BusinessException {
		// DBInstallProgress progress = (DBInstallProgress)
		// progressThreadLocal.get();
		// File[] childFiles = FileUtil.getChildDirAndSqlFiles(dir);
		// int count = childFiles == null ? 0 : childFiles.length;
		// childFiles = FileUtil.sortFileByName(childFiles);
		// ITemplatePacth tp = (ITemplatePacth)
		// NCLocator.getInstance().lookup(ITemplatePacth.class.getName());
		// String dsName =
		// InvocationInfoProxy.getInstance().getUserDataSource();
		// //
		// ArrayList<String> pkAl = new ArrayList<String>();
		// for (int i = 0; i < count; i++) {
		// File file = childFiles[i];
		// if (file.isDirectory()) {
		// installBillTempletPatch(file, isHasDynamicTempletData);
		// } else {
		// // if(needExecScriptPath(file, ignorePathAL)){
		// InstallLogTool.log(">>>��ʼִ��sql�ű�:" + file.getPath());
		// progress.currDetail = "��ʼ��װ����ģ�油���ű�:" + getString(file.getPath());
		// ;//
		// nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("accountmanager","UPPaccountmanager-000044")/*@res
		// // "��ʼִ�е���ģ��sql�ű�:"*/ + getString(file.getPath());
		// ArrayList al = new ScriptFileReader().getSqlsFromFile(file);
		// String[] sqls = (String[]) al.toArray(new String[0]);
		// if (sqls.length > 0) {
		// String[] billTempletKeyValAry = new
		// SqlParser(sqls).getValueByCol("pub_billtemplet", "pk_billtemplet");
		// tp.pacthDeleteBillTemplet(billTempletKeyValAry,
		// isHasDynamicTempletData);
		// try {
		// getAccountCreateService().execSqls_RequiresNew(dsName, sqls);
		// //
		// tp.ajustCustomBillTemplet(billTempletKeyValAry,isHasDynamicTempletData);
		// } catch (BusinessException e) {
		// InstallLogTool.logException(e);
		// Logger.error(e.getMessage(), e);
		// throw e;
		// }
		// pkAl.addAll(Arrays.asList(billTempletKeyValAry));
		// }
		// InstallLogTool.log(">>>sql�ű�ִ�����:" + file.getPath());
		// // }else{
		// // InstallLogTool.log(">>>sql�ű�������:" + file.getPath());
		// // }
		// }
		//
		// }
		// if (pkAl.size() > 0) {
		// InstallLogTool.log("�Ե��ݽű����е�����" + dir.getPath());
		// tp.ajustCustomBillTemplet(pkAl.toArray(new String[0]),
		// isHasDynamicTempletData);
		// }
	}

	private void installQuerytempletPatch(File dir,
			boolean isHasDynamicTempletData) throws BusinessException {
		// DBInstallProgress progress = (DBInstallProgress)
		// progressThreadLocal.get();
		// File[] childFiles = FileUtil.getChildDirAndSqlFiles(dir);
		// int count = childFiles == null ? 0 : childFiles.length;
		// childFiles = FileUtil.sortFileByName(childFiles);
		// ITemplatePacth tp = (ITemplatePacth)
		// NCLocator.getInstance().lookup(ITemplatePacth.class.getName());
		// String dsName =
		// InvocationInfoProxy.getInstance().getUserDataSource();
		//
		// ArrayList<String> pkAl = new ArrayList<String>();
		// for (int i = 0; i < count; i++) {
		// File file = childFiles[i];
		// if (file.isDirectory()) {
		// installQuerytempletPatch(file, isHasDynamicTempletData);
		// } else {
		// InstallLogTool.log(">>>��ʼִ��sql�ű�:" + file.getPath());
		// progress.currDetail = "��ʼ��װ��ѯģ�油���ű�:" + getString(file.getPath());//
		// nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("accountmanager","UPPaccountmanager-000044")/*@res
		// // "��ʼִ�е���ģ��sql�ű�:"*/
		// // +
		// // getString(file.getPath());
		// ArrayList al = new ScriptFileReader().getSqlsFromFile(file);
		// String[] sqls = (String[]) al.toArray(new String[0]);
		// if (sqls.length > 0) {
		// String[] queryTempletKeyValAry = new
		// SqlParser(sqls).getValueByCol("pub_query_templet", "id");
		// tp.pacthdeleteQuerytemplet(queryTempletKeyValAry,
		// isHasDynamicTempletData);
		// try {
		// getAccountCreateService().execSqls_RequiresNew(dsName, sqls);
		// //
		// tp.ajustCustomQueryTemplet(queryTempletKeyValAry,isHasDynamicTempletData);
		// } catch (BusinessException e) {
		// InstallLogTool.logException(e);
		// Logger.error(e.getMessage(), e);
		// throw e;
		// }
		// pkAl.addAll(Arrays.asList(queryTempletKeyValAry));
		// }
		// InstallLogTool.log(">>>sql�ű�ִ�����:" + file.getPath());
		// }
		//
		// }
		// if (pkAl.size() > 0) {
		// InstallLogTool.log("�Բ�ѯģ��ű����е�����" + dir.getPath());
		// tp.ajustCustomQueryTemplet(pkAl.toArray(new String[0]),
		// isHasDynamicTempletData);
		// }
	}

	private void installReporttempletPatch(File dir,
			boolean isHasDynamicTempletData) throws BusinessException {
		// DBInstallProgress progress = (DBInstallProgress)
		// progressThreadLocal.get();
		// File[] childFiles = FileUtil.getChildDirAndSqlFiles(dir);
		// int count = childFiles == null ? 0 : childFiles.length;
		// childFiles = FileUtil.sortFileByName(childFiles);
		// ITemplatePacth tp = (ITemplatePacth)
		// NCLocator.getInstance().lookup(ITemplatePacth.class.getName());
		// String dsName =
		// InvocationInfoProxy.getInstance().getUserDataSource();
		// ArrayList<String> pkAl = new ArrayList<String>();
		// for (int i = 0; i < count; i++) {
		// File file = childFiles[i];
		// if (file.isDirectory()) {
		// installReporttempletPatch(file, isHasDynamicTempletData);
		// } else {
		// InstallLogTool.log(">>>��ʼִ��sql�ű�:" + file.getPath());
		// progress.currDetail = "��ʼ��װ����ģ�油���ű�:" + getString(file.getPath());//
		// nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("accountmanager","UPPaccountmanager-000044")/*@res
		// // "��ʼִ�е���ģ��sql�ű�:"*/
		// // +
		// // getString(file.getPath());
		// ArrayList al = new ScriptFileReader().getSqlsFromFile(file);
		// String[] sqls = (String[]) al.toArray(new String[0]);
		// if (sqls.length > 0) {
		// String[] reportTempletKeyValAry = new
		// SqlParser(sqls).getValueByCol("pub_report_templet", "pk_templet");
		// tp.pacthdeleteReporttemplet(reportTempletKeyValAry,
		// isHasDynamicTempletData);
		// try {
		// getAccountCreateService().execSqls_RequiresNew(dsName, sqls);
		// //
		// tp.ajustCustomReportTemplet(reportTempletKeyValAry,isHasDynamicTempletData);
		// } catch (BusinessException e) {
		// InstallLogTool.logException(e);
		// Logger.error(e.getMessage(), e);
		// throw e;
		// }
		// pkAl.addAll(Arrays.asList(reportTempletKeyValAry));
		// }
		// InstallLogTool.log(">>>sql�ű�ִ�����:" + file.getPath());
		// }
		//
		// }
		// if (pkAl.size() > 0) {
		// InstallLogTool.log("�Ա���ģ��ű����е�����" + dir.getPath());
		// tp.ajustCustomReportTemplet(pkAl.toArray(new String[0]),
		// isHasDynamicTempletData);
		// }
	}

	private void installPrinttempletPatch(File dir,
			boolean isHasDynamicTempletData) throws BusinessException {
		// DBInstallProgress progress = (DBInstallProgress)
		// progressThreadLocal.get();
		// File[] childFiles = FileUtil.getChildDirAndSqlFiles(dir);
		// int count = childFiles == null ? 0 : childFiles.length;
		// childFiles = FileUtil.sortFileByName(childFiles);
		// ITemplatePacth tp = (ITemplatePacth)
		// NCLocator.getInstance().lookup(ITemplatePacth.class.getName());
		// String dsName =
		// InvocationInfoProxy.getInstance().getUserDataSource();
		// for (int i = 0; i < count; i++) {
		// File file = childFiles[i];
		// if (file.isDirectory()) {
		// installPrinttempletPatch(file, isHasDynamicTempletData);
		// } else {
		// InstallLogTool.log(">>>��ʼִ��sql�ű�:" + file.getPath());
		// progress.currDetail = "��ʼ��װ��ӡģ�油���ű�:" + getString(file.getPath());//
		// nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("accountmanager","UPPaccountmanager-000044")/*@res
		// // "��ʼִ�е���ģ��sql�ű�:"*/
		// // +
		// // getString(file.getPath());
		// ArrayList al = new ScriptFileReader().getSqlsFromFile(file);
		// String[] sqls = (String[]) al.toArray(new String[0]);
		// if (sqls.length > 0) {
		// String[] printTempletKeyValAry = new
		// SqlParser(sqls).getValueByCol("pub_print_template", "ctemplateid");
		// tp.pacthdeletePrinttemplet(printTempletKeyValAry,
		// isHasDynamicTempletData);
		// try {
		// getAccountCreateService().execSqls_RequiresNew(dsName, sqls);
		// } catch (BusinessException e) {
		// InstallLogTool.logException(e);
		// Logger.error(e.getMessage(), e);
		// throw e;
		// }
		// }
		// InstallLogTool.log(">>>sql�ű�ִ�����:" + file.getPath());
		// }
		//
		// }
	}

	private void installBillType(File dir, boolean isUpdate,
			ArrayList<String> ignorePathAL) throws BusinessException {
		// DBInstallProgress progress = (DBInstallProgress)
		// progressThreadLocal.get();
		// File[] childFiles = FileUtil.getChildDirAndSqlFiles(dir);
		// int count = childFiles == null ? 0 : childFiles.length;
		// childFiles = FileUtil.sortFileByName(childFiles);
		// ITemplateInstall pfidu = (ITemplateInstall)
		// NCLocator.getInstance().lookup(ITemplateInstall.class.getName());
		// String dsName =
		// InvocationInfoProxy.getInstance().getUserDataSource();
		// for (int i = 0; i < count; i++) {
		// File file = childFiles[i];
		// if (file.isDirectory()) {
		// installBillType(file, isUpdate, ignorePathAL);
		// } else {
		// if (needExecScriptPath(file, ignorePathAL)) {
		// InstallLogTool.log(">>>��ʼִ��sql�ű�:" + file.getPath());
		// progress.currDetail =
		// nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("accountmanager",
		// "UPPaccountmanager-000045")/*
		// * @res
		// * "��ʼִ�е�������sql�ű�:"
		// */
		// + getString(file.getPath());
		// ArrayList al = new ScriptFileReader().getSqlsFromFile(file);
		// String[] sqls = (String[]) al.toArray(new String[0]);
		// if (sqls.length > 0) {
		// if (isUpdate) {
		// String[] billTypeKeyValAry = new
		// SqlParser(sqls).getValueByCol("pk_billtypecode");
		// pfidu.installDeleteBillType(billTypeKeyValAry);
		// }
		// try {
		// getAccountCreateService().execSqls_RequiresNew(dsName, sqls);
		// } catch (BusinessException e) {
		// InstallLogTool.logException(e);
		// Logger.error(e.getMessage(), e);
		// throw e;
		// }
		// }
		// InstallLogTool.log(">>>sql�ű�ִ�����:" + file.getPath());
		// } else {
		// InstallLogTool.log(">>>sql�ű�������:" + file.getPath());
		// }
		// }
		//
		// }
	}

	@Override
	public boolean isNewInstalOr65to65(String dsName) {
		AccountInstallDAO dao = null;
		try {
			if (dsName == null)
				dao = new AccountInstallDAO();
			else
				dao = new AccountInstallDAO(dsName);
			if (!dao.isVersionTableExsist() || !dao.is63DBTable()) {// �����ڣ������Ѿ���65�İ汾���ˣ�
				return true;
			}
		} catch (Exception e) {
			Logger.error(e);
		}
		return false;
	}

	private String getString(String str) {
		int len = 50;
		if (isNullStr(str)) {
			return str;
		} else if (str.length() <= len) {
			return str;
		} else {
			String s = str.substring(str.length() - len);
			return "..." + s;
		}

	}

	public String createDBInstallProgress() throws BusinessException {
		return InstallProgressCenter.getInstance().createProgress();
	}

	public void removeProgress(String id) throws BusinessException {
		InstallProgressCenter.getInstance().removeDBInstallProgress(id);
	}

	public DBInstallProgress getProgress(String id) throws BusinessException {
		return InstallProgressCenter.getInstance().getDBInstallProgress(id);

	}

	public CodeVerinfoTreeNode getCodeVerinfoTree() throws BusinessException {

		return CodeVerinfoTreeScaner.getTreeStruct(ISysConstant.ncHome);
	}

	public void doCreateDB(String moduleCode, String scriptPath, String dsName)
			throws BusinessException {
		TableUpdateProcessor tup = new TableUpdateProcessor();
		try {
			tup.processWithDbName(moduleCode, scriptPath, dsName, null);
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		}

	}

	@Override
	public void updateDBML(Map<String, Integer> langmap)
			throws BusinessException {
		AppendDBML append = new AppendDBML();
		Logger.error("AccountManageImpl��Ӷ���" + langmap.toString()); /*
																	 * -=notranslate
																	 * =-
																	 */
		append.appendDBML(langmap);
	}

	@Override
	public DBInstalledModule[] getDbInstallModules(String dsName)
			throws BusinessException {
		AccountInstallDAO dao = null;
		try {
			if (dsName == null)
				dao = new AccountInstallDAO();
			else
				dao = new AccountInstallDAO(dsName);
			return dao.getDbInstalledModules();
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage());
		} finally {
			if (dao != null)
				dao.close();
		}
	}

}