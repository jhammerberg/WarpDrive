package li.cil.oc.api.machine;

import java.util.Map;
import net.minecraft.item.ItemStack;

public interface Arguments extends Iterable<Object> {
	int count();
	
	Object checkAny(int var1);
	
	boolean checkBoolean(int var1);
	
	int checkInteger(int var1);
	
	double checkDouble(int var1);
	
	String checkString(int var1);
	
	byte[] checkByteArray(int var1);
	
	Map checkTable(int var1);
	
	ItemStack checkItemStack(int var1);
	
	Object optAny(int var1, Object var2);
	
	boolean optBoolean(int var1, boolean var2);
	
	int optInteger(int var1, int var2);
	
	double optDouble(int var1, double var2);
	
	String optString(int var1, String var2);
	
	byte[] optByteArray(int var1, byte[] var2);
	
	Map optTable(int var1, Map var2);
	
	ItemStack optItemStack(int var1, ItemStack var2);
	
	boolean isBoolean(int var1);
	
	boolean isInteger(int var1);
	
	boolean isDouble(int var1);
	
	boolean isString(int var1);
	
	boolean isByteArray(int var1);
	
	boolean isTable(int var1);
	
	boolean isItemStack(int var1);
	
	Object[] toArray();
}
