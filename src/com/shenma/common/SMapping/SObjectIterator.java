package com.shenma.common.SMapping;

import java.util.Enumeration;
import java.util.Hashtable;

public class SObjectIterator implements SIIterator {
	private Hashtable m_hashtable = null;
	private Enumeration m_enumeration = null;
	private Object m_objKey = null; 
	private Object m_objValue = null;
	public SObjectIterator(Hashtable table ) {
		m_hashtable = table;
		m_enumeration = m_hashtable.keys();
	}

	public boolean Next() {
		if(m_enumeration.hasMoreElements()){
			m_objKey = m_enumeration.nextElement();
			m_objValue = m_hashtable.get((String)m_objKey);
			return true;
		}
		return false;
	}
	
	public String GetKey() {
		return (String)m_objKey;
	}
	public Object GetValue() {
		return m_objValue;
	}
	public SVCMapping GetMapValue() {
		return null;
	}

	public String GetStringValue() {
		return null;
	}
	
	public int GetIntValue() {
		return 0;
	}
}
