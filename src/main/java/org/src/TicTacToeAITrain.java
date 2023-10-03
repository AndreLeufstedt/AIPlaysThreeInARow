package org.src;

import java.io.File;
import org.deeplearning4j.datasets.iterator.utilty.ListDataSetIterator;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.preprocessor.RnnToFeedForwardPreProcessor;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.parallelism.ParallelWrapper;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.util.ArrayList;
import java.util.List;

public class TicTacToeAITrain {

    private static int inputSize;
    private static int hiddenSize;
    private static int outputSize;
    private static double learningRate;

    public static void main(String[] args) throws Exception {
        // Fetch Training Data from Data Preparation Class
        List<INDArray> inputs = TicTacToeDataPreparation.getInputs();
        List<INDArray> outputs = TicTacToeDataPreparation.getOutputs();

        // Convert lists of inputs and outputs to DataSet
        List<DataSet> trainingData = new ArrayList<>();
        for (int i = 0; i < inputs.size(); i++) {
            trainingData.add(new DataSet(inputs.get(i), outputs.get(i)));
        }

        // Neural Network Configuration
        inputSize = 27;
        hiddenSize = 104;
        outputSize = 10;
        int numEpochs = 100;
        learningRate = 0.03;

        MultiLayerConfiguration config = getConfiguration();

        MultiLayerNetwork model = new MultiLayerNetwork(config);
        model.init();

        // Use ParallelWrapper for parallel training
        ParallelWrapper wrapper = new ParallelWrapper.Builder<>(model)
                .prefetchBuffer(24)
                .workers(20)
                .averagingFrequency(3)
                .reportScoreAfterAveraging(true)
                .build();

        // Create a DataSetIterator for training
        ListDataSetIterator<DataSet> iterator = new ListDataSetIterator<>(trainingData, trainingData.size());

        // Configure the training process
        model.setListeners(new ScoreIterationListener(10));

        // Train the model using ParallelWrapper
        for (int epoch = 0; epoch < numEpochs; epoch++) {
            System.out.print("\nEpoch: " + (epoch + 1));
            iterator.reset();
            wrapper.fit(iterator);
        }

        // Save the trained model
        String modelSavePath = "TicTacToeModel.zip";
        model.save(new File(modelSavePath));

        // Evaluation on the training data (for demonstration purposes)
        Evaluation evaluation = new Evaluation(outputSize);
        iterator.reset();

        while (iterator.hasNext()) {
            DataSet ds = iterator.next();
            INDArray output = model.output(ds.getFeatures());
            evaluation.eval(ds.getLabels(), output);
        }

        System.out.println("Training Evaluation:\n" + evaluation.stats());
    }

    public static MultiLayerConfiguration getConfiguration() {
        return new NeuralNetConfiguration.Builder()
                .seed(123)
                .weightInit(WeightInit.XAVIER)
                .updater(new org.nd4j.linalg.learning.config.Adam(learningRate))
                .list()
                .layer(0, new DenseLayer.Builder()
                        .nIn(inputSize)
                        .nOut(hiddenSize)
                        .activation(Activation.RELU)
                        .dropOut(0.5)
                        .build())
                .layer(1, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .nIn(hiddenSize)
                        .nOut(outputSize)
                        .activation(Activation.SOFTMAX)
                        .build())
                .inputPreProcessor(0, new RnnToFeedForwardPreProcessor())
                .build();
    }
}
