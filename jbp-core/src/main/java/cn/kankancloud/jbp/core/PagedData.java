package cn.kankancloud.jbp.core;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Tag(name = "分页数据载体")
public class PagedData<T> {

    @Schema(description = "分页数据")
    private List<T> records;

    @Schema(description = "总记录行数")
    private long total = 0;

    @Schema(description = "当前页")
    private long current = 0;
}
