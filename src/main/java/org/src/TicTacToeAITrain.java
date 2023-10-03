package org.src;


import java.io.File;
import org.deeplearning4j.datasets.iterator.utilty.ListDataSetIterator;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.util.ArrayList;
import java.util.List;

public class TicTacToeAITrain {

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
        int inputSize = 18; // As it was one-hot encoded to 18
        int hiddenSize = 18;
        int outputSize = 9; // Possible moves
        int numEpochs = 1000;
        double learningRate = 0.01;

        MultiLayerConfiguration config = getConfiguration();

        MultiLayerNetwork model = new MultiLayerNetwork(config);
        model.init();

        // Create a DataSetIterator for training
        ListDataSetIterator<DataSet> iterator = new ListDataSetIterator<>(trainingData, trainingData.size());

        // Configure the training process
        model.setListeners(new ScoreIterationListener(10)); // Print scores every 10 iterations

        // Train the model
        for (int epoch = 0; epoch < numEpochs; epoch++) {
            iterator.reset();
            model.fit(iterator);
        }

        // Save the trained model
        String modelSavePath = "TicTacToeModel.zip";
        model.save(new File(modelSavePath));

        // Evaluation on the training data (for demonstration purposes)
        Evaluation evaluation = new Evaluation(outputSize);
        iterator.reset();
        int iteration = 0;
        while (iterator.hasNext()) {
            iteration++;
            System.out.print("Iteration: " + iteration);
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
                .updater(new org.nd4j.linalg.learning.config.Adam(0.01))
                .list()
                .layer(0, new DenseLayer.Builder()
                        .nIn(27)  // Adjusted input size
                        .nOut(54) // For simplicity, I'm doubling this but you can experiment with different sizes
                        .activation(Activation.RELU)
                        .build())
                .layer(1, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .nIn(54)
                        .nOut(9)
                        .activation(Activation.SOFTMAX)
                        .build())
                .build();
    }
}
