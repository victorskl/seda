package seda.consoleapp;

public class Setting {
    private static final Setting ourInstance = new Setting();

    public static Setting getInstance() {
        return ourInstance;
    }

    private Setting() {
    }


}
