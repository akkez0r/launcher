package net.minecraft.src;

public class GuiProgress extends GuiScreen implements IProgressUpdate {
	private String progressMessage = "";
	private String workingMessage = "";
	private int currentProgress = 0;
	private boolean noMoreProgress;

	public void displayProgressMessage(String var1) {
		this.resetProgressAndMessage(var1);
	}

	public void resetProgressAndMessage(String var1) {
		this.progressMessage = var1;
		this.resetProgresAndWorkingMessage("Working...");
	}

	public void resetProgresAndWorkingMessage(String var1) {
		this.workingMessage = var1;
		this.setLoadingProgress(0);
	}

	public void setLoadingProgress(int var1) {
		this.currentProgress = var1;
	}

	public void onNoMoreProgress() {
		this.noMoreProgress = true;
	}

	public void drawScreen(int var1, int var2, float var3) {
		if(this.noMoreProgress) {
			this.mc.displayGuiScreen((GuiScreen)null);
		} else {
			this.drawDefaultBackground();
			this.drawCenteredString(this.fontRenderer, this.progressMessage, this.width / 2, 70, 16777215);
			this.drawCenteredString(this.fontRenderer, this.workingMessage + " " + this.currentProgress + "%", this.width / 2, 90, 16777215);
			super.drawScreen(var1, var2, var3);
		}
	}
}
