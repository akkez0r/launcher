package net.minecraft.src;

import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogAgent implements ILogAgent {
	private final Logger serverLogger;
	private final String logFile;
	private final String loggerName;
	private final String loggerPrefix;

	public LogAgent(String var1, String var2, String var3) {
		this.serverLogger = Logger.getLogger(var1);
		this.loggerName = var1;
		this.loggerPrefix = var2;
		this.logFile = var3;
		this.setupLogger();
	}

	private void setupLogger() {
		this.serverLogger.setUseParentHandlers(false);
		Handler[] var1 = this.serverLogger.getHandlers();
		int var2 = var1.length;

		for(int var3 = 0; var3 < var2; ++var3) {
			Handler var4 = var1[var3];
			this.serverLogger.removeHandler(var4);
		}

		LogFormatter var6 = new LogFormatter(this, (LogAgentINNER1)null);
		ConsoleHandler var7 = new ConsoleHandler();
		var7.setFormatter(var6);
		this.serverLogger.addHandler(var7);

		try {
			FileHandler var8 = new FileHandler(this.logFile, true);
			var8.setFormatter(var6);
			this.serverLogger.addHandler(var8);
		} catch (Exception var5) {
			this.serverLogger.log(Level.WARNING, "Failed to log " + this.loggerName + " to " + this.logFile, var5);
		}

	}

	public void logInfo(String var1) {
		this.serverLogger.log(Level.INFO, var1);
	}

	public void logWarning(String var1) {
		this.serverLogger.log(Level.WARNING, var1);
	}

	public void logWarningFormatted(String var1, Object... var2) {
		this.serverLogger.log(Level.WARNING, var1, var2);
	}

	public void logWarningException(String var1, Throwable var2) {
		this.serverLogger.log(Level.WARNING, var1, var2);
	}

	public void logSevere(String var1) {
		this.serverLogger.log(Level.SEVERE, var1);
	}

	public void logSevereException(String var1, Throwable var2) {
		this.serverLogger.log(Level.SEVERE, var1, var2);
	}

	public void logFine(String var1) {
		this.serverLogger.log(Level.FINE, var1);
	}

	static String func_98237_a(LogAgent var0) {
		return var0.loggerPrefix;
	}
}
