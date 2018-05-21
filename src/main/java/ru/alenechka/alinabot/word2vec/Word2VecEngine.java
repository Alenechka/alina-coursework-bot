package ru.alenechka.alinabot.word2vec;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class Word2VecEngine {
    private static Logger log = LoggerFactory.getLogger(Word2VecEngine.class);

    private static String MODEL_FILE_PATH = "/app/src/main/resources/word2vec/model.txt";

    public enum ModelMode {
        LOAD, // Load trained model from file
        INIT // Train new model with training data
    }

    private Word2Vec model;

    /**
     * Common constructor for {@link Word2VecEngine}.
     *
     * @param filePath - absolute path to data set file or file with saved model.
     * @param mode - loading mode.
     */
    public Word2VecEngine(String filePath, ModelMode mode) throws IOException {
        if (mode.equals(ModelMode.INIT)) {
            log.info("'INIT' mode is selected." +
                    "Load and vectorize sentences from file: " + filePath);

            // Strip white space before and after for each line
            SentenceIterator iter = new BasicLineIterator(filePath);

            // Split on white spaces in the line to get words
            TokenizerFactory t = new DefaultTokenizerFactory();

            // Apply the following regex to each token: [\d\.:,"'\(\)\[\]|/?!;]+ .
            // All numbers, punctuation symbols and some special symbols are stripped off.
            t.setTokenPreProcessor(new CommonPreprocessor());

            log.info("Building model ...");

            model = new Word2Vec.Builder()
                    .minWordFrequency(5)
                    .iterations(1)
                    .layerSize(100)
                    .seed(42)
                    .windowSize(5)
                    .iterate(iter)
                    .tokenizerFactory(t)
                    .build();

            log.info("Fitting Word2Vec model ...");
            model.fit();

            log.info("Save model to file " +"...");
            WordVectorSerializer.writeWord2VecModel(model, MODEL_FILE_PATH);
        } else {
            log.info("'LOAD' mode is selected." +
                    "Load model from file: " + filePath);
            model = WordVectorSerializer.readWord2VecModel(filePath);
        }
    }

    public Collection<String> getNearestNWords(String keyWord, int n) {
        log.info("Finding '" + n + "' closest words for key '" + keyWord + "' ...");

        return model.wordsNearest(keyWord, n);
    }

    public String getNearestWordFromText(String message) {
        log.info("Finding the closest words for message '" + message + "' ...");

        String strippedMessage = new CommonPreprocessor().preProcess(message).replace("\n", " ");
        ArrayList<String> wordsFromMessage = new ArrayList<>();

        wordsFromMessage.addAll(Arrays.asList(strippedMessage.split(" ")));

        LinkedList<String> nearestWords =
                (LinkedList<String>) model.wordsNearest(wordsFromMessage, Collections.emptyList(), 1);

        if (!nearestWords.isEmpty()) {
            return nearestWords.get(0);
        }

        return "";
    }
}
