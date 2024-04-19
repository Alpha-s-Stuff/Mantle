package slimeknights.mantle.client.screen.book.element;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import slimeknights.mantle.client.screen.book.BookScreen;

import java.util.List;
import java.util.Optional;

public abstract class BookElement {

  public BookScreen parent;

  protected Minecraft mc = Minecraft.getInstance();
  protected TextureManager renderEngine = this.mc.getTextureManager();

  public int x, y;

  public BookElement(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public abstract void draw(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks, Font fontRenderer);

  public void drawOverlay(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks, Font fontRenderer) {
  }

  public void mouseClicked(double mouseX, double mouseY, int mouseButton) {

  }

  public void mouseReleased(double mouseX, double mouseY, int clickedMouseButton) {

  }

  public void mouseDragged(double clickX, double clickY, double mx, double my, double lastX, double lastY, int button) {

  }

  public void renderToolTip(GuiGraphics guiGraphics, Font fontRenderer, ItemStack stack, int x, int y) {
    List<Component> list = stack.getTooltipLines(this.mc.player, this.mc.options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL);

    Font font = mc.gui.getFont();
    if (font == null) {
      font = fontRenderer;
    }

    this.drawTooltip(guiGraphics, list, x, y, font);
  }

  public void drawTooltip(GuiGraphics guiGraphics, List<Component> textLines, int x, int y, Font font) {
    // GuiUtils.drawHoveringText(matrixStack, textLines, x, y, this.parent.width, this.parent.height, -1, font);
    // GuiUtils.drawHoveringText(matrixStack, textLines, x, y, BookScreen.PAGE_WIDTH, BookScreen.PAGE_HEIGHT, BookScreen.PAGE_WIDTH, font);
    int oldWidth = parent.width;
    int oldHeight = parent.height;
    parent.width = BookScreen.PAGE_WIDTH;
    parent.height = BookScreen.PAGE_HEIGHT;
    guiGraphics.renderTooltip(font, textLines, Optional.empty(), x, y);
    parent.width = oldWidth;
    parent.height = oldHeight;
  }
}
