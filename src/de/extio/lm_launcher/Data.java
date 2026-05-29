package de.extio.lm_launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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
			if (model.maxContextSize() < model.contextSize() || model.promptTemplate() == null || model.temperature() == null || model.topP() == null || model.topK() == null
					|| model.minP() == null) {
				modelData.models.set(i, new Model(model.path(), model.contextSize(), model.maxContextSize(), model.gpuLayers(), model.threads(), model.temperature(), model.topP(), model.topK(),
						model.minP(), model.promptTemplate(), model.ctime()));
			}
		}
		sortModels();
		
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
		sortModels();
		final ObjectMapper objectMapper = new ObjectMapper();
		try {
			objectMapper.writeValue(new File("models.json"), modelData);
		}
		catch (final IOException e) {
			e.printStackTrace();
		}
	}
	
	static void sortModels() {
		modelData.models().sort(Comparator
				.<Model, String>comparing(m -> Path.of(m.path()).getParent().toString())
				.thenComparing(Model::ctime).reversed()
				.thenComparing(Data::modelName, String.CASE_INSENSITIVE_ORDER));
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
	
	private static String modelName(final Model model) {
		final Path fileName = Path.of(model.path()).getFileName();
		return fileName == null ? model.path() : fileName.toString();
	}
	
	static List<String> scanModels() {
		return scanModelPaths().stream().map(p -> p.toString()).toList();
	}
	
	static List<Path> scanModelPaths() {
		final List<Path> result = new ArrayList<>();
		try {
			try (var paths = Files.walk(Path.of(props.getProperty("models")))) {
				paths
						.filter(Files::isRegularFile)
						.filter(p -> p.getFileName().toString().endsWith(".gguf"))
						.map(Path::toAbsolutePath)
						.map(Path::normalize)
						.forEach(result::add);
			}
		}
		catch (final IOException e) {
			e.printStackTrace();
		}
		// Sort by parent path (alphabetical), then by file ctime within the same directory
		result.sort(Comparator
				.<Path, String>comparing(p -> p.getParent().toString())
				.thenComparing(p -> {
					try {
						return Files.readAttributes(p, BasicFileAttributes.class).creationTime();
					}
					catch (final IOException e) {
						return FileTime.fromMillis(0);
					}
				}).reversed()
				.thenComparing(Path::toString));
		return result;
	}
	
	static Model findModelByPath(final String path) {
		return modelData.models().stream()
				.filter(m -> m.path().equals(path))
				.findFirst()
				.orElse(null);
	}
	
	static Model defaultModel(final String path) {
		long ctime = 0;
		try {
			ctime = Files.readAttributes(Path.of(path), BasicFileAttributes.class).creationTime().toMillis();
		}
		catch (final IOException e) {
			// Use 0 as fallback
		}
		return new Model(path, 16000, 128000, 99, 6, Model.DEFAULT_TEMPERATURE, Model.DEFAULT_TOP_P, Model.DEFAULT_TOP_K, Model.DEFAULT_MIN_P, "", ctime);
	}
	
	static record ModelData(List<Model> models) {
		
	}
	
	static record AppData(List<App> apps) {
		
	}
}
