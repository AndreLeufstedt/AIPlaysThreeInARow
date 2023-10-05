package org.rewardModel.Training;

import org.deeplearning4j.datasets.iterator.utilty.ListDataSetIterator;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.WorkspaceMode;
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

    private static final int INPUT_SIZE = 432; // Adjusted for 12x12 board
    private static final int HIDDEN_SIZE = 488;
    private static final int NUM_EPOCHS = 5;
    public static final int OUTPUT_SIZE = 144; // Adjusted for the 12x12 board
    private static final double LEARNING_RATE = 0.0007;



    public static void main(String[] args) throws Exception {
        // Fetch Training Data from Data Preparation Class
        List<INDArray> inputs = DataPreparation.getInputs();
        List<INDArray> outputs = DataPreparation.getOutputs();

        // Convert lists of inputs and outputs to DataSet
        List<DataSet> allData = new ArrayList<>();
        for (int i = 0; i < inputs.size(); i++) {
            allData.add(new DataSet(inputs.get(i), outputs.get(i)));
        }



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
        for (int epoch = 0; epoch < NUM_EPOCHS; epoch++) {
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
        Evaluation evaluation = new Evaluation(OUTPUT_SIZE);
        iterator.reset();

        while (iterator.hasNext()) {
            DataSet ds = iterator.next();
            INDArray output = model.output(ds.getFeatures());
            evaluation.eval(ds.getLabels(), output);
        }

        System.out.println("Training Evaluation:\n" + evaluation.stats());
    }


    public static MultiLayerConfiguration getConfiguration() {
        int seed = 123;

        return new NeuralNetConfiguration.Builder()
                .seed(seed)
                .trainingWorkspaceMode(WorkspaceMode.SINGLE)
                .inferenceWorkspaceMode(WorkspaceMode.SINGLE)
                .updater(new Nesterovs(LEARNING_RATE, 0.9))
                .weightInit(WeightInit.XAVIER)
                .l2(1e-4)
                .list()
                .layer(new ConvolutionLayer.Builder(3, 3)
                        .nIn(1)
                        .stride(1, 1)
                        .nOut(64)
                        .activation(Activation.RELU)
                        .build())
                .layer(new ConvolutionLayer.Builder(3, 3)
                        .nOut(64)
                        .activation(Activation.RELU)
                        .build())
                .layer(new DenseLayer.Builder()
                        .nOut(64)
                        .activation(Activation.RELU)
                        .build())
                .layer(new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .nOut(OUTPUT_SIZE)
                        .activation(Activation.SOFTMAX)
                        .build())
                .setInputType(InputType.convolutionalFlat(12, 12, 1))
                .build();
    }

}