package cr0s.warpdrive.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.TickableSound;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SirenSound extends TickableSound {
    
    float range;
    final BlockPos blockPosSource;
    
    /**x, y and z are the position of the tile entity. the actual sound is broadcast from
       xPosF, yPosF, zPosF, which is the location of the player.
       The volume is adjusted according to the distance to x, y, z.
       Why? Because Minecraft's sound system is complete and utter shit, and this
       is the easiest way which:
       1. Produces a sound audible from a specifiable range.
       2. Produces a sound which decreases in volume the farther you get away from it.
       3. Doesn't keep playing for you once you're half the world away.
       4. Doesn't completely spazz out the instant you try to actually use it.*/
    public SirenSound(final SoundEvent soundEvent, final float range, final BlockPos blockPosSource) {
        super(soundEvent, SoundCategory.AMBIENT);
        
        this.range = range;
        
        this.blockPosSource = blockPosSource;
        this.x = blockPosSource.getX() + 0.5F;
        this.y = blockPosSource.getY() + 0.5F;
        this.z = blockPosSource.getZ() + 0.5F;
    }
    
    @Override
    public void tick() {
        final PlayerEntity player = Minecraft.getInstance().player;
        assert player != null;
        
        this.x = (float) player.getPosX();
        this.y = (float) player.getPosY();
        this.z = (float) player.getPosZ();
        
        final float distance = (float) Math.sqrt(player.getDistanceSq(blockPosSource.getX() + 0.5D,
                                                                      blockPosSource.getY() + 0.5D,
                                                                      blockPosSource.getZ() + 0.5D ));
        if (distance > range * range) {
            this.volume = 0.0F;
        } else {
            // @TODO: Better distance/volume formula that has a better drop off rate.
            this.volume = 1.0F - scaleTo(distance, 0.0F, range, 0.0F, 1.0F);
        }
    }
    
    private float scaleTo(final float num, final float oldMin, final float oldMax, final float newMin, final float newMax) {
        return ((newMax - newMin)*(num - oldMin)) / (oldMax - oldMin) + newMin;
    }
}
