package com.android.bottombar.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * author:  YJZ
 * date: 2017/12/11
 * description：选中图标注解
 */

@Retention (RetentionPolicy.RUNTIME)
@Target (ElementType.FIELD)
public @interface SelectIcons {
}
