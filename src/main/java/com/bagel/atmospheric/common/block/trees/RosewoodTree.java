package com.bagel.atmospheric.common.block.trees;

import java.util.Random;

import javax.annotation.Nullable;

import com.bagel.atmospheric.common.world.biome.AtmosphericBiomeFeatures;
import com.bagel.atmospheric.core.registry.AtmosphericFeatures;

import net.minecraft.block.trees.Tree;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;

public class RosewoodTree extends Tree {
	
	@Nullable
	protected ConfiguredFeature<TreeFeatureConfig, ?> getTreeFeature(Random randomIn, boolean beehive) {
		return AtmosphericFeatures.ROSEWOOD_TREE.withConfiguration(beehive ? AtmosphericBiomeFeatures.ROSEWOOD_TREE_MORE_BEEHIVES_CONFIG : AtmosphericBiomeFeatures.ROSEWOOD_TREE_CONFIG);
	}
}
