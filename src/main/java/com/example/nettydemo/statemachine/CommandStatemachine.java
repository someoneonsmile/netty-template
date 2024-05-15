package com.example.nettydemo.statemachine;

import com.example.nettydemo.constants.CommonConst;
import lombok.Getter;
import org.apache.commons.lang3.EnumUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 命令状态机
 */
public class CommandStatemachine {

    private State state;
    private Command command;

    public String execute(String msg) {
        if (isTerminated()) {
            return null;
        }
        try {
            Command msgCmd = Command.getByName(msg);
            if (msgCmd != null) {
                command = msgCmd;
                return Objects.toString(command.intro(this), "") + CommonConst.BR + prompt();
            }
            return Objects.toString(command.exec(this, msg), "") + CommonConst.BR + prompt();
        } catch (Exception e) {
            return "Error: " + e.getMessage() + CommonConst.BR + prompt();
        }
    }

    /**
     * 是否已终止
     */
    public boolean isTerminated() {
        return state == State.TERMINATED;
    }

    public String prompt() {
        return "[" + command + "]> ";
    }

    /**
     * 状态枚举
     */
    public enum State {
        INIT,
        RUNNING,
        TERMINATED,
    }

    /**
     * 命令枚举
     */
    @Getter
    public enum Command {
        HELP("help", "帮助") {
            @Override
            public String intro(CommandStatemachine statemachine) {
                return "输入命令进入相应的模式" + CommonConst.BR + "有效模式命令如下:" + CommonConst.BR
                        + Arrays.stream(Command.class.getEnumConstants())
                        .map(it -> it.getName() + ": " + it.getDesc())
                        .collect(Collectors.joining(CommonConst.BR));
            }

            @Override
            public String exec(CommandStatemachine statemachine, String args) {
                return intro(statemachine);
            }
        },
        INFO("info", "查询") {
            @Override
            public String intro(CommandStatemachine statemachine) {
                return "请输入搜索内容";
            }

            @Override
            public String exec(CommandStatemachine statemachine, String args) {
                return "";
            }
        },
        QUIT("quit", "退出") {
            @Override
            public String intro(CommandStatemachine statemachine) {
                statemachine.state = State.TERMINATED;
                return null;
            }

            @Override
            public String exec(CommandStatemachine statemachine, String args) {
                return null;
            }
        };

        private String name;
        private String desc;

        Command(String name, String desc) {
            this.name = name;
            this.desc = desc;
        }

        public String intro(CommandStatemachine statemachine) {
            return "";
        }

        public abstract String exec(CommandStatemachine statemachine, String args);

        private static Map<String, Command> map = EnumUtils.getEnumMap(Command.class, Command::getName);

        public static Command getByName(String name) {
            return map.get(name);
        }
    }
}
