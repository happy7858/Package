package com.shenma.common.SMapping;

public class SIntHashTable extends SObjectHashTable {

	// return the iterator of hash table
	public SIIterator iterator() {
		return new SIntInterator(this);
	}

	// query the integer value by key
	public int queryint(String sKey) {
		Object obj = queryobj(sKey);
		if (obj == null) {
			return 0;
		}
		return ((Integer) obj).intValue();
	}

	public void set(String sKey, int nValue) {
		set(sKey, new Integer(nValue));
	}

	// add nValue by sKey
	public void add(String sKey, int nValue) {
		int nCurrValue = queryint(sKey);
		nCurrValue += nValue;
		set(sKey, nCurrValue);
	}

	public void copy(SIntHashTable intMap) {
		clear();
		SIIterator iter = intMap.iterator();
		while(iter.Next()){
			set(iter.GetKey(), iter.GetIntValue());
		}
	}
}
