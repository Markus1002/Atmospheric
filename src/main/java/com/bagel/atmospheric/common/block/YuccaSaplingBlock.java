package com.bagel.atmospheric.common.block;

import javax.annotation.Nullable;

import com.bagel.atmospheric.core.registry.AtmosphericDamageSources;

import net.minecraft.block.BlockState;
import net.minecraft.block.SaplingBlock;
import net.minecraft.block.trees.Tree;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class YuccaSaplingBlock extends SaplingBlock implements net.minecraftforge.common.IPlantable {
	public YuccaSaplingBlock(Tree tree, Properties properties) {
        super(tree, properties);
    }
	
	@Override
	public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
		return worldIn.getBlockState(pos.down()).isIn(BlockTags.BAMBOO_PLANTABLE_ON);
	}
	
	@Override
	public net.minecraftforge.common.PlantType getPlantType(IBlockReader world, BlockPos pos) {
		return net.minecraftforge.common.PlantType.Desert;	
	}
	
	public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
		if (entityIn instanceof LivingEntity) {
			if (!worldIn.isRemote && (entityIn.lastTickPosX != entityIn.posX || entityIn.lastTickPosZ != entityIn.posZ)) {
				double d0 = Math.abs(entityIn.posX - entityIn.lastTickPosX);
				double d1 = Math.abs(entityIn.posZ - entityIn.lastTickPosZ);
	            if (d0 >= (double)0.003F || d1 >= (double)0.003F) {
	            	if (!entityIn.isSneaking()) {
		            	entityIn.addVelocity(MathHelper.sin((float) (entityIn.rotationYaw * Math.PI / 180.0F)) * 2F * 0.05F, 0.005F, -MathHelper.cos((float) (entityIn.rotationYaw * Math.PI / 180.0F)) * 2F * 0.05F);
	            	}
	            	entityIn.attackEntityFrom(AtmosphericDamageSources.YUCCA_SAPLING, 1.0F);	
	            }
			}
		}	
	}
	
	@Nullable
    @Override
    public PathNodeType getAiPathNodeType(BlockState state, IBlockReader world, BlockPos pos, @Nullable MobEntity entity) {
        return  PathNodeType.DAMAGE_CACTUS;
    }
}
