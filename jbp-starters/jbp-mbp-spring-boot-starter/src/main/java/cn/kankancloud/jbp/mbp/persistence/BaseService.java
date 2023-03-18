package cn.kankancloud.jbp.mbp.persistence;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.kankancloud.jbp.core.PagedData;
import cn.kankancloud.jbp.core.query.PageQuery;

/**
 * mbp service基类
 *
 * @param <T> PO类型
 */
public interface BaseService<T extends BasePo> extends IService<T> {

    PagedData<T> pageQuery(Class<T> clazz, PageQuery pageQuery);

    <E extends IPage<T>> PagedData<T> pageQuery(E page, Wrapper<T> queryWrapper);

}
