package ttsWorker.exception;

public class TextToSpeechAlreadyProcessedException extends Exception {
    public TextToSpeechAlreadyProcessedException(String msg) {
        super(msg);
    }
}
