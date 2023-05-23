package nl.brandonvdongen.cactuscrafts.helpers;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public class NbtHelper {

    private CompoundTag _NBT;
    private final String namespace;
    public NbtHelper(String namespace){
        this.namespace = namespace;
    }

    public NbtHelper(String namespace, CompoundTag nbt){
        this.namespace = namespace;
        this._NBT = nbt;
    }

    public NbtHelper SanitizeEntityNBT(){
        AttemptToRemove(_NBT,"Motion");
        AttemptToRemove(_NBT,"Sitting");
        AttemptToRemove(_NBT,"UUID");
        AttemptToRemove(_NBT,"HurtByTimestamp");
        AttemptToRemove(_NBT,"HurtTime");
        AttemptToRemove(_NBT,"InLove");
        AttemptToRemove(_NBT,"Rotation");
        AttemptToRemove(_NBT,"PortalCooldown");

        return this;
    }

    public NbtHelper set(String key, Tag tag){
        _NBT.put(namespace+"."+key, tag);
        return this;
    }

    public NbtHelper set(String key, Float number){
        _NBT.putFloat(namespace+"."+key, number);
        return this;
    }

    public NbtHelper set(String key, Integer number){
        _NBT.putInt(namespace+"."+key, number);
        return this;
    }

    public NbtHelper set(String key, String text){
        _NBT.putString(namespace+"."+key, text);
        return this;
    }

    public CompoundTag getCompound(String key){
        return _NBT.getCompound(namespace+"."+key);
    }

    public Float getFloat(String key){
        return _NBT.getFloat(namespace+"."+key);

    }

    public Integer getInt(String key){
        return _NBT.getInt(namespace+"."+key);
    }

    public String getString(String key){
        return _NBT.getString(namespace+"."+key);

    }


    /**
     * Returns the internal Tag
     * @return CompoundTag
     */
    public CompoundTag get(){
        return _NBT;
    }

    private static void AttemptToRemove(CompoundTag nbt, String name){
        if(nbt.contains(name))nbt.remove(name);
    }
}
