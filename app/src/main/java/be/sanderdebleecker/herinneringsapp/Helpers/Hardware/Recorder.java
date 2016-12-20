package be.sanderdebleecker.herinneringsapp.Helpers.Hardware;

import android.media.MediaRecorder;
import java.io.IOException;
import java.util.concurrent.TimeUnit;


public class Recorder {
    private boolean isRecording = false;
    private MediaRecorder recorder;
    private long startTime = 0L;
    private long deltaTime = 0L;

    public void startRecording(String path){
        recorder = new MediaRecorder();
        deltaTime = 0L;
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(path);
        recorder.setOnErrorListener(errorListener);
        recorder.setOnInfoListener(infoListener);
        try {
            recorder.prepare();
            recorder.start();
            startTime = System.currentTimeMillis();
            isRecording = true;
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void stopRecording(){
        if(null != recorder && isRecording){
            recorder.stop();
            isRecording = false;
            recorder.reset();
            recorder.release();
            recorder = null;
            deltaTime = System.currentTimeMillis() - startTime;
            startTime = 0L;
        }
    }
    public long getDuration() {
        return deltaTime;
    }
    public String getDurationString() {
        long millis = getDuration();
        String duration = String.format("%d min %d sec",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        );
        return duration;
    }
    public boolean isRecording() {
        return isRecording;
    }
    public void setRecording(boolean recording) {
        isRecording = recording;
    }

    private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
        @Override
        public void onError(MediaRecorder mr, int what, int extra) {
            System.out.println("Error: " + what + ", " + extra);
        }
    };
    private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
        @Override
        public void onInfo(MediaRecorder mr, int what, int extra) {
            System.out.println("Warning: " + what + ", " + extra);
        }
    };
}
