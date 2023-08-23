package slimeknights.mantle.client;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.config.Config;
import io.github.fabricators_of_create.porting_lib.event.client.OverlayRenderCallback.Types;

import java.util.Random;

public class ExtraHeartRenderHandler {
  private static final ResourceLocation ICON_HEARTS = new ResourceLocation(Mantle.modId, "textures/gui/hearts.png");
  private static final ResourceLocation ICON_ABSORB = new ResourceLocation(Mantle.modId, "textures/gui/absorb.png");
  private static final ResourceLocation ICON_VANILLA = Gui.GUI_ICONS_LOCATION;

  private final Minecraft mc = Minecraft.getInstance();

  private int playerHealth = 0;
  private int lastPlayerHealth = 0;
  private long healthUpdateCounter = 0;
  private long lastSystemTime = 0;
  private final Random rand = new Random();

  private int regen;

  /**
   * Draws a texture to the screen
   * @param guiGraphics  gui graphics instance
   * @param texture      The texture to draw
   * @param x            X position
   * @param y            Y position
   * @param textureX     Texture X
   * @param textureY     Texture Y
   * @param width        Width to draw
   * @param height       Height to draw
   */
  private void blit(GuiGraphics guiGraphics, ResourceLocation texture, int x, int y, int textureX, int textureY, int width, int height) {
    guiGraphics.blit(texture, x, y, textureX, textureY, width, height);
  }

  /* HUD */

  /**
   * Event listener
   * @param guiGraphics  Event instance
   */
  public boolean renderHealthbar(GuiGraphics guiGraphics, float partialTicks, Window window, Types type) {
    if (!Config.EXTRA_HEART_RENDERER.get() || type != Types.PLAYER_HEALTH) {
      return false;
    }
    // ensure its visible
    if (mc.options.hideGui || !(mc.gameMode.canHurtPlayer() && mc.getCameraEntity() instanceof Player)) {
      return false;
    }
    Entity renderViewEnity = this.mc.getCameraEntity();
    if (!(renderViewEnity instanceof Player player)) {
      return false;
    }
    RenderSystem.enableBlend();
    RenderSystem.defaultBlendFunc();
    RenderSystem.disableDepthTest();
    RenderSystem.setShaderTexture(0, Gui.GUI_ICONS_LOCATION);

    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    RenderSystem.setShader(GameRenderer::getPositionTexShader);

    this.mc.getProfiler().push("health");

    // extra setup stuff from us
    int left_height = 39;
    if (FabricLoader.getInstance().getObjectShare().get("raised:hud") instanceof Integer distance) {
      left_height += distance;
    }
    int width = this.mc.getWindow().getGuiScaledWidth();
    int height = this.mc.getWindow().getGuiScaledHeight();
    int updateCounter = this.mc.gui.getGuiTicks();

    // start default forge/mc rendering
    // changes are indicated by comment

    int health = Mth.ceil(player.getHealth());
    boolean highlight = this.healthUpdateCounter > (long) updateCounter && (this.healthUpdateCounter - (long) updateCounter) / 3L % 2L == 1L;

    if (health < this.playerHealth && player.invulnerableTime > 0) {
      this.lastSystemTime = Util.getMillis();
      this.healthUpdateCounter = (updateCounter + 20);
    }
    else if (health > this.playerHealth && player.invulnerableTime > 0) {
      this.lastSystemTime = Util.getMillis();
      this.healthUpdateCounter = (updateCounter + 10);
    }

    if (Util.getMillis() - this.lastSystemTime > 1000L) {
      this.playerHealth = health;
      this.lastPlayerHealth = health;
      this.lastSystemTime = Util.getMillis();
    }

    this.playerHealth = health;
    int healthLast = this.lastPlayerHealth;

    AttributeInstance attrMaxHealth = player.getAttribute(Attributes.MAX_HEALTH);
    float healthMax = attrMaxHealth == null ? 0 : (float) attrMaxHealth.getValue();
    float absorb = Mth.ceil(player.getAbsorptionAmount());

    // CHANGE: simulate 10 hearts max if there's more, so vanilla only renders one row max
    healthMax = Math.min(healthMax, 20f);
    health = Math.min(health, 20);
    absorb = Math.min(absorb, 20);

    int healthRows = Mth.ceil((healthMax + absorb) / 2.0F / 10.0F);
    int rowHeight = Math.max(10 - (healthRows - 2), 3);

    this.rand.setSeed(updateCounter * 312871L);

    int left = width / 2 - 91;
    int top = height - left_height;
    // change: these are unused below, unneeded? should these adjust the Forge variable?
    //left_height += (healthRows * rowHeight);
    //if (rowHeight != 10) left_height += 10 - rowHeight;

    this.regen = -1;
    if (player.hasEffect(MobEffects.REGENERATION)) {
      this.regen = updateCounter % 25;
    }

    assert this.mc.level != null;
    final int TOP = 9 * (this.mc.level.getLevelData().isHardcore() ? 5 : 0);
    final int BACKGROUND = (highlight ? 25 : 16);
    int MARGIN = 16;
    if      (player.hasEffect(MobEffects.POISON)) MARGIN += 36;
    else if (player.hasEffect(MobEffects.WITHER)) MARGIN += 72;
    float absorbRemaining = absorb;

    for (int i = Mth.ceil((healthMax + absorb) / 2.0F) - 1; i >= 0; --i) {
      int row = Mth.ceil((float) (i + 1) / 10.0F) - 1;
      int x = left + i % 10 * 8;
      int y = top - row * rowHeight;

      if (health <= 4) y += this.rand.nextInt(2);
      if (i == this.regen) y -= 2;

      this.blit(guiGraphics, Gui.GUI_ICONS_LOCATION, x, y, BACKGROUND, TOP, 9, 9);

      if (highlight) {
        if (i * 2 + 1 < healthLast) {
          this.blit(guiGraphics, Gui.GUI_ICONS_LOCATION, x, y, MARGIN + 54, TOP, 9, 9); //6
        }
        else if (i * 2 + 1 == healthLast) {
          this.blit(guiGraphics, Gui.GUI_ICONS_LOCATION, x, y, MARGIN + 63, TOP, 9, 9); //7
        }
      }

      if (absorbRemaining > 0.0F) {
        if (absorbRemaining == absorb && absorb % 2.0F == 1.0F) {
          this.blit(guiGraphics, Gui.GUI_ICONS_LOCATION, x, y, MARGIN + 153, TOP, 9, 9); //17
          absorbRemaining -= 1.0F;
        }
        else {
          this.blit(guiGraphics, Gui.GUI_ICONS_LOCATION, x, y, MARGIN + 144, TOP, 9, 9); //16
          absorbRemaining -= 2.0F;
        }
      }
      else {
        if (i * 2 + 1 < health) {
          this.blit(guiGraphics, Gui.GUI_ICONS_LOCATION, x, y, MARGIN + 36, TOP, 9, 9); //4
        }
        else if (i * 2 + 1 == health) {
          this.blit(guiGraphics, Gui.GUI_ICONS_LOCATION, x, y, MARGIN + 45, TOP, 9, 9); //5
        }
      }
    }

    this.renderExtraHearts(guiGraphics, left, top, player);
    this.renderExtraAbsorption(guiGraphics, left, top - rowHeight, player);

    left_height += 10;
    if (absorb > 0) {
      left_height += 10;
    }

    RenderSystem.disableBlend();
    this.mc.getProfiler().pop();
    return true;
  }

  /**
   * Gets the texture from potion effects
   * @param player  Player instance
   * @return  Texture offset for potion effects
   */
  private int getPotionOffset(Player player) {
    int potionOffset = 0;
    MobEffectInstance potion = player.getEffect(MobEffects.WITHER);
    if (potion != null) {
      potionOffset = 18;
    }
    potion = player.getEffect(MobEffects.POISON);
    if (potion != null) {
      potionOffset = 9;
    }
    assert this.mc.level != null;
    if (this.mc.level.getLevelData().isHardcore()) {
      potionOffset += 27;
    }
    return potionOffset;
  }

  /**
   * Renders the health above 10 hearts
   * @param guiGraphics  Gui graphics instance
   * @param xBasePos     Health bar top corner
   * @param yBasePos     Health bar top corner
   * @param player       Player instance
   */
  private void renderExtraHearts(GuiGraphics guiGraphics, int xBasePos, int yBasePos, Player player) {
    int potionOffset = this.getPotionOffset(player);

    // Extra hearts
    int hp = Mth.ceil(player.getHealth());
    this.renderCustomHearts(guiGraphics, ICON_HEARTS, xBasePos, yBasePos, potionOffset, hp, false);
  }

  /**
   * Renders the absorption health above 10 hearts
   * @param guiGraphics  Gui graphics instance
   * @param xBasePos     Health bar top corner
   * @param yBasePos     Health bar top corner
   * @param player       Player instance
   */
  private void renderExtraAbsorption(GuiGraphics guiGraphics, int xBasePos, int yBasePos, Player player) {
    int potionOffset = this.getPotionOffset(player);

    // Extra hearts
    int absorb = Mth.ceil(player.getAbsorptionAmount());
    this.renderCustomHearts(guiGraphics, ICON_ABSORB, xBasePos, yBasePos, potionOffset, absorb, true);
  }

  /**
   * Gets the texture offset from the regen effect
   * @param i       Heart index
   * @param offset  Current offset
   */
  private int getYRegenOffset(int i, int offset) {
    return i + offset == this.regen ? -2 : 0;
  }

  /**
   * Shared logic to render custom hearts
   * @param guiGraphics  Gui graphics instance
   * @param texture      Texture to draw
   * @param xBasePos     Health bar top corner
   * @param yBasePos     Health bar top corner
   * @param potionOffset Offset from the potion effect
   * @param count        Number to render
   * @param absorb       If true, render absorption hearts
   */
  private void renderCustomHearts(GuiGraphics guiGraphics, ResourceLocation texture, int xBasePos, int yBasePos, int potionOffset, int count, boolean absorb) {
    int regenOffset = absorb ? 10 : 0;
    for (int iter = 0; iter < count / 20; iter++) {
      int renderHearts = (count - 20 * (iter + 1)) / 2;
      int heartIndex = iter % 11;
      if (renderHearts > 10) {
        renderHearts = 10;
      }
      for (int i = 0; i < renderHearts; i++) {
        int y = this.getYRegenOffset(i, regenOffset);
        if (absorb) {
          this.blit(guiGraphics, texture, xBasePos + 8 * i, yBasePos + y, 0, 54, 9, 9);
        }
        this.blit(guiGraphics, texture, xBasePos + 8 * i, yBasePos + y, 18 * heartIndex, potionOffset, 9, 9);
      }
      if (count % 2 == 1 && renderHearts < 10) {
        int y = this.getYRegenOffset(renderHearts, regenOffset);
        if (absorb) {
          this.blit(guiGraphics, texture, xBasePos + 8 * renderHearts, yBasePos + y, 0, 54, 9, 9);
        }
        this.blit(guiGraphics, texture, xBasePos + 8 * renderHearts, yBasePos + y, 9 + 18 * heartIndex, potionOffset, 9, 9);
      }
    }
  }
}
