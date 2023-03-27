package com.epam.ld.module2.testing;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * The type Client.
 */
public class Client {
    private String addresses;
    private String name;
    private Map<String, String> variables;
    private String inputFileName;
    private String OutputFileName;

    public Client() {
    }

    public Client(String addresses, String name, Map<String, String> variables, String inputFileName, String outputFileName) {
        this.addresses = addresses;
        this.name = name;
        this.variables = variables;
        this.inputFileName = inputFileName;
        OutputFileName = outputFileName;
    }

    /**
     * Gets addresses.
     *
     * @return the addresses
     */
    public String getAddresses() {
        return addresses;
    }

    /**
     * Sets addresses.
     *
     * @param addresses the addresses
     */
    public void setAddresses(String addresses) {
        this.addresses = addresses;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, String> variables) {
        this.variables = variables;
    }

    public String getInputFileName() {
        return inputFileName;
    }

    public void setInputFileName(String inputFileName) {
        this.inputFileName = inputFileName;
    }

    public String getOutputFileName() {
        return OutputFileName;
    }

    public void setOutputFileName(String outputFileName) {
        OutputFileName = outputFileName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return Objects.equals(addresses, client.addresses) && Objects.equals(name, client.name) && Objects.equals(variables, client.variables) && Objects.equals(inputFileName, client.inputFileName) && Objects.equals(OutputFileName, client.OutputFileName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(addresses, name, variables, inputFileName, OutputFileName);
    }

    @Override
    public String toString() {
        return "Client{" +
                "addresses='" + addresses + '\'' +
                ", name='" + name + '\'' +
                ", variables=" + variables +
                ", inputFileName='" + inputFileName + '\'' +
                ", OutputFileName='" + OutputFileName + '\'' +
                '}';
    }
}
