package de.extio.lm_launcher;

import java.math.BigDecimal;

record Model(String path, int contextSize, int maxContextSize, int gpuLayers, int threads, Double temperature, Double topP,
		Integer topK, Double minP, String promptTemplate, long ctime) {
	
	static final double DEFAULT_TEMPERATURE = 0.8d;
	
	static final double DEFAULT_TOP_P = 0.95d;
	
	static final int DEFAULT_TOP_K = 40;
	
	static final double DEFAULT_MIN_P = 0.05d;
	
	static final double MIN_TEMPERATURE = 0d;
	
	static final double MAX_TEMPERATURE = 2d;
	
	static final double MIN_TOP_P = 0d;
	
	static final double MAX_TOP_P = 1d;
	
	static final int MIN_TOP_K = 0;
	
	static final int MAX_TOP_K = 250;
	
	static final double MIN_MIN_P = 0d;
	
	static final double MAX_MIN_P = 1d;
	
	public Model {
		maxContextSize = Math.max(Math.max(1, maxContextSize), Math.max(1, contextSize));
		contextSize = clampInt(contextSize, 1, maxContextSize);
		gpuLayers = Math.max(0, gpuLayers);
		threads = Math.max(0, threads);
		temperature = sanitizeDouble(temperature, DEFAULT_TEMPERATURE, MIN_TEMPERATURE, MAX_TEMPERATURE);
		topP = sanitizeDouble(topP, DEFAULT_TOP_P, MIN_TOP_P, MAX_TOP_P);
		topK = sanitizeInt(topK, DEFAULT_TOP_K, MIN_TOP_K, MAX_TOP_K);
		minP = sanitizeDouble(minP, DEFAULT_MIN_P, MIN_MIN_P, MAX_MIN_P);
		promptTemplate = promptTemplate == null ? "" : promptTemplate;
	}
	
	double temperatureValue() {
		return this.temperature;
	}
	
	double topPValue() {
		return this.topP;
	}
	
	int topKValue() {
		return this.topK;
	}
	
	double minPValue() {
		return this.minP;
	}
	
	String temperatureDisplay() {
		return formatDecimal(this.temperatureValue());
	}
	
	String topPDisplay() {
		return formatDecimal(this.topPValue());
	}
	
	String topKDisplay() {
		return String.valueOf(this.topKValue());
	}
	
	String minPDisplay() {
		return formatDecimal(this.minPValue());
	}
	
	private static Double sanitizeDouble(final Double value, final double defaultValue, final double minValue, final double maxValue) {
		if (value == null || value.isNaN() || value.isInfinite()) {
			return defaultValue;
		}
		return Math.max(minValue, Math.min(maxValue, value));
	}
	
	private static Integer sanitizeInt(final Integer value, final int defaultValue, final int minValue, final int maxValue) {
		if (value == null) {
			return defaultValue;
		}
		return clampInt(value, minValue, maxValue);
	}
	
	private static int clampInt(final int value, final int minValue, final int maxValue) {
		return Math.max(minValue, Math.min(maxValue, value));
	}
	
	static String formatDecimal(final double value) {
		return BigDecimal.valueOf(value).stripTrailingZeros().toPlainString();
	}
}
