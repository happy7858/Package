package com.shenma.common.SMapping;

public abstract interface SIReadWrite {
	public static final int READ_ERROR = -1;
	public abstract int read(String string);	// ������
	public abstract String write();				// д����
}
