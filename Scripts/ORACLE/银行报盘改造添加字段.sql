--  ���б�������˵ڶ�����־��
alter table hr_dataio_intface add iiftop2 INT NULL;
alter table hr_dataio_intface add toplineposition2 INT NULL;
alter table hr_dataio_intface add toplinenum2 INT NULL;

-- ���б����������ѡ������ڸ�ʽ
alter table hr_dataintface_b add inextline INT NULL;
alter table hr_dataintface_b add dateformat VARCHAR(101);

-- ��־��VO�������ѡ���������л���β��
alter table hr_ifsettop add inextline INT NULL;
alter table hr_ifsettop add itoplineposition INT NULL;

-- 2019-03-18������ֶ�
alter table hr_dataintface_b add iskipifzero INT NULL;