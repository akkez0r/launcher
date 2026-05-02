package net.minecraft.src;

public class MaterialLogic extends Material {
	public MaterialLogic(MapColor var1) {
		super(var1);
		this.setAlwaysHarvested();
	}

	public boolean isSolid() {
		return false;
	}

	public boolean getCanBlockGrass() {
		return false;
	}

	public boolean blocksMovement() {
		return false;
	}
}
