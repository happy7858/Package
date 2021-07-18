package com.shenma.common.SMapping;

/**
 * eg: ������д��SVMapping,��ת��Ϊ�ַ��� public String GetUserGameData() { SVCMapping map =
 * new SVCMapping(); SStringReadWrite rw = new SStringReadWrite(map);
 * map.set("MaxStage", m_nMaxStage); map.set("MaxScore", m_nRankScore);
 * map.set("Title", m_nCurrTitle); return rw.write(); }
 * 
 * eg: ���ַ���ת��ΪSVCMapping���ٴ��ж�ȡ���� public void LoadDataFromSVCMapping(String data)
 * { SVCMapping map = new SVCMapping(); SStringReadWrite rw = new
 * SStringReadWrite(map); rw.read(data);
 * 
 * m_lifenum = map.query("Life");// ��������������� m_nCurrStage =
 * map.query("Stage");// ��ǰ��Ϸ�Ĺؿ�-hcl-[2011-10-22] m_nCurrScore =
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
				// �����ַ���(["age":20,"name":"xiao","love":({"book","bike"}),...])
				if ((c = sBuff.charAt(nIndex++)) == 0)
					return READ_ERROR;
				// 0: //��ʼ̬������һ��'('ת1̬���������
				if (c != '(')
					return READ_ERROR;
				if ((c = sBuff.charAt(nIndex++)) == 0)
					return READ_ERROR;
				// 1: //����һ��'['ת2̬���������
				if (c != '[')
					return READ_ERROR;
				if ((c = sBuff.charAt(nIndex++)) == 0)
					return READ_ERROR;
				state = 2;
				break;
			// 2: //����һ��']'ת����̬����֤')'�󷵻أ�����һ��'"'ת4̬����������
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
				// 4: //����һ��"""ת5̬������ѹ���ַ�����
				while (c != '"') {
					key += c;
					if ((c = sBuff.charAt(nIndex++)) == 0)
						return READ_ERROR;
				}
				if ((c = sBuff.charAt(nIndex++)) == 0)
					return READ_ERROR;
				// 5: //����һ��":"ת6̬���������
				if (c != ':')
					return READ_ERROR;
				if ((c = sBuff.charAt(nIndex++)) == 0)
					return READ_ERROR;
				// 6: //����һ��'"'ת7̬������һ������ת9̬������һ��'('ת10̬����������
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
			// 7: //����һ��'\\'��ת��Ϊ�棬��һ��"""ת7̬������ѹ���ַ�����
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
				map.set(key, val); // �����ַ�����
			}
				if ((c = sBuff.charAt(nIndex++)) == 0)
					return READ_ERROR;
				// 8: //����һ��","ת2̬���������
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
					// ����mapping���loadbuffer
					SVCMapping m = new SVCMapping();
					int len = readFromString(m, sBuff.substring(nIndex - 2));
					if (len == READ_ERROR) {
						m = null;
						return READ_ERROR;
					}
					nIndex += len - 2;
					map.set(key, m);
				} else if (c == '{') {
					System.out.println("ERROR!!����������Ϣ���˴���Ӧ�ý��룡����");
					// ����vector���loadbuffer
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

	// ���ַ����ĸ�ʽ�ڿ���̨���
	public String write() {
		StringBuffer sResult = new StringBuffer();
		SaveMapping(sResult, m_map);
		// System.out.println("SVCMapping", sResult.toString());
		return sResult.toString();
	}

	// ����������Ϣ�������У�
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
