package slimeknights.mantle.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
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
import net.minecraft.client.gui.GuiComponent;
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
import slimeknights.mantle.client.model.fluid.FluidTextureModel;
import slimeknights.mantle.client.model.fluid.FluidsModel;
import slimeknights.mantle.client.model.inventory.InventoryModel;
import slimeknights.mantle.client.model.util.ColoredBlockModel;
import slimeknights.mantle.client.model.util.MantleItemLayerModel;
import slimeknights.mantle.client.model.util.ModelHelper;
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
    loaders.put(Mantle.getResource("connected"), ConnectedModel.Loader.INSTANCE);
    loaders.put(Mantle.getResource("item_layer"), MantleItemLayerModel.LOADER);
    loaders.put(Mantle.getResource("colored_block"), ColoredBlockModel.LOADER);
    loaders.put(Mantle.getResource("fallback"), FallbackModelLoader.INSTANCE);

    // NBT dynamic models - require specific data defined in the block/item to use
    loaders.put(Mantle.getResource("nbt_key"), NBTKeyModel.LOADER);
    loaders.put(Mantle.getResource("retextured"), RetexturedModel.Loader.INSTANCE);

    // data models - contain information for other parts in rendering rather than rendering directly
    loaders.put(Mantle.getResource("fluid_texture"), FluidTextureModel.LOADER);
    loaders.put(Mantle.getResource("inventory"), InventoryModel.Loader.INSTANCE);
    loaders.put(Mantle.getResource("fluids"), FluidsModel.Loader.INSTANCE);

    ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(FluidTextureModel.LOADER);
  }

  static void commonSetup() {
    OverlayRenderCallback.EVENT.register(new ExtraHeartRenderHandler()::renderHealthbar);
    OverlayRenderCallback.EVENT.register(ClientEvents::renderOffhandAttackIndicator);
  }

  // registered with FORGE bus
  private static boolean renderOffhandAttackIndicator(PoseStack matrixStack, float partialTicks, Window window, OverlayRenderCallback.Types overlay) {
    // must have a player, not be in spectator, and have the indicator enabled
    Minecraft minecraft = Minecraft.getInstance();
    Options settings = minecraft.options;
    if (minecraft.player == null || minecraft.gameMode == null || minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR || settings.attackIndicator().get() == AttackIndicatorStatus.OFF) {
      return false;
    }

    if (overlay != Types.CROSSHAIRS /*&& overlay != Types.HOTBAR_ELEMENT*/) {
      return false;
    }

    // enabled if either in the tag, or if force enabled
    float cooldown = OffhandCooldownTracker.CAPABILITY.maybeGet(minecraft.player).filter(OffhandCooldownTracker::isEnabled).map(COOLDOWN_TRACKER).orElse(1.0f);
    if (cooldown >= 1.0f) {
      return false;
    }

    // show attack indicator
    switch (settings.attackIndicator().get()) {
      case CROSSHAIR:
        if (overlay == Types.CROSSHAIRS && minecraft.options.getCameraType().isFirstPerson()) {
          if (!settings.renderDebug || settings.hideGui || minecraft.player.isReducedDebugInfo() || settings.reducedDebugInfo().get()) {
            // mostly cloned from vanilla attack indicator
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            int scaledHeight = minecraft.getWindow().getGuiScaledHeight();
            // integer division makes this a pain to line up, there might be a simplier version of this formula but I cannot think of one
            int y = (scaledHeight / 2) - 14 + (2 * (scaledHeight % 2));
            int x = minecraft.getWindow().getGuiScaledWidth() / 2 - 8;
            int width = (int)(cooldown * 17.0F);
            RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
            minecraft.gui.blit(matrixStack, x, y, 36, 94, 16, 4);
            minecraft.gui.blit(matrixStack, x, y, 52, 94, width, 4);
          }
        }
        break;
      case HOTBAR:
        if (/*overlay == ForgeIngameGui.HOTBAR_ELEMENT && */minecraft.cameraEntity == minecraft.player) {
          int centerWidth = minecraft.getWindow().getGuiScaledWidth() / 2;
          int y = minecraft.getWindow().getGuiScaledHeight() - 20;
          int x;
          // opposite of the vanilla hand location, extra bit to offset past the offhand slot
          if (minecraft.player.getMainArm() == HumanoidArm.RIGHT) {
            x = centerWidth - 91 - 22 - 32;
          } else {
            x = centerWidth + 91 + 6 + 32;
          }
          RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
          int l1 = (int)(cooldown * 19.0F);
          RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
          minecraft.gui.blit(matrixStack, x, y, 0, 94, 18, 18);
          minecraft.gui.blit(matrixStack, x, y + 18 - l1, 18, 112 - l1, 18, l1);
        }
        break;
    }
    return false;
  }
}
