package com.shenma.common.SMapping;

import java.util.Hashtable;

public class SMapIterator extends SObjectIterator {

	public SMapIterator(Hashtable table) {
		super(table);
	}

	public SVCMapping GetMapValue() {
		return (SVCMapping)GetValue();
	}

}
