package com.shenma.common.SMapping;

import java.util.Hashtable;

public class SObjectHashTable extends Hashtable {
	public SIIterator iterator() {
		return new SObjectIterator(this);
	}

	public void del(String sKey) {
		remove(sKey);
	}

	public Object queryobj(String sKey) {
		return get(sKey);
	}

	public void set(String sKey, Object oValue) {
		put(sKey, oValue);
	}

}
