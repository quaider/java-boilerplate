package cn.kankancloud.jbp.web.validation;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class FieldValidationError implements Serializable {
    private static final long serialVersionUID = 2468331074027424620L;
    private String field;
    private String error;
}
