package t.me.p1azmer.engine.api.server;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Groups {

    private final String luckPermsID;
    private final String name;
    private final int weight;
    private final String ru_displayName;
    private final String eng_displayName;

    private final String groupColor;
    private final String nameColor;
    private final String messageColor;

    private final String rgb_start;
    private final String rgb_middle;
    private final String rgb_end;

    public String getDisplayName(boolean ru){
        return ru ? ru_displayName : eng_displayName;
    }

    public String getGroupPrefix(boolean ru){
        return getGroupColor() + getDisplayName(ru) + nameColor + " ";
    }

    public String getGroupPrefixWithoutColor(boolean ru){
        return getDisplayName(ru)  + " ";
    }
}
