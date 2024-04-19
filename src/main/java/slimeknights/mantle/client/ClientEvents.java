package slimeknights.mantle.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.fabricators_of_create.porting_lib.event.client.OverlayRenderCallback;
import io.github.fabricators_of_create.porting_lib.event.client.OverlayRenderCallback.Types;
import io.github.fabricators_of_create.porting_lib.models.geometry.IGeometryLoader;
import io.github.fabricators_of_create.porting_lib.models.geometry.RegisterGeometryLoadersCallback;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.level.GameType;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.client.book.BookLoader;
import slimeknights.mantle.client.book.repository.FileRepository;
import slimeknights.mantle.client.model.FallbackModelLoader;
import slimeknights.mantle.client.model.NBTKeyModel;
import slimeknights.mantle.client.model.RetexturedModel;
import slimeknights.mantle.client.model.connected.ConnectedModel;
import slimeknights.mantle.client.model.fluid.FluidsModel;
import slimeknights.mantle.client.model.inventory.InventoryModel;
import slimeknights.mantle.client.model.util.ColoredBlockModel;
import slimeknights.mantle.client.model.util.MantleItemLayerModel;
import slimeknights.mantle.client.model.util.ModelHelper;
import slimeknights.mantle.fluid.texture.FluidTextureManager;
import slimeknights.mantle.client.render.MantleShaders;
import slimeknights.mantle.fluid.tooltip.FluidTooltipHandler;
import slimeknights.mantle.network.MantleNetwork;
import slimeknights.mantle.registration.MantleRegistrations;
import slimeknights.mantle.registration.RegistrationHelper;
import slimeknights.mantle.util.OffhandCooldownTracker;

import java.util.Map;
import java.util.function.Function;

import static net.minecraft.client.renderer.Sheets.SIGN_SHEET;

@SuppressWarnings("unused")
public class ClientEvents implements ClientModInitializer {
  private static final Function<OffhandCooldownTracker,Float> COOLDOWN_TRACKER = OffhandCooldownTracker::getCooldown;

  static void registerEntityRenderers() {
    BlockEntityRenderers.register(MantleRegistrations.SIGN, SignRenderer::new);
  }

  static void registerListeners() {
    ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(ModelHelper.LISTENER);
    ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new BookLoader());
    ResourceColorManager.init();
    FluidTooltipHandler.init();
    FluidTextureManager.init(event);
  }

  @Override
  public void onInitializeClient() {
    RegistrationHelper.forEachWoodType(woodType ->  {
      ResourceLocation location = new ResourceLocation(woodType.name());
      Sheets.SIGN_MATERIALS.put(woodType, new Material(SIGN_SHEET, new ResourceLocation(location.getNamespace(), "entity/signs/" + location.getPath())));
    });

    BookLoader.registerBook(Mantle.getResource("test"), new FileRepository(Mantle.getResource("books/test")));

    registerEntityRenderers();
    registerListeners();
    CoreShaderRegistrationCallback.EVENT.register(MantleShaders::registerShaders);
    RegisterGeometryLoadersCallback.EVENT.register(ClientEvents::registerModelLoaders);
    commonSetup();
    MantleNetwork.INSTANCE.network.initClientListener();
  }

  static void registerModelLoaders(Map<ResourceLocation, IGeometryLoader<?>> loaders) {
    // standard models - useful in resource packs for any model
    event.register("connected", ConnectedModel.LOADER);
    event.register("item_layer", MantleItemLayerModel.LOADER);
    event.register("colored_block", ColoredBlockModel.LOADER);
    event.register("fallback", FallbackModelLoader.INSTANCE);

    // NBT dynamic models - require specific data defined in the block/item to use
    event.register("nbt_key", NBTKeyModel.LOADER);
    event.register("retextured", RetexturedModel.LOADER);

    // data models - contain information for other parts in rendering rather than rendering directly
    event.register("inventory", InventoryModel.LOADER);
    event.register("fluids", FluidsModel.LOADER);
  }

  static void commonSetup() {
    OverlayRenderCallback.EVENT.register(new ExtraHeartRenderHandler()::renderHealthbar);
    OverlayRenderCallback.EVENT.register(ClientEvents::renderOffhandAttackIndicator);
  }

  // registered with FORGE bus
  private static boolean renderOffhandAttackIndicator(GuiGraphics guiGraphics, float partialTicks, Window window, OverlayRenderCallback.Types overlay) {
    // must have a player, not be in spectator, and have the indicator enabled
    Minecraft minecraft = Minecraft.getInstance();
    Options settings = minecraft.options;
    AttackIndicatorStatus indicator = settings.attackIndicator().get();
    if (minecraft.player == null || minecraft.gameMode == null || minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR || indicator == AttackIndicatorStatus.OFF) {
      return;
    }

    // only care about hotbar and crosshair
    NamedGuiOverlay overlay = event.getOverlay();
    // will be true for hotbar, false for crosshair
    boolean isHotbar = VanillaGuiOverlay.HOTBAR.type() == overlay;
    if (!isHotbar && VanillaGuiOverlay.CROSSHAIR.type() != overlay) {
      return;
    }

    // enabled if either in the tag, or if force enabled
    float cooldown = OffhandCooldownTracker.CAPABILITY.maybeGet(minecraft.player).filter(OffhandCooldownTracker::isEnabled).map(COOLDOWN_TRACKER).orElse(1.0f);
    if (cooldown >= 1.0f) {
      return false;
    }

    // show attack indicator
    PoseStack matrixStack = event.getPoseStack();
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
            guiGraphics.blit(Gui.GUI_ICONS_LOCATION, x, y, 36, 94, 16, 4);
            guiGraphics.blit(Gui.GUI_ICONS_LOCATION, x, y, 52, 94, width, 4);
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
          int l1 = (int)(cooldown * 19.0F);
          RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
          guiGraphics.blit(Gui.GUI_ICONS_LOCATION, x, y, 0, 94, 18, 18);
          guiGraphics.blit(Gui.GUI_ICONS_LOCATION, x, y + 18 - l1, 18, 112 - l1, 18, l1);
        }
        break;
    }
    return false;
  }
}
