package net.minecraft.src;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class LowerStringMap implements Map {
	private final Map internalMap = new LinkedHashMap();

	public int size() {
		return this.internalMap.size();
	}

	public boolean isEmpty() {
		return this.internalMap.isEmpty();
	}

	public boolean containsKey(Object var1) {
		return this.internalMap.containsKey(var1.toString().toLowerCase());
	}

	public boolean containsValue(Object var1) {
		return this.internalMap.containsKey(var1);
	}

	public Object get(Object var1) {
		return this.internalMap.get(var1.toString().toLowerCase());
	}

	public Object putLower(String var1, Object var2) {
		return this.internalMap.put(var1.toLowerCase(), var2);
	}

	public Object remove(Object var1) {
		return this.internalMap.remove(var1.toString().toLowerCase());
	}

	public void putAll(Map var1) {
		Iterator var2 = var1.entrySet().iterator();

		while(var2.hasNext()) {
			Entry var3 = (Entry)var2.next();
			this.putLower((String)var3.getKey(), var3.getValue());
		}

	}

	public void clear() {
		this.internalMap.clear();
	}

	public Set keySet() {
		return this.internalMap.keySet();
	}

	public Collection values() {
		return this.internalMap.values();
	}

	public Set entrySet() {
		return this.internalMap.entrySet();
	}

	public Object put(Object var1, Object var2) {
		return this.putLower((String)var1, var2);
	}
}
