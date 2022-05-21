package com.luka5w.customplants.common.util.debug_please_remove;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

/**
 * A collection of debugging utilities.
 * @Deprecated THIS CLASS HAS TO BE REMOVED BEFORE RELEASE!!!
 */
@Deprecated
public class DebugUtils {
    
    /**
     * In KDE (Window Mgr of Kubuntu), I'm unable to get the mouse back when the client is stopped by a breakpoint.
     * @deprecated This method isn't deprecated but <b>MUST NOT</b> be used in production code!!!
     * @param worldIn
     * @param enabled must be true. (For quick disabling this without changing the imports and causing the need to reimport DebugUtils)
     * @return Whether the focus is lost.
     */
    @Deprecated
    public static boolean releaseMouse(World worldIn, boolean enabled) {
        if (worldIn != null && enabled && worldIn.isRemote) {
            Minecraft.getMinecraft().setIngameNotInFocus();
            return true;
        }
        return false;
    }
}
