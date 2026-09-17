-- Sample rows for local/demo use only (spring.sql.init, "local" profile).
-- IDs are seeded starting at 1000 to leave room below for whatever the app-managed
-- sequences generate next - fine for a throwaway in-memory DB, not a real numbering scheme.
INSERT INTO BANK (ID, AR_NAME, EN_NAME)
VALUES (
    1000,
    'البنك الأهلي المصري',
    'National Bank of Egypt'
  );
INSERT INTO BANK (ID, AR_NAME, EN_NAME)
VALUES (1001, 'بنك مصر', 'Banque Misr');
INSERT INTO BANK (ID, AR_NAME, EN_NAME)
VALUES (
    1002,
    'البنك التجاري الدولي',
    'Commercial International Bank'
  );
INSERT INTO CURRENCY (ID, CODE, AR_NAME, EN_NAME, SYMBOL)
VALUES (1000, 'EGP', 'جنيه مصري', 'Egyptian Pound', 'E£');
INSERT INTO CURRENCY (ID, CODE, AR_NAME, EN_NAME, SYMBOL)
VALUES (1001, 'USD', 'دولار أمريكي', 'US Dollar', '$');
INSERT INTO CURRENCY (ID, CODE, AR_NAME, EN_NAME, SYMBOL)
VALUES (1002, 'EUR', 'يورو', 'Euro', '€');
INSERT INTO INSTITUTION (ID, CODE, AR_NAME, EN_NAME)
VALUES (
    1000,
    'MOF',
    'وزارة المالية',
    'Ministry of Finance'
  );
INSERT INTO INSTITUTION (ID, CODE, AR_NAME, EN_NAME)
VALUES (
    1001,
    'CBE',
    'البنك المركزي المصري',
    'Central Bank of Egypt'
  );
INSERT INTO LANGUAGE (ID, CODE, AR_NAME, EN_NAME)
VALUES (1000, 'AR', 'العربية', 'Arabic');
INSERT INTO LANGUAGE (ID, CODE, AR_NAME, EN_NAME)
VALUES (1001, 'EN', 'الإنجليزية', 'English');
INSERT INTO TITLE (ID, CODE, AR_NAME, EN_NAME)
VALUES (1000, 'MR', 'السيد', 'Mr.');
INSERT INTO TITLE (ID, CODE, AR_NAME, EN_NAME)
VALUES (1001, 'MS', 'السيدة', 'Ms.');
INSERT INTO ENTITY_TYPE (ID, CODE, AR_NAME, EN_NAME)
VALUES (1000, 'GOV', 'حكومي', 'Governmental');
INSERT INTO ENTITY_TYPE (ID, CODE, AR_NAME, EN_NAME)
VALUES (1001, 'PVT', 'خاص', 'Private');
INSERT INTO ENTITY_LEVEL (ID, CODE, AR_NAME, EN_NAME)
VALUES (1000, 'L1', 'المستوى الأول', 'Level 1');
INSERT INTO ENTITY_LEVEL (ID, CODE, AR_NAME, EN_NAME)
VALUES (1001, 'L2', 'المستوى الثاني', 'Level 2');
INSERT INTO ENTITY_CATEGORY (ID, CODE, AR_NAME, EN_NAME)
VALUES (1000, 'MIN', 'وزارة', 'Ministry');
INSERT INTO ENTITY_CATEGORY (ID, CODE, AR_NAME, EN_NAME)
VALUES (1001, 'AUT', 'هيئة', 'Authority');
INSERT INTO APPLIED_TO_TYPE (ID, CODE, AR_NAME, EN_NAME)
VALUES (1000, 'ALL', 'الجميع', 'All');
INSERT INTO APPLIED_TO_TYPE (ID, CODE, AR_NAME, EN_NAME)
VALUES (1001, 'ENT', 'كيان محدد', 'Specific Entity');
INSERT INTO SYSTEM_PARAMETERS_TYPE (ID, CODE, AR_NAME, EN_NAME)
VALUES (1000, 'NUM', 'رقمي', 'Numeric');
INSERT INTO SYSTEM_PARAMETERS_TYPE (ID, CODE, AR_NAME, EN_NAME)
VALUES (1001, 'TXT', 'نصي', 'Text');
INSERT INTO STATUS_TYPE (ID, CODE, AR_NAME, EN_NAME)
VALUES (1000, 'ACC', 'حالة الحساب', 'Account Status');
INSERT INTO STATUS_TYPE (ID, CODE, AR_NAME, EN_NAME)
VALUES (1001, 'VCH', 'حالة السند', 'Voucher Status');
INSERT INTO STATUS (ID, CODE, AR_NAME, EN_NAME)
VALUES (1000, 'ACT', 'نشط', 'Active');
INSERT INTO STATUS (ID, CODE, AR_NAME, EN_NAME)
VALUES (1001, 'CLS', 'مغلق', 'Closed');
INSERT INTO VOUCHER_ENTRY_MODE (
    ID,
    CODE,
    AR_NAME,
    EN_NAME,
    AR_DESCRIPTION,
    EN_DESCRIPTION
  )
VALUES (
    1000,
    'MAN',
    'يدوي',
    'Manual',
    'إدخال يدوي من المستخدم',
    'Entered manually by a user'
  );
INSERT INTO VOUCHER_ENTRY_MODE (
    ID,
    CODE,
    AR_NAME,
    EN_NAME,
    AR_DESCRIPTION,
    EN_DESCRIPTION
  )
VALUES (
    1001,
    'SYS',
    'نظام',
    'System',
    'مولد تلقائيًا من النظام',
    'Generated automatically by the system'
  );
INSERT INTO FILE_DIRECTION (ID, CODE, AR_NAME, EN_NAME)
VALUES (1000, 'IN', 'وارد', 'Incoming');
INSERT INTO FILE_DIRECTION (ID, CODE, AR_NAME, EN_NAME)
VALUES (1001, 'OUT', 'صادر', 'Outgoing');
INSERT INTO FILE_TYPE (
    ID,
    CODE,
    AR_NAME,
    EN_NAME,
    AR_DESCRIPTION,
    EN_DESCRIPTION
  )
VALUES (
    1000,
    'CSV',
    'ملف نصي مفصول بفواصل',
    'CSV File',
    'ملف بيانات بصيغة CSV',
    'Comma-separated data file'
  );
INSERT INTO FILE_TYPE (
    ID,
    CODE,
    AR_NAME,
    EN_NAME,
    AR_DESCRIPTION,
    EN_DESCRIPTION
  )
VALUES (
    1001,
    'XML',
    'ملف XML',
    'XML File',
    'ملف بيانات بصيغة XML',
    'XML data file'
  );
INSERT INTO ERROR_CODE (CODE, AR_NAME, EN_NAME)
VALUES (
    'E01',
    'رقم حساب غير صالح',
    'Invalid account number'
  );
INSERT INTO ERROR_CODE (CODE, AR_NAME, EN_NAME)
VALUES ('E02', 'رصيد غير كاف', 'Insufficient balance');
INSERT INTO ERROR_CODE (CODE, AR_NAME, EN_NAME)
VALUES (
    'E03',
    'مرجع معاملة مكرر',
    'Duplicate transaction reference'
  );
INSERT INTO AUDIT_ACTION_TYPE (ID, CODE, AR_NAME, EN_NAME)
VALUES (1000, 'INS', 'إضافة', 'Insert');
INSERT INTO AUDIT_ACTION_TYPE (ID, CODE, AR_NAME, EN_NAME)
VALUES (1001, 'UPD', 'تعديل', 'Update');
INSERT INTO OTHER_ENTITY (ID, CODE, AR_NAME, EN_NAME)
VALUES (1000, 'CAI', 'القاهرة', 'Cairo');
INSERT INTO OTHER_ENTITY (ID, CODE, AR_NAME, EN_NAME)
VALUES (1001, 'ALX', 'الإسكندرية', 'Alexandria');
INSERT INTO CUSTOMER_CONTACT_PERN_TYPE (ID, CODE, AR_NAME, EN_NAME)
VALUES (1000, 'PRI', 'أساسي', 'Primary');
INSERT INTO CUSTOMER_CONTACT_PERN_TYPE (ID, CODE, AR_NAME, EN_NAME)
VALUES (1001, 'SEC', 'ثانوي', 'Secondary');
INSERT INTO GFMIS_REQUEST_STATUS (ID, CODE, NAME, DESCRIPTION)
VALUES (
    1000,
    'PEN',
    'Pending',
    'Request submitted, awaiting processing'
  );
INSERT INTO GFMIS_REQUEST_STATUS (ID, CODE, NAME, DESCRIPTION)
VALUES (
    1001,
    'CMP',
    'Completed',
    'Request processed successfully'
  );