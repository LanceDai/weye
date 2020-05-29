package cn.lancedai.weye.common.model.rule;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 60384
 */
@Data
public abstract class BaseRule implements Serializable {

    // 是挂载在WebApp上的
    protected int webAppId;
}
