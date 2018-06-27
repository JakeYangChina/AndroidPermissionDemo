package jake.yang.permission.library.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jake.yang.permission.library.utils.PermissionUtils;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RequestPermissionDenied {
    int requestCode() default PermissionUtils.DEFAULT_REQUEST_CODE;
}
