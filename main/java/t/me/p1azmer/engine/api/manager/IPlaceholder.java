package t.me.p1azmer.engine.api.manager;

import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

@Deprecated
public interface IPlaceholder {

    String DELIMITER_DEFAULT = "\n" + ChatColor.GREEN;
    Pattern PERCENT_PATTERN   = Pattern.compile("%([^%]+)%");

    @NotNull UnaryOperator<String> replacePlaceholders();
}