package com.luka5w.customplants.common.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.EntityNotFoundException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.List;

public class TargetSelectors {
    
    /**
     * Returns a list of all entities matching the passed target selector.
     * @param worldIn The world of the executing block.
     * @param pos The position of the executing block.
     * @param state The state of the executing block.
     * @param target The target selector.
     * @return A list of all entities matching the passed target selector. This list might be empty.
     * @throws CommandException When the target selector is invalid.
     */
    public static List<Entity> getMatchingEntities(World worldIn, BlockPos pos, IBlockState state, String target) throws CommandException {
        ICommandSender sender = new ICommandSender() {
            public String getName() {
                return state.getBlock().getUnlocalizedName();
            }
        
            public boolean canUseCommand(int permLevel, String commandName) {
                return permLevel <= 2;
            }
        
            public BlockPos getPosition() {
                return pos;
            }
        
            public Vec3d getPositionVector() {
                return new Vec3d((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5);
            }
        
            public World getEntityWorld() {
                return worldIn;
            }
        
            public MinecraftServer getServer() {
                return worldIn.getMinecraftServer();
            }
        
            // ICommandSender#getCommandSenderEntity can be null because it is accessed by
            // EntitySelector#matchEntitiesDefault when the target starts with %s or
            // EntitySelector#getEntitiesFromPredicates to overwrite the previously generated list of matching entities
        };
        List<Entity> targets;
        try {
            targets = CommandBase.getEntityList(worldIn.getMinecraftServer(), sender, target);
        }
        catch (EntityNotFoundException e) {
            return Collections.emptyList();
        }
        return targets;
    }
}
