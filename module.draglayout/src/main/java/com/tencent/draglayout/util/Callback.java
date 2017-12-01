package com.tencent.draglayout.util;

public interface Callback {
	void onBefore();

	boolean onRun();

	void onAfter(boolean b);
}
