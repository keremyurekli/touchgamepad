package com.keremyurekli;


import org.lwjgl.openvr.OpenVR;
import org.lwjgl.openvr.VR;
import org.lwjgl.openvr.VRControllerState;
import org.lwjgl.openvr.VRSystem;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.IntBuffer;

import static org.lwjgl.openvr.VR.*;
import static org.lwjgl.openvr.VRSystem.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class Main {

    private static int VRtoken;

    private static VirtualGamepadManager virtualGamepadManager;

    private static int refreshSpeed = 5;

    public static void main(String[] args) {
        System.out.println(ConsoleColors.YELLOW + "Initializing vr environment!" + ConsoleColors.RESET);

        System.err.println("VR_IsRuntimeInstalled()=" + VR_IsRuntimeInstalled());
        System.err.println("VR_RuntimePath() =" + VR_RuntimePath());
        System.err.println("VR_IsHmdPresent()=" + VR_IsHmdPresent());

        try (MemoryStack stack = stackPush()) {
            IntBuffer peError = stack.mallocInt(1);

            VRtoken = VR_InitInternal(peError, VR.EVRApplicationType_VRApplication_Overlay);
            if (peError.get(0) == 0) {

                OpenVR.create(VRtoken);

                System.err.println("Model Number:" + VRSystem_GetStringTrackedDeviceProperty(k_unTrackedDeviceIndex_Hmd, ETrackedDeviceProperty_Prop_ModelNumber_String, peError));
                System.err.println("Serial Number:" + VRSystem_GetStringTrackedDeviceProperty(k_unTrackedDeviceIndex_Hmd, ETrackedDeviceProperty_Prop_SerialNumber_String, peError));

                IntBuffer www = stack.mallocInt(1);
                IntBuffer hhh = stack.mallocInt(1);
                VRSystem_GetRecommendedRenderTargetSize(www, hhh);
                System.err.println("Recommended width :" + www.get(0));
                System.err.println("Recommended height:" + hhh.get(0));

                

                System.out.println("Successfully initialized the vr environment!");


                //VRBlankApp.run();


                virtualGamepadManager = new VirtualGamepadManager();
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    @Override
                    public void run() {
                        virtualGamepadManager.mainProcess.destroy();
                        //VRBlankApp.terminate();
                        VR.VR_ShutdownInternal();
                    }
                });
                if (virtualGamepadManager.createVirtualJoystick()) {
                    virtualGamepadManager.write("TEST");
                }


                VRControllerState controllerState = VRControllerState.calloc(stack);
                while (!Thread.currentThread().isInterrupted()) {
                    for (int i = 0; i < k_unMaxTrackedDeviceCount; i++) {
                        if (VRSystem_GetTrackedDeviceClass(i) == ETrackedDeviceClass_TrackedDeviceClass_Controller) {
                            VRSystem_GetControllerState(i, controllerState);
                            //VRSystem_TriggerHapticPulse(i, 0, (short) 0);//NO HAPTICS
                            if ((controllerState.ulButtonPressed() & (1L << EVRButtonId_k_EButton_SteamVR_Touchpad)) != 0) {
                                //System.out.println("Touchpad, on controller " + i);
                                if (i == 1) {
                                    if(!L3){
                                        virtualGamepadManager.write("BUTTON", "L3", "1");
                                        L3 = !L3;
                                    }
                                } else if (i == 2) {
                                    if(!R3){
                                        virtualGamepadManager.write("BUTTON", "R3", "1");
                                        R3 = !R3;
                                    }
                                }
                            } else {
                                if (i == 1) {
                                    if(L3){
                                        virtualGamepadManager.write("BUTTON", "L3", "0");
                                        L3 = !L3;
                                    }
                                } else if (i == 2) {
                                    if(R3){
                                        virtualGamepadManager.write("BUTTON", "R3", "0");
                                        R3 = !R3;
                                    }
                                }
                            }
                            if ((controllerState.ulButtonPressed() & (1L << EVRButtonId_k_EButton_A)) != 0) {
                                //System.out.println("A, on controller " + i);
                                if (i == 1) {
                                    if (!B) {
                                        virtualGamepadManager.write("BUTTON", "B", "1");
                                        B = !B;
                                    }
                                } else if (i == 2) {
                                    if (!A) {
                                        virtualGamepadManager.write("BUTTON", "A", "1");
                                        A = !A;
                                    }
                                }
                            } else {
                                if (i == 1) {
                                    if (B) {
                                        virtualGamepadManager.write("BUTTON", "B", "0");
                                        B = !B;
                                    }
                                } else if (i == 2) {
                                    if (A) {
                                        virtualGamepadManager.write("BUTTON", "A", "0");
                                        A = !A;
                                    }
                                }
                            }
                            if ((controllerState.ulButtonPressed() & (1L << EVRButtonId_k_EButton_IndexController_B)) != 0) {
                                //System.out.println("B, on controller " + i);
                                if (i == 1) {
                                    if (!Y) {
                                        virtualGamepadManager.write("BUTTON", "Y", "1");
                                        Y = !Y;
                                    }
                                } else if (i == 2) {
                                    if (!X) {
                                        virtualGamepadManager.write("BUTTON", "X", "1");
                                        X = !X;
                                    }
                                }
                            } else {
                                if (i == 1) {
                                    if (Y) {
                                        virtualGamepadManager.write("BUTTON", "Y", "0");
                                        Y = !Y;
                                    }
                                } else if (i == 2) {
                                    if (X) {
                                        virtualGamepadManager.write("BUTTON", "X", "0");
                                        X = !X;
                                    }
                                }
                            }
                            if ((controllerState.ulButtonPressed() & (1L << EVRButtonId_k_EButton_SteamVR_Trigger)) != 0) {
                                //System.out.println("Trigger, on controller " + i);
                                if (i == 1) {
                                    if (!L1) {
                                        virtualGamepadManager.write("BUTTON", "L1", "255");
                                        L1 = !L1;
                                    }
                                } else if (i == 2) {
                                    if (!R1) {
                                        virtualGamepadManager.write("BUTTON", "R1", "255");
                                        R1 = !R1;
                                    }
                                }
                            } else {
                                if (i == 1) {
                                    if (L1) {
                                        virtualGamepadManager.write("BUTTON", "L1", "0");
                                        L1 = !L1;
                                    }
                                } else if (i == 2) {
                                    if (R1) {
                                        virtualGamepadManager.write("BUTTON", "R1", "0");
                                        R1 = !R1;
                                    }
                                }
                            }
                            if ((controllerState.ulButtonPressed() & (1L << EVRButtonId_k_EButton_Grip)) != 0) {
                                //System.out.println("Grip, on controller " + i);
                                if (i == 1) {
                                    if (!L2) {
                                        virtualGamepadManager.write("BUTTON", "L2", "1");
                                        L2 = !L2;
                                    }
                                } else if (i == 2) {
                                    if (!R2) {
                                        virtualGamepadManager.write("BUTTON", "R2", "1");
                                        R2 = !R2;
                                    }
                                }
                            } else {
                                if (i == 1) {
                                    if (L2) {
                                        virtualGamepadManager.write("BUTTON", "L2", "0");
                                        L2 = !L2;
                                    }
                                } else if (i == 2) {
                                    if (R2) {
                                        virtualGamepadManager.write("BUTTON", "R2", "0");
                                        R2 = !R2;
                                    }
                                }
                            }

                            ///RELEASE



                            VRSystem_GetControllerState(ETrackingUniverseOrigin_TrackingUniverseStanding, controllerState, i);
                            float xAxis = controllerState.rAxis(0).x();
                            float yAxis = controllerState.rAxis(0).y();

                            float fixedX = xAxis * Short.MAX_VALUE;
                            float fixedY = yAxis * Short.MAX_VALUE;

                           // if (fixedY != 0 || fixedX != 0) {
                                virtualGamepadManager.write("JOYSTICK", String.valueOf(i), String.valueOf(fixedX), String.valueOf(fixedY));
                            //}
                            //System.out.println("Joystick Axis - Controller " + i + ": X=" + xAxis + ", Y=" + yAxis);
                        }
                    }

                    Thread.sleep(refreshSpeed);
                }
            } else {

                System.out.println("INIT ERROR SYMBOL:" + VR_GetVRInitErrorAsSymbol(peError.get(0)));
                System.out.println("INIT ERROR  DESCR:" + VR_GetVRInitErrorAsEnglishDescription(peError.get(0)));
                System.out.println("Failed to initialize vr environment!");

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    public static boolean R1 = false;
    public static boolean R2 = false;
    public static boolean R3 = false;

    public static boolean L1 = false;
    public static boolean L2 = false;
    public static boolean L3 = false;

    public static boolean A = false;
    public static boolean B = false;
    public static boolean X= false;

    public static boolean Y = false;



}