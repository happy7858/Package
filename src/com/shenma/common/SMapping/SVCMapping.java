package com.shenma.common.SMapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SVCMapping {
	// hashmap<String, int>
	private SIntHashTable m_mapInt = new SIntHashTable();
	// hashmap<String, string>
	private SStringHashTable m_mapString = new SStringHashTable();
	// hashmap<String, SVCMapping>
	private SMapHashTable m_mapMap = new SMapHashTable();
	// hashmap<String, Object>
	private SObjectHashTable m_mapObject = new SObjectHashTable();

	// 查询部分
	public int query(String sKey) {
		String sSubKey = getSubKey(sKey);
		if (sSubKey == null) {
			return 0;
		}
		SVCMapping m = getSubMap(sKey);
		if (m != null) {
			return m.query(sSubKey);
		} else {
			return m_mapInt.queryint(sKey);
		}
	}

	public String querystr(String sKey) {
		String sSubKey = getSubKey(sKey);
		if (sSubKey == null) {
			return null;
		}
		SVCMapping m = getSubMap(sKey);
		if (m != null) {
			return m.querystr(sSubKey);
		} else {
			return m_mapString.querystr(sKey);
		}
	}

	public SVCMapping querymap(String sKey) {
		String sSubKey = getSubKey(sKey);
		if (sSubKey == null) {
			return null;
		}
		SVCMapping m = getSubMap(sKey);
		if (m != null) {
			return m.querymap(sSubKey);
		} else {
			return m_mapMap.querymap(sKey);
		}
	}

	public Object queryobj(String sKey) {
		String sSubKey = getSubKey(sKey);
		if (sSubKey == null) {
			return null;
		}
		SVCMapping m = getSubMap(sKey);
		if (m != null) {
			return m.queryobj(sSubKey);
		} else {
			return m_mapObject.queryobj(sKey);
		}
	}

	/** 遍历所有表，查找指定的键值 */
	public boolean isdef(String sKey) {
		if (isdefInt(sKey)) {
			return true;
		}
		if (isdefString(sKey)) {
			return true;
		}
		if (isdefMap(sKey)) {
			return true;
		}
		if (isdefObject(sKey)) {
			return true;
		}
		return false;
	}

	/** 遍历int表，查找指定的键值 */
	public boolean isdefInt(String sKey) {
		String sSubKey = getSubKey(sKey);
		if (sSubKey == null) {
			return false;
		}
		SVCMapping m = getSubMap(sKey);
		if (m != null) {
			return m.isdefInt(sSubKey);
		} else {
			return m_mapInt.containsKey(sKey);
		}
	}

	/** 遍历string表，查找指定的键值 */
	public boolean isdefString(String sKey) {
		String sSubKey = getSubKey(sKey);
		if (sSubKey == null) {
			return false;
		}
		SVCMapping m = getSubMap(sKey);
		if (m != null) {
			return m.isdefString(sSubKey);
		} else {
			return m_mapString.containsKey(sKey);
		}
	}

	/** 遍历map表，查找指定的键值 */
	public boolean isdefMap(String sKey) {
		String sSubKey = getSubKey(sKey);
		if (sSubKey == null) {
			return false;
		}
		SVCMapping m = getSubMap(sKey);
		if (m != null) {
			return m.isdefMap(sSubKey);
		} else {
			return m_mapMap.containsKey(sKey);
		}
	}

	/** 遍历object表，查找指定的键值 */
	public boolean isdefObject(String sKey) {
		String sSubKey = getSubKey(sKey);
		if (sSubKey == null) {
			return false;
		}
		SVCMapping m = getSubMap(sKey);
		if (m != null) {
			return m.isdefObject(sSubKey);
		} else {
			return m_mapObject.containsKey(sKey);
		}
	}

	// 设置部分
	public void set(String sKey, int nValue) {
		String sSubKey = getSubKey(sKey);
		if (sSubKey == null) {
			return;
		}
		SVCMapping m = createSubMap(sKey);
		if (m != null) {
			m.set(sSubKey, nValue);
		} else {
			m_mapInt.set(sKey, nValue);
		}
	}

	public void set(String sKey, String sValue) {
		String sSubKey = getSubKey(sKey);
		if (sSubKey == null) {
			return;
		}
		SVCMapping m = createSubMap(sKey);
		if (m != null) {
			m.set(sSubKey, sValue);
		} else {
			m_mapString.set(sKey, sValue);
		}
	}

	public void set(String sKey, SVCMapping mValue) {
		String sSubKey = getSubKey(sKey);
		if (sSubKey == null) {
			return;
		}

		SVCMapping m = createSubMap(sKey);
		if (m != null) {
			m.set(sSubKey, mValue);
		} else {
			SVCMapping m2 = m_mapMap.querymap(sKey);
			if (m2 != null) {
				m2 = null;
			}
			m_mapMap.set(sKey, mValue);
		}
	}

	public void set(String sKey, Object oValue) {
		String sSubKey = getSubKey(sKey);
		if (sSubKey == null) {
			return;
		}
		SVCMapping m = createSubMap(sKey);
		if (m != null) {
			m.set(sSubKey, oValue);
		} else {
			m_mapObject.set(sKey, oValue);
		}
	}

	public void copy(SVCMapping mMap) {
		if (mMap == null) {
			return;
		}
		m_mapInt.copy(mMap.m_mapInt);
		m_mapString.copy(mMap.m_mapString);
		m_mapMap.copy(mMap.m_mapMap);
		m_mapObject = mMap.m_mapObject;
	}

	public void del(String sKey) {
		String sSubKey = getSubKey(sKey);
		if (sSubKey == null) {
			return;
		}
		SVCMapping m = getSubMap(sKey);
		if (m != null) {
			m.del(sSubKey);
		} else {
			m_mapInt.del(sKey);
		}
	}

	public void delstr(String sKey) {
		String sSubKey = getSubKey(sKey);
		if (sSubKey == null) {
			return;
		}
		SVCMapping m = getSubMap(sKey);
		if (m != null) {
			m.delstr(sSubKey);
		} else {
			m_mapString.del(sKey);
		}
	}

	public void delmap(String sKey) {
		String sSubKey = getSubKey(sKey);
		if (sSubKey == null) {
			return;
		}
		SVCMapping m = getSubMap(sKey);
		if (m != null) {
			m.delmap(sSubKey);
		} else {
			m_mapMap.del(sKey);
		}
	}

	public void delobj(String sKey) {
		String sSubKey = getSubKey(sKey);
		if (sSubKey == null) {
			return;
		}
		SVCMapping m = getSubMap(sKey);
		if (m != null) {
			m.delmap(sSubKey);
		} else {
			m_mapObject.del(sKey);
		}
	}

	// 跌代器部分
	public SIIterator iterator() {
		return m_mapObject.iterator();
	}

	public SIIterator intIterator() {
		return m_mapInt.iterator();
	}

	public SIIterator stringIterator() {
		return m_mapString.iterator();
	}

	public SIIterator mapIterator() {
		return m_mapMap.iterator();
	}

	public List<String> sortIntKey() {
		List<String> ret = new ArrayList<String>(m_mapInt.keySet());
		Collections.sort(ret);
		return ret;
	}

	public List<String> sortStringKey() {
		List<String> ret = new ArrayList<String>(m_mapString.keySet());
		Collections.sort(ret);
		return ret;
	}

	public List<String> sortMapKey() {
		List<String> ret = new ArrayList<String>(m_mapMap.keySet());
		Collections.sort(ret);
		return ret;
	}

	// 容量
	public int size() {
		return (m_mapInt.size() + m_mapString.size() + m_mapMap.size() + m_mapObject.size());
	}

	// 自增
	public void add(String sKey, int nValue) {
		String sSubKey = getSubKey(sKey);
		if (sSubKey == null) {
			return;
		}
		SVCMapping m = createSubMap(sKey);
		if (m != null) {
			m.add(sSubKey, nValue);
		} else {
			m_mapInt.add(sKey, nValue);
		}
	}

	// 查询子键值
	private String getSubKey(String sKey) {
		if (sKey == null || sKey.equals("")) {
			return null;
		}
		String sResult = null;
		int nLastIndex = sKey.lastIndexOf('/');
		if (nLastIndex != -1) {
			sResult = sKey.substring(nLastIndex + 1);
		} else {
			sResult = sKey;
		}
		return sResult;
	}

	// 查询子表，如果为空则直接返回空
	private SVCMapping getSubMap(String sKey) {
		SVCMapping mCurrMap = this;
		while (mCurrMap != null) {
			int nFirstIndex = sKey.indexOf('/');
			if (nFirstIndex == -1) {
				break;
			}
			String mapKey = sKey.substring(0, nFirstIndex);
			sKey = sKey.substring(nFirstIndex + 1);
			mCurrMap = mCurrMap.querymap(mapKey);
		}

		if (mCurrMap == this || mCurrMap == null) {
			return null;
		}
		return mCurrMap;
	}

	// 查询子表，如果为空则创建表
	private SVCMapping createSubMap(String sKey) {
		int nFirstIndex = 0;
		SVCMapping mPreMap = this;
		SVCMapping mCurrMap = null;
		String mapKey;
		while (mPreMap != null) {
			nFirstIndex = sKey.indexOf('/');
			if (nFirstIndex == -1) {
				break;
			}
			mapKey = sKey.substring(0, nFirstIndex);
			mCurrMap = mPreMap.querymap(mapKey);
			if (mCurrMap == null) {
				mCurrMap = new SVCMapping();
				mPreMap.set(mapKey, mCurrMap);
			}
			sKey = sKey.substring(nFirstIndex + 1);
			mPreMap = mCurrMap;
		}
		if (mCurrMap == null || mCurrMap == this) {
			return null;
		}
		return mCurrMap;
	}

	// 清空表信息
	public void clear() {
		m_mapInt.clear();
		m_mapString.clear();
		m_mapObject.clear();
		m_mapMap.clear();
	}

	/** 从字符串加载对象 */
	public void load(String data) {
		clear();
		SStringReadWrite rw = new SStringReadWrite(this);
		rw.read(data);
	}

	/** 将其转换为字符串 */
	public String toString() {
		SStringReadWrite rw = new SStringReadWrite(this);
		return rw.write();
	}

}
