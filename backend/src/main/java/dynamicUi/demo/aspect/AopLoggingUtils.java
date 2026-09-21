package dynamicUi.demo.aspect;

import java.lang.reflect.Field;

/**
 * Shared, defensive argument-formatting logic for logging aspects.
 * Never throws — logging must never be able to break a real request.
 */
final class AopLoggingUtils {

    private AopLoggingUtils() {}

    static String redact(Object arg) {
        if (arg == null) return "null";

        String typeName = arg.getClass().getName();
        if (typeName.startsWith("jakarta.servlet") || typeName.startsWith("org.springframework")) {
            return arg.getClass().getSimpleName();
        }

        try {
            StringBuilder sb = new StringBuilder(arg.getClass().getSimpleName()).append("(");
            Field[] fields = arg.getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                field.setAccessible(true);
                String name = field.getName();
                Object value;
                try {
                    value = field.get(arg);
                } catch (Exception e) {
                    value = "?";
                }

                boolean sensitive = name.toLowerCase().contains("password")
                        || name.toLowerCase().contains("token")
                        || name.toLowerCase().contains("secret");

                sb.append(name).append("=").append(sensitive ? "***REDACTED***" : value);
                if (i < fields.length - 1) sb.append(", ");
            }
            return sb.append(")").toString();
        } catch (Exception e) {
            return arg.getClass().getSimpleName() + "[unavailable]";
        }
    }

    static String redactedArgs(Object[] args) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < args.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(redact(args[i]));
        }
        return sb.append("]").toString();
    }
}