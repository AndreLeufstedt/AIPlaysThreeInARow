package org.rewardModel.Training;


import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ModelValidator {

    private static final float TRAIN_RATIO = 0.8f;
    private List<DataSet> validationData;
    private MultiLayerNetwork model;

    public ModelValidator(MultiLayerNetwork model, List<DataSet> allData) {
        this.model = model;
        splitData(allData);
    }

    private void splitData(List<DataSet> allData) {
        int trainSize = (int) (allData.size() * TRAIN_RATIO);
        List<DataSet> trainingData = new ArrayList<>(trainSize);
        validationData = new ArrayList<>(allData.size() - trainSize);

        Random rand = new Random();
        for (DataSet dataSet : allData) {
            if (trainingData.size() < trainSize) {
                trainingData.add(dataSet);
            } else {
                validationData.add(dataSet);
            }
        }
    }

    public Evaluation validate() {
        Evaluation evaluation = new Evaluation(AITrain.outputSize);
        for (DataSet ds : validationData) {
            INDArray output = model.output(ds.getFeatures());
            evaluation.eval(ds.getLabels(), output);
        }
        return evaluation;
    }

    public List<DataSet> getValidationData() {
        return validationData;
    }
}
