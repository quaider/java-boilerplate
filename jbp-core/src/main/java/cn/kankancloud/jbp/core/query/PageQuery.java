package cn.kankancloud.jbp.core.query;

import cn.kankancloud.jbp.core.util.CastUtil;
import cn.kankancloud.jbp.core.util.StrUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
@Tag(name = "分页通用查询实体")
public class PageQuery {

    @Schema(description = "页码")
    private Integer current = 1;

    @Schema(description = "页大小")
    private Integer pageSize = 10;

    @Schema(description = "查询条件", example = """
            {
                    "conditions": [
                        {
                            "operator": "EQ",
                            "field": "fullname",
                            "value": "张奎"
                        }
                    ]
                }""")
    private QueryConditions filter;

    @Schema(description = "排序", example = "字段名1 desc,字段名2 asc 或者 array ['字段名1 desc','字段名2 asc']")
    private Object sort;

    @SuppressWarnings("unchecked")
    public Map<String, String> getSorts() {
        Map<String, String> map = new LinkedHashMap<>(4);
        if (sort == null) {
            return map;
        }

        String[] sortArr = new String[0];

        if (sort instanceof CharSequence) {
            String sortStr = CastUtil.toStr(sort);
            if (StrUtil.isEmpty(sortStr)) {
                return map;
            }

            sortArr = sortStr.split(",");
        } else if (sort.getClass().isArray()) {
            sortArr = new String[Array.getLength(sort)];
            for (int i = 0; i < sortArr.length; i++) {
                sortArr[i] = Array.get(sort, i) + "";
            }
        } else if (sort instanceof Collection) {
            sortArr = ((Collection<String>) this.sort).toArray(new String[0]);
        }

        for (String sortDirStr : sortArr) {
            String[] sortDir = StringUtils.trim(sortDirStr.replaceAll("\\s+", " ")).split(" ");
            if (ObjectUtils.isEmpty(sortDir)) {
                continue;
            }

            String field = StringUtils.trim(sortDir[0]);
            String dir = "ASC";
            if (sortDir.length > 1 && StringUtils.trim(sortDir[1]).equalsIgnoreCase("DESC")) {
                dir = "DESC";
            }

            map.put(field, dir);
        }

        return map;
    }
}
