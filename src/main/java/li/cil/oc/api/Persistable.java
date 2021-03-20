package li.cil.oc.api;

import net.minecraft.nbt.CompoundNBT;

public interface Persistable {
    void load(CompoundNBT var1);

    void save(CompoundNBT var1);
}