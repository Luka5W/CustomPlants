import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

// TODO: 21.05.22 search for a way to add those docs to the jars (libraries)
/**
 * A utility file to collect missing JavaDocs from experience or random forum threads.<br /><br />
 *
 * Styleguide:<br />
 * 1st line: link to the method, class, etc.<br />
 * 2nd line: link to tge source<br />
 * ...: normal JavaDoc<br />
 * Docs may get grouped in inner interfaces.<br />
 * absolute package paths!
 */
public interface ListOfJavaDocs {
    
    interface World {
    
        /**
         * {@link net.minecraft.world.World#setBlockState(BlockPos, IBlockState, int)}
         * Src: Cadiboo's <a href="https://forums.minecraftforge.net/topic/64271-solvedsetblockstate-flags/?do=findComment&comment=303928">answer</a>
         *
         * @param pos      The position of the block.
         * @param newState The new Meta data of the block.
         * @param flags    1: Will cause a block update.<br />
         *                 2: Will send the change to clients.<br />
         *                 4: Will prevent the block from being re-rendered, if this is a client world.<br />
         *                 8: Will force any re-renders to run on the main thread instead of the worker pool, if this is a
         *                 client world and flag 4 is clear.<br />
         *                 16: Will prevent observers from seeing this change.<br />
         *                 Flags can be OR-ed.
         */
        public boolean setBlockState(BlockPos pos, IBlockState newState, int flags);
    }
}
