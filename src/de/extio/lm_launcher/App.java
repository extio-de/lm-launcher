package de.extio.lm_launcher;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

record App(Path path, Path interpreter, @Deprecated List<String> arguments, List<AppArgument> appArguments, List<AppModelSettings> modelSettings) {
	
	public App {
		arguments = arguments == null ? new ArrayList<>() : arguments;
		appArguments = appArguments == null ? new ArrayList<>() : appArguments;
		modelSettings = modelSettings == null ? new ArrayList<>() : modelSettings;
	}
	
	static Map<String, Function<Model, String>> PLACEHOLDERS = Map.of(
			"MODEL", m -> m.path(),
			"CONTEXT_SIZE", m -> String.valueOf(m.contextSize()),
			"THREADS", m -> String.valueOf(m.threads()),
			"GPU_LAYERS", m -> String.valueOf(m.gpuLayers()),
			"TEMP", Model::temperatureDisplay,
			"TOP_P", Model::topPDisplay,
			"TOP_K", Model::topKDisplay,
			"MIN_P", Model::minPDisplay);
	
	String argumentsToString(boolean showAll) {
		return this.argumentsToStringByArgs(this.appArguments.stream(), showAll);
	}
	
	String argumentsToString(final List<AppArgument> argumentOverrides, boolean showAll) {
		return this.argumentsToStringByArgs(this.appArguments
				.stream()
				.map(arg -> argumentOverrides.stream().filter(a -> a.argument().equals(arg.argument())).findFirst().orElse(arg)), 
				showAll);
	}
	
	List<AppArgument> argumentsForModel(final String modelPath) {
		if (modelPath == null || modelPath.isBlank()) {
			return copyArguments(this.appArguments);
		}
		final AppModelSettings savedSettings = this.findModelSettings(modelPath);
		if (savedSettings == null) {
			return copyArguments(this.appArguments);
		}
		return this.mergeArguments(savedSettings.appArguments());
	}
	
	void saveArgumentsForModel(final String modelPath, final List<AppArgument> selectedArguments) {
		if (modelPath == null || modelPath.isBlank()) {
			return;
		}
		final AppModelSettings updatedSettings = new AppModelSettings(modelPath, copyArguments(selectedArguments));
		for (int i = 0; i < this.modelSettings.size(); i++) {
			if (this.modelSettings.get(i).modelPath().equals(modelPath)) {
				this.modelSettings.set(i, updatedSettings);
				return;
			}
		}
		this.modelSettings.add(updatedSettings);
	}
	
	private String argumentsToStringByArgs(final Stream<AppArgument> args, boolean showAll) {
		return args
				.filter(arg -> showAll || (arg.optional() ? arg.default_() : true))
				.map(AppArgument::argument)
				.collect(Collectors.joining(" "));
	}
	
	private List<AppArgument> mergeArguments(final List<AppArgument> savedArguments) {
		if (savedArguments == null || savedArguments.isEmpty()) {
			return copyArguments(this.appArguments);
		}
		final Map<String, Integer> occurrences = new HashMap<>();
		final List<AppArgument> mergedArguments = new ArrayList<>(this.appArguments.size());
		for (final AppArgument baseArgument : this.appArguments) {
			final int occurrenceIndex = occurrences.getOrDefault(baseArgument.argument(), 0);
			occurrences.put(baseArgument.argument(), occurrenceIndex + 1);
			final AppArgument savedArgument = nthArgument(savedArguments, baseArgument.argument(), occurrenceIndex);
			mergedArguments.add(savedArgument == null
					? new AppArgument(baseArgument.argument(), baseArgument.optional(), baseArgument.default_())
					: new AppArgument(baseArgument.argument(), baseArgument.optional(), baseArgument.optional() ? savedArgument.default_() : baseArgument.default_()));
		}
		return mergedArguments;
	}
	
	private AppModelSettings findModelSettings(final String modelPath) {
		return this.modelSettings.stream()
				.filter(settings -> settings.modelPath().equals(modelPath))
				.findFirst()
				.orElse(null);
	}
	
	private static AppArgument nthArgument(final List<AppArgument> arguments, final String argument, final int occurrenceIndex) {
		int matchIndex = 0;
		for (final AppArgument candidate : arguments) {
			if (!candidate.argument().equals(argument)) {
				continue;
			}
			if (matchIndex == occurrenceIndex) {
				return candidate;
			}
			matchIndex++;
		}
		return null;
	}
	
	private static List<AppArgument> copyArguments(final List<AppArgument> arguments) {
		return arguments.stream()
				.map(argument -> new AppArgument(argument.argument(), argument.optional(), argument.default_()))
				.collect(Collectors.toCollection(ArrayList::new));
	}
	
	void run(final Model model, final List<AppArgument> argumentOverrides) throws IOException, IllegalArgumentException {
		String interpreterStr = "";
		if (this.interpreter() != null) {
			interpreterStr = this.interpreter().toString().concat(" ");
		}
		
		String consoleCmdStr = Data.props.getProperty("console")
				.replace("CMD", interpreterStr + this.path().toString() + " " + this.argumentsToString(argumentOverrides, false));
		
		if (PLACEHOLDERS.keySet().stream().anyMatch(consoleCmdStr::contains)) {
			if (model == null) {
				throw new IllegalArgumentException("Select a model first");
			}
			for (final Entry<String, Function<Model, String>> placeholder : PLACEHOLDERS.entrySet()) {
				consoleCmdStr = consoleCmdStr.replace(placeholder.getKey(), placeholder.getValue().apply(model));
			}
		}
		
		Runtime.getRuntime().exec(
				consoleCmdStr.split("\\s+"),
				null,
				this.path().toAbsolutePath().getParent().toFile());
	}
	
	static record AppModelSettings(String modelPath, List<AppArgument> appArguments) {
		
		public AppModelSettings {
			appArguments = appArguments == null ? new ArrayList<>() : appArguments;
		}
	}
}
