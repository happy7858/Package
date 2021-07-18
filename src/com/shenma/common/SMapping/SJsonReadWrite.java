package com.shenma.common.SMapping;

/**
 * eg: 将数据写入SVMapping,再转换为字符串 public String GetUserGameData() { SVCMapping map =
 * new SVCMapping(); SStringReadWrite rw = new SStringReadWrite(map);
 * map.set("MaxStage", m_nMaxStage); map.set("MaxScore", m_nRankScore);
 * map.set("Title", m_nCurrTitle); return rw.write(); }
 * 
 * eg: 将字符串转换为SVCMapping，再从中读取数据 public void LoadDataFromSVCMapping(String data)
 * { SVCMapping map = new SVCMapping(); SStringReadWrite rw = new
 * SStringReadWrite(map); rw.read(data);
 * 
 * m_lifenum = map.query("Life");// 增加生命购买情况 m_nCurrStage =
 * map.query("Stage");// 当前游戏的关卡-hcl-[2011-10-22] m_nCurrScore =
 * map.query("Score"); }
 */

public class SJsonReadWrite implements SIReadWrite {
	protected SVCMapping m_map;

	public SJsonReadWrite(SVCMapping m) {
		m_map = m;
	}

	public int read(String sBuff) {
		m_map.clear();
		return readFromString(m_map, sBuff);
	}

	private int readFromString(SVCMapping map, String sBuff) {
		String key = "";
		int nBuffLength = sBuff.length();
		int nIndex = 0;
		int state = 1;
		char c = 0;
		while (nIndex < nBuffLength) {
			switch (state) {
			case 1:
				// 分析字符串(["age":20,"name":"xiao","love":({"book","bike"}),...])
				if ((c = sBuff.charAt(nIndex++)) == 0)
					return READ_ERROR;
				// 0: //开始态：接收一个'('转1态，否则错误。
				if (c != '(')
					return READ_ERROR;
				if ((c = sBuff.charAt(nIndex++)) == 0)
					return READ_ERROR;
				// 1: //接收一个'['转2态，否则错误。
				if (c != '[')
					return READ_ERROR;
				if ((c = sBuff.charAt(nIndex++)) == 0)
					return READ_ERROR;
				state = 2;
				break;
			// 2: //接收一个']'转结束态，验证')'后返回；接收一个'"'转4态；其他错误。
			case 2:
				if (c == ']') {
					if ((c = sBuff.charAt(nIndex++)) == 0) {
						return READ_ERROR;
					}
					if (c != ')') {
						return READ_ERROR;
					}
					return nIndex; // return buffer - buf;
				} else if (c != '"') {
					return READ_ERROR;
				}

				if ((c = sBuff.charAt(nIndex++)) == 0)
					return READ_ERROR;
				key = "";
				// 4: //接收一个"""转5态，其他压入字符串。
				while (c != '"') {
					key += c;
					if ((c = sBuff.charAt(nIndex++)) == 0)
						return READ_ERROR;
				}
				if ((c = sBuff.charAt(nIndex++)) == 0)
					return READ_ERROR;
				// 5: //接收一个":"转6态，否则错误。
				if (c != ':')
					return READ_ERROR;
				if ((c = sBuff.charAt(nIndex++)) == 0)
					return READ_ERROR;
				// 6: //接收一个'"'转7态；接收一个数字转9态；接收一个'('转10态。其他错误。
				if (c == '(') {
					state = 10;
					break;
				} else if (c == '-' || c >= '0' && c <= '9') {
					state = 9;
					break;
				} else if (c != '"') {
					return READ_ERROR;
				}
				if ((c = sBuff.charAt(nIndex++)) == 0)
					return READ_ERROR;
			// 7: //接收一个'\\'记转义为真，收一个"""转7态。否则压入字符串。
			{
				String val = "";
				while (c != '"') {
					if (c == '\\') {
						if ((c = sBuff.charAt(nIndex++)) == 0)
							return READ_ERROR;
					}
					val += c;
					if ((c = sBuff.charAt(nIndex++)) == 0)
						return READ_ERROR;
				}
				map.set(key, val); // 设置字符串表
			}
				if ((c = sBuff.charAt(nIndex++)) == 0)
					return READ_ERROR;
				// 8: //接收一个","转2态，否则错误。
				if (c != ',')
					return READ_ERROR;
				if ((c = sBuff.charAt(nIndex++)) == 0)
					return READ_ERROR;
				state = 2;// goto case_2;
				break;
			case 9:
				int nBegin = nIndex - 1;
				if ((c = sBuff.charAt(nIndex++)) == 0)
					return READ_ERROR;
				while (c != ',') {
					if (c < '0' || c > '9')
						return READ_ERROR;
					if ((c = sBuff.charAt(nIndex++)) == 0)
						return READ_ERROR;
				}
				map.set(key, Integer.parseInt(sBuff.substring(nBegin, nIndex - 1)));
				if ((c = sBuff.charAt(nIndex++)) == 0)
					return READ_ERROR;
				state = 2;
				break;
			case 10:
				if ((c = sBuff.charAt(nIndex++)) == 0)
					return READ_ERROR;
				if (c == '[') {
					// 处理mapping类的loadbuffer
					SVCMapping m = new SVCMapping();
					int len = readFromString(m, sBuff.substring(nIndex - 2));
					if (len == READ_ERROR) {
						m = null;
						return READ_ERROR;
					}
					nIndex += len - 2;
					map.set(key, m);
				} else if (c == '{') {
					System.out.println("ERROR!!加载设置信息表，此处不应该进入！！！");
					// 处理vector类的loadbuffer
					// SVCStringSet set;
					// int len = set.LoadBuffer(buffer-2);
					// if( len == READ_ERROR )
					return READ_ERROR;
					// nIndex += len - 2;
					// m_SetMap.set(const_cast<char *>(key.c_str()), set);
				} else
					return READ_ERROR;
				if ((c = sBuff.charAt(nIndex++)) == 0)
					return READ_ERROR;
				if (c != ',')
					return READ_ERROR;
				if ((c = sBuff.charAt(nIndex++)) == 0)
					return READ_ERROR;
				state = 2;
				break;
			}
		}
		return READ_ERROR;
	}

	// 以字符串的格式在控制台输出
	public String write() {
		StringBuffer sResult = new StringBuffer();
		SaveMapping(sResult, m_map);
		// System.out.println("SVCMapping", sResult.toString());
		return sResult.toString();
	}

	// 输出自身的信息（不换行）
	private void SaveMapping(StringBuffer result, SVCMapping m) {
		result.append("{");

		SIIterator iter = m.intIterator();
		while (iter.Next()) {
			result.append('"');
			result.append(iter.GetKey());
			result.append("\":");
			result.append(iter.GetIntValue());
			result.append(',');
		}

		iter = m.stringIterator();
		while (iter.Next()) {
			result.append('"');
			result.append(iter.GetKey());
			result.append("\":\"");
			String sValue = iter.GetStringValue();
			for (int i = 0; i < sValue.length(); i++) {
				char p = sValue.charAt(i);
				if (p == '"' || p == '\\') {
					result.append('\\');
				}
				result.append(p);
			}
			result.append("\",");
		}

		iter = m.mapIterator();
		while (iter.Next()) {
			SVCMapping submap = iter.GetMapValue();
			if (submap == null) {
				continue;
			}
			result.append('"');
			result.append(iter.GetKey());
			result.append("\":");
			SaveMapping(result, submap);
			result.append(',');
		}
		int len = result.length();
		if (len > 0 && result.charAt(len - 1) == ',') {
			result.setCharAt(len - 1, '}');
		} else {
			result.append("}");
		}
	}

}
