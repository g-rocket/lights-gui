package io.yancey.felix.lights_gui;

import java.awt.*;
import java.io.*;
import java.nio.charset.*;

import javax.script.*;

public class NameThatColor {
	ScriptEngine nashorn;
	Bindings ntcjs;
	
	public NameThatColor() {
		ScriptEngineManager factory = new ScriptEngineManager();
        nashorn = factory.getEngineByName("nashorn");
        ntcjs = nashorn.createBindings();
        Reader ntcjsFile = new InputStreamReader(this.getClass().getResourceAsStream("ntc.js"), Charset.forName("UTF-8"));
        try {
			nashorn.eval(ntcjsFile, ntcjs);
		} catch (ScriptException e) {
			throw new RuntimeException(e);
		}
	}
	
	public String colorToName(Color c) {
		String colorString = String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
		String script = String.format("ntc.name(\"%s\")[1]", colorString);
		try {
			return (String) nashorn.eval(script, ntcjs);
		} catch (ScriptException e) {
			throw new RuntimeException(e);
		}
	}
}
