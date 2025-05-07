package com.malayrental.malayrentalserver.common;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

public class IdGeneratorUtil {
    public static <T> String generateId(BaseMapper<T> mapper, String idField, String prefix) {
        QueryWrapper<T> wrapper = new QueryWrapper<>();
        wrapper.select(idField);
        List<T> list = mapper.selectList(wrapper);
        if (list == null || list.isEmpty()) {
            return prefix + "001";
        }
        int max = 0;
        for (T item : list) {
            try {
                String getter = "get" + toCamelCase(idField);
                String id = (String) item.getClass().getMethod(getter).invoke(item);
                if (id != null && id.startsWith(prefix)) {
                    int num = Integer.parseInt(id.replace(prefix, ""));
                    if (num > max) max = num;
                }
            } catch (Exception ignored) {}
        }
        int next = max + 1;
        return prefix + String.format("%03d", next);
    }

    private static String toCamelCase(String str) {
        StringBuilder sb = new StringBuilder();
        boolean upper = true;
        for (char c : str.toCharArray()) {
            if (c == '_') {
                upper = true;
            } else if (upper) {
                sb.append(Character.toUpperCase(c));
                upper = false;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}