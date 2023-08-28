package cn.kankancloud.jbp.core.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Map;

@Slf4j
public class JsonUtil {
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String STANDARD_FORMAT = "yyyy-MM-dd HH:mm:ss";

    static {
        // 对象的所有字段全部列入
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        // 取消默认转换timestamps形式
        OBJECT_MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        // 忽略空Bean转json的错误
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        // 所有的日期格式都统一为以下的样式，即yyyy-MM-dd HH:mm:ss
        OBJECT_MAPPER.setDateFormat(new SimpleDateFormat(STANDARD_FORMAT));
        // 忽略 在json字符串中存在，但是在java对象中不存在对应属性的情况。防止错误
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private JsonUtil() {
    }

    /**
     * 对象转Json格式字符串
     *
     * @param obj 对象
     * @return Json格式字符串
     */
    public static <T> String toJson(T obj) {
        if (obj == null) {
            return null;
        }

        try {
            return obj instanceof String ? (String) obj : OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.warn("Parse Object to String error: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 对象转Json格式字符串(格式化的Json字符串)
     *
     * @param obj 对象
     * @return 美化的Json格式字符串
     */
    public static <T> String toJsonPretty(T obj) {
        if (obj == null) {
            return null;
        }

        try {
            return obj instanceof String ? (String) obj : OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.warn("Parse Object to String error: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 字符串转换为自定义对象
     *
     * @param json  要转换的字符串
     * @param clazz 自定义对象的class对象
     * @return 自定义对象
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        if (StringUtils.isEmpty(json) || clazz == null) {
            return null;
        }

        try {
            return clazz.equals(String.class) ? (T) json : OBJECT_MAPPER.readValue(json, clazz);
        } catch (Exception e) {
            log.warn("Parse String to Object error: {}", e.getMessage());
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        if (StringUtils.isEmpty(json) || typeReference == null) {
            return null;
        }

        try {
            return (T) (typeReference.getType().equals(String.class) ? json : OBJECT_MAPPER.readValue(json, typeReference));
        } catch (IOException e) {
            log.warn("Parse String to Object error", e);
            return null;
        }
    }

    /**
     * 将json字符串转换为集合
     * <p>
     * List<User> userListBean2 = JsonUtil.fromJson(userListJson, List.class, User.class);
     * </p>
     *
     * @param json            json字符串
     * @param collectionClazz 集合类型
     * @param elementClasses  集合元素类型
     * @param <T>             元素类型参数
     * @return 集合对象
     */
    public static <T> T fromJson(String json, Class<?> collectionClazz, Class<?>... elementClasses) {
        JavaType javaType = OBJECT_MAPPER.getTypeFactory().constructParametricType(collectionClazz, elementClasses);
        try {
            return OBJECT_MAPPER.readValue(json, javaType);
        } catch (IOException e) {
            log.warn("Parse String to Object error : {}" + e.getMessage());
            return null;
        }
    }

    public static Map<String, Object> mapFromJson(String json) {
        try {
            return OBJECT_MAPPER.readValue(json, new TypeReference<Map<String, Object>>() {
            });
        } catch (JsonProcessingException e) {
            log.warn("Parse String to MAP error : {}" + e.getMessage());
            return Collections.emptyMap();
        }
    }
}
