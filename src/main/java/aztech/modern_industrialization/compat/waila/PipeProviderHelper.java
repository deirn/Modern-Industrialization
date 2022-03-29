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

import aztech.modern_industrialization.pipes.api.PipeNetworkNode;
import aztech.modern_industrialization.pipes.impl.PipeBlockEntity;
import aztech.modern_industrialization.pipes.impl.PipeVoxelShape;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class PipeProviderHelper {
    private static final Map<Player, HitNode> HIT_CACHE = new HashMap<>();

    public static @Nullable PipeNetworkNode getHitNode(PipeBlockEntity pipe, Player player, HitResult hitResult) {
        HitNode hitNode = HIT_CACHE.computeIfAbsent(player, p -> new HitNode());
        Vec3 hitPos = hitResult.getLocation();
        if (!hitPos.equals(hitNode.pos) || !pipe.doesNodeStillExist(hitNode.node)) {
            hitNode.pos = hitPos;
            hitNode.node = null;
            BlockPos blockPos = pipe.getBlockPos();

            for (PipeVoxelShape partShape : pipe.getPartShapes()) {
                Vec3 posInBlock = hitPos.subtract(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                for (AABB box : partShape.shape.toAabbs()) {
                    // move slightly towards box center
                    Vec3 dir = box.getCenter().subtract(posInBlock).normalize().scale(1e-4);
                    if (box.contains(posInBlock.add(dir))) {
                        for (PipeNetworkNode node : pipe.getNodes()) {
                            if (node.getType() == partShape.type) {
                                hitNode.node = node;
                                return node;
                            }
                        }
                    }
                }
            }
        }

        return hitNode.node;
    }

    private static class HitNode {
        @Nullable Vec3 pos;
        @Nullable PipeNetworkNode node;
    }
}
