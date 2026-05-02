package net.minecraft.src;

import java.io.IOException;

public class TaskOnlineConnect extends TaskLongRunning {
	private NetClientHandler field_96586_a;
	private final McoServer field_96585_c;
	private final GuiScreen field_96584_d;

	public TaskOnlineConnect(GuiScreen var1, McoServer var2) {
		this.field_96584_d = var1;
		this.field_96585_c = var2;
	}

	public void run() {
		this.func_96576_b(StringTranslate.getInstance().translateKey("mco.connect.connecting"));
		McoClient var1 = new McoClient(this.func_96578_b().session);
		boolean var2 = false;
		boolean var3 = false;
		McoServerAddress var4 = null;

		for(int var5 = 0; var5 < 10 && !this.func_96577_c(); ++var5) {
			try {
				var4 = var1.func_96374_a(this.field_96585_c.field_96408_a);
				var2 = true;
			} catch (ExceptionRetryCall var7) {
			} catch (ExceptionMcoService var8) {
				var3 = true;
				this.func_96575_a(var8.getLocalizedMessage());
				break;
			} catch (IOException var9) {
			} catch (Exception var10) {
				var3 = true;
				this.func_96575_a(var10.getLocalizedMessage());
			}

			if(var2) {
				break;
			}

			this.func_96581_e();
		}

		if(!this.func_96577_c() && !var3) {
			if(var2) {
				ServerAddress var11 = ServerAddress.func_78860_a(var4.field_96417_a);
				this.func_96582_a(var11.getIP(), var11.getPort());
			} else {
				this.func_96578_b().displayGuiScreen(this.field_96584_d);
			}
		}

	}

	private void func_96581_e() {
		try {
			Thread.sleep(5000L);
		} catch (InterruptedException var2) {
			System.err.println(var2);
		}

	}

	private void func_96582_a(String var1, int var2) {
		(new ThreadOnlineConnect(this, var1, var2)).start();
	}

	public void func_96573_a() {
		if(this.field_96586_a != null) {
			this.field_96586_a.processReadPackets();
		}

	}

	static NetClientHandler func_96583_a(TaskOnlineConnect var0, NetClientHandler var1) {
		return var0.field_96586_a = var1;
	}

	static GuiScreen func_98172_a(TaskOnlineConnect var0) {
		return var0.field_96584_d;
	}

	static NetClientHandler func_96580_a(TaskOnlineConnect var0) {
		return var0.field_96586_a;
	}
}
