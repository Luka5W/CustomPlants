package com.luka5w.customplants.common.data.plantspacks.plants;

import com.luka5w.customplants.common.blocks.BlockCustomBush;
import com.luka5w.customplants.common.blocks.BlockCustomCrops;
import com.luka5w.customplants.common.items.ItemCustomSeeds;
import com.luka5w.customplants.common.plantsbehavior.PlantBehavior;
import com.luka5w.customplants.common.tileentities.TileEntityCustomPlant;
import com.luka5w.customplants.common.util.Registry;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * A holder for the parsed #plant section of plant config files.
 * @see com.luka5w.customplants.common.data.plantspacks.plants.serialization.Serializer
 */
public class Config {
    private final PlantsFileMeta file;
    private final PlantBehavior behavior;
    private final Plant plant;
    private final Type type;
    private String name;
    
    public Config(PlantsFileMeta file, PlantBehavior behavior, Plant plant, Type type) {
        this.file = file;
        this.behavior = behavior;
        this.plant = plant;
        this.type = type;
        this.name = null;
    }
    
    public Config finalize(String name) {
        this.name = name;
        return this;
    }
    
    public void addRegistryEntries(Registry registry) {
        if (this.name == null) throw new IllegalStateException("Not finalized");
        Block block;
        ItemCustomSeeds seeds = null;
        switch (this.type.getType()) {
            case Bush:
                block = new BlockCustomBush(
                        this.plant.getAABBs(),
                        this.behavior,
                        this.plant.getMaterial(),
                        BlockFaceShape.CENTER,
                        this.plant.getDrops(),
                        this.plant.getFacings(),
                        this.plant.getOreDict(),
                        this.plant.getType(),
                        this.plant.getSoils(),
                        this.plant.isSoilsAllowed());
                break;
            case Crops:
                Type.Crops type = (Type.Crops) this.type;
                //noinspection ConstantConditions
                block = new BlockCustomCrops(
                        this.plant.getAABBs(),
                        this.behavior,
                        this.plant.getMaterial(),
                        BlockFaceShape.CENTER,
                        this.plant.getDrops(),
                        this.plant.getFacings().get(0),
                        this.plant.getOreDict(),
                        this.plant.getType(),
                        type.isCanUseBonemeal(),
                        seeds,
                        this.plant.getSoils(),
                        this.plant.isSoilsAllowed());
                seeds = new ItemCustomSeeds(this.plant.getType(), (BlockCustomCrops) block, this.plant.getFacings());
                break;
            case Extendable:
                //block = new BlockCustomBush(this.behavior, this.block, this.type);
                //break;
                return;
            case Overlay:
                //block = new BlockCustomBush(this.behavior, this.block, this.type);
                //break;
                return;
            case Sapling:
                //block = new BlockCustomSapling(this.behavior, this.block, this.type);
                //break;
                return;
            default:
                throw new IllegalStateException("Unknown type");
        }
        registry.addBlock(block, seeds == null ? this.name : this.name + ".crops", false, registry.getDefaultTab());
        registry.addItemBlock(new ItemBlock(block) {
            @Override
            public int getItemBurnTime(ItemStack itemStack) {
                return plant.getBurnTime();
            }
        }, this.name);
        if (seeds != null) registry.addItem(seeds, this.name + ".seeds", registry.getDefaultTab());
        if (registry.getTileEntities().stream().noneMatch(t -> t.getFirst() == TileEntityCustomPlant.class)) registry.addTileEntity(TileEntityCustomPlant.class, "customplant");
    }
}
