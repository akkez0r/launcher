package net.minecraft.src;

class GuiBeaconButtonConfirm extends GuiBeaconButton {
	final GuiBeacon beaconGui;

	public GuiBeaconButtonConfirm(GuiBeacon var1, int var2, int var3, int var4) {
		super(var2, var3, var4, "/gui/beacon.png", 90, 220);
		this.beaconGui = var1;
	}

	public void func_82251_b(int var1, int var2) {
		this.beaconGui.drawCreativeTabHoveringText(StatCollector.translateToLocal("gui.done"), var1, var2);
	}
}
