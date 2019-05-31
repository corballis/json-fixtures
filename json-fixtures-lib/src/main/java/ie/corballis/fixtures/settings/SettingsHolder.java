package ie.corballis.fixtures.settings;

public final class SettingsHolder {

    private static ThreadLocal<Settings> settings = ThreadLocal.withInitial(Settings::defaultSettings);

    public static void updateSettings(Settings newSettings) {
        settings.set(newSettings);
    }

    public static Settings settings() {
        return settings.get();
    }

}
