package ie.corballis.fixtures.settings;

public final class SettingsHolder {

    private static ThreadLocal<Settings> settings = new ThreadLocal<>();

    public static void updateSettings(Settings newSettings) {
        settings.set(newSettings);
    }

    public static Settings settings() {
        Settings current = settings.get();
        if (current != null) {
            return current;
        }
        updateSettings(Settings.defaultSettings());
        return settings.get();
    }

}
