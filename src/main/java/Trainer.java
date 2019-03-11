import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.sentdetect.SentenceSample;
import opennlp.tools.sentdetect.SentenceSampleStream;
import opennlp.tools.util.*;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Trainer {

    public static void main(String[] args) throws Exception {
        Trainer trainer = new Trainer();
        trainer.trainSentDectectModel();

    }

    public void trainSentDectectModel() throws IOException {
        // directory to save the model file that is to be generated, create this directory in prior
        File destDir = new File("C:\\Users\\Tank\\IdeaProjects\\html2json\\src\\main\\java\\models");

        // training data
        InputStreamFactory in = new MarkableFileInputStreamFactory(new File("C:\\Users\\Tank\\IdeaProjects\\html2json\\src\\main\\java\\models\\trainingSet.txt"));

        // parameters used by machine learning algorithm, Maxent, to train its weights
        TrainingParameters mlParams = new TrainingParameters();
        mlParams.put(TrainingParameters.ITERATIONS_PARAM, Integer.toString(15));
        mlParams.put(TrainingParameters.CUTOFF_PARAM, Integer.toString(1));

        // train the model
        SentenceModel sentdetectModel = SentenceDetectorME.train(
                "nl",
                new SentenceSampleStream(new PlainTextByLineStream(in, StandardCharsets.UTF_8)),
                true,
                null,
                mlParams);

        // save the model, to a file, "en-sent-custom.bin", in the destDir : "custom_models"
        File outFile = new File(destDir,"nl-sent-custom1.bin");
        FileOutputStream outFileStream = new FileOutputStream(outFile);
        sentdetectModel.serialize(outFileStream);

        // loading the model
        SentenceDetectorME sentDetector = new SentenceDetectorME(sentdetectModel);

        // detecting sentences in the test string
        String testString = ("Sugar is sweet. That doesn't mean its good.");
        System.out.println("\nTest String: "+testString);
        String[] sents = sentDetector.sentDetect(testString);
        System.out.println("---------Sentences Detected by the SentenceDetector ME class using the generated model-------");
        for(int i=0;i<sents.length;i++){
            System.out.println("Sentence "+(i+1)+" : "+sents[i]);
        }
    }

}
