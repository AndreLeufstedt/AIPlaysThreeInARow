package org.rewardModel.Training;

import org.deeplearning4j.datasets.iterator.utilty.ListDataSetIterator;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.parallelism.ParallelWrapper;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AITrain {

    private static int inputSize;
    private static int hiddenSize;
    public static int outputSize;
    private static double learningRate;

    public static void main(String[] args) throws Exception {
        // Fetch Training Data from Data Preparation Class
        List<INDArray> inputs = DataPreparation.getInputs();
        List<INDArray> outputs = DataPreparation.getOutputs();

        // Convert lists of inputs and outputs to DataSet
        List<DataSet> allData = new ArrayList<>();
        for (int i = 0; i < inputs.size(); i++) {
            allData.add(new DataSet(inputs.get(i), outputs.get(i)));
        }

        // Neural Network Configuration
        inputSize = 432; // Adjusted for 12x12 board
        hiddenSize = 488;
        outputSize = 144; // Adjusted for the 12x12 board
        int numEpochs = 5;


        MultiLayerConfiguration config = getConfiguration();

        MultiLayerNetwork model = new MultiLayerNetwork(config);
        model.init();

        // Create ModelValidator instance
        ModelValidator validator = new ModelValidator(model, allData);
        /*
        // Use ParallelWrapper for parallel training
        ParallelWrapper wrapper = new ParallelWrapper.Builder<>(model)
                .prefetchBuffer(24)
                .workers(20)
                .averagingFrequency(3)
                .reportScoreAfterAveraging(true)
                .build();
        */
        // Create a DataSetIterator for training
        ListDataSetIterator<DataSet> iterator = new ListDataSetIterator<>(validator.getValidationData(), validator.getValidationData().size());

        // Configure the training process
        model.setListeners(new ScoreIterationListener(10));

        // Train the model using ParallelWrapper
        for (int epoch = 0; epoch < numEpochs; epoch++) {
            System.out.print("\nEpoch: " + (epoch + 1));
            iterator.reset();
            model.fit(iterator);

            // Validate after each epoch
            Evaluation valEvaluation = validator.validate();
            System.out.println("Validation stats after epoch " + (epoch + 1) + ":\n" + valEvaluation.stats());
        }

        // Save the trained model
        String modelSavePath = "TicTacToeModelReward.zip";
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


    /*public static MultiLayerConfiguration getConfiguration() {
        int seed = 123;
        double learningRate = 0.0005;

        return new NeuralNetConfiguration.Builder()
                .seed(seed)
                .updater(new Nesterovs(learningRate, 0.9)) // Nesterovs momentum
                .weightInit(WeightInit.XAVIER)
                .l2(1e-4)
                .list()
                .layer(new ConvolutionLayer.Builder(3, 3)
                        .nIn(1) // 1 input channel
                        .stride(1, 1)
                        .nOut(128)
                        .activation(Activation.RELU)
                        .build())
                .layer(new ConvolutionLayer.Builder(3, 3)
                        .nOut(256)
                        .activation(Activation.RELU)
                        .build())
                .layer(new ConvolutionLayer.Builder(3, 3)
                        .nOut(128)
                        .activation(Activation.RELU)
                        .build())
                .layer(new DenseLayer.Builder()
                        .nOut(512)
                        .activation(Activation.RELU)
                        .build())
                .layer(new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .nOut(144) // Adjusted for 12x12 board
                        .activation(Activation.SOFTMAX)
                        .build())
                .setInputType(InputType.convolutional(12, 12, 1))
                .build();
    }*/

    public static MultiLayerConfiguration getConfiguration() {
        int seed = 123;
        double learningRate = 0.0007;

        return new NeuralNetConfiguration.Builder()
                .seed(seed)
                .updater(new Nesterovs(learningRate, 0.9)) // Nesterovs momentum
                .weightInit(WeightInit.XAVIER)
                .l2(1e-4)
                .list()
                .layer(new ConvolutionLayer.Builder(3, 3)
                        .nIn(1) // 1 input channel
                        .stride(1, 1)
                        .nOut(64) // Reduced from 128
                        .activation(Activation.RELU)
                        .build())
                .layer(new ConvolutionLayer.Builder(3, 3)
                        .nOut(128) // Kept as 128 but you can reduce further if needed
                        .activation(Activation.RELU)
                        .build())
                .layer(new DenseLayer.Builder()
                        .nOut(256) // Reduced from 512
                        .activation(Activation.RELU)
                        .build())
                .layer(new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .nOut(144) // Adjusted for 12x12 board
                        .activation(Activation.SOFTMAX)
                        .build())
                .setInputType(InputType.convolutionalFlat(12, 12, 1))
                .build();
    }

}