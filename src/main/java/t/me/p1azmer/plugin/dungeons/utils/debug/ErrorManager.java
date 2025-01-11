package t.me.p1azmer.plugin.dungeons.utils.debug;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ErrorManager {
    List<String> errors = new ArrayList<>();

    public void addError(@NotNull String error) {
        errors.add("[" + LocalDateTime.now() + "] " + error);
    }

    public String getErrors() {
        if (errors.isEmpty()) return "Empty";

        return String.join("\n", ErrorManager.errors);
    }

    public void clear() {
        errors.clear();
    }
}
