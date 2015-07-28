package com.github.dbunit.rules.dataset;

import com.github.dbunit.rules.type.SeedStrategy;

/**
 * Created by pestano on 26/07/15.
 */
public class DataSetModel {

    private String name;
    private SeedStrategy seedStrategy = SeedStrategy.CLEAN_INSERT;
    private boolean useSequenceFiltering = true;
    private boolean disableConstraints = false;
    private String[] tableOrdering = {};
    private String[] executeStatementsBefore = {};
    private String[] executeStatementsAfter = {};

    public DataSetModel name(String name) {
        this.name = name;
        return this;
    }

    public DataSetModel seedStrategy(SeedStrategy seedStrategy) {
        this.seedStrategy = seedStrategy;
        return this;
    }

    public DataSetModel useSequenceFiltering(boolean useSequenceFiltering) {
        this.useSequenceFiltering = useSequenceFiltering;
        return this;
    }

    public DataSetModel disableConstraints(boolean disableConstraints) {
        this.disableConstraints = disableConstraints;
        return this;
    }

    public DataSetModel tableOrdering(String[] tableOrdering) {
        this.tableOrdering = tableOrdering;
        return this;
    }

    public DataSetModel executeStatementsBefore(String[] executeStatementsBefore) {
        this.executeStatementsBefore = executeStatementsBefore;
        return this;
    }

    public DataSetModel executeStatementsAfter(String[] executeStatementsAfter) {
        this.executeStatementsAfter = executeStatementsAfter;
        return this;
    }

    public DataSetModel from(DataSet dataSet) {
        return name(dataSet.value()).seedStrategy(dataSet.strategy()).
                useSequenceFiltering(dataSet.useSequenceFiltering()).
                tableOrdering(dataSet.tableOrdering()).
                disableConstraints(dataSet.disableConstraints()).
                executeStatementsBefore(dataSet.executeStatementsBefore()).
                executeStatementsAfter(dataSet.executeStatementsAfter());
    }

    public String getName() {
        return name;
    }

    public SeedStrategy getSeedStrategy() {
        return seedStrategy;
    }

    public boolean isUseSequenceFiltering() {
        return useSequenceFiltering;
    }

    public boolean isDisableConstraints() {
        return disableConstraints;
    }

    public String[] getTableOrdering() {
        return tableOrdering;
    }

    public String[] getExecuteStatementsBefore() {
        return executeStatementsBefore;
    }

    public String[] getExecuteStatementsAfter() {
        return executeStatementsAfter;
    }
}
