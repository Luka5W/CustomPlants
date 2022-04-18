package com.luka5w.customplants.common.util;

import com.luka5w.customplants.common.data.MainConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

/**
 * Utility to check for updates.
 */
@Mod.EventBusSubscriber
public class UpdateChecker {

    private static ITextComponent updateAdvice;

    /**
     * Initiates the UpdateChecker and requests if a new version is available.
     *
     * @param logger A logger to inform via console.
     */
    public static void init(Logger logger) {
        try {
            updateAdvice = UpdateChecker.getAdviceAboutNewReleasesWhenExisting(MainConfig.updates.adviceUnstable, MainConfig.updates.adviceStable);
        } catch (UpdateChecker.UpdateException e) {
            updateAdvice = e.getTranslation();
        }
        if (updateAdvice != null) logger.info(updateAdvice.getFormattedText());
    }

    /**
     * Advice player about a new update when available.
     */
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void advicePlayer(EntityJoinWorldEvent event) {
        // World#isRemote == true -> logical client
        if (updateAdvice != null && event.getWorld().isRemote && event.getEntity() instanceof EntityPlayer) {
            event.getEntity().sendMessage(new TextComponentTranslation("customplants.update.chat_prefix", updateAdvice));
            // Don't annoy anymore
            updateAdvice = null;
        }
    }

    /**
     * Calls {@link ForgeVersion#getResult(ModContainer)} with this mod as container.
     *
     * @return The result from {@link ForgeVersion#getResult(ModContainer)}
     */
    public static ForgeVersion.CheckResult checkVersion() {
        return ForgeVersion.getResult(Loader.instance().activeModContainer());
    }

    /**
     * Utility class to retrieve a text component translation to send via console or in the chat about new available updates.
     * Returns null on success because then is nothing to tell.
     * @param adviceOnNewUnstable Whether to advice the player/ admin on new unstable releases.
     * @param adviceOnNewStable Whether to advice the player/ admin on new stable releases.
     * @return The translation component to send to the player/ admin or null on success.
     * @throws UpdateException When the update check failed or is pending. The exception contains the translation component.
     */
    @Nullable
    public static ITextComponent getAdviceAboutNewReleasesWhenExisting(boolean adviceOnNewUnstable, boolean adviceOnNewStable) throws UpdateException {
        if (adviceOnNewUnstable || adviceOnNewStable) {
            ForgeVersion.CheckResult result = checkVersion();
            switch (result.status) {
                case UP_TO_DATE: // this version >= latest stable
                case AHEAD: // undocumented
                    // just assume everything is OK...
                case BETA: // this version >= latest unstable
                    return null;
                case OUTDATED: // this version < latest stable
                    return adviceOnNewStable ? new TextComponentTranslation("customplants.update.outdated", result.url) : null;
                case BETA_OUTDATED: // this version < latest unstable
                    return adviceOnNewStable ? new TextComponentTranslation("customplants.update.beta_outdated", result.url) : null;
                case PENDING:
                    // treat pending requests as failed
                case FAILED:
                default:
                    throw new UpdateException(new TextComponentTranslation("customplants.update.failed"));
            }
        } else {
            return null;
        }
    }

    /**
     * Thrown by when the update check failed. The translation is retreived via {@link #getTranslation()}
     */
    public static class UpdateException extends Exception {

        private final TextComponentTranslation translation;

        public UpdateException(TextComponentTranslation translation) {
            super();
            this.translation = translation;
        }

        public UpdateException(TextComponentTranslation translation, String message) {
            super(message);
            this.translation = translation;
        }

        public TextComponentTranslation getTranslation() {
            return translation;
        }
    }
}
