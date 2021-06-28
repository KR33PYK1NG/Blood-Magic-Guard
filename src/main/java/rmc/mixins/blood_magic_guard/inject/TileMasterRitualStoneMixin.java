package rmc.mixins.blood_magic_guard.inject;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import rmc.libs.event_factory.EventFactory;
import rmc.libs.tile_ownership.TileOwnership;
import rmc.mixins.blood_magic_guard.BloodMagicGuard;
import wayoftime.bloodmagic.ritual.AreaDescriptor;
import wayoftime.bloodmagic.ritual.Ritual;
import wayoftime.bloodmagic.tile.TileMasterRitualStone;
import wayoftime.bloodmagic.util.helper.RitualHelper;

/**
 * Developed by RMC Team, 2021
 * @author KR33PY
 */
@Mixin(value = TileMasterRitualStone.class)
public abstract class TileMasterRitualStoneMixin {

    @Redirect(method = "Lwayoftime/bloodmagic/tile/TileMasterRitualStone;performRitual(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V",
              remap = false,
              at = @At(value = "INVOKE",
                       target = "Lwayoftime/bloodmagic/util/helper/RitualHelper;checkValidRitual(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lwayoftime/bloodmagic/ritual/Ritual;Lnet/minecraft/util/Direction;)Z"))
    private boolean guardRitualStart(World world, BlockPos pos, Ritual ritual, Direction direction) {
        if (RitualHelper.checkValidRitual(world, pos, ritual, direction)) {
            for (AreaDescriptor area : ritual.getModableRangeMap().values()) {
                for (BlockPos bpos : area.getContainedPositions(pos)) {
                    if (!EventFactory.testBlockBreak(EventFactory.convertFake(world, TileOwnership.loadOwner(((TileMasterRitualStone)(Object) this).getTileData())), world, bpos, BloodMagicGuard.RITUAL_FAKE)) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

}