package vinaygaykar.benchmark;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;


public class Setup {

	private static final String DATASET_URL_WORDLE =
			"https://raw.githubusercontent.com/swkasica/wordle-words/master/wordle.csv";

	private static final String DATASET_NAME_WORDLE = "wordle";

	public static final String FILENAME_WORDLE = "data/" + DATASET_NAME_WORDLE + "/" + DATASET_NAME_WORDLE + ".csv";


	private static final String DATASOURCE_URL_SCREEN_SUMMARIES =
			"https://raw.githubusercontent.com/google-research-datasets/screen2words/main/screen_summaries.csv";

	private static final String DATASET_NAME_SCREEN_SUMMARIES = "screen_summaries";

	public static final String FILENAME_SCREEN_SUMMARIES =
			"data/" + DATASET_NAME_SCREEN_SUMMARIES + "/" + DATASET_NAME_SCREEN_SUMMARIES + ".csv";


	private static final String DATASOURCE_URL_DWYL_EN_WORDS =
			"https://raw.githubusercontent.com/dwyl/english-words/master/words.txt";

	private static final String DATASET_NAME_DWYL_EN_WORDS = "en-words";

	public static final String FILENAME_DWYL_EN_WORDS =
			"data/dwyl/" + DATASET_NAME_DWYL_EN_WORDS + ".txt";


	public static void main(final String[] args) throws IOException {
		downloadDataset(DATASET_URL_WORDLE, FILENAME_WORDLE, DATASET_NAME_WORDLE);
		downloadDataset(DATASOURCE_URL_SCREEN_SUMMARIES, FILENAME_SCREEN_SUMMARIES, DATASET_NAME_SCREEN_SUMMARIES);
		downloadDataset(DATASOURCE_URL_DWYL_EN_WORDS, FILENAME_DWYL_EN_WORDS, DATASET_NAME_DWYL_EN_WORDS);
	}

	private static void downloadDataset(final String url,
										final String fileName,
										final String datasetName) throws IOException {
		System.out.println("Downloading " + datasetName + " from " + url);

		final File file = new File(fileName);
		file.getParentFile().mkdirs();

		final URL website = new URL(url);
		final ReadableByteChannel rbc = Channels.newChannel(website.openStream());
		final FileOutputStream fos = new FileOutputStream(fileName);

		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		fos.close();
		rbc.close();

		System.out.println("Downloaded " + datasetName + " from " + url + " and saved it as " + fileName);
	}

}
