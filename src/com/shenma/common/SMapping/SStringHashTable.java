package com.shenma.common.SMapping;

public class SStringHashTable extends SObjectHashTable {

	public SIIterator iterator() {
		return new SStringIterator(this);
	}

	public String querystr(String sKey) {
		Object obj = queryobj(sKey);
		if (obj == null) {
			return null;
		}
		return (String) obj;
	}

	public void set(String sKey, String sValue) {
		super.set(sKey, sValue);
	}

	public void copy(SStringHashTable strMap) {
		clear();
		SIIterator iter = strMap.iterator();
		while(iter.Next()){
			set(iter.GetKey(), iter.GetStringValue());
		}
	}
}
