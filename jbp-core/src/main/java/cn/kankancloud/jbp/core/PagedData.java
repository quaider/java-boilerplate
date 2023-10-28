package cn.kankancloud.jbp.core;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Schema(description = "分页数据载体")
public class PagedData<T> {

    @Schema(description = "分页数据")
    private List<T> records;

    @Schema(description = "总记录行数")
    private long total = 0;

    @Schema(description = "当前页")
    private long current = 0;
}
