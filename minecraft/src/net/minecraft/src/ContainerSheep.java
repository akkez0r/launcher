package net.minecraft.src;

class ContainerSheep extends Container {
	final EntitySheep field_90034_a;

	ContainerSheep(EntitySheep var1) {
		this.field_90034_a = var1;
	}

	public boolean canInteractWith(EntityPlayer var1) {
		return false;
	}
}
