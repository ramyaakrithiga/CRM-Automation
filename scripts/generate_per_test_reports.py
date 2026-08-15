#!/usr/bin/env python3
"""
Generate one HTML report per <testcase> from a JUnit/TestNG XML report.
Usage: python generate_per_test_reports.py -i <input_xml> -o <output_dir>
"""
import argparse
import os
import xml.etree.ElementTree as ET
import html


def sanitize_filename(name):
    keep = "-_(). "
    return "".join(c if c.isalnum() or c in keep else '_' for c in name).strip()


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('-i', '--input', required=True, help='Path to JUnit/TestNG XML file')
    parser.add_argument('-o', '--output', required=True, help='Output directory for per-test HTML reports')
    args = parser.parse_args()

    os.makedirs(args.output, exist_ok=True)

    tree = ET.parse(args.input)
    root = tree.getroot()

    # Count and process <testcase> elements
    testcases = root.findall('.//testcase')
    if not testcases:
        print('No <testcase> elements found in', args.input)
        return

    for idx, tc in enumerate(testcases, start=1):
        name = tc.get('name') or f'testcase_{idx}'
        classname = tc.get('classname') or ''
        time = tc.get('time') or ''

        status = 'passed'
        failure_node = tc.find('failure')
        error_node = tc.find('error')
        skipped_node = tc.find('skipped')
        if failure_node is not None:
            status = 'failed'
            message = failure_node.get('message') or ''
            details = failure_node.text or ''
        elif error_node is not None:
            status = 'error'
            message = error_node.get('message') or ''
            details = error_node.text or ''
        elif skipped_node is not None:
            status = 'skipped'
            message = skipped_node.get('message') or ''
            details = skipped_node.text or ''
        else:
            message = ''
            details = ''

        safe_name = sanitize_filename(f"{idx:02d}_{name}")
        out_path = os.path.join(args.output, safe_name + '.html')

        with open(out_path, 'w', encoding='utf-8') as f:
            f.write('<!doctype html>\n<html><head><meta charset="utf-8"><title>')
            f.write(html.escape(name))
            f.write('</title>')
            f.write('<style>body{font-family:Segoe UI,Arial;margin:20px} .meta{color:#555} .failed{color:#b00020} .passed{color:#0a7a0a} pre{background:#f4f4f4;padding:10px;border-radius:6px}</style>')
            f.write('</head><body>')
            f.write(f'<h1>{html.escape(name)}</h1>')
            f.write(f'<p class="meta"><strong>Class:</strong> {html.escape(classname)} &nbsp; <strong>Time:</strong> {html.escape(time)} &nbsp; <strong>Status:</strong> <span class="{html.escape(status)}">{html.escape(status)}</span></p>')
            if message:
                f.write(f'<h2>Message</h2><p>{html.escape(message)}</p>')
            if details:
                f.write('<h2>Details</h2><pre>')
                f.write(html.escape(details))
                f.write('</pre>')
            # Include any system-out / system-err if present
            sout = tc.find('system-out')
            serr = tc.find('system-err')
            if sout is not None and (sout.text or '').strip():
                f.write('<h2>System Out</h2><pre>')
                f.write(html.escape(sout.text or ''))
                f.write('</pre>')
            if serr is not None and (serr.text or '').strip():
                f.write('<h2>System Err</h2><pre>')
                f.write(html.escape(serr.text or ''))
                f.write('</pre>')
            f.write('</body></html>')

        print('Wrote', out_path)

    print(f'Generated {len(testcases)} per-test reports in {args.output}')


if __name__ == '__main__':
    main()
