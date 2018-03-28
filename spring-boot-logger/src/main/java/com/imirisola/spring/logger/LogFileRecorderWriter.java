package com.imirisola.spring.logger;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogFileRecorderWriter {

	private static final String LOG_FILE = "application.log";
	private static final String OLD_LOG_FILE = "application_roll.log ";
	private static final int MAX_RECORDS = 10000;
	
	private static final TimeZone GMT = TimeZone.getTimeZone("GMT");
	protected static final char SEPERATOR = '#';	
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LogFileRecorderWriter.class);

	private final File logFile;
	private final File oldLogFile;
	private OutputStream stream;
	private int counter = 0;
	private final ReentrantLock lock = new ReentrantLock();
	
	public LogFileRecorderWriter(File logDir) {
		logFile = new File(logDir, LOG_FILE);
		oldLogFile = new File(logDir, OLD_LOG_FILE);
	}
	
	public void write (long timestamp, String user, HttpServletRequest request, String msg, Object... obj) {
		
		StringBuilder sb = new StringBuilder(512);

		addRecordLine(sb, timestamp, user, request, msg, obj);
		sb.append('\n');

		try {
			byte[] content =  sb.toString().getBytes("UTF-8");  //IOUtils.toUTF8Bytes(sb.toString());
			if (lock.tryLock(10, TimeUnit.MILLISECONDS)) {
				try {
					if (stream == null) {
						openStream();
					}
					stream.write(content);
					stream.flush();
					counter++;
					if (counter == MAX_RECORDS) {
						rollover();
					}
				} catch (IOException e) {
					closeStream();
					if (LOGGER.isInfoEnabled()) {
						LOGGER.info("Write failed!", e);
					}
				} finally {
					lock.unlock();
				}
			} else {
				if (LOGGER.isInfoEnabled()) {
					LOGGER.info("Could not get lock in time!");
				}
			}
		} catch (Exception e) {
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("Could not get lock!", e);
			}
			Thread.currentThread().interrupt();
		}		
	}
	
	private void openStream() throws IOException {
		stream = new BufferedOutputStream(new FileOutputStream(logFile, true));
	}
	private void closeStream() {
		if (stream != null) {
			IOUtils.closeQuietly(stream);
			stream = null;
		}
	}
	private void rollover() {
		if (stream != null) {
			IOUtils.closeQuietly(stream);
		}
		if (logFile.exists()) {
			boolean canRename = true;
			if (oldLogFile.exists()) {
				canRename = oldLogFile.delete();
			}
			boolean hasRenamed = canRename;
			if (canRename) {
				hasRenamed = logFile.renameTo(oldLogFile);
			}
			if (!hasRenamed) {
				if (!logFile.delete()) {
					if (LOGGER.isErrorEnabled()) {
						LOGGER.error("Could neither rename nor delete record file!");
					}
				}
			}
		}
		counter = 0;
	}
	
	protected void addRecordLine(final StringBuilder sb, long timestamp, String user, HttpServletRequest request, String msg, Object... obj) {
		addTimestamp(sb, timestamp);
		sb.append(SEPERATOR);
		addSessionId(sb, request);
		sb.append(SEPERATOR);
		addUser(sb, user);
		sb.append(SEPERATOR);
		addMessage(sb, msg, obj);
	}

	protected void addTimestamp(final StringBuilder sb, final long timestamp) {
		final GregorianCalendar cal = new GregorianCalendar(GMT);
		cal.setTimeInMillis(timestamp);
		add4d(sb, cal.get(Calendar.YEAR));
		sb.append('-');
		add2d(sb, cal.get(Calendar.MONTH) + 1);
		sb.append('-');
		add2d(sb, cal.get(Calendar.DAY_OF_MONTH));
		sb.append(' ');
		add2d(sb, cal.get(Calendar.HOUR_OF_DAY));
		sb.append(':');
		add2d(sb, cal.get(Calendar.MINUTE));
		sb.append(':');
		add2d(sb, cal.get(Calendar.SECOND));
		sb.append('.');
		int ms = cal.get(Calendar.MILLISECOND);
		add3d(sb, ms);
	}	
	
	private void add2d(final StringBuilder sb, int value) {
		if (value < 10) {
			sb.append('0');
		}
		sb.append(value);
	}
	private void add3d(final StringBuilder sb, int value) {
		if (value < 10) {
			sb.append('0');
		}
		if (value < 100) {
			sb.append('0');
		}
		sb.append(value);
	}	
	
	private void add4d(final StringBuilder sb, int value) {
		if (value < 10) {
			sb.append('0');
		}
		if (value < 100) {
			sb.append('0');
		}
		if (value < 1000) {
			sb.append('0');
		}
		sb.append(value);
	}
	
	protected void addSessionId(final StringBuilder sb, final HttpServletRequest request) {
		if (request == null) {
			sb.append('-');
			return;
		}
		HttpSession session = request.getSession(false);
		if (session == null) {
			sb.append("nosession");
			return;
		}
		sb.append(session.getId());
	}
	
	protected void addUser(final StringBuilder sb, final String user) {
		if (user == null) {
			sb.append('-');
			return;
		}
		sb.append(user);
	}
	
	protected void addMessage(final StringBuilder sb, final String msg, final Object... obj) {
		if (msg == null) {
			return;
		}
		if (obj == null || obj.length == 0) {
			sb.append(msg);
			return;
		}
		int idx = 0;
		for (Object o : obj) {
			boolean foundPlaceholder = false;
			while (!foundPlaceholder) {
				int pos = msg.indexOf('{', idx);
				if (pos == -1) {
					break;
				}
				if (pos + 1 == msg.length()) {
					break;
				}
				sb.append(msg.substring(idx, pos));
				char nextChar = msg.charAt(pos + 1);
				if (nextChar == '}') {
					if (o == null) {
						sb.append("null");
					} else {
						sb.append(o.toString());
					}
					foundPlaceholder = true;
				} else {
					sb.append('{');
					sb.append(nextChar);
				}
				idx = pos + 2;
			}
			if (!foundPlaceholder) {
				break;
			}
		}
		if (idx == 0) {
			sb.append(msg);
		} else {
			sb.append(msg.substring(idx));
		}
	}	
}
