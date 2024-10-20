package de.extio.lm_launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Data {
	
	static final Properties props;
	
	static ModelData modelData;
	
	static AppData appData;
	
	static {
		props = new Properties();
		try {
			props.load(new FileInputStream("lm-launcher.properties"));
		}
		catch (final IOException e) {
			e.printStackTrace();
		}
		
		final ObjectMapper objectMapper = new ObjectMapper();
		
		try {
			modelData = objectMapper.readValue(new File("models.json"), ModelData.class);
		}
		catch (final IOException e) {
			e.printStackTrace();
		}
		if (modelData == null || modelData.models == null) {
			modelData = new ModelData(new ArrayList<>());
		}
		// Migration
		for (int i = 0; i < modelData.models().size(); i++) {
			final Model model = modelData.models.get(i);
			if (model.maxContextSize() < model.contextSize() || model.promptTemplate() == null) {
				modelData.models.set(i, new Model(model.path(), model.contextSize(), model.contextSize(), model.gpuLayers(), model.threads(), Objects.requireNonNullElse(model.promptTemplate(), "")));
			}
		}
		
		try {
			appData = objectMapper.readValue(new File("apps.json"), AppData.class);
		}
		catch (final IOException e) {
			e.printStackTrace();
		}
		if (appData == null || appData.apps == null) {
			appData = new AppData(new ArrayList<>());
		}
		// Migration
		for (int i = 0; i < appData.apps.size(); i++) {
			final App app = appData.apps.get(i);
			if (app.appArguments() == null) {
				final List<AppArgument> arguments = new ArrayList<>(app.arguments().size());
				app.arguments().stream().map(argument -> new AppArgument(argument, false, true)).forEach(arguments::add);
				appData.apps.set(i, new App(app.path(), app.interpreter(), new ArrayList<>(), arguments));
			}
		}
	}
	
	static void saveModels() {
		final ObjectMapper objectMapper = new ObjectMapper();
		try {
			objectMapper.writeValue(new File("models.json"), modelData);
		}
		catch (final IOException e) {
			e.printStackTrace();
		}
	}
	
	static void saveApps() {
		final ObjectMapper objectMapper = new ObjectMapper();
		try {
			objectMapper.writeValue(new File("apps.json"), appData);
		}
		catch (final IOException e) {
			e.printStackTrace();
		}
	}
	
	static List<String> scanModels() {
		final List<String> result = new ArrayList<>();
		try {
			Files
					.list(Path.of(props.getProperty("models")))
					.filter(p -> p.getFileName().toString().endsWith(".gguf"))
					.map(Path::toAbsolutePath)
					.map(Path::toString)
					.sorted()
					.forEach(result::add);
		}
		catch (final IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	static record ModelData(List<Model> models) {
		
	}
	
	static record AppData(List<App> apps) {
		
	}
}
