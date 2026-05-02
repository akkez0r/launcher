package net.minecraft.src;

import java.util.List;
import java.util.Random;

public class Item {
	private CreativeTabs tabToDisplayOn = null;
	protected static Random itemRand = new Random();
	public static Item[] itemsList = new Item[32000];
	public static Item shovelIron = (new ItemSpade(0, EnumToolMaterial.IRON)).setUnlocalizedName("shovelIron");
	public static Item pickaxeIron = (new ItemPickaxe(1, EnumToolMaterial.IRON)).setUnlocalizedName("pickaxeIron");
	public static Item axeIron = (new ItemAxe(2, EnumToolMaterial.IRON)).setUnlocalizedName("hatchetIron");
	public static Item flintAndSteel = (new ItemFlintAndSteel(3)).setUnlocalizedName("flintAndSteel");
	public static Item appleRed = (new ItemFood(4, 4, 0.3F, false)).setUnlocalizedName("apple");
	public static ItemBow bow = (ItemBow)(new ItemBow(5)).setUnlocalizedName("bow");
	public static Item arrow = (new Item(6)).setUnlocalizedName("arrow").setCreativeTab(CreativeTabs.tabCombat);
	public static Item coal = (new ItemCoal(7)).setUnlocalizedName("coal");
	public static Item diamond = (new Item(8)).setUnlocalizedName("diamond").setCreativeTab(CreativeTabs.tabMaterials);
	public static Item ingotIron = (new Item(9)).setUnlocalizedName("ingotIron").setCreativeTab(CreativeTabs.tabMaterials);
	public static Item ingotGold = (new Item(10)).setUnlocalizedName("ingotGold").setCreativeTab(CreativeTabs.tabMaterials);
	public static Item swordIron = (new ItemSword(11, EnumToolMaterial.IRON)).setUnlocalizedName("swordIron");
	public static Item swordWood = (new ItemSword(12, EnumToolMaterial.WOOD)).setUnlocalizedName("swordWood");
	public static Item shovelWood = (new ItemSpade(13, EnumToolMaterial.WOOD)).setUnlocalizedName("shovelWood");
	public static Item pickaxeWood = (new ItemPickaxe(14, EnumToolMaterial.WOOD)).setUnlocalizedName("pickaxeWood");
	public static Item axeWood = (new ItemAxe(15, EnumToolMaterial.WOOD)).setUnlocalizedName("hatchetWood");
	public static Item swordStone = (new ItemSword(16, EnumToolMaterial.STONE)).setUnlocalizedName("swordStone");
	public static Item shovelStone = (new ItemSpade(17, EnumToolMaterial.STONE)).setUnlocalizedName("shovelStone");
	public static Item pickaxeStone = (new ItemPickaxe(18, EnumToolMaterial.STONE)).setUnlocalizedName("pickaxeStone");
	public static Item axeStone = (new ItemAxe(19, EnumToolMaterial.STONE)).setUnlocalizedName("hatchetStone");
	public static Item swordDiamond = (new ItemSword(20, EnumToolMaterial.EMERALD)).setUnlocalizedName("swordDiamond");
	public static Item shovelDiamond = (new ItemSpade(21, EnumToolMaterial.EMERALD)).setUnlocalizedName("shovelDiamond");
	public static Item pickaxeDiamond = (new ItemPickaxe(22, EnumToolMaterial.EMERALD)).setUnlocalizedName("pickaxeDiamond");
	public static Item axeDiamond = (new ItemAxe(23, EnumToolMaterial.EMERALD)).setUnlocalizedName("hatchetDiamond");
	public static Item stick = (new Item(24)).setFull3D().setUnlocalizedName("stick").setCreativeTab(CreativeTabs.tabMaterials);
	public static Item bowlEmpty = (new Item(25)).setUnlocalizedName("bowl").setCreativeTab(CreativeTabs.tabMaterials);
	public static Item bowlSoup = (new ItemSoup(26, 6)).setUnlocalizedName("mushroomStew");
	public static Item swordGold = (new ItemSword(27, EnumToolMaterial.GOLD)).setUnlocalizedName("swordGold");
	public static Item shovelGold = (new ItemSpade(28, EnumToolMaterial.GOLD)).setUnlocalizedName("shovelGold");
	public static Item pickaxeGold = (new ItemPickaxe(29, EnumToolMaterial.GOLD)).setUnlocalizedName("pickaxeGold");
	public static Item axeGold = (new ItemAxe(30, EnumToolMaterial.GOLD)).setUnlocalizedName("hatchetGold");
	public static Item silk = (new ItemReed(31, Block.tripWire)).setUnlocalizedName("string").setCreativeTab(CreativeTabs.tabMaterials);
	public static Item feather = (new Item(32)).setUnlocalizedName("feather").setCreativeTab(CreativeTabs.tabMaterials);
	public static Item gunpowder = (new Item(33)).setUnlocalizedName("sulphur").setPotionEffect(PotionHelper.gunpowderEffect).setCreativeTab(CreativeTabs.tabMaterials);
	public static Item hoeWood = (new ItemHoe(34, EnumToolMaterial.WOOD)).setUnlocalizedName("hoeWood");
	public static Item hoeStone = (new ItemHoe(35, EnumToolMaterial.STONE)).setUnlocalizedName("hoeStone");
	public static Item hoeIron = (new ItemHoe(36, EnumToolMaterial.IRON)).setUnlocalizedName("hoeIron");
	public static Item hoeDiamond = (new ItemHoe(37, EnumToolMaterial.EMERALD)).setUnlocalizedName("hoeDiamond");
	public static Item hoeGold = (new ItemHoe(38, EnumToolMaterial.GOLD)).setUnlocalizedName("hoeGold");
	public static Item seeds = (new ItemSeeds(39, Block.crops.blockID, Block.tilledField.blockID)).setUnlocalizedName("seeds");
	public static Item wheat = (new Item(40)).setUnlocalizedName("wheat").setCreativeTab(CreativeTabs.tabMaterials);
	public static Item bread = (new ItemFood(41, 5, 0.6F, false)).setUnlocalizedName("bread");
	public static ItemArmor helmetLeather = (ItemArmor)(new ItemArmor(42, EnumArmorMaterial.CLOTH, 0, 0)).setUnlocalizedName("helmetCloth");
	public static ItemArmor plateLeather = (ItemArmor)(new ItemArmor(43, EnumArmorMaterial.CLOTH, 0, 1)).setUnlocalizedName("chestplateCloth");
	public static ItemArmor legsLeather = (ItemArmor)(new ItemArmor(44, EnumArmorMaterial.CLOTH, 0, 2)).setUnlocalizedName("leggingsCloth");
	public static ItemArmor bootsLeather = (ItemArmor)(new ItemArmor(45, EnumArmorMaterial.CLOTH, 0, 3)).setUnlocalizedName("bootsCloth");
	public static ItemArmor helmetChain = (ItemArmor)(new ItemArmor(46, EnumArmorMaterial.CHAIN, 1, 0)).setUnlocalizedName("helmetChain");
	public static ItemArmor plateChain = (ItemArmor)(new ItemArmor(47, EnumArmorMaterial.CHAIN, 1, 1)).setUnlocalizedName("chestplateChain");
	public static ItemArmor legsChain = (ItemArmor)(new ItemArmor(48, EnumArmorMaterial.CHAIN, 1, 2)).setUnlocalizedName("leggingsChain");
	public static ItemArmor bootsChain = (ItemArmor)(new ItemArmor(49, EnumArmorMaterial.CHAIN, 1, 3)).setUnlocalizedName("bootsChain");
	public static ItemArmor helmetIron = (ItemArmor)(new ItemArmor(50, EnumArmorMaterial.IRON, 2, 0)).setUnlocalizedName("helmetIron");
	public static ItemArmor plateIron = (ItemArmor)(new ItemArmor(51, EnumArmorMaterial.IRON, 2, 1)).setUnlocalizedName("chestplateIron");
	public static ItemArmor legsIron = (ItemArmor)(new ItemArmor(52, EnumArmorMaterial.IRON, 2, 2)).setUnlocalizedName("leggingsIron");
	public static ItemArmor bootsIron = (ItemArmor)(new ItemArmor(53, EnumArmorMaterial.IRON, 2, 3)).setUnlocalizedName("bootsIron");
	public static ItemArmor helmetDiamond = (ItemArmor)(new ItemArmor(54, EnumArmorMaterial.DIAMOND, 3, 0)).setUnlocalizedName("helmetDiamond");
	public static ItemArmor plateDiamond = (ItemArmor)(new ItemArmor(55, EnumArmorMaterial.DIAMOND, 3, 1)).setUnlocalizedName("chestplateDiamond");
	public static ItemArmor legsDiamond = (ItemArmor)(new ItemArmor(56, EnumArmorMaterial.DIAMOND, 3, 2)).setUnlocalizedName("leggingsDiamond");
	public static ItemArmor bootsDiamond = (ItemArmor)(new ItemArmor(57, EnumArmorMaterial.DIAMOND, 3, 3)).setUnlocalizedName("bootsDiamond");
	public static ItemArmor helmetGold = (ItemArmor)(new ItemArmor(58, EnumArmorMaterial.GOLD, 4, 0)).setUnlocalizedName("helmetGold");
	public static ItemArmor plateGold = (ItemArmor)(new ItemArmor(59, EnumArmorMaterial.GOLD, 4, 1)).setUnlocalizedName("chestplateGold");
	public static ItemArmor legsGold = (ItemArmor)(new ItemArmor(60, EnumArmorMaterial.GOLD, 4, 2)).setUnlocalizedName("leggingsGold");
	public static ItemArmor bootsGold = (ItemArmor)(new ItemArmor(61, EnumArmorMaterial.GOLD, 4, 3)).setUnlocalizedName("bootsGold");
	public static Item flint = (new Item(62)).setUnlocalizedName("flint").setCreativeTab(CreativeTabs.tabMaterials);
	public static Item porkRaw = (new ItemFood(63, 3, 0.3F, true)).setUnlocalizedName("porkchopRaw");
	public static Item porkCooked = (new ItemFood(64, 8, 0.8F, true)).setUnlocalizedName("porkchopCooked");
	public static Item painting = (new ItemHangingEntity(65, EntityPainting.class)).setUnlocalizedName("painting");
	public static Item appleGold = (new ItemAppleGold(66, 4, 1.2F, false)).setAlwaysEdible().setPotionEffect(Potion.regeneration.id, 5, 0, 1.0F).setUnlocalizedName("appleGold");
	public static Item sign = (new ItemSign(67)).setUnlocalizedName("sign");
	public static Item doorWood = (new ItemDoor(68, Material.wood)).setUnlocalizedName("doorWood");
	public static Item bucketEmpty = (new ItemBucket(69, 0)).setUnlocalizedName("bucket").setMaxStackSize(16);
	public static Item bucketWater = (new ItemBucket(70, Block.waterMoving.blockID)).setUnlocalizedName("bucketWater").setContainerItem(bucketEmpty);
	public static Item bucketLava = (new ItemBucket(71, Block.lavaMoving.blockID)).setUnlocalizedName("bucketLava").setContainerItem(bucketEmpty);
	public static Item minecartEmpty = (new ItemMinecart(72, 0)).setUnlocalizedName("minecart");
	public static Item saddle = (new ItemSaddle(73)).setUnlocalizedName("saddle");
	public static Item doorIron = (new ItemDoor(74, Material.iron)).setUnlocalizedName("doorIron");
	public static Item redstone = (new ItemRedstone(75)).setUnlocalizedName("redstone").setPotionEffect(PotionHelper.redstoneEffect);
	public static Item snowball = (new ItemSnowball(76)).setUnlocalizedName("snowball");
	public static Item boat = (new ItemBoat(77)).setUnlocalizedName("boat");
	public static Item leather = (new Item(78)).setUnlocalizedName("leather").setCreativeTab(CreativeTabs.tabMaterials);
	public static Item bucketMilk = (new ItemBucketMilk(79)).setUnlocalizedName("milk").setContainerItem(bucketEmpty);
	public static Item brick = (new Item(80)).setUnlocalizedName("brick").setCreativeTab(CreativeTabs.tabMaterials);
	public static Item clay = (new Item(81)).setUnlocalizedName("clay").setCreativeTab(CreativeTabs.tabMaterials);
	public static Item reed = (new ItemReed(82, Block.reed)).setUnlocalizedName("reeds").setCreativeTab(CreativeTabs.tabMaterials);
	public static Item paper = (new Item(83)).setUnlocalizedName("paper").setCreativeTab(CreativeTabs.tabMisc);
	public static Item book = (new ItemBook(84)).setUnlocalizedName("book").setCreativeTab(CreativeTabs.tabMisc);
	public static Item slimeBall = (new Item(85)).setUnlocalizedName("slimeball").setCreativeTab(CreativeTabs.tabMisc);
	public static Item minecartCrate = (new ItemMinecart(86, 1)).setUnlocalizedName("minecartChest");
	public static Item minecartPowered = (new ItemMinecart(87, 2)).setUnlocalizedName("minecartFurnace");
	public static Item egg = (new ItemEgg(88)).setUnlocalizedName("egg");
	public static Item compass = (new Item(89)).setUnlocalizedName("compass").setCreativeTab(CreativeTabs.tabTools);
	public static ItemFishingRod fishingRod = (ItemFishingRod)(new ItemFishingRod(90)).setUnlocalizedName("fishingRod");
	public static Item pocketSundial = (new Item(91)).setUnlocalizedName("clock").setCreativeTab(CreativeTabs.tabTools);
	public static Item lightStoneDust = (new Item(92)).setUnlocalizedName("yellowDust").setPotionEffect(PotionHelper.glowstoneEffect).setCreativeTab(CreativeTabs.tabMaterials);
	public static Item fishRaw = (new ItemFood(93, 2, 0.3F, false)).setUnlocalizedName("fishRaw");
	public static Item fishCooked = (new ItemFood(94, 5, 0.6F, false)).setUnlocalizedName("fishCooked");
	public static Item dyePowder = (new ItemDye(95)).setUnlocalizedName("dyePowder");
	public static Item bone = (new Item(96)).setUnlocalizedName("bone").setFull3D().setCreativeTab(CreativeTabs.tabMisc);
	public static Item sugar = (new Item(97)).setUnlocalizedName("sugar").setPotionEffect(PotionHelper.sugarEffect).setCreativeTab(CreativeTabs.tabMaterials);
	public static Item cake = (new ItemReed(98, Block.cake)).setMaxStackSize(1).setUnlocalizedName("cake").setCreativeTab(CreativeTabs.tabFood);
	public static Item bed = (new ItemBed(99)).setMaxStackSize(1).setUnlocalizedName("bed");
	public static Item redstoneRepeater = (new ItemReed(100, Block.redstoneRepeaterIdle)).setUnlocalizedName("diode").setCreativeTab(CreativeTabs.tabRedstone);
	public static Item cookie = (new ItemFood(101, 2, 0.1F, false)).setUnlocalizedName("cookie");
	public static ItemMap map = (ItemMap)(new ItemMap(102)).setUnlocalizedName("map");
	public static ItemShears shears = (ItemShears)(new ItemShears(103)).setUnlocalizedName("shears");
	public static Item melon = (new ItemFood(104, 2, 0.3F, false)).setUnlocalizedName("melon");
	public static Item pumpkinSeeds = (new ItemSeeds(105, Block.pumpkinStem.blockID, Block.tilledField.blockID)).setUnlocalizedName("seeds_pumpkin");
	public static Item melonSeeds = (new ItemSeeds(106, Block.melonStem.blockID, Block.tilledField.blockID)).setUnlocalizedName("seeds_melon");
	public static Item beefRaw = (new ItemFood(107, 3, 0.3F, true)).setUnlocalizedName("beefRaw");
	public static Item beefCooked = (new ItemFood(108, 8, 0.8F, true)).setUnlocalizedName("beefCooked");
	public static Item chickenRaw = (new ItemFood(109, 2, 0.3F, true)).setPotionEffect(Potion.hunger.id, 30, 0, 0.3F).setUnlocalizedName("chickenRaw");
	public static Item chickenCooked = (new ItemFood(110, 6, 0.6F, true)).setUnlocalizedName("chickenCooked");
	public static Item rottenFlesh = (new ItemFood(111, 4, 0.1F, true)).setPotionEffect(Potion.hunger.id, 30, 0, 0.8F).setUnlocalizedName("rottenFlesh");
	public static Item enderPearl = (new ItemEnderPearl(112)).setUnlocalizedName("enderPearl");
	public static Item blazeRod = (new Item(113)).setUnlocalizedName("blazeRod").setCreativeTab(CreativeTabs.tabMaterials);
	public static Item ghastTear = (new Item(114)).setUnlocalizedName("ghastTear").setPotionEffect(PotionHelper.ghastTearEffect).setCreativeTab(CreativeTabs.tabBrewing);
	public static Item goldNugget = (new Item(115)).setUnlocalizedName("goldNugget").setCreativeTab(CreativeTabs.tabMaterials);
	public static Item netherStalkSeeds = (new ItemSeeds(116, Block.netherStalk.blockID, Block.slowSand.blockID)).setUnlocalizedName("netherStalkSeeds").setPotionEffect("+4");
	public static ItemPotion potion = (ItemPotion)(new ItemPotion(117)).setUnlocalizedName("potion");
	public static Item glassBottle = (new ItemGlassBottle(118)).setUnlocalizedName("glassBottle");
	public static Item spiderEye = (new ItemFood(119, 2, 0.8F, false)).setPotionEffect(Potion.poison.id, 5, 0, 1.0F).setUnlocalizedName("spiderEye").setPotionEffect(PotionHelper.spiderEyeEffect);
	public static Item fermentedSpiderEye = (new Item(120)).setUnlocalizedName("fermentedSpiderEye").setPotionEffect(PotionHelper.fermentedSpiderEyeEffect).setCreativeTab(CreativeTabs.tabBrewing);
	public static Item blazePowder = (new Item(121)).setUnlocalizedName("blazePowder").setPotionEffect(PotionHelper.blazePowderEffect).setCreativeTab(CreativeTabs.tabBrewing);
	public static Item magmaCream = (new Item(122)).setUnlocalizedName("magmaCream").setPotionEffect(PotionHelper.magmaCreamEffect).setCreativeTab(CreativeTabs.tabBrewing);
	public static Item brewingStand = (new ItemReed(123, Block.brewingStand)).setUnlocalizedName("brewingStand").setCreativeTab(CreativeTabs.tabBrewing);
	public static Item cauldron = (new ItemReed(124, Block.cauldron)).setUnlocalizedName("cauldron").setCreativeTab(CreativeTabs.tabBrewing);
	public static Item eyeOfEnder = (new ItemEnderEye(125)).setUnlocalizedName("eyeOfEnder");
	public static Item speckledMelon = (new Item(126)).setUnlocalizedName("speckledMelon").setPotionEffect(PotionHelper.speckledMelonEffect).setCreativeTab(CreativeTabs.tabBrewing);
	public static Item monsterPlacer = (new ItemMonsterPlacer(127)).setUnlocalizedName("monsterPlacer");
	public static Item expBottle = (new ItemExpBottle(128)).setUnlocalizedName("expBottle");
	public static Item fireballCharge = (new ItemFireball(129)).setUnlocalizedName("fireball");
	public static Item writableBook = (new ItemWritableBook(130)).setUnlocalizedName("writingBook").setCreativeTab(CreativeTabs.tabMisc);
	public static Item writtenBook = (new ItemEditableBook(131)).setUnlocalizedName("writtenBook");
	public static Item emerald = (new Item(132)).setUnlocalizedName("emerald").setCreativeTab(CreativeTabs.tabMaterials);
	public static Item itemFrame = (new ItemHangingEntity(133, EntityItemFrame.class)).setUnlocalizedName("frame");
	public static Item flowerPot = (new ItemReed(134, Block.flowerPot)).setUnlocalizedName("flowerPot").setCreativeTab(CreativeTabs.tabDecorations);
	public static Item carrot = (new ItemSeedFood(135, 4, 0.6F, Block.carrot.blockID, Block.tilledField.blockID)).setUnlocalizedName("carrots");
	public static Item potato = (new ItemSeedFood(136, 1, 0.3F, Block.potato.blockID, Block.tilledField.blockID)).setUnlocalizedName("potato");
	public static Item bakedPotato = (new ItemFood(137, 6, 0.6F, false)).setUnlocalizedName("potatoBaked");
	public static Item poisonousPotato = (new ItemFood(138, 2, 0.3F, false)).setPotionEffect(Potion.poison.id, 5, 0, 0.6F).setUnlocalizedName("potatoPoisonous");
	public static ItemEmptyMap emptyMap = (ItemEmptyMap)(new ItemEmptyMap(139)).setUnlocalizedName("emptyMap");
	public static Item goldenCarrot = (new ItemFood(140, 6, 1.2F, false)).setUnlocalizedName("carrotGolden").setPotionEffect(PotionHelper.goldenCarrotEffect);
	public static Item skull = (new ItemSkull(141)).setUnlocalizedName("skull");
	public static Item carrotOnAStick = (new ItemCarrotOnAStick(142)).setUnlocalizedName("carrotOnAStick");
	public static Item netherStar = (new ItemSimpleFoiled(143)).setUnlocalizedName("netherStar").setCreativeTab(CreativeTabs.tabMaterials);
	public static Item pumpkinPie = (new ItemFood(144, 8, 0.3F, false)).setUnlocalizedName("pumpkinPie").setCreativeTab(CreativeTabs.tabFood);
	public static Item firework = (new ItemFirework(145)).setUnlocalizedName("fireworks");
	public static Item fireworkCharge = (new ItemFireworkCharge(146)).setUnlocalizedName("fireworksCharge").setCreativeTab(CreativeTabs.tabMisc);
	public static ItemEnchantedBook enchantedBook = (ItemEnchantedBook)(new ItemEnchantedBook(147)).setMaxStackSize(1).setUnlocalizedName("enchantedBook");
	public static Item comparator = (new ItemReed(148, Block.redstoneComparatorIdle)).setUnlocalizedName("comparator").setCreativeTab(CreativeTabs.tabRedstone);
	public static Item netherrackBrick = (new Item(149)).setUnlocalizedName("netherbrick").setCreativeTab(CreativeTabs.tabMaterials);
	public static Item netherQuartz = (new Item(150)).setUnlocalizedName("netherquartz").setCreativeTab(CreativeTabs.tabMaterials);
	public static Item minecartTnt = (new ItemMinecart(151, 3)).setUnlocalizedName("minecartTnt");
	public static Item minecartHopper = (new ItemMinecart(152, 5)).setUnlocalizedName("minecartHopper");
	public static Item record13 = (new ItemRecord(2000, "13")).setUnlocalizedName("record");
	public static Item recordCat = (new ItemRecord(2001, "cat")).setUnlocalizedName("record");
	public static Item recordBlocks = (new ItemRecord(2002, "blocks")).setUnlocalizedName("record");
	public static Item recordChirp = (new ItemRecord(2003, "chirp")).setUnlocalizedName("record");
	public static Item recordFar = (new ItemRecord(2004, "far")).setUnlocalizedName("record");
	public static Item recordMall = (new ItemRecord(2005, "mall")).setUnlocalizedName("record");
	public static Item recordMellohi = (new ItemRecord(2006, "mellohi")).setUnlocalizedName("record");
	public static Item recordStal = (new ItemRecord(2007, "stal")).setUnlocalizedName("record");
	public static Item recordStrad = (new ItemRecord(2008, "strad")).setUnlocalizedName("record");
	public static Item recordWard = (new ItemRecord(2009, "ward")).setUnlocalizedName("record");
	public static Item record11 = (new ItemRecord(2010, "11")).setUnlocalizedName("record");
	public static Item recordWait = (new ItemRecord(2011, "wait")).setUnlocalizedName("record");
	public final int itemID;
	protected int maxStackSize = 64;
	private int maxDamage = 0;
	protected boolean bFull3D = false;
	protected boolean hasSubtypes = false;
	private Item containerItem = null;
	private String potionEffect = null;
	private String unlocalizedName;
	protected Icon itemIcon;

	protected Item(int var1) {
		this.itemID = 256 + var1;
		if(itemsList[256 + var1] != null) {
			System.out.println("CONFLICT @ " + var1);
		}

		itemsList[256 + var1] = this;
	}

	public Item setMaxStackSize(int var1) {
		this.maxStackSize = var1;
		return this;
	}

	public int getSpriteNumber() {
		return 1;
	}

	public Icon getIconFromDamage(int var1) {
		return this.itemIcon;
	}

	public final Icon getIconIndex(ItemStack var1) {
		return this.getIconFromDamage(var1.getItemDamage());
	}

	public boolean onItemUse(ItemStack var1, EntityPlayer var2, World var3, int var4, int var5, int var6, int var7, float var8, float var9, float var10) {
		return false;
	}

	public float getStrVsBlock(ItemStack var1, Block var2) {
		return 1.0F;
	}

	public ItemStack onItemRightClick(ItemStack var1, World var2, EntityPlayer var3) {
		return var1;
	}

	public ItemStack onEaten(ItemStack var1, World var2, EntityPlayer var3) {
		return var1;
	}

	public int getItemStackLimit() {
		return this.maxStackSize;
	}

	public int getMetadata(int var1) {
		return 0;
	}

	public boolean getHasSubtypes() {
		return this.hasSubtypes;
	}

	protected Item setHasSubtypes(boolean var1) {
		this.hasSubtypes = var1;
		return this;
	}

	public int getMaxDamage() {
		return this.maxDamage;
	}

	protected Item setMaxDamage(int var1) {
		this.maxDamage = var1;
		return this;
	}

	public boolean isDamageable() {
		return this.maxDamage > 0 && !this.hasSubtypes;
	}

	public boolean hitEntity(ItemStack var1, EntityLiving var2, EntityLiving var3) {
		return false;
	}

	public boolean onBlockDestroyed(ItemStack var1, World var2, int var3, int var4, int var5, int var6, EntityLiving var7) {
		return false;
	}

	public int getDamageVsEntity(Entity var1) {
		return 1;
	}

	public boolean canHarvestBlock(Block var1) {
		return false;
	}

	public boolean itemInteractionForEntity(ItemStack var1, EntityLiving var2) {
		return false;
	}

	public Item setFull3D() {
		this.bFull3D = true;
		return this;
	}

	public boolean isFull3D() {
		return this.bFull3D;
	}

	public boolean shouldRotateAroundWhenRendering() {
		return false;
	}

	public Item setUnlocalizedName(String var1) {
		this.unlocalizedName = var1;
		return this;
	}

	public String getLocalizedName(ItemStack var1) {
		String var2 = this.getUnlocalizedName(var1);
		return var2 == null ? "" : StatCollector.translateToLocal(var2);
	}

	public String getUnlocalizedName() {
		return "item." + this.unlocalizedName;
	}

	public String getUnlocalizedName(ItemStack var1) {
		return "item." + this.unlocalizedName;
	}

	public Item setContainerItem(Item var1) {
		this.containerItem = var1;
		return this;
	}

	public boolean doesContainerItemLeaveCraftingGrid(ItemStack var1) {
		return true;
	}

	public boolean getShareTag() {
		return true;
	}

	public Item getContainerItem() {
		return this.containerItem;
	}

	public boolean hasContainerItem() {
		return this.containerItem != null;
	}

	public String getStatName() {
		return StatCollector.translateToLocal(this.getUnlocalizedName() + ".name");
	}

	public String func_77653_i(ItemStack var1) {
		return StatCollector.translateToLocal(this.getUnlocalizedName(var1) + ".name");
	}

	public int getColorFromItemStack(ItemStack var1, int var2) {
		return 16777215;
	}

	public void onUpdate(ItemStack var1, World var2, Entity var3, int var4, boolean var5) {
	}

	public void onCreated(ItemStack var1, World var2, EntityPlayer var3) {
	}

	public boolean isMap() {
		return false;
	}

	public EnumAction getItemUseAction(ItemStack var1) {
		return EnumAction.none;
	}

	public int getMaxItemUseDuration(ItemStack var1) {
		return 0;
	}

	public void onPlayerStoppedUsing(ItemStack var1, World var2, EntityPlayer var3, int var4) {
	}

	protected Item setPotionEffect(String var1) {
		this.potionEffect = var1;
		return this;
	}

	public String getPotionEffect() {
		return this.potionEffect;
	}

	public boolean isPotionIngredient() {
		return this.potionEffect != null;
	}

	public void addInformation(ItemStack var1, EntityPlayer var2, List var3, boolean var4) {
	}

	public String getItemDisplayName(ItemStack var1) {
		return ("" + StringTranslate.getInstance().translateNamedKey(this.getLocalizedName(var1))).trim();
	}

	public boolean hasEffect(ItemStack var1) {
		return var1.isItemEnchanted();
	}

	public EnumRarity getRarity(ItemStack var1) {
		return var1.isItemEnchanted() ? EnumRarity.rare : EnumRarity.common;
	}

	public boolean isItemTool(ItemStack var1) {
		return this.getItemStackLimit() == 1 && this.isDamageable();
	}

	protected MovingObjectPosition getMovingObjectPositionFromPlayer(World var1, EntityPlayer var2, boolean var3) {
		float var4 = 1.0F;
		float var5 = var2.prevRotationPitch + (var2.rotationPitch - var2.prevRotationPitch) * var4;
		float var6 = var2.prevRotationYaw + (var2.rotationYaw - var2.prevRotationYaw) * var4;
		double var7 = var2.prevPosX + (var2.posX - var2.prevPosX) * (double)var4;
		double var9 = var2.prevPosY + (var2.posY - var2.prevPosY) * (double)var4 + 1.62D - (double)var2.yOffset;
		double var11 = var2.prevPosZ + (var2.posZ - var2.prevPosZ) * (double)var4;
		Vec3 var13 = var1.getWorldVec3Pool().getVecFromPool(var7, var9, var11);
		float var14 = MathHelper.cos(-var6 * ((float)Math.PI / 180.0F) - (float)Math.PI);
		float var15 = MathHelper.sin(-var6 * ((float)Math.PI / 180.0F) - (float)Math.PI);
		float var16 = -MathHelper.cos(-var5 * ((float)Math.PI / 180.0F));
		float var17 = MathHelper.sin(-var5 * ((float)Math.PI / 180.0F));
		float var18 = var15 * var16;
		float var20 = var14 * var16;
		double var21 = 5.0D;
		Vec3 var23 = var13.addVector((double)var18 * var21, (double)var17 * var21, (double)var20 * var21);
		return var1.rayTraceBlocks_do_do(var13, var23, var3, !var3);
	}

	public int getItemEnchantability() {
		return 0;
	}

	public boolean requiresMultipleRenderPasses() {
		return false;
	}

	public Icon getIconFromDamageForRenderPass(int var1, int var2) {
		return this.getIconFromDamage(var1);
	}

	public void getSubItems(int var1, CreativeTabs var2, List var3) {
		var3.add(new ItemStack(var1, 1, 0));
	}

	public CreativeTabs getCreativeTab() {
		return this.tabToDisplayOn;
	}

	public Item setCreativeTab(CreativeTabs var1) {
		this.tabToDisplayOn = var1;
		return this;
	}

	public boolean func_82788_x() {
		return true;
	}

	public boolean getIsRepairable(ItemStack var1, ItemStack var2) {
		return false;
	}

	public void registerIcons(IconRegister var1) {
		this.itemIcon = var1.registerIcon(this.unlocalizedName);
	}

	static {
		StatList.initStats();
	}
}
