/*
 * MIT License
 *
 * Copyright (c) 2020 Azercoco & Technici4n
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package aztech.modern_industrialization.compat.waila;

import aztech.modern_industrialization.pipes.MIPipes;
import aztech.modern_industrialization.pipes.api.PipeNetworkType;
import aztech.modern_industrialization.util.FluidHelper;
import aztech.modern_industrialization.util.NbtHelper;
import mcp.mobius.waila.api.*;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

/**
 * Overrides the name of the pipes in Waila to prevent
 * "block.modern_industrialization.pipe" being displayed.
 */
public class PipeComponentProvider implements IBlockComponentProvider {
    private static final boolean HAS_MEGANE = FabricLoader.getInstance().isModLoaded("megane-runtime");

    @Override
    public void appendHead(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        CompoundTag data = accessor.getServerData();
        if (data.contains("type")) {
            PipeNetworkType type = PipeNetworkType.get(new ResourceLocation(data.getString("type")));
            Component text = IWailaConfig.get().getFormatter().blockName(I18n.get(MIPipes.INSTANCE.getPipeItem(type).getDescriptionId()));
            tooltip.setLine(WailaConstants.OBJECT_NAME_TAG, text);
        }
    }

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        CompoundTag data = accessor.getServerData();
        if (!HAS_MEGANE && data.contains("fluid")) {
            FluidVariant fluid = NbtHelper.getFluidCompatible(data, "fluid");
            long amount = data.getLong("amount");
            int capacity = data.getInt("capacity");
            FluidHelper.getTooltipForFluidStorage(fluid, amount, capacity).forEach(tooltip::addLine);
        }

        if (data.contains("eu")) {
            String tier = data.getString("tier");
            tooltip.addLine(new TranslatableComponent("text.modern_industrialization.cable_tier_" + tier));

            if (!HAS_MEGANE) {
                long eu = data.getLong("eu");
                long maxEu = data.getLong("maxEu");
                tooltip.addLine(new TranslatableComponent("text.modern_industrialization.energy_bar", eu, maxEu));
            }
        }
    }
}
