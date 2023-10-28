package cn.kankancloud.jbp.mbp.persistence;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.kankancloud.jbp.core.PagedData;
import cn.kankancloud.jbp.core.query.PageQuery;
import cn.kankancloud.jbp.mbp.query.QueryUtil;
import org.springframework.validation.annotation.Validated;

@Validated
public class BaseServiceImpl<M extends BaseMapper<T>, T extends BasePo> extends ServiceImpl<M, T> implements BaseService<T> {

    @Override
    public <E extends IPage<T>> PagedData<T> pageQuery(E page, Wrapper<T> queryWrapper) {
        E mbpPage = page(page, queryWrapper);

        return new PagedData<>(mbpPage.getRecords(), mbpPage.getTotal(), mbpPage.getCurrent());
    }

    @Override
    public PagedData<T> pageQuery(Class<T> clazz, PageQuery pageQuery) {
        return pageQuery(QueryUtil.buildPage(pageQuery), QueryUtil.createQuery(clazz, pageQuery));
    }
}
