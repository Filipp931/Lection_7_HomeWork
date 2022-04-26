package pluginRootDirectory.RandomPlugin;

import pluginRootDirectory.Plugin;

public class RandomPlugin implements Plugin {
    public RandomPlugin() {
        System.out.println(Math.random()*100);
    }
}
