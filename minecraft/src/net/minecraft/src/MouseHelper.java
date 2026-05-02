package net.minecraft.src;

import java.awt.Component;
import org.lwjgl.input.Mouse;

public class MouseHelper {
	private final Component windowComponent;
	private final GameSettings field_85184_d;
	public int deltaX;
	public int deltaY;

	public MouseHelper(Component var1, GameSettings var2) {
		this.windowComponent = var1;
		this.field_85184_d = var2;
	}

	public void grabMouseCursor() {
		Mouse.setGrabbed(true);
		this.deltaX = 0;
		this.deltaY = 0;
	}

	public void ungrabMouseCursor() {
		int var1 = this.windowComponent.getWidth();
		int var2 = this.windowComponent.getHeight();
		if(this.windowComponent.getParent() != null) {
			var1 = this.windowComponent.getParent().getWidth();
			var2 = this.windowComponent.getParent().getHeight();
		}

		Mouse.setCursorPosition(var1 / 2, var2 / 2);
		Mouse.setGrabbed(false);
	}

	public void mouseXYChange() {
		this.deltaX = Mouse.getDX();
		this.deltaY = Mouse.getDY();
	}
}
