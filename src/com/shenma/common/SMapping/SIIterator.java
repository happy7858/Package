package com.shenma.common.SMapping;

public interface SIIterator {
	boolean 	Next();				// get the next element in hash table
	String 		GetKey();			// get the key of current element
	Object 		GetValue();			// current element
	int 		GetIntValue();		// get current value as integer
	String 		GetStringValue();	// get current value as string
	SVCMapping 	GetMapValue();		// get current value as svcmapping
}
