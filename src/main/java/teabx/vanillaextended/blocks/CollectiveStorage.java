package teabx.vanillaextended.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.network.NetworkHooks;
import teabx.vanillaextended.blocks.interfaces.IStorageBlockPart;
import teabx.vanillaextended.tileentities.CSTile;
import teabx.vanillaextended.tileentities.TPTile;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class CollectiveStorage extends Block {

    public CollectiveStorage(Properties properties) {
        super(properties);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        CSTile tile = (CSTile) worldIn.getTileEntity(pos);
        if(tile.getSb() == null){
            tile.setSb(new StorageBlock(tile));
            tile.getSb().add(tile);
        }
    }

    @Override
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        CSTile tile = (CSTile) worldIn.getTileEntity(pos);
        if(tile.getSb() == null) tile.setSb(new StorageBlock(tile));

        for(TileEntity te : tile.getConnectedTiles()){
            if(te instanceof IStorageBlockPart){
                ((IStorageBlockPart) te).updateStorageBlock(tile.getSb());
            }
        }

        tile.getSb().update();

        if(!worldIn.isRemote){
            NetworkHooks.openGui((ServerPlayerEntity) player, tile, tile.getPos());
        }
        return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new CSTile();
    }
}
