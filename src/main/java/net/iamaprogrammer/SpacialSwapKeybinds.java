package net.iamaprogrammer;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

public class SpacialSwapKeybinds {
    public static KeyBinding SPACIAL_SWAP = new KeyBinding("spacial-swap.keybind.swap", GLFW.GLFW_KEY_R, "category.spacial-swap");

    public static void register() {
        KeyBindingHelper.registerKeyBinding(SPACIAL_SWAP);
    }
}
