package com.luka5w.customplants.common.data;

import com.luka5w.customplants.common.blocks.*;
import com.luka5w.customplants.common.items.ItemCustomSeeds;
import net.minecraft.util.EnumFacing;

import java.util.HashMap;

public class Plant {
    
    private EnumType type;
    private String name;
    private EnumFacing facing;
    private String[] soils;
    private boolean soilsAllowed;
    private String texture;
    
    private String[] textures;
    
    private boolean pickupAllowed;
    
    private HashMap<String, Integer> drops;
    
    /**
     * Only available if {@link Plant#type} is {@link EnumType#BUSH}.
     * @return The bush
     */
    public BlockCustomBush createBush() throws ConfigException {
        if (this.type != EnumType.BUSH) throw new RuntimeException("This method requires type to be BUSH");
        return new BlockCustomBush(this);
    }
    
    /**
     * Only available if {@link Plant#type} is {@link EnumType#CROP}.
     * @return The crops (the block which is planted on the soil)
     */
    public BlockCustomCrops createCrop() throws ConfigException {
        if (this.type != EnumType.CROP) throw new RuntimeException("This method requires type to be CROP");
        return new BlockCustomCrops(this);
    }
    
    /**
     * Only available if {@link Plant#type} is {@link EnumType#CROP}.
     * @return The seeds (the item used to plant the crops)
     */
    public ItemCustomSeeds createSeeds() throws ConfigException {
        if (this.type != EnumType.CROP) throw new RuntimeException("This method requires type to be CROP");
        return new ItemCustomSeeds(this);
    }
    
    /**
     * Only available if {@link Plant#type} is {@link EnumType#EXTENDABLE}.
     * @return The extendable
     */
    public BlockCustomExtendable createExtendable() throws ConfigException {
        if (this.type != EnumType.EXTENDABLE) throw new RuntimeException("This method requires type to be EXTENDABLE");
        return new BlockCustomExtendable(this);
    }
    
    /**
     * Only available if {@link Plant#type} is {@link EnumType#OVERLAY}.
     * @return The overlay
     */
    public BlockCustomOverlay createOverlay() throws ConfigException {
        if (this.type != EnumType.OVERLAY) throw new RuntimeException("This method requires type to be OVERLAY");
        return new BlockCustomOverlay(this);
    }
    
    /**
     * Only available if {@link Plant#type} is {@link EnumType#TREE}.
     * @return The sapling
     */
    public BlockCustomSapling createSapling() throws ConfigException {
        if (this.type != EnumType.TREE) throw new RuntimeException("This method requires type to be TREE");
        return new BlockCustomSapling(this);
    }
    
    /**
     * Only available if {@link Plant#type} is {@link EnumType#TREE}.
     * @return The tree config
     */
    public TreeConfig createTree() throws ConfigException {
        if (this.type != EnumType.TREE) throw new RuntimeException("This method requires type to be TREE");
        return new TreeConfig(this);
    }
    
    /** The plant type. */
    public enum EnumType {
        BUSH,
        CROP,
        EXTENDABLE,
        OVERLAY,
        TREE;
    }
    
    public EnumType getType() throws ConfigException {
        if (this.type == null) throw new ConfigException();
        return this.type;
    }
    
    public void setType(EnumType type) {
        this.type = type;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public EnumFacing getFacing() throws ConfigException {
        if (this.facing == null) throw new ConfigException();
        return this.facing;
    }
    
    public void setFacing(EnumFacing facing) {
        this.facing = facing;
    }
    
    public String[] getSoils() throws ConfigException {
        if (this.soils == null) throw new ConfigException();
        return this.soils;
    }
    
    public void setSoils(String[] soils) {
        this.soils = soils;
    }
    
    public boolean isSoilsAllowed() {
        return soilsAllowed;
    }
    
    public void setSoilsAllowed(boolean soilsAllowed) {
        this.soilsAllowed = soilsAllowed;
    }
    
    public String getTexture() throws ConfigException {
        if (this.texture == null) throw new ConfigException();
        return this.texture;
    }
    
    public void setTexture(String texture) {
        this.texture = texture;
    }
    
    public String[] getTextures() throws ConfigException {
        if (this.textures == null) throw new ConfigException();
        return this.textures;
    }
    
    public void setTextures(String[] textures) {
        this.textures = textures;
    }
    
    public boolean isPickupAllowed() {
        return pickupAllowed;
    }
    
    public void setPickupAllowed(boolean pickupAllowed) {
        this.pickupAllowed = pickupAllowed;
    }
    
    public HashMap<String, Integer> getDrops() throws ConfigException {
        if (this.drops == null) throw new ConfigException();
        return this.drops;
    }
    
    public void setDrops(HashMap<String, Integer> drops) {
        this.drops = drops;
    }
    
    public void getModel() {
        // TODO: 18.04.22
        //  implement
        //  is this even needed?
        //  ref: getTextures
    }
}
