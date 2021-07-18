package com.shenma.common.SMapping;

import java.util.List;

public class SLuaReadWrite implements SIReadWrite {
	protected SVCMapping m_map;

	public SLuaReadWrite(SVCMapping m) {
		m_map = m;
	}

	public int read(String sFileName) {
		return 0;
	}

	// 以LUA脚本的格式在控制台输出
	public String write() {
		StringBuffer buff = new StringBuffer();
		buff.append("local SVCMapping =");
		print(buff, m_map, 1);
		buff.append("return SVCMapping");
		return buff.toString();
	}

	// 打印一个表的内容
	private void print(StringBuffer buff, SVCMapping m, int index) {
		printtab(buff, index - 1);
		buff.append("{\n");
		List<String> iterInt = m.sortIntKey();
		for (String key : iterInt) {
			printtab(buff, index);
			buff.append("[\"" + key + "\"] = " + m.query(key) + ",\n");
		}

		List<String> iterStr = m.sortStringKey();
		for (String key : iterStr) {
			printtab(buff, index);
			buff.append("[\"" + key + "\"] = \"" + m.querystr(key) + "\",\n");
		}
		List<String> iterMap = m.sortMapKey();
		for (String key : iterMap) {
			SVCMapping submap = m.querymap(key);
			if (submap == null) {
				continue;
			}

			printtab(buff, index);
			buff.append("[\"" + key + "\"] = \n"/* + submap */);
			print(buff, submap, index + 1);
		}
		printtab(buff, index - 1);
		buff.append("}");
		if (index > 1) {
			buff.append(",\n");
		} else {
			buff.append("\n");
		}
	}

	// 打印缩进符
	private void printtab(StringBuffer buff, int num) {
		for (int i = 0; i < num; i++)
			buff.append('\t');
	}
}
