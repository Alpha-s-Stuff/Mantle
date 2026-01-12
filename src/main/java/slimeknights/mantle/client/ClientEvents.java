package slimeknights.mantle.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.fabricators_of_create.porting_lib.event.client.OverlayRenderCallback;
import io.github.fabricators_of_create.porting_lib.event.client.OverlayRenderCallback.Types;
import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
import io.github.fabricators_of_create.porting_lib.models.geometry.IGeometryLoader;
import io.github.fabricators_of_create.porting_lib.models.geometry.RegisterGeometryLoadersCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.minecraft.ChatFormatting;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.block.GaugeBlock;
import slimeknights.mantle.client.book.BookLoader;
import slimeknights.mantle.client.book.repository.FileRepository;
import slimeknights.mantle.client.model.FallbackModelLoader;
import slimeknights.mantle.client.model.NBTKeyModel;
import slimeknights.mantle.client.model.RetexturedModel;
import slimeknights.mantle.client.model.TextureColorHelper;
import slimeknights.mantle.client.model.connected.ConnectedModel;
import slimeknights.mantle.client.model.util.ColoredBlockModel;
import slimeknights.mantle.client.model.util.MantleItemLayerModel;
import slimeknights.mantle.client.model.util.ModelHelper;
import slimeknights.mantle.client.render.FluidCuboid;
import slimeknights.mantle.client.render.RenderItem;
import slimeknights.mantle.command.client.MantleClientCommand;
import slimeknights.mantle.datagen.MantleTags;
import slimeknights.mantle.fluid.texture.FluidTextureManager;
import slimeknights.mantle.fluid.tooltip.FluidTooltipHandler;
import slimeknights.mantle.network.MantleNetwork;
import slimeknights.mantle.network.channel.SimpleChannel;
import slimeknights.mantle.registration.MantleRegistrations;
import slimeknights.mantle.util.OffhandCooldownTracker;
import slimeknights.mantle.util.RegistryHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ClientEvents {
  /** Called on construct to initiatlize things that need early entry */
  public static void onConstruct() {
    registerEntityRenderers();
    registerListeners();
    clientSetup();
    RegisterGeometryLoadersCallback.EVENT.register(ClientEvents::registerModelLoaders);
    commonSetup();
    SimpleChannel.initClientListener(MantleNetwork.INSTANCE.network);
  }

  @SuppressWarnings("ConstantConditions")
  static void registerEntityRenderers() {
    if (MantleRegistrations.SIGN != null) {
      BlockEntityRenderers.register(MantleRegistrations.SIGN, SignRenderer::new);
    }
    if (MantleRegistrations.HANGING_SIGN != null) {
      BlockEntityRenderers.register(MantleRegistrations.HANGING_SIGN, HangingSignRenderer::new);
    }
  }

  @SuppressWarnings("removal")
  static void registerListeners() {
    ResourceManagerHelper helper = ResourceManagerHelper.get(PackType.CLIENT_RESOURCES);
    helper.registerReloadListener(ModelHelper.LISTENER);
    helper.registerReloadListener(new BookLoader());
    ResourceColorManager.init(helper);
    FluidTooltipHandler.init(helper);
    FluidTextureManager.init(helper);
    helper.registerReloadListener(FluidCuboid.REGISTRY);
    helper.registerReloadListener(RenderItem.REGISTRY);
    helper.registerReloadListener(RenderItem.STATE_REGISTRY);
    helper.registerReloadListener(TextureColorHelper.RELOAD_LISTENER);
  }

  static void clientSetup() {
//    ModsLoadedCallback.EVENT.register((envType) -> RegistrationHelper.forEachWoodType(Sheets::addWoodType));

    BookLoader.registerBook(Mantle.getResource("test"), new FileRepository(Mantle.getResource("books/test")));
    MantleClientCommand.init();
  }

  static void registerModelLoaders(Map<ResourceLocation, IGeometryLoader<?>> loaders) {
    // standard models - useful in resource packs for any model
    loaders.put(Mantle.getResource("connected"), ConnectedModel.LOADER);
    loaders.put(Mantle.getResource("item_layer"), MantleItemLayerModel.LOADER);
    loaders.put(Mantle.getResource("colored_block"), ColoredBlockModel.LOADER);
    loaders.put(Mantle.getResource("fallback"), FallbackModelLoader.INSTANCE);

    // NBT dynamic models - require specific data defined in the block/item to use
    loaders.put(Mantle.getResource("nbt_key"), NBTKeyModel.LOADER);
    loaders.put(Mantle.getResource("retextured"), RetexturedModel.LOADER);
  }

  static void commonSetup() {
    new ExtraHeartRenderHandler().registerEvents();
    OverlayRenderCallback.EVENT.register(ClientEvents::renderOffhandAttackIndicator);
    OverlayRenderCallback.EVENT.register(ClientEvents::renderGaugeTooltip);
  }

  // registered with FORGE bus
  private static boolean renderOffhandAttackIndicator(GuiGraphics graphics, float partialTicks, Window window, OverlayRenderCallback.Types overlay) {
    // must have a player, not be in spectator, and have the indicator enabled
    Minecraft minecraft = Minecraft.getInstance();
    Options settings = minecraft.options;
    AttackIndicatorStatus indicator = settings.attackIndicator().get();
    if (minecraft.player == null || minecraft.gameMode == null || minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR || indicator == AttackIndicatorStatus.OFF) {
      return false;
    }

    // only care about hotbar and crosshair
    // will be true for hotbar, false for crosshair
    boolean isHotbar = Types.HOTBAR == overlay;
    if (!isHotbar && Types.CROSSHAIRS != overlay) {
      return false;
    }

    // fetch the current cooldown
    OffhandCooldownTracker tracker = OffhandCooldownTracker.get(minecraft.player);
    if (tracker == null) {
      return false;
    }
    float cooldown = tracker.getCooldown();
    if (cooldown >= 1.0f) {
      return false;
    }

    // show attack indicator
    switch (indicator) {
      case CROSSHAIR:
        if (!isHotbar && minecraft.options.getCameraType().isFirstPerson()) {
          if (!settings.renderDebug || settings.hideGui || minecraft.player.isReducedDebugInfo() || settings.reducedDebugInfo().get()) {
            // mostly cloned from vanilla attack indicator
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            int scaledHeight = minecraft.getWindow().getGuiScaledHeight();
            // integer division makes this a pain to line up, there might be a simplier version of this formula but I cannot think of one
            int y = (scaledHeight / 2) - 14 + (2 * (scaledHeight % 2));
            int x = minecraft.getWindow().getGuiScaledWidth() / 2 - 8;
            int width = (int)(cooldown * 17.0F);
            graphics.blit(Gui.GUI_ICONS_LOCATION, x, y, 36, 94, 16, 4);
            graphics.blit(Gui.GUI_ICONS_LOCATION, x, y, 52, 94, width, 4);
          }
        }
        break;
      case HOTBAR:
        if (isHotbar && minecraft.cameraEntity == minecraft.player) {
          int centerWidth = minecraft.getWindow().getGuiScaledWidth() / 2;
          int y = minecraft.getWindow().getGuiScaledHeight() - 20;
          int x;
          // opposite of the vanilla hand location, extra bit to offset past the offhand slot
          if (minecraft.player.getMainArm() == HumanoidArm.RIGHT) {
            x = centerWidth - 91 - 22 - 32;
          } else {
            x = centerWidth + 91 + 6 + 32;
          }
//          RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
          int l1 = (int)(cooldown * 19.0F);
          RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
          graphics.blit(Gui.GUI_ICONS_LOCATION, x, y, 0, 94, 18, 18);
          graphics.blit(Gui.GUI_ICONS_LOCATION, x, y + 18 - l1, 18, 112 - l1, 18, l1);
        }
        break;
    }
    return false;
  }



  /** Renders the tooltip when targeting the gauge block */
  private static boolean renderGaugeTooltip(GuiGraphics guiGraphics, float partialTicks, Window window, OverlayRenderCallback.Types type) {
    if (type != OverlayRenderCallback.Types.CROSSHAIRS) {
      return false;
    }
    // must not be in a screen, though chat is fine
    Minecraft minecraft = Minecraft.getInstance();
    if (minecraft.screen != null && minecraft.screen.getClass() != ChatScreen.class) {
      return false;
    }
    // must have a hit result
    if (minecraft.level == null || minecraft.hitResult == null || minecraft.hitResult.getType() != HitResult.Type.BLOCK) {
      return false;
    }
    BlockHitResult blockHit = (BlockHitResult) minecraft.hitResult;
    BlockPos pos = blockHit.getBlockPos();

    // must be targeting a gauge
    BlockState targeted = minecraft.level.getBlockState(blockHit.getBlockPos());
    if (!targeted.is(MantleTags.Blocks.GAUGES)) {
      return false;
    }
    BlockEntity gaugeContainer;
    Direction side;
    if (targeted.is(MantleTags.Blocks.ATTACHED_GAUGES)) {
      side = targeted.getValue(BlockStateProperties.FACING);
      gaugeContainer = minecraft.level.getBlockEntity(pos.relative(side.getOpposite()));
    } else {
      side = blockHit.getDirection();
      gaugeContainer = minecraft.level.getBlockEntity(pos);
    }
    // must have a block entity behind the gauge that is not blacklisted
    if (gaugeContainer == null || RegistryHelper.contains(BuiltInRegistries.BLOCK_ENTITY_TYPE, MantleTags.BlockEntities.GAUGE_BLACKLIST, gaugeContainer.getType())) {
      return false;
    }
    // block entity must have a fluid handler
    Storage<FluidVariant> handler = FluidStorage.SIDED.find(minecraft.level, gaugeContainer.getBlockPos(), null, gaugeContainer, side);
    if (handler == null) {
      return false;
    }

    // if the fluid is empty, just render the capacity
    List<Component> tooltip = List.of();
    for (StorageView<FluidVariant> view : handler) {
      FluidStack fluid = new FluidStack(view);
      if (view.getAmount() > 0 && !view.isResourceBlank()) { // Filter out blank storages
        if (fluid.isEmpty()) {
          tooltip = List.of(GaugeBlock.formatCapacity(view.getCapacity()));
        } else if (RegistryHelper.contains(BuiltInRegistries.BLOCK_ENTITY_TYPE, MantleTags.BlockEntities.HIDES_GAUGE_AMOUNT, gaugeContainer.getType())) {
          // in the tag, don't show capacity
          ResourceLocation id = BuiltInRegistries.FLUID.getKey(fluid.getFluid());
          tooltip = new ArrayList<>(3);
          tooltip.add(fluid.getDisplayName());
          FluidTooltipHandler.appendAdvanced(id, tooltip);
          tooltip.add(GaugeBlock.formatCapacity(view.getCapacity()).withStyle(ChatFormatting.GRAY));
          tooltip.add(FluidTooltipHandler.formatModName(id));
        } else {
          // render full fluid tooltip
          tooltip = FluidTooltipHandler.getFluidTooltip(fluid);
        }
        break;
      }
    }


    int x = minecraft.getWindow().getGuiScaledWidth() / 2;
    int y = minecraft.getWindow().getGuiScaledHeight() / 2;
    guiGraphics.renderTooltip(minecraft.font, tooltip, Optional.empty(), x, y);
    return false;
  }
}
