package de.extio.lm_launcher;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

record App(Path path, Path interpreter, @Deprecated List<String> arguments, List<AppArgument> appArguments) {
	
	static Map<String, Function<Model, String>> PLACEHOLDERS = Map.of(
			"MODEL", m -> m.path(),
			"CONTEXT_SIZE", m -> String.valueOf(m.contextSize()),
			"THREADS", m -> String.valueOf(m.threads()),
			"GPU_LAYERS", m -> String.valueOf(m.gpuLayers()));
	
	String argumentsToString(boolean showAll) {
		return this.argumentsToStringByArgs(this.appArguments.stream(), showAll);
	}
	
	String argumentsToString(final List<AppArgument> argumentOverrides, boolean showAll) {
		return this.argumentsToStringByArgs(this.appArguments
				.stream()
				.map(arg -> argumentOverrides.stream().filter(a -> a.argument().equals(arg.argument())).findFirst().orElse(arg)), 
				showAll);
	}
	
	private String argumentsToStringByArgs(final Stream<AppArgument> args, boolean showAll) {
		return args
				.filter(arg -> showAll || (arg.optional() ? arg.default_() : true))
				.map(AppArgument::argument)
				.collect(Collectors.joining(" "));
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
}
