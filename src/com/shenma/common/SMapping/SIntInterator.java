package com.shenma.common.SMapping;

import java.util.Hashtable;
public class SIntInterator extends SObjectIterator {

	public SIntInterator(Hashtable table) {
		super(table);
	}


	public int GetIntValue() {
		return ((Integer)GetValue()).intValue();
	}
	
}
