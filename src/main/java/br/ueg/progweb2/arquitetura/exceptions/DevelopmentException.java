package br.ueg.progweb2.arquitetura.exceptions;

public class DevelopmentException  extends RuntimeException {
    public DevelopmentException(){ super();}
    public DevelopmentException(String message){
        super(message);
    }
    public DevelopmentException(String message, Throwable e){
        super(message,e);
    }
}
