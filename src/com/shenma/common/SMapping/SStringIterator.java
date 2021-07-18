package com.shenma.common.SMapping;

import java.util.Hashtable;

public class SStringIterator extends SObjectIterator {
	
	public SStringIterator(Hashtable table) {
		super(table);
	}

	public String GetStringValue() {
		return (String)GetValue();
	}
}
