package cn.kankancloud.jbp.core.secure;

import lombok.Data;

import java.util.List;

@Data
public class UserDetail {
    private String identity;
    private String account;
    private String fullname;
    private List<String> departments;

    public UserDetail() {

    }

    public UserDetail(String identity, String account, String fullname) {
        this.identity = identity;
        this.account = account;
        this.fullname = fullname;
    }
}
