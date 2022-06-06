package com.luka5w.customplants.common.data;

import com.luka5w.customplants.CustomPlants;
import net.minecraftforge.common.config.Config;

@Config(modid = CustomPlants.MOD_ID)
public class MainConfig {

    @Config.Comment({"The directory where all plant packs are stored in (relative to the server.jar)."})
    @Config.Name("dir_plants_packs")
    @Config.RequiresMcRestart
    public static String dirPlantsPacks = "addons/customplants";

    @Config.Name("updates")
    public static Updates updates = new Updates();
    public static class Updates {
        @Config.Comment({"Whether to advice players about a new stable version."})
        @Config.Name("advice_stable")
        @Config.RequiresMcRestart
        public boolean adviceStable = true;

        @Config.Comment({"Whether to advice players about a new unstable version."})
        @Config.Name("advice_unstable")
        @Config.RequiresMcRestart
        public boolean adviceUnstable = false;
    }
    
    @Config.Name("defaults")
    public static Defaults defaults = new Defaults();
    
    private static class Defaults {
        @Config.Comment({"Enable Creative Tab with all custom plants"})
        @Config.Name("creative_tab")
        @Config.RequiresMcRestart
        public boolean creativeTab = true;
    }
    
    @Config.Name("performance")
    public static Performance performance = new Performance();
    
    public static class Performance {
        
        @Config.Comment({"The maximum items a plant can drop at once."})
        @Config.Name("max_drops")
        @Config.RequiresMcRestart
        public static int maxDrops;
    }
}
