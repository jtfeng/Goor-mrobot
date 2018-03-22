package cn.mrobot.utils;

import java.util.*;

/**
 * @author Jelynn
 * @date 2018/3/22
 */
public class MapUtils {

    /**
     * 使用 Map按value进行降序排序
     *
     * @param map
     * @return
     */
    public static Map<Object, Integer> sortMapByValue(Map<? extends Object, Integer> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        Map<Object, Integer> sortedMap = new LinkedHashMap();
        List<Map.Entry<? extends Object, Integer>> entryList = new ArrayList<>(map.entrySet());
        Collections.sort(entryList, new Comparator<Map.Entry<? extends Object, Integer>>(){
            @Override
            public int compare(Map.Entry<? extends Object, Integer> me1, Map.Entry<? extends Object, Integer> me2) {
                return me2.getValue()- me1.getValue();
            }
        });

        Iterator<Map.Entry<? extends Object, Integer>> iter = entryList.iterator();
        Map.Entry<? extends Object, Integer> tmpEntry = null;
        while (iter.hasNext()) {
            tmpEntry = iter.next();
            sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
        }
        return sortedMap;
    }
}
