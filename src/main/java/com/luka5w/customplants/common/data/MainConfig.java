package com.luka5w.customplants.common.data;

import com.luka5w.customplants.CustomPlants;
import net.minecraftforge.common.config.Config;

@Config(modid = CustomPlants.MOD_ID)
public class MainConfig {

    @Config.Comment({"The directory where all plants and their resources are configured in (relative to the server.jar)."})
    @Config.Name("dir_resources")
    @Config.RequiresMcRestart
    public static String dirResources = "config/customplants";

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

}
