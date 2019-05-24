--费率表
  -- drop table WA_MYPCB_RATE;
  CREATE TABLE "WA_MYPCB_RATE" 
   (  
   "PCB_GROUP" VARCHAR2(20) DEFAULT '~',
   "PCB_FORMULA" VARCHAR2(50) default '~', 
  "PCB_FORMULANMAE" VARCHAR2(50) default '~', 
  "LOWER_LIMIT" NUMBER(28,8),
  "UPPER_LIMIT" NUMBER(28,8),
  "PCB_M" NUMBER(28,8),--用于公式计算
  "PCB_RATE" NUMBER(28,8) ,
  "PCB_CATEGORY1_3"NUMBER(28,8),
  "PCB_CATEGORY2" NUMBER(28,8)
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 262144 NEXT 262144 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "NNC_DATA01" 
  
 --INSERT WA_MYPCB_RATE
delete from WA_MYPCB_RATE where 1=1;

insert into WA_MYPCB_RATE (PCB_GROUP, PCB_FORMULA, PCB_FORMULANMAE, LOWER_LIMIT, UPPER_LIMIT, PCB_M, PCB_RATE, PCB_CATEGORY1_3, PCB_CATEGORY2)
values ('03', '(((P*R)-T) - (Z+X))/(n+1)', 'For Returning Expert Program (REP)', 0.00000000, 35000.00000000, null, 15.00000000, 400.00000000, 800.00000000);

insert into WA_MYPCB_RATE (PCB_GROUP, PCB_FORMULA, PCB_FORMULANMAE, LOWER_LIMIT, UPPER_LIMIT, PCB_M, PCB_RATE, PCB_CATEGORY1_3, PCB_CATEGORY2)
values ('03', '(((P*R)-T) - (Z+X))/(n+1)', 'For Returning Expert Program (REP)', 35000.00000000, null, null, 15.00000000, 0.00000000, 0.00000000);

insert into WA_MYPCB_RATE (PCB_GROUP, PCB_FORMULA, PCB_FORMULANMAE, LOWER_LIMIT, UPPER_LIMIT, PCB_M, PCB_RATE, PCB_CATEGORY1_3, PCB_CATEGORY2)
values ('04', '((P*R)-(Z+X))/(n+1)', 'For Knowledge Worker (KW) at specified region', 0.00000000, 35000.00000000, null, 15.00000000, null, null);

insert into WA_MYPCB_RATE (PCB_GROUP, PCB_FORMULA, PCB_FORMULANMAE, LOWER_LIMIT, UPPER_LIMIT, PCB_M, PCB_RATE, PCB_CATEGORY1_3, PCB_CATEGORY2)
values ('04', '((P*R)-(Z+X))/(n+1)', 'For Knowledge Worker (KW) at specified region', 35000.00000000, null, null, 15.00000000, null, null);

insert into WA_MYPCB_RATE (PCB_GROUP, PCB_FORMULA, PCB_FORMULANMAE, LOWER_LIMIT, UPPER_LIMIT, PCB_M, PCB_RATE, PCB_CATEGORY1_3, PCB_CATEGORY2)
values ('02', 'Total Monthly Remuneration *28%', 'Non Resident Employee', null, null, null, 28.00000000, null, null);

insert into WA_MYPCB_RATE (PCB_GROUP, PCB_FORMULA, PCB_FORMULANMAE, LOWER_LIMIT, UPPER_LIMIT, PCB_M, PCB_RATE, PCB_CATEGORY1_3, PCB_CATEGORY2)
values ('01', '((((P-M)*R)+B)-(Z+X))/(n+1)', 'For Normal Remuneration', 5001.00000000, 20000.00000000, 5000.00000000, 1.00000000, -400.00000000, -800.00000000);

insert into WA_MYPCB_RATE (PCB_GROUP, PCB_FORMULA, PCB_FORMULANMAE, LOWER_LIMIT, UPPER_LIMIT, PCB_M, PCB_RATE, PCB_CATEGORY1_3, PCB_CATEGORY2)
values ('01', '((((P-M)*R)+B)-(Z+X))/(n+1)', 'For Normal Remuneration', 20001.00000000, 35000.00000000, 20000.00000000, 3.00000000, -250.00000000, -650.00000000);

insert into WA_MYPCB_RATE (PCB_GROUP, PCB_FORMULA, PCB_FORMULANMAE, LOWER_LIMIT, UPPER_LIMIT, PCB_M, PCB_RATE, PCB_CATEGORY1_3, PCB_CATEGORY2)
values ('01', '((((P-M)*R)+B)-(Z+X))/(n+1)', 'For Normal Remuneration', 35001.00000000, 50000.00000000, 35000.00000000, 8.00000000, 600.00000000, 600.00000000);

insert into WA_MYPCB_RATE (PCB_GROUP, PCB_FORMULA, PCB_FORMULANMAE, LOWER_LIMIT, UPPER_LIMIT, PCB_M, PCB_RATE, PCB_CATEGORY1_3, PCB_CATEGORY2)
values ('01', '((((P-M)*R)+B)-(Z+X))/(n+1)', 'For Normal Remuneration', 50001.00000000, 70000.00000000, 50000.00000000, 14.00000000, 1800.00000000, 1800.00000000);

insert into WA_MYPCB_RATE (PCB_GROUP, PCB_FORMULA, PCB_FORMULANMAE, LOWER_LIMIT, UPPER_LIMIT, PCB_M, PCB_RATE, PCB_CATEGORY1_3, PCB_CATEGORY2)
values ('01', '((((P-M)*R)+B)-(Z+X))/(n+1)', 'For Normal Remuneration', 70001.00000000, 100000.00000000, 70000.00000000, 21.00000000, 4600.00000000, 4600.00000000);

insert into WA_MYPCB_RATE (PCB_GROUP, PCB_FORMULA, PCB_FORMULANMAE, LOWER_LIMIT, UPPER_LIMIT, PCB_M, PCB_RATE, PCB_CATEGORY1_3, PCB_CATEGORY2)
values ('01', '((((P-M)*R)+B)-(Z+X))/(n+1)', 'For Normal Remuneration', 100001.00000000, 250000.00000000, 100000.00000000, 24.00000000, 10900.00000000, 10900.00000000);

insert into WA_MYPCB_RATE (PCB_GROUP, PCB_FORMULA, PCB_FORMULANMAE, LOWER_LIMIT, UPPER_LIMIT, PCB_M, PCB_RATE, PCB_CATEGORY1_3, PCB_CATEGORY2)
values ('01', '((((P-M)*R)+B)-(Z+X))/(n+1)', 'For Normal Remuneration', 250001.00000000, 400000.00000000, 250000.00000000, 24.50000000, 46900.00000000, 46900.00000000);

insert into WA_MYPCB_RATE (PCB_GROUP, PCB_FORMULA, PCB_FORMULANMAE, LOWER_LIMIT, UPPER_LIMIT, PCB_M, PCB_RATE, PCB_CATEGORY1_3, PCB_CATEGORY2)
values ('01', '((((P-M)*R)+B)-(Z+X))/(n+1)', 'For Normal Remuneration', 400001.00000000, 600000.00000000, 400000.00000000, 25.00000000, 83650.00000000, 83650.00000000);

insert into WA_MYPCB_RATE (PCB_GROUP, PCB_FORMULA, PCB_FORMULANMAE, LOWER_LIMIT, UPPER_LIMIT, PCB_M, PCB_RATE, PCB_CATEGORY1_3, PCB_CATEGORY2)
values ('01', '((((P-M)*R)+B)-(Z+X))/(n+1)', 'For Normal Remuneration', 600001.00000000, 1000000.00000000, 600000.00000000, 26.00000000, 133650.00000000, 46900.00000000);

insert into WA_MYPCB_RATE (PCB_GROUP, PCB_FORMULA, PCB_FORMULANMAE, LOWER_LIMIT, UPPER_LIMIT, PCB_M, PCB_RATE, PCB_CATEGORY1_3, PCB_CATEGORY2)
values ('01', '((((P-M)*R)+B)-(Z+X))/(n+1)', 'For Normal Remuneration', 1000001.00000000, 100000000000.00000000, 10000000.00000000, 28.00000000, 237650.00000000, 237650.00000000);

commit;
