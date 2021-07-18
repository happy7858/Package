import java.util.ArrayList;
import java.util.List;

import com.shenma.common.util.Log;

public class Test {

	private Test() {123
	}

	///////////////////////////////////////////////////////
	// JAVA调用c++的相关代码
	public native void init(String appname, String dir);

	public native void setStatus(String pageCode, int status);

	public native String getPati();

	public native void close();

	///////////////////////////////////////////////////////
	// C++调用java的相关代码
	public static void logout(String info) {
		Log.out(info);
	}

	static {

	}

	public native int add(int a, int b); // native方法，具体实现在C中

	static List<Integer> items = new ArrayList<Integer>();

	public static void main(String[] args) {
		for (int i = 0; i < 50; i++) {
			items.add(i);
		}

		for (int i = 0; i < 5; i++) {
			Thread t = new Thread(new Runnable() {

				@Override
				public void run() {
					while (true) {
						synchronized (items) {
							if (items.isEmpty() == false) {
								Integer num = items.remove(0);
								if (num != null) {
									System.out.println("remove :" + num);
								}
							} else {
								// System.out.println("empty:" );
							}
						}
					}

				}
			});
			t.start();
		}
	}

}
