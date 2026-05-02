package net.minecraft.src;

import java.util.List;
import java.util.Random;

public class Block {
	private CreativeTabs displayOnCreativeTab;
	public static final StepSound soundPowderFootstep = new StepSound("stone", 1.0F, 1.0F);
	public static final StepSound soundWoodFootstep = new StepSound("wood", 1.0F, 1.0F);
	public static final StepSound soundGravelFootstep = new StepSound("gravel", 1.0F, 1.0F);
	public static final StepSound soundGrassFootstep = new StepSound("grass", 1.0F, 1.0F);
	public static final StepSound soundStoneFootstep = new StepSound("stone", 1.0F, 1.0F);
	public static final StepSound soundMetalFootstep = new StepSound("stone", 1.0F, 1.5F);
	public static final StepSound soundGlassFootstep = new StepSoundStone("stone", 1.0F, 1.0F);
	public static final StepSound soundClothFootstep = new StepSound("cloth", 1.0F, 1.0F);
	public static final StepSound soundSandFootstep = new StepSound("sand", 1.0F, 1.0F);
	public static final StepSound soundSnowFootstep = new StepSound("snow", 1.0F, 1.0F);
	public static final StepSound soundLadderFootstep = new StepSoundSand("ladder", 1.0F, 1.0F);
	public static final StepSound soundAnvilFootstep = new StepSoundAnvil("anvil", 0.3F, 1.0F);
	public static final Block[] blocksList = new Block[4096];
	public static final boolean[] opaqueCubeLookup = new boolean[4096];
	public static final int[] lightOpacity = new int[4096];
	public static final boolean[] canBlockGrass = new boolean[4096];
	public static final int[] lightValue = new int[4096];
	public static boolean[] useNeighborBrightness = new boolean[4096];
	public static final Block stone = (new BlockStone(1)).setHardness(1.5F).setResistance(10.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("stone");
	public static final BlockGrass grass = (BlockGrass)(new BlockGrass(2)).setHardness(0.6F).setStepSound(soundGrassFootstep).setUnlocalizedName("grass");
	public static final Block dirt = (new BlockDirt(3)).setHardness(0.5F).setStepSound(soundGravelFootstep).setUnlocalizedName("dirt");
	public static final Block cobblestone = (new Block(4, Material.rock)).setHardness(2.0F).setResistance(10.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("stonebrick").setCreativeTab(CreativeTabs.tabBlock);
	public static final Block planks = (new BlockWood(5)).setHardness(2.0F).setResistance(5.0F).setStepSound(soundWoodFootstep).setUnlocalizedName("wood");
	public static final Block sapling = (new BlockSapling(6)).setHardness(0.0F).setStepSound(soundGrassFootstep).setUnlocalizedName("sapling");
	public static final Block bedrock = (new Block(7, Material.rock)).setBlockUnbreakable().setResistance(6000000.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("bedrock").disableStats().setCreativeTab(CreativeTabs.tabBlock);
	public static final BlockFluid waterMoving = (BlockFluid)(new BlockFlowing(8, Material.water)).setHardness(100.0F).setLightOpacity(3).setUnlocalizedName("water").disableStats();
	public static final Block waterStill = (new BlockStationary(9, Material.water)).setHardness(100.0F).setLightOpacity(3).setUnlocalizedName("water").disableStats();
	public static final BlockFluid lavaMoving = (BlockFluid)(new BlockFlowing(10, Material.lava)).setHardness(0.0F).setLightValue(1.0F).setUnlocalizedName("lava").disableStats();
	public static final Block lavaStill = (new BlockStationary(11, Material.lava)).setHardness(100.0F).setLightValue(1.0F).setUnlocalizedName("lava").disableStats();
	public static final Block sand = (new BlockSand(12)).setHardness(0.5F).setStepSound(soundSandFootstep).setUnlocalizedName("sand");
	public static final Block gravel = (new BlockGravel(13)).setHardness(0.6F).setStepSound(soundGravelFootstep).setUnlocalizedName("gravel");
	public static final Block oreGold = (new BlockOre(14)).setHardness(3.0F).setResistance(5.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("oreGold");
	public static final Block oreIron = (new BlockOre(15)).setHardness(3.0F).setResistance(5.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("oreIron");
	public static final Block oreCoal = (new BlockOre(16)).setHardness(3.0F).setResistance(5.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("oreCoal");
	public static final Block wood = (new BlockLog(17)).setHardness(2.0F).setStepSound(soundWoodFootstep).setUnlocalizedName("log");
	public static final BlockLeaves leaves = (BlockLeaves)(new BlockLeaves(18)).setHardness(0.2F).setLightOpacity(1).setStepSound(soundGrassFootstep).setUnlocalizedName("leaves");
	public static final Block sponge = (new BlockSponge(19)).setHardness(0.6F).setStepSound(soundGrassFootstep).setUnlocalizedName("sponge");
	public static final Block glass = (new BlockGlass(20, Material.glass, false)).setHardness(0.3F).setStepSound(soundGlassFootstep).setUnlocalizedName("glass");
	public static final Block oreLapis = (new BlockOre(21)).setHardness(3.0F).setResistance(5.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("oreLapis");
	public static final Block blockLapis = (new Block(22, Material.rock)).setHardness(3.0F).setResistance(5.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("blockLapis").setCreativeTab(CreativeTabs.tabBlock);
	public static final Block dispenser = (new BlockDispenser(23)).setHardness(3.5F).setStepSound(soundStoneFootstep).setUnlocalizedName("dispenser");
	public static final Block sandStone = (new BlockSandStone(24)).setStepSound(soundStoneFootstep).setHardness(0.8F).setUnlocalizedName("sandStone");
	public static final Block music = (new BlockNote(25)).setHardness(0.8F).setUnlocalizedName("musicBlock");
	public static final Block bed = (new BlockBed(26)).setHardness(0.2F).setUnlocalizedName("bed").disableStats();
	public static final Block railPowered = (new BlockRailPowered(27)).setHardness(0.7F).setStepSound(soundMetalFootstep).setUnlocalizedName("goldenRail");
	public static final Block railDetector = (new BlockDetectorRail(28)).setHardness(0.7F).setStepSound(soundMetalFootstep).setUnlocalizedName("detectorRail");
	public static final BlockPistonBase pistonStickyBase = (BlockPistonBase)(new BlockPistonBase(29, true)).setUnlocalizedName("pistonStickyBase");
	public static final Block web = (new BlockWeb(30)).setLightOpacity(1).setHardness(4.0F).setUnlocalizedName("web");
	public static final BlockTallGrass tallGrass = (BlockTallGrass)(new BlockTallGrass(31)).setHardness(0.0F).setStepSound(soundGrassFootstep).setUnlocalizedName("tallgrass");
	public static final BlockDeadBush deadBush = (BlockDeadBush)(new BlockDeadBush(32)).setHardness(0.0F).setStepSound(soundGrassFootstep).setUnlocalizedName("deadbush");
	public static final BlockPistonBase pistonBase = (BlockPistonBase)(new BlockPistonBase(33, false)).setUnlocalizedName("pistonBase");
	public static final BlockPistonExtension pistonExtension = new BlockPistonExtension(34);
	public static final Block cloth = (new BlockCloth()).setHardness(0.8F).setStepSound(soundClothFootstep).setUnlocalizedName("cloth");
	public static final BlockPistonMoving pistonMoving = new BlockPistonMoving(36);
	public static final BlockFlower plantYellow = (BlockFlower)(new BlockFlower(37)).setHardness(0.0F).setStepSound(soundGrassFootstep).setUnlocalizedName("flower");
	public static final BlockFlower plantRed = (BlockFlower)(new BlockFlower(38)).setHardness(0.0F).setStepSound(soundGrassFootstep).setUnlocalizedName("rose");
	public static final BlockFlower mushroomBrown = (BlockFlower)(new BlockMushroom(39, "mushroom_brown")).setHardness(0.0F).setStepSound(soundGrassFootstep).setLightValue(2.0F / 16.0F).setUnlocalizedName("mushroom");
	public static final BlockFlower mushroomRed = (BlockFlower)(new BlockMushroom(40, "mushroom_red")).setHardness(0.0F).setStepSound(soundGrassFootstep).setUnlocalizedName("mushroom");
	public static final Block blockGold = (new BlockOreStorage(41)).setHardness(3.0F).setResistance(10.0F).setStepSound(soundMetalFootstep).setUnlocalizedName("blockGold");
	public static final Block blockIron = (new BlockOreStorage(42)).setHardness(5.0F).setResistance(10.0F).setStepSound(soundMetalFootstep).setUnlocalizedName("blockIron");
	public static final BlockHalfSlab stoneDoubleSlab = (BlockHalfSlab)(new BlockStep(43, true)).setHardness(2.0F).setResistance(10.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("stoneSlab");
	public static final BlockHalfSlab stoneSingleSlab = (BlockHalfSlab)(new BlockStep(44, false)).setHardness(2.0F).setResistance(10.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("stoneSlab");
	public static final Block brick = (new Block(45, Material.rock)).setHardness(2.0F).setResistance(10.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("brick").setCreativeTab(CreativeTabs.tabBlock);
	public static final Block tnt = (new BlockTNT(46)).setHardness(0.0F).setStepSound(soundGrassFootstep).setUnlocalizedName("tnt");
	public static final Block bookShelf = (new BlockBookshelf(47)).setHardness(1.5F).setStepSound(soundWoodFootstep).setUnlocalizedName("bookshelf");
	public static final Block cobblestoneMossy = (new Block(48, Material.rock)).setHardness(2.0F).setResistance(10.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("stoneMoss").setCreativeTab(CreativeTabs.tabBlock);
	public static final Block obsidian = (new BlockObsidian(49)).setHardness(50.0F).setResistance(2000.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("obsidian");
	public static final Block torchWood = (new BlockTorch(50)).setHardness(0.0F).setLightValue(15.0F / 16.0F).setStepSound(soundWoodFootstep).setUnlocalizedName("torch");
	public static final BlockFire fire = (BlockFire)(new BlockFire(51)).setHardness(0.0F).setLightValue(1.0F).setStepSound(soundWoodFootstep).setUnlocalizedName("fire").disableStats();
	public static final Block mobSpawner = (new BlockMobSpawner(52)).setHardness(5.0F).setStepSound(soundMetalFootstep).setUnlocalizedName("mobSpawner").disableStats();
	public static final Block stairsWoodOak = (new BlockStairs(53, planks, 0)).setUnlocalizedName("stairsWood");
	public static final BlockChest chest = (BlockChest)(new BlockChest(54, 0)).setHardness(2.5F).setStepSound(soundWoodFootstep).setUnlocalizedName("chest");
	public static final BlockRedstoneWire redstoneWire = (BlockRedstoneWire)(new BlockRedstoneWire(55)).setHardness(0.0F).setStepSound(soundPowderFootstep).setUnlocalizedName("redstoneDust").disableStats();
	public static final Block oreDiamond = (new BlockOre(56)).setHardness(3.0F).setResistance(5.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("oreDiamond");
	public static final Block blockDiamond = (new BlockOreStorage(57)).setHardness(5.0F).setResistance(10.0F).setStepSound(soundMetalFootstep).setUnlocalizedName("blockDiamond");
	public static final Block workbench = (new BlockWorkbench(58)).setHardness(2.5F).setStepSound(soundWoodFootstep).setUnlocalizedName("workbench");
	public static final Block crops = (new BlockCrops(59)).setUnlocalizedName("crops");
	public static final Block tilledField = (new BlockFarmland(60)).setHardness(0.6F).setStepSound(soundGravelFootstep).setUnlocalizedName("farmland");
	public static final Block furnaceIdle = (new BlockFurnace(61, false)).setHardness(3.5F).setStepSound(soundStoneFootstep).setUnlocalizedName("furnace").setCreativeTab(CreativeTabs.tabDecorations);
	public static final Block furnaceBurning = (new BlockFurnace(62, true)).setHardness(3.5F).setStepSound(soundStoneFootstep).setLightValue(14.0F / 16.0F).setUnlocalizedName("furnace");
	public static final Block signPost = (new BlockSign(63, TileEntitySign.class, true)).setHardness(1.0F).setStepSound(soundWoodFootstep).setUnlocalizedName("sign").disableStats();
	public static final Block doorWood = (new BlockDoor(64, Material.wood)).setHardness(3.0F).setStepSound(soundWoodFootstep).setUnlocalizedName("doorWood").disableStats();
	public static final Block ladder = (new BlockLadder(65)).setHardness(0.4F).setStepSound(soundLadderFootstep).setUnlocalizedName("ladder");
	public static final Block rail = (new BlockRail(66)).setHardness(0.7F).setStepSound(soundMetalFootstep).setUnlocalizedName("rail");
	public static final Block stairsCobblestone = (new BlockStairs(67, cobblestone, 0)).setUnlocalizedName("stairsStone");
	public static final Block signWall = (new BlockSign(68, TileEntitySign.class, false)).setHardness(1.0F).setStepSound(soundWoodFootstep).setUnlocalizedName("sign").disableStats();
	public static final Block lever = (new BlockLever(69)).setHardness(0.5F).setStepSound(soundWoodFootstep).setUnlocalizedName("lever");
	public static final Block pressurePlateStone = (new BlockPressurePlate(70, "stone", Material.rock, EnumMobType.mobs)).setHardness(0.5F).setStepSound(soundStoneFootstep).setUnlocalizedName("pressurePlate");
	public static final Block doorIron = (new BlockDoor(71, Material.iron)).setHardness(5.0F).setStepSound(soundMetalFootstep).setUnlocalizedName("doorIron").disableStats();
	public static final Block pressurePlatePlanks = (new BlockPressurePlate(72, "wood", Material.wood, EnumMobType.everything)).setHardness(0.5F).setStepSound(soundWoodFootstep).setUnlocalizedName("pressurePlate");
	public static final Block oreRedstone = (new BlockRedstoneOre(73, false)).setHardness(3.0F).setResistance(5.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("oreRedstone").setCreativeTab(CreativeTabs.tabBlock);
	public static final Block oreRedstoneGlowing = (new BlockRedstoneOre(74, true)).setLightValue(10.0F / 16.0F).setHardness(3.0F).setResistance(5.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("oreRedstone");
	public static final Block torchRedstoneIdle = (new BlockRedstoneTorch(75, false)).setHardness(0.0F).setStepSound(soundWoodFootstep).setUnlocalizedName("notGate");
	public static final Block torchRedstoneActive = (new BlockRedstoneTorch(76, true)).setHardness(0.0F).setLightValue(0.5F).setStepSound(soundWoodFootstep).setUnlocalizedName("notGate").setCreativeTab(CreativeTabs.tabRedstone);
	public static final Block stoneButton = (new BlockButtonStone(77)).setHardness(0.5F).setStepSound(soundStoneFootstep).setUnlocalizedName("button");
	public static final Block snow = (new BlockSnow(78)).setHardness(0.1F).setStepSound(soundSnowFootstep).setUnlocalizedName("snow").setLightOpacity(0);
	public static final Block ice = (new BlockIce(79)).setHardness(0.5F).setLightOpacity(3).setStepSound(soundGlassFootstep).setUnlocalizedName("ice");
	public static final Block blockSnow = (new BlockSnowBlock(80)).setHardness(0.2F).setStepSound(soundSnowFootstep).setUnlocalizedName("snow");
	public static final Block cactus = (new BlockCactus(81)).setHardness(0.4F).setStepSound(soundClothFootstep).setUnlocalizedName("cactus");
	public static final Block blockClay = (new BlockClay(82)).setHardness(0.6F).setStepSound(soundGravelFootstep).setUnlocalizedName("clay");
	public static final Block reed = (new BlockReed(83)).setHardness(0.0F).setStepSound(soundGrassFootstep).setUnlocalizedName("reeds").disableStats();
	public static final Block jukebox = (new BlockJukeBox(84)).setHardness(2.0F).setResistance(10.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("jukebox");
	public static final Block fence = (new BlockFence(85, "wood", Material.wood)).setHardness(2.0F).setResistance(5.0F).setStepSound(soundWoodFootstep).setUnlocalizedName("fence");
	public static final Block pumpkin = (new BlockPumpkin(86, false)).setHardness(1.0F).setStepSound(soundWoodFootstep).setUnlocalizedName("pumpkin");
	public static final Block netherrack = (new BlockNetherrack(87)).setHardness(0.4F).setStepSound(soundStoneFootstep).setUnlocalizedName("hellrock");
	public static final Block slowSand = (new BlockSoulSand(88)).setHardness(0.5F).setStepSound(soundSandFootstep).setUnlocalizedName("hellsand");
	public static final Block glowStone = (new BlockGlowStone(89, Material.glass)).setHardness(0.3F).setStepSound(soundGlassFootstep).setLightValue(1.0F).setUnlocalizedName("lightgem");
	public static final BlockPortal portal = (BlockPortal)(new BlockPortal(90)).setHardness(-1.0F).setStepSound(soundGlassFootstep).setLightValue(12.0F / 16.0F).setUnlocalizedName("portal");
	public static final Block pumpkinLantern = (new BlockPumpkin(91, true)).setHardness(1.0F).setStepSound(soundWoodFootstep).setLightValue(1.0F).setUnlocalizedName("litpumpkin");
	public static final Block cake = (new BlockCake(92)).setHardness(0.5F).setStepSound(soundClothFootstep).setUnlocalizedName("cake").disableStats();
	public static final BlockRedstoneRepeater redstoneRepeaterIdle = (BlockRedstoneRepeater)(new BlockRedstoneRepeater(93, false)).setHardness(0.0F).setStepSound(soundWoodFootstep).setUnlocalizedName("diode").disableStats();
	public static final BlockRedstoneRepeater redstoneRepeaterActive = (BlockRedstoneRepeater)(new BlockRedstoneRepeater(94, true)).setHardness(0.0F).setLightValue(10.0F / 16.0F).setStepSound(soundWoodFootstep).setUnlocalizedName("diode").disableStats();
	public static final Block lockedChest = (new BlockLockedChest(95)).setHardness(0.0F).setLightValue(1.0F).setStepSound(soundWoodFootstep).setUnlocalizedName("lockedchest").setTickRandomly(true);
	public static final Block trapdoor = (new BlockTrapDoor(96, Material.wood)).setHardness(3.0F).setStepSound(soundWoodFootstep).setUnlocalizedName("trapdoor").disableStats();
	public static final Block silverfish = (new BlockSilverfish(97)).setHardness(12.0F / 16.0F).setUnlocalizedName("monsterStoneEgg");
	public static final Block stoneBrick = (new BlockStoneBrick(98)).setHardness(1.5F).setResistance(10.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("stonebricksmooth");
	public static final Block mushroomCapBrown = (new BlockMushroomCap(99, Material.wood, 0)).setHardness(0.2F).setStepSound(soundWoodFootstep).setUnlocalizedName("mushroom");
	public static final Block mushroomCapRed = (new BlockMushroomCap(100, Material.wood, 1)).setHardness(0.2F).setStepSound(soundWoodFootstep).setUnlocalizedName("mushroom");
	public static final Block fenceIron = (new BlockPane(101, "fenceIron", "fenceIron", Material.iron, true)).setHardness(5.0F).setResistance(10.0F).setStepSound(soundMetalFootstep).setUnlocalizedName("fenceIron");
	public static final Block thinGlass = (new BlockPane(102, "glass", "thinglass_top", Material.glass, false)).setHardness(0.3F).setStepSound(soundGlassFootstep).setUnlocalizedName("thinGlass");
	public static final Block melon = (new BlockMelon(103)).setHardness(1.0F).setStepSound(soundWoodFootstep).setUnlocalizedName("melon");
	public static final Block pumpkinStem = (new BlockStem(104, pumpkin)).setHardness(0.0F).setStepSound(soundWoodFootstep).setUnlocalizedName("pumpkinStem");
	public static final Block melonStem = (new BlockStem(105, melon)).setHardness(0.0F).setStepSound(soundWoodFootstep).setUnlocalizedName("pumpkinStem");
	public static final Block vine = (new BlockVine(106)).setHardness(0.2F).setStepSound(soundGrassFootstep).setUnlocalizedName("vine");
	public static final Block fenceGate = (new BlockFenceGate(107)).setHardness(2.0F).setResistance(5.0F).setStepSound(soundWoodFootstep).setUnlocalizedName("fenceGate");
	public static final Block stairsBrick = (new BlockStairs(108, brick, 0)).setUnlocalizedName("stairsBrick");
	public static final Block stairsStoneBrick = (new BlockStairs(109, stoneBrick, 0)).setUnlocalizedName("stairsStoneBrickSmooth");
	public static final BlockMycelium mycelium = (BlockMycelium)(new BlockMycelium(110)).setHardness(0.6F).setStepSound(soundGrassFootstep).setUnlocalizedName("mycel");
	public static final Block waterlily = (new BlockLilyPad(111)).setHardness(0.0F).setStepSound(soundGrassFootstep).setUnlocalizedName("waterlily");
	public static final Block netherBrick = (new Block(112, Material.rock)).setHardness(2.0F).setResistance(10.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("netherBrick").setCreativeTab(CreativeTabs.tabBlock);
	public static final Block netherFence = (new BlockFence(113, "netherBrick", Material.rock)).setHardness(2.0F).setResistance(10.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("netherFence");
	public static final Block stairsNetherBrick = (new BlockStairs(114, netherBrick, 0)).setUnlocalizedName("stairsNetherBrick");
	public static final Block netherStalk = (new BlockNetherStalk(115)).setUnlocalizedName("netherStalk");
	public static final Block enchantmentTable = (new BlockEnchantmentTable(116)).setHardness(5.0F).setResistance(2000.0F).setUnlocalizedName("enchantmentTable");
	public static final Block brewingStand = (new BlockBrewingStand(117)).setHardness(0.5F).setLightValue(2.0F / 16.0F).setUnlocalizedName("brewingStand");
	public static final BlockCauldron cauldron = (BlockCauldron)(new BlockCauldron(118)).setHardness(2.0F).setUnlocalizedName("cauldron");
	public static final Block endPortal = (new BlockEndPortal(119, Material.portal)).setHardness(-1.0F).setResistance(6000000.0F);
	public static final Block endPortalFrame = (new BlockEndPortalFrame(120)).setStepSound(soundGlassFootstep).setLightValue(2.0F / 16.0F).setHardness(-1.0F).setUnlocalizedName("endPortalFrame").setResistance(6000000.0F).setCreativeTab(CreativeTabs.tabDecorations);
	public static final Block whiteStone = (new Block(121, Material.rock)).setHardness(3.0F).setResistance(15.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("whiteStone").setCreativeTab(CreativeTabs.tabBlock);
	public static final Block dragonEgg = (new BlockDragonEgg(122)).setHardness(3.0F).setResistance(15.0F).setStepSound(soundStoneFootstep).setLightValue(2.0F / 16.0F).setUnlocalizedName("dragonEgg");
	public static final Block redstoneLampIdle = (new BlockRedstoneLight(123, false)).setHardness(0.3F).setStepSound(soundGlassFootstep).setUnlocalizedName("redstoneLight").setCreativeTab(CreativeTabs.tabRedstone);
	public static final Block redstoneLampActive = (new BlockRedstoneLight(124, true)).setHardness(0.3F).setStepSound(soundGlassFootstep).setUnlocalizedName("redstoneLight");
	public static final BlockHalfSlab woodDoubleSlab = (BlockHalfSlab)(new BlockWoodSlab(125, true)).setHardness(2.0F).setResistance(5.0F).setStepSound(soundWoodFootstep).setUnlocalizedName("woodSlab");
	public static final BlockHalfSlab woodSingleSlab = (BlockHalfSlab)(new BlockWoodSlab(126, false)).setHardness(2.0F).setResistance(5.0F).setStepSound(soundWoodFootstep).setUnlocalizedName("woodSlab");
	public static final Block cocoaPlant = (new BlockCocoa(127)).setHardness(0.2F).setResistance(5.0F).setStepSound(soundWoodFootstep).setUnlocalizedName("cocoa");
	public static final Block stairsSandStone = (new BlockStairs(128, sandStone, 0)).setUnlocalizedName("stairsSandStone");
	public static final Block oreEmerald = (new BlockOre(129)).setHardness(3.0F).setResistance(5.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("oreEmerald");
	public static final Block enderChest = (new BlockEnderChest(130)).setHardness(22.5F).setResistance(1000.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("enderChest").setLightValue(0.5F);
	public static final BlockTripWireSource tripWireSource = (BlockTripWireSource)(new BlockTripWireSource(131)).setUnlocalizedName("tripWireSource");
	public static final Block tripWire = (new BlockTripWire(132)).setUnlocalizedName("tripWire");
	public static final Block blockEmerald = (new BlockOreStorage(133)).setHardness(5.0F).setResistance(10.0F).setStepSound(soundMetalFootstep).setUnlocalizedName("blockEmerald");
	public static final Block stairsWoodSpruce = (new BlockStairs(134, planks, 1)).setUnlocalizedName("stairsWoodSpruce");
	public static final Block stairsWoodBirch = (new BlockStairs(135, planks, 2)).setUnlocalizedName("stairsWoodBirch");
	public static final Block stairsWoodJungle = (new BlockStairs(136, planks, 3)).setUnlocalizedName("stairsWoodJungle");
	public static final Block commandBlock = (new BlockCommandBlock(137)).setUnlocalizedName("commandBlock");
	public static final BlockBeacon beacon = (BlockBeacon)(new BlockBeacon(138)).setUnlocalizedName("beacon").setLightValue(1.0F);
	public static final Block cobblestoneWall = (new BlockWall(139, cobblestone)).setUnlocalizedName("cobbleWall");
	public static final Block flowerPot = (new BlockFlowerPot(140)).setHardness(0.0F).setStepSound(soundPowderFootstep).setUnlocalizedName("flowerPot");
	public static final Block carrot = (new BlockCarrot(141)).setUnlocalizedName("carrots");
	public static final Block potato = (new BlockPotato(142)).setUnlocalizedName("potatoes");
	public static final Block woodenButton = (new BlockButtonWood(143)).setHardness(0.5F).setStepSound(soundWoodFootstep).setUnlocalizedName("button");
	public static final Block skull = (new BlockSkull(144)).setHardness(1.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("skull");
	public static final Block anvil = (new BlockAnvil(145)).setHardness(5.0F).setStepSound(soundAnvilFootstep).setResistance(2000.0F).setUnlocalizedName("anvil");
	public static final Block chestTrapped = (new BlockChest(146, 1)).setHardness(2.5F).setStepSound(soundWoodFootstep).setUnlocalizedName("chestTrap");
	public static final Block pressurePlateGold = (new BlockPressurePlateWeighted(147, "blockGold", Material.iron, 64)).setHardness(0.5F).setStepSound(soundWoodFootstep).setUnlocalizedName("weightedPlate_light");
	public static final Block pressurePlateIron = (new BlockPressurePlateWeighted(148, "blockIron", Material.iron, 640)).setHardness(0.5F).setStepSound(soundWoodFootstep).setUnlocalizedName("weightedPlate_heavy");
	public static final BlockComparator redstoneComparatorIdle = (BlockComparator)(new BlockComparator(149, false)).setHardness(0.0F).setStepSound(soundWoodFootstep).setUnlocalizedName("comparator").disableStats();
	public static final BlockComparator redstoneComparatorActive = (BlockComparator)(new BlockComparator(150, true)).setHardness(0.0F).setLightValue(10.0F / 16.0F).setStepSound(soundWoodFootstep).setUnlocalizedName("comparator").disableStats();
	public static final BlockDaylightDetector daylightSensor = (BlockDaylightDetector)(new BlockDaylightDetector(151)).setHardness(0.2F).setStepSound(soundWoodFootstep).setUnlocalizedName("daylightDetector");
	public static final Block blockRedstone = (new BlockPoweredOre(152)).setHardness(5.0F).setResistance(10.0F).setStepSound(soundMetalFootstep).setUnlocalizedName("blockRedstone");
	public static final Block oreNetherQuartz = (new BlockOre(153)).setHardness(3.0F).setResistance(5.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("netherquartz");
	public static final BlockHopper hopperBlock = (BlockHopper)(new BlockHopper(154)).setHardness(3.0F).setResistance(8.0F).setStepSound(soundWoodFootstep).setUnlocalizedName("hopper");
	public static final Block blockNetherQuartz = (new BlockQuartz(155)).setStepSound(soundStoneFootstep).setHardness(0.8F).setUnlocalizedName("quartzBlock");
	public static final Block stairsNetherQuartz = (new BlockStairs(156, blockNetherQuartz, 0)).setUnlocalizedName("stairsQuartz");
	public static final Block railActivator = (new BlockRailPowered(157)).setHardness(0.7F).setStepSound(soundMetalFootstep).setUnlocalizedName("activatorRail");
	public static final Block dropper = (new BlockDropper(158)).setHardness(3.5F).setStepSound(soundStoneFootstep).setUnlocalizedName("dropper");
	public final int blockID;
	protected float blockHardness;
	protected float blockResistance;
	protected boolean blockConstructorCalled = true;
	protected boolean enableStats = true;
	protected boolean needsRandomTick;
	protected boolean isBlockContainer;
	protected double minX;
	protected double minY;
	protected double minZ;
	protected double maxX;
	protected double maxY;
	protected double maxZ;
	public StepSound stepSound = soundPowderFootstep;
	public float blockParticleGravity = 1.0F;
	public final Material blockMaterial;
	public float slipperiness = 0.6F;
	private String unlocalizedName;
	protected Icon blockIcon;

	protected Block(int var1, Material var2) {
		if(blocksList[var1] != null) {
			throw new IllegalArgumentException("Slot " + var1 + " is already occupied by " + blocksList[var1] + " when adding " + this);
		} else {
			this.blockMaterial = var2;
			blocksList[var1] = this;
			this.blockID = var1;
			this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
			opaqueCubeLookup[var1] = this.isOpaqueCube();
			lightOpacity[var1] = this.isOpaqueCube() ? 255 : 0;
			canBlockGrass[var1] = !var2.getCanBlockGrass();
		}
	}

	protected void initializeBlock() {
	}

	protected Block setStepSound(StepSound var1) {
		this.stepSound = var1;
		return this;
	}

	protected Block setLightOpacity(int var1) {
		lightOpacity[this.blockID] = var1;
		return this;
	}

	protected Block setLightValue(float var1) {
		lightValue[this.blockID] = (int)(15.0F * var1);
		return this;
	}

	protected Block setResistance(float var1) {
		this.blockResistance = var1 * 3.0F;
		return this;
	}

	public static boolean isNormalCube(int var0) {
		Block var1 = blocksList[var0];
		return var1 == null ? false : var1.blockMaterial.isOpaque() && var1.renderAsNormalBlock() && !var1.canProvidePower();
	}

	public boolean renderAsNormalBlock() {
		return true;
	}

	public boolean getBlocksMovement(IBlockAccess var1, int var2, int var3, int var4) {
		return !this.blockMaterial.blocksMovement();
	}

	public int getRenderType() {
		return 0;
	}

	protected Block setHardness(float var1) {
		this.blockHardness = var1;
		if(this.blockResistance < var1 * 5.0F) {
			this.blockResistance = var1 * 5.0F;
		}

		return this;
	}

	protected Block setBlockUnbreakable() {
		this.setHardness(-1.0F);
		return this;
	}

	public float getBlockHardness(World var1, int var2, int var3, int var4) {
		return this.blockHardness;
	}

	protected Block setTickRandomly(boolean var1) {
		this.needsRandomTick = var1;
		return this;
	}

	public boolean getTickRandomly() {
		return this.needsRandomTick;
	}

	public boolean hasTileEntity() {
		return this.isBlockContainer;
	}

	protected final void setBlockBounds(float var1, float var2, float var3, float var4, float var5, float var6) {
		this.minX = (double)var1;
		this.minY = (double)var2;
		this.minZ = (double)var3;
		this.maxX = (double)var4;
		this.maxY = (double)var5;
		this.maxZ = (double)var6;
	}

	public float getBlockBrightness(IBlockAccess var1, int var2, int var3, int var4) {
		return var1.getBrightness(var2, var3, var4, lightValue[var1.getBlockId(var2, var3, var4)]);
	}

	public int getMixedBrightnessForBlock(IBlockAccess var1, int var2, int var3, int var4) {
		return var1.getLightBrightnessForSkyBlocks(var2, var3, var4, lightValue[var1.getBlockId(var2, var3, var4)]);
	}

	public boolean shouldSideBeRendered(IBlockAccess var1, int var2, int var3, int var4, int var5) {
		return var5 == 0 && this.minY > 0.0D ? true : (var5 == 1 && this.maxY < 1.0D ? true : (var5 == 2 && this.minZ > 0.0D ? true : (var5 == 3 && this.maxZ < 1.0D ? true : (var5 == 4 && this.minX > 0.0D ? true : (var5 == 5 && this.maxX < 1.0D ? true : !var1.isBlockOpaqueCube(var2, var3, var4))))));
	}

	public boolean isBlockSolid(IBlockAccess var1, int var2, int var3, int var4, int var5) {
		return var1.getBlockMaterial(var2, var3, var4).isSolid();
	}

	public Icon getBlockTexture(IBlockAccess var1, int var2, int var3, int var4, int var5) {
		return this.getIcon(var5, var1.getBlockMetadata(var2, var3, var4));
	}

	public Icon getIcon(int var1, int var2) {
		return this.blockIcon;
	}

	public final Icon getBlockTextureFromSide(int var1) {
		return this.getIcon(var1, 0);
	}

	public AxisAlignedBB getSelectedBoundingBoxFromPool(World var1, int var2, int var3, int var4) {
		return AxisAlignedBB.getAABBPool().getAABB((double)var2 + this.minX, (double)var3 + this.minY, (double)var4 + this.minZ, (double)var2 + this.maxX, (double)var3 + this.maxY, (double)var4 + this.maxZ);
	}

	public void addCollisionBoxesToList(World var1, int var2, int var3, int var4, AxisAlignedBB var5, List var6, Entity var7) {
		AxisAlignedBB var8 = this.getCollisionBoundingBoxFromPool(var1, var2, var3, var4);
		if(var8 != null && var5.intersectsWith(var8)) {
			var6.add(var8);
		}

	}

	public AxisAlignedBB getCollisionBoundingBoxFromPool(World var1, int var2, int var3, int var4) {
		return AxisAlignedBB.getAABBPool().getAABB((double)var2 + this.minX, (double)var3 + this.minY, (double)var4 + this.minZ, (double)var2 + this.maxX, (double)var3 + this.maxY, (double)var4 + this.maxZ);
	}

	public boolean isOpaqueCube() {
		return true;
	}

	public boolean canCollideCheck(int var1, boolean var2) {
		return this.isCollidable();
	}

	public boolean isCollidable() {
		return true;
	}

	public void updateTick(World var1, int var2, int var3, int var4, Random var5) {
	}

	public void randomDisplayTick(World var1, int var2, int var3, int var4, Random var5) {
	}

	public void onBlockDestroyedByPlayer(World var1, int var2, int var3, int var4, int var5) {
	}

	public void onNeighborBlockChange(World var1, int var2, int var3, int var4, int var5) {
	}

	public int tickRate(World var1) {
		return 10;
	}

	public void onBlockAdded(World var1, int var2, int var3, int var4) {
	}

	public void breakBlock(World var1, int var2, int var3, int var4, int var5, int var6) {
	}

	public int quantityDropped(Random var1) {
		return 1;
	}

	public int idDropped(int var1, Random var2, int var3) {
		return this.blockID;
	}

	public float getPlayerRelativeBlockHardness(EntityPlayer var1, World var2, int var3, int var4, int var5) {
		float var6 = this.getBlockHardness(var2, var3, var4, var5);
		return var6 < 0.0F ? 0.0F : (!var1.canHarvestBlock(this) ? var1.getCurrentPlayerStrVsBlock(this, false) / var6 / 100.0F : var1.getCurrentPlayerStrVsBlock(this, true) / var6 / 30.0F);
	}

	public final void dropBlockAsItem(World var1, int var2, int var3, int var4, int var5, int var6) {
		this.dropBlockAsItemWithChance(var1, var2, var3, var4, var5, 1.0F, var6);
	}

	public void dropBlockAsItemWithChance(World var1, int var2, int var3, int var4, int var5, float var6, int var7) {
		if(!var1.isRemote) {
			int var8 = this.quantityDroppedWithBonus(var7, var1.rand);

			for(int var9 = 0; var9 < var8; ++var9) {
				if(var1.rand.nextFloat() <= var6) {
					int var10 = this.idDropped(var5, var1.rand, var7);
					if(var10 > 0) {
						this.dropBlockAsItem_do(var1, var2, var3, var4, new ItemStack(var10, 1, this.damageDropped(var5)));
					}
				}
			}

		}
	}

	protected void dropBlockAsItem_do(World var1, int var2, int var3, int var4, ItemStack var5) {
		if(!var1.isRemote && var1.getGameRules().getGameRuleBooleanValue("doTileDrops")) {
			float var6 = 0.7F;
			double var7 = (double)(var1.rand.nextFloat() * var6) + (double)(1.0F - var6) * 0.5D;
			double var9 = (double)(var1.rand.nextFloat() * var6) + (double)(1.0F - var6) * 0.5D;
			double var11 = (double)(var1.rand.nextFloat() * var6) + (double)(1.0F - var6) * 0.5D;
			EntityItem var13 = new EntityItem(var1, (double)var2 + var7, (double)var3 + var9, (double)var4 + var11, var5);
			var13.delayBeforeCanPickup = 10;
			var1.spawnEntityInWorld(var13);
		}
	}

	protected void dropXpOnBlockBreak(World var1, int var2, int var3, int var4, int var5) {
		if(!var1.isRemote) {
			while(var5 > 0) {
				int var6 = EntityXPOrb.getXPSplit(var5);
				var5 -= var6;
				var1.spawnEntityInWorld(new EntityXPOrb(var1, (double)var2 + 0.5D, (double)var3 + 0.5D, (double)var4 + 0.5D, var6));
			}
		}

	}

	public int damageDropped(int var1) {
		return 0;
	}

	public float getExplosionResistance(Entity var1) {
		return this.blockResistance / 5.0F;
	}

	public MovingObjectPosition collisionRayTrace(World var1, int var2, int var3, int var4, Vec3 var5, Vec3 var6) {
		this.setBlockBoundsBasedOnState(var1, var2, var3, var4);
		var5 = var5.addVector((double)(-var2), (double)(-var3), (double)(-var4));
		var6 = var6.addVector((double)(-var2), (double)(-var3), (double)(-var4));
		Vec3 var7 = var5.getIntermediateWithXValue(var6, this.minX);
		Vec3 var8 = var5.getIntermediateWithXValue(var6, this.maxX);
		Vec3 var9 = var5.getIntermediateWithYValue(var6, this.minY);
		Vec3 var10 = var5.getIntermediateWithYValue(var6, this.maxY);
		Vec3 var11 = var5.getIntermediateWithZValue(var6, this.minZ);
		Vec3 var12 = var5.getIntermediateWithZValue(var6, this.maxZ);
		if(!this.isVecInsideYZBounds(var7)) {
			var7 = null;
		}

		if(!this.isVecInsideYZBounds(var8)) {
			var8 = null;
		}

		if(!this.isVecInsideXZBounds(var9)) {
			var9 = null;
		}

		if(!this.isVecInsideXZBounds(var10)) {
			var10 = null;
		}

		if(!this.isVecInsideXYBounds(var11)) {
			var11 = null;
		}

		if(!this.isVecInsideXYBounds(var12)) {
			var12 = null;
		}

		Vec3 var13 = null;
		if(var7 != null && (var13 == null || var5.squareDistanceTo(var7) < var5.squareDistanceTo(var13))) {
			var13 = var7;
		}

		if(var8 != null && (var13 == null || var5.squareDistanceTo(var8) < var5.squareDistanceTo(var13))) {
			var13 = var8;
		}

		if(var9 != null && (var13 == null || var5.squareDistanceTo(var9) < var5.squareDistanceTo(var13))) {
			var13 = var9;
		}

		if(var10 != null && (var13 == null || var5.squareDistanceTo(var10) < var5.squareDistanceTo(var13))) {
			var13 = var10;
		}

		if(var11 != null && (var13 == null || var5.squareDistanceTo(var11) < var5.squareDistanceTo(var13))) {
			var13 = var11;
		}

		if(var12 != null && (var13 == null || var5.squareDistanceTo(var12) < var5.squareDistanceTo(var13))) {
			var13 = var12;
		}

		if(var13 == null) {
			return null;
		} else {
			byte var14 = -1;
			if(var13 == var7) {
				var14 = 4;
			}

			if(var13 == var8) {
				var14 = 5;
			}

			if(var13 == var9) {
				var14 = 0;
			}

			if(var13 == var10) {
				var14 = 1;
			}

			if(var13 == var11) {
				var14 = 2;
			}

			if(var13 == var12) {
				var14 = 3;
			}

			return new MovingObjectPosition(var2, var3, var4, var14, var13.addVector((double)var2, (double)var3, (double)var4));
		}
	}

	private boolean isVecInsideYZBounds(Vec3 var1) {
		return var1 == null ? false : var1.yCoord >= this.minY && var1.yCoord <= this.maxY && var1.zCoord >= this.minZ && var1.zCoord <= this.maxZ;
	}

	private boolean isVecInsideXZBounds(Vec3 var1) {
		return var1 == null ? false : var1.xCoord >= this.minX && var1.xCoord <= this.maxX && var1.zCoord >= this.minZ && var1.zCoord <= this.maxZ;
	}

	private boolean isVecInsideXYBounds(Vec3 var1) {
		return var1 == null ? false : var1.xCoord >= this.minX && var1.xCoord <= this.maxX && var1.yCoord >= this.minY && var1.yCoord <= this.maxY;
	}

	public void onBlockDestroyedByExplosion(World var1, int var2, int var3, int var4, Explosion var5) {
	}

	public int getRenderBlockPass() {
		return 0;
	}

	public boolean canPlaceBlockOnSide(World var1, int var2, int var3, int var4, int var5, ItemStack var6) {
		return this.canPlaceBlockOnSide(var1, var2, var3, var4, var5);
	}

	public boolean canPlaceBlockOnSide(World var1, int var2, int var3, int var4, int var5) {
		return this.canPlaceBlockAt(var1, var2, var3, var4);
	}

	public boolean canPlaceBlockAt(World var1, int var2, int var3, int var4) {
		int var5 = var1.getBlockId(var2, var3, var4);
		return var5 == 0 || blocksList[var5].blockMaterial.isReplaceable();
	}

	public boolean onBlockActivated(World var1, int var2, int var3, int var4, EntityPlayer var5, int var6, float var7, float var8, float var9) {
		return false;
	}

	public void onEntityWalking(World var1, int var2, int var3, int var4, Entity var5) {
	}

	public int onBlockPlaced(World var1, int var2, int var3, int var4, int var5, float var6, float var7, float var8, int var9) {
		return var9;
	}

	public void onBlockClicked(World var1, int var2, int var3, int var4, EntityPlayer var5) {
	}

	public void velocityToAddToEntity(World var1, int var2, int var3, int var4, Entity var5, Vec3 var6) {
	}

	public void setBlockBoundsBasedOnState(IBlockAccess var1, int var2, int var3, int var4) {
	}

	public final double getBlockBoundsMinX() {
		return this.minX;
	}

	public final double getBlockBoundsMaxX() {
		return this.maxX;
	}

	public final double getBlockBoundsMinY() {
		return this.minY;
	}

	public final double getBlockBoundsMaxY() {
		return this.maxY;
	}

	public final double getBlockBoundsMinZ() {
		return this.minZ;
	}

	public final double getBlockBoundsMaxZ() {
		return this.maxZ;
	}

	public int getBlockColor() {
		return 16777215;
	}

	public int getRenderColor(int var1) {
		return 16777215;
	}

	public int colorMultiplier(IBlockAccess var1, int var2, int var3, int var4) {
		return 16777215;
	}

	public int isProvidingWeakPower(IBlockAccess var1, int var2, int var3, int var4, int var5) {
		return 0;
	}

	public boolean canProvidePower() {
		return false;
	}

	public void onEntityCollidedWithBlock(World var1, int var2, int var3, int var4, Entity var5) {
	}

	public int isProvidingStrongPower(IBlockAccess var1, int var2, int var3, int var4, int var5) {
		return 0;
	}

	public void setBlockBoundsForItemRender() {
	}

	public void harvestBlock(World var1, EntityPlayer var2, int var3, int var4, int var5, int var6) {
		var2.addStat(StatList.mineBlockStatArray[this.blockID], 1);
		var2.addExhaustion(0.025F);
		if(this.canSilkHarvest() && EnchantmentHelper.getSilkTouchModifier(var2)) {
			ItemStack var8 = this.createStackedBlock(var6);
			if(var8 != null) {
				this.dropBlockAsItem_do(var1, var3, var4, var5, var8);
			}
		} else {
			int var7 = EnchantmentHelper.getFortuneModifier(var2);
			this.dropBlockAsItem(var1, var3, var4, var5, var6, var7);
		}

	}

	protected boolean canSilkHarvest() {
		return this.renderAsNormalBlock() && !this.isBlockContainer;
	}

	protected ItemStack createStackedBlock(int var1) {
		int var2 = 0;
		if(this.blockID >= 0 && this.blockID < Item.itemsList.length && Item.itemsList[this.blockID].getHasSubtypes()) {
			var2 = var1;
		}

		return new ItemStack(this.blockID, 1, var2);
	}

	public int quantityDroppedWithBonus(int var1, Random var2) {
		return this.quantityDropped(var2);
	}

	public boolean canBlockStay(World var1, int var2, int var3, int var4) {
		return true;
	}

	public void onBlockPlacedBy(World var1, int var2, int var3, int var4, EntityLiving var5, ItemStack var6) {
	}

	public void onPostBlockPlaced(World var1, int var2, int var3, int var4, int var5) {
	}

	public Block setUnlocalizedName(String var1) {
		this.unlocalizedName = var1;
		return this;
	}

	public String getLocalizedName() {
		return StatCollector.translateToLocal(this.getUnlocalizedName() + ".name");
	}

	public String getUnlocalizedName() {
		return "tile." + this.unlocalizedName;
	}

	public String getUnlocalizedName2() {
		return this.unlocalizedName;
	}

	public boolean onBlockEventReceived(World var1, int var2, int var3, int var4, int var5, int var6) {
		return false;
	}

	public boolean getEnableStats() {
		return this.enableStats;
	}

	protected Block disableStats() {
		this.enableStats = false;
		return this;
	}

	public int getMobilityFlag() {
		return this.blockMaterial.getMaterialMobility();
	}

	public float getAmbientOcclusionLightValue(IBlockAccess var1, int var2, int var3, int var4) {
		return var1.isBlockNormalCube(var2, var3, var4) ? 0.2F : 1.0F;
	}

	public void onFallenUpon(World var1, int var2, int var3, int var4, Entity var5, float var6) {
	}

	public int idPicked(World var1, int var2, int var3, int var4) {
		return this.blockID;
	}

	public int getDamageValue(World var1, int var2, int var3, int var4) {
		return this.damageDropped(var1.getBlockMetadata(var2, var3, var4));
	}

	public void getSubBlocks(int var1, CreativeTabs var2, List var3) {
		var3.add(new ItemStack(var1, 1, 0));
	}

	public CreativeTabs getCreativeTabToDisplayOn() {
		return this.displayOnCreativeTab;
	}

	public Block setCreativeTab(CreativeTabs var1) {
		this.displayOnCreativeTab = var1;
		return this;
	}

	public void onBlockHarvested(World var1, int var2, int var3, int var4, int var5, EntityPlayer var6) {
	}

	public void onSetBlockIDWithMetaData(World var1, int var2, int var3, int var4, int var5) {
	}

	public void fillWithRain(World var1, int var2, int var3, int var4) {
	}

	public boolean isFlowerPot() {
		return false;
	}

	public boolean func_82506_l() {
		return true;
	}

	public boolean canDropFromExplosion(Explosion var1) {
		return true;
	}

	public boolean isAssociatedBlockID(int var1) {
		return this.blockID == var1;
	}

	public static boolean isAssociatedBlockID(int var0, int var1) {
		return var0 == var1 ? true : (var0 != 0 && var1 != 0 && blocksList[var0] != null && blocksList[var1] != null ? blocksList[var0].isAssociatedBlockID(var1) : false);
	}

	public boolean hasComparatorInputOverride() {
		return false;
	}

	public int getComparatorInputOverride(World var1, int var2, int var3, int var4, int var5) {
		return 0;
	}

	public void registerIcons(IconRegister var1) {
		this.blockIcon = var1.registerIcon(this.unlocalizedName);
	}

	public String getItemIconName() {
		return null;
	}

	static {
		Item.itemsList[cloth.blockID] = (new ItemCloth(cloth.blockID - 256)).setUnlocalizedName("cloth");
		Item.itemsList[wood.blockID] = (new ItemMultiTextureTile(wood.blockID - 256, wood, BlockLog.woodType)).setUnlocalizedName("log");
		Item.itemsList[planks.blockID] = (new ItemMultiTextureTile(planks.blockID - 256, planks, BlockWood.woodType)).setUnlocalizedName("wood");
		Item.itemsList[silverfish.blockID] = (new ItemMultiTextureTile(silverfish.blockID - 256, silverfish, BlockSilverfish.silverfishStoneTypes)).setUnlocalizedName("monsterStoneEgg");
		Item.itemsList[stoneBrick.blockID] = (new ItemMultiTextureTile(stoneBrick.blockID - 256, stoneBrick, BlockStoneBrick.STONE_BRICK_TYPES)).setUnlocalizedName("stonebricksmooth");
		Item.itemsList[sandStone.blockID] = (new ItemMultiTextureTile(sandStone.blockID - 256, sandStone, BlockSandStone.SAND_STONE_TYPES)).setUnlocalizedName("sandStone");
		Item.itemsList[blockNetherQuartz.blockID] = (new ItemMultiTextureTile(blockNetherQuartz.blockID - 256, blockNetherQuartz, BlockQuartz.quartzBlockTypes)).setUnlocalizedName("quartzBlock");
		Item.itemsList[stoneSingleSlab.blockID] = (new ItemSlab(stoneSingleSlab.blockID - 256, stoneSingleSlab, stoneDoubleSlab, false)).setUnlocalizedName("stoneSlab");
		Item.itemsList[stoneDoubleSlab.blockID] = (new ItemSlab(stoneDoubleSlab.blockID - 256, stoneSingleSlab, stoneDoubleSlab, true)).setUnlocalizedName("stoneSlab");
		Item.itemsList[woodSingleSlab.blockID] = (new ItemSlab(woodSingleSlab.blockID - 256, woodSingleSlab, woodDoubleSlab, false)).setUnlocalizedName("woodSlab");
		Item.itemsList[woodDoubleSlab.blockID] = (new ItemSlab(woodDoubleSlab.blockID - 256, woodSingleSlab, woodDoubleSlab, true)).setUnlocalizedName("woodSlab");
		Item.itemsList[sapling.blockID] = (new ItemMultiTextureTile(sapling.blockID - 256, sapling, BlockSapling.WOOD_TYPES)).setUnlocalizedName("sapling");
		Item.itemsList[leaves.blockID] = (new ItemLeaves(leaves.blockID - 256)).setUnlocalizedName("leaves");
		Item.itemsList[vine.blockID] = new ItemColored(vine.blockID - 256, false);
		Item.itemsList[tallGrass.blockID] = (new ItemColored(tallGrass.blockID - 256, true)).setBlockNames(new String[]{"shrub", "grass", "fern"});
		Item.itemsList[snow.blockID] = new ItemSnow(snow.blockID - 256, snow);
		Item.itemsList[waterlily.blockID] = new ItemLilyPad(waterlily.blockID - 256);
		Item.itemsList[pistonBase.blockID] = new ItemPiston(pistonBase.blockID - 256);
		Item.itemsList[pistonStickyBase.blockID] = new ItemPiston(pistonStickyBase.blockID - 256);
		Item.itemsList[cobblestoneWall.blockID] = (new ItemMultiTextureTile(cobblestoneWall.blockID - 256, cobblestoneWall, BlockWall.types)).setUnlocalizedName("cobbleWall");
		Item.itemsList[anvil.blockID] = (new ItemAnvilBlock(anvil)).setUnlocalizedName("anvil");

		for(int var0 = 0; var0 < 256; ++var0) {
			if(blocksList[var0] != null) {
				if(Item.itemsList[var0] == null) {
					Item.itemsList[var0] = new ItemBlock(var0 - 256);
					blocksList[var0].initializeBlock();
				}

				boolean var1 = false;
				if(var0 > 0 && blocksList[var0].getRenderType() == 10) {
					var1 = true;
				}

				if(var0 > 0 && blocksList[var0] instanceof BlockHalfSlab) {
					var1 = true;
				}

				if(var0 == tilledField.blockID) {
					var1 = true;
				}

				if(canBlockGrass[var0]) {
					var1 = true;
				}

				if(lightOpacity[var0] == 0) {
					var1 = true;
				}

				useNeighborBrightness[var0] = var1;
			}
		}

		canBlockGrass[0] = true;
		StatList.initBreakableStats();
	}
}
