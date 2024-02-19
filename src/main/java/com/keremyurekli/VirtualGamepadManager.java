package com.keremyurekli;

import java.io.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executors;

import static java.nio.charset.StandardCharsets.UTF_8;

public class VirtualGamepadManager {

    private static final Queue<Queue<String>> writingQueue = new LinkedList<>();
    public InputStream inputStream;
    public OutputStream outputStream;

    public Process mainProcess;

    public VirtualGamepadManager() {

    }

    public boolean createVirtualJoystick() throws IOException {
        ProcessBuilder pb = new ProcessBuilder("gamepad/virtual-gamepad.exe");
        pb.directory(new File("gamepad"));

        Process p = pb.start();
        this.inputStream = p.getInputStream();
        this.outputStream = p.getOutputStream();

        outputHandler(this.outputStream);
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                inputHandler(this.inputStream);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        this.mainProcess = p;

        return true;
    }

    public void inputHandler(InputStream inputStream) throws IOException, InterruptedException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = bufferedReader.readLine();
        while (line != null) {
            final String str = line + "\n";
            System.out.println("[JOYSTICK-IN] " + str);

            line = bufferedReader.readLine();
        }
    }

    public void write(String... s) {
        Queue<String> queue = new LinkedList<>();
        Collections.addAll(queue, s);
        if (!writingQueue.contains(queue)) {
            //System.out.println("ADDING");
            writingQueue.add(queue);
        }
    }

    public void outputHandler(OutputStream outputStream) throws IOException {
        Executors.newSingleThreadExecutor().execute(() -> {
            while (true) {
                //System.out.println(writingQueue.size());
                if (!writingQueue.isEmpty()) {
                    Queue<String> current = writingQueue.poll();
                    if (outputStream != null) {
                        try {
                            if(current != null) {
                                while (!current.isEmpty()){
                                    String s = current.poll()+"\n";
                                    outputStream.write(s.getBytes(UTF_8));
                                    outputStream.flush();
                                }
                            }

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }

}
