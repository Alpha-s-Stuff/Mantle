package slimeknights.mantle.lib.util;

import slimeknights.mantle.lib.util.Constants.Crafting;

public class ShapedRecipeUtil {
	public static void setCraftingSize(int width, int height) {
		Crafting.HEIGHT = height;
		Crafting.WIDTH = width;
	}
}
