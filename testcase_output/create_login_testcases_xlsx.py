import os
import zipfile
from xml.sax.saxutils import escape

out_path = r"e:\ramya\Automation AI training\crm-automation-pipeline\testcase_output\Login_Test_Cases.xlsx"
rows = [
    ["TC_ID", "Scenario Type", "Test Case Title", "Preconditions", "Test Steps", "Expected Result", "Priority", "Status"],
    ["TC-01", "Positive", "User logs in with valid credentials", "User has a valid CRM account", "1. Open CRM login page\n2. Enter valid username\n3. Enter valid password\n4. Click Sign in", "User is redirected to dashboard and sees the welcome page", "High", "Ready"],
    ["TC-02", "Positive", "User can view the dashboard after successful login", "User is logged in successfully", "1. Login with valid credentials\n2. Observe dashboard page", "Dashboard loads successfully with navigation items visible", "High", "Ready"],
    ["TC-03", "Negative", "User cannot login with invalid username", "User has a valid password but wrong username", "1. Open CRM login page\n2. Enter invalid username\n3. Enter valid password\n4. Click Sign in", "Login fails and an error message is displayed", "High", "Ready"],
    ["TC-04", "Negative", "User cannot login with invalid password", "User has a valid username but wrong password", "1. Open CRM login page\n2. Enter valid username\n3. Enter invalid password\n4. Click Sign in", "Login fails and the user remains on the login page", "High", "Ready"],
    ["TC-05", "Negative", "User cannot login with empty username", "User is on the login page", "1. Leave username blank\n2. Enter valid password\n3. Click Sign in", "Login is blocked and validation message is shown", "Medium", "Ready"],
    ["TC-06", "Negative", "User cannot login with empty password", "User is on the login page", "1. Enter valid username\n2. Leave password blank\n3. Click Sign in", "Login is blocked and validation message is shown", "Medium", "Ready"],
]

# Build shared strings and sheet XML
shared_strings = []
for row in rows:
    for value in row:
        if value not in shared_strings:
            shared_strings.append(value)


def cell_ref(col_idx, row_idx):
    letters = []
    while col_idx > 0:
        col_idx, rem = divmod(col_idx - 1, 26)
        letters.append(chr(65 + rem))
    return ''.join(reversed(letters)) + str(row_idx)

sheet_rows = []
for r_idx, row in enumerate(rows, start=1):
    cells = []
    for c_idx, value in enumerate(row, start=1):
        # Use inline strings to avoid shared strings complexity
        cells.append(f'<c r="{cell_ref(c_idx, r_idx)}" t="inlineStr"><is><t>{escape(str(value))}</t></is></c>')
    sheet_rows.append(f'<row r="{r_idx}">{"".join(cells)}</row>')

sheet_xml = f'''<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<worksheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main" xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships">
  <sheetData>{"".join(sheet_rows)}</sheetData>
</worksheet>'''

content_types = '''<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
  <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
  <Default Extension="xml" ContentType="application/xml"/>
  <Override PartName="/xl/workbook.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml"/>
  <Override PartName="/xl/worksheets/sheet1.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml"/>
  <Override PartName="/xl/styles.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.styles+xml"/>
  <Override PartName="/docProps/app.xml" ContentType="application/vnd.openxmlformats-officedocument.extended-properties+xml"/>
  <Override PartName="/docProps/core.xml" ContentType="application/vnd.openxmlformats-package.core-properties+xml"/>
</Types>'''

rels = '''<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="xl/workbook.xml"/>
  <Relationship Id="rId2" Type="http://schemas.openxmlformats.org/package/2006/relationships/metadata/core-properties" Target="docProps/core.xml"/>
  <Relationship Id="rId3" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/extended-properties" Target="docProps/app.xml"/>
</Relationships>'''

workbook = '''<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<workbook xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main" xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships">
  <sheets>
    <sheet name="Login Test Cases" sheetId="1" r:id="rId1"/>
  </sheets>
</workbook>'''

workbook_rels = '''<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet" Target="worksheets/sheet1.xml"/>
  <Relationship Id="rId2" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles" Target="styles.xml"/>
</Relationships>'''

styles = '''<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<styleSheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main">
  <fonts count="1"><font><sz val="11"/><name val="Calibri"/></font></fonts>
  <fills count="1"><fill><patternFill patternType="none"/></fill></fills>
  <borders count="1"><border/></borders>
  <cellStyleXfs count="1"><xf numFmtId="0" fontId="0" fillId="0" borderId="0"/></cellStyleXfs>
  <cellXfs count="1"><xf numFmtId="0" fontId="0" fillId="0" borderId="0" xfId="0"/></cellXfs>
  <cellStyles count="1"><cellStyle name="Normal" xfId="0" builtinId="0"/></cellStyles>
</styleSheet>'''

app_props = '''<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Properties xmlns="http://schemas.openxmlformats.org/officeDocument/2006/extended-properties"
  xmlns:vt="http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes">
  <Application>Microsoft Excel</Application>
</Properties>'''

core_props = '''<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<cp:coreProperties xmlns:cp="http://schemas.openxmlformats.org/package/2006/metadata/core-properties"
  xmlns:dc="http://purl.org/dc/elements/1.1/"
  xmlns:dcterms="http://purl.org/dc/terms/"
  xmlns:dcmitype="http://purl.org/dc/dcmitype/"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  <dc:title>Login Test Cases</dc:title>
  <dc:creator>Copilot</dc:creator>
  <cp:lastModifiedBy>Copilot</cp:lastModifiedBy>
</cp:coreProperties>'''

os.makedirs(os.path.dirname(out_path), exist_ok=True)
with zipfile.ZipFile(out_path, 'w', zipfile.ZIP_DEFLATED) as z:
    z.writestr('[Content_Types].xml', content_types)
    z.writestr('_rels/.rels', rels)
    z.writestr('docProps/app.xml', app_props)
    z.writestr('docProps/core.xml', core_props)
    z.writestr('xl/workbook.xml', workbook)
    z.writestr('xl/_rels/workbook.xml.rels', workbook_rels)
    z.writestr('xl/styles.xml', styles)
    z.writestr('xl/worksheets/sheet1.xml', sheet_xml)

print(f'Created {out_path}')
