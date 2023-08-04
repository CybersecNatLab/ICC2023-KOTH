package it.cybersecnatlab.koth23.vm;

public interface VMInstruction {
    void execute() throws VMException;
    int getGasCost() throws VMException;
}
