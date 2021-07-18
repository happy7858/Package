package com.shenma.common.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class Log {
	public static String PATH_CLIENT_LOG = "client.log";
	private static Logger E;

	static class MyLogFormatter extends Formatter {
		@Override
		public String format(LogRecord record) {
			Date date = new Date(record.getMillis());
			SimpleDateFormat f = new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss SSS");
			String sTime = f.format(date);
			return sTime + " " + record.getLevel() + "] " + record.getMessage() + "\n";
		}
	}

	static class MyConsoleHandler extends ConsoleHandler {

		public MyConsoleHandler() {
			super();
			setOutputStream(System.out);
		}
	}

	static {
		E = Logger.getLogger("E");
		E.setUseParentHandlers(false);
		if (true) {
			try {
				if (false) {// 调试版本不清除文件
					Date date = new Date(System.currentTimeMillis());
					SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
					PATH_CLIENT_LOG = f.format(date) + ".log";
				}
				FileHandler fileHandler = new FileHandler(PATH_CLIENT_LOG);
				fileHandler.setLevel(Level.INFO);
				fileHandler.setEncoding("GBK");
				fileHandler.setFormatter(new MyLogFormatter());
				E.addHandler(fileHandler);

				ConsoleHandler conHandler = new MyConsoleHandler();
				conHandler.setFormatter(new MyLogFormatter());
				E.addHandler(conHandler);

				System.setErr(new PrintStream(new OutputStream() {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();

					@Override
					public void write(int b) throws IOException {
						if (b == '\n') {
							String info = baos.toString();
							err(info);
							baos.reset();
						} else if (b == '\r') {
						} else {
							baos.write(b);
						}
					}
				}));
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void out() {
		out("");
	}

	public static void out(String info) {
		E.info(info);
	}

	public static void err(String info) {
		E.severe(info);
	}

	public static void flush() {
		Handler[] handlers = E.getHandlers();
		for (int i = 0; i < handlers.length; i++) {
			Handler handler = handlers[i];
			handler.flush();
		}
	}
}
