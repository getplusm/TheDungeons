package t.me.p1azmer.engine.api.placeholder;

import org.jetbrains.annotations.NotNull;

import java.util.function.UnaryOperator;

public interface IPlaceholder {

    @NotNull PlaceholderMap getPlaceholders();

    @NotNull
    default UnaryOperator<String> replacePlaceholders() {
        return this.getPlaceholders().replacer();
    }
}