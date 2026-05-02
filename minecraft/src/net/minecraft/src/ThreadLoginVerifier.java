package net.minecraft.src;

class ThreadLoginVerifier extends Thread {
	final NetLoginHandler loginHandler;

	ThreadLoginVerifier(NetLoginHandler var1) {
		this.loginHandler = var1;
	}

	public void run() {
		try {
			NetLoginHandler.func_72531_a(this.loginHandler, true);
		} catch (Exception var5) {
			this.loginHandler.raiseErrorAndDisconnect("Failed to verify username! [internal error " + var5 + "]");
			var5.printStackTrace();
		}

	}
}
