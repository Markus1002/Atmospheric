package com.bagel.atmospheric.common.item;

import java.util.List;
import java.util.function.Predicate;

import com.bagel.atmospheric.common.entity.AtmosphericBoatEntity;

import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class AtmosphericBoatItem extends Item {
	private static final Predicate<Entity> field_219989_a = EntityPredicates.NOT_SPECTATING.and(Entity::canBeCollidedWith);
    private final AtmosphericBoatEntity.Type type;

    public AtmosphericBoatItem(AtmosphericBoatEntity.Type typeIn, Item.Properties properties) {
        super(properties);
        this.type = typeIn;
        DispenserBlock.registerDispenseBehavior(this, new DispenserBoatBehavior(typeIn));
    }

    /**
     * Called to trigger the item's "innate" right click behavior. To handle when this item is used on a Block, see
     * {@link #onItemUse}.
     */
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        RayTraceResult raytraceresult = rayTrace(worldIn, playerIn, RayTraceContext.FluidMode.ANY);
        if (raytraceresult.getType() == RayTraceResult.Type.MISS) {
            return new ActionResult<>(ActionResultType.PASS, itemstack);
        } else {
            Vec3d vec3d = playerIn.getLook(1.0F);
            List<Entity> list = worldIn.getEntitiesInAABBexcluding(playerIn, playerIn.getBoundingBox().expand(vec3d.scale(5.0D)).grow(1.0D), field_219989_a);
            if (!list.isEmpty()) {
                Vec3d vec3d1 = playerIn.getEyePosition(1.0F);

                for(Entity entity : list) {
                    AxisAlignedBB axisalignedbb = entity.getBoundingBox().grow((double)entity.getCollisionBorderSize());
                    if (axisalignedbb.contains(vec3d1)) {
                        return new ActionResult<>(ActionResultType.PASS, itemstack);
                    }
                }
            }

            if (raytraceresult.getType() == RayTraceResult.Type.BLOCK) {
            	AtmosphericBoatEntity boatentity = new AtmosphericBoatEntity(worldIn, raytraceresult.getHitVec().x, raytraceresult.getHitVec().y, raytraceresult.getHitVec().z);
                boatentity.setBoatType(this.type);
                boatentity.rotationYaw = playerIn.rotationYaw;
                if (!worldIn.hasNoCollisions(boatentity, boatentity.getBoundingBox().grow(-0.1D))) {
                    return new ActionResult<>(ActionResultType.FAIL, itemstack);
                } else {
                    if (!worldIn.isRemote) {
                        worldIn.addEntity(boatentity);
                    }

                    if (!playerIn.abilities.isCreativeMode) {
                        itemstack.shrink(1);
                    }

                    playerIn.addStat(Stats.ITEM_USED.get(this));
                    return new ActionResult<>(ActionResultType.SUCCESS, itemstack);
                }
            } else {
                return new ActionResult<>(ActionResultType.PASS, itemstack);
            }
        }
    }
    
    static class DispenserBoatBehavior extends DefaultDispenseItemBehavior {
    	private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();
    	private final AtmosphericBoatEntity.Type type;

    	public DispenserBoatBehavior(AtmosphericBoatEntity.Type type) {
    		this.type = type;
    	}

    	@SuppressWarnings("deprecation")
    	public ItemStack dispenseStack(IBlockSource iBlockSource, ItemStack stack) {
    		Direction direction = iBlockSource.getBlockState().get(DispenserBlock.FACING);
    		World world = iBlockSource.getWorld();
    		double x = iBlockSource.getX() + (double) ((float) direction.getXOffset() * 1.125f);
    		double y = iBlockSource.getY() + (double) ((float) direction.getYOffset() * 1.125f);
    		double z = iBlockSource.getZ() + (double) ((float) direction.getZOffset() * 1.125f);
    		BlockPos pos = iBlockSource.getBlockPos().offset(direction);
    		double adjustY;
    		if (world.getFluidState(pos).isTagged(FluidTags.WATER)) {
    			adjustY = 1d;
    		} else {
    			if (!world.getBlockState(pos).isAir() || !world.getFluidState(pos.down()).isTagged(FluidTags.WATER)) {
    				return this.defaultDispenseItemBehavior.dispense(iBlockSource, stack);
    			}
    			adjustY = 0d;
    		}
    		AtmosphericBoatEntity boat = new AtmosphericBoatEntity(world, x, y + adjustY, z);
    		boat.setBoatType(this.type);
    		boat.rotationYaw = direction.getHorizontalAngle();
    		world.addEntity(boat);
    		stack.shrink(1);
    		return stack;
    	}

    	protected void playDispenseSound(IBlockSource iBlockSource) {
    		iBlockSource.getWorld().playEvent(1000, iBlockSource.getBlockPos(), 0);
    	}
    }

}
