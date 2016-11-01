package org.elasticsearch.custom.nativescript.plugin;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.custom.nativescript.scripts.ScoreScript;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.plugins.ScriptPlugin;
import org.elasticsearch.script.NativeScriptFactory;
import org.elasticsearch.script.ScriptContext;
import org.elasticsearch.script.ScriptEngineService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lizhu on 11/1/16.
 *
 * You can also implements AnalysisPlugin, SearchPlugin, etc
 */
public class NativeScriptPlugin extends Plugin implements ScriptPlugin {

    @Override
    public ScriptEngineService getScriptEngineService(Settings settings) {
        // you can add a new script engine, or you can use settings to get defined values
        // String redisHost = settings.get("redis.host", "127.0.0.1");
        return null;
    }

    @Override
    public List<NativeScriptFactory> getNativeScripts() {
        // add all your script instances to the list
        List<NativeScriptFactory> list = new ArrayList<>();
        list.add(new ScoreScript.Factory());
        return list;
    }

    @Override
    public ScriptContext.Plugin getCustomScriptContexts() {
        return null;
    }
}
