package t.me.p1azmer.plugin.dungeons.utils;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import java.util.List;

public class ItemReader {

    @NotNull
    public static String write(@NotNull ItemStack item){
        return write(List.of(item));
    }

    @NotNull
    public static String write(@NotNull List<ItemStack> items) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            String var10;
            try {
                BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

                try {
                    dataOutput.writeInt(items.size());
                    for (ItemStack item : items) {
                        dataOutput.writeObject(item);
                    }

                    var10 = Base64Coder.encodeLines(outputStream.toByteArray());
                } catch (Throwable var7) {
                    try {
                        dataOutput.close();
                    } catch (Throwable var6) {
                        var7.addSuppressed(var6);
                    }

                    throw var7;
                }

                dataOutput.close();
            } catch (Throwable var8) {
                try {
                    outputStream.close();
                } catch (Throwable var5) {
                    var8.addSuppressed(var5);
                }

                throw var8;
            }

            outputStream.close();
            return var10;
        } catch (Exception var9) {
            return "";
        }
    }

    @NotNull
    public static ItemStack[] read(@NotNull String source) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(source));

            ItemStack[] var10;
            try {
                BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);

                try {
                    ItemStack[] items = new ItemStack[dataInput.readInt()];

                    for(int i = 0; i < items.length; ++i) {
                        items[i] = (ItemStack)dataInput.readObject();
                    }

                    var10 = items;
                } catch (Throwable var7) {
                    try {
                        dataInput.close();
                    } catch (Throwable var6) {
                        var7.addSuppressed(var6);
                    }

                    throw var7;
                }

                dataInput.close();
            } catch (Throwable var8) {
                try {
                    inputStream.close();
                } catch (Throwable var5) {
                    var8.addSuppressed(var5);
                }

                throw var8;
            }

            inputStream.close();
            return var10;
        } catch (Exception var9) {
            return new ItemStack[0];
        }
    }
}
