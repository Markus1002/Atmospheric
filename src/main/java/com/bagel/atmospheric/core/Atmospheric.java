package com.bagel.atmospheric.core;

import com.bagel.atmospheric.common.data.PassionVineBundleDispenseBehavior;
import com.bagel.atmospheric.common.data.PassionVineDispenseBehavior;
import com.bagel.atmospheric.common.world.biome.AtmosphericBiomeFeatures;
import com.bagel.atmospheric.core.data.AtmosphericBlockData;
import com.bagel.atmospheric.core.data.AtmosphericColors;
import com.bagel.atmospheric.core.registry.AtmosphericBiomes;
import com.bagel.atmospheric.core.registry.AtmosphericBlocks;
import com.bagel.atmospheric.core.registry.AtmosphericEffects;
import com.bagel.atmospheric.core.registry.AtmosphericEntities;
import com.bagel.atmospheric.core.registry.AtmosphericItems;

import net.minecraft.block.DispenserBlock;
import net.minecraft.potion.Effect;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("atmospheric")
@Mod.EventBusSubscriber(modid = "atmospheric", bus = Mod.EventBusSubscriber.Bus.MOD)
public class Atmospheric
{
    //private static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "atmospheric";

    public Atmospheric() {
    	IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        AtmosphericBlocks.BLOCKS.register(modEventBus);
        AtmosphericItems.ITEMS.register(modEventBus);
    	AtmosphericBiomes.BIOMES.register(modEventBus);
        AtmosphericEntities.ENTITY_TYPES.register(modEventBus);
        AtmosphericEffects.EFFECTS.register(modEventBus);
        AtmosphericEffects.POTIONS.register(modEventBus);
        
        MinecraftForge.EVENT_BUS.register(this);
        
        modEventBus.addListener(this::setup);
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
        	modEventBus.addListener(this::clientSetup);
        });
    }
    
    private void setup(final FMLCommonSetupEvent event)
	{    	
    	AtmosphericBiomeFeatures.addCarvables();
        AtmosphericBiomes.registerBiomesToDictionary();
    	AtmosphericBlockData.registerCompostables();
    	AtmosphericBlockData.registerFlammables();
    	AtmosphericBlockData.registerStrippables();
    	AtmosphericEffects.registerBrewingRecipes();
    	DispenserBlock.registerDispenseBehavior(AtmosphericBlocks.PASSION_VINE_BUNDLE.get().asItem(), new PassionVineBundleDispenseBehavior());
    	DispenserBlock.registerDispenseBehavior(AtmosphericBlocks.PASSION_VINE.get().asItem(), new PassionVineDispenseBehavior());
	}
    
    private void clientSetup(final FMLClientSetupEvent event) 
    {
    	AtmosphericColors.registerBlockColors();
    	AtmosphericBlockData.setupRenderLayer();
    	AtmosphericEntities.registerRendering();
    }
    
    @SubscribeEvent
	public static void onRegisterEffects(RegistryEvent.Register<Effect> event) {
    	event.getRegistry().registerAll(
    			AtmosphericEffects.RELIEF, 
    			AtmosphericEffects.WORSENING,
    			AtmosphericEffects.SPITTING);
    }
}
