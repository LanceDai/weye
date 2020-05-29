package cn.lancedai.weye.server.VO;

import cn.lancedai.weye.common.model.CustomCommand;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscribeCommandInfo {
    private int subscribeId;
    private int customCommandId;
    private String name;
    private String command;
    private String duration;

    public SubscribeCommandInfo(int subscribeId, CustomCommand command) {
        this.subscribeId = subscribeId;
        this.customCommandId = command.getId();
        this.name = command.getName();
        this.command = command.getCommand();
        this.duration = command.getDuration();
    }
}
