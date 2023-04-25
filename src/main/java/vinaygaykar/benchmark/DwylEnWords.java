package vinaygaykar.benchmark;


import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import vinaygaykar.Dictionary;
import vinaygaykar.trieforce.compressed.CompressedTrie;
import vinaygaykar.trieforce.simple.SimpleTrie;


public class DwylEnWords {

	// Count of records to load. If dataset has fewer records then all of them will be loaded.
	private static final int DEFAULT_MAX_DATASET_SIZE = Integer.MAX_VALUE;

	private static final String DATASET_FILE = Setup.FILENAME_DWYL_EN_WORDS;


	public static void main(final String[] args) throws IOException, CsvValidationException {
		final Dictionary<Integer> dict = getInstanceOfAlgoToBenchmark(args);
		final int datasetSize = getSampleSizeToBenchmark(args);

		System.out.println("Testing dataset: " + DATASET_FILE
								   + ", with records: " + datasetSize
								   + ", with dictionary type: " + dict.getClass().getSimpleName());

		// read data from CSV file and add to trie
		benchmarkAdd(dict, datasetSize);

		// benchmark getValue method
		benchmarkGet(dict, datasetSize);
	}

	private static Dictionary<Integer> getInstanceOfAlgoToBenchmark(final String[] args) {
		int algo;
		try {
			algo = (args.length > 0) ? Integer.parseInt(args[0]) : 0;
		} catch (final NumberFormatException e) {
			algo = 0;
		}
		algo = (algo >= 0 && algo <= 1) ? algo : 0;

		switch (algo) {
			default:
			case 0:
				return new SimpleTrie<>();
			case 1:
				return new CompressedTrie<>();
		}
	}

	private static int getSampleSizeToBenchmark(final String[] args) {
		try {
			return (args.length) > 1 ? Integer.parseInt(args[1]) : DEFAULT_MAX_DATASET_SIZE;
		} catch (final NumberFormatException e) {
			return DEFAULT_MAX_DATASET_SIZE;
		}
	}


	private static void benchmarkAdd(final Dictionary<Integer> dict, final int datasetSize)
	throws IOException, CsvValidationException {
		final long start = System.nanoTime();

		try (final CSVReader reader = new CSVReader(new FileReader(DATASET_FILE))) {
			String[] line;
			int count = 0;
			while ((line = reader.readNext()) != null && count++ < datasetSize)
				dict.add(line[0], count);
		}

		long end = System.nanoTime();
		long addTime = TimeUnit.NANOSECONDS.toMillis(end - start);
		System.out.println("Time taken to add " + dict.size()
								   + " words: " + addTime
								   + " ms, current heap size: " + Runtime.getRuntime().totalMemory() + " bytes");
	}

	private static void benchmarkGet(final Dictionary<Integer> dict, final int datasetSize)
	throws CsvValidationException, IOException {
		final long start = System.nanoTime();

		try (final CSVReader reader = new CSVReader(new FileReader(DATASET_FILE))) {
			String[] line;
			int count = 0;
			while ((line = reader.readNext()) != null && count++ < datasetSize) {
				final Optional<Integer> val = dict.getValue(line[0]);

				assert val.isPresent();
				// There is no value assigned to a words to assert
			}
		}

		long end = System.nanoTime();
		long addTime = TimeUnit.NANOSECONDS.toMillis(end - start);
		System.out.println("Time taken to get " + dict.size() + " words: " + addTime + " ms");
	}

}
