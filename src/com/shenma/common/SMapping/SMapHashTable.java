package com.shenma.common.SMapping;

public class SMapHashTable extends SObjectHashTable {

	public SIIterator iterator() {
		return new SMapIterator(this);
	}

	public SVCMapping querymap(String sKey) {
		Object obj = queryobj(sKey);
		if (obj == null) {
			return null;
		}
		return (SVCMapping) obj;
	}

	public void set(String sKey, SVCMapping mValue) {
		super.set(sKey, mValue);
	}
	public void clear(){
		SIIterator iter = iterator();
		while(iter.Next()){
			iter.GetMapValue().clear();
		}
		super.clear();
	}

	public void copy(SMapHashTable mapMap) {
		clear();
		SIIterator iter = mapMap.iterator();
		while(iter.Next()){
			SVCMapping m = new SVCMapping();
			m.copy(iter.GetMapValue());
			set(iter.GetKey(), m);
		}
	}
}
