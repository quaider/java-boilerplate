package cn.kankancloud.jbp.mbp.audit;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import cn.kankancloud.jbp.core.principal.PrincipalFactory;
import cn.kankancloud.jbp.core.principal.SupportedPrincipalFactories;
import cn.kankancloud.jbp.core.principal.UserPrincipal;
import org.apache.ibatis.reflection.MetaObject;

import java.util.Date;

public class AuditMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        Date now = new Date();
        this.fillUser(metaObject, true);
        this.strictInsertFill(metaObject, "createTime", () -> now, Date.class);
        this.strictUpdateFill(metaObject, "updateTime", () -> now, Date.class);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.fillUser(metaObject, false);
        this.strictUpdateFill(metaObject, "updateTime", Date::new, Date.class);
    }

    private void fillUser(MetaObject metaObject, boolean insert) {
        UserPrincipal current;

        for (PrincipalFactory factory : SupportedPrincipalFactories.getPrincipalFactories()) {

            if (!factory.supported() || (current = factory.getCurrent()) == null) {
                continue;
            }

            if (insert) {
                this.strictInsertFill(metaObject, "updateUser", current::getFullName, String.class);
                this.strictInsertFill(metaObject, "createUser", current::getFullName, String.class);
            } else {
                this.strictUpdateFill(metaObject, "updateUser", current::getFullName, String.class);
            }
        }
    }
}
