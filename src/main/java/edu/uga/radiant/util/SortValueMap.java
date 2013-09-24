package edu.uga.radiant.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@SuppressWarnings("rawtypes")
/**
 * The Map which is sorted by the value
 */
public class SortValueMap <K, V> implements Map<K, V> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** the source data map
	 */ 
	public HashMap<K, V> base = new HashMap<K, V>();
	
	/** store equal value 
	 */
	public TreeMap<V, ArrayList<K>> equal = new TreeMap<V, ArrayList<K>>();
    
	class ValueComparator implements Comparator {
		public int compare(Object a, Object b) {
			try{
				double valueA = Double.valueOf(a.toString());
				double valueB = Double.valueOf(b.toString());
				if (valueA < valueB) {
					return 1;
				} else if (valueA == valueB) {
					return 0;
				} else {
					return -1;
				}
			}catch (Exception e){
				String strA = a.toString();
				String strB = b.toString();
				return strA.compareTo(strB);
			}			
		}
	}
	
	public void clear() {
		base.clear();
	}
	
	public boolean containsKey(Object key) {
		return base.containsKey(key);
	}
	
	public boolean containsValue(Object value) {
		return base.containsValue(value);
	}
	
	public Set<Entry<K, V>> entrySet() {
		return base.entrySet();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Set<K> keySet() {
		LinkedHashSet<K> KeySet = new LinkedHashSet<K>();
		ArrayList<K> Keys = null;
		ArrayList<V> Values = new ArrayList<V>();
		Values.addAll(equal.keySet());
		ValueComparator compare = new ValueComparator();
		Collections.sort(Values, compare);
		for (V value : Values){
			Keys = equal.get(value);
			for (K key : Keys){
				KeySet.add(key);
			}
		}
		return KeySet;
	}
	
	public V get(Object key) {
		return base.get(key);
	}
	
	public boolean isEmpty() {
		return base.isEmpty();
	}
	
	public V put(K key, V value) {
		
		ArrayList<K> Keys = null;
		
		if (keySet().contains(key)){
			equal.get(base.get(key)).remove(key);
		}
		
		if (equal.containsKey(value)){
			Keys = equal.get(value);
			Keys.add(key);
		}else{
			Keys = new ArrayList<K>();
			Keys.add(key);
		}
		
		equal.put(value, Keys);
		base.put(key, value);
		
		return base.put(key, value);
	}
	
	public void putAll(Map<? extends K, ? extends V> m) {
		for (K key : m.keySet()){
			put(key, m.get(key));
		}
	}
	
	public V remove(Object key) {
		return base.remove(key);
	}
	
	public int size() {
		return base.size();
	}
	
	public Collection<V> values() {
		return base.values();
	}
	
	public static void main(String args[]) {
		
		SortValueMap<String, Double> score = new SortValueMap<String, Double>();
		
		score.put("b", 3.0D);
		score.put("a", 4.0D);
		score.put("c", 1.0D);
		score.put("e", 6.0D);
		score.put("a", 7.0D);
		score.put("b", 3.0D);
		score.put("d", 3.0D);
		score.put("e", 7.0D);

		System.out.println("size = " + score.size());
		
		for (Object key : score.keySet()){
			System.out.println("key = " + key + ", value = " + score.get(key));
		}
		
		SortValueMap<String, String> test = new SortValueMap<String, String>();
		
		test.put("a", "dsfsd");
		test.put("b", "sdag");
		test.put("c", "asg");
		test.put("d", "hj");
		test.put("e", "e5yh");
		test.put("e", "sdjj");
		
		System.out.println("size = " + test.size());
		
		for (Object key : test.keySet()){
			System.out.println("key = " + key + ", value = " + test.get(key));
		}
		
	}

}
