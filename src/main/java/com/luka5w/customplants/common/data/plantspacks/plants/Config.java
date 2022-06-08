package com.luka5w.customplants.common.data.plantspacks.plants;

import com.luka5w.customplants.common.blocks.BlockCustomBush;
import com.luka5w.customplants.common.blocks.BlockCustomCrops;
import com.luka5w.customplants.common.blocks.BlockCustomExtendable;
import com.luka5w.customplants.common.blocks.BlockCustomSapling;
import com.luka5w.customplants.common.data.plantspacks.trees.serialization.v0.WorldGenCustomTreeSerializer;
import com.luka5w.customplants.common.items.ItemCustomSeeds;
import com.luka5w.customplants.common.plantsbehavior.PlantBehavior;
import com.luka5w.customplants.common.tileentities.TileEntityCustomPlant;
import com.luka5w.customplants.common.util.Registry;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.gen.feature.WorldGenerator;
import org.apache.logging.log4j.Logger;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A holder for the parsed #plant section of plant config files.
 * @see com.luka5w.customplants.common.data.plantspacks.plants.serialization.Serializer
 */
public class Config {
    
    private final Logger logger;
    private final PlantsFileMeta file;
    private final PlantBehavior behavior;
    private final Plant plant;
    private final Type type;
    private String name;
    
    public Config(Logger logger, PlantsFileMeta file, PlantBehavior behavior, Plant plant, Type type) {
        this.logger = logger;
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
    
    public void addRegistryEntries(Registry registry, ArrayList<HashMap<String, String>> treeConfigs) {
        if (this.name == null) throw new IllegalStateException("Not finalized");
        Block block;
        ItemCustomSeeds seeds = null;
        switch (this.type.getType()) {
            case Bush:
                block = new BlockCustomBush(this.plant.getAABBs(),
                                            this.behavior,
                                            this.plant.getMaterial(),
                                            BlockFaceShape.CENTER,
                                            this.plant.getDrops(),
                                            this.plant.getFacings(),
                                            this.plant.getType(),
                                            this.plant.getSoils(),
                                            this.plant.isSoilsAllowed());
                break;
            case Crops:
                Type.Crops crops = (Type.Crops) this.type;
                //noinspection ConstantConditions
                block = new BlockCustomCrops(this.plant.getAABBs(),
                                             this.behavior,
                                             this.plant.getMaterial(),
                                             BlockFaceShape.CENTER,
                                             this.plant.getDrops(),
                                             this.plant.getFacings().get(0),
                                             this.plant.getType(),
                                             crops.isCanUseBonemeal(),
                                             seeds,
                                             crops.getReqLightvl(),
                                             this.plant.getSoils(),
                                             this.plant.isSoilsAllowed());
                seeds = new ItemCustomSeeds(this.plant.getType(), (BlockCustomCrops) block, this.plant.getFacings());
                break;
            case Extendable:
                block = new BlockCustomExtendable(this.plant.getAABBs(),
                                                  this.behavior,
                                                  this.plant.getMaterial(),
                                                  BlockFaceShape.CENTER,
                                                  this.plant.getDrops(),
                                                  this.plant.getFacings().get(0),
                                                  this.plant.getType(),
                                                  this.plant.getSoils(),
                                                  this.plant.isSoilsAllowed());
                break;
            case Sapling:
                Type.Sapling sapling = (Type.Sapling) this.type;
                List<WorldGenerator[]> trees = new ArrayList<>();
                List<String> keys = treeConfigs.get(0)
                                               .keySet()
                                               .stream()
                                               .filter(k -> this.name.startsWith(k))
                                               .collect(Collectors.toList());
                for (int i = 0; i < keys.size(); i++) {
                    try {
                        trees.add(WorldGenCustomTreeSerializer.deserialize(this.logger, true,
                                                                           sapling.getLogBlock(),
                                                                           sapling.getLeafBlock(),
                                                                           treeConfigs.get(0).get(keys.get(i))));
                    }
                    catch (ParseException e) {
                        this.logger.warn("Skipping invalid tree config: " + treeConfigs.get(0).get(this.name), e);
                    }
                }
                block = new BlockCustomSapling(this.plant.getAABBs(),
                                               this.behavior,
                                               this.plant.getMaterial(),
                                               BlockFaceShape.CENTER,
                                               this.plant.getDrops(),
                                               this.plant.getFacings(),
                                               this.plant.getType(),
                                               sapling.isCanUseBonemeal(),
                                               sapling.getReqLightLvl(),
                                               trees,
                                               this.plant.getSoils(),
                                               this.plant.isSoilsAllowed());
                break;
            default:
                throw new IllegalStateException("Unknown type");
        }
        registry.addBlock(block,
                          seeds == null ? this.name : this.name + ".crops",
                          false, // need custom burn time
                          seeds == null ? registry.getDefaultTab() : null);
        registry.addItemBlock(new ItemBlock(block) {
            @Override
            public int getItemBurnTime(ItemStack itemStack) {
                return plant.getBurnTime();
            }
        }, this.name);
        String oreDict = this.plant.getOreDict();
        if (oreDict != null) registry.addOre(oreDict, block);
        if (seeds != null) {
            oreDict = ((Type.Crops) this.type).getOreDict();
            registry.addItem(seeds, this.name + ".seeds", registry.getDefaultTab());
            if (oreDict != null) registry.addOre(oreDict, seeds);
        }
        if (registry.getTileEntities().stream().noneMatch(t -> t.getFirst() == TileEntityCustomPlant.class)) registry.addTileEntity(TileEntityCustomPlant.class, "customplant");
    }
}
